from __future__ import annotations

import os
from typing import Dict, List

from data_structures import Cola, Pila, RegistroNota
from models import Alumno, Profesor, ProgramaAcademico, crear_programa


class GestorSGA:
    ARCHIVO_ALUMNOS = "alumnos.txt"
    ARCHIVO_PROFESORES = "profesores.txt"
    ARCHIVO_CERTIFICADOS = "certificados_pendientes.txt"

    def __init__(self, directorio_datos: str = ".") -> None:
        self._directorio_datos: str = directorio_datos
        self._alumnos: Dict[str, Alumno] = {}
        self._profesores: Dict[str, Profesor] = {}
        self._pila_notas: Pila[RegistroNota] = Pila()
        self._cargar_datos()

    def _ruta(self, nombre_archivo: str) -> str:
        return os.path.join(self._directorio_datos, nombre_archivo)


    # Registro de personas
    def registrar_alumno(self, cedula: str, nombre: str, correo: str, tipo_programa: str) -> Alumno:
        if not cedula or not nombre:
            raise ValueError("La cédula y el nombre del alumno son obligatorios.")
        if cedula in self._alumnos:
            raise ValueError(f"Ya existe un alumno registrado con la cédula {cedula}.")
        programa: ProgramaAcademico = crear_programa(tipo_programa)
        alumno = Alumno(cedula, nombre, correo, programa)
        self._alumnos[cedula] = alumno
        return alumno

    def registrar_profesor(
        self, cedula: str, nombre: str, correo: str, especialidad: str, materia: str
    ) -> Profesor:
        if not cedula or not nombre:
            raise ValueError("La cédula y el nombre del profesor son obligatorios.")
        if cedula in self._profesores:
            raise ValueError(f"Ya existe un profesor registrado con la cédula {cedula}.")
        profesor = Profesor(cedula, nombre, correo, especialidad, materia)
        self._profesores[cedula] = profesor
        return profesor


    # Notas + Pila LIFO
    def registrar_nota(self, cedula: str, nota: float) -> None:
        alumno = self._alumnos.get(cedula)
        if alumno is None:
            raise ValueError(f"No existe un alumno registrado con la cédula {cedula}.")
        alumno.agregar_nota(nota)
        self._pila_notas.apilar((cedula, nota))

    def deshacer_ultima_nota(self) -> RegistroNota:
        """Desapila el último registro de nota y lo elimina del alumno correspondiente."""
        cedula, nota = self._pila_notas.desapilar()
        alumno = self._alumnos.get(cedula)
        if alumno is None:
            raise ValueError("El alumno asociado a ese registro ya no existe en el sistema.")
        alumno.eliminar_ultima_nota()
        return cedula, nota


    # Cola FIFO (Certificados)
    def generar_cola_certificados(self) -> List[Alumno]:
        cola: Cola[Alumno] = Cola()
        for alumno in self._alumnos.values():
            if alumno.esta_aprobado():
                cola.encolar(alumno)

        procesados: List[Alumno] = []
        while not cola.esta_vacia():
            procesados.append(cola.desencolar())

        self._exportar_certificados(procesados)
        return procesados

    def _exportar_certificados(self, alumnos_aprobados: List[Alumno]) -> None:
        with open(self._ruta(self.ARCHIVO_CERTIFICADOS), "w", encoding="utf-8") as archivo:
            archivo.write("=== REPORTE DE CERTIFICADOS PENDIENTES (procesado en orden FIFO) ===\n\n")
            if not alumnos_aprobados:
                archivo.write("No hay alumnos aprobados pendientes de certificación.\n")
                return
            for posicion, alumno in enumerate(alumnos_aprobados, start=1):
                archivo.write(
                    f"{posicion}. Cédula: {alumno.get_cedula()} | Nombre: {alumno.get_nombre()} "
                    f"| Programa: {alumno.get_programa()} | Promedio: {alumno.get_promedio():.2f}\n"
                )


    # Reportes
    def obtener_reporte_general(self) -> str:
        lineas: List[str] = ["=== ALUMNOS ==="]
        if not self._alumnos:
            lineas.append("No hay alumnos registrados.")
        for alumno in self._alumnos.values():
            lineas.append(alumno.to_string())

        lineas.append("\n=== PROFESORES ===")
        if not self._profesores:
            lineas.append("No hay profesores registrados.")
        for profesor in self._profesores.values():
            lineas.append(profesor.to_string())

        return "\n".join(lineas)

    def listar_alumnos(self) -> List[Alumno]:
        return list(self._alumnos.values())

    def listar_profesores(self) -> List[Profesor]:
        return list(self._profesores.values())


    # Persistencia en archivos .txt
    def guardar_datos(self) -> None:
        self._guardar_alumnos()
        self._guardar_profesores()

    def _guardar_alumnos(self) -> None:
        with open(self._ruta(self.ARCHIVO_ALUMNOS), "w", encoding="utf-8") as archivo:
            for alumno in self._alumnos.values():
                notas = alumno.get_notas()
                notas_completas = notas + [0] * (Alumno.MAX_NOTAS - len(notas))
                notas_str = ",".join(str(nota) for nota in notas_completas[: Alumno.MAX_NOTAS])
                tipo_programa = alumno.get_programa().get_nombre_programa()
                linea = (
                    f"{alumno.get_cedula()},{alumno.get_nombre()},{alumno.get_correo()},"
                    f"{tipo_programa},{notas_str}\n"
                )
                archivo.write(linea)

    def _guardar_profesores(self) -> None:
        with open(self._ruta(self.ARCHIVO_PROFESORES), "w", encoding="utf-8") as archivo:
            for profesor in self._profesores.values():
                linea = (
                    f"{profesor.get_cedula()},{profesor.get_nombre()},{profesor.get_correo()},"
                    f"{profesor.get_especialidad()},{profesor.get_materia()}\n"
                )
                archivo.write(linea)

    def _cargar_datos(self) -> None:
        self._cargar_alumnos()
        self._cargar_profesores()

    def _cargar_alumnos(self) -> None:
        ruta = self._ruta(self.ARCHIVO_ALUMNOS)
        if not os.path.exists(ruta):
            return
        with open(ruta, "r", encoding="utf-8") as archivo:
            for linea in archivo:
                linea = linea.strip()
                if not linea:
                    continue
                try:
                    partes = linea.split(",")
                    cedula, nombre, correo, tipo_programa = partes[0], partes[1], partes[2], partes[3]
                    notas_crudas = partes[4 : 4 + Alumno.MAX_NOTAS]
                    programa = crear_programa(tipo_programa)
                    alumno = Alumno(cedula, nombre, correo, programa)
                    for nota_texto in notas_crudas:
                        valor = float(nota_texto)
                        if valor > 0:
                            alumno.agregar_nota(valor)
                    self._alumnos[cedula] = alumno
                except (ValueError, IndexError):
                    continue

    def _cargar_profesores(self) -> None:
        ruta = self._ruta(self.ARCHIVO_PROFESORES)
        if not os.path.exists(ruta):
            return
        with open(ruta, "r", encoding="utf-8") as archivo:
            for linea in archivo:
                linea = linea.strip()
                if not linea:
                    continue
                try:
                    cedula, nombre, correo, especialidad, materia = linea.split(",")
                    self._profesores[cedula] = Profesor(cedula, nombre, correo, especialidad, materia)
                except (ValueError, IndexError):
                    continue

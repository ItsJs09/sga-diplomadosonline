from __future__ import annotations

from abc import ABC, abstractmethod
from typing import List


class Persona:

    def __init__(self, cedula: str, nombre: str, correo: str) -> None:
        self._cedula: str = cedula
        self._nombre: str = nombre
        self._correo: str = correo

    # ---------- Getters ----------
    def get_cedula(self) -> str:
        return self._cedula

    def get_nombre(self) -> str:
        return self._nombre

    def get_correo(self) -> str:
        return self._correo

    def to_string(self) -> str:
        return f"Cédula: {self._cedula} | Nombre: {self._nombre} | Correo: {self._correo}"

    def __str__(self) -> str:
        return self.to_string()


class ProgramaAcademico(ABC):

    def __init__(self, nombre_programa: str) -> None:
        self._nombre_programa: str = nombre_programa

    def get_nombre_programa(self) -> str:
        return self._nombre_programa

    def calcular_promedio(self, notas: List[float]) -> float:
        notas_validas = [nota for nota in notas if nota > 0] if notas else []
        if not notas_validas:
            return 0.0
        return sum(notas_validas) / len(notas_validas)

    @abstractmethod
    def evaluar_aprobacion(self, notas: List[float]) -> bool:
        raise NotImplementedError

    def __str__(self) -> str:
        return self._nombre_programa


class Curso(ProgramaAcademico):

    NOTA_MINIMA: float = 10.0

    def __init__(self) -> None:
        super().__init__("Curso")

    def evaluar_aprobacion(self, notas: List[float]) -> bool:
        return self.calcular_promedio(notas) >= Curso.NOTA_MINIMA


class Diplomado(ProgramaAcademico):

    NOTA_MINIMA: float = 14.0

    def __init__(self) -> None:
        super().__init__("Diplomado")

    def evaluar_aprobacion(self, notas: List[float]) -> bool:
        return self.calcular_promedio(notas) >= Diplomado.NOTA_MINIMA


class Bootcamp(ProgramaAcademico):

    NOTA_MINIMA: float = 14.0

    def __init__(self) -> None:
        super().__init__("Bootcamp")

    def evaluar_aprobacion(self, notas: List[float]) -> bool:
        notas_validas = [nota for nota in notas if nota > 0]
        if not notas_validas:
            return False
        return all(nota >= Bootcamp.NOTA_MINIMA for nota in notas_validas)


def crear_programa(tipo_programa: str) -> ProgramaAcademico:
    tipo = tipo_programa.strip().lower()
    if tipo == "curso":
        return Curso()
    if tipo == "diplomado":
        return Diplomado()
    if tipo == "bootcamp":
        return Bootcamp()
    raise ValueError(
        f"Tipo de programa académico no reconocido: '{tipo_programa}'. "
        "Use 'Curso', 'Diplomado' o 'Bootcamp'."
    )


class Alumno(Persona):

    MAX_NOTAS: int = 3

    def __init__(self, cedula: str, nombre: str, correo: str, programa: ProgramaAcademico) -> None:
        super().__init__(cedula, nombre, correo)
        self._notas: List[float] = []
        self._programa: ProgramaAcademico = programa

    def get_programa(self) -> ProgramaAcademico:
        return self._programa

    def get_notas(self) -> List[float]:
        return list(self._notas)

    def agregar_nota(self, nota: float) -> None:
        if len(self._notas) >= Alumno.MAX_NOTAS:
            raise ValueError(
                f"El alumno {self._nombre} ya tiene el máximo de {Alumno.MAX_NOTAS} notas registradas."
            )
        if nota < 0 or nota > 20:
            raise ValueError("La nota debe estar en el rango de 0 a 20.")
        self._notas.append(nota)

    def eliminar_ultima_nota(self) -> float:
        if not self._notas:
            raise ValueError(f"El alumno {self._nombre} no tiene notas para eliminar.")
        return self._notas.pop()

    def esta_aprobado(self) -> bool:
        return self._programa.evaluar_aprobacion(self._notas)

    def get_promedio(self) -> float:
        return self._programa.calcular_promedio(self._notas)

    def to_string(self) -> str:
        notas_str = ", ".join(str(nota) for nota in self._notas) if self._notas else "Sin notas"
        estado = "APROBADO" if self.esta_aprobado() else "NO APROBADO"
        return (
            f"{super().to_string()} | Programa: {self._programa} "
            f"| Notas: [{notas_str}] | Promedio: {self.get_promedio():.2f} | Estado: {estado}"
        )


class Profesor(Persona):

    def __init__(self, cedula: str, nombre: str, correo: str, especialidad: str, materia: str) -> None:
        super().__init__(cedula, nombre, correo)
        self._especialidad: str = especialidad
        self._materia: str = materia

    def get_especialidad(self) -> str:
        return self._especialidad

    def get_materia(self) -> str:
        return self._materia

    def to_string(self) -> str:
        return f"{super().to_string()} | Especialidad: {self._especialidad} | Materia: {self._materia}"

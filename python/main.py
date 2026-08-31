from gestor_sga import GestorSGA

MENU = """
========== SGA-DO :: Sistema de Gestión Académica ==========
1. Registrar Alumno
2. Registrar Profesor
3. Registrar Notas a un Alumno
4. Deshacer Último Registro de Nota (Pila LIFO)
5. Generar Cola de Certificados (Cola FIFO + exportar archivo)
6. Mostrar Reporte General
7. Salir (Guarda cambios y cierra limpiamente)
==============================================================
"""


def opcion_registrar_alumno(gestor: GestorSGA) -> None:
    try:
        cedula = input("Cédula del alumno: ").strip()
        nombre = input("Nombre del alumno: ").strip()
        correo = input("Correo del alumno: ").strip()
        print("Tipos de programa disponibles: Curso, Diplomado, Bootcamp")
        tipo_programa = input("Tipo de programa: ").strip()
        gestor.registrar_alumno(cedula, nombre, correo, tipo_programa)
        print(f"Alumno '{nombre}' registrado exitosamente en el programa '{tipo_programa}'.")
    except ValueError as error:
        print(f"Error: {error}")


def opcion_registrar_profesor(gestor: GestorSGA) -> None:
    try:
        cedula = input("Cédula del profesor: ").strip()
        nombre = input("Nombre del profesor: ").strip()
        correo = input("Correo del profesor: ").strip()
        especialidad = input("Especialidad: ").strip()
        materia = input("Materia que dicta: ").strip()
        gestor.registrar_profesor(cedula, nombre, correo, especialidad, materia)
        print(f"Profesor '{nombre}' registrado exitosamente.")
    except ValueError as error:
        print(f"Error: {error}")


def opcion_registrar_nota(gestor: GestorSGA) -> None:
    cedula = input("Cédula del alumno: ").strip()
    nota_texto = input("Nota a registrar (0-20): ").strip()

    try:
        nota = float(nota_texto)
    except ValueError:
        print("Error: Ingrese un valor numérico válido.")
        return

    try:
        gestor.registrar_nota(cedula, nota)
        print(f"Nota {nota} registrada correctamente para el alumno {cedula}.")
    except ValueError as error:
        print(f"Error: {error}")


def opcion_deshacer_nota(gestor: GestorSGA) -> None:
    try:
        cedula, nota = gestor.deshacer_ultima_nota()
        print(f"Se deshizo la última nota registrada: {nota} (alumno {cedula}).")
    except (IndexError, ValueError) as error:
        print(f"Error: {error}")


def opcion_generar_certificados(gestor: GestorSGA) -> None:
    procesados = gestor.generar_cola_certificados()
    print(f"Se procesaron {len(procesados)} alumno(s) aprobado(s) en orden de llegada (FIFO).")
    print(f"Reporte exportado a '{GestorSGA.ARCHIVO_CERTIFICADOS}'.")


def opcion_reporte_general(gestor: GestorSGA) -> None:
    print(gestor.obtener_reporte_general())


def main() -> None:
    gestor = GestorSGA()

    while True:
        print(MENU)
        opcion = input("Seleccione una opción (1-7): ").strip()

        try:
            if opcion == "1":
                opcion_registrar_alumno(gestor)
            elif opcion == "2":
                opcion_registrar_profesor(gestor)
            elif opcion == "3":
                opcion_registrar_nota(gestor)
            elif opcion == "4":
                opcion_deshacer_nota(gestor)
            elif opcion == "5":
                opcion_generar_certificados(gestor)
            elif opcion == "6":
                opcion_reporte_general(gestor)
            elif opcion == "7":
                gestor.guardar_datos()
                print("Datos guardados. ¡Hasta pronto!")
                break
            else:
                print("Error: Opción inválida. Seleccione un número entre 1 y 7.")
        except ValueError:
            print("Error: Ingrese un valor numérico válido.")
        except Exception as error:  
            print(f"Error inesperado: {error}")


if __name__ == "__main__":
    main()

package com.diplomadosonline.sga;

import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;
import com.diplomadosonline.sga.models.Alumno;
import com.diplomadosonline.sga.models.Bootcamp;
import com.diplomadosonline.sga.models.Curso;
import com.diplomadosonline.sga.models.Diplomado;
import com.diplomadosonline.sga.models.Profesor;
import com.diplomadosonline.sga.models.ProgramaAcademico;
import com.diplomadosonline.sga.models.RegistroNota;
import com.diplomadosonline.sga.services.GestorSGA;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final GestorSGA gestor = new GestorSGA();

    public static void main(String[] args) {
        try {
            gestor.cargarDatos();
            System.out.println("Datos cargados correctamente desde archivos .txt (si existían).");
        } catch (IOException e) {
            System.out.println("Aviso: no se pudieron cargar los datos previos (" + e.getMessage() + ").");
        }

        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opción (1-7): ");

            try {
                switch (opcion) {
                    case 1:
                        registrarAlumno();
                        break;
                    case 2:
                        registrarProfesor();
                        break;
                    case 3:
                        registrarNota();
                        break;
                    case 4:
                        deshacerNota();
                        break;
                    case 5:
                        generarCertificados();
                        break;
                    case 6:
                        mostrarReporte();
                        break;
                    case 7:
                        salir();
                        break;
                    default:
                        System.out.println("Opción inválida. Intente nuevamente.");
                }
            } catch (IOException e) {
                System.out.println("Error de Entrada/Salida: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Ocurrió un error inesperado: " + e.getMessage());
            }

        } while (opcion != 7);

        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("========= SGA-DO :: MENÚ PRINCIPAL =========");
        System.out.println("1. Registrar Alumno");
        System.out.println("2. Registrar Profesor");
        System.out.println("3. Registrar Nota a Alumno");
        System.out.println("4. Deshacer Último Registro de Nota (Stack LIFO)");
        System.out.println("5. Generar Cola de Certificados (Queue FIFO y exportación)");
        System.out.println("6. Mostrar Reporte General");
        System.out.println("7. Salir (Guardar y cerrar limpiamente)");
        System.out.println("=============================================");
    }

    // Opciones del menú
    private static void registrarAlumno() {
        System.out.println("--- Registro de Alumno ---");
        String cedula = leerTexto("Cédula: ");
        String nombre = leerTexto("Nombre: ");
        String correo = leerTexto("Correo: ");

        System.out.println("Tipo de programa: 1) Curso  2) Diplomado  3) Bootcamp");
        int tipo = leerEntero("Seleccione el tipo (1-3): ");

        ProgramaAcademico programa;
        switch (tipo) {
            case 1:
                programa = new Curso("Curso");
                break;
            case 2:
                programa = new Diplomado("Diplomado");
                break;
            case 3:
                programa = new Bootcamp("Bootcamp");
                break;
            default:
                System.out.println("Tipo inválido. Se asignará 'Curso' por defecto.");
                programa = new Curso("Curso");
        }

        Alumno alumno = new Alumno(cedula, nombre, correo, programa);
        gestor.registrarAlumno(alumno);
        System.out.println("Alumno registrado correctamente: " + alumno);
    }

    private static void registrarProfesor() {
        System.out.println("--- Registro de Profesor ---");
        String cedula = leerTexto("Cédula: ");
        String nombre = leerTexto("Nombre: ");
        String correo = leerTexto("Correo: ");
        String especialidad = leerTexto("Especialidad: ");
        String materia = leerTexto("Materia que dicta: ");

        Profesor profesor = new Profesor(cedula, nombre, correo, especialidad, materia);
        gestor.registrarProfesor(profesor);
        System.out.println("Profesor registrado correctamente: " + profesor);
    }

    private static void registrarNota() {
        System.out.println("--- Registro de Nota ---");
        String cedula = leerTexto("Cédula del alumno: ");
        double nota = leerDecimal("Nota (0.0 - 20.0): ");

        gestor.registrarNotaAlumno(cedula, nota);
        System.out.println("Nota registrada correctamente y apilada en la Pila LIFO.");
    }

    private static void deshacerNota() {
        System.out.println("--- Deshacer Último Registro de Nota ---");
        Optional<RegistroNota> registro = gestor.deshacerUltimoRegistroNota();
        if (registro.isPresent()) {
            System.out.println("Se deshizo el registro: " + registro.get());
        } else {
            System.out.println("No hay registros de notas en la pila para deshacer.");
        }
    }

    private static void generarCertificados() throws IOException {
        System.out.println("--- Generación de Cola de Certificados ---");
        int cantidad = gestor.generarColaCertificados();
        System.out.println("Proceso finalizado. " + cantidad
                + " certificado(s) exportado(s) a certificados_pendientes.txt");
    }

    private static void mostrarReporte() {
        System.out.println(gestor.generarReporteGeneral());
    }

    private static void salir() throws IOException {
        gestor.guardarDatos();
        System.out.println("Datos guardados correctamente en alumnos.txt y profesores.txt.");
        System.out.println("Cerrando SGA-DO. ¡Hasta pronto!");
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un valor numérico válido");
            }
        }
    }

    private static double leerDecimal(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            try {
                return Double.parseDouble(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un valor numérico válido");
            }
        }
    }
}

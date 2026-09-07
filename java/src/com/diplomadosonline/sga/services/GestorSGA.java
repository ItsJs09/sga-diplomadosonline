package com.diplomadosonline.sga.services;

import com.diplomadosonline.sga.models.Alumno;
import com.diplomadosonline.sga.models.Bootcamp;
import com.diplomadosonline.sga.models.Curso;
import com.diplomadosonline.sga.models.Diplomado;
import com.diplomadosonline.sga.models.Profesor;
import com.diplomadosonline.sga.models.ProgramaAcademico;
import com.diplomadosonline.sga.models.RegistroNota;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;

public class GestorSGA {

    private static final String DIRECTORIO_DATOS = "java";

    private static final String ARCHIVO_ALUMNOS = "alumnos.txt";
    private static final String ARCHIVO_PROFESORES = "profesores.txt";
    private static final String ARCHIVO_CERTIFICADOS = "certificados_pendientes.txt";

    private final List<Alumno> alumnos;
    private final List<Profesor> profesores;

    // Pila (LIFO) 
    private final Stack<RegistroNota> pilaNotas;
    
    public GestorSGA() {
        this.alumnos = new ArrayList<>();
        this.profesores = new ArrayList<>();
        this.pilaNotas = new Stack<>();
    }

    // Registro de personas
    public void registrarAlumno(Alumno alumno) {
        alumnos.add(alumno);
    }

    public void registrarProfesor(Profesor profesor) {
        profesores.add(profesor);
    }

    public Optional<Alumno> buscarAlumnoPorCedula(String cedula) {
        return alumnos.stream()
                .filter(a -> a.getCedula().equalsIgnoreCase(cedula))
                .findFirst();
    }

    public List<Alumno> getAlumnos() {
        return new ArrayList<>(alumnos);
    }

    public List<Profesor> getProfesores() {
        return new ArrayList<>(profesores);
    }

    // Pila (Stack) LIFO — Deshacer nota
    public void registrarNotaAlumno(String cedula, double nota) {
        Alumno alumno = buscarAlumnoPorCedula(cedula)
                .orElseThrow(() -> new IllegalArgumentException("No existe un alumno con cédula: " + cedula));
        alumno.agregarNota(nota);
        pilaNotas.push(new RegistroNota(cedula, nota));
    }

    public Optional<RegistroNota> deshacerUltimoRegistroNota() {
        if (pilaNotas.isEmpty()) {
            return Optional.empty();
        }
        RegistroNota ultimo = pilaNotas.pop();
        buscarAlumnoPorCedula(ultimo.getCedulaAlumno()).ifPresent(Alumno::eliminarUltimaNota);
        return Optional.of(ultimo);
    }

    // Cola (Queue) FIFO — Certificados
    public int generarColaCertificados() throws IOException {
        Queue<Alumno> colaCertificados = new LinkedList<>();

        for (Alumno alumno : alumnos) {
            if (alumno.esAprobado()) {
                colaCertificados.offer(alumno);
            }
        }

        List<String> lineasReporte = new ArrayList<>();
        lineasReporte.add("=== REPORTE DE CERTIFICADOS PENDIENTES (Orden FIFO) ===");
        int contador = 0;

        Alumno actual;
        while ((actual = colaCertificados.poll()) != null) {
            contador++;
            String nombrePrograma = (actual.getPrograma() != null)
                    ? actual.getPrograma().getNombrePrograma()
                    : "N/A";
            lineasReporte.add(String.format("%d) Cedula: %s | Nombre: %s | Programa: %s | Notas: %s",
                    contador, actual.getCedula(), actual.getNombre(), nombrePrograma, actual.getNotas()));
        }

        if (contador == 0) {
            lineasReporte.add("No hay alumnos aprobados pendientes de certificación.");
        }

        escribirLineas(ARCHIVO_CERTIFICADOS, lineasReporte);
        return contador;
    }

    // Reporte general
    public String generarReporteGeneral() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== ALUMNOS ===\n");
        if (alumnos.isEmpty()) {
            sb.append("(sin alumnos registrados)\n");
        } else {
            for (Alumno a : alumnos) {
                String programa = (a.getPrograma() != null)
                        ? capitalizar(a.getPrograma().getTipo())
                        : "N/A";
                String estado = a.esAprobado() ? "APROBADO" : "NO APROBADO";
                sb.append(String.format(
                        "Cédula: %s | Nombre: %s | Correo: %s | Programa: %s | Notas: %s | Promedio: %.2f | Estado: %s%n",
                        a.getCedula(), a.getNombre(), a.getCorreo(), programa,
                        a.getNotas(), a.getPromedio(), estado));
            }
        }

        sb.append('\n');
        sb.append("=== PROFESORES ===\n");
        if (profesores.isEmpty()) {
            sb.append("(sin profesores registrados)\n");
        } else {
            for (Profesor p : profesores) {
                sb.append(String.format(
                        "Cédula: %s | Nombre: %s | Correo: %s | Especialidad: %s | Materia: %s%n",
                        p.getCedula(), p.getNombre(), p.getCorreo(), p.getEspecialidad(), p.getMateria()));
            }
        }

        return sb.toString();
    }

    private static String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }


    // Persistencia
    public void cargarDatos() throws IOException {
        cargarAlumnos();
        cargarProfesores();
    }

    private Path resolverRutaDatos(String nombreArchivo) throws IOException {
        Path carpeta = Paths.get(DIRECTORIO_DATOS);
        if (!Files.exists(carpeta)) {
            Files.createDirectories(carpeta);
        }
        return carpeta.resolve(nombreArchivo);
    }

    private void cargarAlumnos() throws IOException {
        Path ruta = resolverRutaDatos(ARCHIVO_ALUMNOS);
        if (!Files.exists(ruta)) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(ruta)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }
                String[] campos = linea.split(",", -1);
                if (campos.length < 7) {
                    continue; 
                }
                String cedula = campos[0].trim();
                String nombre = campos[1].trim();
                String correo = campos[2].trim();
                String tipoPrograma = campos[3].trim();

                ProgramaAcademico programa = crearProgramaPorTipo(tipoPrograma);

                Alumno alumno = new Alumno(cedula, nombre, correo, programa);
                List<Double> notas = new ArrayList<>();
                try {
                    double n1 = Double.parseDouble(campos[4].trim());
                    double n2 = Double.parseDouble(campos[5].trim());
                    double n3 = Double.parseDouble(campos[6].trim());
                    if (n1 > 0) notas.add(n1);
                    if (n2 > 0) notas.add(n2);
                    if (n3 > 0) notas.add(n3);
                } catch (NumberFormatException e) {
                }
                alumno.setNotas(notas);
                alumnos.add(alumno);
            }
        }
    }

    private void cargarProfesores() throws IOException {
        Path ruta = resolverRutaDatos(ARCHIVO_PROFESORES);
        if (!Files.exists(ruta)) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(ruta)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }
                String[] campos = linea.split(",", -1);
                if (campos.length < 5) {
                    continue;
                }
                Profesor profesor = new Profesor(
                        campos[0].trim(),
                        campos[1].trim(),
                        campos[2].trim(),
                        campos[3].trim(),
                        campos[4].trim());
                profesores.add(profesor);
            }
        }
    }

    private ProgramaAcademico crearProgramaPorTipo(String tipo) {
        switch (tipo.toUpperCase()) {
            case "DIPLOMADO":
                return new Diplomado("Diplomado");
            case "BOOTCAMP":
                return new Bootcamp("Bootcamp");
            case "CURSO":
                return new Curso("Curso");
            default:
                return new Curso("Curso");
        }
    }

    public void guardarDatos() throws IOException {
        guardarAlumnos();
        guardarProfesores();
    }

    private void guardarAlumnos() throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Alumno a : alumnos) {
            lineas.add(a.toCSV());
        }
        escribirLineas(ARCHIVO_ALUMNOS, lineas);
    }

    private void guardarProfesores() throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Profesor p : profesores) {
            lineas.add(p.toCSV());
        }
        escribirLineas(ARCHIVO_PROFESORES, lineas);
    }

    private void escribirLineas(String nombreArchivo, List<String> lineas) throws IOException {
        Path ruta = resolverRutaDatos(nombreArchivo);
        try (BufferedWriter writer = Files.newBufferedWriter(ruta)) {
            for (String linea : lineas) {
                writer.write(linea);
                writer.newLine();
            }
        }
    }
}

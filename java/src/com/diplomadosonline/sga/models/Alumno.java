package com.diplomadosonline.sga.models;

import java.util.ArrayList;
import java.util.List;

public class Alumno extends Persona {

    private List<Double> notas;
    private ProgramaAcademico programa;

    public Alumno(String cedula, String nombre, String correo, ProgramaAcademico programa) {
        super(cedula, nombre, correo);
        this.programa = programa;
        this.notas = new ArrayList<>();
    }

    public List<Double> getNotas() {
        return new ArrayList<>(notas);
    }

    public void setNotas(List<Double> notas) {
        this.notas = (notas == null) ? new ArrayList<>() : new ArrayList<>(notas);
    }

    public ProgramaAcademico getPrograma() {
        return programa;
    }

    public void setPrograma(ProgramaAcademico programa) {
        this.programa = programa;
    }

    public void agregarNota(double nota) {
        if (nota < 0.0 || nota > 20.0) {
            throw new IllegalArgumentException("La nota debe estar entre 0.0 y 20.0");
        }
        notas.add(nota);
    }

    public Double eliminarUltimaNota() {
        if (notas.isEmpty()) {
            return null;
        }
        return notas.remove(notas.size() - 1);
    }

    public boolean esAprobado() {
        if (programa == null) {
            return false;
        }
        return programa.evaluarAprobacion(notas);
    }

    public double getPromedio() {
        if (notas.isEmpty()) {
            return 0.0;
        }
        double suma = 0.0;
        for (double nota : notas) {
            suma += nota;
        }
        return suma / notas.size();
    }

    public String toCSV() {
        String tipoPrograma = (programa != null) ? programa.getTipo() : "SIN_PROGRAMA";
        double n1 = notas.size() > 0 ? notas.get(0) : 0.0;
        double n2 = notas.size() > 1 ? notas.get(1) : 0.0;
        double n3 = notas.size() > 2 ? notas.get(2) : 0.0;
        return String.join(",",
                cedula,
                nombre,
                correo,
                tipoPrograma,
                String.valueOf(n1),
                String.valueOf(n2),
                String.valueOf(n3));
    }

    @Override
    public String toString() {
        String estado = esAprobado() ? "APROBADO" : "NO APROBADO";
        String nombrePrograma = (programa != null) ? programa.getNombrePrograma() : "N/A";
        return String.format("Alumno[cedula=%s, nombre=%s, correo=%s, programa=%s, notas=%s, estado=%s]",
                cedula, nombre, correo, nombrePrograma, notas, estado);
    }
}

package com.diplomadosonline.sga.models;

import java.util.List;

public abstract class ProgramaAcademico {

    protected String nombrePrograma;

    public ProgramaAcademico(String nombrePrograma) {
        this.nombrePrograma = nombrePrograma;
    }

    public String getNombrePrograma() {
        return nombrePrograma;
    }

    public void setNombrePrograma(String nombrePrograma) {
        this.nombrePrograma = nombrePrograma;
    }

    protected double calcularPromedio(List<Double> notas) {
        if (notas == null || notas.isEmpty()) {
            return 0.0;
        }
        double suma = 0.0;
        for (double nota : notas) {
            suma += nota;
        }
        return suma / notas.size();
    }

    public abstract boolean evaluarAprobacion(List<Double> notas);

    public abstract String getTipo();

    @Override
    public String toString() {
        return getTipo() + ": " + nombrePrograma;
    }
}

package com.diplomadosonline.sga.models;

import java.util.List;

public class Curso extends ProgramaAcademico {

    public static final double NOTA_MINIMA = 10.0;

    public Curso(String nombrePrograma) {
        super(nombrePrograma);
    }

    @Override
    public boolean evaluarAprobacion(List<Double> notas) {
        return calcularPromedio(notas) >= NOTA_MINIMA;
    }

    @Override
    public String getTipo() {
        return "CURSO";
    }
}

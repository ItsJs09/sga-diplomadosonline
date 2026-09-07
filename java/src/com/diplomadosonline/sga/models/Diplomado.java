package com.diplomadosonline.sga.models;

import java.util.List;

public class Diplomado extends ProgramaAcademico {

    public static final double NOTA_MINIMA = 14.0;

    public Diplomado(String nombrePrograma) {
        super(nombrePrograma);
    }

    @Override
    public boolean evaluarAprobacion(List<Double> notas) {
        return calcularPromedio(notas) >= NOTA_MINIMA;
    }

    @Override
    public String getTipo() {
        return "DIPLOMADO";
    }
}

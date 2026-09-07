package com.diplomadosonline.sga.models;

import java.util.List;

public class Bootcamp extends ProgramaAcademico {

    public static final double NOTA_MINIMA = 14.0;

    public Bootcamp(String nombrePrograma) {
        super(nombrePrograma);
    }

    @Override
    public boolean evaluarAprobacion(List<Double> notas) {
        if (notas == null || notas.isEmpty()) {
            return false;
        }
        for (double nota : notas) {
            if (nota < NOTA_MINIMA) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getTipo() {
        return "BOOTCAMP";
    }
}

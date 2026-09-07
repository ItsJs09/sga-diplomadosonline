package com.diplomadosonline.sga.models;

public class RegistroNota {

    private final String cedulaAlumno;
    private final double nota;

    public RegistroNota(String cedulaAlumno, double nota) {
        this.cedulaAlumno = cedulaAlumno;
        this.nota = nota;
    }

    public String getCedulaAlumno() {
        return cedulaAlumno;
    }

    public double getNota() {
        return nota;
    }

    @Override
    public String toString() {
        return String.format("RegistroNota[cedula=%s, nota=%.2f]", cedulaAlumno, nota);
    }
}

package com.diplomadosonline.sga.models;

public class Profesor extends Persona {

    private String especialidad;
    private String materia;

    public Profesor(String cedula, String nombre, String correo, String especialidad, String materia) {
        super(cedula, nombre, correo);
        this.especialidad = especialidad;
        this.materia = materia;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String toCSV() {
        return String.join(",", cedula, nombre, correo, especialidad, materia);
    }

    @Override
    public String toString() {
        return String.format("Profesor[cedula=%s, nombre=%s, correo=%s, especialidad=%s, materia=%s]",
                cedula, nombre, correo, especialidad, materia);
    }
}

package com.proyectos.florm.a_dedo.Models;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String mail;
    private String nombre;
    private String telefono;
    private String foto;

    public User() {
    }

    public User(String mail, String nombre, String telefono, String foto) {
        this.mail = mail;
        this.nombre = nombre;
        this.telefono = telefono;
        this.foto = foto;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}

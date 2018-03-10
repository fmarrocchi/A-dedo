package com.proyectos.florm.a_dedo.Models;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String nombre;
    private String apellido;
    private String direccion;
    private String localidad;
    private String foto;
    private String mail;
    private String tel;
    private List<Viaje> viajes;
    private List<Viaje> viajesSuscriptos;

    public User() {
    }

    public User(String nombre, String apellido, String direccion, String localidad, String foto, String mail, String tel) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.direccion = direccion;
        this.localidad = localidad;
        this.foto = foto;
        this.mail = mail;
        this.tel = tel;
        viajes = new ArrayList<Viaje>();
        viajesSuscriptos = new ArrayList<Viaje>();
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDirección() {
        return direccion;
    }

    public void setDirección(String dirección) {
        this.direccion = dirección;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public List<Viaje> getViajes() {
        return viajes;
    }

    public void setViajes(List<Viaje> viajes) {
        this.viajes = viajes;
    }

    public List<Viaje> getViajesSuscriptos() {
        return viajesSuscriptos;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setViajesSuscriptos(List<Viaje> viajesSuscriptos) {
        this.viajesSuscriptos = viajesSuscriptos;
    }

}

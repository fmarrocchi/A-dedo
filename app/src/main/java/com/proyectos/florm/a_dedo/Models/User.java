package com.proyectos.florm.a_dedo.Models;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String mail;
    private List<Viaje> viajes;
    private List<Viaje> viajesSuscriptos;

    public User() {
    }

    public User(String mail) {
        this.mail = mail;
        viajes = new ArrayList<Viaje>();
        viajesSuscriptos = new ArrayList<Viaje>();
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

   public void setViajesSuscriptos(List<Viaje> viajesSuscriptos) {
        this.viajesSuscriptos = viajesSuscriptos;
    }

}

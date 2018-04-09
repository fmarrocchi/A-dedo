package com.proyectos.florm.a_dedo.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Viaje implements Serializable{
    private String destino;
    private String salida;
    private String hora;
    private String fecha;
    private Integer pasajeros;
    private String informacion;
    private String conductor;
    private String direccion;
    private List<String> suscriptos;

    public Viaje(){ }

    public Viaje(String dir, String cond, String des, String sal, String hs, String f, Integer pas, String info){
        suscriptos = new ArrayList<String>();
        conductor = cond;
        direccion = dir;
        destino = des;
        salida = sal;
        pasajeros = pas;
        hora = hs;
        fecha = f;
        informacion = info;
    }

    public List<String> getSuscriptos() {
        return suscriptos;
    }

    public void setSuscriptos(List<String> suscriptos) {
        this.suscriptos = suscriptos;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDestino() {
        return destino;
    }

    public String getSalida() {
        return salida;
    }

    public String getHora() {
        return hora;
    }

    public String getFecha() {
        return fecha;
    }

    public Integer getPasajeros() {
        return pasajeros;
    }

    public String getInformacion() {
        return informacion;
    }

    public String getConductor() {
        return conductor;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public void setSalida(String salida) {
        this.salida = salida;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setPasajeros(Integer pasajeros) {
        this.pasajeros = pasajeros;
    }

    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }

    public void setConductor(String conductor) {
        this.conductor = conductor;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("destino", destino);
        result.put("salida", salida);
        result.put("hora", hora);
        result.put("fecha", fecha);
        result.put("pasajeros", pasajeros);
        result.put("informacion", informacion);
        result.put("conductor", conductor);
        result.put("direccion", direccion);
        result.put("suscriptos", suscriptos);

        return result;
    }

}

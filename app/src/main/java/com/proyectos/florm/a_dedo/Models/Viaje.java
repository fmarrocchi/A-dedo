package com.proyectos.florm.a_dedo.Models;

import java.util.HashMap;
import java.util.Map;

public class Viaje {
    private String destino;
    private String salida;
    private String equipaje;
    private String hora;
    private String fecha;
    private Integer pasajeros;
    private String estado; //activo- completo - terminado
    private String informacion;
    private String conductor;


    //Constructor por defecto
    public Viaje(){ }

    public Viaje(String cond, String des, String sal, String hs, String f, String eq, Integer pas, String est, String info){
        destino = des;
        salida = sal;
        pasajeros = pas;
        equipaje = eq;
        hora = hs;
        fecha = f;
        estado = est;
        conductor = cond;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("salida", salida);
        result.put("destino", destino);
        result.put("pasajeros", pasajeros);
        result.put("equipaje", equipaje);
        result.put("hora", hora);
        result.put("fecha", fecha);
        result.put("estado", estado);
        result.put("informacion", informacion);
        result.put("conductor", conductor);

        return result;
    }

    public String getDestino() {
        return destino;
    }

    public String getSalida() {
        return salida;
    }

    public String getEquipaje() {
        return equipaje;
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

    public String getEstado() {
        return estado;
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

    public void setEquipaje(String equipaje) {
        this.equipaje = equipaje;
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

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }

    public void setConductor(String conductor) {
        this.conductor = conductor;
    }

}

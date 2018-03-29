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
    private String direccion;
    private String localidad;
    private String tel;

    //Constructor por defecto
    public Viaje(){ }

    public Viaje(String loc, String dir, String telefono, String cond, String des, String sal, String hs, String f, String eq, Integer pas, String est, String info){
        conductor = cond;
        localidad = loc;
        direccion = dir;
        tel = telefono;
        destino = des;
        salida = sal;
        pasajeros = pas;
        equipaje = eq;
        hora = hs;
        fecha = f;
        estado = est;
        informacion = info;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
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

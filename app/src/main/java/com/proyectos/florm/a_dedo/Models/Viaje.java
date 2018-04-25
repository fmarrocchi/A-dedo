package com.proyectos.florm.a_dedo.Models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Viaje implements Serializable{
    private String destino;
    private String origen;
    private String hora;
    private String fecha;
    private Integer lugares;
    private String informacion;
    private String conductor;
    private String direccion;
    private Map<String, Integer> suscriptos; //Mapeo donde la clave es el id del usuario y el valor es la cantidad de reservas para el viaje

    public Viaje(){ }

    public Viaje(String dir, String cond, String des, String or, String hs, String f, Integer lug, String info){
        conductor = cond;
        direccion = dir;
        destino = des;
        origen = or;
        lugares = lug;
        hora = hs;
        fecha = f;
        if(info=="")
            info="No hay información adicional.";
        informacion = info;
    }

    public Map<String, Integer> getSuscriptos() {
        return suscriptos;
    }

    public void setSuscriptos(Map<String, Integer> suscriptos) {
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

    public String getOrigen() {
        return origen;
    }

    public String getHora() {
        return hora;
    }

    public String getFecha() {
        return fecha;
    }

    public Integer getLugares() {
        return lugares;
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

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setLugares(Integer lugares) {
        this.lugares = lugares;
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
        result.put("origen", origen);
        result.put("hora", hora);
        result.put("fecha", fecha);
        result.put("lugares", lugares);
        result.put("informacion", informacion);
        result.put("conductor", conductor);
        result.put("direccion", direccion);
        result.put("suscriptos", suscriptos);

        return result;
    }

}

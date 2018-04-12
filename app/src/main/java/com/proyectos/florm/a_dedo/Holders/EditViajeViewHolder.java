package com.proyectos.florm.a_dedo.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.proyectos.florm.a_dedo.R;

public class EditViajeViewHolder extends RecyclerView.ViewHolder{
    private View view;
    private Button botonEditar;

    public void setView(View view) {
        this.view = view;
    }

    public void setBotonEditar(Button botonEditar) {
        this.botonEditar = botonEditar;
    }

    public void setBotonEliminar(Button botonEliminar) {
        this.botonEliminar = botonEliminar;
    }

    public void setBotonGuardar(Button botonGuardar) {
        this.botonGuardar = botonGuardar;
    }

    public void setLblDireccion(EditText lblDireccion) {
        this.lblDireccion = lblDireccion;
    }

    public void setLblHora(EditText lblHora) {
        this.lblHora = lblHora;
    }

    public void setLblFecha(EditText lblFecha) {
        this.lblFecha = lblFecha;
    }

    public void setLblInfo(EditText lblInfo) {
        this.lblInfo = lblInfo;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    private Button botonEliminar;
    private Button botonGuardar;
    private EditText lblDireccion, lblHora, lblFecha, lblInfo;
    private Boolean visible;

    public EditViajeViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        botonEditar = view.findViewById(R.id.btn_editar);
        botonEliminar = view.findViewById(R.id.btn_eliminar);
        botonGuardar = view.findViewById(R.id.btn_guardar);
        lblDireccion = view.findViewById(R.id.lblDireccion);
        lblInfo = view.findViewById(R.id.lblInformacion);
        lblHora = view.findViewById(R.id.lblHora);
        lblFecha = view.findViewById(R.id.lblFecha);
        visible = false;
    }

    public void setDestino(String destino) {
        TextView field = view.findViewById(R.id.lblDestino);
        field.setText(destino);
    }

    public Button getBotonEditar() {
        return botonEditar;
    }

    public Button getBotonEliminar() {
        return botonEliminar;
    }

    public Button getBotonGuardar() {
        return botonGuardar;
    }

    public EditText getLblDireccion() {
        return lblDireccion;
    }

    public EditText getLblHora() {
        return lblHora;
    }

    public EditText getLblFecha() {
        return lblFecha;
    }

    public EditText getLblInfo() {
        return lblInfo;
    }

    public View getView() {
        return view;
    }

    public void setOrigen(String origen) {
        TextView field = view.findViewById(R.id.lblOrigen);
        field.setText(origen);
    }

    public void setFecha(String fecha) {
        lblFecha.setText(fecha);
    }

    public void setHora(String hora) {
        lblHora.setText(hora);
    }

    public void setDireccion(String direccion) {
        lblDireccion.setText(direccion);
    }

    public void setLugares(String lugares) {
        TextView field = (TextView) view.findViewById(R.id.lblLugares);
        field.setText(lugares);
    }

    public void setInformacion(String informacion) {
        lblInfo.setText(informacion);
    }

//    public void masInfo(){
//        if (visible) {
//            info.setVisibility(View.GONE);
//            visible = false;
//        }
//        else{
//            info.setVisibility(View.VISIBLE);
//            visible = true;
//        }
//
//    }
}
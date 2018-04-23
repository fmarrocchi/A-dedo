package com.proyectos.florm.a_dedo.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.proyectos.florm.a_dedo.R;

public class EditViajeViewHolder extends RecyclerView.ViewHolder{
    private View view;
    private ImageButton botonEditar, botonEliminar, botonGuardar;
    private TextView lblDireccion, lblHora, lblFecha;
    private Button botonSuscriptos;
    private EditText lblInfo;
    private Boolean visible;

    public void setView(View view) {
        this.view = view;
    }

    public void setBotonEditar(ImageButton botonEditar) {
        this.botonEditar = botonEditar;
    }

    public void setBotonEliminar(ImageButton botonEliminar) {
        this.botonEliminar = botonEliminar;
    }

    public void setBotonGuardar(ImageButton botonGuardar) {
        this.botonGuardar = botonGuardar;
    }

    public void setLblDireccion(TextView lblDireccion) {
        this.lblDireccion = lblDireccion;
    }

    public void setLblHora(TextView lblHora) {
        this.lblHora = lblHora;
    }

    public void setLblFecha(TextView lblFecha) {
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

    public EditViajeViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        botonEditar = view.findViewById(R.id.btn_editar);
        botonEliminar = view.findViewById(R.id.btn_eliminar);
        botonGuardar = view.findViewById(R.id.btn_guardar);
        botonSuscriptos = view.findViewById(R.id.btn_ver_suscriptos);
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

    public ImageButton getBotonEditar() {
        return botonEditar;
    }

    public ImageButton getBotonEliminar() {
        return botonEliminar;
    }

    public ImageButton getBotonGuardar() {
        return botonGuardar;
    }

    public Button getBotonVerSuscriptos() {
        return botonSuscriptos;
    }
    public TextView getLblDireccion() {
        return lblDireccion;
    }

    public TextView getLblHora() {
        return lblHora;
    }

    public TextView getLblFecha() {
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
}
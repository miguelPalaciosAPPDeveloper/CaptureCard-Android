package com.x1mexico.x1.capturecard.Administradores;

import java.io.Serializable;

/**
 * Created by miguelpalacios on 23/02/16.
 */
public class AdminTarjetas implements Serializable {
    private String ID;
    private String Empresa;
    private String Nombre;
    private String Correo;
    private String Telefono1;
    private String Telefono2;
    private String Archivo;

    public AdminTarjetas(String id,String empresa, String nombre, String correo, String telefono1, String telefono2, String archivo){
        ID = id;
        Empresa = empresa;
        Nombre = nombre;
        Correo = correo;
        Telefono1 = telefono1;
        Telefono2 = telefono2;
        Archivo = archivo;
    }

    public String getID(){return ID;}

    public String getEmpresa(){return Empresa;}

    public String getNombre(){return Nombre;}

    public String getCorreo(){return Correo;}

    public String getTelefono1(){return Telefono1;}

    public String getTelefono2(){return Telefono2;}

    public String getArchivo(){return Archivo;}
}

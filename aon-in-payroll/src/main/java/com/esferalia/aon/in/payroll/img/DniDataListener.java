package com.esferalia.aon.in.payroll.img;

import java.util.Date;

public interface DniDataListener {
	 	void onDniData(String dni);
	    void onApellidosData(String apellido1, String apellido2);
	    void onNombreData(String nombre);
	    void onSexoData(String sexo);
	    void onNacionalidadData(String nacionalidad);
	    void onFechaNacimientoData(Date fechaNacimiento);
	    void onDireccionData(String direccion);
	    void onLocalidadData(String localidad);
	    void onLugarNacimientoData(String lugarNacimiento);
	    void onNombrePadreData(String nombrePadre);
	    void onNombreMadreData(String nombreMadre);
}

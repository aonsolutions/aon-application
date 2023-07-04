package com.esferalia.aon.in.payroll.img;

import java.util.Date;

public class MyDniDataListener implements DniDataListener{

		@Override
	    public void onDniData(String dni) {
	        System.out.println("DNI: " + dni);
	    }

	    @Override
	    public void onApellidosData(String apellido1, String apellido2) {
	        System.out.println("Apellidos: " + apellido1 + " " + apellido2);
	    }

	    @Override
	    public void onNombreData(String nombre) {
	        System.out.println("Nombre: " + nombre);
	    }

	    @Override
	    public void onSexoData(String sexo) {
	        System.out.println("Sexo: " + sexo);
	    }

	    @Override
	    public void onNacionalidadData(String nacionalidad) {
	        System.out.println("Nacionalidad: " + nacionalidad);
	    }

	    @Override
	    public void onFechaNacimientoData(Date fechaNacimiento) {
	    	System.out.println("Fecha nacimiento: " +fechaNacimiento);
	    }
	    
	    @Override
	    public void onDireccionData(String direccion) {
	    	System.out.println("Direccion: " +direccion);
	    }
	    
	    @Override
	    public void onLocalidadData(String localidad) {
	    	System.out.println("Localidad: " +localidad);
	    }
	    
	    @Override
	    public void onLugarNacimientoData(String lugarNacimiento) {
	    	System.out.println("Lugar de nacimiento: " +lugarNacimiento);
	    }
	    
	    @Override
	    public void onNombrePadreData(String nombrePadre) {
	    	System.out.println("Nombre de padre: " +nombrePadre);
	    }
	    
	    @Override
	    public void onNombreMadreData(String nombreMadre) {
	    	System.out.println("Nombre de madre: " +nombreMadre);
	    }
	    
	   
	   
}

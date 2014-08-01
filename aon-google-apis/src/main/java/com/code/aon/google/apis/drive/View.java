package com.code.aon.google.apis.drive;

import com.google.api.services.drive.model.File;

public class View {

	public static void file(File f){
		System.out.println("> "+f.getTitle());
		System.out.println("   --> ID:"+f.getId());
		System.out.println("   --> MIME TYPE:"+f.getMimeType());
		System.out.println("   --> SIZE:"+f.getFileSize());
		System.out.println("   --> LINK:"+f.getAlternateLink());
		System.out.println("");
	}
	
	public static void fileOut(File f){
		System.out.println(f.getId());
	}
	
	public static void domain(String d){
		System.out.println("DOMAIN --> "+d);
		System.out.println();
	}
	
	public static void delete(File f){
		System.out.println("> Archivo eliminado:");
		System.out.println("   --> TITLE:"+f.getTitle());
		System.out.println("   --> ID:"+f.getId());
		System.out.println("");
	}
	
	public static void error1(){
		System.out.println("El tipo de búsqueda introducido no es correcto.");
	}
	
	public static void error2(){
		System.out.println("El tipo de borrado introducido no es correcto.");
	}
	
	public static void error3(){
		System.out.println("ERROR: No se ha especificado el id del archivo a eliminar.");
	}
	
}

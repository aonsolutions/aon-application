package com.code.aon.google.apis.drive;

import com.google.api.services.drive.model.File;

public class View {

	public static void file(File f){
		System.out.println("> "+f.getName());
		System.out.println("   --> ID:"+f.getId());
		System.out.println("   --> MIME TYPE:"+f.getMimeType());
		System.out.println("   --> SIZE:"+f.getSize());
		System.out.println("   --> LINK:"+f.getWebViewLink());
		if(f.getProperties() != null){
			for(String key : f.getProperties().keySet()){
				if(key.equals("fileId")){
					System.out.println("   --> DB-ID:" + f.getProperties().get(key));
				}
				if(key.equals("oldDriveId")){
					System.out.println("   --> OLD DRIVE ID: " + f.getProperties().get(key));
				}
				if(key.equals("aontype"))
					System.out.println("   --> AON TYPE: " + f.getProperties().get(key));
			}
		}
		System.out.println("");
	}
	
	public static void file(File f, String driveId){
		System.out.println("> "+f.getName());
		System.out.println("   --> ID:"+f.getId());
		System.out.println("   --> OLD DRIVE ID: "+ driveId);
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
		System.out.println("   --> TITLE:"+f.getName());
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

    public static void error4() {
        System.out.println("ERROR: No se ha especificado el email.");
    }

    public static void error5(){
    	System.out.println("ERROR:No se dispone del identificador del archivo en Base de Datos");
    }
    
}

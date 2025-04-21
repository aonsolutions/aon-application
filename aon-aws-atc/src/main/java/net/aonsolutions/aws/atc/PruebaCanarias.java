package net.aonsolutions.aws.atc;

import java.util.List;

import org.grecasa.ext.pa.mod420.MIModelo420;

public class PruebaCanarias {

	// FALTA - OBTENER FICHERO PARA PRESENTACION PASANDO EL XML AL MODULO DE IMPRESION DE LA ATC
	public static String obtenerPresentacion(String declaracion) throws Exception {
		if (declaracion==null)
			return null;
		String resultado = null;
//		try {
			System.out.println("PASO 2.1");
 //			Path prueba = Files.createTempDirectory("Temp");
 //			String path = prueba.toString();
			String path = "C:\\TEMP";
			System.out.println("PASO 2.2. path="+path);
			MIModelo420 miModelo420 = new MIModelo420(path);
			System.out.println("PASO 2.3");
			resultado = miModelo420.getFicheroPresentacion(declaracion);
			System.out.println("PASO 2.4. resultado="+resultado);
			if (resultado == null)
				mostrarMensajes(miModelo420.getMensajes());
//		} catch (Exception e) {
//			e.printStackTrace();			
//			throw new AonCoreException(e);
//		}
		return resultado;
	}
	
	private static void mostrarMensajes(List<String> mensajes) {
		if (mensajes != null && mensajes.size() > 0) {
			System.out.println("Nº mensajes error: " + mensajes.size());
			for (String m : mensajes)
				System.out.println("" + m);
		}
	}
	
//	public static void main(String[] args) {
//		
//		String xml = obtenerXML();
//		String resultado = obtenerPresentacion(xml);
//		
//          	if (resultado != null) {
//			System.out.println("Resultado:");
//			System.out.println(resultado);

//			try {
//				File archivo = new File("c:\\tmp\\prueba.atc");
//	            FileWriter escribir = new FileWriter(archivo, true);
//	            escribir.write(resultado);
//	            escribir.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	
}

 

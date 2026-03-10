package net.aonsolutions.aws.atc.lambda;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.grecasa.ext.pa.mod417.MIModelo417;
import org.grecasa.ext.pa.mod420.MIModelo420;
import org.grecasa.ext.pa.mod421.MIModelo421;
import org.grecasa.ext.pa.tributos.MIModeloComun;

public class MIModelo420Handler {
	
	private void mostrarMensajes(List<String> mensajes) {
		if (mensajes == null) {
			System.out.println("mensajes is null");
		} else if (mensajes.isEmpty()) {
			System.out.println("mensajes is empty");
		} else {
			System.err.println("Nº mensajes error/aviso: " + mensajes.size());
			for (String m : mensajes)
				System.err.println("" + m);
		}
	}

	private static String getDownloadDir() {
		return System.getProperty("java.io.tmpdir");
	}
	
	private static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}
	
// MODELOS 417, 420, 421 y 425 ----------------------------------------------------------------------------
	
	public MIModelo420Result obtenerPresentacion(String declaracion) {
		
		MIModelo420Result result = new MIModelo420Result();
		if (isEmpty(declaracion)) {
			System.err.println("ERROR: DECLARACION ES NULO");
			result.getErrores().add("ERROR: DECLARACION ES NULO");
			return result;
		}
		try {
			MIModeloComun miModelo = getMiModelo(declaracion);
		
			if (miModelo == null) {
				System.err.println("ERROR: MODELO O EJERCICIO NO SOPORTADO");
				result.getErrores().add("ERROR: MODELO O EJERCICIO NO SOPORTADO");
				return result;
			}
			
			System.out.println("INFO: Llamada al modulo de impresion de la ATC");
			System.out.println("INFO: " + miModelo.getClass().getName() + ".getFicheroPresentacion()");
			
			String resultado = miModelo.getFicheroPresentacion(declaracion);
			System.out.println("INFO: resultado = " + resultado);
			
			if (resultado != null) {
				result.setResultado(resultado);
				System.out.println("INFO: DECLARACION CORRECTA");
			} else {
				System.err.println("INFO: DECLARACION ERRONEA");
			}
			
			List<String> mensajes = miModelo.getMensajes() ;
			if (miModelo.getMensajes() != null) {
				mostrarMensajes(mensajes);
				result.getErrores().addAll(mensajes);
			}
		} catch (Exception e) {
			System.err.println("EXCEPTION ERROR: " + e.getMessage());
			e.printStackTrace();
			result.getErrores().add("EXCEPTION ERROR: " + e.getMessage());			
		}
		return result;
		
	}

	public MIModelo420Result obtenerBorrador(String declaracion) {
		
		MIModelo420Result result = new MIModelo420Result();
		if (isEmpty(declaracion)) {
			System.err.println("ERROR: DECLARACION ES NULO");
			result.getErrores().add("ERROR: DECLARACION ES NULO");
			return result;
		}
		try {
			MIModeloComun miModelo = getMiModelo(declaracion);
			
			if (miModelo == null) {
				System.err.println("ERROR: MODELO O EJERCICIO NO SOPORTADO");
				result.getErrores().add("ERROR: MODELO O EJERCICIO NO SOPORTADO");
				return result;
			}
			
			System.out.println("INFO: Llamada al modulo de impresion de la ATC");
			System.out.println("INFO: " + miModelo.getClass().getName() + ".getBorrador()");
			
			byte[] resultado = miModelo.getBorrador(declaracion);
			System.out.println("INFO: resultado = " + resultado);
			
			if (resultado != null) {
				result.setResultado(new String(resultado, StandardCharsets.ISO_8859_1));
				System.out.println("INFO: DECLARACION CORRECTA");
			} else {
				System.err.println("INFO: DECLARACION ERRONEA");
			}
			
			List<String> mensajes = miModelo.getMensajes() ;
			if (miModelo.getMensajes() != null) {
				mostrarMensajes(mensajes);
				result.getErrores().addAll(mensajes);				
			}
		} catch (Exception e) {
			System.err.println("EXCEPTION ERROR: " + e.getMessage());
			e.printStackTrace();
			result.getErrores().add("EXCEPTION ERROR: " + e.getMessage());
		}
		return result;
	}
	
	private MIModeloComun getMiModelo(String declaracion) {
		
		if (declaracion.contains("MOD=\"420\"")) {
			return new MIModelo420(getDownloadDir());
		} else if (declaracion.contains("MOD=\"417\"")) {
			return new MIModelo417(getDownloadDir());
		} else if (declaracion.contains("MOD=\"421\"")) {
			return new MIModelo421(getDownloadDir());
		} else 
//		if (declaracion.contains("MOD=\"425\"")) {
//			return new MIModelo425(getDownloadDir());
//		} else 
		{	
			return null;
		}
		
	}
	
// MODELO 415 ----------------------------------------------------------------------------------
	
//	public MIModelo420Result obtenerPresentacion415(String declaracion) {
//		MIModelo420Result result = new MIModelo420Result();
//		if (isEmpty(declaracion)) {
//			System.err.println("ERROR: DECLARACION ES NULO");
//			result.getErrores().add("ERROR: DECLARACION ES NULO");
//		}
//		try {
//			System.out.println("INFO: Llamada al modulo de impresion de la ATC (MIModelo415.getFicheroPresentacion)");
//			MIModelo415 miModelo415 = new MIModelo415(getDownloadDir());
//			String resultado = miModelo415.getFicheroPresentacion(declaracion);
//			System.out.println("INFO: resultado = " + resultado);
//			
//			if (resultado != null) {
//				result.setResultado(resultado);
//			}
//			List<String> mensajes = miModelo415.getMensajes() ;
//			if (miModelo415.getMensajes() != null) {
//				mostrarMensajes(mensajes);
//				result.getErrores().addAll(mensajes);
//			}
//		} catch (Exception e) {
//			System.err.println("EXCEPTION ERROR: " + e.getMessage());
//			e.printStackTrace();
//			result.getErrores().add("EXCEPTION ERROR: " + e.getMessage());			
//		}
//		return result;
//	}
//
//	public MIModelo420Result obtenerBorrador415(String declaracion) {
//		MIModelo420Result result = new MIModelo420Result();
//		if (isEmpty(declaracion)) {
//			System.err.println("ERROR: DECLARACION ES NULO");
//			result.getErrores().add("ERROR: DECLARACION ES NULO");
//		}
//		try {
//			System.out.println("INFO: Llamada al modulo de impresion de la ATC (MIModelo415.getBorrador)");
//			MIModelo415 miModelo415 = new MIModelo415(getDownloadDir());
//			
//			// EN ESTE MODELO EL METODO getBorrador NO SE PUEDE USAR, ASI QUE SE HACE LLAMANDO AL METODO getFicheroPresentacion Y POSTERIORMENTE A getCopiaAutoliquidacion
//			String resultado = miModelo415.getFicheroPresentacion(declaracion);
//			System.out.println("INFO: resultado1 = " + resultado);
//			
//			if (resultado != null) {
//				byte[] resultado2 = miModelo415.getCopiaAutoliquidacion();
//				System.out.println("INFO: resultado2 = " + resultado2);
//				if (resultado2 != null)
//					result.setResultado(new String(resultado2, StandardCharsets.ISO_8859_1));
//			}
//			List<String> mensajes = miModelo415.getMensajes() ;
//			if (miModelo415.getMensajes() != null) {
//				mostrarMensajes(mensajes);
//				result.getErrores().addAll(mensajes);				
//			}
//		} catch (Exception e) {
//			System.err.println("EXCEPTION ERROR: " + e.getMessage());
//			e.printStackTrace();
//			result.getErrores().add("EXCEPTION ERROR: " + e.getMessage());
//		}
//		return result;
//	}

}

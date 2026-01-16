package net.aonsolutions.aws.atc.lambda;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.grecasa.ext.pa.mod415.MIModelo415;
import org.grecasa.ext.pa.mod417.MIModelo417;
import org.grecasa.ext.pa.mod420.MIModelo420;
import org.grecasa.ext.pa.mod425.MIModelo425;

public class MIModelo420Handler {
	
	public MIModelo420Result errorModeloNoSoportado() {
		MIModelo420Result result = new MIModelo420Result();
		System.err.println("ERROR: MODELO NO SOPORTADO");
		result.getErrores().add("ERROR: MODELO NO SOPORTADO");
		return result;
	}
	
	public MIModelo420Result obtenerPresentacion420(String declaracion) {
		MIModelo420Result result = new MIModelo420Result();
		if (isEmpty(declaracion)) {
			System.err.println("ERROR: DECLARACION ES NULO");
			result.getErrores().add("ERROR: DECLARACION ES NULO");
		}
		try {
			System.out.println("INFO: Llamada al modulo de impresion de la ATC (MIModelo420.getFicheroPresentacion)");
			MIModelo420 miModelo420 = new MIModelo420(getDownloadDir());
			String resultado = miModelo420.getFicheroPresentacion(declaracion);
			System.out.println("INFO: resultado = " + resultado);
			
			if (resultado != null) {
				result.setResultado(resultado);
			}
			List<String> mensajes = miModelo420.getMensajes() ;
			if (miModelo420.getMensajes() != null) {
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
	
	public MIModelo420Result obtenerPresentacion417(String declaracion) {
		MIModelo420Result result = new MIModelo420Result();
		if (isEmpty(declaracion)) {
			System.err.println("ERROR: DECLARACION ES NULO");
			result.getErrores().add("ERROR: DECLARACION ES NULO");
		}
		try {
			System.out.println("INFO: Llamada al modulo de impresion de la ATC (MIModelo417.getFicheroPresentacion)");
			MIModelo417 miModelo417 = new MIModelo417(getDownloadDir());
			String resultado = miModelo417.getFicheroPresentacion(declaracion);
			System.out.println("INFO: resultado = " + resultado);
			
			if (resultado != null) {
				result.setResultado(resultado);
			}
			List<String> mensajes = miModelo417.getMensajes() ;
			if (miModelo417.getMensajes() != null) {
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
	
	public MIModelo420Result obtenerPresentacion425(String declaracion) {
		MIModelo420Result result = new MIModelo420Result();
		if (isEmpty(declaracion)) {
			System.err.println("ERROR: DECLARACION ES NULO");
			result.getErrores().add("ERROR: DECLARACION ES NULO");
		}
		try {
			System.out.println("INFO: Llamada al modulo de impresion de la ATC (MIModelo425.getFicheroPresentacion)");
			MIModelo425 miModelo425 = new MIModelo425(getDownloadDir());
			String resultado = miModelo425.getFicheroPresentacion(declaracion);
			System.out.println("INFO: resultado = " + resultado);
			
			if (resultado != null) {
				result.setResultado(resultado);
			}
			List<String> mensajes = miModelo425.getMensajes() ;
			if (miModelo425.getMensajes() != null) {
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
	
	public MIModelo420Result obtenerPresentacion415(String declaracion) {
		MIModelo420Result result = new MIModelo420Result();
		if (isEmpty(declaracion)) {
			System.err.println("ERROR: DECLARACION ES NULO");
			result.getErrores().add("ERROR: DECLARACION ES NULO");
		}
		try {
			System.out.println("INFO: Llamada al modulo de impresion de la ATC (MIModelo415.getFicheroPresentacion)");
			MIModelo415 miModelo415 = new MIModelo415(getDownloadDir());
			String resultado = miModelo415.getFicheroPresentacion(declaracion);
			System.out.println("INFO: resultado = " + resultado);
			
			if (resultado != null) {
				result.setResultado(resultado);
			}
			List<String> mensajes = miModelo415.getMensajes() ;
			if (miModelo415.getMensajes() != null) {
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
	
	public MIModelo420Result obtenerBorrador420(String declaracion) {
		MIModelo420Result result = new MIModelo420Result();
		if (isEmpty(declaracion)) {
			System.err.println("ERROR: DECLARACION ES NULO");
			result.getErrores().add("ERROR: DECLARACION ES NULO");
		}
		try {
			System.out.println("INFO: Llamada al modulo de impresion de la ATC (MIModelo420.getBorrador)");
			MIModelo420 miModelo420 = new MIModelo420(getDownloadDir());
			byte[] resultado = miModelo420.getBorrador(declaracion);
			System.out.println("INFO: resultado = " + resultado);
			
			if (resultado != null) {
				result.setResultado(new String(resultado, StandardCharsets.ISO_8859_1));
			}
			List<String> mensajes = miModelo420.getMensajes() ;
			if (miModelo420.getMensajes() != null) {
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
	
	public MIModelo420Result obtenerBorrador417(String declaracion) {
		MIModelo420Result result = new MIModelo420Result();
		if (isEmpty(declaracion)) {
			System.err.println("ERROR: DECLARACION ES NULO");
			result.getErrores().add("ERROR: DECLARACION ES NULO");
		}
		try {
			System.out.println("INFO: Llamada al modulo de impresion de la ATC (MIModelo417.getBorrador)");
			MIModelo417 miModelo417 = new MIModelo417(getDownloadDir());
			byte[] resultado = miModelo417.getBorrador(declaracion);
			System.out.println("INFO: resultado = " + resultado);
			
			if (resultado != null) {
				result.setResultado(new String(resultado, StandardCharsets.ISO_8859_1));
			}
			List<String> mensajes = miModelo417.getMensajes() ;
			if (miModelo417.getMensajes() != null) {
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
	
	public MIModelo420Result obtenerBorrador425(String declaracion) {
		MIModelo420Result result = new MIModelo420Result();
		if (isEmpty(declaracion)) {
			System.err.println("ERROR: DECLARACION ES NULO");
			result.getErrores().add("ERROR: DECLARACION ES NULO");
		}
		try {
			System.out.println("INFO: Llamada al modulo de impresion de la ATC (MIModelo425.getBorrador)");
			MIModelo425 miModelo425 = new MIModelo425(getDownloadDir());
			byte[] resultado = miModelo425.getBorrador(declaracion);
			System.out.println("INFO: resultado = " + resultado);
			
			if (resultado != null) {
				result.setResultado(new String(resultado, StandardCharsets.ISO_8859_1));
			}
			List<String> mensajes = miModelo425.getMensajes() ;
			if (miModelo425.getMensajes() != null) {
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
	
	public MIModelo420Result obtenerBorrador415(String declaracion) {
		MIModelo420Result result = new MIModelo420Result();
		if (isEmpty(declaracion)) {
			System.err.println("ERROR: DECLARACION ES NULO");
			result.getErrores().add("ERROR: DECLARACION ES NULO");
		}
		try {
			System.out.println("INFO: Llamada al modulo de impresion de la ATC (MIModelo415.getBorrador)");
			MIModelo415 miModelo415 = new MIModelo415(getDownloadDir());
			
			// FALTA - EL METODO getBorrador DEVUELVE SIEMPRE NULL, AUNQUE LE PASES COMO SEGUNDO PARAMETRO UN STRING VALIDO CON LOS DATOS ADICIONALES
			// EN LAS INSTRUCCIONES NO APARECE ESTE METODO ASI QUE SE PUEDE HACER LLAMANDO AL METODO getFicheroPresentacion Y POSTERIORMENTE A getCopiaAutoliquidacion
			// AUNQUE HABRIA QUE INTENTAR PONER EN ESE PDF ALGUNA MARCA DE AGUA O ALGO QUE INDIQUE QUE ES UN BORRADOR
//			byte[] resultado = miModelo415.getBorrador(declaracion, null); 
			String resultado = miModelo415.getFicheroPresentacion(declaracion);
			System.out.println("INFO: resultado1 = " + resultado);
			
			if (resultado != null) {
				byte[] resultado2 = miModelo415.getCopiaAutoliquidacion();
				System.out.println("INFO: resultado2 = " + resultado2);
				if (resultado2 != null)
					result.setResultado(new String(resultado2, StandardCharsets.ISO_8859_1));
			}
			List<String> mensajes = miModelo415.getMensajes() ;
			if (miModelo415.getMensajes() != null) {
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
	
	private void mostrarMensajes(List<String> mensajes) {
		if (mensajes == null) {
			System.out.println("mensajes is null");
		} else if (mensajes.isEmpty()) {
			System.out.println("mensajes is empty");
		} else {
			System.err.println("Nº mensajes error: " + mensajes.size());
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

}

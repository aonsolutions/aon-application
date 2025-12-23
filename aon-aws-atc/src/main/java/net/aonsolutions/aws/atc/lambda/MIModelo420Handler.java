package net.aonsolutions.aws.atc.lambda;

import java.nio.charset.StandardCharsets;
import java.util.List;

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
	
	private void mostrarMensajes(List<String> mensajes) {
		if (mensajes != null && !mensajes.isEmpty()) {
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

package net.aonsolutions.aws.atc.lambda;

import java.util.List;

import org.grecasa.ext.pa.mod417.MIModelo417;
import org.grecasa.ext.pa.mod420.MIModelo420;

public class MIModelo420Handler {
//	MIModelo420 miModelo420;
	
//	public MIModelo420Result obtenerPresentacion(String declaracion) {
//		MIModelo420Result result = new MIModelo420Result();
//		if (isEmpty(declaracion)) {
//			System.err.println("ERROR: DECLARACION ES NULO");
//			result.getErrores().add("ERROR: DECLARACION ES NULO");
//		}
//		try {
//			System.out.println("INFO: Llamada al modulo de impresion de la ATC");
//			String resultado = getMIModelo420().getFicheroPresentacion(declaracion);
//			System.out.println("INFO: resultado = " + resultado);
//			
//			if (resultado != null) {
//				result.setResultado(resultado);
//			}
//			List<String> mensajes = getMIModelo420().getMensajes() ;
//			if ( getMIModelo420().getMensajes() != null  ) {
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
	
	public MIModelo420Result obtenerPresentacion420(String declaracion) {
		MIModelo420Result result = new MIModelo420Result();
		if (isEmpty(declaracion)) {
			System.err.println("ERROR: DECLARACION ES NULO");
			result.getErrores().add("ERROR: DECLARACION ES NULO");
		}
		try {
			System.out.println("INFO: Llamada al modulo de impresion de la ATC (MIModelo420)");
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
			System.out.println("INFO: Llamada al modulo de impresion de la ATC (MIModelo417)");
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
	
	private void mostrarMensajes(List<String> mensajes) {
		if (mensajes != null && mensajes.size() > 0) {
			System.err.println("Nº mensajes error: " + mensajes.size());
			for (String m : mensajes)
				System.err.println("" + m);
		}
	}

//	private MIModelo420 getMIModelo420() {
//		if (this.miModelo420 == null)
//			this.miModelo420 = new MIModelo420(getDownloadDir());
//		return this.miModelo420;
//	}

	private static String getDownloadDir() {
		return System.getProperty("java.io.tmpdir");
	}
	
	private static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

//	public static void main(String[] args) {
//		String fileXML =
//			"<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>" +
//			"<DEC PER=\"1T\" ANY=\"2025\" MOD=\"420\">" +
//			"<IDE>" +
//			"<OTP PAI=\"ES\" CP=\"35002\" CMU=\"35016\" POP=\"35\" NVP=\"AGENCIA TRIBUTARIA CANARIA\" SVP=\"CL\" NRS=\"DECLARACION SIN ACTIVIDAD\" NIF=\"B50111111\"/>" +
//			"</IDE>" +
//			"<RES TIP=\"S\"/>" +
//			"</DEC>";
//		System.out.println(new MIModelo420Handler().obtenerPresentacion(fileXML).getResultado());
//	}


}

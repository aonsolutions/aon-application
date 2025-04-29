package net.aonsolutions.aws.atc.lambda;

import java.util.Base64;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class MIModelo420RequestHandler implements RequestHandler<MIModelo420Request, MIModelo420Result> {
	
	@Override
	public MIModelo420Result handleRequest(MIModelo420Request input, Context context) {
		String declaracion = new String(Base64.getDecoder().decode(input.getDeclaracion()));
		System.out.println(declaracion);
		if (declaracion.contains("MOD=\"417\"")) {
			// Modelo 417
			if (input.isBorrador())
				return new MIModelo420Handler().obtenerBorrador417(declaracion); // Borrador
			else
				return new MIModelo420Handler().obtenerPresentacion417(declaracion); // Fichero para presentación
		}
		else {
			// Modelo 420
			if (input.isBorrador())
				return new MIModelo420Handler().obtenerBorrador420(declaracion); // Borrador
			else
				return new MIModelo420Handler().obtenerPresentacion420(declaracion); // Fichero para presentación
		}
	}
	
//	private static String obtenerXML(String modelo) {
//		String fileXML =
//			"<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>" +
//			"<DEC PER=\"1T\" ANY=\"2025\" MOD=\"" + modelo + "\">" +
//			"<IDE>" +
//			"<OTP PAI=\"ES\" CP=\"35002\" CMU=\"35016\" POP=\"35\" NVP=\"AGENCIA TRIBUTARIA CANARIA\" SVP=\"CL\" NRS=\"DECLARACION SIN ACTIVIDAD\" NIF=\"B50111111\"/>" +
//			"</IDE>" +
//			"<RES TIP=\"S\"/>" +
//			"</DEC>";
//		return fileXML;
//	}
//	
//	public static void main(String[] args) {
//		
//		MIModelo420Request input = new MIModelo420Request();
//		input.setDeclaracion(Base64.getEncoder().encodeToString(obtenerXML("420").getBytes()));
//		
//		MIModelo420RequestHandler mi = new MIModelo420RequestHandler();
//		mi.handleRequest(input, null);
//		
//	}
	
}

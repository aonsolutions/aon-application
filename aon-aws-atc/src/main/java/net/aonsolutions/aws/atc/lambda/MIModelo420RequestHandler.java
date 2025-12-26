package net.aonsolutions.aws.atc.lambda;

import java.util.Base64;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class MIModelo420RequestHandler implements RequestHandler<MIModelo420Request, MIModelo420Result> {
	
	@Override
	public MIModelo420Result handleRequest(MIModelo420Request input, Context context) {
		String declaracion = new String(Base64.getDecoder().decode(input.getDeclaracion()));
		System.out.println(declaracion);
		if (declaracion.contains("MOD=\"425\"")) {
			// Modelo 425
			if (input.isBorrador())
				return new MIModelo420Handler().obtenerBorrador425(declaracion); // Borrador
			else
				return new MIModelo420Handler().obtenerPresentacion425(declaracion); // Fichero para presentación
		} else if (declaracion.contains("MOD=\"417\"")) {
			// Modelo 417
			if (input.isBorrador())
				return new MIModelo420Handler().obtenerBorrador417(declaracion); // Borrador
			else
				return new MIModelo420Handler().obtenerPresentacion417(declaracion); // Fichero para presentación
		} else if (declaracion.contains("MOD=\"420\"")) {
			// Modelo 420
			if (input.isBorrador())
				return new MIModelo420Handler().obtenerBorrador420(declaracion); // Borrador
			else
				return new MIModelo420Handler().obtenerPresentacion420(declaracion); // Fichero para presentación
		} else {
			return new MIModelo420Handler().errorModeloNoSoportado();
		}
	}
	
//	private static String obtenerXML() {
//		String fileXML =
//			"<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>" +
//			"<DEC MOD=\"425\" ANY=\"2025\" PER=\"0A\">" +
//			"    <IDE RDM=\"N\" RPE=\"N\">" +
//			"        <OTP NIF=\"12345678Z\" NRS=\"EMPRESA DE PRUEBA, S.L.\" SVP=\"CL\" NVP=\"SANTA ANA SDFDFDSFDSFDSF\" NPK=\"120\" ESC=\"E\" PIS=\"P\" PUE=\"PT\" LOC=\"UTEBO\" TEL=\"976111111\" POP=\"50\" CMU=\"50272\" CP=\"50180\"/>" +
//			"    </IDE>" +
//			"    <EST DTP=\"N\" RECC=\"N\">" +
//			"        <ACT CLA=\"1\" EPI=\"11211\" REG=\"1\" PRO=\"000\" DEF=\"000\" ESP=\"\"/>" +
//			"    </EST>" +
//			"    			<REP TIP=\"PF\">" +
//			"    			<OTP>" +
//			"    			<PER NRS=\"NOMBRE REPRESENTANTE\" NIF=\"11111111H\"/>" +
//			"    			<DIR SVP=\"CL\" NVP=\"NOMBRE DE LA VIA DEL REPRESENTANTE\" NPK=\"99\" ESC=\"E\" PIS=\"PI\" PUE=\"PU\" LOC=\"LOCALIDAD REP\" POP=\"35\" CP=\"35002\" TEL=\"976000000\" CMU=\"35016\"/>" +			
//			"    			</OTP>" +
//			"    			</REP>" +
//			"    <REG RES=\"7000\">" +
//			"        <DEV TBA=\"100000\" TCU=\"7000\">" +
//			"            <RGO BAS=\"100000\" TIP=\"700\" CUO=\"7000\"/>" +
//			"        </DEV>" +
//			"    </REG>" +
//			"    <LIQ RCU=\"000\" CPA=\"000\" SUT=\"7000\" RLI=\"7000\"/>" +
//			"    <AUT TOA=\"7000\" TOM=\"000\" IMC=\"000\" IMD=\"000\"/>" +
//			"    <OPE REG=\"100000\" RES=\"000\" REC=\"000\" EXP=\"000\" EXE=\"000\" OIE=\"000\" ECD=\"000\" ESD=\"000\" NSJ=\"000\" REAG=\"000\" REBU=\"000\" REAV=\"000\" EBI=\"000\" EBT=\"000\" IBE=\"000\" CND=\"000\" ODD=\"000\" TOT=\"100000\"/>" +
//			"    <RCC>" +
//			"        <IEB BAS=\"000\" CUO=\"000\"/>" +
//			"        <IAB BAS=\"000\" CUO=\"000\"/>" +
//			"    </RCC>" +
//			"</DEC>";		
//		return fileXML;
//		
//	}
//	
//	public static void main(String[] args) {
//		
//		MIModelo420Request input = new MIModelo420Request();
//		input.setDeclaracion(Base64.getEncoder().encodeToString(obtenerXML().getBytes()));
//		input.setBorrador(false);
//		
//		MIModelo420RequestHandler mi = new MIModelo420RequestHandler();
//		mi.handleRequest(input, null);
//		
//	}
	
}

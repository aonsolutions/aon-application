package net.aonsolutions.aws.atc.lambda;

import java.util.Base64;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class MIModelo420RequestHandler implements RequestHandler<MIModelo420Request, MIModelo420Result> {
	
	@Override
	public MIModelo420Result handleRequest(MIModelo420Request input, Context context) {
		String declaracion = new String(Base64.getDecoder().decode(input.getDeclaracion()));
		System.out.println(declaracion);
		// FALTA - PARA EL MODELO 425 ESTA PUESTA AHORA MISMO LA VERSION 6.2.0 DEL MODULO DE IMPRESION, QUE ES DEL EJERCICIO 2024, LA DEL 2025 AUN NO ESTA DISPONIBLE EN LA ATC
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
	
	private static String obtenerXML() {
		String fileXML =
			"<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>" +
			"<DEC MOD=\"425\" ANY=\"2024\" PER=\"0A\">" +
			"    <IDE RDM=\"N\" RPE=\"N\">" +
			"        <OTP NIF=\"12345678Z\" NRS=\"EMPRESA DE PRUEBA, S.L.\" SVP=\"CL\" NVP=\"SANTA ANA SDFDFDSFDSFDSF\" NPK=\"120\" ESC=\"E\" PIS=\"P\" PUE=\"PT\" LOC=\"UTEBO\" TEL=\"976111111\" POP=\"50\" CMU=\"50272\" CP=\"50180\"/>" +
			"    </IDE>" +
			"    <EST DTP=\"N\" RECC=\"N\">" +
//			"        <ACT CLA=\"1\" EPI=\"14191\" REG=\"1\" PRO=\"000\" DEF=\"000\" ESP=\"\"/>" +
			" 	     <ACT CLA=\"1\" EPI=\"16919\" REG=\"1\"/>" +
//			"        <ACT REG=\"5\" EPI=\"16732\" CLA=\"1\"/>" +
			"    </EST>" +
//			"    <REP FPO=\"01/01/2001\" NOT=\"NOTARIA1\" TIP=\"PJ\">" +
//			"        <OTP>" +
//			"            <PER NIF=\"11111111H\" NRS=\"REPRESENTANTE LEGAL\"/>" +
//			"        </OTP>" +
//			"    </REP>" +
			"    			<REP TIP=\"PF\">" +
//			"    			<OTP TPE=\"RP\" SEC=\"1\">" +
			"    			<OTP>" +
			"    			<PER NRS=\"NOMBRE REPRESENTANTE\" NIF=\"11111111H\"/>" +
//			"    			<DIR PAI=\"ES\" CP=\"35002\" CMU=\"35016\" POP=\"35\" NPK=\"99\" NVP=\"NOMBRE DE LA VIA DEL REPRESENTANTE\" SVP=\"CL\"/>" +
			"    			<DIR SVP=\"CL\" NVP=\"NOMBRE DE LA VIA DEL REPRESENTANTE\" NPK=\"99\" ESC=\"E\" PIS=\"PI\" PUE=\"PU\" LOC=\"LOCALIDAD REP\" POP=\"35\" CP=\"35002\" TEL=\"976000000\" CMU=\"35016\"/>" +			
			"    			</OTP>" +
			"    			</REP>" +
			
//			"    <REG RES=\"-307173\">" +
//			"        <DEV TBA=\"186300\" TCU=\"3450\">" +
//			"            <RGO BAS=\"161300\" TIP=\"000\" CUO=\"000\"/>" +
//			"            <RGO BAS=\"180000\" TIP=\"2000\" CUO=\"36000\"/>" +
//			"            <MBC BAS=\"-155000\" CUO=\"-32550\"/>" +
//			"        </DEV>" +
//			"        <DED TOT=\"310623\">" +
//			"            <OIC BAS=\"2477900\" CUO=\"298394\"/>" +
//			"            <OII BAS=\"271733\" CUO=\"51919\"/>" +
//			"            <RED BAS=\"-370000\" CUO=\"-41550\"/>" +
//			"            <CRA CUO=\"1860\"/>" +
//			"        </DED>" +
//			"    </REG>" +
			
			"    <RES RES=\"101250\">" +
			"       <MOD EPI=\"16919\" SEC=\"2\" MOD1=\"200\" MOD2=\"1000\" MOD3=\"000\" MOD4=\"000\" MOD5=\"000\" MOD6=\"000\" MOD7=\"000\" TOT=\"226670\" CASA=\"226670\" CASB=\"000\" CASC=\"000\" CASD=\"226670\" CASE=\"4800\" CASF=\"108802\" CASG=\"226670\"/>" +
//			"       <MOD EPI=\"16919\" SEC=\"REPARACIÓN DE CALZADO\" MOD1=\"200\" MOD2=\"1000\" CASA=\"226670\" CASB=\"000\" CASC=\"000\" CASD=\"226670\" CASE=\"4800\" CASF=\"108802\" CASG=\"226670\"/>"+
			"       <CRS TRS=\"226670\" CAF=\"000\" CBI=\"000\" RCU=\"000\" TOT=\"226670\" DAF=\"000\" DBI=\"000\" TOD=\"000\"/>" +
			"    </RES>" +
			
			"    <LIQ RCU=\"000\" CPA=\"000\" SUT=\"000\" RLI=\"000\"/>" +
			"    <AUT TOA=\"000\" TOM=\"000\" IMC=\"000\" IMD=\"000\"/>" +
			"    <OPE REG=\"000\" RES=\"000\" REC=\"000\" EXP=\"000\" EXE=\"000\" OIE=\"000\" ECD=\"000\" ESD=\"000\" NSJ=\"000\" REAG=\"000\" REBU=\"000\" REAV=\"000\" EBI=\"000\" EBT=\"000\" IBE=\"000\" CND=\"000\" ODD=\"000\" TOT=\"000\"/>" +
			"    <RCC>" +
			"        <IEB BAS=\"000\" CUO=\"000\"/>" +
			"        <IAB BAS=\"000\" CUO=\"000\"/>" +
			"    </RCC>" +
//			"    <RPE EPE=\"186300\" ECM=\"000\" ONS=\"000\" OFC=\"000\" IST=\"000\" TOT=\"000\"/>" +
			"</DEC>";		
		return fileXML;
	}
	
	public static void main(String[] args) {
		
		MIModelo420Request input = new MIModelo420Request();
		input.setDeclaracion(Base64.getEncoder().encodeToString(obtenerXML().getBytes()));
		input.setBorrador(false);
		
		MIModelo420RequestHandler mi = new MIModelo420RequestHandler();
		mi.handleRequest(input, null);
		
	}
	
}

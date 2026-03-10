package net.aonsolutions.aws.atc.lambda;

import java.util.Base64;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class MIModelo420RequestHandler implements RequestHandler<MIModelo420Request, MIModelo420Result> {
	
	@Override
	public MIModelo420Result handleRequest(MIModelo420Request input, Context context) {
		String declaracion = new String(Base64.getDecoder().decode(input.getDeclaracion()));
		System.out.println(declaracion);
		
		// MODELOS 417, 420, 421 y 425
		if (input.isBorrador())
			return new MIModelo420Handler().obtenerBorrador(declaracion);     // Borrador
		else
			return new MIModelo420Handler().obtenerPresentacion(declaracion); // Fichero para presentación
		
		// MODELO 415
//		if (input.isBorrador())
//			return new MIModelo420Handler().obtenerBorrador415(declaracion);     // Borrador
//		else
//			return new MIModelo420Handler().obtenerPresentacion415(declaracion); // Fichero para presentación
		
	}
	
// PRUEBAS ------------------------------------------------------------------------------------------------------------------------------	
	
//	private static String obtenerXML420() {
//		String fileXML =
//			"<DEC MOD=\"420\" ANY=\"2026\" PER=\"1T\">"+
//			    "<IDE ACO=\"N\" RECC=\"N\" DRECC=\"N\" EOP=\"N\" ACR=\"N\">"+
//			        "<OTP NIF=\"B50111111\" NRS=\"EMPRESA DE PRUEBA, S.L.\" SVP=\"CL\" NVP=\"SANTA ANA\" NPK=\"1\" ESC=\"\" PIS=\"\" PUE=\"\" LOC=\"UTEBO LOCALIDAD\" TEL=\"976000001\" MOV=\"600000000\" EMA=\"correo@correo.com\" POP=\"50\" CMU=\"50272\" CP=\"50180\"/>"+
//			    "</IDE>"+
//			    "<IGI_DEV TOT=\"37650\">"+
//			        "<DEV BAS=\"10000\" TIP=\"000\" CUO=\"000\"/>"+
//			        "<DEV BAS=\"20000\" TIP=\"300\" CUO=\"600\"/>"+
//			        "<DEV BAS=\"30000\" TIP=\"500\" CUO=\"1500\"/>"+
//			        "<DEV BAS=\"40000\" TIP=\"700\" CUO=\"2800\"/>"+
//			        "<DEV BAS=\"50000\" TIP=\"950\" CUO=\"4750\"/>"+
//			        "<DEV BAS=\"60000\" TIP=\"1500\" CUO=\"9000\"/>"+
//			        "<DEV BAS=\"70000\" TIP=\"2000\" CUO=\"14000\"/>"+
//			        "<DEV BAS=\"80000\" TIP=\"100\" CUO=\"800\"/>"+
//			        "<OIN BAS=\"90000\" CUO=\"2000\"/>"+
//			        "<MBC BAS=\"100000\" CUO=\"2200\"/>"+
//			    "</IGI_DEV>"+
//			    "<IGI_DED TOT=\"42000\">"+
//			        "<OIC BAS=\"600000\" CUO=\"42000\"/>"+
//			        "<OII BAS=\"000\" CUO=\"000\"/>"+
//			        "<IMC BAS=\"000\" CUO=\"000\"/>"+
//			        "<IMI BAS=\"000\" CUO=\"000\"/>"+
//			        "<RED BAS=\"000\" CUO=\"000\"/>"+
//			        "<CRA CUO=\"000\"/>"+
//			        "<RBI CUO=\"000\"/>"+
//			        "<RIA CUO=\"000\"/>"+
//			        "<RPP CUO=\"000\"/>"+
//			    "</IGI_DED>"+
//			    "<LIQ DIF=\"-4350\" RCU=\"000\" CPA=\"000\" DAC=\"000\" RLI=\"-4350\"/>"+
//			    "<RES TIP=\"C\" IMP=\"4350\"/>"+
//			    "<ADI EOA=\"000\" ODD=\"000\"/>"+
//			    "<RCC>"+
//			        "<IEB BAS=\"000\" CUO=\"000\"/>"+
//			        "<IAB BAS=\"000\" CUO=\"000\"/>"+
//			    "</RCC>"+
//			"</DEC>";
//	    return fileXML;
//    
//	}
//	
//	private static String obtenerXML425() {
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
//	private static String obtenerXML417() {
//		
//		String fileXML = 
//			"<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>" +
//			"<DEC MOD=\"417\" ANY=\"2026\" PER=\"01\">" +
//			    "<IDE RDM=\"N\" RECC=\"N\" DRECC=\"N\" EOP=\"N\" ACR=\"N\">" +
//			        "<OTP NIF=\"B50111111\" NRS=\"EMPRESA DE PRUEBA, S.L.\" SVP=\"CL\" NVP=\"SANTA ANA\" NPK=\"1\" ESC=\"\" PIS=\"\" PUE=\"\" LOC=\"UTEBO LOCALIDAD\" TEL=\"976000001\" MOV=\"600000000\" EMA=\"correo@correo.com\" POP=\"50\" CMU=\"50272\" CP=\"50180\"/>" +
//			    "</IDE>" +
//			    "<IGI_DEV TOT=\"7000\">" +
//			        "<DEV BAS=\"100000\" TIP=\"700\" CUO=\"7000\"/>" +
//			        "<OIN BAS=\"000\" CUO=\"000\"/>" +
//			        "<MBC BAS=\"000\" CUO=\"000\"/>" +
//			    "</IGI_DEV>" +
//			    "<IGI_DED TOT=\"31500\">" +
//			        "<OIC BAS=\"150000\" CUO=\"31500\"/>" +
//			        "<OII BAS=\"000\" CUO=\"000\"/>" +
//			        "<IMC BAS=\"000\" CUO=\"000\"/>" +
//			        "<IMI BAS=\"000\" CUO=\"000\"/>" +
//			        "<RED BAS=\"000\" CUO=\"000\"/>" +
//			        "<CRA CUO=\"000\"/>" +
//			        "<RBI CUO=\"000\"/>" +
//			        "<RIA CUO=\"000\"/>" +
//			        "<RPP CUO=\"000\"/>" +
//			    "</IGI_DED>" +
//			    "<LIQ DIF=\"-24500\" RCU=\"000\" CPA=\"000\" DAC=\"000\" RLI=\"-24500\"/>" +
//			    "<RES TIP=\"C\" IMP=\"24500\"/>" +
//			    "<ADI EOA=\"000\" ODD=\"000\"/>" +
//			    "<RCC>" +
//			        "<IEB BAS=\"000\" CUO=\"000\"/>" +
//			        "<IAB BAS=\"000\" CUO=\"000\"/>" +
//			    "</RCC>" +
//		    "</DEC>";
//    
//    	return fileXML;
//	}
//	
//	private static String obtener415() {
//		
//		String fileTxt = 
//			 "14152025B50111111EMPRESA DE PRUEBA, S.L.                 000000002 000000002602006000000004 000000003527366000000000 000000000000000000000000 000000000000000000000000 000000000000000000000000 000000000000000000000000 000000000000000 0000000000000" + "\r\n" 
//			+"24152025B50111111B17456640PADRIAN SANCHO MARQUES                                  000000000000000 000000000602500000000000000000 000000000000000 0000000000010000000 000000000121000 000000000000000 000000000000100 000000000181500 000000000000000 000000000000200 000000000300000 000000000000000 000000000000300 000000000000000 000000000000000 000000000000400" +"\r\n" 
//			+"24152025B50111111B26485990HADA PUNZANO MILLAN                                     000000000000000 000000001380246000000000000000 000000000000000 0000000000000000000 000000000760246 000000000000000 000000000000000 000000000363000 000000000000000 000000000000000 000000000257000 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000" +"\r\n" 
//			+"24152025B50111111B26485990HADA PUNZANO MILLAN                                 X   000000000000000 000000000157300000000000000000 000000000000000 0000000000000000000 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000" +"\r\n" 
//			+"24152025B50111111AA01021690ALAVESA DE PINTURAS                                    000000000000000 000000002432606000000000000000 000000000000000 0000000000000000000 000000000099000 000000000000000 000000000000000 000000001586380 000000000000000 000000000000000 000000000211726 000000000000000 000000000000000 000000000535500 000000000000000 000000000000000" +"\r\n" 
//			+"24152025B50111111AA01021690ALAVESA DE PINTURAS                                X   000000000169400 000000000169400000000000000000 000000000000000 0000000000000000000 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000" +"\r\n" 
//			+"24152025B50111111B         OSS CLIENTE INTRA ALEMANIA OSS                   DE    000000000000000 000000001386320000000000000000 000000000000000 0000000000000000000 000000000509070 000000000000000 000000000000000 000000000877250 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000 000000000000000" +"\r\n" 
////			+"34152025B5011111117456640PADRIAN SANCHO MARQUES                             00000000602500N                         CL   SEVILLA                                           NUM00002                                                          ZARAGOZA                      ZARAGOZA                      502975050001" +"\r\n"
//		;
//		
//		return fileTxt;
//	
//	}
//
//	private static String obtenerXML421() {
//		return
//		"<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>" +
////		"<DEC ADM=\"35601\" MOD=\"421\" ANY=\"2026\" PER=\"1T\" ACR=\"\">"+
////	    "<OTP NIF=\"12345678Z\" NRS=\"APE APE, NOMBRE\" SVP=\"CL\" NVP=\"SANTA ANA\" NPK=\"1\" ESC=\"\" PIS=\"\" PUE=\"\" LOC=\"\" TEL=\"976000001\" MOV=\"600000000\" EMA=\"correo@correo.com\" POP=\"35\" CP=\"35468\" CMU=\"35001\" />"+
////	    "<AUT/>" +
////	    "<RESULTADO_LIQUIDACION TIP=\"S\"/>"+
////	    "</DEC>";
//
////				fileString = fileString.replace("'", " ");
////				fileString = fileString.replace("&", "Y");	
////				fileString = fileString.replace("Á", "A");
////				fileString = fileString.replace("É", "E");
////				fileString = fileString.replace("Í", "I");
////				fileString = fileString.replace("Ó", "O");
////				fileString = fileString.replace("Ú", "U");
////				fileString = fileString.replace("Ü", "U");		
//				
////		"<DEC ADM=\"35601\" MOD=\"421\" ANY=\"2026\" PER=\"1T\" ACR=\"\">"+
////		    "<OTP NIF=\"12345678Z\" NRS=\"APE APE, NOMBRE\" SVP=\"CL\" NVP=\"SANTA ANA\" NPK=\"1\" ESC=\"\" PIS=\"\" PUE=\"\" LOC=\"\" TEL=\"976000001\" MOV=\"600000000\" EMA=\"correo@correo.com\" POP=\"35\" CMU=\"35001\" CP=\"35468\"/>"+
////		    "<AUT C06=\"83113\" C12=\"000\" C13=\"000\" C14=\"000\" C15=\"000\" C16=\"000\" C17=\"000\" C18=\"000\" C19=\"83113\">"+
////		        "<EPIGRAFES>"+
////		            "<EPIGRAFE EPI=\"1722\" SEC=\"1\" MOD1=\"100\" MOD2=\"100\" MOD3=\"000\" MOD4=\"000\" MOD5=\"000\" MOD6=\"000\" MOD7=\"000\" TOT=\"65644\" DIAS_EJE_ANT=\"0\" DIAS_TRI_CUR=\"90\" IND_COR=\"000\" CUO_RES_TRIM=\"9190\"/>"+
////		            "<EPIGRAFE EPI=\"1722\" SEC=\"2\" MOD1=\"100\" MOD2=\"100\" MOD3=\"000\" MOD4=\"000\" MOD5=\"000\" MOD6=\"000\" MOD7=\"000\" TOT=\"28757\" DIAS_EJE_ANT=\"0\" DIAS_TRI_CUR=\"90\" IND_COR=\"000\" CUO_RES_TRIM=\"4026\"/>"+
////		            "<EPIGRAFE EPI=\"16919\" SEC=\"1\" MOD1=\"100\" MOD2=\"100\" MOD3=\"000\" MOD4=\"000\" MOD5=\"000\" MOD6=\"000\" MOD7=\"000\" TOT=\"109301\" DIAS_EJE_ANT=\"0\" DIAS_TRI_CUR=\"90\" IND_COR=\"000\" CUO_RES_TRIM=\"25139\"/>"+
////		            "<EPIGRAFE EPI=\"16919\" SEC=\"2\" MOD1=\"100\" MOD2=\"100\" MOD3=\"000\" MOD4=\"000\" MOD5=\"000\" MOD6=\"000\" MOD7=\"000\" TOT=\"194602\" DIAS_EJE_ANT=\"0\" DIAS_TRI_CUR=\"90\" IND_COR=\"000\" CUO_RES_TRIM=\"44758\"/>"+
////		        "</EPIGRAFES>"+
////		    "</AUT>"+
////		    "<RESULTADO_LIQUIDACION TIP=\"I\" IMP=\"83113\" FPA=\"5\"/>"+
////		"</DEC>";
//		
////	"<DEC ADM=\"35601\" MOD=\"421\" ANY=\"2026\" PER=\"1T\" ACR=\"\">"+
////	    "<OTP NIF=\"12345678Z\" NRS=\"APE APE, NOMBRE\" SVP=\"CL\" NVP=\"SANTA ANA\" NPK=\"1\" ESC=\"\" PIS=\"\" PUE=\"\" LOC=\"\" TEL=\"976000001\" MOV=\"600000000\" EMA=\"correo@correo.com\" POP=\"35\" CMU=\"35001\" CP=\"35468\"/>"+
////	    "<AUT C06=\"19633\" C12=\"000\" C13=\"000\" C14=\"000\" C15=\"000\" C16=\"000\" C17=\"000\" C18=\"000\" C19=\"19633\">"+
////	        "<EPIGRAFES>"+
////	            "<EPIGRAFE EPI=\"1722\" SEC=\"JHJHJH\" MOD1=\"300\" MOD2=\"2500\" MOD3=\"000\" MOD4=\"000\" MOD5=\"000\" MOD6=\"000\" MOD7=\"000\" TOT=\"140237\" DIAS_EJE_ANT=\"0\" DIAS_TRI_CUR=\"90\" IND_COR=\"000\" CUO_RES_TRIM=\"19633\"/>"+
////	        "</EPIGRAFES>"+
////	    "</AUT>"+
////	    "<RESULTADO_LIQUIDACION TIP=\"I\" IMP=\"19633\" FPA=\"5\"/>"+
////	"</DEC>";
//
//		"<DEC ADM=\"35601\" MOD=\"421\" ANY=\"2026\" PER=\"1T\" ACR=\"\">"+
//	    "<OTP NIF=\"12345678Z\" NRS=\"APE APE, NOMBRE\" SVP=\"CL\" NVP=\"SANTA ANA\" NPK=\"1\" ESC=\"\" PIS=\"\" PUE=\"\" LOC=\"\" TEL=\"976000001\" MOV=\"600000000\" EMA=\"correo@correo.com\" POP=\"35\" CMU=\"35001\" CP=\"35468\"/>"+
//	    "<AUT C06=\"83113\" C12=\"000\" C13=\"000\" C14=\"000\" C15=\"000\" C16=\"000\" C17=\"000\" C18=\"000\" C19=\"83113\">"+
//	        "<EPIGRAFES>"+
//	            "<EPIGRAFE EPI=\"1722\" MOD1=\"100\" MOD2=\"100\" MOD3=\"000\" MOD4=\"000\" MOD5=\"000\" MOD6=\"000\" MOD7=\"000\" TOT=\"28757\" DIAS_EJE_ANT=\"0\" DIAS_TRI_CUR=\"90\" IND_COR=\"000\" CUO_RES_TRIM=\"4026\"/>"+
//	            "<EPIGRAFE EPI=\"1722\" MOD1=\"100\" MOD2=\"100\" MOD3=\"000\" MOD4=\"000\" MOD5=\"000\" MOD6=\"000\" MOD7=\"000\" TOT=\"65644\" DIAS_EJE_ANT=\"0\" DIAS_TRI_CUR=\"90\" IND_COR=\"000\" CUO_RES_TRIM=\"9190\"/>"+
//	            "<EPIGRAFE EPI=\"16919\" MOD1=\"100\" MOD2=\"100\" MOD3=\"000\" MOD4=\"000\" MOD5=\"000\" MOD6=\"000\" MOD7=\"000\" TOT=\"194602\" DIAS_EJE_ANT=\"0\" DIAS_TRI_CUR=\"90\" IND_COR=\"000\" CUO_RES_TRIM=\"44758\"/>"+
//	            "<EPIGRAFE EPI=\"16919\" MOD1=\"100\" MOD2=\"100\" MOD3=\"000\" MOD4=\"000\" MOD5=\"000\" MOD6=\"000\" MOD7=\"000\" TOT=\"109301\" DIAS_EJE_ANT=\"0\" DIAS_TRI_CUR=\"90\" IND_COR=\"000\" CUO_RES_TRIM=\"25139\"/>"+
//	        "</EPIGRAFES>"+
//	    "</AUT>"+
//	    "<RESULTADO_LIQUIDACION TIP=\"I\" IMP=\"83113\" FPA=\"5\"/>"+
//	    "</DEC>";
//
//	
//	}
//	
//	public static void main(String[] args) {
//		
//		MIModelo420Request input = new MIModelo420Request();
//		input.setDeclaracion(Base64.getEncoder().encodeToString(obtenerXML421().getBytes()));
////		input.setDeclaracion(Base64.getEncoder().encodeToString(obtener415().getBytes()));
//		input.setBorrador(false);
//		
//		MIModelo420RequestHandler mi = new MIModelo420RequestHandler();
////		mi.handleRequest(input, null);
//		
//		MIModelo420Result result = mi.handleRequest(input, null);
//		String resultado = result.getResultado();
//		
//		// grabar la variable resultado en un fichero externo
//		PrintWriter writer;
//		try {
//			writer = new PrintWriter("c:\\tmp\\resultado_421.atc", "UTF-8");
//			writer.print(resultado);
////			writer.print(obtener415());
//			writer.close();
//		} catch (FileNotFoundException | UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		
//	}
	
// --------------------------------------------------------------------------------------------------------------------------------------
	
}

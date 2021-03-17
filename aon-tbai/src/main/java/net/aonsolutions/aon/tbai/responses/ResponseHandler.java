package net.aonsolutions.aon.tbai.responses;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import net.aonsolutions.aon.tbai.exceptions.http.StatusCodeException;
import net.aonsolutions.aon.tbai.exceptions.response.TbaiResponseException;
import net.aonsolutions.aon.tbai.toolkit.DataToolkit;

public class ResponseHandler {

	public static void HandleStatusCode(int code) throws StatusCodeException {
		switch (code) {
			case 400: throw new StatusCodeException(code,"Bad request");
			case 401: throw new StatusCodeException(code,"Unathorized");
			case 402: throw new StatusCodeException(code,"Payment required");
			case 403: throw new StatusCodeException(code,"Forbidden");
			case 404: throw new StatusCodeException(code,"Not found");
			case 405: throw new StatusCodeException(code,"Method not allowed");
			case 406: throw new StatusCodeException(code,"Not acceptable");
			case 407: throw new StatusCodeException(code,"Proxy authentication required");
			case 408: throw new StatusCodeException(code,"Request timeout");
			case 409: throw new StatusCodeException(code,"Conflict");
			case 410: throw new StatusCodeException(code,"Gone");
			case 411: throw new StatusCodeException(code,"Length required");
			case 412: throw new StatusCodeException(code,"Precondition failed");
			case 413: throw new StatusCodeException(code,"Payload too large");
			case 414: throw new StatusCodeException(code,"URI too long");
			case 415: throw new StatusCodeException(code,"Unsupported media type");
			case 416: throw new StatusCodeException(code,"Requested range not satisfiable");
			case 417: throw new StatusCodeException(code,"Expectation failed");
			case 418: throw new StatusCodeException(code,"I'm a teapot");
			case 421: throw new StatusCodeException(code,"Misdirected request");
			case 422: throw new StatusCodeException(code,"Unprocessable entity");
			case 423: throw new StatusCodeException(code,"Locked");
			case 424: throw new StatusCodeException(code,"Failed dependency");
			case 426: throw new StatusCodeException(code,"Upgrade required");
			case 428: throw new StatusCodeException(code,"Precondition required");
			case 429: throw new StatusCodeException(code,"Too Many requests");
			case 431: throw new StatusCodeException(code,"Request header fields too large");
			case 451: throw new StatusCodeException(code,"Unavaliable for legal reasons");
		
			case 500: throw new StatusCodeException(code,"Internal server error");
			case 501: throw new StatusCodeException(code,"Not implemented");
			case 502: throw new StatusCodeException(code,"Bad gateway");
			case 503: throw new StatusCodeException(code,"Service unavailable");
			case 504: throw new StatusCodeException(code,"Gateway timeout");
			case 505: throw new StatusCodeException(code,"HTTP version not supported");
			case 506: throw new StatusCodeException(code,"Variant also negotiates");
			case 507: throw new StatusCodeException(code,"Insufficient storage");
			case 508: throw new StatusCodeException(code,"Loop detected");
			case 510: throw new StatusCodeException(code,"Not extended");
			case 511: throw new StatusCodeException(code,"Network autentication required");				
			
			default: break;
		}
	}

	public static void HandleTbaiResponse(byte[] bytes) throws TbaiResponseException{
		TbaiResponse response = getTbaiResponse(bytes);
		
		Integer status = response.getStatus().orElse(-1);
		switch (status) {
			case -1: throw new TbaiResponseException(-1, "NO RESPONSE FOUND");
			case  0: break;
			case  1: throw new TbaiResponseException(1,  "TBAI NOT ACCEPTED");  
			default: throw new TbaiResponseException(status, "UNKNOWN RESPONSE: "  + response.getDescription().orElse("-"));
		}	
		
		Integer validation_code = response.getValidation_code().orElse(-1);
		switch (validation_code) {
			default: throw new TbaiResponseException(validation_code, response.getValidation_description().orElse("-"));
		}
	}
	
	private static TbaiResponse getTbaiResponse(byte[] bytes) {
		try {
			System.out.println("\t Parsing XML response.... ");
			
			InputStream is = new ByteArrayInputStream(bytes);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);

			System.out.println("\t XML version: \t " + doc.getXmlVersion());
						
			String estado;
			try{estado = doc.getElementsByTagName("Estado").item(0).getTextContent();}
			catch(Exception e) {estado = null;}
			
			String fecha_str;
			try{fecha_str = doc.getElementsByTagName("FechaRecepcion").item(0).getTextContent();}
			catch(Exception e) {fecha_str = null;}
			
			String descripcion;
			try{descripcion = doc.getElementsByTagName("Descripcion").item(0).getTextContent();}
			catch(Exception e) {descripcion = null;}
			
			String descripcion_eus;
			try{descripcion_eus = doc.getElementsByTagName("Azalpena").item(0).getTextContent();}
			catch(Exception e) {descripcion_eus = null;}
			
			String validation_code;
			try{validation_code = doc.getElementsByTagName("Codigo").item(0).getTextContent();}
			catch(Exception e) {validation_code = null;}
			
			String validation_desc;
			try{validation_desc = doc.getElementsByTagName("Descripcion").item(1).getTextContent();}
			catch(Exception e) {validation_desc = null;}
			
			String validation_desc_eus;
			try{validation_desc_eus = doc.getElementsByTagName("Azalpena").item(1).getTextContent();}
			catch(Exception e) {validation_desc_eus = null;}
			
			
			System.out.println("\t Status code: \t" + estado);
			System.out.println("\t Reception date: \t" + fecha_str);
			System.out.println("\t Description: \t" + descripcion);
			System.out.println("\t Azalpena: \t" + descripcion_eus);
			System.out.println("\n\t Validation results: ");
			System.out.println("\t-------------------------");
			System.out.println("\t Code: \t" + validation_code);
			System.out.println("\t Description: \t" + validation_desc);
			System.out.println("\t Azalpena: \t" + validation_desc_eus);
	
			
			TbaiResponse response = new TbaiResponse();
			response
			.setStatus(Integer.parseInt(estado))
			.setDescription(descripcion)
			.setDescription_eus(descripcion_eus)
			.setReception_date(DataToolkit.parseDate(fecha_str, "dd-MM-yyyy hh:mm:ss"))
			.setValidation_code(Integer.parseInt(validation_code))
			.setValidation_description(validation_desc)
			.setValidation_description_eus(validation_desc_eus);
			
			return response;
		}catch(IOException | ParserConfigurationException | SAXException e) {e.printStackTrace();}
		return null;
	}

}

package net.aonsolutions.aon.tbai.responses;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import net.aonsolutions.aon.tbai.exceptions.http.StatusCodeException;

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

	public static void HandleTbaiResponse(byte[] bytes){
		try {
			System.out.println("\t Parsing XML response.... ");
			
			InputStream is = new ByteArrayInputStream(bytes);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);

			System.out.println("\t XML version: \t " + doc.getXmlVersion());
			System.out.println("\t XML Response:");
			
			Node   ns2 		 		= doc.getFirstChild();
			String estado 	 		= doc.getElementsByTagName("Estado").item(0).getTextContent();
			String fecha_str 		= doc.getElementsByTagName("FechaRecepcion").item(0).getTextContent();
			String descripcion 		= doc.getElementsByTagName("Descripcion").item(0).getTextContent();
			String descripcion_eus 	= doc.getElementsByTagName("Azalpena").item(0).getTextContent();
			String validation_code 	= doc.getElementsByTagName("Codigo").item(0).getTextContent();
			String validation_desc	= doc.getElementsByTagName("Descripcion").item(1).getTextContent();
			String validation_desc_eus	= doc.getElementsByTagName("Azalpena").item(1).getTextContent();
			
			
			System.out.println("\t Status code: \t" + estado);
			System.out.println("\t Reception date: \t" + fecha_str);
			System.out.println("\t Description: \t" + descripcion);
			System.out.println("\t Azalpena: \t" + descripcion_eus);
			System.out.println("\t Validation code: \t" + validation_code);
			System.out.println("\t Validation description: \t" + validation_desc);
			System.out.println("\t Validation azalpena: \t" + validation_desc_eus);
	
			
			TbaiResponse response = new TbaiResponse();
			response
			.setStatus(null)
			.setDescription(null)
			.setDescription_eus(null)
			.setReception_date(null)
			.setValidation_code(null)
			.setValidation_description(null)
			.setValidation_description_eus(null);
			
		}catch(IOException | ParserConfigurationException | SAXException e) {e.printStackTrace();}
		
	}

}

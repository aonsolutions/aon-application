package net.aonsolutions.aon.tbai;

import static net.aonsolutions.aon.tbai.responses.ResponseHandler.HandleStatusCode;
import static net.aonsolutions.aon.tbai.responses.ResponseHandler.HandleTbaiResponse;
import static net.aonsolutions.aon.tbai.toolkit.DataToolkit.isEmpty;
import static net.aonsolutions.aon.tbai.toolkit.DataToolkit.isPresent;
import static net.aonsolutions.aon.tbai.toolkit.DataToolkit.parseDate;
import static net.aonsolutions.aon.tbai.toolkit.JsonToolkit.getArray;
import static net.aonsolutions.aon.tbai.toolkit.JsonToolkit.getNumber;
import static net.aonsolutions.aon.tbai.toolkit.JsonToolkit.getObject;
import static net.aonsolutions.aon.tbai.toolkit.JsonToolkit.getString;
import static net.aonsolutions.aon.tbai.toolkit.JsonToolkit.read;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.esferalia.aon.occam.api.model.type.Country;

import net.aonsolutions.aon.tbai._beans.Entity;
import net.aonsolutions.aon.tbai._beans.InvoiceDetailData;
import net.aonsolutions.aon.tbai._enums.IDtype;
import net.aonsolutions.aon.tbai._enums.Territory;
import net.aonsolutions.aon.tbai.emision.EmisionInvoice;
import net.aonsolutions.aon.tbai.emision.EmisionInvoice.TbaiEmisionInvoiceBuilder;
import net.aonsolutions.aon.tbai.emision.araba.ArabaEmisionValidator;
import net.aonsolutions.aon.tbai.emision.bizkaia.BizkaiaEmisionValidator;
import net.aonsolutions.aon.tbai.emision.gipuzkoa.GipuzkoaEmisionValidator;
import net.aonsolutions.aon.tbai.exceptions.http.StatusCodeException;
import net.aonsolutions.aon.tbai.exceptions.json.JsonNotFoundException;
import net.aonsolutions.aon.tbai.exceptions.json.JsonParseException;
import net.aonsolutions.aon.tbai.exceptions.response.TbaiResponseException;
import net.aonsolutions.aon.tbai.exceptions.validation.ValidationException;
import net.aonsolutions.aon.tbai.exceptions.xml.XMLCreationException;
import net.aonsolutions.aon.tbai.toolkit.DataToolkit;
import ticketbai.emision.Cabecera;
import ticketbai.emision.Factura;
import ticketbai.emision.HuellaTBAI;
import ticketbai.emision.Sujetos;
import ticketbai.emision.TicketBai;

public class TbaiMain {
	
	public static EmisionInvoice jsonToInvoice(final InputStream is) throws JsonParseException, JsonNotFoundException {
	
		if(isEmpty(is)) throw new JsonParseException("Cannot parse JSON file");	
		final JsonObject json ;
				
		try{json = read(is);}
		catch(Exception e) {throw new JsonNotFoundException("Json not found");}
		
		final String date = 				getString(json, "date");
		final JsonObject receiver_o = 		getObject(json, "receiver");
		
		final JsonObject address_o = 		getObject(receiver_o,"address");	
		final String rec_zip = 				getString(address_o, "zip");
		final String rec_address = 			getString(address_o, "address");
		final String rec_province = 		getString(address_o, "province");
		final String rec_city = 			getString(address_o, "city");
		final String rec_document = 		getString(receiver_o,"document");
		final String rec_name = 			getString(receiver_o,"name");	
		
		String rec_total_address = 	"";
		if(isPresent(rec_address) && isPresent(rec_city) && isPresent(rec_province))
		rec_total_address = rec_address + ". " + rec_city + ", " + rec_province; 		
			
		final String number = 				getString(json,"number");
		final JsonNumber total = 			getNumber(json,"total");
		
		final JsonObject sender_o = 		getObject(json,"sender");
		final JsonObject sender_address_o = getObject(sender_o ,"address");
		final String sen_zip =  			getString(sender_address_o, "zip");
		final String sen_country = 			getString(sender_address_o, "country");
		final String sen_address = 			getString(sender_address_o, "address");
		final String sen_province = 		getString(sender_address_o, "province");
		final String sen_city = 			getString(sender_address_o, "city");			
		final String sen_document = 		getString(sender_o, "document");
		final String sen_name = 			getString(sender_o, "name");
		
		String sen_total_address = 	"";
		if(isPresent(sen_address) && isPresent(sen_city) && isPresent(sen_province))
		sen_total_address = sen_address + ". " + sen_city + ", " + sen_province; 
		
		final String series = 				getString(json, "series");	
		final JsonArray details = 			getArray (json,  "details");
		final String category =  			getString(json, "category");
		
		//final JsonNumber id =				getNumber(json, "id");
		//final JsonArray taxes = 			getArray (json,"taxes");	
		//final JsonArray finances = 		getArray (json,  "finances");
		//final String transaction = 		getString(json, "transaction");
	
		final Entity   sender = new Entity();
		sender	.setNif(sen_document)
				.setName(sen_name)
				.setCountry((sen_country == null) ? null : Country.valueOf(sen_country))
				.setId_type(IDtype.NIF_IVA)
				.setId("0")
				.setZip(sen_zip)
				.setAddress(sen_total_address);
		
		final ArrayList<Entity> receivers = new ArrayList<>();
		Entity receiver = new Entity();
		receiver	.setNif(rec_document)
					.setName(rec_name)
					.setId_type(IDtype.NIF_IVA)
					.setId("0")
					.setZip(rec_zip)
					.setAddress(rec_total_address);
		receivers.add(receiver);
		
		final ArrayList<InvoiceDetailData> detail_list = new ArrayList<>();
		if(!isEmpty(details)) {
			for (JsonValue det : details) {
				final JsonObject o = 			(JsonObject) det;
				final String description = 		getString(o, "description");
				
				final JsonNumber jquantity = 		getNumber(o, "quantity");
				final JsonNumber jprice = 			getNumber(o, "price");
				final JsonNumber jdiscount = 		getNumber(o, "discount");
				final JsonNumber jtotal_amount = 	getNumber(o, "amount");
						
				final Double quantity = 		(isEmpty(jquantity)) 	 ? null : 	jquantity.doubleValue();
				final Double price = 			(isEmpty(jprice))    	 ? null : 	jprice.doubleValue();
				final Double discount = 		(isEmpty(jdiscount)) 	 ? null : 	jdiscount.doubleValue();
				final Double total_amount =		(isEmpty(jtotal_amount)) ? null : 	jtotal_amount.doubleValue();
				
				detail_list.add(new InvoiceDetailData(description, quantity,price, discount, total_amount));
			}	
		}
		
		final TbaiEmisionInvoiceBuilder builder = new TbaiEmisionInvoiceBuilder();		
		final EmisionInvoice invoice = 
			 builder
			.setSender				(sender)
			.setRecievers			(receivers)
			.setMultiple			(receivers.size() > 1)
			.setSeries				(series)
			.setNumber				(number)
			.setExpedition_date		(parseDate(date, "yyyy-MM-dd'T'hh:mm"))
			.setSimplified			(null)
			.setReplace_simplified	(null)
			.setRectification		(null)
			.setOperation_date		(parseDate(date, "yyyy-MM-dd"))
			.setDescription			(category)
			.setDetails				(detail_list)
			.setTotal_amount		((isEmpty(total))? null : total.doubleValue())
			.setSupported_retention	(null)												//NOT COMPULSORY
			.setTax_base_cost		(null)												//NOT COMPULSORY
			.setId_keys				(new ArrayList<>())
			.setBreakdown			(null)  
			.build();
		
		return invoice;
	}

	public static void createEmisionTBAI(final EmisionInvoice i,final String name,Territory territory) throws XMLCreationException, ValidationException, StatusCodeException, TbaiResponseException {
		try {			
			switch (territory) {
				case ARABA:				ArabaEmisionValidator.validate(i); 		break;
				case BIZKAIA: 			BizkaiaEmisionValidator.validate(i);  	break;
				case GIPUZKOA:			GipuzkoaEmisionValidator.validate(i); 	break;
				default:  				throw new ValidationException("Territory is not defined");
			}
			
			final Cabecera 		cabecera = 	i.getCabecera();
			final Sujetos 		sujetos = 	i.getSujetos();
			final Factura 		factura = 	i.getFactura();
			final HuellaTBAI 	huella =	i.getHuellaTbai();
			
			final TicketBai tbai = new TicketBai();
			tbai.setCabecera	(cabecera);
			tbai.setSujetos		(sujetos);
			tbai.setFactura		(factura);
			tbai.setHuellaTBAI	(huella);
			tbai.setSignature	(null);
			
			final JAXBContext jaxbContext     = JAXBContext.newInstance( TicketBai.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	 		
			
			final OutputStream os = new FileOutputStream( "./" + name);
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( tbai, os );
			jaxbMarshaller.marshal( tbai, bos );
			
			sendXML(new ByteArrayInputStream(bos.toByteArray()));		
		} 
		catch (JAXBException | FileNotFoundException e) {throw new XMLCreationException("ERROR WHILE ACCESSING DISK: Aborting...", e);} 
	}
	
	public static void sendXML(InputStream xml) throws StatusCodeException, TbaiResponseException {
		URL url;
		try {
			url = new URL("https://tbai-prep.egoitza.gipuzkoa.eus/WAS/HACI/HTBRecepcionFacturasWEB/rest/recepcionFacturas/alta");
			URLConnection con = url.openConnection();
			HttpURLConnection http = (HttpURLConnection)con;
			
			http.setRequestMethod("POST"); 
			con.setRequestProperty("Content-Type", "application/xml; charset=utf-8;");
			http.setDoOutput(true);
			
			OutputStream os = http.getOutputStream();
			os.write(xml.readAllBytes());
			os.close();
			
			System.out.println("\n\tServer status: \t" + http.getResponseCode() + ": " +http.getResponseMessage());			
			System.out.println("\tMethod used: \t" + http.getRequestMethod());
			System.out.println("\tEncoding used: \t" + http.getRequestProperty("Content-Type"));
			
			HandleStatusCode(http.getResponseCode());
			
			System.out.println("\n\t-------------------------------------------------------------------------------------------------------------------------------------------------");
			System.out.println("\t SERVICE RESPONSE: ");
			System.out.println("\t-------------------------------------------------------------------------------------------------------------------------------------------------");
			
			InputStream response = (InputStream) http.getContent();
			byte[] bytes = response.readAllBytes();
			HandleTbaiResponse(bytes);			
			DataToolkit.buildFile(bytes, "./response.xml");
		} 
		catch (MalformedURLException e) {e.printStackTrace();} 
		catch (IOException e) {e.printStackTrace();}	
	}
//	
//	public static void main(String[] args) {
//		try {for (int i = 0; i < 1; i++) sendXML(new FileInputStream("/home/akrck02/eclipse-workspace/aon.parent/aon-tbai/JSONtoTBAI.xml"));} 
//		catch (FileNotFoundException e) {e.printStackTrace();}
//	}
//	
	public static void createAnulacionTBAI(){/*TO DO uwu*/}

}

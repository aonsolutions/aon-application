package net.aonsolutions.aon.tbai;

import static net.aonsolutions.aon.tbai.Toolkit.JsonToolkit.getArray;
import static net.aonsolutions.aon.tbai.Toolkit.JsonToolkit.getNumber;
import static net.aonsolutions.aon.tbai.Toolkit.JsonToolkit.getObject;
import static net.aonsolutions.aon.tbai.Toolkit.JsonToolkit.getString;
import static net.aonsolutions.aon.tbai.Toolkit.JsonToolkit.read;
import static net.aonsolutions.aon.tbai.Toolkit.TbaiToolkit.parseDate;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.esferalia.aon.occam.api.model.type.Country;

import net.aonsolutions.aon.tbai.beans.invoice.TbaiEmisionInvoice;
import net.aonsolutions.aon.tbai.beans.invoice.TbaiEmisionInvoice.TbaiEmisionInvoiceBuilder;
import net.aonsolutions.aon.tbai.beans.invoice.enums.IDtype;
import net.aonsolutions.aon.tbai.beans.invoice.parts.Entity;
import net.aonsolutions.aon.tbai.beans.invoice.parts.InvoiceDetailData;
import net.aonsolutions.aon.tbai.exceptions.CannotCreateXMLException;
import ticketbai.emision.Cabecera;
import ticketbai.emision.Factura;
import ticketbai.emision.HuellaTBAI;
import ticketbai.emision.Sujetos;
import ticketbai.emision.TicketBai;

public class TbaiMain {
	
	

	//JSON TO INVOICE
	public static TbaiEmisionInvoice json_to_invoice(final InputStream is) {
	
		final JsonObject json = read(is);		
		final String date = 				getString(json, "date");
		
		final JsonObject receiver_o = 		getObject(json, "receiver");
		final JsonObject address_o = 		getObject(receiver_o,"address");	
		final String rec_zip = 				getString(address_o, "zip");
		final String rec_address = 			getString(address_o, "address");
		final String rec_province = 		getString(address_o, "province");
		final String rec_city = 			getString(address_o, "city");
		final String rec_document = 		getString(receiver_o,"document");
		final String rec_name = 			getString(receiver_o,"name");	
		final String rec_total_address = 	rec_address + ". " + rec_city + ", " + rec_province; 
		
		//final JsonArray taxes = 			getArray (json,"taxes");		
		final String number = 				getString(json,"number");
		final JsonNumber total = 			getNumber(json,"total");
		
		final JsonObject sender_o = 		getObject(json,"sender");
		final JsonObject sender_address_o = getObject(sender_o ,"address");
		final String sen_zip =  			getString(sender_address_o, "zip");
		final String sen_country = 			getString(sender_address_o, "country");
		final String sen_address = 			getString(sender_address_o, "address");
		final String sen_province = 		getString(sender_address_o, "province");
		final String sen_city = 			getString(sender_address_o, "city");			
		final String sen_total_address = 	sen_address + ". " + sen_city + ", " + sen_province; 
		final String sen_document = 		getString(sender_o, "document");
		final String sen_name = 			getString(sender_o, "name");
		
		final String series = 				getString(json, "series");	
		final JsonArray details = 			getArray (json,  "details");
		//final JsonNumber id =				getNumber(json, "id");
		final String category =  			getString(json, "category");
		//final JsonArray finances = 		getArray (json,  "finances");
		//final String transaction = 		getString(json, "transaction");
	
		final Entity   sender =    new Entity(sen_document, sen_name, Country.valueOf(sen_country), IDtype.NIF_IVA,"0", sen_zip, sen_total_address);		
		final ArrayList<Entity> receivers = new ArrayList<>();
		receivers.add(new Entity(rec_document,rec_name,Country.ES, IDtype.NIF_IVA, "0",rec_zip,rec_total_address));
		
		final ArrayList<InvoiceDetailData> detail_list = new ArrayList<>();
		for (JsonValue det : details) {
			final JsonObject o = 			(JsonObject) det;
			final String description = 		getString(o, "description");
			final Double quantity = 		getNumber(o, "quantity")	.doubleValue();
			final Double price = 			getNumber(o, "price")		.doubleValue();
			final Double discount = 		getNumber(o, "discount")	.doubleValue();
			final Double total_amount = 	getNumber(o, "amount")		.doubleValue();
			
			detail_list.add(new InvoiceDetailData(description, quantity,price, discount, total_amount));
		}	
		
		final TbaiEmisionInvoiceBuilder builder = new TbaiEmisionInvoiceBuilder();		
		final TbaiEmisionInvoice invoice = 
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
			.setTotal_amount		(total.doubleValue())
			.setSupported_retention	(null)
			.setTax_base_cost		(null)
			.setId_keys				(new ArrayList<>())
			.setBreakdown			(null)
			.build();
		
		return invoice;
	}
	
	//INVOICE TBAI EMISION
	public static void tbai_emision(final TbaiEmisionInvoice i,final String name) throws CannotCreateXMLException {
		try {
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
			jaxbMarshaller.marshal( tbai, os );
		} 
		catch (JAXBException | FileNotFoundException e) {throw new CannotCreateXMLException("ERROR WHILE ACCESSING DISK: Aborting...", e);} 
	}
	
	//INVOICE TBAI ANULATION
	public static void tbai_anulation(){/*TO DO uwu*/}

}

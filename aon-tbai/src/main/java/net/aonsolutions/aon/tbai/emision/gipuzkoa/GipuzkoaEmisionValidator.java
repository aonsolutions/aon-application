package net.aonsolutions.aon.tbai.emision.gipuzkoa;

import static net.aonsolutions.aon.tbai.emision._beans.validators.EntityValidator.validateCodigoPostal;
import static net.aonsolutions.aon.tbai.emision._beans.validators.EntityValidator.validateDireccion;
import static net.aonsolutions.aon.tbai.emision._beans.validators.EntityValidator.validateNIF;
import static net.aonsolutions.aon.tbai.emision._beans.validators.EntityValidator.validateNombre;
import static net.aonsolutions.aon.tbai.toolkit.DataToolkit.isEmpty;

import java.util.Date;
import java.util.List;

import net.aonsolutions.aon.tbai.emision.EmisionInvoice;
import net.aonsolutions.aon.tbai.exceptions.validation.ValidationException;
import ticketbai.emision.Cabecera;
import ticketbai.emision.CabeceraFacturaType;
import ticketbai.emision.ClavesType;
import ticketbai.emision.DatosFacturaType;
import ticketbai.emision.Destinatarios;
import ticketbai.emision.DetallesFacturaType;
import ticketbai.emision.Emisor;
import ticketbai.emision.Factura;
import ticketbai.emision.HuellaTBAI;
import ticketbai.emision.IDDestinatario;
import ticketbai.emision.IDDetalleFacturaType;
import ticketbai.emision.Sujetos;

public class GipuzkoaEmisionValidator {

	public static void validate(EmisionInvoice invoice) throws ValidationException{
		if(invoice == null) throw new ValidationException("Invoice not found");
		try {
			validateHeader    (invoice.getCabecera());
			validateSubjects  (invoice.getSujetos());
			validateInvoice	  (invoice.getFactura());
			validateTbaiPrint (invoice.getHuellaTbai());		
		}
		catch(Exception e) {
			if(e.getCause()   != null && e.getMessage() != null) 	
				throw new ValidationException(e.getMessage(), e.getCause());
			if(e.getCause()   != null) 								
				throw new ValidationException(e.getCause());
			if(e.getMessage() != null) 								
				throw new ValidationException(e.getMessage());	
			throw new ValidationException("Unexpected exception ocurred",e);
		}
		
	}
	
	private static void validateHeader(Cabecera cabecera) throws Exception{
		if(cabecera == null) 			throw new Exception("Header not found");
		
		String version = 				cabecera.getIDVersionTBAI();
		String pattern = 				"[0-9]+.[0-9]+";
		if(isEmpty(version)) 			throw new Exception("TBAI version not found");
		if(!version.matches(pattern))	throw new Exception("TBAI version in wrong format");
	}
	
	private static void validateSubjects(Sujetos sujetos) throws Exception{
		if(isEmpty(sujetos)) throw new Exception("Subjects not found");
		
		Emisor emisor = sujetos.getEmisor();
		validateEmisor(emisor);
		
		Destinatarios destinatarios = sujetos.getDestinatarios();
		validateDestinatarios(destinatarios);
	}

	private static void validateEmisor(Emisor emisor) throws Exception {
		if(isEmpty(emisor)) throw new Exception("Sender not found");
		
		String nif 	  =  emisor.getNIF();
		String nombre =  emisor.getApellidosNombreRazonSocial();
		
		validateNIF(nif,"Sender");
		validateNombre(nombre,"Sender");
	}


	private static void validateDestinatarios(Destinatarios destinatarios) throws Exception {
		if(isEmpty(destinatarios)) 						 throw new Exception("Receivers not found");
		if(destinatarios.getIDDestinatario().size() < 1) throw new Exception("Receivers not found");
		
		List<IDDestinatario> destinatarios_list = destinatarios.getIDDestinatario();
		for (IDDestinatario destinatario : destinatarios_list) {
			final String name = "Receiver";
			validateNIF				(destinatario.getNIF(),name);
			validateNombre			(destinatario.getApellidosNombreRazonSocial(),name);
			validateCodigoPostal	(destinatario.getCodigoPostal(),name);
			validateDireccion		(destinatario.getDireccion(),name);
		}
	}

	private static void validateInvoice(Factura factura)   throws Exception{
	
		if(isEmpty(factura)) throw new ValidationException("Invoice data not found");
		
		CabeceraFacturaType cabecera = factura.getCabeceraFactura();
		validateInvoiceCabecera(cabecera);
		
		DatosFacturaType datos_factura = factura.getDatosFactura();
		validateDatosFactura(datos_factura);
	}
	
	private static void validateInvoiceCabecera(CabeceraFacturaType cabecera) throws Exception {
		if(isEmpty(cabecera)) throw new Exception("Invoice header not found");
		
		String 	numero_factura 	 = cabecera.getNumFactura();
		String 	fecha_expedicion = cabecera.getFechaExpedicionFactura();
		String  hora_expedicion	 = cabecera.getHoraExpedicionFactura();
		
		if(isEmpty(numero_factura)) 	throw new Exception("Invoice number not found");
		if(isEmpty(fecha_expedicion)) 	throw new Exception("Invoice expedition date not found");
		if(isEmpty(hora_expedicion)) 	throw new Exception("Invoice expedition hour not found");
		
		//TODO[Rectificative]
	}	
	
	private static void validateDatosFactura(DatosFacturaType datos_factura) throws Exception {
		if(isEmpty(datos_factura)) throw new Exception("Invoice data not found");
		
		ClavesType 			claves 						= datos_factura.getClaves();
		String 				base_imponible_a_coste		= datos_factura.getBaseImponibleACoste();
		String 				descripcion 				= datos_factura.getDescripcionFactura();
		DetallesFacturaType detalles 					= datos_factura.getDetallesFactura();
		String 				fecha_operacion 			= datos_factura.getFechaOperacion();
		String 				importe_total 				= datos_factura.getImporteTotalFactura();
		String 				retencion_soportada 		= datos_factura.getRetencionSoportada();
		
		// validateClaves(claves);
		validateDetalles(detalles);
		
		if(isEmpty(descripcion))			 	throw new Exception("Invoice description not found");
		if(isEmpty(importe_total)) 				throw new Exception("Total amount not found");
	}
	
	private static void validateDetalles(DetallesFacturaType detalles) throws Exception {
		if(isEmpty(detalles)) throw new Exception("Invoice details not found");
		List<IDDetalleFacturaType> det_list = detalles.getIDDetalleFactura();
		if(det_list.size() == 0) throw new Exception("Invoice details not found");
		
		for (IDDetalleFacturaType detail : det_list) {
			if(isEmpty(detail)) throw new Exception("Detail not found in detail list");
			
			String cantidad = 			detail.getCantidad();
			String descripcion = 		detail.getDescripcionDetalle();
			String importe_unitario = 	detail.getImporteUnitario();
			String importe_total = 	  	detail.getImporteTotal();
			
			if(isEmpty(cantidad))			throw new Exception("Detail quantity not found");
			if(isEmpty(descripcion))		throw new Exception("Detail description not found");
			if(isEmpty(importe_unitario))	throw new Exception("Detail price not found");
			if(isEmpty(importe_total))		throw new Exception("Detail total_amount not found");
				
		}
		
	}

	private static void validateTbaiPrint(HuellaTBAI huellaTBAI) throws Exception{
		
		if(huellaTBAI == null) throw new ValidationException("TBAI print not found");
		
	}
}

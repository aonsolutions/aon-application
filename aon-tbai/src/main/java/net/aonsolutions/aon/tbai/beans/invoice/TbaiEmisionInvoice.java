package net.aonsolutions.aon.tbai.beans.invoice;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.type.Country;

import net.aonsolutions.aon.tbai.beans.invoice.parts.Entity;
import net.aonsolutions.aon.tbai.beans.invoice.parts.InvoiceDetails;
import net.aonsolutions.aon.tbai.beans.invoice.parts.RectificationData;
import ticketbai.emision.EntidadDesarrolladoraType;
import ticketbai.emision.Cabecera;
import ticketbai.emision.CabeceraFacturaType;
import ticketbai.emision.ClavesType;
import ticketbai.emision.DatosFacturaType;
import ticketbai.emision.Destinatarios;
import ticketbai.emision.DetallesFacturaType;
import ticketbai.emision.Emisor;
import ticketbai.emision.Factura;
import ticketbai.emision.FacturaRectificativaType;
import ticketbai.emision.HuellaTBAI;
import ticketbai.emision.IDDestinatario;
import ticketbai.emision.IDDetalleFacturaType;
import ticketbai.emision.IDOtro;
import ticketbai.emision.ImporteRectificacionSustitutivaType;
import ticketbai.emision.SiNoType;
import ticketbai.emision.SoftwareFacturacionType;
import ticketbai.emision.Sujetos;
import ticketbai.emision.TipoDesgloseType;

public class TbaiEmisionInvoice extends Invoice {
	
	private String 		versionTBAI;	
	private String 		device_number;
	private String 		software_name;
	private String 		software_version;
	private Entity 		software_developer;
	
	public TbaiEmisionInvoice() {}
	
	public Cabecera getCabecera() { 
		Cabecera c = new Cabecera();
		c.setIDVersionTBAI(versionTBAI);
		return c; 
	}

	public Sujetos getSujetos() {
		Sujetos entities = new Sujetos();
		
		if(this.getSender().isPresent()) {
			Emisor sender = new Emisor();
			
			Entity sen = 				this.getSender().get();
			Optional<String> name = 	sen.getName();
			Optional<String> nif = 		sen.getNif();
			
			if(name.isPresent())	sender.setApellidosNombreRazonSocial(name.get());
			if(nif.isPresent())     sender.setNIF(nif.get());
			
			entities.setEmisor(sender);			
		}		
		
		if(!this.getRecievers().isEmpty()) {
			Destinatarios receivers = new Destinatarios();
			
			List<Entity> Receivers = this.getRecievers();
			for (Entity e : Receivers) {
				IDDestinatario receiver_id = new IDDestinatario();
				
				Optional<String> name = e.getName();
				Optional<String> zip = e.getZip();
				Optional<String> address = e.getAddress();
				
				Optional<Country> country = e.getCountry();
				Optional<String> id = e.getId();
				Optional<String> id_type = e.getId_type();
				Optional<String> nif = e.getNif();
				
				if(name.isPresent()) 	receiver_id.setApellidosNombreRazonSocial(name.get());
				if(zip.isPresent())		receiver_id.setCodigoPostal(zip.get());
				if(address.isPresent())	receiver_id.setDireccion(address.get());
				
				IDOtro other_id = new IDOtro();
				if(country.isPresent()) other_id.setCodigoPais(null); //countrytype cast
				if(id.isPresent()) 		other_id.setID(id.get());
				if(id_type.isPresent())	other_id.setIDType(id_type.get());
				
				receiver_id.setIDOtro(other_id);
				if(nif.isPresent())	receiver_id.setNIF(nif.get());
				
				receivers.getIDDestinatario().add(receiver_id);				
			}
			
			entities.setDestinatarios(receivers);
		}		
		
		Optional<Boolean> external = this.isExternal(); 
		Optional<Boolean> multiple = this.isMultiple();
		
		if(external.isPresent()) entities.setEmitidaPorTercerosODestinatario(null); //EMITIDA POR TERCEROS IS A STRING !!!! :(
		if(multiple.isPresent()) entities.setVariosDestinatarios(booleanToSiNoType(multiple.get()));
		
		return entities;
	}

	public Factura	getFactura()  {
		Factura invoice = new Factura();
		
		CabeceraFacturaType invoice_header = new CabeceraFacturaType();
		
		Optional<Boolean> simplified = 			this.isSimplified();
		Optional<RectificationData> rect_data = this.getRectification();
		Optional<Date> expedition_date =		this.getExpedition_date(); 
		Optional<String> number = 				this.getNumber();
		Optional<String> series = 				this.getSeries();
		
		invoice_header.setFacturaEmitidaSustitucionSimplificada(null);
		
		if(rect_data.isPresent()) {
			FacturaRectificativaType rect = new FacturaRectificativaType();
			
			RectificationData rd = 					rect_data.get();
			Optional<String> rd_code = 				rd.getCode();
			Optional<String> rd_series = 			rd.getSeries();
			Optional<String> rd_type = 				rd.getType();
			Optional<Double> rd_im_tax_base = 		rd.getImport_tax_base();
			Optional<Double> rd_im_tax_quota = 		rd.getImport_tax_quote();
			Optional<Double> rd_im_rech_amount = 	rd.getImport_recharge_amount();
						
			ImporteRectificacionSustitutivaType imp = new ImporteRectificacionSustitutivaType();
			if(rd_im_tax_base.isPresent()) 		imp.setBaseRectificada(rd_im_tax_base.get() + "");
			if(rd_im_rech_amount.isPresent()) 	imp.setCuotaRecargoRectificada(rd_im_rech_amount.get() + "");
			if(rd_im_tax_quota.isPresent())		imp.setCuotaRectificada(rd_im_tax_quota.get() + "");
			
			if(rd_code.isPresent()) rect.setCodigo(null); 	//cast claveTipoFacturaType
			if(rd_type.isPresent()) rect.setTipo(null);		//cast claveTipoRectificativaType
			
			rect.setImporteRectificacionSustitutiva(imp);
			invoice_header.setFacturaRectificativa(rect);	
		}		
		
		if(simplified.isPresent())			invoice_header.setFacturaSimplificada(booleanToSiNoType(simplified.get()));
		invoice_header.setFacturasRectificadasSustituidas(null);
		
		if(expedition_date.isPresent()) 	{
			invoice_header.setFechaExpedicionFactura(null); 	//format expedition_date.get()	
			invoice_header.setHoraExpedicionFactura(null);		//format expedition_date.get()
		}
		if(number.isPresent())				invoice_header.setNumFactura(number.get());
		if(series.isPresent()) 				invoice_header.setSerieFactura(series.get());
		
		DatosFacturaType invoice_data = new DatosFacturaType();
		
		Optional<Double> tax_base_cost = 		this.getTax_base_cost();
		Optional<String> description = 			this.getDescription();
		Optional<Date> op_date = 				this.getOperation_date();
		Optional<Double> total_amount = 		this.getTotal_amount();
		Optional<Double> supported_retention = 	this.getSupported_retention();
		
		if(tax_base_cost.isPresent()) 	invoice_data.setBaseImponibleACoste(tax_base_cost.get() + "");
		ClavesType cl = new ClavesType();
		cl.getIDClave();
		
		//SET KEYS HERE :)
		
		invoice_data.setClaves(cl);
		
		if(description.isPresent()) 	invoice_data.setDescripcionFactura(description.get());

		List<InvoiceDetails> detail_list = this.getDetails();
		DetallesFacturaType details = new DetallesFacturaType();
		for (InvoiceDetails det : detail_list) {
			IDDetalleFacturaType id_det = new IDDetalleFacturaType();
			
			Optional<Double> d_amount = 		det.getDetail_amount();
			Optional<Double> d_discount = 		det.getDetail_discount();
			Optional<String> d_description =  	det.getDetail_description(); 
			Optional<Double> d_total_amount = 	det.getDetail_total_amount();
			Optional<Double> d_unit_amount = 	det.getDetail_unit_amount();
			
			if(d_amount.isPresent())			id_det.setCantidad(d_amount.get() +"");
			if(d_description.isPresent())		id_det.setDescripcionDetalle(d_description.get());
			if(d_discount.isPresent())			id_det.setDescuento(d_discount.get() + "");
			if(d_total_amount.isPresent())		id_det.setImporteTotal(d_total_amount.get() + "");
			if(d_unit_amount.isPresent())		id_det.setImporteUnitario(d_unit_amount.get() + "");
			
			details.getIDDetalleFactura().add(id_det);
		}
		invoice_data.setDetallesFactura(details);
		
		if(op_date.isPresent())				invoice_data.setFechaOperacion(null); //FORMAT op_date.get()
		if(total_amount.isPresent())		invoice_data.setImporteTotalFactura(total_amount.get() + "");
		if(supported_retention.isPresent())	invoice_data.setRetencionSoportada(supported_retention.get() + "");
				
		//BREAKDOWN TYPE
		TipoDesgloseType breakdown_type = new TipoDesgloseType();
		breakdown_type.setDesgloseFactura(null);
		breakdown_type.setDesgloseTipoOperacion(null);
				
		invoice.setCabeceraFactura(invoice_header);
		invoice.setDatosFactura(invoice_data);
		invoice.setTipoDesglose(breakdown_type);
		
		return invoice; 		
	}
	
	public HuellaTBAI getHuellaTbai() { 
		HuellaTBAI tbai_print = new HuellaTBAI();
		
		SoftwareFacturacionType s = new SoftwareFacturacionType();
		
		Optional<String> d_number = 		getDevice_number();
		Optional<String> sf_name = 			getSoftware_name();
		Optional<String> sf_version = 		getSoftware_version();
		Optional<Entity> sf_dev = 			getDeveloper_entity();
		
		EntidadDesarrolladoraType en = new EntidadDesarrolladoraType();
		
		if(sf_dev.isPresent()) 	{	
			
			Optional<String> nif = sf_dev.get().getNif();
			Optional<String> id = sf_dev.get().getId();
			Optional<String> id_type = sf_dev.get().getId_type();
			Optional<Country> country = sf_dev.get().getCountry();
			
			IDOtro id_other = new IDOtro();
			if(country.isPresent()) 	id_other.setCodigoPais(null); //cast from country to countryType2
			if(id.isPresent())			id_other.setID(id.get());
			if(id_type.isPresent())		id_other.setIDType(id_type.get());
			
			en.setIDOtro(id_other);
			if(nif.isPresent()) 		en.setNIF(nif.get());
		}
		s.setEntidadDesarrolladora(en);		
		
		if(d_number.isPresent()) 	s.setLicenciaTBAI(d_number.get());
		if(sf_name.isPresent())		s.setNombre(sf_name.get());
		if(sf_version.isPresent())	s.setVersion(sf_version.get());
		
		tbai_print.setNumSerieDispositivo(device_number);
		tbai_print.setSoftware(s);
		return tbai_print;
	}
	
	public Object getFirma() {return null;};	
	
	public SiNoType booleanToSiNoType(boolean b) {
		if(b) return SiNoType.S;
		else  return SiNoType.N;
	}

	public Optional<String> getVersionTBAI() {return Optional.ofNullable(versionTBAI);}
	public void setVersionTBAI(String versionTBAI) {this.versionTBAI = versionTBAI;}

	public Optional<String> getDevice_number() {return Optional.ofNullable(device_number);}
	public void setDevice_number(String device_number) {this.device_number = device_number;}

	public Optional<String> getSoftware_name() {return Optional.ofNullable(software_name);}
	public void setSoftware_name(String software_name) {this.software_name = software_name;}

	public Optional<String> getSoftware_version() {return Optional.ofNullable(software_version);}
	public void setSoftware_version(String software_version) {this.software_version = software_version;}

	public Optional<Entity> getDeveloper_entity() {return Optional.ofNullable(software_developer);}
	public void setDeveloper_entity(Entity developer_entity) {this.software_developer = developer_entity;}
	
}

package net.aonsolutions.aon.tbai.beans.invoice;

import static net.aonsolutions.aon.tbai.Toolkit.TbaiToolkit.emision_boolean_to_siNoType;
import static net.aonsolutions.aon.tbai.Toolkit.TbaiToolkit.emision_country_to_countryType2;
import static net.aonsolutions.aon.tbai.Toolkit.TbaiToolkit.format_date;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.type.Country;

import net.aonsolutions.aon.tbai.beans.invoice.enums.IDtype;
import net.aonsolutions.aon.tbai.beans.invoice.parts.Entity;
import net.aonsolutions.aon.tbai.beans.invoice.parts.InvoiceDetailData;
import net.aonsolutions.aon.tbai.beans.invoice.parts.RectificationData;
import net.aonsolutions.aon.tbai.beans.invoice.parts.breakdowns.Breakdown;
import net.aonsolutions.aon.tbai.beans.invoice.parts.breakdowns.Delivery;
import net.aonsolutions.aon.tbai.beans.invoice.parts.breakdowns.Exempted;
import net.aonsolutions.aon.tbai.beans.invoice.parts.breakdowns.National;
import net.aonsolutions.aon.tbai.beans.invoice.parts.breakdowns.NoExempted;
import net.aonsolutions.aon.tbai.beans.invoice.parts.breakdowns.NoNational;
import net.aonsolutions.aon.tbai.beans.invoice.parts.breakdowns.NoSubject;
import net.aonsolutions.aon.tbai.beans.invoice.parts.breakdowns.Service;
import net.aonsolutions.aon.tbai.beans.invoice.parts.breakdowns.Subject;
import net.aonsolutions.aon.tbai.exceptions.CannotCreateXMLException;
import ticketbai.emision.Cabecera;
import ticketbai.emision.CabeceraFacturaType;
import ticketbai.emision.CausaExencionType;
import ticketbai.emision.CausaNoSujetaType;
import ticketbai.emision.ClaveTipoFacturaType;
import ticketbai.emision.ClaveTipoRectificativaType;
import ticketbai.emision.ClavesType;
import ticketbai.emision.DatosFacturaType;
import ticketbai.emision.DesgloseFacturaType;
import ticketbai.emision.DesgloseIVAType;
import ticketbai.emision.DesgloseTipoOperacionType;
import ticketbai.emision.Destinatarios;
import ticketbai.emision.DetalleExentaType;
import ticketbai.emision.DetalleIVAType;
import ticketbai.emision.DetalleNoExentaType;
import ticketbai.emision.DetalleNoSujeta;
import ticketbai.emision.DetallesFacturaType;
import ticketbai.emision.Emisor;
import ticketbai.emision.EmitidaPorTercerosType;
import ticketbai.emision.EntidadDesarrolladoraType;
import ticketbai.emision.Entrega;
import ticketbai.emision.ExentaType;
import ticketbai.emision.Factura;
import ticketbai.emision.FacturaRectificativaType;
import ticketbai.emision.FacturasRectificadasSustituidasType;
import ticketbai.emision.HuellaTBAI;
import ticketbai.emision.IDClaveType;
import ticketbai.emision.IDDestinatario;
import ticketbai.emision.IDDetalleFacturaType;
import ticketbai.emision.IDFacturaRectificadaSustituidaType;
import ticketbai.emision.IDOtro;
import ticketbai.emision.ImporteRectificacionSustitutivaType;
import ticketbai.emision.NoExentaType;
import ticketbai.emision.NoSujetaType;
import ticketbai.emision.PrestacionServicios;
import ticketbai.emision.SoftwareFacturacionType;
import ticketbai.emision.SujetaType;
import ticketbai.emision.Sujetos;
import ticketbai.emision.TipoDesgloseType;
import ticketbai.emision.TipoOperacionSujetaNoExentaType;

public class TbaiEmisionInvoice extends Invoice {
	
	private final String versionTBAI;	
	private final String device_number;
	private final String software_name ;
	private final String software_version;
	private final Entity software_developer;
	
	public TbaiEmisionInvoice() {
		versionTBAI= 			"1.2";
		device_number = 		"TBAIGIPRE00000000131";
		software_name = 		"aonSolutions";
		software_version = 		"9.23";
		software_developer = 	new Entity("B01487271", "AON SOLUTIONS, SL", Country.ES, IDtype.NIF_IVA, "0","","c/Duque de Wellington 52-Bajo · 01010 VItoria-Gasteiz (ARABA)");
	}
	
	public Cabecera getCabecera() { 
		final Cabecera c = new Cabecera();
		c.setIDVersionTBAI(versionTBAI);
		return c; 
	}
	
	public Sujetos getSujetos() throws CannotCreateXMLException {
		try {
			
			final Sujetos entities = new Sujetos();
			if(this.getSender().isPresent()) {
				final Emisor sender = new Emisor();
				
				final Entity sen = 				this.getSender().get();
				final Optional<String> name = 	sen.getName();
				final Optional<String> nif = 	sen.getNif();
				
				if(name.isPresent())	sender.setApellidosNombreRazonSocial(name.get());
				if(nif.isPresent())     sender.setNIF(nif.get());
				
				entities.setEmisor(sender);			
			}		
			
			if(!this.getRecievers().isEmpty()) {
				final Destinatarios receivers = new Destinatarios();
				
				final List<Entity> Receivers = this.getRecievers();
				for (final Entity e : Receivers) {
					
					final IDDestinatario receiver_id = new IDDestinatario();
					final Optional<String> name = 		e.getName();
					final Optional<String> zip = 		e.getZip();
					final Optional<String> address = 	e.getAddress();
					final Optional<Country> country = 	e.getCountry();
					final Optional<String> id = 		e.getId();
					final Optional<IDtype> id_type = 	e.getId_type();
					final Optional<String> nif = 		e.getNif();
					
					if(name.isPresent()) 	receiver_id.setApellidosNombreRazonSocial(name.get());
					if(zip.isPresent())		receiver_id.setCodigoPostal(zip.get());
					if(address.isPresent())	receiver_id.setDireccion(address.get());
										
					final IDOtro other_id = new IDOtro();
					if(country.isPresent()) other_id.setCodigoPais(emision_country_to_countryType2(country.get())); 
					if(id.isPresent()) 		other_id.setID(id.get());
					
					if(id_type.isPresent()) {
						final IDClaveType clave_type = new IDClaveType();
						clave_type.setClaveRegimenIvaOpTrascendencia(id_type.get().getCode());	
						other_id.setIDType(clave_type.getClaveRegimenIvaOpTrascendencia());
					}
					
					receiver_id.setIDOtro(other_id);
					if(nif.isPresent())	receiver_id.setNIF(nif.get());
					
					receivers.getIDDestinatario().add(receiver_id);				
				}
				entities.setDestinatarios(receivers);
			}		
			
			final Optional<Boolean> multiple = this.isMultiple();
			entities.setEmitidaPorTercerosODestinatario(EmitidaPorTercerosType.T); 
			
			if(multiple.isPresent()) entities.setVariosDestinatarios(emision_boolean_to_siNoType(multiple.get()));
			return entities;
		}catch(Exception e) {throw new CannotCreateXMLException("ERROR: SUBJECT DATA CORRUPTED");}
	}

	public Factura	getFactura() throws CannotCreateXMLException {
		try {
			
			final Factura invoice = new Factura();
			final CabeceraFacturaType invoice_header = new CabeceraFacturaType();
			
			final Optional<Boolean> simplified = 			this.isSimplified();
			final Optional<RectificationData> rect_data = 	this.getRectification();
			final Optional<Date> expedition_date =			this.getExpedition_date(); 
			final Optional<String> number = 				this.getNumber();
			final Optional<String> series = 				this.getSeries();
			
			invoice_header.setFacturaEmitidaSustitucionSimplificada(null);
			setRectData(invoice_header,rect_data);
			
			if(simplified.isPresent())			invoice_header.setFacturaSimplificada(emision_boolean_to_siNoType(simplified.get()));
			if(number.isPresent())				invoice_header.setNumFactura(number.get());
			if(series.isPresent()) 				invoice_header.setSerieFactura(series.get());
			
			if(expedition_date.isPresent()) 	{
				invoice_header.setFechaExpedicionFactura(format_date(expedition_date.get(), "dd/MM/yyyy").orElseThrow()); 	
				invoice_header.setHoraExpedicionFactura(format_date(expedition_date.get(),  "hh:mm").orElseThrow());		
			}
			
			final DatosFacturaType invoice_data = 			new DatosFacturaType();
			final Optional<Double> tax_base_cost = 			this.getTax_base_cost();
			final Optional<String> description = 			this.getDescription();
			final Optional<Date> op_date = 					this.getOperation_date();
			final Optional<Double> total_amount = 			this.getTotal_amount();
			final Optional<Double> supported_retention = 	this.getSupported_retention();
			
			if(tax_base_cost.isPresent()) 		invoice_data.setBaseImponibleACoste(tax_base_cost.get() + "");
			if(description.isPresent()) 		invoice_data.setDescripcionFactura(description.get());
			if(op_date.isPresent())				invoice_data.setFechaOperacion(format_date(op_date.get(), "dd/MM/yyyy").orElseThrow()); 
			if(total_amount.isPresent())		invoice_data.setImporteTotalFactura(total_amount.get() + "");
			if(supported_retention.isPresent())	invoice_data.setRetencionSoportada(supported_retention.get() + "");
					
			set_id_keys(invoice_data);
			setInvoiceDetails(invoice_data);
			
			final TipoDesgloseType breakdown_type = new TipoDesgloseType();
			final Optional<Breakdown> breakdown = this.getBreakdown();
			
			if(breakdown.isPresent()) {
				final Breakdown b = breakdown.get();
						
				if(b instanceof National){	
					final DesgloseFacturaType factura = new DesgloseFacturaType();
				
					final Optional<Subject>   subject = 	((National) b).getSubject();
					final Optional<NoSubject> no_subject = 	((National) b).getNoSubject();
					
					if(subject.isPresent()) 	factura.setSujeta(getSubject(subject));
					if(no_subject.isPresent()) 	factura.setNoSujeta(getNosubject(no_subject));
					
					breakdown_type.setDesgloseFactura(factura);
				}
				else if(b instanceof NoNational){
					final DesgloseTipoOperacionType operacion = new DesgloseTipoOperacionType();
					final Optional <Service>  service  = ((NoNational) b).getService();
					final Optional <Delivery> delivery = ((NoNational) b).getDelivery();
					
					if(delivery.isPresent()) {
						final Entrega entrega = new Entrega();
						final Optional <Subject>   subject    = service.get().getSubject();
						final Optional <NoSubject> no_subject = service.get().getNoSubject();
						
						if(subject.isPresent()) entrega.setSujeta(getSubject(subject));						
						if(no_subject.isPresent()) entrega.setNoSujeta(getNosubject(no_subject));
						operacion.setEntrega(entrega);
					}
					if(service.isPresent()) {
						final PrestacionServicios p_servicios = new PrestacionServicios();
						final Optional <Subject>   subject    = service.get().getSubject();
						final Optional <NoSubject> no_subject = service.get().getNoSubject();
						
						if(subject.isPresent()) p_servicios.setSujeta(getSubject(subject));						
						if(no_subject.isPresent()) p_servicios.setNoSujeta(getNosubject(no_subject));
						operacion.setPrestacionServicios(p_servicios);
					}
					breakdown_type.setDesgloseTipoOperacion(operacion);
				}
			}	
			invoice.setCabeceraFactura(invoice_header);
			invoice.setDatosFactura(invoice_data);
			invoice.setTipoDesglose(breakdown_type);
			
			return invoice; 	
		}catch(Exception e) {throw new CannotCreateXMLException("ERROR: INVOICE DATA CORRUPTED",e);}
	}
	
	private NoSujetaType getNosubject(final Optional<NoSubject> no_subject) {
		final NoSujetaType    no_sujeta =	new NoSujetaType();
		final DetalleNoSujeta det = 		new DetalleNoSujeta();
		
		final Optional<CausaNoSujetaType> cause = 	no_subject.get().getDetails_cause();
		final Optional<Double> amount = 			no_subject.get().getDetails_amount();
		
		if(cause.isPresent()) 		det.setCausa(cause.get());
		if(amount.isPresent())  	det.setImporte(amount.get() + "");
		
		no_sujeta.getDetalleNoSujeta().add(det);
		return no_sujeta;
	}

	private SujetaType getSubject(final Optional<Subject> subject) {
		final SujetaType 		   sujeta = 	new SujetaType();
		final Optional<Exempted>   exenta = 	subject.get().getExempted();
		final Optional<NoExempted> no_exenta = 	subject.get().getNo_exempted();
		
		if(exenta.isPresent()) 		sujeta.setExenta(getExenta(exenta));
		if(no_exenta.isPresent()) 	sujeta.setNoExenta(getNoExenta(no_exenta));
		return sujeta;
	}

	private ExentaType getExenta(final Optional<Exempted> exenta) {
		final ExentaType ex = 		new ExentaType();
		final DetalleExentaType det = new DetalleExentaType();
		
		final Optional<CausaExencionType> cause = 	exenta.get().getCause();
		final Optional<Double> tax_base = 			exenta.get().getTax_base();
		
		if(cause.isPresent()) 		det.setCausaExencion(cause.get());
		if(tax_base.isPresent()) 	det.setBaseImponible(tax_base.get() + "");
		
		ex.getDetalleExenta().add(det);
		return ex;
	}
	
	private NoExentaType getNoExenta(final Optional<NoExempted> no_exenta) {
		final NoExentaType no_ex = 			new NoExentaType();
		final DetalleNoExentaType det = 	new DetalleNoExentaType();
		final DesgloseIVAType iva = 		new DesgloseIVAType();
		final DetalleIVAType iva_det = 		new DetalleIVAType();
		
		final Optional<Double>  tax_base = 						no_exenta.get().getTax_base();
		final Optional<Double>  tax_rate = 						no_exenta.get().getTax_rate();
		final Optional<Double>  tax_quote = 					no_exenta.get().getTax_quote();
		final Optional<Double>  equiv_rech_amount = 			no_exenta.get().getEquivalence_recharge_amount();
		final Optional<Boolean> equiv_rech_or_simpl = 			no_exenta.get().isEquivalence_recharge_or_simplified();
		final Optional<String>  equiv_type = 					no_exenta.get().getEquivalence_type();
		final Optional<TipoOperacionSujetaNoExentaType> type = 	no_exenta.get().getType();							
		
		if(tax_base.isPresent()) 				iva_det.setBaseImponible (tax_base.get()  + "");
		if(tax_rate.isPresent()) 				iva_det.setTipoImpositivo(tax_rate.get()  + "");
		if(tax_quote.isPresent()) 				iva_det.setCuotaImpuesto (tax_quote.get() + "");
		if(equiv_rech_amount.isPresent()) 		iva_det.setCuotaRecargoEquivalencia(equiv_rech_amount.get() + "");

		if(equiv_rech_or_simpl.isPresent())		iva_det.setOperacionEnRecargoDeEquivalenciaORegimenSimplificado(emision_boolean_to_siNoType(equiv_rech_or_simpl.get()));
		if(equiv_type.isPresent()) 				iva_det.setTipoRecargoEquivalencia(equiv_type.get());
		if(type.isPresent())					det.setTipoNoExenta(type.get());
		
		iva.getDetalleIVA().add(iva_det);
		det.setDesgloseIVA(iva);
		no_ex.getDetalleNoExenta().add(det);
		
		return no_ex;
	}

	private void setInvoiceDetails(final DatosFacturaType invoice_data) {
		final List<InvoiceDetailData> detail_list = this.getDetails();
		final DetallesFacturaType details = new DetallesFacturaType();
		for (final InvoiceDetailData det : detail_list) {
			final IDDetalleFacturaType id_det =			new IDDetalleFacturaType();
			final Optional<Double> d_price = 			det.getPrice();
			final Optional<Double> d_discount = 		det.getDiscount();
			final Optional<String> d_description =  	det.getDescription(); 
			final Optional<Double> d_total_amount = 	det.getTotalAmount();
			final Optional<Double> d_unit_amount = 		det.getUnitAmount();
			
			if(d_price.isPresent())				id_det.setCantidad(d_price.get() + "");
			if(d_description.isPresent())		id_det.setDescripcionDetalle(d_description.get());
			if(d_discount.isPresent())			id_det.setDescuento(d_discount.get() + "");
			if(d_total_amount.isPresent())		id_det.setImporteTotal(d_total_amount.get() + "");
			if(d_unit_amount.isPresent())		id_det.setImporteUnitario(d_unit_amount.get() + "");
			
			details.getIDDetalleFactura().add(id_det);
		}
		invoice_data.setDetallesFactura(details);
	}

	private void setRectData(final CabeceraFacturaType invoice_header, final Optional<RectificationData> rect_data) {
		if(rect_data.isPresent()) {
			final FacturaRectificativaType rect = 		new FacturaRectificativaType();
			final RectificationData rd = 				rect_data.get();
			final Optional<String> rd_code = 			rd.getCode();
			final Optional<String> rd_series = 			rd.getSeries();
			final Optional<String> rd_type = 			rd.getType();
			final Optional<Double> rd_im_tax_base = 	rd.getImport_tax_base();
			final Optional<Double> rd_im_tax_quota = 	rd.getImport_tax_quote();
			final Optional<Double> rd_im_rech_amount = 	rd.getImport_recharge_amount();
			
			final FacturasRectificadasSustituidasType fact_sus = new FacturasRectificadasSustituidasType();
			final IDFacturaRectificadaSustituidaType id = new IDFacturaRectificadaSustituidaType();
			id.setSerieFactura(rd_series.get());
			fact_sus.getIDFacturaRectificadaSustituida().add(id);
			invoice_header.setFacturasRectificadasSustituidas(fact_sus);
			
			final ImporteRectificacionSustitutivaType imp = new ImporteRectificacionSustitutivaType();
			if(rd_im_tax_base.isPresent()) 		imp.setBaseRectificada(rd_im_tax_base.get() + "");
			if(rd_im_rech_amount.isPresent()) 	imp.setCuotaRecargoRectificada(rd_im_rech_amount.get() + "");
			if(rd_im_tax_quota.isPresent())		imp.setCuotaRectificada(rd_im_tax_quota.get() + "");
			if(rd_code.isPresent()) 			rect.setCodigo(ClaveTipoFacturaType.fromValue(rd_code.get())); 		
			if(rd_type.isPresent()) 			rect.setTipo(ClaveTipoRectificativaType.fromValue(rd_type.get()));	
			
			rect.setImporteRectificacionSustitutiva(imp);
			invoice_header.setFacturaRectificativa(rect);	
		}		
	}
	
	private void set_id_keys(final DatosFacturaType invoice_data) {
		final ClavesType cl = new ClavesType();
		
		for (int i = 0; i < this.getId_keys().size(); i++) {;
			final IDClaveType id_clave = new IDClaveType();
			id_clave.setClaveRegimenIvaOpTrascendencia( this.getId_keys().get(i));
			cl.getIDClave().add(id_clave);
		}		
		invoice_data.setClaves(cl);
	}
	
	public HuellaTBAI getHuellaTbai() { 
		
		final HuellaTBAI tbai_print = 			new HuellaTBAI();
		final SoftwareFacturacionType s = 		new SoftwareFacturacionType();
		final Optional<String> d_number = 		getDevice_number();
		final Optional<String> sf_name = 		getSoftware_name();
		final Optional<String> sf_version = 	getSoftware_version();
		final Optional<Entity> sf_dev = 		getSoftware_developer();
		final EntidadDesarrolladoraType en = 	new EntidadDesarrolladoraType();
		
		if(sf_dev.isPresent()) 	{	
			
			final Optional<String> nif = 		sf_dev.get().getNif();
			final Optional<String> id = 		sf_dev.get().getId();
			final Optional<IDtype> id_type = 	sf_dev.get().getId_type();
			final Optional<Country> country = 	sf_dev.get().getCountry();
			
			final IDOtro id_other = 	new IDOtro();
			if(country.isPresent()) 	id_other.setCodigoPais(emision_country_to_countryType2(country.get()));
			if(id.isPresent())			id_other.setID(id.get());
			if(id_type.isPresent())		id_other.setIDType(id_type.get().getCode());
			if(nif.isPresent()) 		en.setNIF(nif.get());
			
			en.setIDOtro(id_other);	
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
		
	public Optional<String> getVersionTBAI() 		{return Optional.ofNullable(versionTBAI);}
	public Optional<String> getDevice_number() 		{return Optional.ofNullable(device_number);}
	public Optional<String> getSoftware_name() 		{return Optional.ofNullable(software_name);}
	public Optional<String> getSoftware_version() 	{return Optional.ofNullable(software_version);}
	public Optional<Entity> getSoftware_developer() {return Optional.ofNullable(software_developer);}

	public static class TbaiEmisionInvoiceBuilder{
				
		private Entity 	 				sender;
		private List<Entity>			recievers;
		private Boolean 				multiple; 								
		
		private String 	  				series;									
		private String 	  				number;									
		private Date	  				expedition_date;						
		private Boolean  				simplified;							
		private Boolean  				replace_simplified;
		
		private RectificationData 		rectification;						
		private String 					replaced_number;						
		private String   				replaced_expedition_date;	
		
		private Date 					operation_date;							
		private String 					description;							
		private List<InvoiceDetailData> 	details;
		
		private Double 					total_amount;							
		private Double 					supported_retention;					
		private Double 					tax_base_cost;							
		private List<String>			id_keys;								

		private Breakdown 				breakdown;
		private String 					signature; 			
		
		public TbaiEmisionInvoiceBuilder setSender(final Entity sender) 										{this.sender = sender; 										return this;}
		public TbaiEmisionInvoiceBuilder setRecievers(final List<Entity> recievers) 							{this.recievers = recievers;    							return this;}
		public TbaiEmisionInvoiceBuilder setMultiple(final Boolean multiple) 									{this.multiple = multiple;      							return this;}
		public TbaiEmisionInvoiceBuilder setSeries(final String series) 										{this.series = series;										return this;}
		public TbaiEmisionInvoiceBuilder setNumber(final String number) 										{this.number = number;										return this;}
		public TbaiEmisionInvoiceBuilder setExpedition_date(final Date expedition_date) 						{this.expedition_date = expedition_date;					return this;}
		public TbaiEmisionInvoiceBuilder setSimplified(final Boolean simplified) 								{this.simplified = simplified;								return this;}
		public TbaiEmisionInvoiceBuilder setReplace_simplified(final Boolean replace_simplified) 				{this.replace_simplified = replace_simplified;				return this;}
		public TbaiEmisionInvoiceBuilder setRectification(final RectificationData rectification) 				{this.rectification = rectification;						return this;}
		public TbaiEmisionInvoiceBuilder setReplaced_number(final String replaced_number) 						{this.replaced_number = replaced_number;					return this;}
		public TbaiEmisionInvoiceBuilder setReplaced_expedition_date(final String replaced_expedition_date) 	{this.replaced_expedition_date = replaced_expedition_date;	return this;}
		public TbaiEmisionInvoiceBuilder setOperation_date(final Date operation_date) 							{this.operation_date = operation_date;						return this;}
		public TbaiEmisionInvoiceBuilder setDescription(final String description) 								{this.description = description;							return this;}
		public TbaiEmisionInvoiceBuilder setDetails(final List<InvoiceDetailData> details) 						{this.details = details;									return this;}
		public TbaiEmisionInvoiceBuilder setTotal_amount(final Double total_amount) 							{this.total_amount = total_amount;							return this;}
		public TbaiEmisionInvoiceBuilder setSupported_retention(final Double supported_retention) 				{this.supported_retention = supported_retention;			return this;}
		public TbaiEmisionInvoiceBuilder setTax_base_cost(final Double tax_base_cost) 							{this.tax_base_cost = tax_base_cost;						return this;}
		public TbaiEmisionInvoiceBuilder setId_keys(final List<String> id_keys) 								{this.id_keys = id_keys;									return this;}
		public TbaiEmisionInvoiceBuilder setBreakdown(final Breakdown breakdown) 								{this.breakdown = breakdown;								return this;}
		public TbaiEmisionInvoiceBuilder setSignature(final String signature) 									{this.signature = signature;								return this;}	

		public TbaiEmisionInvoice build() {
			TbaiEmisionInvoice  i = new TbaiEmisionInvoice();
			
			i.setSender						(this.sender)
			.setRecievers					(this.recievers)
			.setMultiple					(this.multiple)
			.setSeries						(this.series)
			.setNumber						(this.number)
			.setExpedition_date				(this.expedition_date)
			.setSimplified					(this.simplified)
			.setReplace_simplified			(this.replace_simplified)
			.setRectification				(this.rectification)
			.setReplaced_number				(this.replaced_number)
			.setReplaced_expedition_date	(this.replaced_expedition_date)
			.setOperation_date				(this.operation_date)
			.setDescription					(this.description)
			.setDetails						(this.details)
			.setTotal_amount				(this.total_amount)
			.setSupported_retention			(this.supported_retention)
			.setTax_base_cost				(this.tax_base_cost)
			.setId_keys						(this.id_keys)
			.setBreakdown					(this.breakdown)
			.setSignature					(this.signature);
			
			return i;
		}
	}
}

package net.aonsolutions.aon.tbai.lroe;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.SiNoEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionFacturaConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesIngresosConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DatosOperacionType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DetalleRentaIngresosType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IngresoConSGCodificadoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IngresosConSGCodificadoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.ProvisionFondoSuplidoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.ProvisionesFondosSuplidosType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.RentaIngresosType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_altapeticion_v1_0_2.LROEPF140IngresosConFacturaConSGAltaPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_anulacionpeticion_v1_0_0.LROEPF140IngresosConFacturaConSGAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_6_provisionesysuplidos_altamodifpeticion_v1_0_0.LROEPF140ProvisionesFondosSuplidosAltaModifPeticion;
import net.aonsolutions.aon.tbai.LroeData;
import net.aonsolutions.aon.tbai.exceptions.http.StatusCodeException;
import net.aonsolutions.aon.tbai.responses.LROEResponse;

public class LROE140_6 extends LROE140 {

	private static final String CAPITULO = "1";
	private static final String SUBCAPITULO = "1.1";
	
	private LROEPF140IngresosConFacturaConSGAltaPeticion build(Person person, Invoice invoice,LROEInfo info, byte[] data) {
		LROEPF140ProvisionesFondosSuplidosAltaModifPeticion p = new LROEPF140ProvisionesFondosSuplidosAltaModifPeticion();
		p.setCabecera(buildCabecera(person, info));
		
		ProvisionesFondosSuplidosType suplidos = new ProvisionesFondosSuplidosType();
		ProvisionFondoSuplidoType suplido = new ProvisionFondoSuplidoType();
		DatosOperacionType datosOperacion = new DatosOperacionType();
		datosOperacion.setSerieFactura(invoice.getSeries());
		datosOperacion.setNumFactura(Integer.toString(invoice.getNumber()));
		Double total = invoice.getDetails().stream().filter(f -> f.isPrepayment()).mapToDouble(r -> r.getQuantity() * r.getPrice() -(r.getQuantity() * r.getPrice() * r.getDiscount() / 100)).sum();
		datosOperacion.setImporteTotal(AonNumberUtils.toString(AonMathUtils.round(total)));
		suplido.setDatosOperacion(datosOperacion);
		suplidos.getFondoSuplido().add(suplido);
		p.setFondosSuplidos(suplidos);
		
		LROEPF140IngresosConFacturaConSGAltaPeticion proba = new LROEPF140IngresosConFacturaConSGAltaPeticion();
		proba.setCabecera(buildCabecera(person, info));

		IngresosConSGCodificadoType ingresos = new IngresosConSGCodificadoType();
		IngresoConSGCodificadoType ingreso = new IngresoConSGCodificadoType();
		ingreso.setTicketBai(data);
		
		RentaIngresosType renta = new RentaIngresosType();
		DetalleRentaIngresosType detalleRenta = new DetalleRentaIngresosType();
		detalleRenta.setCriterioCobrosYPagos(invoice.isVatAccrualPayment() ? SiNoEnum.S : SiNoEnum.N);
		if(invoice.getEpigraph().equals("183320")) invoice.setEpigraph("183321");
		if(invoice.getEpigraph().equals("183310")) invoice.setEpigraph("183311");
		detalleRenta.setEpigrafe(invoice.getEpigraph());
		detalleRenta.setIngresoAComputarIRPFDiferenteBaseImpoIVA(SiNoEnum.N);
		//detalleRenta.setImporteIngresoIRPF();
		renta.getDetalleRenta().add(detalleRenta);
		ingreso.setRenta(renta);

		ingresos.getIngreso().add(ingreso);
		proba.setIngresos(ingresos);
		return proba;
	}
	
	public LROEResponse alta(TbaiConfiguration tbaiConfiguration, Person person, Invoice invoice, byte[] tbai) throws StatusCodeException {
		try {
			LROEInfo info = buildInfo(OperacionEnum.A_00, getEjercicio(tbaiConfiguration, invoice));
			final LROEPF140IngresosConFacturaConSGAltaPeticion p140 = build(person, invoice, info, tbai); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPF140IngresosConFacturaConSGAltaPeticion.class );
			final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p140, bos );
			byte[] xml = bos.toByteArray();
			DataRequest dataRequest = LroeData.saveRequest(person.getDomain(), new User().setLogin(""), invoice, info, xml);
			byte[] data = toGzip(xml);
			return send(tbaiConfiguration, buildJSON(person, info), data).setDataRequest(dataRequest);
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public LROEInfo buildInfo(OperacionEnum operacion, Integer ejercicio) {
		return new LROEInfo(MODEL_140, CAPITULO, SUBCAPITULO, operacion, ejercicio);
	}
	
	private LROEPF140IngresosConFacturaConSGAnulacionPeticion buildBaja(Person person, Invoice invoice, LROEInfo info, byte[] data) {	
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = new LROEPF140IngresosConFacturaConSGAnulacionPeticion();
		lroe.setCabecera(buildCabecera(person, info));
		AnulacionesIngresosConSGType anulaciones = new AnulacionesIngresosConSGType();
		
		AnulacionFacturaConSGType anulacion = new AnulacionFacturaConSGType();
		anulacion.setAnulacionTicketBai(data);
		anulaciones.getIngreso().add(anulacion);
		lroe.setIngresos(anulaciones);
		return lroe;
	}
	
	public LROEResponse anulacion(TbaiConfiguration tbaiConfiguration, Person person, Invoice invoice, byte[] tbai)  {
		try {
			LROEInfo info = buildInfo(OperacionEnum.AN_0, getEjercicio(tbaiConfiguration, invoice));
			final LROEPF140IngresosConFacturaConSGAnulacionPeticion p140 = buildBaja(person, invoice, info, tbai); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPF140IngresosConFacturaConSGAnulacionPeticion.class );
			final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p140, bos );
			byte[] xml = bos.toByteArray();
			DataRequest dataRequest = LroeData.saveRequest(person.getDomain(), new User().setLogin(""), invoice, info, xml);
			byte[] data = toGzip(xml);
			return send(tbaiConfiguration, buildJSON(person, info), data).setDataRequest(dataRequest);
		} catch (Exception e) {
			return error(e);
		}
	}
}

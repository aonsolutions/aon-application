package net.aonsolutions.aon.verifactu;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.Invoice;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CabeceraType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.EncadenamientoFacturaAnteriorType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PersonaFisicaJuridicaESType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SiNoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SistemaInformaticoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegistroFacturaType;
import net.aonsolutions.aon.verifactu.exceptions.VerifactuError;
import net.aonsolutions.aon.verifactu.exceptions.VerifactuException;

class Invoice2Verifactu {
	
	static final String VERSION  = "1.0";
	static final String SERVICE_DESCRIPTION = "Prestacion de servicios";
	static final String NO_SERVICE_DESCRIPTION = "Venta de mercaderias";
	
	private Invoice2Verifactu() {
	
	}
	
	static RegFactuSistemaFacturacion build(VerifactuContext vc) throws VerifactuException {
		RegFactuSistemaFacturacion regFactu = new RegFactuSistemaFacturacion();
		regFactu.setCabecera(getCabecera(vc));
		for (Invoice invoice : vc.getInvoices()) {
			RegistroFacturaType factura = getFactura(vc, invoice);
			regFactu.getRegistroFactura().add(factura);
			vc.setBlockchain( newBlockchain( factura ) );
		}
		return regFactu;
	}
	
	
	private static VerifactuBlockchain newBlockchain(RegistroFacturaType factura) throws VerifactuException {
		if (factura.getRegistroAlta() != null) {
			return Invoice2VerifactuAlta.newBlockchain( factura.getRegistroAlta() );
		}
		if (factura.getRegistroAnulacion() != null) {
			return Invoice2VerifactuAnulacion.newBlockchain( factura.getRegistroAnulacion() );
		}
		throw new VerifactuException( VerifactuError.AON_0009 ); 
	}

	private static CabeceraType getCabecera(VerifactuContext vc) { 
		final CabeceraType c = new CabeceraType();
		PersonaFisicaJuridicaESType obligado = new PersonaFisicaJuridicaESType();
		obligado.setNIF(vc.getCompany().getDocument());
		obligado.setNombreRazon(vc.getCompany().getName());
		c.setObligadoEmision(obligado);
		
		// REPRESENTANTE/ASESOR SI LO TUVIERA 
		// c.getRepresentante().setNIF(null);
		// c.getRepresentante().setNombreRazon(null);		
		
		// REMISION VOLUNTARIA
		// c.getRemisionVoluntaria().setFechaFinVeriFactu("");
		// c.getRemisionVoluntaria().setIncidencia(IncidenciaType.S);
		
		// REMISION REQUERIMIENTO
		// c.getRemisionRequerimiento().setFinRequerimiento(null);
		// c.getRemisionRequerimiento().setRefRequerimiento("");	
		
		return c; 
	}
	
	private static RegistroFacturaType getFactura(VerifactuContext vc, Invoice invoice) throws VerifactuException {
		RegistroFacturaType factura = new RegistroFacturaType();
		if (invoice.isAnnulled()) {
			factura.setRegistroAnulacion( Invoice2VerifactuAnulacion.get(vc, invoice) );
		} else {
			factura.setRegistroAlta( Invoice2VerifactuAlta.get(vc, invoice) );
		}
		return factura;
	}
	
	static EncadenamientoFacturaAnteriorType getEncadenamientoFacturaAnterior(VerifactuBlockchain blockchain) {
		EncadenamientoFacturaAnteriorType cadena = new EncadenamientoFacturaAnteriorType();
		cadena.setFechaExpedicionFactura(blockchain.getDate());
		cadena.setHuella(blockchain.getHuella());
		cadena.setIDEmisorFactura(blockchain.getDocument());
		cadena.setNumSerieFactura(blockchain.getReference());
		return cadena;
	}


	static XMLGregorianCalendar getXmlDate() throws VerifactuException {
		 try {
			 XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar( new GregorianCalendar());
			 xmlGregorianCalendar.setFractionalSecond(null);
			 return xmlGregorianCalendar;
		 } catch (DatatypeConfigurationException e) {
			 throw new VerifactuException( e );
		}
	}
	
	static SistemaInformaticoType getSistemaInformatico(Company company) {
		SistemaInformaticoType sys = new SistemaInformaticoType();
		sys.setNIF("B01487271");
		sys.setNombreRazon("AON SOLUTIONS SL");
		sys.setIdSistemaInformatico("01");
		sys.setNombreSistemaInformatico("aonSolutions");
		sys.setVersion("9.23");
		sys.setNumeroInstalacion(company.getDocument() + "-" + company.getDomain().getId()); // CONCATENA EL NIF Y DOMAIN ID.
		sys.setTipoUsoPosibleSoloVerifactu(SiNoType.N);
		sys.setTipoUsoPosibleMultiOT(SiNoType.S);
		sys.setIndicadorMultiplesOT(SiNoType.S);
		return sys;		
	}
	
}


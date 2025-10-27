package net.aonsolutions.aon.verifactu;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonObjectUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.IDFacturaExpedidaBajaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PrimerRegistroCadenaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RechazoPrevioAnulacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAnulacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAnulacionType.Encadenamiento;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SinRegistroPrevioType;

class Invoice2VerifactuAnulacion {
	
	private Invoice2VerifactuAnulacion() {
	
	}
	
	static RegistroFacturacionAnulacionType get(VerifactuContext vc, Invoice invoice) throws InvoiceCommunicationException {
		RegistroFacturacionAnulacionType anul = new RegistroFacturacionAnulacionType();
		anul.setIDVersion(Invoice2Verifactu.VERSION);
		IDFacturaExpedidaBajaType idFactura = new IDFacturaExpedidaBajaType();
		idFactura.setIDEmisorFacturaAnulada(vc.getCompany().getDocument());
		idFactura.setNumSerieFacturaAnulada(invoice.getReferenceCode());
		idFactura.setFechaExpedicionFacturaAnulada( VerifactuUtils.toString(invoice.getExpDate()) );
		anul.setIDFactura(idFactura);
		anul.setRefExterna(AonNumberUtils.toString(invoice.getId()));
		anul.setSinRegistroPrevio( SinRegistroPrevioType.N ); // hasRegistroPrevio( vc, invoice ) );
		anul.setRechazoPrevio(RechazoPrevioAnulacionType.N);
//		anul.setGeneradoPor(GeneradoPorType.E);
//		PersonaFisicaJuridicaType generador = new PersonaFisicaJuridicaType();
//		generador.setNombreRazon(vc.getCompany().getName());
//		generador.setNIF(vc.getCompany().getDocument());
//		anul.setGenerador(generador);
		anul.setEncadenamiento(getEncadenamientoAnulacion(vc.getBlockchain()));
		anul.setSistemaInformatico(Invoice2Verifactu.getSistemaInformatico(vc.getCompany()));
		anul.setFechaHoraHusoGenRegistro(Invoice2Verifactu.getXmlDate());
		anul.setTipoHuella("01");
		anul.setHuella(calculateAnulacionHuella(anul, vc.getBlockchain()));
		return anul;
	}
	
//	private static SinRegistroPrevioType hasRegistroPrevio(VerifactuContext vc, Invoice invoice) {
//		return invoice.optInfo()
//			.filter(i -> i.getType() == InvoiceCommunicationType.VERIFACTU)
//			.map( i -> SinRegistroPrevioType.N )
//			.orElse(SinRegistroPrevioType.S)	
//		;
//	}
	
	private static Encadenamiento getEncadenamientoAnulacion(VerifactuBlockchain blockchain) {
		Encadenamiento encadenamiento = new Encadenamiento();
		if(blockchain == null || blockchain.isEmpty()) 
			encadenamiento.setPrimerRegistro(PrimerRegistroCadenaType.S);
		else {
			encadenamiento.setRegistroAnterior(Invoice2Verifactu.getEncadenamientoFacturaAnterior(blockchain));
		}
		return encadenamiento;		
	}
	
	private static String calculateAnulacionHuella(RegistroFacturacionAnulacionType anul, VerifactuBlockchain previousBlockchain) {
		return new VerifactuAnulacionHuella()
			.setiDEmisorFacturaAnulada(anul.getIDFactura().getIDEmisorFacturaAnulada())
			.setNumSerieFacturaAnulada(anul.getIDFactura().getNumSerieFacturaAnulada())
			.setFechaExpedicionFacturaAnulada(anul.getIDFactura().getFechaExpedicionFacturaAnulada())
			.setPreviousHuella(AonStringUtils.defaultIfBlank(AonObjectUtils.ifNotNullGet(previousBlockchain, VerifactuBlockchain::getHuella )))
			.setFechaHoraHusoGenRegistro(anul.getFechaHoraHusoGenRegistro().toString())
			.digest();
	}

	public static VerifactuBlockchain newBlockchain(RegistroFacturacionAnulacionType registroAnulacion) {
		return new VerifactuBlockchain()
			.setDate(registroAnulacion.getIDFactura().getFechaExpedicionFacturaAnulada())
			.setDocument(registroAnulacion.getIDFactura().getIDEmisorFacturaAnulada())
			.setReference(registroAnulacion.getIDFactura().getNumSerieFacturaAnulada())
			.setHuella(registroAnulacion.getHuella());
	}
	
}


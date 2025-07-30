package net.aonsolutions.aon.verifactu;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonObjectUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.consultalr.ConsultaFactuSistemaFacturacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.consultalr.DatosAdicionalesRespuestaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.consultalr.LRFiltroRegFacturacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CabeceraConsultaSf;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.GeneradoPorType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.IDFacturaExpedidaBajaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ObligadoEmisionConsultaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PersonaFisicaJuridicaESType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PrimerRegistroCadenaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RechazoPrevioAnulacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAnulacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAnulacionType.Encadenamiento;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SinRegistroPrevioType;
import net.aonsolutions.aon.verifactu.exceptions.VerifactuException;

class Invoice2VerifactuConsulta {
	
	private Invoice2VerifactuConsulta() {
	
	}
	
	static ConsultaFactuSistemaFacturacionType get(VerifactuContext vc, Invoice invoice) throws VerifactuException {
		
		ConsultaFactuSistemaFacturacionType consulta = new ConsultaFactuSistemaFacturacionType();

		CabeceraConsultaSf cabecera = new CabeceraConsultaSf();
		cabecera.setIDVersion(Invoice2Verifactu.VERSION);
		cabecera.setDestinatario(null);
		
		ObligadoEmisionConsultaType obligado = new ObligadoEmisionConsultaType();
		obligado.setNIF(vc.getCompany().getDocument());
		obligado.setNombreRazon(vc.getCompany().getName());
		cabecera.setObligadoEmision(obligado);
		
		consulta.setCabecera(cabecera);
		
		DatosAdicionalesRespuestaType datos = new DatosAdicionalesRespuestaType();
		consulta.setDatosAdicionalesRespuesta(datos);
		
		LRFiltroRegFacturacionType filtro = new LRFiltroRegFacturacionType();
		consulta.setFiltroConsulta(filtro);
		
		return consulta;
	}

}


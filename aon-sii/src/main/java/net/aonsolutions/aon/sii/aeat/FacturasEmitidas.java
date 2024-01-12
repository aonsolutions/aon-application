package net.aonsolutions.aon.sii.aeat;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatData;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.RespuestaLRBajaFEmitidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.RespuestaLRCobrosEmitidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.RespuestaLRFEmitidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CabeceraSii;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CabeceraSiiBaja;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CabeceraSiiCobrosPagos;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CausaExencionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.ClaveTipoComunicacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.ClaveTipoFacturaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CobrosType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CountryType2;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CuponType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.DatosInmuebleType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.DatosPagoCobroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.DesgloseRectificacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.DetalleExentaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.DetalleIVAEmitidaPrestacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.DetalleIVAEmitidaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.EmitidaPorTercerosType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.FacturaExpedidaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.FacturaExpedidaType.DatosInmueble;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.FacturaExpedidaType.TipoDesglose;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.FacturaType.FacturasAgrupadas;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.FacturaType.FacturasRectificadas;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaARType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaExpedidaBCType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaExpedidaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaExpedidaType.IDEmisorFactura;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDOtroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.MacrodatoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.NoSujetaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.PersonaFisicaJuridicaESType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.PersonaFisicaJuridicaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaPrestacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaPrestacionType.Exenta;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaPrestacionType.NoExenta;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaPrestacionType.NoExenta.DesgloseIVA;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.TipoConDesgloseType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.TipoOperacionSujetaNoExentaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.TipoSinDesglosePrestacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.TipoSinDesgloseType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.VariosDestinatariosType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.BajaLRFacturasEmitidas;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRBajaExpedidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRCobrosEmitidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRfacturasEmitidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.SuministroLRCobrosEmitidas;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.SuministroLRFacturasEmitidas;
import net.aonsolutions.aon.sii.ClaveRegimenEspecialOTrascendenciaEmitidasType;
import net.aonsolutions.aon.sii.IDType;

public class FacturasEmitidas extends SIIBuilt {

	public static FacturasEmitidas getInstance() {
		return new FacturasEmitidas();
	}
	
	public FacturasEmitidas() {

	}
	
	// ------------------- FACTURAS EMITIDAS
	 
	public byte[] getSuministroFacturasEmitidas(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, Boolean mod, String terceros){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(SuministroLRFacturasEmitidas.class);
			b = writeXml(ctx, suministroFacturasEmitidas(domain, login, company, invoiceId, contextList, mod, terceros));
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	protected byte[] getSuministroFacturasEmitidas(SuministroLRFacturasEmitidas suministro){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(SuministroLRFacturasEmitidas.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	public byte[] getRespuestaSuministroFacturasEmitidas(RespuestaLRFEmitidasType suministro){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRFEmitidasType.class);
			QName qName = new QName("https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro", "RespuestaLRFEmitidasType");
		    JAXBElement<RespuestaLRFEmitidasType> root = new JAXBElement<>(qName, RespuestaLRFEmitidasType.class, suministro);
			b = writeXml(ctx, root);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	public byte[] getBajaFacturasEmitidas(BajaLRFacturasEmitidas suministro){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(BajaLRFacturasEmitidas.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	public byte[] getRespuestaBajaFacturasEmitidas(RespuestaLRBajaFEmitidasType suministro){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRBajaFEmitidasType.class);
			QName qName = new QName("https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro", "RespuestaLRBajaFEmitidasType");
		    JAXBElement<RespuestaLRBajaFEmitidasType> root = new JAXBElement<>(qName, RespuestaLRBajaFEmitidasType.class, suministro);
			b = writeXml(ctx, root);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	/**
	 * Libro de registro de Facturas expedidas.
	 * 
	 * @param company
	 * @param vatList
	 */
	protected SuministroLRFacturasEmitidas suministroFacturasEmitidas(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, Boolean mod, String terceros) {
    	SuministroLRFacturasEmitidas suministro = new SuministroLRFacturasEmitidas();

		// CABECERA
		suministro.setCabecera(cabecera(company, mod, terceros));
		
		// BODY

		VatContext vat = contextList.stream().filter(f -> f.getInvoice().equals(invoiceId)).findFirst().orElse(new VatContext());
		
		EnterpriseActivity activity = AON.getEnterpriseActivity(domain.getName(), domain.getId(), login, vat.getActivity());
		if(activity == null) {
			activity = AON.getEnterpriseActivities(domain.getName(), domain.getId(), login)
					.findFirst().orElse(new EnterpriseActivity());
		}
		boolean exempt = (activity.getVatRegime() != null && activity.getVatRegime().isExempt()) 
				|| vat.isIntracommunity() || vat.isExtracommunity() || vat.isCanCeuMel();
		
		Double exenta =  contextList.stream().filter(f -> f.getInvoice().equals(invoiceId) && exempt && f.getPercentage() == 0  && ! VatDeductionType.NON_TAXABLE.equals(f.getVatDeductionType()) && !f.isPrepayment()) 
				.mapToDouble(f -> f.getBase()).sum();
		Double noSujeta =  contextList.stream().filter(f -> f.getInvoice().equals(invoiceId) && (VatDeductionType.NON_TAXABLE.equals(f.getVatDeductionType()) || f.isPrepayment() ))
				.mapToDouble(f -> f.getBase()).sum();
		LinkedList<VatData> noExenta = contextList.stream().filter(f -> f.getInvoice().equals(invoiceId) && (!exempt || f.getPercentage() > 0)  && !VatDeductionType.NON_TAXABLE.equals(f.getVatDeductionType()) && !f.isPrepayment())
				.map(f -> new VatData().setBase(f.getBase())
						.setPercentage(f.getPercentage())
						.setQuota(f.getQuota())
						.setSurchargePercent(f.getSurchargePercent())
						.setSurchargeQuota(f.getSurchargeQuota()))
				.collect(Collectors.toCollection(LinkedList::new));
			
		LRfacturasEmitidasType factura = new LRfacturasEmitidasType();
		
		factura.setPeriodoLiquidacion(periodoLiquidacion(vat, false));
		IDFacturaExpedidaType idFactura = new IDFacturaExpedidaType();
		
		idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));
		//idFactura.setNumSerieFacturaEmisorResumenFin(vat.getReferenceCode()); // SI ES ASIENTO RESUMEN
		idFactura.setNumSerieFacturaEmisor(vat.getReferenceCode());
		IDEmisorFactura emisor = new IDEmisorFactura();
		
		emisor.setNIF(company.getDocument());
		idFactura.setIDEmisorFactura(emisor);
		factura.setIDFactura(idFactura);
		FacturaExpedidaType fet = new FacturaExpedidaType();
		
		fet.setTipoFactura(ClaveTipoFacturaType.F_1); // TODO  De momento a piñon fijo!!!
		if(vat.getRegistryDocument() == null || vat.getRegistryDocument().equals("")){
			fet.setTipoFactura(ClaveTipoFacturaType.F_2);
		}
		if(vat.isRectification()){
			fet.setTipoFactura(ClaveTipoFacturaType.R_1); // TODO R_1 || R_2 || R_3 || R_4 || R_4.  De momento a piñon fijo!!!
			fet.setTipoRectificativa("I"); //TODO  S (por sustitucion) || I (por diferencia). De momento a piñon fijo!!!
			// FACTURA RECTIFICADAS
			FacturasRectificadas fr = new FacturasRectificadas();
			Invoice rectificada = AON.getInvoice(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(vat.getRectificationInvoice()));
			// SOLO 1 RECTIFICADA PARA CADA RECTIFICATIVA! 
			IDFacturaARType a2 = new IDFacturaARType();
			a2.setFechaExpedicionFacturaEmisor(AonDateUtils.format(rectificada.getIssueDate(), "dd-MM-yyyy")); //TODO
			a2.setNumSerieFacturaEmisor(rectificada.getReferenceCode());
			fr.getIDFacturaRectificada().add(a2);
			fet.setFacturasRectificadas(fr);
			
			// IMPORTE RECTIFICACION 
			if("S".equalsIgnoreCase(fet.getTipoRectificativa())){
				DesgloseRectificacionType drt = new DesgloseRectificacionType();
				drt.setBaseRectificada(Double.toString(AonMathUtils.round(rectificada.getTaxableBase()))); 
// TODO			drt.setCuotaRecargoRectificado(Double.toString(vat.getRectified().getSurchargeQuota()));  
				drt.setCuotaRectificada(Double.toString(AonMathUtils.round(rectificada.getVatQuota()))); // TODO ¿?
				fet.setImporteRectificacion(drt);
			}
		}
		
		if(ClaveTipoFacturaType.F_3.equals(fet.getTipoFactura())){
			// TODO FACTURAS AGRUPADAS
			FacturasAgrupadas fa = new FacturasAgrupadas();

			// FOR
			IDFacturaARType a = new IDFacturaARType();
			a.setFechaExpedicionFacturaEmisor(""); 
			a.setNumSerieFacturaEmisor(""); 
			fa.getIDFacturaAgrupada().add(a);		
			fet.setFacturasAgrupadas(fa);
		}

		
		// CLAVE REGIMEN IVA || TRANSCENDENCIA  
	
		fet.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaEmitidasType._01.getName()); //TODO 
		
		// CLAVE REGIMEN IVA || TRANSCENDENCIA ADICIONAL 1			
		if(vat.isVatAccrualRegime()){
			fet.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaEmitidasType._07.getName());//TODO OPTIONAL
		}	
		if(vat.isExtracommunity() || vat.isCanCeuMel()){
			fet.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaEmitidasType._02.getName());
		}
		
		ApplicationParameter ap = AON.getApplicationParameter(domain.getName(), domain.getId(), login, AppParam.FS_MODEL_CFG_SII);
    	Boolean isRegistro = "R".equals(ap.getValue());
		Date opDate = isRegistro ? vat.getCreationDate() : vat.getTaxDate();
		if(opDate.compareTo(AonDateUtils.getDate(2017, 6, 1)) < 0){
//			fet.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaEmitidasType._16.getName());
		}
			
		// CLAVE REGIMEN IVA || TRANSCENDENCIA ADICIONAL 2
		//fet.setClaveRegimenEspecialOTrascendenciaAdicional2("");//TODO OPTIONAL

		// NUMERO REGISTRO AUTORIZACION
		fet.setNumRegistroAcuerdoFacturacion("");// TODO TIENE K DARLO EL CLIENTE
		
		// IMPORTE TOTAL 
		//Double total2 = contextList.stream().filter(g -> g.getInvoice().equals(invoice)).mapToDouble(g -> g.getBase() + g.getQuota()).sum();
		Double total = noSujeta + exenta + noExenta.stream().mapToDouble(f -> f.getBase() + f.getQuota() + f.getSurchargeQuota()).sum();
		fet.setImporteTotal(Double.toString(AonMathUtils.round(total)));
		fet.setMacrodato(total >= 100000000 ? MacrodatoType.S: MacrodatoType.N);
		// BASE IMPONIBLE A COSTE (OPTIONAL)
		if(fet.getClaveRegimenEspecialOTrascendencia().equals("06")
				|| (fet.getClaveRegimenEspecialOTrascendenciaAdicional1() != null && fet.getClaveRegimenEspecialOTrascendenciaAdicional1().equals("06"))
				|| (fet.getClaveRegimenEspecialOTrascendenciaAdicional2() != null && fet.getClaveRegimenEspecialOTrascendenciaAdicional2().equals("06"))){
			Double base = contextList.stream().filter(g -> g.getInvoice().equals(invoiceId)).mapToDouble(g -> g.getBase()).sum();
			fet.setBaseImponibleACoste(Double.toString(AonMathUtils.round(base)));
		}			
		// DESCRIPCION OPERACION 
		
		AccountingInvoice ai = ACCOUNTING.getAccountingInvoiceFromInvoice(domain.getName(), domain.getId(), login, invoiceId);
		String str = "";
		if(ai != null && ai.getAccountEntry() != null && ai.getAccountEntry().getDetails() != null){
			for(AccountEntryDetail aed : ai.getAccountEntry().getDetails()){
				Account a = ACCOUNTING.getAccount(domain.getName(), domain.getId(), login,  aed.getAccount());
				if(a.getCode().substring(0, 1).equals("6") ||  a.getCode().substring(0, 1).equals("7")) {
					str = str + a.getDescription() + "-";
				}
			}
		}
		
		str = str + vat.getDetailDescription().replaceAll("<", "").replaceAll(">", "");
		
		fet.setDescripcionOperacion(str.length() > 100 ? str.substring(0, 99) : str);
		
		// DATOS INMUEBLES (OPTIONAL) TODO // sii regimen IVA 12 - Operaciones de arrendamiento de local de negocio no sujetos a retención.
		if(fet.getClaveRegimenEspecialOTrascendencia().equals("12")
				|| (fet.getClaveRegimenEspecialOTrascendenciaAdicional1() != null && fet.getClaveRegimenEspecialOTrascendenciaAdicional1().equals("12"))
				|| (fet.getClaveRegimenEspecialOTrascendenciaAdicional2() != null && fet.getClaveRegimenEspecialOTrascendenciaAdicional2().equals("12"))){
			DatosInmueble datosInmueble = new DatosInmueble();
			DatosInmuebleType dit = new DatosInmuebleType();
			dit.setReferenciaCatastral("");//TODO
			dit.setSituacionInmueble("");//TODO
			datosInmueble.getDetalleInmueble().add(dit);
			fet.setDatosInmueble(datosInmueble);
			//Invoice i = new Invoice();
		}
		// IMPORTE TRANSMISION SUJETOS A IVA (OPTIONAL) ... importe TODO ¿?
		//fet.setImporteTransmisionSujetoAIVA("");//TODO
	
		// EMITIDA POR TERCEROS (OPTIONAL) ... default N
		fet.setEmitidaPorTercerosODestinatario(EmitidaPorTercerosType.N);//TODO
		
		// VARIOS DESTINATARIOS (OPTIONAL) ... default N
		fet.setVariosDestinatarios(VariosDestinatariosType.N);//TODO
		
		// CUPON (OPTIONAL) ... default N
		if(fet.getTipoFactura().equals(ClaveTipoFacturaType.R_1)
			|| fet.getTipoFactura().equals(ClaveTipoFacturaType.R_5)
			|| fet.getTipoFactura().equals(ClaveTipoFacturaType.F_4)){
			fet.setCupon(CuponType.N);//TODO
		}
		
		// CONTRAPARTE
		if(vat.getRegistryDocument() != null && !vat.getRegistryDocument().equals("")){
			if(vat.isIntracommunity()) {
				fet.setContraparte(contraparteIntracomunitario(vat));
			} else fet.setContraparte(contraparte(vat));
		}
		// TIPO DESGLOSE
		TipoDesglose tipoDesglose = new TipoDesglose(); 

		HashMap<Double, VatData> noExentaMap = new HashMap<>();
		noExenta.stream().forEach(r -> {
			if(noExentaMap.containsKey(r.getPercentage())){
				VatData vd = noExentaMap.get(r.getPercentage());
				noExentaMap.get(r.getPercentage()).setBase(vd.getBase() + r.getBase());
				noExentaMap.get(r.getPercentage()).setQuota(vd.getQuota() + r.getQuota());
				noExentaMap.get(r.getPercentage()).setSurchargeQuota(vd.getSurchargeQuota() + r.getSurchargeQuota());					
			} else noExentaMap.put(r.getPercentage(), r);
		});
		
		if(!fet.getTipoFactura().equals(ClaveTipoFacturaType.F_2) && !fet.getTipoFactura().equals(ClaveTipoFacturaType.F_4) 
			&& (vat.isService() || vat.isIntracommunity() || vat.isExtracommunity() || isNDocument(vat.getRegistryDocument()))){
			TipoConDesgloseType tcdt = new TipoConDesgloseType();
			
			if(vat.isService() && !vat.isIntracommunity()){
				TipoSinDesglosePrestacionType prestacion = new TipoSinDesglosePrestacionType();
				if(!noSujeta.equals(0.0)) {
					NoSujetaType nst3 = new NoSujetaType();
					nst3.setImportePorArticulos714Otros(Double.toString(AonMathUtils.round(noSujeta))); 
					//	nst3.setImporteTAIReglasLocalizacion(""); // TODO
					prestacion.setNoSujeta(nst3);
				}
				SujetaPrestacionType st3 = new SujetaPrestacionType();
				
				NoExenta noExenta3 = new NoExenta();
				DesgloseIVA diva3 = new DesgloseIVA();
				if(AonMathUtils.isNotZero(exenta)) {
					if(vat.isOtherISP()) {
						DetalleIVAEmitidaPrestacionType diet = new DetalleIVAEmitidaPrestacionType();
						diet.setBaseImponible(Double.toString(AonMathUtils.round(exenta))); 
						diet.setCuotaRepercutida("0"); 
						diet.setTipoImpositivo("0"); 
						diva3.getDetalleIVA().add(diet);


						noExenta3.setDesgloseIVA(diva3);
						noExenta3.setTipoNoExenta(TipoOperacionSujetaNoExentaType.S_2);		
						st3.setNoExenta(noExenta3);
					} else {
						Exenta exenta3 = new Exenta();
						DetalleExentaType detalleExenta = new DetalleExentaType();
						detalleExenta.setBaseImponible(Double.toString(AonMathUtils.round(exenta)));
						if(vat.isIntracommunity()){
							detalleExenta.setCausaExencion(CausaExencionType.E_5);
						}else if(vat.isExtracommunity() ||  vat.isCanCeuMel()){
							detalleExenta.setCausaExencion(CausaExencionType.E_2);
						} else {
							detalleExenta.setCausaExencion(CausaExencionType.E_6);
						}
						exenta3.getDetalleExenta().add(detalleExenta);
						st3.setExenta(exenta3);
					}
				}
				
				if(!noExenta.isEmpty()){
					noExentaMap.keySet().stream().forEach(key -> {
						DetalleIVAEmitidaPrestacionType diet = new DetalleIVAEmitidaPrestacionType();
						diet.setBaseImponible(Double.toString(AonMathUtils.round(noExentaMap.get(key).getBase()))); 
						diet.setCuotaRepercutida(Double.toString(AonMathUtils.round(noExentaMap.get(key).getQuota()))); 
						diet.setTipoImpositivo(Double.toString(AonMathUtils.round(key))); 
						diva3.getDetalleIVA().add(diet);
					});
					noExenta3.setDesgloseIVA(diva3);
					noExenta3.setTipoNoExenta(vat.isOtherISP() 
							? TipoOperacionSujetaNoExentaType.S_2
							: TipoOperacionSujetaNoExentaType.S_1);
					st3.setNoExenta(noExenta3);
				}
				if(st3.getExenta() != null || st3.getNoExenta() != null)
					prestacion.setSujeta(st3);
				tcdt.setPrestacionServicios(prestacion);
			} else {
				TipoSinDesgloseType entrega = new TipoSinDesgloseType();
				if(!noSujeta.equals(0.0)) {
					NoSujetaType nst2 = new NoSujetaType();
					if(vat.isIntracommunity() && vat.isOtherISP()){
						nst2.setImporteTAIReglasLocalizacion(Double.toString(AonMathUtils.round(noSujeta)));
					} else nst2.setImportePorArticulos714Otros(Double.toString(AonMathUtils.round(noSujeta))); // TODO
					// // TODO
					entrega.setNoSujeta(nst2);
				}
				SujetaType st2 = new SujetaType();
					
				https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.Exenta exenta2 = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.Exenta();
				DetalleExentaType detalleExenta = new DetalleExentaType();
				detalleExenta.setBaseImponible(Double.toString(AonMathUtils.round(exenta)));

				if(vat.isIntracommunity()){
					detalleExenta.setCausaExencion(CausaExencionType.E_5);
				}else if(vat.isExtracommunity() ||  vat.isCanCeuMel()){
					detalleExenta.setCausaExencion(CausaExencionType.E_2);
				} else {
					detalleExenta.setCausaExencion(CausaExencionType.E_6);
				}
				exenta2.getDetalleExenta().add(detalleExenta);
				st2.setExenta(exenta2); // TODO
				if(!noExenta.isEmpty()){
					https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta.DesgloseIVA diva2 = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta.DesgloseIVA();
					noExentaMap.keySet().stream().forEach(key -> {
						DetalleIVAEmitidaType diet = new DetalleIVAEmitidaType();
						diet.setBaseImponible(Double.toString(AonMathUtils.round(noExentaMap.get(key).getBase())));
						diet.setCuotaRepercutida(Double.toString(AonMathUtils.round(noExentaMap.get(key).getQuota())));
						diet.setTipoImpositivo(Double.toString(AonMathUtils.round(key)));
						if(noExentaMap.get(key).getSurchargePercent() > 0.0 && noExentaMap.get(key).getSurchargeQuota() > 0.0){
							diet.setTipoRecargoEquivalencia(Double.toString(AonMathUtils.round(noExentaMap.get(key).getSurchargeQuota())));
							diet.setCuotaRecargoEquivalencia(Double.toString(AonMathUtils.round(noExentaMap.get(key).getSurchargePercent())));
						}
						diva2.getDetalleIVA().add(diet);
					});

					https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta noExenta2 = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta();
					noExenta2.setDesgloseIVA(diva2);
					noExenta2.setTipoNoExenta(vat.isOtherISP()
							? TipoOperacionSujetaNoExentaType.S_2
							: TipoOperacionSujetaNoExentaType.S_1);
					st2.setNoExenta(noExenta2); // TODO
				}
				if(st2.getExenta() != null || st2.getNoExenta() != null)
					entrega.setSujeta(st2);
				tcdt.setEntrega(entrega);
			}				
			tipoDesglose.setDesgloseTipoOperacion(tcdt);
		} else {
			TipoSinDesgloseType tsdt = new TipoSinDesgloseType();
			if(!noSujeta.equals(0.0)) {
				NoSujetaType nst = new NoSujetaType();
				nst.setImportePorArticulos714Otros(Double.toString(AonMathUtils.round(noSujeta))); // TODO
				//	nst.setImporteTAIReglasLocalizacion(""); // TODO
				tsdt.setNoSujeta(nst);
			}
			SujetaType st = new SujetaType();
		
			https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta noExenta1 = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta();
			https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta.DesgloseIVA diva = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta.DesgloseIVA();
			if(AonMathUtils.isNotZero(exenta)) {
				if(vat.isOtherISP()) {
					DetalleIVAEmitidaType diet = new DetalleIVAEmitidaType();
					diet.setBaseImponible(Double.toString(AonMathUtils.round(exenta))); 
					diet.setCuotaRepercutida("0"); 
					diet.setTipoImpositivo("0"); 
					diva.getDetalleIVA().add(diet);

					noExenta1.setDesgloseIVA(diva);
					noExenta1.setTipoNoExenta(TipoOperacionSujetaNoExentaType.S_2);		
					st.setNoExenta(noExenta1);
				} else {
					https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.Exenta exenta1 = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.Exenta();
					DetalleExentaType detalleExenta = new DetalleExentaType();
					detalleExenta.setBaseImponible(Double.toString(AonMathUtils.round(exenta)));
					if(vat.isIntracommunity()){
						detalleExenta.setCausaExencion(CausaExencionType.E_5);
					}else if(vat.isExtracommunity() ||  vat.isCanCeuMel()){
						detalleExenta.setCausaExencion(CausaExencionType.E_2);
					} else {
						detalleExenta.setCausaExencion(CausaExencionType.E_6);
					}
					exenta1.getDetalleExenta().add(detalleExenta);
					st.setExenta(exenta1);
				}
			}			
			if(!noExenta.isEmpty()){				
				noExentaMap.keySet().stream().forEach(key -> {
					DetalleIVAEmitidaType diet = new DetalleIVAEmitidaType();
					diet.setBaseImponible(Double.toString(AonMathUtils.round(noExentaMap.get(key).getBase())));
					diet.setCuotaRepercutida(Double.toString(AonMathUtils.round(noExentaMap.get(key).getQuota())));
					diet.setTipoImpositivo(Double.toString(AonMathUtils.round(key)));
					if(noExentaMap.get(key).getSurchargePercent() > 0.0 && noExentaMap.get(key).getSurchargeQuota() > 0.0){
						diet.setTipoRecargoEquivalencia(Double.toString(AonMathUtils.round(noExentaMap.get(key).getSurchargePercent())));
						diet.setCuotaRecargoEquivalencia(Double.toString(AonMathUtils.round(noExentaMap.get(key).getSurchargeQuota())));
					}
					diva.getDetalleIVA().add(diet);
				});

				noExenta1.setDesgloseIVA(diva);

				noExenta1.setTipoNoExenta(vat.isOtherISP()
						? TipoOperacionSujetaNoExentaType.S_2
						: TipoOperacionSujetaNoExentaType.S_1);
				
				st.setNoExenta(noExenta1);
			}
			if(st.getExenta() != null || st.getNoExenta() != null)
				tsdt.setSujeta(st);
			tipoDesglose.setDesgloseFactura(tsdt);
		}
		fet.setTipoDesglose(tipoDesglose);
			
		factura.setFacturaExpedida(fet);
		suministro.getRegistroLRFacturasEmitidas().add(factura);
		return suministro;
	}
	
	/**
	 * Libro de registro de Facturas expedidas.
	 * 
	 * @param company
	 * @param vatList
	 */
	protected SuministroLRFacturasEmitidas suministroFacturasEmitidas(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList
			, Boolean mod, String terceros) {
    	SuministroLRFacturasEmitidas suministro = new SuministroLRFacturasEmitidas();

		// CABECERA
		suministro.setCabecera(cabecera(company, mod, terceros));
		
		// BODY
		invoiceList.stream().forEach(invoice -> {	
			VatContext vat = contextList.stream().filter(f -> f.getInvoice().equals(invoice)).findFirst().orElse(new VatContext());

			EnterpriseActivity activity = AON.getEnterpriseActivity(domain.getName(), domain.getId(), login, vat.getActivity());
			if(activity == null) {
				activity = AON.getEnterpriseActivities(domain.getName(), domain.getId(), login)
						.findFirst().orElse(new EnterpriseActivity());
			}
			boolean exempt = (activity.getVatRegime() != null && activity.getVatRegime().isExempt()) 
					|| vat.isIntracommunity() || vat.isExtracommunity() || vat.isCanCeuMel();
			
			Double exenta =  contextList.stream().filter(f -> f.getInvoice().equals(invoice) && exempt && f.getPercentage() == 0  && ! VatDeductionType.NON_TAXABLE.equals(f.getVatDeductionType()))
					.mapToDouble(f -> f.getBase()).sum();
			Double noSujeta =  contextList.stream().filter(f -> f.getInvoice().equals(invoice) && VatDeductionType.NON_TAXABLE.equals(f.getVatDeductionType()))
					.mapToDouble(f -> f.getBase()).sum();
			LinkedList<VatData> noExenta = contextList.stream().filter(f -> f.getInvoice().equals(invoice) &&( !exempt || f.getPercentage() > 0 ) && !VatDeductionType.NON_TAXABLE.equals(f.getVatDeductionType()))
					.map(f -> new VatData().setBase(f.getBase())
							.setPercentage(f.getPercentage())
							.setQuota(f.getQuota())
							.setSurchargePercent(f.getSurchargePercent())
							.setSurchargeQuota(f.getSurchargeQuota()))
					.collect(Collectors.toCollection(LinkedList::new));
			

			LRfacturasEmitidasType factura = new LRfacturasEmitidasType();
			
			factura.setPeriodoLiquidacion(periodoLiquidacion(vat, false));
			IDFacturaExpedidaType idFactura = new IDFacturaExpedidaType();
			
			idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));
			//idFactura.setNumSerieFacturaEmisorResumenFin(vat.getReferenceCode()); // SI ES ASIENTO RESUMEN
			idFactura.setNumSerieFacturaEmisor(vat.getReferenceCode());
			IDEmisorFactura emisor = new IDEmisorFactura();
			
			emisor.setNIF(company.getDocument());
			idFactura.setIDEmisorFactura(emisor);
			factura.setIDFactura(idFactura);
			FacturaExpedidaType fet = new FacturaExpedidaType();
			
			fet.setTipoFactura(ClaveTipoFacturaType.F_1); // TODO  De momento a piñon fijo!!!
			if(vat.getRegistryDocument() == null || vat.getRegistryDocument().equals("")){
				fet.setTipoFactura(ClaveTipoFacturaType.F_2);
			}

			if(vat.isRectification()){
				fet.setTipoFactura(ClaveTipoFacturaType.R_1); // TODO R_1 || R_2 || R_3 || R_4 || R_4.  De momento a piñon fijo!!!
				fet.setTipoRectificativa("I"); //TODO  S (por sustitucion) || I (por diferencia). De momento a piñon fijo!!!

				// FACTURA RECTIFICADAS
				FacturasRectificadas fr = new FacturasRectificadas();
				Invoice rectificada = AON.getInvoice(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(vat.getRectificationInvoice()));
				// SOLO 1 RECTIFICADA PARA CADA RECTIFICATIVA! 
				IDFacturaARType a2 = new IDFacturaARType();
				a2.setFechaExpedicionFacturaEmisor(AonDateUtils.format(rectificada.getIssueDate(), "dd-MM-yyyy")); //TODO
				a2.setNumSerieFacturaEmisor(rectificada.getReferenceCode());
				fr.getIDFacturaRectificada().add(a2);
				fet.setFacturasRectificadas(fr);
				
				// IMPORTE RECTIFICACION 
				if("S".equalsIgnoreCase(fet.getTipoRectificativa())){
					DesgloseRectificacionType drt = new DesgloseRectificacionType();
					drt.setBaseRectificada(Double.toString(AonMathUtils.round(rectificada.getTaxableBase()))); 
		// TODO			drt.setCuotaRecargoRectificado(Double.toString(vat.getRectified().getSurchargeQuota()));  
					drt.setCuotaRectificada(Double.toString(AonMathUtils.round(rectificada.getVatQuota()))); // TODO ¿?
					fet.setImporteRectificacion(drt);
				}
			}
			
			if(ClaveTipoFacturaType.F_3.equals(fet.getTipoFactura())){
				// TODO FACTURAS AGRUPADAS
				FacturasAgrupadas fa = new FacturasAgrupadas();

				// FOR
				IDFacturaARType a = new IDFacturaARType();
				a.setFechaExpedicionFacturaEmisor(""); 
				a.setNumSerieFacturaEmisor(""); 
				fa.getIDFacturaAgrupada().add(a);		
				fet.setFacturasAgrupadas(fa);
			}
		
			
			// CLAVE REGIMEN IVA || TRANSCENDENCIA  
		
			fet.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaEmitidasType._01.getName()); //TODO 
			
			// CLAVE REGIMEN IVA || TRANSCENDENCIA ADICIONAL 1			
			if(vat.isVatAccrualRegime()){
				fet.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaEmitidasType._07.getName());//TODO OPTIONAL
			}	
			if(vat.isExtracommunity()){
				fet.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaEmitidasType._02.getName());
			}
			
			ApplicationParameter ap = AON.getApplicationParameter(domain.getName(), domain.getId(), login, AppParam.FS_MODEL_CFG_SII);
	    	Boolean isRegistro = "R".equals(ap.getValue());
			Date opDate = isRegistro ? vat.getCreationDate() : vat.getTaxDate();
			if(opDate.compareTo(AonDateUtils.getDate(2017, 6, 1)) < 0){
	//			fet.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaEmitidasType._16.getName());
			}
			
			// CLAVE REGIMEN IVA || TRANSCENDENCIA ADICIONAL 2
			//fet.setClaveRegimenEspecialOTrascendenciaAdicional2("");//TODO OPTIONAL
	
			// NUMERO REGISTRO AUTORIZACION
			fet.setNumRegistroAcuerdoFacturacion("");// TODO TIENE K DARLO EL CLIENTE
			
			// IMPORTE TOTAL 
			//Double total2 = contextList.stream().filter(g -> g.getInvoice().equals(invoice)).mapToDouble(g -> g.getBase() + g.getQuota()).sum();
			Double total = noSujeta + exenta + noExenta.stream().mapToDouble(f -> f.getBase() + f.getQuota() + f.getSurchargeQuota()).sum();
			fet.setImporteTotal(Double.toString(AonMathUtils.round(total)));
			fet.setMacrodato(total >= 100000000 ? MacrodatoType.S: MacrodatoType.N);
			// BASE IMPONIBLE A COSTE (OPTIONAL)
			if(fet.getClaveRegimenEspecialOTrascendencia().equals("06")
					|| (fet.getClaveRegimenEspecialOTrascendenciaAdicional1() != null && fet.getClaveRegimenEspecialOTrascendenciaAdicional1().equals("06"))
					|| (fet.getClaveRegimenEspecialOTrascendenciaAdicional2() != null && fet.getClaveRegimenEspecialOTrascendenciaAdicional2().equals("06"))){
				Double base = contextList.stream().filter(g -> g.getInvoice().equals(invoice)).mapToDouble(g -> g.getBase()).sum();
				fet.setBaseImponibleACoste(Double.toString(AonMathUtils.round(base)));
			}			
			// DESCRIPCION OPERACION 
			
			AccountingInvoice ai = ACCOUNTING.getAccountingInvoiceFromInvoice(domain.getName(), domain.getId(), login, invoice);
			String str = "";
			if(ai != null && ai.getAccountEntry() != null && ai.getAccountEntry().getDetails() != null){
				for(AccountEntryDetail aed : ai.getAccountEntry().getDetails()){
					Account a = ACCOUNTING.getAccount(domain.getName(), domain.getId(), login,  aed.getAccount());
					if(a.getCode().substring(0, 1).equals("6") ||  a.getCode().substring(0, 1).equals("7")) {
						str = str + a.getDescription() + "-";
					}
				}
			}
			str = str + vat.getDetailDescription().replaceAll("<", "").replaceAll(">", "");
			
			fet.setDescripcionOperacion(str.length() > 100 ? str.substring(0, 99) : str);
			
			// DATOS INMUEBLES (OPTIONAL) TODO // sii regimen IVA 12 - Operaciones de arrendamiento de local de negocio no sujetos a retención.
			if(fet.getClaveRegimenEspecialOTrascendencia().equals("12")
					|| (fet.getClaveRegimenEspecialOTrascendenciaAdicional1() != null && fet.getClaveRegimenEspecialOTrascendenciaAdicional1().equals("12"))
					|| (fet.getClaveRegimenEspecialOTrascendenciaAdicional2() != null && fet.getClaveRegimenEspecialOTrascendenciaAdicional2().equals("12"))){
				DatosInmueble datosInmueble = new DatosInmueble();
				DatosInmuebleType dit = new DatosInmuebleType();
				dit.setReferenciaCatastral("");//TODO
				dit.setSituacionInmueble("");//TODO
				datosInmueble.getDetalleInmueble().add(dit);
				fet.setDatosInmueble(datosInmueble);
				//Invoice i = new Invoice();
			}
			// IMPORTE TRANSMISION SUJETOS A IVA (OPTIONAL) ... importe TODO ¿?
			//fet.setImporteTransmisionSujetoAIVA("");//TODO
		
			// EMITIDA POR TERCEROS (OPTIONAL) ... default N
			fet.setEmitidaPorTercerosODestinatario(EmitidaPorTercerosType.N);//TODO
			
			// VARIOS DESTINATARIOS (OPTIONAL) ... default N
			fet.setVariosDestinatarios(VariosDestinatariosType.N);//TODO
			
			// CUPON (OPTIONAL) ... default N
			if(fet.getTipoFactura().equals(ClaveTipoFacturaType.R_1)
				|| fet.getTipoFactura().equals(ClaveTipoFacturaType.R_5)
				|| fet.getTipoFactura().equals(ClaveTipoFacturaType.F_4)){
				fet.setCupon(CuponType.N);//TODO
			}
			
			// CONTRAPARTE
			if(vat.getRegistryDocument() != null && !vat.getRegistryDocument().equals("")){
				if(vat.isIntracommunity()) {
					fet.setContraparte(contraparteIntracomunitario(vat));
				} else fet.setContraparte(contraparte(vat));
			}
			// TIPO DESGLOSE
			TipoDesglose tipoDesglose = new TipoDesglose(); 

			HashMap<Double, VatData> noExentaMap = new HashMap<>();
			noExenta.stream().forEach(r -> {
				if(noExentaMap.containsKey(r.getPercentage())){
					VatData vd = noExentaMap.get(r.getPercentage());
					noExentaMap.get(r.getPercentage()).setBase(vd.getBase() + r.getBase());
					noExentaMap.get(r.getPercentage()).setQuota(vd.getQuota() + r.getQuota());
					noExentaMap.get(r.getPercentage()).setSurchargeQuota(vd.getSurchargeQuota() + r.getSurchargeQuota());					
				} else noExentaMap.put(r.getPercentage(), r);
			});
			
			if(!fet.getTipoFactura().equals(ClaveTipoFacturaType.F_2) && !fet.getTipoFactura().equals(ClaveTipoFacturaType.F_4) 
				&& (vat.isService() || vat.isIntracommunity() || vat.isExtracommunity() || isNDocument(vat.getRegistryDocument()))){
				TipoConDesgloseType tcdt = new TipoConDesgloseType();
				
				if(vat.isService() && !vat.isIntracommunity()){
					TipoSinDesglosePrestacionType prestacion = new TipoSinDesglosePrestacionType();
					if(!noSujeta.equals(0.0)) {
						NoSujetaType nst3 = new NoSujetaType();
						nst3.setImportePorArticulos714Otros(Double.toString(AonMathUtils.round(noSujeta))); 
						//	nst3.setImporteTAIReglasLocalizacion(""); // TODO
						prestacion.setNoSujeta(nst3);
					}
					SujetaPrestacionType st3 = new SujetaPrestacionType();
					
					NoExenta noExenta3 = new NoExenta();
					DesgloseIVA diva3 = new DesgloseIVA();
					if(AonMathUtils.isNotZero(exenta)) {
						if(vat.isOtherISP()) {
							DetalleIVAEmitidaPrestacionType diet = new DetalleIVAEmitidaPrestacionType();
							diet.setBaseImponible(Double.toString(AonMathUtils.round(exenta))); 
							diet.setCuotaRepercutida("0"); 
							diet.setTipoImpositivo("0"); 
							diva3.getDetalleIVA().add(diet);


							noExenta3.setDesgloseIVA(diva3);
							noExenta3.setTipoNoExenta(TipoOperacionSujetaNoExentaType.S_2);		
							st3.setNoExenta(noExenta3);
						} else {
							Exenta exenta3 = new Exenta();
							DetalleExentaType detalleExenta = new DetalleExentaType();
							detalleExenta.setBaseImponible(Double.toString(AonMathUtils.round(exenta)));
							if(vat.isIntracommunity()){
								detalleExenta.setCausaExencion(CausaExencionType.E_5);
							}else if(vat.isExtracommunity() ||  vat.isCanCeuMel()){
								detalleExenta.setCausaExencion(CausaExencionType.E_2);
							} else {
								detalleExenta.setCausaExencion(CausaExencionType.E_6);
							}							
							exenta3.getDetalleExenta().add(detalleExenta);
							st3.setExenta(exenta3);
						}
					}
					if(!noExenta.isEmpty()){
						noExentaMap.keySet().stream().forEach(key -> {
							DetalleIVAEmitidaPrestacionType diet = new DetalleIVAEmitidaPrestacionType();
							diet.setBaseImponible(Double.toString(AonMathUtils.round(noExentaMap.get(key).getBase()))); 
							diet.setCuotaRepercutida(Double.toString(AonMathUtils.round(noExentaMap.get(key).getQuota()))); 
							diet.setTipoImpositivo(Double.toString(AonMathUtils.round(key))); 
							diva3.getDetalleIVA().add(diet);
						});
						noExenta3.setDesgloseIVA(diva3);
						noExenta3.setTipoNoExenta(vat.isOtherISP() 
								? TipoOperacionSujetaNoExentaType.S_2
								: TipoOperacionSujetaNoExentaType.S_1);
						st3.setNoExenta(noExenta3);
					}
					if(st3.getExenta() != null || st3.getNoExenta() != null)
						prestacion.setSujeta(st3);		
					tcdt.setPrestacionServicios(prestacion);
				} else {
					TipoSinDesgloseType entrega = new TipoSinDesgloseType();
					if(!noSujeta.equals(0.0)) {
						NoSujetaType nst2 = new NoSujetaType();
						if(vat.isIntracommunity() && vat.isOtherISP()){
							nst2.setImporteTAIReglasLocalizacion(Double.toString(AonMathUtils.round(noSujeta)));
						} else nst2.setImportePorArticulos714Otros(Double.toString(AonMathUtils.round(noSujeta))); // TODO
						// // TODO
						entrega.setNoSujeta(nst2);
					}
					SujetaType st2 = new SujetaType();
					
					https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.Exenta exenta2 = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.Exenta();
					DetalleExentaType detalleExenta = new DetalleExentaType();
					detalleExenta.setBaseImponible(Double.toString(AonMathUtils.round(exenta)));					
					
					if(vat.isIntracommunity()){
						detalleExenta.setCausaExencion(CausaExencionType.E_5);
					}else if(vat.isExtracommunity()){
						detalleExenta.setCausaExencion(CausaExencionType.E_2);
					} else {
						detalleExenta.setCausaExencion(CausaExencionType.E_6);
					}
					exenta2.getDetalleExenta().add(detalleExenta);
					st2.setExenta(exenta2); // TODO
					if(!noExenta.isEmpty()){
						https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta.DesgloseIVA diva2 = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta.DesgloseIVA();
						noExentaMap.keySet().stream().forEach(key -> {
							DetalleIVAEmitidaType diet = new DetalleIVAEmitidaType();
							diet.setBaseImponible(Double.toString(AonMathUtils.round(noExentaMap.get(key).getBase())));
							diet.setCuotaRepercutida(Double.toString(AonMathUtils.round(noExentaMap.get(key).getQuota())));
							diet.setTipoImpositivo(Double.toString(AonMathUtils.round(key)));
							if(noExentaMap.get(key).getSurchargePercent() > 0.0 && noExentaMap.get(key).getSurchargeQuota() > 0.0){
								diet.setTipoRecargoEquivalencia(Double.toString(AonMathUtils.round(noExentaMap.get(key).getSurchargeQuota())));
								diet.setCuotaRecargoEquivalencia(Double.toString(AonMathUtils.round(noExentaMap.get(key).getSurchargePercent())));
							}
							diva2.getDetalleIVA().add(diet);
						});

						https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta noExenta2 = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta();
						noExenta2.setDesgloseIVA(diva2);
						noExenta2.setTipoNoExenta(vat.isOtherISP()
								? TipoOperacionSujetaNoExentaType.S_2
								: TipoOperacionSujetaNoExentaType.S_1);
						st2.setNoExenta(noExenta2); // TODO
					}
					if(st2.getExenta() != null || st2.getNoExenta() != null)
						entrega.setSujeta(st2);
					tcdt.setEntrega(entrega);
				}				
				tipoDesglose.setDesgloseTipoOperacion(tcdt);
			} else {
				TipoSinDesgloseType tsdt = new TipoSinDesgloseType();
				if(!noSujeta.equals(0.0)) {
					NoSujetaType nst = new NoSujetaType();
					nst.setImportePorArticulos714Otros(Double.toString(AonMathUtils.round(noSujeta))); // TODO
					//	nst.setImporteTAIReglasLocalizacion(""); // TODO
					tsdt.setNoSujeta(nst);
				}
				SujetaType st = new SujetaType();
			
				https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta noExenta1 = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta();
				https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta.DesgloseIVA diva = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta.DesgloseIVA();
				if(AonMathUtils.isNotZero(exenta)) {
					if(vat.isOtherISP()) {
						DetalleIVAEmitidaType diet = new DetalleIVAEmitidaType();
						diet.setBaseImponible(Double.toString(AonMathUtils.round(exenta))); 
						diet.setCuotaRepercutida("0"); 
						diet.setTipoImpositivo("0"); 
						diva.getDetalleIVA().add(diet);

						noExenta1.setDesgloseIVA(diva);
						noExenta1.setTipoNoExenta(TipoOperacionSujetaNoExentaType.S_2);		
						st.setNoExenta(noExenta1);
					} else {
						https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.Exenta exenta1 = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.Exenta();
						DetalleExentaType detalleExenta = new DetalleExentaType();
						detalleExenta.setBaseImponible(Double.toString(AonMathUtils.round(exenta)));
						if(vat.isIntracommunity()){
							detalleExenta.setCausaExencion(CausaExencionType.E_5);
						}else if(vat.isExtracommunity() ||  vat.isCanCeuMel()){
							detalleExenta.setCausaExencion(CausaExencionType.E_2);
						} else {
							detalleExenta.setCausaExencion(CausaExencionType.E_6);
						}
						exenta1.getDetalleExenta().add(detalleExenta);
						st.setExenta(exenta1);
					}
				}
				
				if(!noExenta.isEmpty()){
					noExentaMap.keySet().stream().forEach(key -> {
						DetalleIVAEmitidaType diet = new DetalleIVAEmitidaType();
						diet.setBaseImponible(Double.toString(AonMathUtils.round(noExentaMap.get(key).getBase())));
						diet.setCuotaRepercutida(Double.toString(AonMathUtils.round(noExentaMap.get(key).getQuota())));
						diet.setTipoImpositivo(Double.toString(AonMathUtils.round(key)));
						if(noExentaMap.get(key).getSurchargePercent() > 0.0 && noExentaMap.get(key).getSurchargeQuota() > 0.0){
							diet.setTipoRecargoEquivalencia(Double.toString(AonMathUtils.round(noExentaMap.get(key).getSurchargePercent())));
							diet.setCuotaRecargoEquivalencia(Double.toString(AonMathUtils.round(noExentaMap.get(key).getSurchargeQuota())));
						}
						diva.getDetalleIVA().add(diet);
					});

					noExenta1.setDesgloseIVA(diva);
					noExenta1.setTipoNoExenta(vat.isOtherISP() 
						? TipoOperacionSujetaNoExentaType.S_2
						: TipoOperacionSujetaNoExentaType.S_1); 
					st.setNoExenta(noExenta1);
				}
				if(st.getExenta() != null || st.getNoExenta() != null)
					tsdt.setSujeta(st);
				tipoDesglose.setDesgloseFactura(tsdt);
			}
			fet.setTipoDesglose(tipoDesglose);
			
			factura.setFacturaExpedida(fet);
			suministro.getRegistroLRFacturasEmitidas().add(factura);
		});
		return suministro;
	}
	
	
	public BajaLRFacturasEmitidas bajaFacturasEmitidas(Company company, Integer invoiceId, LinkedList<VatContext> vatList, String terceros) {
		BajaLRFacturasEmitidas baja = new BajaLRFacturasEmitidas();
		baja.setCabecera(cabeceraBaja(company, terceros));
		
		VatContext vat = vatList.stream().filter(f -> f.getInvoice().equals(invoiceId)).findFirst().orElse(new VatContext());
		LRBajaExpedidasType factura = new LRBajaExpedidasType();
			
		IDFacturaExpedidaBCType idFactura = new IDFacturaExpedidaBCType();
		idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));
		idFactura.setNumSerieFacturaEmisor(vat.getReferenceCode());
		https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaExpedidaBCType.IDEmisorFactura emisor = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaExpedidaBCType.IDEmisorFactura();
		emisor.setNIF(company.getDocument());
		idFactura.setIDEmisorFactura(emisor);
		factura.setIDFactura(idFactura);
			
		factura.setPeriodoLiquidacion(periodoLiquidacion(vat, false));
		
		baja.getRegistroLRBajaExpedidas().add(factura);
		return baja;
	}
	
	// ------------------- SUMINISTRO FACTURAS EMITIDAS COBROS
	
	public byte[] getSuministroFacturasEmitidasCobros(SuministroLRCobrosEmitidas suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(SuministroLRCobrosEmitidas.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	public byte[] getRespuestaSuministroFacturasEmitidasCobros(RespuestaLRCobrosEmitidasType suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRCobrosEmitidasType.class);
			QName qName = new QName("https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro", "RespuestaLRCobrosEmitidasType");
		    JAXBElement<RespuestaLRCobrosEmitidasType> root = new JAXBElement<>(qName, RespuestaLRCobrosEmitidasType.class, suministro);
			b = writeXml(ctx, root);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	/**
	 * Libro de registro de Facturas expedidas, COBROS.
	 *  
	 * @param company
	 */
	public SuministroLRCobrosEmitidas suministroFacturasEmitidasCobros(Domain domain, String login, Company company, LinkedList<Finance> financeList, Integer invoiceId) {
		SuministroLRCobrosEmitidas suministro = new SuministroLRCobrosEmitidas();
		
		suministro.setCabecera(cabeceraCobrosPagos(company));

		LRCobrosEmitidasType cobros = new LRCobrosEmitidasType();
		CobrosType ct = new CobrosType();
		financeList.stream().filter(f -> f.getInvoice().getId().equals(invoiceId)).forEach(f -> {
			DatosPagoCobroType dpct = new DatosPagoCobroType();
			dpct.setFecha(AonDateUtils.format(f.getDueDate(), "dd-MM-yyyy"));
			dpct.setImporte(Double.toString(AonMathUtils.round(f.getAmount())));
			if(f.getPayMethodType().equals(PayMethodType.BANK_TRANSFER)){
				dpct.setMedio("01");
			} else if(f.getPayMethodType().equals(PayMethodType.CHEQUE)){
				dpct.setMedio("02");
			} else dpct.setMedio("04");
			ct.getCobro().add(dpct);
		});
			
		Invoice invoice = financeList.stream().filter(f -> f.getInvoice().getId().equals(invoiceId)).findFirst().get().getInvoice();
		cobros.setCobros(ct);
		IDFacturaExpedidaBCType f = new IDFacturaExpedidaBCType();
		f.setFechaExpedicionFacturaEmisor(AonDateUtils.format(invoice.getIssueDate(), "dd-MM-yyyy"));
		https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaExpedidaBCType.IDEmisorFactura emisor = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaExpedidaBCType.IDEmisorFactura();
		emisor.setNIF(company.getDocument());
		f.setIDEmisorFactura(emisor);
		f.setNumSerieFacturaEmisor(invoice.getReferenceCode());
		cobros.setIDFactura(f);
		
		suministro.getRegistroLRCobros().add(cobros);

		return suministro;
	}
	


	// -------------------- FUNCIONES
	
	/**
	 * Devuelve la cabecera para cobros y pagos.
	 * 
	 * @param company
	 * @return CabeceraSiiCobrosPagos
	 */
	private CabeceraSiiCobrosPagos cabeceraCobrosPagos(Company company){
		CabeceraSiiCobrosPagos cabecera = new CabeceraSiiCobrosPagos();
		cabecera.setIDVersionSii("1.1");
		PersonaFisicaJuridicaESType titular = new PersonaFisicaJuridicaESType();
		titular.setNIF(company.getDocument());
		titular.setNombreRazon(company.getName());
		cabecera.setTitular(titular);
		return cabecera;
	}
	
	/**
	 * Devuelve la cabecera.
	 * 
	 * @param company
	 * @return CabeceraSii
	 */
	private CabeceraSii cabecera(Company company, Boolean mod, String terceros){
		CabeceraSii cabecera = new CabeceraSii();
		cabecera.setIDVersionSii("1.1");
		cabecera.setTipoComunicacion(mod ? ClaveTipoComunicacionType.A_1 : ClaveTipoComunicacionType.A_0);
		PersonaFisicaJuridicaESType titular = new PersonaFisicaJuridicaESType();
		titular.setNIF(company.getDocument());
		titular.setNombreRazon(company.getName());
		if(terceros != null && !terceros.equals("false"))
			titular.setNIFRepresentante(terceros);
		cabecera.setTitular(titular);
		
		
		return cabecera;
	}
	
	/**
	 * Devuelve la cabecera para bajas.
	 * 
	 * @param company
	 * @return CabeceraSiiBaja
	 */
	private CabeceraSiiBaja cabeceraBaja(Company company, String terceros){
		CabeceraSiiBaja cabecera = new CabeceraSiiBaja();
		cabecera.setIDVersionSii("1.1");
		PersonaFisicaJuridicaESType titular = new PersonaFisicaJuridicaESType();
		titular.setNIF(company.getDocument());
		titular.setNombreRazon(company.getName());
		if(terceros != null && !terceros.equals("false"))
			titular.setNIFRepresentante(terceros);
		cabecera.setTitular(titular);
		return cabecera;
	}
	
	/**
	 * Devuelve la contraparte, persona fisica o juridica (cliente ó proveedor).
	 * 
	 * @param invoice
	 * @return PersonaFisicaJuridicaType
	 */
	private PersonaFisicaJuridicaType contraparte(VatContext vat) {
		PersonaFisicaJuridicaType contraparte = new PersonaFisicaJuridicaType();
		contraparte.setNombreRazon(vat.getRegistryName());
	
		if((vat.getRegistryDocumentCountry() == null || vat.getRegistryDocumentCountry().equals(Country.ES))
				&& (!vat.getInvoiceType().equals(InvoiceType.SALES) || !isPersonaFisica(vat.getRegistryDocument()) ||  validateNif(vat.getRegistryDocument(), vat.getRegistryName(), vat.getRegistryDocumentType()))){
			contraparte.setNIF(vat.getRegistryDocument());
		} else {
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryType2.valueOf(vat.getRegistryDocumentCountry().getIso2()));
			otro.setID(vat.getRegistryDocument());
			otro.setIDType(vat.getRegistryDocumentCountry().equals(Country.ES) ? 
				IDType.NO_CENSADO.getName() : IDType.valueOf(vat.getRegistryDocumentType()).getName());
			contraparte.setIDOtro(otro);
		}	
		return contraparte;
	}
	
	private PersonaFisicaJuridicaType contraparteIntracomunitario(VatContext vat) {
		PersonaFisicaJuridicaType contraparte = new PersonaFisicaJuridicaType();
		contraparte.setNombreRazon(vat.getRegistryName());
		if(vat.getRegistryDocumentCountry().equals(Country.ES)
				&& validateNif(vat.getRegistryDocument(), vat.getRegistryName(),vat.getRegistryDocumentType())){
			contraparte.setNIF(vat.getRegistryDocument());
		} else {
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryType2.valueOf(vat.getRegistryDocumentCountry().getIso2()));

			String document = vat.getRegistryDocument();
			if(!document.substring(0,2).equalsIgnoreCase(vat.getRegistryDocumentCountry().getIso2())) {
				boolean isGrecia = Country.GR.equals(vat.getRegistryDocumentCountry());
				String countryDocument = isGrecia ? "EL" : vat.getRegistryDocumentCountry().getIso2();
				document = countryDocument + document;
			}
			otro.setID(document);		
			
			otro.setIDType(IDType.NIF_IVA.getName());
			contraparte.setIDOtro(otro);
		}	
		return contraparte;
	}
	
	private Boolean isPersonaFisica(String document){
		String pri = document.substring(0, 1);
	return document.length() == 9 
		&& (isNumber(pri) || pri.equals("L") || pri.equals("K"));
	}
	
	private Boolean isNumber(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private Boolean validateNif(String nif, String name, DocumentType type) {
		return !DocumentType.NOT_CENSUSED.equals(type);
		/*
		VNifV1Ent vnif = new VNifV1Ent();
		vnif.setNif(nif);
		vnif.setNombre(name);
		return NIFPost.getInstance(cert, pass).vnifV1(vnif);
		*/
	}
	
	private static byte[] writeXml(JAXBContext ctx, Object object) throws JAXBException, IOException{		
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		marshaller.marshal(object, baos);
		baos.close();

		return baos.toByteArray();
	}

	private boolean isNDocument(String document) {
		return !AonStringUtils.isBlank(document) && 'N' == document.charAt(0);
	}
	
}

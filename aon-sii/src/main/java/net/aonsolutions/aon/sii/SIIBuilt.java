package net.aonsolutions.aon.sii;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatData;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;
import com.esferalia.aon.watson.util.AonMathUtils;

import net.aonsolutions.aon.nif.NIFPost;
import net.aonsolutions.core.aeat.nif.VNifV1Ent;
import net.aonsolutions.core.aeat.sii.BajaLRBienesInversion;
import net.aonsolutions.core.aeat.sii.BajaLRDetOperacionIntracomunitaria;
import net.aonsolutions.core.aeat.sii.BajaLRFacturasEmitidas;
import net.aonsolutions.core.aeat.sii.BajaLRFacturasRecibidas;
import net.aonsolutions.core.aeat.sii.BienDeInversionType;
import net.aonsolutions.core.aeat.sii.CabeceraSii;
import net.aonsolutions.core.aeat.sii.CabeceraSiiBaja;
import net.aonsolutions.core.aeat.sii.CabeceraSiiCobrosPagos;
import net.aonsolutions.core.aeat.sii.CausaExencionType;
import net.aonsolutions.core.aeat.sii.ClaveOperacionType;
import net.aonsolutions.core.aeat.sii.ClaveTipoComunicacionType;
import net.aonsolutions.core.aeat.sii.ClaveTipoFacturaType;
import net.aonsolutions.core.aeat.sii.CobrosType;
import net.aonsolutions.core.aeat.sii.CountryMiembroType;
import net.aonsolutions.core.aeat.sii.CountryType2;
import net.aonsolutions.core.aeat.sii.CuponType;
import net.aonsolutions.core.aeat.sii.DatosInmuebleType;
import net.aonsolutions.core.aeat.sii.DatosPagoCobroType;
import net.aonsolutions.core.aeat.sii.DesgloseFacturaRecibidasType;
import net.aonsolutions.core.aeat.sii.DesgloseFacturaRecibidasType.InversionSujetoPasivo;
import net.aonsolutions.core.aeat.sii.DesgloseRectificacionType;
import net.aonsolutions.core.aeat.sii.DetalleIVAEmitidaPrestacionType;
import net.aonsolutions.core.aeat.sii.DetalleIVAEmitidaType;
import net.aonsolutions.core.aeat.sii.DetalleIVARecibida2Type;
import net.aonsolutions.core.aeat.sii.DetalleIVARecibidaType;
import net.aonsolutions.core.aeat.sii.EmitidaPorTercerosType;
import net.aonsolutions.core.aeat.sii.FacturaExpedidaType;
import net.aonsolutions.core.aeat.sii.FacturaExpedidaType.DatosInmueble;
import net.aonsolutions.core.aeat.sii.FacturaExpedidaType.TipoDesglose;
import net.aonsolutions.core.aeat.sii.FacturaRecibidaType;
import net.aonsolutions.core.aeat.sii.FacturaType.FacturasAgrupadas;
import net.aonsolutions.core.aeat.sii.FacturaType.FacturasRectificadas;
import net.aonsolutions.core.aeat.sii.IDFacturaARType;
import net.aonsolutions.core.aeat.sii.IDFacturaComunitariaType;
import net.aonsolutions.core.aeat.sii.IDFacturaExpedidaBCType;
import net.aonsolutions.core.aeat.sii.IDFacturaExpedidaType;
import net.aonsolutions.core.aeat.sii.IDFacturaExpedidaType.IDEmisorFactura;
import net.aonsolutions.core.aeat.sii.IDFacturaRecibidaNombreBCType;
import net.aonsolutions.core.aeat.sii.IDFacturaRecibidaType;
import net.aonsolutions.core.aeat.sii.IDOtroType;
import net.aonsolutions.core.aeat.sii.LRAgenciasViajesType;
import net.aonsolutions.core.aeat.sii.LRBajaBienesInversionType;
import net.aonsolutions.core.aeat.sii.LRBajaExpedidasType;
import net.aonsolutions.core.aeat.sii.LRBajaOperacionIntracomunitariaType;
import net.aonsolutions.core.aeat.sii.LRBajaRecibidasType;
import net.aonsolutions.core.aeat.sii.LRBienesInversionType;
import net.aonsolutions.core.aeat.sii.LRCobrosEmitidasType;
import net.aonsolutions.core.aeat.sii.LRCobrosMetalicoType;
import net.aonsolutions.core.aeat.sii.LRFacturasRecibidasType;
import net.aonsolutions.core.aeat.sii.LROperacionIntracomunitariaType;
import net.aonsolutions.core.aeat.sii.LROperacionesSegurosType;
import net.aonsolutions.core.aeat.sii.LRPagosEmitidasType;
import net.aonsolutions.core.aeat.sii.LRfacturasEmitidasType;
import net.aonsolutions.core.aeat.sii.NoSujetaType;
import net.aonsolutions.core.aeat.sii.OperacionIntracomunitariaType;
import net.aonsolutions.core.aeat.sii.PagosType;
import net.aonsolutions.core.aeat.sii.PersonaFisicaJuridicaESType;
import net.aonsolutions.core.aeat.sii.PersonaFisicaJuridicaType;
import net.aonsolutions.core.aeat.sii.RegistroSii.PeriodoImpositivo;
import net.aonsolutions.core.aeat.sii.RespuestaLRAgenciasViajesType;
import net.aonsolutions.core.aeat.sii.RespuestaLRBajaBienesInversionType;
import net.aonsolutions.core.aeat.sii.RespuestaLRBajaFEmitidasType;
import net.aonsolutions.core.aeat.sii.RespuestaLRBajaFRecibidasType;
import net.aonsolutions.core.aeat.sii.RespuestaLRBajaOComunitariasType;
import net.aonsolutions.core.aeat.sii.RespuestaLRBienesInversionType;
import net.aonsolutions.core.aeat.sii.RespuestaLRCobrosEmitidasType;
import net.aonsolutions.core.aeat.sii.RespuestaLRFEmitidasType;
import net.aonsolutions.core.aeat.sii.RespuestaLRFRecibidasType;
import net.aonsolutions.core.aeat.sii.RespuestaLRIMetalicoType;
import net.aonsolutions.core.aeat.sii.RespuestaLROComunitariasType;
import net.aonsolutions.core.aeat.sii.RespuestaLROperacionesSegurosType;
import net.aonsolutions.core.aeat.sii.RespuestaLRPagosRecibidasType;
import net.aonsolutions.core.aeat.sii.SujetaPrestacionType;
import net.aonsolutions.core.aeat.sii.SujetaType;
import net.aonsolutions.core.aeat.sii.SujetaType.Exenta;
import net.aonsolutions.core.aeat.sii.SujetaType.NoExenta;
import net.aonsolutions.core.aeat.sii.SujetaType.NoExenta.DesgloseIVA;
import net.aonsolutions.core.aeat.sii.SuministroLRAgenciasViajes;
import net.aonsolutions.core.aeat.sii.SuministroLRBienesInversion;
import net.aonsolutions.core.aeat.sii.SuministroLRCobrosEmitidas;
import net.aonsolutions.core.aeat.sii.SuministroLRCobrosMetalico;
import net.aonsolutions.core.aeat.sii.SuministroLRDetOperacionIntracomunitaria;
import net.aonsolutions.core.aeat.sii.SuministroLRFacturasEmitidas;
import net.aonsolutions.core.aeat.sii.SuministroLRFacturasRecibidas;
import net.aonsolutions.core.aeat.sii.SuministroLROperacionesSeguros;
import net.aonsolutions.core.aeat.sii.SuministroLRPagosRecibidas;
import net.aonsolutions.core.aeat.sii.TipoConDesgloseType;
import net.aonsolutions.core.aeat.sii.TipoOperacionSujetaNoExentaType;
import net.aonsolutions.core.aeat.sii.TipoSinDesglosePrestacionType;
import net.aonsolutions.core.aeat.sii.TipoSinDesgloseType;
import net.aonsolutions.core.aeat.sii.VariosDestinatariosType;


public class SIIBuilt {

	public static SIIBuilt getInstance() {
		return new SIIBuilt();
	}
	
	public SIIBuilt() {

	}
	
	// ------------------- FACTURAS EMITIDAS
	 
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

	protected byte[] getRespuestaSuministroFacturasEmitidas(RespuestaLRFEmitidasType suministro){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRFEmitidasType.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	protected byte[] getBajaFacturasEmitidas(BajaLRFacturasEmitidas suministro){
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
	
	protected byte[] getRespuestaBajaFacturasEmitidas(RespuestaLRBajaFEmitidasType suministro){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRBajaFEmitidasType.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	byte[] cert;
	String pass;

	/**
	 * Libro de registro de Facturas expedidas.
	 * 
	 * @param company
	 * @param vatList
	 */
	protected SuministroLRFacturasEmitidas suministroFacturasEmitidas(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList
			, byte[] cert, String pass, Boolean mod, String terceros, String auth) {
		this.cert = cert;
		this.pass = pass;
		SuministroLRFacturasEmitidas suministro = new SuministroLRFacturasEmitidas();

		// CABECERA
		suministro.setCabecera(cabecera(company, mod, terceros));
		
		// BODY
		invoiceList.stream().forEach(invoice -> {	
			Double exenta =  contextList.stream().filter(f -> f.getInvoice().equals(invoice) && f.getPercentage() == 0  && !f.getVatDeductionType().equals(VatDeductionType.NON_TAXABLE))
					.mapToDouble(f -> f.getBase()).sum();
			Double noSujeta =  contextList.stream().filter(f -> f.getInvoice().equals(invoice) && f.getVatDeductionType().equals(VatDeductionType.NON_TAXABLE))
					.mapToDouble(f -> f.getBase()).sum();
			LinkedList<VatData> noExenta = contextList.stream().filter(f -> f.getInvoice().equals(invoice) && f.getPercentage() > 0  && !f.getVatDeductionType().equals(VatDeductionType.NON_TAXABLE))
					.map(f -> new VatData().setBase(f.getBase())
							.setPercentage(f.getPercentage())
							.setQuota(f.getQuota())
							.setSurchargePercent(f.getSurchargePercent())
							.setSurchargeQuota(f.getSurchargeQuota()))
					.collect(Collectors.toCollection(LinkedList::new));
				
			VatContext vat = contextList.stream().filter(f -> f.getInvoice().equals(invoice)).findFirst().orElse(new VatContext());

			LRfacturasEmitidasType factura = new LRfacturasEmitidasType();
			
			factura.setPeriodoImpositivo(periodoImpositivo(vat, false));

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
			if(vat.isIntracommunity() || vat.isExtracommunity()){
				fet.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaEmitidasType._02.getName());
			}
			if(vat.getTaxDate().compareTo(AonDateUtils.getDate(2017, 6, 1)) < 0){
	//			fet.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaEmitidasType._16.getName());
			}
			
			// CLAVE REGIMEN IVA || TRANSCENDENCIA ADICIONAL 2
			//fet.setClaveRegimenEspecialOTrascendenciaAdicional2("");//TODO OPTIONAL
	
			// NUMERO REGISTRO AUTORIZACION
			fet.setNumRegistroAcuerdoFacturacion("");//auth);//TODO TIENE K DARLO EL CLIENTE
			
			// IMPORTE TOTAL 
			//Double total2 = contextList.stream().filter(g -> g.getInvoice().equals(invoice)).mapToDouble(g -> g.getBase() + g.getQuota()).sum();
			Double total = noSujeta + exenta + noExenta.stream().mapToDouble(f -> f.getBase() + f.getQuota()).sum();
			fet.setImporteTotal(Double.toString(AonMathUtils.round(total)));

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
					str = str + ACCOUNTING.getAccount(domain.getName(), domain.getId(), login,  aed.getAccount()).getDescription() + "-";
				}
			}
			fet.setDescripcionOperacion(str + vat.getDetailDescription());
			
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
			fet.setEmitidaPorTerceros(EmitidaPorTercerosType.N);//TODO
			
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
				fet.setContraparte(contraparte(vat));
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
				&& (vat.isService() || vat.isIntracommunity() || vat.isExtracommunity())){
				TipoConDesgloseType tcdt = new TipoConDesgloseType();
				
				if(vat.isService() && !vat.isIntracommunity()){
					TipoSinDesglosePrestacionType prestacion = new TipoSinDesglosePrestacionType();
					NoSujetaType nst3 = new NoSujetaType();
					
					nst3.setImportePorArticulos714Otros(Double.toString(AonMathUtils.round(noSujeta))); 
				//	nst3.setImporteTAIReglasLocalizacion(""); // TODO
					prestacion.setNoSujeta(nst3);
					SujetaPrestacionType st3 = new SujetaPrestacionType();
					
					net.aonsolutions.core.aeat.sii.SujetaPrestacionType.Exenta exenta3 = new net.aonsolutions.core.aeat.sii.SujetaPrestacionType.Exenta();
					exenta3.setBaseImponible(Double.toString(AonMathUtils.round(exenta)));
					exenta3.setCausaExencion(CausaExencionType.E_6); //TODO  exencion otros a piñon fijo!!
					st3.setExenta(exenta3); 
					if(!noExenta.isEmpty()){
						net.aonsolutions.core.aeat.sii.SujetaPrestacionType.NoExenta.DesgloseIVA diva3 = new net.aonsolutions.core.aeat.sii.SujetaPrestacionType.NoExenta.DesgloseIVA();
						noExentaMap.keySet().stream().forEach(key -> {
							DetalleIVAEmitidaPrestacionType diet = new DetalleIVAEmitidaPrestacionType();
							diet.setBaseImponible(Double.toString(AonMathUtils.round(noExentaMap.get(key).getBase()))); 
							diet.setCuotaRepercutida(Double.toString(AonMathUtils.round(noExentaMap.get(key).getQuota()))); 
							diet.setTipoImpositivo(Double.toString(AonMathUtils.round(key))); 
							diva3.getDetalleIVA().add(diet);
						});
						net.aonsolutions.core.aeat.sii.SujetaPrestacionType.NoExenta noExenta3 = new net.aonsolutions.core.aeat.sii.SujetaPrestacionType.NoExenta();
						noExenta3.setDesgloseIVA(diva3);
						noExenta3.setTipoNoExenta(TipoOperacionSujetaNoExentaType.S_1);
						if(vat.isOtherISP()){
							noExenta3.setTipoNoExenta(TipoOperacionSujetaNoExentaType.S_2);
						}	
						st3.setNoExenta(noExenta3);
					}
					prestacion.setSujeta(st3);		
					tcdt.setPrestacionServicios(prestacion);
				} else {
					TipoSinDesgloseType entrega = new TipoSinDesgloseType();
					NoSujetaType nst2 = new NoSujetaType();
					if(vat.isIntracommunity() && vat.isOtherISP()){
						nst2.setImporteTAIReglasLocalizacion(Double.toString(AonMathUtils.round(noSujeta)));
					} else nst2.setImportePorArticulos714Otros(Double.toString(AonMathUtils.round(noSujeta))); // TODO
					// // TODO
					entrega.setNoSujeta(nst2);
					SujetaType st2 = new SujetaType();
					
					Exenta exenta2 = new Exenta();
					exenta2.setBaseImponible(Double.toString(AonMathUtils.round(exenta))); // TODO
				/* TODO	si es operacion intracomunitaria	
				 * if(vat.isIntracommunity()){
						exenta2.setCausaExencion(CausaExencionType.E_5);
					}else*/ 
					if(vat.isIntracommunity() || vat.isExtracommunity()){
						exenta2.setCausaExencion(CausaExencionType.E_2);
					} else {
						exenta2.setCausaExencion(CausaExencionType.E_6);
					}
					st2.setExenta(exenta2); // TODO
					if(!noExenta.isEmpty()){
						DesgloseIVA diva2 = new DesgloseIVA();
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

						NoExenta noExenta2 = new NoExenta();
						noExenta2.setDesgloseIVA(diva2);
						noExenta2.setTipoNoExenta(TipoOperacionSujetaNoExentaType.S_1);
						st2.setNoExenta(noExenta2); // TODO
					}
					entrega.setSujeta(st2);
					tcdt.setEntrega(entrega);
				}				
				tipoDesglose.setDesgloseTipoOperacion(tcdt);
			} else {
				TipoSinDesgloseType tsdt = new TipoSinDesgloseType();
				NoSujetaType nst = new NoSujetaType();
				nst.setImportePorArticulos714Otros(Double.toString(AonMathUtils.round(noSujeta))); // TODO
			//	nst.setImporteTAIReglasLocalizacion(""); // TODO
				tsdt.setNoSujeta(nst);
				SujetaType st = new SujetaType();
				
				Exenta exenta1 = new Exenta();
				exenta1.setBaseImponible(Double.toString(AonMathUtils.round(exenta))); // TODO
				exenta1.setCausaExencion(CausaExencionType.E_6); // TODO
				st.setExenta(exenta1);
				
				if(!noExenta.isEmpty()){
					DesgloseIVA diva = new DesgloseIVA();
					
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

					NoExenta noExenta1 = new NoExenta();
					noExenta1.setDesgloseIVA(diva);
					TipoOperacionSujetaNoExentaType noExType = TipoOperacionSujetaNoExentaType.S_1;
					// TODO inversion sujeto pasivo 
					noExenta1.setTipoNoExenta(noExType); // TODO 
					st.setNoExenta(noExenta1);
				}
				tsdt.setSujeta(st);
				tipoDesglose.setDesgloseFactura(tsdt);
			}
			fet.setTipoDesglose(tipoDesglose);
			
			factura.setFacturaExpedida(fet);
			suministro.getRegistroLRFacturasEmitidas().add(factura);
		});
		return suministro;
	}
	
	protected BajaLRFacturasEmitidas bajaFacturasEmitidas(Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> vatList, String terceros, String auth) {
		BajaLRFacturasEmitidas baja = new BajaLRFacturasEmitidas();
		baja.setCabecera(cabeceraBaja(company, terceros));
		
		invoiceList.stream().forEach(i -> {
			VatContext vat = vatList.stream().filter(f -> f.getInvoice().equals(i)).findFirst().orElse(new VatContext());
			LRBajaExpedidasType factura = new LRBajaExpedidasType();
			
			IDFacturaExpedidaBCType idFactura = new IDFacturaExpedidaBCType();
			idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));
			idFactura.setNumSerieFacturaEmisor(vat.getReferenceCode());
			net.aonsolutions.core.aeat.sii.IDFacturaExpedidaBCType.IDEmisorFactura emisor = new net.aonsolutions.core.aeat.sii.IDFacturaExpedidaBCType.IDEmisorFactura();
			emisor.setNIF(company.getDocument());
			idFactura.setIDEmisorFactura(emisor);
			factura.setIDFactura(idFactura);
			
			factura.setPeriodoImpositivo(periodoImpositivo(vat, false));
		
			baja.getRegistroLRBajaExpedidas().add(factura);
		});
		
		return baja;
	}
	
	// ------------------- SUMINISTRO FACTURAS EMITIDAS COBROS
	
	protected byte[] getSuministroFacturasEmitidasCobros(SuministroLRCobrosEmitidas suministro) {
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
	
	protected byte[] getRespuestaSuministroFacturasEmitidasCobros(RespuestaLRCobrosEmitidasType suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRCobrosEmitidasType.class);
			b = writeXml(ctx, suministro);
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
	protected SuministroLRCobrosEmitidas suministroFacturasEmitidasCobros(Domain domain, String login, Company company, LinkedList<Finance> financeList, LinkedList<Integer> invoiceList) {
		SuministroLRCobrosEmitidas suministro = new SuministroLRCobrosEmitidas();
		
		suministro.setCabecera(cabeceraCobrosPagos(company));

		invoiceList.stream().forEach(i -> {
			LRCobrosEmitidasType cobros = new LRCobrosEmitidasType();
			CobrosType ct = new CobrosType();
			financeList.stream().filter(f -> f.getInvoice().getId().equals(i)).forEach(f -> {
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
			
			Invoice invoice = financeList.stream().filter(f -> f.getInvoice().getId().equals(i)).findFirst().get().getInvoice();
			cobros.setCobros(ct);
			IDFacturaExpedidaBCType f = new IDFacturaExpedidaBCType();
			f.setFechaExpedicionFacturaEmisor(AonDateUtils.format(invoice.getIssueDate(), "dd-MM-yyyy"));
			net.aonsolutions.core.aeat.sii.IDFacturaExpedidaBCType.IDEmisorFactura emisor = new net.aonsolutions.core.aeat.sii.IDFacturaExpedidaBCType.IDEmisorFactura();
			emisor.setNIF(company.getDocument());
			f.setIDEmisorFactura(emisor);
			f.setNumSerieFacturaEmisor(invoice.getReferenceCode());
			cobros.setIDFactura(f);
			
			suministro.getRegistroLRCobros().add(cobros);
		});

		return suministro;
	}
	
	// ------------------- FACTURAS RECIBIDAS
	
	protected byte[] getSuministroFacturasRecibidas(SuministroLRFacturasRecibidas suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(SuministroLRFacturasRecibidas.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
		
	
	protected byte[] getRespuestaSuministroFacturasRecibidas(RespuestaLRFRecibidasType suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRFRecibidasType.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	protected byte[] getBajaFacturasRecibidas(BajaLRFacturasRecibidas suministro){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(BajaLRFacturasRecibidas.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	protected byte[] getRespuestaBajaFacturasRecibidas(RespuestaLRBajaFRecibidasType suministro){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRBajaFRecibidasType.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	
	/**
	 * Libro de registro de Facturas recibidas.
	 * 
	 * @param company
	 * @param invoiceList
	 */
	protected SuministroLRFacturasRecibidas suministroFacturasRecibidas(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, 
			byte[] cert, String pass, Boolean mod, String terceros, String auth) {
		this.cert = cert;
		this.pass = pass;
		SuministroLRFacturasRecibidas suministro = new SuministroLRFacturasRecibidas();
			
		// CABECERA
		suministro.setCabecera(cabecera(company, mod, terceros));
		
		// BODY
		invoiceList.stream().forEach(invoice -> {
			VatContext vat = contextList.stream().filter(f -> f.getInvoice().equals(invoice)).findFirst().orElse(new VatContext());

			LinkedList<VatData> noExenta = new LinkedList<>();
			LinkedList<VatData> pasivoList = new LinkedList<>();
			if(!vat.isIntracommunity()){
				noExenta = contextList.stream().filter(f -> f.getInvoice().equals(invoice) && !f.isOtherISP())
					.map(h -> new VatData()
							.setBase(h.getBase())
							.setPercentage(h.getPercentage())
							.setQuota(h.getQuota())
							.setSurchargePercent(h.getSurchargePercent())
							.setSurchargeQuota(h.getSurchargeQuota()))
					.collect(Collectors.toCollection(LinkedList::new));
			
				pasivoList = contextList.stream().filter(f -> f.getInvoice().equals(invoice) && f.isOtherISP())
					.map(f -> new VatData().setBase(f.getBase())
							.setPercentage(f.getPercentage())
							.setQuota(f.getQuota())
							.setSurchargePercent(f.getSurchargePercent())
							.setSurchargeQuota(f.getSurchargeQuota()))
					.collect(Collectors.toCollection(LinkedList::new));
			} else {
				noExenta = contextList.stream().filter(f -> f.getInvoice().equals(invoice))
					.map(f -> new VatData().setBase(f.getBase())
							.setPercentage(f.getPercentage())
							.setQuota(f.getQuota())
							.setSurchargePercent(f.getSurchargePercent())
							.setSurchargeQuota(f.getSurchargeQuota()))
					.collect(Collectors.toCollection(LinkedList::new));
			}
			
			LRFacturasRecibidasType factura = new LRFacturasRecibidasType();

			// PeriodoLiquidacion || PeriodoImpositivo
			factura.setPeriodoImpositivo(periodoImpositivo(vat, false));
			
			// IDFactura
			IDFacturaRecibidaType f = new IDFacturaRecibidaType();
			
			
			net.aonsolutions.core.aeat.sii.IDFacturaRecibidaType.IDEmisorFactura emisor = new net.aonsolutions.core.aeat.sii.IDFacturaRecibidaType.IDEmisorFactura();

			if(vat.getRegistryDocumentCountry().equals(Country.ES)){
				emisor.setNIF(vat.getRegistryDocument());
			} else {
				IDOtroType otro = new IDOtroType();
				otro.setCodigoPais(CountryType2.valueOf(vat.getRegistryDocumentCountry().getIso2()));
				otro.setID(vat.getRegistryDocument());
				otro.setIDType(IDType.valueOf(vat.getRegistryDocumentType()).getName());
				emisor.setIDOtro(otro);
			}			
			f.setIDEmisorFactura(emisor);
			
			f.setNumSerieFacturaEmisor(vat.getReferenceCode());// nº serie + nº factura 
			// (optional) f.setNumSerieFacturaEmisorResumenFin("");
			f.setFechaExpedicionFacturaEmisor(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));

			factura.setIDFactura(f);
			
			FacturaRecibidaType frt = new FacturaRecibidaType();
			// CONTRAPARTE
			frt.setContraparte(contraparte(vat));

			// CLAVE REGIMEN IVA || TRANSCENDENCIA  
			frt.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaRecibidasType._01.getName()); //TODO 

			if(vat.isVatAccrualRegime()){
				frt.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaRecibidasType._07.getName());//TODO OPTIONAL
			}
			
			if(vat.isIntracommunity()){
				frt.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaRecibidasType._09.getName());
			}
			if(vat.getTaxDate().compareTo(AonDateUtils.getDate(2017, 6, 1)) < 0){
	//			frt.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaRecibidasType._14.getName());
			}
			
			// BASE IMPONIBLE A COSTE (OPTIONAL)
			if(frt.getClaveRegimenEspecialOTrascendencia().equals("06")
					|| (frt.getClaveRegimenEspecialOTrascendenciaAdicional1() != null && frt.getClaveRegimenEspecialOTrascendenciaAdicional1().equals("06"))
					|| (frt.getClaveRegimenEspecialOTrascendenciaAdicional2() != null && frt.getClaveRegimenEspecialOTrascendenciaAdicional2().equals("06"))){
				Double base = noExenta.stream().mapToDouble(h -> h.getBase()).sum()
						+ pasivoList.stream().mapToDouble(h -> h.getBase()).sum();
				frt.setBaseImponibleACoste(Double.toString(AonMathUtils.round(base)));
			}			

			// CUOTA DEDUCIBLE
			frt.setCuotaDeducible(AonMathUtils.round(contextList.stream().filter(a -> a.getInvoice().equals(invoice)).mapToDouble(a -> a.getDeductibleQuota()).sum()) + ""); // TODO

			// DESCRIPCION OPERACION
			AccountingInvoice ai = ACCOUNTING.getAccountingInvoiceFromInvoice(domain.getName(), domain.getId(), login, invoice);
			String str = "";
			if(ai != null && ai.getAccountEntry() != null && ai.getAccountEntry().getDetails() != null){
				for(AccountEntryDetail aed : ai.getAccountEntry().getDetails()){
					str = str + ACCOUNTING.getAccount(domain.getName(), domain.getId(), login,  aed.getAccount()).getDescription() + "-";
				}
			}
			frt.setDescripcionOperacion(str + vat.getDetailDescription());
			
			// FECHA OPERACION
			frt.setFechaOperacion(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));//TODO
				
			// FECHA REGISTRO CONTABLE
			//frt.setFechaRegContable(AonDateUtils.format(vat.getRegContableDate(), "dd-MM-yyyy"));
			frt.setFechaRegContable(AonDateUtils.format(vat.getTaxDate(), "dd-MM-yyyy"));
			
			// IMPORTE TOTAL
			Double total = noExenta.stream().mapToDouble(h -> h.getBase() + h.getQuota()).sum()
					+ pasivoList.stream().mapToDouble(h -> h.getBase() + h.getQuota()).sum();
			frt.setImporteTotal(Double.toString(AonMathUtils.round(total)));//TODO
			
			// NUM REGISTRO ACUERDO FACTURACION
			frt.setNumRegistroAcuerdoFacturacion("");//auth);//TODO
			// TIPO FACTURA
			frt.setTipoFactura(ClaveTipoFacturaType.F_1);//TODO De momento a piñon fijo!!!
			if(vat.isIntracommunity()){
				frt.setTipoFactura(ClaveTipoFacturaType.F_5);
			}
				
			if(vat.isRectification()){
				frt.setTipoFactura(ClaveTipoFacturaType.R_1); // TODO R_1 || R_2 || R_3 || R_4 || R_4.  De momento a piñon fijo!!!
				frt.setTipoRectificativa("I"); //TODO  S (por sustitucion) || I (por diferencia). De momento a piñon fijo!!!

				// FACTURA RECTIFICADAS
				FacturasRectificadas fr = new FacturasRectificadas();
				Invoice rectificada = AON.getInvoice(domain.getName(), domain.getId(), login, h -> h.getIdProperty().eq(vat.getRectificationInvoice()));
				// SOLO 1 RECTIFICADA PARA CADA RECTIFICATIVA! 
				IDFacturaARType a2 = new IDFacturaARType();
				a2.setFechaExpedicionFacturaEmisor(AonDateUtils.format(rectificada.getIssueDate(), "dd-MM-yyyy")); //TODO
				a2.setNumSerieFacturaEmisor(rectificada.getReferenceCode());
				fr.getIDFacturaRectificada().add(a2);
				frt.setFacturasRectificadas(fr);
				
				// IMPORTE RECTIFICACION
				if("S".equalsIgnoreCase(frt.getTipoRectificativa())){
					DesgloseRectificacionType drt = new DesgloseRectificacionType();
					drt.setBaseRectificada(Double.toString(AonMathUtils.round(rectificada.getTaxableBase()))); 
		// TODO			drt.setCuotaRecargoRectificado(Double.toString(vat.getRectified().getSurchargeQuota()));  
					drt.setCuotaRectificada(Double.toString(AonMathUtils.round(rectificada.getVatQuota()))); // TODO ¿? 
					frt.setImporteRectificacion(drt);
				}
			}
			
			// for!!!
			if(ClaveTipoFacturaType.F_3.equals(frt.getTipoFactura())){
				// TODO FACTURAS AGRUPADAS
				FacturasAgrupadas fa = new FacturasAgrupadas();

				// FOR
				IDFacturaARType a = new IDFacturaARType();
				a.setFechaExpedicionFacturaEmisor("0.0"); 
				a.setNumSerieFacturaEmisor("0.0"); 
				fa.getIDFacturaAgrupada().add(a);		
				frt.setFacturasAgrupadas(fa);
			}

			DesgloseFacturaRecibidasType dfrt = new DesgloseFacturaRecibidasType();
			if(!noExenta.isEmpty()){
				HashMap<Double, VatData> noExentaMap = new HashMap<>();
				LinkedList<VatData> noExentaAux = noExenta;
				noExentaAux.stream().forEach(r -> {
					if(noExentaMap.containsKey(r.getPercentage())){
						VatData vd = noExentaMap.get(r.getPercentage());
						noExentaMap.get(r.getPercentage()).setBase(vd.getBase() + r.getBase());
						noExentaMap.get(r.getPercentage()).setQuota(vd.getQuota() + r.getQuota());
						noExentaMap.get(r.getPercentage()).setSurchargeQuota(vd.getSurchargeQuota() + r.getSurchargeQuota());					
					} else noExentaMap.put(r.getPercentage(), r);
				});
				
				net.aonsolutions.core.aeat.sii.DesgloseFacturaRecibidasType.DesgloseIVA diva = new net.aonsolutions.core.aeat.sii.DesgloseFacturaRecibidasType.DesgloseIVA();
				noExentaMap.keySet().stream().forEach(key -> {
					DetalleIVARecibidaType diet = new DetalleIVARecibidaType();
					diet.setBaseImponible(Double.toString(AonMathUtils.round(noExentaMap.get(key).getBase()))); //TODO
					diet.setCuotaSoportada(Double.toString(AonMathUtils.round(noExentaMap.get(key).getQuota())));
					diet.setTipoImpositivo(Double.toString(AonMathUtils.round(noExentaMap.get(key).getPercentage())));
					if(noExentaMap.get(key).getSurchargePercent() > 0.0 && noExentaMap.get(key).getSurchargeQuota() > 0.0){
						diet.setCuotaRecargoEquivalencia(Double.toString(AonMathUtils.round(noExentaMap.get(key).getSurchargeQuota())));
						diet.setTipoRecargoEquivalencia(Double.toString(AonMathUtils.round(noExentaMap.get(key).getSurchargePercent())));
					}
					diva.getDetalleIVA().add(diet);
				});
				dfrt.setDesgloseIVA(diva);
			}
			
			if(!pasivoList.isEmpty()){
				HashMap<Double, VatData> pasivoMap = new HashMap<>();
				pasivoList.stream().forEach(r -> {
					if(pasivoMap.containsKey(r.getPercentage())){
						VatData vd = pasivoMap.get(r.getPercentage());
						pasivoMap.get(r.getPercentage()).setBase(vd.getBase() + r.getBase());
						pasivoMap.get(r.getPercentage()).setQuota(vd.getQuota() + r.getQuota());
						pasivoMap.get(r.getPercentage()).setSurchargeQuota(vd.getSurchargeQuota() + r.getSurchargeQuota());					
					} else pasivoMap.put(r.getPercentage(), r);
				});
				
				InversionSujetoPasivo isp = new InversionSujetoPasivo();
				pasivoMap.keySet().stream().forEach(key -> {
					DetalleIVARecibida2Type diet = new DetalleIVARecibida2Type();
					diet.setBaseImponible(Double.toString(AonMathUtils.round(pasivoMap.get(key).getBase()))); //TODO
					diet.setCuotaSoportada(Double.toString(AonMathUtils.round(pasivoMap.get(key).getQuota())));
					diet.setTipoImpositivo(Double.toString(AonMathUtils.round(pasivoMap.get(key).getPercentage())));
					if(pasivoMap.get(key).getSurchargePercent() > 0.0 && pasivoMap.get(key).getSurchargeQuota() > 0.0){
						diet.setCuotaRecargoEquivalencia(Double.toString(AonMathUtils.round(pasivoMap.get(key).getSurchargeQuota())));
						diet.setTipoRecargoEquivalencia(Double.toString(AonMathUtils.round(pasivoMap.get(key).getSurchargePercent())));
					}
					isp.getDetalleIVA().add(diet);
				});
				dfrt.setInversionSujetoPasivo(isp);
			}	
			frt.setDesgloseFactura(dfrt); //TODO
			
			factura.setFacturaRecibida(frt);
			
			suministro.getRegistroLRFacturasRecibidas().add(factura);
		});
		return suministro;
	}
		
	protected BajaLRFacturasRecibidas bajaFacturasRecibidas(Company company,LinkedList<Integer> invoiceList, LinkedList<VatContext> vatList, String terceros, String auth) {
		BajaLRFacturasRecibidas baja = new BajaLRFacturasRecibidas();
		baja.setCabecera(cabeceraBaja(company, terceros));
		
		invoiceList.stream().forEach(i -> {
			VatContext vat = vatList.stream().filter(f -> f.getInvoice().equals(i)).findFirst().orElse(new VatContext());

			LRBajaRecibidasType factura = new LRBajaRecibidasType();
			
			IDFacturaRecibidaNombreBCType idFactura = new IDFacturaRecibidaNombreBCType();
			idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));
			idFactura.setNumSerieFacturaEmisor(vat.getReferenceCode());
			net.aonsolutions.core.aeat.sii.IDFacturaRecibidaNombreBCType.IDEmisorFactura emisor = new net.aonsolutions.core.aeat.sii.IDFacturaRecibidaNombreBCType.IDEmisorFactura();
		
			emisor.setNombreRazon(vat.getRegistryName());
			if(vat.getRegistryDocumentCountry().equals(Country.ES)){
				emisor.setNIF(vat.getRegistryDocument());
			} else {
				IDOtroType otro = new IDOtroType();
				otro.setCodigoPais(CountryType2.valueOf(vat.getRegistryDocumentCountry().getIso2()));
				otro.setID(vat.getRegistryDocument());
				otro.setIDType(IDType.valueOf(vat.getRegistryDocumentType()).getName());
				emisor.setIDOtro(otro);
			}
			
			idFactura.setIDEmisorFactura(emisor);
			factura.setIDFactura(idFactura);
			
			factura.setPeriodoImpositivo(periodoImpositivo(vat, false));
			
			baja.getRegistroLRBajaRecibidas().add(factura);
		});
		return baja;
	}
	
		
	// ------------------- SUMINISTRO FACTURAS RECIBIDAS PAGOS
			
	protected byte[] getSuministroFacturasRecibidasPagos(SuministroLRPagosRecibidas suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(SuministroLRPagosRecibidas.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	protected byte[] getRespuestaSuministroFacturasRecibidasPagos(RespuestaLRPagosRecibidasType suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRPagosRecibidasType.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	/**
	 * Libro de registro de Facturas recibidas, PAGOS.
	 * 
	 * @param company
	 */
	protected SuministroLRPagosRecibidas suministroFacturasRecibidasPagos(Domain domain, String login, Company company,  LinkedList<Finance> financeList, LinkedList<Integer> invoiceList) {
		SuministroLRPagosRecibidas suministro = new SuministroLRPagosRecibidas();
		
		// CABECERA
		suministro.setCabecera(cabeceraCobrosPagos(company));	
		
		// FOR PAGOS
		
		invoiceList.stream().forEach(i -> {
			LRPagosEmitidasType pagos = new LRPagosEmitidasType();
			PagosType pt = new PagosType();
			
			financeList.stream().filter(f -> f.getInvoice().getId().equals(i)).forEach(f -> {
				DatosPagoCobroType dpct = new DatosPagoCobroType();
				dpct.setFecha(AonDateUtils.format(f.getDueDate(), "dd-MM-yyyy"));
				dpct.setImporte(Double.toString(AonMathUtils.round(f.getAmount())));
				if(f.getPayMethodType().equals(PayMethodType.BANK_TRANSFER)){
					dpct.setMedio("01");
				} else if(f.getPayMethodType().equals(PayMethodType.CHEQUE)){
					dpct.setMedio("02");
				} else dpct.setMedio("04");
				pt.getPago().add(dpct);
			});
			pagos.setPagos(pt);
			Invoice invoice = financeList.stream().filter(f -> f.getInvoice().getId().equals(i)).findFirst().get().getInvoice();
			
			IDFacturaRecibidaNombreBCType f = new IDFacturaRecibidaNombreBCType();
			f.setFechaExpedicionFacturaEmisor(AonDateUtils.format(invoice.getIssueDate(), "dd-MM-yyyy"));
			
			net.aonsolutions.core.aeat.sii.IDFacturaRecibidaNombreBCType.IDEmisorFactura emisor = new net.aonsolutions.core.aeat.sii.IDFacturaRecibidaNombreBCType.IDEmisorFactura();
			emisor.setNombreRazon(invoice.getRegistryName());
			if(invoice.getRegistryDocumentCountry().equals(Country.ES)){
				emisor.setNIF(invoice.getRegistryDocument());
			} else {
				IDOtroType otro = new IDOtroType();
				otro.setCodigoPais(CountryType2.valueOf(invoice.getRegistryDocumentCountry().getIso2()));
				otro.setID(invoice.getRegistryDocument());
				otro.setIDType(IDType.valueOf(invoice.getRegistryDocumentType()).getName());
				emisor.setIDOtro(otro);
			}
			f.setIDEmisorFactura(emisor);
			f.setNumSerieFacturaEmisor(invoice.getReferenceCode());
			pagos.setIDFactura(f);
			
			suministro.getRegistroLRPagos().add(pagos);
		});
		
		return suministro;
	}
		
	// ------------------- BIENES INVERSION
		
	protected byte[] getSuministroBienesInversion(SuministroLRBienesInversion suministro){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(SuministroLRPagosRecibidas.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	protected byte[] getRespuestaSuministroBienesInversion(RespuestaLRBienesInversionType suministro){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRBienesInversionType.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	
	protected byte[] getBajaBienesInversion(BajaLRBienesInversion suministro){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(BajaLRBienesInversion.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	protected byte[] getRespuestaBajaBienesInversion(RespuestaLRBajaBienesInversionType suministro){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRBajaBienesInversionType.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	/**
	 * Libro de registro de Bienes de Inversión.
	 * 
	 * @param company
	 * @param invoiceList
	 */
	protected SuministroLRBienesInversion suministroBienesInversion(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, Boolean mod, String terceros, String auth) {
		SuministroLRBienesInversion suministro = new SuministroLRBienesInversion();
		
		// CABECERA
		suministro.setCabecera(cabecera(company, mod, terceros));
		
		// BODY
		invoiceList.stream().forEach(invoice -> {
			VatContext vat = contextList.stream().filter(f -> f.getInvoice().equals(invoice)).findFirst().orElse(new VatContext());

			LRBienesInversionType bien = new LRBienesInversionType();
			
			bien.setPeriodoImpositivo(periodoImpositivo(vat, true));
			
			IDFacturaComunitariaType idFactura = new IDFacturaComunitariaType();
			
			idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));
			
			net.aonsolutions.core.aeat.sii.IDFacturaComunitariaType.IDEmisorFactura emisor = new net.aonsolutions.core.aeat.sii.IDFacturaComunitariaType.IDEmisorFactura();
			emisor.setNIF(vat.getRegistryDocument());
			emisor.setNombreRazon(vat.getRegistryName());
			idFactura.setIDEmisorFactura(emisor);
			
			idFactura.setNumSerieFacturaEmisor(vat.getReferenceCode());
			
			bien.setIDFactura(idFactura);			
			
			
			BienDeInversionType bdit = new BienDeInversionType();
			
			bdit.setFechaInicioUtilizacion(AonDateUtils.format(vat.getAmortizationInitialDate(), "dd-MM-yyyy"));
			bdit.setIdentificacionBien(vat.getAmortizationDescription());
			bdit.setProrrataAnualDefinitiva(vat.getAmortizationPercentage().toString());
//			bdit.setRegularizacionAnualDeduccion(""); // OPTIONAL
//			bdit.setIdentificacionEntrega(""); // OPTIONAL
//			bdit.setRegularizacionDeduccionEfectuada(""); // OPTIONAL
			bien.setBienesInversion(bdit);
			
			suministro.getRegistroLRBienesInversion().add(bien);
		});
		return suministro;
	}
	
	protected BajaLRBienesInversion bajaBienesInversion(Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> vatList, String terceros, String auth) {
		BajaLRBienesInversion baja = new BajaLRBienesInversion();
		baja.setCabecera(cabeceraBaja(company, terceros));
		
		invoiceList.stream().forEach(invoice -> {
			VatContext vat = vatList.stream().filter(f -> f.getInvoice().equals(invoice)).findFirst().orElse(new VatContext());

			LRBajaBienesInversionType factura = new LRBajaBienesInversionType();
			
			factura.setIdentificacionBien(""); // TODO
			IDFacturaComunitariaType idFactura = new IDFacturaComunitariaType();
			idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));
			idFactura.setNumSerieFacturaEmisor(vat.getReferenceCode());
			net.aonsolutions.core.aeat.sii.IDFacturaComunitariaType.IDEmisorFactura emisor = new net.aonsolutions.core.aeat.sii.IDFacturaComunitariaType.IDEmisorFactura();
			emisor.setNIF(company.getDocument());
			idFactura.setIDEmisorFactura(emisor);
			factura.setIDFactura(idFactura);
				
			factura.setPeriodoImpositivo(periodoImpositivo(vat, false));
			
			baja.getRegistroLRBajaBienesInversion().add(factura);
		});
		return baja;
	}
		
	// ------------------- SUMINISTRO OPERACIONES INTRACOMUNITARIAS
		
	protected byte[] getSuministroOperacionesIntracomunitarias(SuministroLRDetOperacionIntracomunitaria suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(SuministroLRDetOperacionIntracomunitaria.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
		
	protected byte[] getRespuestaSuministroOperacionesIntracomunitarias(RespuestaLROComunitariasType suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLROComunitariasType.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	protected byte[] getBajaOperacionesIntracomunitarias(BajaLRDetOperacionIntracomunitaria suministro){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(BajaLRDetOperacionIntracomunitaria.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	protected byte[] getRespuestaBajaOperacionesIntracomunitarias(RespuestaLRBajaOComunitariasType suministro){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRBajaOComunitariasType.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	/**
	 * Libro de registro de Determinadas Operaciones Intracomunitarias.
	 * 
	 * @param company
	 * @param invoiceList
	 */
	protected SuministroLRDetOperacionIntracomunitaria suministroOperacionesIntracomunitarias(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String tipoOp, Boolean mod, String terceros, String auth) {
		SuministroLRDetOperacionIntracomunitaria suministro = new SuministroLRDetOperacionIntracomunitaria();
		
		// CABECERA
		suministro.setCabecera(cabecera(company, mod, terceros));
		
		// BODY
		invoiceList.stream().forEach(invoice -> {
			VatContext vat = contextList.stream().filter(f -> f.getInvoice().equals(invoice)).findFirst().orElse(new VatContext());
			
			LROperacionIntracomunitariaType opIntracomunitaria = new LROperacionIntracomunitariaType();
			
			opIntracomunitaria.setPeriodoImpositivo(periodoImpositivo(vat, false));
			
			IDFacturaComunitariaType idFactura = new IDFacturaComunitariaType();
			idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));
			
			net.aonsolutions.core.aeat.sii.IDFacturaComunitariaType.IDEmisorFactura emisor = new net.aonsolutions.core.aeat.sii.IDFacturaComunitariaType.IDEmisorFactura();
			if(vat.getInvoiceType().equals(InvoiceType.SALES)){
				emisor.setNIF(company.getDocument());
				emisor.setNombreRazon(company.getName());
			} else {
				emisor.setNombreRazon(vat.getRegistryName());
				if(vat.getRegistryDocumentCountry().equals(Country.ES)){
					emisor.setNIF(vat.getRegistryDocument());
				} else {
					IDOtroType otro = new IDOtroType();
					otro.setCodigoPais(CountryType2.valueOf(vat.getRegistryDocumentCountry().getIso2()));
					
					String document = vat.getRegistryDocument();
					if(!document.substring(0,2).equals(vat.getRegistryDocumentCountry().getIso2())) {
						document = vat.getRegistryDocumentCountry().getIso2() + document;
					}
					otro.setID(document);				
					
					otro.setIDType(IDType.NIF_IVA.getName()); 
					emisor.setIDOtro(otro);
				}
			}
			idFactura.setIDEmisorFactura(emisor);
			
			idFactura.setNumSerieFacturaEmisor(vat.getReferenceCode());
			opIntracomunitaria.setIDFactura(idFactura);

			opIntracomunitaria.setContraparte(contraparteIntracomunitario(vat));

			OperacionIntracomunitariaType oit = new OperacionIntracomunitariaType();
			oit.setTipoOperacion(tipoOp);// A(art 70) || B (art 16, 9)
			oit.setClaveDeclarado(vat.getInvoiceType().equals(InvoiceType.SALES) ? "D" : "R");
			oit.setEstadoMiembro(CountryMiembroType.valueOf(vat.getRegistryDocumentCountry().getIso2()));
			//oit.setPlazoOperacion(""); //OPTIONAL
			String desc = "";
			for (VatContext vatContext : contextList) {
				desc = desc + vatContext.getDetailDescription();
			}
			oit.setDescripcionBienes(desc.length() > 39 ? desc.substring(0, 39) : desc);// TODO
			RAddress address = AON.getRAddres(domain.getName(), domain.getId(), login, vat.getRegistry());
			oit.setDireccionOperador(address.getFullAddress() != null && !address.getFullAddress().equals("") ? address.getFullAddress() : "Sin direcci\u00f3n");
			//oit.setFacturasODocumentacion(""); // OPTIONAL
			
			opIntracomunitaria.setOperacionIntracomunitaria(oit);
			
			suministro.getRegistroLRDetOperacionIntracomunitaria().add(opIntracomunitaria);
		});
		return suministro;
	}
		
	protected BajaLRDetOperacionIntracomunitaria bajaOperacionesIntracomunitarias(Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> vatList, String terceros, String auth) {
		BajaLRDetOperacionIntracomunitaria baja = new BajaLRDetOperacionIntracomunitaria();
		baja.setCabecera(cabeceraBaja(company, terceros));
		
		invoiceList.stream().forEach(invoice -> {
			VatContext vat = vatList.stream().filter(f -> f.getInvoice().equals(invoice)).findFirst().orElse(new VatContext()); 
		
			LRBajaOperacionIntracomunitariaType factura = new LRBajaOperacionIntracomunitariaType();
			
			IDFacturaComunitariaType idFactura = new IDFacturaComunitariaType();
			idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));
			idFactura.setNumSerieFacturaEmisor(vat.getReferenceCode());
			net.aonsolutions.core.aeat.sii.IDFacturaComunitariaType.IDEmisorFactura emisor = new net.aonsolutions.core.aeat.sii.IDFacturaComunitariaType.IDEmisorFactura();
			if(vat.getInvoiceType().equals(InvoiceType.SALES)){
				emisor.setNIF(company.getDocument());
				emisor.setNombreRazon(company.getName());
			} else {
				emisor.setNombreRazon(vat.getRegistryName());
				if(vat.getRegistryDocumentCountry().equals(Country.ES)){
					emisor.setNIF(vat.getRegistryDocument());
				} else {
					IDOtroType otro = new IDOtroType();
					otro.setCodigoPais(CountryType2.valueOf(vat.getRegistryDocumentCountry().getIso2()));
					
					String document = vat.getRegistryDocument();
					if(!document.substring(0,2).equals(vat.getRegistryDocumentCountry().getIso2())) {
						document = vat.getRegistryDocumentCountry().getIso2() + document;
					}
					otro.setID(document);
					otro.setIDType(IDType.NIF_IVA.getName()); //valueOf(vat.getRegistryDocumentType()).getName());
					emisor.setIDOtro(otro);
				}
			}
			idFactura.setIDEmisorFactura(emisor);
			
			factura.setIDFactura(idFactura);
			
			factura.setPeriodoImpositivo(periodoImpositivo(vat, false));
			
			baja.getRegistroLRBajaDetOperacionIntracomunitaria().add(factura);
		});
		return baja;
	}
		
	// ------------------- SUMINISTRO COBROS METALICO

	protected byte[] getSuministroCobrosMetalico(SuministroLRCobrosMetalico suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(SuministroLRDetOperacionIntracomunitaria.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	protected byte[] getRespuestaSuministroCobrosMetalico(RespuestaLRIMetalicoType suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRIMetalicoType.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	/**
	 * Suministros de Operaciones en metálico.
	 * 
	 * @param company
	 * @param invoiceList
	 */
	protected SuministroLRCobrosMetalico suministroCobrosMetalico(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
		SuministroLRCobrosMetalico suministro = new SuministroLRCobrosMetalico();
		
		// CABECERA
		suministro.setCabecera(cabecera(company));
					
		// BODY
		invoiceList.stream().forEach(invoice -> {
			VatContext vat = contextList.stream().filter(f -> f.getInvoice().equals(invoice)).findFirst().orElse(new VatContext());
			
			LRCobrosMetalicoType metalico = new LRCobrosMetalicoType();	

			metalico.setPeriodoImpositivo(periodoImpositivo(vat, false));
			metalico.setContraparte(contraparte(vat));
			metalico.setImporteTotal(Double.toString(AonMathUtils.round(vat.getBase() + vat.getQuota())));	

			suministro.getRegistroLRCobrosMetalico().add(metalico);
		});
		return suministro;
	}
		
	// ------------------- SUMINISTRO OPERACIONES SEGUROS
	
	protected byte[] getSuministroOperacionesSeguros(SuministroLROperacionesSeguros suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(SuministroLROperacionesSeguros.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	protected byte[] getRespuestaSuministroOperacionesSeguros(RespuestaLROperacionesSegurosType suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLROperacionesSegurosType.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	/**
	 * Suministro de Operaciones de seguros.
	 * 
	 * @param company
	 * @param invoiceList
	 */
	protected SuministroLROperacionesSeguros suministroOperacionesSeguros(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
		SuministroLROperacionesSeguros suministro = new SuministroLROperacionesSeguros();
		
		// CABECERA
		suministro.setCabecera(cabecera(company));
		
		// BODY
		invoiceList.stream().forEach(invoice -> {
			VatContext vat = contextList.stream().filter(f -> f.getInvoice().equals(invoice)).findFirst().orElse(new VatContext());		
			
			LROperacionesSegurosType seguros = new LROperacionesSegurosType();
			
			seguros.setPeriodoImpositivo(periodoImpositivo(vat, false));
			seguros.setContraparte(contraparte(vat));
			seguros.setClaveOperacion(ClaveOperacionType.A); // TODO 
			seguros.setImporteTotal(Double.toString(AonMathUtils.round(vat.getBase() + vat.getQuota())));
			
			suministro.getRegistroLROperacionesSeguros().add(seguros);
		});
		
		return suministro;
	}
			
	// ------------------- SUMINISTRO AGENCIAS VIAJES	
		
	protected byte[] getSuministroAgenciasViajes(SuministroLRAgenciasViajes suministro){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(SuministroLRAgenciasViajes.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	protected byte[] getRespuestaSuministroAgenciasViajes(RespuestaLRAgenciasViajesType suministro){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRAgenciasViajesType.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	
	/**
	 * Suministro de Agencias de Viajes.
	 * 
	 * @param company
	 * @param invoiceList
	 */
	protected SuministroLRAgenciasViajes suministroAgenciasViajes(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
		SuministroLRAgenciasViajes suministro = new SuministroLRAgenciasViajes();
		
		// CABECERA
		suministro.setCabecera(cabecera(company));
		
		// BODY
		invoiceList.stream().forEach(invoice -> {
			VatContext vat = contextList.stream().filter(f -> f.getInvoice().equals(invoice)).findFirst().orElse(new VatContext());			

			LRAgenciasViajesType agencias = new LRAgenciasViajesType();	

			agencias.setPeriodoImpositivo(periodoImpositivo(vat, false));
			agencias.setContraparte(contraparte(vat));
			agencias.setImporteTotal(Double.toString(AonMathUtils.round(vat.getBase() + vat.getQuota())));
			
			suministro.getRegistroLRAgenciasViajes().add(agencias);
		});
		return suministro;
	}

	// -------------------- FUNCIONES
	
	/**
	 * Devuelve el periodo Impositivo ó periodo de liquidación.
	 * 
	 * @param invoice
	 * @return PeriodoImpositivo
	 */
	private PeriodoImpositivo periodoImpositivo(VatContext vat, Boolean anual){
		Integer year = AonDateUtils.getYear(vat.getIssueDate());
		Integer month = AonDateUtils.getMonth(vat.getIssueDate()) + 1;
		String p = month.toString();
		if(month < 10){
			p = "0" + p;
		}
		PeriodoImpositivo periodo = new PeriodoImpositivo();
		periodo.setEjercicio(year.toString());
		if(anual){
			periodo.setPeriodo("0A");
		} else {
			periodo.setPeriodo(p);// mes (01,02,03,04,...,12) || anual (0A));
		}
		return periodo;
	}

	/**
	 * Devuelve la cabecera para cobros y pagos.
	 * 
	 * @param company
	 * @return CabeceraSiiCobrosPagos
	 */
	private CabeceraSiiCobrosPagos cabeceraCobrosPagos(Company company){
		CabeceraSiiCobrosPagos cabecera = new CabeceraSiiCobrosPagos();
		cabecera.setIDVersionSii("1.0");
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
	public CabeceraSii cabecera(Company company, Boolean mod, String terceros){
		CabeceraSii cabecera = new CabeceraSii();
		cabecera.setIDVersionSii("1.0");
		cabecera.setTipoComunicacion(mod ? ClaveTipoComunicacionType.A_1 : ClaveTipoComunicacionType.A_0);
		PersonaFisicaJuridicaESType titular = new PersonaFisicaJuridicaESType();
		titular.setNIF(company.getDocument());
		titular.setNombreRazon(company.getName());
		if(terceros != null && !terceros.equals("false"))
			titular.setNIFRepresentante(terceros);
		cabecera.setTitular(titular);
		
		
		return cabecera;
	}
	public CabeceraSii cabecera(Company company){
		CabeceraSii cabecera = new CabeceraSii();
		cabecera.setIDVersionSii("1.0");
		cabecera.setTipoComunicacion(ClaveTipoComunicacionType.A_0);
		PersonaFisicaJuridicaESType titular = new PersonaFisicaJuridicaESType();
		titular.setNIF(company.getDocument());
		titular.setNombreRazon(company.getName());
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
		cabecera.setIDVersionSii("1.0");
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
		if(vat.getRegistryDocumentCountry().equals(Country.ES)
				&& (!vat.getInvoiceType().equals(InvoiceType.SALES) || !isPersonaFisica(vat.getRegistryDocument()) || validateNif(vat.getRegistryDocument(), vat.getRegistryName()))){
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
	
	private PersonaFisicaJuridicaType contraparteIntracomunitario(VatContext vat) {
		PersonaFisicaJuridicaType contraparte = new PersonaFisicaJuridicaType();
		contraparte.setNombreRazon(vat.getRegistryName());
		if(vat.getRegistryDocumentCountry().equals(Country.ES)
				&& validateNif(vat.getRegistryDocument(), vat.getRegistryName())){
			contraparte.setNIF(vat.getRegistryDocument());
		} else {
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryType2.valueOf(vat.getRegistryDocumentCountry().getIso2()));

			String document = vat.getRegistryDocument();
			if(!document.substring(0,2).equals(vat.getRegistryDocumentCountry().getIso2())) {
				document = vat.getRegistryDocumentCountry().getIso2() + document;
			}
			otro.setID(document);		
			
			otro.setIDType(IDType.NIF_IVA.getName());
			contraparte.setIDOtro(otro);
		}	
		return contraparte;
	}
	
	public Boolean validateNif(String nif, String name) {
		VNifV1Ent vnif = new VNifV1Ent();
		vnif.setNif(nif);
		vnif.setNombre(name);
		return NIFPost.getInstance(cert, pass).vnifV1(vnif);
	}
	
	public static byte[] writeXml(JAXBContext ctx, Object object) throws JAXBException, IOException{		
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		marshaller.marshal(object, baos);
		baos.close();

		return baos.toByteArray();
	}

	public static Object readXml(JAXBContext ctx, byte[] xmlFile) throws JAXBException{
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
	
		InputStream input = new ByteArrayInputStream(xmlFile);
		return unmarshaller.unmarshal(input);
	}
	
}

package com.code.aon.webservice.sii;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatData;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;

import net.aonsolutions.aon.sii.extra.ClaveRegimenEspecialOTrascendenciaType;
import net.aonsolutions.aon.sii.suministroinformacion.BienDeInversionType;
import net.aonsolutions.aon.sii.suministroinformacion.CabeceraSii;
import net.aonsolutions.aon.sii.suministroinformacion.CabeceraSiiBaja;
import net.aonsolutions.aon.sii.suministroinformacion.CabeceraSiiCobrosPagos;
import net.aonsolutions.aon.sii.suministroinformacion.CausaExencionType;
import net.aonsolutions.aon.sii.suministroinformacion.ClaveOperacionType;
import net.aonsolutions.aon.sii.suministroinformacion.ClaveTipoComunicacionType;
import net.aonsolutions.aon.sii.suministroinformacion.ClaveTipoFacturaType;
import net.aonsolutions.aon.sii.suministroinformacion.CobrosType;
import net.aonsolutions.aon.sii.suministroinformacion.CountryType2;
import net.aonsolutions.aon.sii.suministroinformacion.CuponType;
import net.aonsolutions.aon.sii.suministroinformacion.DatosInmuebleType;
import net.aonsolutions.aon.sii.suministroinformacion.DatosPagoCobroType;
import net.aonsolutions.aon.sii.suministroinformacion.DesgloseFacturaRecibidasType;
import net.aonsolutions.aon.sii.suministroinformacion.DesgloseFacturaRecibidasType.InversionSujetoPasivo;
import net.aonsolutions.aon.sii.suministroinformacion.DesgloseRectificacionType;
import net.aonsolutions.aon.sii.suministroinformacion.DetalleIVAEmitidaPrestacionType;
import net.aonsolutions.aon.sii.suministroinformacion.DetalleIVAEmitidaType;
import net.aonsolutions.aon.sii.suministroinformacion.DetalleIVARecibida2Type;
import net.aonsolutions.aon.sii.suministroinformacion.DetalleIVARecibidaType;
import net.aonsolutions.aon.sii.suministroinformacion.EmitidaPorTercerosType;
import net.aonsolutions.aon.sii.suministroinformacion.FacturaExpedidaType;
import net.aonsolutions.aon.sii.suministroinformacion.FacturaExpedidaType.DatosInmueble;
import net.aonsolutions.aon.sii.suministroinformacion.FacturaExpedidaType.TipoDesglose;
import net.aonsolutions.aon.sii.suministroinformacion.FacturaRecibidaType;
import net.aonsolutions.aon.sii.suministroinformacion.FacturaType.FacturasAgrupadas;
import net.aonsolutions.aon.sii.suministroinformacion.FacturaType.FacturasRectificadas;
import net.aonsolutions.aon.sii.suministroinformacion.IDFacturaARType;
import net.aonsolutions.aon.sii.suministroinformacion.IDFacturaComunitariaType;
import net.aonsolutions.aon.sii.suministroinformacion.IDFacturaExpedidaBCType;
import net.aonsolutions.aon.sii.suministroinformacion.IDFacturaExpedidaType;
import net.aonsolutions.aon.sii.suministroinformacion.IDFacturaExpedidaType.IDEmisorFactura;
import net.aonsolutions.aon.sii.suministroinformacion.IDFacturaRecibidaNombreBCType;
import net.aonsolutions.aon.sii.suministroinformacion.IDFacturaRecibidaType;
import net.aonsolutions.aon.sii.suministroinformacion.IDOtroType;
import net.aonsolutions.aon.sii.suministroinformacion.NoSujetaType;
import net.aonsolutions.aon.sii.suministroinformacion.OperacionIntracomunitariaType;
import net.aonsolutions.aon.sii.suministroinformacion.PagosType;
import net.aonsolutions.aon.sii.suministroinformacion.PersonaFisicaJuridicaESType;
import net.aonsolutions.aon.sii.suministroinformacion.PersonaFisicaJuridicaType;
import net.aonsolutions.aon.sii.suministroinformacion.RegistroSii.PeriodoImpositivo;
import net.aonsolutions.aon.sii.suministroinformacion.SujetaPrestacionType;
import net.aonsolutions.aon.sii.suministroinformacion.SujetaType;
import net.aonsolutions.aon.sii.suministroinformacion.SujetaType.Exenta;
import net.aonsolutions.aon.sii.suministroinformacion.SujetaType.NoExenta;
import net.aonsolutions.aon.sii.suministroinformacion.SujetaType.NoExenta.DesgloseIVA;
import net.aonsolutions.aon.sii.suministroinformacion.TipoConDesgloseType;
import net.aonsolutions.aon.sii.suministroinformacion.TipoOperacionSujetaNoExentaType;
import net.aonsolutions.aon.sii.suministroinformacion.TipoSinDesglosePrestacionType;
import net.aonsolutions.aon.sii.suministroinformacion.TipoSinDesgloseType;
import net.aonsolutions.aon.sii.suministroinformacion.VariosDestinatariosType;
import net.aonsolutions.aon.sii.suministrolr.BajaLRBienesInversion;
import net.aonsolutions.aon.sii.suministrolr.BajaLRDetOperacionIntracomunitaria;
import net.aonsolutions.aon.sii.suministrolr.BajaLRFacturasEmitidas;
import net.aonsolutions.aon.sii.suministrolr.BajaLRFacturasRecibidas;
import net.aonsolutions.aon.sii.suministrolr.LRAgenciasViajesType;
import net.aonsolutions.aon.sii.suministrolr.LRBajaBienesInversionType;
import net.aonsolutions.aon.sii.suministrolr.LRBajaExpedidasType;
import net.aonsolutions.aon.sii.suministrolr.LRBajaOperacionIntracomunitariaType;
import net.aonsolutions.aon.sii.suministrolr.LRBajaRecibidasType;
import net.aonsolutions.aon.sii.suministrolr.LRBienesInversionType;
import net.aonsolutions.aon.sii.suministrolr.LRCobrosEmitidasType;
import net.aonsolutions.aon.sii.suministrolr.LRCobrosMetalicoType;
import net.aonsolutions.aon.sii.suministrolr.LRFacturasRecibidasType;
import net.aonsolutions.aon.sii.suministrolr.LROperacionIntracomunitariaType;
import net.aonsolutions.aon.sii.suministrolr.LROperacionesSegurosType;
import net.aonsolutions.aon.sii.suministrolr.LRPagosEmitidasType;
import net.aonsolutions.aon.sii.suministrolr.LRfacturasEmitidasType;
import net.aonsolutions.aon.sii.suministrolr.SuministroLRAgenciasViajes;
import net.aonsolutions.aon.sii.suministrolr.SuministroLRBienesInversion;
import net.aonsolutions.aon.sii.suministrolr.SuministroLRCobrosEmitidas;
import net.aonsolutions.aon.sii.suministrolr.SuministroLRCobrosMetalico;
import net.aonsolutions.aon.sii.suministrolr.SuministroLRDetOperacionIntracomunitaria;
import net.aonsolutions.aon.sii.suministrolr.SuministroLRFacturasEmitidas;
import net.aonsolutions.aon.sii.suministrolr.SuministroLRFacturasRecibidas;
import net.aonsolutions.aon.sii.suministrolr.SuministroLROperacionesSeguros;
import net.aonsolutions.aon.sii.suministrolr.SuministroLRPagosRecibidas;

public class SIIBuilt {

	public static SIIBuilt getInstance() {
		return new SIIBuilt();
	}
	
	public SIIBuilt() {

	}
	
	// ------------------- FACTURAS EMITIDAS
	 
	protected byte[] getSuministroFacturasEmitidas(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) throws JAXBException, IOException{
		JAXBContext ctx = JAXBContext.newInstance(SuministroLRFacturasEmitidas.class);
		return writeXml(ctx, suministroFacturasEmitidas(domain, login, company, invoiceList, contextList));
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

	
	/**
	 * Libro de registro de Facturas expedidas.
	 * 
	 * @param company
	 * @param vatList
	 */
	protected SuministroLRFacturasEmitidas suministroFacturasEmitidas(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
		SuministroLRFacturasEmitidas suministro = new SuministroLRFacturasEmitidas();
	
		// CABECERA
		suministro.setCabecera(cabecera(company));
		
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
			
			factura.setPeriodoImpositivo(periodoImpositivo(vat));

			IDFacturaExpedidaType idFactura = new IDFacturaExpedidaType();
			
			idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));
			idFactura.setNumSerieFacturaEmisorResumenFin(vat.getReferenceCode());
			idFactura.setNumSerieFacturaEmisor(vat.getReferenceCode());
			IDEmisorFactura emisor = new IDEmisorFactura();
			emisor.setNIF(company.getDocument());
			idFactura.setIDEmisorFactura(emisor);
			factura.setIDFactura(idFactura);
			FacturaExpedidaType fet = new FacturaExpedidaType();
			
			fet.setTipoFactura(ClaveTipoFacturaType.F_1); // TODO  De momento a piñon fijo!!!
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
				DesgloseRectificacionType drt = new DesgloseRectificacionType();
				drt.setBaseRectificada(Double.toString(rectificada.getTaxableBase())); 
	// TODO			drt.setCuotaRecargoRectificado(Double.toString(vat.getRectified().getSurchargeQuota()));  
				if("S".equalsIgnoreCase(fet.getTipoRectificativa())){
					drt.setCuotaRectificada(Double.toString(rectificada.getVatQuota())); // TODO ¿? 
				}
 				fet.setImporteRectificacion(drt);
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
		
			// FECHA OPERACION 
			fet.setFechaOperacion("05-06-2017");//TODO 
			
			// CLAVE REGIMEN IVA || TRANSCENDENCIA  
			
			fet.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaType._01.getName()); //TODO 

			// CLAVE REGIMEN IVA || TRANSCENDENCIA ADICIONAL 1			
			if(vat.isVatAccrualRegime()){
				fet.setClaveRegimenEspecialOTrascendenciaAdicional1(ClaveRegimenEspecialOTrascendenciaType._07.getName());//TODO OPTIONAL
			}
			
			// CLAVE REGIMEN IVA || TRANSCENDENCIA ADICIONAL 2
			//fet.setClaveRegimenEspecialOTrascendenciaAdicional2("");//TODO OPTIONAL
	
			// NUMERO REGISTRO AUTORIZACION
			fet.setNumRegistroAcuerdoFacturacion("");//TODO TIENE K DARLO EL CLIENTE
			
			// IMPORTE TOTAL 
			Double total = noSujeta + exenta + noExenta.stream().mapToDouble(f -> f.getBase() + f.getQuota()).sum();
			fet.setImporteTotal(Double.toString(total));

			// BASE IMPONIBLE A COSTE (OPTIONAL)
			Double base = noSujeta + exenta + noExenta.stream().mapToDouble(f -> f.getBase()).sum();
			fet.setBaseImponibleACoste(Double.toString(base));
			
			// DESCRIPCION OPERACION 
			fet.setDescripcionOperacion("FACTURA " + vat.getReferenceCode()); // + " - " + vat.getComments());
		
			// DATOS INMUEBLES (OPTIONAL) TODO // sii regimen IVA 12 - Operaciones de arrendamiento de local de negocio no sujetos a retención.
			if(fet.getClaveRegimenEspecialOTrascendencia().equals("12")
					|| fet.getClaveRegimenEspecialOTrascendenciaAdicional1().equals("12")
					|| fet.getClaveRegimenEspecialOTrascendenciaAdicional2().equals("12")){
				DatosInmueble datosInmueble = new DatosInmueble();
				DatosInmuebleType dit = new DatosInmuebleType();
				dit.setReferenciaCatastral("");//TODO
				dit.setSituacionInmueble("");//TODO
				datosInmueble.getDetalleInmueble().add(dit);
				fet.setDatosInmueble(datosInmueble);
				Invoice i = new Invoice();
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
			fet.setContraparte(contraparte(vat));
	
			// TIPO DESGLOSE
			TipoDesglose tipoDesglose = new TipoDesglose(); 
			
			if(!vat.isService()){
				TipoSinDesgloseType tsdt = new TipoSinDesgloseType();
				NoSujetaType nst = new NoSujetaType();
				nst.setImportePorArticulos714Otros(Double.toString(noSujeta)); // TODO
			//	nst.setImporteTAIReglasLocalizacion(""); // TODO
				tsdt.setNoSujeta(nst);
				SujetaType st = new SujetaType();
				
				Exenta exenta1 = new Exenta();
				exenta1.setBaseImponible(Double.toString(exenta)); // TODO
				exenta1.setCausaExencion(CausaExencionType.E_6); // TODO
				st.setExenta(exenta1);
					
				DesgloseIVA diva = new DesgloseIVA();
				noExenta.stream().forEach(r->{
					DetalleIVAEmitidaType diet = new DetalleIVAEmitidaType();
					diet.setBaseImponible(Double.toString(r.getBase())); //TODO
					diet.setCuotaRecargoEquivalencia(Double.toString(r.getSurchargePercent())); //TODO
					diet.setCuotaRepercutida(Double.toString(r.getQuota())); //TODO
					diet.setTipoImpositivo(Double.toString(r.getPercentage())); //TODO
					diet.setTipoRecargoEquivalencia(Double.toString(r.getSurchargeQuota())); //TODO
		
					diva.getDetalleIVA().add(diet);
				});
				NoExenta noExenta1 = new NoExenta();
				noExenta1.setDesgloseIVA(diva);
				noExenta1.setTipoNoExenta(TipoOperacionSujetaNoExentaType.S_3); // TODO 
				st.setNoExenta(noExenta1);

				tsdt.setSujeta(st);
				tipoDesglose.setDesgloseFactura(tsdt);
			} else {
				TipoConDesgloseType tcdt = new TipoConDesgloseType();
				/*
				TipoSinDesgloseType entrega = new TipoSinDesgloseType();
				NoSujetaType nst2 = new NoSujetaType();
				nst2.setImportePorArticulos714Otros(""); // TODO
				nst2.setImporteTAIReglasLocalizacion(""); // TODO
				entrega.setNoSujeta(nst2);
				SujetaType st2 = new SujetaType();
				
				Exenta exenta2 = new Exenta();
				exenta2.setBaseImponible(""); // TODO
				exenta2.setCausaExencion(CausaExencionType.E_6);
				st2.setExenta(exenta2); // TODO
				DesgloseIVA diva2 = new DesgloseIVA();
				
				invoiceTaxList.stream().forEach(r->{
					DetalleIVAEmitidaType diet = new DetalleIVAEmitidaType();
					diet.setBaseImponible(""); //TODO
					diet.setCuotaRecargoEquivalencia(""); //TODO
					diet.setCuotaRepercutida(""); //TODO
					diet.setTipoImpositivo(""); //TODO
					diet.setTipoRecargoEquivalencia(""); //TODO
					
					diva2.getDetalleIVA().add(diet);
				});	
			
				NoExenta noExenta2 = new NoExenta();
				noExenta2.setDesgloseIVA(diva2);
				noExenta2.setTipoNoExenta(TipoOperacionSujetaNoExentaType.S_1);
				st2.setNoExenta(noExenta2); // TODO
				entrega.setSujeta(st2);
				tcdt.setEntrega(entrega);
				*/
				TipoSinDesglosePrestacionType prestacion = new TipoSinDesglosePrestacionType();
				NoSujetaType nst3 = new NoSujetaType();
				nst3.setImportePorArticulos714Otros(Double.toString(noSujeta)); 
			//	nst3.setImporteTAIReglasLocalizacion(""); // TODO
				prestacion.setNoSujeta(nst3);
				SujetaPrestacionType st3 = new SujetaPrestacionType();
				
				net.aonsolutions.aon.sii.suministroinformacion.SujetaPrestacionType.Exenta exenta3 = new net.aonsolutions.aon.sii.suministroinformacion.SujetaPrestacionType.Exenta();
				exenta3.setBaseImponible(Double.toString(exenta));
				exenta3.setCausaExencion(CausaExencionType.E_6); //TODO  exencion otros a piñon fijo!!
				st3.setExenta(exenta3); 
				net.aonsolutions.aon.sii.suministroinformacion.SujetaPrestacionType.NoExenta.DesgloseIVA diva3 = new net.aonsolutions.aon.sii.suministroinformacion.SujetaPrestacionType.NoExenta.DesgloseIVA();
				
				noExenta.stream().forEach(r->{
					DetalleIVAEmitidaPrestacionType diet = new DetalleIVAEmitidaPrestacionType();
					diet.setBaseImponible(Double.toString(r.getBase())); 
					diet.setCuotaRepercutida(Double.toString(r.getQuota())); 
					diet.setTipoImpositivo(Double.toString(r.getPercentage())); 
					
					diva3.getDetalleIVA().add(diet);
				});	
			
				net.aonsolutions.aon.sii.suministroinformacion.SujetaPrestacionType.NoExenta noExenta3 = new net.aonsolutions.aon.sii.suministroinformacion.SujetaPrestacionType.NoExenta();
				noExenta3.setDesgloseIVA(diva3);
				noExenta3.setTipoNoExenta(TipoOperacionSujetaNoExentaType.S_3);
				st3.setNoExenta(noExenta3);
				prestacion.setSujeta(st3);		
				tcdt.setPrestacionServicios(new TipoSinDesglosePrestacionType());
				tipoDesglose.setDesgloseTipoOperacion(tcdt);
			}
			fet.setTipoDesglose(tipoDesglose);
			
			factura.setFacturaExpedida(fet);
			suministro.getRegistroLRFacturasEmitidas().add(factura);
		});
		return suministro;
	}
	
	protected BajaLRFacturasEmitidas bajaFacturasEmitidas(Company company, LinkedList<VatContext> vatList) {
		BajaLRFacturasEmitidas baja = new BajaLRFacturasEmitidas();
		baja.setCabecera(cabeceraBaja(company));
		
		vatList.stream().forEach(vat -> {
			LRBajaExpedidasType factura = new LRBajaExpedidasType();
			
			IDFacturaExpedidaBCType idFactura = new IDFacturaExpedidaBCType();
			idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));
			idFactura.setNumSerieFacturaEmisor(vat.getReferenceCode());
			net.aonsolutions.aon.sii.suministroinformacion.IDFacturaExpedidaBCType.IDEmisorFactura emisor = new net.aonsolutions.aon.sii.suministroinformacion.IDFacturaExpedidaBCType.IDEmisorFactura();
			emisor.setNIF(company.getDocument());
			idFactura.setIDEmisorFactura(emisor);
			factura.setIDFactura(idFactura);
			
			factura.setPeriodoImpositivo(periodoImpositivo(vat));
		
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

	
	/**
	 * Libro de registro de Facturas expedidas, COBROS.
	 *  
	 * @param company
	 */
	protected SuministroLRCobrosEmitidas suministroFacturasEmitidasCobros(Company company) {
		SuministroLRCobrosEmitidas suministro = new SuministroLRCobrosEmitidas();
		
		// CABECERA
		suministro.setCabecera(cabeceraCobrosPagos(company));

		// FOR COBROS
		
		LRCobrosEmitidasType cobros = new LRCobrosEmitidasType();
		CobrosType ct = new CobrosType();
		//TODO FOR
		DatosPagoCobroType dpct = new DatosPagoCobroType();
		dpct.setCuentaOMedio(""); //TODO
		dpct.setFecha("");//TODO
		dpct.setImporte("");//TODO
		dpct.setMedio("");//TODO
		ct.getCobro().add(dpct);
		cobros.setCobros(ct);
		IDFacturaExpedidaBCType f = new IDFacturaExpedidaBCType();
		f.setFechaExpedicionFacturaEmisor("");//TODO
		net.aonsolutions.aon.sii.suministroinformacion.IDFacturaExpedidaBCType.IDEmisorFactura emisor = new net.aonsolutions.aon.sii.suministroinformacion.IDFacturaExpedidaBCType.IDEmisorFactura();
		emisor.setNIF(company.getDocument());
		f.setIDEmisorFactura(emisor);
		f.setNumSerieFacturaEmisor(""); //TODO
		// TODO NO HAY OPTION f.setNumSerieFacturaEmisorResumenFin("");
		cobros.setIDFactura(f);
		
		suministro.getRegistroLRCobros().add(cobros);

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
	
	/**
	 * Libro de registro de Facturas recibidas.
	 * 
	 * @param company
	 * @param invoiceList
	 */
	protected SuministroLRFacturasRecibidas suministroFacturasRecibidas(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
		SuministroLRFacturasRecibidas suministro = new SuministroLRFacturasRecibidas();
		
		// CABECERA
		suministro.setCabecera(cabecera(company));
				
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
			
			LRFacturasRecibidasType factura = new LRFacturasRecibidasType();

			// PeriodoLiquidacion || PeriodoImpositivo
			factura.setPeriodoImpositivo(periodoImpositivo(vat));
			
			// IDFactura
			IDFacturaRecibidaType f = new IDFacturaRecibidaType();
			net.aonsolutions.aon.sii.suministroinformacion.IDFacturaRecibidaType.IDEmisorFactura emisor = new net.aonsolutions.aon.sii.suministroinformacion.IDFacturaRecibidaType.IDEmisorFactura();
			emisor.setNIF(company.getDocument());
			f.setIDEmisorFactura(emisor);
			f.setNumSerieFacturaEmisor(vat.getReferenceCode());// nº serie + nº factura 
			// (optional) f.setNumSerieFacturaEmisorResumenFin("");
			f.setFechaExpedicionFacturaEmisor("");
			factura.setIDFactura(f);
			
			FacturaRecibidaType frt = new FacturaRecibidaType();
			// CONTRAPARTE
			frt.setContraparte(contraparte(vat));

			// BASE IMPONIBLE A COSTE
			frt.setBaseImponibleACoste(""); // TODO
			
			// REGIMEN ESPECIAL O TRASCENDENCIA
			frt.setClaveRegimenEspecialOTrascendencia(""); //TODO
			frt.setClaveRegimenEspecialOTrascendenciaAdicional1(""); // TODO
			frt.setClaveRegimenEspecialOTrascendenciaAdicional2(""); //TODO

			// CUOTA DEDUCIBLE
			frt.setCuotaDeducible(""); // TODO
			
			// DESCRIPCION OPERACION
			frt.setDescripcionOperacion(""); //TODO
			
			// FECHA OPERACION
			frt.setFechaOperacion("");//TODO
		
			// FECHA REGISTRO CONTABLE
			frt.setFechaRegContable("");//TODO
			
			// IMPORTE TOTAL
			frt.setImporteTotal("");//TODO
			
			// NUM REGISTRO ACUERDO FACTURACION
			frt.setNumRegistroAcuerdoFacturacion("");//TODO
			
			// TIPO RECTIFICATIVA
			frt.setTipoRectificativa("");//TODO
			
			// TIPO FACTURA
			frt.setTipoFactura(ClaveTipoFacturaType.F_1);//TODO
			
			DesgloseRectificacionType drt = new DesgloseRectificacionType();
			drt.setBaseRectificada(""); // TODO
			drt.setCuotaRecargoRectificado(""); // TODO
			drt.setCuotaRectificada(""); // TODO
			frt.setImporteRectificacion(drt);
			
			// for!!!
			FacturasAgrupadas fa = new FacturasAgrupadas();
			IDFacturaARType art = new IDFacturaARType();
			art.setFechaExpedicionFacturaEmisor(""); // TODO 
			art.setNumSerieFacturaEmisor(""); // TODO
			fa.getIDFacturaAgrupada().add(art);
			frt.setFacturasAgrupadas(fa);
			
			FacturasRectificadas fr = new FacturasRectificadas();
			IDFacturaARType art2 = new IDFacturaARType();
			art2.setFechaExpedicionFacturaEmisor(""); // TODO 
			art2.setNumSerieFacturaEmisor(""); // TODO
			fr.getIDFacturaRectificada().add(art2);
			frt.setFacturasRectificadas(fr);
			
			DesgloseFacturaRecibidasType dfrt = new DesgloseFacturaRecibidasType();
			net.aonsolutions.aon.sii.suministroinformacion.DesgloseFacturaRecibidasType.DesgloseIVA diva = new net.aonsolutions.aon.sii.suministroinformacion.DesgloseFacturaRecibidasType.DesgloseIVA();
			LinkedList<InvoiceDetail> invoiceDetailList = new LinkedList<>();
			invoiceDetailList.stream().forEach(r->{
				DetalleIVARecibidaType diet = new DetalleIVARecibidaType();
				diet.setBaseImponible(""); //TODO
				diet.setCuotaRecargoEquivalencia(""); //TODO
				diet.setTipoImpositivo(""); //TODO
				diet.setTipoRecargoEquivalencia(""); //TODO
			
				diva.getDetalleIVA().add(diet);
			});
				
			dfrt.setDesgloseIVA(diva);
			
			InversionSujetoPasivo isp = new InversionSujetoPasivo();
			
			invoiceDetailList.stream().forEach(r->{
				DetalleIVARecibida2Type diet = new DetalleIVARecibida2Type();
				diet.setBaseImponible(""); //TODO
				diet.setCuotaRecargoEquivalencia(""); //TODO
				diet.setTipoImpositivo(""); //TODO
				diet.setTipoRecargoEquivalencia(""); //TODO
				diet.setCuotaSoportada(""); // TODO
			
				isp.getDetalleIVA().add(diet);
			});
			dfrt.setInversionSujetoPasivo(isp);
			
			frt.setDesgloseFactura(dfrt); //TODO
			
			factura.setFacturaRecibida(frt);
			
			suministro.getRegistroLRFacturasRecibidas().add(factura);
		});
		return suministro;
	}
	
	protected BajaLRFacturasRecibidas bajaFacturasRecibidas(Company company, LinkedList<VatContext> vatList) {
		BajaLRFacturasRecibidas baja = new BajaLRFacturasRecibidas();
		baja.setCabecera(cabeceraBaja(company));
		
		vatList.stream().forEach(vat -> {
			LRBajaRecibidasType factura = new LRBajaRecibidasType();
			
			IDFacturaRecibidaNombreBCType idFactura = new IDFacturaRecibidaNombreBCType();
			idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));
			idFactura.setNumSerieFacturaEmisor(vat.getReferenceCode());
			net.aonsolutions.aon.sii.suministroinformacion.IDFacturaRecibidaNombreBCType.IDEmisorFactura emisor = new net.aonsolutions.aon.sii.suministroinformacion.IDFacturaRecibidaNombreBCType.IDEmisorFactura();
			emisor.setNIF(company.getDocument());
			idFactura.setIDEmisorFactura(emisor);
			factura.setIDFactura(idFactura);
			
			factura.setPeriodoImpositivo(periodoImpositivo(vat));
		
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
	
	/**
	 * Libro de registro de Facturas recibidas, PAGOS.
	 * 
	 * @param company
	 */
	protected SuministroLRPagosRecibidas suministroFacturasRecibidasPagos(Company company) {
		SuministroLRPagosRecibidas suministro = new SuministroLRPagosRecibidas();
		
		// CABECERA
		suministro.setCabecera(cabeceraCobrosPagos(company));

		// FOR PAGOS
		
		// PAGOS
		LRPagosEmitidasType pagos = new LRPagosEmitidasType();
		PagosType pt = new PagosType();
		DatosPagoCobroType dpct = new DatosPagoCobroType();
		dpct.setCuentaOMedio("");
		dpct.setFecha("");
		dpct.setImporte("");
		dpct.setMedio("");
		pt.getPago().add(dpct);
		pagos.setPagos(pt);
		
		// ID FACTURA
		IDFacturaRecibidaNombreBCType f = new IDFacturaRecibidaNombreBCType();
		f.setFechaExpedicionFacturaEmisor("");
		net.aonsolutions.aon.sii.suministroinformacion.IDFacturaRecibidaNombreBCType.IDEmisorFactura emisor = new net.aonsolutions.aon.sii.suministroinformacion.IDFacturaRecibidaNombreBCType.IDEmisorFactura();
		emisor.setNIF(company.getDocument());
		f.setIDEmisorFactura(emisor);
		f.setNumSerieFacturaEmisor("");
		pagos.setIDFactura(f);
		
		suministro.getRegistroLRPagos().add(pagos);
		
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
	
	/**
	 * Libro de registro de Bienes de Inversión.
	 * 
	 * @param company
	 * @param invoiceList
	 */
	protected SuministroLRBienesInversion suministroBienesInversion(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
		SuministroLRBienesInversion suministro = new SuministroLRBienesInversion();
		
		// CABECERA
		suministro.setCabecera(cabecera(company));
				
		// BODY
		invoiceList.stream().forEach(invoice -> {
			VatContext vat = contextList.stream().filter(f -> f.getInvoice().equals(invoice)).findFirst().orElse(new VatContext());

			
			LRBienesInversionType bien = new LRBienesInversionType();

			bien.setPeriodoImpositivo(periodoImpositivo(vat));
			bien.setIDFactura(new IDFacturaComunitariaType());			
			bien.setBienesInversion(new BienDeInversionType());
			
			suministro.getRegistroLRBienesInversion().add(bien);
		});
		return suministro;
	}
	
	protected BajaLRBienesInversion bajaBienesInversion(Company company, LinkedList<VatContext> vatList) {
		BajaLRBienesInversion baja = new BajaLRBienesInversion();
		baja.setCabecera(cabeceraBaja(company));
		
		vatList.stream().forEach(vat -> {
			LRBajaBienesInversionType factura = new LRBajaBienesInversionType();
			
			factura.setIdentificacionBien(""); // TODO
			IDFacturaComunitariaType idFactura = new IDFacturaComunitariaType();
			idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));
			idFactura.setNumSerieFacturaEmisor(vat.getReferenceCode());
			net.aonsolutions.aon.sii.suministroinformacion.IDFacturaComunitariaType.IDEmisorFactura emisor = new net.aonsolutions.aon.sii.suministroinformacion.IDFacturaComunitariaType.IDEmisorFactura();
			emisor.setNIF(company.getDocument());
			idFactura.setIDEmisorFactura(emisor);
			factura.setIDFactura(idFactura);
			
			factura.setPeriodoImpositivo(periodoImpositivo(vat));
		
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

	
	/**
	 * Libro de registro de Determinadas Operaciones Intracomunitarias.
	 * 
	 * @param company
	 * @param invoiceList
	 */
	protected SuministroLRDetOperacionIntracomunitaria suministroOperacionesIntracomunitarias(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
		SuministroLRDetOperacionIntracomunitaria suministro = new SuministroLRDetOperacionIntracomunitaria();
		
		// CABECERA
		suministro.setCabecera(cabecera(company));
				
		// BODY
		invoiceList.stream().forEach(invoice -> {
			VatContext vat = contextList.stream().filter(f -> f.getInvoice().equals(invoice)).findFirst().orElse(new VatContext());
			
			LROperacionIntracomunitariaType opIntracomunitaria = new LROperacionIntracomunitariaType();

			opIntracomunitaria.setPeriodoImpositivo(periodoImpositivo(vat));
			opIntracomunitaria.setIDFactura(new IDFacturaComunitariaType());			
			opIntracomunitaria.setContraparte(contraparte(vat));
			opIntracomunitaria.setOperacionIntracomunitaria(new OperacionIntracomunitariaType());
			
			suministro.getRegistroLRDetOperacionIntracomunitaria().add(opIntracomunitaria);
		});
		return suministro;
	}
	
	protected BajaLRDetOperacionIntracomunitaria bajaOperacionesIntracomunitarias(Company company, LinkedList<VatContext> vatList) {
		BajaLRDetOperacionIntracomunitaria baja = new BajaLRDetOperacionIntracomunitaria();
		baja.setCabecera(cabeceraBaja(company));
		
		vatList.stream().forEach(vat -> {
			LRBajaOperacionIntracomunitariaType factura = new LRBajaOperacionIntracomunitariaType();
			
			IDFacturaComunitariaType idFactura = new IDFacturaComunitariaType();
			idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));
			idFactura.setNumSerieFacturaEmisor(vat.getReferenceCode());
			net.aonsolutions.aon.sii.suministroinformacion.IDFacturaComunitariaType.IDEmisorFactura emisor = new net.aonsolutions.aon.sii.suministroinformacion.IDFacturaComunitariaType.IDEmisorFactura();
			emisor.setNIF(company.getDocument());
			idFactura.setIDEmisorFactura(emisor);
			factura.setIDFactura(idFactura);
			
			factura.setPeriodoImpositivo(periodoImpositivo(vat));
		
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

			metalico.setPeriodoImpositivo(periodoImpositivo(vat));
			metalico.setContraparte(contraparte(vat));
			metalico.setImporteTotal(Double.toString(vat.getBase() + vat.getQuota()));

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

			seguros.setPeriodoImpositivo(periodoImpositivo(vat));
			seguros.setContraparte(contraparte(vat));
			seguros.setClaveOperacion(ClaveOperacionType.A); // TODO 
			seguros.setImporteTotal(Double.toString(vat.getBase() + vat.getQuota()));
			
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

			agencias.setPeriodoImpositivo(periodoImpositivo(vat));
			agencias.setContraparte(contraparte(vat));
			agencias.setImporteTotal(Double.toString(vat.getBase() + vat.getQuota()));
			
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
	private PeriodoImpositivo periodoImpositivo(VatContext vat){
		Integer year = AonDateUtils.getYear(vat.getIssueDate());
		Integer month = AonDateUtils.getMonth(vat.getIssueDate()) + 1;
		String p = month.toString();
		if(month < 10){
			p = "0" + p;
		}
		PeriodoImpositivo periodo = new PeriodoImpositivo();
		periodo.setEjercicio(year.toString());
		periodo.setPeriodo(p);// mes (01,02,03,04,...,12) || anual (0A));
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
		cabecera.setIDVersionSii("0.7");
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
	private CabeceraSii cabecera(Company company){
		CabeceraSii cabecera = new CabeceraSii();
		cabecera.setIDVersionSii("0.7");
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
	private CabeceraSiiBaja cabeceraBaja(Company company){
		CabeceraSiiBaja cabecera = new CabeceraSiiBaja();
		cabecera.setIDVersionSii("0.7");
		PersonaFisicaJuridicaESType titular = new PersonaFisicaJuridicaESType();
		titular.setNIF(company.getDocument());
		titular.setNombreRazon(company.getName());
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
		if(vat.getRegistryDocumentCountry().equals(Country.ES)){
			contraparte.setNIF(vat.getRegistryDocument());
		} else {
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryType2.values()[vat.getRegistryDocumentCountry().ordinal()]);
			otro.setID(vat.getRegistryDocument());
			otro.setIDType(vat.getRegistryDocumentType().getDescription());
			contraparte.setIDOtro(otro);
		}
		return contraparte;
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

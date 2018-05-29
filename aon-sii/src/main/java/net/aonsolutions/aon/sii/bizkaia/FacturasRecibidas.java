package net.aonsolutions.aon.sii.bizkaia;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatData;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;
import com.esferalia.aon.watson.util.AonMathUtils;

import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaLRBajaFRecibidasType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaLRFRecibidasType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaLRPagosRecibidasType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.CabeceraSii;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.CabeceraSiiBaja;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.CabeceraSiiCobrosPagos;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.ClaveTipoComunicacionType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.ClaveTipoFacturaType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.CountryType2;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.DatosPagoCobroType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.DesgloseFacturaRecibidasType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.DesgloseFacturaRecibidasType.DesgloseIVA;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.DesgloseFacturaRecibidasType.InversionSujetoPasivo;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.DesgloseRectificacionType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.DetalleIVARecibida2Type;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.DetalleIVARecibidaType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.FacturaRecibidaType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.FacturaType.FacturasAgrupadas;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.FacturaType.FacturasRectificadas;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.IDFacturaARType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.IDFacturaRecibidaNombreBCType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.IDFacturaRecibidaType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.IDFacturaRecibidaType.IDEmisorFactura;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.IDOtroType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.PagosType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.PersonaFisicaJuridicaESType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.PersonaFisicaJuridicaType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.BajaLRFacturasRecibidas;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.LRBajaRecibidasType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.LRFacturasRecibidasType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.LRPagosEmitidasType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.SuministroLRFacturasRecibidas;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.SuministroLRPagosRecibidas;
import net.aonsolutions.aon.sii.ClaveRegimenEspecialOTrascendenciaRecibidasType;
import net.aonsolutions.aon.sii.IDType;

public class FacturasRecibidas extends SIIBuilt {

	public static FacturasRecibidas getInstance() {
		return new FacturasRecibidas();
	}
	
	public FacturasRecibidas() {

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

	protected byte[] getBajaFacturasRecibidas(BajaLRFacturasRecibidas suministro) {
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

	protected byte[] getRespuestaBajaFacturasRecibidas(RespuestaLRBajaFRecibidasType suministro) {
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
	protected SuministroLRFacturasRecibidas suministroFacturasRecibidas(Domain domain, String login, Company company,
			LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, Boolean mod, String terceros, String auth) {
		SuministroLRFacturasRecibidas suministro = new SuministroLRFacturasRecibidas();

		// CABECERA
		suministro.setCabecera(cabecera(company, mod, terceros));

		// BODY
		invoiceList.stream().forEach(invoice -> {
			VatContext vat = contextList.stream().filter(f -> f.getInvoice().equals(invoice)).findFirst()
					.orElse(new VatContext());

			LinkedList<VatData> noExenta = new LinkedList<>();
			LinkedList<VatData> pasivoList = new LinkedList<>();
			if (!vat.isIntracommunity()) {
				noExenta = contextList.stream().filter(f -> f.getInvoice().equals(invoice) && !f.isOtherISP())
						.map(h -> new VatData().setBase(h.getBase()).setPercentage(h.getPercentage())
								.setQuota(h.getQuota()).setSurchargePercent(h.getSurchargePercent())
								.setSurchargeQuota(h.getSurchargeQuota()))
						.collect(Collectors.toCollection(LinkedList::new));

				pasivoList = contextList.stream().filter(f -> f.getInvoice().equals(invoice) && f.isOtherISP())
						.map(f -> new VatData().setBase(f.getBase()).setPercentage(f.getPercentage())
								.setQuota(f.getQuota()).setSurchargePercent(f.getSurchargePercent())
								.setSurchargeQuota(f.getSurchargeQuota()))
						.collect(Collectors.toCollection(LinkedList::new));
			} else {
				noExenta = contextList.stream().filter(f -> f.getInvoice().equals(invoice))
						.map(f -> new VatData().setBase(f.getBase()).setPercentage(f.getPercentage())
								.setQuota(f.getQuota()).setSurchargePercent(f.getSurchargePercent())
								.setSurchargeQuota(f.getSurchargeQuota()))
						.collect(Collectors.toCollection(LinkedList::new));
			}

			LRFacturasRecibidasType factura = new LRFacturasRecibidasType();

			// PeriodoLiquidacion || PeriodoImpositivo
			factura.setPeriodoLiquidacion(periodoLiquidacion(vat, false));

			// IDFactura
			IDFacturaRecibidaType f = new IDFacturaRecibidaType();

			IDEmisorFactura emisor = new IDEmisorFactura();

			if (vat.getRegistryDocumentCountry().equals(Country.ES)) {
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
			frt.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaRecibidasType._01.getName()); // TODO

			if (vat.isVatAccrualRegime()) {
				frt.setClaveRegimenEspecialOTrascendencia(
						ClaveRegimenEspecialOTrascendenciaRecibidasType._07.getName());// TODO OPTIONAL
			}

			if (vat.isIntracommunity()) {
				frt.setClaveRegimenEspecialOTrascendencia(
						ClaveRegimenEspecialOTrascendenciaRecibidasType._09.getName());
			}
			ApplicationParameter ap = AON.getApplicationParameter(domain.getName(), domain.getId(), login,
					AppParam.FS_MODEL_CFG_SII);
			Boolean isRegistro = "R".equals(ap.getValue());
			Date opDate = isRegistro ? vat.getCreationDate() : vat.getTaxDate();
			if (opDate.compareTo(AonDateUtils.getDate(2017, 6, 1)) < 0) {
				// frt.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaRecibidasType._14.getName());
			}

			// BASE IMPONIBLE A COSTE (OPTIONAL)
			if (frt.getClaveRegimenEspecialOTrascendencia().equals("06")
					|| (frt.getClaveRegimenEspecialOTrascendenciaAdicional1() != null
							&& frt.getClaveRegimenEspecialOTrascendenciaAdicional1().equals("06"))
					|| (frt.getClaveRegimenEspecialOTrascendenciaAdicional2() != null
							&& frt.getClaveRegimenEspecialOTrascendenciaAdicional2().equals("06"))) {
				Double base = noExenta.stream().mapToDouble(h -> h.getBase()).sum()
						+ pasivoList.stream().mapToDouble(h -> h.getBase()).sum();
				frt.setBaseImponibleACoste(Double.toString(AonMathUtils.round(base)));
			}

			// CUOTA DEDUCIBLE
			frt.setCuotaDeducible(AonMathUtils.round(contextList.stream().filter(a -> a.getInvoice().equals(invoice))
					.mapToDouble(a -> a.getDeductibleQuota()).sum()) + ""); // TODO

			// DESCRIPCION OPERACION
			AccountingInvoice ai = ACCOUNTING.getAccountingInvoiceFromInvoice(domain.getName(), domain.getId(), login,
					invoice);
			String str = "";
			if (ai != null && ai.getAccountEntry() != null && ai.getAccountEntry().getDetails() != null) {
				for (AccountEntryDetail aed : ai.getAccountEntry().getDetails()) {
					Account a = ACCOUNTING.getAccount(domain.getName(), domain.getId(), login, aed.getAccount());
					if (a.getCode().substring(0, 1).equals("6") || a.getCode().substring(0, 1).equals("7")) {
						str = str + a.getDescription() + "-";
					}
				}
			}
			frt.setDescripcionOperacion(str + vat.getDetailDescription());

			// FECHA OPERACION
			frt.setFechaOperacion(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));// TODO

			// FECHA REGISTRO CONTABLE
			// frt.setFechaRegContable(AonDateUtils.format(vat.getRegContableDate(),
			// "dd-MM-yyyy"));
			frt.setFechaRegContable(AonDateUtils.format(opDate, "dd-MM-yyyy"));

			// IMPORTE TOTAL
			Double total = noExenta.stream().mapToDouble(h -> h.getBase() + h.getQuota()).sum()
					+ pasivoList.stream().mapToDouble(h -> h.getBase() + h.getQuota()).sum();
			frt.setImporteTotal(Double.toString(AonMathUtils.round(total)));// TODO

			// NUM REGISTRO ACUERDO FACTURACION
			frt.setNumRegistroAcuerdoFacturacion("");// auth);//TODO
			// TIPO FACTURA
			frt.setTipoFactura(ClaveTipoFacturaType.F_1);// TODO De momento a piñon fijo!!!
			if (vat.isIntracommunity()) {
				frt.setTipoFactura(ClaveTipoFacturaType.F_5);
			}

			if (vat.isRectification()) {
				frt.setTipoFactura(ClaveTipoFacturaType.R_1); // TODO R_1 || R_2 || R_3 || R_4 || R_4. De momento a
																// piñon fijo!!!
				frt.setTipoRectificativa("I"); // TODO S (por sustitucion) || I (por diferencia). De momento a piñon
												// fijo!!!

				// FACTURA RECTIFICADAS
				FacturasRectificadas fr = new FacturasRectificadas();
				Invoice rectificada = AON.getInvoice(domain.getName(), domain.getId(), login,
						h -> h.getIdProperty().eq(vat.getRectificationInvoice()));
				// SOLO 1 RECTIFICADA PARA CADA RECTIFICATIVA!
				IDFacturaARType a2 = new IDFacturaARType();
				a2.setFechaExpedicionFacturaEmisor(AonDateUtils.format(rectificada.getIssueDate(), "dd-MM-yyyy")); // TODO
				a2.setNumSerieFacturaEmisor(rectificada.getReferenceCode());
				fr.getIDFacturaRectificada().add(a2);
				frt.setFacturasRectificadas(fr);

				// IMPORTE RECTIFICACION
				if ("S".equalsIgnoreCase(frt.getTipoRectificativa())) {
					DesgloseRectificacionType drt = new DesgloseRectificacionType();
					drt.setBaseRectificada(Double.toString(AonMathUtils.round(rectificada.getTaxableBase())));
					// TODO
					// drt.setCuotaRecargoRectificado(Double.toString(vat.getRectified().getSurchargeQuota()));
					drt.setCuotaRectificada(Double.toString(AonMathUtils.round(rectificada.getVatQuota()))); // TODO ¿?
					frt.setImporteRectificacion(drt);
				}
			}

			// for!!!
			if (ClaveTipoFacturaType.F_3.equals(frt.getTipoFactura())) {
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
			if (!noExenta.isEmpty()) {
				HashMap<Double, VatData> noExentaMap = new HashMap<>();
				LinkedList<VatData> noExentaAux = noExenta;
				noExentaAux.stream().forEach(r -> {
					if (noExentaMap.containsKey(r.getPercentage())) {
						VatData vd = noExentaMap.get(r.getPercentage());
						noExentaMap.get(r.getPercentage()).setBase(vd.getBase() + r.getBase());
						noExentaMap.get(r.getPercentage()).setQuota(vd.getQuota() + r.getQuota());
						noExentaMap.get(r.getPercentage())
								.setSurchargeQuota(vd.getSurchargeQuota() + r.getSurchargeQuota());
					} else
						noExentaMap.put(r.getPercentage(), r);
				});

				DesgloseIVA diva = new DesgloseIVA();
				noExentaMap.keySet().stream().forEach(key -> {
					DetalleIVARecibidaType diet = new DetalleIVARecibidaType();
					diet.setBaseImponible(Double.toString(AonMathUtils.round(noExentaMap.get(key).getBase()))); // TODO
					diet.setCuotaSoportada(Double.toString(AonMathUtils.round(noExentaMap.get(key).getQuota())));
					diet.setTipoImpositivo(Double.toString(AonMathUtils.round(noExentaMap.get(key).getPercentage())));
					if (noExentaMap.get(key).getSurchargePercent() > 0.0
							&& noExentaMap.get(key).getSurchargeQuota() > 0.0) {
						diet.setCuotaRecargoEquivalencia(
								Double.toString(AonMathUtils.round(noExentaMap.get(key).getSurchargeQuota())));
						diet.setTipoRecargoEquivalencia(
								Double.toString(AonMathUtils.round(noExentaMap.get(key).getSurchargePercent())));
					}
					diva.getDetalleIVA().add(diet);
				});
				dfrt.setDesgloseIVA(diva);
			}

			if (!pasivoList.isEmpty()) {
				HashMap<Double, VatData> pasivoMap = new HashMap<>();
				pasivoList.stream().forEach(r -> {
					if (pasivoMap.containsKey(r.getPercentage())) {
						VatData vd = pasivoMap.get(r.getPercentage());
						pasivoMap.get(r.getPercentage()).setBase(vd.getBase() + r.getBase());
						pasivoMap.get(r.getPercentage()).setQuota(vd.getQuota() + r.getQuota());
						pasivoMap.get(r.getPercentage())
								.setSurchargeQuota(vd.getSurchargeQuota() + r.getSurchargeQuota());
					} else
						pasivoMap.put(r.getPercentage(), r);
				});

				InversionSujetoPasivo isp = new InversionSujetoPasivo();
				pasivoMap.keySet().stream().forEach(key -> {
					DetalleIVARecibida2Type diet = new DetalleIVARecibida2Type();
					diet.setBaseImponible(Double.toString(AonMathUtils.round(pasivoMap.get(key).getBase()))); // TODO
					diet.setCuotaSoportada(Double.toString(AonMathUtils.round(pasivoMap.get(key).getQuota())));
					diet.setTipoImpositivo(Double.toString(AonMathUtils.round(pasivoMap.get(key).getPercentage())));
					if (pasivoMap.get(key).getSurchargePercent() > 0.0
							&& pasivoMap.get(key).getSurchargeQuota() > 0.0) {
						diet.setCuotaRecargoEquivalencia(
								Double.toString(AonMathUtils.round(pasivoMap.get(key).getSurchargeQuota())));
						diet.setTipoRecargoEquivalencia(
								Double.toString(AonMathUtils.round(pasivoMap.get(key).getSurchargePercent())));
					}
					isp.getDetalleIVA().add(diet);
				});
				dfrt.setInversionSujetoPasivo(isp);
			}
			frt.setDesgloseFactura(dfrt); // TODO

			factura.setFacturaRecibida(frt);

			suministro.getRegistroLRFacturasRecibidas().add(factura);
		});
		return suministro;
	}

	protected BajaLRFacturasRecibidas bajaFacturasRecibidas(Company company, LinkedList<Integer> invoiceList,
			LinkedList<VatContext> vatList, String terceros, String auth) {
		BajaLRFacturasRecibidas baja = new BajaLRFacturasRecibidas();
		baja.setCabecera(cabeceraBaja(company, terceros));

		invoiceList.stream().forEach(i -> {
			VatContext vat = vatList.stream().filter(f -> f.getInvoice().equals(i)).findFirst()
					.orElse(new VatContext());

			LRBajaRecibidasType factura = new LRBajaRecibidasType();

			IDFacturaRecibidaNombreBCType idFactura = new IDFacturaRecibidaNombreBCType();
			idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));
			idFactura.setNumSerieFacturaEmisor(vat.getReferenceCode());
			eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.IDFacturaRecibidaNombreBCType.IDEmisorFactura emisor = new eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.IDFacturaRecibidaNombreBCType.IDEmisorFactura();

			emisor.setNombreRazon(vat.getRegistryName());
			if (vat.getRegistryDocumentCountry().equals(Country.ES)) {
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

			factura.setPeriodoLiquidacion(periodoLiquidacion(vat, false));

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
	protected SuministroLRPagosRecibidas suministroFacturasRecibidasPagos(Domain domain, String login, Company company,
			LinkedList<Finance> financeList, LinkedList<Integer> invoiceList) {
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
				if (f.getPayMethodType().equals(PayMethodType.BANK_TRANSFER)) {
					dpct.setMedio("01");
				} else if (f.getPayMethodType().equals(PayMethodType.CHEQUE)) {
					dpct.setMedio("02");
				} else
					dpct.setMedio("04");
				pt.getPago().add(dpct);
			});
			pagos.setPagos(pt);
			Invoice invoice = financeList.stream().filter(f -> f.getInvoice().getId().equals(i)).findFirst().get()
					.getInvoice();

			IDFacturaRecibidaNombreBCType f = new IDFacturaRecibidaNombreBCType();
			f.setFechaExpedicionFacturaEmisor(AonDateUtils.format(invoice.getIssueDate(), "dd-MM-yyyy"));

			eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.IDFacturaRecibidaNombreBCType.IDEmisorFactura emisor = new eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.IDFacturaRecibidaNombreBCType.IDEmisorFactura();
			emisor.setNombreRazon(invoice.getRegistryName());
			if (invoice.getRegistryDocumentCountry().equals(Country.ES)) {
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
	
	// -------------------- FUNCIONES

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

	public Boolean validateNif(String nif, String name, DocumentType type) {
		return !type.equals(DocumentType.NOT_CENSUSED);
		/*
		VNifV1Ent vnif = new VNifV1Ent();
		vnif.setNif(nif);
		vnif.setNombre(name);
		return NIFPost.getInstance(cert, pass).vnifV1(vnif);
		*/
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

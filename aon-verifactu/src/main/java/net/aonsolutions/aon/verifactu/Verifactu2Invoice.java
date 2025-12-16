package net.aonsolutions.aon.verifactu;

import java.util.Date;
import java.util.function.Consumer;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestaconsultalr.RegistroRespuestaConsultaRegFacturacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestaconsultalr.RespuestaDatosRegistroFacturacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestaconsultalr.RespuestaDatosRegistroFacturacionType.Destinatarios;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestaconsultalr.RespuestaDatosRegistroFacturacionType.FacturasRectificadas;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoFacturaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.DesgloseType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.DetalleType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.IDFacturaExpedidaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.IDOtroType;
import net.aonsolutions.aon.verifactu.ClaveRegimen.ClaveRegimenVisitor;
import net.aonsolutions.aon.verifactu.ClaveTipoFactura.ClaveTipoFacturaVisitor;

class Verifactu2Invoice {
	
	private static final String DATE_PATTERN = "dd.MM.yyyy"; 
	
	private Verifactu2Invoice() {
	
	}
	public static Invoice to(RegistroRespuestaConsultaRegFacturacionType verifactu) {
		if ( verifactu == null ) return null;
		Invoice inv = new Invoice();
		REFERENCE_CODE
			.andThen( SERIES_NUMBER )
			.andThen( ISSUE_DATE )
			.andThen( ID )
			.andThen( TYPE )
			.andThen( RECTIFIED_INVOICE )
			.andThen( OPERATION_DATE )
			.andThen( REGISTRY )
			.andThen( DETAILS )
			.andThen( TOTALS )
			.accept( new AltaContext( inv, verifactu ) );
		return inv;
	}
	
	private static record AltaContext(Invoice inv, RegistroRespuestaConsultaRegFacturacionType verifactu) {}
	private static final Consumer<AltaContext> REFERENCE_CODE = c -> {
		IDFacturaExpedidaType idfactu = c.verifactu.getIDFactura();
		if (idfactu != null) {
			String reference = idfactu.getNumSerieFactura();
			c.inv.setReferenceCode( reference );
		}
	};
	
	private static final Consumer<AltaContext> SERIES_NUMBER = c -> {
		IDFacturaExpedidaType idfactu = c.verifactu.getIDFactura();
		if (idfactu != null) {
			String reference = idfactu.getNumSerieFactura();
			if (AonStringUtils.isNotEmpty(reference)) {
				Pair<String, Integer> seriesNumber = parseSeriesNumber(reference);
				c.inv.setSeries( seriesNumber.getLeft() );
				c.inv.setNumber( seriesNumber.getRight()  );
			}				
		}
	};
	
	private static final Consumer<AltaContext> ISSUE_DATE = c -> {
		IDFacturaExpedidaType idfactu = c.verifactu.getIDFactura();
		if (idfactu != null) {
			String expDate = idfactu.getFechaExpedicionFactura(); 
			if (AonStringUtils.isNotEmpty(expDate)) {
				Date ed = AonDateUtils.parse( expDate, DATE_PATTERN );
				c.inv.setIssueDate(ed);
				c.inv.setTaxDate(ed);
				c.inv.ensureFiscal().setExpDate(ed);
			}
		}
	};

	private static final Consumer<AltaContext> ID = c -> {
		RespuestaDatosRegistroFacturacionType datos = c.verifactu.getDatosRegistroFacturacion();
		if (datos != null) {
			String id = datos.getRefExterna();
			if (AonStringUtils.isNumeric( id )) {
				c.inv.setId( AonNumberUtils.toInteger(id) );
			}
		}
	};
		
	private static final Consumer<AltaContext> TYPE = c -> {
		RespuestaDatosRegistroFacturacionType datos = c.verifactu.getDatosRegistroFacturacion();
		if (datos != null) {
			ClaveTipoFacturaType tipoFactura = datos.getTipoFactura();
			if (tipoFactura != null) {
				ClaveTipoFactura ctf = ClaveTipoFactura.from( tipoFactura);
				ctf.visit(new ClaveTipoFacturaVisitor() {

					@Override
					public void visitF_1() {
						c.inv.setType( InvoiceType.SALES );
						c.inv.setRectificationType( RectificationType.NONE);
					}


					@Override
					public void visitR_1() {
						c.inv.setType( InvoiceType.SALES );
						c.inv.setRectificationType( RectificationType.NORMAL_RECTIFIER);
					}

					@Override public void visitF_2() {visitF_1();}
					@Override public void visitR_2() {visitR_1();}
					@Override public void visitR_3() {visitR_1();}
					@Override public void visitR_4() {visitR_1();}
					@Override public void visitR_5() {visitR_1();}
					@Override public void visitF_3() {visitF_1();}
				});
			}
		}
	};
	
	private static final Consumer<AltaContext> RECTIFIED_INVOICE = c -> {
		RespuestaDatosRegistroFacturacionType datos = c.verifactu.getDatosRegistroFacturacion();
		if (datos != null) {
			FacturasRectificadas facturasRectificadas = datos.getFacturasRectificadas();
			if (facturasRectificadas != null) {
				AonCollectionUtils.stream(facturasRectificadas.getIDFacturaRectificada())
					.findFirst()
					.ifPresent( rect -> {
						String date= rect.getFechaExpedicionFactura();
						if (AonStringUtils.isNotEmpty( date )) {
							Date ed = AonDateUtils.parse( date, DATE_PATTERN );
							c.inv.setRectificationInvoiceDate( ed );
						}
						String reference = rect.getNumSerieFactura();
						c.inv.setRectificationInvoiceReference( reference );
						Pair<String, Integer> seriesNumber = parseSeriesNumber(reference);
						c.inv.setRectificationInvoiceSeries( seriesNumber.getLeft() );
						c.inv.setRectificationInvoiceNumber( seriesNumber.getRight()  );
						
					} );
			}
		}
	};
	
	private static final Consumer<AltaContext> OPERATION_DATE = c -> {
		RespuestaDatosRegistroFacturacionType datos = c.verifactu.getDatosRegistroFacturacion();
		if (datos != null) {
			String opDate = datos.getFechaOperacion(); 
			if (AonStringUtils.isNotEmpty(opDate)) {
				Date ed = AonDateUtils.parse( opDate, DATE_PATTERN );
				c.inv.setIssueDate(ed);
			}
		}
	};
	
	private static final Consumer<AltaContext> REGISTRY = c -> {
		RespuestaDatosRegistroFacturacionType datos = c.verifactu.getDatosRegistroFacturacion();
		if (datos != null) {
			Destinatarios destinatarios = datos.getDestinatarios();
			if (destinatarios != null) {
				AonCollectionUtils.stream(destinatarios.getIDDestinatario())
					.findFirst()
					.ifPresent( dest -> {
						String name = dest.getNombreRazon();
						c.inv.setRegistryName( name );
						String nif = dest.getNIF();
						if (AonStringUtils.isNotEmpty( nif )) {
							c.inv.setRegistryDocumentCountry(Country.ES);
							c.inv.setRegistryDocument( nif );
							c.inv.setRegistryDocumentType( DocumentType.identify(nif) );
						} else {
							IDOtroType idOtro = dest.getIDOtro();
							if (idOtro != null) {
								c.inv.setRegistryDocument( idOtro.getID() );
							}
						}
					} );
				return;
			}
		}
	};
	
	private static final Consumer<AltaContext> DETAILS = c -> {
		RespuestaDatosRegistroFacturacionType datos = c.verifactu.getDatosRegistroFacturacion();
		if (datos != null) {
			DesgloseType desglose = datos.getDesglose();
			AonCollectionUtils.stream(desglose.getDetalleDesglose())
				.forEach( det -> {
					ClaveRegimen.safeValueOf( det.getClaveRegimen() )
						.ifPresent( cr -> {
							InvoiceDetail detail = new InvoiceDetail();
							InvoiceTax tax = new InvoiceTax().setTaxType( TaxType.VAT );
							cr.visit( new ClaveRegimenVisitor() {

								private void fillBasic(DetalleType det, InvoiceDetail detail, InvoiceTax tax) {
									double baseImponible = get( det.getBaseImponibleOimporteNoSujeto() );
									double percent = get( det.getTipoImpositivo() );
									double cuotaRepercutida = get( det.getCuotaRepercutida()	);
									
									detail.setQuantity(1.0);
									detail.setPrice(baseImponible);
									detail.setTaxableBase(baseImponible);
									
									tax.setBase( baseImponible );
									tax.setPercentage( percent );
									tax.setQuota( cuotaRepercutida );
								}
								
								// OPERACIÓN DE RÉGIMEN GENERAL. NACIONALES
								@Override
								public void visitC01National() {
									c.inv.setTransaction( InvoiceTransactionType.NATIONAL);
									c.inv.setSurcharge(false);
									c.inv.setWithholdingFarmer(false);
									c.inv.setVatAccrualPayment(false);
									detail.setPrepayment(false);
									fillBasic( det, detail, tax );
									detail.addTax( tax );
									c.inv.addDetail( detail );
								}
								

								// OPERACIÓN DE RÉGIMEN GENERAL. ISP
								@Override
								public void visitC01ISP() {
									c.inv.setTransaction( InvoiceTransactionType.OTHER_ISP);
									c.inv.setSurcharge(false);
									c.inv.setWithholdingFarmer(false);
									c.inv.setVatAccrualPayment(false);
									detail.setPrepayment(false);
									fillBasic( det, detail, tax );
									detail.addTax( tax );
									c.inv.addDetail( detail );
								}
								
								// OPERACIÓN DE RÉGIMEN GENERAL. PRESTACION SERVICIO INTRACOMUNITARIO
								@Override
								public void visitC01IntracommunityService() {
									c.inv.setTransaction( InvoiceTransactionType.INTRACOMMUNITY);
									c.inv.setService(true);
									c.inv.setSurcharge(false);
									c.inv.setWithholdingFarmer(false);
									c.inv.setVatAccrualPayment(false);
									detail.setPrepayment(false);
									fillBasic( det, detail, tax );
									detail.addTax( tax );
									c.inv.addDetail( detail );
								}

								// OPERACIÓN DE RÉGIMEN GENERAL. SUPLIDOS.
								@Override
								public void visitC01Prepayment() {
									c.inv.setTransaction( InvoiceTransactionType.NATIONAL);
									c.inv.setSurcharge(false);
									c.inv.setWithholdingFarmer(false);
									c.inv.setVatAccrualPayment(false);
									detail.setPrepayment(true);
									fillBasic( det, detail, tax );
									c.inv.addDetail( detail );
								}

								/**
								 *	OPERACIÓN DE RÉGIMEN GENERAL. EXENTA E1.
								 *
								 * Art. 20 - Exenciones interiores (en España)
								 * Ciertas actividades no llevan IVA, como:
								 * 	- Educación
								 * 	- Sanidad
								 * 	- Alquiler de vivienda
								 * 	- Servicios financieros y seguros
								 * 	- Actividades sin ánimo de lucro
								 */
								@Override
								public void visitC01ExentaE1() {
									c.inv.setTransaction( InvoiceTransactionType.NATIONAL);
									c.inv.setSurcharge(false);
									c.inv.setWithholdingFarmer(false);
									c.inv.setVatAccrualPayment(false);
									detail.setPrepayment(false);
									fillBasic( det, detail, tax );
									detail.addTax( tax );
									c.inv.addDetail( detail );
								}

							    /**
							     * Art. 25 - Entregas intracomunitarias
							     * 	Vender a empresas en otros países de la UE también está exento de IVA, 
							     * 	si el comprador tiene NIF-IVA intracomunitario y se prueba el envío.	
							     */
								@Override
								public void visitC01ExentaE5() {
									c.inv.setTransaction( InvoiceTransactionType.INTRACOMMUNITY);
									c.inv.setSurcharge(false);
									c.inv.setWithholdingFarmer(false);
									c.inv.setVatAccrualPayment(false);
									detail.setPrepayment(false);
									fillBasic( det, detail, tax );
									detail.addTax( tax );
									c.inv.addDetail( detail );
								}

								// Exportación.
								@Override
								public void visitC02() {
									if (c.inv.getRegistryDocumentCountry() == Country.ES) {
										c.inv.setTransaction( InvoiceTransactionType.CAN_CEU_MEL);
									} else {
										c.inv.setTransaction( InvoiceTransactionType.EXTRACOMMUNITY);
									}
									c.inv.setSurcharge(false);
									c.inv.setWithholdingFarmer(false);
									c.inv.setVatAccrualPayment(false);
									detail.setPrepayment(false);
									fillBasic( det, detail, tax );
									detail.addTax( tax );
									c.inv.addDetail( detail );
								}

								// Régimen especial del criterio de caja.
								@Override
								public void visitC07() {
									c.inv.setTransaction( InvoiceTransactionType.NATIONAL);
									c.inv.setSurcharge(false);
									c.inv.setWithholdingFarmer(false);
									c.inv.setVatAccrualPayment(true);
									detail.setPrepayment(false);
									fillBasic( det, detail, tax );
									detail.addTax( tax );
									c.inv.addDetail( detail );
								}

								// OPERACIÓN DE RÉGIMEN DE RECARGO DE EQUIVALENCIA.
								@Override
								public void visitC18() {
									c.inv.setTransaction( InvoiceTransactionType.NATIONAL);
									c.inv.setSurcharge(true);
									c.inv.setWithholdingFarmer(false);
									c.inv.setVatAccrualPayment(false);
									detail.setPrepayment(false);
									fillBasic( det, detail, tax );
								    double tipoRecargoEquivalencia = get( det.getTipoRecargoEquivalencia() );
								    tax.setSurcharge( tipoRecargoEquivalencia );
								    double cuotaRecargoEquivalencia = get( det.getCuotaRecargoEquivalencia() );
								    tax.setSurchargeQuota( cuotaRecargoEquivalencia );
									detail.addTax( tax );
									c.inv.addDetail( detail );
								}

								// Operaciones de actividades incluidas en el Régimen Especial de Agricultura, Ganadería y Pesca (REAGYP)
								@Override
								public void visitC19() {
//									return false;
								}

								// Régimen simplificado
								@Override
								public void visitC20() {
//									return false;
								}
								
								// Operaciones a las que se aplique el régimen especial de bienes usados, objetos de arte, antigüedades y objetos de colección.
								@Override public void visitC03() { /* Nothing */ }
								// Régimen especial del oro de inversión.
								@Override public void visitC04() { /* Nothing */ }
								// Régimen especial de las agencias de viajes.
								@Override public void visitC05() { /* Nothing */ }
								// Régimen especial grupo de entidades en IVA (Nivel Avanzado)
								@Override public void visitC06() { /* Nothing */ }
								// Operaciones sujetas al IPSI/IGIC (Impuesto sobre la Producción, los Servicios y la Importación/Impuesto General Indirecto Canario).
								@Override public void visitC08() { /* Nothing */ }
								// Facturación de las prestaciones de servicios de agencias de viaje que actúan como mediadoras en nombre y por cuenta ajena (D.A.4.ª RD1619/2012)
								@Override public void visitC09() { /* Nothing */ }
								// Cobros por cuenta de terceros de honorarios profesionales o de derechos derivados de la propiedad industrial, de autor u otros por cuenta de sus socios, asociados o colegiados efectuados por sociedades, asociaciones, colegios profesionales u otras entidades que realicen estas funciones de cobro.
								@Override public void visitC10() { /* Nothing */ }
								// Operaciones de arrendamiento de local de negocio.
								@Override public void visitC11() { /* Nothing */ }
								// Factura con IVA pendiente de devengo en certificaciones de obra cuyo destinatario sea una Administración Pública.
								@Override public void visitC14() { /* Nothing */ }
								// Factura con IVA pendiente de devengo en operaciones de tracto sucesivo.
								@Override public void visitC15() { /* Nothing */ }
								// Operación acogida a alguno de los regímenes previstos en el capítulo XI del título IX (OSS e IOSS)
								@Override public void visitC17() { /* Nothing */ }
									
							});
						});
			});
		}
	};
	
	private static final Consumer<AltaContext> TOTALS = c -> {
		RespuestaDatosRegistroFacturacionType datos = c.verifactu.getDatosRegistroFacturacion();
		if (datos != null) {
			double cuotaTotal = get( datos.getCuotaTotal() );
			c.inv.setVatQuota(cuotaTotal);
			
			double importeTotal = get( datos.getImporteTotal() );
			c.inv.setTotal( importeTotal );
		}
	};

	private static Pair<String, Integer> parseSeriesNumber(String reference) {
		String series = AonStringUtils.substringBeforeLast( reference, "/" );
		String numberStr = AonStringUtils.substringAfterLast( reference, "/" );
		Integer number = null;
		if (AonStringUtils.isNumeric( numberStr )) {
			number = AonNumberUtils.toInteger( numberStr );
		}
		return new Pair<String, Integer>( series, number ); 
	}

	private static double get(String str) {
		if (!AonStringUtils.isNumeric( str )) return 0;
		return AonNumberUtils.toDouble( str );
	}

}


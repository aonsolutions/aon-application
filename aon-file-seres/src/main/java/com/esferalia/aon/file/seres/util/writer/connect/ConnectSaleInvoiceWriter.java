package com.esferalia.aon.file.seres.util.writer.connect;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.config.Tag;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.product.strategy.BasicPriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.enumeration.RegistryItemStatus;
import com.code.aon.registry.enumeration.RegistryMode;
import com.code.aon.sales.SalesDetail;
import com.code.aon.warehouse.DeliveryDetail;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.seres.connect.ConnectInvoice;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.RECTL;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.SINCC;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.SINCD;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.SINCE;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.SINCI;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.SINCL;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.SINCP;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.SINCT;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.SINCU;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.SINCV;
import com.esferalia.aon.file.seres.util.SeresUtils;

public class ConnectSaleInvoiceWriter {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(ConnectSaleInvoiceWriter.class);


	public FileOutput createFile(Invoice invoice, Company company, String companyEdiCode,
			String customerEdiCabeceraCode, String customerEdiPtoEntregaCode, String customerEdiFacturaCode,
			String customerPackage)
			throws FileNotFoundException, UnsupportedEncodingException {

		RECTL rectl = createRECTLRecord(invoice, company, companyEdiCode, customerEdiCabeceraCode,
				customerEdiPtoEntregaCode, customerEdiFacturaCode, customerPackage);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);
		FileFiller filler = new ConnectInvoice(rectl, writer);
		FileOutput output = new FileOutput();
		output.setErrors(filler.create());
		output.setContent(outputStream.toString().getBytes(SeresUtils.DEFAULT_CHARSET_ENC));

		return output;
	}

	private RECTL createRECTLRecord(Invoice invoice, Company company,
			String companyEdiCode, String customerEdiCabeceraCode,
			String customerEdiPtoEntregaCode, String customerEdiFacturaCode, String customerPackage) {

		List<InvoiceDetail> detailList = invoice.getDetailList().stream()
				.map(to -> ((InvoiceDetail) to)).collect(Collectors.toList());
		
		BasicPriceStrategy bps = new BasicPriceStrategy();
		List<TaxBreakDown> taxList = bps.getTaxBreakDowns(invoice, invoice);

		List<Finance> financeList = getFinances(invoice);

		RECTL rectl = new RECTL();
		rectl.setTipoDeMensaje(RECTL.RECTL_2.FACTURA_INVOIC.getValue());
		rectl.setCodigoEmisor(companyEdiCode);
		rectl.setCodigoReceptor(customerEdiCabeceraCode);
		rectl.setIdentificacionDelMensaje(SeresUtils.dateTimeFormat().format(
				new Date()));
		rectl.setFecha_horaDelMensaje(SeresUtils.dateTimeFormat().format(
				new Date()));

		try {
			rectl.sincc = createSINCCRecord(invoice, detailList, companyEdiCode,
					customerEdiCabeceraCode);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		try {
			rectl.sincpList = createSINCPList(invoice, company, companyEdiCode,
					customerEdiCabeceraCode, customerEdiPtoEntregaCode, customerEdiFacturaCode);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		try {
			rectl.sinctList = createSINCTList(invoice, companyEdiCode,
					customerEdiCabeceraCode);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		try {
			rectl.sincvList = createSINCVList(financeList, companyEdiCode,
					customerEdiCabeceraCode);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		try {
			rectl.sincdList = createSINCDList(invoice, detailList);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		try {
			rectl.sinclList = createSINCLList(detailList, companyEdiCode,
					customerEdiCabeceraCode, customerPackage);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		try {
			rectl.sincuList = createSINCUList(detailList);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
//		try {
//			rectl.sinceList = createSINCEList(detailList);
//		} catch (Exception e) {
//			LOGGER.error(e.getMessage());
//		}
		try {
			rectl.sinciList = createSINCIList(taxList, invoice,
					companyEdiCode, customerEdiCabeceraCode);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

		return rectl;
	}

	/**
	 * Cabecera
	 */
	private SINCC createSINCCRecord(Invoice invoice, List<InvoiceDetail> detailList, String companyEdiCode,
			String customerEdiMainCode) {
		List<Finance> financeList = getFinances(invoice);
		SINCC sincc = new SINCC();
//		if(invoice.getType()==InvoiceType.SALES){
//			sincc.setTipoDeFactura_325_380_381_383_385_(SINCC.SINCC_2.NOTA_E_ABONO_381
//					.getValue());
//		} else if(invoice.getType()==InvoiceType.PURCHASE){
//			sincc.setTipoDeFactura_325_380_381_383_385_(SINCC.SINCC_2.NOTA_DE_CARGO_383
//					.getValue());
//		}
		sincc.setTipoDeFactura_325_380_381_383_385_(SINCC.SINCC_2.FACTURA_COMERCIAL_380
				.getValue());
		sincc.setNumeroDeFactura(invoice.getReferenceCode());
		sincc.setFuncionDelMensaje_7_31_5_(null);
		sincc.setFechaDeFactura(Integer.valueOf(SeresUtils.dateFormat().format(invoice
				.getIssueDate())));
		sincc.setFechaDeAlbaran(null);
		sincc.setModoDePago(null);
		sincc.setRazonDeCargoOAbono(null);
		sincc.setCriterioDeModificacion(null);
		sincc.setNumeroDePedido_ON_(obtainSalesNumber(invoice));
		sincc.setNumeroDeAlbaran_DQ_(obtainDeliveryNumber(invoice));
		sincc.setCalificadorDocumentoRectificado_Sustituido(null);
		sincc.setDocumentoRectificado_Sustituido(null);
		sincc.setNumeroDeContrato_acuerdo_CT_(null);
		sincc.setNumeroDeRelacionDeEntrega_REN_(null);
		sincc.setCodigoDeMoneda("EUR");
		if (financeList != null && financeList.size() == 1) {
			sincc.setFechaDeVencimientoUnico(Integer.valueOf(SeresUtils.dateFormat()
					.format(financeList.get(0).getDueDate())));
		}
		sincc.setImporteNetoTotalDeFactura_79_(CommonUtil.round(invoice.getTaxableBase(), 3));
		sincc.setBaseImponible_125_(invoice.getTaxableBase());
		sincc.setImporteBrutoTotalDeFactura_98_(CommonUtil.round(invoice.getTotal(), 3));
		sincc.setImporteTotalDeImpuestos_Tasas_176_(CommonUtil.round(invoice.getVatQuota(), 3));
		sincc.setImporteTotalAPagar_139_(invoice.getTotal());
		sincc.setSubvencionesVinculadasAlPrecio_80A_(null);
		sincc.setTotalIncrementosDelImporteBruto_259_(null);
		sincc.setTotalMinoracionesDelImporteBruto_260_(null);
		sincc.setPeriodoImposicionesFactura_325_(null);
		sincc.setFechaPedido(null);
		sincc.setFecha_horaEfectivaDelServicio_2_(null);
		sincc.setNumeroConfirmacionDeEntrega(null);
		return sincc;
	}
	
	private List<SINCP> createSINCPList(Invoice invoice, Company company,
			String companyEdiCode, String customerEdiCabeceraCode,
			String customerEdiPtoEntregaCode, String customerEdiFacturaCode) {
		Registry customer = invoice.getRegistry();
		RegistryAddress invoiceAddress = invoice.getRegistryAddress();
		RegistryAddress companyAddress = null;
		try {
			companyAddress = company.getRegistry().getDefaultAddress();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}
		RecordData recordData = null;
		try {
			IManagerBean recordDataBean = BeanManager.getManagerBean(RecordData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(recordDataBean.getFieldName(IEntityAlias.RECORD_DATA_REGISTRY_ID), company.getId());
			Iterator<ITransferObject> iter = recordDataBean.getList(criteria, 0, 1).iterator();
			if(iter.hasNext()){
				recordData = (RecordData)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}
		
		List<SINCP> list = new ArrayList<>();
		list.add(createSINCPRecord(SINCP.SINCP_2.PROVEEDOR__SU,
				companyEdiCode, company, companyAddress, recordData));
		list.add(createSINCPRecord(SINCP.SINCP_2.EMISOR_DE_UNA_FACTURA__QUIEN_FACTURA__II,
				companyEdiCode, company, companyAddress, recordData));
		list.add(createSINCPRecord(SINCP.SINCP_2.PUNTO_DESTINO_DE_LA_MERCANCIA_DP,
				customerEdiPtoEntregaCode, customer, invoiceAddress, null));
		list.add(createSINCPRecord(SINCP.SINCP_2.DESTINATARIO_FINAL_UC,
				customerEdiCabeceraCode, customer, invoiceAddress, null));
		list.add(createSINCPRecord(SINCP.SINCP_2.COMPRADOR_BY,
				customerEdiFacturaCode, customer, invoiceAddress, null));
		list.add(createSINCPRecord(SINCP.SINCP_2.A_QUIEN_SE_FACTURA_IV,
				customerEdiCabeceraCode, customer, invoiceAddress, null));
		list.add(createSINCPRecord(SINCP.SINCP_2.SUJETO_DEL_PAGO__A_QUIEN_SE_PAGA__PE,
				companyEdiCode, company, companyAddress, recordData));
		list.add(createSINCPRecord(SINCP.SINCP_2.PAGADOR__QUIEN_PAGA__PR,
				customerEdiCabeceraCode, customer, invoiceAddress, null));
		list.add(createSINCPRecord(SINCP.SINCP_2.EMISOR_DEL_MENSAJE_MS,
				companyEdiCode, company, companyAddress, recordData));
		list.add(createSINCPRecord(SINCP.SINCP_2.RECEPTOR_DEL_MENSAJE_MR,
				customerEdiCabeceraCode, customer, invoiceAddress, null));

		return list;
	}
	
	private List<SINCT> createSINCTList(Invoice invoice, String companyEdiCode,
			String customerEdiMainCode) {
		List<SINCT> list = new ArrayList<>();
		if(StringUtils.isNotBlank(invoice.getComments())){
			list.add(createSINCTRecord(invoice, companyEdiCode, customerEdiMainCode));
		}
		return list;
	}

	private List<SINCV> createSINCVList(List<Finance> financeList,
			String companyEdiCode, String customerEdiMainCode) {
		List<SINCV> list = new ArrayList<>();
		if (financeList != null && financeList.size() > 1) {
			financeList.forEach(finance -> {
				list.add(createSINCVRecord(finance,
						financeList.indexOf(finance), companyEdiCode,
						customerEdiMainCode));
			});
		}
		return list;
	}

	private List<SINCD> createSINCDList(Invoice invoice, List<InvoiceDetail> detailList) {
		List<SINCD> list = new ArrayList<>();
		detailList.forEach(detail -> {
			SINCD r = createSINCDRecord(invoice, detail, list.size()+1);
			if(r!=null){
				list.add(r);
			}
		});
		return list;
	}

	private List<SINCL> createSINCLList(List<InvoiceDetail> detailList,
			String companyEdiCode, String customerEdiMainCode, String customerPackage) {
		List<SINCL> list = new ArrayList<>();
		detailList.forEach(detail -> {
			if(detail.getTaxableBase()!=0.0){
				list.add(createSINCLRecord(detail, detailList.indexOf(detail)+1,
						companyEdiCode, customerEdiMainCode, customerPackage));
			}
		});
		return list;
	}

	private List<SINCU> createSINCUList(List<InvoiceDetail> detailList) {
		List<SINCU> list = new ArrayList<>();
		detailList.forEach(detail -> {
			SINCU r = createSINCURecord(detail, list.size()+1);
			if(r!=null){
				list.add(r);
			}
		});
		return list;
	}

	private List<SINCE> createSINCEList(List<InvoiceDetail> detailList) {
		List<SINCE> list = new ArrayList<>();
		detailList.forEach(detail -> {
			if(detail.getDiscountExpression()!=null
					&& detail.getDiscountExpression().getDiscountExpr()!=null
					&& detail.getDiscountExpression().getDiscounts().length>0
					&& !detail.getDiscountExpression().getDiscountExpr().equals("0.0")
					&& !detail.getDiscountExpression().getDiscountExpr().equals("0")){
				SINCE r = createSINCERecord(detail, list.size()+1);
				if(r!=null){
					list.add(r);
				}
			}
		});
		return list;
	}

	private List<SINCI> createSINCIList(List<TaxBreakDown> taxList,
			Invoice invoice, String companyEdiCode, String customerEdiMainCode) {
		List<SINCI> list = new ArrayList<>();
		for (TaxBreakDown tax : taxList) {
			SINCI sinci = createSINCIRecord(tax,
					list.size()+1, invoice,
					companyEdiCode, customerEdiMainCode);
			if(sinci!=null)
				list.add(sinci);
		}
		return list;
	}

	/**
	 * Información partes involucradas
	 */
	private SINCP createSINCPRecord(SINCP.SINCP_2 type, String ediCode, Registry registry,
			RegistryAddress rAddress, RecordData recordData) {
		SINCP sincp = new SINCP();
		sincp.setCalificadorDelInterlocutor(type.getValue());
		sincp.setCodigoInterlocutor(ediCode);
		sincp.setTipoInterlocutor_J_Persa_Juridica_F_Persa_Fisica_9_EDI_(SINCP.SINCP_41.CODIGO_EAN_9.getValue());
		sincp.setNombre1(null);
		sincp.setNombre1(registry.getRegistry().getName());
		sincp.setNombre2(null);
		sincp.setNombre3(null);
		sincp.setNombre4(null);
		sincp.setNombre5(null);
		try {
			sincp.setDireccion1_Calle_Numero_(rAddress.getAddress());
			sincp.setDireccion2_Calle_Numero_(rAddress.getAddress2());
			sincp.setDireccion3_Calle_Numero_(rAddress.getAddress3());
			sincp.setDireccion4_Calle_Numero_(rAddress.getNumber());
			sincp.setCiudad(rAddress.getCity());
			sincp.setCodigoPostal(rAddress.getZip());
			sincp.setProvincia(rAddress.getGeozone().getName());
			sincp.setCodigoPais(rAddress.getGeozone().getGeoZoneCountry().getCode());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		sincp.setNumeroDeIdentificacionFiscal(registry.getDocument());
		sincp.setCodigoAdicional(null);
		sincp.setFuncionDeContacto(null);
		sincp.setCodigoDepartamentoOEmpleado(null);
		sincp.setNombreDepartamentoOEmpleado(null);
		sincp.setTelefono(null);
		sincp.setFax(null);
		sincp.setNumeroDeCuentaBancaria_IBAN_(null);
		sincp.setRegistroMercantilDelEmisor(getRecordDataValue(recordData));
		sincp.setCapitalSocial(null);
		sincp.setCalificadorReferenciaAdicional(null);
		sincp.setReferenciaAdicional(null);
		return sincp;
	}
	
	private String getRecordDataValue(RecordData recordData) {
		if(recordData!=null){
			StringBuilder builder = new StringBuilder("");
			if(StringUtils.isNotBlank(recordData.getVolume())){
				builder.append(builder.length()>0?", ":"");
				builder.append("Tom. ");
				builder.append(recordData.getVolume());
			}
			if(StringUtils.isNotBlank(recordData.getSection())){
				builder.append(builder.length()>0?", ":"");
				builder.append("Sec. ");
				builder.append(recordData.getSection());
			}
			if(StringUtils.isNotBlank(recordData.getPage())){
				builder.append(builder.length()>0?", ":"");
				builder.append("Fol. ");
				builder.append(recordData.getPage());
			}
			if(StringUtils.isNotBlank(recordData.getSheet())){
				builder.append(builder.length()>0?", ":"");
				builder.append("Hoj. ");
				builder.append(recordData.getSheet());
			}
			if(recordData.getRecordDate()!=null){
				builder.append(" con fecha ");
				builder.append(new SimpleDateFormat("dd/MM/yyyy").format(recordData.getRecordDate()));
			}
			if(StringUtils.isNotBlank(recordData.getRegistration())){
				builder.append(builder.length()>0?", ":"");
				builder.append(recordData.getRegistration());
			}
			return builder.toString();
		}
		return null;
	}

	/**
	 * Observaciones cabecera
	 */
	private SINCT createSINCTRecord(Invoice invoice, String companyEdiCode,
			String customerEdiMainCode) {
		SINCT sinct = new SINCT();
		sinct.setCalificadorDelTemaDeTexto(SINCT.SINCT_2.INFORMACION_GENERAL_AAI
				.getValue());
		String[] comments = invoice.getComments().split("(?<=\\G.{70})");
		sinct.setTexto1(comments.length > 0 ? comments[0] : null);
		sinct.setTexto2(comments.length > 1 ? comments[1] : null);
		sinct.setTexto3(comments.length > 2 ? comments[2] : null);
		sinct.setTexto4(comments.length > 3 ? comments[3] : null);
		sinct.setTexto5(comments.length > 4 ? comments[4] : null);
		return sinct;
	}

	/**
	 * Vencimientos
	 */
	private SINCV createSINCVRecord(Finance finance, int lineNumber,
			String companyEdiCode, String customerEdiMainCode) {
		SINCV sincv = new SINCV();
		sincv.setNumeroDeVencimientos(lineNumber);
		sincv.setFechaDeVencimiento(Integer.valueOf(SeresUtils.dateFormat().format(finance
				.getDueDate())));
		sincv.setImporteSujetoAlVencimiento(finance.getTotalAmount());
		return sincv;
	}

	/**
	 * Descuentos y cargos cabecera
	 */
	private SINCD createSINCDRecord(Invoice invoice, InvoiceDetail detail, int lineNumber) {
		// TODO createSINCDRecord
		return null;
	}

	/**
	 * Línea detalle
	 */
	private SINCL createSINCLRecord(InvoiceDetail detail, int lineNumber,
			String companyEdiCode, String customerEdiMainCode, String customerPackage) {
		detail.fillTaxDataInDetail();
		Item item = detail.getItem();
		Integer customerId = detail.getInvoice().getRegistry().getId();
		RegistryItem rItem = obtainCustomerItem(item.getProduct(), customerId);
		String productCode = null;
		try {
			productCode = rItem!=null?rItem.getCode():item.getProduct().getBaseItem().getBarcode();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}
		if (productCode==null) {
			productCode = item.getProduct().getCode();
		}
		
		SINCL sincl = new SINCL();
		sincl.setNumeroDeLinea(lineNumber);
		sincl.setCodigoArticulo(productCode);
		sincl.setDescripcionDelArticulo(detail.getItem().getProduct().getName());
		if (detail.getItem().getProduct().getType() == ProductType.SERVICE) {
			 sincl.setTipoArticulo(SINCL.SINCL_5.SERVICIO_S.getValue());
		} else {
			sincl.setTipoArticulo(SINCL.SINCL_5.MERCANCIA_M.getValue());
		}
		sincl.setCodigoInternoArticuloProveedor_SA_(productCode);
		sincl.setCodigoInternoArticuloCliente_IN_(null);
		sincl.setCodigoVariablePromocional_PV_(null);
		sincl.setCodigoUnidadDeExpedicion_EN_(null);
		sincl.setNumeroDeLote_BN_(detail.getItem().getSerialNumber());
		
		double unitQuantity = obtainPackageQuantity(detail, customerPackage);
		double unitPriceFactor = detail.getQuantity() / unitQuantity;
		sincl.setCantidadFacturada_47_(unitQuantity);
		sincl.setCantidadBonificada_15E_(null);
		sincl.setUnidadDeMedida(null);
		sincl.setUnidadesEntregadas(null);
		sincl.setNumeroUnidadesDeConsumoEnU_Expedicion(null);
		sincl.setImporteTotalNetoDeLaLineaDeArticulo(CommonUtil.round(detail
				.getTaxableBase(), 3));
		sincl.setPrecioBrutoUnitario(CommonUtil.round(detail.getPrice()*unitPriceFactor, 4));
		sincl.setPrecioNetoUnitario(CommonUtil.round(detail.getPrice()*unitPriceFactor, 4));
		sincl.setUnidadDeMedidaDelPrecio(null);
		sincl.setCalificadorIVA_IGIG(SINCL.SINCL_20.IVA_VAT.getValue());
		sincl.setPorcentajeImpuestoIVA_IGIG(CommonUtil.round(detail.getVatPercent()));
		if (detail.getVatQuota() == 0 ){
			sincl.setImporteImpuestoIVA_IGIG(
					CommonUtil.round(detail.getTaxableBase() * (detail.getVatPercent() / 100), 3));
		} else {
			sincl.setImporteImpuestoIVA_IGIG(CommonUtil.round(detail.getVatQuota(), 3));
		}
		sincl.setPorcentajeRecargoDeEquivalencia(CommonUtil.round(detail.getRetentionPercent()));
		if (detail.getRetentionQuota() == 0) {
			sincl.setImporteRecargoDeEquivalencia(
					CommonUtil.round(detail.getTaxableBase() * (detail.getRetentionPercent() / 100), 3));
		} else {
			sincl.setImporteRecargoDeEquivalencia(CommonUtil.round(detail.getRetentionQuota(), 3));
		}
		sincl.setCalificadorOtroTipoDeImpuesto(null);
		sincl.setPorcentajeOtroTipoDeImpuesto(null);
		sincl.setImporteOtroTipoDeImpuesto(null);
		sincl.setNumeroPedido_ON_(obtainSalesNumber(detail));
		sincl.setNumeroDeAlbaran_DQ_(obtainDeliveryNumber(detail));
		sincl.setNumeroDeEmbalajes(null);
		sincl.setTipoDeEmbalaje(null);
		sincl.setImporteTotalBrutoDeLaLineaDeDetalle(CommonUtil.round(detail.getTotalSalesPrice(), 3));
		sincl.setNumeroDeLineaSuperior(null);
		sincl.setNumeroDeLineaDelPedido_ON_(null);
		sincl.setUnidadBasePrecio(null);
		sincl.setIdentificadorProducto_lineaPedido_MP_(null);
		sincl.setCategoriaProducto_GB_(null);
		sincl.setNumeroArticuloFabricante_MF_(null);
		sincl.setNumeroConfirmacionDeEntrega(null);
		sincl.setNumeroDeLineaConfirmacionDeEntrega(null);
		sincl.setFechaPedido_ON_171_(null);
		sincl.setFechaAlbaran_DQ_171_(null);
		
		if(detail.getDiscountExpression()!=null
				&& detail.getDiscountExpression().getDiscountExpr()!=null
				&& detail.getDiscountExpression().getDiscounts().length>0
				&& !detail.getDiscountExpression().getDiscountExpr().equals("0.0")
				&& !detail.getDiscountExpression().getDiscountExpr().equals("0")){
			sincl.since = createSINCERecord(detail, 1);
		}
		
		return sincl;
	}

	/**
	 * Observaciones de línea de detalle
	 */
	private SINCU createSINCURecord(InvoiceDetail detail, int lineNumber) {
		// TODO createSINCURecord
		return null;
	}

	/**
	 * Descuentos y cargos línea de detalle
	 */
	private SINCE createSINCERecord(InvoiceDetail detail, int lineNumber) {
		Double rawAmount = detail.getQuantity()*detail.getPrice();
		Double discountAmount = Double.valueOf(detail.getDiscountExpression().getDiscountExpr());
		SINCE record = new SINCE();
		record.setNumeroDeDescuento_Cargo(lineNumber);
		record.setIndicadorDeDescuento_Cargo("A");
		record.setIndicadorSecuenciaDeCalculo("1");
		record.setPorcentajeDescuento_Cargo(CommonUtil.round(discountAmount, 4));
		record.setImporteDescuento_Cargo(CommonUtil.round(rawAmount*(discountAmount/100), 3));
		record.setImporteTotalSujetoAAplicacion(rawAmount);
		record.setCantidadDeUnidadesQueSeDescuentanPorLinea(0.0);
		record.setTipoDescuento("TD");
		record.setDescuentosMonetariosPorUnidad(0.0);
		record.setUnidadDeMedida(null);
		record.setDescripcionDescuento_Cargo(null);
		return record;
	}

	/**
	 * Impuestos
	 */
	private SINCI createSINCIRecord(TaxBreakDown tax, int lineNumber,
			Invoice invoice, String companyEdiCode, String customerEdiMainCode) {
		SINCI sinci = null;		
		if(tax.getBase()>0.0) {
			sinci = new SINCI();
			sinci.setNumeroDeLineaDeImpuesto(lineNumber);
			if(tax.getTaxType()==TaxType.VAT){
				sinci.setCalificadorTipoDeImpuesto(SINCI.SINCI_3.IVA_VAT.getValue());
			}
			sinci.setPorcentajeTipoDeImpuesto(CommonUtil.round(tax.getTaxPercent()));
			sinci.setImporteTipoDeImpuesto(CommonUtil.round(tax.getBase() * tax.getTaxPercent()
					/ 100, 3));
			sinci.setBaseImponible(tax.getBase());
		}
		return sinci;
	}
	
	

	///////////////////////////////////////////////
	///////////////////////////////////////////////
	///////////////////////////////////////////////
	
	private String obtainSalesNumber(Invoice invoice) {
		List<InvoiceDetail> list = invoice.getDetailList().stream()
				.map(to -> ((InvoiceDetail) to)).collect(Collectors.toList());
		if (!list.isEmpty()) {
			InvoiceDetail detail = list.get(0);
			return obtainSalesNumber(detail);
		}
		return null;
	}

	private String obtainSalesNumber(InvoiceDetail invoiceDetail) {
		try {
			if (invoiceDetail.getSource() == InvoiceSource.DELIVERY) {
				IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
				DeliveryDetail deliveryDetail = (DeliveryDetail)deliveryDetailBean.get(invoiceDetail.getSourceId());
				if (deliveryDetail != null && deliveryDetail.getId()!=null) {
					if (deliveryDetail.getSalesDetail()!=null) {
						SalesDetail salesDetail = deliveryDetail.getSalesDetail();
						return salesDetail.getSales().getPurchaseReference();
					}
				}
				return null;
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	private String obtainDeliveryNumber(Invoice invoice) {
		List<InvoiceDetail> list = invoice.getDetailList().stream()
				.map(to -> ((InvoiceDetail) to)).collect(Collectors.toList());
		if (!list.isEmpty()) {
			InvoiceDetail detail = list.get(0);
			return obtainDeliveryNumber(detail);
		}
		return null;
	}

	private String obtainDeliveryNumber(InvoiceDetail invoiceDetail) {
		try {
			if (invoiceDetail.getSource() == InvoiceSource.DELIVERY) {
				IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
				DeliveryDetail deliveryDetail = (DeliveryDetail)deliveryDetailBean.get(invoiceDetail.getSourceId());
				return deliveryDetail.getDelivery().getSeries() + "/" + deliveryDetail.getDelivery().getNumber();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	private List<Finance> getFinances(Invoice invoice) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Finance.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					bean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID),
					invoice.getId());
			return bean.getList(criteria).stream().map(to -> ((Finance) to))
					.collect(Collectors.toList());
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	
	private RegistryItem obtainCustomerItem(Product product, Integer customerId) {
		try {
			Item baseItem = product.getBaseItem();
			IManagerBean bean = BeanManager.getManagerBean(RegistryItem.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					bean.getFieldName(IEntityAlias.REGISTRY_ITEM_REGISTRY_ID),
					customerId);
			criteria.addEqualExpression(
					bean.getFieldName(IEntityAlias.REGISTRY_ITEM_ITEM_ID),
					baseItem.getId());
			criteria.addEqualExpression(
					bean.getFieldName(IEntityAlias.REGISTRY_ITEM_TYPE),
					RegistryMode.CUSTOMER);
			criteria.addEqualExpression(
					bean.getFieldName(IEntityAlias.REGISTRY_ITEM_STATUS),
					RegistryItemStatus.ACTIVE);
			List<RegistryItem> rItemList = bean.getList(criteria).stream()
					.map(to -> ((RegistryItem) to)).collect(Collectors.toList());

			if (rItemList != null && !rItemList.isEmpty()) {
				return rItemList.get(0);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	
	private Double obtainPackageQuantity(InvoiceDetail detail,
			String customerPackingTag) {
		if(detail!=null && customerPackingTag!=null){
			Item item = detail.getItem();
			Double quantity = detail.getQuantity();
			Tag itemPackFormatTag = item.getPackFormatTag();
			Tag itemPackMeasurementTag = item.getPackMeasurementTag();
			Tag itemPackingTag = item.getPackUnitsTag();
			double itemPackMeasurement = item.getPackMeasurement();
			int itemPackUnits = item.getPackUnits();
			if (customerPackingTag != null && itemPackingTag != null
					&& itemPackFormatTag != null && itemPackMeasurementTag != null) {
				if (customerPackingTag.equals(itemPackMeasurementTag.getName())) {
					return quantity;
				} else if (customerPackingTag.equals(
						itemPackingTag.getName())) {
					return quantity / itemPackMeasurement;
				} else if (customerPackingTag.equals(
						itemPackFormatTag.getName())) {
					return (quantity / itemPackMeasurement) / itemPackUnits;
				}
			}
			return quantity;
		}
		return null;
	}

}

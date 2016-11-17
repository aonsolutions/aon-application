package com.esferalia.aon.file.seres.util.writer.udapa;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.sales.Sales;
import com.code.aon.warehouse.Delivery;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.seres.udapa.UdapaInvoice;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCC;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCD;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCE;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCI;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCL;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCT;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCT.F4451C;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCU;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCV;

public class UdapaSaleInvoiceWriter {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(UdapaSaleInvoiceWriter.class);

	public static final String CHARSET_ENCODING = "ISO-8859-1";
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

	public FileOutput createFile(Invoice invoice, IPriceStrategy priceStrategy,
			String companyEdiCode, String customerEdiMainCode,
			String customerEdiOperationCode) throws FileNotFoundException,
			UnsupportedEncodingException {
		SINCC sincc = createSINCCRecord(invoice, priceStrategy, companyEdiCode,
				customerEdiMainCode, customerEdiOperationCode);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);
		FileFiller filler = new UdapaInvoice(sincc, writer);
		FileOutput output = new FileOutput();
		output.setErrors(filler.create());
		output.setContent(outputStream.toString().getBytes(CHARSET_ENCODING));
		return output;
	}

	private SINCC createSINCCRecord(Invoice invoice,
			IPriceStrategy priceStrategy, String companyEdiCode,
			String customerEdiMainCode, String customerEdiOperationCode) {
		List<Finance> financeList = getFinances(invoice);

		SINCC sincc = new SINCC();
		sincc.setTipoFactura_325_380_381_383_385_(SINCC.F1001T.FACTURA_COMERCIA_380
				.getValue());
		sincc.setNumeroDeFactura(invoice.getReferenceCode());
		sincc.setCodigoVendedor_aQuienSePide__SU_(companyEdiCode);
		sincc.setCodigoComprador_QuienPide__BY_(customerEdiMainCode);
		sincc.setFuncionDelMensaje_7_31_5_(null);
		sincc.setFechaFactura(Integer.valueOf(formatter.format(invoice
				.getIssueDate())));
		sincc.setPeriodoDeFacturacion(null);
		sincc.setFormaDePago(null);
		sincc.setCodigoEmisorDeLaFactura_QuienFactura__II_(companyEdiCode);
		sincc.setCodigoReceptorDeLaFactura_aQuienSeFactura_(customerEdiMainCode);
		sincc.setCodigoReceptorDeLasMercancias_QuienRecibe_(customerEdiOperationCode);
		sincc.setCodigoReceptorDelPago_aQuienSePaga_(null);
		sincc.setCodigoEmisorDelPago_QuienPaga_(null);
		sincc.setRazonDelCargoODelAbono(null);
		sincc.setNumeroDePedido_ON_(obtainSalesNumber(invoice));
		sincc.setNumeroDeAlbaran_DQ_(obtainDeliveryNumber(invoice));
		sincc.setCalificadorDocumentoRectificado_Sustituido(null);
		sincc.setNumeroDocumentoRectificado_Sustituido(null);
		sincc.setNumeroDeContrato_Acuerdo_CT_(null);
		sincc.setNumeroDeRelacionDeEntregas_REN_(null);
		sincc.setRazonSocialReceptorDeLaFactura(StringUtils.abbreviate(invoice
				.getRegistry().getFullName(), 70));
		RegistryAddress rAddress = invoice.getRegistryAddress();
		sincc.setNombre_NumeroDeLaCalleDelReceptorDeLaFactura(StringUtils
				.abbreviate(rAddress.getFullAddress(), 70));
		sincc.setPoblacionDelReceptorDeLaFactura(StringUtils.abbreviate(
				rAddress.getCity(), 35));
		sincc.setCodigoPostalDelReceptorDeLaFactura(rAddress.getZip() == null
				|| rAddress.getZip().length() <= 9 ? rAddress.getZip()
				: rAddress.getZip().substring(0, 9));
		sincc.setNifDelReceptorDeLaFactura(invoice.getRegistryDocument());
		sincc.setNombre_NumeroDeLaCalleDelEmisorDeLaFactura(null);
		sincc.setPoblacionDelEmisorDeLaFactura(null);
		sincc.setCodigoPostalDelEmisorDeLaFactura(null);
		sincc.setCodigoDeMoneda(null);
		if (financeList != null && financeList.size() == 1) {
			sincc.setFechaVencimientoUnico(Integer.valueOf(formatter
					.format(financeList.get(0).getDueDate())));
		}
		sincc.setImporteNetoTotalFactura_79_(invoice.getTotal());
		sincc.setBaseImponible_125_(invoice.getTaxableBase());
		sincc.setImporteBrutoTotalFactura_98_(null);
		sincc.setImporteTotalDeImpuestos_176_(invoice.getVatQuota());
		sincc.setImporteTotalAPagar_139_(invoice.getTotal());
		sincc.setSubvencionesVinculadasAlPrecio_80A_(null);
		sincc.setTotalIncrementosDelImporteBruto_259_(null);
		sincc.setTotalMinoracionesDelImporteBruto_260_(null);
		sincc.setIdentificacionAdicionalDeLaParte_API_(null);
		sincc.setReceptorDelDocumento(null);
		sincc.setIdentificacionAdicionalProveedor_API__NAD_SU_(null);

		sincc.sinctList = createSINCTList(invoice, companyEdiCode,
				customerEdiMainCode);
		sincc.sincvList = createSINCVList(financeList, companyEdiCode,
				customerEdiMainCode);
		sincc.sincdList = createSINCDList(invoice);
		sincc.sinclList = createSINCLList(
				invoice.getDetailList().stream()
						.map(to -> ((InvoiceDetail) to))
						.collect(Collectors.toList()), companyEdiCode,
				customerEdiMainCode);
		sincc.sincuList = createSINCUList(invoice);
		sincc.sinceList = createSINCEList(invoice);
		sincc.sinciList = createSINCIList(
				invoice.getDetailList().stream()
						.map(to -> ((InvoiceDetail) to))
						.collect(Collectors.toList()), invoice, companyEdiCode,
				customerEdiMainCode);

		return sincc;
	}

	private List<SINCT> createSINCTList(Invoice invoice, String companyEdiCode,
			String customerEdiMainCode) {
		List<SINCT> list = new ArrayList<>();
		list.add(createSINCTRecord(invoice, companyEdiCode, customerEdiMainCode));
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

	// TODO createSINCDList
	private List<SINCD> createSINCDList(Invoice invoice) {
		List<SINCD> list = new ArrayList<>();
		return list;
	}

	private List<SINCL> createSINCLList(List<InvoiceDetail> detailList,
			String companyEdiCode, String customerEdiMainCode) {
		List<SINCL> list = new ArrayList<>();
		detailList.forEach(detail -> {
			list.add(createSINCLRecord(detail, detailList.indexOf(detail),
					companyEdiCode, customerEdiMainCode));
		});
		return list;
	}

	// TODO createSINCUList
	private List<SINCU> createSINCUList(Invoice invoice) {
		List<SINCU> list = new ArrayList<>();
		return list;
	}

	// TODO createSINCEList
	private List<SINCE> createSINCEList(Invoice invoice) {
		List<SINCE> list = new ArrayList<>();
		return list;
	}

	private List<SINCI> createSINCIList(List<InvoiceDetail> detailList,
			Invoice invoice, String companyEdiCode, String customerEdiMainCode) {
		List<SINCI> list = new ArrayList<>();
		for (InvoiceDetail detail : detailList) {
			detail.getTaxBreakDowns().forEach(
					tax -> {
						list.add(createSINCIRecord(tax,
								detailList.indexOf(detail), invoice,
								companyEdiCode, customerEdiMainCode));
					});
		}
		return list;
	}

	private SINCT createSINCTRecord(Invoice invoice, String companyEdiCode,
			String customerEdiMainCode) {
		SINCT sinct = new SINCT();
		sinct.setTipoFactura_325_380_381_383_385_(SINCC.F1001T.FACTURA_COMERCIA_380
				.getValue());
		sinct.setCalificadorDelTemaDelTexto(F4451C.INFORMACION_GENERA_AAI
				.getValue());
		sinct.setCodigoComprador_BY_(customerEdiMainCode);
		sinct.setCodigoVendedor_SU_(companyEdiCode);
		sinct.setNumeroDeFactura(invoice.getReferenceCode());
		sinct.setNumeroDeLinea(0);
		String[] comments = invoice.getComments().split("(?<=\\G.{70})");
		sinct.setTexto1(comments.length > 0 ? comments[0] : null);
		sinct.setTexto2(comments.length > 1 ? comments[1] : null);
		sinct.setTexto3(comments.length > 2 ? comments[2] : null);
		sinct.setTexto4(comments.length > 3 ? comments[3] : null);
		sinct.setTexto5(comments.length > 4 ? comments[4] : null);
		return sinct;
	}

	private SINCV createSINCVRecord(Finance finance, int lineNumber,
			String companyEdiCode, String customerEdiMainCode) {
		SINCV sincv = new SINCV();
		sincv.setTipoFactura_325_380_381_383_385_(SINCC.F1001T.FACTURA_COMERCIA_380
				.getValue());
		sincv.setNumeroDeFactura(finance.getInvoice().getReferenceCode());
		sincv.setCodigoVendedor_SU_(companyEdiCode);
		sincv.setCodigoComprador_BY_(customerEdiMainCode);
		sincv.setNumeroDeVencimiento(lineNumber);
		sincv.setFechaVencimiento(Integer.valueOf(formatter.format(finance
				.getDueDate())));
		sincv.setImporteSujetoAlVencimiento(finance.getTotalAmount());
		return sincv;
	}

	// TODO createSINCDRecord
	private SINCD createSINCDRecord(Invoice invoice) {
		return null;
	}

	private SINCL createSINCLRecord(InvoiceDetail detail, int lineNumber,
			String companyEdiCode, String customerEdiMainCode) {
		SINCL sincl = new SINCL();
		sincl.setTipoFactura_325_380_381_383_385_(SINCC.F1001T.FACTURA_COMERCIA_380
				.getValue());
		sincl.setNumeroDeFactura(detail.getInvoice().getReferenceCode());
		sincl.setCodigoVendedor_SU_(companyEdiCode);
		sincl.setCodigoComprador_BY_(customerEdiMainCode);
		sincl.setNumeroDeLinea(lineNumber);
		sincl.setCodigoArticulo(detail.getItem().getProduct().getCode());
		sincl.setDescripcionDelArticulo(detail.getItem().getProduct().getName());
		// TODO enum SINCL.F7081A
		if (detail.getItem().getProduct().getType() == ProductType.SERVICE) {
			// sincl.setTipoArticulo(SINCL.F7081A.S);
			sincl.setTipoArticulo("S");
		} else {
			// sincl.setTipoArticulo(SINCL.F7081A.M);
			sincl.setTipoArticulo("M");
		}
		sincl.setCodigoInternoArticuloProveedor_SA_(null);
		sincl.setCodigoInternoArticuloCliente_IN_(null);
		sincl.setCodigoVariablePromocional_PV_(null);
		sincl.setCodigoUnidadDeExpedicion_EN_(null);
		sincl.setNumeroDeLote_BN_(detail.getItem().getSerialNumber());
		sincl.setCantidadFacturada_47_(detail.getQuantity());
		sincl.setCantidadBonificada_15E_(null);
		sincl.setUnidadDeMedida(null);
		sincl.setUnidadesEntregadas(null);
		sincl.setNumeroUnidadesDeConsumoEnU_Expedicion_59_(null);
		sincl.setImporteTotalNetoDeLaLineaDeArticulo(detail
				.getTotalSalesPrice());
		sincl.setPrecioBrutoUnitario(detail.getPrice());
		sincl.setPrecioNetoUnitario(null);
		sincl.setUnidadDeMedidaDelPrecio(null);
		sincl.setCalificadorIVA_IGIG(SINCL.F5153I.IV_VAT.getValue());
		sincl.setPorcentajeImpuestoIVA_IGIG(detail.getVatPercent());
		sincl.setImporteImpuestoIVA_IGIG(detail.getVatQuota());
		sincl.setPorcentajeRecargoDeEquivalencia(detail.getRetentionPercent());
		sincl.setImporteRecargoDeEquivalencia(detail.getSurchargeQuota());
		sincl.setCalificadorOtroTipoDeImpuesto(null);
		sincl.setPorcentajeOtroTipoDeImpuesto(null);
		sincl.setImporteOtroTipoDeImpuesto(null);
		sincl.setNumeroPedido_ON_(null);
		sincl.setNumeroDeAlbaran_DQ_(null);
		sincl.setNumeroDeEmbalajes(null);
		sincl.setTipoDeEmbalaje(null);
		sincl.setImporteTotalBrutoDeLaLineaDeArticulo_98_(null);
		return sincl;
	}

	// TODO createSINCURecord
	private SINCU createSINCURecord(Invoice invoice) {
		return null;
	}

	// TODO createSINCERecord
	private SINCE createSINCERecord(Invoice invoice) {
		return null;
	}

	private SINCI createSINCIRecord(TaxBreakDown tax, int lineNumber,
			Invoice invoice, String companyEdiCode, String customerEdiMainCode) {
		SINCI sinci = new SINCI();
		sinci.setTipoFactura_325_380_381_383_385_(SINCC.F1001T.FACTURA_COMERCIA_380
				.getValue());
		sinci.setNumeroDeFactura(invoice.getReferenceCode());
		sinci.setCodigoVendedor_SU_(companyEdiCode);
		sinci.setCodigoComprador_BY_(customerEdiMainCode);
		sinci.setNumeroDeLineaImpuesto(lineNumber);
		sinci.setCalificadorTipoDeImpuesto(SINCI.F5153T.IV_VAT.getValue());
		sinci.setBaseImponible(tax.getBase());
		sinci.setPorcentajeTipoDeImpuesto(tax.getTaxPercent());
		sinci.setImporteTipoDeImpuesto(tax.getBase() * tax.getTaxPercent()
				/ 100);
		return sinci;
	}

	private String obtainSalesNumber(Invoice invoice) {
		List<InvoiceDetail> list = invoice.getDetailList().stream()
				.map(to -> ((InvoiceDetail) to)).collect(Collectors.toList());
		try {
			if (!list.isEmpty()) {
				InvoiceDetail detail = list.get(0);
				if (detail.getSource() == InvoiceSource.SALES) {
					Sales sales = (Sales) detail.getSourceTo();
					return sales.getReferenceCode();
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	private String obtainDeliveryNumber(Invoice invoice) {
		List<InvoiceDetail> list = invoice.getDetailList().stream()
				.map(to -> ((InvoiceDetail) to)).collect(Collectors.toList());
		try {
			if (!list.isEmpty()) {
				InvoiceDetail detail = list.get(0);
				if (detail.getSource() == InvoiceSource.DELIVERY) {
					Delivery delivery = (Delivery) detail.getSourceTo();
					return delivery.getSeries() + "/" + delivery.getNumber();
				}
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

}

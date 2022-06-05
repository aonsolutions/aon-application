package com.esferalia.aon.seres.writer.connect2;

import static com.esferalia.aon.watson.util.AonNumberUtils.zeroIfNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.esferalia.aon.file.seres.connect2.ConnectSalesResponse;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPC;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPE;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPL;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPP;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPT;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.RECTL;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.Properties.ItemProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryAddressProperties;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.seres.SeresUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ConnectSalesResponseOccam  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory
			.getLogger(ConnectSalesResponseOccam.class);
	
	private String domainName;
	private Integer domainId;
	private String login;

	public ConnectSalesResponseOccam(String domainName, Integer domainId, String login) {
		this.domainName = domainName;
		this.domainId = domainId;
		this.login = login;
	}

	public FileOutput createFile(Sales sales, Company company, ORSPC.ORSPC_4 funcionDelMensaje, String companyEdiCode, String customerEdiCabeceraCode, String customerEdiPtoEntregaCode, String customerEdiFacturaCode, boolean isInvoicingMainAddress, String customerPackage)
			throws FileNotFoundException, UnsupportedEncodingException {
		RECTL rectl = createRECTLRecord(sales, company, funcionDelMensaje, companyEdiCode, customerEdiCabeceraCode, customerEdiPtoEntregaCode, customerEdiFacturaCode, isInvoicingMainAddress, customerPackage);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);
		FileFiller filler = new ConnectSalesResponse(rectl, writer);
		FileOutput output = new FileOutput();
		output.setErrors(filler.create());
		output.setContent(outputStream.toByteArray());
		return output;
	}

	private RECTL createRECTLRecord(Sales sales, Company company, ORSPC.ORSPC_4 funcionDelMensaje, String companyEdiCode, String customerEdiCabeceraCode, String customerEdiPtoEntregaCode, String customerEdiFacturaCode,boolean isInvoicingMainAddress, String customerPackage) {

		RECTL rectl = new RECTL();
		rectl.setTipoDeMensaje(RECTL.RECTL_2.CONFIRMACION_DE_RECEPCION_RECADV.getValue());
		rectl.setCodigoEmisor(companyEdiCode);
		rectl.setCodigoReceptor(customerEdiCabeceraCode);
		rectl.setIdentificacionDelMensaje(SeresUtils.dateTimeFormat().format(new Date()));
		rectl.setFecha_horaDelMensaje(SeresUtils.dateTimeFormat().format(new Date()));
		
		try {//MANDATORY
			rectl.orspc = createORSPCRecord(sales, funcionDelMensaje);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
		try {
			rectl.orsptList = createORSPTList(sales);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
		try {//MANDATORY
			rectl.orsppList = createORSPPList(sales, company, companyEdiCode, customerEdiCabeceraCode, customerEdiPtoEntregaCode, customerEdiFacturaCode, isInvoicingMainAddress);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
		try {//MANDATORY
			rectl.orsplList = createORSPLList(sales, customerPackage);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
		try {
			rectl.orspeList = createORSPEList(sales);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
		return rectl;
	}


	private ORSPC createORSPCRecord(Sales sales, ORSPC.ORSPC_4 funcionDelMensaje) {
		ORSPC orspc = new ORSPC();
		//--------------------MANDATORY FIELDS--------------------
		orspc.setTipoDocumento_231_(ORSPC.ORSPC_2.RESPUESTA_A_UNA_ORDEN_DE_COMPRA.getValue());
		orspc.setNumeroDeRespuestaAlPedido(sales.getReferenceCode());
		orspc.setFuncionDelMensaje(funcionDelMensaje.getValue());
		orspc.setFecha_horaDocumento_137_102_203_(SeresUtils.dateTimeFormat().format(sales.getDate()));
		orspc.setNumeroPedidoComprador_ON_(sales.getPurchaseReference());
		//--------------------------------------------------------
		
		//--------------------OPTIONAL FIELDS---------------------
		orspc.setCodigoMoneda(ORSPC.ORSPC_20.EURO.getValue());
		orspc.setTipoDeRespuesta(null);
		orspc.setCalificadorFecha_hora1(null);
		orspc.setFecha_hora1(null);
		orspc.setCalificadorFecha_hora2(null);
		orspc.setFecha_hora2(null);
		orspc.setInformacionAdicional(null);
		orspc.setFechaPedido(null);
		orspc.setNumeroPedidoAbierto_BO_(null);
		orspc.setNumeroListaDePrecios_PL_(null);
		orspc.setNumeroPedidoProveedor_VN_(null);
		orspc.setCalificadorReferenciaAdicional(null);
		orspc.setFechaReferenciaAdicional(null);
		orspc.setFechaDeVencimientoUnico(null);
		orspc.setMetodoPagoDeCostesDeTransportes(null);
		orspc.setCondicionesDeEntrega(null);
		if (sales.getDetails() != null && !sales.getDetails().isEmpty()) {			
			orspc.setImporteTotalNeto_79_(null);
			orspc.setImporteTotalDescuentos_Cargos_131_(null);
			orspc.setBaseImponible_125_(null);
			
			Double totalImpuestos = sales.getDetails().stream()
					.filter(Objects::nonNull)
					.map(SalesDetail::getTaxes)
					.filter(Objects::nonNull)
					.reduce(0d, (acc, value) -> acc + value);
			
			orspc.setImporteTotalImpuestos_176_(totalImpuestos);
			orspc.setImporteAPagar_139_(null);
			orspc.setImporteTotalBruto_98_(null);
		}
		//--------------
		
		
		//--------------------------------------------------------
		
		return orspc;
	}
	
	private List<ORSPT> createORSPTList(Sales sales) {
		List<ORSPT> orsptList = new LinkedList<>();
		if(StringUtils.isNotBlank(sales.getComments())){
			orsptList.add(createORSPTRecord(sales));
		}
		return orsptList;
	}
	
	private ORSPT createORSPTRecord(Sales sales) {
		ORSPT orspt = new ORSPT();
		orspt.setCalificadorDelTemaDeTexto(ORSPT.ORSPC_2.INFORMACION_GENERAL.getValue());
		String[] comments = sales.getComments().split("(?<=\\G.{70})");
		orspt.setTexto1(comments.length > 0 ? comments[0] : null);
		orspt.setTexto2(comments.length > 1 ? comments[1] : null);
		orspt.setTexto3(comments.length > 2 ? comments[2] : null);
		orspt.setTexto4(comments.length > 3 ? comments[3] : null);
		orspt.setTexto5(comments.length > 4 ? comments[4] : null);
		return orspt;
	}

	private List<ORSPP> createORSPPList(Sales sales, Company company, String companyEdiCode, String customerEdiCabeceraCode, String customerEdiPtoEntregaCode, String customerEdiFacturaCode, boolean isInvoicingMainAddress) {
		
		Customer customer = sales.getCustomer();
		RegistryAddress salesAddress = sales.getShippingAddress();
		RegistryAddress companyAddress = null;
		RegistryAddress customerMainAddress = null;
		RecordData recordData = null;
		
		try {
			customerMainAddress = AON.get(domainName, domainId, login, new RegistryAddressFilter() {
				
				@Override
				public Filter filter(RegistryAddressProperties properties) {
					return properties.getIdProperty().eq(customer.getMainAddress().getId());
				}
			});
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

		try {
			companyAddress = AON.get(domainName, domainId, login, new RegistryAddressFilter() {
				
				@Override
				public Filter filter(RegistryAddressProperties properties) {
					return properties.getIdProperty().eq(company.getMainAddress().getId());
				}
			});
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
		try {
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage());			
		}
		
		try {			
			recordData = AON.getRecordData(domainName, domainId, login, f -> f.getRegistryProperty().eq(company.getId()));
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
		String ediBY = isDia(customer) ? customerEdiCabeceraCode : customerEdiFacturaCode;
		String ediIV = isDia(customer) ? customerEdiFacturaCode : customerEdiCabeceraCode;
		
		
		List<ORSPP> orsppList = new LinkedList<>();
		orsppList.add(createORSPPRegistry(ORSPP.ORSPP_2.PROVEEDOR, companyEdiCode, company, companyAddress, recordData));
		orsppList.add(createORSPPRegistry(ORSPP.ORSPP_2.EMISOR_DEL_MENSAJE, companyEdiCode, company, companyAddress, null));
		orsppList.add(createORSPPRegistry(ORSPP.ORSPP_2.PUNTO_DESTINO_DE_LA_MERCANCIA, customerEdiPtoEntregaCode, customer, salesAddress, null));
		orsppList.add(createORSPPRegistry(ORSPP.ORSPP_2.RECEPTOR_DEL_MENSAJE, customerEdiCabeceraCode, customer, salesAddress, null));
		orsppList.add(createORSPPRegistry(ORSPP.ORSPP_2.DESTINATARIO_FINAL, customerEdiCabeceraCode, customer, salesAddress, null));
		orsppList.add(createORSPPRegistry(ORSPP.ORSPP_2.COMPRADOR, ediBY, customer, salesAddress, null/*, calificadorReferenciaAdicional, referenciaAdicional*/));
		orsppList.add(createORSPPRegistry(ORSPP.ORSPP_2.A_QUIEN_SE_FACTURA, ediIV, customer, isInvoicingMainAddress ? customerMainAddress : salesAddress, null));
		orsppList.add(createORSPPRegistry(ORSPP.ORSPP_2.SUJETO_DEL_PAGO_A_QUIEN_SE_PAGA_, companyEdiCode, customer, companyAddress, null));
		orsppList.add(createORSPPRegistry(ORSPP.ORSPP_2.PAGADOR_QUIEN_PAGA_, customerEdiCabeceraCode, customer, salesAddress, null));
		
		//------FALTAN------
			//ORSPP.ORSPP_2.PUNTO_DE_EXPEDICION
		//------------------
		
		return orsppList;
	}
	
	private ORSPP createORSPPRegistry(ORSPP.ORSPP_2 calificadorDelInterlocutor, String ediCode, Registry registry, RegistryAddress rAddress, RecordData recordData) {
		return createORSPPRegistry(calificadorDelInterlocutor, ediCode, registry, rAddress, recordData, null, null);
	}
	
	private ORSPP createORSPPRegistry(ORSPP.ORSPP_2 calificadorDelInterlocutor, String ediCode, Registry registry, RegistryAddress rAddress, RecordData recordData, String calificadorReferenciaAdicional, String referenciaAdicional) {
		ORSPP orspp = new ORSPP();
		//--------------------MANDATORY FIELDS--------------------
		orspp.setCalificadorDelInterlocutor(calificadorDelInterlocutor.getValue());
		//--------------------------------------------------------
		
		//--------------------OPTIONAL FIELDS---------------------
		orspp.setCodigoInterlocutor(ediCode);
		orspp.setAgenciaResponsableDeLaListaDeCodigos(null);
		orspp.setNombre1(registry.getName());
		orspp.setNombre2(null);
		orspp.setNombre3(null);

		try {
			orspp.setCalleYNumero1(rAddress.getAddress());
			orspp.setCalleYNumero2(rAddress.getAddress2());
			orspp.setCalleYNumero3(rAddress.getAddress3());
			orspp.setCalleYNumero4(rAddress.getNumber());
			orspp.setPoblacion(rAddress.getCity());
			orspp.setProvincia(rAddress.getProvince());
			orspp.setCodigoPostal(rAddress.getZip());
			orspp.setCodigoPais(rAddress.getCountry().getIso3());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		orspp.setCalificadorReferencia1(null);
		orspp.setReferencia1(null);
		orspp.setFuncionDeContacto(null);
		orspp.setDepartamentoOIdentificacionDelEmpleado(null);
		orspp.setDepartamentoOEmpleado(null);
		orspp.setCalificadorReferencia2(null);
		orspp.setReferencia2(null);
		//--------------------------------------------------------
		
		return orspp;
	}
	
	private List<ORSPL> createORSPLList(Sales sales, String customerPackage) {
		List<ORSPL> orsplList = new LinkedList<>();
		for (SalesDetail salesDetail : sales.getDetails()) {
			orsplList.add(createORSPLRegistry(sales, salesDetail, sales.getDetails().indexOf(salesDetail) + 1, customerPackage));
		}
		return orsplList;
	}

	private ORSPL createORSPLRegistry(Sales sales, SalesDetail salesDetail, int lineaArticulo, String customerPackage) {
		Item item = salesDetail.getItem();
		Product product = item != null ? item.getProduct() : null;
		
		String productDescription = null;
		String productType = null;
		String productCustomerCode = "";
		Double quantity = null;
		double packUnits = 0;
		if (item != null && item.getProduct() != null) {
			productDescription = item.getProduct().getName();
			
			if (ProductType.SERVICE.equals(item.getProduct().getType())) {
				productType = ORSPL.ORSPL_9.SERVICIOS.getValue();
			} else {
				productType = ORSPL.ORSPL_9.MERCANCIAS.getValue();
			}
			Integer customerId = sales.getCustomer().getId();
			productCustomerCode = obtainProductCustomerCode(item, customerId);
			quantity = /*item.getPackUnits() * item.getPackMeasurement()*/obtainPackageQuantity(salesDetail, customerPackage);
			packUnits = item.getPackUnits();
		}
		
		
		ORSPL orspl = new ORSPL();
		//--------------------MANDATORY FIELDS--------------------
		orspl.setNumeroDeLineaArticulo(lineaArticulo);
		orspl.setSolicitudDeAccionONotificacion(ORSPL.ORSPL_3.ACEPTADO_SIN_CORRECCION.getValue());
		//--------------------------------------------------------
		//--------------------OPTIONAL FIELDS---------------------
		orspl.setCodigoEAN_13_DUN_14DelArticulo(productCustomerCode);
		orspl.setTipoCodigoArticulo(ORSPL.ORSPL_5.EAN.getValue());
		orspl.setTipoIdentificacionDeArticulo_CU_DU_(ORSPL.ORSPL_6.UNIDAD_DE_CONSUMO.getValue());
		
		orspl.setDescripcion1Articulo(productDescription);
		orspl.setDescripcion2Articulo(null);
		orspl.setTipoArticulo(productType);
		orspl.setNumeroArticuloComprador_IN_BP_(null);
		orspl.setVariablePromocional_PV_(null);
		orspl.setCodigoEANDelArticuloAdicional_1_EN_(null);
		orspl.setCantidadPedida_21_(salesDetail.getQuantity());//?
		orspl.setCantidadBonificada_192_(null);
		orspl.setCalificadorUnidadDeMedida(ORSPL.ORSPL_16.KILOGRAMO.getValue());

		String packFormatTagName = (item != null && item.getPackFormatTag() != null) ? item.getPackFormatTag().getName() : null;
		String packUnitsTagName = (item != null && item.getPackUnitsTag() != null) ? item.getPackUnitsTag().getName() : null;
		
		
		
		if(item != null && AonStringUtils.equalsIgnoreCase(packFormatTagName, packUnitsTagName)){
			orspl.setNumeroUnidadesDeConsumoEnU_Expedicion(item.getPackUnits() * item.getPackMeasurement());
		} else {
			orspl.setNumeroUnidadesDeConsumoEnU_Expedicion(packUnits);
		}
		
		orspl.setVariacionEnCantidad(null);
		orspl.setCodigoDiscrepancia(null);
		orspl.setRazonDelCambio(null);
		if (salesDetail.getDeliveryDate() != null) {			
			orspl.setCalificadorFecha_Hora1_2_11_64_(ORSPL.ORSPL_21.FECHA_HORA_DE_ENTREGA_REQUERIDA.getValue());
			orspl.setFecha_Hora1(SeresUtils.dateTimeFormat().format(salesDetail.getDeliveryDate()));
		}
		
		if (sales.getDate() != null) {			
			orspl.setCalificadorFecha_Hora2_2_11_63_(ORSPL.ORSPL_23.FECHA_HORA_DE_ENVIO.getValue());//?
			orspl.setFecha_Hora2(SeresUtils.dateTimeFormat().format(sales.getDate()));//?
		}
		
		double discount = AonNumberUtils.todouble(sales.getDiscountExpr()) * zeroIfNull(salesDetail.getPrice());
		
		orspl.setImporteNetoLinea_203_(zeroIfNull(salesDetail.getQuantity()) * (zeroIfNull(salesDetail.getPrice()) - discount));
		orspl.setImporteLineaConImpuestos_388_(null);
		orspl.setPrecioBrutoUnitario_AAB_(zeroIfNull(salesDetail.getPrice()));
		orspl.setPrecioNetoUnitario_AAA_(zeroIfNull(salesDetail.getPrice()) - discount);
		orspl.setPrecioATituloInformativo_INF_(null);
		orspl.setCalificadorUnidadDeMedidaPrecio(null);
		orspl.setUnidadBasePrecio(null);
		orspl.setCalificadorIVA_IGIC(ORSPL.ORSPL_32.IVA.getValue());
		
		Tax vat = product != null ? product.getVat() : null;
		Double vatPercent = vat != null ? vat.getPercentage() : null;
		Double vatAmount = vatPercent != null ? vatPercent/100 * orspl.getPrecioNetoUnitario_AAA_() : null;
		orspl.setPorcentajeIVA_IGIC(vatPercent);
		orspl.setImporteIVA_IGIC(vatAmount);
		
		Tax retention = product != null ? product.getRetention() : null;
		Double retentionPercent = retention != null ? retention.getPercentage() : null;
		Double retentionAmount = retentionPercent != null ? retentionPercent/100 * orspl.getPrecioNetoUnitario_AAA_() : null;
		orspl.setPorcentajeRecargoDeEquivalencia(retentionPercent);
		orspl.setImporteRecargoDeEquivalencia(retentionAmount);
		
		orspl.setCalificadorOtroTipoDeImpuesto(null);
		orspl.setPorcentajeOtroTipoDeImpuesto(null);
		orspl.setImporteOtroTipoDeImpuesto(null);
		orspl.setPesoNeto_PD_AAA_(quantity * (item != null ? zeroIfNull(item.getPackMeasurement()) : 0));
		orspl.setCalificadorUnidadDeMedidaPeso("KGM");
		orspl.setDescripcionDelModelo_BRN_(null);
		orspl.setColor_35_(null);
		orspl.setAnchuraOTalla_UP5_(null);
		orspl.setPresentacion_cantidad_formato_U03_(null);
		orspl.setCantidadAceptada_enviada_12_(null);
		orspl.setCantidadRechazada_83_(null);
		orspl.setCancelledQuantity_QTY_182_(null);
		orspl.setRejectedQuantity_QTY_185_(null);
		orspl.setNumeroDeEmbalajes_PAC_(null);
		orspl.setIdentificacionDelTipoDeEmbalaje_PAC_(null);
		//--------------------------------------------------------
		
		return orspl;
	}
	
	private List<ORSPE> createORSPEList(Sales sales) {
		List<ORSPE> orspeList = new LinkedList<>();
		for (SalesDetail salesDetail : sales.getDetails()) {
			orspeList.add(createORSPERegistry(salesDetail, sales.getDetails().indexOf(salesDetail) + 1));
		}
		return orspeList;
	}
	
	private ORSPE createORSPERegistry(SalesDetail salesDetail, int lineNumber) {
		Double rawAmount = salesDetail.getQuantity() * salesDetail.getPrice();
		Double discountAmount = AonNumberUtils.todouble(salesDetail.getDiscountExpression());
		ORSPE orspe = new ORSPE();
		orspe.setNumeroDescuento_Cargo(lineNumber);
		orspe.setIndicadorDescuento_Cargo_A_C_(ORSPE.ORSPU_3.DESCUENTO.getValue());
		orspe.setIndicadorSecuenciaDeCalculo("1");
		orspe.setTipoDescuento_Cargo(ORSPE.ORSPU_5.DTO_COMERCIAL.getValue());
		orspe.setDescripcionDescuento_Cargo(null);
		orspe.setPorcentajeDescuento_Cargo(CommonUtil.round(discountAmount, 4));
		orspe.setImporteDescuento_Cargo(CommonUtil.round(rawAmount*(discountAmount/100), 3));
		orspe.setImporteTotalSujetoAAplicacion_13_(rawAmount);
		orspe.setDescuentosMonetariosPorUnidad(0.0);
		orspe.setUnidadDeMedida(null);
		orspe.setCantidadDescuento_Cargo(null);
		return orspe;
	}
	
	private boolean isDia(Registry registry) {
		return "A80782519".equalsIgnoreCase(registry.getDocument());
	}
	
	private String obtainProductCustomerCode(Item item, Integer customerId) {
		try {
			Integer baseItemId = getBaseItemId(item.getProduct());
			com.esferalia.aon.occam.api.model.registry.RegistryItem rItem = 
				AON.getRItemStream(domainName, domainId, login, f -> f
					.getItemProperty().eq(baseItemId)
					.and(f.getRegistryProperty().eq(customerId))
					.and(f.getStatusProperty().eq(com.esferalia.aon.occam.api.model.registry.RegistryItemStatus.ACTIVE.value()))
					.and(f.getTypeProperty().eq(com.esferalia.aon.occam.api.model.registry.RegistryMode.CUSTOMER.value())))
				.sorted((i1, i2) -> i1.getPriority().compareTo(i2.getPriority()))
				.findFirst().orElse(new com.esferalia.aon.occam.api.model.registry.RegistryItem());
			if(rItem!=null)
				return rItem.getCode();
		} catch (Throwable e) {
			LOGGER.error(e.getMessage());
		}
		return "";
	}
	
	private Integer getBaseItemId(Product product){
		try {
			Item item = AON.getItem(AON.getDomain(domainName, domainId, login), login, f -> 
				f.getProductProperty().eq(product.getId())
				 .and(f.getSerialNumberProperty().isNull())
			);
			
			if(item!=null)
				return item.getId();
		} catch (Throwable e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	
	private Item getItem(Integer itemId){
		try {
			
			return AON.getItem(AON.getDomain(domainName, domainId, login), login, f -> f.getIdProperty().eq(itemId));
		} catch (Throwable e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	
	
	private Double obtainPackageQuantity(SalesDetail detail,
			String customerPackingTag) {
		if(detail!=null && customerPackingTag!=null){
			Item item = getItem(detail.getItem().getId());
			Double quantity = detail.getQuantity();
			Tag itemPackFormatTag = item.getPackFormatTag();
			Tag itemPackMeasurementTag = item.getPackMeasurementTag();
			Tag itemPackingTag = item.getPackUnitsTag();
			double itemPackMeasurement = item.getPackMeasurement();
			double itemPackUnits = item.getPackUnits();
			if (itemPackingTag != null && itemPackFormatTag != null && itemPackMeasurementTag != null) {
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

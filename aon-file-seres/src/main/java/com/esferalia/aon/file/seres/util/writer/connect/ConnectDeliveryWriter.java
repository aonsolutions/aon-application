package com.esferalia.aon.file.seres.util.writer.connect;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tag;
import com.code.aon.customer.Customer;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.enumeration.RegistryItemStatus;
import com.code.aon.registry.enumeration.RegistryMode;
import com.code.aon.sales.Sales;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.seres.connect.ConnectDelivery;
import com.esferalia.aon.file.seres.connect.delivery.v4.data.RECTL;
import com.esferalia.aon.file.seres.connect.delivery.v4.data.SEH1B;
import com.esferalia.aon.file.seres.connect.delivery.v4.data.SEH1C;
import com.esferalia.aon.file.seres.connect.delivery.v4.data.SEH1D;
import com.esferalia.aon.file.seres.connect.delivery.v4.data.SEH1G;
import com.esferalia.aon.file.seres.connect.delivery.v4.data.SEH1L;
import com.esferalia.aon.file.seres.connect.delivery.v4.data.SEH1P;
import com.esferalia.aon.file.seres.util.DeliveryPackages;
import com.esferalia.aon.file.seres.util.SeresUtils;

public class ConnectDeliveryWriter {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(ConnectDeliveryWriter.class);


	public FileOutput createFile(Delivery delivery, String packageData, String companyEdiCode,
			String customerEdiCode, String deliveryPointEdiCode,
			String customerPackage) throws FileNotFoundException, UnsupportedEncodingException {
		RECTL rectl = createRECTLRecord(delivery, packageData, companyEdiCode,
				customerEdiCode, deliveryPointEdiCode, customerPackage);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);
		FileFiller filler = new ConnectDelivery(rectl, writer);
		FileOutput output = new FileOutput();
		output.setErrors(filler.create());
		output.setContent(outputStream.toString().getBytes(SeresUtils.DEFAULT_CHARSET_ENC));
		return output;
	}

	private RECTL createRECTLRecord(Delivery delivery, String packageData, String companyEdiCode,
			String customerEdiCode, String deliveryPointEdiCode, String customerPackage) {
		RECTL rectl = new RECTL();
		rectl.setTipoDeMensaje(RECTL.RECTL_2.AVISO_DE_EXPEDICION_DESADV.getValue());
		rectl.setCodigoEmisor(companyEdiCode);
		rectl.setCodigoReceptor(customerEdiCode);
		rectl.setIdentificacionDelMensaje(SeresUtils.dateTimeFormat().format(new Date()));
		rectl.setFecha_horaDelMensaje(SeresUtils.dateTimeFormat().format(new Date()));
		
		rectl.seh1c = createSEH1CRecord(delivery, companyEdiCode,
				customerEdiCode, deliveryPointEdiCode);
		rectl.seh1dList = createSEH1DList(delivery, companyEdiCode,
				customerEdiCode, deliveryPointEdiCode);
		rectl.seh1pList = createSEH1PList(delivery, packageData, companyEdiCode,
				customerEdiCode, customerPackage);
//		rectl.seh1lList = createSEH1LList(delivery, companyEdiCode,
//				customerEdiCode);
		rectl.seh1gList = createSEH1GList(delivery);
		rectl.seh1bList = createSEH1BList(delivery);
		
		return rectl;
	}
	
	/**
	 * Cabecera
	 */
	private SEH1C createSEH1CRecord(Delivery delivery, String companyEdiCode,
			String customerEdiCode, String deliveryPointEdiCode) {
		SEH1C seh1c = new SEH1C();
		
		seh1c.setTipoDeDocumento_351_35E_(SEH1C.SEH1C_2.NOTAS_DE_ENVIO_351
				.getValue());
		seh1c.setNumeroDelDocumento(delivery.getReferenceCode());
		seh1c.setFuncionDelMensaje(SEH1C.SEH1C_4.ORIGINAL___EL_ENVIO_DE_UN_AVISO_DE_EXPEDICION_ORIGINAL_9
				.getValue());
		seh1c.setFecha_horaDelDocumento_137__102_203_(SeresUtils.dateTimeFormat().format(delivery
				.getDate()));
		seh1c.setFecha_horaEstimadaDeEntrega_17__102_203_(SeresUtils.dateTimeFormat().format(delivery.getIssueTime()));
		seh1c.setCalificadorFecha_Hora1_2_11_64_(null);
		seh1c.setFecha_hora1(null);
		seh1c.setCalificadorFecha_Hora2_2_11_63_(null);
		seh1c.setFecha_hora2(null);
		seh1c.setInformacionAdicional(null);
		seh1c.setNumeroPedido_comprador__ON_(obtainPurchaseReference(delivery));
		seh1c.setFecha_horaNumeroPedido_171__102_203_(null);
		seh1c.setNumeroAlbaran_DQ_(delivery.getReferenceCode());
		seh1c.setFecha_horaNumeroAlbaran_171__102_203_(null);
		seh1c.setCalificadorDeReferencia1(null);
		seh1c.setNumeroDeReferencia1(null);
		seh1c.setFecha_horaReferencia1_102_203_(null);
		seh1c.setCalificadorDeReferencia2(null);
		seh1c.setNumeroDeReferencia2(null);
		seh1c.setFecha_horaReferencia2_102_203_(null);
		seh1c.setMetodoPagoDeCostesDeTransporte(null);
		seh1c.setCondicionesDeEntregaOTransporte_Codificada(null);
		seh1c.setCondicionesDeEntregaOTransporte_TextoLibre(null);
		seh1c.setModoDeTransporte_Codificado(null);
		seh1c.setIdentificacionDeTransportista(delivery.getDriverDocument());
		seh1c.setNombreDelTransportista(delivery.getDriver());
		seh1c.setMatriculaDelVehiculo(delivery.getNumberPlate());
		seh1c.setLugarDeEntrega_Codificado_8_(deliveryPointEdiCode);
		String deliveryAddress = delivery.getRegistryAddress().getFullAddress();
		deliveryAddress = deliveryAddress != null
				&& deliveryAddress.length() > 70 ? StringUtils.abbreviate(
				deliveryAddress, 70) : deliveryAddress;
		seh1c.setLugarDeEntrega_TextoLibre_8_(deliveryAddress);
		return seh1c;
	}

	private List<SEH1D> createSEH1DList(Delivery delivery,
			String companyEdiCode, String customerEdiCode,
			String deliveryPointEdiCode) {
		List<SEH1D> list = new ArrayList<>();

		list.add(createSEH1DRecord(SEH1D.SEH1D_2.EMISOR_DEL_MENSAJE_MS,
				companyEdiCode, delivery.getWorkPlace().getEnterprise()));
		list.add(createSEH1DRecord(SEH1D.SEH1D_2.RECEPTOR_DEL_MENSAJE_MR,
				customerEdiCode, delivery.getCustomer()));
		// list.add(createSEH1DRecord(SEH1D.SEH1D_2.PROVEEDOR__SU,
		// null, null));
		list.add(createSEH1DRecord(
				SEH1D.SEH1D_2.PUNTO_DESDE_DONDE_SE_ENVIAN_LAS_MERCANCIAS_PW,
				companyEdiCode, delivery.getWorkPlace().getEnterprise()));
		list.add(createSEH1DRecord(
				SEH1D.SEH1D_2.PUNTO_DESTINO_DE_LA_MERCANCIA_DP,
				deliveryPointEdiCode, delivery.getCustomer()));
		// list.add(createSEH1DRecord(SEH1D.SEH1D_2.DESTINATARIO_FINAL_UC,
		// null, null));
		list.add(createSEH1DRecord(SEH1D.SEH1D_2.COMPRADOR_BY, customerEdiCode,
				delivery.getCustomer()));
		list.add(createSEH1DRecord(SEH1D.SEH1D_2.EXPEDIDOR_SH, companyEdiCode,
				delivery.getWorkPlace().getEnterprise()));
		list.add(createSEH1DRecord(SEH1D.SEH1D_2.A_QUIEN_SE_FACTURA_IV,
				customerEdiCode, delivery.getCustomer()));

		return list;
	}

	private List<SEH1P> createSEH1PList(Delivery delivery, String packageData, 
			String companyEdiCode, String customerEdiCode, String customerPackage) {
		
		List<SEH1P> list = new ArrayList<>();
		List<DeliveryDetail> detailList = delivery.getDetailList().stream()
				.map(to -> (DeliveryDetail)to)
				.sorted((d1, d2)->d1.getLine().compareTo(d2.getLine()))
				.collect(Collectors.toList());
		
		Map<Integer, List<Integer>> level1Map = DeliveryPackages.loadLevel1Map(packageData, detailList);
		Map<Integer, List<Integer>> level2Map = DeliveryPackages.loadLevel2Map(packageData);
		Map<Integer, List<Integer>> level3Map = DeliveryPackages.loadLevel3Map(packageData);
//		System.out.print("L1: ");
//		level1List.forEach(System.out::println);
//		System.out.println("L1: "+level1Map.keySet()+" | "+level1Map);
//		System.out.println("L2: "+level2Map.keySet()+" | "+level2Map);
//		System.out.println("L3: "+level3Map.keySet()+" | "+level3Map);
		
		
//		System.out.println("*** LEVEL 1 *************************");
//		level1Map.keySet().forEach(key->{
//			System.out.println("MAIN_PACKAGE");
//			level1Map.get(key).forEach(id->{
//				DeliveryDetail contentDetail = (DeliveryDetail) detailList.get(id-1);
//				System.out.println("\t"+id+" - "+contentDetail.getDescription());
//			});
//		});
//		System.out.println("*** LEVEL 2 *************************");
//		level2Map.keySet().forEach(key->{
//			DeliveryDetail mainDetail = (DeliveryDetail) detailList.get(key-1);
//			System.out.println(key+" - "+mainDetail.getDescription());
//			level2Map.get(key).forEach(id->{
//				DeliveryDetail contentDetail = (DeliveryDetail) detailList.get(id-1);
//				System.out.println("\t"+id+" - "+contentDetail.getDescription());
//			});
//		});
//		System.out.println("*** LEVEL 3 *************************");
//		level3Map.keySet().forEach(key->{
//			DeliveryDetail mainDetail = (DeliveryDetail) detailList.get(key-1);
//			System.out.println(key+" - "+mainDetail.getQuantity()+"x"+mainDetail.getDescription());
//			level3Map.get(key).forEach(id->{
//				DeliveryDetail contentDetail = (DeliveryDetail) detailList.get(id-1);
//				System.out.println("\t"+id+" - "+contentDetail.getDescription());
//			});
//		});
		
		
		int mainPackageLine = 0;
		int packageLine = 0;
		int mainPackageSize = 0;
		for(Integer level1Key: level1Map.keySet()){
			List<Integer> packageLineList = level1Map.get(level1Key);
			
			// MAIN-PACKAGE
			mainPackageSize = (int)detailList.stream()
				.filter(detail->packageLineList.contains(detail.getLine()))
				.mapToDouble(DeliveryDetail::getQuantity).sum();
			mainPackageLine = ++packageLine;
			SEH1P mainPackage = createSEH1PRecord(mainPackageLine, mainPackageSize, "201");
			mainPackage.seh1lList = new ArrayList<>();
			list.add(mainPackage);
			
			// SUB-PACKAGE OR PRODUCT OVER MAIN-PACKAGE
			for(Integer level2Key: level2Map.keySet()){
				if(packageLineList.contains(level2Key)){
					
					List<Integer> level2LineList = new LinkedList<>(level2Map.get(level2Key));
					for(int level2LineId: level2LineList){
						DeliveryDetail level2Detail = (DeliveryDetail) detailList.get(level2LineId-1);
						if(!isPackageItem(level2Detail.getItem())) {
							mainPackage.seh1lList.add(createSEH1LRecord(level2Detail, null,
									companyEdiCode, customerEdiCode, customerPackage));
						} else {
							SEH1P subPackage = createSEH1PRecord(++packageLine, (int) level2Detail.getQuantity(), "CT");
							subPackage.setNumeroDeJerarquiaPadreDeEmbalaje(mainPackage.getNumeroDeJerarquiaDeEmbalaje());
							subPackage.seh1lList = new ArrayList<>();
							list.add(subPackage);
							
							// PRODUCT OVER SUB-PACKAGE, IF EXIST
							List<Integer> level3LineList = new LinkedList<>(level3Map.get(level2Detail.getLine()));
							for(int level3LineId: level3LineList){
								DeliveryDetail level3Detail = (DeliveryDetail) detailList.get(level3LineId-1);
								subPackage.seh1lList.add(createSEH1LRecord(level3Detail, level2Detail.getQuantity(),
										companyEdiCode, customerEdiCode, customerPackage));
							}
						}
					}
				}				
			}
			
		}
		
		return list;
	}
	

	private List<SEH1G> createSEH1GList(Delivery delivery) {
		List<SEH1G> list = new ArrayList<>();
		createSEH1GRecord(delivery);
		return list;
	}

	private List<SEH1B> createSEH1BList(Delivery delivery) {
		List<SEH1B> list = new ArrayList<>();
		createSEH1BRecord(delivery);
		return list;
	}

	/**
	 * Información de partes
	 */
	private SEH1D createSEH1DRecord(SEH1D.SEH1D_2 type, String ediCode, IRegistry registry) {
		SEH1D record = new SEH1D();
		record.setCalificadorDelInterlocutor(type.getValue());
		record.setCodigoInterlocutor(ediCode);
		record.setAgenciaResponsableDeLaListaDeCodigos(SEH1D.SEH1D_4.EAN_9.getValue());
		record.setNombre1(registry.getRegistry().getName());
		record.setNombre2(null);
		record.setNombre3(null);
		record.setNombre4(null);
		record.setNombre5(null);
		try {
			record.setCalleYNumero1(registry.getRegistry().getDefaultAddress().getAddress());
			record.setCalleYNumero2(registry.getRegistry().getDefaultAddress().getAddress2());
			record.setCalleYNumero3(registry.getRegistry().getDefaultAddress().getAddress3());
			record.setCalleYNumero4(registry.getRegistry().getDefaultAddress().getNumber());
			record.setPoblacion(registry.getRegistry().getDefaultAddress().getCity());
			record.setProvincia(registry.getRegistry().getDefaultAddress().getGeozone().getName());
			record.setCodigoPostal(registry.getRegistry().getDefaultAddress().getZip());
			record.setCodigoPais(registry.getRegistry().getDefaultAddress().getGeozone().getGeoZoneCountry().getCode());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		record.setCalificadorReferencia1(null);
		record.setReferencia1(null);
		record.setFuncionDeContacto(null);
		record.setDepartamentoOIdentificacionDelEmpleado(null);
		record.setDepartamentoOEmpleado(null);
		record.setCalificadorReferencia2(null);
		record.setReferencia2(null);
		return record;
	}

	/**
	 * Secuencia de embalajes
	 */
	private SEH1P createSEH1PRecord(int lineNumber, int quantity, String format) {
		SEH1P record = new SEH1P();
		record.setNumeroDeJerarquiaDeEmbalaje(String.valueOf(lineNumber));
		record.setNumeroDeJerarquiaPadreDeEmbalaje(null);
		record.setNumeroDePaquetes(quantity);
		record.setInformacionSobreElEmbalaje_Codificado(null);
		record.setTerminosYCondicionesDelEmbalaje_Codificado(null);
		record.setTipoDeEmbalaje_Codificado(format);
		record.setTipoDeEmbalaje_TextoLibre(null);
		record.setResponsabilidadPagoTransporteDeEmbalaje(null);
		record.setPesoNeto1_AAC_(null);
		record.setPesoNeto2(null);
		record.setCodigoSignificacionDeLaMedidaPesoNeto(null);
		record.setUnidadDeMedidaParaElPesoNeto(null);
		record.setPesoBruto1_AAD_(null);
		record.setPesoBruto2(null);
		record.setCodigoSignificacionDeLaMedidaPesoBruto(null);
		record.setUnidadDeMedidaParaElPesoBruto(null);
		record.setDimensionDeAltura1_HT_(null);
		record.setDimensionDeAltura2(null);
		record.setCodigoSignificacionDeLaMedidaAltura(null);
		record.setUnidadDeMedidaParaLaAltura(null);
		record.setDimensionDeAncho1_WD_(null);
		record.setDimensionDeAncho2(null);
		record.setCodigoSignificacionDeLaMedidaAncho(null);
		record.setUnidadDeMedidaParaElAncho(null);
		record.setDimensionDeLongitud1_LN_(null);
		record.setDimensionDeLongitud2(null);
		record.setCodigoSignificacionDeLaMedidaLongitud(null);
		record.setUnidadDeMedidaParaLaLongitud(null);
		record.setDimensionDeTemperatura1_TC_(null);
		record.setDimensionDeTemperatura2(null);
		record.setCodigoSignificacionDeLaMedidaTemperatura(null);
		record.setUnidadDeMedidaParaLaTemperatura(null);
		record.setCantidadPorEmbalaje(null);
		record.setInstruccionesDeManejo_Codificado(null);
		record.setInstruccionesDeManejo_TextoLibre(null);
		record.setMarcaDeEnvio1(null);
		record.setMarcaDeEnvio2(null);
		record.setMarcaDeEnvio3(null);
		record.setMarcaDeEnvio4(null);
		record.setNumeroSerial1ONumeroDeIdentificacionInferior(null);
		record.setNumeroSerial1ONumeroDeIdentificacionSuperior(null);
		record.setNumeroSerial2oNumeroDeIdentificacionInferior(null);
		record.setNumeroSerial2ONumeroDeIdentificacionSuperior(null);
		record.setNumeroSerial3ONumeroDeIdentificacionInferior(null);
		record.setNumeroSerial3ONumeroDeIdentificacionSuperior(null);
		return record;
	}

	/**
	 * Línea de artículos
	 */
	private SEH1L createSEH1LRecord(DeliveryDetail detail,
			Double packageQuantity, String companyEdiCode, String customerEdiCode, String customerPackage) {
		Item item = detail.getItem();
		Customer customer = detail.getDelivery().getCustomer();
		String productCustomerCode = obtainProductCustomerCode(item, customer);

		String barcode = item.getBarcode();
		if(StringUtils.isBlank(barcode)){
			try {
				barcode = item.getProduct().getBaseItem().getBarcode();
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage());
			}
		}
		
		SEH1L record = new SEH1L();
		record.setNumeroDeLineaDelArticulo(detail.getLine());
		record.setCodigoEANDelArticulo(barcode);
		record.setDescripcionDelArticulo(item.getProduct().getName());
		record.setTipoDeIdentificacionDelArticulo_CU_DU_("CU");
		record.setNumeroDeArticuloDelProveedor_SA_(productCustomerCode);
		record.setNumeroVariablePromocional_PV_(null);
		record.setCodigoDUN_14_ADU_(null);
		record.setCodigoACU_ACU_(null);
		record.setNumeroDeLote_NB_(item.getSerialNumber());
		record.setNumeroDeArticuloDelComprador_IN_(null);
		
		double quantity = 0.0;
		double packUnits = detail.getItem().getPackUnits();
		if(packageQuantity==null){
			quantity = obtainPackageQuantity(detail, customerPackage);
		} else {
			quantity = packUnits * packageQuantity;
		}
		record.setCantidadEnviada_12_(quantity);
		
		record.setUnidadDeMedidaCantidadEnviada(null);
		record.setUnidadesDeConsumoEnUnidadDeExpedicion_59_(packUnits);
		record.setFechaDeCaducidad_36__102_203_(null);
		record.setCalificadorReferencia1(null);
		record.setNumeroReferencia1(null);
		record.setFecha_horaReferencia1_102_203_(null);
		record.setCalificadorReferencia2(null);
		record.setNumeroReferencia2(null);
		record.setFecha_horaReferencia2_102_203_(null);
		record.setCalificadorReferencia3(null);
		record.setNumeroReferencia3(null);
		record.setFecha_horaReferencia3_102_203_(null);
		record.setUnidadesEnAgrupacionSuperior_45E_(null);
		record.setCodigoEANAdicional(null);
		record.setCantidadSinCargo_192_(null);
		record.setCalificadorCantidadAdicional(null);
		record.setCantidadAdicional(null);
		record.setUnidadDeMedidaCantidadAdicional(null);
		record.setNumeroDeSerieDelArticulo_SN_(item.getSerialNumber());
		record.setNumeroArticuloFabricante_MF_(null);
		record.setNumeroDeLineaReferencia1(null);
		record.setNumeroDeLineaReferencia2(null);
		record.setNumeroDeLineaReferencia3(null);
		record.setDiferenciaEnCantidadPedida_21_(null);
		record.setCodigoDiscrepancia(null);
		record.setPesoTotalNetoDeLaLinea_AAI_AAF_(null);
		record.setPesoTotalBrutoDeLaLinea_AAI_AAB_(null);
		record.setUnidadDeMedidaPeso(null);
		record.setDimensionDeTemperatura1_TC_(null);
		record.setDimensionDeTemperatura2(null);
		record.setUnidadDeMedidaParaLaTemperatura(null);
		record.setDenominacionComercial(null);
		record.setDenominacionCientifica(null);
		record.setPaisDeCaptura_produccion_cosecha_cria(null);
		record.setZonaFAODeCaptura(null);
		record.setMetodoDeProduccion(null);
		record.setCodigoDePresentacion(null);
		record.setCodigoFAODeLaEspecie(null);
		record.setFechaOPeriodoDeCaptura(null);
		record.setFechaDeProduccion(null);
		record.setArteDePesca(null);
		record.setInformacionDeCongelado(null);
		record.setFechaDeCongelacion_91E_(null);
		return record;
	}

	/**
	 * Desglose cantidad/Localizaciones
	 */
	private SEH1G createSEH1GRecord(Delivery delivery) {
		// TODO createSEH1GRecord
		SEH1G record = new SEH1G();
		record.setCodigoLugar_localizacion(null);
		record.setAgenciaResponsableListaDeCodigos(null);
		record.setLugar_localizacion_TextoLibre(null);
		record.setFecha_horaEstimadaDeEntrega_17_(null);
		record.setCantidadEnvioDividida_11_12_(null);
		record.setPesoEnvioDivididoEnKGM_12_(null);
		record.setCalificadorLugar_localizacion(null);
		record.setFechaDeSacrificio_X20_(null);
		return record;
	}

	/**
	 * Información de lotes
	 */
	private SEH1B createSEH1BRecord(Delivery delivery) {
		// TODO createSEH1BRecord
		SEH1B record = new SEH1B();
		record.setCodigoInstrucciones(null);
		record.setMarcasDeEnvio(null);
		record.setFechaDeCaducidad_36__102_203_(null);
		record.setFecha_horaRecepcionDeLaMercancia_50__102_203_(null);
		record.setConsumirAntesDeFecha_361__102_203_(null);
		record.setCalificadorDeCantidad_11_12_(null);
		record.setCantidad(null);
		record.setCalificadorNumeroIdentidad(null);
		record.setNumeroIdentidad(null);
		record.setFechaDeEnvasadoOEmpaquetado_365__102_203_(null);
		record.setFechaProduccion_fabricacion_94__102_203_(null);
		return record;
	}
	
	
	/////////////////////////////////////////////////
	/////////////////////////////////////////////////
	/////////////////////////////////////////////////
	
	private String obtainPurchaseReference(Delivery delivery) {
		List<DeliveryDetail> list = delivery.getDetailList().stream()
				.map(to -> ((DeliveryDetail) to)).collect(Collectors.toList());
		if (!list.isEmpty()) {
			DeliveryDetail detail = list.get(0);
			if (detail.getSalesDetail() != null) {
				Sales sales = detail.getSalesDetail().getSales();
				return sales.getPurchaseReference();
			}
		}
		return null;
	}

	private String obtainProductCustomerCode(Item item, Customer customer) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(RegistryItem.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					bean.getFieldName(IEntityAlias.REGISTRY_ITEM_ITEM_ID),
					item.getProduct().getBaseItem().getId());
			criteria.addEqualExpression(
					bean.getFieldName(IEntityAlias.REGISTRY_ITEM_REGISTRY_ID),
					customer.getId());
			criteria.addEqualExpression(
					bean.getFieldName(IEntityAlias.REGISTRY_ITEM_STATUS),
					RegistryItemStatus.ACTIVE);
			criteria.addEqualExpression(
					bean.getFieldName(IEntityAlias.REGISTRY_ITEM_TYPE),
					RegistryMode.CUSTOMER);
			criteria.addOrder(bean
					.getFieldName(IEntityAlias.REGISTRY_ITEM_PRIORITY));
			List<ITransferObject> list = bean.getList(criteria);
			if (list != null && !list.isEmpty())
				return ((RegistryItem) list.get(0)).getCode();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}
		return "";
	}

	private boolean isPackageItem(Item item) {
		return item != null && item.getSerialNumber() == null
				&& item.getSerialDate() == null;
	}
	
	private Double obtainPackageQuantity(DeliveryDetail detail,
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

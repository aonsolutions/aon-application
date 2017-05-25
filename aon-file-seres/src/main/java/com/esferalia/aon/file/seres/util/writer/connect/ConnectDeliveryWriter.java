package com.esferalia.aon.file.seres.util.writer.connect;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
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
import com.esferalia.aon.file.seres.util.SeresUtils;

public class ConnectDeliveryWriter {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(ConnectDeliveryWriter.class);


	public FileOutput createFile(Delivery delivery, String companyEdiCode,
			String customerEdiCode, String deliveryPointEdiCode) throws FileNotFoundException,
			UnsupportedEncodingException {
		RECTL rectl = createRECTLRecord(delivery, companyEdiCode,
				customerEdiCode, deliveryPointEdiCode);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);
		FileFiller filler = new ConnectDelivery(rectl, writer);
		FileOutput output = new FileOutput();
		output.setErrors(filler.create());
		output.setContent(outputStream.toString().getBytes(SeresUtils.DEFAULT_CHARSET_ENC));
		return output;
	}

	private RECTL createRECTLRecord(Delivery delivery, String companyEdiCode,
			String customerEdiCode, String deliveryPointEdiCode) {
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
		rectl.seh1pList = createSEH1PList(delivery, companyEdiCode,
				customerEdiCode);
//		rectl.seh1lList = createSEH1LList(delivery, companyEdiCode,
//				customerEdiCode);
		rectl.seh1gList = createSEH1GList(delivery);
		rectl.seh1bList = createSEH1BList(delivery);
		
		return rectl;
	}
	
	/*
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
		seh1c.setFecha_horaEstimadaDeEntrega_17__102_203_(null);
		seh1c.setCalificadorFecha_Hora1_2_11_64_(null);
		seh1c.setFecha_hora1(null);
		seh1c.setCalificadorFecha_Hora2_2_11_63_(null);
		seh1c.setFecha_hora2(null);
		seh1c.setInformacionAdicional(null);
		seh1c.setNumeroPedido_comprador__ON_(obtainPurchaseReference(delivery));
		seh1c.setFecha_horaNumeroPedido_171__102_203_(null);
		seh1c.setNumeroAlbaran_DQ_(null);
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
				customerEdiCode, delivery.getCustomer()));
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

	private List<SEH1P> createSEH1PList(Delivery delivery, 
			String companyEdiCode, String customerEdiCode) {
		List<SEH1P> list = new ArrayList<>();
		
		delivery.getDetailList().stream()
				.map(to -> (DeliveryDetail)to)
				.forEach( to -> {
					DeliveryDetail detail = (DeliveryDetail) to;
					if(isPackageItem(detail.getItem())){
						list.add(createSEH1PRecord(detail, list.size()+1));
					}
				});
		
		SEH1P mainPackage = list.stream()
				.filter(o -> o.getTipoDeEmbalaje_Codificado().equals("201"))
				.findFirst().orElse(null);
		
		if(mainPackage!=null) {
			int packageCount = list.stream()
					.filter(o -> o.getTipoDeEmbalaje_Codificado().equals("201"))
					.mapToInt(SEH1P::getNumeroDePaquetes)
					.sum();
			mainPackage.setNumeroDePaquetes(packageCount);
			
			List<SEH1P> list2 = list.stream()
					.filter(o -> !o.getTipoDeEmbalaje_Codificado().equals("201"))
					.collect(Collectors.toList());
			list2.add(0, mainPackage);
			
			list2.stream()
					.filter(o -> !o.getTipoDeEmbalaje_Codificado().equals("201"))
					.forEach(o -> {
						Integer line = Integer.parseInt(o.getNumeroDeJerarquiaDeEmbalaje());
						o.setNumeroDeJerarquiaDeEmbalaje(String.valueOf(line-packageCount+1));
						o.setNumeroDeJerarquiaPadreDeEmbalaje(mainPackage.getNumeroDeJerarquiaDeEmbalaje());
					});
			
			
			fillPackageLines(delivery, list2, companyEdiCode, customerEdiCode);
			
			return list2;
		}
		
		return list;
	}
	
	private void fillPackageLines(Delivery delivery, List<SEH1P> packageList,
			String companyEdiCode, String customerEdiCode){
		
		Map<Integer, List<Integer>> seh1pMap = obtainSeh1pMap(delivery);
//		System.out.println(seh1pMap);
		List<Integer> containerList = new ArrayList<>();
		containerList.addAll(seh1pMap.keySet());
		
		seh1pMap.keySet().stream().sorted().forEach(key -> {
			List<Integer> list = seh1pMap.get(key);
			containerList.removeAll(list);	
		});
//		System.out.println(containerList);
		
		
		SEH1P mainPalet = packageList.get(0);
		mainPalet.seh1lList = new ArrayList<>();
		
		containerList.forEach(palet -> {
			List<Integer> content = seh1pMap.get(palet);
			content.forEach(contentIdx ->{
				DeliveryDetail detail = (DeliveryDetail) delivery.getDetailList().get(contentIdx-1);
				if(!isPackageItem(detail.getItem())){
					mainPalet.seh1lList.add(createSEH1LRecord(detail,
							companyEdiCode, customerEdiCode));
				}
			});
		});
		
		
		Map<Integer, List<Integer>> seh1lMap = obtainSeh1lMap(delivery);
//		System.out.println(seh1lMap);
		int containerCount = containerList.size();
		int linesCount = seh1lMap.values().size();
		for(int idx=1; idx<packageList.size(); idx++){
			SEH1P palet = packageList.get(idx);
			palet.seh1lList = new ArrayList<>();
			
			List<Integer> content = seh1lMap.get(idx+containerCount+linesCount);
			if(content!=null && !content.isEmpty()){
				content.forEach(contentIdx ->{
					DeliveryDetail detail = (DeliveryDetail) delivery.getDetailList().get(contentIdx-1);
					palet.seh1lList.add(createSEH1LRecord(detail,
							companyEdiCode, customerEdiCode));
				});
			}
		}
		
	}

	private List<SEH1L> createSEH1LList(Delivery delivery,
			String companyEdiCode, String customerEdiCode) {
		List<SEH1L> list = new ArrayList<>();
		delivery.getDetailList().forEach(
				to -> {
					DeliveryDetail detail = (DeliveryDetail) to;
					if(!isPackageItem(detail.getItem())){
						list.add(createSEH1LRecord(detail,
								companyEdiCode, customerEdiCode));
					}
				});
		return list;
	}

	// TODO createSEH1GList
	private List<SEH1G> createSEH1GList(Delivery delivery) {
		List<SEH1G> list = new ArrayList<>();
		createSEH1GRecord(delivery);
		return list;
	}

	// TODO createSEH1BList
	private List<SEH1B> createSEH1BList(Delivery delivery) {
		List<SEH1B> list = new ArrayList<>();
		createSEH1BRecord(delivery);
		return list;
	}

	/*
	 * Información de partes
	 */
	private SEH1D createSEH1DRecord(SEH1D.SEH1D_2 type, String ediCode, IRegistry registry) {
		SEH1D record = new SEH1D();
		record.setCalificadorDelInterlocutor(type.getValue());
		record.setCodigoInterlocutor(ediCode);
		record.setAgenciaResponsableDeLaListaDeCodigos(null);
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

	/*
	 * Secuencia de embalajes
	 */
	private SEH1P createSEH1PRecord(DeliveryDetail detail, int lineNumber) {
		// TODO createSEH1PRecord
		
		String format = "";
//		CT - Caja de cartón
//		CS - Caja rígida
//		PK - Paquete
//		SL - Placa de Plástico
//		SW - Retractilado
//		RO - Enrollado
//		09 - Pallet retornable
//		08 - Pallet no retornable
//		201 - Pallet ISO 1 - 1/1 EURO Pallet
		try {
			if(detail.getItem().getProduct().getName()!=null){
				format = detail.getItem().getProduct().getName();
			}
			if(detail.getItem().getProduct().getBaseItem().getPackFormatTag()!=null){
				format = detail.getItem().getProduct().getBaseItem().getPackFormatTag().getName();
			}
		} catch (ManagerBeanException e) {
			// nada
		}
		
		if(format.toLowerCase().contains("bolsa")){
			format = "CT";
		} else if(format.toLowerCase().contains("box")){
			format = "CT";
		} else if(format.toLowerCase().contains("caja")){
			format = "CT";
		} else if(format.toLowerCase().contains("kg")){
			format = "CT";
		} else if(format.toLowerCase().contains("palet")){
			format = "201";
		} else if(format.toLowerCase().contains("saco")){
			format = "CT";
		} else {
			format = "CT";
		}
		
		return createSEH1PRecord(lineNumber, (int)detail.getQuantity(), format);
	}
		
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

	/*
	 * Línea de artículos
	 */
	private SEH1L createSEH1LRecord(DeliveryDetail detail,
			String companyEdiCode, String customerEdiCode) {
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
		record.setCantidadEnviada_12_(detail.getQuantity());
		record.setUnidadDeMedidaCantidadEnviada(null);
		record.setUnidadesDeConsumoEnUnidadDeExpedicion_59_(null);
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

	/*
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

	/*
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
	

	private Map<Integer, List<Integer>> obtainSeh1pMap(Delivery delivery) {
		String remarks = delivery.getRemarks();
		
		if(remarks!=null && !"".equals(remarks)){
			Map<Integer, List<Integer>> seh1pMap = new HashMap<>();
			
			Pattern pattern = Pattern.compile("\\[(ENV=\\d{1,3});(CONT=\\d{1,3})\\]");
			Matcher matcher = pattern.matcher(remarks);
			while (matcher.find()) {	
				String value1 = matcher.group(1).replaceFirst("ENV=", "");
				String value2 = matcher.group(2).replaceFirst("CONT=", "");
				Integer key = Integer.parseInt(value2);
				Integer value = Integer.parseInt(value1);
				List<Integer> list = new LinkedList<>();
				list.add(value);
				if(seh1pMap.containsKey(key))
					seh1pMap.get(key).addAll(list);
				else
					seh1pMap.put(key, list);
			}
			
			return seh1pMap;
		}
		return null;
	}
	
	private Map<Integer, List<Integer>> obtainSeh1lMap(Delivery delivery) {
		String remarks = delivery.getRemarks();
		
		if(remarks!=null && !"".equals(remarks)){
			Map<Integer, List<Integer>> seh1pMap = new HashMap<>();
			
			Pattern pattern = Pattern.compile("\\[(ENV=\\d{1,3});(LIN=\\d{1,3})\\]");
			Matcher matcher = pattern.matcher(remarks);
			while (matcher.find()) {
				String value1 = matcher.group(1).replaceFirst("ENV=", "");
				String value2 = matcher.group(2).replaceFirst("LIN=", "");
				Integer key = Integer.parseInt(value1);
				Integer value = Integer.parseInt(value2);
				List<Integer> list = new LinkedList<>();
				list.add(value);
				if(seh1pMap.containsKey(key))
					seh1pMap.get(key).addAll(list);
				else
					seh1pMap.put(key, list);
			}
			
			return seh1pMap;
		}
		return null;
	}
	

}

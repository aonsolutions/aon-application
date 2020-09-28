package com.esferalia.aon.seres.writer.connect;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.esferalia.aon.file.seres.connect.ConnectDelivery;
import com.esferalia.aon.file.seres.connect.delivery.v4.data.RECTL;
import com.esferalia.aon.file.seres.connect.delivery.v4.data.SEH1B;
import com.esferalia.aon.file.seres.connect.delivery.v4.data.SEH1C;
import com.esferalia.aon.file.seres.connect.delivery.v4.data.SEH1D;
import com.esferalia.aon.file.seres.connect.delivery.v4.data.SEH1G;
import com.esferalia.aon.file.seres.connect.delivery.v4.data.SEH1L;
import com.esferalia.aon.file.seres.connect.delivery.v4.data.SEH1P;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.seres.SeresUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class ConnectDeliveryWriterOccam  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory
			.getLogger(ConnectDeliveryWriterOccam.class);
	
	private String domainName;
	private Integer domainId;
	private String login;

	public ConnectDeliveryWriterOccam(String domainName, Integer domainId, String login) {
		this.domainName = domainName;
		this.domainId = domainId;
		this.login = login;
	}

	public FileOutput createFile(Delivery delivery, String packageData, String companyEdiCode,
			String customerEdiCode, String deliveryPointEdiCode,
			String customerPackage, String department) throws FileNotFoundException, UnsupportedEncodingException {
		RECTL rectl = createRECTLRecord(delivery, packageData, companyEdiCode,
				customerEdiCode, deliveryPointEdiCode, customerPackage, department);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);
		FileFiller filler = new ConnectDelivery(rectl, writer);
		FileOutput output = new FileOutput();
		output.setErrors(filler.create());
		output.setContent(outputStream.toByteArray());
		return output;
	}

	private RECTL createRECTLRecord(Delivery delivery, String packageData, String companyEdiCode,
			String customerEdiCode, String deliveryPointEdiCode, String customerPackage, String department) {
		RECTL rectl = new RECTL();
		rectl.setTipoDeMensaje(RECTL.RECTL_2.AVISO_DE_EXPEDICION_DESADV.getValue());
		rectl.setCodigoEmisor(companyEdiCode);
		rectl.setCodigoReceptor(customerEdiCode);
		rectl.setIdentificacionDelMensaje(SeresUtils.dateTimeFormat().format(new Date()));
		rectl.setFecha_horaDelMensaje(SeresUtils.dateTimeFormat().format(new Date()));
		
		rectl.seh1c = createSEH1CRecord(delivery, companyEdiCode,
				customerEdiCode, deliveryPointEdiCode);
		rectl.seh1dList = createSEH1DList(delivery, companyEdiCode,
				customerEdiCode, deliveryPointEdiCode, department);
		rectl.seh1pList = createSEH1PList(delivery, packageData, companyEdiCode,
				customerEdiCode, customerPackage);
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
	
		String referenceCode = delivery.getReferenceCode();
		if(isECI(delivery.getCustomerDocument())) {
			referenceCode = "";
			for(Integer i = 0; i < delivery.getReferenceCode().length(); i++) {
				if(AonNumberUtils.isNumber("" + delivery.getReferenceCode().charAt(i))) {
					referenceCode.concat(delivery.getReferenceCode().charAt(i)+ "");
				}
			}
		}
		
		seh1c.setTipoDeDocumento_351_35E_(SEH1C.SEH1C_2.NOTAS_DE_ENVIO_351
				.getValue());
		seh1c.setNumeroDelDocumento(delivery.getReferenceCode());
		seh1c.setFuncionDelMensaje(SEH1C.SEH1C_4.ORIGINAL___EL_ENVIO_DE_UN_AVISO_DE_EXPEDICION_ORIGINAL_9
				.getValue());
		seh1c.setFecha_horaDelDocumento_137__102_203_(SeresUtils.dateTimeFormat().format(delivery.getIssueTime()));
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
		String deliveryAddress = getDeliveryFullAddress(delivery.getAddress());
		deliveryAddress = deliveryAddress != null
				&& deliveryAddress.length() > 70 ? StringUtils.abbreviate(
				deliveryAddress, 70) : deliveryAddress;
		seh1c.setLugarDeEntrega_TextoLibre_8_(deliveryAddress);
		return seh1c;
	}

	private List<SEH1D> createSEH1DList(Delivery delivery,
			String companyEdiCode, String customerEdiCode,
			String deliveryPointEdiCode, String department) {
		List<SEH1D> list = new ArrayList<>();

		list.add(createSEH1DRecord(SEH1D.SEH1D_2.EMISOR_DEL_MENSAJE_MS,
				companyEdiCode, getWorkPlace(delivery.getWorkplace()).getEnterprise(), department));
		list.add(createSEH1DRecord(SEH1D.SEH1D_2.RECEPTOR_DEL_MENSAJE_MR,
				customerEdiCode, delivery.getCustomer(), department));
		// list.add(createSEH1DRecord(SEH1D.SEH1D_2.PROVEEDOR__SU,
		// null, null));
		list.add(createSEH1DRecord(
				SEH1D.SEH1D_2.PUNTO_DESDE_DONDE_SE_ENVIAN_LAS_MERCANCIAS_PW,
				companyEdiCode, getWorkPlace(delivery.getWorkplace()).getEnterprise(), department));
		list.add(createSEH1DRecord(
				SEH1D.SEH1D_2.PUNTO_DESTINO_DE_LA_MERCANCIA_DP,
				deliveryPointEdiCode, delivery.getCustomer(), department));
		// list.add(createSEH1DRecord(SEH1D.SEH1D_2.DESTINATARIO_FINAL_UC,
		// null, null));
		list.add(createSEH1DRecord(SEH1D.SEH1D_2.COMPRADOR_BY, customerEdiCode,
				delivery.getCustomer(), department));
		list.add(createSEH1DRecord(SEH1D.SEH1D_2.EXPEDIDOR_SH, companyEdiCode,
				getWorkPlace(delivery.getWorkplace()).getEnterprise(), department));
		list.add(createSEH1DRecord(SEH1D.SEH1D_2.A_QUIEN_SE_FACTURA_IV,
				customerEdiCode, delivery.getCustomer(), department));

		return list;
	}

	private List<SEH1P> createSEH1PList(Delivery delivery, String packageData, 
			String companyEdiCode, String customerEdiCode, String customerPackage) {
		
		List<SEH1P> list = new ArrayList<>();
		List<DeliveryDetail> detailList = getDetailList(delivery.getId()).stream()
				.map(to -> (DeliveryDetail)to)
				.sorted((d1, d2)->Short.compare(d1.getLine(),d2.getLine()))
				.collect(Collectors.toList());
		
		Map<Integer, List<Integer>> level1Map = DeliveryPackages.loadLevel1Map(packageData, detailList);
		Map<Integer, List<Integer>> level2Map = DeliveryPackages.loadLevel2Map(packageData);
		Map<Integer, List<Integer>> level3Map = DeliveryPackages.loadLevel3Map(packageData);
		Map<Integer, String> ssccMap = DeliveryPackages.loadSSCCMap(packageData);
		
		int mainPackageLine = 0;
		int packageLine = 0;
		int mainPackageSize = 0;
		for(Integer level1Key: level1Map.keySet()){
			List<Integer> packageLineList = level1Map.get(level1Key);
			
			// MAIN-PACKAGE
			mainPackageSize = (int)detailList.stream()
				.filter(detail->packageLineList.contains((int)detail.getLine()))
				.mapToDouble(DeliveryDetail::getQuantity).sum();
			mainPackageLine = ++packageLine;
			SEH1P mainPackage = createSEH1PRecord(mainPackageLine, mainPackageSize, "201", null);
			mainPackage.seh1lList = new ArrayList<>();
			list.add(mainPackage);
			
			// SUB-PACKAGE OR PRODUCT OVER MAIN-PACKAGE
			for(Integer level2Key: level2Map.keySet()){
				if(packageLineList.contains(level2Key)){
					
					List<Integer> level2LineList = new LinkedList<>(level2Map.get(level2Key));
					for(int level2LineId: level2LineList){
						DeliveryDetail level2Detail = (DeliveryDetail) detailList.get(level2LineId-1);
						if(!isPackageItem(level2Detail.getItem().getId())) {
							Integer mainPackageKey = level1Map.get(level1Key).get(0);
							completePackageSSCC(mainPackage, ssccMap.get(mainPackageKey));
							int lineNumber = mainPackage.seh1lList.size()+1;
							mainPackage.seh1lList.add(createSEH1LRecord(lineNumber, delivery, level2Detail, null,
									companyEdiCode, customerEdiCode, customerPackage));
						} else {
							SEH1P subPackage = null;
							
							// PRODUCT OVER SUB-PACKAGE, IF EXIST
							List<Integer> level3LineList = new LinkedList<>(level3Map.get((int)level2Detail.getLine()));
							for(int level3LineId: level3LineList){
								DeliveryDetail level3Detail = (DeliveryDetail) detailList.get(level3LineId-1);
								SEH1P p = searchExistingPackage(list, level3Detail, ssccMap.get(level2Key));
								if(p==null && subPackage==null){
									subPackage = createSEH1PRecord(++packageLine, (int) level2Detail.getQuantity(), "CT", ssccMap.get(level2Key));
									subPackage.setNumeroDeJerarquiaPadreDeEmbalaje(mainPackage.getNumeroDeJerarquiaDeEmbalaje());
									subPackage.seh1lList = new ArrayList<>();
									list.add(subPackage);
								}
								addLine(list, (p!=null?p:subPackage), delivery, level3Detail, level2Detail.getQuantity(), ssccMap.get(level2Key),
										companyEdiCode, customerEdiCode, customerPackage);
							}
						}
					}
				}				
			}
			
		}
		
		return list;
	}
	
	private void completePackageSSCC(SEH1P seh1p, String sscc) {
		seh1p.setNumeroSerial1ONumeroDeIdentificacionInferior(StringUtils.leftPad(sscc, 18, "0"));
	}
	
	private void addLine(List<SEH1P> list, SEH1P targetPackage, Delivery delivery, DeliveryDetail detail, Double packageQuantity,
			String sscc, String companyEdiCode, String customerEdiCode, String customerPackage) {
		String seralNumber = getItem(detail.getItem().getId()).getSerialNumber();
		boolean success = false;
		for(SEH1P p: list){
			if( StringUtils.isBlank(p.getNumeroSerial1ONumeroDeIdentificacionInferior())
					|| StringUtils.equals(sscc, p.getNumeroSerial1ONumeroDeIdentificacionInferior()) ){
				for(SEH1L l: p.seh1lList){
					if(l.getNumeroDeLote_NB_()!=null && !"".equals(l.getNumeroDeLote_NB_())
							&& l.getNumeroDeLote_NB_().equals(seralNumber)){
						SEH1L newLine = createSEH1LRecord(0, delivery, detail, packageQuantity,
								companyEdiCode, customerEdiCode, customerPackage);
						l.setCantidadEnviada_12_(l.getCantidadEnviada_12_()+newLine.getCantidadEnviada_12_());
						if(packageQuantity!=null){
							p.setNumeroDePaquetes(p.getNumeroDePaquetes()+packageQuantity.intValue());
						}
						success = true;
					}
				}
			}
		}
		if(!success){
			int lineNumber = targetPackage.seh1lList.size()+1;
			targetPackage.seh1lList.add(createSEH1LRecord(lineNumber, delivery, detail, packageQuantity,
					companyEdiCode, customerEdiCode, customerPackage));
		}
	}
	
	private SEH1P searchExistingPackage(List<SEH1P> list, DeliveryDetail detail, String sscc) {
		String seralNumber = getItem(detail.getItem().getId()).getSerialNumber();
		boolean success = false;
		SEH1P p = null;
		Iterator<SEH1P> packageIt = list.iterator();
		while(packageIt.hasNext() && !success){
			p = packageIt.next();
			if( StringUtils.isBlank(p.getNumeroSerial1ONumeroDeIdentificacionInferior())
					|| StringUtils.equals(sscc, p.getNumeroSerial1ONumeroDeIdentificacionInferior()) ){
				Iterator<SEH1L> lineIt = p.seh1lList.iterator();
				while(lineIt.hasNext() && !success){
					SEH1L l = lineIt.next();
					success = StringUtils.equals(l.getNumeroDeLote_NB_(), seralNumber);
				}
			}
		}
		return success?p:null;
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
	private SEH1D createSEH1DRecord(SEH1D.SEH1D_2 type, String ediCode, Integer registryId, String department) {
		Registry registry = getRegistry(registryId);
		SEH1D record = new SEH1D();
		record.setCalificadorDelInterlocutor(type.getValue());
		record.setCodigoInterlocutor(ediCode);
		record.setAgenciaResponsableDeLaListaDeCodigos(SEH1D.SEH1D_4.EAN_9.getValue());
		record.setNombre1(registry.getName().replaceAll("[^\\w\\.,\\s/-]", "?"));
		record.setNombre2(null);
		record.setNombre3(null);
		record.setNombre4(null);
		record.setNombre5(null);
		try {
			RAddress defaultAddress = getDefaultAddress(registryId);
			record.setCalleYNumero1(defaultAddress.getAddress().replaceAll("[^\\w\\.,\\s/-]", "?"));
			record.setCalleYNumero2(defaultAddress.getAddress2().replaceAll("[^\\w\\.,\\s/-]", "?"));
			record.setCalleYNumero3(defaultAddress.getAddress3().replaceAll("[^\\w\\.,\\s/-]", "?"));
			record.setCalleYNumero4(defaultAddress.getNumber());
			record.setPoblacion(defaultAddress.getCity().replaceAll("[^\\w\\.,\\s/-]", "?"));
			record.setCodigoPostal(defaultAddress.getZip());
			GeoZone geozone = getGeozone(defaultAddress.getGeozone());
			if(geozone!=null && geozone.getId()!=null){
				record.setProvincia(geozone.getName());
				record.setCodigoPais(getGeoZoneCountry(geozone.getId()).getCode());
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		record.setCalificadorReferencia1(null);
		record.setReferencia1(null);
		record.setFuncionDeContacto(null);
		record.setDepartamentoOIdentificacionDelEmpleado(department);
		record.setDepartamentoOEmpleado(department);
		record.setCalificadorReferencia2(null);
		record.setReferencia2(null);
		return record;
	}

	/**
	 * Secuencia de embalajes
	 */
	private SEH1P createSEH1PRecord(int lineNumber, int quantity, String format, String sscc) {
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
		if(sscc!=null)
			record.setNumeroSerial1ONumeroDeIdentificacionInferior(StringUtils.leftPad(sscc, 18, "0"));
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
	private SEH1L createSEH1LRecord(Integer lineNumber, Delivery delivery, DeliveryDetail detail, Double packageQuantity,
			String companyEdiCode, String customerEdiCode, String customerPackage) {
		Item item = getItem(detail.getItem().getId());
		Integer customerId = delivery.getCustomer();
		String productCustomerCode = obtainProductCustomerCode(item, customerId);
		
		SEH1L record = new SEH1L();
		record.setNumeroDeLineaDelArticulo(lineNumber);
		record.setCodigoEANDelArticulo(productCustomerCode);
		record.setDescripcionDelArticulo(item.getProduct().getName());
		record.setTipoDeIdentificacionDelArticulo_CU_DU_("CU");
		record.setNumeroDeArticuloDelProveedor_SA_(productCustomerCode);
		record.setNumeroVariablePromocional_PV_(null);
		record.setCodigoDUN_14_ADU_(null);
		record.setCodigoACU_ACU_(null);
		record.setNumeroDeLote_NB_(item.getSerialNumber());
		record.setNumeroDeArticuloDelComprador_IN_(null);
		
		double quantity = 0.0;
		double packUnits = item.getPackUnits();
		if(packageQuantity==null){
			quantity = obtainPackageQuantity(detail, customerPackage);
		} else {
			quantity = packUnits * packageQuantity;
		}
		
		if(item.getPackFormatTag().getName().equals(item.getPackUnitsTag().getName())){
			record.setCantidadEnviada_12_(quantity * item.getPackUnits() * item.getPackMeasurement());
			if(item.getPackMeasurementTag()!=null && item.getPackMeasurementTag().getName()!=null){
				record.setUnidadDeMedidaCantidadEnviada(
						StringUtils.substring(item.getPackMeasurementTag().getName(), 0, 3).toUpperCase());
			}
			record.setUnidadesDeConsumoEnUnidadDeExpedicion_59_(item.getPackUnits() * item.getPackMeasurement());
		} else {
			record.setCantidadEnviada_12_(quantity);
			record.setUnidadDeMedidaCantidadEnviada(null);
			record.setUnidadesDeConsumoEnUnidadDeExpedicion_59_(packUnits);
		}
		record.setFechaDeCaducidad_36__102_203_(SeresUtils.dateFormat().format(item.getSerialDate()));
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
		record.setPesoTotalNetoDeLaLinea_AAI_AAF_(quantity * item.getPackMeasurement());
		record.setPesoTotalBrutoDeLaLinea_AAI_AAB_(null);
		if(item.getPackMeasurementTag()!=null && item.getPackMeasurementTag().getName()!=null){
			record.setUnidadDeMedidaPeso(
					StringUtils.substring(item.getPackMeasurementTag().getName(), 0, 3).toUpperCase());
		}
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
	
	private String getDeliveryFullAddress(Integer addressId){
		RAddress address =  getDeliveryAddress(addressId);
		StringBuffer buf = new StringBuffer();
    	buf.append((address.getStreet_type()!=null) ? address.getStreet_type() : "");
    	buf.append((address.getStreet_type()!=null) ? ". " : "");
    	buf.append(StringUtils.isEmpty(address.getAddress())? "":address.getAddress());
    	buf.append(StringUtils.isEmpty(address.getNumber())?"":" ");
    	buf.append(StringUtils.isEmpty(address.getNumber())?"":address.getNumber());
    	buf.append(StringUtils.isEmpty(address.getAddress2())?"":", ");
    	buf.append(StringUtils.isEmpty(address.getAddress2())?"":address.getAddress2());
    	buf.append(StringUtils.isEmpty(address.getAddress3())?"":" (");
    	buf.append(StringUtils.isEmpty(address.getAddress3())?"":address.getAddress3());
    	buf.append(StringUtils.isEmpty(address.getAddress3())?"":")");
    	return buf.toString();
	}
	
	public RAddress getDeliveryAddress(Integer addressId) {
		return AON.getRAddress(domainName, domainId, login, f -> f.getIdProperty().eq(addressId));
	}
	
	private List<DeliveryDetail> getDetailList(Integer deliveryId) {
		return AON.getDeliveryDetailStream(domainName, domainId, login, f -> f.getDelivery().eq(deliveryId))
				.collect(Collectors.toList());
	}
	
	private Workplace getWorkPlace(Integer workplaceId) {
		return AON.getWorkplace(domainName, domainId, login, f -> f.getIdProperty().eq(workplaceId));
	}
	
	private Registry getRegistry(Integer registryId) {
		return AON.getRegistry(domainName, domainId, login, registryId);
	}
	
	private RAddress getDefaultAddress(Integer registryId) {
		return AON.getRAddres(domainName, domainId, login, registryId);
	}
	
	private GeoZone getGeozone(Integer geozoneId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private GeoZone getGeoZoneCountry(Integer geozoneId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private SalesDetail getSalesDetail(Integer salesDetail) {
		if(salesDetail != null){
			return AON.getSalesDetailStream(domainName, domainId, login, f -> f.getIdProperty().eq(salesDetail))
					.findFirst().orElse(new SalesDetail());
		} else {
			return null;
		}
	}
	
	private Integer getBaseItemId(Product product){
		try {
			com.esferalia.aon.occam.api.model.product.Item item = AON.getItem(domainName, domainId, login, f -> f
					.getProductProperty().eq(product.getId())
					.and(f.getSerialNumberProperty().isNull()));
			if(item!=null)
				return item.getId();
		} catch (Throwable e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	private Item getItem(Integer itemId){
		try {
			return AON.getItem(domainName, domainId, login, itemId);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	
	private String getItemBarcode(Integer itemId){
		try {
			Item item = getItem(itemId);
			if(item!=null)
				return item.getBarcode();
		} catch (Throwable e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	
	private String obtainPurchaseReference(Delivery delivery) {
		List<DeliveryDetail> list = getDetailList(delivery.getId()).stream()
				.map(to -> ((DeliveryDetail) to)).collect(Collectors.toList());
		if (!list.isEmpty()) {
			DeliveryDetail detail = list.get(0);
			if (detail.getSalesDetail() != null) {
				Sales sales = getSalesDetail(detail.getSalesDetail()).getSales();
				return sales.getPurchaseReference();
			}
		}
		return null;
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

	private boolean isPackageItem(Integer itemId) {
		Item item = getItem(itemId);
		return item != null && item.getSerialNumber() == null
				&& item.getSerialDate() == null;
	}
	
	private Double obtainPackageQuantity(DeliveryDetail detail,
			String customerPackingTag) {
		if(detail!=null && customerPackingTag!=null){
			Item item = getItem(detail.getItem().getId());
			Double quantity = detail.getQuantity();
			Tag itemPackFormatTag = item.getPackFormatTag();
			Tag itemPackMeasurementTag = item.getPackMeasurementTag();
			Tag itemPackingTag = item.getPackUnitsTag();
			double itemPackMeasurement = item.getPackMeasurement();
			double itemPackUnits = item.getPackUnits();
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
	
	// //////////////////////////////////////////////////////////////////////
	// Re-Code of class 'com.esferalia.aon.file.seres.util.DeliveryPackages'
	// //////////////////////////////////////////////////////////////////////
	// TODO: avoid using duplicate code for class DeliveryPackages
	public static class DeliveryPackages {
		
		public static Attach obtainPackageDataAttach(String domainName, int domainId, String user, Integer deliveryId) {
			if (deliveryId != null) {
				Attach attach = AON.getAttach(
						domainName,
						domainId,
						user,
						f -> f.getDomainProperty()
								.eq(domainId)
								.and(f.getSourceTypeProperty().eq(
										DataAttachSource.DELIVERY.value()))
								.and(f.getSourceBatchProperty()
										.eq(deliveryId)), AttachType.DATA,
						true);
				attach.setAttachType(AttachType.DATA);
				return attach;
			}
			return null;
		}
		
		private static Collection<Integer> loadLevel1List(String data) {
			Map<Integer, List<Integer>> containerMap = loadContainerMap(data);
			Map<Integer, List<Integer>> linesMap = loadLinesMap(data);
			Collection<Integer> c = CollectionUtils.subtract(linesMap.keySet(), containerMap.keySet());
			containerMap.values().forEach( list -> {
				c.removeAll(CollectionUtils.subtract(list, c));
			});
			return CollectionUtils.union(containerMap.keySet(), c);
		}
		
		public static Map<Integer, List<Integer>> loadLevel1Map(String data, List<DeliveryDetail> detailList) {
			Collection<Integer> level1List =  loadLevel1List(data);
			Map<Integer, List<Integer>> level1Map = new LinkedHashMap<>();
			if(detailList!=null){
				level1List.forEach(id->{
					DeliveryDetail packageLine = (DeliveryDetail) detailList.get(id-1);
					
					List<Integer> packageList = new LinkedList<>();
					packageList.add(id);
					int itemId = packageLine.getItem().getId();
					if (level1Map.containsKey(itemId))
						level1Map.get(itemId).addAll(packageList);
					else
						level1Map.put(itemId, packageList);
				});
			}
			return level1Map;
		}
		
		public static Map<Integer, List<Integer>> loadLevel2Map(String data) {
			Collection<Integer> containerList =  loadLevel1List(data);
			Map<Integer, List<Integer>> containerMap = loadContainerMap(data);
			Map<Integer, List<Integer>> linesMap = loadLinesMap(data);
			
			
			Map<Integer, List<Integer>> level2Map = new LinkedHashMap<>();
			containerList.forEach(id->{			
				if(containerMap.containsKey(id)){
					level2Map.put(id, containerMap.get(id));
				} else if(linesMap.containsKey(id)){
					level2Map.put(id, linesMap.get(id));
				} else {
					System.out.println("ERROR! package-id not found");
				}
			});
			return level2Map;
		}
		
		public static Map<Integer, List<Integer>> loadLevel3Map(String data) {
			Map<Integer, List<Integer>> containerMap = loadContainerMap(data);
			Map<Integer, List<Integer>> linesMap = loadLinesMap(data);
			
			Map<Integer, List<Integer>> level3Map = new LinkedHashMap<>();
			containerMap.keySet().forEach(key->{
				containerMap.get(key).forEach( id -> {
					if(linesMap.containsKey(key)){
						level3Map.put(id, linesMap.get(key));
					}
				});
			});
			return level3Map;
		}
		
		public static Map<Integer, List<Integer>> loadContainerMap(String data) {
			String regex = "\\[(ENV=\\d{1,3});(CONT=\\d{1,3})(;SSCC=\\w{1,})?\\]";
			String keyPrefix = "CONT=", valuePrefix = "ENV=";
			int keyGroup = 2, valueGroup = 1;
			return obtainPackagesMap(data, 
					regex, keyPrefix, valuePrefix, keyGroup, valueGroup);
		}
		
		public static Map<Integer, List<Integer>> loadLinesMap(String data) {
			String regex = "\\[(ENV=\\d{1,3});(LIN=\\d{1,3})(;SSCC=\\w{1,})?\\]";
			String keyPrefix = "ENV=", valuePrefix = "LIN=";
			int keyGroup = 1, valueGroup = 2;
			return obtainPackagesMap(data, 
					regex, keyPrefix, valuePrefix, keyGroup, valueGroup);
		}
		
		public static Map<Integer, String> loadSSCCMap(String data) {
			String regexCont = "\\[(ENV=\\d{1,3});(CONT=\\d{1,3})(;SSCC=\\w{1,})?\\]";
			String keyPrefixCont = "CONT=", ssccPrefixCont = "SSCC=";
			int keyGroupCont = 2, ssccGroupCont = 3;
			Map<Integer, String> contMap = obtainSSCCMap(data, 
					regexCont, keyPrefixCont, ssccPrefixCont, keyGroupCont, ssccGroupCont);
			
			String regexLin = "\\[(ENV=\\d{1,3});(LIN=\\d{1,3})(;SSCC=\\w{1,})?\\]";
			String keyPrefixLin = "ENV=", ssccPrefixLin = "SSCC=";
			int keyGroupLin = 1, ssccGroupLin = 3;		
			Map<Integer, String> linMap = obtainSSCCMap(data, 
					regexLin, keyPrefixLin, ssccPrefixLin, keyGroupLin, ssccGroupLin);
			
			contMap.putAll(linMap);
			return contMap;
		}
		
		private static Map<Integer, List<Integer>> obtainPackagesMap(String data,
				String regex,
				String keyPrefix, String valuePrefix, int keyGroup, int valueGroup) {
			Map<Integer, List<Integer>> map = new LinkedHashMap<>();
			if (data != null && !"".equals(data)) {
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(data);
				while (matcher.find()) {
					String _key = matcher.group(keyGroup).replaceFirst(keyPrefix, "").replaceAll(";", "");
					String _value = matcher.group(valueGroup).replaceFirst(valuePrefix, "").replaceAll(";", "");
					Integer key = Integer.parseInt(_key);
					Integer value = Integer.parseInt(_value);
					List<Integer> list = new LinkedList<>();
					list.add(value);
					if (map.containsKey(key))
						map.get(key).addAll(list);
					else
						map.put(key, list);
				}
			}
			map = map
					.entrySet()
					.stream()
					.sorted(Map.Entry.comparingByKey())
					.collect(
							Collectors.toMap(Map.Entry::getKey,
									Map.Entry::getValue, (x, y) -> {
										throw new AssertionError();
									}, LinkedHashMap::new));
			return map;
		}
		
		private static Map<Integer, String> obtainSSCCMap(String data,
				String regex,
				String keyPrefix, String ssccPrefix, int keyGroup, int ssccGroup) {
			Map<Integer, String> map = new LinkedHashMap<>();
			if (data != null && !"".equals(data)) {
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(data);
				while (matcher.find()) {
					String _sscc = matcher.group(ssccGroup);
					if(StringUtils.isNotBlank(_sscc)){
						String _key = matcher.group(keyGroup).replaceFirst(keyPrefix, "").replaceAll(";", "");
						_sscc = _sscc.replaceFirst(ssccPrefix, "").replaceAll(";", "");
						Integer key = Integer.parseInt(_key);
						map.put(key, _sscc);
					}
				}
			}
			map = map
					.entrySet()
					.stream()
					.sorted(Map.Entry.comparingByKey())
					.collect(
							Collectors.toMap(Map.Entry::getKey,
									Map.Entry::getValue, (x, y) -> {
										throw new AssertionError();
									}, LinkedHashMap::new));
			return map;
		}
		
	}
	

	private Boolean isECI(String document) {
		return "A28017895".equalsIgnoreCase(document);
	}

}

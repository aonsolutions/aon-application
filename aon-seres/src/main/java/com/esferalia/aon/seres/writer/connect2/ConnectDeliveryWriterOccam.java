package com.esferalia.aon.seres.writer.connect2;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import com.esferalia.aon.file.seres.connect2.ConnectDelivery;
import com.esferalia.aon.file.seres.connect2.delivery.v4.data.RECTL;
import com.esferalia.aon.file.seres.connect2.delivery.v4.data.SEH1B;
import com.esferalia.aon.file.seres.connect2.delivery.v4.data.SEH1C;
import com.esferalia.aon.file.seres.connect2.delivery.v4.data.SEH1D;
import com.esferalia.aon.file.seres.connect2.delivery.v4.data.SEH1G;
import com.esferalia.aon.file.seres.connect2.delivery.v4.data.SEH1L;
import com.esferalia.aon.file.seres.connect2.delivery.v4.data.SEH1P;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.seres.EdiCodes;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryPackaging;
import com.esferalia.aon.seres.SeresUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class ConnectDeliveryWriterOccam  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectDeliveryWriterOccam.class);
	
	private String domainName;
	private Integer domainId;
	private String login;

	public ConnectDeliveryWriterOccam(String domainName, Integer domainId, String login) {
		this.domainName = domainName;
		this.domainId = domainId;
		this.login = login;
	}
	
	public ConnectDeliveryWriterOccam(Domain domain, String login) {
		this.domainName = domain.getName();
		this.domainId = domain.getId();
		this.login = login;
	}

	public FileOutput createFile(Delivery delivery) throws FileNotFoundException, UnsupportedEncodingException {
		return createFile(delivery, delivery.getPackagingData(), delivery.getEdiCodes());
	}
	
	public FileOutput createFile(Delivery delivery, String packageData, EdiCodes codes) throws FileNotFoundException, UnsupportedEncodingException {
		RECTL rectl = createRECTLRecord(delivery, packageData, codes);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);
		FileFiller filler = new ConnectDelivery(rectl, writer);
		FileOutput output = new FileOutput();
		output.setErrors(filler.create());
		output.setContent(outputStream.toByteArray());
		return output;
	}

	private RECTL createRECTLRecord(Delivery delivery, String packageData, EdiCodes codes) {
		RECTL rectl = new RECTL();
		rectl.setTipoDeMensaje(RECTL.RECTL_2.AVISO_DE_EXPEDICION_DESADV.getValue());
		rectl.setCodigoEmisor(codes.getMscode());
		rectl.setCodigoReceptor(codes.getMrcode());
		rectl.setIdentificacionDelMensaje(SeresUtils.dateTimeFormat().format(new Date()));
		rectl.setFecha_horaDelMensaje(SeresUtils.dateTimeFormat().format(new Date()));
		
		rectl.seh1c = createSEH1CRecord(delivery, codes);
		rectl.seh1dList = createSEH1DList(delivery, codes);
//		rectl.seh1pList = createSEH1PList(delivery, packageData, codes);
		if(packageData != null) {
			rectl.seh1pList = createSEH1PList2(delivery, packageData, codes);			
		} else if(!delivery.getPackaging().isEmpty()) {
			rectl.seh1pList = createSEH1PList2(delivery, codes);
		}

		rectl.seh1gList = createSEH1GList(delivery);
		rectl.seh1bList = createSEH1BList(delivery);
		
		return rectl;
	}
	
	/**
	 * Cabecera
	 */
	private SEH1C createSEH1CRecord(Delivery delivery, EdiCodes codes) {
		SEH1C seh1c = new SEH1C();
		
		String referenceCode = SeresUtils.isECI(delivery.getCustomer().getDocument()) ? referenceCodeNumber(delivery.getReferenceCode()) : delivery.getReferenceCode();
		
		seh1c.setTipoDeDocumento_351_35E_(SEH1C.SEH1C_2.NOTAS_DE_ENVIO_351
				.getValue());
		seh1c.setNumeroDelDocumento(referenceCode);
		seh1c.setFuncionDelMensaje(SEH1C.SEH1C_4.ORIGINAL___EL_ENVIO_DE_UN_AVISO_DE_EXPEDICION_ORIGINAL_9
				.getValue());
		seh1c.setFecha_horaDelDocumento_137__102_203_(SeresUtils.dateTimeFormat().format(delivery.getDate()));

		Integer salesDetail = !delivery.getDetails().isEmpty()
					? delivery.getDetails().get(0).getSalesDetail() : null;
		Date salesDeliveryDate = null;
		if(salesDetail != null) {
			SalesDetail detail = AON.getSalesDetailStream(domainName, domainId, login, f -> f.getIdProperty().eq(salesDetail))
				.findFirst().orElse(new SalesDetail());
			salesDeliveryDate = detail.getSales() != null
				? detail.getSales().getDeliveryDate() : null;
		}
		seh1c.setFecha_horaEstimadaDeEntrega_17__102_203_(SeresUtils.dateTimeFormat().format(
				salesDeliveryDate != null ? salesDeliveryDate : delivery.getDate()));
	
		seh1c.setCalificadorFecha_Hora1_2_11_64_(null);
		seh1c.setFecha_hora1(null);
		seh1c.setCalificadorFecha_Hora2_2_11_63_(null);
		seh1c.setFecha_hora2(null);
		seh1c.setInformacionAdicional(null);
		seh1c.setNumeroPedido_comprador__ON_(obtainPurchaseReference(delivery));
		seh1c.setFecha_horaNumeroPedido_171__102_203_(null);
		seh1c.setNumeroAlbaran_DQ_(referenceCode);
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
		seh1c.setLugarDeEntrega_Codificado_8_(codes.getDpcode());
		String deliveryAddress = getDeliveryFullAddress(delivery.getAddress().getId());
		deliveryAddress = deliveryAddress != null
				&& deliveryAddress.length() > 70 ? StringUtils.abbreviate(
				deliveryAddress, 70) : deliveryAddress;
		seh1c.setLugarDeEntrega_TextoLibre_8_(deliveryAddress);
		return seh1c;
	}

	private List<SEH1D> createSEH1DList(Delivery delivery, EdiCodes codes) {
		List<SEH1D> list = new ArrayList<>();
		boolean eci = SeresUtils.isECI(delivery.getCustomer().getDocument());
		list.add(createSEH1DRecord(SEH1D.SEH1D_2.EMISOR_DEL_MENSAJE_MS,
				codes.getMscode(), getWorkPlace(delivery.getWorkplace().getId()).getEnterprise(), "", eci));
		list.add(createSEH1DRecord(SEH1D.SEH1D_2.RECEPTOR_DEL_MENSAJE_MR,
				codes.getMrcode(), delivery.getCustomer().getId(), "", eci));
		
		list.add(createSEH1DRecord(SEH1D.SEH1D_2.PROVEEDOR__SU,
				codes.getSucode(), getWorkPlace(delivery.getWorkplace().getId()).getEnterprise(), "", eci));

		list.add(createSEH1DRecord(SEH1D.SEH1D_2.PUNTO_DESDE_DONDE_SE_ENVIAN_LAS_MERCANCIAS_PW,
				codes.getPwcode(), getWorkPlace(delivery.getWorkplace().getId()).getEnterprise(), "", eci));
		list.add(createSEH1DRecord(SEH1D.SEH1D_2.PUNTO_DESTINO_DE_LA_MERCANCIA_DP,
				codes.getDpcode(), delivery.getCustomer().getId(), "", eci));
		// TODO UC list.add(createSEH1DRecord(SEH1D.SEH1D_2.DESTINATARIO_FINAL_UC, null, null));
		list.add(createSEH1DRecord(SEH1D.SEH1D_2.COMPRADOR_BY, codes.getBycode(),
				delivery.getCustomer().getId(), codes.getDepartment(), eci));
		list.add(createSEH1DRecord(SEH1D.SEH1D_2.EXPEDIDOR_SH, codes.getShcode(),
				getWorkPlace(delivery.getWorkplace().getId()).getEnterprise(), "", eci));
		list.add(createSEH1DRecord(SEH1D.SEH1D_2.A_QUIEN_SE_FACTURA_IV,
				codes.getIvcode(), delivery.getCustomer().getId(), "", eci));
		
		return list;
	}
	
	private List<IngenetPackaging> getPackageList(String packageData) {
		String[] b = packageData.split("\\]\\[");
		LinkedList<IngenetPackaging> packageList = new LinkedList<>();
		Integer cont = 0;
		while(cont < b.length) {
			packageList.add(IngenetPackaging.parse(b[cont]));
			cont++;
		}
		return packageList;
	}

	private List<SEH1P> createSEH1PList2(Delivery delivery, String packageData, EdiCodes codes) {
		List<SEH1P> list = new ArrayList<>();
		List<DeliveryDetail> detailList = delivery.getDetails().isEmpty()
				? getDetailList(delivery.getId()).stream()
						.sorted((d1, d2)->Short.compare(d1.getLine(),d2.getLine()))
						.collect(Collectors.toList())
				: delivery.getDetails();
		
 
		List<IngenetPackaging> packageList = getPackageList(packageData);
		List<IngenetPackaging> ssccList = packageList.stream().filter(f -> f.hasSscc()).collect(Collectors.toCollection(LinkedList::new));
		
		SEH1P mainPackage = createSEH1PRecord(1, ssccList.size(), "201", null, null, codes);
		mainPackage.seh1lList = new ArrayList<>();
		list.add(mainPackage);

//		LinkedList<Integer> lineList = new LinkedList<>();
//		Integer auxLine = ssccList.size();
		for (Integer i = 0; i < ssccList.size(); i++) {
			IngenetPackaging sscc = ssccList.get(i);

			IngenetPackaging aux = packageList.stream().filter(f -> f.hasCont() && f.getCont().equals(sscc.getEnv())).findFirst().orElse(null);
			
			DeliveryDetail detail = detailList.stream().filter(f-> f.getLine() == sscc.getLin().shortValue()).findFirst().orElse(new DeliveryDetail());
				
			Short env = aux != null ? aux.getEnv().shortValue() : sscc.getEnv().shortValue();
			DeliveryDetail auxDetail = detailList.stream().filter(f-> f.getLine() == env).findFirst().orElse(new DeliveryDetail());
			Double quantity = auxDetail.getQuantity();
			
			SEH1P packaging  = createSEH1PRecord(i + 2, quantity.intValue(), "CT", sscc.getSscc(), detail, codes);
			packaging.setNumeroDeJerarquiaPadreDeEmbalaje(mainPackage.getNumeroDeJerarquiaDeEmbalaje());

			packaging.seh1lList = new ArrayList<>();
			Integer line = i + 1;
			packaging.seh1lList.add(createSEH1LRecord(line, delivery, detail, quantity, codes));
			list.add(packaging);
		}
		
		return list;
	}

	private List<SEH1P> createSEH1PList2(Delivery delivery, EdiCodes codes) {
		List<SEH1P> list = new ArrayList<>();
		
		SEH1P mainPackage = createSEH1PRecord(1, delivery.getPackaging().size(), "201", null, null, codes);
		mainPackage.seh1lList = new ArrayList<>();
		list.add(mainPackage);
		
		for (Integer i = 0; i < delivery.getPackaging().size(); i++) {
			DeliveryPackaging dp = delivery.getPackaging().get(i);
			if(!dp.getItem().getItemComposition().isEmpty()) {
				ItemComposition ic = dp.getItem().getItemComposition().get(0);
				Item item = ic.getComposition();
				
				Double boxQuantity = ic.getQuantity();
				if(item.getStockUnitTag().getId().equals(item.getPackMeasurementTag().getId())) {
					boxQuantity = ic.getQuantity() / item.getPackMeasurement();
					boxQuantity = boxQuantity / item.getPackUnits().doubleValue();	
				} else if(item.getStockUnitTag().getId().equals(item.getPackUnitsTag().getId())) {
					boxQuantity = ic.getQuantity() / item.getPackUnits().doubleValue();	
				}    
				boxQuantity = AonMathUtils.round(boxQuantity);
				
				SEH1P packaging  = createSEH1PRecord(i + 2, boxQuantity.intValue(), "CT", dp.getItem().getSerialNumber(), null, codes);
				packaging.setNumeroDeJerarquiaPadreDeEmbalaje(mainPackage.getNumeroDeJerarquiaDeEmbalaje());

				packaging.seh1lList = new ArrayList<>();

				Integer line = i + 1;

				packaging.seh1lList.add(createSEH1LRecord(line, delivery, ic, boxQuantity, codes));

				list.add(packaging);
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
	    return new LinkedList<>();
//		List<SEH1B> list = new ArrayList<>();
//		if(SeresUtils.isEroski(delivery.getCustomer().getDocument())) {
//			for (DeliveryDetail detail : delivery.getDetails()) {
//				if(!ProductType.AUXILIARY.equals(detail.getItem().getProduct().getType()))
//						list.add(createSEH1BRecord(detail));
//			}
//		}
//		return list;
	}

	/**
	 * Información de partes
	 */
	private SEH1D createSEH1DRecord(SEH1D.SEH1D_2 type, String ediCode, Integer registryId, String department, boolean eci) {
		Registry registry = getRegistry(registryId);
		SEH1D seh1d = new SEH1D();
		seh1d.setCalificadorDelInterlocutor(type.getValue());
		seh1d.setCodigoInterlocutor(ediCode);
		seh1d.setAgenciaResponsableDeLaListaDeCodigos(SEH1D.SEH1D_4.EAN_9.getValue());
		seh1d.setNombre1(registry.getName().replaceAll("[^\\w\\.,\\s/-]", "?"));
		seh1d.setNombre2(null);
		seh1d.setNombre3(null);
		seh1d.setNombre4(null);
		seh1d.setNombre5(null);
		try {
			RAddress defaultAddress = getDefaultAddress(registryId);
			seh1d.setCalleYNumero1(defaultAddress.getAddress().replaceAll("[^\\w\\.,\\s/-]", "?"));
			seh1d.setCalleYNumero2(defaultAddress.getAddress2().replaceAll("[^\\w\\.,\\s/-]", "?"));
			seh1d.setCalleYNumero3(defaultAddress.getAddress3().replaceAll("[^\\w\\.,\\s/-]", "?"));
			seh1d.setCalleYNumero4(defaultAddress.getNumber());
			seh1d.setPoblacion(defaultAddress.getCity().replaceAll("[^\\w\\.,\\s/-]", "?"));
			seh1d.setCodigoPostal(defaultAddress.getZip());
			GeoZone geozone = getGeozone(defaultAddress.getGeozone());
			if(geozone!=null && geozone.getId()!=null){
				seh1d.setProvincia(geozone.getName());
				seh1d.setCodigoPais(getGeoZoneCountry(geozone.getId()).getCode());
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
		boolean eciDP = eci && SEH1D.SEH1D_2.PUNTO_DESTINO_DE_LA_MERCANCIA_DP.equals(type);
		seh1d.setCalificadorReferencia1("API");
		seh1d.setReferencia1(department);
		seh1d.setFuncionDeContacto(eciDP ? "DL" : null);
		seh1d.setDepartamentoOIdentificacionDelEmpleado(eciDP ? "PERECEDEROS" : null);
		seh1d.setDepartamentoOEmpleado(null);
		seh1d.setCalificadorReferencia2(null);
		seh1d.setReferencia2(null);
		return seh1d;
	}

	/**
	 * Secuencia de embalajes
	 */
	private SEH1P createSEH1PRecord(int lineNumber, Integer quantity, String format, String sscc, DeliveryDetail detail, EdiCodes codes) {
		SEH1P seh1p = new SEH1P();
		seh1p.setNumeroDeJerarquiaDeEmbalaje(String.valueOf(lineNumber));
		seh1p.setNumeroDeJerarquiaPadreDeEmbalaje(null);
		seh1p.setNumeroDePaquetes(quantity);
		seh1p.setInformacionSobreElEmbalaje_Codificado(null);
		seh1p.setTerminosYCondicionesDelEmbalaje_Codificado(null);
		seh1p.setTipoDeEmbalaje_Codificado(format);
		seh1p.setTipoDeEmbalaje_TextoLibre(null);
		seh1p.setResponsabilidadPagoTransporteDeEmbalaje(null);
		seh1p.setPesoNeto1_AAC_(null);			
		seh1p.setPesoNeto2(null);
		seh1p.setCodigoSignificacionDeLaMedidaPesoNeto(null);
		seh1p.setUnidadDeMedidaParaElPesoNeto(null);

		seh1p.setPesoBruto1_AAD_(null);
		seh1p.setPesoBruto2(null);
		seh1p.setCodigoSignificacionDeLaMedidaPesoBruto(null);
		seh1p.setUnidadDeMedidaParaElPesoBruto(null);
		if(detail != null && detail.getItem() != null) {
			double q = 0.0;
			double packUnits = detail.getItem().getPackUnits();
			if(quantity ==null) {
				q = obtainPackageQuantity(detail.getItem(), detail.getQuantity(), codes.getCustomerEdiCode());
			} else {
				q = packUnits * quantity;
			}
			
			seh1p.setPesoNeto1_AAC_(q * detail.getItem().getPackMeasurement());
			seh1p.setPesoBruto1_AAD_(q * detail.getItem().getPackMeasurement());
			if(detail.getItem().getPackMeasurementTag()!=null && detail.getItem().getPackMeasurementTag().getName()!=null) {
				seh1p.setUnidadDeMedidaParaElPesoNeto(StringUtils.substring(detail.getItem().getPackMeasurementTag().getName(), 0, 3).toUpperCase());
				seh1p.setUnidadDeMedidaParaElPesoBruto(StringUtils.substring(detail.getItem().getPackMeasurementTag().getName(), 0, 3).toUpperCase());
			}
		}

		seh1p.setDimensionDeAltura1_HT_(null);
		seh1p.setDimensionDeAltura2(null);
		seh1p.setCodigoSignificacionDeLaMedidaAltura(null);
		seh1p.setUnidadDeMedidaParaLaAltura(null);
		seh1p.setDimensionDeAncho1_WD_(null);
		seh1p.setDimensionDeAncho2(null);
		seh1p.setCodigoSignificacionDeLaMedidaAncho(null);
		seh1p.setUnidadDeMedidaParaElAncho(null);
		seh1p.setDimensionDeLongitud1_LN_(null);
		seh1p.setDimensionDeLongitud2(null);
		seh1p.setCodigoSignificacionDeLaMedidaLongitud(null);
		seh1p.setUnidadDeMedidaParaLaLongitud(null);
		seh1p.setDimensionDeTemperatura1_TC_(null);
		seh1p.setDimensionDeTemperatura2(null);
		seh1p.setCodigoSignificacionDeLaMedidaTemperatura(null);
		seh1p.setUnidadDeMedidaParaLaTemperatura(null);
		seh1p.setCantidadPorEmbalaje(null);
		seh1p.setInstruccionesDeManejo_Codificado(null);
		seh1p.setInstruccionesDeManejo_TextoLibre(null);
		seh1p.setMarcaDeEnvio1(null);
		seh1p.setMarcaDeEnvio2(null);
		seh1p.setMarcaDeEnvio3(null);
		seh1p.setMarcaDeEnvio4(null);
		if(sscc!=null)
			seh1p.setNumeroSerial1ONumeroDeIdentificacionInferior(StringUtils.leftPad(sscc, 18, "0"));
		seh1p.setNumeroSerial1ONumeroDeIdentificacionSuperior(null);
		seh1p.setNumeroSerial2oNumeroDeIdentificacionInferior(null);
		seh1p.setNumeroSerial2ONumeroDeIdentificacionSuperior(null);
		seh1p.setNumeroSerial3ONumeroDeIdentificacionInferior(null);
		seh1p.setNumeroSerial3ONumeroDeIdentificacionSuperior(null);
		return seh1p;
	}

	/**
	 * Línea de artículos
	 */
	private SEH1L createSEH1LRecord(Integer lineNumber, Delivery delivery, DeliveryDetail detail, Double packageQuantity, EdiCodes codes) {
		if(detail.getItem().getExpireDate() == null && detail.getItem().getProduct().isPerishable()) {
			Date expireDate = AonDateUtils.addDays(detail.getItem().getSerialDate(), detail.getItem().getProduct().getDaysToExpire());
			detail.getItem().setExpireDate(expireDate);			
		}
		
		Integer customerId = delivery.getCustomer().getId();
		
		com.esferalia.aon.occam.api.model.registry.RegistryItem ritem = obtainProductCustomerCode(detail.getItem(), customerId);
		String productCustomerBarcode = ritem != null ? ritem.getEdiSalesCode() : "";
		String productCustomerCode = ritem != null ? ritem.getCode() : "";
		
		Item base = AON.getItem(new Domain().setId(domainId).setName(domainName), login, f -> 
			f.getDomainProperty().eq(domainId)
			.and(f.getProductProperty().eq(detail.getItem().getProduct().getId()))
			.and(f.getSerialNumberProperty().isNull()));
		
		SEH1L seh1l = new SEH1L();
		seh1l.setNumeroDeLineaDelArticulo(lineNumber);
		seh1l.setCodigoEANDelArticulo(productCustomerBarcode);
		seh1l.setDescripcionDelArticulo(detail.getItem().getProduct().getName());
		seh1l.setTipoDeIdentificacionDelArticulo_CU_DU_("CU");
		seh1l.setNumeroDeArticuloDelProveedor_SA_(productCustomerCode);
		seh1l.setNumeroVariablePromocional_PV_(null);
		seh1l.setCodigoDUN_14_ADU_(base.getBarcode());
		seh1l.setCodigoACU_ACU_(null);
		seh1l.setNumeroDeLote_NB_(detail.getItem().getSerialNumber());
		seh1l.setNumeroDeArticuloDelComprador_IN_(null);
		// ANTES SOLO ESTABA PARA EROSKI AHORA PARA TODOS. 
		seh1l.setSeh1b(createSEH1BRecord(detail.getItem()));
		
		double quantity = 0.0;
		double packUnits = detail.getItem().getPackUnits();
		if(packageQuantity==null) {
			quantity = obtainPackageQuantity(detail.getItem(), detail.getQuantity(), codes.getCustomerEdiCode());
		} else {
			quantity = packUnits * packageQuantity;
		}

		if(detail.getItem().getPackFormatTag().getName().equals(detail.getItem().getPackUnitsTag().getName())){
			seh1l.setCantidadEnviada_12_(quantity * detail.getItem().getPackUnits() * detail.getItem().getPackMeasurement());
			if(detail.getItem().getPackMeasurementTag()!=null && detail.getItem().getPackMeasurementTag().getName()!=null){
				seh1l.setUnidadDeMedidaCantidadEnviada(
						StringUtils.substring(detail.getItem().getPackMeasurementTag().getName(), 0, 3).toUpperCase());
			}
			seh1l.setUnidadesDeConsumoEnUnidadDeExpedicion_59_(detail.getItem().getPackUnits() * detail.getItem().getPackMeasurement());
		} else {
			seh1l.setCantidadEnviada_12_(quantity);
			seh1l.setUnidadDeMedidaCantidadEnviada(null);
			seh1l.setUnidadesDeConsumoEnUnidadDeExpedicion_59_(packUnits);
		}

//		Date fechaCaducidad = detail.getItem().getExpireDate() != null
//				? detail.getItem().getExpireDate()
//				: detail.getItem().getSerialDate();
//		if(fechaCaducidad != null)
//			seh1l.setFechaDeCaducidad_36__102_203_(SeresUtils.dateFormat().format(fechaCaducidad));
		seh1l.setCalificadorReferencia1(null);
		seh1l.setNumeroReferencia1(null);
		seh1l.setFecha_horaReferencia1_102_203_(null);
		seh1l.setCalificadorReferencia2(null);
		seh1l.setNumeroReferencia2(null);
		seh1l.setFecha_horaReferencia2_102_203_(null);
		seh1l.setCalificadorReferencia3(null);
		seh1l.setNumeroReferencia3(null);
		seh1l.setFecha_horaReferencia3_102_203_(null);
		seh1l.setUnidadesEnAgrupacionSuperior_45E_(null);
		seh1l.setCodigoEANAdicional(null);
		seh1l.setCantidadSinCargo_192_(null);
		seh1l.setCalificadorCantidadAdicional(null);
		seh1l.setCantidadAdicional(null);
		seh1l.setUnidadDeMedidaCantidadAdicional(null);
		seh1l.setNumeroDeSerieDelArticulo_SN_(detail.getItem().getSerialNumber());
		seh1l.setNumeroArticuloFabricante_MF_(null);
		seh1l.setNumeroDeLineaReferencia1(null);
		seh1l.setNumeroDeLineaReferencia2(null);
		seh1l.setNumeroDeLineaReferencia3(null);
		seh1l.setDiferenciaEnCantidadPedida_21_(null);
		seh1l.setCodigoDiscrepancia(null);
		seh1l.setPesoTotalNetoDeLaLinea_AAI_AAF_(quantity * detail.getItem().getPackMeasurement());
		seh1l.setPesoTotalBrutoDeLaLinea_AAI_AAB_(null);
		if(detail.getItem().getPackMeasurementTag()!=null && detail.getItem().getPackMeasurementTag().getName()!=null) {
			seh1l.setUnidadDeMedidaPeso(StringUtils.substring(detail.getItem().getPackMeasurementTag().getName(), 0, 3).toUpperCase());
		}
		seh1l.setDimensionDeTemperatura1_TC_(null);
		seh1l.setDimensionDeTemperatura2(null);
		seh1l.setUnidadDeMedidaParaLaTemperatura(null);
		seh1l.setDenominacionComercial(null);
		seh1l.setDenominacionCientifica(null);
		seh1l.setPaisDeCaptura_produccion_cosecha_cria(null);
		seh1l.setZonaFAODeCaptura(null);
		seh1l.setMetodoDeProduccion(null);
		seh1l.setCodigoDePresentacion(null);
		seh1l.setCodigoFAODeLaEspecie(null);
		seh1l.setFechaOPeriodoDeCaptura(null);
		seh1l.setFechaDeProduccion(null);
		seh1l.setArteDePesca(null);
		seh1l.setInformacionDeCongelado(null);
		seh1l.setFechaDeCongelacion_91E_(null);
		return seh1l;
	}

	/**
	 * Línea de artículos
	 */
	private SEH1L createSEH1LRecord(Integer lineNumber, Delivery delivery, ItemComposition ic, Double packageQuantity, EdiCodes codes) {
		Item item = ic.getComposition();
		if(item.getExpireDate() == null && item.getProduct().isPerishable()) {
			Date expireDate = AonDateUtils.addDays(item.getSerialDate(), item.getProduct().getDaysToExpire());
			item.setExpireDate(expireDate);			
		}
		
		Integer customerId = delivery.getCustomer().getId();

		com.esferalia.aon.occam.api.model.registry.RegistryItem ritem = obtainProductCustomerCode(item, customerId);
		String productCustomerBarcode = ritem != null ? ritem.getEdiSalesCode() : "";
		String productCustomerCode = ritem != null ? ritem.getCode() : "";
		
		Item base = AON.getItem(new Domain().setId(domainId).setName(domainName), login, f -> 
			f.getDomainProperty().eq(domainId)
			.and(f.getProductProperty().eq(item.getProduct().getId()))
			.and(f.getSerialNumberProperty().isNull()));
		
		SEH1L seh1l = new SEH1L();
		seh1l.setNumeroDeLineaDelArticulo(lineNumber);
		seh1l.setCodigoEANDelArticulo(productCustomerBarcode);
		seh1l.setDescripcionDelArticulo(item.getProduct().getName());
		seh1l.setTipoDeIdentificacionDelArticulo_CU_DU_("CU");
		seh1l.setNumeroDeArticuloDelProveedor_SA_(productCustomerCode);
		seh1l.setNumeroVariablePromocional_PV_(null);
		seh1l.setCodigoDUN_14_ADU_(base.getBarcode());
		seh1l.setCodigoACU_ACU_(null);
		seh1l.setNumeroDeLote_NB_(item.getSerialNumber());
		seh1l.setNumeroDeArticuloDelComprador_IN_(null);
		// ANTES SOLO ESTABA PARA EROSKI AHORA PARA TODOS. 
		seh1l.setSeh1b(createSEH1BRecord(item));
		
		double quantity = 0.0;
		double packUnits = item.getPackUnits();
		if(packageQuantity == null) {
			quantity = obtainPackageQuantity(item, ic.getQuantity(), codes.getCustomerEdiCode());
		} else {
			quantity = packUnits * packageQuantity;
		}

		if(item.getPackFormatTag().getName().equals(item.getPackUnitsTag().getName())){
			seh1l.setCantidadEnviada_12_(quantity * item.getPackUnits() * item.getPackMeasurement());
			if(item.getPackMeasurementTag()!=null && item.getPackMeasurementTag().getName()!=null){
				seh1l.setUnidadDeMedidaCantidadEnviada(
						StringUtils.substring(item.getPackMeasurementTag().getName(), 0, 3).toUpperCase());
			}
			seh1l.setUnidadesDeConsumoEnUnidadDeExpedicion_59_(item.getPackUnits() * item.getPackMeasurement());
		} else {
			seh1l.setCantidadEnviada_12_(quantity);
			seh1l.setUnidadDeMedidaCantidadEnviada(null);
			seh1l.setUnidadesDeConsumoEnUnidadDeExpedicion_59_(packUnits);
		}

//		Date fechaCaducidad = detail.getItem().getExpireDate() != null
//				? detail.getItem().getExpireDate()
//				: detail.getItem().getSerialDate();
//		if(fechaCaducidad != null)
//			seh1l.setFechaDeCaducidad_36__102_203_(SeresUtils.dateFormat().format(fechaCaducidad));
		seh1l.setCalificadorReferencia1(null);
		seh1l.setNumeroReferencia1(null);
		seh1l.setFecha_horaReferencia1_102_203_(null);
		seh1l.setCalificadorReferencia2(null);
		seh1l.setNumeroReferencia2(null);
		seh1l.setFecha_horaReferencia2_102_203_(null);
		seh1l.setCalificadorReferencia3(null);
		seh1l.setNumeroReferencia3(null);
		seh1l.setFecha_horaReferencia3_102_203_(null);
		seh1l.setUnidadesEnAgrupacionSuperior_45E_(null);
		seh1l.setCodigoEANAdicional(null);
		seh1l.setCantidadSinCargo_192_(null);
		seh1l.setCalificadorCantidadAdicional(null);
		seh1l.setCantidadAdicional(null);
		seh1l.setUnidadDeMedidaCantidadAdicional(null);
		seh1l.setNumeroDeSerieDelArticulo_SN_(item.getSerialNumber());
		seh1l.setNumeroArticuloFabricante_MF_(null);
		seh1l.setNumeroDeLineaReferencia1(null);
		seh1l.setNumeroDeLineaReferencia2(null);
		seh1l.setNumeroDeLineaReferencia3(null);
		seh1l.setDiferenciaEnCantidadPedida_21_(null);
		seh1l.setCodigoDiscrepancia(null);
		seh1l.setPesoTotalNetoDeLaLinea_AAI_AAF_(quantity * item.getPackMeasurement());
		seh1l.setPesoTotalBrutoDeLaLinea_AAI_AAB_(quantity * item.getPackMeasurement());
		if(item.getPackMeasurementTag()!=null && item.getPackMeasurementTag().getName()!=null) {
			seh1l.setUnidadDeMedidaPeso(StringUtils.substring(item.getPackMeasurementTag().getName(), 0, 3).toUpperCase());
		}
		seh1l.setDimensionDeTemperatura1_TC_(null);
		seh1l.setDimensionDeTemperatura2(null);
		seh1l.setUnidadDeMedidaParaLaTemperatura(null);
		seh1l.setDenominacionComercial(null);
		seh1l.setDenominacionCientifica(null);
		seh1l.setPaisDeCaptura_produccion_cosecha_cria(null);
		seh1l.setZonaFAODeCaptura(null);
		seh1l.setMetodoDeProduccion(null);
		seh1l.setCodigoDePresentacion(null);
		seh1l.setCodigoFAODeLaEspecie(null);
		seh1l.setFechaOPeriodoDeCaptura(null);
		seh1l.setFechaDeProduccion(null);
		seh1l.setArteDePesca(null);
		seh1l.setInformacionDeCongelado(null);
		seh1l.setFechaDeCongelacion_91E_(null);
		return seh1l;
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
	private SEH1B createSEH1BRecord(Item item) {
		if(item.getExpireDate() == null && item.getProduct().isPerishable()) {
			Date expireDate = AonDateUtils.addDays(item.getSerialDate(), item.getProduct().getDaysToExpire());
			item.setExpireDate(expireDate);			
		}
		
		Date fechaCaducidad = item.getExpireDate() != null	
				? item.getExpireDate()
				: item.getSerialDate();
		
		String date = SeresUtils.dateTimeFormat().format(item.getSerialDate());
		SEH1B seh1b = new SEH1B();
		seh1b.setCodigoInstrucciones("36E");
		seh1b.setMarcasDeEnvio(null);
		if(fechaCaducidad != null)
			seh1b.setFechaDeCaducidad_36__102_203_(SeresUtils.dateTimeFormat().format(fechaCaducidad));
		seh1b.setFecha_horaRecepcionDeLaMercancia_50__102_203_(null);
		seh1b.setConsumirAntesDeFecha_361__102_203_(null);
		seh1b.setCalificadorDeCantidad_11_12_(null);
		seh1b.setCantidad(null);
		seh1b.setCalificadorNumeroIdentidad("BX");
		seh1b.setNumeroIdentidad(item.getSerialNumber());
		seh1b.setFechaDeEnvasadoOEmpaquetado_365__102_203_(date);
		seh1b.setFechaProduccion_fabricacion_94__102_203_(null);
		return seh1b;
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
	
	private Integer getBaseItemId(Integer product){
		Domain domain = new Domain().setName(domainName).setId(domainId);
		Item item = AON.getItem(domain, login, f -> f.getProductProperty().eq(product)
				.and(f.getSerialNumberProperty().isNull()));
		return item.getId();
	}

	private Item getItem(Integer itemId){
		try {
			Domain domain = new Domain()
					.setName(domainName)
					.setId(domainId);
			return AON.getItem(domain, login, itemId);
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

	private com.esferalia.aon.occam.api.model.registry.RegistryItem obtainProductCustomerCode(Item item, Integer customerId) {
		try {
			Integer baseItemId = getBaseItemId(item.getProduct().getId());
			com.esferalia.aon.occam.api.model.registry.RegistryItem rItem = 
				AON.getRItemStream(domainName, domainId, login, f -> f
					.getItemProperty().eq(baseItemId)
					.and(f.getRegistryProperty().eq(customerId))
					.and(f.getStatusProperty().eq(com.esferalia.aon.occam.api.model.registry.RegistryItemStatus.ACTIVE.value()))
					.and(f.getTypeProperty().eq(com.esferalia.aon.occam.api.model.registry.RegistryMode.CUSTOMER.value())))
				.sorted((i1, i2) -> i1.getPriority().compareTo(i2.getPriority()))
				.findFirst().orElse(new com.esferalia.aon.occam.api.model.registry.RegistryItem());
			if(rItem!=null)
				return rItem;
		} catch (Throwable e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	
	private Double obtainPackageQuantity(Item item, double quantity, String customerPackingTag) {
		if(customerPackingTag!=null){
			Item oldItem = getItem(item.getId());
			Tag itemPackFormatTag = oldItem.getPackFormatTag();
			Tag itemPackMeasurementTag = oldItem.getPackMeasurementTag();
			Tag itemPackingTag = oldItem.getPackUnitsTag();
			double itemPackMeasurement = oldItem.getPackMeasurement();
			double itemPackUnits = oldItem.getPackUnits();
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
	
	private String referenceCodeNumber(String referenceCode) {
		StringBuilder builder = new StringBuilder();
		for(Integer i = 0; i < referenceCode.length(); i++) {
			if(AonNumberUtils.isNumber("" + referenceCode.charAt(i))) {
				builder.append(referenceCode.charAt(i));
			}
		}
		return builder.toString();
	}
}

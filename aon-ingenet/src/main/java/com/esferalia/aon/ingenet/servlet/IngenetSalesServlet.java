package com.esferalia.aon.ingenet.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.esferalia.aon.ingenet.api.consultaPedidos.ACCIONTYPE;
import com.esferalia.aon.ingenet.api.consultaPedidos.CONSULTAPEDIDOS;
import com.esferalia.aon.ingenet.api.consultaPedidos.ESTADOTYPE;
import com.esferalia.aon.ingenet.api.consultaPedidos.PARAMETROSBUSQUEDATYPE;
import com.esferalia.aon.ingenet.api.consultaPedidos.REFERENCIATYPE;
import com.esferalia.aon.ingenet.api.respuestaPedidos.CIFNIFTYPE;
import com.esferalia.aon.ingenet.api.respuestaPedidos.DATOSCENTROTRABAJOTYPE;
import com.esferalia.aon.ingenet.api.respuestaPedidos.DATOSCLIENTETYPE;
import com.esferalia.aon.ingenet.api.respuestaPedidos.DATOSDIRECCIONTYPE;
import com.esferalia.aon.ingenet.api.respuestaPedidos.DATOSPRODUCTOTYPE;
import com.esferalia.aon.ingenet.api.respuestaPedidos.DATOSREGISTROTYPE;
import com.esferalia.aon.ingenet.api.respuestaPedidos.DETALLEPEDIDOTYPE;
import com.esferalia.aon.ingenet.api.respuestaPedidos.ERRORESTYPE;
import com.esferalia.aon.ingenet.api.respuestaPedidos.LINEADETALLETYPE;
import com.esferalia.aon.ingenet.api.respuestaPedidos.PAISTYPE;
import com.esferalia.aon.ingenet.api.respuestaPedidos.RESPUESTAPEDIDOS;
import com.esferalia.aon.ingenet.api.respuestaPedidos.RESPUESTAPEDIDOTYPE;
import com.esferalia.aon.ingenet.api.util.IngenetXmlValidator;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Properties.SalesProperties;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.impl.jooq.dao.GeoZoneDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO;

@WebServlet(name = "IngenetSalesServlet", urlPatterns = { "/ingenet/sales/*", "/ingenet/sales/dev/*" })
public class IngenetSalesServlet extends AbstractIngenetServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IngenetSalesServlet.class.getName());
	
	private List<String> errorList;
	
	
	protected void processRequest(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws ServletException, IOException {
		
		errorList = new LinkedList<>();
		
		String _xml = httpRequest.getParameter(PARAM_VALUE);
		PARAMETROSBUSQUEDATYPE params = null;
		if(_xml!=null){
			CONSULTAPEDIDOS consulta = null;
			try {
				super.validateConsultaPedidosXmlPattern(new ByteArrayInputStream(_xml.getBytes()));
				consulta = (CONSULTAPEDIDOS) IngenetXmlValidator.extractValue(_xml, CONSULTAPEDIDOS.class);
				if(consulta!=null && consulta.getDATOSCONSULTAPEDIDOS()!=null
						&& consulta.getDATOSCONSULTAPEDIDOS().getPARAMETROSBUSQUEDA()!=null){
					params = consulta.getDATOSCONSULTAPEDIDOS().getPARAMETROSBUSQUEDA();
				}
			} catch (SAXException e) {
				errorList.add("El fichero no ha pasado el proceso de validacion");
				errorList.add(e.getMessage());
			} catch (Exception e) {
				errorList.add(e.getMessage());
			}
		}
		
		if(params==null) {
			params = new PARAMETROSBUSQUEDATYPE();
			params.setFECHA(getDateFormatter().format(new Date()));
		}
		if(params.getACCION()==null) {
			params.setACCION(ACCIONTYPE.RECUPERAR);
		}
		
		Date date = null;
		if(params.getFECHA()!=null){
			try {
				date = getDateFormatter().parse(params.getFECHA());
			} catch (ParseException e) {
				LOGGER.error("Cannot parse date value. Reason: "+ e.getMessage());
			}
		}
		List<String> statusList = new LinkedList<>();
		if(params.getESTADO()!=null){
			if(params.getESTADO().contains(ESTADOTYPE.PENDIENTE)){
				statusList.add("PENDING");
			}
			if(params.getESTADO().contains(ESTADOTYPE.REABIERTO)){
				statusList.add("REOPENED");
			}
			if(params.getESTADO().contains(ESTADOTYPE.RECUPERADO)){
				statusList.add("RETRIEVED");
			}
			if(params.getESTADO().contains(ESTADOTYPE.CERRADO)){
				statusList.add("CLOSED");
			}
		}
		
		AONContext ctx = AONContext.getAONContext(getDomain(), getDomainId(), getUser());
		List<Sales> salesList = null;
		try {
			if(ACCIONTYPE.RECUPERAR==params.getACCION()) {
				List<DataResponse> responseList = getDataResponseList(ctx, date, statusList);
				
				Integer[] salesIds = responseList.stream()
						.mapToInt(DataResponse::getSourceId).boxed().toArray(Integer[]::new);
				salesList = SalesDAO.getSalesStream(
						ctx, f -> f.getIdProperty().in(salesIds)).collect(Collectors.toList());
				flushSales(httpResponse, ctx, salesList);
				
				responseList.stream().forEach(response -> {
					DataResponseDetail detail = new DataResponseDetail();
					detail.setDomain(response.getDomain());
					detail.setDataResponse(response.getId());
					detail.setDataVariable("STATUS");
					detail.setDataValue("RETRIEVED");
					detail = AON.insertDataResponseDetail(ctx.getDomainName(),
							ctx.getDomainId(), ctx.getUser(), detail);
				});
				
				
//				String subject = "Recuperación automática de pedidos";
//				String content = fillResponseMessage(salesList);
//				sendEmail(subject, content, "recuperar", _xml, RECIPIENTS_TO_LOG);
//				saveToDisk("elaboration", "elaboration-request", _xml!=null?_xml:"");
			} else if(ACCIONTYPE.CERRAR==params.getACCION()) {
				if(params.getPEDIDOS()!=null && params.getPEDIDOS().getREFERENCIAS()!=null)
					blockSales(ctx, params.getPEDIDOS().getREFERENCIAS());
				else
					errorList.add("No se han encontrado referencias de pedido para marcarlas en proceso de elaboración");	
			} else if(ACCIONTYPE.CANCELAR==params.getACCION()) {
				reopenSales(ctx, params.getPEDIDOS().getREFERENCIAS());
				// TODO cancelar pedidos recuperados
				errorList.add("Accion 'CANCELAR' en proceso de implementación");
			} else {
				errorList.add("No se ha indicado la accion a realizar");
			}
		} catch (Exception e) {
			LOGGER.error(e.toString());
			errorList.add(e.toString());
		} finally {
			if (ctx != null)
				ctx.close();
		}
		
		if(errorList!=null && errorList.size()>0){
			errorList.add(0, "Se han producido errores al comunicar los pedidos");
			flushErrors(httpResponse, errorList);
		}
		
	}
	

	private void blockSales(AONContext ctx, List<REFERENCIATYPE> referencias) {
		List<Sales> salesList = new LinkedList<>();
		for (REFERENCIATYPE ref : referencias)
			salesList.add(SalesDAO.getSales(ctx, ref.getSERIE(), Integer.parseInt(ref.getNUMERO())));
		
		Integer[] salesIds = salesList.stream()
				.mapToInt(Sales::getId).boxed().toArray(Integer[]::new);
		List<DataResponse> responseList = getDataResponseList(ctx, salesIds);
		
		responseList.stream().forEach(response -> {
			DataResponseDetail detail = new DataResponseDetail();
			detail.setDomain(response.getDomain());
			detail.setDataResponse(response.getId());
			detail.setDataVariable("STATUS");
			detail.setDataValue("IN_PROGRESS");
			detail = AON.insertDataResponseDetail(ctx.getDomainName(),
					ctx.getDomainId(), ctx.getUser(), detail);
		});
	}

	// TODO reopenSales
	private void reopenSales(AONContext ctx, List<REFERENCIATYPE> referencias) {
//		referencias.forEach(ref -> {
//			String series = ref.getSERIE();
//			Integer number = Integer.parseInt(ref.getNUMERO());
//			Sales sales = SalesDAO.getSales(ctx,
//					series, number);
//			if(sales.getStatus()==SalesStatus.IN_PROGRESS.value()){
//				sales.setStatus(SalesStatus.REOPEN.value());
//				sales.setRemarks(StringUtils.mid(ref.getOBSERVACIONES(), 0, 128));
//				salesList.add(sales);
//			}
//		});
	}
	
	private String fillResponseMessage(List<Sales> salesList) {
		StringBuffer bf = new StringBuffer("<h1>Recuperación de pedidos.</h1>");
		bf.append("<ul>");
		salesList.forEach(sales -> {
			bf.append("<li>Pedido ").append(sales.getSeries()).append("/").append(sales.getNumber()).append(" del ")
					.append(sales.getIssueDate()).append("</li>");
		});
		bf.append("</ul>");
		return bf.toString();
	}
	
	private String fillCancellationMessage(List<REFERENCIATYPE> list) {
		StringBuffer bf = new StringBuffer("<h1>Cancelación de pedidos.</h1>");
		bf.append("<ul>");
		list.forEach(pedido -> {
			bf.append("<li>Pedido ").append(pedido.getSERIE()).append("/").append(pedido.getNUMERO()).append("</li>");
			bf.append("<li>").append(pedido.getOBSERVACIONES()).append("</li>");
		});
		bf.append("</ul>");
		return bf.toString();
	}
	
	private String fillErrorMessage(ERRORESTYPE errorestype, List<String> errorList) {
		StringBuffer bf = new StringBuffer("<h1>Envío de pedidos.</h1>");
		bf.append("<ul>");
		errorList.forEach(error -> {
			if (error != null)
				bf.append("<li>").append(error).append("</li>");
		});
		bf.append("</ul>");
		bf.append("<ul>");
		errorestype.getERRORES().forEach(error -> {
			if (error != null)
				bf.append("<li>").append(error).append("</li>");
		});
		bf.append("</ul>");
		return bf.toString();
	}

	private void flushErrors(HttpServletResponse httpResponse,
			List<String> errorList) throws IOException {
		RESPUESTAPEDIDOS respuesta = new RESPUESTAPEDIDOS();
		respuesta.setERRORES(new ERRORESTYPE());
		errorList.forEach(error -> {
			respuesta.getERRORES().getERRORES().add(error);
		});
		String xml = IngenetXmlValidator.convertToXml(respuesta, RESPUESTAPEDIDOS.class);
		
		String subject = "Envío automático de pedidos";
		String content = fillErrorMessage(respuesta.getERRORES(), errorList);
		log(IngenetLogLevel.ERROR, subject, content, "pedidos", xml, RECIPIENTS_TO_FAILURES);
		
		httpResponse.setContentType("application/xml");
		httpResponse.setContentLength(xml.length());
		
		PrintWriter out = httpResponse.getWriter();
		out.print(xml);
		out.flush();
	}
	
	private void flushSales(HttpServletResponse httpResponse,
			AONContext ctx, List<Sales> pendingList) throws IOException {
		RESPUESTAPEDIDOS pedidos = fillPedidosData(ctx, pendingList);
		String xml = IngenetXmlValidator.convertToXml(pedidos, RESPUESTAPEDIDOS.class);
		
		httpResponse.setContentType("application/xml");
		httpResponse.setContentLength(xml.length());
		PrintWriter out = httpResponse.getWriter();
		out.print(xml);
		out.flush();
	}
	
	private RESPUESTAPEDIDOS fillPedidosData(AONContext ctx, List<Sales> pendingList) {
		Integer[] salesIds = pendingList.stream().mapToInt(Sales::getId).boxed().toArray(Integer[]::new);
		Map<Integer, Object> salesStatus = new HashMap<>();
		AON.getDataResponseStream(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), DataResponseSource.INGENET_SALES, 
				f -> f.getSourceIdProperty().in(salesIds))
			.forEach(response -> {
					salesStatus.put(response.getSourceId(), response.getId());
				});
		AON.getLastDataResponseDetailStream(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), 
				f -> f.getDomainProperty().eq(ctx.getDomainId())
					.and(f.getSourceProperty().eq(DataResponseSource.INGENET_SALES.value())
					.and(f.getSourceIdProperty().in(salesIds))))
			.forEach(detail -> {
				for(Integer sales: salesStatus.keySet())
					if(salesStatus.get(sales).equals(detail.getDataResponse()))
						salesStatus.put(sales, detail.getDataValue());	
			});
		
		
		RESPUESTAPEDIDOS pedidos = new RESPUESTAPEDIDOS();
		pendingList.forEach(sales -> {
			try {
				Customer customer = sales.getCustomer();
				RESPUESTAPEDIDOTYPE pedido = new RESPUESTAPEDIDOTYPE();
				pedido.setSERIE(sales.getSeries());
				pedido.setNUMERO(String.valueOf(sales.getNumber()));
				pedido.setFECHAEMISION(getDateFormatter().format(sales.getIssueDate()));
				pedido.setCOMENTARIOS(sales.getComments());
				pedido.setREFERENCIACOMPRA(sales.getPurchaseReference());				
				if(customer!=null && customer.getId()!=null){
					customer = AON.getCustomer(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), customer.getId());
					pedido.setDATOSCLIENTE(obtainDATOSCLIENTE(ctx, customer));
				}
				pedido.setDATOSDIRECCIONENTREGA(obtainDATOSDIRECCIONENTREGA(ctx, sales));
				pedido.setDATOSCENTROTRABAJO(obtainDATOSCENTROTRABAJO(ctx, sales));
				pedido.setDETALLEPEDIDO(obtainDETALLEPEDIDO(ctx, sales));

				System.out.println(salesStatus.get(sales.getId()));
				if(salesStatus.get(sales.getId()).equals("PENDING")) {
					pedido.setESTADO(com.esferalia.aon.ingenet.api.respuestaPedidos.ESTADOTYPE.PENDIENTE);
				} else if(salesStatus.get(sales.getId()).equals("REOPENED")) {	
					pedido.setESTADO(com.esferalia.aon.ingenet.api.respuestaPedidos.ESTADOTYPE.REABIERTO);
				}
				
				pedidos.getDATOSRESPUESTAPEDIDOS().add(pedido);
			} catch (Exception e) {
				String errorMsg = "No se ha podido procesar el pedido " 
						+ sales.getSeries() + "/" + sales.getNumber();
				errorList.add(errorMsg);
				errorList.add(e.getMessage());
			}
		});
		pedidos.setTOTAL(String.valueOf(pendingList.size()));
		return pedidos;
	}

	private DETALLEPEDIDOTYPE obtainDETALLEPEDIDO(AONContext ctx, Sales sales) {
		DETALLEPEDIDOTYPE detalle = new DETALLEPEDIDOTYPE();
		AON.getSalesDetails(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(),
				f -> f.getIdProperty().eq(sales.getId())).forEach(detail -> {
					Item item = AON.getItem(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(),
							detail.getItem().getId());
					Product product = AON.getProduct(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(),
							item.getProductId());
					
					LINEADETALLETYPE linea = new LINEADETALLETYPE();
					linea.setLINEA(String.valueOf(detail.getLine()));
					linea.setCANTIDAD(String.format(Locale.US, "%.3f", detail.getQuantity()));
					if(detail.getItem().getStockUnitTag()!=null){
						linea.setUNIDADMEDIDA(detail.getItem().getStockUnitTag().getName());
					} else {
						String errorMsg = "Producto sin unidad de stock:" 
								+ product.getCode() + "-" + product.getName();
						errorList.add(errorMsg);
					}
		
					DATOSPRODUCTOTYPE datosProducto = new DATOSPRODUCTOTYPE();
					datosProducto.setCODIGO(product.getCode());
					datosProducto.setNOMBRE(product.getName());
					datosProducto.setDESCRIPCION(item.getDescription());
					datosProducto.setDETALLE(item.getDetail());
					datosProducto.setDETALLE2(item.getDetail2());
					datosProducto.setDETALLE3(item.getDetail3());
					datosProducto.setFECHASERIE(null);
					datosProducto.setNUMEROSERIE(null);
					datosProducto.setCODIGOBARRAS(item.getBarcode());
					double price = 0.0;
					if(sales.getCustomer()!=null && sales.getCustomer().getId()!=null){
//						datosProducto.setPRECIO(String.format(Locale.US, "%.3f", detail.getPrice()));
						price = detail.getPrice();
						RegistryItem rItem = obtainCustomerItem(ctx, item, sales.getCustomer());
						datosProducto.setREFERENCIACLIENTE(rItem.getCode());
					} else {
//						datosProducto.setPRECIO(String.format(Locale.US, "%.3f", item.getPrice()));
						price = item.getPrice();
					}
					
					linea.setDATOSPRODUCTO(datosProducto);
					linea.setPRECIO(String.format(Locale.US, "%.3f", price));
					detalle.getLINEADETALLE().add(linea);
				});
		
		return detalle;
	}

	private RegistryItem obtainCustomerItem(AONContext ctx, Item item, Customer customer) {
		RegistryItem rItem = null;
		if(item!=null && item.getId()!=null && customer!=null && customer.getId()!=null){
			 rItem = RegistryDAO
					.getRItemStream(ctx,
							f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getItemProperty().eq(item.getId()))
									.and(f.getRegistryProperty().eq(customer.getId())))
//					.sorted((o1, o2) -> o1.getPriority().compareTo(o2.getPriority()))
					.findFirst()
					.orElse(new RegistryItem());
		}
		return rItem;
	}
	
	
	private DATOSCENTROTRABAJOTYPE obtainDATOSCENTROTRABAJO(AONContext ctx,
			Sales sales) {
		Workplace workplace = WorkplaceDAO.getWorkplace(ctx, p -> p.getIdProperty().eq(sales.getWorkplace()));
		if(workplace!=null){
			RAddress address = RegistryDAO
					.getRAddressStream(ctx,
							p -> p.getIdProperty().eq(workplace.getAddress()))
					.findFirst().orElse(new RAddress());
			if(address!=null){
				DATOSCENTROTRABAJOTYPE datos = new DATOSCENTROTRABAJOTYPE();
				datos.setDESCRIPCION(workplace.getDescription());
				datos.setDATOSDIRECCION(new DATOSDIRECCIONTYPE());
				datos.getDATOSDIRECCION().setDIRECCION(address.getAddress());
				datos.getDATOSDIRECCION().setDIRECCION2(address.getAddress2());
				datos.getDATOSDIRECCION().setDIRECCION3(address.getAddress3());
				datos.getDATOSDIRECCION().setCIUDAD(address.getCity());
				datos.getDATOSDIRECCION().setCODIGOPOSTAL(address.getZip());
				datos.getDATOSDIRECCION().setPROVINCIA(null);
				return datos;
			} else {
				String errorMsg = "Centro de trabajo sin direccion definida:" 
						+ workplace.getDescription();
				errorList.add(errorMsg);
			}
		} else {
			String errorMsg = "Pedido sin centro de trabajo definido:" 
					+ sales.getSeries() + "/" + sales.getNumber();
			errorList.add(errorMsg);
		}
		return null;
	}

	private DATOSDIRECCIONTYPE obtainDATOSDIRECCIONENTREGA(AONContext ctx,
			Sales sales) {
		DATOSDIRECCIONTYPE datos = new DATOSDIRECCIONTYPE();
		if (sales.getShippingAlternativeAddress() != null
				&& sales.getShippingAlternativeAddress2() != null
				&& sales.getShippingAlternativeCity() != null
				&& sales.getShippingAlternativeZip() != null) {
			datos.setDIRECCION(sales.getShippingAlternativeAddress());
			datos.setDIRECCION2(sales.getShippingAlternativeAddress2());
			datos.setDIRECCION3(null);
			datos.setCIUDAD(sales.getShippingAlternativeCity());
			datos.setCODIGOPOSTAL(sales.getShippingAlternativeZip());
			datos.setPROVINCIA(null);
		} else {
			RAddress address = obtainAddress(ctx, sales.getShippingAddress());
			if(address!=null){
				GeoZone gz = obtainGeozone(ctx, address.getGeozone());
				datos.setDIRECCION(address.getAddress());
				datos.setDIRECCION2(address.getAddress2());
				datos.setDIRECCION3(address.getAddress3());
				datos.setCIUDAD(address.getCity());
				datos.setCODIGOPOSTAL(address.getZip());
				datos.setPROVINCIA(gz!=null?gz.getName():null);
			} else {
				String errorMsg = "Pedido sin direccion definida:" 
						+ sales.getSeries() + "/" + sales.getNumber();
				errorList.add(errorMsg);
			}
		}
		return datos;
	}

	private DATOSCLIENTETYPE obtainDATOSCLIENTE(AONContext ctx, Customer customer) {
		Registry registry = obtainRegistry(ctx, customer.getId());
		
		DATOSCLIENTETYPE datos = new DATOSCLIENTETYPE();
		datos.setCODIGO(String.valueOf(customer.getId()));
		datos.setALBARANVALORADO(customer.isDeliveryValuated()?"S":"N");
		datos.setDATOSREGISTRO(new DATOSREGISTROTYPE());
		datos.getDATOSREGISTRO().setDATOSDOCUMENTO(new CIFNIFTYPE());
		datos.getDATOSREGISTRO().getDATOSDOCUMENTO().setPAISDOCUMENTO(new PAISTYPE());
		if(registry!=null){
			Country documentCountry = registry.getDocumentCountry();
			
			if(documentCountry!=null){
				datos.getDATOSREGISTRO().getDATOSDOCUMENTO().getPAISDOCUMENTO().setCODIGO(String.valueOf(documentCountry.getIsoCode()));
				datos.getDATOSREGISTRO().getDATOSDOCUMENTO().getPAISDOCUMENTO().setDESCRIPCION(documentCountry.getName());
				datos.getDATOSREGISTRO().getDATOSDOCUMENTO().setTIPODOCUMENTO(documentCountry.name());
			} else {
				String errorMsg = "Pais documento invalido para: (" 
						+ registry.getDocument() + ")" + registry.getName();
				errorList.add(errorMsg);
			}
			datos.getDATOSREGISTRO().getDATOSDOCUMENTO().setDOCUMENTO(registry.getDocument());
			datos.getDATOSREGISTRO().setNOMBRE(registry.getName());
			datos.getDATOSREGISTRO().setALIAS(registry.getAlias());
			Country nationality = registry.getNationality();
			if(nationality!=null){
				datos.getDATOSREGISTRO().setNACIONALIDAD(new PAISTYPE());
				datos.getDATOSREGISTRO().getNACIONALIDAD().setCODIGO(String.valueOf(nationality.getIsoCode()));
				datos.getDATOSREGISTRO().getNACIONALIDAD().setDESCRIPCION(nationality.getName());
			} else {
				String errorMsg = "Nacionalidad invalida para: (" 
						+ registry.getDocument() + ")" + registry.getName();
				errorList.add(errorMsg);
			}
		}
		datos.getDATOSREGISTRO().setTELEFONOFIJO(obtainCustomerPhone(ctx, customer));
		datos.getDATOSREGISTRO().setTELEFONOMOVIL(obtainCustomerCellular(ctx, customer));
		return datos;
	}
	
	
	private Registry obtainRegistry(AONContext ctx, Integer id){
		if(id!=null){
			return RegistryDAO.getRegistry(ctx, f -> f.getIdProperty().eq(id));
		}
		return null;
	}
	
	private Customer obtainCustomer(AONContext ctx, Integer salesId){
		if(salesId!=null){
			Sales sales = obtainSales(ctx, salesId);
			return AON.getCustomer(ctx.getDomainName(), ctx.getDomainId(),
					ctx.getUser(), sales.getCustomer().getId());
		}
		return null;
	}
	
	private String obtainCustomerPhone(AONContext ctx, Customer customer) {
		if (customer != null && customer.getId() != null) {
			return RegistryDAO
					.getRMediaStream(
							ctx,
							filter -> filter
									.getRegistryProperty()
									.eq(customer.getId())
									.and(filter.getMediaProperty().eq(
											MediaType.FIXED_PHONE.value())))
					.findFirst().orElse(new RegistryMedia()).getValue();
		}
		return null;
	}

	private String obtainCustomerCellular(AONContext ctx, Customer customer) {
		if (customer != null && customer.getId() != null) {
			return RegistryDAO
					.getRMediaStream(
							ctx,
							filter -> filter
									.getRegistryProperty()
									.eq(customer.getId())
									.and(filter.getMediaProperty().eq(
											MediaType.CELLULAR.value())))
					.findFirst().orElse(new RegistryMedia()).getValue();
		}
		return null;
	}

	private Sales obtainSales(AONContext ctx, Integer salesId){
		if(salesId!=null){
			return SalesDAO.getSales(ctx, salesId);
		}
		return null;
	}
	
	private RAddress obtainAddress(AONContext ctx, Integer id) {
		if (id != null) {
			return RegistryDAO
					.getRAddressStream(ctx, f -> f.getIdProperty().eq(id))
					.findFirst().orElse(new RAddress());
		}
		return null;
	}
	
	private GeoZone obtainGeozone(AONContext ctx, Integer id) {
		if (id != null) {
			return GeoZoneDAO.get(ctx, id);
		}
		return null;
	}

	private List<DataResponse> getDataResponseList(AONContext ctx, Date date,
			List<String> statusList) {
		String[] statuses = { null, null, null, null, null };
		if (statusList != null && statusList.size() > 0) {
			for (int i = 0; i < statusList.size(); i++)
				statuses[i] = statusList.get(i);
		} else {
			statuses[0] = "PENDIENTE";
			statuses[1] = "REABIERTO";
		}
		
		Integer[] responseIds = AON.getLastDataResponseDetailStream(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), 
				f -> {
					if (date != null) {
						java.sql.Date start = null;
						java.sql.Date end = null;
						start = new java.sql.Date(DateUtils
								.setSeconds(
										DateUtils.setMinutes(
												DateUtils.setHours(date, 0), 0), 0)
												.getTime());
						end = new java.sql.Date(DateUtils
								.setSeconds(
										DateUtils.setMinutes(
												DateUtils.setHours(date, 23), 59),
												59).getTime());
						return f.getDomainProperty().eq(ctx.getDomainId())
								.and(f.getSourceProperty().eq(DataResponseSource.INGENET_SALES.value()))
								.and(f.getIssueDateProperty().between(start, end));
					} else {
						return f.getDomainProperty().eq(ctx.getDomainId())
								.and(f.getSourceProperty().eq(DataResponseSource.INGENET_SALES.value()))
								.and(f.getIssueDateProperty().isNotNull());
					}
				})
				.filter(detail-> ArrayUtils.contains(statuses, detail.getDataValue())
						)
				.mapToInt(DataResponseDetail::getDataResponse).boxed().toArray(Integer[]::new);
		
		return AON.getDataResponseStream(ctx.getDomainName(), ctx.getDomainId(),
				ctx.getUser(), DataResponseSource.INGENET_SALES, f -> f.getIdProperty().in(responseIds))
				.collect(Collectors.toList());
		
	}
	
	private List<DataResponse> getDataResponseList(AONContext ctx, Integer[] salesIds) {
		Integer[] responseIds = AON.getLastDataResponseDetailStream(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), 
				f -> {
					return f.getDomainProperty().eq(ctx.getDomainId())
							.and(f.getSourceProperty().eq(DataResponseSource.INGENET_SALES.value()))
							.and(f.getSourceIdProperty().in(salesIds));
				})
				.mapToInt(DataResponseDetail::getDataResponse).boxed().toArray(Integer[]::new);
		return AON.getDataResponseStream(ctx.getDomainName(), ctx.getDomainId(),
				ctx.getUser(), DataResponseSource.INGENET_SALES, f -> f.getIdProperty().in(responseIds))
				.collect(Collectors.toList());
	}
	

}

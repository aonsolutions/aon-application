package com.esferalia.aon.ingenet.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import com.esferalia.aon.ingenet.api.consultaElaboraciones.ACCIONTYPE;
import com.esferalia.aon.ingenet.api.consultaElaboraciones.CONSULTAELABORACIONES;
import com.esferalia.aon.ingenet.api.consultaElaboraciones.ESTADOTYPE;
import com.esferalia.aon.ingenet.api.consultaElaboraciones.PARAMETROSBUSQUEDATYPE;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.CIFNIFTYPE;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.DATOSCENTROTRABAJOTYPE;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.DATOSCLIENTETYPE;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.DATOSDIRECCIONTYPE;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.DATOSPEDIDOORIGENTYPE;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.DATOSPRODUCTOTYPE;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.DATOSREGISTROTYPE;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.ERRORESTYPE;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.PAISTYPE;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.RESPUESTAELABORACIONES;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.RESPUESTAELABORACIONTYPE;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.ElaborationSource;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.impl.jooq.dao.ElaborationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.GeoZoneDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO;

public class IngenetElaborationServlet extends AbstractIngenetServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	private final SimpleDateFormat timeFormatter = new SimpleDateFormat("hhmm");
	
	private final static String PARAM_VALUE = "value";
	
	
	protected void processRequest(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws ServletException, IOException {
		
		PARAMETROSBUSQUEDATYPE params = null;
		
		String _xml = httpRequest.getParameter(PARAM_VALUE);
		if(_xml!=null){
			CONSULTAELABORACIONES consulta = extractValue(_xml);
			if(consulta!=null && consulta.getDATOSCONSULTAELABORACIONES()!=null
					&& consulta.getDATOSCONSULTAELABORACIONES().getPARAMETROSBUSQUEDA()!=null){
				params = consulta.getDATOSCONSULTAELABORACIONES().getPARAMETROSBUSQUEDA();
			}
		}
		
		if(params==null) {
			params = new PARAMETROSBUSQUEDATYPE();
		}
		if(params.getACCION()==null) {
			params.setACCION(ACCIONTYPE.RECUPERAR);
		}
		
		AONContext ctx = AONContext.getAONContext(getDomain(), getDomainId(), getUser());
		Date date = null;
		if(params.getFECHA()!=null){
			try {
				date = getDateFormatter().parse(params.getFECHA());
			} catch (ParseException e) {
				System.err.println("Cannot parse date value. Reason: "+ e.getMessage());
			}
		}
		List<ElaborationStatus> statusList = new LinkedList<>();
		if(params.getESTADO()!=null){
			if(params.getESTADO().contains(ESTADOTYPE.PENDIENTE)){
				statusList.add(ElaborationStatus.PENDING);
			}
			if(params.getESTADO().contains(ESTADOTYPE.PROCESANDO)){
				statusList.add(ElaborationStatus.IN_PROGRESS);
			}
			if(params.getESTADO().contains(ESTADOTYPE.FINALIZADO)){
				statusList.add(ElaborationStatus.CLOSED);
			}
		}
		
		List<Elaboration> elaborationList = getElaborationList(ctx, date, statusList);
		
		List<String> errorList = null;
		if(ACCIONTYPE.RECUPERAR==params.getACCION()){
			flushElaborations(httpResponse, ctx, elaborationList);
			elaborationList.forEach(elaboration -> {
				elaboration.setStatus(ElaborationStatus.IN_PROGRESS.value());
				ElaborationDAO.updateElaboration(ctx, elaboration);
			});
		} else if(ACCIONTYPE.CANCELAR==params.getACCION()){
			if(params.getELABORACIONES()!=null 
					&& params.getELABORACIONES().getREFERENCIAS()!=null 
					&& params.getELABORACIONES().getREFERENCIAS().size()>0){
				params.getELABORACIONES().getREFERENCIAS().forEach(ref -> {
					String series = ref.getSERIE();
					Integer number = Integer.parseInt(ref.getNUMERO());
					Elaboration elaboration = ElaborationDAO
							.getElaboration(ctx, series, number);
					elaboration.setStatus(ElaborationStatus.PENDING.value());
					ElaborationDAO.updateElaboration(ctx, elaboration);
				});
				flushElaborations(httpResponse, ctx, elaborationList);
			}
		} else {
			errorList = new LinkedList<>();
			errorList.add("No se ha indicado la accion a realizar");
			flushErrors(httpResponse, ctx, errorList);
		}
		
	}

	private void flushErrors(HttpServletResponse httpResponse,
			AONContext ctx, List<String> errorList) throws IOException {
		RESPUESTAELABORACIONES respuesta = new RESPUESTAELABORACIONES();
		respuesta.setERRORES(new ERRORESTYPE());
		errorList.forEach(error -> {
			respuesta.getERRORES().getERRORES().add(error);
		});
		String xml = convertToXml(respuesta, RESPUESTAELABORACIONES.class);
		httpResponse.setContentType("application/xml");
		httpResponse.setContentLength(xml.length());
		
		PrintWriter out = httpResponse.getWriter();
		out.print(xml);
		out.flush();
	}
	
	private void flushElaborations(HttpServletResponse httpResponse,
			AONContext ctx, List<Elaboration> pendingList) throws IOException {
		RESPUESTAELABORACIONES elaboraciones = fillElaborationData(ctx, pendingList);
		String xml = convertToXml(elaboraciones, RESPUESTAELABORACIONES.class);
		httpResponse.setContentType("application/xml");
		httpResponse.setContentLength(xml.length());
		PrintWriter out = httpResponse.getWriter();
		out.print(xml);
		out.flush();
	}
	
	private RESPUESTAELABORACIONES fillElaborationData(AONContext ctx, List<Elaboration> pendingList) {
		RESPUESTAELABORACIONES elaboraciones = new RESPUESTAELABORACIONES();
		pendingList.forEach(elaboration -> {
			Item item = ProductDAO.getItem(ctx, elaboration.getItem().getId());
			Product product = ProductDAO.getProduct(ctx, item.getProduct().getId());
			SalesDetail salesDetail = obtainSalesDetail(ctx, elaboration);
			Sales sales = obtainSales(ctx, salesDetail.getSales());
			Customer customer = obtainCustomer(ctx, salesDetail.getSales());
			RESPUESTAELABORACIONTYPE elaboracion = new RESPUESTAELABORACIONTYPE();
			elaboracion.setSERIE(elaboration.getSeries());
			elaboracion.setNUMERO(String.valueOf(elaboration.getNumber()));
			elaboracion.setFECHAEMISION(dateFormatter.format(elaboration.getDate()));
			elaboracion.setDATOSPEDIDOORIGEN(new DATOSPEDIDOORIGENTYPE());
			elaboracion.getDATOSPEDIDOORIGEN().setSERIE(sales.getSeries());
			elaboracion.getDATOSPEDIDOORIGEN().setNUMERO(String.valueOf(sales.getNumber()));
			elaboracion.getDATOSPEDIDOORIGEN().setREFERENCIACOMPRA(sales.getPurchaseReference());
			elaboracion.getDATOSPEDIDOORIGEN().setDATOSCLIENTE(obtainDATOSCLIENTE(ctx, salesDetail, customer));
			elaboracion.getDATOSPEDIDOORIGEN().setDATOSDIRECCIONENTREGA(obtainDATOSDIRECCIONENTREGA(ctx, sales));
			elaboracion.setCOMENTARIOS(elaboration.getComments());
			elaboracion.setDATOSCENTROTRABAJO(obtainDATOSCENTROTRABAJO(ctx, sales));
			elaboracion.setDATOSPRODUCTO(new DATOSPRODUCTOTYPE());
			elaboracion.getDATOSPRODUCTO().setCODIGO(product.getCode());
			elaboracion.getDATOSPRODUCTO().setNOMBRE(product.getName());
			elaboracion.getDATOSPRODUCTO().setDESCRIPCION(item.getDescription());
			elaboracion.getDATOSPRODUCTO().setDETALLE(item.getDetail());
			elaboracion.getDATOSPRODUCTO().setDETALLE2(item.getDetail2());
			elaboracion.getDATOSPRODUCTO().setDETALLE3(item.getDetail3());
			elaboracion.getDATOSPRODUCTO().setFECHASERIE(null);
			elaboracion.getDATOSPRODUCTO().setNUMEROSERIE(null);
			elaboracion.getDATOSPRODUCTO().setCODIGOBARRAS(item.getBarcode());
			elaboracion.getDATOSPRODUCTO().setPRECIO(String.format(Locale.US, "%.3f%n", item.getPrice()));
			elaboracion.getDATOSPRODUCTO().setREFERENCIACLIENTE(obtainCustomerProductCode(ctx, elaboration.getItem(), customer));
			elaboracion.setCANTIDAD(String.format(Locale.US, "%.3f%n", elaboration.getQuantity()));
			elaboracion.setUNIDADMEDIDA(elaboration.getItem().getStockUnitTag().getName());
			ElaborationStatus elaborationStatus = ElaborationStatus.values()[elaboration.getStatus()];
			if(elaborationStatus==ElaborationStatus.PENDING){
				elaboracion.setESTADO(com.esferalia.aon.ingenet.api.respuestaElaboraciones.ESTADOTYPE.PENDIENTE);
			} else if(elaborationStatus==ElaborationStatus.IN_PROGRESS){
				elaboracion.setESTADO(com.esferalia.aon.ingenet.api.respuestaElaboraciones.ESTADOTYPE.PROCESANDO);
			}
			elaboracion.setFECHACONSULTA(dateFormatter.format(elaboration.getModificationDate()));
			elaboracion.setHORACONSULTA(timeFormatter.format(elaboration.getModificationDate()));
			elaboraciones.getDATOSRESPUESTAELABORACIONES().add(elaboracion);
		});
		elaboraciones.setTOTAL(String.valueOf(pendingList.size()));
		return elaboraciones;
	}

	private String obtainCustomerProductCode(AONContext ctx, Item item, Customer customer) {
		if(item!=null && item.getId()!=null
			&& customer!=null && customer.getId()!=null){
			return ElaborationDAO.getCustomerItemCode(ctx, item.getId(), customer.getId());
		}
		return null;
	}
	
	private DATOSCENTROTRABAJOTYPE obtainDATOSCENTROTRABAJO(AONContext ctx,
			Sales sales) {
		Workplace workplace = WorkplaceDAO.getWorkplace(ctx, p -> p.getIdProperty().eq(sales.getWorkplace()));
		RAddress address = RegistryDAO
				.getRAddressStream(ctx,
						p -> p.getIdProperty().eq(workplace.getAddress()))
				.findFirst().orElse(new RAddress());

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
			GeoZone gz = obtainGeozone(ctx, address.getGeozone());
			datos.setDIRECCION(address.getAddress());
			datos.setDIRECCION2(address.getAddress2());
			datos.setDIRECCION3(address.getAddress3());
			datos.setCIUDAD(address.getCity());
			datos.setCODIGOPOSTAL(address.getZip());
			datos.setPROVINCIA(gz!=null?gz.getName():null);
		}
		return datos;
	}

	private DATOSCLIENTETYPE obtainDATOSCLIENTE(AONContext ctx, SalesDetail salesDetail, Customer customer) {
		Registry registry = obtainRegistry(ctx, customer.getId());
		
		DATOSCLIENTETYPE datos = new DATOSCLIENTETYPE();
		datos.setCODIGO(String.valueOf(customer.getId()));
		datos.setALBARANVALORADO(new Byte("1").equals(customer.getDeliveryValuated())?"S":"N");
		datos.setDATOSREGISTRO(new DATOSREGISTROTYPE());
		datos.getDATOSREGISTRO().setDATOSDOCUMENTO(new CIFNIFTYPE());
		datos.getDATOSREGISTRO().getDATOSDOCUMENTO().setPAISDOCUMENTO(new PAISTYPE());
		datos.getDATOSREGISTRO().getDATOSDOCUMENTO().getPAISDOCUMENTO().setCODIGO(String.valueOf(registry.getDocumentCountry().getIsoCode()));
		datos.getDATOSREGISTRO().getDATOSDOCUMENTO().getPAISDOCUMENTO().setDESCRIPCION(registry.getDocumentCountry().getName());
		datos.getDATOSREGISTRO().getDATOSDOCUMENTO().setTIPODOCUMENTO(registry.getDocumentType().name());
		datos.getDATOSREGISTRO().getDATOSDOCUMENTO().setDOCUMENTO(registry.getDocument());
		datos.getDATOSREGISTRO().setNOMBRE(registry.getName());
		datos.getDATOSREGISTRO().setALIAS(registry.getAlias());
		datos.getDATOSREGISTRO().setNACIONALIDAD(new PAISTYPE());
		datos.getDATOSREGISTRO().getNACIONALIDAD().setCODIGO(String.valueOf(registry.getNationality().getIsoCode()));
		datos.getDATOSREGISTRO().getNACIONALIDAD().setDESCRIPCION(registry.getNationality().getName());
		datos.getDATOSREGISTRO().setTELEFONOFIJO(obtainCustomerPhone(ctx, customer));
		datos.getDATOSREGISTRO().setTELEFONOMOVIL(obtainCustomerCellular(ctx, customer));
		return datos;
	}
	
	
	private Registry obtainRegistry(AONContext ctx, Integer id){
		if(id!=null){
			return RegistryDAO.getRegistry(ctx, id);
		}
		return null;
	}
	
	private Customer obtainCustomer(AONContext ctx, Integer salesId){
		if(salesId!=null){
			Sales sales = obtainSales(ctx, salesId);
			return AON.getCustomer(ctx.getDomainName(), ctx.getDomainId(),
					ctx.getUser(), sales.getCustomer());
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

	private SalesDetail obtainSalesDetail(AONContext ctx, Elaboration elaboration){
		if(elaboration.getSource()==ElaborationSource.SALES.value()
				&& elaboration.getSourceId()!=null){
			return SalesDAO.getSalesDetail(ctx, elaboration.getSourceId());
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

	private List<Elaboration> getElaborationList(AONContext ctx, Date date,
			List<ElaborationStatus> statusList) {
		List<Elaboration> elaborationList = ElaborationDAO.getElaborationList(
				ctx,
				p -> {
					Byte[] statuses = {null, null};
					if(statusList!=null && statusList.size()>0){
						for(int i=0; i<statusList.size(); i++){
							statuses[i] = statusList.get(i).value();
						}
					} else {
						statuses[0] = ElaborationStatus.PENDING.value();
						statuses[1] = ElaborationStatus.IN_PROGRESS.value();
					}
				return p.getStatusProperty()
						.in(statuses)
						.and(date != null ? p.getDateProperty().eq(
								new java.sql.Timestamp(date.getTime())) : p
								.getDateProperty().isNotNull())
								;
				});
		return elaborationList;
	}
	
	
	/*
	 * JAXB
	 */
	private CONSULTAELABORACIONES extractValue(String xml) throws IOException {
    	InputStream inputStream = null;
    	try {
			byte[] bytes = xml.getBytes("UTF-8");
			inputStream = new ByteArrayInputStream(bytes);
			String contextPath = CONSULTAELABORACIONES.class.getPackage().getName();
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setEventHandler(new ConsultaValidationEventHandler());
			CONSULTAELABORACIONES consulta = (CONSULTAELABORACIONES) unmarshaller.unmarshal(inputStream);
			return consulta;
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} finally {
			if(inputStream!=null){
				inputStream.close();
			}
		}
    }
	
	private String convertToXml(Object source, Class<?>... type) {
        String result;
        StringWriter sw = new StringWriter();
        try {
            JAXBContext context = JAXBContext.newInstance(type);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(source, sw);
            result = sw.toString();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public class ConsultaValidationEventHandler implements ValidationEventHandler {
		public boolean handleEvent(ValidationEvent ve) {
			if (ve.getSeverity() == ValidationEvent.FATAL_ERROR || ve.getSeverity() == ValidationEvent.ERROR) {
				ValidationEventLocator locator = ve.getLocator();
				// Print message from valdation event
				System.out.println("Invalid value: " + locator.getURL());
				System.out.println("Error: " + ve.getMessage());
				// Output line and column number
				System.out.println("Error at column "
						+ locator.getColumnNumber() + ", line "
						+ locator.getLineNumber());
			}
			return true;
		}
	}
	

    
	public static void main(String[] args) throws Exception {
		String path = "http://";
		path += "udapa.esferalia.net";
		path += ":8080";
		path += "/aon-aio";
		path += "/ingenet/elaboration";
		
		String user = "ingenet";
		String passwd = "1ng3n3t";
		
		String FILENAME = "C:\\TEMP\\consultaElaboraciones_example.xml";
		String xml = "";
		try (
			BufferedReader xml_br = new BufferedReader(new FileReader(FILENAME))) {
			String sCurrentLine;
			while ((sCurrentLine = xml_br.readLine()) != null) {
				xml += sCurrentLine;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<CONSULTA_ELABORACIONES>"
				+ "<DATOS_CONSULTA_ELABORACIONES>"
				+ "<PARAMETROS_BUSQUEDA>"
				+ "<ACCION>CONSULTAR</ACCION>"
				+ "<!--FECHA>20170201</FECHA-->"
				+ "<ESTADO>PENDIENTE</ESTADO>"
				+ "<ESTADO>PROCESANDO</ESTADO>"
				+ "<!--ELABORACIONES></ELABORACIONES-->"
				+ "</PARAMETROS_BUSQUEDA>"
				+ "</DATOS_CONSULTA_ELABORACIONES>"
				+ "</CONSULTA_ELABORACIONES>";
        
        StringBuilder postData = new StringBuilder();
        postData.append('&');
        postData.append(URLEncoder.encode(PARAM_USERNAME, "UTF-8"));
        postData.append('=');
        postData.append(URLEncoder.encode(user, "UTF-8"));
        postData.append('&');
        postData.append(URLEncoder.encode(PARAM_PASSWORD, "UTF-8"));
        postData.append('=');
        postData.append(URLEncoder.encode(passwd, "UTF-8"));
        postData.append('&');
        postData.append(URLEncoder.encode(PARAM_VALUE, "UTF-8"));
        postData.append('=');
        postData.append(URLEncoder.encode(xml, "UTF-8"));
        
        byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8.name());

        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.connect();
        conn.getOutputStream().write(postDataBytes);

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8.name()));
        StringBuffer sb = new StringBuffer();
        for(String in; (in = br.readLine()) != null;) {
            sb.append(in + "\n");
        }
        System.out.println(sb);
        br.close();
	}

}

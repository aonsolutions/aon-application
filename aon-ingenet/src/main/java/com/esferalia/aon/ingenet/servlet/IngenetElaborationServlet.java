package com.esferalia.aon.ingenet.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.esferalia.aon.ingenet.api.elaboraciones.CIFNIFTYPE;
import com.esferalia.aon.ingenet.api.elaboraciones.DATOSCENTROTRABAJOTYPE;
import com.esferalia.aon.ingenet.api.elaboraciones.DATOSCLIENTETYPE;
import com.esferalia.aon.ingenet.api.elaboraciones.DATOSDIRECCIONTYPE;
import com.esferalia.aon.ingenet.api.elaboraciones.DATOSPEDIDOORIGENTYPE;
import com.esferalia.aon.ingenet.api.elaboraciones.DATOSPRODUCTOTYPE;
import com.esferalia.aon.ingenet.api.elaboraciones.DATOSREGISTROTYPE;
import com.esferalia.aon.ingenet.api.elaboraciones.ELABORACIONES;
import com.esferalia.aon.ingenet.api.elaboraciones.ELABORACIONTYPE;
import com.esferalia.aon.ingenet.api.elaboraciones.PAISTYPE;
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

	private static final String PARAM_DATE = "date";
	private static final String PARAM_ACTION = "action";
	private static final String PARAM_IDS = "ids";
	
	private static final String PARAM_ACTION_VIEW = "CONSULTAR";
	private static final String PARAM_ACTION_PROCCESS = "PROCESAR";
	
	
	protected void processRequest(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws ServletException, IOException {
		
		String _date = httpRequest.getParameter(PARAM_DATE);
		String _action = httpRequest.getParameter(PARAM_ACTION);
		String _ids = httpRequest.getParameter(PARAM_IDS);
		
		Date date = null;
		if(_date!=null){
			try {
				date = getDateFormatter().parse(_date);
			} catch (ParseException e) {
				System.err.println("Cannot parse date value. Reason: "+ e.getMessage());
			}
		}
		
		AONContext ctx = AONContext.getAONContext(getDomain(), getDomainId(), getUser());
		
		List<Elaboration> pendingList = getPendingList(ctx, date);
		
		if(PARAM_ACTION_VIEW.equals(_action)){
			flushElaborations(httpResponse, ctx, pendingList);
		} else if(PARAM_ACTION_PROCCESS.equals(_action)){
			if(!"".equals(_ids)){
				String[] ids = _ids.replace(" ", "").split(",");
				if(ids.length>0){
					int[] idArray = Arrays.asList(ids).stream().mapToInt(id -> Integer.valueOf(id)).toArray();
					initElaborationProccess(ctx, idArray);
					httpResponse.setStatus(HttpServletResponse.SC_CREATED);
				}
			}
		} else {
			// TODO return helpMessage
			flushErrors(httpResponse,
					ctx);
			httpResponse.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
		}

	}

	// TODO flushErrors
	private void flushErrors(HttpServletResponse httpResponse,
			AONContext ctx) throws IOException {
//		RESPUESTAELABORACIONES respuesta = new RESPUESTAELABORACIONES();	
//		respuesta.setDATOSRESPUESTAELABORACIONES(new RESPUESTAELABORACIONESTYPE());	
//		respuesta.getDATOSRESPUESTAELABORACIONES().setPARAMETROSADMITIDOS(new PARAMETROSADMITIDOS());	
//		respuesta.getDATOSRESPUESTAELABORACIONES().getPARAMETROSADMITIDOS().setAction(new Action());	
//		respuesta.getDATOSRESPUESTAELABORACIONES().getPARAMETROSADMITIDOS().getAction();	
////		respuesta.getDATOSRESPUESTAELABORACIONES().getPARAMETROSADMITIDOS().getDate().DESCRIPTION;	
//		respuesta.setERRORES(new ERRORESTYPE());	
//		respuesta.getERRORES().getERRORES().add("error1");	
//		respuesta.getERRORES().getERRORES().add("error2");
//		
//		String xml = convertToXml(respuesta, RESPUESTAELABORACIONES.class);
//		
//		// httpResponse.setHeader("", "");
//		// httpResponse.setContentType("application/json");
//		httpResponse.setContentType("application/xml");
//		// httpResponse.setContentType("text/xml;charset=UTF-8");
//		httpResponse.setContentLength(xml.length());
//		
//		PrintWriter out = httpResponse.getWriter();
//		out.print(xml);
//		out.flush();
	}
	
	private void flushElaborations(HttpServletResponse httpResponse,
			AONContext ctx, List<Elaboration> pendingList) throws IOException {
		
		ELABORACIONES elaboraciones = fillElaborationData(ctx, pendingList);
		String xml = convertToXml(elaboraciones, ELABORACIONES.class);

		// httpResponse.setHeader("", "");
		// httpResponse.setContentType("application/json");
		httpResponse.setContentType("application/xml");
		// httpResponse.setContentType("text/xml;charset=UTF-8");
		httpResponse.setContentLength(xml.length());

		PrintWriter out = httpResponse.getWriter();
		out.print(xml);
		out.flush();
	}
	
	// TODO initElaborationProccess
	private void initElaborationProccess(AONContext ctx, int[] ids) {
//		ctx.transaction(t -> {
//			for (int i = 0; i < ids.length; i++) {
//				Integer id = ids[i];
//				Elaboration elaboration = ElaborationDAO
//						.getElaboration(ctx, id);
//				elaboration.setStatus(ElaborationStatus.IN_PROGRESS.value());
//				ElaborationDAO.updateElaboration(ctx, elaboration);
//			}
//		});
	}
	
	private ELABORACIONES fillElaborationData(AONContext ctx, List<Elaboration> pendingList) {
		ELABORACIONES elaboraciones = new ELABORACIONES();
		pendingList.forEach(elaboration -> {
			Item item = ProductDAO.getItem(ctx, elaboration.getItem().getId());
			Product product = ProductDAO.getProduct(ctx, item.getProduct().getId());
			SalesDetail salesDetail = obtainSalesDetail(ctx, elaboration);
			Sales sales = obtainSales(ctx, salesDetail.getSales());
			Customer customer = obtainCustomer(ctx, salesDetail.getSales());
			ELABORACIONTYPE elaboracion = new ELABORACIONTYPE();
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
			elaboraciones.getDATOSELABORACIONES().add(elaboracion);
		});
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
			datos.setPROVINCIA(gz.getName());
		}
		return datos;
	}

	private DATOSCLIENTETYPE obtainDATOSCLIENTE(AONContext ctx, SalesDetail salesDetail, Customer customer) {
		Registry registry = obtainRegistry(ctx, customer.getId());
		
		DATOSCLIENTETYPE datos = new DATOSCLIENTETYPE();
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

	private List<Elaboration> getPendingList(AONContext ctx, Date date){
		List<Elaboration> elaborationList = ElaborationDAO.getElaborationList(ctx, p -> {
			return p.getStatusProperty().eq(ElaborationStatus.PENDING.value())
					.and(date != null ? p.getDateProperty().eq(new java.sql.Timestamp(date.getTime()))
							: p.getDateProperty().isNotNull())
							;
		});
		return elaborationList;
	}
	
	
	/*
	 * JAXB
	 */
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

	

    
	public static void main(String[] args) throws Exception {
		String path = "http://";
		path += "udapa.esferalia.net";
		path += ":8080";
		path += "/aon-aio";
		path += "/ingenet/elaboration";
		
		String user = "ingenet";
		String passwd = "1ng3n3t";
		String date = "2016-12-19";
        
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
        postData.append(URLEncoder.encode(PARAM_DATE, "UTF-8"));
        postData.append('=');
        postData.append(URLEncoder.encode(date, "UTF-8"));
        
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

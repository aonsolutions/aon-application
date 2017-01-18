package com.esferalia.aon.ingenet.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import com.esferalia.aon.ingenet.api.pedidos.CIFNIFTYPE;
import com.esferalia.aon.ingenet.api.pedidos.DATOSAGENCIATRANSPORTETYPE;
import com.esferalia.aon.ingenet.api.pedidos.DATOSCENTROTRABAJOTYPE;
import com.esferalia.aon.ingenet.api.pedidos.DATOSCLIENTETYPE;
import com.esferalia.aon.ingenet.api.pedidos.DATOSDIRECCIONTYPE;
import com.esferalia.aon.ingenet.api.pedidos.DATOSREGISTROTYPE;
import com.esferalia.aon.ingenet.api.pedidos.PAISTYPE;
import com.esferalia.aon.ingenet.api.pedidos.PEDIDOS;
import com.esferalia.aon.ingenet.api.pedidos.PEDIDOTYPE;
import com.esferalia.aon.ingenet.api.pedidos.PRODUCTOTYPE;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
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
	
	private String returnValue;
	
	
	protected void processRequest(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws ServletException, IOException {
		
		String _date = httpRequest.getParameter(PARAM_DATE);
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
//		Gson gson = new Gson();
//		returnValue = "";
//		pendingList.forEach(elaboration -> {
//			fillResponse(gson, elaboration);
//			List<ElaborationDetail> detailList = getDetailList(ctx, elaboration.getId());
//			detailList.forEach(elaborationDetail -> {
//				fillResponse(gson, elaborationDetail);
//			});
//		});

		PEDIDOS orders = fillElaborationData(ctx, pendingList);
		returnValue = convertToXml(orders, PEDIDOS.class);
        
		
//		httpResponse.setHeader("", "");
//		httpResponse.setContentType("application/json");
		httpResponse.setContentType("application/xml");
//		httpResponse.setContentType("text/xml;charset=UTF-8");
		httpResponse.setContentLength(returnValue.length());
		
		PrintWriter out = httpResponse.getWriter();
		out.print(returnValue);
		out.flush();
	}
	
	private PEDIDOS fillElaborationData(AONContext ctx, List<Elaboration> pendingList) {
		PEDIDOS pedidos = new PEDIDOS();
		pendingList.forEach(elaboration -> {
			Item item = ProductDAO.getItem(ctx, elaboration.getItem().getId());
			Product product = ProductDAO.getProduct(ctx, item.getProduct().getId());
			SalesDetail salesDetail = obtainSalesDetail(ctx, elaboration);
			Sales sales = obtainSales(ctx, salesDetail.getSales());
			Customer customer = obtainCustomer(ctx, salesDetail.getSales());
			PEDIDOTYPE pedido = new PEDIDOTYPE();
			pedido.setDATOSCLIENTE(obtainDATOSCLIENTE(ctx, salesDetail, customer));
		    pedido.setSERIE(elaboration.getSeries());
		    pedido.setNUMERO(String.valueOf(elaboration.getNumber()));
		    pedido.setREFERENCIAPEDIDO("");
		    pedido.setDATOSDIRECCIONENTREGA(obtainDATOSDIRECCIONENTREGA(ctx, sales));
		    pedido.setFECHAEMISION(dateFormatter.format(elaboration.getDate()));
		    pedido.setCOMENTARIOS(elaboration.getComments());
		    pedido.setDATOSCENTROTRABAJO(obtainDATOSCENTROTRABAJO(ctx, sales));
		    pedido.setDATOSAGENCIATRANSPORTE(obtainDATOSAGENCIATRANSPORTE(elaboration));
		    pedido.setPRODUCTO(new PRODUCTOTYPE());
		    pedido.getPRODUCTO().setCODIGO(product.getCode());
		    pedido.getPRODUCTO().setNOMBRE(product.getName());
		    pedido.getPRODUCTO().setDESCRIPCION(item.getDescription());
		    pedido.getPRODUCTO().setDETALLE(item.getDetail());
		    pedido.getPRODUCTO().setDETALLE2(item.getDetail2());
		    pedido.getPRODUCTO().setDETALLE3(item.getDetail3());
		    pedido.getPRODUCTO().setFECHASERIE(null);
		    pedido.getPRODUCTO().setNUMEROSERIE(null);
		    pedido.getPRODUCTO().setCODIGOBARRAS(item.getBarcode());
		    pedido.getPRODUCTO().setREFERENCIACLIENTE(obtainCustomerProductCode(ctx, elaboration.getItem(), customer));
			pedido.setCANTIDAD(String.valueOf((int)elaboration.getQuantity()));
		    pedidos.getPEDIDO().add(pedido);
		});
		return pedidos;
	}

	private String obtainCustomerProductCode(AONContext ctx, Item item, Customer customer) {
		if(item!=null && item.getId()!=null
			&& customer!=null && customer.getId()!=null){
			return ElaborationDAO.getCustomerItemCode(ctx, item.getId(), customer.getId());
		}
		return null;
	}
	
	private DATOSAGENCIATRANSPORTETYPE obtainDATOSAGENCIATRANSPORTE(
			Elaboration elaboration) {
		// TODO Auto-generated method stub
		DATOSAGENCIATRANSPORTETYPE datos = new DATOSAGENCIATRANSPORTETYPE();
		datos.setDATOSREGISTRO(new DATOSREGISTROTYPE());
		datos.getDATOSREGISTRO().setDOCUMENTO(new CIFNIFTYPE());
		datos.getDATOSREGISTRO().getDOCUMENTO().setPAISDOCUMENTO(null);
		datos.getDATOSREGISTRO().getDOCUMENTO().setTIPODOCUMENTO(null);
		datos.getDATOSREGISTRO().getDOCUMENTO().setDOCUMENTO(null);
		datos.getDATOSREGISTRO().setNOMBRE(null);
		datos.getDATOSREGISTRO().setALIAS(null);
		datos.getDATOSREGISTRO().setNACIONALIDAD(new PAISTYPE());
		datos.getDATOSREGISTRO().getNACIONALIDAD().setCODIGO(null);
		datos.getDATOSREGISTRO().getNACIONALIDAD().setDESCRIPCION(null);
		datos.getDATOSREGISTRO().setTELEFONOFIJO(null);
		datos.getDATOSREGISTRO().setTELEFONOMOVIL(null);
		
		return datos;
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

	private DATOSDIRECCIONTYPE obtainDATOSDIRECCIONENTREGA(
			AONContext ctx, Sales sales) {
		DATOSDIRECCIONTYPE datos = new DATOSDIRECCIONTYPE();
		datos.setDIRECCION(sales.getShippingAlternativeAddress());
		datos.setDIRECCION2(sales.getShippingAlternativeAddress2());
		datos.setDIRECCION3(null);
		datos.setCIUDAD(sales.getShippingAlternativeCity());
		datos.setCODIGOPOSTAL(sales.getShippingAlternativeZip());
		datos.setPROVINCIA(null);
		return datos;
	}

	private DATOSCLIENTETYPE obtainDATOSCLIENTE(AONContext ctx, SalesDetail salesDetail, Customer customer) {
		Registry registry = obtainRegistry(ctx, customer.getId());
		
		DATOSCLIENTETYPE datos = new DATOSCLIENTETYPE();
		datos.setALBARANVALORADO(new Byte("1").equals(customer.getDeliveryValuated())?"S":"N");
		datos.setDATOSREGISTRO(new DATOSREGISTROTYPE());
		datos.getDATOSREGISTRO().setDOCUMENTO(new CIFNIFTYPE());
		datos.getDATOSREGISTRO().getDOCUMENTO().setPAISDOCUMENTO(new PAISTYPE());
		datos.getDATOSREGISTRO().getDOCUMENTO().getPAISDOCUMENTO().setCODIGO(String.valueOf(registry.getDocumentCountry().getIsoCode()));
		datos.getDATOSREGISTRO().getDOCUMENTO().getPAISDOCUMENTO().setDESCRIPCION(registry.getDocumentCountry().getName());
		datos.getDATOSREGISTRO().getDOCUMENTO().setTIPODOCUMENTO(registry.getDocumentType().name());
		datos.getDATOSREGISTRO().getDOCUMENTO().setDOCUMENTO(registry.getDocument());
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
	
	private RAddress obtainAddress(AONContext ctx, Integer registry) {
		if (registry != null) {
			return RegistryDAO.getRAddressStream(ctx, registry).findFirst()
					.orElse(new RAddress());
		}
		return null;
	}

//	private void fillResponse(Gson gson, Elaboration elaboration) {
//		this.returnValue += gson.toJson(elaboration);
//	}
//	
//	private void fillResponse(Gson gson, ElaborationDetail detail) {
//		this.returnValue += gson.toJson(detail);
//	}

	private List<Elaboration> getPendingList(AONContext ctx, Date date){
		List<Elaboration> elaborationList = ElaborationDAO.getElaborationList(ctx, p -> {
			return p.getStatusProperty().eq(ElaborationStatus.PENDING.value())
					.and(date != null ? p.getDateProperty().eq(new java.sql.Timestamp(date.getTime()))
							: p.getDateProperty().isNotNull())
							;
		});
		return elaborationList;
	}

	private List<ElaborationDetail> getDetailList(AONContext ctx, Integer elaborationId){
		List<ElaborationDetail> list = ElaborationDAO.getElaborationDetailList(ctx, p -> {
			return p.getElaborationProperty().eq(elaborationId)
					;
		});
		return list;
	}
	
	
	public String convertToXml(Object source, Class<?>... type) {
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

    public String extractValue(String xml, String xpathExpression) {
        String actual;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilderFactory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();

            byte[] bytes = xml.getBytes("UTF-8");
            InputStream inputStream = new ByteArrayInputStream(bytes);
            Document doc = docBuilder.parse(inputStream);
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            actual = xpath.evaluate(xpathExpression, doc, XPathConstants.STRING).toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return actual;
    }

	

	public static void main(String[] args) throws Exception {
		String path = "http://";
		path += "udapa.aonsolutions.net";
		path += ":8080";
		path += "/aon-aio/";
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

package com.esferalia.aon.ingenet.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import com.esferalia.aon.ingenet.api.albaranes.ALBARANES;
import com.esferalia.aon.ingenet.api.albaranes.ALBARANTYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSCLIENTETYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSDIRECCIONTYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSLINEAALBARANTYPE;
import com.esferalia.aon.ingenet.api.albaranes.ELABORACIONORIGENTYPE;
import com.esferalia.aon.ingenet.api.albaranes.PRODUCTOTYPE;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.impl.jooq.dao.ElaborationDAO;

public class IngenetDeliveryServlet extends AbstractIngenetServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
	private final static String PARAM_VALUE = "value";
	
		
	protected void processRequest(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws ServletException, IOException {
		
		String _xml = httpRequest.getParameter(PARAM_VALUE);
		if(_xml!=null){
			ALBARANES deliveryList = extractValue(_xml);
			if(deliveryList!=null && deliveryList.getDATOSALBARANES()!=null 
					&& deliveryList.getDATOSALBARANES().size()>0){
				processData(deliveryList.getDATOSALBARANES());
				httpResponse.sendError(HttpServletResponse.SC_CREATED);
			} else {
				// TODO: deliveries file is empty
				httpResponse.sendError(HttpServletResponse.SC_NO_CONTENT);
			}
		} else {
			httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
		
	}
	
	private void processData(List<ALBARANTYPE> list){
		AONContext ctx = AONContext.getAONContext(getDomain(), getDomainId(), getUser());
		ctx.getDslContext().transaction(configuration -> {
			list.forEach(albaran -> {
				createDelivery(ctx, albaran);
				createCarrierPacking(albaran);
			});
		});
	}

	private Delivery createDelivery(AONContext ctx, ALBARANTYPE albaran) {
		Delivery delivery = new Delivery(); 
		fillDelivery(ctx, albaran, delivery);		
//		WarehouseDAO.insertDelivery(ctx, delivery);
		List<DeliveryDetail> detailList = new LinkedList<DeliveryDetail>();
		fillDeliveryDetailList(ctx, albaran.getLINEASALBARAN().getDATOSLINEAALBARAN(), delivery, detailList);
//		WarehouseDAO.insertDeliveryDetails(ctx, detailList);
		return delivery;
	}

	private Delivery fillDelivery(AONContext ctx, ALBARANTYPE albaran, Delivery delivery) {
		delivery.setDomain(ctx.getDomainId());
		delivery.setProject(null);
		delivery.setSeries("IGN"+new SimpleDateFormat("yy").format(new Date()));
		delivery.setNumber(Integer.valueOf(albaran.getNUMERO()));
		delivery.setCustomer(obtainCustomer(albaran.getDATOSCLIENTE()));
		delivery.setAddress(obtainAddress(albaran.getDATOSDIRECCIONENTREGA()));
		// if address == null, fill shippingAlternative
		
		try {
			delivery.setIssueTime(dateFormatter.parse(albaran.getFECHAEMISION()));
		} catch (ParseException e) {
			// TODO log me
			delivery.setIssueTime(new Date());
		}
		delivery.setSecurityLevel((byte) 0);
		delivery.setStatus(DeliveryStatus.PENDING);
		delivery.setComments(albaran.getCOMENTARIOS());
		delivery.setRemarks("Importado por INGENET el "
				+ new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
		delivery.setWorkplace(null);
		delivery.setScope(null);
		delivery.setPayMethod(null);
		delivery.setNumberOfPymnts((short) 0);
		delivery.setDaysToFirstPymnt((short) 0);
		delivery.setDaysBetweenPymnt((short) 0);
		delivery.setPymntDays(null);
		delivery.setBankAccount(null);
		delivery.setBankAlias(null);
		delivery.setBic(null);
		return delivery;
	}


	private List<DeliveryDetail> fillDeliveryDetailList(AONContext ctx,
			List<DATOSLINEAALBARANTYPE> list, Delivery delivery,
			List<DeliveryDetail> detailList) {
		if (list != null && list.size() > 0) {
			list.stream()
					.filter(linea -> !isPackageItem(linea))
					.forEach(
							linea -> {
								DeliveryDetail detail = new DeliveryDetail();
								detail.setDomain(ctx.getDomainId());
								detail.setDelivery(delivery);
								detail.setLine(Short.valueOf(linea.getLINEA()));
								Item item = obtainItem(linea
										.getPRODUCTOELABORADO());
								detail.setItem(item);
								detail.setDescription(linea.getDESCRIPCION());
								detail.setWarehouse(null);
								detail.setQuantity(Double.valueOf(linea
										.getCANTIDAD()));
								detail.setPrice(item.getPrice());
								detail.setDiscountExpression("");
								detail.setSalesDetail(obtainSales(linea
										.getDATOSELABORACIONORIGEN()));
								detailList.add(detail);
								manageElaboration(ctx, linea);
							});
			list.stream()
					.filter(linea -> isPackageItem(linea))
					.sorted((linea1, linea2) -> linea1.getDESCRIPCION()
							.compareTo(linea2.getDESCRIPCION()))
					.forEach(
							linea -> {
								DeliveryDetail detail = new DeliveryDetail();
								detail.setDomain(ctx.getDomainId());
								detail.setDelivery(delivery);
								detail.setLine(Short.valueOf(linea.getLINEA()));
								detail.setItem(obtainItem(linea
										.getPRODUCTOELABORADO()));
								detail.setQuantity(Double.valueOf(linea
										.getCANTIDAD()));
								detailList.add(detail);
							});
		}
		return detailList;
	}

	
	private void manageElaboration(AONContext ctx,
			DATOSLINEAALBARANTYPE lineaAlbaran) {

		Elaboration elaboration = obtainElaboration(lineaAlbaran
				.getDATOSELABORACIONORIGEN());

		ElaborationDetail elaborationDetail = new ElaborationDetail();
		elaborationDetail.setDomain(ctx.getDomainId());
		elaborationDetail.setElaboration(elaboration);
		elaborationDetail.setDate(new Date());
		elaborationDetail.setItem(obtainItem(lineaAlbaran
				.getPRODUCTOELABORADO()));
		elaborationDetail
				.setQuantity(Double.valueOf(lineaAlbaran.getCANTIDAD()));
		elaborationDetail.setWarehouse(null);
		elaborationDetail.setAddInfo("");
		ElaborationDAO.insertElaborationDetail(ctx, elaborationDetail);

		lineaAlbaran
				.getCOMPOSICIONPRODUCTOELABORADO()
				.getDATOSCOMPOSICIONPRODUCTO()
				.forEach(
						lineaComposicion -> {
							ElaborationDetailComposition elaborationDetailComposition = new ElaborationDetailComposition();
							elaborationDetailComposition.setDomain(ctx
									.getDomainId());
							elaborationDetailComposition
									.setElaborationDetail(elaborationDetail);
							elaborationDetailComposition
									.setItem(obtainItem(lineaComposicion
											.getPRODUCTO()));
							elaborationDetailComposition.setQuantity(Double
									.valueOf(lineaComposicion.getCANTIDAD()));
							elaborationDetailComposition.setWarehouse(null);
							elaborationDetailComposition.setAddInfo(null);
							ElaborationDAO.insertElaborationDetailComposition(
									ctx, elaborationDetailComposition);
						});
	}
	
	
	// TODO: create carrier_packing
	private void createCarrierPacking(ALBARANTYPE albaran) {
		albaran.getDATOSHOJARUTA();
	}
	

	
	
	
	private boolean isPackageItem(DATOSLINEAALBARANTYPE linea) {
		return "S".equals(linea.getENVASE());
	}
	
	// TODO obtainElaboration
	private Elaboration obtainElaboration(
			ELABORACIONORIGENTYPE datoselaboracionorigen) {
		return null;
	}

	// TODO obtainAddress
	private Integer obtainAddress(DATOSDIRECCIONTYPE datosdireccionentrega) {
		return null;
	}
	
	// TODO obtainCustomer
	private Integer obtainCustomer(DATOSCLIENTETYPE datoscliente) {
		datoscliente.getDATOSREGISTRO().getDOCUMENTO();
		return null;
	}
	
	// TODO obtainSales
	private Integer obtainSales(ELABORACIONORIGENTYPE datospedidoorigen) {
		return null;
	}

	// TODO obtainItem
	private Item obtainItem(PRODUCTOTYPE productoelaborado) {
		return null;
	}
	
	
	/*
	 * JAXB
	 */
	private ALBARANES extractValue(String xml) throws IOException {
    	InputStream inputStream = null;
    	try {
			byte[] bytes = xml.getBytes("UTF-8");
			inputStream = new ByteArrayInputStream(bytes);
			String contextPath = ALBARANES.class.getPackage().getName();
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setEventHandler(new AlbaranesValidationEventHandler());
			ALBARANES albaranes = (ALBARANES) unmarshaller.unmarshal(inputStream);
			return albaranes;
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
    
    public class AlbaranesValidationEventHandler implements ValidationEventHandler {
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
		path += "/ingenet/delivery";

		String user = "ingenet";
		String passwd = "1ng3n3t";
		
		String FILENAME = "C:\\TEMP\\delivery_example.xml";
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
        br.close();
	}
		
}

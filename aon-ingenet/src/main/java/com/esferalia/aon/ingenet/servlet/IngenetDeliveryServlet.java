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
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO;

public class IngenetDeliveryServlet extends AbstractIngenetServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final static String PARAM_VALUE = "value";
	
		
	protected void processRequest(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws ServletException, IOException {
		
		String _xml = httpRequest.getParameter(PARAM_VALUE);
		if(_xml!=null){
			ALBARANES deliveryList = extractValue(_xml);
			if(deliveryList!=null && deliveryList.getALBARAN()!=null 
					&& deliveryList.getALBARAN().size()>0){
				// TODO: create deliveries
				AONContext ctx = AONContext.getAONContext(getDomain(), getDomainId(), getUser());
				httpResponse.sendError(HttpServletResponse.SC_CREATED);
			} else {
				// TODO: deliveries file is empty
				httpResponse.sendError(HttpServletResponse.SC_NO_CONTENT);
			}
		} else {
			httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
		
	}
	
	private Delivery createDelivery(Delivery delivery, List<DeliveryDetail> detailList) {
		AONContext ctx = AONContext.getAONContext(getDomain(), getDomainId(), getUser());
		ctx.getDslContext().transaction(configuration -> {
			WarehouseDAO.insertDelivery(ctx, delivery);
			WarehouseDAO.insertDeliveryDetails(ctx, detailList);
		});		
		return delivery;
	}
	
	
	/*
	 * JAXB
	 */

//    private String extractValue(String xml, String xpathExpression) {
//        String actual;
//        try {
//            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//            documentBuilderFactory.setNamespaceAware(true);
//            documentBuilderFactory.setIgnoringElementContentWhitespace(true);
//            DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
//
//            byte[] bytes = xml.getBytes("UTF-8");
//            InputStream inputStream = new ByteArrayInputStream(bytes);
//            Document doc = docBuilder.parse(inputStream);
//            XPathFactory xPathFactory = XPathFactory.newInstance();
//            XPath xpath = xPathFactory.newXPath();
//
//            actual = xpath.evaluate(xpathExpression, doc, XPathConstants.STRING).toString();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        return actual;
//    }
    private ALBARANES extractValue(String xml) throws IOException {
    	InputStream inputStream = null;
    	try {
			byte[] bytes = xml.getBytes("UTF-8");
			inputStream = new ByteArrayInputStream(bytes);
			inputStream.reset();
			
			JAXBContext contratoContext = JAXBContext.newInstance(ALBARANES.class.getPackage().getName());
			
			Unmarshaller unmarshaller = contratoContext.createUnmarshaller();
			unmarshaller.setEventHandler(new AlbaranesValidationEventHandler());
			ALBARANES albaranes = (ALBARANES) unmarshaller.unmarshal(inputStream);
			return albaranes;
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(inputStream!=null){
				inputStream.close();
			}
		}
		return null;
    }
    
    public class AlbaranesValidationEventHandler implements ValidationEventHandler {
		public boolean handleEvent(ValidationEvent ve) {
			if (ve.getSeverity() == ValidationEvent.FATAL_ERROR || ve.getSeverity() == ValidationEvent.ERROR) {
				ValidationEventLocator locator = ve.getLocator();
				// Print message from valdation event
				System.out.println("Invalid booking document: " + locator.getURL());
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

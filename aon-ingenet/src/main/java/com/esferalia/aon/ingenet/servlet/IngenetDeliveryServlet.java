package com.esferalia.aon.ingenet.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO;
import com.google.gson.Gson;

public class IngenetDeliveryServlet extends AbstractIngenetServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
		
	protected void processRequest(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws ServletException, IOException {
		
		String _value = httpRequest.getParameter("value");
		
		if(_value!=null){
			
			Gson gson = new Gson();
			Delivery delivery = gson.fromJson(_value, Delivery.class);
			
			delivery = createDelivery(delivery, null);
			
			
			if(delivery!=null && delivery.getId()!=null){
				httpResponse.sendError(HttpServletResponse.SC_CREATED);
			} else {
				// TODO: 
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
	

	public static void main(String[] args) throws Exception {
		String path = "http://";
		path += "udapa.aonsolutions.net";
		path += ":8080";
		path += "/aon-aio/";
		path += "/ingenet/delivery";

		 String user = "ingenet";
		String passwd = "1ng3n3t";
        
        StringBuilder postData = new StringBuilder();
        postData.append('&');
        postData.append(URLEncoder.encode(PARAM_USERNAME, "UTF-8"));
        postData.append('=');
        postData.append(URLEncoder.encode(user, "UTF-8"));
        postData.append('&');
        postData.append(URLEncoder.encode(PARAM_PASSWORD, "UTF-8"));
        postData.append('=');
        postData.append(URLEncoder.encode(passwd, "UTF-8"));
        
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

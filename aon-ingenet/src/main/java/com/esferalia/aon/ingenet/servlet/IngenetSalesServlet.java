package com.esferalia.aon.ingenet.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.type.SalesStatus;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO;
import com.google.gson.Gson;

public class IngenetSalesServlet extends AbstractIngenetServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String PARAM_DATE = "date";
	
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
		
		List<Sales> list = getPendingList(date);
		Gson gson = new Gson();
		String returnValue = gson.toJson(list);
		
//		httpResponse.setHeader("", "");
		httpResponse.setContentType("application/json");
		httpResponse.setContentLength(returnValue.length());
		
		PrintWriter out = httpResponse.getWriter();
		out.print(returnValue);
		out.flush();
	}
	
	private List<Sales> getPendingList(Date date){
		// TODO Auto-generated method stub
		
		AONContext ctx = AONContext.getAONContext(getDomain(), getDomainId(), getUser());
		Sales sales = SalesDAO.getSales(ctx, p -> {
			return p.getStatusProperty().eq(SalesStatus.PENDING.value())
					.and(date != null ? p.getIssueDateProperty().eq(new java.sql.Date(date.getTime()))
							: p.getIssueDateProperty().isNotNull());
		});

		List<Sales> list = new LinkedList<>();
		list.add(sales);
		return list;
	}

	

	public static void main(String[] args) throws Exception {
		String path = "http://";
		path += "udapa.aonsolutions.net";
		path += ":8080";
		path += "/aon-aio/";
		path += "/ingenet/sales";
		
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

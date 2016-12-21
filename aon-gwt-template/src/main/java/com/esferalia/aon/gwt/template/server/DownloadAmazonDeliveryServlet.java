package com.esferalia.aon.gwt.template.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.template.jooq.DBMarketplace;
import com.esferalia.aon.gwt.template.shared.marketplace.AmazonDelivery;
import com.esferalia.aon.gwt.template.shared.marketplace.Order;
import com.esferalia.aon.occam.api.model.Domain;

@WebServlet(name = "DownloadAmazonDelivery", urlPatterns = { "/aon_gwt_template/gwt_download_amazon_delivery/*"
															 ,"/aon_gwt_aio/gwt_download_amazon_delivery/*"})
public class DownloadAmazonDeliveryServlet extends HttpServlet {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response)throws ServletException, IOException{
        String domain_id = p_request.getParameter("domain_id");
        String login = p_request.getParameter("username");
        Integer domainId = Integer.parseInt(domain_id);
        String domainName = AonServletUtils.getRequestDomainName(p_request);
        Domain domain = new Domain().setId(domainId).setName(domainName); 
        
        HSSFWorkbook libro = new HSSFWorkbook();
        ByteArrayOutputStream archivo = new ByteArrayOutputStream();
        libro.createSheet("Primeros Pasos");
        libro.createSheet("Definición de Datos");
        HSSFSheet hoja = libro.createSheet("Plantilla");
        
        Row header = hoja.createRow(0);
     
        header.createCell(0).setCellValue("order-id");
        header.createCell(1).setCellValue("order-item-id");
        header.createCell(2).setCellValue("quantity");
        header.createCell(3).setCellValue("ship-date");
        header.createCell(4).setCellValue("carrier-code");
        header.createCell(5).setCellValue("carrier-name");
        header.createCell(6).setCellValue("tracking-number");
        header.createCell(7).setCellValue("ship-method");
        
        List<Order> orderDeliveryList = DBMarketplace.getOrderDeliveryList(domain, login);
        for (Integer i = 1; i<= orderDeliveryList.size(); i++) {
        	AmazonDelivery ad = orderDeliveryList.get(i-1).getAmazonDelivery(); 
        	Row row = hoja.createRow(i);
        	 row.createCell(0).setCellValue(ad.getOrderId());
        	 row.createCell(1).setCellValue(ad.getOrderItemId());
        	 if(ad.getQuantity()!= null)row.createCell(2).setCellValue(ad.getQuantity());
        	 row.createCell(3).setCellValue(ad.getShipDateStr());
        	 Cell c4 = row.createCell(4);
        	 if(ad.getCarrierCode() != null && ad.getCarrierCode().getName()!= null)
        		 c4.setCellValue(ad.getCarrierCode().getName());
        	 row.createCell(5).setCellValue(ad.getCarrierName());
        	 row.createCell(6).setCellValue(ad.getTrackingNumber());
        	 row.createCell(7).setCellValue(ad.getShipMethod());
		}
        
        for(Integer h = 0; h< 8;h++){
        	hoja.autoSizeColumn(h);
        }
        libro.write(archivo);       
        byte[] data = archivo.toByteArray();
        archivo.close();
        libro.close();
        
        Integer length = data.length;
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        
        p_response.addHeader("Content-Disposition","attachment; filename=\"" + "AmazonDelivery-"+ domainId  +".xls" +"\"");
        //p_response.setContentType("application/octet-stream");
        p_response.setContentType("application/msexcel");

        if (length > 0 && length <= Integer.MAX_VALUE);
            p_response.setContentLength((int)length);
        ServletOutputStream out = p_response.getOutputStream();
        p_response.setBufferSize(32768);
        int bufSize = p_response.getBufferSize();
        byte[] buffer = new byte[bufSize];
        BufferedInputStream bis = new BufferedInputStream(bais,bufSize);
        int bytes;
        while ((bytes = bis.read(buffer, 0, bufSize)) >= 0)
            out.write(buffer, 0, bytes);
        
        bis.close();
        bais.close();
        out.flush();
        out.close();
	}
}

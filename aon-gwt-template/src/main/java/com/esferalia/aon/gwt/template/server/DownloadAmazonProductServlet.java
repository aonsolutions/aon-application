package com.esferalia.aon.gwt.template.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.template.server.marketplace.XMLUtils;
import com.esferalia.aon.gwt.template.shared.EcommerceProduct;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.AttachmentType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;

@WebServlet(name = "DownloadAmazonProduct", urlPatterns = { "/aon_gwt_template/gwt_download_amazon_product/*" })
public class DownloadAmazonProductServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response) throws IOException{		
		String domain_id = p_request.getParameter("domain_id");
        String login = p_request.getParameter("username");
        String description = p_request.getParameter("description");
        Integer domainId = Integer.parseInt(domain_id);
        String domainName = AonServletUtils.getRequestDomainName(p_request);
        
		LinkedList<Attach> iattachList = AON.getAttachList(domainName, domainId, login,
				filter -> filter.getTypeProperty().eq(AttachmentType.ECOMMERCE_PRODUCT.value())
				.and(filter.getDescriptionProperty().eq(description))
				.and(filter.getDomainProperty().eq(domainId)),
				AttachType.ITEM);
		
		Attach attach = AON.getAttach(domainName, domainId, login,
				f -> f.getDomainProperty().eq(domainId)
				.and(f.getDescriptionProperty().eq(description))
				.and(f.getTypeProperty().eq(RegistryAttachmentType.ECOMMERCE_PRODUCT_TEMPLATES.value())),
				AttachType.REGISTRY);
		
		File xlsFile = File.createTempFile("AmazonProducts"+domainId, ".xls");
		FileOutputStream outFile = new FileOutputStream(xlsFile);
		
		HSSFWorkbook book = new HSSFWorkbook();
		HSSFSheet sheet = book.createSheet("Template");
		
		Row header0 = sheet.createRow(0);
		Row header1 = sheet.createRow(1);
		Row header2 = sheet.createRow(2);
		EcommerceProduct ep = new EcommerceProduct();
		try {
			ep = XMLUtils.readXml(attach.getData());
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	
		for(Integer column = 0; column < ep.getProductData().getEcommerce().size(); column++){
			if(column == 0) header0.createCell(column).setCellValue(ep.getTemplate().getAmazonTemplateType());
			if(column == 1) header0.createCell(column).setCellValue(ep.getTemplate().getAmazonVersion());
			header1.createCell(column).setCellValue(ep.getProductData().getEcommerce().get(column).getName());
			header2.createCell(column).setCellValue(ep.getProductData().getEcommerce().get(column).getCode());
		}
		
		if(iattachList != null && iattachList.size()>0){

			for (Integer i = 0; i< iattachList.size(); i++) {
				EcommerceProduct ecp = new EcommerceProduct();
				try {
					ecp = XMLUtils.readXml(iattachList.get(i).getData());
				} catch (JAXBException e) {
					e.printStackTrace();
				}
				Row row = sheet.createRow(i+3);
				for(Integer column = 0; column < ep.getProductData().getEcommerce().size(); column++){
					row.createCell(column).setCellValue(ecp.getProductData().getEcommerce().get(column).getValue());
				}
			}

			for(Integer h = 0; h<  ep.getProductData().getEcommerce().size();h++){
				sheet.autoSizeColumn(h);
			}	
		}
		book.write(outFile);        
		outFile.close();
		book.close();
		
		
		long length = xlsFile.length();
		FileInputStream fis = new FileInputStream(xlsFile);
        
        p_response.addHeader("Content-Disposition","attachment; filename=\"" + xlsFile.getName() +"\"");
        //p_response.setContentType("application/octet-stream");
        p_response.setContentType("application/msexcel");

        if (length > 0 && length <= Integer.MAX_VALUE);
            p_response.setContentLength((int)length);
        ServletOutputStream out = p_response.getOutputStream();
        p_response.setBufferSize(32768);
        int bufSize = p_response.getBufferSize();
        byte[] buffer = new byte[bufSize];
        BufferedInputStream bis = new BufferedInputStream(fis,bufSize);
        int bytes;
        while ((bytes = bis.read(buffer, 0, bufSize)) >= 0)
            out.write(buffer, 0, bytes);
        
        bis.close();
        fis.close();
        out.flush();
        out.close();
        xlsFile.delete();
	}
}

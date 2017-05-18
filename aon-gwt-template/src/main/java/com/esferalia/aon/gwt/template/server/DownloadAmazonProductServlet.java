package com.esferalia.aon.gwt.template.server;

import static com.esferalia.aon.jooq.tables.Stock.STOCK;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import org.jooq.Record1;
import org.jooq.Result;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.template.server.marketplace.XMLUtils;
import com.esferalia.aon.gwt.template.shared.EcommerceProduct;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.ItemAttachmentType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.product.Item;

@WebServlet(name = "DownloadAmazonProduct", urlPatterns = { "/aon_gwt_template/gwt_download_amazon_product/*"
															,"/aon_gwt_aio/gwt_download_amazon_product/*"})
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
				filter -> filter.getTypeProperty().eq(ItemAttachmentType.ECOMMERCE_PRODUCT.value())
				.and(filter.getDescriptionProperty().eq(description))
				.and(filter.getDomainProperty().eq(domainId)),
				AttachType.ITEM);
		
		Attach attach = AON.getAttach(domainName, domainId, login,
				f -> f.getDomainProperty().eq(domainId)
				.and(f.getDescriptionProperty().eq(description))
				.and(f.getTypeProperty().eq(RegistryAttachmentType.ECOMMERCE_PRODUCT_TEMPLATES.value())),
				AttachType.REGISTRY);
		
		ByteArrayOutputStream outFile = new ByteArrayOutputStream();
		
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

				Item item = null;
				if(ecp.getProduct().getItem()!= null){
					Integer itemId = Integer.parseInt(ecp.getProduct().getItem());
					item = AON.getItem(domainName, domainId, login, f -> f.getIdProperty().eq(itemId));
				}
				
				for(Integer column = 0; column < ep.getProductData().getEcommerce().size(); column++){
					String value = getValue(domainName, domainId, login, ecp, column, item);
					row.createCell(column).setCellValue(value);
					//TODO VARIABLE SISTEMA!!!
					
				}
			}

			for(Integer h = 0; h<  ep.getProductData().getEcommerce().size();h++){
				sheet.autoSizeColumn(h);
			}	
		}
		book.write(outFile);    
		byte[] data = outFile.toByteArray();
		outFile.close();
		book.close();
		
		
		Integer length = data.length;
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
        
        p_response.addHeader("Content-Disposition","attachment; filename=\"" + "AmazonProducts"+domainId + ".xls" +"\"");
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
	
	
	private String getValue(String domainName, Integer domainId, String login,
			EcommerceProduct ecp, Integer column, Item item) {
		String val = ecp.getProductData().getEcommerce().get(column).getValue();
		if(ecp.getProduct().getItem() == null) 
			return val;
		if(val.contains("{brand}"))
			val = val.replace("{brand}", item.getProduct().getBrandName()!= null? item.getProduct().getBrandName():"");
		if(val.contains("{code}"))
			val = val.replace("{code}", item.getProduct().getCode() != null?item.getProduct().getCode():"");
		if(val.contains("{title}"))
			val = val.replace("{title}", item.getProduct().getName()!= null?item.getProduct().getName():"");
		if(val.contains("{barcode}"))
			val = val.replace("{barcode}", item.getBarcode()!= null?item.getBarcode():"");		
		if(val.contains("{detail}"))
			val = val.replace("{detail}", item.getDetail()!= null?item.getDetail():"");
		if(val.contains("{detail2}"))
			val = val.replace("{detail2}", item.getDetail2()!= null?item.getDetail2():"");
		if(val.contains("{detail3}"))
			val = val.replace("{detail3}", item.getDetail3()!= null?item.getDetail3():"");
		if(val.contains("{sku}")){
			String code = item.getProduct().getCode()!= null?item.getProduct().getCode():"";
			String detail = item.getDetail()!= null? item.getDetail():"";
			String detail2 = item.getDetail2()!= null? item.getDetail2():"";
			String detail3 = item.getDetail3()!= null? item.getDetail3():"";
			val = val.replace("{sku}", code+detail+detail2+detail3);
		}
		if(val.contains("{description}"))
			val = val.replace("{description}", item.getDescription()!= null? item.getDescription():"");
		if(val.contains("{price}"))
			val = val.replace("{price}", String.valueOf(item.getPrice()));
		if(val.contains("{stock}"))
			val = val.replace("{stock}", getItemStock(domainName, domainId, login, item.getId()).toString());
		
		if(val.contains("{image1}"))
			val = val.replace("{image1}", getItemImageUrl(domainName, domainId, login, item.getId(),0));
		if(val.contains("{image2}"))
			val = val.replace("{image2}", getItemImageUrl(domainName, domainId, login, item.getId(),1));
		if(val.contains("{image3}"))
			val = val.replace("{image3}", getItemImageUrl(domainName, domainId, login, item.getId(),2));
		if(val.contains("{image4}"))
			val = val.replace("{image4}", getItemImageUrl(domainName, domainId, login, item.getId(),3));
		if(val.contains("{image5}"))
			val = val.replace("{image5}", getItemImageUrl(domainName, domainId, login, item.getId(),4));
		
		return val;
	
	}
	
	public Double getItemStock(String domainName, Integer domainId, String login, Integer itemId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			Result<Record1<Double>> result = ctx.getDslContext().select(STOCK.QUANTITY).from(STOCK).where(STOCK.ITEM.eq(itemId)).fetch();
			Double d = 0.0;
			for (Record1<Double> r : result) 
				d += r.getValue(STOCK.QUANTITY);
			return d;
		} finally {
			if(ctx != null)
				ctx.close();
		}
	}
	
	public String getItemImageUrl(String domainName, Integer domainId, String login, Integer itemId, Integer i) {
		LinkedList<Attach> attach = AON.getAttachList(domainName, domainId, login,
				f -> f.getAttachModuleProperty().eq(itemId).and(f.getTypeProperty().eq(ItemAttachmentType.IMAGE.value())), AttachType.ITEM);
		if(attach != null && i<attach.size())
			return domainName+"/aonItemImage/"+attach.get(i).getId()+"."+attach.get(i).getMimeType().getExtension();
		else return "";
	}
}

package com.code.aon.webservice.commercial;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;

import com.code.aon.webservice.common.Utils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.management.Offer;
import com.esferalia.aon.occam.api.model.management.OfferProperties;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

@WebServlet(name = "DownloadOfferExcel", urlPatterns = {"/aon_gwt_aio/download_offer_excel/*"})
public class DownloadOfferExcelServlet extends HttpServlet {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Filter offerFilter(HttpServletRequest req, OfferProperties f) {
		Integer domainId = Integer.parseInt(req.getParameter("domain_id"));
		String statuses = req.getParameter("statuses");
		String target = req.getParameter("target");
		String seller = req.getParameter("seller");
		String supplier = req.getParameter("supplier");
		String project = req.getParameter("project");
		String type = req.getParameter("type");
		String series = req.getParameter("series");
		String fromNumber = req.getParameter("from_number");
		String toNumber = req.getParameter("to_number");
		String fromDate = req.getParameter("from_date");
		String toDate = req.getParameter("to_date");
		String workplace = req.getParameter("workplace");
		String scope = req.getParameter("scope");
		String confidential = req.getParameter("confidential");
		String signed = req.getParameter("signed");
				
		Filter filter = f.getDomainProperty().eq(domainId);
		if(statuses != null && !"null".equals(statuses) && !"".equals(statuses)) {
			String[] a = statuses.substring(1).split("-");
			Filter stFilter = null;
			for(Integer i = 0; i < a.length; i++) {
				Integer st = Integer.parseInt(a[i]);
				System.out.println(st);
				stFilter = stFilter != null
					? stFilter.or(f.getStatusProperty().eq(st.byteValue()))
					: f.getStatusProperty().eq(st.byteValue());
			}
			if(stFilter != null) filter = filter.and(stFilter);
		}
		if(target != null && !"null".equals(target) && !"".equals(target)) {
			filter = filter.and(f.getTargetProperty().eq(Integer.parseInt(target)));
		}
		if(seller != null && !"null".equals(seller) && !"".equals(seller)) {
			filter = filter.and(f.getSellerProperty().eq(Integer.parseInt(seller)));
		}
		if(supplier != null && !"null".equals(supplier) && !"".equals(supplier)) {
			filter = filter.and(f.getSupplierProperty().eq(Integer.parseInt(supplier)));
		}
		if(project != null && !"null".equals(project) && !"".equals(project)) {
			filter = filter.and(f.getProjectProperty().eq(Integer.parseInt(project)));
		}
		if(type != null && !"null".equals(type) && !"".equals(type)) {
			Integer t = Integer.parseInt(type);
			filter = filter.and(f.getTypeProperty().eq(t.byteValue()));
		}
		if(series != null && !"null".equals(series) && !"".equals(series)) {
			filter = filter.and(f.getSeriesProperty().eq(series));
		}
		
		if(fromNumber != null && !"null".equals(fromNumber) && !"".equals(fromNumber)) {
			filter = filter.and(f.getNumberProperty().ge(Integer.parseInt(fromNumber)));
		}
		
		if(toNumber != null && !"null".equals(toNumber) && !"".equals(toNumber)) {
			filter = filter.and(f.getNumberProperty().le(Integer.parseInt(toNumber)));
		}
		
		if(fromDate != null && !"null".equals(fromDate) && !"".equals(fromDate)) {
			filter = filter.and(f.getIssueDateProperty().ge(AonDateUtils.simpleParse(fromDate)));
		}
		
		if(toDate != null && !"null".equals(toDate) && !"".equals(toDate)) {
			filter = filter.and(f.getIssueDateProperty().le(AonDateUtils.simpleParse(toDate)));
		}
		if(workplace != null && !"null".equals(workplace) && !"".equals(workplace)) {
			filter = filter.and(f.getWorkplaceProperty().eq(Integer.parseInt(workplace)));
		}
		if(scope != null && !"null".equals(scope) && !"".equals(scope)) {
			filter = filter.and(f.getScopeProperty().eq(Integer.parseInt(scope)));
		}
		if(confidential != null && !"null".equals(confidential) && !"".equals(confidential)) {
			Boolean c = Boolean.parseBoolean(confidential);
			filter = filter.and(f.getConfidentialProperty().eq((byte) (c ? 1 : 0)));
		}
		if(signed != null && !"null".equals(signed) && !"".equals(signed)) {
			Boolean s = Boolean.parseBoolean(signed);
			filter = filter.and(f.getSignedProperty().eq((byte) (s ? 1 : 0)));
		}
		
		return filter;
	}

	private Integer cont;
	private Double base;
	private Double quota;
	private Integer rid;
	private Row row;

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		System.out.println("GET METHOD");
		String domainName = req.getParameter("domain");
		Integer domainId = Integer.parseInt(req.getParameter("domain_id"));
		String userName = req.getParameter("username");
		
		Domain domain = AON.getDomain(domainName, domainId, userName, f->f.getNameProperty().eq(domainName));		
		
		
        HSSFWorkbook libro = new HSSFWorkbook();
        ByteArrayOutputStream archivo = new ByteArrayOutputStream();
        HSSFSheet hoja = libro.createSheet("Presupuestos");
     
        Row fila = hoja.createRow(0);
        
        fila.setHeightInPoints(16);
        CellStyle style = libro.createCellStyle();
        Font font = libro.createFont();
        font.setFontHeightInPoints((short)12);
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.MEDIUM); 
       	
        CellStyle style2 = libro.createCellStyle();
        Font font2 = libro.createFont();
        font.setFontHeightInPoints((short)12);
		style2.setFont(font2);
		style2.setAlignment(HorizontalAlignment.RIGHT);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderLeft(BorderStyle.THIN);
		
        CellStyle style3 = libro.createCellStyle();
		style3.setFont(font2);
		style3.setAlignment(HorizontalAlignment.LEFT);
		style3.setBorderBottom(BorderStyle.THIN);
		style3.setBorderRight(BorderStyle.THIN);
		style3.setBorderLeft(BorderStyle.THIN);
		style3.setBorderTop(BorderStyle.THIN);
		
		Cell c0 = fila.createCell(0);
    	c0.setCellValue("Fecha");
    	c0.setCellStyle(style);

    	Cell c1 = fila.createCell(1);
    	c1.setCellValue("Nº Presupuesto");
    	c1.setCellStyle(style);
    	
    	Cell c2 = fila.createCell(2);
    	c2.setCellValue("Cliente Potencial");
    	c2.setCellStyle(style);
    	
    	Cell c3 = fila.createCell(3);
    	c3.setCellValue("NIF");
    	c3.setCellStyle(style);
    	
    	Cell c4 = fila.createCell(4);
    	c4.setCellValue("Comercial");
    	c4.setCellStyle(style);
    	
    	Cell c5 = fila.createCell(5);
    	c5.setCellValue("Ámbito");
    	c5.setCellStyle(style);

    	Cell c6 = fila.createCell(6);
    	c6.setCellValue("Base");
    	c6.setCellStyle(style);
    	
    	Cell c7 = fila.createCell(7);
    	c7.setCellValue("Cuota");
    	c7.setCellStyle(style);
    	
    	Cell c8 = fila.createCell(8);
    	c8.setCellValue("Total");
    	c8.setCellStyle(style);
    	
    	Cell c9 = fila.createCell(9);
    	c9.setCellValue("Estado");
    	c9.setCellStyle(style);
    
    	Cell c10 = fila.createCell(10);
    	c10.setCellValue("Tipo");
    	c10.setCellStyle(style);
    	
    	Cell c11 = fila.createCell(11);
    	c11.setCellValue("Representado");
    	c11.setCellStyle(style);
    	
    	cont = 1;
    	base = 0.0;
    	quota = 0.0;
    	rid = -1;
    	
    	AON.getOfferDetails(domain.getName(), domain.getId(), userName, f ->offerFilter(req, f)).forEach(result -> {
    		Offer offer = result.getOffer();
    		Optional<Supplier> sup = null;
    		if(offer.getSupplier() != null && offer.getSupplier().getId() != null)
    			sup = AON.getSupplier(domain.getName(), domain.getId(), userName, f -> f.getIdProperty().eq(offer.getSupplier().getId()));
    		
    		if(!rid.equals(offer.getId())) {
    			rid = offer.getId();
    			if(row != null) {
    				Cell ct6 = row.createCell(6);
    				ct6.setCellValue(AonMathUtils.round(base));	
    				ct6.setCellStyle(style3);
 	    	
    				Cell ct7 = row.createCell(7);
    				ct7.setCellValue(AonMathUtils.round(quota));	
    				ct7.setCellStyle(style3);
    				
    				Cell ct8 = row.createCell(8);
    				ct8.setCellValue(AonMathUtils.round(base + quota));	
    				ct8.setCellStyle(style3);
    				base = 0.0;
    				quota = 0.0;
    			}
    			row = hoja.createRow(cont++);
    		
    			Cell ct0 = row.createCell(0);
    			ct0.setCellValue(AonDateUtils.simpleFormat(offer.getIssueDate())); 
    			ct0.setCellStyle(style3);
 	    	
    			Cell ct1 = row.createCell(1);
    			ct1.setCellValue(offer.getReferenceCode());
    			ct1.setCellStyle(style3);
 	    	
    			Cell ct2 = row.createCell(2);
    			ct2.setCellValue(offer.getTarget().getName());
    			ct2.setCellStyle(style3);
 	    	
    			Cell ct3 = row.createCell(3);
    			ct3.setCellValue(offer.getTarget().getDocument());
    			ct3.setCellStyle(style3);
 	    	
    			Cell ct4 = row.createCell(4);
    			ct4.setCellValue(offer.getSeller() != null ? offer.getSeller().getRegistryName() : "");	
    			ct4.setCellStyle(style3);
    			
    			Cell ct5 = row.createCell(5);
    			ct5.setCellValue(offer.getScope() != null ? offer.getScope().getDescription() : "");	
    			ct5.setCellStyle(style3);
 	    	
    			Cell ct9 = row.createCell(9);
    			ct9.setCellValue(offer.getStatus().getDescription());	
    			ct9.setCellStyle(style3);	    	
 	    	
    			Cell ct10 = row.createCell(10);
    			ct10.setCellValue(offer.getType().getDescription());	
    			ct10.setCellStyle(style3);
    			
    			Cell ct11 = row.createCell(11);
    			ct11.setCellValue(sup!= null ?sup.get().getName() : ""); 	
    			ct11.setCellStyle(style3);
    		} 
    		Double b = result.getPrice() * result.getQuantity();
    		Double discount = AonNumberUtils.toDouble(result.getDiscountExpression());
    		b = b - (b * (discount / 100));
    		
    		Double vat = result.getItem().getVat().getPercentage();
    		quota = quota + (b * (vat/100));
    		base = base + b;
    		
    	});
    	if(row != null) {
			Cell ct6 = row.createCell(6);
			ct6.setCellValue(AonMathUtils.round(base));	
			ct6.setCellStyle(style3);
 	
			Cell ct7 = row.createCell(7);
			ct7.setCellValue(AonMathUtils.round(quota));	
			ct7.setCellStyle(style3);
			
			Cell ct8 = row.createCell(8);
			ct8.setCellValue(AonMathUtils.round(base + quota));	
			ct8.setCellStyle(style3);
			base = 0.0;
			quota = 0.0;
			rid = -1;
			row = null;
		}

    	for(Integer i = 0; i < 24; i++){
    		hoja.autoSizeColumn(i);
    	}
     
        libro.write(archivo);  
        byte[] data = archivo.toByteArray();
        archivo.close();
        libro.close();

        Utils.giveBackData(resp, data , "presupuestos.xls");
    }
	
	public static Double round(Double value, Integer places) {
	    if (places < 0) throw new IllegalArgumentException();

	    Long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    Long tmp = Math.round(value);
	    return (double) tmp / factor;
	}

}

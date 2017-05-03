package com.esferalia.aon.gwt.finance.server;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.watson.util.AonStringUtils;

public class PurchaseOrderExcelAction extends AbsExcelAction implements Consumer<PurchaseDetail> {
	
    
    private List<String> tags; 
    private Map<Integer,String[]> productTags;
    
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public Map<Integer, String[]> getProductTags() {
		return productTags;
	}
	public void setProductTags(Map<Integer, String[]> productTags) {
		this.productTags = productTags;
	}

	@Override
	protected void headerRow() {
		this.tags = tags!=null&&tags.size()>0?tags:null;  
		this.productTags = productTags!=null&&productTags.size()>0?productTags:null;

	    row = sheet.createRow(rowCount++);
		cellCount = 0;

		Font orientedHeaderFont= workbook.createFont();
		orientedHeaderFont.setColor( IndexedColors.WHITE.index );

		XSSFCellStyle orientedHeaderCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		orientedHeaderCellStyle.setAlignment( HSSFCellStyle.ALIGN_CENTER );
		orientedHeaderCellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		orientedHeaderCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
		orientedHeaderCellStyle.setFillForegroundColor(AON_BLUE);
		orientedHeaderCellStyle.setRotation( (short) 90 );
		orientedHeaderCellStyle.setFont(orientedHeaderFont);

		for (int i = 0 ; i < row.getLastCellNum(); i ++) {
			sheet.autoSizeColumn(i);
		}
		
	    CellUtil.createCell(row, cellCount, "Tipo", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 10*256);

	    CellUtil.createCell(row, cellCount, "Estado", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 10*256);

		CellUtil.createCell(row, cellCount, "F. Emis.", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 11*256);

		CellUtil.createCell(row, cellCount, "Serie/Número", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 20*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Ln", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 4*256);		    
	    
	    CellUtil.createCell(row, cellCount, "T.D.", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 5*256);		    
	    
	    CellUtil.createCell(row, cellCount, "P.D.", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 5*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Nº Doc.", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 15*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Nombre o Razón Social", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 40*256);		    
	    
/*	    CellUtil.createCell(row, cellCount, "Localidad", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 30*256);		    
	    
	    CellUtil.createCell(row, cellCount, "C.P.", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 9*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Provincia", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 25*256);		    
*/	    
	    CellUtil.createCell(row, cellCount, "Producto", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 19*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Categoría", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 20*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Descripción", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 60*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Cantidad", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 10*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Precio", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 10*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Dtos.", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 10*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Ámbito", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 20*256);		    

	    CellUtil.createCell(row, cellCount, "Ctr. Trabajo", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 20*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Expediente", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 25*256);		    
	    
/*	    CellUtil.createCell(row, cellCount, "Ag. Comercial", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 20*256);
	*/    
	    if (tags != null) {
	    	int maxTagWidth = 0;
		    for (String tag : tags) {
		    	CellUtil.createCell(row, cellCount, tag , orientedHeaderCellStyle);	
			    sheet.setColumnWidth(cellCount++, 3*256);
			    maxTagWidth = (maxTagWidth > AonStringUtils.length(tag))?maxTagWidth:AonStringUtils.length(tag); 
		    }
		    row.setHeight( (short) (maxTagWidth * 130) );
	    }
	}
	
	@Override
	public void accept(PurchaseDetail detail) {
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		alignCenter(addCell(detail.getPurchase().getDocumentType().getName()));
		alignCenter(addCell(detail.getPurchase().getStatus().getName()));
		addCell(detail.getPurchase().getIssueDate());
		addCell((detail.getPurchase().getSeries() != null  ?  detail.getPurchase().getSeries() + "/" : "")
				+ detail.getPurchase().getNumber());
		addCell(detail.getLine().toString());
		alignCenter(addCell(detail.getPurchase().getSupplier2().getDocumentType() == null ? null : 
			detail.getPurchase().getSupplier2().getDocumentType().getDescription()));
		alignCenter(addCell(detail.getPurchase().getSupplier2().getDocumentCountry()));
		addCell( detail.getPurchase().getSupplier2().getDocument());
		addCell( detail.getPurchase().getSupplier2().getName());
		
/*
		addCell( detail.getOffer().getRegistryTown() );
		addCell( detail.getOffer().getRegistryZIP() );
		addCell( detail.getOffer().getRegistryProvince() );
	*/	 
		addCell(detail.getItem2()!= null ? detail.getItem2().getCode() : null);
		addCell(detail.getItem2()!= null ? detail.getItem2().getCategory() : null);
		addCell(AonStringUtils.abbreviate(detail.getDescription(), 60)) ;
		addCell(detail.getQuantity());
		addCell(detail.getPrice());
		addCell(detail.getDiscountExpression());
		addCell(detail.getPurchase().getScopeName());
		addCell(detail.getPurchase().getWorkplaceName());
		addCell(detail.getProjectName());
		//addCell(detail.getPurchase().getSeller()!=null?detail.getPurchase().getSeller().getRegistryName():null );

		Integer productId = detail.getProductId();
		if (tags != null && productTags != null && productId != null)  {
			String[] tagArray = productTags.get(productId);
			for (String tag : tags) {
				boolean exists = false;
				if (tagArray != null && tagArray.length > 0) {
					for (String t : tagArray) {
						if (AonStringUtils.equals(t,tag)) {
							exists = true;
							break;
						}
					}
				}
				alignCenter( addCell( exists?"X":"") );		
			}
		} 
	}
	
	
}

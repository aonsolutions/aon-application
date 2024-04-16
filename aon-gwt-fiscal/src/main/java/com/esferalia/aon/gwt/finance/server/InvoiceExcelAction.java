package com.esferalia.aon.gwt.finance.server;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceExcelAction extends AbsExcelAction implements Consumer<InvoiceDetail> {
	
    
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
		orientedHeaderCellStyle.setAlignment( HorizontalAlignment.CENTER );
		orientedHeaderCellStyle.setBorderBottom(BorderStyle.MEDIUM);
		orientedHeaderCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);  
		orientedHeaderCellStyle.setFillForegroundColor(AON_BLUE);
		orientedHeaderCellStyle.setRotation( (short) 90 );
		orientedHeaderCellStyle.setFont(orientedHeaderFont);

		for (int i = 0 ; i < row.getLastCellNum(); i ++) {
			sheet.autoSizeColumn(i);
		}
		
	    CellUtil.createCell(row, cellCount, "Tipo", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 10*256);

		CellUtil.createCell(row, cellCount, "F. Emis.", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 11*256);

		CellUtil.createCell(row, cellCount, "F. IVA", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 11*256);
	    
		CellUtil.createCell(row, cellCount, "Nº Factura", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 20*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Documento", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 15*256);		    
	    
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
	    
	    CellUtil.createCell(row, cellCount, "Localidad", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 30*256);		    
	    
	    CellUtil.createCell(row, cellCount, "C.P.", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 9*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Provincia", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 25*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Producto", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 19*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Categoría", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 20*256);		    

	    CellUtil.createCell(row, cellCount, "Marca", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 20*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Descripción", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 60*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Cantidad", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 10*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Precio", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 10*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Dtos.", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 10*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Importe", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 14*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Pr. Unit.", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 10*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Pr. Coste", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 10*256);
	    
	    CellUtil.createCell(row, cellCount, "Pr. Base", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 10*256);		    

	    CellUtil.createCell(row, cellCount, "Ámbito", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 20*256);		    

	    CellUtil.createCell(row, cellCount, "Ctr. Trabajo", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 20*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Expediente", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 25*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Ag. Comercial", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 25*256);
	    
	    CellUtil.createCell(row, cellCount, "Ag. Soporte", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 25*256);
	    
	    CellUtil.createCell(row, cellCount, "Segmento", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 40*256);
	    
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
	public void accept(InvoiceDetail detail) {
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		alignCenter( addCell( detail.getInvoice().getType().getDescription() ) );
		addCell( detail.getInvoice().getIssueDate() );
		addCell( detail.getInvoice().getTaxDate() );
		addCell( detail.getInvoice().getReferenceCode() );
		addCell( detail.getInvoice().getDocumentNumber() );
		addCell( detail.getLine() );
		alignCenter( addCell( detail.getInvoice().getRegistryDocumentType()==null?null:
			detail.getInvoice().getRegistryDocumentType().getDescription()));
		alignCenter( addCell( detail.getInvoice().getRegistryDocumentCountry() ));
		addCell( detail.getInvoice().getRegistryDocument() );
		addCell( detail.getInvoice().getRegistryName() );
		addCell( detail.getInvoice().getAddress().getCity());
		addCell( detail.getInvoice().getAddress().getZip());
		addCell( detail.getInvoice().getAddress().getProvince());
		 
		addCell( detail.getItem()!= null ? detail.getItem().getProduct().getCode() : null );
		addCell( detail.getItem()!= null ? detail.getItem().getProduct().getCategory().getName() : null );
		addCell( detail.getItem()!= null ? detail.getItem().getProduct().getBrand().getName() : null );
		addCell( AonStringUtils.abbreviate(detail.getDescription(), 60) ) ;
		addCell( detail.getQuantity() );
		addCell( detail.getPrice() );
		addCell( detail.getDiscount());
		addCell( detail.getTaxableBase() );
		addCell( detail.getQuantity()==0.0
				?0.0
				:AonMathUtils.round( detail.getTaxableBase() / detail.getQuantity()) );

		addCell( detail.getItem()!= null ? detail.getItem().getPurchasePrice()  : null );
		addCell( detail.getItem()!= null ? detail.getItem().getPrice()  : null );
		
		addCell( detail.getInvoice().getScope().getDescription());
		addCell( detail.getWorkPlaceName() );
		addCell( detail.getProjectName() );
		addCell( detail.getSeller()!=null? detail.getSeller().getName() : null );
		addCell( detail.getSellerSupport()!=null? detail.getSellerSupport() : null );
		
		String segments = "";
		if(null != detail.getSegments()) {
			for (String segment : detail.getSegments()) { segments += segment + ", "; }
	        // Remove the trailing comma and space
			if(AonStringUtils.isNotBlank(segments)) segments = segments.substring(0, segments.length() - 2);
		}
		addCell(segments);
		
		Integer productId = detail.getItem()!= null 
				? detail.getItem().getProduct().getId() : null;
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

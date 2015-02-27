package com.esferalia.aon.gwt.finance.server;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Invoice Report (excel)", urlPatterns = { "/aon_gwt_fiscal/InvoiceReport" })
public class InvoiceReportServlet extends HttpServlet {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		AONContext ctx = null;
		try {
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			
			String fromDateParam = req.getParameter("fromDate");
			Date fromDate = null;
			if (AonStringUtils.isNotBlank(fromDateParam)){
				fromDate = DATE_FORMAT.parse(fromDateParam);
			}
			
			Date toDate = null;
			String toDateParam = req.getParameter("toDate");
			if (AonStringUtils.isNotBlank(toDateParam)){
				toDate = DATE_FORMAT.parse(toDateParam);
			}
			
					
			ctx = AONContext.getAONContext(domainName, domainId);
			
			InvoiceFilter filter = new InvoiceFilter();
			filter.setPurchasesEnabled( AonStringUtils.equals("on",req.getParameter("purchases")));
			filter.setSalesEnabled( AonStringUtils.equals("on",req.getParameter("sales")));
			filter.setExpensesEnabled( AonStringUtils.equals("on",req.getParameter("expenses")));
			filter.setUndeductibleExpensesEnabled( AonStringUtils.equals("on",req.getParameter("undeductibleExpenses")));
			filter.setDomain(domainId);
			filter.setFromDate(fromDate);
			filter.setToDate(toDate);
			

			ExcelAction action = new ExcelAction();
			List<String> tags = ProductDAO.getProductTags(ctx);
			Map<Integer,String[]> productTags = null;
			if (tags != null && tags.size() > 0) {
				productTags = ProductDAO.getProductTagMap(ctx);	
			}
			action.initialize("FACTURAS",tags,productTags);
			InvoiceDAO.getInvoiceDetails(ctx, filter, action);
			
			String fileName = "Facturas";
			resp.setContentType(MimeType.MIME_MS_EXCEL.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\""
					+ fileName + ".xslx\";");
			action.finalize(resp.getOutputStream());
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		} finally {
			if (ctx != null) ctx.finalize();
		}
	}

	public static class ExcelAction implements Consumer<InvoiceDetail> {
		private static final String DATE_PATTERN = "dd/MM/yyyy";
		private static final String DECIMAL_PATTERN = "#,##0.00";
		private static final String NUMBER_PATTERN = "#,###";
		
		

		private SXSSFWorkbook workbook;
	    private SXSSFSheet sheet;
	    private Row row;
	    private int rowCount;
	    private int cellCount;
	    private DataFormat dataFormat;
	    private CellStyle dateStyle;
	    private CellStyle decimalStyle;
	    private CellStyle numberStyle;
	    private CellStyle centerCellStyle;
	    
	    private List<String> tags; 
	    private Map<Integer,String[]> productTags;

		public void initialize(String name,List<String> tags , Map<Integer,String[]> productTags) {
			this.tags = tags!=null&&tags.size()>0?tags:null;  
			this.productTags = productTags!=null&&productTags.size()>0?productTags:null;
			
			workbook = new SXSSFWorkbook(1);
			
		    sheet = (SXSSFSheet) workbook.createSheet(name);
		    dataFormat = workbook.getCreationHelper().createDataFormat();
		    rowCount = 0;
		    cellCount = 0;
		    dateStyle = workbook.createCellStyle();
		    dateStyle.setDataFormat(dataFormat.getFormat(DATE_PATTERN));
		    dateStyle.setAlignment( HSSFCellStyle.ALIGN_CENTER );
		    
		    numberStyle = workbook.createCellStyle();
		    numberStyle.setDataFormat(dataFormat.getFormat(NUMBER_PATTERN));
		    numberStyle.setAlignment( HSSFCellStyle.ALIGN_CENTER );
		     
		    decimalStyle = workbook.createCellStyle();
		    decimalStyle.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
		    numberStyle.setAlignment( HSSFCellStyle.ALIGN_RIGHT );
		    
		    row = sheet.createRow(rowCount++);
			cellCount = 0;
			
			

			centerCellStyle = workbook.createCellStyle();
			centerCellStyle.setAlignment( HSSFCellStyle.ALIGN_CENTER );
			
			XSSFColor aonBlue = new XSSFColor(new java.awt.Color(0,114,207));

			Font headerFont= workbook.createFont();
			headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
			headerFont.setColor( IndexedColors.WHITE.index );

			XSSFCellStyle headerCellStyle = (XSSFCellStyle) workbook.createCellStyle();
			headerCellStyle.setAlignment( HSSFCellStyle.ALIGN_CENTER );
			headerCellStyle.setVerticalAlignment( HSSFCellStyle.VERTICAL_CENTER);
		    headerCellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		    headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		    headerCellStyle.setFillForegroundColor(aonBlue);
		    headerCellStyle.setFont(headerFont);
		    

			Font orientedHeaderFont= workbook.createFont();
			orientedHeaderFont.setColor( IndexedColors.WHITE.index );

			XSSFCellStyle orientedHeaderCellStyle = (XSSFCellStyle) workbook.createCellStyle();
			orientedHeaderCellStyle.setAlignment( HSSFCellStyle.ALIGN_CENTER );
			orientedHeaderCellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
			orientedHeaderCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
			orientedHeaderCellStyle.setFillForegroundColor(aonBlue);
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
		    
		    CellUtil.createCell(row, cellCount, "Pr. Neto", headerCellStyle);
		    sheet.setColumnWidth(cellCount++, 10*256);		    
		    
		    CellUtil.createCell(row, cellCount, "Ctr. Trabajo", headerCellStyle);
		    sheet.setColumnWidth(cellCount++, 20*256);		    
		    
		    CellUtil.createCell(row, cellCount, "Expediente", headerCellStyle);
		    sheet.setColumnWidth(cellCount++, 25*256);		    
		    
		    CellUtil.createCell(row, cellCount, "Ag. Comercial", headerCellStyle);
		    sheet.setColumnWidth(cellCount++, 20*256);
		    
		    
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
			addCell( detail.getInvoice().getRegistryTown() );
			addCell( detail.getInvoice().getRegistryZIP() );
			addCell( detail.getInvoice().getRegistryProvince() );
			 
			addCell( detail.getItem()!= null ? detail.getItem().getCode() : null );
			addCell( detail.getItem()!= null ? detail.getItem().getCategory()  : null );
			addCell( AonStringUtils.abbreviate(detail.getDescription(), 60) ) ;
			addCell( detail.getQuantity() );
			addCell( detail.getPrice() );
			addCell( detail.getDiscountExpression() );
			addCell( detail.getTaxableBase() );
			addCell( detail.getQuantity()==0.0
					?0.0
					:AonMathUtils.round( detail.getTaxableBase() / detail.getQuantity()) );
			addCell( detail.getWorkPlace() );
			addCell( detail.getProject() );
			addCell( detail.getSeller()!=null?detail.getSeller().getRegistryName():null );
			
			Integer productId = detail.getItem()!= null 
					? detail.getItem().getProductId() : null;
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

//		        ¿precio neto (Base Imponible)?
//		        ¿Tipo IVA/Importe con IVA?
//		        etiquetas (1:n)
			
		}
		
		private Cell alignCenter(Cell cell) {
			cell.setCellStyle( centerCellStyle );
			return cell;
		}

		private Cell addCell(String value) {
			Cell cell = row.createCell(cellCount++);
			cell.setCellValue(AonStringUtils.trimToEmpty( value ) );
			cell.setCellType(Cell.CELL_TYPE_STRING);
			return cell;
		}

		private Cell addCell(Enum<?> value) {
			Cell cell = row.createCell(cellCount++);
			if ( value != null ) {
				cell.setCellValue(value.toString());
			}
			cell.setCellType(Cell.CELL_TYPE_STRING);
			return cell;
		}

		private Cell addCell(Short value) {
			Cell cell = row.createCell(cellCount++);
			cell.setCellStyle(numberStyle);
			if ( value != null ) {
				cell.setCellValue(value);
			}
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			return cell;
		}
		
		private Cell addCell(Date value) {
			Cell cell = row.createCell(cellCount++);
			cell.setCellStyle(dateStyle);
			if ( value != null ) {
				cell.setCellValue(value);
			}
			return cell;
		}

		private Cell addCell(Double number) {
			Cell cell = row.createCell(cellCount++);
			cell.setCellStyle(decimalStyle);
			cell.setCellValue(number);
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			return cell;
		}

		public void finalize(OutputStream out) throws IOException {
			workbook.write(out);
			
			// Note that SXSSF allocates temporary files that you 
			// must always clean up explicitly, by calling the dispose method.
			//
			// http://poi.apache.org/spreadsheet/how-to.html#sxssf
			//
			workbook.dispose();
		}
		
	}

}

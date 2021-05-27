package com.code.aon.ui.finance.controller;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.util.TempFile;
import org.apache.poi.util.TempFileCreationStrategy;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.impl.DSL;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.SqlRenderer;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Order;
import com.code.aon.ql.ast.IdentExpression;
import com.code.aon.ql.ast.impl.IdentExpressionImpl;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceExcelReport {

	protected static class ExcelSqlRenderer extends SqlRenderer {

		public ExcelSqlRenderer(Writer out) {
			super(out);
		}

		@Override
		public void visitIdentExpression(IdentExpression expression) {
			expression = transform(expression);
			super.visitIdentExpression(expression);
		}
		private static IdentExpression transform(IdentExpression expression) {
			String name = expression.getName();
			name = AonStringUtils.replace(name , "<", ".");
			if (AonStringUtils.countMatches(name, ".") > 2) {
				throw new IllegalArgumentException("[" + name + "] not accepted.");
			}
			if (AonStringUtils.countMatches(name, ".") == 2) {
				String[] tokens = AonStringUtils.split(name, '.');
				if (!"id".equals(tokens[tokens.length - 1])) {
					throw new IllegalArgumentException("[" + name + "] not accepted.");
				}
				expression = new IdentExpressionImpl(AonStringUtils.substringBeforeLast(name, ".id"));
			}
			expression = new IdentExpressionImpl(addAndDeHump(expression.getName()));
			if (AonStringUtils.countMatches(expression.getName(), ".") > 1) {
				throw new IllegalArgumentException("[" + expression.getName() + "] not accepted.");
			}
			if (!AonStringUtils.equalsIgnoreCase("Invoice", AonStringUtils.substringBeforeLast(expression.getName(), "."))) {
				throw new IllegalArgumentException("[" + name + "] not accepted.");
			}
			return expression;
		}
		private static String addAndDeHump(String view) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < view.length(); i++) {
				if ((i != 0) && Character.isUpperCase(view.charAt(i))) {
					sb.append('_');
				}
				sb.append(view.charAt(i));
			}
			return sb.toString().trim().toLowerCase();
		}
	}


	public void run(Criteria criteria) throws ManagerBeanException {
		StringWriter whereWriter = new StringWriter();
		ExcelSqlRenderer esr = new ExcelSqlRenderer(whereWriter);
		try {
			criteria.accept(esr);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new ManagerBeanException("Sólo se puede ejecutar el listado excel a partir de "
					+ "filtros en la factura, no en su detalle, titular ni vencimientos.");
		}
		String where = AonStringUtils.substringBefore(whereWriter.toString(), "ORDER BY");
		List<OrderField<Object>> orderList = new LinkedList<OrderField<Object>>();
		if (criteria.getOrderByList() != null) {
			List<Order> orders = criteria.getOrderByList().getOrders();
			for (Order order :orders) {
				String field = ExcelSqlRenderer.transform(order.getExpression()).getName();
				Field<Object> f = DSL.field(field);
				orderList.add( order.isAscending()?f.asc():f.desc() );
			}
		}
		
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), AonUtil.getRemoteUser() );
			ExcelAction action = new ExcelAction();
			action.initialize("Listado Facturas");
			ctx.getDslContext()
				.select( INVOICE.ISSUE_DATE,
						INVOICE.SERIES,
						INVOICE.NUMBER,
						INVOICE.REFERENCE_CODE,
						INVOICE.RDOCUMENT,
						INVOICE.RNAME,
						INVOICE.TAXABLE_BASE,
						INVOICE.VAT_QUOTA,
						INVOICE.RETENTION_QUOTA,
						INVOICE.TOTAL,
						INVOICE.COMMENTS,
						INVOICE.REMARKS
						)
				.from(INVOICE )
				.where(DSL.condition(where))
				.orderBy(orderList)
				.fetch()
				.stream()
				.map( rec -> new InvoicePojo()
						.setIssueDate(rec.get(INVOICE.ISSUE_DATE))
						.setSeries(rec.get(INVOICE.SERIES))
						.setNumber(rec.get(INVOICE.NUMBER))
						.setReferenceCode(rec.get(INVOICE.REFERENCE_CODE))
						.setRegistryDocument(rec.get(INVOICE.RDOCUMENT))
						.setRegistryName(rec.get(INVOICE.RNAME))
						.setTaxableBase(rec.get(INVOICE.TAXABLE_BASE))
						.setVatQuota(rec.get(INVOICE.VAT_QUOTA))
						.setRetentionQuota(rec.get(INVOICE.RETENTION_QUOTA))
						.setTotal(rec.get(INVOICE.TOTAL))
						.setComments(rec.get(INVOICE.COMMENTS))
						.setRemarks(rec.get(INVOICE.REMARKS))
					)
				.forEach(action);
			HttpServletResponse resp = DownloadUtil.getResponse();
			resp.setContentType(MimeType.MS_EXCEL.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"Listado Facturas."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
			action.finalize(resp.getOutputStream());
			resp.flushBuffer();
			FacesContext context = FacesContext.getCurrentInstance();
	        context.responseComplete();    			
		} catch (IOException e) {
			throw new ManagerBeanException("Error al ejecutar el listado[" + e.getMessage() + "]");
		} finally {
			if (ctx != null)
				ctx.close();
		}
		
	}
	private static class InvoicePojo {
		private Date issueDate;
		private String series;
		private Integer number;
		private String referenceCode;
		private String registryDocument;
		private String registryName;
		private Double taxableBase;
		private Double vatQuota;
		private Double retentionQuota;
		private Double total;
		private String comments;
		private String remarks;
		
		public Date getIssueDate() {
			return issueDate;
		}
		public InvoicePojo setIssueDate(Date issueDate) {
			this.issueDate = issueDate;
			return this;
		}
		public String getSeries() {
			return series;
		}
		public InvoicePojo setSeries(String series) {
			this.series = series;
			return this;
		}
		public Integer getNumber() {
			return number;
		}
		public InvoicePojo setNumber(Integer number) {
			this.number = number;
			return this;
		}
		public String getReferenceCode() {
			return referenceCode;
		}
		public InvoicePojo setReferenceCode(String referenceCode) {
			this.referenceCode = referenceCode;
			return this;
		}
		public String getRegistryDocument() {
			return registryDocument;
		}
		public InvoicePojo setRegistryDocument(String registryDocument) {
			this.registryDocument = registryDocument;
			return this;
		}
		public String getRegistryName() {
			return registryName;
		}
		public InvoicePojo setRegistryName(String registryName) {
			this.registryName = registryName;
			return this;
		}
		public Double getTaxableBase() {
			return taxableBase;
		}
		public InvoicePojo setTaxableBase(Double taxableBase) {
			this.taxableBase = taxableBase;
			return this;
		}
		public Double getVatQuota() {
			return vatQuota;
		}
		public InvoicePojo setVatQuota(Double vatQuota) {
			this.vatQuota = vatQuota;
			return this;
		}
		public Double getRetentionQuota() {
			return retentionQuota;
		}
		public InvoicePojo setRetentionQuota(Double retentionQuota) {
			this.retentionQuota = retentionQuota;
			return this;
		}
		public Double getTotal() {
			return total;
		}
		public InvoicePojo setTotal(Double total) {
			this.total = total;
			return this;
		}
		public String getComments() {
			return comments;
		}
		public InvoicePojo setComments(String comments) {
			this.comments = comments;
			return this;
		}
		public String getRemarks() {
			return remarks;
		}
		public InvoicePojo setRemarks(String remarks) {
			this.remarks = remarks;
			return this;
		}
	}

	private static class ExcelAction implements Consumer<InvoicePojo>{
		protected static final String DATE_PATTERN = "dd/MM/yyyy";
		protected static final String DECIMAL_PATTERN = "#,##0.00";
		protected static final String NUMBER_PATTERN = "#,###";
		protected static final XSSFColor AON_BLUE = new XSSFColor(new java.awt.Color(0,114,207));
		protected static final XSSFColor AON_LIGHT_GRAY = new XSSFColor(new java.awt.Color(240,240,240));
		
		protected SXSSFWorkbook workbook;
		protected SXSSFSheet sheet;
		protected Row row;
		protected int rowCount;
		protected int cellCount;
		protected DataFormat dataFormat;
		protected CellStyle dateStyle;
		protected CellStyle smallDateStyle;
		protected CellStyle decimalStyle;
		protected CellStyle numberStyle;
		protected CellStyle centerCellStyle;
		protected XSSFCellStyle headerCellStyle;
		protected XSSFCellStyle rowStyle; 
		protected Font boldFont;
		protected Font defaulFont;	
		protected Font smallFont;
		protected Font smallBoldFont;
		
		private XSSFCellStyle titleCellStyle;
		private CellStyle wrappedCellStyle;
		private CellStyle defaultStyle;
		private XSSFCellStyle titleStyle;
		
		private int columns;
		
		public void initialize(String name) {
			workbook = new SXSSFWorkbook(1);
			
			
		    sheet = (SXSSFSheet) workbook.createSheet(name);
		    dataFormat = workbook.getCreationHelper().createDataFormat();
		    rowCount = 0;
		    cellCount = 0;
		    dateStyle = workbook.createCellStyle();
		    dateStyle.setDataFormat(dataFormat.getFormat(DATE_PATTERN));
		    dateStyle.setAlignment( HorizontalAlignment.CENTER );
		    dateStyle.setVerticalAlignment(VerticalAlignment.TOP );
		    
		    numberStyle = workbook.createCellStyle();
		    numberStyle.setDataFormat(dataFormat.getFormat(NUMBER_PATTERN));
		    numberStyle.setAlignment( HorizontalAlignment.CENTER );
		    numberStyle.setVerticalAlignment(VerticalAlignment.TOP );
		     
		    decimalStyle = workbook.createCellStyle();
		    decimalStyle.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
		    decimalStyle.setAlignment( HorizontalAlignment.RIGHT );
		    decimalStyle.setVerticalAlignment(VerticalAlignment.TOP );
		    
			centerCellStyle = workbook.createCellStyle();
			centerCellStyle.setAlignment( HorizontalAlignment.CENTER );
			centerCellStyle.setVerticalAlignment(VerticalAlignment.TOP );

			defaulFont= workbook.createFont();
			defaulFont.setFontHeightInPoints((short) 9);
			
			smallFont = workbook.createFont();
			smallFont.setFontHeightInPoints((short) 8);

			smallBoldFont = workbook.createFont();
			smallBoldFont.setFontHeightInPoints((short) 8);
			smallBoldFont.setBold(true);

			smallDateStyle = workbook.createCellStyle();
		    smallDateStyle.setDataFormat(dataFormat.getFormat(DATE_PATTERN));
		    smallDateStyle.setAlignment( HorizontalAlignment.CENTER );
		    smallDateStyle.setFont( smallFont );
		    smallDateStyle.setVerticalAlignment(VerticalAlignment.TOP );

		    boldFont= workbook.createFont();
			boldFont.setFontHeightInPoints((short) 9);
			boldFont.setBold(true);

			Font headerFont= workbook.createFont();
			headerFont.setBold(true);
			headerFont.setColor( IndexedColors.WHITE.index );

			headerCellStyle = (XSSFCellStyle) workbook.createCellStyle();
			headerCellStyle.setAlignment( HorizontalAlignment.CENTER );
			headerCellStyle.setVerticalAlignment( VerticalAlignment.CENTER);
		    headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
		    headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    headerCellStyle.setFillForegroundColor(AON_BLUE);
		    headerCellStyle.setFont(headerFont);
		    
		    rowStyle = (XSSFCellStyle) workbook.createCellStyle();
		    rowStyle.setWrapText(true);
		    rowStyle.setVerticalAlignment(VerticalAlignment.TOP);

		    headerRow();
		}
		
//		protected Cell alignCenter(Cell cell) {
//			cell.setCellStyle( centerCellStyle );
//			return cell;
//		}

		protected Cell addCell(String value) {
			Cell cell = row.createCell(cellCount++);
			cell.setCellValue(AonStringUtils.trimToEmpty( value ) );
			cell.setCellType(CellType.STRING);
			return cell;
		}

//		protected Cell addCell(Enum<?> value) {
//			Cell cell = row.createCell(cellCount++);
//			if ( value != null ) {
//				cell.setCellValue(value.toString());
//			}
//			cell.setCellType(CellType.STRING);
//			return cell;
//		}

//		protected Cell addCell(Short value) {
//			Cell cell = row.createCell(cellCount++);
//			cell.setCellStyle(numberStyle);
//			if ( value != null ) {
//				cell.setCellValue(value);
//			}
//			cell.setCellType(CellType.NUMERIC);
//			return cell;
//		}
		
//		protected Cell addCell(Integer value) {
//			Cell cell = row.createCell(cellCount++);
//			cell.setCellStyle(numberStyle);
//			if ( value != null ) {
//				cell.setCellValue(value);
//			}
//			cell.setCellType(CellType.NUMERIC);
//			return cell;
//		}

		protected Cell addCell(Date value) {
			Cell cell = row.createCell(cellCount++);
			cell.setCellStyle(dateStyle);
			if ( value != null ) {
				cell.setCellValue(value);
			}
			return cell;
		}

		protected Cell addCell(Double number) {
			Cell cell = row.createCell(cellCount++);
			cell.setCellStyle(decimalStyle);
			cell.setCellValue(number!=null?number:0.0);
			cell.setCellType(CellType.NUMERIC);
			return cell;
		}

		protected void headerRow() {
			columns = 9;
			
			sheet.setDisplayZeros(false);
			
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape( true );
			sheet.setMargin(Sheet.TopMargin, 0.3 );
			sheet.setMargin(Sheet.LeftMargin, 0.3 );
			sheet.setMargin(Sheet.RightMargin, 0.3 );
			Footer footer = sheet.getFooter();
			footer.setLeft("Generado el &D");
			footer.setRight("P\u00E1g: &P/&N");
			
			defaultStyle = workbook.createCellStyle();
			defaultStyle.setFont(smallFont);
			
			wrappedCellStyle = workbook.createCellStyle();
			wrappedCellStyle.cloneStyleFrom(defaultStyle);
			wrappedCellStyle.setWrapText(true);
			
			decimalStyle.setFont(smallFont);

			Font journalHeaderFont = workbook.createFont();
			journalHeaderFont.setBold(true);
			journalHeaderFont.setFontHeightInPoints((short) 8);
			
			XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
			headerStyle.setAlignment( HorizontalAlignment.CENTER );
			headerStyle.setVerticalAlignment( VerticalAlignment.CENTER);
		    headerStyle.setFont(journalHeaderFont);
		    
		    XSSFCellStyle columnHeaderStyle = (XSSFCellStyle) workbook.createCellStyle();
		    columnHeaderStyle.cloneStyleFrom(headerStyle);
		    columnHeaderStyle.setBorderTop(BorderStyle.THIN);
		    columnHeaderStyle.setBorderBottom(BorderStyle.THIN);
		    columnHeaderStyle.setBorderLeft(BorderStyle.THIN);
		    columnHeaderStyle.setBorderRight(BorderStyle.THIN);
		    columnHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    columnHeaderStyle.setFillForegroundColor(AON_LIGHT_GRAY);
		    
			Font titleFont= workbook.createFont();
			titleFont.setFontHeightInPoints((short) 10);
			titleFont.setBold(true);

			titleStyle = (XSSFCellStyle) workbook.createCellStyle();
		    titleStyle.cloneStyleFrom(headerStyle);
		    titleStyle.setFont(titleFont);
		    titleStyle.setBorderBottom(BorderStyle.NONE);
		    
			Font subTitleFont= workbook.createFont();
			subTitleFont.setFontHeightInPoints((short) 8);
			subTitleFont.setBold(true);
			
			XSSFCellStyle subTitleStyle = (XSSFCellStyle) workbook.createCellStyle();
			subTitleStyle.cloneStyleFrom(headerStyle);
			subTitleStyle.setFont(subTitleFont);
			subTitleStyle.setBorderBottom(BorderStyle.NONE);

			Font smallBoldFont= workbook.createFont();
			smallBoldFont.setFontHeightInPoints((short) 8);
			smallBoldFont.setBold(true);

			titleCellStyle = (XSSFCellStyle) workbook.createCellStyle();
			titleCellStyle.cloneStyleFrom(decimalStyle);
			titleCellStyle.setAlignment(HorizontalAlignment.RIGHT);
			titleCellStyle.setFont(smallBoldFont);
			titleCellStyle.setWrapText(true);
			
			// ----------------------------------------------- ROW 7 - Column Header
			row = sheet.createRow(rowCount);
			cellCount = 0;

			sheet.setColumnWidth(cellCount, 9 * 256);
			CellUtil.createCell(row, cellCount, "FECHA", columnHeaderStyle);
			
			cellCount++;

			sheet.setColumnWidth(cellCount, 25 * 256);
			CellUtil.createCell(row, cellCount, "Nª DOCUMENTO", columnHeaderStyle);
			cellCount++;
			
			sheet.setColumnWidth(cellCount, 12 * 256);
			CellUtil.createCell(row, cellCount, "NIF", columnHeaderStyle);
			cellCount++;
			
			sheet.setColumnWidth(cellCount, 40 * 256);
			CellUtil.createCell(row, cellCount, "TITULAR", columnHeaderStyle);
			cellCount++;
			
			sheet.setColumnWidth(cellCount, 8 * 256);
			CellUtil.createCell(row, cellCount, "BASE IMP.", columnHeaderStyle);
			cellCount++;
			
			sheet.setColumnWidth(cellCount, 8 * 256);
			CellUtil.createCell(row, cellCount, "IVA", columnHeaderStyle);
			cellCount++;
			
			sheet.setColumnWidth(cellCount, 8 * 256);
			CellUtil.createCell(row, cellCount, "IRPF", columnHeaderStyle);
			cellCount++;
			
			sheet.setColumnWidth(cellCount, 8 * 256);
			CellUtil.createCell(row, cellCount, "TOTAL", columnHeaderStyle);
			cellCount++;
			
			sheet.setColumnWidth(cellCount, 50 * 256);
			CellUtil.createCell(row, cellCount, "OBSERVACIONES", columnHeaderStyle);
			cellCount++;

			sheet.setColumnWidth(cellCount, 50 * 256);
			CellUtil.createCell(row, cellCount, "COMENTARIOS", columnHeaderStyle);
			cellCount++;

			++rowCount;
			sheet.setRepeatingRows(new CellRangeAddress(0, rowCount - 1 , 0, columns-1));
		}

		@Override
		public void accept(InvoicePojo st) {
			row = sheet.createRow(rowCount++);
			row.setRowStyle(rowStyle);
			cellCount = 0;
			addCell(st.getIssueDate());
			addCell(st.getReferenceCode());
			addCell(st.getRegistryDocument());
			addCell(st.getRegistryName());
			addCell(st.getTaxableBase());
			addCell(st.getVatQuota());
			addCell(st.getRetentionQuota());
			addCell(st.getTotal());
			Cell cell = addCell(st.getRemarks());
			cell.setCellStyle(rowStyle);
			cell = addCell(st.getComments());
			cell.setCellStyle(rowStyle);
			String l = (AonStringUtils.length(st.getRemarks()) > AonStringUtils.length(st.getComments()))
				?st.getRemarks()
				:st.getComments();
			if (AonStringUtils.length(l) > 65) {
				double ll = ((double)AonStringUtils.length(l)) / 65.0;
				int lines = (int) AonMathUtils.ceil( ll, 0 );
				lines = lines + AonStringUtils.countMatches(l,'\n');
				row.setHeight((short) (lines * 256));
			}
			try {
				if (rowCount % 100 == 0) sheet.flushRows();
			} catch (IOException e) {
				throw new AonCoreException( e );
			}
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
	
	private static class AONTempFileCreationStrategy implements TempFileCreationStrategy {

        /** The directory where the temporary files will be created (<code>null</code> to use the default directory). */
        private File dir;

        @Override
		public File createTempFile(String prefix, String suffix) throws IOException {
            // Identify and create our temp dir, if needed
        	
            if (dir == null || !dir.canWrite()) {
                dir = new File(System.getProperty("java.io.tmpdir"), "poifiles");
                dir.mkdir();
                if (System.getProperty("poi.keep.tmp.files") == null)
                    dir.deleteOnExit();
            }

            // Generate a unique new filename 
            File newFile = File.createTempFile(prefix, suffix, dir);

            // Set the delete on exit flag, unless explicitly disabled
            if (System.getProperty("poi.keep.tmp.files") == null)
                newFile.deleteOnExit();

            // All done
            return newFile;
		}
        
        @Override
        public File createTempDirectory(String prefix) throws IOException {
        	// TODO Auto-generated method stub
            File dir = new File(System.getProperty("java.io.tmpdir"), prefix);
            dir.mkdir();
            if (System.getProperty("poi.keep.tmp.files") == null)
                dir.deleteOnExit();
            return dir;
        }
		
	}
	
	static {
		TempFile.setTempFileCreationStrategy(new AONTempFileCreationStrategy());
	}
}

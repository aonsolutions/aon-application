package old.com.esferalia.aon.gwt.fiscal.test.accounting;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AyudatCounters {
	
	private AyudatCounters() {
	}

	public static void main(String[] args) throws IOException {
		Matrix matrix = new Matrix();
		String[] files = new String[] { 
			 "/home/ecastellano/TRABAJO/SELECTS/invoice_ayudat.txt"
			,"/home/ecastellano/TRABAJO/SELECTS/invoice_grupo.txt"
			,"/home/ecastellano/TRABAJO/SELECTS/invoice_pro.txt"
			,"/home/ecastellano/TRABAJO/SELECTS/salary_ayudat.txt"
			,"/home/ecastellano/TRABAJO/SELECTS/salary_grupo.txt"
			,"/home/ecastellano/TRABAJO/SELECTS/salary_pro.txt"
		};
		for (String f :files ) {
			readFile(f, matrix);
		}
		
		printMatrix( matrix );
	}


	private static void readFile(String f, Matrix matrix) throws IOException {
		boolean salary = AonStringUtils.contains(f, "salary"); 
		String schema = AonStringUtils.substringBefore(f, ".");
		schema = AonStringUtils.substringAfter(schema, "_");
		try (FileInputStream fis = new FileInputStream(f)) {
			InputStreamReader ireader = new InputStreamReader(fis);
			LineNumberReader reader = new LineNumberReader(ireader);
			while ( reader.ready()) {
				String line = reader.readLine();
				if ( !AonStringUtils.startsWith( line, "id" ) ) {
					matrix.add( parseData( schema, salary, line) );
				}
			}
		}
	}

	private static Data parseData(String schema, boolean salary, String line) {
		String[] tokens =  AonStringUtils.splitPreserveAllTokens(line, '\t' );
		for (int i = 0; i < tokens.length ; i++) {
			tokens[i] = AonStringUtils.trimToNull(tokens[i]);
			if (AonStringUtils.equals(tokens[i],"NULL")) {
				tokens[i] = null;
			}
		}
		
		return new Data()
			.setSchema( schema )
			.setSalary( salary )
			.setParent( AonNumberUtils.toInteger( tokens[0]) )
			.setParentName( tokens[1] )
			.setParentDescription( tokens[2] )	
			.setDomain( AonNumberUtils.toInteger( tokens[3]) )
			.setDomainName( tokens[4] )
			.setDomainDescription( tokens[5] )
			.setMonth( AonNumberUtils.toInteger( tokens[6]) )	
			.setCount( AonNumberUtils.toInteger( tokens[7]) )
		;
	}
	
	private static class Data {
		private String schema;
		private boolean salary;
		private Integer parent;	
		private String parentName;	
		private String parentDescription;	
		private Integer domain;
		private String domainName;
		private String domainDescription;	
		private Integer month;	
		private Integer count;
		
		public String getSchema() {
			return schema;
		}
		public Data setSchema(String schema) {
			this.schema = schema;
			return this;
		}
		
		public boolean isSalary() {
			return salary;
		}
		public Data setSalary(boolean salary) {
			this.salary = salary;
			return this;
		}
		
		public Integer getParent() {
			return parent;
		}
		public Data setParent(Integer parent) {
			this.parent = parent;
			return this;
		}
		
		public String getParentName() {
			return parentName;
		}
		public Data setParentName(String parentName) {
			this.parentName = parentName;
			return this;
		}

		public String getParentDescription() {
			return parentDescription;
		}
		public Data setParentDescription(String parentDescription) {
			this.parentDescription = parentDescription;
			return this;
		}

		public Integer getDomain() {
			return domain;
		}
		public Data setDomain(Integer domain) {
			this.domain = domain;
			return this;
		}

		public String getDomainName() {
			return domainName;
		}
		public Data setDomainName(String domainName) {
			this.domainName = domainName;
			return this;
		}

		public String getDomainDescription() {
			return domainDescription;
		}
		public Data setDomainDescription(String domainDescription) {
			this.domainDescription = domainDescription;
			return this;
		}

		public Integer getMonth() {
			return month;
		}
		public Data setMonth(Integer month) {
			this.month = month;
			return this;
		}

		public Integer getCount() {
			return count;
		}
		public Data setCount(Integer count) {
			this.count = count;
			return this;
		}
		
	}
	
	private static class Parent {
		private String schema;	
		private Integer id;	
		private String name;	
		private String description;
		private Map<Integer,Domain> domains = new LinkedHashMap<>();
		
		public String getSchema() {
			return schema;
		}
		public Parent setSchema(String schema) {
			this.schema = schema;
			return this;
		}
		
		public Integer getId() {
			return id;
		}
		public Parent setId(Integer id) {
			this.id = id;
			return this;
		}

		public String getName() {
			return name;
		}
		public Parent setName(String parentName) {
			this.name = parentName;
			return this;
		}

		public String getDescription() {
			return description;
		}
		public Parent setDescription(String parentDescription) {
			this.description = parentDescription;
			return this;
		}

		public Map<Integer, Domain> getDomains() {
			return domains;
		}
		
		public void add(Data data) {
			Domain domain =  domains.get( data.getDomain() );
			if (domain == null) {
				domain = new Domain()
					.setId( data.getDomain() )
					.setName( data.getDomainName() )
					.setDescription( data.getDomainDescription() )
				;
				domains.put(data.getDomain(), domain );
			}
			int i =  data.getMonth() -1 ;
			if ( data.isSalary() ) {
				domain.getSalaries()[i] = domain.getSalaries()[i] + data.getCount();
			} else {
				domain.getInvoices()[i] = domain.getInvoices()[i] + data.getCount();
				if ( i == 11 && data.getCount() > 100) {
					System.out.println( data.getCount() );
				}
			}
		}
	}
	
	private static class Domain {
		private Integer id;
		private String nmainName;
		private String description;	
		private Integer[] invoices = new Integer[] {0,0,0,0,0,0,0,0,0,0,0,0};
		private Integer[] salaries = new Integer[] {0,0,0,0,0,0,0,0,0,0,0,0};
		
		public Integer getId() {
			return id;
		}
		public Domain setId(Integer domain) {
			this.id = domain;
			return this;
		}
		
		public String getName() {
			return nmainName;
		}
		public Domain setName(String domainName) {
			this.nmainName = domainName;
			return this;
		}
		
		public String getDescription() {
			return description;
		}
		public Domain setDescription(String domainDescription) {
			this.description = domainDescription;
			return this;
		}
		
		public Integer[] getInvoices() {
			return invoices;
		}
		public Integer[] getSalaries() {
			return salaries;
		}
	}

	private static class Matrix {
		private Map<Integer,Parent> parents = new LinkedHashMap<>();
		
		public Map<Integer, Parent> getParents() {
			return parents;
		}
		public void add(Data data) {
			Parent parent =  parents.get( data.getParent() );
			if (parent == null) {
				parent = new Parent()
					.setSchema( data.getSchema() )
					.setId( data.getParent() )
					.setName( data.getParentName() )
					.setDescription( data.getParentDescription() )
				;
				parents.put(data.getParent(), parent );
			}
			parent.add( data ); 
		}
	}
	
	
	private static class ExcelAction extends AbsExcelAction implements Consumer<Matrix>{
		private XSSFCellStyle entryHeaderStyle;
		
		@Override
		protected void headerRow() {
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			sheet.setMargin(Sheet.LeftMargin, 0.3 );
			sheet.setMargin(Sheet.RightMargin, 0.3 );
			sheet.setMargin(Sheet.TopMargin, 0.3 );
			Footer footer = sheet.getFooter();
			footer.setLeft("Listado diario de movimientos ");
			footer.setRight("P\u00E1g: &P/&N");
			
			
			row = sheet.createRow(rowCount);
			CellStyle defaultStyle = workbook.createCellStyle();
			defaultStyle.setFont(smallFont);
			
			decimalStyle.setFont(smallFont);

			Font journalHeaderFont = workbook.createFont();
			journalHeaderFont.setBold(true);
			journalHeaderFont.setFontHeightInPoints((short) 8);
			
			XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
			headerStyle.setAlignment( HorizontalAlignment.CENTER );
			headerStyle.setVerticalAlignment( VerticalAlignment.CENTER);
		    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    headerStyle.setFillForegroundColor(AON_LIGHT_GRAY);
		    headerStyle.setFont(journalHeaderFont);
		    headerStyle.setBorderTop(BorderStyle.THIN);
		    headerStyle.setBorderRight(BorderStyle.THIN);
		    headerStyle.setBorderLeft(BorderStyle.THIN);
		    headerStyle.setBorderBottom(BorderStyle.THIN);
		    
		    
			entryHeaderStyle = (XSSFCellStyle) workbook.createCellStyle();
			entryHeaderStyle.setAlignment( HorizontalAlignment.CENTER );
			entryHeaderStyle.setVerticalAlignment( VerticalAlignment.CENTER);
			entryHeaderStyle.setBorderBottom(BorderStyle.THIN);
			entryHeaderStyle.setFont(defaulFont);

			
		    
			XSSFCellStyle rightHeaderCellStyle = (XSSFCellStyle) headerStyle.clone();
			rightHeaderCellStyle.setAlignment(HorizontalAlignment.RIGHT);

			row = sheet.createRow(rowCount++);
			cellCount = 0;
			
			CellUtil.createCell(row, cellCount, "SCHEMA", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 4 * 256);

			CellUtil.createCell(row, cellCount, "ID", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 8 * 256);

			CellUtil.createCell(row, cellCount, "PARENT NAME", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "PARENT DESCRIPTION", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);

			CellUtil.createCell(row, cellCount, "ID", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);

			CellUtil.createCell(row, cellCount, "DOMAIN NAME", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);

			CellUtil.createCell(row, cellCount, "DOMAIN DESCRIPTION", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);

			String[] months = new String[]{"ENE","FEB","MAR","ABR","MAY","JUN","JUL","AGO","SEP","OCT","NOV","DIC"};
			for ( int i = 0; i < months.length; i++) {
				CellUtil.createCell(row, cellCount, months[i], headerStyle);
				sheet.setDefaultColumnStyle(cellCount, defaultStyle);
				sheet.setColumnWidth(cellCount++, 12 * 256);
			}
			for ( int i = 0; i < months.length; i++) {
				CellUtil.createCell(row, cellCount, months[i], headerStyle);
				sheet.setDefaultColumnStyle(cellCount, defaultStyle);
				sheet.setColumnWidth(cellCount++, 12 * 256);
			}
		}

		@Override
		public void accept(Matrix matrix) {
			for ( Parent p : matrix.getParents().values() ) {
				for ( Domain d : p.getDomains().values() ) {
					row = sheet.createRow(rowCount++);
					cellCount = 0;
					addCell(p.getSchema());
					addCell(p.getId());
					addCell(p.getName());
					addCell(p.getDescription());
					addCell(d.getId());
					addCell(d.getName());
					addCell(d.getDescription());
					for (int i = 0; i < d.getSalaries().length; i++) {
						addCell(d.getSalaries()[i]);	
					}
					for (int i = 0; i < d.getInvoices().length; i++) {
						addCell(d.getInvoices()[i]);	
					}
				}
			}
			try {
				if (rowCount % 100 == 0) sheet.flushRows();
			} catch (IOException e) {
				throw new AonCoreException( e );
			}
		}
	}

	private static void printMatrix(Matrix matrix) throws IOException {
		FileOutputStream fos = new FileOutputStream( "/home/ecastellano/TRABAJO/SELECTS/CONTADORES.xls" );
		ExcelAction action = new ExcelAction( );
		action.initialize("Contadores");
		Stream.of( matrix ) 
			.forEach(action);
		action.finalize(fos);
		fos.flush();
		fos.close();
	}
	
}

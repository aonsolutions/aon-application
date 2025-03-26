package old.com.esferalia.aon.gwt.fiscal.test.accounting;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dev.util.collect.HashMap;

public class AyudatInvofoxCounters {
	
	private static Map<String,List<Domain>> domains = new HashMap<>();
	private static Map<Integer,Integer> counters = new HashMap<>();
	
	private AyudatInvofoxCounters() {
	}

	public static void main(String[] args) throws IOException {
		String[] files = new String[] {
			 "/home/ecastellano/TRABAJO/INVOFOX/USAGE/invofox_usage_ayudat.txt"
			,"/home/ecastellano/TRABAJO/INVOFOX/USAGE/invofox_usage_grupo.txt"
			,"/home/ecastellano/TRABAJO/INVOFOX/USAGE/invofox_usage_pro.txt"
		};
		for (String f :files ) {
			readFile(f);
		}
		
		String[] counterFiles = new String[] {
				 "/home/ecastellano/TRABAJO/INVOFOX/USAGE/invofox-ayudat.txt"
				,"/home/ecastellano/TRABAJO/INVOFOX/USAGE/invofox-grupo.txt"
				,"/home/ecastellano/TRABAJO/INVOFOX/USAGE/invofox-pro.txt"
			};
			for (String f :counterFiles ) {
				readCounters(f);
			}

		readExcel( );
	}

	private static void readCounters(String f) throws IOException {
		String s =AonStringUtils.substringAfter(f, "/home/ecastellano/TRABAJO/INVOFOX/USAGE/invofox-"); 
		String schema = AonStringUtils.substringBefore(s, ".");
		try (FileInputStream fis = new FileInputStream(f)) {
			List<Domain> schemaDomains = domains.get(schema);
			InputStreamReader ireader = new InputStreamReader(fis);
			LineNumberReader reader = new LineNumberReader(ireader);
			while ( reader.ready()) {
				String line = reader.readLine();
				if ( !AonStringUtils.startsWith( line, "domain" ) ) {
					String[] tokens =  AonStringUtils.splitPreserveAllTokens(line, '\t' );
					Integer domain = AonNumberUtils.toInteger(AonStringUtils.trimToNull(tokens[0]));
					Integer count = AonNumberUtils.toInteger(AonStringUtils.trimToNull(tokens[1]));
					counters.put( domain, count );
					schemaDomains.stream()
						.filter( d -> AonNumberUtils.equals(domain, d.getDomainId()  ))
						.findAny()
						.ifPresentOrElse( 
							d -> d.setCounter(count)
							, () -> System.out.println( schema + " -- " + domain + " ---> " + count + " --- NO PRESENT!!!") 
						)
						;
				}
			}
		}
	}

	private static void readFile(String f) throws IOException {
		String s =AonStringUtils.substringAfter(f, "/home/ecastellano/TRABAJO/INVOFOX/USAGE/invofox_usage_"); 
		String schema = AonStringUtils.substringBefore(s, ".");
		try (FileInputStream fis = new FileInputStream(f)) {
			List<Domain> schemaDomains = domains.get(schema);
			if (schemaDomains == null) {
				schemaDomains = new LinkedList<>();
				domains.put(schema, schemaDomains);
			}
			InputStreamReader ireader = new InputStreamReader(fis);
			LineNumberReader reader = new LineNumberReader(ireader);
			while ( reader.ready()) {
				String line = reader.readLine();
				if ( !AonStringUtils.startsWith( line, "registry" ) ) {
					schemaDomains.add( parseData( schema, line) );
				}
			}
		}
	}

	private static Domain parseData(String schema, String line) {
		String[] tokens =  AonStringUtils.splitPreserveAllTokens(line, '\t' );
		for (int i = 0; i < tokens.length ; i++) {
			tokens[i] = AonStringUtils.trimToNull(tokens[i]);
			if (AonStringUtils.equals(tokens[i],"NULL")) {
				tokens[i] = null;
			}
		}
		
		return new Domain()
			.setSchema( schema )
			.setRegistry( AonNumberUtils.toInteger( tokens[0]) )
			.setDocument( tokens[1] )	
			.setCompanyName( tokens[2] )
			.setDomainId( AonNumberUtils.toInteger( tokens[3]) )
			.setDomainName( tokens[4] )	
			.setDomainDescription( tokens[5] )	
			.setDomainAonCustomer( AonNumberUtils.toInteger( tokens[6]) )	
			.setParentId( AonNumberUtils.toInteger( tokens[7]) )
			.setParentName( tokens[8] )	
			.setParentDescription( tokens[9] )	
			.setParentAonCustomer( AonNumberUtils.toInteger( tokens[10]) )	
		;
	}
	private static class Invofox {
		private String name;
		private String document;
		private double processed;	
		
		public String getName() {
			return name;
		}
		public Invofox setName(String name) {
			this.name = name;
			return this;
		}
		public String getDocument() {
			return document;
		}
		public Invofox setDocument(String document) {
			this.document = document;
			return this;
		}
		
		public double getProcessed() {
			return processed;
		}
		public Invofox setProcessed(double processed) {
			this.processed = processed;
			return this;
		}
		public void addProcessed(double processed) {
			this.processed = AonMathUtils.round(this.processed + processed);
		}
		
	}
	

	private static class Domain {
		private String schema;
		private Integer registry;	
		private String document;
		private String companyName;
		
		private Integer parentId;	
		private String parentName;	
		private String parentDescription;	
		private Integer parentAonCustomer;
		
		private Integer domainId;
		private String domainName;	
		private String domainDescription;	
		private Integer domainAonCustomer;
		
		private Integer counter;
		
		public String getSchema() {
			return schema;
		}
		public Domain setSchema(String schema) {
			this.schema = schema;
			return this;
		}

		public Integer getRegistry() {
			return registry;
		}
		public Domain setRegistry(Integer registry) {
			this.registry = registry;
			return this;
		}
		
		public String getDocument() {
			return document;
		}
		public Domain setDocument(String document) {
			this.document = document;
			return this;
		}
		
		public String getCompanyName() {
			return companyName;
		}
		public Domain setCompanyName(String companyName) {
			this.companyName = companyName;
			return this;
		}
		
		public Integer getParentId() {
			return parentId;
		}
		public Domain setParentId(Integer parentId) {
			this.parentId = parentId;
			return this;
		}
		
		public String getParentName() {
			return parentName;
		}
		public Domain setParentName(String parentName) {
			this.parentName = parentName;
			return this;
		}
		
		public String getParentDescription() {
			return parentDescription;
		}
		public Domain setParentDescription(String parentDescription) {
			this.parentDescription = parentDescription;
			return this;
		}
		
		public Integer getParentAonCustomer() {
			return parentAonCustomer;
		}
		public Domain setParentAonCustomer(Integer parentAonCustomer) {
			this.parentAonCustomer = parentAonCustomer;
			return this;
		}
		
		public Integer getDomainId() {
			return domainId;
		}
		public Domain setDomainId(Integer domainId) {
			this.domainId = domainId;
			return this;
		}
		
		public String getDomainName() {
			return domainName;
		}
		public Domain setDomainName(String domainName) {
			this.domainName = domainName;
			return this;
		}
		
		public String getDomainDescription() {
			return domainDescription;
		}
		public Domain setDomainDescription(String domainDescription) {
			this.domainDescription = domainDescription;
			return this;
		}
		
		public Integer getDomainAonCustomer() {
			return domainAonCustomer;
		}
		public Domain setDomainAonCustomer(Integer domainAonCustomer) {
			this.domainAonCustomer = domainAonCustomer;
			return this;
		}
		public Integer getCounter() {
			return counter;
		}
		public Domain setCounter(Integer counter) {
			this.counter = counter;
			return this;
		}
	}
	
	private static void readExcel() throws IOException {
		FileOutputStream fos = new FileOutputStream( "/home/ecastellano/TRABAJO/INVOFOX/USAGE/February_2025.xlsx" );
		ExcelAction action = new ExcelAction( );
		action.initialize("USAGE");
		String f = "/home/ecastellano/TRABAJO/INVOFOX/USAGE/AonDocsPerCompany.xlsx";
		LinkedHashMap<String, Invofox> map = new LinkedHashMap<>();
		try (FileInputStream fis = new FileInputStream(f)) {
			try (XSSFWorkbook workbook = new XSSFWorkbook(fis)){
				XSSFSheet sheet = workbook.getSheetAt(0);
				Iterator<Row> rowIterator = sheet.iterator();
				int line = 0;
				while (rowIterator.hasNext()) {
					System.out.print("Line ..: " + line);
					Row row =  rowIterator.next();
					if (line > 0) {
						String document = row.getCell( 2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK ).getStringCellValue();
						System.out.print(" " + document + " ---> ");
						if (AonStringUtils.isNotBlank( document )) {
							double processed = row.getCell( 3 ).getNumericCellValue();
							Invofox invofox = map.get( document );
							if (invofox == null) {
								invofox = new Invofox()
									.setDocument(document)
									.setProcessed( processed )	
									;
								map.put(document, invofox);
							} else {
								invofox.addProcessed( processed );
							}
						}
					}
					System.out.println();
					line++;
				}
			}
		}
		
		for (Invofox invofox : map.values()) {
			String document = invofox.getDocument();
			long multipleDomains = AonCollectionUtils.valuesStream(domains)
				.flatMap( l -> AonCollectionUtils.stream(l))
				.filter( d -> AonStringUtils.equalsIgnoreCase(document, d.getDocument() ))
				.count();
			;
			MutableInt counter = new MutableInt();
			MutableInt docsCounter = new MutableInt();
			boolean grouped = (multipleDomains > 1);
			boolean found = false;
			for (List<Domain> schemaDomains : domains.values() ) {
				for (Domain d : schemaDomains ) {
					if (AonStringUtils.equalsIgnoreCase(invofox.getDocument(), d.getDocument())) {
						invofox.setName((multipleDomains>1?">>":"")); 
						action.accept(d, invofox , grouped , counter.getValue());		
						counter.increment();
						if (d.getCounter() != null) {
							docsCounter.add( d.getCounter() );
						}
						found = true;
					}
				}
			}
			if (!found) {
				invofox.setName("NOT FOUND");
				action.accept(null, invofox , false , 0);
			}
			
			if (counter.getValue() > 1) {
				invofox.setName("TOTAL");
				action.groupTotal(invofox, docsCounter.getValue() );
			}
		}
		
		action.finalize(fos);
		fos.flush();
		fos.close();
	}

	private static class ExcelAction extends AbsExcelAction {
		private XSSFCellStyle entryHeaderStyle;
		protected CellStyle decimalBoldStyle;
		
		@Override
		protected void headerRow() {
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			sheet.setMargin(Sheet.LeftMargin, 0.3 );
			sheet.setMargin(Sheet.RightMargin, 0.3 );
			sheet.setMargin(Sheet.TopMargin, 0.3 );
			
			row = sheet.createRow(rowCount);
			CellStyle defaultStyle = workbook.createCellStyle();
			defaultStyle.setFont(smallFont);
			
			decimalStyle.setFont(smallFont);
			
			decimalBoldStyle = workbook.createCellStyle();
		    decimalBoldStyle.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
		    decimalBoldStyle.setAlignment( HorizontalAlignment.RIGHT );
			decimalBoldStyle.setFont(smallBoldFont);

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

			XSSFCellStyle rightHeaderCellStyle = (XSSFCellStyle) headerStyle.copy();
			rightHeaderCellStyle.setAlignment(HorizontalAlignment.RIGHT);

			row = sheet.createRow(rowCount++);
			cellCount = 0;
			String[] columns = new String[] {
				"Company Name"
				,"TaxId"
				,"AON"
				,"Processed"
				,"BD"
				,"COMPANY ID"
				,"COMPANY NIF"
				,"COMPANY Nombre"
				,"DOMAIN ID"
				,"DOMAIN URL"
				,"DOMAIN NOMBRE"
				,"DOMAIN aonCustomer"
				,"PARENT ID"
				,"PARENT NOMBRE"
				,"PARENT DESCRIPCIÓN"
				,"PARENT aonCustomer"
			};
			
						
			for (String c : columns) {
				CellUtil.createCell(row, cellCount, c, headerStyle);
				sheet.setDefaultColumnStyle(cellCount, defaultStyle);
				sheet.setColumnWidth(cellCount++, 10 * 256);
			}
		}
		
		public void groupTotal(Invofox invofox, double total) {
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			Cell cell = addCell(invofox.getName());
			
			cell.getCellStyle().setFont(boldFont);
			cell = addCell(invofox.getDocument());
			cell.getCellStyle().setFont(boldFont);
			cell = addCell(total);
			cell.setCellStyle(decimalBoldStyle);
			cell = addCell(invofox.getProcessed());
			cell.setCellStyle(decimalBoldStyle);
			addEmptyCell();
			addEmptyCell();
			addEmptyCell();
			addEmptyCell();
			addEmptyCell();
			addEmptyCell();
			addEmptyCell();
			addEmptyCell();
			addEmptyCell();
			addEmptyCell();
			addEmptyCell();
			addEmptyCell();
		}
		
		public void accept(Domain domain, Invofox invofox, boolean grouped, Integer counter) {
			row = sheet.createRow(rowCount++);
			cellCount = 0;

			addCell(invofox.getName());
			addCell(invofox.getDocument());
			
			if (domain != null && domain.getCounter() != null) {
				addCell((double) domain.getCounter());
			} else {
				addEmptyCell();
			}
			
			if (!grouped || counter == 0) {
				addCell(invofox.getProcessed());
			} else {
				addEmptyCell();
			}
			if (domain != null) {
				addCell(domain.getSchema());
				addCell(domain.getRegistry());
				addCell(domain.getDocument());
				addCell(domain.getCompanyName());
				addCell(domain.getDomainId());
				addCell(domain.getDomainName());
				addCell(domain.getDomainDescription());
				addCell(domain.getDomainAonCustomer());
				addCell(domain.getParentId());
				addCell(domain.getParentName());
				addCell(domain.getParentDescription());
				addCell(domain.getParentAonCustomer());
			}
		}
	}

}

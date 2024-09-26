package old.com.esferalia.aon.gwt.fiscal.test.accounting;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
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
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AyudatInvofoxCounters {
	
	private static List<Domain> domains = new LinkedList<>();
	
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
		
		readExcel( );
	}

	private static void readFile(String f) throws IOException {
		String s =AonStringUtils.substringAfter(f, "/home/ecastellano/TRABAJO/INVOFOX/USAGE/invofox_usage_"); 
		String schema = AonStringUtils.substringBefore(s, ".");
		try (FileInputStream fis = new FileInputStream(f)) {
			InputStreamReader ireader = new InputStreamReader(fis);
			LineNumberReader reader = new LineNumberReader(ireader);
			while ( reader.ready()) {
				String line = reader.readLine();
				if ( !AonStringUtils.startsWith( line, "registry" ) ) {
					domains.add( parseData( schema, line) );
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
			.setParentId( AonNumberUtils.toInteger( tokens[3]) )
			.setParentName( tokens[4] )	
			.setParentDescription( tokens[5] )	
			.setParentAonCustomer( AonNumberUtils.toInteger( tokens[6]) )	
			.setDomainId( AonNumberUtils.toInteger( tokens[7]) )
			.setDomainName( tokens[8] )	
			.setDomainDescription( tokens[9] )	
			.setDomainAonCustomer( AonNumberUtils.toInteger( tokens[10]) )	
		;
	}
	private static class Invofox {
		private String companyId;
		private String name;
		private String document;
		private double pendingCorrection;
		private double pendingDecission;
		private double approved;
		private double rejected;
		private double discarded;
		private double exported;
		private double error;
		private double total;
		
		private double processed;	
		private double automated;
		private double clientDiscarded;	
		private double duplicated;
		private double usedClassifier;	
		private double usedSplitter;

		
		public String getCompanyId() {
			return companyId;
		}
		public Invofox setCompanyId(String companyId) {
			this.companyId = companyId;
			return this;
		}
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
		
		public double getPendingCorrection() {
			return pendingCorrection;
		}
		public Invofox setPendingCorrection(double pendingCorrection) {
			this.pendingCorrection = pendingCorrection;
			return this;
		}
		
		public double getPendingDecission() {
			return pendingDecission;
		}
		public Invofox setPendingDecission(double pendingDecission) {
			this.pendingDecission = pendingDecission;
			return this;
		}
		
		public double getApproved() {
			return approved;
		}
		public Invofox setApproved(double approved) {
			this.approved = approved;
			return this;
		}
		
		public double getRejected() {
			return rejected;
		}
		public Invofox setRejected(double rejected) {
			this.rejected = rejected;
			return this;
		}
		
		public double getDiscarded() {
			return discarded;
		}
		public Invofox setDiscarded(double discarded) {
			this.discarded = discarded;
			return this;
		}
		
		public double getExported() {
			return exported;
		}
		public Invofox setExported(double exported) {
			this.exported = exported;
			return this;
		}
		
		public double getError() {
			return error;
		}
		public Invofox setError(double error) {
			this.error = error;
			return this;
		}
		
		public double getTotal() {
			return total;
		}
		public Invofox setTotal(double total) {
			this.total = total;
			return this;
		}
		public double getProcessed() {
			return processed;
		}
		public Invofox setProcessed(double processed) {
			this.processed = processed;
			return this;
		}
		public double getAutomated() {
			return automated;
		}
		public Invofox setAutomated(double automated) {
			this.automated = automated;
			return this;
		}
		public double getClientDiscarded() {
			return clientDiscarded;
		}
		public Invofox setClientDiscarded(double clientDiscarded) {
			this.clientDiscarded = clientDiscarded;
			return this;
		}
		public double getDuplicated() {
			return duplicated;
		}
		public Invofox setDuplicated(double duplicated) {
			this.duplicated = duplicated;
			return this;
		}
		public double getUsedClassifier() {
			return usedClassifier;
		}
		public Invofox setUsedClassifier(double usedClassifier) {
			this.usedClassifier = usedClassifier;
			return this;
		}
		public double getUsedSplitter() {
			return usedSplitter;
		}
		public Invofox setUsedSplitter(double usedSplitter) {
			this.usedSplitter = usedSplitter;
			return this;
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
	
	}
	
	private static void readExcel() throws FileNotFoundException, IOException {
		FileOutputStream fos = new FileOutputStream( "/home/ecastellano/TRABAJO/INVOFOX/USAGE/August 2024.xlsx" );
		ExcelAction action = new ExcelAction( );
		action.initialize("USAGE");
		String f = "/home/ecastellano/TRABAJO/INVOFOX/USAGE/AonDocsPerCompany.xlsx";
		try (FileInputStream fis = new FileInputStream(f)) {
			try (XSSFWorkbook workbook = new XSSFWorkbook(fis)){
				XSSFSheet sheet = workbook.getSheetAt(1);
				Iterator<Row> rowIterator = sheet.iterator();
				int line = 0;
				while (rowIterator.hasNext()) {
					System.out.print("Line ..: " + line);
					Row row =  rowIterator.next();
					if (line > 0) {
						String document = row.getCell( 2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK ).getStringCellValue();
						if (AonStringUtils.isNotBlank( document )) {
							Invofox invofox = new Invofox()
									.setCompanyId( row.getCell( 0 , Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue())
									.setName( row.getCell( 1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK ).getStringCellValue())
									.setDocument(document)
									
//							.setPendingCorrection(row.getCell( 3 ).getNumericCellValue())
//							.setPendingDecission(row.getCell( 4 ).getNumericCellValue())
//							.setApproved(row.getCell( 5 ).getNumericCellValue())
//							.setRejected(row.getCell( 6 ).getNumericCellValue())
//							.setDiscarded(row.getCell( 7 ).getNumericCellValue())
//							.setExported(row.getCell( 8 ).getNumericCellValue())
//							.setError(row.getCell( 9 ).getNumericCellValue())
//							.setTotal(row.getCell( 10 ).getNumericCellValue())
									
									.setProcessed( row.getCell( 3 ).getNumericCellValue() )	
									.setAutomated(row.getCell( 4 ).getNumericCellValue()) 	
									.setClientDiscarded(row.getCell( 5 ).getNumericCellValue())	
									.setDuplicated(row.getCell( 6 ).getNumericCellValue())	
									.setUsedClassifier(row.getCell( 7 ).getNumericCellValue())	
									.setUsedSplitter(row.getCell( 8 ).getNumericCellValue())
									
									;	
							
							boolean exists = domains.stream()
									.filter( d -> AonStringUtils.equalsIgnoreCase(invofox.getDocument(), d.getDocument() ))
									.findAny()
									.isPresent();
							
							if (!exists) {
								System.out.println( invofox.getDocument() );
							}
							domains.stream()
							.filter( d -> AonStringUtils.equalsIgnoreCase(invofox.getDocument(), d.getDocument() ))
							.forEach( d -> action.accept(d, invofox ))
							;
						}
					}
					System.out.println();
					line++;
				}
			}
		}
		action.finalize(fos);
		fos.flush();
		fos.close();
	}

	private static class ExcelAction extends AbsExcelAction {
		private XSSFCellStyle entryHeaderStyle;
		
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
			String[] columns = new String[] {
				"Company Name"
				,"TaxId"						
				,"Processed"
				,"Automated"
				,"ClientDiscarded"	
				,"Duplicated"
				,"UsedClassifier"	
				,"UsedSplitter"
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

		public void accept(Domain domain, Invofox invofox) {
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			addCell(invofox.getName());
			addCell(invofox.getDocument());
			
			addCell(invofox.getProcessed());
			addCell(invofox.getAutomated());
			addCell(invofox.getClientDiscarded());	
			addCell(invofox.getDuplicated());
			addCell(invofox.getUsedClassifier());	
			addCell(invofox.getUsedSplitter());
			
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

			try {
				if (rowCount % 100 == 0) sheet.flushRows();
			} catch (IOException e) {
				throw new AonCoreException( e );
			}
		}
	}
}

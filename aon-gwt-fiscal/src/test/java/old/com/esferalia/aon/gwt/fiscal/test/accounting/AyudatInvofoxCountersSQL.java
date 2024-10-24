package old.com.esferalia.aon.gwt.fiscal.test.accounting;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.ibm.icu.text.MessageFormat;

public class AyudatInvofoxCountersSQL {
	
	private static List<Domain> domains = new LinkedList<>();
	
	private static String select = 
		"insert into enterprise_data "  
			+"(`domain` "
			+",`enterprise` "
			+",`name`  "
			+",`expression` "
			+",`start_date` "
			+",`end_date`) "
		+"select " 
			+"{0}"
			+",registry"
			+",\"INVOFOX\""
			+",\"{1}\""
			+",\"2024-09-01\""
			+",\"2024-09-30\""
			+" from enterprise where domain = {2} limit 1;"
	;
	
	private AyudatInvofoxCountersSQL() {
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
		FileOutputStream pro_fos = new FileOutputStream( "/home/ecastellano/TRABAJO/INVOFOX/USAGE/pro.sql" );
		FileOutputStream gru_fos = new FileOutputStream( "/home/ecastellano/TRABAJO/INVOFOX/USAGE/grupo-ayudat.sql" );
		FileOutputStream ayu_fos = new FileOutputStream( "/home/ecastellano/TRABAJO/INVOFOX/USAGE/ayudat.sql" );
		PrintWriter pro_wr = new PrintWriter(pro_fos);		
		PrintWriter gru_wr = new PrintWriter(gru_fos);
		PrintWriter ayu_wr = new PrintWriter(ayu_fos);
		
		String f = "/home/ecastellano/TRABAJO/INVOFOX/USAGE/AonDocsPerCompany.xlsx";
		try (FileInputStream fis = new FileInputStream(f)) {
			try (XSSFWorkbook workbook = new XSSFWorkbook(fis)){
				XSSFSheet sheet = workbook.getSheetAt(0);
				Iterator<Row> rowIterator = sheet.iterator();
				int line = 0;
				while (rowIterator.hasNext()) {
					Row row =  rowIterator.next();
					if (line > 0) {
						String document = row.getCell( 2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK ).getStringCellValue();
						if (AonStringUtils.isNotBlank( document )) {
							Invofox invofox = new Invofox()
								.setCompanyId( row.getCell( 0 , Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue())
								.setName( row.getCell( 1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK ).getStringCellValue())
								.setDocument(document)
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
								System.out.println( invofox.getDocument() + " --- " + invofox.getProcessed());
							}
							domains.stream()
								.filter( d -> AonStringUtils.equalsIgnoreCase(invofox.getDocument(), d.getDocument() ))
								.filter( d -> d.getDomainId() == null || d.getDomainId() == 0)
								.forEach( d -> {
									System.out.println( d.getDomainId() + " --- " + invofox.getDocument() + " --- " + invofox.getProcessed());	
								});
							
							domains.stream()
								.filter( d -> AonStringUtils.equalsIgnoreCase(invofox.getDocument(), d.getDocument() ))
								.filter( d -> d.getDomainId() != null && d.getDomainId() != 0)
								.forEach( d -> {
									String domainId =  AonNumberUtils.toString(d.getDomainId());
									String value  =  AonNumberUtils.toString((int) invofox.getProcessed());
									String sql = MessageFormat.format(select, domainId, value, domainId);
									if ("pro".equals(d.getSchema())) {
										pro_wr.println(sql);
									} else if ("grupo".equals(d.getSchema())) {
										gru_wr.println(sql);
									} else if ("ayudat".equals(d.getSchema())) {
										ayu_wr.println(sql);
									} else{
										throw new IllegalStateException("???????");
									}
								})
							;
						}
					}
					line++;
				}
			}
		}
		pro_wr.flush();		
		gru_wr.flush();
		ayu_wr.flush();
		pro_fos.flush();
		pro_fos.close();
		gru_fos.flush();
		gru_fos.close();
		ayu_fos.flush();
		ayu_fos.close();
	}

}

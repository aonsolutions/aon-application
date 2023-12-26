package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Fbatch.FBATCH;
import static com.esferalia.aon.jooq.tables.FbatchDetail.FBATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rpaymethod.RPAYMETHOD;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jooq.Record;
import org.jooq.Result;

import com.esferalia.aon.jooq.tables.records.FinanceRecord;
import com.esferalia.aon.jooq.tables.records.RattachRecord;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SettleSalariesDAO {
	
	private static java.sql.Date parseToSQLDate(Date date){
		return null == date ? null : new java.sql.Date(date.getTime());
	}
	
	private static String formatDate(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		return simpleDateFormat.format(date);
	}
	
	private static String formatYear(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
		return simpleDateFormat.format(date);
	}
	
	private static String formatMonthYear(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMyyyy");
		return simpleDateFormat.format(date);
	}
	
	private static String formatCreDtTm(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return simpleDateFormat.format(date);
	}
	
	// ------------------- CREATE VENCIMIENTOS
	
	public static void createSettleSalaries(CloseableAONContext ctx, Date date) {
		Result<Record> salaries = ctx.getDslContext().select().from(SALARY)
				.join(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
				.join(WORKPLACE).on(WORKPLACE.ID.eq(CONTRACT.WORKPLACE))
				.join(REGISTRY).on(REGISTRY.ID.eq(CONTRACT.PERSON))
				.join(RPAYMETHOD).on(RPAYMETHOD.REGISTRY.eq(CONTRACT.PERSON))
				.leftOuterJoin(RBANK).on(RBANK.ID.eq(RPAYMETHOD.RBANK))
				.where(SALARY.DOMAIN.eq(ctx.getDomainId()))
				.and(SALARY.ISSUE_DATE.eq(parseToSQLDate(date)))
				.and(SALARY.TYPE.lt((byte)4)) // Nomina, Extra, Finiquito, Atraso
				.fetch();
		
		if(salaries.isEmpty()) throw new AonCoreException("No existen n\u00f3minas sobre las que generar un vencimiento para el periodo " + formatDate(date));
		
		boolean hasSettleSalaryModify = false;
		
		for(Record record : salaries) {
			// Delete finance for this registry which status == 0 (Pediente)
			ctx.getDslContext().deleteFrom(FINANCE)
				.where(FINANCE.DOMAIN.eq(ctx.getDomainId()))
				.and(FINANCE.DUE_DATE.eq(parseToSQLDate(date)))
				.and(FINANCE.REGISTRY.eq(record.get(REGISTRY.ID)))
				.and(FINANCE.PAYROLL.eq((byte)1))
				.and(FINANCE.STATUS.eq((byte)0))
				.execute();
			
			String concept = getSalaryType(record.get(SALARY.TYPE));
			
			Result<FinanceRecord> finances = ctx.getDslContext().selectFrom(FINANCE)
					.where(FINANCE.DOMAIN.eq(ctx.getDomainId()))
					.and(FINANCE.DUE_DATE.eq(parseToSQLDate(date)))
					.and(FINANCE.REGISTRY.eq(record.get(REGISTRY.ID)))
					.and(FINANCE.PAYROLL.eq((byte)1))
					.and(FINANCE.CONCEPT.like(concept + "%"))
					.fetch();
			
			if(finances.isEmpty()) {
				// Insert new salary finance
				ctx.getDslContext().insertInto(FINANCE)
					.set(FINANCE.DOMAIN, ctx.getDomainId())
					.set(FINANCE.PAYMENT, (byte)1)
					.set(FINANCE.REGISTRY, record.get(REGISTRY.ID))
					.set(FINANCE.RDOCUMENT, record.get(REGISTRY.DOCUMENT))
					.set(FINANCE.RDOCUMENT_TYPE, record.get(REGISTRY.DOCUMENT_TYPE))
					.set(FINANCE.RDOCUMENT_COUNTRY, record.get(REGISTRY.DOCUMENT_COUNTRY))
					.set(FINANCE.RNAME, record.get(REGISTRY.NAME))
					.set(FINANCE.AMOUNT, record.get(SALARY.TOTAL_LIQUID))
					.set(FINANCE.CONCEPT, concept + " - " + formatDate(date))
					.set(FINANCE.DUE_DATE, parseToSQLDate(date))
					.set(FINANCE.PAY_METHOD, record.get(RPAYMETHOD.PAY_METHOD))
					.set(FINANCE.BANK_ACCOUNT, record.get(RBANK.BANK_ACCOUNT))
					.set(FINANCE.BANK_ALIAS, record.get(RBANK.ALIAS))
					.set(FINANCE.BIC, record.get(RBANK.BIC))
					.set(FINANCE.SCOPE, record.get(WORKPLACE.SCOPE))
					.set(FINANCE.PAYROLL, (byte)1)
					.set(FINANCE.SOURCE_ID, record.get(SALARY.ID))
					.set(FINANCE.CREATION_DATE, new Timestamp(new Date().getTime()))
					.set(FINANCE.CREATION_USER, ctx.getUser())
					.execute();
				
				hasSettleSalaryModify = true;
			} else {
				// Check if existing amount is same as salary
				Double salaryAmount = record.get(SALARY.TOTAL_LIQUID);
				Double financeAmount = finances.stream().mapToDouble(finance -> finance.getAmount()).sum();
				
				if(!salaryAmount.equals(financeAmount)) {
					Double amountDiff = salaryAmount - financeAmount;
					// Insert new salary diff finance
					ctx.getDslContext().insertInto(FINANCE)
						.set(FINANCE.DOMAIN, ctx.getDomainId())
						.set(FINANCE.PAYMENT, (byte)1)
						.set(FINANCE.REGISTRY, record.get(REGISTRY.ID))
						.set(FINANCE.RDOCUMENT, record.get(REGISTRY.DOCUMENT))
						.set(FINANCE.RDOCUMENT_TYPE, record.get(REGISTRY.DOCUMENT_TYPE))
						.set(FINANCE.RDOCUMENT_COUNTRY, record.get(REGISTRY.DOCUMENT_COUNTRY))
						.set(FINANCE.RNAME, record.get(REGISTRY.NAME))
						.set(FINANCE.AMOUNT, amountDiff)
						.set(FINANCE.CONCEPT, concept + " - " + formatDate(date))
						.set(FINANCE.DUE_DATE, parseToSQLDate(date))
						.set(FINANCE.PAY_METHOD, record.get(RPAYMETHOD.PAY_METHOD))
						.set(FINANCE.BANK_ACCOUNT, record.get(RBANK.BANK_ACCOUNT))
						.set(FINANCE.BANK_ALIAS, record.get(RBANK.ALIAS))
						.set(FINANCE.BIC, record.get(RBANK.BIC))
						.set(FINANCE.SCOPE, record.get(WORKPLACE.SCOPE))
						.set(FINANCE.PAYROLL, (byte)1)
						.set(FINANCE.SOURCE_ID, record.get(SALARY.ID))
						.set(FINANCE.CREATION_DATE, new Timestamp(new Date().getTime()))
						.set(FINANCE.CREATION_USER, ctx.getUser())
						.execute();
					
					hasSettleSalaryModify = true;
				}
			}	
		}
		
		if(!hasSettleSalaryModify) throw new AonCoreException("No existen modificaciones en las n\u00f3minas sobre los vencimiento ya generados para el periodo " + formatDate(date));
	}

	private static String getSalaryType(Byte salaryType) {
		switch (salaryType) {
		case 1:
			return "EXTRA";
		case 2:
			return "FINIQUITO";
		case 3:
			return "ATRASO";
		default:
			return "N\u00d3MINA";
		}
	}
	
	// ------------------- CREATE FICHERO SEPA
	
	public static Integer createSepaFile(CloseableAONContext ctx, Integer fbatchId) throws Exception {
		
		// Check if has vencimientos
		
		Result<Record> fbatchDetails = ctx.getDslContext().select().from(FBATCH_DETAIL)
			.innerJoin(FINANCE).on(FINANCE.ID.eq(FBATCH_DETAIL.FINANCE))
			.innerJoin(RADDRESS).on(RADDRESS.REGISTRY.eq(FINANCE.REGISTRY))
			.innerJoin(GEOZONE).on(GEOZONE.ID.eq(RADDRESS.GEOZONE))
			.where(FBATCH_DETAIL.FBATCH.eq(fbatchId))
			.and(RADDRESS.TYPE.eq((byte)0))
			.fetch();
		
		if(null == fbatchDetails || fbatchDetails.isEmpty()) throw new AonCoreException("No existen vencimientos en la remesa sobre los que generar el fichero Sepa");
		
		Record fbatchEnterprise = ctx.getDslContext().select().from(FBATCH)
				.innerJoin(ENTERPRISE).on(ENTERPRISE.DOMAIN.eq(FBATCH.DOMAIN))
				.innerJoin(REGISTRY).on(REGISTRY.ID.eq(ENTERPRISE.REGISTRY))
				.innerJoin(RBANK).on(RBANK.ID.eq(FBATCH.RBANK))
				.innerJoin(RADDRESS).on(RADDRESS.REGISTRY.eq(REGISTRY.ID))
				.innerJoin(GEOZONE).on(GEOZONE.ID.eq(RADDRESS.GEOZONE))
				.where(FBATCH.ID.eq(fbatchId))
				.and(RADDRESS.TYPE.eq((byte)0))
				.fetchOne();
		
		// Start SEPA xml file
		
		String xmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
		
		xmlData += "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"; 
		
		xmlData += "<CstmrCdtTrfInitn>";
		
		// Encabezado
		
		xmlData += "<GrpHdr>";
		
		xmlData += "<MsgId>A" + AonStringUtils.leftPad(fbatchEnterprise.get(FBATCH.ID).toString(), 10, '0') + formatYear(fbatchEnterprise.get(FBATCH.ISSUE_DATE)) + formatMonthYear(fbatchEnterprise.get(FBATCH.ISSUE_DATE)) + AonStringUtils.leftPad(fbatchEnterprise.get(ENTERPRISE.REGISTRY).toString(), 14, '0') + "</MsgId>";
		
		xmlData += "<CreDtTm>" + formatCreDtTm(fbatchEnterprise.get(FBATCH.ISSUE_DATE)) + "T00:00:00</CreDtTm>";
		
		xmlData += "<NbOfTxs>" + fbatchDetails.size() + "</NbOfTxs>";
		
		xmlData += "<CtrlSum>" + formatDouble(fbatchDetails.stream().map(r -> r.get(FBATCH_DETAIL.AMOUNT)).reduce(0.00, (a, b) -> a + b)) + "</CtrlSum>";
		
		xmlData += "<InitgPty>";
		
		xmlData += "<Nm>" + removeSpecialCharacters(fbatchEnterprise.get(REGISTRY.NAME)) + "</Nm>";
		
		xmlData += "<Id>";
		
		xmlData += "<OrgId>";
		
		xmlData += "<Othr>";
		
		xmlData += "<Id>" + AonStringUtils.rightPad(fbatchEnterprise.get(REGISTRY.DOCUMENT), 12, '0') + "</Id>";
		
		xmlData += "</Othr>";
		
		xmlData += "</OrgId>";
		
		xmlData += "</Id>";
		
		xmlData += "</InitgPty>";
		
		xmlData += "</GrpHdr>";
		
		// Informacion Pago
		
		xmlData += "<PmtInf>";
		
		xmlData += "<PmtInfId>A" + AonStringUtils.leftPad(fbatchEnterprise.get(FBATCH.ID).toString(), 10, '0') + formatYear(fbatchEnterprise.get(FBATCH.ISSUE_DATE)) + formatMonthYear(fbatchEnterprise.get(FBATCH.ISSUE_DATE)) + AonStringUtils.leftPad(fbatchEnterprise.get(REGISTRY.DOCUMENT), 14, '0') + "</PmtInfId>";
		
		xmlData += "<PmtMtd>TRF</PmtMtd>";
		
		xmlData += "<ReqdExctnDt>" + formatCreDtTm(fbatchEnterprise.get(FBATCH.ISSUE_DATE)) + "</ReqdExctnDt>";
		
		xmlData += "<Dbtr>";
		
		xmlData += "<Nm>" + removeSpecialCharacters(fbatchEnterprise.get(REGISTRY.NAME)) + "</Nm>";
		
		xmlData += "<PstlAdr>";
		
		xmlData += "<Ctry>ES</Ctry>";
		
		xmlData += "<AdrLine>" + removeSpecialCharacters(fbatchEnterprise.get(RADDRESS.STREET_TYPE)) + ". " + removeSpecialCharacters(fbatchEnterprise.get(RADDRESS.ADDRESS)) + " " + removeSpecialCharacters(fbatchEnterprise.get(RADDRESS.NUMBER)) + ", " + removeSpecialCharacters(fbatchEnterprise.get(RADDRESS.ADDRESS2)) + " " + fbatchEnterprise.get(RADDRESS.ZIP) + " " + removeSpecialCharacters(fbatchEnterprise.get(RADDRESS.CITY)) + " (" + removeSpecialCharacters(fbatchEnterprise.get(GEOZONE.NAME)) + ")</AdrLine>";
		
		xmlData += "</PstlAdr>";
		
		xmlData += "<Id><OrgId><Othr><Id>" + AonStringUtils.rightPad(fbatchEnterprise.get(REGISTRY.DOCUMENT), 12, '0') + "</Id></Othr></OrgId></Id>";
		
		xmlData += "</Dbtr>";
		
		xmlData += "<DbtrAcct>";
		
		xmlData += "<Id><IBAN>" + fbatchEnterprise.get(RBANK.BANK_ACCOUNT) + "</IBAN></Id>";
		
		xmlData += "<Ccy>EUR</Ccy>";
		
		xmlData += "</DbtrAcct>";
		
		xmlData += "<DbtrAgt><FinInstnId><BIC>" + fbatchEnterprise.get(RBANK.BIC) + "</BIC></FinInstnId></DbtrAgt>";
		
		// Informacion Pago (Trabajador)
		
		for(Record fbatchDetail : fbatchDetails) {
			xmlData += "<CdtTrfTxInf>";
			
			xmlData += "<PmtId><EndToEndId>" + fbatchDetail.get(FINANCE.ID) + "/" + fbatchDetail.get(FINANCE.RDOCUMENT) + "/" + new SimpleDateFormat("yyyyMMdd").format(fbatchDetail.get(FINANCE.DUE_DATE)) + "</EndToEndId></PmtId>";
			
			xmlData += "<PmtTpInf><SvcLvl><Cd>SEPA</Cd></SvcLvl><CtgyPurp><Cd>SALA</Cd></CtgyPurp></PmtTpInf>";
			
			xmlData += "<Amt><InstdAmt Ccy=\"EUR\">" + formatDouble(fbatchDetail.get(FINANCE.AMOUNT)) + "</InstdAmt></Amt>";
			
			xmlData += "<ChrgBr>SLEV</ChrgBr>";
			
			xmlData += "<CdtrAgt><FinInstnId><BIC>" + fbatchDetail.get(FINANCE.BIC) + "</BIC></FinInstnId></CdtrAgt>";
			
			xmlData += "<Cdtr>";
			
			xmlData += "<Nm>" + removeSpecialCharacters(fbatchDetail.get(FINANCE.RNAME)) + "</Nm>";
			
			xmlData += "<PstlAdr>";
			
			xmlData += "<Ctry>ES</Ctry>";
			
			xmlData += "<AdrLine>" + removeSpecialCharacters(fbatchDetail.get(RADDRESS.STREET_TYPE)) + ". " + removeSpecialCharacters(fbatchDetail.get(RADDRESS.ADDRESS)) + " " + removeSpecialCharacters(fbatchDetail.get(RADDRESS.NUMBER)) + ", " + removeSpecialCharacters(fbatchDetail.get(RADDRESS.ADDRESS2)) + " " + fbatchDetail.get(RADDRESS.ZIP) + " " + removeSpecialCharacters(fbatchDetail.get(RADDRESS.CITY)) + " (" + removeSpecialCharacters(fbatchDetail.get(GEOZONE.NAME)) + ")</AdrLine>";
			
			xmlData += "</PstlAdr>";
			
			xmlData += "<Id><PrvtId><Othr>";
			
			xmlData += "<Id>" + fbatchDetail.get(FINANCE.RDOCUMENT) + "</Id>";
			
			xmlData += "<Issr>" + getDocumentType(fbatchDetail.get(FINANCE.RDOCUMENT_TYPE)) + "</Issr>";
			
			xmlData += "</Othr></PrvtId></Id>";
			
			xmlData += "</Cdtr>";
			
			xmlData += "<CdtrAcct><Id><IBAN>" + fbatchDetail.get(FINANCE.BANK_ACCOUNT) + "</IBAN></Id></CdtrAcct>";
			
			xmlData += "</CdtTrfTxInf>";
		}
		
		xmlData += "</PmtInf>";
		
		// End
		
		xmlData += "</CstmrCdtTrfInitn></Document>";
		
		System.out.println("SEPA GENERATED!");
		
		return saveAttach(ctx, fbatchId, generarBytesDesdeXML(xmlData));
	}
	
	private static Integer saveAttach(CloseableAONContext ctx, Integer fbatchId, byte[] data) {
		Record fbatchEnterprise = ctx.getDslContext().select().from(FBATCH)
				.innerJoin(ENTERPRISE).on(ENTERPRISE.DOMAIN.eq(FBATCH.DOMAIN))
				.where(FBATCH.ID.eq(fbatchId))
				.fetchOne();
		
		if(null != fbatchEnterprise.get(FBATCH.RATTACH)) {
			ctx.getDslContext().update(RATTACH)
				.set(RATTACH.DATA, data)
				.set(RATTACH.DESCRIPTION, "SEPA_34_14_XML_" + fbatchEnterprise.get(FBATCH.DESCRIPTION))
				.set(RATTACH.MODIFICATION_USER,ctx.getUser())
				.set(RATTACH.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
				.where(RATTACH.ID.eq(fbatchEnterprise.get(FBATCH.RATTACH)))
				.execute();
			
			return fbatchEnterprise.get(FBATCH.RATTACH);
		} else {
			RattachRecord rattach = ctx.getDslContext().insertInto(RATTACH)
				.set(RATTACH.DOMAIN, fbatchEnterprise.get(FBATCH.DOMAIN))
				.set(RATTACH.REGISTRY, fbatchEnterprise.get(ENTERPRISE.REGISTRY))
				.set(RATTACH.MIMETYPE, (byte)5) //XML
				.set(RATTACH.DESCRIPTION, "SEPA_34_14_XML_" + fbatchEnterprise.get(FBATCH.DESCRIPTION))
				.set(RATTACH.DATA, data)
				.set(RATTACH.TYPE, (byte)22) //¡?
				.set(RATTACH.SECURITY_LEVEL, (byte)0)
				.set(RATTACH.ATTACH_DATE, AonDateUtils.toSql(new Date()))
				.set(RATTACH.CREATION_USER,ctx.getUser())
				.set(RATTACH.CREATION_DATE, new Timestamp( System.currentTimeMillis()))
				.returning(RATTACH.ID)
				.fetchOne();
			
			ctx.getDslContext().update(FBATCH)
				.set(FBATCH.RATTACH, rattach.getId())
				.where(FBATCH.ID.eq(fbatchId))
				.execute();
		
			return rattach.getId();
		}
	}

	private static String getDocumentType(Byte documentType) {
		switch (documentType) {
			case (byte)0:
				return "DNI";
			case (byte)1:
				return "CIF";
			case (byte)2:
				return "NIE";
			case (byte)3:
				return "Pasaporte";
			default:
				return "No Censado";
		}
	}

	private static void buildFile(byte[] arr_bytes, String docName) {
		File f = new File(docName);
		try {
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(arr_bytes);
			fos.close();
		} catch (FileNotFoundException e) {
			System.err.println("Archivo no encontrado");
		} catch (IOException e) {
			System.err.println("Error al escribir");
		}

	}
	
	public static String removeSpecialCharacters(String input) {
		if(AonStringUtils.isBlank(input)) return "";
		return input.replace("Á", "A")
	        .replace("É", "E")
	        .replace("Í", "I")
	        .replace("Ó", "O")
	        .replace("Ú", "U")
	        .replace("á", "a")
	        .replace("é", "e")
	        .replace("í", "i")
	        .replace("ó", "o")
	        .replace("ú", "u")
	        .replace("\u00D1", "N")
	        .replace("\u00F1", "n")
	        .replace("º", "")
	        .replace("ª", "")
	        .replace("\u00AA", "")
	        .replace("\u00B0", "")
	        ;
    }
	
	public static double formatDouble(double number) {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#.##", symbols);

        String formattedString = decimalFormat.format(number);
        double formattedNumber = Double.parseDouble(formattedString);
        return formattedNumber;
    }
	
	private static byte[] generarBytesDesdeXML(String xmlData) throws Exception {
        // Crea un StreamSource a partir de la cadena XML
		InputStream inputStream = new ByteArrayInputStream(xmlData.getBytes(StandardCharsets.UTF_8));


        // Crea un ByteArrayOutputStream para almacenar los bytes generados
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Utiliza un Transformer para transformar el XML en un flujo de bytes
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(new StreamSource(inputStream), new StreamResult(outputStream));

        // Devuelve los bytes generados
        return outputStream.toByteArray();
    }
	
}

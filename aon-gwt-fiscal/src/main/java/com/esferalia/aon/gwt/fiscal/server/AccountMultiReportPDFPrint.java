package com.esferalia.aon.gwt.fiscal.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.FlatAccountEntryDetail;
import com.esferalia.aon.occam.api.model.ReportMetadata;
import com.esferalia.aon.occam.api.model.accounting.BalanceType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import net.aonsolutions.aon.accounting.report.AccountBalanceReportPDF;
import net.aonsolutions.aon.accounting.report.AccountJournalReportPDF;
import net.aonsolutions.aon.accounting.report.AccountLedgerReportPDF;
import net.aonsolutions.aon.accounting.report.AccountOperatingReportPDF;
import net.aonsolutions.aon.accounting.report.AccountReportPdfPageEvent;
import net.aonsolutions.aon.accounting.report.AccountTrialBalanceReportPDF;
import net.aonsolutions.aon.accounting.report.IAccountReportPDF;
import net.aonsolutions.aon.accounting.report.VatReportPDF;

@WebServlet(name = "AccountMultiReport PDF Print", urlPatterns = { "/aon_gwt_fiscal/roms/AccountMultiReportPDFPrint" })
public class AccountMultiReportPDFPrint extends HttpServlet {

	private static final long serialVersionUID = 4787704703612285036L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String accountReportParams = req.getParameter( IRequestParamsNames.ACCOUNT_REPORT_PARAMS );
			AccountingReportParams params = JsonParser.parseAccountingParams(accountReportParams);
			params.setDomain(Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID)));
			params.setUser(req.getParameter(IRequestParamsNames.USER));
			params.setDomainName(req.getParameter(IRequestParamsNames.DOMAIN_NAME));
			
			resp.setContentType(MimeType.PDF.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"Listado Multiple."+ MimeType.PDF.getExtension()+ "\";");
			// FALTA 
//			printMultiReportPDF(resp.getOutputStream(), params);
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");	
	
	private static void printMultiReportPDF(OutputStream out, AccountingReportParams params, List<IAccountReportPDF> reports) throws IOException, DocumentException {
		
		LinkedList<File> files = new LinkedList<File>(); 
		File tempFile = null;
		FileOutputStream destStream = null;
		File runnerTempFile = null;
		FileInputStream input = null;
		try {
			tempFile = File.createTempFile("merge", ".pdf");
			files.add(tempFile);
			System.out.println("Main File ..: " + tempFile);
			PDFMergerUtility document = new PDFMergerUtility();
			destStream = new FileOutputStream(tempFile);
			document.setDestinationStream(destStream);
			
			// Portada
			System.out.println("Portada...");
			runnerTempFile = File.createTempFile("merge", ".pdf");
			files.add(runnerTempFile);
			OutputStream output = new FileOutputStream(runnerTempFile);
			
			params.setTitle("LISTADO MULTIPLE") // FALTA - TITULO POR DEFECTO
				.setShowCover(true);			
			      
			printCover(output, params);
			
			document.addSource(runnerTempFile);
			IOUtils.closeQuietly(output);
			
			params.setShowCover(false); // Los listados propiamente dichos no llevan portada, solo hay una portada principal en el documento completo
			
			int totalPages = 1;
				
			for (IAccountReportPDF report : reports) {
			
				System.out.print(report.getDefaultTitle() + " ... ");
				
				runnerTempFile = File.createTempFile("merge", ".pdf");
				files.add(runnerTempFile);
				output = new FileOutputStream(runnerTempFile);
				
				params.setTitle(report.getDefaultTitle())
					  .setPageOffset(totalPages);
				
				report.printReportPDF(output, params);
				
				// Comprobar cuantas paginas tiene el PDF generado para acumular en el total de paginas
				InputStream in = new FileInputStream(runnerTempFile);
				PdfReader reader = new PdfReader(in);
				totalPages = totalPages + reader.getNumberOfPages();
				System.out.println(reader.getNumberOfPages());
				reader.close();
				
				document.addSource(runnerTempFile);
				IOUtils.closeQuietly(output);
			}				
				
			// Mezclar todos los documentos
			System.out.println("Mezclando documentos...");					
			document.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());
			input = new FileInputStream(tempFile); 
			IOUtils.copy(input, out);
			out.flush();
			
			System.out.println("TERMINADO PDF");
			
		} finally {
			IOUtils.closeQuietly(destStream);
			IOUtils.closeQuietly(input);
			for (File file : files ) {
				FileUtils.deleteQuietly(file);	
			}
		}	
		
		
	}
	
	private static void printCover(OutputStream outputStream, AccountingReportParams params) throws DocumentException {
		
		String domainName = params.getDomainName();
		String user = params.getUser();
		int domainId = params.getDomain();
		
		AonConfiguration config = AON.getConfiguration(domainName, domainId, user);
		Company company = config.getCompany();
		String companyName = company == null ? "" : company.getName();
		
		CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, user);
		if (params.getPeriod() != null) {
			params.setSelectedPeriod(AccountPeriodDAO.getPeriod(ctx, params.getPeriod()));
		}
//		if (params.getAccount() != null) {
//			params.setSelectedAccount(AccountDAO.get(ctx, params.getAccount()));
//		}
		if (params.getActivity() != null) {
			params.setSelectedActivity(CompanyDAO.getEnterpriseActivity(ctx, params.getActivity()));
		}
		ctx.close();
		
		ReportMetadata metadata = params.getReportMetadata()
				.setCompanyName(companyName)
				.setFilterDescription(getFilterDescription(params))
			;
		
		Document document = new Document();
		document.setPageSize(PageSize.A4);
		document.setMargins(30, 30, 50, 30);
		
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		//writer.setPageEmpty(false);
		// FALTA
		//writer.setPageEvent(new AccountReportPdfPageEvent(metadata, true));
		writer.setPageEvent(new AccountReportPdfPageEvent(metadata));
		document.open();

		//document.add(new Chunk(""));		
		
		document.close();

	}
	
	private static String getFilterDescription(AccountingReportParams params) {
		StringBuilder buf = new StringBuilder();
		if (params.getSelectedPeriod() != null) {
			concat(buf, "Ejr.: " + params.getSelectedPeriod().getName() );
		}
		if (params.getFromDate() != null) {
			concat(buf, "Desde: " + DATE_FORMATTER.format(params.getFromDate()) );
		}
		if (params.getToDate() != null) {
			concat(buf, "Hasta: " + DATE_FORMATTER.format(params.getToDate()) );
		}
		if (params.getSelectedActivity() != null) {
			concat(buf, "Act.: " + params.getSelectedActivity().getDescription() );
		}
//		if (params.getSecurityLevel() != null && params.getSecurityLevel() == SecurityLevel.CONFIDENTIAL) {
//			concat(buf, "Seg: CONFID.");	
//		}
//		if (params.getSecurityLevel() != null && params.getSecurityLevel() == SecurityLevel.OFFICIAL) {
//			concat(buf, "Seg: NO CONFID.");	
//		}
		return buf.toString();
	}
	
	private static void concat(StringBuilder buf, String string) {
		if (buf.length() > 0) {
			buf.append(", ");
		}
		buf.append(string);
	}
	
	private static void createZip(File zipFile, File pdfFile) throws IOException {

		System.out.println("Generando Zip...");

		FileInputStream in = new FileInputStream(pdfFile);
		FileOutputStream out = new FileOutputStream(zipFile);
		ZipOutputStream zos = new ZipOutputStream(out);

		try {					
			zos.putNextEntry(new ZipEntry(pdfFile.getName()));
			byte[] buffer = new byte[1024];
			while (true) {
				int readCount = in.read(buffer);
				if (readCount < 0) {
					break;
				}
				zos.write(buffer, 0, readCount);
			}
			zos.closeEntry();
		} finally {
			zos.close();
			in.close();
			out.close();
		}

		System.out.println("TERMINADO ZIP");

	}
	
	public static void main(String[] args) throws IOException, DocumentException, SQLException {
				
		final int DOMAIN_ID = 9253;  
		final String DOMAIN = "prueba-dsi.aonsolutions.dev";
		final String USER = "admin";
		
		// Filtros: Comunes a todos los listados se podría poner desde/hasta fecha y actividad
		AccountingReportParams params = new AccountingReportParams()
				.setDomain(DOMAIN_ID)
				.setDomainName(DOMAIN)
				.setUser(USER)
				.setFromDate(new Date("01/01/2023"))
				.setToDate(new Date("12/31/2023"))
				.setPeriod(21392)  // Ejercicio: Se necesita en la Cuenta de Explotación: Ejercicio 2023 (id de account_period)
				.setLevel(9)   // Nivel de las cuentas (1,2,3,4,9) se usa en "Sumas y Saldos" y "Listado PyG"
		        //.setReports(new LinkedList<IAccountReportPDF>())
		        .setBalanceType(BalanceType.BALANCE_ABBREVIATE);  // Balances Oficiales: Balance de Situación Abreviado
		
		// Reports que se van a incluir
		ArrayList<IAccountReportPDF> reports = new ArrayList<>();		  
		reports.add(new AccountJournalReportPDF()); // Listado diario de movimientos 
		reports.add(new AccountLedgerReportPDF());  // Listado Mayor de Cuentas
		reports.add(new AccountTrialBalanceReportPDF()); // Balance de Sumas y saldos
		reports.add(new AccountOperatingReportPDF()); // Listado PyG
		//params.getReports().add(new AccountBalanceReportPDF());  // Balances oficiales (Balance Abreviado para probar)
//			.setBalanceType(BalanceType.BALANCE_ABBREVIATE);
		reports.add(new VatReportPDF()); // Listado de IVA
				
		File pdfFile = new File("c:\\tmp\\prueba.pdf");		
		FileOutputStream pdfStream = new FileOutputStream(pdfFile);
		
		printMultiReportPDF(pdfStream, params, reports);
		
		File zipFile = new File("c:\\tmp\\prueba.zip");
		createZip(zipFile, pdfFile);		
		
	}
	
//	public static void main(String[] args) {
//		
//		// PRUEBA CONCATENAR DOS ARCHIVOS
//		
//		LinkedList<File> files = new LinkedList<File>(); 
//		File tempFile = null;
//		
//		
////		FileInputStream input = null;
//		FileOutputStream destStream = null;
//		try {
//			//tempFile = File.createTempFile("merge", ".pdf");
//			tempFile = new File("c:\\tmp\\prueba.pdf");
//			 
//			files.add(tempFile);
//			System.out.println("Main File ..: " + tempFile);
//			
//			
//			PDFMergerUtility document = new PDFMergerUtility();
//			destStream = new FileOutputStream(tempFile);
//			document.setDestinationStream(destStream);
//			
//			// Listado Diario de Movimientos
//			//for (IAccountingBookRunner runner : runners ) {
//			File runnerTempFile1 = new File("c:\\tmp\\file1.pdf");
//			File runnerTempFile2 = new File("c:\\tmp\\file2.pdf");
//			//files.add(runnerTempFile);
//			//InputStream output = new FileInputStream(runnerTempFile);
//				
//				
//				
//				// Llamada al report para sacar el diario
////				AccountJournalReportPDF reportPDF = new AccountJournalReportPDF();
////				reportPDF.printBalanceReport(output, params);
//
//				
//				
//				document.addSource(runnerTempFile1);
//				document.addSource(runnerTempFile2);
//				
//				
//				
//				//IOUtils.closeQuietly(output);
//				// FileUtils.deleteQuietly(runnerTempFile);
//			//}
//			// Mezclar todos los documentos
//			document.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());			
//			//input = new FileInputStream(tempFile); 
//			//IOUtils.copy(input, out);
//			//out.flush();
//			
//		} catch (IOException e) {
////			throw new IOException(e);
//		} finally {
//			IOUtils.closeQuietly(destStream);
//			//IOUtils.closeQuietly(input);
//			
//			//for (File file : files ) {
//		//		FileUtils.deleteQuietly(file);	
//			//}
//		}
//		
//	}
	
}

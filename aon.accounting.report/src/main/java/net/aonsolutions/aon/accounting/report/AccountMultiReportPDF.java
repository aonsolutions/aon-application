package net.aonsolutions.aon.accounting.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.itextpdf.text.pdf.PdfReader;

public class AccountMultiReportPDF {

	public void printMultiReportPDF(OutputStream out, LinkedHashMap<IAccountReportPDF,AccountingReportParams> reports) throws IOException {
		
		LinkedList<File> filesToDelete = new LinkedList<File>(); 
		File mainTempFile = File.createTempFile("merge", ".pdf");
		filesToDelete.add(mainTempFile);
		
		System.out.println("Main Temp File ... " + mainTempFile);
		
		try (FileOutputStream mainTempStream = new FileOutputStream(mainTempFile)) {
			
			PDFMergerUtility document = new PDFMergerUtility();			
			document.setDestinationStream(mainTempStream);			
			
			int totalPages = 0;
				
			for (IAccountReportPDF report : reports.keySet()) {
				
				AccountingReportParams params = reports.get(report);
			
				System.out.println(params.getTitle() + " ... ");
				
				File reportTempFile = File.createTempFile("merge", ".pdf");
				try (OutputStream reportTempStream = new FileOutputStream(reportTempFile)) {
				
					filesToDelete.add(reportTempFile);  // Guardamos el fichero para borrarlo al final				
					
					params.setPageOffset(totalPages);  // Para que se imprima el contador de páginas totales del documento				
					report.printReportPDF(reportTempStream, params);
					
					// Comprobar cuantas paginas tiene el PDF generado para acumular en el total de paginas
					totalPages = totalPages + numTotalPages(reportTempFile);
					
					document.addSource(reportTempFile);
				}
			}				
				
			// Mezclar todos los documentos
			System.out.println("Mezclando documentos...");					
			document.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());
			try (FileInputStream input = new FileInputStream(mainTempFile)) { 
				IOUtils.copy(input, out);
				out.flush();
			}
			
			System.out.println("TERMINADO PDF");
			
		} finally {
			// Borrar ficheros temporales
			for (File file : filesToDelete ) {
				FileUtils.deleteQuietly(file);	
			}
		}	
		
	}
	
	private int numTotalPages(File file) throws IOException {

		try (InputStream in = new FileInputStream(file)) {
			PdfReader reader = new PdfReader(in);
			int pages = reader.getNumberOfPages();			
			reader.close();
			return pages;
		}
		
	}

	public void createZip(File zipFile, File pdfFile) throws IOException {

		System.out.println("Generando ZIP...");

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
	
}

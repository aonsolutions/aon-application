package solutions.aon.saltra.api;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Ignore;
import org.junit.Test;

public class SaltraTestCase {

	@Ignore("Not yet")
	@Test
	public void test() throws IOException {
		Saltra saltra = new Saltra("http://127.0.0.1/api/v1");
		saltra.login("cliente@cliente.es", "qwerty");
		try {
			saltra.deleteEmpresa("G8956966");
		} catch ( Throwable  t ) {
			
		}
		saltra.saveEmpresa("SALTRA TEST CASE", "G8956966");
		saltra.saveCertificado("G8956966", "jg@FNMT", new FileInputStream("/tmp/Julio GARCIA - Certificado FNMT.p12"));
		byte idc [] = saltra.getIDC("011005185924", "0111", "01105360062", "01-08-2020");
		
		PDDocument pdDocument = PDDocument.load(idc);
		print(pdDocument);
		
		saltra.deleteEmpresa("G8956966");
		
	}

	
	private static void print(PDDocument doc ) throws IOException {
        AccessPermission ap = doc.getCurrentAccessPermission();
		if (!ap.canExtractContent())
		{
			throw new IOException("You do not have permission to extract text");
		}
		
		PDFTextStripper stripper= new PDFTextStripper();
		
		stripper.setSortByPosition(true);
				
		for (int p = 1; p <= doc.getNumberOfPages(); p++) {
            // Set the page interval to extract. 
			// If we don't, then all pages would be extracted.
			stripper.setStartPage(p);
			stripper.setEndPage(p);
			
			String text = stripper.getText(doc);

						
			System.out.print(text);
			
		}
	}	
}

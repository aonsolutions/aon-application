package solutions.aon.in.invoice.pdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;

import solutions.aon.in.invoice.InvoiceBuilder;
import solutions.aon.in.invoice.InvoiceTemplate;
import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.img.InvoiceIMGParser;
import solutions.aon.in.invoice.templates.Templates;

public class InvoicePDFParser {
	
	public static void parse( File file , InvoiceBuilder<?> handler) throws InvoicePDFException {
		try (PDDocument doc = PDDocument.load(file)) {
			parser(doc, handler);
		} catch (IOException e) {
			throw new InvoicePDFException(e);
		} catch (UnknownInvoiceException e) {
			throw new InvoicePDFException(e);
		}
	}

	public static void parse( InputStream is , InvoiceBuilder<?> handler) throws InvoicePDFException {
		try (PDDocument doc = PDDocument.load(is)) {
			
			parser(doc, handler);
		} catch (IOException e) {
			throw new InvoicePDFException(e);
		} catch (InvoicePDFException e) {
			;
		} catch (UnknownInvoiceException e) {
			throw new InvoicePDFException(e);
		} 
	}
	
	private static void parser(PDDocument doc, InvoiceBuilder<?> handler) throws InvoicePDFException, IOException, UnknownInvoiceException {
        AccessPermission ap = doc.getCurrentAccessPermission();
		if (!ap.canExtractContent()) {
			throw new InvoicePDFException("You do not have permission to extract text");
		}
		PDFTextStripper stripper= new PDFTextStripper();
		stripper.setSortByPosition(true);
		InvoiceTemplate template = null;
		
		
		
		for (int p = 1; p <= doc.getNumberOfPages(); p++) {
            // Set the page interval to extract. If we don't, then all pages would be extracted.
			stripper.setStartPage(p);
			stripper.setEndPage(p);
			
			String text = stripper.getText(doc);
			
			if (isBlank(text)) {
				// try with images ...
				text = getImages(doc,p-1).stream()
				.map(img -> InvoiceIMGParser.extract(img)) 
				.collect(Collectors.joining("\r\n"));
			}
			
			template = Templates.parse(template, text, handler);
		}
		
		
		handler.finalizeParse();
	}
	
	public static Collection<byte[]> getImages ( PDDocument document, int pageIndex) throws IOException {
		LinkedList<byte[]> images  = new LinkedList<byte[]>();
		PDPage page = document.getPage(pageIndex);
        PDResources pdResources = page.getResources();
        for (COSName name : pdResources.getXObjectNames()) {
            PDXObject o = pdResources.getXObject(name);
            if (o instanceof PDImageXObject) {
                PDImageXObject image = (PDImageXObject)o;
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(image.getImage(),"PNG", byteArrayOutputStream);
                images.add(byteArrayOutputStream.toByteArray());
            }
        }
        return images;
	}

	public static Collection<byte[]> getImages ( PDDocument document) throws IOException {
		LinkedList<byte[]> images  = new LinkedList<byte[]>();
        for (PDPage page : document.getPages()) {
            PDResources pdResources = page.getResources();
            for (COSName name : pdResources.getXObjectNames()) {
                PDXObject o = pdResources.getXObject(name);
                if (o instanceof PDImageXObject) {
                    PDImageXObject image = (PDImageXObject)o;
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ImageIO.write(image.getImage(),"PNG", byteArrayOutputStream);
                    images.add(byteArrayOutputStream.toByteArray());
                }
            }
        }
        return images;
	}
	
	private static boolean isBlank( String cs ) {
		if (cs == null || (cs.length()) == 0) {
			return true;
		}
		return cs.trim().length() == 0;		
	}

}

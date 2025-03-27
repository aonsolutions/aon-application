package solutions.aon.in.invoice.pdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.DrawObject;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.state.Concatenate;
import org.apache.pdfbox.contentstream.operator.state.Restore;
import org.apache.pdfbox.contentstream.operator.state.Save;
import org.apache.pdfbox.contentstream.operator.state.SetGraphicsStateParameters;
import org.apache.pdfbox.contentstream.operator.state.SetMatrix;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.jbig2.JBIG2ImageReaderSpi;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.util.Matrix;

import com.esferalia.aon.watson.util.AonStringUtils;
import com.github.jaiimageio.jpeg2000.impl.J2KImageReaderSpi;

import solutions.aon.in.invoice.InvoiceBuilder;
import solutions.aon.in.invoice.InvoiceTemplate;
import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.img.InvoiceIMGParser;
import solutions.aon.in.invoice.templates.Templates;

public class InvoicePDFParser {
	
	static {
		IIORegistry registry = IIORegistry.getDefaultInstance();
		registry.registerServiceProvider(new J2KImageReaderSpi());
		registry.registerServiceProvider(new JBIG2ImageReaderSpi());
	}

	private InvoicePDFParser() {
	}
	
	public static void parse( File file , InvoiceBuilder<?> handler) throws InvoicePDFException {
		try (PDDocument doc = Loader.loadPDF(file)) {
			parser(doc, handler);
		} catch (IOException | UnknownInvoiceException e) {
			throw new InvoicePDFException(e);
		} 
	}

	public static void parse( InputStream is , InvoiceBuilder<?> handler) throws InvoicePDFException {
		try (PDDocument doc = Loader.loadPDF(is.readAllBytes())) {
			parser(doc, handler);
		} catch (IOException | UnknownInvoiceException e) {
			throw new InvoicePDFException(e);
		} catch (InvoicePDFException e) {
			
		}  
	}
	
	private static void parser(PDDocument doc, InvoiceBuilder<?> handler) throws InvoicePDFException, IOException, UnknownInvoiceException {
        AccessPermission ap = doc.getCurrentAccessPermission();
		if (!ap.canExtractContent()) {
			throw new InvoicePDFException("You do not have permission to extract text");
		}
		PDFTextStripper stripper= new PDFTextStripper();
		stripper.setSortByPosition(true);
		String text = stripper.getText(doc);
		InvoiceTemplate template = (!isBlank(text))
			?Templates.parse(null, text, handler)
			:null;
		if (!handler.isMinInfoSet() && doc.getNumberOfPages() >= 1) {
			for ( byte[] image : getImages(doc,0) ) {
				String t = InvoiceIMGParser.extract( image );
				if (AonStringUtils.isNotEmpty(t)) {
					Templates.parse(template, t, handler);		
				}
				if ( handler.isMinInfoSet() ) break;
			}
		}
		handler.finalizeParse();
	}
	
	private static boolean isParseable(PDPage page, float[] imageSize) {
		if (imageSize == null || imageSize.length == 0) return false;
		float ph = page.getMediaBox().getHeight();
		float pw = page.getMediaBox().getWidth();
		float iw = imageSize[0];
		float ih = imageSize[1];
		float widthRatio = iw / pw;   
		float heightRatio = ih / ph;
        return widthRatio > 0.1 
    		&& heightRatio > 0.1;
	}	

	public static Collection<byte[]> getImages ( PDDocument document, int pageIndex) throws IOException {
		LinkedList<byte[]> images  = new LinkedList<>();
		PDPage page = document.getPage(pageIndex);

		ImageSizeExtractor imageSizeExtractor = new ImageSizeExtractor();
	    imageSizeExtractor.processPage(page);
	    Map<String, float[]> imageSizeMap = imageSizeExtractor.getPageImages();
		
		PDResources pdResources = page.getResources();
        for (COSName name : pdResources.getXObjectNames()) {
            PDXObject o = pdResources.getXObject(name);
            if (o instanceof PDImageXObject image &&
                isParseable(page, imageSizeMap.get(name.getName()) )) {
            		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            		ImageIO.write(image.getImage(),"PNG", byteArrayOutputStream);
            		images.add(byteArrayOutputStream.toByteArray());
            }
        }
        return images;
	}

//	public static Collection<byte[]> getImages ( PDDocument document) throws IOException {
//		LinkedList<byte[]> images  = new LinkedList<>();
//        for (PDPage page : document.getPages()) {
//    		ImageSizeExtractor imageSizeExtractor = new ImageSizeExtractor();
//    	    imageSizeExtractor.processPage(page);
//    	    Map<String, float[]> imageSizeMap = imageSizeExtractor.getPageImages();
//    	    
//            PDResources pdResources = page.getResources();
//            for (COSName name : pdResources.getXObjectNames()) {
//                PDXObject o = pdResources.getXObject(name);
//                if (o instanceof PDImageXObject image) {
//                	if (isParseable(page, imageSizeMap.get(name.getName()) )) {
//	                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//	                    ImageIO.write(image.getImage(),"PNG", byteArrayOutputStream);
//	                    images.add(byteArrayOutputStream.toByteArray());
//                	}
//                }
//            }
//        }
//        return images;
//	}
	
	private static boolean isBlank( String cs ) {
		if (cs == null || (cs.length()) == 0) {
			return true;
		}
		return cs.trim().length() == 0;		
	}

	private static class ImageSizeExtractor extends PDFStreamEngine {

	    private Map<String, float[]> pageImages;

	    public ImageSizeExtractor() throws IOException {
	        // preparing PDFStreamEngine
	        addOperator(new Concatenate(this));
	        addOperator(new DrawObject(this));
	        addOperator(new SetGraphicsStateParameters(this));
	        addOperator(new Save(this));
	        addOperator(new Restore(this));
	        addOperator(new SetMatrix(this));
	        pageImages = new HashMap<>();
	    }

	    public Map<String, float[]> getPageImages() {
	        return pageImages;
	    }

	    @Override
	    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
	        String operation = operator.getName();
	        if ("Do".equals(operation)) {
	            COSName objectName = (COSName) operands.get(0);
	            // get the PDF object
	            PDXObject xobject = getResources().getXObject(objectName);
	            // check if the object is an image object
	            if (xobject instanceof PDImageXObject) {
	                Matrix ctmNew = getGraphicsState().getCurrentTransformationMatrix();
	                float imageXScale = ctmNew.getScalingFactorX();
	                float imageYScale = ctmNew.getScalingFactorY();
	                float[] xy = {imageXScale,imageYScale};
	                pageImages.put(objectName.getName(), xy);
	            } 
	        } else {
	            super.processOperator(operator, operands);
	        }
	    }

	}
}

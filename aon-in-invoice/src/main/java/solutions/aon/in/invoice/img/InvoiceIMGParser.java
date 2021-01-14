package solutions.aon.in.invoice.img;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;
import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.BoundingBox;
import com.amazonaws.services.textract.model.DetectDocumentTextRequest;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.amazonaws.services.textract.model.Document;

import solutions.aon.in.invoice.InvoiceBuilder;
import solutions.aon.in.invoice.UnknownInvoiceException;

public class InvoiceIMGParser {

	public static void parse( File file , InvoiceBuilder<?> handler) throws InvoiceIMGException {
		try (InputStream is = new FileInputStream(file)) {
			parse(is, handler);
		} catch (IOException e) {
			throw new InvoiceIMGException(e);
		} 
	}


	public static void parse( InputStream is , InvoiceBuilder<?> handler) throws InvoiceIMGException {
		try {
			parser(new Document().withBytes(ByteBuffer.wrap(is.readAllBytes())), handler);
		} catch (IOException e) {
			throw new InvoiceIMGException(e);
		} catch (UnknownInvoiceException e) {
			throw new InvoiceIMGException(e);
		} 
	}

	private static void parser(Document doc , InvoiceBuilder<?> handler) throws InvoiceIMGException, IOException, UnknownInvoiceException {
		AmazonTextract client = AmazonTextractClientBuilder.defaultClient();
		DetectDocumentTextRequest detectDocumentTextRequest = 
		new DetectDocumentTextRequest().withDocument(doc);
		
		DetectDocumentTextResult detectDocumentTextResult = client.detectDocumentText(detectDocumentTextRequest);
		
		List<Block> blocks = detectDocumentTextResult.getBlocks();
		//Collections.sort(blocks, InvoiceIMGParser::compare ) ;
		
		blocks.stream()
		.filter(b -> b.getText() != null )
		.filter(b -> b.getBlockType().equals("LINE"))
		.forEach(b -> System.out.println(getBottom(b) + ":" +  b.getText() ));
//		.collect(Collectors.toMap(InvoiceIMGParser::getBottom, b -> b.getText() , (t1,t2) -> t1 + t2 ));
		
		
	}

	private static int compare(Block b1, Block b2) {
		return compare(b1.getGeometry().getBoundingBox(), b2.getGeometry().getBoundingBox());
	}
	
	private static int compare(BoundingBox b1, BoundingBox b2) {
		float top = b1.getTop() - b2.getTop();
		if ( top < 0 ) 
			return -1;
		if ( top > 0 )
			return 1;
		
		float left = b1.getLeft() - b2.getLeft();
		if ( left < 0 ) 
			return -1;
		if ( left > 0 )
			return 1;
		
		return 0;
	}
	
	private static float getBottom(Block b) {
		BoundingBox box = b.getGeometry().getBoundingBox();
		return box.getTop() + box.getHeight();
	}
	
	

}

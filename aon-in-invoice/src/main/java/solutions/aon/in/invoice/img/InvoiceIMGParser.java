package solutions.aon.in.invoice.img;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.stream.Collectors;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;
import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.DetectDocumentTextRequest;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.amazonaws.services.textract.model.Document;

import solutions.aon.in.invoice.InvoiceBuilder;
import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.templates.Templates;

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

	public static String extract( byte[] bytes ) throws InvoiceIMGException {
		return extract(new Document().withBytes(ByteBuffer.wrap(bytes)));
	}
	
	
	private static void parser(Document doc , InvoiceBuilder<?> handler) throws InvoiceIMGException, IOException, UnknownInvoiceException {
		String text = extract(doc);
		Templates.parse(text, handler);		
		handler.finalizeParse();
	}


	private static String extract(Document doc) {
		if (doc != null 
			&& doc.getBytes() != null 
			&& (doc.getBytes().position() + doc.getBytes().remaining()) > (5*1024*1024)) {
			throw new InvoiceIMGException("Las imagenes a analizar, no pueden superar los 5MB de tamaño");		
		}

		AmazonTextract client = AmazonTextractClientBuilder
			.standard()
			.withRegion(Regions.EU_WEST_1)
			.build();
		DetectDocumentTextRequest detectDocumentTextRequest = 
		new DetectDocumentTextRequest().withDocument(doc);
		
		DetectDocumentTextResult detectDocumentTextResult = client.detectDocumentText(detectDocumentTextRequest);
		
		
		detectDocumentTextResult.getBlocks().stream()
		.filter(b -> b.getText() != null )
		.filter(b -> b.getBlockType().equals("LINE"))
		.forEach(b -> {
			
		});
		
		Block blocks[] = detectDocumentTextResult.getBlocks()
			.stream()
			.filter(b -> b.getText() != null )
			.filter(b -> b.getBlockType().equals("LINE"))
			.toArray(Block[]::new);
		
		String text = null;
		if (blocks != null && blocks.length > 0 ) {
			LinkedList<Block> lines = new LinkedList<Block>();
			for ( int i = 0; i < 1 ; i++  ) {
				lines.add(blocks[i]);
			}			
			for ( int i = 1; i < blocks.length; i++  ) {
				Block block = blocks[i];
				Block line = lines.peekLast();
				if ( intersects(line, block)) 
					line.setText(line.getText() + " " + block.getText() );
				else 
					lines.add(block);
			}
			text = lines.stream().map(b -> b.getText()).collect(Collectors.joining("\r\n"));
		}
		return text;
	}

//	private static int compare(Block b1, Block b2) {
//		return compare(b1.getGeometry().getBoundingBox(), b2.getGeometry().getBoundingBox());
//	}
	
//	private static int compare(BoundingBox b1, BoundingBox b2) {
//		float top = b1.getTop() - b2.getTop();
//		if ( top < 0 ) 
//			return -1;
//		if ( top > 0 )
//			return 1;
//		
//		float left = b1.getLeft() - b2.getLeft();
//		if ( left < 0 ) 
//			return -1;
//		if ( left > 0 )
//			return 1;
//		
//		return 0;
//	}
	
//	private static float getTop(Block b) {
//		return b.getGeometry().getBoundingBox().getTop();
//	}
	
	private static boolean intersects(Block b1, Block b2) {
		
		float top1 = b1.getGeometry().getBoundingBox().getTop();
		float height1 = b1.getGeometry().getBoundingBox().getHeight();
		
		float top2 =  b2.getGeometry().getBoundingBox().getTop();
		if ( Math.abs(top2 -top1 ) <= height1 /2.00)
			return true;
		
//		float bottom2 =  top2 + b2.getGeometry().getBoundingBox().getHeight();
//		if ( bottom2 >= top1 && bottom2 <= bottom1)
//			return true;
		
		return false;
	}
	
	

}

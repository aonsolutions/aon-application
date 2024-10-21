package solutions.aon.in.invoice.img;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextRequest;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextResponse;
import software.amazon.awssdk.services.textract.model.Document;
import solutions.aon.in.invoice.InvoiceBuilder;
import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.templates.Templates;

public class InvoiceIMGParser {

	private InvoiceIMGParser() {
		
	}
	
	private static TextractClient getClient() {
		return TextractClient.create();
	}
	
	public static void parse( File file , InvoiceBuilder<?> handler) throws InvoiceIMGException {
		try (InputStream is = new FileInputStream(file)) {
			parse(is, handler);
		} catch (IOException e) {
			throw new InvoiceIMGException(e);
		} 
	}


	public static void parse( InputStream is , InvoiceBuilder<?> handler) throws InvoiceIMGException {
		try {
			Document document = Document.builder().bytes(SdkBytes.fromByteArray(is.readAllBytes())).build();
			parser(document, handler);
		} catch (IOException e) {
			throw new InvoiceIMGException(e);
		} catch (UnknownInvoiceException e) {
			throw new InvoiceIMGException(e);
		} 
	}

	public static String extract( byte[] bytes ) throws InvoiceIMGException {
		Document document = Document.builder().bytes(SdkBytes.fromByteArray(bytes)).build();
		return extract(document);
	}
	
	private static void parser(Document doc , InvoiceBuilder<?> handler) throws InvoiceIMGException, IOException, UnknownInvoiceException {
		String text = extract(doc);
		Templates.parse(text, handler);		
		handler.finalizeParse();
	}


	private static String extract(Document doc) {
		if (doc != null 
			&& doc.bytes() != null 
			&& (doc.bytes().asByteBuffer().position() + doc.bytes().asByteBuffer().remaining()) > (10*1024*1024)) {
			throw new InvoiceIMGException("Las imagenes a analizar, no pueden superar los 5MB de tamaño");		
		}

		TextractClient client = getClient();
		DetectDocumentTextRequest detectDocumentTextRequest = DetectDocumentTextRequest.builder().document(doc).build();
		
		DetectDocumentTextResponse detectDocumentTextResult = client.detectDocumentText(detectDocumentTextRequest);
		
		Block[] blocks = detectDocumentTextResult.blocks().stream()
			.filter(b -> b.text() != null)
			.filter(b -> "LINE".equals(b.blockType().name()))
			.toArray(Block[]::new);
		
		String text = null;
		if (blocks != null && blocks.length > 0 ) {
			LinkedList<Block> lines = new LinkedList<>();
			lines.addAll(Arrays.asList(blocks));

			for ( int i = 1; i < blocks.length; i++  ) {
				Block block = blocks[i];
				Block line = lines.peekLast();
				if (intersects(line, block)) {
					Block newLine = Block.builder()
						.text(line.text() + " " + block.text())
						.geometry(line.geometry())
						.blockType(line.blockType())
						.build();
					lines.remove(line);
					lines.add(newLine);
				} else lines.add(block);
			}
			text = lines.stream().map(Block::text).collect(Collectors.joining("\r\n"));
		}
		return text;
	}

	private static boolean intersects(Block b1, Block b2) {
		float top1 = b1.geometry().boundingBox().top();
		float height1 = b1.geometry().boundingBox().height();
		
		float top2 =  b2.geometry().boundingBox().top();
		
		return Math.abs(top2 -top1 ) <= height1 /2.00;
	}
}

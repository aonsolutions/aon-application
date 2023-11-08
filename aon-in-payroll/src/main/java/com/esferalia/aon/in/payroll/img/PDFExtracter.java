package com.esferalia.aon.in.payroll.img;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;
import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.DetectDocumentTextRequest;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.amazonaws.services.textract.model.Document;
import com.esferalia.aon.in.payroll.img.PersonDocumentExtracters.IPersonDocumentExtracter;
import com.esferalia.aon.in.payroll.pdf.util.PDFTextStripper;

public class PDFExtracter implements IPersonDocumentExtracter {

	@Override
	public boolean accept(InputStream is) {
		  try {
	            PDDocument doc = Loader.loadPDF(is);
	            return (doc != null);
	        } catch (IOException e) {
	            return false;
	        }
	}

	@Override
	public String extract(byte [] bytes) {
		String text = "";
		try {
			PDDocument document = Loader.loadPDF(bytes);			
			PDFTextStripper stripper = new PDFTextStripper();
			text = stripper.getText(document);
			if (isBlank(text)) {
				text = getImages(document).stream().map(this::extractImage)
						.collect(Collectors.joining(System.lineSeparator()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new PersonDocumentExtractException("Error al procesar el pdf");
		}
		return text;
	}
	
	private static boolean isBlank(String cs) {
		if (cs == null || (cs.length()) == 0) {
			return true;
		}
		return cs.trim().length() == 0;
	}

	private static Collection<byte[]> getImages(PDDocument document) throws IOException {
		PersonDocumentParserValidation.validatePDDoc(document);
		LinkedList<byte[]> images = new LinkedList<>();
		for (PDPage page : document.getPages()) {
			PDResources pdResources = page.getResources();
			for (COSName name : pdResources.getXObjectNames()) {
				PDXObject o = pdResources.getXObject(name);
				if (o instanceof PDImageXObject) {
					PDImageXObject image = (PDImageXObject) o;
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					ImageIO.write(image.getImage(), "PNG", byteArrayOutputStream);
					images.add(byteArrayOutputStream.toByteArray());
				}
			}
		}
		return images;
	}

	private String extractImage(byte[] bytes) {
		PersonDocumentParserValidation.validateBytes(bytes);
		return extract(new Document().withBytes(ByteBuffer.wrap(bytes)));
	}

	private String extract(Document doc) {

		AmazonTextract client = AmazonTextractClientBuilder.defaultClient();

		PersonDocumentParserValidation.validateDoc(doc);

		DetectDocumentTextRequest detectDocumentTextRequest = new DetectDocumentTextRequest().withDocument(doc);

		DetectDocumentTextResult detectDocumentTextResult = client.detectDocumentText(detectDocumentTextRequest);

		detectDocumentTextResult.getBlocks().stream().filter(b -> b.getText() != null)
				.filter(b -> b.getBlockType().equals("LINE")).forEach(b -> {

				});

		Block[] blocks = detectDocumentTextResult.getBlocks().stream().filter(b -> b.getText() != null)
				.filter(b -> b.getBlockType().equals("LINE")).toArray(Block[]::new);

		String extract = null;

		if (blocks != null && blocks.length > 0) {
			LinkedList<Block> lines = new LinkedList<>();
			for (int i = 0; i < blocks.length; i++) {
				lines.addAll(Arrays.asList(blocks));
			}
			for (int i = 1; i < blocks.length; i++) {
				Block block = blocks[i];
				Block line = lines.peekLast();
				if (intersects(line, block))
					line.setText(line.getText() + " " + block.getText());
				else
					lines.add(block);
				extract = lines.stream().map(Block::getText).collect(Collectors.joining("\r\n"));
			}

		}
		return extract;
	}

	private static boolean intersects(Block b1, Block b2) {
		float top1 = b1.getGeometry().getBoundingBox().getTop();
		float height1 = b1.getGeometry().getBoundingBox().getHeight();
		float top2 = b2.getGeometry().getBoundingBox().getTop();

		return (Math.abs(top2 - top1) <= height1 / 2.00);

	}
}

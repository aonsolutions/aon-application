package com.esferalia.aon.in.payroll.img;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.esferalia.aon.in.payroll.img.PersonDocumentExtracters.IPersonDocumentExtracter;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextRequest;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextResponse;
import software.amazon.awssdk.services.textract.model.Document;

class ImageExtracter implements IPersonDocumentExtracter {

	@Override
	public boolean accept(byte [] bytes) {
		try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
			ImageIO.read(is);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public String extract(byte [] bytes) {
		return extractImage(bytes);
	}

	private String extractImage(byte[] bytes) {
		PersonDocumentParserValidation.validateBytes(bytes);
		Document document = Document.builder().bytes(SdkBytes.fromByteArray(bytes)).build();
		return extract(document);
	}

	private String extract(Document doc) {

		TextractClient client = TextractClient.builder().region(Region.EU_WEST_1).build();
		PersonDocumentParserValidation.validateDoc(null);
		PersonDocumentParserValidation.validateDoc(doc);

		DetectDocumentTextRequest detectDocumentTextRequest = DetectDocumentTextRequest.builder().document(doc).build();
		DetectDocumentTextResponse detectDocumentTextResult = client.detectDocumentText(detectDocumentTextRequest);

		
		Block[] blocks = detectDocumentTextResult.blocks().stream()
				.filter(b -> b.text() != null)
				.filter(b -> "LINE".equals(b.blockType().name()))
				.toArray(Block[]::new);
		
		String extract = null;
 
		if (blocks != null && blocks.length > 0) {
			LinkedList<Block> lines = new LinkedList<>();
			for (int i = 0; i < blocks.length; i++) {
				lines.addAll(Arrays.asList(blocks));
			}
			for (int i = 1; i < blocks.length; i++) {
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
				extract = lines.stream().map(Block::text).collect(Collectors.joining("\r\n"));
			}
		}
		return extract;
	}

	private static boolean intersects(Block b1, Block b2) {
		float top1 = b1.geometry().boundingBox().top();
		float height1 = b1.geometry().boundingBox().height();
		float top2 = b2.geometry().boundingBox().top();

		return (Math.abs(top2 - top1) <= height1 / 2.00);
	}
}

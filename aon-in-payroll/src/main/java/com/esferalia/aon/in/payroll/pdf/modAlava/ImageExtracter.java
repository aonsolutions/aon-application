package com.esferalia.aon.in.payroll.pdf.modAlava;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;
import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.DetectDocumentTextRequest;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.amazonaws.services.textract.model.Document;
import com.esferalia.aon.in.payroll.pdf.modAlava.ModelDocumentExtracters.IModelDocumentExtracter;

class ImageExtracter implements IModelDocumentExtracter  {

	public boolean accept(byte [] bytes) {
		try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
			ImageIO.read(is);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public String extract(byte [] bytes) {
		return extractImage(bytes);
	}

	private String extractImage(byte[] bytes) {
		return extract(new Document().withBytes(ByteBuffer.wrap(bytes)));
	}

	private String extract(Document doc) {

		AmazonTextract client = AmazonTextractClientBuilder.defaultClient();


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

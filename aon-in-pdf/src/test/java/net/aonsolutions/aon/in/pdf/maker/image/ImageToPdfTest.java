package net.aonsolutions.aon.in.pdf.maker.image;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.in.pdf.maker.exception.CanNotCreatePdfException;

public class ImageToPdfTest {

	@Test
	public void test1() throws CanNotCreatePdfException, IOException {
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("ref_homologation", "000000");
		metadata.put("software_name", "Aon Solutions");
		metadata.put("software_version", "9.23");
		metadata.put("timestamp", AonDateUtils.format(new Date(), "hh:mm dd/MM/yyyy"));

		byte[] image = ImageToPdfTest.class.getResourceAsStream("image1.jpg").readAllBytes();
		try (ImageToPdf imageToPdf = new ImageToPdf(image, metadata)) {
			imageToPdf.save(new FileOutputStream("./ImageToPdfTest1.pdf"));
		}
	}
	
	@Test
	public void test2() throws CanNotCreatePdfException, IOException {
		byte[] image = ImageToPdfTest.class.getResourceAsStream("image2.jpg").readAllBytes();
		try (ImageToPdf imageToPdf = new ImageToPdf(image)) {
			imageToPdf.save(new FileOutputStream("./ImageToPdfTest2.pdf"));
		}
	}
	
	@Test
	public void test3() throws CanNotCreatePdfException, IOException {
		byte[] image = ImageToPdfTest.class.getResourceAsStream("image3.jpeg").readAllBytes();
		try (ImageToPdf imageToPdf = new ImageToPdf(image)) {
			imageToPdf.save(new FileOutputStream("./ImageToPdfTest3.pdf"));
		}
	}
	
	@Test
	public void test4() throws CanNotCreatePdfException, IOException {
		byte[] image = ImageToPdfTest.class.getResourceAsStream("image4.jpeg").readAllBytes();
		try (ImageToPdf imageToPdf = new ImageToPdf(image)) {
			imageToPdf.save(new FileOutputStream("./ImageToPdfTest4.pdf"));
		}
	}
	
	@Test
	public void test5() throws CanNotCreatePdfException, IOException {
		byte[] image = ImageToPdfTest.class.getResourceAsStream("image5.jpeg").readAllBytes();
		try (ImageToPdf imageToPdf = new ImageToPdf(image)) {
			imageToPdf.save(new FileOutputStream("./ImageToPdfTest5.pdf"));
		}
	}
	
	@Test
	public void test6() throws CanNotCreatePdfException, IOException {
		byte[] image = ImageToPdfTest.class.getResourceAsStream("image6.png").readAllBytes();
		try (ImageToPdf imageToPdf = new ImageToPdf(image)) {
			imageToPdf.save(new FileOutputStream("./ImageToPdfTest6.pdf"));
		}
	}
	
	@Test
	public void test7() throws CanNotCreatePdfException, IOException {
		byte[] image = ImageToPdfTest.class.getResourceAsStream("image7.jpeg").readAllBytes();
		try (ImageToPdf imageToPdf = new ImageToPdf(image)) {
			imageToPdf.save(new FileOutputStream("./ImageToPdfTest7.pdf"));
		}
	}
	
	@Test
	public void test8() throws CanNotCreatePdfException, IOException {
		byte[] image = ImageToPdfTest.class.getResourceAsStream("image8.jpg").readAllBytes();
		try (ImageToPdf imageToPdf = new ImageToPdf(image)) {
			imageToPdf.save(new FileOutputStream("./ImageToPdfTest8.pdf"));
		}
	}
	
	@Test
	public void testAll() throws CanNotCreatePdfException, IOException {
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("ref_homologation", "000000");
		metadata.put("software_name", "Aon Solutions");
		metadata.put("software_version", "9.23");
		metadata.put("timestamp", AonDateUtils.format(new Date(), "hh:mm dd/MM/yyyy"));

		byte[] image1 = ImageToPdfTest.class.getResourceAsStream("image1.jpg").readAllBytes();
		byte[] image2 = ImageToPdfTest.class.getResourceAsStream("image2.jpg").readAllBytes();
		byte[] image3 = ImageToPdfTest.class.getResourceAsStream("image3.jpeg").readAllBytes();
		byte[] image4 = ImageToPdfTest.class.getResourceAsStream("image4.jpeg").readAllBytes();
		byte[] image5 = ImageToPdfTest.class.getResourceAsStream("image5.jpeg").readAllBytes();
		byte[] image6 = ImageToPdfTest.class.getResourceAsStream("image6.png").readAllBytes();
		byte[] image7 = ImageToPdfTest.class.getResourceAsStream("image7.jpeg").readAllBytes();
		byte[] image8 = ImageToPdfTest.class.getResourceAsStream("image8.jpg").readAllBytes();

		List<byte[]> images = new LinkedList<>();
		images.add(image1);
		images.add(image2);
		images.add(image3);
		images.add(image4);
		images.add(image5);
		images.add(image6);
		images.add(image7);
		images.add(image8);

		try (ImageToPdf imageToPdf = new ImageToPdf(images, metadata)) {
			imageToPdf.save(new FileOutputStream("./ImageToPdfTestAll.pdf"));
		}
	}
}

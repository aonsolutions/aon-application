package com.esferalia.aon.gwt.payroll.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.code.aon.ui.common.session.MockHttpServletResponse;
import com.esferalia.aon.in.payroll.img.DNIParser;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.annotation.MultipartConfig;

@MultipartConfig
public class DNIServletTestImpl extends DNIServlet {

	private static final long serialVersionUID = 6424878288942526801L;

	@Test
	public void fillJsonNullTest() {

		MockHttpServletResponse response = new MockHttpServletResponse();
		DNIServlet ds = new DNIServlet();
		AonCoreException e = assertThrows(AonCoreException.class, () -> ds.fillJson(response, null));
		assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());
		System.out.println(e.getMessage());
	}

	@Test
	public void fillJsonNextLineNull() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		DNIServlet ds = new DNIServlet();
		InputStream is = new FileInputStream("/tmp/DNIJordi.jpg");
		byte[] bytes = IOUtils.toByteArray(is);
		String text = DNIParser.extractImage(bytes);
		String[] lineas = text.split("\n");
		AonCoreException e = assertThrows(AonCoreException.class, () -> ds.fillJson(response, lineas));
		assertEquals(AonError.NULL_NEXT_LINE.getMessage(), e.getMessage());

	}
}

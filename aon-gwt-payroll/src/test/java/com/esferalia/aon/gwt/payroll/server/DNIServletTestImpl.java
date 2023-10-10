package com.esferalia.aon.gwt.payroll.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.code.aon.ui.common.session.MockHttpServletResponse;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.annotation.MultipartConfig;


@MultipartConfig
public class DNIServletTestImpl extends DNIServlet{

	private static final long serialVersionUID = 6424878288942526801L;

	@Test
	public void fillJsonNullTest(){

	MockHttpServletResponse response = new MockHttpServletResponse();
	DNIServlet ds = new DNIServlet();
	AonCoreException e =
	assertThrows(AonCoreException.class,() -> ds.fillJson(response, null)); 
	assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());
	System.out.println(e.getMessage());

 	}
}

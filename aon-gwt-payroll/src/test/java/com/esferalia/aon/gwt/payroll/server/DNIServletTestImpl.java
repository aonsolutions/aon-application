package com.esferalia.aon.gwt.payroll.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.code.aon.ui.common.session.MockHttpServletResponse;
import com.esferalia.aon.gwt.payroll.shared.EmployeeData;
import com.esferalia.aon.in.payroll.img.DNIParser;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@MultipartConfig
public class DNIServletTestImpl extends DNIServlet{

	@Test
	public void fillJsonNullTest(){

	MockHttpServletResponse response = new MockHttpServletResponse();
	DNIServlet ds = new DNIServlet();
	AonCoreException e =
	assertThrows(AonCoreException.class,() ->	ds.fillJson(response, null));
	assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());

 	}
}

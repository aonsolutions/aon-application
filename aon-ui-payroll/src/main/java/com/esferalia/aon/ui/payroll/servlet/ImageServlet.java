package com.esferalia.aon.ui.payroll.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.Classpath;
import com.code.aon.common.util.ImageUtil;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.utils.PdfToImage;

public class ImageServlet extends HttpServlet implements IPayrollConstants{
	private static final long serialVersionUID = 1L;

	public ImageServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String page = request.getServletPath();
		page = StringUtils.substringBetween(page, "/", ".");
		if(StringUtils.isEmpty(page)){
			throw new IllegalArgumentException("Pagina de contrato desconocida");
		}
		String model = request.getParameter("model");
//		Integer zoom = Integer.parseInt(request.getParameter("zoom"));
		Integer width = Integer.parseInt(request.getParameter("width"));
		Integer height = Integer.parseInt(request.getParameter("height"));
		if (StringUtils.isEmpty(model)) {
			throw new IllegalArgumentException("Modelo vacio");
		}
		final String SCHEMA = model+".pdf"; 
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = Classpath.search(cl, MODEL_PATH, SCHEMA);
//		BufferedImage pic = PdfToImage.create(urls[0], Integer.parseInt(page), width.intValue(), height.intValue());
		BufferedImage pic = PdfToImage.getImage();
		byte[] buffer = ImageUtil.getImage(pic,MimeType.MIME_PNG.getExtension());
		InputStream in = new ByteArrayInputStream(buffer);
		int bytes = in.read(buffer);
		while (bytes != -1) {
			response.getOutputStream().write(buffer, 0, bytes);
			bytes = in.read(buffer);
		}
		in.close();
		response.setContentType(MimeType.MIME_PNG.getName()); 
		response.flushBuffer();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		throw new ServletException("POST not supported");
	}

}
package com.esferalia.aon.ui.payroll.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.Classpath;
import com.code.aon.common.util.ImageUtil;
import com.esferalia.aon.ui.payroll.utils.PdfToImage;

public class ImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ImageServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String model = request.getParameter("model");
		if (StringUtils.isEmpty(model)) {
			throw new IllegalArgumentException("Modelo vacio");
		}
		String page = request.getServletPath();
		page = StringUtils.substringBetween(page, "/", ".");
		if(StringUtils.isEmpty(page)){
			throw new IllegalArgumentException("Pagina de contrato desconocida");
		}
//		String pFile = "C:\\TMP\\a.pdf"; 
//		this.getClass().getResourceAsStream( "/com/code/aon/ui/payroll/logo.jpg" );
//		this.getClass().getResource( pFile ).getContent();
//		String pFile = "com/esferalia/aon/ui/payroll/contractModel/"+model+".pdf"; 
		
//		String pFile = "/TMP/"+model+".pdf"; 
		final String SCHEMA = model+".pdf"; 
		
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = Classpath.search(cl, "com/esferalia/aon/ui/payroll/contractModel/", SCHEMA);
		
		List<BufferedImage> pics = PdfToImage.create(urls[0]);
//		List<BufferedImage> pics = PdfToImage.create(pFile);
		BufferedImage pic = pics.get(Integer.parseInt(page)-1);
		byte[] buffer = ImageUtil.getImage(pic,MimeType.MIME_PNG.getExtension());
		InputStream in = new ByteArrayInputStream(buffer);
		int bytes = in.read(buffer);
		while (bytes != -1) {
			response.getOutputStream().write(buffer, 0, bytes);
			bytes = in.read(buffer);
		}
		in.close();
		response.setContentType(MimeType.MIME_JPEG.getName()); // Formato de la imagen
		response.flushBuffer();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		throw new ServletException("POST not supported");
	}

}
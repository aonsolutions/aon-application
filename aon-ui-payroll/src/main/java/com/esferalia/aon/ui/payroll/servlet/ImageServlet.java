package com.esferalia.aon.ui.payroll.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.ImageUtil;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.utils.PdfUtils;

public class ImageServlet extends HttpServlet implements IPayrollConstants{
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public ImageServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		BufferedImage contractWallpaper = PdfUtils.getPdfWallpaperImage();
		if (contractWallpaper==null) {
			throw new IllegalArgumentException("Se ha producido un error al obtener la página del contrato.");
		}
		byte[] buffer = ImageUtil.getImage(contractWallpaper,MimeType.MIME_PNG.getExtension());
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
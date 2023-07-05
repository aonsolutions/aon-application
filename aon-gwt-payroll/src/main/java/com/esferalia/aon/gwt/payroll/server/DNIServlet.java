package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import com.amazonaws.util.IOUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeData;
import com.esferalia.aon.in.payroll.img.DNIParser;
import com.esferalia.aon.in.payroll.img.MyDniDataListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet(urlPatterns = "/DNIServlet")
@MultipartConfig
public class DNIServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		MyDniDataListener listener = new MyDniDataListener();
		Part filePart = req.getPart("archivo");
		String fileName = filePart.getSubmittedFileName();
		System.out.println(fileName);

		EmployeeData ed = new EmployeeData();
		String text;
		String json;
		PrintWriter os;
		ObjectMapper objectMapper = new ObjectMapper();

		int action;

		if (fileName.contains(".pdf")) {
			action = 0;
		} else if (fileName.contains(".jpg") || fileName.contains(".png")) {
			action = 1;
		} else {
			action = 2;
		}
		switch (action) {

		case 0:
			InputStream is = filePart.getInputStream();

			DNIParser.parse(is);
			text = DNIParser.getText();
			DNIParser.getNewDniBothPdf(text, listener);
			ed.setDni(DNIParser.dni.replaceAll("\\r", ""));
			ed.setNacionalidad(DNIParser.nacionalidad.replaceAll("\\r", ""));
			ed.setNombre(DNIParser.nombre.replaceAll("\\r", ""));
			ed.setApellido1(DNIParser.apellido1.replaceAll("\\r", ""));
			ed.setApellido2(DNIParser.apellido2.replaceAll("\\r", ""));

			json = objectMapper.writeValueAsString(ed);

			resp.setContentType("text/html");

			os = resp.getWriter();

			os.println(json);
			os.flush();
			os.close();

			System.out.println(json);
			
			break;

		case 1:
			InputStream prueba2 = filePart.getInputStream();
			byte[] bytes = IOUtils.toByteArray(prueba2);
			text = DNIParser.extractImage(bytes);
			DNIParser.getNewDniBothJpg(text, listener);

			ed.setDni(DNIParser.dni.replaceAll("\\r", ""));
			ed.setNacionalidad(DNIParser.nacionalidad.replaceAll("\\r", ""));
			ed.setNombre(DNIParser.nombre.replaceAll("\\r", ""));
			ed.setApellido1(DNIParser.apellido1.replaceAll("\\r", ""));
			ed.setApellido2(DNIParser.apellido2.replaceAll("\\r", ""));

			json = objectMapper.writeValueAsString(ed);

			resp.setContentType("text/html");

			os = resp.getWriter();

			os.println(json);
			os.flush();
			os.close();

			System.out.println(json);

			break;
			
		case 2:
				throw new IOException();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);

	}
}

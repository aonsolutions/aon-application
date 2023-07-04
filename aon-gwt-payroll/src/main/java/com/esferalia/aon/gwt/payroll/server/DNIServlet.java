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
		
		
		if (fileName.contains(".pdf")) {
			InputStream is = filePart.getInputStream();

			DNIParser.parse(is);
			String text = DNIParser.getText();
			DNIParser.getNewDniBothPdf(text, listener);
//			DNIParser.getNewDniFront(text, listener);

			EmployeeData ed = new EmployeeData();

			ed.setDni(DNIParser.dni.replaceAll("\\r", ""));
			ed.setNacionalidad(DNIParser.nacionalidad.replaceAll("\\r", ""));
			ed.setNombre(DNIParser.nombre.replaceAll("\\r", ""));
			ed.setApellido1(DNIParser.apellido1.replaceAll("\\r", ""));
			ed.setApellido2(DNIParser.apellido2.replaceAll("\\r", ""));

			ObjectMapper objectMapper = new ObjectMapper();

			String json = objectMapper.writeValueAsString(ed);

			resp.setContentType("text/html");


			PrintWriter os = resp.getWriter();

			os.println(json);
			os.flush();
			os.close();

			System.out.println(json);

		} else if (fileName.contains(".jpg")) {
			InputStream prueba2 = filePart.getInputStream();
			byte[] bytes = IOUtils.toByteArray(prueba2);
			String text = DNIParser.extractImage(bytes);
			DNIParser.getNewDniBothJpg(text, listener);

			EmployeeData ed = new EmployeeData();

			ed.setDni(DNIParser.dni.replaceAll("\\r", ""));
			ed.setNacionalidad(DNIParser.nacionalidad.replaceAll("\\r", ""));
			ed.setNombre(DNIParser.nombre.replaceAll("\\r", ""));
			ed.setApellido1(DNIParser.apellido1.replaceAll("\\r", ""));
			ed.setApellido2(DNIParser.apellido2.replaceAll("\\r", ""));

			ObjectMapper objectMapper = new ObjectMapper();

			String json = objectMapper.writeValueAsString(ed);

			resp.setContentType("text/html");

			PrintWriter os = resp.getWriter();

			os.println(json);
			os.flush();
			os.close();
			
			System.out.println(json);

			
		} 
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);

	}
}

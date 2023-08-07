package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.amazonaws.util.IOUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeData;
import com.esferalia.aon.in.payroll.img.DNIParser;
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

		List<InputStream> inputStreams = new ArrayList<InputStream>();
		Part filePart = req.getPart("archivo");
		Collection<Part> filesPart = req.getParts();
		for (Iterator iterator = filesPart.iterator(); iterator.hasNext();) {
			Part part = (Part) iterator.next();
			InputStream is = part.getInputStream();
			inputStreams.add(is);

		}

		String fileName = filePart.getSubmittedFileName();
//		System.out.println(fileName);
		int accion;

		DNIParser dnip = new DNIParser();

		EmployeeData ed = new EmployeeData();
		String text = "";
		String json = "";
		String dni = "";
		String nombre = "";
		String apellido1 = "";
		String apellido2 = "";
		String nacionalidad = "";
		String[] lineas = {};
		PrintWriter os;
		ObjectMapper objectMapper = new ObjectMapper();

		if (fileName.contains(".pdf")) {
			accion = 0;
		} else if (fileName.contains(".jpg")) {
			accion = 1;
		} else {
			accion = 2;
		}

		switch (accion) {
		case 0:
			dnip.parse(inputStreams);
			text = dnip.getText();
			break;

		case 1:
			for (Iterator i = filesPart.iterator(); i.hasNext();) {
				Part part = (Part) i.next();
				InputStream is = part.getInputStream();

				byte[] bytes = IOUtils.toByteArray(is);
				text = dnip.extractImage(bytes);
			}
			break;
		case 2:
			System.out.println("El archivo no es valido");
			break;
		}

		System.out.println(text);
		lineas = text.split("\n");
		for (int i = 0; i < lineas.length; i++) {
			String linea = lineas[i];
			if (linea.startsWith("DNI") || linea.startsWith("DOCUMENTO NACIONAL DE IDENTIDAD")) {
				dni = lineas[i + 1];
				if (!dnip.validateDni(dni)) {
					dni = "";
					continue;
				}

			} else if (linea.startsWith("APELLIDOS") || linea.startsWith("APALLIDOS")) {
				apellido1 = lineas[i + 1];
				apellido2 = lineas[i + 2];

			} else if (linea.startsWith("NOMBRE") || linea.startsWith("NONBRE")) {
				nombre = lineas[i + 1];

			} else if (linea.startsWith("NACIONALIDAD")) {
				nacionalidad = lineas[i + 3];
				if (!dnip.validateNationality(nacionalidad)) {
					nacionalidad = "";
					continue;
				}
				if (nacionalidad.equals("ESP")) {
					nacionalidad = "ESPAÑA";
				}

			}
		}

		ed.setDni(dni.replaceAll("\\r", ""));
		ed.setNacionalidad(nacionalidad.replaceAll("\\r", ""));
		ed.setNombre(nombre.replaceAll("\\r", ""));
		ed.setApellido1(apellido1.replaceAll("\\r", ""));
		ed.setApellido2(apellido2.replaceAll("\\r", ""));

		json = objectMapper.writeValueAsString(ed);

		resp.setContentType("text/html");

		os = resp.getWriter();

		os.println(json);

		os.flush();
		os.close();

		System.out.println(json);

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);

	}
}

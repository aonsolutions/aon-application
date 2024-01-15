package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.esferalia.aon.gwt.payroll.client.PersonDocumentResult;
import com.esferalia.aon.gwt.payroll.shared.EmployeeData;
import com.esferalia.aon.in.payroll.img.PersonDocumentParser;
import com.esferalia.aon.occam.api.model.PersonDocument;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet(name = "PERSONDOCUMENT-SERVLET", urlPatterns = "/PersonDocumentServlet")
@MultipartConfig
public class PersonDocumentServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException  {
		
		PrintWriter os;
		ObjectMapper objectMapper = new ObjectMapper();
		String json = "";
		Part filePart = req.getPart("archivo");
		InputStream is = filePart.getInputStream();
		
		byte [] bytes = AonIOUtils.toByteArray(is);
		
		PersonDocument personDocument = PersonDocumentParser.parse(bytes);
		
		String dni = personDocument.getDocument();
		System.out.println(dni);
		String apellido1 = personDocument.getFirstSurname();
		System.out.println(apellido1);
		String apellido2 = personDocument.getSecondSurname();
		System.out.println(apellido2);
		String nombre = personDocument.getName();
		System.out.println(nombre);
		Country nacionalidad = personDocument.getNationality();
		System.out.println(nacionalidad);
		
//		pdr.setDni(dni.replace("\\r", ""));
//		pdr.setNacionalidad(nacionalidad);
//		pdr.setNombre(nombre.replace("\\r", ""));
//		pdr.setApellido1(apellido1.replace("\\r", ""));
//		pdr.setApellido2(apellido2.replace("\\r", ""));

		try {
			json = objectMapper.writeValueAsString(personDocument);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		resp.setContentType("text/html");
		try {
			os = resp.getWriter();
			os.println(json);
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		
		
//		Collection<Part> filesPart = req.getParts();
//		for (Iterator<Part> iterator = filesPart.iterator(); iterator.hasNext();) {
//			Part part = iterator.next();
//			InputStream is = part.getInputStream();
//			inputStreams.add(is);
//		}
		
//
//		DNIParser dnip = new DNIParser();
//		String text = "";
//
//		if (fileName.contains(".pdf")) {
//			accion = 0;
//		} else if (fileName.contains(".jpg") || (fileName.contains(".png") || fileName.contains(".jpeg"))) {
//			accion = 1;
//		} else {
//			accion = 2;
//		}
//
//		switch (accion) {
//		case 0:
//			dnip.parse(inputStreams);
//			text = dnip.getText();
//			break;
//		case 1:
//			for (Iterator<Part> i = filesPart.iterator(); i.hasNext();) {
//				Part part = i.next();
//				InputStream is = part.getInputStream();
//				byte[] bytes = IOUtils.toByteArray(is);
//				text = dnip.extractImage(bytes);
//			}
//			break;
//		default:
//			break;
//
//		}
//		List<String> result = dnip.getDNIData(text);
//	
//		String dni = result.get(0);
//		String apellido1 = result.get(1);
//		String apellido2 = result.get(2);
//		String nombre = result.get(3);
//		String nacionalidad = result.get(4);
//
//		ed.setDni(dni.replace("\\r", ""));
//		ed.setNacionalidad(nacionalidad.replace("\\r", ""));
//		ed.setNombre(nombre.replace("\\r", ""));
//		ed.setApellido1(apellido1.replace("\\r", ""));
//		ed.setApellido2(apellido2.replace("\\r", ""));
//
//		try {
//			json = objectMapper.writeValueAsString(ed);
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//
//		resp.setContentType("text/html");
//		try {
//			os = resp.getWriter();
//			os.println(json);
//			os.flush();
//			os.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);

	}
}

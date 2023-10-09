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
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet(name = "DNI-SERVLET", urlPatterns = "/DNIServlet")
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
		int accion;

		DNIParser dnip = new DNIParser();
		String text = "";
		String[] lineas = {};

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
			text = DNIParser.getText();
			break;
		case 1:
			for (Iterator i = filesPart.iterator(); i.hasNext();) {
				Part part = (Part) i.next();
				InputStream is = part.getInputStream();
				byte[] bytes = IOUtils.toByteArray(is);
				text = DNIParser.extractImage(bytes);
			}
			break;
		case 2:
			System.out.println("El archivo no es valido");
			break;
		}

		System.out.println(text);
		lineas = text.split("\n");
		fillJson(resp, lineas);


	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);

	}
	
	public void fillJson(HttpServletResponse resp, String lineas[]) {
		EmployeeData ed = new EmployeeData();
		DNIParser dnip = new DNIParser();
		PrintWriter os;
		ObjectMapper objectMapper = new ObjectMapper();
		String json = "";
		String dni = "";
		String nombre = "";
		String apellido1 = "";
		String apellido2 = "";
		String nacionalidad = "";
		
		validate(lineas);
		
		for (int i = 0; i < lineas.length; i++) {
			String linea = lineas[i]; 
			if (linea.startsWith("DNI") || linea.startsWith("DOCUMENTO NACIONAL DE IDENTIDAD")) {
				dni = lineas[i + 1];
//				if (!dnip.validateDni(dni)) {
//					dni = "";
//					continue;
//				}

			} else if (linea.startsWith("APELLIDOS") || linea.startsWith("APALLIDOS")) {
				apellido1 = lineas[i + 1];
				apellido2 = lineas[i + 2];

			} else if (linea.startsWith("NOMBRE") || linea.startsWith("NONBRE")) {
				nombre = lineas[i + 1];

			} else if (linea.startsWith("NACIONALIDAD")) {
				nacionalidad = lineas[i + 3];
//				if (!dnip.validateNationality(nacionalidad)) {
//					nacionalidad = "";
//					continue;
//				}
				if (nacionalidad.equals("ESP")) {
					nacionalidad = "ESPAÑA";
				}

			}
		}

		validateDni(dni);
		ed.setDni(dni.replaceAll("\\r", ""));
		validateNationality(nacionalidad);
		ed.setNacionalidad(nacionalidad.replaceAll("\\r", ""));
		validateName(nombre);
		ed.setNombre(nombre.replaceAll("\\r", ""));
		validateFirstSurname(apellido1);
		ed.setApellido1(apellido1.replaceAll("\\r", ""));
		validateSecondSurname(apellido2);
		ed.setApellido2(apellido2.replaceAll("\\r", ""));

		try {
			json = objectMapper.writeValueAsString(ed);
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

		System.out.println(json);

	}
	
	private static final Consumer<String []> NULL_ARRAY_LINES = lineas ->{
		if (lineas == null) {
			throw new AonCoreException(AonError.NULL_FILE_UPLOADED.getMessage());
		}
	};
	
	private static final Consumer <String> NULL_DNI = dni ->{
		if (dni == null || dni.equals("")) {
			throw new AonCoreException(AonError.INVALID_DNI_FORMAT.getMessage());
		}
	};
	
	private static final Consumer<String> NULL_NATIONALITY = nationality ->{
		if (nationality == null|| nationality.equals("")) {
			throw new AonCoreException(AonError.INVALID_NATIONALITY_FORMAT.getMessage());

		}
	};
	
	private static final Consumer<String> NULL_NAME = name ->{
		if (name == null|| name.equals("")) {
			throw new AonCoreException(AonError.INVALID_NAME_FORMAT.getMessage());

		}
	};
	private static final Consumer<String> NULL_FIRST_SURNAME = firstSurname ->{
		if (firstSurname == null|| firstSurname.equals("")) {
			throw new AonCoreException(AonError.INVALID_FIRST_SURNAME.getMessage());

		}
	};
	private static final Consumer<String> NULL_SECOND_SURNAME = secondSurname ->{
		if (secondSurname == null|| secondSurname.equals("")) {
			throw new AonCoreException(AonError.INVALID_SECOND_SURNAME.getMessage());

		}
	};
	
	
	public static void validate(String [] lineas) throws AonCoreException {
		NULL_ARRAY_LINES.accept(lineas);
	}
	
	public static void validateDni(String dni) throws AonCoreException{
		NULL_DNI.accept(dni);
	}
	
	public static void validateNationality(String nacionalidad) throws AonCoreException {
		NULL_NATIONALITY.accept(nacionalidad);
	}
	
	public static void validateName(String name) throws AonCoreException{
		NULL_NAME.accept(name);
	}
	
	public static void validateFirstSurname(String firstSurname) throws AonCoreException{
		NULL_FIRST_SURNAME.accept(firstSurname);
	}
	
	public static void validateSecondSurname(String secondSurname) throws AonCoreException{
		NULL_SECOND_SURNAME.accept(secondSurname);
	}
	
	
}

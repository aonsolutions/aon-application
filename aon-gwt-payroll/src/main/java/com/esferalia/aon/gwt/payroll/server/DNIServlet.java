package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "DNIServlet", urlPatterns = "/DNIServlet")
@MultipartConfig
public class DNIServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)  {
//		EmployeeData ed = new EmployeeData();
//		PrintWriter os;
//		ObjectMapper objectMapper = new ObjectMapper();
//		String json = "";
//		List<InputStream> inputStreams = new ArrayList<>();
//		Part filePart = req.getPart("archivo");
//		Collection<Part> filesPart = req.getParts();
//		for (Iterator<Part> iterator = filesPart.iterator(); iterator.hasNext();) {
//			Part part = iterator.next();
//			InputStream is = part.getInputStream();
//			inputStreams.add(is);
//		}
//
//		String fileName = filePart.getSubmittedFileName();
//		int accion;
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

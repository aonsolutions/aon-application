package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import com.esferalia.aon.in.payroll.img.PersonDocumentParser;
import com.esferalia.aon.occam.api.model.PersonDocument;
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
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);

	}
}

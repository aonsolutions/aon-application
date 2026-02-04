package com.esferalia.aon.gwt.fiscal.server.fiscal.mod347;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL347;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.server.fiscal.format.Mod347Writer;
import com.esferalia.aon.watson.server.io.AonIOUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Mod415 File download", urlPatterns = { "/aon_gwt_fiscal/ms/Model415File" })
public class Mod415File extends HttpServlet {
	
	private static final long serialVersionUID = -112360763622268352L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			System.out.println("Mod415File: PASO 0");
			boolean bocFormat = Boolean.parseBoolean(req.getParameter("bocFormat"));
			boolean isBorrador = Boolean.parseBoolean(req.getParameter("isBorrador"));
			AEATParams aeatParams = ModelAdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(aeatParams.getDomainName())
					.setDomain(aeatParams.getDomainId())
					.setUser(aeatParams.getUser());
			Mod347 mod415 = MODEL347.get(occam, ModelAdmonUtils.getFiscalModelId(aeatParams));
			
			System.out.println("Mod415File: PASO 1");
			
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod347Writer.fillWriter(mod415, writer);
			
			String txt = new String(output.toByteArray(), "ISO-8859-1");			
			System.out.println("Mod415File: PASO 2. txt=" + txt);
			
			if (bocFormat) {
				// Simplemente generar un fichero para descarga con el formato BOC

				ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());
				
				String s = mod415.getName();
				StringBuilder sb = new StringBuilder();
				if (!Character.isJavaIdentifierStart(s.charAt(0))) {
					sb.append("_");
				}
				for (char c : s.toCharArray()) {
					if (Character.isJavaIdentifierPart(c)) {
						sb.append(c);
					}
				}
				 
			    String fileName = "Mod415" + "_" + mod415.getYear() + "_" + sb.toString();			
				
				resp.setContentType(MimeType.TXT.getName());
				resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".txt\";");
				AonIOUtils.copy(in, resp.getOutputStream());
				resp.flushBuffer();
				System.out.println("Mod415File: PASO 3. FILE TXT OK");
			} else {
				// Pasarlo al modulo de impresión para obtener el fichero para la presentación 
				ModelAdmonUtils.callAtcAwsFunction(txt, mod415, isBorrador, resp);
				System.out.println("Mod415File: PASO 3. CALL ATC OK");
			}
			
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}

}

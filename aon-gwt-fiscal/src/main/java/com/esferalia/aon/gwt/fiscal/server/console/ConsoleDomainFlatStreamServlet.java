package com.esferalia.aon.gwt.fiscal.server.console;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.tools.json.ParseException;
import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.gwt.fiscal.server.JsonParser;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.CONSOLE;
import com.esferalia.aon.occam.api.json.ConsoleDomainJSON;
import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.mutable.MutableBoolean;

@WebServlet(name = "Console Domain Flat Stream Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/ConsoleDomainFlatStreamServlet" })
public class ConsoleDomainFlatStreamServlet extends HttpServlet {

	private static final long serialVersionUID = 2983470757068498342L;
	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainFlatStreamServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Stream<ConsoleDomain> stream = null;
		try {
			String domainParams = req.getParameter(IRequestParamsNames.DOMAIN_PARAMS);
			DomainParams params = JsonParser.parseDomainParams(domainParams);
			LOGGER.log(Level.INFO, "Console Domain Flat [{0},{1}]",new Object[] {params.getOffset(),params.getLimit()});
			resp.setContentType(MimeType.JSON.getName());
			PrintWriter out = resp.getWriter();
			final MutableBoolean first = new MutableBoolean(true);
			stream =  CONSOLE.getDomains(params);
			out.write('[');
			stream.map( ConsoleDomainJSON::toJSON )
				.forEach(json -> write(out,json,first));
			out.write(']');
			
		} catch (ParseException | java.text.ParseException e) {
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "Console Domain Flat [{0}]",e.getMessage());
		} finally {
			if (stream != null) {
				stream.close();
			}
			try {
				resp.flushBuffer();
			} catch (IOException e) {
				// Nothing
			}
		}

	}

	private void write(PrintWriter out, JSONObject json, MutableBoolean first) {
		try {
			if (first.isTrue()) {
				first.setValue(false);
			} else {
				out.write(',');							
			}
			json.write(out);
			json.write( new PrintWriter(System.out) );
			out.write('\n');
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}

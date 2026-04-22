package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.json.JSONException;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.json.DomainInvoiceStatJSON;
import com.esferalia.aon.occam.api.model.DomainInvoiceStatParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.DomainInvoiceStatDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Domain Invoice Stat Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/DomainInvoiceStatServlet" })
public class DomainInvoiceStatServlet extends HttpServlet {

	private static final long serialVersionUID = -5703828624659508582L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	    // 1. Parseo y validacion de parametros
	    final String domainParams = req.getParameter(IRequestParamsNames.DOMAIN_PARAMS);
	    final String domainName   = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
	    final String user         = req.getParameter(IRequestParamsNames.USER);
	    final String rawDomainId  = req.getParameter(IRequestParamsNames.DOMAIN_ID);

	    if (rawDomainId == null || rawDomainId.isBlank()) {
	        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Par\u00E1metro inv\u00E1lido: domainId");
	        return;
	    }
	    final int domainId;
	    try {
	        domainId = Integer.parseInt(rawDomainId);
	    } catch (NumberFormatException e) {
	        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Par\u00E1metro inv\u00E1lido: domainId=" + rawDomainId);
	        return;
	    }

	    final DomainInvoiceStatParams params;
	    try {
	        params = JsonParser.parseDomainInvoiceStatParams(domainParams);
	    } catch (Exception e) {
	        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Par\u00E1metro inv\u00E1lido: domainParams. " + e.getMessage());
	        return;
	    }

	    // 2. Preparar respuesta - configurar headers ANTES de escribir body
	    resp.setContentType(MimeType.JSON.getName());
	    resp.setCharacterEncoding("UTF-8");

	    final PrintWriter out = resp.getWriter();

	    // 3. Construir contexto y hacer streaming del JSON array
	    final Occam occam = new Occam()
            .setDomain(domainId)
            .setDomainName(domainName)
            .setUser(user);

	    try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {

	        out.write('[');

	        // AtomicBoolean es seguro para uso en lambdas (alternativa a MutableBoolean)
	        final AtomicBoolean first = new AtomicBoolean(true);

	        // Acumulamos cualquier excepcion ocurrida dentro del forEach
	        final AtomicReference<Exception> streamError = new AtomicReference<>();

	        DomainInvoiceStatDAO.stream(ctx, params)
	        	.map(entry -> DomainInvoiceStatJSON.toJSON(entry).orElse(null))
	        	.filter(Objects::nonNull)
	        	.forEach(json -> {
	        		// Si ya hubo un error previo, cortocircuitamos el forEach
	        		if (streamError.get() != null) return;
						try {
							if (!first.compareAndSet(true, false)) {
								out.write(',');
							}
							json.write(out);
							out.write('\n');
						} catch (JSONException e) {
							// Capturamos y propagamos fuera del forEach
							streamError.set(e);
						}
	                });

	        // Si hubo un error durante el stream, lo lanzamos para que el catch exterior lo maneje
	        final Exception error = streamError.get();
	        if (error != null) {
	            throw new ServletException("Error al serializar entrada del stream", error);
	        }
	        out.write(']');

	        // flush explicito solo si no se ha producido error
	        resp.flushBuffer();

	    } catch (ServletException e) {
	        // Re-lanzamos excepciones de servlet sin envolver
	        throw e;
	    } catch (Exception e) {
	        // 4. Solo lanzar ServletException para excepciones recuperables.
	        //    NO capturar Throwable - dejar que Errors de JVM se propaguen solos
	        throw new ServletException("Error inesperado procesando la solicitud DomainInvoiceStat", e);
	    }
	}
}
/*
//@Override
protected void _doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	try {
		String domainParams = req.getParameter(IRequestParamsNames.DOMAIN_PARAMS);
		String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
		String user = req.getParameter(IRequestParamsNames.USER);
		int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
		DomainInvoiceStatParams params = JsonParser.parseDomainInvoiceStatParams(domainParams);
		resp.setContentType(MimeType.JSON.getName());
		PrintWriter out = resp.getWriter();
		out.write('[');
		final MutableBoolean first = new MutableBoolean(true);
		Occam occam = new Occam()
				.setDomain(domainId)
				.setDomainName(domainName)
				.setUser(user);
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			DomainInvoiceStatDAO.stream(ctx, params)
			.map( entry -> DomainInvoiceStatJSON.toJSON(entry).orElse(null))
			.filter( Objects::nonNull )
			.forEach(json -> {
				try {
					if (first.getValue()) {
						first.setValue(false);
					} else {
						out.write(',');							
					}
					json.write(out);
					out.write('\n');
				} catch (JSONException e) {
					e.printStackTrace();
				}
			});
			;
		}
		
		out.write(']');
		resp.flushBuffer();
	} catch (Throwable e) {
		throw new ServletException(e);
	}
}
*/
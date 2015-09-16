package com.esferalia.aon.gwt.payroll.server;

import static java.lang.String.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.shared.CretaService;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.tgss.creta.Bases;
import com.esferalia.aon.payroll.tgss.creta.Bases.BasesCallback;
import com.esferalia.aon.payroll.tgss.creta.Borrador;
import com.esferalia.aon.payroll.tgss.creta.Calculo;
import com.esferalia.aon.payroll.tgss.creta.Confirmacion;
import com.esferalia.aon.payroll.tgss.creta.TrabajadoresTramos;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.tgss.creta.jaxb.Dato;
import net.aonsolutions.tgss.creta.jaxb.DatoSolicitado;
import net.aonsolutions.tgss.creta.jaxb.Trabajador;
import net.aonsolutions.tgss.creta.jaxb.Tramo;
import net.aonsolutions.tgss.creta.jaxb.bases.TramoBuilder;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "SLD-Solicitud", urlPatterns = { "/aon_gwt_payroll/sdl/*" })
public class CretaServlet extends HttpServlet implements
		CretaService.File.Visitor<HttpServletRequest, HttpServletResponse, Exception> {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String uri = req.getRequestURI();
		String fichero = AonServletUtils.getFileName(uri);

		CretaService.File file = CretaService.File.valueOf(fichero);

		try {
			file.accept(this, req, resp);
		} catch (Exception e) {
			throw new ServletException(e);
		}

	}

	protected Connection getConnection() throws SQLException {
		return AonServletUtils.getConnection();
	}

	// ------------------------------------------------------------------------
	// CretaService.Solicitud.Visitor<HttpServletRequest, HttpServletResponse,
	// Exception>

	@Override
	public void visitBases(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {

		Connection connection = getConnection();
		resp.setContentType("text/html;");

		String nafs[] = req
				.getParameterValues(CretaService.Parameter.NAFS.name());
		String defaults[] = req
				.getParameterValues(CretaService.Parameter.DEFAULTS.name());

//		boolean comments = AonStringUtils.equalsIgnoreCase(
//				Boolean.toString(true), CretaService.Parameter.COMMENTS.name());
		boolean acceptPrevBases = AonStringUtils.equalsIgnoreCase(
				Boolean.toString(true),
				CretaService.Parameter.ACEPTAR_BASES_ANTERIORES.name());

		EventsPickerBasesCallback pickerBasesCb = new EventsPickerBasesCallback();

		PrintWriter os = resp.getWriter();

		os.println("<html>");
		os.println("<body>");
		os.println("<script>");
		for (Part part : req.getParts()) {

			os.println("parent.__onBases (");
			os.println("{");

			os.printf("\"full_bases\":\"%s\",\r\n",
					generateBases(connection, true, false, acceptPrevBases,
							nafs, defaults, part.getInputStream(), pickerBasesCb));

			os.printf("\"diff_bases\":\"%s\",\r\n",
					generateBases(connection, true, true, acceptPrevBases,
							nafs, defaults, part.getInputStream()));

			os.printf("\"errors\":%s,\r\n", toJSON(pickerBasesCb.errors));

			os.printf("\"warnings\":%s,\r\n",
					toJSON(pickerBasesCb.warnings));

			os.printf("\"messages\":[]\r\n");
			
			os.println("});");
		}
		os.println("</script>");
		os.println("</body>");
		os.println("</html>");

		os.flush();
		os.close();

	}

	@Override
	public void visitBorrador(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType("text/xml;");
		String mes = req.getParameter(CretaService.Parameter.MES.name());
		String anho = req.getParameter(CretaService.Parameter.ANHO.name());
		String tipo = req.getParameter(CretaService.Parameter.TIPO.name());
		String cccs[] = req
				.getParameterValues(CretaService.Parameter.CCC.name());
		String autorizado = req
				.getParameter(CretaService.Parameter.AUTORIZADO.name());
		boolean aceptarBasesAnteriores = Boolean.parseBoolean(req.getParameter(
				CretaService.Parameter.ACEPTAR_BASES_ANTERIORES.name()));
		Borrador.generate(autorizado, mes, anho, tipo, aceptarBasesAnteriores,
				cccs, resp.getOutputStream());
	}

	@Override
	public void visitConfirmacion(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("text/xml;");
		String mes = req.getParameter(CretaService.Parameter.MES.name());
		String anho = req.getParameter(CretaService.Parameter.ANHO.name());
		String tipo = req.getParameter(CretaService.Parameter.TIPO.name());
		String cccs[] = req
				.getParameterValues(CretaService.Parameter.CCC.name());
		String autorizado = req
				.getParameter(CretaService.Parameter.AUTORIZADO.name());
		Confirmacion.generate(autorizado, mes, anho, tipo, cccs,
				resp.getOutputStream());
	}

	@Override
	public void visitCalculos(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType("text/xml;");
		String mes = req.getParameter(CretaService.Parameter.MES.name());
		String anho = req.getParameter(CretaService.Parameter.ANHO.name());
		String tipo = req.getParameter(CretaService.Parameter.TIPO.name());
		String cccs[] = req
				.getParameterValues(CretaService.Parameter.CCC.name());
		String autorizado = req
				.getParameter(CretaService.Parameter.AUTORIZADO.name());
		Calculo.generate(autorizado, mes, anho, tipo, cccs,
				resp.getOutputStream());
	}

	@Override
	public void visitTrabajadoresTramos(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("text/xml;");
		String mes = req.getParameter(CretaService.Parameter.MES.name());
		String anho = req.getParameter(CretaService.Parameter.ANHO.name());
		String tipo = req.getParameter(CretaService.Parameter.TIPO.name());
		String cccs[] = req
				.getParameterValues(CretaService.Parameter.CCC.name());
		String autorizado = req
				.getParameter(CretaService.Parameter.AUTORIZADO.name());
		TrabajadoresTramos.generate(autorizado, mes, anho, tipo, cccs,
				resp.getOutputStream());
	}

	// ------------------------------------------------------------------------

	private static String generateBases(Connection connection, boolean comments,
			boolean skipExisting, boolean acceptPrevBases, String nafs[],
			String defaults[], InputStream is, BasesCallback... cbs)
					throws JAXBException, XMLStreamException,
					FactoryConfigurationError, IOException {

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Bases.generate(connection, comments, skipExisting, acceptPrevBases,
				nafs, defaults, is, null /* respuestaIs */, os, cbs);
		os.close();
		return String.format("%s", URLEncoder.encode(os.toString(), "UTF-8"));

	}

	// ------------------------------------------------------------------------

	private static interface JSON {
		String toJSON();
	}

	private static class Event implements JSON {

		private String message;

		public String getMessage() {
			return message;
		}

		public Event setMessage(String message) {
			this.message = message;
			return this;
		}

		// --------------------------------------------------------------- JSON

		@Override
		public String toJSON() {
			return String.format("{" + "\"message\":\"%s\",\r\n" + "}",
					message);
		}
	}

	private static class EventsPickerBasesCallback implements BasesCallback {
		
		
		private List<Event> errors = new ArrayList<Event>();
		private List<Event> warnings = new ArrayList<Event>();

		// ------------------------------------------------------------- Errors

		@Override
		public void unknownDato(Salary salary, Tramo tramo,
				DatoSolicitado datoSolicitado, TramoBuilder tramoBuilder) {
			boolean mandatory = "B".equalsIgnoreCase(datoSolicitado.getIndicadorObligatoriedad());
			
			errors.add(new Event().setMessage(String.format(
					"Lo sentimos. %s (%s) no está soportado en AON SOLUTIONS",
					getDescription(datoSolicitado),
					mandatory? "Obligatorio" : "Opcional")));
		}

		@Override
		public void unknownTrabajador(String ccc, Trabajador trabajador) {
			errors.add(new Event().setMessage(
					format("El trabajador (NAF:%s, CCC:%s) no encontrado en AON SOLUTIONS",
							trabajador.getNaf(), ccc)));
		}

		@Override
		public void wrongContextVariable(Salary salary, ContextVariable var, Period p,
				String right, String wrong) {
			errors.add(new Event().setMessage(
					format("%s (IPF:%s, NAF:%s) .%s (%2$td/%2$tm/%2$tY..%3$td/%3$tm/%3$tY) incorrecta. Se esperaba '%s' y es '%s'",
//							salary.getEnterpriseName(), 
							salary.getEmployeeName(),
							salary.getEmployeeDocument(), 
							salary.getEmployeeSSNumber(),
//							salary.getEnterpriseCCC(),
							var.getName(),
							p.getStart(),
							p.getEnd(),
							right,
							wrong)));
		}

		@Override
		public void noSuchContextVariable(Salary salary, ContextVariable var, Period p,
				String right) {
			errors.add(new Event().setMessage(
					format("%s (IPF:%s, NAF:%s) .%s (%2$td/%2$tm/%2$tY..%3$td/%3$tm/%3$tY) no encontrada. Se esperaba '%s'",
//							salary.getEnterpriseName(), 
							salary.getEmployeeName(),
							salary.getEmployeeDocument(), 
							salary.getEmployeeSSNumber(),
//							salary.getEnterpriseCCC(),
							var.getName(),
							p.getStart(),
							p.getEnd(),
							right)));
		}


		@Override
		public void ambigousContextVariable(Salary salary, ContextVariable var, Period p,
				String right, String... wrongs) {
			errors.add(new Event().setMessage(
					format("%s (IPF:%s, NAF:%s) .%s (%2$td/%2$tm/%2$tY..%3$td/%3$tm/%3$tY) ambigua. Se esperaba '%s' y es %s",
//							salary.getEnterpriseName(), 
							salary.getEmployeeName(),
							salary.getEmployeeDocument(), 
							salary.getEmployeeSSNumber(),
//							salary.getEnterpriseCCC(),
							var.getName(),
							p.getStart(),
							p.getEnd(),
							right,
							Arrays.stream(wrongs).map(wrong -> String.format("'%s'", wrong)).collect(Collectors.joining(",")))));
		}

		@Override
		public void unMatchedContextVariable(Salary salary, ContextVariable var,
				ContextData contextData, Dato datoSolicitado, Tramo tramo,
				TramoBuilder tramoBuilder, boolean optional) {
			errors.add(new Event().setMessage(
					format("%s (IPF:%s, NAF:%s) .%s (%2$td/%2$tm/%2$tY..%3$td/%3$tm/%3$tY) incorrecta. Se esperaba (%s/%s/%s..%s/%s/%s)",
//							salary.getEnterpriseName(), 
							salary.getEmployeeName(),
							salary.getEmployeeDocument(), 
							salary.getEmployeeSSNumber(),
//							salary.getEnterpriseCCC(),
							var.getName(),
							contextData.getStartDate(),
							contextData.getEndDate(),
							tramo.getFechaDesde().getDia(),
							tramo.getFechaDesde().getMes(),
							tramo.getFechaDesde().getAnho(),
							tramo.getFechaHasta().getDia(),
							tramo.getFechaHasta().getMes(),
							tramo.getFechaHasta().getAnho()
							)));
		}

		@Override
		public void noSuchDato(Salary salary, Tramo tramo, Dato datoSolicitado,
				TramoBuilder tramoBuilder, boolean optional) {
			Event event = new Event().setMessage(
					format("%s (IPF:%s, NAF:%s) .%s (%s/%s/%s..%s/%s/%s) no encontrado. ",
//							salary.getEnterpriseName(), 
							salary.getEmployeeName(),
							salary.getEmployeeDocument(), 
							salary.getEmployeeSSNumber(),
//							salary.getEnterpriseCCC(),
							getDescription(datoSolicitado),
							tramo.getFechaDesde().getDia(),
							tramo.getFechaDesde().getMes(),
							tramo.getFechaDesde().getAnho(),
							tramo.getFechaHasta().getDia(),
							tramo.getFechaHasta().getMes(),
							tramo.getFechaHasta().getAnho()
							));
			if ( optional )
				warnings.add(event);
			else 
				errors.add(event);
		}


		// ----------------------------------------------------------- Warnings

		@Override
		public void unknownSalary(Salary salary) {
			warnings.add(new Event().setMessage(format(
					"%s (IPF:%s, NAF:%s) no encontrado en el Sistema de Liquidación Directa (Proyecto Cret@)s",
//					salary.getEnterpriseName(), 
					salary.getEmployeeName(),
					salary.getEmployeeDocument(), 
					salary.getEmployeeSSNumber()
//					salary.getEnterpriseCCC()))
					)));
		}



		// ----------------------------------------------------- Private Static
		
		
		
		private static String getDescription(Dato dato){
			switch (dato.getCodigo()) {
			case "02":
				return "El Número de Horas Complementarias";
			case "501":
				return "La Base de Horas Extras Fuerza Mayor";
			case "502":
				return "La Base de Otras Horas Extras";
			}
			
			switch (dato.getTipoDato()) {
			case "C":
				return format("El Concepto Económico de Cotización %s", dato.getCodigo());
			case "H":
				return format("Las Horas %s", dato.getCodigo());
			case "I":
				return format("El Indicador %s", dato.getCodigo());
			default:
				return "";
			}
		}
		

	}

	private static <J extends JSON> String toJSON(List<J> jsons) {
		StringBuffer buff = new StringBuffer();
		buff.append("[\r\n");
		buff.append(jsons.stream().map(json -> json.toJSON())
				.collect(Collectors.joining(",")));
		buff.append("\r\n]");
		return buff.toString();
	}
}

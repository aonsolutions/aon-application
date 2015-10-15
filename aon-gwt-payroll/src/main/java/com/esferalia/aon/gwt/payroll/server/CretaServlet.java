package com.esferalia.aon.gwt.payroll.server;

import static java.lang.String.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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

import org.apache.commons.lang.StringEscapeUtils;

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
import com.mchange.lang.CharUtils;

import net.aonsolutions.tgss.creta.jaxb.Dato;
import net.aonsolutions.tgss.creta.jaxb.DatoSolicitado;
import net.aonsolutions.tgss.creta.jaxb.Trabajador;
import net.aonsolutions.tgss.creta.jaxb.Tramo;
import net.aonsolutions.tgss.creta.jaxb.Utils;
import net.aonsolutions.tgss.creta.jaxb.bases.TramoBuilder;
import net.aonsolutions.tgss.creta.jaxb.respuesta.Respuesta;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.Periodo;

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
		// String defaults[] = req
		// .getParameterValues(CretaService.Parameter.DEFAULTS.name());
		String defaults[] = { "51=M", "737=0" };
		// boolean comments = AonStringUtils.equalsIgnoreCase(
		// Boolean.toString(true), CretaService.Parameter.COMMENTS.name());
		boolean acceptPrevBases = AonStringUtils.equalsIgnoreCase("on",
				req.getParameter(CretaService.Parameter.ACEPTAR_BASES_ANTERIORES
						.name()));

		EventsPickerBasesCallback pickerBasesCb = new EventsPickerBasesCallback();

		PrintWriter os = resp.getWriter();

		os.println("<html>");
		os.println("<body>");
		os.println("<script>");
		os.println("parent.__onBases (");
		os.println("{");

		List<InputStream> inputStreams = new ArrayList<InputStream>();
		for (Part part : req.getParts()) {
			inputStreams.add(part.getInputStream());
		}
		os.printf("\"full_bases\":\"%s\",\r\n",
				generateBases(connection, true, false, acceptPrevBases, nafs,
						defaults, inputStreams, pickerBasesCb));

		inputStreams.clear();
		for (Part part : req.getParts()) {
			inputStreams.add(part.getInputStream());
		}
		os.printf("\"diff_bases\":\"%s\",\r\n", generateBases(connection, true,
				true, acceptPrevBases, nafs, defaults, inputStreams));

		os.printf("\"errors\":%s,\r\n", toJSON(pickerBasesCb.errors));

		os.printf("\"warnings\":%s,\r\n", toJSON(pickerBasesCb.warnings));

		os.printf("\"messages\":[]\r\n");

		os.println("});");
		os.println("</script>");
		os.println("</body>");
		os.println("</html>");

		os.flush();
		os.close();

	}

	@Override
	public void visitSolicitudBorrador(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType("text/xml;");
		String mes = req.getParameter(CretaService.Parameter.MES.name());
		String anho = req.getParameter(CretaService.Parameter.ANHO.name());
		String tipo = req.getParameter(CretaService.Parameter.TIPO.name());
		String cccs[] = req
				.getParameterValues(CretaService.Parameter.CCC.name());
		String autorizado = req
				.getParameter(CretaService.Parameter.AUTORIZADO.name());
		boolean aceptarBasesAnteriores = AonStringUtils.equalsIgnoreCase("on",
				req.getParameter(CretaService.Parameter.ACEPTAR_BASES_ANTERIORES
						.name()));
		Borrador.generate(autorizado, mes, anho, tipo, aceptarBasesAnteriores,
				cccs, resp.getOutputStream());
	}

	@Override
	public void visitSolicitudConfirmacion(HttpServletRequest req,
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
	public void visitSolicitudCalculos(HttpServletRequest req, HttpServletResponse resp)
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
	public void visitSolicitudTrabajadoresTramos(HttpServletRequest req,
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
	
	@Override
	public void visitTrabajadoresTramos(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {


		PrintWriter os = resp.getWriter();

		os.println("<html>");
		os.println("<body>");
		os.println("<script>");
		os.println("parent.__onTrabajadoresYTramos(");
		
		os.println(
		//@formatter:off
		req.getParts()
		.stream()
		.map(part->unmarshall(net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos.class, part))
		.filter(optional->optional.isPresent())
		.map(optional->optional.get())
		.sorted(CretaServlet::compare)
		.map(t->String.format("{%s,\"file\":\"%s\"}\r\n", toJSON(t.getLiquidacion()), marshall2Json(t)))
		.collect(Collectors.joining(",", "[", "]"))
		//@formatter:on
		);
		
		os.println(",");
		
		os.println("[");
		os.println(
		//@formatter:off
		req.getParts()
		.stream()
		.map(part->unmarshall(net.aonsolutions.tgss.creta.jaxb.respuesta.Respuesta.class, part))
		.filter(optional->optional.isPresent())
		.map(optional->optional.get())
		.map(r-> r.getLiquidacion()
				.stream()
				.map(l-> String.format("{%s,\"errors\":[%s],\"file\":\"%s\"}\r\n", 
										toJSON(l), 
										toJSON(l.getErrores()),
										marshall2Json(l)
						)
					)
				.collect(Collectors.joining(","))
			)
		.collect(Collectors.joining(","))
		//@formatter:on
		);
		os.println("]");

		os.println(");");
		os.println("</script>");
		os.println("</body>");
		os.println("</html>");

		os.flush();
		os.close();

	}
	
	@Override
	public void visitRespuesta(HttpServletRequest t, HttpServletResponse l)
			throws Exception {
		// TODO Auto-generated method stub
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
	

	private static String generateBases(Connection connection, boolean comments,
			boolean skipExisting, boolean acceptPrevBases, String nafs[],
			String defaults[], List<InputStream> is, BasesCallback... cbs)
					throws JAXBException, XMLStreamException,
					FactoryConfigurationError, IOException {

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Bases.generate(connection, comments, skipExisting, acceptPrevBases,
				nafs, defaults, is, Collections.emptyList() /* respuestaIs */,
				os, cbs);
		os.close();
		return String.format("%s", URLEncoder.encode(os.toString(), "UTF-8"));

	}

	// ------------------------------------------------------------------------
	
	private static <T> String marshall2Json(T t) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			Utils.marshal(t, os);
			os.close();
			return String.format("%s", URLEncoder.encode(os.toString(), "UTF-8"));
		} catch (JAXBException |IOException e) {
		}
		return "";
	}

	private static <T> Optional<T> unmarshall(Class<T> clazz, Part part) {
		try {
			return Optional.of(Utils.unmarshal(clazz, part.getInputStream() ));
		} catch (JAXBException | IOException e) {
			return Optional.empty();
		}
	}
	
	private static int compare( 
			net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos t1, 
			net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos t2){
		return toString(t2.getLiquidacion().getPeriodoDesde()).compareTo(toString(t1.getLiquidacion().getPeriodoDesde()));
	}
	


	private static String toString(net.aonsolutions.tgss.creta.jaxb.Periodo p ) {
		return String.format("%s-%02d", p.getAnho(), Integer.parseInt(p.getMes()));
	}


	
	private static String toJSON(net.aonsolutions.tgss.creta.jaxb.Liquidacion<?, ?, ?> l){
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(String.format("\"ccc\":\"%s%s%s\",", 
				l.getCcc().getRegimen(),
				l.getCcc().getProvincia(),
				l.getCcc().getNumero()
				));
		buffer.append(String.format("\"from\":\"%s\",", 
				toString(l.getPeriodoDesde())
				));
		buffer.append(String.format("\"to\":\"%s\"", 
				toString(l.getPeriodoDesde())
				));
		return buffer.toString();
	}

	private static String toJSON(net.aonsolutions.tgss.creta.jaxb.respuesta.Errores errs){
		StringBuffer buffer = new StringBuffer();
		buffer.append(
				errs
				.getError()
				.stream()
				.map(err->
						String.format(
						"{\"code\":\"%s\", \"msg\":\"%s\"}", 
						err.getCodigoErr(), 
						err.getDescripcion()
						)
				)
				.collect(Collectors.joining(","))
				);

		errs.getError().stream().forEach(err->System.out.println(err.getDescripcion()));
		
		return buffer.toString();
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
			boolean mandatory = "B".equalsIgnoreCase(
					datoSolicitado.getIndicadorObligatoriedad());

			errors.add(new Event().setMessage(String.format(
					"Lo sentimos. %s (%s) no está soportado en AON SOLUTIONS",
					getDescription(datoSolicitado),
					mandatory ? "Obligatorio" : "Opcional")));
		}

		@Override
		public void unknownTrabajador(String ccc, Trabajador trabajador) {
			errors.add(new Event().setMessage(
					format("El trabajador (NAF:%s, CCC:%s) no encontrado en AON SOLUTIONS",
							trabajador.getNaf(), ccc)));
		}

		@Override
		public void wrongContextVariable(Salary salary, ContextVariable var,
				Period p, String right, String wrong) {
			errors.add(new Event().setMessage(format(
					"%s (IPF:%s, NAF:%s) .%s (%5$td/%5$tm/%5$tY..%6$td/%6$tm/%6$tY) incorrecta. Se esperaba '%s' y es '%s'",
					// salary.getEnterpriseName(),
					salary.getEmployeeName(), salary.getEmployeeDocument(),
					salary.getEmployeeSSNumber(),
					// salary.getEnterpriseCCC(),
					var.getName(), p.getStart(), p.getEnd(), right, wrong)));
		}

		@Override
		public void noSuchContextVariable(Salary salary, ContextVariable var,
				Period p, String right) {
			errors.add(new Event().setMessage(format(
					"%s (IPF:%s, NAF:%s) .%s (%5$td/%5$tm/%5$tY..%6$td/%6$tm/%6$tY) no encontrada. Se esperaba '%s'",
					// salary.getEnterpriseName(),
					salary.getEmployeeName(), salary.getEmployeeDocument(),
					salary.getEmployeeSSNumber(),
					// salary.getEnterpriseCCC(),
					var.getName(), p.getStart(), p.getEnd(), right)));
		}

		@Override
		public void ambigousContextVariable(Salary salary, ContextVariable var,
				Period p, String right, String... wrongs) {
			errors.add(new Event().setMessage(format(
					"%s (IPF:%s, NAF:%s) .%s (%5$td/%5$tm/%5$tY..%6$td/%6$tm/%6$tY) ambigua. Se esperaba '%s' y es %s",
					// salary.getEnterpriseName(),
					salary.getEmployeeName(), salary.getEmployeeDocument(),
					salary.getEmployeeSSNumber(),
					// salary.getEnterpriseCCC(),
					var.getName(), p.getStart(), p.getEnd(), right,
					Arrays.stream(wrongs)
							.map(wrong -> String.format("'%s'", wrong))
							.collect(Collectors.joining(",")))));
		}

		@Override
		public void unMatchedContextVariable(Salary salary, ContextVariable var,
				ContextData contextData, Dato datoSolicitado, Tramo tramo,
				TramoBuilder tramoBuilder, boolean optional) {
			errors.add(new Event().setMessage(format(
					"%s (IPF:%s, NAF:%s) .%s (%5$td/%5$tm/%5$tY..%6$td/%6$tm/%6$tY) incorrecta. Se esperaba (%s/%s/%s..%s/%s/%s)",
					// salary.getEnterpriseName(),
					salary.getEmployeeName(), salary.getEmployeeDocument(),
					salary.getEmployeeSSNumber(),
					// salary.getEnterpriseCCC(),
					var.getName(), contextData.getStartDate(),
					contextData.getEndDate(), tramo.getFechaDesde().getDia(),
					tramo.getFechaDesde().getMes(),
					tramo.getFechaDesde().getAnho(),
					tramo.getFechaHasta().getDia(),
					tramo.getFechaHasta().getMes(),
					tramo.getFechaHasta().getAnho())));
		}

		@Override
		public void noSuchDato(Salary salary, Tramo tramo, Dato datoSolicitado,
				TramoBuilder tramoBuilder, boolean optional) {
			Event event = new Event().setMessage(format(
					"%s (IPF:%s, NAF:%s) .%s (%s/%s/%s..%s/%s/%s) no encontrado. ",
					// salary.getEnterpriseName(),
					salary.getEmployeeName(), salary.getEmployeeDocument(),
					salary.getEmployeeSSNumber(),
					// salary.getEnterpriseCCC(),
					getDescription(datoSolicitado),
					tramo.getFechaDesde().getDia(),
					tramo.getFechaDesde().getMes(),
					tramo.getFechaDesde().getAnho(),
					tramo.getFechaHasta().getDia(),
					tramo.getFechaHasta().getMes(),
					tramo.getFechaHasta().getAnho()));
			if (optional)
				warnings.add(event);
			else
				errors.add(event);
		}

		// ----------------------------------------------------------- Warnings

		@Override
		public void unknownSalary(Salary salary) {
			warnings.add(new Event().setMessage(format(
					"%s (IPF:%s, NAF:%s) no encontrado en el Sistema de Liquidación Directa (Proyecto Cret@)s",
					// salary.getEnterpriseName(),
					salary.getEmployeeName(), salary.getEmployeeDocument(),
					salary.getEmployeeSSNumber()
			// salary.getEnterpriseCCC()))
			)));
		}

		// ----------------------------------------------------- Private Static

		private static String getDescription(Dato dato) {
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
				return format("El Concepto Económico de Cotización %s",
						dato.getCodigo());
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

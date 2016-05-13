package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.CRETA_RESPUESTA;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.CRETA_TRABAJADORES_Y_TRAMOS;
import static java.lang.String.format;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.code.aon.google.apis.DriveUtils;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.shared.CretaService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.payroll.Pair;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.tgss.creta.Bases;
import com.esferalia.aon.payroll.tgss.creta.Bases.BasesCallback;
import com.esferalia.aon.payroll.tgss.creta.Bases.EmptyBasesException;
import com.esferalia.aon.payroll.tgss.creta.DCL.LineaSalary;
import com.esferalia.aon.payroll.tgss.creta.Borrador;
import com.esferalia.aon.payroll.tgss.creta.Calculo;
import com.esferalia.aon.payroll.tgss.creta.Confirmacion;
import com.esferalia.aon.payroll.tgss.creta.DBA;
import com.esferalia.aon.payroll.tgss.creta.DCL;
import com.esferalia.aon.payroll.tgss.creta.IndentXMLStreamWriter;
import com.esferalia.aon.payroll.tgss.creta.TrabajadoresTramos;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.tgss.creta.jaxb.Dato;
import net.aonsolutions.tgss.creta.jaxb.DatoSolicitado;
import net.aonsolutions.tgss.creta.jaxb.Liquidacion;
import net.aonsolutions.tgss.creta.jaxb.Periodo;
import net.aonsolutions.tgss.creta.jaxb.Trabajador;
import net.aonsolutions.tgss.creta.jaxb.Tramo;
import net.aonsolutions.tgss.creta.jaxb.Utils;
import net.aonsolutions.tgss.creta.jaxb.bases.LiquidacionBuilder;
import net.aonsolutions.tgss.creta.jaxb.bases.TramoBuilder;
import net.aonsolutions.tgss.creta.jaxb.dcl.LineaDCL;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "SLD-Solicitud", urlPatterns = { "/aon_gwt_payroll/sdl/*" })
public class CretaServlet extends HttpServlet
		implements CretaService.File.Visitor<HttpServletRequest, HttpServletResponse, Exception> {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

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
	public void visitBases(HttpServletRequest req, HttpServletResponse resp) throws Exception {

		Connection connection = getConnection();
		resp.setContentType("text/html;");

		String nafs[] = req.getParameterValues(CretaService.Parameter.NAFS.name());

		List<String> defaultsList = new ArrayList<String>();
		defaultsList.addAll(Arrays.asList("51=M", "737=0"));

		String paramDefaults[] = req.getParameterValues(CretaService.Parameter.DEFAULTS.name());
		if (paramDefaults != null && paramDefaults.length > 0)
			defaultsList.addAll(Arrays.asList(paramDefaults));

		String defaults[] = defaultsList.toArray(new String[defaultsList.size()]);

		EventsPickerBasesCallback pickerBasesCb = new EventsPickerBasesCallback();

		PrintWriter os = resp.getWriter();

		os.println("{");

		List<InputStream> respuestasIss = new ArrayList<InputStream>();
		List<InputStream> trabajadoresYTramosIss = new ArrayList<InputStream>();
		for (Part part : req.getParts()) {
			try {
				CretaService.File file = CretaService.File.valueOf(part.getName());
				if (file == CretaService.File.TRABAJADORES_TRAMOS)
					trabajadoresYTramosIss.add(part.getInputStream());
				else if (file == CretaService.File.RESPUESTA)
					respuestasIss.add(part.getInputStream());
			} catch (IllegalArgumentException e) {

			}
		}

		try {
			os.printf("\"full_bases\":\"%s\",\r\n", generateBases(connection, true, false, false, nafs, defaults,
					trabajadoresYTramosIss, respuestasIss, pickerBasesCb));
		} catch (EmptyBasesException e) {
			os.printf("\"full_bases\":\"\",\r\n");
		}

		respuestasIss.clear();
		trabajadoresYTramosIss.clear();
		for (Part part : req.getParts()) {
			try {
				CretaService.File file = CretaService.File.valueOf(part.getName());
				if (file == CretaService.File.TRABAJADORES_TRAMOS)
					trabajadoresYTramosIss.add(part.getInputStream());
				else if (file == CretaService.File.RESPUESTA)
					respuestasIss.add(part.getInputStream());
			} catch (IllegalArgumentException e) {

			}
		}

		NoDiffsBasesCallback noDiffsBasesCb = new NoDiffsBasesCallback() {
			@Override
			public void noDiffs(net.aonsolutions.tgss.creta.jaxb.bases.Liquidacion liquidacion) {
				super.noDiffs(liquidacion);
				pickerBasesCb.noDiffs(liquidacion);
			}
		};
		try {
			os.printf("\"diff_bases\":\"%s\",\r\n", generateBases(connection, true, true, true, nafs, defaults,
					trabajadoresYTramosIss, respuestasIss, noDiffsBasesCb));
		} catch (EmptyBasesException e) {
			os.printf("\"draft_request\":\"%s\",\r\n",
					generateBorrador(e.getAutorizado(), noDiffsBasesCb.getMeses(), noDiffsBasesCb.getAnhos(),
							noDiffsBasesCb.getTipos(), noDiffsBasesCb.getAceptarBasesAnteriores(),
							noDiffsBasesCb.getCCCs()));
		}

		os.printf("\"errors\":%s,\r\n", toJSON(pickerBasesCb.errors));

		os.printf("\"unknown\":%s,\r\n", toJSON(pickerBasesCb.unknown));

		os.printf("\"warnings\":%s,\r\n", toJSON(pickerBasesCb.warnings));

		os.printf("\"messages\":[]\r\n");

		os.println("}");

		os.flush();
		os.close();

	}

	@Override
	public void visitSolicitudBorrador(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		resp.setContentType("text/xml;");
		String tipo = req.getParameter(CretaService.Parameter.TIPO.name());
		String cccs[] = req.getParameterValues(CretaService.Parameter.CCC.name());
		String desdeMes = req.getParameter(CretaService.Parameter.DESDE_MES.name());
		String desdeAnho = req.getParameter(CretaService.Parameter.DESDE_ANHO.name());
		String hastaMes = req.getParameter(CretaService.Parameter.HASTA_MES.name());
		String hastaAnho = req.getParameter(CretaService.Parameter.HASTA_ANHO.name());
		String autorizado = req.getParameter(CretaService.Parameter.AUTORIZADO.name());
		boolean aceptarBasesAnteriores = AonStringUtils.equalsIgnoreCase("on",
				req.getParameter(CretaService.Parameter.ACEPTAR_BASES_ANTERIORES.name()));
		Borrador.generate(autorizado, desdeMes, desdeAnho, hastaMes, hastaAnho, tipo, aceptarBasesAnteriores, cccs, resp.getOutputStream());
	}

	@Override
	public void visitSolicitudConfirmacion(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		resp.setContentType("text/xml;");
		String tipo = req.getParameter(CretaService.Parameter.TIPO.name());
		String cccs[] = req.getParameterValues(CretaService.Parameter.CCC.name());
		String desdeMes = req.getParameter(CretaService.Parameter.DESDE_MES.name());
		String desdeAnho = req.getParameter(CretaService.Parameter.DESDE_ANHO.name());
		String hastaMes = req.getParameter(CretaService.Parameter.HASTA_MES.name());
		String hastaAnho = req.getParameter(CretaService.Parameter.HASTA_ANHO.name());
		String autorizado = req.getParameter(CretaService.Parameter.AUTORIZADO.name());
		Confirmacion.generate(autorizado, desdeMes, desdeAnho, hastaMes, hastaAnho, tipo, cccs, resp.getOutputStream());
	}

	@Override
	public void visitSolicitudCalculos(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		resp.setContentType("text/xml;");
		String tipo = req.getParameter(CretaService.Parameter.TIPO.name());
		String cccs[] = req.getParameterValues(CretaService.Parameter.CCC.name());
		String desdeMes = req.getParameter(CretaService.Parameter.DESDE_MES.name());
		String desdeAnho = req.getParameter(CretaService.Parameter.DESDE_ANHO.name());
		String hastaMes = req.getParameter(CretaService.Parameter.HASTA_MES.name());
		String hastaAnho = req.getParameter(CretaService.Parameter.HASTA_ANHO.name());
		String autorizado = req.getParameter(CretaService.Parameter.AUTORIZADO.name());
		Calculo.generate(autorizado,desdeMes, desdeAnho, hastaMes, hastaAnho, tipo, cccs, resp.getOutputStream());
	}

	@Override
	public void visitSolicitudTrabajadoresTramos(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		resp.setContentType("text/xml;");
		String tipo = req.getParameter(CretaService.Parameter.TIPO.name());
		String cccs[] = req.getParameterValues(CretaService.Parameter.CCC.name());
		String desdeMes = req.getParameter(CretaService.Parameter.DESDE_MES.name());
		String desdeAnho = req.getParameter(CretaService.Parameter.DESDE_ANHO.name());
		String hastaMes = req.getParameter(CretaService.Parameter.HASTA_MES.name());
		String hastaAnho = req.getParameter(CretaService.Parameter.HASTA_ANHO.name());
		String ctrlMes = req.getParameter(CretaService.Parameter.CTRL_MES.name());
		String ctrlAnho = req.getParameter(CretaService.Parameter.CTRL_ANHO.name());
		String autorizado = req.getParameter(CretaService.Parameter.AUTORIZADO.name());
		TrabajadoresTramos.generate(autorizado, desdeMes, desdeAnho, hastaMes, hastaAnho, ctrlMes, ctrlAnho, tipo, cccs, resp.getOutputStream());
	}

	@Override
	public void visitTrabajadoresTramos(HttpServletRequest req, HttpServletResponse resp) throws Exception {

		PrintWriter os = resp.getWriter();

		os.println("<html>");
		os.println("<body>");
		os.println("<script>");
		
		
		//@formatter:off
		__onTrabajadoresYTramos(os, 
				Stream.concat(
					req.getParts().stream()
					.map(part -> unmarshall(net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos.class, part))
					.filter(optional -> optional.isPresent())
					.map(optional -> optional.get())
					.peek(t -> {saveTrabajadoresYTramos(req, t);}),
					findTrabajadoresYTramos(req)
				),
				Stream.concat(
					req.getParts().stream()
					.map(part -> unmarshall(net.aonsolutions.tgss.creta.jaxb.respuesta.Respuesta.class, part))
					.filter(optional -> optional.isPresent())
					.map(optional -> optional.get())
					.peek(r -> {saveRespuesta(req, r);}),
					findRespuestas(req)
				)
		);
		//@formatter:on
		
		os.println("</script>");
		os.println("</body>");
		os.println("</html>");

		os.flush();
		os.close();

	}

	@Override
	public void visitRespuesta(HttpServletRequest t, HttpServletResponse l) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void visitComunicacionDatosBancarios(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		resp.setContentType("text/xml;");

		String autorizado = req.getParameter(CretaService.Parameter.AUTORIZADO.name());
		String cccs[] = req.getParameterValues(CretaService.Parameter.CCC.name());
		String tipoMoviento = req.getParameter(CretaService.Parameter.TIPO_MOVIMIENTO.name());
		String tipoAccion = req.getParameter(CretaService.Parameter.TIPO_ACCION.name());
		String iban = req.getParameter(CretaService.Parameter.IBAN.name());
		String titular = req.getParameter(CretaService.Parameter.TITULAR.name());
		String documento = AonStringUtils.leftPad(req.getParameter(CretaService.Parameter.DOCUMENTO.name()), 10, '0');
		String tipoDocumento = req.getParameter(CretaService.Parameter.TIPO_DOCUMENTO.name());

		// @formatter:off
		DBA.generate(autorizado, cccs, tipoMoviento, tipoAccion, iban, titular, documento, tipoDocumento,
				resp.getOutputStream());
		// @formatter:on
	}
	
	
	@Override
	public void visitDocumentoCalculoLiquidacion(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		PrintWriter os = resp.getWriter();
		
		

		os.println("<html>");
		os.println("<body>");
		os.println("<script>");
		
		
		//@formatter:off
		__onDocumentoCalculoLiquidacion(
					getConnection(), 
					os, 
					req.getParts().stream()
					.map(part -> unmarshall(net.aonsolutions.tgss.creta.jaxb.dcl.DCL.class, part))
					.filter(optional -> optional.isPresent())
					.map(optional -> optional.get())
		);
		//@formatter:on
		
		os.println("</script>");
		os.println("</body>");
		os.println("</html>");

		os.flush();
		os.close();
		
	}

	// ------------------------------------------------------------------------

	private static String generateBases(Connection connection, boolean comments, boolean skipExisting,
			boolean acceptPrevBases, String nafs[], String defaults[], InputStream is, BasesCallback... cbs)
					throws EmptyBasesException, JAXBException, XMLStreamException, FactoryConfigurationError,
					IOException {

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Bases.generate(connection, comments, skipExisting, acceptPrevBases, nafs, defaults, is, null /* respuestaIs */,
				os, cbs);
		os.close();
		return String.format("%s", URLEncoder.encode(os.toString(), "UTF-8"));

	}

	private static String generateBases(Connection connection, boolean comments, boolean skipExisting,
			boolean acceptPrevBases, String nafs[], String defaults[], List<InputStream> trabajadoresYTramosIss,
			List<InputStream> respuestasIss, BasesCallback... cbs) throws EmptyBasesException, JAXBException,
					XMLStreamException, FactoryConfigurationError, IOException {

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Bases.generate(connection, comments, skipExisting, acceptPrevBases, nafs, defaults, trabajadoresYTramosIss,
				respuestasIss, os, cbs);
		os.close();

		return String.format("%s", URLEncoder.encode(os.toString(), "UTF-8"));

	}

	private static String generateBorrador(String autorizado, String meses[], String anhos[], String tipos[],
			Boolean aceptarBasesAnteriores[], String cccs[])
					throws JAXBException, IOException, XMLStreamException, FactoryConfigurationError {

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		XMLStreamWriter xsw = new IndentXMLStreamWriter(XMLOutputFactory.newInstance().createXMLStreamWriter(os),
				"  ") {

			@Override
			public void writeStartElement(String prefix, String localName, String namespaceURI)
					throws XMLStreamException {
				if (localName.equalsIgnoreCase("Liquidacion"))
					beforeLiquidacion();
				else if (localName.equalsIgnoreCase("AceptarBasesAnteriores"))
					beforeAceptarBasesAnteriores();
				super.writeStartElement(prefix, localName, namespaceURI);
			}

			public void beforeLiquidacion() throws XMLStreamException {
				super.writeComment("\r\nNo es necesario comunicar nada nuevo respecto\r\n"
						+ "a la informaci\u00F3n del mes anterior.\r\n");
			}

			public void beforeAceptarBasesAnteriores() throws XMLStreamException {
				super.writeComment("\r\nSi se utiliza como v\u00EDa de inicio, el usuario\r\n"
						+ "deber\u00E1 solicitar la recuperaci\u00F3n de las bases\r\n"
						+ "del mes anterior v\u00E1lidas a efectos de c\u00E1lculo.\r\n");
			}

		};

		// @formatter:off
		Borrador.generate(autorizado, meses, anhos, tipos, aceptarBasesAnteriores, cccs, xsw);
		// @formatter:on

		os.close();

		return String.format("%s", URLEncoder.encode(os.toString(), "UTF-8"));

	}
	// ------------------------------------------------------------------------

	private static <T> byte[] marshall(T t) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			XMLStreamWriter xsw = new IndentXMLStreamWriter(XMLOutputFactory.newInstance().createXMLStreamWriter(os),
					"  ");
			Utils.marshal(t, xsw);
			os.close();
			return os.toByteArray();
		} catch (JAXBException | IOException | XMLStreamException e) {
		}
		return new byte[0];
	}

	private static <T> String marshallAndEncode(T t) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			XMLStreamWriter xsw = new IndentXMLStreamWriter(XMLOutputFactory.newInstance().createXMLStreamWriter(os),
					"  ");
			Utils.marshal(t, xsw);
			os.close();
			return String.format("%s", URLEncoder.encode(os.toString(), "UTF-8"));
		} catch (JAXBException | IOException | XMLStreamException e) {
		}
		return "";
	}

	private static <T> Optional<T> unmarshall(Class<T> clazz, Part part) {
		try {
			return Optional.of(Utils.unmarshal(clazz, part.getInputStream()));
		} catch (JAXBException | IOException e) {
			
			return Optional.empty();
		}
	}

	private static <T> Optional<T> unmarshall(Class<T> clazz, byte [] bytes) {
		InputStream is = new ByteArrayInputStream(bytes);
		try {
			return Optional.of(Utils.unmarshal(clazz, is));
		} catch (JAXBException e) {
			return Optional.empty();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
	}

	private static int compare(net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos t1,
			net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos t2) {
		return toString(t2.getLiquidacion().getPeriodoDesde())
				.compareTo(toString(t1.getLiquidacion().getPeriodoDesde()));
	}

	private static String toString(net.aonsolutions.tgss.creta.jaxb.Fecha f) {
		return String.format("%s-%02d-%02d", f.getAnho(), Integer.parseInt(f.getMes()), Integer.parseInt(f.getDia()));
	}

	private static String toString(net.aonsolutions.tgss.creta.jaxb.Periodo p) {
		return String.format("%s-%02d", p.getAnho(), Integer.parseInt(p.getMes()));
	}

	private static String toJSON(net.aonsolutions.tgss.creta.jaxb.Liquidacion<?, ?, ?, ?, ?> l) {

		StringBuffer buffer = new StringBuffer();
		buffer.append(String.format("\"ccc\":\"%s%s%s\",", l.getCcc().getRegimen(), l.getCcc().getProvincia(),
				l.getCcc().getNumero()));
		buffer.append(String.format("\"type\":\"%s\",", l.getTipo()));
		buffer.append(String.format("\"from\":\"%s\",", toString(l.getPeriodoDesde())));
		buffer.append(String.format("\"to\":\"%s\",", toString(l.getPeriodoDesde())));
		buffer.append(String.format("\"date\":\"%s\",", toString(l.getFechaHoraRecaudacion().getFechaRecaudacion())));
		buffer.append(String.format("\"time\":\"%s\"", l.getFechaHoraRecaudacion().getHoraRecaudacion()));
		return buffer.toString();
	}

	private static String toJSON(net.aonsolutions.tgss.creta.jaxb.respuesta.Errores errs) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(errs.getError().stream().map(err -> String.format("{\"code\":\"%s\", \"msg\":\"%s\"}",
				err.getCodigoErr(), safeEncode(err.getDescripcion()))).collect(Collectors.joining(",")));

//		errs.getError().stream().forEach(err -> System.out.println(err.getDescripcion()));

		return buffer.toString();
	}

	private static String toJSON(net.aonsolutions.tgss.creta.jaxb.respuesta.Trabajadores trabajadores) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(trabajadores.getTrabajador().stream()
				.map(trabajador -> String.format("{\"naf\":\"%s\"}", trabajador.getNaf()))
				.collect(Collectors.joining(",")));

		return buffer.toString();
	}

	private static String toJSON(Stream<net.aonsolutions.tgss.creta.jaxb.respuesta.LiquidacionMes> liquidacionesMes) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(
				liquidacionesMes.map(liquidacionMes -> String.format("%s", toJSON(liquidacionMes.getTrabajadores())))
						.collect(Collectors.joining(",")));

		return buffer.toString();
	}

	// ------------------------------------------------------------------------

	private static interface JSON {
		String toJSON();
	}

	private static class Event<T extends Event<?>> implements JSON {

		private String message;

		public String getMessage() {
			return message;
		}

		public T setMessage(String message) {
			this.message = message;
			return (T) this;
		}

		// --------------------------------------------------------------- JSON

		@Override
		public String toJSON() {
			return String.format("{" + "\"message\":\"%s\",\r\n" + "}", message);
		}
	}

	private static class UnknownDato extends Event<UnknownDato> {

		private DatoSolicitado dato;
		private Liquidacion<?, ?, ?, ?, ?> liquidacion;

		public UnknownDato setDato(DatoSolicitado dato) {
			this.dato = dato;
			return this;
		}

		public UnknownDato setLiquidacion(Liquidacion<?, ?, ?, ?, ?> liquidacion) {
			this.liquidacion = liquidacion;
			return this;
		}

		// --------------------------------------------------------------- JSON

		@Override
		public String toJSON() {
			return String.format(
					"{" + "\"message\":\"%s\",\r\n" + "\"type\":\"%s\",\r\n" + "\"code\":\"%s\",\r\n"
							+ "\"mandatory\":%s\r\n" + "}",
					getMessage(), dato.getTipoDato(), dato.getCodigo(), "B".equals(dato.getIndicadorObligatoriedad()));
		}

	}

	private static class NoDiffs extends Event<NoDiffs> {
		private net.aonsolutions.tgss.creta.jaxb.bases.Liquidacion liquidacion;

		public NoDiffs setLiquidacion(net.aonsolutions.tgss.creta.jaxb.bases.Liquidacion liquidacion) {
			this.liquidacion = liquidacion;
			return this;
		}

		// --------------------------------------------------------------- JSON

		@Override
		public String toJSON() {
			return String.format("{" + "\"message\":\"%s\",\r\n" + "}", getMessage());
		}
	}

	private static class NoDiffsBasesCallback implements BasesCallback {

		List<net.aonsolutions.tgss.creta.jaxb.bases.Liquidacion> liquidaciones = new ArrayList<net.aonsolutions.tgss.creta.jaxb.bases.Liquidacion>();

		@Override
		public void noDiffs(net.aonsolutions.tgss.creta.jaxb.bases.Liquidacion liquidacion) {
			liquidaciones.add(liquidacion);
		}

		// --------------------------------------------------------------------

		String[] getCCCs() {
			// @formatter:off
			return liquidaciones.stream().map(l -> l.getCcc())
					.map(ccc -> ccc.getRegimen() + ccc.getProvincia() + ccc.getNumero()).toArray(String[]::new);
			// @formatter:on
		}

		String[] getTipos() {
			// @formatter:off
			return liquidaciones.stream().map(l -> l.getTipo()).toArray(String[]::new);
			// @formatter:on
		}

		String[] getMeses() {
			// @formatter:off
			return liquidaciones.stream().map(l -> l.getPeriodoDesde().getMes()).toArray(String[]::new);
			// @formatter:on
		}

		String[] getAnhos() {
			// @formatter:off
			return liquidaciones.stream().map(l -> l.getPeriodoDesde().getAnho()).toArray(String[]::new);
			// @formatter:on
		}

		Boolean[] getAceptarBasesAnteriores() {
			// @formatter:off
			return liquidaciones.stream().map(l -> "S".equalsIgnoreCase(l.getAceptarBasesAnteriores()))
					.toArray(Boolean[]::new);
			// @formatter:on
		}

	}

	private static class EventsPickerBasesCallback implements BasesCallback {

		private List<Event> errors = new ArrayList<Event>();
		private List<Event> warnings = new ArrayList<Event>();

		private List<UnknownDato> unknown = new ArrayList<UnknownDato>();

		// ------------------------------------------------------------- Errors

		@Override
		public void unknownDato(Liquidacion<?, ?, ?, ?, ?> liquidacion, DatoSolicitado datoSolicitado,
				LiquidacionBuilder liquidacionBuilder) {
			boolean mandatory = "B".equalsIgnoreCase(datoSolicitado.getIndicadorObligatoriedad());
			String message = String.format(

					"Lo sentimos. %s (%s) no está soportado en AON SOLUTIONS ( Liquidaci\u00F3n %s%s%s).",

					getDescription(datoSolicitado),

					mandatory ? "Obligatorio" : "Opcional",

					liquidacion.getCcc().getProvincia(), liquidacion.getCcc().getRegimen(),
					liquidacion.getCcc().getNumero()

			);

			UnknownDato event = new UnknownDato().setMessage(message).setDato(datoSolicitado)
					.setLiquidacion(liquidacion);

			unknown.add(event);
		}

		@Override
		public void unknownDato(Salary salary, Tramo tramo, DatoSolicitado datoSolicitado, TramoBuilder tramoBuilder) {

			boolean mandatory = "B".equalsIgnoreCase(datoSolicitado.getIndicadorObligatoriedad());
			String message = String.format("Lo sentimos. %s (%s) no está soportado en AON SOLUTIONS",
					getDescription(datoSolicitado), mandatory ? "Obligatorio" : "Opcional");

			UnknownDato event = new UnknownDato().setMessage(message).setDato(datoSolicitado);

			unknown.add(event);

		}

		@Override
		public void salaryNotFound(String ccc, Trabajador<?> trabajador, Periodo mes) {

			errors.add(new Event().setMessage(
					format("No se ha encontrado n\u00F3mina del %s del %s para el trabajador (NAF:%s, CCC:%s) ",
							mes.getMes(), mes.getAnho(), trabajador.getNaf(), ccc)));
		}

		@Override
		public void wrongContextVariable(Salary salary, ContextVariable var, Period p, String right, String wrong) {
			if (right == null)
				warnings.add(new Event().setMessage(
						format("%s (IPF:%s, NAF:%s) .%s (%5$td/%5$tm/%5$tY..%6$td/%6$tm/%6$tY) incorrecta. No se esperaba y es '%7$s'",
								salary.getEmployeeName(), salary.getEmployeeDocument(), salary.getEmployeeSSNumber(),
								var.getName(), p.getStart(), p.getEnd(), wrong)));
			else
				warnings.add(new Event().setMessage(
						format("%s (IPF:%s, NAF:%s) .%s (%5$td/%5$tm/%5$tY..%6$td/%6$tm/%6$tY) incorrecta. Se esperaba '%7$s' y es '%8$s'",
								salary.getEmployeeName(), salary.getEmployeeDocument(), salary.getEmployeeSSNumber(),
								var.getName(), p.getStart(), p.getEnd(), right, wrong)));
		}

		@Override
		public void noSuchContextVariable(Salary salary, ContextVariable var, Period p, String right) {
			warnings.add(new Event().setMessage(format(
					"%s (IPF:%s, NAF:%s) .%s (%5$td/%5$tm/%5$tY..%6$td/%6$tm/%6$tY) no encontrada. Se esperaba '%7$s'",
					// salary.getEnterpriseName(),
					salary.getEmployeeName(), salary.getEmployeeDocument(), salary.getEmployeeSSNumber(),
					// salary.getEnterpriseCCC(),
					var.getName(), p.getStart(), p.getEnd(), right)));
		}

		@Override
		public void ambigousContextVariable(Salary salary, ContextVariable var, Period p, String right,
				String... wrongs) {
			warnings.add(new Event().setMessage(format(
					"%s (IPF:%s, NAF:%s) .%s (%5$td/%5$tm/%5$tY..%6$td/%6$tm/%6$tY) ambigua. Se esperaba '%7$s' y es %8$s",
					// salary.getEnterpriseName(),
					salary.getEmployeeName(), salary.getEmployeeDocument(), salary.getEmployeeSSNumber(),
					// salary.getEnterpriseCCC(),
					var.getName(), p.getStart(), p.getEnd(), right, Arrays.stream(wrongs)
							.map(wrong -> String.format("'%s'", wrong)).collect(Collectors.joining(",")))));
		}

		@Override
		public void unMatchedContextVariable(Salary salary, ContextVariable var, ContextData contextData,
				Dato datoSolicitado, Tramo tramo, TramoBuilder tramoBuilder, boolean optional) {
			errors.add(new Event().setMessage(format(
					"%s (IPF:%s, NAF:%s) .Tramo para %s (%5$td/%5$tm/%5$tY..%6$td/%6$tm/%6$tY) incorrecto se esperaba (%7$s/%8$s/%9$s...%10$s/%11$s/%12$s)",
					// salary.getEnterpriseName(),
					salary.getEmployeeName(), salary.getEmployeeDocument(), salary.getEmployeeSSNumber(),
					// salary.getEnterpriseCCC(),
					var.getName(),

					contextData.getStartDate(), contextData.getEndDate(),

					tramo.getFechaDesde().getDia(), tramo.getFechaDesde().getMes(), tramo.getFechaDesde().getAnho(),

					tramo.getFechaHasta().getDia(), tramo.getFechaHasta().getMes(), tramo.getFechaHasta().getAnho())));

		}

		@Override
		public void noSuchDato(Salary salary, Tramo tramo, Dato datoSolicitado, TramoBuilder tramoBuilder,
				boolean optional) {
			Event event = new Event().setMessage(format("%s (IPF:%s, NAF:%s) .%s (%s/%s/%s..%s/%s/%s) no encontrado. ",
					// salary.getEnterpriseName(),
					salary.getEmployeeName(), salary.getEmployeeDocument(), salary.getEmployeeSSNumber(),
					// salary.getEnterpriseCCC(),
					getDescription(datoSolicitado), tramo.getFechaDesde().getDia(), tramo.getFechaDesde().getMes(),
					tramo.getFechaDesde().getAnho(), tramo.getFechaHasta().getDia(), tramo.getFechaHasta().getMes(),
					tramo.getFechaHasta().getAnho()));
			if (optional)
				warnings.add(event);
			else
				errors.add(event);
		}

		// ----------------------------------------------------------- Warnings

		@Override
		public void unknownSalary(Salary salary) {
			warnings.add(new Event().setMessage(
					format("%s (IPF:%s, NAF:%s) no encontrado en el Sistema de Liquidación Directa (Proyecto Cret@)s",
							// salary.getEnterpriseName(),
							salary.getEmployeeName(), salary.getEmployeeDocument(), salary.getEmployeeSSNumber()
			// salary.getEnterpriseCCC()))
			)));
		}

		@Override
		public void noDiffs(net.aonsolutions.tgss.creta.jaxb.bases.Liquidacion liquidacion) {
			warnings.add(new NoDiffs().setMessage(
					format("No es necesario comunicar nada nuevo respecto a la información del mes anterior (%s%s%s).",
							liquidacion.getCcc().getProvincia(), liquidacion.getCcc().getRegimen(),
							liquidacion.getCcc().getNumero()))
					.setLiquidacion(liquidacion));
		}

		// ----------------------------------------------------- Private Static

		private static String getDescription(Dato dato) {
			switch (dato.getCodigo()) {
			case "02":
				return "El Número de Horas Complementarias (horas 02) ";
			case "03":
				return "Número de horas de formación teórica presencial (horas 03)";
			case "04":
				return "Número de horas de formación teórica a distancia (horas 04)";
			case "06":
				return "Número de horas tutoría (horas 06)";
			case "501":
				return "La Base de Horas Extras Fuerza Mayor (concepto 501)";
			case "502":
				return "La Base de Otras Horas Extras  (concepto 502)";
			case "737":
				return "Bonificación tutoría (concepto 737)";
			case "563":
				return "Compensación IT contingencias comunes (concepto 563)";
			case "663":
				return "Compensación IT AT y EP (concepto 663)";
			case "763":
				return "Bonificaciones de formación continua IT AT y EP (concepto 763)";
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
		buff.append(jsons.stream().map(json -> json.toJSON()).collect(Collectors.joining(",")));
		buff.append("\r\n]");
		return buff.toString();
	}

	private static String safeEncode(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return str;
		}
	}

	
	private static Stream<net.aonsolutions.tgss.creta.jaxb.respuesta.Respuesta> findRespuestas(HttpServletRequest req){
		String login = AonServletUtils.getRequestUser(req);
		Integer domainId = AonServletUtils.getRequestDomain(req);
		String domainName = AonServletUtils.getRequestDomainName(req);
		return
		findAttachs(domainName, domainId, login, RegistryAttachmentType.CRETA_RESPUESTA)
		.map(attach-> unmarshall(net.aonsolutions.tgss.creta.jaxb.respuesta.Respuesta.class, attach.getData()))
		.filter(optional -> optional.isPresent())
		.map(optional -> optional.get())
		;
	}

	private static Stream<net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos> findTrabajadoresYTramos(HttpServletRequest req){
		String login = AonServletUtils.getRequestUser(req);
		Integer domainId = AonServletUtils.getRequestDomain(req);
		String domainName = AonServletUtils.getRequestDomainName(req);
		return
		findAttachs(domainName, domainId, login, RegistryAttachmentType.CRETA_TRABAJADORES_Y_TRAMOS)
		.map(attach-> unmarshall(net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos.class, attach.getData()))
		.filter(optional -> optional.isPresent())
		.map(optional -> optional.get())
		;
	}
	
	private static void saveRespuesta(HttpServletRequest req,
			net.aonsolutions.tgss.creta.jaxb.respuesta.Respuesta t) {
		saveAttach(req, CRETA_RESPUESTA, t);
	}

	private static void saveTrabajadoresYTramos(HttpServletRequest req,
			net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos t) {
		saveAttach(req, CRETA_TRABAJADORES_Y_TRAMOS, t);
	}
	
	
	private static <T> void saveAttach(HttpServletRequest req, RegistryAttachmentType type, T t) {

		Date now = Calendar.getInstance().getTime();

		byte data [] = marshall(t);
		String md5 = AonFileUtils.getMD5Checksum(data);

		String login = AonServletUtils.getRequestUser(req);
		Integer domainId = AonServletUtils.getRequestDomain(req);
		String domainName = AonServletUtils.getRequestDomainName(req);

		Attach attach = getAttach(domainName, domainId, login, type, md5);

		if (attach != null) 
			return; // already saved

		Company  company = AON.getCompanyForDomain(domainName, domainId, login);
		

		attach = new Attach(AttachType.REGISTRY);

		attach.setDate(now);
		attach.setDescription(md5);
		attach.setConfidential(true);
		attach.setAttachModule(company.getId());
		
		attach.setCreationDate(now);
		attach.setCreationUser(login);
		attach.setModificationDate(now);
		attach.setModificationUser(login);

		attach.setData(data);
		attach.setMimeType(MimeType.XML);
		attach.setType((short) type.ordinal());
		attach.setDparentId(Integer.toString(data.length));
		attach.setDomain(new Domain().setId(domainId).setName(domainName));

		AON.insert(domainName, domainId, login, attach);
	}

	private static Attach getAttach(String domainName, Integer domainId, String login, RegistryAttachmentType type, String md5) {

		return  AON.getAttach(
				domainName, 
				domainId, 
				login,
				p -> 
				p.getDomainProperty().eq(domainId)
				.and(p.getTypeProperty().eq((byte)type.ordinal()))
				.and(p.getDescriptionProperty().eq(md5)), 
				AttachType.REGISTRY
				);

	}
	
	private static Stream<Attach> findAttachs(String domainName, Integer domainId, String login, RegistryAttachmentType type ) {
		return  AON.getAttachList(
				domainName, 
				domainId, 
				login,
				p -> 
				p.getDomainProperty().eq(domainId)
				.and(p.getTypeProperty().eq((byte)type.ordinal())), 
				AttachType.REGISTRY
				)
				.stream()
				.filter(a -> checkData(a, login));
		
	}
	
	private static void __onTrabajadoresYTramos (PrintWriter os, 
			Stream<net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos> ts,
			Stream<net.aonsolutions.tgss.creta.jaxb.respuesta.Respuesta> rs) {

			os.println("parent.__onTrabajadoresYTramos(");

			os.println(
			// @formatter:off
						ts
						.sorted(CretaServlet::compare)
						.map(t -> String.format("{\"name\":\"%s\",%s,\"file\":\"%s\"}\r\n",CretaService.File.TRABAJADORES_TRAMOS, toJSON(t.getLiquidacion()), marshallAndEncode(t)))
						.collect(Collectors.joining(",", "[", "]"))
			// @formatter:on
			);

			os.println(",");

			os.println("[");
			os.println(
			// @formatter:off
				rs
				.map(r -> r.getLiquidacion().stream()
						.map(l -> String.format("{\"name\":\"%s\",%s,\"errors\":[%s],\"employees\":[%s],\"file\":\"%s\"}\r\n",CretaService.File.RESPUESTA, toJSON(l), toJSON(l.getErrores()),toJSON(l.getLiquidacionMes().stream()), marshallAndEncode(r)))
						.collect(Collectors.joining(","))
					)
				.filter(s-> AonStringUtils.isNotBlank(s))
				.collect(Collectors.joining(","))
			// @formatter:on
			);
			os.println("]");

			os.println(");");
		
	}
	
	private static void __onDocumentoCalculoLiquidacion(Connection connection, PrintWriter os, 
			Stream<net.aonsolutions.tgss.creta.jaxb.dcl.DCL> dlcs) {
		
			
			
			List<Pair<LineaDCL, LineaSalary>> errors = new LinkedList<Pair<LineaDCL, LineaSalary>>();  
			List<Pair<LineaDCL, LineaSalary>> success = new LinkedList<Pair<LineaDCL, LineaSalary>>();  
			
			// @formatter:off
			dlcs
			.forEach(dcl -> DCL.check(connection, dcl,  ( lineaDCL, lineaSalary )-> (DCL.checkEquals(lineaDCL, lineaSalary ) ? success: errors).add(new Pair<LineaDCL, LineaSalary>(lineaDCL, lineaSalary))));
			// @formatter:on

			
			
			
			os.println("parent.__onDocumentoCalculoLiquidacion(");
			os.println(
			// @formatter:off
						success.stream()
						.map(pair -> String.format(
								"{"
								+ "\"description\":\"%s\""
								+ ",\"sldBase\":%s"
								+ ",\"sldImporte\":%s"
								+ ",\"aonBase\":%d"
								+ ",\"aonImporte\":%d"
								+ ",\"message\":\"%s: Con base %.2f e importe %.2f correcto.\""
								+ "}\r\n", 
								pair.fst.getDescripcionLDCL()
								, pair.fst.getBaseLDCL()
								, pair.fst.getImporteLDCL()
								, pair.snd.getBase()
								, pair.snd.getImporte()
								
								,pair.fst.getDescripcionLDCL()
								, pair.snd.getBase()/100.00
								, pair.snd.getImporte()/100.00
								))
						.collect(Collectors.joining(",", "[", "]"))
			// @formatter:on
			);
			os.println(",");
			os.println(
			// @formatter:off
						errors.stream()
						.map(pair -> String.format(
								"{"
								+ "\"description\":\"%s\""
								+ ",\"sldBase\":%s"
								+ ",\"sldImporte\":%s"
								+ ",\"aonBase\":%d"
								+ ",\"aonImporte\":%d"
								+ ",\"message\":\"%s : %s %s\""
								+ "}\r\n", 
								pair.fst.getDescripcionLDCL()
								, pair.fst.getBaseLDCL()
								, pair.fst.getImporteLDCL()
								, pair.snd.getBase()
								, pair.snd.getImporte()

								,pair.fst.getDescripcionLDCL()
								, pair.snd.getBase() != (long) Long.parseLong(pair.fst.getBaseLDCL()) ? String.format("Base incorrecta se esperaba %.2f y es %.2f.", pair.snd.getBase()/100.00, (long) Long.parseLong(pair.fst.getBaseLDCL())/100.00  ) : ""
								, pair.snd.getImporte() != (long) Long.parseLong(pair.fst.getImporteLDCL()) ? String.format("Importe incorrecto se esperaba %.2f y es %.2f.", pair.snd.getImporte()/100.00, (long) Long.parseLong(pair.fst.getImporteLDCL())/100.00  ) : ""
								
								))
						.collect(Collectors.joining(",", "[", "]"))
			// @formatter:on
			);
			os.println(");");
		
	}
	
	private static boolean checkData(Attach attach, String login){
		if ( attach.getDriveId() != null  ){
			Domain domain = attach.getDomain() ;
		
			byte data [] =	
			DriveUtils.getByteFile(
					domain.getName(), 
					domain.getId(), 
					login, 
					attach.getDriveId(), 
					attach.getId());
			attach.setData(data);
			DriveUtils.deleteFile(domain.getName(), domain.getId(), login, attach.getDriveId());
			attach.setDriveId(null);

			AON.update(domain.getName(), domain.getId(), login, attach);
		}
		return attach.getData() != null && attach.getData().length > 0;
		
	}
	
	
}

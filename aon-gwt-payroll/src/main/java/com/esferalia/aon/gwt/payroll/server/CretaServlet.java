package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.CRETA_RESPUESTA;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.CRETA_TRABAJADORES_Y_TRAMOS;
import static java.lang.String.format;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
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
import com.esferalia.aon.gwt.payroll.shared.CretaService.File;
import com.esferalia.aon.gwt.payroll.shared.Province;
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
import com.esferalia.aon.payroll.tgss.creta.Bases;
import com.esferalia.aon.payroll.tgss.creta.Bases.BasesCallback;
import com.esferalia.aon.payroll.tgss.creta.Bases.CustomizeBasesCallback;
import com.esferalia.aon.payroll.tgss.creta.Bases.EmptyBasesException;
import com.esferalia.aon.payroll.tgss.creta.Borrador;
import com.esferalia.aon.payroll.tgss.creta.Calculo;
import com.esferalia.aon.payroll.tgss.creta.Confirmacion;
import com.esferalia.aon.payroll.tgss.creta.DBA;
import com.esferalia.aon.payroll.tgss.creta.DCL;
import com.esferalia.aon.payroll.tgss.creta.DCL.LineaSalary;
import com.esferalia.aon.payroll.tgss.creta.IndentXMLStreamWriter;
import com.esferalia.aon.payroll.tgss.creta.SolicitudTrabajadoresTramos;
import com.esferalia.aon.payroll.tgss.creta.TrabajadoresTramos;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.tgss.creta.jaxb.Dato;
import net.aonsolutions.core.tgss.creta.jaxb.DatoSolicitado;
import net.aonsolutions.core.tgss.creta.jaxb.Fecha;
import net.aonsolutions.core.tgss.creta.jaxb.Liquidacion;
import net.aonsolutions.core.tgss.creta.jaxb.Periodo;
import net.aonsolutions.core.tgss.creta.jaxb.Trabajador;
import net.aonsolutions.core.tgss.creta.jaxb.Tramo;
import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.bases.LiquidacionBuilder;
import net.aonsolutions.core.tgss.creta.jaxb.bases.TramoBuilder;
import net.aonsolutions.core.tgss.creta.jaxb.dcl.LineaDCL;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta;

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
	

	protected Connection getConnection(HttpServletRequest req) throws SQLException {
		String domain = req.getServerName();
		return AonServletUtils.getConnection(domain);
	}

	// ------------------------------------------------------------------------
	// CretaService.Solicitud.Visitor<HttpServletRequest, HttpServletResponse,
	// Exception>

	@Override
	public void visitBases(HttpServletRequest req, HttpServletResponse resp) throws Exception {

		Connection connection = getConnection(req);
		resp.setContentType("text/html;");

		boolean indicadorReftificacion = AonStringUtils.equalsIgnoreCase("on",
				req.getParameter(CretaService.Parameter.INDICADOR_RECTIFICACION.name()));

		String nafs[] = req.getParameterValues(CretaService.Parameter.NAFS.name());

		List<String> defaultsList = new ArrayList<String>();
		defaultsList.addAll(Arrays.asList("51=M", "737=0", "54=1"));

		String paramDefaults[] = req.getParameterValues(CretaService.Parameter.DEFAULTS.name());
		if (paramDefaults != null && paramDefaults.length > 0)
			defaultsList.addAll(Arrays.asList(paramDefaults));

		String defaults[] = defaultsList.toArray(new String[defaultsList.size()]);
		
		CustomizeBasesCallback customBasesCb = new CustomizeBasesCallback()
				.setReftificationMark(indicadorReftificacion)
				;

		InfoPickerBasesCallback pickerBasesCb = new InfoPickerBasesCallback();

		PrintWriter os = resp.getWriter();

		os.println("{");

		List<InputStream> respuestasIss = new ArrayList<InputStream>();
		List<InputStream> trabajadoresYTramosIss = new ArrayList<InputStream>();
		
		try {
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
		}
		catch ( ServletException e ) { 
			//if this request is not of type multipart/form-data
			trabajadoresYTramosIss.add(generateTrabajadoresYTramos(connection, req));
		}
		
		try {
			os.printf("\"full_bases\":\"%s\",\r\n", generateBases(connection, true, false, false, nafs, defaults,
					trabajadoresYTramosIss, respuestasIss, customBasesCb, pickerBasesCb));
		} catch (EmptyBasesException e) {
			os.printf("\"full_bases\":\"\",\r\n");
		}

		respuestasIss.clear();
		trabajadoresYTramosIss.clear();
		try {
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
		}
		catch ( ServletException e ) { 
			//if this request is not of type multipart/form-data
			trabajadoresYTramosIss.add(generateTrabajadoresYTramos(connection, req));
		}

		NoSkippedCallback skippedCallback = new NoSkippedCallback();
		NoDiffsBasesCallback noDiffsBasesCb = new NoDiffsBasesCallback() {
			@Override
			public void noDiffs(net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion liquidacion) {
				super.noDiffs(liquidacion);
				pickerBasesCb.noDiffs(liquidacion);
			}
		};
		
		try {
			os.printf("\"diff_bases\":\"%s\",\r\n", generateBases(connection, true, true, true, nafs, defaults,
					trabajadoresYTramosIss, respuestasIss, customBasesCb, noDiffsBasesCb, skippedCallback));
		} catch (EmptyBasesException e) {
			os.printf("\"draft_request\":\"%s\",\r\n",
					generateBorrador(e.getAutorizado(), noDiffsBasesCb.getMeses(), noDiffsBasesCb.getAnhos(),
							noDiffsBasesCb.getTipos(), noDiffsBasesCb.getAceptarBasesAnteriores(),
							noDiffsBasesCb.getCCCs()));
		} catch (NoneSkippedException e) {
//			os.printf("\"diff_bases\":null,\r\n");
		}

		os.printf("\"errors\":%s,\r\n", toJSON(pickerBasesCb.errors));

		os.printf("\"unknown\":%s,\r\n", toJSON(pickerBasesCb.unknown));

		os.printf("\"warnings\":%s,\r\n", toJSON(pickerBasesCb.warnings));

		os.printf("\"messages\":[],\r\n");

		os.printf("\"rectifying\":%b\r\n", indicadorReftificacion );

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
		boolean claculosDesglosados = AonStringUtils.equalsIgnoreCase("on",
				req.getParameter(CretaService.Parameter.CALCULOS_DESGLOSADOS.name()));
		Calculo.generate(autorizado,desdeMes, desdeAnho, hastaMes, hastaAnho, tipo, claculosDesglosados, cccs, resp.getOutputStream());
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
		SolicitudTrabajadoresTramos.generate(autorizado, desdeMes, desdeAnho, hastaMes, hastaAnho, ctrlMes, ctrlAnho, tipo, cccs, resp.getOutputStream());
	}

	@Override
	public void visitTrabajadoresTramos(HttpServletRequest req, HttpServletResponse resp) throws Exception {

		PrintWriter os = resp.getWriter();

		os.println("<html>");
		os.println("<body>");
		os.println("<script>");
		
		Date fromDate = AonDateUtils.getLastDayOfMonth(AonDateUtils.add(new Date(), Calendar.MONTH, -3));
				
		//@formatter:off
		__onTrabajadoresYTramos(os, 
				Stream.concat(
					req.getParts().stream()
					.map(part -> unmarshall(net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos.class, part))
					.filter(optional -> optional.isPresent())
					.map(optional -> optional.get())
					.peek(t -> {saveTrabajadoresYTramos(req, t);})
					.peek(t -> os.printf("//MESSAGE %s %s %s (%s) Guardado\r\n", File.TRABAJADORES_TRAMOS.getFilename(), t.getLiquidacion().getTipo(), Province.getName(t.getLiquidacion().getCcc().getProvincia()), t.getLiquidacion().getCcc().getNumero()))
					.peek(t -> os.flush())
					,
					findTrabajadoresYTramos(req)
				)
				.filter(t -> t.getLiquidacion() != null)
				.filter(t -> toDate(t.getLiquidacion().getFechaHoraRecaudacion()).after(fromDate)),
				Stream.concat(
					req.getParts().stream()
					.map(part -> unmarshall(net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta.class, part))
					.filter(optional -> optional.isPresent())
					.map(optional -> optional.get())
					.peek(r -> {saveRespuesta(req, r);})
					.peek(r -> os.printf("//MESSAGE %s %s Guardada\r\n", File.RESPUESTA.getFilename(), r.getReferenciaExterna()))
					.peek(r -> os.flush())
					,
					findRespuestas(req)
				)
				.filter(r -> r.getLiquidacion() != null && r.getLiquidacion().size() > 0 )
				.filter(r -> toDate(r.getLiquidacion().get(0).getFechaHoraRecaudacion()).after(fromDate))
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
					getConnection(req), 
					os, 
					req.getParts().stream()
					.map(part -> unmarshall(net.aonsolutions.core.tgss.creta.jaxb.dcl.DCL.class, part))
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

	private static InputStream generateTrabajadoresYTramos(Connection connection, HttpServletRequest req ) throws JAXBException, IOException {
		
		String tipo = req.getParameter(CretaService.Parameter.TIPO.name());
		String cccs[] = req.getParameterValues(CretaService.Parameter.CCC.name());
		String desdeMes = req.getParameter(CretaService.Parameter.DESDE_MES.name());
		String desdeAnho = req.getParameter(CretaService.Parameter.DESDE_ANHO.name());
		String hastaMes = req.getParameter(CretaService.Parameter.HASTA_MES.name());
		String hastaAnho = req.getParameter(CretaService.Parameter.HASTA_ANHO.name());
		String ctrlMes = req.getParameter(CretaService.Parameter.CTRL_MES.name());
		String ctrlAnho = req.getParameter(CretaService.Parameter.CTRL_ANHO.name());
		String autorizado = req.getParameter(CretaService.Parameter.AUTORIZADO.name());
		
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		TrabajadoresTramos.generate(
				connection, 
				autorizado, 
				desdeMes, 
				desdeAnho, 
				hastaMes, 
				hastaAnho, 
				ctrlMes, 
				ctrlAnho, 
				tipo, 
				cccs, 
				os);
		os.close();
		
		return new StringBufferInputStream(String.format("%s", os.toString(), "UTF-8"));
		//return new ByteArrayInputStream(os.toByteArray());
	
	}
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
		} catch (Throwable t) {
			return Optional.empty();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
	}

	private static int compare(net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos t1,
			net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos t2) {
		return toString(t2.getLiquidacion().getPeriodoDesde())
				.compareTo(toString(t1.getLiquidacion().getPeriodoDesde()));
	}

	private static String toString(net.aonsolutions.core.tgss.creta.jaxb.Fecha f) {
		return String.format("%s-%02d-%02d", f.getAnho(), Integer.parseInt(f.getMes()), Integer.parseInt(f.getDia()));
	}

	private static String toString(net.aonsolutions.core.tgss.creta.jaxb.Periodo p) {
		return String.format("%s-%02d", p.getAnho(), Integer.parseInt(p.getMes()));
	}

	private static String toJSON(net.aonsolutions.core.tgss.creta.jaxb.Liquidacion<?, ?, ?, ?, ?> l) {

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

	private static String toJSON(net.aonsolutions.core.tgss.creta.jaxb.respuesta.Errores errs) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(errs.getError().stream().map(err -> String.format("{\"code\":\"%s\", \"msg\":\"%s\"}",
				err.getCodigoErr(), safeEncode(err.getDescripcion()))).collect(Collectors.joining(",")));

//		errs.getError().stream().forEach(err -> System.out.println(err.getDescripcion()));

		return buffer.toString();
	}

	private static String toJSON(net.aonsolutions.core.tgss.creta.jaxb.respuesta.Trabajadores trabajadores) {
		if ( trabajadores == null )
			return "";

		return toJSON(trabajadores.getTrabajador());
	}

	private static String toJSON(Collection<net.aonsolutions.core.tgss.creta.jaxb.respuesta.Trabajador> trabajadores) {
		if ( trabajadores == null )
			return "";
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(trabajadores.stream()
				.map(trabajador -> String.format("{\"naf\":\"%s\"}", trabajador.getNaf()))
				.collect(Collectors.joining(",")));

		return buffer.toString();
	}

	private static String toJSON(Stream<net.aonsolutions.core.tgss.creta.jaxb.respuesta.LiquidacionMes> liquidacionesMes) {
		if ( liquidacionesMes == null )
			return "";

		StringBuffer buffer = new StringBuffer();
		buffer.append(
				liquidacionesMes.map(liquidacionMes -> String.format("%s", toJSON(liquidacionMes.getTrabajadores())))
						.collect(Collectors.joining(",")));

		return buffer.toString();
	}

	private static String toJSON(net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Trabajadores trabajadores) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(trabajadores.getTrabajador().stream()
				.map(trabajador -> String.format("{\"naf\":\"%s\",\"ipf\":\"%s\",\"caf\":\"%s\"}", trabajador.getNaf(), trabajador.getIpf().getNumeroIpf() ,trabajador.getCaf()))
				.collect(Collectors.joining(",")));

		return buffer.toString();
	}

	private static String toJS0N(Stream<net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.LiquidacionMes> liquidacionesMes) {
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

		private Dato dato;
		private String value;
		private Salary salary;
		private Trabajador<?> trabajador;
		private DatoSolicitado datoSolicitado;
		private Liquidacion<?, ?, ?, ?, ?> liquidacion;
			
		public UnknownDato setDato(Dato dato) {
			this.dato = dato;
			return this;
		}
		
		public UnknownDato setValue(String value) {
			this.value = value;
			return this;
		}
		
		public UnknownDato setSalary(Salary salary) {
			this.salary = salary;
			return this;
		}
		
		public UnknownDato setDatoSolicitado(DatoSolicitado dato) {
			this.datoSolicitado = dato;
			return this;
		}

		public UnknownDato setLiquidacion(Liquidacion<?, ?, ?, ?, ?> liquidacion) {
			this.liquidacion = liquidacion;
			return this;
		}
		
		public UnknownDato setTrabajador(Trabajador<?> trabajador) {
			this.trabajador = trabajador;
			return this;
		}

		// --------------------------------------------------------------- JSON

		@Override
		public String toJSON() {
			return String.format(
					"{" 
						+ "\"message\":\"%s\",\r\n" 
						+ "\"type\":\"%s\",\r\n" 
						+ "\"code\":\"%s\",\r\n"
						+ "\"mandatory\":%s" 
						+ "%s"
						+ "%s"
						+ "%s"
						+ "\r\n"
						+ "}",
					getMessage(), 
					datoSolicitado!= null ? datoSolicitado.getTipoDato() : dato.getTipoDato(), 
					datoSolicitado!= null ?datoSolicitado.getCodigo() : dato.getCodigo(),
					datoSolicitado!= null ? "B".equals(datoSolicitado.getIndicadorObligatoriedad()): false,
					
					(trabajador != null) ? String.format(",\"naf\":\"%s\"\r\n", trabajador.getNaf()): "",
					(trabajador == null && salary != null) ? String.format(",\"naf\":\"%s\"\r\n", salary.getEmployeeSSNumber()): "",
					
					(value != null) ? String.format(",\"value\":\"%s\"\r\n", value): ""

					);
		}

	}


	private static class NoDiffs extends Event<NoDiffs> {
		private net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion liquidacion;

		public NoDiffs setLiquidacion(net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion liquidacion) {
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

		List<net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion> liquidaciones = new ArrayList<net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion>();

		@Override
		public void noDiffs(net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion liquidacion) {
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

	private static class InfoPickerBasesCallback implements BasesCallback {

		private List<Event> errors = new ArrayList<Event>();
		private List<Event> warnings = new ArrayList<Event>();

		private List<UnknownDato> unknown = new ArrayList<UnknownDato>();

		// ------------------------------------------------------------- Errors

		@Override
		public void unknownDato(Liquidacion<?, ?, ?, ?, ?> liquidacion, DatoSolicitado datoSolicitado,
				LiquidacionBuilder liquidacionBuilder) {
			boolean mandatory = "B".equalsIgnoreCase(datoSolicitado.getIndicadorObligatoriedad());
			String message = String.format(

					"Lo sentimos. %s (%s) no encontrado en AON SOLUTIONS ( Liquidaci\u00F3n %s%s%s).",

					getDescription(datoSolicitado),

					mandatory ? "Obligatorio" : "Opcional",

					liquidacion.getCcc().getProvincia(), liquidacion.getCcc().getRegimen(),
					liquidacion.getCcc().getNumero()

			);

			UnknownDato event = new UnknownDato()
					.setMessage(message)
					.setDatoSolicitado(datoSolicitado)
					.setLiquidacion(liquidacion);

			unknown.add(event);
		}

		@Override
		public void unknownDato(Salary salary, Trabajador<?> trabajador,  Tramo tramo, DatoSolicitado datoSolicitado, TramoBuilder tramoBuilder) {

			boolean mandatory = "B".equalsIgnoreCase(datoSolicitado.getIndicadorObligatoriedad());
			String message = String.format("Lo sentimos. %s (%s) no encontrado en AON SOLUTIONS. %s %s (%s)",
					getDescription(datoSolicitado), 
					mandatory ? "Obligatorio" : "Opcional",
					salary.getEmployeeName(),
					salary.getEmployeeSSNumber(),
					salary.getEmployeeDocument());

			UnknownDato event = new UnknownDato()
					.setMessage(message)
					.setDatoSolicitado(datoSolicitado)
					.setTrabajador(trabajador);

			unknown.add(event);

		}

		@Override
		public void defaultDato(Salary salary, Tramo tramo, Dato dato, TramoBuilder tramoBuilder, String value) {
			String message ;
			if ( AonStringUtils.isNotBlank(value))
				message = String.format("%s no encontrado se le ha asignado el valor '%s'. %s %s (%s)",
						getDescription(dato), 
						value,
						salary.getEmployeeName(),
						salary.getEmployeeSSNumber(),
						salary.getEmployeeDocument());
			else 
				message = String.format(
						"%s no encontrado, ha sido eliminado. %s %s (%s)" ,
						getDescription(dato), 
						salary.getEmployeeName(),
						salary.getEmployeeSSNumber(),
						salary.getEmployeeDocument());

			UnknownDato event = new UnknownDato()
					.setValue(value)
					.setMessage(message)
					.setDato(dato)
					.setSalary(salary);

			unknown.add(event);

		}

		@Override
		public void defaultDato(Salary salary, Trabajador<?> trabajador,  Tramo tramo, DatoSolicitado datoSolicitado, TramoBuilder tramoBuilder, String value) {

			boolean mandatory = "B".equalsIgnoreCase(datoSolicitado.getIndicadorObligatoriedad());
			String message ;
			if ( AonStringUtils.isNotBlank(value))
				message = String.format(
						"%s (%s) no encontrado, se le ha asignado el valor '%s'. %s %s (%s)" ,
						getDescription(datoSolicitado), 
						mandatory ? "Obligatorio" : "Opcional",
						value,
						salary.getEmployeeName(),
						salary.getEmployeeSSNumber(),
						salary.getEmployeeDocument());
			else 
				message = String.format(
						"%s (%s) no encontrado, ha sido eliminado. %s %s (%s)" ,
						getDescription(datoSolicitado), 
						mandatory ? "Obligatorio" : "Opcional",
						salary.getEmployeeName(),
						salary.getEmployeeSSNumber(),
						salary.getEmployeeDocument());

			UnknownDato event = new UnknownDato()
					.setValue(value)
					.setMessage(message)
					.setDatoSolicitado(datoSolicitado)
					.setTrabajador(trabajador);

			unknown.add(event);

		}

		@Override
		public void salaryNotFound(String ccc, Trabajador<?> trabajador, Periodo mes) {

			errors.add(new Event().setMessage(
					format("No se ha encontrado n\u00F3mina del %s del %s para el trabajador (NAF:%s, CCC:%s) ",
							mes.getMes(), mes.getAnho(), trabajador.getNaf(), ccc)));
		}

		@Override
		public void wrongVariable(Salary salary, String var, Period p, String right, String wrong) {
			if (right == null)
				warnings.add(new Event().setMessage(
						format("%s (IPF:%s, NAF:%s) .%s (%5$td/%5$tm/%5$tY..%6$td/%6$tm/%6$tY) incorrecta. No se esperaba y es '%7$s'",
								salary.getEmployeeName(), salary.getEmployeeDocument(), salary.getEmployeeSSNumber(),
								var, p.getStart(), p.getEnd(), wrong)));
			else
				warnings.add(new Event().setMessage(
						format("%s (IPF:%s, NAF:%s) .%s (%5$td/%5$tm/%5$tY..%6$td/%6$tm/%6$tY) incorrecta. Se esperaba '%7$s' y es '%8$s'",
								salary.getEmployeeName(), salary.getEmployeeDocument(), salary.getEmployeeSSNumber(),
								var, p.getStart(), p.getEnd(), right, wrong)));
		}

		@Override
		public void noSuchVariable(Salary salary, String var, Period p, String right) {
			warnings.add(new Event().setMessage(format(
					"%s (IPF:%s, NAF:%s) .%s (%5$td/%5$tm/%5$tY..%6$td/%6$tm/%6$tY) no encontrada. Se esperaba '%7$s'",
					// salary.getEnterpriseName(),
					salary.getEmployeeName(), salary.getEmployeeDocument(), salary.getEmployeeSSNumber(),
					// salary.getEnterpriseCCC(),
					var, p.getStart(), p.getEnd(), right)));
		}

		@Override
		public void ambigousVariable(Salary salary, String var, Period p, String right,
				String... wrongs) {
			warnings.add(new Event().setMessage(format(
					"%s (IPF:%s, NAF:%s) .%s (%5$td/%5$tm/%5$tY..%6$td/%6$tm/%6$tY) ambigua. Se esperaba '%7$s' y es %8$s",
					// salary.getEnterpriseName(),
					salary.getEmployeeName(), salary.getEmployeeDocument(), salary.getEmployeeSSNumber(),
					// salary.getEnterpriseCCC(),
					var, p.getStart(), p.getEnd(), right, Arrays.stream(wrongs)
							.map(wrong -> String.format("'%s'", wrong)).collect(Collectors.joining(",")))));
		}

		@Override
		public void unMatchedVariable(Salary salary, String var, ContextData contextData,
				Dato datoSolicitado, Tramo tramo, TramoBuilder tramoBuilder, boolean optional) {
			errors.add(new Event().setMessage(format(
					"%s (IPF:%s, NAF:%s) .Tramo para %s (%5$td/%5$tm/%5$tY..%6$td/%6$tm/%6$tY) incorrecto se esperaba (%7$s/%8$s/%9$s...%10$s/%11$s/%12$s)",
					// salary.getEnterpriseName(),
					salary.getEmployeeName(), salary.getEmployeeDocument(), salary.getEmployeeSSNumber(),
					// salary.getEnterpriseCCC(),
					var,

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
		public void noDiffs(net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion liquidacion) {
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

	public static class NoneSkippedException extends RuntimeException {
		
	}

	public static class NoSkippedCallback implements BasesCallback {
		
		private boolean skipped = false;
		
		@Override
		public void bases(net.aonsolutions.core.tgss.creta.jaxb.bases.Bases bases) {
			if ( skipped )
				return;
			throw new NoneSkippedException();
		}
		
		@Override
		public void trabajadorSkipped(net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
				Trabajador trabajadorCreta, Salary salary) {
			skipped = true;
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

	
	private static Stream<net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta> findRespuestas(HttpServletRequest req) throws SQLException{
		String login = ":-)" ; 
		String domainName = req.getServerName();
		Integer domainId = AonServletUtils.getDomainID(domainName);
		Date firstDayOfMonth = AonDateUtils.getFirstDayOfMonth(new Date());
		Date from = AonDateUtils.add(firstDayOfMonth, Calendar.MONTH, -30);
		
		return
		findAttachs(domainName, domainId, login, RegistryAttachmentType.CRETA_RESPUESTA, from)
		.map(attach-> unmarshall(net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta.class, attach.getData()))
		.filter(optional -> optional.isPresent())
		.map(optional -> optional.get())
		;
	}

	private static Stream<net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos> findTrabajadoresYTramos(HttpServletRequest req) throws SQLException{
		String login = ":-)" ; 
		String domainName = req.getServerName();
		Integer domainId = AonServletUtils.getDomainID(domainName);
		Date firstDayOfMonth = AonDateUtils.getFirstDayOfMonth(new Date());
		Date from = AonDateUtils.add(firstDayOfMonth, Calendar.MONTH, -30);
		
		return
		findAttachs(domainName, domainId, login, RegistryAttachmentType.CRETA_TRABAJADORES_Y_TRAMOS, from)
		.map(attach-> unmarshall(net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos.class, attach.getData()))
		.filter(optional -> optional.isPresent())
		.map(optional -> optional.get())
		;
	}
	
	private static void saveRespuesta(HttpServletRequest req,
			net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta t) {
		try {
			saveAttach(req, CRETA_RESPUESTA, t);
		} catch (SQLException e) {
			//TODO: Log
		}
	}

	private static void saveTrabajadoresYTramos(HttpServletRequest req,
			net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos t) {
		
		try {
			saveAttach(req, CRETA_TRABAJADORES_Y_TRAMOS, t);
		} catch (SQLException e) {
			// TODO: Log
		}
	}
	
	
	private static <T> void saveAttach(HttpServletRequest req, RegistryAttachmentType type, T t)   throws SQLException {

		Date now = Calendar.getInstance().getTime();

		byte data [] = marshall(t);
		String md5 = AonFileUtils.getMD5Checksum(data);

		String login = ":-)" ; 
		String domainName = req.getServerName();
		Integer domainId = AonServletUtils.getDomainID(domainName);

		Attach attach = getAttach(domainName, domainId, login, type, md5);

		if (attach != null && attach.getId() != null ) 
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
		attach.setType((byte) type.ordinal());
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
	
	private static Stream<Attach> findAttachs(String domainName, Integer domainId, String login, RegistryAttachmentType type, Date from ) {
		return  AON.getAttachList(
				domainName, 
				domainId, 
				login,
				p -> 
				p.getDomainProperty().eq(domainId)
				.and(p.getTypeProperty().eq((byte)type.ordinal()))
				.and(p.getAttachDateProperty().ge(new java.sql.Date(from.getTime())))
				,
				AttachType.REGISTRY
				)
				.stream()
				.filter(a -> checkData(a, login));
		
	}
	
	private static void __onTrabajadoresYTramos (PrintWriter os, 
			Stream<net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos> ts,
			Stream<net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta> rs) {
		
			os.println("parent.__onTrabajadoresYTramos(");
			os.println("[");
			os.println("//BEGIN_TRABAJADORES_TRAMOS");
			os.flush();
			
			// @formatter:off
			Iterator<net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos> tsIt = ts.sorted(CretaServlet::compare).iterator();
			String sep = "";
			while ( tsIt.hasNext() ){
				net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos t = tsIt.next();
				os.printf("%s\r\n{\"name\":\"%s\",%s,\"employees\":[%s],\"file\":\"%s\"}\r\n", sep ,CretaService.File.TRABAJADORES_TRAMOS, toJSON(t.getLiquidacion()),toJS0N(t.getLiquidacion().getLiquidacionMes().stream()), marshallAndEncode(t));
				os.flush();
				sep = ",";
			}
			// @formatter:on

			os.println("//END_TRABAJADORES_TRAMOS");
			os.flush();
			os.println("]");
			
			os.println(",");

			os.println("[");
			os.println("//BEGIN_RESPUESTAS");
			os.flush();
			// @formatter:off
			Iterator<Respuesta> rsIt = rs.iterator();
			sep = "";
			while ( rsIt.hasNext() ){
				Respuesta r = rsIt.next();
				List<net.aonsolutions.core.tgss.creta.jaxb.respuesta.Liquidacion> liquidacion = r.getLiquidacion();
				for  (net.aonsolutions.core.tgss.creta.jaxb.respuesta.Liquidacion l : liquidacion ){
					os.printf("%s\r\n{\"name\":\"%s\",%s,\"errors\":[%s],\"employees\":[%s],\"file\":\"%s\"}\r\n",sep, CretaService.File.RESPUESTA, toJSON(l), toJSON(l.getErrores()),toJSON(l.getLiquidacionMes().stream()), marshallAndEncode(r));
					os.flush();
					sep = ",";
					
				}
			}
			// @formatter:on

			os.println("//END_RESPUESTAS");
			os.flush();
			os.println("]");

			os.println(");");
		
	}
	
	private static void __onDocumentoCalculoLiquidacion(Connection connection, PrintWriter os, 
			Stream<net.aonsolutions.core.tgss.creta.jaxb.dcl.DCL> dlcs) {
		
			
			
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
	
	private static <F extends net.aonsolutions.core.tgss.creta.jaxb.Fecha> Date toDate(net.aonsolutions.core.tgss.creta.jaxb.FechaHoraRecaudacion<F> fechaHoraRecaudacion){
		
		
		Calendar calendar = Calendar.getInstance();
		
		//Hora hora = fechaHoraRecaudacion.getHoraRecaudacion();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);

		Fecha fecha = fechaHoraRecaudacion.getFechaRecaudacion();
		calendar.set(Calendar.DATE, Integer.parseInt(fecha.getDia()));
		calendar.set(Calendar.MONTH, Integer.parseInt(fecha.getMes())-1);
		calendar.set(Calendar.YEAR, Integer.parseInt(fecha.getAnho()));

		return calendar.getTime();
		
	}
	
	private static String getParameter (HttpServletRequest req, CretaService.Parameter param, String def) {
		String value = req.getParameter(param.name());
		return value != null ? value : def;
	}
	
	
}

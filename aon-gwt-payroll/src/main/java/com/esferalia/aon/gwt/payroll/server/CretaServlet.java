package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.gwt.payroll.shared.Province.getProvinces;
import static com.esferalia.aon.jooq.Keys.FK_CONTRACT_ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.Keys.FK_SALARY_CONTRACT;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
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
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
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

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.code.aon.google.apis.DriveUtils;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.shared.CretaService;
import com.esferalia.aon.gwt.payroll.shared.CretaService.File;
import com.esferalia.aon.gwt.payroll.shared.CretaService.Parameter;
import com.esferalia.aon.gwt.payroll.shared.Province;
import com.esferalia.aon.in.payroll.tgss.idc.Idcplnss;
import com.esferalia.aon.in.payroll.tgss.idc.TrabajadoresTramosCallback;
import com.esferalia.aon.jooq.Keys;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.payroll.Pair;
import com.esferalia.aon.payroll.tgss.creta.Bases;
import com.esferalia.aon.payroll.tgss.creta.Bases.BasesCallback;
import com.esferalia.aon.payroll.tgss.creta.Bases.ConstantDatoBasesCallback;
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
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.CtaCot;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Error;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Errores;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.FechaHoraRecaudacion;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.TrabajadorBuilder.TipoIpf;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exception.SegSocialException;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(
		name = "SLD-Solicitud", 
		urlPatterns = { 
				"/aon_gwt_aio/sdl/*" ,
				"/aon_gwt_payroll/sdl/*" 
		}
)
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
		Connection connection = null;
		try {
			connection = getConnection(req);
			
			resp.setContentType("text/html;");
	
			boolean indicadorReftificacion = AonStringUtils.equalsIgnoreCase("on",
					req.getParameter(CretaService.Parameter.INDICADOR_RECTIFICACION.name()));
			boolean solicitudRecepcionRNT = AonStringUtils.equalsIgnoreCase("on",
					req.getParameter(CretaService.Parameter.SOLICITUD_RECEPCION_RNT.name()));
			boolean aceptarBasesAnteriores = AonStringUtils.equalsIgnoreCase("on",
					req.getParameter(CretaService.Parameter.ACEPTAR_BASES_ANTERIORES.name()));
	
			String nafs[] = req.getParameterValues(CretaService.Parameter.NAFS.name());
	
			List<String> defaultsList = new ArrayList<String>();
			//defaultsList.addAll(Arrays.asList("51=M", "737=0", "54=1"));
			defaultsList.addAll(Arrays.asList("51=M", "54=1" ));
	
			String paramDefaults[] = req.getParameterValues(CretaService.Parameter.DEFAULTS.name());
			if (paramDefaults != null && paramDefaults.length > 0)
				defaultsList.addAll(Arrays.asList(paramDefaults));
	
			String defaults[] = defaultsList.toArray(new String[defaultsList.size()]);
			
			CustomizeBasesCallback customBasesCb = new CustomizeBasesCallback()
					.setReftificationMark(indicadorReftificacion)
					.setSolicitudRecepcionRNT(solicitudRecepcionRNT)
					;
			
			String i54 = req.getParameter(CretaService.Parameter.I54.name() );
			ConstantDatoBasesCallback i54Callback = new ConstantDatoBasesCallback("54", "I", i54);
	
			InfoPickerBasesCallback pickerBasesCb = new InfoPickerBasesCallback();
	
			PrintWriter os = resp.getWriter();
	
			os.println("{");
			
			os.printf("\"__progress\":[");
			os.printf("\r\n{\"percent\": 0.00, \"msg\":\"...\"}", CretaService.Message.BEGIN);
			os.flush();
			
			ProgressCallback progressCb = new ProgressCallback() {
				@Override
				public void progress(String message) {
					os.printf(",\r\n{\"percent\": 0.00, \"msg\":\"%s\"}", message);
					os.flush();
				}
			};
			
			List<InputStream> respuestasIss = new ArrayList<InputStream>();
			List<InputStream> trabajadoresYTramosIss = new ArrayList<InputStream>();
			
			try {
				for (Part part : req.getParts()) {
					try {
						CretaService.File file = CretaService.File.valueOf(part.getName());
						if (file == CretaService.File.TRABAJADORES_TRAMOS) {
							trabajadoresYTramosIss.add(part.getInputStream());
						}else if (file == CretaService.File.RESPUESTA) {
							try {
								// Try with IDC first
								trabajadoresYTramosIss.addAll(generateIDCTrabajadoresYTramos(req, part));
							} catch ( Throwable t ) {
								trabajadoresYTramosIss.add(generateTrabajadoresYTramos(connection, part.getInputStream()));//respuestasIss.add(part.getInputStream());
							}
						}
					} catch (IllegalArgumentException e) {
		
					}
				}
			}
			catch ( ServletException e ) { 
				//if this request is not of type multipart/form-data
				try {
					// Try with IDC first
					trabajadoresYTramosIss.addAll(generateIDCTrabajadoresYTramos(req));
				} catch ( Throwable t ) {
					trabajadoresYTramosIss.add(generateTrabajadoresYTramos(connection, req));
				}
				
			}
			
			NoDiffsBasesCallback noDiffsBasesCb = new NoDiffsBasesCallback() {
				@Override
				public void noDiffs(net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion liquidacion) {
					super.noDiffs(liquidacion);
					pickerBasesCb.noDiffs(liquidacion);
				}
			};

			try {
				String bases = generateBases(connection, true, aceptarBasesAnteriores, aceptarBasesAnteriores, nafs, defaults,
						trabajadoresYTramosIss, respuestasIss, customBasesCb, noDiffsBasesCb, i54Callback, pickerBasesCb, progressCb );
				os.printf(",\r\n{\"percent\": 100.00, \"msg\":\":-)\"}", CretaService.Message.END);
				os.flush();
				os.printf("],\r\n");
				os.printf("\"full_bases\":\"%s\",\r\n", bases);
			} catch (EmptyBasesException e) {
				String borrador = generateBorrador(
						e.getAutorizado(), 
						getMesControl(), 
						getAnhoControl(),
						noDiffsBasesCb.getMeses(), 
						noDiffsBasesCb.getAnhos(),
						noDiffsBasesCb.getTipos(), 
						noDiffsBasesCb.getAceptarBasesAnteriores(),
						noDiffsBasesCb.getCCCs()
						);
				os.printf(",\r\n{\"percent\": 100.00, \"msg\":\":-)\"}", CretaService.Message.END);
				os.flush();
				os.printf("],\r\n");
				os.printf("\"full_bases\":\"\",\r\n");
				os.printf("\"draft_request\":\"%s\",\r\n", borrador);
			}
	
			respuestasIss.clear();
			trabajadoresYTramosIss.clear();
			
			
//			try {
//				for (Part part : req.getParts()) {
//					try {
//						CretaService.File file = CretaService.File.valueOf(part.getName());
//						if (file == CretaService.File.TRABAJADORES_TRAMOS)
//							trabajadoresYTramosIss.add(part.getInputStream());
//						else if (file == CretaService.File.RESPUESTA)
//							trabajadoresYTramosIss.add(generateTrabajadoresYTramos(connection, part.getInputStream()));//respuestasIss.add(part.getInputStream());
//					} catch (IllegalArgumentException e) {
//						//e.printStackTrace();
//					}
//				}
//			}
//			catch ( ServletException e ) { 
//				//if this request is not of type multipart/form-data
//				trabajadoresYTramosIss.add(generateTrabajadoresYTramos(connection, req));
//			}
//	
//			NoSkippedCallback skippedCallback = new NoSkippedCallback();
//			NoDiffsBasesCallback noDiffsBasesCb = new NoDiffsBasesCallback() {
//				@Override
//				public void noDiffs(net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion liquidacion) {
//					super.noDiffs(liquidacion);
//					pickerBasesCb.noDiffs(liquidacion);
//				}
//			};
//			
//			
//			
//			if (trabajadoresYTramosIss.size() == 1 ) {
//				try {
//						os.printf("\"diff_bases\":\"%s\",\r\n", generateBases(connection, true, true, true, nafs, defaults,
//								trabajadoresYTramosIss, respuestasIss, customBasesCb, noDiffsBasesCb, skippedCallback, i54Callback));
//				} catch (EmptyBasesException e) {
//					os.printf("\"draft_request\":\"%s\",\r\n",
//							generateBorrador(e.getAutorizado(), 
//									getMesControl(), getAnhoControl(),
//									noDiffsBasesCb.getMeses(), noDiffsBasesCb.getAnhos(),
//									noDiffsBasesCb.getTipos(), noDiffsBasesCb.getAceptarBasesAnteriores(),
//									noDiffsBasesCb.getCCCs()));
//				} catch (NoneSkippedException e) {
//					//os.printf("\"diff_bases\":null,\r\n");
//				}
//			}
//	
//			try {
//				
//				if (trabajadoresYTramosIss.size() == 1 ) {
//					try {
//						// full_bases generated from  'SDL Fichero de Trabajadores y Tramos' try from salaries 
//						Map<Parameter,Object> parameterMap = new HashMap<CretaService.Parameter, Object>();
//						parameterMap.put(Parameter.TIPO, pickerBasesCb.getTipo());
//						parameterMap.put(Parameter.AUTORIZADO, pickerBasesCb.getAutorizado());
//						parameterMap.put(Parameter.DESDE_MES, pickerBasesCb.getDesdeMes());
//						parameterMap.put(Parameter.DESDE_ANHO, pickerBasesCb.getDesdeAnho());
//						parameterMap.put(Parameter.HASTA_MES, pickerBasesCb.getHastaMes());
//						parameterMap.put(Parameter.HASTA_ANHO, pickerBasesCb.getHastaAnho());
//						parameterMap.put(Parameter.CTRL_MES, pickerBasesCb.getDesdeMes());
//						parameterMap.put(Parameter.CTRL_ANHO, pickerBasesCb.getDesdeAnho());
//						parameterMap.put(Parameter.CCC, pickerBasesCb.getCCCs());
//						trabajadoresYTramosIss = Collections.singletonList(generateTrabajadoresYTramos(connection, parameterMap ));
//						try {
//							os.printf("\"salary_bases\":\"%s\",\r\n", generateBases(connection, true, false, false, nafs, defaults,
//									trabajadoresYTramosIss, respuestasIss, 
//									customBasesCb, 
//									i54Callback , 
//									new CheckNotEqualsBasesCallback(pickerBasesCb.getBases()  )));
//						} catch (EmptyBasesException e) {
//						}
//					} catch (IllegalArgumentException e) {
//					}
//				}
//			}
//			catch ( Exception e ) { 
//			}

			os.printf("\"errors\":%s,\r\n", toJSON(pickerBasesCb.errors));
	
			os.printf("\"unknown\":%s,\r\n", toJSON(pickerBasesCb.unknown));
	
			os.printf("\"warnings\":%s,\r\n", toJSON(pickerBasesCb.warnings));
	
			os.printf("\"messages\":[],\r\n");
	
			os.printf("\"rectifying\":%b,\r\n", indicadorReftificacion );
	
			os.printf("\"requestSendRNT\":%b,\r\n", solicitudRecepcionRNT );

			os.printf("\"acceptPrevBases\":%b\r\n", aceptarBasesAnteriores );

			os.println("}");
	
			os.flush();
			os.close();
		} finally {
			if ( connection != null )
				connection.close();
		}

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
		boolean solicitudRecepcionRNT = AonStringUtils.equalsIgnoreCase("on",
				req.getParameter(CretaService.Parameter.SOLICITUD_RECEPCION_RNT.name()));
		Borrador.generate(autorizado, desdeMes, desdeAnho, hastaMes, hastaAnho, tipo, aceptarBasesAnteriores, solicitudRecepcionRNT, cccs, resp.getOutputStream());
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
		String controlMes = req.getParameter(CretaService.Parameter.CTRL_MES.name());
		String controlAnho = req.getParameter(CretaService.Parameter.CTRL_ANHO.name());
		String autorizado = req.getParameter(CretaService.Parameter.AUTORIZADO.name());
		Confirmacion.generate(autorizado, desdeMes, desdeAnho, hastaMes, hastaAnho, controlMes, controlAnho, tipo, cccs, resp.getOutputStream());
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
				Stream.of(
					req.getParts().stream()
					.map(part -> unmarshall(net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta.class, part))
					.filter(optional -> optional.isPresent())
					.map(optional -> optional.get())
					.filter( r -> !AonStringUtils.equals(r.getReferenciaExterna(), CretaService.AON_REFERENCIA_EXTERNA))
					.peek(r -> {saveRespuesta(req, r);})
					.peek(r -> os.printf("//MESSAGE %s %s Guardada\r\n", File.RESPUESTA.getFilename(), r.getReferenciaExterna()))
					.peek(r -> os.flush())
					,
					findRespuestas(req), 
					findNotStarted(req)
				).flatMap(s -> s)
				.filter(r -> r.getLiquidacion() != null && r.getLiquidacion().size() > 0 )
				.filter(r -> toDate(r.getLiquidacion().get(0).getFechaHoraRecaudacion()).after(fromDate))
				,
				findBases(req)
				.filter(b -> b.getLiquidacion() != null && b.getLiquidacion().size() > 0 )
				//.filter(b -> toDate(b.getLiquidacion().get(0).getFechaControl()).after(fromDate))
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
		
		Connection connection = null;
		
		try {
			connection = getConnection(req);
			
			PrintWriter os = resp.getWriter();
	
			os.println("<html>");
			os.println("<body>");
			os.println("<script>");
			
			//@formatter:off
			__onDocumentoCalculoLiquidacion(
						connection, 
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
		} finally {
			if ( connection != null )
				connection.close();
		}
		
	}

	// ------------------------------------------------------------------------

	private static InputStream generateTrabajadoresYTramos(Connection connection, InputStream respuestaIs ) throws JAXBException, IOException {
		
		Respuesta respuesta = Utils.unmarshal(Respuesta.class, respuestaIs);
		
		String tipo = getTipo(respuesta);
		String cccs[] = getCCCs(respuesta);
		
		Periodo desde = getPeriodoDesde(respuesta);
		String desdeMes = desde.getMes();
		String desdeAnho = desde.getAnho();
		
		Periodo hasta = getPeriodoHasta(respuesta);
		String hastaMes = hasta.getMes();
		String hastaAnho = hasta.getAnho();
		
		//Periodo control = getFechaControl(respuesta);
		
		String ctrlMes = hasta.getMes();
		String ctrlAnho = hasta.getAnho();
		String autorizado = respuesta.getAutorizado();
		
		
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
		
	}


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
	
	private static InputStream generateTrabajadoresYTramos(Connection connection,Map<Parameter,Object> defs  ) throws JAXBException, IOException {
		
		String tipo = (String)defs.get(CretaService.Parameter.TIPO);
		String cccs[] = (String[])defs.get(CretaService.Parameter.CCC);
		String desdeMes = (String)defs.get(CretaService.Parameter.DESDE_MES);
		String desdeAnho = (String)defs.get(CretaService.Parameter.DESDE_ANHO);
		String hastaMes = (String)defs.get(CretaService.Parameter.HASTA_MES);
		String hastaAnho = (String)defs.get(CretaService.Parameter.HASTA_ANHO);
		String ctrlMes = (String)defs.get(CretaService.Parameter.CTRL_MES);
		String ctrlAnho = (String)defs.get(CretaService.Parameter.CTRL_ANHO);
		String autorizado = (String)defs.get(CretaService.Parameter.AUTORIZADO);
		
		
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

	private static List<InputStream> generateIDCTrabajadoresYTramos(HttpServletRequest req, Part part ) throws JAXBException, IOException, SegSocialException {
		
		try ( CloseableAONContext ctx = getAONAonContext(req) ) {

			Certificate certificate = getCertificate(ctx);
			
			Respuesta respuesta = Utils.unmarshal(Respuesta.class, part.getInputStream());
			
			List<InputStream> trabajaresYTramosIsList = new LinkedList<>();

			String nafs[] = req.getParameterValues(CretaService.Parameter.NAFS .name());
			
			String autorizado = respuesta.getAutorizado();
			
			for ( Liquidacion<?,?,?,?,?> l : respuesta.getLiquidacion() ) {
				Date date = toDate(l.getPeriodoDesde());
				String ccc = l.getCcc().getRegimen()+l.getCcc().getProvincia()+l.getCcc().getNumero();
				try {
					net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresYTramos = 
					getTrabajadoresTramos(ctx, certificate, date, ccc, nafs);
					
					trabajadoresYTramos.setAutorizado(autorizado);
					
					trabajaresYTramosIsList.add(toInputStream(trabajadoresYTramos));
				} catch (SegSocialException | JAXBException e) {
				}
				
			}
			if ( trabajaresYTramosIsList.isEmpty()){		
				throw new NullPointerException();
			}
			return trabajaresYTramosIsList;
		}
	
	}
	
	private static List<InputStream> generateIDCTrabajadoresYTramos(HttpServletRequest req ) throws JAXBException, IOException {
		
		try ( CloseableAONContext ctx = getAONAonContext(req) ) {

			Certificate certificate = getCertificate(ctx);
			
			String tipo = req.getParameter(CretaService.Parameter.TIPO.name());
			if ( !AonStringUtils.contains("L00", tipo)) {
				throw new IOException("Unsupported type '" + tipo + "' from IDC. Comming soon :-(");
			}
			
			String cccs[] = req.getParameterValues(CretaService.Parameter.CCC.name());
			String nafs[] = req.getParameterValues(CretaService.Parameter.NAFS .name());
			String desdeMes = req.getParameter(CretaService.Parameter.DESDE_MES.name());
			String desdeAnho = req.getParameter(CretaService.Parameter.DESDE_ANHO.name());
			String hastaMes = req.getParameter(CretaService.Parameter.HASTA_MES.name());
			String hastaAnho = req.getParameter(CretaService.Parameter.HASTA_ANHO.name());
			String ctrlMes = req.getParameter(CretaService.Parameter.CTRL_MES.name());
			String ctrlAnho = req.getParameter(CretaService.Parameter.CTRL_ANHO.name());
			String autorizado = req.getParameter(CretaService.Parameter.AUTORIZADO.name());
			
			Date desdeDate = toDate(desdeMes, desdeAnho); 
			
			List<InputStream> trabajaresYTramosIsList = new LinkedList<>();  
			
			for ( String ccc : cccs ) {
				
				try {
					trabajaresYTramosIsList.add(toInputStream(getTrabajadoresTramos(ctx, certificate, desdeDate, ccc, nafs)));
				} catch (SegSocialException | JAXBException e) {
				}
			}
			
			return trabajaresYTramosIsList;
		}
	
	}
	
	private static <T> InputStream toInputStream(T t) throws JAXBException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Utils.marshal(t, os);
		System.out.println(String.format("%s", os.toString(), "UTF-8"));
		InputStream is = new StringBufferInputStream(String.format("%s", os.toString(), "UTF-8"));
		return is;
	}
	
	private static Stream<byte[]> getIdcplnss(Certificate certificate, Date date, String ccc, String [] nafs) throws SegSocialException {

		String regime = ccc.substring(0,4);
		String number = ccc.substring(4);
		
		List<byte[]> list = new LinkedList<>();
		for ( String naf: nafs ) {
			byte [] idc = SistemaRED.getIDCNSS(
				certificate.getCertificate(), 
				certificate.getPassword(), 
				certificate.getType(), 
				regime, 
				number, 
				naf, 
				date);
			
//			try ( OutputStream os = new FileOutputStream(java.io.File.createTempFile("idcplnss", "pdf")) ) {
//				os.write(idc);
//			} catch ( IOException e ) {
//				
//			}
			
			list.add(idc);
		}
		
		return list.stream();
	}

	private static net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos getTrabajadoresTramos(AONContext ctx, byte[] idcplnss) {
		try {
			TrabajadoresTramosCallback cb = new TrabajadoresTramosCallback() {
				@Override
				public String getIpf(String naf) {
					// TODO Auto-generated method stub
					return TrabajadoresTramosCallback.super.getIpf(naf);
				}
				
				@Override
				public TipoIpf getTipoIpf(String naf) {
					// TODO Auto-generated method stub
					return TrabajadoresTramosCallback.super.getTipoIpf(naf);
				}
				
				@Override
				public boolean isPartTimeEmployee(String ssNum, String ccc, Date start, Date end) {
					// TODO Auto-generated method stub
					return TrabajadoresTramosCallback.super.isPartTimeEmployee(ssNum, ccc, start, end);
				}
				
				@Override
				public boolean isScholarEmployee(String ssNum, String ccc, Date start, Date end) {
					// TODO Auto-generated method stub
					return TrabajadoresTramosCallback.super.isScholarEmployee(ssNum, ccc, start, end);
				}
				
				@Override
				public boolean isTraining421Employee(String ssNum, String ccc, Date start, Date end) {
					// TODO Auto-generated method stub
					return TrabajadoresTramosCallback.super.isTraining421Employee(ssNum, ccc, start, end);
				}
			};
			return Idcplnss.getTrabajadoresTramos(idcplnss, cb);
		} catch (Exception e) {
		//catch (IOException | UnknownPDFException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	private static net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos getTrabajadoresTramos(AONContext ctx, Certificate certificate, Date date, String ccc, String [] nafs) throws SegSocialException{
		return getIdcplnss(certificate, date, ccc, nafs)
		.map(idcplnss -> getTrabajadoresTramos(ctx, idcplnss))
		.reduce((tyt1,tyt2) -> {
			List<net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Trabajador> trabajadores1 = tyt1.getLiquidacion().getLiquidacionMes().get(0).getTrabajadores().getTrabajador();
			List<net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Trabajador> trabajadores2 = tyt2.getLiquidacion().getLiquidacionMes().get(0).getTrabajadores().getTrabajador();
			trabajadores1.addAll(trabajadores2);
			return tyt1;
		})
		.orElseThrow(SegSocialException::new);
		
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

	private static String generateBorrador(String autorizado,
			Month mesControl, Integer anhoControl,
			String meses[], String anhos[], String tipos[],
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
		Borrador.generate(autorizado, mesControl, anhoControl, meses, anhos, tipos, aceptarBasesAnteriores, cccs, xsw);
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
		
		int offset = 0;
		int length = bytes.length;
		for ( ; offset < bytes.length ; offset++, length-- )
			if ( bytes[offset] == '<') 
				break;
		
		if ( length == 0 )
			return Optional.empty();
			
		InputStream is = new ByteArrayInputStream(bytes, offset, length);
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

	private static net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta
	create(net.aonsolutions.core.tgss.creta.jaxb.respuesta.Liquidacion l, 
		net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta r ) {

		net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta respuesta = 
		new net.aonsolutions.core.tgss.creta.jaxb.respuesta.ObjectFactory().createRespuesta();
		
		respuesta.setErrores(r.getErrores());
		respuesta.setAutorizado(r.getAutorizado());
		respuesta.setReferenciaExterna(r.getReferenciaExterna());
		respuesta.setAccionDatosBancarios(r.getAccionDatosBancarios());
		respuesta.setIndicadorRectificacion(r.getIndicadorRectificacion());

		respuesta.setLiquidacion(Collections.singletonList(l));
		
		return respuesta;
		
	}

	private static net.aonsolutions.core.tgss.creta.jaxb.bases.Bases
	create(net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion l, 
		net.aonsolutions.core.tgss.creta.jaxb.bases.Bases b ) {

		net.aonsolutions.core.tgss.creta.jaxb.bases.Bases bases = 
		new net.aonsolutions.core.tgss.creta.jaxb.bases.ObjectFactory().createBases();
		
		bases.setAutorizado(b.getAutorizado());
		bases.setReferenciaExterna(b.getReferenciaExterna());
		bases.setIndicadorRectificacion(b.getIndicadorRectificacion());

		bases.setLiquidacion(Collections.singletonList(l));
		
		return bases;
		
	}

	private static String toJSON(net.aonsolutions.core.tgss.creta.jaxb.Liquidacion<?, ?, ?, ?, ?> l) {

		StringBuilder builder = new StringBuilder();
		builder.append(String.format("\"ccc\":\"%s%s%s\",", l.getCcc().getRegimen(), l.getCcc().getProvincia(),
				l.getCcc().getNumero()));
		builder.append(String.format("\"type\":\"%s\",", l.getTipo()));
		builder.append(String.format("\"from\":\"%s\",", toString(l.getPeriodoDesde())));
		builder.append(String.format("\"to\":\"%s\"", toString(l.getPeriodoDesde())));
		try {
			builder.append(String.format(",\"date\":\"%s\"", toString(l.getFechaHoraRecaudacion().getFechaRecaudacion())));
			builder.append(String.format(",\"time\":\"%s\"", l.getFechaHoraRecaudacion().getHoraRecaudacion()));
		} catch ( UnsupportedOperationException e) {
			
		}
		return builder.toString();
	}

	private static String toJSON(net.aonsolutions.core.tgss.creta.jaxb.respuesta.Errores errs) {
		if ( errs == null )
			return "";
		
		StringBuilder builder = new StringBuilder();
		builder.append(errs.getError().stream().map(err -> String.format("{\"code\":\"%s\", \"msg\":\"%s\"}",
				err.getCodigoErr(), safeEncode(err.getDescripcion()))).collect(Collectors.joining(",")));

		return builder.toString();
	}

	private static String toJSON(net.aonsolutions.core.tgss.creta.jaxb.respuesta.Tramos tramos) {
		if ( tramos == null )
			return "";
		
		StringBuilder builder = new StringBuilder();
		builder.append(tramos.getTramo().stream()
				.map(tramo -> 
				String.format("{\"desde\":\"%s\", \"hasta\":\"%s\", \"datos\":[%s], \"errores\":[%s] }",toString(tramo.getFechaDesde()), toString(tramo.getFechaHasta()), toJSON(tramo.getDatosTramo()), toJSON(tramo.getErrores())))
				.collect(Collectors.joining(",")));

		return builder.toString();
	}

	private static String toJSON(net.aonsolutions.core.tgss.creta.jaxb.respuesta.DatosTramo datosTramo) {
		if ( datosTramo == null )
			return "";

		StringBuilder builder = new StringBuilder();
		
		builder.append(datosTramo.getDato().stream()
				.map(dato -> String.format("{\"codigo\":\"%s\", \"tipo\":\"%s\", \"valor\":\"%s\", \"errores\":[%s] }", dato.getCodigo(), dato.getTipoDato(), dato.getValor(), toJSON(dato.getErrores())))
				.collect(Collectors.joining(",")));
		
		return builder.toString();
	}

	private static String toJSON(net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajadores trabajadores) {
		if ( trabajadores == null )
			return "";

		return toJS0N(trabajadores.getTrabajador());
	}

	private static String toJSON(net.aonsolutions.core.tgss.creta.jaxb.respuesta.Trabajadores trabajadores) {
		if ( trabajadores == null )
			return "";

		return toJSON(trabajadores.getTrabajador());
	}

	private static String toJS0N(Collection<net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador> trabajadores) {
		if ( trabajadores == null )
			return "";
		
		StringBuilder builder = new StringBuilder();
		builder.append(trabajadores.stream()
				.map(trabajador -> String.format("{\"naf\":\"%s\"}", trabajador.getNaf()))
				.collect(Collectors.joining(",")));

		return builder.toString();
	}


	private static String toJSON(Collection<net.aonsolutions.core.tgss.creta.jaxb.respuesta.Trabajador> trabajadores) {
		if ( trabajadores == null )
			return "";
				
		StringBuilder builder = new StringBuilder();
		builder.append(trabajadores.stream()
				.map(trabajador -> String.format("{\"naf\":\"%s\",\"errores\":[%s],\"tramos\":[%s] }", trabajador.getNaf(), toJSON(trabajador.getErrores()), toJSON(trabajador.getTramos())))
				.collect(Collectors.joining(",")));
		
		return builder.toString();
	}


	private static String toJSoN(Stream<net.aonsolutions.core.tgss.creta.jaxb.bases.LiquidacionMes> liquidacionesMes) {
		if ( liquidacionesMes == null )
			return "";

		StringBuilder builder = new StringBuilder();
		builder.append(
				liquidacionesMes.map(liquidacionMes -> String.format("%s", toJSON(liquidacionMes.getTrabajadores())))
						.collect(Collectors.joining(",")));

		return builder.toString();
	}

	private static net.aonsolutions.core.tgss.creta.jaxb.respuesta.Errores getErrores(net.aonsolutions.core.tgss.creta.jaxb.respuesta.Liquidacion liquidacion) {
		
		List<net.aonsolutions.core.tgss.creta.jaxb.respuesta.Error> list = new LinkedList<>();
		
		Optional.ofNullable(liquidacion.getDatosNoTratados()).ifPresent(d -> d.getContent().stream()
		.filter(o -> o instanceof net.aonsolutions.core.tgss.creta.jaxb.respuesta.LiquidacionMes)
		.map( o -> ((net.aonsolutions.core.tgss.creta.jaxb.respuesta.LiquidacionMes)o).getErrores())
		.filter( Objects::nonNull  )
		.forEach( e -> e.getError().forEach(list::add))
		);
		
		Optional.ofNullable(liquidacion.getErrores())
		.ifPresent( e -> e.getError().forEach(list::add));
		
		net.aonsolutions.core.tgss.creta.jaxb.respuesta.Errores errores = new net.aonsolutions.core.tgss.creta.jaxb.respuesta.Errores();
		errores.setError(list);

		return errores;
		
	}
	
	private static List<net.aonsolutions.core.tgss.creta.jaxb.respuesta.LiquidacionMes> getLiquidacionMes(net.aonsolutions.core.tgss.creta.jaxb.respuesta.Liquidacion liquidacion) {
		List<net.aonsolutions.core.tgss.creta.jaxb.respuesta.LiquidacionMes> liquidacionMes = new LinkedList<>();
		
		Optional.ofNullable(liquidacion.getDatosNoTratados()).ifPresent(d -> d.getContent().stream()
		.filter(o -> o instanceof net.aonsolutions.core.tgss.creta.jaxb.respuesta.LiquidacionMes)
		.forEach( o -> liquidacionMes.add((net.aonsolutions.core.tgss.creta.jaxb.respuesta.LiquidacionMes)o)));
		
		liquidacion.getLiquidacionMes().forEach(l -> liquidacionMes.add(l));

		return liquidacionMes;
		
	}

	private static String toJSON(Stream<net.aonsolutions.core.tgss.creta.jaxb.respuesta.LiquidacionMes> liquidacionesMes) {
		if ( liquidacionesMes == null )
			return "";
		

		StringBuilder builder = new StringBuilder();
		builder.append(
				liquidacionesMes
				.map(liquidacionMes -> String.format("%s", toJSON(liquidacionMes.getTrabajadores())))
				.filter( AonStringUtils::isNotBlank)
				.collect(Collectors.joining(",")));
		
		return builder.toString();
	}

	private static String toJSON(net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Trabajadores trabajadores) {
		StringBuilder builder = new StringBuilder();
		builder.append(trabajadores.getTrabajador().stream()
				.map(trabajador -> String.format("{\"naf\":\"%s\",\"ipf\":\"%s\",\"caf\":\"%s\",\"tramos\":[%s]}", trabajador.getNaf(), trabajador.getIpf().getNumeroIpf() ,trabajador.getCaf(), toJSON(trabajador.getTramos())))
				.collect(Collectors.joining(",")));

		return builder.toString();
	}

	private static String toJSON(net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Tramos tramos) {
		return tramos.getTramo().stream()
				.map(tramo -> String.format("{\"desde\":\"%s\", \"hasta\":\"%s\", \"dias\":\"%s\",\"peculiaridades\": [%s], \"datos\": [%s]}", 
						toString(tramo.getFechaDesde()),
						toString(tramo.getFechaHasta()), 
						tramo.getDiasCotizados(), 
						toJSON(tramo.getInformacionAfiliacion().getPeculiaridades()), 
						toJSON(tramo.getDatosTramo())))
				.collect(Collectors.joining(","));
	}
	
	private static String toJSON(net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.DatosTramo datosTramo) {
		if ( datosTramo == null )
			return "";
		return "";

	}

	private static String toJSON(net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Peculiaridades peculiaridades) {
		if ( peculiaridades == null )
			return "";
		
		return peculiaridades.getPeculiaridad().stream()
				.map(peculiaridad -> String.format("{\"cod\":\"%s\", \"colectivo\":\"%s\", \"fraccion\":\"%s\", \"valor\":\"%s\"}", peculiaridad.getCodPec(), peculiaridad.getColectIncentivado(),peculiaridad.getFraccionCuota(), peculiaridad.getValorPec()))
				.collect(Collectors.joining(","));
	}

	private static String toJS0N(Stream<net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.LiquidacionMes> liquidacionesMes) {
		StringBuilder builder = new StringBuilder();
		builder.append(
				liquidacionesMes.map(liquidacionMes -> String.format("%s", toJSON(liquidacionMes.getTrabajadores())))
						.collect(Collectors.joining(",")));

		return builder.toString();
	}

	// ------------------------------------------------------------------------

	private static interface JSON {
		String toJSON();
	}

	private static class Event<T extends Event<?>> implements JSON {

		private String message;
		private String id = "666";

		public String getMessage() {
			return message;
		}

		public T setId(String id) {
			this.id = id;
			return (T) this;
		}

		public T setMessage(String message) {
			this.message = message;
			return (T) this;
		}
		
		public String getId() {
			return id;
		}
		
		// --------------------------------------------------------------- JSON

		@Override
		public String toJSON() {
			return String.format("{"
					+ "\"id\":\"%s\",\r\n"
					+ "\"message\":\"%s\",\r\n" 
					+ "}",
					id,
					message);
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
						+ "\"id\":\"%s\",\r\n"
						+ "\"message\":\"%s\",\r\n" 
						+ "\"type\":\"%s\",\r\n" 
						+ "\"code\":\"%s\",\r\n"
						+ "\"mandatory\":%s" 
						+ "%s"
						+ "%s"
						+ "%s"
						+ "\r\n"
						+ "}",
					getId(),
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
		
		private net.aonsolutions.core.tgss.creta.jaxb.bases.Bases bases;

		private List<Event> errors = new ArrayList<Event>();
		private List<Event> warnings = new ArrayList<Event>();

		private List<UnknownDato> unknown = new ArrayList<UnknownDato>();

		// --------------------------------------------------------------- Info
		
		@Override
		public void bases(net.aonsolutions.core.tgss.creta.jaxb.bases.Bases bases) {
			this.bases = bases;
		}
		
		public net.aonsolutions.core.tgss.creta.jaxb.bases.Bases getBases() {
			return bases;
		}
		
		public String getAutorizado() {
			return bases.getAutorizado();
		}
		
		public String getTipo() {
			return getLiquidacion().getTipo();
		}
		
		public String [] getCCCs() {
			return 
			bases.getLiquidacion()
			.stream()
			.map(l -> l.getCcc() )
			.map(c -> c.getRegimen() + c.getProvincia() + c.getNumero() )
			.toArray(String[]::new)
			;
		}
		
		public String getDesdeMes() {
			return getLiquidacion().getPeriodoDesde().getMes();
		}

		public String getDesdeAnho() {
			return getLiquidacion().getPeriodoDesde().getAnho();
		}

		public String getHastaMes() {
			return getLiquidacion().getPeriodoHasta().getMes();
		}

		public String getHastaAnho() {
			return getLiquidacion().getPeriodoHasta().getAnho();
		}

		public net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion getLiquidacion() {
			return bases.getLiquidacion().stream().findFirst().orElseThrow(() -> new IllegalArgumentException());
		}
		
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
					.setId(format("%s%s%s%s%s%s", 
							salary.getEmployeeSSNumber(),
							dato.getCodigo(),
							tramo.getFechaDesde().getDia(),
							tramo.getFechaDesde().getMes(),
							tramo.getFechaHasta().getDia(),
							tramo.getFechaHasta().getMes()
							))
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
					.setId(format("%s%s%s%s%s%s", 
							salary.getEmployeeSSNumber(),
							datoSolicitado.getCodigo(),
							tramo.getFechaDesde().getDia(),
							tramo.getFechaDesde().getMes(),
							tramo.getFechaHasta().getDia(),
							tramo.getFechaHasta().getMes()
							))
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
			errors.add(new Event()
					.setId(format("%s%s%s%s%s%s", 
					salary.getEmployeeSSNumber(),
					datoSolicitado.getCodigo(),
					tramo.getFechaDesde().getDia(),
					tramo.getFechaDesde().getMes(),
					tramo.getFechaHasta().getDia(),
					tramo.getFechaHasta().getMes()
					))
					.setMessage(format(
					"%s (IPF:%s, NAF:%s) .Tramo para %s (%5$td/%5$tm/%5$tY..%6$td/%6$tm/%6$tY) incorrecto se esperaba (%7$s/%8$s/%9$s...%10$s/%11$s/%12$s)",
					// salary.getEnterpriseName(),
					salary.getEmployeeName(), 
					salary.getEmployeeDocument(), 
					salary.getEmployeeSSNumber(),
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

		@Override
		public void negativeDato(String  var, Double value, Dato datoSolicitado,
				Tramo tramo, Salary salary) {
			errors.add(new Event()
					.setId(format("%s%s%s%s%s%s", 
							salary.getEmployeeSSNumber(),
							datoSolicitado.getCodigo(),
							tramo.getFechaDesde().getDia(),
							tramo.getFechaDesde().getMes(),
							tramo.getFechaHasta().getDia(),
							tramo.getFechaHasta().getMes()
							))
					.setMessage(format(
					"%s (IPF:%s, NAF:%s) .%s (%s/%s/%s..%s/%s/%s) negativo '%.2f'",
					// salary.getEnterpriseName(),
					salary.getEmployeeName(), 
					salary.getEmployeeDocument(), 
					salary.getEmployeeSSNumber(),
					// salary.getEnterpriseCCC(),
					var, 
					tramo.getFechaDesde().getDia(),
					tramo.getFechaDesde().getMes(),
					tramo.getFechaDesde().getAnho(),
					tramo.getFechaHasta().getDia(),
					tramo.getFechaHasta().getMes(),
					tramo.getFechaHasta().getAnho(),
					value)));
		};
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
		
		
		@Override
		public void invalidLiquidacion(net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion liquidacion,
				Exception e) {
			warnings.add(new NoDiffs().setMessage(
					format("(%s%s%s) %s .",
							liquidacion.getCcc().getProvincia(), 
							liquidacion.getCcc().getRegimen(),
							liquidacion.getCcc().getNumero(),
							e.getLocalizedMessage()
							)
					)
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
	
	private static class CheckNotEqualsBasesCallback implements BasesCallback {
		
		private net.aonsolutions.core.tgss.creta.jaxb.bases.Bases bases;
		
		public CheckNotEqualsBasesCallback(net.aonsolutions.core.tgss.creta.jaxb.bases.Bases bases) {
			this.bases = bases;
		}
		
		@Override
		public void bases(net.aonsolutions.core.tgss.creta.jaxb.bases.Bases bases) {
			if ( compare(this.bases, bases, AonStringUtils::compareIgnoreCase) == 0)
				throw new IllegalArgumentException();
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

	public abstract static class ProgressCallback implements BasesCallback {
		
		
		@Override
		public void trabajadorAdded(net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
				Trabajador trabajadorCreta, Salary salary) {
			
			progress(String.format("%s: %s [%s]", salary.getEnterpriseName(), salary.getEmployeeName(), salary.getEmployeeDocument()));
		}
		
		public abstract void progress(String comment) ;
	}

	private static <J extends JSON> String toJSON(List<J> jsons) {
		StringBuilder buff = new StringBuilder();
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

	
	private static Stream<net.aonsolutions.core.tgss.creta.jaxb.bases.Bases> findBases(HttpServletRequest req) throws SQLException{
		return Stream.empty();
//		String login = ":-)" ; 
//		Date from = getFromDate();
//		String domainName = req.getServerName();
//		Integer domainId = AonServletUtils.getDomainID(domainName);
//		Collection<String> cccs = getParameterValues(req, Parameter.CCC);
//		
//		return
//		findAttachs(domainName, domainId, login, RegistryAttachmentType.CRETA_BASES, from, cccs)
//		.map(attach-> unmarshall(net.aonsolutions.core.tgss.creta.jaxb.bases.Bases.class, attach.getData()))
//		.filter(optional -> optional.isPresent())
//		.map(optional -> optional.get())
//		;
	}

	private static Stream<net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta> findRespuestas(HttpServletRequest req) throws SQLException{
		Date from = getFromDate();
		String domainName = req.getServerName();
		Integer domainId = AonServletUtils.getDomainID(domainName);
		Collection<String>  cccs = getParameterValues(req, Parameter.CCC);
		String login = req.getParameter(CretaService.Parameter.USER.name());
		
		return
		findAttachs(domainName, domainId, login, RegistryAttachmentType.CRETA_RESPUESTA, from, cccs)
		.map(attach-> unmarshall(net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta.class, attach.getData()))
		.filter(optional -> optional.isPresent())
		.map(optional -> optional.get())
		.map( r -> {
			if ( cccs != null && !cccs.isEmpty() ) {
				r.getLiquidacion().removeIf(l -> !cccs.contains(l.getCcc().getProvincia() + l.getCcc().getNumero() ) );
			}
			return r;
		})

		;
	}

	private static Stream<net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta> findNotStarted(HttpServletRequest req) throws SQLException{
		Date firstDayOfMonth = AonDateUtils.getFirstDayOfMonth(new Date());				
		Date from = AonDateUtils.add(firstDayOfMonth, Calendar.MONTH, -1 );
		//String login = req.getParameter(CretaService.Parameter.USER.name());  
		
		String domainName = req.getServerName();
		Integer domainId = AonServletUtils.getDomainID(domainName);
		Collection<String> cccs = findCCCs(domainName, domainId, new java.sql.Date(from.getTime()), getParameterValues(req, Parameter.CCC));
		cccs = filter(cccs);
		
		
		
		return cccs.stream()
		.map(ccc -> getNotStartedRespuesta(ccc, from))
//		.map(ccc -> getNotStartedAttachData(ccc, from))
//		.map(data-> unmarshall(net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta.class,data) )
//		.filter(optional -> optional.isPresent())
//		.map(optional -> optional.get())
//		.peek(r -> System.out.println(r.getAutorizado()))
		;
	}

	private static Date getFromDate() {
		Date today = new Date();
		int dayOfMonth = AonDateUtils.get(today, Calendar.DAY_OF_MONTH);
		Date firstDayOfMonth = AonDateUtils.getFirstDayOfMonth(today);				
		return dayOfMonth >= 33 ? firstDayOfMonth : AonDateUtils.add(firstDayOfMonth, Calendar.MONTH, -1 );
	}

	private static Stream<net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos> findTrabajadoresYTramos(HttpServletRequest req) throws SQLException{
		String domainName = req.getServerName();
		Integer domainId = AonServletUtils.getDomainID(domainName);
		Date from = getFromDate();
		Collection<String>  cccs = getParameterValues(req, Parameter.CCC);
		String login = req.getParameter(CretaService.Parameter.USER.name());; //":-)" ; 

		
		return distinct(
			findAttachs(domainName, domainId, login, RegistryAttachmentType.CRETA_TRABAJADORES_Y_TRAMOS, from, cccs)
			.map(attach-> unmarshall(net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos.class, attach.getData()))
			.filter(optional -> optional.isPresent())
			.map(optional -> optional.get())
		)
		;
	}
	
	private static Stream<net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos> distinct(Stream<net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos> ts){
		return ts
				
//		.peek( t -> {
//			if ( (t.getLiquidacion() == null )
//					|| ( t.getLiquidacion().getCcc() == null )
//					|| ( t.getLiquidacion().getPeriodoDesde() == null )
//					|| ( t.getLiquidacion().getPeriodoHasta() == null )
//					)
//				try {
//					Utils.marshal(t, System.out );
//				} catch (JAXBException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		})
				
		.filter(t -> t.getLiquidacion() != null )
		.filter(t -> t.getLiquidacion().getCcc() != null )
		.filter(t -> t.getLiquidacion().getPeriodoDesde() != null )
		.filter(t -> t.getLiquidacion().getPeriodoHasta() != null )
		
		.collect(Collectors.groupingBy(
		t -> 
			t.getLiquidacion().getTipo()
			+ t.getLiquidacion().getCcc().getRegimen()
			+ t.getLiquidacion().getCcc().getProvincia()
			+ t.getLiquidacion().getCcc().getNumero()
			+ t.getLiquidacion().getPeriodoDesde().getAnho()
			+ t.getLiquidacion().getPeriodoDesde().getMes()
			+ t.getLiquidacion().getPeriodoHasta().getAnho()
			+ t.getLiquidacion().getPeriodoHasta().getMes()
			))
		.values().stream()
		.peek(l -> Collections.sort(l, (t1,t2)-> 
			(
			t1.getLiquidacion().getFechaHoraRecaudacion().getFechaRecaudacion().getAnho()
			+ t1.getLiquidacion().getFechaHoraRecaudacion().getFechaRecaudacion().getMes()
			+ t1.getLiquidacion().getFechaHoraRecaudacion().getFechaRecaudacion().getDia()
			+ t1.getLiquidacion().getFechaHoraRecaudacion().getHoraRecaudacion())
			.compareTo(
			t2.getLiquidacion().getFechaHoraRecaudacion().getFechaRecaudacion().getAnho()
			+ t2.getLiquidacion().getFechaHoraRecaudacion().getFechaRecaudacion().getMes()
			+ t2.getLiquidacion().getFechaHoraRecaudacion().getFechaRecaudacion().getDia()
			+ t2.getLiquidacion().getFechaHoraRecaudacion().getHoraRecaudacion()
			)
		))
		.map(l -> l.get(0));
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

		Integer userDomain = AON.getUser(domainName, domainId, login).getDomain();

		return  AON.getAttach(
				domainName, 
				domainId, 
				login,
				p -> 
				p.getDomainProperty().in(new Integer[]{domainId, userDomain})
				.and(p.getTypeProperty().eq((byte)type.ordinal()))
				.and(p.getDescriptionProperty().eq(md5)), 
				AttachType.REGISTRY
				);

	}
	
	private static Stream<Attach> findAttachs(String domainName, Integer domainId, String login, RegistryAttachmentType type, Date from, Collection<String>  cccs) {
		
		Integer userDomain = AON.getUser(domainName, domainId, login).getDomain();
		
		return  AON.getAttachList(
				domainName, 
				domainId, 
				login,    
				p -> 
					p.getDomainProperty().in(new Integer[]{domainId, userDomain})
					.and(p.getTypeProperty().eq((byte)type.ordinal()))
					.and(p.getAttachDateProperty().ge(new java.sql.Date(from.getTime())))
					.and(cccs.stream().map(ccc -> p.getDataProperty().like(("%"+ccc.substring(2)+"%").getBytes())).reduce((f1,f2)->f1.or(f2)).orElse(null))
				,
				AttachType.REGISTRY
				)
				.stream()
				.filter(a -> checkData(a, login));
		
	}



	private static void __onTrabajadoresYTramos (PrintWriter os, 
			Stream<net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos> ts,
			Stream<net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta> rs,
			Stream<net.aonsolutions.core.tgss.creta.jaxb.bases.Bases> bs) {
		
			os.println("parent.__onTrabajadoresYTramos(");
			os.println("[");
			os.println("//BEGIN_TRABAJADORES_TRAMOS");
			os.flush();
			
			// @formatter:off
			Iterator<net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos> tsIt = ts.sorted(CretaServlet::compare).iterator();
			String sep = "";
			while ( tsIt.hasNext() ){
				net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos t = tsIt.next();
				os.printf("%s\r\n{\"name\":\"%s\",\"authorized\":\"%s\",\"externalReference\":\"%s\",%s,\"employees\":[%s],\"file\":\"%s\"}\r\n", sep ,CretaService.File.TRABAJADORES_TRAMOS, t.getAutorizado(), t.getReferenciaExterna(), toJSON(t.getLiquidacion()),toJS0N(t.getLiquidacion().getLiquidacionMes().stream()), marshallAndEncode(t));
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
					os.printf("%s\r\n{\"name\":\"%s\",\"authorized\":\"%s\",\"externalReference\":\"%s\",%s,\"errors\":[%s],\"employees\":[%s],\"file\":\"%s\"}\r\n",sep, CretaService.File.RESPUESTA, r.getAutorizado(), r.getReferenciaExterna(), toJSON(l), toJSON(getErrores(l)),toJSON(getLiquidacionMes(l).stream()), marshallAndEncode(create(l,r)));
					os.flush();
					sep = ",";
					
				}
			}
			// @formatter:on

			os.println("//END_RESPUESTAS");
			os.flush();
			os.println("]");

			os.println(",");

			os.println("[");
			os.println("//BEGIN_BASES");
			os.flush();
			// @formatter:off
			Iterator<net.aonsolutions.core.tgss.creta.jaxb.bases.Bases> bsIt = bs.iterator();
			sep = "";
			while ( bsIt.hasNext() ){
				net.aonsolutions.core.tgss.creta.jaxb.bases.Bases b = bsIt.next();
				List<net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion> liquidacion = b.getLiquidacion();
				for  (net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion l : liquidacion ){
					os.printf("%s\r\n{\"name\":\"%s\",\"authorized\":\"%s\",\"externalReference\":\"%s\",%s,\"employees\":[%s],\"file\":\"%s\"}\r\n",sep, CretaService.File.BASES, b.getAutorizado(), b.getReferenciaExterna(), toJSON(l), toJSoN(l.getLiquidacionMes().stream()), marshallAndEncode(create(l,b)));
					os.flush();
					sep = ",";
					
				}
			}
			// @formatter:on

			os.println("//END_BASES");
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
	
	private static CloseableAONContext getAONAonContext(HttpServletRequest req) {
		String user = req.getParameter(CretaService.Parameter.USER.name());
		String domain = req.getParameter(CretaService.Parameter.DOMAIN.name());
		return AONContext.getAONContext(domain, user);
	}
	
	private static Certificate getCertificate(AONContext ctx) {
		User user = AON.getUser(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser());
		return AON.getCertificate(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), user.getId(), "TGSS");
//		return AON.getCertificate(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), user.getId());
	}

	private static Optional<Employee> getEmployee(AONContext ctx, String ccc, String naf, Date startDate, Date endDate) {
		return
		PAYROLL.getEmployees(
				ctx.getDomainName(), 
				ctx.getDomainId(), 
				ctx.getUser(), 
				p -> p.getCCCProperty().eq(ccc)
				.and(p.getNafProperty().eq(naf)))
		.findAny()
		;
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
	
	private static Date toDate(String mes, String anho){
		
		
		Calendar calendar = Calendar.getInstance();
		
		//Hora hora = fechaHoraRecaudacion.getHoraRecaudacion();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);

		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.MONTH, Integer.parseInt(mes)-1);
		calendar.set(Calendar.YEAR, Integer.parseInt(anho));

		return calendar.getTime();
		
	}

	private static Date toDate(net.aonsolutions.core.tgss.creta.jaxb.bases.Periodo periodo){
		
		
		Calendar calendar = Calendar.getInstance();
		
		//Hora hora = fechaHoraRecaudacion.getHoraRecaudacion();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);

		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.MONTH, Integer.parseInt(periodo.getMes())-1);
		calendar.set(Calendar.YEAR, Integer.parseInt(periodo.getAnho()));

		return calendar.getTime();
		
	}

	private static Date toDate(Periodo periodo){
		
		
		Calendar calendar = Calendar.getInstance();
		
		//Hora hora = fechaHoraRecaudacion.getHoraRecaudacion();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);

		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.MONTH, Integer.parseInt(periodo.getMes())-1);
		calendar.set(Calendar.YEAR, Integer.parseInt(periodo.getAnho()));

		return calendar.getTime();
		
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
	
	
	private static Collection<String> getParameterValues (HttpServletRequest req, CretaService.Parameter param) {
		return Optional.ofNullable(req.getParameterValues(param.name())).map(values -> Arrays.asList(values)).orElse(Collections.emptyList());
	}

	private static Collection<String> getParameterValues (HttpServletRequest req, CretaService.Parameter param, Supplier<Collection<String>> supplier) {
		return Optional.ofNullable(req.getParameterValues(param.name())).map(values -> ( Collection<String> )Arrays.asList(values)).orElseGet(supplier);
	}

	private static int getAnhoControl() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		return c.get(Calendar.YEAR);
	}

	private static Month getMesControl() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		
		switch ( c.get(Calendar.MONTH) ) {
		case Calendar.JANUARY:
			return Month.JANUARY;
		case Calendar.FEBRUARY:
			return Month.FEBRUARY;
		case Calendar.MARCH:
			return Month.MARCH;
		case Calendar.APRIL:
			return Month.APRIL;
		case Calendar.MAY:
			return Month.MAY;
		case Calendar.JUNE:
			return Month.JUNE;
		case Calendar.JULY:
			return Month.JULY;
		case Calendar.AUGUST:
			return Month.AUGUST;
		case Calendar.SEPTEMBER:
			return Month.SEPTEMBER;
		case Calendar.OCTOBER:
			return Month.OCTOBER;
		case Calendar.NOVEMBER:
			return Month.NOVEMBER;
		case Calendar.DECEMBER:
			return Month.DECEMBER;
		}
		
		throw new IllegalArgumentException();
	}
	
	private static int compare( net.aonsolutions.core.tgss.creta.jaxb.bases.Bases b1, net.aonsolutions.core.tgss.creta.jaxb.bases.Bases b2, Comparator<String> c) {
		return compare(b1.getLiquidacion(), b2.getLiquidacion(), (l1,l2) -> compare(l1,  l2, c) );
	}
	

	private static <T extends Object > int compare(List<T> l1, List<T> l2, Comparator<T> c) {
		int compare = l1.size() - l2.size();
		if ( compare != 0 )
			return compare;
		
		Collections.sort(l1, c);
		Collections.sort(l2, c);
		
		Iterator<T> it1 = l1.iterator();
		Iterator<T> it2 = l2.iterator();
		while ( it1.hasNext() && it2.hasNext() ) {
			compare = c.compare(it1.next(), it2.next());
			if ( compare != 0 ) {
				return compare;
			}
		}
		return 0;
	}

	private static int compare(net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion l1, net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion l2, Comparator<String> c) {
		int compare = c.compare(l1.getTipo(), l2.getTipo());
		if ( compare != 0 )
			return compare;
		compare = compare(l1.getCcc(), l2.getCcc(),c);
		if ( compare != 0 )
			return compare;
		compare = compare(l1.getPeriodoDesde(), l2.getPeriodoDesde(),c);
		if ( compare != 0 )
			return compare;
		compare = compare(l1.getPeriodoHasta(), l2.getPeriodoHasta(),c);
		if ( compare != 0 )
			return compare;
		compare = compare(l1.getLiquidacionMes(), l2.getLiquidacionMes(),(lm1,lm2) -> compare(lm1,lm2,c));
		if ( compare != 0 )
			return compare;
		
		return c.compare(l1.getAceptarBasesAnteriores(), l2.getAceptarBasesAnteriores());
		
	}
	
	private static int compare(net.aonsolutions.core.tgss.creta.jaxb.bases.LiquidacionMes l1, net.aonsolutions.core.tgss.creta.jaxb.bases.LiquidacionMes l2, Comparator<String> c) {
		int compare = compare(l1.getMesLiquidativo(), l2.getMesLiquidativo(), c);
		if ( compare != 0 )
			return compare;
		return compare(l1.getTrabajadores().getTrabajador(), l2.getTrabajadores().getTrabajador(), (t1,t2) -> compare(t1,t2,c));
	}

	private static int compare(net.aonsolutions.core.tgss.creta.jaxb.bases.CtaCot l1, net.aonsolutions.core.tgss.creta.jaxb.bases.CtaCot l2, Comparator<String> c) {
		int compare = c.compare(l1.getNumero(),  l2.getNumero());
		if ( compare != 0 )
			return compare;
		compare = c.compare(l1.getProvincia(),  l2.getProvincia());
		if ( compare != 0 )
			return compare;
		return c.compare(l1.getNumero(),  l2.getNumero());
	}

	private static int compare(net.aonsolutions.core.tgss.creta.jaxb.bases.Fecha l1, net.aonsolutions.core.tgss.creta.jaxb.bases.Fecha l2, Comparator<String> c) {
		int compare = c.compare(l1.getDia(),  l2.getDia());
		if ( compare != 0 )
			return compare;
		compare = c.compare(l1.getMes(),  l2.getMes());
		if ( compare != 0 )
			return compare;
		return c.compare(l1.getAnho(),  l2.getAnho());
	}

	private static int compare(net.aonsolutions.core.tgss.creta.jaxb.bases.Periodo l1, net.aonsolutions.core.tgss.creta.jaxb.bases.Periodo l2, Comparator<String> c) {
		int compare = c.compare(l1.getMes(),  l2.getMes());
		if ( compare != 0 )
			return compare;
		return c.compare(l1.getAnho(),  l2.getAnho());
	}
	
	
	private static int compare(net.aonsolutions.core.tgss.creta.jaxb.bases.Dato d1, net.aonsolutions.core.tgss.creta.jaxb.bases.Dato d2, Comparator<String> c) {
		int compare = c.compare(d1.getTipoDato(), d2.getTipoDato());
		if ( compare != 0 )
			return compare;
		compare = c.compare(d1.getCodigo(), d2.getCodigo());
		if ( compare != 0 )
			return compare;
		return c.compare(d1.getValor(), d2.getValor());
	}

	private static int compare(net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador t1, net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador t2, Comparator<String> c) {
		int compare = c.compare(t1.getNaf(),  t2.getNaf());
		if ( compare != 0 )
			return compare;
		return compare(t1.getTramos().getTramo(),  t2.getTramos().getTramo(), (_t1,_t2) -> compare(_t1,_t2,c));
	}

	private static int compare(net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo t1, net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo t2, Comparator<String> c) {
		int compare = compare(t1.getFechaDesde(), t2.getFechaDesde(),c);
		if ( compare != 0 )
			return compare;
		compare = compare(t1.getFechaHasta(), t2.getFechaHasta(),c);
		if ( compare != 0 )
			return compare;
		return compare(t1.getDatosTramo().getDato(),  t2.getDatosTramo().getDato(), (d1,d2) -> compare(d1,d2,c));
	}
	
	private static byte[] getNotStartedAttachData(String ccc, Date from) {
		// 012345XXXXXX
		String regimen = AonStringUtils.substring(ccc,0,2);
		String provincia = AonStringUtils.substring(ccc,2,6);
		String numero = AonStringUtils.substring(ccc,6);
		
		int mes = AonDateUtils.get(from, Calendar.MONTH) +1;
		int anho =  AonDateUtils.get(from, Calendar.YEAR);
		
		
		return String.format(
		"<?xml version='1.0' encoding='UTF-8'?>\n" + 
		"<Respuesta xmlns=\"http://www.seg-social.es/creta/esquemas/V120/Respuesta\">\n" + 
		"  <Autorizado>%d</Autorizado>\n" + 
		"  <ReferenciaExterna>%s</ReferenciaExterna>\n" + 
		"  <Liquidacion>\n" + 
		"    <Ccc>\n" + 
		"      <Regimen>%s</Regimen>\n" + 
		"      <Provincia>%s</Provincia>\n" + 
		"      <Numero>%s</Numero>\n" + 
		"    </Ccc>\n" + 
		"    <PeriodoDesde>\n" + 
		"      <Mes>%02d</Mes>\n" + 
		"      <Anho>%d</Anho>\n" + 
		"    </PeriodoDesde>\n" + 
		"    <PeriodoHasta>\n" + 
		"      <Mes>%02d</Mes>\n" + 
		"      <Anho>%d</Anho>\n" + 
		"    </PeriodoHasta>\n" + 
		"    <Tipo>L00</Tipo>\n" + 
		"    <FechaHoraRecaudacion>\n" + 
		"      <FechaRecaudacion>\n" + 
		"        <Dia>01</Dia>\n" + 
		"        <Mes>%02d</Mes>\n" + 
		"        <Anho>%d</Anho>\n" + 
		"      </FechaRecaudacion>\n" + 
		"      <HoraRecaudacion>000000</HoraRecaudacion>\n" + 
		"    </FechaHoraRecaudacion>\n" + 
		"    <Errores>\n" + 
		"      <Error>\n" + 
		"        <CodigoErr>A9999</CodigoErr>\n" + 
		"        <Descripcion>Liquidación, no inciada</Descripcion>\n" + 
		"      </Error>\n" + 
		"    </Errores>\n" + 
		"  </Liquidacion>\n" + 
		"</Respuesta>"
		, System.currentTimeMillis()
		, CretaService.AON_REFERENCIA_EXTERNA
		, regimen
		, provincia
		, numero 
		, mes
		, anho
		, mes
		, anho
		, mes
		, anho
		)
		.getBytes()		
		;
	}
	
	private static Respuesta getNotStartedRespuesta(String ccc, Date from) {
		// 012345XXXXXX
		String regimen = AonStringUtils.substring(ccc,0,2);
		String provincia = AonStringUtils.substring(ccc,2,6);
		String numero = AonStringUtils.substring(ccc,6);
		
		String mes = String.format("%02d", AonDateUtils.get(from, Calendar.MONTH) +1);
		String anho =  String.format("%02d", AonDateUtils.get(from, Calendar.YEAR));
		
		Respuesta respuesta = new Respuesta();
		respuesta.setAutorizado( AonStringUtils.repeat('6', 8));
		respuesta.setReferenciaExterna(CretaService.AON_REFERENCIA_EXTERNA);
		
		net.aonsolutions.core.tgss.creta.jaxb.respuesta.Liquidacion liquidacion = 
		new net.aonsolutions.core.tgss.creta.jaxb.respuesta.Liquidacion();
		
		liquidacion.setTipo("L00");
		
		CtaCot ctaCot = new CtaCot();
		ctaCot.setRegimen(regimen);
		ctaCot.setNumero(numero);
		ctaCot.setProvincia(provincia);
		liquidacion.setCcc(ctaCot);
		
		net.aonsolutions.core.tgss.creta.jaxb.respuesta.Periodo periodo = 
		new net.aonsolutions.core.tgss.creta.jaxb.respuesta.Periodo();
		periodo.setMes(mes);
		periodo.setAnho(anho);
		liquidacion.setPeriodoDesde(periodo);
		liquidacion.setPeriodoHasta(periodo);	
//		liquidacion.setFechaControl(periodo);
		
		FechaHoraRecaudacion fechaHoraRecaudacion = 
		new FechaHoraRecaudacion();
		net.aonsolutions.core.tgss.creta.jaxb.respuesta.Fecha fecha = 
		new net.aonsolutions.core.tgss.creta.jaxb.respuesta.Fecha();
		fecha.setDia("01");
		fecha.setMes(mes);
		fecha.setAnho(anho);
		fechaHoraRecaudacion.setFechaRecaudacion(fecha);
		fechaHoraRecaudacion.setHoraRecaudacion("000000");
		liquidacion.setFechaHoraRecaudacion(fechaHoraRecaudacion);
		

		Errores errores = new Errores();
		Error error = new Error();
		error.setCodigoErr("A9999");
		error.setDescripcion("Liquidación, no inciada");
		errores.getError().add(error);
		
		liquidacion.setErrores(errores);
		
		respuesta.getLiquidacion().add(liquidacion);
		
		
		return respuesta;
		
		
	}

	private static Collection<String> findCCCs(String domain , int domainId, java.sql.Date month, Collection<String> cccs) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		try ( 
				Connection connection = AonServletUtils.getConnection(domain) ;
			){
			DSLContext dslContect = DSL.using(connection, settings );
			java.sql.Date firstDayOfMonth = AonDateUtils.getFirstDayOfMonth(month);
			java.sql.Date lastDayOfMonth = AonDateUtils.getLastDayOfMonth(month);
			
			SelectConditionStep<Record> selectCCCs = 
			dslContect
			.select()
			.from(SALARY)
			.innerJoin(CONTRACT).onKey(FK_SALARY_CONTRACT)
			.innerJoin(ENTERPRISE_CCC).onKey(FK_CONTRACT_ENTERPRISE_CCC)
			.innerJoin(DOMAIN).onKey(Keys.FK_ENTERPRISE_CCC_DOMAIN)
			.where(DOMAIN.ID.eq(domainId))
			.or(DOMAIN.PARENT.eq(domainId))
			.and(SALARY.START_DATE.le(lastDayOfMonth))
			.and(SALARY.END_DATE.ge(firstDayOfMonth))
			.and(SALARY.SOCIAL_SECURITY_NUMBER.isNotNull()) // Skip RETAs
			;
			
			if ( cccs != null && !cccs.isEmpty())
				selectCCCs = selectCCCs.and(ENTERPRISE_CCC.CCC.in(cccs));
			
			return 
			selectCCCs
			.fetchStreamInto(ENTERPRISE_CCC)
			.map(ccc -> String.format("%s%s", getCCCType(ccc.getType()), ccc.getCcc() ))			
			.collect(Collectors.toSet())
			;
			
		} catch ( Throwable t ) {
			return Collections.emptySet();
		}
		
	}
	
	private static Collection<String> filter(Collection<String> cccs) {
		return cccs.stream().filter(ccc -> checkCCC(ccc)).collect(Collectors.toList());
	}
	
	private static boolean checkCCC(String ccc) {
		ccc = AonStringUtils.trim(ccc);
		ccc = AonStringUtils.substring(ccc, 4);
		
		return AonStringUtils.length(ccc) == 11 
				&& AonStringUtils.isNumeric(ccc) 
				&& checkProvinceCCC(ccc)
//				&& checkCtrlDigitCCC(ccc)
				;
	}

	private static boolean checkProvinceCCC(String ccc) {
		return getProvinces().containsKey(AonStringUtils.substring(ccc, 0,2));
	}
	
	private static boolean checkCtrlDigitCCC(String ccc) {
		int code = Integer.parseInt(AonStringUtils.substring(ccc, 2, 9));
		int ctrl = Integer.parseInt(AonStringUtils.substring(ccc, -2));
		return code % 97 == ctrl;
	}
	
	
	private static String getCCCType(Byte cccType) {
		switch (cccType) {
		case 0:
			return "0111";
		case 1:
			return "0111";
		case 2:
			return "0111";
		case 3:
			return "0111";
		case 4:
			return "0111";
		case 5:
			return "0111";
		case 6:
			return "0138";
		case 7:
			return "0163";
		default:
			return "0111";
		}
	} 
	
	private static CretaService.File getFile(Part part) {
		try {
			return CretaService.File.valueOf( part.getName());
		} catch ( IllegalArgumentException e) {
			return null;
		}
	}
	
	private static String getTipo(Respuesta respuesta) {
		return respuesta.getLiquidacion().stream().map(net.aonsolutions.core.tgss.creta.jaxb.respuesta.Liquidacion::getTipo).findAny().orElseThrow(IllegalArgumentException::new);		
	}

	private static net.aonsolutions.core.tgss.creta.jaxb.respuesta.Periodo getPeriodoDesde(Respuesta respuesta) {
		return respuesta.getLiquidacion().stream().map(net.aonsolutions.core.tgss.creta.jaxb.respuesta.Liquidacion::getPeriodoDesde).findAny().orElseThrow(IllegalArgumentException::new);		
	}

	private static net.aonsolutions.core.tgss.creta.jaxb.respuesta.Periodo getPeriodoHasta(Respuesta respuesta) {
		return respuesta.getLiquidacion().stream().map(net.aonsolutions.core.tgss.creta.jaxb.respuesta.Liquidacion::getPeriodoHasta).findAny().orElseThrow(IllegalArgumentException::new)  ;		
	}

	private static String [] getCCCs(Respuesta respuesta) {
		return 
		respuesta.getLiquidacion().stream()
		.map(l -> l.getCcc().getRegimen()+l.getCcc().getProvincia()+l.getCcc().getNumero())
		.toArray(String[]::new);
	}
	
	private static String [] getNafs(Respuesta respuesta) {
		return 
		respuesta.getLiquidacion().stream()
		.filter(l -> l.getLiquidacionMes() != null)
		.flatMap(l -> l.getLiquidacionMes().stream())
		.filter(lm -> lm.getTrabajadores() != null)
		.flatMap(lm -> lm.getTrabajadores().getTrabajador().stream())
		.map( t -> t.getNaf() )
		.toArray(String[]::new);
	}

}

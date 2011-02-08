package com.code.aon.ui.employee.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.Classpath;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.file.payroll.certificate.Certificate;
import com.esferalia.aon.file.payroll.certificate.data.Cotizacion;
import com.esferalia.aon.file.payroll.certificate.data.CuentaCotizacion;
import com.esferalia.aon.file.payroll.certificate.data.DistribucionJornada;
import com.esferalia.aon.file.payroll.certificate.data.Empresa;
import com.esferalia.aon.file.payroll.certificate.data.Periodo;
import com.esferalia.aon.file.payroll.certificate.data.Representante;
import com.esferalia.aon.file.payroll.certificate.data.Trabajador;
import com.esferalia.aon.file.payroll.certificate.data.Vacaciones;
import com.esferalia.aon.payroll.AonPayroll;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IDivisa;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.IFiniquito;
import com.esferalia.aon.payroll.core.IFiniquitoDiferencia;
import com.esferalia.aon.payroll.core.INomina;
import com.esferalia.aon.payroll.core.INominaDiferencia;
import com.esferalia.aon.payroll.core.ITrabajo;
import com.esferalia.aon.payroll.core.empleado.EmpleadoDAOFactory;
import com.esferalia.aon.payroll.core.empleado.IEmpleadoDAO;
import com.esferalia.aon.payroll.core.empresa.EmpresaDAOFactory;
import com.esferalia.aon.payroll.core.empresa.IEmpresaDAO;
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresa;
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresaDetalle;
import com.esferalia.aon.payroll.core.enumeration.TiempoContrato;
import com.esferalia.aon.payroll.core.enumeration.TipoNomina;
import com.esferalia.aon.payroll.core.enumeration.TipoTiempoParcial;
import com.esferalia.aon.payroll.core.nomina.INominaDAO;
import com.esferalia.aon.payroll.core.nomina.NominaDAOFactory;
import com.esferalia.aon.payroll.core.nomina.NominaParams;

public class CertificateWriter {
	
	private Certificate certificate;
	private IEmpleadoDAO empleadoDAO;
	private IEmpresaDAO empresaDAO;
	private INominaDAO nominaDAO;
	
	private IEmpleadoDAO getEmpleadoDAO() {
		if(empleadoDAO == null) {
			empleadoDAO = EmpleadoDAOFactory.getInstance().getEmpleadoDAO();
		}
		
		return empleadoDAO;
	}
	private IEmpresaDAO getEmpresaDAO() {
		if(empresaDAO == null) {
			empresaDAO = EmpresaDAOFactory.getInstance().getEmpresaDAO();
		}
		
		return empresaDAO;
	}
	private INominaDAO getNominaDAO() {
		if(nominaDAO == null) {
			nominaDAO = NominaDAOFactory.getInstance().getNominaDAO();
		}
		
		return nominaDAO;
	}
	
	public Certificate getCertificate() {
		return certificate;
	}
	
	public void setCertificate(Certificate certificate) {
		this.certificate = certificate;
	}
	
	public FileOutput createCertificate(IRemesaCertificadoEmpresa remesa, List<IRemesaCertificadoEmpresaDetalle> remesaDetail) throws ManagerBeanException {
		final String INDENT_AMOUNT_VALUE = "4";
		final String INDENT_AMOUNT_PROPERTY = "{http://xml.apache.org/xslt}indent-amount";
		
		try {
			setCertificate(new Certificate(remesa.getEmpresa().getRegistry().getDocument().getValue()));
			
			List<CuentaCotizacion> listaCuentas = new ArrayList<CuentaCotizacion>();
			for(String ccc: getCccList(remesaDetail)) {
				listaCuentas.add(createCuentaCotizacionRecord(ccc, remesa, remesaDetail));
			}
			getCertificate().setCuentaCotizacion(listaCuentas);
			
			FileOutput output = new FileOutput();
			File file = File.createTempFile("aon-temp", ".XML");
			FileOutputStream out = new FileOutputStream(file);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document xmldoc = builder.newDocument();
			Element root = certificate.getElement(xmldoc);
			xmldoc.appendChild(root);
			certificate.fillElement(xmldoc, root);
			
			DOMSource domSource = new DOMSource(xmldoc);
			StreamResult streamResult = new StreamResult(out);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer serializer = tf.newTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty(INDENT_AMOUNT_PROPERTY, INDENT_AMOUNT_VALUE);
			serializer.transform(domSource, streamResult);
			
			output.setErrors(certificate.getExceptions());
			output.setFile(file);
			
			if (output.getErrors().size() > 0) {
				AonUtil.addErrorMessage("Se han producido errores en la generación del fichero.");
				// No se lanza excepción, que vaya a la última página.
			} else {
				validateXmlPattern(file);
			}
			
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		} catch (PayrollException e) {
			throw new ManagerBeanException(e);
		} catch (ParserConfigurationException e) {
			throw new ManagerBeanException(e);
		} catch (TransformerConfigurationException e) {
			throw new ManagerBeanException(e);
		} catch (TransformerException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	public Element getError(Document xmldoc, Certificate certificate){
		if(certificate.getErrors()!=null){
			final String ERRORS = "Errores";
			final String ERROR = "Error";
			Element errores = xmldoc.createElement(ERRORS);
			for (Integer i : certificate.getErrors()) {
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				String errorMsg = null;
				errorMsg = AonPayroll.getMessage(locale,"aon_payroll_certificate_error_" + i);
				Element error = xmldoc.createElement(ERROR);
				error.appendChild(xmldoc.createTextNode(errorMsg));
				errores.appendChild(error);
			}
			return errores;
		} else {
			return null;
		}
	}
	
	private List<String> getCccList(List<IRemesaCertificadoEmpresaDetalle> remesaDetail) throws PayrollException{
		List<String> list = new ArrayList<String>();
		String ccc;
		for(IRemesaCertificadoEmpresaDetalle detail: remesaDetail){
			ccc = getEmpresaDAO().getEmpresaCccEmpleado(detail.getEmpleado());
			if(!list.contains(ccc)){
				list.add(ccc);
			}
		}
		return list;
	}
		
	private void validateXmlPattern(File xml) {
		final String SCHEMA = "enterpriseCertificate.xsd";
		
		try {
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			URL[] urls = Classpath.search(cl, "META-INF/", SCHEMA);
			Validator validator = sf.newSchema(urls[0]).newValidator();
			StreamSource source = new StreamSource(xml);
			validator.validate(source);
		} catch (Exception e) {
			String msg = "Error de formato al generar el XML";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
	}
	
	private CuentaCotizacion createCuentaCotizacionRecord(String ccc, IRemesaCertificadoEmpresa remesa, List<IRemesaCertificadoEmpresaDetalle> listaDetalle) throws PayrollException {
		CuentaCotizacion cuentaCotizacion = new CuentaCotizacion();
		Representante representante = new Representante();
		
		representante.setCifNif(remesa.getEmpresa().getRepresentanteDocument());
		representante.setNombre(parseMaxLength(remesa.getEmpresa().getNombreRepresentante(), 15));
		representante.setApellido1(parseMaxLength(remesa.getEmpresa().getApellido1Representante(), 20));
		
		if(!StringUtils.isBlank(remesa.getEmpresa().getApellido2Representante())) {
			representante.setApellido2(parseMaxLength(remesa.getEmpresa().getApellido2Representante(), 20));
		}
		
		if(!StringUtils.isBlank(remesa.getEmpresa().getCargo())) {
			representante.setCargo(parseMaxLength(remesa.getEmpresa().getCargo(), 40));
		}
		
		cuentaCotizacion.setRepresentante(representante);
		
		Empresa empresa = new Empresa();
		
		empresa.setCcc(parseToLength(ccc, 15));
		empresa.setCifNif(remesa.getEmpresa().getRegistry().getDocument().getValue());
		
		cuentaCotizacion.setEmpresa(empresa);
		
		List<Trabajador> listaTrabajadores = new ArrayList<Trabajador>();
		
		for(IRemesaCertificadoEmpresaDetalle detalle:listaDetalle) {
			if(getEmpresaDAO().getEmpresaCccEmpleado(detalle.getEmpleado()).equals(ccc)){
				listaTrabajadores.add(createTrabajadorRecord(detalle));
			}
		}
		
		cuentaCotizacion.setListaTrabajadores(listaTrabajadores);
		
		return cuentaCotizacion;
	}
	
	private Trabajador createTrabajadorRecord(IRemesaCertificadoEmpresaDetalle detalle) {
		List<ITrabajo> trabajos = null;
		
		try {
			trabajos = getEmpleadoDAO().getTrabajos(detalle.getEmpleado());
		}
		catch(PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
		
		if(trabajos == null || trabajos.size() == 0) {
			return null;
		}
		
		Trabajador trabajador = new Trabajador();
		
		trabajador.setDniNie(detalle.getEmpleado().getPersona().getRegistry().getDocument().getValue());
		trabajador.setNombre(parseMaxLength(detalle.getEmpleado().getPersona().getName(), 15));
		trabajador.setApellido1(parseMaxLength(detalle.getEmpleado().getPersona().getSurname(), 20));
		
		if(!StringUtils.isBlank(detalle.getEmpleado().getPersona().getLastName())) {
			trabajador.setApellido2(parseMaxLength(detalle.getEmpleado().getPersona().getLastName(), 20));
		}
		
		trabajador.setNumSs(detalle.getEmpleado().getPersona().getNumSS());
		trabajador.setGrupoCotizacion(parseToLength(trabajos.get(0).getBaseCotizacion().getCdg(), 2));
		trabajador.setTipoContrato(parseToLength(trabajos.get(0).getContratoTc2().getCdg(), 3));
		
		if(trabajos.get(0).getFechaFinCont() != null && trabajos.get(0).getFechaInicioCont() != null) {
			trabajador.setDuracionContrato(parseToLength(differenceBetweenDates(trabajos.get(0).getFechaInicioCont(), trabajos.get(0).getFechaFinCont()), 5));
		}
		
		// trabajador.setIndicadorDuracionContrato();
		trabajador.setCodProfesion(parseToLength(trabajos.get(0).getCno(), 7, false));
		// trabajador.setCargoPublicoSindical();
		// trabajador.setPorcentualDedicacion();
		trabajador.setFechaAltaEmpresa(parseFecha(detalle.getEmpleado().getFechaInicio()));
		trabajador.setCodCausaSuspension(parseToLength(detalle.getCausaSuspension().getValue(), 2));
		// trabajador.setFechaSuspensionExtincion(detalle.getEmpleado().getFechaFin().toString());
		trabajador.setFechaSuspensionExtincion(parseFecha(detalle.getFechaBaja()));
		// trabajador.setFechaFinSuspension();
		// trabajador.setEre();
		// trabajador.setPorcentualReduccionERE();
		// trabajador.setPorcentualReduccionOTROS();
		// trabajador.setCodCausaPorcentReduccion();
		// trabajador.setFechaDesdePeriodoSalarios();
		// trabajador.setFechaHastaPeriodoSalarios();
		trabajador.setDiasSalarioTramitacion("00000");
		
		/*
		 * NODOS
		 */
		if(!trabajos.get(0).getTiempoContrato().equals(TiempoContrato.COMPLETO)) {
			trabajador.setDistribucionJornada(createDistribucionJornadaRecord(detalle));
		}
		
		List<Cotizacion> listaDatosCotizacion = new ArrayList<Cotizacion>();
		
		listaDatosCotizacion.addAll(createDatosCotizacionRecord(detalle));
		
		trabajador.setDatosCotizacion(listaDatosCotizacion);
		trabajador.setDatosVacacionesCotizadas(createDatosVacacionesCotizadasRecord(detalle.getEmpleado()));
		
		return trabajador;
	}
	
	private DistribucionJornada createDistribucionJornadaRecord(IRemesaCertificadoEmpresaDetalle detalle) {
		List<ITrabajo> trabajosTP = null;
		DistribucionJornada jornada = null;
		
		try {
			trabajosTP = getEmpleadoDAO().getTrabajosTP(detalle.getEmpleado());
		} catch(PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
		
		if(trabajosTP != null && trabajosTP.size() > 0) {
			Periodo periodo = null;
			List<Periodo> listaPeriodos = new ArrayList<Periodo>();
			DateFormat dateDDMMYYYY = new SimpleDateFormat("ddMMyyyy");
			
			for(ITrabajo t:trabajosTP){
				if(t.getTipoTP() != null && t.getDiasTP()!= null) {
					if(periodo == null) {
						periodo = new Periodo();
						
						periodo.setFechaInicioPeriodo(parseFecha(t.getFecini()));
						
						if(dateDDMMYYYY.format(t.getFecfin()).equals("31129999")) {
							periodo.setFechaFinPeriodo(parseFecha(detalle.getFechaBaja()));
						}
						else {
							periodo.setFechaFinPeriodo(parseFecha(t.getFecfin()));
						}
						
						periodo.setTipoDistribucion(parseTipoDistribucion(t.getTipoTP()));
						periodo.setNumeroDiasTrabajadosPorSemanaOPeriodo(parseToLength(t.getDiasTP(), 5));
					}
					else if(periodo.getNumeroDiasTrabajadosPorSemanaOPeriodo().equals(parseToLength(t.getDiasTP(), 5)) && periodo.getTipoDistribucion().equals(parseTipoDistribucion(t.getTipoTP()))) {
						periodo.setFechaInicioPeriodo(parseFecha(t.getFecini()));
					}
					else {
						listaPeriodos.add(periodo);
						
						periodo = new Periodo();
						
						periodo.setFechaInicioPeriodo(parseFecha(t.getFecini()));
						periodo.setFechaFinPeriodo(parseFecha(t.getFecfin()));
						periodo.setTipoDistribucion(parseTipoDistribucion(t.getTipoTP()));
						periodo.setNumeroDiasTrabajadosPorSemanaOPeriodo(parseToLength(t.getDiasTP(), 5));
					}
				}
			}
			
			if(periodo != null) {
				listaPeriodos.add(periodo);
				
				jornada = new DistribucionJornada();
				jornada.setListaPeriodos(listaPeriodos);
			}
		}
		
		return jornada;
	}
	
	private List<Cotizacion> createDatosCotizacionRecord(IRemesaCertificadoEmpresaDetalle detalle) {
		INomina nomina = null;
		
		try {
			Integer totalDias = 0;
			
			Calendar calInicio = new GregorianCalendar();
			Calendar calFin = new GregorianCalendar();
			
			calInicio.setTime(detalle.getEmpleado().getFechaInicio());
			
			calFin.setTime(detalle.getEmpleado().getFechaFin());
			calFin.set(Calendar.DAY_OF_MONTH, calFin.getActualMaximum(Calendar.DAY_OF_MONTH));
			
			List<Cotizacion> cotizacionList = new ArrayList<Cotizacion>();
			
			while((calInicio.before(calFin) || calInicio.equals(calFin)) && totalDias < 180) {
				NominaParams params = new NominaParams();
				
				params.setEmpleado(detalle.getEmpleado());
				params.setTipo(TipoNomina.NORMAL);
				params.setMes(calFin.get(Calendar.MONTH)+1);
				params.setYear(calFin.get(Calendar.YEAR));
				
				nomina = getNominaDAO().getNomina(params);
				
				calFin.add(Calendar.DATE, -calFin.get(Calendar.DAY_OF_MONTH));
				
				if(nomina != null) {
					Double baseCg = nomina.getBaseCgPts();
					Double baseAcc = nomina.getBaseAccPts();
					Double baseDesempleo = nomina.getBasePerdes();
					
					List<INominaDiferencia> nominasDiferencia = getNominaDAO().getNominasDiferencia(params);
					
					for(INominaDiferencia nomDf:nominasDiferencia) {
						baseCg += nomDf.getBaseCgPts();
						baseAcc += nomDf.getBaseAccPts();
						baseDesempleo += nomDf.getBasePerdes();
					}
					
					if(nomina.getBaseHorasExtrasEstructurales() == 0 && nomina.getBaseHorasExtrasNoEstructurales() == 0) {
						baseDesempleo = baseAcc;
					} else if(!detalle.getEmpleado().getEmpresa().getDivisa().getCdg().equals("2")) {
						baseDesempleo = convertMoney(baseDesempleo, detalle.getEmpleado().getEmpresa().getDivisa());
					}
					
					totalDias += nomina.getDiasNomina();
					
					Cotizacion cotizacion = new Cotizacion();
					
					cotizacion.setAno(nomina.getYear().toString());
					cotizacion.setMes(parseToLength(nomina.getMes(), 2));
					cotizacion.setNumDiasCotizados(parseToLength(nomina.getDiasNomina(), 3));
					cotizacion.setBaseCotizacionContingenciasComunes(parseToLength(baseCg, 9));
					cotizacion.setBaseCotizacionDesempleo(parseToLength(baseDesempleo, 9));
					cotizacion.setObservaciones(null);
					
					cotizacionList.add(cotizacion);
				}
			}
			
			return cotizacionList;
		} catch(PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
		
		return null;
	}
	
	private Vacaciones createDatosVacacionesCotizadasRecord(IEmpleado empleado) {
		try {
			IFiniquito finiquito = getNominaDAO().getFiniquito(empleado);
			
			if(finiquito != null && finiquito.getDiasVacaciones() !=0 && finiquito.getImporteVacaciones() != 0) {
				Double baseAccidentesTrabajo = finiquito.getBaseAccidentesTrabajo();
				Double baseContingenciaGenerales = finiquito.getBaseContingenciasGenerales();
				
				IFiniquitoDiferencia finiquitodf = getNominaDAO().getFiniquitoDiferencia(empleado);
				
				if(finiquitodf != null && finiquitodf.getDiasVacaciones() != 0 && finiquitodf.getImporteVacaciones() != 0) {
					baseAccidentesTrabajo += finiquitodf.getBaseAccidentesTrabajo();
					baseContingenciaGenerales += finiquitodf.getBaseContingenciasGenerales();
				}
				
				Vacaciones vacaciones = new Vacaciones();
				
				vacaciones.setNumDiasCotizados(parseToLength(finiquito.getDiasVacaciones(), 3));
				vacaciones.setBaseCotizacionContingenciasComunes(parseToLength(baseContingenciaGenerales, 9));
				vacaciones.setBaseCotizacionDesempleo(parseToLength(baseAccidentesTrabajo, 9));
				vacaciones.setObservaciones(null);
				
				return vacaciones;
			}
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
		
		return null;
	}
	
	/*
	 * CONVERSIONES
	 */
	private Double convertMoney(Double money, IDivisa divisa) {
		return money;
	}
	
	private Integer differenceBetweenDates(Date from, Date to) {
		Integer diffDays = new Integer(0);
		final Double MS_PER_DAY = new Double(1000 * 60 * 60 * 24);
		
		if(from.before(to)) {
			diffDays = (int)((Math.floor((to.getTime() - from.getTime()) / MS_PER_DAY + 0.5d) + 1));
		}
		
		return diffDays;
	}
	
	private String parseMaxLength(String var, Integer lon) {
		if(var != null) {
			lon = Math.abs(lon);
			
			if(var.length() > lon) {
				var = var.substring(0, lon);
			}
		}
		
		return var;
	}
	
	private String parseToLength(String var, Integer lon, Boolean dir) {
		StringBuffer parse = new StringBuffer();
		
		if(var != null) {
			if(!dir) {
				parse.append(var);
			}
			
			for(int i = var.length(); i < Math.abs(lon); ++i) {
				parse.append("0");
			}
			
			if(dir) {
				parse.append(var);
			}
		}
		
		return parse.toString();
	}
	
	private String parseToLength(String var, Integer lon) {
		return parseToLength(var, lon, true);
	}
	
	private String parseToLength(Integer var, Integer lon, Boolean dir) {
		
		return parseToLength(String.valueOf(var), lon, dir);
	}
	
	private String parseToLength(Integer var, Integer lon) {
		return parseToLength(var, lon, true);
	}
	
	private String parseToLength(Double var, Integer lon, Boolean dir) {
		var *= 100;
		var = CommonUtil.round(var);
		
		return parseToLength(String.valueOf(var.intValue()), lon, dir);
	}
	
	private String parseToLength(Double var, Integer lon) {
		return parseToLength(var, lon, true);
	}
	
	private String parseFecha(Date date) {
		StringBuffer parse = null;
		
		if(date != null) {
			Calendar cal = new GregorianCalendar();
			
			cal.setTime(date);
			
			parse = new StringBuffer(String.valueOf(cal.get(Calendar.YEAR)));
			parse.append(parseToLength(cal.get(Calendar.MONTH) + 1, 2));
			parse.append(parseToLength(cal.get(Calendar.DAY_OF_MONTH), 2));
		}
		
		return parse.toString();
	}
	
	private String parseTipoDistribucion(TipoTiempoParcial tipoTP) {
		String parse = null;
		
		if(tipoTP != null) {
			parse = new String(String.valueOf(tipoTP.ordinal() + 1));
		}
		
		return parse;
	}
		
}

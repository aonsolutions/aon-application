package com.esferalia.aon.ui.payroll.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
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
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresa;
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresaDetalle;
import com.esferalia.aon.payroll.core.enumeration.TipoNomina;
import com.esferalia.aon.payroll.core.enumeration.TipoTiempoParcial;
import com.esferalia.aon.payroll.core.nomina.INominaDAO;
import com.esferalia.aon.payroll.core.nomina.NominaDAOFactory;
import com.esferalia.aon.payroll.core.nomina.NominaParams;

public class CertificateWriter {
	
	private Certificate certificate;
	private IEmpleadoDAO empleadoDAO;
	private INominaDAO nominaDAO;
	
	private IEmpleadoDAO getEmpleadoDAO() {
		if (empleadoDAO == null) {
			empleadoDAO = EmpleadoDAOFactory.getInstance().getEmpleadoDAO();
		}
		return empleadoDAO;
	}
	
	private INominaDAO getNominaDAO() {
		if (nominaDAO == null) {
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

	public FileOutput createCertificate(IRemesaCertificadoEmpresa remesa, List<IRemesaCertificadoEmpresaDetalle> remesaDetail ) throws ManagerBeanException {
		final String INDENT_AMOUNT_PROPERTY = "{http://xml.apache.org/xslt}indent-amount";
		final String INDENT_AMOUNT_VALUE = "4";
		try {
			setCertificate(new Certificate());
			List<CuentaCotizacion> listaCuentas = new ArrayList<CuentaCotizacion>();
			listaCuentas.add(createCuentaCotizacionRecord(remesa, remesaDetail));
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
			validateCertificateData(certificate);

			DOMSource domSource = new DOMSource(xmldoc);
			StreamResult streamResult = new StreamResult(out);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer serializer = tf.newTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty(INDENT_AMOUNT_PROPERTY, INDENT_AMOUNT_VALUE);
			serializer.transform(domSource, streamResult);
			validateXmlPattern(file);
			
			output.setFile(file);
//			output.setErrors(fdi.create());
			output.setErrors(new ArrayList<Exception>());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		} 
		catch (PayrollException e) {
			throw new ManagerBeanException(e);
		} catch (ParserConfigurationException e) {
			throw new ManagerBeanException(e);
		} catch (TransformerConfigurationException e) {
			throw new ManagerBeanException(e);
		} catch (TransformerException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	private void validateCertificateData(Certificate certificate) {
		ArrayList<Integer> errors = new ArrayList<Integer>();
		for(CuentaCotizacion cc: certificate.getCuentaCotizacion()){
			if(StringUtils.isBlank(cc.getRepresentante().getCifNif())){
				errors.add(0);
			}
			if(StringUtils.isBlank(cc.getRepresentante().getNombre())){
				errors.add(1);
			}
			if(StringUtils.isBlank(cc.getRepresentante().getApellido1())){
				errors.add(2);
			}
			if(StringUtils.isBlank(cc.getEmpresa().getCifNif())){
				errors.add(3);
			}
			if(StringUtils.isBlank(cc.getEmpresa().getCcc())){
				errors.add(4);
			}
			
			if(cc.getListaTrabajadores()!=null){
				for(Trabajador t: cc.getListaTrabajadores()){
					if(t!=null){
						if(StringUtils.isBlank(t.getDniNie())){
							errors.add(5);
						}
						if(StringUtils.isBlank(t.getNombre())){
							errors.add(6);
						}
						if(StringUtils.isBlank(t.getApellido1())){
							errors.add(7);
						}
						if(StringUtils.isBlank(t.getNumSs())){
							errors.add(8);
						}
						if(StringUtils.isBlank(t.getTipoContrato())){
							errors.add(9);
						}
						if(StringUtils.isBlank(t.getCodProfesion())){
							errors.add(10);
						}
						if(StringUtils.isBlank(t.getFechaAltaEmpresa())){
							errors.add(11);	
						}
						if(StringUtils.isBlank(t.getCodCausaSuspension())){
							errors.add(12);
						}
						if(StringUtils.isBlank(t.getFechaSuspensionExtincion())){
							errors.add(13);
						}
						if(StringUtils.isBlank(t.getDiasSalarioTramitacion())){
							errors.add(14);
						}
					
						/*
						 * NODOS
						 */
						if(t.getDistribucionJornada()!=null){
							for(Periodo p: t.getDistribucionJornada().getListaPeriodos()){
								if(StringUtils.isBlank(p.getTipoDistribucion())){
									errors.add(15);
								}
								if(StringUtils.isBlank(p.getFechaInicioPeriodo())){
									errors.add(16);
								}
								if(StringUtils.isBlank(p.getFechaFinPeriodo())){
									errors.add(17);
								}
								if(StringUtils.isBlank(p.getNumeroDiasTrabajadosPorSemanaOPeriodo())){
									errors.add(18);
								}
							}
						}
						for(Cotizacion c: t.getDatosCotizacion()){
							if(StringUtils.isBlank(c.getAno())){
								errors.add(19);
							}
							if(StringUtils.isBlank(c.getMes())){
								errors.add(20);
							}
							if(StringUtils.isBlank(c.getNumDiasCotizados())){
								errors.add(21);
							}
							if(StringUtils.isBlank(c.getBaseCotizacionDesempleo())){
								errors.add(22);
							}
						}
						if(t.getDatosVacacionesCotizadas()!=null){
							if(StringUtils.isBlank(t.getDatosVacacionesCotizadas().getNumDiasCotizados())){
								errors.add(23);
							}
							if(StringUtils.isBlank(t.getDatosVacacionesCotizadas().getBaseCotizacionDesempleo())){
								errors.add(24);
							}
						}
					}
				}
			}
		}
		if (!errors.isEmpty()) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			String errorMsg = null;
			for(Integer i: errors){
				errorMsg = AonPayroll.getMessage(locale, "aon_payroll_certificate_error_" + i);
				AonUtil.addErrorMessage(errorMsg);
			}
			throw new AbortProcessingException(errorMsg);
		}
		
	}
	
	private void validateXmlPattern(File xml) {
		final String SCHEMA = "enterpriseCertificate.xsd";
		try {
			// Create a schema factory
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			URL[] urls = Classpath.search(cl, "META-INF/", SCHEMA);
			// Create a validator
			Validator validator = sf.newSchema(urls[0]).newValidator();
			// Create a streamSource based on input XML
			StreamSource source = new StreamSource(xml);
			// Invoke the validation
			validator.validate(source);
		} catch (Exception e) {
			String msg = "error de formato al generar el xml";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg,e);
			// NADA
		}
	}
	
	private CuentaCotizacion createCuentaCotizacionRecord( IRemesaCertificadoEmpresa remesa, List<IRemesaCertificadoEmpresaDetalle> listaDetalle) throws PayrollException {
		CuentaCotizacion cuentaCotizacion = new CuentaCotizacion();
		Representante representante = new Representante();
		representante.setCifNif(remesa.getEmpresa().getRepresentanteDocument());
		representante.setNombre(parseNombre(remesa.getEmpresa().getNombreRepresentante()));
		representante.setApellido1(parseApellido(remesa.getEmpresa().getApellido1Representante()));
		if(!StringUtils.isBlank(remesa.getEmpresa().getApellido2Representante())){
			representante.setApellido2(parseApellido(remesa.getEmpresa().getApellido2Representante()));
		}
		if(!StringUtils.isBlank(remesa.getEmpresa().getCargo())){
			representante.setCargo(parseCargo(remesa.getEmpresa().getCargo()));
		}
		cuentaCotizacion.setRepresentante(representante);
		Empresa empresa = new Empresa();
		empresa.setCcc(parseToLength(remesa.getNumeroCcc(),15));
		
		empresa.setCifNif(remesa.getEmpresa().getRegistry().getDocument().getValue());
		cuentaCotizacion.setEmpresa(empresa);

		List<Trabajador> listaTrabajadores = new ArrayList<Trabajador>();
		for(IRemesaCertificadoEmpresaDetalle detalle: listaDetalle){
			listaTrabajadores.add(createTrabajadorRecord(detalle));
		}
		cuentaCotizacion.setListaTrabajadores(listaTrabajadores);
		return cuentaCotizacion;
	}

	private Trabajador createTrabajadorRecord(IRemesaCertificadoEmpresaDetalle detalle) {
		List<ITrabajo> trabajos = null;
		try {
			trabajos = getEmpleadoDAO().getTrabajos(detalle.getEmpleado());
		} catch (PayrollException e) {
			// NADA
		}
		
		if(trabajos==null || trabajos.size()==0){
			return null;
		}
			
		Trabajador trabajador = new Trabajador();
		trabajador.setDniNie(detalle.getEmpleado().getPersona().getRegistry().getDocument().getValue());
		trabajador.setNombre(parseNombre(detalle.getEmpleado().getPersona().getName()));
		trabajador.setApellido1(parseApellido(detalle.getEmpleado().getPersona().getSurname()));
		if(!StringUtils.isBlank(detalle.getEmpleado().getPersona().getLastName())){
			trabajador.setApellido2(parseApellido(detalle.getEmpleado().getPersona().getLastName()));
		}
		trabajador.setNumSs(detalle.getEmpleado().getPersona().getNumSS());
		trabajador.setGrupoCotizacion(parseToLength(trabajos.get(0).getBaseCotizacion().getCdg(),2));
		trabajador.setTipoContrato(parseToLength(trabajos.get(0).getContratoTc2().getCdg(),3));
		if(trabajos.get(0).getFechaFinCont()!=null && trabajos.get(0).getFechaInicioCont()!=null){
			trabajador.setDuracionContrato(parseToLength(differenceBetweenDates(trabajos.get(0).getFechaFinCont(),trabajos.get(0).getFechaInicioCont()),5));
		}
//		trabajador.setIndicadorDuracionContrato();
		trabajador.setCodProfesion(parseToLength(trabajos.get(0).getCno(),7,false));
//		trabajador.setCargoPublicoSindical();
//		trabajador.setPorcentualDedicacion();
		trabajador.setFechaAltaEmpresa(parseFecha(detalle.getEmpleado().getFechaInicio()));
		
		trabajador.setCodCausaSuspension(parseToLength(detalle.getCausaSuspension().getValue(),2));
//		trabajador.setFechaSuspensionExtincion(detalle.getEmpleado().getFechaFin().toString());
		trabajador.setFechaSuspensionExtincion(parseFecha(detalle.getFechaBaja()));
//		trabajador.setFechaFinSuspension();
//		trabajador.setEre();
//		trabajador.setPorcentualReduccionERE();
//		trabajador.setPorcentualReduccionOTROS();
//		trabajador.setCodCausaPorcentReduccion();
//		trabajador.setFechaDesdePeriodoSalarios();
//		trabajador.setFechaHastaPeriodoSalarios();
		trabajador.setDiasSalarioTramitacion("00000");

		/*
		 * NODOS
		 */
		trabajador.setDistribucionJornada(createDistribucionJornadaRecord(detalle));		
		List<Cotizacion> listaDatosCotizacion = new ArrayList<Cotizacion>();
//		for(IRemesaCertificadoEmpresaDetalle detalle: listaDetalle){
		listaDatosCotizacion.addAll(createDatosCotizacionRecord(detalle));
//		}
		trabajador.setDatosCotizacion(listaDatosCotizacion);
		trabajador.setDatosVacacionesCotizadas(createDatosVacacionesCotizadasRecord(detalle.getEmpleado()));
		
		return trabajador;
	}

	private DistribucionJornada createDistribucionJornadaRecord(IRemesaCertificadoEmpresaDetalle detalle) {
		List<ITrabajo> trabajosTP = null;
		try {
			trabajosTP = getEmpleadoDAO().getTrabajosTP(detalle.getEmpleado());
		} catch (PayrollException e) {
			// NADA
		}
		if(trabajosTP!=null && trabajosTP.size()>0){
			List<Periodo> listaPeriodos = new ArrayList<Periodo>();
			for(ITrabajo t: trabajosTP){
				Periodo periodo = new Periodo();
				periodo.setTipoDistribucion(parseTipoDistribucion(t.getTipoTP()));
				periodo.setFechaInicioPeriodo(parseFecha(t.getFechaInicioCont()));
				periodo.setFechaFinPeriodo(parseFecha(t.getFechaFinCont()));
				periodo.setNumeroDiasTrabajadosPorSemanaOPeriodo(parseToLength(t.getDiasTP(),5));
				listaPeriodos.add(periodo);
			}
			DistribucionJornada jornada = new DistribucionJornada();
			jornada.setListaPeriodos(listaPeriodos);
			return jornada;
		}
		return null;
	}
	
	private List<Cotizacion> createDatosCotizacionRecord(
			IRemesaCertificadoEmpresaDetalle detalle) {
		INomina nomina = null;
		INominaDiferencia nominaDiferencia = null;
		try {
	        Double totalBaseCg=0.0;
	        Double totalBaseDesempleo=0.0;
	        Double baseAcc = 0.0;
	        Integer totalDias = 0;
			
			Calendar calInicio = new GregorianCalendar();
			Calendar calFin = new GregorianCalendar();
			calInicio.setTime(detalle.getEmpleado().getFechaInicio());
			calFin.setTime(detalle.getEmpleado().getFechaFin());
			calFin.set(Calendar.DAY_OF_MONTH, calFin.getActualMaximum(Calendar.DAY_OF_MONTH));
			
			List<Cotizacion> cotizacionList = new ArrayList<Cotizacion>();
			while((calInicio.before(calFin) || calInicio.equals(calFin)) && totalDias < 180 && existNomina(detalle.getEmpleado(),calFin)){
				NominaParams params = new NominaParams();
				params.setEmpleado(detalle.getEmpleado());
				params.setTipo(TipoNomina.NORMAL);
				params.setMes(calFin.get(Calendar.MONTH)+1);
				params.setYear(calFin.get(Calendar.YEAR));
				
				nomina = getNominaDAO().getNomina(params);
				totalBaseCg = nomina.getBaseCgPts();
		        totalBaseDesempleo = nomina.getBasePerdes();
		        baseAcc = nomina.getBaseAccPts();
				
		        nominaDiferencia = getNominaDAO().getNominaDiferencia(params);
				if(nominaDiferencia!=null){
					totalBaseCg += nominaDiferencia.getBaseCgPts();
					totalBaseDesempleo += nominaDiferencia.getBasePerdes();
					baseAcc += nominaDiferencia.getBaseAccPts();
				}
				
				if(nomina.getBaseHorasExtrasEstructurales()==0 && nomina.getBaseHorasExtrasNoEstructurales()==0){
					totalBaseDesempleo = baseAcc;
				} else if(!detalle.getEmpleado().getEmpresa().getDivisa().getCdg().equals("2")){
					totalBaseDesempleo = convertMoney(totalBaseDesempleo, detalle.getEmpleado().getEmpresa().getDivisa());
				}
				
//				totalBaseCg = nomina.getBaseCgPts();
//		        totalBaseDesempleo = nomina.getBasePerdes();
				totalDias += nomina.getDiasNomina();
				calFin.add(Calendar.DATE, -calFin.get(Calendar.DAY_OF_MONTH));
				Cotizacion cotizacion = new Cotizacion();
				cotizacion.setAno(nomina.getYear().toString());
				cotizacion.setMes(parseToLength(nomina.getMes(),2));
				cotizacion.setNumDiasCotizados(parseToLength(nomina.getDiasNomina(),3));
				cotizacion.setBaseCotizacionContingenciasComunes(parseToLength(totalBaseCg,9));
				cotizacion.setBaseCotizacionDesempleo(parseToLength(totalBaseDesempleo,9));
				cotizacion.setObservaciones(null);
				cotizacionList.add(cotizacion);
			}
			return cotizacionList;
		} catch (PayrollException e) {
			// NADA
		}
		return null;
	}

	private boolean existNomina(IEmpleado empleado, Calendar calFin) throws PayrollException {
		NominaParams params = new NominaParams();
		params.setEmpleado(empleado);
		params.setTipo(TipoNomina.NORMAL);
		params.setMes(calFin.get(Calendar.MONTH)+1);
		params.setYear(calFin.get(Calendar.YEAR));
		if(getNominaDAO().getNomina(params)!=null){
			return true;
		} 
		return false;
	}

	private Vacaciones createDatosVacacionesCotizadasRecord(IEmpleado empleado) {
		try {
			IFiniquito finiquito = getNominaDAO().getFiniquito(empleado);
			if(finiquito!=null){
				Vacaciones vacaciones = new Vacaciones();
				vacaciones.setNumDiasCotizados(parseToLength(finiquito.getDiasVacaciones(),3));
				vacaciones.setBaseCotizacionContingenciasComunes(parseToLength(finiquito.getBaseContingenciasGenerales(),9));
				vacaciones.setBaseCotizacionDesempleo(parseToLength(finiquito.getBaseAccidentesTrabajo(),9));
				vacaciones.setObservaciones(null);
				IFiniquitoDiferencia finiquitodf = getNominaDAO().getFiniquitoDiferencia(empleado);
				if(finiquitodf!=null){
					vacaciones.setBaseCotizacionDesempleo(vacaciones.getBaseCotizacionDesempleo()+finiquitodf.getBaseAccidentesTrabajo().intValue());
					vacaciones.setBaseCotizacionContingenciasComunes(vacaciones.getBaseCotizacionContingenciasComunes()+finiquitodf.getBaseContingenciasGenerales().intValue());
				}
				return vacaciones;
			}
		} catch (PayrollException e) {
			// NADA
		}
		return null;
	}
	
	/*
	 * CONVERSIONES
	 */
	private Double convertMoney(Double totalBaseDesempleo, IDivisa iDivisa) {
		
		return totalBaseDesempleo;
	}
	
	private Integer differenceBetweenDates(Date from, Date to) {
        long difDays = 0;
        if(from.before(to)) {
              difDays = ((to.getTime() - from.getTime()) / (1000 * 60 * 60 * 24)) + 1;
        }
        return (int)difDays;
	}

	private String parseNombre(String nombre){
		if(nombre.length()>15){
			return nombre.substring(0, 15);
		}
		return nombre;
	}
			
	private String parseApellido(String apellido){
		if(apellido.length()>20){
			return apellido.substring(0, 20);
		}
		return apellido;
	}
	
	private String parseCargo(String cargo){
		if(cargo.length()>40){
			return cargo.substring(0, 40);
		}
		return cargo;
	}
	
	private String parseFecha(Date date) {
		if(date!=null){
			Calendar cal = new GregorianCalendar();
			cal.setTime(date);
			String d = String.valueOf(cal.get(Calendar.YEAR));
			d += parseToLength(cal.get(Calendar.MONTH)+1,2);
			d += parseToLength(cal.get(Calendar.DAY_OF_MONTH),2);
			return d;
		}
		return null;
	}
	
	private String parseTipoDistribucion(TipoTiempoParcial tipoTP) {
		if(tipoTP!=null){
			return tipoTP.getValue();
		}
		return null;
	}
	
	/**
	 * complete a string with cero digits up to digits indicated in length.
	 * direction indicates with a true value the completion should be on the left hand. 
	 * 
	 * @param value
	 * @param length
	 * @param direction
	 * @return
	 */
	private String parseToLength(String value, Integer length, boolean direction) {
		if(value!=null){
			while(value.length()<length){
				if(direction){
					value = "0".concat(value);
				} else {
					value = value.concat("0");
				}
			}
			return value;
		}
		return null;
	}
	private String parseToLength(String value, Integer length) {
		return parseToLength(value, length, true);
	}
	private String parseToLength(Integer value, Integer length) {
		return parseToLength(String.valueOf(value), length, true);
	}
	private String parseToLength(Double value, Integer length) {
		value =(CommonUtil.round(value)*100);
		return parseToLength(String.valueOf(value.intValue()), length, true);
	}
		
}

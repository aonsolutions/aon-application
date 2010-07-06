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
			DOMSource domSource = new DOMSource(xmldoc);
			StreamResult streamResult = new StreamResult(out);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer serializer = tf.newTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty(INDENT_AMOUNT_PROPERTY, INDENT_AMOUNT_VALUE);
			serializer.transform(domSource, streamResult);
			validateXml(file);
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
	
	private void validateXml(File xml) {
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
			AonUtil.addErrorMessage("error de formato al generar el xml");
			throw new AbortProcessingException();
			// NADA
		}
	}
	
	private CuentaCotizacion createCuentaCotizacionRecord( IRemesaCertificadoEmpresa remesa, List<IRemesaCertificadoEmpresaDetalle> listaDetalle) throws PayrollException {
		CuentaCotizacion cuentaCotizacion = new CuentaCotizacion();
		Representante representante = new Representante();
		representante.setCifNif(remesa.getEmpresa().getRepresentanteDocument());
		representante.setNombre(remesa.getEmpresa().getNombreRepresentante());
		representante.setApellido1(remesa.getEmpresa().getApellido1Representante());
		if(!StringUtils.isBlank(remesa.getEmpresa().getApellido2Representante())){
			representante.setApellido2(remesa.getEmpresa().getApellido2Representante());
		}
		if(!StringUtils.isBlank(remesa.getEmpresa().getCargo())){
			representante.setCargo(remesa.getEmpresa().getCargo());
		}
		cuentaCotizacion.setRepresentante(representante);
		Empresa empresa = new Empresa();
		empresa.setCcc(parse15Digit(remesa.getNumeroCcc()));
		
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
		trabajador.setNombre(detalle.getEmpleado().getPersona().getName());
		trabajador.setApellido1(detalle.getEmpleado().getPersona().getSurname());
		trabajador.setApellido2(detalle.getEmpleado().getPersona().getLastName());
		trabajador.setNumSs(detalle.getEmpleado().getPersona().getNumSS());
		trabajador.setGrupoCotizacion(trabajos.get(0).getBaseCotizacion().getCdg());
		trabajador.setTipoContrato(parse3Digit(trabajos.get(0).getContratoTc2().getCdg()));
		if(trabajos.get(0).getFechaFinCont()!=null && trabajos.get(0).getFechaInicioCont()!=null){
			trabajador.setDuracionContrato(parse5Digit(differenceBetweenDates(trabajos.get(0).getFechaFinCont(),trabajos.get(0).getFechaInicioCont())));
		}
//		trabajador.setIndicadorDuracionContrato();
		trabajador.setCodProfesion(parse7DigitRight(trabajos.get(0).getCno()));
//		trabajador.setCargoPublicoSindical();
//		trabajador.setPorcentualDedicacion();
		trabajador.setFechaAltaEmpresa(parseFecha(detalle.getEmpleado().getFechaInicio()));
		
		trabajador.setCodCausaSuspension(parse2Digit(detalle.getCausaSuspension().getValue()));
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
				try{
					Periodo periodo = new Periodo();
					periodo.setTipoDistribucion(t.getTipoTP().getValue());
					periodo.setFechaInicioPeriodo(parseFecha(t.getFecini()));
					periodo.setFechaFinPeriodo(parseFecha(t.getFecfin()));
					periodo.setNumeroDiasTrabajadosPorSemanaOPeriodo(parse5Digit(t.getDiasTP()));
					listaPeriodos.add(periodo);
				} catch(NullPointerException e){
					// NADA, no tener en cuenta el periodo
				}
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
				nominaDiferencia = getNominaDAO().getNominaDiferencia(params);
				Double baseAcc = nomina.getBaseAccPts();
				if(nominaDiferencia!=null){
					totalBaseCg += nominaDiferencia.getBaseCgPts();
					totalBaseDesempleo += nominaDiferencia.getBasePerdes();
					baseAcc = nominaDiferencia.getBaseAccPts();
				}
				
				if(nomina.getBaseHorasExtrasEstructurales()==0 && nomina.getBaseHorasExtrasNoEstructurales()==0){
					totalBaseDesempleo = baseAcc;
				} else if(detalle.getEmpleado().getEmpresa().getDivisa().getCdg().equals("2")){
					totalBaseDesempleo = convertMoney(totalBaseDesempleo, detalle.getEmpleado().getEmpresa().getDivisa());
				}
				
				totalBaseCg += nomina.getBaseCgPts();
		        totalBaseDesempleo += nomina.getBasePerdes();
				totalDias += nomina.getDiasNomina();
				calFin.add(Calendar.DATE, -calFin.get(Calendar.DAY_OF_MONTH));
				Cotizacion cotizacion = new Cotizacion();
				cotizacion.setAno(nomina.getYear().toString());
				cotizacion.setMes(parse2Digit(nomina.getMes()));
				cotizacion.setNumDiasCotizados(parse3Digit(nomina.getDiasNomina()));
				cotizacion.setBaseCotizacionContingenciasComunes(parse9Digit(totalBaseCg));
				cotizacion.setBaseCotizacionDesempleo(parse9Digit(totalBaseDesempleo));
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
				vacaciones.setNumDiasCotizados(parse3Digit(finiquito.getDiasVacaciones()));
				vacaciones.setBaseCotizacionDesempleo(parse9Digit(finiquito.getBaseAccidentesTrabajo()));
				vacaciones.setBaseCotizacionContingenciasComunes(parse9Digit(finiquito.getBaseContingenciasGenerales()));
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
	
	private Integer differenceBetweenDates(Date date1, Date date2){
		GregorianCalendar initDate = new GregorianCalendar();
		GregorianCalendar endDate = new GregorianCalendar();
		initDate.setTime(date1);
		endDate.setTime(date2);
		int days1 = 0;
		int days2 = 0;
		int maxYear = Math.max(initDate.get(Calendar.YEAR), endDate.get(Calendar.YEAR));
		GregorianCalendar gctmp = (GregorianCalendar) initDate.clone();
		for (int f = gctmp.get(Calendar.YEAR); f < maxYear; f++) {
			days1 += gctmp.getActualMaximum(Calendar.DAY_OF_YEAR);
			gctmp.add(Calendar.YEAR, 1);
		}
		gctmp = (GregorianCalendar) endDate.clone();
		for (int f = gctmp.get(Calendar.YEAR); f < maxYear; f++) {
			days2 += gctmp.getActualMaximum(Calendar.DAY_OF_YEAR);
			gctmp.add(Calendar.YEAR, 1);
		}
		days1 += initDate.get(Calendar.DAY_OF_YEAR) - 1;
		days2 += endDate.get(Calendar.DAY_OF_YEAR) - 1;
		return (days2>days1)?(days2-days1):(days1-days2);
	}
	
	private String parseFecha(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		String d = String.valueOf(cal.get(Calendar.YEAR));
		d += parse2Digit(cal.get(Calendar.MONTH)+1);
		d += parse2Digit(cal.get(Calendar.DAY_OF_MONTH));
		return d;
	}
	
	private String parse2Digit(Integer i) {
		String m = String.valueOf(i);
		return parse2Digit(m);
	}
	
	private String parse2Digit(String s) {
		while(s.length()<2){
			s = "0".concat(s);
		}
		return s;
	}
	
	private String parse3Digit(Integer i) {
		String d = String.valueOf(i);
		return parse3Digit(d);
	}

	private String parse3Digit(String s) {
		while(s.length()<3){
			s = "0".concat(s);
		}
		return s;
	}
	
	private String parse5Digit(Integer i) {
		String d = String.valueOf(i);
		while(d.length()<5){
			d = "0".concat(d);
		}
		return d;
	}
	
	private String parse7DigitRight(String s) {
		while(s.length()<7){
			s = s.concat("0");
		}
		return s;
	}
	private String parse7Digit(String s) {
		while(s.length()<7){
			s = "0".concat(s);
		}
		return s;
	}
	
	private String parse9Digit(Double d) {
		d =(CommonUtil.round(d)*100);
		String b = String.valueOf(d.intValue());
		while(b.length()<9){
			b = "0".concat(b);
		}
		return b;
	}
	
	private String parse15Digit(String s) {
		while(s.length()<15){
			s = "0".concat(s);
		}
		return s;
	}

	
}

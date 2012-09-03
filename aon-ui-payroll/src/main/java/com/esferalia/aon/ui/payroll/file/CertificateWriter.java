package com.esferalia.aon.ui.payroll.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.Classpath;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryDirStaff;
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
import com.esferalia.aon.payroll.Certifica2Batch;
import com.esferalia.aon.payroll.Certifica2BatchData;
import com.esferalia.aon.payroll.Certifica2BatchDetail;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractWorkingDay;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.Period;

public class CertificateWriter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CertificateWriter.class.getName());
	private static final String DATE_FORMAT = "yyyyMMdd";
	private static final String FINAL_END_DATE = "99991231";
	private static final String SHARE_HOLDER = "Socio";
	private static final String REPRESENTATIVE = "Administrativo";
	private static final String REPRESENTATIVE_LABOR = "Representante Laboral";
	private static final String DIRECTOR = "Apoderado";
	private static final String IRREGULAR_VALUE = "2";
	private static final String REGULAR_VALUE = "1";
	
	private Certificate certificate;
	
	public Certificate getCertificate() {
		return certificate;
	}
	
	public void setCertificate(Certificate certificate) {
		this.certificate = certificate;
	}
	
	public FileOutput createCertificate(Certifica2Batch batch, List<Certifica2BatchDetail> batchDetail) throws ManagerBeanException {
		final String INDENT_AMOUNT_VALUE = "4";
		final String INDENT_AMOUNT_PROPERTY = "{http://xml.apache.org/xslt}indent-amount";
		try {
			setCertificate(new Certificate(batch.getEnterprise().getRegistry().getDocument(), batch.getDate()));
			List<CuentaCotizacion> list = new ArrayList<CuentaCotizacion>();
			for(String ccc: getCccList(batchDetail)) {
				list.add(createCuentaCotizacionRecord(ccc, batch, batchDetail));
			}
			getCertificate().setCuentaCotizacion(list);
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
				AonUtil.addErrorMessage("Fichero generado con errores.");
				// No se lanza excepción, que vaya a la última página.
			} else {
				validateXmlPattern(file);
			}
			return output;
		} catch (IOException e) {
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
				String errorMsg = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.messages").getString("aon_payroll_certificate_error_" + i);
				Element error = xmldoc.createElement(ERROR);
				error.appendChild(xmldoc.createTextNode(errorMsg));
				errores.appendChild(error);
			}
			return errores;
		} else {
			return null;
		}
	}
	
	private List<String> getCccList(List<Certifica2BatchDetail> batchDetail) {
		List<String> list = new ArrayList<String>();
		String ccc;
		for(Certifica2BatchDetail detail: batchDetail){
			ccc = detail.getContract().getEnterpriseCCC().getCcc();
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
	
	private CuentaCotizacion createCuentaCotizacionRecord(String ccc, Certifica2Batch batch, List<Certifica2BatchDetail> listaDetalle) {
		CuentaCotizacion cuentaCotizacion = new CuentaCotizacion();
		Representante representante = new Representante();
		RegistryDirStaff rds = null;
		rds = getRegistryDirStaff(batch.getEnterprise());
		if(rds!=null){
			String name[] = StringUtils.split(rds.getName(),' ');
			String nombre = name.length>=1?name[0]:"";
			String ap1 = name.length>=2?name[1]:"";
			String ap2 = name.length>=3?name[2]:"";
			representante.setCifNif(rds.getDocument());
			representante.setNombre(parseMaxLength(nombre, 15));
			representante.setApellido1(parseMaxLength(ap1, 20));
			representante.setApellido2(parseMaxLength(ap2, 20));
			if (rds.isShareHolder()){
				representante.setCargo(parseMaxLength(SHARE_HOLDER, 40));
			} else if (rds.isRepresentative()){
				representante.setCargo(parseMaxLength(REPRESENTATIVE, 40));
			} else if (rds.isRepresentativeLabor()){
				representante.setCargo(parseMaxLength(REPRESENTATIVE_LABOR, 40));
			} else if (rds.isDirector()){
				representante.setCargo(parseMaxLength(DIRECTOR, 40));
			}
		}
		cuentaCotizacion.setRepresentante(representante);
		Empresa empresa = new Empresa();
		empresa.setCcc(parseToLength(ccc, 15));
		empresa.setCifNif(batch.getEnterprise().getRegistry().getDocument());
		cuentaCotizacion.setEmpresa(empresa);
		List<Trabajador> listaTrabajadores = new ArrayList<Trabajador>();
		for(Certifica2BatchDetail detalle: listaDetalle) {
			if(detalle.getContract().getEnterpriseCCC().getCcc().equals(ccc)){
				listaTrabajadores.add(createTrabajadorRecord(detalle));
			}
		}
		cuentaCotizacion.setListaTrabajadores(listaTrabajadores);
		return cuentaCotizacion;
	}
	
	private Trabajador createTrabajadorRecord(Certifica2BatchDetail detail) {
		Trabajador trabajador = new Trabajador();
		trabajador.setDniNie(detail.getDocument());
		trabajador.setNombre(parseMaxLength(detail.getName(), 15));
		trabajador.setApellido1(parseMaxLength(detail.getFirstSurname(), 20));
		if(!StringUtils.isBlank(detail.getSecondSurname())) {
			trabajador.setApellido2(parseMaxLength(detail.getSecondSurname(), 20));
		}
		trabajador.setNumSs(detail.getSsNumber());
		trabajador.setGrupoCotizacion(detail.getQuoteGroup());
		trabajador.setTipoContrato(parseToLength(detail.getContractType(), 3));
		if(detail.getContract().getEndDate() != null && detail.getContract().getStartDate() != null) {
			trabajador.setDuracionContrato(parseToLength(detail.getContractDuration(), 5));
		}
		// trabajador.setIndicadorDuracionContrato();
		trabajador.setCodProfesion(parseToLength(detail.getOccupationCode(), 7, false));
		// trabajador.setCargoPublicoSindical();
		// trabajador.setPorcentualDedicacion();
		trabajador.setFechaAltaEmpresa(parseFecha(detail.getEnterpriseStartDate()));
		trabajador.setCodCausaSuspension(parseToLength(detail.getSuspensionCause().getValue(), 2));
		trabajador.setFechaSuspensionExtincion(parseFecha(detail.getExpireDate()));
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
		if(!isFulltimeContract(detail)){
			trabajador.setDistribucionJornada(createDistribucionJornadaRecord(detail));
		}
		List<Cotizacion> listaDatosCotizacion = new ArrayList<Cotizacion>();
		listaDatosCotizacion.addAll(createDatosCotizacionRecord(detail));
		trabajador.setDatosCotizacion(listaDatosCotizacion);
		trabajador.setDatosVacacionesCotizadas(createDatosVacacionesCotizadasRecord(detail.getContract()));
		return trabajador;
	}
	
	private DistribucionJornada createDistribucionJornadaRecord(Certifica2BatchDetail detail) {
		DistribucionJornada jornada = null;
		Periodo periodo = null;
		List<Periodo> listaPeriodos = new ArrayList<Periodo>();
		List<Period> existingPeriods = getPeriodList(detail.getContract());
		if(existingPeriods!=null){
			for (Period p : getPeriodList(detail.getContract())) {
				String diasTp = getContractDataExpression(detail.getContract(), p, ContextVariable.CONTRACT_DAYS);
				String diasSemanaTp = getContractDataExpression(detail.getContract(), p, ContextVariable.WEEK_DAYS);
				if(diasTp!=null || diasSemanaTp!=null){
					if(isIrregular(detail.getContract(), p)){
						addPeriod(IRREGULAR_VALUE, p, diasTp, listaPeriodos, periodo);
					} else {
						addPeriod(REGULAR_VALUE, p, diasSemanaTp, listaPeriodos, periodo);
					}
				}
			}
		}
		if(!listaPeriodos.isEmpty()){
			jornada = new DistribucionJornada();
			jornada.setListaPeriodos(listaPeriodos);
		}
		return jornada;
	}
	
	private void addPeriod(String tipoTp, Period p, String diasTp, List<Periodo> listaPeriodos, Periodo periodo) {
		DateFormat dateYYYYMMDD = new SimpleDateFormat(DATE_FORMAT);
		Date startDate = p.getStart();
		Date endDate = p.getEnd();
		if(listaPeriodos.isEmpty()){
			periodo = new Periodo();
			periodo.setFechaInicioPeriodo(parseFecha(startDate));
			periodo.setFechaFinPeriodo(parseFecha(endDate));
			periodo.setTipoDistribucion(tipoTp);
			periodo.setNumeroDiasTrabajadosPorSemanaOPeriodo(parseToLength(diasTp, 5));
			listaPeriodos.add(periodo);
		} else {
			Periodo tmp = listaPeriodos.get(listaPeriodos.size()-1);
			try {
				if(tmp.getTipoDistribucion().equals(tipoTp) 
						&& tmp.getNumeroDiasTrabajadosPorSemanaOPeriodo().equals(parseToLength(diasTp, 5))
						&& differenceBetweenDates(dateYYYYMMDD.parse(tmp.getFechaFinPeriodo()),startDate).equals(2)){
					tmp.setFechaFinPeriodo(parseFecha(endDate));
				} else {
					periodo = new Periodo();
					periodo.setFechaInicioPeriodo(parseFecha(startDate));
					periodo.setFechaFinPeriodo(parseFecha(endDate));
					periodo.setTipoDistribucion(tipoTp);
					periodo.setNumeroDiasTrabajadosPorSemanaOPeriodo(parseToLength(diasTp, 5));
					listaPeriodos.add(periodo);
				}
			} catch (ParseException e) {
				String msg = "Error al obtener la fecha de inicio del periodo ("+e.getMessage()+")";
				LOGGER.warn(msg);
			}
		}
	}

	private String getContractDataExpression(Contract contract, Period p,
			ContextVariable workedDays) {
		ContractData cd = getContractDataMap(contract, p).get(workedDays.getName());
		return (cd==null)?null:cd.getExpression();
	}

	private boolean isIrregular(Contract contract, Period p) {
		ContractData cd = getContractDataMap(contract, p).get(ContextVariable.IRREGULAR.getName());
		return (cd!=null && new Boolean(cd.getExpression()));
	}

	private String getContractDataExpression(ContractData cd, ContextVariable cv) {
		List<ContractData> list = getContractDataList(cd.getContract());
		for (ContractData c : list) {
			if(c.getName()==cv.getName()
					&& (c.getStartDate().after(cd.getStartDate()) || c.getStartDate().equals(cd.getStartDate()))
					&& (c.getEndDate().before(cd.getEndDate()) || c.getEndDate().equals(cd.getEndDate()))){
				return c.getExpression();
			}
		}
		return null;
	}
	
	private List<Period> getPeriodList(Contract contract) {
		List<Period> list = null;
		for(ContractData cd: getContractDataList(contract)){
			if(cd.getName().equals(ContextVariable.WEEK_DAYS.getName()) || cd.getName().equals(ContextVariable.CONTRACT_DAYS.getName())){
				Period period = new Period(cd.getStartDate(), cd.getEndDate());
				if(list==null){
					list = new LinkedList<Period>();
					list.add(period);
				} else {
					Period tmp = list.get(list.size()-1);
					if(!tmp.getStart().equals(period.getStart()) || !tmp.getEnd().equals(period.getEnd())){
						list.add(period);
					}
				}
			}
		}
		return list;
	}

	private List<Cotizacion> createDatosCotizacionRecord(Certifica2BatchDetail detail) {
		List<Cotizacion> cotizacionList = null;
		cotizacionList = new ArrayList<Cotizacion>();
		for(ITransferObject to: getBatchData(detail)){
			Certifica2BatchData data = (Certifica2BatchData) to;
			Cotizacion cotizacion = new Cotizacion();
			cotizacion.setAno(data.getYear().toString());
			cotizacion.setMes(parseToLength(data.getMonth().toString(), 2));
			cotizacion.setNumDiasCotizados(parseToLength(data.getContributionDays(), 3));
			cotizacion.setBaseCotizacionContingenciasComunes(parseToLength(data.getCgcContributionBase(), 9));
			cotizacion.setBaseCotizacionDesempleo(parseToLength(data.getUnemploymentContributionBase(), 9));
			cotizacion.setObservaciones(data.getComments());
			cotizacionList.add(cotizacion);
		}
		return cotizacionList;
	}
	
	private Vacaciones createDatosVacacionesCotizadasRecord(Contract contract) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Salary.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_CONTRACT_ID), contract.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_TYPE), SalaryType.SETTLE);
			// un contracto solo puede tener un finiquito, se asume el primero de la lista
			List<ITransferObject> list = bean.getList(criteria);
			ContractData cd = getContractDataMap(contract).get(ContextVariable.NO_HOLIDAYS.getName());
			String noHolidays = null;
			if(cd != null){
				noHolidays = cd.getExpression();
			}
			if(!list.isEmpty() && noHolidays!=null && noHolidays!="0"){
				Vacaciones vacaciones = new Vacaciones();
				Salary salary = (Salary) list.get(0);
				Double baseContingenciaGenerales = salary.getCommonBase();
				Double baseAccidentesTrabajo = salary.getProfessionalBase();
// TODO contemplar el caso de que exista atraso de finiquito 
//				IFiniquitoDiferencia finiquitodf = getNominaDAO().getFiniquitoDiferencia(empleado);
//				if(finiquitodf != null && finiquitodf.getDiasVacaciones() != 0 && finiquitodf.getImporteVacaciones() != 0) {
//					baseAccidentesTrabajo += finiquitodf.getBaseAccidentesTrabajo();
//					baseContingenciaGenerales += finiquitodf.getBaseContingenciasGenerales();
//				}
				vacaciones.setNumDiasCotizados(parseToLength(noHolidays, 3));
				vacaciones.setBaseCotizacionContingenciasComunes(parseToLength(baseContingenciaGenerales, 9));
				vacaciones.setBaseCotizacionDesempleo(parseToLength(baseAccidentesTrabajo, 9));
				vacaciones.setObservaciones(null);
				return vacaciones;
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener el finiquito de "+contract.getPerson().getFullName();
			AonUtil.addErrorMessage(msg);
		}
		return null;
	}
	
	/*
	 * AUXILIARES
	 */
	private RegistryDirStaff getRegistryDirStaff(Enterprise enterprise) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(RegistryDirStaff.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REGISTRY_ID), enterprise.getId());
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE), new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE));
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
			
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				return (RegistryDirStaff) list.get(0);
			}
		} catch (ManagerBeanException e) {
			// NADA, que siga con la generacion del fichero
		}
		return null;
	}
	
	private List<ITransferObject> getBatchData(Certifica2BatchDetail detail) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DATA_CERTIFICA2BATCH_DETAIL_ID), detail.getId());
			criteria.addOrder(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DATA_YEAR), false);
			criteria.addOrder(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DATA_MONTH), false);
			return bean.getList(criteria);
		} catch (ManagerBeanException e) {
			// NADA, que siga con la generacion del fichero
		}
		return null;
	}
	
	private ContractWorkingDay getContractWorkingTime(String tc2) {
		if(tc2.startsWith("1") || tc2.startsWith("4")){ // completa
			return ContractWorkingDay.FULL_TIME;
		} else if(tc2.startsWith("2") || tc2.startsWith("5")){ // parcial
			return ContractWorkingDay.PART_TIME;
		} else if(tc2.startsWith("3")){ // discontinua
			return null;
		} else if(tc2.startsWith("9")){ // otros
			return null;
		}
		return null;
	}
	
	private boolean isFulltimeContract(Certifica2BatchDetail detalle) {
		ContractData fullTime = getContractDataMap(detalle.getContract()).get(ContextVariable.FULL_TIME.getName());
		if(fullTime==null || new Boolean(fullTime.getName())){
			if(detalle.getContractType().startsWith("1") || detalle.getContractType().startsWith("4")){ 
				return true;
			} 
		}
		return false;
	}
	
	protected Map<String, ContractData> getContractDataMap(Contract contract) {
		Map<String, ContractData> map = new HashMap<String, ContractData>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			for(ITransferObject to: bean.getList(criteria)){
				ContractData data = (ContractData) to;
				if(data.getExpression()!=null){
					map.put(data.getName(), data);
				}
			}
		} catch (ManagerBeanException e) {
			// NADA, que siga generando el fichero
		}
		return map;
	}
	
	protected Map<String, ContractData> getContractDataMap(Contract contract, Period period) {
		Map<String, ContractData> map = new HashMap<String, ContractData>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), period.getStart());
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), period.getEnd());
			for(ITransferObject to: bean.getList(criteria)){
				ContractData data = (ContractData) to;
				if(data.getExpression()!=null){
					map.put(data.getName(), data);
				}
			}
		} catch (ManagerBeanException e) {
			// NADA, que siga generando el fichero
		}
		return map;
	}
	
	protected List<ContractData> getContractDataList(Contract contract) {
		List<ContractData> list = new LinkedList<ContractData>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE));
			for(ITransferObject to: bean.getList(criteria)){
				ContractData data = (ContractData) to;
				if(data.getExpression()!=null){
					list.add(data);
				}
			}
		} catch (ManagerBeanException e) {
			// NADA, que siga generando el fichero
		}
		return list;
	}
	
	/*
	 * CONVERSIONES
	 */
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
	
		
}

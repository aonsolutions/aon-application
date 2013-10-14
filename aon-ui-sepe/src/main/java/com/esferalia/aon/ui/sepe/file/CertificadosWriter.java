package com.esferalia.aon.ui.sepe.file;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.ui.common.converter.TransferObjectConverter;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Certifica2Batch;
import com.esferalia.aon.payroll.Certifica2BatchDetail;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.COTIZACIONREATYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.COTIZACIONTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.CUENTACOTIZACIONTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.CertificadoEmpresa;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.DISTRIBUCIONJORNADASTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.EMPRESATYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.PERIODODISTRIBUCIONJORNADASTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.REPRESENTANTETYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.TRABAJADORTYPE;
import com.esferalia.aon.ui.sepe.utils.SEPEFileUtils;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

public class CertificadosWriter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CertificadosWriter.class.getName());
	
	private final String CERTIFICA2_MODEL_PATH = "com.esferalia.aon.sepe.api.certificados.certificadoEmpresa";
	
	private String fileName;
	
	
	public CertificadosWriter() {
	}
	
	public String getFileName() {
		return fileName; 
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public File createFile(Certifica2Batch batch, List<ITransferObject> batchDetailList) throws ManagerBeanException, IOException{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String date = formatter.format(new Date());
		formatter = new SimpleDateFormat("HHmm");
		String hour = formatter.format(new Date());
		fileName = batch.getEnterprise().getRegistry().getDocument();
		fileName += String.valueOf(Integer.parseInt(date)) + String.valueOf(Integer.parseInt(hour));
		
		CertificadoEmpresa certificado = createCertificadoEmpresaType(batch, batchDetailList);
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(CERTIFICA2_MODEL_PATH);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, SEPEFileUtils.XML_FILE_ENCODING);
			File file = File.createTempFile("aon-temp", ".XML"); 
			marshaller.marshal( certificado, file );
			return file;
		} catch (JAXBException e) {
			String msg = "Error al generar el documento xml del certificado." ;
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage("[" + e + "]");
			throw new AbortProcessingException(msg, e);
		}		
	}
	
	
	
	public CertificadoEmpresa createCertificadoEmpresaType(Certifica2Batch batch, List<ITransferObject> batchDetailList) throws ManagerBeanException {
		CertificadoEmpresa certificado = new CertificadoEmpresa();
//		for(EnterpriseCCC ccc: getCccList(batchDetailList)){
//			certificado.getCuentaCotizacion().add(createCuentaCotizacionType(ccc, batch, batchDetailList));
//		}
//		return certificado;
		EnterpriseCCC ccc = null;
		CUENTACOTIZACIONTYPE cuentaCotizacionType = null;
		int i = 0;
		for(ITransferObject to: batchDetailList){
			Certifica2BatchDetail detail = (Certifica2BatchDetail) to;
			EnterpriseCCC contractCcc = detail.getContract().getEnterpriseCCC();
			if(ccc == null || !ccc.getId().equals(contractCcc.getId())){
				cuentaCotizacionType = createCuentaCotizacionType(contractCcc);
				certificado.getCuentaCotizacion().add(cuentaCotizacionType);
				ccc = contractCcc;
			}
			cuentaCotizacionType.getDatosTrabajador().add(createTrabajadorType(detail));
			i++;
			System.out.println("__ " + i);
			
			
		}
		return certificado;
	}
	
	/**
	 * 	<xsd:complexType name="CUENTA_COTIZACION_TYPE">
			<xsd:sequence>
				<xsd:element name="Datos_Representante" type="REPRESENTANTE_TYPE"/>
				<xsd:element name="Datos_Empresa" type="EMPRESA_TYPE"/>
				<xsd:element name="Datos_Trabajador" type="TRABAJADOR_TYPE" maxOccurs="unbounded"/>
				<xsd:any namespace="##any" processContents="strict" minOccurs="0" maxOccurs="0"/>
			</xsd:sequence>
		</xsd:complexType>
	 * @param ccc
	 * @param batch
	 * @param listaDetalle
	 * @return
	 */
	private CUENTACOTIZACIONTYPE createCuentaCotizacionType(EnterpriseCCC ccc) {
		CUENTACOTIZACIONTYPE o = new CUENTACOTIZACIONTYPE();
		o.setDatosRepresentante(createRepresentanteType(ccc.getActivity().getEnterprise()));
		o.setDatosEmpresa(createEmpresaType(ccc));
		return o;
		
	}
//	private CUENTACOTIZACIONTYPE createCuentaCotizacionType(EnterpriseCCC ccc, Certifica2Batch batch, List<ITransferObject> batchDetailList) {
//		CUENTACOTIZACIONTYPE o = new CUENTACOTIZACIONTYPE();
//		o.setDatosRepresentante(createRepresentanteType(batch.getEnterprise()));
//		o.setDatosEmpresa(createEmpresaType(ccc));
////		int i = 0;
////		for(ITransferObject to: batchDetailList){
////			Certifica2BatchDetail detail = (Certifica2BatchDetail) to;
////			o.getDatosTrabajador().add(createTrabajadorType(detail));
////			i++;
////			System.out.println("__ " + i);
////		}
//		return o;
//		
//	}

	/**
	 * <xsd:complexType name="REPRESENTANTE_TYPE">
		<xsd:sequence>
			<xsd:element name="CIF_NIF" type="CIF_NIF_SIMPLETYPE"/>
			<xsd:element name="Nombre" type="NOMBRE_SIMPLETYPE"/>
			<xsd:element name="Apellido1" type="APELLIDO_SIMPLETYPE"/>
			<xsd:element name="Apellido2" type="APELLIDO_SIMPLETYPE" minOccurs="0"/>
			<xsd:element name="Cargo" type="CARGO_SIMPLETYPE" minOccurs="0"/>
			<xsd:any namespace="##any" processContents="strict" minOccurs="0" maxOccurs="0"/>
		</xsd:sequence>
	</xsd:complexType>
	 * @return
	 */
	private REPRESENTANTETYPE createRepresentanteType(Enterprise enterprise) {
		REPRESENTANTETYPE o = null;
		RegistryDirStaff dirStaff = getRegistryDirStaff(enterprise);
		if(dirStaff!=null){
			o = new REPRESENTANTETYPE();
			String name[] = StringUtils.split(dirStaff.getName(),' ');
			String nombre = name.length>=1?name[0]:"";
			String ap1 = name.length>=2?name[1]:"";
			String ap2 = name.length>=3?name[2]:"";
			String cargo = null;
			if (dirStaff.isShareHolder()){
				cargo = "Socio";
			} else if (dirStaff.isRepresentative()){
				cargo = "Administrativo";
			} else if (dirStaff.isRepresentativeLabor()){
				cargo = "Representante Laboral";
			} else if (dirStaff.isDirector()){
				cargo = "Apoderado";
			}
			o.setCIFNIF(dirStaff.getDocument());
			o.setNombre(nombre);
			o.setApellido1(ap1);
			o.setApellido2(ap2);
			o.setCargo(cargo);
		}
		return o;
	}

	/**
	 * <xsd:complexType name="EMPRESA_TYPE">
		<xsd:sequence>
			<xsd:element name="CIF_NIF" type="CIF_NIF_SIMPLETYPE"/>
			<xsd:element name="CCC" type="CODIGO_CUENTA_COTIZACION_SIMPLETYPE"/>
			<xsd:any namespace="##any" processContents="strict" minOccurs="0" maxOccurs="0"/>
		</xsd:sequence>
	</xsd:complexType>
	 * @return
	 */
	private EMPRESATYPE createEmpresaType(EnterpriseCCC ccc) {
		EMPRESATYPE o = new EMPRESATYPE();
		if(ccc!=null){
			o.setCIFNIF(ccc.getActivity().getEnterprise().getRegistry().getDocument());
			o.setCCC(ccc.getActivity().getQuoteRegimeCode() + ccc.getCcc());
		}
		return o;
	}
	
	/**
	 * <xsd:complexType name="TRABAJADOR_TYPE">
		<xsd:sequence>
			<xsd:element name="DNI_NIE" type="NIF_NIE_SIMPLETYPE"/>
			<xsd:element name="Nombre" type="NOMBRE_SIMPLETYPE"/>
			<xsd:element name="Apellido1" type="APELLIDO_SIMPLETYPE"/>
			<xsd:element name="Apellido2" type="APELLIDO_SIMPLETYPE" minOccurs="0"/>
			<xsd:element name="NumSS" type="NUMERO_SEGURIDAD_SOCIAL_SIMPLETYPE" minOccurs="0"/>
			<xsd:element name="GrupoCotizacion" type="N2_BASICTYPE" minOccurs="0"/>
			<xsd:element name="TipoContrato" type="N3_BASICTYPE"/>
			<xsd:element name="DuracionContrato" type="N5_BASICTYPE" minOccurs="0"/>
			<xsd:element name="IndicadorDuracionContrato" type="IND_DUR_CONTRATO_SIMPLETYPE" minOccurs="0"/>
			<xsd:element name="CodProfesion" type="N7_BASICTYPE"/>
			<xsd:element name="CargoPublicoSindical" type="N2_BASICTYPE" minOccurs="0"/>
			<xsd:choice minOccurs="0">
				<xsd:element name="PorcentualDedicacion" type="N4_BASICTYPE" minOccurs="0"/>
				<xsd:element name="DedicacionCompleta" type="DEDICACION_COMPLETA_SIMPLETYPE" minOccurs="0"/>
			</xsd:choice>
			<xsd:element name="FechaAltaEmpresa" type="FECHA_SIMPLETYPE"/>
			<xsd:element name="CodCausaSuspension" type="N2_BASICTYPE"/>
			<xsd:element name="FechaSuspensionExtincion" type="FECHA_SIMPLETYPE"/>
			<xsd:element name="FechaFinSuspension" type="FECHA_SIMPLETYPE" minOccurs="0"/>
			<xsd:element name="ERE" type="ERE_SIMPLETYPE" minOccurs="0"/>
			<xsd:element name="PorcentualReduccionERE" type="N4_BASICTYPE" minOccurs="0"/>
			<xsd:element name="PorcentualReduccionOTROS" type="N4_BASICTYPE" minOccurs="0"/>
			<xsd:element name="CodCausaPorcentReduccion" type="N2_BASICTYPE" minOccurs="0"/>
			<xsd:element name="FechaDesdePeriodoSalarios" type="FECHA_SIMPLETYPE" minOccurs="0"/>
			<xsd:element name="FechaHastaPeriodoSalarios" type="FECHA_SIMPLETYPE" minOccurs="0"/>
			<xsd:element name="DiasSalarioTramitacion" type="N5_BASICTYPE"/>
			<xsd:element name="DistribucionJornadas" type="DISTRIBUCION_JORNADAS_TYPE" minOccurs="0"/>
			<xsd:choice>
				<xsd:sequence>
					<xsd:element name="Datos_Cotizacion" type="COTIZACION_TYPE" maxOccurs="unbounded"/>
				</xsd:sequence>
				<xsd:sequence>
					<xsd:element name="Datos_Cotizacion_REA" type="COTIZACION_REA_TYPE" maxOccurs="unbounded"/>
				</xsd:sequence>
			</xsd:choice>
			<xsd:choice minOccurs="0">
				<xsd:element name="Datos_VacacionesCotizadas" minOccurs="0">
				</xsd:element>
				<xsd:element name="Datos_VacacionesCotizadas_REA" minOccurs="0">
				</xsd:element>
			</xsd:choice>
			<xsd:any namespace="##any" processContents="strict" minOccurs="0" maxOccurs="0"/>
		</xsd:sequence>
	</xsd:complexType>
	 * @param detail
	 * @return
	 */
	private TRABAJADORTYPE createTrabajadorType(Certifica2BatchDetail batchDetail) {
		SEPEUtils utils = new SEPEUtils();
		
		TRABAJADORTYPE o = new TRABAJADORTYPE();
		
		o.setDNINIE(batchDetail.getContract().getPerson().getRegistry().getDocument());
		String name = batchDetail.getContract().getPerson().getName();
		o.setNombre(name.length()>9?name.substring(0, 8):name);
		o.setApellido1(batchDetail.getContract().getPerson().getFirstSurname());
		String secondSurname = batchDetail.getContract().getPerson().getSecondSurname();
		o.setApellido2(StringUtils.isBlank(secondSurname)?null:secondSurname);
		o.setNumSS(batchDetail.getContract().getPerson().getSocialSecurityNumber());
		String quoteGroup = utils.getContractDataMap(batchDetail.getContract()).get(ContextVariable.QUOTE_GROUP.getName());
		o.setGrupoCotizacion(quoteGroup!=null?quoteGroup:null);
		String tc2 = utils.getContractDataMap(batchDetail.getContract()).get(ContextVariable.TC2.getName());
		o.setTipoContrato(tc2);
		o.setDuracionContrato(completeLength(differenceBetweenDates(batchDetail.getContract().getStartDate(), batchDetail.getContract().getEndDate()).toString(),5,false));
		o.setIndicadorDuracionContrato(null);

		String occupation = utils.getContractDataMap(batchDetail.getContract()).get(ContextVariable.CNO.getName());
		o.setCodProfesion(completeLength(occupation,7,false));
		o.setCargoPublicoSindical(null);
		
//		<xsd:choice minOccurs="0">
			o.setPorcentualDedicacion(null);
			o.setDedicacionCompleta(null);
//		</xsd:choice>
			
		o.setFechaAltaEmpresa(createFechaSimpleType(batchDetail.getContract().getStartDate()));
		o.setCodCausaSuspension(batchDetail.getSuspensionCause().getValue());
		o.setFechaSuspensionExtincion(createFechaSimpleType(batchDetail.getContract().getEndDate()));
		o.setFechaFinSuspension(null);
		o.setERE(null);
		o.setPorcentualReduccionERE(null);
		o.setPorcentualReduccionOTROS(null);
		o.setCodCausaPorcentReduccion(null);
		o.setFechaDesdePeriodoSalarios(null);
		o.setFechaHastaPeriodoSalarios(null);
		o.setDiasSalarioTramitacion("00000");
		
		if(!isFulltimeContract(batchDetail)){
			o.setDistribucionJornadas(createDistribucionJornadasType(batchDetail));
		}
		
//		for(ITransferObject to: obtainBatchData(batchDetail)){
//			Certifica2BatchData batchData = (Certifica2BatchData) to;
//			o.getDatosCotizacion().add(createCotizacionType(batchData));
		for(Cotizacion cotizacion: getCotizacionList(batchDetail)){
			o.getDatosCotizacion().add(createCotizacionType(cotizacion));
		}
		
		o.getDatosCotizacionREA().add(null);

		o.setDatosVacacionesCotizadas(createVacacionesCotizadasType(batchDetail.getContract()));
		o.setDatosVacacionesCotizadasREA(null);
		return o;
	}
	
	/**
	<!-- NIVEL 3.1 -->
	<xsd:complexType name="DISTRIBUCION_JORNADAS_TYPE">
		<xsd:sequence>
			<xsd:element name="Periodo" type="PERIODO_DISTRIBUCION_JORNADAS_TYPE" maxOccurs="unbounded"/>
		</xsd:sequence>
	</xsd:complexType>
	 * @param batchDetail 
	 * 
	 * @return
	 */
	private DISTRIBUCIONJORNADASTYPE createDistribucionJornadasType(Certifica2BatchDetail batchDetail){
		final String IRREGULAR_VALUE = "2";
		final String REGULAR_VALUE = "1";
		SEPEUtils utils = new SEPEUtils();
		
		DISTRIBUCIONJORNADASTYPE o = new DISTRIBUCIONJORNADASTYPE();
		PERIODODISTRIBUCIONJORNADASTYPE periodo = null;
		List<PERIODODISTRIBUCIONJORNADASTYPE> listaPeriodos = new ArrayList<PERIODODISTRIBUCIONJORNADASTYPE>();
		List<Period> existingPeriods = getPeriodList(batchDetail.getContract());
		
		if(existingPeriods!=null){
			for (Period p : getPeriodList(batchDetail.getContract())) {
				
				Map<String, ContractData> map = utils.getContractDataMap(batchDetail.getContract(), p.getStart(), p.getEnd());
				
				ContractData diasTp = map.get(ContextVariable.CONTRACT_DAYS.getName());
				ContractData diasSemanaTp = map.get(ContextVariable.WEEK_DAYS.getName());
				
				
//				String diasTp = getContractDataExpression(batchDetail.getContract(), p, ContextVariable.CONTRACT_DAYS);
//				String diasSemanaTp = getContractDataExpression(batchDetail.getContract(), p, ContextVariable.WEEK_DAYS);
				if(diasTp!=null || diasSemanaTp!=null){
					if(isIrregular(batchDetail.getContract(), p)){
						addPeriod(IRREGULAR_VALUE, p, diasTp.getExpression(), listaPeriodos, periodo);
					} else {
						addPeriod(REGULAR_VALUE, p, diasSemanaTp.getExpression(), listaPeriodos, periodo);
					}
				}
			}
		}

		if(!listaPeriodos.isEmpty()){
			o.getPeriodo().clear();
			o.getPeriodo().addAll(listaPeriodos);
		}
		return o;
	}
	
	private List<Period> getPeriodList(Contract contract) {
		SEPEUtils utils = new SEPEUtils();
		List<Period> list = null;
		for(ContractData cd: utils.getContractDataMap(contract, null, null).values()){
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
	
	private void addPeriod(String tipoTp, Period p, String diasTp, List<PERIODODISTRIBUCIONJORNADASTYPE> listaPeriodos, PERIODODISTRIBUCIONJORNADASTYPE periodo) {
		DateFormat dateYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
		Date startDate = p.getStart();
		Date endDate = p.getEnd();
		if(listaPeriodos.isEmpty()){
			periodo = createPeriodoDistribucionJornadasType(tipoTp,startDate,endDate,diasTp);
			listaPeriodos.add(periodo);
		} else {
			PERIODODISTRIBUCIONJORNADASTYPE tempPeriodo = listaPeriodos.get(listaPeriodos.size()-1);
			try {
				if(tempPeriodo.getTipoDistribucion().equals(tipoTp) 
						&& tempPeriodo.getNumeroDiasTrabajadosPorSemanaOPeriodo().equals(completeLength(diasTp, 5))
						&& differenceBetweenDates(dateYYYYMMDD.parse(tempPeriodo.getFechaFinPeriodo()),startDate).equals(2)){
					tempPeriodo.setFechaFinPeriodo(createFechaSimpleType(endDate));
				} else {
					periodo = createPeriodoDistribucionJornadasType(tipoTp,startDate,endDate,diasTp);
					listaPeriodos.add(periodo);
				}
			} catch (ParseException e) {
				String msg = "Error al obtener la fecha de inicio del periodo ("+e.getMessage()+")";
				LOGGER.warn(msg);
			}
		}
	}
		
	/**
	<!-- NIVEL 3.1 -->
	<xsd:complexType name="PERIODO_DISTRIBUCION_JORNADAS_TYPE">
		<xsd:sequence>
			<xsd:element name="TipoDistribucion" type="N1_BASICTYPE"/>
			<xsd:element name="FechaInicioPeriodo" type="FECHA_SIMPLETYPE"/>
			<xsd:element name="FechaFinPeriodo" type="FECHA_SIMPLETYPE"/>
			<xsd:element name="NumeroDiasTrabajadosPorSemanaOPeriodo" type="N5_BASICTYPE_MAYOR"/>
			<xsd:any namespace="##any" processContents="strict" minOccurs="0" maxOccurs="0"/>
		</xsd:sequence>
	</xsd:complexType>
	 * 
	 * @return
	 */
	private PERIODODISTRIBUCIONJORNADASTYPE createPeriodoDistribucionJornadasType(String tipoDistribucion, Date inicio, Date fin, String numDiasSemanaPeriodo){
		PERIODODISTRIBUCIONJORNADASTYPE o = new PERIODODISTRIBUCIONJORNADASTYPE();
		o.setTipoDistribucion(tipoDistribucion);
		o.setFechaInicioPeriodo(createFechaSimpleType(inicio));
		o.setFechaFinPeriodo(createFechaSimpleType(fin));
		o.setNumeroDiasTrabajadosPorSemanaOPeriodo(completeLength(numDiasSemanaPeriodo, 5));
		return o;
	}
	
	/**
	<!-- NIVEL 3.2 -->
	<xsd:complexType name="COTIZACION_TYPE">
		<xsd:sequence>
			<xsd:element name="Ano" type="ANO_SIMPLETYPE"/>
			<xsd:element name="Mes" type="MES_SIMPLETYPE"/>
			<xsd:element name="NumDiasCotizados" type="DIAS_SIMPLETYPE"/>
			<xsd:element name="BaseCotizacionContingenciasComunes" type="N9_BASICTYPE" minOccurs="0"/>
			<xsd:element name="BaseCotizacionDesempleo" type="N9_BASICTYPE"/>
			<xsd:element name="Observaciones" type="BIG_STRING_BASICTYPE" minOccurs="0"/>
			<xsd:any namespace="##any" processContents="strict" minOccurs="0" maxOccurs="0"/>
		</xsd:sequence>
	</xsd:complexType>
	 * 
	 * @return
	 */
//	private COTIZACIONTYPE createCotizacionType(Certifica2BatchData batchData){
//		COTIZACIONTYPE o = new COTIZACIONTYPE();
//		Certifica2BatchData data = batchData;
//		o.setAno(data.getYear().toString());
//		o.setMes(parseToLength(data.getMonth().toString(), 2));
//		o.setNumDiasCotizados(parseToLength(data.getContributionDays(), 3));
//		o.setBaseCotizacionContingenciasComunes(parseToLength(data.getCgcContributionBase(), 9));
//		o.setBaseCotizacionDesempleo(parseToLength(data.getUnemploymentContributionBase(), 9));
//		o.setObservaciones(data.getComments());
//		return o;
//	}
	private COTIZACIONTYPE createCotizacionType(Cotizacion batchData){
		COTIZACIONTYPE o = new COTIZACIONTYPE();
		Cotizacion data = batchData;
		o.setAno(data.getYear().toString());
		o.setMes(completeLength(data.getMonth().toString(), 2,false));
		o.setNumDiasCotizados(completeLength(data.getContributionDays().toString(), 3,false));
		o.setBaseCotizacionContingenciasComunes(completeLength(data.getCgcContributionBase(), 9, false));
		o.setBaseCotizacionDesempleo(completeLength(data.getUnemploymentContributionBase(), 9,false));
		o.setObservaciones(data.getComments());
		return o;
	}
	
	private List<Cotizacion> getCotizacionList(Certifica2BatchDetail detail) {
		ISalary nomina = null;
		List<Cotizacion> cotizacionList = null;
		Integer totalDias = 0;
		Calendar calInicio = new GregorianCalendar();
		Calendar calFin = new GregorianCalendar();
		calInicio.setTime(detail.getContract().getStartDate());
		calFin.setTime(detail.getContract().getEndDate());
		calFin.set(Calendar.DAY_OF_MONTH, calFin.getActualMaximum(Calendar.DAY_OF_MONTH));
		cotizacionList = new ArrayList<Cotizacion>();
		while((calInicio.before(calFin) || calInicio.equals(calFin)) && totalDias < 180) {
			Calendar sDate = new GregorianCalendar();
			Calendar eDate = new GregorianCalendar();
			sDate.setTime(new Date(calFin.getTimeInMillis()));
			eDate.setTime(new Date(calFin.getTimeInMillis()));
			sDate.set(Calendar.DAY_OF_MONTH, 1);
			eDate.set(Calendar.DAY_OF_MONTH, sDate.getActualMaximum(Calendar.DAY_OF_MONTH));
			nomina = getSalary(detail.getContract(),  sDate.getTime(), eDate.getTime());
			calFin.add(Calendar.DATE, -calFin.get(Calendar.DAY_OF_MONTH));
			if(nomina != null) {
				Double baseCg = nomina.getCommonBase();
				Double baseAcc = nomina.getProfessionalBase();
				//TODO obtener la base por desempleo
//				Double baseDesempleo = nomina.getBasePerdes(); 
				Double baseDesempleo = nomina.getIrpfBase();
				// TODO obtener las nominas diferencia
				ISalary atraso = getSalary(detail.getContract(),  sDate.getTime(), eDate.getTime(), SalaryType.DELAY);
				if(atraso!=null){
					baseCg += atraso.getCommonBase();
					baseAcc += atraso.getProfessionalBase();
					baseDesempleo += atraso.getIrpfBase();
				}
//				List<INominaDiferencia> nominasDiferencia = getNominaDAO().getNominasDiferencia(params);
//				for(INominaDiferencia nomDf:nominasDiferencia) {
//					baseCg += nomDf.getBaseCgPts();
//					baseAcc += nomDf.getBaseAccPts();
//					baseDesempleo += nomDf.getBasePerdes();
//				}
				if ((nomina.getOvertimeBase() == null || nomina.getOvertimeBase() == 0)
						&& (nomina.getNonEstructuralOvertimeBase() == null || nomina.getNonEstructuralOvertimeBase() == 0)) {
					baseDesempleo = baseAcc;
				} 
				totalDias += nomina.getTimeUnits();
				Cotizacion cotizacion = new Cotizacion();
				Calendar cal = new GregorianCalendar();
				cal.setTime(nomina.getEndDate());
				cotizacion.setYear(cal.get(Calendar.YEAR));
				cotizacion.setMonth(cal.get(Calendar.MONTH)+1);
				cotizacion.setContributionDays(nomina.getTimeUnits());
				cotizacion.setCgcContributionBase(baseCg);
				cotizacion.setUnemploymentContributionBase(baseDesempleo);
				cotizacion.setComments(null);
				cotizacionList.add(cotizacion);
			}
		}
		return cotizacionList;
	}
	
	public ISalary getSalary(Contract contract, Date startDate, Date endDate) {
		return getSalary(contract, startDate, endDate, SalaryType.SALARY);
	}
	
	public ISalary getSalary(Contract contract, Date startDate, Date endDate, SalaryType type) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Salary.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_CONTRACT_ID), contract.getId());
			if(type!=null){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_TYPE), type);
			} else {
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_TYPE), SalaryType.SALARY);
			}
			if(startDate != null){
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
				criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.SALARY_START_DATE), cal.getTime());
			}
			if(endDate != null){
				Calendar cal = Calendar.getInstance();
				cal.setTime(endDate);
				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.SALARY_END_DATE), cal.getTime());
			}
			criteria.addOrder(bean.getFieldName(IEntityAlias.SALARY_END_DATE), false);
			List<ITransferObject> list = bean.getList(criteria);
			if(list!=null && !list.isEmpty()){
				return (ISalary) list.get(0);
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible obtener la ultima nomina";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		return null;
	}
	
	/**
	<xsd:complexType name="COTIZACION_REA_TYPE">
		<xsd:sequence>
			<xsd:element name="Ano" type="ANO_SIMPLETYPE"/>
			<xsd:element name="Mes" type="MES_SIMPLETYPE"/>
			<xsd:element name="GrupoCotizacion" type="N2_BASICTYPE" minOccurs="0"/>
			<xsd:element name="NumDiasCotizados" type="DIAS_COTIZ_REA_SIMPLETYPE" minOccurs="0"/>
			<xsd:element name="NumJornadasCotizadas" type="JORN_COTIZ_REA_SIMPLETYPE" minOccurs="0"/>
			<xsd:element name="BaseCotizacionDesempleo" type="N9_BASICTYPE" minOccurs="0"/>
			<xsd:element name="Observaciones" type="BIG_STRING_BASICTYPE" minOccurs="0"/>
			<xsd:any namespace="##any" processContents="strict" minOccurs="0" maxOccurs="0"/>
		</xsd:sequence>
	</xsd:complexType>
	 * 
	 * @return
	 */
	private COTIZACIONREATYPE createCotizacionReaType(){
		COTIZACIONREATYPE o = new COTIZACIONREATYPE();
		o.setAno(createAnioSimpleType(null));
		o.setMes(createMesSimpleType(null));
		o.setGrupoCotizacion(createN2BasicType(null));
		o.setNumDiasCotizados(createDiasCotizReaSimpleType());
		o.setNumJornadasCotizadas(createJornCotizReaSimpleType());
		o.setBaseCotizacionDesempleo(createN9BasicType(null));
		o.setObservaciones(createBigStringBasicType(null));
		return o;
	}
	
	/**
	<xsd:complexType name="VACACIONES_COTIZADAS_TYPE"/>
	
	<xsd:extension base="VACACIONES_COTIZADAS_TYPE">
		<xsd:sequence>
			<xsd:element name="NumDiasCotizados" type="N3_BASICTYPE"/>
			<xsd:element name="BaseCotizacionContingenciasComunes" type="N9_BASICTYPE" minOccurs="0"/>
			<xsd:element name="BaseCotizacionDesempleo" type="N9_BASICTYPE"/>
			<xsd:element name="Observaciones" type="BIG_STRING_BASICTYPE" minOccurs="0"/>
			<xsd:any namespace="##any" processContents="strict" minOccurs="0" maxOccurs="0"/>
		</xsd:sequence>
	</xsd:extension>
	 * 
	 * @return
	 */
	private TRABAJADORTYPE.DatosVacacionesCotizadas createVacacionesCotizadasType(Contract contract){
		TRABAJADORTYPE.DatosVacacionesCotizadas o = null;
		SEPEUtils utils = new SEPEUtils();
		try {
			Salary settle = obtainSettle(contract);;
			String noHolidays = utils.getContractDataMap(contract).get(ContextVariable.NO_HOLIDAYS.getName());
			if(settle!=null && noHolidays!=null && noHolidays!="0"){
				o = new TRABAJADORTYPE.DatosVacacionesCotizadas();
				Double baseContingenciaGenerales = settle.getCommonBase();
				Double baseAccidentesTrabajo = settle.getProfessionalBase();
// TODO contemplar el caso de que exista atraso de finiquito 
//				IFiniquitoDiferencia finiquitodf = getNominaDAO().getFiniquitoDiferencia(empleado);
//				if(finiquitodf != null && finiquitodf.getDiasVacaciones() != 0 && finiquitodf.getImporteVacaciones() != 0) {
//					baseAccidentesTrabajo += finiquitodf.getBaseAccidentesTrabajo();
//					baseContingenciaGenerales += finiquitodf.getBaseContingenciasGenerales();
//				}
				o.setNumDiasCotizados(completeLength(noHolidays,3,false));
				o.setBaseCotizacionContingenciasComunes(completeLength(baseContingenciaGenerales, 9,false));
				o.setBaseCotizacionDesempleo(completeLength(baseAccidentesTrabajo, 9,false));
				o.setObservaciones(null);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener el finiquito de "+contract.getPerson().getFullName();
			AonUtil.addErrorMessage(msg);
		}
		return o;
	}
	
	/**
	<xsd:complexType name="VACACIONES_COTIZADAS_REA_TYPE"/>
	
	<xsd:extension base="VACACIONES_COTIZADAS_REA_TYPE">
		<xsd:sequence>
			<xsd:element name="GrupoCotizacion" type="N2_BASICTYPE" minOccurs="0"/>
			<xsd:element name="NumDiasCotizados" type="N2_BASICTYPE"/>
			<xsd:element name="NumJornadasCotizadas" type="VACAC_JORN_COTIZ_REA_SIMPLETYPE"/>
			<xsd:element name="BaseCotizacionDesempleo" type="N9_BASICTYPE" minOccurs="0"/>
			<xsd:element name="Observaciones" type="BIG_STRING_BASICTYPE" minOccurs="0"/>
			<xsd:any namespace="##any" processContents="strict" minOccurs="0" maxOccurs="0"/>
		</xsd:sequence>
	</xsd:extension>
	 * 
	 * @return
	 */
	private TRABAJADORTYPE.DatosVacacionesCotizadasREA createVacacionesCotizadasReaType(){
		TRABAJADORTYPE.DatosVacacionesCotizadasREA o = new TRABAJADORTYPE.DatosVacacionesCotizadasREA();
		o.setGrupoCotizacion(createN2BasicType(null));
		o.setNumDiasCotizados(createN2BasicType(null));
		o.setNumJornadasCotizadas(createVacacJornCotizReaSimpleType());
		o.setBaseCotizacionDesempleo(createN9BasicType(null));
		o.setObservaciones(createBigStringBasicType(null));
		return o;
	}
	
	// ***********************************************
	// TIPOS SIMPLES
	// ***********************************************
	
	/**
	<xsd:simpleType name="CIF_NIF_SIMPLETYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="([A-Z][0-9]{7}[A-Z0-9])|([0-9]{8}[A-Z])|([XYZ][0-9]{7}[A-Z])"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createCifNifSimpleType(String value){
		return null;
	}
	
	/**
	<xsd:simpleType name="NIF_NIE_SIMPLETYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="([0-9XYZ][0-9]{7}[A-Z]){1,1}"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createNifNieSimpleType(String value){
		return null;
	}
	
	
	/**
	<xsd:simpleType name="NOMBRE_SIMPLETYPE">
		<xsd:annotation>
			<xsd:documentation> 
				Debe comenzar por letra. A continuación, se permiten blancos, puntos, comas, guiones y apostrofos.
			</xsd:documentation>
		</xsd:annotation>
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="[\w][\w\s.,-`'´]{1,14}"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createNombreSimpleType(){
		return null;
	}
	
	/**
	<xsd:simpleType name="APELLIDO_SIMPLETYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="[\w][\w\s.,-`'´]{1,19}"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createApellidoSimpleType(){
		return null;
	}

	/**
	<xsd:simpleType name="CARGO_SIMPLETYPE">
		<xsd:restriction base="xsd:string">
			<xsd:minLength value="1"/>
			<xsd:maxLength value="40"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createCargoSimpleType(){
		return null;
	}

	/**
	<xsd:simpleType name="CODIGO_CUENTA_COTIZACION_SIMPLETYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="\d{15}"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createCodigoCuentaCotizacionSimpleType(){
		return null;
	}

	/**
	<xsd:simpleType name="FECHA_SIMPLETYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="(19|20)\d\d(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createFechaSimpleType(Date date){
		String pattern = "yyyyMMdd";
		return DateFormatUtils.format(date, pattern);
	}

	/**
	<xsd:simpleType name="ERE_SIMPLETYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="[0-9]{5}(19|20)[0-9]{2}"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createEreSimpleType(){
		return null;
	}

	/**
	<xsd:simpleType name="NUMERO_SEGURIDAD_SOCIAL_SIMPLETYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="[0-9]{12}"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createNumeroSSSimpleType(){
		return null;
	}

	/**
	<xsd:simpleType name="IND_DUR_CONTRATO_SIMPLETYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="[DMA]"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createIndDurContratoSimpleType(){
		return null;
	}
	
	/**
	<xsd:simpleType name="DEDICACION_COMPLETA_SIMPLETYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="1"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createDedicacionCompletaSimpleType(){
		return null;
	}

	/**
	<xsd:simpleType name="ANO_SIMPLETYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="(19|20)[0-9]{2}"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createAnioSimpleType(Date date){
		return completeLength(CommonUtil.getYear(date), 2);
	}
	
	/**
	<xsd:simpleType name="MES_SIMPLETYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="(01|02|03|04|05|06|07|08|09|10|11|12)"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createMesSimpleType(Date date){
		return completeLength(CommonUtil.getMonth(date), 2);
	}
	
	/**
	<xsd:simpleType name="DIAS_SIMPLETYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="(00[1-9]|0[12][0-9]|03[01])"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createDiasSimpleType(Date date){
		return completeLength(CommonUtil.getDay(date), 3);
	}

	/**
	<xsd:simpleType name="DIAS_COTIZ_REA_SIMPLETYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="(0[0-9]|[12][0-9]|3[01])"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createDiasCotizReaSimpleType(){
		return null;
	}
	
	/**
	<xsd:simpleType name="JORN_COTIZ_REA_SIMPLETYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="(0[0-9]|[12][0-9]|3[01])"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createJornCotizReaSimpleType(){
		return null;
	}

	/**
	<xsd:simpleType name="VACAC_JORN_COTIZ_REA_SIMPLETYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="00"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createVacacJornCotizReaSimpleType(){
		return null;
	}
	
	// *************************************** 
	// TIPOS BASICOS 
	// *************************************** 
	/**
	<xsd:simpleType name="N1_BASICTYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="[0-9]{1}"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createN1BasicType(String value){
		return null;
	}

	/**
	<xsd:simpleType name="N2_BASICTYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="[0-9]{2}"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createN2BasicType(String value){
		return null;
	}

	/**
	<xsd:simpleType name="N3_BASICTYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="[0-9]{3}"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createN3BasicType(String value){
		return null;
	}
	
	/**
	<xsd:simpleType name="N4_BASICTYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="[0-9]{4}"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createN4BasicType(String value){
		return null;
	}

	/**
	<xsd:simpleType name="N5_BASICTYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="[0-9]{5}"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createN5BasicType(String value){
		return null;
	}

	/**
	<xsd:simpleType name="N5_BASICTYPE_MAYOR">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="([1-9][0-9]{4}|[0-9][1-9][0-9]{3}|[0-9]{2}[1-9][0-9]{2}|[0-9]{3}[1-9][0-9]|[0-9]{4}[1-9])"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createN5BasicTypeMayor(String value){
		return null;
	}

	/**
	<xsd:simpleType name="N6_BASICTYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="[0-9]{6}"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createN6BasicType(String value){
		return null;
	}

	/**
	<xsd:simpleType name="N7_BASICTYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="[0-9]{7}"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createN7BasicType(String value){
		return null;
	}

	/**
	<xsd:simpleType name="N8_BASICTYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="[0-9]{8}"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createN8BasicType(String value){
		return null;
	}

	/**
	<xsd:simpleType name="N9_BASICTYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="[0-9]{9}"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createN9BasicType(String value){
		return null;
	}
	
	/**
	<xsd:simpleType name="N10_BASICTYPE">
		<xsd:restriction base="xsd:string">
			<xsd:pattern value="[0-9]{10}"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createN10BasicType(String value){
		return null;
	}
	
	/**
	<xsd:simpleType name="SMALL_STRING_BASICTYPE">
		<xsd:restriction base="xsd:string">
			<xsd:maxLength value="10"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createSmallStringBasicType(String value){
		return null;
	}
	
	/**
	<xsd:simpleType name="BIG_STRING_BASICTYPE">
		<xsd:restriction base="xsd:string">
			<xsd:minLength value="1"/>
			<xsd:maxLength value="50"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createBigStringBasicType(String value){
		return null;
	}
	
	/**
	<xsd:simpleType name="CHAR_STRING_BASICTYPE">
		<xsd:restriction base="xsd:string">
			<xsd:length value="1"/>
		</xsd:restriction>
	</xsd:simpleType>
	 * 
	 * @return
	 */
	private String createCharStringBasicType(String value){
		return null;
	}
	
	// ****************************************************
	// ****************************************************
	// AUXILIARES
	// ****************************************************
	// ****************************************************
	
//	private List<ITransferObject> obtainBatchDetailList(Certifica2Batch batch) {
//		try {
//			IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
//			Criteria criteria = new Criteria();
//			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DETAIL_CERTIFICA2BATCH_ID), batch.getId());
//			return bean.getList(criteria);
//		} catch (ManagerBeanException e) {
//			// NADA, que siga con la generacion del fichero
//		}
//		return null;
//	}

	private List<EnterpriseCCC> getCccList(List<ITransferObject> batchDetailList) {
		List<EnterpriseCCC> list = new ArrayList<EnterpriseCCC>();
		for(ITransferObject to: batchDetailList){
			Certifica2BatchDetail detail = (Certifica2BatchDetail) to;
			EnterpriseCCC ccc = detail.getContract().getEnterpriseCCC();
			if(!list.contains(ccc)){
				list.add(ccc);
			}
		}
		return list;
	}
	
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
	
	private boolean isFulltimeContract(Certifica2BatchDetail detail) {
		SEPEUtils utils = new SEPEUtils();
		String fullTime = utils.getContractDataMap(detail.getContract()).get(ContextVariable.FULL_TIME.getName());
		String tc2 = utils.getContractDataMap(detail.getContract()).get(ContextVariable.TC2.getName());
		if(fullTime==null || new Boolean(fullTime)){
//			if(detail.getContractType().startsWith("1") || detail.getContractType().startsWith("4")){ 
			if(tc2.startsWith("1") || tc2.startsWith("4")){ 
				return true;
			} 
		}
		return false;
	}
	
	private boolean isIrregular(Contract contract, Period p) {
		SEPEUtils utils = new SEPEUtils();
		ContractData cd = utils.getContractDataMap(contract, p.getStart(), p.getEnd()).get(ContextVariable.IRREGULAR.getName());
		return (cd!=null && new Boolean(cd.getExpression()));
	}
	
	private Salary obtainSettle(Contract contract) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Salary.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_CONTRACT_ID), contract.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_TYPE), SalaryType.SETTLE);
		List<ITransferObject> list = bean.getList(criteria);
		if(!list.isEmpty()){
			return (Salary) list.get(0);
		}
		return null;
	}
	
//	private List<ITransferObject> obtainBatchData(Certifica2BatchDetail detail) {
//		try {
//			IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchData.class);
//			Criteria criteria = new Criteria();
//			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DATA_CERTIFICA2BATCH_DETAIL_ID), detail.getId());
//			criteria.addOrder(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DATA_YEAR), false);
//			criteria.addOrder(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DATA_MONTH), false);
//			return bean.getList(criteria);
//		} catch (ManagerBeanException e) {
//			// NADA, que siga con la generacion del fichero
//		}
//		return null;
//	}
	
	private String completeLength(Integer value, Integer length, boolean rightAppend) {
		return completeLength(value.toString(), length, rightAppend);
	}
	
	private String completeLength(String value, Integer length, boolean rightAppend) {
		return completeLength(value, length, "0", rightAppend);
	}
	
	private String completeLength(String value, Integer length, String appendValue, boolean rightAppend) {
		if(value==null)return null;
		StringBuilder builder = new StringBuilder("");
		if(rightAppend){
			builder.append(value);
		}
		for(int i=value.length(); i<length; i++){
			builder.append(appendValue);
		}
		if(!rightAppend){
			builder.append(value);
		}
		return builder.toString();
	}
	
//	private String parseToLength(String var, Integer lon, Boolean dir) {
//		StringBuffer parse = new StringBuffer();
//		if(var != null) {
//			if(!dir) {
//				parse.append(var);
//			}
//			for(int i = var.length(); i < Math.abs(lon); ++i) {
//				parse.append("0");
//			}
//			if(dir) {
//				parse.append(var);
//			}
//		}
//		return parse.toString();
//	}
	
	private String completeLength(String var, Integer lon) {
		return completeLength(var, lon, true);
	}
	
	private String completeLength(Integer var, Integer lon, Boolean dir) {
		return completeLength(String.valueOf(var), lon, dir);
	}
	
	private String completeLength(Integer var, Integer lon) {
		return completeLength(var, lon, true);
	}
	
	private String completeLength(Double var, Integer lon, Boolean dir) {
		var *= 100;
		var = CommonUtil.round(var);
		return completeLength(String.valueOf(var.intValue()), lon, dir);
	}
	
	private String completeLength(Double var, Integer lon) {
		return completeLength(var, lon, true);
	}
	
	private Integer differenceBetweenDates(Date from, Date to) {
		Integer diffDays = new Integer(0);
		final Double MS_PER_DAY = new Double(1000 * 60 * 60 * 24);
		if(from.before(to)) {
			diffDays = (int)((Math.floor((to.getTime() - from.getTime()) / MS_PER_DAY + 0.5d) + 1));
		}
		return diffDays;
	}
	
	class Cotizacion{
		private Integer year;
		private Integer month;
		private Integer contributionDays;
		private Double cgcContributionBase;
		private Double unemploymentContributionBase;
		private String comments;
		public Integer getYear() {
			return year;
		}
		public void setYear(Integer year) {
			this.year = year;
		}
		public Integer getMonth() {
			return month;
		}
		public void setMonth(Integer month) {
			this.month = month;
		}
		public Integer getContributionDays() {
			return contributionDays;
		}
		public void setContributionDays(Integer contributionDays) {
			this.contributionDays = contributionDays;
		}
		public Double getCgcContributionBase() {
			return cgcContributionBase;
		}
		public void setCgcContributionBase(Double cgcContributionBase) {
			this.cgcContributionBase = cgcContributionBase;
		}
		public Double getUnemploymentContributionBase() {
			return unemploymentContributionBase;
		}
		public void setUnemploymentContributionBase(Double unemploymentContributionBase) {
			this.unemploymentContributionBase = unemploymentContributionBase;
		}
		public String getComments() {
			return comments;
		}
		public void setComments(String comments) {
			this.comments = comments;
		}
	}
	
}

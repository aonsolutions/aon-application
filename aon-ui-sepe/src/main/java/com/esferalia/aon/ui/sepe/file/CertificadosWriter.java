package com.esferalia.aon.ui.sepe.file;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.faces.event.AbortProcessingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
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
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Certifica2Batch;
import com.esferalia.aon.payroll.Certifica2BatchDetail;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.SuspensionCause;
import com.esferalia.aon.payroll.util.PayrollUtils;
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
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CertificadosWriter implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
		EnterpriseCCC ccc = null;
		CUENTACOTIZACIONTYPE cuentaCotizacionType = null;
		for(ITransferObject to: batchDetailList){
			Certifica2BatchDetail detail = (Certifica2BatchDetail) to;
			EnterpriseCCC contractCcc = detail.getContract().getEnterpriseCCC();
			if(ccc == null || !ccc.getId().equals(contractCcc.getId())){
				cuentaCotizacionType = createCuentaCotizacionType(contractCcc);
				certificado.getCuentaCotizacion().add(cuentaCotizacionType);
				ccc = contractCcc;
//				System.out.println("__ccc: " + ccc.getFullCcc());
			}
			cuentaCotizacionType.getDatosTrabajador().add(createTrabajadorType(detail));
//			System.out.println("__trabajador: " + detail.getContract().getPerson().getFullName());
		}
		return certificado;
	}

	public CertificadoEmpresa createCertificadoEmpresaType(Contract contract, String suspensionCauseCode) throws ManagerBeanException {
		CertificadoEmpresa certificado = new CertificadoEmpresa();
		EnterpriseCCC ccc = null;
		CUENTACOTIZACIONTYPE cuentaCotizacionType = null;
		Certifica2BatchDetail detail = new Certifica2BatchDetail();
		detail.setContract(contract);
		if(suspensionCauseCode!=null){
			for(SuspensionCause cause: SuspensionCause.values()){
				if(Integer.parseInt(cause.getValue()) == Integer.parseInt(suspensionCauseCode)){
					detail.setSuspensionCause(cause);
					break;
				}
			}
		}
		EnterpriseCCC contractCcc = detail.getContract().getEnterpriseCCC();
		if(ccc == null || !ccc.getId().equals(contractCcc.getId())){
			cuentaCotizacionType = createCuentaCotizacionType(contractCcc);
			certificado.getCuentaCotizacion().add(cuentaCotizacionType);
			ccc = contractCcc;
		}
		cuentaCotizacionType.getDatosTrabajador().add(createTrabajadorType(detail));
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
				cargo = "Apoderado";
			} else if (dirStaff.isDirector()){
				cargo = "Administrador";
			} else if (dirStaff.isRepresentativeLabor()){
				cargo = "Representante Laboral";
			}
			o.setCIFNIF(dirStaff.getDocument());
			o.setNombre(createNombreSimpleType(nombre));
			o.setApellido1(createApellidoSimpleType(ap1));
			o.setApellido2(StringUtils.isBlank(ap2)?null:ap2);
			o.setCargo(cargo);
		} else {
			LOGGER.error("No se han definido los datos del representante laboral");
			AonUtil.addErrorMessage("No se han definido los datos del representante laboral");
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
			o.setCCC(PayrollUtils.getInstance().getRegimeCode(ccc)+ccc.getCcc());
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
		Contract contract = batchDetail.getContract();
		SEPEUtils utils = SEPEUtils.getInstance();
		String name = contract.getPerson().getName();
		String surname1 = contract.getPerson().getFirstSurname();
		String surname2 = contract.getPerson().getSecondSurname();
		String quoteGroup = utils.getContractDataMap(contract, Boolean.FALSE, Boolean.TRUE).get(ContextVariable.QUOTE_GROUP.getName());
		String tc2 = utils.getContractDataMap(contract, Boolean.FALSE, Boolean.TRUE).get(ContextVariable.TC2.getName());
		String occupation = utils.getContractDataMap(contract, Boolean.FALSE, Boolean.TRUE).get(ContextVariable.CNO.getName());
		
		Date endDate = contract.getEndDate();
		ContractData ereFactor = null;
		//C17=Suspensión del contrato ERE or C18=Reducción temporal de jornada ERE		
		if ( batchDetail.getSuspensionCause() == SuspensionCause.C17 
			|| batchDetail.getSuspensionCause() == SuspensionCause.C18  ) {
//			Map<String, ContractData> dataMap = utils.getContractDataMap(contract, null, null );
//			for (ContextVariable var : ContextVariable.ERE_FACTORS) {
//				if (dataMap.containsKey(var.getName())) {
//					ereFactor = dataMap.get(var.getName());
//					Date startDate = ereFactor.getStartDate();
//					endDate = AonDateUtils.add(startDate, Calendar.DAY_OF_MONTH, -1);
//					break;
//				}
//			}
			
			for (ContextVariable var : ContextVariable.ERE_FACTORS) {
				try {
					List<ITransferObject> objects = 
					utils.getContractData(contract, null, null, var.getName(), false);
					ContractData data = 
					objects.stream()
					.map(o -> (ContractData)o )
					.sorted((d1,d2)-> d1.getStartDate().compareTo(d2.getStartDate()))
					.reduce((d,d1) -> {
						
						double factor = Double.parseDouble(d.getExpression());
						double factor1 = Double.parseDouble(d1.getExpression());
						if ( factor != factor1 )
							return d;
						
						
						Date end = d.getEndDate();
						if ( end == null ) 
							return d;
						Date start = d1.getStartDate();
						
						if ( days(end, start) > 3 ) // ???
							return d ;
						
						d.setEndDate(d1.getEndDate());
						
						return d;
					} ).orElseThrow();
					
					Date startDate = data.getStartDate();
					endDate = AonDateUtils.add(startDate, Calendar.DAY_OF_MONTH, -1);
					ereFactor = data;
					break;
					
				} catch (Exception e) {
				}
			}
			
			

		}
		
		
		
		

		TRABAJADORTYPE o = new TRABAJADORTYPE();
		
		o.setDNINIE(AonStringUtils.upperCase(contract.getPerson().getRegistry().getDocument()));
		o.setNombre(createNombreSimpleType(name));
		o.setApellido1(createApellidoSimpleType(surname1));
		o.setApellido2(StringUtils.isBlank(surname2)?null:createApellidoSimpleType(surname2));
		o.setNumSS(contract.getPerson().getSocialSecurityNumber());
		if(contract.getEnterpriseCCC().getType()!=CCCType.AGRICULTURAL){
			o.setGrupoCotizacion(quoteGroup!=null?quoteGroup:null);
		}
		o.setTipoContrato(tc2);
		o.setDuracionContrato(completeLength(differenceBetweenDates(contract.getStartDate(), endDate).toString(),5,false));
		o.setIndicadorDuracionContrato(null);

		o.setCodProfesion(completeLength(occupation,7, true));
		o.setCargoPublicoSindical(null);
		
//		<xsd:choice minOccurs="0">
			o.setPorcentualDedicacion(null);
			o.setDedicacionCompleta(null);
//		</xsd:choice>
			
		o.setFechaAltaEmpresa(createFechaSimpleType(contract.getStartDate()));
		o.setCodCausaSuspension(batchDetail.getSuspensionCause()!=null?batchDetail.getSuspensionCause().getValue():null);
		o.setFechaSuspensionExtincion(createFechaSimpleType(endDate));
		
		
		if ( batchDetail.getSuspensionCause() == SuspensionCause.C16
			|| batchDetail.getSuspensionCause() == SuspensionCause.C17 
			|| batchDetail.getSuspensionCause() == SuspensionCause.C18  ) {
			String ereNumber = batchDetail.getEreNumber();
			if (AonStringUtils.isBlank(ereNumber)) {
				ereNumber = "12020";
			}
			ereNumber = ereNumber.replaceAll("[^0-9]+", "");
			if ( AonStringUtils.length(ereNumber) <= 3) {
					ereNumber += "2020";
			}
			ereNumber = ereNumber.replaceAll("[^0-9]+", "");
			    
			o.setERE(completeLength(ereNumber,9,false));
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY,0);
			calendar.set(Calendar.MINUTE,0);
			calendar.set(Calendar.SECOND,0);
			calendar.set(Calendar.MONTH,Calendar.MAY);
			calendar.set(Calendar.DAY_OF_MONTH,2);
			Date _02052020 = calendar.getTime();
			
			o.setFechaFinSuspension(createFechaSimpleType(Period.min(_02052020, ereFactor.getEndDate())));
		}else {
			o.setERE(null);
			o.setFechaFinSuspension(null);
		}
		
		
		if ( batchDetail.getSuspensionCause() == SuspensionCause.C18 ) {
			try {
				Integer coeficenteReduccion = (int)Double.parseDouble(ereFactor.getExpression()) * 100;
				o.setPorcentualReduccionERE(completeLength(coeficenteReduccion.toString(),4,false));
			} catch ( Exception e ) {
				o.setPorcentualReduccionERE(null);
			}
		} else {
			o.setPorcentualReduccionERE(null);
		
		}
		
		o.setPorcentualReduccionOTROS(null);
		o.setCodCausaPorcentReduccion(null);
		o.setFechaDesdePeriodoSalarios(null); 
		o.setFechaHastaPeriodoSalarios(null);
		o.setDiasSalarioTramitacion("00000");
		
		if(!isFulltimeContract(batchDetail)){
			o.setDistribucionJornadas(createDistribucionJornadasType(batchDetail,contract.getStartDate(), endDate));
		}
		
		if(contract.getEnterpriseCCC().getType()!=CCCType.AGRICULTURAL){
			for(COTIZACIONTYPE cotizacion: __getCotizacionList(batchDetail, endDate)){
				o.getDatosCotizacion().add(cotizacion);
			}
			o.setDatosVacacionesCotizadas(createVacacionesCotizadasType(contract));
		} else {
			for(COTIZACIONREATYPE cotizacion: getCotizacionReaList(batchDetail)){
				o.getDatosCotizacionREA().add(cotizacion);
			}
			o.setDatosVacacionesCotizadasREA(null);
		}

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
	private DISTRIBUCIONJORNADASTYPE createDistribucionJornadasType(Certifica2BatchDetail batchDetail, Date contractStart, Date contractEnd){
		final String IRREGULAR_VALUE = "2";
		final String REGULAR_VALUE = "1";
		SEPEUtils utils = SEPEUtils.getInstance();
		
		DISTRIBUCIONJORNADASTYPE o = new DISTRIBUCIONJORNADASTYPE();
		PERIODODISTRIBUCIONJORNADASTYPE periodo = null;
		List<PERIODODISTRIBUCIONJORNADASTYPE> listaPeriodos = new ArrayList<PERIODODISTRIBUCIONJORNADASTYPE>();
		List<Period> existingPeriods = getPeriodList(batchDetail.getContract());
		
		if(existingPeriods!=null){
			for (Period p : existingPeriods) {
				
				Map<String, ContractData> map = utils.getContractDataMap(batchDetail.getContract(), p.getStart(), p.getEnd(), Boolean.TRUE);
				
				ContractData diasTp = map.get(ContextVariable.CONTRACT_DAYS.getName());
				ContractData diasSemanaTp = map.get(ContextVariable.WEEK_DAYS.getName());
				
				if(diasTp!=null || diasSemanaTp!=null){
					if(isIrregular(batchDetail.getContract(), p)){
						addPeriod(IRREGULAR_VALUE, p, diasTp.getExpression(), listaPeriodos, periodo);
					} else {
						addPeriod(REGULAR_VALUE, p, diasSemanaTp.getExpression(), listaPeriodos, periodo);
					}
				}
			}
		} else {
			List<ContractData[]> weekList = SEPEUtils.getInstance().obtainWeekList(batchDetail.getContract());
			
			weekList.forEach(week -> {
				Integer totalWeekDays = obtainTotalWeekDays(week);
				if(totalWeekDays>0){
					Date start = week[0].getStartDate();
					Date end = week[0].getEndDate();
					addPeriod(REGULAR_VALUE, start!=null?start:contractStart, end!=null?end:contractEnd, totalWeekDays.toString(), listaPeriodos, periodo);
				}
			});
		}

		if(!listaPeriodos.isEmpty()){
			o.getPeriodo().clear();
			o.getPeriodo().addAll(listaPeriodos);
		}
		return o;
	}
	
	private Integer obtainTotalWeekDays(ContractData[] weekHours) {
		Integer totalWeekDays = 0;
		totalWeekDays += (weekHours[0]!=null && NumberUtils.isNumber(weekHours[0].getExpression()) && NumberUtils.toDouble(weekHours[0].getExpression())>0.0)?1:0;
		totalWeekDays += (weekHours[1]!=null && NumberUtils.isNumber(weekHours[1].getExpression()) && NumberUtils.toDouble(weekHours[1].getExpression())>0.0)?1:0;
		totalWeekDays += (weekHours[2]!=null && NumberUtils.isNumber(weekHours[2].getExpression()) && NumberUtils.toDouble(weekHours[2].getExpression())>0.0)?1:0;
		totalWeekDays += (weekHours[3]!=null && NumberUtils.isNumber(weekHours[3].getExpression()) && NumberUtils.toDouble(weekHours[3].getExpression())>0.0)?1:0;
		totalWeekDays += (weekHours[4]!=null && NumberUtils.isNumber(weekHours[4].getExpression()) && NumberUtils.toDouble(weekHours[4].getExpression())>0.0)?1:0;
		totalWeekDays += (weekHours[5]!=null && NumberUtils.isNumber(weekHours[5].getExpression()) && NumberUtils.toDouble(weekHours[5].getExpression())>0.0)?1:0;
		totalWeekDays += (weekHours[6]!=null && NumberUtils.isNumber(weekHours[6].getExpression()) && NumberUtils.toDouble(weekHours[6].getExpression())>0.0)?1:0;
		return totalWeekDays;
	}

	private List<Period> getPeriodList(Contract contract) {
		SEPEUtils utils = SEPEUtils.getInstance();
		List<Period> list = null;
		for(ContractData cd: utils.getContractDataMap(contract, null, null, Boolean.TRUE).values()){
			if(cd.getName().equals(ContextVariable.WEEK_DAYS.getName()) || cd.getName().equals(ContextVariable.CONTRACT_DAYS.getName())){
				Date startDate = cd.getStartDate();
				Date endDate = cd.getEndDate();
				Period period = new Period(
						startDate!=null?startDate:contract.getStartDate(),
						endDate!=null?endDate:contract.getEndDate());
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
		addPeriod(tipoTp, p.getStart(), p.getEnd(), diasTp, listaPeriodos, periodo);
	}
	private void addPeriod(String tipoTp, Date startDate, Date endDate, String diasTp, List<PERIODODISTRIBUCIONJORNADASTYPE> listaPeriodos, PERIODODISTRIBUCIONJORNADASTYPE periodo) {
		DateFormat dateYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
		if(listaPeriodos.isEmpty()){
			periodo = createPeriodoDistribucionJornadasType(tipoTp,startDate,endDate,diasTp);
			listaPeriodos.add(periodo);
		} else {
			PERIODODISTRIBUCIONJORNADASTYPE tempPeriodo = listaPeriodos.get(listaPeriodos.size()-1);
			try {
				if(tempPeriodo.getTipoDistribucion().equals(tipoTp) 
						&& tempPeriodo.getNumeroDiasTrabajadosPorSemanaOPeriodo().equals(completeLength(diasTp, 5, false))
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
		o.setNumeroDiasTrabajadosPorSemanaOPeriodo(completeLength(numDiasSemanaPeriodo, 5, false));
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
	private COTIZACIONTYPE createCotizacionType(int year, int month, Integer contributionDays, 
			Double cgcContributionBase, Double unemploymentContributionBase, String comments) {
		COTIZACIONTYPE o = new COTIZACIONTYPE();
		o.setAno(String.valueOf(year));
		o.setMes(completeLength(String.valueOf(month), 2,false));
		o.setNumDiasCotizados(completeLength(contributionDays.toString(), 3,false));
		o.setBaseCotizacionContingenciasComunes(completeLength(cgcContributionBase, 9, false));
		o.setBaseCotizacionDesempleo(completeLength(unemploymentContributionBase, 9,false));
		o.setObservaciones(comments);
		return o;
	}
	
	private List<COTIZACIONTYPE> getCotizacionList(Certifica2BatchDetail detail, Date inicio, Date fin) {
		List<ISalary> salaryList = null;
		List<COTIZACIONTYPE> cotizacionList = null;
		Integer totalDias = 0;
		Calendar calInicio = new GregorianCalendar();
		Calendar calFin = new GregorianCalendar();
		calInicio.setTime(inicio);
		calFin.setTime(fin);
		calFin.set(Calendar.DAY_OF_MONTH, calFin.getActualMaximum(Calendar.DAY_OF_MONTH));
		cotizacionList = new ArrayList<COTIZACIONTYPE>();
		List<ISalary> delayList;
		try {
			delayList = getSalaries(detail.getContract(), inicio, fin, SalaryType.DELAY);
			while((calInicio.before(calFin) || calInicio.equals(calFin)) && totalDias < 180) {
				Calendar startDate = new GregorianCalendar();
				Calendar endDate = new GregorianCalendar();
				startDate.setTime(new Date(calFin.getTimeInMillis()));
				endDate.setTime(new Date(calFin.getTimeInMillis()));
				startDate.set(Calendar.DAY_OF_MONTH, 1);
				endDate.set(Calendar.DAY_OF_MONTH, startDate.getActualMaximum(Calendar.DAY_OF_MONTH));
				salaryList = getSalaries(detail.getContract(), startDate.getTime(), endDate.getTime());
				calFin.add(Calendar.DATE, -calFin.get(Calendar.DAY_OF_MONTH));
				for(ISalary salary: salaryList){
					Double baseCg = salary.getCommonBase();
					Double baseAcc = salary.getProfessionalBase();
					
					// obtener las bases de los atrasos de las nominas
					if(delayList.size()>0){
						baseCg += getDelayBaseAmount(delayList, startDate.getTime(), endDate.getTime(), ContextVariable.CGC_BASE);
						baseAcc += getDelayBaseAmount(delayList, startDate.getTime(), endDate.getTime(), ContextVariable.CGP_BASE);
					}
					
					int salaryDays = differenceBetweenDates(salary.getStartDate(), salary.getEndDate());
					int cotizacionDays = differenceBetweenDates(salary.getStartDate(), minBetweenDates(salary.getEndDate(), fin));
					cotizacionDays = Math.min(cotizacionDays, 180 - totalDias );
					baseCg = baseCg / salaryDays * cotizacionDays;
					baseAcc = baseAcc / salaryDays * cotizacionDays;
					
					totalDias += cotizacionDays;
					Calendar cal = new GregorianCalendar();
					cal.setTime(salary.getEndDate());
					COTIZACIONTYPE cotizacion = createCotizacionType( cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, 
							cotizacionDays, baseCg, baseAcc, null);
					cotizacionList.add(cotizacion);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Ha ocurrido un error al obtener datos de las nominas.";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
		return cotizacionList;
	}
	private List<COTIZACIONTYPE> __getCotizacionList(Certifica2BatchDetail detail, Date endDate) {
		 
		Date startDate = AonDateUtils.add(endDate, Calendar.DAY_OF_MONTH, -179 );
		
		List<SalaryData> cgcBases;
		try {
			cgcBases = SEPEUtils.getInstance()
					.getSalaryDataList(detail.getContract(), startDate, endDate, ContextVariable.CGC_BASE.getName() );
		} catch (ManagerBeanException e) {
			String msg = "Ha ocurrido un error al obtener datos de las nominas.";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
		
		cgcBases.stream().forEach(d -> System.out.println(d.getName() + " = " + d.getExpression() + "(" + d.getStartDate() + ".." + d.getEndDate()));
		
		Map<Date, List<SalaryData>> monthCgcBasesMap = cgcBases.stream()
		.peek(d -> { 
			if ( d.getEndDate().after(endDate)) {
				double base = Double.parseDouble(d.getExpression()) / days(d.getStartDate(), d.getEndDate());
				d.setEndDate(endDate);
				d.setExpression(Double.toString(base * days(d.getStartDate(), endDate)));
			}
			if ( d.getStartDate().before(startDate) ) {
				double base = Double.parseDouble(d.getExpression()) / days(d.getStartDate(), d.getEndDate());
				d.setStartDate(startDate);
				d.setExpression(Double.toString(base * days(startDate, d.getEndDate())));
			}
		})
		.collect(Collectors.groupingBy(d -> AonDateUtils.getFirstDayOfMonth(d.getStartDate())));
		
		List<COTIZACIONTYPE> cotizacionList = new ArrayList<COTIZACIONTYPE>();
		for ( Map.Entry<Date, List<SalaryData>> entry: monthCgcBasesMap.entrySet() ) {
			Date date = entry.getKey();
			List<SalaryData> datas = entry.getValue();
			
			int year = AonDateUtils.get(date, Calendar.YEAR);
			int month = AonDateUtils.get(date, Calendar.MONTH) + 1;
			
			Map<Period, List<SalaryData>> datasMap = 
			datas.stream()
			.collect(Collectors.groupingBy(d -> 
			
			new Period(d.getStartDate(), d.getEndDate()) {
				@Override
				public int hashCode() {
					return Objects.hash(getStart(), getEnd());
				}
				
			}
			
			));
			
			int days = 0;
			double baseCgc = 0;
//			for ( SalaryData d: datas ) {				
//				baseCgc += Double.parseDouble(d.getExpression());
//				days += new Period(d.getStartDate(), d.getEndDate()).daysStream().count();
//			}
			Period p1 = null; 
			for ( Period p : datasMap.keySet()) {
				days += p.daysStream().count();
				baseCgc += datasMap.get(p)
						.stream()
						.collect(Collectors.summingDouble(d -> Double.parseDouble(d.getExpression())));
			}
			
			COTIZACIONTYPE cotizacion = createCotizacionType( 
					year, 
					month, 
					days, 
					baseCgc, 
					baseCgc, 
					null);
			
			cotizacionList.add(cotizacion);
		}
		
		cotizacionList.sort(  (c1, c2 ) -> 
		c2.getAno().equals(c1.getAno()) ? 
		Integer.parseInt(c2.getMes()) - Integer.parseInt(c1.getMes()): 
		Integer.parseInt(c2.getAno()) - Integer.parseInt(c1.getAno())
		);
		
		return cotizacionList;
		
		
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
	private COTIZACIONREATYPE createCotizacionReaType(int year, int month, String quoteGroup,
			Integer contributionDays, Integer journalDays,
			Double cgcContributionBase, Double unemploymentContributionBase,
			String comments){
		COTIZACIONREATYPE o = new COTIZACIONREATYPE();
		o.setAno(String.valueOf(year));
		o.setMes(completeLength(String.valueOf(month), 2,false));
		o.setGrupoCotizacion(quoteGroup!=null?quoteGroup:null);
		o.setNumDiasCotizados(contributionDays!=null?completeLength(contributionDays.toString(), 2,false):null);
		o.setNumJornadasCotizadas(journalDays!=null?completeLength(journalDays.toString(), 2,false):null);
		o.setBaseCotizacionDesempleo(completeLength(unemploymentContributionBase, 9,false));
		o.setObservaciones(createBigStringBasicType(comments));
		return o;
	}
	
	private List<COTIZACIONREATYPE> getCotizacionReaList(Certifica2BatchDetail detail) {
		SEPEUtils utils = SEPEUtils.getInstance();
		List<ISalary> salaryList = null;
		List<COTIZACIONREATYPE> cotizacionList = null;
		Integer totalDias = 0;
		Calendar calInicio = new GregorianCalendar();
		Calendar calFin = new GregorianCalendar();
		calInicio.setTime(detail.getContract().getStartDate());
		calFin.setTime(detail.getContract().getEndDate());
		calFin.set(Calendar.DAY_OF_MONTH, calFin.getActualMaximum(Calendar.DAY_OF_MONTH));
		cotizacionList = new ArrayList<COTIZACIONREATYPE>();
		List<ISalary> delayList;
		try {
			delayList = getSalaries(detail.getContract(), detail.getContract().getStartDate(), detail.getContract().getEndDate(), SalaryType.DELAY);
			while((calInicio.before(calFin) || calInicio.equals(calFin)) && totalDias < 180) {
				Calendar startDate = new GregorianCalendar();
				Calendar endDate = new GregorianCalendar();
				startDate.setTime(new Date(calFin.getTimeInMillis()));
				endDate.setTime(new Date(calFin.getTimeInMillis()));
				startDate.set(Calendar.DAY_OF_MONTH, 1);
				endDate.set(Calendar.DAY_OF_MONTH, startDate.getActualMaximum(Calendar.DAY_OF_MONTH));
				salaryList = getSalaries(detail.getContract(), startDate.getTime(), endDate.getTime());
				calFin.add(Calendar.DATE, -calFin.get(Calendar.DAY_OF_MONTH));
				for(ISalary salary: salaryList){
					Double baseCg = salary.getCommonBase();
					Double baseAcc = salary.getProfessionalBase();
					
					// obtener las bases de los atrasos de las nominas
					if(delayList.size()>0){
						baseCg += getDelayBaseAmount(delayList, startDate.getTime(), endDate.getTime(), ContextVariable.CGC_BASE);
						baseAcc += getDelayBaseAmount(delayList, startDate.getTime(), endDate.getTime(), ContextVariable.CGP_BASE);
					}
					
					List<SalaryData> journalDaysList = utils.getSalaryDataList(salary, salary.getStartDate(), salary.getEndDate(), "JORNADAS_REALES");
					Integer journalDays = null;
					try {
						journalDays = journalDaysList!=null && journalDaysList.size()>0 ? Integer.parseInt(journalDaysList.get(0).getExpression()) : null;
					} catch (NumberFormatException e) {
						LOGGER.error("No se han podido obtener las jornadas reales", e);
					}
					Integer quoteDays = null;
					if(journalDays==null || journalDays==0){
						quoteDays = salary.getTimeUnits();
					}
					
					totalDias += salary.getTimeUnits();
					Calendar cal = new GregorianCalendar();
					cal.setTime(salary.getEndDate());
					COTIZACIONREATYPE cotizacion = createCotizacionReaType( cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, 
							salary.getQuoteGroup(), quoteDays, journalDays, baseCg, baseAcc, null);
					cotizacionList.add(cotizacion);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Ha ocurrido un error al obtener datos de las nominas.";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
		return cotizacionList;
	}
	
	/**
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
		SEPEUtils utils = SEPEUtils.getInstance();
		try {
			List<ISalary> settleList = getSalaries(contract, null, null, SalaryType.SETTLE);
			for(ISalary settle: settleList){
				if(settle!=null){
					List<SalaryData> noHolidaysData = utils.getSalaryDataList(settle, settle.getStartDate(), settle.getEndDate(), ContextVariable.NO_HOLIDAYS.getName());
					Double noHolidays = 0.0;
					if(noHolidaysData!=null && noHolidaysData.size()>0){
						for(SalaryData data: noHolidaysData){
							if(data.getExpression()!=null && NumberUtils.isNumber(data.getExpression())){
								noHolidays += CommonUtil.ceil(Double.valueOf(data.getExpression()), 0);
							}
						}
					}
					if(noHolidays!=null && noHolidays.intValue()>0){
						o = new TRABAJADORTYPE.DatosVacacionesCotizadas();
						Double baseCg = getCommonBase(settle, utils.getSalaryDataList(settle, settle.getStartDate(), settle.getEndDate()));
						Double baseAcc = getProfessionalBase(settle, utils.getSalaryDataList(settle, settle.getStartDate(), settle.getEndDate()));
						
						// TODO: obtener las bases de los atrasos de finiquitos 
//					baseCg += getDelayBaseAmount(settleList, null, null, ContextVariable.CGC_BASE);
//					baseAcc += getDelayBaseAmount(settleList, null, null, ContextVariable.CGP_BASE);
						
						o.setNumDiasCotizados(completeLength(noHolidays.intValue(),3,false));
						o.setBaseCotizacionContingenciasComunes(completeLength(baseCg, 9,false));
						o.setBaseCotizacionDesempleo(completeLength(baseAcc, 9,false));
						o.setObservaciones(null);
					}
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener el finiquito de "+contract.getPerson().getFullName();
			AonUtil.addErrorMessage(msg);
		}
		return o;
	}
	
	private Double getCommonBase(ISalary salary, List<SalaryData> list) {
		Double commonBase = 0.0;
		for(SalaryData sd: list){
			if(sd.getName().equals(ContextVariable.CGC_BASE_ENTERPRISE.getName())){
				String _commonBase = sd.getExpression();
				if(_commonBase!=null && NumberUtils.isNumber(_commonBase)){
					commonBase += Double.parseDouble(_commonBase);
				}
			}
		}
		if(commonBase==0.0){
			commonBase = salary.getCommonBase();
		}
		return commonBase;
	}
	
	private Double getProfessionalBase(ISalary salary, List<SalaryData> list) {
		Double profBase = 0.0;
		for(SalaryData sd: list){
			if(sd.getName().equals(ContextVariable.CGP_BASE_ENTERPRISE.getName())){
				String _profBase = sd.getExpression();
				if(_profBase!=null && NumberUtils.isNumber(_profBase)){
					profBase += Double.parseDouble(_profBase);
				}
			}
		}
		if(profBase==0.0){
			profBase = salary.getProfessionalBase();
		}
		return profBase;
	}
	
	/**
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
	private TRABAJADORTYPE.DatosVacacionesCotizadasREA createVacacionesCotizadasReaType(Contract contract){
		// TODO createVacacionesCotizadasReaType()
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
	private String createNombreSimpleType(String value){
		value = StringUtils.trim(value);
		if(StringUtils.isNotBlank(value) && StringUtils.length(value)>14 ){
			return StringUtils.substring(value, 0, 14);
		}
		return value;
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
	private String createApellidoSimpleType(String value){
		value = StringUtils.trim(value);
		if(StringUtils.isNotBlank(value) && StringUtils.length(value)>19 ){
			return StringUtils.substring(value, 0, 19);
		}
		return value;
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
		if(date!=null){
			String pattern = "yyyyMMdd";
			return DateFormatUtils.format(date, pattern);
		}
		return null;
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
	private String createDiasCotizReaSimpleType(Integer days){
		return completeLength(days, 2);
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
	private String createJornCotizReaSimpleType(Integer days){
		return completeLength(days,  2);
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
		return "00";
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
		if(value==null)
			return null;
		return value.length()>50?value.substring(0, 10):value;
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
		if(value==null)
			return null;
		return value.length()>50?value.substring(0, 50):value;
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
	
	private RegistryDirStaff getRegistryDirStaff(Enterprise enterprise) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(RegistryDirStaff.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REGISTRY_ID), enterprise.getId());
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE), new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE));
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
			SEPEUtils utils = SEPEUtils.getInstance();
			utils.completeChildDomainCriteria(criteria, bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DOMAIN));
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
		SEPEUtils utils = SEPEUtils.getInstance();
		String tc2 = utils.getContractDataMap(detail.getContract(), Boolean.FALSE, Boolean.TRUE).get(ContextVariable.TC2.getName());
		if(tc2.startsWith("1") || tc2.startsWith("4")){ 
			return true;
		}
		if(tc2.startsWith("2") || tc2.startsWith("5")){
			return false;
		}
		return false;
	}
	
	private boolean isIrregular(Contract contract, Period p) {
		SEPEUtils utils = SEPEUtils.getInstance();
		ContractData cd = utils.getContractDataMap(contract, p.getStart(), p.getEnd(), Boolean.TRUE).get(ContextVariable.IRREGULAR.getName());
		return (cd!=null && new Boolean(cd.getExpression()));
	}
	
	private Double getDelayBaseAmount(List<ISalary> delayList, Date startDate, Date endDate, ContextVariable baseName) {
		SEPEUtils utils = SEPEUtils.getInstance();
		try {
			Double amount = 0.0;
			if(baseName!=null){
				for(ISalary delay: delayList){
					for(SalaryData data: utils.getSalaryDataList((Salary) delay, startDate, endDate, baseName.getName())){
						amount += Double.parseDouble(data.getExpression());
					}
				}
			}
			return amount;
		} catch (ManagerBeanException e) {
			String msg = "Ha ocurrido un error al obtener la base '" + baseName.getName();
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
		}
		return null;
	}

	public List<ISalary> getSalaries(Contract contract, Date startDate, Date endDate) throws ManagerBeanException {
		return getSalaries(contract, startDate, endDate, SalaryType.SALARY);
	}
	
	public List<ISalary> getSalaries(Contract contract, Date startDate, Date endDate, SalaryType type) throws ManagerBeanException {
		SEPEUtils utils = SEPEUtils.getInstance();
		IManagerBean bean = BeanManager.getManagerBean(Salary.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_CONTRACT_ID), contract.getId());
		if(type!=null){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_TYPE), type);
		} else {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_TYPE), SalaryType.SALARY);
		}
		if(startDate != null){
			Calendar startCal = Calendar.getInstance();
			startCal.setTime(startDate);
			startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMinimum(Calendar.DAY_OF_MONTH));
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.SALARY_START_DATE), startCal.getTime());
		}
		if(endDate != null){
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(endDate);
			endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.SALARY_END_DATE), endCal.getTime());
		}
		utils.completeChildDomainCriteria(criteria, bean.getFieldName(IEntityAlias.SALARY_DOMAIN));
		criteria.addOrder(bean.getFieldName(IEntityAlias.SALARY_END_DATE), false);
		List<ISalary> list = new LinkedList<ISalary>();
		for(ITransferObject to: bean.getList(criteria)){
			list.add((ISalary) to);
		}
		return list;
	}
	
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
	
	private String completeLength(Integer var, Integer lon) {
		return completeLength(var, lon, true);
	}
	
	private String completeLength(Double var, Integer lon, Boolean dir) {
		var *= 100;
		var = CommonUtil.round(var);
		return completeLength(String.valueOf(var.intValue()), lon, dir);
	}
	
	private Date minBetweenDates(Date from, Date to) {		
		return ( from.before(to)) ? from: to;
	}

	private Date maxBetweenDates(Date from, Date to) {		
		return ( from.after(to)) ? from: to;
	}

	private Integer differenceBetweenDates(Date from, Date to) {
		Integer diffDays = new Integer(0);
		final Double MS_PER_DAY = new Double(1000 * 60 * 60 * 24);
		if(from.equals(to)) {
			return 1;
		} else if(!from.after(to)) {
			diffDays = (int)((Math.floor((to.getTime() - from.getTime()) / MS_PER_DAY + 0.5d) + 1));
		}
		return diffDays;
	}
	
	private static long days( Date startDate, Date endDate ) {
		return new Period(startDate, endDate).daysStream().count();
	}
	
}

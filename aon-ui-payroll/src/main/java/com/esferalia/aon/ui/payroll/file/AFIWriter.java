package com.esferalia.aon.ui.payroll.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.afi.AFI;
import com.esferalia.aon.file.payroll.afi.data.AYN;
import com.esferalia.aon.file.payroll.afi.data.EMP;
import com.esferalia.aon.file.payroll.afi.data.ETF;
import com.esferalia.aon.file.payroll.afi.data.ETI;
import com.esferalia.aon.file.payroll.afi.data.FAB;
import com.esferalia.aon.file.payroll.afi.data.FCT;
import com.esferalia.aon.file.payroll.afi.data.RZS;
import com.esferalia.aon.file.payroll.afi.data.TRA;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractBatchDetail;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.enumeration.AfiActionType;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.enumeration.ss.T21;
import com.esferalia.aon.payroll.enumeration.ss.T41;
import com.esferalia.aon.payroll.util.PayrollUtils;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;

public class AFIWriter implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final String AFI 				= "AFI";
	private final String WINSUITE_VERSION 	= "71WSxxx";
	private final String TESTING_CHECK		= "P";
	
	private final String WHITESPACE_1  = " ";
	private final String WHITESPACE_2  = "  ";
	private final String WHITESPACE_3  = "   ";
	private final String WHITESPACE_15 = "               ";
	private final String WHITESPACE_20 = "                    ";

	
	private ETI eti;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
	public ETI getEti() {
		return eti;
	}
	public void setEti(ETI eti) {
		this.eti = eti;
	}

	public FileOutput createAFI(List<ContractBatchDetail> list ) throws ManagerBeanException {
		try {
			ETI eti = createETIRecord( isAfiTestEnvironmentActive(), list );
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			PrintWriter writer = new PrintWriter(outputStream);
			FileFiller afi = new AFI(eti, writer);
			FileOutput output = new FileOutput();
			output.setErrors(afi.create());
			output.setContent(outputStream.toByteArray());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	private ETI createETIRecord( boolean testFile, List<ContractBatchDetail> list ) throws ManagerBeanException {
		ETI eti = new ETI();
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String f =  formatter.format(date);
		eti.setFecha(Integer.parseInt(f));
		formatter = new SimpleDateFormat("HHmm");
		String t =  formatter.format(date);
		eti.setHora(Integer.parseInt(t));
		formatter = new SimpleDateFormat("ddHHmmss");
		eti.setFichero(formatter.format(date));
		
		eti.setIdentificador(AFI+WINSUITE_VERSION);
		String authorizationKey = getAuthorizationKey(DomainManager.getCurrentDomain());
		if(StringUtils.isBlank(authorizationKey)){
			authorizationKey = getAuthorizationKey(DomainManager.getDomainProvider().getParentDomain());
		}
		if(StringUtils.isNotBlank(authorizationKey)){
			eti.setClave(StringUtils.leftPad(authorizationKey, 8, '0'));
		} else {
			AonUtil.addErrorMessage("No se ha definido la clave de autorización.");
		}
		eti.setPrueba( testFile?TESTING_CHECK:WHITESPACE_1 );
		
		list = list.stream()
				.sorted((d1, d2) -> d1.getContract().getEnterpriseCCC().getId().compareTo(d2.getContract().getEnterpriseCCC().getId()))
				.collect(Collectors.toList());
		
		EMP emp = null;
		EnterpriseCCC previousCcc = null;
		for (ContractBatchDetail detail: list) {
			if(emp==null || !detail.getContract().getEnterpriseCCC().getId().equals(previousCcc.getId())){
				previousCcc = detail.getContract().getEnterpriseCCC();
				emp = createEMPrecord(previousCcc, list);
				eti.getEmpresas().add(emp);
			}
			fillTRARecords(emp, detail);
		}
		setEti( eti );
		createETFRecord( eti );
		return getEti();
	}

	private void createETFRecord( ETI eti ) throws ManagerBeanException {
		ETF etf = new ETF();
		etf.setIdentificador(eti.getIdentificador());
		etf.setClave(eti.getClave());
		etf.setPrueba(eti.getPrueba());
		etf.setFichero(eti.getFichero());
		etf.setFecha(eti.getFecha());
		etf.setHora(eti.getHora());
		eti.setEtf(etf);
	}
	
	private EMP createEMPrecord(EnterpriseCCC enterpriseCcc, List<ContractBatchDetail> list) throws  ManagerBeanException {
		EMP emp = new EMP();
		Enterprise enterprise = enterpriseCcc.getActivity().getEnterprise();
		emp.setCodigoCuentaCotizacionSeguridadSocial(PayrollUtils.getInstance().getRegimeCode(enterpriseCcc)+enterpriseCcc.getCcc());
		DocumentType docType = enterprise.getRegistry().getDocumentType();
    	if(docType==DocumentType.NIF){
    		emp.setTipoDocumento("1");
    	} else if(docType==DocumentType.PASSPORT){
    		emp.setTipoDocumento("2");
    	} else if(docType==DocumentType.NIE){
    		emp.setTipoDocumento("6");
    	} else if(docType==DocumentType.CIF){
    		emp.setTipoDocumento("9");
    	} else {
    		emp.setTipoDocumento("9");
    	}
		
		String pais = null;
		try {
			pais = String.valueOf(enterprise.getRegistry().getDocumentCountry().getIsoNum());
		} catch (NullPointerException e) {
		}			
		if (StringUtils.isBlank(pais)) {
			pais = WHITESPACE_3;
		}
		emp.setPais(pais);
		emp.setNumeroIdentificacion(autoComplete(enterprise.getRegistry().getDocument(), 14, "0", true));
		emp.setCalificador(WHITESPACE_2);
		emp.setCodigoCuentaCotizacionPrincipal(obtainMainCCC(enterprise));

		emp.setRzs(createRZSrecord(enterprise));
		emp.setAccion(WHITESPACE_3);
		return emp;
	}
	
	private RZS createRZSrecord(Enterprise enterprise) {
		RZS rzs = new RZS();
		rzs.setIndicador("0");
		/*
		TipoAlfabeticoEmpresario
		1 Individual
		2 Colectivo
		3 Sin personalidad jurídica
		4 Entidad u Organismo de las Admones.Públicas
		*/
		rzs.setTipoAlfabeticoEmpresario("1");
		rzs.setRazonSocial(FileUtils.obtainUtf(enterprise.getRegistry().getName()));
		return rzs;
	}
	
	private void fillTRARecords(EMP emp, ContractBatchDetail detail) {
		if(detail.getActionType()==AfiActionType.MA || detail.getActionType()==AfiActionType.MB){
			TRA tra = createTRARecord(detail);
			tra.setFab( createFABRecord(detail) );
			emp.getTrabajadores().add(tra);
		} else if(detail.getActionType()==AfiActionType.MHU){
			TRA tra = null;
			
			/*
			 *  segmento TRA para inicio huelga
			 */
			tra = createTRARecord(detail);
			tra.setFab( createFABStrikeRecord(detail, true) );
			emp.getTrabajadores().add(tra);
			
			/*
			 *  segmento TRA para fin huelga
			 */
			tra = createTRARecord(detail);
			tra.setFab( createFABStrikeRecord(detail, false) );
			emp.getTrabajadores().add(tra);
		}
	}
	
	private TRA createTRARecord(ContractBatchDetail detail) {
		TRA tra = new TRA();
		tra.setNumeroAfiliacion( detail.getContract().getPerson().getSocialSecurityNumber() ); 
		String tipo;
		if(detail.getContract().getPerson().getRegistry().getDocumentType()== DocumentType.NIF){
			tipo = "1";
		} else if(detail.getContract().getPerson().getRegistry().getDocumentType()== DocumentType.PASSPORT){
			tipo = "2";
		} else if(detail.getContract().getPerson().getRegistry().getDocumentType()== DocumentType.NIE){
			tipo = "6";
		} else {
			tipo = "1";
		}
		String pais = String.valueOf(detail.getContract().getPerson().getRegistry().getDocumentCountry().getIsoNum());
		String doc = autoComplete(detail.getContract().getPerson().getRegistry().getDocument(), 14, "0", true);
		String ipf = StringUtils.isBlank(tipo)?"9":tipo;
		ipf += StringUtils.isBlank(pais)?"   ":pais; 
		ipf += doc;
		tra.setIpf(ipf);
		tra.setAyn(createAYNRecord(detail.getContract()));
		tra.setFct(createFCTRecord(detail));
		return tra;
	}
	
	private AYN createAYNRecord(Contract contract) {
		AYN ayn = new AYN();
		String ap1 = FileUtils.obtainUtf(contract.getPerson().getFirstSurname());
		String ap2 = FileUtils.obtainUtf(contract.getPerson().getSecondSurname());
		String n = FileUtils.obtainUtf(contract.getPerson().getName());
		ayn.setApellido1(ap1!=null?ap1:WHITESPACE_20);
		ayn.setApellido2(ap2!=null?ap2:WHITESPACE_20);
		ayn.setNombre(n!=null?n:WHITESPACE_15);
		return ayn;
	}

	private FAB createFABRecord(ContractBatchDetail detail) {
		FAB fab = new FAB();
		fab.setAccion(autoComplete(detail.getActionType()==null?AfiActionType.MA.getValue():detail.getActionType().getValue(), 3, " ", false));
		
		if(detail.getActionType()==AfiActionType.MA){
			fab.setSituacion(autoComplete(T21.T21_1.getCode(), 2, "0", true));
			fab.setFechaReal(Integer.parseInt(dateFormatter.format(detail.getContract().getStartDate())));
		} else if(detail.getActionType()==AfiActionType.MB && detail.getLeaveType()!=null){
			fab.setSituacion(autoComplete(detail.getLeaveType()==null?"":detail.getLeaveType().getValue(), 2, "0", true));
			fab.setFechaReal(Integer.parseInt(dateFormatter.format(detail.getContract().getEndDate())));
		}
		
		Integer quoteGroup = getQuoteGroup(detail.getContract());
		if(quoteGroup!=null){
			fab.setGrupoCotizacion(quoteGroup);
		}
		// TODO indicativoGrupoCcotizacionDiario
		fab.setIndicativoGrupoCotizacionDiario(null);
		// TODO GradoDiscapacidad
		fab.setGradoDiscapacidad(null);
		fab.setClaveContrato(Integer.parseInt(getContractCode(detail.getContract()).getValue()));
		// TODO CondicionDesempleado
		fab.setCondicionDesempleado(null);
		// TODO MujerSubrepresentada
		fab.setMujerSubrepresentada(null);
		if( !fab.getClaveContrato().toString().startsWith("1") && !fab.getClaveContrato().toString().startsWith("4") ){
			Double weekHours = obtainWeekHours(detail.getContract());
			Double hoursPercent = weekHours * 2.5;
			int percent = (int)CommonUtil.ceil(hoursPercent, 0);
			String percentValue = autoComplete(String.valueOf(percent), 2, "0", true);
			fab.setCoeficienteTiempoParcial( autoComplete(percentValue, 3, "0", false) );
		}
		// TODO ColectivoTrabajador
		fab.setColectivoTrabajador(null);
		/*
		IndicadorImpresion. Sus posibles valores son: 
		Espacio=no impresión 
		S= Impresión resolución
		C= Impresión resolución + IDC 
		I=IDC
		*/
		fab.setIndicadorImpresion(WHITESPACE_1);
		if(PayrollUtils.getInstance().getRegimeCode(detail.getContract().getEnterpriseCCC()).equals("0911")
				|| detail.getContract().getEnterpriseCCC().getActivity().getType()==SSRegimeType.SEA_WORKERS){
			// TODO CategoriaProfesional
			fab.setCategoriaProfesional(null);
		}
		if(detail.getContract().getPerson().getBirthDate()!=null){
			fab.setFechaNacimiento(dateFormatter.format(detail.getContract().getPerson().getBirthDate()));
		}
		if(detail.getContract().getPerson().getGender()==Gender.MALE){
			fab.setSexo(1);
		} else if(detail.getContract().getPerson().getGender()==Gender.FEMALE){
			fab.setSexo(2);
		} else {
			fab.setSexo(1);
		}
		
		
		// TODO TipoInactividad
		fab.setTipoInactividad(null);
		// TODO ExclusionDesempleo
		fab.setExclusionDesempleo(null);
		// TODO CoeficienteActividadHuelgaParcialEre
		fab.setCoeficienteActividadHuelgaParcialEre(null);
		// TODO MujerReincorporada
		fab.setMujerReincorporada(null);
		// TODO IncapacitadoReadmitido
		fab.setIncapacitadoReadmitido(null);
		// TODO TrabajadorDeAutonomo
		fab.setTrabajadorDeAutonomo(null);
		// TODO SemamaSegunConvenio5jr
		fab.setSemamaSegunConvenio5jr(null);
		// TODO IndNumTrabajadoresEmpresa
		fab.setIndNumTrabajadoresEmpresa(null);
		// TODO ExclusionSocialVictimas
		fab.setExclusionSocialVictimas(null);
		// TODO RentaActivaInsercion
		fab.setRentaActivaInsercion(null);
		// TODO CostratadasPostAlumbramiento
		fab.setCostratadasPostAlumbramiento(null);
		
		return fab;
	}
	
	private FAB createFABStrikeRecord(ContractBatchDetail detail, boolean isFirstDay) {
		FAB fab = new FAB();
		fab.setAccion(autoComplete(detail.getActionType().getValue(), 3, " ", false));
		fab.setFechaReal(Integer.parseInt(dateFormatter.format(detail.getRealDate())));
		if(isFirstDay) {			
			Double strikeFactor = obtainStrikeFactor(detail.getContract(), detail.getRealDate());
			if(strikeFactor<1.0)
				fab.setTipoInactividad(T41.T41_3.getCode());
			else
				fab.setTipoInactividad(T41.T41_2.getCode());
			fab.setCoeficienteActividadHuelgaParcialEre((int)(strikeFactor*1000));
		}
		return fab;
	}
	
	

	private FCT createFCTRecord(ContractBatchDetail detail) {
		FCT fct = null;
		if(detail.getActionType()==AfiActionType.MB){
			SEPEUtils utils = SEPEUtils.getInstance();
			Double noHolidays = 0.0;
			try {
				List<ISalary> settleList = getSalaries(detail.getContract(), null, null, SalaryType.SETTLE);
				for(ISalary settle: settleList){
					if(settle!=null){
						List<SalaryData> noHolidaysData = utils.getSalaryDataList(settle, settle.getStartDate(), settle.getEndDate(), ContextVariable.NO_HOLIDAYS.getName());
						if(noHolidaysData!=null && noHolidaysData.size()>0){
							for(SalaryData data: noHolidaysData){
								if(data.getExpression()!=null && NumberUtils.isNumber(data.getExpression())){
									noHolidays += CommonUtil.ceil(Double.valueOf(data.getExpression()), 0);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(noHolidays!=null && noHolidays>0){
				fct = new FCT();
				Date finalEndDate = DateUtils.addDays(detail.getContract().getEndDate(), noHolidays.intValue());
				fct.setFechaFinVacaciones(dateFormatter.format(finalEndDate));
			}
		}
		return fct;
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
	
	private Double obtainWeekHours(Contract contract) {
		String[] hourList = {
				SEPEUtils.getInstance().getContractDataMap(contract, false, true).getOrDefault(ContextVariable.MONDAY_HOURS.getName(), ""),
				SEPEUtils.getInstance().getContractDataMap(contract, false, true).getOrDefault(ContextVariable.TUESDAY_HOURS.getName(), ""),
				SEPEUtils.getInstance().getContractDataMap(contract, false, true).getOrDefault(ContextVariable.WEDNESDAY_HOURS.getName(), ""),
				SEPEUtils.getInstance().getContractDataMap(contract, false, true).getOrDefault(ContextVariable.THURSDAY_HOURS.getName(), ""),
				SEPEUtils.getInstance().getContractDataMap(contract, false, true).getOrDefault(ContextVariable.FRIDAY_HOURS.getName(), ""),
				SEPEUtils.getInstance().getContractDataMap(contract, false, true).getOrDefault(ContextVariable.SATURDAY_HOURS.getName(), ""),
				SEPEUtils.getInstance().getContractDataMap(contract, false, true).getOrDefault(ContextVariable.SUNDAY_HOURS.getName(), "")
		};
		Double weekHours = 0.0;
		for(String value: hourList){
			if(NumberUtils.isNumber(value)){
				weekHours += Double.valueOf(value);
			}
		}
		return weekHours;
	}
	
	private List<Enterprise> getEnterprises(List<ContractBatchDetail> list){
		List<Enterprise> enterprises = new LinkedList<Enterprise>();
		for(ContractBatchDetail c: list){
			if(!enterprises.contains(c.getContract().getWorkPlace().getEnterprise())){
				enterprises.add(c.getContract().getWorkPlace().getEnterprise());
			}
		}
		return enterprises;
	}
	
	private List<Contract> getContracts(Enterprise enterprise, List<Contract> contractList) throws ManagerBeanException{
		List<Contract> list = new LinkedList<Contract>();
		for(Contract c: contractList){
			if(c.getWorkPlace().getEnterprise().equals(enterprise)){
				list.add(c);
			}
		}
		return list;
	}

	private String autoComplete(String value, int lenght, String completeValue, boolean leftSide) {
		while( value.length() < lenght ){
			value = leftSide ? (completeValue + value) : (value + completeValue);
		}
		return value;
	}
	
	private String obtainMainCCC(Enterprise enterprise) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(EnterpriseCCC.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_ACTIVITY_ENTERPRISE_ID), enterprise.getId() );
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_TYPE), CCCType.PRINCIPAL );
			List<ITransferObject> cccList = bean.getList(criteria);
			if ( !cccList.isEmpty() ) {
				EnterpriseCCC ccc = (EnterpriseCCC) cccList.get(0);	
				return PayrollUtils.getInstance().getRegimeCode(ccc)+ccc.getCcc();	
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
		return null;
	}

	private Integer getQuoteGroup(Contract contract) {
		String q = SEPEUtils.getInstance().getContractDataMap(contract, false, true).get(ContextVariable.QUOTE_GROUP.getName());
		return q!=null && !q.isEmpty()?Integer.parseInt(q):null;
	}
	private ContractCode getContractCode(Contract contract) {
		String tc2 = SEPEUtils.getInstance().getContractDataMap(contract, false, true).get(ContextVariable.TC2.getName());
		return ContractCode.getContractCodeByValue(tc2);
	}
	
	private Double obtainStrikeFactor(Contract contract, Date date) {
		Double strikeFactor = 0.0;
		try {
			Criteria criteria = new Criteria();
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME), ContextVariable.STRIKE_FACTOR.getName());
			criteria.addNotNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_EXPRESSION));
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), date);
			
			List<ITransferObject> list = bean.getList(criteria);
			if(list!=null && !list.isEmpty())
				strikeFactor = Double.valueOf(((ContractData)list.get(0)).getExpression());	
		} catch (ManagerBeanException e) {
			// NADA
		}
		return strikeFactor;
	}
	
	private boolean isAfiTestEnvironmentActive() {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT value FROM app_param";
			select += " WHERE domain = " + DomainManager.getCurrentDomain();
			select += " AND name = '" + AppParam.PAY_afi_test_env_PAY.getValue() + "';";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return rs.getBoolean(1);
			}
		} catch (AonConnectionException e) {
			// return null
		} catch (SQLException e) {
			// return null
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return false;
	}
	
	private String getAuthorizationKey(Integer domain) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT value FROM app_param";
			select += " WHERE domain = " + domain;
			select += " AND name = '" + AppParam.PAY_authorization_key_PAY.getValue() + "';";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString(1);
			}
		} catch (AonConnectionException e) {
			// return null
		} catch (SQLException e) {
			// return null
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return null;
	}
	
}

package com.esferalia.aon.ui.payroll.file;

import java.io.File;
import java.io.IOException;
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

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.fan.FAN;
import com.esferalia.aon.file.payroll.fan.data.AYN;
import com.esferalia.aon.file.payroll.fan.data.DAT;
import com.esferalia.aon.file.payroll.fan.data.EDL;
import com.esferalia.aon.file.payroll.fan.data.EDT;
import com.esferalia.aon.file.payroll.fan.data.EMP;
import com.esferalia.aon.file.payroll.fan.data.ETI;
import com.esferalia.aon.file.payroll.fan.data.MPG;
import com.esferalia.aon.file.payroll.fan.data.RZS;
import com.esferalia.aon.file.payroll.fan.data.TCT;
import com.esferalia.aon.file.payroll.fan.data.TRA;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractBonus;
import com.esferalia.aon.payroll.ContractLeave;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBonus;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.LiquidationType;
import com.esferalia.aon.payroll.enumeration.Mutual;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.controller.PayrollAppParamsController;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

public class FANWriter implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final String TESTING_CHECK		= "P";
	/* Clave proporcionada por la seguridad social */
	private final Integer SS_KEY			= 12345678;
	
	private final String  VIRGULILLA = "~";
	
	private final String WHITESPACE_1  = " ";
	private final String WHITESPACE_2  = "  ";
	private final String WHITESPACE_3  = "   ";
	private final String WHITESPACE_15 = "               ";
	private final String WHITESPACE_20 = "                    ";
	
	private ETI eti;
	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");

	private Integer year;
	private Month startMonth; 
	private Month endMonth; 
	private LiquidationType liquidationType;
	
	private int totalContractSum = 0;
	
	private Date getStartDate(){
		Calendar cal = Calendar.getInstance();
		cal.set(year, startMonth.ordinal(), 1, 0, 0, 0);
		return cal.getTime(); 
	}
	private Date getEndDate(){
		Calendar cal = Calendar.getInstance();
		cal.set(year, endMonth.ordinal(), 1, 23, 59, 59);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}
	
	public ETI getEti() {
		return eti;
	}
	public void setEti(ETI eti) {
		this.eti = eti;
	}

	public FileOutput createFAN(boolean testFile, List<EnterpriseCCC> list, LiquidationType liquidationType, Integer year, Month startMonth, Month endMonth ) throws ManagerBeanException {
		try {
			ETI eti = createETIRecord( isFanTestEnvironmentActive(), list, liquidationType, year, startMonth, endMonth );
			File file = File.createTempFile("temp", ".FAN");
			FileFiller fan = new FAN(eti, file.getAbsolutePath());
			FileOutput output = new FileOutput();
			output.setFile(file);
			output.setErrors(fan.create());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	public ETI createETIRecord(boolean testFile, List<EnterpriseCCC> list, LiquidationType liquidationType, Integer year, Month startMonth, Month endMonth ) throws ManagerBeanException {
		ETI eti = new ETI();
		this.year = year;
		this.startMonth = startMonth; 
		this.endMonth= endMonth ; 
		this.liquidationType = liquidationType;
		this.totalContractSum = 0;
		
		eti.setClave(SS_KEY);
		eti.setPrueba(testFile?TESTING_CHECK:WHITESPACE_1);
		for (EnterpriseCCC ccc: list) {
			EMP emp = createEMPrecord(ccc);
			if(emp!=null){
				eti.getEmpresas().add(emp);
			}
		}
		setEti( eti );
		return getEti();
	}
	
	private EMP createEMPrecord(EnterpriseCCC ccc) throws  ManagerBeanException {
		EMP emp = new EMP();
		emp.setCodigoCuentaCotizacionSeguridadSocial(ccc.getFullCcc());
    	
    	DocumentType docType = ccc.getActivity().getEnterprise().getRegistry().getDocumentType();
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
    	emp.setPais(WHITESPACE_3);
    	emp.setNumeroIdentificacion(autoComplete(ccc.getActivity().getEnterprise().getRegistry().getDocument(), 14, "0", true));
    	
    	
    	
    	
    	emp.setCalificador(WHITESPACE_2);
		emp.setCodigoCuentaCotizacionPrincipal(obtainMainCCC(ccc).getFullCcc());
		emp.setAnio(year);
		emp.setDesdeMes(startMonth.ordinal()+1);
		emp.setHastaMes(endMonth.ordinal()+1);
		emp.setCalificadorLiquidacion(liquidationType.getValue());
		//TODO: Clase de liquidacion
//		00 - solicitud cuota total 
//		01 - cuota de la aportacion del trabajador
		emp.setClaseLiquidacion(0);
		emp.setRzs(createRZSrecord(ccc.getActivity().getEnterprise()));
		
		List<ITransferObject> list = obtainContracts(ccc, getStartDate(), getEndDate());
		if(!list.isEmpty()){
			for(ITransferObject to: list){
				Contract contract = (Contract) to;
				if(getSalary(contract)!=null && contract.getRegimeType()!=SSRegimeType.SELF_EMPLOYED){
					TRA tra = createTRARecord(contract, emp);
					emp.getTrabajadores().add(tra);
					++totalContractSum;
				}
			}
			emp.getTcTotales().add(createTCTRecord(ccc));
			createEDTRecords(ccc, emp, list);
		
			emp.setMpg(createMPGRecord(ccc));
			return emp;
		}
		return null;
	}
	
	private EnterpriseCCC obtainMainCCC(EnterpriseCCC ccc) {
		if(ccc.getType()==CCCType.PRINCIPAL){
			return ccc;
		}
		try {
			IManagerBean bean = BeanManager.getManagerBean(EnterpriseCCC.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_ACTIVITY_ID), ccc.getActivity().getId() );
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_GEOZONE_ID), ccc.getGeozone().getId() );
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_TYPE), CCCType.PRINCIPAL );
			List<ITransferObject> cccList = bean.getList(criteria);
			if ( !cccList.isEmpty() ) {
				return (EnterpriseCCC) cccList.get(0);	
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
		return ccc;
	}
	
	private RegistryDirStaff obtainDirStaff(Enterprise enterprise){
		try {
			IManagerBean bean = BeanManager.getManagerBean(RegistryDirStaff.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REGISTRY_ID), enterprise.getRegistry().getId() );
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE), new Date() );
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REPRESENTATIVE_LABOR), true );
			List<ITransferObject> dirStaffList = bean.getList(criteria);
			if (! dirStaffList.isEmpty() ) {
				return (RegistryDirStaff) dirStaffList.get(0);	
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
		return null;
	}
	
	private String autoComplete(String value, int lenght, String completeValue, boolean leftSide) {
		while( value.length() < lenght ){
			value = leftSide ? (completeValue + value) : (value + completeValue);
		}
		return value;
	}
	private RZS createRZSrecord(Enterprise enterprise) {
		RZS rzs = new RZS();
		
		/*
		TipoAlfabeticoEmpresario
		1 Individual
		2 Colectivo
		3 Sin personalidad jurídica
		4 Entidad u Organismo de las Admones.Públicas
		*/
		rzs.setTipoAlfabeticoEmpresario("1");
		rzs.setRazonSocial(enterprise.getRegistry().getName());
		return rzs;
	}
	
	/**
	 * TRA - TRAbajador
	 * 
	 * @param contract
	 * @param emp
	 * @return
	 * @throws ManagerBeanException
	 */
	private TRA createTRARecord(Contract contract, EMP emp) throws ManagerBeanException {
		TRA tra = new TRA();
		tra.setNumeroAfiliacion( contract.getPerson().getSocialSecurityNumber() );
		String tipo;
		if(contract.getPerson().getRegistry().getDocumentType()== DocumentType.NIF){
			tipo = "1";
		} else if(contract.getPerson().getRegistry().getDocumentType()== DocumentType.PASSPORT){
			tipo = "2";
		} else if(contract.getPerson().getRegistry().getDocumentType()== DocumentType.NIE){
			tipo = "6";
		} else {
			tipo = "1";
		}
//		String pais = contract.getPerson().getRegistry().getDocumentCountry().getIso3();
		String pais = WHITESPACE_3;
		String doc = autoComplete(contract.getPerson().getRegistry().getDocument(), 14, "0", true);
		String ipf = StringUtils.isBlank(tipo)?"9":tipo;
		ipf += StringUtils.isBlank(pais)?WHITESPACE_3:pais; 
		ipf += doc;
		tra.setIpf(ipf);
		tra.setAyn(createAYNRecord(contract));
		tra.setDat(createDATRecords(contract));
		return tra;
	}
	
	
	/**
	 * AYN - Apellidos Y Nombre
	 * 
	 * @param contract
	 * @return
	 */
	private AYN createAYNRecord(Contract contract) {
		AYN ayn = new AYN();
		String ap1 = contract.getPerson().getFirstSurname();
		String ap2 = contract.getPerson().getSecondSurname();
		String n = contract.getPerson().getName();
		ayn.setPrimerApellido(ap1!=null?ap1:WHITESPACE_20);
		ayn.setSegundoApellido(ap2!=null?ap2:WHITESPACE_20);
		ayn.setNombre(n!=null?n:WHITESPACE_15);
		String abbrv = "";
		abbrv += (ap1!=null&&ap1.length()>0)?ap1.charAt(0):" ";
		abbrv += (ap1!=null&&ap1.length()>1)?ap1.charAt(1):" ";
		abbrv += (ap2!=null&&ap2.length()>0)?ap2.charAt(0):" ";
		abbrv += (ap2!=null&&ap2.length()>1)?ap2.charAt(1):" ";
		abbrv += (n!=null&&n.length()>0)?n.charAt(0):" ";
		ayn.setAbreviado(abbrv);
		return ayn;
	}
	
	private List<DAT> createDATRecords(Contract contract) throws ManagerBeanException {
		List<DAT> datList = new LinkedList<DAT>();
		
		datList.add(createDATRecord(contract, null, getContractDaysOrHours(contract)));
		
		// TODO comprobar que situaciones implican un nuevo segmento de tipo DAT
		if(isPartialStrike(contract)){
			datList.add(createDATRecord(contract, autoComplete("H", 1, " ", true), getContractDaysOrHours(contract)));
		}
		if(isMoonlighting(contract)){
			datList.add(createDATRecord(contract, autoComplete("P", 2, " ", true), getContractDaysOrHours(contract)));
		}
		if(StringUtils.isNotBlank(getJournalReduction(contract))){
			Integer itDays = getItDays(contract);
			String code = getContractCode(contract).getValue();
			if(!code.startsWith("1") && !code.startsWith("4")){
				String weekHours = SEPEUtils.getInstance().getContractDataMap(contract, false, true).get(ContextVariable.WEEK_HOURS.getName());
				Double dayHours = (Double.parseDouble(weekHours)/5);
				itDays = Double.valueOf(CommonUtil.round(itDays * dayHours, 0)).intValue();
			}
			datList.add(createDATRecord(contract, autoComplete(getJournalReduction(contract), 3, " ", true), itDays));
		}
		if(isMonthSalary(contract)){
			datList.add(createDATRecord(contract, autoComplete("M", 4, " ", true), getContractDaysOrHours(contract)));
		}
		if(isNoRetributionDischarge(contract)){
			datList.add(createDATRecord(contract, autoComplete("A", 5, " ", true), getContractDaysOrHours(contract)));
		}
		if(StringUtils.isNotBlank(getOthers(contract))){
			datList.add(createDATRecord(contract, autoComplete(getOthers(contract), 6, " ", true), getContractDaysOrHours(contract)));
		}
		if(isLessThan7DaysContract(contract)){
			datList.add(createDATRecord(contract, autoComplete("C", 7, " ", true), getContractDaysOrHours(contract)));
		}
		
		return datList;
	}
	
	/**
	 * DAT - DATos del trabajador y periodo
	 * 
	 * @param contract
	 * @return
	 * @throws ManagerBeanException
	 */
	private DAT createDATRecord(Contract contract, String indicadosPerfil, Integer diasHoras) {
		DAT dat = new DAT();
		dat.setMes(endMonth.ordinal()+1);
		dat.setIndicadoresPerfil(indicadosPerfil);
		dat.setDiasHoras(diasHoras);
		dat.setDiasAlta(getContractDischargeDays(contract));
		dat.setIndicadorCotizacion(getQuoteIndicator(contract));
		dat.setIndicadorVacaciones(getVacationIndicator(contract));
		dat.setClaveJornadasColectivo(getCollectiveJournalHours(contract));
		dat.setEspecificos(getSpecifics(contract));
		dat.setIndReduccionBoni(getBonificationReduction(contract));
		dat.setGrupoCotizacion(getQuoteGroup(contract));
		dat.setTipoContrato(getSourceContractType(contract));
		dat.setClaveContrato(getContractCode(contract).getValue());
		dat.setEpigrafeAtEp(getAtEpEpigraph(contract));
		dat.setEpigrafeSecundario(getSecondariEpigraph(contract));
		dat.setOcupacion(getContractOccupation(contract));
		dat.setModalidadCotizacion(getQuoteMode(contract));
		dat.setIndDiscapacidad(getHandicapIndicator(contract));
		dat.setIndRelacion(getEmploymentRelation(contract));
		dat.setColectivoPeculiar(getParticularGroup(contract));
		dat.setInfoComplementaria(null);
		dat.setCotizacionDesempleo(null);
		
		createEDLRecords(contract, dat);
		
		return dat;
	}

	private List<ITransferObject> getContractLeaves(Contract contract, Date startDate, Date endDate) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractLeave.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_CONTRACT_ID), contract.getId());
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_START_DATE), endDate);
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_START_DATE), startDate);
//			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_END_DATE), startDate);
//			Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_END_DATE));
//			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
			criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_START_DATE));
			return bean.getList(criteria);
		} catch (ManagerBeanException e) {
			// NADA
		}
		return null;
	}
	
	private Double getItDailyBase(Contract contract){
		Double base = null;
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT daily_reg_base FROM contract_leave";
			select += " WHERE contract = " + contract.getId();
			select += " AND start_date <= '" + dateFormatter.format(getEndDate()) + "'";
			select += " AND (end_date >= '" + dateFormatter.format(getStartDate()) + "' OR end_date is null);";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				base =  rs.getDouble(1);
			}
		} catch (AonConnectionException e) {
			// return null
		} catch (SQLException e) {
			// return null
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		if(base!=null){
			return base; 
		}
		return null;
	}

	private Integer getItDays(Contract contract){
		return getItDays(contract, 0);
	}
	
	private Integer getItDays(Contract contract, Integer startIncrease){
		Date startDate = null;
		Date endDate = null;
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT start_date, end_date FROM contract_leave";
			select += " WHERE contract = " + contract.getId();
			select += " AND start_date <= '" + dateFormatter.format(getEndDate()) + "'";
			select += " AND (end_date >= '" + dateFormatter.format(getStartDate()) + "' OR end_date is null);";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				Calendar cal = Calendar.getInstance();
				if(rs.getDate(1)!=null){
					cal.setTime(rs.getDate(1));
					startDate = cal.getTime();
				}
				if(rs.getDate(2)!=null){
					cal.setTime(rs.getDate(2));
					endDate = cal.getTime();
				}
				if(startDate!=null && startIncrease!=null && startIncrease>0){
					Calendar start = Calendar.getInstance();
					start.setTime(startDate);
					start.add(Calendar.DAY_OF_MONTH, startIncrease);
					startDate = start.getTime();
				}
				
				if(startDate.before(getStartDate())) startDate = getStartDate();
				if(endDate == null || endDate.after(getEndDate())) endDate = getEndDate();
				
				int days = Integer.parseInt(String.valueOf(CommonUtil.getDaysBetweenDates(startDate, endDate, false)));
				return days>=0?days+1:0;
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
	
	private Integer getIt1MoreDays(Contract contract) {
		return getItDays(contract, 1);
	}
	private Integer getIt4To15MoreDays(Contract contract) {
		return getItDays(contract, 4) - getItDays(contract, 15);
	}
	private Integer getIt16To20MoreDays(Contract contract) {
		return getItDays(contract, 16) - getItDays(contract, 21);
	}
	private Integer getIt21MoreDays(Contract contract) {
		return getItDays(contract, 21);
	}
	
	private void createEDLRecords(Contract contract, DAT dat) {
		Salary salary = getSalary(contract);
		if(salary!=null){
				
			List<ITransferObject> leaveList = getContractLeaves(contract, getStartDate(), getEndDate());
			if(dat.getIndicadoresPerfil()==null || !dat.getIndicadoresPerfil().contains("I")){
				if(leaveList==null || leaveList.isEmpty()){
					createEDLBa01Segment(salary.getCommonBase(), dat);
					createEDLBa02Segment(salary.getProfessionalBase(), dat);
				} else {
					Double itBase = getItDailyBase(salary.getContract()) * getItDays(salary.getContract());
					
					createEDLBa01Segment(salary.getCommonBase() - itBase, dat);
					createEDLBa02Segment(salary.getProfessionalBase() - itBase, dat);
				}
					
					
				createEDLBa05Segment(salary, dat);
				createEDLBa06Segment(salary, dat);
				createEDLBa07Segment(salary, dat);
				createEDLBa08Segment(salary, dat);
				createEDLBa09Segment(salary, dat);
				createEDLBa10Segment(salary, dat);
				createEDLBa11Segment(salary, dat);
				createEDLBa20Segment(salary, dat);
				createEDLBa21Segment(salary, dat);
				createEDLBa22Segment(salary, dat);
				createEDLBa23Segment(salary, dat);
				createEDLBa28Segment(salary, dat);
				createEDLBa30Segment(salary, dat);
				createEDLBa31Segment(salary, dat);
				createEDLBa32Segment(salary, dat);
				createEDLBa33Segment(salary, dat);
				createEDLBa34Segment(salary, dat);
				createEDLBa35Segment(salary, dat);
				createEDLBa36Segment(salary, dat);
				createEDLBa37Segment(salary, dat);
				createEDLBa38Segment(salary, dat);
				createEDLBa41Segment(salary, dat);
				createEDLBa42Segment(salary, dat);
				
				List<ITransferObject> bonusList = getSalaryBonuses(salary);
				if (bonusList!=null && !bonusList.isEmpty()) {
					SalaryBonus bonus = null;
					BonusType bonusType = null;
					for(ITransferObject to: bonusList){
						bonus = (SalaryBonus) to;
						bonusType = obtainBonusType(bonus);
						if(bonus!=null && bonusType != null){
							if(bonusType==BonusType.SOCIAL_SECURITY){
								createEDLCd07Segment(bonus, dat);
							} else if(bonusType==BonusType.EMPLOYMENT_PROMOTION){
								createEDLCd22Segment(bonus, dat);
							} else if(bonusType==BonusType.CEUTA_MELILLA){
								createEDLCd20Segment(bonus, dat);
							} else if(bonusType==BonusType.HANDICAP){
								createEDLCd13Segment(bonus, dat);
							} else if(bonusType==BonusType.LAW_19_94){
								createEDLCd12Segment(bonus, dat);
							} else if(bonusType==BonusType.DISTANCE_FORMATION){
								createEDLCd11Segment(bonus, dat);
							} else if(bonusType==BonusType.CLASSROOM_FORMATION){
								createEDLCd10Segment(bonus, dat);
							} else if(bonusType==BonusType.ERE){
								createEDLCd28Segment(bonus, dat);
							} else if(bonusType==BonusType.ENCOURAGED_INDUSTRIAL_SECTOR){
								createEDLCd23Segment(bonus, dat);
							} else if(bonusType==BonusType.GT_60){
							} else if(bonusType==BonusType.EXEMPTION_GT30_CHILD){
								createEDLCd25Segment(bonus, dat);
							} else if(bonusType==BonusType.REDUCTION_RIGHT_CONTRACT){
								createEDLCd06Segment(bonus, dat);
							} else if(bonusType==BonusType.REDUCTION_COMMON_CONTINGENCY_EXCEPT_IT){
								createEDLCd17Segment(bonus, dat);
							} else if(bonusType==BonusType.REDUCTION_FARMER_COMMON_CONTINGENCY){
								createEDLCd29Segment(bonus, dat);
							} else if(bonusType==BonusType.REDUCTION_FARMER_UNEMPLOYMENT){
								createEDLCd30Segment(bonus, dat);
							}
						} else if(bonus!=null && bonusType==null) {
							createEDLCd07Segment(bonus, dat);
						}
					}
				}
					
			} else {				
				if (leaveList!=null && !leaveList.isEmpty()) {
					ContractLeave it = null;
					for(ITransferObject to: leaveList){
						Double itBase = null;
						it = (ContractLeave) to;
						if(it.getType()==LeaveType.COMMON_DISEASE || it.getType()==LeaveType.NON_OCCUPATIONAL_DISEASE){
							Integer itDays = getItDays(salary.getContract());
							// itDays = 30 - dias sano
							Calendar cal = Calendar.getInstance();
							cal.setTime(getStartDate());
							itDays = 30 - (cal.getActualMaximum(Calendar.DAY_OF_MONTH)-itDays);
							
							itBase = getItDailyBase(salary.getContract()) * itDays;
							createEDLBa01Segment(itBase, dat);
							createEDLBa02Segment(itBase, dat);
							createEDLCd01Segment(salary, dat);
						} else if(it.getType()==LeaveType.OCCUPATIONAL_DISEASE){
							Integer itDays = getItDays(salary.getContract());
							// itDays = 30 - dias sano
							Calendar cal = Calendar.getInstance();
							cal.setTime(getStartDate());
							itDays = 30 - (cal.getActualMaximum(Calendar.DAY_OF_MONTH)-itDays);
						
							itBase = getItDailyBase(salary.getContract()) * itDays;
							createEDLBa01Segment(itBase, dat);
							createEDLBa02Segment(itBase, dat);
							createEDLCd03Segment(salary, dat);
						}
					}
				}
			}
		}
//		createEDLCd05Segment(salary, dat);
//		createEDLCd16Segment(salary, dat);
//		createEDLCd18Segment(salary, dat);
//		createEDLCd21Segment(salary, dat);
//		createEDLCd24Segment(salary, dat);
//		createEDLCd26Segment(salary, dat);
//		createEDLCd27Segment(salary, dat);
	}
	
	////////////////////////
	// COMPENSACIONES - DEDUCCIONES
	////////////////////////
	/**
	 *  30 Reducciones. SEA Desempleo
	 * @param salary
	 * @param dat
	 */
	private void createEDLCd30Segment(SalaryBonus bonus, DAT dat) {
		if(bonus.getSalary().getContract().getRegimeType()==SSRegimeType.AGRICULTURAL){
			EDL edl = dat.getEdlSegment("CD30");
			createEDLRecord(edl, "CD", 30, new Double(CommonUtil.round((bonus).getAmount())*100).intValue());
		}
	}
	/**
	 *  29 Reducciones SEA. Contingencias comunes Sistema Especial Agrario 
	 * @param salary
	 * @param dat
	 */
	private void createEDLCd29Segment(SalaryBonus bonus, DAT dat) {
		if(bonus.getSalary().getContract().getRegimeType()==SSRegimeType.AGRICULTURAL){
			EDL edl = dat.getEdlSegment("CD29");
			createEDLRecord(edl, "CD", 29, new Double(CommonUtil.round((bonus).getAmount())*100).intValue());
		}
	}
	/**
	 *  28 Bonificación por ERE 
	 * @param salary
	 * @param dat
	 */
	private void createEDLCd28Segment(SalaryBonus bonus, DAT dat) {
		EDL edl = dat.getEdlSegment("CD28");
		createEDLRecord(edl, "CD", 28, new Double(CommonUtil.round((bonus).getAmount())*100).intValue());
	}
	private void createEDLCd27Segment(Salary salary, DAT dat) {
		// 27 Reducciones REA "Jornadas reales" (Baja a partir del 1 de enero de 2012) Régimen Especial Agrario 
	}
	private void createEDLCd26Segment(Salary salary, DAT dat) {
		// 26 Reducciones REA Cuantía mensual (modalidad G y J) (Baja a partir del 1 de enero de 2012) Régimen Especial Agrario 
	}
	/**
	 *  25 Exención de desempleo hijos<30años Autonomos
	 * @param salary
	 * @param dat
	 */
	private void createEDLCd25Segment(SalaryBonus bonus, DAT dat) {
		EDL edl = dat.getEdlSegment("CD25");
		createEDLRecord(edl, "CD", 25, new Double(CommonUtil.round((bonus).getAmount())*100).intValue());
	}
	private void createEDLCd24Segment(Salary salary, DAT dat) {
		// 24 Bonificación I+D+I (Baja a partir del 1 de agosto de 2012) Régimen General
	}
	/**
	 *  23 Bonificación Sector Industrial Incentivado
	 * @param salary
	 * @param dat
	 */
	private void createEDLCd23Segment(SalaryBonus bonus, DAT dat) {
		EDL edl = dat.getEdlSegment("CD23");
		createEDLRecord(edl, "CD", 23, new Double(CommonUtil.round((bonus).getAmount())*100).intValue());
	}
	/**
	 *  22 Bonificación Fom. Empleo Cuantía fija. Excepto Rég. Gral. Artistas (0112)
	 * @param salary
	 * @param dat
	 */
	private void createEDLCd22Segment(SalaryBonus bonus, DAT dat) {
		if(bonus.getSalary().getContract().getRegimeType()!=SSRegimeType.ARTIST){
			EDL edl = dat.getEdlSegment("CD22");
			Date bonusStart = bonus.getSalary().getStartDate();
			Date bonusEnd = bonus.getSalary().getEndDate();
			int bonusDays = 30;
			if(bonusStart.after(getStartDate()) || (bonusEnd!=null && bonusEnd.before(getEndDate())) ){
				bonusStart = bonusStart.before(getStartDate())?getStartDate():bonusStart;
				bonusEnd = (bonusEnd!=null && bonusEnd.after(getEndDate()))?getEndDate():bonusEnd;
				bonusDays = differenceBetweenDates(bonusStart, bonusEnd);
			}
			dat.setDiasAlta(bonusDays);
			createEDLRecord(edl, "CD", 22, bonusDays,new Double(CommonUtil.round((bonus).getAmount())*100).intValue());
		}
	}
	private void createEDLCd21Segment(Salary salary, DAT dat) {
		// 21 Bonificación Copa del America (R.D.L. 2146/2004) (Baja a partir del 1 de agosto de 2012)
	}
	/**
	 *  20 Bonificación Ceuta y Melilla (O. TAS/471/2004)
	 * @param salary
	 * @param dat
	 */
	private void createEDLCd20Segment(SalaryBonus bonus, DAT dat) {
		EDL edl = dat.getEdlSegment("CD20");
		createEDLRecord(edl, "CD", 20, new Double(CommonUtil.round((bonus).getAmount())*100).intValue());
	}
	private void createEDLCd18Segment(Salary salary, DAT dat) {
		// 18 Reducción por Exención de desempleo (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario)
	}
	/**
	 *  17 Reducción contingencias comunes excepto I.T. (R.D.L. 16/2001)
	 * @param salary
	 * @param dat
	 */
	private void createEDLCd17Segment(SalaryBonus bonus, DAT dat) {
		EDL edl = dat.getEdlSegment("CD17");
		createEDLRecord(edl, "CD", 17, new Double(CommonUtil.round((bonus).getAmount())*100).intValue());
	}
	private void createEDLCd16Segment(Salary salary, DAT dat) {
		// 16 Bonificación por trabajadores con 60 o más años (Baja a partir del 1 de agosto de 2012)
	}
	/**
	 *  13 Bonificación minusvalidos en Centros Especiales de Empleo
	 * @param salary
	 * @param dat
	 */
	private void createEDLCd13Segment(SalaryBonus bonus, DAT dat) {
		EDL edl = dat.getEdlSegment("CD13");
		createEDLRecord(edl, "CD", 13, new Double(CommonUtil.round((bonus).getAmount())*100).intValue());
	}
	/**
	 *  12 Bonificación por Ley 19/94 (Registro Canario) Régimen del Mar
	 * @param salary
	 * @param dat
	 */
	private void createEDLCd12Segment(SalaryBonus bonus, DAT dat) {
		if(bonus.getSalary().getContract().getRegimeType()==SSRegimeType.SEA_WORKERS){
			EDL edl = dat.getEdlSegment("CD12");
			createEDLRecord(edl, "CD", 12, new Double(CommonUtil.round((bonus).getAmount())*100).intValue());
		}
	}
	/** 
	 *  11 Bonificación por formación teórica a distancia
	 * @param salary
	 * @param dat
	 */
	private void createEDLCd11Segment(SalaryBonus bonus, DAT dat) {
		EDL edl = dat.getEdlSegment("CD11");
		createEDLRecord(edl, "CD", 11, new Double(CommonUtil.round((bonus).getAmount())*100).intValue());
	}
	/**
	 *  10 Bonificación por formación teórica presencial
	 * @param salary
	 * @param dat
	 */
	private void createEDLCd10Segment(SalaryBonus bonus, DAT dat) {
		EDL edl = dat.getEdlSegment("CD10");
		createEDLRecord(edl, "CD", 10, new Double(CommonUtil.round((bonus).getAmount())*100).intValue());
	}
	/**
	 *  7 Bonificaciones Contratos con derecho a bonificación/reducción (casilla 601 de TC1)
	 * @param bonus
	 * @param dat
	 */
	private void createEDLCd07Segment(SalaryBonus bonus, DAT dat) { 
		EDL edl = dat.getEdlSegment("CD07");
		createEDLRecord(edl, "CD", 7, new Double(CommonUtil.round((bonus).getAmount())*100).intValue());
	}
	/**
	 *  6 Reducciones Contratos con derecho a reducción (casilla 209 de TC1)
	 * @param salary
	 * @param dat
	 */
	private void createEDLCd06Segment(SalaryBonus bonus, DAT dat) {
		EDL edl = dat.getEdlSegment("CD06");
		createEDLRecord(edl, "CD", 6, new Double(CommonUtil.round((bonus).getAmount())*100).intValue());
	}
	private void createEDLCd05Segment(Salary salary, DAT dat) {
		// TODO 5 IT O.M. 3/4/73 Minería del Carbón
	}
	/**
	 *  3 IT por AT y EP 
	 *  No es de aplicación en el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
	 * @param salary
	 * @param dat
	 */
	private void createEDLCd03Segment(Salary salary, DAT dat) {
		// TODO
		Double amount = getItDailyBase(salary.getContract()) * 0.75 * getIt1MoreDays(salary.getContract());
		if (salary.getContract().getRegimeType() != SSRegimeType.ARTIST
				&& salary.getContract().getRegimeType() != SSRegimeType.AGRICULTURAL
				&& Double.compare(amount,0.0d)>0) {
			EDL edl = dat.getEdlSegment("CD03");
			createEDLRecord(edl, "CD", 3, new Double(CommonUtil.round(amount)*100).intValue());
		}
	}
	
	/**
	 *  1 IT enfermedad común y accidente no laboral 
	 *  No es de aplicación en el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
	 * @param salary
	 * @param dat
	 */
	private void createEDLCd01Segment(Salary salary, DAT dat) {
		// TODO 
		Double dailyBase = getItDailyBase(salary.getContract());
		Double amount = dailyBase * 0.6 * getIt16To20MoreDays(salary.getContract());
		amount += dailyBase * 0.75 * getIt21MoreDays(salary.getContract());
		if (salary.getContract().getRegimeType() != SSRegimeType.ARTIST
				&& salary.getContract().getRegimeType() != SSRegimeType.AGRICULTURAL
				&& Double.compare(amount,0.0d)>0) {
			EDL edl = dat.getEdlSegment("CD01");
			createEDLRecord( edl, "CD", 1, new Double(CommonUtil.round(amount)*100).intValue());
		}
	}
	
	
	////////////////////////
	// BASES
	////////////////////////
	private void createEDLBa42Segment(Salary salary, DAT dat) {
		// TODO 42 Base exclusiva de AT y EP sin cotización de Otras Cotizaciones
	}
	private void createEDLBa41Segment(Salary salary, DAT dat) {
		// 41 Contingencias Comunes y FOGASA, (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	}
	private void createEDLBa38Segment(Salary salary, DAT dat) {
		// 38 Cotización exclusivamente por FOGASA. (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario) 
	}
	private void createEDLBa37Segment(Salary salary, DAT dat) {
		// 37 Cotización jornadas reales y FOGASA excluido desempleo(Baja a partir del 1 de enero de 2012). (Régimen Especial Agrario) 
	}
	private void createEDLBa36Segment(Salary salary, DAT dat) {
		// 36 Base exclusiva Desempleo/FOGASA tipo total. (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario) 
	}
	private void createEDLBa35Segment(Salary salary, DAT dat) {
		// 35 Base fija cotización trabajadores cuenta ajena extranjeros (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario) 
	}
	private void createEDLBa34Segment(Salary salary, DAT dat) {
		// 34 Base de AT en vacaciones. (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	}
	private void createEDLBa33Segment(Salary salary, DAT dat) {
		// 33 Base de cotización exclusiva de Otras Cotizaciones en situación de IT/maternidad/riesgo durante el embarazo. 
		// Trabajadores no fijos (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario) 
	}
	private void createEDLBa32Segment(Salary salary, DAT dat) {
		// 32 Base de cotización Jornadas Reales en situación de IT por desempleo empresarial y FOGASA. 
		// (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	}
	private void createEDLBa31Segment(Salary salary, DAT dat) {
		// 31 Contingencias comunes trajadores no fijos (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario) 
	}
	private void createEDLBa30Segment(Salary salary, DAT dat) {
		// 30 Cotización por Jornadas Reales. (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	}
	private void createEDLBa28Segment(Salary salary, DAT dat) {
		// 28 Diferencia en bases (contingencias comunes y salario normalizado), cotización exclusivamente empresarial 
		// Solo para liquidaciones anteriores al año 2002 del Régimen Especial de la Minería del Carbón (0911). Baja a partir de 2002 
	}
	private void createEDLBa23Segment(Salary salary, DAT dat) {
		// 23 Base de cotización tipo total desempleo y FOGASA. (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	}
	private void createEDLBa22Segment(Salary salary, DAT dat) {
		// TODO 22 Base de cotización empresarial por AT y EP y Otras Cotizaciones
	}
	private void createEDLBa21Segment(Salary salary, DAT dat) {
		// 21 Base de cotización empresarial por contingencias comunes Base de cotización empresarial por desempleo y FOGASA 
		// (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	}
	private void createEDLBa20Segment(Salary salary, DAT dat) {
		// TODO 20 Base de cotización empresarial C.Comunes = AT y EP
	}
	/**
	 *  11 Horas extras no estructurales / Otras horas extras desde 1/1/98 
	 *  No será de utilización para Régimen General de Artistas (0112),), ni Régimen Especial de Minería del Carbón (0911)
	 * @param salary
	 * @param dat
	 */
	private void createEDLBa11Segment(Salary salary, DAT dat) {
		if (salary.getContract().getRegimeType() != SSRegimeType.ARTIST
				&& salary.getContract().getRegimeType() != SSRegimeType.COAL_MINING
				&& salary.getNonEstructuralOvertimeBase()!=null && salary.getNonEstructuralOvertimeBase().compareTo(0.0d)>0) {
			EDL edl = dat.getEdlSegment("BA11");
			createEDLRecord(edl, "BA", 11, new Double((salary.getNonEstructuralOvertimeBase())*100).intValue());
		}
	}
	/**
	 *  10 Horas extras estructurales / Causa de fuerza mayor desde 1/1/98 
	 *  No será de utilización para Régimen General de Artistas (0112), Régimen Especial de Minería del Carbón (0911).
	 * @param salary
	 * @param dat
	 */
	private void createEDLBa10Segment(Salary salary, DAT dat) {
		if (salary.getContract().getRegimeType() != SSRegimeType.ARTIST
				&& salary.getContract().getRegimeType() != SSRegimeType.COAL_MINING
				&& salary.getOvertimeBase()!=null && salary.getOvertimeBase().compareTo(0.0d)>0) {
			EDL edl = dat.getEdlSegment("BA10");
			createEDLRecord(edl, "BA", 10, new Double((salary.getOvertimeBase())*100).intValue());
		}
	}
	private void createEDLBa09Segment(Salary salary, DAT dat) {
		// TODO: 9 Horas complementarias No será de utilización para Régimen General de Artistas (0112)
	}
	private void createEDLBa08Segment(Salary salary, DAT dat) {
		// 8 Diferencia Bases (contingencias comunes y salario normalizado) 
		// Solo para liquidaciones anteriores al año 2002 del Régimen Especial de la Minería del Carbón (0911). Baja a partir de 2002
	}
	private void createEDLBa07Segment(Salary salary, DAT dat) {
		// 7 AT y EP sin horas extraordinarias 
		// No será de utilización para Régimen General de Artistas (0112), ni Régimen Especial de Minería del Carbón (0911).
		// Baja a partir de 2002
	}
	private void createEDLBa06Segment(Salary salary, DAT dat) {
		// TODO 6 Importe percepciones Integras (Artistas.)
	}
	private void createEDLBa05Segment(Salary salary, DAT dat) {
		// TODO 5 Exceso del tope (Minería del Carbón)
	}
	/**
	 *  2 AT y EP
	 * @param salary
	 * @param dat
	 */
	private void createEDLBa02Segment(Double amount, DAT dat) {
		EDL edl = dat.getEdlSegment("BA02");
		createEDLRecord(edl, "BA", 2, new Double(amount*100).intValue());
	}
	/**
	 *  1 Contingencias comunes
	 * @param salary
	 * @param dat
	 */
	private void createEDLBa01Segment(Double amount, DAT dat) {
		EDL edl = dat.getEdlSegment("BA01");
		createEDLRecord(edl, "BA", 1, new Double(amount*100).intValue());
	}
	/**
	 * 0 Normal C. Comunes = AT y EP
	 * @param salary
	 * @param dat
	 */
	private void createEDLBa00Segment(Salary salary, DAT dat) {
		EDL edl = dat.getEdlSegment("BA00");
		createEDLRecord(edl, "BA", 0, new Double(salary.getCommonBase()*100).intValue());		
	}
	
	private Integer getDcDays(Contract c) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private List<ITransferObject> getSalaryBonuses(Salary salary) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(SalaryBonus.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_BONUS_SALARY_ID), salary.getId());
			return bean.getList(criteria);
		} catch (ManagerBeanException e) {
			// NADA
		}
		return null;
	}
	
	private BonusType obtainBonusType(SalaryBonus bonus) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractBonus.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_BONUS_CONTRACT_ID), bonus.getSalary().getContract().getId());
			List<ITransferObject> list = bean.getList(criteria);
			if( !list.isEmpty() ){
				if(list.size()==1){
					ContractBonus cb = (ContractBonus) list.get(0);
					if(cb.getBonusConcept()!=null){
						return ((ContractBonus) list.get(0)).getBonusConcept().getType();
					}
				} else {
					for(ITransferObject to: list){
						ContractBonus cb = (ContractBonus) to;
						if(StringUtils.isNotBlank(bonus.getDescription()) && bonus.getDescription().equals(cb.getBonusConcept().getDescription())){
							return cb.getBonusConcept().getType();
						}
					}
				}
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
		return null;
	}
	
	private boolean isBonusRight(SalaryBonus bonus){
//		Mujeres desempleadas así como las víctimas de violencia de género o doméstica.
//
//		Mujeres desempleadas contratadas en los 24 meses siguientes al parto, adopción o acogimiento.
//
//		Mujeres desempleadas contratadas después de 5 años de inactividad laboral, si, anteriormente a su retirada, han trabajado, al menos, 3 años.
//
//		Mayores de 45 años desempleados.
//
//		Jóvenes desempleados de 16 a 30 años.
//
//		Desempleados durante al menos 6 meses y trabajadores en situación de exclusión social.
//
//		Personas con discapacidad.
//
//		Personas con discapacidad severa.
//
//		Personas con discapacidad contratados por los centros especiales de empleo.
//		
//		Contratación indefinida de personal investigador por parte de las empresas dedicadas a actividades de investigación y desarrollo e innovación tecnológica.
//		Real Decreto 278/2007, de 23 de febrero, sobre bonificaciones en la cotización a la (Seguridad Social) respecto del personal investigador. (BOE de 24 de febrero)
//
//		Desempleados excedentes del sector textil y de la confección que hubieran sido despedidos entre el 13 de junio de 2006 y el 31 de diciembre de 2008, siempre que la contratación se produzca durante los 2 años siguientes a la fecha de despido.
		if(bonus.getType() == BonusType.EMPLOYMENT_PROMOTION){
			
		}
		return false;
	}
	
	private String getParticularGroup(Contract contract) {
		String o = SEPEUtils.getInstance().getContractDataMap(contract, false, true).get(ContextVariable.QUOTE_PECULIARITY_COLLECTIVE.getName());
		return o!=null && !o.isEmpty()?o.replaceAll("\"", ""):null;
	}
	private Integer getEmploymentRelation(Contract contract) {
		// TODO getEmploymentRelation
//		0100 Personal de Alta Dirección
//		0409 Deportistas profesionales
//		0900 Abogados en despachos de abogados
//		9909 Personal becario de investigación
		return null;
	}
	private String getHandicapIndicator(Contract contract) {
		// TODO getHandicapIndicator
//		D Minusvalía igual o superior al 33%
//		S Pensionista incapacidad permanente de la S.S.
//		P Pensionista incapacidad permanente clases pasivas
		
		return null;
	}
	private String getQuoteMode(Contract contract) {
		// TODO getQuoteMode
//		J Cotización Jornadas Reales
//		G Cotización Sistema General
		//obligatorio para reg. 0613 
		return null;
	}
	private String getContractOccupation(Contract contract) {
		String o = SEPEUtils.getInstance().getContractDataMap(contract, false, true).get(ContextVariable.OCCUPATION.getName());
		return o!=null && !o.isEmpty()?o:null;
	}
	private Integer getSecondariEpigraph(Contract contract) {
		// TODO getSecondariEpigraph
		return null;
	}
	private Integer getAtEpEpigraph(Contract contract) {
		// TODO getAtEpEpigraph
		return null;
	}
	private ContractCode getContractCode(Contract contract) {
		String tc2 = SEPEUtils.getInstance().getContractDataMap(contract, false, true).get(ContextVariable.TC2.getName());
		return ContractCode.getContractCodeByValue(tc2);
	}
	private String getSourceContractType(Contract contract) {
		if(getContractCode(contract)==ContractCode.C540){
			String i = SEPEUtils.getInstance().getContractDataMap(contract, false, true).get(ContextVariable.INDEFINITE.getName());
			return i!=null && !i.isEmpty()?"I":"D";
		}
		return null;
	}
	private Integer getQuoteGroup(Contract contract) {
		String q = SEPEUtils.getInstance().getContractDataMap(contract, false, true).get(ContextVariable.QUOTE_GROUP.getName());
		return q!=null && !q.isEmpty()?Integer.parseInt(q):null;
	}
	private Integer getBonificationReduction(Contract contract) {
		// TODO getBonificationReduction
//		1 Reducción minima
//		2 Reducción media
//		3 Reducción máxima
//		4 Importe total sin reducción
		return null;
	}
	private Integer getSpecifics(Contract contract) {
		// TODO getSpecifics
		// obligatorio para regimen 0911
		return null;
	}
	private String getCollectiveJournalHours(Contract contract) {
		// TODO getCollectiveJournalHours
		// Obligatorio para el R.E. Minería del Carbón.
//		0 Normales
//		2 Efectivos
		return null;
	}
	private String getVacationIndicator(Contract contract) {
		// TODO 
//		String v = SEPEUtils.getInstance().getContractDataMap(contract, false, true).get(ContextVariable.NO_HOLIDAYS.getName());
//		return v!=null && !v.isEmpty()?"V":WHITESPACE_1;
		return null;
	}
	
	/**
	 * Indicador
		Días u horas a las que se refiere la cotización.
		Es obligatorio consignar "H" cuando se trate de contratos a tiempo parcial que coticen por horas.
	 * @param c
	 * @return
	 */
	private String getQuoteIndicator(Contract contract) {
		// TODO 
		String code = getContractCode(contract).getValue();
		if(code.startsWith("2") || code.startsWith("5")){
			String weekHours = SEPEUtils.getInstance().getContractDataMap(contract, false, true).get(ContextVariable.WEEK_HOURS.getName());
			if( StringUtils.isNotBlank(weekHours) ){
				return "H";
			}
		}
		return null;
	}
	
	/**
	 * Nº días alta trabaj. extr. REA y trabaj. a T. Parcial bonificados R.D.L. 5/2006
		Este campo es obligatorio con formato numérico para contratos a tiempo parcial bonificados con el
		programa de fomento de empleo (R.D.L. 5/2006 de 9 de junio)
		Podrá tomar valor entre 1 y 31. Se cumplimentará en un solo segmento DAT de los varios que pueda tener un mismo trabajador y
		periodo (diferentes situaciones contractuales en el mismo mes).
	 * @param c
	 * @return
	 */
	private Integer getContractDischargeDays(Contract contract) {
		// TODO 
		return null;
	}
	
	/**
	 * Dias/horas
		Este campo puede tomar valor entre 1 y 30 (Retribución mensual) ó 31 (Retribución diaria), para la
		cotización por días, y entre 1 y 248 en caso de cotización por horas. Si existe más de un segmento
		DAT para un mismo trabajador y periodo (diferentes situaciones contractuales en el mismo mes),
		estos límites se aplicarán a la suma de todos ellos. Cuando el trabajador se encuentre en situación de
		Maternidad a tiempo parcial o en situación de ERE parcial, los días consignados en el segmento DAT
		relativo a una de estas situaciones, no se tendrán en cuenta a efectos del límite máximo de días.
		Para el Rég. 0163: para los trabajadores de modalidad G se indicará número de dias elta y para los
		trabajadores de modalidad J se indicará el número de jornadas reales efectivamente trabajadas, o, en
		situación IT las que se deberían haber realizado.
	 * @param c
	 * @return
	 */
	private Integer getContractDaysOrHours(Contract contract) {
		// TODO 
		Integer itDays = getItDays(contract);
		String code = getContractCode(contract).getValue();
		if(code.startsWith("1") || code.startsWith("4")){
			if(itDays!=null && itDays>0){
				Calendar cal = Calendar.getInstance();
				cal.setTime(getStartDate());
				return cal.getActualMaximum(Calendar.DAY_OF_MONTH)-itDays; 
			}
			if( contract.getEndDate()!=null && getEndDate().after(contract.getEndDate()) ){
				return (int) getAvailableDays(getStartDate(), contract.getEndDate());
			} else {
				return 30;
			}
		} else if(code.startsWith("2") || code.startsWith("5")){
			String weekHours = SEPEUtils.getInstance().getContractDataMap(contract, false, true).get(ContextVariable.WEEK_HOURS.getName());
			Double dayHours = (Double.parseDouble(weekHours)/7);
			
			Calendar startCal = Calendar.getInstance();
			startCal.setTime(getStartDate());
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(getEndDate());
			
			long totalDays = 0;
			if( contract.getEndDate()!=null && getEndDate().after(contract.getEndDate()) ){
				totalDays =  getAvailableDays(getStartDate(), contract.getEndDate());
			} else {
				totalDays =  getAvailableDays(getStartDate(), getEndDate());
			}
			
			if(itDays!=null && itDays>0){
				return Double.valueOf(CommonUtil.round((totalDays - itDays) * dayHours, 0)).intValue();
			}
			return Double.valueOf(CommonUtil.round(totalDays * dayHours, 0)).intValue();
		}
		return null;
		
	}
	
	private long getAvailableDays(Date start, Date end) {
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(start);
		startCal.set(Calendar.HOUR_OF_DAY, 0);
		startCal.set(Calendar.MINUTE, 0);
		startCal.set(Calendar.SECOND, 0);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(end);
		endCal.set(Calendar.HOUR_OF_DAY, 23);
		endCal.set(Calendar.MINUTE, 59);
		endCal.set(Calendar.SECOND, 59);
		return CommonUtil.getDaysBetweenDates(startCal.getTime(), endCal.getTime());
	}
	
	/**
	 * C - Obligatorio para contratos de duración efectiva inferior a 7 días Ley 12/2001
	 * @param c
	 * @return
	 */
	private boolean isLessThan7DaysContract(Contract contract) {
		if(contract.getEndDate()!=null && differenceBetweenDates(contract.getStartDate(), contract.getEndDate())<7){
			return true;
		}
		return false;
	}
	
	/**
	 * Obligatorio para estas situaciones:
		I = Incapacidad Temporal diferida.
		R = Regulación de Empleo Parcial (parte trabajada)
		P = Regulación de Empleo Parcial (situación ERE)
		T = Regulación de Empleo Total
		F = Cotización por baja fuera de plazo
		Q = Cotización exclusiva trienios
		H=Reservistas voluntarios en período de activación
	 * @param c
	 * @return
	 */
	private String getOthers(Contract contract) {
		// TODO 
		
		return " ";
	}
	
	/**
	 * A - Obligatorio en situaciones de Alta sin percibo de retribución
	 * @param c
	 * @return
	 */
	private boolean isNoRetributionDischarge(Contract contract) {
		// TODO 
		return false;
	}
	
	/**
	 * M - Obligatorio para grupos de 8 a 11 que tengan retribución mensual
	 * @param c
	 * @return
	 */
	private boolean isMonthSalary(Contract contract) {
		// TODO 
		return false;
	}
	
	/**
	 * Obligatorio para estas situaciones:
		R = Reducccón jornada guarda legal contempladas en la Ley Orgánica 3/2007, de 22 de marzo ,
			ó cuidado de menores afectados por cáncer u otra enfermedad grave Ley 39/2010,
			o Maternidad/Paternidad tiempo Parcial (jornada trabajada) 
				(si R se solapa con I , D o E se consignará de éstas últimas la que corresponda) .
		T = Maternidad/Paternidad Tiempo Parcial (jornada relativa a descanso por maternidad)
		D = Descanso maternidad/Paternidad a tiempo completo
		E = Riesgo durante el embarazo
		I = incapacidad Temporal
	 * @param c
	 * @return
	 */
	private String getJournalReduction(Contract contract) {
		// TODO 

		List<ITransferObject> leaveList = getContractLeaves(contract, getStartDate(), getEndDate());
		if(leaveList!=null && !leaveList.isEmpty()){
			return "I";
		}
		return null;
	}
	
	/**
	 * P - Obligatorio en situaciones de Pluriempleo
	 * @param c
	 * @return
	 */
	private boolean isMoonlighting(Contract contract) {
		// TODO 
		return false;
	}
	
	/**
	 * H - Obligatorio en situaciones de Huelga Parcial
	 * @param c
	 * @return
	 */
	private boolean isPartialStrike(Contract contract) {
		// TODO 
		return false;
	}
	
	private void createEDLRecord(EDL edl, String type, Integer key, Integer amount) {
		createEDLRecord(edl, type, key, 0, amount, " ", 0, autoComplete("0", 8, "0", true), autoComplete("0", 8, "0", true), autoComplete("0", 8, "0", true), " ");
	}
	private void createEDLRecord(EDL edl, String type, Integer key, Integer element, Integer amount) {
		createEDLRecord(edl, type, key, element, amount, " ", 0, autoComplete("0", 8, "0", true), autoComplete("0", 8, "0", true), autoComplete("0", 8, "0", true), " ");
	}
	private void createEDLRecord(EDL edl, String type, Integer key, Integer amount, String sign) {
		createEDLRecord(edl, type, key, 0, amount, " ", 0, autoComplete("0", 8, "0", true), autoComplete("0", 8, "0", true), autoComplete("0", 8, "0", true), " ");
	}
	private void createEDLRecord(EDL edl, String type, Integer key, Integer element, Integer amount, String sign) {
		createEDLRecord(edl, type, key, element, amount, sign, 0, autoComplete("0", 8, "0", true), autoComplete("0", 8, "0", true), autoComplete("0", 8, "0", true), " ");
	}
	/**
	Tipo de elementos de datos
	Determina la naturaleza del elemento que siga a continuación, indicando:
	BA Si se trata de una base. En este caso debe cumplimentarse el importe de la misma en el
	subcampo correspondiente. Para BA09 (Base de horas complementarias), deberá indicarse en el
	campo elemento, el nº de horas complementarias realizadas.
	CD Si se trata de una compensación y/o deducción. En tal caso deben cumplimentarse días e importe.
	
	Clave. Tipo específico de base o compensación/deducción. Según tabla de Bases, si se trata de una base, o
	según tabla compensaciones y/o deducciones si se trata de compensación y/o deducción. (Vercapítulo
	Tabla T - 25 y T - 26
	
	Elemento. Indica el número de días a que se refiere la compensación o deducción, es decir, los días con derecho
	a compensación, bonificación, subvención o reducción.
	Necesariamente va ligado al tipo de elemento CD. A ceros en el caso de BA, excepto para BA09, que
	es obligatorio, e indicará el nº de horas complementarias realizadas.
	Obligatorio para Compensación/Deducción por formación teórica presencial CD10 o formación teórica
	a distancia: CD11.
	
	Importe. Indica el importe de la base o de la compensación/deducción. Los importes se consignarán con dos
	céntimos de euro, sin caracteres se
	paradores de céntimos.
	1360
	
	Signo del importe. Si es negativo aparece el carácter '-'. En campos positivos el carácter ' '.
	Restricciones de uso. No se podrán consignar bases en negativo. Para los segmentos CD no se
	admitirán signos negativos, excepto para liquidaciones L04.
	 */
	private void createEDLRecord(EDL edl, String type, Integer key, Integer element, Integer amount, String sign, Integer resolutionType, String resolutionDate, String startPeriod, String endPeriod, String resolutionReference) {
		edl.setTipoElementoDatos(type);
		edl.setClave(key);
		edl.setElemento(element);
		edl.setImporte(amount);
		edl.setSigno(sign);
		edl.setTipoResolucion(resolutionType);
		edl.setFechaResolucion(resolutionDate);
		edl.setInicioPeriodo(startPeriod);
		edl.setFinPeriodo(endPeriod);
		edl.setReferencia(resolutionReference);
	}
	
	/**
	 * TC/2 Totales
	 * @param ccc
	 * @return
	 */
	private TCT createTCTRecord(EnterpriseCCC ccc) {
		// TODO
		Calendar cal = Calendar.getInstance();
		cal.set(year, endMonth.ordinal(), 1);
		TCT tct = new TCT();
		Mutual mutual = obtainMutual(ccc);
		tct.setEntidadAtEp(mutual!=null?mutual.getValue():null);
		tct.setNumeroTrabajadores(Integer.parseInt(autoComplete(String.valueOf(totalContractSum), 6, "0", true)));
		// numero de trabajadores fijos con jornadas reales. Sin cumplimentacion a partir de 2009
		tct.setNumeroTrabajadoresFijos(null);
		// numero de trabajadores no fijos con jornadas reales. Sin cumplimentacion a partir de 2009 
		tct.setNumeroTrabajadoresNoFijos(null);
		// total numero trabajadores con jornadas reales. Sin cumplimentacion a partir de 2009
		tct.setTotalNumeroTrabajadores(null);
		if(liquidationType==LiquidationType.L02 || liquidationType==LiquidationType.L03){
			tct.setFechaControl(year+Integer.parseInt(autoComplete(String.valueOf(startMonth.ordinal()+1), 2, "0", true)+"01"));
		} else {
			tct.setFechaControl(null);
		}
		tct.setTotalJornadas(null);
		// numero trabajadores extranjeros del REA
		tct.setTrabajadoresExtranjeros(null);
		return tct;
	}
	
	private void createEDTRecords(EnterpriseCCC ccc, EMP emp,List<ITransferObject> contractList) throws ManagerBeanException {
		for(ITransferObject to: contractList){
			Contract c = (Contract) to;
		
			if(getSalary(c)!=null){
				
				createEDTBa01Segment(emp);
				createEDTBa02Segment(emp);
				createEDTBa05Segment(emp);
				createEDTBa06Segment(emp);
				createEDTBa07Segment(emp);
				createEDTBa08Segment(emp);
				createEDTBa09Segment(ccc, emp);
				createEDTBa10Segment(ccc, emp);
				createEDTBa11Segment(ccc, emp);
				createEDTBa21Segment(ccc, emp);
				createEDTBa22Segment(emp);
				createEDTBa23Segment(emp);
				createEDTBa28Segment(emp);
				createEDTBa30Segment(emp);
				createEDTBa31Segment(emp);
				createEDTBa32Segment(emp);
				createEDTBa33Segment(emp);
				createEDTBa34Segment(emp);
				createEDTBa35Segment(emp);
				createEDTBa36Segment(emp);
				createEDTBa37Segment(emp);
				createEDTBa38Segment(emp);
				createEDTBa41Segment(emp);
				createEDTBa42Segment(emp);
				
				createEDTCd01Segment(ccc, emp);
				createEDTCd03Segment(ccc, emp);
				createEDTCd05Segment(emp);
				createEDTCd06Segment(emp);
				createEDTCd07Segment(emp);
				createEDTCd10Segment(emp);
				createEDTCd11Segment(emp);
				createEDTCd12Segment(emp);
				createEDTCd13Segment(emp);
				createEDTCd16Segment(emp);
				createEDTCd17Segment(ccc, emp);
				createEDTCd18Segment(emp);
				createEDTCd20Segment(emp);
				createEDTCd21Segment(emp);
				createEDTCd22Segment(ccc, emp);
				createEDTCd23Segment(emp);
				createEDTCd24Segment(ccc, emp);
				createEDTCd25Segment(ccc, emp);
				createEDTCd26Segment(emp);
				createEDTCd27Segment(emp);
				createEDTCd28Segment(emp);
				createEDTCd29Segment(emp);
				createEDTCd30Segment(emp);
				
				createEDTCa01Segment(ccc, emp);
				createEDTCa02Segment(emp);
				createEDTCa03Segment(emp);
				createEDTCa11Segment(emp);
				createEDTCa12Segment(emp);
				createEDTCa20Segment(emp);
				createEDTCa21Segment(emp);
				createEDTCa22Segment(emp);
				emp.getEdtSegment("EDTCA30");
				createEDTCa31Segment(ccc, emp);
				createEDTCa32Segment(ccc, emp);
				createEDTCa30Segment(emp);
				
				createEDTCa50Segment(ccc, emp);
				createEDTCa51Segment(emp);
				createEDTCa52Segment(emp);
				createEDTCa53Segment(emp);
				createEDTCa54Segment(emp);
				createEDTCa55Segment(emp);
				createEDTCa56Segment(emp);
				createEDTCa57Segment(emp);
				createEDTCa60Segment(emp);
				createEDTCa80Segment(emp);
				createEDTCa90Segment(emp);
				
				emp.getEdt().remove("EDTTT10");
				emp.getEdt().remove("EDTTT20");
				emp.getEdt().remove("EDTTT30");
				emp.getEdt().remove("EDTTT91");
				emp.getEdt().remove("EDTTT92");
				emp.getEdtSegment("EDTTT10");
				emp.getEdtSegment("EDTTT20");
				emp.getEdtSegment("EDTTT30");
				createEDTTt10Segment(emp);
				createEDTTt20Segment(emp);
				createEDTTt30Segment(emp);
				createEDTTt9XSegment(emp);
			}
		}
	}
	
	
	//////////////////////////////////////
	// TOTALES
	//////////////////////////////////////
	/**
	 * 91 A Ingresar
	 * 92 A percibir
	 * se crea el segmento tt91 o tt92 dependiendo del signo del importe
	 */
	private void createEDTTt9XSegment(EMP emp) {
		Integer amount = 0; 
		amount += ((emp.getEdt().containsKey("EDTTT10") && emp.getEdtSegment("EDTTT10").getImporte()!=null)?emp.getEdtSegment("EDTTT10").getImporte():0);
		amount += ((emp.getEdt().containsKey("EDTTT20") && emp.getEdtSegment("EDTTT20").getImporte()!=null)?emp.getEdtSegment("EDTTT20").getImporte():0);
		amount += ((emp.getEdt().containsKey("EDTTT30") && emp.getEdtSegment("EDTTT30").getImporte()!=null)?emp.getEdtSegment("EDTTT30").getImporte():0);
		if(amount != 0){
			EDT edt = emp.getEdtSegment("EDTTT"+(amount<0?"92":"91"));
			edt.setTipoElemento("TT");
			edt.setClave(amount<0?92:91);
			edt.setCalificadorClave(null);
			edt.setBase(null);
			edt.setIndicadorFactorTipo(" ");
			edt.setParteEnteraTipo(0);
			edt.setParteDecimalFactorTipo(0);
			edt.setSigno(" ");
			if(amount<0){
				amount += (emp.getEdt().containsKey("EDTCA90")?emp.getEdtSegment("EDTCA90").getImporte():0);
			}
			edt.setImporte(amount);
		}
	}
	/**
	 *  30 Liquido otras cotizaciones
	 * @param emp
	 */
	private void createEDTTt30Segment(EMP emp) {
		Integer amount = 0; 
		amount += (emp.getEdt().containsKey("EDTCA50")&&emp.getEdtSegment("EDTCA50").getImporte()!=null?emp.getEdtSegment("EDTCA50").getImporte():0); 
		amount += (emp.getEdt().containsKey("EDTCA57")?emp.getEdtSegment("EDTCA57").getImporte():0);
		amount -= (emp.getEdt().containsKey("EDTCA60")&&emp.getEdtSegment("EDTCA60").getImporte()!=null?emp.getEdtSegment("EDTCA60").getImporte():0);
		amount -= (emp.getEdt().containsKey("EDTCD24")?emp.getEdtSegment("EDTCD24").getImporte():0);
		if(amount != 0){
			EDT edt = emp.getEdtSegment("EDTTT30");
			edt.setTipoElemento("TT");
			edt.setClave(30);
			edt.setCalificadorClave(null);
			edt.setBase(null);
			edt.setIndicadorFactorTipo(" ");
			edt.setParteEnteraTipo(0);
			edt.setParteDecimalFactorTipo(0);
			edt.setImporte(amount);
			edt.setSigno(amount<0?"-":" ");
		}
	}
	/**
	 * 20 Liquido accidentes de trabajo y enfermedad profesional
	 */
	private void createEDTTt20Segment(EMP emp) {
		Integer amount = 0; 
		amount += (emp.getEdt().containsKey("EDTCA30")&&emp.getEdtSegment("EDTCA30").getImporte()!=null?emp.getEdtSegment("EDTCA30").getImporte():0); 
		amount -= (emp.getEdt().containsKey("EDTCD03")?emp.getEdtSegment("EDTCD03").getImporte():0);
		if(amount != 0){
			EDT edt = emp.getEdtSegment("EDTTT20");
			edt.setTipoElemento("TT");
			edt.setClave(20);
			edt.setCalificadorClave(null);
			edt.setBase(null);
			edt.setIndicadorFactorTipo(" ");
			edt.setParteEnteraTipo(0);
			edt.setParteDecimalFactorTipo(0);
			edt.setImporte(amount);
			edt.setSigno(amount<0?"-":" ");
		}
	}
	/**
	 * 10 Liquido contingencias generales
	 * @param emp
	 */
	private void createEDTTt10Segment(EMP emp) {
		Integer amount = 0; 
		amount += (emp.getEdt().containsKey("EDTCA01")&&emp.getEdtSegment("EDTCA01").getImporte()!=null?emp.getEdtSegment("EDTCA01").getImporte():0); 
		amount += (emp.getEdt().containsKey("EDTCA02")?emp.getEdtSegment("EDTCA02").getImporte():0);
		amount += (emp.getEdt().containsKey("EDTCA11")?emp.getEdtSegment("EDTCA11").getImporte():0);
		amount += (emp.getEdt().containsKey("EDTCA12")?emp.getEdtSegment("EDTCA12").getImporte():0);
		amount -= (emp.getEdt().containsKey("EDTCA20")?emp.getEdtSegment("EDTCA20").getImporte():0);
		amount -= (emp.getEdt().containsKey("EDTCA21")?emp.getEdtSegment("EDTCA21").getImporte():0);
		amount -= (emp.getEdt().containsKey("EDTCA22")?emp.getEdtSegment("EDTCA22").getImporte():0);
		amount -= (emp.getEdt().containsKey("EDTBA10")?emp.getEdtSegment("EDTBA10").getImporte():0);
		amount -= (emp.getEdt().containsKey("EDTBA11")?emp.getEdtSegment("EDTBA11").getImporte():0);
		if(amount != 0){
			EDT edt = emp.getEdtSegment("EDTTT10");
			edt.setTipoElemento("TT");
			edt.setClave(10);
			edt.setCalificadorClave(null);
			edt.setBase(0);
			edt.setIndicadorFactorTipo(null);
			edt.setParteEnteraTipo(0);
			edt.setParteDecimalFactorTipo(0);
			edt.setImporte(amount);
			edt.setSigno(amount<0?"-":" ");
		}
	}
	
	
	//////////////////////////////////////
	// ELEMENTO CALCULADO TOTALES
	//////////////////////////////////////
	private void createEDTCa90Segment(EMP emp) {
		// TODO 90 Recargo de mora
	}
	private void createEDTCa80Segment(EMP emp) {
		// TODO 80 Bonificación INEM formación continua
	}
	/**
	 *  60 Suma de bonificaciones, subvenciones y compensaciones
	 * @param emp
	 */
	private void createEDTCa60Segment(EMP emp) {
		Integer amount = 0;
		amount += emp.getEdt().containsKey("EDTCD07")?emp.getEdtSegment("EDTCD07").getImporte():0;
		amount += emp.getEdt().containsKey("EDTCD10")?emp.getEdtSegment("EDTCD10").getImporte():0;
		amount += emp.getEdt().containsKey("EDTCD11")?emp.getEdtSegment("EDTCD11").getImporte():0;
		amount += emp.getEdt().containsKey("EDTCD13")?emp.getEdtSegment("EDTCD13").getImporte():0;
		amount += emp.getEdt().containsKey("EDTCD16")?emp.getEdtSegment("EDTCD16").getImporte():0;
		amount += emp.getEdt().containsKey("EDTCD20")?emp.getEdtSegment("EDTCD20").getImporte():0;
		amount += emp.getEdt().containsKey("EDTCD21")?emp.getEdtSegment("EDTCD21").getImporte():0;
		amount += emp.getEdt().containsKey("EDTCD22")?emp.getEdtSegment("EDTCD22").getImporte():0;
		amount += emp.getEdt().containsKey("EDTCD23")?emp.getEdtSegment("EDTCD23").getImporte():0;
		amount += emp.getEdt().containsKey("EDTCD25")?emp.getEdtSegment("EDTCD25").getImporte():0;
		amount += emp.getEdt().containsKey("EDTCA28")?emp.getEdtSegment("EDTCA28").getImporte():0;
		amount += emp.getEdt().containsKey("EDTCA80")?emp.getEdtSegment("EDTCA80").getImporte():0;
		if(amount != 0){
			EDT edt = emp.getEdtSegment("EDTCA60");
			edt.setTipoElemento("CA");
			edt.setClave(60);
			edt.setCalificadorClave(null);
			edt.setBase(null);
//	edt.setIndicadorFactorTipo("T");
//	edt.setParteEnteraTipo(28);
//	edt.setParteDecimalFactorTipo(03);
//	edt.setImporte(edt.getBase()*28);
			edt.setImporte(amount);
			edt.setSigno(" ");
		}
	}
	private void createEDTCa57Segment(EMP emp) {
		// TODO 57 Cuota empresarial por Otras Cotizaciones
	}
	private void createEDTCa56Segment(EMP emp) {
		// TODO 56 Total Otras Cotizaciones cuota empresarial (TC1/16) Régimen Especial del Mar
	}
	private void createEDTCa55Segment(EMP emp) {
		// TODO 55 Cotización empresarial por Fogasa y FP (TC1/16) Régimen Especial del Mar
	}
	private void createEDTCa54Segment(EMP emp) {
		// TODO 54 Cotización empresarial por desempleo ( TC1/16) - Régimen Especial del Mar
		// Cotización empresarial por Desempleo y Formación Profesional (TC1/25) - Sistema Especial de Manipulado y Empaquetado de Tomate Fresco
	}
	private void createEDTCa53Segment(EMP emp) {
		// TODO 53 Total Otras Cotizaciones (Desempleo, FOGASA y Formación Profesional) (TC1/16) - Régimen Especial del Mar
		// Total Otras Cotizaciones (Desempleo, FOGASA y Formación Profesional (TC1/25) - Sistema Especial de Manipulado y Empaquetado de Tomate Fresco
	}
	private void createEDTCa52Segment(EMP emp) {
		// TODO 52 Otras cotizaciones (FOGASA y Formación Profesional) (TC1/16) - Régimen Especial del Mar 
		// Otras Cotizaciones (FOGASA) (TC1/25) - Sistema Especial de Manipulado y Empaquetado de Tomate Fresco
	}
	private void createEDTCa51Segment(EMP emp) {
		// TODO 51 Otras cotizaciones (Desempleo) (Tc1/16) - Régimen Especial del Mar
		// Otra cotizaciones (Desempleo y Formación Profesional cuota obrera) (TC1/25) - Sistema Especial de Manipulado y Empaquetado de Tomate Fresco
	}
	/**
	 *  50 Otras cotizaciones (Desempleo, FOGASA y Formación Profesional)
	 *  Resto de regímenes excepto Régimen Especial del Mar, Régimen Especial de Manipulado y Empaquetado y Tomate Fresco y Régimen Especial Agrario (0613).
	 * @param ccc
	 * @param emp
	 */
	private void createEDTCa50Segment(EnterpriseCCC ccc, EMP emp) {
		// TODO 
		if(ccc.getActivity().getType() != SSRegimeType.SEA_WORKERS 
//				&& contract.getRegimeType() != SSRegimeType.TOMATO_MANIPULATION 
				&& ccc.getActivity().getType() != SSRegimeType.AGRICULTURAL){
			Integer base = emp.getEdt().containsKey("EDTBA02")?emp.getEdtSegment("EDTBA02").getBase():0;
			
//			Double amount = new Double((base)*0.283);
			
			Double amount = 0.0;
			try {
				amount += obtainOtherEnterpriseTotal(ccc);
				amount += obtainOtherEmployeeTotal(ccc);
				amount = CommonUtil.round(amount, 2);
			} catch (SQLException e) {
				String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
				AonUtil.addErrorMessage(msg);
			} catch (AonConnectionException e) {
				String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
				AonUtil.addErrorMessage(msg);
			}
			
			if(base != 0){
				EDT edt = emp.getEdtSegment("EDTCA50");
				edt.setTipoElemento("CA");
				edt.setClave(50);
				edt.setBase(base);
				edt.setIndicadorFactorTipo("T");
//				edt.setParteEnteraTipo(28);
//				edt.setParteDecimalFactorTipo(30000);
				edt.setImporte((new Double(amount*100)).intValue());
			}
		}
	}
	private Double obtainOtherEnterpriseTotal(EnterpriseCCC ccc) throws AonConnectionException, SQLException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT sum(amount) FROM salary_cost";
			select += " WHERE type in (" + DeductionType.UNEMPLOYMENT.ordinal() + ", " + DeductionType.JOB_TRAINING.ordinal() + ", " + DeductionType.FOGASA.ordinal() + ")";
			select += " AND salary in (";
			select += "SELECT id FROM salary WHERE domain = " + ccc.getDomain()
					+ " AND start_date >= '" + dateFormatter.format(getStartDate()) + "'" 
					+ " AND end_date <= '" + dateFormatter.format(getEndDate())+"'";
			select += " );";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) return rs.getDouble(1);
			return 0.0;
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}
	
	private Double obtainOtherEmployeeTotal(EnterpriseCCC ccc)  throws AonConnectionException, SQLException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT sum(amount) FROM salary_deduction";
			select += " WHERE type in (" + DeductionType.UNEMPLOYMENT.ordinal() + ", " + DeductionType.JOB_TRAINING.ordinal() + ")";
			select += " AND salary in (";
			select += " SELECT id FROM salary WHERE domain = " + ccc.getDomain() 
					+ " AND ccc = '" + ccc.getCcc() + "'"
					+ " AND start_date >= '" + dateFormatter.format(getStartDate()) + "'" 
					+ " AND end_date <= '" + dateFormatter.format(getEndDate())+"'";
			select += " );";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) return rs.getDouble(1);
			return 0.0;
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}
	
	/**
	 *  32 Cuotas por Invalidez, muerte y supervivencia (IMS) por AT y EP
	 * @param ccc
	 * @param emp
	 */
	private void createEDTCa32Segment(EnterpriseCCC ccc, EMP emp) {
		// TODO 
		
		Double amount = 0.0;
		try {
			amount = obtainImsTotal(ccc);
			amount = CommonUtil.round(amount, 2);
		} catch (SQLException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
		} catch (AonConnectionException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
		}
		
		if(amount != 0){
			EDT edt = emp.getEdtSegment("EDTCA32");
			edt.setTipoElemento("CA");
			edt.setClave(32);
			edt.setImporte((new Double(amount*100)).intValue());
			edt.setSigno(" ");
		}
	}
	private Double obtainImsTotal(EnterpriseCCC ccc) throws AonConnectionException, SQLException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT sum(amount) FROM salary_cost";
//			select += " WHERE cost_concept in ('" + ContextVariable.IMS_ENTERPRISE.getName() + "')";
			select += " WHERE cost_concept in ('IMS_E')";
			select += " AND salary in (";
			select += " SELECT id FROM salary WHERE domain = " + ccc.getDomain() 
					+ " AND ccc = '" + ccc.getCcc() + "'"
					+ " AND start_date >= '" + dateFormatter.format(getStartDate()) + "'" 
					+ " AND end_date <= '" + dateFormatter.format(getEndDate())+"'";
			select += " );";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) return rs.getDouble(1);
			return 0.0;
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}
	/**
	 *  31 Cuotas por Incapacidad Temporal por AT y EP
	 * @param emp
	 */
	private void createEDTCa31Segment(EnterpriseCCC ccc, EMP emp) {
		// TODO 

		Double amount = 0.0;
		try {
			amount = obtainITTotal(ccc);
			amount = CommonUtil.round(amount, 2);
		} catch (SQLException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
		} catch (AonConnectionException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
		}
		if(amount != 0){
			EDT edt = emp.getEdtSegment("EDTCA31");
			edt.setTipoElemento("CA");
			edt.setClave(31);
			edt.setCalificadorClave(null);
			edt.setBase(null);
			edt.setImporte((new Double(amount*100)).intValue());
			edt.setSigno(" ");
		}
	}
	private Double obtainITTotal(EnterpriseCCC ccc) throws AonConnectionException, SQLException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT sum(amount) FROM salary_cost";
			select += " WHERE cost_concept in ('" + ContextVariable.IT_ENTERPRISE.getName() + "')";
			select += " AND salary in (";
			select += " SELECT id FROM salary WHERE domain = " + ccc.getDomain() 
					+ " AND ccc = '" + ccc.getCcc() + "'" 
					+ " AND start_date >= '" + dateFormatter.format(getStartDate()) + "'" 
					+ " AND end_date <= '" + dateFormatter.format(getEndDate())+"'";
			select += " );";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) return rs.getDouble(1);
			return 0.0;
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}
	/**
	 *  TODO 30 Total cuotas AT y EP
	 * @param emp
	 */
	private void createEDTCa30Segment(EMP emp) {
		Integer amount = 0;
		amount += emp.getEdt().containsKey("EDTCA31")?emp.getEdtSegment("EDTCA31").getImporte():0;
		amount += emp.getEdt().containsKey("EDTCA32")?emp.getEdtSegment("EDTCA32").getImporte():0;
		if(amount != 0){
			EDT edt = emp.getEdtSegment("EDTCA30");
			edt.setTipoElemento("CA");
			edt.setClave(30);
			edt.setCalificadorClave(null);
			edt.setBase(null);
			edt.setImporte(amount);
			edt.setSigno(" ");
		}
	}
	/**
	 *  TODO 22 Suma de compensaciones y reducciones
	 * @param emp
	 */
	private void createEDTCa22Segment(EMP emp) {
		Integer amount = 0;
		amount += emp.getEdt().containsKey("EDTCD01")?emp.getEdtSegment("EDTCD01").getImporte():0;
		amount += emp.getEdt().containsKey("EDTCD06")?emp.getEdtSegment("EDTCD06").getImporte():0;
		amount += emp.getEdt().containsKey("EDTCD17")?emp.getEdtSegment("EDTCD17").getImporte():0;
		if(amount != 0){
			EDT edt = emp.getEdtSegment("EDTCA30");
			edt.setTipoElemento("CA");
			edt.setClave(30);
			edt.setCalificadorClave(null);
			edt.setBase(null);
			edt.setImporte(amount);
			edt.setSigno(" ");
		}
	}
	private void createEDTCa21Segment(EMP emp) {
		// TODO 21 Deducción colaboración voluntaria enfermedades comunes y accidente no laboral
		// No es de aplicación para el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
	}
	private void createEDTCa20Segment(EMP emp) {
		// TODO 20 Deducción por contingencias excluidas 
		// No es de aplicación para el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
	}
	private void createEDTCa12Segment(EMP emp) {
		// TODO 12 Aportación a los servicios comunes 
		// No es de aplicación en el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
	}
	private void createEDTCa11Segment(EMP emp) {
		// TODO 11 Otros conceptos
		
//		calificador de clave
//		4 Asistencia sanitaria de Administraciones Públicas
//		8 Cotización adicional Ex.-MUNPAL
//		6 Contratación inferior a 7 días
//			Para contratos de duración efectiva inferior de 7 días, a los que es de aplicación 
//			el incremento del 36% de la cotización empresarial por contingencias comunes, establecido en la Ley 12/2001
//		14 Cotización adicional Ex-Munpal y contratación inferior a 7 días
//		Se utilizará cuando coincida la cotización adicional por clave 8 y por clave 6
//		15 Cotización adicional Bomberos al servicio de las Administraciones y Organismos Públicos
		
	}
	private void createEDTCa03Segment(EMP emp) {
		// 03 Cuota fija trabajador cuenta ajena extranjero (Baja a partir del 1 de enero de 2009) Es de aplicación solo para el Régimen Especial Agrario
	}
	private void createEDTCa02Segment(EMP emp) {
		// TODO 02 Cuota empresarial por Contingencias Comunes
		// Cotización empresarial / Toneladas Régimen Especial de Manipulado y Empaquetado de Tomate Fresco (0134)
	}
	/**
	 *  01 Contingencias Comunes
	 * @param emp
	 */
	private void createEDTCa01Segment(EnterpriseCCC ccc, EMP emp) {
		// TODO 
		Integer base = emp.getEdt().containsKey("EDTBA01")?emp.getEdtSegment("EDTBA01").getBase():0;
//		Double amount = new Double(base*0.283);
		
		Double amount = 0.0;
		try {
			amount += obtainCGCTotalEnterprise(ccc);
			amount += obtainCGCTotalEmployee(ccc);
			amount = CommonUtil.round(amount, 2);
		} catch (SQLException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
		} catch (AonConnectionException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
		}
		
		
		if(base != 0){
			EDT edt = emp.getEdtSegment("EDTCA01");
			edt.setTipoElemento("CA");
			edt.setClave(1);
			edt.setCalificadorClave(null);
			edt.setBase(base);
			edt.setIndicadorFactorTipo("T");
			edt.setParteEnteraTipo(28);
			edt.setParteDecimalFactorTipo(30000);
			edt.setImporte((new Double(amount*100)).intValue());
			edt.setSigno(" ");
		}
	}
	
	private Double obtainCGCTotalEnterprise(EnterpriseCCC ccc) throws AonConnectionException, SQLException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT sum(amount) FROM salary_cost";
			select += " WHERE type in (" + DeductionType.COMMON_CONTINGENCY.ordinal() + ")";
			select += " AND salary in (";
			select += " SELECT id FROM salary WHERE domain = " + ccc.getDomain() 
					+ " AND ccc = '" + ccc.getCcc() + "'"
					+ " AND start_date >= '" + dateFormatter.format(getStartDate()) + "'" 
					+ " AND end_date <= '" + dateFormatter.format(getEndDate())+"'";
			select += " );";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) return rs.getDouble(1);
			return 0.0;
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}
	private Double obtainCGCTotalEmployee(EnterpriseCCC ccc) throws AonConnectionException, SQLException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT sum(amount) FROM salary_deduction";
			select += " WHERE type in (" + DeductionType.COMMON_CONTINGENCY.ordinal() + ")";
			select += " AND salary in (";
			select += " SELECT id FROM salary WHERE domain = " + ccc.getDomain() 
					+ " AND ccc = '" + ccc.getCcc() + "'"
					+ " AND start_date >= '" + dateFormatter.format(getStartDate()) + "'" 
					+ " AND end_date <= '" + dateFormatter.format(getEndDate())+"'";
			select += " );";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) return rs.getDouble(1);
			return 0.0;
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}
	
	
	//////////////////////////////////////
	// COMPENSACION - DEDUCCION TOTALES
	//////////////////////////////////////
	private void createEDTCd30Segment(EMP emp) { 
		// TODO 30 Reducciones. SEA Desempleo Sistema Especial Agrario
	}
	private void createEDTCd29Segment(EMP emp) {
		// TODO 29 Reducciones SEA. Contingencias comunes Sistema Especial Agrario 
	}
	/**
	 *  28 Bonificación por ERE 
	 * @param c
	 * @param emp
	 */
	private void createEDTCd28Segment(EMP emp) {
		Integer amount = 0;
		for(TRA tra: emp.getTrabajadores()){
			for(DAT dat: tra.getDat()){
				amount += dat.getEdl().containsKey("CD28")?dat.getEdlSegment("CD28").getImporte():0;
			}
		}
		if(amount != 0){
			EDT edt = emp.getEdtSegment("EDTCD28");
			edt.setTipoElemento("CD");
			edt.setClave(28);
			edt.setImporte(amount);	
		}
	}
	private void createEDTCd27Segment(EMP emp) {
		// 27 Reducciones REA "Jornadas reales". (Baja a partir del 1 de enero de 2012) Régimen Especial Agrario 
	}
	private void createEDTCd26Segment(EMP emp) {
		// 26 Reducciones REA Cuantía mensual (modalidad G y J). (Baja a partir del 1 de enero de 2012) Régimen Especial Agrario 
	}
	/**
	 *  25 Exención de desempleo hijos<30años Autonomos
	 * @param contract
	 * @param emp
	 */
	private void createEDTCd25Segment(EnterpriseCCC ccc, EMP emp) {
		if(ccc.getActivity().getType() == SSRegimeType.SELF_EMPLOYED){
			Integer amount = 0;
			for(TRA tra: emp.getTrabajadores()){
				for(DAT dat: tra.getDat()){
					amount += dat.getEdl().containsKey("CD25")?dat.getEdlSegment("CD25").getImporte():0;
				}
			}
			if(amount != 0){
				EDT edt = emp.getEdtSegment("EDTCD25");
				edt.setTipoElemento("CD");
				edt.setClave(25);
				edt.setImporte(amount);
			}
		}
	}
	/**
	 *  24 Bonificación I+D+I Régimen General (0111)
	 * @param contract
	 * @param emp
	 */
	private void createEDTCd24Segment(EnterpriseCCC ccc, EMP emp) {
		if(ccc.getActivity().getType() == SSRegimeType.GENERAL){
			Integer amount = 0;
			for(TRA tra: emp.getTrabajadores()){
				for(DAT dat: tra.getDat()){
					amount += dat.getEdl().containsKey("CD24")?dat.getEdlSegment("CD24").getImporte():0;
				}
			}
			if(amount != 0){
				EDT edt = emp.getEdtSegment("EDTCD24");
				edt.setTipoElemento("CD");
				edt.setClave(24);
				edt.setImporte(amount);
			}
		}
	}
	/**
	 *  23 Bonificación Sector industrial incentivado
	 * @param c
	 * @param emp
	 */
	private void createEDTCd23Segment(EMP emp) {
		Integer amount = 0;
		for(TRA tra: emp.getTrabajadores()){
			for(DAT dat: tra.getDat()){
				amount += dat.getEdl().containsKey("CD23")?dat.getEdlSegment("CD23").getImporte():0;
			}
		}
		if(amount != 0){
			EDT edt = emp.getEdtSegment("EDTCD23");
			edt.setTipoElemento("CD");
			edt.setClave(23);
			edt.setImporte(amount);
		}
	}
	/**
	 *  22 Bonificación Form. Empleo Cuantía fija. Excepto Rég. Gral. Artistas (0112)
	 */
	private void createEDTCd22Segment(EnterpriseCCC ccc, EMP emp) {
		if(ccc.getActivity().getType() != SSRegimeType.ARTIST){
			Integer amount = 0;
			for(TRA tra: emp.getTrabajadores()){
				for(DAT dat: tra.getDat()){
					amount += dat.getEdl().containsKey("CD22")?dat.getEdlSegment("CD22").getImporte():0;
				}
			}
			if(amount != 0){
				EDT edt = emp.getEdtSegment("EDTCD22");
				edt.setTipoElemento("CD");
				edt.setClave(22);
				edt.setImporte(amount);
			}
		}
	}
	/**
	 *  21 Bonificación Copa del America (R.D.L. 2146/2004)
	 * @param c
	 * @param emp
	 */
	private void createEDTCd21Segment(EMP emp) {
		Integer amount = 0;
		for(TRA tra: emp.getTrabajadores()){
			for(DAT dat: tra.getDat()){
				amount += dat.getEdl().containsKey("CD21")?dat.getEdlSegment("CD21").getImporte():0;
			}
		}
		if(amount != 0){
			EDT edt = emp.getEdtSegment("EDTCD21");
			edt.setTipoElemento("CD");
			edt.setClave(21);
			edt.setImporte(amount);
		}
	}
	/**
	 *  20 Bonificación Ceuta y Melilla (O. TAS/471/2004)
	 * @param c
	 * @param emp
	 */
	private void createEDTCd20Segment(EMP emp) {
		Integer amount = 0;
		for(TRA tra: emp.getTrabajadores()){
			for(DAT dat: tra.getDat()){
				amount += dat.getEdl().containsKey("CD20")?dat.getEdlSegment("CD20").getImporte():0;
			}
		}
		if(amount != 0){
			EDT edt = emp.getEdtSegment("EDTCD20");
			edt.setTipoElemento("CD");
			edt.setClave(20);
			edt.setImporte(amount);
		}
	}
	private void createEDTCd18Segment(EMP emp) {
		// 18 Reducción por Exencón de desempleo (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario)
	}
	/**
	 *  17 Reducción contingencias comunes excepto I.T. (R.D.L. 16/2001)
	 *  No es de aplicación en el Régimen Especial Agrario (0613)
	 * @param contract
	 * @param emp
	 */
	private void createEDTCd17Segment(EnterpriseCCC ccc, EMP emp) {
		if(ccc.getActivity().getType() != SSRegimeType.AGRICULTURAL){
			Integer amount = 0;
			for(TRA tra: emp.getTrabajadores()){
				for(DAT dat: tra.getDat()){
					amount += dat.getEdl().containsKey("CD17")?dat.getEdlSegment("CD17").getImporte():0;
				}
			}
			if(amount != 0){
				EDT edt = emp.getEdtSegment("EDTCD17");
				edt.setTipoElemento("CD");
				edt.setClave(17);
				edt.setImporte(amount);
			}
		}
	}
	/**
	 *  16 Bonificación por trabajadores con 60 o más años
	 * @param c
	 * @param emp
	 */
	private void createEDTCd16Segment(EMP emp) {
		Integer amount = 0;
		for(TRA tra: emp.getTrabajadores()){
			for(DAT dat: tra.getDat()){
				amount += dat.getEdl().containsKey("CD16")?dat.getEdlSegment("CD16").getImporte():0;
			}
		}
		if(amount != 0){
			EDT edt = emp.getEdtSegment("EDTCD16");
			edt.setTipoElemento("CD");
			edt.setClave(16);
			edt.setImporte(amount);
		}
	}
	/**
	 *  13 Bonificación minusvalidos en Centros Especiales de Empleo
	 * @param c
	 * @param emp
	 */
	private void createEDTCd13Segment(EMP emp) {
		Integer amount = 0;
		for(TRA tra: emp.getTrabajadores()){
			for(DAT dat: tra.getDat()){
				amount += dat.getEdl().containsKey("CD13")?dat.getEdlSegment("CD13").getImporte():0;
			}
		}
		if(amount != 0){
			EDT edt = emp.getEdtSegment("EDTCD13");
			edt.setTipoElemento("CD");
			edt.setClave(13);
			edt.setImporte(amount);
		}
	}
	private void createEDTCd12Segment(EMP emp) {
		// TODO 12 Bonificación por Ley 19/94 (Registro Canario) Régimen Especial del Mar
	}
	/**
	 *  11 Bonificación por formación teórica a distancia
	 * @param c
	 * @param emp
	 */
	private void createEDTCd11Segment(EMP emp) {
		Integer amount = 0;
		for(TRA tra: emp.getTrabajadores()){
			for(DAT dat: tra.getDat()){
				amount += dat.getEdl().containsKey("CD11")?dat.getEdlSegment("CD11").getImporte():0;
			}
		}
		if(amount != 0){
			EDT edt = emp.getEdtSegment("EDTCD11");
			edt.setTipoElemento("CD");
			edt.setClave(11);
			edt.setImporte(amount);
		}
	}
	/**
	 *  10 Bonificación por formación teórica presencial
	 * @param c
	 * @param emp
	 */
	private void createEDTCd10Segment(EMP emp) {
		Integer amount = 0;
		for(TRA tra: emp.getTrabajadores()){
			for(DAT dat: tra.getDat()){
				amount += dat.getEdl().containsKey("CD10")?dat.getEdlSegment("CD10").getImporte():0;
			}
		}
		if(amount != 0){
			EDT edt = emp.getEdtSegment("EDTCD10");
			edt.setTipoElemento("CD");
			edt.setClave(10);
			edt.setImporte(amount);
		}
	}
	/**
	 *  07 Bonificaciones
	 * @param contract
	 * @param emp
	 */
	private void createEDTCd07Segment(EMP emp) {
		Integer amount = 0;
		for(TRA tra: emp.getTrabajadores()){
			for(DAT dat: tra.getDat()){
				amount += dat.getEdl().containsKey("CD07")?dat.getEdlSegment("CD07").getImporte():0;
			}
		}
		if(amount != 0){
			EDT edt = emp.getEdtSegment("EDTCD07");
			edt.setTipoElemento("CD");
			edt.setClave(7);
			edt.setImporte(amount);
		}
	}
	
	/**
	 *  06 Reducciones
	 * @param contract
	 * @param emp
	 */
	private void createEDTCd06Segment(EMP emp) {
		Integer amount = 0;
		for(TRA tra: emp.getTrabajadores()){
			for(DAT dat: tra.getDat()){
				amount += dat.getEdl().containsKey("CD06")?dat.getEdlSegment("CD06").getImporte():0;
			}
		}	
		if(amount != 0){
			EDT edt = emp.getEdtSegment("EDTCD06");
			edt.setTipoElemento("CD");
			edt.setClave(6);
			edt.setImporte(amount);
		}
	}
	private void createEDTCd05Segment(EMP emp) {
		// TODO 5 IT O.M. 3/4/73. Minería del Carbón
	}
	/**
	 *  03 IT por AT y EP
	 *  No es de aplicación en el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
	 * @param contract
	 * @param emp
	 */
	private void createEDTCd03Segment(EnterpriseCCC ccc, EMP emp) {
		if(ccc.getActivity().getType() != SSRegimeType.ARTIST && ccc.getActivity().getType() != SSRegimeType.COAL_MINING){
			Integer amount = 0;
			for(TRA tra: emp.getTrabajadores()){
				for(DAT dat: tra.getDat()){
					amount += dat.getEdl().containsKey("CD03")?dat.getEdlSegment("CD03").getImporte():0;
				}
			}	
			if(amount != 0){
				EDT edt = emp.getEdtSegment("EDTCD03");
				edt.setTipoElemento("CD");
				edt.setClave(3);
				edt.setImporte(amount);
			}
		}
	}
	/**
	 *  01 IT enfermedad común y accidente no laboral
	 *  No es de aplicación en el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
	 * @param contract
	 * @param emp
	 */
	private void createEDTCd01Segment(EnterpriseCCC ccc, EMP emp) {
		if(ccc.getActivity().getType() != SSRegimeType.ARTIST && ccc.getActivity().getType() != SSRegimeType.COAL_MINING){
			Integer amount = 0;
			for(TRA tra: emp.getTrabajadores()){
				for(DAT dat: tra.getDat()){
					amount += dat.getEdl().containsKey("CD01")?dat.getEdlSegment("CD01").getImporte():0;
				}
			}	
			if(amount != 0){
				EDT edt = emp.getEdtSegment("EDTCD01");
				edt.setTipoElemento("CD");
				edt.setClave(1);
				edt.setImporte(amount);
			}
		}
	}
	
	//////////////////////////
	// BASES TOTALES
	//////////////////////////
	/**
	 *  42 Base exclusiva de AT y EP sin cotización de Otras Cotizaciones
	 * @param contract
	 * @param emp
	 */
	private void createEDTBa42Segment(EMP emp) {
		Integer base = 0;
		for(TRA tra: emp.getTrabajadores()){
			for(DAT dat: tra.getDat()){
				base += dat.getEdl().containsKey("BA42")?dat.getEdlSegment("BA42").getImporte():0;
			}
		}	
		if(base != 0){
			EDT edt = emp.getEdtSegment("EDTBA42");
			edt.setTipoElemento("BA");
			edt.setClave(42);
			edt.setBase(base);
		}
	}
	private void createEDTBa41Segment(EMP emp) {
		// 41 Contingencias Comunes y FOGASA(Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	}
	private void createEDTBa38Segment(EMP emp) {
		// 38 Cotización exclusivamente por FOGASA(Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario) 
	}
	private void createEDTBa37Segment(EMP emp) {
		// 37 Cotización jornadas reales y FOGASA excluido desempleo(Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario) 
	}
	private void createEDTBa36Segment(EMP emp) {
		// 36 Base exclusiva Desempleo/FOGASA tipo total (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario) 
	}
	private void createEDTBa35Segment(EMP emp) {
		// 35 Base fija cotización trabajadores cuenta ajena extranjeros (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario) 
	}
	private void createEDTBa34Segment(EMP emp) {
		// 34 Base de AT en vacaciones (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	}
	private void createEDTBa33Segment(EMP emp) {
		// 33 Base de cotización exclusiva de Otras Cotizaciones en situación de IT/maternidad/riesgo durante el embarazo. 
		// Trabajadores no fijos (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario) 
	}
	private void createEDTBa32Segment(EMP emp) {
		// 32 Base de cotización Jornadas Reales en situación de IT por desempleo empresarial y FOGASA. 
		// (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	}
	private void createEDTBa31Segment(EMP emp) {
		// 31 Contingencias comunes trajadores no fijos (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario) 
	}
	private void createEDTBa30Segment(EMP emp) {
		// 30 Cotización por Jornadas Reales (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	}
	private void createEDTBa28Segment(EMP emp) {
		// 28 Diferencia en bases (contingencias comunes y salario normalizado), cotización exclusivamente empresarial
		// Solo para liquidaciones anteriores al año 2002 del Régimen Especial de la Minería del Carbón (0911). Baja a partir de 2002 
	}
	private void createEDTBa23Segment(EMP emp) {
		// 23 Base de cotización tipo total desempleo y FOGASA(Baja a partir del 1 de enero de 2012) . (Régimen Especial Agrario)
	}
	/**
	 *  22 Base de cotización empresarial por AT y EP y Otras Cotizaciones
	 * @param contract
	 * @param emp
	 */
	private void createEDTBa22Segment(EMP emp) {
		Integer base = 0;
		for(TRA tra: emp.getTrabajadores()){
			for(DAT dat: tra.getDat()){
				base += dat.getEdl().containsKey("BA20")?dat.getEdlSegment("BA20").getImporte():0;
				base += dat.getEdl().containsKey("BA22")?dat.getEdlSegment("BA22").getImporte():0;
			}
		}	
		if(base != 0){
			EDT edt = emp.getEdtSegment("EDTBA22");
			edt.setTipoElemento("BA");
			edt.setClave(22);
			edt.setBase(base);
		}
	}
	/**
	 *  21 Base de cotización empresarial por contingencias comunes
	 *  Base de cotización empresarial desempleo y FOGASA (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	 * @param contract
	 * @param emp
	 */
	private void createEDTBa21Segment(EnterpriseCCC ccc, EMP emp) {
		if(ccc.getActivity().getType() != SSRegimeType.AGRICULTURAL && CommonUtil.getYear(getStartDate()) < 2012) {
			Integer base = 0;
			for(TRA tra: emp.getTrabajadores()){
				for(DAT dat: tra.getDat()){
					base += dat.getEdl().containsKey("BA20")?dat.getEdlSegment("BA20").getImporte():0;
					base += dat.getEdl().containsKey("BA21")?dat.getEdlSegment("BA21").getImporte():0;
				}
			}	
			if(base != 0){
				EDT edt = emp.getEdtSegment("EDTBA21");
				edt.setTipoElemento("BA");
				edt.setClave(21);
				edt.setBase(base);
			}
		}
	}
	private void createEDTBa11Segment(EnterpriseCCC ccc, EMP emp) {
		// TODO enterpriseNonStructural && employeeNonStructural 
		// 11 Horas extras no estructurales / Otras horas extras desde 1/1/98
		// No se podrá utilizar para e Régimen General de Artistas (0112) , ni Régimen Especial de Minería del Carbón (0911)
		if(ccc.getActivity().getType() != SSRegimeType.ARTIST && ccc.getActivity().getType() != SSRegimeType.COAL_MINING){
			Double enterpriseNonStructural = 23.60;
			Double employeeNonStructural = 4.70;
			Integer base = 0;
			for(TRA tra: emp.getTrabajadores()){
				for(DAT dat: tra.getDat()){
					base += dat.getEdl().containsKey("BA11")?dat.getEdlSegment("BA11").getImporte():0;
				}
			}	
			if(base != 0){
				Integer amount = (int)(CommonUtil.round(((new Double(base))/100)*((enterpriseNonStructural + employeeNonStructural)/100))*100);
				EDT edt = emp.getEdtSegment("EDTBA11");
				edt.setTipoElemento("BA");
				edt.setClave(11);
				edt.setBase(base);
				edt.setIndicadorFactorTipo("T");
				edt.setParteEnteraTipo((int)(enterpriseNonStructural + employeeNonStructural));
				edt.setParteDecimalFactorTipo((int)(((enterpriseNonStructural + employeeNonStructural)-(int)(enterpriseNonStructural + employeeNonStructural))*100));
				edt.setImporte(amount);
			}
		}
	}
	private void createEDTBa10Segment(EnterpriseCCC ccc, EMP emp) {
		// TODO enterpriseNonStructural && employeeNonStructural 
		// 10 Horas extras estructurales / Causa de fuerza mayor desde 1/1/98
		// No se podrá utilizar para el Régimen General de Artistas (0112), ni Régimen Especial de Minería del Carbón (0911).
		if(ccc.getActivity().getType() != SSRegimeType.ARTIST && ccc.getActivity().getType() != SSRegimeType.COAL_MINING){
			Double enterpriseNonStructural = 12.00;
			Double employeeNonStructural = 2.00;
			Integer base = 0;
			for(TRA tra: emp.getTrabajadores()){
				for(DAT dat: tra.getDat()){
					base += dat.getEdl().containsKey("BA10")?dat.getEdlSegment("BA10").getImporte():0;
				}
			}	
			if(base != 0){
				Integer amount = (int)(CommonUtil.round(((new Double(base))/100)*((enterpriseNonStructural + employeeNonStructural)/100))*100);
				EDT edt = emp.getEdtSegment("EDTBA10");
				edt.setTipoElemento("BA");
				edt.setClave(10);
				edt.setBase(base);
				edt.setIndicadorFactorTipo("T");
				edt.setParteEnteraTipo((int)(enterpriseNonStructural + employeeNonStructural));
				edt.setParteDecimalFactorTipo((int)(((enterpriseNonStructural + employeeNonStructural)-(int)(enterpriseNonStructural + employeeNonStructural))*100));
				edt.setImporte(amount);
			}
		}
	}
	private void createEDTBa09Segment(EnterpriseCCC ccc, EMP emp) {
		// TODO 09 Horas complementarias No se utilizará para Régimen General de Artistas (0112)
		if(ccc.getActivity().getType() != SSRegimeType.ARTIST){
			Integer base = 0;
			for(TRA tra: emp.getTrabajadores()){
				for(DAT dat: tra.getDat()){
					base += dat.getEdl().containsKey("BA09")?dat.getEdlSegment("BA09").getImporte():0;
				}
			}	
			if(base != 0){
				EDT edt = emp.getEdtSegment("EDTBA09");
				edt.setTipoElemento("BA");
				edt.setClave(9);
				edt.setBase(base);
			}
		}
	}
	private void createEDTBa08Segment(EMP emp) {
		// 8 Diferencia Bases (Contingencias comunes y salario normalizado)
		// Solo para liquidaciones anteriores al año 2002 del Régimen Especial de la Minería del Carbón (0911). Baja a partir de 2002
	}
	private void createEDTBa07Segment(EMP emp) {
		// 7 AT y EP sin horas extraordinarias
		// No será de utilización para Régimen General de Artistas (0112), ni Régimen Especial de Minería del Carbón (0911). Baja a partir de 2002
	}
	private void createEDTBa06Segment(EMP emp) {
		// TODO 6 Importe percepciones Integras (Artistas)
	}
	private void createEDTBa05Segment(EMP emp) {
		// TODO 5 Exceso del tope (Minería del Carbón)
	}
	/**
	 *  02 AT y EP
	 */
	private void createEDTBa02Segment(EMP emp) {
		Integer base = 0;
		for(TRA tra: emp.getTrabajadores()){
			for(DAT dat: tra.getDat()){
				base += dat.getEdl().containsKey("BA00")?dat.getEdlSegment("BA00").getImporte():0;
				base += dat.getEdl().containsKey("BA02")?dat.getEdlSegment("BA02").getImporte():0;
			}
		}
		if(base != 0){
			EDT edt = emp.getEdtSegment("EDTBA02");
			edt.setTipoElemento("BA");
			edt.setClave(2);
//		edt.setCalificadorClave(null);
			edt.setBase(base);
//		edt.setIndicadorFactorTipo(null);
//		edt.setParteEnteraTipo(0);
//		edt.setParteDecimalFactorTipo(0);
//		edt.setImporte(null);
//		edt.setSigno(" ");
		}
	}
	/**
	 *  01 Contingencias comunes
	 * @param c
	 * @param emp
	 */
	private void createEDTBa01Segment(EMP emp) {
		Integer base = 0;
		for(TRA tra: emp.getTrabajadores()){
			for(DAT dat: tra.getDat()){
				base += dat.getEdl().containsKey("BA00")?dat.getEdlSegment("BA00").getImporte():0;
				base += dat.getEdl().containsKey("BA01")?dat.getEdlSegment("BA01").getImporte():0;
			}
		}
		if(base != 0){
			EDT edt = emp.getEdtSegment("EDTBA01");
			edt.setTipoElemento("BA");
			edt.setClave(1);
//		edt.setCalificadorClave(null);
			edt.setBase(base);
//		edt.setIndicadorFactorTipo(null);
//		edt.setParteEnteraTipo(0);
//		edt.setParteDecimalFactorTipo(0);
//		edt.setImporte(null);
//		edt.setSigno(" ");
		}
	}
	
	/**
	 * MPG - Modalidad de PaGo
	 * Obligatorio para pago electronico, saldos acreedores y cargo en cuenta
	 * 
	 * @param ccc
	 * @return
	 */
	private MPG createMPGRecord(EnterpriseCCC ccc) {
		MPG mpg = new MPG();
		// TODO: how obtain "modalidad de pago"
//		~ Saldo Acreedor
//		C Cargo en Cuenta
//		V Pago electrónico
		RegistryBank bank = obtainBank(ccc);
		if(bank==null || bank.getId()==null){
			mpg.setSolicitudModalidadPago("V");
		} else {
			mpg.setSolicitudModalidadPago("C");
			mpg.setCondigoCuentaCliente(bank.getBankAccount().getBban());
			
//			Obligatorio para Cargo en Cuenta y Saldos Acreedores
			DocumentType docType = ccc.getActivity().getEnterprise().getRegistry().getDocumentType();
			if(docType==DocumentType.NIF){
				mpg.setTipoIdentificadorTitular("1");
			} else if(docType==DocumentType.PASSPORT){
				mpg.setTipoIdentificadorTitular("2");
			} else if(docType==DocumentType.NIE){
				mpg.setTipoIdentificadorTitular("6");
			} else if(docType==DocumentType.CIF){
				mpg.setTipoIdentificadorTitular("9");
			} else {
				mpg.setTipoIdentificadorTitular("9");
			}
			
			// Obligatorio para Cargo en Cuenta y Saldos Acreedores. Ajustado a la derecha, relleno a ceros por la izquierda
			mpg.setIdentificadorTitular(autoComplete(ccc.getActivity().getEnterprise().getRegistry().getDocument(), 14, "0", true));
			// Obligatorio para Saldos Acreedores
			mpg.setNombreTitular("");
		}
		
		return mpg;
	}
	
	
	private boolean isFanTestEnvironmentActive() {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT value FROM app_param";
			select += " WHERE domain = " + DomainManager.getCurrentDomain();
			select += " AND name = '" + PayrollAppParamsController.FAN_TEST_ENVIRONMENT_ACTIVE + "';";
			
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
	private RegistryBank obtainBank(EnterpriseCCC ccc) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT value FROM app_param";
			select += " WHERE domain = " + ccc.getDomain();
			select += " AND name = '" + PayrollAppParamsController.SS_PAYMENT_BANK_ACCOUNT_KEY + "';";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				String id = rs.getString(1);
				if(id!=null){
					try {
						IManagerBean bean = BeanManager.getManagerBean(RegistryBank.class);
						Criteria criteria = new Criteria();
						criteria.setSkipDomainFilter(true);
						criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_BANK_DOMAIN), ccc.getDomain() );
						criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_BANK_ID), Integer.parseInt(id) );
						List<ITransferObject> bankList = bean.getList(criteria);
						if(bankList.size()>0){
							return (RegistryBank) bankList.get(0);
						}
					} catch (ManagerBeanException e) {
						// nothing to do
					}
				}
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
	private Mutual obtainMutual(EnterpriseCCC ccc) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT value FROM app_param";
			select += " WHERE domain = " + ccc.getDomain();
			select += " AND name = '" + PayrollAppParamsController.SS_MUTUAL_KEY + "';";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				String value = rs.getString(1);
				for(Mutual mutual: Mutual.values()){
					if(mutual.getValue().equals(value)){
						return mutual;
					}
				}
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
	
	private Salary getSalary(Contract c) {
		Calendar startCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();
		startCal.set(Calendar.YEAR, year);
		startCal.set(Calendar.MONTH, endMonth.ordinal());
		startCal.set(Calendar.DAY_OF_MONTH, 1);
		endCal.set(Calendar.YEAR, year);
		endCal.set(Calendar.MONTH, endMonth.ordinal());
		endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
		Salary salary = null;
		try {
			IManagerBean bean = BeanManager.getManagerBean(Salary.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_CONTRACT_ID), c.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_TYPE), SalaryType.SALARY);
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.SALARY_START_DATE), startCal.getTime());
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.SALARY_END_DATE), endCal.getTime());
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				salary = (Salary) list.get(0);
			}
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return salary;
	}
	
	private List<ITransferObject> obtainContracts(EnterpriseCCC ccc, Date startDate, Date endDate) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(Contract.class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ENTERPRISE_CCC_ID), ccc.getId());
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_START_DATE), endDate);
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_END_DATE), startDate);
		Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_END_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));			
//		criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_PERSON_REGISTRY_DOCUMENT));
		criteria.addOrder("Contract.person.socialSecurityNumber");
		
		return bean.getList(criteria);
	}
	
	private Integer differenceBetweenDates(Date from, Date to) {
		Integer diffDays = new Integer(0);
		final Double MS_PER_DAY = new Double(1000 * 60 * 60 * 24);
		if(from.before(to)) {
			diffDays = (int)((Math.floor((to.getTime() - from.getTime()) / MS_PER_DAY + 0.5d) + 1));
		}
		return diffDays;
	}

	
	
}

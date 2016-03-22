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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
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
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.fan.FAN;
import com.esferalia.aon.file.payroll.fan.FANAgricultural;
import com.esferalia.aon.file.payroll.fan.FANGeneral;
import com.esferalia.aon.file.payroll.fan.IFanFactory;
import com.esferalia.aon.file.payroll.fan.data.AYN;
import com.esferalia.aon.file.payroll.fan.data.DAT;
import com.esferalia.aon.file.payroll.fan.data.EDL;
import com.esferalia.aon.file.payroll.fan.data.EMP;
import com.esferalia.aon.file.payroll.fan.data.ETI;
import com.esferalia.aon.file.payroll.fan.data.MPG;
import com.esferalia.aon.file.payroll.fan.data.RZS;
import com.esferalia.aon.file.payroll.fan.data.TCT;
import com.esferalia.aon.file.payroll.fan.data.TRA;
import com.esferalia.aon.payroll.BonusConcept;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractBonus;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBonus;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.LiquidationType;
import com.esferalia.aon.payroll.enumeration.Mutual;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.enumeration.ss.T54;
import com.esferalia.aon.payroll.enumeration.ss.T86;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

public class FANWriter implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final String FAN 				= "FAN";
	private final String WINSUITE_VERSION 	= "71WSxxx";
	private final String TESTING_CHECK		= "P";
	
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
	
	private IFanFactory fanFactory;
	
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

	public FileOutput createFAN(List<EnterpriseCCC> list, LiquidationType liquidationType, Integer year, Month startMonth, Month endMonth ) throws ManagerBeanException {
		this.year = year;
		this.startMonth = startMonth; 
		this.endMonth= endMonth ; 
		this.liquidationType = liquidationType;

		analizeData(list, liquidationType);
		
		try {
			ETI eti = createETIRecord( isFanTestEnvironmentActive(), list, liquidationType, year, startMonth, endMonth );
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			PrintWriter writer = new PrintWriter(outputStream);
			FileFiller fan = new FAN(eti, writer);
			FileOutput output = new FileOutput();
			output.setErrors(fan.create());
			output.setContent(outputStream.toByteArray());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	public ETI createETIRecord(boolean testFile, List<EnterpriseCCC> list, LiquidationType liquidationType, Integer year, Month startMonth, Month endMonth ) throws ManagerBeanException {
		ETI eti = new ETI();
		this.totalContractSum = 0;
		
		String authorizationKey = getAuthorizationKey();
		if(StringUtils.isNotBlank(authorizationKey)){
			eti.setClave(StringUtils.leftPad(authorizationKey, 8, '0'));
		} else {
			AonUtil.addErrorMessage("No se ha definido la clave de autorización.");
		}
		
		eti.setPrueba(testFile?TESTING_CHECK:WHITESPACE_1);
		for (EnterpriseCCC ccc: list) {
			if(ccc.getType()==CCCType.AGRICULTURAL){
				fanFactory = new FANAgricultural();
			} else {
				fanFactory = new FANGeneral();
			}
			
			EMP emp = createEMPrecord(ccc);
			if(emp!=null){
				eti.getEmpresas().add(emp);
			}
		}
		eti.setProveedorNomina(T86.T86_498.getCode());
		setEti( eti );
		return getEti();
	}
	
	private EMP createEMPrecord(EnterpriseCCC ccc) throws  ManagerBeanException {
		EMP emp = new EMP();
		emp.setCodigoCuentaCotizacionSeguridadSocial(PayrollUtils.getInstance().getRegimeCode(ccc)+ccc.getCcc());
		
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
    	EnterpriseCCC mainCcc = obtainMainCCC(ccc);
		emp.setCodigoCuentaCotizacionPrincipal(PayrollUtils.getInstance().getRegimeCode(mainCcc)+mainCcc.getCcc());
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
			Integer previousPerson = null;
			TRA tra = null;
//			System.out.println("STARTING ...");
//			Date start = new Date();
//			Date contractLoop = new Date();
			for(ITransferObject to: list){
				Contract contract = (Contract) to;
				Salary salary = null;
				if(liquidationType==LiquidationType.L00){
					salary = getSalary(contract, SalaryType.SALARY);
				} else if(liquidationType==LiquidationType.L13){
					salary = getSalary(contract, SalaryType.SETTLE);
					if(salary==null || salary.getCommonBase()==null || salary.getCommonBase()<=0.0d){
						salary = null;
					}
				}
				if(salary!=null && contract.getRegimeType()!=SSRegimeType.SELF_EMPLOYED){
					List<DAT> datList = createDATRecords(contract);
					if(datList!=null && datList.size()>0){
						if(previousPerson==null || !previousPerson.equals(contract.getPerson().getId())){
							tra = createTRARecord(contract, emp);
							previousPerson = contract.getPerson().getId();
							emp.getTrabajadores().add(tra);
						}
						tra.getDat().addAll(datList);
						++totalContractSum;
					}
				}
//				Date end = new Date();
//				System.out.println((end.getTime()-contractLoop.getTime()) + " .... " + contract.getPerson().getFullName());
//				contractLoop = end;
			}
//			Date end = new Date();
//			System.out.println((end.getTime()-start.getTime()));
			emp.getTcTotales().add(createTCTRecord(ccc));
//			System.out.println("STARTING TOTALS");
//			start = new Date();
			createEDTRecords(ccc, emp, list);
//			end = new Date();
//			System.out.println((end.getTime()-start.getTime()));
		
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
		rzs.setRazonSocial(obtainUtf(enterprise.getRegistry().getName()));
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
		String pais = WHITESPACE_3;
		String doc = autoComplete(contract.getPerson().getRegistry().getDocument(), 14, "0", true);
		String ipf = StringUtils.isBlank(tipo)?"9":tipo;
		ipf += StringUtils.isBlank(pais)?WHITESPACE_3:pais; 
		ipf += doc;
		tra.setIpf(ipf);
		tra.setAyn(createAYNRecord(contract));
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
		String ap1 = obtainUtf(contract.getPerson().getFirstSurname());
		String ap2 = obtainUtf(contract.getPerson().getSecondSurname());
		String n = obtainUtf(contract.getPerson().getName());
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
		
		if(liquidationType==LiquidationType.L00){
			Salary salary = getSalary(contract, SalaryType.SALARY);
			List<ITransferObject> salaryDataList = PayrollUtils.getInstance().getSalaryDataList(salary, true);
			
			if(isLessThan7DaysContract(contract)){
				createDATRecord(datList, contract, salaryDataList, autoComplete("C", 7, " ", true), getContractDaysOrHours(contract, salaryDataList));
			} else {
				Integer daysHours = getContractDaysOrHours(contract, salaryDataList);
				Integer discountDaysHours = 0;
				if(isErePartial(salary, salaryDataList)){
					ContractCode code = getContractCode(contract);
					discountDaysHours = Double.valueOf(CommonUtil.round(getEreDays(salary, salaryDataList), 0)).intValue();
					if(code!=null && !code.getValue().startsWith("1") && !code.getValue().startsWith("4")){
						Double dayHours = obtainDayHours(contract);
						discountDaysHours = Double.valueOf(CommonUtil.round(discountDaysHours * dayHours, 0)).intValue();
					}
				}
				if(isEreTotal(salary, salaryDataList)){
					daysHours = null;
				}
				if(daysHours!=null){
					createDATRecord(datList, contract, salaryDataList, null, daysHours-discountDaysHours);
				}
			}
			
			// TODO comprobar que situaciones implican un nuevo segmento de tipo DAT
			// TODO
//			if(isPartialStrike(contract)){
//				datList.add(createDATRecord(contract, autoComplete("H", 1, " ", true), getContractDaysOrHours(contract)));
//			}
			// TODO
//			if(isMoonlighting(contract)){
//				datList.add(createDATRecord(contract, autoComplete("P", 2, " ", true), getContractDaysOrHours(contract)));
//			}
			if(StringUtils.isNotBlank(getJournalReduction(contract))){
				Integer itDays = getItDays(contract);
				ContractCode code = getContractCode(contract);
				if(code!=null && !code.getValue().startsWith("1") && !code.getValue().startsWith("4")){
					Double dayHours = obtainDayHours(contract);
					itDays = Double.valueOf(CommonUtil.round(itDays * dayHours, 0)).intValue();
				}
				createDATRecord(datList, contract, salaryDataList, autoComplete(getJournalReduction(contract), 3, " ", true), itDays);
			}
			// TODO
//			if(isMonthSalary(contract)){
//				datList.add(createDATRecord(contract, autoComplete("M", 4, " ", true), getContractDaysOrHours(contract)));
//			}
			// TODO
//			if(isNoRetributionDischarge(contract)){
//				datList.add(createDATRecord(contract, autoComplete("A", 5, " ", true), getContractDaysOrHours(contract)));
//			}
			if(StringUtils.isNotBlank(getOthers(salary, salaryDataList))){
				Integer ereDays = getEreDays(salary, salaryDataList);
				ContractCode code = getContractCode(contract);
				if(code!=null && !code.getValue().startsWith("1") && !code.getValue().startsWith("4")){
					Double dayHours = obtainDayHours(contract);
					ereDays = Double.valueOf(CommonUtil.round(ereDays * dayHours, 0)).intValue();
				}
				createDATRecord(datList, contract, salaryDataList, autoComplete(getOthers(salary, salaryDataList), 6, " ", true), ereDays);
			}
		} else if(liquidationType==LiquidationType.L13){
			Salary salary = getSalary(contract, SalaryType.SETTLE);
			if(salary==null){
				salary = getSalary(contract, SalaryType.SALARY);
			}
			Integer notEnjoyedVacationDays = getNotEnjoyedVacationDays(contract);
			if(salary!=null && notEnjoyedVacationDays!=null && notEnjoyedVacationDays>0){
				List<ITransferObject> salaryDataList = PayrollUtils.getInstance().getSalaryDataList(salary, true);
				createDATRecord(datList, contract, salaryDataList, null, notEnjoyedVacationDays);
			}
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
	private DAT createDATRecord(List<DAT> list, Contract contract, List<ITransferObject> salaryDataList, String indicadorPerfil, Integer diasHoras) {
		DAT dat = new DAT();
		dat.setMes(endMonth.ordinal()+1);
		dat.setIndicadoresPerfil(indicadorPerfil);
		dat.setDiasHoras(diasHoras);
		dat.setDiasAlta(getContractDischargeDays(contract));
		dat.setIndicadorCotizacion(getQuoteIndicator(contract, salaryDataList));
		dat.setIndicadorVacaciones(getVacationIndicator(contract));
		dat.setClaveJornadasColectivo(getCollectiveJournalHours(contract));
		dat.setEspecificos(getSpecifics(contract));
		dat.setIndReduccionBoni(getBonificationReduction(contract));
		dat.setGrupoCotizacion(getQuoteGroup(contract));
		dat.setTipoContrato(getSourceContractType(contract));
		ContractCode code = getContractCode(contract);
		dat.setClaveContrato(code==null?"000":code.getValue());
		dat.setEpigrafeAtEp(getAtEpEpigraph(contract));
		dat.setEpigrafeSecundario(getSecondariEpigraph(contract));
		dat.setOcupacion(getContractOccupation(contract));
		dat.setModalidadCotizacion(getQuoteMode(salaryDataList));
		dat.setIndDiscapacidad(getHandicapIndicator(contract));
		dat.setIndRelacion(getEmploymentRelation(contract));
		dat.setColectivoPeculiar(getParticularGroup(contract));
		dat.setInfoComplementaria(null);
		dat.setCotizacionDesempleo(null);
		list.add(dat);
		createEDLRecords(contract, salaryDataList, dat, list);
		return dat;
	}
	
	private boolean isContractLeave(Contract contract){
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT * FROM salary_payment";
			select += " WHERE payment_concept IN ('" + ContextVariable.PREST_IT.getName() + "')";
			select += " AND salary in (";
			select += " SELECT id FROM salary WHERE contract = " + contract.getId()
					+ " AND end_date >= '" + dateFormatter.format(getStartDate()) + "'" 
					+ " AND end_date <= '" + dateFormatter.format(getEndDate())+"'";
			select += " );";
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (AonConnectionException e) {
			// do nothing
			System.out.println("isContractLeave - AonConnectionException");
		} catch (SQLException e) {
			// do nothing
			System.out.println("isContractLeave - SQLException");
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return false;
	}
	
	private boolean isContractLeaveMaternity(Contract contract){
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT * FROM salary_payment";
			select += " WHERE payment_concept IN ('" + ContextVariable.MATERNITY.getName() + "')";
			select += " AND salary in (";
			select += " SELECT id FROM salary WHERE contract = " + contract.getId()
					+ " AND end_date >= '" + dateFormatter.format(getStartDate()) + "'" 
					+ " AND end_date <= '" + dateFormatter.format(getEndDate())+"'";
			select += " );";
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (AonConnectionException e) {
			// do nothing
			System.out.println("isContractLeave - AonConnectionException");
		} catch (SQLException e) {
			// do nothing
			System.out.println("isContractLeave - SQLException");
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return false;
	}
	
//	private Integer getItDays(Contract contract){
//		Integer ecssDays = getECSSDays(contract);
//		if(ecssDays!=null && ecssDays>0){
//			return ecssDays;
//		}
//		Integer atepDays = getATEPDays(contract);
//		if(atepDays!=null && atepDays>0){
//			return atepDays;
//		}
//		return 0;
//	}
//	private Integer getECSSDays(Contract contract){
//		Connection conn = null;
//		PreparedStatement ps = null;
//		try {
//			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
//			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
//			String select = "SELECT expression FROM contract_data";
//			select += " WHERE contract = " + contract.getId();
//			select += " AND start_date >= '" + dateFormatter.format(getStartDate()) + "'";
//			select += " AND end_date <= '" + dateFormatter.format(getEndDate()) + "'";
//			select += " AND name IN ('DIAS_ENFERMEDAD_COMUN_4_15', 'DIAS_ENFERMEDAD_COMUN_16_20', 'DIAS_ENFERMEDAD_COMUN_21')";
//			select += " ;";
//			ps = conn.prepareStatement(select);
//			ResultSet rs = ps.executeQuery();
//			Integer days = 0;
//			if(rs.next()) 
//				days += (int)rs.getDouble(1);
//			return days;
//		} catch (AonConnectionException e) {
//			// do nothing
//			System.out.println("ecss days - AonConnectionException");
//		} catch (SQLException e) {
//			// do nothing
//			System.out.println("ecss days - SQLException");
//		} finally {
//			DatabaseUtil.closeQuietly(ps);
//			DatabaseUtil.closeQuietly(conn);
//		}
//		return 0;
//	}
//	private Integer getATEPDays(Contract contract){
//		Connection conn = null;
//		PreparedStatement ps = null;
//		try {
//			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
//			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
//			String select = "SELECT expression FROM contract_data";
//			select += " WHERE contract = " + contract.getId();
//			select += " AND start_date >= '" + dateFormatter.format(getStartDate()) + "'";
//			select += " AND end_date <= '" + dateFormatter.format(getEndDate()) + "'";
//			select += " AND name = '" + ContextVariable.OCCUPATIONAL_DISEASE_DAYS.getName() + "'";
//			select += " ;";
//			ps = conn.prepareStatement(select);
//			ResultSet rs = ps.executeQuery();
//			if(rs.next()) 
//				return (int)rs.getDouble(1);
//		} catch (AonConnectionException e) {
//			// do nothing
//			System.out.println("atep days - AonConnectionException");
//		} catch (SQLException e) {
//			// do nothing
//			System.out.println("atep days - SQLException");
//		} finally {
//			DatabaseUtil.closeQuietly(ps);
//			DatabaseUtil.closeQuietly(conn);
//		}
//		return 0;
//	}

	private Integer getItDays(Contract contract){
		return getItDays(contract, 0);
	}
	
	private Integer getItDays(Contract contract, Integer startIncrease){
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			
			String sqlStart = dateFormatter.format(getStartDate());
			String sqlEnd = dateFormatter.format(getEndDate());
			String select = "SELECT sum( DATEDIFF("
						+ "	IF(ISNULL(end_date),LAST_DAY('" + sqlStart + "'),IF(end_date>'" + sqlEnd + "','" + sqlEnd + "',end_date)),"
						+ " IF(start_date<'" + sqlStart + "','" + sqlStart + "',start_date)"
						+ " ) + 1)"
					+ " FROM contract_leave"
					+ " WHERE contract = " + contract.getId()
					+ " AND start_date <= '" + sqlEnd + "'"
					+ " AND (end_date>='" + sqlStart + "' OR end_date IS NULL)";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			int days = 0;
			rs.last();
			if(rs.getRow()>0){
				rs.beforeFirst();
				if(rs.next()){
					days = rs.getInt(1);
				}
			}
			
			return days;
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
	
	private void createEDLRecords(Contract contract, List<ITransferObject> salaryDataList, DAT dat, List<DAT> datList) {
		Salary salary = null;
		Double baseCgc = null;
		Double baseCgp = null;
	
		if(liquidationType==LiquidationType.L00){
		
			salary = getSalary(contract, SalaryType.SALARY);
			baseCgc = salary.getCommonBase();
			baseCgp = getProfessionalBase(salary, salaryDataList);
			
			if(salary!=null){
					
				if(dat.getIndicadoresPerfil()==null 
						|| (!dat.getIndicadoresPerfil().contains("I")
								&& !dat.getIndicadoresPerfil().contains("D") 
								&& !dat.getIndicadoresPerfil().contains("P") 
								&& !dat.getIndicadoresPerfil().contains("T")) ){
					
					Double decreaseBase = 0.0;
					if(isContractLeave(contract) || isContractLeaveMaternity(contract)){
						decreaseBase = getITBase(salary);
					} else if(isErePartial(salary, salaryDataList) || isEreTotal(salary, salaryDataList)){
						decreaseBase = getEreBase(salary,salaryDataList);
					}
					fanFactory.createEDLBa01Segment(baseCgc-decreaseBase, dat);
					fanFactory.createEDLBa02Segment(baseCgp-decreaseBase, dat);
					
					
					fanFactory.createEDLBa05Segment();
					fanFactory.createEDLBa06Segment();
					fanFactory.createEDLBa07Segment();
					fanFactory.createEDLBa08Segment();
					fanFactory.createEDLBa09Segment();
					fanFactory.createEDLBa10Segment(salary.getOvertimeBase(), dat);
					fanFactory.createEDLBa11Segment(salary.getNonEstructuralOvertimeBase(), dat);
					fanFactory.createEDLBa23Segment();
					fanFactory.createEDLBa28Segment();
					fanFactory.createEDLBa30Segment();
					fanFactory.createEDLBa31Segment();
					fanFactory.createEDLBa32Segment();
					fanFactory.createEDLBa33Segment();
					fanFactory.createEDLBa34Segment();
					fanFactory.createEDLBa35Segment();
					fanFactory.createEDLBa36Segment();
					fanFactory.createEDLBa37Segment();
					fanFactory.createEDLBa38Segment();
					fanFactory.createEDLBa41Segment();
					fanFactory.createEDLBa42Segment();
					
				} else {
					if (isContractLeave(contract)) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(getStartDate());
						Double itBase = getITBase(salary);
						fanFactory.createEDLBa01Segment(datList.size()>1?itBase:baseCgc, dat);
						fanFactory.createEDLBa02Segment(datList.size()>1?itBase:baseCgp, dat);
						fanFactory.createEDLCd01Segment(getECSSAmount(salary), dat);
						fanFactory.createEDLCd03Segment(getATEPAmount(salary), dat);
					} else if(isContractLeaveMaternity(contract)){
						Calendar cal = Calendar.getInstance();
						cal.setTime(getStartDate());
						Double itBase = getITBase(salary);
						fanFactory.createEDLBa21Segment(datList.size()>1?itBase:baseCgc, dat);
						fanFactory.createEDLBa22Segment(datList.size()>1?itBase:baseCgp, dat);
					} else if(isErePartial(salary, salaryDataList) || isEreTotal(salary, salaryDataList)){
						Double ereBase = getEreBase(salary, salaryDataList);
						fanFactory.createEDLBa21Segment(datList.size()>1?ereBase:baseCgc, dat);
						fanFactory.createEDLBa22Segment(datList.size()>1?ereBase:baseCgp, dat);
					}  
				}

				createBonusSegment(salary, dat, datList, salaryDataList);
				
			}
//			createEDLCd05Segment(salary, dat);
//			createEDLCd16Segment(salary, dat);
//			createEDLCd18Segment(salary, dat);
//			createEDLCd21Segment(salary, dat);
//			createEDLCd24Segment(salary, dat);
//			createEDLCd26Segment(salary, dat);
//			createEDLCd27Segment(salary, dat);
		
		} else if(liquidationType==LiquidationType.L13){
			salary = getSalary(contract, SalaryType.SETTLE);
			if(salary!=null){
				fanFactory.createEDLBa01Segment(baseCgc, dat);
				fanFactory.createEDLBa02Segment(baseCgp, dat);
			}
		}
		
	}
	
	
	private void createBonusSegment(Salary salary, DAT dat, List<DAT> datList, List<ITransferObject> salaryDataList){
		List<ITransferObject> bonusList = getSalaryBonuses(salary);
		if (bonusList!=null && !bonusList.isEmpty()) {
			SalaryBonus bonus = null;
			BonusType bonusType = null;
			for(ITransferObject to: bonusList){
				bonus = (SalaryBonus) to;
				bonusType = obtainBonusType(bonus);
				if(bonus!=null && bonusType != null){
					if(bonusType==BonusType.SOCIAL_SECURITY){
						fanFactory.createEDLCd07Segment(bonus.getAmount(), dat);
					} else if(bonusType==BonusType.EMPLOYMENT_PROMOTION){
						fanFactory.createEDLCd22Segment(getBonusDays(bonus), bonus.getAmount(), dat);
					} else if(bonusType==BonusType.CEUTA_MELILLA){
						fanFactory.createEDLCd20Segment(bonus.getAmount(), dat);
					} else if(bonusType==BonusType.HANDICAP){
						fanFactory.createEDLCd13Segment(bonus.getAmount(), dat);
						if(datList.size()>1){
							populateBonusAmount(datList, "CD13");
						}
					} else if(bonusType==BonusType.LAW_19_94){
						fanFactory.createEDLCd12Segment(bonus.getAmount(), dat);
					} else if(bonusType==BonusType.DISTANCE_FORMATION){
						Integer days = obtainFormationDays(bonus, BonusType.DISTANCE_FORMATION);
						fanFactory.createEDLCd11Segment(days, bonus.getAmount(), dat);
					} else if(bonusType==BonusType.CLASSROOM_FORMATION){
						Integer days = obtainFormationDays(bonus, BonusType.CLASSROOM_FORMATION);
						fanFactory.createEDLCd10Segment(days, bonus.getAmount(), dat);
					} else if(bonusType==BonusType.ERE){
						fanFactory.createEDLCd28Segment(bonus.getAmount(), dat);
					} else if(bonusType==BonusType.ENCOURAGED_INDUSTRIAL_SECTOR){
						fanFactory.createEDLCd23Segment(bonus.getAmount(), dat);
					} else if(bonusType==BonusType.GT_60){
					} else if(bonusType==BonusType.EXEMPTION_GT30_CHILD){
						fanFactory.createEDLCd25Segment(bonus.getAmount(), dat);
					} else if(bonusType==BonusType.REDUCTION_RIGHT_CONTRACT){
						fanFactory.createEDLCd06Segment(bonus.getAmount(), dat);
					} else if(bonusType==BonusType.REDUCTION_COMMON_CONTINGENCY_EXCEPT_IT){
						fanFactory.createEDLCd17Segment(bonus.getAmount(), dat);
					} else if(bonusType==BonusType.CONTINUOUS_FORMATION){
						// no se tiene en cuenta, se suma en los totalizadores
					} else if(bonusType==BonusType.REDUCTION_FLAT_RATE_RDL03_2014){
						fanFactory.createEDLCd31Segment(bonus.getAmount(), dat);
						if(datList.size()>1){
							populateBonusAmount(datList, "CD31");
						}
					} else if(bonusType==BonusType.REDUCTION_RATE_RDL01_2015){
						fanFactory.createEDLCd34Segment(getBonusDays(bonus), bonus.getAmount(), dat);
					}
				} else if(bonus!=null && bonusType==null) {
					fanFactory.createEDLCd07Segment(bonus.getAmount(), dat);
				}
			}
		}
		if(salary.getContract().getEnterpriseCCC().getType()==CCCType.AGRICULTURAL){
			Double cgcTotalEnterprise = 0.0;
			Double cgcTotalEmployee = 0.0;
			try {
//				cgcTotalEnterprise = obtainCGCTotalEnterprise(salary);
				cgcTotalEnterprise = obtainCGCEnterpriseCuota(salary);
				cgcTotalEmployee = obtainCGCTotalEmployee(salary);
			} catch (AonConnectionException e) {
				// do nothing
			} catch (SQLException e) {
				// do nothing
			} 
			fanFactory.createEDLCd29Segment(cgcTotalEnterprise, cgcTotalEmployee, dat, salaryDataList);
			if(isContractLeave(salary.getContract()) || isContractLeaveMaternity(salary.getContract())){
				fanFactory.createEDLCd30Segment(dat);
			}
		}
	}
	
	private int getBonusDays(SalaryBonus bonus){
		Date bonusStart = bonus.getSalary().getStartDate();
		Date bonusEnd = bonus.getSalary().getEndDate();
		int bonusDays = 30;
		if(bonusStart.after(getStartDate()) || (bonusEnd!=null && bonusEnd.before(getEndDate())) ){
			bonusStart = bonusStart.before(getStartDate())?getStartDate():bonusStart;
			bonusEnd = (bonusEnd!=null && bonusEnd.after(getEndDate()))?getEndDate():bonusEnd;
			bonusDays = differenceBetweenDates(bonusStart, bonusEnd);
		}
		return bonusDays;
	}
	
	private void populateBonusAmount(List<DAT> datList, String key) {
		Integer totalDiasHoras = 0;
		for(DAT dat: datList){
			if(dat.getEdl().containsKey(key)){
				totalDiasHoras += dat.getDiasHoras();
			}
		}
		for(DAT dat: datList){
			Integer diasHoras = dat.getDiasHoras();
			if(dat.getEdl().containsKey(key)){
				EDL edl = dat.getEdl().get(key);
				edl.setImporte((edl.getImporte()*diasHoras)/totalDiasHoras);
			}
		}
	}
	
	private Double getProfessionalBase(Salary salary, List<ITransferObject> salaryDataList) {
		Double profBase = 0.0;
		String _profBase = null;
		for(ITransferObject to: salaryDataList){
			SalaryData sd = (SalaryData) to;
			if(sd.getName().equals(ContextVariable.CGP_BASE.getName())){
				_profBase = sd.getExpression();
			}
		}
		if(_profBase!=null && NumberUtils.isNumber(_profBase)){
			profBase = Double.parseDouble(_profBase);
		} else {
			profBase = salary.getProfessionalBase();
		}
		return profBase;
	}
	
	private Double getEreBase(Salary salary, List<ITransferObject> salaryDataList) {
		Double ereBase = 0.0;
		String _ereBase = null;
		for(ITransferObject to: salaryDataList){
			SalaryData sd = (SalaryData) to;
			if(sd.getName().equals(ContextVariable.ERE_BASE.getName())){
				_ereBase = sd.getExpression();
			}
		}
		if(_ereBase!=null && NumberUtils.isNumber(_ereBase)){
			ereBase = Double.parseDouble(_ereBase);
		}
		return ereBase;
	}
	
	private Double getITBase(Salary salary) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT sum(quote) FROM salary_payment";
			select += " WHERE salary = " + salary.getId();
			select += " AND payment_concept IN ('" + ContextVariable.PREST_IT.getName() + "', '" + ContextVariable.MATERNITY.getName() + "')";
			select += " GROUP BY payment_concept;";
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) return rs.getDouble(1);
		} catch (AonConnectionException e) {
			// do nothing
		} catch (SQLException e) {
			// do nothing
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return 0.0;
	}
	
	private Double getATEPAmount(Salary salary) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT sum(amount) FROM salary_cost";
			select += " WHERE salary = " + salary.getId();
			select += " AND cost_concept = 'ATEP_E'";
			select += " GROUP BY cost_concept;";
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) return rs.getDouble(1);
		} catch (AonConnectionException e) {
			// do nothing
			System.out.println("ATEP amount - AonConnectionException");
		} catch (SQLException e) {
			// do nothing
			System.out.println("ATEP amount - SQLException");
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return 0.0;
	}

	private Double getECSSAmount(Salary salary) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT sum(amount) FROM salary_cost";
			select += " WHERE salary = " + salary.getId();
			select += " AND cost_concept = 'ECSS_E'";
			select += " GROUP BY cost_concept;";
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) return rs.getDouble(1);
		} catch (AonConnectionException e) {
			// do nothing
			System.out.println("ECSS amount - AonConnectionException");
		} catch (SQLException e) {
			// do nothing
			System.out.println("ECSS amount - SQLException");
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return 0.0;
	}
	
	
	private List<ITransferObject> getSalaryBonuses(Salary salary) {
		if(salary!=null && salary.getId()!=null){
			try {
				IManagerBean bean = BeanManager.getManagerBean(SalaryBonus.class);
				Criteria criteria = new Criteria();
				criteria.setSkipDomainFilter(true);
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_BONUS_SALARY_ID), salary.getId());
				return bean.getList(criteria);
			} catch (ManagerBeanException e) {
				// NADA
			}
		}
		return null;
	}

	private boolean existSalaryBonuses(Salary salary) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(SalaryBonus.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_BONUS_SALARY_ID), salary.getId());
			return bean.getCount(criteria)>0;
		} catch (ManagerBeanException e) {
			// NADA
		}
		return false;
	}
	
	private BonusType obtainBonusType(SalaryBonus bonus) {
		BonusType type = null;
		try {
			if(bonus.getBonusConcept()!=null && NumberUtils.isNumber(bonus.getBonusConcept())){
				IManagerBean bean = BeanManager.getManagerBean(BonusConcept.class);
				BonusConcept bc = (BonusConcept) bean.get(Integer.parseInt(bonus.getBonusConcept()));
				type = bc.getType();
				if ( type == null )
					type = PayrollUtils.getInstance().getBonusTypeByCode(bonus.getBonusConcept());
				return type;
			}
		} catch (ManagerBeanException e) {
			// continue
		}
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
						type = ((ContractBonus) list.get(0)).getBonusConcept().getType();
						if ( type == null )
							type = PayrollUtils.getInstance().getBonusTypeByCode(Integer.toString(cb.getBonusConcept().getId()));
					}
				} else {
					for(ITransferObject to: list){
						ContractBonus cb = (ContractBonus) to;
						if(StringUtils.isNotBlank(bonus.getDescription()) && bonus.getDescription().equals(cb.getBonusConcept().getDescription())){
							type = cb.getBonusConcept().getType();
							if ( type == null )
								type = PayrollUtils.getInstance().getBonusTypeByCode(Integer.toString(cb.getBonusConcept().getId()));
							break;
						}
					}
				}
				
			}
			if ( type == null )
				type = PayrollUtils.getInstance().getBonusTypeByCode(bonus.getBonusConcept());
			
		} catch (ManagerBeanException e) {
			// NADA
		}
		return type;
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
		if(liquidationType!=LiquidationType.L13){
			Salary salary = getSalary(contract, SalaryType.SALARY);
			if(existSalaryBonuses(salary)){
				String code = null;
				for(ITransferObject to: getSalaryBonuses(salary)){
					SalaryBonus sb = (SalaryBonus) to;
					T54 value = T54.getEnumByValue(sb.getBonusConcept());
					if(value!=null){
						code = value.getCode();
					}
				}
				if(StringUtils.isBlank(code)){
					code = SEPEUtils.getInstance().getContractDataMap(contract, false, true).get(ContextVariable.QUOTE_PECULIARITY_COLLECTIVE.getName());
				}
				return code!=null && !code.isEmpty()?code.replaceAll("\"", ""):null;
			}
		}
		return null;
	}
	private Integer getEmploymentRelation(Contract contract) {
		// TODO getEmploymentRelation
//		0100 Personal de Alta Dirección
//		0409 Deportistas profesionales
//		0900 Abogados en despachos de abogados
//		9909 Personal becario de investigación
		return null;
	}
	/**
	 * INDICADOR DISCAPACIDAD
		D Minusvalía igual o superior al 33%
		S Pensionista incapacidad permanente de la S.S.
		P Pensionista incapacidad permanente clases pasivas
	 * 
	 * @param contract
	 * @return
	 */
	private String getHandicapIndicator(Contract contract) {
		String o = SEPEUtils.getInstance().getContractInfoMap(contract, false, true).get(ContractVariable.DISABILITY_INDICATOR.getValue());
		return o!=null && !o.isEmpty()?o:null;
	}
	/**
	 * MODALIDAD DE COTIZACION
		J Cotización Jornadas Reales
		G Cotización Sistema General
		obligatorio para reg. 0613 
	 */
	private String getQuoteMode(List<ITransferObject> list) {
		return fanFactory.getQuoteMode(list);
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
	/**
	 * INDICADOR REDUCCION BONIFICACIO CONTRATOS TP
			1	Reducción mínima *
			2	Reducción media
			3	Reducción máxima
			4	Importe total sin reducción
			5	Tarifa plana minima (RDL 3/2014).
			6	Tarifa plana media (RDL 3/2014)
	 * @param contract
	 * @return
	 */
	private Integer getBonificationReduction(Contract contract) {
		String o = SEPEUtils.getInstance().getContractInfoMap(contract, false, true).get(ContractVariable.BONUS_REDUCTION_INDICATOR.getValue());
		return o!=null && !o.isEmpty()?Integer.parseInt(o):null;
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
	private String getQuoteIndicator(Contract contract, List<ITransferObject> salaryDataList) {
		return fanFactory.getQuoteIndicator(salaryDataList, SEPEUtils.getInstance().getContractDataMap(contract, false, true));
	}
	
	/**
	 * 
		Nº días alta trabaj. extr. REA y trabaj. a T. Parcial bonificados R.D.L. 5/2006
		Este  campo  es  obligatorio  con  formato  numérico  para contratos  a  tiempo  parcial  bonificados  con  el 
		programa  de  fomento  de  empleo  (R.D.L.  5/2006  de  9  de  junio).  
		
		Obligatorio  para  contratos  a  tiempo parcial bonificados por el R.D.L. 3/2014, R.D.L 8/2014, R.D.L. 1/2015. 
		Podrá tomar valor entre 1 y 31. 
		Se  cumplimentará  en  un  solo  segmento  DAT  de  los  varios  que  pueda  tener  un  mismo  trabajador  y 
		periodo (diferentes situaciones contractuales en el mismo mes)
	* 
		Obligatorio para contratos a tiempo parcial bonificados con el programa de fomento de empleo (R.D.L. 5/2006 de 9 de junio).
		Obligatorio para TRL 930 (socios cooperativas) con contrato a tiempo parcial. 
		Obligatorio para contratos a tiempo parcial bonificados por el R.D.L. 3/2014, 
			R.D.L 8/2014 
			y R.D.L 1/2015
		
	 * @param c
	 * @return
	 */
	private Integer getContractDischargeDays(Contract contract) {
		ContractCode code = getContractCode(contract);
		boolean match = false;
		if(code!=null && (code.getValue().startsWith("2") || code.getValue().startsWith("3") || code.getValue().startsWith("5"))){
			List<ITransferObject> bonusList = getSalaryBonuses(getSalary(contract, SalaryType.SALARY));
			if (bonusList!=null && !bonusList.isEmpty()) {
				for(ITransferObject to: bonusList){
					SalaryBonus bonus = (SalaryBonus) to;
					BonusType bonusType = obtainBonusType(bonus);
					if(bonusType==BonusType.REDUCTION_FLAT_RATE_RDL03_2014
							|| bonusType==BonusType.REDUCTION_RATE_RDL01_2015){
						match = true;
					}
				}
			}
		}
		
		String cooperativePartner = SEPEUtils.getInstance().getContractInfoMap(contract, false, true).get(ContractVariable.COOPERATIVE_PARTNER.getValue());
		if(new Boolean(cooperativePartner)){
			match = true;
		}
		
		if(match){
			Date contractStart = contract.getStartDate();
			Date contractEnd = contract.getEndDate();
			Date start = getStartDate().before(contractStart)?contractStart:getStartDate();
			Date end = (contractEnd!=null && getEndDate().after(contractEnd))?contractEnd:getEndDate();
			try {
				return (int)CommonUtil.getDaysBetweenDates(start, end, true);
			} catch (IllegalArgumentException e) {
				// nada  
			}
		}
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
	private Integer getContractDaysOrHours(Contract contract, List<ITransferObject> salaryDataList) {
		Salary salary = getSalary(contract, SalaryType.SALARY);
		Integer itDays = getItDays(contract);
		return fanFactory.getContractDaysOrHours(salary, salaryDataList, SEPEUtils.getInstance().getContractDataMap(contract, false, true), itDays, getStartDate(), getEndDate());
	}
	
	
	private Integer getNotEnjoyedVacationDays(Contract contract) {
		String days = SEPEUtils.getInstance().getContractDataMap(contract, false, true).get(ContextVariable.NO_HOLIDAYS.getName());
		return (int) NumberUtils.toDouble(days);
	}
	
	private Double obtainDayHours(Contract contract) throws ManagerBeanException {
		Salary salary = getSalary(contract, SalaryType.SALARY);
		SalaryData workedHoursData = SEPEUtils.getInstance().getSalaryDataMap(salary, salary.getStartDate(), salary.getEndDate()).get(ContextVariable.WORKED_HOURS.getName());
		String workedHours = workedHoursData!=null && NumberUtils.isNumber(workedHoursData.getExpression())?workedHoursData.getExpression():null;
		Double dayHours = null;
		if(workedHours!=null && NumberUtils.isNumber(workedHours)){
			dayHours = Double.parseDouble(workedHours)/salary.getTimeUnits();
		} else {
			String weekHours = SEPEUtils.getInstance().getContractDataMap(contract, false, true).get(ContextVariable.WEEK_HOURS.getName());
			dayHours = (weekHours!=null && NumberUtils.isNumber(weekHours))?(Double.parseDouble(weekHours)/5):0;
		}
		return dayHours;
	}
	
	/**
	 * numero de horas destinadas a formacion para las bonificaciones por formacion
	 * 
	 * Para la formacion teorica presencial - 8e hora/trabajador
	 * Para la formacion teorica a distancia - 5e hora/trabajador
	 *  
	 * @param classroomFormation 
	 * @return
	 */
	private Integer obtainFormationDays(SalaryBonus bonus, BonusType bonusType) {
		Integer days = 0;
		if(bonusType==BonusType.CLASSROOM_FORMATION){
			days = (int) Math.ceil(bonus.getAmount()/8);
		} else if(bonusType==BonusType.DISTANCE_FORMATION){
			days = (int) Math.ceil(bonus.getAmount()/5);
		}
		return days;
	}
	
	/**
	 * C - Obligatorio para contratos de duración efectiva inferior a 7 días Ley 12/2001
	 * @param c
	 * @return
	 */
	private boolean isLessThan7DaysContract(Contract contract) {
		ContractCode code = getContractCode(contract);
		if(contract.getEndDate()!=null && differenceBetweenDates(contract.getStartDate(), contract.getEndDate())<7){
			if(code != ContractCode.C410 && code != ContractCode.C418 
					&& code != ContractCode.C510 && code != ContractCode.C518
					&& code != ContractCode.C421
					&& contract.getEnterpriseCCC().getType() != CCCType.AGRICULTURAL){
				return true;
			}
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
	private String getOthers(Salary salary, List<ITransferObject> list) {
		// TODO 
		
		if(isErePartial(salary, list)){
			return "P";
		}
		if(isEreTotal(salary, list)){
			return "T";
		}
		return " ";
	}
	
	private Integer getEreDays(Salary salary, List<ITransferObject> list) {
		String _workedDays = null;
		String _monthDays = null;
		String _ereBase = null;
		for(ITransferObject to: list){
			SalaryData sd = (SalaryData) to;
			if(sd.getName().equals(ContextVariable.WORKED_DAYS.getName())){
				_workedDays = sd.getExpression();
			}
			if(sd.getName().equals(ContextVariable.MONTH_DAYS.getName())){
				_monthDays = sd.getExpression();
			}
			if(sd.getName().equals(ContextVariable.ERE_BASE.getName())){
				_ereBase = sd.getExpression();
			}
		}
		if(_ereBase!=null && NumberUtils.isNumber(_ereBase)){
			if(CommonUtil.round(Double.parseDouble(_ereBase))==CommonUtil.round(salary.getCommonBase())){
				return 30;
			} else {
				Integer ereDays = Double.valueOf(_monthDays).intValue() - Double.valueOf(_workedDays).intValue();
				if(Double.parseDouble(_ereBase)>0.0 && ereDays>0){
					return ereDays;
				}
			}
		}
		
		return null;
	}
	
	private boolean isErePartial(Salary salary, List<ITransferObject> list) {
		Integer days = getEreDays(salary, list);
		return (days!=null && days>0 && days<30);
	}
	
	private boolean isEreTotal(Salary salary, List<ITransferObject> list) {
		Integer days = getEreDays(salary, list);
		return (days!=null && days>0 && days==30);
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

		if(isContractLeave(contract)){
			return "I";
		} else if(isContractLeaveMaternity(contract)){
			return "D";
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
	
	
	/**
	 * TC/2 Totales
	 * @param ccc
	 * @return
	 */
	private TCT createTCTRecord(EnterpriseCCC ccc) {
		
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
				
		try {
			fanFactory.createEDTBa01Segment(emp);
			fanFactory.createEDTBa02Segment(emp);
			fanFactory.createEDTBa05Segment(emp);
			fanFactory.createEDTBa06Segment(emp);
			fanFactory.createEDTBa07Segment(emp);
			fanFactory.createEDTBa08Segment(emp);
			fanFactory.createEDTBa09Segment(ccc, emp);
			fanFactory.createEDTBa10Segment(ccc, emp);
			fanFactory.createEDTBa11Segment(ccc, emp);
			fanFactory.createEDTBa21Segment(ccc, emp);
			fanFactory.createEDTBa22Segment(emp);
			fanFactory.createEDTBa23Segment(emp);
			fanFactory.createEDTBa28Segment(emp);
			fanFactory.createEDTBa30Segment(emp);
			fanFactory.createEDTBa31Segment(emp);
			fanFactory.createEDTBa32Segment(emp);
			fanFactory.createEDTBa33Segment(emp);
			fanFactory.createEDTBa34Segment(emp);
			fanFactory.createEDTBa35Segment(emp);
			fanFactory.createEDTBa36Segment(emp);
			fanFactory.createEDTBa37Segment(emp);
			fanFactory.createEDTBa38Segment(emp);
			fanFactory.createEDTBa41Segment(emp);
			fanFactory.createEDTBa42Segment(emp);
			
			fanFactory.createEDTCd01Segment(ccc, emp);
			fanFactory.createEDTCd03Segment(ccc, emp);
			fanFactory.createEDTCd05Segment(emp);
			fanFactory.createEDTCd06Segment(emp);
			fanFactory.createEDTCd07Segment(emp);
			fanFactory.createEDTCd10Segment(emp);
			fanFactory.createEDTCd11Segment(emp);
			fanFactory.createEDTCd12Segment(emp);
			fanFactory.createEDTCd13Segment(emp);
			fanFactory.createEDTCd16Segment(emp);
			fanFactory.createEDTCd17Segment(ccc, emp);
			fanFactory.createEDTCd18Segment(emp);
			fanFactory.createEDTCd20Segment(emp);
			fanFactory.createEDTCd21Segment(emp);
			fanFactory.createEDTCd22Segment(ccc, emp);
			fanFactory.createEDTCd23Segment(emp);
			fanFactory.createEDTCd24Segment(ccc, emp);
			fanFactory.createEDTCd25Segment(ccc, emp);
			fanFactory.createEDTCd26Segment(emp);
			fanFactory.createEDTCd27Segment(emp);
			fanFactory.createEDTCd28Segment(emp);
			fanFactory.createEDTCd29Segment(emp);
			fanFactory.createEDTCd30Segment(emp);
			fanFactory.createEDTCd31Segment(ccc, emp);
			fanFactory.createEDTCd34Segment(ccc, emp);
			
			
			Double CGCEnterpriseTotal = obtainCGCEnterpriseTotal(ccc);
			Double CGCOnlyEnterpriseTotal = obtainCGCOnlyEnterpriseTotal(ccc);
			Double CGCEmployeeTotal = obtainCGCEmployeeTotal(ccc);
			
			fanFactory.createEDTCa01Segment(
					CGCEnterpriseTotal - CGCOnlyEnterpriseTotal, 
					CGCEmployeeTotal, 
					emp);
			fanFactory.createEDTCa02Segment(CGCOnlyEnterpriseTotal, emp);
			fanFactory.createEDTCa03Segment(emp);
			fanFactory.createEDTCa11Segment(obtainLessThanSevenDaysContractAmount(ccc), emp);
			fanFactory.createEDTCa12Segment(emp);
			fanFactory.createEDTCa20Segment(emp);
			fanFactory.createEDTCa21Segment(emp);
			fanFactory.createEDTCa22Segment(emp);
			emp.getEdtSegment("EDTCA30");
			fanFactory.createEDTCa31Segment(obtainITTotal(ccc), emp);
			fanFactory.createEDTCa32Segment(obtainImsTotal(ccc), emp);
			fanFactory.createEDTCa30Segment(emp);
			
			
			Double otherEnterpriseTotalUnemployment = obtainOtherEnterpriseTotal(ccc, DeductionType.UNEMPLOYMENT);
			Double otherEnterpriseTotalFogasa = obtainOtherEnterpriseTotal(ccc, DeductionType.FOGASA);
			Double otherEnterpriseTotalJobTraining = obtainOtherEnterpriseTotal(ccc, DeductionType.JOB_TRAINING);
			Double otherEmployeeTotalUnemployment = obtainOtherEmployeeTotal(ccc, DeductionType.UNEMPLOYMENT);
			Double otherEmployeeTotalJobTraining = obtainOtherEmployeeTotal(ccc, DeductionType.JOB_TRAINING);

			Double otherOnlyEnterpriseTotalUnemployment = obtainOtherOnlyEnterpriseTotal(ccc, DeductionType.UNEMPLOYMENT);
			Double otherOnlyEnterpriseTotalFogasa = obtainOtherOnlyEnterpriseTotal(ccc, DeductionType.FOGASA);
			Double otherOnlyEnterpriseTotalJobTraining = obtainOtherOnlyEnterpriseTotal(ccc, DeductionType.JOB_TRAINING);
			Double otherOnlyEmployeeTotalUnemployment = obtainOtherOnlyEmployeeTotal(ccc, DeductionType.UNEMPLOYMENT);
			Double otherOnlyEmployeeTotalJobTraining = obtainOtherOnlyEmployeeTotal(ccc, DeductionType.JOB_TRAINING);
			
			fanFactory.createEDTCa50Segment(
					otherEnterpriseTotalUnemployment - otherOnlyEnterpriseTotalUnemployment,
					otherEnterpriseTotalFogasa - otherOnlyEnterpriseTotalFogasa,
					otherEnterpriseTotalJobTraining - otherOnlyEnterpriseTotalJobTraining,
					otherEmployeeTotalUnemployment - otherOnlyEmployeeTotalUnemployment,
					otherEmployeeTotalJobTraining - otherOnlyEmployeeTotalJobTraining,
					emp);
			fanFactory.createEDTCa51Segment(emp);
			fanFactory.createEDTCa52Segment(
					otherEnterpriseTotalUnemployment,
					otherEnterpriseTotalFogasa,
					otherEnterpriseTotalJobTraining,
					otherEmployeeTotalUnemployment,
					otherEmployeeTotalJobTraining, 
					emp);
			fanFactory.createEDTCa53Segment(emp);
			fanFactory.createEDTCa54Segment(emp);
			fanFactory.createEDTCa55Segment(emp);
			fanFactory.createEDTCa56Segment(emp);
			fanFactory.createEDTCa57Segment(
					otherOnlyEnterpriseTotalUnemployment,
					otherOnlyEnterpriseTotalFogasa,
					otherOnlyEnterpriseTotalJobTraining,
					emp);
			
			emp.getEdtSegment("EDTCA60");
			fanFactory.createEDTCa80Segment(obtainContinuousFormationTotal(ccc), emp);
			fanFactory.createEDTCa60Segment(emp);
			if(emp.getEdtSegment("EDTCA60").getImporte()==null){
				emp.getEdt().remove("EDTCA60");
			}
			fanFactory.createEDTCa90Segment(emp);
			
			emp.getEdt().remove("EDTTT10");
			emp.getEdt().remove("EDTTT20");
			emp.getEdt().remove("EDTTT30");
			emp.getEdt().remove("EDTTT91");
			emp.getEdt().remove("EDTTT92");
			emp.getEdtSegment("EDTTT10");
			emp.getEdtSegment("EDTTT20");
			emp.getEdtSegment("EDTTT30");
			fanFactory.createEDTTt10Segment(emp);
			fanFactory.createEDTTt20Segment(emp);
			fanFactory.createEDTTt30Segment(emp);
			fanFactory.createEDTTt9XSegment(emp);
		} catch (SQLException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
		} catch (AonConnectionException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
		}
	}
	
	
	private Double obtainContinuousFormationTotal(EnterpriseCCC ccc) throws AonConnectionException, SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			if(liquidationType!=LiquidationType.L13){
				conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
				// TODO 
				String select = "SELECT sum(expression) FROM contract_bonus";
				select += " WHERE contract in ( SELECT id FROM contract WHERE enterprise_ccc = " + ccc.getId() + " )";
				select += " AND start_date <= '" + dateFormatter.format(getStartDate()) + "'"; 
				select += " AND (end_date IS NULL"; 
				select += " OR (end_date >= '" + dateFormatter.format(getStartDate()) + "'"; 
				select += " AND end_date <= '" + dateFormatter.format(getEndDate())+"'))";
				select += " AND bonus_concept in (";
				select += " SELECT id FROM bonus_concept WHERE type = " + BonusType.CONTINUOUS_FORMATION.ordinal();
				select += " );";
				ps = conn.prepareStatement(select);
				ResultSet rs = ps.executeQuery();
				if(rs.next()) return rs.getDouble(1);
			}
			return 0.0;
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}
	
	private Double obtainOtherEnterpriseTotal(EnterpriseCCC ccc) throws AonConnectionException, SQLException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		SalaryType salaryType = null;
		if(liquidationType==LiquidationType.L00){
			salaryType = SalaryType.SALARY;
		} else if(liquidationType==LiquidationType.L13){
			salaryType = SalaryType.SETTLE;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT sum(amount) FROM salary_cost";
			select += " WHERE type in (" + DeductionType.UNEMPLOYMENT.ordinal() + ", " + DeductionType.JOB_TRAINING.ordinal() + ", " + DeductionType.FOGASA.ordinal() + ")";
			select += " AND salary in (";
			select += " SELECT id FROM salary WHERE domain = " + ccc.getDomain()
					+ " AND ccc = '" + ccc.getCcc() + "'"
					+ " AND type = " + salaryType.ordinal() + ""
					+ " AND end_date >= '" + dateFormatter.format(getStartDate()) + "'" 
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
		SalaryType salaryType = null;
		if(liquidationType==LiquidationType.L00){
			salaryType = SalaryType.SALARY;
		} else if(liquidationType==LiquidationType.L13){
			salaryType = SalaryType.SETTLE;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT sum(amount) FROM salary_deduction";
			select += " WHERE type in (" + DeductionType.UNEMPLOYMENT.ordinal() + ", " + DeductionType.JOB_TRAINING.ordinal() + ")";
			select += " AND salary in (";
			select += " SELECT id FROM salary WHERE domain = " + ccc.getDomain() 
					+ " AND ccc = '" + ccc.getCcc() + "'"
					+ " AND type = " + salaryType.ordinal() + ""
					+ " AND end_date >= '" + dateFormatter.format(getStartDate()) + "'" 
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
	private Double obtainOtherEnterpriseTotal(EnterpriseCCC ccc, DeductionType type) throws AonConnectionException, SQLException {
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		SalaryType salaryType = null;
		if(liquidationType==LiquidationType.L00){
			salaryType = SalaryType.SALARY;
		} else if(liquidationType==LiquidationType.L13){
			salaryType = SalaryType.SETTLE;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT sum(amount) FROM salary_cost";
			select += " WHERE type in (" + type.ordinal() + ")";
			select += " AND salary in (";
			select += " SELECT id FROM salary WHERE domain = " + ccc.getDomain()
					+ " AND ccc = '" + ccc.getCcc() + "'"
					+ " AND type = " + salaryType.ordinal() + ""
					+ " AND end_date >= '" + dateFormatter.format(getStartDate()) + "'" 
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
	
	private Double obtainOtherEmployeeTotal(EnterpriseCCC ccc, DeductionType type)  throws AonConnectionException, SQLException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		SalaryType salaryType = null;
		if(liquidationType==LiquidationType.L00){
			salaryType = SalaryType.SALARY;
		} else if(liquidationType==LiquidationType.L13){
			salaryType = SalaryType.SETTLE;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT sum(amount) FROM salary_deduction";
			select += " WHERE type in (" + type.ordinal() + ")";
			select += " AND salary in (";
			select += " SELECT id FROM salary WHERE domain = " + ccc.getDomain() 
					+ " AND ccc = '" + ccc.getCcc() + "'"
					+ " AND type = " + salaryType.ordinal() + ""
					+ " AND end_date >= '" + dateFormatter.format(getStartDate()) + "'" 
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

	private Double obtainOtherOnlyEnterpriseTotal(EnterpriseCCC ccc, DeductionType type) throws AonConnectionException, SQLException {
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		SalaryType salaryType = null;
		if(liquidationType==LiquidationType.L00){
			salaryType = SalaryType.SALARY;
		} else if(liquidationType==LiquidationType.L13){
			salaryType = SalaryType.SETTLE;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			
			String maternitySelect = "SELECT salary FROM salary_payment";
			maternitySelect += " WHERE payment_concept IN ('" + ContextVariable.MATERNITY.getName() + "')";
			maternitySelect += " AND salary in (";
			maternitySelect += " SELECT id FROM salary WHERE domain = " + ccc.getDomain()
					+ " AND ccc = '" + ccc.getCcc() + "'"
					+ " AND type = " + salaryType.ordinal()
					+ " AND end_date >= '" + dateFormatter.format(getStartDate()) + "'" 
					+ " AND end_date <= '" + dateFormatter.format(getEndDate())+"'";
			maternitySelect += " )";
			
			String select = "SELECT sum(amount) FROM salary_cost"
					+ " WHERE type in (" + type.ordinal() + ")"
					+ " AND salary in ( " + maternitySelect + " )";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return rs.getDouble(1);
			}
			return 0.0;
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}

	private Double obtainOtherOnlyEmployeeTotal(EnterpriseCCC ccc, DeductionType type) throws AonConnectionException, SQLException {
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		SalaryType salaryType = null;
		if(liquidationType==LiquidationType.L00){
			salaryType = SalaryType.SALARY;
		} else if(liquidationType==LiquidationType.L13){
			salaryType = SalaryType.SETTLE;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			
			String maternitySelect = "SELECT salary FROM salary_payment";
			maternitySelect += " WHERE payment_concept IN ('" + ContextVariable.MATERNITY.getName() + "')";
			maternitySelect += " AND salary in (";
			maternitySelect += " SELECT id FROM salary WHERE domain = " + ccc.getDomain()
					+ " AND ccc = '" + ccc.getCcc() + "'"
					+ " AND type = " + salaryType.ordinal()
					+ " AND end_date >= '" + dateFormatter.format(getStartDate()) + "'" 
					+ " AND end_date <= '" + dateFormatter.format(getEndDate())+"'";
			maternitySelect += " )";
			
			String select = "SELECT sum(amount) FROM salary_deduction"
					+ " WHERE type in (" + type.ordinal() + ")"
					+ " AND salary in ( " + maternitySelect + " );";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return rs.getDouble(1);
			}
			return 0.0;
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}
	
	private Double obtainImsTotal(EnterpriseCCC ccc) throws AonConnectionException, SQLException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		SalaryType salaryType = null;
		if(liquidationType==LiquidationType.L00){
			salaryType = SalaryType.SALARY;
		} else if(liquidationType==LiquidationType.L13){
			salaryType = SalaryType.SETTLE;
		}
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
					+ " AND type = " + salaryType.ordinal() + ""
					+ " AND end_date >= '" + dateFormatter.format(getStartDate()) + "'" 
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
	private Double obtainITTotal(EnterpriseCCC ccc) throws AonConnectionException, SQLException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		SalaryType salaryType = null;
		if(liquidationType==LiquidationType.L00){
			salaryType = SalaryType.SALARY;
		} else if(liquidationType==LiquidationType.L13){
			salaryType = SalaryType.SETTLE;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT sum(amount) FROM salary_cost";
			select += " WHERE cost_concept in ('" + ContextVariable.IT_ENTERPRISE.getName() + "')";
			select += " AND salary in (";
			select += " SELECT id FROM salary WHERE domain = " + ccc.getDomain() 
					+ " AND ccc = '" + ccc.getCcc() + "'" 
					+ " AND type = " + salaryType.ordinal() + "" 
					+ " AND end_date >= '" + dateFormatter.format(getStartDate()) + "'" 
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
	
	private Double obtainLessThanSevenDaysContractAmount(EnterpriseCCC ccc) throws AonConnectionException, SQLException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT sum(amount) FROM salary_cost";
			select += " WHERE type in (" + DeductionType.COMMON_CONTINGENCY.ordinal() + ")";
			select += " AND cost_concept in ('CGC_E_TEMP')";
			select += " AND salary in (";
			select += " SELECT id FROM salary WHERE domain = " + ccc.getDomain() 
					+ " AND ccc = '" + ccc.getCcc() + "'"
					+ " AND end_date >= '" + dateFormatter.format(getStartDate()) + "'" 
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
	
	
	private Double obtainCGCEnterpriseTotal(EnterpriseCCC ccc) throws AonConnectionException, SQLException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		SalaryType salaryType = null;
		if(liquidationType==LiquidationType.L00){
			salaryType = SalaryType.SALARY;
		} else if(liquidationType==LiquidationType.L13){
			salaryType = SalaryType.SETTLE;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT sum(amount) FROM salary_cost";
			select += " WHERE type in (" + DeductionType.COMMON_CONTINGENCY.ordinal() + ")";
			select += " AND cost_concept in ('CGC_E')";
			select += " AND salary in (";
			select += " SELECT id FROM salary WHERE domain = " + ccc.getDomain() 
					+ " AND ccc = '" + ccc.getCcc() + "'"
					+ " AND type = " + salaryType.ordinal() + ""
					+ " AND end_date >= '" + dateFormatter.format(getStartDate()) + "'" 
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
	private Double obtainCGCOnlyEnterpriseTotal(EnterpriseCCC ccc) throws AonConnectionException, SQLException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		SalaryType salaryType = null;
		if(liquidationType==LiquidationType.L00){
			salaryType = SalaryType.SALARY;
		} else if(liquidationType==LiquidationType.L13){
			salaryType = SalaryType.SETTLE;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			
			String maternitySelect = "SELECT salary FROM salary_payment";
			maternitySelect += " WHERE payment_concept IN ('" + ContextVariable.MATERNITY.getName() + "')";
			maternitySelect += " AND salary in (";
			maternitySelect += " SELECT id FROM salary WHERE domain = " + ccc.getDomain()
					+ " AND ccc = '" + ccc.getCcc() + "'"
					+ " AND type = " + salaryType.ordinal()
					+ " AND end_date >= '" + dateFormatter.format(getStartDate()) + "'" 
					+ " AND end_date <= '" + dateFormatter.format(getEndDate())+"'";
			maternitySelect += " )";
			
			String select = "SELECT sum(amount) FROM salary_cost";
			select += " WHERE type in (" + DeductionType.COMMON_CONTINGENCY.ordinal() + ")";
			select += " AND cost_concept in ('CGC_E')";
			select += " AND salary in ( " + maternitySelect + " );";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) return rs.getDouble(1);
			return 0.0;
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}
	private Double obtainCGCEmployeeTotal(EnterpriseCCC ccc) throws AonConnectionException, SQLException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		SalaryType salaryType = null;
		if(liquidationType==LiquidationType.L00){
			salaryType = SalaryType.SALARY;
		} else if(liquidationType==LiquidationType.L13){
			salaryType = SalaryType.SETTLE;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT sum(amount) FROM salary_deduction";
			select += " WHERE type in (" + DeductionType.COMMON_CONTINGENCY.ordinal() + ")";
			select += " AND salary in (";
			select += " SELECT id FROM salary WHERE domain = " + ccc.getDomain() 
					+ " AND ccc = '" + ccc.getCcc() + "'"
					+ " AND type = " + salaryType.ordinal() + ""
					+ " AND end_date >= '" + dateFormatter.format(getStartDate()) + "'" 
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
	private Double obtainCGCEnterpriseCuota(Salary salary) throws AonConnectionException, SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT expression FROM salary_data";
			select += " WHERE name = '_CUOTA'";
			select += " AND salary = " + salary.getId() + " ;";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) return rs.getDouble(1);
			return 0.0;
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}
	
	private Double obtainCGCTotalEmployee(Salary salary) throws AonConnectionException, SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT sum(amount) FROM salary_deduction";
			select += " WHERE type in (" + DeductionType.COMMON_CONTINGENCY.ordinal() + ")";
			select += " AND salary = " + salary.getId() + " ;";
			
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
			select += " AND name = '" + AppParam.PAY_fan_test_env_PAY.getValue() + "';";
			
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

	private String getAuthorizationKey() {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT value FROM app_param";
			select += " WHERE domain = " + DomainManager.getCurrentDomain();
			select += " AND name = '" + AppParam.PAY_authorization_key_PAY.getValue() + "';";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next() && StringUtils.isNotBlank(rs.getString(1))){
				return rs.getString(1);
			} else {
				select = "SELECT value FROM app_param";
				select += " WHERE domain = " + DomainManager.getDomainProvider().getParentDomain();
				select += " AND name = '" + AppParam.PAY_authorization_key_PAY.getValue() + "';";
				
				ps = conn.prepareStatement(select);
				rs = ps.executeQuery();
				if(rs.next() && StringUtils.isNotBlank(rs.getString(1))){
					return rs.getString(1);
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
	
	private RegistryBank obtainBank(EnterpriseCCC ccc) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT value FROM app_param";
			select += " WHERE domain = " + ccc.getDomain();
			select += " AND name = '" + AppParam.PAY_ss_payment_bankAccount_PAY.getValue() + "';";
			
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
			select += " AND name = '" + AppParam.PAY_ss_mutual_PAY.getValue() + "';";
			
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
	
	
	private Salary getSalary(Contract c, SalaryType salaryType) {
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
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_TYPE), salaryType);
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.SALARY_END_DATE), startCal.getTime());
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
	
	private String obtainUtf(String value) {
		if(value!=null){
			value = value.replaceAll("[ÁÀ]", "A");
			value = value.replaceAll("[ÉÈ]", "E");
			value = value.replaceAll("[ÍÌ]", "I");
			value = value.replaceAll("[ÓÒ]", "O");
			value = value.replaceAll("[ÚÙ]", "U");
			value = value.replaceAll("[^-_;:¿?¡!@#$&\\(\\)\\s\\.,a-zA-Z0-9]", "?");
		}
		return value;
	}
	
	private void analizeData(List<EnterpriseCCC> cccList, LiquidationType liquidationType) throws ManagerBeanException {
		ArrayList<String> errors = new ArrayList<>();
		ArrayList<String> cccErrors = new ArrayList<>();
		
		if(liquidationType==LiquidationType.L00){
			for(EnterpriseCCC ccc: cccList){
				cccErrors = new ArrayList<>();
				List<ITransferObject> contractList = obtainContracts(ccc, getStartDate(), getEndDate());
				
//			******************************
//			contratos activos > 0
//			******************************
				if(contractList==null || contractList.size()==0){
					cccErrors.add("- No hay contratos activos");
				}
				for(ITransferObject _contract: contractList){
					Contract contract = (Contract) _contract;
					Salary salary = null;
					if(liquidationType==LiquidationType.L00){
						salary = getSalary(contract, SalaryType.SALARY);
					} else if(liquidationType==LiquidationType.L13){
						salary = getSalary(contract, SalaryType.SETTLE);
					}
					
//				******************************
//				nominas > 0
//				******************************
					if(salary==null){
						cccErrors.add("- " + contract.getPerson().getFullName() + " no tiene nómina");
					} else {
						
//					******************************
//					bonificacion & colectivo peculiaridad cotización
//					******************************
						List<ITransferObject> bonusList = getSalaryBonuses(salary);
						if(bonusList!=null && bonusList.size()>0 && getParticularGroup(contract)==null){
							SalaryBonus bonus = (SalaryBonus) bonusList.get(0);
							BonusType bonusType = obtainBonusType(bonus);
							if(bonusType!=BonusType.CONTINUOUS_FORMATION){
								cccErrors.add("- " + contract.getPerson().getFullName() + " no tiene definido el colectivo de peculiaridad de cotización");
							}
						}
						
//					******************************
//					R. AGRARIO. jornadas reales & cotizacion mensual
//					******************************
						if(ccc.getType()==CCCType.AGRICULTURAL){
							List<ITransferObject> salaryDataList = PayrollUtils.getInstance().getSalaryDataList(salary, true);
							String jornadas = null;
							List<ITransferObject> list = salaryDataList;
							for(ITransferObject to: list){
								if(((SalaryData) to).getName().equals("JORNADAS_REALES")){
									jornadas = ((SalaryData) to).getExpression();
								}
							}
							if(jornadas!=null && NumberUtils.isNumber(jornadas)){
								String o = null;
								for(ITransferObject to: salaryDataList){
									if(((SalaryData)to).getName().equals("COTIZACION_MENSUAL")){
										o = ((SalaryData)to).getExpression();
									}
								}
								if(o==null || new Boolean(o)){
									cccErrors.add("- " + contract.getPerson().getFullName() + " tiene jornadas reales definidas pero su cotización es mensual.");
								}
							}
						}
					}
					
				}
				if(cccErrors!=null && cccErrors.size()>0){
					errors.add("Errores de " + ccc.getActivity().getEnterprise().getRegistry().getFullName() + " (" + PayrollUtils.getInstance().getRegimeCode(ccc)+ccc.getCcc() + "): ");
					errors.addAll(cccErrors);
					errors.add(".");
				}
			}
		} else if(liquidationType==LiquidationType.L13){
			
			for(EnterpriseCCC ccc: cccList){
				cccErrors = new ArrayList<>();
				List<ITransferObject> contractList = obtainContracts(ccc, getStartDate(), getEndDate());
				
//			******************************
//			contratos activos > 0
//			******************************
				if(contractList==null || contractList.size()==0){
					cccErrors.add("- No hay contratos activos");
				}
				
				for(ITransferObject _contract: contractList){
					Contract contract= (Contract)_contract;
					Salary salary = getSalary(contract, SalaryType.SETTLE);
					if(salary!=null && salary.getId()!=null){
						Integer days = getNotEnjoyedVacationDays(contract);
						if(days==null || days<=0){
							cccErrors.add("- " + contract.getPerson().getFullName() + " no tiene dias de vacaciones retribuidos y no disfrutados. SE EXCLUYE.");
						}
					} 
				}
				if(cccErrors!=null && cccErrors.size()>0){
					errors.add("Errores de " + ccc.getActivity().getEnterprise().getRegistry().getFullName() + " (" + PayrollUtils.getInstance().getRegimeCode(ccc)+ccc.getCcc() + "): ");
					errors.addAll(cccErrors);
					errors.add(".");
				}
			}
			
		}
		
		for(String error: errors){
			AonUtil.addErrorMessage(error);
		}
		
	}
	
	
}

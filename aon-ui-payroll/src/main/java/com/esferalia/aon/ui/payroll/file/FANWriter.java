package com.esferalia.aon.ui.payroll.file;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.enumeration.DocumentType;
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
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractLeave;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBonus;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LiquidationType;
import com.esferalia.aon.payroll.enumeration.Mutual;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.SalaryType;

public class FANWriter {
	
//	private ETI eti;
//	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
//	
//	private final String  VIRGULILLA = "~";
//	
//	private final String  BLANK_1 = " ";
//	private final String  BLANK_2 = "  ";
//	private final String  BLANK_3 = "   ";
//	private final String  BLANK_7 = "       ";
//	private final String BLANK_15 = "               ";
//	private final String BLANK_20 = "                    ";
//	
//	private Integer year;
//	private Month startMonth; 
//	private Month endMonth; 
//	private LiquidationType liquidationType;
//	
////	private EMP currentEMP;
//	private int totalContractSum = 0;
////	private DAT currentDAT;
//	
//	private Date getStartDate(){
//		Calendar cal = Calendar.getInstance();
//		cal.set(year, startMonth.ordinal(), 1);
//		return cal.getTime(); 
//	}
//	private Date getEndDate(){
//		Calendar cal = Calendar.getInstance();
//		cal.set(year, endMonth.ordinal(), 1);
//		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
//		return cal.getTime();
//	}
//	
//	public ETI getEti() {
//		return eti;
//	}
//	public void setEti(ETI eti) {
//		this.eti = eti;
//	}
//
//	public FileOutput createFAN(List<EnterpriseCCC> list, LiquidationType liquidationType, Integer year, Month startMonth, Month endMonth ) throws ManagerBeanException {
//		this.year = year;
//		this.startMonth = startMonth; 
//		this.endMonth= endMonth ; 
//		this.liquidationType = liquidationType;
//		totalContractSum = 0;
//		
////		createETIRecord( list );
////		return null;
//		
//		try {
//			ETI eti = createETIRecord( list );
//			File file = File.createTempFile("XXXXXXXX", ".FAN");
//			FileFiller fan = new FAN(eti, file.getAbsolutePath());
//			FileOutput output = new FileOutput();
//			output.setFile(file);
//			output.setErrors(fan.create());
//			return output;
//		} catch (IOException e) {
//			throw new ManagerBeanException(e);
//		}
//	}
//	
//	private ETI createETIRecord( List<EnterpriseCCC> list ) throws ManagerBeanException {
//		ETI eti = new ETI();
//		// TODO Clave proporcionada por la seguridad social
//		Integer clave = 12345678;
//		eti.setClave(clave);
//		for (EnterpriseCCC ccc: list) {
//			EMP emp = createEMPrecord(ccc);
//			if(emp!=null){
//				eti.getEmpresas().add(emp);
//			}
//		}
//		setEti( eti );
//		return getEti();
//	}
//	
//	private EMP createEMPrecord(EnterpriseCCC ccc) throws  ManagerBeanException {
//		EMP emp = new EMP();
////		currentEMP = new EMP();
////		EMP emp = currentEMP;
//		// TODO se ha movido el ccc y activity de company a payroll
////		ent.initMainActiviy();
//		// regimen de la SS + provincia + numero ccc
//		emp.setCodigoCuentaCotizacionSeguridadSocial("0111"+ccc.getCcc());
//		
//		RegistryDirStaff dirStaff = getDirStaff(ccc);
//    	if ( dirStaff!=null ) {
////    		if(dirStaff.getRegistry().getDocumentType()==DocumentType.NIF){
////    			emp.setTipoDocumento("1");
////    		} else if(dirStaff.getRegistry().getDocumentType()==DocumentType.PASSPORT){
////    			emp.setTipoDocumento("2");
////    		} else if(dirStaff.getRegistry().getDocumentType()==DocumentType.NIE){
////    			emp.setTipoDocumento("6");
////    		} else if(dirStaff.getRegistry().getDocumentType()==DocumentType.CIF){
////    			emp.setTipoDocumento("9");
////    		} else {
////    			emp.setTipoDocumento("9");
////    		}
//    		emp.setTipoDocumento("1");
//    		String pais = null;
//    		try {
////    			pais = dirStaff.getRegistry().getDocumentCountry().getIso3();
//    			pais = Country.ES.getIso3();
//    		} catch (NullPointerException e) {
//    			pais = BLANK_3;
//    		}			
//    		if (StringUtils.isBlank(pais)) {
//    			pais = BLANK_3;
//    		}
//    		emp.setPais(pais);
//    		emp.setNumeroIdentificacion(autoComplete(dirStaff.getDocument(), 14, "0", true));
//    	}
//    	emp.setCalificador(BLANK_2);
//		emp.setCodigoCuentaCotizacionPrincipal("0111"+ccc.getCcc());
//		emp.setAnio(year);
//		emp.setDesdeMes(startMonth.ordinal()+1);
//		emp.setHastaMes(endMonth.ordinal()+1);
//		emp.setCalificadorLiquidacion(liquidationType.getValue());
//		//solicitud cuota total o cuota de la aportacion del trabajador
//		emp.setClaseLiquidacion(0);
//
//		emp.setRzs(createRZSrecord(ccc.getActivity().getEnterprise()));
//		
//		Calendar cal = Calendar.getInstance();
//		cal.set(year, endMonth.ordinal(), 1);
//		List<ITransferObject> list = getContracts(ccc, cal.getTime());
//		if(!list.isEmpty()){
////			return null;
//			for(ITransferObject to: list){
//				Contract c = (Contract) to;
//				if(getSalary(c)!=null && c.getRegimeType()!=SSRegimeType.SELF_EMPLOYED){
////				TRA tra = createTRARecord(c);
////				emp.getTrabajadores().add(tra);
//					createTRARecord(c, emp);
//					++totalContractSum;
//				}
//			}
//			emp.getTcTotales().add(createTCTRecord(ccc));
//			createEDTRecords(ccc, emp);
//		
////		emp.getEdt().addAll(createEDTRecord(ccc));
//		
////		emp.setEdt(createEDTRecords(ccc));
//		
//		// TODO Obligatorio para pago electronico, saldos acreedores y cargo en cuenta
//			emp.setMpg(createMPGRecord(ccc));
//			
//			return emp;
//		}
//		return null;
//	}
//	
//	private RegistryDirStaff getDirStaff(EnterpriseCCC ccc){
//		try {
//			IManagerBean bean = BeanManager.getManagerBean(RegistryDirStaff.class);
//			Criteria criteria = new Criteria();
//			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REGISTRY_ID), ccc.getActivity().getEnterprise().getRegistry().getId() );
//			criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE), new Date() );
//			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REPRESENTATIVE_LABOR), true );
//			List<ITransferObject> dirStaffList = bean.getList(criteria);
//			if (! dirStaffList.isEmpty() ) {
//				return (RegistryDirStaff) dirStaffList.get(0);	
//			}
//		} catch (ManagerBeanException e) {
//			// NADA
//		}
//		return null;
//	}
//	
//	private String autoComplete(String value, int lenght, String completeValue, boolean leftSide) {
//		while( value.length() < lenght ){
//			value = leftSide ? (completeValue + value) : (value + completeValue);
//		}
//		return value;
//	}
//	private RZS createRZSrecord(Enterprise enterprise) {
//		RZS rzs = new RZS();
//		/*
//		TipoAlfabeticoEmpresario
//		1 Individual
//		2 Colectivo
//		3 Sin personalidad jurídica
//		4 Entidad u Organismo de las Admones.Públicas
//		*/
//		rzs.setTipoAlfabeticoEmpresario("1");
//		rzs.setRazonSocial(enterprise.getRegistry().getName());
//		return rzs;
//	}
//	
//	private TRA createTRARecord(Contract contract, EMP emp) throws ManagerBeanException {
//		TRA tra = emp.getTraSegment(contract.getPerson().getSocialSecurityNumber());
////		TRA tra = new TRA();
//		tra.setNumeroAfiliacion( contract.getPerson().getSocialSecurityNumber() );
//		String tipo;
//		if(contract.getPerson().getRegistry().getDocumentType()== DocumentType.NIF){
//			tipo = "1";
//		} else if(contract.getPerson().getRegistry().getDocumentType()== DocumentType.PASSPORT){
//			tipo = "2";
//		} else if(contract.getPerson().getRegistry().getDocumentType()== DocumentType.NIE){
//			tipo = "6";
//		} else {
//			tipo = "1";
//		}
//		String pais = contract.getPerson().getRegistry().getDocumentCountry().getIso3();
//		String doc = autoComplete(contract.getPerson().getRegistry().getDocument(), 14, "0", true);
//		String ipf = StringUtils.isBlank(tipo)?"9":tipo;
//		ipf += StringUtils.isBlank(pais)?BLANK_3:pais; 
//		ipf += doc;
//		tra.setIpf(ipf);
//		tra.setAyn(createAYNRecord(contract));
//		
////		int start = startMonth.ordinal();
////		int end = endMonth.ordinal();
////		while(start<=end){
////			DAT dat = createDATRecord(contract);
////			tra.getDat().add(dat);
////			start++;
////		}
//		
//		tra.setDat(createDATRecords(contract));
//			
//		return tra;
//	}
//	
//	private AYN createAYNRecord(Contract contract) {
//		AYN ayn = new AYN();
//		String ap1 = contract.getPerson().getFirstSurname();
//		String ap2 = contract.getPerson().getSecondSurname();
//		String n = contract.getPerson().getName();
//		ayn.setPrimerApellido(ap1!=null?ap1:BLANK_20);
//		ayn.setSegundoApellido(ap2!=null?ap2:BLANK_20);
//		ayn.setNombre(n!=null?n:BLANK_15);
//		ayn.setAbreviado((ap1!=null?ap1.substring(0, 2):BLANK_2)+(ap2!=null?ap2.substring(0, 2):BLANK_2)+(n!=null?n.substring(0, 1):BLANK_1));
//		return ayn;
//	}
//	
//	private List<DAT> createDATRecords(Contract contract) throws ManagerBeanException {
//		List<DAT> datList = new LinkedList<DAT>();
//		
//		// TODO comprobar que situaciones implican un nuevo segmento de tipo DAT
//		// partes IT, ¿epigrafes? 
//		List<ITransferObject> leaves = getContractLeaves(contract, getStartDate(), getEndDate());
//		if(leaves.isEmpty()){
//			createDATRecord(contract, null, null, null);
//		} else {
//			int leaveDays = 0;
//			for(ITransferObject to: leaves){
//				ContractLeave leave = (ContractLeave) to;
////				createDATRecord(contract, leave.getStartDate(), leave.getEndDate());
//				leaveDays += differenceBetweenDates(leave.getStartDate(), leave.getEndDate());
//			}
//			if(leaveDays < differenceBetweenDates(getStartDate(), getEndDate())){
//				createDATRecord(contract, null, null, leaveDays);
//			}
//		}
//		return datList;
//	}
//	
//	private DAT createDATRecord(Contract contract, Date startDate, Date endDate, Integer leaveDays) {
//		DAT dat = new DAT();
//		dat.setMes(endMonth.ordinal()+1);
//		dat.setIndicadoresPerfil((isPartialStrike(contract)?"H":VIRGULILLA)
//				+(isMoonlighting(contract)?"P":VIRGULILLA)
//				+(getJournalReduction(contract)!=null?getJournalReduction(contract):VIRGULILLA)
//				+(isMonthSalary(contract)?"M":VIRGULILLA)
//				+(isNoRetributionDischarge(contract)?"A":VIRGULILLA)
//				+(getOthers(contract))
//				+(isLessThan7DaysContract(contract)?"C":VIRGULILLA));
//		dat.setDiasHoras(getContractDaysHours(contract));
//		dat.setDiasAlta(getContractDischargeDays(contract));
//		dat.setIndicadorCotizacion(getQuoteIndicator(contract));
//		dat.setIndicadorVacaciones(getVacationIndicator(contract));
//		dat.setClaveJornadasColectivo(getCollectiveJournalHours(contract));
//		dat.setEspecificos(getSpecifics(contract));
//		dat.setIndReduccionBoni(getBonificationReduction(contract));
//		dat.setGrupoCotizacion(getQuoteGroup(contract));
//		dat.setTipoContrato(getContractType(contract));
//		dat.setClaveContrato(getContractKey(contract));
//		dat.setEpigrafeAtEp(getAtEpEpigraph(contract));
//		dat.setEpigrafeSecundario(getSecondariEpigraph(contract));
//		dat.setOcupacion(getContractOccupation(contract));
//		dat.setModalidadCotizacion(getQuoteMode(contract));
//		dat.setIndDiscapacidad(getHandicapIndicator(contract));
//		dat.setIndRelacion(getEmploymentRelation(contract));
//		dat.setColectivoPeculiar(getParticularGroup(contract));
//		dat.setInfoComplementaria(null);
//		
//		createEDLRecords(contract, dat);
//		
//		return dat;
//	}
//
//	private List<ITransferObject> getContractLeaves(Contract contract, Date startDate, Date endDate) throws ManagerBeanException {
//		IManagerBean bean = BeanManager.getManagerBean(ContractLeave.class);
//		Criteria criteria = new Criteria();
//		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_CONTRACT_ID), contract.getId());
//		criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_START_DATE), endDate);
//		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_END_DATE), startDate);
//		Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_END_DATE));
//		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
//		criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_START_DATE));
//		return bean.getList(criteria);
//	}
//	
//	
//	private void createEDLRecords(Contract c, DAT dat) {
//		// TODO Auto-generated method stub
//		Salary salary = getSalary(c);
//		if(salary!=null){
//			if(salary.getCommonBase().equals(salary.getRawCommonBase())){
//				createEDLBa00Segment(salary, dat);
//			} else {
//				createEDLBa01Segment(salary, dat);
//				createEDLBa02Segment(salary, dat);
//			}
//			createEDLBa05Segment(salary, dat);
//			createEDLBa06Segment(salary, dat);
//			createEDLBa07Segment(salary, dat);
//			createEDLBa08Segment(salary, dat);
//			createEDLBa09Segment(salary, dat);
//			createEDLBa10Segment(salary, dat);
//			createEDLBa11Segment(salary, dat);
//			createEDLBa20Segment(salary, dat);
//			createEDLBa21Segment(salary, dat);
//			createEDLBa22Segment(salary, dat);
//			createEDLBa23Segment(salary, dat);
//			createEDLBa28Segment(salary, dat);
//			createEDLBa30Segment(salary, dat);
//			createEDLBa31Segment(salary, dat);
//			createEDLBa32Segment(salary, dat);
//			createEDLBa33Segment(salary, dat);
//			createEDLBa34Segment(salary, dat);
//			createEDLBa35Segment(salary, dat);
//			createEDLBa36Segment(salary, dat);
//			createEDLBa37Segment(salary, dat);
//			createEDLBa38Segment(salary, dat);
//			createEDLBa41Segment(salary, dat);
//			createEDLBa42Segment(salary, dat);
//
//			
//			createEDLCd01Segment(salary, dat);
//			createEDLCd03Segment(salary, dat);
//			createEDLCd05Segment(salary, dat);
//			createEDLCd06Segment(salary, dat);
//			createEDLCd07Segment(salary, dat);
////			SalaryBonus bonus = getBonus(c);
////			if(bonus!=null){
//////				Double baseDc = bonus.getAmount();
//////				Integer dcDays = differenceBetweenDates(bonus.getStartDate().before(getStartDate())?getStartDate():bonus.getStartDate(), bonus.getEndDate().after(getEndDate())?getEndDate():bonus.getEndDate());
//////				Integer dcDays = 0;
////				if(bonus.getAmount() > 0){
////					createEDLCd07Segment(bonus, dat);
////				}
////			}
//			createEDLCd10Segment(salary, dat);
//			createEDLCd11Segment(salary, dat);
//			createEDLCd12Segment(salary, dat);
//			createEDLCd13Segment(salary, dat);
//			createEDLCd16Segment(salary, dat);
//			createEDLCd17Segment(salary, dat);
//			createEDLCd18Segment(salary, dat);
//			createEDLCd20Segment(salary, dat);
//			createEDLCd21Segment(salary, dat);
//			createEDLCd22Segment(salary, dat);
//			createEDLCd23Segment(salary, dat);
//			createEDLCd24Segment(salary, dat);
//			createEDLCd25Segment(salary, dat);
//			createEDLCd26Segment(salary, dat);
//			createEDLCd27Segment(salary, dat);
//			createEDLCd28Segment(salary, dat);
//			createEDLCd29Segment(salary, dat);
//			createEDLCd30Segment(salary, dat);
//			
//		}
//		
//	}
//	
//	private void createEDLCd30Segment(Salary salary, DAT dat) {
//		// TODO 30 Reducciones. SEA Desempleo
//		
//	}
//	private void createEDLCd29Segment(Salary salary, DAT dat) {
//		// TODO 29 Reducciones SEA. Contingencias comunes Sistema Especial Agrario 
//		
//	}
//	private void createEDLCd28Segment(Salary salary, DAT dat) {
//		// TODO 28 Bonificación por ERE 
//		Double amount = 0.0;
//		for(ITransferObject to: getSalaryBonuses(salary.getContract())){
//			SalaryBonus bonus = (SalaryBonus) to;
//			if(bonus.getType() == BonusType.ERE){
//				amount += bonus.getAmount(); 
//			}
//		}
//		if (amount.compareTo(0.0d) > 0) {
//			EDL edl = dat.getEdlSegment("CD28");
//			createEDLRecord(
//					edl,
//					"CD",
//					28,
//					0,
//					new Double(CommonUtil.round(amount)*100).intValue(),
//					" ",
//					0,
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					" ");
//		}
//	}
//	private void createEDLCd27Segment(Salary salary, DAT dat) {
//		// 27 Reducciones REA "Jornadas reales" (Baja a partir del 1 de enero de 2012) Régimen Especial Agrario 
//	}
//	private void createEDLCd26Segment(Salary salary, DAT dat) {
//		// 26 Reducciones REA Cuantía mensual (modalidad G y J) (Baja a partir del 1 de enero de 2012) Régimen Especial Agrario 
//	}
//	private void createEDLCd25Segment(Salary salary, DAT dat) {
//		// TODO 25 Exención de desempleo hijos<30años Autonomos
//		
//	}
//	private void createEDLCd24Segment(Salary salary, DAT dat) {
//		// 24 Bonificación I+D+I (Baja a partir del 1 de agosto de 2012) Régimen General
//	}
//	private void createEDLCd23Segment(Salary salary, DAT dat) {
//		// TODO 23 Bonificación Sector Industrial Incentivado
//		Double amount = 0.0;
//		for(ITransferObject to: getSalaryBonuses(salary.getContract())){
//			SalaryBonus bonus = (SalaryBonus) to;
//			if(bonus.getType() == BonusType.ENCOURAGED_INDUSTRIAL_SECTOR){
//				amount += bonus.getAmount(); 
//			}
//		}
//		if (amount.compareTo(0.0d) > 0) {
//			EDL edl = dat.getEdlSegment("CD23");
//			createEDLRecord(
//					edl,
//					"CD",
//					23,
//					0,
//					new Double(CommonUtil.round(amount)*100).intValue(),
//					" ",
//					0,
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					" ");
//		}
//	}
//	private void createEDLCd22Segment(Salary salary, DAT dat) {
//		// TODO 22 Bonificación Fom. Empleo Cuantía fija. Excepto Rég. Gral. Artistas (0112)
//		Double amount = 0.0;
//		for(ITransferObject to: getSalaryBonuses(salary.getContract())){
//			SalaryBonus bonus = (SalaryBonus) to;
//			if(bonus.getType() == BonusType.EMPLOYMENT_PROMOTION){
//				amount += bonus.getAmount(); 
//			}
//		}
//		if (salary.getContract().getRegimeType() != SSRegimeType.ARTIST
//				&& amount.compareTo(0.0d)>0) {
//			EDL edl = dat.getEdlSegment("CD22");
//			createEDLRecord(
//					edl,
//					"CD",
//					22,
//					0,
//					new Double(CommonUtil.round(amount)*100).intValue(),
//					" ",
//					0,
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					" ");
//		}
//	}
//	private void createEDLCd21Segment(Salary salary, DAT dat) {
//		// 21 Bonificación Copa del America (R.D.L. 2146/2004) (Baja a partir del 1 de agosto de 2012)
//	}
//	private void createEDLCd20Segment(Salary salary, DAT dat) {
//		// TODO 20 Bonificación Ceuta y Melilla (O. TAS/471/2004)
//		Double amount = 0.0;
//		for(ITransferObject to: getSalaryBonuses(salary.getContract())){
//			SalaryBonus bonus = (SalaryBonus) to;
//			if(bonus.getType() == BonusType.CEUTA_MELILLA){
//				amount += bonus.getAmount(); 
//			}
//		}
//		if (amount.compareTo(0.0d) > 0) {
//			EDL edl = dat.getEdlSegment("CD20");
//			createEDLRecord(
//					edl,
//					"CD",
//					20,
//					0,
//					new Double(CommonUtil.round(amount)*100).intValue(),
//					" ",
//					0,
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					" ");
//		}
//	}
//	private void createEDLCd18Segment(Salary salary, DAT dat) {
//		// 18 Reducción por Exención de desempleo (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario)
//	}
//	private void createEDLCd17Segment(Salary salary, DAT dat) {
//		// TODO 17 Reducción contingencias comunes excepto I.T. (R.D.L. 16/2001)
//		
//	}
//	private void createEDLCd16Segment(Salary salary, DAT dat) {
//		// 16 Bonificación por trabajadores con 60 o más años (Baja a partir del 1 de agosto de 2012)
//		Double amount = 0.0;
//		for(ITransferObject to: getSalaryBonuses(salary.getContract())){
//			SalaryBonus bonus = (SalaryBonus) to;
//			if(bonus.getType() == BonusType.GT_60){
//				amount += bonus.getAmount(); 
//			}
//		}
//		if (amount.compareTo(0.0d) > 0) {
//			EDL edl = dat.getEdlSegment("CD16");
//			createEDLRecord(
//					edl,
//					"CD",
//					16,
//					0,
//					new Double(CommonUtil.round(amount)*100).intValue(),
//					" ",
//					0,
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					" ");
//		}
//	}
//	private void createEDLCd13Segment(Salary salary, DAT dat) {
//		// TODO 13 Bonificación minusvalidos en Centros Especiales de Empleo
//		Double amount = 0.0;
//		for(ITransferObject to: getSalaryBonuses(salary.getContract())){
//			SalaryBonus bonus = (SalaryBonus) to;
//			if(bonus.getType() == BonusType.HANDICAP){
//				amount += bonus.getAmount(); 
//			}
//		}
//		if (amount.compareTo(0.0d) > 0) {
//			EDL edl = dat.getEdlSegment("CD13");
//			createEDLRecord(
//					edl,
//					"CD",
//					13,
//					0,
//					new Double(CommonUtil.round(amount)*100).intValue(),
//					" ",
//					0,
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					" ");
//		}
//	}
//	private void createEDLCd12Segment(Salary salary, DAT dat) {
//		// TODO 12 Bonificación por Ley 19/94 (Registro Canario) Régimen del Mar
//		Double amount = 0.0;
//		for(ITransferObject to: getSalaryBonuses(salary.getContract())){
//			SalaryBonus bonus = (SalaryBonus) to;
//			if(bonus.getType() == BonusType.LAW_19_94){
//				amount += bonus.getAmount(); 
//			}
//		}
//		if (salary.getContract().getRegimeType() != SSRegimeType.SEA_WORKERS
//				&& amount.compareTo(0.0d) > 0) {
//			EDL edl = dat.getEdlSegment("CD12");
//			createEDLRecord(
//					edl,
//					"CD",
//					12,
//					0,
//					new Double(CommonUtil.round(amount)*100).intValue(),
//					" ",
//					0,
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					" ");
//		}
//	}
//	private void createEDLCd11Segment(Salary salary, DAT dat) {
//		// TODO 11 Bonificación por formación teórica a distancia
//		Double amount = 0.0;
//		for(ITransferObject to: getSalaryBonuses(salary.getContract())){
//			SalaryBonus bonus = (SalaryBonus) to;
//			if(bonus.getType() == BonusType.DISTANCE_FORMATION){
//				amount += bonus.getAmount(); 
//			}
//		}
//		if(amount.compareTo(0.0d)>0){
//			// TODO calculate dcDays
//			Integer dcDays = 0;
//			EDL edl = dat.getEdlSegment("CD11");
//			createEDLRecord(
//					edl,
//					"CD",
//					11,
//					dcDays,
//					new Double(CommonUtil.round(amount)*100).intValue(),
//					" ",
//					0,
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					" ");
//		}
//		 
//	}
//	private void createEDLCd10Segment(Salary salary, DAT dat) {
//		// TODO 10 Bonificación por formación teórica presencial
//		Double amount = 0.0;
//		for(ITransferObject to: getSalaryBonuses(salary.getContract())){
//			SalaryBonus bonus = (SalaryBonus) to;
//			if(bonus.getType() == BonusType.CLASSROOM_FORMATION){
//				amount += bonus.getAmount(); 
//			}
//		}
//		if(amount.compareTo(0.0d)>0){
//			// TODO calculate dcDays
//			Integer dcDays = 0;
//			EDL edl = dat.getEdlSegment("CD10");
//			createEDLRecord(
//					edl,
//					"CD",
//					10,
//					dcDays,
//					new Double(CommonUtil.round(amount)*100).intValue(),
//					" ",
//					0,
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					" ");
//		}
//	}
//	private void createEDLCd07Segment(Salary salary, DAT dat) { 
//		// TODO 7 Bonificaciones Contratos con derecho a bonificación/reducción (casilla 601 de TC1)
//		Double amount = 0.0;
//		for(ITransferObject to: getSalaryBonuses(salary.getContract())){
//			SalaryBonus bonus = (SalaryBonus) to;
//			if(isBonusRight(bonus)){
//				amount += bonus.getAmount(); 
//			}
//		}
//		if(amount.compareTo(0.0d)>0){
//			Integer dcDays = 0;
//			EDL edl = dat.getEdlSegment("CD07");
//			createEDLRecord(
//					edl,
//					"CD",
//					7,
//					0,
//					new Double(CommonUtil.round(amount)*100).intValue(),
//					" ",
//					0,
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					" ");
//		}
//	}
//	private void createEDLCd06Segment(Salary salary, DAT dat) {
//		// TODO 6 Reducciones Contratos con derecho a reducción (casilla 209 de TC1)
////		EDL edl = dat.getEdlSegment("CD06");
////		createEDLRecord(
////				edl,
////				"CD",
////				6,
////				0,
////				new Double(getProfessionalDiseaseAmount(salary)*100).intValue(),
////				" ",
////				0,
////				autoComplete("0", 8, "0", true),
////				autoComplete("0", 8, "0", true),
////				autoComplete("0", 8, "0", true),
////				" ");
//	}
//	private void createEDLCd05Segment(Salary salary, DAT dat) {
//		// TODO 5 IT O.M. 3/4/73 Minería del Carbón
//		if (salary.getContract().getRegimeType() == SSRegimeType.COAL_MINING) {
////			EDL edl = dat.getEdlSegment("CD05");
////			createEDLRecord(
////					edl,
////					"CD",
////					5,
////					0,
////					new Double(getProfessionalDiseaseAmount(salary)*100).intValue(),
////					" ",
////					0,
////					autoComplete("0", 8, "0", true),
////					autoComplete("0", 8, "0", true),
////					autoComplete("0", 8, "0", true),
////					" ");
//		}
//	}
//	private void createEDLCd03Segment(Salary salary, DAT dat) {
//		// TODO 3 IT por AT y EP No es de aplicación en el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
//		Double professionalDiseaseAmount = 0.0;
//		if (salary.getContract().getRegimeType() != SSRegimeType.ARTIST
//				&& salary.getContract().getRegimeType() != SSRegimeType.AGRICULTURAL
//				&& professionalDiseaseAmount.compareTo(0.0d)>0) {
//			EDL edl = dat.getEdlSegment("CD03");
//			createEDLRecord(
//					edl,
//					"CD",
//					3,
//					0,
//					new Double(CommonUtil.round(professionalDiseaseAmount)*100).intValue(),
//					" ",
//					0,
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					" ");
//		}
//		
//	}
//	private void createEDLCd01Segment(Salary salary, DAT dat) {
//		// TODO 1 IT enfermedad común y accidente no laboral 
//		// No es de aplicación en el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
//		Double commonDiseaseAmount = 0.0;
//		if (salary.getContract().getRegimeType() != SSRegimeType.ARTIST
//				&& salary.getContract().getRegimeType() != SSRegimeType.AGRICULTURAL
//				&& commonDiseaseAmount.compareTo(0.0d)>0) {
//			EDL edl = dat.getEdlSegment("CD01");
//			createEDLRecord(
//					edl,
//					"CD",
//					1,
//					0,
//					new Double(CommonUtil.round(commonDiseaseAmount)*100).intValue(),
//					" ",
//					0,
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					" ");
//		}
//	}
//	
//	
//	private void createEDLBa42Segment(Salary salary, DAT dat) {
//		// TODO 42 Base exclusiva de AT y EP sin cotización de Otras Cotizaciones
////		if (salary.getContract().getRegimeType() == SSRegimeType.AGRICULTURAL
////				&& new Boolean(getContractDataMap(salary.getContract()).get(ContextVariable.MATERNITY))
////				&& ( !getContractDataMap(salary.getContract()).get(ContextVariable.UNEMPLOY_ENTERPRISE).isEmpty() 
////						|| !getContractDataMap(salary.getContract()).get(ContextVariable.FOGASA_ENTERPRISE).isEmpty() )
////				&& CommonUtil.getYear(getStartDate()) < 2012) {
////			EDL edl = dat.getEdlSegment("BA42");
////			createEDLRecord(
////					edl,
////					"BA",
////					42,
////					0,
////					new Double((salary.getCommonBase())*100).intValue(),
////					" ",
////					0,
////					autoComplete("0", 8, "0", true),
////					autoComplete("0", 8, "0", true),
////					autoComplete("0", 8, "0", true),
////					" ");
////		}
//	}
//	private void createEDLBa41Segment(Salary salary, DAT dat) {
//		// TODO 41 Contingencias Comunes y FOGASA, (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
//		Double amount = 0.0;
//		if (salary.getContract().getRegimeType() == SSRegimeType.AGRICULTURAL
//				&& CommonUtil.getYear(getStartDate()) < 2012) {
//			EDL edl = dat.getEdlSegment("BA41");
//			createEDLRecord(
//					edl,
//					"BA",
//					41,
//					0,
//					new Double(CommonUtil.round(amount)*100).intValue(),
//					" ",
//					0,
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					" ");
//		}
//	}
//	private void createEDLBa38Segment(Salary salary, DAT dat) {
//		// 38 Cotización exclusivamente por FOGASA. (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario) 
//		Double amount = 0.0;
//		
//		if (salary.getContract().getRegimeType() == SSRegimeType.AGRICULTURAL
//				&& CommonUtil.getYear(getStartDate()) < 2012) {
//			EDL edl = dat.getEdlSegment("BA38");
//			createEDLRecord(
//					edl,
//					"BA",
//					38,
//					0,
//					new Double(CommonUtil.round(amount)*100).intValue(),
//					" ",
//					0,
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					" ");
//		}
//	}
//	private void createEDLBa37Segment(Salary salary, DAT dat) {
//		// 37 Cotización jornadas reales y FOGASA excluido desempleo(Baja a partir del 1 de enero de 2012). (Régimen Especial Agrario) 
//	}
//	private void createEDLBa36Segment(Salary salary, DAT dat) {
//		// 36 Base exclusiva Desempleo/FOGASA tipo total. (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario) 
//	}
//	private void createEDLBa35Segment(Salary salary, DAT dat) {
//		// 35 Base fija cotización trabajadores cuenta ajena extranjeros (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario) 
//	}
//	private void createEDLBa34Segment(Salary salary, DAT dat) {
//		// 34 Base de AT en vacaciones. (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
//	}
//	private void createEDLBa33Segment(Salary salary, DAT dat) {
//		// 33 Base de cotización exclusiva de Otras Cotizaciones en situación de IT/maternidad/riesgo durante el embarazo. 
//		// Trabajadores no fijos (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario) 
//	}
//	private void createEDLBa32Segment(Salary salary, DAT dat) {
//		// 32 Base de cotización Jornadas Reales en situación de IT por desempleo empresarial y FOGASA. 
//		// (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
//	}
//	private void createEDLBa31Segment(Salary salary, DAT dat) {
//		// 31 Contingencias comunes trajadores no fijos (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario) 
//	}
//	private void createEDLBa30Segment(Salary salary, DAT dat) {
//		// 30 Cotización por Jornadas Reales. (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
//	}
//	private void createEDLBa28Segment(Salary salary, DAT dat) {
//		// 28 Diferencia en bases (contingencias comunes y salario normalizado), cotización exclusivamente empresarial 
//		// Solo para liquidaciones anteriores al año 2002 del Régimen Especial de la Minería del Carbón (0911). Baja a partir de 2002 
//	}
//	private void createEDLBa23Segment(Salary salary, DAT dat) {
//		// 23 Base de cotización tipo total desempleo y FOGASA. (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
//	}
//	private void createEDLBa22Segment(Salary salary, DAT dat) {
//		// TODO 22 Base de cotización empresarial por AT y EP y Otras Cotizaciones
////		EDL edl = dat.getEdlSegment("BA22");
////		createEDLRecord(
////				edl,
////				"BA",
////				22,
////				0,
////				new Double((salary.getNonEstructuralOvertimeBase())*100).intValue(),
////				" ",
////				0,
////				autoComplete("0", 8, "0", true),
////				autoComplete("0", 8, "0", true),
////				autoComplete("0", 8, "0", true),
////				" ");
//	}
//	private void createEDLBa21Segment(Salary salary, DAT dat) {
//		// 21 Base de cotización empresarial por contingencias comunes Base de cotización empresarial por desempleo y FOGASA 
//		// (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
//	}
//	private void createEDLBa20Segment(Salary salary, DAT dat) {
//		// TODO 20 Base de cotización empresarial C.Comunes = AT y EP
////			EDL edl = dat.getEdlSegment("BA20");
////			createEDLRecord(
////					edl,
////					"BA",
////					20,
////					0,
////					new Double((salary.getNonEstructuralOvertimeBase())*100).intValue(),
////					" ",
////					0,
////					autoComplete("0", 8, "0", true),
////					autoComplete("0", 8, "0", true),
////					autoComplete("0", 8, "0", true),
////					" ");
//	}
//	private void createEDLBa11Segment(Salary salary, DAT dat) {
//		// TODO 11 Horas extras no estructurales / Otras horas extras desde 1/1/98 
//		// No será de utilización para Régimen General de Artistas (0112),), ni Régimen Especial de Minería del Carbón (0911)
//		if (salary.getContract().getRegimeType() != SSRegimeType.ARTIST
//				&& salary.getContract().getRegimeType() != SSRegimeType.COAL_MINING
//				&& salary.getNonEstructuralOvertimeBase()!=null && salary.getNonEstructuralOvertimeBase().compareTo(0.0d)>0) {
//			EDL edl = dat.getEdlSegment("BA11");
//			createEDLRecord(
//					edl,
//					"BA",
//					11,
//					0,
//					new Double((salary.getNonEstructuralOvertimeBase())*100).intValue(),
//					" ",
//					0,
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					" ");
//		}
//	}
//	private void createEDLBa10Segment(Salary salary, DAT dat) {
//		// TODO 10 Horas extras estructurales / Causa de fuerza mayor desde 1/1/98 
//		// No será de utilización para Régimen General de Artistas (0112), Régimen Especial de Minería del Carbón (0911).
//		if (salary.getContract().getRegimeType() != SSRegimeType.ARTIST
//				&& salary.getContract().getRegimeType() != SSRegimeType.COAL_MINING
//				&& salary.getOvertimeBase()!=null && salary.getOvertimeBase().compareTo(0.0d)>0) {
//			EDL edl = dat.getEdlSegment("BA10");
//			createEDLRecord(
//					edl,
//					"BA",
//					10,
//					0,
//					new Double((salary.getOvertimeBase())*100).intValue(),
//					" ",
//					0,
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					" ");
//		}
//	}
//	private void createEDLBa09Segment(Salary salary, DAT dat) {
//		// TODO 9 Horas complementarias No será de utilización para Régimen General de Artistas (0112)
//		if (salary.getContract().getRegimeType() != SSRegimeType.ARTIST) {
////			EDL edl = dat.getEdlSegment("BA09");
////			createEDLRecord(
////					edl,
////					"BA",
////					9,
////					0,
////					new Double((salary.get)*100).intValue(),
////					" ",
////					0,
////					autoComplete("0", 8, "0", true),
////					autoComplete("0", 8, "0", true),
////					autoComplete("0", 8, "0", true),
////					" ");
//		}
//	}
//	private void createEDLBa08Segment(Salary salary, DAT dat) {
//		// 8 Diferencia Bases (contingencias comunes y salario normalizado) 
//		// Solo para liquidaciones anteriores al año 2002 del Régimen Especial de la Minería del Carbón (0911). Baja a partir de 2002
//	}
//	private void createEDLBa07Segment(Salary salary, DAT dat) {
//		// 7 AT y EP sin horas extraordinarias 
//		// No será de utilización para Régimen General de Artistas (0112), ni Régimen Especial de Minería del Carbón (0911).
//		// Baja a partir de 2002
//		if (salary.getContract().getRegimeType() != SSRegimeType.ARTIST
//				&& salary.getContract().getRegimeType() != SSRegimeType.COAL_MINING
//				&& salary.getNonEstructuralOvertimeBase()!=null && salary.getNonEstructuralOvertimeBase().compareTo(0.0d)>0
//				&& CommonUtil.getYear(getStartDate()) < 2002) {
//			EDL edl = dat.getEdlSegment("BA07");
//			createEDLRecord(
//					edl,
//					"BA",
//					7,
//					0,
//					new Double((salary.getProfessionalBase()-salary.getOvertimeBase())*100).intValue(),
//					" ",
//					0,
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					" ");
//		}
//	}
//	private void createEDLBa06Segment(Salary salary, DAT dat) {
//		// TODO 6 Importe percepciones Integras (Artistas.)
//		if(salary.getContract().getRegimeType() == SSRegimeType.ARTIST){
//			EDL edl = dat.getEdlSegment("BA05");
//			createEDLRecord(
//					edl,
//					"BA",
//					5,
//					0,
//					new Double(salary.getTotalLiquid()*100).intValue(),
//					" ",
//					0,
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					autoComplete("0", 8, "0", true),
//					" ");
//		}
//	}
//	private void createEDLBa05Segment(Salary salary, DAT dat) {
//		// TODO 5 Exceso del tope (Minería del Carbón)
////		EDL edl = dat.getEdlSegment("BA05");
////		createEDLRecord(
////				edl,
////				"BA",
////				5,
////				0,
////				new Double(salary.getRawCommonBase()*100).intValue(),
////				" ",
////				0,
////				autoComplete("0", 8, "0", true),
////				autoComplete("0", 8, "0", true),
////				autoComplete("0", 8, "0", true),
////				" ");
//	}
//	private void createEDLBa02Segment(Salary salary, DAT dat) {
//		// TODO 2 AT y EP
//		EDL edl = dat.getEdlSegment("BA02");
//		createEDLRecord(
//				edl,
//				"BA",
//				2,
//				0,
//				new Double(salary.getRawCommonBase()*100).intValue(),
//				" ",
//				0,
//				autoComplete("0", 8, "0", true),
//				autoComplete("0", 8, "0", true),
//				autoComplete("0", 8, "0", true),
//		" ");
//	}
//	private void createEDLBa01Segment(Salary salary, DAT dat) {
//		// TODO 1 Contingencias comunes
//		EDL edl = dat.getEdlSegment("BA01");
//		createEDLRecord(
//				edl,
//				"BA",
//				1,
//				0,
//				new Double(salary.getCommonBase()*100).intValue(),
//				" ",
//				0,
//				autoComplete("0", 8, "0", true),
//				autoComplete("0", 8, "0", true),
//				autoComplete("0", 8, "0", true),
//		" ");
//	}
//	private void createEDLBa00Segment(Salary salary, DAT dat) {
//		// TODO 0 Normal C. Comunes = AT y EP
//		EDL edl = dat.getEdlSegment("BA00");
//		createEDLRecord(
//				edl,
//				"BA",
//				0,
//				0,
//				new Double(salary.getCommonBase()*100).intValue(),
//				" ",
//				0,
//				autoComplete("0", 8, "0", true),
//				autoComplete("0", 8, "0", true),
//				autoComplete("0", 8, "0", true),
//		" ");		
//	}
//	private Integer getDcDays(Contract c) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//	private List<ITransferObject> getSalaryBonuses(Contract c) {
//		Salary salary = getSalary(c);
//		try {
//			IManagerBean bean = BeanManager.getManagerBean(SalaryBonus.class);
//			Criteria criteria = new Criteria();
//			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_BONUS_SALARY_ID), salary.getId());
//			return bean.getList(criteria);
//		} catch (ManagerBeanException e) {
//			// NADA
//		}
//		return null;
//	}
//	
//	private boolean isBonusRight(SalaryBonus bonus){
////		Mujeres desempleadas así como las víctimas de violencia de género o doméstica.
////
////		Mujeres desempleadas contratadas en los 24 meses siguientes al parto, adopción o acogimiento.
////
////		Mujeres desempleadas contratadas después de 5 años de inactividad laboral, si, anteriormente a su retirada, han trabajado, al menos, 3 años.
////
////		Mayores de 45 años desempleados.
////
////		Jóvenes desempleados de 16 a 30 años.
////
////		Desempleados durante al menos 6 meses y trabajadores en situación de exclusión social.
////
////		Personas con discapacidad.
////
////		Personas con discapacidad severa.
////
////		Personas con discapacidad contratados por los centros especiales de empleo.
////		
////		Contratación indefinida de personal investigador por parte de las empresas dedicadas a actividades de investigación y desarrollo e innovación tecnológica.
////		Real Decreto 278/2007, de 23 de febrero, sobre bonificaciones en la cotización a la (Seguridad Social) respecto del personal investigador. (BOE de 24 de febrero)
////
////		Desempleados excedentes del sector textil y de la confección que hubieran sido despedidos entre el 13 de junio de 2006 y el 31 de diciembre de 2008, siempre que la contratación se produzca durante los 2 años siguientes a la fecha de despido.
//		if(bonus.getType() == BonusType.EMPLOYMENT_PROMOTION){
//			
//		}
//		return false;
//	}
//	
//	private Integer getParticularGroup(Contract c) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	private Integer getEmploymentRelation(Contract c) {
////		0100 Personal de Alta Dirección
////		0409 Deportistas profesionales
////		0900 Abogados en despachos de abogados
////		9909 Personal becario de investigación
//		String tc2 = getContractDataMap(c).get(ContextVariable.TC2);
//		if(tc2!=null && (tc2.equals("100") || tc2.equals("409") || tc2.equals("900"))){
//			return Integer.parseInt(tc2);
//		}
//		return null;
//	}
//	private String getHandicapIndicator(Contract c) {
//		// TODO Auto-generated method stub
////		D Minusvalía igual o superior al 33%
////		S Pensionista incapacidad permanente de la S.S.
////		P Pensionista incapacidad permanente clases pasivas
//		
//		return null;
//	}
//	private String getQuoteMode(Contract c) {
//		// TODO Auto-generated method stub
////		J Cotización Jornadas Reales
////		G Cotización Sistema General
//		//obligatorio para reg. 0613 
//		return null;
//	}
//	private String getContractOccupation(Contract c) {
//		String o = getContractDataMap(c).get(ContextVariable.OCCUPATION);
//		return o!=null && !o.isEmpty()?o:null;
//	}
//	private Integer getSecondariEpigraph(Contract c) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	private Integer getAtEpEpigraph(Contract c) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	private Integer getContractKey(Contract c) {
//		String tc2 = getContractDataMap(c).get(ContextVariable.TC2);
//		return tc2!=null && !tc2.isEmpty()?Integer.parseInt(tc2):null;
//	}
//	private String getContractType(Contract c) {
//		String i = getContractDataMap(c).get(ContextVariable.INDEFINITE);
//		return i!=null && !i.isEmpty()?"I":"D";
//	}
//	private Integer getQuoteGroup(Contract c) {
//		String q = getContractDataMap(c).get(ContextVariable.QUOTE_GROUP);
//		return q!=null && !q.isEmpty()?Integer.parseInt(q):null;
//	}
//	private Integer getBonificationReduction(Contract c) {
//		// TODO Auto-generated method stub
////		1 Reducción minima
////		2 Reducción media
////		3 Reducción máxima
////		4 Importe total sin reducción
//		return null;
//	}
//	private Integer getSpecifics(Contract c) {
//		// TODO Auto-generated method stub
//		// obligatorio para regimen 0911
//		return null;
//	}
//	private String getCollectiveJournalHours(Contract c) {
//		// TODO Auto-generated method stub
//		// Obligatorio para el R.E. Minería del Carbón.
////		0 Normales
////		2 Efectivos
//		return null;
//	}
//	private String getVacationIndicator(Contract c) {
//		String v = getContractDataMap(c).get(ContextVariable.NO_HOLIDAYS);
//		return v!=null && !v.isEmpty()?"V":BLANK_1;
//	}
//	private String getQuoteIndicator(Contract c) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	private Integer getContractDischargeDays(Contract c) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	private Integer getContractDaysHours(Contract c) {
//		// TODO Auto-generated method stub
//		Calendar cal = Calendar.getInstance();
//		cal.set(year, startMonth.ordinal(), 1);
//		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//	}
//	private boolean isLessThan7DaysContract(Contract c) {
//		if(c.getEndDate()!=null && differenceBetweenDates(c.getStartDate(), c.getEndDate())<7){
//			return true;
//		}
//		return false;
//	}
//	private String getOthers(Contract c) {
//		// TODO Auto-generated method stub
//		
////		R Regulación de empleo Parcial (parte trabajada)
////		T Regulación de empleo Total
////		P Regulación de empleo Parcial (parte E.R.E)
////		I Incapacidad temporal diferida
//		return " ";
//	}
//	private boolean isNoRetributionDischarge(Contract c) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//	private boolean isMonthSalary(Contract c) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//	private String getJournalReduction(Contract c) {
////		~ Sin reducción
////		I Incapacidad Temporal
////		R Con reducción (jornada guarda legal,contempladas en la
////		Ley Orgánica 3/2007, de 22 de marzo de 2008,de Igualdad
////		maternidad/Paternidad a tiempo parcial). Jornada trabajada
////		T Maternidad/Paternidad Tiempo Parcial (jornada de
////		descanso)
////		D Descanso por Maternidad/Paternidad a tiempo completo
////		E Riesgo durante el embarazo
//		return null;
//	}
//	private boolean isMoonlighting(Contract c) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//	private boolean isPartialStrike(Contract c) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//	private EDL createEDLRecord(EDL edl, String type, Integer key, Integer element, Integer amount, String sign, Integer resolutionType, String resolutionDate, String startPeriod, String endPeriod, String resolutionReference) {
////		EDL edl = null;
////		edl = new EDL();
//		edl.setTipoElementoDatos(type);
//		edl.setClave(key);
//		edl.setElemento(element);
//		edl.setImporte(amount);
//		edl.setSigno(sign);
//		edl.setTipoResolucion(resolutionType);
//		edl.setFechaResolucion(resolutionDate);
//		edl.setInicioPeriodo(startPeriod);
//		edl.setFinPeriodo(endPeriod);
//		edl.setReferencia(resolutionReference);
//		return edl;
//	}
//	
//	private String getResolutionReference(Contract c) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	private String getEndPeriod(Contract c) {
//		// TODO Auto-generated method stub
//		Calendar cal = Calendar.getInstance();
//		cal.set(year, endMonth.ordinal(), 1);
//		return year.toString()+autoComplete(String.valueOf(endMonth.ordinal()+1), 2, "0", true)+cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//	}
//	private String getStartPeriod(Contract c) {
//		// TODO Auto-generated method stub
//		return year.toString()+autoComplete(String.valueOf(startMonth.ordinal()+1), 2, "0", true)+"01";
//	}
//	private String getResolutionDate(Contract c) {
//		// TODO Auto-generated method stub
//		Calendar cal = Calendar.getInstance();
//		cal.set(year, endMonth.ordinal(), 1);
//		return year.toString()+autoComplete(String.valueOf(endMonth.ordinal()+1), 2, "0", true)+cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//	}
//	private Integer getResolutionType(Contract c) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	private String getAmountSign(Salary salary) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	private String getDataAmount(Salary salary) {
//		// TODO Auto-generated method stub
////		return String.valueOf(CommonUtil.round(salary.getCommonBase()));
//		return String.valueOf(new Double(salary.getCommonBase()*100).intValue());
//	}
//	private Integer getDataElement(Contract c) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	private Integer getDataKey(Contract c) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	private TCT createTCTRecord(EnterpriseCCC ccc) throws ManagerBeanException {
//		// TODO
//		Calendar cal = Calendar.getInstance();
//		cal.set(year, endMonth.ordinal(), 1);
//		TCT tct = new TCT();
//		tct.setEntidadAtEp(Mutual.M001.getValue());
//		tct.setNumeroTrabajadores(Integer.parseInt(autoComplete(String.valueOf(totalContractSum), 6, "0", true)));
//		// numero de trabajadores fijos con jornadas reales. Sin cumplimentacion a partir de 2009
//		tct.setNumeroTrabajadoresFijos(null);
//		// numero de trabajadores no fijos con jornadas reales. Sin cumplimentacion a partir de 2009 
//		tct.setNumeroTrabajadoresNoFijos(null);
//		// total numero trabajadores con jornadas reales. Sin cumplimentacion a partir de 2009
//		tct.setTotalNumeroTrabajadores(null);
//		if(liquidationType==LiquidationType.L02 || liquidationType==LiquidationType.L03){
//			tct.setFechaControl(year+Integer.parseInt(autoComplete(String.valueOf(startMonth.ordinal()+1), 2, "0", true)+"01"));
//		} else {
//			tct.setFechaControl(null);
//		}
//		tct.setTotalJornadas(null);
//		// numero trabajadores extranjeros del REA
//		tct.setTrabajadoresExtranjeros(null);
//		return tct;
//	}
//	
//	private Map<String, EDT> createEDTRecords(EnterpriseCCC ccc, EMP emp) throws ManagerBeanException {
//		// TODO
//		List<EDT> list = new LinkedList<EDT>();
//		EDT edt = null;
//		
//		for(ITransferObject to: getContracts(ccc, getStartDate())){
//			Contract c = (Contract) to;
//		
//			if(getSalary(c)!=null){
//				
//				createEDTBa01Segment(c, emp);
//				createEDTBa02Segment(c, emp);
//				createEDTBa05Segment(c, emp);
//				createEDTBa06Segment(c, emp);
//				createEDTBa07Segment(c, emp);
//				createEDTBa08Segment(c, emp);
//				createEDTBa09Segment(c, emp);
//				createEDTBa10Segment(c, emp);
//				createEDTBa11Segment(c, emp);
//				createEDTBa21Segment(c, emp);
//				createEDTBa22Segment(c, emp);
//				createEDTBa23Segment(c, emp);
//				createEDTBa28Segment(c, emp);
//				createEDTBa30Segment(c, emp);
//				createEDTBa31Segment(c, emp);
//				createEDTBa32Segment(c, emp);
//				createEDTBa33Segment(c, emp);
//				createEDTBa34Segment(c, emp);
//				createEDTBa35Segment(c, emp);
//				createEDTBa36Segment(c, emp);
//				createEDTBa37Segment(c, emp);
//				createEDTBa38Segment(c, emp);
//				createEDTBa41Segment(c, emp);
//				createEDTBa42Segment(c, emp);
//				
//				createEDTCd01Segment(c, emp);
//				createEDTCd03Segment(c, emp);
//				createEDTCd05Segment(c, emp);
//				createEDTCd06Segment(c, emp);
//				createEDTCd07Segment(c, emp);
//				createEDTCd10Segment(c, emp);
//				createEDTCd11Segment(c, emp);
//				createEDTCd12Segment(c, emp);
//				createEDTCd13Segment(c, emp);
//				createEDTCd16Segment(c, emp);
//				createEDTCd17Segment(c, emp);
//				createEDTCd18Segment(c, emp);
//				createEDTCd20Segment(c, emp);
//				createEDTCd21Segment(c, emp);
//				createEDTCd22Segment(c, emp);
//				createEDTCd23Segment(c, emp);
//				createEDTCd24Segment(c, emp);
//				createEDTCd25Segment(c, emp);
//				createEDTCd26Segment(c, emp);
//				createEDTCd27Segment(c, emp);
//				createEDTCd28Segment(c, emp);
//				createEDTCd29Segment(c, emp);
//				createEDTCd30Segment(c, emp);
//				
//				createEDTCa01Segment(c, emp);
//				createEDTCa02Segment(c, emp);
//				createEDTCa11Segment(c, emp);
//				createEDTCa12Segment(c, emp);
//				createEDTCa20Segment(c, emp);
//				createEDTCa21Segment(c, emp);
//				createEDTCa22Segment(c, emp);
//				createEDTCa30Segment(c, emp);
//				createEDTCa31Segment(c, emp);
//				createEDTCa32Segment(c, emp);
//				createEDTCa50Segment(c, emp);
//				createEDTCa51Segment(c, emp);
//				createEDTCa52Segment(c, emp);
//				createEDTCa53Segment(c, emp);
//				createEDTCa54Segment(c, emp);
//				createEDTCa55Segment(c, emp);
//				createEDTCa56Segment(c, emp);
//				createEDTCa57Segment(c, emp);
//				createEDTCa60Segment(c, emp);
//				createEDTCa80Segment(c, emp);
//				createEDTCa90Segment(c, emp);
//				
//				createEDTTt10Segment(c, emp);
//				createEDTTt20Segment(c, emp);
//				createEDTTt30Segment(c, emp);
//				createEDTTt9XSegment(c, emp);
//				createEDTTt93Segment(c, emp);
//			}
//
//		}
//		
////		return list;
//		return null;
//	}
//	
//	private void createEDTTt93Segment(Contract c, EMP emp) {
//		// TODO 93 A compensar
//		
//	}
//	/**
//	 * se crea el segmento tt91 o tt92 dependiendo del signo del importe
//	 */
//	private void createEDTTt9XSegment(Contract c, EMP emp) {
//		// TODO 91 A Ingresar
//		// TODO 92 A percibir
//		Integer amount = (emp.getEdt().containsKey("4_"+"TT10")?emp.getEdtSegment("4_"+"TT10").getImporte():0) 
//		+ (emp.getEdt().containsKey("4_"+"TT20")?emp.getEdtSegment("4_"+"TT20").getImporte():0)
//		+ (emp.getEdt().containsKey("4_"+"TT30")?emp.getEdtSegment("4_"+"TT30").getImporte():0);
//		if(amount != 0){
//			EDT edt = emp.getEdtSegment("4_"+"TT91");
//			edt.setTipoElemento("TT");
//			edt.setClave(amount<0?92:91);
//			edt.setCalificadorClave(null);
//			edt.setBase(null);
//			edt.setIndicadorFactorTipo(" ");
//			edt.setParteEnteraTipo(0);
//			edt.setParteDecimalFactorTipo(0);
//			edt.setSigno(amount<0?"-":" ");
//			if(amount<0){
//				amount += (emp.getEdt().containsKey("3_"+"CA90")?emp.getEdtSegment("3_"+"CA90").getImporte():0);
//			}
//			edt.setImporte(amount);
//		}
//	}
//	private void createEDTTt30Segment(Contract c, EMP emp) {
//		// TODO 30 Liquido otras cotizaciones
//		Integer amount = (emp.getEdt().containsKey("3_"+"CA50")&&emp.getEdtSegment("3_"+"CA50").getImporte()!=null?emp.getEdtSegment("3_"+"CA50").getImporte():0) 
//		+ (emp.getEdt().containsKey("3_"+"CA57")?emp.getEdtSegment("3_"+"CA57").getImporte():0)
//		- (emp.getEdt().containsKey("3_"+"CA60")&&emp.getEdtSegment("3_"+"CA60").getImporte()!=null?emp.getEdtSegment("3_"+"CA60").getImporte():0)
//		- (emp.getEdt().containsKey("2_"+"CD24")?emp.getEdtSegment("2_"+"CD24").getImporte():0);
//		if(amount != 0){
//			EDT edt = emp.getEdtSegment("4_"+"TT30");
//			edt.setTipoElemento("TT");
//			edt.setClave(30);
//			edt.setCalificadorClave(null);
//			edt.setBase(null);
//			edt.setIndicadorFactorTipo(" ");
//			edt.setParteEnteraTipo(0);
//			edt.setParteDecimalFactorTipo(0);
//			edt.setImporte(amount);
//			edt.setSigno(amount<0?"-":" ");
//		}
//	}
//	private void createEDTTt20Segment(Contract c, EMP emp) {
//		// TODO 20 Liquido accidentes de trabajo y enfermedad profesional
//		Integer amount = (emp.getEdt().containsKey("3_"+"CA30")&&emp.getEdtSegment("3_"+"CA30").getImporte()!=null?emp.getEdtSegment("3_"+"CA30").getImporte():0) 
//		- (emp.getEdt().containsKey("2_"+"CD03")?emp.getEdtSegment("2_"+"CD03").getImporte():0);
//		if(amount != 0){
//			EDT edt = emp.getEdtSegment("4_"+"TT20");
//			edt.setTipoElemento("TT");
//			edt.setClave(20);
//			edt.setCalificadorClave(null);
//			edt.setBase(null);
//			edt.setIndicadorFactorTipo(" ");
//			edt.setParteEnteraTipo(0);
//			edt.setParteDecimalFactorTipo(0);
//			edt.setImporte(amount);
//			edt.setSigno(amount<0?"-":" ");
//		}
//	}
//	private void createEDTTt10Segment(Contract c, EMP emp) {
//		// TODO 10 Liquido contingencias generales
//		Integer amount = (emp.getEdt().containsKey("3_"+"CA01")&&emp.getEdtSegment("3_"+"CA01").getImporte()!=null?emp.getEdtSegment("3_"+"CA01").getImporte():0) 
//			+ (emp.getEdt().containsKey("3_"+"CA02")?emp.getEdtSegment("3_"+"CA02").getImporte():0)
//			+ (emp.getEdt().containsKey("3_"+"CA11")?emp.getEdtSegment("3_"+"CA11").getImporte():0)
//			+ (emp.getEdt().containsKey("3_"+"CA12")?emp.getEdtSegment("3_"+"CA12").getImporte():0)
//			- (emp.getEdt().containsKey("3_"+"CA20")?emp.getEdtSegment("3_"+"CA20").getImporte():0)
//			- (emp.getEdt().containsKey("3_"+"CA21")?emp.getEdtSegment("3_"+"CA21").getImporte():0)
//			- (emp.getEdt().containsKey("3_"+"CA22")?emp.getEdtSegment("3_"+"CA22").getImporte():0)
//			- (emp.getEdt().containsKey("1_"+"BA10")?emp.getEdtSegment("1_"+"BA10").getImporte():0)
//			- (emp.getEdt().containsKey("1_"+"BA11")?emp.getEdtSegment("1_"+"BA11").getImporte():0);
//		if(amount != 0){
//			EDT edt = emp.getEdtSegment("4_"+"TT10");
//			edt.setTipoElemento("TT");
//			edt.setClave(10);
//			edt.setCalificadorClave(null);
//			edt.setBase(0);
//			edt.setIndicadorFactorTipo(null);
//			edt.setParteEnteraTipo(0);
//			edt.setParteDecimalFactorTipo(0);
//			edt.setImporte(amount);
//			edt.setSigno(amount<0?"-":" ");
//		}
//	}
//	private void createEDTCa90Segment(Contract c, EMP emp) {
//		// TODO 90 Recargo de mora
//		
//	}
//	private void createEDTCa80Segment(Contract c, EMP emp) {
//		// TODO 80 Bonificación INEM formación continua
//		
//	}
//	private void createEDTCa60Segment(Contract c, EMP emp) {
//		// TODO 60 Suma de bonificaciones, subvenciones y compensaciones
//		Integer amount = 0;
//		amount += emp.getEdt().containsKey("2_"+"CD07")?emp.getEdtSegment("2_"+"CD07").getImporte():0;
//		amount += emp.getEdt().containsKey("2_"+"CD10")?emp.getEdtSegment("2_"+"CD10").getImporte():0;
//		amount += emp.getEdt().containsKey("2_"+"CD11")?emp.getEdtSegment("2_"+"CD11").getImporte():0;
//		amount += emp.getEdt().containsKey("2_"+"CD13")?emp.getEdtSegment("2_"+"CD13").getImporte():0;
//		amount += emp.getEdt().containsKey("2_"+"CD16")?emp.getEdtSegment("2_"+"CD16").getImporte():0;
//		amount += emp.getEdt().containsKey("2_"+"CD20")?emp.getEdtSegment("2_"+"CD20").getImporte():0;
//		amount += emp.getEdt().containsKey("2_"+"CD21")?emp.getEdtSegment("2_"+"CD21").getImporte():0;
//		amount += emp.getEdt().containsKey("2_"+"CD22")?emp.getEdtSegment("2_"+"CD22").getImporte():0;
//		amount += emp.getEdt().containsKey("2_"+"CD23")?emp.getEdtSegment("2_"+"CD23").getImporte():0;
//		amount += emp.getEdt().containsKey("2_"+"CD25")?emp.getEdtSegment("2_"+"CD25").getImporte():0;
//		amount += emp.getEdt().containsKey("3_"+"CA28")?emp.getEdtSegment("3_"+"CA28").getImporte():0;
//		amount += emp.getEdt().containsKey("3_"+"CA80")?emp.getEdtSegment("3_"+"CA80").getImporte():0;
//		if(amount != 0){
//			EDT edt = emp.getEdtSegment("3_"+"CA60");
//			edt.setTipoElemento("CA");
//			edt.setClave(60);
//			edt.setCalificadorClave(null);
//			edt.setBase(null);
////	edt.setIndicadorFactorTipo("T");
////	edt.setParteEnteraTipo(28);
////	edt.setParteDecimalFactorTipo(03);
////	edt.setImporte(edt.getBase()*28);
//			edt.setImporte(amount);
//			edt.setSigno(" ");
//		}
//	}
//	private void createEDTCa57Segment(Contract c, EMP emp) {
//		// TODO 57 Cuota empresarial por Otras Cotizaciones
//		
//	}
//	private void createEDTCa56Segment(Contract contract, EMP emp) {
//		// TODO 56 Total Otras Cotizaciones cuota empresarial (TC1/16) Régimen Especial del Mar
//		if(contract.getRegimeType() == SSRegimeType.SEA_WORKERS){
//			
//		} 
//	}
//	private void createEDTCa55Segment(Contract contract, EMP emp) {
//		// TODO 55 Cotización empresarial por Fogasa y FP (TC1/16) Régimen Especial del Mar
//		if(contract.getRegimeType() == SSRegimeType.SEA_WORKERS){
//			
//		} 
//	}
//	private void createEDTCa54Segment(Contract contract, EMP emp) {
//		// TODO 54 Cotización empresarial por desempleo ( TC1/16) - Régimen Especial del Mar
//		// Cotización empresarial por Desempleo y Formación Profesional (TC1/25) - Sistema Especial de Manipulado y Empaquetado de Tomate Fresco
//		if(contract.getRegimeType() == SSRegimeType.SEA_WORKERS){
//			
//		} 
////		else if(contract.getRegimeType() == SSRegimeType.TOMATO_MANIPULATION){
////			
////		}
//	}
//	private void createEDTCa53Segment(Contract contract, EMP emp) {
//		// TODO 53 Total Otras Cotizaciones (Desempleo, FOGASA y Formación Profesional) (TC1/16) - Régimen Especial del Mar
//		// Total Otras Cotizaciones (Desempleo, FOGASA y Formación Profesional (TC1/25) - Sistema Especial de Manipulado y Empaquetado de Tomate Fresco
//		if(contract.getRegimeType() == SSRegimeType.SEA_WORKERS){
//			
//		} 
////		else if(contract.getRegimeType() == SSRegimeType.TOMATO_MANIPULATION){
////			
////		}
//	}
//	private void createEDTCa52Segment(Contract contract, EMP emp) {
//		// TODO 52 Otras cotizaciones (FOGASA y Formación Profesional) (TC1/16) - Régimen Especial del Mar 
//		// Otras Cotizaciones (FOGASA) (TC1/25) - Sistema Especial de Manipulado y Empaquetado de Tomate Fresco
//		if(contract.getRegimeType() == SSRegimeType.SEA_WORKERS){
//			
//		} 
////		else if(contract.getRegimeType() == SSRegimeType.TOMATO_MANIPULATION){
////			
////		}
//	}
//	private void createEDTCa51Segment(Contract contract, EMP emp) {
//		// TODO 51 Otras cotizaciones (Desempleo) (Tc1/16) - Régimen Especial del Mar
//		// Otra cotizaciones (Desempleo y Formación Profesional cuota obrera) (TC1/25) - Sistema Especial de Manipulado y Empaquetado de Tomate Fresco
//		if(contract.getRegimeType() == SSRegimeType.SEA_WORKERS){
//			
//		} 
////		else if(contract.getRegimeType() == SSRegimeType.TOMATO_MANIPULATION){
////			
////		}
//	}
//	private void createEDTCa50Segment(Contract contract, EMP emp) {
//		// TODO 50 Otras cotizaciones (Desempleo, FOGASA y Formación Profesional)
//		// Resto de regímenes excepto Régimen Especial del Mar, Régimen Especial de Manipulado y Empaquetado y Tomate Fresco y Régimen Especial Agrario (0613).
//		if(contract.getRegimeType() != SSRegimeType.SEA_WORKERS 
////				&& contract.getRegimeType() != SSRegimeType.TOMATO_MANIPULATION 
//				&& contract.getRegimeType() != SSRegimeType.AGRICULTURAL){
//			Integer base = emp.getEdt().containsKey("1_"+"BA02")?emp.getEdtSegment("1_"+"BA02").getBase():0;
//			if(base != 0){
//				EDT edt = emp.getEdtSegment("3_"+"CA50");
//				edt.setTipoElemento("CA");
//				edt.setClave(50);
//				edt.setBase(base);
//				edt.setImporte(new Double((edt.getBase())*0.283).intValue());
//			}
//		}
//	}
//	private void createEDTCa32Segment(Contract c, EMP emp) {
//		// TODO 32 Cuotas por Invalidez, muerte y supervivencia (IMS) por AT y EP
//		Integer amount = 0;
//		// TODO suma cuotas trabajadores segun epigrafes
////		amount += currentEMP.getEdt().containsKey("3_"+"CA31")?currentEMP.getEdtSegment("3_"+"CA31").getBase():0;
////		amount += currentEMP.getEdt().containsKey("3_"+"CA31")?currentEMP.getEdtSegment("3_"+"CA31").getBase():0;
//		if(amount != 0){
//			EDT edt = emp.getEdtSegment("3_"+"CA32");
//			edt.setTipoElemento("CA");
//			edt.setClave(32);
//			edt.setImporte(amount);
//			edt.setSigno(" ");
//		}
//	}
//	private void createEDTCa31Segment(Contract c, EMP emp) {
//		// TODO 31 Cuotas por Incapacidad Temporal por AT y EP
//		Integer amount = 0;
//		// TODO suma cuotas trabajadores segun epigrafes
////		amount += currentEMP.getEdt().containsKey("3_"+"CA31")?currentEMP.getEdtSegment("3_"+"CA31").getBase():0;
////		amount += currentEMP.getEdt().containsKey("3_"+"CA31")?currentEMP.getEdtSegment("3_"+"CA31").getBase():0;
//		if(amount != 0){
//			EDT edt = emp.getEdtSegment("3_"+"CA31");
//			edt.setTipoElemento("CA");
//			edt.setClave(31);
//			edt.setCalificadorClave(null);
//			edt.setBase(null);
//			edt.setImporte(amount);
//			edt.setSigno(" ");
//		}
//	}
//	private void createEDTCa30Segment(Contract contract, EMP emp) {
//		// TODO 30 Total cuotas AT y EP
//		Integer amount = 0;
//		amount += emp.getEdt().containsKey("3_"+"CA31")?emp.getEdtSegment("3_"+"CA31").getImporte():0;
//		amount += emp.getEdt().containsKey("3_"+"CA31")?emp.getEdtSegment("3_"+"CA31").getImporte():0;
//		if(amount != 0){
//			EDT edt = emp.getEdtSegment("3_"+"CA30");
//			edt.setTipoElemento("CA");
//			edt.setClave(30);
//			edt.setCalificadorClave(null);
//			edt.setBase(null);
//			edt.setImporte(amount);
//			edt.setSigno(" ");
//		}
//	}
//	private void createEDTCa22Segment(Contract c, EMP emp) {
//		// TODO 22 Suma de compensaciones y reducciones
//		
//	}
//	private void createEDTCa21Segment(Contract contract, EMP emp) {
//		// TODO 21 Deducción colaboración voluntaria enfermedades comunes y accidente no laboral
//		// No es de aplicación para el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
//		if(contract.getRegimeType() != SSRegimeType.ARTIST && contract.getRegimeType() != SSRegimeType.AGRICULTURAL){
//			
//		}
//	}
//	private void createEDTCa20Segment(Contract contract, EMP emp) {
//		// TODO 20 Deducción por contingencias excluidas 
//		// No es de aplicación para el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
//		if(contract.getRegimeType() != SSRegimeType.ARTIST && contract.getRegimeType() != SSRegimeType.AGRICULTURAL){
//			
//		}
//	}
//	private void createEDTCa12Segment(Contract contract, EMP emp) {
//		// TODO 12 Aportación a los servicios comunes 
//		// No es de aplicación en el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
//		if(contract.getRegimeType() != SSRegimeType.ARTIST && contract.getRegimeType() != SSRegimeType.AGRICULTURAL){
//			
//		}
//	}
//	private void createEDTCa11Segment(Contract c, EMP emp) {
//		// TODO 11 Otros conceptos
//		
//		// calificador de clave
////		4 Asistencia sanitaria de Administraciones Públicas
////		8 Cotización adicional Ex.-MUNPAL
////		6 Contratación inferior a 7 días
////		Para contratos de duración efectiva inferior de 7 días, a los que es de aplicación el incremento del 36% de la cotización empresarial por contingencias comunes, establecido en la Ley 12/2001
////		14 Cotización adicional Ex-Munpal y contratación inferior a 7 días
////		Se utilizará cuando coincida la cotización adicional por clave 8 y por clave 6
////		15 Cotización adicional Bomberos al servicio de las Administraciones y Organismos Públicos
//		
//	}
//	private void createEDTCa03Segment(Contract c, EMP emp) {
//		// 03 Cuota fija trabajador cuenta ajena extranjero (Baja a partir del 1 de enero de 2009) Es de aplicación solo para el Régimen Especial Agrario
//	}
//	private void createEDTCa02Segment(Contract contract, EMP emp) {
//		// TODO 02 Cuota empresarial por Contingencias Comunes
//		// Cotización empresarial / Toneladas Régimen Especial de Manipulado y Empaquetado de Tomate Fresco (0134)
////		if(contract.getRegimeType() == SSRegimeType.TOMATO_MANIPULATION){
////			
////		}
//	}
//	private void createEDTCa01Segment(Contract c, EMP emp) {
//		// TODO 01 Contingencias Comunes
//		Integer base = emp.getEdt().containsKey("1_"+"BA01")?emp.getEdtSegment("1_"+"BA01").getBase():0;
//		if(base != 0){
//			EDT edt = emp.getEdtSegment("3_"+"CA01");
//			edt.setTipoElemento("CA");
//			edt.setClave(1);
//			edt.setCalificadorClave(null);
//			edt.setBase(base);
////	edt.setIndicadorFactorTipo("T");
////	edt.setParteEnteraTipo(28);
////	edt.setParteDecimalFactorTipo(03);
//			edt.setImporte(new Double((edt.getBase())*0.283).intValue());
//			edt.setSigno(" ");
//		}
//	}
//	
//	
//	
//	private void createEDTCd30Segment(Contract contract, EMP emp) { 
//		// TODO 30 Reducciones. SEA Desempleo Sistema Especial Agrario
//		if(contract.getRegimeType() == SSRegimeType.AGRICULTURAL){
//			
//		}
//	}
//	private void createEDTCd29Segment(Contract contract, EMP emp) {
//		// TODO 29 Reducciones SEA. Contingencias comunes Sistema Especial Agrario 
//		if(contract.getRegimeType() == SSRegimeType.AGRICULTURAL){
//			
//		}
//	}
//	private void createEDTCd28Segment(Contract c, EMP emp) {
//		// 28 Bonificación por ERE 
//		Integer amount = 0;
//		for(String key: emp.getTrabajadores().keySet()){
//			amount += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("CD28")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("CD28").getImporte():0;
//		}
//		if(amount != 0){
//			EDT edt = emp.getEdtSegment("2_"+"CD28");
//			edt.setTipoElemento("CD");
//			edt.setClave(28);
//			edt.setImporte(amount);	
//		}
//	}
//	private void createEDTCd27Segment(Contract c, EMP emp) {
//		// 27 Reducciones REA "Jornadas reales". (Baja a partir del 1 de enero de 2012) Régimen Especial Agrario 
//	}
//	private void createEDTCd26Segment(Contract c, EMP emp) {
//		// 26 Reducciones REA Cuantía mensual (modalidad G y J). (Baja a partir del 1 de enero de 2012) Régimen Especial Agrario 
//	}
//	private void createEDTCd25Segment(Contract contract, EMP emp) {
//		// 25 Exención de desempleo hijos<30años Autonomos
//		if(contract.getRegimeType() == SSRegimeType.SELF_EMPLOYED){
//			Integer amount = 0;
//			for(String key: emp.getTrabajadores().keySet()){
//				amount += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("CD25")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("CD25").getImporte():0;
//			}
//			if(amount != 0){
//				EDT edt = emp.getEdtSegment("2_"+"CD25");
//				edt.setTipoElemento("CD");
//				edt.setClave(25);
//				edt.setImporte(amount);
//			}
//		}
//	}
//	private void createEDTCd24Segment(Contract contract, EMP emp) {
//		// 24 Bonificación I+D+I Régimen General (0111)
//		if(contract.getRegimeType() == SSRegimeType.GENERAL){
//			Integer amount = 0;
//			for(String key: emp.getTrabajadores().keySet()){
//				amount += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("CD24")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("CD24").getImporte():0;
//			}
//			if(amount != 0){
//				EDT edt = emp.getEdtSegment("2_"+"CD24");
//				edt.setTipoElemento("CD");
//				edt.setClave(24);
//				edt.setImporte(amount);
//			}
//		}
//	}
//	private void createEDTCd23Segment(Contract c, EMP emp) {
//		// 23 Bonificación Sector industrial incentivado
//		Integer amount = 0;
//		for(String key: emp.getTrabajadores().keySet()){
//			amount += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("CD23")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("CD23").getImporte():0;
//		}
//		if(amount != 0){
//			EDT edt = emp.getEdtSegment("2_"+"CD23");
//			edt.setTipoElemento("CD");
//			edt.setClave(23);
//			edt.setImporte(amount);
//		}
//	}
//	private void createEDTCd22Segment(Contract contract, EMP emp) {
//		// 22 Bonificación Form. Empleo Cuantía fija. Excepto Rég. Gral. Artistas (0112)
//		if(contract.getRegimeType() != SSRegimeType.ARTIST){
//			Integer amount = 0;
//			for(String key: emp.getTrabajadores().keySet()){
//				amount += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("CD22")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("CD22").getImporte():0;
//			}
//			if(amount != 0){
//				EDT edt = emp.getEdtSegment("2_"+"CD22");
//				edt.setTipoElemento("CD");
//				edt.setClave(22);
//				edt.setImporte(amount);
//			}
//		}
//	}
//	private void createEDTCd21Segment(Contract c, EMP emp) {
//		// 21 Bonificación Copa del America (R.D.L. 2146/2004)
//		Integer amount = 0;
//		for(String key: emp.getTrabajadores().keySet()){
//			amount += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("CD21")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("CD21").getImporte():0;
//		}
//		if(amount != 0){
//			EDT edt = emp.getEdtSegment("2_"+"CD21");
//			edt.setTipoElemento("CD");
//			edt.setClave(21);
//			edt.setImporte(amount);
//		}
//	}
//	private void createEDTCd20Segment(Contract c, EMP emp) {
//		// 20 Bonificación Ceuta y Melilla (O. TAS/471/2004)
//		Integer amount = 0;
//		for(String key: emp.getTrabajadores().keySet()){
//			amount += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("CD20")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("CD20").getImporte():0;
//		}
//		if(amount != 0){
//			EDT edt = emp.getEdtSegment("2_"+"CD20");
//			edt.setTipoElemento("CD");
//			edt.setClave(20);
//			edt.setImporte(amount);
//		}
//	}
//	private void createEDTCd18Segment(Contract c, EMP emp) {
//		// 18 Reducción por Exencón de desempleo (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario)
//	}
//	private void createEDTCd17Segment(Contract contract, EMP emp) {
//		// 17 Reducción contingencias comunes excepto I.T. (R.D.L. 16/2001)
//		// No es de aplicación en el Régimen Especial Agrario (0613)
//		if(contract.getRegimeType() != SSRegimeType.AGRICULTURAL){
//			Integer amount = 0;
//			for(String key: emp.getTrabajadores().keySet()){
//				amount += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("CD17")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("CD17").getImporte():0;
//			}
//			if(amount != 0){
//				EDT edt = emp.getEdtSegment("2_"+"CD17");
//				edt.setTipoElemento("CD");
//				edt.setClave(17);
//				edt.setImporte(amount);
//			}
//		}
//	}
//	private void createEDTCd16Segment(Contract c, EMP emp) {
//		// 16 Bonificación por trabajadores con 60 o más años
//		Integer amount = 0;
//		for(String key: emp.getTrabajadores().keySet()){
//			amount += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("CD16")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("CD16").getImporte():0;
//		}
//		if(amount != 0){
//			EDT edt = emp.getEdtSegment("2_"+"CD16");
//			edt.setTipoElemento("CD");
//			edt.setClave(16);
//			edt.setImporte(amount);
//		}
//	}
//	private void createEDTCd13Segment(Contract c, EMP emp) {
//		// 13 Bonificación minusvalidos en Centros Especiales de Empleo
//		Integer amount = 0;
//		for(String key: emp.getTrabajadores().keySet()){
//			amount += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("CD13")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("CD13").getImporte():0;
//		}
//		if(amount != 0){
//			EDT edt = emp.getEdtSegment("2_"+"CD13");
//			edt.setTipoElemento("CD");
//			edt.setClave(13);
//			edt.setImporte(amount);
//		}
//	}
//	private void createEDTCd12Segment(Contract contract, EMP emp) {
//		// TODO 12 Bonificación por Ley 19/94 (Registro Canario) Régimen Especial del Mar
//		
//	}
//	private void createEDTCd11Segment(Contract c, EMP emp) {
//		// 11 Bonificación por formación teórica a distancia
//		Integer amount = 0;
//		for(String key: emp.getTrabajadores().keySet()){
//			amount += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("CD11")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("CD11").getImporte():0;
//		}
//		if(amount != 0){
//			EDT edt = emp.getEdtSegment("2_"+"CD11");
//			edt.setTipoElemento("CD");
//			edt.setClave(11);
//			edt.setImporte(amount);
//		}
//	}
//	private void createEDTCd10Segment(Contract c, EMP emp) {
//		// 10 Bonificación por formación teórica presencial
//		Integer amount = 0;
//		for(String key: emp.getTrabajadores().keySet()){
//			amount += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("CD10")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("CD10").getImporte():0;
//		}
//		if(amount != 0){
//			EDT edt = emp.getEdtSegment("2_"+"CD10");
//			edt.setTipoElemento("CD");
//			edt.setClave(10);
//			edt.setImporte(amount);
//		}
//	}
//	private void createEDTCd07Segment(Contract contract, EMP emp) {
//		// 07 Bonificaciones
//		Integer amount = 0;
//		for(String key: emp.getTrabajadores().keySet()){
//			amount += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("CD07")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("CD07").getImporte():0;
//		}
//		if(amount != 0){
//			EDT edt = emp.getEdtSegment("2_"+"CD07");
//			edt.setTipoElemento("CD");
//			edt.setClave(7);
//			edt.setImporte(amount);
//		}
//	}
//	
//	private void createEDTCd06Segment(Contract contract, EMP emp) {
//		// 06 Reducciones
//		Integer amount = 0;
//		for(String key: emp.getTrabajadores().keySet()){
//			amount += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("CD06")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("CD06").getImporte():0;
//		}	
//		if(amount != 0){
//			EDT edt = emp.getEdtSegment("2_"+"CD06");
//			edt.setTipoElemento("CD");
//			edt.setClave(6);
//			edt.setImporte(amount);
//		}
//	}
//	private void createEDTCd05Segment(Contract contract, EMP emp) {
//		// TODO 5 IT O.M. 3/4/73. Minería del Carbón
//		if(contract.getRegimeType() == SSRegimeType.COAL_MINING){
//			
//		}
//	}
//	private void createEDTCd03Segment(Contract contract, EMP emp) {
//		// 03 IT por AT y EP
//		// No es de aplicación en el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
//		if(contract.getRegimeType() != SSRegimeType.ARTIST && contract.getRegimeType() != SSRegimeType.COAL_MINING){
//			Integer amount = 0;
//			for(String key: emp.getTrabajadores().keySet()){
//				amount += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("CD03")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("CD03").getImporte():0;
//			}	
//			if(amount != 0){
//				EDT edt = emp.getEdtSegment("2_"+"CD03");
//				edt.setTipoElemento("CD");
//				edt.setClave(3);
//				edt.setImporte(amount);
//			}
//		}
//	}
//	private void createEDTCd01Segment(Contract contract, EMP emp) {
//		// 01 IT enfermedad común y accidente no laboral
//		// No es de aplicación en el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
//		if(contract.getRegimeType() != SSRegimeType.ARTIST && contract.getRegimeType() != SSRegimeType.COAL_MINING){
//			Integer amount = 0;
//			for(String key: emp.getTrabajadores().keySet()){
//				amount += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("CD01")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("CD01").getImporte():0;
//			}	
//			if(amount != 0){
//				EDT edt = emp.getEdtSegment("2_"+"CD01");
//				edt.setTipoElemento("CD");
//				edt.setClave(1);
//				edt.setImporte(amount);
//			}
//		}
//	}
//	
//	
//	private void createEDTBa42Segment(Contract contract, EMP emp) {
//		// 42 Base exclusiva de AT y EP sin cotización de Otras Cotizaciones
//		Integer base = 0;
//		for(String key: emp.getTrabajadores().keySet()){
//			base += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("BA42")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("BA42").getImporte():0;
//		}	
//		if(base != 0){
//			EDT edt = emp.getEdtSegment("1_"+"BA42");
//			edt.setTipoElemento("BA");
//			edt.setClave(42);
//			edt.setBase(base);
//		}
//	}
//	private void createEDTBa41Segment(Contract c, EMP emp) {
//		// TODO 41 Contingencias Comunes y FOGASA(Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
//	}
//	private void createEDTBa38Segment(Contract c, EMP emp) {
//		// TODO 38 Cotización exclusivamente por FOGASA(Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario) 
//	}
//	private void createEDTBa37Segment(Contract c, EMP emp) {
//		// TODO 37 Cotización jornadas reales y FOGASA excluido desempleo(Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario) 
//	}
//	private void createEDTBa36Segment(Contract c, EMP emp) {
//		// TODO 36 Base exclusiva Desempleo/FOGASA tipo total (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario) 
//	}
//	private void createEDTBa35Segment(Contract c, EMP emp) {
//		// TODO 35 Base fija cotización trabajadores cuenta ajena extranjeros (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario) 
//	}
//	private void createEDTBa34Segment(Contract c, EMP emp) {
//		// TODO 34 Base de AT en vacaciones (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
//	}
//	private void createEDTBa33Segment(Contract c, EMP emp) {
//		// TODO 33 Base de cotización exclusiva de Otras Cotizaciones en situación de IT/maternidad/riesgo durante el embarazo. 
//		// Trabajadores no fijos (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario) 
//	}
//	private void createEDTBa32Segment(Contract c, EMP emp) {
//		// TODO 32 Base de cotización Jornadas Reales en situación de IT por desempleo empresarial y FOGASA. 
//		// (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
//	}
//	private void createEDTBa31Segment(Contract c, EMP emp) {
//		// TODO 31 Contingencias comunes trajadores no fijos (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario) 
//	}
//	private void createEDTBa30Segment(Contract c, EMP emp) {
//		// TODO 30 Cotización por Jornadas Reales (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
//	}
//	private void createEDTBa28Segment(Contract c, EMP emp) {
//		// TODO 28 Diferencia en bases (contingencias comunes y salario normalizado), cotización exclusivamente empresarial
//		// Solo para liquidaciones anteriores al año 2002 del Régimen Especial de la Minería del Carbón (0911). Baja a partir de 2002 
//	}
//	private void createEDTBa23Segment(Contract c, EMP emp) {
//		// TODO 23 Base de cotización tipo total desempleo y FOGASA(Baja a partir del 1 de enero de 2012) . (Régimen Especial Agrario)
//	}
//	private void createEDTBa22Segment(Contract contract, EMP emp) {
//		// 22 Base de cotización empresarial por AT y EP y Otras Cotizaciones
//		Integer base = 0;
//		for(String key: emp.getTrabajadores().keySet()){
//			base += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("BA20")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("BA20").getImporte():0;
//			base += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("BA22")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("BA22").getImporte():0;
//		}	
//		if(base != 0){
//			EDT edt = emp.getEdtSegment("1_"+"BA22");
//			edt.setTipoElemento("BA");
//			edt.setClave(22);
//			edt.setBase(base);
//		}
//	}
//	private void createEDTBa21Segment(Contract contract, EMP emp) {
//		// 21 Base de cotización empresarial por contingencias comunes
//		// Base de cotización empresarial desempleo y FOGASA (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
//		if(contract.getRegimeType() != SSRegimeType.AGRICULTURAL && CommonUtil.getYear(getStartDate()) < 2012) {
//			Integer base = 0;
//			for(String key: emp.getTrabajadores().keySet()){
//				base += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("BA20")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("BA20").getImporte():0;
//				base += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("BA21")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("BA21").getImporte():0;
//			}	
//			if(base != 0){
//				EDT edt = emp.getEdtSegment("1_"+"BA21");
//				edt.setTipoElemento("BA");
//				edt.setClave(21);
//				edt.setBase(base);
//			}
//		}
//	}
//	private void createEDTBa11Segment(Contract contract, EMP emp) {
//		// TODO enterpriseNonStructural && employeeNonStructural 
//		// 11 Horas extras no estructurales / Otras horas extras desde 1/1/98
//		// No se podrá utilizar para e Régimen General de Artistas (0112) , ni Régimen Especial de Minería del Carbón (0911)
//		if(contract.getRegimeType() != SSRegimeType.ARTIST && contract.getRegimeType() != SSRegimeType.COAL_MINING){
//			Double enterpriseNonStructural = 23.60;
//			Double employeeNonStructural = 4.70;
//			Integer base = 0;
//			for(String key: emp.getTrabajadores().keySet()){
//				base += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("BA11")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("BA11").getImporte():0;
//			}	
//			if(base != 0){
//				Integer amount = (int)(CommonUtil.round(((new Double(base))/100)*((enterpriseNonStructural + employeeNonStructural)/100))*100);
//				EDT edt = emp.getEdtSegment("1_"+"BA11");
//				edt.setTipoElemento("BA");
//				edt.setClave(11);
//				edt.setBase(base);
//				edt.setIndicadorFactorTipo("T");
//				edt.setParteEnteraTipo((int)(enterpriseNonStructural + employeeNonStructural));
//				edt.setParteDecimalFactorTipo((int)(((enterpriseNonStructural + employeeNonStructural)-(int)(enterpriseNonStructural + employeeNonStructural))*100));
//				edt.setImporte(amount);
//			}
//		}
//	}
//	private void createEDTBa10Segment(Contract contract, EMP emp) {
//		// TODO enterpriseNonStructural && employeeNonStructural 
//		// 10 Horas extras estructurales / Causa de fuerza mayor desde 1/1/98
//		// No se podrá utilizar para el Régimen General de Artistas (0112), ni Régimen Especial de Minería del Carbón (0911).
//		if(contract.getRegimeType() != SSRegimeType.ARTIST && contract.getRegimeType() != SSRegimeType.COAL_MINING){
//			Double enterpriseNonStructural = 12.00;
//			Double employeeNonStructural = 2.00;
//			Integer base = 0;
//			for(String key: emp.getTrabajadores().keySet()){
//				base += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("BA10")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("BA10").getImporte():0;
//			}	
//			if(base != 0){
//				Integer amount = (int)(CommonUtil.round(((new Double(base))/100)*((enterpriseNonStructural + employeeNonStructural)/100))*100);
//				EDT edt = emp.getEdtSegment("1_"+"BA10");
//				edt.setTipoElemento("BA");
//				edt.setClave(10);
//				edt.setBase(base);
//				edt.setIndicadorFactorTipo("T");
//				edt.setParteEnteraTipo((int)(enterpriseNonStructural + employeeNonStructural));
//				edt.setParteDecimalFactorTipo((int)(((enterpriseNonStructural + employeeNonStructural)-(int)(enterpriseNonStructural + employeeNonStructural))*100));
//				edt.setImporte(amount);
//			}
//		}
//	}
//	private void createEDTBa09Segment(Contract contract, EMP emp) {
//		// 09 Horas complementarias No se utilizará para Régimen General de Artistas (0112)
//		if(contract.getRegimeType() != SSRegimeType.ARTIST){
//			Integer base = 0;
//			for(String key: emp.getTrabajadores().keySet()){
//				base += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("BA09")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("BA09").getImporte():0;
//			}	
//			if(base != 0){
//				EDT edt = emp.getEdtSegment("1_"+"BA09");
//				edt.setTipoElemento("BA");
//				edt.setClave(9);
//				edt.setBase(base);
//			}
//		}
//	}
//	private void createEDTBa08Segment(Contract c, EMP emp) {
//		// 8 Diferencia Bases (Contingencias comunes y salario normalizado)
//		// Solo para liquidaciones anteriores al año 2002 del Régimen Especial de la Minería del Carbón (0911). Baja a partir de 2002
//	}
//	private void createEDTBa07Segment(Contract c, EMP emp) {
//		// 7 AT y EP sin horas extraordinarias
//		// No será de utilización para Régimen General de Artistas (0112), ni Régimen Especial de Minería del Carbón (0911). Baja a partir de 2002
//	}
//	private void createEDTBa06Segment(Contract contract, EMP emp) {
//		// TODO 6 Importe percepciones Integras (Artistas)
//		if(contract.getRegimeType() == SSRegimeType.ARTIST){
//			
//		}
//	}
//	private void createEDTBa05Segment(Contract contract, EMP emp) {
//		// TODO 5 Exceso del tope (Minería del Carbón)
//		if(contract.getRegimeType() == SSRegimeType.COAL_MINING){
//			
//		}
//	}
//	private void createEDTBa02Segment(Contract contract, EMP emp) {
//		// 02 AT y EP
//		Integer base = 0;
//		for(String key: emp.getTrabajadores().keySet()){
//			base += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("BA00")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("BA00").getImporte():0;
//			base += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("BA02")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("BA02").getImporte():0;
//		}
//		if(base != 0){
//			EDT edt = emp.getEdtSegment("1_"+"BA02");
//			edt.setTipoElemento("BA");
//			edt.setClave(2);
////		edt.setCalificadorClave(null);
//			edt.setBase(base);
////		edt.setIndicadorFactorTipo(null);
////		edt.setParteEnteraTipo(0);
////		edt.setParteDecimalFactorTipo(0);
////		edt.setImporte(null);
////		edt.setSigno(" ");
//		}
//	}
//	private void createEDTBa01Segment(Contract c, EMP emp) {
//		// 01 Contingencias comunes
//		Integer base = 0;
//		for(String key: emp.getTrabajadores().keySet()){
//			base += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("BA00")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("BA00").getImporte():0;
//			base += emp.getTraSegment(key).getDat().get(0).getEdl().containsKey("BA01")?emp.getTraSegment(key).getDat().get(0).getEdlSegment("BA01").getImporte():0;
//		}
//		if(base != 0){
//			EDT edt = emp.getEdtSegment("1_"+"BA01");
//			edt.setTipoElemento("BA");
//			edt.setClave(1);
////		edt.setCalificadorClave(null);
//			edt.setBase(base);
////		edt.setIndicadorFactorTipo(null);
////		edt.setParteEnteraTipo(0);
////		edt.setParteDecimalFactorTipo(0);
////		edt.setImporte(null);
////		edt.setSigno(" ");
//		}
//	}
//	
//	
//	private Salary getContractSalary(EnterpriseCCC ccc) throws ManagerBeanException {
//		// TODO Auto-generated method stub
//		Calendar startCal = Calendar.getInstance();
//		startCal.set(year, startMonth.ordinal(), 1);
//		Calendar endCal = Calendar.getInstance();
//		endCal.set(year, startMonth.ordinal(), endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
//		
//		List<ISalary> list;
//		Criteria criteria = new Criteria();
//		IManagerBean bean = BeanManager.getManagerBean(Salary.class);
//		String alias = bean.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ENTERPRISE_ID);
//		criteria.addEqualExpression(alias, ccc.getActivity().getEnterprise().getId());
//		alias = bean.getFieldName(IEntityAlias.SALARY_END_DATE);
//		criteria.addGreaterThanOrEqualExpression(alias, startCal.getTime());
//		alias = bean.getFieldName(IEntityAlias.SALARY_END_DATE);
//		criteria.addLessThanOrEqualExpression(alias, endCal.getTime());
//		criteria.addOrder(bean.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ID));
//		criteria.addOrder(bean.getFieldName(IEntityAlias.SALARY_EMPLOYEE_NAME));
//		list = new LinkedList<ISalary>();
////		Double total = new Double(0);
////		for( ITransferObject to : bean.getList(criteria) ) {
////			Salary s = (Salary) to;
//////			list.add(s);
////			total += s.getRawCommonBase();
////		}	
////		return (new Double(CommonUtil.round(total)*10)).intValue();
//		return (Salary) bean.getList(criteria).get(0);
//	}
//	private Integer getEnterpriseAmount(EnterpriseCCC ccc) throws ManagerBeanException {
//		// TODO Auto-generated method stub
//		Calendar startCal = Calendar.getInstance();
//		startCal.set(year, startMonth.ordinal(), 1);
//		Calendar endCal = Calendar.getInstance();
//		endCal.set(year, startMonth.ordinal(), endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
//		
//		List<ISalary> list;
//		Criteria criteria = new Criteria();
//		IManagerBean bean = BeanManager.getManagerBean(Salary.class);
//		String alias = bean.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ENTERPRISE_ID);
//		criteria.addEqualExpression(alias, ccc.getActivity().getEnterprise().getId());
//		alias = bean.getFieldName(IEntityAlias.SALARY_END_DATE);
//		criteria.addGreaterThanOrEqualExpression(alias, startCal.getTime());
//		alias = bean.getFieldName(IEntityAlias.SALARY_END_DATE);
//		criteria.addLessThanOrEqualExpression(alias, endCal.getTime());
//		criteria.addOrder(bean.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ID));
//		criteria.addOrder(bean.getFieldName(IEntityAlias.SALARY_EMPLOYEE_NAME));
//		list = new LinkedList<ISalary>();
//		Double total = new Double(0);
//		for( ITransferObject to : bean.getList(criteria) ) {
//			Salary s = (Salary) to;
////			list.add(s);
//			total += s.getTotalEnterprise();
//		}		
//		return (new Double(CommonUtil.round(total)*100)).intValue();
//	}
//	private MPG createMPGRecord(EnterpriseCCC ccc) {
//		// TODO
//		RegistryBank bank = getBank(ccc);
//		RegistryDirStaff dirStaff = getDirStaff(ccc);
//		if(bank==null || dirStaff==null){
//			return null;
//		}
//		MPG mpg = new MPG();
////		~ Saldo Acreedor
////		C Cargo en Cuenta
////		V Pago electrónico
//		mpg.setSolicitudModalidadPago("C");
//		mpg.setCondigoCuentaCliente(bank.getBankAccount().getValue());
//		mpg.setTipoIdentificadorTitular("1");
//		mpg.setIdentificadorTitular(autoComplete(dirStaff.getDocument(), 14, "0", true));
//		mpg.setNombreTitular(dirStaff.getName());
////		mpg.setNombreTitular(autoComplete(dirStaff.getName(), 31, " ", true));
//		return mpg;
//	}
//	
//	private RegistryBank getBank(EnterpriseCCC ccc) {
//		try {
//			IManagerBean bean = BeanManager.getManagerBean(RegistryBank.class);
//			Criteria criteria = new Criteria();
//			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_BANK_REGISTRY_ID), ccc.getActivity().getEnterprise().getRegistry().getId() );
//			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_BANK_ACTIVE), true );
//			List<ITransferObject> bankList = bean.getList(criteria);
//			if(bankList.size()>0){
//				return (RegistryBank) bankList.get(0);
//			}
//		} catch (ManagerBeanException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	private Salary getSalary(Contract c) {
//		Calendar startCal = Calendar.getInstance();
//		Calendar endCal = Calendar.getInstance();
//		startCal.set(Calendar.YEAR, year);
//		startCal.set(Calendar.MONTH, endMonth.ordinal());
//		startCal.set(Calendar.DAY_OF_MONTH, 1);
//		endCal.set(Calendar.YEAR, year);
//		endCal.set(Calendar.MONTH, endMonth.ordinal());
//		endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
//		Salary salary = null;
//		try {
//			IManagerBean bean = BeanManager.getManagerBean(Salary.class);
//			Criteria criteria = new Criteria();
//			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_CONTRACT_ID), c.getId());
//			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_TYPE), SalaryType.SALARY);
//			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.SALARY_ISSUE_DATE), startCal.getTime());
//			criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.SALARY_ISSUE_DATE), endCal.getTime());
//			List<ITransferObject> list = bean.getList(criteria);
//			if(!list.isEmpty()){
//				salary = (Salary) list.get(0);
//			}
//		} catch (ManagerBeanException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return salary;
//	}
//	
////	private ISalary calculateSalary(Contract c,Integer year, Month month) {
////		try {
////			Contract contract = (Contract) getTo();
////
////			ISalaryCalculatorContext ctx;
////			ctx = contract.getSalaryCalculatorContext(
////					getYear(),
////					getMonth(),
////					getSalaryType());
////			
////			salary = ctx.getSalaryProxy().getSalary();
////			
////			( ( Salary ) salary).setContract(contract);
////			
////			paymentsModel = null;
////			paymentsList = null;
////			initializePaymentModel();
////
////			return salary;
////		} 
////		catch (OutOfDateException e) {
////			return null;
////		}catch (SalaryException e) {
////			String msg = "Error en el calculo del borrador de la nómina";
////			LOGGER.error(msg, e);
////			return null;
////		}
////	}
//	
//	private List<Enterprise> getEnterprises(List<Contract> contractList){
//		List<Enterprise> list = new LinkedList<Enterprise>();
//		for(Contract c: contractList){
//			if(!list.contains(c.getWorkPlace().getEnterprise())){
//				list.add(c.getWorkPlace().getEnterprise());
//			}
//		}
//		return list;
//	}
//	
//	private List<ITransferObject> getContracts(EnterpriseCCC ccc, Date startDate) throws ManagerBeanException{
//		IManagerBean bean = BeanManager.getManagerBean(Contract.class);
//		Criteria criteria = new Criteria();
//		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ENTERPRISE_CCC_ID), ccc.getId());
//		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_END_DATE), startDate);
//		Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_END_DATE));
//		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));			
////		criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_PERSON_REGISTRY_DOCUMENT));
//		criteria.addOrder("Contract.person.socialSecurityNumber");
//		
//		
////		criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_START_DATE), new Date());
////		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_END_DATE), new Date());
////		Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_END_DATE));
////		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));			
//		return bean.getList(criteria);
//	}
//	
//	private Map<String, String> getContractDataMap(Contract contract) {
//		Map<String, String> map = new HashMap<String, String>();
//		try {
//			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
//			Criteria criteria = new Criteria();
//			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
//			String endDateAlias = bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE);
//			Expression exp1  = ExpressionUtilities.getNotNullExpression(endDateAlias);
//			Expression exp2  = ExpressionUtilities.getGreaterThanOrEqualExpression(endDateAlias, getEndDate());
//			criteria.addExpression( ExpressionUtilities.getOrExpression(exp1, exp2) );
//			for(ITransferObject to: bean.getList(criteria)){
//				ContractData data = (ContractData) to;
//				if(data.getExpression()!=null){
//					map.put(data.getName(), data.getExpression().replace('"', ' ').trim());
//				}
//			}
//		} catch (ManagerBeanException e) {
//			// NADA, que siga generando el fichero
//		}
//		return map;
//	}
//	
//	private Integer differenceBetweenDates(Date from, Date to) {
//		Integer diffDays = new Integer(0);
//		final Double MS_PER_DAY = new Double(1000 * 60 * 60 * 24);
//		if(from.before(to)) {
//			diffDays = (int)((Math.floor((to.getTime() - from.getTime()) / MS_PER_DAY + 0.5d) + 1));
//		}
//		return diffDays;
//	}

	
	
}

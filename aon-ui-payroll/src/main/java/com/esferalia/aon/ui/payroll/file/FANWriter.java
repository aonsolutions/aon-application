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
import com.code.aon.registry.dao.IRegistryAlias;
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
import com.esferalia.aon.payroll.ContractBonus;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBonus;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.payroll.enumeration.LiquidationType;
import com.esferalia.aon.payroll.enumeration.Mutual;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.enumeration.SalaryType;

public class FANWriter {
	
	private ETI eti;
	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
	private final String  VIRGULILLA = "~";
	
	private final String  BLANK_1 = " ";
	private final String  BLANK_2 = "  ";
	private final String  BLANK_3 = "   ";
	private final String  BLANK_7 = "       ";
	private final String BLANK_15 = "               ";
	private final String BLANK_20 = "                    ";
	
	private Integer year;
	private Month startMonth; 
	private Month endMonth; 
	private LiquidationType liquidationType;
	
	private EMP currentEMP;
	private int totalContractSum = 0;
	
	private Date getStartDate(){
		Calendar cal = Calendar.getInstance();
		cal.set(year, startMonth.ordinal(), 1);
		return cal.getTime(); 
	}
	private Date getEndDate(){
		Calendar cal = Calendar.getInstance();
		cal.set(year, endMonth.ordinal(), 1);
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
		totalContractSum = 0;
		try {
			ETI eti = createETIRecord( list );
			File file = File.createTempFile("XXXXXXXX", ".FAN");
			FileFiller fan = new FAN(eti, file.getAbsolutePath());
			FileOutput output = new FileOutput();
			output.setFile(file);
			output.setErrors(fan.create());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	private ETI createETIRecord( List<EnterpriseCCC> list ) throws ManagerBeanException {
		ETI eti = new ETI();
		// TODO Clave proporcionada por la seguridad social
		Integer clave = 12345678;
		eti.setClave(clave);
		for (EnterpriseCCC ccc: list) {
			EMP emp = createEMPrecord(ccc);
			eti.getEmpresas().add(emp);
		}
		setEti( eti );
		return getEti();
	}
	
	private EMP createEMPrecord(EnterpriseCCC ccc) throws  ManagerBeanException {
//		EMP emp = new EMP();
		currentEMP = new EMP();
		EMP emp = currentEMP;
		// TODO se ha movido el ccc y activity de company a payroll
//		ent.initMainActiviy();
		// regimen de la SS + provincia + numero ccc
		emp.setCodigoCuentaCotizacionSeguridadSocial("0111"+ccc.getCcc());
		
		RegistryDirStaff dirStaff = getDirStaff(ccc);
    	if ( dirStaff!=null ) {
//    		if(dirStaff.getRegistry().getDocumentType()==DocumentType.NIF){
//    			emp.setTipoDocumento("1");
//    		} else if(dirStaff.getRegistry().getDocumentType()==DocumentType.PASSPORT){
//    			emp.setTipoDocumento("2");
//    		} else if(dirStaff.getRegistry().getDocumentType()==DocumentType.NIE){
//    			emp.setTipoDocumento("6");
//    		} else if(dirStaff.getRegistry().getDocumentType()==DocumentType.CIF){
//    			emp.setTipoDocumento("9");
//    		} else {
//    			emp.setTipoDocumento("9");
//    		}
    		emp.setTipoDocumento("1");
    		String pais = null;
    		try {
//    			pais = dirStaff.getRegistry().getDocumentCountry().getIso3();
    			pais = Country.ES.getIso3();
    		} catch (NullPointerException e) {
    			pais = BLANK_3;
    		}			
    		if (StringUtils.isBlank(pais)) {
    			pais = BLANK_3;
    		}
    		emp.setPais(pais);
    		emp.setNumeroIdentificacion(autoComplete(dirStaff.getDocument(), 14, "0", true));
    	}
    	emp.setCalificador(BLANK_2);
		emp.setCodigoCuentaCotizacionPrincipal("0111"+ccc.getCcc());
		emp.setAnio(year);
		emp.setDesdeMes(startMonth.ordinal()+1);
		emp.setHastaMes(endMonth.ordinal()+1);
		emp.setCalificadorLiquidacion(liquidationType.getValue());
		//solicitud cuota total o cuota de la aportacion del trabajador
		emp.setClaseLiquidacion(0);

		emp.setRzs(createRZSrecord(ccc.getActivity().getEnterprise()));
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, endMonth.ordinal(), 1);
		List<ITransferObject> list = getContracts(ccc, cal.getTime()); 
		for(ITransferObject to: list){
			Contract c = (Contract) to;
			if(getSalary(c)!=null && c.getRegimeType()!=SSRegimeType.SELF_EMPLOYED){
				TRA tra = createTRARecord(c);
				emp.getTrabajadores().add(tra);
				++totalContractSum;
			}
		}
		
		emp.getTcTotales().add(createTCTRecord(ccc));
		
//		emp.getEdt().addAll(createEDTRecord(ccc));
		createEDTRecords(ccc);
		
		// TODO Obligatorio para pago electronico, saldos acreedores y cargo en cuenta
		emp.setMpg(createMPGRecord(ccc));
		
		return emp;
	}
	
	private RegistryDirStaff getDirStaff(EnterpriseCCC ccc){
		try {
			IManagerBean bean = BeanManager.getManagerBean(RegistryDirStaff.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_DIR_STAFF_REGISTRY_ID), ccc.getActivity().getEnterprise().getRegistry().getId() );
			criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_DIR_STAFF_REPRESENTATIVE_LABOR), true );
			List<ITransferObject> dirStaffList = bean.getList(criteria);
			if (! dirStaffList.isEmpty() ) {
				return (RegistryDirStaff) dirStaffList.get(0);	
			}
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String autoComplete(String value, int lenght, String completeValue, boolean leftSide) {
		while( value.length() < lenght ){
			value = leftSide?completeValue+value:value+completeValue;
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
	
	private TRA createTRARecord(Contract contract) throws ManagerBeanException {
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
		String pais = contract.getPerson().getRegistry().getDocumentCountry().getIso3();
		String doc = autoComplete(contract.getPerson().getRegistry().getDocument(), 14, "0", true);
		String ipf = StringUtils.isBlank(tipo)?"9":tipo;
		ipf += StringUtils.isBlank(pais)?BLANK_3:pais; 
		ipf += doc;
		tra.setIpf(ipf);
		tra.setAyn(createAYNRecord(contract));
		
//		int start = startMonth.ordinal();
//		int end = endMonth.ordinal();
//		while(start<=end){
//			DAT dat = createDATRecord(contract);
//			tra.getDat().add(dat);
//			start++;
//		}
		
		tra.setDat(createDATRecord(contract));
			
		return tra;
	}
	
	private AYN createAYNRecord(Contract contract) {
		AYN ayn = new AYN();
		String ap1 = contract.getPerson().getFirstSurname();
		String ap2 = contract.getPerson().getSecondSurname();
		String n = contract.getPerson().getName();
		ayn.setPrimerApellido(ap1!=null?ap1:BLANK_20);
		ayn.setSegundoApellido(ap2!=null?ap2:BLANK_20);
		ayn.setNombre(n!=null?n:BLANK_15);
		ayn.setAbreviado((ap1!=null?ap1.substring(0, 2):BLANK_2)+(ap2!=null?ap2.substring(0, 2):BLANK_2)+(n!=null?n.substring(0, 1):BLANK_1));
		return ayn;
	}
	
	private List<DAT> createDATRecord(Contract c) {
		List<DAT> datList = new LinkedList<DAT>();
		
		// TODO comprobar que situaciones implican un nuevo segmento de tipo DAT
		
		DAT dat = new DAT();
		dat.setMes(endMonth.ordinal()+1);
		dat.setIndicadoresPerfil((isPartialStrike(c)?"H":BLANK_1)
				+(isMoonlighting(c)?"P":BLANK_1)
				+(isJournalReduction(c)!=null?isJournalReduction(c):BLANK_1)
				+(isMonthSalary(c)?"M":BLANK_1)
				+(isNoRetributionDischarge(c)?"A":BLANK_1)
				+(getOthers(c))
				+(isLessThan7DaysContract(c)?"C":BLANK_1));
		dat.setDiasHoras(getContractDaysHours(c));
		dat.setDiasAlta(getContractDischargeDays(c));
		dat.setIndicadorCotizacion(getQuoteIndicator(c));
		dat.setIndicadorVacaciones(getVacationIndicator(c));
		dat.setClaveJornadasColectivo(getCollectiveJournalHours(c));
		dat.setEspecificos(getSpecifics(c));
		dat.setIndReduccionBoni(getBonificationReduction(c));
		dat.setGrupoCotizacion(getQuoteGroup(c));
		dat.setTipoContrato(getContractType(c));
		dat.setClaveContrato(getContractKey(c));
		dat.setEpigrafeAtEp(getAtEpEpigraph(c));
		dat.setEpigrafeSecundario(getSecondariEpigraph(c));
		dat.setOcupacion(getContractOccupation(c));
		dat.setModalidadCotizacion(getQuoteMode(c));
		dat.setIndDiscapacidad(getHandicapIndicator(c));
		dat.setIndRelacion(getEmploymentRelation(c));
		dat.setColectivoPeculiar(getParticularGroup(c));
		dat.setInfoComplementaria(null);
		
		//*** segmento EDL ***
		Salary salary = getSalary(c);
		if(salary!=null){
			EDL edl;
			if(salary.getCommonBase().equals(salary.getRawCommonBase())){
				edl = dat.getEdlSegment("BA00");
				createEDLRecord(
						edl,
						"BA",
						0,
						0,
						new Double(salary.getCommonBase()*100).intValue(),
						" ",
						0,
						autoComplete("0", 8, "0", true),
						autoComplete("0", 8, "0", true),
						autoComplete("0", 8, "0", true),
				" ");	
			} else {
				edl = dat.getEdlSegment("BA01");
				createEDLRecord(
						edl,
						"BA",
						1,
						0,
						new Double(salary.getCommonBase()*100).intValue(),
						" ",
						0,
						autoComplete("0", 8, "0", true),
						autoComplete("0", 8, "0", true),
						autoComplete("0", 8, "0", true),
				" ");	
				edl = dat.getEdlSegment("BA02");
				createEDLRecord(
						edl,
						"BA",
						2,
						0,
						new Double(salary.getRawCommonBase()*100).intValue(),
						" ",
						0,
						autoComplete("0", 8, "0", true),
						autoComplete("0", 8, "0", true),
						autoComplete("0", 8, "0", true),
				" ");
			}
			
			
			
			SalaryBonus bonus = getBonus(c);
			if(bonus!=null){
				Double baseDc = bonus.getAmount();
//				Integer dcDays = differenceBetweenDates(bonus.getStartDate().before(getStartDate())?getStartDate():bonus.getStartDate(), bonus.getEndDate().after(getEndDate())?getEndDate():bonus.getEndDate());
				Integer dcDays = 0;
				if(baseDc != null && baseDc > 0){
					edl = dat.getEdlSegment("CD07");
							createEDLRecord(
									edl,
									"CD",
									7,
									dcDays,
									new Double(baseDc*100).intValue(),
									" ",
									0,
									autoComplete("0", 8, "0", true),
									autoComplete("0", 8, "0", true),
									autoComplete("0", 8, "0", true),
									" ");
				}
			}
		}
		//*****************************************************************
		//*****************************************************************
		//*****************************************************************
		//*****************************************************************
		datList.add(dat);
		return datList;
	}

	private Integer getDcDays(Contract c) {
		// TODO Auto-generated method stub
		return null;
	}
	private SalaryBonus getBonus(Contract c) {
		Salary salary = getSalary(c);
		
		try {
			IManagerBean bean = BeanManager.getManagerBean(SalaryBonus.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_BONUS_SALARY_ID), salary.getId());
//			criteria.addLessThanOrEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_BONUS_START_DATE), getEndDate());
//			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_BONUS_END_DATE), getStartDate());
//			Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IPayrollAlias.CONTRACT_BONUS_END_DATE));
//			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				return (SalaryBonus) list.get(0);
			}
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private Integer getParticularGroup(Contract c) {
		// TODO Auto-generated method stub
		return null;
	}
	private Integer getEmploymentRelation(Contract c) {
		// TODO Auto-generated method stub
//		0100 Personal de Alta Dirección
//		0409 Deportistas profesionales
//		0900 Abogados en despachos de abogados
//		9909 Personal becario de investigación
		String tc2 = getContractDataMap(c).get(ContractVariables.TC2);
		if(tc2!=null && (tc2.equals("100") || tc2.equals("409") || tc2.equals("900"))){
			return Integer.parseInt(tc2);
		}
		return null;
	}
	private String getHandicapIndicator(Contract c) {
		// TODO Auto-generated method stub
//		D Minusvalía igual o superior al 33%
//		S Pensionista incapacidad permanente de la S.S.
//		P Pensionista incapacidad permanente clases pasivas
		
		return null;
	}
	private String getQuoteMode(Contract c) {
		// TODO Auto-generated method stub
//		J Cotización Jornadas Reales
//		G Cotización Sistema General
		//obligatorio para reg. 0613 
		return null;
	}
	private String getContractOccupation(Contract c) {
		String o = getContractDataMap(c).get(ContractVariables.OCCUPATION);
		return o!=null && !o.isEmpty()?o:null;
	}
	private Integer getSecondariEpigraph(Contract c) {
		// TODO Auto-generated method stub
		return null;
	}
	private Integer getAtEpEpigraph(Contract c) {
		// TODO Auto-generated method stub
		return null;
	}
	private Integer getContractKey(Contract c) {
		String tc2 = getContractDataMap(c).get(ContractVariables.TC2);
		return tc2!=null && !tc2.isEmpty()?Integer.parseInt(tc2):null;
	}
	private String getContractType(Contract c) {
		String i = getContractDataMap(c).get(ContractVariables.INDEFINITE);
		return i!=null && !i.isEmpty()?"I":"D";
	}
	private Integer getQuoteGroup(Contract c) {
		String q = getContractDataMap(c).get(ContractVariables.QUOTE_GROUP);
		return q!=null && !q.isEmpty()?Integer.parseInt(q):null;
	}
	private Integer getBonificationReduction(Contract c) {
		// TODO Auto-generated method stub
//		1 Reducción minima
//		2 Reducción media
//		3 Reducción máxima
//		4 Importe total sin reducción
		return null;
	}
	private Integer getSpecifics(Contract c) {
		// TODO Auto-generated method stub
		// obligatorio para regimen 0911
		return null;
	}
	private String getCollectiveJournalHours(Contract c) {
		// TODO Auto-generated method stub
		// Obligatorio para el R.E. Minería del Carbón.
//		0 Normales
//		2 Efectivos
		return null;
	}
	private String getVacationIndicator(Contract c) {
		String v = getContractDataMap(c).get(ContractVariables.NO_HOLIDAYS);
		return v!=null && !v.isEmpty()?"V":BLANK_1;
	}
	private String getQuoteIndicator(Contract c) {
		// TODO Auto-generated method stub
		return null;
	}
	private Integer getContractDischargeDays(Contract c) {
		// TODO Auto-generated method stub
		return null;
	}
	private Integer getContractDaysHours(Contract c) {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		cal.set(year, startMonth.ordinal(), 1);
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	private boolean isLessThan7DaysContract(Contract c) {
		if(c.getEndDate()!=null && differenceBetweenDates(c.getStartDate(), c.getEndDate())<7){
			return true;
		}
		return false;
	}
	private String getOthers(Contract c) {
		// TODO Auto-generated method stub
		
//		R Regulación de empleo Parcial (parte trabajada)
//		T Regulación de empleo Total
//		P Regulación de empleo Parcial (parte E.R.E)
//		I Incapacidad temporal diferida
		return " ";
	}
	private boolean isNoRetributionDischarge(Contract c) {
		// TODO Auto-generated method stub
		return false;
	}
	private boolean isMonthSalary(Contract c) {
		// TODO Auto-generated method stub
		return false;
	}
	private String isJournalReduction(Contract c) {
//		~ Sin reducción
//		I Incapacidad Temporal
//		R Con reducción (jornada guarda legal,contempladas en la
//		Ley Orgánica 3/2007, de 22 de marzo de 2008,de Igualdad
//		maternidad/Paternidad a tiempo parcial). Jornada trabajada
//		T Maternidad/Paternidad Tiempo Parcial (jornada de
//		descanso)
//		D Descanso por Maternidad/Paternidad a tiempo completo
//		E Riesgo durante el embarazo
		return null;
	}
	private boolean isMoonlighting(Contract c) {
		// TODO Auto-generated method stub
		return false;
	}
	private boolean isPartialStrike(Contract c) {
		// TODO Auto-generated method stub
		return false;
	}
	private EDL createEDLRecord(EDL edl, String type, Integer key, Integer element, Integer amount, String sign, Integer resolutionType, String resolutionDate, String startPeriod, String endPeriod, String resolutionReference) {
//		EDL edl = null;
//		edl = new EDL();
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
		return edl;
	}
	
	private String getResolutionReference(Contract c) {
		// TODO Auto-generated method stub
		return null;
	}
	private String getEndPeriod(Contract c) {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		cal.set(year, endMonth.ordinal(), 1);
		return year.toString()+autoComplete(String.valueOf(endMonth.ordinal()+1), 2, "0", true)+cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	private String getStartPeriod(Contract c) {
		// TODO Auto-generated method stub
		return year.toString()+autoComplete(String.valueOf(startMonth.ordinal()+1), 2, "0", true)+"01";
	}
	private String getResolutionDate(Contract c) {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		cal.set(year, endMonth.ordinal(), 1);
		return year.toString()+autoComplete(String.valueOf(endMonth.ordinal()+1), 2, "0", true)+cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	private Integer getResolutionType(Contract c) {
		// TODO Auto-generated method stub
		return null;
	}
	private String getAmountSign(Salary salary) {
		// TODO Auto-generated method stub
		return null;
	}
	private String getDataAmount(Salary salary) {
		// TODO Auto-generated method stub
//		return String.valueOf(CommonUtil.round(salary.getCommonBase()));
		return String.valueOf(new Double(salary.getCommonBase()*100).intValue());
	}
	private Integer getDataElement(Contract c) {
		// TODO Auto-generated method stub
		return 0;
	}
	private Integer getDataKey(Contract c) {
		// TODO Auto-generated method stub
		return 0;
	}
	private TCT createTCTRecord(EnterpriseCCC ccc) throws ManagerBeanException {
		// TODO
		Calendar cal = Calendar.getInstance();
		cal.set(year, endMonth.ordinal(), 1);
		TCT tct = new TCT();
		tct.setEntidadAtEp(Mutual.M001.getValue());
		tct.setNumeroTrabajadores(Integer.parseInt(autoComplete(String.valueOf(totalContractSum), 6, "0", true)));
		tct.setNumeroTrabajadoresFijos(null);
		tct.setNumeroTrabajadoresNoFijos(null);
		tct.setTotalNumeroTrabajadores(null);
		if(liquidationType==LiquidationType.L02 || liquidationType==LiquidationType.L03){
			tct.setFechaControl(year+Integer.parseInt(autoComplete(String.valueOf(startMonth.ordinal()+1), 2, "0", true)+"01"));
		} else {
			tct.setFechaControl(null);
		}
		tct.setTotalJornadas(null);
		tct.setTrabajadoresExtranjeros(null);
		return tct;
	}
	
	private Map<String, EDT> createEDTRecords(EnterpriseCCC ccc) throws ManagerBeanException {
		// TODO
		List<EDT> list = new LinkedList<EDT>();
		EDT edt = null;
		
		for(ITransferObject to: getContracts(ccc, getStartDate())){
			Contract c = (Contract) to;
		
			if(getSalary(c)!=null){
				
				createEDTBa01Segment(c);
				createEDTBa02Segment(c);
				createEDTBa09Segment(c);
				createEDTBa10Segment(c);
				createEDTBa11Segment(c);
				createEDTBa21Segment(c);
				createEDTBa22Segment(c);
				createEDTBa42Segment(c);
				
				createEDTCd01Segment(c);
				createEDTCd03Segment(c);
				createEDTCd06Segment(c);
				createEDTCd07Segment(c);
				createEDTCd10Segment(c);
				createEDTCd11Segment(c);
				createEDTCd13Segment(c);
				createEDTCd16Segment(c);
				createEDTCd17Segment(c);
				createEDTCd20Segment(c);
				createEDTCd21Segment(c);
				createEDTCd22Segment(c);
				createEDTCd23Segment(c);
				createEDTCd24Segment(c);
				createEDTCd25Segment(c);
				createEDTCd28Segment(c);
				
				createEDTCa01Segment(c);
				createEDTCa02Segment(c);
				createEDTCa11Segment(c);
				createEDTCa12Segment(c);
				createEDTCa20Segment(c);
				createEDTCa21Segment(c);
				createEDTCa22Segment(c);
				createEDTCa30Segment(c);
				createEDTCa31Segment(c);
				createEDTCa32Segment(c);
				createEDTCa50Segment(c);
				createEDTCa57Segment(c);
				createEDTCa60Segment(c);
				createEDTCa80Segment(c);
				createEDTCa90Segment(c);
				
				createEDTTt10Segment(c);
				createEDTTt20Segment(c);
				createEDTTt30Segment(c);
				createEDTTt9XSegment(c);
			}

		}
		
//		return list;
		return null;
	}
	
	/**
	 * se crea el segmento tt91 o tt92 dependiendo del signo del importe
	 */
	private void createEDTTt9XSegment(Contract c) {
		Integer amount = (currentEMP.getEdt().containsKey("4_"+"TT10")?currentEMP.getEdtSegment("4_"+"TT10").getImporte():0) 
		+ (currentEMP.getEdt().containsKey("4_"+"TT20")?currentEMP.getEdtSegment("4_"+"TT20").getImporte():0)
		+ (currentEMP.getEdt().containsKey("4_"+"TT30")?currentEMP.getEdtSegment("4_"+"TT30").getImporte():0);
		EDT edt = currentEMP.getEdtSegment("4_"+"TT91");
		edt.setTipoElemento("TT");
		edt.setClave(amount<0?92:91);
		edt.setCalificadorClave(null);
		edt.setBase(null);
		edt.setIndicadorFactorTipo(" ");
		edt.setParteEnteraTipo(0);
		edt.setParteDecimalFactorTipo(0);
		edt.setSigno(amount<0?"-":" ");
		if(amount<0){
			amount += (currentEMP.getEdt().containsKey("3_"+"CA90")?currentEMP.getEdtSegment("3_"+"CA90").getImporte():0);
		}
		edt.setImporte(amount);
	}
	private void createEDTTt30Segment(Contract c) {
		Integer amount = (currentEMP.getEdt().containsKey("3_"+"CA50")&&currentEMP.getEdtSegment("3_"+"CA50").getImporte()!=null?currentEMP.getEdtSegment("3_"+"CA50").getImporte():0) 
		+ (currentEMP.getEdt().containsKey("3_"+"CA57")?currentEMP.getEdtSegment("3_"+"CA57").getImporte():0)
		- (currentEMP.getEdt().containsKey("3_"+"CA60")&&currentEMP.getEdtSegment("3_"+"CA60").getImporte()!=null?currentEMP.getEdtSegment("3_"+"CA60").getImporte():0)
		- (currentEMP.getEdt().containsKey("2_"+"CD24")?currentEMP.getEdtSegment("2_"+"CD24").getImporte():0);
		EDT edt = currentEMP.getEdtSegment("4_"+"TT30");
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
	private void createEDTTt20Segment(Contract c) {
		Integer amount = (currentEMP.getEdt().containsKey("3_"+"CA30")&&currentEMP.getEdtSegment("3_"+"CA30").getImporte()!=null?currentEMP.getEdtSegment("3_"+"CA30").getImporte():0) 
		- (currentEMP.getEdt().containsKey("2_"+"CD03")?currentEMP.getEdtSegment("2_"+"CD03").getImporte():0);
		EDT edt = currentEMP.getEdtSegment("4_"+"TT20");
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
	private void createEDTTt10Segment(Contract c) {
		Integer amount = (currentEMP.getEdt().containsKey("3_"+"CA01")&&currentEMP.getEdtSegment("3_"+"CA01").getImporte()!=null?currentEMP.getEdtSegment("3_"+"CA01").getImporte():0) 
			+ (currentEMP.getEdt().containsKey("3_"+"CA02")?currentEMP.getEdtSegment("3_"+"CA02").getImporte():0)
			+ (currentEMP.getEdt().containsKey("3_"+"CA11")?currentEMP.getEdtSegment("3_"+"CA11").getImporte():0)
			+ (currentEMP.getEdt().containsKey("3_"+"CA12")?currentEMP.getEdtSegment("3_"+"CA12").getImporte():0)
			- (currentEMP.getEdt().containsKey("3_"+"CA20")?currentEMP.getEdtSegment("3_"+"CA20").getImporte():0)
			- (currentEMP.getEdt().containsKey("3_"+"CA21")?currentEMP.getEdtSegment("3_"+"CA21").getImporte():0)
			- (currentEMP.getEdt().containsKey("3_"+"CA22")?currentEMP.getEdtSegment("3_"+"CA22").getImporte():0)
			- (currentEMP.getEdt().containsKey("1_"+"BA10")?currentEMP.getEdtSegment("1_"+"BA10").getImporte():0)
			- (currentEMP.getEdt().containsKey("1_"+"BA11")?currentEMP.getEdtSegment("1_"+"BA11").getImporte():0);
		EDT edt = currentEMP.getEdtSegment("4_"+"TT10");
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
	private void createEDTCa90Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCa80Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCa60Segment(Contract c) {
		Integer amount = 0;
		amount += currentEMP.getEdt().containsKey("2_"+"CD07")?currentEMP.getEdtSegment("2_"+"CD07").getImporte():0;
		amount += currentEMP.getEdt().containsKey("2_"+"CD10")?currentEMP.getEdtSegment("2_"+"CD10").getImporte():0;
		amount += currentEMP.getEdt().containsKey("2_"+"CD11")?currentEMP.getEdtSegment("2_"+"CD11").getImporte():0;
		amount += currentEMP.getEdt().containsKey("2_"+"CD13")?currentEMP.getEdtSegment("2_"+"CD13").getImporte():0;
		amount += currentEMP.getEdt().containsKey("2_"+"CD16")?currentEMP.getEdtSegment("2_"+"CD16").getImporte():0;
		amount += currentEMP.getEdt().containsKey("2_"+"CD20")?currentEMP.getEdtSegment("2_"+"CD20").getImporte():0;
		amount += currentEMP.getEdt().containsKey("2_"+"CD21")?currentEMP.getEdtSegment("2_"+"CD21").getImporte():0;
		amount += currentEMP.getEdt().containsKey("2_"+"CD22")?currentEMP.getEdtSegment("2_"+"CD22").getImporte():0;
		amount += currentEMP.getEdt().containsKey("2_"+"CD23")?currentEMP.getEdtSegment("2_"+"CD23").getImporte():0;
		amount += currentEMP.getEdt().containsKey("2_"+"CD25")?currentEMP.getEdtSegment("2_"+"CD25").getImporte():0;
		amount += currentEMP.getEdt().containsKey("3_"+"CA28")?currentEMP.getEdtSegment("3_"+"CA28").getImporte():0;
		amount += currentEMP.getEdt().containsKey("3_"+"CA80")?currentEMP.getEdtSegment("3_"+"CA80").getImporte():0;
		EDT edt = currentEMP.getEdtSegment("3_"+"CA60");
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
	private void createEDTCa57Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCa50Segment(Contract c) {
		Integer base = currentEMP.getEdt().containsKey("1_"+"BA02")?currentEMP.getEdtSegment("1_"+"BA02").getBase():0;
		EDT edt = currentEMP.getEdtSegment("3_"+"CA50");
		edt.setTipoElemento("CA");
		edt.setClave(50);
		edt.setCalificadorClave(null);
		edt.setBase(base);
		edt.setImporte(new Double((edt.getBase())*0.283).intValue());
		edt.setSigno(" ");
	}
	private void createEDTCa32Segment(Contract c) {
		Integer amount = 0;
		// TODO suma cuotas trabajadores segun epigrafes
//		amount += currentEMP.getEdt().containsKey("3_"+"CA31")?currentEMP.getEdtSegment("3_"+"CA31").getBase():0;
//		amount += currentEMP.getEdt().containsKey("3_"+"CA31")?currentEMP.getEdtSegment("3_"+"CA31").getBase():0;
		EDT edt = currentEMP.getEdtSegment("3_"+"CA32");
		edt.setTipoElemento("CA");
		edt.setClave(32);
		edt.setCalificadorClave(null);
		edt.setBase(null);
		edt.setImporte(amount);
		edt.setSigno(" ");
	}
	private void createEDTCa31Segment(Contract c) {
		Integer amount = 0;
		// TODO suma cuotas trabajadores segun epigrafes
//		amount += currentEMP.getEdt().containsKey("3_"+"CA31")?currentEMP.getEdtSegment("3_"+"CA31").getBase():0;
//		amount += currentEMP.getEdt().containsKey("3_"+"CA31")?currentEMP.getEdtSegment("3_"+"CA31").getBase():0;
		EDT edt = currentEMP.getEdtSegment("3_"+"CA31");
		edt.setTipoElemento("CA");
		edt.setClave(31);
		edt.setCalificadorClave(null);
		edt.setBase(null);
		edt.setImporte(amount);
		edt.setSigno(" ");
	}
	private void createEDTCa30Segment(Contract c) {
		Integer amount = 0;
		amount += currentEMP.getEdt().containsKey("3_"+"CA31")?currentEMP.getEdtSegment("3_"+"CA31").getImporte():0;
		amount += currentEMP.getEdt().containsKey("3_"+"CA31")?currentEMP.getEdtSegment("3_"+"CA31").getImporte():0;
		EDT edt = currentEMP.getEdtSegment("3_"+"CA30");
		edt.setTipoElemento("CA");
		edt.setClave(30);
		edt.setCalificadorClave(null);
		edt.setBase(null);
		edt.setImporte(amount);
		edt.setSigno(" ");
	}
	private void createEDTCa22Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCa21Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCa20Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCa12Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCa11Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCa02Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCa01Segment(Contract c) {
		Integer base = currentEMP.getEdt().containsKey("1_"+"BA01")?currentEMP.getEdtSegment("1_"+"BA01").getBase():0; 
		EDT edt = currentEMP.getEdtSegment("3_"+"CA01");
		edt.setTipoElemento("CA");
		edt.setClave(1);
		edt.setCalificadorClave(null);
		edt.setBase(base);
//	edt.setIndicadorFactorTipo("T");
//	edt.setParteEnteraTipo(28);
//	edt.setParteDecimalFactorTipo(03);
		edt.setImporte(new Double((edt.getBase())*0.283).intValue());
		edt.setSigno(" ");
	}
	private void createEDTCd28Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCd25Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCd24Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCd23Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCd22Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCd21Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCd20Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCd17Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCd16Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCd13Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCd11Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCd10Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCd07Segment(Contract c) {
		Integer amount = 0;
		for(TRA tra: currentEMP.getTrabajadores()){
			amount += tra.getDat().get(0).getEdl().containsKey("CD07")?tra.getDat().get(0).getEdlSegment("CD07").getImporte():0;
		}
//		if(getBonus(c)!=null){
			EDT edt = currentEMP.getEdtSegment("2_"+"CD07");
			edt.setTipoElemento("CD");
			edt.setClave(7);
			edt.setCalificadorClave(null);
			edt.setBase(0);
			edt.setIndicadorFactorTipo(null);
			edt.setParteEnteraTipo(0);
			edt.setParteDecimalFactorTipo(0);
			edt.setImporte(amount);
			edt.setSigno(" ");
//		}
	}
	private void createEDTCd06Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCd03Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTCd01Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTBa42Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTBa22Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTBa21Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTBa11Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTBa10Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTBa09Segment(Contract c) {
		// TODO Auto-generated method stub
		
	}
	private void createEDTBa02Segment(Contract c) {
		Integer base = 0;
		for(TRA tra: currentEMP.getTrabajadores()){
			base += tra.getDat().get(0).getEdl().containsKey("BA00")?tra.getDat().get(0).getEdlSegment("BA00").getImporte():0;
			base += tra.getDat().get(0).getEdl().containsKey("BA02")?tra.getDat().get(0).getEdlSegment("BA02").getImporte():0;
		}
		EDT edt = currentEMP.getEdtSegment("1_"+"BA02");
		edt.setTipoElemento("BA");
		edt.setClave(2);
		edt.setCalificadorClave(null);
		edt.setBase(base);
		edt.setIndicadorFactorTipo(null);
		edt.setParteEnteraTipo(0);
		edt.setParteDecimalFactorTipo(0);
		edt.setImporte(null);
		edt.setSigno(" ");
	}
	private void createEDTBa01Segment(Contract c) {
		Integer base = 0;
		for(TRA tra: currentEMP.getTrabajadores()){
			base += tra.getDat().get(0).getEdl().containsKey("BA00")?tra.getDat().get(0).getEdlSegment("BA00").getImporte():0;
			base += tra.getDat().get(0).getEdl().containsKey("BA01")?tra.getDat().get(0).getEdlSegment("BA01").getImporte():0;
		}
		EDT edt = currentEMP.getEdtSegment("1_"+"BA01");
		edt.setTipoElemento("BA");
		edt.setClave(1);
		edt.setCalificadorClave(null);
		edt.setBase(base);
		edt.setIndicadorFactorTipo(null);
		edt.setParteEnteraTipo(0);
		edt.setParteDecimalFactorTipo(0);
		edt.setImporte(null);
		edt.setSigno(" ");
	}
	
	
	private Salary getContractSalary(EnterpriseCCC ccc) throws ManagerBeanException {
		// TODO Auto-generated method stub
		Calendar startCal = Calendar.getInstance();
		startCal.set(year, startMonth.ordinal(), 1);
		Calendar endCal = Calendar.getInstance();
		endCal.set(year, startMonth.ordinal(), endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		List<ISalary> list;
		Criteria criteria = new Criteria();
		IManagerBean bean = BeanManager.getManagerBean(Salary.class);
		String alias = bean.getFieldName(IPayrollAlias.SALARY_CONTRACT_WORK_PLACE_ENTERPRISE_ID);
		criteria.addEqualExpression(alias, ccc.getActivity().getEnterprise().getId());
		alias = bean.getFieldName(IPayrollAlias.SALARY_END_DATE);
		criteria.addGreaterThanOrEqualExpression(alias, startCal.getTime());
		alias = bean.getFieldName(IPayrollAlias.SALARY_END_DATE);
		criteria.addLessThanOrEqualExpression(alias, endCal.getTime());
		criteria.addOrder(bean.getFieldName(IPayrollAlias.SALARY_CONTRACT_WORK_PLACE_ID));
		criteria.addOrder(bean.getFieldName(IPayrollAlias.SALARY_EMPLOYEE_NAME));
		list = new LinkedList<ISalary>();
//		Double total = new Double(0);
//		for( ITransferObject to : bean.getList(criteria) ) {
//			Salary s = (Salary) to;
////			list.add(s);
//			total += s.getRawCommonBase();
//		}	
//		return (new Double(CommonUtil.round(total)*10)).intValue();
		return (Salary) bean.getList(criteria).get(0);
	}
	private Integer getEnterpriseAmount(EnterpriseCCC ccc) throws ManagerBeanException {
		// TODO Auto-generated method stub
		Calendar startCal = Calendar.getInstance();
		startCal.set(year, startMonth.ordinal(), 1);
		Calendar endCal = Calendar.getInstance();
		endCal.set(year, startMonth.ordinal(), endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		List<ISalary> list;
		Criteria criteria = new Criteria();
		IManagerBean bean = BeanManager.getManagerBean(Salary.class);
		String alias = bean.getFieldName(IPayrollAlias.SALARY_CONTRACT_WORK_PLACE_ENTERPRISE_ID);
		criteria.addEqualExpression(alias, ccc.getActivity().getEnterprise().getId());
		alias = bean.getFieldName(IPayrollAlias.SALARY_END_DATE);
		criteria.addGreaterThanOrEqualExpression(alias, startCal.getTime());
		alias = bean.getFieldName(IPayrollAlias.SALARY_END_DATE);
		criteria.addLessThanOrEqualExpression(alias, endCal.getTime());
		criteria.addOrder(bean.getFieldName(IPayrollAlias.SALARY_CONTRACT_WORK_PLACE_ID));
		criteria.addOrder(bean.getFieldName(IPayrollAlias.SALARY_EMPLOYEE_NAME));
		list = new LinkedList<ISalary>();
		Double total = new Double(0);
		for( ITransferObject to : bean.getList(criteria) ) {
			Salary s = (Salary) to;
//			list.add(s);
			total += s.getTotalEnterprise();
		}		
		return (new Double(CommonUtil.round(total)*100)).intValue();
	}
	private MPG createMPGRecord(EnterpriseCCC ccc) {
		// TODO
		RegistryBank bank = getBank(ccc);
		RegistryDirStaff dirStaff = getDirStaff(ccc);
		MPG mpg = new MPG();
//		~ Saldo Acreedor
//		C Cargo en Cuenta
//		V Pago electrónico
		mpg.setSolicitudModalidadPago("C");
		mpg.setCondigoCuentaCliente(bank.getBankAccount().getValue());
		mpg.setTipoIdentificadorTitular("1");
		mpg.setIdentificadorTitular(autoComplete(dirStaff.getDocument(), 14, "0", true));
		mpg.setNombreTitular(dirStaff.getName());
//		mpg.setNombreTitular(autoComplete(dirStaff.getName(), 31, " ", true));
		return mpg;
	}
	
	private RegistryBank getBank(EnterpriseCCC ccc) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(RegistryBank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_BANK_REGISTRY_ID), ccc.getActivity().getEnterprise().getRegistry().getId() );
			criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_BANK_ACTIVE), true );
			List<ITransferObject> bankList = bean.getList(criteria);
			if(bankList.size()>0){
				return (RegistryBank) bankList.get(0);
			}
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_CONTRACT_ID), c.getId());
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_TYPE), SalaryType.SALARY);
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_START_DATE), startCal.getTime());
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_END_DATE), endCal.getTime());
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
	
//	private ISalary calculateSalary(Contract c,Integer year, Month month) {
//		try {
//			Contract contract = (Contract) getTo();
//
//			ISalaryCalculatorContext ctx;
//			ctx = contract.getSalaryCalculatorContext(
//					getYear(),
//					getMonth(),
//					getSalaryType());
//			
//			salary = ctx.getSalaryProxy().getSalary();
//			
//			( ( Salary ) salary).setContract(contract);
//			
//			paymentsModel = null;
//			paymentsList = null;
//			initializePaymentModel();
//
//			return salary;
//		} 
//		catch (OutOfDateException e) {
//			return null;
//		}catch (SalaryException e) {
//			String msg = "Error en el calculo del borrador de la nómina";
//			LOGGER.error(msg, e);
//			return null;
//		}
//	}
	
	private List<Enterprise> getEnterprises(List<Contract> contractList){
		List<Enterprise> list = new LinkedList<Enterprise>();
		for(Contract c: contractList){
			if(!list.contains(c.getWorkPlace().getEnterprise())){
				list.add(c.getWorkPlace().getEnterprise());
			}
		}
		return list;
	}
	
	private List<ITransferObject> getContracts(EnterpriseCCC ccc, Date startDate) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(Contract.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_ENTERPRISE_CCC_ID), ccc.getId());
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_END_DATE), startDate);
		Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IPayrollAlias.CONTRACT_END_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));			
//		criteria.addLessThanOrEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_START_DATE), new Date());
//		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_END_DATE), new Date());
//		Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IPayrollAlias.CONTRACT_END_DATE));
//		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));			
		return bean.getList(criteria);
	}
	
	private Map<String, String> getContractDataMap(Contract contract) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			criteria.addNotNullExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_END_DATE));
			for(ITransferObject to: bean.getList(criteria)){
				ContractData data = (ContractData) to;
				if(data.getExpression()!=null){
					map.put(data.getName(), data.getExpression().replace('"', ' ').trim());
				}
			}
		} catch (ManagerBeanException e) {
			// NADA, que siga generando el fichero
		}
		return map;
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

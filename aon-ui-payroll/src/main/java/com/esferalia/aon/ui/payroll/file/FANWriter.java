package com.esferalia.aon.ui.payroll.file;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.file.payroll.fan.FAN;
import com.esferalia.aon.file.payroll.fan.data.AYN;
import com.esferalia.aon.file.payroll.fan.data.DAT;
import com.esferalia.aon.file.payroll.fan.data.EDL;
import com.esferalia.aon.file.payroll.fan.data.EMP;
import com.esferalia.aon.file.payroll.fan.data.ETI;
import com.esferalia.aon.file.payroll.fan.data.RZS;
import com.esferalia.aon.file.payroll.fan.data.TRA;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.dao.IPayrollAlias;

public class FANWriter {
	
	private ETI eti;
	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
	private final String BLANK_1 = " ";
	private final String BLANK_2 = "  ";
	private final String BLANK_3 = "   ";
	private final String BLANK_15 = "               ";
	private final String BLANK_20 = "                    ";
	
	public ETI getEti() {
		return eti;
	}
	public void setEti(ETI eti) {
		this.eti = eti;
	}

	public FileOutput createFAN(List<Enterprise> list, Integer year, Month startMonth, Month endMonth ) throws ManagerBeanException {
		try {
			ETI eti = createETIRecord( list,year, startMonth, endMonth );
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

	private ETI createETIRecord( List<Enterprise> list, Integer year, Month startMonth, Month endMonth ) throws ManagerBeanException {
		ETI eti = new ETI();
		// TODO Clave proporcionada por la seguridad social
		Integer clave = 12345678;
		eti.setClave(clave);
		for (Enterprise e: list) {
			EMP emp = createEMPrecord(e, year, startMonth, endMonth );
			eti.getEmpresas().add(emp);
		}
		setEti( eti );
		return getEti();
	}
	
	private EMP createEMPrecord(Enterprise enterprise, Integer year, Month startMonth, Month endMonth ) throws  ManagerBeanException {
		EMP emp = new EMP();
		// TODO se ha movido el ccc y activity de company a payroll
//		ent.initMainActiviy();
//		String ccc = ent.getCcc().getCcc();
//		emp.setCodigoCuentaCotizacionSeguridadSocial(ccc);
		String tipo = String.valueOf(enterprise.getRegistry().getType().ordinal());
		if (StringUtils.isBlank(tipo)) {
			tipo = "9";
		}
		emp.setTipo(tipo);
		String pais = null;
		try {
			pais = enterprise.getRegistry().getDefaultAddress().getGeozone().getName();
		} catch (NullPointerException e) {
			pais = "   ";
		}			
		if (StringUtils.isBlank(pais)) {
			pais = "   ";
		}
		emp.setPais(pais);
		emp.setNumero(enterprise.getRegistry().getDocument());
		emp.setCalificador("  ");
//		emp.setCodigoCuentaCotizacionPrincipal(ccc);
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
		emp.setRzs(rzs);
		
		emp.setAnio(year);
		emp.setDesdeMes(startMonth.ordinal());
		emp.setHastaMes(endMonth.ordinal());
		emp.setCalificadorLiquidacion("00");
		emp.setClaseLiquidacion(1);
		
		List<ITransferObject> list = getContracts(enterprise); 
		for(ITransferObject to: list){
			Contract c = (Contract) to;
			TRA tra = createTRARecord(c, year, startMonth, endMonth);
			emp.getTrabajadores().add(tra);
		}
		return emp;
	}
	
	private TRA createTRARecord(Contract contract, Integer year, Month startMonth, Month endMonth) throws ManagerBeanException {
		TRA tra = new TRA();
		tra.setNumeroAfiliacion( contract.getPerson().getSocialSecurityNumber() );
		String tipo = String.valueOf(contract.getPerson().getRegistry().getType()); 
		String pais = contract.getPerson().getRegistry().getDefaultAddress().getGeozone().getName();
		String doc = contract.getPerson().getRegistry().getDocument();
		String ipf = StringUtils.isBlank(tipo)?"9":tipo;
		ipf += StringUtils.isBlank(pais)?BLANK_3:pais; 
		ipf += doc;
		tra.setIpf(ipf);
		AYN ayn = createAYNRecord(contract);
		tra.setAyn(ayn);
		
		int start = startMonth.ordinal();
		int end = endMonth.ordinal();
		while(start<=end){
			DAT dat = createDATRecord(contract, year, Month.getMonthByValue(start));
			tra.getDat().add(dat);
			start++;
		}
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
	
	private DAT createDATRecord(Contract c, Integer year, Month month) {
		DAT dat = new DAT();
		dat.setMes(month.ordinal());
		dat.setIndicadoresPerfil(null);
		dat.setDiasHoras(null);
		dat.setDiasAlta(null);
		dat.setIndicadorCotizacion(null);
		dat.setIndicadorVacaciones(null);
		dat.setClaveJornadasColectivo(null);
		dat.setEspecificos(null);
		dat.setIndReduccionBoni(null);
		dat.setGrupoCotizacion(null);
		dat.setTipoContrato(null);
		dat.setClaveContrato(null);
		dat.setEpigrafeAtEp(null);
		dat.setEpigrafeSecundario(null);
		dat.setOcupacion(null);
		dat.setModalidadCotizacion(null);
		dat.setIndDiscapacidad(null);
		dat.setIndRelacion(null);
		dat.setColectivoPeculiar(null);
		dat.setInfoComplementaria(null);
		EDL edl = createEDLRecord(c, year, month);
		dat.getEdl().add(edl);
		return dat;
	}

	private EDL createEDLRecord(Contract c, Integer year, Month month) {
		Salary salary = getSalary(c, year, month);
		EDL edl = new EDL();
		if(salary!=null){
			edl.setTipoElementoDatos("BA");
			edl.setClave(null);
			edl.setElemento(null);
			edl.setImporte(String.valueOf(CommonUtil.round(salary.getCommonBase())));
			edl.setSigno(" ");
			edl.setTipoResolucion(null);
			edl.setFechaResolucion(null);
			edl.setInicioPeriodo(null);
			edl.setFinPeriodo(null);
			edl.setReferencia(null);
		}
		return edl;
	}
	
	private Salary getSalary(Contract c,Integer year, Month month) {
		Calendar startCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();
		startCal.set(Calendar.YEAR, year);
		endCal.set(Calendar.YEAR, year);
		startCal.set(Calendar.MONTH, month.ordinal());
		endCal.set(Calendar.MONTH, month.ordinal());
		startCal.set(Calendar.DAY_OF_MONTH, 1);
		endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
		Salary salary = null;
		try {
			IManagerBean bean = BeanManager.getManagerBean(Salary.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_CONTRACT_ID), c.getId());
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
	
	private List<Enterprise> getEnterprises(List<Contract> contractList){
		List<Enterprise> list = new LinkedList<Enterprise>();
		for(Contract c: contractList){
			if(!list.contains(c.getWorkPlace().getEnterprise())){
				list.add(c.getWorkPlace().getEnterprise());
			}
		}
		return list;
	}
	
	private List<ITransferObject> getContracts(Enterprise enterprise) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(Contract.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID), enterprise.getId());
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_START_DATE), new Date());
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_END_DATE), new Date());
		Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IPayrollAlias.CONTRACT_END_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));			
		return bean.getList(criteria);
	}

	
	
}

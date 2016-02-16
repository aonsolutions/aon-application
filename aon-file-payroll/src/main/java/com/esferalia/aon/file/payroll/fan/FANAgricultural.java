package com.esferalia.aon.file.payroll.fan;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.file.payroll.fan.data.DAT;
import com.esferalia.aon.file.payroll.fan.data.EDL;
import com.esferalia.aon.file.payroll.fan.data.EDT;
import com.esferalia.aon.file.payroll.fan.data.EMP;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryData;

public class FANAgricultural extends FANGeneral implements Serializable, IFanFactory {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	
	@Override
	public String getQuoteIndicator(List<ITransferObject> salaryDataList, Map<String, String> contractDataMap) {
		return null;
	}
	
	@Override
	public String getQuoteMode(List<ITransferObject> list) {
		Integer _realDays = obtainJornadasReales(list);
		Boolean _monthlyQuote = isMonthlyQuote(list);
		return _realDays!=null && !_monthlyQuote?"J":"G";
	}
	
	private Boolean isMonthlyQuote(List<ITransferObject> list){
		String o;
		o = null;
		for(ITransferObject to: list){
			SalaryData sa = (SalaryData) to;
			if(sa.getName().equals("COTIZACION_MENSUAL")){
				o = sa.getExpression();
			}
		}
		if(o!=null){
			return new Boolean(o);
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
	@Override
	public Integer getContractDaysOrHours(Salary salary, List<ITransferObject> salaryDataList, Map<String, String> contractDataMap, Integer itDays, Date startDate, Date endDate) {
		
		if(itDays!=null && itDays>0){
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH)-itDays; 
			return (days==0)?null:days;
		}
	
		Integer _realDays = obtainJornadasReales(salaryDataList);
		if(_realDays!=null && !"".equals(_realDays)){
			return Double.valueOf(_realDays).intValue();
		} else {
			Date contractStart = salary.getContract().getStartDate();
			Date contractEnd = salary.getContract().getEndDate();
			Date start = startDate.before(contractStart)?contractStart:startDate;
			Date end = (contractEnd!=null && endDate.after(contractEnd))?contractEnd:endDate;
			int availableDays = (int) getAvailableDays(start, end);
			return (contractEnd!=null && contractEnd.before(endDate)) ? availableDays : 30;
		}
		
	}
	
	private Integer obtainJornadasReales(List<ITransferObject> salaryDataList){
		String o = null;
		List<ITransferObject> list = salaryDataList;
		for(ITransferObject to: list){
			SalaryData sa = (SalaryData) to;
			if(sa.getName().equals("JORNADAS_REALES")){
				o = sa.getExpression();
			}
		}
		if(o!=null && NumberUtils.isNumber(o)){
			return Integer.parseInt(o);
		}
		return null;
	}
	
	
	/**
	 *  29 Reducciones SEA. Contingencias comunes Sistema Especial Agrario 
	 * @param salary
	 * @param dat
	 */
	@Override
	public void createEDLCd29Segment(Double cgcTotalEnterprise, Double cgcTotalEmployee, DAT dat, List<ITransferObject> salaryDataList) {
		
		String _cgcBase = obtainCgcBase(salaryDataList);
		String _reductionPercent = obtainSeaReduction(salaryDataList);
		
		// la cuota de la reduccion se obtiene a aplicando 
		// el porcentaje de reduccion a la base de contingencias comunes 
		
		Double cgcBase = NumberUtils.isNumber(_cgcBase)?Double.parseDouble(_cgcBase):0.0;
		Double reductionPercent = NumberUtils.isNumber(_reductionPercent)?Double.parseDouble(_reductionPercent):0.0;
		
		EDL edl = dat.getEdlSegment("CD29");
		super.createEDLRecord(edl, "CD", 29, new Double(CommonUtil.round(cgcBase*reductionPercent/100)*100).intValue());
		
	}
	
	private String obtainCgcBase(List<ITransferObject> salaryDataList){
		String _base = null;
		List<ITransferObject> list = salaryDataList;
		for(ITransferObject to: list){
			SalaryData sa = (SalaryData) to;
			if(sa.getName().equals("BASE_CGC")){
				_base = sa.getExpression();
			}
		}
		return _base;
	}
	private String obtainSeaReduction(List<ITransferObject> salaryDataList){
		String _reductionPercent = null;
		List<ITransferObject> list = salaryDataList;
		for(ITransferObject to: list){
			SalaryData sa = (SalaryData) to;
			if(sa.getName().equals("REDUCCION_CGC_E_02")){
				_reductionPercent = sa.getExpression();
			}
		}
		if(_reductionPercent==null){
			for(ITransferObject to: list){
				SalaryData sa = (SalaryData) to;
				if(sa.getName().equals("REDUCCION_CGC_E_01")){
					_reductionPercent = sa.getExpression();
				}
			}
		}
		return _reductionPercent;
	}

	/**
	 *  30 Reducciones. SEA Desempleo
	 * @param salary
	 * @param dat
	 */
	public void createEDLCd30Segment(DAT dat) {
		// TODO
//		if(bonus.getSalary().getContract().getRegimeType()==SSRegimeType.AGRICULTURAL){
//			EDL edl = dat.getEdlSegment("CD30");
//			createEDLRecord(edl, "CD", 30, new Double(CommonUtil.round((bonus).getAmount())*100).intValue());
//		}
	}
	
	/**
	Fórmulas de cálculo y validación del Sistema Especial Agrario (0163)
	Entrada en vigor a partir de 1 de enero de 2012
	*/
	
	
	
	// ****************************
	// ****************************
	// ELEMENTO CALCULADO TOTALES
	// ****************************
	// ****************************
	
	/**
	 * 01 Contingencias Comunes
	 * 
	 * @param cgcTotalEnterprise
	 * @param cgcTotalEmployee
	 * @param emp
	 */
	@Override
	public void createEDTCa01Segment(Double cgcTotalEnterprise, Double cgcTotalEmployee, EMP emp) {
		super.createEDTCa01Segment(cgcTotalEnterprise, cgcTotalEmployee, emp);
		
		// La cuota se calcula el descuento de la reduccion SEA incluida
		// pero se debe indicar la cuota integra
		
		EDT edtCd29 = emp.getEdtSegment("EDTCD29");
		EDT edtCa01 = emp.getEdtSegment("EDTCA01");
		edtCa01.setImporte(edtCa01.getImporte() + edtCd29.getImporte());
	}
	
	/**
	13
	Cotización especial de solidaridad.
	=EDLBA01+EDLBA21
	=EDLBA01 x 8% + EDLBA21 x 6%
	*/
	private void createEDTCa13(EMP emp) {
		// TODO 
//		Integer base01 = emp.getEdt().containsKey("EDTBA01") ? emp.getEdtSegment("EDTBA01").getBase() : 0;
//		Integer base21 = emp.getEdt().containsKey("EDTBA21") ? emp.getEdtSegment("EDTBA21").getBase() : 0;
//		
//		Double amount = 0.0;
//		amount += base01*0.08;
//		amount += base21*0.06;
//		amount = CommonUtil.round(amount, 2);
//
//		if ((base01+base21) != 0) {
//			EDT edt = emp.getEdtSegment("EDTCA01");
//			edt.setTipoElemento("CA");
//			edt.setClave(13);
//			edt.setCalificadorClave(null);
//			edt.setBase(base01+base21);
//			edt.setIndicadorFactorTipo(" ");
//			edt.setParteEnteraTipo(0);
//			edt.setParteDecimalFactorTipo(0);
//			edt.setImporte((new Double(CommonUtil.round(amount * 100))).intValue());
//			edt.setSigno(" ");
//		}
	}
	
	/**
	22 Suma de compensaciones y reducciones
	= EDTCD06 + EDTCD17
	*/
	@Override
	public void createEDTCa22Segment(EMP emp) {
		Integer amount = 0;
		amount += emp.getEdt().containsKey("EDTCD06") ? emp.getEdtSegment("EDTCD06").getImporte() : 0;
		amount += emp.getEdt().containsKey("EDTCD17") ? emp.getEdtSegment("EDTCD17").getImporte() : 0;
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCA22");
			edt.setTipoElemento("CA");
			edt.setClave(22);
			edt.setCalificadorClave(null);
			edt.setBase(null);
			edt.setImporte(amount);
			edt.setSigno(" ");
		}
	}
	
	
	/**
	50 Desempleo
	=BA02b (suma de bases excepto trabajadores con N en el campo 1271)
	b*t (desempleo tipo total)
	*/
	@Override
	public void createEDTCa50Segment(Double desmplEnterpriseTotal, Double fogasaEnterpriseTotal, Double fpEnterpriseTotal, 
			Double desmplEmployeeTotal, Double fpEmployeeTotal, EMP emp) {
		
		Integer base = emp.getEdt().containsKey("EDTBA02") ? emp.getEdtSegment("EDTBA02").getBase() : 0;

		Double amount = 0.0;
		amount += desmplEnterpriseTotal;
		amount += desmplEmployeeTotal;
		amount = CommonUtil.round(amount, 2);

		if (base != 0) {
			EDT edt = emp.getEdtSegment("EDTCA50");
			edt.setTipoElemento("CA");
			edt.setClave(50);
			edt.setBase(base);
			edt.setIndicadorFactorTipo(" ");
			// edt.setParteEnteraTipo(28);
			// edt.setParteDecimalFactorTipo(30000);
			edt.setImporte((new Double(CommonUtil.round(amount * 100))).intValue());
		}
	}
	
	/**
	51 Desempleo exclusivamente empresarial
	=BA22b (suma de bases excepto trabajadores con N en el campo 1271)
	b*t (desempleo, tipo exclusivamente empresarial)
	*/
	@Override
	public void createEDTCa51Segment(EMP emp) {
		// TODO 
	}
	
	/**
	52
	Fogasa y Formación Profesional Tipo total
	=BA02
	b*t (fogasa) + b*t (formación profesional total)
	*/
	@Override
	public void createEDTCa52Segment(Double desmplEnterpriseTotal, Double fogasaEnterpriseTotal, Double fpEnterpriseTotal, 
				Double desmplEmployeeTotal, Double fpEmployeeTotal, EMP emp) {

		Integer base = emp.getEdt().containsKey("EDTBA02") ? emp.getEdtSegment("EDTBA02").getBase() : 0;
		
		Double amount = 0.0;
		amount += fogasaEnterpriseTotal;
		amount += fpEnterpriseTotal;
		amount += fpEmployeeTotal;
		amount = CommonUtil.round(amount, 2);
		
		if (base != 0) {
			EDT edt = emp.getEdtSegment("EDTCA52");
			edt.setTipoElemento("CA");
			edt.setClave(52);
			edt.setBase(base);
			edt.setIndicadorFactorTipo(" ");
			// edt.setParteEnteraTipo(28);
			// edt.setParteDecimalFactorTipo(30000);
			edt.setImporte((new Double(CommonUtil.round(amount * 100))).intValue());
		}
	}
	
	/**
	53 Fogasa y Formación Profesional tipo empresarial
	=BA22b
	b*t (fogasa) + b*t (formación profesional exclusivamente empresarial)
	*/
	@Override
	public void createEDTCa53Segment(EMP emp) {
		// TODO
	}
	
	
	
	// ****************************
	// ****************************
	// TOTALES
	// ****************************
	// ****************************
	
	
	/**
	10 Liquido contingencias Generales
	=CA01i + CA02i - CA22i - CD29i + CA13
	*/
	@Override
	public void createEDTTt10Segment(EMP emp) {
		Integer amount = 0;
		amount += (emp.getEdt().containsKey("EDTCA01") && emp.getEdtSegment("EDTCA01").getImporte() != null ? emp.getEdtSegment("EDTCA01").getImporte() : 0);
		amount += (emp.getEdt().containsKey("EDTCA02") ? emp.getEdtSegment("EDTCA02").getImporte() : 0);
		amount -= (emp.getEdt().containsKey("EDTCA22") ? emp.getEdtSegment("EDTCA22").getImporte() : 0);
		amount -= (emp.getEdt().containsKey("EDTCD29") ? emp.getEdtSegment("EDTCD29").getImporte() : 0);
		amount += (emp.getEdt().containsKey("EDTCA13") ? emp.getEdtSegment("EDTCA13").getImporte() : 0);
		
		EDT edt = emp.getEdtSegment("EDTTT10");
		edt.setTipoElemento("TT");
		edt.setClave(10);
		edt.setCalificadorClave(null);
		edt.setBase(0);
		edt.setIndicadorFactorTipo(null);
		edt.setParteEnteraTipo(0);
		edt.setParteDecimalFactorTipo(0);
		edt.setImporte(amount);
		edt.setSigno(amount < 0 ? "-" : " ");
	}
	
	/**
	20 Liquido accidentes de trabajo y enfermedad profesional
	=CA30i
	*/
	@Override
	public void createEDTTt20Segment(EMP emp) {
		Integer amount = 0;
		amount += (emp.getEdt().containsKey("EDTCA30") && emp.getEdtSegment("EDTCA30").getImporte() != null ? emp.getEdtSegment("EDTCA30").getImporte() : 0);
		
		EDT edt = emp.getEdtSegment("EDTTT20");
		edt.setTipoElemento("TT");
		edt.setClave(20);
		edt.setCalificadorClave(null);
		edt.setBase(null);
		edt.setIndicadorFactorTipo(" ");
		edt.setParteEnteraTipo(0);
		edt.setParteDecimalFactorTipo(0);
		edt.setImporte(amount);
		edt.setSigno(amount < 0 ? "-" : " ");
	}
	
	/**
	30 Liquido otras cotizaciones
	=CA50i + CA51 + CA52 + CA53 - CA60i -CD30i
	*/
	@Override
	public void createEDTTt30Segment(EMP emp) {
		Integer amount = 0;
		amount += (emp.getEdt().containsKey("EDTCA50") && emp.getEdtSegment("EDTCA50").getImporte() != null ? emp.getEdtSegment("EDTCA50").getImporte() : 0);
		amount += (emp.getEdt().containsKey("EDTCA51") && emp.getEdtSegment("EDTCA51").getImporte() != null ? emp.getEdtSegment("EDTCA51").getImporte() : 0);
		amount += (emp.getEdt().containsKey("EDTCA52") && emp.getEdtSegment("EDTCA52").getImporte() != null ? emp.getEdtSegment("EDTCA52").getImporte() : 0);
		amount += (emp.getEdt().containsKey("EDTCA53") && emp.getEdtSegment("EDTCA53").getImporte() != null ? emp.getEdtSegment("EDTCA53").getImporte() : 0);
		amount -= (emp.getEdt().containsKey("EDTCA60") && emp.getEdtSegment("EDTCA60").getImporte() != null ? emp.getEdtSegment("EDTCA60").getImporte() : 0);
		amount -= (emp.getEdt().containsKey("EDTCD30") ? emp.getEdtSegment("EDTCD30").getImporte() : 0);

		EDT edt = emp.getEdtSegment("EDTTT30");
		edt.setTipoElemento("TT");
		edt.setClave(30);
		edt.setCalificadorClave(null);
		edt.setBase(null);
		edt.setIndicadorFactorTipo(" ");
		edt.setParteEnteraTipo(0);
		edt.setParteDecimalFactorTipo(0);
		edt.setImporte(amount);
		edt.setSigno(amount < 0 ? "-" : " ");
	}
	
	
}


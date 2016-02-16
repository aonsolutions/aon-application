package com.esferalia.aon.file.payroll.fan;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.file.payroll.fan.data.DAT;
import com.esferalia.aon.file.payroll.fan.data.EDL;
import com.esferalia.aon.file.payroll.fan.data.EDT;
import com.esferalia.aon.file.payroll.fan.data.EMP;
import com.esferalia.aon.file.payroll.fan.data.TRA;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.enumeration.ss.T33;

public class FANGeneral implements Serializable, IFanFactory {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public String getQuoteIndicator(List<ITransferObject> salaryDataList, Map<String, String> contractDataMap) {
		ContractCode code = getContractCode(salaryDataList, contractDataMap);
		if(code!=null && (code.getValue().startsWith("2") || code.getValue().startsWith("3") || code.getValue().startsWith("5"))){
			String weekHours = obtainWeekHours(salaryDataList, contractDataMap);
			if( StringUtils.isNotBlank(weekHours) ){
				return "H";
			}
		}
		return null;
	}
	
	@Override
	public String getQuoteMode(List<ITransferObject> list) {
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
	public Integer getContractDaysOrHours(Salary salary, List<ITransferObject> salaryDataList, Map<String, String> contractDataMap, Integer itDays, Date startDate, Date endDate) {
		Date contractStart = salary.getContract().getStartDate();
		Date contractEnd = salary.getContract().getEndDate();
		
		ContractCode code = getContractCode(salaryDataList, contractDataMap);
		if(code==null || code.getValue().startsWith("1") || code.getValue().startsWith("4")){
			Date start = startDate.before(contractStart)?contractStart:startDate;
			Date end = (contractEnd!=null && endDate.after(contractEnd))?contractEnd:endDate;
			int availableDays = (int) getAvailableDays(start, end);
			if(itDays!=null && itDays>0){
				availableDays = availableDays-itDays; 
				return (availableDays==0)?null:availableDays;
			}
			if( startDate.before(contractStart) || (contractEnd!=null && endDate.after(contractEnd)) ){
				return availableDays;
			} else {
				return 30;
			}
		} else {
			String workedHours = obtainWorkedHours(salaryDataList);
			long totalDays = getAvailableDays(startDate, endDate);
			Double dayHours = 0.0;
			
			if(NumberUtils.isNumber(workedHours)){
				dayHours = (Double.parseDouble(NumberUtils.isNumber(workedHours)?workedHours:"0")/totalDays);
			} else {
				String weekHours = obtainWeekHours(salaryDataList, contractDataMap);
				dayHours = (Double.parseDouble(NumberUtils.isNumber(weekHours)?weekHours:"0")/7);
			}
			
			if( startDate.before(contractStart) || (contractEnd!=null && endDate.after(contractEnd)) ){
				Date start = startDate.before(contractStart)?contractStart:startDate;
				Date end = (contractEnd!=null && endDate.after(contractEnd))?contractEnd:endDate;
				totalDays =  getAvailableDays(start, end);
			}
			
			if(itDays!=null && itDays>0){
				int days = Double.valueOf(CommonUtil.round((totalDays - itDays) * dayHours, 0)).intValue();
				return (days==0)?null:days;
			}
			
			return (totalDays * dayHours)<1?1:Double.valueOf(CommonUtil.round(totalDays * dayHours, 0)).intValue();
		}
	}
	
	protected long getAvailableDays(Date start, Date end) {
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
	
	private ContractCode getContractCode(List<ITransferObject> salaryDataList, Map<String, String> contractDataMap) {
		String o;
		o = null;
		List<ITransferObject> list = salaryDataList;
		for(ITransferObject to: list){
			SalaryData sa = (SalaryData) to;
			if(sa.getName().equals(ContextVariable.TC2.getName())){
				o = sa.getExpression();
			}
		}
		ContractCode code = ContractCode.getContractCodeByValue(o);
		return code!=null?code:ContractCode.getContractCodeByValue(contractDataMap.get(ContextVariable.TC2.getName()));
	}
	
	private String obtainWeekHours(List<ITransferObject> salaryDataList, Map<String, String> contractDataMap){
		String o = null;
		List<ITransferObject> list = salaryDataList;
		for(ITransferObject to: list){
			SalaryData sa = (SalaryData) to;
			if(sa.getName().equals(ContextVariable.WEEK_HOURS.getName())){
				o = sa.getExpression();
			}
		}
		if(o==null || !NumberUtils.isNumber(o)){
			return contractDataMap.get(ContextVariable.WEEK_HOURS.getName());
		}
		return o;
	}
	
	private String obtainWorkedHours(List<ITransferObject> salaryDataList){
		String o = null;
		List<ITransferObject> list = salaryDataList;
		for(ITransferObject to: list){
			SalaryData sa = (SalaryData) to;
			if(sa.getName().equals(ContextVariable.WORKED_HOURS.getName())){
				o = sa.getExpression();
			}
		}
		return o;
	}
	
	
	// *********************************************
	// *********************************************
	// BASES TRABAJADOR
	// *********************************************
	// *********************************************
	
	/**
	 * 0 Normal C. Comunes = AT y EP
	 * 
	 * @param salary
	 * @param dat
	 */
	public void createEDLBa00Segment(Double commonBase, DAT dat) {
		EDL edl = dat.getEdlSegment("BA00");
		createEDLRecord(edl, "BA", 0, new Double(commonBase * 100).intValue());
	}
	
	/**
	 * 1 Contingencias comunes
	 * 
	 * @param salary
	 * @param dat
	 */
	public void createEDLBa01Segment(Double commonBase, DAT dat) {
		EDL edl = dat.getEdlSegment("BA01");
		createEDLRecord(edl, "BA", 1, new Double(commonBase * 100).intValue());
	}
	
	/**
	 * 2 AT y EP
	 * 
	 * @param salary
	 * @param dat
	 */
	public void createEDLBa02Segment(Double professionalBase, DAT dat) {
		EDL edl = dat.getEdlSegment("BA02");
		createEDLRecord(edl, "BA", 2, new Double(professionalBase * 100).intValue());
	}
	
	public void createEDLBa05Segment() {
		// TODO 5 Exceso del tope (Minería del Carbón)
	}
	
	public void createEDLBa06Segment() {
		// TODO 6 Importe percepciones Integras (Artistas.)
	}
	
	public void createEDLBa07Segment() {
		// 7 AT y EP sin horas extraordinarias
		// No será de utilización para Régimen General de Artistas (0112), ni
		// Régimen Especial de Minería del Carbón (0911).
		// Baja a partir de 2002
	}
	
	public void createEDLBa08Segment() {
		// 8 Diferencia Bases (contingencias comunes y salario normalizado)
		// Solo para liquidaciones anteriores al año 2002 del Régimen Especial
		// de la Minería del Carbón (0911). Baja a partir de 2002
	}
	
	public void createEDLBa09Segment() {
		// TODO: 9 Horas complementarias No será de utilización para Régimen
		// General de Artistas (0112)
	}
	
	/**
	 * 10 Horas extras estructurales / Causa de fuerza mayor desde 1/1/98 No
	 * será de utilización para Régimen General de Artistas (0112), Régimen
	 * Especial de Minería del Carbón (0911).
	 * 
	 * @param salary
	 * @param dat
	 */
	public void createEDLBa10Segment(Double overtimeBase, DAT dat) {
		if ( overtimeBase != null && overtimeBase.compareTo(0.0d) > 0) {
			EDL edl = dat.getEdlSegment("BA10");
			createEDLRecord(edl, "BA", 10, new Double((overtimeBase) * 100).intValue());
		}
	}
	
	/**
	 * 11 Horas extras no estructurales / Otras horas extras desde 1/1/98 No
	 * será de utilización para Régimen General de Artistas (0112),), ni Régimen
	 * Especial de Minería del Carbón (0911)
	 * 
	 * @param salary
	 * @param dat
	 */
	public void createEDLBa11Segment(Double nonEstructuralOvertimeBase, DAT dat) {
		if ( nonEstructuralOvertimeBase != null && nonEstructuralOvertimeBase.compareTo(0.0d) > 0) {
			EDL edl = dat.getEdlSegment("BA11");
			createEDLRecord(edl, "BA", 11, new Double((nonEstructuralOvertimeBase) * 100).intValue());
		}
	}
	
	/**
	 * 20 Base de cotización empresarial C.Comunes = AT y EP
	 */
	public void createEDLBa20Segment(Double enterpriseBase, DAT dat) {
		EDL edl = dat.getEdlSegment("BA20");
		createEDLRecord(edl, "BA", 20, new Double(enterpriseBase * 100).intValue());
	}

	/**
	 * 21 Base de cotización empresarial por contingencias comunes 
	 * 
	 * @param salary
	 * @param dat
	 */
	public void createEDLBa21Segment(Double commonEnterpriseBase, DAT dat) {
		EDL edl = dat.getEdlSegment("BA21");
		createEDLRecord(edl, "BA", 21, new Double(commonEnterpriseBase * 100).intValue());
	}
	
	/**
	 *  22 Base de cotización empresarial por AT y EP y Otras Cotizaciones
	 * 
	 * @param salary
	 * @param dat
	 */
	public void createEDLBa22Segment(Double profesisonalEnterpriseBase, DAT dat) {
		EDL edl = dat.getEdlSegment("BA22");
		createEDLRecord(edl, "BA", 22, new Double(profesisonalEnterpriseBase * 100).intValue());
	}
	
	public void createEDLBa23Segment() {
		// 23 Base de cotización tipo total desempleo y FOGASA. (Baja a partir
		// del 1 de enero de 2012) (Régimen Especial Agrario)
	}
	
	public void createEDLBa28Segment() {
		// 28 Diferencia en bases (contingencias comunes y salario normalizado),
		// cotización exclusivamente empresarial
		// Solo para liquidaciones anteriores al año 2002 del Régimen Especial
		// de la Minería del Carbón (0911). Baja a partir de 2002
	}
	
	public void createEDLBa30Segment() {
		// 30 Cotización por Jornadas Reales. (Baja a partir del 1 de enero de
		// 2012) (Régimen Especial Agrario)
	}
	
	public void createEDLBa31Segment() {
		// 31 Contingencias comunes trajadores no fijos (Baja a partir del 1 de
		// enero de 2009) (Régimen Especial Agrario)
	}
	
	public void createEDLBa32Segment() {
		// 32 Base de cotización Jornadas Reales en situación de IT por
		// desempleo empresarial y FOGASA.
		// (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	}
	
	public void createEDLBa33Segment() {
		// 33 Base de cotización exclusiva de Otras Cotizaciones en situación de
		// IT/maternidad/riesgo durante el embarazo.
		// Trabajadores no fijos (Baja a partir del 1 de enero de 2009) (Régimen
		// Especial Agrario)
	}
	
	public void createEDLBa34Segment() {
		// 34 Base de AT en vacaciones. (Baja a partir del 1 de enero de 2012)
		// (Régimen Especial Agrario)
	}
	
	public void createEDLBa35Segment() {
		// 35 Base fija cotización trabajadores cuenta ajena extranjeros (Baja a
		// partir del 1 de enero de 2009) (Régimen Especial Agrario)
	}
	
	public void createEDLBa36Segment() {
		// 36 Base exclusiva Desempleo/FOGASA tipo total. (Baja a partir del 1
		// de enero de 2012) (Régimen Especial Agrario)
	}
	
	public void createEDLBa37Segment() {
		// 37 Cotización jornadas reales y FOGASA excluido desempleo(Baja a
		// partir del 1 de enero de 2012). (Régimen Especial Agrario)
	}
	
	public void createEDLBa38Segment() {
		// 38 Cotización exclusivamente por FOGASA. (Baja a partir del 1 de
		// enero de 2012) (Régimen Especial Agrario)
	}
	
	public void createEDLBa41Segment() {
		// 41 Contingencias Comunes y FOGASA, (Baja a partir del 1 de enero de
		// 2012) (Régimen Especial Agrario)
	}

	public void createEDLBa42Segment() {
		// TODO 42 Base exclusiva de AT y EP sin cotización de Otras
		// Cotizaciones
	}
	
	protected void createEDLRecord(EDL edl, String type, Integer key, Integer amount) {
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
	
	private String autoComplete(String value, int lenght, String completeValue, boolean leftSide) {
		while( value.length() < lenght ){
			value = leftSide ? (completeValue + value) : (value + completeValue);
		}
		return value;
	}
	
	
	// *********************************************
	// *********************************************
	// COMPENSACIONES - DEDUCCIONES TRABAJADOR
	// *********************************************
	// *********************************************
	
	/**
	 *  1 IT enfermedad común y accidente no laboral 
	 *  No es de aplicación en el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
	 * @param salary
	 * @param dat
	 */
	public void createEDLCd01Segment(Double ecssAmount, DAT dat) {
		Double amount = (-1) * ecssAmount;
		if ( Double.compare(amount,0.0d) > 0 ) {
			EDL edl = dat.getEdlSegment("CD01");
			createEDLRecord( edl, "CD", 1, new Double(CommonUtil.round(amount)*100).intValue());
		}
	}

	/**
	 *  3 IT por AT y EP 
	 *  No es de aplicación en el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
	 * @param salary
	 * @param dat
	 */
	public void createEDLCd03Segment(Double atepAmount, DAT dat) {
		Double amount = (-1) * atepAmount;
		if ( Double.compare(amount,0.0d) > 0 ) {
			EDL edl = dat.getEdlSegment("CD03");
			createEDLRecord(edl, "CD", 3, new Double(CommonUtil.round(amount)*100).intValue());
		}
	}
	
	public void createEDLCd05Segment(DAT dat) {
		// TODO 5 IT O.M. 3/4/73 Minería del Carbón
	}
	
	/**
	 *  6 Reducciones Contratos con derecho a reducción (casilla 209 de TC1)
	 * @param salary
	 * @param dat
	 */
	public void createEDLCd06Segment(Double bonusAmount, DAT dat) {
		EDL edl = dat.getEdlSegment("CD06");
		createEDLRecord(edl, "CD", 6, new Double(CommonUtil.round(bonusAmount)*100).intValue());
	}
	
	/**
	 *  7 Bonificaciones Contratos con derecho a bonificación/reducción (casilla 601 de TC1)
	 * @param bonus
	 * @param dat
	 */
	public void createEDLCd07Segment(Double bonusAmount, DAT dat) { 
		EDL edl = dat.getEdlSegment("CD07");
		createEDLRecord(edl, "CD", 7, new Double(CommonUtil.round(bonusAmount)*100).intValue());
	}
	
	/**
	 *  10 Bonificación por formación teórica presencial
	 * @param salary
	 * @param dat
	 */
	public void createEDLCd10Segment(Integer formationDays, Double bonusAmount, DAT dat) {
		EDL edl = dat.getEdlSegment("CD10");
		Integer amount = new Double(CommonUtil.round(bonusAmount)*100).intValue();
		Integer days = formationDays;
		createEDLRecord(edl, "CD", 10, days, amount);
	}
	
	/** 
	 *  11 Bonificación por formación teórica a distancia
	 * @param salary
	 * @param dat
	 */
	public void createEDLCd11Segment(Integer formationDays, Double bonusAmount, DAT dat) {
		EDL edl = dat.getEdlSegment("CD11");
		Integer amount = new Double(CommonUtil.round(bonusAmount)*100).intValue();
		Integer days = formationDays;
		createEDLRecord(edl, "CD", 11, days, amount);
	}
	
	/**
	 *  12 Bonificación por Ley 19/94 (Registro Canario) Régimen del Mar
	 * @param salary
	 * @param dat
	 */
	public void createEDLCd12Segment(Double bonusAmount, DAT dat) {
		EDL edl = dat.getEdlSegment("CD12");
		createEDLRecord(edl, "CD", 12, new Double(CommonUtil.round(bonusAmount)*100).intValue());
	}
	
	/**
	 *  13 Bonificación minusvalidos en Centros Especiales de Empleo
	 * @param salary
	 * @param dat
	 */
	public void createEDLCd13Segment(Double bonusAmount, DAT dat) {
		EDL edl = dat.getEdlSegment("CD13");
		createEDLRecord(edl, "CD", 13, new Double(CommonUtil.round(bonusAmount)*100).intValue());
	}
	
	public void createEDLCd16Segment(DAT dat) {
		// 16 Bonificación por trabajadores con 60 o más años (Baja a partir del 1 de agosto de 2012)
	}
	/**
	 *  17 Reducción contingencias comunes excepto I.T. (R.D.L. 16/2001)
	 * @param salary
	 * @param dat
	 */
	public void createEDLCd17Segment(Double bonusAmount, DAT dat) {
		EDL edl = dat.getEdlSegment("CD17");
		createEDLRecord(edl, "CD", 17, new Double(CommonUtil.round(bonusAmount)*100).intValue());
	}

	public void createEDLCd18Segment(DAT dat) {
		// 18 Reducción por Exención de desempleo (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario)
	}
	/**
	 *  20 Bonificación Ceuta y Melilla (O. TAS/471/2004)
	 * @param salary
	 * @param dat
	 */
	public void createEDLCd20Segment(Double bonusAmount, DAT dat) {
		EDL edl = dat.getEdlSegment("CD20");
		createEDLRecord(edl, "CD", 20, new Double(CommonUtil.round(bonusAmount)*100).intValue());
	}

	public void createEDLCd21Segment(DAT dat) {
		// 21 Bonificación Copa del America (R.D.L. 2146/2004) (Baja a partir del 1 de agosto de 2012)
	}
	
	/**
	 *  22 Bonificación Fom. Empleo Cuantía fija. Excepto Rég. Gral. Artistas (0112)
	 * @param salary
	 * @param dat
	 */
	public void createEDLCd22Segment(Integer bonusDays, Double bonusAmount, DAT dat) {
		EDL edl = dat.getEdlSegment("CD22");
		dat.setDiasAlta(bonusDays);
		createEDLRecord(edl, "CD", 22, bonusDays,new Double(CommonUtil.round(bonusAmount)*100).intValue());
	}
	
	/**
	 *  23 Bonificación Sector Industrial Incentivado
	 * @param salary
	 * @param dat
	 */
	public void createEDLCd23Segment(Double bonusAmount, DAT dat) {
		EDL edl = dat.getEdlSegment("CD23");
		createEDLRecord(edl, "CD", 23, new Double(CommonUtil.round(bonusAmount)*100).intValue());
	}
	
	public void createEDLCd24Segment(DAT dat) {
		// 24 Bonificación I+D+I (Baja a partir del 1 de agosto de 2012) Régimen General
	}

	/**
	 *  25 Exención de desempleo hijos<30años Autonomos
	 * @param salary
	 * @param dat
	 */
	public void createEDLCd25Segment(Double bonusAmount, DAT dat) {
		EDL edl = dat.getEdlSegment("CD25");
		createEDLRecord(edl, "CD", 25, new Double(CommonUtil.round(bonusAmount)*100).intValue());
	}

	public void createEDLCd26Segment(DAT dat) {
		// 26 Reducciones REA Cuantía mensual (modalidad G y J) (Baja a partir del 1 de enero de 2012) Régimen Especial Agrario 
	}
	
	public void createEDLCd27Segment(DAT dat) {
		// 27 Reducciones REA "Jornadas reales" (Baja a partir del 1 de enero de 2012) Régimen Especial Agrario 
	}

	/**
	 *  28 Bonificación por ERE 
	 * @param salary
	 * @param dat
	 */
	public void createEDLCd28Segment(Double bonusAmount, DAT dat) {
		EDL edl = dat.getEdlSegment("CD28");
		createEDLRecord(edl, "CD", 28, new Double(CommonUtil.round(bonusAmount)*100).intValue());
	}
	
	/**
	 *  29 Reducciones SEA. Contingencias comunes Sistema Especial Agrario 
	 * @param salary
	 * @param dat
	 */
	public void createEDLCd29Segment(Double cgcTotalEnterprise, Double cgcTotalEmployee, DAT dat, List<ITransferObject> salaryDataList) {
		
	}

	/**
	 *  30 Reducciones. SEA Desempleo
	 * @param salary
	 * @param dat
	 */
	public void createEDLCd30Segment(DAT dat) {
		
	}
	
	/**
	 * 31 Reducciones RDL-3/2014 
	 * Sólo para Regimen General,Régimen Especial del Mar y Régimen Especial de la Minería del Carbón
	 * 
	 * @param bonus
	 * @param emp
	 */
	public void createEDLCd31Segment(Double bonusAmount, DAT dat) {
		EDL edl = dat.getEdlSegment("CD31");
		createEDLRecord(edl, "CD", 31, new Double(CommonUtil.round(bonusAmount)*100).intValue());
	}
	
	/**
	 * 34 Reducción Tarifa Reducida R.D.L. 1/2015
	 * 
	 * @param bonus
	 * @param emp
	 */
	public void createEDLCd34Segment(Integer bonusDays, Double bonusAmount, DAT dat) {
		EDL edl = dat.getEdlSegment("CD34");
		dat.setDiasAlta(bonusDays);
		createEDLRecord(edl, "CD", 34, bonusDays, new Double(CommonUtil.round(bonusAmount)*100).intValue());
	}
	
	

	// *********************************************
	// *********************************************
	// BASES TOTALES
	// *********************************************
	// *********************************************
	
	/**
	 *  01 Contingencias comunes
	 * @param c
	 * @param emp
	 */
	public void createEDTBa01Segment(EMP emp) {
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
			edt.setBase(base);
		}
	}
	
	/**
	 *  02 AT y EP
	 */
	public void createEDTBa02Segment(EMP emp) {
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
			edt.setBase(base);
		}
	}
	
	public void createEDTBa05Segment(EMP emp) {
		// TODO 5 Exceso del tope (Minería del Carbón)
	}
	public void createEDTBa06Segment(EMP emp) {
		// TODO 6 Importe percepciones Integras (Artistas)
	}
	public void createEDTBa07Segment(EMP emp) {
		// 7 AT y EP sin horas extraordinarias
		// No será de utilización para Régimen General de Artistas (0112), ni Régimen Especial de Minería del Carbón (0911). Baja a partir de 2002
	}
	public void createEDTBa08Segment(EMP emp) {
		// 8 Diferencia Bases (Contingencias comunes y salario normalizado)
		// Solo para liquidaciones anteriores al año 2002 del Régimen Especial de la Minería del Carbón (0911). Baja a partir de 2002
	}
	public void createEDTBa09Segment(EnterpriseCCC ccc, EMP emp) {
		// TODO 09 Horas complementarias No se utilizará para Régimen General de Artistas (0112)
//		if(ccc.getActivity().getType() != SSRegimeType.ARTIST){
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
//		}
	}
	public void createEDTBa10Segment(EnterpriseCCC ccc, EMP emp) {
		// TODO enterpriseNonStructural && employeeNonStructural 
		// 10 Horas extras estructurales / Causa de fuerza mayor desde 1/1/98
		// No se podrá utilizar para el Régimen General de Artistas (0112), ni Régimen Especial de Minería del Carbón (0911).
//		if(ccc.getActivity().getType() != SSRegimeType.ARTIST && ccc.getActivity().getType() != SSRegimeType.COAL_MINING){
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
//		}
	}
	public void createEDTBa11Segment(EnterpriseCCC ccc, EMP emp) {
		// TODO enterpriseNonStructural && employeeNonStructural 
		// 11 Horas extras no estructurales / Otras horas extras desde 1/1/98
		// No se podrá utilizar para e Régimen General de Artistas (0112) , ni Régimen Especial de Minería del Carbón (0911)
//		if(ccc.getActivity().getType() != SSRegimeType.ARTIST && ccc.getActivity().getType() != SSRegimeType.COAL_MINING){
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
//		}
	}
	/**
	 *  21 Base de cotización empresarial por contingencias comunes
	 *  Base de cotización empresarial desempleo y FOGASA (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	 * @param contract
	 * @param emp
	 */
	public void createEDTBa21Segment(EnterpriseCCC ccc, EMP emp) {
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
	
	/**
	 *  22 Base de cotización empresarial por AT y EP y Otras Cotizaciones
	 * @param contract
	 * @param emp
	 */
	public void createEDTBa22Segment(EMP emp) {
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

	public void createEDTBa23Segment(EMP emp) {
		// 23 Base de cotización tipo total desempleo y FOGASA(Baja a partir del 1 de enero de 2012) . (Régimen Especial Agrario)
	}
	public void createEDTBa28Segment(EMP emp) {
		// 28 Diferencia en bases (contingencias comunes y salario normalizado), cotización exclusivamente empresarial
		// Solo para liquidaciones anteriores al año 2002 del Régimen Especial de la Minería del Carbón (0911). Baja a partir de 2002 
	}
	public void createEDTBa30Segment(EMP emp) {
		// 30 Cotización por Jornadas Reales (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	}
	public void createEDTBa31Segment(EMP emp) {
		// 31 Contingencias comunes trajadores no fijos (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario) 
	}
	public void createEDTBa32Segment(EMP emp) {
		// 32 Base de cotización Jornadas Reales en situación de IT por desempleo empresarial y FOGASA. 
		// (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	}
	public void createEDTBa33Segment(EMP emp) {
		// 33 Base de cotización exclusiva de Otras Cotizaciones en situación de IT/maternidad/riesgo durante el embarazo. 
		// Trabajadores no fijos (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario) 
	}
	public void createEDTBa34Segment(EMP emp) {
		// 34 Base de AT en vacaciones (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	}
	public void createEDTBa35Segment(EMP emp) {
		// 35 Base fija cotización trabajadores cuenta ajena extranjeros (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario) 
	}
	public void createEDTBa36Segment(EMP emp) {
		// 36 Base exclusiva Desempleo/FOGASA tipo total (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario) 
	}
	public void createEDTBa37Segment(EMP emp) {
		// 37 Cotización jornadas reales y FOGASA excluido desempleo(Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario) 
	}
	public void createEDTBa38Segment(EMP emp) {
		// 38 Cotización exclusivamente por FOGASA(Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario) 
	}
	public void createEDTBa41Segment(EMP emp) {
		// 41 Contingencias Comunes y FOGASA(Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	}
	/**
	 *  42 Base exclusiva de AT y EP sin cotización de Otras Cotizaciones
	 * @param contract
	 * @param emp
	 */
	public void createEDTBa42Segment(EMP emp) {
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
	
	
	

	// *********************************************
	// *********************************************
	// COMPENSACION - DEDUCCION TOTALES
	// *********************************************
	// *********************************************

	/**
	 * 01 IT enfermedad común y accidente no laboral No es de aplicación en el
	 * Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
	 * 
	 * @param contract
	 * @param emp
	 */
	public void createEDTCd01Segment(EnterpriseCCC ccc, EMP emp) {
		Integer amount = 0;
		for (TRA tra : emp.getTrabajadores()) {
			for (DAT dat : tra.getDat()) {
				amount += dat.getEdl().containsKey("CD01") ? dat.getEdlSegment("CD01").getImporte() : 0;
			}
		}
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCD01");
			edt.setTipoElemento("CD");
			edt.setClave(1);
			edt.setImporte(amount);
		}
	}
	
	/**
	 * 03 IT por AT y EP No es de aplicación en el Régimen General de Artistas
	 * (0112) y Régimen Especial Agrario (0613)
	 * 
	 * @param contract
	 * @param emp
	 */
	public void createEDTCd03Segment(EnterpriseCCC ccc, EMP emp) {
		Integer amount = 0;
		for (TRA tra : emp.getTrabajadores()) {
			for (DAT dat : tra.getDat()) {
				amount += dat.getEdl().containsKey("CD03") ? dat.getEdlSegment("CD03").getImporte() : 0;
			}
		}
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCD03");
			edt.setTipoElemento("CD");
			edt.setClave(3);
			edt.setImporte(amount);
		}
	}

	public void createEDTCd05Segment(EMP emp) {
		// TODO 5 IT O.M. 3/4/73. Minería del Carbón
	}
	
	/**
	 * 06 Reducciones
	 * 
	 * @param contract
	 * @param emp
	 */
	public void createEDTCd06Segment(EMP emp) {
		Integer amount = 0;
		for (TRA tra : emp.getTrabajadores()) {
			for (DAT dat : tra.getDat()) {
				amount += dat.getEdl().containsKey("CD06") ? dat.getEdlSegment("CD06").getImporte() : 0;
			}
		}
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCD06");
			edt.setTipoElemento("CD");
			edt.setClave(6);
			edt.setImporte(amount);
		}
	}
	
	/**
	 * 07 Bonificaciones
	 * 
	 * @param contract
	 * @param emp
	 */
	public void createEDTCd07Segment(EMP emp) {
		Integer amount = 0;
		for (TRA tra : emp.getTrabajadores()) {
			for (DAT dat : tra.getDat()) {
				amount += dat.getEdl().containsKey("CD07") ? dat.getEdlSegment("CD07").getImporte() : 0;
			}
		}
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCD07");
			edt.setTipoElemento("CD");
			edt.setClave(7);
			edt.setImporte(amount);
		}
	}

	/**
	 * 10 Bonificación por formación teórica presencial
	 * 
	 * @param c
	 * @param emp
	 */
	public void createEDTCd10Segment(EMP emp) {
		Integer amount = 0;
		for (TRA tra : emp.getTrabajadores()) {
			for (DAT dat : tra.getDat()) {
				amount += dat.getEdl().containsKey("CD10") ? dat.getEdlSegment("CD10").getImporte() : 0;
			}
		}
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCD10");
			edt.setTipoElemento("CD");
			edt.setClave(10);
			edt.setImporte(amount);
		}
	}
	
	/**
	 * 11 Bonificación por formación teórica a distancia
	 * 
	 * @param c
	 * @param emp
	 */
	public void createEDTCd11Segment(EMP emp) {
		Integer amount = 0;
		for (TRA tra : emp.getTrabajadores()) {
			for (DAT dat : tra.getDat()) {
				amount += dat.getEdl().containsKey("CD11") ? dat.getEdlSegment("CD11").getImporte() : 0;
			}
		}
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCD11");
			edt.setTipoElemento("CD");
			edt.setClave(11);
			edt.setImporte(amount);
		}
	}

	public void createEDTCd12Segment(EMP emp) {
		// TODO 12 Bonificación por Ley 19/94 (Registro Canario) Régimen
		// Especial del Mar
	}

	/**
	 * 13 Bonificación minusvalidos en Centros Especiales de Empleo
	 * 
	 * @param c
	 * @param emp
	 */
	public void createEDTCd13Segment(EMP emp) {
		Integer amount = 0;
		for (TRA tra : emp.getTrabajadores()) {
			for (DAT dat : tra.getDat()) {
				amount += dat.getEdl().containsKey("CD13") ? dat.getEdlSegment("CD13").getImporte() : 0;
			}
		}
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCD13");
			edt.setTipoElemento("CD");
			edt.setClave(13);
			edt.setImporte(amount);
		}
	}	

	/**
	 * 16 Bonificación por trabajadores con 60 o más años
	 * 
	 * @param c
	 * @param emp
	 */
	public void createEDTCd16Segment(EMP emp) {
		Integer amount = 0;
		for (TRA tra : emp.getTrabajadores()) {
			for (DAT dat : tra.getDat()) {
				amount += dat.getEdl().containsKey("CD16") ? dat.getEdlSegment("CD16").getImporte() : 0;
			}
		}
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCD16");
			edt.setTipoElemento("CD");
			edt.setClave(16);
			edt.setImporte(amount);
		}
	}

	/**
	 * 17 Reducción contingencias comunes excepto I.T. (R.D.L. 16/2001) No es de
	 * aplicación en el Régimen Especial Agrario (0613)
	 * 
	 * @param contract
	 * @param emp
	 */
	public void createEDTCd17Segment(EnterpriseCCC ccc, EMP emp) {
		Integer amount = 0;
		for (TRA tra : emp.getTrabajadores()) {
			for (DAT dat : tra.getDat()) {
				amount += dat.getEdl().containsKey("CD17") ? dat.getEdlSegment("CD17").getImporte() : 0;
			}
		}
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCD17");
			edt.setTipoElemento("CD");
			edt.setClave(17);
			edt.setImporte(amount);
		}
	}

	public void createEDTCd18Segment(EMP emp) {
		// 18 Reducción por Exencón de desempleo (Baja a partir del 1 de enero
		// de 2009) (Régimen Especial Agrario)
	}

	/**
	 * 20 Bonificación Ceuta y Melilla (O. TAS/471/2004)
	 * 
	 * @param c
	 * @param emp
	 */
	public void createEDTCd20Segment(EMP emp) {
		Integer amount = 0;
		for (TRA tra : emp.getTrabajadores()) {
			for (DAT dat : tra.getDat()) {
				amount += dat.getEdl().containsKey("CD20") ? dat.getEdlSegment("CD20").getImporte() : 0;
			}
		}
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCD20");
			edt.setTipoElemento("CD");
			edt.setClave(20);
			edt.setImporte(amount);
		}
	}

	/**
	 * 21 Bonificación Copa del America (R.D.L. 2146/2004)
	 * 
	 * @param c
	 * @param emp
	 */
	public void createEDTCd21Segment(EMP emp) {
		Integer amount = 0;
		for (TRA tra : emp.getTrabajadores()) {
			for (DAT dat : tra.getDat()) {
				amount += dat.getEdl().containsKey("CD21") ? dat.getEdlSegment("CD21").getImporte() : 0;
			}
		}
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCD21");
			edt.setTipoElemento("CD");
			edt.setClave(21);
			edt.setImporte(amount);
		}
	}	

	/**
	 * 22 Bonificación Form. Empleo Cuantía fija. Excepto Rég. Gral. Artistas
	 * (0112)
	 */
	public void createEDTCd22Segment(EnterpriseCCC ccc, EMP emp) {
		Integer amount = 0;
		for (TRA tra : emp.getTrabajadores()) {
			for (DAT dat : tra.getDat()) {
				amount += dat.getEdl().containsKey("CD22") ? dat.getEdlSegment("CD22").getImporte() : 0;
			}
		}
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCD22");
			edt.setTipoElemento("CD");
			edt.setClave(22);
			edt.setImporte(amount);
		}
	}

	/**
	 * 23 Bonificación Sector industrial incentivado
	 * 
	 * @param c
	 * @param emp
	 */
	public void createEDTCd23Segment(EMP emp) {
		Integer amount = 0;
		for (TRA tra : emp.getTrabajadores()) {
			for (DAT dat : tra.getDat()) {
				amount += dat.getEdl().containsKey("CD23") ? dat.getEdlSegment("CD23").getImporte() : 0;
			}
		}
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCD23");
			edt.setTipoElemento("CD");
			edt.setClave(23);
			edt.setImporte(amount);
		}
	}	

	/**
	 * 24 Bonificación I+D+I Régimen General (0111)
	 * 
	 * @param contract
	 * @param emp
	 */
	public void createEDTCd24Segment(EnterpriseCCC ccc, EMP emp) {
		Integer amount = 0;
		for (TRA tra : emp.getTrabajadores()) {
			for (DAT dat : tra.getDat()) {
				amount += dat.getEdl().containsKey("CD24") ? dat.getEdlSegment("CD24").getImporte() : 0;
			}
		}
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCD24");
			edt.setTipoElemento("CD");
			edt.setClave(24);
			edt.setImporte(amount);
		}
	}

	/**
	 * 25 Exención de desempleo hijos<30años Autonomos
	 * 
	 * @param contract
	 * @param emp
	 */
	public void createEDTCd25Segment(EnterpriseCCC ccc, EMP emp) {
		if (ccc.getActivity().getType() == SSRegimeType.SELF_EMPLOYED) {
			Integer amount = 0;
			for (TRA tra : emp.getTrabajadores()) {
				for (DAT dat : tra.getDat()) {
					amount += dat.getEdl().containsKey("CD25") ? dat.getEdlSegment("CD25").getImporte() : 0;
				}
			}
			if (amount != 0) {
				EDT edt = emp.getEdtSegment("EDTCD25");
				edt.setTipoElemento("CD");
				edt.setClave(25);
				edt.setImporte(amount);
			}
		}
	}
	
	public void createEDTCd26Segment(EMP emp) {
		// 26 Reducciones REA Cuantía mensual (modalidad G y J). (Baja a partir
		// del 1 de enero de 2012) Régimen Especial Agrario
	}

	public void createEDTCd27Segment(EMP emp) {
		// 27 Reducciones REA "Jornadas reales". (Baja a partir del 1 de enero
		// de 2012) Régimen Especial Agrario
	}
	
	/**
	 * 28 Bonificación por ERE
	 * 
	 * @param c
	 * @param emp
	 */
	public void createEDTCd28Segment(EMP emp) {
		Integer amount = 0;
		for (TRA tra : emp.getTrabajadores()) {
			for (DAT dat : tra.getDat()) {
				amount += dat.getEdl().containsKey("CD28") ? dat.getEdlSegment("CD28").getImporte() : 0;
			}
		}
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCD28");
			edt.setTipoElemento("CD");
			edt.setClave(28);
			edt.setImporte(amount);
		}
	}

	/**
	 * 29 Reducciones SEA. Contingencias comunes Sistema Especial Agrario
	 * 
	 * @param emp
	 */
	public void createEDTCd29Segment(EMP emp) {
		Integer amount = 0;
		for (TRA tra : emp.getTrabajadores()) {
			for (DAT dat : tra.getDat()) {
				amount += dat.getEdl().containsKey("CD29") ? dat.getEdlSegment("CD29").getImporte() : 0;
			}
		}
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCD29");
			edt.setTipoElemento("CD");
			edt.setClave(29);
			edt.setImporte(amount);
		}
	}	

	/**
	 * 30 Reducciones. SEA Desempleo Sistema Especial Agrario
	 * 
	 * @param emp
	 */
	public void createEDTCd30Segment(EMP emp) {
		Integer amount = 0;
		for (TRA tra : emp.getTrabajadores()) {
			for (DAT dat : tra.getDat()) {
				amount += dat.getEdl().containsKey("CD30") ? dat.getEdlSegment("CD30").getImporte() : 0;
			}
		}
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCD30");
			edt.setTipoElemento("CD");
			edt.setClave(30);
			edt.setImporte(amount);
		}
	}

	/**
	 * 31 Reducciones RDL-3/2014 
	 * 
	 * Sólo para Regimen General, 
	 * Régimen Especial del Mar y 
	 * Régimen Especial de la Minería del Carbón
	 * 
	 * @param ccc
	 * @param emp
	 */
	public void createEDTCd31Segment(EnterpriseCCC ccc, EMP emp) {
		Integer amount = 0;
		for (TRA tra : emp.getTrabajadores()) {
			for (DAT dat : tra.getDat()) {
				amount += dat.getEdl().containsKey("CD31") ? dat.getEdlSegment("CD31").getImporte() : 0;
			}
		}
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCD31");
			edt.setTipoElemento("CD");
			edt.setClave(31);
			edt.setImporte(amount);
		}
	}	
	
	/**
	 * 34 Reducción Tarifa Reducida RDL 1/2015 
	 * 	 
	 * Sólo para Régimen General, 
	 * Régimen Especial del Mar y 
	 * Régimen Especial de la Minería del Carbón. 
	 * 
	 * @param ccc
	 * @param emp
	 */
	public void createEDTCd34Segment(EnterpriseCCC ccc, EMP emp) {
		Integer amount = 0;
		for (TRA tra : emp.getTrabajadores()) {
			for (DAT dat : tra.getDat()) {
				amount += dat.getEdl().containsKey("CD34") ? dat.getEdlSegment("CD34").getImporte() : 0;
			}
		}
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCD34");
			edt.setTipoElemento("CD");
			edt.setClave(34);
			edt.setImporte(amount);
		}
	}	
	
	
	
	// *********************************************
	// *********************************************
	// ELEMENTO CALCULADO TOTALES
	// *********************************************
	// *********************************************

	/**
	 * 01 Contingencias Comunes
	 * 
	 * @param cgcTotalEnterprise
	 * @param cgcTotalEmployee
	 * @param emp
	 */
	public void createEDTCa01Segment(Double cgcTotalEnterprise, Double cgcTotalEmployee, EMP emp) {
		Integer base = emp.getEdt().containsKey("EDTBA01") ? emp.getEdtSegment("EDTBA01").getBase() : 0;
	
		Double amount = 0.0;
		amount += cgcTotalEnterprise;
		amount += cgcTotalEmployee;
		amount = CommonUtil.round(amount, 2);

		if (base != 0) {
			EDT edt = emp.getEdtSegment("EDTCA01");
			edt.setTipoElemento("CA");
			edt.setClave(1);
			edt.setCalificadorClave(null);
			edt.setBase(base);
			edt.setIndicadorFactorTipo("T");
			edt.setParteEnteraTipo(28);
			edt.setParteDecimalFactorTipo(30000);
			edt.setImporte((new Double(CommonUtil.round(amount * 100))).intValue());
			edt.setSigno(" ");
		}
	}

	/**
	 * 02 Cuota empresarial por Contingencias Comunes
	 * Cotización empresarial / Toneladas Régimen Especial de Manipulado y Empaquetado de Tomate Fresco (0134)
	 * 
	 * @param cgcTotalEnterprise
	 * @param cgcTotalEmployee
	 * @param emp
	 */
	public void createEDTCa02Segment(Double cgcOnlyEnterprise, EMP emp) {
		Integer base = emp.getEdt().containsKey("EDTBA21") ? emp.getEdtSegment("EDTBA21").getBase() : 0;
		
		Double amount = 0.0;
		amount += cgcOnlyEnterprise;
		amount = CommonUtil.round(amount, 2);
		
		if (base != 0) {
			EDT edt = emp.getEdtSegment("EDTCA02");
			edt.setTipoElemento("CA");
			edt.setClave(2);
			edt.setCalificadorClave(null);
			edt.setBase(base);
			edt.setIndicadorFactorTipo("T");
			edt.setParteEnteraTipo(23);
			edt.setParteDecimalFactorTipo(60000);
			edt.setImporte((new Double(CommonUtil.round(amount * 100))).intValue());
			edt.setSigno(" ");
		}
	}

	public void createEDTCa03Segment(EMP emp) {
		// 03 Cuota fija trabajador cuenta ajena extranjero (Baja a partir del 1
		// de enero de 2009) Es de aplicación solo para el Régimen Especial
		// Agrario
	}

	/**
	 * 11 Otros conceptos calificador de clave: 4 Asistencia sanitaria de
	 * Administraciones Públicas 8 Cotización adicional Ex.-MUNPAL 6
	 * Contratación inferior a 7 días Para contratos de duración efectiva
	 * inferior de 7 días, a los que es de aplicación el incremento del 36% de
	 * la cotización empresarial por contingencias comunes, establecido en la
	 * Ley 12/2001 14 Cotización adicional Ex-Munpal y contratación inferior a 7
	 * días Se utilizará cuando coincida la cotización adicional por clave 8 y
	 * por clave 6 15 Cotización adicional Bomberos al servicio de las
	 * Administraciones y Organismos Públicos
	 * 
	 * @param emp
	 */
	public void createEDTCa11Segment(Double lessThanSevenDaysContractAmount, EMP emp) {
		// TODO

		Double amount = 0.0;
		amount += lessThanSevenDaysContractAmount;
		amount = CommonUtil.round(amount, 2);

		if (amount != 0) {
			Integer base = (int) (CommonUtil.round((amount / 0.36)) * 100);

			EDT edt = emp.getEdtSegment("EDTCA11");
			edt.setTipoElemento("CA");
			edt.setClave(11);
			edt.setCalificadorClave(Integer.parseInt(T33.T33_6.getCode()));
			edt.setBase(base);
			edt.setIndicadorFactorTipo("T");
			edt.setParteEnteraTipo(36);
			edt.setParteDecimalFactorTipo(00000);
			edt.setImporte((new Double(CommonUtil.round(amount * 100))).intValue());
			edt.setSigno(" ");
		}
	}

	public void createEDTCa12Segment(EMP emp) {
		// TODO 12 Aportación a los servicios comunes
		// No es de aplicación en el Régimen General de Artistas (0112) y
		// Régimen Especial Agrario (0613)
	}

	public void createEDTCa20Segment(EMP emp) {
		// TODO 20 Deducción por contingencias excluidas
		// No es de aplicación para el Régimen General de Artistas (0112) y
		// Régimen Especial Agrario (0613)
	}

	public void createEDTCa21Segment(EMP emp) {
		// TODO 21 Deducción colaboración voluntaria enfermedades comunes y
		// accidente no laboral
		// No es de aplicación para el Régimen General de Artistas (0112) y
		// Régimen Especial Agrario (0613)
	}

	/**
	 * 22 Suma de compensaciones y reducciones
	 * 
	 * @param emp
	 */
	public void createEDTCa22Segment(EMP emp) {
		Integer amount = 0;
		amount += emp.getEdt().containsKey("EDTCD01") ? emp.getEdtSegment("EDTCD01").getImporte() : 0;
		amount += emp.getEdt().containsKey("EDTCD06") ? emp.getEdtSegment("EDTCD06").getImporte() : 0;
		amount += emp.getEdt().containsKey("EDTCD17") ? emp.getEdtSegment("EDTCD17").getImporte() : 0;
		amount += emp.getEdt().containsKey("EDTCD31") ? emp.getEdtSegment("EDTCD31").getImporte() : 0;
		amount += emp.getEdt().containsKey("EDTCD34") ? emp.getEdtSegment("EDTCD34").getImporte() : 0;
		
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
	 * 30 Total cuotas AT y EP
	 * 
	 * @param emp
	 */
	public void createEDTCa30Segment(EMP emp) {
		Integer amount = 0;
		amount += emp.getEdt().containsKey("EDTCA31") ? emp.getEdtSegment("EDTCA31").getImporte() : 0;
		amount += emp.getEdt().containsKey("EDTCA32") ? emp.getEdtSegment("EDTCA32").getImporte() : 0;
		
		EDT edt = emp.getEdtSegment("EDTCA30");
		edt.setTipoElemento("CA");
		edt.setClave(30);
		edt.setCalificadorClave(null);
		edt.setBase(null);
		edt.setImporte(amount);
		edt.setSigno(" ");
	}

	/**
	 * 31 Cuotas por Incapacidad Temporal por AT y EP
	 * 
	 * @param emp
	 */
	public void createEDTCa31Segment(Double itTotal, EMP emp) {
		Double amount = 0.0;
		amount = itTotal;
		amount = CommonUtil.round(amount, 2);

		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCA31");
			edt.setTipoElemento("CA");
			edt.setClave(31);
			edt.setCalificadorClave(null);
			edt.setBase(null);
			edt.setImporte((new Double(CommonUtil.round(amount * 100))).intValue());
			edt.setSigno(" ");
		}
	}

	/**
	 * 32 Cuotas por Invalidez, muerte y supervivencia (IMS) por AT y EP
	 * 
	 * @param ccc
	 * @param emp
	 */
	public void createEDTCa32Segment(Double imsTotal, EMP emp) {
		Double amount = 0.0;
		amount = imsTotal;
		amount = CommonUtil.round(amount, 2);

		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCA32");
			edt.setTipoElemento("CA");
			edt.setClave(32);
			edt.setImporte((new Double(CommonUtil.round(amount * 100))).intValue());
			edt.setSigno(" ");
		}
	}

	/**
	 * 50 Otras cotizaciones (Desempleo, FOGASA y Formación Profesional) Resto
	 * de regímenes excepto Régimen Especial del Mar, Régimen Especial de
	 * Manipulado y Empaquetado y Tomate Fresco y Régimen Especial Agrario
	 * (0613).
	 * 
	 * @param ccc
	 * @param emp
	 */
	public void createEDTCa50Segment(Double desmplEnterpriseTotal, Double fogasaEnterpriseTotal, Double fpEnterpriseTotal, 
			Double desmplEmployeeTotal, Double fpEmployeeTotal, EMP emp) {
		Integer base = emp.getEdt().containsKey("EDTBA02") ? emp.getEdtSegment("EDTBA02").getBase() : 0;
		
		Double amount = 0.0;
		amount += desmplEnterpriseTotal;
		amount += fogasaEnterpriseTotal;
		amount += fpEnterpriseTotal;
		amount += desmplEmployeeTotal;
		amount += fpEmployeeTotal;
		amount = CommonUtil.round(amount, 2);

		if (base != 0) {
			EDT edt = emp.getEdtSegment("EDTCA50");
			edt.setTipoElemento("CA");
			edt.setClave(50);
			edt.setBase(base);
			edt.setIndicadorFactorTipo("T");
			edt.setParteEnteraTipo(0);
			edt.setParteDecimalFactorTipo(0);
			edt.setImporte((new Double(CommonUtil.round(amount * 100))).intValue());
		}
	}

	public void createEDTCa51Segment(EMP emp) {
		// TODO 51 Otras cotizaciones (Desempleo) (Tc1/16) - Régimen Especial
		// del Mar
		// Otra cotizaciones (Desempleo y Formación Profesional cuota obrera)
		// (TC1/25) - Sistema Especial de Manipulado y Empaquetado de Tomate
		// Fresco
	}

	public void createEDTCa52Segment(Double desmplEnterpriseTotal, Double fogasaEnterpriseTotal, Double fpEnterpriseTotal, 
			Double desmplEmployeeTotal, Double fpEmployeeTotal, EMP emp) {
		// TODO 52 Otras cotizaciones (FOGASA y Formación Profesional) (TC1/16)
		// - Régimen Especial del Mar
		// Otras Cotizaciones (FOGASA) (TC1/25) - Sistema Especial de Manipulado
		// y Empaquetado de Tomate Fresco
	}

	public void createEDTCa53Segment(EMP emp) {
		// TODO 53 Total Otras Cotizaciones (Desempleo, FOGASA y Formación
		// Profesional) (TC1/16) - Régimen Especial del Mar
		// Total Otras Cotizaciones (Desempleo, FOGASA y Formación Profesional
		// (TC1/25) - Sistema Especial de Manipulado y Empaquetado de Tomate
		// Fresco
	}

	public void createEDTCa54Segment(EMP emp) {
		// TODO 54 Cotización empresarial por desempleo ( TC1/16) - Régimen
		// Especial del Mar
		// Cotización empresarial por Desempleo y Formación Profesional (TC1/25)
		// - Sistema Especial de Manipulado y Empaquetado de Tomate Fresco
	}

	public void createEDTCa55Segment(EMP emp) {
		// TODO 55 Cotización empresarial por Fogasa y FP (TC1/16) Régimen
		// Especial del Mar
	}

	public void createEDTCa56Segment(EMP emp) {
		// TODO 56 Total Otras Cotizaciones cuota empresarial (TC1/16) Régimen
		// Especial del Mar
	}

	/**
	 * 57 Cuota empresarial por Otras Cotizaciones
	 * 
	 */
	public void createEDTCa57Segment(Double desmplOnlyEnterpriseTotal, Double fogasaOnlyEnterpriseTotal,
			Double fpOnlyEnterpriseTotal, EMP emp) {
		
		Integer base = emp.getEdt().containsKey("EDTBA22") ? emp.getEdtSegment("EDTBA22").getBase() : 0;

		Double amount = 0.0;
		amount += desmplOnlyEnterpriseTotal;
		amount += fogasaOnlyEnterpriseTotal;
		amount += fpOnlyEnterpriseTotal;
		amount = CommonUtil.round(amount, 2);

		if (base != 0) {
			EDT edt = emp.getEdtSegment("EDTCA57");
			edt.setTipoElemento("CA");
			edt.setClave(57);
			edt.setBase(base);
			edt.setIndicadorFactorTipo("T");
			edt.setParteEnteraTipo(0);
			edt.setParteDecimalFactorTipo(0);
			edt.setImporte((new Double(CommonUtil.round(amount * 100))).intValue());
		}
	}

	/**
	 * 60 Suma de bonificaciones, subvenciones y compensaciones
	 * 
	 * @param emp
	 */
	public void createEDTCa60Segment(EMP emp) {
		Integer amount = 0;
		amount += emp.getEdt().containsKey("EDTCD07") ? emp.getEdtSegment("EDTCD07").getImporte() : 0;
		amount += emp.getEdt().containsKey("EDTCD10") ? emp.getEdtSegment("EDTCD10").getImporte() : 0;
		amount += emp.getEdt().containsKey("EDTCD11") ? emp.getEdtSegment("EDTCD11").getImporte() : 0;
		amount += emp.getEdt().containsKey("EDTCD13") ? emp.getEdtSegment("EDTCD13").getImporte() : 0;
		amount += emp.getEdt().containsKey("EDTCD16") ? emp.getEdtSegment("EDTCD16").getImporte() : 0;
		amount += emp.getEdt().containsKey("EDTCD20") ? emp.getEdtSegment("EDTCD20").getImporte() : 0;
		amount += emp.getEdt().containsKey("EDTCD21") ? emp.getEdtSegment("EDTCD21").getImporte() : 0;
		amount += emp.getEdt().containsKey("EDTCD22") ? emp.getEdtSegment("EDTCD22").getImporte() : 0;
		amount += emp.getEdt().containsKey("EDTCD23") ? emp.getEdtSegment("EDTCD23").getImporte() : 0;
		amount += emp.getEdt().containsKey("EDTCD25") ? emp.getEdtSegment("EDTCD25").getImporte() : 0;
		amount += emp.getEdt().containsKey("EDTCA28") ? emp.getEdtSegment("EDTCA28").getImporte() : 0;
		amount += emp.getEdt().containsKey("EDTCA80") ? emp.getEdtSegment("EDTCA80").getImporte() : 0;
		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCA60");
			edt.setTipoElemento("CA");
			edt.setClave(60);
			edt.setCalificadorClave(null);
			edt.setBase(null);
			edt.setImporte(amount);
			edt.setSigno(" ");
		}
	}

	/**
	 * 80 Bonificación INEM formación continua
	 * 
	 * @param ccc
	 * @param emp
	 */
	public void createEDTCa80Segment(Double continuousFormationTotal, EMP emp) {
		Double amount = 0.0;
		amount = continuousFormationTotal;
		amount = CommonUtil.round(amount, 2);

		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCA80");
			edt.setTipoElemento("CA");
			edt.setClave(80);
			edt.setBase(0);
			edt.setIndicadorFactorTipo(" ");
			edt.setParteEnteraTipo(null);
			edt.setParteDecimalFactorTipo(null);
			edt.setImporte((new Double(CommonUtil.round(amount * 100))).intValue());
		}
	}

	public void createEDTCa90Segment(EMP emp) {
		// TODO 90 Recargo de mora
	}

	
	
	
	// *********************************************
	// *********************************************
	// TOTALES
	// *********************************************
	// *********************************************
	// 
	/**
	 * 10 Liquido contingencias generales
	 * 
	 * @param emp
	 */
	public void createEDTTt10Segment(EMP emp) {
		Integer amount = 0;
		amount += (emp.getEdt().containsKey("EDTCA01") && emp.getEdtSegment("EDTCA01").getImporte() != null ? emp.getEdtSegment("EDTCA01").getImporte() : 0);
		amount += (emp.getEdt().containsKey("EDTCA02") ? emp.getEdtSegment("EDTCA02").getImporte() : 0);
		amount += (emp.getEdt().containsKey("EDTCA11") ? emp.getEdtSegment("EDTCA11").getImporte() : 0);
		amount += (emp.getEdt().containsKey("EDTCA12") ? emp.getEdtSegment("EDTCA12").getImporte() : 0);
		amount -= (emp.getEdt().containsKey("EDTCA20") ? emp.getEdtSegment("EDTCA20").getImporte() : 0);
		amount -= (emp.getEdt().containsKey("EDTCA21") ? emp.getEdtSegment("EDTCA21").getImporte() : 0);
		amount -= (emp.getEdt().containsKey("EDTCA22") ? emp.getEdtSegment("EDTCA22").getImporte() : 0);
		amount += (emp.getEdt().containsKey("EDTBA10") ? emp.getEdtSegment("EDTBA10").getImporte() : 0);
		amount += (emp.getEdt().containsKey("EDTBA11") ? emp.getEdtSegment("EDTBA11").getImporte() : 0);

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
	 * 20 Liquido accidentes de trabajo y enfermedad profesional
	 */
	public void createEDTTt20Segment(EMP emp) {
		Integer amount = 0;
		amount += (emp.getEdt().containsKey("EDTCA30") && emp.getEdtSegment("EDTCA30").getImporte() != null ? emp.getEdtSegment("EDTCA30").getImporte() : 0);
		amount -= (emp.getEdt().containsKey("EDTCD03") ? emp.getEdtSegment("EDTCD03").getImporte() : 0);
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
	 * 30 Liquido otras cotizaciones
	 * 
	 * @param emp
	 */
	public void createEDTTt30Segment(EMP emp) {
		Integer amount = 0;
		amount += (emp.getEdt().containsKey("EDTCA50") && emp.getEdtSegment("EDTCA50").getImporte() != null ? emp.getEdtSegment("EDTCA50").getImporte() : 0);
		amount += (emp.getEdt().containsKey("EDTCA57") ? emp.getEdtSegment("EDTCA57").getImporte() : 0);
		amount -= (emp.getEdt().containsKey("EDTCA60") && emp.getEdtSegment("EDTCA60").getImporte() != null ? emp.getEdtSegment("EDTCA60").getImporte() : 0);
		amount -= (emp.getEdt().containsKey("EDTCD24") ? emp.getEdtSegment("EDTCD24").getImporte() : 0);

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

	/**
	 * 91 A Ingresar 92 A percibir se crea el segmento tt91 o tt92 dependiendo
	 * del signo del importe
	 */
	public void createEDTTt9XSegment(EMP emp) {
		Integer amount = 0;
		amount += ((emp.getEdt().containsKey("EDTTT10") && emp.getEdtSegment("EDTTT10").getImporte() != null) ? emp.getEdtSegment("EDTTT10").getImporte() : 0);
		amount += ((emp.getEdt().containsKey("EDTTT20") && emp.getEdtSegment("EDTTT20").getImporte() != null) ? emp.getEdtSegment("EDTTT20").getImporte() : 0);
		amount += ((emp.getEdt().containsKey("EDTTT30") && emp.getEdtSegment("EDTTT30").getImporte() != null) ? emp.getEdtSegment("EDTTT30").getImporte() : 0);
		EDT edt = emp.getEdtSegment("EDTTT" + (amount < 0 ? "92" : "91"));
		edt.setTipoElemento("TT");
		edt.setClave(amount < 0 ? 92 : 91);
		edt.setCalificadorClave(null);
		edt.setBase(null);
		edt.setIndicadorFactorTipo(" ");
		edt.setParteEnteraTipo(0);
		edt.setParteDecimalFactorTipo(0);
		edt.setSigno(" ");
		if (amount < 0) {
			amount += (emp.getEdt().containsKey("EDTCA90") ? emp.getEdtSegment(
					"EDTCA90").getImporte() : 0);
		}
		edt.setImporte(amount);
	}


}

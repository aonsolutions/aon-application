package com.esferalia.aon.file.payroll.fan;

import java.io.Serializable;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.file.payroll.fan.data.DAT;
import com.esferalia.aon.file.payroll.fan.data.EDT;
import com.esferalia.aon.file.payroll.fan.data.EMP;
import com.esferalia.aon.file.payroll.fan.data.TRA;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.enumeration.ss.T33;

public class FANGeneral implements Serializable, IFanFactory {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	
	
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
//		edt.setCalificadorClave(null);
			edt.setBase(base);
//		edt.setIndicadorFactorTipo(null);
//		edt.setParteEnteraTipo(0);
//		edt.setParteDecimalFactorTipo(0);
//		edt.setImporte(null);
//		edt.setSigno(" ");
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
		// TODO
//		if(ccc.getActivity().getType() != SSRegimeType.AGRICULTURAL && CommonUtil.getYear(getStartDate()) < 2012) {
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
//		}
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
//		if (ccc.getActivity().getType() != SSRegimeType.ARTIST
//				&& ccc.getActivity().getType() != SSRegimeType.COAL_MINING) {
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
//		}
	}
	
	/**
	 * 03 IT por AT y EP No es de aplicación en el Régimen General de Artistas
	 * (0112) y Régimen Especial Agrario (0613)
	 * 
	 * @param contract
	 * @param emp
	 */
	public void createEDTCd03Segment(EnterpriseCCC ccc, EMP emp) {
//		if (ccc.getActivity().getType() != SSRegimeType.ARTIST
//				&& ccc.getActivity().getType() != SSRegimeType.COAL_MINING) {
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
//		}
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
//		if (ccc.getActivity().getType() != SSRegimeType.AGRICULTURAL) {
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
//		}
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
//		if (ccc.getActivity().getType() != SSRegimeType.ARTIST) {
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
//		}
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
//		if (ccc.getActivity().getType() == SSRegimeType.GENERAL) {
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
//		}
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
	 * 31 Reducciones RDL-3/2014 Sólo para Regimen General,Régimen Especial del
	 * Mar y Régimen Especial de la Minería del Carbón
	 * 
	 * @param ccc
	 * @param emp
	 */
	public void createEDTCd31Segment(EnterpriseCCC ccc, EMP emp) {
//		if (ccc.getActivity().getType() == SSRegimeType.GENERAL
//				|| ccc.getActivity().getType() == SSRegimeType.SEA_WORKERS
//				|| ccc.getActivity().getType() == SSRegimeType.COAL_MINING) {
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
//		}
	}	
	
	
	
	
	
	// *********************************************
	// *********************************************
	// ELEMENTO CALCULADO TOTALES
	// *********************************************
	// *********************************************

	/**
	 * 01 Contingencias Comunes
	 * 
	 * @param emp
	 */
	public void createEDTCa01Segment(Double cgcTotalEnterprise, Double cgcTotalEmployee, EMP emp) {
		// TODO
		Integer base = emp.getEdt().containsKey("EDTBA01") ? emp.getEdtSegment("EDTBA01").getBase() : 0;
		// Double amount = new Double(base*0.283);

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
			edt.setImporte((new Double(amount * 100)).intValue());
			edt.setSigno(" ");
		}
	}

	public void createEDTCa02Segment(EMP emp) {
		// TODO 02 Cuota empresarial por Contingencias Comunes
		// Cotización empresarial / Toneladas Régimen Especial de Manipulado y
		// Empaquetado de Tomate Fresco (0134)
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
			edt.setImporte((new Double(amount * 100)).intValue());
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
		// if(amount != 0){
		EDT edt = emp.getEdtSegment("EDTCA30");
		edt.setTipoElemento("CA");
		edt.setClave(30);
		edt.setCalificadorClave(null);
		edt.setBase(null);
		edt.setImporte(amount);
		edt.setSigno(" ");
		// }
	}

	/**
	 * 31 Cuotas por Incapacidad Temporal por AT y EP
	 * 
	 * @param emp
	 */
	public void createEDTCa31Segment(Double itTotal, EMP emp) {
		// TODO

		Double amount = 0.0;
		amount = itTotal;
		amount = CommonUtil.round(amount, 2);

		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCA31");
			edt.setTipoElemento("CA");
			edt.setClave(31);
			edt.setCalificadorClave(null);
			edt.setBase(null);
			edt.setImporte((new Double(amount * 100)).intValue());
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
		// TODO

		Double amount = 0.0;
		amount = imsTotal;
		amount = CommonUtil.round(amount, 2);

		if (amount != 0) {
			EDT edt = emp.getEdtSegment("EDTCA32");
			edt.setTipoElemento("CA");
			edt.setClave(32);
			edt.setImporte((new Double(amount * 100)).intValue());
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

		// Double amount = new Double((base)*0.283);

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
			// edt.setParteEnteraTipo(28);
			// edt.setParteDecimalFactorTipo(30000);
			edt.setImporte((new Double(amount * 100)).intValue());
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

	public void createEDTCa57Segment(EMP emp) {
		// TODO 57 Cuota empresarial por Otras Cotizaciones
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
			// edt.setIndicadorFactorTipo("T");
			// edt.setParteEnteraTipo(28);
			// edt.setParteDecimalFactorTipo(03);
			// edt.setImporte(edt.getBase()*28);
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
		// if (liquidationType == LiquidationType.L00) {
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
			edt.setImporte((new Double(amount * 100)).intValue());
		}
		// }
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

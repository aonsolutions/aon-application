package com.code.aon.ui.payroll.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.payroll.cotizacion.Porcentaje;

public class Utils {

	@SuppressWarnings("unchecked")
	public static final boolean hasOverlap(Date fecini, Date fecfin, Iterator iter) {
		boolean hasOverlap = false;
		while (iter.hasNext() && !hasOverlap) {
			Porcentaje p = (Porcentaje) iter.next();
			if ( p.getFecfin().compareTo( fecfin ) != 0 && p.getFecfin().after( fecini ) ) {
				hasOverlap = true;
			}
		}
		return hasOverlap;
	}
	
	@SuppressWarnings("unchecked")
	public static final boolean startEndDateschecker(Date fecini, Date fecfin) {
		boolean hasOverlap = false;
		if ( fecfin.before( fecini ) ) {
			hasOverlap = true;
		}
		return hasOverlap;
	}
	
	/**
	 * Valida el parametro pDato segun el tipo de mascara indicado.
	 * 
	 * @param pDato
	 * @param pMask
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final boolean validarMascara(String pDato, String pMask) {
		boolean valido=false;
		
		if(pDato.length() == pMask.length()){
			for(int i=0;i<pDato.length();i++){
				switch (pMask.charAt(i)){
					case '#': 
						if(pDato.charAt(i)<='9' && pDato.charAt(i)>='0') valido=true;
					break;
					case 'A': 
						if((pDato.charAt(i)>='A' && pDato.charAt(i)<='Z') || (pDato.charAt(i)>='a' && pDato.charAt(i)<='z')) valido=true;
					break;
					case 'X': 
						if((pDato.charAt(i)>='A' && pDato.charAt(i)<='Z') || (pDato.charAt(i)>='a' && pDato.charAt(i)<='z') || (pDato.charAt(i)<='9' && pDato.charAt(i)>='0')) valido=true;
					break;
					default: valido=false;
				}
			}
		}
		System.out.println(valido);
		return valido;
	}
	
	/**
	 * Funcion que recibida una fecha Char en formato DDMM comprueba que sea valida 0101 a 3112
	 * 
	 * @param pFecha
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final boolean isFechaDDMM(String pFecha) {
		int fecha, mes, dia;
		fecha = Integer.parseInt(pFecha);
		dia=fecha/100;
		mes=fecha%100;
		
		Calendar cal=Calendar.getInstance();
		
		if(mes-1<=cal.getActualMaximum(Calendar.MONTH) && mes>0){
			cal.set(Calendar.MONTH, mes);
			if(dia<=cal.getActualMaximum(Calendar.DAY_OF_MONTH) && dia>0)
				return true;
			else
				return false;
		} else
			return false;
	}
	
	/**
	 * Dado el nombre de un POJO(osea, la clase JAVA) y un campo de la tabla, devuelve el valor maximo
	 * @param tableName
	 * @param fieldName
	 * @return
	 */
	public static final String maxCode(String tableName, String fieldName){
		
		String sql = "select max("+fieldName.toString()+") from "+tableName.toString();
		System.out.println(sql);
		Query query = HibernateUtil.getSession().createQuery(sql);			
		List results = query.list();
		
		return results.get(0).toString();
	}
	
	/*
	 // MODIFICACION HECHA POR EUKE, HECHAR UN VISTAZO CON MAS TIEMPO
	public static final String maxCode(String tableName, String fieldName){
		
		String sql = "select max("+fieldName.toString()+") from "+tableName.toString();
		System.out.println(sql);
		String factoryName = HibernateUtil.getSessionFactoryName(null);
		Query query = HibernateUtil.getSession(factoryName).createQuery(sql);			
		List results = query.list();
		
		return results.get(0).toString();
	}
	*/
	
	/**
	 * Dado el nombre de  un POJO(osea, la clase JAVA) un campo de la tabla y una clausula where, 
	 * devuelve su valor maximo
	 * @param tableName
	 * @param fieldName
	 * @return
	 */
	public static final String maxCode(String tableName, String fieldName, String where){
		
		if(tableName!=null && fieldName!=null && where!=null){
			String sql = "select max("+fieldName.toString()+") from "+tableName.toString()+" where "+where.toString();	
			System.out.println(sql);
			Query query = HibernateUtil.getSession().createQuery(sql);			
			List results = query.list();
				
			if(results.size()<=0)
				return "0";
			else
				return results.get(0).toString();
		} else
			return null;
	}
}

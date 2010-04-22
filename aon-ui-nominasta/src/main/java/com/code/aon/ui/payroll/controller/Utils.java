package com.code.aon.ui.payroll.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.payroll.cotizacion.Porcentaje;

public class Utils {
	
	private static final int[] DIGITS = new int[] { 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };

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
	
	public static boolean isValidNif(String nifParam){
		char[] nif = nifParam.toCharArray();
		if (nif.length == 9) {

// [ÑAPA PARA EVITAR QUE LOS NIF DE LOS EXTRANJEROS DEN ERROR,
//  SE SUSTITUYE LA X INICIAL POR UN CERO]
			nif[0] = (nif[0] == 'X')?'0':nif[0];
// [FIN ÑAPA]
			return ((nif[0] >= '0' && nif[0] <= '9')?checkDNI(nif):checkNIF(nif));
		}
		else {
			return false;
		}
	}

	public static boolean checkDNI ( char[] nif ){
		char[] letters = {'T','R','W','A','G','M','Y','F','P','D','X','B','N','J',
											'Z','S','Q','V','H','L','C','K','E'};
		String numbers = new String(nif, 0, 8);
		int iDni = 0;
		try {
			iDni = Integer.parseInt(numbers);
		} catch (NumberFormatException ex) {
			return false;
		}
		int rest = iDni % 23;
		return ( nif[8] == letters[rest] );
	}

	public static String getDniLetter (char[] dni){
		String s="";
		char[] letters = {'T','R','W','A','G','M','Y','F','P','D','X','B','N','J',
											'Z','S','Q','V','H','L','C','K','E'};
		String numbers = new String(dni, 0, 8);
		int iDni = 0;
		try {
			iDni = Integer.parseInt(numbers);
		} catch (NumberFormatException ex) {
			return s;
		}
		int rest = iDni % 23;
		String se = String.valueOf(letters[rest]);
		return se ;
	}
	
	public static boolean checkNIF ( char[] nif ){
		int lInDC = 0;
		for (int i = 1; i < 8 ; ++i ) {
			String strDigit = new String(nif, i, 1);
			int digit = 0;
			try {
				digit = Integer.parseInt( strDigit );
			} catch (NumberFormatException ex) {
				return false;
			}
			if ((i % 2) != 0) {
				digit *= 2;
				if ( digit >= 10 ) {
					digit -= 9;
				}
			}
			lInDC += digit;
		}
		//Buscamos el multiplo de diez mas cercano mayor al numero calculado.
		lInDC = ( ( (lInDC / 10)  + 1) * 10 ) - lInDC;
		if (lInDC == 10) {
        lInDC = 0;
    }
		if (nif[0] == 'P' || nif[0] == 'S' || nif[0] == 'Q') {
			char[] letras = {'J','A','B','C','D','E','F','G','H','I'};
			return (letras[lInDC] == nif[8]);
		}
		else {
			String strDC = new String(nif, 8, 1);
			int comp = 0;
			try {
				comp = Integer.parseInt( strDC );
			} catch (NumberFormatException ex) {
					return false;
			}
			return (comp==lInDC);
		}
	}
	
	
	public static boolean isValidAccount(String entidad, String sucursal, String dc, String numcta) {
		String cd = calculateControlDigit( entidad,  sucursal, dc, numcta);
		return cd != null && cd.equals(dc);
	}

	public static String calculateControlDigit(String entidad, String sucursal, String dc, String numcta) {
		if (entidad == null || entidad.length() != 4 || sucursal == null
				|| sucursal.length() != 4 || numcta == null || numcta.length() != 10) {
			return "XX";
		}
		String entoff = entidad + sucursal;
		int sum = 0;
		int total = 0;
		for (int i = 0; i < entoff.length(); i++) {
			int digito = Integer.parseInt(String.valueOf(entoff.charAt(entoff.length() - 1 - i)));
			sum = digito * DIGITS[i];
			total = total + sum;
		}
		total = 11 - (total % 11);
		if (total == 10) {
			total = 1;
		}
		if (total == 11) {
			total = 0;
		}
		int number = 0;
		int control = 0;
		int c = 0;
		for (int i = 0; i < numcta.length(); i++) {
			number = Integer.parseInt(String.valueOf(numcta.charAt(	numcta.length() - 1 - i)));
			control = number * DIGITS[i];
			c = c + control;
		}
		c = 11 - (c % 11);
		if (c == 10) {
			c = 1;
		}
		if (c == 11) {
			c = 0;
		}
		return String.valueOf(total) + String.valueOf(c);
	}
	
	
}

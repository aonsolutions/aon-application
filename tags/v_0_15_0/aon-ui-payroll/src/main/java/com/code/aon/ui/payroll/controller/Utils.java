package com.code.aon.ui.payroll.controller;

import java.util.Date;
import java.util.Iterator;

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
						if(pDato.charAt(i)<'9' && pDato.charAt(i)>'0') valido=true;
					break;
					case 'A': 
						if((pDato.charAt(i)>'A' && pDato.charAt(i)<'Z') || (pDato.charAt(i)>'a' && pDato.charAt(i)<'z')) valido=true;
					break;
					case 'X': 
						if((pDato.charAt(i)>'A' && pDato.charAt(i)<'Z') || (pDato.charAt(i)>'a' && pDato.charAt(i)<'z') || (pDato.charAt(i)<'9' && pDato.charAt(i)>'0')) valido=true;
					break;
					default: valido=false;
				}
			}
		}
		System.out.println(valido);
		return valido;
	}
	
}

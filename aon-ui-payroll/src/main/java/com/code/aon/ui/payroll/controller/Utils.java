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
}

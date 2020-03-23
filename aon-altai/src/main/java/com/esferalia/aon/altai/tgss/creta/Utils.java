package com.esferalia.aon.altai.tgss.creta;

import java.sql.Date;

import net.aonsolutions.core.tgss.creta.jaxb.Fecha;

public class Utils {

	public static Date fecha2Date(Fecha fecha ) {
		int day = Integer.parseInt(fecha.getDia());
		int year = Integer.parseInt(fecha.getAnho()) - 1900;
		int month = Integer.parseInt(fecha.getMes()) - 1;
		return new Date (year, month, day);
	}

	public static Date fecha2Date(net.aonsolutions.core.tgss.creta.jaxb.calculos.Fecha fecha ) {
		int day = Integer.parseInt(fecha.getDia());
		int year = Integer.parseInt(fecha.getAnho()) - 1900;
		int month = Integer.parseInt(fecha.getMes()) - 1;
		return new Date (year, month, day);
	}

}

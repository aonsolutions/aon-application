package com.esferalia.aon.in.payroll.tgss.idc;

import java.util.Date;

import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.TrabajadorBuilder.TipoIpf;

public interface TrabajadoresTramosCallback {

	
	default String getIpf( String naf ) {
		return "51013253N";
	}

	default TipoIpf getTipoIpf( String naf ) {
		return TipoIpf.DNI;
	}
	
	default boolean isQuoteByRealDays(String ssNum, String ccc, Date start, Date end) {
	    return false;
	}

	default boolean isPartTimeEmployee(String ssNum, String ccc, Date start, Date end) {
		return false;
	}
	
	default boolean isScholarEmployee(String ssNum, String ccc, Date start, Date end) {
		return false;
	}

	default boolean isTraining421Employee(String ssNum, String ccc, Date start, Date end) {
		return false;
	}

}

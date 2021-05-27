package com.esferalia.aon.in.payroll.tgss.idc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;

import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.TrabajadorBuilder.TipoIpf;

public class Idcplnss {
	
	public static Collection<Bonus> getSSBonuses (byte pdf []) throws IOException, UnknownPDFException {
		try (InputStream is = new ByteArrayInputStream(pdf)){
			return getSSBonuses(is);
		}
	}
	
	public static Collection<Bonus> getSSBonuses (InputStream is) throws IOException, UnknownPDFException {	
		BonusListener  ssBonusListener = new BonusListener();
		IdcplnssParser.parse(is, ssBonusListener );
		return ssBonusListener.getSSBonuses();
	}



}

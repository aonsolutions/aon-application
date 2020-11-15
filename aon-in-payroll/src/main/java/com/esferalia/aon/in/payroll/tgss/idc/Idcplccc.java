package com.esferalia.aon.in.payroll.tgss.idc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;

public class Idcplccc {
	
	public static Collection<Bonus> getSSBonuses (byte pdf []) throws IOException, UnknownPDFException {
		try (InputStream is = new ByteArrayInputStream(pdf)){
			return getSSBonuses(is);
		}
	}
	
	public static Collection<Bonus> getSSBonuses (InputStream is) throws IOException, UnknownPDFException {	
		BonusListener  ssBonusListener = new BonusListener();
		IdcplcccParser.parse(is, ssBonusListener );
		return ssBonusListener.getSSBonuses();
		
	}
}

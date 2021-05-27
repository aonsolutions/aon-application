package com.esferalia.aon.in.payroll.tgss.idc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;

import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.TrabajadorBuilder.TipoIpf;

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

	public static TrabajadoresTramos geTrabajadoresTramos (InputStream is, TrabajadoresTramosCallback cb) throws IOException, UnknownPDFException {	
		CretaListener  cretaListener = new CretaListener() {
			@Override
			protected String getIpf(String naf) {
				return cb.getIpf(naf);
			}
			@Override
			protected TipoIpf getTipoIpf(String naf) {
				return cb.getTipoIpf(naf);
			}
			
			@Override
			protected boolean isPartTimeEmployee(String ssNum, String ccc, Date start, Date end) {
				return cb.isPartTimeEmployee(ssNum, ccc, start, end);
			}
			
			@Override
			protected boolean isScholarEmployee(String ssNum, String ccc, Date start, Date end) {
				return cb.isScholarEmployee(ssNum, ccc, start, end);
			}
			
			@Override
			protected boolean isTraining421Employee(String ssNum, String ccc, Date start, Date end) {
				return cb.isTraining421Employee(ssNum, ccc, start, end);
			}
		};
		IdcplcccParser.parse(is, cretaListener );
		return cretaListener.getTrabajadoresTramos();
		
	}

}

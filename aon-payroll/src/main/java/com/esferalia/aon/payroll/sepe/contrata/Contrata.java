package com.esferalia.aon.payroll.sepe.contrata;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATOS;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;

public class Contrata {
	
	public CONTRATOS getCONTRATOS(byte[] data) {
		if(null == data)
			return null;
		
		try {
			InputStream in = new ByteArrayInputStream(data); 
			return Utils.unmarshal(CONTRATOS.class, in);
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}
}

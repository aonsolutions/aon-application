package com.esferalia.aon.payroll.sepe.contrata;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.jooq.DSLContext;
import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATOS;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;

public class Contrata {
	
	public String getContracta(DSLContext dslContext) {
		return "";
	}

	public CONTRATOS getCONTRATOS(byte[] data) {
		if(null == data)
			return null;
		
		InputStream in = new ByteArrayInputStream(data); 
		
		try {
			CONTRATOS contratos = Utils.unmarshal(CONTRATOS.class, in);
			return contratos;
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}
}

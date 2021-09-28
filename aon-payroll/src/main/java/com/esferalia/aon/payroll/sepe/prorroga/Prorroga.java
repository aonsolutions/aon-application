package com.esferalia.aon.payroll.sepe.prorroga;

import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.jooq.DSLContext;

import com.esferalia.aon.sepe.api.contrata.prorrogas.PRORROGAS;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;

public class Prorroga {
	
	private Prorroga() {
		super();
	}
	
	public static PRORROGAS getProrroga(DSLContext dslContext, Integer contractId) {
		List<byte[]> prorrogaRecords = dslContext.select(CONTRACT_ATTACH.DATA).from(CONTRACT_ATTACH)
			.where(CONTRACT_ATTACH.CONTRACT.eq(contractId))
			.and(CONTRACT_ATTACH.TYPE.eq((byte)13))
			.orderBy(CONTRACT_ATTACH.ID)
			.fetch(CONTRACT_ATTACH.DATA);
		
		if(prorrogaRecords.isEmpty()) return null;
		
		byte[] data = prorrogaRecords.get(0);
		
		InputStream in = new ByteArrayInputStream(data); 
		
		try {
			return Utils.unmarshal(PRORROGAS.class, in);
		} catch (JAXBException e) {
			return null;
		}
	}
}

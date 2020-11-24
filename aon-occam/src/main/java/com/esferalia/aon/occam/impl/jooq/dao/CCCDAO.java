package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.Result;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.payroll.CCCInfo;

public class CCCDAO {
	


	
	// -------------------- CCC
	
	public static Stream<CCCInfo> getCCCStream(AONContext ctx){
		ctx.checkRead();
	
		Map<Integer, CCCInfo> cccs = new HashMap<Integer, CCCInfo>();
		
		//ENTERPRISE_ACTIVITY
		List<Record> enterpriseActivitiesRecords = ctx.getDslContext().select().from(ENTERPRISE_ACTIVITY)
				.where(ENTERPRISE_ACTIVITY.DOMAIN.eq(ctx.getDomainId()))
				.fetch();
		
		for(Record enterpriseActivityRecord : enterpriseActivitiesRecords) {
			Integer enterpriseActivityId = enterpriseActivityRecord.get(ENTERPRISE_ACTIVITY.ID);			
			
			//ENTERPRISE_CCC
			Result<Record> enterpriseCCCRecords = ctx.getDslContext().select().from(ENTERPRISE_CCC)
					.where(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY.eq(enterpriseActivityId))
					.fetch();
			
			for(Record enterpriseCCCRecord : enterpriseCCCRecords) {
				Integer cccId = enterpriseCCCRecord.get(ENTERPRISE_CCC.ID);
				String ccc = enterpriseCCCRecord.get(ENTERPRISE_CCC.CCC);
				Byte cccRegime = enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE);
				String cccRegimeCode = getCCCRegimeCode(cccRegime);
				Integer geozoneId = enterpriseCCCRecord.get(ENTERPRISE_CCC.GEOZONE);
				
				Boolean useByContracts = false;
				
				Result<Record> contractRecord = ctx.getDslContext().select().from(CONTRACT)
						.where(CONTRACT.ENTERPRISE_CCC.eq(cccId))
						.and(CONTRACT.ID.greaterThan(0))
						.fetch();
				
				if(null != contractRecord && !contractRecord.isEmpty())
					useByContracts = true;
				
				String geozone = null;
				String geozoneCode = null;
				if(null != geozoneId) {
					Result<Record> geozoneName = ctx.getDslContext().select()
							.from(GEOZONE)
							.where(GEOZONE.ID.eq(geozoneId))
							.fetch();
					
					geozone = geozoneName.get(0).get(GEOZONE.NAME);
					geozoneCode = geozoneName.get(0).get(GEOZONE.CODE);
				}
				
				CCCInfo cccInfo = new CCCInfo(ccc, cccRegimeCode, ccc, cccRegime, geozone, geozoneCode, enterpriseActivityId, cccId, useByContracts);
				cccs.put(cccId, cccInfo);	
			}
		}
		
		
		return cccs.values().stream();	
	}
	
	private static String getCCCRegimeCode(Byte cccRegime) {
		switch (cccRegime) {
		case 0:
			return "0111";
		case 1:
			return "0111";
		case 2:
			return "0111";
		case 3:
			return "0111";
		case 4:
			return "0111";
		case 5:
			return "0111";
		case 6:
			return "0138";
		case 7:
			return "0163";
		case 8:
			return "0112";
		default:
			return "0111";
		}
	}
	
}





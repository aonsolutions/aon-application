package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.CraBatch.CRA_BATCH;
import static com.esferalia.aon.jooq.tables.CraBatchDetail.CRA_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class CraBatchUpdate implements Update {
	
	public static final CraBatchUpdate CRAUPDATE = new CraBatchUpdate();

	private CraBatchUpdate() {}

	@Override
	public void upgrade(Connection connection) {
		
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
			
		deleteCraBatchDetails(dslContext);
		fixCraBatch(dslContext);
	}

	private void deleteCraBatchDetails(DSLContext dslContext) {
		dslContext.delete(CRA_BATCH_DETAIL).execute();
	}

	private void fixCraBatch(DSLContext dslContext) {
		// List of craBatch should be deleted
		List<Integer> craBatchDeleteIds = new ArrayList<Integer>();
		
		// Get all craBatchRecords
		Result<Record> craBatchRecords = dslContext.select().from(CRA_BATCH).fetch();
		
		for(Record craBatchRecord : craBatchRecords) {
			Integer craBarchId = craBatchRecord.get(CRA_BATCH.ID);
			Integer craDomian = craBatchRecord.get(CRA_BATCH.DOMAIN);
			
			// Check if dates are null, if they are set to deleted them
			Timestamp creationDate = craBatchRecord.get(CRA_BATCH.DATE);
			Timestamp craPeriod = craBatchRecord.get(CRA_BATCH.OUTCOME_FILE_DATE);
			
			if(null == creationDate || null == craPeriod) {
				craBatchDeleteIds.add(craBarchId);
				continue;
			}
			
			// Check if it has an outcome file
			
			byte[] craOutcomeFile = craBatchRecord.get(CRA_BATCH.OUTCOME_FILE);
			
			if(null == craOutcomeFile || craOutcomeFile.length == 0) {
				craBatchDeleteIds.add(craBarchId);
				continue;
			}
			
			// Get CCC codes from outcome file
			
			List<String> cccCodes = getCCCCodesFromOutcomeFile(craOutcomeFile);
			
			if(cccCodes.isEmpty()) {
				craBatchDeleteIds.add(craBarchId);
				continue;
			}
			
			// Get CCCId list from cccCodes list
			
			List<Integer> cccIds = getCCCIdsFromCCCCodes(dslContext, cccCodes);
			
			// Insert new craBatchDetails
			
			for(Integer cccId : cccIds) {
				dslContext.insertInto(CRA_BATCH_DETAIL)
					.set(CRA_BATCH_DETAIL.DOMAIN, craDomian)
					.set(CRA_BATCH_DETAIL.CRA_BATCH, craBarchId)
					.set(CRA_BATCH_DETAIL.ENTERPRISE_CCC, cccId)
					.execute();
			}
		}
	}

	private List<String> getCCCCodesFromOutcomeFile(byte[] craOutcomeFile) {
		List<String> cccCodes = new ArrayList<String>();
		
		String dataStr = new String(craOutcomeFile);
		
		String subStringAnalize = "";
		
		if(dataStr.length() == 0 || dataStr.length() < 72)
			return cccCodes;
		
		for(int i=0; i<dataStr.length(); i+=72) {
			
			subStringAnalize = dataStr.substring(i, i + 72);
			
			if(subStringAnalize.contains("DDE")) {
				
				String analizeCCC = subStringAnalize.substring(7, 18);
				
				cccCodes.add(analizeCCC);
			}
		}
		
		return cccCodes;
	}
	
	private List<Integer> getCCCIdsFromCCCCodes(DSLContext dslContext, List<String> cccCodes) {
		List<Integer> cccIds = new ArrayList<Integer>();
		
		for(String cccCode : cccCodes) {
			Integer cccId = dslContext.select(ENTERPRISE_CCC.ID).from(ENTERPRISE_CCC)
					.where(ENTERPRISE_CCC.CCC.eq(cccCode))
					.fetchOne(ENTERPRISE_CCC.ID);
			
			cccIds.add(cccId);
		}
		
		return cccIds;
	}
	 
	
}

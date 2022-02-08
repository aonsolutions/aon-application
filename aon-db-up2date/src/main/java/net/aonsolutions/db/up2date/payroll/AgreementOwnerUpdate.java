package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AgreementOwnerUpdate implements Update {
	
	public static final AgreementOwnerUpdate AGREEMENTOWNERUPDATE = new AgreementOwnerUpdate();

	private AgreementOwnerUpdate() {}

	@Override
	public void upgrade(Connection connection) {
		
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		dslContext.transaction( config -> updateOwnerColumn(dslContext));
	}

	private void updateOwnerColumn(DSLContext dslContext) {
		// Update owner columns
		Result<Record> agreementRecords = dslContext.select().from(AGREEMENT).fetch();
		for(Record agreementRecord : agreementRecords) {
			Integer agreementId = agreementRecord.get(AGREEMENT.ID);
			String agreementSSNum = agreementRecord.get(AGREEMENT.SS_NUMBER);
			
			Record agreementDataRecord = dslContext.select().from(AGREEMENT_DATA)
				.where(AGREEMENT_DATA.NAME.eq("SERVIAGREEMENT"))
				.and(AGREEMENT_DATA.AGREEMENT.eq(agreementId))
				.fetchOne();
			
			Integer agreementDataId = null;
			String serviAgreement = null;	
			// Has SERVIAGREEMENT on AgreementData
			if(null != agreementDataRecord) {
				agreementDataId = agreementDataRecord.get(AGREEMENT_DATA.ID);
				serviAgreement = agreementDataRecord.get(AGREEMENT_DATA.EXPRESSION);
				
				if(null == serviAgreement || serviAgreement.equalsIgnoreCase("FALSE"))
					dslContext.update(AGREEMENT).set(AGREEMENT.OWNER, (byte)0).where(AGREEMENT.ID.eq(agreementId)).execute();
				else
					dslContext.update(AGREEMENT).set(AGREEMENT.OWNER, (byte)1).where(AGREEMENT.ID.eq(agreementId)).execute();
				
				dslContext.delete(AGREEMENT_DATA)
					.where(AGREEMENT_DATA.ID.eq(agreementDataId))
					.execute();
			} else {
			// Has not SERVIAGREEMENT on AgreementData, so we look for ssNumber
				if(null == agreementSSNum || agreementSSNum.length() == 0)
					dslContext.update(AGREEMENT).set(AGREEMENT.OWNER, (byte)0).where(AGREEMENT.ID.eq(agreementId)).execute();
				else
					dslContext.update(AGREEMENT).set(AGREEMENT.OWNER, (byte)1).where(AGREEMENT.ID.eq(agreementId)).execute();
			}
		}
	}

	
}

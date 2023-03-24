package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.IrpfData.IRPF_DATA;

import java.sql.Connection;
import java.sql.Date;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.IrpfDataRecord;

import net.aonsolutions.db.up2date.Update;

public class Mod145StartDateUpdate implements Update {
	
	public static final Mod145StartDateUpdate MOD145STARTDATEUPDATE = new Mod145StartDateUpdate();
	
	private Mod145StartDateUpdate() {}

	@Override
	public void upgrade(Connection connection) {
		
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		dslContext.transaction( (config) -> {
			updateMod145StartDate(dslContext);
		});
	}

	
	private void updateMod145StartDate(DSLContext dslContext) {
		Result<IrpfDataRecord> irpfDataRecords = dslContext.selectFrom(IRPF_DATA).where(IRPF_DATA.START_DATE.isNull()).fetch();
		for(IrpfDataRecord irpfDataRecord : irpfDataRecords) {
			// If start_date is null, set issue_date as start_date
			Date issueDate = irpfDataRecord.getIssueDate();
			dslContext.update(IRPF_DATA).set(IRPF_DATA.START_DATE, issueDate).where(IRPF_DATA.ID.eq(irpfDataRecord.getId())).execute();
		}
		System.out.println("IrpfDate updates : " + irpfDataRecords.size());
	}

}

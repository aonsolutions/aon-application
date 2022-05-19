package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class ContractAttachUpdate implements Update {

	public static final ContractAttachUpdate CONTRACTATTACHUPDATE = new ContractAttachUpdate();
	
	private ContractAttachUpdate() {}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		dslContext.transaction( config -> {
			removeIDCAttachs(dslContext);
			updateBasicCopyContractAttachs(dslContext);	
			updateCopyContractAttachs(dslContext);	
		});
	}

	private void removeIDCAttachs(DSLContext dslContext) {
		int idcDeletes = dslContext.delete(CONTRACT_ATTACH)
			.where(CONTRACT_ATTACH.TYPE.eq((byte)101)
					.or(CONTRACT_ATTACH.TYPE.eq((byte)102))
			).and(CONTRACT_ATTACH.DESCRIPTION.contains("%IDC%"))
			.execute();
		
		System.out.println("IDCs deleted : " + idcDeletes);
	}

	private void updateBasicCopyContractAttachs(DSLContext dslContext) {
		int basicCopyUpdates = dslContext.update(CONTRACT_ATTACH)
			.set(CONTRACT_ATTACH.TYPE, (byte)102)
			.where(CONTRACT_ATTACH.TYPE.eq((byte)3))
			.and(CONTRACT_ATTACH.DESCRIPTION.contains("%Copia Basica%"))
			.execute();
		
		System.out.println("Contract Basic Copy updated : " + basicCopyUpdates);
	}
	
	private void updateCopyContractAttachs(DSLContext dslContext) {
		int basicCopyUpdates = dslContext.update(CONTRACT_ATTACH)
			.set(CONTRACT_ATTACH.TYPE, (byte)101)
			.where(CONTRACT_ATTACH.TYPE.eq((byte)1))
			.and(CONTRACT_ATTACH.DESCRIPTION.contains("%Copia Contrato%"))
			.execute();
		
		System.out.println("Contract Copy updated : " + basicCopyUpdates);
	}
	
}

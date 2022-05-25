package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class ContractAttachUpdateII implements Update {

	public static final ContractAttachUpdateII CONTRACTATTACHUPDATEII = new ContractAttachUpdateII();
	
	private ContractAttachUpdateII() {}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		dslContext.transaction( config -> {
			updateOtherAttachs(dslContext);
			updateIdcContractAttachs(dslContext);	
			updateIdcPlNssContractAttachs(dslContext);	
		});
	}
	
	private void updateOtherAttachs(DSLContext dslContext) {
		int otherUpdates = dslContext.update(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.TYPE, (byte)106)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)104))
				.execute();
			
		System.out.println("Other attachs updated : " + otherUpdates);
	}

	private void updateIdcContractAttachs(DSLContext dslContext) {
		int idcUpdates = dslContext.update(CONTRACT_ATTACH)
			.set(CONTRACT_ATTACH.TYPE, (byte)104)
			.where(CONTRACT_ATTACH.TYPE.eq((byte)101))
			.and(CONTRACT_ATTACH.DESCRIPTION.contains("%TGSS%"))
			.and(CONTRACT_ATTACH.DESCRIPTION.contains("%IDC%"))
			.execute();
		
		System.out.println("Idc updated : " + idcUpdates);
	}
	
	private void updateIdcPlNssContractAttachs(DSLContext dslContext) {
		int idcUpdates = dslContext.update(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.TYPE, (byte)105)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)102))
				.and(CONTRACT_ATTACH.DESCRIPTION.contains("%TGSS%"))
				.and(CONTRACT_ATTACH.DESCRIPTION.contains("%IDC%"))
				.and(CONTRACT_ATTACH.DESCRIPTION.contains("%NSS%"))
				.execute();
			
		System.out.println("IdcPlNss updated : " + idcUpdates);
	}
	
}

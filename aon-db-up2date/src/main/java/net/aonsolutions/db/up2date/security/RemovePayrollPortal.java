package net.aonsolutions.db.up2date.security;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.DomainApplicationModule;

import net.aonsolutions.db.up2date.Update;


public class RemovePayrollPortal implements Update {


	public static final RemovePayrollPortal REMOVE_PAYROLL_PORTAL = new RemovePayrollPortal();

	public static final byte PAYROLL_PORTAL = (byte) 14;
	
	private RemovePayrollPortal() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		removePayrollPortal(dslContext, PAYROLL_PORTAL);

	}
	
	private void removePayrollPortal(DSLContext dslContext, byte pack) {
		dslContext.select().from(Domain.DOMAIN)
			.join(DomainApplicationModule.DOMAIN_APPLICATION_MODULE).on(DomainApplicationModule.DOMAIN_APPLICATION_MODULE.DOMAIN.eq(Domain.DOMAIN.ID))
			.where(DomainApplicationModule.DOMAIN_APPLICATION_MODULE.MODULE.eq(pack))
			.and(Domain.DOMAIN.TYPE.eq((byte) 1))
			.and(Domain.DOMAIN.PARENT.isNull())
		.fetch().stream().forEach(r -> {
			String domainName = r.getValue(Domain.DOMAIN.NAME);
			Integer damID = r.getValue(DomainApplicationModule.DOMAIN_APPLICATION_MODULE.ID);
			dslContext.delete(DomainApplicationModule.DOMAIN_APPLICATION_MODULE)
				.where(DomainApplicationModule.DOMAIN_APPLICATION_MODULE.ID.eq(damID))
				.execute();
			System.out.println("Se ha borrado PAYROLL_PORTAL del dominio " + domainName);
		});
		
	}
	
}

package net.aonsolutions.db.up2date.user;

import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.RattachTag.RATTACH_TAG;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.User.USER;
import static org.jooq.impl.SQLDataType.VARCHAR;

import java.net.UnknownServiceException;
import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import com.esferalia.aon.jooq.tables.Rattach;
import com.esferalia.aon.jooq.tables.RattachTag;
import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.jooq.tables.User;

import net.aonsolutions.db.up2date.Update;

public class CertificateDomainFix implements Update {

	public static final CertificateDomainFix CERTIFICATEDOMAINFIX = new CertificateDomainFix();
	
	private CertificateDomainFix() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		UpdateConditionStep<Record> updateUserRegistry = 
		dslContext.update(REGISTRY.join(USER).on(REGISTRY.ID.eq(USER.REGISTRY)))
		.set(REGISTRY.DOMAIN, USER.DOMAIN)
		.where(REGISTRY.DOMAIN.ne(USER.DOMAIN));
		
		UpdateConditionStep<Record> updateUserRattachs = 
		dslContext.update(RATTACH.join(USER).on(RATTACH.REGISTRY.eq(USER.REGISTRY)))
		.set(RATTACH.DOMAIN, USER.DOMAIN)
		.where(RATTACH.DOMAIN.ne(USER.DOMAIN));
		
		UpdateConditionStep<Record> updateUserRattachTags = 
		dslContext.update(RATTACH_TAG.join(RATTACH).on(RATTACH.ID.eq(RATTACH_TAG.RATTACH)).join(USER).on(USER.REGISTRY.eq(RATTACH.REGISTRY)))
		.set(RATTACH_TAG.DOMAIN, USER.DOMAIN)
		.where(RATTACH_TAG.DOMAIN.ne(USER.DOMAIN));

		SelectConditionStep<Record> crossSelect = 
		dslContext.select()
		.from(RATTACH_TAG)
		.join(RATTACH).on(RATTACH_TAG.RATTACH.eq(RATTACH.ID))
		.join(REGISTRY).on(RATTACH.REGISTRY.eq(REGISTRY.ID))
		.join(USER).on(REGISTRY.ID.eq(USER.REGISTRY))
		.where(RATTACH_TAG.DOMAIN.ne(USER.DOMAIN))
		.or(RATTACH.DOMAIN.ne(USER.DOMAIN))
		.or(REGISTRY.DOMAIN.ne(USER.DOMAIN));
		
		crossSelect.forEach(r -> System.out.printf("WARN: user '%s' with cross certificate.%n",r.get(USER.LOGIN)));

		dslContext.transaction( (config) -> {
			int updated = updateUserRegistry.execute();
			System.out.printf("INFO: updated %d registries %n", updated);
			updated = updateUserRattachs.execute();
			System.out.printf("INFO: updated %d certificated %n", updated);
			updated = updateUserRattachTags.execute();
			System.out.printf("INFO: updated %d tags %n", updated);
			
		});
		
		crossSelect.forEach(r -> System.out.printf("ERROR: user '%s' with cross certificate.%n",r.get(USER.LOGIN)));
	}

}

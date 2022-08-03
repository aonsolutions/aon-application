package net.aonsolutions.db.up2date.attach;

import java.sql.Connection;
import java.util.stream.Stream;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Company;
import com.esferalia.aon.jooq.tables.Rattach;

import net.aonsolutions.db.up2date.Update;

public class DocumentalUpdateFix implements Update {

	public static final DocumentalUpdateFix DOCUMENTAL_UPDATE_FIX = new DocumentalUpdateFix();

	private DocumentalUpdateFix() {
		
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
	
		getAttachStream(dslContext).forEach(attach -> {
			Integer registry = getCompany(dslContext, attach.getDomain());
			if(registry != null) updateAttach(dslContext, attach, registry);
		});
	}
	
	
	private Integer getCompany(DSLContext dslContext, Integer domain) {
		return dslContext.select(Company.COMPANY.REGISTRY)
				.from(Company.COMPANY)
				.where(Company.COMPANY.DOMAIN.eq(domain))
				.fetch().stream().map(r -> r.getValue(Company.COMPANY.REGISTRY))
				.findFirst().orElse(null);
	}
	
	private void updateAttach(DSLContext dslContext, Attach attach, Integer registry) {
		dslContext.update(Rattach.RATTACH).set(Rattach.RATTACH.REGISTRY, registry).where(Rattach.RATTACH.ID.eq(attach.getId())).execute();
	}
	
	private Stream<Attach> getAttachStream(DSLContext dslContext) {
		return dslContext.select(Rattach.RATTACH.ID, Rattach.RATTACH.DOMAIN)
				.from(Rattach.RATTACH)
				.where(Rattach.RATTACH.REGISTRY.eq(1))
				.and(Rattach.RATTACH.TYPE.eq((byte) 3))
				.fetch().stream().map(r -> new Attach()
						.setId(r.getValue(Rattach.RATTACH.ID))
						.setDomain(r.getValue(Rattach.RATTACH.DOMAIN)));
	}
	
	public class Attach {
		Integer id;
		Integer domain;

		public Integer getId() {
			return id;
		}
		
		public Attach setId(Integer id) {
			this.id = id;
			return this;
		}
		
		public Integer getDomain() {
			return domain;
		}
		
		public Attach setDomain(Integer domain) {
			this.domain = domain;
			return this;
		}
	}
}

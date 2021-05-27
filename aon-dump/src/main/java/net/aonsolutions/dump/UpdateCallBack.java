package net.aonsolutions.dump;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.io.PrintStream;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Schema;
import org.jooq.Table;

public class UpdateCallBack extends AbstractChaimCallbackDump{

	PrintStream out;
	AonDump aonDump;
	IdsMap idsMap;
	String descripcionEmpresa;
	Integer oldIdDomain;
	
	public UpdateCallBack(CallbackDump cb, PrintStream out, AonDump aonDump, String descripcionEmpresa, int idDomain) {
		super(cb);
		this.out = out;
		this.aonDump = aonDump;
		this.descripcionEmpresa = descripcionEmpresa;
		this.oldIdDomain = idDomain;
	}
	
	@Override
	public void header(Schema schema, String hostName, Map<Table<?>, Integer> domainTables, DSLContext dslContext,
			int id, IdsMap idsMap) {
		
		this.idsMap = idsMap;
		super.header(schema, hostName, domainTables, dslContext, id, idsMap);
	}

	@Override
	public void footer() {
		
		Field<Integer> newIdDomain = this.idsMap.getOrder(DOMAIN.getName(), this.oldIdDomain);
		
		aonDump.dslContext.update(DOMAIN)
		.set(DOMAIN.DESCRIPTION, this.descripcionEmpresa)
		.where(DOMAIN.ID.eq(newIdDomain))
		.execute();
		
		super.footer();
	}
}

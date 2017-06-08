package net.aonsolutions.dump;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Schema;
import org.jooq.SelectConditionStep;
import org.jooq.Table;

public class ParentCallbackDump extends AbstractChaimCallbackDump{

	private static String DEFAULTS_TABLES = "geozone,account";
	
	private CallbackDump  cb;
	private Integer parentDomain;
	
	public ParentCallbackDump(CallbackDump cb) {
		super(cb);
		this.cb = cb;
	}
	
	@Override
	public void header(Schema schema, String hostName, Map<Table<?>, Integer> domainTables, DSLContext dslContext,
			int id, IdsMap idsMap) {
		
		parentDomain = dslContext.select(DOMAIN.PARENT).from(DOMAIN).where((DOMAIN.ID).equal(id)).fetchOne().value1();
		super.header(schema, hostName, domainTables, dslContext, id, idsMap);
	}

	@Override
	public Field<Integer> onErrFk(DSLContext dslContext, Record r, ForeignKey<?, ?> fk, AonDump aondump, IdsMap idsMap,
			CallbackDump cb, List<Table<?>> ciclica, List<?> references, Condition where) {

		Record1<?> fkField;

		// Doesn't exit domain field
		if ((Field<Integer>) fk.getKey().getTable().field("domain") == null)
			return null;

		SelectConditionStep<?> select = dslContext.select(fk.getKey().getFields().get(0)).from(fk.getKey().getTable().getName())
				.where((((Field<Integer>) fk.getKey().getTable().field("domain")).equal(parentDomain))
						.and(((Field<Integer>) fk.getKey().getFields().get(0))
								.eq(r.getValue((Field<Integer>) fk.getFields().get(0), Integer.class))));
		
		fkField = (Record1<?>) select.fetchAny();

		if (fkField != null) {
			cb.downloadParent(dslContext, r, fk, aondump, idsMap, cb, ciclica, references, where);

			return idsMap.getOrder(fk.getKey().getTable().getName(), r.getValue((Field<Integer>) fk.getFields().get(0), Integer.class));
		}

		return super.onErrFk(dslContext, r, fk, aondump, idsMap, cb, ciclica, references, where);
	}
	
	@Override
	public void downloadParent(DSLContext dslContext, Record r, ForeignKey<?, ?> fk, AonDump aondump, IdsMap idsMap, CallbackDump cb, List<Table<?>> ciclica, List<?> references, Condition whereParent) {
		Condition where ;
		if ( DEFAULTS_TABLES.contains(fk.getKey().getTable().getName()) )
			where = ((Field<Integer>)fk.getKey().getTable().field("domain")).eq(parentDomain);
		else 
			where = ((Field<Integer>)fk.getKey().getTable().field("id")).in(dslContext.select((Field<Integer>)fk.getFields().get(0)).from(fk.getTable())
					.where(whereParent))
					.and(((Field<Integer>)fk.getKey().getTable().field("domain")).eq(parentDomain));

		aondump.downloadTableReferenceDomain(fk.getKey().getTable(), fk.getKey().getTable().getReferences(), idsMap, cb, ciclica, where);

		this.cb.downloadParent(dslContext, r, fk, aondump, idsMap, cb, ciclica, references, where);
	}
	
}

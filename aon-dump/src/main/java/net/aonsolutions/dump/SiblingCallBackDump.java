package net.aonsolutions.dump;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

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
import org.jooq.impl.DSL;

public class SiblingCallBackDump extends AbstractChaimCallbackDump {

	private Integer parentDomain;

	public SiblingCallBackDump(CallbackDump cb) {
		super(cb);

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
	
		SelectConditionStep<Record1<Integer>> select = dslContext.selectOne().from(fk.getKey().getTable().getName())
					.where((((Field<Integer>) fk.getKey().getTable().field("domain")).equal(parentDomain))
					.and(((Field<Integer>) fk.getKey().getFields().get(0))
							.eq(r.getValue((Field<Integer>) fk.getFields().get(0)))));

		fkField = select.fetchAny();

		if (fkField != null)
			return DSL.cast(r.getValue((Field<Integer>) fk.getFields().get(0)), Integer.class);
				
		return super.onErrFk(dslContext, r, fk, aondump, idsMap, cb, ciclica, references, where);
	}
	
}

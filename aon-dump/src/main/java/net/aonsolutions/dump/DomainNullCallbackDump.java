package net.aonsolutions.dump;

import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.Table;
import org.jooq.impl.DSL;

public class DomainNullCallbackDump extends AbstractChaimCallbackDump {

	public DomainNullCallbackDump(CallbackDump cb) {
		super(cb);
	}

	@Override
	public Field<Integer> onErrFk(DSLContext dslContext, Record r, ForeignKey<?, ?> fk, AonDump aondump, IdsMap idsMap,
			CallbackDump cb, List<Table<?>> ciclica, List<?> references, Condition where) {

		Record1<?> fkField;
		
		Integer value = r.getValue((Field<Integer>) fk.getFields().get(0), Integer.class);

		Field<Integer> domainField = (Field<Integer>) fk.getKey().getTable().field("domain");
		if (domainField == null)
			return DSL.cast(value, Integer.class);
		
		
		SelectConditionStep<Record1<Integer>> select = dslContext.selectOne()
					.from(fk.getKey().getTable())
					.where(((domainField).isNull()))
					.and(((Field<Integer>) fk.getKey().getFields().get(0))
							.eq(r.getValue((Field<Integer>) fk.getFields().get(0), Integer.class)));

		fkField = select.fetchAny();

		if (fkField != null)
			return DSL.cast(value, Integer.class);
			
		return super.onErrFk(dslContext, r, fk, aondump, idsMap, cb, ciclica, references, where);
	}

}

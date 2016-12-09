package net.aonsolutions.dump;

import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Record;
import org.jooq.Table;

public class DomainCeroCallbackDump extends AbstractChaimCallbackDump {

	public DomainCeroCallbackDump(CallbackDump cb) {
		super(cb);

	}

	@Override
	public Field<Integer> onErrFk(DSLContext dslContext, Record r, ForeignKey<?, ?> fk, AonDump aondump, IdsMap idsMap,
			CallbackDump cb, List<Table<?>> ciclica, List<?> references, Condition where) {

		if ((Integer) r.get("domain") == 0){
			return (Field<Integer>) fk.getKey().getTable().field(0); 		
		}
		
		return super.onErrFk(dslContext, r, fk, aondump, idsMap, cb, ciclica, references, where);
	}

}

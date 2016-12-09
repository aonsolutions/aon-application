package net.aonsolutions.dump;

import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.ForeignKey;
import org.jooq.Record;
import org.jooq.Table;

public class SiblingCallbackDump extends AbstractChaimCallbackDump {
	
	public SiblingCallbackDump(CallbackDump cb) {
		super(cb);
	}

	@Override
	public void downloadParent(DSLContext dslContext, Record r, ForeignKey<?, ?> fk, AonDump aondump, IdsMap idsMap, CallbackDump cb, List<Table<?>> ciclica, List<?> references, Condition whereParent) {

		cb.downloadParent(dslContext, r, fk, aondump, idsMap, cb, ciclica, references, whereParent);
	}

}

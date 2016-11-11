package net.aonsolutions.dump;

import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Record;

public class ParentCallbackDump extends AbstractChaimCallbackDump{

	private CallbackDump  cb;
	
	public ParentCallbackDump(CallbackDump cb) {
		super(cb);
		this.cb = cb;
	}

	@Override
	public void downloadParent(DSLContext dslContext, Record r, ForeignKey<?, ?> fk, AonDump aondump, IdsMap idsMap, CallbackDump cb, boolean ciclica, List<?> references, Condition whereParent) {

		Condition where = ((Field<Integer>)fk.getKey().getTable().field("id")).in(dslContext.select((Field<Integer>)fk.getFields().get(0)).from(fk.getTable())
				.where(whereParent));
		
		
		aondump.downloadTableReferenceDomain(fk.getKey().getTable(), fk.getKey().getTable().getReferences(), idsMap, cb, ciclica, where);
		
		this.cb.downloadParent(dslContext, r, fk, aondump, idsMap, cb, ciclica, references, where);
	}
}

package net.aonsolutions.dump;

import java.util.List;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.UpdateConditionStep;

public abstract class AbstractChaimCallbackDump implements CallbackDump {

	protected CallbackDump  next;

	public AbstractChaimCallbackDump(CallbackDump  cb) {
		this.next =cb;
	}

	@Override
	public void header(Schema schema, String hostName, Map<Table<?>, Integer> domainTables, DSLContext dslContext,
			int id, IdsMap idsMap) {
		this.next.header(schema, hostName, domainTables, dslContext, id, idsMap);
	}

	@Override
	public void footer() {
		this.next.footer();
	}

	@Override
	public Field<Integer> onErrFk(DSLContext dslContext, Record r, ForeignKey<?, ?> fk, AonDump aondump, IdsMap idsMap,
			CallbackDump cb, List<Table<?>> ciclica, List<?> references, Condition where) {
		return this.next.onErrFk(dslContext, r, fk, aondump, idsMap, cb, ciclica, references, where);
	}

	@Override
	public void accept(InsertSetMoreStep<?> inSet, Table<?> table, List<Table<?>> ciclica, Integer numRows, String varTableName) {
		this.next.accept(inSet, table, ciclica, numRows, varTableName);
		
	}

	@Override
	public void onAttachInsert(UpdateConditionStep<?> update) {
		this.next.onAttachInsert(update);
	}

	@Override
	public void downloadParent(DSLContext dslContext, Record r, ForeignKey<?, ?> fk, AonDump aondump, IdsMap idsMap, // NOPMD by svaldepenas on 10/01/17 11:41
			CallbackDump cb, List<Table<?>> ciclica, List<?> references, Condition where) {
		this.next.downloadParent(dslContext, r, fk, aondump, idsMap, cb, ciclica, references, where);
	}

	@Override
	public void onNewRow(Table<?> t, Map<Field<?>, Object> insertMap, Record r) {
		this.next.onNewRow(t, insertMap, r);
	}
	
	@Override
	public void accept(String sql) {
		this.next.accept(sql);
	}


}
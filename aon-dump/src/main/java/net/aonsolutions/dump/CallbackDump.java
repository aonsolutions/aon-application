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

public interface CallbackDump {
	default void header(Schema schema, String hostName, Map<Table<?>, Integer> domainTables, DSLContext dslContext, int id, IdsMap idsMap){};
	default void footer(){};
	default Field<Integer> onErrFk(DSLContext dslContext, Record r, ForeignKey<?, ?> fk, AonDump aondump, IdsMap idsMap, CallbackDump cb, List<Table<?>> ciclica, List<?> references, Condition where){return null;};
	void accept(InsertSetMoreStep<?> inSet, Table<?> table, List<Table<?>> ciclica, Integer numRows, String varTableName);
	default void onAttachInsert(UpdateConditionStep<?> update){};
	default void downloadParent(DSLContext dslContext, Record r, ForeignKey<?, ?> fk, AonDump aondump, IdsMap idsMap, CallbackDump cb, List<Table<?>> ciclica, List<?> references, Condition where){}
	default void onNewRow(Table<?> t, Map<Field<?>, Object> insertMap, Record r){}
	default void accept(String sql){};
	
}

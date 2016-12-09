package net.aonsolutions.dump;

import java.io.PrintStream;
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

public class CommentsPrintCallbackDump extends AbstractPrintCallbackDump {

	private CallbackDump  cb;
	
	// Consumer that add comments to our sql file
	public CommentsPrintCallbackDump(PrintStream out, CallbackDump cb) {
		super(out);
		this.cb = cb;
	}

	@Override
	public void header (Schema schema, String hostName, Map<Table<?>, Integer> domainTables, DSLContext dslContext, int id, IdsMap idsMap) {
		
		println("-- --------------------------------------------------------------");
		println("-- Host: "+ hostName + "		Database: " + schema.getName());
		println("-- --------------------------------------------------------------");
		println();
		println("--");
		println("-- Initials Configurations");
		println("--");
		println();
		
		cb.header(schema, hostName, domainTables, dslContext, id, idsMap);
	}
	
	@Override
	public void accept(InsertSetMoreStep<?> inSet, Table<?> table, List<Table<?>> ciclica, Integer numRows) {
		
		println();
		println("--");
		println("-- Dumping data for table `" + table.getName() + "`");
		println("--");
		println();
		
		cb.accept(inSet, table, ciclica, numRows);
	}

	@Override
	public void footer() {
		cb.footer();
	}
	
	@Override
	public Field<Integer> onErrFk(DSLContext dslContext, Record r, ForeignKey<?, ?> fk, AonDump aondump, IdsMap idsMap, CallbackDump cb, List<Table<?>> ciclica, List<?> references, Condition where) {
		return this.cb.onErrFk(dslContext, r, fk, aondump, idsMap, cb, ciclica, references, where);
	}
	
	@Override
	public void onAttachInsert(UpdateConditionStep<?> update) {
		cb.onAttachInsert(update);
	}
	
	@Override
	public void downloadParent(DSLContext dslContext, Record r, ForeignKey<?, ?> fk, AonDump aondump,
			IdsMap idsMap, CallbackDump cb, List<Table<?>> ciclica, List<?> references, Condition where) {
		this.cb.downloadParent(dslContext, r, fk, aondump, idsMap, cb, ciclica, references, where);
	}
	
	@Override
	public void onNewRow(Table<?> t, Map<Field<?>, Object> insertMap, Record r) {
		cb.onNewRow(t, insertMap, r);
	}
	
	@Override
	public void accept(String sql) {
		cb.accept(sql);
	}
}

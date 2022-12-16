package com.esferalia.aon.occam.impl.jooq.console;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.AONContext.UnpooledCloseableAONContext;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ConsoleIDsTableInfo {

	public static final String SCHEMA = "domainextract-aonsolutions-net";
	
	private UnpooledCloseableAONContext ctx;
	private String tableName;
	private Table<Record> table;
	private Field<String> tableColumn;
	private Field<Integer> oldIdColumn;
	private Field<Integer> newIdColumn;
	
	public void initialize(ConsoleParams params) {
		tableName = obtainTableName(params);
		table = DSL.table(tableName);
		tableColumn = DSL.field(tableName + ".table_name", String.class);
		oldIdColumn = DSL.field(tableName + ".old_id", Integer.class);
		newIdColumn = DSL.field(tableName + ".new_id", Integer.class);
	}
	
	private String obtainTableName(ConsoleParams params) {
		StringBuilder buf = new StringBuilder();
		buf.append(AonStringUtils.substringBefore(params.getFromConnection().getSchemaName(), AonStringUtils.HYPHEN));
		buf.append(AonStringUtils.UNDERSCORE);
		buf.append(params.getFromConnection().getDomain().getId());
		buf.append(AonStringUtils.UNDERSCORE);
		buf.append(AonStringUtils.substringBefore(params.getToConnection().getSchemaName(), AonStringUtils.HYPHEN));
		buf.append(AonStringUtils.UNDERSCORE);
		buf.append(params.getToConnection().getDomain().getId());
		return buf.toString();
	}
	
	public String getTableName() {
		return tableName;
	}

	public Table<Record> getTable() {
		return table;
	}

	public Field<String> getTableColumn() {
		return tableColumn;
	}

	public Field<Integer> getOldIdColumn() {
		return oldIdColumn;
	}

	public Field<Integer> getNewIdColumn() {
		return newIdColumn;
	}


	public UnpooledCloseableAONContext getCtx() {
		return ctx;
	}
	public ConsoleIDsTableInfo setCtx(UnpooledCloseableAONContext ctx) {
		this.ctx = ctx;
		return this;
	}

	
}

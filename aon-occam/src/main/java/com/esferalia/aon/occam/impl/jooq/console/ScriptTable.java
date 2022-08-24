package com.esferalia.aon.occam.impl.jooq.console;

import java.util.HashSet;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Record;
import org.jooq.Table;

class ScriptTable {
	
	private Table<Record> table;
	private Field<Integer> primaryKey;
	private List<ForeignKey<Record,?>> references;
	private HashSet<Field<?>> referenceColumns;
	private int rows;
	private int currentRow;
	private int percent;
	
	public ScriptTable(Table<Record> table) {
		this.table = table;
	}
	public Table<Record> getTable() {
		return table;
	}
	public String getTableName() {
		return table.getName();
	}
	public Field<Integer> getPrimaryKey() {
		return primaryKey;
	}
	public ScriptTable setPrimaryKey(Field<Integer> primaryKey) {
		this.primaryKey = primaryKey;
		return this;
	}
	public List<ForeignKey<Record,?>> getReferences() {
		return references;
	}
	public ScriptTable setReferences(List<ForeignKey<Record,?>> references) {
		this.references = references;
		return this;
	}
	
	public HashSet<Field<?>> getReferenceColumns() {
		return referenceColumns;
	}
	public ScriptTable setReferenceColumns(HashSet<Field<?>> referenceColumns) {
		this.referenceColumns = referenceColumns;
		return this;
	}
	public int getRows() {
		return rows;
	}
	public ScriptTable setRows(int rows) {
		this.rows = rows;
		return this;
	}
	public int getCurrentRow() {
		return currentRow;
	}
	public ScriptTable setCurrentRow(int currentRow) {
		this.currentRow = currentRow;
		return this;
	}
	public int getPercent() {
		return percent;
	}
	public ScriptTable setPercent(int percent) {
		this.percent = percent;
		return this;
	}
	
}

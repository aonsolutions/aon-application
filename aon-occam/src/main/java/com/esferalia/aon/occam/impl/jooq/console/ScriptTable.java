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
	private int totalProgress;
	private int progress;
	private int lastMessagePercent;
	
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
	
	public int getTotalProgress() {
		return totalProgress;
	}
	public ScriptTable setTotalProgress(int rows) {
		this.totalProgress = rows;
		return this;
	}
	
	public int getProgress() {
		return progress;
	}
	public ScriptTable setProgress(int currentRow) {
		this.progress = currentRow;
		return this;
	}
	
	public int getLastMessagePercent() {
		return lastMessagePercent;
	}
	public void setLastMessagePercent(int lastMessagePercent) {
		this.lastMessagePercent = lastMessagePercent;
	}
}

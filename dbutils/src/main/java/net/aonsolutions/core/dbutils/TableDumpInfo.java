package net.aonsolutions.core.dbutils;

public class TableDumpInfo {
	
	private boolean begin;
	
	private boolean end;
	
	private int rows;
	
	public TableDumpInfo() {
		this.begin = true;
		this.end = true;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}
	
	public void incRows() {
		this.rows++;
	}

	public boolean isFirstInsert() {
		return this.rows == 0;
	}

	public boolean isBegin() {
		return begin;
	}

	public void setBegin(boolean begin) {
		this.begin = begin;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

}

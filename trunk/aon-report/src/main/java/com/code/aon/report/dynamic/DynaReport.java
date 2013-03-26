package com.code.aon.report.dynamic;

import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;

public class DynaReport {
	private FastReportBuilder report;
	
	public FastReportBuilder getReport() {
		if (report == null) {
			report = new FastReportBuilder(); 
		}
		return report;
	}
	
	public DynaReport addField( String name, Class<?> clazz) {
		getReport().addField(name,clazz.getName());
		return this;
	}
	
	public DynaReport addColumn( AbstractColumn column ) {
		getReport().addColumn(column);
		return this;
	}
	
}

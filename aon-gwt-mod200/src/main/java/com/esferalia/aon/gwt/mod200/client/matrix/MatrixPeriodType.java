package com.esferalia.aon.gwt.mod200.client.matrix;

import com.esferalia.aon.occam.api.model.type.Period;

enum MatrixPeriodType {
	 MONTHLY	("Mensual"	,12	,Period::isMonthPeriod)
	,QUARTERLY	("Trimes."	, 4	,Period::isQuarterPeriod)
	,YEARLY		("Anual"	, 1	,period -> period == Period.YEAR)
	;
	private String value;
	private int arraySize;
	private IPeriodTypeAccepter accepter;
	private MatrixPeriodType( String value, int arraySize, IPeriodTypeAccepter accepter) {
		this.value = value;
		this.arraySize = arraySize;
		this.accepter = accepter;
	}
	protected String getValue() {
		return value;
	}
	protected int getArraySize() {
		return arraySize;
	}
	protected int getIndex(Period period) {
		if (period == Period.YEAR) return 0;
		if (period.isMonthPeriod()) return period.ordinal();
		return (period.ordinal() - 12);
	}
	protected Period getInitialPeriod() {
		if (this == MONTHLY) return Period.M01; 
		else if (this == QUARTERLY) return Period.T1;
		return Period.YEAR;
		
	}
	protected static MatrixPeriodType getPeriodType(Period period) {
		for (MatrixPeriodType type : MatrixPeriodType.values()) {
			if (type.accepter.accept(period)) return type;
		}
		return YEARLY;
	}
	
	@FunctionalInterface
	private static interface IPeriodTypeAccepter {
		boolean accept(Period mod);
	}
	
}

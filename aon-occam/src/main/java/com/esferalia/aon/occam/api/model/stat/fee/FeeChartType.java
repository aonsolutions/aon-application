package com.esferalia.aon.occam.api.model.stat.fee;

import java.io.Serializable;

public enum FeeChartType implements Serializable {

	 FEE_TYPE("Cuotas por meses", new ITypeVisitor() {
		@Override
		public void visit(IFeeChartTypeVisitor chartVisitor) {
			chartVisitor. visitFeeType();
		}
	 })
	;
	
	public static interface ITypeVisitor {
		void visit(IFeeChartTypeVisitor chartVisitor);
	}
	
	private String description;
	private ITypeVisitor typeVisitor;
	
	private FeeChartType(String description,ITypeVisitor typeVisitor ) {
		this.description = description;
		this.typeVisitor = typeVisitor;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void visit(IFeeChartTypeVisitor chartVisitor) {
		typeVisitor.visit(chartVisitor);
	}
	
	public Byte value() {
		return (byte) this.ordinal();
	}
}

package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum FiscalModelType implements Serializable{
	
	M111	("111","111"		, false){ @Override public void visit(FiscalModelTypeVisitor visitor) { visitor.visitM111();} }, 
	M115	("115","115"		, false){ @Override public void visit(FiscalModelTypeVisitor visitor) { visitor.visitM115();} }, 
	M123	("123","123"		, false){ @Override public void visit(FiscalModelTypeVisitor visitor) { visitor.visitM123();} }, 
	M130	("130","130"		, false){ @Override public void visit(FiscalModelTypeVisitor visitor) { visitor.visitM130();} },
	M131	("131","131"		, false){ @Override public void visit(FiscalModelTypeVisitor visitor) { visitor.visitM131();} },
	M303_RG	("303","303 R.G."	, false){ @Override public void visit(FiscalModelTypeVisitor visitor) { } },
	M303_RS	("303","303 R.S."	, false){ @Override public void visit(FiscalModelTypeVisitor visitor) { } },
	M340	("340","340"		, false){ @Override public void visit(FiscalModelTypeVisitor visitor) { } },
	M347	("347","347"		, true ){ @Override public void visit(FiscalModelTypeVisitor visitor) { visitor.visitM347();} },
	M349	("349","349"		, false){ @Override public void visit(FiscalModelTypeVisitor visitor) { visitor.visitM349();} },
	M390	("390","390"		, true ){ @Override public void visit(FiscalModelTypeVisitor visitor) { visitor.visitM390();} },
	M390_HF	("390","390 H.F."	, true ){ @Override public void visit(FiscalModelTypeVisitor visitor) { visitor.visitM390HF();} },
	M180	("180","180"		, true ){ @Override public void visit(FiscalModelTypeVisitor visitor) { visitor.visitM180();} },
	M184	("184","184"		, true ){ @Override public void visit(FiscalModelTypeVisitor visitor) { visitor.visitM184();} },
	M190	("190","190"		, true ){ @Override public void visit(FiscalModelTypeVisitor visitor) { visitor.visitM190();} },
	M193	("193","193"		, true ){ @Override public void visit(FiscalModelTypeVisitor visitor) { visitor.visitM193();} },
	M310	("310","310"		, false){ @Override public void visit(FiscalModelTypeVisitor visitor) { } },
	M311	("311","311"		, false){ @Override public void visit(FiscalModelTypeVisitor visitor) { } },
	M200	("200","200"		, true ){ @Override public void visit(FiscalModelTypeVisitor visitor) { visitor.visitM200();} },
	M202	("202","202"		, false){ @Override public void visit(FiscalModelTypeVisitor visitor) { visitor.visitM202();} },
	M303    ("IVA","IVA"		, false){ @Override public void visit(FiscalModelTypeVisitor visitor) { visitor.visitM303();} },
	M140	("140","140"		, false){ @Override public void visit(FiscalModelTypeVisitor visitor) { } },
	M240	("240","240"		, false){ @Override public void visit(FiscalModelTypeVisitor visitor) { } },
	SII		("SII","SII"		, false){ @Override public void visit(FiscalModelTypeVisitor visitor) { } }
	;

	private String value;
	private String name;
	private boolean yearly;
	
	private FiscalModelType(String value,String name, boolean yearly) {
		this.value = value;
		this.name = name;
		this.yearly = yearly;
	}
	public String getValue() {
		return value;
	}
	public String getName() {
		return name;
	}
	public boolean isYearly() {
		return yearly;
	}
	public static Optional<FiscalModelType> value(String value) {
		if (value == null) Optional.empty();
		for (FiscalModelType t : FiscalModelType.values()) {
			if (AonStringUtils.equals(value, t.getValue())) return Optional.of(t);
		}
		return Optional.empty();
	}

	public static Optional<FiscalModelType> valueByName(String name) {
		if (name == null) return Optional.empty();
		for (FiscalModelType t : FiscalModelType.values()) {
			if (AonStringUtils.equals(name, t.getName())) return Optional.of(t);
		}
		return Optional.empty();
	}

	public boolean isMonthly(Administration admon) {
		return this != M130 && this != M131 && this != M202;
	}
	
	public boolean isOtherDeponentAllowedInSamePeriod() {
		return this == M130 || this == M131;
	}
	
	public boolean isVat() {
		return this == M303_RG 
			|| this == M303_RS 
			|| this == M340 
			|| this == M347 
			|| this == M349 
			|| this == M390
			|| this == M390_HF 
			|| this == M310 
			|| this == M311 
			|| this == M303;				
	}

	public boolean isRetention() {
		return this == M111	 
			|| this == M115	 
			|| this == M123
			|| this == M130
			|| this == M131
			|| this == M180
			|| this == M184
			|| this == M190
			|| this == M193
			|| this == M200
			|| this == M202;
	}
	
	public boolean isInformative() {
		return this == M347 
			|| this == M349 
			|| this == M390
			|| this == M390_HF 
			|| this == M180 
			|| this == M184 
			|| this == M190 
			|| this == M193;
	}
	
	public abstract void visit(FiscalModelTypeVisitor visitor);
	public static interface FiscalModelTypeVisitor {
		void visitM111();
		void visitM115();
		void visitM123();
		void visitM130();
		void visitM131();
		void visitM347();
		void visitM349();
		void visitM390();
		void visitM390HF();
		void visitM180();
		void visitM184();
		void visitM190();
		void visitM193();
		void visitM200();
		void visitM202();
		void visitM303();
	}
}

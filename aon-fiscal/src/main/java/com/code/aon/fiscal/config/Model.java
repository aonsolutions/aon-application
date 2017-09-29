package com.code.aon.fiscal.config;

public enum Model {
	
	M111("111",null,"mod111Report"), 
	M115("115",null,"mod115Report"), 
	M123("123",null,"mod123Report"), 
	M130("130",null,"mod130Report"),
	M131("131",null,"mod131Report"),
	M303_RG("303 R.G.",null,"vatTaxReport"),
	M303_RS("303 R.S.",null,"mod303Report"),
	M340("340",null,null),
	M347("347",null,"mod347List"),
	M349("349",null,"mod349List"),
	M390("390",null,null),
	M390_HF("390 H.F.",null,"vatTaxReport"),
	M180("180",null,null),
	M190("190",null,null),
	M310("310",2013,null),
	M311("311",2013,null),
	M200("200",2013,null),
	M202("202",null,null),
	M184("184",null,null),
	M193("193",null,null),
	MIVA("IVA New",null,null)
	;

	private String name;
	private Integer deprecatedYear;
	private String reportKey;
	
	private Model(String name,Integer deprecatedYear,String reportKey) {
		this.name = name;
		this.deprecatedYear = deprecatedYear;
		this.reportKey = reportKey;
	}
	public String getName() {
		return name;
	}
	public Integer getDeprecatedYear() {
		return deprecatedYear;
	}
	public String getReportKey() {
		return reportKey;
	}
}
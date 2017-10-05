package com.code.aon.fiscal.config;

public enum Model {
	
	M111("111",null), 
	M115("115",null), 
	M123("123",null), 
	M130("130",null),
	M131("131",null),
	M303_RG("303 R.G.",null),
	M303_RS("303 R.S.",null),
	M340("340",null),
	M347("347",null),
	M349("349",null),
	M390("390",null),
	M390_HF("390 H.F.",null),
	M180("180",null),
	M190("190",null),
	M310("310",2013),
	M311("311",2013),
	M200("200",2013),
	M202("202",null),
	M184("184",null),
	M193("193",null),
	MIVA("IVA New",null)
	;

	private String name;
	private Integer deprecatedYear;
	
	private Model(String name,Integer deprecatedYear) {
		this.name = name;
		this.deprecatedYear = deprecatedYear;
	}
	public String getName() {
		return name;
	}
	public Integer getDeprecatedYear() {
		return deprecatedYear;
	}
}
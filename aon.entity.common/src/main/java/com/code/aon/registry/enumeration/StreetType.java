package com.code.aon.registry.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum StreetType implements IResourceable, IStringEnum  {

	AQ("AQ"),
	AC("AC"),
	AL("AL"),
	AD("AD"),
	AM("AM"),
	AN("AN"),
	AP("AP"),
	AT("AT"),
	AV("AV"),
	BA("BA"),
	BC("BC"),
	BD("BD"),
	BO("BO"),
	BL("BL"),
	CL("CL"),
	CA("CA"),
	CJ("CJ"),
	CE("CE"),
	CZ("CZ"),
	CM("CM"),
	CR("CR"),
	CT("CT"),
	CS("CS"),
	CH("CH"),
	CO("CO"),
	CP("CP"),
	KO("KO"),
	CN("CN"),
	CU("CU"),
	ED("ED"),
	EA("EA"),
	ES("ES"),
	EL("EL"),
	ET("ET"),
	GL("GL"),
	GR("GR"),
	LL("LL"),
	LG("LG"),
	MZ("MZ"),
	MC("MC"),
	MO("MO"),
	MN("MN"),
	ZZ("ZZ"),
	PQ("PQ"),
	PC("PC"),
	PD("PD"),
	PJ("PJ"),
	PA("PA"),
	PO("PO"),
	PI("PI"),
	PS("PS"),
	PT("PT"),
	PL("PL"),
	PZ("PZ"),
	PE("PE"),
	PU("PU"),
	PB("PB"),
	PG("PG"),
	PR("PR"),
	PV("PV"),
	PN("PN"),
	RA("RA"),
	RB("RB"),
	RP("RP"),
	RR("RR"),
	RN("RN"),
	RC("RC"),
	RD("RD"),
	SC("SC"),
	SD("SD"),
	SR("SR"),
	XX("XX"),
	SU("SU"),
	TT("TT"),
	TL("TL"),
	TS("TS"),
	TR("TR"),
	UR("UR"),
	VI("VI"),
	VL("VL"),
	ZO("ZO");
	
	private String value;
	
	private StreetType(String value) {
		this.value = value;	
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	private static final String BASE_NAME = "com.code.aon.registry.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_streettype_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
}
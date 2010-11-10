package com.code.aon.registry.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum StreetType implements IResourceable, IStringEnum  {

	AC("AC"),
	AD("AD"),
	AL("AL"),
	AM("AM"),
	AN("AN"),
	AP("AP"),
	AQ("AQ"),
	AT("AT"),
	AV("AV"),
	BA("BA"),
	BC("BC"),
	BD("BD"),
	BL("BL"),
	BO("BO"),
	CA("CA"),
	CE("CE"),
	CH("CH"),
	CJ("CJ"),
	CL("CL"),
	CM("CM"),
	CN("CN"),
	CO("CO"),
	CP("CP"),
	CR("CR"),
	CS("CS"),
	CT("CT"),
	CU("CU"),
	CZ("CZ"),
	EA("EA"),
	ED("ED"),
	EL("EL"),
	ES("ES"),
	ET("ET"),
	GL("GL"),
	GR("GR"),
	KO("KO"),
	LG("LG"),
	LL("LL"),
	MC("MC"),
	MN("MN"),
	MO("MO"),
	MZ("MZ"),
	PA("PA"),
	PB("PB"),
	PC("PC"),
	PD("PD"),
	PE("PE"),
	PG("PG"),
	PI("PI"),
	PJ("PJ"),
	PL("PL"),
	PN("PN"),
	PO("PO"),
	PQ("PQ"),
	PR("PR"),
	PS("PS"),
	PT("PT"),
	PU("PU"),
	PV("PV"),
	PZ("PZ"),
	RA("RA"),
	RB("RB"),
	RC("RC"),
	RD("RD"),
	RN("RN"),
	RP("RP"),
	RR("RR"),
	SC("SC"),
	SD("SD"),
	SR("SR"),
	SU("SU"),
	TL("TL"),
	TR("TR"),
	TS("TS"),
	TT("TT"),
	UR("UR"),
	VI("VI"),
	VL("VL"),
	XX("XX"),
	ZO("ZO"),
	ZZ("ZZ");
	
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
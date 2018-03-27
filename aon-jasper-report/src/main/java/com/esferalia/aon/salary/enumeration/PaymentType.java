package com.esferalia.aon.salary.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

public enum PaymentType {
	CRA_0000,
	CRA_0001,
	CRA_0002,
	CRA_0003,
	CRA_0004,
	CRA_0005,
	CRA_0006,
	CRA_0007,
	CRA_0008,
	CRA_0009,
	CRA_0010,
	CRA_0011,
	CRA_0012,
	CRA_0013,
	CRA_0014,
	CRA_0015,
	CRA_0016,
	CRA_0017,
	CRA_0018,
	CRA_0019,
	CRA_0020,
	CRA_0021,
	CRA_0022,
	CRA_0023,
	CRA_0024,
	CRA_0025,
	CRA_0026,
	CRA_0027,
	CRA_0028,
	CRA_0029,
	CRA_0030,
	CRA_0031,
	CRA_0032,
	CRA_0033,
	CRA_0034,
	CRA_0035, // 
	CRA_0036,
	CRA_0037,
	CRA_0038,
	CRA_0039,
	CRA_0040,
	CRA_0041,
	CRA_0042,
	CRA_0043,
	CRA_0044,
	CRA_0045,
	CRA_0046,
	CRA_0047,
	CRA_0048,
	CRA_0049,
	CRA_0050,
	CRA_0051,
	CRA_0052,
	CRA_0053,
	CRA_0054,
	CRA_0055,
	CRA_0056;

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("com.code.aon.common.i18n.enum", locale);
        return bundle.getString(toString());
    }
	
}

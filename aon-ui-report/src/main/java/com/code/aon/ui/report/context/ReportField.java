package com.code.aon.ui.report.context;

import com.code.aon.common.enumeration.DateMask;
import com.code.aon.common.enumeration.DecimalMask;
import com.code.aon.common.enumeration.IMask;
import com.code.aon.common.enumeration.IntegerMask;
import com.code.aon.common.enumeration.TimeMask;

public class ReportField {
	
	private String name;
	
	private String fieldlabel;
	
	private String maskEnum;
	
	private String mask;

	public String getFieldlabel() {
		return fieldlabel;
	}

	public void setFieldlabel(String fieldlabel) {
		this.fieldlabel = fieldlabel;
	}
	
	public boolean isCompositeField() {
		return name.indexOf('.') != -1;
	}

	public String getVelocityName() {
		int pos = name.indexOf('.');
		if ( pos != -1 ) {
			return name.substring(0, pos);
		}
		return name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMaskEnum() {
		return maskEnum;
	}

	public void setMaskEnum(String type) {
		this.maskEnum = type;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}
	
	public IMask getIMask() {
		if ( DateMask.class.getName().equals(getMaskEnum()) ) {
			return DateMask.valueOf(getMask());
		} else if ( TimeMask.class.getName().equals(getMaskEnum()) ) {
			return TimeMask.valueOf(getMask());
		} else if ( IntegerMask.class.getName().equals(getMaskEnum()) ) {
			return IntegerMask.valueOf(getMask());
		} else if ( DecimalMask.class.getName().equals(getMaskEnum()) ) {
			return DecimalMask.valueOf(getMask());
		}
		return null;
	}
	
}

package com.code.aon.fiscal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.entity.master.FiscalActivityInfoDB;

@Entity
@Table(name="fs_activity_info")
public class FiscalActivityInfo extends FiscalActivityInfoDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Transient
	public Object cast() throws NumberFormatException{
		Object ret = getValue();	
		if (getValue() != null) {
			if (Integer.class == getInfoKey().getClazz()) {
				ret = Integer.parseInt(getValue());
			} else if (Double.class == getInfoKey().getClazz()) {
				ret = Double.parseDouble(getValue());
			}
		}
		return ret;
	}

	@Transient
	public Double getDoubleValue() {
		if (StringUtils.isBlank( getValue() )) {
			return 0.0;
		}
		try {
			return Double.parseDouble(getValue());
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}
	public void setDoubleValue(Double doubleValue) {
		if (doubleValue == null) {
			doubleValue = 0.0;
		}
		setValue( Double.toString( doubleValue ) );
		setBase( CommonUtil.round( doubleValue * getFactor()));

	}
	
	@Transient
	public boolean isTitle() {
		return getInfoKey() != null?getInfoKey().isTitle() : false;
	}
	
	@Transient
	public boolean isCalculated() {
		return getInfoKey() != null?getInfoKey().isCalculated() : false;
	}
	
	@Transient
	public boolean isRequired() {
		return getInfoKey() != null?getInfoKey().isRequired() : false;
	}

	@Transient
	public boolean isChoice() {
		return getInfoKey() != null?getInfoKey().isChoice() : false;
	}

	@Transient
	public boolean isRounded() {
		return getInfoKey() != null?getInfoKey().isRounded() : false;
	}
}

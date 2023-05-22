package com.code.aon.finance;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.finance.enumeration.PosDisplayMode;
import com.esferalia.aon.entity.master.PosDB;

@Entity
@Table(name="pos")
public class Pos extends PosDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public Pos() {
		setDisplayMode(PosDisplayMode.STANDARD);
		setActive(true);
	}

	@Transient
	public boolean isStandard() {
		return PosDisplayMode.STANDARD == getDisplayMode();
	}
	@Transient
	public boolean isTouchScreen() {
		return PosDisplayMode.TOUCHSCREEN == getDisplayMode();
	}

	@Transient
	public int getLimit() {
		return getNumRows() * getNumCols();
	}

}

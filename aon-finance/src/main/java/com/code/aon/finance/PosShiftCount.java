package com.code.aon.finance;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.PosShiftCountDB;

@Entity
@Table(name="pos_shift_count")
public class PosShiftCount extends PosShiftCountDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean skipCheckPosShift;

	@Transient
	public boolean isSkipCheckPosShift() {
		return skipCheckPosShift;
	}
	public void setSkipCheckPosShift(boolean skipCheckPosShift) {
		this.skipCheckPosShift = skipCheckPosShift;
	}

}

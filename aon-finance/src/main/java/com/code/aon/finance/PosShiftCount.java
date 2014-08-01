package com.code.aon.finance;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.PosShiftCountDB;

@Entity
@Table(name="pos_shift_count")
public class PosShiftCount extends PosShiftCountDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}

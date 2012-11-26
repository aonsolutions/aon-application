package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.PosShiftCountDB;

@Entity
@Table(name="pos_shift_count")
public class PosShiftCount extends PosShiftCountDB {

	private static final long serialVersionUID = 1L;

}

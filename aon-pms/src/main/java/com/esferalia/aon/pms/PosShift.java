package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.PosShiftDB;

@Entity
@Table(name="pos_shift")
public class PosShift extends PosShiftDB {

	private static final long serialVersionUID = 1L;

}

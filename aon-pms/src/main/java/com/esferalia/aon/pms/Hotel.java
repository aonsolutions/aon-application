package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.config.IScopable;
import com.esferalia.aon.entity.master.HotelDB;

@Entity
@Table(name="hotel")
public class Hotel extends HotelDB implements IScopable {

	private static final long serialVersionUID = 1L;


}

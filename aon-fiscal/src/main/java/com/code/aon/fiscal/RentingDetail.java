package com.code.aon.fiscal;


import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.RentingDetailDB;

@Entity
@Table(name="fs_renting_detail")
public class RentingDetail extends RentingDetailDB {
	
	private static final long serialVersionUID = 1L;

}

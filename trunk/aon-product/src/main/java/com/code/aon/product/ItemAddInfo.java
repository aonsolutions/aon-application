package com.code.aon.product;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ItemAddInfoDB;

@Entity
@Table(name="item_addinfo")
public class ItemAddInfo extends ItemAddInfoDB {

	private static final long serialVersionUID = 1L;

	public ItemAddInfo() {
		setValueDate(new Date());
	}

}

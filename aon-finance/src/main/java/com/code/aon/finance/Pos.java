package com.code.aon.finance;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.finance.enumeration.PosDisplayMode;
import com.esferalia.aon.entity.master.PosDB;

@Entity
@Table(name="pos")
public class Pos extends PosDB {

	private static final long serialVersionUID = 1L;

	public Pos() {
		setActive(true);
	}

	@Transient
	public boolean isShop() {
		return PosDisplayMode.SHOP == getDisplayMode();
	}
	@Transient
	public boolean isBarRestaurant() {
		return PosDisplayMode.BAR_RESTAURANT == getDisplayMode();
	}

	@Transient
	public int getLimit() {
		return getNumRows() * getNumCols();
	}

}

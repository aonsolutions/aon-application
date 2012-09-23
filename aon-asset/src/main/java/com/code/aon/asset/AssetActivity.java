package com.code.aon.asset;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.esferalia.aon.entity.master.AssetActivityDB;

@Entity
@Table(name="asset_activity")
public class AssetActivity extends AssetActivityDB {

	private static final long serialVersionUID = 1L;
	
	private boolean check;

	@Transient
	public boolean isCheck() {
		return check;
	}

	@Transient
	public void setCheck(boolean check) {
		this.check = check;
	}

}

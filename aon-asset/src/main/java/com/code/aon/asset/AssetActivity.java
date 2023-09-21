package com.code.aon.asset;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.AssetActivityDB;

@Entity
@Table(name="asset_activity")
public class AssetActivity extends AssetActivityDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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

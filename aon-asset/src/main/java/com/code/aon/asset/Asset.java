package com.code.aon.asset;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.AssetDB;

@Entity
@Table(name="asset")
public class Asset extends AssetDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
}

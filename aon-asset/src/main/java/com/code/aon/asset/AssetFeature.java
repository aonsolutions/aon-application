package com.code.aon.asset;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.AssetFeatureDB;

@Entity
@Table(name = "asset_feature")
public class AssetFeature extends AssetFeatureDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
package com.code.aon.asset;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.AssetFeatureDB;

@Entity
@Table(name = "asset_feature")
public class AssetFeature extends AssetFeatureDB {

	private static final long serialVersionUID = 1L;

}
package com.code.aon.asset;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.FeatureDB;

@Entity
@Table(name = "feature")
public class Feature extends FeatureDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;


}
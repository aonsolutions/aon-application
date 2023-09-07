package com.code.aon.company;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.InvestAssetDB;

@Entity
@Table(name="invest_asset")
public class InvestAsset extends InvestAssetDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}

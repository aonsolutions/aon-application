package com.code.aon.marketing;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.MarketingActionDB;

@Entity
@Table(name="mk_action")
public class MarketingAction extends MarketingActionDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}
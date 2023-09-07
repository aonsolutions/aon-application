package com.code.aon.groupware;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.CampaignTypeDB;

@Entity
@Table(name="campaign_type")
public class CampaignType extends CampaignTypeDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
    
    public CampaignType() {
		setActive(true);
	}
	
}
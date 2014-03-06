package com.code.aon.groupware;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.CampaignTypeDB;

@Entity
@Table(name="campaign_type")
public class CampaignType extends CampaignTypeDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
    
    public CampaignType() {
		setActive(true);
	}
	
}
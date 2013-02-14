package com.code.aon.groupware;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.CampaignTypeDB;

@Entity
@Table(name="campaign_type")
public class CampaignType extends CampaignTypeDB {
	
	private static final long serialVersionUID = 1L;
    
    public CampaignType() {
		setActive(true);
	}
	
}
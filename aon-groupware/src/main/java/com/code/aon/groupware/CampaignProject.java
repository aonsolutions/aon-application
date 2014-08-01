package com.code.aon.groupware;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.CampaignProjectDB;

@Entity
@Table(name="campaign_project")
public class CampaignProject extends CampaignProjectDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
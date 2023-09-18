package com.code.aon.marketing;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.MarketingCampaignDB;

@Entity
@Table(name="mk_campaign")
public class MarketingCampaign extends MarketingCampaignDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private List<MarketingAction> actions;	

    public MarketingCampaign() {
    	setActive( true );
    }

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REMOVE }, mappedBy = "campaign")
	@org.hibernate.annotations.Cascade( {
			org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	public List<MarketingAction> getActions() {
		return actions;
	}
	public void setActions(List<MarketingAction> actions) {
		this.actions = actions;
	}
	
}
package com.code.gbp.report;

import java.util.List;

import com.code.aon.common.ITransferObject;
import com.code.gbp.Campaign;

public class CampaignReport implements ITransferObject {

	private Campaign campaign;
	
	private List<ITransferObject> suppliers;

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public List<ITransferObject> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<ITransferObject> suppliers) {
		this.suppliers = suppliers;
	}

}

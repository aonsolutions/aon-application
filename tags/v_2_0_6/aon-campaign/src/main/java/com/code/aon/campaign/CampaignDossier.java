package com.code.aon.campaign;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.project.Dossier;

@Entity
@Table(name="campaign_dossier")
public class CampaignDossier implements ITransferObject {

	private Integer id;
	
	private Campaign campaign;
	
	private Dossier dossier;
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="campaign", nullable=false)
	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	@ManyToOne
	@JoinColumn(name="dossier", nullable=false)
	public Dossier getDossier() {
		return dossier;
	}

	public void setDossier(Dossier dossier) {
		this.dossier = dossier;
	}

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CampaignDossier) {
            return (this.id.equals(((CampaignDossier)obj).getId()));
        }
        return false;
    }

}
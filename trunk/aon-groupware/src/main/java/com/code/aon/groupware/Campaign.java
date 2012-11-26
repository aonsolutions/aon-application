package com.code.aon.groupware;


import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.groupware.enumeration.CampaignStatus;
import com.esferalia.aon.entity.master.CampaignDB;

@Entity
@Table(name="campaign")
public class Campaign extends CampaignDB {
	
	private static final long serialVersionUID = 1L;

    @Transient
    public boolean isPending() {
        return (this.getStatus() == CampaignStatus.PENDING);
    }
    
    @Transient
    public boolean isInProgress() {
        return (this.getStatus() == CampaignStatus.IN_PROGRESS);
    }

    @Transient
    public boolean isFinished() {
        return (this.getStatus() == CampaignStatus.FINISHED);
    }

    @Transient
    public boolean isDeleted() {
        return (this.getStatus() == CampaignStatus.DELETED);
    }
    
}
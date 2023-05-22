package com.code.aon.groupware;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.groupware.enumeration.CampaignStatus;
import com.esferalia.aon.entity.master.CampaignDB;

@Entity
@Table(name="campaign")
public class Campaign extends CampaignDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
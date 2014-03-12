package com.code.aon.commercial;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.CommercialTrackingDB;

@Entity
@Table(name="commercial_tracking")
public class CommercialTracking extends CommercialTrackingDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public CommercialTracking() {
		setStatus(CommercialTrackingStatus.PENDING);
	}

}
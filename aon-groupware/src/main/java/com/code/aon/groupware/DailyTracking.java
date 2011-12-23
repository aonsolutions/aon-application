package com.code.aon.groupware;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.entity.master.DailyTrackingDB;

@Entity
@Table(name="daily_tracking")
public class DailyTracking extends DailyTrackingDB {
	
	private static final long serialVersionUID = 1L;

	@Transient
	public Double getAmount() {
		return CommonUtil.round(getTrackingDuration() * getCost());
	}
	
}
package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.esferalia.aon.entity.master.ProjectReservationServiceDB;

@Entity
@Table(name="project_reservation_service")
public class ProjectReservationService extends ProjectReservationServiceDB {

	private static final long serialVersionUID = 1L;

	private boolean showServiceDetail;

	@Transient
	public boolean isShowServiceDetail() {
		return showServiceDetail;
	}
	public void setShowServiceDetail(boolean value) {
		this.showServiceDetail = value;
	}

}
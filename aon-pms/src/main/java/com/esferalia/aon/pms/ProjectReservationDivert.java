package com.esferalia.aon.pms;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ProjectReservationDivertDB;
import com.esferalia.aon.pms.enumeration.ReservationDivertStatus;

@Entity
@Table(name="project_reservation_divert")
public class ProjectReservationDivert extends ProjectReservationDivertDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Transient
	public boolean isPending(){
		return this.getStatus()==ReservationDivertStatus.PENDING;
	}
	
	@Override
	public void setProjectReservation(ProjectReservation projectReservation) {
		super.setProjectReservation(projectReservation);
		if(projectReservation.getStartDate()!=null && projectReservation.getStartDate().after(new Date())){
			this.setDivertDate(projectReservation.getStartDate());
		}
	}

}
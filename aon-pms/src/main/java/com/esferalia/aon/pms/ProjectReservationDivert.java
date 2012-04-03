package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.esferalia.aon.entity.master.ProjectReservationDivertDB;
import com.esferalia.aon.pms.enumeration.ReservationDivertStatus;

@Entity
@Table(name="project_reservation_divert")
public class ProjectReservationDivert extends ProjectReservationDivertDB {

	private static final long serialVersionUID = 1L;
	
	@Transient
	public boolean isPending(){
		return this.getStatus()==ReservationDivertStatus.PENDING;
	}

}
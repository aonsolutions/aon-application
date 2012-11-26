package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.audit.IAuditable;
import com.esferalia.aon.entity.master.ReservationRequestRoomDB;

@Entity
@Table(name="reservation_request_room")
public class ReservationRequestRoom extends ReservationRequestRoomDB implements IAuditable {

	private static final long serialVersionUID = 1L;

}
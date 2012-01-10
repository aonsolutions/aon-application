package com.esferalia.aon.pms.reservation.service;

import javax.jws.WebService;

import com.esferalia.aon.pms.PmsSoap;
import com.esferalia.aon.pms.reservation.ReservationManager;

@WebService(endpointInterface="com.esferalia.aon.pms.PmsSoap", wsdlLocation="/WEB-INF/wsdl/AonPmsSoapService.wsdl", targetNamespace = "http://com.tradyso.pms.partner.ws_Pms/")
public class AonPmsSoap implements PmsSoap {

	@Override
	public String getReservation(String reservation) {
		ReservationManager reservationManager = new ReservationManager();
		return reservationManager.processReservation(reservation);
	}

}

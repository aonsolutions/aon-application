package com.esferalia.aon.pms.reservation.service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.jws.WebService;

import com.esferalia.aon.pms.PmsSoap;

@WebService(endpointInterface="com.esferalia.aon.pms.PmsSoap", wsdlLocation="/WEB-INF/wsdl/AonPmsSoapService.wsdl", targetNamespace = "http://com.tradyso.pms.partner.ws_Pms/")
public class AonPmsSoap implements PmsSoap {

	@Override
	public String getReservation(String reservation) {
		File file = new File("/tmp/reservation_" + new Date().getTime() + ".xml");
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(file);
			writer.write(reservation);
			writer.flush();
		} catch (IOException ex) {
		} finally {
			writer.close();
		}
		return null;
	}

}

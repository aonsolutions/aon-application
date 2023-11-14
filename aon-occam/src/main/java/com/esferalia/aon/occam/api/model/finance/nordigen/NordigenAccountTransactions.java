package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class NordigenAccountTransactions implements Serializable {
	
	private static final long serialVersionUID = -3879480833728917316L;
	
	private List<NordigenAccountTransaction> pending;
	private List<NordigenAccountTransaction> booked;
	
	public List<NordigenAccountTransaction> getPending() {
		return pending;
	}
	public NordigenAccountTransactions setPending(List<NordigenAccountTransaction> pending) {
		this.pending = pending;
		return this;
	}
	
	public List<NordigenAccountTransaction> getBooked() {
		return booked;
	}
	public NordigenAccountTransactions setBooked(List<NordigenAccountTransaction> booked) {
		this.booked = booked;
		return this;
	}
	
	
}

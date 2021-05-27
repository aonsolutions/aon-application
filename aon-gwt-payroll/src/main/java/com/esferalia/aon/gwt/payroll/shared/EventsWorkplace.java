package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.payroll.client.Quintet;

public class EventsWorkplace implements Serializable{

	private List<Quintet<Integer, String, Date, Date, String>> updateEventsWorkplace;

	public EventsWorkplace() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EventsWorkplace(List<Quintet<Integer, String, Date, Date, String>> updateEventsWorkplace) {
		super();
		this.updateEventsWorkplace = updateEventsWorkplace;
	}

	public void setUpdateEventsWorkplace(List<Quintet<Integer, String, Date, Date, String>> updateEventsWorkplace) {
		this.updateEventsWorkplace = updateEventsWorkplace;
	}

	public List<Quintet<Integer, String, Date, Date, String>> getUpdateEventsWorkplace() {
		return updateEventsWorkplace;
	}

}

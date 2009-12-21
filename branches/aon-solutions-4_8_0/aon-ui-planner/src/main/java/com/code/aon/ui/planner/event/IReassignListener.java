package com.code.aon.ui.planner.event;

import java.util.Date;

import net.fortuna.ical4j.model.ComponentList;

public interface IReassignListener {
	
	void update( ComponentList events, Date date );

}

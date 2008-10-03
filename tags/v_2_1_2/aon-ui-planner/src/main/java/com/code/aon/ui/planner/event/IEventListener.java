package com.code.aon.ui.planner.event;

import com.code.aon.planner.IEvent;

public interface IEventListener {
	
	void add( IEvent event );

	void update( IEvent event );

	boolean remove( IEvent event ); 

}

package com.esferalia.aon.occam.api;

import com.esferalia.aon.occam.api.model.Alarm;
import com.esferalia.aon.occam.api.model.office.Notice;

public interface IGroupware {
		
	Notice getNotice(AONContext ctx, Integer id);
	
	Integer insertNotice(AONContext ctx, Notice notice);
	
	Alarm getAlarm(AONContext ctx, Integer id);
	
	Integer insertAlarm(AONContext ctx, Alarm alarm);
	
}

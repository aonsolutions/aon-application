/**
 * 
 */
package com.code.aon.planner;

import com.code.aon.calendar.AonCalendar;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 12-jun-2006
 * @since 1.0
 *
 */

public interface IOwner {

	Integer getId();

	AonCalendar getCalendar();
//	RegistryAttachment getCalendar();
}

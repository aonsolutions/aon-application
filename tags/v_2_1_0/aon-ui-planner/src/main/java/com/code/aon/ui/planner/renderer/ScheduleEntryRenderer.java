package com.code.aon.ui.planner.renderer;

import java.util.HashMap;
import java.util.Random;

import javax.faces.context.FacesContext;

import org.apache.myfaces.custom.schedule.HtmlSchedule;
import org.apache.myfaces.custom.schedule.model.ScheduleEntry;
import org.apache.myfaces.custom.schedule.renderer.DefaultScheduleEntryRenderer;

import com.code.aon.calendar.enumeration.EventCategory;
import com.code.aon.planner.IEvent;

/**
 * An example ScheduleEntryRenderer that assigns a random color to each
 * entry.
 * 
 * @author Jurgen Lust (latest modification by $Author$)
 * @version $Revision$
 */
public class ScheduleEntryRenderer extends DefaultScheduleEntryRenderer {

	private String[] categories;
	private HashMap colors = new HashMap();

	public ScheduleEntryRenderer() {
		categories = new String[ EventCategory.values().length ];
		for (int i = 0; i < categories.length; i++) {
			StringBuffer color = new StringBuffer();
			Random random = new Random();
	        color.append("rgb(");
	        color.append(random.nextInt(255));
	        color.append(",");
	        color.append(random.nextInt(255));
	        color.append(",");
	        color.append(random.nextInt(255));
	        color.append(")");
			categories[i] = color.toString();
		}
	}

	@SuppressWarnings("unchecked")
	public String getColor(FacesContext context, 
							HtmlSchedule schedule, 
							ScheduleEntry entry, 
							boolean selected) {
		if ( colors.containsKey( entry.getId() ) ) 
			return (String)colors.get( entry.getId() );

        String colorString = categories[ ( (IEvent)entry ).getCategory().ordinal() ];
        colors.put( entry.getId(), colorString );
        return colorString + ";background-color:" + colorString;
    }

}

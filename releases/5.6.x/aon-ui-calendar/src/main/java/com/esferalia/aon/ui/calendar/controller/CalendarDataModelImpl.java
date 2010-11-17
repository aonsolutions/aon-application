package com.esferalia.aon.ui.calendar.controller;

import java.util.Date;

import org.richfaces.model.CalendarDataModel;
import org.richfaces.model.CalendarDataModelItem;
 

 
public class CalendarDataModelImpl implements CalendarDataModel {
 
	/* (non-Javadoc)
		 * @see org.richfaces.component.CalendarDataModel#getData(java.util.Date[])
		 */
		public CalendarDataModelItem[] getData(Date[] dateArray) {
			if (dateArray == null) {
				return null;
			}
			
			CalendarDataModelItemImpl[] items = new CalendarDataModelItemImpl[dateArray.length];
			for (int i = 0; i < dateArray.length; i++) {
				items[i] = createDataModelItem(dateArray[i]);
				items[i].setToolTip("dfsadafds");
			}
	
			return items;
		}
	
		protected CalendarDataModelItemImpl createDataModelItem(Date date) {
			CalendarDataModelItemImpl item = new CalendarDataModelItemImpl();
			item.setDate(date);
			/*Map data = new HashMap();
			DateFormat enFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH);
			DateFormat frFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.FRENCH);
			DateFormat deFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);
			data.put("enLabel", enFormatter.format(date));
			data.put("frLabel", frFormatter.format(date));
			data.put("deLabel", deFormatter.format(date));*/
			//item.setData(data);
			
			return item;
		}
	
		/* (non-Javadoc)
		 * @see org.richfaces.component.CalendarDataModel#getToolTip(java.util.Date)
		 */
		public Object getToolTip(Date date) {
			// TODO Auto-generated method stub
			return null;
		}

}

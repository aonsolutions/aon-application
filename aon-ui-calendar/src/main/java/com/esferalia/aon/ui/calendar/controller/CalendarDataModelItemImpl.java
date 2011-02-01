package com.esferalia.aon.ui.calendar.controller;

import java.util.Date;

import org.richfaces.model.CalendarDataModelItem;

public class CalendarDataModelItemImpl implements CalendarDataModelItem {
	 
		private Object data;
		private Date date;
		private String styleClass;
		private Object toolTip;
		private boolean enabled = true;
		
		/* (non-Javadoc)
		 * @see org.richfaces.component.CalendarDataModelItem#getData()
		 */
		public Object getData() {
			return data;
		}
	
		/* (non-Javadoc)
		 * @see org.richfaces.component.CalendarDataModelItem#getDate()
		 */
		public Date getDate() {
			return date;
		}
	
		/* (non-Javadoc)
		 * @see org.richfaces.component.CalendarDataModelItem#getStyleClass()
		 */
		public String getStyleClass() {
			return styleClass;
		}
	
		/* (non-Javadoc)
		 * @see org.richfaces.component.CalendarDataModelItem#getToolTip()
		 */
		public Object getToolTip() {
			return toolTip;
		}
	
		/* (non-Javadoc)
		 * @see org.richfaces.component.CalendarDataModelItem#hasToolTip()
		 */
		public boolean hasToolTip() {
			return getToolTip() != null;
		}
	
		/* (non-Javadoc)
		 * @see org.richfaces.component.CalendarDataModelItem#isEnabled()
		 */
		public boolean isEnabled() {
			return enabled;
		}
	
		/**
		 * @param data the data to set
		 */
		public void setData(Object data) {
			this.data = data;
		}
	
		/**
		 * @param date the date to set
		 */
		public void setDate(Date date) {
			this.date = date;
		}
	
		/**
		 * @param styleClass the styleClass to set
		 */
		public void setStyleClass(String styleClass) {
			this.styleClass = styleClass;
		}
	
		/**
		 * @param toolTip the toolTip to set
		 */
		public void setToolTip(Object toolTip) {
			this.toolTip = toolTip;
		}
	
		/**
		 * @param enabled the enabled to set
		 */
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		@Override
		public int getDay() {
			// TODO Auto-generated method stub
			return 0;
		}
	
	}



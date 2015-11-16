package com.esferalia.aon.gwt.fiscal.client.stats;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

public class YearClickEvent extends GwtEvent<YearClickHandler> {
	
	private static Type<YearClickHandler> TYPE = new Type<YearClickHandler>();

	private Integer year;

	public YearClickEvent(HasHandlers source,Integer year) {
		super();
		setSource(source);
		this.year = year;
	}

	public Integer getYear() {
		return year;
	}

	public static Type<YearClickHandler> getType() {
		return TYPE;
	}
	
	@Override
	public Type<YearClickHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(YearClickHandler handler) {
		handler.onYearClick(this);
	}

	public static <T> void fire(HasHandlers source,Integer year) {
		source.fireEvent(new YearClickEvent(source,year));
	}
}

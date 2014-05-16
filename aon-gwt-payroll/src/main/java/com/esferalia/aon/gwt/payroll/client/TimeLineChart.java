/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
//package com.google.gwt.visualization.client.visualizations;
package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.ajaxloader.client.ArrayHelper;
import com.google.gwt.ajaxloader.client.Properties;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasContextMenuHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.HasScrollHandlers;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDrawOptions;
import com.google.gwt.visualization.client.Selectable;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.events.Handler;
import com.google.gwt.visualization.client.events.OnMouseOutHandler;
import com.google.gwt.visualization.client.events.OnMouseOverHandler;
import com.google.gwt.visualization.client.events.ReadyHandler;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.google.gwt.visualization.client.events.StateChangeHandler;
import com.google.gwt.visualization.client.visualizations.Visualization;

/**
 * TimeLine Chart visualization. Note that this chart does not work when loading
 * the HTML from a local file. It works only when loading the HTML from a web
 * server.
 * 
 * @see <a
 *      href="http://code.google.com/apis/visualization/documentation/gallery/TimeLinechart.html"
 *      > TimeLine Chart Visualization Reference</a>
 */
public class TimeLineChart extends Visualization<TimeLineChart.Options>
		implements HasScrollHandlers, HasMouseOverHandlers, HasClickHandlers, HasContextMenuHandlers, Selectable {
	/**
	 * Options for drawing the chart.
	 */

	private Element scrollable;

	public static class Options extends AbstractDrawOptions {
		public static Options create() {
			return JavaScriptObject.createObject().cast();
		}

		public static class BarLabelStyle extends Properties {
			public static BarLabelStyle create() {
				return JavaScriptObject.createObject().cast();
			}

			protected BarLabelStyle() {

			}

			public final native void setColor(String color) /*-{
				this.color = color;
			}-*/;

			public final native void setFontName(String fontName) /*-{
				this.fontName = fontName;
			}-*/;

			public final native void setFontSize(String fontSize) /*-{
				this.fontSize = fontSize;
			}-*/;
		}

		public static class RowLabelStyle extends Properties {
			public static RowLabelStyle create() {
				return JavaScriptObject.createObject().cast();
			}

			protected RowLabelStyle() {

			}

			public final native void setTextAlign(String align) /*-{
				this.align = align;
			}-*/;

			public final native void setColor(String color) /*-{
				this.color = color;
			}-*/;

			public final native void setFontName(String fontName) /*-{
				this.fontName = fontName;
			}-*/;

			public final native void setFontSize(String fontSize) /*-{
				this.fontSize = fontSize;
			}-*/;
		}

		public static class Timeline extends Properties {
			public static Timeline create() {
				return JavaScriptObject.createObject().cast();
			}

			protected Timeline() {

			}

			public final native void setBarLabelStyle(Object labelStyle) /*-{
				this.barLabelStyle = labelStyle;
			}-*/;

			public final native void setColorByRowLabel(boolean colorByRowLabel) /*-{
				this.colorByRowLabel = colorByRowLabel;
			}-*/;

			public final native void setGroupByRowLabel(boolean groupByRowLabel) /*-{
				this.groupByRowLabel = groupByRowLabel;
			}-*/;

			public final native void setRowLabelStyle(Object rowLabelStyle) /*-{
				this.rowLabelStyle = rowLabelStyle;
			}-*/;

			public final native void setShowRowLabels(boolean showRowLabels) /*-{
				this.showRowLabels = showRowLabels;
			}-*/;

			public final native void setShowBarLabels(boolean showBarLabels) /*-{
				this.showBarLabels = showBarLabels;
			}-*/;

			public final native void setSingleColor(String singleColor) /*-{
				this.singleColor = singleColor;
			}-*/;
		}

		protected Options() {
		}

		public final native void setTimeline(Timeline timeline) /*-{
			this.timeline = timeline;
		}-*/;

		public final native void setAvoidOverlappingGridLines(boolean avoidOver) /*-{
			this.avoidOverlappingGridLines = avoidOver;
		}-*/;

		public final native void setBackgroundColor(String color) /*-{
			this.backgroundColor = color;
		}-*/;

		public final native void setColors(JsArrayString col0rs) /*-{
			this.colors = col0rs;
		}-*/;

		public final void setColors(String... colors) {
			setColors(ArrayHelper.toJsArrayString(colors));
		}

		public final native void setEnableInteractivity(boolean enable) /*-{
			this.enableInteractivity = enable;
		}-*/;

		public final native void setForceIFrame(boolean force) /*-{
			this.forceIFrame = force;
		}-*/;

		public final native void setHeight(int height) /*-{
			this.height = height;
		}-*/;

		public final native void setWidth(int width) /*-{
			this.width = width;
		}-*/;

		public final native void setTitle(String title) /*-{
			this.title = title;
		}-*/;
	}

	public static final String PACKAGE = "timeline";

	public TimeLineChart() {
		super();
	}

	public TimeLineChart(AbstractDataTable data, Options options) {
		super(data, options);
	}

	public final void addOnMouseOutHandler(OnMouseOutHandler handler) {
		Handler.addHandler(this, "onmouseout", handler);
	}

	public final void addOnMouseOverHandler(OnMouseOverHandler handler) {
		Handler.addHandler(this, "onmouseover", handler);
	}

	public final void addReadyHandler(ReadyHandler handler) {
		Handler.addHandler(this, "ready", handler);
	}

	public final void addStateChangeHandler(StateChangeHandler handler) {
		Handler.addHandler(this, "statechange", handler);
	}

	public void addSelectHandler(SelectHandler handler) {
		Selection.addSelectHandler(this, handler);
	}

	public void setSelections(JsArray<Selection> sel) {
		Selection.setSelections(this, sel);
	}

	public final JsArray<Selection> getSelections() {
		return Selection.getSelections(this);
	}

	/**
	 * Returns the current state of the {@link TimeLineChart}, serialized to a
	 * JSON string. To assign this state to the chart, assign this string to the
	 * state option in the draw() method. This is often used to specify a custom
	 * chart state on startup, instead of using the default state.
	 * 
	 * @return a JSON encoded string indicating the state of the UI. This method
	 *         may return <code>null</code> if the state was not supplied by
	 *         {@link TimeLineChart.Options#setState(String)} or a statechange
	 *         event has not yet fired.
	 */
	public final native String getState() /*-{
		var jso = this.@com.google.gwt.visualization.client.visualizations.Visualization::getJso()();

		// The getState() method doesn't seem to always be present. I think this
		// happens when you don't properly initialize it or when you try to query
		// it before a statechanged event fires.  
		if (jso.getState) {
			return jso.getState();
		}
		return null;
	}-*/;

	// ------------------------------------------------------ HasScrollHandlers
	@Override
	public HandlerRegistration addScrollHandler(ScrollHandler handler) {
		return addHandler(handler, ScrollEvent.getType());
	}

	// --------------------------------------------------- HasMouseOverHandlers
	public HandlerRegistration addMouseOverHandler(
			com.google.gwt.event.dom.client.MouseOverHandler handler) {
		return addHandler(handler, MouseOverEvent.getType());
	};
	
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addHandler(handler, ClickEvent.getType());
	}
	
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return addHandler(handler, MouseMoveEvent.getType());
	}
	
	@Override
	public HandlerRegistration addContextMenuHandler(ContextMenuHandler handler) {
		return addHandler(handler, ContextMenuEvent.getType());
	}

	@Override
	protected native JavaScriptObject createJso(Element parent) /*-{
		return new $wnd.google.visualization.Timeline(parent);

	}-*/;

	@Override
	protected void onLoad() {
		super.onLoad();
		initScrollHandler();
		initEventHandlers();
	}

	private void initScrollHandler() {

		Element el = getElement();
		while (DivElement.is(el)) {
			el = el.getFirstChildElement();
		}

		if (el.getNextSibling() != null) {
			el = el.getNextSiblingElement();
			while (DivElement.is(el)) {
				el = el.getFirstChildElement();
			}
		}

		for (; el != getElement(); el = el.getParentElement()) {
			Event.sinkEvents(el, Event.ONSCROLL);
		}

		DOM.setEventListener(getElement(), new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				ScrollEvent.fireNativeEvent(event, TimeLineChart.this);
			}
		});
	}

	private void initEventHandlers() {

		
		Element el = getElement();
		for ( int i = 0 ; i < el.getChildCount(); i++)
			sinkEvents(Element.as(el.getChild(i)), Event.ONCLICK | Event.ONMOUSEOVER
					| Event.ONMOUSEMOVE |Event.ONCONTEXTMENU);
		

		DOM.setEventListener(el, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				DomEvent.fireNativeEvent(event, TimeLineChart.this);
			}
		});
	}
	

	private void sinkEvents(Element el, int eventBits){
		
		Event.sinkEvents(el, eventBits);
		
		for ( int i = 0 ; i < el.getChildCount(); i++)
			sinkEvents(Element.as(el.getChild(i)), eventBits);
		
	}
	
	public int getVerticalScrollPosition(Element el) {
		return el.getScrollTop();
	}

	public int getMaximumVerticalScrollPosition(Element el) {
		return el.getScrollHeight() - el.getClientHeight();
	}
}

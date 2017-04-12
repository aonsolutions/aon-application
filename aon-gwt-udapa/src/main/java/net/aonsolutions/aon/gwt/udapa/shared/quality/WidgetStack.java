package net.aonsolutions.aon.gwt.udapa.shared.quality;

import com.google.gwt.user.client.ui.Widget;

public class WidgetStack {

	
	
	Widget widget;
	String value;
	String prevValue;
	WidgetType widgetType;
	QualitySheetCode code;
	
	public WidgetStack(Widget widget, QualitySheetCode code) {
		this.widget = widget;
		this.code = code;
	}
	
	public WidgetStack() {

	}
	
	public Integer getIntValue() {
		return Integer.parseInt(value);
	}
	
	public Double getDoubleValue() {
		return Double.parseDouble(value);
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	
	public void setValue(Integer value){
		this.value = value.toString();
	}
	
	public void setValue(Double value){
		this.value = value.toString();
	}

	public Integer getIntPrevValue() {
		return Integer.parseInt(prevValue);
	}
	
	public Double getDoublePrevValue() {
		return Double.parseDouble(prevValue);
	}
	
	public String getPrevValue() {
		return prevValue;
	}
	
	public void setPrevValue(String prevValue){
		this.prevValue = prevValue;
	}
	
	public void setPrevValue(Integer prevValue){
		this.prevValue = prevValue.toString();
	}
	
	public void setPrevValue(Double prevValue){
		this.prevValue = prevValue.toString();
	}
	
	public Widget getWidget() {
		return widget;
	}

	public void setWidget(Widget widget) {
		this.widget = widget;
	}

	public WidgetType getWidgetType() {
		return widgetType;
	}

	public void setWidgetType(WidgetType widgetType) {
		this.widgetType = widgetType;
	}

	public QualitySheetCode getCode() {
		return code;
	}

	public void setCode(QualitySheetCode code) {
		this.code = code;
	}
	
}

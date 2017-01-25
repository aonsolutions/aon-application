package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperItem;

public abstract class AonFilterDialog extends PopupPanel {

	HashMap<String, Boolean> selectedMap;
	
	public AonFilterDialog(Widget w, String label, String icon,
		LinkedList<String> filterList, AonJsArray<JsObject> data) {
		selectedMap = new HashMap<>();
		
		filterList.stream().forEach(f -> selectedMap.put(f, true));
		
		VerticalPanel verticalPanel = new VerticalPanel();
		PaperInput textBox = new PaperInput();
		textBox.setLabel(label);

		textBox.addDomHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				verticalPanel.remove(1);
				verticalPanel.add(buildItems(data, textBox.getValue(), icon));
			}
		}, KeyUpEvent.getType());
		


		verticalPanel.add(textBox);
		verticalPanel.add(buildItems(data, textBox.getValue(), icon));
			
		add(verticalPanel);
		
		int left = w.getAbsoluteLeft();
		int top = w.getAbsoluteTop() + w.getOffsetHeight();
		Integer width = Window.getClientWidth();
		if(left > width - 200){
			left = left - 200;
		}
		
		setPopupPosition(left, top);
		setAutoHideEnabled(true);
	}

	protected abstract void onSelect(JsObject js, Boolean apply);

	
	private ScrollPanel buildItems(AonJsArray<JsObject> data, String filter, String icon){
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(AON.AON_CSS.aonStatMenuStyle());
			
		VerticalPanel vp = new VerticalPanel();
		vp.addStyleName(AON.AON_CSS.aonWidthAll());
		vp.addStyleName(AON.AON_CSS.aonCursorPointer());
		
		data.stream().filter(js -> js.getName().toLowerCase().contains(filter.toLowerCase()))
				.forEach(js -> vp.add(buildItem(js, icon)));
		scrollPanel.add(vp);
		return scrollPanel;
	}
	
	private SimplePanel buildItem(JsObject js, String icon) {
		Boolean filtered = selectedMap.containsKey(js.getId()+"") &&
					selectedMap.get(js.getId()+"");
		PaperItem pi = new PaperItem();
    	IronIcon ironIcon = new IronIcon();
    	if(filtered){
    		ironIcon.setIcon("check");
    	}else ironIcon.setIcon(icon);
    	pi.add(ironIcon);
    	pi.add(new Label(js.getName()));
    	pi.setStyle("min-height:36px;font-size:16px;padding:0px;");
    	
    	SimplePanel sp = new SimplePanel();
    	sp.add(pi);
    	sp.addDomHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				sp.getElement().getStyle().setBackgroundColor("#bbb");
			}
		}, MouseOverEvent.getType());
    	
    	sp.addDomHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				sp.getElement().getStyle().setBackgroundColor("#fff");
			}
		}, MouseOutEvent.getType());
    	
    	sp.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hide();
				onSelect(js, !filtered);
			}
		}, ClickEvent.getType());
    	
    	return sp;
	}
	
}

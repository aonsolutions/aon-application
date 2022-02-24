package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.List;

import com.esferalia.aon.gwt.common.shared.AonMenuItem;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AonMenu extends HTMLPanel{
	
	public AonMenu() {
		super("");
		this.getElement().getStyle().setProperty("border-right", "1px solid #c4c4c4");
	}

	public void addItem(AonMenuItem item) {
		add(buildItem(item));
	}
	
	public void addItems(List<AonMenuItem> items) {
		items.stream().forEach(r -> addItem(r));
	}
	
	private VerticalPanel buildItem(AonMenuItem item) {
		VerticalPanel vp = new VerticalPanel();
		FlowPanel chapter = new FlowPanel();
		chapter.getElement().getStyle().setMarginTop(10, Unit.PX);
		chapter.getElement().getStyle().setDisplay(Display.FLEX);
		if(!item.isDisabled()) chapter.getElement().getStyle().setCursor(Cursor.POINTER);
		else chapter.getElement().getStyle().setOpacity(0.5);
		AonIcon icon = new AonIcon("arrow_right");
		if(!item.getItems().isEmpty()) {
			chapter.add(icon);
		}
		
		Label label = new Label(item.getTitle());
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		label.getElement().getStyle().setPaddingTop(5, Unit.PX);
		if(item.getItems().isEmpty()) {
			label.getElement().getStyle().setPaddingLeft(24, Unit.PX);
		}
		chapter.add(label);
		vp.add(chapter);
		
		VerticalPanel vp1 = new VerticalPanel();
		vp1.getElement().getStyle().setPaddingLeft(35, Unit.PX);
		vp1.setVisible(false);
		item.getItems().stream().forEach(r -> vp1.add(buildItemChild(r)));

		vp.add(vp1);
		
		if(!item.isDisabled()) {
			ClickHandler handler = new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if(!item.getItems().isEmpty()) {
						vp1.setVisible(!vp1.isVisible());
						icon.setIcon(vp1.isVisible() ? "arrow_drop_down" : "arrow_right");
					} 
				}
			};
			if(item.hasHandler()) {
				icon.addDomHandler(handler, ClickEvent.getType());
				label.addDomHandler(item.getHandler(), ClickEvent.getType());
			} else {
				chapter.addDomHandler(handler, ClickEvent.getType());
			}
		}

		return vp;
	}
	
	private FlowPanel buildItemChild(AonMenuItem subItem) {
		FlowPanel chapter = new FlowPanel();
		chapter.getElement().getStyle().setMarginTop(10, Unit.PX);
		chapter.add(new Label(subItem.getTitle()));
		if(subItem.hasHandler()) {
			chapter.getElement().getStyle().setCursor(Cursor.POINTER);
			chapter.addDomHandler(subItem.getHandler(), ClickEvent.getType());
		}
		return chapter;
	}

}

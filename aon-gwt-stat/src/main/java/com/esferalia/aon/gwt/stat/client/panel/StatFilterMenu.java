package com.esferalia.aon.gwt.stat.client.panel;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class StatFilterMenu extends PopupPanel implements HasSelectionHandlers<StatFilterItem>{
		private FlowPanel container;
		private int itemsSelected;
		
		public StatFilterMenu() {
			super();
		
			ScrollPanel scroll = new ScrollPanel();			
			container = new FlowPanel();
			scroll.add(container);
			add(scroll);
			setStyleName( AON.AON_CSS.aonStatPopUpPanel());
			setAutoHideEnabled(true);
			
			scroll.addStyleName(AON.AON_CSS.aonPadding2Left());
			scroll.addStyleName(AON.AON_CSS.aonPadding2Top());
			scroll.addStyleName(AON.AON_CSS.aonPadding2Bottom());
			scroll.addStyleName(AON.AON_CSS.aonStatMenuStyle());

		}

		public void addItem(final StatFilterItem item) {
			final FocusPanel itemPanel = new FocusPanel();
			itemPanel.setStyleName(AON.AON_CSS.aonStatItemPanel());
			if(item.isSelected()){
				itemPanel.addStyleName(AON.AON_CSS.aonStatCheckyes());
			}else{
				itemPanel.addStyleName(AON.AON_CSS.aonListStat());	
			}
			String description = item.getLabel();
			description = AonStringUtils.abbreviate(description, 35);
			Label label = new Label(description);
			label.setTitle(item.getLabel());
			itemPanel.setWidget(label);
			itemPanel.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					item.setSelected( !item.isSelected() );
					if(item.isSelected()){
						itemsSelected = itemsSelected + 1;
						itemPanel.addStyleName(AON.AON_CSS.aonStatCheckyes());
						itemPanel.removeStyleName(AON.AON_CSS.aonListStat());
					}else{
						itemsSelected = itemsSelected - 1;
						itemPanel.removeStyleName(AON.AON_CSS.aonStatCheckyes());
						itemPanel.addStyleName(AON.AON_CSS.aonListStat());
					}
					SelectionEvent.<StatFilterItem>fire(StatFilterMenu.this, item);
				}
			});
			container.add(itemPanel);
			
		}
		
		@Override
	    public HandlerRegistration addSelectionHandler(SelectionHandler<StatFilterItem> handler) {
			return super.addHandler(handler, SelectionEvent.getType());
	    }       

		public int getItemsSelected() {
			return itemsSelected;
		}

	}

package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonCards extends ScrollPanel {

	private FlowPanel container;
	
	public static class AonCard extends FocusPanel {
		
		private FlexTable cardTab = new FlexTable();
		private FlowPanel menuPanel = new FlowPanel();
		
		public AonCard() {
			setStyleName(AON.CSS.aonCard());
			
			
			cardTab.getColumnFormatter().setWidth(0, "auto");
			cardTab.getColumnFormatter().setWidth(1, "25px");
			cardTab.setWidth("100%");
			cardTab.setHeight("100%");
			
			cardTab.getCellFormatter().setStyleName(0, 0, AON.CSS.aonCardTitle());
			
			menuPanel.setStyleName(AON.CSS.aonCardFooter());
			cardTab.setWidget(0, 1, menuPanel);
			
			cardTab.getCellFormatter().setStyleName(0, 1, AON.CSS.aonBorderLeft());
			cardTab.getCellFormatter().addStyleName(0, 1, AON.CSS.aonMarginTop());
			cardTab.getCellFormatter().addStyleName(0, 1, AON.CSS.aonVerticalAlignTop());
			cardTab.getFlexCellFormatter().setRowSpan(0, 1, 2);
			setWidget(cardTab);
		}
		
		public AonCard(Widget title, Widget body ) {
			this();
			setTitle( title );
			setBody( body );
		}
		
		protected void setBody(Widget body) {
			FlowPanel bodyPanel = new FlowPanel();
			bodyPanel.setStyleName(AON.CSS.aonCardBody());
			bodyPanel.add(body);
			cardTab.setWidget(1, 0, bodyPanel);
		}

		protected void setTitle(Widget title) {
			cardTab.setWidget(0, 0, title);
		}

		protected FlowPanel getMenuPanel() {
			return menuPanel;
		}
	}
	
	public AonCards() {
		super();
		attachContainer();
	}
	
	private void attachContainer() {
		container = new FlowPanel();
		container.setStyleName(AON.CSS.aonCards());
		setWidget(container);
	}
	
	@Override
	public void clear() {
		super.clear();
		attachContainer();
	}
	
	
	public AonCard addCard(Widget title, Widget body ) {
		AonCard card = new AonCard(title, body);
		return addCard(card);
	}
	
	public AonCard addCard(AonCard card) {
		container.add( card  );
		return card;
	}
	
}

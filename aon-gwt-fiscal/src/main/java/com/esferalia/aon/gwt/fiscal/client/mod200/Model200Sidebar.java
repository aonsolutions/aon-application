package com.esferalia.aon.gwt.fiscal.client.mod200;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Model200Sidebar extends ResizeComposite {

	interface Model200SidebarBinder extends UiBinder<Widget, Model200Sidebar> {
	}

	private static final Model200SidebarBinder BINDER = GWT
			.create(Model200SidebarBinder.class);
	private final static AonResources RESOURCES = GWT.create(AonResources.class);

	@UiField
	FocusPanel linkPage00;
	@UiField
	FocusPanel linkPage01;
	@UiField
	FocusPanel linkPage02;
	@UiField
	FocusPanel linkPage03;
	@UiField
	FocusPanel linkPage04;
	@UiField
	FocusPanel linkPage05;
	@UiField
	FocusPanel linkPage06;
	@UiField
	FocusPanel linkPage07;
	@UiField
	FocusPanel linkPage08;
	@UiField
	FocusPanel linkPage09;
	@UiField
	FocusPanel linkPage10;
	@UiField
	FocusPanel linkPage11;
	@UiField
	FocusPanel linkPage12;
	@UiField
	FocusPanel linkPage13;

	public Model200Sidebar() {
		Widget ui = BINDER.createAndBindUi(this);
		initWidget(ui);
	}
	
	protected void clearLinks() {
		Panel[] linkPanels = new Panel[] {
				 linkPage00,linkPage01,linkPage02,linkPage03
				,linkPage04,linkPage05,linkPage06,linkPage07
				,linkPage08,linkPage09,linkPage10,linkPage11
				,linkPage12,linkPage13
					};	
		for (Panel p : linkPanels) {
			p.setStyleName(RESOURCES.css().aonLinkItem());
		}
		
	}
	
	public void addListener(final FocusPanel link, final DeckPanel deckPanel, final PageAbs page ) {
		link.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				clearLinks();
				link.setStyleName(RESOURCES.css().aonLinkItemSelected());
				deckPanel.showWidget(deckPanel.getWidgetIndex(page));
			}
		});
	}

	public void setVisibleLinks(boolean visible) {
		Panel[] linkPanels = new Panel[] {
				 linkPage00,linkPage01,linkPage02,linkPage03
				,linkPage04,linkPage05,linkPage06,linkPage07
				,linkPage08,linkPage09,linkPage10,linkPage11
				,linkPage12,linkPage13
					};	
		for (Panel p : linkPanels) {
			p.setVisible(visible);
		}
	}

}

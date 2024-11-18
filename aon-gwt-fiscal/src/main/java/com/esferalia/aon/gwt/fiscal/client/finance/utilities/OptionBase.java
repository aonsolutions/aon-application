package com.esferalia.aon.gwt.fiscal.client.finance.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTabLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.fiscal.client.finance.utilities.FinanceUtilitiesModulePanel.IOption;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

abstract class OptionBase extends SimpleLayoutPanel implements IOption {

	private FinanceUtilitiesModuleOptions options;
	private Domain domain;
	
	private static final int NOTIFICATIONS_TAB = 0;
	private static final int RESULTS_TAB = 1;
	
	private AonLayoutPanel mainPanel;
	private SplitLayoutPanel splitLayoutPanel;
	private AonMinimizePanel footPanel;
	private AonTabLayoutPanel tabLayout; 
	private SimpleLayoutPanel notificationsContent;
	private SimpleLayoutPanel resultContent;
	
	OptionBase(FinanceUtilitiesModuleOptions options, Domain domain) {
		this.options = options;
		this.domain = domain;
		
		mainPanel = new AonLayoutPanel(Unit.PX);
		mainPanel.setStyleName(AON.AON_CSS.aonSelector());
		
		mainPanel.addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		
		splitLayoutPanel = new SplitLayoutPanel();
		footPanel = new AonMinimizePanel();
		footPanel.addMinimizeHandler(event -> closeFootPanel());
		footPanel.addMaximizeHandler(event -> openFootPanel());
		footPanel.setStyleName(AON.AON_CSS.aonSelector());
		tabLayout = new AonTabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		
		notificationsContent = new SimpleLayoutPanel();
		tabLayout.add(notificationsContent, AON.MSG.notifications());
		
		resultContent = new SimpleLayoutPanel();
		tabLayout.add(resultContent, AON.MSG.result());
		
		footPanel.add(tabLayout);
		splitLayoutPanel.addSouth(footPanel, 30);
		mainPanel.add(splitLayoutPanel);
		setWidget(mainPanel);
	}
	
	protected void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 30);
		splitLayoutPanel.animate(500);
	}
	protected void openFootPanel() {
		int size = Window.getClientHeight() / 2;
		splitLayoutPanel.setWidgetSize(footPanel, size);
		splitLayoutPanel.animate(500);
	}
	protected void openFootPanelIfNeeded() {
		if (splitLayoutPanel.getWidgetSize(footPanel) <= 50) {
			openFootPanel();
		}
	}
	
	protected FinanceUtilitiesModuleOptions getOptions() {
		return options;
	}
	protected Domain getDomain() {
		return domain;
	}
	
	protected void showResults(Widget widget) {
		openFootPanelIfNeeded();
		tabLayout.selectTab(RESULTS_TAB);
		resultContent.setWidget(widget);
	}
	protected void showResults(FinanceUtilitiesResult result) {
		ScrollPanel panel = new ScrollPanel();
		panel.add(paintResults(result) );
		showResults(panel);
	}
	protected void cleanErrorPanel() {
		ScrollPanel panel = new ScrollPanel();
		notificationsContent.setWidget(panel);
		closeFootPanel();
	}
	protected void showErrorPanel(String msg) {
		openFootPanelIfNeeded();
		tabLayout.selectTab(NOTIFICATIONS_TAB);
		ScrollPanel panel = new ScrollPanel();
		FlexTable tab = new FlexTable();
		tab.setWidth("95%");
		tab.setStyleName(AON.AON_CSS.aonBlockCenter());
		tab.addStyleName(AON.AON_CSS.aonMarginBottom());
		tab.addStyleName(AON.AON_CSS.aonMarginTop());
		tab.getColumnFormatter().setWidth(0, "20px");
		tab.getColumnFormatter().setWidth(1, "auto");
		
		InlineLabel icon = new InlineLabel("");
		icon.setStyleName(AON.AON_CSS.aonIconPointRed());
		icon.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
		tab.setWidget(0, 0, icon);
		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		
		InlineLabel label = new InlineLabel(msg);
		label.addStyleName(AON.AON_CSS.aonColorRed());
		label.addStyleName(AON.AON_CSS.aonBold());
		tab.setWidget(0, 1, label);
		tab.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonPanelGridEven());
		
		panel.add(tab);
		notificationsContent.setWidget(panel);
	}
	
	protected void showInfoPanel(String msg) {
		openFootPanelIfNeeded();
		tabLayout.selectTab(NOTIFICATIONS_TAB);
		ScrollPanel panel = new ScrollPanel();
		FlexTable tab = new FlexTable();
		tab.setWidth("95%");
		tab.setStyleName(AON.AON_CSS.aonBlockCenter());
		tab.addStyleName(AON.AON_CSS.aonMarginBottom());
		tab.addStyleName(AON.AON_CSS.aonMarginTop());
		tab.getColumnFormatter().setWidth(0, "20px");
		tab.getColumnFormatter().setWidth(1, "auto");
		
		InlineLabel icon = new InlineLabel("");
		icon.setStyleName(AON.AON_CSS.aonIconPointLightGreen());
		icon.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
		tab.setWidget(0, 0, icon);
		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		
		InlineLabel label = new InlineLabel(msg);
		label.addStyleName(AON.AON_CSS.aonBold());
		tab.setWidget(0, 1, label);
		tab.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonPanelGridEven());
		
		panel.add(tab);
		notificationsContent.setWidget(panel);
	}

	protected void setContent(Widget content) {
		splitLayoutPanel.add(content);
	}
	
	@Override
	public Widget getSidebarWidget() {
		Label optLabel = new Label();
		optLabel.setText(getOptionDescription());
		optLabel.setStyleName(AON.AON_CSS.aonClickableBlock());
		optLabel.addStyleName(AON.AON_CSS.aonPadding());
		optLabel.addClickHandler(event -> SelectionEvent.<IOption>fire(OptionBase.this, OptionBase.this));
		return optLabel;
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<IOption> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	protected AonToolbar getToolbarPanel() {
		return new AonToolbar(AonStringUtils.abbreviate(getOptionDescription(),30));
	}

	protected abstract Widget paintResults(FinanceUtilitiesResult result);
	
}


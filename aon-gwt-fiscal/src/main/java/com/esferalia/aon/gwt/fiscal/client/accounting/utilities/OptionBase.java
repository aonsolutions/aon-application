package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeHandler;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeHandler;
import com.esferalia.aon.gwt.fiscal.client.accounting.utilities.AccountingUtilities.IOption;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

abstract class OptionBase extends SimpleLayoutPanel implements IOption {

	interface TabLayoutFolderSafeTemplate extends SafeHtmlTemplates {
		@Template ("<span class=\"gwt-InlineLabel .aon-padding-right aon-padding-left-20 {1}\">{0}</span>")
		SafeHtml tab(String title, String icon);
	}
	private static final TabLayoutFolderSafeTemplate TABLAYOUT_FOLDER_TEMPLATE = GWT.create(TabLayoutFolderSafeTemplate.class);
	
	private String domainName;
	private String user;
	private Domain domain;
	
	private final static int NOTIFICATIONS_TAB = 0;
	private final static int RESULTS_TAB = 1;
	
	private DockLayoutPanel mainPanel;
	private SplitLayoutPanel splitLayoutPanel;
	private MinimizePanel footPanel;
	private TabLayoutPanel tabLayout; 
	private SimpleLayoutPanel notificationsContent;
	private SimpleLayoutPanel resultContent;
	
	public OptionBase(String domainName, String user, Domain domain) {
		this.domainName = domainName;
		this.user = user;
		this.domain = domain;
		
		mainPanel = new DockLayoutPanel(Unit.PX);
		mainPanel.addNorth(getToolbarPanel(), 25);
		
		splitLayoutPanel = new SplitLayoutPanel();
		footPanel = new MinimizePanel();
		footPanel.addMinimizeHandler(new MinimizeHandler() {
			
			@Override
			public void onMinimize(MinimizeEvent event) {
				closeFootPanel();
			}
		});
		footPanel.addMaximizeHandler(new MaximizeHandler() {
			
			@Override
			public void onMaximize(MaximizeEvent event) {
				openFootPanel();
			}
		});
		footPanel.setStyleName(AON.AON_CSS.aonSelector());
		tabLayout = new TabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		
		notificationsContent = new SimpleLayoutPanel();
		tabLayout.add(notificationsContent, TABLAYOUT_FOLDER_TEMPLATE.tab(AON.MSG.notifications(), AON.AON_CSS.aonIconJournalLog()));
		
		resultContent = new SimpleLayoutPanel();
		tabLayout.add(resultContent, TABLAYOUT_FOLDER_TEMPLATE.tab(AON.MSG.result(), AON.AON_CSS.aonIconModel()));
		
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
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 2);
		splitLayoutPanel.animate(500);
	}
	protected void openFootPanelIfNeeded() {
		if (splitLayoutPanel.getWidgetSize(footPanel) <= 50) {
			openFootPanel();
		}
	}
	
	protected String getDomainName() {
		return domainName;
	}
	protected String getUser() {
		return user;
	}
	protected Domain getDomain() {
		return domain;
	}
	protected void showResults(AccUtilitiesResult result) {
		openFootPanelIfNeeded();
		tabLayout.selectTab(RESULTS_TAB);
		ScrollPanel panel = new ScrollPanel();
		panel.add(paintResults(result) );
		resultContent.setWidget(panel);
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
	
	protected void setContent(Widget content) {
		splitLayoutPanel.add(content);
	}
	
	@Override
	public Widget getSidebarWidget() {
		Label optLabel = new Label();
		optLabel.setText(getOptionDescription());
		optLabel.setStyleName(AON.AON_CSS.aonClickableBlock());
		optLabel.addStyleName(AON.AON_CSS.aonPadding());
		
		optLabel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SelectionEvent.<IOption>fire(OptionBase.this, OptionBase.this);
			}
		});
		return optLabel;
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<IOption> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	protected Widget getToolbarPanel() {
		FlowPanel toolbarPanel = new FlowPanel();
		toolbarPanel.setStyleName(AON.AON_CSS.aonFindingTitleToolbar());
		toolbarPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		FlexTable toolbar = new FlexTable();
		toolbar.setCellPadding(0);
		toolbar.setCellSpacing(0);
		toolbar.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.setStyleName(AON.AON_CSS.aonFindingTitleInternal());
		toolbar.setWidget(0, 0, titlePanel);
		toolbar.setWidget(0, 0, new Label(AonStringUtils.abbreviate(getOptionDescription(),30)));
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}

	protected abstract Widget paintResults(AccUtilitiesResult result);
	
}


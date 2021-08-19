package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MaximizeHandler;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MinimizeHandler;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

abstract class OptionBase extends SimpleLayoutPanel implements IOption {

	interface TabLayoutFolderSafeTemplate extends SafeHtmlTemplates {
		@Template ("<span class=\"aon_tab_label {1}\">{0}</span>")
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
	private AonMinimizePanel footPanel;
	private TabLayoutPanel tabLayout;
	private FlexTable infoPanelTab;
	private SimpleLayoutPanel notificationsContent;
	private SimpleLayoutPanel resultContent;
	
	public OptionBase(String domainName, String user, Domain domain) {
		this.domainName = domainName;
		this.user = user;
		this.domain = domain;
		
		mainPanel = new DockLayoutPanel(Unit.PX);
		mainPanel.addNorth(getToolbarPanel(), 50);
		
		splitLayoutPanel = new SplitLayoutPanel();
		footPanel = new AonMinimizePanel();
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
		footPanel.setStyleName(AON.CSS.aonSelector());
		tabLayout = new TabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		
		notificationsContent = new SimpleLayoutPanel();
		tabLayout.add(notificationsContent, TABLAYOUT_FOLDER_TEMPLATE.tab(AON.MSG.notifications(), AON.CSS.aonIconList()));
		
		resultContent = new SimpleLayoutPanel();
		tabLayout.add(resultContent, TABLAYOUT_FOLDER_TEMPLATE.tab(AON.MSG.result(), AON.CSS.aonIconPreview()));
		
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
	protected void showResults(Widget widget) {
		openFootPanelIfNeeded();
		tabLayout.selectTab(RESULTS_TAB);
		resultContent.setWidget(widget);
	}
	protected void showResults(AccUtilitiesResult result) {
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
		prepareInfoPanel();
		addErrorPanel(msg);
	}

	protected void addErrorPanel(String msg) {
		int row = infoPanelTab.getRowCount();
		infoPanelTab.setWidget(row, 0, new Label());
		InlineLabel label = new InlineLabel(msg);
		label.setStyleName(AON.CSS.aonBlockMessage());
		label.addStyleName(AON.CSS.aonBlockErrorMessage());
		label.addStyleName(AON.CSS.aonBold());
		infoPanelTab.setWidget(row, 1, label);
	}

	protected void prepareInfoPanel() {
		openFootPanelIfNeeded();
		tabLayout.selectTab(NOTIFICATIONS_TAB);
		ScrollPanel panel = new ScrollPanel();
		infoPanelTab = new FlexTable();
		infoPanelTab.setWidth("95%");
		infoPanelTab.setStyleName(AON.CSS.aonBlockCenter());
		infoPanelTab.addStyleName(AON.CSS.aonMarginBottom());
		infoPanelTab.addStyleName(AON.CSS.aonMarginTop());
		infoPanelTab.getColumnFormatter().setWidth(0, "20px");
		infoPanelTab.getColumnFormatter().setWidth(1, "auto");
		
		panel.add(infoPanelTab);
		notificationsContent.setWidget(panel);
	}
	protected void addInfoPanel(String msg) {
		int row = infoPanelTab.getRowCount();
		infoPanelTab.setWidget(row, 0, new Label());

		InlineLabel label = new InlineLabel(msg);
		label.setStyleName(AON.CSS.aonBlockMessage());
		label.addStyleName(AON.CSS.aonBlockInfoMessage());
		label.addStyleName(AON.CSS.aonBold());
		infoPanelTab.setWidget(row, 1, label);
	}
	
	protected void showInfoPanel(String msg) {
		prepareInfoPanel();
		addInfoPanel( msg );
	}

	protected void setContent(Widget content) {
		splitLayoutPanel.add(content);
	}
	
	@Override
	public Widget getSidebarWidget() {
		Label optLabel = new Label();
		optLabel.setText(getOptionDescription());
		optLabel.setStyleName(AON.CSS.aonClickableBlock());
		optLabel.addStyleName(AON.CSS.aonPadding());
		
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
		AonToolbar toolbarPanel = new AonToolbar( AonStringUtils.abbreviate(getOptionDescription(),50));
		return toolbarPanel;
	}


	protected Widget getSplashWidget() {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setStyleName(AON.CSS.aonBlockCenter());
		hp.addStyleName(AON.CSS.aonMarginTop() );
		Label iconWaitLabel = new Label();
		iconWaitLabel.setStyleName(AON.CSS.aonLoader());
		iconWaitLabel.addStyleName(AON.CSS.aonMargin());
		hp.add(iconWaitLabel);
		Label textWaitLabel = new Label( AON.MSG.processing());
		textWaitLabel.setStyleName(AON.CSS.aonMargin());
		textWaitLabel.addStyleName(AON.CSS.aonBold());
		hp.add(textWaitLabel);
		return hp;
	}
	
	protected abstract Widget paintResults(AccUtilitiesResult result);
	
}


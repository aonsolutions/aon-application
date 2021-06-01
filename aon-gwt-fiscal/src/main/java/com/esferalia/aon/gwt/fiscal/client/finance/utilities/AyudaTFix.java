package com.esferalia.aon.gwt.fiscal.client.finance.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.occam.api.model.finance.utilities.IFinanceUtilitiesItem;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class AyudaTFix extends OptionBase {

	private static FinanceUtilitiesServiceAsync SERVICE;
	private String domainName;
	private String user;
	private Domain domain;
	
	private DockLayoutPanel dockPanel = new  DockLayoutPanel(Unit.PX);
	private SimpleLayoutPanel content;
	private ScrollPanel container;
	
	protected AyudaTFix(String domainName, String user, Domain domain) {
		super(domainName, user, domain);
		this.domainName = domainName;
		this.user = user;
		this.domain = domain;
		
		FinanceUtilitiesServiceAsync serviceRaw = GWT.create(FinanceUtilitiesService.class);
		SERVICE = new FinanceUtilitiesServiceAsyncDecorator(serviceRaw);

		
		Button run = new Button();
		run.setText("Ejecutar");
		run.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		run.addStyleName(AON.AON_CSS.aonSimpleBorder());
		run.addStyleName(AON.AON_CSS.aonClickable());
		run.addStyleName(AON.AON_CSS.aonIconLoupe());
		run.addStyleName(AON.AON_CSS.aonMarginTop());
		run.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				run.setEnabled(false);
				run();
			}
		});
		
		
		setContent(dockPanel);
		
		dockPanel.addNorth(run, 150);
		content = new SimpleLayoutPanel();
		content.setStyleName(AON.AON_CSS.aonBorderTop());
		
		container = new ScrollPanel();
		container.setStyleName(AON.AON_CSS.aonScrollArea());
		content.add(container);
		dockPanel.add(content);
		
	}
	
	@Override
	public String getOptionDescription() {
		return AonStringUtils.BULLET + " Arreglo AyudaT.";
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
		toolbar.setWidget(0, 0, new Label(getOptionDescription()));
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
	
	
	public void run() {
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		SERVICE.ayudatFix(domainName, user, domain, new AsyncCallback<FinanceUtilitiesResult>(){

			@Override
			public void onFailure(Throwable caught) {
				openFootPanelIfNeeded();
				showErrorPanel(caught.getMessage());
				popup.hide();
			}

			@Override
			public void onSuccess(FinanceUtilitiesResult result) {
				popup.hide();
				cleanErrorPanel();
				container.setWidget( paintResults(result) );
			}
		});
	}

	@Override
	protected Widget paintResults(FinanceUtilitiesResult result) {
		FlowPanel tabContainer = new FlowPanel("pre");
		tabContainer.addStyleName(AON.CSS.aonFixedFont());
		tabContainer.addStyleName(AON.CSS.aonFontSmaller());

		if (result.getItems() != null && result.getItems().size() > 0) {
			for ( IFinanceUtilitiesItem it : result.getItems() ) {
				Label label = new Label(it.getMessage()); 
				label.setStyleName(AON.AON_CSS.aonPre());
				tabContainer.add(label);
			}
		}
		return tabContainer;
	}
}

package com.esferalia.aon.gwt.fiscal.client.finance.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.Domain;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class FinanceUtilitiesModule extends MainEntryPoint{

	static final FinanceUtilitiesServiceAsync SERVICE;
	static {
		FinanceUtilitiesServiceAsync serviceRaw = GWT.create(FinanceUtilitiesService.class);
		SERVICE = new FinanceUtilitiesServiceAsyncDecorator(serviceRaw);
	}

	protected static interface IOption extends HasSelectionHandlers<IOption> {
		Widget getSidebarWidget();
		String getOptionDescription();
	}

	private FinanceUtilitiesModuleOptions options;
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		FinanceUtilitiesModuleOptions opts = new FinanceUtilitiesModuleOptions();
		opts.setParentWidget(root);
		opts.setDomainName(getCurrentDomainName());
		opts.setDomain(getCurrentDomain());
		opts.setUser(getCurrentUser());
		this.onModuleLoad( opts );
	}
	
	public void onModuleLoad(FinanceUtilitiesModuleOptions options) {
		this.options = options;
		
		AON.ensureInjected();
		
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		

		SERVICE.getDomain(options.getOccam(), new AsyncCallback<Domain>() {
			
			@Override
			public void onSuccess(Domain domain) {
				root.add(paint( domain ));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				SimpleLayoutPanel content = new SimpleLayoutPanel();		
				Label errorLabel = new Label( "No se ha podido determinar la configuraci\u00F3n" );
				errorLabel.setStyleName(AON.AON_CSS.aonBold());
				errorLabel.addStyleName(AON.AON_CSS.aonColorRed());
				errorLabel.addStyleName(AON.AON_CSS.aonErrorPanel());
				content.setWidget(errorLabel);
				root.add(content);
			}
		});
		
	}
	
	protected Widget paint(Domain domain) {
		AonLayoutPanel dockLayoutPanel = new AonLayoutPanel(Unit.PX);
		dockLayoutPanel.setStyleName(AON.CSS.aonSelector());
		dockLayoutPanel.addNorth(getToolbarPanel(), 25);
		
		SimpleLayoutPanel sidebar = new SimpleLayoutPanel();
		sidebar.setStyleName(AON.AON_CSS.aonBorderRight());
		dockLayoutPanel.addWest(sidebar, 275);
		
		ScrollPanel scrollPanel = new ScrollPanel(); 
		scrollPanel.setStyleName(AON.AON_CSS.aonWidthAll());
		sidebar.setWidget(scrollPanel);
		
		FlowPanel sidebarMenu = new FlowPanel();
		scrollPanel.setWidget(sidebarMenu);

		SimpleLayoutPanel content = new SimpleLayoutPanel();
		
		DisclosurePanel checksDisclosurePanel = new DisclosurePanel("VENCIMIENTOS");
		checksDisclosurePanel.setOpen(true);
		FlowPanel checksPanel = new FlowPanel();
		checksDisclosurePanel.add(checksPanel);
		
		MissingFinanceInvoicesCheck missingFinanceInvoices = new MissingFinanceInvoicesCheck(options,domain);
		checksPanel.add(missingFinanceInvoices.getSidebarWidget());
		missingFinanceInvoices.addSelectionHandler( event -> content.setWidget( missingFinanceInvoices ));

		
		FinanceInvoiceIntegrityCheck financeInvoiceIntegrityCheck = new FinanceInvoiceIntegrityCheck(options,domain);
		checksPanel.add(financeInvoiceIntegrityCheck.getSidebarWidget());
		financeInvoiceIntegrityCheck.addSelectionHandler( event -> {
			content.setWidget( financeInvoiceIntegrityCheck );
			financeInvoiceIntegrityCheck.run();
		});
		
		sidebarMenu.add(checksDisclosurePanel);
		
		dockLayoutPanel.add( content );
		return dockLayoutPanel; 
	}

	private Widget getToolbarPanel() {
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
		toolbar.setWidget(0, 0, new Label("Utilidades de tesorer\u00EDa"));
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
	
}

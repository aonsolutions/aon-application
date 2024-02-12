package com.esferalia.aon.gwt.fiscal.client.aeat;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTabLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.aeat.AeatCommunicationCenterPanel.IAeatCommunicationCenterPanelCallback;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

public class AeatModule extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(AeatModule.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
	}
	
	private AonLayoutPanel aonLayout;
	private SplitLayoutPanel splitLayoutPanel;
	private AonTabLayoutPanel tabLayout;
	private AonMinimizePanel footPanel;
	private ScrollPanel infoPanel;
	private AeatModuleModuleOptions options;
	
	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {
			@Override
			public void onSuccess(AonConfiguration config) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				AeatModuleModuleOptions opts = new AeatModuleModuleOptions();
				opts.setParentWidget(root);
				opts.setDomainName(getCurrentDomainName());
				opts.setDomain(getCurrentDomain());
				opts.setUser(getCurrentUser());
				opts.setConfiguration(config);
				onModuleLoad( opts );
			}
			
			@Override 
			public void onFailure(Throwable caught) {
				Window.alert( AON.MSG.loadError("Modelo 303"));
			}
		});
	}
	private AeatModuleModuleOptions getOptions() {
		if (this.options == null) {
			this.options = new AeatModuleModuleOptions();
		}
		return this.options;
	}
	
	public void onModuleLoad(AeatModuleModuleOptions options) {
		this.options = options;
		AON.ensureInjected();

		aonLayout = new AonLayoutPanel();
		aonLayout.addStyleName("aon-Model");
		
		AonToolbar toolbar = new AonToolbar("Centro de comunicaci\u00F3n con la Agencia Tributaria");
		aonLayout.addNorth( toolbar , AonToolbar.HEIGTH );
		
		splitLayoutPanel = new SplitLayoutPanel( 2 );
		aonLayout.add(splitLayoutPanel);
		
		AonMinimizePanel minimizePanel = getMinimizePanel();
		minimizePanel.addStyleName("aon-Model-Info");
		splitLayoutPanel.addSouth(minimizePanel, 30);
		
		AeatCommunicationCenterPanel aeatPanel = new AeatCommunicationCenterPanel( new IAeatCommunicationCenterPanelCallback() {
			
			@Override
			public void showError(String msg) {
				aonLayout.showErrorPanel(msg);
			}
			
			@Override
			public AeatModuleModuleOptions getOptions() {
				return options;
			}
			
			@Override
			public String getFiscalDataAction() {
				return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/AeatFiscalData";
			}
			
			@Override
			public String getAddressCheckAction() {
				return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/AeatCheckAddress";
			}
		});
		splitLayoutPanel.add( aeatPanel );
		
		getOptions().getParentWidget().add(aonLayout);
	}


	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 30);
		splitLayoutPanel.animate(500);
	}

	private void openFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4.0);
		splitLayoutPanel.animate(500);
	}
	private void openFootPanelIfNeeded() {
		if (splitLayoutPanel.getWidgetSize(footPanel) <= 50) {
			openFootPanel();
		}
	}
	private void maximizeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 2.0);
		splitLayoutPanel.animate(500);
	}
	
	private AonMinimizePanel getMinimizePanel() {
		footPanel = new AonMinimizePanel();
		footPanel.addMinimizeHandler( event -> closeFootPanel() );
		footPanel.addMaximizeHandler( event -> maximizeFootPanel());
		footPanel.setStyleName(AON.CSS.aonSelector());
		tabLayout = new AonTabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		footPanel.add(tabLayout);
		
		infoPanel = new ScrollPanel();
		tabLayout.add(infoPanel, AON.MSG.informationBreakdown());

		tabLayout.setAnimationDuration(300);
		tabLayout.addSelectionHandler( event -> openFootPanelIfNeeded());
		return footPanel; 
	}

	public static void run() {
		GWT.runAsync(AeatModule.class, new RunAsyncCallback() {

			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("Modelo 303"));
			}

			@Override
			public void onSuccess() {
				AeatModule model303 = new AeatModule();
				model303.onModuleLoad();
			}
			
		});
	}
	
}

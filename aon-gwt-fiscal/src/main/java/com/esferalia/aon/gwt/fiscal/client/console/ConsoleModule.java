package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
 
public class ConsoleModule extends MainEntryPoint {
	
	private static final Logger LOGGER = Logger.getLogger(ConsoleModule.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
	}

	private ConsoleModuleOptions options;
	private AonLayoutPanel aonLayout;

	private ConsoleModuleOptions getOptions() {
		if (this.options == null) {
			this.options = new ConsoleModuleOptions();
		}
		return this.options;
	}

	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser()
				, new AsyncCallback<AonConfiguration>() {
				
				@Override
				public void onSuccess(AonConfiguration config) {
					RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
					ConsoleModuleOptions opts = new ConsoleModuleOptions();
					opts.setParentWidget(root);
					opts.setDomainName(getCurrentDomainName());
					opts.setDomain(getCurrentDomain());
					opts.setUser(getCurrentUser());
					opts.setConfiguration(config);
					onModuleLoad( opts );
				}
				
				@Override public void onFailure(Throwable caught) {
					Window.alert( "Error al cargar el module" );
				}
			});
	}
	
	public void onModuleLoad(ConsoleModuleOptions options) {
		this.options = options;
		AON.ensureInjected();

		SimpleLayoutPanel content = new SimpleLayoutPanel();
		
		aonLayout = new AonLayoutPanel();
		aonLayout.addNorth(getToolbarPanel(), AonToolbar.HEIGTH);

		
		SimpleLayoutPanel sidebar = new SimpleLayoutPanel();
		sidebar.setStyleName(AON.CSS.aonBorderRight());
		ScrollPanel scrollPanel = new ScrollPanel(); 
		scrollPanel.setStyleName(AON.CSS.aonWidthAll());
		sidebar.setWidget(scrollPanel);
		FlowPanel sidebarMenu = new FlowPanel();
		scrollPanel.setWidget(sidebarMenu);

		AonDisplayTable optionsGrid = new AonDisplayTable();
		sidebarMenu.add(optionsGrid);
		optionsGrid.addRow().addCell(new InlineLabel("UTILIDADES"), AON.CSS.aonBold(), AON.CSS.aonTextUnderline());
		
		Label opt1 = new Label(AonStringUtils.BULLET + "Validaci\u00F3n de dominios");
		opt1.setStyleName(AON.CSS.aonClickableBlock());
		opt1.addStyleName(AON.CSS.aonPadding());
		opt1.addClickHandler( e -> content.setWidget( new ConsoleDomainCheckIntegrity(options) ));
		optionsGrid.addRow().addCell(opt1);

		Label opt2 = new Label(AonStringUtils.BULLET + "Extracci\u00F3n de dominios");
		opt2.setStyleName(AON.CSS.aonClickableBlock());
		opt2.addStyleName(AON.CSS.aonPadding());
		opt2.addClickHandler( e -> content.setWidget( new ConsoleDomainIsolate(options) ));
		optionsGrid.addRow().addCell(opt2);
		
		aonLayout.addWest(sidebar, 275);
		aonLayout.add(content);
		
		getOptions().getParentWidget().add(aonLayout);
	}

	private AonToolbar getToolbarPanel() {
		AonToolbar toolbarPanel = new AonToolbar();
		toolbarPanel.setTitle("M\u00F3dulo CONSOLE");
		return toolbarPanel;
	}
	
	
	public static void run() {
		GWT.runAsync(ConsoleModule.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("ConsoleModule"));
			}

			@Override
			public void onSuccess() {
				ConsoleModule domainIntegrityCheck = new ConsoleModule();
				domainIntegrityCheck.onModuleLoad();
			}
			
		});
	}
	
}

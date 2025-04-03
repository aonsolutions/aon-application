package com.esferalia.aon.gwt.fiscal.client.rawdoc;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.RawdocService;
import com.esferalia.aon.gwt.fiscal.client.RawdocServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.RawdocServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.rawdoc.RawdocAttachPanel.RawdocAttachPanelCallback;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.RawdocParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class RawdocModuleNew extends MainEntryPoint {
	private static final Logger LOGGER = Logger.getLogger(RawdocModuleNew.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	static final RawdocServiceAsync RAWDOC_SERVICE;
	static {
		RawdocServiceAsync rawdocServiceRaw = GWT.create(RawdocService.class);
		RAWDOC_SERVICE = new RawdocServiceAsyncDecorator(rawdocServiceRaw);
	}
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
	}
	
	private DockLayoutPanel dockLayoutPanel;
	 
	private SplitLayoutPanel splitLayoutPanel;
	private SimpleLayoutPanel centerLayoutPanel;
	
	private AonMinimizePanel footPanel;
	private TabLayoutPanel tabLayout;
	private ScrollPanel extraInfoContainer;
	
	private RawdocAttachPanel attachPanel;
	
	private AonToolbar toolbar;
	
	private boolean minimizedByUser;
	private int extraInfoTabIndex;

	class RawdocCallback {
		void showError(String msg) {
			if (AonStringUtils.isBlank(msg)) {
				msg = "Se ha producido un error no codificado.";
			}
			toolbar.showErrorMessage(msg);
		}

		void showExtraInfo(Widget widget) {
			openFootPanelIfNeeded(5);
			tabLayout.selectTab(extraInfoTabIndex);
			extraInfoContainer.setWidget(widget);
			extraInfoContainer.scrollToTop();
		}
		
		public void showViewer( MimeType mimeType, String url ) {
			attachPanel.showViewer( mimeType, url
				, new RawdocAttachPanelCallback() {
					public boolean openAttach() {
						double from = splitLayoutPanel.getWidgetSize(attachPanel) == null? 0 : splitLayoutPanel.getWidgetSize(attachPanel);
						int to = Window.getClientWidth() - 900;
						if (from < to) {
							splitLayoutPanel.setWidgetSize(attachPanel, to);
							return true;
						}
						return false;
					}
					
					public void closeAttach() {
						splitLayoutPanel.setWidgetSize(attachPanel, 20);
					}
				} 
			);
		}
		
	}

	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		RawdocModuleOptions options = new RawdocModuleOptions()
			.setParentWidget(root)
			.setDomainName(getCurrentDomainName())
			.setDomain(getCurrentDomain())
			.setUser(getCurrentUser())
			.setParams(new RawdocParams()
				.setDomain(getCurrentDomain())
				.setDomainName(getCurrentDomainName())
				.setStatus(RawdocStatus.INBOX))
		;
		this.onModuleLoad( options );
	}
	
	public void onModuleLoad( final RawdocModuleOptions opt ) {
		AON.ensureInjected();
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		opt.getParentWidget().add(dockLayoutPanel);
		if ( opt.getConfiguration() == null) {
			COMMON_SERVICE.getAonConfiguration(opt.getDomainName(),opt.getDomain(),opt.getUser(),new AsyncCallback<AonConfiguration>() {
				@Override
				public void onSuccess(AonConfiguration result) {
					opt.setConfiguration(result);
					loadModule( opt );					
				}
				
				@Override
				public void onFailure(Throwable caught) {
					dockLayoutPanel.add(new Label(AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage()+ "]"));
				}
			});
		} else {
			loadModule( opt );
		}
	}
	
	private void loadModule( final RawdocModuleOptions opt ) {
		dockLayoutPanel.addNorth(getToolbarPanel( opt ), AonToolbar.HEIGTH );

		splitLayoutPanel = new SplitLayoutPanel();
		dockLayoutPanel.add(splitLayoutPanel);
		
		splitLayoutPanel.addSouth(getMinimizePanel(), 30);
		
		attachPanel = new RawdocAttachPanel( );
		splitLayoutPanel.addEast(attachPanel,0);
		
		centerLayoutPanel = new SimpleLayoutPanel();
		splitLayoutPanel.add(centerLayoutPanel);
		search(opt);
	}



	private Widget getToolbarPanel(final RawdocModuleOptions opt) {
		toolbar = new AonToolbar(AON.MSG.rawdocModule());

		AonToolbarButton refreshButton = new AonToolbarButton( AON.MSG.refresh(), AON.CSS.aonIconRefresh() );
		refreshButton.addClickHandler(event -> search( opt ));
		toolbar.add(refreshButton);

		AonToolbarButton allInboxButton = new AonToolbarButton( AON.MSG.all(), AON.CSS.aonIconAllInbox() );
		allInboxButton.addClickHandler(event -> search( opt , null));
		toolbar.add(allInboxButton);

		AonToolbarButton inboxButton = new AonToolbarButton( AON.MSG.inbox(), AON.CSS.aonIconInbox() );
		inboxButton.addClickHandler(event -> search( opt , RawdocStatus.INBOX));
		toolbar.add(inboxButton);

		AonToolbarButton rejectedButton = new AonToolbarButton( AON.MSG.rejectedDocs(), AON.CSS.aonIconReject() );
		rejectedButton.addClickHandler(event -> search( opt , RawdocStatus.REJECTED));
		toolbar.add(rejectedButton);

		AonToolbarButton draftButton = new AonToolbarButton( AON.MSG.draftDocs(), AON.CSS.aonIconDraft() );
		draftButton.addClickHandler(event -> search( opt , RawdocStatus.DRAFT));
		toolbar.add(draftButton);

		return toolbar;
	}
	
	private AonMinimizePanel getMinimizePanel() {
		footPanel = new AonMinimizePanel();
		footPanel.addMinimizeHandler(event -> {
			minimizedByUser = true;
			closeFootPanel();
		});
		footPanel.addMaximizeHandler(event -> openFootPanel(5));
		footPanel.setStyleName(AON.CSS.aonSelector());
		tabLayout = new TabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		
		footPanel.add(tabLayout);
		int tabIndex = 0;
		
		extraInfoContainer = new ScrollPanel();
		tabLayout.add(extraInfoContainer, AON.MSG.additionalData());
		extraInfoTabIndex = tabIndex;

		tabLayout.setAnimationDuration(300);
		tabLayout.addSelectionHandler(event -> {
			minimizedByUser = false;
			openFootPanelIfNeeded();
		});
		return footPanel; 
	}

	private void search(RawdocModuleOptions opt, RawdocStatus status) {
		clearFootInfo();
		splitLayoutPanel.setWidgetSize(attachPanel, 20);
		opt.setParams(
			new RawdocParams()
				.setDomain(opt.getDomain())
				.setDomainName(opt.getDomainName())
				.setStatus(status)
		);
		search(opt);
	}

	protected void search(final RawdocModuleOptions opt) {
		centerLayoutPanel.clear();
		RawdocTable table = new RawdocTable(opt, new RawdocCallback());
		centerLayoutPanel.setWidget( table );
	}

	private void clearFootInfo( ) {
		closeFootPanel();
		clearExtraInfo();
		attachPanel.clear();
	}
	
	private void clearExtraInfo( ) {
		extraInfoContainer.setWidget(new Label());
	}
	
	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 30);
		splitLayoutPanel.animate(500);
	}
	private void openFootPanelIfNeeded() {
		openFootPanelIfNeeded(5);
	}
	
	private void openFootPanelIfNeeded( int effectiveHeigth) {
		if (!minimizedByUser && splitLayoutPanel.getWidgetSize(footPanel) <= 30) {
			openFootPanel(effectiveHeigth);
		}
	}
	private void openFootPanel(int effectiveHeigth) {
		splitLayoutPanel.setWidgetSize(footPanel, ((double) Window.getClientHeight()) / effectiveHeigth);
		splitLayoutPanel.animate(500);
	}

	// ------------------------------- [LAUNCHER]		
	public static void run() {
		GWT.runAsync(RawdocModuleNew.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("RawdocModuleNew"));
			}
			
			@Override
			public void onSuccess() {
				RawdocModuleNew rawdocModuleNew = new RawdocModuleNew();
				rawdocModuleNew.onModuleLoad();
			}
		});
	}
	
}		

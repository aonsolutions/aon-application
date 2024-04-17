package com.esferalia.aon.gwt.fiscal.client.mod131;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model131 extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Model131.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	static final int INFORMATION_TAB = 0;
	
	static final Mod131ServiceAsync SERVICE;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
		
		Mod131ServiceAsync serviceRaw = GWT.create(Mod131Service.class);
		SERVICE = new Mod131ServiceAsyncDecorator(serviceRaw);
	}

	private Model131ModuleOptions options;

	private AonLayoutPanel aonLayout;
	private SplitLayoutPanel splitLayoutPanel;
	private SimpleLayoutPanel declarationContainer;
	private Model131Table model131Table;
	private TabLayoutPanel tabLayout;
	private AonMinimizePanel footPanel;
	private ScrollPanel breakdownPanel;	

	protected class Model131Callback implements IFiscalModelCallback<Mod131,Model131ModuleOptions> {
		
		@Override
		public void showError(String msg) {
			showErrorMessage(msg);
		}
		
		@Override
		public void hideError() {
			aonLayout.hideErrorPanel();
		}

		@Override
		public void showInfoPanel(String htmlText) {
			showInfoPanelWidget(new HTMLPanel(htmlText));
		}
		
		public void showInfoPanelWidget(Widget widget) {
			cleanInfoPanel();
			openFootPanelIfNeeded();
			tabLayout.selectTab(INFORMATION_TAB);
			breakdownPanel.setWidget(widget);
			breakdownPanel.scrollToTop();
		}

		@Override
		public void cleanInfoPanel() {
			Widget w = breakdownPanel.getWidget();
			if (w != null) {
				breakdownPanel.remove( breakdownPanel.getWidget() ); 
			}
		}

		@Override
		public Model131ModuleOptions getOptions() {
			return Model131.this.options;
		}

		@Override
		public void onAccept(Mod131 model) {
			// Nothing
		}

		@Override
		public void onCancel(Mod131 model) {
			cleanInfoPanel();
			declarationContainer.setWidget(model131Table);
			model131Table.refresh( new Model131Callback() );
			tabLayout.selectTab(INFORMATION_TAB);
			closeFootPanel();
		}

		@Override
		public void onRemove(Mod131 model) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onExit(model);
			} else {
				onCancel(model);
			}
		}

		@Override
		public void onNew() {
			SERVICE.initialize(getOptions().getOccam(),null,
			new AsyncCallback<Mod131>() {
				@Override
				public void onSuccess(Mod131 m131) {
					cleanInfoPanel();
					tabLayout.selectTab(INFORMATION_TAB);
					closeFootPanel();
					showNewDeclarationPanel(m131);
				}


				@Override
				public void onFailure(Throwable caught) {
					showErrorMessage(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
				}
			});
		}
	
	}

	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {
			
			@Override
			public void onSuccess(AonConfiguration config) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model131ModuleOptions opts = new Model131ModuleOptions();
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
	
	private Model131ModuleOptions getOptions() {
		if (this.options == null) {
			this.options = new Model131ModuleOptions();
		}
		return this.options;
	}
	
	public void onModuleLoad(Model131ModuleOptions options) {
		this.options = options;
		AON.ensureInjected();

		aonLayout = new AonLayoutPanel();
		aonLayout.addStyleName("aon-Model");
		splitLayoutPanel = new SplitLayoutPanel( 2 );
		aonLayout.add(splitLayoutPanel);
		
		declarationContainer = new SimpleLayoutPanel();
		declarationContainer.addStyleName("aon-Model-Detail");
		AonMinimizePanel minimizePanel = getMinimizePanel();
		minimizePanel.addStyleName("aon-Model-Info");
		splitLayoutPanel.addSouth(minimizePanel, 30);

		splitLayoutPanel.add(declarationContainer);

		model131Table = new Model131Table( new Model131Callback() );
		model131Table.addSelectionHandler( this::onSelectionChange );
		declarationContainer.setWidget(model131Table);
		
		getOptions().getParentWidget().add(aonLayout);
		if (getOptions().getFiscalModelId() != null ) {
			onSelect(getOptions().getFiscalModelId());
		} else if (getOptions().getNewModel() != null ) {
			newModel(getOptions().getNewModel()); 
		} else {
			model131Table.refresh( new Model131Callback() );
		}
		tabLayout.setAnimationDuration(300);
		tabLayout.addSelectionHandler(event -> openFootPanelIfNeeded());
	}
	
	private AonMinimizePanel getMinimizePanel() {
		footPanel = new AonMinimizePanel();
		footPanel.addMinimizeHandler( event -> closeFootPanel() );
		footPanel.addMaximizeHandler( event -> {
			splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 2.0);
			splitLayoutPanel.animate(500);
		});
		footPanel.setStyleName(AON.CSS.aonSelector());
		tabLayout = new TabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		footPanel.add(tabLayout);
		
		breakdownPanel = new ScrollPanel();
		tabLayout.add(breakdownPanel, AON.MSG.informationBreakdown());

		tabLayout.setAnimationDuration(300);
		tabLayout.addSelectionHandler( event -> openFootPanelIfNeeded());
		return footPanel; 
	}

	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 30);
		splitLayoutPanel.animate(500);
	}
	private void openFootPanelIfNeeded() {
		if (splitLayoutPanel.getWidgetSize(footPanel) <= 50) {
			splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4.0);
			splitLayoutPanel.animate(500);
		}
	}

	private void onSelect(Integer id ) {
		LOGGER.info("OnSelect Model131 with a ID: " + getOptions().getFiscalModelId());
		SERVICE.get(getOptions().getOccam(), id , new AsyncCallback<Mod131>() {
			@Override
			public void onSuccess(Mod131 selected) {
				if (selected == null) {
					showErrorMessage(AON.MSG.unableToFindDeclaration());
				} else {
					LOGGER.info("onSuccess Model131 with a ID: " + selected.getId());
					select(selected);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
			}
		});
	}
	
	private void newModel(Mod131 newModel) {
		SERVICE.initialize(getOptions().getOccam(),newModel, new AsyncCallback<Mod131>() {
			@Override
			public void onSuccess(Mod131 m131) {
				tabLayout.selectTab(INFORMATION_TAB);
				closeFootPanel();
				showNewDeclarationPanel( m131 );
			}

			@Override
			public void onFailure(Throwable caught) {
				aonLayout.showErrorPanel(AON.MSG.unableToInitializeDeclaration(caught.getMessage()));
			}
		});
	}

	enum Mod131Declarations {
		AEAT_2024 {
			@Override
			public boolean accept(Mod131 mod131) {
				return mod131.isAEAT() && mod131.getYear() >= 2024;
			}

			@Override
			public Widget getDeclarationWidget(Mod131 mod131, Model131Callback cbk) {
				return new Model131AEAT2024(mod131, cbk);
			}
		}
		,AEAT_2023 {
			@Override
			public boolean accept(Mod131 mod131) {
				return mod131.isAEAT() && mod131.getYear() < 2024;
			}

			@Override
			public Widget getDeclarationWidget(Mod131 mod131, Model131Callback cbk) {
				return new Model131AEAT2023(mod131, cbk);
			}
		},
		
		;
		public abstract boolean accept(Mod131 mod131);
		public abstract Widget getDeclarationWidget(Mod131 mod131, Model131Callback cbk);
	}

	private void select(Mod131 selected) {
		aonLayout.hideErrorPanel();
		Widget declaration = null;
		for (Mod131Declarations dec : Mod131Declarations.values()) {
			if (dec.accept(selected)) {
				declaration = dec.getDeclarationWidget(selected, new Model131Callback());
			}
		}
		if (declaration != null) {
			declarationContainer.setWidget( declaration );		
		} else {
			aonLayout.showErrorPanel("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}

	private void onSelectionChange(SelectionEvent<Mod131> event) {
		Mod131 sel = event.getSelectedItem();
		SERVICE.get(getOptions().getOccam(),sel.getId(), new AsyncCallback<Mod131>() {
			@Override
			public void onSuccess(Mod131 selected) {
				if (selected == null) {
					aonLayout.showErrorPanel(AON.MSG.unableToFindDeclaration());
				} else {
					select(selected);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
			}
		});
	}
	
	private void showNewDeclarationPanel( Mod131 mod131) {
		Model131NewDeclarationPanel newDeclarationPanel = new Model131NewDeclarationPanel(mod131, new Model131Callback() {
			@Override
			public void onAccept(Mod131 mod131) {
				SERVICE.create(getOptions().getOccam(),mod131, new AsyncCallback<Mod131>() {
					@Override
					public void onSuccess(Mod131 m131) {
						select(m131);
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorMessage(AON.MSG.unableToInitializeDeclaration(caught.getMessage()));
					}
				});
			}
		}); 
		declarationContainer.setWidget(newDeclarationPanel);
		tabLayout.selectTab(INFORMATION_TAB);
		closeFootPanel();
	}
	

	private void showErrorMessage(String msg) {
		aonLayout.showErrorPanel(msg);
	}
	
	public static void run() {
		GWT.runAsync(Model131.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("Modelo 131"));
			}

			@Override
			public void onSuccess() {
				Model131 model131 = new Model131();
				model131.onModuleLoad();
			}
			
		});
	}
	
}

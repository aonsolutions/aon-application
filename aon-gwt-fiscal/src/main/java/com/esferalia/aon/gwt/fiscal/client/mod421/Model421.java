package com.esferalia.aon.gwt.fiscal.client.mod421;

import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomain;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomainName;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentUser;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getRootPanel;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTabLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model421 implements EntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Model421.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static final int INFORMATION_TAB = 0;

	protected static Mod421ServiceAsync service;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		Mod421ServiceAsync mod421ServiceRaw = GWT.create(Mod421Service.class);
		service = new Mod421ServiceAsyncDecorator(mod421ServiceRaw);
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
	}
	
	private AonLayoutPanel aonLayout;
	private SplitLayoutPanel splitLayoutPanel;
	private SimpleLayoutPanel declarationContainer;
	private AonTabLayoutPanel tabLayout;
	private AonMinimizePanel footPanel;
	private ScrollPanel breakdownPanel;
	private Model421ModuleOptions options;
	private Model421Table model421Table;

	protected class Model421Callback implements IFiscalModelCallback<Mod421, Model421ModuleOptions> {

		@Override
		public void onAccept(Mod421 mod421) {
			// REDEFINE
		}
		
		@Override
		public void onRemove(Mod421 mod421) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onRemove(mod421);
			} else {
				onCancel(mod421);
			}
		}
		
		@Override
		public void onCancel(Mod421 mod421) {
			aonLayout.hideErrorPanel();
			cleanInfoPanel();
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onExit(mod421);
			} else {
				declarationContainer.setWidget(model421Table);
				model421Table.refresh( new Model421Callback() );
				tabLayout.selectTab(INFORMATION_TAB);
				closeFootPanel();
			}
		}
		
		@Override
		public void onNew() {
			aonLayout.hideErrorPanel();
			service.initialize(getOptions().getOccam(),null,
					new AsyncCallback<Mod421>() {
						@Override
						public void onSuccess(Mod421 m421) {
							tabLayout.selectTab(INFORMATION_TAB);
							closeFootPanel();
							showNewDeclarationPanel(m421);
						}

						@Override
						public void onFailure(Throwable caught) {
							tabLayout.selectTab(INFORMATION_TAB);
							closeFootPanel();
							showNewDeclarationPanel( null );
						}
					});
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
		
		public Widget getTabWidget(String tabLabel) {
			SimpleLayoutPanel container = (SimpleLayoutPanel) tabLayout.getOrCreateWidget(tabLabel, SimpleLayoutPanel::new);
			tabLayout.selectTab( container );
			maximizeFootPanel();
			return container;
		}
		
		public void removeTabWidget(String tabLabel) {
			tabLayout.remove(tabLabel);
			closeFootPanel();
		}

		@Override
		public void cleanInfoPanel() {
			Widget w = breakdownPanel.getWidget();
			if (w != null) {
				breakdownPanel.remove( breakdownPanel.getWidget() ); 
			}
		}
		
		@Override
		public void showError(String msg) {
			aonLayout.showErrorPanel(msg);
		}

		@Override
		public void hideError() {
			aonLayout.hideErrorPanel();
		}

		@Override
		public Model421ModuleOptions getOptions() {
			return Model421.this.getOptions();
		}

	}
	
	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {
			@Override
			public void onSuccess(AonConfiguration config) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model421ModuleOptions opts = new Model421ModuleOptions();
				opts.setParentWidget(root);
				opts.setDomainName(getCurrentDomainName());
				opts.setDomain(getCurrentDomain());
				opts.setUser(getCurrentUser());
				opts.setConfiguration(config);
				onModuleLoad( opts );
			}
			
			@Override 
			public void onFailure(Throwable caught) {
				Window.alert( AON.MSG.loadError("Modelo 421"));
			}
		});
	}
	private Model421ModuleOptions getOptions() {
		if (this.options == null) {
			this.options = new Model421ModuleOptions();
		}
		return this.options;
	}
	
	public void onModuleLoad(Model421ModuleOptions options) {
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
		
		Model421Callback callback = new Model421Callback();
		
		model421Table = new Model421Table(callback);
		model421Table.addSelectionHandler( this::onSelectionChange );
		declarationContainer.setWidget(model421Table);
		
		getOptions().getParentWidget().add(aonLayout);
		if (getOptions().getFiscalModelId() != null ) {
			LOGGER.info("Access to Model421 with a ID: " + getOptions().getFiscalModelId());
			onSelect(getOptions().getFiscalModelId());
		} else if (getOptions().getNewModel() != null ) {
			LOGGER.info("Access to Model421 new Model");
			newModel(getOptions().getNewModel()); 
		} else {
			model421Table.refresh( callback );
		}
	}

	private void newModel(Mod421 newModel) {
		service.initialize(getOptions().getOccam(),newModel,
				new AsyncCallback<Mod421>() {
					@Override
					public void onSuccess(Mod421 m421) {
						tabLayout.selectTab(INFORMATION_TAB);
						closeFootPanel();
						showNewDeclarationPanel( m421 );
					}


					@Override
					public void onFailure(Throwable caught) {
						aonLayout.showErrorPanel(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
					}
				});
	}

	private void onSelect(Integer id ) {
		LOGGER.info("OnSelect Model421 with a ID: " + getOptions().getFiscalModelId());
		service.getMod421(getOptions().getOccam(), id , new AsyncCallback<Mod421>() {
					@Override
					public void onSuccess(Mod421 selected) {
						if (selected == null) {
							LOGGER.info("onSuccess Model421 with a NULL selected Model ID: ");
							aonLayout.showErrorPanel(AON.MSG.unableToFindDeclaration());
						} else {
							LOGGER.info("onSuccess Model421 with a ID: " + selected.getId());
							select(selected);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						aonLayout.showErrorPanel(AON.MSG.unableToReadDeclaration(caught.getMessage()));
					}
				});
	}

	private void onSelectionChange(SelectionEvent<Mod421> event) {
		Mod421 sel = event.getSelectedItem();
		service.getMod421(getOptions().getOccam(),
				sel.getId(), new AsyncCallback<Mod421>() {
					@Override
					public void onSuccess(Mod421 selected) {
						if (selected == null) {
							aonLayout.showErrorPanel(AON.MSG.unableToFindDeclaration());
						} else {
							select(selected);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						aonLayout.showErrorPanel(AON.MSG.unableToReadDeclaration(caught.getMessage()));
					}
				});
	}

	private void showNewDeclarationPanel(Mod421 m421) {
		Model421NewDeclarationPanel newDeclarationPanel = new Model421NewDeclarationPanel( m421,
			new Model421Callback() {

					@Override
					public void onAccept(Mod421 mod421) {
						final PopupPanel popup = new PopupPanel(false, true);
						popup.add(new AonSplash());
						popup.setGlassEnabled(true);
						popup.setAnimationEnabled(true);
						popup.center();

						// Crear el modelo nuevo
						service.create(getOptions().getOccam(),mod421,
								new AsyncCallback<Mod421>() {
									@Override
									public void onSuccess(Mod421 m421) {
										popup.hide();
										select(m421);
									}

									@Override
									public void onFailure(Throwable caught) {
										popup.hide();
										aonLayout.showErrorPanel(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
									}
								});

					}
				}
			); 
		declarationContainer.setWidget(newDeclarationPanel);
		tabLayout.selectTab(INFORMATION_TAB);
		closeFootPanel();
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
		
		breakdownPanel = new ScrollPanel();
		tabLayout.add(breakdownPanel, AON.MSG.informationBreakdown());

		tabLayout.setAnimationDuration(300);
		tabLayout.addSelectionHandler( event -> openFootPanelIfNeeded());
		return footPanel; 
	}

	enum Mod421Declarations {
		
		ATC_2026 {
			@Override
			public boolean accept(Mod421 mod421) {
				return (mod421.isCanarias() && mod421.getYear() >= 2026);
			}

			@Override
			public Widget getDeclarationWidget(Mod421 mod421, Model421Callback cbk) {
				return new Model421ATC2026(mod421,cbk);
			}
		};
		
		public abstract boolean accept(Mod421 mod421);
		public abstract Widget getDeclarationWidget(Mod421 mod421, Model421Callback cbk);
	}
	
	private void select(Mod421 selected) {
		aonLayout.hideErrorPanel();
		Widget declaration = null;
		for (Mod421Declarations dec : Mod421Declarations.values()) {
			if (dec.accept(selected)) {
				declaration = dec.getDeclarationWidget(selected, new Model421Callback());
			}
		}
		if (declaration != null) {
			declarationContainer.setWidget( declaration );		
		} else {
			aonLayout.showErrorPanel("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}
	
	public static void run() {
		GWT.runAsync(Model421.class, new RunAsyncCallback() {

			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("Modelo 421"));
			}

			@Override
			public void onSuccess() {
				Model421 model421 = new Model421();
				model421.onModuleLoad();
			}
			
		});
	}
	
}

package com.esferalia.aon.gwt.fiscal.client.mod390hf;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
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
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model390HF extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Model390HF.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static final int INFORMATION_TAB = 0;

	protected static final Mod390HFServiceAsync MOD_SERVICE;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
		
		Mod390HFServiceAsync serviceRaw = GWT.create(Mod390HFService.class);
		MOD_SERVICE = new Mod390HFServiceAsyncDecorator(serviceRaw);
	}
	
	private AonLayoutPanel aonLayout;
	private SplitLayoutPanel splitLayoutPanel;
	private SimpleLayoutPanel declarationContainer;
	private TabLayoutPanel tabLayout;
	private AonMinimizePanel footPanel;
	private ScrollPanel breakdownPanel;
	private Model390HFModuleOptions options;
	private  Model390HFTable model390HFTable;

	protected class Model390HFCallback implements IFiscalModelCallback<Mod390HF, Model390HFModuleOptions> {

		@Override
		public void onAccept(Mod390HF mod390HF) {
			// REDEFINE
		}
		
		@Override
		public void onRemove(Mod390HF mod390HF) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onExit(mod390HF);
			} else {
				onCancel(mod390HF);
			}
		}
		
		@Override
		public void onCancel(Mod390HF mod390HF) {
			aonLayout.hideErrorPanel();
			cleanInfoPanel();
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onExit(mod390HF);
			} else {
				declarationContainer.setWidget(model390HFTable);
				model390HFTable.refresh( new Model390HFCallback() );
				tabLayout.selectTab(INFORMATION_TAB);
				closeFootPanel();
			}
		}
		
		@Override
		public void onNew() {
			aonLayout.hideErrorPanel();
			MOD_SERVICE.initialize(getOptions().getOccam(),null,
					new AsyncCallback<Mod390HF>() {
						@Override
						public void onSuccess(Mod390HF m390HF) {
							cleanInfoPanel();
							tabLayout.selectTab(INFORMATION_TAB);
							closeFootPanel();
							showNewDeclarationPopup(m390HF);
						}

						@Override
						public void onFailure(Throwable caught) {
							aonLayout.showErrorPanel(AON.MSG.unableToInitializeDeclaration(caught.getMessage()));
						}
					});
		}
		
		@Override
		public void showInfoPanel(String htmlText) {
			openFootPanelIfNeeded();
			tabLayout.selectTab(INFORMATION_TAB);
			HTMLPanel panel = new HTMLPanel(htmlText);
			breakdownPanel.setWidget(panel);
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
		public void showError(String msg) {
			aonLayout.showErrorPanel(msg);
		}

		@Override
		public void hideError() {
			aonLayout.hideErrorPanel();
		}
		@Override
		public Model390HFModuleOptions getOptions() {
			return Model390HF.this.getOptions();
		}

		public void showInfoPanelWidget(Widget widget) {
			cleanInfoPanel();
			openFootPanelIfNeeded();
			tabLayout.selectTab(INFORMATION_TAB);
			breakdownPanel.setWidget(widget);
			breakdownPanel.scrollToTop();
		}

	}
	
	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {
			@Override
			public void onSuccess(AonConfiguration config) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model390HFModuleOptions opts = new Model390HFModuleOptions();
				opts.setParentWidget(root);
				opts.setDomainName(getCurrentDomainName());
				opts.setDomain(getCurrentDomain());
				opts.setUser(getCurrentUser());
				opts.setConfiguration(config);
				onModuleLoad( opts );
			}
			
			@Override 
			public void onFailure(Throwable caught) {
				Window.alert( AON.MSG.loadError("Modelo 390HF"));
			}
		});
	}
	private Model390HFModuleOptions getOptions() {
		if (this.options == null) {
			this.options = new Model390HFModuleOptions();
		}
		return this.options;
	}
	
	public void onModuleLoad(Model390HFModuleOptions options) {
		this.options = options;
		AON.ensureInjected();

		aonLayout = new AonLayoutPanel();
		splitLayoutPanel = new SplitLayoutPanel( 2 );
		aonLayout.add(splitLayoutPanel);
		
		declarationContainer = new SimpleLayoutPanel();
		splitLayoutPanel.addSouth(getMinimizePanel(), 30);
		
		splitLayoutPanel.add(declarationContainer);
		
		Model390HFCallback callback = new Model390HFCallback();
		model390HFTable = new Model390HFTable(callback);
		model390HFTable.addSelectionHandler( this::onSelectionChange );
		declarationContainer.setWidget(model390HFTable);
		
		getOptions().getParentWidget().add(aonLayout);
		if (getOptions().getFiscalModelId() != null ) {
			LOGGER.info("Access to Model390HF with a ID: " + getOptions().getFiscalModelId());
			onSelect(getOptions().getFiscalModelId());
		} else if (getOptions().getNewModel() != null ) {
			LOGGER.info("Access to Model390HF new Model");
			newModel(getOptions().getNewModel()); 
		} else {
			model390HFTable.refresh( callback );
		}
	}
	
	private void newModel(Mod390HF newModel) {
		MOD_SERVICE.initialize(getOptions().getOccam(),newModel,
				new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF m390HF) {
						tabLayout.selectTab(INFORMATION_TAB);
						closeFootPanel();
						showNewDeclarationPopup( m390HF );
					}


					@Override
					public void onFailure(Throwable caught) {
						aonLayout.showErrorPanel(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
					}
				});
	}

	private void onSelect(Integer id ) {
		LOGGER.info("OnSelect Model390HF with a ID: " + getOptions().getFiscalModelId());
		MOD_SERVICE.get(getOptions().getOccam(), id , new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF selected) {
						if (selected == null) {
							LOGGER.info("onSuccess Model390HF with a NULL selected Model ID: ");
							aonLayout.showErrorPanel(AON.MSG.unableToFindDeclaration());
						} else {
							LOGGER.info("onSuccess Model390HF with a ID: " + selected.getId());
							select(selected);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						aonLayout.showErrorPanel(AON.MSG.unableToReadDeclaration(caught.getMessage()));
					}
				});
	}

	private void onSelectionChange(SelectionEvent<Mod390HF> event) {
		Mod390HF sel = event.getSelectedItem();
		MOD_SERVICE.get(getOptions().getOccam(),
				sel.getId(), new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF selected) {
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

	private void showNewDeclarationPopup(Mod390HF m390HF) {
		Model390HFNewDeclarationPanel newDeclarationPanel = new Model390HFNewDeclarationPanel( m390HF,
			new Model390HFCallback() {

					@Override
					public void onAccept(Mod390HF mod390HF) {
						final PopupPanel popup = new PopupPanel(false, true);
						popup.add(new AonSplash());
						popup.setGlassEnabled(true);
						popup.setAnimationEnabled(true);
						popup.center();

						// Crear el modelo nuevo
						MOD_SERVICE.create(getOptions().getOccam(),mod390HF,
								new AsyncCallback<Mod390HF>() {
									@Override
									public void onSuccess(Mod390HF m390HF) {
										popup.hide();
										select(m390HF);
									}

									@Override
									public void onFailure(Throwable caught) {
										popup.hide();
										aonLayout.showErrorPanel(AON.MSG.unableToInitializeDeclaration(caught.getMessage()));
									}
								});

					}
					@Override
					public void onCancel(Mod390HF model) {
						if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
							getOptions().getExternalCallback().onExit(model);
						}						
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
	
	enum Mod390HFDeclarations {
		BIZKAIA_2022 {
			@Override
			public boolean accept(Mod390HF mod390HF) {
				return (mod390HF.isBizkaia() && mod390HF.getYear() >= 2022);
			}

			@Override
			public Widget getDeclarationWidget(Mod390HF mod390HF, Model390HFCallback cbk) {
				return new Model390HF2022BIZKAIA(cbk,mod390HF);
			}
		},

		BIZKAIA_2017 {
			@Override
			public boolean accept(Mod390HF mod390HF) {
				return (mod390HF.isBizkaia() && mod390HF.getYear() < 2022);
			}

			@Override
			public Widget getDeclarationWidget(Mod390HF mod390HF, Model390HFCallback cbk) {
				return new Model390HF2017BIZKAIA(cbk,mod390HF);
			}
		},

		ARABA_2022 {
			@Override
			public boolean accept(Mod390HF mod390HF) {
				return (mod390HF.isAraba() && mod390HF.getYear() >= 2022);
			}

			@Override
			public Widget getDeclarationWidget(Mod390HF mod390HF, Model390HFCallback cbk) {
				return new Model390HF2022ARABA(cbk,mod390HF);
			}
		},

		ARABA_2021 {
			@Override
			public boolean accept(Mod390HF mod390HF) {
				return (mod390HF.isAraba() && mod390HF.getYear() == 2021);
			}

			@Override
			public Widget getDeclarationWidget(Mod390HF mod390HF, Model390HFCallback cbk) {
				return new Model390HF2021ARABA(cbk,mod390HF);
			}
		},
		ARABA_2017 {
			@Override
			public boolean accept(Mod390HF mod390HF) {
				return (mod390HF.isAraba() && mod390HF.getYear() >= 2017 && mod390HF.getYear() < 2021 );
			}

			@Override
			public Widget getDeclarationWidget(Mod390HF mod390HF, Model390HFCallback cbk) {
				return new Model390HF2017ARABA(cbk,mod390HF);
			}
		},
		GIPUZKOA_2022 {
			@Override
			public boolean accept(Mod390HF mod390HF) {
				return (mod390HF.isGipuzkoa() && mod390HF.getYear() >= 2022);
			}

			@Override
			public Widget getDeclarationWidget(Mod390HF mod390HF, Model390HFCallback cbk) {
				return new Model390HF2022GIPUZKOA(cbk,mod390HF);
			}
		},
		GIPUZKOA_2021 {
			@Override
			public boolean accept(Mod390HF mod390HF) {
				return (mod390HF.isGipuzkoa() && mod390HF.getYear() == 2021);
			}

			@Override
			public Widget getDeclarationWidget(Mod390HF mod390HF, Model390HFCallback cbk) {
				return new Model390HF2021GIPUZKOA(cbk,mod390HF);
			}
		},
		GIPUZKOA_2017 {
			@Override
			public boolean accept(Mod390HF mod390HF) {
				return (mod390HF.isGipuzkoa() && mod390HF.getYear() >= 2017 && mod390HF.getYear() < 2021);
			}

			@Override
			public Widget getDeclarationWidget(Mod390HF mod390HF, Model390HFCallback cbk) {
				return new Model390HF2017GIPUZKOA(cbk,mod390HF);
			}
		},
		;
		public abstract boolean accept(Mod390HF mod390HF);
		public abstract Widget getDeclarationWidget(Mod390HF mod390HF, Model390HFCallback cbk);
	}
	
	private void select(Mod390HF selected) {
		aonLayout.hideErrorPanel();
		Widget declaration = null;
		for (Mod390HFDeclarations dec : Mod390HFDeclarations.values()) {
			if (dec.accept(selected)) {
				declaration = dec.getDeclarationWidget(selected, new Model390HFCallback());
			}
		}
		if (declaration != null) {
			declarationContainer.setWidget( declaration );		
		} else {
			aonLayout.showErrorPanel("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}
	
	public static void run() {
		GWT.runAsync(Model390HF.class, new RunAsyncCallback() {

			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("Modelo 390HF"));
			}

			@Override
			public void onSuccess() {
				Model390HF model390HF = new Model390HF();
				model390HF.onModuleLoad();
			}
			
		});
	}
}

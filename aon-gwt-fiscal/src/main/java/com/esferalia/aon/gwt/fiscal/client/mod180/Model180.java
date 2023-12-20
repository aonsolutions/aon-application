package com.esferalia.aon.gwt.fiscal.client.mod180;

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
import com.esferalia.aon.gwt.fiscal.client.model.AonJSFiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
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

public class Model180 extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Model180.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static final int INFORMATION_TAB = 0;
	
	static final Model180ServiceAsync SERVICE;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
		
		Model180ServiceAsync serviceRaw = GWT.create(Model180Service.class);
		SERVICE = new Model180ServiceAsyncDecorator(serviceRaw);
	}
	
	protected class Model180Callback implements IFiscalModelCallback<Mod180,Model180ModuleOptions> {

		@Override
		public Model180ModuleOptions getOptions() {
			return Model180.this.options;
		}
		
		@Override
		public void onAccept(Mod180 mod180) {
			// 
		}
		
		@Override
		public void onRemove(Mod180 model) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onExit(model);
			} else {
				onCancel(model);
			}
		}
		
		@Override
		public void onCancel(Mod180 model) {
			cleanErrorMessage();
			declarationContainer.setWidget(model180Table);
			model180Table.refresh( new Model180Callback() );
			closeFootPanel();
		}
		
		@Override
		public void onNew() {
			newModel(getOptions(), AonJSFiscalModelUtils.guessModelYear());
		}
		
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

		public void onSelect(Mod180 mod180, Integer selectedIndex) {
			select(getOptions(), mod180, selectedIndex);
		}
		public void onReset(Model180ModuleOptions options, Mod180 mod180) {
			cleanErrorMessage();
			SERVICE.initialize(options.getOccam(), mod180.getYear(),
					new AsyncCallback<Mod180>() {
						@Override
						public void onSuccess(Mod180 newMod180) {
							cleanAndClose();
							newMod180.setAdministration(mod180.getAdministration());
							newMod180.setYear(mod180.getYear());
							newMod180.setComplementary(mod180.isComplementary());
							newMod180.setReplacement(mod180.isReplacement());
							newMod180.setReplacedReceipt(mod180.getReplacedReceipt());						
							showResetDeclarationPopup(options, newMod180, mod180);
						}

						@Override
						public void onFailure(Throwable caught) {
							showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						}
					});
		}
		
		public void onDuplicate(Model180ModuleOptions options, int id) {
			cleanErrorMessage();
			SERVICE.get(options.getOccam(), id,
					new AsyncCallback<Mod180>() {
						@Override
						public void onSuccess(Mod180 m180) {
							cleanAndClose();
							showDuplicateDeclarationPopup(options, m180);
						}

						@Override
						public void onFailure(Throwable caught) {
							showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						}
					});
		}
		
		private void showResetDeclarationPopup(Model180ModuleOptions options, Mod180 newMod180, Mod180 oldMod180) {
			Model180NewDeclarationPopup newDialog = new Model180NewDeclarationPopup( newMod180, false, true,
				new Model180Callback() {

						@Override
						public void onAccept(Mod180 model) {
							final PopupPanel popup = new PopupPanel(false, true);
							popup.add( new AonSplash() );
							popup.setGlassEnabled(true);
							popup.setAnimationEnabled(true);
							popup.center();
							
							SERVICE.delete(options.getOccam(), oldMod180, 
									new AsyncCallback<Void>() {
										
										@Override
										public void onSuccess(Void result) {
											SERVICE.save(options.getOccam(),model,
													new AsyncCallback<Mod180>() {
														@Override
														public void onSuccess(Mod180 model) {
															popup.hide();
															select(options,model, null);
														}

														@Override
														public void onFailure(Throwable caught) {
															popup.hide();
															showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
														}
													});
										}
										
										@Override
										public void onFailure(Throwable caught) {
											popup.hide();
											showErrorMessage(AON.MSG.unableToDeleteDeclaration(caught.getMessage()));
										}
									});
						}
						
					}
				); 
				newDialog.center();
				newDialog.show();
		}
		
		private void showDuplicateDeclarationPopup(Model180ModuleOptions options, Mod180 model) {
			Model180NewDeclarationPopup newDialog = new Model180NewDeclarationPopup(model, true, false, 
				new Model180Callback() {

						@Override
						public void onAccept(Mod180 model) {
							final PopupPanel popup = new PopupPanel(false, true);
							popup.add(new AonSplash());
							popup.setGlassEnabled(true);
							popup.setAnimationEnabled(true);
							popup.center();

							SERVICE.duplicate(options.getOccam(), model,
									new AsyncCallback<Mod180>() {
										@Override
										public void onSuccess(Mod180 model) {
											popup.hide();
											select(options, model, null);
										}

										@Override
										public void onFailure(Throwable caught) {
											popup.hide();
											showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
										}
									});
						}
						
					}
				); 
				newDialog.center();
				newDialog.show();
		}
	}

	private Model180ModuleOptions options;
	
	private AonLayoutPanel aonLayout;
	private SplitLayoutPanel splitLayoutPanel;
	private SimpleLayoutPanel declarationContainer;
	
	private Model180Table model180Table;
	private TabLayoutPanel tabLayout;
	private AonMinimizePanel footPanel;
	private ScrollPanel breakdownPanel;
	
	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {
			
			@Override
			public void onSuccess(AonConfiguration aonConfiguration) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model180ModuleOptions opts = new Model180ModuleOptions();
				opts.setParentWidget(root);
				opts.setDomainName(getCurrentDomainName());
				opts.setDomain(getCurrentDomain());
				opts.setUser(getCurrentUser());
				opts.setConfiguration(aonConfiguration);
				onModuleLoad( opts );
			}
			
			@Override public void onFailure(Throwable caught) {
				Window.alert( "Error al cargar el module" );
			}
		});
	}
	
	public void onModuleLoad(Model180ModuleOptions options) {
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
		
		model180Table = new Model180Table(new Model180Callback());
		model180Table.addSelectionHandler(event -> onSelectionChange(options,event));
		
		declarationContainer.setWidget(model180Table);

		options.getParentWidget().add(aonLayout);
		if (options.getFiscalModelId() != null ) {
			onSelect(options,options.getFiscalModelId());
		} else if (options.getNewModel() != null ) {
			newModel(options, options.getNewModel().getYear()); 
		} else {
			model180Table.refresh( new Model180Callback());
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

	private void cleanAndClose() {
		cleanBreakdownPanel();
		tabLayout.selectTab(INFORMATION_TAB);
		closeFootPanel();
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


	private void onSelect(Model180ModuleOptions options, Integer id ) {
		LOGGER.info("OnSelect Model180 with a ID: " + options.getFiscalModelId());
		SERVICE.get(options.getOccam(), id , new AsyncCallback<Mod180>() {
			@Override
			public void onSuccess(Mod180 selected) {
				if (selected == null) {
					showErrorMessage(AON.MSG.unableToFindDeclaration());
				} else {
					select(options, selected, null);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
			}
		});
	}

	private void onSelectionChange(Model180ModuleOptions options, SelectionEvent<Mod180> event) {
		Mod180 sel = event.getSelectedItem();
		SERVICE.get(options.getOccam(), sel.getId(), new AsyncCallback<Mod180>() {
					@Override
					public void onSuccess(Mod180 selected) {
						if (selected == null) {
							showErrorMessage(AON.MSG.unableToFindDeclaration());
						} else {
							select(options, selected, null);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
					}
				} );
	}
	

	private void select(Model180ModuleOptions options, Mod180 selected, Integer selectedIndex) {
		cleanErrorMessage();
//		if ( selected.isAEAT() && selected.getYear() >= 2020 ) {
		if ( selected.isAEAT() ) {
			declarationContainer.setWidget( new Model180AEAT(new Model180Callback(),selected,selectedIndex));
		} else if ( selected.isAraba() ) {
			declarationContainer.setWidget( new Model180ARABA(new Model180Callback(),selected,selectedIndex));			
		} else if ( selected.isBizkaia() ) {
			declarationContainer.setWidget( new Model180BIZKAIA(new Model180Callback(),selected,selectedIndex));			
		} else if ( selected.isGipuzkoa() ) {
			declarationContainer.setWidget( new Model180GIPUZKOA(new Model180Callback(),selected,selectedIndex));			
		} else if ( selected.isNavarra() ) {
			declarationContainer.setWidget( new Model180NAVARRA(new Model180Callback(),selected,selectedIndex));			
		} else {
			showErrorMessage("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}
	
	private void newModel(Model180ModuleOptions options, int year) {
		cleanErrorMessage();
		SERVICE.initialize(options.getOccam(), year,
				new AsyncCallback<Mod180>() {
					@Override
					public void onSuccess(Mod180 m180) {
						cleanAndClose();
						showNewDeclarationPopup(options,m180);
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
					}
				});
	}

	private void cleanErrorMessage() {
		aonLayout.hideErrorPanel();
	}
	
	private void showErrorMessage(String msg) {
		aonLayout.showErrorPanel(msg);
	}
	
	private void cleanBreakdownPanel() {
		Widget w = breakdownPanel.getWidget();
		if (w != null) {
			breakdownPanel.remove( breakdownPanel.getWidget() ); 
		}
	}
	
	private void showNewDeclarationPopup(Model180ModuleOptions options, Mod180 model) {
		Model180NewDeclarationPopup newDialog = new Model180NewDeclarationPopup( model,
			new Model180Callback() {

					@Override
					public void onAccept(Mod180 model) {
						final PopupPanel popup = new PopupPanel(false, true);
						popup.add(new AonSplash());
						popup.setGlassEnabled(true);
						popup.setAnimationEnabled(true);
						popup.center();

						SERVICE.save(options.getOccam(),model,
								new AsyncCallback<Mod180>() {
									@Override
									public void onSuccess(Mod180 model) {
										popup.hide();
										select(options,model, null);
									}

									@Override
									public void onFailure(Throwable caught) {
										popup.hide();
										showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
									}
								});
					}
					
					@Override
					public void onCancel(Mod180 model) {
						if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
							getOptions().getExternalCallback().onExit(model);
						}						
					}
					
				}
			); 
			newDialog.center();
			newDialog.show();
	}

	public static void run() {
		GWT.runAsync(Model180.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("Modelo 180"));
			}
			
			@Override
			public void onSuccess() {
				Model180 model180 = new Model180();
				model180.onModuleLoad();
			}
		});
	}
	
}

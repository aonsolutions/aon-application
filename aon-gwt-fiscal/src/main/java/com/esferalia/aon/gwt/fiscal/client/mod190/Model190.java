package com.esferalia.aon.gwt.fiscal.client.mod190;

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
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
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

public class Model190 extends MainEntryPoint {
	private static final Logger LOGGER = Logger.getLogger(Model190.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	private static final int INFORMATION_TAB = 0;
	
	static final Model190ServiceAsync SERVICE;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
		
		Model190ServiceAsync serviceRaw = GWT.create(Model190Service.class);
		SERVICE = new Model190ServiceAsyncDecorator(serviceRaw);
	}
	
	public class Model190Callback implements IFiscalModelCallback<Mod190,Model190ModuleOptions> {

		@Override
		public Model190ModuleOptions getOptions() {
			return Model190.this.options;
		}

		@Override
		public void onAccept(Mod190 mod190) {
			// Nothing 
		}

		@Override
		public void onRemove(Mod190 model) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onRemove(model);
			} else {
				onCancel(model);
			}
		}

		@Override
		public void onCancel(Mod190 model) {
			cleanErrorMessage();
			declarationContainer.setWidget(model190Table);
			model190Table.refresh( new Model190Callback() );
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
			showInfoPanel( new HTMLPanel(htmlText) );
		}
		
		public void showInfoPanel(Widget widget) {
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
		
		// ----------------------------------------------------
		// ----------------------------------------------------
		public void onSelect(Mod190 mod190, Integer selectedIndex) {
			select(getOptions(),mod190, selectedIndex);
		}

		public void onReset(Model190ModuleOptions options, Mod190 mod190) {
			cleanErrorMessage();
			SERVICE.initialize(options.getOccam(), mod190.getYear(),
					new AsyncCallback<Mod190>() {
						@Override
						public void onSuccess(Mod190 newMod190) {
							cleanAndClose();
							newMod190.setAdministration(mod190.getAdministration());
							newMod190.setYear(mod190.getYear());
							newMod190.setComplementary(mod190.isComplementary());
							newMod190.setReplacement(mod190.isReplacement());
							newMod190.setReplacedReceipt(mod190.getReplacedReceipt());						
							showResetDeclarationPopup(options, newMod190, mod190);
						}

						@Override
						public void onFailure(Throwable caught) {
							showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						}
					});
		}
		
		public void onDuplicate(Model190ModuleOptions options, int id) {
			cleanErrorMessage();
			SERVICE.getMod190(options.getOccam(), id,
					new AsyncCallback<Mod190>() {
						@Override
						public void onSuccess(Mod190 m190) {
							cleanAndClose();
							showDuplicateDeclarationPopup(options, m190);
						}

						@Override
						public void onFailure(Throwable caught) {
							showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						}
					});
		}
		
		private void showResetDeclarationPopup(Model190ModuleOptions options, Mod190 newMod190, Mod190 oldMod190) {
			Model190NewDeclarationPopup newDialog = new Model190NewDeclarationPopup( newMod190, false, true,
				new Model190Callback() {

						@Override
						public void onAccept(Mod190 model) {
							final PopupPanel popup = new PopupPanel(false, true);
							popup.add( new AonSplash() );
							popup.setGlassEnabled(true);
							popup.setAnimationEnabled(true);
							popup.center();
							
							SERVICE.delete(options.getOccam(), oldMod190, 
									new AsyncCallback<Void>() {
										
										@Override
										public void onSuccess(Void result) {
											SERVICE.save(options.getOccam(),model,
													new AsyncCallback<Mod190>() {
														@Override
														public void onSuccess(Mod190 model) {
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
		
		private void showDuplicateDeclarationPopup(Model190ModuleOptions options, Mod190 model) {
			Model190NewDeclarationPopup newDialog = new Model190NewDeclarationPopup(model, true, false, 
				new Model190Callback() {

						@Override
						public void onAccept(Mod190 model) {
							final PopupPanel popup = new PopupPanel(false, true);
							popup.add(new AonSplash());
							popup.setGlassEnabled(true);
							popup.setAnimationEnabled(true);
							popup.center();

							SERVICE.duplicate(options.getOccam(), model,
									new AsyncCallback<Mod190>() {
										@Override
										public void onSuccess(Mod190 model) {
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
	
	private Model190ModuleOptions options;
	
	private AonLayoutPanel aonLayout;
	private SplitLayoutPanel splitLayoutPanel;
	private SimpleLayoutPanel declarationContainer;
	
	private Model190Table model190Table;
	private TabLayoutPanel tabLayout;
	private AonMinimizePanel footPanel;
	private ScrollPanel breakdownPanel;	
	
	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {
			
			@Override
			public void onSuccess(AonConfiguration config) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model190ModuleOptions opts = new Model190ModuleOptions();
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
	
	public void onModuleLoad(Model190ModuleOptions options) {
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
		
		model190Table = new Model190Table(new Model190Callback());
		model190Table.addSelectionHandler(event -> onSelectionChange(options,event));
		
		declarationContainer.setWidget(model190Table);

		options.getParentWidget().add(aonLayout);
		if (options.getFiscalModelId() != null ) {
			onSelect(options,options.getFiscalModelId());
		} else if (options.getNewModel() != null ) {
			newModel(options, options.getNewModel().getYear()); 
		} else {
			model190Table.refresh( new Model190Callback());
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

	private void onSelect(Model190ModuleOptions options, Integer id ) {
		LOGGER.info("OnSelect Model190 with a ID: " + options.getFiscalModelId());
		SERVICE.getMod190(options.getOccam(), id , new AsyncCallback<Mod190>() {
			@Override
			public void onSuccess(Mod190 selected) {
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

	private void onSelectionChange(Model190ModuleOptions options,SelectionEvent<Mod190> event) {
		Mod190 sel = event.getSelectedItem();
		SERVICE.getMod190(options.getOccam(), sel.getId(), new AsyncCallback<Mod190>() {
					@Override
					public void onSuccess(Mod190 selected) {
						if (selected == null) {
							showErrorMessage(AON.MSG.unableToFindDeclaration());
						} else {
							select(options,selected, null);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
					}
				} );
	}
	

	private void select(Model190ModuleOptions options,Mod190 selected, Integer selectedIndex) {
		cleanErrorMessage();
		if ( selected.isAEAT() ) {
			declarationContainer.setWidget( new Model190AEAT(new Model190Callback(),selected,selectedIndex));
		} else if ( selected.isAraba() ) {
			declarationContainer.setWidget( new Model190ARABA(new Model190Callback(), selected,selectedIndex));			
		} else if ( selected.isBizkaia() ) {
			declarationContainer.setWidget( new Model190BIZKAIA(new Model190Callback(),selected, selectedIndex));			
		} else if ( selected.isGipuzkoa() ) {
			declarationContainer.setWidget( new Model190GIPUZKOA(new Model190Callback(),selected, selectedIndex));			
		} else if ( selected.isNavarra() ) {
			declarationContainer.setWidget( new Model190NAVARRA(new Model190Callback(),selected,selectedIndex));			
		} else {
			showErrorMessage("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}

	private void newModel(Model190ModuleOptions options, int year) {
		cleanErrorMessage();
		SERVICE.initialize(options.getOccam(), year,
				new AsyncCallback<Mod190>() {
					@Override
					public void onSuccess(Mod190 m190) {
						cleanAndClose();
						showNewDeclarationPopup(options,m190);
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
	
	private void showNewDeclarationPopup(Model190ModuleOptions options, Mod190 model) {
		Model190NewDeclarationPopup newDialog = new Model190NewDeclarationPopup( model,
			new Model190Callback() {

					@Override
					public void onAccept(Mod190 model) {
						final PopupPanel popup = new PopupPanel(false, true);
						popup.add(new AonSplash());
						popup.setGlassEnabled(true);
						popup.setAnimationEnabled(true);
						popup.center();

						SERVICE.save(options.getOccam(),model,
								new AsyncCallback<Mod190>() {
									@Override
									public void onSuccess(Mod190 model) {
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
					public void onCancel(Mod190 model) {
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
		GWT.runAsync(Model190.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("Modelo 190"));
			}
			
			@Override
			public void onSuccess() {
				Model190 model190 = new Model190();
				model190.onModuleLoad();
			}
		});
	}

}

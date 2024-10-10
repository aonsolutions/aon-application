package com.esferalia.aon.gwt.fiscal.client.mod184;

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
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
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

public class Model184 extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Model184.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static final int INFORMATION_TAB = 0;
	
	static final Model184ServiceAsync SERVICE;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
		
		Model184ServiceAsync serviceRaw = GWT.create(Model184Service.class);
		SERVICE = new Model184ServiceAsyncDecorator(serviceRaw);
	}
	
	protected class Model184Callback implements IFiscalModelCallback<Mod184,Model184ModuleOptions> {

		@Override
		public Model184ModuleOptions getOptions() {
			return Model184.this.options;
		}
		
		@Override
		public void onAccept(Mod184 mod184) {
			// 
		}
		
		@Override
		public void onRemove(Mod184 model) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onRemove(model);
			} else {
				onCancel(model);
			}
		}
		
		@Override
		public void onCancel(Mod184 model) {
			cleanErrorMessage();
			declarationContainer.setWidget(model184Table);
			model184Table.refresh( new Model184Callback() );
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
		public Integer getSelecttedTab() {
			return Model184.this.getSelectedTab();
		}
		public void setSelectedTab(Integer tabIndex) {
			Model184.this.setSelectedTab(tabIndex);
		}
		
		public void onSelect(Mod184 mod184, Integer selectedIncomeIndex, Integer selectedPartnerIndex) {
			select(mod184, selectedIncomeIndex, selectedPartnerIndex);
		}
		
		public void onReset(Model184ModuleOptions options, Mod184 mod184) {
			cleanErrorMessage();
			SERVICE.initialize(options.getOccam(), mod184.getYear(),
					new AsyncCallback<Mod184>() {
						@Override
						public void onSuccess(Mod184 newMod184) {
							cleanAndClose();
							newMod184.setAdministration(mod184.getAdministration());
							newMod184.setYear(mod184.getYear());
							newMod184.setComplementary(mod184.isComplementary());
							newMod184.setReplacement(mod184.isReplacement());
							newMod184.setReplacedReceipt(mod184.getReplacedReceipt());						
							showResetDeclarationPopup(options, newMod184, mod184);
						}

						@Override
						public void onFailure(Throwable caught) {
							showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						}
					});
		}
		
		public void onDuplicate(Model184ModuleOptions options, int id) {
			cleanErrorMessage();
			SERVICE.get(options.getOccam(), id,
					new AsyncCallback<Mod184>() {
						@Override
						public void onSuccess(Mod184 m184) {
							cleanAndClose();
							showDuplicateDeclarationPopup(options, m184);
						}

						@Override
						public void onFailure(Throwable caught) {
							showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						}
					});
		}
		
		private void showResetDeclarationPopup(Model184ModuleOptions options, Mod184 newMod184, Mod184 oldMod184) {
			Model184NewDeclarationPopup newDialog = new Model184NewDeclarationPopup( newMod184, false, true,
				new Model184Callback() {

						@Override
						public void onAccept(Mod184 model) {
							final PopupPanel popup = new PopupPanel(false, true);
							popup.add( new AonSplash() );
							popup.setGlassEnabled(true);
							popup.setAnimationEnabled(true);
							popup.center();
							
							SERVICE.delete(options.getOccam(), oldMod184, 
									new AsyncCallback<Void>() {
										
										@Override
										public void onSuccess(Void result) {
											SERVICE.save(options.getOccam(),model,
													new AsyncCallback<Mod184>() {
														@Override
														public void onSuccess(Mod184 model) {
															popup.hide();
															select(model, null, null);
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
		
		private void showDuplicateDeclarationPopup(Model184ModuleOptions options, Mod184 model) {
			Model184NewDeclarationPopup newDialog = new Model184NewDeclarationPopup(model, true, false, 
				new Model184Callback() {

						@Override
						public void onAccept(Mod184 model) {
							final PopupPanel popup = new PopupPanel(false, true);
							popup.add(new AonSplash());
							popup.setGlassEnabled(true);
							popup.setAnimationEnabled(true);
							popup.center();

							SERVICE.duplicate(options.getOccam(), model,
									new AsyncCallback<Mod184>() {
										@Override
										public void onSuccess(Mod184 model) {
											popup.hide();
											select(model, null, null);
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

	private Model184ModuleOptions options;
	
	private AonLayoutPanel aonLayout;
	private SplitLayoutPanel splitLayoutPanel;
	private SimpleLayoutPanel declarationContainer;
	
	private Model184Table model184Table;
	private TabLayoutPanel tabLayout;
	private AonMinimizePanel footPanel;
	private ScrollPanel breakdownPanel;
	private Integer selectedTab;
	
	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {
			
			@Override
			public void onSuccess(AonConfiguration config) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model184ModuleOptions opts = new Model184ModuleOptions();
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
	
	public void onModuleLoad(Model184ModuleOptions options) {
		this.options = options;
		AON.ensureInjected();
		aonLayout = new AonLayoutPanel();
		aonLayout.addStyleName("aaon-Model");
		splitLayoutPanel = new SplitLayoutPanel( 2 );
		aonLayout.add(splitLayoutPanel);
		
		declarationContainer = new SimpleLayoutPanel();
		declarationContainer.addStyleName("aon-Model-Detail");
		AonMinimizePanel minimizePanel = getMinimizePanel();
		minimizePanel.addStyleName("aon-Model-Info");
		splitLayoutPanel.addSouth(minimizePanel, 30);

		splitLayoutPanel.add(declarationContainer);
		
		model184Table = new Model184Table(new Model184Callback());
		model184Table.addSelectionHandler(event -> onSelectionChange(options,event));
		
		declarationContainer.setWidget(model184Table);

		options.getParentWidget().add(aonLayout);
		if (options.getFiscalModelId() != null ) {
			onSelect(options,options.getFiscalModelId());
		} else if (options.getNewModel() != null ) {
			newModel(options, options.getNewModel().getYear()); 
		} else {
			model184Table.refresh( new Model184Callback());
		}

	}
	
	protected Integer getSelectedTab() {
		return selectedTab;
	}
	protected void setSelectedTab(Integer selectedTab) {
		this.selectedTab = selectedTab;
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
	
	private void onSelect(Model184ModuleOptions options, Integer id ) {
		LOGGER.info("OnSelect Model184 with a ID: " + options.getFiscalModelId());
		SERVICE.get(options.getOccam(), id , new AsyncCallback<Mod184>() {
			@Override
			public void onSuccess(Mod184 selected) {
				if (selected == null) {
					showErrorMessage(AON.MSG.unableToFindDeclaration());
				} else {
					select(selected, null, null);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
			}
		});
	}
	
	private void onSelectionChange(Model184ModuleOptions options, SelectionEvent<Mod184> event) {
		Mod184 sel = event.getSelectedItem();
		SERVICE.get(options.getOccam(), sel.getId(), new AsyncCallback<Mod184>() {
			@Override
			public void onSuccess(Mod184 selected) {
				if (selected == null) {
					showErrorMessage(AON.MSG.unableToFindDeclaration());
				} else {
					select(selected, null, null);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
			}
		} );
	}
	
	private void select(Mod184 selected, Integer selectedIncomeIndex, Integer selectedPartnerIndex) {
		cleanErrorMessage();
		if ( selected.isAEAT() ) {
			declarationContainer.setWidget( new Model184AEAT(new Model184Callback(),selected,selectedIncomeIndex,selectedPartnerIndex));
		} else if ( selected.isAraba() ) {
			declarationContainer.setWidget( new Model184ARABA(new Model184Callback(),selected,selectedIncomeIndex,selectedPartnerIndex));
		} else if ( selected.isBizkaia() ) {
			declarationContainer.setWidget( new Model184BIZKAIA(new Model184Callback(),selected,selectedIncomeIndex,selectedPartnerIndex));
		} else if ( selected.isGipuzkoa() ) {
			declarationContainer.setWidget( new Model184GIPUZKOA(new Model184Callback(),selected,selectedIncomeIndex,selectedPartnerIndex));
		} else if ( selected.isNavarra() ) {
			declarationContainer.setWidget( new Model184NAVARRA(new Model184Callback(),selected,selectedIncomeIndex,selectedPartnerIndex));
		} else {
			showErrorMessage("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}
	
	private void newModel(Model184ModuleOptions options, int year) {
		cleanErrorMessage();
		SERVICE.initialize(options.getOccam(), year,
				new AsyncCallback<Mod184>() {
					@Override
					public void onSuccess(Mod184 m184) {
						cleanAndClose();
						showNewDeclarationPopup(options,m184);
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
	
	private void showNewDeclarationPopup(Model184ModuleOptions options, Mod184 model) {
		Model184NewDeclarationPopup newDialog = new Model184NewDeclarationPopup( model,
			new Model184Callback() {

					@Override
					public void onAccept(Mod184 model) {
						final PopupPanel popup = new PopupPanel(false, true);
						popup.add(new AonSplash());
						popup.setGlassEnabled(true);
						popup.setAnimationEnabled(true);
						popup.center();

						SERVICE.save(options.getOccam(),model,
								new AsyncCallback<Mod184>() {
									@Override
									public void onSuccess(Mod184 model) {
										popup.hide();
										select(model, null, null);
									}

									@Override
									public void onFailure(Throwable caught) {
										popup.hide();
										showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
									}
								});
					}
					
					@Override
					public void onCancel(Mod184 model) {
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
		GWT.runAsync(Model184.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("Modelo 184"));
			}
			
			@Override
			public void onSuccess() {
				Model184 model184 = new Model184();
				model184.onModuleLoad();
			}
		});
	}
}

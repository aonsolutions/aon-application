package com.esferalia.aon.gwt.fiscal.client.mod369;

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
import com.esferalia.aon.occam.api.model.fiscal.Mod369;
import com.esferalia.aon.occam.api.model.type.Period;
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

public class Model369 extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Model369.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static final int INFORMATION_TAB = 0;
	
	static final Model369ServiceAsync SERVICE;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
		
		Model369ServiceAsync serviceRaw = GWT.create(Model369Service.class);
		SERVICE = new Model369ServiceAsyncDecorator(serviceRaw);
	}
	
	protected class Model369Callback implements IFiscalModelCallback<Mod369,Model369ModuleOptions> {

		@Override
		public Model369ModuleOptions getOptions() {
			return Model369.this.options;
		}
		
		@Override
		public void onAccept(Mod369 mod369) {
			// 
		}
		
		@Override
		public void onRemove(Mod369 model) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onRemove(model);
			} else {
				onCancel(model);
			}
		}
		
		@Override
		public void onCancel(Mod369 model) {
			cleanErrorMessage();
			declarationContainer.setWidget(model369Table);
			model369Table.refresh( new Model369Callback() );
			closeFootPanel();
		}

		@Override
		public void onNew() {
			newModel(getOptions(), 0, null);
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
		public Integer getSelectedTab() {
			return Model369.this.getSelectedTab();
		}
		public void setSelectedTab(Integer tabIndex) {
			Model369.this.setSelectedTab(tabIndex);
		}
		
		public void onSelect(Mod369 mod369) {
			select(mod369);
		}		
		
		public void onReset(Model369ModuleOptions options, Mod369 mod369) {
			cleanErrorMessage();
			SERVICE.initialize(options.getOccam(), mod369.getYear(), mod369.getPeriod(), 
					new AsyncCallback<Mod369>() {
						@Override
						public void onSuccess(Mod369 newMod369) {
							cleanAndClose();
							newMod369.setAdministration(mod369.getAdministration());
							newMod369.setYear(mod369.getYear());
							newMod369.setPeriod(mod369.getPeriod());
							newMod369.setRegime(mod369.getRegime());
							showResetDeclarationPopup(options, newMod369, mod369);
						}

						@Override
						public void onFailure(Throwable caught) {
							showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						}
					});
		}
		
		public void onDuplicate(Model369ModuleOptions options, int id) {
			cleanErrorMessage();
			SERVICE.get(options.getOccam(), id,
					new AsyncCallback<Mod369>() {
						@Override
						public void onSuccess(Mod369 m369) {
							cleanAndClose();
							showDuplicateDeclarationPopup(options, m369);
						}

						@Override
						public void onFailure(Throwable caught) {
							showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						}
					});
		}
		
		private void showResetDeclarationPopup(Model369ModuleOptions options, Mod369 newMod369, Mod369 oldMod369) {
			Model369NewDeclarationPopup newDialog = new Model369NewDeclarationPopup( newMod369, false, true,
				new Model369Callback() {

						@Override
						public void onAccept(Mod369 model) {
							final PopupPanel popup = new PopupPanel(false, true);
							popup.add( new AonSplash() );
							popup.setGlassEnabled(true);
							popup.setAnimationEnabled(true);
							popup.center();
							
							SERVICE.delete(options.getOccam(), oldMod369, 
									new AsyncCallback<Void>() {
										
										@Override
										public void onSuccess(Void result) {
											SERVICE.save(options.getOccam(),model,
													new AsyncCallback<Mod369>() {
														@Override
														public void onSuccess(Mod369 model) {
															popup.hide();
															select(model);
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
		
		private void showDuplicateDeclarationPopup(Model369ModuleOptions options, Mod369 model) {
			Model369NewDeclarationPopup newDialog = new Model369NewDeclarationPopup(model, true, false, 
				new Model369Callback() {

						@Override
						public void onAccept(Mod369 model) {
							final PopupPanel popup = new PopupPanel(false, true);
							popup.add(new AonSplash());
							popup.setGlassEnabled(true);
							popup.setAnimationEnabled(true);
							popup.center();

							SERVICE.duplicate(options.getOccam(), model,
									new AsyncCallback<Mod369>() {
										@Override
										public void onSuccess(Mod369 model) {
											popup.hide();
											select(model);
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

	private Model369ModuleOptions options;
	
	private AonLayoutPanel aonLayout;
	private SplitLayoutPanel splitLayoutPanel;
	private SimpleLayoutPanel declarationContainer;
	
	private Model369Table model369Table;
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
				Model369ModuleOptions opts = new Model369ModuleOptions();
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
	
	public void onModuleLoad(Model369ModuleOptions options) {
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
		
		model369Table = new Model369Table(new Model369Callback());
		model369Table.addSelectionHandler(event -> onSelectionChange(options,event));
		
		declarationContainer.setWidget(model369Table);

		options.getParentWidget().add(aonLayout);
		if (options.getFiscalModelId() != null ) {			
			onSelect(options,options.getFiscalModelId());
		} else if (options.getNewModel() != null ) {			
			newModel(options, options.getNewModel().getYear(), options.getNewModel().getPeriod()); 
		} else {			
			model369Table.refresh( new Model369Callback());
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
	
	private void onSelect(Model369ModuleOptions options, Integer id ) {
		LOGGER.info("OnSelect Model369 with a ID: " + options.getFiscalModelId());
		SERVICE.get(options.getOccam(), id , new AsyncCallback<Mod369>() {
			@Override
			public void onSuccess(Mod369 selected) {
				if (selected == null) {
					showErrorMessage(AON.MSG.unableToFindDeclaration());
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
	
	private void onSelectionChange(Model369ModuleOptions options, SelectionEvent<Mod369> event) {
		Mod369 sel = event.getSelectedItem();
		SERVICE.get(options.getOccam(), sel.getId(), new AsyncCallback<Mod369>() {
			@Override
			public void onSuccess(Mod369 selected) {
				if (selected == null) {
					showErrorMessage(AON.MSG.unableToFindDeclaration());
				} else {
					select(selected);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
			}
		} );
	}
	
	private void select(Mod369 selected) {
		cleanErrorMessage();
		// POR AHORA SOLO AEAT
		declarationContainer.setWidget(new Model369AEAT(new Model369Callback(), selected));
//		if ( selected.isAEAT() ) {
//			declarationContainer.setWidget( new Model369AEAT(new Model369Callback(),selected,selectedIncomeIndex,selectedPartnerIndex));
//		} else if ( selected.isAraba() ) {
//			declarationContainer.setWidget( new Model369ARABA(new Model369Callback(),selected,selectedIncomeIndex,selectedPartnerIndex));
//		} else if ( selected.isBizkaia() ) {
//			declarationContainer.setWidget( new Model369BIZKAIA(new Model369Callback(),selected,selectedIncomeIndex,selectedPartnerIndex));
//		} else if ( selected.isGipuzkoa() ) {
//			declarationContainer.setWidget( new Model369GIPUZKOA(new Model369Callback(),selected,selectedIncomeIndex,selectedPartnerIndex));
//		} else if ( selected.isNavarra() ) {
//			declarationContainer.setWidget( new Model369NAVARRA(new Model369Callback(),selected,selectedIncomeIndex,selectedPartnerIndex));
//		} else {
//			showErrorMessage("Administraci\u00F3n y/o ejercicio no soportado.");
//		}
	}
	
	private void newModel(Model369ModuleOptions options, int year, Period period) {
		cleanErrorMessage();
		SERVICE.initialize(options.getOccam(), year, period,
				new AsyncCallback<Mod369>() {
					@Override
					public void onSuccess(Mod369 m369) {
						cleanAndClose();
						showNewDeclarationPopup(options,m369);
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
	
	private void showNewDeclarationPopup(Model369ModuleOptions options, Mod369 model) {
		Model369NewDeclarationPopup newDialog = new Model369NewDeclarationPopup( model,
			new Model369Callback() {

					@Override
					public void onAccept(Mod369 model) {
						final PopupPanel popup = new PopupPanel(false, true);
						popup.add(new AonSplash());
						popup.setGlassEnabled(true);
						popup.setAnimationEnabled(true);
						popup.center();

						SERVICE.save(options.getOccam(),model,
								new AsyncCallback<Mod369>() {
									@Override
									public void onSuccess(Mod369 model) {
										popup.hide();
										select(model);
									}

									@Override
									public void onFailure(Throwable caught) {
										popup.hide();
										showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
									}
								});
					}
					
					@Override
					public void onCancel(Mod369 model) {
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
		GWT.runAsync(Model369.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("Modelo 369"));
			}
			
			@Override
			public void onSuccess() {
				Model369 model369 = new Model369();
				model369.onModuleLoad();
			}
		});
	}
}

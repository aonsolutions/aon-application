package com.esferalia.aon.gwt.fiscal.client.mod193;

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
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
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

public class Model193 extends MainEntryPoint {
	private static final Logger LOGGER = Logger.getLogger(Model193.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static final int INFORMATION_TAB = 0;
	
	static final Model193ServiceAsync SERVICE;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
		
		Model193ServiceAsync serviceRaw = GWT.create(Model193Service.class);
		SERVICE = new Model193ServiceAsyncDecorator(serviceRaw);
	}
	
	protected class Model193Callback implements IFiscalModelCallback<Mod193,Model193ModuleOptions> {
		
		@Override
		public Model193ModuleOptions getOptions() {
			return Model193.this.options;
		}

		@Override
		public void onAccept(Mod193 mod193) {
			// Nothing 
		}

		@Override
		public void onRemove(Mod193 model) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onExit(model);
			} else {
				onCancel(model);
			}
		}

		@Override
		public void onCancel(Mod193 model) {
			cleanErrorMessage();
			declarationContainer.setWidget(model193Table);
			model193Table.refresh( new Model193Callback() );
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
		
		// ----------------------------------------------------
		// ----------------------------------------------------
		public void onSelect(Mod193 mod193, Integer selectedIndex) {
			select(mod193, selectedIndex);
		}
		
		public void onReset(Model193ModuleOptions options, Mod193 mod193) {
			cleanErrorMessage();
			SERVICE.initialize(options.getOccam(), mod193.getYear(),
					new AsyncCallback<Mod193>() {
						@Override
						public void onSuccess(Mod193 newMod193) {
							cleanAndClose();
							newMod193.setAdministration(mod193.getAdministration());
							newMod193.setYear(mod193.getYear());
							newMod193.setComplementary(mod193.isComplementary());
							newMod193.setReplacement(mod193.isReplacement());
							newMod193.setReplacedReceipt(mod193.getReplacedReceipt());						
							showResetDeclarationPopup(options, newMod193, mod193);
						}

						@Override
						public void onFailure(Throwable caught) {
							showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						}
					});
		}
		
		public void onDuplicate(Model193ModuleOptions options, int id) {
			cleanErrorMessage();
			SERVICE.get(options.getOccam(), id,
					new AsyncCallback<Mod193>() {
						@Override
						public void onSuccess(Mod193 m193) {
							cleanAndClose();
							showDuplicateDeclarationPopup(options, m193);
						}

						@Override
						public void onFailure(Throwable caught) {
							showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						}
					});
		}
		
		private void showResetDeclarationPopup(Model193ModuleOptions options, Mod193 newMod193, Mod193 oldMod193) {
			Model193NewDeclarationPopup newDialog = new Model193NewDeclarationPopup( newMod193, false, true,
				new Model193Callback() {

						@Override
						public void onAccept(Mod193 model) {
							final PopupPanel popup = new PopupPanel(false, true);
							popup.add( new AonSplash() );
							popup.setGlassEnabled(true);
							popup.setAnimationEnabled(true);
							popup.center();
							
							SERVICE.delete(options.getOccam(), oldMod193, 
									new AsyncCallback<Void>() {
										
										@Override
										public void onSuccess(Void result) {
											SERVICE.save(options.getOccam(),model,
													new AsyncCallback<Mod193>() {
														@Override
														public void onSuccess(Mod193 model) {
															popup.hide();
															select(model, null);
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
		
		private void showDuplicateDeclarationPopup(Model193ModuleOptions options, Mod193 model) {
			Model193NewDeclarationPopup newDialog = new Model193NewDeclarationPopup(model, true, false, 
				new Model193Callback() {

						@Override
						public void onAccept(Mod193 model) {
							final PopupPanel popup = new PopupPanel(false, true);
							popup.add(new AonSplash());
							popup.setGlassEnabled(true);
							popup.setAnimationEnabled(true);
							popup.center();

							SERVICE.duplicate(options.getOccam(), model,
									new AsyncCallback<Mod193>() {
										@Override
										public void onSuccess(Mod193 model) {
											popup.hide();
											select(model, null);
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

	private Model193ModuleOptions options;
	
	private AonLayoutPanel aonLayout;
	private SplitLayoutPanel splitLayoutPanel;
	private SimpleLayoutPanel declarationContainer;
	
	private Model193Table model193Table;
	private TabLayoutPanel tabLayout;
	private AonMinimizePanel footPanel;
	private ScrollPanel breakdownPanel;	
	
	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {
			
			@Override
			public void onSuccess(AonConfiguration config) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model193ModuleOptions opts = new Model193ModuleOptions();
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
	
	public void onModuleLoad(Model193ModuleOptions options) {
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
		
		model193Table = new Model193Table(new Model193Callback());
		model193Table.addSelectionHandler(event -> onSelectionChange(options,event));
		
		declarationContainer.setWidget(model193Table);

		options.getParentWidget().add(aonLayout);
		if (options.getFiscalModelId() != null ) {
			onSelect(options,options.getFiscalModelId());
		} else if (options.getNewModel() != null ) {
			newModel(options, options.getNewModel().getYear()); 
		} else {
			model193Table.refresh( new Model193Callback() );
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

	private void onSelect(Model193ModuleOptions options, Integer id ) {
		LOGGER.info("OnSelect Model193 with a ID: " + options.getFiscalModelId());
		SERVICE.get(options.getOccam(), id , new AsyncCallback<Mod193>() {
			@Override
			public void onSuccess(Mod193 selected) {
				if (selected == null) {
					showErrorMessage(AON.MSG.unableToFindDeclaration());
				} else {
					select(selected, null);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
			}
		});
	}

	private void onSelectionChange(Model193ModuleOptions options,SelectionEvent<Mod193> event) {
		Mod193 sel = event.getSelectedItem();
		SERVICE.get(options.getOccam(),sel.getId(), new AsyncCallback<Mod193>() {
			@Override
			public void onSuccess(Mod193 selected) {
				if (selected == null) {
					showErrorMessage(AON.MSG.unableToFindDeclaration());
				} else {
					select(selected, null);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
			}
		} );
	}
	
	private void select(Mod193 selected, Integer selectedIndex) {
		cleanErrorMessage();
		if ( selected.getYear() > 2014 ) {
			declarationContainer.setWidget( new Model193AEAT(new Model193Callback(),selected,selectedIndex));
		} else {
			showErrorMessage(getCurrentDomainName());
		}
	}

	private void newModel(Model193ModuleOptions options, int year) {
		cleanErrorMessage();
		SERVICE.initialize(options.getOccam(), year,
				new AsyncCallback<Mod193>() {
					@Override
					public void onSuccess(Mod193 m193) {
						cleanAndClose();
						showNewDeclarationPopup(options,m193);
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
	
	private void showNewDeclarationPopup(Model193ModuleOptions options, Mod193 model) {
		Model193NewDeclarationPopup newDialog = new Model193NewDeclarationPopup( model,
			new Model193Callback() {

					@Override
					public void onAccept(Mod193 model) {
						final PopupPanel popup = new PopupPanel(false, true);
						popup.add(new AonSplash());
						popup.setGlassEnabled(true);
						popup.setAnimationEnabled(true);
						popup.center();

						SERVICE.save(options.getOccam(),model,
								new AsyncCallback<Mod193>() {
									@Override
									public void onSuccess(Mod193 model) {
										popup.hide();
										select(model, null);
									}

									@Override
									public void onFailure(Throwable caught) {
										popup.hide();
										showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
									}
								});
					}
					
					@Override
					public void onCancel(Mod193 model) {
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
		GWT.runAsync(Model193.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("Modelo 193"));
			}
			
			@Override
			public void onSuccess() {
				Model193 model193 = new Model193();
				model193.onModuleLoad();
			}
		});
	}
}

package com.esferalia.aon.gwt.fiscal.client.mod349;

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
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
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

public class Model349 extends MainEntryPoint {
	
	private static final Logger LOGGER = Logger.getLogger(Model349.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static final int INFORMATION_TAB = 0;
	
	static final Model349ServiceAsync SERVICE;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
		
		Model349ServiceAsync serviceRaw = GWT.create(Model349Service.class);
		SERVICE = new Model349ServiceAsyncDecorator(serviceRaw);
	}
	
	public class Model349Callback implements IFiscalModelCallback<Mod349,Model349ModuleOptions> {
		
		@Override
		public Model349ModuleOptions getOptions() {
			return Model349.this.options;
		}

		@Override
		public void onAccept(Mod349 mod349) {
			// 
		}
		
		@Override
		public void onRemove(Mod349 model) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onRemove(model);
			} else {
				onCancel(model);
			}
		}
		
		@Override
		public void onCancel(Mod349 mod349) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onExit(mod349);
			} else {
				cleanErrorMessage();
				declarationContainer.setWidget(model349Table);
				model349Table.refresh( new Model349Callback() );
				closeFootPanel();
			}
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
		
		public void cleanAndCloseInfoPanel() {
			Widget w = breakdownPanel.getWidget();
			if (w != null) {
				breakdownPanel.remove( breakdownPanel.getWidget() ); 
			}
			closeFootPanel();
		}

		public void onSelect(Mod349 mod349, Integer selectedIndex) {
			select(mod349, selectedIndex);
		}
		// ----------------------------------------------------
		public void onDuplicate(Model349ModuleOptions options, int id) {
			cleanErrorMessage();
			SERVICE.get(options.getOccam(), id, new AsyncCallback<Mod349>() {
				@Override
				public void onSuccess(Mod349 m349) {
					cleanAndClose();
					showDuplicateDeclarationPopup(options, m349);
				}

				@Override
				public void onFailure(Throwable caught) {
					showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
				}
			});
		}

		private void showDuplicateDeclarationPopup(Model349ModuleOptions options, Mod349 model) {
			Model349NewDeclarationPopup newDeclarationPanel = new Model349NewDeclarationPopup(model, new Model349Callback() {

						@Override
						public void onAccept(Mod349 model) {
							final PopupPanel popup = new PopupPanel(false, true);
							popup.add(new AonSplash());
							popup.setGlassEnabled(true);
							popup.setAnimationEnabled(true);
							popup.center();

							SERVICE.duplicate(options.getOccam(), model,
									new AsyncCallback<Mod349>() {
										@Override
										public void onSuccess(Mod349 model) {
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
				newDeclarationPanel.setCaption(AON.MSG.duplicate());
				declarationContainer.setWidget(newDeclarationPanel);
				model349Table.refresh( new Model349Callback() );
				tabLayout.selectTab(INFORMATION_TAB);
				closeFootPanel();
		}
	}
	
	private Model349ModuleOptions options;
	
	private AonLayoutPanel aonLayout;
	private SplitLayoutPanel splitLayoutPanel;
	private SimpleLayoutPanel declarationContainer;
	
	private Model349Table model349Table;
	private TabLayoutPanel tabLayout;
	private AonMinimizePanel footPanel;
	private ScrollPanel breakdownPanel;	
	
	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {
			
			@Override
			public void onSuccess(AonConfiguration config) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model349ModuleOptions opts = new Model349ModuleOptions();
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
	
	public void onModuleLoad(Model349ModuleOptions options) {
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
		
		model349Table = new Model349Table(new Model349Callback());
		model349Table.addSelectionHandler(event -> onSelectionChange(options,event));
		
		declarationContainer.setWidget(model349Table);

		options.getParentWidget().add(aonLayout);
		if (options.getFiscalModelId() != null ) {
			onSelect(options,options.getFiscalModelId());
		} else if (options.getNewModel() != null ) {
//			newModel(options);
			newModel(options, options.getNewModel().getYear(), options.getNewModel().getPeriod());
		} else {
			model349Table.refresh( new Model349Callback() );
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
	
	private void onSelect(Model349ModuleOptions options, Integer id ) {
		LOGGER.info("OnSelect Model349 with a ID: " + options.getFiscalModelId());
		SERVICE.get(options.getOccam(), id , new AsyncCallback<Mod349>() {
			@Override
			public void onSuccess(Mod349 selected) {
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

	private void onSelectionChange(Model349ModuleOptions options, SelectionEvent<Mod349> event) {
		Mod349 sel = event.getSelectedItem();
		onSelect(options, sel.getId());
	}

	private void select(Mod349 selected, Integer selectedIndex) {
		cleanErrorMessage();
		if ( selected.isAEAT() ) {
			declarationContainer.setWidget( new Model349AEAT(new Model349Callback(),selected,selectedIndex));
		} else if ( selected.isAraba() ) {
			declarationContainer.setWidget( new Model349ARABA(new Model349Callback(),selected,selectedIndex));			
		} else if ( selected.isBizkaia() ) {
			declarationContainer.setWidget( new Model349BIZKAIA(new Model349Callback(),selected,selectedIndex));			
		} else if ( selected.isGipuzkoa() ) {
			declarationContainer.setWidget( new Model349GIPUZKOA(new Model349Callback(),selected,selectedIndex));			
		} else if ( selected.isNavarra() ) {
			declarationContainer.setWidget( new Model349NAVARRA(new Model349Callback(),selected,selectedIndex));			
		} else {
			showErrorMessage("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}

	private void newModel(Model349ModuleOptions options, int year, Period period) {
		cleanErrorMessage();
		SERVICE.initialize(options.getOccam(), year, period, new AsyncCallback<Mod349>() {
			@Override
			public void onSuccess(Mod349 m349) {
				cleanAndClose();
				showNewDeclarationPopup(options,m349);
			}

			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
			}
		});
	}

	private void showNewDeclarationPopup(Model349ModuleOptions options, Mod349 model) {
		Model349NewDeclarationPopup newDeclarationPanel = new Model349NewDeclarationPopup( model, new Model349Callback() {

				@Override
				public void onAccept(Mod349 model) {
					final PopupPanel popup = new PopupPanel(false, true);
					popup.add( new AonSplash());
					popup.setGlassEnabled(true);
					popup.setAnimationEnabled(true);
					popup.center();

					SERVICE.save(options.getOccam(),model, new AsyncCallback<Mod349>() {
						@Override
						public void onSuccess(Mod349 model) {
							popup.hide();
							select(model,null);
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
		declarationContainer.setWidget(newDeclarationPanel);
		tabLayout.selectTab(INFORMATION_TAB);
		closeFootPanel();
	}
	
	public static void run() {
		GWT.runAsync(Model349.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("Modelo 349"));
			}
			
			@Override
			public void onSuccess() {
				Model349 model349 = new Model349();
				model349.onModuleLoad();
			}
		});
	}
}

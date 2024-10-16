package com.esferalia.aon.gwt.fiscal.client.mod347;

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
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
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

public class Model347 extends MainEntryPoint {
	private static final Logger LOGGER = Logger.getLogger(Model347.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static final int INFORMATION_TAB = 0;
	
	static final Model347ServiceAsync SERVICE;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
		
		Model347ServiceAsync serviceRaw = GWT.create(Model347Service.class);
		SERVICE = new Model347ServiceAsyncDecorator(serviceRaw);
	}
	
	protected class Model347Callback implements IFiscalModelCallback<Mod347,Model347ModuleOptions> {
		

		
		@Override
		public Model347ModuleOptions getOptions() {
			return Model347.this.options;
		}

		@Override
		public void onAccept(Mod347 mod347) {
			// 
		}

		@Override
		public void onRemove(Mod347 model) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onRemove(model);
			} else {
				onCancel(model);
			}
		}
		
		@Override
		public void onCancel(Mod347 mod347) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onExit(mod347);
			} else {
				cleanErrorMessage();
				declarationContainer.setWidget(model347Table);
				model347Table.refresh( new Model347Callback() );
				closeFootPanel();
			}
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

		public void onSelect(Mod347 mod347, Integer selectedIndexDeclared,  Integer selectedIndexAsset, int tabPanelIndex) {
			select(mod347, selectedIndexDeclared, selectedIndexAsset, tabPanelIndex);
		}

		// ***********************************************************
		// ***********************************************************
		
		public void onDuplicate(Model347ModuleOptions options, int id) {
			cleanErrorMessage();
			SERVICE.get(options.getOccam(), id, new AsyncCallback<Mod347>() {
				@Override
				public void onSuccess(Mod347 m347) {
					cleanAndClose();
					m347.setYear(m347.getYear() + 1);
					showDuplicateDeclarationPopup(options, m347);
				}

				@Override
				public void onFailure(Throwable caught) {
					showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
				}
			});
		}

		private void showDuplicateDeclarationPopup(Model347ModuleOptions options, Mod347 model) {
			Model347NewDeclarationPopup newDeclarationPanel = new Model347NewDeclarationPopup(model,true, new Model347Callback() {

					@Override
					public void onAccept(Mod347 model) {
						final PopupPanel popup = new PopupPanel(false, true);
						popup.add(new AonSplash());
						popup.setGlassEnabled(true);
						popup.setAnimationEnabled(true);
						popup.center();

						SERVICE.duplicate(options.getOccam(), model,
								new AsyncCallback<Mod347>() {
									@Override
									public void onSuccess(Mod347 model) {
										popup.hide();
										select(model, null, null, 0);
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
			model347Table.refresh( new Model347Callback() );
			tabLayout.selectTab(INFORMATION_TAB);
			closeFootPanel();
		}
	}

	private Model347ModuleOptions options;
	
	private AonLayoutPanel aonLayout;
	private SplitLayoutPanel splitLayoutPanel;
	private SimpleLayoutPanel declarationContainer;
	
	private Model347Table model347Table;
	private TabLayoutPanel tabLayout;
	private AonMinimizePanel footPanel;
	private ScrollPanel breakdownPanel;	

	
	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {
			
			@Override
			public void onSuccess(AonConfiguration config) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model347ModuleOptions opts = new Model347ModuleOptions();
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
	
	public void onModuleLoad(Model347ModuleOptions options) {
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
		
		model347Table = new Model347Table(new Model347Callback());
		model347Table.addSelectionHandler(event -> onSelectionChange(options,event));
		
		declarationContainer.setWidget(model347Table);

		options.getParentWidget().add(aonLayout);
		if (options.getFiscalModelId() != null ) {
			onSelect(options,options.getFiscalModelId());
		} else if (options.getNewModel() != null ) {
//			newModel(options);
			newModel(options, options.getNewModel().getYear());
		} else {
			model347Table.refresh(new Model347Callback());
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
	
	// **************************************************************
	// **************************************************************
	// **************************************************************
	private void onSelect(Model347ModuleOptions options, Integer id ) {
		LOGGER.info("OnSelect Model347 with a ID: " + options.getFiscalModelId());
		SERVICE.get(options.getOccam(), id , new AsyncCallback<Mod347>() {
			@Override
			public void onSuccess(Mod347 selected) {
				if (selected == null) {
					showErrorMessage(AON.MSG.unableToFindDeclaration());
				} else {
					select(selected, null, null, 0);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
			}
		});
	}

	private void onSelectionChange(Model347ModuleOptions options,SelectionEvent<Mod347> event) {
		Mod347 sel = event.getSelectedItem();
		onSelect(options, sel.getId());
	}
	

	private void select(Mod347 selected, Integer selectedIndexDeclared, Integer selectedIndexAsset, int tabPanelIndex) {
		cleanErrorMessage();
		if ( selected.isAEAT() ) {
			declarationContainer.setWidget( new Model347AEAT(new Model347Callback(), selected,selectedIndexDeclared,selectedIndexAsset,tabPanelIndex));
		} else if ( selected.isAraba() ) {
			declarationContainer.setWidget( new Model347ARABA(new Model347Callback(),selected,selectedIndexDeclared,selectedIndexAsset,tabPanelIndex));			
		} else if ( selected.isBizkaia() ) {
			declarationContainer.setWidget( new Model347BIZKAIA(new Model347Callback(),selected,selectedIndexDeclared,selectedIndexAsset,tabPanelIndex));			
		} else if ( selected.isGipuzkoa() ) {
			declarationContainer.setWidget( new Model347GIPUZKOA(new Model347Callback(),selected,selectedIndexDeclared,selectedIndexAsset,tabPanelIndex));			
		} else if ( selected.isNavarra() ) {
			declarationContainer.setWidget( new Model347NAVARRA(new Model347Callback(),selected,selectedIndexDeclared,selectedIndexAsset,tabPanelIndex));			
		} else {
			showErrorMessage("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}

	private void newModel(Model347ModuleOptions options, int year) {
		cleanErrorMessage();
		SERVICE.initialize(options.getOccam(), year, new AsyncCallback<Mod347>() {
			@Override
			public void onSuccess(Mod347 m347) {
				cleanAndClose();
				showNewDeclarationPopup(options,m347);
			}

			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
			}
		});
	}
	
	private void showNewDeclarationPopup(Model347ModuleOptions options, Mod347 model) {
		Model347NewDeclarationPopup newDeclarationPanel = new Model347NewDeclarationPopup( model, false, new Model347Callback() {
				@Override
				public void onAccept(Mod347 model) {
					final PopupPanel popup = new PopupPanel(false, true);
					popup.add(new AonSplash());
					popup.setGlassEnabled(true);
					popup.setAnimationEnabled(true);
					popup.center();

					SERVICE.save(options.getOccam(),model,new AsyncCallback<Mod347>() {
						@Override
						public void onSuccess(Mod347 model) {
							popup.hide();
							select(model, null, null, 0);
						}

						@Override
						public void onFailure(Throwable caught) {
							popup.hide();
							showErrorMessage(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
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
		GWT.runAsync(Model347.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("Modelo 347"));
			}
			
			@Override
			public void onSuccess() {
				Model347 model347 = new Model347();
				model347.onModuleLoad();
			}
		});
	}
}

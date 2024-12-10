package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2018.Model3902018;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2021.Model3902021;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2022.Model3902022;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2023.Model3902023;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2024.Model3902024;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model390 extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Model390.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static final int INFORMATION_TAB = 0;

	public static final Model390ServiceAsync MOD390_SERVICE;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
		
		Model390ServiceAsync serviceRaw = GWT.create(Model390Service.class);
		MOD390_SERVICE = new Model390ServiceAsyncDecorator(serviceRaw);
	}
	
	public class Model390Callback implements IFiscalModelCallback<Mod390,Model390ModuleOptions> {
		@Override
		public Model390ModuleOptions getOptions() {
			return Model390.this.options; 
		}
		@Override
		public void onAccept(Mod390 mod390) {
			// REDEFINE
		}
		@Override
		public void onCancel(Mod390 mod390) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onExit(mod390);
			} else {
				cleanErrorMessage();
				cleanAndClose();
				declarationContainer.setWidget(model390Table);
				model390Table.refresh( new Model390Callback());
				tabLayout.selectTab(INFORMATION_TAB);
				closeFootPanel();
			}
		}
		@Override
		public void onNew() {
			onNew( 0 );
		}
		@Override
		public void onRemove(Mod390 model) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onRemove(model);
			} else {
				onCancel(model);
			}
		}
		@Override
		public void showError(String msg) {
			Model390.this.showErrorMessage(msg);
		}
		@Override
		public void hideError() {
			aonLayout.hideErrorPanel();
		}
		@Override
		public void showInfoPanel(String text) {
			openFootPanelIfNeeded();
			tabLayout.selectTab(INFORMATION_TAB);
			HTMLPanel panel = new HTMLPanel(text);
			breakdownPanel.setWidget(panel);
			breakdownPanel.scrollToTop();
		}
		@Override
		public void cleanInfoPanel() {
			Model390.this.cleanInfoPanel();
		}
		
		// ********************************************
		// ********************************************
		// ********************************************
		
		public void onNew(int year) {
			Model390.this.onNew( year );
		}
		public void onReset(Model390ModuleOptions options, Mod390 mod390) {
			cleanErrorMessage();
			MOD390_SERVICE.initialize(options.getOccam(), mod390.getYear(), new AsyncCallback<Mod390>() {
				@Override
				public void onSuccess(Mod390 newMod390) {
					cleanAndClose();
					tabLayout.selectTab(INFORMATION_TAB);
					closeFootPanel();
					// Valores de la declaración actual
					newMod390.setAdministration(mod390.getAdministration());
					newMod390.setYear(mod390.getYear());
					newMod390.setComplementary(mod390.isComplementary());
					newMod390.setReplacement(mod390.isReplacement());
					newMod390.setReplacedReceipt(mod390.getReplacedReceipt());						
					showResetDeclarationPopup(options, newMod390, mod390);
				}

				@Override
				public void onFailure(Throwable caught) {
					showErrorMessage(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
				}
			});
		}
		
		private void showResetDeclarationPopup(Model390ModuleOptions options, Mod390 newMod390, Mod390 oldMod390) {
			cleanErrorMessage();
			cleanAndClose();
			tabLayout.selectTab(INFORMATION_TAB);
			closeFootPanel();
			Model390NewDeclarationPopup newDialog = new Model390NewDeclarationPopup( newMod390, true, new Model390Callback() {

						@Override
						public void onAccept(Mod390 mod390) {
						
							final PopupPanel popup = new PopupPanel(false, true);
							Label label = new Label(AON.MSG.processing());
							label.addStyleName(AON.AON_CSS.aonTimer());
							popup.add(label);
							popup.setGlassEnabled(true);
							popup.setAnimationEnabled(true);
							popup.center();

							MOD390_SERVICE.delete(options.getOccam(), oldMod390, new AsyncCallback<Void>() {
								@Override
								public void onSuccess(Void result) {
									popup.hide();
									select(options, mod390);
								}

								@Override
								public void onFailure(Throwable caught) {
									popup.hide();
									showErrorMessage(AON.MSG.unableToDeleteDeclaration(caught.getMessage()));
								}
							});				
						}
						@Override
						public void onCancel(Mod390 mod390) {
							// Nothing
						}
					}
				); 
				newDialog.center();
				newDialog.show();
		}
		public void cleanErrorPanel() {
			Model390.this.cleanErrorMessage();
		}
		
		public void reload(Integer id) {
			onSelect(getOptions(), id);
		}
	}
	
	private Model390ModuleOptions options;
	
	private AonLayoutPanel aonLayout;
	private SplitLayoutPanel splitLayoutPanel;
	private SimpleLayoutPanel declarationContainer;
	
	private Model390Table model390Table;
	private TabLayoutPanel tabLayout;
	private AonMinimizePanel footPanel;
	private ScrollPanel breakdownPanel;	
	
	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {
			
			@Override
			public void onSuccess(AonConfiguration config) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model390ModuleOptions opts = new Model390ModuleOptions();
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
	
	public void onModuleLoad(Model390ModuleOptions options) {
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
		
		model390Table = new Model390Table(new Model390Callback());
		model390Table.addSelectionHandler(event -> onSelectionChange(options,event));
		
		declarationContainer.setWidget(model390Table);

		options.getParentWidget().add(aonLayout);
		if (options.getFiscalModelId() != null ) {
			onSelect(options,options.getFiscalModelId());
		} else if (options.getNewModel() != null ) {
			onNew(options.getNewModel().getYear());
		} else {
			model390Table.refresh( new Model390Callback());
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
		cleanInfoPanel();
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
	
	private void cleanInfoPanel() {
		Widget w = breakdownPanel.getWidget();
		if (w != null) {
			breakdownPanel.remove( breakdownPanel.getWidget() ); 
		}
	}

	private void onSelect(Model390ModuleOptions options, Integer id ) {
		LOGGER.info("OnSelect Model390 with a ID: " + options.getFiscalModelId());
		MOD390_SERVICE.getMod390(options.getOccam(), id , new AsyncCallback<Mod390>() {
			@Override
			public void onSuccess(Mod390 selected) {
				if (selected == null) {
					showErrorMessage(AON.MSG.unableToFindDeclaration());
				} else {
					select(options, selected);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
			}
		});
	}

	private void onSelectionChange(Model390ModuleOptions options, SelectionEvent<Mod390> event) {
		Mod390 sel = event.getSelectedItem();
		if (sel.getStatus() ==  FiscalStatus.BLOCKED) {
			showErrorMessage("El visor de la declaraci\u00F3n ya no está disponible");
		} else {
			select(options,sel);
		}
	}
	
	private void select(Model390ModuleOptions options, Mod390 selected) {
		cleanErrorMessage();
		if (selected.isAEAT()) {
			if (selected.getYear() >= 2024) {
				declarationContainer.setWidget(new Model3902024(new Model390Callback(),selected));
			} else if (selected.getYear() == 2023) {
				declarationContainer.setWidget(new Model3902023(new Model390Callback(),selected));
			} else if (selected.getYear() == 2022) {
				declarationContainer.setWidget(new Model3902022(new Model390Callback(),selected));
			}  else if (selected.getYear() == 2021) {
				declarationContainer.setWidget(new Model3902021(new Model390Callback(),selected));
			}  else if (selected.getYear() == 2018 || selected.getYear() == 2019 || selected.getYear() == 2020) {
				declarationContainer.setWidget(new Model3902018(new Model390Callback(),selected));
			} else if (selected.getYear() == 2015 || selected.getYear() == 2016 || selected.getYear() == 2017) {
				declarationContainer.setWidget(new Model3902015(new Model390Callback(),selected));
			} else {
				showErrorMessage("Administraci\u00F3n y/o ejercicio no soportado.");
			}
		} else {
			showErrorMessage("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}
	
	private void onNew(int year) {
		cleanErrorMessage();
		MOD390_SERVICE.initialize(options.getOccam(),year, new AsyncCallback<Mod390>() {
			@Override
			public void onSuccess(Mod390 m390) {
				cleanAndClose();
				tabLayout.selectTab(INFORMATION_TAB);
				closeFootPanel();
				showNewDeclarationPopup(m390);
			}

			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
			}
		});
	}
	
	private void showNewDeclarationPopup(Mod390 m390) {
		cleanErrorMessage();
		cleanAndClose();
		tabLayout.selectTab(INFORMATION_TAB);
		closeFootPanel();
		Model390NewDeclarationPopup newDialog = new Model390NewDeclarationPopup( m390, new Model390Callback() {

					@Override
					public void onAccept(Mod390 mod390) {
						select(options,m390);
					}
				}
			); 
			newDialog.center();
			newDialog.show();
	}
	
	public static void run() {
		GWT.runAsync(Model390.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("Modelo 390"));
			}
			
			@Override
			public void onSuccess() {
				Model390 model390 = new Model390();
				model390.onModuleLoad();
			}
		});
	}
}

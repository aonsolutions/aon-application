package com.esferalia.aon.gwt.fiscal.client.mod303;

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
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
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

public class Model303 extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Model303.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static final int INFORMATION_TAB = 0;

	protected static Mod303ServiceAsync service;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		Mod303ServiceAsync mod303ServiceRaw = GWT.create(Mod303Service.class);
		service = new Mod303ServiceAsyncDecorator(mod303ServiceRaw);
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
	}
	
	private AonLayoutPanel aonLayout;
	private SplitLayoutPanel splitLayoutPanel;
	private SimpleLayoutPanel declarationContainer;
	private AonTabLayoutPanel tabLayout;
	private AonMinimizePanel footPanel;
	private ScrollPanel breakdownPanel;
	private Model303ModuleOptions options;
	private  Model303Table model303Table;

	protected class Model303Callback implements IFiscalModelCallback<Mod303, Model303ModuleOptions> {

		@Override
		public void onAccept(Mod303 mod303) {
			// REDEFINE
		}
		
		@Override
		public void onRemove(Mod303 mod303) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onRemove(mod303);
			} else {
				onCancel(mod303);
			}
		}
		
		@Override
		public void onCancel(Mod303 mod303) {
			aonLayout.hideErrorPanel();
			cleanInfoPanel();
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onExit(mod303);
			} else {
				declarationContainer.setWidget(model303Table);
				model303Table.refresh( new Model303Callback() );
				tabLayout.selectTab(INFORMATION_TAB);
				closeFootPanel();
			}
		}
		
		@Override
		public void onNew() {
			aonLayout.hideErrorPanel();
			service.initialize(getOptions().getOccam(),null,
					new AsyncCallback<Mod303>() {
						@Override
						public void onSuccess(Mod303 m303) {
							tabLayout.selectTab(INFORMATION_TAB);
							closeFootPanel();
							showNewDeclarationPanel(m303);
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
		public Model303ModuleOptions getOptions() {
			return Model303.this.getOptions();
		}

	}
	
	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {
			@Override
			public void onSuccess(AonConfiguration config) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model303ModuleOptions opts = new Model303ModuleOptions();
				opts.setParentWidget(root);
				opts.setDomainName(getCurrentDomainName());
				opts.setDomain(getCurrentDomain());
				opts.setUser(getCurrentUser());
				opts.setConfiguration(config);
				onModuleLoad( opts );
			}
			
			@Override 
			public void onFailure(Throwable caught) {
				Window.alert( AON.MSG.loadError("Modelo 303"));
			}
		});
	}
	private Model303ModuleOptions getOptions() {
		if (this.options == null) {
			this.options = new Model303ModuleOptions();
		}
		return this.options;
	}
	
	public void onModuleLoad(Model303ModuleOptions options) {
		this.options = options;
		AON.ensureInjected();

		aonLayout = new AonLayoutPanel();
		splitLayoutPanel = new SplitLayoutPanel( 2 );
		aonLayout.add(splitLayoutPanel);
		
		declarationContainer = new SimpleLayoutPanel();
		splitLayoutPanel.addSouth(getMinimizePanel(), 30);
		
		splitLayoutPanel.add(declarationContainer);
		
		Model303Callback callback = new Model303Callback();
		model303Table = new Model303Table(callback);
		model303Table.addSelectionHandler( this::onSelectionChange );
		declarationContainer.setWidget(model303Table);
		
		getOptions().getParentWidget().add(aonLayout);
		if (getOptions().getFiscalModelId() != null ) {
			LOGGER.info("Access to Model303 with a ID: " + getOptions().getFiscalModelId());
			onSelect(getOptions().getFiscalModelId());
		} else if (getOptions().getNewModel() != null ) {
			LOGGER.info("Access to Model303 new Model");
			newModel(getOptions().getNewModel()); 
		} else {
			model303Table.refresh( callback );
		}
	}

	private void newModel(Mod303 newModel) {
		service.initialize(getOptions().getOccam(),newModel,
				new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 m303) {
						tabLayout.selectTab(INFORMATION_TAB);
						closeFootPanel();
						showNewDeclarationPanel( m303 );
					}


					@Override
					public void onFailure(Throwable caught) {
						aonLayout.showErrorPanel(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
					}
				});
	}

	private void onSelect(Integer id ) {
		LOGGER.info("OnSelect Model303 with a ID: " + getOptions().getFiscalModelId());
		service.getMod303(getOptions().getOccam(), id , new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 selected) {
						if (selected == null) {
							LOGGER.info("onSuccess Model303 with a NULL selected Model ID: ");
							aonLayout.showErrorPanel(AON.MSG.unableToFindDeclaration());
						} else {
							LOGGER.info("onSuccess Model303 with a ID: " + selected.getId());
							select(selected);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						aonLayout.showErrorPanel(AON.MSG.unableToReadDeclaration(caught.getMessage()));
					}
				});
	}

	private void onSelectionChange(SelectionEvent<Mod303> event) {
		Mod303 sel = event.getSelectedItem();
		service.getMod303(getOptions().getOccam(),
				sel.getId(), new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 selected) {
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

	private void showNewDeclarationPanel(Mod303 m303) {
		Model303NewDeclarationPanel newDeclarationPanel = new Model303NewDeclarationPanel( m303,
			new Model303Callback() {

					@Override
					public void onAccept(Mod303 mod303) {
						final PopupPanel popup = new PopupPanel(false, true);
						popup.add(new AonSplash());
						popup.setGlassEnabled(true);
						popup.setAnimationEnabled(true);
						popup.center();

						// Crear el modelo nuevo
						service.create(getOptions().getOccam(),mod303,
								new AsyncCallback<Mod303>() {
									@Override
									public void onSuccess(Mod303 m303) {
										popup.hide();
										select(m303);
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

	enum Mod303Declarations {
		AEAT_2023 {
			@Override
			public boolean accept(Mod303 mod303) {
				return (mod303.isAEAT() && mod303.getYear() >= 2023);
			}

			@Override
			public Widget getDeclarationWidget(Mod303 mod303, Model303Callback cbk) {
				return new Model303AEAT2023(mod303,cbk);
			}
		},
		ARABA_2023 {
			@Override
			public boolean accept(Mod303 mod303) {
				return (mod303.isAraba() && mod303.getYear() >= 2023);
			}

			@Override
			public Widget getDeclarationWidget(Mod303 mod303, Model303Callback cbk) {
				return new Model303ARABA2023(mod303,cbk);
			}
		},
		GIPUZKOA_2023 {
			@Override
			public boolean accept(Mod303 mod303) {
				return (mod303.isGipuzkoa() && mod303.getYear() >= 2023);
			}

			@Override
			public Widget getDeclarationWidget(Mod303 mod303, Model303Callback cbk) {
				return new Model303GIPUZKOA2023(mod303,cbk);
			}
		},
		// Ejercicio anteriores
		AEAT_2022 {
			@Override
			public boolean accept(Mod303 mod303) {
				return (mod303.isAEAT() && mod303.getYear() == 2022);
			}

			@Override
			public Widget getDeclarationWidget(Mod303 mod303, Model303Callback cbk) {
				return new Model303AEAT2022(mod303,cbk);
			}
		},
		AEAT_2021_LAST_SEMESTER {
			@Override
			public boolean accept(Mod303 mod303) {
				return (mod303.isAEAT() && mod303.getYear() == 2021 && mod303.getPeriod().isLastSemester());
			}

			@Override
			public Widget getDeclarationWidget(Mod303 mod303, Model303Callback cbk) {
				return new Model303AEAT20212(mod303,cbk);
			}
		},
		AEAT_2021_FIRST_SEMESTER {
			@Override
			public boolean accept(Mod303 mod303) {
				return (mod303.isAEAT() && mod303.getYear() == 2021 && mod303.getPeriod().isFirstSemester());
			}

			@Override
			public Widget getDeclarationWidget(Mod303 mod303, Model303Callback cbk) {
				return new Model303AEAT2021(mod303,cbk);
			}
		},
		AEAT_2020_LAST_PERIOD {
			@Override
			public boolean accept(Mod303 mod303) {
				return (mod303.isAEAT() && mod303.getYear() == 2020 && mod303.isLastPeriod());
			}

			@Override
			public Widget getDeclarationWidget(Mod303 mod303, Model303Callback cbk) {
				return new Model303AEAT2020(mod303,cbk);
			}
		},
		AEAT_2018_2020 {
			@Override
			public boolean accept(Mod303 mod303) {
				return (mod303.isAEAT() && (
					(mod303.getYear() >= 2018 && mod303.getYear() <= 2020) 
				 || (mod303.getYear() == 2020 && !mod303.isLastPeriod())));
			}

			@Override
			public Widget getDeclarationWidget(Mod303 mod303, Model303Callback cbk) {
				return new Model303AEAT2018(mod303,cbk);
			}
		},
		AEAT_2017 {
			@Override
			public boolean accept(Mod303 mod303) {
				return (mod303.isAEAT() && mod303.getYear() < 2017);
			}

			@Override
			public Widget getDeclarationWidget(Mod303 mod303, Model303Callback cbk) {
				return new Model303AEAT2017(mod303,cbk);
			}
		}
		,ARABA_2022 {
			@Override
			public boolean accept(Mod303 mod303) {
				return (mod303.isAraba() && mod303.getYear() == 2022);
			}

			@Override
			public Widget getDeclarationWidget(Mod303 mod303, Model303Callback cbk) {
				return new Model303ARABA2022(mod303,cbk);
			}
		}
		,ARABA_2019 {
			@Override
			public boolean accept(Mod303 mod303) {
				return (mod303.isAraba() && mod303.getYear() >= 2019 && mod303.getYear() < 2022);
			}

			@Override
			public Widget getDeclarationWidget(Mod303 mod303, Model303Callback cbk) {
				return new Model303ARABA2019(mod303,cbk);
			}
		}
		,ARABA_2017 {
			@Override
			public boolean accept(Mod303 mod303) {
				return (mod303.isAraba() && mod303.getYear() < 2019);
			}

			@Override
			public Widget getDeclarationWidget(Mod303 mod303, Model303Callback cbk) {
				return new Model303ARABA2017(mod303,cbk);
			}
		}
		,BIZKAIA_2022 {
			@Override
			public boolean accept(Mod303 mod303) {
				return (mod303.isBizkaia() && mod303.getYear()>2021);
			}

			@Override
			public Widget getDeclarationWidget(Mod303 mod303, Model303Callback cbk) {
				return new Model303BIZKAIA2022(mod303,cbk);
			}
		}
		,BIZKAIA {
			@Override
			public boolean accept(Mod303 mod303) {
				return (mod303.isBizkaia() && mod303.getYear()<2022);
			}

			@Override
			public Widget getDeclarationWidget(Mod303 mod303, Model303Callback cbk) {
				return new Model303BIZKAIA2017(mod303,cbk);
			}
		},
		GIPUZKOA_2022 {
			@Override
			public boolean accept(Mod303 mod303) {
				return (mod303.isGipuzkoa() && mod303.getYear() == 2022);
			}

			@Override
			public Widget getDeclarationWidget(Mod303 mod303, Model303Callback cbk) {
				return new Model303GIPUZKOA2022(mod303,cbk);
			}
		},
		GIPUZKOA_2017 {
			@Override
			public boolean accept(Mod303 mod303) {
				return (mod303.isGipuzkoa() && ((mod303.getYear() < 2021)
					|| (mod303.getYear() == 2021 && mod303.getPeriod().isFirstSemester())));
			}

			@Override
			public Widget getDeclarationWidget(Mod303 mod303, Model303Callback cbk) {
				return new Model303GIPUZKOA2017(mod303,cbk);
			}
		},
		GIPUZKOA_2021_LAST_SEMESTER {
			@Override
			public boolean accept(Mod303 mod303) {
				return (mod303.isGipuzkoa() && mod303.getYear() == 2021 && mod303.getPeriod().isLastSemester());
			}

			@Override
			public Widget getDeclarationWidget(Mod303 mod303, Model303Callback cbk) {
				return new Model303GIPUZKOA2021(mod303,cbk);
			}
		},
		NAVARRA_2022 {
			@Override
			public boolean accept(Mod303 mod303) {
				return (mod303.isNavarra() && mod303.getYear() > 2021);
			}

			@Override
			public Widget getDeclarationWidget(Mod303 mod303, Model303Callback cbk) {
				return new Model303NAVARRA2022(mod303,cbk);
			}
		},
		;
		public abstract boolean accept(Mod303 mod303);
		public abstract Widget getDeclarationWidget(Mod303 mod303, Model303Callback cbk);
	}
	
	private void select(Mod303 selected) {
		aonLayout.hideErrorPanel();
		Widget declaration = null;
		for (Mod303Declarations dec : Mod303Declarations.values()) {
			if (dec.accept(selected)) {
				declaration = dec.getDeclarationWidget(selected, new Model303Callback());
			}
		}
		if (declaration != null) {
			declarationContainer.setWidget( declaration );		
		} else {
			aonLayout.showErrorPanel("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}
	
	public static void run() {
		GWT.runAsync(Model303.class, new RunAsyncCallback() {

			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("Modelo 303"));
			}

			@Override
			public void onSuccess() {
				Model303 model303 = new Model303();
				model303.onModuleLoad();
			}
			
		});
	}
	
}

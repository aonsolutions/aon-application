package com.esferalia.aon.gwt.fiscal.client.mod111;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model111 extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Model111.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static final int INFORMATION_TAB = 0;

	protected static final Mod111ServiceAsync SERVICE;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
		
		Mod111ServiceAsync serviceRaw = GWT.create(Mod111Service.class);
		SERVICE = new Mod111ServiceAsyncDecorator(serviceRaw);
	}
	
	private Model111ModuleOptions options;

	private AonLayoutPanel aonLayout;
	private SplitLayoutPanel splitLayoutPanel;
	private SimpleLayoutPanel declarationContainer;
	private Model111Table model111Table;
	private TabLayoutPanel tabLayout;
	private AonMinimizePanel footPanel;
	private ScrollPanel breakdownPanel;	

	protected class Model111Callback implements IFiscalModelCallback<Mod111,Model111ModuleOptions> {
		
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
			showInfoPanelWidget(new HTMLPanel(htmlText));	
		}
		
		public void showInfoPanelWidget(Widget widget) {
			cleanInfoPanel();
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

		@Override
		public Model111ModuleOptions getOptions() {
			return Model111.this.options;
		}

		@Override
		public void onAccept(Mod111 model) {
			// Nothing
		}

		@Override
		public void onCancel(Mod111 model) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onExit(model);
			} else {
				cleanInfoPanel();
				hideError();
				declarationContainer.setWidget(model111Table);
				model111Table.refresh( new Model111Callback());
				tabLayout.selectTab(INFORMATION_TAB);
				closeFootPanel();
			}
		}

		@Override
		public void onRemove(Mod111 model) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onExit(model);
			} else {
				onCancel(model);
			}
		}

		@Override
		public void onNew() {
			SERVICE.initialize(getOptions().getOccam(),null,
			new AsyncCallback<Mod111>() {
				@Override
				public void onSuccess(Mod111 m111) {
					cleanInfoPanel();
					tabLayout.selectTab(INFORMATION_TAB);
					closeFootPanel();
					showNewDeclarationPanel(m111);
				}


				@Override
				public void onFailure(Throwable caught) {
					showErrorMessage(AON.MSG.unableToInitializeDeclaration(caught.getMessage()));
				}
			});
		}
	}
	
	
	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser()
			, new AsyncCallback<AonConfiguration>() {
			
			@Override
			public void onSuccess(AonConfiguration config) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model111ModuleOptions opts = new Model111ModuleOptions();
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
	private Model111ModuleOptions getOptions() {
		if (this.options == null) {
			this.options = new Model111ModuleOptions();
		}
		return this.options;
	}
	
	public void onModuleLoad(Model111ModuleOptions options) {
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
		
		
		
		model111Table = new Model111Table( new Model111Callback());
		model111Table.addSelectionHandler( this::onSelectionChange );
		declarationContainer.setWidget(model111Table);
		
		getOptions().getParentWidget().add(aonLayout);
		if (getOptions().getFiscalModelId() != null ) {
			onSelect(getOptions().getFiscalModelId());
		} else if (getOptions().getNewModel() != null ) {
			newModel(getOptions().getNewModel()); 
		} else {
			model111Table.refresh( new Model111Callback());
		}
		tabLayout.setAnimationDuration(300);
		tabLayout.addSelectionHandler(event -> openFootPanelIfNeeded());
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
	
	private void onSelect(Integer id ) {
		SERVICE.getMod111(getOptions().getOccam(), id , new AsyncCallback<Mod111>() {
			@Override
			public void onSuccess(Mod111 selected) {
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

	private void newModel(Mod111 newModel) {
		SERVICE.initialize(getOptions().getOccam(),newModel,
				new AsyncCallback<Mod111>() {
					@Override
					public void onSuccess(Mod111 m111) {
						tabLayout.selectTab(INFORMATION_TAB);
						closeFootPanel();
						showNewDeclarationPanel( m111 );
					}


					@Override
					public void onFailure(Throwable caught) {
						aonLayout.showErrorPanel(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
					}
				});
	}

	enum Mod111Declarations {
		AEAT {
			@Override
			public boolean accept(Mod111 mod111) {
				return mod111.isAEAT();
			}

			@Override
			public Widget getDeclarationWidget(Mod111 mod111, Model111Callback cbk) {
				return new Model111AEAT(mod111, cbk);
			}
		},
		BIZKAIA_QUARTER {

			@Override
			public boolean accept(Mod111 mod111) {
				return mod111.isBizkaia() && mod111.getPeriod().isQuarterPeriod();
			}

			@Override
			public Widget getDeclarationWidget(Mod111 mod111, Model111Callback cbk) {
				return new Model110Bizkaia(mod111,cbk);
			}
		},
		BIZKAIA_MONTH {

			@Override
			public boolean accept(Mod111 mod111) {
				return mod111.isBizkaia() && mod111.getPeriod().isMonthPeriod();
			}

			@Override
			public Widget getDeclarationWidget(Mod111 mod111, Model111Callback cbk) {
				return new Model111Bizkaia(mod111,cbk);
			}
		},
		GIPUZKOA_QUARTER {

			@Override
			public boolean accept(Mod111 mod111) {
				return mod111.isGipuzkoa() && mod111.getPeriod().isQuarterPeriod();
			}

			@Override
			public Widget getDeclarationWidget(Mod111 mod111, Model111Callback cbk) {
				return new Model110Gipuzkoa(mod111,cbk);
			}
		},
		GIPUZKOA_MONTH {

			@Override
			public boolean accept(Mod111 mod111) {
				return mod111.isGipuzkoa() && mod111.getPeriod().isMonthPeriod();
			}

			@Override
			public Widget getDeclarationWidget(Mod111 mod111, Model111Callback cbk) {
				return new Model111Gipuzkoa(mod111,cbk);
			}
		},
		ARABA_2016 {

			@Override
			public boolean accept(Mod111 mod111) {
				return mod111.isAraba() && mod111.getYear() > 2015;
			}

			@Override
			public Widget getDeclarationWidget(Mod111 mod111, Model111Callback cbk) {
				return new Model111Araba2016(mod111,cbk);
			}
		},
		ARABA {

			@Override
			public boolean accept(Mod111 mod111) {
				return mod111.isAraba() && mod111.getYear() <= 2015;
			}

			@Override
			public Widget getDeclarationWidget(Mod111 mod111, Model111Callback cbk) {
				return new Model111Araba(mod111,cbk);
			}
		},
		NAVARRA_QUARTER {

			@Override
			public boolean accept(Mod111 mod111) {
				return mod111.isNavarra() && mod111.getPeriod().isQuarterPeriod();
			}

			@Override
			public Widget getDeclarationWidget(Mod111 mod111, Model111Callback cbk) {
				return new Model715Navarra(mod111,cbk);
			}
		},
		NAVARRA_MONTH {

			@Override
			public boolean accept(Mod111 mod111) {
				return mod111.isNavarra() && mod111.getPeriod().isMonthPeriod();
			}

			@Override
			public Widget getDeclarationWidget(Mod111 mod111, Model111Callback cbk) {
				return new Model745Navarra(mod111,cbk);
			}
		},
		;
		public abstract boolean accept(Mod111 mod111);
		public abstract Widget getDeclarationWidget(Mod111 mod111, Model111Callback cbk);
	}

			
	private void select(Mod111 selected) {
		aonLayout.hideErrorPanel();
		Widget declaration = null;
		for (Mod111Declarations dec : Mod111Declarations.values()) {
			if (dec.accept(selected)) {
				declaration = dec.getDeclarationWidget(selected, new Model111Callback());
			}
		}
		if (declaration != null) {
			declarationContainer.setWidget( declaration );		
		} else {
			aonLayout.showErrorPanel("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}

	private void onSelectionChange(SelectionEvent<Mod111> event) {
		Mod111 sel = event.getSelectedItem();
		SERVICE.getMod111(getOptions().getOccam(),sel.getId(), new AsyncCallback<Mod111>() {
			@Override
			public void onSuccess(Mod111 selected) {
				if (selected == null) {
					aonLayout.showErrorPanel(AON.MSG.unableToFindDeclaration());
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
	
	private void showNewDeclarationPanel( Mod111 m111) {
		Model111NewDeclarationPanel newDeclarationPanel = new Model111NewDeclarationPanel(m111,new Model111Callback() { 
			@Override
			public void onAccept(Mod111 mod111) {
				SERVICE.create(getOptions().getOccam(),mod111,
					new AsyncCallback<Mod111>() {
						@Override
						public void onSuccess(Mod111 m111) {
							select(m111);
						}

						@Override
						public void onFailure(Throwable caught) {
							showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						}
					});
			}
		}); 
		declarationContainer.setWidget(newDeclarationPanel);
		tabLayout.selectTab(INFORMATION_TAB);
		closeFootPanel();
	}

	private void showErrorMessage(String msg) {
		aonLayout.showErrorPanel(msg);
	}
	
	public static void run() {
		GWT.runAsync(Model111.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("Modelo 111"));
			}

			@Override
			public void onSuccess() {
				Model111 model111 = new Model111();
				model111.onModuleLoad();
			}
			
		});
	}
}

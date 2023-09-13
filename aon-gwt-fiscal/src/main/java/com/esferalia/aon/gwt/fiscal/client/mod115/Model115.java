package com.esferalia.aon.gwt.fiscal.client.mod115;

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
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
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

public class Model115 extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Model115.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static final int INFORMATION_TAB = 0;

	protected static final Mod115ServiceAsync SERVICE;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
		
		Mod115ServiceAsync serviceRaw = GWT.create(Mod115Service.class);
		SERVICE = new Mod115ServiceAsyncDecorator(serviceRaw);
	}
	
	private Model115ModuleOptions options;

	private AonLayoutPanel aonLayout;
	private SplitLayoutPanel splitLayoutPanel;
	private SimpleLayoutPanel declarationContainer;
	private Model115Table model115Table;
	private TabLayoutPanel tabLayout;
	private AonMinimizePanel footPanel;
	private ScrollPanel breakdownPanel;	

	protected class Model115Callback implements IFiscalModelCallback<Mod115,Model115ModuleOptions> {
		
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
		public Model115ModuleOptions getOptions() {
			return Model115.this.options;
		}

		@Override
		public void onAccept(Mod115 model) {
			// Nothing
		}

		@Override
		public void onCancel(Mod115 model) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onExit(model);
			} else {
				cleanInfoPanel();
				hideError();
				declarationContainer.setWidget(model115Table);
				model115Table.refresh( new Model115Callback() );
				tabLayout.selectTab(INFORMATION_TAB);
				closeFootPanel();
			}
		}

		@Override
		public void onRemove(Mod115 model) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onExit(model);
			} else {
				onCancel(model);
			}
		}

		@Override
		public void onNew() {
			SERVICE.initialize(getOptions().getOccam(),null,
			new AsyncCallback<Mod115>() {
				@Override
				public void onSuccess(Mod115 m115) {
					cleanInfoPanel();
					tabLayout.selectTab(INFORMATION_TAB);
					closeFootPanel();
					showNewDeclarationPanel(m115);
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
				Model115ModuleOptions opts = new Model115ModuleOptions();
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
	private Model115ModuleOptions getOptions() {
		if (this.options == null) {
			this.options = new Model115ModuleOptions();
		}
		return this.options;
	}
	
	public void onModuleLoad(Model115ModuleOptions options) {
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
		
		
		
		model115Table = new Model115Table( new Model115Callback() );
		model115Table.addSelectionHandler( this::onSelectionChange );
		declarationContainer.setWidget(model115Table);
		
		getOptions().getParentWidget().add(aonLayout);
		if (getOptions().getFiscalModelId() != null ) {
			onSelect(getOptions().getFiscalModelId());
		} else if (getOptions().getNewModel() != null ) {
			newModel(getOptions().getNewModel()); 
		} else {
			model115Table.refresh( new Model115Callback() );
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
		SERVICE.getMod115(getOptions().getOccam(), id , new AsyncCallback<Mod115>() {
			@Override
			public void onSuccess(Mod115 selected) {
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

	private void newModel(Mod115 newModel) {
		SERVICE.initialize(getOptions().getOccam(),newModel,
				new AsyncCallback<Mod115>() {
					@Override
					public void onSuccess(Mod115 m115) {
						tabLayout.selectTab(INFORMATION_TAB);
						closeFootPanel();
						showNewDeclarationPanel( m115 );
					}


					@Override
					public void onFailure(Throwable caught) {
						aonLayout.showErrorPanel(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
					}
				});
	}

	enum Mod115Declarations {
		AEAT {
			@Override
			public boolean accept(Mod115 mod115) {
				return mod115.isAEAT();
			}

			@Override
			public Widget getDeclarationWidget(Mod115 mod115, Model115Callback cbk) {
				return new Model115AEAT(mod115, cbk);
			}
		},
		BIZKAIA {

			@Override
			public boolean accept(Mod115 mod115) {
				return mod115.isBizkaia();
			}

			@Override
			public Widget getDeclarationWidget(Mod115 mod115, Model115Callback cbk) {
				return new Model115Bizkaia(mod115,cbk);
			}
		},
		GIPUZKOA {

			@Override
			public boolean accept(Mod115 mod115) {
				return mod115.isGipuzkoa();
			}

			@Override
			public Widget getDeclarationWidget(Mod115 mod115, Model115Callback cbk) {
				return new Model115Gipuzkoa(mod115,cbk);
			}
		},
		ARABA_2016 {

			@Override
			public boolean accept(Mod115 mod115) {
				return mod115.isAraba() && mod115.getYear() > 2015;
			}

			@Override
			public Widget getDeclarationWidget(Mod115 mod115, Model115Callback cbk) {
				return new Model115Araba2016(mod115,cbk);
			}
		},
		ARABA {

			@Override
			public boolean accept(Mod115 mod115) {
				return mod115.isAraba() && mod115.getYear() <= 2015;
			}

			@Override
			public Widget getDeclarationWidget(Mod115 mod115, Model115Callback cbk) {
				return new Model115Araba(mod115,cbk);
			}
		},
		NAVARRA_QUARTER {

			@Override
			public boolean accept(Mod115 mod115) {
				return mod115.isNavarra() && mod115.getPeriod().isQuarterPeriod();
			}

			@Override
			public Widget getDeclarationWidget(Mod115 mod115, Model115Callback cbk) {
				return new Model759Navarra(mod115,cbk);
			}
		},
		NAVARRA_MONTH {

			@Override
			public boolean accept(Mod115 mod115) {
				return mod115.isNavarra() && mod115.getPeriod().isMonthPeriod();
			}

			@Override
			public Widget getDeclarationWidget(Mod115 mod115, Model115Callback cbk) {
				return new Model760Navarra(mod115,cbk);
			}
		},
		;
		public abstract boolean accept(Mod115 mod115);
		public abstract Widget getDeclarationWidget(Mod115 mod115, Model115Callback cbk);
	}

			
	private void select(Mod115 selected) {
		aonLayout.hideErrorPanel();
		Widget declaration = null;
		for (Mod115Declarations dec : Mod115Declarations.values()) {
			if (dec.accept(selected)) {
				declaration = dec.getDeclarationWidget(selected, new Model115Callback());
			}
		}
		if (declaration != null) {
			declarationContainer.setWidget( declaration );		
		} else {
			aonLayout.showErrorPanel("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}

	private void onSelectionChange(SelectionEvent<Mod115> event) {
		Mod115 sel = event.getSelectedItem();
		SERVICE.getMod115(getOptions().getOccam(),sel.getId(), new AsyncCallback<Mod115>() {
			@Override
			public void onSuccess(Mod115 selected) {
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
	
	private void showNewDeclarationPanel( Mod115 m115) {
		Model115NewDeclarationPanel newDeclarationPanel = new Model115NewDeclarationPanel(m115,new Model115Callback() { 
			@Override
			public void onAccept(Mod115 mod115) {
				SERVICE.create(getOptions().getOccam(),mod115,
					new AsyncCallback<Mod115>() {
						@Override
						public void onSuccess(Mod115 m115) {
							select(m115);
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
		GWT.runAsync(Model115.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("Modelo 115"));
			}

			@Override
			public void onSuccess() {
				Model115 model115 = new Model115();
				model115.onModuleLoad();
			}
			
		});
	}
}

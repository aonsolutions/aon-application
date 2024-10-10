package com.esferalia.aon.gwt.fiscal.client.mod123;

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
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
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

public class Model123 extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Model123.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static final int INFORMATION_TAB = 0;

	protected static final Mod123ServiceAsync SERVICE;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
		
		Mod123ServiceAsync serviceRaw = GWT.create(Mod123Service.class);
		SERVICE = new Mod123ServiceAsyncDecorator(serviceRaw);
	}
	
	private Model123ModuleOptions options;

	private AonLayoutPanel aonLayout;
	private SplitLayoutPanel splitLayoutPanel;
	private SimpleLayoutPanel declarationContainer;
	private Model123Table model123Table;
	private TabLayoutPanel tabLayout;
	private AonMinimizePanel footPanel;
	private ScrollPanel breakdownPanel;	

	protected class Model123Callback implements IFiscalModelCallback<Mod123,Model123ModuleOptions> {
		
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
		public Model123ModuleOptions getOptions() {
			return Model123.this.options;
		}

		@Override
		public void onAccept(Mod123 model) {
			// Nothing
		}

		@Override
		public void onCancel(Mod123 model) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onExit(model);
			} else {
				cleanInfoPanel();
				hideError();
				declarationContainer.setWidget(model123Table);
				model123Table.refresh( new Model123Callback() );
				tabLayout.selectTab(INFORMATION_TAB);
				closeFootPanel();
			}
		}

		@Override
		public void onRemove(Mod123 model) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onRemove(model);
			} else {
				onCancel(model);
			}
		}

		@Override
		public void onNew() {
			SERVICE.initialize(getOptions().getOccam(),null,
			new AsyncCallback<Mod123>() {
				@Override
				public void onSuccess(Mod123 m123) {
					cleanInfoPanel();
					tabLayout.selectTab(INFORMATION_TAB);
					closeFootPanel();
					showNewDeclarationPanel(m123);
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
				Model123ModuleOptions opts = new Model123ModuleOptions();
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
	private Model123ModuleOptions getOptions() {
		if (this.options == null) {
			this.options = new Model123ModuleOptions();
		}
		return this.options;
	}
	
	public void onModuleLoad(Model123ModuleOptions options) {
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
		
		
		
		model123Table = new Model123Table( new Model123Callback() );
		model123Table.addSelectionHandler( this::onSelectionChange );
		declarationContainer.setWidget(model123Table);
		
		getOptions().getParentWidget().add(aonLayout);
		if (getOptions().getFiscalModelId() != null ) {
			onSelect(getOptions().getFiscalModelId());
		} else if (getOptions().getNewModel() != null ) {
			newModel(getOptions().getNewModel()); 
		} else {
			model123Table.refresh( new Model123Callback() );
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
		SERVICE.getMod123(getOptions().getOccam(), id , new AsyncCallback<Mod123>() {
			@Override
			public void onSuccess(Mod123 selected) {
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

	private void newModel(Mod123 newModel) {
		SERVICE.initialize(getOptions().getOccam(),newModel,
				new AsyncCallback<Mod123>() {
					@Override
					public void onSuccess(Mod123 m123) {
						tabLayout.selectTab(INFORMATION_TAB);
						closeFootPanel();
						showNewDeclarationPanel( m123 );
					}


					@Override
					public void onFailure(Throwable caught) {
						aonLayout.showErrorPanel(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
					}
				});
	}

	enum Mod123Declarations {
		AEAT {
			@Override
			public boolean accept(Mod123 mod123) {
				return mod123.isAEAT();
			}

			@Override
			public Widget getDeclarationWidget(Mod123 mod123, Model123Callback cbk) {
				return new Model123AEAT(mod123, cbk);
			}
		},
		BIZKAIA {

			@Override
			public boolean accept(Mod123 mod123) {
				return mod123.isBizkaia();
			}

			@Override
			public Widget getDeclarationWidget(Mod123 mod123, Model123Callback cbk) {
				return new Model123Bizkaia(mod123,cbk);
			}
		},
		GIPUZKOA {

			@Override
			public boolean accept(Mod123 mod123) {
				return mod123.isGipuzkoa();
			}

			@Override
			public Widget getDeclarationWidget(Mod123 mod123, Model123Callback cbk) {
				return new Model123Gipuzkoa(mod123,cbk);
			}
		},
		ARABA_2016 {

			@Override
			public boolean accept(Mod123 mod123) {
				return mod123.isAraba() && mod123.getYear() > 2015;
			}

			@Override
			public Widget getDeclarationWidget(Mod123 mod123, Model123Callback cbk) {
				return new Model123Araba2016(mod123,cbk);
			}
		},
		ARABA {

			@Override
			public boolean accept(Mod123 mod123) {
				return mod123.isAraba() && mod123.getYear() <= 2015;
			}

			@Override
			public Widget getDeclarationWidget(Mod123 mod123, Model123Callback cbk) {
				return new Model123Araba(mod123,cbk);
			}
		},
		NAVARRA {

			@Override
			public boolean accept(Mod123 mod123) {
				return mod123.isNavarra();
			}

			@Override
			public Widget getDeclarationWidget(Mod123 mod123, Model123Callback cbk) {
				return new Model716Navarra(mod123,cbk);
			}
		},
		;
		public abstract boolean accept(Mod123 mod123);
		public abstract Widget getDeclarationWidget(Mod123 mod123, Model123Callback cbk);
	}

			
	private void select(Mod123 selected) {
		aonLayout.hideErrorPanel();
		Widget declaration = null;
		for (Mod123Declarations dec : Mod123Declarations.values()) {
			if (dec.accept(selected)) {
				declaration = dec.getDeclarationWidget(selected, new Model123Callback());
			}
		}
		if (declaration != null) {
			declarationContainer.setWidget( declaration );		
		} else {
			aonLayout.showErrorPanel("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}

	private void onSelectionChange(SelectionEvent<Mod123> event) {
		Mod123 sel = event.getSelectedItem();
		SERVICE.getMod123(getOptions().getOccam(),sel.getId(), new AsyncCallback<Mod123>() {
			@Override
			public void onSuccess(Mod123 selected) {
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
	
	private void showNewDeclarationPanel( Mod123 m123) {
		Model123NewDeclarationPanel newDeclarationPanel = new Model123NewDeclarationPanel(m123,new Model123Callback() { 
			@Override
			public void onAccept(Mod123 mod123) {
				SERVICE.create(getOptions().getOccam(),mod123,
					new AsyncCallback<Mod123>() {
						@Override
						public void onSuccess(Mod123 m123) {
							select(m123);
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
		GWT.runAsync(Model123.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("Modelo 123"));
			}

			@Override
			public void onSuccess() {
				Model123 model123 = new Model123();
				model123.onModuleLoad();
			}
			
		});
	}
}

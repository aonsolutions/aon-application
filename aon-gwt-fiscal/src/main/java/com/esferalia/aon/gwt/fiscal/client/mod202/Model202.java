package com.esferalia.aon.gwt.fiscal.client.mod202;

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
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Period;
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

public class Model202 extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Model202.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	static final int INFORMATION_TAB = 0;

	static final Mod202ServiceAsync SERVICE;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
		
		Mod202ServiceAsync serviceRaw = GWT.create(Mod202Service.class);
		SERVICE = new Mod202ServiceAsyncDecorator(serviceRaw);
	}
	
	private Model202ModuleOptions options;
	
	private AonLayoutPanel aonLayout;
	private SplitLayoutPanel splitLayoutPanel;
	private SimpleLayoutPanel declarationContainer;
	private Model202Table model202Table;
	private TabLayoutPanel tabLayout;
	private AonMinimizePanel footPanel;
	private ScrollPanel breakdownPanel;	
	
	protected class Model202Callback implements IFiscalModelCallback<Mod202,Model202ModuleOptions> {
		
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

		@Override
		public Model202ModuleOptions getOptions() {
			return Model202.this.options;
		}

		@Override
		public void onAccept(Mod202 model) {
			// Nothing
		}

		@Override
		public void onCancel(Mod202 model) {
			cleanInfoPanel();
			declarationContainer.setWidget(model202Table);
			model202Table.refresh( new Model202Callback() );
			tabLayout.selectTab(INFORMATION_TAB);
			closeFootPanel();
		}

		@Override
		public void onRemove(Mod202 model) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onExit(model);
			} else {
				onCancel(model);
			}
		}

		@Override
		public void onNew() {
			SERVICE.initialize(getOptions().getOccam(),null,
			new AsyncCallback<Mod202>() {
				@Override
				public void onSuccess(Mod202 m202) {
					cleanInfoPanel();
					tabLayout.selectTab(INFORMATION_TAB);
					closeFootPanel();
					showNewDeclarationPopup(m202);
				}


				@Override
				public void onFailure(Throwable caught) {
					showErrorMessage(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
				}
			});
		}
	}
	
	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {
			
			@Override
			public void onSuccess(AonConfiguration aonConfiguration) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model202ModuleOptions opts = new Model202ModuleOptions();
				opts.setParentWidget(root);
				opts.setDomainName(getCurrentDomainName());
				opts.setDomain(getCurrentDomain());
				opts.setUser(getCurrentUser());
				opts.setConfiguration(aonConfiguration);
				onModuleLoad( opts );
			}
			
			@Override public void onFailure(Throwable caught) {
				Window.alert( "Error al cargar el module" );
			}
		});
	}
	
	private Model202ModuleOptions getOptions() {
		if (this.options == null) {
			this.options = new Model202ModuleOptions();
		}
		return this.options;
	}
	
	public void onModuleLoad(Model202ModuleOptions options) {
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

		model202Table = new Model202Table( new Model202Callback() );
		model202Table.addSelectionHandler( this::onSelectionChange );
		declarationContainer.setWidget(model202Table);

		getOptions().getParentWidget().add(aonLayout);
		if (getOptions().getFiscalModelId() != null ) {
			onSelect(getOptions().getFiscalModelId());
		} else if (getOptions().getNewModel() != null ) {
			newModel(getOptions().getNewModel()); 
		} else {
			model202Table.refresh( new Model202Callback() );
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
		LOGGER.info("OnSelect Model202 with a ID: " + getOptions().getFiscalModelId());
		SERVICE.getMod202(getOptions().getOccam(), id , new AsyncCallback<Mod202>() {
			@Override
			public void onSuccess(Mod202 selected) {
				if (selected == null) {
					showErrorMessage(AON.MSG.unableToFindDeclaration());
				} else {
					LOGGER.info("onSuccess Model202 with a ID: " + selected.getId());
					select(selected);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
			}
		});
	}
	
	private void newModel(Mod202 newModel) {
		SERVICE.initialize(getOptions().getOccam(),newModel, new AsyncCallback<Mod202>() {
			@Override
			public void onSuccess(Mod202 m202) {
				tabLayout.selectTab(INFORMATION_TAB);
				closeFootPanel();
				showNewDeclarationPopup( m202 );
			}

			@Override
			public void onFailure(Throwable caught) {
				aonLayout.showErrorPanel(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
			}
		});
	}
	
	enum Mod202Declarations {
		AEAT {
			@Override
			public boolean accept(Mod202 mod202) {
				return mod202.isAEAT() && (mod202.getYear() < 2018 || 
						(mod202.getYear() == 2018 && mod202.getPeriod().ordinal() < Period.T2.ordinal()));
			}

			@Override
			public Widget getDeclarationWidget(Mod202 mod202, Model202Callback cbk) {
				return new Model202AEAT(mod202, cbk);
			}
		},
		AEAT_2018 {
			@Override
			public boolean accept(Mod202 mod202) {
				return mod202.isAEAT() 
					&& (mod202.getYear() > 2018 
					|| (mod202.getYear() == 2018 && mod202.getPeriod().ordinal() >= Period.T2.ordinal()));
			}

			@Override
			public Widget getDeclarationWidget(Mod202 mod202, Model202Callback cbk) {
				return new Model2022018AEAT(mod202, cbk);
			}
		},
		;
		public abstract boolean accept(Mod202 mod202);
		public abstract Widget getDeclarationWidget(Mod202 mod202, Model202Callback cbk);
	}
	
	private void select(Mod202 selected) {
		aonLayout.hideErrorPanel();
		Widget declaration = null;
		for (Mod202Declarations dec : Mod202Declarations.values()) {
			if (dec.accept(selected)) {
				declaration = dec.getDeclarationWidget(selected, new Model202Callback());
			}
		}
		if (declaration != null) {
			declarationContainer.setWidget( declaration );		
		} else {
			aonLayout.showErrorPanel("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}

	private void onSelectionChange(SelectionEvent<Mod202> event) {
		Mod202 sel = event.getSelectedItem();
		SERVICE.getMod202(getOptions().getOccam(),sel.getId(), new AsyncCallback<Mod202>() {
			@Override
			public void onSuccess(Mod202 selected) {
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
	
	private void showNewDeclarationPopup( Mod202 mod202) {
		Model202NewDeclarationPopup newDialog = new Model202NewDeclarationPopup(mod202,
			new Model202Callback() {

				@Override
				public void onAccept(Mod202 mod202) {
					SERVICE.create(getOptions().getOccam(),mod202,
							new AsyncCallback<Mod202>() {
								@Override
								public void onSuccess(Mod202 m202) {
									select(m202);
								}

								@Override
								public void onFailure(Throwable caught) {
									showErrorMessage(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
								}
							});
				}
				@Override
				public void onCancel(Mod202 model) {
					if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
						getOptions().getExternalCallback().onExit(model);
					}						
				}

			}
		); 
		newDialog.center();
		newDialog.show();
	}
	
	private void showErrorMessage(String msg) {
		aonLayout.showErrorPanel(msg);
	}

	public static void run() {
		GWT.runAsync(Model202.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("Modelo 202"));
			}

			@Override
			public void onSuccess() {
				Model202 model202 = new Model202();
				model202.onModuleLoad();
			}
			
		});
	}
	
}

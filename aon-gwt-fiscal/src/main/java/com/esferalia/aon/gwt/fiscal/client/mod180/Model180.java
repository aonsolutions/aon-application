package com.esferalia.aon.gwt.fiscal.client.mod180;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;

public class Model180 extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Model180.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	public static final ProvidesKey<Mod180Detail> MOD180_DETAIL_PROVIDES_KEY = new ProvidesKey<Mod180Detail>() {
		@Override
		public Object getKey(Mod180Detail det) {
			return det == null ?null: det.getId() == null? det.getTempId(): det.getId();
		}
	};

	private final static int NOTIFICATIONS_TAB = 0;
	private final static int BREAKDOWN_TAB = 1;
	
	static Model180ServiceAsync SERVICE;
	final FiscalServiceAsync impl = GWT.create(FiscalService.class);
	
	interface Model180Binder extends UiBinder<Widget, Model180> {}
	private static final Model180Binder MODEL_180_BINDER = GWT.create(Model180Binder.class);

	protected static interface IModel180Callback{
		void onAccept(Mod180 mod180);
//		int getDomain();
//		String getUser();
//		String getDomainName();
		void onCancel();
		void onSelect(Model180ModuleOptions options, Mod180 mod180, Integer selectedIndex);
		void showError(String msg);
		void cleanErrorPanel();
		void onNew(Model180ModuleOptions options);
	}

	protected class Model180Callback implements IModel180Callback {
		@Override
		public void onAccept(Mod180 mod180) {
			// 
		}
		@Override
		public void onCancel() {
			cancel();
		}
		@Override
		public void onSelect(Model180ModuleOptions options, Mod180 mod180, Integer selectedIndex) {
			select(options, mod180, selectedIndex);
		}
		@Override
		public void onNew(Model180ModuleOptions options) {
			newModel(options);
		}
		@Override
		public void cleanErrorPanel() {
			Model180.this.cleanErrorPanel();
		}
		@Override
		public void showError(String msg) {
			Model180.this.showErrorPanel(msg);
		}
//		@Override
//		public String getDomainName() {
//			return getCurrentDomainName();
//		}
//		@Override
//		public String getUser() {
//			return getCurrentUser();
//		}
//		@Override
//		public int getDomain() {
//			return getCurrentDomain();
//		}
	};

	@UiField
	SplitLayoutPanel splitLayoutPanel;

	@UiField
	SimpleLayoutPanel declarationContainer;
	
	@UiField
	TabLayoutPanel tabLayout;
	
	@UiField
	ResultsPanel notificationsPanel;
	
	@UiField
	MinimizePanel footPanel;

	@UiField
	ScrollPanel breakdownPanel;

	Model180Table model180Table;
	
	Panel formContainer;
	SimplePanel headerPanel = new SimplePanel();
	
	@Override
	public void onModuleLoad() {
		impl.getAonData(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonData>() {
			
			@Override
			public void onSuccess(AonData aonData) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model180ModuleOptions options = new Model180ModuleOptions();
				options.setParentWidget(root);
				options.setDomainName(getCurrentDomainName());
				options.setDomain(getCurrentDomain());
				options.setUser(getCurrentUser());
				options.setAonData(aonData);
				onModuleLoad( options );
			}
			
			@Override public void onFailure(Throwable caught) {
				Window.alert( "Error al cargar el module" );
			}
		});
	}
	
	public void onModuleLoad(Model180ModuleOptions options) {
		AON.ensureInjected();

		Model180ServiceAsync serviceRaw = GWT.create(Model180Service.class);
		SERVICE = new Model180ServiceAsyncDecorator(serviceRaw);

		Widget ui = MODEL_180_BINDER.createAndBindUi(this);

		model180Table = new Model180Table(options, new Model180Callback());
		model180Table.addSelectionHandler(new SelectionHandler<Mod180>() {
			
			@Override
			public void onSelection(SelectionEvent<Mod180> event) {
				onSelectionChange(options, event);
			}
		});
		
		declarationContainer.setWidget(model180Table);
		
//		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
//		root.add(ui);
		options.getParentWidget().add(ui);
		if (options.getFiscalModelId() != null ) {
			LOGGER.info("Access to Model180 with a ID: " + options.getFiscalModelId());
			onSelect(options,options.getFiscalModelId());
		} else if (options.getNewModel() != null ) {
			LOGGER.info("Access to Model180 new Model");
			newModel(options, options.getNewModel().getYear()); 
		} else {
			model180Table.refresh();
			LOGGER.info("Model180 setting NOTIFICATIONS_TAB");
			tabLayout.selectTab(NOTIFICATIONS_TAB);
		}

	}

	private void onSelect(Model180ModuleOptions options, Integer id ) {
		LOGGER.info("OnSelect Model111 with a ID: " + options.getFiscalModelId());
		SERVICE.getMod180(options.getDomainName(), options.getUser(), options.getDomain(), id , new AsyncCallback<Mod180>() {
			@Override
			public void onSuccess(Mod180 selected) {
				if (selected == null) {
					showErrorPanel(AON.MSG.unableToFindDeclaration());
				} else {
					select(options, selected, null);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				showErrorPanel(AON.MSG.unableToReadDeclaration(caught.getMessage()));
			}
		});
	}

	private void onSelectionChange(Model180ModuleOptions options, SelectionEvent<Mod180> event) {
		Mod180 sel = event.getSelectedItem();
		SERVICE.getMod180(getCurrentDomainName(), getCurrentUser(), getCurrentDomain(),
				sel.getId(), new AsyncCallback<Mod180>() {
					@Override
					public void onSuccess(Mod180 selected) {
						if (selected == null) {
							showErrorPanel(AON.MSG.unableToFindDeclaration());
						} else {
							select(options, selected, null);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(AON.MSG.unableToReadDeclaration(caught.getMessage()));
					}
				} );
	}
	

	private void select(Model180ModuleOptions options, Mod180 selected, Integer selectedIndex) {
		cleanErrorPanel();
		if ( selected.isAEAT() && selected.getYear() < 2020 ) {
			declarationContainer.setWidget( new Model1802017AEAT(selected,options,new Model180Callback(),selectedIndex));
		} else if ( selected.isAEAT() && selected.getYear() >= 2020 ) {
			declarationContainer.setWidget( new Model1802020AEAT(selected,options,new Model180Callback(),selectedIndex));
		} else if ( selected.isAraba() ) {
			declarationContainer.setWidget( new Model1802017ARABA(selected,options,new Model180Callback(),selectedIndex));			
		} else if ( selected.isBizkaia() ) {
			declarationContainer.setWidget( new Model1802017BIZKAIA(selected,options,new Model180Callback(),selectedIndex));			
		} else if ( selected.isGipuzkoa() ) {
			declarationContainer.setWidget( new Model1802017GIPUZKOA(selected,options,new Model180Callback(),selectedIndex));			
		} else if ( selected.isNavarra() ) {
			declarationContainer.setWidget( new Model1802017NAVARRA(selected,options,new Model180Callback(),selectedIndex));			
		} else {
			showErrorPanel("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}
	
	private void newModel(Model180ModuleOptions options) {
		newModel(options, 2020);
	}

	private void newModel(Model180ModuleOptions options, int year) {
		cleanErrorPanel();
		SERVICE.initializeMod180(getCurrentDomainName(), getCurrentUser(),getCurrentDomain(), year,
				new AsyncCallback<Mod180>() {
					@Override
					public void onSuccess(Mod180 m180) {
						cleanBreakdownPanel();
						tabLayout.selectTab(BREAKDOWN_TAB);
						closeFootPanel();
						showNewDeclarationPopup(options, m180);
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
					}
				});
	}

	private void cancel() {
		cleanErrorPanel();
		declarationContainer.setWidget(model180Table);
		model180Table.refresh();
		closeFootPanel();
	}
	
	@UiHandler("footPanel")
	protected void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}

	@UiHandler("footPanel")
	protected void onFootMaximize(MaximizeEvent event) {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 2);
		splitLayoutPanel.animate(500);
	}

	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 30);
		splitLayoutPanel.animate(500);
	}

	private void openFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4);
		splitLayoutPanel.animate(500);
	}
	
//	private void showResultsPanel() {
//		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 5);
//	}
//
//	private boolean isResultsPanelVisible() {
//		return splitLayoutPanel.getWidgetSize(footPanel) > 0;
//	}
	
	private void cleanErrorPanel() {
		SimpleLayoutPanel panel = new SimpleLayoutPanel();
		notificationsPanel.setWidget(panel);
		closeFootPanel();
	}
	private void openFootPanelIfNeeded() {
		if (splitLayoutPanel.getWidgetSize(footPanel) <= 50) {
			openFootPanel();
		}
	}
	private void showErrorPanel(String msg) {
		//openFootPanelIfNeeded();

		ScrollPanel panel = new ScrollPanel();
		FlexTable tab = new FlexTable();
		tab.setWidth("95%");
		tab.setStyleName(AON.AON_CSS.aonBlockCenter());
		tab.addStyleName(AON.AON_CSS.aonMarginBottom());
		tab.addStyleName(AON.AON_CSS.aonMarginTop());
		tab.getColumnFormatter().setWidth(0, "20px");
		tab.getColumnFormatter().setWidth(1, "auto");
		
		InlineLabel icon = new InlineLabel("");
		icon.setStyleName(AON.AON_CSS.aonIconPointRed());
		icon.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
		tab.setWidget(0, 0, icon);
		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		
		InlineLabel label = new InlineLabel(msg);
		label.addStyleName(AON.AON_CSS.aonColorRed());
		label.addStyleName(AON.AON_CSS.aonBold());
		tab.setWidget(0, 1, label);
		tab.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonPanelGridEven());
		
		panel.add(tab);
		notificationsPanel.setWidget(panel);
		
		// Abrimos el panel inferior, si es necesario y seleccionamos la pestaña de notificaciones
		openFootPanelIfNeeded();		
		tabLayout.selectTab(NOTIFICATIONS_TAB);
	}
	
	private void cleanBreakdownPanel() {
		Widget w = breakdownPanel.getWidget();
		if (w != null) {
			breakdownPanel.remove( breakdownPanel.getWidget() ); 
		}
	}
	
	private void showNewDeclarationPopup(Model180ModuleOptions options,Mod180 model) {
		NewDeclarationPopup newDialog = new NewDeclarationPopup( model,
			new Model180Callback() {

					@Override
					public void onAccept(Mod180 model) {
						final PopupPanel popup = new PopupPanel(false, true);
						Label label = new Label(AON.MSG.processing());
						label.addStyleName(AON.AON_CSS.aonTimer());
						popup.add(label);
						popup.setGlassEnabled(true);
						popup.setAnimationEnabled(true);
						popup.center();

						SERVICE.saveMod180(getCurrentDomainName(), getCurrentUser(),getCurrentDomain(),model,
								new AsyncCallback<Mod180>() {
									@Override
									public void onSuccess(Mod180 model) {
										popup.hide();
										select(options, model, null);
									}

									@Override
									public void onFailure(Throwable caught) {
										popup.hide();
										showErrorPanel(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
									}
								});
					}
				}
			); 
			newDialog.center();
			newDialog.show();
	}
}

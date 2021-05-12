package com.esferalia.aon.gwt.fiscal.client.mod184;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.client.FiscalMSService;
import com.esferalia.aon.gwt.fiscal.client.FiscalMSServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Income;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Partner;
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

public class Model184 extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Model184.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	public static final ProvidesKey<Mod184Income> MOD184_INCOME_PROVIDES_KEY = new ProvidesKey<Mod184Income>() {
		@Override
		public Object getKey(Mod184Income det) {
			return det == null ?null: det.getId() == null? det.getTempId(): det.getId();
		}
	};
	public static final ProvidesKey<Mod184Partner> MOD184_PARTNER_PROVIDES_KEY = new ProvidesKey<Mod184Partner>() {
		@Override
		public Object getKey(Mod184Partner det) {
			return det == null ?null: det.getId() == null? det.getTempId(): det.getId();
		}
	};
	
	private final static int NOTIFICATIONS_TAB = 0;
	private final static int BREAKDOWN_TAB = 1;
	
	static Model184ServiceAsync SERVICE;
	final FiscalMSServiceAsync FISCAL_SERVICE = GWT.create(FiscalMSService.class);
	
	interface Model184Binder extends UiBinder<Widget, Model184> {}
	private static final Model184Binder MODEL_184_BINDER = GWT.create(Model184Binder.class);

	protected static interface IModel184Callback{
		void onAccept(Mod184 mod184);
		void onCancel();
		void onSelect(Model184ModuleOptions options,Mod184 mod184, Integer selectedIncomeIndex, Integer selectedPartnerIndex, Integer tabIndex);
		void showError(String msg);
		void cleanErrorPanel();
		void onNew(Model184ModuleOptions options);
	}
	
	protected class Model184Callback implements IModel184Callback {
		
		@Override
		public void onAccept(Mod184 mod184) {
			// 
		}
		@Override
		public void onCancel() {
			cancel();
		}
		@Override
		public void onSelect(Model184ModuleOptions options,Mod184 mod184, Integer selectedIncomeIndex, Integer selectedPartnerIndex, Integer tabIndex) {
			select(options,mod184, selectedIncomeIndex,selectedPartnerIndex,tabIndex);
		}
		@Override
		public void onNew(Model184ModuleOptions options) {
			newModel(options);
		}
		@Override
		public void cleanErrorPanel() {
			Model184.this.cleanErrorPanel();
		}
		@Override
		public void showError(String msg) {
			Model184.this.showErrorPanel(msg);
		}
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

	Model184Table model184Table;
	
	Panel formContainer;
	SimplePanel headerPanel = new SimplePanel();
	
	@Override
	public void onModuleLoad() {
		FISCAL_SERVICE.getAonData(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonData>() {
			
			@Override
			public void onSuccess(AonData aonData) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model184ModuleOptions options = new Model184ModuleOptions();
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
	
	public void onModuleLoad(Model184ModuleOptions options) {
		AON.ensureInjected();

		Model184ServiceAsync serviceRaw = GWT.create(Model184Service.class);
		SERVICE = new Model184ServiceAsyncDecorator(serviceRaw);

		Widget ui = MODEL_184_BINDER.createAndBindUi(this);

		model184Table = new Model184Table(options,new Model184Callback());
		model184Table.addSelectionHandler(new SelectionHandler<Mod184>() {
			
			@Override
			public void onSelection(SelectionEvent<Mod184> event) {
				onSelectionChange(options, event);
			}
		});
		
		declarationContainer.setWidget(model184Table);
		
		options.getParentWidget().add(ui);
		if (options.getFiscalModelId() != null ) {
			LOGGER.info("Access to Model184 with a ID: " + options.getFiscalModelId());
			onSelect(options,options.getFiscalModelId());
		} else if (options.getNewModel() != null ) {
			LOGGER.info("Access to Model184 new Model");
			newModel(options, options.getNewModel().getYear()); 
		} else {
			model184Table.refresh();
			LOGGER.info("Model184 setting NOTIFICATIONS_TAB");
			tabLayout.selectTab(NOTIFICATIONS_TAB);
		}

	}
	
	private void onSelect(Model184ModuleOptions options, Integer id ) {
		LOGGER.info("OnSelect Model184 with a ID: " + options.getFiscalModelId());
		SERVICE.getMod184(options.getDomainName(), options.getUser(), options.getDomain(), id , new AsyncCallback<Mod184>() {
			@Override
			public void onSuccess(Mod184 selected) {
				if (selected == null) {
					showErrorPanel(AON.MSG.unableToFindDeclaration());
				} else {
					select(options, selected, null, null, null);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				showErrorPanel(AON.MSG.unableToReadDeclaration(caught.getMessage()));
			}
		});
	}

	private void onSelectionChange(Model184ModuleOptions options, SelectionEvent<Mod184> event) {
		Mod184 sel = event.getSelectedItem();
		SERVICE.getMod184(options.getDomainName(),options.getUser(), options.getDomain(),
				sel.getId(), new AsyncCallback<Mod184>() {
					@Override
					public void onSuccess(Mod184 selected) {
						if (selected == null) {
							showErrorPanel(AON.MSG.unableToFindDeclaration());
						} else {
							select(options, selected, null, null, null);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(AON.MSG.unableToReadDeclaration(caught.getMessage()));
					}
				} );
	}
	

	private void select(Model184ModuleOptions options,Mod184 selected, Integer selectedIncomeIndex, Integer selectedPartnerIndex,Integer tabIndex) {
		cleanErrorPanel();
		if ( selected.isAEAT() ) {
			declarationContainer.setWidget( new Model184AEAT(selected,options,new Model184Callback(),selectedIncomeIndex,selectedPartnerIndex,tabIndex));
		} else if ( selected.isAraba() ) {
			declarationContainer.setWidget( new Model184ARABA(selected,options,new Model184Callback(),selectedIncomeIndex,selectedPartnerIndex,tabIndex));
		} else if ( selected.isBizkaia() ) {
			declarationContainer.setWidget( new Model184BIZKAIA(selected,options,new Model184Callback(),selectedIncomeIndex,selectedPartnerIndex,tabIndex));
		} else if ( selected.isGipuzkoa() ) {
			declarationContainer.setWidget( new Model184GIPUZKOA(selected,options,new Model184Callback(),selectedIncomeIndex,selectedPartnerIndex,tabIndex));
		} else if ( selected.isNavarra() ) {
			declarationContainer.setWidget( new Model184NAVARRA(selected,options,new Model184Callback(),selectedIncomeIndex,selectedPartnerIndex,tabIndex));
		} else {
			showErrorPanel("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}
		
	private void newModel(Model184ModuleOptions options) {
		newModel(options, 2020 );
	}

	private void newModel(Model184ModuleOptions options, int year) {
		cleanErrorPanel();
		SERVICE.initializeMod184(options.getDomainName(),options.getUser(),options.getDomain(), year,
				new AsyncCallback<Mod184>() {
					@Override
					public void onSuccess(Mod184 m184) {
						cleanBreakdownPanel();
						tabLayout.selectTab(BREAKDOWN_TAB);
						closeFootPanel();
						showNewDeclarationPopup(options, m184);
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
					}
				});
	}

	private void cancel() {
		cleanErrorPanel();
		declarationContainer.setWidget(model184Table);
		model184Table.refresh();
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
	
	private void showNewDeclarationPopup(Model184ModuleOptions options, Mod184 model) {
		NewDeclarationPopup newDialog = new NewDeclarationPopup( model,
			new Model184Callback() {

					@Override
					public void onAccept(Mod184 model) {
						final PopupPanel popup = new PopupPanel(false, true);
						Label label = new Label(AON.MSG.processing());
						label.addStyleName(AON.AON_CSS.aonTimer());
						popup.add(label);
						popup.setGlassEnabled(true);
						popup.setAnimationEnabled(true);
						popup.center();

						SERVICE.saveMod184(options.getDomainName(),options.getUser(),options.getDomain(),model,
								new AsyncCallback<Mod184>() {
									@Override
									public void onSuccess(Mod184 model) {
										popup.hide();
										select(options, model, null, null, null);
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

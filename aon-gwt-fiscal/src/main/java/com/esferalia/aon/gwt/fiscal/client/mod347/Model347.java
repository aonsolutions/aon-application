package com.esferalia.aon.gwt.fiscal.client.mod347;

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
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Asset;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
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
import com.google.gwt.user.client.ui.HTMLPanel;
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

public class Model347 extends MainEntryPoint {
	private static final Logger LOGGER = Logger.getLogger(Model347.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	public static final ProvidesKey<Mod347Declared> MOD347_DECLARED_PROVIDES_KEY = new ProvidesKey<Mod347Declared>() {
		@Override
		public Object getKey(Mod347Declared det) {
			return det == null ?null: det.getId() == null? det.getTempId(): det.getId();
		}
	};
	public static final ProvidesKey<Mod347Asset> MOD347_ASSET_PROVIDES_KEY = new ProvidesKey<Mod347Asset>() {
		@Override
		public Object getKey(Mod347Asset det) {
			return det == null ?null: det.getId() == null? det.getTempId(): det.getId();
		}
	};
	
	private final static int NOTIFICATIONS_TAB = 0;
	private final static int BREAKDOWN_TAB = 1;
	
	static Model347ServiceAsync SERVICE;
	final FiscalMSServiceAsync FISCAL_SERVICE = GWT.create(FiscalMSService.class);
	
	interface Model347Binder extends UiBinder<Widget, Model347> {}
	private static final Model347Binder MODEL_347_BINDER = GWT.create(Model347Binder.class);

	protected static interface IModel347Callback{

		void onAccept(Mod347 mod347);
		void onCancel();
		void onSelect(Model347ModuleOptions options,Mod347 mod347, Integer selectedIndexDeclared, Integer selectedIndexAsset, int tabPanelIndex);
		void showError(String msg);
		void cleanErrorPanel();
		void onNew(Model347ModuleOptions options);
		void showBreakdownPanel(String htmlText);
		void cleanBreakdownPanel();
	}
	
	protected class Model347Callback implements IModel347Callback {
		
		@Override
		public void onAccept(Mod347 mod347) {
			// 
		}
		@Override
		public void onCancel() {
			cancel();
		}
		@Override
		public void onSelect(Model347ModuleOptions options,Mod347 mod347, Integer selectedIndexDeclared,  Integer selectedIndexAsset, int tabPanelIndex) {
			select(options,mod347, selectedIndexDeclared, selectedIndexAsset, tabPanelIndex);
		}
		@Override
		public void onNew(Model347ModuleOptions options) {
			newModel(options);
		}
		@Override
		public void cleanErrorPanel() {
			Model347.this.cleanErrorPanel();
		}
		@Override
		public void showError(String msg) {
			Model347.this.showErrorPanel(msg);
		}
		
		@Override
		public void showBreakdownPanel(String htmlText) {
			Model347.this.showBreakdownPanel(htmlText);
		}
		
		@Override
		public void cleanBreakdownPanel() {
			Model347.this.cleanBreakdownPanel();
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

	Model347Table model347Table;
	
	Panel formContainer;
	SimplePanel headerPanel = new SimplePanel();
	
	@Override
	public void onModuleLoad() {
		FISCAL_SERVICE.getAonData(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonData>() {
			
			@Override
			public void onSuccess(AonData aonData) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model347ModuleOptions options = new Model347ModuleOptions();
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
	
	public void onModuleLoad(Model347ModuleOptions options) {
		AON.ensureInjected();

		Model347ServiceAsync serviceRaw = GWT.create(Model347Service.class);
		SERVICE = new Model347ServiceAsyncDecorator(serviceRaw);

		Widget ui = MODEL_347_BINDER.createAndBindUi(this);

		model347Table = new Model347Table(options, new Model347Callback());
		model347Table.addSelectionHandler(new SelectionHandler<Mod347>() {
			
			@Override
			public void onSelection(SelectionEvent<Mod347> event) {
				onSelectionChange(options,event);
			}
		});
		
		declarationContainer.setWidget(model347Table);
		options.getParentWidget().add(ui);
		if (options.getFiscalModelId() != null ) {
			LOGGER.info("Access to Model347 with a ID: " + options.getFiscalModelId());
			onSelect(options,options.getFiscalModelId());
		} else if (options.getNewModel() != null ) {
			LOGGER.info("Access to Model347 new Model");
			newModel(options); 
		} else {
			model347Table.refresh();
			LOGGER.info("Model347 setting NOTIFICATIONS_TAB");
			tabLayout.selectTab(NOTIFICATIONS_TAB);
		}
	}

	private void onSelect(Model347ModuleOptions options, Integer id ) {
		LOGGER.info("OnSelect Model347 with a ID: " + options.getFiscalModelId());
		SERVICE.getMod347(options.getDomainName(), options.getUser(), options.getDomain(), id , new AsyncCallback<Mod347>() {
			@Override
			public void onSuccess(Mod347 selected) {
				if (selected == null) {
					showErrorPanel(AON.MSG.unableToFindDeclaration());
				} else {
					select(options,selected, null, null, 0);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				showErrorPanel(AON.MSG.unableToReadDeclaration(caught.getMessage()));
			}
		});
	}

	private void onSelectionChange(Model347ModuleOptions options,SelectionEvent<Mod347> event) {
		Mod347 sel = event.getSelectedItem();
		SERVICE.getMod347(options.getDomainName(), options.getUser(), options.getDomain(),
				sel.getId(), new AsyncCallback<Mod347>() {
					@Override
					public void onSuccess(Mod347 selected) {
						if (selected == null) {
							showErrorPanel(AON.MSG.unableToFindDeclaration());
						} else {
							select(options,selected, null, null, 0);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(AON.MSG.unableToReadDeclaration(caught.getMessage()));
					}
				} );
	}
	

	private void select(Model347ModuleOptions options, Mod347 selected, Integer selectedIndexDeclared, Integer selectedIndexAsset, int tabPanelIndex) {
		cleanErrorPanel();
		if ( selected.isAEAT() ) {
			declarationContainer.setWidget( new Model347AEAT(options, selected,new Model347Callback(),selectedIndexDeclared,selectedIndexAsset,tabPanelIndex));
		} else if ( selected.isAraba() ) {
			declarationContainer.setWidget( new Model347ARABA(options, selected,new Model347Callback(),selectedIndexDeclared,selectedIndexAsset,tabPanelIndex));			
		} else if ( selected.isBizkaia() ) {
			declarationContainer.setWidget( new Model347BIZKAIA(options, selected,new Model347Callback(),selectedIndexDeclared,selectedIndexAsset,tabPanelIndex));			
		} else if ( selected.isGipuzkoa() ) {
			declarationContainer.setWidget( new Model347GIPUZKOA(options, selected,new Model347Callback(),selectedIndexDeclared,selectedIndexAsset,tabPanelIndex));			
		} else if ( selected.isNavarra() ) {
			declarationContainer.setWidget( new Model347NAVARRA(options, selected,new Model347Callback(),selectedIndexDeclared,selectedIndexAsset,tabPanelIndex));			
		} else {
			showErrorPanel("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}

	private void newModel(Model347ModuleOptions options) {
		cleanErrorPanel();
		SERVICE.initializeMod347(options.getDomainName(), options.getUser(), options.getDomain(), 
				new AsyncCallback<Mod347>() {
					@Override
					public void onSuccess(Mod347 m347) {
						cleanBreakdownPanel();
						tabLayout.selectTab(BREAKDOWN_TAB);
						closeFootPanel();
						showNewDeclarationPopup(options, m347);
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
					}
				});
	}

	private void cancel() {
		cleanErrorPanel();
		declarationContainer.setWidget(model347Table);
		model347Table.refresh();
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
		closeFootPanel();
	}
	
	private void showBreakdownPanel(String htmlText) {
		openFootPanelIfNeeded();
		tabLayout.selectTab(BREAKDOWN_TAB);
		HTMLPanel panel = new HTMLPanel(htmlText);
		breakdownPanel.setWidget(panel);
		breakdownPanel.scrollToTop();
	}
	
	private void showNewDeclarationPopup(Model347ModuleOptions options, Mod347 model) {
		NewDeclarationPopup newDialog = new NewDeclarationPopup( model,
			new Model347Callback() {

					@Override
					public void onAccept(Mod347 model) {
						final PopupPanel popup = new PopupPanel(false, true);
						Label label = new Label(AON.MSG.processing());
						label.addStyleName(AON.AON_CSS.aonTimer());
						popup.add(label);
						popup.setGlassEnabled(true);
						popup.setAnimationEnabled(true);
						popup.center();

						SERVICE.saveMod347(options.getDomainName(),options.getUser(),options.getDomain(),model,
								new AsyncCallback<Mod347>() {
									@Override
									public void onSuccess(Mod347 model) {
										popup.hide();
										select(options,model, null, null, 0);
									}

									@Override
									public void onFailure(Throwable caught) {
										popup.hide();
										showErrorPanel(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
									}
								});
					}
				}
			); 
			newDialog.center();
			newDialog.show();
	}
}

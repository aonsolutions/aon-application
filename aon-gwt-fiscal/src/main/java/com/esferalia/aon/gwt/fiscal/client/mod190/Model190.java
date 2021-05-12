package com.esferalia.aon.gwt.fiscal.client.mod190;

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
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
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

public class Model190 extends MainEntryPoint {
	private static final Logger LOGGER = Logger.getLogger(Model190.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	public static final ProvidesKey<Mod190Detail> MOD190_DETAIL_PROVIDES_KEY = new ProvidesKey<Mod190Detail>() {
		@Override
		public Object getKey(Mod190Detail det) {
			return det == null ?null: det.getId() == null? det.getTempId(): det.getId();
		}
	};

	private final static int NOTIFICATIONS_TAB = 0;
	private final static int BREAKDOWN_TAB = 1;
	
	static Model190ServiceAsync SERVICE;
	final FiscalMSServiceAsync FISCAL_SERVICE = GWT.create(FiscalMSService.class);
	
	interface Model190Binder extends UiBinder<Widget, Model190> {}
	private static final Model190Binder MODEL_190_BINDER = GWT.create(Model190Binder.class);

	protected static interface IModel190Callback{

		void onAccept(Mod190 mod190);
		void onCancel();
		void onSelect(Model190ModuleOptions options,Mod190 mod190, Integer selectedIndex);
		void showError(String msg);
		void cleanErrorPanel();
		void onNew(Model190ModuleOptions options);
	}
	protected class Model190Callback implements IModel190Callback {
		
		@Override
		public void onAccept(Mod190 mod190) {
			// 
		}
		@Override
		public void onCancel() {
			cancel();
		}
		@Override
		public void onSelect(Model190ModuleOptions options, Mod190 mod190, Integer selectedIndex) {
			select(options,mod190, selectedIndex);
		}
		@Override
		public void onNew(Model190ModuleOptions options) {
			newModel(options);
		}
		@Override
		public void cleanErrorPanel() {
			Model190.this.cleanErrorPanel();
		}
		@Override
		public void showError(String msg) {
			Model190.this.showErrorPanel(msg);
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

	Model190Table model190Table;
	
	Panel formContainer;
	SimplePanel headerPanel = new SimplePanel();
	
	@Override
	public void onModuleLoad() {
		FISCAL_SERVICE.getAonData(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonData>() {
			
			@Override
			public void onSuccess(AonData aonData) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model190ModuleOptions options = new Model190ModuleOptions();
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
	
	public void onModuleLoad(Model190ModuleOptions options) {
		AON.ensureInjected();

		Model190ServiceAsync serviceRaw = GWT.create(Model190Service.class);
		SERVICE = new Model190ServiceAsyncDecorator(serviceRaw);

		Widget ui = MODEL_190_BINDER.createAndBindUi(this);

		model190Table = new Model190Table(options, new Model190Callback());
		model190Table.addSelectionHandler(new SelectionHandler<Mod190>() {
			
			@Override
			public void onSelection(SelectionEvent<Mod190> event) {
				onSelectionChange(options,event);
			}
		});
		
		declarationContainer.setWidget(model190Table);

		options.getParentWidget().add(ui);
		if (options.getFiscalModelId() != null ) {
			LOGGER.info("Access to Model190 with a ID: " + options.getFiscalModelId());
			onSelect(options,options.getFiscalModelId());
		} else if (options.getNewModel() != null ) {
			LOGGER.info("Access to Model190 new Model");
			newModel(options, options.getNewModel().getYear()); 
		} else {
			model190Table.refresh();
			LOGGER.info("Model190 setting NOTIFICATIONS_TAB");
			tabLayout.selectTab(NOTIFICATIONS_TAB);
		}

	}

	private void onSelect(Model190ModuleOptions options, Integer id ) {
		LOGGER.info("OnSelect Model190 with a ID: " + options.getFiscalModelId());
		SERVICE.getMod190(options.getDomainName(), options.getUser(), options.getDomain(), id , new AsyncCallback<Mod190>() {
			@Override
			public void onSuccess(Mod190 selected) {
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

	private void onSelectionChange(Model190ModuleOptions options,SelectionEvent<Mod190> event) {
		Mod190 sel = event.getSelectedItem();
		SERVICE.getMod190(options.getDomainName(), options.getUser(), options.getDomain(),
				sel.getId(), new AsyncCallback<Mod190>() {
					@Override
					public void onSuccess(Mod190 selected) {
						if (selected == null) {
							showErrorPanel(AON.MSG.unableToFindDeclaration());
						} else {
							select(options,selected, null);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(AON.MSG.unableToReadDeclaration(caught.getMessage()));
					}
				} );
	}
	

	private void select(Model190ModuleOptions options,Mod190 selected, Integer selectedIndex) {
		cleanErrorPanel();
		if ( selected.isAEAT() ) {
			declarationContainer.setWidget( new Model190AEAT(options, selected,new Model190Callback(),selectedIndex));
		} else if ( selected.isAraba() ) {
			declarationContainer.setWidget( new Model190ARABA(options, selected,new Model190Callback(),selectedIndex));			
		} else if ( selected.isBizkaia() ) {
			declarationContainer.setWidget( new Model190BIZKAIA(options, selected,new Model190Callback(),selectedIndex));			
		} else if ( selected.isGipuzkoa() ) {
			declarationContainer.setWidget( new Model190GIPUZKOA(options, selected,new Model190Callback(),selectedIndex));			
		} else {
			showErrorPanel("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}

	private void newModel(Model190ModuleOptions options) {
		newModel(options, 2020);
	}

	private void newModel(Model190ModuleOptions options, int year) {
		cleanErrorPanel();
		SERVICE.initialize(options.getDomainName(), options.getUser(),options.getDomain(), year,
				new AsyncCallback<Mod190>() {
					@Override
					public void onSuccess(Mod190 m190) {
						cleanBreakdownPanel();
						tabLayout.selectTab(BREAKDOWN_TAB);
						closeFootPanel();
						showNewDeclarationPopup(options,m190);
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
					}
				});
	}

	private void cancel() {
		cleanErrorPanel();
		declarationContainer.setWidget(model190Table);
		model190Table.refresh();
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
		openFootPanelIfNeeded();

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
		tabLayout.selectTab(NOTIFICATIONS_TAB);
	}
	
	private void cleanBreakdownPanel() {
		Widget w = breakdownPanel.getWidget();
		if (w != null) {
			breakdownPanel.remove( breakdownPanel.getWidget() ); 
		}
	}
	
	private void showNewDeclarationPopup(Model190ModuleOptions options, Mod190 model) {
		NewDeclarationPopup newDialog = new NewDeclarationPopup( model,
			new Model190Callback() {

					@Override
					public void onAccept(Mod190 model) {
						final PopupPanel popup = new PopupPanel(false, true);
						Label label = new Label(AON.MSG.processing());
						label.addStyleName(AON.AON_CSS.aonTimer());
						popup.add(label);
						popup.setGlassEnabled(true);
						popup.setAnimationEnabled(true);
						popup.center();

						SERVICE.save(options.getDomainName(), options.getUser(),options.getDomain(),model,
								new AsyncCallback<Mod190>() {
									@Override
									public void onSuccess(Mod190 model) {
										popup.hide();
										select(options,model, null);
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

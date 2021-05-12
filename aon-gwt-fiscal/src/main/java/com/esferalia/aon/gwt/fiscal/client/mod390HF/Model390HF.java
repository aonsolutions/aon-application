package com.esferalia.aon.gwt.fiscal.client.mod390HF;

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
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
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
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model390HF extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Model390HF.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private final static int NOTIFICATIONS_TAB = 0;
	private final static int INFORMATION_TAB = 1;

	protected static Mod390HFServiceAsync MOD_SERVICE;
	protected static final FiscalMSServiceAsync FISCAL_SERVICE = GWT.create(FiscalMSService.class);
	
	interface Mod390HFBinder extends UiBinder<Widget, Model390HF> {
	}
	private static final Mod390HFBinder MODEL_390_BINDER = GWT.create(Mod390HFBinder.class);
	
	@UiField
	SplitLayoutPanel splitLayoutPanel;

	@UiField
	SimpleLayoutPanel declarationContainer;
	
	@UiField
	TabLayoutPanel tabLayout;
	
	@UiField
	ResultsPanel resultsPanel;
	
	@UiField
	MinimizePanel footPanel;
	
	@UiField
	ScrollPanel breakdownPanel;
	
	Model390HFTable model390Table;

	protected interface IModel390HFCallback {

		public void onAccept(Mod390HF mod390);
		public void onCancel();
		public void onNew(Model390HFModuleOptions options);
		public void showBreakdownPanel(String htmlText);
		public void cleanBreakdownPanel();
		public void cleanErrorPanel();
		public void showError(String msg);

	};

	protected class Model390HFCallback implements IModel390HFCallback{

		public void onAccept(Mod390HF mod390) {
			// REDEFINE
		}
		public void onCancel() {
			cancel();
		}
		public void onNew(Model390HFModuleOptions options) {
			Model390HF.this.onNew(options);
		}
		public void showBreakdownPanel(String htmlText) {
			Model390HF.this.showBreakdownPanel(htmlText);
		}
		public void cleanBreakdownPanel() {
			Model390HF.this.cleanBreakdownPanel();
		}
		public void cleanErrorPanel() {
			Model390HF.this.cleanErrorPanel();
		}
		public void showError(String msg) {
			Model390HF.this.showErrorPanel(msg);
		}

	};
	

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();
		LOGGER.info("Access to onModuleLoad");
		FISCAL_SERVICE.getAonData(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonData>() {
			
			@Override
			public void onSuccess(AonData aonData) {
				LOGGER.info("Access to onModuleLoad: aonData get");
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model390HFModuleOptions options = new Model390HFModuleOptions();
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
	
	public void onModuleLoad(Model390HFModuleOptions options) {
		LOGGER.info("Access to onModuleLoad with options");
		AON.ensureInjected();

		Mod390HFServiceAsync modServiceRaw = GWT.create(Mod390HFService.class);
		MOD_SERVICE = new Mod390HFServiceAsyncDecorator(modServiceRaw);

		Widget ui = MODEL_390_BINDER.createAndBindUi(this);

		model390Table = new Model390HFTable(options, new Model390HFCallback());
		model390Table.addSelectionHandler(new SelectionHandler<Mod390HF>() {
			
			@Override
			public void onSelection(SelectionEvent<Mod390HF> event) {
				onSelectionChange(event, options);
			}
		});
		
		declarationContainer.setWidget(model390Table);
		
		options.getParentWidget().add(ui);
		if (options.getFiscalModelId() != null ) {
			LOGGER.info("Access to Model390HF with a ID: " + options.getFiscalModelId());
			onSelect(options.getFiscalModelId(),options);
		} else if (options.getNewModel() != null ) {
			LOGGER.info("Access to Model390HF new Model");
			newModel(options.getNewModel(),options); 
		} else {
			model390Table.refresh();
			LOGGER.info("Model390HF setting NOTIFICATIONS_TAB");
			tabLayout.selectTab(NOTIFICATIONS_TAB);
		}
		tabLayout.setAnimationDuration(300);
		tabLayout.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				openFootPanelIfNeeded();
			}
		});
	}

	private void newModel(Mod390HF newModel,Model390HFModuleOptions options) {
		MOD_SERVICE.initialize(options.getDomainName(),options.getDomain(),options.getUser(),newModel,
				new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF m390HF) {
						tabLayout.selectTab(INFORMATION_TAB);
						closeFootPanel();
						showNewDeclarationPopup( m390HF, options );
					}


					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
					}
				});
	}

	private void onSelect(Integer id,Model390HFModuleOptions options) {
		LOGGER.info("OnSelect Model390HF with a ID: " + options.getFiscalModelId());
		MOD_SERVICE.getMod390HF(options.getDomainName(),options.getDomain(),options.getUser(), id , new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF selected) {
						if (selected == null) {
							LOGGER.info("onSuccess Model390HF with a NULL selected Model ID: ");
							showErrorPanel(AON.MSG.unableToFindDeclaration());
						} else {
							LOGGER.info("onSuccess Model390HF with a ID: " + selected.getId());
							select(selected,options);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(AON.MSG.unableToReadDeclaration(caught.getMessage()));
					}
				});
	}

	private void onSelectionChange(SelectionEvent<Mod390HF> event,Model390HFModuleOptions options) {
		Mod390HF sel = event.getSelectedItem();
		MOD_SERVICE.getMod390HF(options.getDomainName(),options.getDomain(),options.getUser(),
				sel.getId(), new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF selected) {
						if (selected == null) {
							showErrorPanel(AON.MSG.unableToFindDeclaration());
						} else {
							select(selected,options);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(AON.MSG.unableToReadDeclaration(caught.getMessage()));
					}
				});
	}
	
	private void select(Mod390HF selected,Model390HFModuleOptions options) {
		cleanErrorPanel();
		if (selected.isBizkaia() && selected.getYear() >= 2017) {
			declarationContainer.setWidget( new Model3902017BIZKAIA(selected,options,new Model390HFCallback()));
		} else if (selected.isAraba() && selected.getYear() >= 2017) {
			declarationContainer.setWidget( new Model3902017ARABA(selected,options,new Model390HFCallback()));
		} else if (selected.isGipuzkoa() && selected.getYear() >= 2017) {
			declarationContainer.setWidget( new Model3902017GIPUZKOA(selected,options,new Model390HFCallback()));
		} else {
			showErrorPanel("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}

	private void onNew(Model390HFModuleOptions options) {
		cleanErrorPanel();
		MOD_SERVICE.initialize(options.getDomainName(),options.getDomain(),options.getUser(),null,
				new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF m390) {
						cleanBreakdownPanel();
						tabLayout.selectTab(INFORMATION_TAB);
						closeFootPanel();
						showNewDeclarationPopup(m390,options);
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
					}
				});
	}
	
	private void showNewDeclarationPopup(Mod390HF m390,Model390HFModuleOptions options) {
		NewDeclarationPopup<Mod390HF> newDialog = new NewDeclarationPopup<Mod390HF>( options, m390,
			new Model390HFCallback() {

					@Override
					public void onAccept(Mod390HF mod390) {
						final PopupPanel popup = new PopupPanel(false, true);
						Label label = new Label(AON.MSG.processing());
						label.addStyleName(AON.AON_CSS.aonTimer());
						popup.add(label);
						popup.setGlassEnabled(true);
						popup.setAnimationEnabled(true);
						popup.center();

						MOD_SERVICE.create(options.getDomainName(),options.getDomain(),options.getUser(),mod390,
								new AsyncCallback<Mod390HF>() {
									@Override
									public void onSuccess(Mod390HF m390) {
										popup.hide();
										select(m390,options);
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

	private void cancel() {
		cleanErrorPanel();
		cleanBreakdownPanel();
		declarationContainer.setWidget(model390Table);
		model390Table.refresh();
		tabLayout.selectTab(INFORMATION_TAB);
		closeFootPanel();
	}

	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}

	@UiHandler("footPanel")
	void onFootMaximize(MaximizeEvent event) {
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
	private void openFootPanelIfNeeded() {
		if (splitLayoutPanel.getWidgetSize(footPanel) <= 50) {
			openFootPanel();
		}
	}
	
	private void cleanErrorPanel() {
		SimpleLayoutPanel panel = new SimpleLayoutPanel();
		resultsPanel.setWidget(panel);
		closeFootPanel();
	}

	private void showErrorPanel(String msg) {
		openFootPanelIfNeeded();
		tabLayout.selectTab(NOTIFICATIONS_TAB);
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
		resultsPanel.setWidget(panel);
	}
	
	private void cleanBreakdownPanel() {
		Widget w = breakdownPanel.getWidget();
		if (w != null) {
			breakdownPanel.remove( breakdownPanel.getWidget() ); 
		}
	}
	
	private void showBreakdownPanel(String htmlText) {
		openFootPanelIfNeeded();
		tabLayout.selectTab(INFORMATION_TAB);
		HTMLPanel panel = new HTMLPanel(htmlText);
		breakdownPanel.setWidget(panel);
		breakdownPanel.scrollToTop();
	}
	
}

package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.LinkedList;
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
import com.esferalia.aon.gwt.fiscal.client.mod390.e2014.Model3902014;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2018.Model3902018;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model390 extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Model390.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private final static int NOTIFICATIONS_TAB = 0;
	private final static int INFORMATION_TAB = 1;
	private final static int AEAT_TAB = 2;

	public static Model390ServiceAsync MOD390_SERVICE;
	final FiscalServiceAsync impl = GWT.create(FiscalService.class);
	
	interface Model390Binder extends UiBinder<Widget, Model390> {
	}
	private static final Model390Binder MODEL_390_BINDER = GWT.create(Model390Binder.class);
	
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
	
	@UiField
	SimpleLayoutPanel aeatPanel;
	
	Model390Table model390Table;
	
	public class Model390Callback {

		public void onAccept(Mod390 mod390) {
			// REDEFINE
		}
		public void onCancel() {
			cancel();
		}
		public void onNew(Model390ModuleOptions options, int year) {
			Model390.this.onNew( options, year );
		}
		public void showBreakdownPanel(String htmlText) {
			Model390.this.showBreakdownPanel(htmlText);
		}
	
		public void showVisorAEAT() {
			Model390.this.showVisorAEAT();
		}
		
		public void cleanBreakdownPanel() {
			Model390.this.cleanBreakdownPanel();
		}
		public void cleanErrorPanel() {
			Model390.this.cleanErrorPanel();
		}
		public void showError(String msg) {
			Model390.this.showErrorPanel(msg);
		}
		public void showError(LinkedList<Widget> messages) {
			Model390.this.showErrorPanel(messages);
		}
	};
	
	@Override
	public void onModuleLoad() {
		impl.getAonData(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonData>() {
			
			@Override
			public void onSuccess(AonData aonData) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model390ModuleOptions options = new Model390ModuleOptions();
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
	
	public void onModuleLoad(Model390ModuleOptions options) {
		AON.ensureInjected();

		Model390ServiceAsync serviceRaw = GWT.create(Model390Service.class);
		MOD390_SERVICE = new Model390ServiceAsyncDecorator(serviceRaw);

		Widget ui = MODEL_390_BINDER.createAndBindUi(this);
		
		HTMLPanel html = new HTMLPanel("<iframe name='aeatForm' width='100%' height='100%' style='border:none'/>");
		html.setWidth("100%");
		html.setHeight("100%");
		aeatPanel.setWidget(html);
		
		model390Table = new Model390Table(options,new Model390Callback());
		model390Table.addSelectionHandler(new SelectionHandler<Mod390>() {
			
			@Override
			public void onSelection(SelectionEvent<Mod390> event) {
				onSelectionChange(options,event);
			}
		});
		
		declarationContainer.setWidget(model390Table);
		options.getParentWidget().add(ui);
		if (options.getFiscalModelId() != null ) {
			LOGGER.info("Access to Model390 with a ID: " + options.getFiscalModelId());
			onSelect(options,options.getFiscalModelId());
		} else if (options.getNewModel() != null ) {
			LOGGER.info("Access to Model390 new Model");
			onNew(options, options.getNewModel().getYear());
		} else {
			model390Table.refresh();
			LOGGER.info("Model390 setting NOTIFICATIONS_TAB");
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


	private void onSelect(Model390ModuleOptions options, Integer id ) {
		LOGGER.info("OnSelect Model390 with a ID: " + options.getFiscalModelId());
		MOD390_SERVICE.getMod390(options.getDomainName(), options.getDomain(), options.getUser(), id , new AsyncCallback<Mod390>() {
			@Override
			public void onSuccess(Mod390 selected) {
				if (selected == null) {
					showErrorPanel(AON.MSG.unableToFindDeclaration());
				} else {
					select(options, selected);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				showErrorPanel(AON.MSG.unableToReadDeclaration(caught.getMessage()));
			}
		});
	}

	private void onSelectionChange(Model390ModuleOptions options, SelectionEvent<Mod390> event) {
		Mod390 sel = event.getSelectedItem();
		select(options,sel);
	}
	
	private void select(Model390ModuleOptions options, Mod390 selected) {
		cleanErrorPanel();
		if (selected.isAEAT()) {
			if (selected.getYear() == 2013 || selected.getYear() == 2014) {
				Model3902014 model3902014 = new Model3902014(new Model390Callback());
				model3902014.select(selected);
				declarationContainer.setWidget( model3902014 );
			} else if (selected.getYear() == 2015 || selected.getYear() == 2016 || selected.getYear() == 2017) {
				Model3902015 model3902015 = new Model3902015(options, selected,new Model390Callback());
				declarationContainer.setWidget(model3902015);
			}  else if (selected.getYear() == 2018 || selected.getYear() == 2019) {
				Model3902018 model3902018 = new Model3902018(options, selected,new Model390Callback());
				declarationContainer.setWidget(model3902018);
			}  else if (selected.getYear() == 2020) {
				Model3902018 model3902018 = new Model3902018(options, selected,new Model390Callback());
				declarationContainer.setWidget(model3902018);
			} else {
				showErrorPanel("Administraci\u00F3n y/o ejercicio no soportado.");
			}
		} else {
			showErrorPanel("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}

	private void onNew(Model390ModuleOptions options,int year) {
		cleanErrorPanel();
		MOD390_SERVICE.initialize(options.getDomainName(),options.getDomain(),options.getUser(),year,
				new AsyncCallback<Mod390>() {
					@Override
					public void onSuccess(Mod390 m390) {
						cleanBreakdownPanel();
						tabLayout.selectTab(INFORMATION_TAB);
						closeFootPanel();
						showNewDeclarationPopup(options,m390);
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
					}
				});
	}
	
	private void showNewDeclarationPopup(Model390ModuleOptions options,Mod390 m390) {
		cleanErrorPanel();
		cleanBreakdownPanel();
		tabLayout.selectTab(INFORMATION_TAB);
		closeFootPanel();
		NewDeclarationPopup newDialog = new NewDeclarationPopup( m390, new Model390Callback() {

					@Override
					public void onAccept(Mod390 mod390) {
						select(options,m390);

//						final PopupPanel popup = new PopupPanel(false, true);
//						Label label = new Label(AON.MSG.processing());
//						label.addStyleName(AON.AON_CSS.aonTimer());
//						popup.add(label);
//						popup.setGlassEnabled(true);
//						popup.setAnimationEnabled(true);
//						popup.center();
//
//						MOD390_SERVICE.create(getCurrentDomainName(),getCurrentDomain(),mod390,
//								new AsyncCallback<Mod390>() {
//									@Override
//									public void onSuccess(Mod390 m390) {
//										popup.hide();
//										select(m390);
//									}
//
//									@Override
//									public void onFailure(Throwable caught) {
//										popup.hide();
//										showErrorPanel(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
//									}
//								});
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
	
	private void showErrorPanel(LinkedList<Widget> messages) {
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
		
		int row = 0;
		for (Widget message : messages) {
			InlineLabel icon = new InlineLabel("");
			icon.setStyleName(AON.AON_CSS.aonIconPointOrange());
			icon.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
			tab.setWidget(row, 0, icon);
			tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());

			tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
			tab.setWidget(row++, 1, message);	
		}
		tab.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonPanelGridEven());
		
		panel.add(tab);
		resultsPanel.setWidget(panel);
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
	
	private void showVisorAEAT() {
		openFootPanelIfNeeded();
		tabLayout.selectTab(AEAT_TAB);	
	}

	public static void main(String[] args) {
		System.out.println("dd2");
	}
}

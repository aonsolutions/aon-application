package com.esferalia.aon.gwt.fiscal.client.mod180;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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

	public static final ProvidesKey<Mod180Detail> MOD180_DETAIL_PROVIDES_KEY = new ProvidesKey<Mod180Detail>() {
		@Override
		public Object getKey(Mod180Detail det) {
			return det == null ?null: det.getId() == null? det.getTempId(): det.getId();
		}
	};

	private final static int NOTIFICATIONS_TAB = 0;
	private final static int BREAKDOWN_TAB = 1;
	
	static Model180ServiceAsync SERVICE;
	
	interface Model180Binder extends UiBinder<Widget, Model180> {}
	private static final Model180Binder MODEL_180_BINDER = GWT.create(Model180Binder.class);

	protected static interface IModel180Callback{

		void onAccept(Mod180 mod180);
		void onCancel();
		void onSelect(Mod180 mod180, Integer selectedIndex);
		void showError(String msg);
		void cleanErrorPanel();
		void onNew();
		
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
		public void onSelect(Mod180 mod180, Integer selectedIndex) {
			select(mod180, selectedIndex);
		}
		@Override
		public void onNew() {
			newModel();
		}
		@Override
		public void cleanErrorPanel() {
			Model180.this.cleanErrorPanel();
		}
		@Override
		public void showError(String msg) {
			Model180.this.showErrorPanel(msg);
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

	Model180Table model180Table;
	
	Panel formContainer;
	SimplePanel headerPanel = new SimplePanel();
	
	private int domain;
	private int enterprise;

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

		Model180ServiceAsync serviceRaw = GWT.create(Model180Service.class);
		SERVICE = new Model180ServiceAsyncDecorator(serviceRaw);

		Widget ui = MODEL_180_BINDER.createAndBindUi(this);

		model180Table = new Model180Table(new Model180Callback());
		model180Table.addSelectionHandler(new SelectionHandler<Mod180>() {
			
			@Override
			public void onSelection(SelectionEvent<Mod180> event) {
				onSelectionChange(event);
			}
		});
		
		declarationContainer.setWidget(model180Table);
		model180Table.refresh();
		
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);

	}

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	private void onSelectionChange(SelectionEvent<Mod180> event) {
		Mod180 sel = event.getSelectedItem();
		SERVICE.getMod180(getCurrentDomainName(), getCurrentDomain(),
				sel.getId(), new AsyncCallback<Mod180>() {
					@Override
					public void onSuccess(Mod180 selected) {
						if (selected == null) {
							showErrorPanel(AON.MSG.unableToFindDeclaration());
						} else {
							select(selected, null);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(AON.MSG.unableToReadDeclaration(caught.getMessage()));
					}
				} );
	}
	

	private void select(Mod180 selected, Integer selectedIndex) {
		cleanErrorPanel();
		if ( selected.isAEAT() ) {
			declarationContainer.setWidget( new Model1802017AEAT(selected,new Model180Callback(),selectedIndex));
		} else if ( selected.isAraba() ) {
			declarationContainer.setWidget( new Model1802017ARABA(selected,new Model180Callback(),selectedIndex));			
		} else if ( selected.isBizkaia() ) {
			declarationContainer.setWidget( new Model1802017BIZKAIA(selected,new Model180Callback(),selectedIndex));			
		} else if ( selected.isGipuzkoa() ) {
			declarationContainer.setWidget( new Model1802017GIPUZKOA(selected,new Model180Callback(),selectedIndex));			
		} else if ( selected.isNavarra() ) {
			declarationContainer.setWidget( new Model1802017NAVARRA(selected,new Model180Callback(),selectedIndex));			
		} else {
			showErrorPanel("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}

	private void newModel() {
		cleanErrorPanel();
		SERVICE.initializeMod180(getCurrentDomainName(),getCurrentDomain(), 2017,
				new AsyncCallback<Mod180>() {
					@Override
					public void onSuccess(Mod180 m180) {
						cleanBreakdownPanel();
						tabLayout.selectTab(BREAKDOWN_TAB);
						closeFootPanel();
						showNewDeclarationPopup(m180);
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
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
	
	private void showResultsPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 5);
	}

	private boolean isResultsPanelVisible() {
		return splitLayoutPanel.getWidgetSize(footPanel) > 0;
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
	
	private void showNewDeclarationPopup(Mod180 model) {
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

						SERVICE.saveMod180(getCurrentDomainName(),getCurrentDomain(),model,
								new AsyncCallback<Mod180>() {
									@Override
									public void onSuccess(Mod180 model) {
										popup.hide();
										select(model, null);
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

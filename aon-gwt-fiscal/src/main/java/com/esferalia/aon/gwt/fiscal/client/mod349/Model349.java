package com.esferalia.aon.gwt.fiscal.client.mod349;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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

public class Model349 extends MainEntryPoint {
	
	public static final ProvidesKey<Mod349Detail> MOD349_DETAIL_PROVIDES_KEY = new ProvidesKey<Mod349Detail>() { 
		 
		@Override 
		public Object getKey(Mod349Detail det) {
			return det == null ?null: det.getId() == null? det.getTempId(): det.getId();
		}
		
	}; 

	private final static int NOTIFICATIONS_TAB = 0;
	private final static int BREAKDOWN_TAB = 1;
	
	static Model349ServiceAsync SERVICE;
	
	interface Model349Binder extends UiBinder<Widget, Model349> {}
	private static final Model349Binder MODEL_349_BINDER = GWT.create(Model349Binder.class);

	protected static interface IModel349Callback{

		void onAccept(Mod349 mod349);
		void onCancel();
		void onSelect(Mod349 mod349, Integer selectedIndex); 
		void showError(String msg);
		void cleanErrorPanel();
		void onNew();
		void showBreakdownPanel(String htmlText);
		void cleanBreakdownPanel();
		String getDomainName();
		String getUser();
		int getDomain();
		
	}
	
	protected class Model349Callback implements IModel349Callback {
		
		@Override
		public void onAccept(Mod349 mod349) {
			// 
		}
		
		@Override
		public void onCancel() {
			cancel();
		}
		
		@Override
		public void onSelect(Mod349 mod349, Integer selectedIndex) {
			select(mod349, selectedIndex);
		} 		
		
		@Override
		public void onNew() {
			newModel();
		}
		
		@Override
		public void cleanErrorPanel() {
			Model349.this.cleanErrorPanel();
		}
		
		@Override
		public void showError(String msg) {
			Model349.this.showErrorPanel(msg);
		}
		
		@Override
		public void showBreakdownPanel(String htmlText) {
			Model349.this.showBreakdownPanel(htmlText);
		}
		
		@Override
		public void cleanBreakdownPanel() {
			Model349.this.cleanBreakdownPanel();
		}
		@Override
		public String getDomainName() {
			return getCurrentDomainName();
		}
		@Override
		public String getUser() {
			return getCurrentUser();
		}
		@Override
		public int getDomain() {
			return getCurrentDomain();
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

	Model349Table model349Table;
	
	Panel formContainer;
	SimplePanel headerPanel = new SimplePanel();
	
	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

		Model349ServiceAsync serviceRaw = GWT.create(Model349Service.class);
		SERVICE = new Model349ServiceAsyncDecorator(serviceRaw);

		Widget ui = MODEL_349_BINDER.createAndBindUi(this);

		model349Table = new Model349Table(new Model349Callback());
		model349Table.addSelectionHandler(new SelectionHandler<Mod349>() {
			
			@Override
			public void onSelection(SelectionEvent<Mod349> event) {
				onSelectionChange(event);
			}
		});
		
		declarationContainer.setWidget(model349Table);
		model349Table.refresh();
		
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);

	}

	private void onSelectionChange(SelectionEvent<Mod349> event) {
		Mod349 sel = event.getSelectedItem();
		SERVICE.getMod349(getCurrentDomainName(), getCurrentUser(), getCurrentDomain(),
				sel.getId(), new AsyncCallback<Mod349>() {
					@Override
					public void onSuccess(Mod349 selected) {
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
	

	private void select(Mod349 selected, Integer selectedIndex) {
		cleanErrorPanel();
		if ( selected.isAEAT() ) {
			declarationContainer.setWidget( new Model349AEAT(selected,new Model349Callback(),selectedIndex));
		} else if ( selected.isAraba() ) {
			declarationContainer.setWidget( new Model349ARABA(selected,new Model349Callback(),selectedIndex));			
		} else if ( selected.isBizkaia() ) {
			declarationContainer.setWidget( new Model349BIZKAIA(selected,new Model349Callback(),selectedIndex));			
		} else if ( selected.isGipuzkoa() ) {
			declarationContainer.setWidget( new Model349GIPUZKOA(selected,new Model349Callback(),selectedIndex));			
		} else if ( selected.isNavarra() ) {
			declarationContainer.setWidget( new Model349NAVARRA(selected,new Model349Callback(),selectedIndex));			
		} else {
			showErrorPanel("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}

	private void newModel() {
		cleanErrorPanel();
		SERVICE.initializeMod349(getCurrentDomainName(), getCurrentUser(),getCurrentDomain(),
				new AsyncCallback<Mod349>() {
					@Override
					public void onSuccess(Mod349 m349) {
						cleanBreakdownPanel();
						tabLayout.selectTab(BREAKDOWN_TAB);
						closeFootPanel();
						showNewDeclarationPopup(m349);
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
					}
				});
	}

	private void cancel() {
		cleanErrorPanel();
		declarationContainer.setWidget(model349Table);
		model349Table.refresh();
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
	
	private void showNewDeclarationPopup(Mod349 model) {
		NewDeclarationPopup newDialog = new NewDeclarationPopup( model,
			new Model349Callback() {

					@Override
					public void onAccept(Mod349 model) {
						final PopupPanel popup = new PopupPanel(false, true);
						Label label = new Label(AON.MSG.processing());
						label.addStyleName(AON.AON_CSS.aonTimer());
						popup.add(label);
						popup.setGlassEnabled(true);
						popup.setAnimationEnabled(true);
						popup.center();

						SERVICE.saveMod349(getCurrentDomainName(), getCurrentUser(),getCurrentDomain(),model,
								new AsyncCallback<Mod349>() {
									@Override
									public void onSuccess(Mod349 model) {
										popup.hide();
										select(model,null);
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

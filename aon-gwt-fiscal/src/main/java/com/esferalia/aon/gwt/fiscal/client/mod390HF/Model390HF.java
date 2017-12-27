package com.esferalia.aon.gwt.fiscal.client.mod390HF;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
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
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model390HF extends MainEntryPoint {

	private final static int NOTIFICATIONS_TAB = 0;
	private final static int INFORMATION_TAB = 1;

	protected static FiscalServiceAsync FISCAL_SERVICE;
	protected static Mod390HFServiceAsync MOD_SERVICE;
	
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
		public void onNew();
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
		public void onNew() {
			Model390HF.this.onNew();
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

		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		FISCAL_SERVICE = new FiscalServiceAsyncDecorator(fiscalServiceRaw);

		Mod390HFServiceAsync modServiceRaw = GWT.create(Mod390HFService.class);
		MOD_SERVICE = new Mod390HFServiceAsyncDecorator(modServiceRaw);

		Widget ui = MODEL_390_BINDER.createAndBindUi(this);

		tabLayout.setAnimationDuration(300);
		tabLayout.selectTab(NOTIFICATIONS_TAB);
		tabLayout.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				openFootPanelIfNeeded();
			}
		});
		
		model390Table = new Model390HFTable(new Model390HFCallback());
		model390Table.addSelectionHandler(new SelectionHandler<Mod390HF>() {
			
			@Override
			public void onSelection(SelectionEvent<Mod390HF> event) {
				onSelectionChange(event);
			}
		});
		
		declarationContainer.setWidget(model390Table);
		model390Table.refresh();
		
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

	private void onSelectionChange(SelectionEvent<Mod390HF> event) {
		Mod390HF sel = event.getSelectedItem();
		MOD_SERVICE.getMod390HF(getCurrentDomainName(), getCurrentDomain(),
				sel.getId(), new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF selected) {
						if (selected == null) {
							showErrorPanel(AON.MSG.unableToFindDeclaration());
						} else {
							select(selected);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(AON.MSG.unableToReadDeclaration(caught.getMessage()));
					}
				});
	}
	
	private void select(Mod390HF selected) {
		cleanErrorPanel();
		if (selected.isBizkaia()) {
			if (selected.getYear() >= 2017) {
				declarationContainer.setWidget( new Model3902017BIZKAIA(selected,new Model390HFCallback()));
			}
		} else {
			showErrorPanel("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}

	private void onNew() {
		cleanErrorPanel();
		MOD_SERVICE.initialize(getCurrentDomainName(),getCurrentDomain(),null,
				new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF m390) {
						cleanBreakdownPanel();
						tabLayout.selectTab(INFORMATION_TAB);
						closeFootPanel();
						showNewDeclarationPopup(m390);
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
					}
				});
	}
	private void showNewDeclarationPopup(Mod390HF m390) {
		NewDeclarationPopup<Mod390HF> newDialog = new NewDeclarationPopup<Mod390HF>( m390,
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

						MOD_SERVICE.create(getCurrentDomainName(),getCurrentDomain(),mod390,
								new AsyncCallback<Mod390HF>() {
									@Override
									public void onSuccess(Mod390HF m390) {
										popup.hide();
										select(m390);
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

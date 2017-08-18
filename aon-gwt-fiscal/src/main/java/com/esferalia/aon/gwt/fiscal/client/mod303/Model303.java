package com.esferalia.aon.gwt.fiscal.client.mod303;

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
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model303 extends MainEntryPoint {

	private final static int NOTIFICATIONS_TAB = 0;
	private final static int INFORMATION_TAB = 1;

	protected static FiscalServiceAsync fiscalService;
	protected static Mod303ServiceAsync mod303Service;
	
	interface Model303Binder extends UiBinder<Widget, Model303> {
	}
	private static final Model303Binder MODEL_303_BINDER = GWT.create(Model303Binder.class);
	
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
	
	Model303Table model303Table;

	protected class Model303Callback {
		public void onAccept(Mod303 mod303) {
			// REDEFINE
		}
		public void onCancel() {
			cancel();
		}
		public void onNew() {
			Model303.this.onNew();
		}
		public void showBreakdownPanel(String htmlText) {
			Model303.this.showBreakdownPanel(htmlText);
		}
		public void cleanBreakdownPanel() {
			Model303.this.cleanBreakdownPanel();
		}
		public void cleanErrorPanel() {
			Model303.this.cleanErrorPanel();
		}
		public void showError(String msg) {
			Model303.this.showErrorPanel(msg);
		}

	};
	

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);

		Mod303ServiceAsync mod303ServiceRaw = GWT.create(Mod303Service.class);
		mod303Service = new Mod303ServiceAsyncDecorator(mod303ServiceRaw);

		Widget ui = MODEL_303_BINDER.createAndBindUi(this);

		tabLayout.setAnimationDuration(300);
		tabLayout.selectTab(NOTIFICATIONS_TAB);
		tabLayout.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				openFootPanelIfNeeded();
			}
		});
		
		model303Table = new Model303Table(new Model303Callback());
		model303Table.addSelectionHandler(new SelectionHandler<Mod303>() {
			
			@Override
			public void onSelection(SelectionEvent<Mod303> event) {
				onSelectionChange(event);
			}
		});
		
		declarationContainer.setWidget(model303Table);
		model303Table.refresh();
		
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

	private void onSelectionChange(SelectionEvent<Mod303> event) {
		Mod303 sel = event.getSelectedItem();
		mod303Service.getMod303(getCurrentDomainName(), getCurrentDomain(),
				sel.getId(), new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 selected) {
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
	
	private void select(Mod303 selected) {
		cleanErrorPanel();
		if (selected.getAdministration() == Administration.COMMON_TERRITORY) {
			declarationContainer.setWidget( new Model3032017AEAT(selected,new Model303Callback()));
		} else if (selected.getAdministration() == Administration.BIZKAIA) {
			declarationContainer.setWidget( new Model3032017BIZKAIA(selected,new Model303Callback()));
		} else if (selected.getAdministration() == Administration.ALAVA) {
			declarationContainer.setWidget( new Model3032017ARABA(selected,new Model303Callback()));
		} else {
			showErrorPanel("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}

	private void onNew() {
		cleanErrorPanel();
		mod303Service.initializeMod303(getCurrentDomainName(),getCurrentDomain(),null,
				new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 m303) {
						cleanBreakdownPanel();
						tabLayout.selectTab(INFORMATION_TAB);
						closeFootPanel();
						showNewDeclarationPopup(m303);
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
					}
				});
	}
	private void showNewDeclarationPopup(Mod303 m303) {
		NewDeclarationPopup<Mod303> newDialog = new NewDeclarationPopup<Mod303>( m303,
			new Model303Callback() {

					@Override
					public void onAccept(Mod303 mod303) {
						mod303Service.createMod303(getCurrentDomainName(),getCurrentDomain(),mod303,
								new AsyncCallback<Mod303>() {
									@Override
									public void onSuccess(Mod303 m303) {
										select(m303);
									}

									@Override
									public void onFailure(Throwable caught) {
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
		declarationContainer.setWidget(model303Table);
		model303Table.refresh();
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
		SimpleLayoutPanel panel = new SimpleLayoutPanel();
		Label label = new Label(msg);
		label.addStyleName("aon-icon-errorwarning");
		label.addStyleName("aon-message-error");
		label.addStyleName("aon-icon");
		panel.add(label);
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

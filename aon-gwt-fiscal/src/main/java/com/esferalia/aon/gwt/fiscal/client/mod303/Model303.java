package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model303 extends MainEntryPoint {

	private final static int NOTIFICATIONS_TAB = 0;
	private final static int INFORMATION_TAB = 1;
	private final static int AEAT_TAB = 2;

	protected static Mod303ServiceAsync SERVICE;
	final FiscalServiceAsync impl = GWT.create(FiscalService.class);
	
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
	
	@UiField
	SimpleLayoutPanel aeatPanel;
	
	AonData aonData;
	Model303Table model303Table;

	public AonData getAonData() {
		return aonData;
	}
	
	protected interface IModel303Callback {
		public String getDomainName();
		public String getUser();
		public int getDomain();
		
		public void onAccept(Mod303 mod303);
		public void onCancel();
		public void onNew();
		public void showBreakdownPanel(String htmlText);
		public void showVisorAEAT();
		public void cleanBreakdownPanel();
		public void cleanErrorPanel();
		public void showError(String msg);
		public void onTransfer();
	};

	protected class Model303Callback implements IModel303Callback{

		@Override
		public void onAccept(Mod303 mod303) {
			// REDEFINE
		}
		@Override
		public void onCancel() {
			cancel();
		}
		@Override
		public void onNew() {
			Model303.this.onNew();
		}
		@Override
		public void showVisorAEAT() {
			Model303.this.showVisorAEAT();
		}
		@Override
		public void showBreakdownPanel(String htmlText) {
			Model303.this.showBreakdownPanel(htmlText);
		}
		@Override
		public void cleanBreakdownPanel() {
			Model303.this.cleanBreakdownPanel();
		}
		@Override
		public void cleanErrorPanel() {
			Model303.this.cleanErrorPanel();
		}
	
		@Override
		public void showError(String msg) {
			Model303.this.showErrorPanel(msg);
		}
		
		@Override
		public void onTransfer() {
			Model303.this.onTransfer();
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
	

	@Override
	public void onModuleLoad() {
		impl.getAonData(getCurrentDomainName(), getCurrentDomain(), new AsyncCallback<AonData>() {

			@Override public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(AonData aonData) {
				onModuleLoad(aonData);
			}
		});
	}
	
	public void onModuleLoad(AonData aonData) {
		this.aonData = aonData;
		AON.ensureInjected();

		Mod303ServiceAsync mod303ServiceRaw = GWT.create(Mod303Service.class);
		SERVICE = new Mod303ServiceAsyncDecorator(mod303ServiceRaw);

		Widget ui = MODEL_303_BINDER.createAndBindUi(this);
		
		HTMLPanel html = new HTMLPanel("<iframe name='aeatForm' width='100%' height='100%' style='border:none'/>");
		html.setWidth("100%");
		html.setHeight("100%");
		aeatPanel.setWidget(html);
		
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

	private void onSelectionChange(SelectionEvent<Mod303> event) {
		Mod303 sel = event.getSelectedItem();
		SERVICE.getMod303(getCurrentDomainName(), getCurrentUser(), getCurrentDomain(),
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
		if (selected.isAEAT()) {
			if (selected.getYear() < 2018) {
				declarationContainer.setWidget( new Model3032017AEAT(selected,new Model303Callback(), getAonData()));
			} else {
				declarationContainer.setWidget( new Model3032018AEAT(selected,new Model303Callback(), getAonData()));
			}
		} else if (selected.isBizkaia()) {
			if (selected.getYear() < 2017) {
				declarationContainer.setWidget( new Model3032017BIZKAIA(selected,new Model303Callback(), getAonData()));	
			} else { 
				declarationContainer.setWidget( new Model3032017BIZKAIA(selected,new Model303Callback(), getAonData()));
			}
		} else if (selected.isAraba()) {
			if (selected.getYear() < 2019) {
				declarationContainer.setWidget( new Model3032017ARABA(selected,new Model303Callback(), getAonData()));	
			} else {
				declarationContainer.setWidget( new Model3032019ARABA(selected,new Model303Callback(), getAonData()));
			}
		} else if (selected.isGipuzkoa()) {
			if (selected.getYear() < 2017) {
				declarationContainer.setWidget( new Model3032017GIPUZKOA(selected,new Model303Callback(), getAonData()));	
			} else {
				declarationContainer.setWidget( new Model3032017GIPUZKOA(selected,new Model303Callback(), getAonData()));
			}
		} else {
			showErrorPanel("Administraci\u00F3n y/o ejercicio no soportado.");
		}
	}

	private void onTransfer() {
		cleanErrorPanel();
		CustomDialog dialog = new CustomDialog();
		dialog.setCaption(AON.MSG.transferModels());
		dialog.setGlassEnabled(true);
		dialog.setAnimationEnabled(true);
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.AON_CSS.aonScrollArea());
		scroll.setWidth("600px");
		scroll.setHeight("400px");
		FlowPanel flow = new FlowPanel();
		Label text = new Label();
		
		
		text.setText("");
		Label a = new Label("Si continua, se importar\u00E1n las declaraciones realizadas con el programa antiguo.");
		a.setStyleName(AON.AON_CSS.aonMarginTop());
 		flow.add(a);
		Label b = new Label("Se importar\u00E1n tanto las declaraciones de r\u00E9gimen general como las de simplificado.");
		flow.add(b);

		Label b0 = new Label("Si ejecuta m\u00E1s de una vez, los modelos se duplicar\u00E1n.");
		flow.add(b0);

		Label b01 = new Label("Los modelos traspasados se grabar\u00E1n con el estado \"Bloqueado\", de tal forma que si desea realizar alg\u00FAn cambio, deber\u00E1 pulsar en la opci\u00F3n \"Reabrir\"");
		flow.add(b01);

		FlowPanel box = new FlowPanel();
		box.setStyleName(AON.AON_CSS.aonWidth90Percent());
		box.addStyleName(AON.AON_CSS.aonMarginTop());
		box.addStyleName(AON.AON_CSS.aonBlockCenter());
		box.addStyleName(AON.AON_CSS.aonSimpleBorder());
		
		Label b1 = new Label("IMPORTANTE");
		b1.setStyleName(AON.AON_CSS.aonMarginTop());
		b1.addStyleName(AON.AON_CSS.aonBold());
		box.add(b1);
		Label c = new Label("Si realiza el c\u00E1lculo de los impuestos por diferencia:");
		c.setStyleName(AON.AON_CSS.aonMarginTop());
		box.add(c);
		Label d = new Label("* Deber\u00E1 importar los modelos anteriores.");
		d.setStyleName(AON.AON_CSS.aonMarginLeft10());
		box.add(d);
		Label e = new Label("* Deber\u00E1 tener en cuenta que se han producido modificaciones en el c\u00E1lculo del impuesto de tal forma que puede que en algunos casos aparezcan situaciones confusas:");
		e.setStyleName(AON.AON_CSS.aonMarginLeft10());
		box.add(e);
		Label f = new Label("1) En el nuevo modelo no se tienen en cuenta las bases al cero por ciento. El programa antiguo s\u00ED las tiene en cuenta por lo que aparecer\u00E1n las diferencias en las correspondientes casillas.");
		f.setStyleName(AON.AON_CSS.aonMarginLeft20());
		box.add(f);
		Label g = new Label("2) Las facturas recibidas de servicios extracomunitarios, Canarias, Ceuta y Melilla, en el programa nuevo provocan inversi\u00F3n del sujeto pasivo, en el programa antiguo no, por lo que aparecer\u00E1n las diferencias en las correspondientes casillas.");
		g.setStyleName(AON.AON_CSS.aonMarginLeft20());
		box.add(g);
		flow.add(box);
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.AON_CSS.aonPadding());
		buttonsPanel.addStyleName(AON.AON_CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.AON_CSS.aonConfirmDialogOkButton());
		acceptButton.setText( AON.MSG.continueAction());
		
		acceptButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				acceptButton.setEnabled(false);
				SERVICE.importMod303(getCurrentDomainName(), getCurrentUser(), getCurrentDomain(),
						new AsyncCallback<Void>() {
							@Override
							public void onSuccess(Void v) {
								model303Table.refresh();
								dialog.hide();
							}

							@Override
							public void onFailure(Throwable caught) {
								showErrorPanel(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
							}
						});
			}
		});
		buttonsPanel.add(acceptButton);
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.AON_CSS.aonConfirmDialogCancelButton());
    	cancelButton.addStyleName(AON.AON_CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				dialog.hide();
			}
			
		});
		buttonsPanel.add(cancelButton);
		flow.add(buttonsPanel);
		
		flow.add(text);
		scroll.add(flow);
		dialog.add(scroll);
		dialog.center();
		dialog.show();
	}

	private void onNew() {
		cleanErrorPanel();
		SERVICE.initialize(getCurrentDomainName(), getCurrentUser(), getCurrentDomain(),null,
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
						final PopupPanel popup = new PopupPanel(false, true);
						Label label = new Label(AON.MSG.processing());
						label.addStyleName(AON.AON_CSS.aonTimer());
						popup.add(label);
						popup.setGlassEnabled(true);
						popup.setAnimationEnabled(true);
						popup.center();

						SERVICE.create(getCurrentDomainName(), getCurrentUser(), getCurrentDomain(),mod303,
								new AsyncCallback<Mod303>() {
									@Override
									public void onSuccess(Mod303 m303) {
										popup.hide();
										select(m303);
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

	private void showVisorAEAT() {
		openFootPanelIfNeeded();
		tabLayout.selectTab(AEAT_TAB);		
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

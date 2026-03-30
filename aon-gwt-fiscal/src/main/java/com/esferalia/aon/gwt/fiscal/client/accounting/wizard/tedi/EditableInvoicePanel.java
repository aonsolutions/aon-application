package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.InvoiceTransactionListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonFullDocument;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryService;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.ISelectionCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.InvoicePanel.InvoicePanelCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.InvoiceRectificationDataPanel.InvoiceRectificationDataPanelCallback;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceCommunicationIconsPanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceMassagesList;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.invoice.console.InvoiceConsoleTextPanel;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType.InvoiceTransactionTypeVisitor;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EditableInvoicePanel extends SimpleLayoutPanel implements HasSelectionHandlers<AccountingInvoice>,HasAccountEntrySelectionHandlers,Focusable {
	
	private static final String BORDER_RADIUS = "border-radius";
	private static final String DARK_GRAY = "DarkGray";
	private static final String FACTURA = "Factura ";
	private static final String MIN_WIDTH = "min-width";
	private static final Logger LOGGER = Logger.getLogger(EditableInvoicePanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	private static final FinanceServiceAsync FINANCE_SERVICE;
	static {
		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		FINANCE_SERVICE = new FinanceServiceAsyncDecorator(financeServiceRaw);
	}
	private static final AccountEntryServiceAsync ACCOUNT_ENTRY_SERVICE;
	static {
		AccountEntryServiceAsync accountEntryServiceRaw = GWT.create(AccountEntryService.class);
		ACCOUNT_ENTRY_SERVICE = new AccountEntryServiceAsyncDecorator(accountEntryServiceRaw);
	}

	protected static final String INNER_BACKGROUND_COLOR = "inherit";
	protected static final String LABEL_BACKGROUND_COLOR = "#DDD";
	protected static final String TAG_ITEM_BACKGROUND_COLOR = "#DDD";
	protected static final String TAG_ITEM_FOREGROUND_COLOR = "Black";
	protected static final String DUA_BACKGROUND_COLOR = "HoneyDew";
	
	private SimplePanel invoicePanelContainer;
	private AonAccountingRegistryBox registryBox;
	private CheckBox undeductible;
	private InlineLabel invoiceTypeLabel;
	private FocusPanel dropPanel; 	
	private ListBox series;
	private TextBox referenceCode;
	private AonDoubleBox invoiceTotal;
	private AonDateBox taxDate;
	private AonDateBox issueDate;
	private InlineLabel communicationWarningLabel = new InlineLabel(
		 "Las facturas de venta introducidas desde el mantenimiento de "
		+"facturas se marcan como comunicadas externamente.");
	
	private FlowPanel checksLabel;
	private FlowPanel checksTable;
	private boolean checkTableVisible = true;
	private CheckLabel service;
	private CheckLabel rectifier;
	private CheckLabel prepayment;
	private CheckLabel investment;
	private CheckLabel withholding;
	private CheckLabel surcharge;
	private CheckLabel vatAccrualPayment;
	private CheckLabel withholdingFarmer;
	private CheckLabel duaLinked;
	private CheckLabel uossRegime;
	private CheckLabel euossRegime;
	private CheckLabel iossRegime;
	
	
	private InvoiceVATPanel vatPanel;
	private FlowPanel duaPanelContainer;
	private InvoiceDUAPanel duaPanel;
	private InvoiceWithholdingPanel withholdingPanel;
	private InvoiceFinancePanel financePanel;
	
	private InvoicePanelCallback invCallback;
	
	protected static interface IEditableInvoicePanelCallback extends IInvoicePanelCallback {
		void enableInvoiceTotal(boolean b);
		InvoiceVAT getVat(final int vatIdx);
	}
	
	protected class EditableInvoicePanelCallback implements IEditableInvoicePanelCallback {
		
		private IInvoicePanelCallback invoiceCallback;
		
		EditableInvoicePanelCallback( IInvoicePanelCallback invoiceCallback) {
			this.invoiceCallback = invoiceCallback;
		}

		@Override
		public AccountingInvoice getInvoice() {
			return invoiceCallback.getInvoice();
		}

		@Override
		public boolean isInvestAssetsAvailable() {
			return invoiceCallback.isInvestAssetsAvailable();
		}

		@Override
		public void paintEntry() {
			invoiceCallback.paintEntry();
		}

		@Override
		public Occam getOccam() {
			return invoiceCallback.getOccam();
		}

		@Override
		@Deprecated
		public String getCurrentDomainName() {
			return invoiceCallback.getOccam().getDomainName();
		}

		@Override
		@Deprecated
		public int getCurrentDomainId() {
			return invoiceCallback.getOccam().getDomain();
		}

		@Override
		@Deprecated
		public String getCurrentUser() {
			return invoiceCallback.getOccam().getUser();
		}

		@Override
		public AccountEntryModule getModule() {
			return invoiceCallback.getModule();
		}

		@Override
		public AonConfiguration getConfiguration() {
			return invoiceCallback.getConfiguration();
		}
		
		@Override
		public AccountEntryModuleOptions getModuleOptions() {
			return invoiceCallback.getModuleOptions();
		}

		@Override
		public void enableInvoiceTotal(boolean enabled) {
			invoiceTotal.setEnabled(enabled);
		}
		
		@Override
		public InvoiceVAT getVat(final int vatIdx) {
			return getInvoice().getVats().get(vatIdx);
		}

		@Override
		public AccountingRegistry getLastRegistry() {
			return invoiceCallback.getLastRegistry();
		}
	}
	
	private static class CheckLabel extends InlineLabel {

		public CheckLabel(String text) {
			super(text);
		}

		public void paint( boolean checked) {
			this.setStyleName(AON.CSS.aonTabIcon());
			this.addStyleName(AON.CSS.aonClickable());
			this.addStyleName(AON.CSS.aonFlexLabelInner());
			this.addStyleName(AON.CSS.aonDisplayTableCell());
			this.addStyleName(checked?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck());		
		}

	}

	public EditableInvoicePanel(final InvoicePanelCallback invoiceCallback) {
	
		this.invCallback = invoiceCallback; 

		// *************************************************************************
		// ** PANEL ( Titular, Label de factura) ***********************************
		// *************************************************************************

		FlowPanel registryPanel = new FlowPanel();
		registryPanel.setStyleName(AON.CSS.aonPaddingBottom());
		registryPanel.addStyleName(AON.CSS.aonBorderBottom());
		
		FlowPanel regTable = new FlowPanel();
		regTable.setStyleName(AON.CSS.aonFlexBlock());
		regTable.addStyleName(AON.CSS.aonWidthAll());

		registryPanel.add(regTable);
		
		InlineLabel label = new InlineLabel("Titular de la factura");
		label.setStyleName(AON.CSS.aonFlexLabel());
		regTable.add(label);
		
		undeductible = new CheckBox("Gastos no deducibles");
		undeductible.setTabIndex(-1);
		undeductible.setVisible(false);
		undeductible.setValue(false);
		
		registryBox = new AonAccountingRegistryBox(
				invoiceCallback.getModuleOptions()
				,true);
		registryBox.addStyleName(AON.CSS.aonFlexGrow1());
		
		registryBox.addKeyUpHandler( event -> {
			if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
				if ( registryBox.getId() == null) {
					if (invoiceCallback.getLastRegistry() != null) {
						registryBox.set(invoiceCallback.getLastRegistry());	
					}
				} else {
					registryBox.setFocus( true );
				}
			}
		});
		registryBox.addSelectionHandler(event -> {
			final AccountingRegistry ar = event.getSelectedItem();
			undeductible.setValue(false);
			registryChanged(invoiceCallback, ar );
		});
		regTable.add(registryBox);
		
		undeductible.addClickHandler(event -> invoiceTypeChanged(invoiceCallback));
		undeductible.setStyleName(AON.CSS.aonNowrap());
		undeductible.addStyleName(AON.CSS.aonMarginLeft());
		regTable.add(undeductible);
		
		
		FlowPanel labelsPanel = new FlowPanel();
		labelsPanel.setStyleName(AON.CSS.aonFlexBlock());
		labelsPanel.addStyleName(AON.CSS.aonAlignItemsBaseline());
		
		if (invoiceCallback.getInvoice().getInvoice() != null 
			&& invoiceCallback.getInvoice().getInvoice().isDUALinkAllowed() 
			&& invoiceCallback.getInvoice().getDuaNationalInvoice() != null ) {
			AonTableButton duaButton = new AonTableButton("Ver Factura DUA",AON.CSS.aonIconLaunch());
			duaButton.addStyleName(AON.CSS.aonMarginLeft());
			duaButton.addClickHandler(event -> ACCOUNT_ENTRY_SERVICE.getAccountingInvoiceFromInvoice(
				invoiceCallback.getOccam().getDomainName()
				,invoiceCallback.getOccam().getDomain()
				,invoiceCallback.getOccam().getUser()
				,invoiceCallback.getInvoice().getDuaNationalInvoice()
				,new AsyncCallback<AccountingInvoice>() {
				
				@Override
				public void onSuccess(AccountingInvoice result) {
					if (result != null) {
						SelectionEvent.<AccountingInvoice>fire( EditableInvoicePanel.this, result);
					} else {
						invoiceCallback.getModule().onError(AON.MSG.invoiceNotFound());	
					}
				}
				
				@Override
				public void onFailure(Throwable caught) {
					invoiceCallback.getModule().onError(caught.getMessage());
				}
			}));
			labelsPanel.add(duaButton);
			
		}
		
		if (invoiceCallback.getInvoice().getInvoice() != null
			&& invoiceCallback.getInvoice().isDuaLinked() 
			&& invoiceCallback.getInvoice().getDuaInvoice() != null 
			&& invoiceCallback.getInvoice().getDuaInvoice().getAccountingInvoice() != null
			&& invoiceCallback.getInvoice().getDuaInvoice().getAccountingInvoice().getInvoice() != null
				) {
			AonTableButton duaLabel = new AonTableButton("Ver Factura de compra Extracomunitaria",AON.CSS.aonIconLaunch());
			duaLabel.addStyleName(AON.CSS.aonMarginLeft());
			duaLabel.addClickHandler(event -> 
				ACCOUNT_ENTRY_SERVICE.getAccountingInvoiceFromInvoice(
					 invoiceCallback.getOccam().getDomainName()
					,invoiceCallback.getOccam().getDomain()
					,invoiceCallback.getOccam().getUser()
					,invoiceCallback.getInvoice().getDuaInvoice().getAccountingInvoice().getInvoice().getId()
					,new AsyncCallback<AccountingInvoice>() {
					
					@Override
					public void onSuccess(AccountingInvoice result) {
						if (result != null) {
							SelectionEvent.<AccountingInvoice>fire( EditableInvoicePanel.this, result);
						} else {
							invoiceCallback.getModule().onError(AON.MSG.invoiceNotFound());	
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						invoiceCallback.getModule().onError(caught.getMessage());
					}
				})
			);
			labelsPanel.add(duaLabel);
			
		}
		
		invoiceTypeLabel = new InlineLabel();
		labelsPanel.add(invoiceTypeLabel);
		invoiceTypeLabel.setStyleName(AON.CSS.aonInlineBlock());
		invoiceTypeLabel.addStyleName(AON.CSS.aonBold());
		invoiceTypeLabel.addStyleName(AON.CSS.aonFontMedium());
		invoiceTypeLabel.addStyleName(AON.CSS.aonMarginLeftDouble());
		invoiceTypeLabel.addStyleName(AON.CSS.aonMarginRightDouble());
		invoiceTypeLabel.addStyleName(AON.CSS.aonTextRight());
		invoiceTypeLabel.addClickHandler( e -> {
			if (e.isControlKeyDown() && e.isShiftKeyDown()) {
				debugInvoice( invoiceCallback );
			}
		});
		
		decorateInvoiceTypeLabel( invoiceCallback );
		
		if (invoiceCallback.getInvoice().getInvoice() != null && invoiceCallback.getInvoice().getInvoice().getId() != null) {
			
			boolean guest = invoiceCallback.getConfiguration().getUser().hasGuestRole() && !invoiceCallback.getConfiguration().getUser().hasAdminRole();
			
			if (invoiceCallback.getInvoice().getInvoice().getRectificationInvoice() != null 
					&& (invoiceCallback.getInvoice().getInvoice().isRectifier() 
					|| invoiceCallback.getInvoice().getInvoice().isRectified())) {
				
				AonTableButton rectLabel = (invoiceCallback.getInvoice().getInvoice().isRectified())
					?new AonTableButton(AON.MSG.seeRectifierInvoice(),AON.CSS.aonIconLaunch())
					:new AonTableButton(AON.MSG.seeRectifiedInvoice(),AON.CSS.aonIconLaunch());
				rectLabel.addStyleName(AON.CSS.aonMarginLeft());
				rectLabel.addClickHandler(event -> 
					ACCOUNT_ENTRY_SERVICE.getAccountingInvoiceFromInvoice(
						invoiceCallback.getOccam().getDomainName()
						,invoiceCallback.getOccam().getDomain()
						,invoiceCallback.getOccam().getUser()
						,invoiceCallback.getInvoice().getInvoice().getRectificationInvoice()
						,new AsyncCallback<AccountingInvoice>() {
						
						@Override
						public void onSuccess(AccountingInvoice result) {
							if (result != null) {
								SelectionEvent.<AccountingInvoice>fire( EditableInvoicePanel.this, result);
							} else {
								invoiceCallback.getModule().onError(AON.MSG.invoiceNotFound());	
							}
						}
						
						@Override
						public void onFailure(Throwable caught) {
							invoiceCallback.getModule().onError(caught.getMessage());
						}
					})
				);
				labelsPanel.add(rectLabel);
			}
			if (!guest 
				 && !invoiceCallback.getInvoice().getInvoice().isRectifier() 
				 && !invoiceCallback.getInvoice().isUndeductible()) {
					AonTableButton rectify  = new AonTableButton(AON.MSG.rectifyInvoice(),AON.CSS.aonIconSwap());
					rectify.addStyleName(AON.CSS.aonMarginLeft());
					rectify.addClickHandler(event -> {
						final InvoiceRectificationData data = new InvoiceRectificationData();
						data.setIssueDate(invoiceCallback.getInvoice().getInvoice().getIssueDate());
						data.setType(invoiceCallback.getInvoice().getInvoice().getType());
						data.setRectificationtype(RectificationType.NORMAL_RECTIFIER);
						data.setSettleFinances(true);
						final AonCustomDialog dialog = new AonCustomDialog();
						dialog.setCaption(AON.MSG.rectifyInvoice());
						
						final InvoiceRectificationDataPanel rectPanel = new InvoiceRectificationDataPanel();
						rectPanel.show(invoiceCallback
								,data
								, new InvoiceRectificationDataPanelCallback() {
							
							@Override
							public void onCancel() {
								dialog.hide();
							}
							
							@Override
							public void onAccept(InvoiceRectificationData data) {
								ACCOUNT_ENTRY_SERVICE.rectifyInvoice(invoiceCallback.getOccam()
										,invoiceCallback.getInvoice().getInvoice().getId()
										,data
										,new AsyncCallback<AccountingInvoice>() {
									
									@Override
									public void onSuccess(AccountingInvoice result) {
										dialog.hide();
										if (result != null) {
											SelectionEvent.<AccountingInvoice>fire( EditableInvoicePanel.this, result);
										} else {
											invoiceCallback.getModule().onError("Error al rectificar la factura.");	
										}
									}
									
									@Override
									public void onFailure(Throwable caught) {
										dialog.hide();
										invoiceCallback.getModule().onError(caught.getMessage());
									}
								});	
							}
						});
						dialog.add( rectPanel );
						dialog.center();
						dialog.show();
						
						Scheduler.get().scheduleDeferred(() -> rectPanel.setFocus(true));		
						
					});
					labelsPanel.add(rectify);
				}
			
		}
		regTable.add(labelsPanel);
		
		if (invoiceCallback.getInvoice() != null 
			&& invoiceCallback.getInvoice().getInvoice() != null
			&& invoiceCallback.getInvoice().isTediParsed() 
			&& invoiceCallback.getInvoice().hasMessages() ) {
			AonTableButton tediButton  = new AonTableButton("Avisos proceso OCR",AON.CSS.aonIconWarningRed());
			tediButton.addStyleName(AON.CSS.aonMarginRight());
			tediButton.addStyleName(AON.CSS.aonMarginLeft());
			tediButton.addStyleName(AON.CSS.aonBlink());
			tediButton.getElement().getStyle().setColor("red");
			regTable.add(tediButton);

			final AonCustomPopup dialog = new AonCustomPopup();
			dialog.setWidth("600px");
			dialog.setHeight((Window.getClientHeight() - 100) + "px");
			dialog.setCaption( "Avisos proceso OCR");
			InvoiceMassagesList messagesPanel = new InvoiceMassagesList( invoiceCallback.getInvoice().getInvoice() );
			dialog.add( messagesPanel );
			messagesPanel.addClickHandler( event -> dialog.hide() );
			messagesPanel.addKeyUpHandler( event -> {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER || event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialog.hide();
				}
			});
			
			Scheduler.get().scheduleDeferred(() -> {
				dialog.center();
				dialog.show();
				Scheduler.get().scheduleDeferred(() -> messagesPanel.setFocus( true ) );
			});
			
			tediButton.addClickHandler(event -> {
				tediButton.removeStyleName(AON.CSS.aonBlink());
				dialog.center();
				dialog.show();
				Scheduler.get().scheduleDeferred(() -> messagesPanel.setFocus( true ) );
			});
		}
		
		if (invoiceCallback.getInvoice() != null 
			&& invoiceCallback.getInvoice().getInvoice() != null) {
			
			InvoiceModuleOptions invoiceOptions = new InvoiceModuleOptions()
				.setDomainName( invoiceCallback.getOccam().getDomainName() )
				.setDomain( invoiceCallback.getOccam().getDomain() )
				.setUser( invoiceCallback.getOccam().getUser() )
			;
			InvoiceCommunicationIconsPanel communicationIcons = new InvoiceCommunicationIconsPanel( invoiceOptions, invoiceCallback.getInvoice().getInvoice(), false );
			communicationIcons.addStyleName(AON.CSS.aonMarginLeft());
			regTable.add(communicationIcons);
		}
		
		
		AonTableButton helpButton  = new AonTableButton(AON.MSG.help(),AON.CSS.aonIconHelp());
		helpButton.addStyleName(AON.CSS.aonMarginRight());
		helpButton.addStyleName(AON.CSS.aonMarginLeft());
		helpButton.addClickHandler(event -> {
			final AonCustomDialog dialog = new AonCustomDialog();
			dialog.setCaption(AON.MSG.information());
			FlowPanel tabContainer = new FlowPanel();
			tabContainer.addStyleName(AON.CSS.aonPadding());
			FlexTable infoTab = new FlexTable();
			infoTab.addStyleName(AON.CSS.aonTable());
			infoTab.setWidget(0, 0, new Label( "CAMPO" ));
			infoTab.getFlexCellFormatter().addStyleName(0, 0,AON.CSS.aonTableLabel());
			infoTab.setWidget(0, 1, new Label( "TECLAS" ));
			infoTab.getFlexCellFormatter().addStyleName(0, 1,AON.CSS.aonTableLabel());
			infoTab.setWidget(0, 2, new Label( "ACCI\u00D3N" ));
			infoTab.getFlexCellFormatter().addStyleName(0, 2,AON.CSS.aonTableLabel());

			infoTab.setWidget(1, 0, new Label( "Titular"));
			infoTab.setWidget(1, 1, new Label( "F9"));
			infoTab.setWidget(1, 2, new Label( "Si est\u00E1 vacio, repite el \u00FAltimo titular introducido."));
			infoTab.setWidget(2, 0, new Label( "Titular"));
			infoTab.setWidget(2, 1, new Label( "Ctrl+F3"));
			infoTab.setWidget(2, 2, new Label( "Posibilidad de crear o modificar el titular."));
			infoTab.setWidget(3, 0, new Label( "Total"));
			infoTab.setWidget(3, 1, new Label( "F9"));
			infoTab.setWidget(3, 2, new Label( "Repite los valores de la \u00FAltima factura del titular."));
			infoTab.setWidget(4, 0, new Label( "Cuenta"));
			infoTab.setWidget(4, 1, new Label( "Ctrl+F3"));
			infoTab.setWidget(4, 2, new Label( "Posibilidad de crear o modificar la cuenta."));
			
			tabContainer.add( infoTab );
			FlowPanel buttons = new FlowPanel();
			buttons.setStyleName(AON.CSS.aonTextCenter());
			buttons.addStyleName(AON.CSS.aonMarginTop());
			buttons.addStyleName(AON.CSS.aonMarginBottom());
			final Button okButton = new Button();
			okButton.setStyleName(AON.CSS.aonOkButton());
			okButton.setText( AON.MSG.accept());
			okButton.addClickHandler(event1 -> dialog.hide());
			buttons.add(okButton);
			
			tabContainer.add( buttons );
			dialog.add( tabContainer );
			dialog.center();
			dialog.show();
		});
		regTable.add(helpButton);
		
		
		FlowPanel invoiceRootPanel = new FlowPanel();
		invoiceRootPanel.add(registryPanel);
		invoiceRootPanel.getElement().getStyle().setProperty(MIN_WIDTH, "850px");
		
		invoicePanelContainer = new SimplePanel();
		invoicePanelContainer.setStyleName(AON.CSS.aonWidthAll());
		
		invoiceRootPanel.add(invoicePanelContainer);
		
		ScrollPanel rootScrollPanel = new ScrollPanel();
		rootScrollPanel.setStyleName(AON.CSS.aonScrollArea());
		rootScrollPanel.setWidget(invoiceRootPanel);
		
		if ( invoiceCallback.getInvoice() != null && invoiceCallback.getInvoice().getInvoice() != null) {
			undeductible.setValue(invoiceCallback.getInvoice().isUndeductible());
			if (invoiceCallback.getInvoice().getRegistry() != null) {
				invoicePanelContainer.setWidget(editInvoice(invoiceCallback));
				registryBox.setValue(invoiceCallback.getInvoice().getRegistry(),false);
			}
		} else {
			invoicePanelContainer.setWidget( getDropFileZone( invoiceCallback ) );
		}
		
		setStyleName(AON.CSS.aonWidthAll());
		setWidget(rootScrollPanel);
	}

	private void invoiceTypeChanged(InvoicePanelCallback invoiceCallback) {
		if ( invoiceCallback.getInvoice() != null 
			&& invoiceCallback.getInvoice().getRegistry() != null ) {
			final AccountingRegistry ar = invoiceCallback.getInvoice().getRegistry();
			if (undeductible.getValue().booleanValue()) {
				invoiceCallback.getInvoice().getInvoice().setType( InvoiceType.UNDEDUCTIBLE); 
				ar.setType(AccountingRegistryType.UNDED_CREDITOR);
			} else {
				invoiceCallback.getInvoice().getInvoice().setType( InvoiceType.EXPENSES);
				ar.setType(AccountingRegistryType.CREDITOR);
			}
			LOGGER.info("invoiceTypeChanged --> initializeInvoice");
			LOGGER.info("invoiceTypeChanged --> " + ar.getType().getDescription()  + " - " + ar.getType().getInvoiceType().getDescription() );
			initializeInvoice(invoiceCallback, ar, true);
		}
	}

	private void registryChanged(InvoicePanelCallback invoiceCallback, AccountingRegistry ar) {
		
		// NEW invoice
		if (invoiceCallback.getInvoice() == null || invoiceCallback.getInvoice().getInvoice() == null) {
			initializeInvoice(invoiceCallback, ar , false);
			return;
		}
		
		InvoiceType invoiceType = invoiceCallback.getInvoice().getInvoice().getType();
		InvoiceType registryInvoiceType = ar.getType().getInvoiceType();
		
		if (invoiceType == registryInvoiceType) {
			initializeInvoice(invoiceCallback, ar , invoiceCallback.getInvoice().hasTotal());
			return;
		}
		
		if ( invoiceCallback.getInvoice().getInvoice().getId() != null		 // Factura ya grabada 
			&& (invoiceCallback.getInvoice().isDuaLinked()	 				 // Factura DUA
			 || invoiceCallback.getInvoice().getInvoice().isRectified()		 // Factura Rectificada
			 || invoiceCallback.getInvoice().getInvoice().isRectifier())	 // Factura Rectificativa
			) {
			String msg = "";
			if (invoiceCallback.getInvoice().isDuaLinked()) {
				msg = "No se permite el cambio de titular en una factura DUA";	
			}
			if (invoiceCallback.getInvoice().getInvoice().isRectified()) {
				msg = "No se permite el cambio de titular en una factura rectificada";
			}
			if (invoiceCallback.getInvoice().getInvoice().isRectifier()) {
				msg = "No se permite el cambio de titular en una factura rectificativa";
			}
			AonMessageDialog.error(msg, ( ) -> registryBox.setValue( invoiceCallback.getInvoice().getRegistry(), false ));
			return;
		}
		
		AonConfirmDialog.showConfirm( "Va a proceder a un cambio de titular en la factura. "
			+ "La factura actual de de tipo \""
			+ invoiceType.getDescription() 
			+ "\" y el titular seleccionado genera facturas de \"" 
			+ registryInvoiceType.getDescription() 
			+ "\". Si contin\u00FAa, revise la factura.",
			new AonConfirmDialogCallback() {
				@Override
				public void onAccept() {
					initializeInvoice(invoiceCallback, ar , true);
				}
				
				@Override
				public void onCancel() {
					registryBox.setValue( invoiceCallback.getInvoice().getRegistry(), false );
				}
			}
		);
		
	}
	
	protected void initializeInvoice(InvoicePanelCallback invoiceCallback, AccountingRegistry ar, boolean preserveData) {
		AsyncCallback<AccountingInvoice> initializeCallback = new AsyncCallback<AccountingInvoice>() {
			
			@Override
			public void onSuccess(AccountingInvoice result) {
				if ( invoiceCallback.getInvoice().getInvoice() != null) {
					invoiceCallback.getInvoice().getInvoice().getDoc().ifPresent(d -> {
						result.getInvoice().setDoc(d);
						result.setFromRawdoc(invoiceCallback.getInvoice().isFromRawdoc());
					});
				} 
				
				if ( (invoiceCallback.getInvoice().getInvoice() == null || result.isFromRawdoc())
						&& result.getAttach() == null 
						&& invoiceCallback.getInvoice().getAttach() != null) {
					result.setAttach( invoiceCallback.getInvoice().getAttach() );
				}
				
				AccountEntry ae = invoiceCallback.getInvoice().getAccountEntry();
				invoiceCallback.setInvoice(result);
				invoiceCallback.setAccountEntry(ae);
				undeductible.setVisible((result.isExpenses() || result.isUndeductible()) && invoiceCallback.getInvoice().getInvoice().getId() == null);
				
				Account account = new Account();
				account.setId(ar.getAccountId());
				account.setCode(ar.getAccountCode());
				account.setDescription(ar.getAccountDescription());
				invoiceCallback.paintEntry();
				LOGGER.info("initializeInvoice ready To edit!");
				registryBox.setValue(invoiceCallback.getInvoice().getRegistry(),false);
				invoicePanelContainer.setWidget(editInvoice(invoiceCallback));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				invoiceCallback.getModule().onError(caught.getMessage());
			}
		};
		
		if (preserveData) {
			LOGGER.info("initializeInvoice preserving data!");
			LOGGER.info("initializeInvoice preserving data! " + ar.getType().getDescription() + " - " + ar.getType().getInvoiceType().getDescription() );
			ACCOUNT_ENTRY_SERVICE.initializeInvoice(invoiceCallback.getOccam(),ar,invoiceCallback.getInvoice(),true, initializeCallback);
		} else {
			LOGGER.info("initializeInvoice NOT preserving data!");
			LOGGER.info("initializeInvoice NOT preserving data! " + ar.getType().getDescription() + " - " + ar.getType().getInvoiceType().getDescription() );
			ACCOUNT_ENTRY_SERVICE.initializeInvoice(invoiceCallback.getOccam(),ar,invoiceCallback.getModule().getActivity(),invoiceCallback.getModule().getEntryDate(), initializeCallback);
		}
	}

	protected void initializeInvoice(InvoicePanelCallback invoiceCallback, AccountingRegistry ar) {
		ACCOUNT_ENTRY_SERVICE.initializeInvoice(
				invoiceCallback.getOccam()
				,ar
				,invoiceCallback.getModule().getActivity()
				,invoiceCallback.getModule().getEntryDate()
				,new AsyncCallback<AccountingInvoice>() {
					
					@Override
					public void onSuccess(AccountingInvoice result) {
						invoiceCallback.getInvoice().getInvoice().getDoc().ifPresent(d -> {
							result.getInvoice().setDoc(d);
						});
						AccountEntry ae = invoiceCallback.getInvoice().getAccountEntry();
						invoiceCallback.setInvoice(result);
						invoiceCallback.setAccountEntry(ae);
						undeductible.setVisible((result.isExpenses() || result.isUndeductible()) && invoiceCallback.getInvoice().getInvoice().getId() == null);
						
						Account account = new Account();
						account.setId(ar.getAccountId());
						account.setCode(ar.getAccountCode());
						account.setDescription(ar.getAccountDescription());
						invoiceCallback.paintEntry();
						invoicePanelContainer.setWidget(editInvoice(invoiceCallback));
					}
					
					@Override
					public void onFailure(Throwable caught) {
						invoiceCallback.getModule().onError(caught.getMessage());
					}
				});
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<AccountingInvoice> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
	@Override
	public HandlerRegistration addSelectionHandler(AccountEntrySelectionHandler handler) {
		return super.addHandler(handler, AccountEntrySelectionEvent.getType());
	}

	private void decorateIssueDate(AccountingInvoice ai) {
		Date issue = ai.getInvoice().getIssueDate();
		Date entry = ai.getAccountEntry().getEntryDate();
		if(    (issue == null && entry != null)
			|| (issue != null && entry == null)
			|| (issue != null && issue.compareTo(entry) != 0)) {
			issueDate.addStyleName(AON.CSS.aonBackgroundOrange());
			issueDate.setTitle("La fecha de emisi\u00F3n no coincide con la fecha del apunte" );
		} else {
			issueDate.removeStyleName(AON.CSS.aonBackgroundOrange());
			issueDate.setTitle("" );
		}
	}

	private void decorateTaxDate(AccountingInvoice ai) {
		Date issue = ai.getInvoice().getIssueDate();
		Date tax = ai.getInvoice().getTaxDate();
		if(    (issue == null && tax != null)
			|| (issue != null && tax == null)
			|| (issue != null && issue.compareTo(tax) != 0)) {
			taxDate.addStyleName(AON.CSS.aonBackgroundOrange());
			taxDate.setTitle("La fecha IVA no coincide con la fecha de emisi\u00F3n " );
		} else {
			taxDate.removeStyleName(AON.CSS.aonBackgroundOrange());
			taxDate.setTitle("" );
		}
	}

	protected void headerDataChanged(IInvoicePanelCallback invoiceCallback) {
		vatPanel.headerInfoChanged(new EditableInvoicePanelCallback(invoiceCallback));
		withholdingPanel.setVisible(invoiceCallback.getInvoice().isWithholding());
		withholdingPanel.setValue(invoiceCallback.getInvoice().getWithholdingData());
		invoiceTotal.setValue(invoiceCallback.getInvoice().getTotalInvoice(),false);
		invoiceCallback.paintEntry();
		invoiceCallback.getInvoice().getAccountEntry().setDirty(true);
		invoiceCallback.getModule().refreshIdLabel();
	}

	private void decorateInvoiceTypeLabel( IInvoicePanelCallback invoiceCallback ) {
		AccountingInvoice ai = invoiceCallback.getInvoice();
		InvoiceType invoiceType = null;
		RectificationType rt = null;
		if (ai != null && ai.getInvoice() != null) {
			invoiceType = ai.getInvoice().getType();
			rt = ai.getInvoice().getRectificationType();
		}
		String x = "";
		if (rt == null || rt == RectificationType.NONE) {
			x = "";
		} else {
			x = rt.getDescription() + " ";
		}
		String l = "";
		if (invoiceType == InvoiceType.SALES   ) l = FACTURA+x+"de Ventas";
		else if (invoiceType == InvoiceType.EXPENSES) l = FACTURA+x+"de Gastos";
		else if (invoiceType == InvoiceType.PURCHASE) l = FACTURA+x+"de Compra";
		else if (invoiceType == InvoiceType.UNDEDUCTIBLE) l = "Ticket/Gasto no Ded.";
		else l = "Nueva factura"; 
		l = l + (ai.isInvestment()?". (Inv)":"");
		l = l + (ai.isVatAccrualPayment()?". (Crit.Caja)":"");
		invoiceTypeLabel.setText(l);
		
		if (checksLabel != null) {
			checksLabel.clear();
			if (rt != null && rt != RectificationType.NONE) {
				InlineLabel l3 = new InlineLabel(rt.getDescription());
				l3.setStyleName(AON.CSS.aonTagItem());
				l3.getElement().getStyle().setColor(TAG_ITEM_FOREGROUND_COLOR);
				l3.getElement().getStyle().setBackgroundColor(TAG_ITEM_BACKGROUND_COLOR);
				checksLabel.add(l3);
			}
			
			if (ai.isService()) {
				InlineLabel l3 = new InlineLabel("Servicio");
				l3.setStyleName(AON.CSS.aonTagItem());
				l3.getElement().getStyle().setColor(TAG_ITEM_FOREGROUND_COLOR);
				l3.getElement().getStyle().setBackgroundColor(TAG_ITEM_BACKGROUND_COLOR);
				checksLabel.add(l3);
			}
	
			if (ai.isInvestment()) {
				InlineLabel l3 = new InlineLabel("Inversi\u00F3n");
				l3.setStyleName(AON.CSS.aonTagItem());
				l3.getElement().getStyle().setColor(TAG_ITEM_FOREGROUND_COLOR);
				l3.getElement().getStyle().setBackgroundColor(TAG_ITEM_BACKGROUND_COLOR);
				checksLabel.add(l3);
			}
			
			if (ai.isWithholding()) {
				InlineLabel l3 = new InlineLabel("IRPF");
				l3.setStyleName(AON.CSS.aonTagItem());
				l3.getElement().getStyle().setColor(TAG_ITEM_FOREGROUND_COLOR);
				l3.getElement().getStyle().setBackgroundColor(TAG_ITEM_BACKGROUND_COLOR);
				checksLabel.add(l3);
			}
			
			if (ai.isSurcharge()) {
				InlineLabel l3 = new InlineLabel("R.E.");
				l3.setStyleName(AON.CSS.aonTagItem());
				l3.getElement().getStyle().setColor(TAG_ITEM_FOREGROUND_COLOR);
				l3.getElement().getStyle().setBackgroundColor(TAG_ITEM_BACKGROUND_COLOR);
				checksLabel.add(l3);
			}
	
			if (ai.isWithholdingFarmer()) {
				InlineLabel l3 = new InlineLabel("Reg. Agr.");
				l3.setStyleName(AON.CSS.aonTagItem());
				l3.getElement().getStyle().setColor(TAG_ITEM_FOREGROUND_COLOR);
				l3.getElement().getStyle().setBackgroundColor(TAG_ITEM_BACKGROUND_COLOR);
				checksLabel.add(l3);
			}
	
			if (ai.isVatAccrualPayment()) {
				InlineLabel l3 = new InlineLabel("Crit.Caja");
				l3.setStyleName(AON.CSS.aonTagItem());
				l3.getElement().getStyle().setColor(TAG_ITEM_FOREGROUND_COLOR);
				l3.getElement().getStyle().setBackgroundColor(TAG_ITEM_BACKGROUND_COLOR);
				checksLabel.add(l3);
			}
	
			if (ai.isDuaLinked()) {
				InlineLabel l3 = new InlineLabel("DUA");
				l3.setStyleName(AON.CSS.aonTagItem());
				l3.getElement().getStyle().setColor(TAG_ITEM_FOREGROUND_COLOR);
				l3.getElement().getStyle().setBackgroundColor(TAG_ITEM_BACKGROUND_COLOR);
				checksLabel.add(l3);
			}
			
			if (ai.hasPrepayments()) {
				InlineLabel l3 = new InlineLabel("Suplidos");
				l3.setStyleName(AON.CSS.aonTagItem());
				l3.getElement().getStyle().setColor(TAG_ITEM_FOREGROUND_COLOR);
				l3.getElement().getStyle().setBackgroundColor(TAG_ITEM_BACKGROUND_COLOR);
				checksLabel.add(l3);
			}
			
			if (ai.isVatImportation()) {
				InlineLabel l3 = new InlineLabel("Reg. Import. (IOSS)");
				l3.setStyleName(AON.CSS.aonTagItem());
				l3.getElement().getStyle().setColor(TAG_ITEM_FOREGROUND_COLOR);
				l3.getElement().getStyle().setBackgroundColor(TAG_ITEM_BACKGROUND_COLOR);
				checksLabel.add(l3);
			}

			if (ai.isVatUnion()) {
				InlineLabel l3 = new InlineLabel("Reg. Uni\u00F3n (UOSS).");
				l3.setStyleName(AON.CSS.aonTagItem());
				l3.getElement().getStyle().setColor(TAG_ITEM_FOREGROUND_COLOR);
				l3.getElement().getStyle().setBackgroundColor(TAG_ITEM_BACKGROUND_COLOR);
				checksLabel.add(l3);
			}

			if (ai.isVatUnionExternal()) {
				InlineLabel l3 = new InlineLabel("Reg. Exterior Uni\u00F3n (EUOSS).");
				l3.setStyleName(AON.CSS.aonTagItem());
				l3.getElement().getStyle().setColor(TAG_ITEM_FOREGROUND_COLOR);
				l3.getElement().getStyle().setBackgroundColor(TAG_ITEM_BACKGROUND_COLOR);
				checksLabel.add(l3);
			}
			
			AonTableButton moreData = new AonTableButton(AON.MSG.invoiceParams(),AON.CSS.aonIconMoreVertical());
			moreData.addClickHandler(event -> {
				checksTable.setVisible( !checksTable.isVisible() );
				checkTableVisible = checksTable.isVisible(); 
			});
			checksLabel.add(moreData);	
		}
			

	}

	private void repeatLastInvoice(InvoicePanelCallback invoiceCallback, final ISelectionCallback cbk) {
		Integer registryId = invoiceCallback.getInvoice().getRegistry().getId();
		ACCOUNT_ENTRY_SERVICE.getRegistryLastAccountingInvoice(
			invoiceCallback.getOccam().getDomainName()
			,invoiceCallback.getOccam().getDomain()
			,invoiceCallback.getOccam().getUser()
			,registryId
				,new AsyncCallback<AccountingInvoice>() {
						@Override
						public void onSuccess(AccountingInvoice result) {
							if (result == null) {
								invoiceCallback.getModule().onError("No se ha encontrado ninguna factura");
							} else {
								result.setAccountEntry(invoiceCallback.getInvoice().getAccountEntry());
								result.getInvoice().setIssueDate(invoiceCallback.getModule().getEntryDate());
								result.getInvoice().setTaxDate(invoiceCallback.getModule().getEntryDate());
								if (invoiceCallback.hasCommunication() || !result.getInvoice().isSales()) {
									result.getInvoice().setReferenceCode(referenceCode.getValue());
								}
								// TODO Manage due dates for all finances.
								result.getInvoice().getUniqueFinance()
									.ifPresent(f -> f.setDueDate(invoiceCallback.getModule().getEntryDate()));
								result.getInvoice().setId(null);
								SelectionEvent.<AccountingInvoice>fire( EditableInvoicePanel.this, result);
								
							}
							if (cbk != null) {
								cbk.onSuccess();
							}							
						}
						
						@Override
						public void onFailure(Throwable caught) {
							invoiceCallback.getModule().onError("No se ha encontrado ninguna factura");
						}
			});
	}

	private FlowPanel editInvoice(InvoicePanelCallback invoiceCallback) {
		AccountingInvoice inv = invoiceCallback.getInvoice();
		
		undeductible.setVisible((inv.isUndeductible() || inv.isExpenses()) && inv.getInvoice().getId() == null);
		
		AonFullDocument fullDocument = new AonFullDocument();
		TextBox rName = new TextBox();
		taxDate = new AonDateBox();
		issueDate = new AonDateBox();
		ListBox workplaces = new ListBox();
		InvoiceTransactionListBox transactionBox = new InvoiceTransactionListBox( isDefaultAdmonCanarias( invoiceCallback ) );
		
		AonIntegerBox number = new AonIntegerBox();
		
		checksTable = new FlowPanel();
		checksLabel = new FlowPanel();
		service = new CheckLabel(AON.MSG.service());
		rectifier = new CheckLabel(AON.MSG.rectifiedInvoice());
		prepayment = new CheckLabel(AON.MSG.hasPrepayments());
		investment = new CheckLabel(AON.MSG.investAsset());
		withholding = new CheckLabel(AON.MSG.withholding());
		surcharge = new CheckLabel(AON.MSG.surcharge());
		vatAccrualPayment = new CheckLabel(AON.MSG.vatAccrualPaymentAbbr());
		withholdingFarmer = new CheckLabel(AON.MSG.withholdingFarmerAbbr());
		duaLinked = new CheckLabel("DUA");
		uossRegime = new CheckLabel("Reg. Uni\u00F3n (UOSS)");
		euossRegime = new CheckLabel("Reg. Exterior Uni\u00F3n (EUOSS)");
		iossRegime = new CheckLabel("Reg. Import. (IOSS)");
		
		TextBox manualConcept = new TextBox();
		
		invoiceCallback.getModule().getEntryDateBox().addValueChangeHandler(event -> {
			invoiceCallback.getInvoice().getAccountEntry().setEntryDate(event.getValue());
			if (invoiceCallback.getInvoice().getInvoice() != null) {
				invoiceCallback.getInvoice().getInvoice().setIssueDate(event.getValue());
				invoiceCallback.getInvoice().getInvoice().setTaxDate(event.getValue());
				issueDate.setValue(event.getValue());
				taxDate.setValue(event.getValue());
				decorateIssueDate( invoiceCallback.getInvoice() );
				decorateTaxDate( invoiceCallback.getInvoice() );
				invoiceCallback.paintEntry();
				financePanel.invoiceDateIssueChanged(invoiceCallback);
			}
		});
		invoiceCallback.getModule().getActivityBox().addChangeHandler( event -> {
			Integer act = AonNumberUtils.toInteger(invoiceCallback.getModule().getActivityBox().getSelectedValue());
			invoiceCallback.getInvoice().getAccountEntry().setActivity(act);
			invoiceCallback.getInvoice().getInvoice().setActivity(new EnterpriseActivity().setId(act));
		});
		invoiceCallback.getModule().getConfidentialBox().addClickHandler( event -> {
			boolean conf = invoiceCallback.getModule().getConfidentialBox().getValue();
			invoiceCallback.getInvoice().getAccountEntry().setConfidential(conf);
			invoiceCallback.getInvoice().getInvoice().setConfidential(conf);
		});

		fullDocument.setValue(inv.getInvoice().getRegistryDocumentType(),inv.getInvoice().getRegistryDocumentCountry(),inv.getInvoice().getRegistryDocument());
		rName.setValue(inv.getInvoice().getRegistryName());
		taxDate.setValue(inv.getInvoice().getTaxDate());
		issueDate.setValue(inv.getInvoice().getIssueDate());
		transactionBox.setValue(inv.getTransaction());
		
		series = new ListBox();
		referenceCode = new TextBox();
		
		if (!invoiceCallback.hasCommunication() && inv.isSales()) {
			fillSeriesWidget(invoiceCallback, inv );
			number.setValue(inv.getInvoice().getNumber());
		}
		
		referenceCode.setValue(inv.getInvoice().getReferenceCode());
		
		series.setVisible( inv.isSales() );
		number.setVisible( inv.isSales() );
		
		invoiceTotal = new AonDoubleBox();
		invoiceTotal.setValue(inv.getTotalInvoice());
		manualConcept.setValue(inv.getManualConcept());
		
		vatPanel = new InvoiceVATPanel( new EditableInvoicePanelCallback(invoiceCallback) );
		
		financePanel = new InvoiceFinancePanel(invoiceCallback);
		financePanel.addSelectionHandler(event -> AccountEntrySelectionEvent.fire( EditableInvoicePanel.this, event.getSelectedItem(), null));

		FlowPanel invoicePanel = new FlowPanel();
		invoicePanel.setStyleName(AON.CSS.aonWidthAll());
		
		// *************************************************************************
		// ** PANEL ( Datos de la factura) *****************************************
		// *************************************************************************
		FlowPanel invoiceDataTableDiv = new FlowPanel();
		invoiceDataTableDiv.setStyleName(AON.CSS.aonDisplayTable());
		invoiceDataTableDiv.addStyleName(AON.CSS.aonWidthAll());
		invoiceDataTableDiv.addStyleName(AON.CSS.aonMarginTopSep());
		invoiceDataTableDiv.getElement().getStyle().setBackgroundColor(INNER_BACKGROUND_COLOR);

		FlowPanel invoiceDataTableRowDiv = new FlowPanel();
		invoiceDataTableRowDiv.setStyleName(AON.CSS.aonDisplayTableRow());
		invoiceDataTableDiv.add(invoiceDataTableRowDiv);
		
		// *************************************************************************
		// ***************** PANEL ( documento y nombre del titular ) **************
		// *************************************************************************
		
		FlowPanel invoiceDataTableCellDiv0 = new FlowPanel();
		invoiceDataTableCellDiv0.setStyleName(AON.CSS.aonDisplayTableCell());
		invoiceDataTableCellDiv0.addStyleName(AON.CSS.aonTextVerticalContainer());
		invoiceDataTableCellDiv0.getElement().getStyle().setWidth(40, Unit.PX);
		invoiceDataTableCellDiv0.getElement().getStyle().setProperty(MIN_WIDTH, 40, Unit.PX);
		invoiceDataTableCellDiv0.getElement().getStyle().setBackgroundColor(LABEL_BACKGROUND_COLOR);
		invoiceDataTableCellDiv0.getElement().getStyle().setBorderColor(DARK_GRAY);
		invoiceDataTableCellDiv0.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		invoiceDataTableCellDiv0.getElement().getStyle().setBorderWidth(1, Unit.PX);
		invoiceDataTableCellDiv0.getElement().getStyle().setProperty(BORDER_RADIUS, 10, Unit.PCT);
		invoiceDataTableRowDiv.add(invoiceDataTableCellDiv0);

		Label description = new Label("Datos");
		description.setStyleName(AON.CSS.aonTextVertical());
		description.addStyleName(AON.CSS.aonBold());
		invoiceDataTableCellDiv0.add(description);
		
		FlowPanel invoiceDataTableCellDiv1 = new FlowPanel();
		invoiceDataTableCellDiv1.setStyleName(AON.CSS.aonDisplayTableCell());
		invoiceDataTableRowDiv.add(invoiceDataTableCellDiv1);
		
		FlowPanel invoiceDataInnerTableDiv= new FlowPanel();
		invoiceDataInnerTableDiv.setStyleName(AON.CSS.aonDisplayTable());
		invoiceDataInnerTableDiv.addStyleName(AON.CSS.aonWidthAll());
		invoiceDataTableCellDiv1.add(invoiceDataInnerTableDiv);
		
		FlowPanel invoiceDataInnerTableRowDiv1 = new FlowPanel();
		invoiceDataInnerTableRowDiv1.setStyleName(AON.CSS.aonDisplayTableRow());
		invoiceDataInnerTableDiv.add(invoiceDataInnerTableRowDiv1);

		FlowPanel headerPanel1 = new FlowPanel();
		headerPanel1.setStyleName(AON.CSS.aonDisplayTableCell());
		invoiceDataInnerTableRowDiv1.add(headerPanel1);
		
		InlineLabel documentLabel = new InlineLabel(AON.MSG.document());
		documentLabel.setStyleName(AON.CSS.aonFlexLabel());
		headerPanel1.add(documentLabel);
		
		
		fullDocument.addTypeChangeHandler(arg0 -> invoiceCallback.getInvoice().getInvoice().setRegistryDocumentType(fullDocument.getType()));
		fullDocument.addCountryChangeHandler(arg0 -> {
			invoiceCallback.getInvoice().getInvoice().setRegistryDocumentCountry(fullDocument.getCountry());
			InvoiceCalculator.calculate(invoiceCallback.getInvoice());
			enableChecks(invoiceCallback);
			headerDataChanged(invoiceCallback);
		});
		fullDocument.addDocumentChangeHandler(arg0 -> invoiceCallback.getInvoice().getInvoice().setRegistryDocument(fullDocument.getDocument()));
		headerPanel1.add(fullDocument);
		
		InlineLabel nameLabel = new InlineLabel(AON.MSG.name());
		nameLabel.setStyleName(AON.CSS.aonFlexLabel());
		headerPanel1.add(nameLabel);
		
		rName.setStyleName(AON.CSS.aonInputText());
		rName.setVisibleLength(50);
		rName.setMaxLength(50);
		rName.addValueChangeHandler(arg0 -> {
			invoiceCallback.getInvoice().getInvoice().setRegistryName(rName.getValue());
			if (AonStringUtils.isBlank(invoiceCallback.getInvoice().getManualConcept())) {
				invoiceCallback.getInvoice().setManualConcept( rName.getValue());
				headerDataChanged(invoiceCallback);
			}
		});
		headerPanel1.add(rName);
		
		if (invoiceCallback.hasCommunication() && inv.getInvoice().getId() == null && inv.isSales()) {
			communicationWarningLabel.setStyleName(AON.CSS.aonLabelWithIcon());
			communicationWarningLabel.addStyleName(AON.CSS.aonIconWarning());
			communicationWarningLabel.addStyleName(AON.CSS.aonMarginLeft());
			communicationWarningLabel.addStyleName(AON.CSS.aonPadding());
			communicationWarningLabel.addStyleName(AON.CSS.aonBold());
			headerPanel1.add(communicationWarningLabel);
		}
		
		
		// *************************************************************************
		// ***************** PANEL ( Fecha IVA, set de checks 1) *******************
		// *************************************************************************

		FlowPanel invoiceDataInnerTableRowDiv2 = new FlowPanel();
		invoiceDataInnerTableRowDiv2.setStyleName(AON.CSS.aonDisplayTableRow());
		invoiceDataInnerTableDiv.add(invoiceDataInnerTableRowDiv2);

		FlowPanel headerPanel2 = new FlowPanel();
		headerPanel2.setStyleName(AON.CSS.aonDisplayTableCell());
		invoiceDataInnerTableRowDiv2.add(headerPanel2);
		
		InlineLabel issueDateLabel = new InlineLabel(AON.MSG.issueDate());
		issueDateLabel.setStyleName(AON.CSS.aonFlexLabel());
		issueDateLabel.getElement().getStyle().setWidth(80, Unit.PX);
		issueDateLabel.setVisible(!invoiceCallback.getInvoice().isUndeductible());
		headerPanel2.add(issueDateLabel);
		
		issueDate.addValueChangeHandler(event -> {
			invoiceCallback.getInvoice().getInvoice().setIssueDate(event.getValue());
			invoiceCallback.getInvoice().getInvoice().setTaxDate(event.getValue());
			taxDate.setValue(event.getValue(), false);
			decorateIssueDate(invoiceCallback.getInvoice());
			decorateTaxDate(invoiceCallback.getInvoice());
		});
		headerPanel2.add(issueDate);

		InlineLabel taxDateLabel = new InlineLabel(AON.MSG.taxDate());
		taxDateLabel.setStyleName(AON.CSS.aonFlexLabel());
		taxDateLabel.getElement().getStyle().setWidth(80, Unit.PX);
		taxDateLabel.setVisible(!invoiceCallback.getInvoice().isUndeductible());
		headerPanel2.add(taxDateLabel);
		
		taxDate.addValueChangeHandler(event -> {
			invoiceCallback.getInvoice().getInvoice().setTaxDate(event.getValue());
			decorateTaxDate(invoiceCallback.getInvoice());
		});
		taxDate.setVisible(!invoiceCallback.getInvoice().isUndeductible());
		headerPanel2.add(taxDate);
		
		// -------------------------------
		// --------- TRANSACTION ---------
		// -------------------------------
		InlineLabel transactionLabel = new InlineLabel(AON.MSG.transaction());
		transactionLabel.setStyleName(AON.CSS.aonFlexLabel());
		transactionLabel.setVisible(!invoiceCallback.getInvoice().isUndeductible());
		headerPanel2.add(transactionLabel);
		
		transactionBox.addChangeHandler(event -> {
			invoiceCallback.getInvoice().getInvoice().setTransaction(transactionBox.getValue());
			InvoiceCalculator.calculate(invoiceCallback.getInvoice());
			enableChecks(invoiceCallback);
			headerDataChanged(invoiceCallback);
		});
		transactionBox.setVisible(!invoiceCallback.getInvoice().isUndeductible());
		headerPanel2.add(transactionBox);

		// -------------------------------
		// --------- WORKPLACE ---------
		// -------------------------------
		if (invoiceCallback.getConfiguration().getWorkplaces() != null && invoiceCallback.getConfiguration().getWorkplaces().size() > 1) {
			workplaces.addChangeHandler( event -> {
				Integer wp = AonNumberUtils.toInteger(workplaces.getSelectedValue());
				invoiceCallback.getInvoice().setWorkplace(wp);
			});
			int i = 0;
			for (Workplace ea : invoiceCallback.getConfiguration().getWorkplaces()) {
				workplaces.addItem(ea.getDescription(), AonNumberUtils.toString( ea.getId()));
				if ( AonNumberUtils.equals(ea.getId(), invoiceCallback.getInvoice().getWorkplace())) {
					workplaces.setSelectedIndex(i);		
				}
				i++;
			}
			
			InlineLabel workPlaceLabel = new InlineLabel(AON.MSG.workplace());
			workPlaceLabel.setStyleName(AON.CSS.aonFlexLabel());
			workPlaceLabel.getElement().getStyle().setWidth(80, Unit.PX);
			headerPanel2.add(workPlaceLabel);
			
			headerPanel2.add(workplaces);
		}
		
		
		// -------------------------------
		// --------- CHECK LABELS ---------
		// -------------------------------
		
		checksLabel.setStyleName(AON.CSS.aonFlexBlockInline());
		headerPanel2.add(checksLabel);
		
		
		// *************************************************************************
		// ***************** PANEL ( Centro de Trabajo) *******************
		// *************************************************************************
//		if (invoiceCallback.getConfiguration().getWorkplaces() != null && invoiceCallback.getConfiguration().getWorkplaces().size() > 1) {
//			workplaces.addChangeHandler( new ChangeHandler() {
//				
//				@Override
//				public void onChange(ChangeEvent event) {
//					Integer wp = AonNumberUtils.toInteger(workplaces.getSelectedValue());
//					invoiceCallback.getInvoice().setWorkplace(wp);
//				}
//			});
//			workplaces.addItem("-- Todos --", "");
//			workplaces.setSelectedIndex(0);
//			int i = 1;
//			for (Workplace ea : invoiceCallback.getConfiguration().getWorkplaces()) {
//				workplaces.addItem(ea.getDescription(), AonNumberUtils.toStrtring( ea.getId()));
//				if ( AonNumberUtils.equals(ea.getId(), invoiceCallback.getInvoice().getWorkplace())) {
//					workplaces.setSelectedIndex(i);		
//				}
//				i++;
//			}
//			
//			FlowPanel invoiceDataInnerTableRowDiv21 = new FlowPanel();
//			invoiceDataInnerTableRowDiv21.setStyleName(AON.CSS.aonDisplayTableRow());
//			invoiceDataInnerTableDiv.add(invoiceDataInnerTableRowDiv21);
//			
//			FlowPanel headerPanel21 = new FlowPanel();
//			headerPanel21.setStyleName(AON.CSS.aonDisplayTableCell());
//			invoiceDataInnerTableRowDiv21.add(headerPanel21);
//			
//			InlineLabel workPlaceLabel = new InlineLabel(AON.MSG.workplace());
//			workPlaceLabel.setStyleName(AON.CSS.aonFlexLabel());
//			workPlaceLabel.getElement().getStyle().setWidth(80, Unit.PX);
//			headerPanel21.add(workPlaceLabel);
//			
//			headerPanel21.add(workplaces);
//		}

		// *************************************************************************
		// ***************** PANEL ( set de checks 1) *****************
		// *************************************************************************
		FlowPanel invoiceDataInnerTableRowDiv3 = new FlowPanel();
		invoiceDataInnerTableRowDiv3.setStyleName(AON.CSS.aonDisplayTableRow());
		invoiceDataInnerTableDiv.add(invoiceDataInnerTableRowDiv3);

		FlowPanel headerPanel3 = new FlowPanel();
		headerPanel3.setStyleName(AON.CSS.aonDisplayTableCell());
		headerPanel3.getElement().getStyle().setPaddingLeft(30, Unit.PX); 
		invoiceDataInnerTableRowDiv3.add(headerPanel3);
		
		checksTable.setVisible( checkTableVisible );
		checksTable.setStyleName(AON.CSS.aonDisplayTable());
		checksTable.addStyleName(AON.CSS.aonMarginLeft());
		headerPanel3.add(checksTable);
 
		FlowPanel checksTableRow0 = new FlowPanel();
		checksTableRow0.setStyleName(AON.CSS.aonDisplayTableRow());
		checksTableRow0.addStyleName(AON.CSS.aonNowrap());
		checksTable.add(checksTableRow0);
		
		// ---------------------------
		// --------- SERVICE ---------
		// ---------------------------
		if (invoiceCallback.getInvoice().isExpenses()) {
			service.paint(true);
		} else {
			service.paint(invoiceCallback.getInvoice().isService());
			service.addClickHandler(event -> {
				invoiceCallback.getInvoice().getInvoice().setService( !invoiceCallback.getInvoice().isService() );
				service.paint(invoiceCallback.getInvoice().isService());
				decorateInvoiceTypeLabel( invoiceCallback );
				InvoiceCalculator.calculate(invoiceCallback.getInvoice());
				enableChecks(invoiceCallback);
				headerDataChanged(invoiceCallback);
			});
		}
		service.getElement().getStyle().setWidth(120, Unit.PX); 
		checksTableRow0.add(service);

		// -------------------------------------
		// --------- Aplicar retencion ---------
		// -------------------------------------
		withholding.paint(invoiceCallback.getInvoice().getInvoice().isWithholding());
		withholding.addClickHandler( event -> {
			invoiceCallback.getInvoice().getInvoice().setWithholding(!invoiceCallback.getInvoice().getInvoice().isWithholding());
			withholding.paint(invoiceCallback.getInvoice().getInvoice().isWithholding());
			decorateInvoiceTypeLabel( invoiceCallback );
			vatPanel.withholdingChanged( invoiceCallback.getInvoice().getInvoice().isWithholding() );
			for (InvoiceVAT vat : invoiceCallback.getInvoice().getVats()) {
				vat.setWithholding(vat.isPrepayment()?false:invoiceCallback.getInvoice().getInvoice().isWithholding());
			}
			InvoiceCalculator.calculate(invoiceCallback.getInvoice());
			headerDataChanged(invoiceCallback);
		});
		withholding.getElement().getStyle().setWidth(120, Unit.PX); 
		checksTableRow0.add(withholding);
		
		// ---------------------------------------
		// --------- BIENES DE INVERSION ---------
		// ---------------------------------------
		investment.paint(invoiceCallback.getInvoice().isInvestment());
		investment.addClickHandler( event -> {
			invoiceCallback.getInvoice().getInvoice().setInvestment( !invoiceCallback.getInvoice().isInvestment() );
			decorateInvoiceTypeLabel( invoiceCallback );
			investment.paint(invoiceCallback.getInvoice().isInvestment());
			enableChecks(invoiceCallback);
			headerDataChanged(invoiceCallback);
		});
		investment.getElement().getStyle().setWidth(120, Unit.PX); 
		checksTableRow0.add(investment);
		
		// -------------------------------------------
		// --------- RECARGO DE EQUIVALENCIA ---------
		// -------------------------------------------
		surcharge.paint(invoiceCallback.getInvoice().getInvoice().isSurcharge());
		surcharge.addClickHandler( event -> {
			invoiceCallback.getInvoice().getInvoice().setSurcharge(!invoiceCallback.getInvoice().getInvoice().isSurcharge());
			surcharge.paint(invoiceCallback.getInvoice().getInvoice().isSurcharge());
			decorateInvoiceTypeLabel( invoiceCallback );
			InvoiceCalculator.calculate(invoiceCallback.getInvoice());
			headerDataChanged(invoiceCallback);
		});
		surcharge.getElement().getStyle().setWidth(120, Unit.PX); 
		checksTableRow0.add(surcharge);

		Label fillLabel = new Label();
		fillLabel.getElement().getStyle().setWidth(120, Unit.PX);
		checksTableRow0.add(fillLabel);
		
		// *************************************************************************
		// ***************** PANEL ( set de checks 2) *****************
		// *************************************************************************
		
		FlowPanel checksTableRow1 = new FlowPanel();
		checksTableRow1.setStyleName(AON.CSS.aonDisplayTableRow());
		checksTableRow1.addStyleName(AON.CSS.aonNowrap());
		checksTable.add(checksTableRow1);
		
		// -------------------------------------------
		// --------- Reg. agric, gan y pesca ---------
		// -------------------------------------------
		withholdingFarmer.paint(invoiceCallback.getInvoice().getInvoice().isWithholdingFarmer());
		withholdingFarmer.addClickHandler( event -> {
			invoiceCallback.getInvoice().getInvoice().setWithholdingFarmer(!invoiceCallback.getInvoice().getInvoice().isWithholdingFarmer());
			withholdingFarmer.paint(invoiceCallback.getInvoice().getInvoice().isWithholdingFarmer());
			decorateInvoiceTypeLabel( invoiceCallback );
			InvoiceCalculator.calculate(invoiceCallback.getInvoice());
			headerDataChanged(invoiceCallback);
		});
		withholdingFarmer.getElement().getStyle().setWidth(120, Unit.PX); 
		checksTableRow1.add(withholdingFarmer);

		// --------------------------------------------
		// --------- Regimne criterio de caja ---------
		// --------------------------------------------
		vatAccrualPayment.paint(invoiceCallback.getInvoice().getInvoice().isVatAccrualPayment());
		vatAccrualPayment.addClickHandler( event -> {
			invoiceCallback.getInvoice().getInvoice().setVatAccrualPayment(!invoiceCallback.getInvoice().getInvoice().isVatAccrualPayment());
			decorateInvoiceTypeLabel( invoiceCallback );
			vatAccrualPayment.paint(invoiceCallback.getInvoice().getInvoice().isVatAccrualPayment());
		});
		vatAccrualPayment.getElement().getStyle().setWidth(120, Unit.PX); 
		checksTableRow1.add(vatAccrualPayment);
		
		// -------------------------------------
		// --------- Fra. Rectificativa --------
		// -------------------------------------
		rectifier.paint(invoiceCallback.getInvoice().getInvoice().isRectifier());
		rectifier.addClickHandler( event -> {
			invoiceCallback.getInvoice().getInvoice().setNormalRectifier(!invoiceCallback.getInvoice().getInvoice().isRectifier());
			decorateInvoiceTypeLabel( invoiceCallback );
			rectifier.paint(invoiceCallback.getInvoice().getInvoice().isRectifier());
			fillSeriesWidget(invoiceCallback, inv);
			headerDataChanged(invoiceCallback);
		});
		rectifier.getElement().getStyle().setWidth(120, Unit.PX); 
		checksTableRow1.add(rectifier);

		// -------------------------------------
		// --------- CONTIENE SUPLIDOS ---------
		// -------------------------------------
		prepayment.paint(invoiceCallback.getInvoice().hasPrepayments());
		prepayment.addClickHandler( event -> {
			if ( !invoiceCallback.getInvoice().isDuaLinked() ) {
				invoiceCallback.getInvoice().setPrepayments(!invoiceCallback.getInvoice().hasPrepayments());
				prepayment.paint(invoiceCallback.getInvoice().hasPrepayments());
				decorateInvoiceTypeLabel( invoiceCallback );
				if (!invoiceCallback.getInvoice().hasPrepayments()) {
					vatPanel.prepaymentChanged( invoiceCallback.getInvoice().hasPrepayments() );
					for (InvoiceVAT vat : invoiceCallback.getInvoice().getVats()) {
						vat.setPrepayment( false );
					}
					InvoiceCalculator.calculate(invoiceCallback.getInvoice());
				}
				headerDataChanged(invoiceCallback);
			} else {
				AonMessageDialog.error("No se puede modificar si la factura est\u00E1 vinculada a un DUA");
			}
		});
		prepayment.getElement().getStyle().setWidth(120, Unit.PX); 
		checksTableRow1.add(prepayment);
		
		// ------------------------
		// --------- DUA ----------
		// ------------------------
		duaLinked.paint(invoiceCallback.getInvoice().isDuaLinked() && invoiceCallback.getInvoice().getInvoice().isDUAAllowed());
		duaLinked.addClickHandler( event -> {
			invoiceCallback.getInvoice().setDuaLinked(!invoiceCallback.getInvoice().isDuaLinked());
			duaLinked.paint(invoiceCallback.getInvoice().isDuaLinked());
			decorateInvoiceTypeLabel( invoiceCallback );
			enableChecks(invoiceCallback);
			if (invoiceCallback.getInvoice().isDuaLinked()) {
				duaPanel = new InvoiceDUAPanel( invoiceCallback );
				duaPanelContainer.add(duaPanel);
				duaPanel.addSelectionHandler( new SelectionHandler<IInvoicePanelCallback>() {

					@Override
					public void onSelection(SelectionEvent<IInvoicePanelCallback> event) {
						duaInvoiceChanged( event.getSelectedItem() );
					}
				});
				duaPanel.initialize( invoiceCallback, null );
				duaInvoiceChanged( invoiceCallback);
			} else {
				duaPanelContainer.clear();
			}
		});
		duaLinked.getElement().getStyle().setWidth(120, Unit.PX); 
		checksTableRow1.add(duaLinked);
		
		
		// --------------------------------------
		// --------- Regimen Unión UOSS ---------
		// --------------------------------------
		uossRegime.paint(invoiceCallback.getInvoice().isVatUnion());
		uossRegime.addClickHandler( event -> {
			invoiceCallback.getInvoice().getInvoice().setVatUnion(!invoiceCallback.getInvoice().getInvoice().isVatUnion());
			decorateInvoiceTypeLabel( invoiceCallback );
			uossRegime.paint(invoiceCallback.getInvoice().getInvoice().isVatUnion());
			headerDataChanged(invoiceCallback);
		});
		uossRegime.getElement().getStyle().setWidth(120, Unit.PX); 
		checksTableRow1.add(uossRegime);

		// -----------------------------------------
		// ------ Regimen Exterior Unión UOSS ------
		// -----------------------------------------
		euossRegime.paint(invoiceCallback.getInvoice().isVatUnion());
		euossRegime.addClickHandler( event -> {
			invoiceCallback.getInvoice().getInvoice().setVatUnionExternal(!invoiceCallback.getInvoice().getInvoice().isVatUnionExternal());
			decorateInvoiceTypeLabel( invoiceCallback );
			euossRegime.paint(invoiceCallback.getInvoice().getInvoice().isVatUnionExternal());
			headerDataChanged(invoiceCallback);
		});
		euossRegime.getElement().getStyle().setWidth(120, Unit.PX); 
		checksTableRow1.add(euossRegime);

		// ---------------------------------------
		// --------- Regimen importacion IOSS ----
		// ---------------------------------------
		iossRegime.paint(invoiceCallback.getInvoice().isVatImportation());
		iossRegime.addClickHandler( event -> {
			invoiceCallback.getInvoice().getInvoice().setVatImportation(!invoiceCallback.getInvoice().getInvoice().isVatImportation());
			decorateInvoiceTypeLabel( invoiceCallback );
			iossRegime.paint(invoiceCallback.getInvoice().getInvoice().isVatImportation());
			headerDataChanged(invoiceCallback);
		});
		iossRegime.getElement().getStyle().setWidth(120, Unit.PX); 
		checksTableRow1.add(iossRegime);
		

//		// *************************************************************************
//		// ***************** PANEL RECTIFICATIVA ***********************************
//		// *************************************************************************
//		invoiceDataRectifierTableRowDiv = new FlowPanel();
//		invoiceDataRectifierTableRowDiv.setStyleName(AON.CSS.aonDisplayTableRow());
//		invoiceDataInnerTableDiv.add(invoiceDataRectifierTableRowDiv);
//
//		FlowPanel headerRectifierPanel = new FlowPanel();
//		headerRectifierPanel.setStyleName(AON.CSS.aonDisplayTableCell());
//		headerRectifierPanel.addStyleName(AON.CSS.aonPadding());
//		headerRectifierPanel.addStyleName(AON.CSS.aonMargin());
//		headerRectifierPanel.addStyleName(AON.CSS.aonBackgroundOrange());
//		invoiceDataRectifierTableRowDiv.add(headerRectifierPanel);
//		
//		InlineLabel rectInvoiceNumberLabel = new InlineLabel(AON.MSG.invoiceNumber());
//		rectInvoiceNumberLabel.setStyleName(AON.CSS.aonFlexLabel());
//		rectInvoiceNumberLabel.getElement().getStyle().setWidth(80,Unit.PX);
//		headerRectifierPanel.add(rectInvoiceNumberLabel);

		// *************************************************************************
		// ***************** PANEL ( Número Factura, total factura ) ******
		// *************************************************************************
		FlowPanel invoiceDataInnerTableRowDiv4 = new FlowPanel();
		invoiceDataInnerTableRowDiv4.setStyleName(AON.CSS.aonDisplayTableRow());
		invoiceDataInnerTableDiv.add(invoiceDataInnerTableRowDiv4);

		FlowPanel headerPanel4 = new FlowPanel();
		headerPanel4.setStyleName(AON.CSS.aonDisplayTableCell());
		invoiceDataInnerTableRowDiv4.add(headerPanel4);

		// ----------------------------------
		// --------- NUMERO FACTURA ---------
		// ----------------------------------
		String l = AON.MSG.invoiceNumber();
		if ( !invoiceCallback.hasCommunication() && inv.isSales() ) {
			l = AON.MSG.seriesNumber();	
		}
		InlineLabel seriesNumberLabel = new InlineLabel(l);
		seriesNumberLabel.setStyleName(AON.CSS.aonFlexLabel());
		seriesNumberLabel.getElement().getStyle().setWidth(80,Unit.PX);
		headerPanel4.add(seriesNumberLabel);
		
		// -------------------------
		// --------- SERIE ---------
		// -------------------------
		FlowPanel numberPanel = new FlowPanel();
		numberPanel.setStyleName(AON.CSS.aonNowrap());
		numberPanel.addStyleName(AON.CSS.aonFlexBlockInline());

		FlowPanel referenceCodePanel = new FlowPanel();
		referenceCodePanel.setStyleName(AON.CSS.aonNowrap());
		referenceCodePanel.addStyleName(AON.CSS.aonFlexBlockInline());
		referenceCodePanel.setVisible( false );
		
		series.addChangeHandler(event -> {
			invoiceCallback.getInvoice().getInvoice().setSeries(series.getSelectedIndex()==0?null:series.getSelectedValue());
			FINANCE_SERVICE.getInvoiceNextNumber(
					invoiceCallback.getOccam()
					,new Byte[]{invoiceCallback.getInvoice().getInvoice().getType().value()}
					,invoiceCallback.getInvoice().getInvoice().getSeries()
					,new AsyncCallback<Integer>() {

						@Override
						public void onFailure(Throwable caught) {
							invoiceCallback.getModule().onError(caught.getMessage());
						}

						@Override
						public void onSuccess(Integer result) {
							number.setValue(result,false,true);
							invoiceCallback.getInvoice().getInvoice().setNumber(result);
							
							String rc = AonStringUtils.leftPad(Integer.toString(invoiceCallback.getInvoice().getInvoice().getNumber()), 6, "0");
							if (!AonStringUtils.isBlank(invoiceCallback.getInvoice().getInvoice().getSeries())) {
								rc = invoiceCallback.getInvoice().getInvoice().getSeries() + "/" + rc;
							}
							invoiceCallback.getInvoice().getInvoice().setReferenceCode(rc);
							if (AonStringUtils.isNotEmpty(referenceCode.getValue()) ) {
								referenceCode.setValue( rc, false);
							}
							
							invoiceCallback.paintEntry();
							invoiceCallback.getInvoice().getAccountEntry().setDirty(true);
							invoiceCallback.getModule().refreshIdLabel();
						}
					});
		});
		numberPanel.add(series);
		
		// -------------------------
		// --------- NUMBER---------
		// -------------------------
		number.setStyleName(AON.CSS.aonMarginLeft());
		number.addStyleName(AON.CSS.aonInputText());
		number.addValueChangeHandler(event -> {
			invoiceCallback.getInvoice().getInvoice().setNumber(number.getValue());
			String rc = AonStringUtils.leftPad(Integer.toString(invoiceCallback.getInvoice().getInvoice().getNumber()), 6, "0");
			if (!AonStringUtils.isBlank(invoiceCallback.getInvoice().getInvoice().getSeries())) {
				rc = invoiceCallback.getInvoice().getInvoice().getSeries() + "/" + rc;
			}
			invoiceCallback.getInvoice().getInvoice().setReferenceCode(rc);
			if (AonStringUtils.isNotEmpty(referenceCode.getValue()) ) {
				referenceCode.setValue( rc, false);
			}
			invoiceCallback.paintEntry();
			invoiceCallback.getInvoice().getAccountEntry().setDirty(true);
			invoiceCallback.getModule().refreshIdLabel();
		});
		number.setVisibleLength(8);
		number.setMaxLength(8);
		numberPanel.add(number);


		// ----------------------------------
		// --------- REFERENCE CODE ---------
		// ----------------------------------

		if (!invoiceCallback.hasCommunication() && inv.isSales()) {
			AonTableButton showReference = new AonTableButton("Mostrar N\u00BA de factura", AON.CSS.aonIconEdit() );
			showReference.addStyleName( AON.CSS.aonMarginLeft() );
			showReference.addClickHandler( e -> {
				if (referenceCodePanel.isVisible()) {
					referenceCodePanel.setVisible( false );
					showReference.removeStyleName( AON.CSS.aonIconEditRed() );
					showReference.addStyleName( AON.CSS.aonIconEdit() );
				} else {
					referenceCodePanel.setVisible( true );
					showReference.removeStyleName( AON.CSS.aonIconEdit() );
					showReference.addStyleName( AON.CSS.aonIconEditRed() );
				}
			});
			numberPanel.add(showReference);

			InlineLabel invoiceNumberLabel = new InlineLabel(AON.MSG.invoiceNumber());
			invoiceNumberLabel.setStyleName(AON.CSS.aonFlexLabel());
			invoiceNumberLabel.getElement().getStyle().setWidth(80,Unit.PX);
			referenceCodePanel.add(invoiceNumberLabel);
		}
		
		referenceCode.setStyleName(AON.CSS.aonInputText());
		referenceCode.addValueChangeHandler(event -> {
			invoiceCallback.getInvoice().getInvoice().setReferenceCode(referenceCode.getValue());
			invoiceCallback.paintEntry();
			invoiceCallback.getInvoice().getAccountEntry().setDirty(true);
			invoiceCallback.getModule().refreshIdLabel();
		});

		referenceCode.setVisibleLength(15); 
		referenceCode.setMaxLength(32);
		if (!invoiceCallback.hasCommunication() && inv.isSales()) {
			referenceCodePanel.add(referenceCode);
			numberPanel.add(referenceCodePanel);
			headerPanel4.add(numberPanel);
		} else {
			headerPanel4.add(referenceCode);
		}
		
		// ----------------------------------
		// --------- INVOICE TOTAL ----------
		// ----------------------------------
		InlineLabel label0 = new InlineLabel(AON.MSG.invoiceTotal());
		label0.setStyleName(AON.CSS.aonFlexLabel());
		label0.getElement().getStyle().setMarginLeft(50, Unit.PX);
		headerPanel4.add(label0);

		invoiceTotal.addKeyUpHandler( event -> {
			if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
				repeatLastInvoice(invoiceCallback, new ISelectionCallback() {

					@Override
					public void onSuccess() {
					}

					@Override
					public void onFailure() {
						invoiceCallback.getModule().onError(AON.MSG.invoiceNotFound());
					}
					
				});
			}
		});
		invoiceTotal.addValueChangeHandler(event -> {
			if (AonNumberUtils.isNumber(invoiceTotal.getText())) {
				if (invoiceTotal.getValue() == null) invoiceTotal.setValue(0.0, false);
				if (invoiceCallback.getInvoice().getVats() != null &&  invoiceCallback.getInvoice().getVats().size() == 1) {
					InvoiceCalculator.reverseCalculate(invoiceCallback.getInvoice(),invoiceTotal.getValue());
					vatPanel.populateFirstVat( new EditableInvoicePanelCallback(invoiceCallback) );
					withholdingPanel.setValue( invoiceCallback.getInvoice().getWithholdingData() );
					financePanel.invoiceTotalChanged( invoiceCallback );
				}
				invoiceCallback.paintEntry();
				invoiceCallback.getInvoice().getAccountEntry().setDirty(true);
				invoiceCallback.getModule().refreshIdLabel();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						if (invoiceTotal.getValue() != null && invoiceTotal.getValue() != 0) {
//								fastSave.setEnabled(true);
//								fastSave.setFocus(true);
						} else {
							vatPanel.setFocus(true);
						}
				}});
			}
		});
		invoiceTotal.setVisibleLength(12);
		headerPanel4.add(invoiceTotal);
		invoicePanel.add(invoiceDataTableDiv);
		
		// *************************************************************************
		// ** PANEL ( Bases Imponibles) ********************************************
		// *************************************************************************

		FlowPanel vatDataTableDiv = new FlowPanel();
		vatDataTableDiv.setStyleName(AON.CSS.aonDisplayTable());
		vatDataTableDiv.addStyleName(AON.CSS.aonWidthAll());
		vatDataTableDiv.addStyleName(AON.CSS.aonMarginTopSep());
		vatDataTableDiv.getElement().getStyle().setBackgroundColor(INNER_BACKGROUND_COLOR);

		FlowPanel vatDataTableRowDiv = new FlowPanel();
		vatDataTableRowDiv.setStyleName(AON.CSS.aonDisplayTableRow());
		vatDataTableDiv.add(vatDataTableRowDiv);
		
		FlowPanel vatDataTableCellDiv0 = new FlowPanel();
		vatDataTableCellDiv0.setStyleName(AON.CSS.aonDisplayTableCell());
		vatDataTableCellDiv0.addStyleName(AON.CSS.aonTextVerticalContainer());
		vatDataTableCellDiv0.getElement().getStyle().setWidth(40, Unit.PX);
		vatDataTableCellDiv0.getElement().getStyle().setProperty(MIN_WIDTH, 40, Unit.PX);
		vatDataTableCellDiv0.getElement().getStyle().setBackgroundColor(LABEL_BACKGROUND_COLOR);
		vatDataTableCellDiv0.getElement().getStyle().setBorderColor(DARK_GRAY);
		vatDataTableCellDiv0.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		vatDataTableCellDiv0.getElement().getStyle().setBorderWidth(1, Unit.PX);
		vatDataTableCellDiv0.getElement().getStyle().setProperty(BORDER_RADIUS, 10, Unit.PCT);
		vatDataTableRowDiv.add(vatDataTableCellDiv0);
		
		Label baseDescription = new Label("Bases");
		baseDescription.setStyleName(AON.CSS.aonTextVertical());
		baseDescription.addStyleName(AON.CSS.aonBold());
		vatDataTableCellDiv0.add(baseDescription);
		
		FlowPanel vatDataTableCellDiv1 = new FlowPanel();
		vatDataTableCellDiv1.setStyleName(AON.CSS.aonDisplayTableCell());
		ScrollPanel vatScrollPanel = new ScrollPanel(); 
		vatScrollPanel.setStyleName(AON.CSS.aonWidthAll());
		vatScrollPanel.setWidget( vatPanel );
		vatDataTableRowDiv.add(vatScrollPanel);
		invoicePanel.add(vatDataTableDiv);
		
		vatPanel.addValueChangeHandler(event -> {
			InvoiceCalculator.calculate(invoiceCallback.getInvoice());
			withholdingPanel.setValue(invoiceCallback.getInvoice().getWithholdingData());
			invoiceTotal.setValue(invoiceCallback.getInvoice().getInvoice().getTotal(),false);
			financePanel.invoiceTotalChanged(invoiceCallback);
			invoiceCallback.paintEntry();
			invoiceCallback.getInvoice().getAccountEntry().setDirty(true);
			invoiceCallback.getModule().refreshIdLabel();
		});
		vatPanel.addSelectionHandler(event -> {
			invoiceCallback.paintEntry();
			invoiceCallback.getInvoice().getAccountEntry().setDirty(true);
			invoiceCallback.getModule().refreshIdLabel();
		});
		
		
		// *************************************************************************
		// ** DUA PANEL ********************************************************
		// *************************************************************************
		duaPanelContainer = new FlowPanel();
		invoicePanel.add( duaPanelContainer );
		
		// *************************************************************************
		// ** PANEL ( IRPF) ********************************************************
		// *************************************************************************
		
		withholdingPanel = new InvoiceWithholdingPanel( invoiceCallback );
		withholdingPanel.setVisible(invoiceCallback.getInvoice().isWithholding());
		withholdingPanel.setValue(invoiceCallback.getInvoice().getWithholdingData());		
		withholdingPanel.addValueChangeHandler(event -> {
			InvoiceCalculator.calculate(invoiceCallback.getInvoice());
			withholdingPanel.setValue(invoiceCallback.getInvoice().getWithholdingData());
			invoiceTotal.setValue(invoiceCallback.getInvoice().getInvoice().getTotal(),false);
			invoiceCallback.paintEntry();
			invoiceCallback.getInvoice().getAccountEntry().setDirty(true);
			invoiceCallback.getModule().refreshIdLabel();
		});
		withholdingPanel.addSelectionHandler(event -> {
			invoiceCallback.paintEntry();
			invoiceCallback.getInvoice().getAccountEntry().setDirty(true);
			invoiceCallback.getModule().refreshIdLabel();
		});
		invoicePanel.add( withholdingPanel );

		// *************************************************************************
		// ** PANEL ( FINANCES) ****************************************************
		// *************************************************************************
		FlowPanel financeDataTableDiv = new FlowPanel();
		financeDataTableDiv.setStyleName(AON.CSS.aonDisplayTable());
		financeDataTableDiv.addStyleName(AON.CSS.aonWidthAll());
		financeDataTableDiv.addStyleName(AON.CSS.aonMarginTopSep());
		financeDataTableDiv.getElement().getStyle().setBackgroundColor(INNER_BACKGROUND_COLOR);

		FlowPanel financeDataTableRowDiv = new FlowPanel();
		financeDataTableRowDiv.setStyleName(AON.CSS.aonDisplayTableRow());
		financeDataTableDiv.add(financeDataTableRowDiv);
		
		FlowPanel financeDataTableCellDiv0 = new FlowPanel();
		financeDataTableCellDiv0.setStyleName(AON.CSS.aonDisplayTableCell());
		financeDataTableCellDiv0.addStyleName(AON.CSS.aonTextVerticalContainer());
		financeDataTableCellDiv0.getElement().getStyle().setWidth(40, Unit.PX);
		financeDataTableCellDiv0.getElement().getStyle().setProperty(MIN_WIDTH, 40, Unit.PX);
		financeDataTableCellDiv0.getElement().getStyle().setBackgroundColor(LABEL_BACKGROUND_COLOR);
		financeDataTableCellDiv0.getElement().getStyle().setBorderColor(DARK_GRAY);
		financeDataTableCellDiv0.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		financeDataTableCellDiv0.getElement().getStyle().setBorderWidth(1, Unit.PX);
		financeDataTableCellDiv0.getElement().getStyle().setProperty(BORDER_RADIUS, 10, Unit.PCT);
		financeDataTableRowDiv.add(financeDataTableCellDiv0);
		
		Label financeDescription = new Label("Vtos.");
		financeDescription.setStyleName(AON.CSS.aonTextVertical());
		financeDescription.addStyleName(AON.CSS.aonBold());
		financeDataTableCellDiv0.add(financeDescription);
		
		FlowPanel financeDataTableCellDiv1 = new FlowPanel();
		financeDataTableCellDiv1.setStyleName(AON.CSS.aonDisplayTableCell());
		financeDataTableRowDiv.add(financePanel);
		invoicePanel.add(financeDataTableDiv);
		
		// *************************************************************************
		// ***************** PANEL ( OTROS DATOS, concepto ... ) *******************
		// *************************************************************************
		
		FlowPanel otherDataTableDiv = new FlowPanel();
		otherDataTableDiv.setStyleName(AON.CSS.aonDisplayTable());
		otherDataTableDiv.addStyleName(AON.CSS.aonWidthAll());
		otherDataTableDiv.addStyleName(AON.CSS.aonMarginTopSep());
		otherDataTableDiv.getElement().getStyle().setBackgroundColor(INNER_BACKGROUND_COLOR);

		FlowPanel otherDataTableRowDiv = new FlowPanel();
		otherDataTableRowDiv.setStyleName(AON.CSS.aonDisplayTableRow());
		otherDataTableDiv.add(otherDataTableRowDiv);
		
		FlowPanel otherDataTableCellDiv0 = new FlowPanel();
		otherDataTableCellDiv0.setStyleName(AON.CSS.aonDisplayTableCell());
		otherDataTableCellDiv0.addStyleName(AON.CSS.aonTextVerticalContainer());
		otherDataTableCellDiv0.getElement().getStyle().setWidth(40, Unit.PX);
		otherDataTableCellDiv0.getElement().getStyle().setProperty(MIN_WIDTH, 40, Unit.PX);
		otherDataTableCellDiv0.getElement().getStyle().setBackgroundColor(LABEL_BACKGROUND_COLOR);
		otherDataTableCellDiv0.getElement().getStyle().setBorderColor(DARK_GRAY);
		otherDataTableCellDiv0.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		otherDataTableCellDiv0.getElement().getStyle().setBorderWidth(1, Unit.PX);
		otherDataTableCellDiv0.getElement().getStyle().setProperty(BORDER_RADIUS, 10, Unit.PCT);
		otherDataTableRowDiv.add(otherDataTableCellDiv0);
		
		Label otherDescription = new Label("");
		otherDescription.setStyleName(AON.CSS.aonTextVertical());
		otherDescription.addStyleName(AON.CSS.aonBold());
		otherDataTableCellDiv0.add(otherDescription);
		
		FlowPanel otherDataTableCellDiv1 = new FlowPanel();
		otherDataTableCellDiv1.setStyleName(AON.CSS.aonDisplayTableCell());
		
		FlowPanel manualConceptPanel = new FlowPanel();
		manualConceptPanel.setStyleName(AON.CSS.aonPaddingLeft());
		manualConceptPanel.addStyleName(AON.CSS.aonFlexGrow1());
		
		InlineLabel conceptLabel = new InlineLabel(AON.MSG.conceptComplement());
		conceptLabel.setStyleName(AON.CSS.aonFlexLabel());
		manualConceptPanel.add(conceptLabel);

		manualConcept.setStyleName(AON.CSS.aonInputText());
		manualConcept.setTabIndex(-1);
		manualConcept.setVisibleLength(20);
		manualConcept.addValueChangeHandler(event -> {
			invoiceCallback.getInvoice().setManualConcept(manualConcept.getValue());
			invoiceCallback.paintEntry();
			invoiceCallback.getInvoice().getAccountEntry().setDirty(true);
			invoiceCallback.getModule().refreshIdLabel();
		});
		manualConceptPanel.add(manualConcept);
		otherDataTableCellDiv1.add(manualConceptPanel);
		otherDataTableRowDiv.add(otherDataTableCellDiv1);
		invoicePanel.add(otherDataTableDiv);
		
		
		decorateInvoiceTypeLabel( invoiceCallback );
		enableChecks(invoiceCallback);
		invoicePanel.setVisible(true);
		
		Scheduler.get().scheduleDeferred(() -> {
			if (!invoiceCallback.hasCommunication() && invoiceCallback.getInvoice().isSales()) {
				series.setFocus(true);
			} else {
				referenceCode.setFocus(true);
			}
});

		if (invoiceCallback.getInvoice().isDuaLinked()) {
			duaPanel = new InvoiceDUAPanel( invoiceCallback );
			duaPanelContainer.add(duaPanel);
			duaPanel.addSelectionHandler( event -> duaInvoiceChanged( event.getSelectedItem() ));
		} else {
			duaPanelContainer.clear();
		}

		return invoicePanel;
	}

	private boolean isDefaultAdmonCanarias(InvoicePanelCallback invoiceCallback) {
		return invoiceCallback.getConfiguration().fiscal().getAdministration(null) == Administration.CANARIAS;
	}

	private void fillSeriesWidget(InvoicePanelCallback invoiceCallback, AccountingInvoice inv) {
		series.clear();
		series.addItem(" --- ","");
		if (AonCollectionUtils.isNotEmpty(invoiceCallback.getConfiguration().getInvoiceSalesSeries())) {
			LinkedList<String> rectificationSeries = invoiceCallback.getConfiguration().getInvoiceRectificationSalesSeries();
			for (String ser : invoiceCallback.getConfiguration().getInvoiceSalesSeries()) {
				boolean rectifierSerie = (rectificationSeries != null && rectificationSeries.contains(ser));
				if (!rectifierSerie || inv.getInvoice().isRectifier()) {
					series.addItem(ser);
				}
			}
		}

		for (int i = 0; i < series.getItemCount(); i++) {
			if (AonStringUtils.isBlank(inv.getInvoice().getSeries()) 
					&& AonStringUtils.isBlank(series.getValue(i))
					||  (AonStringUtils.equals(inv.getInvoice().getSeries(), series.getValue(i)))) {
				series.setSelectedIndex(i);
				break;
			}
		}
	}

	protected void duaInvoiceChanged(IInvoicePanelCallback invoiceCallback) {
		AccountingInvoice ai = invoiceCallback.getInvoice();
		if ( ai.getDuaInvoice() != null 
			&& ai.getDuaInvoice().getInfo() != null 
			&& ai.getDuaInvoice().getInfo().isAuthCalcEnabled()) {
			
			InvoiceCalculator.calculateDUAInfo( invoiceCallback.getInvoice() );
		}
		InvoiceCalculator.calculateViaDUA(invoiceCallback.getInvoice());
		duaPanel.populate(invoiceCallback);
		invoiceCallback.getInvoice().setPrepayments(invoiceCallback.getInvoice().getDuaInvoice() != null);
		prepayment.paint(invoiceCallback.getInvoice().hasPrepayments());
		vatPanel.paint( new EditableInvoicePanelCallback(invoiceCallback) );
		InvoiceCalculator.calculate(invoiceCallback.getInvoice());
		headerDataChanged(invoiceCallback);
	}

	private void enableChecks(InvoicePanelCallback invoiceCallback) {
		if (invoiceCallback.getInvoice().isUndeductible()) {
			rectifier.setVisible(false);
			service.setVisible(false);
			prepayment.setVisible(false);
			investment.setVisible(false);
			withholding.setVisible(false);
			surcharge.setVisible(false);
			withholdingFarmer.setVisible(false);
			vatAccrualPayment.setVisible(false);
			duaLinked.setVisible(false);
			iossRegime.setVisible(false);
			uossRegime.setVisible(false);
			euossRegime.setVisible(false);
		} else {
			invoiceCallback.getInvoice().getTransaction().visit(new InvoiceTransactionTypeVisitor<Void>() {
				
				@Override
				public Void visitNational() {
					visitCommon();
					investment.setVisible(true);
					withholding.setVisible(true);
					surcharge.setVisible(true);
					withholdingFarmer.setVisible(true);
					vatAccrualPayment.setVisible(true);
					duaLinked.setVisible(invoiceCallback.getInvoice().isExpenses());
					uossRegime.setVisible(
						   invoiceCallback.getInvoice().isSales()
					    && !isDefaultAdmonCanarias( invoiceCallback )
						&& invoiceCallback.getInvoice().getInvoice().getRegistryDocumentCountry() != null
						&& invoiceCallback.getInvoice().getInvoice().getRegistryDocumentCountry().isEuropeanUnionCountry()
					);
					euossRegime.setVisible( false );
					iossRegime.setVisible( false );
					return null;
				}
				
				@Override
				public Void visitOtherISP() {
					visitCommon();
					investment.setVisible(true);
					withholding.setVisible(true);
					surcharge.setVisible(true);
					withholdingFarmer.setVisible(true);
					vatAccrualPayment.setVisible(true);
					duaLinked.setVisible(invoiceCallback.getInvoice().isExpenses());
					iossRegime.setVisible(false);
					uossRegime.setVisible(false);
					euossRegime.setVisible(false);
					return null;
				}
				
				
				@Override
				public Void visitIntracommunity() {
					visitCommon();
					investment.setVisible(true);
					withholding.setVisible(false);
					surcharge.setVisible(true);
					withholdingFarmer.setVisible(false);
					vatAccrualPayment.setVisible(false);
					duaLinked.setVisible(false);
					iossRegime.setVisible(false);
					uossRegime.setVisible(false);
					euossRegime.setVisible(false);
					return null;
				}
				
				@Override
				public Void visitExtracommunity() {
					visitCommon();
					withholding.setVisible(false);
					surcharge.setVisible(true);
					withholdingFarmer.setVisible(false);
					vatAccrualPayment.setVisible(false);
					duaLinked.setVisible(false);
					
					iossRegime.setVisible(
						// Si es una venta de bienes y company tributa en CANARIAS
					   (invoiceCallback.getInvoice().isSales()
						&& !invoiceCallback.getInvoice().isService()
						&& invoiceCallback.getInvoice().getInvoice().getRegistryDocumentCountry() != null
						&& invoiceCallback.getInvoice().getInvoice().getRegistryDocumentCountry().isEuropeanUnionCountry()
					    && isDefaultAdmonCanarias( invoiceCallback )
					   )
					|| 
						// Se recibe una exportación
						(  !invoiceCallback.getInvoice().isSales() 
						&& !invoiceCallback.getInvoice().isService() 
						&& !invoiceCallback.getInvoice().isInvestment()
						&& invoiceCallback.getInvoice().getInvoice().getRegistryDocumentCountry() != null
						&& !invoiceCallback.getInvoice().getInvoice().getRegistryDocumentCountry().isEuropeanUnionCountry())
					);
					euossRegime.setVisible(
						   invoiceCallback.getInvoice().isSales()
						&& invoiceCallback.getInvoice().isService()
						&& invoiceCallback.getInvoice().getInvoice().getRegistryDocumentCountry() != null
						&& invoiceCallback.getInvoice().getInvoice().getRegistryDocumentCountry().isEuropeanUnionCountry()
						&& isDefaultAdmonCanarias( invoiceCallback )
					);
					uossRegime.setVisible( false  );
					return null;
				}
				
				@Override
				public Void visitCanCeuMel() {
					return visitExtracommunity();
				}
				
				private void visitCommon() {
					service.setVisible(true);
					rectifier.setVisible(invoiceCallback.getInvoice().getInvoice().getRectificationInvoice() == null);
					prepayment.setVisible(true);
				}
			});
		}
	}

	@Override
	public int getTabIndex() {
		return registryBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		registryBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		registryBox.setFocus(true);
	}

	@Override
	public void setTabIndex(int index) {
		registryBox.setTabIndex(index);
	}
	
	private Widget getSplashWidget() {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setStyleName(AON.CSS.aonBlockCenter());
		hp.addStyleName(AON.CSS.aonMarginTop() );
		Label iconWaitLabel = new Label();
		iconWaitLabel.setStyleName(AON.CSS.aonLoader());
		iconWaitLabel.addStyleName(AON.CSS.aonMargin());
		hp.add(iconWaitLabel);
		Label textWaitLabel = new Label("Procesando la extracci\u00F3n de datos del documento.");
		textWaitLabel.setStyleName(AON.CSS.aonMargin());
		textWaitLabel.addStyleName(AON.CSS.aonBold());
		hp.add(textWaitLabel);
		return hp;
	}

	private Panel getDropFileZone(InvoicePanelCallback invoiceCallback) {
		FlowPanel filedrag = new FlowPanel();
		FormPanel ocrFileSelectForm = new FormPanel();
		FileUpload ocrFileSelect = new FileUpload();
		ocrFileSelectForm.add(ocrFileSelect);
		filedrag.add(ocrFileSelectForm);

		ocrFileSelect.ensureDebugId("ocrFileSelect");
		ocrFileSelect.getElement().getStyle().setDisplay(Style.Display.NONE);
		ocrFileSelect.addChangeHandler(event -> {
			filedrag.clear();
			if ( invoiceCallback.getConfiguration().isOCRActive() ) {
				filedrag.add(getSplashWidget());
			} else {
				filedrag.clear();
				registryBox.setFocus(true);
			}
			event.preventDefault();
			fileSelectHandler(ocrFileSelect.getElement());
		});
			
		if (!invoiceCallback.getConfiguration().isOCRActive()) {
			FlowPanel fp = new FlowPanel();
			fp.setStyleName(AON.CSS.aonWidthAll());
			fp.addStyleName(AON.CSS.aonDisplayFlexEnd());
			
			InlineLabel noOCR = new InlineLabel( "APIdeF no CONTRATADO" );
			noOCR.setTitle("Asistente Para Introducci\u00F3n de Facturas");
			noOCR.getElement().getStyle().setPaddingLeft(30, Unit.PX);
			noOCR.setStyleName(AON.CSS.aonLabelWithIcon());
			noOCR.addStyleName(AON.CSS.aonIconInfo());
			noOCR.addStyleName(AON.CSS.aonPaddingRight());
			noOCR.addStyleName(AON.CSS.aonColorBlue());
			noOCR.addStyleName(AON.CSS.aonNowrap());
			noOCR.addStyleName(AON.CSS.aonWidth300());
			
			fp.add( noOCR );
			dropPanel = new FocusPanel( fp );	
		} else {
			dropPanel = new FocusPanel();
		}
		
		
		dropPanel.setStyleName(AON.CSS.aonDropZone());
		dropPanel.addStyleName(AON.CSS.aonDropZoneImage());
		
		dropPanel.getElement().getStyle().setCursor(Style.Cursor.POINTER);
		dropPanel.addClickHandler( event -> {
			try {
				event.preventDefault();
				event.stopPropagation();
				ocrFileSelect.click();
			} catch (Throwable t) {
				LOGGER.info("ERROR ...: " + t.getMessage());
			}
		});
			
		dropPanel.addDragOverHandler(event -> {
			dropPanel.addStyleName(AON.CSS.aonDropZoneHover());
			event.preventDefault();
		});
		dropPanel.addDropHandler(event -> {
			dropPanel.removeStyleName(AON.CSS.aonDropZoneHover());
			event.preventDefault();
			fileDrop(event.getNativeEvent());
		});
		filedrag.add(dropPanel);
		return filedrag;
	}
	
	protected void setDocument(final String doc, final String name, String type) {
		invCallback.setDocument(doc, name, type);
	}
	
	private native void fileSelectHandler(Element ocrFileSelect) /*-{
		var self = this;		
		var file = ocrFileSelect.files[0];
		var reader = new FileReader();
		reader.addEventListener("load", function () {
			self.@com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.EditableInvoicePanel::setDocument(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(reader.result,file.name,file.type); 
			}, false);
		reader.readAsDataURL( file );
	}-*/;
	
	private native void fileDrop(NativeEvent e) /*-{
		var self = this;
		var file;		
		if (e.dataTransfer.items && e.dataTransfer.items.length > 0) {
			if (e.dataTransfer.items[0].kind === 'file') {
				file = e.dataTransfer.items[0].getAsFile();
      		}
		} else {
			if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
				file = e.dataTransfer.files[0];
    		}
  		}
  		if (file) {
  			var reader = new FileReader();
			reader.addEventListener("load", function () {
				self.@com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.EditableInvoicePanel::setDocument(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(reader.result,file.name,file.type); 
				if (e.dataTransfer.items) {
					e.dataTransfer.items.clear();
				} else {
					e.dataTransfer.clearData();
				}  		
			}, false);
			reader.readAsDataURL( file );	 
  		}
	}-*/;	

	private void debugInvoice(InvoicePanelCallback invoiceCallback) {
		if (invoiceCallback.getInvoice() != null && invoiceCallback.getInvoice().getInvoice() != null) {
			AonCustomPopup dialog = new AonCustomPopup();
			dialog.setWidth((Window.getClientWidth() - 100) + "px");
			dialog.setHeight((Window.getClientHeight() - 100) + "px");
			dialog.setAnimationEnabled(true);
			dialog.setGlassEnabled(true);
			dialog.setModal(true);
			dialog.setCaption(AON.MSG.invoice());
			dialog.add(new InvoiceConsoleTextPanel(invoiceCallback.getOccam() 
					, invoiceCallback.getInvoice().getInvoice().getDomain()
					, invoiceCallback.getInvoice().getInvoice().getId()));
			dialog.center();
			dialog.show();
		}
	}
}

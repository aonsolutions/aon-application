package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.FullDocument;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.InvoiceTransactionListBox;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleTEDI;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryService;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.ISelectionCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.InvoiceRectificationDataPanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.InvoiceRectificationDataPanel.InvoiceRectificationDataPanelCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.InvoicePanel.InvoicePanelCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTransactionTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.dom.client.DataTransfer.DropEffect;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EditableInvoicePanel extends SimpleLayoutPanel implements HasSelectionHandlers<AccountingInvoice>,HasAccountEntrySelectionHandlers,Focusable {
	
	private static final Logger LOGGER = Logger.getLogger(EditableInvoicePanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	protected static final String INNER_BACKGROUND_COLOR = "WhiteSmoke";
	protected static final String LABEL_BACKGROUND_COLOR = "#DDD";
	protected static final String DUA_BACKGROUND_COLOR = "HoneyDew";
	
	private static FinanceServiceAsync FINANCE_SERVICE;
	private static AccountEntryServiceAsync ACCOUNT_ENTRY_SERVICE;
	
	private SimplePanel invoicePanelContainer;
	private AccountingRegistryBox registryBox;
	private CheckBox undeductible;
	private InlineLabel invoiceTypeLabel;
	private FlowPanel dropPanel; 	
	private ListBox series;
	private TextBox referenceCode;
	private AonDoubleBox invoiceTotal;
	private DateBoxEx taxDate;
	private CheckLabel service;
	private CheckLabel rectifier;
	private CheckLabel prepayment;
	private CheckLabel investment;
	private CheckLabel withholding;
	private CheckLabel surcharge;
	private CheckLabel vatAccrualPayment;
	private CheckLabel withholdingFarmer;
	private CheckLabel duaLinked;
	private InvoiceVATPanel vatPanel;
	private FlowPanel duaPanelContainer;
	private InvoiceDUAPanel duaPanel;
	private InvoiceWithholdingPanel withholdingPanel;
	private InvoiceFinancePanel financePanel;
	
	private InvoicePanelCallback invCallback;
	
	protected static interface IEditableInvoicePanelCallback extends IInvoicePanelCallback {
		void enableInvoiceTotal(boolean b);
	}
	
	protected class EditableInvoicePanelCallback implements IEditableInvoicePanelCallback {

		@Override
		public AccountingInvoice getInvoice() {
			return invCallback.getInvoice();
		}

		@Override
		public boolean isInvestAssetsAvailable() {
			return invCallback.isInvestAssetsAvailable();
		}

		@Override
		public void paintEntry() {
			invCallback.paintEntry();
		}

		@Override
		public String getCurrentDomainName() {
			return invCallback.getCurrentDomainName();
		}

		@Override
		public int getCurrentDomainId() {
			return invCallback.getCurrentDomainId();
		}

		@Override
		public String getCurrentUser() {
			return invCallback.getCurrentUser();
		}

		@Override
		public AccountEntryModuleTEDI getModule() {
			return invCallback.getModule();
		}

		@Override
		public AonConfiguration getConfiguration() {
			return invCallback.getConfiguration();
		}
		
		@Override
		public AccountEntryModuleOptions getModuleOptions() {
			return invCallback.getModuleOptions();
		}

		@Override
		public void enableInvoiceTotal(boolean enabled) {
			invoiceTotal.setEnabled(enabled);
		}

		@Override
		public AccountingRegistry getLastRegistry() {
			return invCallback.getLastRegistry();
		}
	}
	
	public static class CheckLabel extends InlineLabel {

		private double width;
		
		public CheckLabel(String text, double width) {
			super(text);
			this.width = width;
		}

		public void paint( boolean checked) {
			this.setStyleName(AON.CSS.aonTabIcon());
			this.addStyleName(AON.CSS.aonClickable());
			this.addStyleName(checked?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck());		
			this.getElement().getStyle().setWidth(width, Unit.PX);
		}

	}

	public EditableInvoicePanel(final InvoicePanelCallback invoiceCallback) {
	
		this.invCallback = invoiceCallback; 
		AccountEntryServiceAsync accountEntryServiceRaw = GWT.create(AccountEntryService.class);
		ACCOUNT_ENTRY_SERVICE = new AccountEntryServiceAsyncDecorator(accountEntryServiceRaw);

		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		FINANCE_SERVICE = new FinanceServiceAsyncDecorator(financeServiceRaw);

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
		
		registryBox = new AccountingRegistryBox(
				invoiceCallback.getCurrentDomainName()
				,invoiceCallback.getCurrentDomainId()
				,invoiceCallback.getCurrentUser()
				,invoiceCallback.getConfiguration()
				,true);
		registryBox.addStyleName(AON.CSS.aonFlexGrow1());
		
		registryBox.addKeyUpHandler( new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					if ( registryBox.getId() == null) {
						if (invoiceCallback.getLastRegistry() != null) {
							registryBox.set(invoiceCallback.getLastRegistry());	
						}
					} else {
						registryBox.setFocus( true );
					}
				}
			}
		});
		registryBox.addSelectionHandler(new SelectionHandler<AccountingRegistry>() {
			@Override
			public void onSelection(SelectionEvent<AccountingRegistry> event) {
				final AccountingRegistry ar = event.getSelectedItem();
				undeductible.setValue(false);
				registryChanged(invoiceCallback, ar );
			}
		});
		regTable.add(registryBox);
		
		undeductible.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final AccountingRegistry ar = invoiceCallback.getInvoice().getRegistry();
				if (undeductible.getValue()) {
					ar.setType(AccountingRegistryType.UNDED_CREDITOR);
				} else {
					ar.setType(AccountingRegistryType.CREDITOR);
				}
				registryChanged(invoiceCallback, ar );
			}
		});
		regTable.add(undeductible);
		
		
		FlowPanel labelsPanel = new FlowPanel();
		labelsPanel.setStyleName(AON.CSS.aonFlexBlock());
		if (invoiceCallback.getInvoice().getInvoice() != null 
			&& invoiceCallback.getInvoice().getInvoice().isDUALinkAllowed() 
			&& invoiceCallback.getInvoice().getDuaNationalInvoice() != null ) {
			AonTableButton duaButton = new AonTableButton("Ver Factura DUA",AON.CSS.aonIconLaunch());
			duaButton.addStyleName(AON.CSS.aonMarginLeft());
			duaButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					ACCOUNT_ENTRY_SERVICE.getAccountingInvoiceFromInvoice(invoiceCallback.getCurrentDomainName()
							,invoiceCallback.getCurrentDomainId(),invoiceCallback.getCurrentUser()
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
					});								
				}
			});
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
			duaLabel.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					ACCOUNT_ENTRY_SERVICE.getAccountingInvoiceFromInvoice(invoiceCallback.getCurrentDomainName()
							,invoiceCallback.getCurrentDomainId()
							,invoiceCallback.getCurrentUser()
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
					});								
				}
			});
			labelsPanel.add(duaLabel);
			
		}
		
		invoiceTypeLabel = new InlineLabel();
		labelsPanel.add(invoiceTypeLabel);
		invoiceTypeLabel.setText( getInvoiceLabel(invoiceCallback.getInvoice()));
		invoiceTypeLabel.setStyleName(AON.CSS.aonFlexLabel());
		invoiceTypeLabel.addStyleName(AON.CSS.aonFontMedium());
		invoiceTypeLabel.addStyleName(AON.CSS.aonMarginLeftDouble());
		invoiceTypeLabel.addStyleName(AON.CSS.aonMarginRightDouble());
		
		if (invoiceCallback.getInvoice().getInvoice() != null && invoiceCallback.getInvoice().getInvoice().getId() != null) {
			
			boolean guest = invoiceCallback.getConfiguration().getUser().hasGuestRole() && !invoiceCallback.getConfiguration().getUser().hasAdminRole();
			
			if (invoiceCallback.getInvoice().getInvoice().getRectificationInvoice() != null 
					&& (invoiceCallback.getInvoice().getInvoice().isRectifier() 
					|| invoiceCallback.getInvoice().getInvoice().isRectified())) {
				
				AonTableButton rectLabel = (invoiceCallback.getInvoice().getInvoice().isRectified())
					?new AonTableButton(AON.MSG.seeRectifierInvoice(),AON.CSS.aonIconLaunch())
					:new AonTableButton(AON.MSG.seeRectifiedInvoice(),AON.CSS.aonIconLaunch());
				rectLabel.addStyleName(AON.CSS.aonMarginLeft());
				rectLabel.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						ACCOUNT_ENTRY_SERVICE.getAccountingInvoiceFromInvoice(invoiceCallback.getCurrentDomainName()
								,invoiceCallback.getCurrentDomainId()
								,invoiceCallback.getCurrentUser()
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
						});								
					}
				});
				labelsPanel.add(rectLabel);
			}
			if (!guest 
					 && !invoiceCallback.getInvoice().getInvoice().isRectifier() 
					 && !invoiceCallback.getInvoice().isUndeductible()) {
						AonTableButton rectify  = new AonTableButton(AON.MSG.rectifyInvoice(),AON.CSS.aonIconSwap());
						rectify.addStyleName(AON.CSS.aonMarginLeft());
						rectify.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								final InvoiceRectificationData data = new InvoiceRectificationData();
								data.setIssueDate(invoiceCallback.getInvoice().getInvoice().getIssueDate());
								data.setType(invoiceCallback.getInvoice().getInvoice().getType());
								data.setRectificationtype(RectificationType.NORMAL_RECTIFIER);
								data.setSettleFinances(true);
								final CustomDialog dialog = new CustomDialog();
								dialog.setCaption(AON.MSG.rectifyInvoice());
								
								final InvoiceRectificationDataPanel rectPanel = new InvoiceRectificationDataPanel();
								rectPanel.show(invoiceCallback.getCurrentDomainName()
										,invoiceCallback.getCurrentDomainId()
										,invoiceCallback.getCurrentUser()
										,invoiceCallback.getConfiguration()
										,data
										, new InvoiceRectificationDataPanelCallback() {
									
									@Override
									public void onCancel() {
										dialog.hide();
									}
									
									@Override
									public void onAccept(InvoiceRectificationData data) {
										ACCOUNT_ENTRY_SERVICE.rectifyInvoice(invoiceCallback.getCurrentDomainName()
												,invoiceCallback.getCurrentDomainId()
												,invoiceCallback.getCurrentUser()
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
								
								Scheduler.get().scheduleDeferred(new Command() {
									public void execute() {
										rectPanel.setFocus(true);
									}
								});		
								
							}
						});
						labelsPanel.add(rectify);
					}
			
		}
		regTable.add(labelsPanel);
		
		AonTableButton helpButton  = new AonTableButton(AON.MSG.help(),AON.CSS.aonIconHelp());
		helpButton.addStyleName(AON.CSS.aonMarginRight());
		helpButton.addStyleName(AON.CSS.aonMarginLeft());
		helpButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
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
		    	okButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						dialog.hide();
					}
				});
		    	buttons.add(okButton);
		    	
				tabContainer.add( buttons );
				dialog.add( tabContainer );
				dialog.center();
				dialog.show();
			}
		});
		regTable.add(helpButton);
		
		
		FlowPanel invoiceRootPanel = new FlowPanel();
		invoiceRootPanel.add(registryPanel);
		invoiceRootPanel.getElement().getStyle().setProperty("min-width", "850px");
		
		invoicePanelContainer = new SimplePanel();
		invoicePanelContainer.setStyleName(AON.CSS.aonWidthAll());
		
		invoiceRootPanel.add(invoicePanelContainer);
		
		ScrollPanel rootScrollPanel = new ScrollPanel();
		rootScrollPanel.setStyleName(AON.CSS.aonScrollArea());
		rootScrollPanel.setWidget(invoiceRootPanel);
		
		LOGGER.info("EditablePanel Constructor " + (invoiceCallback.getInvoice() != null && invoiceCallback.getInvoice().getInvoice() != null && invoiceCallback.getInvoice().getInvoice().getId() != null));

		if ( invoiceCallback.getInvoice() != null && invoiceCallback.getInvoice().getInvoice() != null) {
			undeductible.setValue(invoiceCallback.getInvoice().isUndeductible());
			if (invoiceCallback.getInvoice().getRegistry() != null) {
				invoicePanelContainer.setWidget(editInvoice(invoiceCallback));
				registryBox.setValue(invoiceCallback.getInvoice().getRegistry(),false);
			}
		} else {
			FlowPanel dropPanel = getDropFileZone( invoiceCallback );
			invoicePanelContainer.setWidget(dropPanel);
		}
		
		setStyleName(AON.CSS.aonWidthAll());
		setWidget(rootScrollPanel);
	}

	private void registryChanged(InvoicePanelCallback invoiceCallback, AccountingRegistry ar) {
		boolean preserveData = (invoiceCallback.getInvoice() != null 
				&& invoiceCallback.getInvoice().hasTotal()
				&& invoiceCallback.getInvoice().getInvoice() != null
				&& invoiceCallback.getInvoice().getInvoice().getType() == ar.getType().getInvoiceType()  
				);
		initializeInvoice(invoiceCallback, ar , preserveData);
	}
	
	protected void initializeInvoice(InvoicePanelCallback invoiceCallback, AccountingRegistry ar, boolean preserveData) {
		if (preserveData) {
			ACCOUNT_ENTRY_SERVICE.initializeInvoice(
					invoiceCallback.getCurrentDomainName()
					,invoiceCallback.getCurrentDomainId()
					,invoiceCallback.getCurrentUser()
					,ar
					,invoiceCallback.getInvoice()
					,true
					,new AsyncCallback<AccountingInvoice>() {
						
						@Override
						public void onSuccess(AccountingInvoice result) {
							if (invoiceCallback.getInvoice().isDocumentAttached()) {
								result.setAttach(invoiceCallback.getInvoice().getAttach());
							}
							AccountEntry ae = invoiceCallback.getInvoice().getAccountEntry();
							invoiceCallback.setInvoice(result);
							invoiceCallback.setAccountEntry(ae);
							undeductible.setVisible((result.isExpenses() || result.isUndeductible()) && invoiceCallback.getInvoice().getInvoice().getId() == null);
							
							Account account = new Account();
							account.setId(ar.getAccountId());
							account.setCode(ar.getAccountCode());
							account.setDescription(ar.getAccountDescription());
							if (ar.getAccountId() != null) {
								invoiceCallback.getModule().onBalance(account);
							}
							
							invoiceCallback.paintEntry();
							invoicePanelContainer.setWidget(editInvoice(invoiceCallback));
						}
						
						@Override
						public void onFailure(Throwable caught) {
							invoiceCallback.getModule().onError(caught.getMessage());
						}
					});
		} else {
			ACCOUNT_ENTRY_SERVICE.initializeInvoice(
					invoiceCallback.getCurrentDomainName()
					,invoiceCallback.getCurrentDomainId()
					,invoiceCallback.getCurrentUser()
					,ar
					,invoiceCallback.getModule().getActivity()
					,invoiceCallback.getModule().getEntryDate()
					,new AsyncCallback<AccountingInvoice>() {
						
						@Override
						public void onSuccess(AccountingInvoice result) {
							if (invoiceCallback.getInvoice().isDocumentAttached()) {
								result.setAttach(invoiceCallback.getInvoice().getAttach());
							}
							AccountEntry ae = invoiceCallback.getInvoice().getAccountEntry();
							invoiceCallback.setInvoice(result);
							invoiceCallback.setAccountEntry(ae);
							undeductible.setVisible((result.isExpenses() || result.isUndeductible()) && invoiceCallback.getInvoice().getInvoice().getId() == null);
							
							Account account = new Account();
							account.setId(ar.getAccountId());
							account.setCode(ar.getAccountCode());
							account.setDescription(ar.getAccountDescription());
							if (ar.getAccountId() != null) {
								invoiceCallback.getModule().onBalance(account);
							}
							
							invoiceCallback.paintEntry();
							invoicePanelContainer.setWidget(editInvoice(invoiceCallback));
						}
						
						@Override
						public void onFailure(Throwable caught) {
							invoiceCallback.getModule().onError(caught.getMessage());
						}
					});
		}
	}

	protected void initializeInvoice(InvoicePanelCallback invoiceCallback, AccountingRegistry ar) {
		ACCOUNT_ENTRY_SERVICE.initializeInvoice(
				invoiceCallback.getCurrentDomainName()
				,invoiceCallback.getCurrentDomainId()
				,invoiceCallback.getCurrentUser()
				,ar
				,invoiceCallback.getModule().getActivity()
				,invoiceCallback.getModule().getEntryDate()
				,new AsyncCallback<AccountingInvoice>() {
					
					@Override
					public void onSuccess(AccountingInvoice result) {
						if (invoiceCallback.getInvoice().isDocumentAttached()) {
							result.setAttach(invoiceCallback.getInvoice().getAttach());
						}
						AccountEntry ae = invoiceCallback.getInvoice().getAccountEntry();
						invoiceCallback.setInvoice(result);
						invoiceCallback.setAccountEntry(ae);
						undeductible.setVisible((result.isExpenses() || result.isUndeductible()) && invoiceCallback.getInvoice().getInvoice().getId() == null);
						
						Account account = new Account();
						account.setId(ar.getAccountId());
						account.setCode(ar.getAccountCode());
						account.setDescription(ar.getAccountDescription());
						if (ar.getAccountId() != null) {
							invoiceCallback.getModule().onBalance(account);
						}
						
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

	private void decorateTaxDate(AccountingInvoice ai) {
		Date issue = ai.getInvoice().getIssueDate();
		Date tax = ai.getInvoice().getTaxDate();
		if(    (issue == null && tax != null)
			|| (issue != null && tax == null)
			|| (issue.compareTo(tax) != 0)) {
			taxDate.addStyleName(AON.CSS.aonBackgroundOrange());
		} else {
			taxDate.removeStyleName(AON.CSS.aonBackgroundOrange());
		}
	}

	protected void headerDataChanged(IInvoicePanelCallback invoiceCallback) {
		vatPanel.headerInfoChanged();
		withholdingPanel.setVisible(invoiceCallback.getInvoice().isWithholding());
		withholdingPanel.setValue(invoiceCallback.getInvoice().getWithholdingData());
		invoiceTotal.setValue(invoiceCallback.getInvoice().getTotalInvoice(),false);
		invoiceCallback.paintEntry();
		invoiceCallback.getInvoice().getAccountEntry().setDirty(true);
		invoiceCallback.getModule().refreshIdLabel();
	}

	private String getInvoiceLabel(AccountingInvoice ai) {
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
		if (invoiceType == InvoiceType.SALES   ) l = "Factura "+x+"de Ventas";
		else if (invoiceType == InvoiceType.EXPENSES) l = "Factura "+x+"de Gastos";
		else if (invoiceType == InvoiceType.PURCHASE) l = "Factura "+x+"de Compra";
		else if (invoiceType == InvoiceType.UNDEDUCTIBLE) l = "Ticket/Gasto no Ded.";
		else if (invoiceType == InvoiceType.UNDEDUCTIBLE) l = "Ticket/Gasto no Ded.";
		else l = "Nueva factura"; 
		l = l + (ai.isInvestment()?". (Inv)":"");
		l = l + (ai.isVatAccrualPayment()?". (Crit.Caja)":"");
		return l;
	}

	private void repeatLastInvoice(InvoicePanelCallback invoiceCallback, final ISelectionCallback cbk) {
		Integer registryId = invoiceCallback.getInvoice().getRegistry().getId();
		ACCOUNT_ENTRY_SERVICE.getRegistryLastAccountingInvoice(invoiceCallback.getCurrentDomainName()
				,invoiceCallback.getCurrentDomainId(),invoiceCallback.getCurrentUser(), registryId
				,new AsyncCallback<AccountingInvoice>() {
						@Override
						public void onSuccess(AccountingInvoice result) {
							if (result == null) {
								invoiceCallback.getModule().onError("No se ha encontrado ninguna factura");
							} else {
								result.setAccountEntry(invoiceCallback.getInvoice().getAccountEntry());
								result.getInvoice().setIssueDate(invoiceCallback.getModule().getEntryDate());
								result.getInvoice().setTaxDate(invoiceCallback.getModule().getEntryDate());
								if (!result.getInvoice().isSales()) {
									result.getInvoice().setReferenceCode(referenceCode.getValue());
								}
								if (result.getInvoice().hasFinances()) {
									result.getInvoice().getFinances().get(0).setDueDate(invoiceCallback.getModule().getEntryDate());
									// TODO Manage due dates for all finances.
								}
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
		
		FullDocument fullDocument = new FullDocument();
		TextBox rName = new TextBox();
		taxDate = new DateBoxEx();
		InvoiceTransactionListBox transactionBox = new InvoiceTransactionListBox();
		IntegerBox number = new IntegerBox();
		service = new CheckLabel(AON.MSG.service() , 80);
		rectifier = new CheckLabel(AON.MSG.rectifiedInvoice() ,120);
		prepayment = new CheckLabel(AON.MSG.hasPrepayments(),150);
		investment = new CheckLabel(AON.MSG.investAsset(),120);
		withholding = new CheckLabel(AON.MSG.withholding(),110);
		surcharge = new CheckLabel(AON.MSG.surcharge() ,130);
		vatAccrualPayment = new CheckLabel(AON.MSG.vatAccrualPaymentAbbr(),100);
		withholdingFarmer = new CheckLabel(AON.MSG.withholdingFarmerAbbr(),150);
		duaLinked = new CheckLabel("DUA",80);
		
		TextBox manualConcept = new TextBox();
		
		invoiceCallback.getModule().getEntryDateBox().addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				invoiceCallback.getInvoice().getAccountEntry().setEntryDate(event.getValue());
				if (invoiceCallback.getInvoice().getInvoice() != null) {
					invoiceCallback.getInvoice().getInvoice().setIssueDate(event.getValue());
					invoiceCallback.getInvoice().getInvoice().setTaxDate(event.getValue());
					taxDate.setValue(event.getValue());
					invoiceCallback.paintEntry();
					financePanel.invoiceDateIssueChanged(invoiceCallback);
				}
			}
		});
		invoiceCallback.getModule().getActivityBox().addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				Integer act = AonNumberUtils.toInteger(invoiceCallback.getModule().getActivityBox().getSelectedValue());
				invoiceCallback.getInvoice().getAccountEntry().setActivity(act);
				invoiceCallback.getInvoice().getInvoice().setActivity(act);
			}
		});
		invoiceCallback.getModule().getConfidentialBox().addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				boolean conf = invoiceCallback.getModule().getConfidentialBox().getValue();
				invoiceCallback.getInvoice().getAccountEntry().setConfidential(conf);
				invoiceCallback.getInvoice().getInvoice().setConfidential(conf);
			}
		});

		fullDocument.setValue(inv.getInvoice().getRegistryDocumentType(),inv.getInvoice().getRegistryDocumentCountry(),inv.getInvoice().getRegistryDocument());
		rName.setValue(inv.getInvoice().getRegistryName());
		taxDate.setValue(inv.getInvoice().getTaxDate());
		transactionBox.setValue(inv.getTransaction());
		
		series = new ListBox();
		referenceCode = new TextBox();
		
		if (inv.isSales()) {
			series.addItem(" --- ","");
			if (invoiceCallback.getConfiguration().getInvoiceSalesSeries() != null 
				&& invoiceCallback.getConfiguration().getInvoiceSalesSeries().size() > 0) {
				LinkedList<String> rectificationSeries = invoiceCallback.getConfiguration().getInvoiceRectificationSalesSeries();
				for (String ser : invoiceCallback.getConfiguration().getInvoiceSalesSeries()) {
					boolean rectifierSerie = (rectificationSeries != null && rectificationSeries.contains(ser));
					if (!rectifierSerie || (rectifierSerie && inv.getInvoice().isRectifier())) {
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
			number.setValue(inv.getInvoice().getNumber());
		} else {
			referenceCode.setValue(inv.getInvoice().getReferenceCode());
		}
		
		series.setVisible( inv.isSales() );
		number.setVisible( inv.isSales() );
		referenceCode.setVisible( !inv.isSales() );
		
		invoiceTotal = new AonDoubleBox();
		invoiceTotal.setValue(inv.getTotalInvoice());
		manualConcept.setValue(inv.getManualConcept());
		
		vatPanel = new InvoiceVATPanel( new EditableInvoicePanelCallback() );
		financePanel = new InvoiceFinancePanel(invoiceCallback);
		financePanel.addSelectionHandler(new AccountEntrySelectionHandler() {
			@Override
			public void onSelection(AccountEntrySelectionEvent event) {
				AccountEntrySelectionEvent.fire( EditableInvoicePanel.this, event.getSelectedItem(), null);
			}
		});

		FlowPanel invoicePanel = new FlowPanel();
		invoicePanel.setStyleName(AON.CSS.aonWidthAll());
		
		// *************************************************************************
		// ** PANEL ( Datos de la factura) *****************************************
		// *************************************************************************
		FlexTable invoiceDataTable = new FlexTable();
		invoiceDataTable.getColumnFormatter().setWidth(0, "40px");
		invoiceDataTable.getColumnFormatter().setWidth(1, "auto");
		
		invoiceDataTable.setStyleName(AON.CSS.aonTable());
		invoiceDataTable.addStyleName(AON.CSS.aonMarginTop());
		invoiceDataTable.addStyleName(AON.CSS.aonWidthAll());
		// invoiceDataTable.addStyleName(AON.CSS.aonSimpleBorder());
		invoiceDataTable.getElement().getStyle().setBackgroundColor(INNER_BACKGROUND_COLOR);

		
		// *************************************************************************
		// ***************** PANEL ( documento y nombre del titular ) **************
		// *************************************************************************
		
		Label description = new Label("Datos");
		description.setStyleName(AON.CSS.aonTextVertical());
		description.addStyleName(AON.CSS.aonBold());
		invoiceDataTable.setWidget(0, 0, description);
		invoiceDataTable.getFlexCellFormatter().setRowSpan(0, 0, 4);
		invoiceDataTable.getCellFormatter().getElement(0, 0).getStyle().setBackgroundColor(LABEL_BACKGROUND_COLOR);
		
		FlowPanel headerPanel1 = new  FlowPanel();
		invoiceDataTable.setWidget(0, 1, headerPanel1);
		headerPanel1.setStyleName(AON.CSS.aonAccountingInvoicePanel());
		
		InlineLabel documentLabel = new InlineLabel(AON.MSG.document());
		documentLabel.setStyleName(AON.CSS.aonInnerLabel());
		documentLabel.getElement().getStyle().setWidth(80, Unit.PX);
		headerPanel1.add(documentLabel);
		
		
		fullDocument.addTypeChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent arg0) {
				invoiceCallback.getInvoice().getInvoice().setRegistryDocumentType(fullDocument.getType());
			}
		});
		fullDocument.addCountryChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent arg0) {
				invoiceCallback.getInvoice().getInvoice().setRegistryDocumentCountry(fullDocument.getCountry());
			}
		});
		fullDocument.addDocumentChangeHandler(new  ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> arg0) {
				invoiceCallback.getInvoice().getInvoice().setRegistryDocument(fullDocument.getDocument());
			}
		});
		headerPanel1.add(fullDocument);
		InlineLabel nameLabel = new InlineLabel(AON.MSG.name());
		nameLabel.setStyleName(AON.CSS.aonInnerLabel());
		headerPanel1.add(nameLabel);
		
		rName.setStyleName(AON.CSS.aonInputText());
		rName.setVisibleLength(50);
		rName.setMaxLength(50);
		rName.addValueChangeHandler(new  ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> arg0) {
				invoiceCallback.getInvoice().getInvoice().setRegistryName(rName.getValue());
				if (AonStringUtils.isBlank(invoiceCallback.getInvoice().getManualConcept())) {
					invoiceCallback.getInvoice().setManualConcept( rName.getValue());
					headerDataChanged(invoiceCallback);
				}
			}
		});
		headerPanel1.add(rName);
		
		
		// *************************************************************************
		// ***************** PANEL ( Fecha IVA, set de checks 1) *******************
		// *************************************************************************

		FlowPanel headerPanel2 = new  FlowPanel();
		invoiceDataTable.setWidget(1, 0, headerPanel2);
		headerPanel2.setStyleName(AON.CSS.aonAccountingInvoicePanel());
		
		InlineLabel taxDateLabel = new InlineLabel(AON.MSG.taxDate());
		taxDateLabel.setStyleName(AON.CSS.aonInnerLabel());
		taxDateLabel.getElement().getStyle().setWidth(80, Unit.PX);
		taxDateLabel.setVisible(!invoiceCallback.getInvoice().isUndeductible());
		headerPanel2.add(taxDateLabel);
		
		FlowPanel taxDateContainer = new FlowPanel();
		taxDateContainer.getElement().getStyle().setWidth(100, Unit.PX);
		
		taxDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				invoiceCallback.getInvoice().getInvoice().setTaxDate(event.getValue());
				decorateTaxDate(invoiceCallback.getInvoice());
			}
		});
		taxDate.setVisible(!invoiceCallback.getInvoice().isUndeductible());
		taxDateContainer.add(taxDate);
		headerPanel2.add(taxDateContainer);
		
		// -------------------------------
		// --------- TRANSACTION ---------
		// -------------------------------
		InlineLabel transactionLabel = new InlineLabel(AON.MSG.transaction());
		transactionLabel.setStyleName(AON.CSS.aonInnerLabel());
		transactionLabel.getElement().getStyle().setWidth(80, Unit.PX);
		transactionLabel.setVisible(!invoiceCallback.getInvoice().isUndeductible());
		headerPanel2.add(transactionLabel);
		
		FlowPanel transactionContainer = new FlowPanel();
		transactionContainer.getElement().getStyle().setWidth(150, Unit.PX);

		transactionBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				invoiceCallback.getInvoice().getInvoice().setTransaction(transactionBox.getValue());
				InvoiceCalculator.calculate(invoiceCallback.getInvoice());
				enableChecks(invoiceCallback);
				headerDataChanged(invoiceCallback);
			}
		});
		transactionContainer.add(transactionBox);
		transactionBox.setVisible(!invoiceCallback.getInvoice().isUndeductible());
		headerPanel2.add(transactionContainer);

		// ---------------------------
		// --------- SERVICE ---------
		// ---------------------------
		if (invoiceCallback.getInvoice().isExpenses()) {
			service.paint(true);
		} else {
			service.paint(invoiceCallback.getInvoice().isService());
			service.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					invoiceCallback.getInvoice().getInvoice().setService( !invoiceCallback.getInvoice().isService() );
					service.paint(invoiceCallback.getInvoice().isService());
					InvoiceCalculator.calculate(invoiceCallback.getInvoice());
					headerDataChanged(invoiceCallback);
				}
			});
		}
		headerPanel2.add(service);

		// -------------------------------------
		// --------- Fra. Rectificativa --------
		// -------------------------------------
		rectifier.paint(invoiceCallback.getInvoice().getInvoice().isRectifier());
		rectifier.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				invoiceCallback.getInvoice().getInvoice().setNormalRectifier(!invoiceCallback.getInvoice().getInvoice().isRectifier());
				invoiceTypeLabel.setText( getInvoiceLabel(invoiceCallback.getInvoice()));
				rectifier.paint(invoiceCallback.getInvoice().getInvoice().isRectifier());
				headerDataChanged(invoiceCallback);
			}
		});
		headerPanel2.add(rectifier);

		// -------------------------------------
		// --------- CONTIENE SUPLIDOS ---------
		// -------------------------------------
		prepayment.paint(invoiceCallback.getInvoice().hasPrepayments());
		prepayment.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if ( !invoiceCallback.getInvoice().isDuaLinked() ) {
					invoiceCallback.getInvoice().setPrepayments(!invoiceCallback.getInvoice().hasPrepayments());
					prepayment.paint(invoiceCallback.getInvoice().hasPrepayments());
					headerDataChanged(invoiceCallback);
				} else {
					MessageDialog.error("No se puede modificar si la factura est\u00E1 vinculada a un DUA");
				}
			}
		});
		headerPanel2.add(prepayment);

		// *************************************************************************
		// ***************** PANEL ( set de checks 1) *****************
		// *************************************************************************
		FlowPanel headerPanel3 = new  FlowPanel();
		invoiceDataTable.setWidget(2, 0, headerPanel3);
		headerPanel3.setStyleName(AON.CSS.aonAccountingInvoicePanel());
		headerPanel3.addStyleName(AON.CSS.aonPaddingTop());
		
		// ----------------------------------
		// --------- EMPTY LABEL ------------
		// ----------------------------------
		InlineLabel emptyLabel1 = new InlineLabel();
		emptyLabel1.setStyleName(AON.CSS.aonInnerLabel());
		emptyLabel1.getElement().getStyle().setWidth(20, Unit.PX);
		emptyLabel1.setVisible(!invoiceCallback.getInvoice().isUndeductible());
		headerPanel3.add(emptyLabel1);

		// -------------------------------------
		// --------- Aplicar retencion ---------
		// -------------------------------------
		withholding.paint(invoiceCallback.getInvoice().getInvoice().isWithholding());
		withholding.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				invoiceCallback.getInvoice().getInvoice().setWithholding(!invoiceCallback.getInvoice().getInvoice().isWithholding());
				withholding.paint(invoiceCallback.getInvoice().getInvoice().isWithholding());
				vatPanel.withholdingChanged( invoiceCallback.getInvoice().getInvoice().isWithholding() );
				for (InvoiceVAT vat : invoiceCallback.getInvoice().getVats()) {
					vat.setWithholding(invoiceCallback.getInvoice().getInvoice().isWithholding());
				}
				InvoiceCalculator.calculate(invoiceCallback.getInvoice());
				headerDataChanged(invoiceCallback);
			}
		});
		headerPanel3.add(withholding);

		// -------------------------------------------
		// --------- RECARGO DE EQUIVALENCIA ---------
		// -------------------------------------------
		surcharge.paint(invoiceCallback.getInvoice().getInvoice().isSurcharge());
		surcharge.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				invoiceCallback.getInvoice().getInvoice().setSurcharge(!invoiceCallback.getInvoice().getInvoice().isSurcharge());
				surcharge.paint(invoiceCallback.getInvoice().getInvoice().isSurcharge());
				InvoiceCalculator.calculate(invoiceCallback.getInvoice());
				headerDataChanged(invoiceCallback);
			}
		});
		headerPanel3.add(surcharge);
		
		// -------------------------------------------
		// --------- Reg. agric, gan y pesca ---------
		// -------------------------------------------
		withholdingFarmer.paint(invoiceCallback.getInvoice().getInvoice().isWithholdingFarmer());
		withholdingFarmer.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				invoiceCallback.getInvoice().getInvoice().setWithholdingFarmer(!invoiceCallback.getInvoice().getInvoice().isWithholdingFarmer());
				withholdingFarmer.paint(invoiceCallback.getInvoice().getInvoice().isWithholdingFarmer());
				InvoiceCalculator.calculate(invoiceCallback.getInvoice());
				headerDataChanged(invoiceCallback);
			}
		});
		headerPanel3.add(withholdingFarmer);

		// --------------------------------------------
		// --------- Regimne criterio de caja ---------
		// --------------------------------------------
		vatAccrualPayment.paint(invoiceCallback.getInvoice().getInvoice().isVatAccrualPayment());
		vatAccrualPayment.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				invoiceCallback.getInvoice().getInvoice().setVatAccrualPayment(!invoiceCallback.getInvoice().getInvoice().isVatAccrualPayment());
				invoiceTypeLabel.setText( getInvoiceLabel(invoiceCallback.getInvoice()));
				vatAccrualPayment.paint(invoiceCallback.getInvoice().getInvoice().isVatAccrualPayment());
			}
		});
		headerPanel3.add(vatAccrualPayment);

		// ------------------------
		// --------- DUA ----------
		// ------------------------
		duaLinked.paint(invoiceCallback.getInvoice().isDuaLinked() && invoiceCallback.getInvoice().getInvoice().isDUAAllowed());
		duaLinked.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				invoiceCallback.getInvoice().setDuaLinked(!invoiceCallback.getInvoice().isDuaLinked());
				duaLinked.paint(invoiceCallback.getInvoice().isDuaLinked());
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
			}
		});
		headerPanel3.add(duaLinked);

		// *************************************************************************
		// ***************** PANEL ( Número Factura, total factura ) ******
		// *************************************************************************
		FlowPanel headerPanel4 = new  FlowPanel();
		invoiceDataTable.setWidget(3, 0, headerPanel4);
		headerPanel4.setStyleName(AON.CSS.aonAccountingInvoicePanel());
		headerPanel4.addStyleName(AON.CSS.aonPaddingTop());
		
		// ---------------------------------------
		// --------- BIENES DE INVERSION ---------
		// ---------------------------------------
		investment.paint(invoiceCallback.getInvoice().isInvestment());
		investment.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				invoiceCallback.getInvoice().getInvoice().setInvestment( !invoiceCallback.getInvoice().isInvestment() );
				invoiceTypeLabel.setText( getInvoiceLabel(invoiceCallback.getInvoice()));
				investment.paint(invoiceCallback.getInvoice().isInvestment());
			}
		});
		headerPanel4.add(investment);
		
		// ----------------------------------
		// --------- EMPTY LABEL ------------
		// ----------------------------------
		InlineLabel emptyLabel2 = new InlineLabel();
		emptyLabel2.setStyleName(AON.CSS.aonInnerLabel());
		emptyLabel2.getElement().getStyle().setWidth(30, Unit.PX);
		emptyLabel2.setVisible(!invoiceCallback.getInvoice().isUndeductible());
		headerPanel4.add(emptyLabel2);

		// ----------------------------------
		// --------- NUMERO FACTURA ---------
		// ----------------------------------
		InlineLabel invoiceNumberLabel = new InlineLabel(AON.MSG.invoiceNumber());
		invoiceNumberLabel.setStyleName(AON.CSS.aonInnerLabel());
		invoiceNumberLabel.addStyleName(AON.CSS.aonBold());
		invoiceNumberLabel.getElement().getStyle().setWidth(80,Unit.PX);
		headerPanel4.add(invoiceNumberLabel);
		
			// -------------------------
			// --------- SERIE ---------
			// -------------------------
		FlowPanel numberPanel = new FlowPanel();
		series.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				invoiceCallback.getInvoice().getInvoice().setSeries(series.getSelectedIndex()==0?null:series.getSelectedValue());
				FINANCE_SERVICE.getInvoiceNextNumber(
						invoiceCallback.getCurrentDomainName()
						,invoiceCallback.getCurrentDomainId()
						,invoiceCallback.getCurrentUser()
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
								invoiceCallback.paintEntry();
								invoiceCallback.getInvoice().getAccountEntry().setDirty(true);
								invoiceCallback.getModule().refreshIdLabel();
							}
						});
			}
		});
		numberPanel.add(series);
		
			// -------------------------
			// --------- NUMBER---------
			// -------------------------
		number.setStyleName(AON.CSS.aonMarginLeft());
		number.addStyleName(AON.CSS.aonInputText());
		number.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				invoiceCallback.getInvoice().getInvoice().setNumber(number.getValue());
				invoiceCallback.paintEntry();
				invoiceCallback.getInvoice().getAccountEntry().setDirty(true);
				invoiceCallback.getModule().refreshIdLabel();
			}
		});
		number.setVisibleLength(8);
		number.setMaxLength(8);
		numberPanel.add(number);
		
			// ----------------------------------
			// --------- REFERENCE CODE ---------
			// ----------------------------------
		referenceCode.setStyleName(AON.CSS.aonInputText());
		referenceCode.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				invoiceCallback.getInvoice().getInvoice().setReferenceCode(referenceCode.getValue());
				invoiceCallback.paintEntry();
				invoiceCallback.getInvoice().getAccountEntry().setDirty(true);
				invoiceCallback.getModule().refreshIdLabel();
			}
		});

		referenceCode.setVisibleLength(15); 
		referenceCode.setMaxLength(32);
		numberPanel.add(referenceCode);

		headerPanel4.add(numberPanel);
		
		// ----------------------------------
		// --------- INVOICE TOTAL ----------
		// ----------------------------------
		InlineLabel label0 = new InlineLabel(AON.MSG.invoiceTotal());
		label0.setStyleName(AON.CSS.aonInnerLabel());
		label0.addStyleName(AON.CSS.aonMarginLeft());
		label0.addStyleName(AON.CSS.aonBold());
		headerPanel4.add(label0);

		invoiceTotal.addKeyUpHandler( new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					repeatLastInvoice(invoiceCallback, new ISelectionCallback() {

						@Override
						public void onSuccess() {
//							fastSave.setFocus(true);
						}

						@Override
						public void onFailure() {
							invoiceCallback.getModule().onError(AON.MSG.invoiceNotFound());
						}
						
					});
				}
			}
		});
		invoiceTotal.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				if (AonNumberUtils.isNumber(invoiceTotal.getText())) {
					if (invoiceTotal.getValue() == null) invoiceTotal.setValue(0.0, false);
					if (invoiceCallback.getInvoice().getVats() != null &&  invoiceCallback.getInvoice().getVats().size() == 1) {
						InvoiceCalculator.reverseCalculate(invoiceCallback.getInvoice(),invoiceTotal.getValue());
						vatPanel.populateFirstVat();
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
			}
		});
		invoiceTotal.setVisibleLength(12);
		headerPanel4.add(invoiceTotal);
		
		invoicePanel.add(invoiceDataTable);
		// *************************************************************************
		// ** PANEL ( Bases Imponibles) ********************************************
		// *************************************************************************

		vatPanel.addValueChangeHandler(new ValueChangeHandler<InvoiceVAT>() {
			@Override
			public void onValueChange(ValueChangeEvent<InvoiceVAT> event) {
				InvoiceCalculator.calculate(invoiceCallback.getInvoice());
				withholdingPanel.setValue(invoiceCallback.getInvoice().getWithholdingData());
				invoiceTotal.setValue(invoiceCallback.getInvoice().getInvoice().getTotal(),false);
				financePanel.invoiceTotalChanged(invoiceCallback);
				invoiceCallback.paintEntry();
				invoiceCallback.getInvoice().getAccountEntry().setDirty(true);
				invoiceCallback.getModule().refreshIdLabel();
			}
		});
		vatPanel.addSelectionHandler(new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				invoiceCallback.getModule().onBalance(event.getSelectedItem());
				invoiceCallback.paintEntry();
				invoiceCallback.getInvoice().getAccountEntry().setDirty(true);
				invoiceCallback.getModule().refreshIdLabel();
			}
		});

		FlexTable vatTable = new FlexTable();
		vatTable.getColumnFormatter().setWidth(0, "40px");
		vatTable.getColumnFormatter().setWidth(1, "auto");
		
		vatTable.setStyleName(AON.CSS.aonTable());
		vatTable.addStyleName(AON.CSS.aonWidthAll());
		vatTable.addStyleName(AON.CSS.aonMarginTop());
		vatTable.getElement().getStyle().setBackgroundColor(INNER_BACKGROUND_COLOR);
		
		Label baseDescription = new Label("Bases");
		baseDescription.setStyleName(AON.CSS.aonTextVertical());
		baseDescription.addStyleName(AON.CSS.aonBold());
		vatTable.setWidget(0, 0, baseDescription);
		vatTable.getCellFormatter().getElement(0, 0).getStyle().setBackgroundColor(LABEL_BACKGROUND_COLOR);
		//vatTable.getCellFormatter().getElement(0, 0).getStyle().setHeight(80.0, Unit.PX);
		vatTable.setWidget(0, 1, vatPanel);
		invoicePanel.add(vatTable);

		// *************************************************************************
		// ** PANEL ( IRPF) ********************************************************
		// *************************************************************************
		duaPanelContainer = new FlowPanel();
		invoicePanel.add( duaPanelContainer );
		
		// *************************************************************************
		// ** PANEL ( IRPF) ********************************************************
		// *************************************************************************
		
		withholdingPanel = new InvoiceWithholdingPanel( invoiceCallback );
		withholdingPanel.setVisible(invoiceCallback.getInvoice().isWithholding());
		withholdingPanel.setValue(invoiceCallback.getInvoice().getWithholdingData());		
		withholdingPanel.addValueChangeHandler(new ValueChangeHandler<InvoiceWithholding>() {
			@Override
			public void onValueChange(ValueChangeEvent<InvoiceWithholding> event) {
				InvoiceCalculator.calculate(invoiceCallback.getInvoice());
				withholdingPanel.setValue(invoiceCallback.getInvoice().getWithholdingData());
				invoiceTotal.setValue(invoiceCallback.getInvoice().getInvoice().getTotal(),false);
				invoiceCallback.paintEntry();
				invoiceCallback.getInvoice().getAccountEntry().setDirty(true);
				invoiceCallback.getModule().refreshIdLabel();
			}
		});
		withholdingPanel.addSelectionHandler(new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				invoiceCallback.getModule().onBalance(event.getSelectedItem());
				invoiceCallback.paintEntry();
				invoiceCallback.getInvoice().getAccountEntry().setDirty(true);
				invoiceCallback.getModule().refreshIdLabel();
			}
		});
		invoicePanel.add( withholdingPanel );

		// *************************************************************************
		// ** PANEL ( FINANCES) ****************************************************
		// *************************************************************************
		FlexTable financesTable = new FlexTable();
		financesTable.getColumnFormatter().setWidth(0, "40px");
		financesTable.getColumnFormatter().setWidth(1, "auto");
		
		financesTable.setStyleName(AON.CSS.aonTable());
		financesTable.addStyleName(AON.CSS.aonWidthAll());
		financesTable.addStyleName(AON.CSS.aonMarginTop());
		financesTable.getElement().getStyle().setBackgroundColor(INNER_BACKGROUND_COLOR);
		
		Label financesDescription = new Label("VTOS");
		financesDescription.setStyleName(AON.CSS.aonTextVertical());
		financesDescription.addStyleName(AON.CSS.aonBold());
		financesTable.setWidget(0, 0, financesDescription);
		financesTable.getCellFormatter().getElement(0, 0).getStyle().setBackgroundColor(LABEL_BACKGROUND_COLOR);
		financesTable.setWidget(0, 1, financePanel);
		invoicePanel.add(financesTable);

		
		invoiceTypeLabel.setText( getInvoiceLabel(invoiceCallback.getInvoice()));
		invoicePanel.setVisible(true);
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				if (invoiceCallback.getInvoice().isSales()) {
					series.setFocus(true);
				} else {
					referenceCode.setFocus(true);
				}
		}});

		// *************************************************************************
		// ***************** PANEL ( OTROS DATOS, concepto ... ) *******************
		// *************************************************************************
		
		FlexTable othersTable = new FlexTable();
		othersTable.getColumnFormatter().setWidth(0, "40px");
		othersTable.getColumnFormatter().setWidth(1, "auto");
		
		othersTable.setStyleName(AON.CSS.aonTable());
		othersTable.addStyleName(AON.CSS.aonWidthAll());
		othersTable.addStyleName(AON.CSS.aonMarginTop());
		othersTable.getElement().getStyle().setBackgroundColor(INNER_BACKGROUND_COLOR);
		
		Label othersDescription = new Label("");
		othersDescription.setStyleName(AON.CSS.aonTextVertical());
		othersDescription.addStyleName(AON.CSS.aonBold());
		othersTable.setWidget(0, 0, othersDescription);
		othersTable.getCellFormatter().getElement(0, 0).getStyle().setBackgroundColor(LABEL_BACKGROUND_COLOR);
		FlowPanel conceptPanel1 = new  FlowPanel();
		conceptPanel1.setStyleName(AON.CSS.aonAccountingInvoicePanel());
		
		FlowPanel manualConceptPanel = new FlowPanel();
		manualConceptPanel.setStyleName(AON.CSS.aonPaddingLeft());
		manualConceptPanel.getElement().getStyle().setProperty("flex-grow", "1");
		
		InlineLabel label1 = new InlineLabel(AON.MSG.conceptComplement());
		label1.setStyleName(AON.CSS.aonInnerLabel());
		manualConceptPanel.add(label1);

		manualConcept.setStyleName(AON.CSS.aonInputText());
		manualConcept.setTabIndex(-1);
		manualConcept.setVisibleLength(20);
		manualConcept.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				invoiceCallback.getInvoice().setManualConcept(manualConcept.getValue());
				invoiceCallback.paintEntry();
				invoiceCallback.getInvoice().getAccountEntry().setDirty(true);
				invoiceCallback.getModule().refreshIdLabel();
			}
		});
		manualConceptPanel.add(manualConcept);
		conceptPanel1.add(manualConceptPanel);
		othersTable.setWidget(0, 1, conceptPanel1);
		invoicePanel.add(othersTable);
		
		invoiceTypeLabel.setText( getInvoiceLabel(invoiceCallback.getInvoice()));
		enableChecks(invoiceCallback);
		invoicePanel.setVisible(true);
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				if (invoiceCallback.getInvoice().isSales()) {
					series.setFocus(true);
				} else {
					referenceCode.setFocus(true);
				}
		}});

		if (invoiceCallback.getInvoice().isDuaLinked()) {
			duaPanel = new InvoiceDUAPanel( invoiceCallback );
			duaPanelContainer.add(duaPanel);
			duaPanel.addSelectionHandler( new SelectionHandler<IInvoicePanelCallback>() {

				@Override
				public void onSelection(SelectionEvent<IInvoicePanelCallback> event) {
					duaInvoiceChanged( event.getSelectedItem() );
				}
			});
			// duaPanel.initialize( invoiceCallback, null );
			// duaInvoiceChanged( invoiceCallback);
		} else {
			duaPanelContainer.clear();
		}

		return invoicePanel;
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
		vatPanel.paint();
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
			// duaPanel.setVisible(false);
		} else {
			invoiceCallback.getInvoice().getTransaction().visit(new IInvoiceTransactionTypeVisitor() {
				
				@Override
				public void visitNational() {
					visitCommon();
					investment.setVisible(true);
					withholding.setVisible(true);
					surcharge.setVisible(true);
					withholdingFarmer.setVisible(true);
					vatAccrualPayment.setVisible(true);
					duaLinked.setVisible(invoiceCallback.getInvoice().isExpenses());
					// duaPanel.setVisible(invoiceCallback.getInvoice().isDuaLinked());
				}
				
				@Override
				public void visitOtherISP() {
					visitNational();
				}
				
				
				@Override
				public void visitIntracommunity() {
					visitCommon();
					investment.setVisible(true);
					withholding.setVisible(false);
					surcharge.setVisible(true);
					withholdingFarmer.setVisible(false);
					vatAccrualPayment.setVisible(false);
					duaLinked.setVisible(false);
					// duaPanel.setVisible(false);
				}
				
				@Override
				public void visitExtracommunity() {
					visitCommon();
					withholding.setVisible(false);
					surcharge.setVisible(true);
					withholdingFarmer.setVisible(false);
					vatAccrualPayment.setVisible(false);
					duaLinked.setVisible(false);
					// duaPanel.setVisible(false);
				}
				
				@Override
				public void visitCanCeuMel() {
					visitExtracommunity();
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

	private FlowPanel getDropFileZone(InvoicePanelCallback invoiceCallback) {
		FileUpload fileUpload = new FileUpload();
		fileUpload.ensureDebugId("fileselect");
		fileUpload.getElement().getStyle().setDisplay(Style.Display.NONE);
		fileUpload.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				dropPanel.clear();
				if ( invoiceCallback.getConfiguration().isOCRActive() ) {
					dropPanel.add(getSplashWidget());
				} else {
					dropPanel.clear();
					registryBox.setFocus(true);
				}
				event.preventDefault();
				fileSelectHandler(fileUpload.getElement());
			}
		});
			
			
		dropPanel = new FlowPanel();
		
		FlowPanel filedrag = new FlowPanel();
		filedrag.add(fileUpload);
		Label dropZone = new Label();
		dropZone.setStyleName(AON.CSS.aonDropZone());
		dropZone.getElement().getStyle().setCursor(Style.Cursor.POINTER);
		dropZone.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				fileUpload.click();		
			}
		});
			
		dropZone.addDragOverHandler(new DragOverHandler() {
			
			@Override
			public void onDragOver(DragOverEvent event) {
				dropZone.addStyleName(AON.CSS.aonDropZoneHover());
				event.preventDefault();
				event.stopPropagation();
		        DataTransfer dataTransfer = event.getDataTransfer();
		        dataTransfer.setDropEffect(DropEffect.NONE);				
			}
		});
		dropZone.addDragLeaveHandler(new DragLeaveHandler() {
			@Override
			public void onDragLeave(DragLeaveEvent event) {
				dropZone.removeStyleName(AON.CSS.aonDropZoneHover());
				event.preventDefault();
			}
		});
		/*
		dropZone.addDropHandler(new DropHandler() {
				
			@Override
			public void onDrop(DropEvent event) {
				dropZone.removeStyleName(AON.CSS.aonDropZoneHover());
				LOGGER.info("File Droped!");
				event.preventDefault();
				event.stopPropagation();
				fileDrop(fileUpload.getElement() ,event.getNativeEvent());
			}
		});
		*/
		filedrag.add(dropZone);
		dropPanel.add(filedrag);
		return dropPanel;
	}
	
	private void setDocument(final String doc, final String name, String type) {
		LOGGER.info("BEFORE invCallback setDocument!");
		invCallback.setDocument(doc, name, type);
	}

	
	private native void fileSelectHandler(Element fileselect) /*-{
		var self = this;		
		var file = fileselect.files[0];
		var reader = new FileReader();
		reader.addEventListener("load", function () {
			self.@com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.EditableInvoicePanel::setDocument(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(reader.result,file.name,file.type); 
			}, false);
		reader.readAsDataURL( file );
	}-*/;
	
	private native void fileDrop(Element fileselect, NativeEvent e) /*-{
		if (e.dataTransfer.items) {
			e.dataTransfer.items.clear();
		} else {
			e.dataTransfer.clearData();
		}
		fileselect.files = e.target.files || e.dataTransfer.files;
//		var event = new Event('change');
//		fileselect.dispatchEvent(event);
//		fileselect.click();
		fileSelectHandler(fileselect);
	}-*/;	

}

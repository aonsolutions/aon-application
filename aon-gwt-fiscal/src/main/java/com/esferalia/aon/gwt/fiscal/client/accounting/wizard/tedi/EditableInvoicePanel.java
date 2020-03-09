package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.FullDocument;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.InvoiceTransactionListBox;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleTEDI;
import com.esferalia.aon.gwt.fiscal.client.accounting.ISelectionCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.InvoicePanel.InvoicePanelCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
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
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
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

public class EditableInvoicePanel extends SimpleLayoutPanel implements HasSelectionHandlers<AccountingInvoice>,Focusable {
	
	private static final Logger LOGGER = Logger.getLogger(EditableInvoicePanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	protected static final String INNER_BACKGROUND_COLOR = "WhiteSmoke";
	protected static final String LABEL_BACKGROUND_COLOR = "Silver";
	
	private static FinanceServiceAsync FINANCE_SERVICE;
	private static FiscalServiceAsync FISCAL_SERVICE;
	
	private SimplePanel invoicePanelContainer;
	private AccountingRegistryBox registryBox;
	private CheckBox undeductible;
	private InlineLabel invoiceTypeLabel;
	private FlowPanel dropPanel; 	
	private ListBox series = new ListBox();
	private TextBox referenceCode = new TextBox();
	private DoubleBox invoiceTotal = new DoubleBox();
	private Button fastSave;
	private DateBoxEx taxDate;
	private InvoiceVATPanel vatPanel;
	private InvoiceWithholdingPanel withholdingPanel;
	private InvoiceFinancePanel financePanel;
	
	private AccountingRegistry lastRegistry;
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
		public void enableInvoiceTotal(boolean enabled) {
			invoiceTotal.setEnabled(enabled);
		}
	}

	public EditableInvoicePanel(final InvoicePanelCallback invoiceCallback) {
	
		this.invCallback = invoiceCallback; 
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		FISCAL_SERVICE = new FiscalServiceAsyncDecorator(fiscalServiceRaw);

		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		FINANCE_SERVICE = new FinanceServiceAsyncDecorator(financeServiceRaw);

		// *************************************************************************
		// ** PANEL ( Titular, Label de factura) ***********************************
		// *************************************************************************

		FlowPanel registryPanel = new FlowPanel();
		registryPanel.setStyleName(AON.AON_CSS.aonPadding2Bottom());
		registryPanel.addStyleName(AON.AON_CSS.aonBorderBottom());
		
		FlexTable regTable = new FlexTable();
		regTable.getColumnFormatter().setWidth(0, "185px");
		regTable.getColumnFormatter().setWidth(1, "auto");
		regTable.getColumnFormatter().setWidth(2, "185px");
		regTable.getColumnFormatter().setWidth(3, "250px");
		
		regTable.setStyleName(AON.AON_CSS.aonWidthAll());
		registryPanel.add(regTable);
		
		InlineLabel label = new InlineLabel("Cuenta del titular de la factura");
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		label.addStyleName(AON.AON_CSS.aonBold());
		label.addStyleName(AON.AON_CSS.aonWidth180());
		regTable.setWidget(0, 0, label);
		
		undeductible = new CheckBox("Gastos no deducibles");
		undeductible.setTabIndex(-1);
		undeductible.setVisible(false);
		undeductible.setValue(false);
		
		registryBox = new AccountingRegistryBox(
				invoiceCallback.getCurrentDomainName()
				,invoiceCallback.getCurrentDomainId()
				,invoiceCallback.getConfiguration()
				,true);
		registryBox.addKeyUpHandler( new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					if ( registryBox.getId() == null) {
						if (lastRegistry != null) {
							registryBox.set(lastRegistry);	
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
				initializeInvoice(invoiceCallback, ar );
			}
		});
		regTable.setWidget(0, 1, registryBox);
		
		undeductible.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final AccountingRegistry ar = invoiceCallback.getInvoice().getRegistry();
				if (undeductible.getValue()) {
					ar.setType(AccountingRegistryType.UNDED_CREDITOR);
				} else {
					ar.setType(AccountingRegistryType.CREDITOR);
				}
				initializeInvoice(invoiceCallback, ar );
			}
		});
		regTable.setWidget(0, 2, undeductible);
		
		invoiceTypeLabel = new InlineLabel();
		invoiceTypeLabel.setText( getInvoiceLabel(invoiceCallback.getInvoice()));
		invoiceTypeLabel.addStyleName(AON.AON_CSS.aonMarginAuto());
		invoiceTypeLabel.addStyleName(AON.AON_CSS.aonInvoiceLabel());
		invoiceTypeLabel.addStyleName(AON.AON_CSS.aonTextRight());
		invoiceTypeLabel.addStyleName(AON.AON_CSS.aonNowrap());
		invoiceTypeLabel.getElement().getStyle().setProperty("flex-grow", "1");
		regTable.setWidget(0, 3, invoiceTypeLabel);
		
		FlowPanel invoiceRootPanel = new FlowPanel();
		invoiceRootPanel.add(registryPanel);
		invoiceRootPanel.getElement().getStyle().setProperty("min-width", "850px");
		
		invoicePanelContainer = new SimplePanel();
		invoicePanelContainer.setStyleName(AON.AON_CSS.aonWidthAll());
		
		invoiceRootPanel.add(invoicePanelContainer);
		
		ScrollPanel rootScrollPanel = new ScrollPanel();
		rootScrollPanel.setStyleName(AON.AON_CSS.aonScrollArea());
		rootScrollPanel.setWidget(invoiceRootPanel);
		
		LOGGER.info("EditablePanel Constructor " + (invoiceCallback.getInvoice() != null && invoiceCallback.getInvoice().getInvoice() != null && invoiceCallback.getInvoice().getInvoice().getId() != null));

		if ( invoiceCallback.getInvoice() != null && invoiceCallback.getInvoice().getInvoice() != null && invoiceCallback.getInvoice().getRegistry() != null) {
			invoicePanelContainer.setWidget(editInvoice(invoiceCallback));
			registryBox.setValue(invoiceCallback.getInvoice().getRegistry());
		} else {
			FlowPanel dropPanel = getDropFileZone( invoiceCallback );
			invoicePanelContainer.setWidget(dropPanel);
		}
		
		setStyleName(AON.AON_CSS.aonWidthAll());
		setWidget(rootScrollPanel);
	}

	protected void initializeInvoice(InvoicePanelCallback invoiceCallback, AccountingRegistry ar) {
		FISCAL_SERVICE.initializeInvoice(
				invoiceCallback.getCurrentDomainName()
				,invoiceCallback.getCurrentDomainId()
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

	private void decorateTaxDate(AccountingInvoice ai) {
		Date issue = ai.getInvoice().getIssueDate();
		Date tax = ai.getInvoice().getTaxDate();
		if(    (issue == null && tax != null)
			|| (issue != null && tax == null)
			|| (issue.compareTo(tax) != 0)) {
			taxDate.addStyleName(AON.AON_CSS.aonBackgroundHighlightedOrange());
		} else {
			taxDate.removeStyleName(AON.AON_CSS.aonBackgroundHighlightedOrange());
		}
	}

	protected void headerDataChanged(InvoicePanelCallback invoiceCallback) {
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
		return l;
	}

	private void repeatLastInvoice(InvoicePanelCallback invoiceCallback, final ISelectionCallback cbk) {
		Integer registryId = invoiceCallback.getInvoice().getRegistry().getId();
		FISCAL_SERVICE.getRegistryLastAccountingInvoice(invoiceCallback.getCurrentDomainName()
				,invoiceCallback.getCurrentDomainId(), registryId
				,new AsyncCallback<AccountingInvoice>() {
						@Override
						public void onSuccess(AccountingInvoice result) {
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
							invoiceCallback.setInvoice(result);
							editInvoice(invoiceCallback);
							invoiceCallback.getModule().onBalance(invoiceCallback.getInvoice().getAccountEntry());
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
		CheckBox service = new CheckBox(AON.MSG.service());
		CheckBox investment = new CheckBox(AON.MSG.investAsset());
		CheckBox surcharge = new CheckBox(AON.MSG.surcharge());
		CheckBox prepayment = new CheckBox(AON.MSG.hasPrepayments());
		InvoiceTransactionListBox transactionBox = new InvoiceTransactionListBox();
		CheckBox vatAccrualPayment = new CheckBox(AON.MSG.vatAccrualPaymentAbbr());
		CheckBox withholding = new CheckBox(AON.MSG.withholding());
		CheckBox withholdingFarmer = new CheckBox(AON.MSG.withholdingFarmerAbbr());
		IntegerBox number = new IntegerBox();
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

		fullDocument.setValue(inv.getRegistry().getDocumentType(),inv.getRegistry().getDocumentCountry(),inv.getRegistry().getDocument());
		rName.setValue(inv.getInvoice().getRegistryName());
		taxDate.setValue(inv.getInvoice().getTaxDate());
		service.setValue(inv.isService());
		investment.setValue(inv.isInvestment());
		surcharge.setValue(inv.isSurcharge());
		prepayment.setValue(inv.hasPrepayments());
		transactionBox.setValue(inv.getTransaction());
		vatAccrualPayment.setValue(inv.isVatAccrualPayment());
		withholding.setValue(inv.isWithholding());
		withholdingFarmer.setValue(inv.isWithholdingFarmer());
		
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
		
		invoiceTotal.setValue(inv.getTotalInvoice());
		manualConcept.setValue(inv.getManualConcept());
		
		vatPanel = new InvoiceVATPanel( new EditableInvoicePanelCallback() );
		financePanel = new InvoiceFinancePanel(invoiceCallback);

		FlowPanel invoicePanel = new FlowPanel();
		invoicePanel.setStyleName(AON.AON_CSS.aonWidthAll());
		
		// *************************************************************************
		// ** PANEL ( Datos de la factura) *****************************************
		// *************************************************************************
		FlexTable invoiceDataTable = new FlexTable();
		invoiceDataTable.getColumnFormatter().setWidth(0, "40px");
		invoiceDataTable.getColumnFormatter().setWidth(1, "auto");
		
		invoiceDataTable.setStyleName(AON.AON_CSS.aonAccountTable());
		invoiceDataTable.addStyleName(AON.AON_CSS.aonMarginTop5());
		invoiceDataTable.addStyleName(AON.AON_CSS.aonWidthAll());
		invoiceDataTable.addStyleName(AON.AON_CSS.aonSimpleBorder());
		invoiceDataTable.getElement().getStyle().setBackgroundColor(INNER_BACKGROUND_COLOR);

		
		// *************************************************************************
		// ***************** PANEL ( documento y nombre del titular ) **************
		// *************************************************************************
		
		Label description = new Label("Datos");
		description.setStyleName(AON.AON_CSS.aonTextVertical());
		description.addStyleName(AON.AON_CSS.aonBold());
		invoiceDataTable.setWidget(0, 0, description);
		invoiceDataTable.getFlexCellFormatter().setRowSpan(0, 0, 4);
		invoiceDataTable.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonSimpleBorder());
		invoiceDataTable.getCellFormatter().getElement(0, 0).getStyle().setBackgroundColor(LABEL_BACKGROUND_COLOR);
		
		FlowPanel headerPanel1 = new  FlowPanel();
		invoiceDataTable.setWidget(0, 1, headerPanel1);
		headerPanel1.setStyleName(AON.AON_CSS.aonInvoicePanelInner());
		
		InlineLabel documentLabel = new InlineLabel(AON.MSG.document());
		documentLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		documentLabel.addStyleName(AON.AON_CSS.aonWidth80());
		headerPanel1.add(documentLabel);
		
		
		fullDocument.getTypeWidget().addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					registryBox.setFocus(true);
		        }
			}
		});
		fullDocument.addTypeChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent arg0) {
				invoiceCallback.getInvoice().getInvoice().setRegistryDocumentType(fullDocument.getType());
			}
		});
		fullDocument.getCountryWidget().addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					registryBox.setFocus(true);
		        }
			}
		});
		fullDocument.addCountryChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent arg0) {
				invoiceCallback.getInvoice().getInvoice().setRegistryDocumentCountry(fullDocument.getCountry());
			}
		});
		fullDocument.getDocumentWidget().addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					registryBox.setFocus(true);
		        }
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
		nameLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		headerPanel1.add(nameLabel);
		
		rName.setStyleName(AON.AON_CSS.aonInputText());
		rName.setVisibleLength(50);
		rName.setMaxLength(50);
		rName.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					registryBox.setFocus(true);
		        }
			}
		});
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
		headerPanel2.setStyleName(AON.AON_CSS.aonInvoicePanelInner());
		
		InlineLabel taxDateLabel = new InlineLabel(AON.MSG.taxDate());
		taxDateLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		taxDateLabel.addStyleName(AON.AON_CSS.aonWidth80());
		taxDateLabel.setVisible(!invoiceCallback.getInvoice().isUndeductible());
		headerPanel2.add(taxDateLabel);
		
		FlowPanel taxDateContainer = new FlowPanel();
		taxDateContainer.setStyleName(AON.AON_CSS.aonWidth150());
		
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
		
		if (invoiceCallback.getInvoice().isExpenses()) {
			FlowPanel serviceContainer = new FlowPanel();
			serviceContainer.setStyleName(AON.AON_CSS.aonWidth100());
			
			InlineLabel serviceLabel = new InlineLabel(AON.MSG.service());
			serviceLabel.setStyleName(AON.AON_CSS.aonIconChecked());
			serviceLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
			serviceLabel.addStyleName(AON.AON_CSS.aonMarginTop5());
			serviceContainer.add(serviceLabel);
			
			headerPanel2.add(serviceContainer);
		} else {
			service.setStyleName(AON.AON_CSS.aonInline());
			service.addStyleName(AON.AON_CSS.aonWidth100());
			service.setVisible(!invoiceCallback.getInvoice().isUndeductible());		
			service.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					invoiceCallback.getInvoice().getInvoice().setService(service.getValue());
					InvoiceCalculator.calculate(invoiceCallback.getInvoice());
					headerDataChanged(invoiceCallback);
				}
			});
			headerPanel2.add(service);
		}
		
		investment.setStyleName(AON.AON_CSS.aonInline());
		investment.addStyleName(AON.AON_CSS.aonWidth150());
		investment.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				invoiceCallback.getInvoice().getInvoice().setInvestment(investment.getValue());
			}
		});
		investment.setVisible(!invoiceCallback.getInvoice().isUndeductible());
		headerPanel2.add(investment);
		
		surcharge.setStyleName(AON.AON_CSS.aonInline());
		surcharge.addStyleName(AON.AON_CSS.aonWidth150());
		surcharge.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				invoiceCallback.getInvoice().getInvoice().setSurcharge(surcharge.getValue());
				InvoiceCalculator.calculate(invoiceCallback.getInvoice());
				headerDataChanged(invoiceCallback);
			}
		});
		surcharge.setVisible(!invoiceCallback.getInvoice().isUndeductible());
		headerPanel2.add(surcharge);
		
		prepayment.setStyleName(AON.AON_CSS.aonInline());
		prepayment.addStyleName(AON.AON_CSS.aonWidth150());
		prepayment.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				invoiceCallback.getInvoice().setPrepayments(prepayment.getValue());
				headerDataChanged(invoiceCallback);
			}
		});
		prepayment.setVisible(!invoiceCallback.getInvoice().isUndeductible());
		headerPanel2.add(prepayment);

		// *************************************************************************
		// ***************** PANEL ( Transaccion, set de checks 1) *****************
		// *************************************************************************
		FlowPanel headerPanel3 = new  FlowPanel();
		invoiceDataTable.setWidget(2, 0, headerPanel3);
		headerPanel3.setStyleName(AON.AON_CSS.aonInvoicePanelInner());
		InlineLabel transactionLabel = new InlineLabel(AON.MSG.transaction());
		transactionLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		transactionLabel.addStyleName(AON.AON_CSS.aonWidth80());
		transactionLabel.setVisible(!invoiceCallback.getInvoice().isUndeductible());
		headerPanel3.add(transactionLabel);
		
		FlowPanel transactionContainer = new FlowPanel();
		transactionContainer.setStyleName(AON.AON_CSS.aonWidth150());

		transactionBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				invoiceCallback.getInvoice().getInvoice().setTransaction(transactionBox.getValue());
				InvoiceCalculator.calculate(invoiceCallback.getInvoice());
				headerDataChanged(invoiceCallback);
			}
		});
		transactionContainer.add(transactionBox);
		transactionBox.setVisible(!invoiceCallback.getInvoice().isUndeductible());
		headerPanel3.add(transactionContainer);

		vatAccrualPayment.setStyleName(AON.AON_CSS.aonInline());
		vatAccrualPayment.addStyleName(AON.AON_CSS.aonWidth100());
		vatAccrualPayment.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				invoiceCallback.getInvoice().getInvoice().setVatAccrualPayment(vatAccrualPayment.getValue());
			}
		});
		vatAccrualPayment.setVisible(!invoiceCallback.getInvoice().isUndeductible());
		headerPanel3.add(vatAccrualPayment);
		
		withholding.setStyleName(AON.AON_CSS.aonInline());
		withholding.addStyleName(AON.AON_CSS.aonWidth150());
		withholding.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				invoiceCallback.getInvoice().getInvoice().setWithholding(withholding.getValue());
				InvoiceCalculator.calculate(invoiceCallback.getInvoice());
				headerDataChanged(invoiceCallback);
			}
		});
		withholding.setVisible(!invoiceCallback.getInvoice().isUndeductible());
		headerPanel3.add(withholding);
		
		withholdingFarmer.setStyleName(AON.AON_CSS.aonInline());
		withholdingFarmer.addStyleName(AON.AON_CSS.aonWidth150());
		withholdingFarmer.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				invoiceCallback.getInvoice().getInvoice().setWithholdingFarmer(withholdingFarmer.getValue());
				InvoiceCalculator.calculate(invoiceCallback.getInvoice());
				headerDataChanged(invoiceCallback);
			}
		});
		withholdingFarmer.setVisible(!invoiceCallback.getInvoice().isUndeductible());
		headerPanel3.add(withholdingFarmer);
		
		// *************************************************************************
		// ***************** PANEL ( Número Factura, total factura, concepto) ******
		// *************************************************************************
		FlowPanel headerPanel4 = new  FlowPanel();
		invoiceDataTable.setWidget(3, 0, headerPanel4);
		headerPanel4.setStyleName(AON.AON_CSS.aonInvoicePanelInner());
		
		InlineLabel invoiceNumberLabel = new InlineLabel(AON.MSG.invoiceNumber());
		invoiceNumberLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		invoiceNumberLabel.addStyleName(AON.AON_CSS.aonWidth80());
		headerPanel4.add(invoiceNumberLabel);
		
		FlowPanel numberPanel = new FlowPanel();
		
		series.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
		            registryBox.setFocus( true );
		        }
			}
		});
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
		
		number.setStyleName(AON.AON_CSS.aonMarginLeft5());
		number.addStyleName(AON.AON_CSS.aonInputText());
		number.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
		            registryBox.setFocus( true );
		        }
			}
		});
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
		
		referenceCode.setStyleName(AON.AON_CSS.aonInputText());
		referenceCode.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
		            registryBox.setFocus( true );
		        }
			}
		});
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
		
		InlineLabel label0 = new InlineLabel(AON.MSG.invoiceTotal());
		label0.setStyleName(AON.AON_CSS.aonInnerLabel());
		label0.addStyleName(AON.AON_CSS.aonMarginLeft());
		headerPanel4.add(label0);

		invoiceTotal.addKeyUpHandler( new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					repeatLastInvoice(invoiceCallback, new ISelectionCallback() {

						@Override
						public void onSuccess() {
							fastSave.setFocus(true);
						}

						@Override
						public void onFailure() {
						}
						
					});
				}
			}
		});
		invoiceTotal.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
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
							fastSave.setEnabled(true);
							fastSave.setFocus(true);
						} else {
							vatPanel.setFocus(true);
						}
				}});
			}
		});
		invoiceTotal.setVisibleLength(12);
		headerPanel4.add(invoiceTotal);
		
		fastSave = new Button(AON.MSG.saveAction());
		fastSave.setStyleName(AON.AON_CSS.aonIconSave());
		fastSave.addStyleName(AON.AON_CSS.aonIconCommandButton());
		fastSave.addStyleName(AON.AON_CSS.aonMarginLeft());
		fastSave.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				invoiceCallback.getModule().onAccept(event);
			}
		});
		fastSave.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				vatPanel.setFocus(true);
			}
		});
		headerPanel4.add(fastSave);
		
		FlowPanel manualConceptPanel = new FlowPanel();
		manualConceptPanel.setStyleName(AON.AON_CSS.aonTextLeft());
		manualConceptPanel.addStyleName(AON.AON_CSS.aonPaddingLeft());
		manualConceptPanel.getElement().getStyle().setProperty("flex-grow", "1");
		
		InlineLabel label1 = new InlineLabel(AON.MSG.conceptComplement());
		label1.setStyleName(AON.AON_CSS.aonInnerLabel());
		manualConceptPanel.add(label1);

		manualConcept.setStyleName(AON.AON_CSS.aonInputText());
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
		headerPanel4.add(manualConceptPanel);
		
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
		
		vatTable.setStyleName(AON.AON_CSS.aonAccountTable());
		vatTable.addStyleName(AON.AON_CSS.aonWidthAll());
		vatTable.addStyleName(AON.AON_CSS.aonSimpleBorder());
		vatTable.addStyleName(AON.AON_CSS.aonMarginTop5());
		vatTable.getElement().getStyle().setBackgroundColor(INNER_BACKGROUND_COLOR);
		
		Label baseDescription = new Label("Bases");
		baseDescription.setStyleName(AON.AON_CSS.aonTextVertical());
		baseDescription.addStyleName(AON.AON_CSS.aonBold());
		vatTable.setWidget(0, 0, baseDescription);
		vatTable.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonSimpleBorder());
		vatTable.getCellFormatter().getElement(0, 0).getStyle().setBackgroundColor(LABEL_BACKGROUND_COLOR);
		//vatTable.getCellFormatter().getElement(0, 0).getStyle().setHeight(80.0, Unit.PX);
		vatTable.setWidget(0, 1, vatPanel);
		invoicePanel.add(vatTable);

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
		
		financesTable.setStyleName(AON.AON_CSS.aonAccountTable());
		financesTable.addStyleName(AON.AON_CSS.aonWidthAll());
		financesTable.addStyleName(AON.AON_CSS.aonSimpleBorder());
		financesTable.addStyleName(AON.AON_CSS.aonMarginTop5());
		financesTable.getElement().getStyle().setBackgroundColor(INNER_BACKGROUND_COLOR);
		
		Label financesDescription = new Label("VTOS");
		financesDescription.setStyleName(AON.AON_CSS.aonTextVertical());
		financesDescription.addStyleName(AON.AON_CSS.aonBold());
		financesTable.setWidget(0, 0, financesDescription);
		financesTable.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonSimpleBorder());
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

		return invoicePanel;
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
		hp.setStyleName(AON.AON_CSS.aonBlockCenter());
		hp.addStyleName(AON.AON_CSS.aonSimpleBorder() );
		hp.addStyleName(AON.AON_CSS.aonMarginTop() );
		Label iconWaitLabel = new Label();
		iconWaitLabel.setStyleName(AON.AON_CSS.aonLoader());
		iconWaitLabel.addStyleName(AON.AON_CSS.aonMargin());
		hp.add(iconWaitLabel);
		Label textWaitLabel = new Label("Procesando el reconocimiento del archivo. Conectando con tEDI Center. Un  momento, por favor.....");
		textWaitLabel.setStyleName(AON.AON_CSS.aonMargin());
		textWaitLabel.addStyleName(AON.AON_CSS.aonBold());
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
				if ( invoiceCallback.getConfiguration().isTediActive() ) {
					dropPanel.add(getSplashWidget());
				} else {
					dropPanel.clear();
					registryBox.setFocus(true);
				}
				fileSelectHandler(fileUpload.getElement(), event.getNativeEvent());
			}
		});
			
			
		dropPanel = new FlowPanel();
		
		FlowPanel filedrag = new FlowPanel();
		filedrag.add(fileUpload);
		String label = "Arrastre aqu\u00ED el archivo o click para seleccionar";
		Label dropZone = new Label( label );
		
		if (invoiceCallback.getConfiguration().isTediActive()) {
			if (invoiceCallback.getConfiguration().isTediSnapshotUser()) {
				dropZone.setStyleName(AON.AON_CSS.aonTediSnapshotDropZone());
			} else if (invoiceCallback.getConfiguration().isTediUser()) {
				dropZone.setStyleName(AON.AON_CSS.aonTediDropZone());
			} else {
				dropZone.setStyleName(AON.AON_CSS.aonDropZone());	
			}
		} else {
			dropZone.setStyleName(AON.AON_CSS.aonDropZone());
		}
		
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
				dropZone.addStyleName(AON.AON_CSS.aonDropZoneHover());
				event.stopPropagation();
				event.preventDefault();
		        DataTransfer dataTransfer = event.getDataTransfer();
		        dataTransfer.setDropEffect(DropEffect.COPY);				
			}
		});
		dropZone.addDragLeaveHandler(new DragLeaveHandler() {
			@Override
			public void onDragLeave(DragLeaveEvent event) {
				dropZone.removeStyleName(AON.AON_CSS.aonDropZoneHover());
				event.preventDefault();
			}
		});
		dropZone.addDropHandler(new DropHandler() {
				
			@Override
			public void onDrop(DropEvent event) {
				dropZone.removeStyleName(AON.AON_CSS.aonDropZoneHover());
				LOGGER.info("File Droped!");
				event.stopPropagation();
				event.preventDefault();
				fileDrop(fileUpload.getElement() ,event.getNativeEvent());
			}
		});
		
		filedrag.add(dropZone);
		dropPanel.add(filedrag);
		return dropPanel;
	}
	
	private void setDocument(final String doc, final String name, String type) {
		LOGGER.info("BEFORE invCallback setDocument!");
		invCallback.setDocument(doc, name, type);
	}

	
	private native void fileSelectHandler(Element fileselect, NativeEvent event) /*-{
		var self = this;		
		event.preventDefault();
		var file = fileselect.files[0];
		var reader = new FileReader();
		reader.addEventListener("load", function () {
			self.@com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.EditableInvoicePanel::setDocument(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(reader.result,file.name,file.type); 
			}, false);
		reader.readAsDataURL( file );
	}-*/;
	
	private native void fileDrop(Element fileselect, NativeEvent event) /*-{
		fileselect.files = e.target.files || e.dataTransfer.files;
		if (e.dataTransfer.items) {
			e.dataTransfer.items.clear();
			} else {
			e.dataTransfer.clearData();
			}		
		fileselect.click();
	}-*/;	

}

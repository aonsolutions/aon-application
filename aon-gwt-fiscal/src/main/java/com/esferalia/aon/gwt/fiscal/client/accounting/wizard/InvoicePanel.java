package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.AccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.PayMethodListBox;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IAccountEntryModuleCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.IAccountingInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceRecorder;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.IAccountingRegistryTypeVisitor;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

public class InvoicePanel extends WizardContentBase<AccountingInvoice> implements HasSelectionHandlers<AccountingInvoice> {
	
	private static final Logger LOGGER = Logger.getLogger(InvoicePanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	static final String BACKGROUND_COLOR = "#DDDDDD";

	
	public final static int TAB_OFFSET = 1000;
	public final static int VAT_PANEL_TAB_OFFSET = 50000;
	public final static int WITHHOLDING_PANEL_TAB_OFFSET = 200000;
	public final static int PAY_PANEL_TAB_OFFSET = 250000;
	public final static int EXTRA_PANEL_TAB_OFFSET = 500000;

	static FinanceServiceAsync financeService;
	
	public static interface IInvoicePanelCallback extends IAccountEntryModuleCallback{
		AccountingInvoice getInvoice();
		boolean isInvestAssetsAvailable();
		void paintEntry();
		void enableInvoiceTotal(boolean enable);
		
		void setFocusOnRegistry();
		void setFocusOnWithholding();
		void setFocusOnPayDate();
	}
	
	private AccountingRegistry lastRegistry;

	private DeckLayoutPanel rootPanel;
	private SimpleLayoutPanel nonEditablePanel; 
			
	private FlexTable regTable;
	private FlexTable flexTable;
	private InvoiceVATPanel vatPanel;
	private InvoiceWithholdingPanel withholdingPanel;
	
	private FlexTable payTable;
	private InvoiceExtraPanel extraPanel;
	
	private AccountingRegistryBox registryBox;
	private ListBox series;
	private IntegerBox number;
	private TextBox referenceCode;
	private DoubleBox invoiceTotal;
	private Button fastSave;
	private TextBox manualConcept;
	private DateBoxEx payDate;
	private InlineLabel payAccountLabel;
	private AccountBox payAccount; 		
	private PayMethodListBox payMethodList;
	private InlineLabel payStatusLabel; 

	
	private final KeyUpHandler f9KeyHandler = new KeyUpHandler() {
		@Override
		public void onKeyUp(KeyUpEvent event) {
			if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
	            extraPanel.setFocus();
	        }
		}
	};
	
	private int tabindex = TAB_OFFSET;
	private AccountingInvoice invoice;
	private InvoicePanelRegistryVisitor invoicePanelRegistryVisitor;
	
	public InvoicePanel(final IAccountEntryModuleCallback callback) {
		setCallback(callback);
		
		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		financeService = new FinanceServiceAsyncDecorator(financeServiceRaw);

		invoicePanelRegistryVisitor = new InvoicePanelRegistryVisitor();
		InvoicePanelCallback invoiceCallback = new InvoicePanelCallback();
		
		rootPanel = new DeckLayoutPanel();
		
		SplitLayoutPanel editablePanel = new SplitLayoutPanel(4);
		
		//  -------------------------- WORKING LOG ------------------------------

		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.setStyleName(AON.AON_CSS.aonFlexContainer());
		centerPanel.addStyleName(AON.AON_CSS.aonPadding2Top());
		centerPanel.setStyleName(AON.AON_CSS.aonInvoicePanel());
		centerPanel.getElement().getStyle().setBackgroundColor(InvoicePanel.BACKGROUND_COLOR);

		//  -------------------------- EXTRA PANEL ------------------------------
		extraPanel = new InvoiceExtraPanel(  );
		extraPanel.addValueChangeHandler(new ValueChangeHandler<Void>() {
			@Override
			public void onValueChange(ValueChangeEvent<Void> event) {
				if (AonStringUtils.isBlank(manualConcept.getValue())) {
					if (AonStringUtils.isNotBlank(invoiceCallback.getInvoice().getManualConcept())) {
						manualConcept.setValue(invoiceCallback.getInvoice().getManualConcept(),false);
					}
				}
				vatPanel.extraInfoChanged();
				withholdingPanel.setVisible(getWrapper().getInvoice().isWithholding());
				withholdingPanel.setValue(getWrapper().getWithholdingData());
				invoiceTotal.setValue(getWrapper().getInvoice().getTotal(),false);
				_paintEntry();
				getWrapper().getAccountEntry().setDirty(true);
				getCallback().getModule().refreshIdLabel();
			}
		});
		extraPanel.addSelectionHandler(new SelectionHandler<AccountingInvoice>() {
			
			@Override
			public void onSelection(SelectionEvent<AccountingInvoice> event) {
				SelectionEvent.<AccountingInvoice>fire( InvoicePanel.this, event.getSelectedItem());
			}
		});
		editablePanel.addEast(extraPanel, 380);

		SimpleLayoutPanel centerContainerPanel = new SimpleLayoutPanel();
		ScrollPanel scrollCenterContainer = new ScrollPanel();
		FlowPanel centerContainer = new FlowPanel();
		
		FlowPanel tablesPanel = new FlowPanel();
		createRegistryTable();
		tablesPanel.add(regTable);
		createFlexTable();
		tablesPanel.add(flexTable);
		centerContainer.add(tablesPanel);
		
		vatPanel = new InvoiceVATPanel( invoiceCallback );
		vatPanel.addValueChangeHandler(new ValueChangeHandler<InvoiceVAT>() {
			@Override
			public void onValueChange(ValueChangeEvent<InvoiceVAT> event) {
				InvoiceCalculator.calculate(getWrapper());
				withholdingPanel.setValue(getWrapper().getWithholdingData());
				invoiceTotal.setValue(getWrapper().getInvoice().getTotal(),false);
				_paintEntry();
				getWrapper().getAccountEntry().setDirty(true);
				getCallback().getModule().refreshIdLabel();
			}
		});
		vatPanel.addSelectionHandler(new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				getCallback().getModule().onBalance(event.getSelectedItem());
				_paintEntry();
				getWrapper().getAccountEntry().setDirty(true);
				getCallback().getModule().refreshIdLabel();
			}
		});
		centerContainer.add(vatPanel);
		
		// WITHHOLDING PANEL
		withholdingPanel = new InvoiceWithholdingPanel( invoiceCallback );
		withholdingPanel.setStyleName(AON.AON_CSS.aonMarginTop());
		withholdingPanel.addValueChangeHandler(new ValueChangeHandler<InvoiceWithholding>() {
			@Override
			public void onValueChange(ValueChangeEvent<InvoiceWithholding> event) {
				InvoiceCalculator.calculate(getWrapper());
				withholdingPanel.setValue(getWrapper().getWithholdingData());
				invoiceTotal.setValue(getWrapper().getInvoice().getTotal(),false);
				_paintEntry();
				getWrapper().getAccountEntry().setDirty(true);
				getCallback().getModule().refreshIdLabel();
			}
		});
		withholdingPanel.addSelectionHandler(new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				getCallback().getModule().onBalance(event.getSelectedItem());
				_paintEntry();
				getWrapper().getAccountEntry().setDirty(true);
				getCallback().getModule().refreshIdLabel();
			}
		});
		centerContainer.add( withholdingPanel );
		
		// PAY TABLE
		createPayTable();
		centerContainer.add(payTable);
		
		scrollCenterContainer.setWidget(centerContainer);
		centerContainerPanel.setWidget(scrollCenterContainer);
		centerPanel.setWidget(centerContainerPanel);
		editablePanel.add(centerPanel);
		
		rootPanel.add(editablePanel);
		
		nonEditablePanel = new SimpleLayoutPanel();
		rootPanel.add(nonEditablePanel);

		rootPanel.showWidget(0);
		initWidget(rootPanel);
	}
	
	@Override
	public AccountingInvoice getWrapper() {
		return invoice;
	}

	@Override
	public void setWrapper(AccountingInvoice wrapper) {
		this.invoice = wrapper;
	}

	private void createRegistryTable() {
		int row = 0;
		regTable = new FlexTable();
		regTable.setStyleName(AON.AON_CSS.aonWidthAll());
		
		Label label = new Label(AON.MSG.titular());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		regTable.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonWidth90());
		regTable.setWidget(row, 0, label);
		
		registryBox = new AccountingRegistryBox(
				 getCallback().getCurrentDomainName()
				,getCallback().getCurrentDomainId()
				,getCallback().getModule().getConfiguration()
				,true);
		registryBox.setTabIndex(++tabindex);
		registryBox.addKeyUpHandler( new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					if ( registryBox.getId() == null) {
						if (lastRegistry != null) {
							registryBox.set(lastRegistry);	
						}
					} else {
						extraPanel.setFocus();
					}
				}
			}
		});
		registryBox.addSelectionHandler(new SelectionHandler<AccountingRegistry>() {
			@Override
			public void onSelection(SelectionEvent<AccountingRegistry> event) {
				final AccountingRegistry ar = event.getSelectedItem();
				getFiscalService().initializeInvoice(
						getCallback().getCurrentDomainName()
						,getCallback().getCurrentDomainId()
						,ar
						,getCallback().getModule().getActivity()
						,getCallback().getModule().getEntryDate()
						,new AsyncCallback<AccountingInvoice>() {
							
							@Override
							public void onSuccess(AccountingInvoice result) {
								AccountEntry ae = getWrapper().getAccountEntry();
								setWrapper(result);
								getWrapper().setAccountEntry(ae);
								invoiceTotal.setEnabled(true);
								Account account = new Account();
								account.setId(ar.getAccountId());
								account.setCode(ar.getAccountCode());
								account.setDescription(ar.getAccountDescription());
								if (ar.getAccountId() != null) {
									getCallback().getModule().onBalance(account);
								}
								vatPanel.setSuggestedAccounts(getWrapper().getSuggestedAccounts());
								_paint();
								extraPanel.invoiceChanged(result);
								getWrapper().getRegistry().getType().visit(getWrapper().getRegistry(),invoicePanelRegistryVisitor);
							}
							
							@Override
							public void onFailure(Throwable caught) {
								getCallback().getModule().onError(caught.getMessage());
							}
						});
				
			}
		});
		regTable.setWidget(row, 1, registryBox);
	}

	private void createFlexTable() {
		int row = 0;
		flexTable = new FlexTable();
		flexTable.setStyleName(AON.AON_CSS.aonWidthAll());
		flexTable.setVisible(false);
		Label label = new Label(AON.MSG.invoiceNumber());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonWidth90());
		flexTable.setWidget(row, 0, label);
		
		FlowPanel numberPanel = new FlowPanel();
		
		series = new ListBox();
		series.setTabIndex(++tabindex);
		series.addKeyUpHandler(f9KeyHandler);
		series.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				getWrapper().getInvoice().setSeries(series.getSelectedIndex()==0?null:series.getSelectedValue());
				financeService.getInvoiceNextNumber(
						getCallback().getCurrentDomainName()
						,getCallback().getCurrentDomainId()
						,new Byte[]{getWrapper().getInvoice().getType().value()}
						,getWrapper().getInvoice().getSeries()
						,new AsyncCallback<Integer>() {

							@Override
							public void onFailure(Throwable caught) {
								getCallback().getModule().onError(caught.getMessage());
							}

							@Override
							public void onSuccess(Integer result) {
								number.setValue(result,false,true);
								getWrapper().getInvoice().setNumber(result);
								_paintEntry();
								getWrapper().getAccountEntry().setDirty(true);
								getCallback().getModule().refreshIdLabel();
							}
						});
			}
		});
		numberPanel.add(series);
		
		number = new IntegerBox();
		number.setTabIndex(++tabindex);
		number.setStyleName(AON.AON_CSS.aonMarginLeft5());
		number.addStyleName(AON.AON_CSS.aonInputText());
		number.addKeyUpHandler(f9KeyHandler);
		number.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				getWrapper().getInvoice().setNumber(number.getValue());
				_paintEntry();
				getWrapper().getAccountEntry().setDirty(true);
				getCallback().getModule().refreshIdLabel();
			}
		});
		number.setVisibleLength(8);
		number.setMaxLength(8);
		numberPanel.add(number);
		
		referenceCode = new TextBox();
		referenceCode.setTabIndex(++tabindex);
		referenceCode.setStyleName(AON.AON_CSS.aonInputText());
		referenceCode.addKeyUpHandler(f9KeyHandler);
		referenceCode.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getWrapper().getInvoice().setReferenceCode(referenceCode.getValue());
				_paintEntry();
				getWrapper().getAccountEntry().setDirty(true);
				getCallback().getModule().refreshIdLabel();
			}
		});

		referenceCode.setVisibleLength(15); 
		referenceCode.setMaxLength(32);
		numberPanel.add(referenceCode);
		
		flexTable.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonNowrap());
		flexTable.getCellFormatter().setWidth(row, 1, "1%");
		flexTable.setWidget(row, 1, numberPanel);
		
		Label label0 = new Label(AON.MSG.invoiceTotal());
		label0.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setWidth(row, 2, "1%");
		flexTable.setWidget(row, 2, label0);
		
		invoiceTotal = new DoubleBox();
		invoiceTotal.setTabIndex(++tabindex);
		invoiceTotal.addKeyUpHandler( new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					repeatLastInvoice( new ISelectionCallback() {

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
				if (getWrapper().getVats() != null &&  getWrapper().getVats().size() == 1) {
					InvoiceCalculator.reverseCalculate(getWrapper(),invoiceTotal.getValue());
					vatPanel.populateFirstVat();
					withholdingPanel.setValue( getWrapper().getWithholdingData() );
				}
				_paintEntry();
				getWrapper().getAccountEntry().setDirty(true);
				getCallback().getModule().refreshIdLabel();
				
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
		flexTable.getCellFormatter().setWidth(row, 3, "1%");
		flexTable.setWidget(row, 3, invoiceTotal);
		
		fastSave = new Button(AON.MSG.saveAction());
		fastSave.setStyleName(AON.AON_CSS.aonIconSave());
		fastSave.addStyleName(AON.AON_CSS.aonIconCommandButton());
		fastSave.setTabIndex(++tabindex);
		fastSave.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getCallback().getModule().onAccept(event);
			}
		});
		fastSave.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				vatPanel.setFocus(true);
			}
		});
		flexTable.getCellFormatter().setWidth(row, 4, "1%");
		flexTable.setWidget(row, 4, fastSave);
		
		Label label1 = new Label(AON.MSG.concept());
		label1.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonTextRight());
		flexTable.getCellFormatter().setWidth(row, 5, "auto");
		flexTable.setWidget(row, 5, label1);

		manualConcept = new TextBox();
		manualConcept.setStyleName(AON.AON_CSS.aonInputText());
		manualConcept.setTabIndex(-1);
		manualConcept.setVisibleLength(20);
		manualConcept.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getWrapper().setManualConcept(manualConcept.getValue());
				_paintEntry();
				getWrapper().getAccountEntry().setDirty(true);
				getCallback().getModule().refreshIdLabel();
			}
		});
		flexTable.getCellFormatter().setWidth(row, 6, "1%");
		flexTable.setWidget(row, 6, manualConcept);
		row++;
	}

	private void createPayTable() {
		int tabindex = PAY_PANEL_TAB_OFFSET;
		payTable = new FlexTable();
		payTable.setVisible(false);
		payTable.setStyleName(AON.AON_CSS.aonBorderTop());
		payTable.addStyleName(AON.AON_CSS.aonWidthAll());
		payTable.addStyleName(AON.AON_CSS.aonMarginTop());
		
		int row = 0;
		int col = 0;
		
		InlineLabel vtoLabel = new InlineLabel(AON.MSG.financeAbbr());
		payTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonFontMedium());
		payTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonWidth50());
		payTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
		payTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBackgroundWhite());
		payTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
		payTable.setWidget(row, col, vtoLabel);
		col++;
		
		InlineLabel dateLabel = new InlineLabel(AON.MSG.date());
		payTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonNowrap());
		payTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
		payTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonFontSmall());
		payTable.getCellFormatter().setWidth(row, col, "1%");
		payTable.setWidget(row, col, dateLabel);
		col++;
		
		payDate = new DateBoxEx();
		payDate.setTabIndex(++tabindex);
		payTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonNowrap());
		payTable.getCellFormatter().setWidth(row, col, "1%");
		payTable.setWidget(row, col, payDate);
		payDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				getWrapper().getFinances().get(0).setDueDate(event.getValue());
				getWrapper().getAccountEntry().setDirty(true);
				getCallback().getModule().refreshIdLabel();
				_paintEntry();
			}
		});
		col++;

		payStatusLabel  = new InlineLabel(AON.MSG.date());
		payTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonNowrap());
		payTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
		payTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonFontMedium());
		payTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonColorRed());
		payTable.getCellFormatter().setWidth(row, col, "1%");
		payTable.setWidget(row, col, payStatusLabel);
		col++;

		if (getCallback().getModule().getConfiguration().getPayMethods() != null 
			&& !getCallback().getModule().getConfiguration().getPayMethods().isEmpty()) {
			InlineLabel payMethodLabel = new InlineLabel(AON.MSG.payMethod());
			payTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonNowrap());
			payTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
			payTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonFontSmall());
			payTable.getCellFormatter().setWidth(row, col, "1%");
			payTable.setWidget(row, col, payMethodLabel);
			col++;
			
			payMethodList = new PayMethodListBox();
			payMethodList.setTabIndex(++tabindex);
			payMethodList.fill(getCallback().getModule().getConfiguration().getPayMethods());
			payMethodList.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					getWrapper().getFinances().get(0).setPayMethod(payMethodList.getValue());
					getWrapper().getAccountEntry().setDirty(true);
					getCallback().getModule().refreshIdLabel();
				}
			});
			payTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonNowrap());
			payTable.getCellFormatter().setWidth(row, col, "1%");
			payTable.setWidget(row, col, payMethodList);
			col++;
		}
		
		payAccountLabel = new InlineLabel(AON.MSG.accountAbr());
		payTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonNowrap());
		payTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
		payTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonFontSmall());
		payTable.getCellFormatter().setWidth(row, col, "1%");
		payTable.setWidget(row, col, payAccountLabel);
		col++;
		
		payAccount = new AccountBox(getCallback().getCurrentDomainName(),getCallback().getCurrentDomainId());
		payAccount.setTabIndex(++tabindex);
		payAccount.addSelectionHandler(new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				getCallback().getModule().onBalance(event.getSelectedItem());
			}
		});
		payAccount.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				extraPanel.setFocus();
			}
		});
		payAccount.addSelectionHandler(new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				if ( getWrapper().getFinances().get(0).isPending() ) {
					if (event.getSelectedItem() != null) {
						getWrapper().setFinanceRecordable(true);
						getWrapper().getFinances().get(0).setPayAccountId(event.getSelectedItem().getId());
						getWrapper().getFinances().get(0).setPayAccountCode(event.getSelectedItem().getCode());
						getWrapper().getFinances().get(0).setPayAccountDescription(event.getSelectedItem().getDescription());
					} else {
						getWrapper().setFinanceRecordable(false);
						getWrapper().getFinances().get(0).setPayAccountId(null);
						getWrapper().getFinances().get(0).setPayAccountCode(null);
						getWrapper().getFinances().get(0).setPayAccountDescription(null);
					}
					_paintEntry();
					getWrapper().getAccountEntry().setDirty(true);
					getCallback().getModule().refreshIdLabel();
				}
			}
		});
		payTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonWidthAuto());
		payTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
		payTable.setWidget(row, col, payAccount);
		col++;
	}
	
	@Override
	public void reset(final AccountEntry base,final ISelectionCallback cbk) {
		if (base == null) {
			getCallback().getModule().onError("[ERROR INTERNO] No hay un apunte base del que crear la factura");
		}
		rootPanel.showWidget(0);
		AccountingInvoice ai = new AccountingInvoice();
		ai.setAccountEntry(new AccountEntry()
			.setPeriod(base.getPeriod())
			.setDomain(getCallback().getCurrentDomainId())
			.setConfidential(base.isConfidential())
			.setEntryDate(base.getEntryDate())
			.setActivity(base.getActivity())
			.setJournal(null));
		select(null, ai, cbk);
	}
	
	@Override
	public void select(final Integer id,final IAccountEntryWrapper wrp,final ISelectionCallback cbk) {
		getCallback().getModule().onClearSessionLog();
		if (id != null) {
			getFiscalService().getAccountingInvoice(getCallback().getCurrentDomainName()
				,getCallback().getCurrentDomainId(),id
				,new AsyncCallback<AccountingInvoice>() {
						@Override
						public void onSuccess(AccountingInvoice result) {
							select(result,cbk);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							getCallback().getModule().onError(caught.getMessage());
						}
					});
		} else {
			if (wrp != null) {
				AccountingInvoice ai = (AccountingInvoice) wrp;
				if (ai.getInvoice() != null) {
					select(getWrapper(),cbk);
				} else {
					setWrapper(ai);
					registryBox.setValue(new AccountingRegistry(),false);
					registryBox.setEnabled(true);
					flexTable.setVisible(false);
					payTable.setVisible(false);
					withholdingPanel.setVisible(false);
					vatPanel.setVisible(false);
					extraPanel.invoiceChanged(getWrapper());
					if (cbk != null) cbk.onSuccess();
				}
			} else {
				getCallback().getModule().onError("[ERROR INTERNO] No hay que seleccionar.");
			}
		}
		
	}		

	public void select(AccountingInvoice result,final ISelectionCallback cbk) {
		populate(result);
		getCallback().getModule().onBalance(getWrapper());
		getCallback().getModule().onPreview(getWrapper());
		if (cbk != null) {
			cbk.onSuccess();
		}
	}
	
	private void populate(AccountingInvoice result) {
		setWrapper(result);
		_paint();
	}

	private void _paint() {
		if (getWrapper() != null && getWrapper().getRegistry() != null) {
			if (isAccountSource()) {
				LOGGER.info("Editing invoice as account source");
				rootPanel.showWidget(0);
				vatPanel.setVisible(true);
				extraPanel.setVisible(true);
				InvoicePanelCallback invoiceCallback = new InvoicePanelCallback();
				fillSalesSeries();
				extraPanel.paint(invoiceCallback);
				vatPanel.paint();
				withholdingPanel.paint();
				_paintEntry();
				registryBox.setValue(getWrapper().getRegistry());
				registryBox.setEnabled(getCallback().getModule().isNew());
				extraPanel.invoiceChanged(getWrapper());
				getWrapper().getInvoice().getType().visit(getWrapper(),new InvoicePanelVisitor());
			} else {
				LOGGER.info("Viewing invoice as management source");
				nonEditablePanel.clear();
				nonEditablePanel.setWidget( new InvoiceViewer(getWrapper().getInvoice()));
				rootPanel.showWidget(1);
				_paintEntry();
			}
		}
	}
	
	private void fillSalesSeries() {
		series.clear();
		series.addItem(" --- ","");
		if (getCallback().getModule().getConfiguration().getInvoiceSalesSeries() != null 
			&& getCallback().getModule().getConfiguration().getInvoiceSalesSeries().size() > 0) {
			LinkedList<String> rectificationSeries = getCallback().getModule().getConfiguration().getInvoiceRectificationSalesSeries();
			for (String ser : getCallback().getModule().getConfiguration().getInvoiceSalesSeries()) {
				boolean rectifierSerie = (rectificationSeries != null && rectificationSeries.contains(ser));
				if (!rectifierSerie || (rectifierSerie && getWrapper().getInvoice().isRectifier())) {
					series.addItem(ser);
				}
			}
		}
		series.setSelectedIndex(0);
	}
	
	private class InvoicePanelVisitor implements IAccountingInvoiceTypeVisitor {

		@Override
		public void visitPurchase(AccountingInvoice invoice) {
			populatePurchaseInvoice(invoice);
		}

		@Override
		public void visitSales(AccountingInvoice invoice) {
			populateSalesInvoice(invoice);
		}

		@Override
		public void visitExpenses(AccountingInvoice invoice) {
			populateExpensesInvoice(invoice);
		}

		@Override
		public void visitUndeductible(AccountingInvoice invoice) {
			populateExpensesInvoice(invoice);
		}
		
	}

	private void populateSalesInvoice(AccountingInvoice invoice) {
		invoice.getAccountEntry().setEntryType(AccountEntryType.SALES_INVOICE);
		for (int i = 0; i < series.getItemCount(); i++) {
			if (AonStringUtils.isBlank(invoice.getInvoice().getSeries()) && AonStringUtils.isBlank(series.getValue(i)) 
				||  (AonStringUtils.equals(invoice.getInvoice().getSeries(), series.getValue(i)))) {
				series.setSelectedIndex(i);
				break;
			}
		}
		number.setValue(invoice.getInvoice().getNumber());
		flexTable.setVisible(true);
		payTable.setVisible(true);
		series.setVisible(true);
		number.setVisible(true);
		referenceCode.setVisible(false);
		series.setFocus(true);
		invoiceTotal.setValue(invoice.getInvoice().getTotal());
		withholdingPanel.setVisible(invoice.isWithholding());
		withholdingPanel.setValue( invoice.getWithholdingData() );
		getCallback().getModule().refreshIdLabel();
		manualConcept.setValue(getWrapper().getManualConcept());
		populatePayment(invoice);
	}
	private void populatePurchaseInvoice(AccountingInvoice invoice) {
		invoice.getAccountEntry().setEntryType(AccountEntryType.PURCHASE_INVOICE);
		flexTable.setVisible(true);
		payTable.setVisible(true);
		series.setVisible(false);
		number.setVisible(false);
		referenceCode.setValue(invoice.getInvoice().getReferenceCode());
		referenceCode.setVisible(true);
		referenceCode.setFocus(true);
		invoiceTotal.setValue(invoice.getTotalInvoice());
		withholdingPanel.setVisible(invoice.isWithholding());
		withholdingPanel.setValue( invoice.getWithholdingData() );
		getCallback().getModule().refreshIdLabel();
		manualConcept.setValue(getWrapper().getManualConcept());
		populatePayment(invoice);
	}
	private void populateExpensesInvoice(AccountingInvoice invoice) {
		invoice.getAccountEntry().setEntryType(AccountEntryType.EXPENSE_INVOICE);
		flexTable.setVisible(true);
		payTable.setVisible(true);
		series.setVisible(false);
		number.setVisible(false);
		referenceCode.setValue(invoice.getInvoice().getReferenceCode());
		referenceCode.setVisible(true);
		referenceCode.setFocus(true);
		invoiceTotal.setValue(invoice.getTotalInvoice());
		withholdingPanel.setVisible(invoice.isWithholding());		
		withholdingPanel.setValue( invoice.getWithholdingData() );
		getCallback().getModule().refreshIdLabel();
		manualConcept.setValue(getWrapper().getManualConcept());
		populatePayment(invoice);
	}

	private void populatePayment(AccountingInvoice invoice) {
		payAccountLabel.setVisible(false);
		payAccount.setEnabled(true);
		payAccount.setVisible(false);
		payMethodList.setEnabled(false);
		
		payStatusLabel.setText(AonStringUtils.EMPTY);
		if (invoice.hasFinances()) {
			if (invoice.getFinances().size() == 1) {
				Finance finance = invoice.getFinances().get(0);
				payDate.setValue(finance.getDueDate());
				getWrapper().setFinanceRecordable(false);
				if (finance.getId() == null) { // NUEVO
//					finance.setPayMethod(payMethodList.getValue());
//					if (payAccount.getId() != null) {
//						getWrapper().setFinanceRecordable(true);
//						finance.setPayAccountId(payAccount.getId());
//						finance.setPayAccountCode(payAccount.getValue());
//						finance.setPayAccountDescription(payAccount.getDescription());
//					} else {
//						getWrapper().setFinanceRecordable(false);
//						finance.setPayAccountId(null);
//						finance.setPayAccountCode(null);
//						finance.setPayAccountDescription(null);
//					}
					payAccount.setValue(finance.getPayAccountId(), finance.getPayAccountCode()
							, finance.getPayAccountDescription(), false);
					payMethodList.setValue(finance.getPayMethod());
					payAccountLabel.setVisible(true);
					payAccount.setVisible(true);
					payMethodList.setEnabled(true);
				} else {
					payMethodList.setValue(finance.getPayMethod());
					payMethodList.setEnabled(false);
				}
				if (!finance.isPending()) {
					payStatusLabel.setText(finance.getFinanceStatus().getDescription());
					payAccount.setEnabled(false);
				}
			} 
		} 
	}

	private class InvoicePanelRegistryVisitor implements IAccountingRegistryTypeVisitor {

		@Override
		public void visitCustomer(AccountingRegistry reg) {
			populateSalesInvoice(getWrapper());
			fastSave.setEnabled(false);
		}

		@Override
		public void visitCreditor(AccountingRegistry reg) {
			populateExpensesInvoice(getWrapper());
			fastSave.setEnabled(false);
		}

		@Override
		public void visitSupplier(AccountingRegistry reg) {
			populatePurchaseInvoice(getWrapper());
			fastSave.setEnabled(false);
		}
		
	}
	
	private void _paintEntry() {
		AccountEntry[] entries = InvoiceRecorder.recordInvoice(getWrapper());
		getCallback().getModule().onPreview(AccountEntryModule.getWrapperArray (entries) );		
	}
	
	private boolean isAccountSource() {
		boolean sourceAccount = true;
		if (getWrapper().getInvoice() != null && getWrapper().getInvoice().getDetails() != null) {
			for (InvoiceDetail detail : getWrapper().getInvoice().getDetails()) {
				LOGGER.info(detail.getSource().getDescription());
				if (detail.getSource() != InvoiceSource.ACCOUNT) {
					sourceAccount = false;
					break;
				}
			}
		}
		return sourceAccount;
	}
	
	@Override
	public boolean isUpdatable() {
		
		return (super.isUpdatable()
				&& getAccountEntry().isInvoice()
				&& isAccountSource()
				&& !hasPaidFinances()
				);
	}
	
	private boolean hasPaidFinances() {
		boolean paidFinances = false;
		for (Finance finance : getWrapper().getFinances()) {
			paidFinances = paidFinances 
				|| finance.getFinanceStatus() == FinanceStatus.PAID
				|| finance.getFinanceStatus() == FinanceStatus.BATCHED
				|| finance.getFinanceStatus() == FinanceStatus.SETTLED;
		}
		return paidFinances;
	}
	
	@Override
	public String getNoUpdatableCause() {
		if (hasPaidFinances()) {
			return AON.MSG.hasPaidFinances();
		}
		if (!isAccountSource()) {
			return AON.MSG.managmentInvoice();
		}
		return null;
	}
	
	@Override
	public boolean isStatusMsgEnabled() {
		return getWrapper() != null && getWrapper().isAccountSource();
	}

	@Override
	public void save(final AsyncCallback<AccountEntry[]> cbk) {
		fastSave.setVisible(false);
		vatPanel.hideButtons(false);
		extraPanel.hideButtons(false);
		getFiscalService().save(getCallback().getCurrentDomainName()
				, getCallback().getCurrentDomainId()
				, getWrapper(), new AsyncCallback<AccountingInvoice>() {

			@Override
			public void onSuccess(AccountingInvoice result) {
				setWrapper(result);
				lastRegistry = result.getRegistry();
				int entriesSize = getWrapper().getAccountEntries().size();
				AccountEntry[] entries = new AccountEntry[entriesSize];  
				fastSave.setVisible(true);
				vatPanel.hideButtons(true);
				extraPanel.hideButtons(true);
				cbk.onSuccess(getWrapper().getAccountEntries().toArray(entries));
			}

			@Override
			public void onFailure(Throwable caught) {
				fastSave.setVisible(true);
				vatPanel.hideButtons(true);
				extraPanel.hideButtons(true);
				cbk.onFailure(caught);
			}

		});
	}
	
	private class InvoicePanelCallback implements IInvoicePanelCallback {
		@Override
		public AccountEntryModule getModule() {
			return getCallback().getModule();
		}
		@Override
		public String getCurrentDomainName() {
			return getCallback().getCurrentDomainName();
		}
		@Override
		public int getCurrentDomainId() {
			return getCallback().getCurrentDomainId();
		}
		@Override
		public String getCurrentUser() {
			return getCallback().getCurrentUser();
		}
		@Override
		public AccountingInvoice getInvoice() {
			return getWrapper();
		}
		
		@Override
		public boolean isInvestAssetsAvailable() {
			return !getWrapper().isSales() 
				&& !getWrapper().isSurcharge()
				&& getWrapper().isOutputVatEnabled() != getWrapper().isInputVatEnabled()
				&& getCallback().getModule().getConfiguration().isInvestAssetsAvailable();
			
		}
		@Override
		public void enableInvoiceTotal(boolean enable) {
			invoiceTotal.setEnabled(enable);
		}
		@Override
		public void setFocusOnRegistry() {
			registryBox.setFocus(true);
		}
		@Override
		public void setFocusOnPayDate() {
			payDate.getTextBox().selectAll();
			payDate.setFocus(true);
		}
		@Override
		public void setFocusOnWithholding() {
			withholdingPanel.setFocus(true);
		}
		
		
		@Override
		public void paintEntry() {
			_paintEntry();
			getWrapper().getAccountEntry().setDirty(true);
			getCallback().getModule().refreshIdLabel();
		}

	};

	@Override
	public void manageWidgets(boolean canRemove, boolean canEdit) {
		if (fastSave != null) fastSave.setVisible(canEdit);
		if (vatPanel != null) vatPanel.enableElements(canRemove,canEdit);		
	}
	
	private void repeatLastInvoice(final ISelectionCallback cbk) {
		Integer registryId = getWrapper().getRegistry().getId();
		getFiscalService().getRegistryLastAccountingInvoice(getCallback().getCurrentDomainName()
				,getCallback().getCurrentDomainId(), registryId
				,new AsyncCallback<AccountingInvoice>() {
						@Override
						public void onSuccess(AccountingInvoice result) {
							result.setAccountEntry(getWrapper().getAccountEntry());
							result.getInvoice().setIssueDate(getCallback().getModule().getEntryDate());
							result.getInvoice().setTaxDate(getCallback().getModule().getEntryDate());
							if (!result.getInvoice().isSales()) {
								result.getInvoice().setReferenceCode(referenceCode.getValue());
							}
							if (result.getFinances() != null && result.getFinances().size() > 0) {
								result.getFinances().get(0).setDueDate(getCallback().getModule().getEntryDate());
							}
							populate(result);
							getCallback().getModule().onBalance(getWrapper().getAccountEntry());
							if (cbk != null) {
								cbk.onSuccess();
							}							
						}
						
						@Override
						public void onFailure(Throwable caught) {
							getCallback().getModule().onError("No se ha encontrado ninguna factura");
						}
					});
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<AccountingInvoice> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
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
	public void setTabIndex(int index) {
		registryBox.setTabIndex(index);
	}

	@Override
	public void setFocus(boolean b) {
		registryBox.setFocus(b);
	}

	public void entryDateChanged(Date entryDate) {
		getWrapper().getAccountEntry().setEntryDate(entryDate);
		if (getWrapper().getInvoice() != null) {
			getWrapper().getInvoice().setIssueDate(entryDate);
			getWrapper().getInvoice().setTaxDate(entryDate);
			extraPanel.taxDate.setValue(entryDate);
		}
	}
	public void activityChanged(Integer activty) {
		getWrapper().getAccountEntry().setActivity(activty);
		if (getWrapper().getInvoice() != null) {
			getWrapper().getInvoice().setActivity(activty);
		}
	}
	public void confidentialChanged(boolean confidential) {
		getWrapper().getAccountEntry().setConfidential(confidential);
		if (getWrapper().getInvoice() != null) {
			getWrapper().getInvoice().setConfidential(confidential);
		}
	}
}

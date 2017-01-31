package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IAccountEntryModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.SessionLog;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.FinanceSearchPanel.IFinancePanelCallback;
import com.esferalia.aon.gwt.fiscal.client.finance.FinancePrinter;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceRecorder;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;


public class FinanceEntryPanel extends WizardContentBase<FinanceEntry> implements HasSelectionHandlers<Finance>{
	
	static final String BACKGROUND_COLOR = "#EEEEEE";
	public final static int TAB_OFFSET = 1000;
	public final static int SEARCH_PANEL_TAB_OFFSET = 10000;
	
	private DockLayoutPanel resultPanel;
	private SessionLog workingLog;
	
	private AccountBox bankAccount;
	private DoubleBox  expenses;
	private AccountBox expensesAccount;
	private FlowPanel container;
	private FinanceSearchPanel financeSearchPanel;
	private FinanceEntry financeEntry;
	
	private final KeyUpHandler f9KeyHandler = new KeyUpHandler() {
		@Override
		public void onKeyUp(KeyUpEvent event) {
			if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
				financeSearchPanel.setFocus(true);
	        }
		}
	};
	
	public FinanceEntryPanel(final IAccountEntryModuleCallback callback) {
		setCallback(callback);
		
		SplitLayoutPanel rootPanel = new SplitLayoutPanel(4);
		
		workingLog = new SessionLog();
		rootPanel.addSouth(workingLog, 150);

		financeSearchPanel = new FinanceSearchPanel(
			AccountEntryModule.getCurrentDomainName()
			, AccountEntryModule.getCurrentDomain()
			, new IFinancePanelCallback() {

				@Override
				public boolean isSelected(Finance finance) {
					if (finance == null) return false;
					return getWrapper().getTrackings().containsKey(finance.getId())
						&& getWrapper().getTrackings().get(finance.getId()).isChecked();
				}
				
			}
			
			,SEARCH_PANEL_TAB_OFFSET);
		financeSearchPanel.setStyleName(AON.AON_CSS.aonInvoicePanelEast());
		financeSearchPanel.addSelectionHandler( new SelectionHandler<Finance>() {
			
			@Override
			public void onSelection(SelectionEvent<Finance> event) {
				if (event.getSelectedItem().isSelected()) {
					financeEntry.add(event.getSelectedItem());	
				} else {
					financeEntry.remove(event.getSelectedItem());	
				}
				refreshTable();
				_paintEntry();
			}

		});
		addSelectionHandler( new SelectionHandler<Finance>() {
			
			@Override
			public void onSelection(SelectionEvent<Finance> event) {
				financeSearchPanel.uncheck(event.getSelectedItem());		
			}
		});
		financeSearchPanel.getElement().getStyle().setBackgroundColor(BACKGROUND_COLOR);		
		rootPanel.addEast(financeSearchPanel, 600);
		
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.setStyleName(AON.AON_CSS.aonInvoicePanel());
		centerPanel.getElement().getStyle().setBackgroundColor(FinanceEntryPanel.BACKGROUND_COLOR);
		int tabindex = TAB_OFFSET;
		resultPanel = new DockLayoutPanel(Unit.PX);
		
		SimpleLayoutPanel northPanel = new SimpleLayoutPanel();
		fillNorthPanel(northPanel,tabindex);
		resultPanel.addNorth(northPanel, 105);
		
		
		ScrollPanel resultScrollPanel = new ScrollPanel();
		resultScrollPanel.setStyleName(AON.AON_CSS.aonScrollArea());
		resultScrollPanel.addStyleName(AON.AON_CSS.aonMarginBottom());
		container = new FlowPanel();
		resultScrollPanel.setWidget(container);
		resultPanel.add(resultScrollPanel);
		
		centerPanel.setWidget(resultPanel);
		
		rootPanel.add(centerPanel);

		initWidget(rootPanel);
	}
	
	private void refreshTable() {
		container.clear();
		for (final FinanceTracking ft : financeEntry.getTrackings().values()) {
			final FocusPanel financePanel = FinancePrinter.print(ft.getFinance());
			financePanel.addStyleName(AON.AON_CSS.aonPaddingLeftImportant());
			boolean updatable = isUpdatable() && ft.isLastTracking();
			if (updatable) {
				if (ft.isDeleted()) {
					financePanel.addStyleName(AON.AON_CSS.aonTextLineThrough());
					financePanel.addStyleName(AON.AON_CSS.aonIconCheck());
				} else {
					financePanel.addStyleName(AON.AON_CSS.aonIconChecked());
				}
				financePanel.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (!isNew()) {
							ft.setDeleted(!ft.isDeleted());
							ft.setChecked(!ft.isDeleted());
						} else {
							getWrapper().getTrackings().remove(ft.getFinance().getId());
						}
						refreshTable();
						_paintEntry();
						SelectionEvent.<Finance>fire( FinanceEntryPanel.this, ft.getFinance());
					}
				});
			} else {
				if (getWrapper().isFromFinanceBatch()) {
					financePanel.setTitle( AON.MSG.fromFBatch() );
				} else if (!ft.isLastTracking()) {
					financePanel.setTitle( AON.MSG.noLastTracking() );
				} else {
					financePanel.setTitle( AON.MSG.unableToUpdate() );
				}
				
				
				financePanel.addStyleName(AON.AON_CSS.aonIconWarn());
				
			}
			container.add(financePanel);
		}
	}

	@Override
	public FinanceEntry getWrapper() {
		return financeEntry;
	}

	@Override
	public void setWrapper(FinanceEntry wrapper) {
		this.financeEntry = wrapper;
	}

	
	private void fillNorthPanel(SimpleLayoutPanel northPanel,int tabIndex) {
		FlowPanel flowNorthPanel = new FlowPanel();
		Label label = new Label(AON.MSG.financeSelected());
		label.setStyleName(AON.AON_CSS.aonWidthAll());
		label.addStyleName(AON.AON_CSS.aonPaddingLeft());
		label.addStyleName(AON.AON_CSS.aonBorderBottom());
		label.addStyleName(AON.AON_CSS.aonTextCenter());
		label.addStyleName(AON.AON_CSS.aonBold());
		flowNorthPanel.add(label);
		
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonBorderBottom());
		tab.addStyleName(AON.AON_CSS.aonPadding());
		tab.addStyleName(AON.AON_CSS.aonWidthAll());
		
		tab.getColumnFormatter().setWidth(0, "100px");
		tab.getColumnFormatter().setWidth(1, "9px");
		tab.getColumnFormatter().setWidth(2, "60px");
		tab.getColumnFormatter().setWidth(3, "auto");
		
		bankAccount = createAccountBox();
		bankAccount.addKeyUpHandler(f9KeyHandler);
		bankAccount.addSelectionHandler( new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				getWrapper().setBankAccount(event.getSelectedItem());
				valueChanged();
			}
		});
		tab.setWidget(0, 0, new Label(AON.MSG.account()));
		tab.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonBold());
		tab.setWidget(0, 1, bankAccount);
		tab.getFlexCellFormatter().setColSpan(0, 1, 3);
		
		
		tab.setWidget(1, 0, new Label(AON.MSG.expenses()));
		tab.getCellFormatter().setStyleName(1,0, AON.AON_CSS.aonBold());
		
		expenses = new DoubleBox();
		expenses.addKeyUpHandler(f9KeyHandler);
		expenses.setVisibleLength(6);
		expenses.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getWrapper().setExpenses(expenses.getValue());
				valueChanged();
			}
		});
		tab.setWidget(1, 1, expenses);

		tab.setWidget(1, 2, new Label(AON.MSG.accountAbr()));
		tab.getCellFormatter().setStyleName(1,2, AON.AON_CSS.aonBold());

		expensesAccount = createAccountBox();
		expensesAccount.setRequired(false);
		expensesAccount.addKeyUpHandler(f9KeyHandler);
		tab.setWidget(1, 3, expensesAccount);
		expensesAccount.addSelectionHandler( new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				getWrapper().setExpensesAccount(event.getSelectedItem());
				valueChanged();
			}
		});
		flowNorthPanel.add(tab);
		northPanel.setWidget(flowNorthPanel);	
	}
	

	protected void valueChanged() {
		_paintEntry();
	}

	@Override
	public void save(final AsyncCallback<AccountEntry[]> callback) {
		
		getFiscalService().save(AccountEntryModule.getCurrentDomainName()
				,AccountEntryModule.getCurrentDomain()
				, getWrapper(), new AsyncCallback<FinanceEntry>() {

			@Override
			public void onSuccess(FinanceEntry result) {
				setWrapper(result);
				callback.onSuccess(new AccountEntry[]{result.getAccountEntry()});
			}

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

		});
	}
	
	@Override
	public void reset(final AccountEntry base,final ISelectionCallback cbk) {
		if (base == null) {
			getCallback().getModule().onError("[ERROR INTERNO] No hay un apunte base del que crear la factura");
		}
		FinanceEntry ai = new FinanceEntry();
		ai.setAccountEntry(new AccountEntry()
			.setEntryType(AccountEntryType.FINANCE)
			.setPeriod(base.getPeriod())
			.setDomain(AccountEntryModule.getCurrentDomain())
			.setConfidential(base.isConfidential())
			.setEntryDate(base.getEntryDate())
			.setActivity(base.getActivity()));
		select(null, ai, cbk);
	}
	
	@Override
	public void select(final Integer id,final IAccountEntryWrapper wrp,final ISelectionCallback cbk) {
		workingLog.clear();
		if (id != null) {
			getFiscalService().getFinanceEntry(AccountEntryModule.getCurrentDomainName()
					,AccountEntryModule.getCurrentDomain(),id
					,new AsyncCallback<FinanceEntry>() {
							@Override
							public void onSuccess(FinanceEntry result) {
								select(result,cbk);
							}
							
							@Override
							public void onFailure(Throwable caught) {
								getCallback().getModule().onError(caught.getMessage());
							}
						});
		} else {
			if (wrp != null) {
				select((FinanceEntry) wrp, cbk);
			} else {
				getCallback().getModule().onError("Asiento no encontrado");
			}
		}
	}
	public void select(FinanceEntry fe,final ISelectionCallback cbk) {
		setWrapper( fe );
		getCallback().getModule().onBalance(getWrapper().getAccountEntry());
		populate();
		refreshTable();
		if (isUpdatable()) {
			financeSearchPanel.enable();
			financeSearchPanel.initialize();
		} else{
			financeSearchPanel.disable();
		}
		if (cbk != null) {
			cbk.onSuccess();
		}
	}

		

	private AccountBox createAccountBox() {
		AccountBox ab = new AccountBox(AccountEntryModule.getCurrentDomainName(), AccountEntryModule.getCurrentDomain());
		ab.addSelectionHandler(new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				Account acc = event.getSelectedItem();
				callback.getModule().onBalance(acc);
			}
		});
		return ab;
	}
	
	private void populate() {
		setAccount(bankAccount,getWrapper().getBankAccount());
		bankAccount.setEnabled( isUpdatable() );
		expenses.setValue(getWrapper().getExpenses());
		expenses.setEnabled( isUpdatable() );
		setAccount(expensesAccount,getWrapper().getExpensesAccount());
		expensesAccount.setEnabled( isUpdatable() );
	}

	private void setAccount(AccountBox accountBox, Account account) {
		if (account != null) {
			accountBox.setValue(account.getId(),account.getCode(),account.getDescription(),false);
		} else {
			accountBox.setValue(null,null,null,false);
		}
	}

	private void _paintEntry() {
		AccountEntry[] entries = FinanceRecorder.recordFinanceEntry(getWrapper());
		onLog(AccountEntryModule.getWrapperArray (entries));
	}
	
	public void onLog(IAccountEntryWrapper wrapper) {
		workingLog.clear();
		workingLog.addPreview(wrapper);			
	}
	
	public void onLog(IAccountEntryWrapper[] wrappers) {
		workingLog.clear();
		for (int i = (wrappers.length - 1); i>=0; i--) {
			workingLog.addPreview(wrappers[i]);
		}
	}
	@Override
	public boolean isUpdatable() {
		return super.isUpdatable() && !getWrapper().isFromFinanceBatch();
	}
	
	@Override
	public void manageWidgets(boolean canRemove, boolean canEdit) {
	}


	@Override
	public int getTabIndex() {
		return bankAccount.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		bankAccount.setAccessKey(key);
	}

	@Override
	public void setTabIndex(int index) {
		bankAccount.setTabIndex(index);
	}
	
	@Override
	public void setFocus(boolean b) {
		bankAccount.setFocus(b);
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Finance> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
	
}

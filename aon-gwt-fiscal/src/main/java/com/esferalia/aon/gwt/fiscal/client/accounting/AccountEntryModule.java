package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.TextCell;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class AccountEntryModule extends MainEntryPoint {

	static FiscalServiceAsync fiscalService;
	
	interface AccountEntryModuleBinder extends UiBinder<Widget, AccountEntryModule> {
	}
	private static final AccountEntryModuleBinder BINDER = GWT.create(AccountEntryModuleBinder.class);

	private static NumberFormat FMT = NumberFormat.getDecimalFormat();
	static {
		FMT.overrideFractionDigits(2);
	}

	private static class AccountEntryObject {
		private AccountEntry ae;
		
		public AccountEntryObject() {
			newAccountEntry();
		}
		
		public void newAccountEntry() {
			Date date = ae!=null?ae.getEntryDate():new Date(); 
			ae = new AccountEntry()
				.setEntryDate(date)
				.setEntryType(AccountEntryType.MANUAL);
		}
		
		public Date getEntryDate() {
			return this.ae.getEntryDate();
		}
		public AccountEntryObject setEntryDate(Date entryDate) {
			this.ae.setEntryDate(entryDate);
			return this;
		}
		
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	@UiField
	ResultsPanel resultsPanel;
	@UiField
	MinimizePanel footPanel;
	
	@UiField
	DateBoxEx entryDate;
	
	@UiField(provided=true)
	AccountBox account;
	@UiField
	TextBox concept;
	@UiField
	DoubleBox debit;
	@UiField
	DoubleBox credit;
	@UiField(provided=true)
	AccountBox balAccount;
	
	private AccountEntryObject current;
	
	private ListDataProvider<AccountEntryDetail> detailProvider;
	private SingleSelectionModel<AccountEntryDetail> detailModel;
	
	@UiField(provided = true)
	CellTable<AccountEntryDetail> detailTable;
	
	@Override
	public void onModuleLoad() {
		AON.ensureInjected();
		
		FiscalServiceAsync mod180ServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(mod180ServiceRaw);
		
		account = new AccountBox(getCurrentDomainName(),getCurrentDomain()); 
		balAccount = new AccountBox(getCurrentDomainName(),getCurrentDomain());
		
		CellTable.Resources aonTableStyle = GWT.create(AonCellTable.class);
		detailTable = new CellTable<AccountEntryDetail>(25,aonTableStyle);
		
		detailTable.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		detailTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		detailTable.setEmptyTableWidget(new HTML(AON.MSG.noData()));
		detailProvider = new ListDataProvider<AccountEntryDetail>();
		detailProvider.addDataDisplay(detailTable);
		detailModel = new SingleSelectionModel<AccountEntryDetail>();
		detailModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler(){
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				AccountEntryDetail aed = detailModel.getSelectedObject();
				account.setValue(aed.getAccountCode());
				concept.setValue(aed.getConcept());
				debit.setValue(aed.getDebit());
				credit.setValue(aed.getCredit());
				balAccount.setValue(aed.getBalancingAccountCode());
				Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
					@Override
					public void execute() {
						account.setFocus(true);
				}});		
			}
		});
		detailTable.setSelectionModel(detailModel);		
		
		addAccountColumn();
		addConceptColumn();
		addDebitColumn();
		addCreditColumn();
		addBalAccountColumn();


		Widget ui = BINDER.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
		
		setCurrent( new AccountEntryObject() );
		
	}
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	// -------------------------------------------------------------- UiHandler

	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}
	@UiHandler("footPanel")
	void onFootMaximize(MinimizeEvent event) {
	}

	private void closeFootPanel() {
		dockLayoutPanel.setWidgetSize(footPanel, 0);
	}
	
	private void showResultsPanel() {
		dockLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4);
	}
	
	// --------------------------------------------------------------- Table Columns
	private void addAccountColumn() {
		TextCell input = new TextCell();
		Column<AccountEntryDetail, String> accountColumn = new Column<AccountEntryDetail, String>(input) {
			@Override
			public String getValue(AccountEntryDetail detail) {
				return AonStringUtils.abbreviate(
						AonStringUtils.join(
								detail.getAccountCode()
								,AonStringUtils.SPACE
								,detail.getAccountDescription()
												),30);
			}
		};
		detailTable.addColumn(accountColumn, AON.MSG.account());
		detailTable.setColumnWidth(accountColumn, 200, Unit.PX);
	}

	private void addConceptColumn() {
		TextCell input = new TextCell();
		Column<AccountEntryDetail, String> conceptColumn = new Column<AccountEntryDetail, String>(input) {
			@Override
			public String getValue(AccountEntryDetail detail) {
				return detail.getConcept();
			}
		};
		detailTable.addColumn(conceptColumn, AON.MSG.concept());
		detailTable.setColumnWidth(conceptColumn, 200, Unit.PX);
	}
	
	private void addDebitColumn() {
		TextCell input = new TextCell();
		Column<AccountEntryDetail, String> debitColumn = new Column<AccountEntryDetail, String>(input) {
			@Override
			public String getValue(AccountEntryDetail detail) {
				return FMT.format( detail.getDebit() );
			}
		};
		detailTable.addColumn(debitColumn, AON.MSG.debit());
		debitColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
		detailTable.setColumnWidth(debitColumn, 150, Unit.PX);
	}

	private void addCreditColumn() {
		TextCell input = new TextCell();
		Column<AccountEntryDetail, String> creditColumn = new Column<AccountEntryDetail, String>(input) {
			@Override
			public String getValue(AccountEntryDetail detail) {
				return FMT.format( detail.getCredit() );
			}
		};
		detailTable.addColumn(creditColumn, AON.MSG.credit());
		creditColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
		detailTable.setColumnWidth(creditColumn, 150, Unit.PX);
	}

	private void addBalAccountColumn() {
		TextCell input = new TextCell();
		Column<AccountEntryDetail, String> balAccountColumn = new Column<AccountEntryDetail, String>(input) {
			@Override
			public String getValue(AccountEntryDetail detail) {
				return AonStringUtils.abbreviate(
						AonStringUtils.join(
								detail.getBalancingAccountCode()
								,AonStringUtils.SPACE
								,detail.getBalancingAccountDescription()
												),30);
			}
		};
		detailTable.addColumn(balAccountColumn, AON.MSG.balancingAccount());
		detailTable.setColumnWidth(balAccountColumn, 200, Unit.PX);
	}
	// --------------------------------------------------------------- Handlers
	@UiHandler("balAccount")
	void onBlurBalAccount(BlurEvent event) {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				AccountEntryDetail aed = detailModel.getSelectedObject(); 
				if ( aed != null) {
					populateDetail( aed );
					detailModel.setSelected(aed, false);
				} else {
					detailProvider.getList().add( populateDetail(new AccountEntryDetail()));			
				}
				detailTable.redraw();
				clearFields();
				account.setFocus(true);
		}});		
	}
	
	@UiHandler("entryDate")
	void onChangeEntryDate(ValueChangeEvent<Date> event) {
		this.current.setEntryDate( event.getValue() );
	}


	private AccountEntryDetail populateDetail(AccountEntryDetail aed) {
		return aed
			.setAccount(account.getId())
			.setAccountCode(account.getValue())
			.setAccountDescription(account.getDescription())
			.setConcept(concept.getText())
			.setDebit(debit.getValue())
			.setCredit(credit.getValue())
			.setBalancingAccount(balAccount.getId())
			.setBalancingAccountCode(balAccount.getValue())
			.setBalancingAccountDescription(balAccount.getDescription())
		;
	}

	private void clearFields() {
		account.setValue(null);
		concept.setValue(null);
		debit.setValue(0.0);
		credit.setValue(0.0);
		balAccount.setValue(null);
	}

	private void setCurrent(AccountEntryObject accountEntryObject) {
		this.current = accountEntryObject;
		entryDate.setValue( current.getEntryDate() );
	}
	
}

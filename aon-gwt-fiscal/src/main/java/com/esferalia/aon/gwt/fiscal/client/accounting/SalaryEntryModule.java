package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SalaryEntryModule extends MainEntryPoint {

	static CommonServiceAsync commonService;
	static FiscalServiceAsync fiscalService;
	
	interface SalaryEntryModuleBinder extends UiBinder<Widget, SalaryEntryModule> {
	}
	private static final SalaryEntryModuleBinder BINDER = GWT.create(SalaryEntryModuleBinder.class);

	@UiField
	DockLayoutPanel dockLayoutPanel;
	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	ResultsPanel resultsPanel;
	@UiField
	MinimizePanel footPanel;
	
	@UiField
	DateBoxEx fromDate; 
	@UiField
	DateBoxEx toDate;
	@UiField
	SimplePanel msgContainer;
	@UiField
	TextBox concept;
	@UiField
	ListBox banks;
	
	@UiField
	Button accept;
	
	@UiField
	SimplePanel tableContainer;

	FlowPanel errors;
	
	private static NumberFormat FMT = NumberFormat.getDecimalFormat();
	static {
		FMT.overrideFractionDigits(2);
	}
	private static DateTimeFormat DFMT = DateTimeFormat.getFormat("dd/MM/yyyy");	

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();
		
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		Widget ui = BINDER.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
		
		Date today = new Date();
		fromDate.setValue(DateUtils.getFirstDayOfMonth(today));
		toDate.setValue(DateUtils.getLastDayOfMonth(today));

		fromDate.addStyleName(AON.AON_CSS.aonInputText());
		fromDate.addStyleName(AON.AON_CSS.aonNopadding());
		toDate.addStyleName(AON.AON_CSS.aonInputText());
		toDate.addStyleName(AON.AON_CSS.aonNopadding());
		
		concept.setStyleName(AON.AON_CSS.aonInputText());
		concept.setVisibleLength(35);
		concept.setMaxLength(32);
		concept.setValue("N\u00D3MINAS");
		
		banks.addItem(AON.MSG.pendingPayment(),"");
		commonService.getCompanyBanks(getCurrentDomainName(), getCurrentDomain()
				, new AsyncCallback<LinkedList<CompanyBank>>() {
			
			@Override
			public void onSuccess(LinkedList<CompanyBank> result) {
				for (CompanyBank cb :  result) {
					banks.addItem(cb.getDisplay(), Integer.toString( cb.getId() ));
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(AON.MSG.registryBankReadError());
			}
		});
		checkPeriodEntries( false );
	}
	
	@UiHandler(value={"fromDate","toDate"})
	void onDateChange(ValueChangeEvent<Date> event) {
		if (validDates() ) {
			checkPeriodEntries(false);
		}
	}
	
	private boolean validDates() {
		boolean validDates = (fromDate.getValue() != null && toDate.getValue() != null && !toDate.getValue().before(fromDate.getValue()));
		accept.setEnabled(validDates);
		return validDates;
	}
	
	private void checkPeriodEntries(final boolean showPreview) {
		if (msgContainer.getWidget() != null) {
			msgContainer.remove(msgContainer.getWidget());
		}
		if (showPreview) {
			if (tableContainer.getWidget() != null) {
				tableContainer.remove(tableContainer.getWidget());
			}
		}
		fiscalService.getSalaryAccountEntries(getCurrentDomainName(), getCurrentDomain(), 
				fromDate.getValue(), toDate.getValue()
				, new AsyncCallback<LinkedList<AccountEntry>>() {
			
			@Override
			public void onSuccess(final LinkedList<AccountEntry> result) {
				if (result != null && result.size() > 0) {
					final InlineLabel msgLabel = new InlineLabel((AON.MSG.salaryEntryErrorMsg(result.size()))); 
					msgLabel.addStyleName(AON.AON_CSS.aonMarginLeft());
					msgLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
					msgLabel.addStyleName(AON.AON_CSS.aonBold());
					msgLabel.addStyleName(AON.AON_CSS.aonIconWarn());
					msgLabel.addStyleName(AON.AON_CSS.aonClickable());
					msgLabel.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							showPreview(new Label("Apuntes existentes en el periodo"),result,true);
						}
					});
					msgContainer.setWidget(msgLabel);
					if (showPreview) {
						showPreview(new Label("Apuntes existentes en el periodo"),result,true);
					}
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Nothing
			}
		});
	}

	@UiHandler("accept")
	void onAccept(ClickEvent event) {
		if (Window.confirm( AON.MSG.generateAccountEntry())) {
			errors = new FlowPanel();
			if (fromDate.getValue() == null) {
				addError("Fecha inicio erronea");
			}
			if (toDate.getValue() == null) {
				addError("Fecha fin erronea");
			}
			if (fromDate.getValue() != null && toDate.getValue() != null) {
				if (toDate.getValue().before(fromDate.getValue())) {
					addError("Rango de fechas incorrecto");
				}
				
			}
			if (errors.getWidgetCount() > 0) {
				splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4);
				resultsPanel.setWidget(errors);
			}
			Integer rbank = AonNumberUtils.toInteger(banks.getSelectedValue());
			fiscalService.insertSalaryAccountEntries(getCurrentDomainName(), getCurrentDomain(), 
					fromDate.getValue(), toDate.getValue(), 
					concept.getText(), rbank
					, new AsyncCallback<LinkedList<AccountEntry>>() {

						@Override
						public void onSuccess(LinkedList<AccountEntry> result) {
							if (msgContainer.getWidget() != null) {
								msgContainer.remove(msgContainer.getWidget());
							}
							fromDate.setValue(null);
							toDate.setValue(null);
							validDates();
							String msg = (result == null || result.isEmpty())
									?AON.MSG.noGeneratedAccountEntries()
									:AON.MSG.generatedAccountEntries();
							showPreview(new Label( msg ), result, false);
						}
						@Override
						public void onFailure(Throwable caught) {
							addError(caught.getMessage());
							splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4);
							resultsPanel.setWidget(errors);
						}

					});
		}
	}
	
	private void addError(String msg) {
		Label error = new Label(msg);
		error.addStyleName(AON.AON_CSS.aonMarginLeft());
		error.addStyleName(AON.AON_CSS.aonPaddingLeft());
		error.addStyleName(AON.AON_CSS.aonBold());
		error.addStyleName(AON.AON_CSS.aonIconError());
		errors.add(error);
	}
	
	// -------------------------------------------------------------- 

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
	
	private void showPreview(Widget title, LinkedList<AccountEntry> result, boolean deleteEnabled) {
		FlexTable table = new FlexTable();
		tableContainer.setWidget(table);
		
		table.setCellSpacing(0);
		table.addStyleName(AON.AON_CSS.aonWidthAll());
		table.addStyleName(AON.AON_CSS.aonMarginTop());
		int row = 0;
		
		table.setWidget(row, 0, title );
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonTextUnderline());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonDataTable());
		table.getFlexCellFormatter().setColSpan(row, 0, 6);
		++row;
		
		
		int i = 0;
		for ( final AccountEntry ae : result) {
			int col = 0;	
			table.getRowFormatter().setStyleName(row, (i%2==0)?AON.AON_CSS.aonOddBackground():AON.AON_CSS.aonEvenBackground());

			table.setWidget(row, col, new Label(DFMT.format(ae.getEntryDate() ) +" ("+ae.getJournal()+")"));
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonDataTable());
			table.getFlexCellFormatter().setColSpan(row, col, deleteEnabled?5:6);
			col++;
			if (deleteEnabled) {
				Button deleteButton = new Button(AON.MSG.deleteAction());
				deleteButton.setStyleName("aon-finding-toolbar-item");
				deleteButton.addStyleName(AON.AON_CSS.aonMarginLeft());
				deleteButton.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
				deleteButton.addStyleName(AON.AON_CSS.aonIconDelete());
				deleteButton.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						if (Window.confirm("Borrar el apunte?")) {
							fiscalService.deleteAccountEntry(getCurrentDomainName()
									, getCurrentDomain(), ae.getId()
									, new AsyncCallback<Void>() {

								@Override
								public void onSuccess(Void result) {
									checkPeriodEntries(true);
								}

								@Override
								public void onFailure(Throwable caught) {
									Window.alert("Error al borrar apunte.");
								}
							});
						}
					}
				});
				table.setWidget(row, col, deleteButton);
			}
			row++;
			col = 0;
			
			table.getRowFormatter().setStyleName(row, (i%2==0)?AON.AON_CSS.aonOddBackground():AON.AON_CSS.aonEvenBackground());
			table.setWidget(row, col, new Label( AON.MSG.account()));
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
			table.getFlexCellFormatter().setColSpan(row, col, 2);
			col++;
			table.getRowFormatter().setStyleName(row, (i%2==0)?AON.AON_CSS.aonOddBackground():AON.AON_CSS.aonEvenBackground());
			table.setWidget(row, col, new Label( AON.MSG.concept()));
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
			col++;
			table.getRowFormatter().setStyleName(row, (i%2==0)?AON.AON_CSS.aonOddBackground():AON.AON_CSS.aonEvenBackground());
			table.setWidget(row, col, new Label( AON.MSG.debit()));
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextRight());
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
			col++;
			table.getRowFormatter().setStyleName(row, (i%2==0)?AON.AON_CSS.aonOddBackground():AON.AON_CSS.aonEvenBackground());
			table.setWidget(row, col, new Label( AON.MSG.credit()));
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextRight());
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
			col++;
			table.setWidget(row, col, new Label( AON.MSG.balancingAccount()));
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextRight());
			row++;
			
			double sumDebit = 0;
			double sumCredit = 0;
			
			for ( AccountEntryDetail aed : ae.getDetails()) {
				table.getRowFormatter().setStyleName(row, (i%2==0)?AON.AON_CSS.aonOddBackground():AON.AON_CSS.aonEvenBackground());
				
				col = 0;
				table.setWidget(row, col, new Label( aed.getAccountCode()));
				col++;
				
				table.setWidget(row, col, new Label( aed.getAccountDescription()));
				col++;
				
				table.setWidget(row, col, new Label( aed.getConcept()));
				col++;

				if (AonMathUtils.isNotZero(aed.getDebit())) {
					table.setWidget(row, col, new Label( FMT.format( aed.getDebit() ) ) );
				}
				table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextRight());
				col++;
				
				if (AonMathUtils.isNotZero(aed.getCredit())) {
					table.setWidget(row, col, new Label( FMT.format( aed.getCredit() ) ) );
				}
				table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextRight());
				col++;

				table.setWidget(row, col, new Label( aed.getBalancingAccountCode()));
				table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextRight());
				col++;
				row++;
				sumDebit = AonMathUtils.round( sumDebit + aed.getDebit() );  
				sumCredit = AonMathUtils.round( sumCredit + aed.getCredit() );
			}
			col = 0;
			table.getRowFormatter().setStyleName(row, (i%2==0)?AON.AON_CSS.aonOddBackground():AON.AON_CSS.aonEvenBackground());
			table.setWidget(row, col, new Label());
			table.getFlexCellFormatter().setColSpan(row, col, 3);
			col++;
			table.getRowFormatter().setStyleName(row, (i%2==0)?AON.AON_CSS.aonOddBackground():AON.AON_CSS.aonEvenBackground());
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextRight());
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderTop());
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
			table.setWidget(row, col, new Label( FMT.format( sumDebit) ));
			col++;
			table.getRowFormatter().setStyleName(row, (i%2==0)?AON.AON_CSS.aonOddBackground():AON.AON_CSS.aonEvenBackground());
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextRight());
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderTop());
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
			table.setWidget(row, col, new Label( FMT.format( sumCredit) ));
			col++;
			table.setWidget(row, col, new Label());
			table.getFlexCellFormatter().setColSpan(row, col, 2);
			row++;
			++i;
		}
	}
	
}

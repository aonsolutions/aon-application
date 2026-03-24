package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.AccountingReportService;
import com.esferalia.aon.gwt.fiscal.client.AccountingReportServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.AccountingReportServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountingReportModuleOptions;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.type.AccountStatementPeriod;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;


public class StatementPanel extends ScrollPanel implements HasAccountEntrySelectionHandlers {

	private static final AccountingReportServiceAsync SERVICE;
	static {
		AccountingReportServiceAsync serviceRaw = GWT.create(AccountingReportService.class);
		SERVICE = new AccountingReportServiceAsyncDecorator(serviceRaw);
	}

	private FlowPanel root;
	private final boolean allowChecks;
	private FlowPanel checksToolbar = new FlowPanel();
	private FlowPanel lastPanel = new FlowPanel();
	private AccountStatementReport result;
	private FlexTable tab;
	private HashMap<String, LinkedList<Integer>> docMap;
	private LinkedList<Integer> tempList;


	public StatementPanel(final AccountingReportModuleOptions options, AccountingReportParams params) {
		this(options, params, true);
	}

	public StatementPanel(final AccountingReportModuleOptions options, AccountingReportParams params, boolean allowChecks) {
		
		// Solo tiene sentido mostrar la operativa de cuadrar a cuentas de clientes, proveedores o acreedores.
		this.allowChecks = allowChecks 
			&& params != null 
			&& params.getAccount() != null 
			&& AonStringUtils.startsWith(params.getAccount().getCode(), "4");
		
		root = new FlowPanel();
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginBottom());
		setWidget(root);
		
		if (params == null
			|| params.getAccount() == null
			|| (params.getAccount().getId() == null && AonStringUtils.isBlank(params.getAccount().getCode()))) {
			showError("Indique una cuenta contable.");
		} else {
			search(options, params);
			scrollToTop();
		}
	}

	private void showError(String msg) {
		final FlowPanel msgPanel = new FlowPanel("pre");
		InlineLabel accountLabel = new InlineLabel(msg);
		accountLabel.addStyleName(AON.CSS.aonBold());
		msgPanel.add(accountLabel);
		root.add(msgPanel);
	}

	private void search(final AccountingReportModuleOptions options, AccountingReportParams params) {
		SERVICE.getAccountStatement(options.getOccam(), params, new AsyncCallback<AccountStatementReport>() {

			@Override
			public void onSuccess(final AccountStatementReport result) {
				paintResult(options, params, result);
			}


			@Override
			public void onFailure(Throwable caught) {
				showError(caught.getMessage());
			}
		});
	}

	private void paintResult(AccountingReportModuleOptions options, AccountingReportParams params, AccountStatementReport result) {
		root.clear();
		lastPanel.clear();
		checksToolbar.clear();
		
		this.result = result;
		this.tab = new FlexTable();
		this.docMap = new HashMap<>();
		this.tempList = new LinkedList<>();
		tab.addStyleName(AON.CSS.aonGrid());

		tab.getColumnFormatter().setWidth( 0, "20px");
		tab.getColumnFormatter().setWidth( 1, "50px");
		tab.getColumnFormatter().setWidth( 2, "70px");
		tab.getColumnFormatter().setWidth( 3, "auto");
		tab.getColumnFormatter().setWidth( 4, "100px");
		tab.getColumnFormatter().setWidth( 5, "100px");
		tab.getColumnFormatter().setWidth( 6, "100px");
		tab.getColumnFormatter().setWidth( 7, "100px");
		tab.getColumnFormatter().setWidth( 8, "200px");
		tab.getColumnFormatter().setWidth( 9, "175px");

		int row = 0;
		InlineLabel accountLabel = new InlineLabel(result.getAccount().getFullName());
		accountLabel.addStyleName(AON.CSS.aonBold());
		accountLabel.addStyleName(AON.CSS.aonFontLarger());
		tab.setWidget(row, 0, accountLabel);
		tab.getFlexCellFormatter().setColSpan(row, 0, 10);
		row++;

		row = buildSummaryRows(row);

		Label emptyLabel = new Label();
		emptyLabel.addStyleName(AON.CSS.aonMarginTop());
		tab.setWidget(row, 0, emptyLabel);
		tab.getFlexCellFormatter().setColSpan(row, 0, 10);
		row++;

		String[] columns = new String[]{"","DIARIO","FECHA","CONCEPTO","DEBE","HABER","S.DEUDOR","S.ACREEDOR","CONTRAPARTIDA","NUM.DOCUMENTO"};
		for (int col = 0; col < columns.length; col++) {
			tab.setWidget(row, col, new Label(columns[col]));
			tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonGridHeader());
		}
		row++;

		if (result.getDetails().isEmpty()) {
			tab.setWidget(row, 0, new Label(AON.MSG.noData()));
			tab.getFlexCellFormatter().setColSpan(row, 0, 11);
		} else {
			if (allowChecks) {
				root.add(checksToolbar);
			}
			
			final int rowOffset = row;
			buildDetailRows(options, params, rowOffset);
			
			if (allowChecks && result.rendered()) {
				fillChecksToolbar(rowOffset);
			}
			if (!result.rendered()) {
				lastPanel.add( 
					new Label("S\u00F3lo se muestran los " + result.getOffset() + " primeros apuntes. "
						+ "Aplique filtros para buscar lo restante."));
				lastPanel.setStyleName(AON.CSS.aonTextCenter());
				lastPanel.addStyleName(AON.CSS.aonPaddingTop());
			}
		}
		root.add(tab);
		root.add(lastPanel);
	}
	
	private int buildSummaryRows(int row) {
		for (AccountStatement as : result.getSummary()) {
			tab.setWidget(row, 1, new Label(as.getConcept()));
			tab.getFlexCellFormatter().setColSpan(row, 1, 3);

			tab.setWidget(row, 2, new Label(AonMathUtils.isZero(as.getDebit()) ? AonStringUtils.SPACE : AON.FMT.format(as.getDebit())));
			tab.getCellFormatter().addStyleName(row, 2, AON.CSS.aonTextRight());

			tab.setWidget(row, 3, new Label(AonMathUtils.isZero(as.getCredit()) ? AonStringUtils.SPACE : AON.FMT.format(as.getCredit())));
			tab.getCellFormatter().addStyleName(row, 3, AON.CSS.aonTextRight());

			tab.setWidget(row, 4, new Label(AonMathUtils.isZero(as.getDebitBalance()) ? AonStringUtils.SPACE : AON.FMT.format(as.getDebitBalance())));
			tab.getCellFormatter().addStyleName(row, 4, AON.CSS.aonTextRight());

			tab.setWidget(row, 5, new Label(AonMathUtils.isZero(as.getUnpaidBalance()) ? AonStringUtils.SPACE : AON.FMT.format(as.getUnpaidBalance())));
			tab.getCellFormatter().addStyleName(row, 5, AON.CSS.aonTextRight());
			tab.getCellFormatter().addStyleName(row, 5, AON.CSS.aonPaddingRight());

			if (as.getPeriod() == AccountStatementPeriod.IN_PERIOD) {
				tab.getCellFormatter().addStyleName(row, 1, AON.CSS.aonColorBlue());
				tab.getCellFormatter().addStyleName(row, 2, AON.CSS.aonColorBlue());
				tab.getCellFormatter().addStyleName(row, 3, AON.CSS.aonColorBlue());
				tab.getCellFormatter().addStyleName(row, 4, AON.CSS.aonColorBlue());
				tab.getCellFormatter().addStyleName(row, 5, AON.CSS.aonColorBlue());
			}
			tab.setWidget(row, 6, new Label());
			tab.getFlexCellFormatter().setColSpan(row, 6, 2);
			row++;
		}
		return row;
	}

	private void fillChecksToolbar(int rowOffset) {
		checksToolbar.setStyleName(AON.CSS.aonTextRight());
		checksToolbar.addStyleName(AON.CSS.aonMarginBottom());
		checksToolbar.addStyleName(AON.CSS.aonPadding());
		checksToolbar.addStyleName(AON.CSS.aonBorder());

		AonTableButton cleanSelected = new AonTableButton("Limpiar", AON.CSS.aonIconClear());
		cleanSelected.addStyleName(AON.CSS.aonMarginLeftSep());
		AonTableButton authBalanced = new AonTableButton("Cuadrar por n\u00BA de documento", AON.CSS.aonIconWizard());
		authBalanced.addStyleName(AON.CSS.aonMarginLeftSep());
		AonTableButton hideBalanced = new AonTableButton("Ocultar los que cuadran", AON.CSS.aonIconMinus());
		hideBalanced.addStyleName(AON.CSS.aonMarginLeftSep());
		AonTableButton showBalanced = new AonTableButton("Mostrar los ocultados", AON.CSS.aonIconAdd());
		showBalanced.addStyleName(AON.CSS.aonMarginLeftSep());

		authBalanced.addClickHandler(event -> {
			HashMap<String, LinkedList<Integer>> docMapDup = new HashMap<>();
			tempList.clear();
			docMap.clear();
			int rw = rowOffset;
			for (final AccountStatement as : result.getDetails()) {
				String doc = as.getDocumentNumber();
				if (!docMapDup.containsKey(doc)) {
					docMapDup.put(doc, new LinkedList<>());
				}
				docMapDup.get(doc).add(rw);
				rw++;
			}
			for (String d : docMapDup.keySet()) {
				if (isSettled(docMapDup.get(d), rowOffset)) {
					docMap.put(d, docMapDup.get(d));
				}
			}
			decorateTable(rowOffset);
		});
		checksToolbar.add(authBalanced);

		hideBalanced.addClickHandler(event -> {
			int rows = 0;
			int rw = 0;
			for (final AccountStatement as : result.getDetails()) {
				if (as.isBalanced()) {
					tab.getRowFormatter().addStyleName(rw + rowOffset, AON.CSS.aonDisplayNone());
					rows++;
				}
				rw++;
			}
			showBalanced.setTitle("Mostrar los ocultados " + (rows > 0 ? "(" + rows + ")" : ""));
			showBalanced.setVisible(rows > 0);
		});
		checksToolbar.add(hideBalanced);

		showBalanced.setVisible(false);
		showBalanced.addClickHandler(event -> {
			for (int rw = 0; rw < result.getDetails().size(); rw++) {
				tab.getRowFormatter().removeStyleName(rw + rowOffset, AON.CSS.aonDisplayNone());
			}
			showBalanced.setTitle("Mostrar los ocultados");
			showBalanced.setVisible(false);
		});
		checksToolbar.add(showBalanced);

		cleanSelected.addClickHandler(event -> {
			tempList.clear();
			docMap.clear();
			int rw = 0;
			for (final AccountStatement as : result.getDetails()) {
				as.setSelected(false);
				as.setBalanced(false);
				Widget check = tab.getWidget(rw + rowOffset, 0);
				if (check != null) {
					check.removeStyleName(AON.CSS.aonIconChecked());
					check.addStyleName(AON.CSS.aonIconCheck());
				}
				tab.getRowFormatter().removeStyleName(rw + rowOffset, AON.CSS.aonDisplayNone());
				tab.getRowFormatter().removeStyleName(rw + rowOffset, AON.CSS.aonBackgroundHighlightedGreen());
				tab.getRowFormatter().removeStyleName(rw + rowOffset, AON.CSS.aonBackgroundHighlightedOrange());
				rw++;
			}
			showBalanced.setVisible(false);
		});
		checksToolbar.add(cleanSelected);
	}

	private void buildDetailRows(AccountingReportModuleOptions options, AccountingReportParams params, int rowOffset) {
		for (final AccountStatement as : result.getDetails()) {
			String document = AonStringUtils.defaultString(as.getDocumentNumber());
			final int currentRow = tab.getRowCount();

			if (allowChecks && result.rendered()) {
				final AonTableButton check = new AonTableButton(AON.MSG.check(), AON.CSS.aonIconCheck());
				check.addClickHandler(event -> {
					boolean selected = !as.isSelected();
					check.setTitle(selected ? AON.MSG.uncheck() : AON.MSG.check());
					as.setSelected(selected);
					toggleStyle(check, selected);
					if (selected) {
						tempList.add(currentRow);
					} else {
						tab.getRowFormatter().removeStyleName(currentRow, AON.CSS.aonBackgroundHighlightedGreen());
						tab.getRowFormatter().removeStyleName(currentRow, AON.CSS.aonBackgroundHighlightedOrange());
						tempList.remove(Integer.valueOf(currentRow));
						if (docMap.containsKey(document)) {
							docMap.get(document).remove(Integer.valueOf(currentRow));
						}
					}
					decorateTable(rowOffset);
				});
				tab.setWidget(currentRow, 0, check);
			}

			Label journalLabel = new Label(AonStringUtils.defaultString(AonNumberUtils.toString(as.getJournal())));
			journalLabel.addStyleName(AON.CSS.aonClickableLabel());
			journalLabel.setTitle(AON.MSG.goAction());
			journalLabel.addClickHandler(event -> {
				AccountEntry ae = new AccountEntry()
						.setId(as.getAccountEntry())
						.setDomain(options.getDomain());
				AccountEntrySelectionEvent.fire(StatementPanel.this, ae, new ModuleCallback() {

					@Override
					public void onRemove(IAccountEntryWrapper removed) {
						int scrollPosition = StatementPanel.this.getVerticalScrollPosition();
						search(options, params);
						StatementPanel.this.setVerticalScrollPosition(scrollPosition);
					}

					@Override
					public void onFailure(Throwable caught) {
						// No se hace nada,
					}

					@Override
					public void onExit() {
						// No se hace nada,
					}

					@Override
					public void onChange(IAccountEntryWrapper changed) {
						int scrollPosition = StatementPanel.this.getVerticalScrollPosition();
						search(options, params);
						StatementPanel.this.setVerticalScrollPosition(scrollPosition);
					}
				});
			});
			tab.setWidget(currentRow, 1, journalLabel);
			tab.getCellFormatter().addStyleName(currentRow, 1, AON.CSS.aonTextRight());

			tab.setWidget(currentRow, 2, new Label(AON.DATE_FORMAT.format(as.getEntryDate())));
			Label conceptLabel = new Label(as.getConcept());
			conceptLabel.setStyleName(AON.CSS.aonNowrap());
			tab.setWidget(currentRow, 3, conceptLabel);
			tab.setWidget(currentRow, 4, new Label(AonMathUtils.isZero(as.getDebit()) ? AonStringUtils.SPACE : AON.FMT.format(as.getDebit())));
			tab.getCellFormatter().addStyleName(currentRow, 4, AON.CSS.aonTextRight());
			tab.setWidget(currentRow, 5, new Label(AonMathUtils.isZero(as.getCredit()) ? AonStringUtils.SPACE : AON.FMT.format(as.getCredit())));
			tab.getCellFormatter().addStyleName(currentRow, 5, AON.CSS.aonTextRight());
			tab.setWidget(currentRow, 6, new Label(AonMathUtils.isZero(as.getDebitBalance()) ? AonStringUtils.SPACE : AON.FMT.format(as.getDebitBalance())));
			tab.getCellFormatter().addStyleName(currentRow, 6, AON.CSS.aonTextRight());
			tab.setWidget(currentRow, 7, new Label(AonMathUtils.isZero(as.getUnpaidBalance()) ? AonStringUtils.SPACE : AON.FMT.format(as.getUnpaidBalance())));
			tab.getCellFormatter().addStyleName(currentRow, 7, AON.CSS.aonTextRight());
			tab.getCellFormatter().addStyleName(currentRow, 7, AON.CSS.aonPaddingRight());

			Label balancingAccountLabel = new Label(AonStringUtils.defaultString(as.getBalancingAccountCode())
					+ " " + AonStringUtils.defaultString(AonStringUtils.abbreviate(as.getBalancingAccountDescription(), 11)));
			balancingAccountLabel.setStyleName(AON.CSS.aonNowrap());
			balancingAccountLabel.setTitle(as.getBalancingAccountDescription());
			tab.setWidget(currentRow, 8, balancingAccountLabel);

			Label documentLabel = new Label(document);
			tab.setWidget(currentRow, 9, documentLabel);
			documentLabel.setStyleName(AON.CSS.aonNowrap());
			if (allowChecks) {
				documentLabel.setTitle("Seleccionar apuntes con este n\u00BA documento");
				documentLabel.addStyleName(AON.CSS.aonClickableLabel());
				documentLabel.addClickHandler(event -> {
					boolean selected = !as.isSelected();
					as.setSelected(selected);
					if (selected) {
						docMap.put(document, new LinkedList<>());
						int rw = rowOffset;
						for (final AccountStatement as2 : result.getDetails()) {
							if (AonStringUtils.equals(as.getDocumentNumber(), as2.getDocumentNumber())) {
								toggleStyle(tab.getWidget(rw, 0), selected);
								docMap.get(document).add(rw);
								as2.setSelected(selected);
							}
							rw++;
						}
					} else {
						if (docMap.containsKey(document)) {
							for (Integer rw : docMap.get(document)) {
								toggleStyle(tab.getWidget(rw, 0), selected);
								tab.getRowFormatter().removeStyleName(rw, AON.CSS.aonBackgroundHighlightedGreen());
								tab.getRowFormatter().removeStyleName(rw, AON.CSS.aonBackgroundHighlightedOrange());
							}
							docMap.remove(document);
						}
					}
					decorateTable(rowOffset);
				});
			}
			result.incrementOffset();
			if ( result.getOffset() >= result.getLimit()) {
				break;
			}
		}
	}

	private void decorateTable(int rowOffset) {
		for (LinkedList<Integer> list : docMap.values()) {
			decorateTable(list, rowOffset);
		}
		decorateTable(tempList, rowOffset);
	}

	private boolean isSettled(LinkedList<Integer> list, int rowOffset) {
		double sumDebit = 0;
		double sumCredit = 0;
		for (Integer row : list) {
			int rw = row - rowOffset;
			AccountStatement as = result.getDetails().get(rw);
			sumDebit = AonMathUtils.round(sumDebit + as.getDebit());
			sumCredit = AonMathUtils.round(sumCredit + as.getCredit());
		}
		return AonMathUtils.isZero(sumDebit - sumCredit);
	}

	private void decorateTable(LinkedList<Integer> list, int rowOffset) {
		boolean balanced = isSettled(list, rowOffset);
		String highlightStyle = balanced
				? AON.CSS.aonBackgroundHighlightedGreen()
				: AON.CSS.aonBackgroundHighlightedOrange();
		for (Integer row : list) {
			tab.getRowFormatter().removeStyleName(row, AON.CSS.aonBackgroundHighlightedGreen());
			tab.getRowFormatter().removeStyleName(row, AON.CSS.aonBackgroundHighlightedOrange());
			tab.getRowFormatter().addStyleName(row, highlightStyle);
			result.getDetails().get(row - rowOffset).setBalanced(balanced);
		}
	}

	private void toggleStyle(Widget widget, boolean selected) {
		if (selected) {
			widget.addStyleName(AON.CSS.aonIconChecked());
			widget.removeStyleName(AON.CSS.aonIconCheck());
		} else {
			widget.addStyleName(AON.CSS.aonIconCheck());
			widget.removeStyleName(AON.CSS.aonIconChecked());
		}
	}

	@Override
	public HandlerRegistration addSelectionHandler(AccountEntrySelectionHandler handler) {
		return super.addHandler(handler, AccountEntrySelectionEvent.getType());
	}
}

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
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;


public class StatementPanelNEW extends ScrollPanel implements HasAccountEntrySelectionHandlers {

    private static AccountingReportServiceAsync SERVICE;

    private FlowPanel root;
    private int rowOffset;
    private AccountingReportParams params;
    private AccountStatementReport result;
    private boolean allowChecks;

    public StatementPanelNEW(final AccountingReportModuleOptions options, AccountingReportParams params) {
        this(options, params, true);
    }

    public StatementPanelNEW(final AccountingReportModuleOptions options, AccountingReportParams params, boolean allowChecks) {
        this.params = params;
        this.allowChecks = allowChecks;
        root = new FlowPanel();
        setWidget(root);
        addStyleName(AON.CSS.aonScrollArea());
        addStyleName(AON.CSS.aonMarginBottom());
        if (params == null
                || params.getAccount() == null
                || (params.getAccount().getId() == null && AonStringUtils.isBlank(params.getAccount().getCode()))) {
            showError("Indique una cuenta contable.");
        } else {
            search(options, allowChecks);
            scrollToTop();
        }
    }

    // -------------------------------------------------------------------------
    // Search & render
    // -------------------------------------------------------------------------

    private void search(final AccountingReportModuleOptions options, boolean allowChecks) {
        root.clear();
        AccountingReportServiceAsync serviceRaw = GWT.create(AccountingReportService.class);
        SERVICE = new AccountingReportServiceAsyncDecorator(serviceRaw);
        SERVICE.getAccountStatement(options.getOccam(), params, new AsyncCallback<AccountStatementReport>() {
            @Override
            public void onSuccess(final AccountStatementReport r) {
                result = r;
                FlexTable tab = buildTable();
                int row = paintTableHeader(tab);
                if (result.getDetails().isEmpty()) {
                    tab.setWidget(row, 0, new Label(AON.MSG.noData()));
                    tab.getFlexCellFormatter().setColSpan(row, 0, 11);
                } else {
                    rowOffset = row;
                    HashMap<String, LinkedList<Integer>> docMap = new HashMap<>();
                    HashMap<String, LinkedList<Integer>> docIndexMap = new HashMap<>();
                    if (allowChecks) {
                        root.add(buildChecksToolbar(tab, docMap, docIndexMap));
                    }
                    scheduleRenderRows(tab, options, allowChecks, docMap, docIndexMap, row);
                }
                root.add(tab);
            }

            @Override
            public void onFailure(Throwable caught) {
                showError(caught.getMessage());
            }
        });
    }

    private FlexTable buildTable() {
        FlexTable tab = new FlexTable();
        tab.addStyleName(AON.CSS.aonGrid());
        tab.getElement().getStyle().setProperty("tableLayout", "fixed");
        tab.getColumnFormatter().setWidth(0, "20px");
        tab.getColumnFormatter().setWidth(1, "50px");
        tab.getColumnFormatter().setWidth(2, "70px");
        tab.getColumnFormatter().setWidth(3, "auto");
        tab.getColumnFormatter().setWidth(4, "100px");
        tab.getColumnFormatter().setWidth(5, "100px");
        tab.getColumnFormatter().setWidth(6, "100px");
        tab.getColumnFormatter().setWidth(7, "100px");
        tab.getColumnFormatter().setWidth(8, "200px");
        tab.getColumnFormatter().setWidth(9, "175px");
        return tab;
    }

    private int paintTableHeader(FlexTable tab) {
        int row = 0;

        InlineLabel accountLabel = new InlineLabel(result.getAccount().getFullName());
        accountLabel.addStyleName(AON.CSS.aonBold());
        accountLabel.addStyleName(AON.CSS.aonFontLarger());
        tab.setWidget(row, 0, accountLabel);
        tab.getFlexCellFormatter().setColSpan(row, 0, 10);
        row++;

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

        Label emptyLabel = new Label();
        emptyLabel.addStyleName(AON.CSS.aonMarginTop());
        tab.setWidget(row, 0, emptyLabel);
        tab.getFlexCellFormatter().setColSpan(row, 0, 10);
        row++;

        String[] columns = new String[]{"", "DIARIO", "FECHA", "CONCEPTO", "DEBE", "HABER", "SALDO DEUDOR", "SALDO ACREEDOR", "CONTRAPARTIDA", "NUM.DOCUMENTO"};
        for (int col = 0; col < columns.length; col++) {
            tab.setWidget(row, col, new Label(columns[col]));
            tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonGridHeader());
        }
        row++;
        return row;
    }

    private void scheduleRenderRows(FlexTable tab, AccountingReportModuleOptions options, boolean allowChecks,
                                    HashMap<String, LinkedList<Integer>> docMap,
                                    HashMap<String, LinkedList<Integer>> docIndexMap, int startRow) {
        LinkedList<AccountStatement> details = result.getDetails();
        int[] index = {0};
        int batchSize = 50;
        MutableInt mutRow = new MutableInt(startRow);

        Scheduler.get().scheduleIncremental(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                int end = Math.min(index[0] + batchSize, details.size());
                for (int i = index[0]; i < end; i++) {
                    paintDetailRow(details.get(i), tab, options, allowChecks, docMap, docIndexMap, mutRow);
                }
                index[0] = end;
                return index[0] < details.size();
            }
        });
    }

    private void paintDetailRow(AccountStatement as, FlexTable tab,
                                 AccountingReportModuleOptions options, boolean allowChecks,
                                 HashMap<String, LinkedList<Integer>> docMap,
                                 HashMap<String, LinkedList<Integer>> docIndexMap,
                                 MutableInt mutRow) {
        String document = AonStringUtils.defaultString(as.getDocumentNumber());
        final int currentRow = mutRow.getValue();

        if (allowChecks) {
            docIndexMap.computeIfAbsent(document, k -> new LinkedList<>()).add(currentRow);
            tab.setWidget(currentRow, 0, buildCheckButton(as, document, currentRow, tab, docMap));
        }

        tab.setWidget(currentRow, 1, buildJournalLabel(as, options));
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

        tab.setWidget(currentRow, 9, buildDocumentLabel(as, document, tab, options, allowChecks, docMap, docIndexMap));
        mutRow.increment();
    }

    // -------------------------------------------------------------------------
    // Widget builders
    // -------------------------------------------------------------------------

    private AonTableButton buildCheckButton(AccountStatement as, String document, int currentRow,
                                             FlexTable tab, HashMap<String, LinkedList<Integer>> docMap) {
        AonTableButton check = new AonTableButton(AON.MSG.check(), AON.CSS.aonIconCheck());
        check.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                boolean selected = !as.isSelected();
                as.setSelected(selected);
                check.setTitle(selected ? AON.MSG.uncheck() : AON.MSG.check());
                toogleStyle(check, selected);
                if (selected) {
                    docMap.computeIfAbsent(document, k -> new LinkedList<>()).add(currentRow);
                } else {
                    clearRowHighlight(tab, currentRow);
                    removeFromDocMap(docMap, document, currentRow);
                }
                decorateTable(tab, docMap);
            }
        });
        return check;
    }

    private Label buildDocumentLabel(AccountStatement as, String document,
                                      FlexTable tab, AccountingReportModuleOptions options,
                                      boolean allowChecks, HashMap<String, LinkedList<Integer>> docMap,
                                      HashMap<String, LinkedList<Integer>> docIndexMap) {
        Label documentLabel = new Label(document);
        documentLabel.setStyleName(AON.CSS.aonNowrap());
        documentLabel.setTitle("Seleccionar apuntes con este nº documento");
        if (allowChecks) {
            documentLabel.setStyleName(AON.CSS.aonClickableLabel());
            documentLabel.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    boolean selected = !as.isSelected();
                    as.setSelected(selected);
                    if (selected) {
                        selectDocumentGroup(document, tab, docMap, docIndexMap);
                    } else {
                        deselectDocumentGroup(document, tab, docMap);
                    }
                    decorateTable(tab, docMap);
                }
            });
        }
        return documentLabel;
    }

    private Label buildJournalLabel(AccountStatement as, AccountingReportModuleOptions options) {
        Label journalLabel = new Label(AonStringUtils.defaultString(AonNumberUtils.toString(as.getJournal())));
        journalLabel.addStyleName(AON.CSS.aonClickableLabel());
        journalLabel.setTitle(AON.MSG.goAction());
        journalLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                AccountEntry ae = new AccountEntry()
                        .setId(as.getAccountEntry())
                        .setDomain(options.getDomain());
                AccountEntrySelectionEvent.fire(StatementPanelNEW.this, ae, new ModuleCallback() {
                    @Override
                    public void onRemove(IAccountEntryWrapper removed) {
                        refreshPanel(options);
                    }
                    @Override
                    public void onFailure(Throwable caught) {}
                    @Override
                    public void onExit() {}
                    @Override
                    public void onChange(IAccountEntryWrapper changed) {
                        refreshPanel(options);
                    }
                });
            }
        });
        return journalLabel;
    }

    private FlowPanel buildChecksToolbar(FlexTable tab,
                                          HashMap<String, LinkedList<Integer>> docMap,
                                          HashMap<String, LinkedList<Integer>> docIndexMap) {
        FlowPanel toolbar = new FlowPanel();
        toolbar.setStyleName(AON.CSS.aonTextRight());
        toolbar.addStyleName(AON.CSS.aonMarginBottom());
        toolbar.addStyleName(AON.CSS.aonPadding());
        toolbar.addStyleName(AON.CSS.aonBorder());

        AonTableButton cleanSelected = new AonTableButton("Limpiar", AON.CSS.aonIconClear());
        cleanSelected.addStyleName(AON.CSS.aonMarginLeftSep());
        AonTableButton authBalanced = new AonTableButton("Cuadrar por n\u00BA de documento", AON.CSS.aonIconWizard());
        authBalanced.addStyleName(AON.CSS.aonMarginLeftSep());
        AonTableButton hideBalanced = new AonTableButton("Ocultar los que cuadran", AON.CSS.aonIconMinus());
        hideBalanced.addStyleName(AON.CSS.aonMarginLeftSep());
        AonTableButton showBalanced = new AonTableButton("Mostrar los ocultados", AON.CSS.aonIconAdd());
        showBalanced.addStyleName(AON.CSS.aonMarginLeftSep());
        showBalanced.setVisible(false);

        authBalanced.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                docMap.clear();
                for (String doc : docIndexMap.keySet()) {
                    if (isSettled(docIndexMap.get(doc))) {
                        docMap.put(doc, new LinkedList<>(docIndexMap.get(doc)));
                    }
                }
                decorateTable(tab, docMap);
            }
        });

        hideBalanced.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                int rows = 0;
                for (LinkedList<Integer> list : docMap.values()) {
                    if (isSettled(list)) {
                        for (Integer rw : list) {
                            tab.getRowFormatter().addStyleName(rw, AON.CSS.aonDisplayNone());
                            rows++;
                        }
                    }
                }
                showBalanced.setTitle("Mostrar los ocultados " + (rows > 0 ? "(" + rows + ")" : ""));
                showBalanced.setVisible(rows > 0);
            }
        });

        showBalanced.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                for (LinkedList<Integer> list : docMap.values()) {
                    for (Integer rw : list) {
                        tab.getRowFormatter().removeStyleName(rw, AON.CSS.aonDisplayNone());
                    }
                }
                showBalanced.setTitle("Mostrar los ocultados");
                showBalanced.setVisible(false);
            }
        });

        cleanSelected.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                for (LinkedList<Integer> rows : docMap.values()) {
                    for (Integer rw : rows) {
                        AccountStatement as = result.getDetails().get(rw - rowOffset);
                        as.setSelected(false);
                        as.setBalanced(false);
                        toogleStyle(tab.getWidget(rw, 0), false);
                        tab.getRowFormatter().removeStyleName(rw, AON.CSS.aonDisplayNone());
                        clearRowHighlight(tab, rw);
                    }
                }
                docMap.clear();
                showBalanced.setVisible(false);
            }
        });

        toolbar.add(authBalanced);
        toolbar.add(hideBalanced);
        toolbar.add(showBalanced);
        toolbar.add(cleanSelected);
        return toolbar;
    }

    // -------------------------------------------------------------------------
    // Document group selection
    // -------------------------------------------------------------------------

    private void selectDocumentGroup(String document, FlexTable tab,
                                      HashMap<String, LinkedList<Integer>> docMap,
                                      HashMap<String, LinkedList<Integer>> docIndexMap) {
        LinkedList<Integer> rows = docIndexMap.get(document);
        if (rows != null) {
            docMap.put(document, new LinkedList<>(rows));
            for (Integer rw : docMap.get(document)) {
                toogleStyle(tab.getWidget(rw, 0), true);
                result.getDetails().get(rw - rowOffset).setSelected(true);
            }
        }
    }

    private void deselectDocumentGroup(String document, FlexTable tab,
                                        HashMap<String, LinkedList<Integer>> docMap) {
        if (docMap.containsKey(document)) {
            for (Integer rw : docMap.get(document)) {
                toogleStyle(tab.getWidget(rw, 0), false);
                clearRowHighlight(tab, rw);
                result.getDetails().get(rw - rowOffset).setSelected(false);
            }
            docMap.remove(document);
        }
    }

    private void removeFromDocMap(HashMap<String, LinkedList<Integer>> docMap, String document, int row) {
        if (docMap.containsKey(document)) {
            docMap.get(document).remove(Integer.valueOf(row));
            if (docMap.get(document).isEmpty()) {
                docMap.remove(document);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Decoration
    // -------------------------------------------------------------------------

    private void decorateTable(FlexTable tab, HashMap<String, LinkedList<Integer>> docMap) {
        for (LinkedList<Integer> list : docMap.values()) {
            decorateGroup(tab, list);
        }
    }

    private void decorateGroup(FlexTable tab, LinkedList<Integer> list) {
        boolean balanced = isSettled(list);
        String addStyle    = balanced ? AON.CSS.aonBackgroundHighlightedGreen() : AON.CSS.aonBackgroundHighlightedOrange();
        String removeStyle = balanced ? AON.CSS.aonBackgroundHighlightedOrange() : AON.CSS.aonBackgroundHighlightedGreen();
        for (Integer row : list) {
            tab.getRowFormatter().addStyleName(row, addStyle);
            tab.getRowFormatter().removeStyleName(row, removeStyle);
            result.getDetails().get(row - rowOffset).setBalanced(balanced);
        }
    }

    private boolean isSettled(LinkedList<Integer> list) {
        double sumDebit = 0;
        double sumCredit = 0;
        for (Integer row : list) {
            AccountStatement as = result.getDetails().get(row - rowOffset);
            sumDebit = AonMathUtils.round(sumDebit + as.getDebit());
            sumCredit = AonMathUtils.round(sumCredit + as.getCredit());
        }
        return AonMathUtils.isZero(sumDebit - sumCredit);
    }

    private void clearRowHighlight(FlexTable tab, int row) {
        tab.getRowFormatter().removeStyleName(row, AON.CSS.aonBackgroundHighlightedGreen());
        tab.getRowFormatter().removeStyleName(row, AON.CSS.aonBackgroundHighlightedOrange());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void refreshPanel(AccountingReportModuleOptions options) {
        int scrollPosition = StatementPanelNEW.this.getVerticalScrollPosition();
        search(options, allowChecks);
        StatementPanelNEW.this.setVerticalScrollPosition(scrollPosition);
    }

    private void showError(String msg) {
        final FlowPanel msgPanel = new FlowPanel("pre");
        InlineLabel accountLabel = new InlineLabel(msg);
        accountLabel.addStyleName(AON.CSS.aonBold());
        msgPanel.add(accountLabel);
        root.add(msgPanel);
    }

    private void toogleStyle(Widget widget, boolean selected) {
        if (widget == null) return;
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
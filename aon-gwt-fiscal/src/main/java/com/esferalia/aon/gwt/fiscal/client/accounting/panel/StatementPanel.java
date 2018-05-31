package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementParams;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.type.AccountStatementPeriod;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;


public class StatementPanel extends ScrollPanel implements HasAccountEntrySelectionHandlers{

	private static FiscalServiceAsync fiscalService;
	
	private FlowPanel root;
	private int rowOffset; 
	
	private String currentDomainName;
	private String currentUser;
	private Integer currentDomainId;
	private AccountStatementParams params;
	
	public StatementPanel(String currentDomainName, String currentUser, Integer currentDomainId, AccountStatementParams params) {
		this(currentDomainName, currentUser, currentDomainId, params, true);
	}
	
	public StatementPanel(String currentDomainName, String currentUser, Integer currentDomainId, AccountStatementParams params, boolean allowChecks) {
		this.currentDomainName = currentDomainName;
		this.currentUser = currentUser;
		this.currentDomainId = currentDomainId;
		this.params = params;

		root = new FlowPanel();
		setWidget(root);
		addStyleName(AON.AON_CSS.aonScrollArea());
		addStyleName(AON.AON_CSS.aonMarginBottom());		
		if (params == null || (params.getAccount() == null && params.getFullAccount() == null)) {
			final FlowPanel msgPanel = new FlowPanel("pre");
			InlineLabel accountLabel = new InlineLabel("Indique una cuenta contable.");
			accountLabel.addStyleName(AON.AON_CSS.aonBold());
			msgPanel.add(accountLabel);
			root.add(msgPanel);	
		} else {
			search(allowChecks);
			scrollToTop();
		}
	}

	private void search(boolean allowChecks) {
		root.clear();
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		
		fiscalService.getAccountStatement(currentDomainName,currentUser,currentDomainId,params
				,  new AsyncCallback<AccountStatementReport>() {
			
			@Override
			public void onSuccess(final AccountStatementReport result) {
				FlexTable tab = new FlexTable();
				tab.addStyleName(AON.AON_CSS.aonReportTable());
				tab.addStyleName(AON.AON_CSS.aonReportTableFontMedium());

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
				accountLabel.addStyleName(AON.AON_CSS.aonBold());
				accountLabel.addStyleName(AON.AON_CSS.aonFontBig());
				tab.setWidget(row, 0, accountLabel);
				tab.getFlexCellFormatter().setColSpan(row, 0, 10);
				row++;

				for (AccountStatement as : result.getSummary()) {
					
					tab.setWidget(row, 1, new Label(as.getConcept()));
					tab.getFlexCellFormatter().setColSpan(row, 1, 3);
					
					tab.setWidget(row, 2, new Label(AonMathUtils.isZero(as.getDebit()) ?AonStringUtils.SPACE:AON.FMT.format(as.getDebit())));
					tab.getCellFormatter().addStyleName(row, 2,AON.AON_CSS.aonTextRight());
					
					tab.setWidget(row, 3, new Label(AonMathUtils.isZero(as.getCredit())?AonStringUtils.SPACE:AON.FMT.format(as.getCredit())));
					tab.getCellFormatter().addStyleName(row, 3,AON.AON_CSS.aonTextRight());
					
					tab.setWidget(row, 4, new Label(AonMathUtils.isZero(as.getDebitBalance())?AonStringUtils.SPACE:AON.FMT.format(as.getDebitBalance())));
					tab.getCellFormatter().addStyleName(row, 4,AON.AON_CSS.aonTextRight());
					
					tab.setWidget(row, 5, new Label(AonMathUtils.isZero(as.getUnpaidBalance())?AonStringUtils.SPACE:AON.FMT.format(as.getUnpaidBalance())));
					tab.getCellFormatter().addStyleName(row, 5,AON.AON_CSS.aonTextRight());
					
					tab.getCellFormatter().addStyleName(row, 5,AON.AON_CSS.aonPaddingRight());
					if (as.getPeriod() == AccountStatementPeriod.IN_PERIOD) {
						tab.getCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonColoRoyalblue());
						tab.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonColoRoyalblue());
						tab.getCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonColoRoyalblue());
						tab.getCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonColoRoyalblue());
						tab.getCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonColoRoyalblue());
					}
					tab.setWidget(row, 6, new Label());
					tab.getFlexCellFormatter().setColSpan(row, 6, 2);
					row++;
				}
				
				Label emptyLabel = new Label();
				emptyLabel.addStyleName(AON.AON_CSS.aonMarginTop());
				tab.setWidget(row, 0, emptyLabel);
				tab.getFlexCellFormatter().setColSpan(row, 0, 10);
				row++;
				
				String[] columns = new String[]{"","DIARIO","FECHA","CONCEPTO","DEBE","HABER","SALDO DEUDOR","SALDO ACREDOR","CONTRAPARTIDA","NUM.DOCUMENTO"};
				for (int col = 0; col <  columns.length; col++) {
					tab.setWidget(row, col, new Label(columns[col]));
					tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonReportTableHeader());
				}
				row++;
				
				if (result.getDetails().isEmpty()) {
					tab.setWidget(row, 0, new Label(AON.MSG.noData()));
					tab.getFlexCellFormatter().setColSpan(row, 0, 11);
				} else {
					rowOffset = row;
					
					HashMap<String, LinkedList<Integer>> docMap = new HashMap<String, LinkedList<Integer>>();
					LinkedList<Integer> tempList = new LinkedList<Integer>();
					
					if (allowChecks) {

						FlowPanel checksToolbar = new FlowPanel();
						checksToolbar.setStyleName(AON.AON_CSS.aonTextRight());
						checksToolbar.addStyleName(AON.AON_CSS.aonMarginBottom());
						checksToolbar.addStyleName(AON.AON_CSS.aonPadding());
						checksToolbar.addStyleName(AON.AON_CSS.aonSimpleBorder());
						
						Button cleanSelected = new Button("Limpiar");
						Button authBalanced = new Button("Cuadrar por n\u00BA de documento");
						Button hideBalanced = new Button("Ocultar los que cuadran");
						Button showBalanced = new Button("Mostrar los ocultados");
						
						authBalanced.addStyleName(AON.AON_CSS.aonIconCommandButton());
						authBalanced.addStyleName(AON.AON_CSS.aonIconWizard());
						authBalanced.addStyleName(AON.AON_CSS.aonSimpleBorder());
						authBalanced.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								HashMap<String, LinkedList<Integer>> docMapDup = new HashMap<String, LinkedList<Integer>>();
								tempList.clear();
								docMap.clear();
								int rw = rowOffset;
								for (final AccountStatement as : result.getDetails()) {
									String doc = as.getDocumentNumber();
									if (!docMapDup.containsKey(doc)) {
										docMapDup.put(doc, new LinkedList<Integer>());
									}
									docMapDup.get(doc).add(rw);
									rw++;
								}
								for (String d  : docMapDup.keySet()) {
									if (isSettled(result, docMapDup.get(d))) {
										docMap.put(d, docMapDup.get(d));
									}
								}
								decorateTable(result, tab, tempList, docMap);
							}
						});
						checksToolbar.add(authBalanced);
						
						
						hideBalanced.setStyleName(AON.AON_CSS.aonMarginLeft());
						hideBalanced.addStyleName(AON.AON_CSS.aonIconCommandButton());
						hideBalanced.addStyleName(AON.AON_CSS.aonIconMinus());
						hideBalanced.addStyleName(AON.AON_CSS.aonSimpleBorder());
						hideBalanced.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								int rows = 0;
								int rw = 0;
								for (final AccountStatement as : result.getDetails()) {
									if (as.isBalanced()) {
										tab.getRowFormatter().addStyleName(rw+rowOffset, AON.AON_CSS.aonDisplayNone());
										rows++;
									}
									rw++;
								}
								showBalanced.setText("Mostrar los ocultados " + (rows>0?"("+rows+")":""));
								showBalanced.setVisible(rows > 0);									
							}
						});
						checksToolbar.add(hideBalanced);
						
						showBalanced.setStyleName(AON.AON_CSS.aonMarginLeft());
						showBalanced.addStyleName(AON.AON_CSS.aonIconCommandButton());
						showBalanced.addStyleName(AON.AON_CSS.aonSimpleBorder());
						showBalanced.addStyleName(AON.AON_CSS.aonIconPlus());
						showBalanced.setVisible(false);
						showBalanced.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								for (int rw = 0; rw < result.getDetails().size(); rw++) {
									tab.getRowFormatter().removeStyleName(rw+rowOffset, AON.AON_CSS.aonDisplayNone());
								}
								showBalanced.setText("Mostrar los ocultados");
								showBalanced.setVisible(false);
							}
						});
						checksToolbar.add(showBalanced);
						
						cleanSelected.setStyleName(AON.AON_CSS.aonMarginLeft());
						cleanSelected.addStyleName(AON.AON_CSS.aonMarginRight());
						cleanSelected.addStyleName(AON.AON_CSS.aonIconCommandButton());
						cleanSelected.addStyleName(AON.AON_CSS.aonSimpleBorder());
						cleanSelected.addStyleName(AON.AON_CSS.aonIconDelete());
						cleanSelected.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								tempList.clear();
								docMap.clear();
								int rw = 0;
								for (final AccountStatement as : result.getDetails()) {
									as.setSelected(false);
									as.setBalanced(false);
									Widget check = tab.getWidget(rw+rowOffset, 0);
									if (check != null) {
										check.removeStyleName(AON.AON_CSS.aonIconChecked());
										check.addStyleName(AON.AON_CSS.aonIconCheck());
									}
									tab.getRowFormatter().removeStyleName(rw+rowOffset, AON.AON_CSS.aonDisplayNone());
									tab.getRowFormatter().removeStyleName(rw+rowOffset, AON.AON_CSS.aonBackgroundHighlightedGreen());
									tab.getRowFormatter().removeStyleName(rw+rowOffset, AON.AON_CSS.aonBackgroundHighlightedOrange());
									rw++;
								}
								showBalanced.setText("Mostrar los ocultados");
								showBalanced.setVisible(false);
							}
						});
						checksToolbar.add(cleanSelected);

						root.add(checksToolbar);
					}
					
					for (final AccountStatement as : result.getDetails()) {
						
						String document = AonStringUtils.defaultString(as.getDocumentNumber());

						final int currentRow = row;
						if (allowChecks) {
							final InlineLabel check = new InlineLabel();
							check.setStyleName(AON.AON_CSS.aonIconCheck());
							check.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
							check.addStyleName(AON.AON_CSS.aonClickable());
							check.setTitle(AON.MSG.check());
							check.addClickHandler(new ClickHandler() {
								
								@Override
								public void onClick(ClickEvent event) {
									boolean selected = !as.isSelected();
									check.setTitle(selected?AON.MSG.uncheck():AON.MSG.check());
									as.setSelected(selected);
									toogleStyle( check, selected  );
									if (selected) {
										tempList.add(currentRow);
									} else {
										tab.getRowFormatter().removeStyleName(currentRow, AON.AON_CSS.aonBackgroundHighlightedGreen());
										tab.getRowFormatter().removeStyleName(currentRow, AON.AON_CSS.aonBackgroundHighlightedOrange());
										tempList.remove(new Integer(currentRow));
										if (docMap.containsKey(document)) {
											docMap.get(document).remove(new Integer(currentRow));
										}
									}
									decorateTable(result, tab, tempList, docMap);
								}
							});
							tab.setWidget(row, 0, check );
						}
						
						Label journalLabel = new Label(AonStringUtils.defaultString(AonNumberUtils.toString(as.getJournal())));
						journalLabel.addStyleName(AON.AON_CSS.aonClickableLabel());
						journalLabel.setTitle(AON.MSG.goAction());
						journalLabel.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								AccountEntry ae = new AccountEntry()
										.setId(as.getAccountEntry())
										.setDomain( currentDomainId );
								AccountEntrySelectionEvent.fire(StatementPanel.this, ae, new ModuleCallback<AccountEntry>() {
									
									@Override
									public void onRemove(AccountEntry removed) {
										int scrollPosition = StatementPanel.this.getVerticalScrollPosition();
										search(allowChecks);
										StatementPanel.this.setVerticalScrollPosition(scrollPosition);
									}
									
									@Override
									public void onFailure(Throwable caught) {
									}
									
									@Override
									public void onExit() {
									}
									
									@Override
									public void onChange(AccountEntry changed) {
										int scrollPosition = StatementPanel.this.getVerticalScrollPosition();
										search(allowChecks);
										StatementPanel.this.setVerticalScrollPosition(scrollPosition);
									}
								} );
							}
						});
						tab.setWidget(row, 1, journalLabel);
						tab.getCellFormatter().addStyleName(row, 1,AON.AON_CSS.aonTextRight());
						
						
						tab.setWidget(row, 2, new Label( AON.DATE_FORMAT.format(as.getEntryDate()) ));
						Label conceptLabel = new Label( AonStringUtils.abbreviate(as.getConcept(),32) );
						conceptLabel.setStyleName(AON.AON_CSS.aonNowrap());
						tab.setWidget(row, 3, conceptLabel);
						tab.setWidget(row, 4, new Label( AonMathUtils.isZero(as.getDebit())?AonStringUtils.SPACE:AON.FMT.format(as.getDebit()) ));
						tab.getCellFormatter().addStyleName(row, 4,AON.AON_CSS.aonTextRight());
						tab.setWidget(row, 5, new Label( AonMathUtils.isZero(as.getCredit())?AonStringUtils.SPACE:AON.FMT.format(as.getCredit()) ));
						tab.getCellFormatter().addStyleName(row, 5,AON.AON_CSS.aonTextRight());
						tab.setWidget(row, 6, new Label( AonMathUtils.isZero(as.getDebitBalance())?AonStringUtils.SPACE:AON.FMT.format(as.getDebitBalance()) ));
						tab.getCellFormatter().addStyleName(row, 6,AON.AON_CSS.aonTextRight());
						tab.setWidget(row, 7, new Label( AonMathUtils.isZero(as.getUnpaidBalance())?AonStringUtils.SPACE:AON.FMT.format(as.getUnpaidBalance()) ));
						tab.getCellFormatter().addStyleName(row, 7,AON.AON_CSS.aonTextRight());
						tab.getCellFormatter().addStyleName(row, 7,AON.AON_CSS.aonPaddingRight());
						Label balancingAccountLabel = new Label( AonStringUtils.defaultString(as.getBalancingAccountCode()) 
								+ " " + AonStringUtils.defaultString(AonStringUtils.abbreviate(as.getBalancingAccountDescription(),11)));
						balancingAccountLabel.setStyleName(AON.AON_CSS.aonNowrap());
						tab.setWidget(row, 8, balancingAccountLabel);
						

						Label documentLabel = new Label( document );
						tab.setWidget(row, 9, documentLabel);
						documentLabel.setStyleName(AON.AON_CSS.aonNowrap());
						documentLabel.setTitle("Seleccionar apuntes con este n\u00BA documento");
						if (allowChecks) {
							documentLabel.setStyleName(AON.AON_CSS.aonClickableLabel());
							documentLabel.addClickHandler(new ClickHandler() {
								
								@Override
								public void onClick(ClickEvent event) {
									boolean selected = !as.isSelected();
									as.setSelected(selected);
									if (selected) {
										docMap.put(document, new LinkedList<Integer>());
										int rw = rowOffset;
										for (final AccountStatement as2 : result.getDetails()) {
											if (AonStringUtils.equals(as.getDocumentNumber(), as2.getDocumentNumber())) {
												toogleStyle(tab.getWidget(rw, 0), selected);
												docMap.get(document).add(rw);
												as2.setSelected(selected);
											}
											rw++;
										}
									} else {
										if (docMap.containsKey(document)) {
											for (Integer rw : docMap.get(document)) {
												toogleStyle(tab.getWidget(rw, 0), selected);
												tab.getRowFormatter().removeStyleName(rw, AON.AON_CSS.aonBackgroundHighlightedGreen());
												tab.getRowFormatter().removeStyleName(rw, AON.AON_CSS.aonBackgroundHighlightedOrange());
											}
											docMap.remove(document);
										}
									}
									decorateTable(result, tab, tempList, docMap);
								}
							});
						}
						tab.getRowFormatter().addStyleName(currentRow, AON.AON_CSS.aonReportTableRowBckHover());
						
						row++;
					}
				}
				root.add(tab);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Label label = new Label(AON.MSG.noData() + " ["+caught+"]");
				root.add(label );
			}
			
		});
	}

	private void decorateTable(AccountStatementReport result, FlexTable tab, LinkedList<Integer> listMap, HashMap<String, LinkedList<Integer>> docMap) {
		for (LinkedList<Integer> list : docMap.values()) {
			decorateTable(result, tab, list);
		}
		decorateTable(result, tab, listMap);
	}

	private boolean isSettled(AccountStatementReport result, LinkedList<Integer> list) {
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
	
	private void decorateTable(AccountStatementReport result, FlexTable tab, LinkedList<Integer> list) {
		boolean balanced = isSettled(result, list);
		String highlightStyle = balanced
				?AON.AON_CSS.aonBackgroundHighlightedGreen()
				:AON.AON_CSS.aonBackgroundHighlightedOrange();
		for (Integer row : list) {
			tab.getRowFormatter().removeStyleName(row, AON.AON_CSS.aonBackgroundHighlightedGreen());
			tab.getRowFormatter().removeStyleName(row, AON.AON_CSS.aonBackgroundHighlightedOrange());
			tab.getRowFormatter().addStyleName(row, highlightStyle);
			result.getDetails().get(row - rowOffset).setBalanced(balanced);
		}
	}
	
	private void toogleStyle(Widget widget, boolean selected) {
		if (selected) {
			widget.addStyleName(AON.AON_CSS.aonIconChecked());
			widget.removeStyleName(AON.AON_CSS.aonIconCheck());
		} else {
			widget.addStyleName(AON.AON_CSS.aonIconCheck());
			widget.removeStyleName(AON.AON_CSS.aonIconChecked());
		}
	}

	@Override
	public HandlerRegistration addSelectionHandler(AccountEntrySelectionHandler handler) {
		return super.addHandler(handler, AccountEntrySelectionEvent.getType());
	}
}

package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox.ExpressionResolver;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.IWizardContent;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasErrorHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;


public class AccountEntryTable extends FlexTable implements HasErrorHandlers, Focusable
 , HasSelectionHandlers<Account>, HasValueChangeHandlers<AccountEntryDetail> {
	
	private String domainName;
	private int domainId;
	private String user;
	
	private IWizardContent wizardContent;
	private Label sumDebit;
	private Label sumCredit;
	private ExpressionResolver resolver;
	
	private boolean confirmConceptChange;
	private boolean confirmDocumentChange;
	
	private MultiWordSuggestOracle oracle = new MultiWordSuggestOracle() {
		
		@Override
		public void requestSuggestions(Request request, Callback callback) {
		    String query = request.getQuery();
			LinkedList<String> autoConcepts =  wizardContent.getConfiguration().getAutoConcepts();
			if (autoConcepts == null || autoConcepts.size() == 0) {
				callback.onSuggestionsReady(request, new Response());	
			} else {
				LinkedList<Suggestion> suggestions = new LinkedList<Suggestion>();
				for ( String concept : autoConcepts ) {
					if (AonStringUtils.startsWithIgnoreCase(concept, query) ) {
						suggestions.add(new MultiWordSuggestion(concept, concept));
					}
				}
				callback.onSuggestionsReady(request, new Response(suggestions));
			}
			
		    
		}
		
	};
	
	private static enum COLS {
		  NUM(AonStringUtils.EMPTY		,"20px" ,AON.AON_CSS.aonTextCenter())
		, ACC(AON.MSG.account()			,"auto" ,null)
		, CON(AON.MSG.concept()			,"250px",AON.AON_CSS.aonTextCenter())
		, DEB(AON.MSG.debit()			,"120px",AON.AON_CSS.aonTextRight())
		, CRE(AON.MSG.credit()			,"120px",AON.AON_CSS.aonTextRight())
		, BAL(AON.MSG.balancingAccount(),"105px",AON.AON_CSS.aonTextCenter())
		, DOC(AON.MSG.document()		,"190px",AON.AON_CSS.aonTextCenter())
		, BUT(AonStringUtils.EMPTY		, "20px",AON.AON_CSS.aonTextCenter())
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private COLS(String headerLabel,String colWidth) {
			this(headerLabel, colWidth, null);
		}

		private COLS(String headerLabel,String colWidth,String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getCellStyleClass() {
			return cellStyleClass;
		}
	}
	
	public AccountEntryTable(String domainName, String user, int domainId, IWizardContent wizardContent) {
		this.wizardContent = wizardContent;
		this.domainName = domainName; 
		this.domainId = domainId;
		this.user = user; 

		setConfirmConceptChange(true);
		setConfirmDocumentChange(true);

		sumDebit = new Label();
		sumDebit.setStyleName(AON.AON_CSS.aonBold());
		sumCredit = new Label();
		sumCredit.setStyleName(AON.AON_CSS.aonBold());
		addStyleName(AON.AON_CSS.aonDataTable());
		resolver = new ExpressionResolver() {
			@Override
			public void resolve(String expression, AsyncCallback<Double> callback) {
				AccountEntryModule.fiscalService.mathExpression(expression,callback);
			}
		}; 
	}
	public String getDomainName() {
		return domainName;
	}
	public int getDomainId() {
		return domainId;
	}
	public String getUser() {
		return user;
	}
	public boolean isConfirmConceptChange() {
		return confirmConceptChange;
	}
	public void setConfirmConceptChange(boolean confirmConceptChange) {
		this.confirmConceptChange = confirmConceptChange;
	}
	public boolean isConfirmDocumentChange() {
		return confirmDocumentChange;
	}
	public void setConfirmDocumentChange(boolean confirmDocumentChange) {
		this.confirmDocumentChange = confirmDocumentChange;
	}



	public void paintTable() {
		paintHeader();
		paintDetails();
		paintFooter();
		paintAddButton();
	}

	private void paintHeader() {
		for ( COLS col : COLS.values()) {
			getColumnFormatter().setWidth(col.ordinal(), col.getColWidth());	
			setWidget(0, col.ordinal(), new Label( col.getHeaderLabel() ));
			getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.AON_CSS.aonDataTableHeader());
			if ( col.getCellStyleClass() != null) {
				getFlexCellFormatter().addStyleName(0, col.ordinal(),col.getCellStyleClass());
				getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.AON_CSS.aonNowrap());
			}
		}
	}

	private void paintFooter() {
		int row = getRowCount();
		setWidget(row, COLS.NUM.ordinal(), new Label(AonStringUtils.EMPTY));
		setWidget(row, COLS.ACC.ordinal(), new Label(AonStringUtils.EMPTY));
		setWidget(row, COLS.CON.ordinal(), new Label(AonStringUtils.EMPTY));
		setWidget(row, COLS.DEB.ordinal(), sumDebit);
		getFlexCellFormatter().addStyleName(row, COLS.DEB.ordinal(),AON.AON_CSS.aonTextRight());
		setWidget(row, COLS.CRE.ordinal(), sumCredit );
		getFlexCellFormatter().addStyleName(row, COLS.CRE.ordinal(),AON.AON_CSS.aonTextRight());
		setWidget(row, COLS.BAL.ordinal(), new Label(AonStringUtils.EMPTY));
		setWidget(row, COLS.DOC.ordinal(), new Label(AonStringUtils.EMPTY));
		refreshTotals();
	}
	
	private double getSumDif(boolean fillWidget) {
		double sumD = 0.0;
		double sumC = 0.0;
		for (AccountEntryDetail aed : wizardContent.getMainEntry().getDetails()) {
			if (!aed.isDeleted()) {
				sumD = AonMathUtils.sum(sumD, aed.getDebit());	
				sumC = AonMathUtils.sum(sumC, aed.getCredit());
			}
		}
		if (fillWidget) {
			sumDebit.setText(AON.FMT.format(sumD));
			sumCredit.setText(AON.FMT.format(sumC));
		}
		return AonMathUtils.round(sumD - sumC);
	}

	private void refreshTotals() {
		boolean equals = AonMathUtils.isZero( getSumDif(true) );
		sumDebit.setStyleName(equals?AON.AON_CSS.aonColorGreen():AON.AON_CSS.aonColorRed());
		sumCredit.setStyleName(equals?AON.AON_CSS.aonColorGreen():AON.AON_CSS.aonColorRed());
	}
	
	private void paintDetails() {
		if ( wizardContent.getMainEntry().getDetails().isEmpty() ) {
			wizardContent.getMainEntry().getDetails().add(new AccountEntryDetail());
		}
		int row = 1;
		for (AccountEntryDetail aed : wizardContent.getMainEntry().getDetails()) {
			paintRow(row,aed);
			++row;
		}
	}

	private void paintRow(int row, final AccountEntryDetail aed) {
		if (wizardContent.isUpdatable()) {
			if (aed.isDeleted()) {
				paintDeletedRow(row, aed);		
			} else {
				paintActiveRow(row, aed);
			}
		} else {
			paintUnmodifiableRow(row, aed);
		}
	}
	private Label getUnmodifiableLabel(String text) {
		Label label = new Label(text);
		return label;
	}
	private Label getDeletedLabel(String text) {
		Label label = new Label(text);
		label.setStyleName(AON.AON_CSS.aonItalic());
		label.addStyleName(AON.AON_CSS.aonTextLineThrough());
		label.addStyleName(AON.AON_CSS.aonFontSmall());
		label.addStyleName(AON.AON_CSS.aonBackgroundDisabled() );
		return label;
	}
	private void paintDeletedRow(int row, final AccountEntryDetail aed) {
		setWidget(row, COLS.NUM.ordinal(), new Label()  );
		setWidget(row, COLS.ACC.ordinal(), getDeletedLabel(
			AonStringUtils.abbreviate( aed.getAccountCode() + " " + aed.getAccountDescription() , 50)));
		
		setWidget(row, COLS.CON.ordinal(), getDeletedLabel(aed.getConcept() ));
		setWidget(row, COLS.DEB.ordinal(), getDeletedLabel(AON.FMT.format( aed.getDebit()) ));
		getFlexCellFormatter().addStyleName(row, COLS.DEB.ordinal(),AON.AON_CSS.aonTextRight());
		setWidget(row, COLS.CRE.ordinal(), getDeletedLabel(AON.FMT.format( aed.getCredit()) ));
		getFlexCellFormatter().addStyleName(row, COLS.CRE.ordinal(),AON.AON_CSS.aonTextRight());
		setWidget(row, COLS.BAL.ordinal(), getDeletedLabel(aed.getBalancingAccountCode()));
		setWidget(row, COLS.DOC.ordinal(), getDeletedLabel(aed.getDocumentNumber() ));
		final int curRow = row;
		Button restoreButton = new Button();
		restoreButton.setStyleName(AON.AON_CSS.aonIconUndo());
		restoreButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
		restoreButton.setTabIndex(Integer.MAX_VALUE);
		restoreButton.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				aed.setId( aed.getId() * -1 );
				paintActiveRow(curRow, aed);
				refreshTotals();
			}
		});
		setWidget(row, COLS.BUT.ordinal(), restoreButton);
	}
	
	private void paintUnmodifiableRow(int row, final AccountEntryDetail aed) {
		setWidget(row, COLS.NUM.ordinal(), new Label()  );
		setWidget(row, COLS.ACC.ordinal(), getUnmodifiableLabel(
				AonStringUtils.abbreviate( aed.getAccountCode() + " " + aed.getAccountDescription() , 50)));
		setWidget(row, COLS.CON.ordinal(), getUnmodifiableLabel(aed.getConcept() ));
		setWidget(row, COLS.DEB.ordinal(), getUnmodifiableLabel(AON.FMT.format( aed.getDebit()) ));
		getFlexCellFormatter().addStyleName(row, COLS.DEB.ordinal(),AON.AON_CSS.aonTextRight());
		setWidget(row, COLS.CRE.ordinal(), getUnmodifiableLabel(AON.FMT.format( aed.getCredit()) ));
		getFlexCellFormatter().addStyleName(row, COLS.CRE.ordinal(),AON.AON_CSS.aonTextRight());
		setWidget(row, COLS.BAL.ordinal(), getUnmodifiableLabel(aed.getBalancingAccountCode()));
		setWidget(row, COLS.DOC.ordinal(), getUnmodifiableLabel(aed.getDocumentNumber() ));
		setWidget(row, COLS.BUT.ordinal(), new Label());
	}
	
	private void paintActiveRow(int row, final AccountEntryDetail aed) {
		setWidget(row, COLS.NUM.ordinal(), new Label()  );
		
		final AccountBox detailAccountBox = new AccountBox(getDomainName(),getDomainId());
		detailAccountBox.setValue(aed.getAccount(), aed.getAccountCode(),aed.getAccountDescription());

		setWidget(row, COLS.ACC.ordinal(), detailAccountBox );
		
		final TextBox conceptBox = new TextBox();
		final SuggestBox conceptSuggestBox = new SuggestBox(oracle,conceptBox);
		conceptBox.setVisibleLength(20);
		conceptBox.setMaxLength(32);
		conceptBox.setStyleName(AON.AON_CSS.aonInputText());
		conceptBox.setValue(aed.getConcept());
		setWidget(row, COLS.CON.ordinal(), conceptSuggestBox );
		
		final DoubleBox debitBox = new DoubleBox();
		debitBox.setValue(aed.getDebit());
		debitBox.setResolver(resolver);
		setWidget(row, COLS.DEB.ordinal(), debitBox );
		getFlexCellFormatter().addStyleName(row, COLS.DEB.ordinal(),AON.AON_CSS.aonTextRight());
		
		final DoubleBox creditBox = new DoubleBox();
		creditBox.setValue(aed.getCredit());
		creditBox.setResolver(resolver);
		setWidget(row, COLS.CRE.ordinal(), creditBox );
		getFlexCellFormatter().addStyleName(row, COLS.CRE.ordinal(),AON.AON_CSS.aonTextRight());
		
		final AccountBox balancingAccountBox = new AccountBox(getDomainName(),getDomainId(), false);
		balancingAccountBox.setValue(aed.getBalancingAccount(), aed.getBalancingAccountCode(),
				aed.getBalancingAccountDescription());
		balancingAccountBox.setRequired(false);
		setWidget(row, COLS.BAL.ordinal(), balancingAccountBox);

		final TextBox documentBox = new TextBox();
		documentBox.setVisibleLength(20);
		documentBox.setMaxLength(32);
		documentBox.setValue(aed.getDocumentNumber());
		documentBox.setStyleName(AON.AON_CSS.aonInputText());
		setWidget(row, COLS.DOC.ordinal(), documentBox );

		// -------------------------------------------------------------- EVENTS
		// ---------------------------------------------------- [DETAIL ACCOUNT]
		detailAccountBox.addSelectionHandler( new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				if (event.getSelectedItem() != null) {
					aed.setAccount(event.getSelectedItem().getId());
					aed.setAccountCode(event.getSelectedItem().getCode());
					aed.setAccountDescription(event.getSelectedItem().getDescription());
				} else {
					aed.setAccount(null);
					aed.setAccountCode(null);
					aed.setAccountDescription(null);
				}
				ValueChangeEvent.<AccountEntryDetail>fire(AccountEntryTable.this, aed);
			}
		});
		detailAccountBox.addSelectionHandler( new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				if (event.getSelectedItem() != null) {
					SelectionEvent.<Account>fire(AccountEntryTable.this, event.getSelectedItem());
				}
			}
		});
		// ----------------------------------------------------------- [CONCEPT]
//		conceptSuggestBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
//			
//			@Override
//			public void onSelection(SelectionEvent<Suggestion> event) {
//				ValueChangeEvent.<String>fire(conceptSuggestBox, event.getSelectedItem().getReplacementString());
//			}
//		});
		
		conceptSuggestBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(final ValueChangeEvent<String> event) {
				aed.setConcept(event.getValue());
				if (getRowCount() > 3 &&  isConfirmConceptChange()) {
					ConfirmDialog cd = new ConfirmDialog();
					cd.confirm(AON.MSG.changeConcept(),new ConfirmDialogCallback() {
						
						@Override
						public void onCancel() {
							setConfirmConceptChange(false);
						}
						@Override
						public void onClose() {
							debitBox.setFocus(true);
						}
						
						@Override
						public void onAccept() {
							for (int i = 1; i < getRowCount() ; i++ ) {
								Widget w = getWidget( i , COLS.CON.ordinal());
								if (w instanceof SuggestBox) {
									SuggestBox cb = (SuggestBox) w;
									cb.getValueBox().setValue(conceptBox.getValue(), false);
								}
							}
							for (AccountEntryDetail aed : wizardContent.getMainEntry().getDetails() ) {
								aed.setConcept(conceptBox.getValue());
							}
							debitBox.setFocus(true);
							ValueChangeEvent.<AccountEntryDetail>fire(AccountEntryTable.this, aed);
						}
					});
				} else {
					ValueChangeEvent.<AccountEntryDetail>fire(AccountEntryTable.this, aed);
				}
			}
		});
		// ------------------------------------------------------------- [DEBIT]
		debitBox.addErrorHandler(new ErrorHandler() {
			@Override
			public void onError(ErrorEvent event) {
				NativeEvent event2 = Document.get().createErrorEvent();
				debitBox.getElement().setAttribute("ERROR", AON.MSG.arithmeticExpressionError(debitBox.getText()));
				DomEvent.fireNativeEvent(event2, AccountEntryTable.this, debitBox.getElement());
			}
		});
		debitBox.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				if (AonMathUtils.isNotZero( debitBox.getValue())) {
					if (AonMathUtils.isNegative( debitBox.getValue())) {
						creditBox.setValue(AonMathUtils.absRounded(debitBox.getValue()),true,true);
						debitBox.setValue(0.0,false);
					} else {
						creditBox.setValue(0.0,true);
						balancingAccountBox.setFocus(true);
					}
				} 
				double d = AonMathUtils.round( debitBox.getValue());
				aed.setDebit( d );
				debitBox.setValue(d,false);
				refreshTotals();
				ValueChangeEvent.<AccountEntryDetail>fire(AccountEntryTable.this, aed);
			}
		});
		debitBox.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					double d = AonMathUtils.round(getSumDif(false) - aed.getDebit() + aed.getCredit());
					if (d < 0) {
						aed.setDebit(AonMathUtils.absRounded(d));
						debitBox.setValue(AonMathUtils.absRounded(d));
						aed.setCredit( 0.0 );
						creditBox.setValue(0.0);
					} else {
						aed.setDebit(0.0);
						debitBox.setValue(0.0);
						aed.setCredit(d);
						creditBox.setValue(d);
					}
					refreshTotals();
					balancingAccountBox.setFocus(true);
					ValueChangeEvent.<AccountEntryDetail>fire(AccountEntryTable.this, aed);
				}
			}
		});
		// ------------------------------------------------------------ [CREDIT]
		creditBox.addErrorHandler(new ErrorHandler() {
			@Override
			public void onError(ErrorEvent event) {
				NativeEvent event2 = Document.get().createErrorEvent();
				creditBox.getElement().setAttribute("ERROR", AON.MSG.arithmeticExpressionError(creditBox.getText()));
				DomEvent.fireNativeEvent(event2, AccountEntryTable.this, creditBox.getElement());
			}
		});
		creditBox.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				if (AonMathUtils.isNotZero( creditBox.getValue())) {
					if (AonMathUtils.isNegative( creditBox.getValue())) {
						debitBox.setValue(AonMathUtils.absRounded(creditBox.getValue()),true,true);
						creditBox.setValue(0.0,false);
					} else {
						debitBox.setValue(0.0,true);
					}
				}
				double d = AonMathUtils.round( creditBox.getValue());
				aed.setCredit( d );
				creditBox.setValue(d,false);
				refreshTotals();
				ValueChangeEvent.<AccountEntryDetail>fire(AccountEntryTable.this, aed);
			}
		});
		final int curRow = row;
		creditBox.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					double d = AonMathUtils.round(getSumDif(false) - aed.getDebit() + aed.getCredit());
					if (d > 0) {
						creditBox.setValue(d);
						aed.setCredit(d);
						debitBox.setValue(0.0);
						aed.setDebit(0.0);
					} else {
						debitBox.setValue(AonMathUtils.absRounded(d));
						aed.setDebit(AonMathUtils.absRounded(d));
						creditBox.setValue(0.0,true);
						aed.setCredit(0.0);
					}
					refreshTotals();
					balancingAccountBox.setFocus(true);
					ValueChangeEvent.<AccountEntryDetail>fire(AccountEntryTable.this, aed);
				}
			}
		});
		// ------------------------------------------------- [BALANCING ACCOUNT]
		balancingAccountBox.addSelectionHandler( new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				if (event.getSelectedItem() != null) {
					aed.setBalancingAccount(event.getSelectedItem().getId());
					aed.setBalancingAccountCode(event.getSelectedItem().getCode());
					aed.setBalancingAccountDescription(event.getSelectedItem().getDescription());
				} else {
					aed.setBalancingAccount(null);
					aed.setBalancingAccountCode(null);
					aed.setBalancingAccountDescription(null);
				}
				ValueChangeEvent.<AccountEntryDetail>fire(AccountEntryTable.this, aed);
			}
		});
		balancingAccountBox.addSelectionHandler( new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				if (event.getSelectedItem() != null) {
					SelectionEvent.<Account>fire(AccountEntryTable.this, event.getSelectedItem());
				}
			}
		});
		// --------------------------------------------------- [DOCUMENT NUMBER]
		documentBox.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(final ValueChangeEvent<String> event) {
				aed.setDocumentNumber(documentBox.getValue());
				if (getRowCount() > 3 && isConfirmDocumentChange()) {
					ConfirmDialog cd = new ConfirmDialog();
					cd.confirm(AON.MSG.changeDocument(),new ConfirmDialogCallback() {
						
						@Override
						public void onCancel() {
							setConfirmDocumentChange(false);
						}
						@Override
						public void onClose() {
							AccountEntryTable.this.setFocus(true);
						}
						@Override
						public void onAccept() {
							for (int i = 1; i < getRowCount() ; i++ ) {
								Widget w = getWidget( i , COLS.DOC.ordinal());
								if (w instanceof TextBox) {
									TextBox db = (TextBox) w;
									db.setValue(event.getValue());
								}
							}
							for (AccountEntryDetail aed : wizardContent.getMainEntry().getDetails() ) {
								aed.setDocumentNumber(event.getValue());
							}
							AccountEntryTable.this.setFocus(true);
						}
					});
				}
				ValueChangeEvent.<AccountEntryDetail>fire(AccountEntryTable.this, aed);
			}
		});
		
		// --------------------------------------------------- [DETAIL REMOVE]
		Button removeButton = new Button();
		removeButton.setStyleName(AON.AON_CSS.aonIconDelete());
		removeButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
		removeButton.setTabIndex(Integer.MAX_VALUE);
		removeButton.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (aed.getId() == null) {
					wizardContent.getMainEntry().getDetails().remove(aed);
					removeRow(curRow);
					removeRow(getRowCount() - 1);
					paintFooter();
					paintAddButton();
				} else {
					aed.setId( aed.getId() * -1 );
					paintDeletedRow(curRow, aed);
					refreshTotals();
				}
			}
		});
		setWidget(row, COLS.BUT.ordinal(), removeButton);
	}

	private boolean canAddLine() {
		if (!wizardContent.isUpdatable()) return false; 
		if (wizardContent.getMainEntry().getDetails() == null || wizardContent.getMainEntry().getDetails().isEmpty()) return false;
		if (wizardContent.getMainEntry().getDetailsSize() == 1) return true;
		return AonMathUtils.isNotZero( getSumDif(false) );
	}

	private void paintAddButton() {
		if ( wizardContent.isUpdatable()) {
			final Button addButton = new Button();
			addButton.setAccessKey( 'L' );
			addButton.setStyleName(AON.AON_CSS.aonIconReset());
			addButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
			addButton.addFocusHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					if (canAddLine()) {
						addLine();
						remove(addButton);
						setFocus(true);
					}
				}
			});
			
			addButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					addLine();
					remove(addButton);
					Focusable focusable = (Focusable) getWidget( getRowCount() - 2, COLS.ACC.ordinal());
					focusable.setFocus(true);
				}
			});
			setWidget(getRowCount() - 1, COLS.NUM.ordinal(),  addButton );
		} else {
			setWidget(getRowCount() - 1, COLS.NUM.ordinal(),  new Label());
		}

	}

	private void addLine() {
		AccountEntryDetail aed = wizardContent.getMainEntry().getLastDetail();
		AccountEntryDetail newDetail = new AccountEntryDetail();
		if (aed != null) {
			newDetail.setConcept(aed.getConcept());
			newDetail.setDocumentNumber(aed.getDocumentNumber());
	
			if ( wizardContent.getMainEntry().getDetailsSize() == 1) {
				newDetail.setAccount(aed.getBalancingAccount());
				newDetail.setAccountCode(aed.getBalancingAccountCode());
				newDetail.setAccountDescription(aed.getBalancingAccountDescription());
				newDetail.setBalancingAccount(aed.getAccount());
				newDetail.setBalancingAccountCode(aed.getAccountCode());
				newDetail.setBalancingAccountDescription(aed.getAccountDescription());
			} else {
				newDetail.setBalancingAccount(aed.getBalancingAccount());
				newDetail.setBalancingAccountCode(aed.getBalancingAccountCode());
				newDetail.setBalancingAccountDescription(aed.getBalancingAccountDescription());
			}
		}
		double balance = getSumDif(false);
		if (balance > 0) {
			newDetail.setDebit(0);
			newDetail.setCredit(balance);
		} else {
			newDetail.setDebit(AonMathUtils.absRounded(balance));
			newDetail.setCredit(0);
		}
		
		wizardContent.getMainEntry().getDetails().add(newDetail);
		paintRow( (getRowCount() - 1), newDetail);
		paintFooter();
		if (wizardContent.isUpdatable()) {
			paintAddButton();
		}
	}

	@Override
	public HandlerRegistration addErrorHandler(ErrorHandler handler) {
		return addHandler(handler, ErrorEvent.getType());
	}

	@Override
	public int getTabIndex() {
		Focusable focusable = (Focusable) getWidget( getRowCount() - 2, COLS.ACC.ordinal());
		return focusable.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		Focusable focusable = (Focusable) getWidget( getRowCount() - 2, COLS.ACC.ordinal());
		focusable.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		if (wizardContent.isUpdatable()) {
			AccountBox ab = (AccountBox) getWidget( getRowCount() - 2, COLS.ACC.ordinal());
			ab.setFocus(true);
		}
	}

	@Override
	public void setTabIndex(int index) {
		Focusable focusable = (Focusable) getWidget( getRowCount() - 2, COLS.ACC.ordinal());
		focusable.setTabIndex(index);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Account> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<AccountEntryDetail> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
}

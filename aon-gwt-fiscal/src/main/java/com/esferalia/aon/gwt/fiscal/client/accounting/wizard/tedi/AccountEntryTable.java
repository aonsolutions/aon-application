package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.IWizardContent;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.HasErrorHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


public class AccountEntryTable extends AonDisplayGrid implements HasErrorHandlers, Focusable
 , HasSelectionHandlers<Account>, HasValueChangeHandlers<AccountEntryDetail> {
	
	private static final Logger LOGGER = Logger.getLogger(AccountEntryTable.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private Focusable focusable;
	
	private IWizardContent wizardContent;
	private Label sumDebit;
	private Label sumCredit;
	
	private boolean confirmConceptChange;
	private boolean confirmDocumentChange;
	private FlowPanel footerRow = new FlowPanel();
	
	private MultiWordSuggestOracle oracle = new MultiWordSuggestOracle() {
		
		@Override
		public void requestSuggestions(Request request, Callback callback) {
		    String query = request.getQuery();
			LinkedList<String> autoConcepts =  wizardContent.getConfiguration().accounting().getAutoConcepts();
			if (autoConcepts == null || autoConcepts.isEmpty()) {
				callback.onSuggestionsReady(request, new Response());	
			} else {
				LinkedList<Suggestion> suggestions = new LinkedList<>();
				for ( String concept : autoConcepts ) {
					if (AonStringUtils.startsWithIgnoreCase(concept, query) ) {
						suggestions.add(new MultiWordSuggestion(concept, concept));
					}
				}
				callback.onSuggestionsReady(request, new Response(suggestions));
			}
		}
		
	};
	
	private enum COLS {
		  NUM("#"						,"20px" ,AON.CSS.aonTextCenter())
		, ACC(AON.MSG.account()			,"auto" ,null)
		, CON(AON.MSG.concept()			,"250px",AON.CSS.aonTextCenter())
		, DEB(AON.MSG.debit()			,"120px",AON.CSS.aonTextCenter())
		, CRE(AON.MSG.credit()			,"120px",AON.CSS.aonTextCenter())
		, BAL(AON.MSG.balancingAccount(),"105px",AON.CSS.aonTextCenter())
		, DOC(AON.MSG.document()		,"190px",AON.CSS.aonTextCenter())
		, BUT(AonStringUtils.EMPTY		, "20px",AON.CSS.aonTextCenter())
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private COLS(String headerLabel,String colWidth,String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public String getColWidth()    		{return colWidth;}
		public String getHeaderLabel() 		{return headerLabel;}
		public String getCellStyleClass() 	{return cellStyleClass;		}
	}
	
	public AccountEntryTable( AccountEntryModuleOptions options, IWizardContent wizardContent) {
		addStyleName(AON.CSS.aonBlockCenter());
		addStyleName(AON.CSS.aonWidthAlmostAll());

		paint( options, wizardContent);
	}
	
	private void paint( AccountEntryModuleOptions options, IWizardContent wizardContent) {
		this.clear();
		this.wizardContent = wizardContent;
		
		setConfirmConceptChange(true);
		setConfirmDocumentChange(true);
		sumDebit = new Label();
		sumDebit.setStyleName(AON.CSS.aonBold());
		sumCredit = new Label();
		sumCredit.setStyleName(AON.CSS.aonBold());
		
		// ******************************************  
		// ***************** HEADER *****************  
		// ******************************************  
		AonDisplayGridHeaderRow headerRow = this.addHeaderRow();
		for ( COLS col : COLS.values()) {
			AonDisplayGridCell headerCell = headerRow.addCell();
			headerCell.add(new Label( col.getHeaderLabel() ));
			headerCell.setWidth(col.getColWidth());
			if ( col.getCellStyleClass() != null) {
				headerCell.addStyleName(col.getCellStyleClass());
			}
		}

		// ******************************************  
		// ***************** DETAILS *****************  
		// ******************************************  
		if ( wizardContent.getMainEntry().getDetails().isEmpty() ) {
			AccountEntryDetail newDetail = new AccountEntryDetail();
			newDetail.setLine(1);
			wizardContent.getMainEntry().getDetails().add( newDetail );
		}
		int rowNumber = 0;
		for (AccountEntryDetail aed : wizardContent.getMainEntry().getDetails()) {
			paintRow(options, rowNumber++, aed);
		}
		
		// ******************************************  
		// ***************** FOOTER *****************  
		// ******************************************
		paintFooter(options, rowNumber);
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

	private void paintFooter(AccountEntryModuleOptions options, int rowNumber) {
		final AonTableButton addButton = new AonTableButton(AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		addButton.setAccessKey( 'L' );
		addButton.addFocusHandler(event -> {
			if (canAddLine()) {
				addLine(options, rowNumber);
				paintFooter(options, rowNumber+1);
			}
		});
		addButton.addClickHandler(event -> {
			addLine( options, rowNumber);
			paintFooter(options, rowNumber+1);
		});
		if (footerRow !=null) {
			remove(footerRow);
		}
		footerRow = addFooterRow()
			.addCell( (wizardContent.isUpdatable()? addButton : new Label()) )
			.addCell( new Label() )
			.addCell( new Label() )
			.addCell( sumDebit , AON.CSS.aonTextRight() )
			.addCell( sumCredit , AON.CSS.aonTextRight() )
			.addCell( new Label())
			.addCell( new Label())
			.addCell( new Label())
			;
	}


	private void paintRow(AccountEntryModuleOptions options, int rowNumber, final AccountEntryDetail aed) {
		if (wizardContent.isUpdatable()) {
			if (aed.isDeleted()) {
				paintDeletedRow(options, rowNumber, addRow(), aed);		
			} else {
				paintActiveRow(options, rowNumber, addRow(), aed);
			}
		} else {
			paintUnmodifiableRow(addRow(), aed);
		}
	}
	
	
	private Focusable paintActiveRow(final AccountEntryModuleOptions options, int rowNumber,AonDisplayGridRow row,final AccountEntryDetail aed) {
		
		final AonAccountBox detailAccountBox = new AonAccountBox(options.getOccam());
		detailAccountBox.setValue(aed.getAccount(), aed.getAccountCode(),aed.getAccountDescription());
		focusable = detailAccountBox;

		final TextBox conceptBox = new TextBox();
		final SuggestBox conceptSuggestBox = new SuggestBox(oracle,conceptBox);
		conceptBox.setVisibleLength(30);
		conceptBox.setMaxLength(64);
		conceptBox.setStyleName(AON.CSS.aonInputText());
		conceptBox.setValue(aed.getConcept());
		
		final AonDoubleBox debitBox = new AonDoubleBox();
		debitBox.setValue(aed.getDebit());
		
		final AonDoubleBox creditBox = new AonDoubleBox();
		creditBox.setValue(aed.getCredit());
		
		final AonAccountBox balancingAccountBox = new AonAccountBox(options.getOccam(), false);
		balancingAccountBox.setValue(aed.getBalancingAccount(), aed.getBalancingAccountCode(),
				aed.getBalancingAccountDescription());
		balancingAccountBox.setRequired(false);
		
		final TextBox documentBox = new TextBox();
		documentBox.setVisibleLength(20);
		documentBox.setMaxLength(32);
		documentBox.setValue(aed.getDocumentNumber());
		documentBox.setStyleName(AON.CSS.aonInputText());

		AonTableButton removeButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete() );
		removeButton.setTabIndex(-2);
		FlowPanel linePanel = new FlowPanel();
		AonIntegerBox lineBox = new AonIntegerBox();
		lineBox.setVisibleLength(2);
		lineBox.setMaxLength(4);
		lineBox.setVisible(false);
		lineBox.setValue(aed.getLine());
		Label line = new Label( aed.getLine() ==null?"":AonNumberUtils.toString( aed.getLine()) );
		
		line.setStyleName(AON.CSS.aonFontSmall());
		line.addStyleName(AON.CSS.aonItalic());
		line.addStyleName(AON.CSS.aonClickableLabel());
		line.addClickHandler( e -> {
			line.setVisible(false);
			lineBox.setVisible(true);
			lineBox.setFocus(true);
			lineBox.selectAll();
		});
		lineBox.addValueChangeHandler( e -> {
			aed.setLine( lineBox.getValue() );
			wizardContent.getMainEntry().getDetails().remove(rowNumber);
			wizardContent.getMainEntry().getDetails().offerFirst( aed );
			Collections.sort(wizardContent.getMainEntry().getDetails(), (aed1, aed2) -> AonNumberUtils.compare(aed1.getLine(), aed2.getLine()));
			for (int i = 0; i < wizardContent.getMainEntry().getDetails().size(); i++) {
				wizardContent.getMainEntry().getDetails().get(i).setLine(i + 1);
			}
			paint( options, this.wizardContent );
		});
		lineBox.addBlurHandler( e -> {
			line.setVisible(true);
			lineBox.setVisible(false);	
		});
		linePanel.add( line );
		linePanel.add( lineBox );
		
		row.addCell( linePanel )
			.addCell( detailAccountBox )
			.addCell( conceptSuggestBox )
			.addCell( debitBox, AON.CSS.aonTextRight() )
			.addCell( creditBox, AON.CSS.aonTextRight() )
			.addCell( balancingAccountBox) 
			.addCell( documentBox )
			.addCell( removeButton )
		;
		
		// -------------------------------------------------------------- EVENTS
		// ---------------------------------------------------- [DETAIL ACCOUNT]
		detailAccountBox.addSelectionHandler( event -> {
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
		});
		detailAccountBox.addSelectionHandler( event -> {
			if (event.getSelectedItem() != null) {
				SelectionEvent.<Account>fire(AccountEntryTable.this, event.getSelectedItem());
			}
		});
		// ----------------------------------------------------------- [CONCEPT]
		conceptSuggestBox.addValueChangeHandler(event -> {
			aed.setConcept(event.getValue());
			if (wizardContent.getMainEntry().getDetails().size() > 3 &&  isConfirmConceptChange()) {
				AonConfirmDialog cd = new AonConfirmDialog();
				cd.confirm(AON.MSG.changeConcept(),new AonConfirmDialogCallback() {
					
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
						for (int i = 0; i < wizardContent.getMainEntry().getDetails().size() ; i++ ) {
							Widget r = getWidget( i + 1 );
							if (r instanceof ComplexPanel) {
								ComplexPanel row = (ComplexPanel) r;
								Widget c = row.getWidget( COLS.CON.ordinal() );
								if (c instanceof ComplexPanel) {
									ComplexPanel cell = (ComplexPanel) c;		
									Widget box = cell.getWidget( 0 );
									if (box instanceof SuggestBox) {
										SuggestBox db = (SuggestBox) box;
										db.setValue(event.getValue());
									}
								}
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
		});
		// ------------------------------------------------------------- [DEBIT]
		debitBox.addErrorHandler(event -> {
			NativeEvent event2 = Document.get().createErrorEvent();
			debitBox.getElement().setAttribute("ERROR", AON.MSG.arithmeticExpressionError(debitBox.getText()));
			DomEvent.fireNativeEvent(event2, AccountEntryTable.this, debitBox.getElement());
		});
		debitBox.addValueChangeHandler(event -> {
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
		});
		debitBox.addKeyUpHandler(event -> {
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
		});
		// ------------------------------------------------------------ [CREDIT]
		creditBox.addErrorHandler(event -> {
			NativeEvent event2 = Document.get().createErrorEvent();
			creditBox.getElement().setAttribute("ERROR", AON.MSG.arithmeticExpressionError(creditBox.getText()));
			DomEvent.fireNativeEvent(event2, AccountEntryTable.this, creditBox.getElement());
		});
		creditBox.addValueChangeHandler(event -> {
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
		});
		
		creditBox.addKeyUpHandler(event -> {
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
		});
		// ------------------------------------------------- [BALANCING ACCOUNT]
		balancingAccountBox.addSelectionHandler( event -> {
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
		});
		balancingAccountBox.addSelectionHandler( event -> {
			if (event.getSelectedItem() != null) {
				SelectionEvent.<Account>fire(AccountEntryTable.this, event.getSelectedItem());
			}
		});
		// --------------------------------------------------- [DOCUMENT NUMBER]
		documentBox.addValueChangeHandler(event -> {
			aed.setDocumentNumber(documentBox.getValue());
			if (wizardContent.getMainEntry().getDetails().size() > 3 && isConfirmDocumentChange()) {
				AonConfirmDialog cd = new AonConfirmDialog();
				cd.confirm(AON.MSG.changeDocument(),new AonConfirmDialogCallback() {
					
					@Override
					public void onCancel() {
						setConfirmDocumentChange(false);
					}
					@Override
					public void onClose() {
						documentBox.setFocus(true);
					}
					@Override
					public void onAccept() {
						for (int i = 0; i < wizardContent.getMainEntry().getDetails().size() ; i++ ) {
							Widget r = getWidget( i + 1 );
							if (r instanceof ComplexPanel) {
								ComplexPanel row = (ComplexPanel) r;
								Widget c = row.getWidget( COLS.DOC.ordinal() );
								if (c instanceof ComplexPanel) {
									ComplexPanel cell = (ComplexPanel) c;		
									Widget box = cell.getWidget( 0 );
									if (box instanceof TextBox) {
										TextBox db = (TextBox) box;
										db.setValue(event.getValue());
									}
								}
							}
						}
						for (AccountEntryDetail aed : wizardContent.getMainEntry().getDetails() ) {
							aed.setDocumentNumber(event.getValue());
						}
						documentBox.setFocus(true);
						ValueChangeEvent.<AccountEntryDetail>fire(AccountEntryTable.this, aed);
					}
				});
			}
			ValueChangeEvent.<AccountEntryDetail>fire(AccountEntryTable.this, aed);
		});
		
		// --------------------------------------------------- [DETAIL REMOVE]
		removeButton.addClickHandler( event -> {
			if (aed.getId() == null) {
				if (wizardContent.getMainEntry().getDetails().size() > 1) {
					wizardContent.getMainEntry().getDetails().remove(aed);
					remove( row );
					refreshTotals();
				} else {
					AonMessageDialog.error("No es posible borrar, al menos debe haber una l\u00EDnea");
				}
			} else {
				aed.setId( aed.getId() * -1 );
				row.clear();
				paintDeletedRow(options,rowNumber,row, aed);
				refreshTotals();
			}
		});
		return detailAccountBox;
	}
	
	private void paintDeletedRow(AccountEntryModuleOptions options, int rowNumber, AonDisplayGridRow row, final AccountEntryDetail aed) {
		AonTableButton restoreButton = new AonTableButton(AON.MSG.undo(), AON.CSS.aonIconUndo() );
		restoreButton.setTabIndex(Integer.MAX_VALUE);
		restoreButton.addClickHandler( event -> {
			aed.setId( aed.getId() * -1 );
			row.clear();
			paintActiveRow(options, rowNumber ,row, aed);
			refreshTotals();
		});
		row.addCell(new Label())
		   .addCell(getDeletedLabel(AonStringUtils.abbreviate( aed.getAccountCode() + " " + aed.getAccountDescription() , 50)))
		   .addCell(getDeletedLabel(aed.getConcept() ))
		   .addCell(getDeletedLabel(AON.FMT.format( aed.getDebit())), AON.CSS.aonTextRight())
		   .addCell(getDeletedLabel(AON.FMT.format( aed.getCredit())), AON.CSS.aonTextRight())
		   .addCell(getDeletedLabel(aed.getBalancingAccountCode()))
		   .addCell(getDeletedLabel(aed.getDocumentNumber()))
		   .addCell(restoreButton)
		;
	}

	private void refreshTotals() {
		boolean equals = AonMathUtils.isZero( getSumDif(true) );
		sumDebit.setStyleName(equals?AON.CSS.aonColorGreen():AON.CSS.aonColorRed());
		sumCredit.setStyleName(equals?AON.CSS.aonColorGreen():AON.CSS.aonColorRed());
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
	
	private Label getDeletedLabel(String text) {
		Label label = new Label(text);
		label.setStyleName(AON.CSS.aonItalic());
		label.addStyleName(AON.CSS.aonTextLineThrough());
		return label;
	}
	
	private void paintUnmodifiableRow(AonDisplayGridRow row, final AccountEntryDetail aed) {
		Label line = new Label( aed.getLine() ==null?"":AonNumberUtils.toString( aed.getLine()));
		line.setStyleName(AON.CSS.aonFontSmall());
		line.addStyleName(AON.CSS.aonItalic());
		row.addCell( line )
			.addCell( new Label(AonStringUtils.abbreviate( aed.getAccountCode() + " " + aed.getAccountDescription() , 50)))
			.addCell( new Label(aed.getConcept() ))
			.addCell( new Label(AON.FMT.format( aed.getDebit()) ), AON.CSS.aonTextRight())
			.addCell( new Label(AON.FMT.format( aed.getCredit()) ), AON.CSS.aonTextRight())
			.addCell( new Label(aed.getBalancingAccountCode()))
			.addCell( new Label(aed.getDocumentNumber() ))
			.addCell( new Label())
		;
	}
	
	private boolean canAddLine() {
		if (!wizardContent.isUpdatable()) return false; 
		if (wizardContent.getMainEntry().getDetails() == null || wizardContent.getMainEntry().getDetails().isEmpty()) return false;
		if (wizardContent.getMainEntry().getDetailsSize() == 1) return true;
		return AonMathUtils.isNotZero( getSumDif(false) );
	}

	private void addLine( AccountEntryModuleOptions options, int rowNumber) {
		AccountEntryDetail aed = wizardContent.getMainEntry().getLastDetail();
		AccountEntryDetail newDetail = new AccountEntryDetail();
		if (aed != null) {
			newDetail.setConcept(aed.getConcept());
			newDetail.setDocumentNumber(aed.getDocumentNumber());
			int line = -1;
			for ( AccountEntryDetail d : wizardContent.getMainEntry().getDetails()) {
				line = (line > d.getLine()) ? line : d.getLine(); 
			}
			newDetail.setLine(++line);
			
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
		paintRow(options, rowNumber, newDetail);
		refreshTotals();
		if (newDetail.getAccount() != null) {
			ValueChangeEvent.<AccountEntryDetail>fire(AccountEntryTable.this, aed);
		}
		Scheduler.get().scheduleDeferred(() -> AccountEntryTable.this.setFocus(true));
	}

	@Override
	public HandlerRegistration addErrorHandler(ErrorHandler handler) {
		return addHandler(handler, ErrorEvent.getType());
	}

	@Override
	public int getTabIndex() {
		return focusable == null? -1 : focusable.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		if (focusable != null) {
			focusable.setAccessKey(key);
		}
	}

	@Override
	public void setFocus(boolean focused) {
		if (focusable != null) {
			focusable.setFocus(true);
		}
	}

	@Override
	public void setTabIndex(int index) {
		if (focusable != null) {
			focusable.setTabIndex(index);
		}
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Account> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<AccountEntryDetail> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
	
}

package com.esferalia.aon.gwt.payroll.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.TextCell;
import com.esferalia.aon.gwt.payroll.shared.BankAccount;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.LongBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CretaDBADialog extends SelectDialog<CCC> {

	interface Binder extends UiBinder<Widget, CretaDBADialog> {

	}

	private static final Binder binder = GWT.create(Binder.class);


	private static class BankAccountSuggestOracle extends MultiWordSuggestOracle{
		
		private Map<String,BankAccount> bankAccounts = new HashMap<>(); 
		
		@Override
		protected MultiWordSuggestion createSuggestion(String replacementString,
				String displayString) {
			return super.createSuggestion(getAccount(replacementString), displayString);
		}
		
		@Override
		public void clear() {
			super.clear();
			bankAccounts.clear();
		}
		
		public void add(BankAccount bankAccount) {
			
			StringBuffer buffer = new StringBuffer();
			String iban = bankAccount.getAccount();
			int i = 4; 
			for  ( ; i < iban.length(); i+=4 ){
				buffer.append(iban.substring(i-4,i));
				buffer.append('.');
			}
			buffer.append(iban.substring(i-4));
			
			
			buffer.append(" [" );
			buffer.append(bankAccount.getBic());
			buffer.append("] " );
			buffer.append(bankAccount.getAlias());
			
			add(buffer.toString());
			
			bankAccounts.put(bankAccount.getAccount(), bankAccount);
		}
		
		
		public BankAccount getBankAccount(String account) {
			return bankAccounts.get(account);
		}
		
		public static String getAccount(String suggestion){
			return suggestion.substring(0,suggestion.indexOf(' ')).replace(".", "");
		}
		
	}
	
	@UiField
	LongBox authLongBox;

	@UiField
	ListBox actionTypeListBox;

	@UiField
	ListBox movementTypeListBox;

	@UiField(provided = true)
	SuggestBox ibanSuggestBox;

	@UiField
	TextBox holderTextBox;

	@UiField
	TextBox documentTextBox;

	@UiField
	ListBox documentTypeListBox;

	@UiField
	Label selectLabel;

	@UiField
	Label messagesLabel;

	@UiField
	Anchor downloadAnchor;

	private BankAccountSuggestOracle ibanOracle;

	public CretaDBADialog() {

		setCaption("Sistema de Liquidaci\u00F3n Directa (Proyecto Cret@)");

		ibanOracle = new BankAccountSuggestOracle();
		ibanSuggestBox = new SuggestBox(ibanOracle);

		setWidget(binder.createAndBindUi(this));

		movementTypeListBox.addItem(PayrollAON.MSGS.liquidationTypeC(), "C");
		movementTypeListBox.addItem(PayrollAON.MSGS.liquidationTypeS(), "S");
		movementTypeListBox.addItem(PayrollAON.MSGS.liquidationTypeA(), "A");

		actionTypeListBox.addItem(PayrollAON.MSGS.actionType1(), "1");
		actionTypeListBox.addItem(PayrollAON.MSGS.actionType2(), "2");

		documentTypeListBox.addItem(PayrollAON.MSGS.documentTypeNif(), "1");
		documentTypeListBox.addItem(PayrollAON.MSGS.documentTypeNie(), "6");
		documentTypeListBox.addItem(PayrollAON.MSGS.documentTypeCif(), "9");

		// Full CCC.
		Column<CCC, String> fullNameColumn = new Column<CCC, String>(
				new TextCell()) {
			@Override
			public String getValue(CCC ccc) {
				return getDescription(ccc);
			}
		};

		addColumn(fullNameColumn, "C\u00F3digo de Cuenta de Cotizaci\u00F3n");

		acceptButton.setEnabled(enableAccept());
	}

	// ------------------------------------------------------------------------

	@UiHandler("acceptButton")
	void onAcceptClicked(ClickEvent e) {
		if (onAccept())
			hide();
	}

	@UiHandler("acceptButton")
	void onCancelClicked(ClickEvent e) {
		hide();
	}

	@UiHandler("authLongBox")
	void onAuthCahnged(ValueChangeEvent<Long> e) {
		acceptButton.setEnabled(enableAccept());
	}
	
	@UiHandler("ibanSuggestBox")
	void onIbanCahnged(ValueChangeEvent<String> e) {
		acceptButton.setEnabled(enableAccept());
	}
	
	@UiHandler("holderTextBox")
	void onHolderCahnged(ValueChangeEvent<String> e) {
		acceptButton.setEnabled(enableAccept());
	}

	@UiHandler("documentTextBox")
	void onDocumentCahnged(ValueChangeEvent<String> e) {
		acceptButton.setEnabled(enableAccept());
	}

	@UiHandler("authLongBox")
	void onAuthCahnged(KeyUpEvent e) {
		acceptButton.setEnabled(enableAccept());
	}
	
	@UiHandler("ibanSuggestBox")
	void onIbanCahnged(KeyUpEvent e) {
		acceptButton.setEnabled(enableAccept());
	}
	
	@UiHandler("holderTextBox")
	void onHolderCahnged(KeyUpEvent e) {
		acceptButton.setEnabled(enableAccept());
	}

	@UiHandler("documentTextBox")
	void onDocumentCahnged(KeyUpEvent e) {
		acceptButton.setEnabled(enableAccept());
	}
	
	@UiHandler("documentTypeListBox")
	void onDocumentTypeCahnged(ChangeEvent e) {
		acceptButton.setEnabled(enableAccept());
	}
	
	
	// ------------------------------------------------------------------------

	public Long getAuthorized() {
		return authLongBox.getValue();
	}

	public void setAuthorized(Long authorized) {
		authLongBox.setValue(authorized);
	}

	public String getIBAN() {
		return ibanSuggestBox.getValue();
	}

	public void setIBAN(String iban) {
		ibanSuggestBox.setValue(iban);
	}

	public String getHolder() {
		return holderTextBox.getValue();
	}

	public void setHolder(String holder) {
		holderTextBox.setValue(holder);
	}

	public String getDocument() {
		return documentTextBox.getValue();
	}

	public void setDocument(String document) {
		documentTextBox.setValue(document);
	}

	public String getActionType() {
		return actionTypeListBox.getSelectedValue();
	}

	public void setActionType(String actionType) {
		setSelected(actionTypeListBox, actionType);
	}

	public String getMovementType() {
		return movementTypeListBox.getSelectedValue();
	}

	public void setMovementType(String movementType) {
		setSelected(movementTypeListBox, movementType);
	}

	public String getDocumentType() {
		return documentTypeListBox.getSelectedValue();
	}

	public void setDocumentType(String documentType) {
		setSelected(documentTypeListBox, documentType);
	}

	public void setBankAccounts(Collection<BankAccount> bankAccounts) {
		
		ibanOracle.clear();
		
		for (BankAccount bankAccount : bankAccounts)
			ibanOracle.add(bankAccount);
		
	}

	// ------------------------------------------------------------------------

	public void download(String fileName, String url) {
		downloadAnchor.setHref(url);
		downloadAnchor.setTarget(fileName);
		downloadAnchor.getElement().setAttribute("download", fileName);
		click(downloadAnchor.getElement());

	}
	// ------------------------------------------------------------------------

	protected boolean onAccept() {
		return true;
	}

	protected String getDescription(CCC ccc) {
		return ccc.getCode();
	}

	// ------------------------------------------------------------------------

	@Override
	protected boolean enableAccept() {
		try {
			checkAuth();
			checkIBAN();
			checkNIF();
			checkHolder();
			checkCCCs();
			hide(messagesLabel, true);
			return true;
		} catch ( Exception e ){
			hide(messagesLabel, false);
			messagesLabel.setText(e.getMessage());
			return false;
		}
		
	}
	
	// ------------------------------------------------------------------------

	private void checkNIF() throws Exception{
		if ( AonStringUtils.isBlank(documentTextBox.getText()))
			throw new Exception("Documento vacio.");
		
		char document [] = documentTextBox.getText().toCharArray();
		
		switch (documentTypeListBox.getSelectedValue()) {
		case "6":
			try {
				if ( AonDocumentUtil.isValidNIE(document) )
					return;
			}
			catch ( Exception e ){
			}
			throw new Exception("Documento ( NIE ) del titular no v\u00E1lido.");
		case "9":
			try {
				if ( AonDocumentUtil.isValidCIF(document) )
					return;
			}
			catch ( Exception e ){
			}
			throw new Exception("Documento ( CIF ) del titular no v\u00E1lido.");
		default:
			try {
				if ( AonDocumentUtil.isValidNIF(document) )
					return;
			}catch(Exception e){
			}
			throw new Exception("Documento ( NIF ) del titular no v\u00E1lido.");
		}
	}

	private void checkCCCs() throws Exception{
		if ( getSelectedData().isEmpty() )
			throw new Exception("Debe seleccionar al menos un C\u00F3digo de Cuenta de Cotizaci\u00F3n (CCC).");
	}

	private void checkAuth() throws Exception{
		if ( authLongBox.getValue() == null )
			throw new Exception("Autorizado no v\u00E1lido. Debe ser un n\u00FAmero que contenga 8 d\u00EDgitos o menos.");
			
	}

	private void checkIBAN() throws Exception{
		String iban = ibanSuggestBox.getValue();
		if ( AonStringUtils.isBlank(iban) || iban.length() != 24)
			throw new Exception("IBAN no v\u00E1lido.");
	}

	private void checkHolder() throws Exception{
		if ( AonStringUtils.isBlank(holderTextBox.getText()) )
			throw new Exception("Apellidos y Nombre o Raz\u00F3n Social del titular vacio.");
		
	}

	// ------------------------------------------------------------------------

	private static native void click(Element a)/*-{
		a.click();
	}-*/;

	private static void hide(Widget widget, boolean hide){
		if ( hide )
			widget.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		else 
			widget.getElement().getStyle().clearVisibility();
	}
	
	private static void setSelected(ListBox listBox, String string) {
		for (int i = 0; i < listBox.getItemCount(); i++) {
			if (listBox.getValue(i).equals(string)) {
				listBox.setSelectedIndex(i);
				return;
			}
		}
	}

}

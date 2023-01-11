package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.client.Constants.DESCRIPTION_MAX_LENGTH;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AgreementSuggestPaymentDialog extends AonCustomDialog {
	
	interface AgreementSuggestPaymentDialogBinder extends UiBinder<Widget, AgreementSuggestPaymentDialog> {}

	private static final AgreementSuggestPaymentDialogBinder binder = GWT.create(AgreementSuggestPaymentDialogBinder.class);
	
	// --------------------- PaymentSuggestionDisplay
	
	private class PaymentSuggestionDisplay extends AbstractItemSuggestionDisplay<Payment> {

		@Override
		Payment getItem(String replacementString) {
			for (Payment paymentIt : availablePaymens) {
				String suggestion = getSuggestionString(paymentIt);
				if (AonStringUtils.equals(replacementString, suggestion))
					return paymentIt;
			}
			return null;
		}
	}
	
	private static String getSuggestionString(Payment payment) {
		return SalaryDraft.getSuggestionString(payment);
	}
	
	// --------------------- UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String button();
	}

	@UiField
	HTMLPanel messagePanel;
	
	@UiField
	HTMLPanel suggestPanel;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// --------------------- Variables
	
	private DomainEmployeesServiceAsync impl = DomainEmployeesServiceAsync.newInstance();
	List<Payment> availablePaymens;
	MultiWordSuggestOracle paymentDescriptionOracle;
	PaymentSuggestionDisplay paymentSuggestionDisplay;
	SuggestBox descriptionSuggest;
 	
	private Payment payment;
	
	private Button manualAgreement;
	private Button acceptBtn;
	
	// --------------------- Constructor
	
	protected AgreementSuggestPaymentDialog() {
		setCaption("Devengos Predefinidos");
		setWidget(binder.createAndBindUi(this));
		getButtonsPanel();
		
		this.showCloseButton(true);
		setEnabled(acceptBtn, false);
		setEnabled(manualAgreement, true);
		availablePaymens = new ArrayList<>();
		paymentDescriptionOracle = new MultiWordSuggestOracle();
		paymentSuggestionDisplay = new PaymentSuggestionDisplay();
		
		payment = new Payment();
		Random rand = new Random();
		int newPaymentId = rand.nextInt(1000) * -1;
		if(newPaymentId > 0) newPaymentId = newPaymentId * -1;
		this.payment.setId(newPaymentId);
		
		impl.getAvailablePayments(Integer.MAX_VALUE, new AsyncCallback<List<Payment>>() {
			
			@Override
			public void onSuccess(List<Payment> payments) {
				createSuggestBox(payments);
				showDialog();
			}

			@Override
			public void onFailure(Throwable caught) {
				showError("Carga devengos", caught.getMessage());
			}
		});
	}

	// --------------------- CreateSuggestBox

	private void createSuggestBox(List<Payment> payments) {
		suggestPanel.clear();
		initSuggestBox(payments);
		
		TextBox descriptionBox = new TextBox();
		descriptionBox.setMaxLength(DESCRIPTION_MAX_LENGTH);
		
		descriptionSuggest = new SuggestBox(paymentDescriptionOracle, descriptionBox, paymentSuggestionDisplay);
		descriptionSuggest.setAutoSelectEnabled(false);
		descriptionSuggest.getElement().getStyle().setWidth(98, Unit.PCT);
		
		descriptionSuggest.addSelectionHandler(event -> {
			Suggestion suggestion = event.getSelectedItem();
			Payment concept = getPayment(suggestion.getReplacementString());
			
			if (concept == null)
				showError("Error devengo", "No se ha podido obtener el devengo");

			payment.setType(concept.getType());
			payment.setName(concept.getName());
			payment.setConceptId(concept.getId());
			payment.setDescription(concept.getDescription());
			payment.setIrpfExpression(concept.getIrpfExpression());
			payment.setQuoteExpression(concept.getQuoteExpression());
			payment.setSalaryType(Salary.Type.SALARY);
			if (concept.getExpression() != null) payment.setExpression(getExpression4Payment(concept));
			setEnabled(acceptBtn, true);
			setEnabled(manualAgreement, false);
			
		});
		
		descriptionSuggest.addKeyDownHandler(event -> {
			if (KeyCodes.KEY_ESCAPE == event.getNativeEvent().getKeyCode())
				paymentSuggestionDisplay.hideSuggestions();
			else if (event.isControlKeyDown() && KeyCodes.KEY_SPACE == event.getNativeEvent().getKeyCode())
				descriptionSuggest.showSuggestionList();
		});
		
		descriptionSuggest.getElement().setPropertyString("placeholder", "Ctrl + espacio para ver sugerencias");
		
		suggestPanel.add(descriptionSuggest);
	}
	
	// --------------------- Initialize PaymentSuggestionDisplay
	
	private void initSuggestBox(List<Payment> paymentsList) {
		availablePaymens.addAll(paymentsList);
		paymentDescriptionOracle.clear();
		
		List<String> suggestionList = new ArrayList<>();
		
		for (Payment paymentIt : availablePaymens) {
			String suggestion = getSuggestionString(paymentIt);
			if (AonStringUtils.isEmpty(suggestion))
				continue;
			suggestionList.add(suggestion);
			paymentDescriptionOracle.add(suggestion);
		}
		
		paymentDescriptionOracle.setDefaultSuggestionsFromText(suggestionList);
	}
	
	// --------------------- Auxiliar methods
	
	private static String getExpression4Payment(Payment concept) {
		String expression = concept.getExpression();
		
		Payment.Type type = concept.getType(); 
		if ( type == Payment.Type.CRA_0055 
			|| type ==  Payment.Type.CRA_0056 ) 
			expression = expression.replaceAll("REMOVE", "HIDE");
		
		return expression;
	}
	
	private Payment getPayment(String suggestionString) {
		for (Payment paymentIt : availablePaymens) {
			if (AonStringUtils.equalsIgnoreCase(suggestionString, getSuggestionString(paymentIt)))
				return paymentIt;
		}
		return null;
	}
	
	private void setEnabled(Button button, boolean enabled) {
		button.setEnabled(enabled);
		if(!enabled) {
			button.getElement().getStyle().setVisibility(Visibility.VISIBLE);
			button.getElement().getStyle().setDisplay(Display.BLOCK);
		}
	}
	
	// --------------------- Accept dialog method
	
	private void getButtonsPanel() {
		manualAgreement = new Button();
		manualAgreement.setText("Modo manual");
		manualAgreement.setStyleName(AON.CSS.aonIconEditNote());
		manualAgreement.addStyleName(style.button());
		manualAgreement.addClickHandler(e -> onManualEdition());
		buttonsPanel.add(manualAgreement);
		
		acceptBtn = new Button();
		acceptBtn.setText("Aceptar");
		acceptBtn.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtn.addClickHandler(e -> {
			hide();
			onAccept(payment);
		});
		
		buttonsPanel.add(acceptBtn);
	}
	
	// --------------------- Show dialog
	
	public void showDialog() {
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
			descriptionSuggest.setFocus(true);
		});
	}

	// --------------------- Message panel
	
	private void showError(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(messagePanel, errorMap);
	}
	
	// --------------------- Abstract method
	
	protected abstract void onAccept(Payment payment);
	protected abstract void onManualEdition();
	
}

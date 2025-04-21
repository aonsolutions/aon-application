package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.AgreementIntegrity;
import com.esferalia.aon.gwt.payroll.shared.AgreementIntegrityFix;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class AgreementIntegrityDialog extends AonCustomDialog {
	
	// Service
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	// UI
	private HTMLPanel body = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel container = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel messagePanel = new HTMLPanel(AonStringUtils.EMPTY);
	private AonCustomListBox agreementLB = new AonCustomListBox("Convenio");
	
	private HTMLPanel contentPanel = new HTMLPanel(AonStringUtils.EMPTY);
	private ScrollPanel scrollPanel;
	private HTMLPanel integrityContainer = new HTMLPanel(AonStringUtils.EMPTY);
	
	private Map<AgreementIntegrityFix, Boolean> isPaymentOpen = new HashMap<AgreementIntegrityFix, Boolean>();
	
	// Variables
	private Integer domain;
	private List<Agreement> agreements = new ArrayList<Agreement>();
	private AgreementIntegrity agreementIntegrity;

	public AgreementIntegrityDialog() {
		super();
		
		showCloseButton(true);
		
		// Style for glass dialog
		getElement().getStyle().setProperty("z-index", "8");
		setGlassStyleName(AON.CSS.aonDialogGlass());
		
		// Caption
		setCaption("Integridad Convenios");
		
		getAgreements(agreementsDB -> {
			initView();
			
			center();
			show();
		});
	}
	
	private void initView() {
		body.clear();
		body.addStyleName(AON.CSS.aonFlexColumn());
		body.getElement().getStyle().setProperty("width", "100%");
		body.getElement().getStyle().setProperty("height", "100%");
		body.getElement().getStyle().setProperty("overflow", "auto");
		
		messagePanel.setWidth("100%");
		body.add(messagePanel);
		
		container.clear();
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("padding", "1rem 1rem 0 1rem");
		
		body.add(container);
		
		contentPanel.getElement().getStyle().setProperty("padding", "0 1rem 1rem 1rem");
		body.add(contentPanel);
		
		initAgreementListBox();
		
		body.add(createFixButton());
		
		add(body);
	}

	private Widget createFixButton() {
		HTMLPanel buttonPanel = new HTMLPanel(AonStringUtils.EMPTY);
		buttonPanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
		buttonPanel.getElement().getStyle().setProperty("padding", "0 1rem 1rem");
		
		Button fixIntegrityBtn = new Button();
		fixIntegrityBtn.setStyleName(AON.CSS.aonIconRepair());
		fixIntegrityBtn.addStyleName(AON.CSS.aonButtonIconText());
		fixIntegrityBtn.setText("Corregir integirdad convenio");
		fixIntegrityBtn.addClickHandler(e -> fixIntegrityAgreement());
		
		buttonPanel.add(fixIntegrityBtn);
		
		return buttonPanel;
	}

	private void initAgreementListBox() {
		Label agreementLabel = new Label("Seleccione un convenio para iniciar la integridad:");
		agreementLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		agreementLabel.setWidth("100%");
		
		container.add(agreementLabel);
		
		agreementLB.clearItems();
		agreementLB.addItem("-", "");
		agreements.forEach(agreement -> agreementLB.addItem((agreement.getDomain() != domain ? "(C) " : "=> ") + agreement.getDescription().toUpperCase(), agreement.getId().toString()));
		
		agreementLB.addChangeHandler(e -> onAgreementChange());
		
		container.add(agreementLB);
	}

	private void onAgreementChange() {
		if(AonStringUtils.isBlank(agreementLB.getValue())) {
			contentPanel.clear();
			center();
			return;
		}
		
		Optional<Agreement> agreement = agreements.stream().filter(agreementIt -> agreementIt.getId() == Integer.parseInt(agreementLB.getValue())).findFirst();
		if(agreement.isEmpty()) AonMessagePanel.showWarning(messagePanel, "No se ha podido encontrar el convenio [" + agreementLB.getValue() + "]");
		
		AonMessagePanel.showLoading(messagePanel, "Comprobando integridad del convenio " + agreement.get().getDescription() + " ...");
		checkAgreementIntegrity(agreementIntegrity -> createAgreementIntegrity());
	}

	private void createAgreementIntegrity() {
		AonMessagePanel.showSuccess(messagePanel, "Integridad completada correctamente");
		
		if(null != contentPanel) contentPanel.clear();
		
		integrityContainer.clear();
		integrityContainer.addStyleName(AON.CSS.aonItemFlex());
		integrityContainer.addStyleName(AON.CSS.aonFlexColumn());
		integrityContainer.setWidth("100%");
		integrityContainer.getElement().getStyle().setProperty("max-height", "30rem");
		
		isPaymentOpen.put(AgreementIntegrityFix.OTHER_DOMAIN_AGREEMENT_PAYMENTS, false);
		isPaymentOpen.put(AgreementIntegrityFix.AGREEMENT_PAYMENTS_WITHOUT_PAYMENT_CONCEPT, false);
		isPaymentOpen.put(AgreementIntegrityFix.AGREEMENT_PAYMENTS_WITH_OTHER_DOMAIN_PAYMENT_CONCEPT, false);
		isPaymentOpen.put(AgreementIntegrityFix.NO_CODE_WRONG_CODE_PAYMENT_CONCEPT, false);
		isPaymentOpen.put(AgreementIntegrityFix.PAYMENT_CONCEPT_CODE_AS_VAR_IN_EXPRESSION, false);
		isPaymentOpen.put(AgreementIntegrityFix.AGREEMENT_VARIABLES_AS_PAYMENT_CONCEPT_CODE, false);
		isPaymentOpen.put(AgreementIntegrityFix.AGREEMENT_PAYMENT_CONCEPT_CODE_AS_CONTEXT_VARIABLE, false);
		isPaymentOpen.put(AgreementIntegrityFix.AGREEMENT_PAYMENT_WRONG_EXPRESSION, false);
		
		isPaymentOpen.put(AgreementIntegrityFix.CONTRACT_PAYMENTS_WITH_OTHER_DOMAIN_PAYMENT_CONCEPT, false);
		isPaymentOpen.put(AgreementIntegrityFix.CONTRACT_PAYMENTS_PAYMENT_CONCEPTS_WITHOUT_CODE, false);
		
		isPaymentOpen.put(AgreementIntegrityFix.PAYMENT_CONCEPT_NO_REFERENCE, false);
		
		isPaymentOpen.put(AgreementIntegrityFix.AGREEMENT_EXTRA_WRONG_FORMAT_PERIOD, false);
		isPaymentOpen.put(AgreementIntegrityFix.AGREEMENT_EXTRA_START_END, false);
		
		integrityContainer.add(createMessagesPanel(AgreementIntegrityFix.OTHER_DOMAIN_AGREEMENT_PAYMENTS));
		integrityContainer.add(createMessagesPanel(AgreementIntegrityFix.AGREEMENT_PAYMENTS_WITHOUT_PAYMENT_CONCEPT));
		integrityContainer.add(createMessagesPanel(AgreementIntegrityFix.AGREEMENT_PAYMENTS_WITH_OTHER_DOMAIN_PAYMENT_CONCEPT));
		integrityContainer.add(createMessagesPanel(AgreementIntegrityFix.NO_CODE_WRONG_CODE_PAYMENT_CONCEPT));
		integrityContainer.add(createMessagesPanel(AgreementIntegrityFix.PAYMENT_CONCEPT_CODE_AS_VAR_IN_EXPRESSION));
		integrityContainer.add(createMessagesPanel(AgreementIntegrityFix.AGREEMENT_VARIABLES_AS_PAYMENT_CONCEPT_CODE));
		integrityContainer.add(createMessagesPanel(AgreementIntegrityFix.AGREEMENT_PAYMENT_CONCEPT_CODE_AS_CONTEXT_VARIABLE));
		integrityContainer.add(createMessagesPanel(AgreementIntegrityFix.AGREEMENT_PAYMENT_WRONG_EXPRESSION));
		
		integrityContainer.add(createMessagesPanel(AgreementIntegrityFix.CONTRACT_PAYMENTS_WITH_OTHER_DOMAIN_PAYMENT_CONCEPT));
		integrityContainer.add(createMessagesPanel(AgreementIntegrityFix.CONTRACT_PAYMENTS_PAYMENT_CONCEPTS_WITHOUT_CODE));
		integrityContainer.add(createMessagesPanel(AgreementIntegrityFix.PAYMENT_CONCEPT_NO_REFERENCE));
		
		integrityContainer.add(createMessagesPanel( AgreementIntegrityFix.AGREEMENT_EXTRA_WRONG_FORMAT_PERIOD));
		integrityContainer.add(createMessagesPanel( AgreementIntegrityFix.AGREEMENT_EXTRA_START_END));
		
		scrollPanel = new ScrollPanel(integrityContainer);
		scrollPanel.setWidth("100%");
		
		contentPanel.add(scrollPanel);
		
		center();
	}
	
	private Widget createMessagesPanel(AgreementIntegrityFix agreementIntegrityFix) {
		List<String> messages = agreementIntegrity.getMessages().get(agreementIntegrityFix);
		
		HTMLPanel paymentDiscPanel = new HTMLPanel(AonStringUtils.EMPTY);
		paymentDiscPanel.getElement().getStyle().setCursor(Cursor.POINTER);
		paymentDiscPanel.getElement().getStyle().setProperty("justify-content", "center");
		paymentDiscPanel.addStyleName(AON.CSS.aonItemFlex());
		paymentDiscPanel.addStyleName(AON.CSS.aonFlexColumn());
		paymentDiscPanel.setWidth("100%");
		
		HTMLPanel paymentPanel = new HTMLPanel(AonStringUtils.EMPTY);
		paymentPanel.addStyleName(AON.CSS.aonFlexColumn());
		paymentPanel.setWidth("100%");
		
		// Toolbar
		HTMLPanel toolbar = new HTMLPanel(AonStringUtils.EMPTY);
		toolbar.addStyleName(AON.CSS.aonItemFlex());
		toolbar.addStyleName(AON.CSS.aonFlexBetween());
		toolbar.addStyleName(AON.CSS.aonAlignItemsCenter());
		toolbar.setWidth("100%");
		
		HTMLPanel toolbarTitle = new HTMLPanel(AonStringUtils.EMPTY);
		toolbarTitle.addStyleName(AON.CSS.aonItemFlex());
		toolbarTitle.addStyleName(AON.CSS.aonAlignItemsCenter());
		toolbarTitle.setWidth("100%");
		toolbar.add(toolbarTitle);
		
		AonToolbarSmallButton paymentDiscBtn = new AonToolbarSmallButton(
				messages.isEmpty() ? "Integridad correcta" : "Desplegar Devengos", 
				messages.isEmpty() ? AON.CSS.aonIconCircleGreen() : AON.CSS.aonIconRight()
		);
		
		AonToolbarSmallButton fixIntegrity = new AonToolbarSmallButton("Corregir integridad correcta", AON.CSS.aonIconRepair());
		fixIntegrity.addClickHandler(e -> onAgreementIntegrityFix(agreementIntegrityFix));
		fixIntegrity.setVisible(false);
		
		Label titleLabel = new Label(agreementIntegrityFix.getTitle());
		titleLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		if(!messages.isEmpty()) {
			toolbarTitle.add(paymentDiscBtn);
		} else titleLabel.getElement().getStyle().setProperty("padding-left", "2rem");
		
		toolbarTitle.add(titleLabel);
		
		paymentDiscPanel.add(toolbar);
		
		if(!messages.isEmpty()) {
			toolbar.add(fixIntegrity);
			
			Label paymentCount = new Label(messages.size() + "");
			paymentCount.addStyleName(AON.CSS.aonBadge());
			toolbar.add(paymentCount);
			
			toolbarTitle.addDomHandler(new ClickHandler() {
			    @Override
			    public void onClick(ClickEvent event) {
			    	isPaymentOpen.put(agreementIntegrityFix, !isPaymentOpen.get(agreementIntegrityFix));
					handleIcon(paymentDiscBtn, isPaymentOpen.get(agreementIntegrityFix));
					if(isPaymentOpen.get(agreementIntegrityFix)) {
						paymentPanel.getElement().getStyle().clearDisplay();
						fixIntegrity.setVisible(/* true */ isIntegrityAviable(agreementIntegrityFix));
					} else {
						paymentPanel.getElement().getStyle().setDisplay(Display.NONE);
						fixIntegrity.setVisible(false);
					}
					center();
			    }

				private boolean isIntegrityAviable(AgreementIntegrityFix agreementIntegrityFix) {
					return  agreementIntegrityFix != AgreementIntegrityFix.AGREEMENT_PAYMENT_CONCEPT_CODE_AS_CONTEXT_VARIABLE && 
							agreementIntegrityFix != AgreementIntegrityFix.CONTRACT_PAYMENTS_WITH_OTHER_DOMAIN_PAYMENT_CONCEPT && 
							agreementIntegrityFix != AgreementIntegrityFix.CONTRACT_PAYMENTS_PAYMENT_CONCEPTS_WITHOUT_CODE &&
							agreementIntegrityFix != AgreementIntegrityFix.AGREEMENT_PAYMENT_WRONG_EXPRESSION;
				}
				
			}, ClickEvent.getType());
			
		} else toolbar.add(paymentDiscBtn);
		
		// Content
		messages.forEach(variable -> {
			Label description = new Label(variable);
			description.getElement().getStyle().setProperty("padding", "0 2rem");
			
			paymentPanel.add(description);
		});
		paymentPanel.getElement().getStyle().setDisplay(Display.NONE);
		
		paymentDiscPanel.add(paymentPanel);
		
		return paymentDiscPanel;
	}

	private void handleIcon(AonToolbarSmallButton button, boolean open) {
		if (open) {
			button.removeStyleName(AON.CSS.aonIconRight());
			button.addStyleName(AON.CSS.aonIconDown());
		} else {
			button.removeStyleName(AON.CSS.aonIconDown());
			button.addStyleName(AON.CSS.aonIconRight());
		}
	}
	
	private void onAgreementIntegrityFix(AgreementIntegrityFix agreementIntegrityFix) {
		AonDialog dialog = new AonDialog("Integridad Convenio", new HTML(agreementIntegrityFix.getDeleteMessage()));
		dialog.confirm(new AonAcceptDialogCallback() {

			@Override
			public void onCancel() {}

			@Override
			public void onAccept() {
				AonMessagePanel.showLoading(messagePanel, "Arreglando integridad del convenio...");
				
				agreementIntegrityFix(agreementIntegrityFix, end -> onAgreementChange());
			}
		});
	}

	private void fixIntegrityAgreement() {
		AonDialog dialog = new AonDialog("Integridad Convenio", new HTML("\u00bfDesea arreglar la integridad del convenio\u003f"));
		dialog.confirm(new AonAcceptDialogCallback() {

			@Override
			public void onCancel() {}

			@Override
			public void onAccept() {
				AonMessagePanel.showLoading(messagePanel, "Arreglando integridad del convenio...");
				agreementIntegrityFix(null, end -> onAgreementChange());
			}
		});
	}
	
	private void getAgreements(Consumer<List<Agreement>> consumer) {
		impl.getDomain(new AsyncCallback<Integer>() {
			
			@Override
			public void onSuccess(Integer currentDomain) {
				domain = currentDomain;
				
				impl.getAgreements(false, new AsyncCallback<List<Agreement>>() {
					
					@Override
					public void onSuccess(List<Agreement> agreementsDB) {
						agreements = agreementsDB;
						agreements.sort((o1, o2) -> o2.getDomain().compareTo(o1.getDomain()));
						consumer.accept(agreementsDB);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						new AonDialog("Convenios", new Label("Error obteniendo convenios")).info();
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
		
		
	}
	
	private void checkAgreementIntegrity(Consumer<AgreementIntegrity> consumer) {
		Integer agreementId = Integer.parseInt(agreementLB.getValue());
		
		impl.checkAgreementIntegrity(agreementId, new AsyncCallback<AgreementIntegrity>() {
			
			@Override
			public void onSuccess(AgreementIntegrity agreementIntegrityDB) {
				agreementIntegrity = agreementIntegrityDB;
				consumer.accept(agreementIntegrityDB);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error comprobando integridad : " + caught.getMessage());
			}
		});
	}
	
	private void agreementIntegrityFix(AgreementIntegrityFix agreementIntegrityFix, Consumer<Void> consumer) {
		Integer agreementId = Integer.parseInt(agreementLB.getValue());
		
		impl.agreementIntegrityFix(agreementId, agreementIntegrityFix, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				consumer.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error arreglando integridad : " + caught.getMessage());
			}
		});
	}
	
}

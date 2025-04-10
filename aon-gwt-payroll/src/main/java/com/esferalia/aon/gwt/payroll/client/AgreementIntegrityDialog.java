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
	
	private Map<Integer, Boolean> isPaymentOpen = new HashMap<Integer, Boolean>();
	
	// Variables
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
		
		add(body);
	}

	private void initAgreementListBox() {
		Label agreementLabel = new Label("Seleccione un convenio para iniciar la integridad:");
		agreementLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		agreementLabel.setWidth("100%");
		
		container.add(agreementLabel);
		
		agreementLB.clearItems();
		agreementLB.addItem("-", "");
		agreements.forEach(agreement -> agreementLB.addItem(agreement.getDescription().toUpperCase(), agreement.getId().toString()));
		
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
		
		isPaymentOpen.put(0, false);
		isPaymentOpen.put(1, false);
		isPaymentOpen.put(2, false);
		isPaymentOpen.put(3, false);
		isPaymentOpen.put(4, false);
		isPaymentOpen.put(5, false);
		isPaymentOpen.put(6, false);
		isPaymentOpen.put(7, false);
		isPaymentOpen.put(8, false);
		isPaymentOpen.put(9, false);
		isPaymentOpen.put(10, false);
		
		integrityContainer.add(createPaymentDomainsPanel());
		integrityContainer.add(createNoPaymentConceptAgreementPaymentsPanel());
		integrityContainer.add(createOtherDomainPaymentConceptsPanel());
		integrityContainer.add(createPaymentConceptsNoCodePanel());
		integrityContainer.add(createCodeInExpressionPanel());
		integrityContainer.add(createVariablesLikeCodePanel());
		integrityContainer.add(createVariableCodeLikeContextPanel());
		
		integrityContainer.add(createOtherDomainPaymentConceptContractsPanel());
		integrityContainer.add(createPaymentConceptsNoCodeContractsPanel());
		integrityContainer.add(createPaymentConceptsNoRefPanel());
		
		integrityContainer.add(createAgreementExtrasPanel());
		
		scrollPanel = new ScrollPanel(integrityContainer);
		scrollPanel.setWidth("100%");
		
		contentPanel.add(scrollPanel);
		
		center();
	}

	private Widget createPaymentDomainsPanel() {
		return createMessagesPanel("Devengos de convenio en otro dominio", agreementIntegrity.getOtherDomainAgreementPayments(), 0);
	}
	
	private Widget createNoPaymentConceptAgreementPaymentsPanel() {
		return createMessagesPanel("Devengos de convenio sin concepto", agreementIntegrity.getNoPaymentConceptAgreementPayments(), 1);
	}
	
	private Widget createOtherDomainPaymentConceptsPanel() {
		return createMessagesPanel("Conceptos de convenio en otro dominio", agreementIntegrity.getOtherDomainPaymentConcepts(), 2);
	}

	private Widget createPaymentConceptsNoCodePanel() {
		return createMessagesPanel("Conceptos sin c\u00f3digo / C\u00f3digo err\u00f3neo", agreementIntegrity.getPaymentConceptsNoCode(), 3);
	}

	private Widget createCodeInExpressionPanel() {
		return createMessagesPanel("C\u00f3digo concepto usado en expresiones", agreementIntegrity.getCodeInExpression(), 4);
	}
	
	private Widget createVariablesLikeCodePanel() {
		return createMessagesPanel("Variables no usadas en expres\u00f3n iguales a c\u00f3digos conceptos", agreementIntegrity.getVariableLikeCodes(), 5);
	}
	
	private Widget createVariableCodeLikeContextPanel() {
		return createMessagesPanel("C\u00f3digo concepto / Variables iguales a variables de contexto", agreementIntegrity.getVariableCodeLikeContext(), 6);
	}

	private Widget createOtherDomainPaymentConceptContractsPanel() {
		return createMessagesPanel("Devengos contrato con conceptos en otro dominio", agreementIntegrity.getOtherDomainPaymentConceptContracts(), 7);
	}

	private Widget createPaymentConceptsNoCodeContractsPanel() {
		return createMessagesPanel("Devengos contrato con conceptos sin c\u00f3digo", agreementIntegrity.getPaymentConceptsNoCodeContracts(), 8);
	}

	private Widget createPaymentConceptsNoRefPanel() {
		return createMessagesPanel("Conceptos del dominio sin referencia al convenio o contrato", agreementIntegrity.getPaymentConceptsNoRef(), 9);
	}
	
	private Widget createAgreementExtrasPanel() {
		return createMessagesPanel("Extras con formato err\u00f3neo en las fechas / Fechas > 12 meses", agreementIntegrity.getAgreementExtras(), 10);
	}
	
	private Widget createMessagesPanel(String title, List<String> messages, Integer isOpenIdx) {
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
		
		AonToolbarSmallButton fixIntegrity = new AonToolbarSmallButton("Corregir integridad correcta", AON.CSS.aonIconFix());
		fixIntegrity.addClickHandler(e -> onAgreementIntegrityFix(isOpenIdx));
		fixIntegrity.setVisible(false);
		
		Label titleLabel = new Label(title);
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
			    	isPaymentOpen.put(isOpenIdx, !isPaymentOpen.get(isOpenIdx));
					handleIcon(paymentDiscBtn, isPaymentOpen.get(isOpenIdx));
					if(isPaymentOpen.get(isOpenIdx)) {
						paymentPanel.getElement().getStyle().clearDisplay();
						fixIntegrity.setVisible(/* true */ isIntegrityAviable(isOpenIdx));
					} else {
						paymentPanel.getElement().getStyle().setDisplay(Display.NONE);
						fixIntegrity.setVisible(false);
					}
					center();
			    }

				private boolean isIntegrityAviable(Integer idx) {
					return idx == 0 || idx == 1 || idx == 2;
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
	
	private void onAgreementIntegrityFix(Integer agreementIntegrityFixIdx) {
		AonMessagePanel.showLoading(messagePanel, "Arreglando integridad del convenio...");
		
		AgreementIntegrityFix agreementIntegrityFix = null;
		if(null != agreementIntegrityFixIdx)
			agreementIntegrityFix = AgreementIntegrityFix.values()[agreementIntegrityFixIdx];
		
		agreementIntegrityFix(agreementIntegrityFix, end -> onAgreementChange());
	}

	private void getAgreements(Consumer<List<Agreement>> consumer) {
		impl.getAgreements(false, new AsyncCallback<List<Agreement>>() {
			
			@Override
			public void onSuccess(List<Agreement> agreementsDB) {
				agreements = agreementsDB;
				consumer.accept(agreementsDB);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				new AonDialog("Convenios", new Label("Error obteniendo convenios")).info();
			}
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

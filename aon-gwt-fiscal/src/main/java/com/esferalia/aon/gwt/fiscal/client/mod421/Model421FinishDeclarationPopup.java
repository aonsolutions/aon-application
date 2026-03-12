package com.esferalia.aon.gwt.fiscal.client.mod421;

import java.util.ArrayList;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIbanTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIbanTextBox.IbanSuggestion;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.FiscalMSService;
import com.esferalia.aon.gwt.fiscal.client.FiscalMSServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalMSServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.mod421.Model421.Model421Callback;
import com.esferalia.aon.gwt.fiscal.client.widget.AonCreditorBox;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.IIbanContainer;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

class Model421FinishDeclarationPopup extends AonCustomDialog {
	
	private static FiscalMSServiceAsync service;
	static {
		FiscalMSServiceAsync serviceRaw = GWT.create(FiscalMSService.class);
		service = new FiscalMSServiceAsyncDecorator(serviceRaw);
	}
	
	public static interface FinishDeclarationPopupCallback {
		public void onAccept();
		public void onCancel();
		public void onCustomerCheck();
	}

	protected final FlexTable tab = new FlexTable();
	protected int row = 0;
	
	protected Model421FinishDeclarationPopup(final Mod421 mod421, final Model421Callback callback,FinishDeclarationPopupCallback popupCallback) {
		setCaption(AON.MSG.finish());
		setGlassEnabled(true);
		setAnimationEnabled(true);
		initializeTable();
		
		// Resultado
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		tab.setWidget(row, 0, new Label(AON.MSG.result()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.CSS.aonTextRight());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.CSS.aonFontLarger());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.CSS.aonPaddingRight());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.CSS.aonBold());
		tab.setWidget(row, 1, new Label(AON.FMT.format(mod421.getDeclarationResult())));
		row++;
		
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		Label labelDeclarationType = new Label(AON.MSG.declarationType());
		labelDeclarationType.addStyleName(AON.CSS.aonMarginTopSep());				
		tab.setWidget(row, 0, labelDeclarationType);
		
		// "Sin actividad" o "A compensar y no es ultimo periodo" 
		if (mod421.getDeclarationResultType() == FiscalModelDeclarationType.NEGATIVE
		 || (mod421.getDeclarationResultType() == FiscalModelDeclarationType.COMPENSATE && mod421.getPeriod() != Period.T4 && mod421.getPeriod() != Period.M12 && !mod421.isAeatRectification())) {
			tab.getFlexCellFormatter().addStyleName(row, 1, AON.CSS.aonTextCenter());
			tab.getFlexCellFormatter().addStyleName(row, 1, AON.CSS.aonBold());
			tab.setWidget(row, 1, new Label( mod421.getDeclarationResultType().getDescription() ));	
			row++;
		} else {
			final AonCreditorBox creditorBox = new AonCreditorBox(callback.getOptions().getDomainName(),callback.getOptions().getDomain(),callback.getOptions().getUser());
			final AonIbanTextBox iban = new AonIbanTextBox( new BanksSuggestOracle(callback) );
			
			Label nrcLabel = new Label("NRC");			
			final AonTextBox nrc = new AonTextBox();
						
			FlexTable aplazaTable = new FlexTable();
			Label aplazaLabel = new Label("Datos aplazamiento");
			Label avisoLabel = new Label("En el caso de \"Solicitud de Aplazamiento\", la presentaci\u00F3n del modelo debe hacerse de forma manual, desde la Oficina Virtual de la Agencia Tributaria, importando el fichero generado desde la aplicaci\u00F3n de AON. En este caso los datos IBAN, n\u00FAmero de plazos y fecha de primer plazo, se guardan en el modelo a t\u00EDtulo informativo, pues no se trasladan al fichero para su presentaci\u00F3n.");
			avisoLabel.setStyleName(AON.CSS.aonPadding());
			avisoLabel.addStyleName(AON.CSS.aonFontSmall());

			AonIntegerBox plazos = new AonIntegerBox();
			AonDateBox fechaPlazo = new AonDateBox();
			
			// TIPO
			
			final ListBox listBox = new ListBox();
			listBox.addStyleName(AON.CSS.aonMarginTopSep());
			listBox.setSelectedIndex(0);
			if (AonMathUtils.isLessThanZero(mod421.getDeclarationResult()) ) {
				if (mod421.isLastPeriod()) {
					listBox.addItem(FiscalModelDeclarationType.PAYBACK.getDescription(), FiscalModelDeclarationType.PAYBACK.getValue());
				}
				listBox.addItem(FiscalModelDeclarationType.COMPENSATE.getDescription(), FiscalModelDeclarationType.COMPENSATE.getValue());
			} else {
				listBox.addItem(FiscalModelDeclarationType.DEPOSIT.getDescription(), FiscalModelDeclarationType.DEPOSIT.getValue());
				listBox.addItem(FiscalModelDeclarationType.BANK.getDescription(), FiscalModelDeclarationType.BANK.getValue());
			}
			listBox.setEnabled(listBox.getItemCount() > 1);
			listBox.addChangeHandler( event -> {
				FiscalModelDeclarationType type = FiscalModelDeclarationType.safeValueOf(listBox.getSelectedValue());
				mod421.setDeclarationResultType( type );
				iban.setEnabled( type.isBankRequired() );
				creditorBox.setEnabled(type.mustCreateFinance() );
				if (type != FiscalModelDeclarationType.DEPOSIT) {
					nrc.setValue("");
					mod421.setNrc("");
				}
				nrcLabel.setVisible(type == FiscalModelDeclarationType.DEPOSIT);
				nrc.setVisible(type == FiscalModelDeclarationType.DEPOSIT);
				
				if (type != FiscalModelDeclarationType.DEFERRAL) {
					plazos.setValue(0);
					fechaPlazo.setValue(null);
				}
				aplazaLabel.setVisible(type == FiscalModelDeclarationType.DEFERRAL);
				aplazaTable.setVisible(type == FiscalModelDeclarationType.DEFERRAL);
				avisoLabel.setVisible(type == FiscalModelDeclarationType.DEFERRAL);
				
			});
			tab.setWidget(row, 1, listBox );
			row++;
			
			// ACREEDOR
			
			tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel()); 
			tab.setWidget(row, 0, new Label(AON.MSG.creditor()));
			Finance finance = mod421.getFinance();
			creditorBox.setValue(new Creditor().copy(finance.getRegistry()));
			creditorBox.addSelectionHandler(event ->  {
				Registry registry = event.getSelectedItem();
				mod421.getFinance().setRegistry(registry);
				mod421.getFinance().setRegistryDocument(registry.getDocument());
				mod421.getFinance().setRegistryDocumentCountry(registry.getDocumentCountry());
				mod421.getFinance().setRegistryDocumentType(registry.getDocumentType());
				mod421.getFinance().setRegistryName(registry.getName());
			});
			
			FlowPanel creditorPanel = new FlowPanel();
			creditorPanel.add(creditorBox);
			InlineLabel label = new InlineLabel("Comience a escribir para recuperar alg\u00FAn acreedor v\u00E1lido");
			label.addStyleName(AON.CSS.aonFontSmall());
			label.addStyleName(AON.CSS.aonItalic());
			creditorPanel.add(label);
			tab.setWidget(row, 1, creditorPanel);
			row++;
	
			// IBAN
			
			tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
			tab.setWidget(row, 0, new Label(AON.MSG.bankAccount()));
			
			tab.setWidget(row, 1, iban);
			FiscalModelDeclarationType type = FiscalModelDeclarationType.safeValueOf(listBox.getSelectedValue());
			iban.setEnabled( type.isBankRequired() );
			iban.addSelectionHandler(event -> {
				IbanSuggestion suggestion = (IbanSuggestion) event.getSelectedItem();
				IIbanContainer cont = suggestion.getIbanContainer();
				iban.setValue(cont.getIBan());
				BankAccount bankAccount = new BankAccount(cont.getIBan());
				mod421.getFinance().setBankAccount(bankAccount);
				mod421.getFinance().setBankAlias(cont.getAlias());
				mod421.getFinance().setBic(cont.getBic());
			});
			row++;
			
			// NRC (Solo si el resultado es positivo)
			
			if (mod421.getDeclarationResult() > 0) {								
				tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
				tab.setWidget(row, 0, nrcLabel);
				
				nrc.setText(mod421.getNrc());
				nrc.addValueChangeHandler(event -> {
					mod421.setNrc(nrc.getValue());
				});
				tab.setWidget(row, 1, nrc);
				row++;
			}
			
			// DATOS DEL APLAZAMIENTO: Nº de Plazos, Fecha primer plazo y mensaje de aviso (solo si es positivo y a partir de 2024)
			
			if (mod421.getDeclarationResult() > 0 && mod421.getYear() >= 2024) {
				avisoLabel.setVisible(false);			
				aplazaLabel.setVisible(false);
				aplazaTable.setVisible(false);
				aplazaTable.getFlexCellFormatter().addStyleName(0, 0, AON.CSS.aonTabLabel());
				aplazaTable.setWidget(0, 0, new Label("N\u00FAmero de plazos"));
				plazos.setMaxLength(2);
				plazos.setVisibleLength(2);
				plazos.setValue(mod421.getPlazos());
				plazos.addValueChangeHandler( event -> {
					if (plazos.getValue() == null)
						plazos.setValue(0);
					mod421.setPlazos(plazos.getValue()); 	
				});			
				aplazaTable.setWidget(0, 1, plazos);
			
				aplazaTable.getFlexCellFormatter().addStyleName(0, 2, AON.CSS.aonTabLabel());
				aplazaTable.setWidget(0, 2, new Label("Fecha del primer plazo"));
				if (AonStringUtils.isNotEmpty(mod421.getFechaPlazo())) {
					fechaPlazo.setValue(fechaPlazo.parse(mod421.getFechaPlazo(), false));
				}			
				fechaPlazo.addValueChangeHandler( event -> { 
						mod421.setFechaPlazo(fechaPlazo.format());
						// Aplazamiento, ponemos como fecha de vencimiento, la fecha de aplazamiento
						if (mod421.getFinance() != null) 
							mod421.getFinance().setDueDate(fechaPlazo.getValue());
					});
				aplazaTable.setWidget(0, 3, fechaPlazo);
				
				tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());			
				tab.setWidget(row, 0, aplazaLabel);
				tab.setWidget(row, 1, aplazaTable);
				row++;
				
				tab.getFlexCellFormatter().setColSpan(row, 0, 2);
				tab.setWidget(row, 0, avisoLabel);			
				row++;
			}
		}
		
		// Aviso de envio de email de notificacion
		if (callback != null 
				&& callback.getOptions() != null 
				&& callback.getOptions().getConfiguration().fiscal().isCustomerCheckEnabled() 
				&& mod421.getStatus() != FiscalStatus.CUSTOMER_CHECK) {
			row++;		
			tab.getFlexCellFormatter().setColSpan(row, 0, 2);
			
			Label sendEmailLabel = new Label("Si pulsa \"Env\u00EDo a Cliente\", adem\u00E1s de cambiar el estado del modelo, se enviar\u00E1 un mensaje de correo electr\u00F3nico a la direcci\u00F3n email de la empresa cliente, indicando que tiene un nuevo modelo para confirmar en el portal.");
			sendEmailLabel.setStyleName(AON.CSS.aonPadding());
			sendEmailLabel.addStyleName(AON.CSS.aonFontSmall());
			tab.setWidget(row, 0, sendEmailLabel);
		}
		
		row++;		
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.setStyleName(AON.CSS.aonPadding());
		flowPanel.addStyleName(AON.CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText( AON.MSG.accept());
		acceptButton.addClickHandler(event -> {
			hide();
			popupCallback.onAccept();
		});
		flowPanel.add(acceptButton);
		
		if (callback != null 
				&& callback.getOptions() != null 
				&& callback.getOptions().getConfiguration().fiscal().isCustomerCheckEnabled() 
				&& mod421.getStatus() != FiscalStatus.CUSTOMER_CHECK)  {
			Button customerCheckButton = new Button();
			customerCheckButton.setStyleName(AON.CSS.aonCheckButton());
			customerCheckButton.addStyleName(AON.CSS.aonMarginLeft());
			customerCheckButton.setText( AON.MSG.customerCheckAction());
			customerCheckButton.addClickHandler(event -> {
				hide();
				popupCallback.onCustomerCheck();
			});
			flowPanel.add(customerCheckButton);
		}

		Button cancelButton = new Button();
		cancelButton.setStyleName(AON.CSS.aonCancelButton());
		cancelButton.addStyleName(AON.CSS.aonMarginLeft());
		cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(event -> {
			hide();
			popupCallback.onCancel();
		});
		flowPanel.add(cancelButton);
		tab.setWidget(row, 0, flowPanel);

		add(tab);
	}

	private void initializeTable() {
		tab.setCellPadding(0);
		tab.setCellSpacing(0);
		tab.setStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		tab.addStyleName(AON.CSS.aonTable());
		ColumnFormatter cf = tab.getColumnFormatter();
		cf.setWidth(0, "130px");
		cf.addStyleName(0, AON.CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.CSS.aonPaddingRight() );
		cf.setWidth(1, "450px");
		cf.addStyleName(0, AON.CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.CSS.aonPaddingRight() );
	}

	private static class BanksSuggestOracle  extends MultiWordSuggestOracle {
		private Model421Callback modelCallback;

		private BanksSuggestOracle (final Model421Callback modelCallback) {
			super();
			this.modelCallback = modelCallback;
		}
		
		@Override
		public void requestSuggestions(final Request request,final Callback callback) {
			service.getCompanyBanks (modelCallback.getOptions().getDomainName(),modelCallback.getOptions().getUser(),modelCallback.getOptions().getDomain(), 
					new AsyncCallback<LinkedList<CompanyBank>>() {

						public void onFailure(Throwable caught) {
							modelCallback.showError(AON.MSG.unableToShowCompanyBanks(caught.getMessage()) );
						}

						public void onSuccess(LinkedList<CompanyBank> result) {
							ArrayList<Suggestion> suggestions = new ArrayList<>();
							if (result != null) {
								for (final CompanyBank cb : result) {
									suggestions.add(new AonIbanTextBox.IbanSuggestion(cb));
								}
							}
							Response resp = new Response(suggestions);
							callback.onSuggestionsReady(request, resp);
						}
					});
		}
	}

}

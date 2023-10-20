package com.esferalia.aon.gwt.fiscal.client.model;

import java.util.ArrayList;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIbanTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIbanTextBox.IbanSuggestion;
import com.esferalia.aon.gwt.fiscal.client.FiscalMSService;
import com.esferalia.aon.gwt.fiscal.client.FiscalMSServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalMSServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.widget.AonCreditorBox;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.IIbanContainer;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
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

public class AonFinishDeclarationPopup<T extends FiscalModel,O extends FiscalModelModuleOptions<T>> extends AonCustomDialog {
	
	static final FiscalMSServiceAsync SERVICE;
	static {
		FiscalMSServiceAsync serviceRaw = GWT.create(FiscalMSService.class);
		SERVICE = new FiscalMSServiceAsyncDecorator(serviceRaw);
	}

	public static interface IFinishDeclarationPopupCallback<T extends FiscalModel> {
		public void onAccept(T t);
		public void onCancel(T t);
		public void onCustomerCheck(T t);
	}
	
	protected final FlexTable tab = new FlexTable();
	protected int row = 0;
	
	public AonFinishDeclarationPopup(T model
		,final IFiscalModelCallback<T,O> callback
		,IFinishDeclarationPopupCallback<T> finishPopupCallback) {
		setCaption(AON.MSG.finish());
		setGlassEnabled(true);
		setAnimationEnabled(true);
		initializeTable();
		paintResul(model);
		paintDeclarationType(model, callback);
		paintButtons(model, callback, finishPopupCallback);
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

	private void paintResul(T model) {
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		tab.setWidget(row, 0, new Label(AON.MSG.fiscalDebt()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.CSS.aonTextRight());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.CSS.aonFontLarger());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.CSS.aonPaddingRight());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.CSS.aonBold());
		tab.setWidget(row, 1, new Label( AON.FMT.format(model.getDeclarationResult())));
		row++;
	}

	private void paintDeclarationType(T model,final IFiscalModelCallback<T,O> callback) {
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		tab.setWidget(row, 0, new Label(AON.MSG.declarationType()));
		
		if (model.getDeclarationResultType() == FiscalModelDeclarationType.NEGATIVE
		 || model.getDeclarationResultType() == FiscalModelDeclarationType.TO_DEDUCE) {
			tab.getFlexCellFormatter().addStyleName(row, 1, AON.CSS.aonTextCenter());
			tab.getFlexCellFormatter().addStyleName(row, 1, AON.CSS.aonBold());
			tab.setWidget(row, 1, new Label( model.getDeclarationResultType().getDescription() ));	
			row++;
		} else {
			final AonCreditorBox creditorBox = new AonCreditorBox(callback.getOptions().getOccam());
			final AonIbanTextBox iban = new AonIbanTextBox( new EnterpriseSuggestOracle<T,O>(callback) );
			final AonTextBox nrc = new AonTextBox();
			final ListBox listBox = new ListBox();
			listBox.setSelectedIndex(0);
			listBox.addItem(FiscalModelDeclarationType.DEPOSIT.getDescription(), FiscalModelDeclarationType.DEPOSIT.getValue());
			listBox.addItem(FiscalModelDeclarationType.BANK.getDescription(), FiscalModelDeclarationType.BANK.getValue());
			if (model.isAEAT()) {
				listBox.addItem(FiscalModelDeclarationType.DEPOSIT_CCT.getDescription(), FiscalModelDeclarationType.DEPOSIT_CCT.getValue());
			}
			listBox.addChangeHandler(event -> {
				FiscalModelDeclarationType type = FiscalModelDeclarationType.safeValueOf(listBox.getSelectedValue());
				model.setDeclarationResultType( type );
				iban.setEnabled( type.isBankRequired() );
				creditorBox.setEnabled(type.mustCreateFinance());
				nrc.setEnabled(type == FiscalModelDeclarationType.DEPOSIT);
				if (type != FiscalModelDeclarationType.DEPOSIT) {
					nrc.setValue("");
					model.setNrc("");
				}
			});
			tab.setWidget(row, 1, listBox );
			row++;
			
			tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
			tab.setWidget(row, 0, new Label(AON.MSG.creditor()));
			Finance finance = model.getFinance();
			creditorBox.setValue(new Creditor().copy(finance.getRegistry()));
			creditorBox.addSelectionHandler(event -> {
				Registry registry = event.getSelectedItem();
				model.getFinance().setRegistry(registry);
				model.getFinance().setRegistryDocument(registry.getDocument());
				model.getFinance().setRegistryDocumentCountry(registry.getDocumentCountry());
				model.getFinance().setRegistryDocumentType(registry.getDocumentType());
				model.getFinance().setRegistryName(registry.getName());
			});
			
			FlowPanel creditorPanel = new FlowPanel();
			creditorPanel.add(creditorBox);
			InlineLabel label = new InlineLabel("Comience a escribir para recuperar alg\u00FAn acreedor v\u00E1lido");
			label.addStyleName(AON.CSS.aonFontSmall());
			label.addStyleName(AON.CSS.aonItalic());
			creditorPanel.add(label);
			tab.setWidget(row, 1, creditorPanel);
			row++;
	
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
				model.getFinance().setBankAccount(bankAccount);
				model.getFinance().setBankAlias(cont.getAlias());
				model.getFinance().setBic(cont.getBic());
			});
			
			// NRC (Solo si el resultado es positivo)
			if (model.getDeclarationResult() > 0) {
				row++;
				tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
				tab.setWidget(row, 0, new Label("NRC"));
				
				nrc.setText(model.getNrc());
				nrc.addValueChangeHandler(event -> {
					model.setNrc(nrc.getValue());
				});
				tab.setWidget(row, 1, nrc);
			}

		}
		
		row++;
		
	}
	
	private void paintButtons(T model
			,final IFiscalModelCallback<T,O> callback
			,IFinishDeclarationPopupCallback<T> finishPopupCallback) {
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.setStyleName(AON.CSS.aonPadding());
		flowPanel.addStyleName(AON.CSS.aonMarginTop());
		flowPanel.addStyleName(AON.CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText( AON.MSG.accept());
		acceptButton.addClickHandler(event -> {
			hide();
			finishPopupCallback.onAccept(model);
		});
		flowPanel.add(acceptButton);
		if (callback.getOptions().getConfiguration().fiscal().isCustomerCheckEnabled() 
			&& model.getStatus() != FiscalStatus.CUSTOMER_CHECK)  {
			Button customerCheckButton = new Button();
			customerCheckButton.setStyleName(AON.CSS.aonCheckButton());
			customerCheckButton.addStyleName(AON.CSS.aonMarginLeft());
			customerCheckButton.setText( AON.MSG.customerCheckAction());
			customerCheckButton.addClickHandler(event -> {
				hide();
				finishPopupCallback.onCustomerCheck(model);
			});
			flowPanel.add(customerCheckButton);
		}
		
		Button cancelButton = new Button();
		cancelButton.setStyleName(AON.CSS.aonCancelButton());
		cancelButton.addStyleName(AON.CSS.aonMarginLeft());
		cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(event -> {
			hide();
			finishPopupCallback.onCancel(model);
		});
		flowPanel.add(cancelButton);
		tab.setWidget(row, 0, flowPanel);
	}

	private static class EnterpriseSuggestOracle<T extends FiscalModel,O extends FiscalModelModuleOptions<T>> extends MultiWordSuggestOracle {
		private IFiscalModelCallback<T,O> modelCallback;

		private EnterpriseSuggestOracle(final IFiscalModelCallback<T,O> modelCallback) {
			super();
			this.modelCallback = modelCallback;
		}
		
		@Override
		public void requestSuggestions(final Request request,
				final Callback callback) {
			SERVICE.getCompanyBanks (
					modelCallback.getOptions().getDomainName(),
					modelCallback.getOptions().getUser(),
					modelCallback.getOptions().getDomain(), 
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

package com.esferalia.aon.gwt.fiscal.client.mod390hf;

import java.util.ArrayList;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIbanTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIbanTextBox.IbanSuggestion;
import com.esferalia.aon.gwt.fiscal.client.mod390hf.Model390HF.Model390HFCallback;
import com.esferalia.aon.gwt.fiscal.client.widget.AonCreditorBox;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.IIbanContainer;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.watson.util.AonMathUtils;
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

public class Model390HFFinishDeclarationPopup extends AonCustomDialog {

	static CommonServiceAsync commonService;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
	}

	public static interface FinishDeclarationPopupCallback {
		public void onAccept();
		public void onCancel();
		public void onCustomerCheck();
	}

	protected final FlexTable tab = new FlexTable();
	protected int row = 0;
	
	protected Model390HFFinishDeclarationPopup(final Mod390HF mod390HF, final Model390HFCallback callback,FinishDeclarationPopupCallback popupCallback) {
		setCaption(AON.MSG.finish());
		setGlassEnabled(true);
		setAnimationEnabled(true);
		initializeTable();

		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		tab.setWidget(row, 0, new Label(AON.MSG.fiscalDebt()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.CSS.aonTextRight());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.CSS.aonFontLarger());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.CSS.aonPaddingRight());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.CSS.aonBold());
		tab.setWidget(row, 1, new Label(AON.FMT.format(mod390HF.getDeclarationResult())));
		row++;

		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		tab.setWidget(row, 0, new Label(AON.MSG.declarationType()));

		if (!mod390HF.getDeclarationResultType().mustCreateFinance()) {	// SIN ACTIVIDAD!!!
			tab.getFlexCellFormatter().addStyleName(row, 1, AON.CSS.aonTextCenter());
			tab.getFlexCellFormatter().addStyleName(row, 1, AON.CSS.aonBold());
			tab.setWidget(row, 1, new Label(mod390HF.getDeclarationResultType().getDescription()));
			row++;
		} else {
			final AonCreditorBox creditorBox = new AonCreditorBox(callback.getOptions().getOccam());
			final AonIbanTextBox iban = new AonIbanTextBox(new BanksSuggestOracle(callback) );

			final ListBox listBox = new ListBox();
			listBox.setSelectedIndex(0);
			if (AonMathUtils.isLessThanZero(mod390HF.getDeclarationResult())) {
				listBox.addItem(FiscalModelDeclarationType.PAYBACK.getDescription(),
						FiscalModelDeclarationType.PAYBACK.getValue());
				listBox.addItem(FiscalModelDeclarationType.COMPENSATE.getDescription(),
						FiscalModelDeclarationType.COMPENSATE.getValue());
			} else {
				listBox.addItem(FiscalModelDeclarationType.DEPOSIT.getDescription(),
						FiscalModelDeclarationType.DEPOSIT.getValue());
				listBox.addItem(FiscalModelDeclarationType.BANK.getDescription(),
						FiscalModelDeclarationType.BANK.getValue());
			}
			listBox.addChangeHandler(event -> {
				FiscalModelDeclarationType type = FiscalModelDeclarationType
						.safeValueOf(listBox.getSelectedValue());
				mod390HF.setDeclarationResultType(type);
				iban.setEnabled(type.isBankRequired());
				creditorBox.setEnabled(type.mustCreateFinance());
			});
			tab.setWidget(row, 1, listBox);
			row++;

			tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
			tab.setWidget(row, 0, new Label(AON.MSG.creditor()));
			Finance finance = mod390HF.getFinance();
			creditorBox.setValue(new Creditor().copy(finance.getRegistry()));
			creditorBox.addSelectionHandler(event -> {
				Registry registry = event.getSelectedItem();
				mod390HF.getFinance().setRegistry(registry);
				mod390HF.getFinance().setRegistryDocument(registry.getDocument());
				mod390HF.getFinance().setRegistryDocumentCountry(registry.getDocumentCountry());
				mod390HF.getFinance().setRegistryDocumentType(registry.getDocumentType());
				mod390HF.getFinance().setRegistryName(registry.getName());
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
			iban.setEnabled(type.isBankRequired());
			iban.addSelectionHandler(event -> {
				IbanSuggestion suggestion = (IbanSuggestion) event.getSelectedItem();
				IIbanContainer cont = suggestion.getIbanContainer();
				iban.setValue(cont.getIBan());
				BankAccount bankAccount = new BankAccount(cont.getIBan());
				mod390HF.getFinance().setBankAccount(bankAccount);
				mod390HF.getFinance().setBankAlias(cont.getAlias());
				mod390HF.getFinance().setBic(cont.getBic());
			});
		}

		row++;

		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.setStyleName(AON.CSS.aonPadding());
		flowPanel.addStyleName(AON.CSS.aonMarginTop());
		flowPanel.addStyleName(AON.CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText(AON.MSG.accept());
		acceptButton.addClickHandler(event -> {
			hide();
			popupCallback.onAccept();
		});
		flowPanel.add(acceptButton);
		
		if (callback != null 
			&& callback.getOptions() != null 
			&& callback.getOptions().getConfiguration().fiscal().isCustomerCheckEnabled() 
			&& mod390HF.getStatus() != FiscalStatus.CUSTOMER_CHECK)  {
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
		cancelButton.setText(AON.MSG.cancelAction());
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
		tab.addStyleName(AON.CSS.aonDisplayTable());
		ColumnFormatter cf = tab.getColumnFormatter();
		cf.setWidth(0, "130px");
		cf.addStyleName(0, AON.CSS.aonPaddingLeft());
		cf.addStyleName(0, AON.CSS.aonPaddingRight());
		cf.setWidth(1, "450px");
		cf.addStyleName(0, AON.CSS.aonPaddingLeft());
		cf.addStyleName(0, AON.CSS.aonPaddingRight());
	}

	private static class BanksSuggestOracle extends MultiWordSuggestOracle {
		private Model390HFCallback modelCallback;

		private BanksSuggestOracle(final Model390HFCallback modelCallback) {
			super();
			this.modelCallback = modelCallback;
		}

		@Override
		public void requestSuggestions(final Request request, final Callback callback) {
			commonService.getCompanyBanks(modelCallback.getOptions().getDomainName()
					,modelCallback.getOptions().getDomain()
					,modelCallback.getOptions().getUser()
					,new AsyncCallback<LinkedList<CompanyBank>>() {

						public void onFailure(Throwable caught) {
							modelCallback.showError(AON.MSG.unableToShowCompanyBanks(caught.getMessage()));
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

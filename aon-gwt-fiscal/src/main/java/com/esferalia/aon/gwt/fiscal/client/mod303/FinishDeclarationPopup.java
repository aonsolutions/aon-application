package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.ArrayList;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.CreditorBox;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.IbanTextBox;
import com.esferalia.aon.gwt.common.client.widget.IbanTextBox.IbanSuggestion;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.IModel303Callback;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.IIbanContainer;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class FinishDeclarationPopup extends CustomDialog {
	
	static CommonServiceAsync commonService;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
	}
	
	public static interface FinishDeclarationPopupCallback {
		public void onAccept();
		public void onCancel();
	}

	final protected FlexTable tab = new FlexTable();
	protected int row = 0;
	
	public FinishDeclarationPopup(final Mod303 mod303 ,final IModel303Callback callback,FinishDeclarationPopupCallback popupCallback) {
		setCaption(AON.MSG.finish());
		setGlassEnabled(true);
		setAnimationEnabled(true);
		initializeTable();

		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.fiscalDebt()));
		tab.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextRight());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonFontBig());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPaddingRight());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		tab.setWidget(row, 1, new Label( AON.FMT.format(mod303.getResult())));
		row++;

		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.declarationType()));
		tab.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		
		// SIN ACTIVIDAD!!!
		if (mod303.getDeclarationType() == FiscalModelDeclarationType.COMPENSATE && mod303.getPeriod() != Period.T4 && mod303.getPeriod() != Period.M12) {
			tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
			tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
			tab.setWidget(row, 1, new Label( mod303.getDeclarationType().getDescription() ));	
			row++;
		} else {
			final CreditorBox creditorBox = new CreditorBox(Model303.getCurrentDomainName(),Model303.getCurrentDomain() );
			final IbanTextBox iban = new IbanTextBox( new EnterpriseSuggestOracle<Mod303>(callback) );
			
			final ListBox listBox = new ListBox();
			listBox.setSelectedIndex(0);
			if (AonMathUtils.isLessThanZero(mod303.getResult()) ) {
				listBox.addItem(FiscalModelDeclarationType.PAYBACK.getDescription(), FiscalModelDeclarationType.PAYBACK.getValue());
				if (mod303.isAEAT()) {
					listBox.addItem(FiscalModelDeclarationType.PAYBACK_CCT.getDescription(), FiscalModelDeclarationType.PAYBACK_CCT.getValue());
				}
				listBox.addItem(FiscalModelDeclarationType.COMPENSATE.getDescription(), FiscalModelDeclarationType.COMPENSATE.getValue());
			} else {
				listBox.addItem(FiscalModelDeclarationType.DEPOSIT.getDescription(), FiscalModelDeclarationType.DEPOSIT.getValue());
				listBox.addItem(FiscalModelDeclarationType.BANK.getDescription(), FiscalModelDeclarationType.BANK.getValue());
				if (mod303.isAEAT()) {
					listBox.addItem(FiscalModelDeclarationType.DEPOSIT_CCT.getDescription(), FiscalModelDeclarationType.DEPOSIT_CCT.getValue());
				}
			}
			listBox.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					FiscalModelDeclarationType type = FiscalModelDeclarationType.safeValueOf(listBox.getSelectedValue());
					mod303.setDeclarationType( type );
					iban.setEnabled( type.isBankRequired() );
					creditorBox.setEnabled(type.mustCreateFinance());
				}
			});
			tab.setWidget(row, 1, listBox );
			row++;
			
			tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
			tab.setWidget(row, 0, new Label(AON.MSG.creditor()));
			tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
			Finance finance = mod303.getFinance();
			creditorBox.setValue(
				new Creditor()
					.setRegistry(finance.getRegistry())
					.setId(finance.getRegistry()==null?null:finance.getRegistry().getId())
				);
			creditorBox.addSelectionHandler(new SelectionHandler<Creditor>() {
				
				@Override
				public void onSelection(SelectionEvent<Creditor> event) {
					Registry registry = event.getSelectedItem().getRegistry();
					mod303.getFinance().setRegistry(registry);
					mod303.getFinance().setRegistryDocument(registry.getDocument());
					mod303.getFinance().setRegistryDocumentCountry(registry.getDocumentCountry());
					mod303.getFinance().setRegistryDocumentType(registry.getDocumentType());
					mod303.getFinance().setRegistryName(registry.getName());
				}
			});
			
			FlowPanel creditorPanel = new FlowPanel();
			creditorPanel.add(creditorBox);
			InlineLabel label = new InlineLabel("Comience a escribir para recuperar alg\u00FAn acreedor v\u00E1lido");
			label.addStyleName(AON.AON_CSS.aonFontSmall());
			label.addStyleName(AON.AON_CSS.aonItalic());
			creditorPanel.add(label);
			tab.setWidget(row, 1, creditorPanel);
			row++;
	
			tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
			tab.setWidget(row, 0, new Label(AON.MSG.bankAccount()));
			tab.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
			
			tab.setWidget(row, 1, iban);
			FiscalModelDeclarationType type = FiscalModelDeclarationType.safeValueOf(listBox.getSelectedValue());
			iban.setEnabled( type.isBankRequired() );
			iban.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
				
				@Override
				public void onSelection(SelectionEvent<Suggestion> event) {
					IbanSuggestion suggestion = (IbanSuggestion) event.getSelectedItem();
					IIbanContainer cont = suggestion.getIbanContainer();
					iban.setValue(cont.getIBan());
					BankAccount bankAccount = new BankAccount(cont.getIBan());
					mod303.getFinance().setBankAccount(bankAccount);
					mod303.getFinance().setBankAlias(cont.getAlias());
					mod303.getFinance().setBic(cont.getBic());
				}
			});
		}
		
		row++;
		
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.setStyleName(AON.AON_CSS.aonPadding());
		flowPanel.addStyleName(AON.AON_CSS.aonMarginTop());
		flowPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.AON_CSS.aonConfirmDialogOkButton());
		acceptButton.setText( AON.MSG.accept());
		acceptButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hide();
				popupCallback.onAccept();
			}
		});
		
		flowPanel.add(acceptButton);
		Button cancelButton = new Button();
		cancelButton.setStyleName(AON.AON_CSS.aonConfirmDialogCancelButton());
		cancelButton.addStyleName(AON.AON_CSS.aonMarginLeft());
		cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(new ClickHandler() {
	
			@Override
			public void onClick(ClickEvent event) {
				hide();
				popupCallback.onCancel();
			}
			
		});
		flowPanel.add(cancelButton);
		tab.setWidget(row, 0, flowPanel);

		add(tab);
	}

	private void initializeTable() {
		tab.setCellPadding(0);
		tab.setCellSpacing(0);
		tab.setStyleName(AON.AON_CSS.aonMarginTop());
		tab.addStyleName(AON.AON_CSS.aonMarginBottom());
		tab.addStyleName(AON.AON_CSS.aonPanelGrid());
		ColumnFormatter cf = tab.getColumnFormatter();
		cf.setWidth(0, "130px");
		cf.addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		cf.setWidth(1, "450px");
		cf.addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.AON_CSS.aonPaddingRight() );
	}

	private static class EnterpriseSuggestOracle<T extends FiscalModel> extends MultiWordSuggestOracle {
		private IModel303Callback modelCallback;

		private EnterpriseSuggestOracle(final IModel303Callback modelCallback) {
			super();
			this.modelCallback = modelCallback;
		}
		
		@Override
		public void requestSuggestions(final Request request,
				final Callback callback) {
			commonService.getCompanyBanks (Model303.getCurrentDomainName(),Model303.getCurrentDomain(), 
					new AsyncCallback<LinkedList<CompanyBank>>() {

						public void onFailure(Throwable caught) {
							modelCallback.showError(AON.MSG.unableToShowCompanyBanks(caught.getMessage()) );
						}

						public void onSuccess(LinkedList<CompanyBank> result) {
							ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();
							if (result != null) {
								for (final CompanyBank cb : result) {
									suggestions.add(new IbanTextBox.IbanSuggestion(cb));
								}
							}
							Response resp = new Response(suggestions);
							callback.onSuggestionsReady(request, resp);
						}
					});
		}
	}

}

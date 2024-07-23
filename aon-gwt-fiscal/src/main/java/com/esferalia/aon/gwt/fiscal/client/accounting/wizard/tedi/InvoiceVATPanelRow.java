package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.EditableInvoicePanel.IEditableInvoicePanelCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.InvoiceVATPanel.InvoiceVATGridColumns;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

class InvoiceVATPanelRow extends AonDisplayGridRow implements Focusable, HasSelectionHandlers<Account>, HasValueChangeHandlers<InvoiceVAT> {
	
	class InvestAssetListBox extends ListBox {
		private InvestAssetListBox(final IEditableInvoicePanelCallback callback) {
			setWidth("90px");
			addItem("------",(String) null);
			if (callback.getConfiguration().isInvestAssetsAvailable()) {
				for (InvestAsset asset : callback.getConfiguration().getInvestAssets()) {
					addItem(asset.getDescription(),AonNumberUtils.toString(asset.getId()));
				}
			}
		}
		
		InvestAsset getValue(final IEditableInvoicePanelCallback callback) {
			if (getSelectedIndex() == 0) return null;
			return callback.getConfiguration().getInvestAssets().get(getSelectedIndex() -1 );
		}

		public void setValue(final IEditableInvoicePanelCallback callback, Integer investAsset) {
			if (investAsset == null) setSelectedIndex(0);
			int i = 1;
			for (InvestAsset asset : callback.getConfiguration().getInvestAssets()) {
				if (AonNumberUtils.equals(asset.getId(),investAsset)) {
					setSelectedIndex(i);		
				}
				i++;
			}
		}
		
	}
	
	private AonAccountBox expAccount;
	private AonDoubleBox taxableBase;
	private AonDoubleBox vatPercent;
	private AonDoubleBox vatQuota;
	private AonDoubleBox surchargePercent;
	private AonDoubleBox surchargeQuota;
	private InvestAssetListBox investAsset;
	private AonAccountBox inputVatAccount;
	private AonAccountBox outputVatAccount;
	private CheckBox withholding;
	private CheckBox prepayment;
	private AonTableButton removeButton;
	
	private AonDoubleBox dedPercent;
	private AonDoubleBox dedQuota;
	private AonDoubleBox noDedQuota;
	private AonAccountBox adjAccount;

	private AonDoubleBox directTaxPercent;
	private AonDoubleBox directTaxNoDedExpenses;
	private AonDoubleBox directTaxDedExpenses;
	private AonAccountBox directTaxAccount;

	
	private final InvoiceVATPanel vatPanel;
	private final int rowIndex;
	
	InvoiceVATPanelRow(InvoiceVATPanel tab, final int rowIndex, final IEditableInvoicePanelCallback callback, final int vatIdx, boolean focus) {
		this.vatPanel = tab;
		this.rowIndex = rowIndex;
		
		defineExpAccount(callback,vatIdx);
		defineTaxableBase(callback,vatIdx);
		defineVatPercent(callback,vatIdx);
		defineVatQuota(callback,vatIdx);
		defineSurchargePercent(callback,vatIdx);
		defineSurchargeQuota(callback,vatIdx);
		defineInvestAsset(callback,vatIdx);
		defineInputVatAccount(callback,vatIdx);
		defineOutputVatAccount(callback,vatIdx);
		defineWithholding(callback,vatIdx);
		definePrepayment(callback,vatIdx);
		defineRemoveButton(callback,vatIdx);
		
		defineDedPercent(callback,vatIdx);
		defineDedQuota(callback,vatIdx);
		defineNoDedQuota(callback,vatIdx);
		defineAdjAccount(callback,vatIdx);

		defineDirectTaxPercent(callback,vatIdx);
		defineDirectTaxNoDedExpenses(callback,vatIdx);
		defineDirectTaxDedExpenses(callback,vatIdx);
		defineDirectTaxAccount(callback,vatIdx);

		paintRow(callback,vatIdx);
		
		if (focus) {
			Scheduler.get().scheduleDeferred(() -> expAccount.setFocus(true));
		}

	}

	private void paintRow(IEditableInvoicePanelCallback callback, final int vatIdx) {
		if (vatPanel.getGrid().getRowCount() > rowIndex) {
			int cellCount = vatPanel.getGrid().getCellCount( rowIndex );
			for (int idx = (cellCount - 1); idx >= 0; --idx) {
				vatPanel.getGrid().removeCell(rowIndex,idx);
			}
		}
		int idx = 0;
		idx = addCell(callback, vatIdx, idx, InvoiceVATPanel.InvoiceVATGridColumns.EXP_ACCOUNT, expAccount);
		idx = addCell(callback, vatIdx, idx, InvoiceVATPanel.InvoiceVATGridColumns.TAXABALE_BASE, taxableBase);
		idx = addCell(callback, vatIdx, idx, InvoiceVATPanel.InvoiceVATGridColumns.VAT_PERCENT, vatPercent);
		idx = addCell(callback, vatIdx, idx, InvoiceVATPanel.InvoiceVATGridColumns.VAT_QUOTA, vatQuota);
		idx = addCell(callback, vatIdx, idx, InvoiceVATPanel.InvoiceVATGridColumns.SURCHARGE_PERCENT, surchargePercent);
		idx = addCell(callback, vatIdx, idx, InvoiceVATPanel.InvoiceVATGridColumns.SURCHARGE_QUOTA, surchargeQuota);
		idx = addCell(callback, vatIdx, idx, InvoiceVATPanel.InvoiceVATGridColumns.INVEST_ASSET, investAsset);
		if (InvoiceVATGridColumns.INVEST_ASSET.isAdditionalDataEnabled(callback, vatIdx)) {
			vatPanel.getGrid().getCellFormatter().addStyleName(rowIndex,(idx-1),AON.CSS.aonBackgroundLigthBlue());
		}
		idx = addCell(callback, vatIdx, idx, InvoiceVATPanel.InvoiceVATGridColumns.INPUT_VAT_ACCOUNT, inputVatAccount);
		idx = addCell(callback, vatIdx, idx, InvoiceVATPanel.InvoiceVATGridColumns.OUTPUT_VAT_ACCOUNT, outputVatAccount);
		idx = addCell(callback, vatIdx, idx, InvoiceVATPanel.InvoiceVATGridColumns.WITHHOLDING, withholding);
		idx = addCell(callback, vatIdx, idx, InvoiceVATPanel.InvoiceVATGridColumns.PREPAYMENT, prepayment);
		addCell(callback, vatIdx, idx, InvoiceVATPanel.InvoiceVATGridColumns.EMPTY, removeButton);
		
		if (InvoiceVATGridColumns.INVEST_ASSET.isAdditionalDataEnabled(callback, vatIdx)) {
			addInvestAssetAdditionalData( callback, vatIdx );
		}
		
	}

	private void addInvestAssetAdditionalData(IEditableInvoicePanelCallback callback, int vatIdx) {
		int vatInvestRowIndex = rowIndex + 1;
		AonDisplayTable iaGrid = new AonDisplayTable();
		iaGrid.addStyleName( AON.CSS.aonMarginLeft());
		iaGrid.addStyleName( AON.CSS.aonPaddingLeft());
		
		Label dedLabel = new Label("IVA Deducible");
		dedLabel.setStyleName(AON.CSS.aonInnerLabel());
		dedLabel.addStyleName(AON.CSS.aonBold());
		dedLabel.addStyleName(AON.CSS.aonPaddingLeft());
		dedLabel.addStyleName(AON.CSS.aonPaddingRight());

		Label dedPercentLabel = new Label(AON.MSG.dedPercent());
		dedPercentLabel.setStyleName(AON.CSS.aonInnerLabel());
		dedPercentLabel.addStyleName(AON.CSS.aonPaddingLeft());
		dedPercentLabel.addStyleName(AON.CSS.aonPaddingRight());
		
		Label dedQuotaLabel = new Label("IVA Deducible");
		dedQuotaLabel.setStyleName(AON.CSS.aonInnerLabel());
		dedQuotaLabel.addStyleName(AON.CSS.aonPaddingLeft());
		dedQuotaLabel.addStyleName(AON.CSS.aonPaddingRight());

		Label noDedQuotaLabel = new Label("IVA NO Deducible");
		noDedQuotaLabel.setStyleName(AON.CSS.aonInnerLabel());
		noDedQuotaLabel.addStyleName(AON.CSS.aonPaddingLeft());
		noDedQuotaLabel.addStyleName(AON.CSS.aonPaddingRight());

		Label adjAccountLabel = new Label(AON.MSG.adjAccount());
		adjAccountLabel.setStyleName(AON.CSS.aonInnerLabel());
		adjAccountLabel.addStyleName(AON.CSS.aonPaddingLeft());
		adjAccountLabel.addStyleName(AON.CSS.aonPaddingRight());

		iaGrid.addRow()
			.addCell( new Label() )
			.addCell( dedPercentLabel )
			.addCell( dedQuotaLabel )
			.addCell( noDedQuotaLabel )
			.addCell( adjAccountLabel );
		iaGrid.addRow()
			.addCell( dedLabel )
			.addCell( dedPercent ,AON.CSS.aonPaddingLeft())
			.addCell( dedQuota   ,AON.CSS.aonPaddingLeft())
			.addCell( noDedQuota ,AON.CSS.aonPaddingLeft())
			.addCell( adjAccount ,AON.CSS.aonPaddingLeft());
		
		Label directTaxLabel = new Label("Gastos");
		directTaxLabel.setStyleName(AON.CSS.aonInnerLabel());
		directTaxLabel.addStyleName(AON.CSS.aonBold());
		directTaxLabel.addStyleName(AON.CSS.aonPaddingLeft());
		directTaxLabel.addStyleName(AON.CSS.aonPaddingRight());

		Label directTaxPercentLabel = new Label(AON.MSG.directTaxPercent());
		directTaxPercentLabel.setStyleName(AON.CSS.aonInnerLabel());
		directTaxPercentLabel.addStyleName(AON.CSS.aonPaddingLeft());
		directTaxPercentLabel.addStyleName(AON.CSS.aonPaddingRight());
		
		Label directTaxDedExpensesLabel = new Label("Deducible");
		directTaxDedExpensesLabel.setStyleName(AON.CSS.aonInnerLabel());
		directTaxDedExpensesLabel.addStyleName(AON.CSS.aonPaddingLeft());
		directTaxDedExpensesLabel.addStyleName(AON.CSS.aonPaddingRight());

		Label directTaxNoDedExpensesLabel = new Label("NO deducible");
		directTaxNoDedExpensesLabel.setStyleName(AON.CSS.aonInnerLabel());
		directTaxNoDedExpensesLabel.addStyleName(AON.CSS.aonPaddingLeft());
		directTaxNoDedExpensesLabel.addStyleName(AON.CSS.aonPaddingRight());

		Label directTaxAccountLabel = new Label(AON.MSG.adjDirectTaxAccount());
		directTaxAccountLabel.setStyleName(AON.CSS.aonInnerLabel());
		directTaxAccountLabel.addStyleName(AON.CSS.aonPaddingLeft());
		directTaxAccountLabel.addStyleName(AON.CSS.aonPaddingRight());

		iaGrid.addRow()
			.addCell( new Label() )
			.addCell( directTaxPercentLabel )
			.addCell( directTaxDedExpensesLabel )
			.addCell( directTaxNoDedExpensesLabel )
			.addCell( directTaxAccountLabel );
		iaGrid.addRow()
			.addCell( directTaxLabel )
			.addCell( directTaxPercent 		,AON.CSS.aonPaddingLeft())
			.addCell( directTaxDedExpenses 	,AON.CSS.aonPaddingLeft())
			.addCell( directTaxNoDedExpenses,AON.CSS.aonPaddingLeft())
			.addCell( directTaxAccount 		,AON.CSS.aonPaddingLeft());
		
		vatPanel.getGrid().setWidget(vatInvestRowIndex,0,iaGrid);
		vatPanel.getGrid().getCellFormatter().setStyleName(vatInvestRowIndex,0,AON.CSS.aonDisplayGridCell());
		vatPanel.getGrid().getCellFormatter().addStyleName(vatInvestRowIndex,0,AON.CSS.aonPadding());
		vatPanel.getGrid().getCellFormatter().addStyleName(vatInvestRowIndex,0,AON.CSS.aonBackgroundLigthBlue());
		vatPanel.getGrid().getFlexCellFormatter().setColSpan(vatInvestRowIndex,0, InvoiceVATGridColumns.values().length);
	}

	private int addCell(IEditableInvoicePanelCallback callback, final int vatIdx, int idx, InvoiceVATGridColumns column, Widget widget) {
		if (column.isHeaderEnabled(callback)) {
			Widget finalWidget = (column.isRowCellEnabled(callback,vatIdx))?widget:new Label();
			vatPanel.getGrid().getCellFormatter().setStyleName(rowIndex,idx,AON.CSS.aonDisplayGridCell());
			vatPanel.getGrid().getCellFormatter().addStyleName(rowIndex,idx,AON.CSS.aonNowrap());
			vatPanel.getGrid().setWidget(rowIndex,idx,finalWidget);
			if ( finalWidget instanceof FocusWidget) {
				FocusWidget focusWidget = (FocusWidget) finalWidget;
				focusWidget.setEnabled( column.isEnabled( callback,vatIdx ) );
			}
			idx++;
		}
		return idx;
	}
	
	private void defineExpAccount(final IEditableInvoicePanelCallback callback, final int vatIdx) {
		expAccount = new AonAccountBox(callback.getOccam(), false);
		expAccount.setAccount(
			callback.getVat(vatIdx).getExpAccount(),
			(callback.getInvoice().getRegistry() != null && callback.getInvoice().getRegistry().getId() != null));
		expAccount.addSelectionHandler( event -> {
			Account a = event.getSelectedItem();
			callback.getVat(vatIdx).setExpAccount(a);
			SelectionEvent.<Account>fire(this, a);
		});
	}
	
	private void defineTaxableBase(IEditableInvoicePanelCallback callback, final int vatIdx) {
		taxableBase = new AonDoubleBox(12,4);
		taxableBase.setValue(callback.getVat(vatIdx).getBase());
		taxableBase.addKeyUpHandler( event -> {
			if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
				InvoiceCalculator.reverseCalculate(callback.getInvoice(),callback.getVat(vatIdx), taxableBase.getValue());
				populate(callback, vatIdx);
				ValueChangeEvent.fire(this, callback.getVat(vatIdx) );
			}
		});
		taxableBase.addValueChangeHandler(event -> {
			callback.getVat(vatIdx).setBase( event.getValue() );
			checkCalculate(callback, vatIdx, vatPercent);
		});
	}
	
	private void defineVatPercent(IEditableInvoicePanelCallback callback, final int vatIdx) {
		vatPercent = new AonDoubleBox(6);
		vatPercent.getElement().getStyle().setWidth(40, Unit.PX);
		vatPercent.setValue(callback.getVat(vatIdx).getPercentage());
		vatPercent.addValueChangeHandler(event -> {
			callback.getVat(vatIdx).setPercentage( event.getValue() );
			checkCalculate(callback, vatIdx, vatQuota);
			ValueChangeEvent.fire(this, callback.getVat(vatIdx) );
		});
	}
	
	private void defineVatQuota(IEditableInvoicePanelCallback callback, final int vatIdx) {
		vatQuota = new AonDoubleBox(8);
		vatQuota.setValue(callback.getVat(vatIdx).getQuota());
		vatQuota.addValueChangeHandler(event -> {
			callback.getVat(vatIdx).setQuotaEdited(AonMathUtils.isNotZero(InvoiceCalculator.getQuotaGap(callback.getVat(vatIdx), vatQuota.getValue())));
			if (callback.getVat(vatIdx).isQuotaEdited()) {
				vatQuota.setTitle("Cuota de IVA modificada. Deber\u00EDa ser: " + InvoiceCalculator.getQuota(callback.getVat(vatIdx)));
			} else {
				vatQuota.setTitle(null);
			}
			callback.getVat(vatIdx).setQuota(event.getValue() );
			calculate(callback, vatIdx);
			ValueChangeEvent.fire(this, callback.getVat(vatIdx) );
		});
		vatQuota.addBlurHandler(event -> decorateVatQuota(callback, vatIdx));
		decorateVatQuota(callback, vatIdx);
	}

	private void defineSurchargePercent(IEditableInvoicePanelCallback callback, final int vatIdx) {
		surchargePercent = new AonDoubleBox(6);
		surchargePercent.getElement().getStyle().setWidth(40, Unit.PX);
		surchargePercent.setValue(callback.getVat(vatIdx).getSurcharge());
		surchargePercent.addValueChangeHandler(event -> {
			callback.getVat(vatIdx).setSurcharge(event.getValue() );
			checkCalculate(callback, vatIdx, surchargeQuota);
			ValueChangeEvent.fire(this, callback.getVat(vatIdx) );
		});
	}

	private void defineSurchargeQuota(IEditableInvoicePanelCallback callback, final int vatIdx) {
		surchargeQuota = new AonDoubleBox(8);
		surchargeQuota.setValue(callback.getVat(vatIdx).getSurchargeQuota());
		surchargeQuota.addValueChangeHandler(event -> {
			callback.getVat(vatIdx).setSurchargeQuotaEdited(AonMathUtils.isNotZero(InvoiceCalculator.getSurchargeQuotaGap(callback.getVat(vatIdx), surchargeQuota.getValue())));
			if (callback.getVat(vatIdx).isSurchargeQuotaEdited()) {
				surchargeQuota.setTitle("Cuota de RE modificada. Deber\u00EDa ser: " + InvoiceCalculator.getSurchargeQuota(callback.getVat(vatIdx)));
			} else {
				surchargeQuota.setTitle(null);
			}
			callback.getVat(vatIdx).setSurchargeQuota(event.getValue() );
			calculate(callback, vatIdx);
			ValueChangeEvent.fire(this, callback.getVat(vatIdx) );
		});
		surchargeQuota.addBlurHandler(event -> decorateSurchargeQuota(callback, vatIdx));
		decorateSurchargeQuota(callback, vatIdx);
	}

	private void defineInvestAsset(IEditableInvoicePanelCallback callback, final int vatIdx) {
		investAsset = new InvestAssetListBox( callback ); 
		investAsset.setValue(callback, callback.getVat(vatIdx).getInvestAsset());
		investAsset.addChangeHandler(event -> {
			if (investAsset.getValue(callback) == null) {
				callback.getVat(vatIdx).setInvestAsset( null );
				callback.getVat(vatIdx).setDeductiblePercent(100.0);
				callback.getVat(vatIdx).setDirectTaxPercent(100.0);
			} else {
				callback.getVat(vatIdx).setInvestAsset( investAsset.getValue(callback).getId() );
				callback.getVat(vatIdx).setDeductiblePercent(investAsset.getValue(callback).getVatPercent());
				callback.getVat(vatIdx).setDirectTaxPercent(investAsset.getValue(callback).getRetentionPercent());
			}
			calculate(callback, vatIdx);
			vatPanel.paint(callback);
			ValueChangeEvent.fire(this, callback.getVat(vatIdx) );
		});
	}
	
	private void defineInputVatAccount(IEditableInvoicePanelCallback callback, final int vatIdx) {
		inputVatAccount = new AonAccountBox(callback.getOccam(), false);
		inputVatAccount.setRequired(false);
		inputVatAccount.setAccount(callback.getVat(vatIdx).getInputAccount(),true);
		inputVatAccount.addSelectionHandler( event -> {
			Account a = event.getSelectedItem();
			callback.getVat(vatIdx).setInputAccount(a);
			ValueChangeEvent.fire(this, callback.getVat(vatIdx) );
		});
	}
	
	private void defineOutputVatAccount(IEditableInvoicePanelCallback callback, final int vatIdx) {
		outputVatAccount = new AonAccountBox(callback.getOccam(), false);
		outputVatAccount.setRequired(false);
		outputVatAccount.setAccount(callback.getVat(vatIdx).getOutputAccount(),true);
		outputVatAccount.addSelectionHandler( event -> {
			Account a = event.getSelectedItem();
			callback.getVat(vatIdx).setOutputAccount(a);
			ValueChangeEvent.fire(this, callback.getVat(vatIdx) );
		});
	}

	private void defineWithholding(IEditableInvoicePanelCallback callback, final int vatIdx) {
		withholding  = new CheckBox();
		withholding.setValue(callback.getVat(vatIdx).isWithholding());
		withholding.addClickHandler( event -> {
			callback.getVat(vatIdx).setWithholding(withholding.getValue());
			checkCalculate(callback, vatIdx, null);
			ValueChangeEvent.fire(this, callback.getVat(vatIdx) );
		});
	}

	private void definePrepayment(IEditableInvoicePanelCallback callback, final int vatIdx) {
		prepayment  = new CheckBox();
		prepayment.setValue(callback.getVat(vatIdx).isPrepayment());
		prepayment.addClickHandler( event -> {
			callback.getVat(vatIdx).setPrepayment(prepayment.getValue());
			enableRowElements( callback, vatIdx );
			checkCalculate(callback, vatIdx, null);
			ValueChangeEvent.fire(this, callback.getVat(vatIdx) );
		});
	}

	private void defineRemoveButton(IEditableInvoicePanelCallback callback, final int vatIdx) {
		removeButton = new AonTableButton( AON.MSG.deleteAction(),AON.CSS.aonIconDelete() );
		removeButton.addClickHandler(event -> {
			if (callback.getInvoice().getVats().size() > 1 ) {
				AonConfirmDialog cd = new AonConfirmDialog();
				cd.confirm(AON.MSG.confirmDeleteAction(), AON.MSG.deleteAction(),() -> {
					callback.getInvoice().getVats().remove( vatIdx );
					vatPanel.paint(callback);
					ValueChangeEvent.fire(this, callback.getVat(vatIdx) );
				});
			} else {
				callback.getModule().onError("Al menos debe haber una l\u00EDnea.");
			}
		});
	}

	private void defineDedPercent(IEditableInvoicePanelCallback callback, int vatIdx) {
		dedPercent = new AonDoubleBox();
		dedPercent.getElement().getStyle().setWidth(40, Unit.PX);
		dedPercent.setValue(callback.getVat(vatIdx).getDeductiblePercent());
		dedPercent.addValueChangeHandler(event -> {
			Double p = event.getValue();
			if (AonMathUtils.isGreatherThan(p,100)) p = 100.0;
			if (AonMathUtils.isLessThan(p,0)) p = 0.0;
			callback.getVat(vatIdx).setDeductiblePercent( p );
			checkCalculate(callback, vatIdx,dedQuota);
			ValueChangeEvent.fire(this, callback.getVat(vatIdx) );
		});
	}
	
	private void defineDedQuota(IEditableInvoicePanelCallback callback, int vatIdx) {
		dedQuota = new AonDoubleBox();
		dedQuota.setValue(callback.getVat(vatIdx).getDeductibleQuota());
		dedQuota.addValueChangeHandler(event -> {
			callback.getVat(vatIdx).setDeductibleQuotaEdited(AonMathUtils.isNotZero(InvoiceCalculator.getDeductibleQuotaGap(callback.getVat(vatIdx), dedQuota.getValue())));
			if (callback.getVat(vatIdx).isDeductibleQuotaEdited()) {
				dedQuota.setTitle("Cuota de IVA deducible modificada. Deber\u00EDa ser: " + InvoiceCalculator.getDeductibleQuota(callback.getVat(vatIdx)));
			} else {
				dedQuota.setTitle(null);
			}
			callback.getVat(vatIdx).setDeductibleQuota(event.getValue() );
			calculate(callback, vatIdx);
			ValueChangeEvent.fire(this, callback.getVat(vatIdx) );
		});
		dedQuota.addBlurHandler(event -> decorateDeductibleQuota(callback, vatIdx));
		decorateDeductibleQuota(callback, vatIdx);
	}
	
	private void defineNoDedQuota(IEditableInvoicePanelCallback callback, int vatIdx) {
		noDedQuota = new AonDoubleBox();
		noDedQuota.setValue(callback.getVat(vatIdx).getNoDeductibleQuota());
		noDedQuota.setEnabled(false);
	}

	private void defineAdjAccount(IEditableInvoicePanelCallback callback, int vatIdx) {
		adjAccount = new AonAccountBox(callback.getOccam(), false);
		adjAccount.setRequired(false);
		adjAccount.setAccount(callback.getVat(vatIdx).getAdjAccount(),true);
		adjAccount.addSelectionHandler( event -> {
			Account a = event.getSelectedItem();
			callback.getVat(vatIdx).setAdjAccount(a);
			ValueChangeEvent.fire(this, callback.getVat(vatIdx) );
		});
	}

	private void defineDirectTaxPercent(IEditableInvoicePanelCallback callback, int vatIdx) {
		directTaxPercent = new AonDoubleBox();
		directTaxPercent.getElement().getStyle().setWidth(40, Unit.PX);
		directTaxPercent.setValue(callback.getVat(vatIdx).getDirectTaxPercent());
		directTaxPercent.addValueChangeHandler(event -> {
			Double p = event.getValue();
			if (p > 100) p = 100.0;
			if (p < 0) p = 0.0;
			callback.getVat(vatIdx).setDirectTaxPercent( p );
			checkCalculate(callback, vatIdx, directTaxNoDedExpenses);
			ValueChangeEvent.fire(this, callback.getVat(vatIdx) );
		});
	}
	
	private void defineDirectTaxNoDedExpenses(IEditableInvoicePanelCallback callback, int vatIdx) {
		directTaxNoDedExpenses= new AonDoubleBox();
		directTaxNoDedExpenses.setValue(callback.getVat(vatIdx).getDirectTaxNoDedExpenses());
		directTaxNoDedExpenses.setEnabled(false);
	}
	
	private void defineDirectTaxDedExpenses(IEditableInvoicePanelCallback callback, int vatIdx) {
		directTaxDedExpenses = new AonDoubleBox();
		directTaxDedExpenses.setValue(callback.getVat(vatIdx).getDirectTaxDedExpenses());
		directTaxDedExpenses.setEnabled(false);
	}

	private void defineDirectTaxAccount(IEditableInvoicePanelCallback callback, int vatIdx) {
		directTaxAccount = new AonAccountBox(callback.getOccam(), false);
		directTaxAccount.setAccount(callback.getVat(vatIdx).getAdjDirectTaxAccount(),true);
		directTaxAccount.addSelectionHandler( event -> {
			Account a = event.getSelectedItem();
			callback.getVat(vatIdx).setAdjDirectTaxAccount(a);
			SelectionEvent.<Account>fire(this, a);
		});
	}

	private void decorateVatQuota(IEditableInvoicePanelCallback callback, final int vatIdx) {
		double quota = InvoiceCalculator.getQuota( callback.getVat(vatIdx) );
		double gap = InvoiceCalculator.getQuotaGap( callback.getVat(vatIdx) , vatQuota.getValue());
		decorateEditableQuota(gap, quota, vatQuota);
	}
	
	private void decorateSurchargeQuota(IEditableInvoicePanelCallback callback, final int vatIdx) {
		double quota = InvoiceCalculator.getSurchargeQuota(callback.getVat(vatIdx));
		double gap = InvoiceCalculator.getSurchargeQuotaGap(callback.getVat(vatIdx), surchargeQuota.getValue());
		decorateEditableQuota(gap, quota, surchargeQuota);
	}

	private void decorateDeductibleQuota(IEditableInvoicePanelCallback callback, final int vatIdx) {
		double quota = InvoiceCalculator.getDeductibleQuota(callback.getVat(vatIdx));
		double gap = InvoiceCalculator.getDeductibleQuotaGap(callback.getVat(vatIdx), dedQuota.getValue());
		decorateEditableQuota(gap, quota, dedQuota);
	}
	
	private void decorateDirectTaxNoDedExpenses(IEditableInvoicePanelCallback callback, final int vatIdx) {
		double quota = InvoiceCalculator.getDirectTaxNoDedExpenses(callback.getVat(vatIdx));
		double gap = InvoiceCalculator.getDirectTaxNoDedExpensesGap(callback.getVat(vatIdx), directTaxNoDedExpenses.getValue());
		decorateEditableQuota(gap, quota, directTaxNoDedExpenses);
	}

	private void decorateEditableQuota(double gap, double quota, AonDoubleBox editableBox) {
		editableBox.removeStyleName(AON.CSS.aonChanged());
		editableBox.removeStyleName(AON.CSS.aonInputError());
		if (AonMathUtils.isNotZero( gap )) {
			if (gap > 1) {
				editableBox.addStyleName(AON.CSS.aonInputError());
				editableBox.setTitle( AON.MSG.editedValueWarning(quota, gap) );
			} else {
				editableBox.addStyleName(AON.CSS.aonChanged());
				editableBox.setTitle( AON.MSG.editedValue(quota) );
			}
		} else {
			editableBox.setTitle(null);
		}
	}
	
	
	private void checkCalculate(IEditableInvoicePanelCallback callback, final int vatIdx, AonDoubleBox toFocus) {
		if (callback.getVat(vatIdx).isAnyQuotaEdited()) {
			AonConfirmDialog.showConfirm(AON.MSG.manualChangeConfirm(),new AonConfirmDialogCallback() {
				
				@Override
				public void onCancel() {
					if (toFocus != null) {
						toFocus.selectAll();
						toFocus.setFocus(true);
					}
					ValueChangeEvent.fire(InvoiceVATPanelRow.this, callback.getVat(vatIdx) );
				}
				
				@Override
				public void onAccept() {
					callback.getVat(vatIdx).setQuotaEdited(false);
					callback.getVat(vatIdx).setSurchargeQuotaEdited(false);
					callback.getVat(vatIdx).setDeductibleQuotaEdited(false);
					calculate(callback, vatIdx);
					decorateVatQuota(callback, vatIdx);
					decorateSurchargeQuota(callback, vatIdx);
					decorateDeductibleQuota(callback, vatIdx);
					decorateDirectTaxNoDedExpenses(callback, vatIdx);
					if (toFocus != null) {
						toFocus.selectAll();
						toFocus.setFocus(true);
					}
					ValueChangeEvent.fire(InvoiceVATPanelRow.this, callback.getVat(vatIdx) );
				}
			});
		} else {
			calculate(callback, vatIdx);
			ValueChangeEvent.fire(this, callback.getVat(vatIdx) );
		}
	}
	protected void calculate(IEditableInvoicePanelCallback callback, final int vatIdx) {
		InvoiceCalculator.calculate(callback.getInvoice(),callback.getVat(vatIdx));
		populate(callback, vatIdx);
	}

	void populate(IEditableInvoicePanelCallback callback, final int vatIdx) {
		taxableBase.setValue( callback.getVat(vatIdx).getBase() , false);
		vatPercent.setValue( callback.getVat(vatIdx).getPercentage() , false);
		vatQuota.setValue( callback.getVat(vatIdx).getQuota() , false);
		surchargePercent.setValue( callback.getVat(vatIdx).getSurcharge() , false);
		surchargeQuota.setValue( callback.getVat(vatIdx).getSurchargeQuota() , false);
		withholding.setValue(callback.getVat(vatIdx).isWithholding() , false);
		dedPercent.setValue( callback.getVat(vatIdx).getDeductiblePercent() , false);
		dedQuota.setValue( callback.getVat(vatIdx).getDeductibleQuota() , false);
		noDedQuota.setValue( callback.getVat(vatIdx).getNoDeductibleQuota() , false);
		directTaxPercent.setValue( callback.getVat(vatIdx).getDirectTaxPercent() , false);
		directTaxNoDedExpenses.setValue( callback.getVat(vatIdx).getDirectTaxNoDedExpenses() , false);
		directTaxDedExpenses.setValue( callback.getVat(vatIdx).getDirectTaxDedExpenses() , false);
	}

	void withholdingChanged(Boolean value) {
		withholding.setValue(value && !prepayment.getValue(),false);
	}
	void prepaymentChanged(Boolean value) {
		prepayment.setValue(value,false);
	}
	
	void enableRowElements(IEditableInvoicePanelCallback callback, final int vatIdx) {
		paintRow( callback , vatIdx);
	}

	@Override
	public int getTabIndex() {
		return expAccount.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		expAccount.setAccessKey(key);
		
	}

	@Override
	public void setFocus(boolean focused) {
		expAccount.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		expAccount.setTabIndex(index);
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Account> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<InvoiceVAT> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

}

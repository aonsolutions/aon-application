package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.EditableInvoicePanel.IEditableInvoicePanelCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class InvoiceVATPanel extends ScrollPanel implements HasValueChangeHandlers<InvoiceVAT>, HasSelectionHandlers<Account>, Focusable {
	
	private FlowPanel tab;
	private IEditableInvoicePanelCallback callback;
	private LinkedList<InvoicePanelRow> rows;
	private LinkedList<Account> suggestedAccounts;
	
	private FlowPanel vatPercentLabelCell;
	private FlowPanel vatQuotaLabelCell;
	private FlowPanel reLabelCell;
	private FlowPanel reQuotaLabelCell;
	private FlowPanel investAssetLabelCell;
	private FlowPanel dedPercentLabelCell;
	private FlowPanel dedQuotaLabelCell;
	private FlowPanel adjAccountLabelCell;
	private FlowPanel inputVatLabelCell;
	private FlowPanel outputVatLabelCell;
	private FlowPanel withholdingLabelCell;
	private FlowPanel prepaymentLabelCell;
	
	private AonTableButton addButton;
	
	private class InvestAssetListBox extends ListBox {
		private InvestAssetListBox() {
			setWidth("90px");
			addItem("------",(String) null);
			if (callback.getConfiguration().isInvestAssetsAvailable()) {
				for (InvestAsset asset : callback.getConfiguration().getInvestAssets()) {
					addItem(asset.getDescription(),AonNumberUtils.toString(asset.getId()));
				}
			}
		}
		
		private InvestAsset getValue() {
			if (getSelectedIndex() == 0) return null;
			return callback.getConfiguration().getInvestAssets().get(getSelectedIndex() -1 );
		}

		public void setValue(Integer investAsset) {
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
	
	public InvoiceVATPanel(IEditableInvoicePanelCallback callback) {
		setStyleName(AON.CSS.aonWidthAll());
		setSuggestedAccounts(callback.getInvoice().getSuggestedAccounts());
		setCallback(callback);
		paint();
	}
	
	public void setCallback(IEditableInvoicePanelCallback invoiceCallback) {
		this.callback = invoiceCallback;
	}

	public void setSuggestedAccounts(LinkedList<Account> suggestedAccounts) {
		this.suggestedAccounts = suggestedAccounts;
	}
	
	private boolean isUndeductible() {
		return this.callback != null && this.callback.getInvoice() != null && this.callback.getInvoice().isUndeductible();
	}

	void paint() {
		this.clear();
		tab = new FlowPanel();
		tab.setStyleName(AON.CSS.aonDisplayGrid());
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonMarginTopSep());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.getElement().getStyle().setBackgroundColor(EditableInvoicePanel.INNER_BACKGROUND_COLOR);
		this.setWidget(tab);
		paintHeader();
		paintRows();
		paintButtons();
	}

	private void paintHeader() {
		FlowPanel headerRow = new FlowPanel();
		headerRow.setStyleName(AON.CSS.aonDisplayGridHeaderRow());
		tab.add(headerRow);

		
		headerRow.add(getHeaderCell(new Label(AON.MSG.accountAbr())));
		headerRow.add(getHeaderCell(new Label(AON.MSG.taxableBaseAbr())));
		vatPercentLabelCell = getHeaderCell(new Label("% IVA")); 
		headerRow.add(vatPercentLabelCell);
		vatQuotaLabelCell = getHeaderCell(new Label(AON.MSG.vatQuota()));
		headerRow.add(vatQuotaLabelCell);
		reLabelCell = getHeaderCell(new Label("% RE"));
		headerRow.add(reLabelCell);
		reQuotaLabelCell = getHeaderCell(new Label(AON.MSG.surchargeQuota()));
		headerRow.add(reQuotaLabelCell);
		investAssetLabelCell = getHeaderCell(new Label(AON.MSG.actInvestAsset()));
		headerRow.add(investAssetLabelCell);
		dedPercentLabelCell = getHeaderCell(new Label(AON.MSG.dedPercent()));
		headerRow.add(dedPercentLabelCell);
		dedQuotaLabelCell = getHeaderCell(new Label(AON.MSG.dedQuota()));
		headerRow.add(dedQuotaLabelCell);
		adjAccountLabelCell = getHeaderCell(new Label(AON.MSG.adjAccount()));
		headerRow.add(adjAccountLabelCell);
		inputVatLabelCell = getHeaderCell(new Label(AON.MSG.inputVatAccount()));
		headerRow.add(inputVatLabelCell);
		outputVatLabelCell = getHeaderCell(new Label(AON.MSG.outputVatAccount()));
		headerRow.add(outputVatLabelCell);
		withholdingLabelCell = getHeaderCell(new Label("IRPF"));
		headerRow.add(withholdingLabelCell);
		prepaymentLabelCell = getHeaderCell(new Label("Supl."));
		headerRow.add(prepaymentLabelCell);
		headerRow.add(getHeaderCell(new Label("")));

		Label l = new Label("");
		FlowPanel cell = new FlowPanel();
		cell.setStyleName(AON.CSS.aonDisplayTableCell());
		cell.addStyleName(AON.CSS.aonWidthAll());
		cell.add(l);
		headerRow.add(cell);
	}
	
	private FlowPanel getHeaderCell(Widget widget) {
		FlowPanel cell = new FlowPanel();
		cell.setStyleName(AON.CSS.aonDisplayGridHeaderCell());
		cell.addStyleName(AON.CSS.aonNowrap());
		cell.add(widget);
		return cell;
	}

	private void paintButtons() {
		
		FlowPanel buttonsRow = new FlowPanel();
		buttonsRow.setStyleName(AON.CSS.aonDisplayGridFooterRow());
		tab.add(buttonsRow);
		
		addButton = new AonTableButton(AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		addButton.setAccessKey( 'L' );
		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int idx = callback.getInvoice().getVats().size() - 1;
				InvoiceVAT last = callback.getInvoice().getVats().get( idx );
				InvoiceVAT vat = new InvoiceVAT()
					.setExpAccountId(last.getExpAccountId())
					.setExpAccountCode(last.getExpAccountCode())
					.setExpAccountDescription(last.getExpAccountDescription())
					.setPercentage(last.getPercentage())
					.setSurcharge(last.getSurcharge())
					.setWithholding(last.isWithholding())
					.setInputAccountId(last.getInputAccountId())
					.setInputAccountCode(last.getInputAccountCode())
					.setInputAccountDescription(last.getInputAccountDescription())
					.setOutputAccountId(last.getOutputAccountId())
					.setOutputAccountCode(last.getOutputAccountCode())
					.setOutputAccountDescription(last.getOutputAccountDescription())
					.setAdjAccountId(last.getAdjAccountId())
					.setAdjAccountCode(last.getAdjAccountCode())
					.setAdjAccountDescription(last.getAdjAccountDescription());
				callback.getInvoice().addVat( vat );
				addRow(vat, true);
			}
		});
		buttonsRow.add( addButton );
		tab.add(buttonsRow);
	}
	
	private void paintRows() {
		rows = new LinkedList<InvoicePanelRow>();
		if (callback.getInvoice().getVats() != null && callback.getInvoice().getVats().size() > 0) {
			for (InvoiceVAT vat : callback.getInvoice().getVats()) {
				addRow(vat, false);
			}
		} 
	}

	private void addRow(InvoiceVAT vat, boolean focus) {
		if (vat.getExpAccountId() == null && suggestedAccounts != null && !suggestedAccounts.isEmpty() && suggestedAccounts.size() > rows.size()) {
			Account a = suggestedAccounts.get(rows.size());
			vat.setExpAccountId(a.getId());
			vat.setExpAccountCode(a.getCode());
			vat.setExpAccountDescription(a.getDescription());
		}
		InvoicePanelRow invoiceRow = new InvoicePanelRow(vat, focus);
		tab.add(invoiceRow);
		rows.add(invoiceRow);
		if (rows.size() > 1) {
			callback.enableInvoiceTotal( false );
		}
	}


	private class InvoicePanelRow extends FlowPanel implements Focusable {
		
		private final FlowPanel expAccountCell = new FlowPanel();
		private final FlowPanel taxableBaseCell = new FlowPanel();
		private final FlowPanel vatPercentCell = new FlowPanel();
		private final FlowPanel vatQuotaCell = new FlowPanel();
		private final FlowPanel surchargePercentCell = new FlowPanel();
		private final FlowPanel surchargeQuotaCell = new FlowPanel();
		private final FlowPanel inputVatAccountCell = new FlowPanel();
		private final FlowPanel outputVatAccountCell = new FlowPanel();
		private final FlowPanel withholdingCell = new FlowPanel();
		private final FlowPanel prepaymentCell = new FlowPanel();
		private final FlowPanel removeButtonCell = new FlowPanel();
		private final FlowPanel investAssetCell = new FlowPanel(); 
		private final FlowPanel dedPercentCell = new FlowPanel();
		private final FlowPanel dedQuotaCell = new FlowPanel();
		private final FlowPanel adjAccountCell = new FlowPanel();

		private final AonAccountBox expAccount = new AonAccountBox(callback.getCurrentDomainName(), callback.getCurrentDomainId(), callback.getCurrentUser(), false);
		private final AonDoubleBox taxableBase = new AonDoubleBox(12,4);
		private final AonDoubleBox vatPercent = new AonDoubleBox(6);
		private final AonDoubleBox vatQuota = new AonDoubleBox(8);
		private final AonDoubleBox surchargePercent = new AonDoubleBox(6);
		private final AonDoubleBox surchargeQuota = new AonDoubleBox(8);
		private final AonAccountBox inputVatAccount = new AonAccountBox(callback.getCurrentDomainName(), callback.getCurrentDomainId(), callback.getCurrentUser(), false);
		private final AonAccountBox outputVatAccount = new AonAccountBox(callback.getCurrentDomainName(), callback.getCurrentDomainId(), callback.getCurrentUser(), false);
		private final CheckBox withholding  = new CheckBox();
		private final CheckBox prepayment  = new CheckBox();
		private final AonTableButton removeButton = new AonTableButton( AON.MSG.deleteAction(),AON.CSS.aonIconDelete() );
		private final InvestAssetListBox investAsset = new InvestAssetListBox(); 
		private final AonDoubleBox dedPercent = new AonDoubleBox(6);
		private final AonDoubleBox dedQuota = new AonDoubleBox(8);
		private final AonAccountBox adjAccount = new AonAccountBox(callback.getCurrentDomainName(), callback.getCurrentDomainId(), callback.getCurrentUser(), false);
		
		private InvoicePanelRow(final InvoiceVAT vat, boolean focus) {
			setStyleName(AON.CSS.aonDisplayGridRow());
			
			inputVatAccount.setRequired(false);
			outputVatAccount.setRequired(false);
			adjAccount.setRequired(false);
			final boolean otherLineWithInvestAssests = callback.isInvestAssetsAvailable() && isOtherLineWithInvestAssests( this );
			expAccount.setValue(vat.getExpAccountId(),vat.getExpAccountCode(),vat.getExpAccountDescription(),
					(callback.getInvoice().getRegistry() != null && callback.getInvoice().getRegistry().getId() != null));
			expAccount.addSelectionHandler( new SelectionHandler<Account>() {
				@Override
				public void onSelection(SelectionEvent<Account> event) {
					Account a = event.getSelectedItem();
					if (a != null) {
						vat.setExpAccountId(a.getId());
						vat.setExpAccountCode(a.getCode());
						vat.setExpAccountDescription(a.getDescription());
					} else {
						vat.setExpAccountId(null);
						vat.setExpAccountCode(null);
						vat.setExpAccountDescription(null);
					}
					SelectionEvent.<Account>fire(InvoiceVATPanel.this, a);
				}
			});
			add( getCell(expAccountCell,expAccount) );
			
			taxableBase.setValue(vat.getBase());
			taxableBase.addKeyUpHandler( new KeyUpHandler() {
				public void onKeyUp(KeyUpEvent event) {
					if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
						InvoiceCalculator.reverseCalculate(callback.getInvoice(),vat, taxableBase.getValue());
						populate(vat);
						ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
					}
				}
			});
			taxableBase.addValueChangeHandler(new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					vat.setBase( event.getValue() );
					checkCalculate(vat, vatPercent);
				}
			});
			add(getCell(taxableBaseCell,taxableBase));

			vatPercent.getElement().getStyle().setWidth(40, Unit.PX);
			vatPercentLabelCell.setVisible(!callback.getInvoice().isUndeductible());
			vatPercentCell.setVisible( isVatEnabledForCell(callback,vat));
			vatPercent.setValue(vat.getPercentage());
			vatPercentCell.setVisible( isVatEnabledForData(callback,vat));
			vatPercent.addValueChangeHandler(new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					vat.setPercentage( event.getValue() );
					checkCalculate(vat, vatQuota);
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			add(getCell(vatPercentCell,vatPercent));
			
			vatQuotaLabelCell.setVisible(!callback.getInvoice().isUndeductible());
			vatQuotaCell.setVisible(isVatEnabledForCell(callback,vat));
			vatQuota.setValue(vat.getQuota());
			vatQuota.setVisible(isVatEnabledForData(callback,vat));
			vatQuota.addValueChangeHandler(new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					vat.setQuotaEdited(AonMathUtils.isNotZero(InvoiceCalculator.getQuotaGap(vat, vatQuota.getValue())));
					if (vat.isQuotaEdited()) {
						vatQuota.setTitle("Cuota de IVA modificada. Deber\u00EDa ser: " + InvoiceCalculator.getQuota(vat));
					} else {
						vatQuota.setTitle(null);
					}
					vat.setQuota(event.getValue() );
					calculate(vat);
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			vatQuota.addBlurHandler(new BlurHandler() {
				
				@Override
				public void onBlur(BlurEvent event) {
					decorateVatQuota(vat);
				}
			});
			decorateVatQuota(vat);
			add(getCell(vatQuotaCell,vatQuota));
			
			surchargePercent.setValue(vat.getSurcharge());
			surchargePercentCell.setVisible( isSurchargeEnabled(callback, vat) );
			reLabelCell.setVisible(callback.getInvoice().isSurcharge());
			surchargePercent.addValueChangeHandler(new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					vat.setSurcharge(event.getValue() );
					checkCalculate(vat, surchargeQuota);
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			add(getCell(surchargePercentCell,surchargePercent));

			surchargeQuota.setValue(vat.getSurchargeQuota());
			surchargeQuotaCell.setVisible( isSurchargeEnabled(callback,vat) );
			reQuotaLabelCell.setVisible(callback.getInvoice().isSurcharge());
			surchargeQuota.addValueChangeHandler(new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					vat.setSurchargeQuotaEdited(AonMathUtils.isNotZero(InvoiceCalculator.getSurchargeQuotaGap(vat, surchargeQuota.getValue())));
					if (vat.isSurchargeQuotaEdited()) {
						surchargeQuota.setTitle("Cuota de RE modificada. Deber\u00EDa ser: " + InvoiceCalculator.getSurchargeQuota(vat));
					} else {
						surchargeQuota.setTitle(null);
					}
					vat.setSurchargeQuota(event.getValue() );
					calculate(vat);
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			surchargeQuota.addBlurHandler(new BlurHandler() {
				
				@Override
				public void onBlur(BlurEvent event) {
					decorateSurchargeQuota(vat);
				}
			});
			decorateSurchargeQuota(vat);
			add(getCell(surchargeQuotaCell,surchargeQuota));
			
			investAsset.setValue(vat.getInvestAsset());
			investAssetCell.setVisible(isInvestAssetsEnabled(callback,vat));
			investAssetLabelCell.setVisible(callback.isInvestAssetsAvailable());
			investAsset.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					if (investAsset.getValue() == null) {
						vat.setInvestAsset( null );
						vat.setDeductiblePercent(100.0);
					} else {
						vat.setInvestAsset( investAsset.getValue().getId() );
						vat.setDeductiblePercent(investAsset.getValue().getPercent());
					}
					dedPercent.setValue(vat.getDeductiblePercent(),false);
					
					dedPercentCell.setVisible(investAsset.getValue() != null);
					dedQuotaCell.setVisible(investAsset.getValue() != null);
					adjAccountCell.setVisible(investAsset.getValue() != null);
					dedPercentLabelCell.setVisible(investAsset.getValue() != null || otherLineWithInvestAssests);
					dedQuotaLabelCell.setVisible(investAsset.getValue() != null || otherLineWithInvestAssests);
					adjAccountLabelCell.setVisible(investAsset.getValue() != null || otherLineWithInvestAssests);
					
					checkCalculate(vat,dedPercent);
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}

			});
			add(getCell(investAssetCell,investAsset));

			dedPercent.getElement().getStyle().setWidth(50, Unit.PX);
			dedPercent.setValue(vat.getDeductiblePercent());
			dedPercentCell.setVisible(isInvestAssetsEnabled(callback,vat) && vat.getInvestAsset() != null);
			dedPercentLabelCell.setVisible(isInvestAssetsEnabled(callback,vat)  && otherLineWithInvestAssests);
			dedPercent.addValueChangeHandler(new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					Double p = event.getValue();
					if (p > 100) p = 100.0;
					if (p < 0) p = 0.0;
					vat.setDeductiblePercent( p );
					checkCalculate(vat,dedQuota);
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			add(getCell(dedPercentCell,dedPercent));
			
			dedQuota.setValue(vat.getDeductibleQuota());
			dedQuotaCell.setVisible(isInvestAssetsEnabled(callback,vat) && vat.getInvestAsset() != null);
			dedQuotaLabelCell.setVisible(isInvestAssetsEnabled(callback,vat) && otherLineWithInvestAssests);
			dedQuota.addValueChangeHandler(new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					vat.setDeductibleQuotaEdited(AonMathUtils.isNotZero(InvoiceCalculator.getDeductibleQuotaGap(vat, dedQuota.getValue())));
					if (vat.isSurchargeQuotaEdited()) {
						dedQuota.setTitle("Cuota de IVA deducible modificada. Deber\u00EDa ser: " + InvoiceCalculator.getDeductibleQuota(vat));
					} else {
						dedQuota.setTitle(null);
					}
					vat.setDeductibleQuota(event.getValue() );
					calculate(vat);
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			dedQuota.addBlurHandler(new BlurHandler() {
				
				@Override
				public void onBlur(BlurEvent event) {
					decorateDeductibleQuota(vat);
				}
			});
			decorateDeductibleQuota(vat);
			add(getCell(dedQuotaCell,dedQuota));

			adjAccount.setValue(vat.getAdjAccountId(),vat.getAdjAccountCode()
					,vat.getAdjAccountDescription(),true);
			adjAccountCell.setVisible(isInvestAssetsEnabled(callback,vat) && vat.getInvestAsset() != null && vat.getDeductiblePercent() != 100);
			adjAccountLabelCell.setVisible(isInvestAssetsEnabled(callback,vat) && otherLineWithInvestAssests);
			adjAccount.addSelectionHandler( new SelectionHandler<Account>() {
				
				@Override
				public void onSelection(SelectionEvent<Account> event) {
					Account a = event.getSelectedItem();
					if (a != null) {
						vat.setAdjAccountId(a.getId());
						vat.setAdjAccountCode(a.getCode());
						vat.setAdjAccountDescription(a.getDescription());
					} else {
						vat.setAdjAccountId(null);
						vat.setAdjAccountCode(null);
						vat.setAdjAccountDescription(null);
					}
					SelectionEvent.<Account>fire(InvoiceVATPanel.this, a);
				}
			});
			add(getCell(adjAccountCell,adjAccount));

			inputVatAccount.setValue(vat.getInputAccountId(),vat.getInputAccountCode()
					,vat.getInputAccountDescription(),true);
			inputVatAccountCell.setVisible(isInputVatEnabledForCell(callback,vat) );
			inputVatLabelCell.setVisible(isInputVatEnabledForCell(callback,vat));
			inputVatAccount.setVisible(isInputVatEnabledForData(callback,vat));
			inputVatAccount.addSelectionHandler( new SelectionHandler<Account>() {
				
				@Override
				public void onSelection(SelectionEvent<Account> event) {
					Account a = event.getSelectedItem();
					vat.setInputAccountId(a.getId());
					vat.setInputAccountCode(a.getCode());
					vat.setInputAccountDescription(a.getDescription());
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			add(getCell(inputVatAccountCell,inputVatAccount));

			outputVatAccount.setValue(vat.getOutputAccountId(),vat.getOutputAccountCode()
					,vat.getOutputAccountDescription(),true);
			outputVatAccountCell.setVisible(isOutputVatEnabledForCell(callback,vat) );
			outputVatLabelCell.setVisible(isOutputVatEnabledForCell(callback,vat) );
			outputVatAccount.setVisible(isOutputVatEnabledForData(callback,vat) );
			outputVatAccount.addSelectionHandler( new SelectionHandler<Account>() {
				
				@Override
				public void onSelection(SelectionEvent<Account> event) {
					Account a = event.getSelectedItem();
					if (a!=null) {
						vat.setOutputAccountId(a.getId());
						vat.setOutputAccountCode(a.getCode());
						vat.setOutputAccountDescription(a.getDescription());
					} else {
						vat.setOutputAccountId(null);
						vat.setOutputAccountCode(null);
						vat.setOutputAccountDescription(null);
					}
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			add(getCell(outputVatAccountCell,outputVatAccount));
			
			withholding.setValue(vat.isWithholding());
			withholdingCell.setVisible(callback.getInvoice().isWithholding() && !vat.isPrepayment());
			withholdingLabelCell.setVisible(callback.getInvoice().isWithholding());
			withholding.addClickHandler( new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					vat.setWithholding(withholding.getValue());
					checkCalculate(vat,null);
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			withholdingCell.addStyleName(AON.CSS.aonTextCenter());
			add(getCell(withholdingCell,withholding));
			
			prepayment.setValue(vat.isPrepayment());
			prepaymentCell.setVisible(callback.getInvoice().hasPrepayments());
			prepaymentLabelCell.setVisible(callback.getInvoice().hasPrepayments());
			prepayment.addClickHandler( new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					vat.setPrepayment(prepayment.getValue());
					enableRowElements( InvoicePanelRow.this );
					checkCalculate(vat,null);
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			prepaymentCell.addStyleName(AON.CSS.aonTextCenter());
			add(getCell(prepaymentCell,prepayment));

			removeButton.setTabIndex(Integer.MAX_VALUE);
			removeButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (callback.getInvoice().getVats().size() > 1 ) {
						AonConfirmDialog cd = new AonConfirmDialog();
						cd.confirm(AON.MSG.confirmDeleteAction(), AON.MSG.deleteAction(),new AonConfirmDialogCallback() {
							
							@Override
							public void onCancel() {
							}
							
							@Override
							public void onAccept() {
								rows.remove( InvoicePanelRow.this );
								InvoicePanelRow.this.removeFromParent();
								callback.getInvoice().getVats().remove( vat );
								paint();
								ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
							}
						});
					} else {
						callback.getModule().onError("Al menos debe haber una l\u00EDnea.");
					}
					
				}
			});
			add(getCell(removeButtonCell,removeButton));
			
			Label l = new Label("");
			FlowPanel cell = new FlowPanel();
			cell.setStyleName(AON.CSS.aonDisplayTableCell());
			cell.addStyleName(AON.CSS.aonWidthAll());
			cell.add(l);
			add(cell);

			if (focus) {
				expAccount.setFocus(true);
			}
		}
		
		private Widget getCell(FlowPanel cell,Widget widget) {
			cell.addStyleName(AON.CSS.aonDisplayGridCell());
			cell.add(widget);
			return cell;
		}
		private void decorateDeductibleQuota(InvoiceVAT vat) {
			double quota = InvoiceCalculator.getDeductibleQuota(vat);
			double gap = InvoiceCalculator.getDeductibleQuotaGap(vat, dedQuota.getValue());
			decorateEditableQuota(gap, quota, dedQuota);
		}

		private void decorateSurchargeQuota(InvoiceVAT vat) {
			double quota = InvoiceCalculator.getSurchargeQuota(vat);
			double gap = InvoiceCalculator.getSurchargeQuotaGap(vat, surchargeQuota.getValue());
			decorateEditableQuota(gap, quota, surchargeQuota);
		}

		private void decorateVatQuota(InvoiceVAT vat) {
			double quota = InvoiceCalculator.getQuota(vat);
			double gap = InvoiceCalculator.getQuotaGap(vat, vatQuota.getValue());
			decorateEditableQuota(gap, quota, vatQuota);
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

		private boolean isOtherLineWithInvestAssests(InvoicePanelRow row) {
			for (InvoicePanelRow roww : rows) {
				if (roww!=row && roww.investAsset.getValue() != null) return true;
			}
			return false;
		}

		private void enableVat( boolean undeductible ) {
			vatPercentCell.setVisible(!undeductible);
			vatPercent.setVisible(!undeductible && !prepayment.getValue());
			vatQuotaCell.setVisible(!undeductible);
			vatQuota.setVisible(!undeductible && !prepayment.getValue());
		}

		private void enableInputVat(boolean enabled) {
			inputVatLabelCell.setVisible(!isUndeductible() && enabled);
			inputVatAccount.setVisible(!isUndeductible() && enabled);
			inputVatAccount.setVisible(!isUndeductible() && !prepayment.getValue() && enabled);
		}
		
		private void enableOutputVat(boolean enabled) {
			outputVatLabelCell.setVisible(!isUndeductible() && enabled);
			outputVatAccountCell.setVisible(!isUndeductible() && enabled);
			outputVatAccount.setVisible(!isUndeductible() && !prepayment.getValue() && enabled);
		}

		private void enableSurcharge(boolean enabled) {
			reLabelCell.setVisible(!isUndeductible() && enabled);
			surchargePercentCell.setVisible(!isUndeductible() && enabled);
			surchargePercent.setVisible(!isUndeductible() && !prepayment.getValue() && enabled);
			reQuotaLabelCell.setVisible(!isUndeductible() && enabled);
			surchargeQuotaCell.setVisible(!isUndeductible() && enabled);
			surchargeQuota.setVisible(!isUndeductible() && !prepayment.getValue() && enabled);
		}
		
		private void enableWithholding(boolean enabled) {
			withholdingLabelCell.setVisible(!isUndeductible() && enabled);
			withholdingCell.setVisible(!isUndeductible() && enabled);
			withholding.setVisible(!isUndeductible() && !prepayment.getValue() && enabled);
		}
		
		private void enableInvestAsset(InvoicePanelRow row) {
			final boolean otherLineWithInvestAssests = callback.isInvestAssetsAvailable() && isOtherLineWithInvestAssests(row);
			
			investAssetLabelCell.setVisible(!isUndeductible() && callback.isInvestAssetsAvailable());
			dedPercentLabelCell.setVisible(!isUndeductible() && otherLineWithInvestAssests);
			dedQuotaLabelCell.setVisible(!isUndeductible() && otherLineWithInvestAssests);
			adjAccountLabelCell.setVisible(!isUndeductible() && otherLineWithInvestAssests);
			
			investAssetCell.setVisible(!isUndeductible() && !prepayment.getValue() && callback.isInvestAssetsAvailable());
			dedPercentCell.setVisible(!isUndeductible() && !prepayment.getValue() && callback.isInvestAssetsAvailable() && investAsset.getValue() != null);
			dedQuotaCell.setVisible(!isUndeductible() && !prepayment.getValue() && callback.isInvestAssetsAvailable() && investAsset.getValue() != null);
			adjAccountCell.setVisible(!isUndeductible() && !prepayment.getValue() && callback.isInvestAssetsAvailable() && investAsset.getValue() != null);
		}
		
		private void enablePrepayment(boolean enabled) {
			prepaymentCell.setVisible(!isUndeductible() && enabled);
			prepaymentLabelCell.setVisible(!isUndeductible() && enabled);
		}
		
		private void checkCalculate(final InvoiceVAT vat, AonDoubleBox toFocus) {
			if (vat.isAnyquotaEdited()) {
				AonConfirmDialog cd = new AonConfirmDialog();
				cd.confirm(AON.MSG.manualChangeConfirm(),new AonConfirmDialogCallback() {
					
					@Override
					public void onCancel() {
						if (toFocus != null) {
							toFocus.selectAll();
							toFocus.setFocus(true);
						}
						ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
					}
					
					@Override
					public void onAccept() {
						vat.setQuotaEdited(false);
						vat.setSurchargeQuotaEdited(false);
						vat.setDeductibleQuotaEdited(false);
						calculate(vat);
						decorateVatQuota(vat);
						decorateSurchargeQuota(vat);
						decorateDeductibleQuota(vat);
						if (toFocus != null) {
							toFocus.selectAll();
							toFocus.setFocus(true);
						}
						ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
					}
				});
			} else {
				calculate(vat);
				ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
			}
		}
		protected void calculate(InvoiceVAT vat) {
			InvoiceCalculator.calculate(callback.getInvoice(),vat);
			populate(vat);
		}

		private void populate(InvoiceVAT vat) {
			taxableBase.setValue( vat.getBase() , false);
			vatPercent.setValue( vat.getPercentage() , false);
			vatQuota.setValue( vat.getQuota() , false);
			surchargePercent.setValue( vat.getSurcharge() , false);
			surchargeQuota.setValue( vat.getSurchargeQuota() , false);
			dedPercent.setValue( vat.getDeductiblePercent() , false);
			dedQuota.setValue( vat.getDeductibleQuota() , false);
			withholding.setValue(vat.isWithholding() , false);
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
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<InvoiceVAT> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	public void populateFirstVat() {
		if (rows != null &&  rows.size() == 1) {
			rows.get(0).populate( callback.getInvoice().getFirstVat() );
		}
	}
	
	public void headerInfoChanged() {
		for (InvoicePanelRow row :  rows) {
			enableRowElements( row );
		}
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Account> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public void enableElements(boolean canRemove, boolean canEdit) {
		if (addButton != null) addButton.setVisible(canEdit);
	}

	@Override
	public int getTabIndex() {
		if (rows != null &&  rows.size() > 0) return rows.get(0).getTabIndex();
		return 0;
	}

	@Override
	public void setAccessKey(char key) {
		if (rows != null &&  rows.size() > 0) rows.get(0).setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		if (rows != null &&  rows.size() > 0) rows.get(0).setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		if (rows != null &&  rows.size() > 0) rows.get(0).setTabIndex(index);
	}

	public void hideButtons(boolean enabled) {
		addButton.setVisible(enabled);
	}
	public void withholdingChanged(Boolean value) {
		for (InvoicePanelRow vat : rows) {
			vat.withholding.setValue(value && !vat.prepayment.getValue(),false);
		}
	}
	public void prepaymentChanged(Boolean value) {
		for (InvoicePanelRow vat : rows) {
			vat.prepayment.setValue(value,false);
		}
	}
	private boolean isVatEnabledForCell(IEditableInvoicePanelCallback callback, InvoiceVAT vat) {
		return  !callback.getInvoice().isUndeductible();	
	}
	private boolean isVatEnabledForData(IEditableInvoicePanelCallback callback, InvoiceVAT vat) {
		return  isVatEnabledForCell(callback, vat) && !vat.isPrepayment();	
	}
	private boolean isSurchargeEnabled(IEditableInvoicePanelCallback callback, InvoiceVAT vat) {
		return isVatEnabledForData(callback,vat) && callback.getInvoice().isSurcharge();
	}
	private boolean isInputVatEnabledForCell(IEditableInvoicePanelCallback callback, InvoiceVAT vat) {
		return isVatEnabledForCell(callback,vat) && callback.getInvoice().isInputVatEnabled(); 	
	}
	private boolean isInputVatEnabledForData(IEditableInvoicePanelCallback callback, InvoiceVAT vat) {
		return isVatEnabledForData(callback,vat) && callback.getInvoice().isInputVatEnabled(); 	
	}
	private boolean isOutputVatEnabledForCell(IEditableInvoicePanelCallback callback, InvoiceVAT vat) {
		return isVatEnabledForCell(callback,vat) & callback.getInvoice().isOutputVatEnabled(); 	
	}
	private boolean isOutputVatEnabledForData(IEditableInvoicePanelCallback callback, InvoiceVAT vat) {
		return isVatEnabledForData(callback,vat) && callback.getInvoice().isOutputVatEnabled(); 	
	}

	// **** TODO *** //
	private void enableRowElements(InvoicePanelRow row) {
		row.enablePrepayment(callback.getInvoice().hasPrepayments());
		row.enableWithholding(callback.getInvoice().isWithholding());
		row.enableInputVat(callback.getInvoice().isInputVatEnabled());
		row.enableOutputVat(callback.getInvoice().isOutputVatEnabled());
		row.enableSurcharge(callback.getInvoice().isSurcharge());
		row.enableVat(callback.getInvoice().isUndeductible());
		row.enableInvestAsset(row);
	}
	private boolean isInvestAssetsEnabled(IEditableInvoicePanelCallback callback, InvoiceVAT vat) {
		return callback.isInvestAssetsAvailable() && !vat.isPrepayment();
	}
}

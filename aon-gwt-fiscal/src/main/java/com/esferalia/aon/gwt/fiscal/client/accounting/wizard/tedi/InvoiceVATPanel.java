package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.EditableInvoicePanel.IEditableInvoicePanelCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class InvoiceVATPanel extends FlowPanel implements HasValueChangeHandlers<InvoiceVAT>, HasSelectionHandlers<Account>, Focusable {

	private static final String WIDTH_100PX = "100px";
	static final String INVEST_ASSET_DEF_BACKGROUND_COLOR = "inherit";
	static final String INVEST_ASSET_BACKGROUND_COLOR = "#9ACD32";

	enum InvoiceVATGridColumns {
		EXP_ACCOUNT {
			@Override
			String width() {
				return WIDTH_100PX;
			}

			@Override
			Widget getLabel() {
				return new Label(AON.MSG.accountAbr());
			}

			@Override
			boolean isHeaderEnabled(IEditableInvoicePanelCallback callback) {
				return true;
			}

			@Override
			boolean isRowCellEnabled(IEditableInvoicePanelCallback callback, final int vatIdx) {
				return true;
			}
			
			@Override
			boolean isEnabled(IEditableInvoicePanelCallback callback, int vatIdx) {
				return true;
			}
		},
		TAXABALE_BASE {
			@Override
			String width() {
				return WIDTH_100PX;
			}

			@Override
			Widget getLabel() {
				return new Label(AON.MSG.taxableBaseAbr());
			}

			@Override
			boolean isHeaderEnabled(IEditableInvoicePanelCallback callback) {
				return true;
			}

			@Override
			boolean isRowCellEnabled(IEditableInvoicePanelCallback callback, final int vatIdx) {
				return true;
			}

			@Override
			boolean isEnabled(IEditableInvoicePanelCallback callback, int vatIdx) {
				return isAccountingSource( callback, vatIdx ); 
			}
		},
		VAT_PERCENT {
			@Override
			String width() {
				return "80px";
			}

			@Override
			Widget getLabel() {
				return new Label("% IVA");
			}

			@Override
			boolean isHeaderEnabled(IEditableInvoicePanelCallback callback) {
				return isVATEnabled(callback);
			}

			@Override
			boolean isRowCellEnabled(IEditableInvoicePanelCallback callback, final int vatIdx) {
				return isVATEnabled(callback, vatIdx);
			}

			@Override
			boolean isEnabled(IEditableInvoicePanelCallback callback, int vatIdx) {
				return isAccountingSource( callback, vatIdx ); 
			}
		},
		VAT_QUOTA {
			@Override
			String width() {
				return WIDTH_100PX;
			}

			@Override
			Widget getLabel() {
				return new Label(AON.MSG.vatQuota());
			}

			@Override
			boolean isHeaderEnabled(IEditableInvoicePanelCallback callback) {
				return isVATEnabled(callback);
			}

			@Override
			boolean isRowCellEnabled(IEditableInvoicePanelCallback callback, final int vatIdx) {
				return isVATEnabled(callback, vatIdx);
			}

			@Override
			boolean isEnabled(IEditableInvoicePanelCallback callback, int vatIdx) {
				return isAccountingSource( callback, vatIdx );
			}
		},
		SURCHARGE_PERCENT {
			@Override
			String width() {
				return "80px";
			}

			@Override
			Widget getLabel() {
				return new Label("% RE");
			}

			@Override
			boolean isHeaderEnabled(IEditableInvoicePanelCallback callback) {
				return isSurchargeEnabled(callback);
			}

			@Override
			boolean isRowCellEnabled(IEditableInvoicePanelCallback callback, final int vatIdx) {
				return isSurchargeEnabled(callback, vatIdx);
			}

			@Override
			boolean isEnabled(IEditableInvoicePanelCallback callback, int vatIdx) {
				return isAccountingSource( callback, vatIdx );
			}
		},
		SURCHARGE_QUOTA {
			@Override
			String width() {
				return WIDTH_100PX;
			}

			@Override
			Widget getLabel() {
				return new Label(AON.MSG.surchargeQuota());
			}

			@Override
			boolean isHeaderEnabled(IEditableInvoicePanelCallback callback) {
				return isSurchargeEnabled(callback);
			}

			@Override
			boolean isRowCellEnabled(IEditableInvoicePanelCallback callback, final int vatIdx) {
				return isSurchargeEnabled(callback, vatIdx);
			}

			@Override
			boolean isEnabled(IEditableInvoicePanelCallback callback, int vatIdx) {
				return isAccountingSource( callback, vatIdx );
			}
		},
		INVEST_ASSET {
			@Override
			String width() {
				return WIDTH_100PX;
			}

			@Override
			Widget getLabel() {
				return new Label(AON.MSG.actInvestAsset());
			}

			@Override
			boolean isHeaderEnabled(IEditableInvoicePanelCallback callback) {
				return isInvestAssetsEnabled(callback);
			}

			@Override
			boolean isRowCellEnabled(IEditableInvoicePanelCallback callback, final int vatIdx) {
				return isInvestAssetsEnabled(callback, vatIdx);
			}

			@Override
			boolean isAdditionalDataEnabled(IEditableInvoicePanelCallback callback, final int vatIdx) {
				return isInvestAssetsEnabled(callback, vatIdx) && callback.getVat(vatIdx).getInvestAsset() != null;
			}

			@Override
			boolean isEnabled(IEditableInvoicePanelCallback callback, int vatIdx) {
				return true;
			}
		},
		INPUT_VAT_ACCOUNT {
			@Override
			String width() {
				return WIDTH_100PX;
			}

			@Override
			Widget getLabel() {
				return new Label(AON.MSG.inputVatAccount());
			}

			@Override
			boolean isHeaderEnabled(IEditableInvoicePanelCallback callback) {
				return isInputVATEnabled(callback);
			}

			@Override
			boolean isRowCellEnabled(IEditableInvoicePanelCallback callback, final int vatIdx) {
				return isInputVATEnabled(callback, vatIdx);
			}

			@Override
			boolean isEnabled(IEditableInvoicePanelCallback callback, int vatIdx) {
				return true;
			}
		},
		OUTPUT_VAT_ACCOUNT {
			@Override
			String width() {
				return WIDTH_100PX;
			}

			@Override
			Widget getLabel() {
				return new Label(AON.MSG.outputVatAccount());
			}

			@Override
			boolean isHeaderEnabled(IEditableInvoicePanelCallback callback) {
				return isOutputVATEnabled(callback);
			}

			@Override
			boolean isRowCellEnabled(IEditableInvoicePanelCallback callback, final int vatIdx) {
				return isOutputVATEnabled(callback, vatIdx);
			}

			@Override
			boolean isEnabled(IEditableInvoicePanelCallback callback, int vatIdx) {
				return true;
			}
		},
		WITHHOLDING {
			@Override
			String width() {
				return "60px";
			}

			@Override
			Widget getLabel() {
				return new Label("IRPF");
			}

			@Override
			boolean isHeaderEnabled(IEditableInvoicePanelCallback callback) {
				return callback.getInvoice().isWithholding();
			}

			@Override
			boolean isRowCellEnabled(IEditableInvoicePanelCallback callback, final int vatIdx) {
				return callback.getInvoice().isWithholding() && !callback.getVat(vatIdx).isPrepayment();
			}

			@Override
			boolean isEnabled(IEditableInvoicePanelCallback callback, int vatIdx) {
				return true;
			}
		},
		PREPAYMENT {
			@Override
			String width() {
				return "60px";
			}

			@Override
			Widget getLabel() {
				return new Label("Supl.");
			}

			@Override
			boolean isHeaderEnabled(IEditableInvoicePanelCallback callback) {
				return callback.getInvoice().hasPrepayments();
			}

			@Override
			boolean isRowCellEnabled(IEditableInvoicePanelCallback callback, final int vatIdx) {
				return callback.getInvoice().hasPrepayments();
			}

			@Override
			boolean isEnabled(IEditableInvoicePanelCallback callback, int vatIdx) {
				return true;
			}
		},
		EMPTY {
			@Override
			String width() {
				return "auto";
			}

			@Override
			Widget getLabel() {
				return new Label("");
			}

			@Override
			boolean isHeaderEnabled(IEditableInvoicePanelCallback callback) {
				return true;
			}

			@Override
			boolean isRowCellEnabled(IEditableInvoicePanelCallback callback, final int vatIdx) {
				return true;
			}

			@Override
			boolean isEnabled(IEditableInvoicePanelCallback callback, int vatIdx) {
				return true;
			}
		};

		abstract String width();
		abstract Widget getLabel();
		abstract boolean isHeaderEnabled(IEditableInvoicePanelCallback callback);
		abstract boolean isRowCellEnabled(IEditableInvoicePanelCallback callback, final int vatIdx);
		abstract boolean isEnabled(IEditableInvoicePanelCallback callback, final int vatIdx);

		boolean isAdditionalDataEnabled(IEditableInvoicePanelCallback callback, final int vatIdx) {
			return callback.isInvestAssetsAvailable() && callback.getVat(vatIdx).getInvestAsset() != null;			
		}

		boolean isVATEnabled(IEditableInvoicePanelCallback callback) {
			return !callback.getInvoice().isUndeductible();
		}

		boolean isVATEnabled(IEditableInvoicePanelCallback callback, final int vatIdx) {
			return isVATEnabled(callback) && !callback.getVat(vatIdx).isPrepayment();
		}

		boolean isSurchargeEnabled(IEditableInvoicePanelCallback callback) {
			return callback.getInvoice().isSurcharge();
		}

		boolean isSurchargeEnabled(IEditableInvoicePanelCallback callback, final int vatIdx) {
			return isSurchargeEnabled(callback) && isVATEnabled(callback, vatIdx);
		}

		boolean isAccountingSource(IEditableInvoicePanelCallback callback, final int vatIdx) {
			return callback.getVat(vatIdx).isAccountingSource();
		}

		boolean isInputVATEnabled(IEditableInvoicePanelCallback callback) {
			return isVATEnabled(callback) && callback.getInvoice().isInputVatEnabled();
		}

		boolean isInputVATEnabled(IEditableInvoicePanelCallback callback, final int vatIdx) {
			return isInputVATEnabled(callback) && isVATEnabled(callback, vatIdx);
		}

		boolean isOutputVATEnabled(IEditableInvoicePanelCallback callback) {
			return isVATEnabled(callback) && callback.getInvoice().isOutputVatEnabled();
		}

		boolean isOutputVATEnabled(IEditableInvoicePanelCallback callback, final int vatIdx) {
			return isOutputVATEnabled(callback) && isVATEnabled(callback, vatIdx);
		}

		boolean isInvestAssetsEnabled(IEditableInvoicePanelCallback callback) {
			return callback.isInvestAssetsAvailable();
		}

		boolean isInvestAssetsEnabled(IEditableInvoicePanelCallback callback, final int vatIdx) {
			return isInvestAssetsEnabled(callback) && !callback.getVat(vatIdx).isPrepayment();
		}
	}

	private final FlexTable grid = new FlexTable();
	private final FlowPanel buttonsRow = new FlowPanel();
	private AonTableButton addButton;
	private LinkedList<InvoiceVATPanelRow> rows;

	public InvoiceVATPanel(IEditableInvoicePanelCallback callback) {
		grid.setStyleName(AON.CSS.aonDisplayGrid());
		grid.addStyleName(AON.CSS.aonWidthAlmostAll());
		grid.addStyleName(AON.CSS.aonMarginTopSep());
		grid.addStyleName(AON.CSS.aonBlockCenter());
		grid.getElement().getStyle().setBackgroundColor(EditableInvoicePanel.INNER_BACKGROUND_COLOR);
		add(grid);

		buttonsRow.setStyleName(AON.CSS.aonDisplayGridFooterRow());
		add(buttonsRow);

		paint(callback);
	}

	FlexTable getGrid() {
		return grid;
	}

	void paint(IEditableInvoicePanelCallback callback) {
		getGrid().clear();
		getGrid().removeAllRows();

		// ------------- HEADER
		int idx = 0;
		for (InvoiceVATGridColumns column : InvoiceVATGridColumns.values()) {
			if (column.isHeaderEnabled(callback)) {
				grid.getColumnFormatter().setWidth(idx, column.width());
				grid.getCellFormatter().setStyleName(0, idx, AON.CSS.aonDisplayGridHeaderCell());
				grid.getCellFormatter().setStyleName(0, idx, AON.CSS.aonNowrap());
				grid.setWidget(0, idx, column.getLabel());
				idx++;
			}
		}
		// ------------- VAT ROWS
		rows = new LinkedList<>();
		for ( int vatIdx = 0; vatIdx < callback.getInvoice().getVats().size(); vatIdx++) {
			addRow(callback, vatIdx, false);
		}

		// ------------- ADD BUTTON
		addButtonsRow(callback);

	}

	private void addButtonsRow(IEditableInvoicePanelCallback callback) {
		buttonsRow.clear();
		addButton = new AonTableButton(AON.MSG.newAction(), AON.CSS.aonIconAdd());
		addButton.setAccessKey('L');
		addButton.addClickHandler(event -> {
			int lastIdx = callback.getInvoice().getVats().size() - 1;
			final InvoiceVAT last = callback.getVat(lastIdx);
			final InvoiceVAT vat = new InvoiceVAT().setExpAccountId(last.getExpAccountId())
					.setExpAccountCode(last.getExpAccountCode())
					.setExpAccountDescription(last.getExpAccountDescription()).setPercentage(last.getPercentage())
					.setSurcharge(last.getSurcharge()).setWithholding(last.isWithholding())
					.setInputAccountId(last.getInputAccountId()).setInputAccountCode(last.getInputAccountCode())
					.setInputAccountDescription(last.getInputAccountDescription())
					.setOutputAccountId(last.getOutputAccountId()).setOutputAccountCode(last.getOutputAccountCode())
					.setOutputAccountDescription(last.getOutputAccountDescription())
					.setAdjAccountId(last.getAdjAccountId()).setAdjAccountCode(last.getAdjAccountCode())
					.setAdjAccountDescription(last.getAdjAccountDescription())
					.setDirectTaxPercent(last.getDirectTaxPercent())
					.setAdjDirectTaxAccountId(last.getAdjDirectTaxAccountId())
					.setAdjDirectTaxAccountCode(last.getAdjDirectTaxAccountCode())
					.setAdjDirectTaxAccountDescription(last.getAdjDirectTaxAccountDescription());
			callback.getInvoice().addVat(vat);
			int insertedIdx = callback.getInvoice().getVats().size() - 1;
			addRow(callback, insertedIdx, true);
			addButtonsRow(callback);
		});
		buttonsRow.add(addButton);
	}

	private void addRow(IEditableInvoicePanelCallback callback, int vatIdx, boolean focus) {
		LinkedList<Account> suggestedAccounts = callback.getInvoice().getSuggestedAccounts();
		if (suggestedAccounts != null && !suggestedAccounts.isEmpty() && suggestedAccounts.size() > rows.size()) {
			Account a = suggestedAccounts.get(rows.size());
			callback.getInvoice().getVats().get(vatIdx).setExpAccountId(a.getId());
			callback.getInvoice().getVats().get(vatIdx).setExpAccountCode(a.getCode());
			callback.getInvoice().getVats().get(vatIdx).setExpAccountDescription(a.getDescription());
		}
		InvoiceVATPanelRow invoiceRow = new InvoiceVATPanelRow(this, grid.getRowCount(), callback, vatIdx, focus);
		invoiceRow.addSelectionHandler(e -> SelectionEvent.fire(this, e.getSelectedItem()));
		invoiceRow.addValueChangeHandler(e -> ValueChangeEvent.fire(this, e.getValue()));
		rows.add(invoiceRow);
		if (rows.size() > 1) {
			callback.enableInvoiceTotal(false);
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<InvoiceVAT> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	void populateFirstVat(IEditableInvoicePanelCallback callback) {
		if (rows != null && rows.size() == 1) {
			rows.get(0).populate(callback, 0);
		}
	}

	public void headerInfoChanged(IEditableInvoicePanelCallback callback) {
		paint(callback);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Account> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public void enableElements(boolean canRemove, boolean canEdit) {
		if (addButton != null)
			addButton.setVisible(canEdit);
	}

	@Override
	public int getTabIndex() {
		if (rows != null && !rows.isEmpty())
			return rows.get(0).getTabIndex();
		return 0;
	}

	@Override
	public void setAccessKey(char key) {
		if (rows != null && !rows.isEmpty())
			rows.get(0).setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		if (rows != null && !rows.isEmpty())
			rows.get(0).setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		if (rows != null && !rows.isEmpty())
			rows.get(0).setTabIndex(index);
	}

	public void hideButtons(boolean enabled) {
		addButton.setVisible(enabled);
	}

	public void withholdingChanged(Boolean value) {
		AonCollectionUtils.stream(rows).forEach(row -> row.withholdingChanged(value));
	}

	public void prepaymentChanged(Boolean value) {
		AonCollectionUtils.stream(rows).forEach(row -> row.prepaymentChanged(value));
	}
	
	boolean isAnyInvestAssetsEnabled(IEditableInvoicePanelCallback callback) {
		for ( int vatIdx = 0; vatIdx < callback.getInvoice().getVats().size(); vatIdx++) {
			if ( InvoiceVATGridColumns.INVEST_ASSET.isAdditionalDataEnabled(callback, vatIdx)) {
				return true;
			}
		}
		return false;
	}

}

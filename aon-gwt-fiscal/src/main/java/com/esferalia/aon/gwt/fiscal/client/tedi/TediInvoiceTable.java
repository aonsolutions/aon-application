package com.esferalia.aon.gwt.fiscal.client.tedi;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.occam.api.model.tedi.TediLevel;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;

public class TediInvoiceTable extends CellTable<TediResult> {
	private static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);

	private NoSelectionModel<TediResult> model;

	public TediInvoiceTable(ProvidesKey<TediResult> providesKey) {
		super(1, TABLE_STYLE, providesKey);
		this.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		addStatusColumn();
		addInvoiceTypeColumn();
		addAttachColumn();
		addIssueDateColumn();
		addTotalColumn();
		addNumberColumn();
		addDocumentColumn();
		addNameColumn();
		model = new NoSelectionModel<TediResult>(providesKey);
		this.setSelectionModel(model);
		this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
	}

	public void addSelectionChangeHandler(SelectionChangeEvent.Handler handler) {
		model.addSelectionChangeHandler(handler);
	}

	private void addStatusColumn() {
		final Column<TediResult, ImageResource> statusColumn = new Column<TediResult, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(TediResult result) {
				TediLevel curLevel = result.getMoreSeriousLevel();
				if (curLevel == TediLevel.INF) {
					return AON.AON_RESOURCES.aonIconPointLightGreen();
				} else if (curLevel == TediLevel.WRN) {
					return AON.AON_RESOURCES.aonIconPointOrange();
				} else if (curLevel == TediLevel.ERR) {
					return AON.AON_RESOURCES.aonIconPointRed();
				}
				return AON.AON_RESOURCES.aonIconPointGreen();
			}
		};
		this.addColumn(statusColumn);
		this.setColumnWidth(statusColumn, 20, Unit.PX);
	}
	
	private void addInvoiceTypeColumn() {
		final TextColumn<TediResult> invoiceTypeColumn = new TextColumn<TediResult>() {
			@Override
			public String getValue(TediResult result) {
				return result.getInvoice().getType().getDescription();
			}
		};
		this.addColumn(invoiceTypeColumn, AON.MSG.type());
		this.setColumnWidth(invoiceTypeColumn, 80, Unit.PX);
	}

	private void addAttachColumn() {
		final Column<TediResult, ImageResource> attachColumn = new Column<TediResult, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(TediResult result) {
				return result.hasAttach() ? AON.AON_RESOURCES.aonIconAttach() : null;
			}
		};
		this.addColumn(attachColumn);
		this.setColumnWidth(attachColumn, 20, Unit.PX);
	}

	private void addIssueDateColumn() {
		final TextColumn<TediResult> issueDateColumn = new TextColumn<TediResult>() {
			@Override
			public String getValue(TediResult result) {
				return result.getInvoice().getIssueDate() != null
						? AON.DATE_FORMAT.format(result.getInvoice().getIssueDate())
						: "";
			}
		};
		this.addColumn(issueDateColumn, AON.MSG.date());
		issueDateColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
		this.setColumnWidth(issueDateColumn, 50, Unit.PX);
	}

	private void addTotalColumn() {
		final TextColumn<TediResult> amountColumn = new TextColumn<TediResult>() {
			@Override
			public String getValue(TediResult result) {
				return AON.FMT.format(result.getInvoice().getTotal());
			}
		};
		this.addColumn(amountColumn, AON.MSG.total());
		amountColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
		this.setColumnWidth(amountColumn, 100, Unit.PX);
	}

	private void addNumberColumn() {
		final TextColumn<TediResult> nameColumn = new TextColumn<TediResult>() {
			@Override
			public String getValue(TediResult result) {
				return result.getInvoice().isSales()
						? AonStringUtils.appendIfMissing(result.getInvoice().getSeries(), "/")
								+ result.getInvoice().getNumber()
						: result.getInvoice().getReferenceCode();
			}
		};
		this.addColumn(nameColumn, AON.MSG.invoiceNumber());
		this.setColumnWidth(nameColumn, 150, Unit.PX);
	}

	private void addDocumentColumn() {
		final TextColumn<TediResult> documentColumn = new TextColumn<TediResult>() {
			@Override
			public String getValue(TediResult result) {
				return result.getTedi().getRdocument();
			}
		};
		this.addColumn(documentColumn, AON.MSG.document());
		this.setColumnWidth(documentColumn, 100, Unit.PX);
	}

	private void addNameColumn() {
		final TextColumn<TediResult> nameColumn = new TextColumn<TediResult>() {
			@Override
			public String getValue(TediResult result) {
				return result.getTedi().getRname();
			}
		};
		this.addColumn(nameColumn, AON.MSG.name());
		this.setColumnWidth(nameColumn, "auto");
	}

	public TediResult getSelected() {
		return model.getLastSelectedObject();
	}
}

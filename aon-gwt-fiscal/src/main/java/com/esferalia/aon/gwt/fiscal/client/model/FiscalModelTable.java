package com.esferalia.aon.gwt.fiscal.client.model;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
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

public class FiscalModelTable<FM extends FiscalModel> extends CellTable<FM> {
	private static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);
	

	private NoSelectionModel<FM> model;
	
	public FiscalModelTable(ProvidesKey<FM> providesKey) {
		super(1,TABLE_STYLE, providesKey);
		this.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		addSelectorColumn();
		addYearColumn();
		addPeriodColumn();
		addAdministrationColumn();
		addModelColumn();
		addStatusColumn();
		addReplacementColumn();
		addComplementaryColumn();
		addDocumentColumn();
		addNameColumn();
		addAmountColumn();
		addFinanceStatusColumn();
		
		model = new NoSelectionModel<FM>(providesKey);
		this.setSelectionModel(model);
		this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
	}

	public FiscalModelTable(SelectionChangeEvent.Handler handler, ProvidesKey<FM> providesKey) {
		this(providesKey);
		model.addSelectionChangeHandler( handler );
	}

	private void addSelectorColumn() {
		final Column<FM, ImageResource> selectorColumn = new Column<FM, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(FM model) {
				return AON.AON_RESOURCES.aonIconRowSelector();
			}
		};
		this.addColumn(selectorColumn);
		this.setColumnWidth(selectorColumn, 20, Unit.PX);
	}

	private void addYearColumn() {
		final TextColumn<FM> yearColumn = new TextColumn<FM>() {
			@Override
			public String getValue(FM model) {
				return Integer.toString(model.getYear());
			}
		};
		this.addColumn(yearColumn, AON.MSG.fiscalYear());
		yearColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
		this.setColumnWidth(yearColumn, 50, Unit.PX);
	}

	private void addPeriodColumn() {
		final TextColumn<FM> documentColumn = new TextColumn<FM>() {
			@Override
			public String getValue(FM model) {
				return model.getPeriod().getDescription();
			}
		};
		this.addColumn(documentColumn, AON.MSG.period());
		this.setColumnWidth(documentColumn, 50, Unit.PX);
	}

	private void addAdministrationColumn() {
		final Column<FM, ImageResource> iconColumn = new Column<FM, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(FM model) {
				return FiscalModelUtils.getAdministrationIconResource(model.getAdministration());
			}
		};
		this.addColumn(iconColumn, "A" );
		this.setColumnWidth(iconColumn, 20, Unit.PX);
	}
	
	private void addModelColumn() {
		final TextColumn<FM> modelColumn = new TextColumn<FM>() {
			@Override
			public String getValue(FM model) {
				return FiscalModelUtils.getModelName(model);
			}
		};
		this.addColumn(modelColumn, AON.MSG.model());
		modelColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
		this.setColumnWidth(modelColumn, 50, Unit.PX);
	}

	private void addStatusColumn() {
		final Column<FM, ImageResource> iconColumn = new Column<FM, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(FM model) {
				return model.isFinished()
						?AON.AON_RESOURCES.aonIconLock()
						:AON.AON_RESOURCES.aonIconUnlock();
			}
		};
		this.addColumn(iconColumn, "E" );
		this.setColumnWidth(iconColumn, 20, Unit.PX);
	}
	
	private void addReplacementColumn() {
		Column<FM, ImageResource> replacementColumn = new Column<FM, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(FM model) {
				return model.isReplacement() ? AON.AON_RESOURCES.aonIconChecked()
						: AON.AON_RESOURCES.aonIconCheck();
			}
		};
		this.addColumn(replacementColumn, "S" );
		replacementColumn.setCellStyleNames(AON.AON_CSS.aonDataTableIconColumn());
		this.setColumnWidth(replacementColumn, 20, Unit.PX);
	}

	private void addComplementaryColumn() {
		Column<FM, ImageResource> complementaryColumn = new Column<FM, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(FM model) {
				return model.isComplementary() 
					? AON.AON_RESOURCES.aonIconChecked()
					: AON.AON_RESOURCES.aonIconCheck();
			}
		};
		this.addColumn(complementaryColumn, "C" );
		complementaryColumn.setCellStyleNames(AON.AON_CSS.aonDataTableIconColumn());
		this.setColumnWidth(complementaryColumn, 20, Unit.PX);
	}
	
	private void addDocumentColumn() {
		final TextColumn<FM> documentColumn = new TextColumn<FM>() {
			@Override
			public String getValue(FM model) {
				return model.getDocument();
			}
		};
		this.addColumn(documentColumn, AON.MSG.document());
		this.setColumnWidth(documentColumn, 100, Unit.PX);
	}

	private void addNameColumn() {
		final TextColumn<FM> nameColumn = new TextColumn<FM>() {
			@Override
			public String getValue(FM model) {
				return model.getFullName();
			}
		};
		this.addColumn(nameColumn, AON.MSG.name());
		this.setColumnWidth(nameColumn, "auto");
	}	

	private void addAmountColumn() {
		final TextColumn<FM> amountColumn = new TextColumn<FM>() {
			@Override
			public String getValue(FM model) {
				return AON.FMT.format(model.getResult()) ;
			}
		};
		this.addColumn(amountColumn, AON.MSG.result());
		amountColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
		this.setColumnWidth(amountColumn, 100, Unit.PX);
	}	

	private void addFinanceStatusColumn() {
		final TextColumn<FM> financeStatusColumn = new TextColumn<FM>() {
			@Override
			public String getValue(FM model) {
				if (model.getFinance() != null && model.getFinance().getFinanceStatus() != null) {
					return model.getFinance().getFinanceStatus().getDescription();
				}
				return AonStringUtils.EMPTY;
			}
		};
		this.addColumn(financeStatusColumn, AON.MSG.financeStatus());
		financeStatusColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
		this.setColumnWidth(financeStatusColumn, 150, Unit.PX);
	}	

	public FM getSelected() {
		return model.getLastSelectedObject();
	}
}

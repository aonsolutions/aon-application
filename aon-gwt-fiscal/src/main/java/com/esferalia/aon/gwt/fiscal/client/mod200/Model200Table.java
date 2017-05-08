package com.esferalia.aon.gwt.fiscal.client.mod200;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.ProvidesKey;

public class Model200Table extends CellTable<Mod200> {
	
	public static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);
	

	public Model200Table(ProvidesKey<Mod200> providesKey) {
		super(1,TABLE_STYLE, providesKey);
		this.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		addSelectorColumn();
		addAdministrationColumn();
		addYearColumn();
		addModelColumn();
		addStatusColumn();
		addComplementaryColumn();
		addDocumentColumn();
		addNameColumn();
		addResultTypeColumn();
		addResultColumn();
		addFinanceStatusColumn();
		this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
	}

	private void addSelectorColumn() {
		final Column<Mod200, ImageResource> selectorColumn = new Column<Mod200, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod200 model) {
				return AON.AON_RESOURCES.aonIconRowSelector();
			}
		};
		this.addColumn(selectorColumn);
		this.setColumnWidth(selectorColumn, 20, Unit.PX);
	}

	private void addAdministrationColumn() {
		final Column<Mod200, ImageResource> iconColumn = new Column<Mod200, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod200 model) {
				return FiscalModelUtils.getAdministrationIconResource(model.getAdministration());
			}
		};
		this.addColumn(iconColumn, "A" );
		this.setColumnWidth(iconColumn, 20, Unit.PX);
	}
	
	private void addYearColumn() {
		final TextColumn<Mod200> yearColumn = new TextColumn<Mod200>() {
			@Override
			public String getValue(Mod200 model) {
				return Integer.toString(model.getYear());
			}
		};
		this.addColumn(yearColumn, AON.MSG.fiscalYear());
		yearColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
		this.setColumnWidth(yearColumn, 80, Unit.PX);
	}

	private void addModelColumn() {
		final TextColumn<Mod200> modelColumn = new TextColumn<Mod200>() {
			@Override
			public String getValue(Mod200 model) {
				return model.getModel().getName(model.getAdministration(), model.getPeriod());
			}
		};
		this.addColumn(modelColumn, AON.MSG.model());
		modelColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
		this.setColumnWidth(modelColumn, 50, Unit.PX);
	}

	private void addStatusColumn() {
		final Column<Mod200, ImageResource> iconColumn = new Column<Mod200, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod200 model) {
				return model.isFinished()
						?AON.AON_RESOURCES.aonIconLock()
						:AON.AON_RESOURCES.aonIconUnlock();
			}
		};
		this.addColumn(iconColumn, "E" );
		this.setColumnWidth(iconColumn, 20, Unit.PX);
	}
	
	private void addComplementaryColumn() {
		Column<Mod200, ImageResource> complementaryColumn = new Column<Mod200, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod200 model) {
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
		final TextColumn<Mod200> documentColumn = new TextColumn<Mod200>() {
			@Override
			public String getValue(Mod200 model) {
				return model.getDocument();
			}
		};
		this.addColumn(documentColumn, AON.MSG.document());
		this.setColumnWidth(documentColumn, 100, Unit.PX);
	}

	private void addNameColumn() {
		final TextColumn<Mod200> nameColumn = new TextColumn<Mod200>() {
			@Override
			public String getValue(Mod200 model) {
				return model.getFullName();
			}
		};
		this.addColumn(nameColumn, AON.MSG.name());
		this.setColumnWidth(nameColumn, "auto");
	}	

	private void addResultTypeColumn() {
		final TextColumn<Mod200> nameColumn = new TextColumn<Mod200>() {
			@Override
			public String getValue(Mod200 model) {
				if (model.isDeposit()) return AON.MSG.deposit(); 
				else if (model.isPayback()) return AON.MSG.payBack();
				else return AonStringUtils.EMPTY;
			}
		};
		this.addColumn(nameColumn, AON.MSG.result());
		this.setColumnWidth(nameColumn, 100, Unit.PX);
	}	

	private void addResultColumn() {
		final TextColumn<Mod200> amountColumn = new TextColumn<Mod200>() {
			@Override
			public String getValue(Mod200 model) {
				return AON.FMT.format(model.getResult()) ;
			}
		};
		this.addColumn(amountColumn, AON.MSG.result());
		amountColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
		this.setColumnWidth(amountColumn, 100, Unit.PX);
	}	

	private void addFinanceStatusColumn() {
		final TextColumn<Mod200> financeStatusColumn = new TextColumn<Mod200>() {
			@Override
			public String getValue(Mod200 model) {
				if (model.getFinance() != null && model.getFinance().getFinanceStatus() != null) {
					return model.getFinance().getFinanceStatus().getDescription();
				}
				return AonStringUtils.EMPTY;
			}
		};
		this.addColumn(financeStatusColumn, AON.MSG.financeStatus());
		financeStatusColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
		this.setColumnWidth(financeStatusColumn, 120, Unit.PX);
	}	

}
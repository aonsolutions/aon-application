package com.esferalia.aon.gwt.fiscal.client.mod111;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
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

public class Model111Table extends CellTable<Mod111> {

	private static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);
	
	public static final ProvidesKey<Mod111> MOD111_PROVIDES_KEY = new ProvidesKey<Mod111>() {
		@Override
		public Object getKey(Mod111 mod111) {
			return mod111 == null ? null : mod111.getId();
		}
	};

	private NoSelectionModel<Mod111> model;
	
	public Model111Table(SelectionChangeEvent.Handler handler) {
		super(1,TABLE_STYLE,MOD111_PROVIDES_KEY);
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
		
		model = new NoSelectionModel<Mod111>(MOD111_PROVIDES_KEY);
		model.addSelectionChangeHandler( handler );
		this.setSelectionModel(model);
		this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
	}

	private void addSelectorColumn() {
		final Column<Mod111, ImageResource> selectorColumn = new Column<Mod111, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod111 mod111) {
				return AON.AON_RESOURCES.aonIconRowSelector();
			}
		};
		this.addColumn(selectorColumn);
		this.setColumnWidth(selectorColumn, 20, Unit.PX);
	}

	private void addYearColumn() {
		final TextColumn<Mod111> yearColumn = new TextColumn<Mod111>() {
			@Override
			public String getValue(Mod111 mod111) {
				return Integer.toString(mod111.getYear());
			}
		};
		this.addColumn(yearColumn, AON.MSG.fiscalYear());
		yearColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
		this.setColumnWidth(yearColumn, 100, Unit.PX);
	}

	private void addPeriodColumn() {
		final TextColumn<Mod111> documentColumn = new TextColumn<Mod111>() {
			@Override
			public String getValue(Mod111 mod111) {
				return mod111.getPeriod().getDescription();
			}
		};
		this.addColumn(documentColumn, AON.MSG.period());
		this.setColumnWidth(documentColumn, 75, Unit.PX);
	}

	private void addAdministrationColumn() {
		final Column<Mod111, ImageResource> iconColumn = new Column<Mod111, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod111 mod111) {
				return FiscalModelUtils.getAdministrationIconResource(mod111.getAdministration());
			}
		};
		this.addColumn(iconColumn, "A" );
		this.setColumnWidth(iconColumn, 20, Unit.PX);
	}
	
	private void addModelColumn() {
		final TextColumn<Mod111> modelColumn = new TextColumn<Mod111>() {
			@Override
			public String getValue(Mod111 mod111) {
				return mod111.getModel().getName(mod111.getAdministration(), mod111.getPeriod());
			}
		};
		this.addColumn(modelColumn, AON.MSG.model());
		modelColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
		this.setColumnWidth(modelColumn, 50, Unit.PX);
	}

	private void addStatusColumn() {
		final Column<Mod111, ImageResource> iconColumn = new Column<Mod111, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod111 mod111) {
				return mod111.isFinished()
						?AON.AON_RESOURCES.aonIconLock()
						:AON.AON_RESOURCES.aonIconUnlock();
			}
		};
		this.addColumn(iconColumn, "E" );
		this.setColumnWidth(iconColumn, 20, Unit.PX);
	}
	
	private void addReplacementColumn() {
		Column<Mod111, ImageResource> replacementColumn = new Column<Mod111, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod111 mod111) {
				return mod111.isReplacement() ? AON.AON_RESOURCES.aonIconChecked()
						: AON.AON_RESOURCES.aonIconCheck();
			}
		};
		this.addColumn(replacementColumn, "S" );
		replacementColumn.setCellStyleNames(AON.AON_CSS.aonDataTableIconColumn());
		this.setColumnWidth(replacementColumn, 100, Unit.PX);
	}

	private void addComplementaryColumn() {
		Column<Mod111, ImageResource> complementaryColumn = new Column<Mod111, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod111 mod111) {
				return mod111.isComplementary() 
					? AON.AON_RESOURCES.aonIconChecked()
					: AON.AON_RESOURCES.aonIconCheck();
			}
		};
		this.addColumn(complementaryColumn, "C" );
		complementaryColumn.setCellStyleNames(AON.AON_CSS.aonDataTableIconColumn());
		this.setColumnWidth(complementaryColumn, 100, Unit.PX);
	}
	
	private void addDocumentColumn() {
		final TextColumn<Mod111> documentColumn = new TextColumn<Mod111>() {
			@Override
			public String getValue(Mod111 mod111) {
				return mod111.getDocument();
			}
		};
		this.addColumn(documentColumn, AON.MSG.document());
		this.setColumnWidth(documentColumn, 150, Unit.PX);
	}

	private void addNameColumn() {
		final TextColumn<Mod111> nameColumn = new TextColumn<Mod111>() {
			@Override
			public String getValue(Mod111 mod111) {
				return mod111.getFullName();
			}
		};
		this.addColumn(nameColumn, AON.MSG.name());
		this.setColumnWidth(nameColumn, 100, Unit.PCT);
	}	

	public Mod111 getSelected() {
		return model.getLastSelectedObject();
	}
}

package com.esferalia.aon.gwt.fiscal.client.mod190;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.type.Administration;
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

public class OLD_Model190Table extends CellTable<Mod190> {

	private static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);
	
	public static final ProvidesKey<Mod190> MOD190_PROVIDES_KEY = new ProvidesKey<Mod190>() {
		@Override
		public Object getKey(Mod190 mod190) {
			return mod190 == null ? null : mod190.getId();
		}
	};

	private NoSelectionModel<Mod190> model;
	
	public OLD_Model190Table(SelectionChangeEvent.Handler handler) {
		super(1,TABLE_STYLE,MOD190_PROVIDES_KEY);
		this.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		addSelectorColumn();
		addYearColumn();
		addAdministrationColumn();
		addReplacementColumn();
		addDocumentColumn();
		addNameColumn();
		
		model = new NoSelectionModel<Mod190>(MOD190_PROVIDES_KEY);
		model.addSelectionChangeHandler( handler );
		this.setSelectionModel(model);
		this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
	}

	private void addSelectorColumn() {
		final Column<Mod190, ImageResource> selectorColumn = new Column<Mod190, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod190 mod190) {
				return AON.AON_RESOURCES.aonIconRowSelector();
			}
		};
		this.addColumn(selectorColumn);
		this.setColumnWidth(selectorColumn, 20, Unit.PX);
	}

	private void addAdministrationColumn() {
		final Column<Mod190, ImageResource> iconColumn = new Column<Mod190, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod190 mod190) {
				Administration adm = mod190.getAdministration(); 
				if (adm ==Administration.ALAVA) {
					return AON.AON_RESOURCES.aonIconAraba();	
				} else if (adm ==Administration.BIZKAIA) {
					return AON.AON_RESOURCES.aonIconBizkaia();
				} else if (adm ==Administration.GIPUZKOA) {
					return AON.AON_RESOURCES.aonIconGipuzkoa();
				} else if (adm ==Administration.NAVARRA) {
					return AON.AON_RESOURCES.aonIconNavarra();
				} 
				return AON.AON_RESOURCES.aonAeat();
			}
		};
		this.addColumn(iconColumn);
		this.setColumnWidth(iconColumn, 20, Unit.PX);
	}

	private void addYearColumn() {
		final TextColumn<Mod190> yearColumn = new TextColumn<Mod190>() {
			@Override
			public String getValue(Mod190 mod180) {
				return Integer.toString(mod180.getYear());
			}
		};
		this.addColumn(yearColumn, AON.MSG.fiscalYear());
		yearColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
		this.setColumnWidth(yearColumn, 100, Unit.PX);
	}
	
	private void addReplacementColumn() {
		Column<Mod190, ImageResource> replacementColumn = new Column<Mod190, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod190 mod180) {
				return mod180.isReplacement() ? AON.AON_RESOURCES.aonIconChecked()
						: AON.AON_RESOURCES.aonIconCheck();
			}
		};
		this.addColumn(replacementColumn, AON.MSG.replacement());
		replacementColumn.setCellStyleNames(AON.AON_CSS.aonDataTableIconColumn());
		this.setColumnWidth(replacementColumn, 100, Unit.PX);
	}

	private void addNameColumn() {
		final TextColumn<Mod190> nameColumn = new TextColumn<Mod190>() {
			@Override
			public String getValue(Mod190 mod180) {
				return mod180.getName();
			}
		};
		this.addColumn(nameColumn, AON.MSG.name());
		this.setColumnWidth(nameColumn, 100, Unit.PCT);
	}

	private void addDocumentColumn() {
		final TextColumn<Mod190> documentColumn = new TextColumn<Mod190>() {
			@Override
			public String getValue(Mod190 mod180) {
				return mod180.getDocument();
			}
		};
		this.addColumn(documentColumn, AON.MSG.document());
		this.setColumnWidth(documentColumn, 150, Unit.PX);
	}

	public Mod190 getSelected() {
		return model.getLastSelectedObject();
	}
}

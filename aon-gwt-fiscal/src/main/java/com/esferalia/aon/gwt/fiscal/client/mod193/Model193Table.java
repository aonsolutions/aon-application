package com.esferalia.aon.gwt.fiscal.client.mod193;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
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

public class Model193Table extends CellTable<Mod193> {

	private static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);
	
	public static final ProvidesKey<Mod193> MOD193_PROVIDES_KEY = new ProvidesKey<Mod193>() {
		@Override
		public Object getKey(Mod193 mod193) {
			return mod193 == null ? null : mod193.getId();
		}
	};

	private NoSelectionModel<Mod193> model;
	
	public Model193Table(SelectionChangeEvent.Handler handler) {
		super(1,TABLE_STYLE,MOD193_PROVIDES_KEY);
		this.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		addSelectorColumn();
		addYearColumn();
		addAdministrationColumn();
		addReplacementColumn();
		addDocumentColumn();
		addNameColumn();
		
		model = new NoSelectionModel<Mod193>(MOD193_PROVIDES_KEY);
		model.addSelectionChangeHandler( handler );
		this.setSelectionModel(model);
		this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
	}

	private void addSelectorColumn() {
		final Column<Mod193, ImageResource> selectorColumn = new Column<Mod193, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod193 mod193) {
				return AON.AON_RESOURCES.aonIconRowSelector();
			}
		};
		this.addColumn(selectorColumn);
		this.setColumnWidth(selectorColumn, 20, Unit.PX);
	}

	private void addYearColumn() {
		final TextColumn<Mod193> yearColumn = new TextColumn<Mod193>() {
			@Override
			public String getValue(Mod193 mod193) {
				return Integer.toString(mod193.getYear());
			}
		};
		this.addColumn(yearColumn, AON.MSG.fiscalYear());
		yearColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
		this.setColumnWidth(yearColumn, 100, Unit.PX);
	}
	
	private void addAdministrationColumn() {
		final Column<Mod193, ImageResource> iconColumn = new Column<Mod193, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod193 mod193) {
				Administration adm = Administration.values()[mod193.getAdministration()]; 
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
	
	private void addReplacementColumn() {
		Column<Mod193, ImageResource> replacementColumn = new Column<Mod193, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod193 mod193) {
				return mod193.isReplacement() ? AON.AON_RESOURCES.aonIconChecked()
						: AON.AON_RESOURCES.aonIconCheck();
			}
		};
		this.addColumn(replacementColumn, AON.MSG.replacement());
		replacementColumn.setCellStyleNames(AON.AON_CSS.aonDataTableIconColumn());
		this.setColumnWidth(replacementColumn, 100, Unit.PX);
	}

	private void addNameColumn() {
		final TextColumn<Mod193> nameColumn = new TextColumn<Mod193>() {
			@Override
			public String getValue(Mod193 mod193) {
				return mod193.getName();
			}
		};
		this.addColumn(nameColumn, AON.MSG.name());
		this.setColumnWidth(nameColumn, 100, Unit.PCT);
	}

	private void addDocumentColumn() {
		final TextColumn<Mod193> documentColumn = new TextColumn<Mod193>() {
			@Override
			public String getValue(Mod193 mod193) {
				return mod193.getDocument();
			}
		};
		this.addColumn(documentColumn, AON.MSG.document());
		this.setColumnWidth(documentColumn, 150, Unit.PX);
	}

	public Mod193 getSelected() {
		return model.getLastSelectedObject();
	}
}

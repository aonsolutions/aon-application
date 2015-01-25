package com.esferalia.aon.gwt.fiscal.client.mod193;

import static com.esferalia.aon.gwt.fiscal.client.mod193.Model193.AON_RESOURCES;
import static com.esferalia.aon.gwt.fiscal.client.mod193.Model193.MSG;

import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
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
		addReplacementColumn();
		addDocumentColumn();
		addNameColumn();
		
		model = new NoSelectionModel<Mod193>(MOD193_PROVIDES_KEY);
		model.addSelectionChangeHandler( handler );
		this.setSelectionModel(model);
		this.setEmptyTableWidget(new HTML(MSG.noData()));
	}

	private void addSelectorColumn() {
		final Column<Mod193, ImageResource> selectorColumn = new Column<Mod193, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod193 mod193) {
				return AON_RESOURCES.aonIconRowSelector();
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
		this.addColumn(yearColumn, MSG.fiscalYear());
		yearColumn.setCellStyleNames(AON_RESOURCES.css().aonTextCenter());
		this.setColumnWidth(yearColumn, 100, Unit.PX);
	}
	
	private void addReplacementColumn() {
		Column<Mod193, ImageResource> replacementColumn = new Column<Mod193, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod193 mod193) {
				return mod193.isReplacement() ? AON_RESOURCES.aonIconChecked()
						: AON_RESOURCES.aonIconCheck();
			}
		};
		this.addColumn(replacementColumn, MSG.replacement());
		replacementColumn.setCellStyleNames(AON_RESOURCES.css()
				.aonDataTableIconColumn());
		this.setColumnWidth(replacementColumn, 100, Unit.PX);
	}

	private void addNameColumn() {
		final TextColumn<Mod193> nameColumn = new TextColumn<Mod193>() {
			@Override
			public String getValue(Mod193 mod193) {
				return mod193.getName();
			}
		};
		this.addColumn(nameColumn, MSG.name());
		this.setColumnWidth(nameColumn, 100, Unit.PCT);
	}

	private void addDocumentColumn() {
		final TextColumn<Mod193> documentColumn = new TextColumn<Mod193>() {
			@Override
			public String getValue(Mod193 mod193) {
				return mod193.getDocument();
			}
		};
		this.addColumn(documentColumn, MSG.document());
		this.setColumnWidth(documentColumn, 150, Unit.PX);
	}

	public Mod193 getSelected() {
		return model.getLastSelectedObject();
	}
}

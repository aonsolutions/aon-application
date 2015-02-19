package com.esferalia.aon.gwt.fiscal.client.mod184;

import static com.esferalia.aon.gwt.fiscal.client.mod184.Model184.AON_RESOURCES;
import static com.esferalia.aon.gwt.fiscal.client.mod184.Model184.MSG;

import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
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

public class Model184Table extends CellTable<Mod184> {

	private static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);
	
	public static final ProvidesKey<Mod184> MOD184_PROVIDES_KEY = new ProvidesKey<Mod184>() {
		@Override
		public Object getKey(Mod184 mod184) {
			return mod184 == null ? null : mod184.getId();
		}
	};

	private NoSelectionModel<Mod184> model;
	
	public Model184Table(SelectionChangeEvent.Handler handler) {
		super(1,TABLE_STYLE,MOD184_PROVIDES_KEY);
		this.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		addSelectorColumn();
		addYearColumn();
		addReplacementColumn();
		addDocumentColumn();
		addNameColumn();
		
		model = new NoSelectionModel<Mod184>(MOD184_PROVIDES_KEY);
		model.addSelectionChangeHandler( handler );
		this.setSelectionModel(model);
		this.setEmptyTableWidget(new HTML(MSG.noData()));
	}

	private void addSelectorColumn() {
		final Column<Mod184, ImageResource> selectorColumn = new Column<Mod184, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod184 mod184) {
				return AON_RESOURCES.aonIconRowSelector();
			}
		};
		this.addColumn(selectorColumn);
		this.setColumnWidth(selectorColumn, 20, Unit.PX);
	}

	private void addYearColumn() {
		final TextColumn<Mod184> yearColumn = new TextColumn<Mod184>() {
			@Override
			public String getValue(Mod184 mod184) {
				return Integer.toString(mod184.getYear());
			}
		};
		this.addColumn(yearColumn, MSG.fiscalYear());
		yearColumn.setCellStyleNames(AON_RESOURCES.css().aonTextCenter());
		this.setColumnWidth(yearColumn, 100, Unit.PX);
	}
	
	private void addReplacementColumn() {
		Column<Mod184, ImageResource> replacementColumn = new Column<Mod184, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod184 mod184) {
				return mod184.isReplacement() ? AON_RESOURCES.aonIconChecked()
						: AON_RESOURCES.aonIconCheck();
			}
		};
		this.addColumn(replacementColumn, MSG.replacement());
		replacementColumn.setCellStyleNames(AON_RESOURCES.css()
				.aonDataTableIconColumn());
		this.setColumnWidth(replacementColumn, 100, Unit.PX);
	}

	private void addNameColumn() {
		final TextColumn<Mod184> nameColumn = new TextColumn<Mod184>() {
			@Override
			public String getValue(Mod184 mod184) {
				return mod184.getName();
			}
		};
		this.addColumn(nameColumn, MSG.name());
		this.setColumnWidth(nameColumn, 100, Unit.PCT);
	}

	private void addDocumentColumn() {
		final TextColumn<Mod184> documentColumn = new TextColumn<Mod184>() {
			@Override
			public String getValue(Mod184 mod184) {
				return mod184.getDocument();
			}
		};
		this.addColumn(documentColumn, MSG.document());
		this.setColumnWidth(documentColumn, 150, Unit.PX);
	}

	public Mod184 getSelected() {
		return model.getLastSelectedObject();
	}
}

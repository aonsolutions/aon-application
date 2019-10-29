package com.esferalia.aon.gwt.fiscal.client.tedi;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.occam.api.model.Company;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;

public class TediCompanyTable extends CellTable<Company> {
	private static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);

	private NoSelectionModel<Company> model;

	public TediCompanyTable(ProvidesKey<Company> providesKey) {
		super(1, TABLE_STYLE, providesKey);
		this.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		addDocumentColumn();
		addNameColumn();
		model = new NoSelectionModel<Company>(providesKey);
		this.setSelectionModel(model);
		this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
	}

	public void addSelectionChangeHandler(SelectionChangeEvent.Handler handler) {
		model.addSelectionChangeHandler(handler);
	}

	private void addDocumentColumn() {
		final TextColumn<Company> documentColumn = new TextColumn<Company>() {
			@Override
			public String getValue(Company result) {
				return result.getDocument();
			}
		};
		this.addColumn(documentColumn, AON.MSG.document());
		this.setColumnWidth(documentColumn, 100, Unit.PX);
	}

	private void addNameColumn() {
		final TextColumn<Company> nameColumn = new TextColumn<Company>() {
			@Override
			public String getValue(Company result) {
				return result.getName();
			}
		};
		this.addColumn(nameColumn, AON.MSG.name());
		this.setColumnWidth(nameColumn, "auto");
	}

	public Company getSelected() {
		return model.getLastSelectedObject();
	}
}

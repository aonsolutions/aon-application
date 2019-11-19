package com.esferalia.aon.gwt.fiscal.client.tedi;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.occam.api.model.tedi.TediCompanyResult;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;

public class TediCompanyTable extends CellTable<TediCompanyResult> {
	private static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);

	private NoSelectionModel<TediCompanyResult> model;

	public TediCompanyTable(ProvidesKey<TediCompanyResult> providesKey) {
		super(1, TABLE_STYLE, providesKey);
		this.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		addDocumentColumn();
		addNameColumn();
		addInboxCountColumn();

		model = new NoSelectionModel<TediCompanyResult>(providesKey);
		this.setSelectionModel(model);
		this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
	}

	public void addSelectionChangeHandler(SelectionChangeEvent.Handler handler) {
		model.addSelectionChangeHandler(handler);
	}

	private void addDocumentColumn() {
		final TextColumn<TediCompanyResult> documentColumn = new TextColumn<TediCompanyResult>() {
			@Override
			public String getValue(TediCompanyResult result) {
				return result.getCompany().getDocument();
			}
		};
		this.addColumn(documentColumn, AON.MSG.document());
		this.setColumnWidth(documentColumn, 100, Unit.PX);
	}

	private void addNameColumn() {
		final TextColumn<TediCompanyResult> nameColumn = new TextColumn<TediCompanyResult>() {
			@Override
			public String getValue(TediCompanyResult result) {
				return result.getCompany().getName();
			}
		};
		this.addColumn(nameColumn, AON.MSG.name());
		this.setColumnWidth(nameColumn, "auto");
	}
	
	private void addInboxCountColumn() {
		final TextColumn<TediCompanyResult> inboxCountColumn = new TextColumn<TediCompanyResult>() {
			@Override
			public void render(Context context, TediCompanyResult object, SafeHtmlBuilder sb) {
				if(object.getInboxCount() != null){
					sb.appendHtmlConstant("<span>"+object.getInboxCount()+"</span>");
				}
				else {
					sb.appendHtmlConstant("<div class='aon-loader'>&nbsp;</div>");
				}
			}
			@Override
			public String getValue(TediCompanyResult result) {
				return result.getInboxCount() != null ? result.getInboxCount() +"" : "" ;
			}
		};
		this.addColumn(inboxCountColumn, "Pendientes");
		this.setColumnWidth(inboxCountColumn, 100, Unit.PX);
	}
	
	public TediCompanyResult getSelected() {
		return model.getLastSelectedObject();
	}
}

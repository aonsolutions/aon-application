package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.occam.api.model.fiscal.Mod303Activity;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Model303AEATActivityTable extends CellTable<Mod303Activity> implements HasSelectionHandlers<Mod303Activity> {
	private static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);

	public Model303AEATActivityTable(ProvidesKey<Mod303Activity> providesKey) {
		super(1,TABLE_STYLE, providesKey);
		this.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		NoSelectionModel<Mod303Activity> model = new NoSelectionModel<Mod303Activity>(providesKey);		
		setSelectionModel(model);
		model.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				SelectionEvent.<Mod303Activity>fire(Model303AEATActivityTable.this, model.getLastSelectedObject());
			}
			
		});
		
		addSelectorColumn();
		addEpigraphColumn();
		
		addDevColumn();
		addPorColumn();
		addResColumn();
		
		this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
	}

	private void addSelectorColumn() {
		final Column<Mod303Activity, ImageResource> selectorColumn = new Column<Mod303Activity, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod303Activity model) {
				return AON.AON_RESOURCES.aonIconRowSelector();
			}
		};
		this.addColumn(selectorColumn);
		this.setColumnWidth(selectorColumn, 20, Unit.PX);
	}

	private void addEpigraphColumn() {
		final TextColumn<Mod303Activity> epigraphColumn = new TextColumn<Mod303Activity>() {
			@Override
			public String getValue(Mod303Activity model) {
				return AonStringUtils.abbreviate(model.getFullDescription(), 100);
			}
		};
		this.addColumn(epigraphColumn, AON.MSG.activity());
		this.setColumnWidth(epigraphColumn, "auto");
	}

	private void addDevColumn() {
		final TextColumn<Mod303Activity> netYieldColumn = new TextColumn<Mod303Activity>() {
			@Override
			public String getValue(Mod303Activity model) {
				return AON.FMT.format(model.getDev());
			}
		};
		this.addColumn(netYieldColumn, AON.MSG.quota());
		netYieldColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
		this.setColumnWidth(netYieldColumn, 125, Unit.PX);
	}	

	private void addPorColumn() {
		final TextColumn<Mod303Activity> percentColumn = new TextColumn<Mod303Activity>() {
			@Override
			public String getValue(Mod303Activity model) {
				return AON.FMT.format(model.getPor());
			}
		};
		this.addColumn(percentColumn, AON.MSG.percent());
		percentColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
		this.setColumnWidth(percentColumn, 100, Unit.PX);
	}	

	private void addResColumn() {
		final TextColumn<Mod303Activity> amountColumn = new TextColumn<Mod303Activity>() {
			@Override
			public String getValue(Mod303Activity model) {
				return AON.FMT.format(model.getIng());
			}
		};
		this.addColumn(amountColumn, AON.MSG.result());
		amountColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
		this.setColumnWidth(amountColumn, 120, Unit.PX);
	}	

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod303Activity> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}	
}

package com.esferalia.aon.gwt.fiscal.client.mod131;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
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

public class Mod131ActivityTable extends CellTable<Mod131Activity> {
	private static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);
	

	private NoSelectionModel<Mod131Activity> model;
	
	public Mod131ActivityTable(SelectionChangeEvent.Handler handler, ProvidesKey<Mod131Activity> providesKey) {
		super(1,TABLE_STYLE, providesKey);
		this.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		addSelectorColumn();
		addEpigraphColumn();
		addNetYieldColumn();
		addPercentColumn();
		addResultColumn();
		
		model = new NoSelectionModel<Mod131Activity>(providesKey);
		if (handler != null) {
			model.addSelectionChangeHandler( handler );
		}
		this.setSelectionModel(model);
		this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
	}

	private void addSelectorColumn() {
		final Column<Mod131Activity, ImageResource> selectorColumn = new Column<Mod131Activity, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod131Activity model) {
				return AON.AON_RESOURCES.aonIconRowSelector();
			}
		};
		this.addColumn(selectorColumn);
		this.setColumnWidth(selectorColumn, 20, Unit.PX);
	}

	private void addEpigraphColumn() {
		final TextColumn<Mod131Activity> documentColumn = new TextColumn<Mod131Activity>() {
			@Override
			public String getValue(Mod131Activity model) {
				return model.getFullDescription();
			}
		};
		this.addColumn(documentColumn, AON.MSG.epigraph());
		this.setColumnWidth(documentColumn, 100, Unit.PCT);
	}

	private void addNetYieldColumn() {
		final TextColumn<Mod131Activity> amountColumn = new TextColumn<Mod131Activity>() {
			@Override
			public String getValue(Mod131Activity model) {
				return AON.FMT.format(model.getNet());
			}
		};
		this.addColumn(amountColumn, AON.MSG.netYield());
		amountColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
		this.setColumnWidth(amountColumn, 120, Unit.PCT);
	}	

	private void addPercentColumn() {
		final TextColumn<Mod131Activity> amountColumn = new TextColumn<Mod131Activity>() {
			@Override
			public String getValue(Mod131Activity model) {
				return AON.FMT.format(model.getPor());
			}
		};
		this.addColumn(amountColumn, AON.MSG.percent());
		amountColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
		this.setColumnWidth(amountColumn, 120, Unit.PCT);
	}	

	private void addResultColumn() {
		final TextColumn<Mod131Activity> amountColumn = new TextColumn<Mod131Activity>() {
			@Override
			public String getValue(Mod131Activity model) {
				return AON.FMT.format(model.getRes());
			}
		};
		this.addColumn(amountColumn, AON.MSG.result());
		amountColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
		this.setColumnWidth(amountColumn, 120, Unit.PCT);
	}	

	public Mod131Activity getSelected() {
		return model.getLastSelectedObject();
	}
}

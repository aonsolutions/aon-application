package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.occam.api.model.fiscal.Mod303ActivityFarmer;
import com.esferalia.aon.watson.util.AonNumberUtils;
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

public class Model303AEATActivityFarmerTable extends CellTable<Mod303ActivityFarmer> implements HasSelectionHandlers<Mod303ActivityFarmer> {
	private static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);
	
	public Model303AEATActivityFarmerTable(ProvidesKey<Mod303ActivityFarmer> providesKey, boolean lastPeriod) {
		super(1,TABLE_STYLE, providesKey);
		this.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		NoSelectionModel<Mod303ActivityFarmer> modelFarmer = new NoSelectionModel<Mod303ActivityFarmer>(providesKey);		
		setSelectionModel(modelFarmer);
		modelFarmer.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				SelectionEvent.<Mod303ActivityFarmer>fire(Model303AEATActivityFarmerTable.this, modelFarmer.getLastSelectedObject());
			}
			
		});
		addSelectorColumn();
		addEpigraphColumn();
		
		addVolColumn();
		addIndColumn();
		addCuoColumn();
		if ( lastPeriod ) {
			addSopColumn();
			addCadColumn();
		} else {
			addPorColumn();
			addResColumn();
		}
		
		this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
	}
	

	private void addSelectorColumn() {
		final Column<Mod303ActivityFarmer, ImageResource> selectorColumn = new Column<Mod303ActivityFarmer, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod303ActivityFarmer model) {
				return AON.AON_RESOURCES.aonIconRowSelector();
			}
		};
		this.addColumn(selectorColumn);
		this.setColumnWidth(selectorColumn, 20, Unit.PX);
	}

	private void addEpigraphColumn() {
		final TextColumn<Mod303ActivityFarmer> epigraphColumn = new TextColumn<Mod303ActivityFarmer>() {
			@Override
			public String getValue(Mod303ActivityFarmer model) {
				return AonStringUtils.abbreviate(model.getFullDescription(), 80);
			}
		};
		this.addColumn(epigraphColumn, AON.MSG.activity());
		this.setColumnWidth(epigraphColumn, "auto");
	}

	private void addVolColumn() {
		final TextColumn<Mod303ActivityFarmer> netYieldColumn = new TextColumn<Mod303ActivityFarmer>() {
			@Override
			public String getValue(Mod303ActivityFarmer act) {
				return act.isEmpty()?AonStringUtils.EMPTY:AON.FMT.format(act.getVol());
			}
		};
		this.addColumn(netYieldColumn, AON.MSG.operationsVolume());
		netYieldColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
		this.setColumnWidth(netYieldColumn, 175, Unit.PX);
	}	

	private void addIndColumn() {
		final TextColumn<Mod303ActivityFarmer> percentColumn = new TextColumn<Mod303ActivityFarmer>() {
			@Override
			public String getValue(Mod303ActivityFarmer act) {
				return act.isEmpty()?AonStringUtils.EMPTY:AonNumberUtils.toString( act.getInd() );
			}
		};
		this.addColumn(percentColumn, AON.MSG.f03Msg());
		percentColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
		this.setColumnWidth(percentColumn, 100, Unit.PX);
	}	

	private void addCuoColumn() {
		final TextColumn<Mod303ActivityFarmer> percentColumn = new TextColumn<Mod303ActivityFarmer>() {
			@Override
			public String getValue(Mod303ActivityFarmer act) {
				return act.isEmpty()?AonStringUtils.EMPTY:AON.FMT.format(act.getCuo());
			}
		};
		this.addColumn(percentColumn, AON.MSG.quota());
		percentColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
		this.setColumnWidth(percentColumn, 125, Unit.PX);
	}	
 
	private void addPorColumn() {
		final TextColumn<Mod303ActivityFarmer> percentColumn = new TextColumn<Mod303ActivityFarmer>() {
			@Override
			public String getValue(Mod303ActivityFarmer act) {
				return act.isEmpty()?AonStringUtils.EMPTY:AON.FMT.format(act.getPor());
			}
		};
		this.addColumn(percentColumn, AON.MSG.percentAbbr());
		percentColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
		this.setColumnWidth(percentColumn, 100, Unit.PX);
	}	

	private void addResColumn() {
		final TextColumn<Mod303ActivityFarmer> amountColumn = new TextColumn<Mod303ActivityFarmer>() {
			@Override
			public String getValue(Mod303ActivityFarmer act) {
				return act.isEmpty()?AonStringUtils.EMPTY:AON.FMT.format(act.getIng());
			}
		};
		this.addColumn(amountColumn, AON.MSG.result() + " [A]");
		amountColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
		this.setColumnWidth(amountColumn, 120, Unit.PX);
	}

	private void addSopColumn() {
		final TextColumn<Mod303ActivityFarmer> amountColumn = new TextColumn<Mod303ActivityFarmer>() {
			@Override
			public String getValue(Mod303ActivityFarmer act) {
				return act.isEmpty()?AonStringUtils.EMPTY:AON.FMT.format(act.getSop());
			}
		};
		this.addColumn(amountColumn, AON.MSG.page6DAbbr());
		amountColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
		this.setColumnWidth(amountColumn, 120, Unit.PX);
	}

	private void addCadColumn() {
		final TextColumn<Mod303ActivityFarmer> amountColumn = new TextColumn<Mod303ActivityFarmer>() {
			@Override
			public String getValue(Mod303ActivityFarmer act) {
				return act.isEmpty()?AonStringUtils.EMPTY:AON.FMT.format(act.getCad());
			}
		};
		this.addColumn(amountColumn, AON.MSG.derQuota() + " [B]");
		amountColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
		this.setColumnWidth(amountColumn, 120, Unit.PX);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod303ActivityFarmer> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}	

}

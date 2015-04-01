package com.esferalia.aon.gwt.fiscal.client.tree;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2015.Epigraph;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;

public class EpigraphSelectionPanel extends CustomDialog {

	public static final ProvidesKey<Epigraph> EPIGRAPH_PROVIDES_KEY = new ProvidesKey<Epigraph>() {
		@Override
		public Object getKey(Epigraph epigraph) {
			return epigraph == null ? null : epigraph.getEpigraph();
		}
	};

	public interface SelectionCallBack {
		void onSelect(Epigraph epigraph);

		void onClose();
	}

	interface EpigraphSelectionPanelBinder extends UiBinder<Widget, EpigraphSelectionPanel> {}

	private static final EpigraphSelectionPanelBinder epigraphPanelBinder = GWT
			.create(EpigraphSelectionPanelBinder.class);

	private SelectionCallBack callback;

	private NoSelectionModel<Epigraph> model;

	@UiField
	Panel tablePanel;
	@UiField(provided = true)
	CellTable<Epigraph> table;

	private Epigraph selected;

	public EpigraphSelectionPanel(SelectionCallBack callback) {
		this();
		setCallback(callback);
	}

	public EpigraphSelectionPanel() {
		setVisible(false);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setModal(true);
		setCaption(AON.MSG.activitySelection());

		AON.AON_CSS.ensureInjected();
		CellTable.Resources tableStyle = GWT.create(AonCellTable.class);

		table = new CellTable<Epigraph>(1, tableStyle, EPIGRAPH_PROVIDES_KEY);
		table.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		addSelectorColumn();
		addEpigraphColumn();
		addDescriptionColumn();

		model = new NoSelectionModel<Epigraph>(EPIGRAPH_PROVIDES_KEY);
		model.addSelectionChangeHandler(new EpigraphSelectionHandler());
		table.setSelectionModel(model);
		table.setEmptyTableWidget(new HTML(AON.MSG.noData()));
		Widget ui = epigraphPanelBinder.createAndBindUi(this);
		setWidget(ui);
	}

	public void setCallback(SelectionCallBack callback) {
		this.callback = callback;
		table.setRowData(new ArrayList<Epigraph>());
		table.setRowCount(0, true);
		selected = null;
	}

	@Override
	public void onClose() {
		this.hide();
		callback.onClose();
	}
	
	private void addSelectorColumn() {
		final Column<Epigraph, ImageResource> selectorColumn = new Column<Epigraph, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Epigraph epigraph) {
				return AON.AON_RESOURCES.aonIconRowSelector();
			}
		};
		table.addColumn(selectorColumn);
		table.setColumnWidth(selectorColumn, 20, Unit.PX);
	}

	private void addEpigraphColumn() {
		final TextColumn<Epigraph> epigraphColumn = new TextColumn<Epigraph>() {
			@Override
			public String getValue(Epigraph epigraph) {
				return epigraph.getEpigraph();
			}
		};
		table.addColumn(epigraphColumn, AON.MSG.epigraph());
		epigraphColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
		table.setColumnWidth(epigraphColumn, 100, Unit.PX);
	}

	private void addDescriptionColumn() {
		final TextColumn<Epigraph> descriptionColumn = new TextColumn<Epigraph>() {
			@Override
			public String getValue(Epigraph epigraph) {
				return epigraph.getDescription();
			}
		};
		table.addColumn(descriptionColumn, AON.MSG.fiscalYear());
		descriptionColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
		table.setColumnWidth(descriptionColumn, 90, Unit.PCT);
	}

	@UiHandler("table")
	void onTableRangeChange(RangeChangeEvent event) {
		FiscalTree.FISCAL_SERVICE.getModuleEpigraphs(FiscalTree.CURRENT_YEAR,
				new AsyncCallback<ArrayList<Epigraph>>() {
					@Override
					public void onSuccess(ArrayList<Epigraph> result) {
						table.setRowData(result);
						table.setRowCount(result.size(), true);
					}

					@Override
					public void onFailure(Throwable caught) {
						DialogMessages.alertErrorWidget(AON.MSG.unableToShowData(caught.getMessage()));
					}
				});
	}

	class EpigraphSelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			selected = model.getLastSelectedObject();
			hide();
			callback.onSelect(selected);
		}
	}

}

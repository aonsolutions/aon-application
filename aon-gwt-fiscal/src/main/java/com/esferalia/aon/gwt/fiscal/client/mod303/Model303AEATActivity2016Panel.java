package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2016.Epigraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Model303AEATActivity2016Panel extends CustomDialog {

	public interface SelectionCallBack {
		void onSelect(Epigraph cnae);
		void onClose();
	}


	private static final ProvidesKey<Epigraph> CNAE_PROVIDES_KEY = new ProvidesKey<Epigraph>() {
		@Override
		public Object getKey(Epigraph item) {
			return item.getEpigraph();
		}
	};
	
	private SelectionCallBack callback;

	private NoSelectionModel<Epigraph> model;

	@UiField(provided = true)
	CellTable<Epigraph> table;

	public Model303AEATActivity2016Panel(SelectionCallBack callback) {
		this();
		this.callback = callback;
	}

	public Model303AEATActivity2016Panel() {
		setVisible(false);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setModal(true);
		setCaption("C.N.A.E. 2009");

		ScrollPanel scroll = new ScrollPanel();
		scroll.setWidth("550px");
		scroll.setHeight("500px");
		scroll.setStyleName(AON.AON_CSS.aonPadding());

		AON.AON_RESOURCES.css().ensureInjected();
		CellTable.Resources tableStyle = GWT.create(AonCellTable.class);

		table = new CellTable<Epigraph>(1, tableStyle, CNAE_PROVIDES_KEY);
		table.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		addCodeColumn();
		addDescriptionColumn();

		model = new NoSelectionModel<Epigraph>(CNAE_PROVIDES_KEY);
		model.addSelectionChangeHandler(new SelectionChangeEvent.Handler(){
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				hide();
				callback.onSelect(model.getLastSelectedObject());
			}
		});
		table.setSelectionModel(model);
		table.setEmptyTableWidget(new HTML(AON.MSG.noData()));
		scroll.setWidget(table);
		this.setWidget(scroll);
	}
	
	public void setCallback(SelectionCallBack callback) {
		this.callback = callback;
	}
	
	@Override
	public void onClose() {
		this.hide();
		callback.onClose();
	}

	public void onShow() {
		if (table.getRowCount() == 0) {
			LinkedList<Epigraph> epigraphs = new LinkedList<Epigraph>();
			for (Epigraph epi : Epigraph.values()) {
				if (epi.hasVATModules() ) {
					epigraphs.add(epi);
				}
			}
			table.setRowData( epigraphs );
			table.setRowCount(Epigraph.values().length, true);
			center();
			show();
		} else {
			center();
			show();
		}
	}

	private void addCodeColumn() {
		final TextColumn<Epigraph> codeColumn = new TextColumn<Epigraph>() {
			@Override
			public String getValue(Epigraph cnae) {
				return cnae.getEpigraph();
			}
		};
		table.addColumn(codeColumn, AON.MSG.code());
		codeColumn.setCellStyleNames(AON.AON_RESOURCES.css().aonTextCenter());
		table.setColumnWidth(codeColumn, 80, Unit.PX);
	}

	private void addDescriptionColumn() {
		final TextColumn<Epigraph> titleColumn = new TextColumn<Epigraph>() {
			@Override
			public String getValue(Epigraph cnae) {
				return cnae.getDescription();
			}
		};
		table.addColumn(titleColumn, AON.MSG.description());
		titleColumn.setCellStyleNames(AON.AON_RESOURCES.css().aonTextLeft());
		table.setColumnWidth(titleColumn, "auto");
	}

}

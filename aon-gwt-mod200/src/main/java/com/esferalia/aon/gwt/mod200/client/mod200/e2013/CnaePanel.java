package com.esferalia.aon.gwt.mod200.client.mod200.e2013;

import java.util.Arrays;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.occam.mod200.api.model.mod200_2013.CNAE;
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

public class CnaePanel extends CustomDialog {

	public interface SelectionCallBack {
		void onSelect(CNAE cnae);
		void onClose();
	}

//	interface CnaePanelBinder extends UiBinder<Widget, CnaePanel> {
//	}
//	private static final CnaePanelBinder cnaePanelBinder = GWT
//			.create(CnaePanelBinder.class);


	private static final ProvidesKey<CNAE> CNAE_PROVIDES_KEY = new ProvidesKey<CNAE>() {
		@Override
		public Object getKey(CNAE item) {
			return item.getCode();
		}
	};
	
	private SelectionCallBack callback;

	private NoSelectionModel<CNAE> model;

	@UiField(provided = true)
	CellTable<CNAE> table;

	public CnaePanel(SelectionCallBack callback) {
		this();
		this.callback = callback;
	}

	public CnaePanel() {
		setVisible(false);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setModal(true);
		setCaption("C.N.A.E.");

		ScrollPanel scroll = new ScrollPanel();
		scroll.setWidth("550px");
		scroll.setHeight("500px");
		scroll.setStyleName(AON.AON_CSS.aonPadding());

		AON.AON_RESOURCES.css().ensureInjected();
		CellTable.Resources tableStyle = GWT.create(AonCellTable.class);

		table = new CellTable<CNAE>(1, tableStyle, CNAE_PROVIDES_KEY);
		table.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		addCodeColumn();
		addDescriptionColumn();

		model = new NoSelectionModel<CNAE>(CNAE_PROVIDES_KEY);
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
//		Widget ui = cnaePanelBinder.createAndBindUi(this);
//		setWidget(ui);

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
			table.setRowData( Arrays.asList(CNAE.values()) );
			table.setRowCount(CNAE.values().length, true);
			center();
			show();
		} else {
			center();
			show();
		}
	}

	private void addCodeColumn() {
		final TextColumn<CNAE> codeColumn = new TextColumn<CNAE>() {
			@Override
			public String getValue(CNAE cnae) {
				return cnae.getCode();
			}
		};
		table.addColumn(codeColumn, AON.MSG.code());
		codeColumn.setCellStyleNames(AON.AON_RESOURCES.css().aonTextCenter());
		table.setColumnWidth(codeColumn, 80, Unit.PX);
	}

	private void addDescriptionColumn() {
		final TextColumn<CNAE> titleColumn = new TextColumn<CNAE>() {
			@Override
			public String getValue(CNAE cnae) {
				return cnae.getDescription();
			}
		};
		table.addColumn(titleColumn, AON.MSG.description());
		titleColumn.setCellStyleNames(AON.AON_RESOURCES.css().aonTextLeft());
		table.setColumnWidth(titleColumn, "auto");
	}

}

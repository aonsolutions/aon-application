package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;

public class SelectAllHeader<T> extends Header<Boolean> {

	private DataGrid<T> dataGrid;
	private MultiSelectionModel<T> selectionModel;

	public SelectAllHeader(MultiSelectionModel<T> selectionModel,
			DataGrid<T> data) {
		super(new CheckboxCell());
		this.dataGrid = data;
		this.selectionModel = selectionModel;
		this.selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					@Override
					public void onSelectionChange(SelectionChangeEvent event) {
						dataGrid.redrawHeaders();
					}
				});

		this.dataGrid.addRangeChangeHandler(new RangeChangeEvent.Handler() {

			@Override
			public void onRangeChange(RangeChangeEvent event) {
				if (!SelectAllHeader.this.getValue())
					return;
				for (T item : SelectAllHeader.this.dataGrid
						.getVisibleItems())
					SelectAllHeader.this.selectionModel.setSelected(item,
							true);
			}
		});

	}

	@Override
	public Boolean getValue() {

		int visibleItemCount = dataGrid.getVisibleItemCount();
		return visibleItemCount > 0
				&& visibleItemCount == selectionModel.getSelectedSet()
						.size();
	}

	@Override
	public void onBrowserEvent(Context context, Element elem,
			NativeEvent event) {
		InputElement input = elem.getFirstChild().cast();
		Boolean isChecked = input.isChecked();

		for (T item : dataGrid.getVisibleItems()) {
			selectionModel.setSelected(item, isChecked);
		}
	}

	// --------------------------------------------------------------------

}
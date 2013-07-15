package com.esferalia.aon.gwt.payroll.client;

import java.util.Set;

import com.esferalia.aon.gwt.payroll.shared.HasId;
import com.esferalia.aon.gwt.payroll.shared.HasName;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

public class SelectDataGrid<T extends HasId<?> & HasName<String>> extends
		CustomDataGrid<T> {
	
	

	public SelectDataGrid() {
		super(new ProvidesKey<T>() {
			@Override
			public Object getKey(T item) {
				return item.getId();
			}
		});

		// Add a selection model to handle user selection.
		setSelectionModel(new MultiSelectionModel<T>(getKeyProvider()),
				DefaultSelectionEventManager.<T> createCheckboxManager(0));

		Header<Boolean> checkHeader = newCheckHeader();
		Column<T, Boolean> checkColumn = newCheckColumn();
		addColumn(checkColumn, checkHeader);
		setColumnWidth(checkColumn, "40px");

	}

	public boolean isAllSelected() {
		return ((Header<Boolean>) getHeader(0)).getValue();
	}

	public Set<T> getSelectedItems() {
		return ((MultiSelectionModel<T>) getSelectionModel()).getSelectedSet();
	}

	public void setNameLabel(String nameLabel) {
		Column<T, String> textColumn = newTextColumn();
		addColumn(textColumn, nameLabel);
	}

	// --------------------------------------------------------- Private methods

	private Column<T, String> newTextColumn() {
		return new Column<T, String>(new TextCell()) {
			@Override
			public String getValue(T object) {
				return object.getName();
			};
		};
	}

	/*
	 * Checkbox column. This table will uses a checkbox column for selection.
	 */
	private Column<T, Boolean> newCheckColumn() {
		return new Column<T, Boolean>(new CheckboxCell()) {
			@Override
			public Boolean getValue(T object) {
				return SelectDataGrid.this.getSelectionModel().isSelected(
						object);
			}
		};
	}

	/*
	 * Checkbox header. This table will uses a checkbox header for select all.
	 */
	private Header<Boolean> newCheckHeader() {
		Header<Boolean> header = new Header<Boolean>(new CheckboxCell(true,true)) {
			@Override
			public Boolean getValue() {
				return getVisibleItemCount() == ((MultiSelectionModel<?>) getSelectionModel())
						.getSelectedSet().size();
			}
		};
		header.setUpdater(new ValueUpdater<Boolean>() {

			@Override
			public void update(Boolean value) {
				for (int i = 0; i < getVisibleItemCount(); i++)
					getSelectionModel().setSelected(getVisibleItem(i), value);
			}
		});
		return header;
	}

}

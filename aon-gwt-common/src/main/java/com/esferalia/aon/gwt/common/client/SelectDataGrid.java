package com.esferalia.aon.gwt.common.client;

import java.util.Set;

import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.shared.HasId;
import com.esferalia.aon.gwt.common.shared.HasName;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

public class SelectDataGrid<T extends HasId<?> & HasName<String>> extends
		CustomDataGrid<T> {

	/**
	 * The default increment size.
	 */
	private static final int DEFAULT_INCREMENT = 20;

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

		addHandlers2ScrollPanel(getScrollPanel(), DEFAULT_INCREMENT);

	}

	public void setNameLabel(String nameLabel) {
		Column<T, String> textColumn = newTextColumn();
		addColumn(textColumn, nameLabel);
	}

	public boolean isAnySelected() {
		return isAllSelected() || getSelectedItems().size() > 0;
	}

	public boolean isAllSelected() {
		return ((Header<Boolean>) getHeader(0)).getValue();
	}

	public Set<T> getSelectedItems() {
		return getMultiSelectionModel().getSelectedSet();
	}

	public MultiSelectionModel<T> getMultiSelectionModel() {
		return ((MultiSelectionModel<T>) getSelectionModel());
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
		Header<Boolean> header = new Header<Boolean>(new CheckboxCell(true,
				true)) {
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

	private void addHandlers2ScrollPanel(final ScrollPanel scrollPanel,
			final int incrementSize) {

		class ScrollPanelHandlers implements ScrollHandler {

			/**
			 * The last scroll position.
			 */
			private int lastScrollPos = 0;

			@Override
			public void onScroll(ScrollEvent event) {
				// If scrolling up, ignore the event.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = scrollPanel.getVerticalScrollPosition();
				if (oldScrollPos >= lastScrollPos) {
					return;
				}

				int maxScrollTop = scrollPanel
						.getMaximumVerticalScrollPosition();
				if (lastScrollPos >= maxScrollTop) {
					// We are near the end, so increase the page size.
					int newPageSize = SelectDataGrid.this.getVisibleRange()
							.getLength() + incrementSize;
					SelectDataGrid.this.setVisibleRange(0, newPageSize);
				}

			}
		}
		;
		scrollPanel.addScrollHandler(new ScrollPanelHandlers());
	}

}

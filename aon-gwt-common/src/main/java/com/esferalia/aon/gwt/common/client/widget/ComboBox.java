package com.esferalia.aon.gwt.common.client.widget;

import static com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.BOUND_TO_SELECTION;

import java.util.List;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class ComboBox<T> extends ListBox implements HasData<T> {

	private static final int MAX_DISPLAY_ROWS = 12;
	private static final int DEFAULT_PAGE_SIZE = 24;
	private static final int DEFAULT_INCREMENT_SIZE = DEFAULT_PAGE_SIZE / 2;

	public static interface Format<C> {
		String format(C c);
	}

	public static class DefaultFormat<C> implements Format<C> {

		@Override
		public String format(C obj) {
			return obj != null ? String.valueOf(obj) : "";
		}
	}

	public static abstract class AbstractFormatSafeHtmlRenderer<E> extends
			AbstractSafeHtmlRenderer<E> {

		public abstract Format<E> getFormat();

		public abstract SafeHtml render(E e);

	}

	public static class FormatSafeHtmlRenderer<E> extends
			AbstractFormatSafeHtmlRenderer<E> {

		private Format<E> format;

		public FormatSafeHtmlRenderer(Format<E> format) {
			this.format = format;
		}

		@Override
		public Format<E> getFormat() {
			return format;
		}

		@Override
		public SafeHtml render(E e) {
			return SafeHtmlUtils.fromString(getFormat().format(e));
		}

	}

	public static interface Resources extends CellList.Resources {

		public static Resources INSTANCE = GWT.create(Resources.class);

		interface DropDownListStyle extends CellList.Style {

		}

		@Override
		@Source({ CellList.Style.DEFAULT_CSS, "comboBoxDropDownCellList.css" })
		DropDownListStyle cellListStyle();

	}

	public static class FormatSafeHtmlCell<E> extends AbstractSafeHtmlCell<E> {

		private ComboBox<E> comboBox;

		public FormatSafeHtmlCell(AbstractFormatSafeHtmlRenderer<E> renderer) {
			super(renderer, BrowserEvents.CLICK, BrowserEvents.KEYPRESS);
		}

		public FormatSafeHtmlCell(Format<E> format) {
			this(new FormatSafeHtmlRenderer<E>(format));
		}

		public AbstractFormatSafeHtmlRenderer<E> getFormatSafeHtmlRenderer() {
			return (AbstractFormatSafeHtmlRenderer<E>) super.getRenderer();
		}

		@Override
		protected void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
			sb.append(data);
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, E value,
				NativeEvent event, ValueUpdater<E> valueUpdater) {
			super.onBrowserEvent(context, parent, value, event, valueUpdater);

			String eventType = event.getType();
			if (BrowserEvents.CLICK.equals(eventType)) {
				comboBox.hideDropDownList();
				comboBox.fireChangeEvent();
			} else if (BrowserEvents.KEYPRESS.equals(eventType))
				switch (event.getKeyCode()) {
				case KeyCodes.KEY_ENTER:
				case KeyCodes.KEY_ESCAPE:
					comboBox.hideDropDownList();
					comboBox.fireChangeEvent();
				}

		}

		public void setComboBox(ComboBox<E> comboBox) {
			this.comboBox = comboBox;
		}
	}

	private CellList<T> dropDownCellList;
	private PopupPanel dropDownPopupPanel;
	private ScrollPanel dropDownScrollPanel;
	private FormatSafeHtmlCell<T> dropDowncell;
	private SingleSelectionModel<T> dropDownselectionModel;

	public ComboBox() {
		this(new DefaultFormat<T>());
	}

	public ComboBox(Format<T> format) {
		this(new FormatSafeHtmlCell<T>(format));
	}

	public ComboBox(AbstractFormatSafeHtmlRenderer<T> renderer) {
		this(new FormatSafeHtmlCell<T>(renderer));
	}

	protected ComboBox(FormatSafeHtmlCell<T> cell) {
		dropDowncell = cell;
		dropDownCellList = new CellList<T>(cell, Resources.INSTANCE);
		dropDownCellList.setPageSize(DEFAULT_PAGE_SIZE);
		dropDownPopupPanel = new PopupPanel(true);
		dropDownScrollPanel = new ScrollPanel();

		dropDownScrollPanel.add(dropDownCellList);
		dropDownPopupPanel.add(dropDownScrollPanel);
		
		// Add a selection model to handle user selection.
		dropDownselectionModel = new SingleSelectionModel<T>(i -> i);
		dropDownCellList.setSelectionModel(dropDownselectionModel);
		dropDownCellList.setKeyboardSelectionPolicy(BOUND_TO_SELECTION);

		// Do not let the scrollable take tab focus.
		dropDownScrollPanel.getElement().setTabIndex(-1);

		dropDownPopupPanel.setStyleName("aon-comboBoxPopup");

		super.insertItem("", null, "", 0);

		addHandlers2ComboBox();
		addHandlers2DropDownCellList();
		addHandlers2DropDownSelectionModel();
		addHandlers2DropDownScrollPanel();
		addHandlers2DropDownPopupPanel();

		dropDowncell.setComboBox(this);
		
	}

	// ------------------------------------------------------- delegated methods

	public void setPageSize(int pageSize) {
		dropDownCellList.setPageSize(pageSize);
	}

	public int getPageSize() {
		return dropDownCellList.getPageSize();
	}

	public void setVisibleRange(int start, int length) {
		dropDownCellList.setVisibleRange(start, length);
	}

	public T getSelected() {
		return dropDownselectionModel.getSelectedObject();
	}

	public void setSelected(T t, boolean selected) {
		dropDownselectionModel.setSelected(t, selected);
	}

	public void setSelected(int index, boolean selected) {
		T t = dropDownCellList.getVisibleItem(index);
		dropDownselectionModel.setSelected(t, selected);
	}

	// -------------------------------------------------------- HasData methods

	@Override
	public int getVisibleItemCount() {
		return dropDownCellList.getVisibleItemCount();
	}

	@Override
	public Iterable<T> getVisibleItems() {
		return dropDownCellList.getVisibleItems();
	}

	@Override
	public SelectionModel<? super T> getSelectionModel() {
		return dropDownCellList.getSelectionModel();
	}

	@Override
	public T getVisibleItem(int indexOnPage) {
		return dropDownCellList.getVisibleItem(indexOnPage);
	}

	@Override
	public void setRowData(int start, List<? extends T> values) {
		dropDownCellList.setRowData(start, values);
	}

	@Override
	public void setVisibleRangeAndClearData(Range range,
			boolean forceRangeChangeEvent) {
		dropDownCellList.setVisibleRangeAndClearData(range,
				forceRangeChangeEvent);
	}

	@Override
	public void setSelectionModel(SelectionModel<? super T> selectionModel) {
		dropDownCellList.setSelectionModel(selectionModel);
	}

	@Override
	public int getRowCount() {
		return dropDownCellList.getRowCount();
	}

	@Override
	public Range getVisibleRange() {
		return dropDownCellList.getVisibleRange();
	}

	@Override
	public boolean isRowCountExact() {
		return dropDownCellList.isRowCountExact();
	}

	@Override
	public void setRowCount(int count) {
		dropDownCellList.setRowCount(count);
	}

	@Override
	public void setRowCount(int count, boolean isExact) {
		dropDownCellList.setRowCount(count, isExact);
	}

	@Override
	public void setVisibleRange(Range range) {
		dropDownCellList.setVisibleRange(range);
	}

	@Override
	public HandlerRegistration addCellPreviewHandler(
			com.google.gwt.view.client.CellPreviewEvent.Handler<T> handler) {
		return dropDownCellList.addCellPreviewHandler(handler);
	}

	@Override
	public HandlerRegistration addRangeChangeHandler(
			com.google.gwt.view.client.RangeChangeEvent.Handler handler) {
		return dropDownCellList.addRangeChangeHandler(handler);
	}

	@Override
	public HandlerRegistration addRowCountChangeHandler(
			com.google.gwt.view.client.RowCountChangeEvent.Handler handler) {
		return dropDownCellList.addRowCountChangeHandler(handler);
	}

	// ------------------------------------------------------ overrided methods
	
	@Override
	protected void onEnsureDebugId(String baseID) {
		super.onEnsureDebugId(baseID);

	    // Set the id of popup & cell list
		dropDownPopupPanel.ensureDebugId(baseID + "-popup");
		dropDownCellList.ensureDebugId(baseID + "-celllist");
	}

	@Override
	public void setSelectedIndex(int index) {
		dropDownselectionModel.setSelected(getVisibleItem(index), true);
	}

	@Override
	public void insertItem(String item, Direction dir, String value, int index) {
		throw new UnsupportedOperationException();
	}


	public void onResizeDropDownPopup() {

		if (dropDownPopupPanel.isShowing())
			return;

		int itemCount = dropDownCellList.getVisibleItemCount();
		if ( itemCount == 0 ) 
			return;

		int listHeight = dropDownCellList.getOffsetHeight();
		int itemHeight = listHeight / itemCount;

		dropDownPopupPanel.setVisible(false);
		Element popupEl = dropDownPopupPanel.getElement();
		Document.get().getBody().appendChild(popupEl);
		popupEl.getStyle().setPosition(Position.ABSOLUTE);

		int height = Math.min(MAX_DISPLAY_ROWS * itemHeight, listHeight);
		dropDownScrollPanel.setHeight(String.valueOf(height) + "px");
		dropDownScrollPanel.onResize();

		onResizeDropDownList(dropDownScrollPanel.getOffsetWidth());

		popupEl.removeFromParent();
		popupEl.getStyle().clearPosition();
	}

	// ------------------------------------------------------- protected methods

	protected void onResizeDropDownList(int dropDownListWidth) {
		setWidth(String.valueOf(dropDownListWidth + 2 /* TODO: border-width */)
				+ "px");
	}

	protected AbstractFormatSafeHtmlRenderer<T> getFormatSafeHtmlRenderer() {
		return (AbstractFormatSafeHtmlRenderer<T>) dropDowncell.getRenderer();
	}

	// --------------------------------------------------------- private methods

	private void showDropDownList() {

		dropDownPopupPanel.setVisible(false);

		dropDownPopupPanel.show();

		int left = getAbsoluteLeft();
		int top = getAbsoluteTop() + getOffsetHeight();

		dropDownPopupPanel.setPopupPosition(left, top);

		int listHeight = dropDownCellList.getOffsetHeight();
		// int rowCount = dropDownCellList.getRowCount();
		int itemCount = dropDownCellList.getVisibleItemCount();
		int itemHeight = listHeight / itemCount;

		int height = Math.min(MAX_DISPLAY_ROWS * itemHeight, listHeight);
		dropDownScrollPanel.setHeight(String.valueOf(height) + "px");
		dropDownScrollPanel.onResize();

		onResizeDropDownList(dropDownScrollPanel.getOffsetWidth());

		dropDownPopupPanel.setVisible(true);

		dropDownCellList.setFocus(true);

		ensureItemVisible(getSelected());
	}

	private void hideDropDownList() {
		dropDownPopupPanel.hide();
	}


	private void addHandlers2ComboBox() {
		class ComboBoxHandlers implements MouseDownHandler {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				ComboBox.this.showDropDownList();
				// skip standard behaviour
				event.preventDefault();
				// event.stopPropagation();
			}
		}
		ComboBoxHandlers handlers = new ComboBoxHandlers();
		addMouseDownHandler(handlers);
	}

	private void addHandlers2DropDownCellList() {
		class DropDownCellListHandler implements RangeChangeEvent.Handler {
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				ComboBox.this.onResizeDropDownPopup();
			}
		}
		DropDownCellListHandler handler = new DropDownCellListHandler();
		dropDownCellList.addRangeChangeHandler(handler);
	}

	private void addHandlers2DropDownSelectionModel() {
		class SelectionModelHandlers implements Handler {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				T selected = ComboBox.this.dropDownselectionModel
						.getSelectedObject();
				Format<T> format = ComboBox.this.dropDowncell
						.getFormatSafeHtmlRenderer().getFormat();
				String text = format.format(selected);
				ComboBox.this.setItemText(0, text);
				
			}
		}
		SelectionModelHandlers handlers = new SelectionModelHandlers();
		dropDownselectionModel.addSelectionChangeHandler(handlers);
	}

	private void addHandlers2DropDownScrollPanel() {

		// This scroll handler is invoked on any scrolling event captured by the
		// items list. It checks whether the scrolling position value is equal
		// to the last or first item position and tries to render the next or
		// previous page of items
		class ScrollPanelHandlers implements ScrollHandler, ScheduledCommand {

			private T visibleItem = null;

			@Override
			public void execute() {
				dropDownCellList.setFocus(true);

				ensureItemVisible(visibleItem);
			}

			@Override
			public void onScroll(ScrollEvent event) {
				if (visibleItem != null) {
					visibleItem = null;
					return;
				}

				int lastScrollPos = ComboBox.this.dropDownScrollPanel
						.getVerticalScrollPosition();

				int maxScrollPos = ComboBox.this.dropDownScrollPanel
						.getMaximumVerticalScrollPosition();
				int minScrollPos = ComboBox.this.dropDownScrollPanel
						.getMinimumVerticalScrollPosition();

				Range range = ComboBox.this.getVisibleRange();
				int rangeStart = range.getStart();
				int rangeLength = range.getLength();

				if (lastScrollPos <= minScrollPos) {
					// We are near the start, so decrease the page start.
					int newRangeStart = Math.max(rangeStart
							- DEFAULT_INCREMENT_SIZE, 0);
					visibleItem = getVisibleItem(0);
					int incrementSize = rangeStart - newRangeStart;
					ComboBox.this.setVisibleRange(newRangeStart, rangeLength
							+ incrementSize);
					Scheduler.get().scheduleFinally(this);

				} else if (lastScrollPos >= maxScrollPos) {
					visibleItem = getVisibleItem(getVisibleItemCount() - 1);
					// We are near the end, so increase the page size.
					ComboBox.this.setVisibleRange(rangeStart, rangeLength
							+ DEFAULT_INCREMENT_SIZE);

					Scheduler.get().scheduleFinally(this);
				}

			}

		}
		;

		ScrollPanelHandlers handlers = new ScrollPanelHandlers();
		dropDownScrollPanel.addScrollHandler(handlers);
	}

	private void addHandlers2DropDownPopupPanel() {
	}

	private void ensureItemVisible(T t) {
		List<T> items = dropDownCellList.getVisibleItems();
		int index = items.indexOf(t);
		Element rowElement = dropDownCellList.getRowElement(index);
		ensureVisibleImpl(rowElement);
	}

	private void ensureVisibleImpl(Element el) {
		if (el == null)
			return;
		Element scrollElement = dropDownScrollPanel.getElement();

		int realOffset = 0;
		for (Element item = el; item != scrollElement; item = item
				.getParentElement())
			realOffset += item.getOffsetTop();

		dropDownScrollPanel.setVerticalScrollPosition(realOffset
				- scrollElement.getOffsetHeight() / 2);
	}

	private void fireChangeEvent() {
		Scheduler.get().scheduleFinally(new ScheduledCommand() {

			@Override
			public void execute() {
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(),
						ComboBox.this);
			}
		});
	}

}

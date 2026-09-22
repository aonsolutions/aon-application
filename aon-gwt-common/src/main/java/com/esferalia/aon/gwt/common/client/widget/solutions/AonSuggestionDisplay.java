package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Collection;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.SuggestionCallback;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class AonSuggestionDisplay extends SuggestBox.DefaultSuggestionDisplay {

	private static final int MAX_HEIGHT_PX = 300;

	public AonSuggestionDisplay() {
		PopupPanel popup = getPopupPanel();
		popup.addStyleName("aonSuggestPopup");
		popup.getElement().getStyle().setPropertyPx("maxHeight", MAX_HEIGHT_PX);
		popup.getElement().getStyle().setOverflowY(Overflow.AUTO);
		popup.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
	}

	@Override
	protected void showSuggestions(SuggestBox suggestBox, Collection<? extends Suggestion> suggestions,
			boolean isDisplayStringHTML, boolean isAutoSelectEnabled, SuggestionCallback callback) {
		super.showSuggestions(suggestBox, suggestions, isDisplayStringHTML, isAutoSelectEnabled, callback);
		// cada vez que se abre la lista, volvemos arriba
		getPopupPanel().getElement().setScrollTop(0);
		if (isAutoSelectEnabled)
			scrollSelectionIntoView();
	}

	@Override
	protected void moveSelectionDown() {
		super.moveSelectionDown();
		scrollSelectionIntoView();
	}

	@Override
	protected void moveSelectionUp() {
		super.moveSelectionUp();
		scrollSelectionIntoView();
	}

	private void scrollSelectionIntoView() {
		// diferido: la clase "-selected" se aplica dentro del super()
		Scheduler.get().scheduleDeferred(() -> {
			PopupPanel popup = getPopupPanel();
			if (null == popup || !popup.isShowing())
				return;

			Element root = popup.getElement();
			Element selected = findSelected(root);
			if (null == selected)
				return;

			scrollTo(findScrollable(root, selected), selected);
		});
	}

	private static native Element findSelected(Element root) /*-{
		return root.querySelector(".item-selected, .gwt-MenuItem-selected, [aria-selected='true']") || null;
	}-*/;

	private static Element findScrollable(Element root, Element item) {
		Element current = item.getParentElement();
		while (null != current) {
			if (current.getScrollHeight() > current.getClientHeight() + 1)
				return current;
			if (current == root)
				break;
			current = current.getParentElement();
		}
		return root;
	}

	private static void scrollTo(Element container, Element item) {
		int scroll = container.getScrollTop();
		int itemTop = item.getAbsoluteTop() - container.getAbsoluteTop() + scroll;
		int itemBottom = itemTop + item.getOffsetHeight();
		int viewBottom = scroll + container.getClientHeight();

		if (itemTop < scroll)
			container.setScrollTop(itemTop);
		else if (itemBottom > viewBottom)
			container.setScrollTop(itemBottom - container.getClientHeight());
	}

}
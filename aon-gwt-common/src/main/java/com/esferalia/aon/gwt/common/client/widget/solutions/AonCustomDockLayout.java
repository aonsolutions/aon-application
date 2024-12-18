package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AonCustomDockLayout extends DockLayoutPanel {

	// Toolbar
	private AonToolbar toolbar;
	
	private SearchFilterComponent searchFilterComponent;
	private AonToolbarSearchBox aonToolbarSearchBox;
	
	protected AonCustomDockLayout(String title) {
		super(Unit.PX);
		if(AonStringUtils.isNotBlank(title))
			createToolbar(title, true);
	}
	
	protected AonCustomDockLayout(String title, boolean searchFilter) {
		super(Unit.PX);
		if(AonStringUtils.isNotBlank(title))
			createToolbar(title, searchFilter);
	}
	
	// Toolbar
	
	private void createToolbar(String title, boolean searchFilter) {
		toolbar = new AonToolbar(title);
		toolbar.addStyleName(AON.CSS.aonNoBorderToolbar());
			
		if(searchFilter) {
			searchFilterComponent = new SearchFilterComponent() {

				@Override
				protected void fireClearFilter() {
					onClearFilter();
				}
		
			};
		
			toolbar.showSearchPanel(searchFilterComponent);
		}
		
		addNorth(toolbar, AonToolbar.HEIGTH);
	}
	
	public AonToolbar getToolbar() {
		return toolbar;
	}
	
	public void setToolbarTitle(String title) {
		getToolbar().setTitle(title);
	}
	
	public void addToolbarButton(Widget widget) {
		getToolbar().add(widget);
	}

	public int getToolbarButtonCount() {
		return getToolbar().getButtonContainer().getWidgetCount();
	}
	
	public FlowPanel getToolbarButtonPanel() {
		return getToolbar().getButtonContainer();
	}
	
	public void setSearchZIndex(Integer zIndex) {
		searchFilterComponent.setSearchZIndex(zIndex);
	}
	
	public void setPopupHeight(String height) {
		searchFilterComponent.setPopupHeight(height);
	}
	
	public TextBox getSearchTextBox() {
		return searchFilterComponent.getSearchTextBox();
	}
	
	public void setSearchPlaceholder(String placeholder) {
		searchFilterComponent.setPlaceholder(placeholder);
	}
	
	public void addKeyUpHandler(KeyUpHandler handler) {
		searchFilterComponent.addKeyUpHandler(handler);
	}
	
	public void hideSearchWidget() {
		searchFilterComponent.hideSearchWidget();
	}
	
	public void addFilterWidget(Widget widget) {
		searchFilterComponent.addFilterWidget(widget);
	}
	
	public void addSortWidget(Widget widget) {
		searchFilterComponent.addSortWidget(widget);
	}
	
	public void addUtilityOption(Button button, String text) {
		searchFilterComponent.addUtilityOption(button, text);
	}
	
	public void addToolbarSearchBox(Widget advancedSearch, Consumer<String> onValueChangeFunction) {
		aonToolbarSearchBox = new AonToolbarSearchBox() {

			@Override
			public void onValueChange(String value) {
				onValueChangeFunction.accept(value);
			}
		};
		if(advancedSearch != null) aonToolbarSearchBox.setAdvancedSearch(advancedSearch);
		getToolbar().showSearchPanel(aonToolbarSearchBox);
	}

	public void hideToolbarFilterMessages() {
		toolbar.getMessagePanel().getElement().getStyle().setDisplay(Display.NONE);
		toolbar.getFilterPanel().getElement().getStyle().setDisplay(Display.NONE);
	}
	
	protected abstract void onClearFilter();

}

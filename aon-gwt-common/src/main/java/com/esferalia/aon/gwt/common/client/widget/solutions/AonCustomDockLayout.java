package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AonCustomDockLayout extends DockLayoutPanel {

	private static final String EMPTY_STRING = "";

	// Toolbar
	private Label titleLabel;
	
	private HTMLPanel toolbarLeft = new HTMLPanel(EMPTY_STRING);
	
	private HTMLPanel toolbarRight = new HTMLPanel(EMPTY_STRING);
	
	private SearchFilterComponent searchFilterComponent;
	
	public AonCustomDockLayout(String title) {
		super(Unit.PX);
		
		searchFilterComponent = new SearchFilterComponent() {

			@Override
			protected void fireClearFilter() {
				onClearFilter();
			}
		
		};
		
		if(AonStringUtils.isNotBlank(title))
			createToolbar(title);
	}
	
	// Toolbar
	
	private void createToolbar(String title) {
		HTMLPanel toolbar = new HTMLPanel(EMPTY_STRING);
		toolbar.addStyleName(AON.CSS.aonFlexBetween());
		toolbar.getElement().getStyle().setProperty("padding", "0 1rem");
		
		// Left
		toolbarLeft.addStyleName(AON.CSS.aonItemFlex());
		toolbar.add(toolbarLeft);
		
		// Right
		toolbarRight.addStyleName(AON.CSS.aonItemFlex());
		
		titleLabel = new Label(title);
		titleLabel.setStyleName(AON.CSS.aonToolbarTitle());
		
		toolbarRight.add(searchFilterComponent);
		toolbarRight.add(titleLabel);
		
		toolbar.add(toolbarRight);
		
		addNorth(toolbar, 50.00);
	}
	
	public void setToolbarTitle(String title) {
		titleLabel.setText(title);
	}
	
	public void addToolbarButton(Widget widget) {
		toolbarLeft.add(widget);
	}

	public int getToolbarButtonCount() {
		return toolbarLeft.getWidgetCount();
	}
	
	public HTMLPanel getToolbarButtonPanel() {
		return toolbarLeft;
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
	
	protected abstract void onClearFilter();

}

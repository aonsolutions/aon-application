package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonSimpleDialog extends PopupPanel implements AonCustomDialogListener {

	private DockLayoutPanel dockPanel;
	private List<AonCustomDialogListener> listeners;
	
	private Label captionLabel; 
	private SimpleLayoutPanel simplePanel;

	public AonSimpleDialog() {
		setStyleName(AON.CSS.aonCustomDialog());
		dockPanel = new DockLayoutPanel(Unit.PX);

		FlowPanel dialogBar = new FlowPanel();
		dialogBar.setStyleName(AON.CSS.aonCustomDialogHeader());
		captionLabel = new Label();
		captionLabel.setStyleName(AON.CSS.aonCustomDialogTitle());
		captionLabel.getElement().getStyle().setCursor(Cursor.DEFAULT);
		dialogBar.add(captionLabel);
		
		listeners = new LinkedList<AonCustomDialogListener>();
		FocusPanel focusBar = new FocusPanel(dialogBar);
		dockPanel.addNorth(focusBar,40);

		simplePanel = new SimpleLayoutPanel();
		dockPanel.add(simplePanel);

		super.setWidget(dockPanel);

		setGlassEnabled(true);
		setGlassStyleName(AON.CSS.aonCustomDialogGlass());
		setAnimationEnabled(true);
		setModal(false);
		addCloseHandler(this);
	}

	public void addCloseHandler(AonCustomDialogListener listener) {
		listeners.add(listener);
	}

	public void removeCloseHandler(AonCustomDialogListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void onClose() {
		hide();
	}

	public void handleMaximize() {
	}

	@Override
	public void setWidget(Widget w) {
		simplePanel.setWidget(w);
	}

	@Override
	public Widget getWidget() {
		return simplePanel.getWidget();
	}

	public String getCaption() {
		return captionLabel.getText();
	}

	public void setCaption(String caption) {
		captionLabel.setText(caption);
	}

	public void handleMove(int absX, int absY) {
		RootPanel.get().setWidgetPosition(this, getAbsoluteLeft() + absX, getAbsoluteTop() + absY);
	}

}

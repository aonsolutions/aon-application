package com.esferalia.aon.gwt.office.client;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class LeftButtonsMenuBar extends Composite {

	interface LeftMenuBarListener {

		void onNewIssueClickEvent();

		void onShowOpenIssuesClickEvent();

		void onShowClosedIssuesClickEvent();

		void onShowAllIssuesClickEvent();

		void onShowDeletedIssuesClickEvent();

		void onShowQuestionIssuesClickEvent();

		void onShowErrorIssuesClickEvent();

		void onShowFaqsIssuesClickEvent();

		void onLabelIssueClickEvent(Button button);
	}

	private static LeftButtonsMenuBarUiBinder uiBinder = GWT
			.create(LeftButtonsMenuBarUiBinder.class);

	interface LeftButtonsMenuBarUiBinder
			extends UiBinder<Widget, LeftButtonsMenuBar> {
	}

	@UiField
	Tree tree;
	@UiField
	Button newButton;

	private Images images;
	private PopupPanel newPopup;
	private Button selectedButton;
	private List<LeftMenuBarListener> listeners;

	private TreeItem labelsItem;

	public LeftButtonsMenuBar() {

		initWidget(uiBinder.createAndBindUi(this));
		images = GWT.create(Images.class);

		this.selectedButton = new Button();
		this.listeners = new LinkedList<LeftMenuBarListener>();

		initNewPopupMenu();
		initTree();

		// setFontBoldColor(openIssues);

	}

	// ******************************************************************
	// ************************* UI HANDLERS ****************************
	// ******************************************************************

	@UiHandler("tree")
	void onTreeItemSelection(SelectionEvent<TreeItem> event)  {
		TreeItem item = event.getSelectedItem();
		Window.alert(item.getText());
		
		if ( item.getText().equals("Pendientes"))
			onOpenIssueTreeItemSelected();
		else if (item.getText().equals("Cerrados"))
			onClosedIssueTreeItemSelected();
		
	}
	@UiHandler("newButton")
	void onNewButtonClicked(ClickEvent event) {
		int left = newButton.getAbsoluteLeft();
		int top = newButton.getAbsoluteTop() + newButton.getOffsetHeight();
		newPopup.setPopupPosition(left, top);
		newPopup.show();
	}
	
	// ******************************************************************
	// ******************************************************************

	// ******************************************************************
	// *********************** PUBLIC METHODS ***************************
	// ******************************************************************

	public void addListener(LeftMenuBarListener listener) {
		listeners.add(listener);
	}

	public void removeListener(LeftMenuBarListener listener) {
		listeners.remove(listener);
	}

	public void addLabelIssueButton(Tag label) {
		addLabel(label);
	}

	public void changeOpenIssuesText(int number) {
		// openIssues.setText(openIssues.getText() + " (" + number + ")");
	}

	public boolean isSelected(Button button) {
		return button == selectedButton;
	}

	// ******************************************************************
	// ********************** PRIVATE METHODS ***************************
	// ******************************************************************

	private void onOpenIssueTreeItemSelected() {
		for (LeftMenuBarListener listener : listeners)
			listener.onShowOpenIssuesClickEvent();
	}
	
	private void onClosedIssueTreeItemSelected() {
		for (LeftMenuBarListener listener : listeners)
			listener.onShowClosedIssuesClickEvent();
	}
	
	private void initTree() {
		
		

		TreeItem openItem = new TreeItem(
				imageItemHtml(images.aon_icon_issue_opened(), "Pendientes"));
		tree.addItem(openItem);

		TreeItem closedItem = new TreeItem(
				imageItemHtml(images.aon_icon_issue_closed(), "Cerrados"));
		tree.addItem(closedItem);

		labelsItem = new TreeItem(
				imageItemHtml(images.aon_icon_issue_title(), "Etiquetas"));
	
		tree.addItem(labelsItem);
	}

	private SafeHtml imageItemHtml(ImageResource imageProto, String title) {
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.append(AbstractImagePrototype.create(imageProto).getSafeHtml());
		builder.append(' ');
		builder.appendEscaped(title);
		return builder.toSafeHtml();
	}

	private void initNewPopupMenu() {

		newPopup = new PopupPanel();
		MenuBar menuBar = new MenuBar();

		MenuItem newLabelMenuItem = new MenuItem(
				imageItemHtml(images.aon_icon_issue_title(), "Etiqueta"),
				new Command() {

					@Override
					public void execute() {
						Window.alert("Creando nueva etiqueta");
					}
				});
		menuBar.addItem(newLabelMenuItem);

		newPopup.add(menuBar);
		newPopup.setStyleName("gwt-MenuBarPopup");
		newPopup.setAutoHideEnabled(true);
	}

	private void addLabel(Tag label) {

		final TreeItem item = new TreeItem(
				imageItemHtml(images.aon_icon_issue_title(), label.getName()));
		labelsItem.addItem(item);
		newPopup.setAutoHideEnabled(true);
	}
}

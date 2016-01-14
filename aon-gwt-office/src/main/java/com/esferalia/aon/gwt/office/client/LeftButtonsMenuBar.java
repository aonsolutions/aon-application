package com.esferalia.aon.gwt.office.client;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.common.client.widget.OptionsToolbar;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class LeftButtonsMenuBar extends Composite implements SelectionHandler<TreeItem> {

	interface LeftMenuBarListener {

		void onNewIssueClickEvent(ClickEvent event);

		void onShowOpenIssuesClickEvent();

		void onShowClosedIssuesClickEvent(ClickEvent event);

		void onShowAllIssuesClickEvent(ClickEvent event);

		void onShowDeletedIssuesClickEvent(ClickEvent event);

		void onShowQuestionIssuesClickEvent(ClickEvent event);

		void onShowErrorIssuesClickEvent(ClickEvent event);

		void onShowFaqsIssuesClickEvent(ClickEvent event);

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
	OptionsToolbar toolbar;

	private Images images;
	private PopupPanel newPopup;
	private Button selectedButton;
	private List<LeftMenuBarListener> listeners;
	
	private TreeItem labelsItem;

	public LeftButtonsMenuBar() {

		initWidget(uiBinder.createAndBindUi(this));
		images = GWT.create(Images.class);

		this.tree.addSelectionHandler(this);
		this.selectedButton = new Button();
		this.listeners = new LinkedList<LeftMenuBarListener>();

		toolbar.setVisibleCollapseButton(false);
		toolbar.setVisibleDraftButton(false);
		toolbar.setVisibleCopyButton(false);
		toolbar.setVisiblePasteButton(false);
		toolbar.setVisibleViewButton(false);	
		
		initTree();

		// setFontBoldColor(openIssues);

	}

	// ******************************************************************
	// ************************* UI HANDLERS ****************************
	// ******************************************************************

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

		MenuItem newLabelMenuItem = new MenuItem("Etiqueta", new Command() {

			@Override
			public void execute() {
				Tag tag = new Tag();

			}
		});
	}

	private void addLabel(Tag label) {
		
		final TreeItem item = new TreeItem(imageItemHtml(images.aon_icon_issue_title(), label.getName()));		
		labelsItem.addItem(item);

//		 final Button labelButton = new Button();
//		
//		 labelButton.setText(label.getName());
//		 labelButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
//		 labelButton.getElement().getStyle().setFontWeight(FontWeight.BOLD);
//		
//		 if (label.getColor() != null && !isWhite(label.getColor()))
//		 labelButton.getElement().getStyle()
//		 .setColor("#" + label.getColor());
//		
//		 setLabelClickEvent(labelButton);
//		 labelsItem.addItem(labelButton);
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		TreeItem item = event.getSelectedItem();
		String name = item.getText();
		
		switch (name) {
		case "Pendientes":
			for ( LeftMenuBarListener listener :  listeners)
				listener.onShowOpenIssuesClickEvent();
			break;		
		default:
			break;
		}

		
	}
}

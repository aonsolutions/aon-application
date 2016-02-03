package com.esferalia.aon.gwt.office.client;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class TagTree extends Composite
		implements SelectionHandler<TreeItem>, KeyDownHandler {

	interface Listener {

		void onAddNewTag(Tag tag);

		void onDeleteTag(Tag tag);
		
		void onHideTagsPanel();
	}

	private static TagTreeUiBinder uiBinder = GWT.create(TagTreeUiBinder.class);

	interface TagTreeUiBinder extends UiBinder<Widget, TagTree> {
	}

	@UiField
	Tree tree;

	@UiField
	TreeItem statusTreeItem;
	@UiField
	TreeItem priorityTreeItem;
	@UiField
	TreeItem typeTreeItem;
	@UiField
	TreeItem issueTreeItem;

	@UiField
	Button draftButton;
	@UiField
	Button newButton;
	@UiField
	Button hideButton;

	private Tag tagSelected;
	private List<Listener> listeners;

	public TagTree() {
		initWidget(uiBinder.createAndBindUi(this));

		listeners = new LinkedList<Listener>();
		tree.addSelectionHandler(this);
		tree.addKeyDownHandler(this);
	}

	public void insertTag(Tag tag) {
		TreeItem treeItem = new TreeItem();
		treeItem.setText(tag.getName());
		treeItem.setUserObject(tag);
		getTreeItem(tag.getType()).addItem(treeItem);
		getTreeItem(tag.getType()).setState(true);
	}

	private void deleteTag(Tag tag) {

		TreeItem treeItem = getTreeItem(tag.getType());
		for (int x = 0; x < treeItem.getChildCount(); x++) {
			if (treeItem.getChild(x).getText().compareTo(tag.getName()) == 0)
				treeItem.removeItem(treeItem.getChild(x));
		}
	}

	private TreeItem getTreeItem(byte type) {

		if (type == TagType.OFFICE_STATUS.value())
			return statusTreeItem;
		else if (type == TagType.OFFICE_PRIORITY.value())
			return priorityTreeItem;
		else if (type == TagType.OFFICE_TYPE.value())
			return typeTreeItem;
		else
			return issueTreeItem;
	}

	public void addListener(Listener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		this.listeners.remove(listener);
	}
	
	@UiHandler("newButton")
	void onNewButtonClick(ClickEvent event) {
		new TagDialog() {
			{
				center();
				show();
			}

			@Override
			protected void onAccept() {

				if (getTagName().trim().isEmpty())
					Window.alert("Nombre de etiqueta vacia");
				else {
					Tag tag = new Tag();
					tag.setName(getTagName());
					tag.setType(getItemSelectedValue().byteValue());

					if (evalNameOfTag(tag))
						Window.alert("Etiqueta repetida");
					else {
						onAddNewTag(tag);
						hide();
					}
				}
			}
		};
	}
	
	@UiHandler("draftButton")
	void onDraftButtonClick(ClickEvent event) {
		if (tagSelected != null)
			confirm2Delete(tagSelected);
	}
	
	@UiHandler("hideButton")
	void onHideButtonClick(ClickEvent event) {
		for (Listener listener : listeners)
			listener.onHideTagsPanel();
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {

		TreeItem item = event.getSelectedItem();
		Object userObject = item.getUserObject();

		if (userObject == null)
			tagSelected = null;
		else if (userObject instanceof Tag)
			tagSelected = (Tag) userObject;
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		int keyCode = event.getNativeKeyCode();
		Object object = tree.getSelectedItem().getUserObject();

		if (object == null)
			tagSelected = null;
		else if (keyCode == KeyCodes.KEY_DELETE && object instanceof Tag)
			confirm2Delete((Tag) object);
	}

	public void onAddNewTag(Tag tag) {
		for (Listener listener : listeners)
			listener.onAddNewTag(tag);
	}

	public void onDeleteTag(Tag tag) {
		for (Listener listener : listeners)
			listener.onDeleteTag(tag);

		deleteTag(tag);
	}

	// --------------------------------------------------------------

	private boolean evalNameOfTag(Tag tag) {

		boolean encontrado = false;

		for (int y = 0; y < tree.getItemCount(); y++) {
			TreeItem treeItem = tree.getItem(y);
			for (int x = 0; x < treeItem.getChildCount(); x++) {
				TreeItem childTree = treeItem.getChild(x);
				if (childTree.getText().toUpperCase()
						.compareTo(tag.getName().toUpperCase()) == 0)
					encontrado = true;
			}
		}
		return encontrado;
	}

	private void confirm2Delete(Tag tag) {

		if (Window.confirm("\u00BFDesea borrar la etiqueta "
				+ tagSelected.getName() + "?"))
			onDeleteTag(tagSelected);
	}
}

package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class TagDialog extends CustomDialog {

	private static TagDialogUiBinder uiBinder = GWT
			.create(TagDialogUiBinder.class);

	interface TagDialogUiBinder extends UiBinder<Widget, TagDialog> {
	}

	@UiField
	ListBox categoryListBox;
	@UiField
	TextBox nameTextBox;

	@UiField
	Button cancelButton;
	@UiField
	Button acceptButton;

	public TagDialog() {
		setCaption("Nueva etiqueta");
		setWidget(uiBinder.createAndBindUi(this));

		this.categoryListBox.addItem("Prioridad",
				String.valueOf(TagType.OFFICE_PRIORITY.value()));
		this.categoryListBox.addItem("Tipo",
				String.valueOf(TagType.OFFICE_TYPE.value()));
		this.categoryListBox.addItem("Incidencia",
				String.valueOf(TagType.OFFICE_NOTICE.value()));

		acceptButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {				
				onAccept();
			}
		});

		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
	}

	protected abstract void onAccept();

	public String getItemSelected() {
		return categoryListBox.getSelectedItemText();
	}
	
	public Integer getItemSelectedValue() {
		return Integer.parseInt(categoryListBox.getSelectedValue());
	}

	public String getTagName() {
		return nameTextBox.getText();
	}
}

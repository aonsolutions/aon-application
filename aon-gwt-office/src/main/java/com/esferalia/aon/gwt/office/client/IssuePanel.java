package com.esferalia.aon.gwt.office.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.FilterDialog;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class IssuePanel extends CustomDialog {

	interface Listener {

		void onCreateNewTag(Tag tag);

		void onCreateNewIssue(Notice notice);
	}

	interface IssuePanelUiBinder extends UiBinder<Widget, IssuePanel> {
	}

	private static IssuePanelUiBinder uiBinder = GWT
			.create(IssuePanelUiBinder.class);

	@UiField
	HorizontalPanel statusHPanel;
	@UiField
	HorizontalPanel priorityHPanel;
	@UiField
	HorizontalPanel typeHPanel;
	@UiField
	HorizontalPanel labelsHPanel;

	@UiField
	Label newStatusButton;
	@UiField
	Label newTypeButton;
	@UiField
	Label newPriorityButton;
	@UiField
	Label newLabelButton;

	@UiField
	TextBox titleTextBox;
	@UiField (provided = true)
	SuggestBox registrySuggest;
	@UiField
	TextArea commentTextArea;

	@UiField
	Button acceptButton;
	@UiField
	Button cancelButton;
	
	private String type;
	private String priority;
	private String company;
	
	private List<Listener> listeners;	
	private List<Registry> registryList;
	private Map<String, String> selectedTags;
	private List<Tag> tagsList = new LinkedList<Tag>();
	private MultiWordSuggestOracle registries = new MultiWordSuggestOracle();

	public IssuePanel(String title) {
		setCaption(title);
		registrySuggest = new SuggestBox(registries);
		setWidget(uiBinder.createAndBindUi(this));

		setAnimationEnabled(true);
		setGlassEnabled(true);
		
		this.selectedTags = new HashMap<String, String>();
		this.listeners = new LinkedList<Listener>();
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public void showPopupPanel() {
		center();
	}

	// ----------------------------------------------------

	public void setRegistries(List<Registry> registries) {
		this.registrySuggest.setEnabled(true);
		this.registryList = registries;
		for ( Registry registry : registries ) {
			this.registries.add(registry.getName());
		}
	}
	
	public void setTagList(List<Tag> tagList) {
		this.tagsList = tagList;

		for (Tag tag : tagList)
			addTag(tag);
	}
	
	private void addTagSelected(String name) {
		selectedTags.put(name, name);
	}
	
	private void removeTagSelected(String name) {
		selectedTags.remove(name);
	}

	public void addTag(Tag tag) {

		if (tag.getType() == TagType.OFFICE_NOTICE.value()) {
			CheckBox check = new CheckBox(tag.getName());			
			check.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					CheckBox cb = (CheckBox) event.getSource();
					if (cb.getValue() == false)
						removeTagSelected(cb.getText());
						
					else
						addTagSelected(cb.getText());						
				}
			});

			labelsHPanel.add(check);

		} else if (tag.getType() == TagType.OFFICE_PRIORITY.value()) {
			RadioButton radioButton = new RadioButton("PRIORITY",
					tag.getName());
			radioButton
					.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

						@Override
						public void onValueChange(
								ValueChangeEvent<Boolean> event) {
							RadioButton rb = (RadioButton) event.getSource();
							IssuePanel.this.priority = rb.getText();
						}
					});
			priorityHPanel.add(radioButton);
		}

		else if (tag.getType() == TagType.OFFICE_TYPE.value()) {
			RadioButton radioButton = new RadioButton("TYPE", tag.getName());
			radioButton
					.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

						@Override
						public void onValueChange(
								ValueChangeEvent<Boolean> event) {
							RadioButton rb = (RadioButton) event.getSource();
							IssuePanel.this.type = rb.getText();
						}
					});
			typeHPanel.add(radioButton);
		}

		else if (tag.getType() == TagType.OFFICE_STATUS.value()) {
			RadioButton radioButton = new RadioButton("STATUS", tag.getName());
			radioButton.setEnabled(false);
			
			if (tag.getName().compareTo(NoticeStatus.OPEN.getValue()) == 0)
				radioButton.setValue(true);

			statusHPanel.add(radioButton);
		}
	}

	public void setTitle(String title) {
		titleTextBox.setValue(title);
	}

	public void setBody(String body) {
		commentTextArea.setValue(body);
	}

	public void setPriority(String priority) {

		Iterator<Widget> iter = priorityHPanel.iterator();

		while (iter.hasNext()) {
			RadioButton rb = (RadioButton) iter.next();
			if (rb.getText().compareTo(priority) == 0)
				rb.setValue(true);
		}
	}

	public void setType(String type) {
		Iterator<Widget> iter = typeHPanel.iterator();

		while (iter.hasNext()) {
			RadioButton rb = (RadioButton) iter.next();
			if (rb.getText().compareTo(type) == 0)
				rb.setValue(true);
		}

	}

	// ----------------------------------------------------
	// ------------------------------------------- Handlers
	// ----------------------------------------------------

	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent event) {
		
		if ( titleTextBox.getText().trim().isEmpty())
			return;
		
		if ( commentTextArea.getText().trim().isEmpty() && 
				Window.confirm("Mensaje vacio \u00BFDesea continuar?") == false)
			return;
		
		Notice notice = new Notice();
		notice.setTitle((titleTextBox.getValue().isEmpty()) ? ""
				: titleTextBox.getValue());
		notice.setStatus(NoticeStatus.OPEN.getValue());
		
		if (priority != null) {
			Tag tagPriority = new Tag();
			tagPriority.setName(priority);
			tagPriority.setType(TagType.OFFICE_PRIORITY.value());
			notice.addTag(tagPriority);
		}			

		if (type != null) {
			Tag tagType = new Tag();
			tagType.setName(type);
			tagType.setType(TagType.OFFICE_TYPE.value());
			notice.addTag(tagType);
			
		}
		
		if (company != null) {
			int recipientId = -1;
			for (Registry registry : registryList) {
				 if ( registry.getName().compareTo(company) == 0)
					 recipientId = registry.getId();
			}
			notice.setCompany(company);
			notice.setSource(String.valueOf(recipientId));
		}

		for ( String name : selectedTags.keySet()) {
			Tag tag = new Tag();
			tag.setName(name);
			tag.setType(TagType.OFFICE_NOTICE.value());
			notice.addTag(tag);
		}

		notice.setBody((commentTextArea.getValue().isEmpty()) ? ""
				: commentTextArea.getValue());
		onCreateNewIssue(notice);
		hide();

	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		hide();
	}

	@UiHandler("newStatusButton")
	void onNewStatusButtonClick(ClickEvent event) {
		/*
		new FilterDialog() {

			{
				setCaption("Nuevo Estado");
				setFilterLabel("Agrega nuevo estado: ");
				setVisibleDateLabel(false);
				setVisibleDateBox(false);
				center();
				show();
			}

			@Override
			protected void onAccept() {

				boolean encontrado = false;

				for (Tag tag : tagsList) {

					if (tag.getName().toUpperCase()
							.compareTo(getName().toUpperCase()) == 0
							&& tag.getType() == TagType.OFFICE_STATUS.value()) {
						encontrado = true;
						break;
					}
				}

				if (encontrado) {
					Window.alert("Nombre del estado ya existente");
				} else {
					Tag tag = new Tag();
					tag.setName(getName());
					tag.setType(TagType.OFFICE_STATUS.value());
					onCreateNewTag(tag);
				}

			}
		};
		*/
	}

	@UiHandler("newPriorityButton")
	void onNewPriorityButtonClick(ClickEvent event) {
		new FilterDialog() {
			{
				setCaption("Nueva Prioridad");
				setFilterLabel("Agrega nueva prioridad: ");
				setVisibleDateLabel(false);
				setVisibleDateBox(false);
				center();
				show();

			}

			@Override
			protected void onAccept() {
				boolean encontrado = false;

				for (Tag tag : tagsList) {

					if (tag.getName().toUpperCase()
							.compareTo(getName().toUpperCase()) == 0
							&& tag.getType() == TagType.PRIORITY.value()) {
						encontrado = true;
						break;
					}
				}

				if (encontrado) {
					Window.alert("Nombre del estado ya existente");
				} else {
					Tag tag = new Tag();
					tag.setName(getName());
					tag.setType(TagType.OFFICE_PRIORITY.value());
					onCreateNewTag(tag);
				}

			}
		};
	}

	@UiHandler("newTypeButton")
	void onNewTypeButtonClick(ClickEvent event) {
		new FilterDialog() {
			{
				setCaption("Nuevo Tipo");
				setFilterLabel("Agrega nuevo tipo: ");
				setVisibleDateLabel(false);
				setVisibleDateBox(false);
				center();
				show();
			}

			@Override
			protected void onAccept() {
				boolean encontrado = false;

				for (Tag tag : tagsList) {

					if (tag.getName().toUpperCase()
							.compareTo(getName().toUpperCase()) == 0
							&& tag.getType() == TagType.OFFICE_TYPE.value()) {
						encontrado = true;
						break;
					}
				}

				if (encontrado) {
					Window.alert("Nombre del estado ya existente");
				} else {
					Tag tag = new Tag();
					tag.setName(getName());
					tag.setType(TagType.OFFICE_TYPE.value());
					onCreateNewTag(tag);
				}
			}
		};

	}

	@UiHandler("newLabelButton")
	void onNewLabelButtonClick(ClickEvent event) {
		new FilterDialog() {
			{
				setCaption("Nueva Etiqueta");
				setFilterLabel("Agrega nueva etiqueta: ");
				setVisibleDateLabel(false);
				setVisibleDateBox(false);
				center();
				show();

			}

			@Override
			protected void onAccept() {
				boolean encontrado = false;

				for (Tag tag : tagsList) {

					if (tag.getName().toUpperCase()
							.compareTo(getName().toUpperCase()) == 0
							&& tag.getType() == TagType.OFFICE_NOTICE.value()) {
						encontrado = true;
						break;
					}
				}

				if (encontrado) {
					Window.alert("Nombre del estado ya existente");
				} else {
					Tag tag = new Tag();
					tag.setName(getName());
					tag.setType(TagType.OFFICE_NOTICE.value());
					onCreateNewTag(tag);
				}
			}
		};
	}
	
	@UiHandler("registrySuggest")
	void onSelectionValue(SelectionEvent<SuggestOracle.Suggestion> event) {
		company = event.getSelectedItem().getReplacementString();
		
	}

	private void onCreateNewTag(Tag tag) {
		for (Listener listener : listeners)
			listener.onCreateNewTag(tag);
	}

	private void onCreateNewIssue(Notice notice) {
		for (Listener listener : listeners)
			listener.onCreateNewIssue(notice);
	}
}

package com.esferalia.aon.gwt.office.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class IssuePanel extends CustomDialog {

	interface Listener {

		void onCreateNewIssue(Notice notice);
	}

	interface IssuePanelUiBinder extends UiBinder<Widget, IssuePanel> {
	}

	private static IssuePanelUiBinder uiBinder = GWT
			.create(IssuePanelUiBinder.class);

	@UiField
	Label dateLabel;
	@UiField
	Label loggedLabel;
//	@UiField
//	HorizontalPanel priorityHPanel;
//	@UiField
//	HorizontalPanel typeHPanel;
//	@UiField	
//	VerticalPanel vPanelTagsContainer;

	@UiField
	TextBox titleTextBox;
	@UiField (provided = true)
	SuggestBox registrySuggest;
//	@UiField
//	TextArea commentTextArea;

	@UiField
	Button acceptButton;
	@UiField
	Button cancelButton;

	private String registry;
	private Date date;
	
	private User user;
	
	private List<Listener> listeners;	
	private List<Registry> registryList;
	private Map<String, String> selectedTags;
	private MultiWordSuggestOracle registries = new MultiWordSuggestOracle();
	
	private DateTimeFormat format = DateTimeFormat.getFormat("dd-MM-yyyy HH:mm");

	public IssuePanel(User user) {
		setCaption("Nueva Incidencia");
		registrySuggest = new SuggestBox(registries);		
		setWidget(uiBinder.createAndBindUi(this));

		setAnimationEnabled(true);
		setGlassEnabled(true);
		
		this.user = user;
		this.selectedTags = new HashMap<String, String>();
		this.listeners = new LinkedList<Listener>();
		
		this.date = new Date();
		dateLabel.setText(format.format(date));
		loggedLabel.setText(user.getName());
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
		this.registryList = registries;
		this.registrySuggest.setEnabled(registries.size() > 0);
		for ( Registry registry : registries ) {			
			this.registries.add(registry.getName());			
		}
	}
	
//	public void setTagList(List<DefaultAonTagIssueSelected> tagList) {
//		this.registrySuggest.setEnabled(true);
//		
//		for (DefaultAonTagIssueSelected tag : tagList)
//			addTag(tag);
//	}
	
	private void addTagSelected(String name) {
		selectedTags.put(name, name);
	}
	
	private void removeTagSelected(String name) {
		selectedTags.remove(name);
	}

//	public void addTag(DefaultAonTagIssueSelected tag) {
//
//		if (tag.getType() == TagType.OFFICE_NOTICE.value()) {
//			CheckBox check = new CheckBox(tag.getName());			
//			check.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
//
//				@Override
//				public void onValueChange(ValueChangeEvent<Boolean> event) {
//					CheckBox cb = (CheckBox) event.getSource();
//					if (cb.getValue() == false)
//						removeTagSelected(cb.getText());
//						
//					else
//						addTagSelected(cb.getText());						
//				}
//			});
//			insertTag(check);
//
//		} else if (tag.getType() == TagType.OFFICE_PRIORITY.value()) {
//			RadioButton radioButton = new RadioButton("PRIORITY",
//					tag.getName());
//			radioButton
//					.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
//
//						@Override
//						public void onValueChange(
//								ValueChangeEvent<Boolean> event) {
//							RadioButton rb = (RadioButton) event.getSource();
//							IssuePanel.this.priority = rb.getText();
//						}
//					});
//			priorityHPanel.add(radioButton);
//		}
//
//		else if (tag.getType() == TagType.OFFICE_TYPE.value()) {
//			RadioButton radioButton = new RadioButton("TYPE", tag.getName());
//			radioButton
//					.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
//
//						@Override
//						public void onValueChange(
//								ValueChangeEvent<Boolean> event) {
//							RadioButton rb = (RadioButton) event.getSource();
//							IssuePanel.this.type = rb.getText();
//						}
//					});
//			typeHPanel.add(radioButton);
//		}
//	}
//	
//	private void insertTag(CheckBox check) {
//		int childs = vPanelTagsContainer.getWidgetCount();
//		
//		if (childs == 0) {
//			vPanelTagsContainer.add(getHorizontalPanel());
//		}
//		
//		HorizontalPanel hPanel = (HorizontalPanel) vPanelTagsContainer.getWidget(
//				vPanelTagsContainer.getWidgetCount() - 1);
//		
//		if ( hPanel.getWidgetCount() < 6)
//			hPanel.add(check);
//		else {
//			vPanelTagsContainer.add(getHorizontalPanel());
//			insertTag(check);
//		}
//
//	}
	
	private HorizontalPanel getHorizontalPanel() {
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(5);
		return hPanel;
	}

	public void setTitle(String title) {
		titleTextBox.setValue(title);
	}

//	public void setBody(String body) {
//		commentTextArea.setValue(body);
//	}

//	public void setPriority(String priority) {
//
//		Iterator<Widget> iter = priorityHPanel.iterator();
//
//		while (iter.hasNext()) {
//			RadioButton rb = (RadioButton) iter.next();
//			if (rb.getText().compareTo(priority) == 0)
//				rb.setValue(true);
//		}
//	}

//	public void setType(String type) {
//		Iterator<Widget> iter = typeHPanel.iterator();
//
//		while (iter.hasNext()) {
//			RadioButton rb = (RadioButton) iter.next();
//			if (rb.getText().compareTo(type) == 0)
//				rb.setValue(true);
//		}
//	}

	// ----------------------------------------------------
	// ------------------------------------------- Handlers
	// ----------------------------------------------------

	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent event) {
		
		if ( titleTextBox.getText().trim().isEmpty())
			return;
		
//		if ( commentTextArea.getText().trim().isEmpty() && 
//				Window.confirm("Mensaje vacio \u00BFDesea continuar?") == false)
//			return;
		
		Notice notice = new Notice();
		notice.setTitle((titleTextBox.getValue().isEmpty()) ? ""
				: titleTextBox.getValue());
		notice.setStatus(NoticeStatus.OPEN.getValue());
		notice.setSender(user);
		notice.setStartDate(date);
		
//		if (priority != null) {
//			Tag priorityTag = getPriorityTag(priority);			
//			notice.addTag(priorityTag);
//		}			

//		if (type != null) {
//			Tag tagType = getTypeTag(type);
//			notice.addTag(tagType);
//		}
		
		if (registry != null) {
			int recipientId = -1;
			for (Registry registry : registryList) {
				 if ( registry.getName().compareTo(this.registry) == 0)
					 recipientId = registry.getId();
			}
			
			if ( recipientId == -1) {
				Window.alert("Remitente no encontrado.");
				return;
			}
				
			notice.setCompany(registry);
			notice.setSource(String.valueOf(recipientId));
		}

//		for ( String name : selectedTags.keySet()) {
//			Tag tag = new Tag();
//			tag.setName(name);
//			tag.setType(TagType.OFFICE_NOTICE.value());
//			notice.addTag(tag);
//		}

//		notice.setBody((commentTextArea.getValue().isEmpty()) ? ""
//				: commentTextArea.getValue());
		onCreateNewIssue(notice);
		hide();

	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		hide();
	}
	
	@UiHandler("registrySuggest")
	void onSelectionValue(SelectionEvent<SuggestOracle.Suggestion> event) {
		this.registry = event.getSelectedItem().getReplacementString();
	}

	private void onCreateNewIssue(Notice notice) {
		for (Listener listener : listeners)
			listener.onCreateNewIssue(notice);
	}
	
//	private Tag getPriorityTag(String priority) {
//		Tag priorityTag = new Tag();		
//		priorityTag.setName(priority);
//		priorityTag.setType(TagType.OFFICE_PRIORITY.value());
//		return priorityTag;
//	}
	
//	private Tag getTypeTag(String type) {
//		Tag typeTag = new Tag();
//		typeTag.setName(type);
//		typeTag.setType(TagType.OFFICE_TYPE.value());
//		return typeTag;
//	}
}

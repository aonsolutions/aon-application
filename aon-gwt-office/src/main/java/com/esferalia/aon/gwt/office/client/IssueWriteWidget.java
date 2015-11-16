package com.esferalia.aon.gwt.office.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.i18n.AonHubMessages;
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.esferalia.aon.gwt.office.client.models.users.JsIdentification;
import com.esferalia.aon.gwt.office.client.models.users.JsUser;
import com.esferalia.aon.gwt.office.client.models.users.JsUserWorkgroups;
import com.esferalia.aon.gwt.office.client.values.LabelValue;
import com.esferalia.aon.gwt.office.client.values.issues.IssueValue;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;

public class IssueWriteWidget extends Composite {

	interface Listener {

		void onCommentButtonClick(IssueValue issueValue);

		void onCreateNewTag(LabelValue tagValue);

	}

	private static IssueWriteWidgetUiBinder uiBinder = GWT
			.create(IssueWriteWidgetUiBinder.class);

	interface IssueWriteWidgetUiBinder extends
			UiBinder<Widget, IssueWriteWidget> {
	}

	private static final String PRIORITY_GROUP = "priorityGroup";
	private final static String TYPE_NOTICE_GROUP = "typeNotice";

	@UiField
	TabPanel tabPanel;

	@UiField
	HorizontalPanel hPanel;
	@UiField
	HorizontalPanel hTypePanel;

	@UiField
	TextArea commentTextArea;

	@UiField
	TextBox companyTextBox;
	@UiField
	TextBox remiteTextBox;
	@UiField
	TextBox phoneTextBox;
	@UiField
	TextBox titleTextBox;

	@UiField(provided = true)
	SuggestBox loginSuggestBox;
	@UiField(provided = true)
	SuggestBox recipientSuggestBox;

	@UiField
	Button commentButton;
	@UiField
	Button addLabelButton;

	@UiField
	Tree priorityTree;
	@UiField
	Tree labelsTree;

	private String noticeType;
	private String priority;
	private String status = "Open";	
	private Integer recipientId;
	private Integer workgroup;

	private MultiWordSuggestOracle logins = new MultiWordSuggestOracle();
	private MultiWordSuggestOracle recipients = new MultiWordSuggestOracle();

	private List<Listener> listeners;

	private Map<String, String> labels;
	
	private JsArray<JsIdentification> idents;

	public IssueWriteWidget(JsUserWorkgroups usersWorkgroups) {
		this.loginSuggestBox = new SuggestBox(logins);
		this.recipientSuggestBox = new SuggestBox(recipients);

		initWidget(uiBinder.createAndBindUi(this));

		tabPanel.selectTab(0);

		this.labelsTree.addTextItem("Etiquetas");
		this.priorityTree.addTextItem("Prioridad");
		this.listeners = new LinkedList<Listener>();
		this.labels = new HashMap<String, String>();
		
		this.remiteTextBox.setValue(usersWorkgroups.getUser());
		this.companyTextBox.setValue(usersWorkgroups.getEnterprise());
		
		this.idents = usersWorkgroups.getIdentificacions();
		
		insertLoggins(idents);
		insertPriorityTags(usersWorkgroups.getPriorities());
		insertLabelTags(usersWorkgroups.getLabels());
		insertRecipientUsers(usersWorkgroups.getUsers());

		loadRadioButtons();
	}

	private void insertPriorityTags(JsArray<JsLabel> jsPriorities) {

		for (int z = 0; z < jsPriorities.length(); z++)
			addNewRadioButton(jsPriorities.get(z).getName());
	}

	private void insertLabelTags(JsArray<JsLabel> jsLabelTags) {

		for (int u = 0; u < jsLabelTags.length(); u++)
			addNewCheckBox(jsLabelTags.get(u).getName());
	}
	
	private void insertLoggins (JsArray<JsIdentification> jsIdents) {
		
		for (int x = 0 ; x < jsIdents.length() ; x++) 
			logins.add(jsIdents.get(x).getValue());
	}
	
	private void insertRecipientUsers (JsArray<JsUser> jsRecipients) {
		
		for ( int j = 0; j < jsRecipients.length() ; j++)
			recipients.add(jsRecipients.get(j).getId() + " - " + jsRecipients.get(j).getName());
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	@UiHandler("addPriorityButton")
	void onAddPriorityButtonClick(ClickEvent event) {

		final TextBox textBox = new TextBox();
		priorityTree.add(textBox);
		textBox.setStyleName(AON.AON_CSS.aonInputText());
		textBox.selectAll();
		textBox.setFocus(true);
		textBox.setWidth("85px");

		textBox.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				int keyCode = event.getNativeKeyCode();
				final String name = textBox.getText();

				if ((keyCode == KeyCodes.KEY_ENTER || keyCode == KeyCodes.KEY_TAB)
						&& !name.isEmpty()) {
					removeInTree(priorityTree, textBox);
					addNewRadioButton(name.toUpperCase());

					LabelValue priorityValue = new LabelValue();
					priorityValue.setName(name.toUpperCase());
					priorityValue.setColor("");
					priorityValue.setType("priority");
					addCreateNewTag(priorityValue);
				} else if (keyCode == KeyCodes.KEY_ESCAPE)
					removeInTree(priorityTree, textBox);
			}
		});
	}

	@UiHandler("addLabelButton")
	void onAddLabelButtonClick(ClickEvent event) {

		final TextBox textBox = new TextBox();
		labelsTree.add(textBox);
		textBox.setStyleName(AON.AON_CSS.aonInputText());
		textBox.selectAll();
		textBox.setFocus(true);
		textBox.setWidth("85px");

		textBox.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				int keyCode = event.getNativeKeyCode();
				final String name = textBox.getText();

				if ((keyCode == KeyCodes.KEY_ENTER || keyCode == KeyCodes.KEY_TAB)
						&& !name.isEmpty()) {
					removeInTree(labelsTree, textBox);
					addNewCheckBox(name.toUpperCase());

					LabelValue tagValue = new LabelValue();
					tagValue.setName(name.toUpperCase());
					tagValue.setColor("");
					tagValue.setType("label");
					addCreateNewTag(tagValue);
				} else if (keyCode == KeyCodes.KEY_ESCAPE)
					removeInTree(labelsTree, textBox);
			}
		});
	}

	@UiHandler("commentButton")
	void onCommentButtonClick(ClickEvent event) {

		if (commentTextArea.getText().isEmpty()) {
			commentTextArea.setFocus(true);
			return;

		} else {
			IssueValue issueValue = new IssueValue();

			issueValue.setTitle((titleTextBox.getText().isEmpty()) ? ""
					: titleTextBox.getText());
			issueValue.setPhone((phoneTextBox.getText().isEmpty()) ? ""
					: phoneTextBox.getText());
			issueValue.setCompany((companyTextBox.getText().isEmpty()) ? ""
					: companyTextBox.getText());	
			
			issueValue.setSource( loginSuggestBox.getText() ); //Identificacion varchar
			issueValue.setBody(commentTextArea.getText());
			issueValue.setLabels(getLabels().split(","));
			issueValue.setType(noticeType);
			issueValue.setPriority(priority);
			issueValue.setState(status);

			if (recipientId != null)
				issueValue.setRecipient(recipientId);

			addCommentButtonClickListener(issueValue);
		}
	}

	@UiHandler("recipientSuggestBox")
	void onSelectedSRecipient(SelectionEvent<SuggestOracle.Suggestion> event) {

		String selected = event.getSelectedItem().getReplacementString();
		String[] cadena = selected.split(" - ");

		recipientId = Integer.parseInt(cadena[0]);
	}

	private void addCommentButtonClickListener(IssueValue issueValue) {

		for (Listener listener : listeners)
			listener.onCommentButtonClick(issueValue);
	}

	private void addCreateNewTag(LabelValue tagValue) {

		for (Listener listener : listeners)
			listener.onCreateNewTag(tagValue);
	}

	private void loadRadioButtons() {

		RadioButton ticket = new RadioButton(TYPE_NOTICE_GROUP, "Ticket");
		ticket.setValue(true);
		ticket.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setNoticeType((RadioButton) event.getSource());
			}
		});
		noticeType = ticket.getText();

		RadioButton aviso = new RadioButton(TYPE_NOTICE_GROUP, "Aviso");
		aviso.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setNoticeType((RadioButton) event.getSource());
			}
		});
		aviso.setEnabled(false);

		RadioButton nota = new RadioButton(TYPE_NOTICE_GROUP, "Nota");
		nota.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setNoticeType((RadioButton) event.getSource());
			}
		});
		nota.setEnabled(false);

		RadioButton comment = new RadioButton(TYPE_NOTICE_GROUP, "Comment");
		comment.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setNoticeType((RadioButton) event.getSource());
			}
		});
		comment.setEnabled(false);

		insertWidgets(hTypePanel, ticket, aviso, nota, comment);
	}

	private String getLabels() {
		
		Iterator<String> iter = labels.keySet().iterator();
		StringBuffer buffer = new StringBuffer();	
		
		while (iter.hasNext()) {
			String id = iter.next();
			buffer.append(id);
			if(iter.hasNext())
				buffer.append(',');
		}

		return buffer.toString();
	}

	private void insertWidgets(Panel container, Widget... widgets) {

		for (Widget widget : widgets)
			container.add(widget);
	}

	private void evalCheckBox(CheckBox checkBox) {
		if (checkBox.getValue())
			labels.put(checkBox.getText(), checkBox.getText());
		else
			labels.remove(checkBox.getText());
	}

	private void setPriority(String priority) {
		this.priority = priority;
	}

	private void setNoticeType(RadioButton radioButton) {
		this.noticeType = radioButton.getText();
	}

	private void removeInTree(Tree tree, Widget widget) {
		tree.remove(widget);
	}

	private void addNewRadioButton(final String name) {
		RadioButton radioButton = new RadioButton(PRIORITY_GROUP, name);
		radioButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setPriority(name);
			}
		});
		priorityTree.add(radioButton);
	}

	private void addNewCheckBox(final String name) {

		CheckBox checkBox = new CheckBox(name);
		checkBox.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				evalCheckBox((CheckBox) event.getSource());

			}
		});

		labelsTree.add(checkBox);		
	}
}

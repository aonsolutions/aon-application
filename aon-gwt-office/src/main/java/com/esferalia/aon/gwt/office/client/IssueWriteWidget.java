package com.esferalia.aon.gwt.office.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.office.client.values.issues.IssueValue;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class IssueWriteWidget extends Composite {

	interface Listener {

		void onCommentButtonClick(IssueValue issueValue);

	}

	private static IssueWriteWidgetUiBinder uiBinder = GWT
			.create(IssueWriteWidgetUiBinder.class);

	interface IssueWriteWidgetUiBinder extends
			UiBinder<Widget, IssueWriteWidget> {
	}
	
	private static final String TYPE_NOTICE_GROUP = "typeNotice";
	private static final String PRIORITY_GROUP = "priorityGroup";

	@UiField
	TabPanel tabPanel;
	@UiField (provided = true)
	SuggestBox registrySuggesBox;
	@UiField
	TextArea commentTextArea;
	@UiField
	TextBox title;
	@UiField
	HorizontalPanel hPanel;
	@UiField
	VerticalPanel vTypePanel;
	@UiField
	VerticalPanel vPriorityPanel;
	@UiField
	VerticalPanel vLabelsPanel;
	@UiField
	Button commentButton;

	private String noticeType;
	private String priority;
	private String status = "Open";
	
	private MultiWordSuggestOracle names = new MultiWordSuggestOracle();
	private List<Listener> listeners;
	private Map<Integer, String> registryMap;
	private Map<String, String> labels;

	public IssueWriteWidget(Map<Integer, String> registryMap) {
		this.registrySuggesBox = new SuggestBox(names);
		initWidget(uiBinder.createAndBindUi(this));

		tabPanel.selectTab(0);
		this.registryMap = registryMap;
		
		for (Integer key : registryMap.keySet())
			names.add(registryMap.get(key));
		
		this.listeners = new LinkedList<Listener>();
		this.labels = new HashMap<String, String>();
		loadRadioButtons();
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	@UiHandler("commentButton")
	void onCommentButtonClick(ClickEvent event) {

		if (commentTextArea.getText().isEmpty() == false) {
			IssueValue issueValue = new IssueValue();
			issueValue.setTitle(title.getText());
			issueValue.setBody(commentTextArea.getText());
			
			Iterator<String> iterator = labels.keySet().iterator();
			StringBuffer buffer = new StringBuffer();
			
			while(iterator.hasNext()) {
				String label = iterator.next();
				buffer.append(label);
				
				if (iterator.hasNext())
					buffer.append(',');
			}
			issueValue.setLabels(buffer.toString().split(","));
			issueValue.setType(noticeType);
			issueValue.setPriority(priority);
			issueValue.setState(status);
			addCommentButtonClickListener(issueValue);
		}
	}

	private void addCommentButtonClickListener(IssueValue issueValue) {

		for (Listener listener : listeners)
			listener.onCommentButtonClick(issueValue);
	}

	private void loadRadioButtons() {
		RadioButton ticket = new RadioButton(TYPE_NOTICE_GROUP, "Ticket");
		ticket.setValue(true);
		noticeType = ticket.getText();
		
		RadioButton aviso = new RadioButton(TYPE_NOTICE_GROUP, "Aviso");
		aviso.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				setNoticeType((RadioButton) event.getSource());
			}
		});
		
		RadioButton nota = new RadioButton(TYPE_NOTICE_GROUP, "Nota");
		nota.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				setNoticeType((RadioButton) event.getSource());
			}
		});
		
		RadioButton comment = new RadioButton(TYPE_NOTICE_GROUP, "Comment");
		comment.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				setNoticeType((RadioButton) event.getSource());
			}
		});
		
		vTypePanel.add(ticket);
		vTypePanel.add(aviso);
		vTypePanel.add(nota);
		vTypePanel.add(comment);
		
		RadioButton noneRadioButton = new RadioButton(PRIORITY_GROUP, "None");
		noneRadioButton.setValue(true);
		priority = noneRadioButton.getText();
		
		noneRadioButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				setPriority((RadioButton) event.getSource());
			}
		});
		
		RadioButton lowRadioButton = new RadioButton(PRIORITY_GROUP, "Low");
		lowRadioButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				setPriority((RadioButton) event.getSource());
			}
		});
		
		RadioButton normalRadioButton = new RadioButton(PRIORITY_GROUP, "Normal");
		normalRadioButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				setPriority((RadioButton) event.getSource());
			}
		});
		
		RadioButton highRadioButton = new RadioButton(PRIORITY_GROUP, "High");
		highRadioButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				setPriority((RadioButton) event.getSource());
			}
		});
		
		vPriorityPanel.add(noneRadioButton);
		vPriorityPanel.add(lowRadioButton);
		vPriorityPanel.add(normalRadioButton);
		vPriorityPanel.add(highRadioButton);

		CheckBox bug = new CheckBox("BUG");		
		bug.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				evalCheckBox((CheckBox) event.getSource());
			}
		});

		CheckBox fiscal = new CheckBox("FISCAL");
		fiscal.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				evalCheckBox((CheckBox) event.getSource());
			}
		});

		CheckBox laboral = new CheckBox("LABORAL");
		laboral.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				evalCheckBox((CheckBox) event.getSource());
			}
		});
		CheckBox soporte = new CheckBox("SOPORTE");
		soporte.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				evalCheckBox((CheckBox) event.getSource());
			}
		});
		CheckBox gestion = new CheckBox("GESTION");
		gestion.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				evalCheckBox((CheckBox) event.getSource());
			}
		});
		CheckBox duplicated = new CheckBox("DUPLICATED");
		duplicated.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				evalCheckBox((CheckBox) event.getSource());
			}
		});

		vLabelsPanel.add(bug);
		vLabelsPanel.add(fiscal);
		vLabelsPanel.add(laboral);
		vLabelsPanel.add(soporte);
		vLabelsPanel.add(gestion);
		vLabelsPanel.add(duplicated);

	}

	private void evalCheckBox(CheckBox object) {
		if (object.getValue())
			labels.put(object.getText(), object.getText());
		else
			labels.remove(object.getText());
	}
	
	private void setPriority(RadioButton radioButton) {
		this.priority = radioButton.getText();
	}
	
	private void setNoticeType (RadioButton radioButton) {
		this.noticeType = radioButton.getText();
	}
}

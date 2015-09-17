package com.esferalia.aon.gwt.office.client;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class LeftButtonsMenuBar extends Composite {

	interface LeftMenuBarListener {

		void onNewIssueClickEvent(ClickEvent event);

		void onShowOpenIssuesClickEvent(ClickEvent event);

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

	interface LeftButtonsMenuBarUiBinder extends
			UiBinder<Widget, LeftButtonsMenuBar> {
	}

	@UiField
	Button newIssue;
	@UiField
	VerticalPanel labelsVPanel;

	// Buttons Bar
	@UiField
	Button openIssues;
	@UiField
	Button closedIssues;
	@UiField
	Button allIssues;
	@UiField
	Button deletedIssues;
	@UiField
	Button questionIssues;
	@UiField
	Button errorIssues;
	@UiField
	Button faqsIssues;
	// **************

	private Button selectedButton;
	private List<LeftMenuBarListener> listeners;

	public LeftButtonsMenuBar() {
		initWidget(uiBinder.createAndBindUi(this));

		this.selectedButton = new Button();
		this.listeners = new LinkedList<LeftMenuBarListener>();
		
		setFontBoldColor(openIssues);

	}

	// ******************************************************************
	// ************************* UI HANDLERS ****************************
	// ******************************************************************

	@UiHandler("newIssue")
	void onNewIssueClick(ClickEvent event) {		
		for (LeftMenuBarListener listener : listeners)
			listener.onNewIssueClickEvent(event);
	}

	@UiHandler("openIssues")
	void onOpenIssuesClick(ClickEvent event) {
		setFontBoldColor(openIssues);
		for (LeftMenuBarListener listener : listeners)
			listener.onShowOpenIssuesClickEvent(event);
	}

	@UiHandler("closedIssues")
	void onClosedIssuesClick(ClickEvent event) {
		setFontBoldColor(closedIssues);
		for (LeftMenuBarListener listener : listeners)
			listener.onShowClosedIssuesClickEvent(event);
	}

	@UiHandler("allIssues")
	void onAllIssuesClick(ClickEvent event) {
		setFontBoldColor(allIssues);
		for (LeftMenuBarListener listener : listeners)
			listener.onShowAllIssuesClickEvent(event);
	}

	@UiHandler("deletedIssues")
	void onDeletedIssuesClick(ClickEvent event) {
		setFontBoldColor(deletedIssues);
		for (LeftMenuBarListener listener : listeners)
			listener.onShowDeletedIssuesClickEvent(event);
	}

	@UiHandler("questionIssues")
	void onQuestionIssuesClick(ClickEvent event) {
		setFontBoldColor(questionIssues);
		for (LeftMenuBarListener listener : listeners)
			listener.onShowQuestionIssuesClickEvent(event);
	}

	@UiHandler("errorIssues")
	void onErrorIssuesClick(ClickEvent event) {
		setFontBoldColor(errorIssues);
		for (LeftMenuBarListener listener : listeners)
			listener.onShowErrorIssuesClickEvent(event);
	}

	@UiHandler("faqsIssues")
	void onFaqsIssuesClick(ClickEvent event) {
		setFontBoldColor(faqsIssues);
		for (LeftMenuBarListener listener : listeners)
			listener.onShowFaqsIssuesClickEvent(event);
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
	
	public void addLabelIssueButton(int row, JsLabel label) {
		addLabel(row, label);
	}
	
	public void changeOpenIssuesText(int number) {
		openIssues.setText(openIssues.getText() + " (" + number +")");
	}
	
	public boolean isSelected(Button button) {
		return button == selectedButton;
	}

	// ******************************************************************
	// ********************** PRIVATE METHODS ***************************
	// ******************************************************************

	private void addLabel(int row, JsLabel label) {

		final Button labelButton = new Button();
		
		labelButton.setText(label.getName());
		labelButton.setTitle(label.getName() + "-" + label.getUrl());
		labelButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		labelButton.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		if (!isWhite(label.getColor()))
			labelButton.getElement().getStyle()
					.setColor("#" + label.getColor());

		setLabelClickEvent(labelButton);
		labelsVPanel.add(labelButton);

	}
	
	private boolean isWhite(String color) {
		return color == "ffffff";
	}
	
	private void setFontBoldColor (Button button) {
		if (button != selectedButton) {
			selectedButton.getElement().getStyle().clearFontWeight();
			button.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			selectedButton = button;
		}
	}
	
	private void setLabelClickEvent (final Button button) {
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				for (LeftMenuBarListener listener : listeners)
					listener.onLabelIssueClickEvent(button);
			}
		});
	}
	// ******************************************************************
	// ******************************************************************

}

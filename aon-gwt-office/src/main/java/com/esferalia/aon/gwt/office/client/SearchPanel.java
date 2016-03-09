package com.esferalia.aon.gwt.office.client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SearchPanel extends Composite implements KeyUpHandler {

	enum DateRange {
		ALL("all"), TODAY("today"), YESTERDAY("yesterday"), THIS_WEEK(
				"thisWeek"), THIS_MONTH("thisMonth"), FIVETEEN_DAYS_AGO(
						"fiveteenDaysAgo"), THIRTY_DAYS_AGO(
								"fiveteenDaysAgo"), THIS_YEAR("thisYear");

		private DateRange(String range) {
		}
	}

	private static SearchPanelUiBinder uiBinder = GWT
			.create(SearchPanelUiBinder.class);

	interface Listener {

		void onOpenIssuesRbSelected(ValueChangeEvent<Boolean> event);

		void onClosedIssuesRbSelected(ValueChangeEvent<Boolean> event);

		void onAllIssuesRbSelected(ValueChangeEvent<Boolean> event);

		void onChangeEventListBox(Date criteria);

		void onSuggestBoxChangeValue(String sender);

		void onAdvancedSearchClick(ClickEvent event);

		void onHideSearchPanel(ClickEvent event);
		
		void onSearchButtonClick(List<Tag> tags, String text);
		
		void onClearSearchClickEvent(ClickEvent event);
	}

	interface SearchPanelUiBinder extends UiBinder<Widget, SearchPanel> {
	}

	@UiField
	ListBox fromListBox;
	@UiField(provided = true)
	SuggestBox registrySuggest;

	@UiField
	RadioButton openIssuesRb;
	@UiField
	RadioButton closedIssuesRb;
	@UiField
	RadioButton allIssuesRb;

	@UiField
	Button advanceSearch;
	@UiField
	Button hideSearchPanel;
	@UiField
	Button typeButton;
	@UiField
	Button priorityButton;
	@UiField
	Button searchButton;
	@UiField
	Button clearSearchButton;

	@UiField
	TextBox textTextBox;

	@UiField
	HorizontalPanel labelsHP;
	@UiField
	VerticalPanel filterHPanel;

	private List<Listener> listeners;
	private List<DefaultAonTagIssueSelected> typeTagList;
	private List<DefaultAonTagIssueSelected> priorityTagList;
	
	private List<Tag> drashTagList;
	
	private MultiWordSuggestOracle registries = new MultiWordSuggestOracle();
	
	private VerticalPanel typeVPanel;
	private VerticalPanel priorityVPanel;

	public SearchPanel() {
		this.registrySuggest = new SuggestBox(registries);		
		initWidget(uiBinder.createAndBindUi(this));

		this.listeners = new LinkedList<Listener>();
		this.typeTagList = new LinkedList<DefaultAonTagIssueSelected>();
		this.priorityTagList = new LinkedList<DefaultAonTagIssueSelected>();
		this.drashTagList = new LinkedList<Tag>();
		this.registrySuggest.getValueBox().addKeyUpHandler(this);

		fromListBox.addItem(" ", DateRange.ALL.name());
		fromListBox.addItem("Hoy", DateRange.TODAY.name());
		fromListBox.addItem("Ayer", DateRange.YESTERDAY.name());
		fromListBox.addItem("Esta semana", DateRange.THIS_WEEK.name());
		fromListBox.addItem("Este mes", DateRange.THIS_MONTH.name());
		fromListBox.addItem("15 d\u00EDas", DateRange.FIVETEEN_DAYS_AGO.name());
		fromListBox.addItem("30 d\u00EDas", DateRange.THIRTY_DAYS_AGO.name());
		fromListBox.addItem("Este a\u00F1o", DateRange.THIS_YEAR.name());
		fromListBox.setSelectedIndex(0);
	}

	public void addRegistry(Registry registry) {
		registries.add(registry.getName());		
	}
	
	public void addTagList(List<DefaultAonTagIssueSelected> list) {
		
		for (DefaultAonTagIssueSelected tag : list) {
			
			if (tag.getType() == TagType.OFFICE_TYPE.value())
				typeTagList.add(tag);
			else if (tag.getType() == TagType.OFFICE_PRIORITY.value())
				priorityTagList.add(tag);
		}
		
		initTypeButton(typeTagList);
		initPriorityButton(priorityTagList);
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	@UiHandler("fromListBox")
	void onChangeEventListBox(ChangeEvent event) {

		String name = fromListBox.getSelectedValue();
		Date criteria = null;
		if (name.compareTo(DateRange.ALL.name()) == 0)
			criteria = null;
		else if (name.compareTo(DateRange.TODAY.name()) == 0)
			criteria = new Date();
		else if (name.compareTo(DateRange.YESTERDAY.name()) == 0)
			criteria = DateUtils.getPrevDay(new Date());
		else if (name.compareTo(DateRange.THIS_WEEK.name()) == 0)
			criteria = DateUtils.getFirstDayOfWorkWeek(new Date());
		else if (name.compareTo(DateRange.THIS_MONTH.name()) == 0)
			criteria = DateUtils.getFirstDayOfMonth();
		else if (name.compareTo(DateRange.FIVETEEN_DAYS_AGO.name()) == 0)
			criteria = DateUtils.deleteDays2Date(new Date(), 15);
		else if (name.compareTo(DateRange.THIRTY_DAYS_AGO.name()) == 0)
			criteria = DateUtils.deleteDays2Date(new Date(), 30);
		else if (name.compareTo(DateRange.THIS_YEAR.name()) == 0)
			criteria = DateUtils.getFirstDayOfYear();

		for (Listener listener : listeners)
			listener.onChangeEventListBox(criteria);
	}

	@UiHandler("openIssuesRb")
	void onSelectedOpenIssuesRb(ValueChangeEvent<Boolean> event) {

		if (event.getValue()) {
			closedIssuesRb.removeStyleName(AON.AON_BOLD);
			allIssuesRb.removeStyleName(AON.AON_BOLD);
			openIssuesRb.setStyleName(AON.AON_BOLD);

			for (Listener listener : listeners)
				listener.onOpenIssuesRbSelected(event);
		}
	}

	@UiHandler("closedIssuesRb")
	void onSelectedClosedIssuesRb(ValueChangeEvent<Boolean> event) {

		if (event.getValue()) {
			openIssuesRb.removeStyleName(AON.AON_BOLD);
			allIssuesRb.removeStyleName(AON.AON_BOLD);
			closedIssuesRb.setStyleName(AON.AON_BOLD);

			for (Listener listener : listeners)
				listener.onClosedIssuesRbSelected(event);
		}
	}

	@UiHandler("allIssuesRb")
	void onSelectedAllIssuesRb(ValueChangeEvent<Boolean> event) {

		if (event.getValue()) {
			openIssuesRb.removeStyleName(AON.AON_BOLD);
			closedIssuesRb.removeStyleName(AON.AON_BOLD);
			allIssuesRb.setStyleName(AON.AON_BOLD);

			for (Listener listener : listeners)
				listener.onAllIssuesRbSelected(event);
		}
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		if (registrySuggest.getValueBox().getValue().trim().length() == 0) {
			for (Listener listener : listeners)
				listener.onSuggestBoxChangeValue(null);
		}
	}

	@UiHandler("registrySuggest")
	void onRegistrySelectionValue(
			SelectionEvent<SuggestOracle.Suggestion> event) {
		String selected = event.getSelectedItem().getReplacementString();
		for (Listener listener : listeners)
			listener.onSuggestBoxChangeValue(selected);
	}

	@UiHandler("advanceSearch")
	void onAdvanceSearchClickEvent(ClickEvent event) {
		this.advanceSearch.setVisible(false);
		this.hideSearchPanel.setVisible(true);
		this.filterHPanel.setVisible(true);

		for (Listener listener : listeners)
			listener.onAdvancedSearchClick(event);
	}
	
	@UiHandler("clearSearchButton")
	void onClearSearchBuuttonClick (ClickEvent event) {
		
		for (int x = 0; x < typeVPanel.getWidgetCount(); x++) {
			CheckBox cb = (CheckBox) typeVPanel.getWidget(x);
			cb.removeStyleName(AON.AON_BOLD);
			cb.setValue(false);
		}
		
		for (int x = 0; x < priorityVPanel.getWidgetCount(); x++) {
			CheckBox cb = (CheckBox) priorityVPanel.getWidget(x);
			cb.removeStyleName(AON.AON_BOLD);
			cb.setValue(false);
		}
		
		labelsHP.clear();
		drashTagList.clear();
		registrySuggest.getValueBox().setValue("");
		textTextBox.setText("");
		
		for (Listener listener : listeners)
			listener.onClearSearchClickEvent(event);
	}

	@UiHandler("hideSearchPanel")
	void onHideSearchPanel(ClickEvent event) {
		this.advanceSearch.setVisible(true);
		this.hideSearchPanel.setVisible(false);
		this.filterHPanel.setVisible(false);

		for (Listener listener : listeners)
			listener.onHideSearchPanel(event);
	}
	
	@UiHandler("searchButton")
	void onSearchButtonClick(ClickEvent event) {
		
		for (Listener listener : listeners)
			listener.onSearchButtonClick(drashTagList, textTextBox.getText());
	}

	public boolean openIsSelected() {
		return openIssuesRb.getValue();
	}

	public boolean closedIsSelected() {
		return closedIssuesRb.getValue();
	}

	public boolean allIsSelected() {
		return allIssuesRb.getValue();
	}

	private void initTypeButton(
			final List<DefaultAonTagIssueSelected> typeTags) {
		
		this.typeButton.addClickHandler(new ClickHandler() {
			
			private PopupPanel popup = new PopupPanel(true);
			
			{
				SearchPanel.this.typeVPanel = new VerticalPanel();
				
				for (final DefaultAonTagIssueSelected tag : typeTags) {
					
					final CheckBox cb = new CheckBox(tag.getName());
					cb.setName(String.valueOf(tag.getId()));
					cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
						
						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							
							if (event.getValue()) {
								cb.setStyleName(AON.AON_BOLD);
								addLabel2HPanel(tag.getName());
								addTag2List(tag);
							}
							else {
								cb.removeStyleName(AON.AON_BOLD);
								removeLabelFromHPanel(tag.getName());
								removeTagFromList(Integer.parseInt(cb.getName()));
							}
						}
					});
					SearchPanel.this.typeVPanel.add(cb);
				}
				popup.add(SearchPanel.this.typeVPanel);
			}
			
			@Override
			public void onClick(ClickEvent event) {
				int left = typeButton.getAbsoluteLeft();
				int top = typeButton.getAbsoluteTop() 
						+ typeButton.getOffsetHeight();
				
				popup.setPopupPosition(left, top);
				popup.show();
			}
		});
	}
	
	private void initPriorityButton(
			final List<DefaultAonTagIssueSelected> priorityTags) {
		
		this.priorityButton.addClickHandler(new ClickHandler() {
			
			private PopupPanel popup = new PopupPanel(true);
			
			{
				SearchPanel.this.priorityVPanel = new VerticalPanel();
				
				for (final DefaultAonTagIssueSelected tag : priorityTags) {
					
					final CheckBox cb = new CheckBox(tag.getName());
					cb.setName(String.valueOf(tag.getId()));
					cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
						
						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {							
							if (event.getValue()) {
								cb.setStyleName(AON.AON_BOLD);
								addLabel2HPanel(tag.getName());
								addTag2List(tag);
							}
							else {
								cb.removeStyleName(AON.AON_BOLD);
								removeLabelFromHPanel(tag.getName());
								removeTagFromList(Integer.parseInt(cb.getName()));
							}
						}
					});
					SearchPanel.this.priorityVPanel.add(cb);
				}
				popup.add(SearchPanel.this.priorityVPanel);
			}
			
			@Override
			public void onClick(ClickEvent event) {
				int left = priorityButton.getAbsoluteLeft();
				int top = priorityButton.getAbsoluteTop() 
						+ priorityButton.getOffsetHeight();
				
				popup.setPopupPosition(left, top);
				popup.show();
			}
		});
	}

	private void addTag2List(DefaultAonTagIssueSelected tag) {
		Tag aux = new Tag();
		aux.setId(tag.getId());
		aux.setName(tag.getName());
		aux.setType(tag.getType());
		drashTagList.add(aux);
	}
	
	private void removeTagFromList(Integer id) {
		
		for (Tag tag : drashTagList) {
			if (tag.getId() == id) {
				drashTagList.remove(tag);
				break;
			}
		}
	}
	
	private void addLabel2HPanel(String name) {
		Label label = new Label();
		label.setText(name);
		label.setStyleName(AON.AON_GREEN);
		labelsHP.add(label);
	}
	
	private void removeLabelFromHPanel(String name) {
		
		for (int x = 0; x < labelsHP.getWidgetCount(); x++) {
			Label label = (Label) labelsHP.getWidget(x);
			if (label.getText().compareTo(name) == 0) {
				labelsHP.remove(x);
				break;
			}
		}
	}
} 

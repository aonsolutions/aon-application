package com.esferalia.aon.gwt.office.client;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SearchPanel extends Composite {

	enum DateRange {
		ALL("all"), 
		TODAY("today"), 
		YESTERDAY("yesterday"), 
		THIS_WEEK("thisWeek"), 
		THIS_MONTH("thisMonth"), 
		FIVETEEN_DAYS_AGO("fiveteenDaysAgo"), 
		THIRTY_DAYS_AGO("fiveteenDaysAgo"), 
		THIS_YEAR("thisYear");

		private DateRange(String range) {
		}
	}
	
	public interface Listener {
	
		void onSelectCompany(String company);
		
		void onSelectSubject(String subject);
		
		void onCloseTagPanel(List<Tag> drashTagList);
		
		void onSelectOwner(List<User> drashUserList);
		
		void onSelectFrom(Date from);

	}

	private static SearchPanelUiBinder uiBinder = GWT
			.create(SearchPanelUiBinder.class);

	interface SearchPanelUiBinder extends UiBinder<Widget, SearchPanel> {
	}

	@UiField
	ListBox fromListBox;
	@UiField(provided = true)
	SuggestBox registrySuggest;

	@UiField
	Button typeButton;
	@UiField
	Button priorityButton;
	@UiField
	Button noticeTagButton;
	@UiField
	Button ownerButton;
	
	@UiField
	TextBox subjectTextBox;
	
	private List<Listener> listeners;
	
	private Date criteria;
	private String sender;
	
	private List<User> users;
	private List<DefaultAonTagIssueSelected> typeTagList;
	private List<DefaultAonTagIssueSelected> priorityTagList;
	private List<DefaultAonTagIssueSelected> noticeTagList;
	
	private List<Tag> drashTagList;
	private List<User> drashUserList;

	private MultiWordSuggestOracle registries = new MultiWordSuggestOracle();

	private VerticalPanel typeVPanel;
	private VerticalPanel priorityVPanel;
	private VerticalPanel noticesVPanel;
	private VerticalPanel ownerVPanel;

	public SearchPanel() {
		this.registrySuggest = new SuggestBox(registries);

		initWidget(uiBinder.createAndBindUi(this));
		
		this.listeners = new LinkedList<Listener>();
		this.typeTagList = new LinkedList<DefaultAonTagIssueSelected>();
		this.priorityTagList = new LinkedList<DefaultAonTagIssueSelected>();
		this.noticeTagList = new LinkedList<DefaultAonTagIssueSelected>();
		this.drashTagList = new LinkedList<Tag>();
		this.drashUserList = new LinkedList<User>();

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
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	// ---------------------------------------------------------
	// ------------------------------------------- UiHandlers --
	// ---------------------------------------------------------

	@UiHandler("registrySuggest")
	void onRegistrySelectionValue(
			SelectionEvent<SuggestOracle.Suggestion> event) {
		String company = event.getSelectedItem().getReplacementString();
		
		for (Listener listener : listeners)
			listener.onSelectCompany(company);
	}
	
	// ---------------------------------------------------------
	
	public void addRegistry(Registry registry) {
		registries.add(registry.getName());
	}
	
	public void addUserList(List<User> users) {
		this.users = users;
		initUserButton(users);
	}

	public void addTagList(List<DefaultAonTagIssueSelected> list) {

		for (DefaultAonTagIssueSelected tag : list) {

			if (tag.getType() == TagType.OFFICE_TYPE.value())
				typeTagList.add(tag);
			else if (tag.getType() == TagType.OFFICE_PRIORITY.value())
				priorityTagList.add(tag);
			else if (tag.getType() == TagType.OFFICE_NOTICE.value())
				noticeTagList.add(tag);
		}

		initTypeButton(typeTagList);
		initPriorityButton(priorityTagList);
		initNoticeButton(noticeTagList);
	}
	
	public void cleanFilterPanels() {
		
		for (int x = 0; x < typeVPanel.getWidgetCount(); x++) {
			CheckBox cb = (CheckBox) typeVPanel.getWidget(x);
			cb.setValue(false);
			cb.removeStyleName(AON.AON_BOLD);
		}

		for (int x = 0; x < priorityVPanel.getWidgetCount(); x++) {
			CheckBox cb = (CheckBox) priorityVPanel.getWidget(x);
			cb.setValue(false);
			cb.removeStyleName(AON.AON_BOLD);
		}

		for (int x = 0; x < noticesVPanel.getWidgetCount(); x++) {
			CheckBox cb = (CheckBox) noticesVPanel.getWidget(x);
			cb.setValue(false);
			cb.removeStyleName(AON.AON_BOLD);
		}
		
		for (int x = 0; x < ownerVPanel.getWidgetCount(); x++) {
			CheckBox cb = (CheckBox) ownerVPanel.getWidget(x);
			cb.setValue(false);
			cb.removeStyleName(AON.AON_BOLD);
		}
		
		
		fromListBox.setSelectedIndex(0);
	}
	
	public Date getCriteria() {
		return this.criteria;
	}
	
	public String getCompany() {
		return registrySuggest.getValueBox().getValue();
	}
	
	public String getSubject() {
		return subjectTextBox.getValue();
	}
	
	public List<Tag> getDrashTagList() {
		return Collections.unmodifiableList(drashTagList);
	}

	@UiHandler("fromListBox")
	void onChangeEventListBox(ChangeEvent event) {

		String name = fromListBox.getSelectedValue();		
		if (name.compareTo(DateRange.ALL.name()) == 0)
			this.criteria = null;
		else if (name.compareTo(DateRange.TODAY.name()) == 0)
			this.criteria = new Date();
		else if (name.compareTo(DateRange.YESTERDAY.name()) == 0)
			this.criteria = DateUtils.getPrevDay(new Date());
		else if (name.compareTo(DateRange.THIS_WEEK.name()) == 0)
			this.criteria = DateUtils.getFirstDayOfWorkWeek(new Date());
		else if (name.compareTo(DateRange.THIS_MONTH.name()) == 0)
			this.criteria = DateUtils.getFirstDayOfMonth();
		else if (name.compareTo(DateRange.FIVETEEN_DAYS_AGO.name()) == 0)
			this.criteria = DateUtils.deleteDays2Date(new Date(), 15);
		else if (name.compareTo(DateRange.THIRTY_DAYS_AGO.name()) == 0)
			this.criteria = DateUtils.deleteDays2Date(new Date(), 30);
		else if (name.compareTo(DateRange.THIS_YEAR.name()) == 0)
			this.criteria = DateUtils.getFirstDayOfYear();
		
		for (Listener listener : listeners) {
			listener.onSelectFrom(criteria);
		}
	}
	
	private void initUserButton(final List<User> users) {
		
		this.ownerButton.addClickHandler(new ClickHandler() {
			
			private PopupPanel popup = new PopupPanel(true);
			
			{
				SearchPanel.this.ownerVPanel = new VerticalPanel();
				for (final User user : users ) {
					
					final CheckBox cb = new CheckBox(user.getName());
					cb.setName(String.valueOf(user.getId()));
					cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
						
						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							if (event.getValue()) {
								cb.addStyleName(AON.AON_BOLD);
								insertDrastUserList(user);
							}
							else {
								cb.removeStyleName(AON.AON_BOLD);
								removeDrashUser(user);
							}
							
							for (Listener listener : listeners)
								listener.onSelectOwner(drashUserList);
						}
					});
					SearchPanel.this.ownerVPanel.add(cb);
				}

				popup.add(SearchPanel.this.ownerVPanel);				
			}
			
			@Override
			public void onClick(ClickEvent event) {
				int left = ownerButton.getAbsoluteLeft();
				int top = ownerButton.getAbsoluteTop()
						+ ownerButton.getOffsetHeight();

				popup.setPopupPosition(left, top);
				popup.show();
			}
		});
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
						public void onValueChange(
								ValueChangeEvent<Boolean> event) {

							if (event.getValue()) {
								cb.addStyleName(AON.AON_BOLD);
								insertDrashTag(tag);
							} else {
								cb.removeStyleName(AON.AON_BOLD);
								removeDrashTag(tag);
							}
							for (Listener listener : listeners) 
								listener.onCloseTagPanel(drashTagList);
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
						public void onValueChange(
								ValueChangeEvent<Boolean> event) {
							if (event.getValue()) {
								cb.addStyleName(AON.AON_BOLD);
								insertDrashTag(tag);
							} else {
								cb.removeStyleName(AON.AON_BOLD);
								removeDrashTag(tag);
							}
							for (Listener listener : listeners) 
								listener.onCloseTagPanel(drashTagList);
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

	private void initNoticeButton(final List<DefaultAonTagIssueSelected> tags) {

		this.noticeTagButton.addClickHandler(new ClickHandler() {

			private PopupPanel popup = new PopupPanel(true);

			{
				SearchPanel.this.noticesVPanel = new VerticalPanel();

				for (final DefaultAonTagIssueSelected tag : tags) {

					final CheckBox cb = new CheckBox(tag.getName());
					cb.setName(String.valueOf(tag.getId()));
					cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

						@Override
						public void onValueChange(
								ValueChangeEvent<Boolean> event) {
							
							if (event.getValue()) {
								cb.addStyleName(AON.AON_BOLD);
								insertDrashTag(tag);
							} else {
								cb.removeStyleName(AON.AON_BOLD);
								removeDrashTag(tag);
							}
							
							for(Listener listener : listeners)
								listener.onCloseTagPanel(drashTagList);
						}
					});
					SearchPanel.this.noticesVPanel.add(cb);
				}
				popup.add(SearchPanel.this.noticesVPanel);
			}

			@Override
			public void onClick(ClickEvent event) {
				int left = noticeTagButton.getAbsoluteLeft();
				int top = noticeTagButton.getAbsoluteTop()
						+ noticeTagButton.getOffsetHeight();

				popup.setPopupPosition(left, top);
				popup.show();
			}
		});
	}
	
	private void insertDrashTag(DefaultAonTagIssueSelected pTag) {
		Tag tag = new Tag();
		tag.setId(pTag.getId());
		tag.setDomain(pTag.getDomain());
		tag.setName(pTag.getName());
		tag.setType(pTag.getType());
		this.drashTagList.add(tag);
	}
	
	private void removeDrashTag(DefaultAonTagIssueSelected pTag) {
		
		for (Tag tag : drashTagList) {
			if (tag.getId() == pTag.getId()) {
				drashTagList.remove(tag);
				break;
			}
		}
	}
	
	private void insertDrastUserList(User user) {
		this.drashUserList.add(user);
	}
	
	private void removeDrashUser(User pUser) {
		
		for (User user : drashUserList) {
			if (user.getId() == pUser.getId()) {
				drashUserList.remove(user);
				break;
			}
		}
	}
}

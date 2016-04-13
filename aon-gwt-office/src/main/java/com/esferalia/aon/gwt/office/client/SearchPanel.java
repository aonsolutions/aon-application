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
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
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

public class SearchPanel extends Composite implements KeyDownHandler {

	enum DateRange {
		ALL("all"), 
		TODAY("today"), 
		YESTERDAY("yesterday"), 
		THIS_WEEK("thisWeek"), 
		THIS_MONTH("thisMonth"), 
		FIVETEEN_DAYS_AGO("fiveteenDaysAgo"), 
		THIRTY_DAYS_AGO("fiveteenDaysAgo"), 
		THIS_YEAR("thisYear");
		
		String description ;

		private DateRange(String range) {
			this.description = range;
		}
		
		public String getValue() {
			return this.description;
		}
	}
	
	public interface Listener {
	
		void onSelectCompany(String company);
		
		void onSelectSubject(String subject);
		
		void onCloseTagPanel(List<Tag> drashTagList);
		
		void onSelectOwner(String userName);
		
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
	private String userSelected;
	
	private List<AonTagIssueSelected> typeTagList;
	private List<AonTagIssueSelected> priorityTagList;
	private List<AonTagIssueSelected> noticeTagList;
	
	private List<Tag> drashTagList;

	private MultiWordSuggestOracle registries = new MultiWordSuggestOracle();

	private VerticalPanel typeVPanel;
	private VerticalPanel priorityVPanel;
	private VerticalPanel noticesVPanel;
	private VerticalPanel ownerVPanel;

	public SearchPanel() {
		this.registrySuggest = new SuggestBox(registries);

		initWidget(uiBinder.createAndBindUi(this));
		
		this.listeners = new LinkedList<Listener>();
		this.typeTagList = new LinkedList<AonTagIssueSelected>();
		this.priorityTagList = new LinkedList<AonTagIssueSelected>();
		this.noticeTagList = new LinkedList<AonTagIssueSelected>();
		this.drashTagList = new LinkedList<Tag>();
		
		this.subjectTextBox.addKeyDownHandler(this);

		fromListBox.addItem(" ", DateRange.ALL.getValue());
		fromListBox.addItem("Hoy", DateRange.TODAY.getValue());
		fromListBox.addItem("Ayer", DateRange.YESTERDAY.getValue());
		fromListBox.addItem("Esta semana", DateRange.THIS_WEEK.getValue());
		fromListBox.addItem("Este mes", DateRange.THIS_MONTH.getValue());
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
	
	@Override
	public void onKeyDown(KeyDownEvent event) {
		int keyCode = event.getNativeKeyCode();
		String subject = subjectTextBox.getValue();
		
		if (keyCode == KeyCodes.KEY_ENTER) {
			onSubjectKeyDown(subject);
		}
	}
	
	private void onSubjectKeyDown(String subject) {
		for (Listener listener : listeners)
			listener.onSelectSubject(subject);
	}
	
	// ---------------------------------------------------------
	
	public void addRegistry(Registry registry) {
		registries.add(registry.getName());
	}
	
	public void addUserList(List<User> users) {		
		initUserButton(users);
	}

	public void addTagList(List<AonTagIssueSelected> list) {

		for (AonTagIssueSelected tag : list) {

			if (tag.isOfficeType())
				typeTagList.add(tag);
			else if (tag.isOfficePriority())
				priorityTagList.add(tag);
			else if (tag.isOfficeNotice())
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
		
		this.userSelected = null;
		this.drashTagList.clear();		
		this.fromListBox.setSelectedIndex(0);
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
			private boolean changes = false;			
			
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
								unSelectPanel(cb.getText());
								SearchPanel.this.userSelected = cb.getText();
							}
							else {
								cb.removeStyleName(AON.AON_BOLD);
								SearchPanel.this.userSelected = null;
							}
							changes = true;
						}
					});
					SearchPanel.this.ownerVPanel.add(cb);
				}
				popup.addCloseHandler(new CloseHandler<PopupPanel>() {
					
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (changes) {
							for (Listener listener : listeners)
								listener.onSelectOwner(SearchPanel.this.userSelected);
							changes = false;
						}
					}
				});
				popup.add(SearchPanel.this.ownerVPanel);				
			}
			
			private void unSelectPanel(String userName) {
				for (int x = 0; x < ownerVPanel.getWidgetCount(); x++) {
					CheckBox cb = (CheckBox) ownerVPanel.getWidget(x);
					if (AonStringUtils.equals(cb.getText(), userName) == false && cb.getValue()) {
						cb.setValue(false);
						cb.removeStyleName(AON.AON_BOLD);
					}
				}
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
			final List<AonTagIssueSelected> typeTags) {

		this.typeButton.addClickHandler(new ClickHandler() {

			private PopupPanel popup = new PopupPanel(true);
			private boolean changes = false;

			{
				SearchPanel.this.typeVPanel = new VerticalPanel();

				for (final AonTagIssueSelected tag : typeTags) {

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
							changes = true;
						}
					});
					SearchPanel.this.typeVPanel.add(cb);
				}

				popup.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (changes) {
							for (Listener listener : listeners) 
								listener.onCloseTagPanel(drashTagList);
							changes = false;
						}
					}
				});

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
			final List<AonTagIssueSelected> priorityTags) {

		this.priorityButton.addClickHandler(new ClickHandler() {

			private PopupPanel popup = new PopupPanel(true);
			boolean changes = false;

			{
				SearchPanel.this.priorityVPanel = new VerticalPanel();

				for (final AonTagIssueSelected tag : priorityTags) {

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
							changes = true;
						}
					});
					
					SearchPanel.this.priorityVPanel.add(cb);
				}

				popup.addCloseHandler(new CloseHandler<PopupPanel>() {
					
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (changes) {
							for (Listener listener : listeners) 
								listener.onCloseTagPanel(drashTagList);
							changes = false;
						}
					}
				});

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

	private void initNoticeButton(final List<AonTagIssueSelected> tags) {

		this.noticeTagButton.addClickHandler(new ClickHandler() {

			private PopupPanel popup = new PopupPanel(true);
			private boolean changes = false;

			{
				SearchPanel.this.noticesVPanel = new VerticalPanel();

				for (final AonTagIssueSelected tag : tags) {

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
							changes = true;
						}
					});
					
					SearchPanel.this.noticesVPanel.add(cb);
				}
				
				popup.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (changes) {
							for(Listener listener : listeners)
								listener.onCloseTagPanel(drashTagList);
							changes = false;
						}
					}
				});

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
	
	private void insertDrashTag(AonTagIssueSelected pTag) {
		Tag tag = new Tag();
		tag.setId(pTag.getId());
		tag.setDomain(pTag.getDomain());
		tag.setName(pTag.getName());
		tag.setType(pTag.getType());
		this.drashTagList.add(tag);
	}
	
	private void removeDrashTag(AonTagIssueSelected pTag) {
		
		for (Tag tag : drashTagList) {
			if (tag.getId() == pTag.getId()) {
				drashTagList.remove(tag);
				break;
			}
		}
	}
}

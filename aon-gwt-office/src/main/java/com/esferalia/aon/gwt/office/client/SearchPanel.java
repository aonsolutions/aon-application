package com.esferalia.aon.gwt.office.client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;

public class SearchPanel extends Composite implements KeyUpHandler {

	private static SearchPanelUiBinder uiBinder = GWT
			.create(SearchPanelUiBinder.class);

	interface Listener {

		void onOpenIssuesRbSelected(ValueChangeEvent<Boolean> event);

		void onClosedIssuesRbSelected(ValueChangeEvent<Boolean> event);

		void onAllIssuesRbSelected(ValueChangeEvent<Boolean> event);

		void onChangeEventListBox(Date criteria);
		
		void onSuggestBoxChangeValue(String sender);
	}

	interface SearchPanelUiBinder extends UiBinder<Widget, SearchPanel> {
	}

	@UiField
	ListBox fromListBox;
	@UiField (provided=true)
	SuggestBox registrySuggest;

	@UiField
	RadioButton openIssuesRb;
	@UiField
	RadioButton closedIssuesRb;
	@UiField
	RadioButton allIssuesRb;
	
	private List<Listener> listeners;	
	private MultiWordSuggestOracle registries = new MultiWordSuggestOracle();

	public SearchPanel() {
		this.registrySuggest = new SuggestBox(registries);
		initWidget(uiBinder.createAndBindUi(this));

		this.listeners = new LinkedList<Listener>();
		this.registrySuggest.getValueBox().addKeyUpHandler(this);
		
		fromListBox.addItem(" -------- ", "all");
		fromListBox.addItem("Hoy", "today");
		fromListBox.addItem("Esta semana", "thisWeek");
		fromListBox.addItem("Este mes", "thisMonth");
		fromListBox.addItem("Este a\u00F1o", "thisYear");
		fromListBox.setSelectedIndex(0);
	}
	
	public void addRegistry (Registry registry) {	
		registries.add(registry.getName());
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
		if (name.compareTo("today") == 0)
			criteria = new Date();
		else if (name.compareTo("thisWeek") == 0)
			criteria = DateUtils.getFirstDayOfWorkWeek(new Date());
		else if (name.compareTo("thisMonth") == 0)
			criteria = DateUtils.getFirstDayOfMonth();
		else if (name.compareTo("thisYear") == 0)
			criteria = DateUtils.getFirstDayOfYear();

		if (criteria != null)
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
	void onRegistrySelectionValue(SelectionEvent<SuggestOracle.Suggestion> event) {
		String selected = event.getSelectedItem().getReplacementString();
		for (Listener listener : listeners)
			listener.onSuggestBoxChangeValue(selected);
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

}

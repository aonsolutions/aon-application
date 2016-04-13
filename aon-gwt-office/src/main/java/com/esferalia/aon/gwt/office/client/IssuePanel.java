package com.esferalia.aon.gwt.office.client;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class IssuePanel extends CustomDialog implements KeyUpHandler {

	interface Listener {

		void onCreateNewFAQ (Notice notice);
		
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
	@UiField
	Label statusLabel;

	@UiField
	TextBox titleTextBox;
	@UiField(provided = true)
	SuggestBox registrySuggest;

	@UiField(provided = true)
	SuggestBox rmediaSuggest;
	
	@UiField
	Button acceptButton;
	@UiField
	Button cancelButton;

	private String registry;
	private Date date;

	private User user;

	private List<Listener> listeners;
	
	private Map<Integer, Registry> registryMap;

	private List<RegistryMedia> rmediasList;
	private MultiWordSuggestOracle registries = new MultiWordSuggestOracle();	
	private MultiWordSuggestOracle rmedias = new MultiWordSuggestOracle();
	
	// ******************************* Mapas de apoyo **
	private Map<String, Integer> registryDrashMap = new HashMap<String, Integer>();
	private Map<String, Integer> rmediaDrashMap = new HashMap<String, Integer>();	
	// *************************************************
	
	private DateTimeFormat format = DateTimeFormat
			.getFormat("dd-MM-yyyy HH:mm");

	public IssuePanel(User user) {
		setCaption("Nueva Incidencia");
		this.registrySuggest = new SuggestBox(registries);		
		this.rmediaSuggest = new SuggestBox(rmedias);
		
		setWidget(uiBinder.createAndBindUi(this));

		setAnimationEnabled(true);
		setGlassEnabled(true);

		this.user = user;
		this.listeners = new LinkedList<Listener>();		
		this.date = new Date();
		this.titleTextBox.addKeyUpHandler(this);
		
		dateLabel.setText(format.format(date));
		loggedLabel.setText(user.getName());
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	public void showFAQPanel() {
		registrySuggest.setEnabled(false);
		rmediaSuggest.setEnabled(false);
		
		statusLabel.setText("Estado: FAQ");
		statusLabel.addStyleName(AON.AON_BOLD);
		statusLabel.addStyleName(AON.AON_RED);

		titleTextBox.setFocus(true);
		titleTextBox.selectAll();
		
		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {				
				addNewFAQClickHandler(event);
			}
		});
		center();
	}
	
	public void showNoticePanel() {
		statusLabel.setText("Estado: OPEN");
		statusLabel.addStyleName(AON.AON_BOLD);
		
		acceptButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				addNewNoticeClickHander(event);
			}
		});
		center();
	}

	// ----------------------------------------------------

	public void setSender(String sender) {
		this.registry = sender;
		this.registrySuggest.setText(sender);
	}
	
	public void setRegistries(Map<Integer, Registry> map) {
	
		this.registryMap = map;
		this.registrySuggest.setEnabled(registryMap.size() > 0);
		
		for (Registry registry : map.values()) {			
			StringBuilder sb = new StringBuilder();
			if (registry.getName().trim().isEmpty() == false) {
				sb.append(registry.getName());
			}			
			this.registries.add(sb.toString());
			this.registryDrashMap.put(sb.toString(), registry.getId());
		}		
	}

	public void setRMedias(List<RegistryMedia> rmedias) {
		this.rmediasList = rmedias;
		this.rmediaSuggest.setEnabled(rmedias.size() > 0);
		
		for (RegistryMedia rmedia : rmediasList) {
			StringBuilder sb = new StringBuilder();
			if (rmedia.getRegistry().getAlias().trim().isEmpty() == false) {
				sb.append("(" + rmedia.getRegistry().getAlias() + ")");
				sb.append(' ');
			}
			
			if (rmedia.getRegistry().getName().trim().isEmpty() == false) {
				sb.append(rmedia.getRegistry().getName());				
			}

			if (sb.length() > 0)
				sb.append(" - ");
			
			if (rmedia.getValue().trim().isEmpty() == false) {
				sb.append(rmedia.getValue());
			}
			
			if ( rmedia.getComment().trim().isEmpty() == false) {
				if (rmedia.getValue().trim().isEmpty() == false)
					sb.append(" / ");
				sb.append(rmedia.getComment());
			}
			this.rmedias.add(sb.toString());
			this.rmediaDrashMap.put(sb.toString(), rmedia.getRegistry().getId());
		}
	}

	public void setTitle(String title) {
		titleTextBox.setValue(title);
	}

	// ----------------------------------------------------
	// ------------------------------------------- Handlers
	// ----------------------------------------------------

	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		hide();
	}

	@UiHandler("registrySuggest")
	void onRegistrySelectionValue(SelectionEvent<SuggestOracle.Suggestion> event) {		
		this.registry = event.getSelectedItem().getReplacementString();
		this.rmediaSuggest.setText("");
	}
	
	@UiHandler("rmediaSuggest")
	void onRmediaSelectionValue(SelectionEvent<SuggestOracle.Suggestion> event) {
		String selected = event.getSelectedItem().getReplacementString();		
		Integer registryId = rmediaDrashMap.get(selected);
		
		Registry aux = registryMap.get(registryId);
		
		if (aux != null) {
			this.registry = aux.getName();
			registrySuggest.setText(aux.getName());			
		}
		else
			Window.alert("Registro no encontrado");
		
		titleTextBox.setFocus(true);
	}
	
	@Override
	public void onKeyUp(KeyUpEvent event) {
		acceptButton.setEnabled(!AonStringUtils.isEmpty(titleTextBox.getText()));		
	}
	
	private void addNewNoticeClickHander(ClickEvent event) {

		Notice notice = new Notice();
		notice.setTitle(titleTextBox.getValue());		
		notice.setStatus(NoticeStatus.OPEN.getValue());
		notice.setSender(user);
		notice.setStartDate(date);

		if (registry != null) {
			int id = registryDrashMap.get(registry);
			Registry aux = registryMap.get(id);
			notice.setCompany(aux.getName());
			notice.setSource(String.valueOf(aux.getId()));
		}

		onCreateNewIssue(notice);
		hide();
	}
	
	private void addNewFAQClickHandler(ClickEvent event) {

		Notice notice = new Notice();
		notice.setTitle(titleTextBox.getValue());
		notice.setStatus(NoticeStatus.OPEN.getValue());
		notice.setSender(user);
		notice.setStartDate(date);

		onCreateNewFAQ(notice);
		hide();
	}
	
	private void onCreateNewFAQ(Notice notice) {
		for (Listener listener : listeners) 
			listener.onCreateNewFAQ(notice);
	}

	private void onCreateNewIssue(Notice notice) {
		for (Listener listener : listeners)
			listener.onCreateNewIssue(notice);
	}
}

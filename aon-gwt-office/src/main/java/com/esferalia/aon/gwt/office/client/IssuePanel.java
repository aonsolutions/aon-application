package com.esferalia.aon.gwt.office.client;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
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
	private List<Registry> registryList;
	private List<RegistryMedia> rmediasList;
	private MultiWordSuggestOracle registries = new MultiWordSuggestOracle();
	private MultiWordSuggestOracle rmedias = new MultiWordSuggestOracle();

	private Map<String, Integer> idsRmediaMap;
	
	private DateTimeFormat format = DateTimeFormat
			.getFormat("dd-MM-yyyy HH:mm");

	public IssuePanel(User user) {
		setCaption("Nueva Incidencia");
		registrySuggest = new SuggestBox(registries);		
		
		rmediaSuggest = new SuggestBox(rmedias);
		
		setWidget(uiBinder.createAndBindUi(this));

		setAnimationEnabled(true);
		setGlassEnabled(true);

		this.user = user;
		this.listeners = new LinkedList<Listener>();
		this.idsRmediaMap = new HashMap<String, Integer>();
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
		
		for (Registry registry : registries) {
			StringBuilder sb = new StringBuilder();
			if (registry.getName().trim().isEmpty() == false) {
				sb.append(registry.getName());
			}			
			this.registries.add(sb.toString());			
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
			this.idsRmediaMap.put(sb.toString(), rmedia.getRegistry().getId());
		}
	}

	public void setTitle(String title) {
		titleTextBox.setValue(title);
	}

	// ----------------------------------------------------
	// ------------------------------------------- Handlers
	// ----------------------------------------------------

	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent event) {

		if (titleTextBox.getText().trim().isEmpty())
			return;

		Notice notice = new Notice();
		notice.setTitle((titleTextBox.getValue().isEmpty()) ? ""
				: titleTextBox.getValue());
		notice.setStatus(NoticeStatus.OPEN.getValue());
		notice.setSender(user);
		notice.setStartDate(date);

		if (registry != null) {
			int recipientId = -1;
			for (Registry registry : registryList) {
				if (registry.getName().compareTo(this.registry) == 0)
					recipientId = registry.getId();
			}

			if (recipientId == -1) {
				Window.alert("Remitente no encontrado.");
				return;
			}

			notice.setCompany(registry);
			notice.setSource(String.valueOf(recipientId));
		}

		onCreateNewIssue(notice);
		hide();

	}

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
		Integer registryId = idsRmediaMap.get(selected);
		
		Registry aux = null;
		for (RegistryMedia rmedia : rmediasList) {
			if (rmedia.getRegistry().getId() == registryId) {
				aux = rmedia.getRegistry();
				break;
			}
		}
		
		if (aux != null) {
			this.registry = aux.getName();
			registrySuggest.setText(aux.getName());			
		}
		else
			Window.alert("Registro no encontrado");
		
	}

	private void onCreateNewIssue(Notice notice) {
		for (Listener listener : listeners)
			listener.onCreateNewIssue(notice);
	}
	
}

package com.esferalia.aon.gwt.marketing.client.customer;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCardSmall;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmall;
import com.esferalia.aon.gwt.marketing.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

public class CustomerNotesModule extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(CustomerNotesModule.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private static CommonServiceAsync COMMON_SERVICE;
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private HTMLPanel container;
	private ScrollPanel scrollPanel;
	private HTMLPanel content;
	
	private HTMLPanel notesContent;

	private List<RegistryNote> notes;
	private Integer customerId;
	
	private RegistryNote observation;

	private boolean observationsOpen = true;
	private boolean messagesOpen = true;
	private Map<Integer, Boolean> notesOpen = new HashMap<Integer, Boolean>();
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");

		AON.ensureInjected();
		
		initializeService();
		
		customerId = getCustomer() > 0 ? getCustomer() : null;
		
		container = new HTMLPanel(AonStringUtils.EMPTY);
		container.setStyleName(AON.CSS.aonFlexColumn());
		
		content = new HTMLPanel(AonStringUtils.EMPTY);
		content.setStyleName(AON.CSS.aonFlexColumn());
		content.getElement().getStyle().setProperty("padding", "0 1rem");
		
		scrollPanel = new ScrollPanel(content);
		container.add(scrollPanel);
		
		root.add(container);
		
		onSearch();
		
		// Remove customer from LS
		removeCustomer();
	}
	
	private void initializeService() {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
	}
	
	public void onSearch() {
		content.clear();
		
		getCustomerNotes(customerNotes -> {
			createNotesPanel();
		});
	}

	private void createNotesPanel() {
		content.clear();
		
		// Observation
		createObservations();
		
		// Notes
		notesContent = new HTMLPanel(AonStringUtils.EMPTY);
		notesContent.setStyleName(AON.CSS.aonFlexColumn());
		createNotes();
		content.add(notesContent);
		
		scrollPanel.setWidget(content);
	}
	
	private void createObservations() {
		List<RegistryNote> observations = notes.stream().filter(note -> note.getNoteType().equals(NoteType.OBSERVATION)).collect(Collectors.toList());
		
		observation = new RegistryNote()
				.setDomain(getCurrentDomain())
				.setNoteDate(new Date())
				.setNoteType(NoteType.OBSERVATION)
				.setRegistry(customerId)
				.setConfidential(false)
				.setSecurityLevel(SecurityLevel.OFFICIAL);
		
		if(!observations.isEmpty()) {
			observation = observations.get(0);
		}
		
		HTMLPanel cardsPanel = new HTMLPanel(AonStringUtils.EMPTY);
		cardsPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		AonTableButton openBtn = new AonTableButton(
				observationsOpen ? "Cerrar observaciones" : "Abrir observaciones" , 
				observationsOpen ?  AON.CSS.aonIconUp() : AON.CSS.aonIconDown());
		
		TextBox description = new TextBox();
		description.setValue(observation.getDescription());
		description.getElement().getStyle().setProperty("font-size", "1rem");
		description.getElement().getStyle().setProperty("font-weight", "700");
		description.getElement().getStyle().setProperty("color", "#5f6368");
		description.getElement().getStyle().setProperty("border", "none");
		description.getElement().getStyle().setProperty("cursor", "pointer");
		description.getElement().setPropertyString("placeholder", "Descripci\u00f3n");
		description.getElement().getStyle().setProperty("background", "transparent");
		description.addValueChangeHandler(e -> {
			observation.setDescription(description.getValue());
			saveNote(observation);
		});
		
		TextArea comments = new TextArea();
		comments.setValue(observation.getComments());
		comments.setVisibleLines(4);
		comments.getElement().getStyle().setProperty("font-size", ".8rem");
		comments.getElement().getStyle().setProperty("font-weight", "500");
		comments.getElement().getStyle().setProperty("color", "#5f6368");
		comments.getElement().getStyle().setProperty("border", "none");
		comments.getElement().getStyle().setProperty("cursor", "pointer");
		comments.getElement().getStyle().setProperty("background", "transparent");
		comments.getElement().getStyle().setProperty("resize", "vertical");
		comments.getElement().setPropertyString("placeholder", "Observaci\u00f3n");	
		comments.addValueChangeHandler(e -> {
			observation.setComments(comments.getValue());
			saveNote(observation);
		});
		
		AonCustomCardSmall card = new AonCustomCardSmall(description, openBtn);
		card.setToolbarWidgetShown();
		
		HTMLPanel cardContentPanel = new HTMLPanel(AonStringUtils.EMPTY);
		cardContentPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		cardContentPanel.add(comments);
		
		HTMLPanel footerPanel = new HTMLPanel(AonStringUtils.EMPTY);
		footerPanel.addStyleName(AON.CSS.aonItemFlex());
		footerPanel.addStyleName(AON.CSS.aonFlexBetween());
		
		HTMLPanel buttonsPanel = new HTMLPanel(AonStringUtils.EMPTY);
		buttonsPanel.addStyleName(AON.CSS.aonItemFlex());
		buttonsPanel.addStyleName(AON.CSS.aonCustomCardButton());
		footerPanel.add(buttonsPanel);
		
		AonTableButton deleteBtn = new AonTableButton("Borrar observaci\u00f3n", AON.CSS.aonIconDelete());
		deleteBtn.addClickHandler(e -> {
			e.stopPropagation();
			
			observation.setDescription(AonStringUtils.EMPTY);
			observation.setComments(AonStringUtils.EMPTY);
			observation.setNoteDate(null);
			
			saveNote(observation);
		});
		buttonsPanel.add(deleteBtn);
		
		AonTableButton noteBtn = new AonTableButton("Archivar nota", AON.CSS.aonIconMoveToInbox());
		noteBtn.addClickHandler(e -> {
			e.stopPropagation();
			moveToNotes(observation);
		});
		buttonsPanel.add(noteBtn);
			
		footerPanel.add(new Label(null == observation.getNoteDate() ? "" : formatDate.format(observation.getNoteDate())));
		
		cardContentPanel.add(footerPanel);
		
		card.add(cardContentPanel);
		
		cardsPanel.add(card);
		
		openBtn.addClickHandler(e -> {
			observationsOpen = !observationsOpen;
			cardContentPanel.setVisible(observationsOpen);
			openBtn.setTitle(observationsOpen ? "Cerrar observaciones" : "Abrir observaciones");
			
			if(observationsOpen) {
				openBtn.removeStyleName(AON.CSS.aonIconDown());
				openBtn.addStyleName(AON.CSS.aonIconUp());
			} else {
				openBtn.removeStyleName(AON.CSS.aonIconUp());
				openBtn.addStyleName(AON.CSS.aonIconDown());
			}
			
		});
		
		content.add(cardsPanel);
		
	}

	private void createNotes() {
		notesContent.clear();
		
		List<RegistryNote> messages = notes.stream().filter(note -> note.getNoteType().equals(NoteType.MESSAGE)).collect(Collectors.toList());
		messages.sort(Comparator.comparing(RegistryNote::getNoteDate, Comparator.nullsFirst(Comparator.reverseOrder())));
		
		AonToolbarSmall toolbar = new AonToolbarSmall((messages.isEmpty() ? "Sin" : messages.size()) + (messages.isEmpty() || messages.size() > 1 ? " Notas" : " Nota"));
		toolbar.getElement().getStyle().setProperty("min-width", "auto");
		
		AonTableButton newBtn = new AonTableButton("Nueva nota", AON.CSS.aonIconAdd());
		newBtn.addClickHandler(e -> {
			notes.add(
				new RegistryNote()
				.setDomain(getCurrentDomain())
				.setNoteType(NoteType.MESSAGE)
				.setNoteDate(new Date())
				.setRegistry(customerId)
				.setConfidential(false)
				.setSecurityLevel(SecurityLevel.OFFICIAL)
			);
			
			createNotes();
		});
		toolbar.add(newBtn);
		
		notesContent.add(toolbar);
		
		if(!messages.isEmpty()) {
			AonTableButton openBtn = new AonTableButton(
					messagesOpen ? "Cerrar mensajes" : "Abrir mensajes" , 
					messagesOpen ? AON.CSS.aonIconDown() : AON.CSS.aonIconLeft());
			toolbar.add(openBtn);
			
			HTMLPanel cardsPanel = new HTMLPanel(AonStringUtils.EMPTY);
			cardsPanel.addStyleName(AON.CSS.aonFlexColumn());
			
			messages.forEach(message -> {
				notesOpen.put(message.getId(), true);
				
				AonTableButton openCardBtn = new AonTableButton(
						notesOpen.get(message.getId()) ? "Cerrar nota" : "Abrir nota" , 
						notesOpen.get(message.getId()) ?  AON.CSS.aonIconUp() : AON.CSS.aonIconDown());
				
				TextBox description = new TextBox();
				description.setValue(message.getDescription());
				description.getElement().getStyle().setProperty("font-size", "1rem");
				description.getElement().getStyle().setProperty("font-weight", "700");
				description.getElement().getStyle().setProperty("color", "#5f6368");
				description.getElement().getStyle().setProperty("border", "none");
				description.getElement().getStyle().setProperty("cursor", "pointer");
				description.getElement().getStyle().setProperty("background", "transparent");
				description.getElement().setPropertyString("placeholder", "Descripci\u00f3n");	
				description.addValueChangeHandler(e -> {
					message.setDescription(description.getValue());
					saveNote(message);
				});
				
				TextArea comments = new TextArea();
				comments.setValue(message.getComments());
				comments.setVisibleLines(4);
				comments.getElement().getStyle().setProperty("font-size", ".8rem");
				comments.getElement().getStyle().setProperty("font-weight", "500");
				comments.getElement().getStyle().setProperty("color", "#5f6368");
				comments.getElement().getStyle().setProperty("border", "none");
				comments.getElement().getStyle().setProperty("cursor", "pointer");
				comments.getElement().getStyle().setProperty("background", "transparent");
				comments.getElement().getStyle().setProperty("resize", "vertical");
				comments.getElement().setPropertyString("placeholder", "Nota");	
				comments.addValueChangeHandler(e -> {
					message.setComments(comments.getValue());
					saveNote(message);
				});
				
				AonCustomCardSmall card = new AonCustomCardSmall(description, openCardBtn);
				card.setToolbarWidgetShown();
				
				HTMLPanel cardContentPanel = new HTMLPanel(AonStringUtils.EMPTY);
				cardContentPanel.addStyleName(AON.CSS.aonFlexColumn());
				
				cardContentPanel.add(comments);
				
				HTMLPanel footerPanel = new HTMLPanel(AonStringUtils.EMPTY);
				footerPanel.addStyleName(AON.CSS.aonItemFlex());
				footerPanel.addStyleName(AON.CSS.aonFlexBetween());
				
				HTMLPanel buttonsPanel = new HTMLPanel(AonStringUtils.EMPTY);
				buttonsPanel.addStyleName(AON.CSS.aonItemFlex());
				buttonsPanel.addStyleName(AON.CSS.aonCustomCardButton());
				footerPanel.add(buttonsPanel);
				
				AonTableButton deleteBtn = new AonTableButton("Borrar nota", AON.CSS.aonIconDelete());
				deleteBtn.addClickHandler(e -> {
					e.stopPropagation();
					deleteNote(message);
				});
				buttonsPanel.add(deleteBtn);
					
				footerPanel.add(new Label(null == message.getNoteDate() ? "" : formatDate.format(message.getNoteDate())));
				
				cardContentPanel.add(footerPanel);
				
				card.add(cardContentPanel);
				
				cardsPanel.add(card);
				
				openCardBtn.addClickHandler(e -> {
					notesOpen.put(message.getId(), !notesOpen.get(message.getId()));
					cardContentPanel.setVisible(notesOpen.get(message.getId()));
					openCardBtn.setTitle(notesOpen.get(message.getId()) ? "Cerrar nota" : "Abrir nota");
					
					if(notesOpen.get(message.getId())) {
						openCardBtn.removeStyleName(AON.CSS.aonIconDown());
						openCardBtn.addStyleName(AON.CSS.aonIconUp());
					} else {
						openCardBtn.removeStyleName(AON.CSS.aonIconUp());
						openCardBtn.addStyleName(AON.CSS.aonIconDown());
					}
					
				});
			});
			
			openBtn.addClickHandler(e -> {
				messagesOpen = !messagesOpen;
				cardsPanel.setVisible(messagesOpen);
				openBtn.setTitle(messagesOpen ? "Cerrar mensajes" : "Abrir mensajes");
				
				if(messagesOpen) {
					openBtn.removeStyleName(AON.CSS.aonIconDown());
					openBtn.addStyleName(AON.CSS.aonIconUp());
				} else {
					openBtn.removeStyleName(AON.CSS.aonIconUp());
					openBtn.addStyleName(AON.CSS.aonIconDown());
				}
				
			});
			
			notesContent.add(cardsPanel);
		}
	}

	private void getCustomerNotes(Consumer<List<RegistryNote>> success) {
		COMMON_SERVICE.getCustomerNotes(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), customerId,
			new AsyncCallback<List<RegistryNote>>() {

				@Override
				public void onSuccess(List<RegistryNote> result) {
					notes = result;
					success.accept(result);
				}

				@Override
				public void onFailure(Throwable caught) { }
		});
	}
	
	private void moveToNotes(RegistryNote observation) {
		RegistryNote newNote = new RegistryNote()
				.setDomain(observation.getDomain())
				.setComments(observation.getComments())
				.setConfidential(observation.isConfidential())
				.setDescription(observation.getDescription())
				.setSecurityLevel(observation.getSecurityLevel())
				.setNoteType(NoteType.MESSAGE)
				.setNoteDate(new Date())
				.setRegistry(customerId);
		
		observation.setDescription(AonStringUtils.EMPTY);
		observation.setComments(AonStringUtils.EMPTY);
		observation.setNoteDate(null);
		
		COMMON_SERVICE.saveNote(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), observation,
			new AsyncCallback<RegistryNote>() {

				@Override
				public void onSuccess(RegistryNote result) {
					COMMON_SERVICE.saveNote(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), newNote,
							new AsyncCallback<RegistryNote>() {

								@Override
								public void onSuccess(RegistryNote result) {
									onSearch();
								}

								@Override
								public void onFailure(Throwable caught) { }
						});
				}

				@Override
				public void onFailure(Throwable caught) { }
		});
	}

	private void saveNote(RegistryNote note) {
		note.setNoteDate(new Date());
		
		COMMON_SERVICE.saveNote(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), note,
			new AsyncCallback<RegistryNote>() {

				@Override
				public void onSuccess(RegistryNote result) {
					onSearch();
				}

				@Override
				public void onFailure(Throwable caught) { }
		});
	}
	
	private void deleteNote(RegistryNote note) {
		COMMON_SERVICE.deleteNote(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), note.getId(),
			new AsyncCallback<Void>() {

				@Override
				public void onSuccess(Void result) {
					onSearch();
				}

				@Override
				public void onFailure(Throwable caught) { }
		});
	}

}

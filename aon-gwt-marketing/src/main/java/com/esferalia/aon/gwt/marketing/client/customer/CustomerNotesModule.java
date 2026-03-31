package com.esferalia.aon.gwt.marketing.client.customer;

import java.util.ArrayList;
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
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCardSmall;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDeleteTooltip;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmall;
import com.esferalia.aon.gwt.marketing.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

public class CustomerNotesModule extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(CustomerNotesModule.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private static CommonServiceAsync COMMON_SERVICE;
	
	private HTMLPanel container;
	private ScrollPanel scrollPanel;
	private HTMLPanel content;
	
	private HTMLPanel notesContent;

	private List<RegistryNote> notes;
	private Integer registryId; // customerId or companyId
	private Integer officeDomain;
	private String notesSource;
	
	private RegistryNote observation;

	private boolean observationsOpen = true;
	private boolean messagesOpen = true;
	private Map<Integer, Boolean> notesOpen = new HashMap<Integer, Boolean>();
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");

		AON.ensureInjected();
		
		initializeService();
		
		registryId = getCustomer() > 0 ? getCustomer() : null;
		officeDomain = getOfficeDomain() > 0 ? getOfficeDomain() : getCurrentDomain();
		notesSource = AonStringUtils.isNotBlank(getNotesSource()) ? getNotesSource() : null;
		
		container = new HTMLPanel(AonStringUtils.EMPTY);
		container.setStyleName(AON.CSS.aonFlexColumn());
		
		content = new HTMLPanel(AonStringUtils.EMPTY);
		content.setStyleName(AON.CSS.aonCustomerNotesContent());
		
		scrollPanel = new ScrollPanel(content);
		container.add(scrollPanel);
		
		root.add(container);
		
		onSearch();
		
		// Remove customer from LS
		removeCustomer();
		removeOfficeDomain();
		removeNotesSource();
	}
	
	private void initializeService() {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
	}
	
	public void onSearch() {
		content.clear();
		
		getRegistryNotes(registryNotes -> {
			createNotesPanel();
		});
	}

	private void createNotesPanel() {
		content.clear();
		
		// Observation
		createObservations();
		
		// Notes
		notesContent = new HTMLPanel(AonStringUtils.EMPTY);
		notesContent.setStyleName(AON.CSS.aonCustomerNotesCard());
		createNotes();
		content.add(notesContent);
		
		scrollPanel.setWidget(content);
	}
	
	private void createObservations() {
		List<RegistryNote> observations = new ArrayList<RegistryNote>();
		
		if(AonStringUtils.isBlank(notesSource))
			observations = notes.stream().filter(note -> note.getNoteType().equals(NoteType.OBSERVATION)).collect(Collectors.toList());
		else
			observations = notes.stream().filter(note -> note.getNoteType().equals(NoteType.safeValueOf(notesSource)) && note.getNoteDate() == null).collect(Collectors.toList());
		
		if(AonStringUtils.isBlank(notesSource))
			observation = new RegistryNote()
					.setDomain(officeDomain)
					.setNoteDate(new Date())
					.setNoteType(NoteType.OBSERVATION)
					.setRegistry(registryId)
					.setConfidential(false);
		else 
			observation = new RegistryNote()
				.setDomain(officeDomain)
				.setNoteType(NoteType.safeValueOf(notesSource))
				.setRegistry(registryId)
				.setConfidential(false);
		
		if(!observations.isEmpty())
			observation = observations.get(0);
		
		HTMLPanel cardsPanel = new HTMLPanel(AonStringUtils.EMPTY);
		cardsPanel.setStyleName(AON.CSS.aonCustomerNotesCard());
		
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
		description.getElement().setPropertyString("placeholder", "Observaci\u00f3n");
		description.getElement().getStyle().setProperty("background", "transparent");
		description.setEnabled(false);
		
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
		comments.getElement().setPropertyString("placeholder", "Escriba su observaci\u00f3n...");	
		comments.addValueChangeHandler(e -> {
			observation.setDescription("Obrservaci\u00f3n");
			observation.setComments(comments.getValue());
			saveNote(observation);
		});
		
		DateBoxEx noteDate = new DateBoxEx();
		noteDate.setValue(observation.getNoteDate());
		noteDate.getElement().getStyle().setProperty("font-size", ".8rem");
		noteDate.getElement().getStyle().setProperty("font-weight", "500");
		noteDate.getElement().getStyle().setProperty("color", "#5f6368");
		noteDate.getElement().getStyle().setProperty("border", "none");
		noteDate.getElement().getStyle().setProperty("cursor", "pointer");
		noteDate.getElement().getStyle().setProperty("background", "transparent");
		noteDate.getElement().getStyle().setProperty("width", "70px");
		noteDate.setEnabled(false);
		
		AonCustomCardSmall card = new AonCustomCardSmall(description, openBtn);
		card.setToolbarWidgetShown();
		card.addStyleName(AON.CSS.aonCustomerCardObservation());
		
		HTMLPanel cardContentPanel = new HTMLPanel(AonStringUtils.EMPTY);
		cardContentPanel.addStyleName(AON.CSS.aonCustomerCardContent());
		
		cardContentPanel.add(comments);
		
		HTMLPanel footerPanel = new HTMLPanel(AonStringUtils.EMPTY);
		footerPanel.addStyleName(AON.CSS.aonItemFlex());
		footerPanel.addStyleName(AON.CSS.aonFlexBetween());
		
		HTMLPanel buttonsPanel = new HTMLPanel(AonStringUtils.EMPTY);
		buttonsPanel.addStyleName(AON.CSS.aonItemFlex());
		buttonsPanel.addStyleName(AON.CSS.aonCustomCardButton());
		footerPanel.add(buttonsPanel);
		
		if(AonStringUtils.isNotBlank(observation.getComments())) {
			AonTableButton deleteBtn = new AonTableButton("Borrar observaci\u00f3n", AON.CSS.aonIconDelete());
			deleteBtn.addClickHandler(e -> {
				e.stopPropagation();
				
				deleteNote(observation, deleteBtn, deletion -> {
					observation.setDescription(AonStringUtils.EMPTY);
					observation.setComments(AonStringUtils.EMPTY);
					observation.setNoteDate(null);
					
					saveNote(observation);
				});
				
			});
			buttonsPanel.add(deleteBtn);
			
			// Only if exist observation, prevent non default observation for PAYROLL, FISCAL, ACCOUNTING
			
			if(null != observation.getId()) {
				AonTableButton noteBtn = new AonTableButton("Archivar nota", AON.CSS.aonIconMoveToInbox());
				noteBtn.addClickHandler(e -> {
					e.stopPropagation();
					moveToNotes(observation);
				});
				buttonsPanel.add(noteBtn);
			}
			
		}
			
		footerPanel.add(noteDate);
		
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
		
		List<RegistryNote> messages = new ArrayList<RegistryNote>();
		
		if(AonStringUtils.isBlank(notesSource))
			messages = notes.stream().filter(note -> note.getNoteType().equals(NoteType.MESSAGE) || note.getNoteType().equals(NoteType.CUSTOMER_STATUS)).collect(Collectors.toList());
		else
			messages = notes.stream().filter(note -> note.getNoteType().equals(NoteType.safeValueOf(notesSource)) && null != note.getNoteDate()).collect(Collectors.toList());
		
		messages.sort(Comparator.comparing(RegistryNote::getNoteDate, Comparator.nullsFirst(Comparator.reverseOrder())));
		
		AonToolbarSmall toolbar = new AonToolbarSmall((messages.isEmpty() ? "Sin" : messages.size()) + (messages.isEmpty() || messages.size() > 1 ? " Anotaciones" : " Anotaci\u00f3n"));
		toolbar.getElement().getStyle().setProperty("min-width", "auto");
		
		AonTableButton newBtn = new AonTableButton("Nueva anotaci\u00f3n", AON.CSS.aonIconAdd());
		newBtn.addClickHandler(e -> {
			notes.add(
				new RegistryNote()
				.setDomain(officeDomain)
				.setNoteType(AonStringUtils.isBlank(notesSource) ? NoteType.MESSAGE : NoteType.safeValueOf(notesSource))
				.setNoteDate(new Date())
				.setRegistry(registryId)
				.setConfidential(false)
			);
			
			createNotes();
		});
		toolbar.add(newBtn);
		
		notesContent.add(toolbar);
		
		if(!messages.isEmpty()) {
			AonTableButton openBtn = new AonTableButton(
					messagesOpen ? "Cerrar anotaciones" : "Abrir anotaciones" , 
					messagesOpen ?  AON.CSS.aonIconUp() : AON.CSS.aonIconDown());
			toolbar.add(openBtn);
			
			HTMLPanel cardsPanel = new HTMLPanel(AonStringUtils.EMPTY);
			cardsPanel.setStyleName(AON.CSS.aonCustomerNotesCard());
			
			messages.forEach(message -> {
				notesOpen.put(message.getId(), true);
				
				AonTableButton openCardBtn = new AonTableButton(
						notesOpen.get(message.getId()) ? "Cerrar anotaci\u00f3n" : "Abrir anotaci\u00f3n" , 
						notesOpen.get(message.getId()) ?  AON.CSS.aonIconUp() : AON.CSS.aonIconDown());
				
				TextBox description = new TextBox();
				description.setValue(message.getDescription());
				description.setEnabled(!message.getNoteType().equals(NoteType.CUSTOMER_STATUS));
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
				comments.setEnabled(!message.getNoteType().equals(NoteType.CUSTOMER_STATUS));
				comments.setVisibleLines(4);
				comments.getElement().getStyle().setProperty("font-size", ".8rem");
				comments.getElement().getStyle().setProperty("font-weight", "500");
				comments.getElement().getStyle().setProperty("color", "#5f6368");
				comments.getElement().getStyle().setProperty("border", "none");
				comments.getElement().getStyle().setProperty("cursor", "pointer");
				comments.getElement().getStyle().setProperty("background", "transparent");
				comments.getElement().getStyle().setProperty("resize", "vertical");
				comments.getElement().setPropertyString("placeholder", "Escriba su nota...");	
				comments.addValueChangeHandler(e -> {
					message.setComments(comments.getValue());
					saveNote(message);
				});
				
				DateBoxEx noteDate = new DateBoxEx();
				noteDate.setValue(message.getNoteDate());
				noteDate.setEnabled(!message.getNoteType().equals(NoteType.CUSTOMER_STATUS));
				noteDate.getElement().getStyle().setProperty("font-size", ".8rem");
				noteDate.getElement().getStyle().setProperty("font-weight", "500");
				noteDate.getElement().getStyle().setProperty("color", "#5f6368");
				noteDate.getElement().getStyle().setProperty("border", "none");
				noteDate.getElement().getStyle().setProperty("cursor", "pointer");
				noteDate.getElement().getStyle().setProperty("background", "transparent");
				noteDate.getElement().getStyle().setProperty("width", "70px");
				noteDate.addValueChangeHandler(e -> {
					message.setNoteDate(noteDate.getValue());
					saveNote(message);
				});
				
				AonCustomCardSmall card = new AonCustomCardSmall(description, openCardBtn);
				card.setToolbarWidgetShown();
				
				HTMLPanel cardContentPanel = new HTMLPanel(AonStringUtils.EMPTY);
				cardContentPanel.addStyleName(AON.CSS.aonCustomerCardContent());
				
				cardContentPanel.add(comments);
				
				HTMLPanel footerPanel = new HTMLPanel(AonStringUtils.EMPTY);
				footerPanel.addStyleName(AON.CSS.aonItemFlex());
				footerPanel.addStyleName(AON.CSS.aonFlexBetween());
				
				HTMLPanel buttonsPanel = new HTMLPanel(AonStringUtils.EMPTY);
				buttonsPanel.addStyleName(AON.CSS.aonItemFlex());
				buttonsPanel.addStyleName(AON.CSS.aonCustomCardButton());
				footerPanel.add(buttonsPanel);
				
				AonTableButton deleteBtn = new AonTableButton("Borrar nota", AON.CSS.aonIconDelete());
				deleteBtn.setEnabled(!message.getNoteType().equals(NoteType.CUSTOMER_STATUS));
				deleteBtn.addClickHandler(e -> {
					e.stopPropagation();
					
					deleteNote(message, deleteBtn, delete -> deleteNote(message));
				});
				buttonsPanel.add(deleteBtn);
		
				if(message.getId() != null) {
					AonTableButton confidentialBtn = new AonTableButton(
							message.isConfidential() ? "Confidencial" : "Publico", 
							message.isConfidential() ? AON.CSS.aonIconNoEncryption() : AON.CSS.aonIconLock());
					
					confidentialBtn.setEnabled(!message.getNoteType().equals(NoteType.CUSTOMER_STATUS));
					
					confidentialBtn.addClickHandler(e -> {
						e.stopPropagation();
						message.setConfidential(!message.isConfidential());
						saveNote(message);
					});
					
					buttonsPanel.add(confidentialBtn);
				}
				
				HTMLPanel datePanel = new HTMLPanel(AonStringUtils.EMPTY);
				datePanel.addStyleName(AON.CSS.aonItemFlex());
				footerPanel.add(datePanel);
				
				if(message.isConfidential()) {
					AonTableButton confidentialIcon = new AonTableButton("Confidencial", AON.CSS.aonIconLock());
					// Hide on hover card
					// confidentialIcon.addStyleName(AON.CSS.aonCustomCardButtonHideOnHover());
					datePanel.add(confidentialIcon);
				}
				
				datePanel.add(noteDate);
				
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
				
				if(message.getId() == null)
					Scheduler.get().scheduleDeferred(() -> {
						description.setFocus(true);
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
	
	private void deleteNote(RegistryNote note, AonTableButton btn, Consumer<Void> deletion) {
		AonCustomDeleteTooltip aonCustomDeleteTooltip = new AonCustomDeleteTooltip(
				note.getNoteType().equals(NoteType.MESSAGE)
	            ? "Eliminar nota"
	            : "Eliminar observaci\u00f3n",
	            note.getNoteType().equals(NoteType.MESSAGE)
	            ? "\u00bfSeguro que quiere eliminar la nota?"
	            : "\u00bfSeguro que quiere eliminar la observaci\u00f3n?",
	            btn );
		aonCustomDeleteTooltip.addDeleteHandler(e -> deletion.accept(null));
	}

	private void getRegistryNotes(Consumer<List<RegistryNote>> success) {
		COMMON_SERVICE.getRegistryNotes(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), registryId,
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
				.setNoteType(AonStringUtils.isBlank(notesSource) ? NoteType.MESSAGE : NoteType.safeValueOf(notesSource))
				.setNoteDate(new Date())
				.setRegistry(registryId);
		
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
									onNoteSaved();
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
		// Protect against description empty
		if(AonStringUtils.isBlank(note.getDescription())) note.setDescription("Descripci\u00f3n");
		
		COMMON_SERVICE.saveNote(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), note,
			new AsyncCallback<RegistryNote>() {

				@Override
				public void onSuccess(RegistryNote result) {
					onSearch();
					onNoteSaved();
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
					onNoteSaved();
				}

				@Override
				public void onFailure(Throwable caught) { }
		});
	}
	
	// Send to JS client
	public static native void onNoteSaved() /*-{
		$wnd.top.postMessage(
		  { type: "REGISTRY_NOTE_SAVED", payload: {} },
		  "*"
		);
	}-*/;

}

package com.esferalia.aon.gwt.marketing.client.customer;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmall;
import com.esferalia.aon.gwt.marketing.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class CustomerNotesModule extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(CustomerNotesModule.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private static CommonServiceAsync COMMON_SERVICE;
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private HTMLPanel container;
	private HTMLPanel messagePanel;
	private ScrollPanel scrollPanel;
	private HTMLPanel content;

	private List<RegistryNote> notes;
	private Integer customerId;

	private boolean observationsOpen = true;
	private boolean messagesOpen = true;
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");

		AON.ensureInjected();
		
		initializeService();
		
		customerId = getCustomer() > 0 ? getCustomer() : null;
		
		container = new HTMLPanel(AonStringUtils.EMPTY);
		container.setStyleName(AON.CSS.aonFlexColumn());
		
		messagePanel = new HTMLPanel(AonStringUtils.EMPTY);
		
		content = new HTMLPanel(AonStringUtils.EMPTY);
		content.setStyleName(AON.CSS.aonFlexColumn());
		content.getElement().getStyle().setProperty("padding", "0 1rem");
		
		scrollPanel = new ScrollPanel(content);
		
		container.add(messagePanel);
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
			if(notes.isEmpty()) {
				Label emptyNotes = new Label("No existe notas");
				content.add(emptyNotes);
			} else
				createNotesPanel();
		});
	}

	private void createNotesPanel() {
		// Observation
		createObservations();
		
		// Notes
		createNotes();
		
		scrollPanel.setWidget(content);
	}
	
	private void createObservations() {
		List<RegistryNote> observations = notes.stream().filter(note -> note.getNoteType().equals(NoteType.OBSERVATION)).collect(Collectors.toList());
		if(!observations.isEmpty()) {
			AonToolbarSmall toolbar = new AonToolbarSmall("Observaciones");
			toolbar.getElement().getStyle().setProperty("min-width", "auto");
			AonTableButton openBtn = new AonTableButton(
					observationsOpen ? "Cerrar observaciones" : "Abrir observaciones" , 
					observationsOpen ?  AON.CSS.aonIconDown() : AON.CSS.aonIconLeft());
			toolbar.add(openBtn);
			
			HTMLPanel cardsPanel = new HTMLPanel(AonStringUtils.EMPTY);
			cardsPanel.addStyleName(AON.CSS.aonFlexColumn());
			
			observations.forEach(observation -> {
				AonCustomCard card = new AonCustomCard(observation.getDescription());
				
				HTMLPanel cardContentPanel = new HTMLPanel(AonStringUtils.EMPTY);
				cardContentPanel.addStyleName(AON.CSS.aonFlexColumn());
				
				cardContentPanel.add(new Label(observation.getComments()));
				cardContentPanel.add(new Label(null == observation.getNoteDate() ? "" : formatDate.format(observation.getNoteDate())));
				
				card.add(cardContentPanel);
				
				cardsPanel.add(card);
			});
			
			openBtn.addClickHandler(e -> {
				observationsOpen = !observationsOpen;
				cardsPanel.setVisible(observationsOpen);
				openBtn.setTitle(observationsOpen ? "Cerrar observaciones" : "Abrir observaciones");
				
				if(observationsOpen) {
					openBtn.removeStyleName(AON.CSS.aonIconLeft());
					openBtn.addStyleName(AON.CSS.aonIconDown());
				} else {
					openBtn.removeStyleName(AON.CSS.aonIconDown());
					openBtn.addStyleName(AON.CSS.aonIconLeft());
				}
				
			});
			
			content.add(toolbar);
			content.add(cardsPanel);
		}
	}
	
	private void createNotes() {
		List<RegistryNote> messages = notes.stream().filter(note -> note.getNoteType().equals(NoteType.MESSAGE)).collect(Collectors.toList());
		if(!messages.isEmpty()) {
			AonToolbarSmall toolbar = new AonToolbarSmall("Mensajes");
			toolbar.getElement().getStyle().setProperty("min-width", "auto");
			AonTableButton openBtn = new AonTableButton(
					messagesOpen ? "Cerrar mensajes" : "Abrir mensajes" , 
					messagesOpen ? AON.CSS.aonIconDown() : AON.CSS.aonIconLeft());
			toolbar.add(openBtn);
			
			HTMLPanel cardsPanel = new HTMLPanel(AonStringUtils.EMPTY);
			cardsPanel.addStyleName(AON.CSS.aonFlexColumn());
			
			messages.forEach(message -> {
				AonCustomCard card = new AonCustomCard(message.getDescription());
				
				HTMLPanel cardContentPanel = new HTMLPanel(AonStringUtils.EMPTY);
				cardContentPanel.addStyleName(AON.CSS.aonFlexColumn());
				
				cardContentPanel.add(new Label(message.getComments()));
				cardContentPanel.add(new Label(null == message.getNoteDate() ? "" : formatDate.format(message.getNoteDate())));
				
				card.add(cardContentPanel);
				
				cardsPanel.add(card);
			});
			
			openBtn.addClickHandler(e -> {
				messagesOpen = !messagesOpen;
				cardsPanel.setVisible(messagesOpen);
				openBtn.setTitle(messagesOpen ? "Cerrar mensajes" : "Abrir mensajes");
				
				if(messagesOpen) {
					openBtn.removeStyleName(AON.CSS.aonIconLeft());
					openBtn.addStyleName(AON.CSS.aonIconDown());
				} else {
					openBtn.removeStyleName(AON.CSS.aonIconDown());
					openBtn.addStyleName(AON.CSS.aonIconLeft());
				}
				
			});
			
			content.add(toolbar);
			content.add(cardsPanel);
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
				public void onFailure(Throwable caught) {
					AonMessagePanel.showError(messagePanel, "Notas cliente : " + caught.getMessage());
				}
		});
	}

}

package com.esferalia.aon.gwt.issues.client;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.AonUrlApi;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.JsEvent;
import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperIconButton;


public class IssuePanel extends Composite{
	
	interface Binder extends UiBinder<Widget, IssuePanel> {}
	
	private static Binder binder = GWT.create(Binder.class);

	@UiField VerticalPanel headerVPanel;
	@UiField Label userLogged;
	@UiField Label typeLabel;
	@UiField Label priorityLabel;
	@UiField VerticalPanel labelsVPanel;
	@UiField VerticalPanel usersVPanel;
	@UiField FlowPanel historialVPanel;
	@UiField Button sendButton;
	@UiField Button duplicatedButton;
	@UiField Button closedButton;
	@UiField Button commentButton;
	@UiField TextArea commentTextArea;
	@UiField Button typeButton;
	@UiField Button priorityButton;
	@UiField Button tagButton;
	@UiField Button userButton;

	@UiField SplitLayoutPanel dockLayoutPanel;
	@UiField MinimizePanel footPanel;
	
	@UiField PaperIconButton returnButton;

	Issues parent;
	private DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd/MM/yyyy");
	private DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private DateTimeFormat hourFormat = DateTimeFormat.getFormat("HH:mm");
	
	public IssuePanel(Issues parent, JsIssue issue) {
		initWidget(binder.createAndBindUi(this));		
		this.parent = parent;
		initHeader(issue);
	}
	
	private void initHeader(JsIssue issue) {
		headerVPanel.add(getTitleLabel(issue.getTitle(), issue.getNumber()));
		
		Incidence i = new Incidence(AonUrlApi.GITHUB, Issues.ACCESS_TOKEN, Issues.USER_NAME, Issues.ORG_NAME, Issues.REPO_NAME);
		i.getEvents(issue.getEventsUrl(), new AsyncCallback<JSON<JsEvent>>() {
			@Override
			public void onSuccess(JSON<JsEvent> result) {
				/* TODO PARA CUANDO SE PUEDA COMPILAR STREAM CON GWT 2.8 anObject
				Stream<JsEvent> eventStream = result.getData().toLinkedList().stream().filter(e -> 
					e.getEvent().equals("reopened") || e.getEvent().equals("closed"));
				headerVPanel.add(getStatusPanel(issue, eventStream));
				*/
				LinkedList<JsEvent> list = new LinkedList<JsEvent>();
				for (JsEvent e : result.getData().toLinkedList()) 
					if(e.getEvent().equals("reopened") || e.getEvent().equals("closed"))
						list.add(e);
				headerVPanel.add(getStatusPanel(issue, list));
				headerVPanel.add(getCompanyPanel(issue));
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	private Label getTitleLabel(String title, Integer id) {
		StringBuilder sb = new StringBuilder();
		sb.append(title.toUpperCase());
		sb.append(" #");
		sb.append(id);

		Label titleLabel = new Label(sb.toString());
		titleLabel.getElement().getStyle().setCursor(Cursor.POINTER);
		titleLabel.setStyleName(AON.AON_BOLD);
		titleLabel.addStyleName(AON.AON_CSS.headerTitle());

		return titleLabel;
	}
	
	private String getStateStyle(String state){
		if(state.equals("open")) return "color:green;position:absolute;";
		if(state.equals("closed")) return "color:red;position:absolute;";
		if(state.equals("reopened")) return "color:blue;position:absolute;";
		return "position:absolute;";		
	}
	
	/* TODO PARA CUANDO SE PUEDA COMPILAR STREAM CON GWT 2.8 anObject
	private Widget getStatusPanel(JsIssue issue, Stream<JsEvent> events) {
		if(events.count() > 0){
			//TODO ORDENAR LISTA POR FECHA DE MAS CERCANO A MAS LEJANO
			//TODO DISCLOSURE CON OPEN.. AL FINAL
		} else {
			HorizontalPanel h = new HorizontalPanel();
			IronIcon icon = new IronIcon();
			icon.setIcon("error-outline");
			icon.setStyle(getStateStyle(issue.getState()));
			h.add(icon);
			String state = issue.getState() + " por " + "USER" + "el" ;//+ dateFormat.format(issue.getcre) 
			h.add(new Label(state));
			issue.getState();
			//TODO  open ....
		}
		return new HorizontalPanel();
	}
	*/
	
	// TODO FUNCION TEMPORAL!!
	private Widget getStatusPanel(JsIssue issue, LinkedList<JsEvent> events) {
		if(events.isEmpty()){
			return getHistorialLogHeader(issue.getState(), issue.getUser().getLogin(), 
					dateTimeFormat.parse(issue.getCreatedAt()));
		} else {
			Collections.sort(events, (e1,e2) -> e1.getCreatedAt().compareTo(e2.getCreatedAt()));
			DisclosurePanel logPanel = new DisclosurePanel();
			logPanel.setAnimationEnabled(true);
			
			Label icon = new Label();
			icon.setStyleName(AON.AON_CSS.aonIconView());
			JsEvent event = events.get(0);
			HorizontalPanel hp = getHistorialLogHeader(event.getEvent(), event.getUser().getLogin(), 
					dateTimeFormat.parse(event.getCreatedAt()));
			hp.insert(icon, 0);
			logPanel.setHeader(hp);
			
			VerticalPanel vp = new VerticalPanel();
			for (Integer i = 1; i < events.size(); i++) {
				vp.add(getHistorialLogHeader(events.get(i).getEvent(), events.get(i).getUser().getLogin(),
						dateTimeFormat.parse(events.get(i).getCreatedAt())));
			}
			vp.add(getHistorialLogHeader(issue.getState(), issue.getUser().getLogin(), 
					dateTimeFormat.parse(issue.getCreatedAt())));
			logPanel.setContent(vp);
			headerVPanel.add(logPanel);
		}
		return new HorizontalPanel();
	}
	
	private HorizontalPanel getHistorialLogHeader(String state, String user, Date date) {
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(5);
		
		IronIcon icon = new IronIcon();
		icon.setIcon("error-outline");
		icon.setStyle(getStateStyle(state));
		hPanel.add(icon);

		Label stateLabel = new Label(state + " por ");
		stateLabel.getElement().getStyle().setPaddingLeft(20, Unit.PX);
		stateLabel.getElement().getStyle().setPaddingTop(5, Unit.PX);
		hPanel.add(stateLabel);

		Label ownLabel = new Label(user);
		ownLabel.getElement().getStyle().setPaddingTop(5, Unit.PX);
		ownLabel.setStyleName(AON.AON_BOLD);
		hPanel.add(ownLabel);

		Label dateLabel = new Label(" el " + dateFormat.format(date)
				+ " a las " + hourFormat.format(date));
		dateLabel.getElement().getStyle().setPaddingTop(5, Unit.PX);
		hPanel.add(dateLabel);

		return hPanel;
	}
	
	private Widget getCompanyPanel(JsIssue issue) {
		// TODO Notificada por: XXXXX el YYYYYY a las ZZZZZZ
		return new HorizontalPanel();
	}
	
	@UiHandler("returnButton")
	void onReturnButtonClick(ClickEvent event){
		//TODO
	}
	
	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}
	
	@UiHandler("footPanel")
	void onFootMaximize(MaximizeEvent event) {
		dockLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 2);
		dockLayoutPanel.animate(500);
	}
	
	private void closeFootPanel() {
		dockLayoutPanel.setWidgetSize(footPanel, 30);
		dockLayoutPanel.animate(500);
	}
	
}

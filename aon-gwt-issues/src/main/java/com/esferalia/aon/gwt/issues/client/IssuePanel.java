package com.esferalia.aon.gwt.issues.client;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.JsComment;
import com.esferalia.aon.gwt.api.client.incidence.JsEvent;
import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.esferalia.aon.gwt.api.client.incidence.JsLabel;
import com.esferalia.aon.gwt.api.client.incidence.JsUser;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesResources;
import com.esferalia.aon.occam.api.model.office.NotificationType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.vaadin.widget.VaadinComboBox;
import com.vaadin.polymer.vaadin.widget.event.ValueChangedEvent;
import com.vaadin.polymer.vaadin.widget.event.ValueChangedEventHandler;


public class IssuePanel extends Composite{
	
	interface Binder extends UiBinder<Widget, IssuePanel> {}
	
	private static Binder binder = GWT.create(Binder.class);

	public static final AonGwtIssuesCSS CSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();
	
	@UiField SimplePanel headerPanel;
	//@UiField VerticalPanel headerVPanel;
	@UiField Label userLogged;
	@UiField Label typeLabel;
	@UiField Label priorityLabel;
	@UiField VerticalPanel labelsVPanel;
	@UiField Label workgroupLabel;
	@UiField Label userLabel;
	@UiField VerticalPanel usersVPanel;
	@UiField FlowPanel historialVPanel;
	@UiField PaperIconButton sendButton;
	@UiField PaperIconButton removeIssueButton;
	@UiField PaperButton duplicatedButton;
	@UiField PaperButton closedButton;
	@UiField PaperButton reopenButton;
	@UiField PaperButton commentButton;
	@UiField TextArea commentTextArea;
	@UiField Button typeButton;
	@UiField Button priorityButton;
	@UiField Button tagButton;
	@UiField Button workgroupButton;	
	@UiField Button userButton;

	@UiField SplitLayoutPanel dockLayoutPanel;
	@UiField MinimizePanel footPanel;
	
	@UiField PaperIconButton returnButton;
	@UiField PaperIconButton userDeleteButton;
	@UiField PaperIconButton typeDeleteButton;
	@UiField PaperIconButton workgroupDeleteButton;

	Issues parent;
	private DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd/MM/yyyy");
	private DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private DateTimeFormat hourFormat = DateTimeFormat.getFormat("HH:mm");
	
	JsIssue issue;
	JsUser workgroup;
	
	private Incidence incidence; 

	public IssuePanel(Issues parent, Incidence incidence, JsIssue issue) {
		initWidget(binder.createAndBindUi(this));		
		this.incidence = incidence;
		this.parent = parent;
		this.issue = issue;
		this.workgroup = issue.getWorkgroup();
		userDeleteButton.setSize("22px", "22px");
		typeDeleteButton.setSize("22px", "22px");
		workgroupDeleteButton.setSize("22px", "22px");
		if(issue.getState().equals("open")){
			closedButton.setVisible(true);
			reopenButton.setVisible(false);
		}else if(issue.getState().equals("closed")){
			closedButton.setVisible(false);
			reopenButton.setVisible(true);
		}

		initHeader(issue);
		initLabels(issue);
		initComments(issue);
		initSendButton();
	}
	
	
	
	private void initHeader(JsIssue issue) {
		VerticalPanel vp = new VerticalPanel();
		vp.setSpacing(10);
		vp.add(getTitleLabel(issue.getTitle(), issue.getNumber()));
		incidence.getEvents(issue.getEventsUrl(), new AsyncCallback<JSON<JsEvent>>() {
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
				vp.add(getStatusPanel(vp, issue, list));
				vp.add(getCompanyPanel(issue));
				headerPanel.add(vp);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	private void initLabels(JsIssue issue){
		String notAssign = "Sin Asignar";
		typeLabel.setText(issue.getType().getName());
		if(issue.getType().getColor() != null && !issue.getType().getColor().equals(""))
			typeLabel.getElement().getStyle().setBackgroundColor("#"+issue.getType().getColor());
		if(issue.getType().getName().equals(notAssign)) {typeDeleteButton.setVisible(false);}
		priorityLabel.setText(issue.getPriority().getName());
		if(issue.getPriority().getColor() != null && !issue.getPriority().getColor().equals(""))
			priorityLabel.getElement().getStyle().setBackgroundColor("#"+issue.getPriority().getColor());

		for (JsLabel label : issue.getLabels().toLinkedList()){
			HorizontalPanel hp = new HorizontalPanel();
			Label l = new Label(label.getName());
			l.setStyleName(AON.AON_CSS.tagStyle());
			l.addStyleName(CSS.tagNoticeIssues());
			if(label.getColor() != null && !label.getColor().equals(""))
				l.getElement().getStyle().setBackgroundColor("#"+label.getColor());
			hp.add(l);
			PaperIconButton pib = new PaperIconButton();
			pib.setIcon("close");
			pib.setStyle("padding:3px !important;");
			pib.setSize("22px", "22px");
			pib.setTitle(labelsVPanel.getWidgetCount()+"");
			pib.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					incidence.deleteLabel2Issue(label.getName(), issue.getNumber(), new AsyncCallback<JsLabel>() {
						@Override public void onFailure(Throwable caught) {}
						@Override public void onSuccess(JsLabel result) {
							Integer index = Integer.parseInt(pib.getTitle());
							labelsVPanel.remove(index);
						}
					});
				}
			});
			hp.add(pib);
			labelsVPanel.add(hp);
		}
		workgroupLabel.setText(issue.getWorkgroup().getLogin());
		if(issue.getWorkgroup().getLogin().equals(notAssign)) {workgroupDeleteButton.setVisible(false);}
		userLabel.setText(issue.getAssignee().getLogin());
		if(issue.getAssignee().getLogin().equals(notAssign)) {userDeleteButton.setVisible(false);}
	}
	
	
	private void initComments(JsIssue issue){
		userLogged.setText(issue.getUser().getLogin());
		printNotification(issue);
		printDescription(issue);
		if(issue.getComments()>0){
			incidence.getComments(issue.getCommentsUrl(), new AsyncCallback<JSON<JsComment>>() {
				
				@Override
				public void onSuccess(JSON<JsComment> result) {
					for (JsComment comment : result.getData().toLinkedList())
						printComment(comment);
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		}
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
	private Widget getStatusPanel(VerticalPanel headerVPanel, JsIssue issue, LinkedList<JsEvent> events) {
		if(events.isEmpty()){
			return getHistorialLogHeader(issue.getState(), issue.getUser().getLogin(), 
					dateTimeFormat.parse(issue.getCreatedAt()));
		} else {
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
			vp.add(getHistorialLogHeader("open", issue.getUser().getLogin(), 
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
	
	private void printNotification(JsIssue issue){
		historialVPanel.add(getHeadNotificationLabel(issue));
	}
	
	private void printDescription(JsIssue issue) {
		 
		 
		int days = getDaysBefore(dateTimeFormat.parse(issue.getCreatedAt()));
		
		PaperIconButton editButton = new PaperIconButton();
		editButton.setIcon("create");
		editButton.setTitle("Editar descripci\u00f3n");
		editButton.setVisible(AonStringUtils.equals(userLogged.getText(), issue.getUser().getLogin()));
		
		final TextArea textArea = getTextArea(issue.getBody());
		textArea.addStyleName(AON.AON_CSS.aonNoborderTop());
		textArea.setName(String.valueOf(issue.getId()));
		editButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if(editButton.getIcon().equals("create")){
					editButton.setIcon("save");
					onEditCommentButtonClick(textArea);
				} else {
					editButton.setIcon("create");
					onAcceptEditDescriptionButtonClick(textArea);
				}
			}
		});
		editButton.setVisible(userLogged.getText().equals(issue.getUser().getLogin()));
		
		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("600px");
		vp.getElement().getStyle().setPaddingBottom(10, Unit.PX);
		HorizontalPanel hp = new HorizontalPanel();
		hp.setWidth("100%");
		hp.getElement().getStyle().setBorderWidth(1, Unit.PX);
		hp.getElement().getStyle().setBorderColor("#999");
		hp.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		hp.getElement().getStyle().setBackgroundColor("#ddd");
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp.add(getHeadDescriptionLabel(issue, days));
		hp.add(editButton);
		vp.add(hp);
		vp.add(textArea);
		historialVPanel.add(vp);
	}
	
	private void printComment(JsComment comment) {
		int days = getDaysBefore(dateTimeFormat.parse(comment.getCreatedAt()));

		PaperIconButton editButton = new PaperIconButton();
		editButton.setIcon("create");
		editButton.setTitle("Editar descripci\u00f3n");
		editButton.setVisible(AonStringUtils.equals(userLogged.getText(), issue.getUser().getLogin()));
		editButton.setTitle("Editar comentario");

		final TextArea textArea = getTextArea(comment.getBody());
		textArea.addStyleName(AON.AON_CSS.aonNoborderTop());
		textArea.setName(String.valueOf(comment.getId()));

		editButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if(editButton.getIcon().equals("create")){
					editButton.setIcon("save");
					onEditCommentButtonClick(textArea);
				} else {
					editButton.setIcon("create");
					onAcceptEditCommentButtonClick(comment, textArea);
				}
			}
		});

		editButton.setVisible(AonStringUtils.equals(userLogged.getText(), comment.getUser().getLogin()));

		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("600px");
		vp.getElement().getStyle().setPaddingBottom(10, Unit.PX);
		HorizontalPanel hp = new HorizontalPanel();
		hp.setWidth("100%");
		hp.getElement().getStyle().setBorderWidth(1, Unit.PX);
		hp.getElement().getStyle().setBorderColor("#999");
		hp.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		hp.getElement().getStyle().setBackgroundColor("#ddd");
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp.add(getHeadCommentLabel(comment, days));
		hp.add(editButton);
		vp.add(hp);
		vp.add(textArea);
		historialVPanel.add(vp);
	}
	
	private int getDaysBefore(Date date) {
		return CalendarUtil.getDaysBetween(date, new Date());
	}
	
	private TextArea getTextArea(String text) {
		TextArea textArea = new TextArea();
		textArea.setReadOnly(true);
		textArea.setVisibleLines(5);
		textArea.setCharacterWidth(10);
		textArea.setWidth("600px");
		textArea.setStylePrimaryName(AON.AON_CSS.textAreaStyle());
		textArea.setValue(text);
		return textArea;
	}
	
	private Label getHeadCommentLabel(JsComment comment, int days) {
		Date createdAt = dateTimeFormat.parse(comment.getCreatedAt());
		StringBuilder sb = new StringBuilder();
		sb.append("COMENTADO por ");
		sb.append(comment.getUser().getLogin());
		sb.append(" el ");
		sb.append(dateFormat.format(createdAt));
		sb.append(" a las ");
		sb.append(hourFormat.format(createdAt));
		sb.append(" (hace " + days
				+ ((days == 1) ? " d\u00EDa)" : " d\u00EDas)"));
		
		//if (AonStringUtils.isNotBlank(comment.getCompany()))
		//	sb.append( " ( " + comment.getCompany() + " )");

		Label label = new Label();
		label.setText(sb.toString());
		label.setStyleName(AON.AON_BOLD);
		label.getElement().getStyle().setPaddingLeft(10, Unit.PX);
		return label;
	}
	
	private Label getHeadDescriptionLabel(JsIssue comment, int days) {
		Date createdAt = dateTimeFormat.parse(comment.getCreatedAt());
		StringBuilder sb = new StringBuilder();
		sb.append("DESCRITO por ");
		sb.append(comment.getUser().getLogin());
		sb.append(" el ");
		sb.append(dateFormat.format(createdAt));
		sb.append(" a las ");
		sb.append(hourFormat.format(createdAt));
		sb.append(" (hace " + days
				+ ((days == 1) ? " d\u00EDa)" : " d\u00EDas)"));
		
		//if (AonStringUtils.isNotBlank(comment.getCompany()))
		//	sb.append( " ( " + comment.getCompany() + " )");

		Label label = new Label();
		label.setText(sb.toString());
		label.setStyleName(AON.AON_BOLD);
		label.getElement().getStyle().setPaddingLeft(10, Unit.PX);
		return label;
	}
	
	private Label getHeadNotificationLabel(JsIssue issue) {
		StringBuilder sb = new StringBuilder();
		sb.append("NOTIFICADA por ");
		sb.append(issue.getEnterprise().getLogin());
		
		Label label = new Label();
		label.setText(sb.toString());
		label.setStyleName(AON.AON_BOLD);
		label.getElement().getStyle().setPaddingBottom(10, Unit.PX);;
		return label;
	}
	
	@UiHandler("returnButton")
	void onReturnButtonClick(ClickEvent event){
		parent.contentDockLayoutPanel.removeFromParent();
		AonToolbar t = (AonToolbar)parent.toolbar.getWidget(0);
		t.setVisibleRefreshButton(true);
		parent.contentDockLayoutPanel = new DockLayoutPanel(Unit.PX);
		parent.contentDockLayoutPanel.addNorth(parent.searchContent, 85);
		parent.contentDockLayoutPanel.add(parent.content);
		parent.dockLayoutPanel.add(parent.contentDockLayoutPanel);
		parent.updateIssueList(parent.issueFilter, false);
	}
	
	private void onEditCommentButtonClick(final TextArea textArea) {
		textArea.setReadOnly(false);
		textArea.setFocus(true);
		textArea.selectAll();
	}
	
	private void onAcceptEditCommentButtonClick(JsComment comment, final TextArea textArea) {
		String request = "{\"body\":\""+ textArea.getText() +"\"}";

		incidence.updateComment(issue, comment, request, new AsyncCallback<JsComment>() {
			@Override
			public void onSuccess(JsComment result) {
				textArea.setValue(result.getBody());
				textArea.setReadOnly(true);			
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	private void onAcceptEditDescriptionButtonClick(final TextArea textArea) {
		String request = "{\"body\":\""+ textArea.getText() +"\"}";
		incidence.updateOrgIssue(issue, request, new AsyncCallback<JsIssue>() {
			
			@Override
			public void onSuccess(JsIssue result) {
				textArea.setValue(result.getBody());
				textArea.setReadOnly(true);							
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("commentButton")
	void onClickCommentButton(ClickEvent event){
		String request = "{\"body\":\""+ commentTextArea.getText() +"\"}";
		incidence.newComment(issue, request, new AsyncCallback<JsComment>() {
			
			@Override public void onSuccess(JsComment result) {
				commentTextArea.setText("");
				printComment(result);
				commentButton.setDisabled(true);
				parent.sendNotification(issue, NotificationType.NEW_INFO);
			}
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("commentTextArea")
	void onKeyUpEvent(KeyUpEvent event) {
		String value = commentTextArea.getValue().trim();
		commentButton.setDisabled(value.isEmpty());
	}
	
	@UiHandler("closedButton")
	void onClickClosedButton(ClickEvent event){
		String request = "{\"state\":\"closed\"}";
		incidence.updateOrgIssue(issue, request, new AsyncCallback<JsIssue>() {
			
			@Override
			public void onSuccess(JsIssue result) {
				headerPanel.getWidget().removeFromParent();
				initHeader(result);
				closedButton.setVisible(false);
				reopenButton.setVisible(true);
				parent.sendNotification(issue, NotificationType.CLOSE);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("removeIssueButton")
	void onClickRemoveIssueButton(ClickEvent event){
		String request = "{\"state\":\"deleted\"}";
		incidence.updateOrgIssue(issue, request, new AsyncCallback<JsIssue>() {
			
			@Override
			public void onSuccess(JsIssue result) {
				parent.contentDockLayoutPanel.removeFromParent();
				AonToolbar t = (AonToolbar)parent.toolbar.getWidget(0);
				t.setVisibleRefreshButton(true);
				parent.contentDockLayoutPanel = new DockLayoutPanel(Unit.PX);
				parent.contentDockLayoutPanel.addNorth(parent.searchContent, 85);
				parent.contentDockLayoutPanel.add(parent.content);
				parent.dockLayoutPanel.add(parent.contentDockLayoutPanel);
				parent.updateIssueList(parent.issueFilter, false);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("reopenButton")
	void onClickReopenButton(ClickEvent event){
		String request = "{\"state\":\"open\"}";
		incidence.updateOrgIssue(issue, request, new AsyncCallback<JsIssue>() {
			
			@Override
			public void onSuccess(JsIssue result) {
				headerPanel.getWidget().removeFromParent();
				initHeader(result);
				closedButton.setVisible(true);
				reopenButton.setVisible(false);
				parent.sendNotification(issue, NotificationType.REOPEN);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("typeButton")
	void onClickTypeButton(ClickEvent event){	
		incidence.getTypes(new AsyncCallback<JSON<JsLabel>>() {
			
			@Override public void onSuccess(JSON<JsLabel> result) {
				AonJsArray<JsLabel> labels = result.getData();
				PopupPanel popup = new PopupPanel();
				VaadinComboBox vcb = new VaadinComboBox();
				vcb.setItems(getLabelArray(labels));
				vcb.setLabel("Tipo");
				vcb.addValueChangedHandler(new ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(ValueChangedEvent event) {
						if(labels != null)
							for(Integer i = 0; i < labels.length(); i++)
								if(labels.get(i).getName().equals(vcb.getValue())) {
									JsLabel jsLabel = labels.get(i);
									typeLabel.setText(jsLabel.getName());
									if(jsLabel.getColor() != null && !jsLabel.getColor().equals(""))
										typeLabel.getElement().getStyle().setBackgroundColor("#"+jsLabel.getColor());
									typeDeleteButton.setVisible(true);
									incidence.addType2Issue(jsLabel.getName(), issue.getNumber(), new AsyncCallback<JsLabel>() {
										@Override public void onFailure(Throwable caught) {}
										@Override public void onSuccess(JsLabel result) {}
									});
									popup.hide();
								}	
					}
				});
				popup.add(vcb);
				int left = typeButton.getAbsoluteLeft();
				int top = typeButton.getAbsoluteTop()
						+ typeButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(vcb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				vcb.toggle();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});	
	}
	
	@UiHandler("priorityButton")
	void onClickPriorityButton(ClickEvent event){	
		
		incidence.getPriorities(new AsyncCallback<JSON<JsLabel>>() {
			
			@Override public void onSuccess(JSON<JsLabel> result) {
				AonJsArray<JsLabel> labels = result.getData();
				PopupPanel popup = new PopupPanel();
				VaadinComboBox vcb = new VaadinComboBox();
				vcb.setItems(getLabelArray(labels));
				vcb.setLabel("Prioridad");
				vcb.addValueChangedHandler(new ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(ValueChangedEvent event) {
						if(labels != null)
							for(Integer i = 0; i < labels.length(); i++)
								if(labels.get(i).getName().equals(vcb.getValue())) {
									JsLabel jsLabel = labels.get(i);
									priorityLabel.setText(jsLabel.getName());
									if(jsLabel.getColor() != null && !jsLabel.getColor().equals(""))
										priorityLabel.getElement().getStyle().setBackgroundColor("#"+jsLabel.getColor());
									incidence.addPriority2Issue(jsLabel.getName(), issue.getNumber(), new AsyncCallback<JsLabel>() {
										@Override public void onFailure(Throwable caught) {}
										@Override public void onSuccess(JsLabel result) {}
									});
									popup.hide();
								}	
					}
				});
				popup.add(vcb);
				int left = priorityButton.getAbsoluteLeft();
				int top = priorityButton.getAbsoluteTop()
						+ priorityButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(vcb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				vcb.toggle();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("tagButton")
	void onClickTagButton(ClickEvent event){	
		incidence.getLabels(new AsyncCallback<JSON<JsLabel>>() {
			
			@Override public void onSuccess(JSON<JsLabel> result) {
				AonJsArray<JsLabel> labels = result.getData();
				PopupPanel popup = new PopupPanel();
				VaadinComboBox vcb = new VaadinComboBox();
				vcb.setItems(getLabelArray(labels));
				vcb.setLabel("Etiqueta");
				vcb.addValueChangedHandler(new ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(ValueChangedEvent event) {
						if(labels != null)
							for(Integer i = 0; i < labels.length(); i++)
								if(labels.get(i).getName().equals(vcb.getValue())) {
									HorizontalPanel hp = new HorizontalPanel();
									JsLabel jsLabel = labels.get(i);
									Boolean bool = true;
									for(Integer j = 0; j < labelsVPanel.getWidgetCount(); j++){
										HorizontalPanel hh = (HorizontalPanel) labelsVPanel.getWidget(j);
										Label ll = (Label) hh.getWidget(0);
										if(ll.getText().equals(jsLabel.getName()))
											bool = false;
									}
									if(bool){
										Label l = new Label(jsLabel.getName());
										l.setStyleName(AON.AON_CSS.tagStyle());
										l.addStyleName(CSS.tagNoticeIssues());
										if(jsLabel.getColor() != null && !jsLabel.getColor().equals(""))
											l.getElement().getStyle().setBackgroundColor("#"+jsLabel.getColor());

										hp.add(l);
										PaperIconButton pib = new PaperIconButton();
										pib.setIcon("close");
										pib.setStyle("padding:3px !important;");
										pib.setSize("22px", "22px");
										pib.setTitle(labelsVPanel.getWidgetCount()+"");
										pib.addClickHandler(new ClickHandler() {
										
											@Override
											public void onClick(ClickEvent event) {
												incidence.deleteLabel2Issue(jsLabel.getName(), issue.getNumber(), new AsyncCallback<JsLabel>() {
													@Override public void onFailure(Throwable caught) {}
													@Override public void onSuccess(JsLabel result) {
														Integer index = Integer.parseInt(pib.getTitle());
														labelsVPanel.remove(index);
													}
												});
											}
										});
										hp.add(pib);
										labelsVPanel.add(hp);
										incidence.addLabel2Issue(jsLabel.getName(), issue.getNumber(), new AsyncCallback<JsLabel>() {
											@Override public void onFailure(Throwable caught) {}
											@Override public void onSuccess(JsLabel result) {}
										});
										popup.hide();
									} else popup.hide();
								}	
					}
				});
				popup.add(vcb);
				int left = tagButton.getAbsoluteLeft();
				int top = tagButton.getAbsoluteTop()
						+ tagButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(vcb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				vcb.toggle();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});	
	}
	
	@UiHandler("workgroupButton")
	void onClickWorkgroupButton(ClickEvent event){	
		incidence.getWorkgroups(new AsyncCallback<JSON<JsUser>>() {
			
			@Override public void onSuccess(JSON<JsUser> result) {
				AonJsArray<JsUser> users = result.getData();
				PopupPanel popup = new PopupPanel();
				VaadinComboBox vcb = new VaadinComboBox();
				vcb.setItems(getUserArray(users));
				vcb.setLabel("Grupo de Trabajo");
				vcb.addValueChangedHandler(new ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(ValueChangedEvent event) {
						if(users != null)
							for(Integer i = 0; i < users.length(); i++)
								if(users.get(i).getLogin().equals(vcb.getValue())) {
									JsUser jsUser = users.get(i);
									workgroup = jsUser;
									workgroupLabel.setText(jsUser.getLogin());
									workgroupDeleteButton.setVisible(true);
									incidence.addWorkgroup2Issue(jsUser.getId(), issue.getNumber(), new AsyncCallback<JsUser>() {
										@Override public void onFailure(Throwable caught) {}
										@Override public void onSuccess(JsUser result) {}
									});
									popup.hide();
								}	
					}
				});
				popup.add(vcb);
				int left = workgroupButton.getAbsoluteLeft();
				int top = workgroupButton.getAbsoluteTop()
						+ workgroupButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(vcb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				vcb.toggle();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}

	@UiHandler("userButton")
	void onClickUserButton(ClickEvent event){	
		incidence.getUsers(workgroup,new AsyncCallback<JSON<JsUser>>() {
			
			@Override
			public void onSuccess(JSON<JsUser> result) {
				AonJsArray<JsUser> users = result.getData();
				PopupPanel popup = new PopupPanel();
				VaadinComboBox vcb = new VaadinComboBox();
				vcb.setItems(getUserArray(users));
				vcb.setLabel("Operario");
				vcb.addValueChangedHandler(new ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(ValueChangedEvent event) {
						if(users != null)
							for(Integer i = 0; i < users.length(); i++)
								if(users.get(i).getLogin().equals(vcb.getValue())) {
									JsUser jsUser = users.get(i);
									userLabel.setText(jsUser.getLogin());
									userDeleteButton.setVisible(true);
									incidence.addUser2Issue(jsUser.getId(), issue.getNumber(), new AsyncCallback<JsUser>() {
										@Override public void onFailure(Throwable caught) {}
										@Override public void onSuccess(JsUser result) {}
									});
									popup.hide();
								}	
					}
				});
				popup.add(vcb);
				int left = userButton.getAbsoluteLeft();
				int top = userButton.getAbsoluteTop()
						+ userButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(vcb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				vcb.toggle();
			

			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("userDeleteButton")
	void onClickUserDeleteButton(ClickEvent event){		
		incidence.deleteUser2Issue(issue.getNumber(), new AsyncCallback<JsUser>() {
			@Override public void onFailure(Throwable caught) {}
			@Override public void onSuccess(JsUser result) {
				userLabel.setText("Sin Asignar");
				userDeleteButton.setVisible(false);
			}
		});
	}
	
	@UiHandler("typeDeleteButton")
	void onClickTypeDeleteButton(ClickEvent event){		
		incidence.deleteType2Issue(issue.getNumber(), new AsyncCallback<JsLabel>() {
			@Override public void onFailure(Throwable caught) {}
			@Override public void onSuccess(JsLabel result) {
				typeLabel.setText("Sin Asignar");
				typeDeleteButton.setVisible(false);
			}
		});
	}
	
	@UiHandler("workgroupDeleteButton")
	void onClickWorkgroupDeleteButton(ClickEvent event){	
		incidence.deleteWorkgroup2Issue(issue.getNumber(), new AsyncCallback<JsUser>() {
			@Override public void onFailure(Throwable caught) {}
			@Override public void onSuccess(JsUser result) {
				workgroup = null;
				workgroupLabel.setText("Sin Asignar");
				workgroupDeleteButton.setVisible(false);
			}
		});
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
	
	
	private String getLabelArray(AonJsArray<JsLabel> labels) {
		String arr= "[";
		if(labels != null)
			for(Integer i = 0; i < labels.length(); i++){
				if(i > 0) arr = arr + " , ";
				arr = arr + "\""+ labels.get(i).getName()+"\"";
		}
		return arr + "]";
	}
	
	private String getUserArray(AonJsArray<JsUser> users) {
		String arr= "[";
		if(users != null)
			for(Integer i = 0; i < users.length(); i++){
				if(i > 0) arr = arr + " , ";
				arr = arr + "\""+ users.get(i).getLogin()+"\"";
		}
		return arr + "]";
	}
	
	//----------------------- SEND NOTIFICATION 
	
	private void initSendButton(){
		sendButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				parent.sendNotification(issue, NotificationType.MANUAL);
			}
		});
	}

}

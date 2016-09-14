package com.esferalia.aon.gwt.issues.client;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;

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
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperIconButton;


public class IssuePanel extends Composite{
	
	interface Binder extends UiBinder<Widget, IssuePanel> {}
	
	private static Binder binder = GWT.create(Binder.class);

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
	@UiField Button sendButton;
	@UiField Button duplicatedButton;
	@UiField Button closedButton;
	@UiField Button reopenButton;
	@UiField Button commentButton;
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
	@UiField PaperIconButton priorityDeleteButton;
	@UiField PaperIconButton typeDeleteButton;
	@UiField PaperIconButton workgroupDeleteButton;

	private static final String HTTP = "http://";

	Issues parent;
	private DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd/MM/yyyy");
	private DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private DateTimeFormat hourFormat = DateTimeFormat.getFormat("HH:mm");
	
	JsIssue issue;
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	public String getUrl(){
		return "http://" + getCurrentDomainName() + "/";
	}
	
	private Incidence incidence; 
	
	public IssuePanel(Issues parent, JsIssue issue) {
		initWidget(binder.createAndBindUi(this));		
	
		incidence = new Incidence(HTTP+getCurrentDomainName()+"/", parent.aonData.getMd5(),
				parent.aonData.getLoggedUser(), parent.aonData.getLoggedUser(), getCurrentDomainName());

		this.parent = parent;
		this.issue = issue;
		userDeleteButton.setSize("22px", "22px");
		priorityDeleteButton.setSize("22px", "22px");
		typeDeleteButton.setSize("22px", "22px");
		workgroupDeleteButton.setSize("22px", "22px");
		if(issue.getState().equals("open")){
			closedButton.setVisible(true);
			reopenButton.setVisible(false);
		}else if(issue.getState().equals("closed")){
			closedButton.setVisible(false);
			reopenButton.setVisible(true);
		}

		VerticalPanel vp = new VerticalPanel();
		vp.setSpacing(10);
		initHeader(vp, issue);
		initLabels(issue);
		initComments(issue);
	}
	
	
	
	private void initHeader(VerticalPanel vp, JsIssue issue) {
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
		if(issue.getType().getName().equals(notAssign)) {typeDeleteButton.setVisible(false);}
		priorityLabel.setText(issue.getPriority().getName());
		if(issue.getPriority().getName().equals(notAssign)) {priorityDeleteButton.setVisible(false);}
		for (JsLabel label : issue.getLabels().toLinkedList()){
			HorizontalPanel hp = new HorizontalPanel();
			Label l = new Label(label.getName());
			l.setStyleName(AON.AON_CSS.tagStyle());
			l.addStyleName(AON.AON_CSS.tagNotice());
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
	
	private void printDescription(JsIssue issue) {
		int days = getDaysBefore(dateTimeFormat.parse(issue.getCreatedAt()));
		final Button editButton = new Button();
		editButton.setStyleName(AON.AON_ICON_EDIT_ADD);
		editButton.addStyleName(AON.AON_ICON_CMD_BUTTON);
		editButton.addStyleName(AON.AON_CSS.editButton());
		editButton.setTitle("Editar descripci\u00f3n");
		editButton.setVisible(AonStringUtils.equals(
				userLogged.getText(), issue.getUser().getLogin()));
		final TextArea textArea = getTextArea(issue.getBody());
		textArea.setName(String.valueOf(issue.getId()));
		editButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (editButton.getStyleName().contains(AON.AON_ICON_EDIT_ADD))
					onEditCommentButtonClick(textArea, editButton);
				else
					onAcceptEditDescriptionButtonClick(textArea, editButton);
			}
		});
		editButton.setVisible(userLogged.getText().equals(issue.getUser().getLogin()));
		FlexTable flexTable = new FlexTable();
		flexTable.setWidget(0, 0, getHeadDescriptionLabel(issue, days));
		flexTable.setWidget(0, 1, editButton);
		flexTable.getFlexCellFormatter().setColSpan(1, 0, 2);
		flexTable.setWidget(1, 0, textArea);
		historialVPanel.add(flexTable);
	}
	
	private void printComment(JsComment comment) {
		int days = getDaysBefore(dateTimeFormat.parse(comment.getCreatedAt()));

		final Button editButton = new Button();
		editButton.setStyleName(AON.AON_ICON_EDIT_ADD);
		editButton.addStyleName(AON.AON_ICON_CMD_BUTTON);
		editButton.addStyleName(AON.AON_CSS.editButton());
		editButton.setTitle("Editar comentario");
		editButton.setVisible(AonStringUtils.equals(
				userLogged.getText(), comment.getUser().getLogin()));

		final TextArea textArea = getTextArea(comment.getBody());

		textArea.setName(String.valueOf(comment.getId()));

		editButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (editButton.getStyleName().contains(AON.AON_ICON_EDIT_ADD))
					onEditCommentButtonClick(textArea, editButton);
				else
					onAcceptEditCommentButtonClick(comment, textArea, editButton);
			}
		});

		editButton.setVisible(AonStringUtils.equals(
				userLogged.getText(), comment.getUser().getLogin()));

		FlexTable flexTable = new FlexTable();
		flexTable.setWidget(0, 0, getHeadCommentLabel(comment, days));
		flexTable.setWidget(0, 1, editButton);
		flexTable.getFlexCellFormatter().setColSpan(1, 0, 2);
		flexTable.setWidget(1, 0, textArea);

		historialVPanel.add(flexTable);
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
		sb.append(" ");
		sb.append(hourFormat.format(createdAt));
		sb.append(" (hace " + days
				+ ((days == 1) ? " d\u00EDa)" : " d\u00EDas)"));
		
		//if (AonStringUtils.isNotBlank(comment.getCompany()))
		//	sb.append( " ( " + comment.getCompany() + " )");

		Label label = new Label();
		label.setText(sb.toString());
		label.setStyleName(AON.AON_BOLD);
		label.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
		return label;
	}
	
	private Label getHeadDescriptionLabel(JsIssue comment, int days) {
		Date createdAt = dateTimeFormat.parse(comment.getCreatedAt());
		StringBuilder sb = new StringBuilder();
		sb.append("DESCRITO por ");
		sb.append(comment.getUser().getLogin());
		sb.append(" el ");
		sb.append(dateFormat.format(createdAt));
		sb.append(" ");
		sb.append(hourFormat.format(createdAt));
		sb.append(" (hace " + days
				+ ((days == 1) ? " d\u00EDa)" : " d\u00EDas)"));
		
		//if (AonStringUtils.isNotBlank(comment.getCompany()))
		//	sb.append( " ( " + comment.getCompany() + " )");

		Label label = new Label();
		label.setText(sb.toString());
		label.setStyleName(AON.AON_BOLD);
		label.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
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
	}
	
	private void onEditCommentButtonClick(final TextArea textArea,
			final Button button) {
		button.removeStyleName(AON.AON_ICON_EDIT_ADD);
		button.addStyleName(AON.AON_ICON_ACCEPT);
		textArea.setReadOnly(false);
		textArea.setFocus(true);
		textArea.selectAll();

		textArea.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				int keyCode = event.getNativeKeyCode();
				if (keyCode == KeyCodes.KEY_ESCAPE)
					onCancelEditComment(textArea, button);
			}
		});
	}
	
	private void onCancelEditComment(TextArea textArea, Button button) {
		button.removeStyleName(AON.AON_ICON_ACCEPT);
		button.addStyleName(AON.AON_ICON_EDIT_ADD);
		textArea.setReadOnly(true);
		textArea.setFocus(false);
	}
	
	private void onAcceptEditCommentButtonClick(JsComment comment, final TextArea textArea,
			final Button button) {
		button.removeStyleName(AON.AON_ICON_ACCEPT);
		button.addStyleName(AON.AON_ICON_EDIT_ADD);
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
	
	private void onAcceptEditDescriptionButtonClick(final TextArea textArea,
			final Button button) {
		button.removeStyleName(AON.AON_ICON_ACCEPT);
		button.addStyleName(AON.AON_ICON_EDIT_ADD);
		
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
				commentButton.setEnabled(false);
			}
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("commentTextArea")
	void onKeyUpEvent(KeyUpEvent event) {
		String value = commentTextArea.getValue().trim();
		commentButton.setEnabled(!value.isEmpty());
	}
	
	@UiHandler("closedButton")
	void onClickClosedButton(ClickEvent event){
		String request = "{\"state\":\"closed\"}";
	
		incidence.updateOrgIssue(issue, request, new AsyncCallback<JsIssue>() {
			
			@Override
			public void onSuccess(JsIssue result) {
				headerPanel.getWidget().removeFromParent();
				VerticalPanel vp = new VerticalPanel();
				vp.setSpacing(10);
				initHeader(vp, result);
				closedButton.setVisible(false);
				reopenButton.setVisible(true);
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
				VerticalPanel vp = new VerticalPanel();
				vp.setSpacing(10);
				initHeader(vp, result);
				closedButton.setVisible(true);
				reopenButton.setVisible(false);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("typeButton")
	void onClickTypeButton(ClickEvent event){	
		incidence.getTypes( new AsyncCallback<JSON<JsLabel>>() {
			
			@Override
			public void onSuccess(JSON<JsLabel> result) {
				AonListDialog dialog = new AonListDialog(result.getData(), null) {

					@Override
					protected void onSelect(JavaScriptObject item) {
						JsLabel label = (JsLabel) item;
						typeLabel.setText(label.getName());
						typeDeleteButton.setVisible(true);
						hide();
						// TODO UPDATE - CREATE
						incidence.addType2Issue(label.getName(), issue.getNumber(), new AsyncCallback<JsLabel>() {
							@Override public void onFailure(Throwable caught) {}
							@Override public void onSuccess(JsLabel result) {}
						});
					}

					@Override
					protected void onFilter(String filter) {
						incidence.getTypes(filter, new AsyncCallback<JSON<JsLabel>>() {
							@Override
							public void onSuccess(JSON<JsLabel> result) { 
								updateLabels(result.getData());
							}
							
							@Override public void onFailure(Throwable caught) {}
						});
					}
				};
				
				int left = typeButton.getAbsoluteLeft();
				int top = typeButton.getAbsoluteTop()
						+ typeButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				dialog.setAutoHideEnabled(true);
				dialog.setPopupPosition(left, top);
				dialog.show();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("priorityButton")
	void onClickPriorityButton(ClickEvent event){				
		incidence.getPriorities(new AsyncCallback<JSON<JsLabel>>() {
			
			@Override
			public void onSuccess(JSON<JsLabel> result) {
				AonListDialog dialog = new AonListDialog(result.getData(), null) {

					@Override
					protected void onSelect(JavaScriptObject item) {
						JsLabel label = (JsLabel) item;
						priorityLabel.setText(label.getName());
						priorityDeleteButton.setVisible(true);
						hide();
						// TODO UPDATE - CREATE
						incidence.addPriority2Issue(label.getName(), issue.getNumber(), new AsyncCallback<JsLabel>() {
							@Override public void onFailure(Throwable caught) {}
							@Override public void onSuccess(JsLabel result) {}
						});
					}
					
					@Override
					protected void onFilter(String filter) {
						incidence.getPriorities(filter, new AsyncCallback<JSON<JsLabel>>() {
							@Override
							public void onSuccess(JSON<JsLabel> result) { 
								updateLabels(result.getData());
							}
							
							@Override public void onFailure(Throwable caught) {}
						});
					}
				};
				int left = priorityButton.getAbsoluteLeft();
				int top = priorityButton.getAbsoluteTop()
						+ priorityButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				dialog.setAutoHideEnabled(true);
				dialog.setPopupPosition(left, top);
				dialog.show();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("tagButton")
	void onClickTagButton(ClickEvent event){				
		incidence.getLabels(new AsyncCallback<JSON<JsLabel>>() {
			
			@Override
			public void onSuccess(JSON<JsLabel> result) {
				AonListDialog dialog = new AonListDialog(result.getData(), null) {

					@Override
					protected void onSelect(JavaScriptObject item) {
						HorizontalPanel hp = new HorizontalPanel();
						JsLabel label = (JsLabel) item;
						Label l = new Label(label.getName());
						l.setStyleName(AON.AON_CSS.tagStyle());
						l.addStyleName(AON.AON_CSS.tagNotice());
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
						hide();
						// TODO UPDATE - CREATE
						incidence.addLabel2Issue(label.getName(), issue.getNumber(), new AsyncCallback<JsLabel>() {
							@Override public void onFailure(Throwable caught) {}
							@Override public void onSuccess(JsLabel result) {}
						});
					}
					
					@Override
					protected void onFilter(String filter) {
						incidence.getPriorities(filter, new AsyncCallback<JSON<JsLabel>>() {
							@Override
							public void onSuccess(JSON<JsLabel> result) { 
								updateLabels(result.getData());
							}
							
							@Override public void onFailure(Throwable caught) {}
						});
					}
				};
				int left = tagButton.getAbsoluteLeft();
				int top = tagButton.getAbsoluteTop()
						+ tagButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				dialog.setAutoHideEnabled(true);
				dialog.setPopupPosition(left, top);
				dialog.show();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("workgroupButton")
	void onClickWorkgroupButton(ClickEvent event){		
		incidence.getWorkgroups(new AsyncCallback<JSON<JsUser>>() {
			
			@Override
			public void onSuccess(JSON<JsUser> result) {
				
				AonListDialog dialog = new AonListDialog(null, result.getData()) {

					@Override
					protected void onSelect(JavaScriptObject item) {
						JsUser jsUser = (JsUser) item;
						workgroupLabel.setText(jsUser.getLogin());
						workgroupDeleteButton.setVisible(true);
						// TODO UPDATE - CREATE
						incidence.addWorkgroup2Issue(jsUser.getId(), issue.getNumber(), new AsyncCallback<JsUser>() {
							@Override public void onFailure(Throwable caught) {}
							@Override public void onSuccess(JsUser result) {}
						});
						hide();
					}
					
					@Override
					protected void onFilter(String filter) {
						incidence.getWorkgroups(filter, new AsyncCallback<JSON<JsUser>>() {
							@Override
							public void onSuccess(JSON<JsUser> result) { 
								updateUsers(result.getData());
							}
							
							@Override public void onFailure(Throwable caught) {}
						});
					}
				};
				int left = workgroupButton.getAbsoluteLeft();
				int top = workgroupButton.getAbsoluteTop()
						+ workgroupButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				dialog.setAutoHideEnabled(true);
				dialog.setPopupPosition(left, top);
				dialog.show();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("userButton")
	void onClickUserButton(ClickEvent event){		
		incidence.getUsers(new AsyncCallback<JSON<JsUser>>() {
			
			@Override
			public void onSuccess(JSON<JsUser> result) {
				AonListDialog dialog = new AonListDialog(null, result.getData()) {

					@Override
					protected void onSelect(JavaScriptObject item) {
						JsUser jsUser = (JsUser) item;
						
						userLabel.setText(jsUser.getLogin());
						userDeleteButton.setVisible(true);
						// TODO UPDATE - CREATE
						incidence.addUser2Issue(jsUser.getId(), issue.getNumber(), new AsyncCallback<JsUser>() {
							@Override public void onFailure(Throwable caught) {}
							@Override public void onSuccess(JsUser result) {}
						});
						hide();

					}
					
					@Override
					protected void onFilter(String filter) {
						incidence.getUsers(filter, new AsyncCallback<JSON<JsUser>>() {
							@Override
							public void onSuccess(JSON<JsUser> result) { 
								updateUsers(result.getData());
							}
							
							@Override public void onFailure(Throwable caught) {}
						});
					}
				};
				
				int left = userButton.getAbsoluteLeft();
				int top = userButton.getAbsoluteTop()
						+ userButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				dialog.setAutoHideEnabled(true);
				dialog.setPopupPosition(left, top);
				dialog.show();
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
	
	@UiHandler("priorityDeleteButton")
	void onClickPriorityDeleteButton(ClickEvent event){		
		incidence.deletePriority2Issue(issue.getNumber(), new AsyncCallback<JsLabel>() {
			@Override public void onFailure(Throwable caught) {}
			@Override public void onSuccess(JsLabel result) {
				priorityLabel.setText("Sin Asignar");
				priorityDeleteButton.setVisible(false);
			}
		});
	}
	
	@UiHandler("workgroupDeleteButton")
	void onClickWorkgroupDeleteButton(ClickEvent event){		
		incidence.deleteWorkgroup2Issue(issue.getNumber(), new AsyncCallback<JsUser>() {
			@Override public void onFailure(Throwable caught) {}
			@Override public void onSuccess(JsUser result) {
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
	
}

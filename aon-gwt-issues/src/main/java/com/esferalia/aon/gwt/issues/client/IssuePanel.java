package com.esferalia.aon.gwt.issues.client;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.IssueFilter;
import com.esferalia.aon.gwt.api.client.incidence.JsComment;
import com.esferalia.aon.gwt.api.client.incidence.JsEvent;
import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.esferalia.aon.gwt.api.client.incidence.JsLabel;
import com.esferalia.aon.gwt.api.client.incidence.JsUser;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonToolbar;
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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperInput;

import net.aonsolutions.polymer.aon.widget.AonComboBox;


public class IssuePanel extends Composite{
	
	interface Binder extends UiBinder<Widget, IssuePanel> {}
	
	private static Binder binder = GWT.create(Binder.class);

	public static final AonGwtIssuesCSS CSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();
	
	@UiField SimplePanel headerPanel;
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
	@UiField PaperButton faqButton;
	@UiField PaperButton duplicatedButton;
	@UiField PaperButton desduplicatedButton;
	@UiField PaperButton closedButton;
	@UiField PaperButton reopenButton;
	@UiField PaperButton commentButton;
	@UiField TextArea commentTextArea;
	@UiField Button typeButton;
	@UiField Button priorityButton;
	@UiField Button tagButton;
	@UiField Button workgroupButton;	
	@UiField Button userButton;

	@UiField TabLayoutPanel tabLayout;
	@UiField SplitLayoutPanel dockLayoutPanel;
	@UiField MinimizePanel footPanel;
	
	@UiField PaperIconButton returnButton;
	@UiField PaperIconButton principalButton;
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
			faqButton.setVisible(false);
		}
		if(issue.isFaqItem()){
			commentButton.setVisible(false);
			closedButton.setVisible(false);
			reopenButton.setVisible(true);
			duplicatedButton.setVisible(false);
			desduplicatedButton.setVisible(false);
			faqButton.setVisible(false);
			
			principalButton.setVisible(true);
			removeIssueButton.setVisible(false);

			typeButton.setVisible(false);
			priorityButton.setVisible(false);
			tagButton.setVisible(false);
			workgroupButton.setVisible(false);
			userButton.setVisible(false);			
		} else if(issue.isPrincipalDuplicate()){
			commentButton.setVisible(true);
			duplicatedButton.setVisible(false);
			desduplicatedButton.setVisible(false);
			principalButton.setVisible(false);
			removeIssueButton.setVisible(false);
		}else if(issue.isDuplicate()){
			commentButton.setVisible(false);
			closedButton.setVisible(false);
			reopenButton.setVisible(false);
			duplicatedButton.setVisible(false);
			desduplicatedButton.setVisible(true);
			principalButton.setVisible(true);
			removeIssueButton.setVisible(false);
			
			typeButton.setVisible(false);
			priorityButton.setVisible(false);
			tagButton.setVisible(false);
			workgroupButton.setVisible(false);
			userButton.setVisible(false);
		} else if(issue.isFaq()){
			commentButton.setVisible(true);
			closedButton.setVisible(false);
			reopenButton.setVisible(false);
			duplicatedButton.setVisible(false);
			desduplicatedButton.setVisible(false);
			faqButton.setVisible(false);

			principalButton.setVisible(false);

			removeIssueButton.setVisible(false);

			priorityButton.setVisible(false);
			workgroupButton.setVisible(false);
			userButton.setVisible(false);
		} else {
			commentButton.setVisible(true);
			duplicatedButton.setVisible(true);
			desduplicatedButton.setVisible(false);
			principalButton.setVisible(false);
			removeIssueButton.setVisible(true);
		} 

		initHeader(issue);
		initLabels(issue);
		initComments(issue);
		initSendButton();
		
		if(issue.isPrincipalDuplicate() || issue.isDuplicate()){
			initDuplicates(issue);
		}
		
		if(issue.isFaq()){
			initFaqs(issue);
		}
	}
	
	private void initFaqs(JsIssue issue){		
		incidence.getDuplicateIssues(issue.getId(), new  AsyncCallback<JSON<JsIssue>>() {
			@Override
			public void onSuccess(JSON<JsIssue> result) {				
				IssueSelector is = new IssueSelector();
				is.getList().setItems(result.getData());
				is.getList().addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						JsIssue  jsIssue = is.getSelectedItem().cast();
						parent.contentDockLayoutPanel.removeFromParent();
						parent.contentDockLayoutPanel = new DockLayoutPanel(Unit.PX);
						parent.contentDockLayoutPanel.add(new IssuePanel(parent, incidence, jsIssue));
						parent.dockLayoutPanel.add(parent.contentDockLayoutPanel);	
					}
				});
				
				Label label = new Label("FAQs");
				label.addStyleName(AON.AON_CSS.aonIconInfo());
				label.addStyleName(AON.AON_CSS.aonPaddingRight());
				label.addStyleName(AON.AON_CSS.aonPaddingLeft20());
				ScrollPanel sp = new ScrollPanel();
				sp.add(is);
				tabLayout.add(sp, label);
				openFootPanel();
				tabLayout.selectTab(2);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	private void initDuplicates(JsIssue issue){		
		Integer id = issue.getParent();
		if(issue.isFaqItem() && issue.isPrincipalDuplicate())
			id = issue.getId();
		incidence.getDuplicateIssues(id, new  AsyncCallback<JSON<JsIssue>>() {
			@Override
			public void onSuccess(JSON<JsIssue> result) {				
				IssueSelector is = new IssueSelector();
				is.getList().setItems(result.getData());
				is.getList().addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						JsIssue  jsIssue = is.getSelectedItem().cast();
						parent.contentDockLayoutPanel.removeFromParent();
						parent.contentDockLayoutPanel = new DockLayoutPanel(Unit.PX);
						parent.contentDockLayoutPanel.add(new IssuePanel(parent, incidence, jsIssue));
						parent.dockLayoutPanel.add(parent.contentDockLayoutPanel);	
					}
				});
				
				Label label = new Label("Duplicados");
				label.addStyleName(AON.AON_CSS.aonIconInfo());
				label.addStyleName(AON.AON_CSS.aonPaddingRight());
				label.addStyleName(AON.AON_CSS.aonPaddingLeft20());
				ScrollPanel sp = new ScrollPanel();
				sp.add(is);
				tabLayout.add(sp, label);
				openFootPanel();
				tabLayout.selectTab(2);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	private void initHeader(JsIssue issue) {
		VerticalPanel vp = new VerticalPanel();
		vp.setSpacing(10);
		Label title = getTitleLabel(issue.getTitle(), issue.getNumber());
		title.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				PaperInput pi = new PaperInput();
				pi.setLabel("T\u00edtulo");
				pi.setValue(issue.getTitle());
				AonDialog2 dialog = new AonDialog2("Editar T\u00edtulo", pi) {
					
					@Override protected void onCancel() {hide();}
				
					@Override
					protected void onAccept() {					
						String request = "{\"title\":\""+ pi.getValue() +"\"}";
						incidence.updateOrgIssue(issue, request, new AsyncCallback<JsIssue>() {
							
							@Override
							public void onSuccess(JsIssue result) {
								PaperInput pi = (PaperInput) content.getWidget(0);
								Label l = (Label) vp.getWidget(0);
								l.setText(pi.getValue()+ " #"+issue.getNumber());
							}
							
							@Override public void onFailure(Throwable caught) {}
						});	
						hide();
					}
				};
				dialog.center();
			}
		});
		vp.add(title);
		if(!issue.isDuplicate()){
			incidence.getEvents(issue.getEventsUrl(), new AsyncCallback<JSON<JsEvent>>() {
				@Override
				public void onSuccess(JSON<JsEvent> result) {
					LinkedList<JsEvent> list = new LinkedList<JsEvent>();
					for (JsEvent e : result.getData().toLinkedList()) 
						if(e.getEvent().equals("reopened") || e.getEvent().equals("closed"))
							list.add(e);
					vp.add(getStatusPanel(vp, issue, list));
					headerPanel.add(vp);
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		} else headerPanel.add(vp);
	}
	
	private void initLabels(JsIssue issue){
		String notAssign = "Sin Asignar";
		typeLabel.setText(issue.getType().getName());
		typeDeleteButton.setVisible(!issue.isDuplicate() && !issue.isFaqItem());
		if(issue.getType().getColor() != null && !issue.getType().getColor().equals(""))
			typeLabel.getElement().getStyle().setBackgroundColor("#"+issue.getType().getColor());
		if(issue.getType().getName().equals(notAssign)) {
			typeDeleteButton.setVisible(false);
			typeLabel.getElement().getStyle().setBackgroundColor("#ddd");
		}
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
			pib.setVisible(!issue.isDuplicate() && !issue.isFaqItem());
			hp.add(pib);
			labelsVPanel.add(hp);
		}
		workgroupLabel.setText(issue.getWorkgroup().getLogin());
		workgroupDeleteButton.setVisible(!issue.isDuplicate());
		if(issue.getWorkgroup().getLogin().equals(notAssign)) {
			workgroupDeleteButton.setVisible(false);
			workgroupLabel.getElement().getStyle().setBackgroundColor("#ddd");
		}
		userLabel.setText(issue.getAssignee().getLogin());
		userDeleteButton.setVisible(!issue.isDuplicate());
		if(issue.getAssignee().getLogin().equals(notAssign)) {
			userDeleteButton.setVisible(false);
			userLabel.getElement().getStyle().setBackgroundColor("#ddd");
		}

	}
	
	
	private void initComments(JsIssue issue){
		userLogged.setText(issue.getUser().getLogin());
		if(!issue.isFaq()) printNotification(issue);
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
		if(issue.isDuplicate() || issue.isFaqItem()) commentTextArea.setVisible(false);
		else commentTextArea.setVisible(true);
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
	
	private String getStateStyle(String state, Boolean dup, Boolean faq){
		if(faq) return "color:blue;position:absolute;";
		if(dup && (state.equals("open") || state.equals("reopened"))) 
			return "color:red;position:absolute;";
		if(state.equals("open")) return "color:green;position:absolute;";
		if(state.equals("closed")) return "color:black;position:absolute;";
		if(state.equals("reopened")) return "color:purple;position:absolute;";
		return "position:absolute;";		
	}
	
	private Widget getStatusPanel(VerticalPanel headerVPanel, JsIssue issue, LinkedList<JsEvent> events) {
		if(events.isEmpty()){
			return getHistorialLogHeader(issue.getState(), issue.getUser().getLogin(), 
					dateTimeFormat.parse(issue.getCreatedAt()), issue.isPrincipalDuplicate(), issue.isFaq());
		} else {
			DisclosurePanel logPanel = new DisclosurePanel();
			logPanel.setAnimationEnabled(true);
			Label icon = new Label();
			icon.setStyleName(AON.AON_CSS.aonIconView());
			JsEvent event = events.get(0);
			HorizontalPanel hp = getHistorialLogHeader(event.getEvent(), event.getUser().getLogin(), 
					dateTimeFormat.parse(event.getCreatedAt()), issue.isPrincipalDuplicate(), issue.isFaqItem());
			hp.insert(icon, 0);
			logPanel.setHeader(hp);
			VerticalPanel vp = new VerticalPanel();
			for (Integer i = 1; i < events.size(); i++) {
				vp.add(getHistorialLogHeader(events.get(i).getEvent(), events.get(i).getUser().getLogin(),
						dateTimeFormat.parse(events.get(i).getCreatedAt()), false, false));
			}
			vp.add(getHistorialLogHeader("open", issue.getUser().getLogin(), 
					dateTimeFormat.parse(issue.getCreatedAt()), false, false));
			logPanel.setContent(vp);
			headerVPanel.add(logPanel);
		}
		return new HorizontalPanel();
	}
	
	private HorizontalPanel getHistorialLogHeader(String state, String user, Date date, Boolean dup, Boolean  faq) {
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(5);
		
		IronIcon icon = new IronIcon();
		icon.setIcon("error-outline");
		icon.setStyle(getStateStyle(state, dup, faq));
		hPanel.add(icon);
		
		Label stateLabel = new Label(state + " por ");
		if(faq) stateLabel.setText(" (FAQ) " + state + " por ");
		else if(dup) stateLabel.setText(" (DUPLICADO) " + state + " por ");
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
	
	private void printNotification(JsIssue issue){
		historialVPanel.add(getHeadNotificationLabel(issue));
	}
	
	private void printDescription(JsIssue issue) {
		 
		 
		int days = getDaysBefore(dateTimeFormat.parse(issue.getCreatedAt()));
		
		PaperIconButton editButton = new PaperIconButton();
		editButton.setIcon("create");
		editButton.setTitle("Editar descripci\u00f3n");		
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
		editButton.setVisible(!issue.isFaqItem() && !issue.isDuplicate() && userLogged.getText().equals(issue.getUser().getLogin()));

		PaperIconButton lockButton = new PaperIconButton();
		lockButton.setIcon("lock");
		lockButton.setVisible(issue.isFaqItem() || issue.isDuplicate() || !userLogged.getText().equals(issue.getUser().getLogin()));
		
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
		hp.add(lockButton);
		vp.add(hp);
		vp.add(textArea);
		historialVPanel.add(vp);
	}
	
	private void printComment(JsComment comment) {
		int days = getDaysBefore(dateTimeFormat.parse(comment.getCreatedAt()));

		PaperIconButton editButton = new PaperIconButton();
		editButton.setIcon("create");
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

		editButton.setVisible(!issue.isFaqItem() && !issue.isDuplicate() && AonStringUtils.equals(userLogged.getText(), comment.getUser().getLogin()));
		
		PaperIconButton lockButton = new PaperIconButton();
		lockButton.setIcon("lock");
		lockButton.setVisible(issue.isFaqItem() || issue.isDuplicate() || !userLogged.getText().equals(issue.getUser().getLogin()));
		
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
		hp.add(lockButton);
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
		String str = checkString(commentTextArea.getText());

		String request = "{\"body\":\""+ str +"\"}";
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
				faqButton.setVisible(false);
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
				parent.sendNotification(issue, NotificationType.REOPEN);

				parent.contentDockLayoutPanel.removeFromParent();
				parent.contentDockLayoutPanel = new DockLayoutPanel(Unit.PX);
				parent.contentDockLayoutPanel.add(new IssuePanel(parent, incidence, result));
				parent.dockLayoutPanel.add(parent.contentDockLayoutPanel);		
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
				AonComboBox acb = new AonComboBox();
				acb.setItems(labels);
				acb.setItemLabelPath("name");
				acb.setLabel("Tipo");
				acb.addValueChangedHandler(new net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent event) {
						JsLabel jsLabel = acb.getSelectedItem().cast();
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
				});
				popup.add(acb);
				int left = typeButton.getAbsoluteLeft();
				int top = typeButton.getAbsoluteTop()
						+ typeButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(acb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				acb.open();
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
				AonComboBox acb = new AonComboBox();
				acb.setItems(labels);
				acb.setItemLabelPath("name");
				acb.setLabel("Prioridad");
				acb.addValueChangedHandler(new net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent event) {
						JsLabel jsLabel = acb.getSelectedItem().cast();
						priorityLabel.setText(jsLabel.getName());
						if(jsLabel.getColor() != null && !jsLabel.getColor().equals(""))
							priorityLabel.getElement().getStyle().setBackgroundColor("#"+jsLabel.getColor());
						incidence.addPriority2Issue(jsLabel.getName(), issue.getNumber(), new AsyncCallback<JsLabel>() {
							@Override public void onFailure(Throwable caught) {}
							@Override public void onSuccess(JsLabel result) {}
						});
						popup.hide();	
					}	
				});
				
				popup.add(acb);
				int left = priorityButton.getAbsoluteLeft();
				int top = priorityButton.getAbsoluteTop()
						+ priorityButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(acb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				acb.open();
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
				AonComboBox acb = new AonComboBox();
				acb.setItems(labels);
				acb.setItemLabelPath("name");
				acb.setLabel("Etiqueta");
				acb.addValueChangedHandler(new net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent event) {
						HorizontalPanel hp = new HorizontalPanel();
						JsLabel jsLabel = acb.getSelectedItem().cast();
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
				});
				popup.add(acb);
				int left = tagButton.getAbsoluteLeft();
				int top = tagButton.getAbsoluteTop()
						+ tagButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(acb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				acb.open();
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
				AonComboBox acb = new AonComboBox();
				acb.setItems(users);
				acb.setItemLabelPath("login");
				acb.setLabel("Grupo de Trabajo");
				acb.addValueChangedHandler(new net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent event) {
						JsUser jsUser = acb.getSelectedItem().cast();
						workgroup = jsUser;
						workgroupLabel.setText(jsUser.getLogin());
						workgroupLabel.getElement().getStyle().setBackgroundColor("#ff704d");
						workgroupDeleteButton.setVisible(true);
						incidence.addWorkgroup2Issue(jsUser.getId(), issue.getNumber(), new AsyncCallback<JsUser>() {
							@Override public void onFailure(Throwable caught) {}
							@Override public void onSuccess(JsUser result) {}
						});
						popup.hide();					
					}
				});
				popup.add(acb);
				int left = workgroupButton.getAbsoluteLeft();
				int top = workgroupButton.getAbsoluteTop()
						+ workgroupButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(acb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				acb.open();
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
				AonComboBox acb = new AonComboBox();
				acb.setItemLabelPath("login");
				acb.setItems(users);
				acb.setLabel("Operario");
				acb.addValueChangedHandler(new net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent event) {
						JsUser jsUser = acb.getSelectedItem().cast();
						userLabel.setText(jsUser.getLogin());
						userLabel.getElement().getStyle().setBackgroundColor("#a30c51");
						userDeleteButton.setVisible(true);
						incidence.addUser2Issue(jsUser.getId(), issue.getNumber(), new AsyncCallback<JsUser>() {
							@Override public void onFailure(Throwable caught) {}
							@Override public void onSuccess(JsUser result) {}
						});
						popup.hide();
					}
				});
				popup.add(acb);
				int left = userButton.getAbsoluteLeft();
				int top = userButton.getAbsoluteTop()
						+ userButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(acb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				acb.open();
			

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
				userLabel.getElement().getStyle().setBackgroundColor("#ddd");
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
				typeLabel.getElement().getStyle().setBackgroundColor("#ddd");
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
				workgroupLabel.getElement().getStyle().setBackgroundColor("#ddd");
				workgroupDeleteButton.setVisible(false);
			}
		});
	}
	
	Boolean more;
	@UiHandler("duplicatedButton")
	void onClickDuplicatedButton(ClickEvent event){
		IssueFilter issueFilter = new IssueFilter();
		issueFilter.setPage(1);issueFilter.setPerPage(30);
		incidence.getLightIssues(issue.getId() ,issueFilter, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				IssueSelector is = new IssueSelector(incidence, issue, issueFilter, "dup", result.getData());
				is.setHeight("400px");
				is.setWidth("500px");

				AonDialog2 dialog = new AonDialog2("Asignar a ", is) {
					
					@Override protected void onCancel() {hide();}
					
					@Override
					protected void onAccept() {
						JsIssue pIssue = is.getSelectedItem();
						String request = "{\"duplicate\":"+pIssue.getId()+"}";
						incidence.updateOrgIssue(issue, request, new AsyncCallback<JsIssue>() {
							
							@Override
							public void onSuccess(JsIssue result) {	
								parent.contentDockLayoutPanel.removeFromParent();
								parent.contentDockLayoutPanel = new DockLayoutPanel(Unit.PX);
								parent.contentDockLayoutPanel.add(new IssuePanel(parent, incidence, result));
								parent.dockLayoutPanel.add(parent.contentDockLayoutPanel);		
							}
							
							@Override public void onFailure(Throwable caught) {}
						});
						hide();
					}
				};
				dialog.center();
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}

	
	@UiHandler("faqButton")
	void onClickFaqButton(ClickEvent event){
		IssueFilter issueFilter = new IssueFilter();
		issueFilter.setState("faq");
		issueFilter.setPage(1);issueFilter.setPerPage(30);
		incidence.getFaqIssues(issueFilter, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				IssueSelector is = new IssueSelector(incidence, issue, issueFilter,"faq", result.getData());
				is.setHeight("400px");
				is.setWidth("500px");
				AonDialog2 dialog = new AonDialog2("Asignar a ", is) {
					
					@Override protected void onCancel() {hide();}
					
					@Override
					protected void onAccept() {
						JsIssue pIssue = is.getSelectedItem();
						String request = "{\"faq\":"+pIssue.getId()+"}";
						incidence.updateOrgIssue(issue, request, new AsyncCallback<JsIssue>() {
							
							@Override
							public void onSuccess(JsIssue result) {	
								parent.contentDockLayoutPanel.removeFromParent();
								parent.contentDockLayoutPanel = new DockLayoutPanel(Unit.PX);
								parent.contentDockLayoutPanel.add(new IssuePanel(parent, incidence, result));
								parent.dockLayoutPanel.add(parent.contentDockLayoutPanel);		
							}
							
							@Override public void onFailure(Throwable caught) {}
						});
						hide();
					}
				};
				dialog.center();
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("desduplicatedButton")
	void onClickDesduplicatedButton(ClickEvent event){
		String request = "{\"duplicate\":\"liberate\"}";
		incidence.updateOrgIssue(issue, request, new AsyncCallback<JsIssue>() {
			
			@Override
			public void onSuccess(JsIssue result) {	
				parent.contentDockLayoutPanel.removeFromParent();
				parent.contentDockLayoutPanel = new DockLayoutPanel(Unit.PX);
				parent.contentDockLayoutPanel.add(new IssuePanel(parent, incidence, result));
				parent.dockLayoutPanel.add(parent.contentDockLayoutPanel);		
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("principalButton")
	void onClickPrincipalButton(ClickEvent event){
		Integer parentId = issue.getParent();
		incidence.getTask(parentId, new AsyncCallback<JSON<JsIssue>>() {
	
			@Override 
			public void onSuccess(JSON<JsIssue> result) {
				JsIssue issue = result.getBData();

				parent.contentDockLayoutPanel.removeFromParent();
				parent.contentDockLayoutPanel = new DockLayoutPanel(Unit.PX);
				parent.contentDockLayoutPanel.add(new IssuePanel(parent, incidence, issue));
				parent.dockLayoutPanel.add(parent.contentDockLayoutPanel);	
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	
	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}
	
	@UiHandler("footPanel")
	void onFootMaximize(MaximizeEvent event) {
		openFootPanel();
	}
	
	private void openFootPanel() {
		dockLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 3);
		dockLayoutPanel.animate(500);
	}
	
	private void closeFootPanel() {
		dockLayoutPanel.setWidgetSize(footPanel, 30);
		dockLayoutPanel.animate(500);
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
	
	private String checkString(String str) {
		String[] array = str.split("\"");
		String s = array[0];
		for(Integer i = 1; i < array.length; i++){
			s = s + "\\\"" + array[i];
		}
		if(str.substring(str.length()-1).equals("\""))
			s = s + "\\\"";
		return s;
	}

}

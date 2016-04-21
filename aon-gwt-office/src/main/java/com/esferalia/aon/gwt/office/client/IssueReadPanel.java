package com.esferalia.aon.gwt.office.client;

import static com.esferalia.aon.gwt.common.client.AON.AONHUB;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.FilterDialog;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.shared.Constants;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class IssueReadPanel extends Composite implements ClickHandler {

	interface Callback<T> {

		void onSucess(T comment);

		void onFailure(Throwable caught);
	}

	interface Listener {

		void onUpdateBody(String title, String body,
				Callback<IssueSelected> callback);

		void onDuplicateClickEvent();

		void onUpdateState(String state);

		void onUpdateComment(Integer id, String body,
				Callback<AonIssueComments> callback);

		void onUpdateTitle(String title, Callback<IssueSelected> callback);

		void onIssueCommentButtonClick(String body,
				Callback<AonIssueComments> callback);

		void onRemoveLabelFromIssue(String oldName, String newName,
				Callback<AonTagIssueSelected> callback);

		void onReplaceLabelsForIssue(List<AonTagIssueSelected> addLabels,
				List<AonTagIssueSelected> deletedLabels);

		void addLabelToAnIssue(List<String> labels);
		
		void onSendNotificationButtonClick();
		
//		void onCompanyShowInfoClick(String name, Callback<RegistryMedia> callback);
	}
	
	private static IssueReadPanelUiBinder uiBinder = GWT
			.create(IssueReadPanelUiBinder.class);

	interface IssueReadPanelUiBinder extends UiBinder<Widget, IssueReadPanel> {
	}

	@UiField
	VerticalPanel headerVPanel;
	@UiField
	Label userLogged;
	@UiField
	Label typeLabel;
	@UiField
	Label priorityLabel;

	@UiField
	VerticalPanel labelsVPanel;

	@UiField
	FlowPanel historialVPanel;

	@UiField Button sendButton;
	
	@UiField
	Button duplicatedButton;
	@UiField
	Button closedButton;
	@UiField
	Button commentButton;

	@UiField
	TextArea commentTextArea;

	@UiField
	Button typeButton;
	@UiField
	Button priorityButton;
	@UiField
	Button tagButton;
	
	@UiField
	SplitLayoutPanel dockLayoutPanel;

	@UiField
	MinimizePanel footPanel;
	
	private Label titleLabel;
	
	private boolean canEdit;

	private List<Listener> listeners;
	private IssueSelected issue;

	private String type;
	private String priority;

	private DateTimeFormat date = DateTimeFormat.getFormat("dd/MM/yyyy");
	private DateTimeFormat hour = DateTimeFormat.getFormat("HH:mm");

	private List<AonTagIssueSelected> assignTags;
	private List<AonTagIssueSelected> addTagsMap;
	private List<AonTagIssueSelected> deletedTagsMap;

	private VerticalPanel infoHeaderContent;
	private VerticalPanel companyContainer;

	public IssueReadPanel(User currentUser, IssueSelected issue) {
		initWidget(uiBinder.createAndBindUi(this));		
		
		this.listeners = new LinkedList<Listener>();
		this.issue = issue;
		this.type = issue.getType();
		this.priority = issue.getPriority();
		this.infoHeaderContent = new VerticalPanel();		
		this.infoHeaderContent.setSpacing(5);

		this.companyContainer = new VerticalPanel();		
		this.companyContainer.setSpacing(5);

		this.addTagsMap = new LinkedList<AonTagIssueSelected>();
		this.deletedTagsMap = new LinkedList<AonTagIssueSelected>();
		this.assignTags = issue.getTags();

		this.userLogged.setText(currentUser.getName());
		this.canEdit = issue instanceof IssueGrid.IssueOpenLoadSelected;
		
		initHeader(issue);
		
		for (AonIssueComments comment : issue.getComments()) {
			printComment(comment);
		}

		if (issue instanceof IssueGrid.IssueOpenLoadSelected) {
			createSendButton();
			createDuplicatedButton();
			createClosedButton();
			createCommentButton();
		}

		else if (issue instanceof IssueGrid.IssueClosedLoadSelected) {
			commentButton.setVisible(false);
			commentTextArea.setVisible(false);
			duplicatedButton.setVisible(false);

			createReopenButton();
		}
		
		else if (issue instanceof IssueGrid.IssueFAQLoadSelected) {
			duplicatedButton.setVisible(false);
			closedButton.setVisible(false);
			createCommentButton();
		}
	}

	public void setTags(List<AonTagIssueSelected> tags) {

		List<AonTagIssueSelected> typesList = new LinkedList<AonTagIssueSelected>();
		List<AonTagIssueSelected> priorityList = new LinkedList<AonTagIssueSelected>();
		List<AonTagIssueSelected> officeList = new LinkedList<AonTagIssueSelected>();

		for (AonTagIssueSelected tag : tags) {
			
			if (tag.isOfficeType()) {
				typesList.add(tag);
			}

			else if (tag.isOfficePriority()) {
				priorityList.add(tag);
			}

			else if (tag.isOfficeNotice())
				officeList.add(tag);
		}

		initTypeButton(typesList);
		initPriorityButton(priorityList);
		initTagButton(officeList);

	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	@UiHandler("commentTextArea")
	void onKeyUpEvent(KeyUpEvent event) {
		String value = commentTextArea.getValue().trim();
		commentButton.setEnabled(!value.isEmpty());
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
	private void printComment(AonIssueComments issueComment) {

		int days = getDaysBefore(issueComment.getCreatedAt());

		final Button editButton = new Button();
		editButton.setStyleName(AON.AON_ICON_EDIT_ADD);
		editButton.addStyleName(AON.AON_ICON_CMD_BUTTON);
		editButton.addStyleName(AON.AON_CSS.editButton());
		editButton.setTitle("Editar comentario");
		editButton.setVisible(canEdit && AonStringUtils.equals(
				userLogged.getText(), issueComment.getUser().getName()));

		final TextArea textArea = getTextArea(issueComment.getBody());

		textArea.setName(String.valueOf(issueComment.getId()));

		editButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (editButton.getStyleName().contains(AON.AON_ICON_EDIT_ADD))
					onEditCommentButtonClick(textArea, editButton);
				else
					onAcceptEditCommentButtonClick(textArea, editButton);
			}
		});

		editButton.setVisible(canEdit && AonStringUtils.equals(
				userLogged.getText(), issueComment.getUser().getName()));

		FlexTable flexTable = new FlexTable();
		flexTable.setWidget(0, 0, getHeadCommentLabel(issueComment, days));
		flexTable.setWidget(0, 1, editButton);
		flexTable.getFlexCellFormatter().setColSpan(1, 0, 2);
		flexTable.setWidget(1, 0, textArea);

		historialVPanel.add(flexTable);

	}

	private void initTypeButton(
			final List<AonTagIssueSelected> typeTags) {
		typeButton.addClickHandler(new ClickHandler() {

			private PopupPanel popup = new PopupPanel(true);

			{
				VerticalPanel vPanel = new VerticalPanel();

				for (AonTagIssueSelected tag : typeTags) {

					RadioButton rb = new RadioButton("TYPE", tag.getName());
					rb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

						@Override
						public void onValueChange(
								ValueChangeEvent<Boolean> event) {
							RadioButton radio = (RadioButton) event.getSource();
							IssueReadPanel.this.type = radio.getText();
						}
					});

					rb.setValue(tag.getName().compareTo(issue.getType()) == 0);
					rb.setEnabled(canEdit);
					vPanel.add(rb);
				}

				popup.add(vPanel);
				typeLabel.setText(issue.getType());
				typeLabel.setTitle(setTitle2Label(issue.getType()));
				popup.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						String typeAux = IssueReadPanel.this.type;
						if (typeAux.compareTo(issue.getType()) != 0) {
							onRemoveTypeLabelFromIssue(typeLabel.getText(),
									typeAux);
						}
					}
				});
			}

			@Override
			public void onClick(ClickEvent event) {
				int left = typeButton.getAbsoluteLeft();
				int top = typeButton.getAbsoluteTop()
						+ typeButton.getOffsetHeight();

				popup.setPopupPosition(left, top);
				popup.show();
			}
		});
	}

	private void initPriorityButton(
			final List<AonTagIssueSelected> priorityTags) {
		priorityButton.addClickHandler(new ClickHandler() {

			private PopupPanel popup = new PopupPanel(true);

			{
				VerticalPanel vPanel = new VerticalPanel();

				for (AonTagIssueSelected tag : priorityTags) {

					RadioButton rb = new RadioButton("PRIORITY", tag.getName());
					rb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

						@Override
						public void onValueChange(
								ValueChangeEvent<Boolean> event) {
							RadioButton radio = (RadioButton) event.getSource();
							IssueReadPanel.this.priority = radio.getText();
						}
					});

					rb.setValue(
							tag.getName().compareTo(issue.getPriority()) == 0);
					rb.setEnabled(canEdit);

					vPanel.add(rb);
				}

				popup.add(vPanel);
				priorityLabel.setText(issue.getPriority());
				priorityLabel.setTitle(setTitle2Label(issue.getPriority()));
				popup.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						String aux = IssueReadPanel.this.priority;
						if (aux.compareTo(issue.getPriority()) != 0) {
							onRemovePriorityLabel(priorityLabel.getText(), aux);
						}
					}
				});
			}

			@Override
			public void onClick(ClickEvent event) {
				int left = priorityButton.getAbsoluteLeft();
				int top = priorityButton.getAbsoluteTop()
						+ priorityButton.getOffsetHeight();

				popup.setPopupPosition(left, top);
				popup.show();
			}
		});
	}

	private void initTagButton(
			final List<AonTagIssueSelected> noticeTags) {
		tagButton.addClickHandler(new ClickHandler() {

			private PopupPanel popup = new PopupPanel(true);

			{
				VerticalPanel vPanel = new VerticalPanel();

				for (final AonTagIssueSelected tag : noticeTags) {

					CheckBox cb = new CheckBox(tag.getName());
					cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

						@Override
						public void onValueChange(
								ValueChangeEvent<Boolean> event) {

							if (event.getValue())
								evalAssignTag(tag);
							else
								evalUnAssingTag(tag);
						}
					});

					cb.setValue(containsOfficeTag(tag.getName()));
					cb.setEnabled(canEdit);
					vPanel.add(cb);
				}

				popup.add(vPanel);
				popup.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (canEdit)
							onReplaceNoticeTag();
					}
				});
			}

			@Override
			public void onClick(ClickEvent event) {
				int left = tagButton.getAbsoluteLeft();
				int top = tagButton.getAbsoluteTop()
						+ tagButton.getOffsetHeight();

				popup.setPopupPosition(left, top);
				popup.show();
			}

		});
	}

	private void evalAssignTag(AonTagIssueSelected tag) {

		for (AonTagIssueSelected tagAux : assignTags) {
			if (tagAux.getName().compareTo(tag.getName()) == 0
					&& tagAux.getType() == tag.getType()) {
				deletedTagsMap.remove(tag);				
			}
		}
		addTagsMap.add(tag);
	}

	private void evalUnAssingTag(AonTagIssueSelected tag) {

		for (AonTagIssueSelected tagAux : assignTags) {
			if (tagAux.getName().compareTo(tag.getName()) == 0
					&& tagAux.getType() == tag.getType()) {
				deletedTagsMap.add(tag);				
			}
		}

		addTagsMap.remove(tag);
	}

	private boolean containsOfficeTag(String name) {
		for (AonTagIssueSelected tag : issue.getTags()) {
			if (tag.getName().compareTo(name) == 0
					&& tag.getDeletedAt() == null) {
				Label label = new Label(name);
				label.setStyleName(AON.AON_CSS.tagStyle());
				label.addStyleName(AON.AON_CSS.tagNotice());
				label.setTitle(setTitle2Label(name));
				labelsVPanel.add(label);
				return true;
			}
		}
		return false;
	}

	private void createSendButton(){
		sendButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				onSendNotificationButtonClick();
			}
		});
	}
	
	private void createCommentButton() {
		commentButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onCommentButtonClick();
			}
		});
	}

	private void createDuplicatedButton() {
		duplicatedButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onDuplicatedButtonClick();
			}
		});
	}

	private void createClosedButton() {
		closedButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onClosedButtonClick();
			}
		});
	}

	private void createReopenButton() {
		closedButton.setText("Abrir");
		closedButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onReopenButtonClick();
			}
		});
	}
	
	private void onSendNotificationButtonClick(){
		for (Listener listener : listeners){
			listener.onSendNotificationButtonClick();
		}
	}

	private void onCommentButtonClick() {
		for (Listener listener : listeners)

			listener.onIssueCommentButtonClick(commentTextArea.getValue(),
					new Callback<AonIssueComments>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Error en la gestion del comentario "
									+ caught.getMessage());
						}

						@Override
						public void onSucess(AonIssueComments comment) {
							IssueReadPanel.this.canEdit = true;
							printComment(comment);
							commentTextArea.setValue("");
							commentButton.setEnabled(false);
						}
					});
	}

	private void onDuplicatedButtonClick() {
		for (Listener listener : listeners)
			listener.onDuplicateClickEvent();
	}

	private void onClosedButtonClick() {
		String value = commentTextArea.getValue().trim();
		if (!value.isEmpty() && Window.confirm(AONHUB.commentSaveConfirm())) {
			onCommentButtonClick();
		}

		for (Listener listener : listeners)
			listener.onUpdateState(NoticeStatus.CLOSED.getValue());
	}

	private void onReopenButtonClick() {
		for (Listener listener : listeners)
			listener.onUpdateState(NoticeStatus.REOPEN.getValue());
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

	private void onRemoveTypeLabelFromIssue(final String oldName,
			final String newName) {

		if (oldName.compareTo(Constants.NOT_ASSIGNED) == 0)
			onAddTypeLabelFromAnIssue(newName);
		else {
			for (Listener listener : listeners)
				listener.onRemoveLabelFromIssue(oldName, newName,
						new Callback<AonTagIssueSelected>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Error al cerrar la etiqueta");
							}

							@Override
							public void onSucess(
									AonTagIssueSelected comment) {
								onAddTypeLabelFromAnIssue(newName);
							}
						});
		}
	}

	private void onAddTypeLabelFromAnIssue(final String name) {
		List<String> list = new LinkedList<String>();
		list.add(name);

		for (Listener listener : listeners)
			listener.addLabelToAnIssue(list);
	}

	private void onAddPriorityLabelFromAnIssue(final String name) {
		List<String> list = new LinkedList<String>();
		list.add(name);

		for (Listener listener : listeners)
			listener.addLabelToAnIssue(list);
	}

	private void onRemovePriorityLabel(final String oldName,
			final String newName) {

		if (oldName.compareTo(Constants.NOT_ASSIGNED) == 0)
			onAddPriorityLabelFromAnIssue(newName);
		else {
			for (Listener listener : listeners)
				listener.onRemoveLabelFromIssue(oldName, newName,
						new Callback<AonTagIssueSelected>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert(
										"Error al cerrar la etiqueta de prioridad");
							}

							@Override
							public void onSucess(
									AonTagIssueSelected comment) {
								onAddPriorityLabelFromAnIssue(newName);
							}
						});
		}
	}

	private void onCancelEditComment(TextArea textArea, Button button) {
		button.removeStyleName(AON.AON_ICON_ACCEPT);
		button.addStyleName(AON.AON_ICON_EDIT_ADD);
		textArea.setReadOnly(true);
		textArea.setFocus(false);
	}

	private void onAcceptEditCommentButtonClick(final TextArea textArea,
			final Button button) {

		for (Listener listener : listeners)
			listener.onUpdateComment(Integer.parseInt(textArea.getName()),
					textArea.getValue(),
					new Callback<AonIssueComments>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(
									"Error en la modificacion del comentario "
											+ caught.getMessage());
						}

						@Override
						public void onSucess(AonIssueComments comment) {
							button.removeStyleName(AON.AON_ICON_ACCEPT);
							button.addStyleName(AON.AON_ICON_EDIT_ADD);
							textArea.setValue(comment.getBody());
							textArea.setReadOnly(true);
						}
					});
	}

	private void onReplaceNoticeTag() {

		for (Listener listener : listeners)
			listener.onReplaceLabelsForIssue(addTagsMap, deletedTagsMap);
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

	private Label setTagStyle(AonTagIssueSelected tag) {

		Label label = new Label(tag.getName());
		label.setStyleName(AON.AON_RESOURCES.css().tagStyle());
		if (tag.getType() == TagType.OFFICE_TYPE.value())
			label.addStyleName(AON.AON_RESOURCES.css().tagType());
		if (tag.getType() == TagType.OFFICE_PRIORITY.value())
			label.addStyleName(AON.AON_RESOURCES.css().tagPriority());
		if (tag.getType() == TagType.OFFICE_NOTICE.value())
			label.addStyleName(AON.AON_RESOURCES.css().tagNotice());

		return label;
	}

	private String setTitle2Label(String tagName) {

		StringBuilder sb = new StringBuilder();
		AonTagIssueSelected aux = null;

		for (AonTagIssueSelected tag : issue.getTags()) {
			if (AonStringUtils.equals(tagName, tag.getName())) {
				aux = tag;
				break;
			}
		}

		if (aux != null) {
			sb.append("Etiqueta asignada por ");
			sb.append(aux.getUser().getName());
			sb.append(" el ");
			sb.append(date.format(aux.getCreateAt()) + " a las "
					+ hour.format(aux.getCreateAt()));
		}
		return sb.toString();
	}

	private Label getTitleLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append(issue.getTitle().toUpperCase());
		sb.append(" #");
		sb.append(issue.getId());

		titleLabel = new Label(sb.toString());
		titleLabel.getElement().getStyle().setCursor(Cursor.POINTER);
		titleLabel.setStyleName(AON.AON_BOLD);
		titleLabel.addStyleName(AON.AON_CSS.headerTitle());
		
		titleLabel.addDoubleClickHandler(new DoubleClickHandler() {
			
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				onShowTitleFilterDialog();
			}
		});

		return titleLabel;
	}
	
	private void onShowTitleFilterDialog() {
		FilterDialog filterDialog = new FilterDialog() {
			
			{
				setCaption(AON.AONHUB.newTitle());
				setNameLabel(AON.AONHUB.title());
				setVisibleFilterLabel(false);
				setVisibleDateLabel(false);
				setVisibleDateBox(false);
				
				setName(issue.getTitle());
			}
			
			@Override
			protected void onAccept() {
				if (!getName().trim().isEmpty()) {
					onUpdateTitle(getName());
				}
			}
		};

		filterDialog.center();
	}
	
	private void onUpdateTitle(String title) {
		for (Listener listener : listeners)
			listener.onUpdateTitle(title, new Callback<IssueSelected>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Error al modificar el title");
				}

				@Override
				public void onSucess(IssueSelected comment) {

					final StringBuilder sb = new StringBuilder();
					sb.append(comment.getTitle().toUpperCase());
					sb.append(" #");
					sb.append(comment.getId());

					IssueReadPanel.this.titleLabel.setText(sb.toString());
				}
			});
	}

	private int getDaysBefore(Date date) {
		return CalendarUtil.getDaysBetween(date, new Date());
	}

	private Label getHeadCommentLabel(AonIssueComments comment,
			int days) {

		StringBuilder sb = new StringBuilder();
		sb.append("COMENTADO por: ");
		sb.append(comment.getUser().getName());
		sb.append(" el ");
		sb.append(date.format(comment.getCreatedAt()));
		sb.append(" ");
		sb.append(hour.format(comment.getCreatedAt()));
		sb.append(" (hace " + days
				+ ((days == 1) ? " d\u00EDa)" : " d\u00EDas)"));
		
		if (AonStringUtils.isNotBlank(comment.getCompany()))
			sb.append( " ( " + comment.getCompany() + " )");

		Label label = new Label();
		label.setText(sb.toString());
		label.setStyleName(AON.AON_BOLD);
		label.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
		return label;
	}
	
	// ---------------------------------------------------------------------
	
	@Override
	public void onClick(ClickEvent event) {
		
	}
	// -------------------------------------------------------------------
	// -------------------------------------------------------------------
	// --------------------------- DIRTY DIRTY AREA, PLEASE NOT ENTER HERE
	// -------------------------------------------------------------------
	// -------------------------------------------------------------------

	private void initHeader(IssueSelected issue) {

		headerVPanel.add(getTitleLabel());
		
		DisclosurePanel logPanel = new DisclosurePanel();
		logPanel.setAnimationEnabled(true);
		
		DisclosurePanel companyPanel = new DisclosurePanel();
		companyPanel.setAnimationEnabled(true);
		
		HorizontalPanel hLogPanel = null;	
		
		for (AonTagIssueSelected tag : issue.getTags()) {
			if( !tag.isOfficeStatus() && !tag.endDateIsNull()) {
				setInfoTag(tag);
				continue;
			}
			
			if (tag.isOfficeStatus() && tag.endDateIsNull())
				hLogPanel = setHistorialLogHeader(tag);
			else if (!tag.endDateIsNull())
				setContent(tag);
		}

		if (infoHeaderContent.getWidgetCount() > 0) {
			Label icon = new Label();
			icon.setStyleName(AON.AON_CSS.aonIconView());
			hLogPanel.insert(icon, 0);
			logPanel.setHeader(hLogPanel);
			logPanel.setContent(infoHeaderContent);
			headerVPanel.add(logPanel);
		} else
			headerVPanel.add(hLogPanel);
		
		for (AonIssueComments comment : issue.getComments()) {
			if (AonStringUtils.isNotBlank(comment.getCompany()) &&
					!AonStringUtils.equals(issue.getCompany(), comment.getCompany())) {
				addCompany(comment.getCompany(), comment.getCreatedAt()); 
			}
		}
		
		HorizontalPanel hCompanyPanel = new HorizontalPanel();
		hCompanyPanel.setSpacing(5);
		
		if (companyContainer.getWidgetCount() > 0) {
			if (AonStringUtils.isNotEmpty(issue.getCompany()))
					addCompany(issue.getCompany(), issue.getCreateAt());
			
			Label icon = new Label();
			icon.setStyleName(AON.AON_CSS.aonIconView());
			hCompanyPanel.add(icon);
			hCompanyPanel.add(new Label("N\u00FAmero de veces que se ha notificado la incidencia: " + companyContainer.getWidgetCount()));
			companyPanel.setHeader(hCompanyPanel);
			companyPanel.setContent(companyContainer);
			headerVPanel.add(companyPanel);
		}
		else {
			
			Label notified = new Label("Notificada por: ");
			notified.setStyleName(AON.AON_BOLD);
			hCompanyPanel.add(notified);
			
			Label company = new Label(issue.getCompany());
			company.getElement().getStyle().setCursor(Cursor.POINTER);
			company.addClickHandler(this);
			hCompanyPanel.add(company);
	
			Label dateLabel = new Label(date.format(issue.getCreateAt()));
			dateLabel.setStyleName(AON.AON_BOLD);

			Label hourLabel = new Label(hour.format(issue.getCreateAt()));
			hourLabel.setStyleName(AON.AON_BOLD);
			
			hCompanyPanel.add(dateLabel);
			hCompanyPanel.add(new Label(" a las "));
			hCompanyPanel.add(hourLabel);

			headerVPanel.add(hCompanyPanel);

		}
	}

	private HorizontalPanel setHistorialLogHeader(
			AonTagIssueSelected tag) {

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(5);

		Label headerIconLabel = new Label();
		headerIconLabel.setStyleName(issue.getStateIconStyle());
		hPanel.add(headerIconLabel);

		hPanel.add(new Label(tag.getName() + " por "));

		Label ownLabel = new Label(tag.getUser().getName());
		ownLabel.setStyleName(AON.AON_BOLD);
		hPanel.add(ownLabel);

		Label dateLabel = new Label(" el " + date.format(tag.getCreateAt())
				+ " a las " + hour.format(tag.getCreateAt()));
		hPanel.add(dateLabel);

		return hPanel;
	}

	private void setInfoTag(AonTagIssueSelected tag) {
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(5);

		Label headerIconLabel = new Label();
		headerIconLabel.setStyleName(AON.AON_CSS.aonIconDelete());
		hPanel.add(headerIconLabel);

		Label ownLabel = new Label(tag.getUser().getName());
		ownLabel.setStyleName(AON.AON_BOLD);
		hPanel.add(ownLabel);

		hPanel.add(new Label(" cerr\u00F3 la etiqueta "));
		hPanel.add(setTagStyle(tag));
		hPanel.add(new Label(" el " + date.format(tag.getDeletedAt())
				+ " a las " + hour.format(tag.getDeletedAt())));

		infoHeaderContent.add(hPanel);
	}

	private void setContent(AonTagIssueSelected tag) {

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(5);

		Label headerIconLabel = new Label();		
		headerIconLabel.setStyleName(issue.getStateIconStyle());
		hPanel.add(headerIconLabel);

		hPanel.add(new Label(tag.getName() + " por "));

		Label ownLabel = new Label(tag.getUser().getName());
		ownLabel.setStyleName(AON.AON_BOLD);
		hPanel.add(ownLabel);

		Label dateLabel = new Label(" el " + date.format(tag.getCreateAt())
				+ " a las " + hour.format(tag.getCreateAt()));
		hPanel.add(dateLabel);
		infoHeaderContent.add(hPanel);
	}

	private void addCompany(String name, Date createdAt) {
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(5);

		Label company = new Label(name);
		company.setStyleName(AON.AON_BOLD);
		Label dateLabel = new Label(" el " + date.format(createdAt)
				+ " a las " + hour.format(createdAt));
		
		hPanel.add(company);
		hPanel.add(dateLabel);
		
		companyContainer.add(hPanel);
	}

	
}

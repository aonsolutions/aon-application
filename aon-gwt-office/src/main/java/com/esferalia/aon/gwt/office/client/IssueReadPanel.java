package com.esferalia.aon.gwt.office.client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.Constants;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class IssueReadPanel extends Composite {

	interface Callback<T> {

		void onSucess(T comment);

		void onFailure(Throwable caught);
	}

	interface Listener {

		void onUpdateIssueBody(String title, String body,
				Callback<IssueSelected> callback);

		void onUpdateIssueState(String state);

		void onUpdateIssueComment(Integer id, String body,
				Callback<DefaultAonIssueComments> callback);

		void onIssueCommentButtonClick(String body,
				Callback<DefaultAonIssueComments> callback);

		void onRemoveLabelFromIssue(String oldName, String newName,
				Callback<DefaultAonTagIssueSelected> callback);

		void onReplaceLabelsForIssue(List<DefaultAonTagIssueSelected> addLabels,
				List<DefaultAonTagIssueSelected> deletedLabels);

		void addLabelToAnIssue(List<String> labels);
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

	private List<Listener> listeners;
	private IssueSelected issue;

	private String type;
	private String priority;

	private DateTimeFormat fmt = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
	private DateTimeFormat date = DateTimeFormat.getFormat("dd/MM/yyyy");
	private DateTimeFormat hour = DateTimeFormat.getFormat("HH:mm");

	private List<DefaultAonTagIssueSelected> assignTags;
	private List<DefaultAonTagIssueSelected> addTagsMap;
	private List<DefaultAonTagIssueSelected> deletedTagsMap;

	private VerticalPanel infoHeaderContent;

	public IssueReadPanel(IssueSelected issue) {
		initWidget(uiBinder.createAndBindUi(this));

		this.listeners = new LinkedList<Listener>();
		this.issue = issue;
		this.type = issue.getType();
		this.priority = issue.getPriority();
		this.infoHeaderContent = new VerticalPanel();
		this.infoHeaderContent.setSpacing(5);
		this.addTagsMap = new LinkedList<DefaultAonTagIssueSelected>();
		this.deletedTagsMap = new LinkedList<DefaultAonTagIssueSelected>();
		this.assignTags = issue.getTags();

		initHeaderAux(issue);

		for (DefaultAonIssueComments comment : issue.getComments()) {
			printComment(comment,
					(issue instanceof IssueGrid.IssueOpenLoadSelected));
		}

		if (issue instanceof IssueGrid.IssueOpenLoadSelected) {
			createClosedButton();
			createCommentButton();
		} else if (issue instanceof IssueGrid.IssueClosedLoadSelected) {
			commentButton.setVisible(false);
			commentTextArea.setVisible(false);
			createReopenButton();
		}
	}

	public void setUser(User user) {
		userLogged.setText(user.getName());
	}

	public void setTags(List<DefaultAonTagIssueSelected> tags) {

		List<DefaultAonTagIssueSelected> typesList = new LinkedList<DefaultAonTagIssueSelected>();
		List<DefaultAonTagIssueSelected> priorityList = new LinkedList<DefaultAonTagIssueSelected>();
		List<DefaultAonTagIssueSelected> officeList = new LinkedList<DefaultAonTagIssueSelected>();

		for (DefaultAonTagIssueSelected tag : tags) {
			if (tag.getType() == TagType.OFFICE_TYPE.value()) {
				typesList.add(tag);
			}

			else if (tag.getType() == TagType.OFFICE_PRIORITY.value()) {
				priorityList.add(tag);
			}

			else if (tag.getType() == TagType.OFFICE_NOTICE.value())
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
		commentButton.setEnabled(
				commentTextArea.getValue().trim().isEmpty() == false);
	}

	private String getStateIcon(String state) {

		String value = "";

		if (state.compareTo(NoticeStatus.OPEN.getValue()) == 0) {
			value = AON.AON_CSS.aonIconIssueOpenedGreen();
		} else if (state.compareTo(NoticeStatus.REOPEN.getValue()) == 0) {
			value = AON.AON_CSS.aonIconIssueReOpenedBlue();
		} else {
			value = AON.AON_CSS.aonIconIssueClosed();
		}

		return value;
	}

	private void initHeaderAux(IssueSelected issue) {

		HorizontalPanel hPanel = null;

		Label titleLabel = new Label(issue.getTitle().toUpperCase());
		titleLabel.setStyleName(AON.AON_BOLD);
		titleLabel.addStyleName(AON.AON_CSS.headerTitle());
		headerVPanel.add(titleLabel);

		DisclosurePanel disclosurePanel = new DisclosurePanel();
		disclosurePanel.setAnimationEnabled(true);

		for (DefaultAonTagIssueSelected tag : issue.getTags()) {

			if (tag.getType() != TagType.OFFICE_STATUS.value()
					&& tag.getDeletedAt() != null) {
				setInfoTag(tag);
				continue;
			}

			if (tag.getDeletedAt() == null)
				hPanel = setHistorialHeader(tag);
			else
				setContent(tag);
		}

		if (infoHeaderContent.getWidgetCount() > 0) {
			Label icon = new Label();
			icon.setStyleName(AON.AON_CSS.aonIconView());
			hPanel.insert(icon, 0);
			disclosurePanel.setHeader(hPanel);
			disclosurePanel.setContent(infoHeaderContent);
			headerVPanel.add(disclosurePanel);
		} else
			headerVPanel.add(hPanel);

		if (issue.getCompany().trim().isEmpty() == false) {
			HorizontalPanel companyPanel = new HorizontalPanel();
			companyPanel.setSpacing(5);

			Label notified = new Label("Notificada por: ");
			notified.setStyleName(AON.AON_BOLD);
			companyPanel.add(notified);
			companyPanel.add(new Label(issue.getCompany()));
			headerVPanel.add(companyPanel);
		}
	}

	private HorizontalPanel setHistorialHeader(DefaultAonTagIssueSelected tag) {

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(5);

		Label headerIconLabel = new Label();
		String iconState = getStateIcon(tag.getName());
		headerIconLabel.setStyleName(iconState);
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

	private void setInfoTag(DefaultAonTagIssueSelected tag) {
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
		hPanel.add(new Label(" el " + date.format(tag.getCreateAt()) + " a las "
				+ hour.format(tag.getCreateAt())));

		infoHeaderContent.add(hPanel);
	}

	private void setContent(DefaultAonTagIssueSelected tag) {

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(5);

		Label headerIconLabel = new Label();
		String iconState = getStateIcon(tag.getName());
		headerIconLabel.setStyleName(iconState);
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

	public void printComment(DefaultAonIssueComments issueComment,
			boolean editVisible) {

		int days = CalendarUtil.getDaysBetween(issueComment.getCreatedAt(),
				new Date());
		Label label = new Label();
		label.setText("COMENTADO por " + issueComment.getUser().getName()
				+ " el " + fmt.format(issueComment.getCreatedAt()) + " (hace "
				+ days + ((days == 1) ? " d\u00EDas)" : " d\u00EDas)"));
		label.setStyleName(AON.AON_BOLD);
		label.getElement().getStyle().setFontStyle(FontStyle.ITALIC);

		final Button editButton = new Button();
		editButton.setStyleName(AON.AON_ICON_EDIT_ADD);
		editButton.addStyleName(AON.AON_ICON_CMD_BUTTON);
		editButton.addStyleName(AON.AON_CSS.editButton());
		editButton.setTitle("Editar comentario");

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

		editButton.setVisible(editVisible);

		FlexTable flexTable = new FlexTable();
		flexTable.setWidget(0, 0, label);
		flexTable.setWidget(0, 1, editButton);
		flexTable.getFlexCellFormatter().setColSpan(1, 0, 2);
		flexTable.setWidget(1, 0, textArea);

		historialVPanel.add(flexTable);

	}

	private void initTypeButton(
			final List<DefaultAonTagIssueSelected> typeTags) {
		typeButton.addClickHandler(new ClickHandler() {

			private PopupPanel popup = new PopupPanel(true);

			{
				VerticalPanel vPanel = new VerticalPanel();

				for (DefaultAonTagIssueSelected tag : typeTags) {

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
					rb.setEnabled(
							issue instanceof IssueGrid.IssueOpenLoadSelected);
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
			final List<DefaultAonTagIssueSelected> priorityTags) {
		priorityButton.addClickHandler(new ClickHandler() {

			private PopupPanel popup = new PopupPanel(true);

			{
				VerticalPanel vPanel = new VerticalPanel();

				for (DefaultAonTagIssueSelected tag : priorityTags) {

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
					rb.setEnabled(
							issue instanceof IssueGrid.IssueOpenLoadSelected);

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
			final List<DefaultAonTagIssueSelected> noticeTags) {
		tagButton.addClickHandler(new ClickHandler() {

			private PopupPanel popup = new PopupPanel(true);

			{
				VerticalPanel vPanel = new VerticalPanel();

				for (final DefaultAonTagIssueSelected tag : noticeTags) {

					CheckBox cb = new CheckBox(tag.getName());
					cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

						@Override
						public void onValueChange(
								ValueChangeEvent<Boolean> event) {

							if (event.getValue() == true)
								evalAssignTag(tag);
							else
								evalUnAssingTag(tag);
						}
					});

					cb.setValue(containsOfficeTag(tag.getName()));
					cb.setEnabled(
							issue instanceof IssueGrid.IssueOpenLoadSelected);
					vPanel.add(cb);
				}

				popup.add(vPanel);
				popup.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (issue instanceof IssueGrid.IssueOpenLoadSelected)
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

	private void evalAssignTag(DefaultAonTagIssueSelected tag) {

		for (DefaultAonTagIssueSelected tagAux : assignTags) {
			if (tagAux.getName().compareTo(tag.getName()) == 0
					&& tagAux.getType() == tag.getType()) {
				deletedTagsMap.remove(tag);
				return;
			}
		}
		addTagsMap.add(tag);
	}

	private void evalUnAssingTag(DefaultAonTagIssueSelected tag) {

		for (DefaultAonTagIssueSelected tagAux : assignTags) {
			if (tagAux.getName().compareTo(tag.getName()) == 0
					&& tagAux.getType() == tag.getType()) {
				deletedTagsMap.add(tag);
				return;
			}
		}

		addTagsMap.remove(tag);
	}

	private boolean containsOfficeTag(String name) {
		for (DefaultAonTagIssueSelected tag : issue.getTags()) {
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

	private void createCommentButton() {
		commentButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onCommentButtonClick();
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

	private void onCommentButtonClick() {
		for (Listener listener : listeners)

			listener.onIssueCommentButtonClick(commentTextArea.getValue(),
					new Callback<DefaultAonIssueComments>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Error en la gestion del comentario "
									+ caught.getMessage());
						}

						@Override
						public void onSucess(DefaultAonIssueComments comment) {
							printComment(comment, true);
							commentTextArea.setValue("");
							commentButton.setEnabled(false);
						}
					});
	}

	private void onClosedButtonClick() {

		if (commentTextArea.getValue().trim().isEmpty() == false
				&& Window.confirm(
						"\u00A1Ey\u0021. Parece que estabas escribiendo un comentario. "
								+ "\n\u00BFQuieres guardarlo?")) {
			onCommentButtonClick();
		}

		for (Listener listener : listeners)
			listener.onUpdateIssueState(NoticeStatus.CLOSED.getValue());
	}

	private void onReopenButtonClick() {
		for (Listener listener : listeners)
			listener.onUpdateIssueState(NoticeStatus.REOPEN.getValue());
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
						new Callback<DefaultAonTagIssueSelected>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Error al cerrar la etiqueta");
							}

							@Override
							public void onSucess(
									DefaultAonTagIssueSelected comment) {
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
						new Callback<DefaultAonTagIssueSelected>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert(
										"Error al cerrar la etiqueta de prioridad");
							}

							@Override
							public void onSucess(
									DefaultAonTagIssueSelected comment) {
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
			listener.onUpdateIssueComment(Integer.parseInt(textArea.getName()),
					textArea.getValue(),
					new Callback<DefaultAonIssueComments>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(
									"Error en la modificacion del comentario "
											+ caught.getMessage());
						}

						@Override
						public void onSucess(DefaultAonIssueComments comment) {
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
	
	private Label setTagStyle(DefaultAonTagIssueSelected tag) {
		
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
	
	private String setTitle2Label (String tagName) {
		
		StringBuilder sb = new StringBuilder();
		DefaultAonTagIssueSelected aux = null;
		
		for (DefaultAonTagIssueSelected tag : issue.getTags()) {
			if (AonStringUtils.equals(tagName, tag.getName())) {
				aux = tag;
				break;
			}
		}
			
		if (aux != null) {
			sb.append("Etiqueta asignada por ");
			sb.append(aux.getUser().getName());
			sb.append(" el ");
			sb.append( date.format(aux.getCreateAt()) + " a las " + hour.format(aux.getCreateAt()));
		}
		return sb.toString();
	}
}

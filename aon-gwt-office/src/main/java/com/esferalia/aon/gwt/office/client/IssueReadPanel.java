package com.esferalia.aon.gwt.office.client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class IssueReadPanel extends Composite
		implements SelectionHandler<TreeItem> {

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
	}

	interface GridStyle extends CssResource {
		@ClassName("options-text-area")
		String optionsTextArea();

		@ClassName("row-background")
		String rowBackground();

		@ClassName("edit-button")
		String editButton();
	}

	private static IssueReadPanelUiBinder uiBinder = GWT
			.create(IssueReadPanelUiBinder.class);

	interface IssueReadPanelUiBinder extends UiBinder<Widget, IssueReadPanel> {
	}

	@UiField
	GridStyle style;
	@UiField
	Label companyLabel;
	@UiField
	Label userLogged;

	@UiField
	Label titleLabel;
	@UiField
	HorizontalPanel labelsHPanel;
	@UiField
	Label typeLabel;
	@UiField
	Label priorityLabel;
	@UiField
	FlowPanel historialVPanel;

	@UiField
	Button closedButton;
	@UiField
	Button commentButton;

	@UiField
	TextArea commentTextArea;

	private List<Listener> listeners;
	private IssueSelected issue;	

	private DateTimeFormat fmt = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");

	public IssueReadPanel(IssueSelected issue) {
		initWidget(uiBinder.createAndBindUi(this));

		this.listeners = new LinkedList<Listener>();
		this.issue = issue;

		setCompany(issue.getCompany());
		setType(issue.getType());
		setPriority(issue.getPriority());
		setAsunto(issue.getTitle());
		setLabels(issue.getTags());
		printBody(issue);

		for (DefaultAonIssueComments comment : issue.getComments()) {
			printComment(comment,
					(issue instanceof IssueGrid.IssueOpenLoadSelected));
		}

		userLogged.setText(issue.getUser().getName());

		if (issue instanceof IssueGrid.IssueOpenLoadSelected) {
			createClosedButton();
			createCommentButton();
		} else if (issue instanceof IssueGrid.IssueClosedLoadSelected) {
			commentButton.setVisible(false);
			commentTextArea.setVisible(false);
			createReopenButton();
		}
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	private void setCompany(String company) {
		companyLabel.setText(company.toUpperCase());
	}

	private void setAsunto(String asunto) {
		this.titleLabel.setText(asunto.toUpperCase());
	}
	
	private void setType(String type) {
		this.typeLabel.setText(type);
	}

	private void setPriority(String priority) {
		this.priorityLabel.setText(priority);
	}

	private void setLabels(List<DefaultAonTagIssueSelected> tags) {

		for (DefaultAonTagIssueSelected tag : tags) {
			labelsHPanel.add(new Label(tag.getName()));
		}
	}

	public void printBody(IssueSelected issue) {

		int days = CalendarUtil.getDaysBetween(issue.getCreateAt(), new Date());
		Label label = new Label();
		label.setText(issue.getState() + " por " + issue.getUser().getName()
				+ " el " + fmt.format(issue.getCreateAt()) + " (hace " + days
				+ ((days == 1) ? " d\u00EDas)" : " d\u00EDas)"));
		label.setStyleName(AON.AON_BOLD);
		label.getElement().getStyle().setFontStyle(FontStyle.ITALIC);	
		
		final Button editButton = new Button();

		editButton.setStyleName(AON.AON_ICON_EDIT_ADD);
		editButton.addStyleName(AON.AON_ICON_CMD_BUTTON);
		editButton.addStyleName(style.editButton());

		final TextArea textArea = getTextArea(issue.getBody());
//		final TextArea textArea = getTextArea(
//				new String(issue.getBody().replaceAll("--", "\n")));
		textArea.setName(String.valueOf(issue.getId()));
		
		editButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (editButton.getStyleName().contains(AON.AON_ICON_EDIT_ADD))
					onEditCommentButtonClick(textArea, editButton);
				else
					onAcceptEditBodyButtonClick(textArea, editButton);
			}
		});
		editButton.setVisible(issue instanceof IssueGrid.IssueOpenLoadSelected);

		FlexTable flexTable = new FlexTable();
		flexTable.setWidget(0, 0, label);
		flexTable.setWidget(0, 1, editButton);
		flexTable.getFlexCellFormatter().setColSpan(1, 0, 2);
		flexTable.setWidget(1, 0, textArea);

		historialVPanel.add(flexTable);

	}

	public void printComment(DefaultAonIssueComments issueComment,
			boolean editVisible) {

		int days = CalendarUtil.getDaysBetween(issueComment.getCreatedAt(),
				new Date());
		Label label = new Label();
		label.setText("COMENTADO por " + issue.getUser().getName() + " el "
				+ fmt.format(issueComment.getCreatedAt()) + " (hace " + days
				+ ((days == 1) ? " d\u00EDas)" : " d\u00EDas)"));
		label.setStyleName(AON.AON_BOLD);
		label.getElement().getStyle().setFontStyle(FontStyle.ITALIC);

		final Button editButton = new Button();
		editButton.setStyleName(AON.AON_ICON_EDIT_ADD);
		editButton.addStyleName(AON.AON_ICON_CMD_BUTTON);
		editButton.addStyleName(style.editButton());
		editButton.setTitle("Editar comentario");
		
		final TextArea textArea = getTextArea(issueComment.getBody());

//		final TextArea textArea = getTextArea(
//				new String(issueComment.getBody().replaceAll("--", "\n")));
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

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		Window.alert("Selection");
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
						}
					});
	}

	private void onClosedButtonClick() {
		for (Listener listener : listeners)
			listener.onUpdateIssueState(NoticeStatus.CLOSED.getValue());
	}

	private void onReopenButtonClick() {
		for (Listener listener : listeners)
			listener.onUpdateIssueState(NoticeStatus.REOPEN.getValue());
	}

	private void onEditCommentButtonClick(final TextArea textArea, final Button button) {
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

	private void onAcceptEditBodyButtonClick(final TextArea textArea,
			final Button button) {

		for (Listener listener : listeners)
			listener.onUpdateIssueBody(issue.getTitle(), textArea.getValue(),
					new Callback<IssueSelected>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(
									"Error al modificar el cuerpo del aviso "
											+ caught.getMessage());
						}

						@Override
						public void onSucess(IssueSelected comment) {

						}
					});
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

	private TextArea getTextArea(String text) {
		TextArea textArea = new TextArea();
		textArea.setReadOnly(true);
		textArea.setVisibleLines(5);
		textArea.setCharacterWidth(10);
		textArea.setWidth("600px");
		textArea.setStylePrimaryName(style.optionsTextArea());
		textArea.setValue(text);
		return textArea;
	}
}

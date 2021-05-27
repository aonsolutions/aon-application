package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class FilesEditor extends ResizeComposite {

	
	
	interface Binder extends UiBinder<Widget, FilesEditor> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	
	interface Template extends SafeHtmlTemplates {
		@Template ("<span class=\"gwt-InlineLabel aon-icon-commandButton {1}\">{0}</span>")
		SafeHtml tab(String title, String icon);
	}

	private static final Template template = GWT.create(Template.class);

	@UiField
	Label titleLabel;

	@UiField
	Button saveButton;

	@UiField
	MenuItem saveMenuItem;

	@UiField
	TabLayoutPanel tabLayoutPanel;
	
	@UiField
	Panel customToolBarPanel;
	

	public FilesEditor() {
		initWidget(binder.createAndBindUi(this));
		saveMenuItem.setScheduledCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				save();
			}
		});
	}

	public void setTitle(String title) {
		titleLabel.setText(title);
	}

	
	// -----------------------------------------------------------------------

	public void add( Button button) {
		customToolBarPanel.add(button);
	}

	public void add( FileEditor fileEditor, String text, String icon) {
		tabLayoutPanel.add(fileEditor, template.tab(text, icon));
	}
	
	public int getSelectedIndex() {
		return tabLayoutPanel.getSelectedIndex();
	}
	

	// -----------------------------------------------------------------------

	@UiHandler("saveButton")
	void onSaveClick(ClickEvent event) {
		save();
	}

	// -----------------------------------------------------------------------

	private void save() {
		FileEditor fileEditor = (FileEditor ) tabLayoutPanel.getWidget(tabLayoutPanel.getSelectedIndex());
		fileEditor.save();
	}


}

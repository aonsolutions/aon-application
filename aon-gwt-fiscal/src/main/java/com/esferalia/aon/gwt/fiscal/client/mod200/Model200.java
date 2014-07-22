package com.esferalia.aon.gwt.fiscal.client.mod200;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.Mod200Service;
import com.esferalia.aon.gwt.fiscal.client.Mod200ServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.Mod200ServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200.BalanceType;
import com.esferalia.aon.gwt.fiscal.shared.mod200.ValidationMessage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model200 extends MainEntryPoint {

	interface IValidationMessageSelectioinHandler {
		void validationMessageSelected(ValidationMessage msg);
	}

	interface DeleteButtonTemplate extends SafeHtmlTemplates {
		@Template("<input type=\"button\" value=\"&nbsp;\" class=\"aon-icon-delete\" style=\"border: medium none !important;\">")
		SafeHtml render(String option);
	}
	static class DeleteButtonSafeHtmlTemplates implements SafeHtmlRenderer<String> {

		private static DeleteButtonTemplate template;

		protected DeleteButtonSafeHtmlTemplates() {
			template = GWT.create(DeleteButtonTemplate.class);
		}
		
		@Override
		public SafeHtml render(String object) {
			return template.render(object);
		}

		@Override
		public void render(String object, SafeHtmlBuilder builder) {
			builder.append( template.render(object) );
		}
		
	}

	interface NavigateButtonTemplate extends SafeHtmlTemplates {
		@Template("<input type=\"button\" value=\"&nbsp;\" class=\"aon-icon-go\" style=\"border: medium none !important;\">")
		SafeHtml render(String option);
	}
	static class NavigateButtonSafeHtmlTemplates implements SafeHtmlRenderer<String> {

		private static NavigateButtonTemplate template;

		protected NavigateButtonSafeHtmlTemplates() {
			template = GWT.create(NavigateButtonTemplate.class);
		}
		
		@Override
		public SafeHtml render(String object) {
			return template.render(object);
		}

		@Override
		public void render(String object, SafeHtmlBuilder builder) {
			builder.append( template.render(object) );
		}
		
	}
	

	interface Model200Binder extends UiBinder<Widget, Model200> {
	}

	private static final Model200Binder MODEL_200_BINDER = GWT
			.create(Model200Binder.class);
	
	private Mod200ServiceAsyncDecorator mod200Service;
	
	final static CommonMessages MSG = GWT.create(CommonMessages.class);
	final static AonResources RESOURCES = GWT.create(AonResources.class);

	private Mod200Object mod200;

	private int year = 2013;
	private int domain;
	private int enterprise;
	
	// ---- UI FIELD
	@UiField
	Button initializeButton;
	@UiField
	Button saveButton;
	@UiField
	Button removeButton;
	@UiField
	Button validateButton;
	@UiField
	Button calculateButton;
	@UiField
	Button aeatAccountingFileButton;
	@UiField
	Button aeatFileButton;
	@UiField
	Button aeatPrintButton;
	@UiField
	CheckBox calculateCheck;
	
	@UiField
	HorizontalPanel messagesPanel;

	@UiField
	DockLayoutPanel dockLayoutPanel;
	@UiField
	Panel toolbarPanel;
	@UiField
	DockLayoutPanel formPanel;
	@UiField
	Model200Deck deckPanel;
	@UiField
	Model200Sidebar sidebar;

	@UiField
	Panel formContainer;
	FormPanel diskForm;
	Hidden mod200Hidden;
	
	// ----
	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		Widget ui = MODEL_200_BINDER.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
		
		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		mod200Hidden = new Hidden("mod200");
		diskForm.add(mod200Hidden);
		formContainer.add(diskForm);

		Mod200ServiceAsync mod200ServiceRaw = GWT
				.create(Mod200Service.class);
		mod200Service = new Mod200ServiceAsyncDecorator(mod200ServiceRaw);
		
		startModel();
	}


	private void startModel() {
		mod200 = new Mod200Object(getCurrentDomain(), year, mod200Service);
		mod200.getMod200(new AsyncCallback<Mod200>() {
			
			@Override
			public void onSuccess(Mod200 result) {
				if (result.getId() == null) {
					//deckPanel.page00.dump(mod200);
					deckPanel.dump(mod200);
					
					deckPanel.pagesPanel.showWidget(deckPanel.pagesPanel.getWidgetIndex(deckPanel.page00));
					sidebar.setVisibleLinks( false );
					
					initializeButton.setVisible(true);
					
					saveButton.setVisible(false);
					removeButton.setVisible(false);
					validateButton.setVisible(false);
					calculateCheck.setVisible(false);
					calculateButton.setVisible(false);
					aeatAccountingFileButton.setVisible(false);
					aeatFileButton.setVisible(false);
					aeatPrintButton.setVisible(false);
					
					deckPanel.page00.enableCharacters( true );
				} else {
					dump();
				}
				calculateCheck.setValue(mod200.isAuthomaticCalculation());
			}
			
			@Override
			public void onFailure(Throwable e) {
				raiseException(e);
				
				initializeButton.setVisible(false);
				saveButton.setVisible(false);
				removeButton.setVisible(false);
				validateButton.setVisible(false);
				calculateButton.setVisible(false);
				aeatAccountingFileButton.setVisible(false);
				aeatFileButton.setVisible(false);
				aeatPrintButton.setVisible(false);
				calculateCheck.setValue(mod200.isAuthomaticCalculation());
				calculateCheck.setVisible(false);
				
			}
		});
	}

	protected void raiseException(Throwable t) {
		deckPanel.pagesPanel.showWidget(deckPanel.pagesPanel.getWidgetIndex(deckPanel.errorPage));
		deckPanel.errorPage.addErrorMsg(t);
	}
	
	
	// Buttons
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		PopupAsyncCallback callback = new PopupAsyncCallback(){
			@Override
			public void onSuccess(Mod200 result) {
				super.onSuccess(result);
				refreshButtonsVisibility();
			}
		};
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(MSG.processing());
		label.addStyleName(RESOURCES.css().aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		callback.setPopup(popup);
		populatePages(mod200);
		try {
			mod200.save(callback);
		} catch (IllegalArgumentException e) {
			refreshButtonsVisibility();
			popup.hide();
			DialogMessages.alertErrorWidget(e.getMessage()).center();
		}
	}
	// Buttons
	@UiHandler("removeButton")
	void onRemoveButtonClick(ClickEvent event) {
		if (Window.confirm(MSG.confirmDeleteAction())) {
			remove(new PopupAsyncCallback() {
				@Override
				public void onSuccess(Mod200 result) {
					super.onSuccess(result);
					startModel();
				}
			});
		}
	}
	
	@UiHandler("aeatAccountingFileButton")
	void onAeatAccountingFileButtonClick(ClickEvent event) {
		Window.alert(
				  "Se va a proceder a la generaci\u00F3n de un fichero\n"
				+ "con los datos contables, para su importaci\u00F3n en\n"
				+ "el programa de ayuda de la Agencia Tributaria.\n\n"
				+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
				+ "El fichero se genera a partir de los datos guardados.");
		diskForm.setAction(GWT.getHostPageBaseURL()
				+ "/aon_gwt_fiscal/Model200AccountingFile");
		mod200Hidden.setValue(String.valueOf(mod200.getMod200().getId()));
		diskForm.submit();
	}

	@UiHandler("aeatFileButton")
	void onAeatFileButtonClick(ClickEvent event) {
		Window.alert(
				  "Se va a proceder a la generaci\u00F3n de un fichero\n"
				+ "con los datos de la declaraci\u00F3n, para su \n"
				+ "presentaci\u00F3n en la web de la Agencia Tributaria.\n\n"
				+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
				+ "El fichero se genera a partir de los datos guardados.");
		diskForm.setAction(GWT.getHostPageBaseURL()
				+ "/aon_gwt_fiscal/Model200File");
		mod200Hidden.setValue(String.valueOf(mod200.getMod200().getId()));
		diskForm.submit();
	}

	@UiHandler("aeatPrintButton")
	void onAeatPrintButtonClick(ClickEvent event) {
		Window.alert(
				  "Se va a proceder a la validaci\u00F3n en los servidores de la \n"
				+ "Agencia Tributaria. En el caso de validaci\u00F3n correcta,la Agencia \n"
				+ "Tributaria devolver\u00E1 un documento PDF borrador con la declarai\u00F3n\n\n"
				+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
				+ "La petici\u00F3n se genera a partir de los datos guardados.");
		diskForm.setAction(GWT.getHostPageBaseURL()
				+ "/aon_gwt_fiscal/Model200Print");
		mod200Hidden.setValue(String.valueOf(mod200.getMod200().getId()));
		diskForm.submit();
	}

	@UiHandler("calculateButton")
	void onCalculateButtonClick(ClickEvent event) {
		mod200.calculate();
	}
	@UiHandler("calculateCheck")
	void onCalculateCheckClick(ClickEvent event) {
		mod200.setAuthomaticCalculation(calculateCheck.getValue());
		calculateButton.setVisible(!calculateCheck.getValue());
		if (calculateCheck.getValue())
			mod200.calculate();
	}
	
	private void populatePages(Mod200Object mod2002) {
		deckPanel.page00.populate(mod200);
		deckPanel.page01.populate(mod200);
		deckPanel.page02.populate(mod200);
		deckPanel.page14.populate(mod200);
	}

	private void remove(PopupAsyncCallback callback) {
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(MSG.processing());
		label.addStyleName(RESOURCES.css().aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		callback.setPopup(popup);
		try {
			mod200.delete(callback);
		} catch (IllegalArgumentException e) {
			popup.hide();
			DialogMessages.alertErrorWidget(e.getMessage()).center();
		}
	}

	private class PopupAsyncCallback implements AsyncCallback<Mod200> {
		PopupPanel popup;
		
		public void setPopup(PopupPanel popup) {
			this.popup = popup;
		}

		@Override
		public void onSuccess(Mod200 result) {
			popup.hide();
		}

		@Override
		public void onFailure(Throwable caught) {
			popup.hide();
		}
		
	}
	@UiHandler("validateButton")
	void onValidateButton(ClickEvent event) {
		validate(new PopupAsyncCallback() {
			@Override
			public void onSuccess(Mod200 result) {
				super.onSuccess(result);
				if (result.getMessages() != null && !result.getMessages().isEmpty()) {
					deckPanel.errorPage.addErrorMsg( result.getMessages() );
					deckPanel.pagesPanel.showWidget(deckPanel.pagesPanel.getWidgetIndex(deckPanel.errorPage));
				} else {
					Window.alert(MSG.noValidationMessages() );
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				deckPanel.errorPage.addErrorMsg(caught);
				deckPanel.pagesPanel.showWidget(deckPanel.pagesPanel.getWidgetIndex(deckPanel.errorPage));
			}
			
		});
	}
	
	private void validate(PopupAsyncCallback callback) {
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(MSG.processing());
		label.addStyleName(RESOURCES.css().aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		callback.setPopup(popup);
		populatePages(mod200);
		mod200.validate(callback);
	}
	
	
	@UiHandler("initializeButton")
	void onInitializeClick(ClickEvent event) {
		initialize(new  PopupAsyncCallback());
	}
	
	private void initialize(final PopupAsyncCallback callback) {
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(MSG.processing());
		label.addStyleName(RESOURCES.css().aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		callback.setPopup(popup);
		deckPanel.page00.populate(mod200);
		mod200.initializeMod200(new AsyncCallback<Mod200>() {
			@Override
			public void onSuccess(Mod200 result) {
				dump();
				callback.onSuccess(result);
			}
			
			@Override
			public void onFailure(Throwable e) {
				raiseException(e);
				callback.onFailure(e);
			}
		});
	}

	private void dump() {
		deckPanel.dump(mod200);
		
		deckPanel.pagesPanel.showWidget(deckPanel.pagesPanel.getWidgetIndex(deckPanel.page00));
		
		sidebar.linkPage00.setVisible(true);
		sidebar.addListener(sidebar.linkPage00, deckPanel.pagesPanel, deckPanel.page00);
		sidebar.linkPage01.setVisible(true);
		sidebar.addListener(sidebar.linkPage01, deckPanel.pagesPanel, deckPanel.page01);
		sidebar.linkPage02.setVisible(true);
		sidebar.addListener(sidebar.linkPage02, deckPanel.pagesPanel, deckPanel.page02);
		sidebar.linkPage03.setVisible(true);
		sidebar.addListener(sidebar.linkPage03, deckPanel.pagesPanel, deckPanel.page03);
		sidebar.linkPage04.setVisible(true);
		sidebar.addListener(sidebar.linkPage04, deckPanel.pagesPanel, deckPanel.page04);
		sidebar.linkPage05.setVisible(true);
		sidebar.addListener(sidebar.linkPage05, deckPanel.pagesPanel, deckPanel.page05);

		if (mod200.getMod200().getBalanceType() != BalanceType.PYMES) {
			sidebar.linkPage06.setVisible(true);
			sidebar.addListener(sidebar.linkPage06, deckPanel.pagesPanel, deckPanel.page06);
		}
		
		sidebar.linkPage07.setVisible(true);
		sidebar.addListener(sidebar.linkPage07, deckPanel.pagesPanel, deckPanel.page07);
		sidebar.linkPage08.setVisible(true);
		sidebar.addListener(sidebar.linkPage08, deckPanel.pagesPanel, deckPanel.page08);
		sidebar.linkPage09.setVisible(true);
		sidebar.addListener(sidebar.linkPage09, deckPanel.pagesPanel, deckPanel.page09);
		sidebar.linkPage10.setVisible(true);
		sidebar.addListener(sidebar.linkPage10, deckPanel.pagesPanel, deckPanel.page10);
		sidebar.linkPage11.setVisible(true);
		sidebar.addListener(sidebar.linkPage11, deckPanel.pagesPanel, deckPanel.page11);
		sidebar.linkPage12.setVisible(true);
		sidebar.addListener(sidebar.linkPage12, deckPanel.pagesPanel, deckPanel.page12);
		sidebar.linkPage13.setVisible(true);
		sidebar.addListener(sidebar.linkPage13, deckPanel.pagesPanel, deckPanel.page13);
		sidebar.linkPage14.setVisible(true);
		sidebar.addListener(sidebar.linkPage14, deckPanel.pagesPanel, deckPanel.page14);
		
		deckPanel.errorPage.addSelectionListener( new IValidationMessageSelectioinHandler() {
			
			@Override
			public void validationMessageSelected(ValidationMessage msg) {
				if ( msg.getPage() >= 0 ) {
					deckPanel.pagesPanel.showWidget(msg.getPage());
					if (msg.getKey() != null) {
						PageAbs page = (PageAbs) deckPanel.pagesPanel.getWidget(msg.getPage());
						DoubleTextBox d = page.getInputs().get(msg.getKey());
						if (d != null) {
							d.setFocus(true);
						}
						BoxLabel l = page.getLabels().get(msg.getKey());
						if (l != null) {
							l.addErrorState(msg.getMessage());
						}
					}
				}
			}
		});
		
		refreshButtonsVisibility();
		
		
		deckPanel.page00.enableCharacters( false );
	}

	private void refreshButtonsVisibility() {
		initializeButton.setVisible(false);
		saveButton.setVisible(true);
		removeButton.setVisible(mod200.getMod200().getId() != null);
		validateButton.setVisible(true);
		calculateCheck.setVisible(true);
		calculateButton.setVisible(!calculateCheck.isVisible());
		
		aeatAccountingFileButton.setVisible(mod200.getMod200().getId() != null);
		aeatFileButton.setVisible(mod200.getMod200().getId() != null);
		aeatPrintButton.setVisible(mod200.getMod200().getId() != null);
	}
	
}

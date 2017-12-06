package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.ContextMenu;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public class OLD_Model390 extends MainEntryPoint {

	public static interface IModel390 {
		void select(Mod390 m390);
		void onNew(int year);
	}	

	public static interface IMod390CallBack {
		void onCancel();
	}	

	interface Model390Binder extends UiBinder<Widget, OLD_Model390> {
	}

	private static final Model390Binder MODEL_390_BINDER = GWT
			.create(Model390Binder.class);

	private Mod390 mod390;
	private Model390ServiceAsync fiscalService;
	private NewContextMenu newContextMenu;


	private int domain;
	private int enterprise;

	@UiField
	DeckLayoutPanel deckPanel;
	@UiField
	DockLayoutPanel listPanel;
	@UiField
	SimpleLayoutPanel formPanel;

	@UiField(provided = true)
	OLD_Model390Table table;

	@UiField
	Button newButton;
	
	private IMod390CallBack mod390CallBack = new IMod390CallBack() {
		
		@Override
		public void onCancel() {
			OLD_Model390.this.onCancel();
		}
	};

	
	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

		Model390ServiceAsync fiscalServiceRaw = GWT.create(Model390Service.class);
		fiscalService = new Model390ServiceAsyncDecorator(fiscalServiceRaw);
		table = new OLD_Model390Table(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				select(table.getSelected());
			}
		});
		newContextMenu = new NewContextMenu();

		// Create the UI defined in Employee.ui.xml.
		Widget ui = MODEL_390_BINDER.createAndBindUi(this);

		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
		
		domain = getCurrentDomain();
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
	}

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	private void select(Mod390 m390) {
//		if (m390.getYear() == 2013 || m390.getYear() == 2014) {
//			Model3902014 model3902014 = new Model3902014(mod390CallBack);
//			formPanel.setWidget(model3902014);
//			model3902014.select(m390);
//		} else if (m390.getYear() == 2015 || m390.getYear() == 2016 || m390.getYear() == 2017) {
//			//Model3902015 model3902015 = new Model3902015(mod390CallBack);
//			formPanel.setWidget(model3902015);
//			model3902015.select(m390);
//		}
//		
		int i = deckPanel.getWidgetIndex(formPanel);
		deckPanel.showWidget(i);
	}
	
	private void newModel(int year) {
//		if (year == 2013 || year == 2014) {
//			Model3902014 model3902014 = new Model3902014(mod390CallBack);
//			formPanel.setWidget(model3902014);
//			model3902014.onNew(year);		
//		} else if (year == 2015 || year == 2016 || year == 2017) {
//			Model3902015 model3902015 = new Model3902015(mod390CallBack);
//			formPanel.setWidget(model3902015);
//			model3902015.onNew(year);		
//		}
		int i = deckPanel.getWidgetIndex(formPanel);
		deckPanel.showWidget(i);
	}

	@UiHandler("table")
	void onTableRangeChange(RangeChangeEvent event) {
		fiscalService.getMod390s(getCurrentDomainName(), getCurrentDomain(),
				new AsyncCallback<LinkedList<Mod390>>() {
					@Override
					public void onSuccess(LinkedList<Mod390> result) {
						if (result == null || result.size() == 0) {
							int i = deckPanel.getWidgetIndex(formPanel);
							deckPanel.showWidget(i);
							newModel(2017);
						} else {
							int i = deckPanel.getWidgetIndex(listPanel);
							table.setRowData(result);
							deckPanel.showWidget(i);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						DialogMessages.alertErrorWidget(AON.MSG.unableToReadDeclaration(caught.getMessage()));
					}
				});
	}

	@UiHandler("newButton")
	void onNewButtonClick(ClickEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		newContextMenu.setPopupPosition(nativeEvent.getClientX(),nativeEvent.getClientY());
		newContextMenu.show();
	}

	void onCancel() {
		int i = deckPanel.getWidgetIndex(listPanel);
		deckPanel.showWidget(i);
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
	}

	public class NewContextMenu extends ContextMenu {
		
		public NewContextMenu() {
			addNewMod3902017();
			addSeparator();
			addNewMod3902016();
			addSeparator();
			addNewMod3902015();
			addSeparator();
			addNewMod3902014();
			addSeparator();
			addNewMod3902013();
			addStyleName(AON.AON_CSS.aonSelector());
		}
		
		public void addItem(FiscalModelType model, String text, ScheduledCommand cmd) {
			super.addItem(model.getValue(), text, cmd);
		}

		protected NewContextMenu addNewMod3902013() {
			addItem(FiscalModelType.M390,AON.MSG.newSomething( "390 - 2013" ), new ScheduledCommand() {
						@Override
						public void execute() {
							newModel(2013);
						}
			});
			return this; 
		}
		protected NewContextMenu addNewMod3902014() {
			addItem(FiscalModelType.M390,AON.MSG.newSomething( "390 - 2014" ), new ScheduledCommand() {
						@Override
						public void execute() {
							newModel(2014);
						}
			});
			return this; 
		}
		protected NewContextMenu addNewMod3902015() {
			addItem(FiscalModelType.M390,AON.MSG.newSomething( "390 - 2015" ), new ScheduledCommand() {
						@Override
						public void execute() {
							newModel(2015);
						}
			});
			return this; 
		}
		protected NewContextMenu addNewMod3902016() {
			addItem(FiscalModelType.M390,AON.MSG.newSomething( "390 - 2016" ), new ScheduledCommand() {
						@Override
						public void execute() {
							newModel(2016);
						}
			});
			return this; 
		}
		
		protected NewContextMenu addNewMod3902017() {
			addItem(FiscalModelType.M390,AON.MSG.newSomething( "390 - 2017" ), new ScheduledCommand() {
						@Override
						public void execute() {
							newModel(2017);
						}
			});
			return this; 
		}
	}
	
	
	public static enum ValidationMessages {
		// PAGE00
		 EMPTY_YEAR		(new ValidationMessage(0, AON.MSG.requiredField(AON.MSG.fiscalYear())))
		,EMPTY_DOCUMENT	(new ValidationMessage(0, AON.MSG.requiredField(AON.MSG.document())))
		,WRONG_DOCUMENT	(new ValidationMessage(0, "El NIF/DNI no es correcto"))
		,REQ_NAME 		(new ValidationMessage(0, "Para personas f\u00EDsicas, el nombre es obligatorio."))
		,REQ_SURNAME 	(new ValidationMessage(0, "Para personas f\u00EDsicas, el primer apellido es obligatorio."))
		,EMPTY_NAME 	(new ValidationMessage(0, "No se ha indicado el nombre del declarante."))
		// PAGE01
		,EMPTY_ACTI 	(new ValidationMessage(1, "No se ha indicado actividad principal."))
		// PAGE02
		,EMPTY_REPR 	(new ValidationMessage(2, "Indique datos del represante."))
		,EMPTY_REPR_DOC	(new ValidationMessage(2, "Para personas f\u00EDsicas, el NIF/DNI del representante es obligatorio."))
		,WRONG_REPR_DOC	(new ValidationMessage(2, "El NIF/DNI del representante no es correcto."))
		,LG1_WRONG_DOC	(new ValidationMessage(2, "El NIF del primer representante para personas jur\u00EDdicas no es correcto."))
		,LG2_WRONG_DOC	(new ValidationMessage(2, "El NIF del segundo representante para personas jur\u00EDdicas no es correcto."))
		,LG3_WRONG_DOC	(new ValidationMessage(2, "El NIF del tercer representante para personas jur\u00EDdicas no es correcto."))
		;
		
		private ValidationMessage msg;
		private ValidationMessages(ValidationMessage msg) {
			this.msg = msg;
		}
		public ValidationMessage getMsg() {
			return msg;
		}
	}
}

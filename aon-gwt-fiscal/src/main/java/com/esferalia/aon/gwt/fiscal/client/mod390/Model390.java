package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.ContextMenu;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Model390 extends MainEntryPoint {

	static interface IMod390CallBack {
		void onCancel();
	}	

	interface Model390Binder extends UiBinder<Widget, Model390> {
	}

	private static final Model390Binder MODEL_390_BINDER = GWT
			.create(Model390Binder.class);

	private Mod3902014 mod390;
	private FiscalServiceAsync fiscalService;
	private NewContextMenu newContextMenu;

	private static final Integer DEFAULT_YEAR = 2015;

	private int domain;
	private int enterprise;

	@UiField
	DeckLayoutPanel deckPanel;
	@UiField
	DockLayoutPanel listPanel;
	@UiField(provided = true)
	Model3902014 formPanel;


	@UiField(provided = true)
	Model390Table table;

	@UiField
	Button newButton;
	
	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		IMod390CallBack mod390CallBack = new IMod390CallBack() {
			
			@Override
			public void onCancel() {
				Model390.this.onCancel();
			}
		};
		formPanel = new Model3902014(mod390CallBack);
		table = new Model390Table(new Mod390SelectionHandler());
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

	class Mod390SelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Mod3902014 sel = table.getSelected();
			fiscalService.getMod390(getCurrentDomainName(), getCurrentDomain(),
					sel.getId(), new AsyncCallback<Mod3902014>() {
				@Override
				public void onSuccess(Mod3902014 selected) {
					if (selected == null) {
						DialogMessages.alertErrorWidget(AON.MSG.unableToFindMod190());
					} else {
						select(selected);
						int i = deckPanel.getWidgetIndex(formPanel);
						deckPanel.showWidget(i);
						formPanel.prepareNew();
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					DialogMessages.alertErrorWidget(AON.MSG.unableToReadMod190(caught.getMessage()));
				}
			});
		}
	}

	private void select(Mod3902014 m390) {
		formPanel.select(m390);
	}

	@UiHandler("table")
	void onTableRangeChange(RangeChangeEvent event) {
		fiscalService.getMod390s(getCurrentDomainName(), getCurrentDomain(),
				new AsyncCallback<LinkedList<Mod3902014>>() {
					@Override
					public void onSuccess(LinkedList<Mod3902014> result) {
						if (result == null || result.size() == 0) {
							onNewButtonClick(null);
						} else {
							int i = deckPanel.getWidgetIndex(listPanel);
							table.setRowData(result);
							deckPanel.showWidget(i);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						DialogMessages.alertErrorWidget(AON.MSG.unableToReadMod190(caught.getMessage()));
					}
				});
	}

	@UiHandler("newButton")
	void onNewButtonClick(ClickEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		newContextMenu.setPopupPosition(nativeEvent.getClientX(),
				nativeEvent.getClientY());
		newContextMenu.show();
	}

	void onCancel() {
		int i = deckPanel.getWidgetIndex(listPanel);
		deckPanel.showWidget(i);
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
	}

	public class NewContextMenu extends ContextMenu {
		
		public NewContextMenu() {
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
							int i = deckPanel.getWidgetIndex(formPanel);
							deckPanel.showWidget(i);
							formPanel.onNewButtonClick(null);
						}
			});
			return this; 
		}
		protected NewContextMenu addNewMod3902014() {
			addItem(FiscalModelType.M390,AON.MSG.newSomething( "390 - 2014" ), new ScheduledCommand() {
						@Override
						public void execute() {
							int i = deckPanel.getWidgetIndex(formPanel);
							deckPanel.showWidget(i);
							formPanel.onNewButtonClick(null);
						}
			});
			return this; 
		}
		protected NewContextMenu addNewMod3902015() {
			addItem(FiscalModelType.M390,AON.MSG.newSomething( "390 - 2015" ), new ScheduledCommand() {
						@Override
						public void execute() {
							int i = deckPanel.getWidgetIndex(formPanel);
							deckPanel.showWidget(i);
							formPanel.onNewButtonClick(null);
						}
			});
			return this; 
		}
		
	}
	
	
}

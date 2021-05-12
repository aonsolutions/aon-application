package com.esferalia.aon.gwt.fiscal.client.widget;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.fiscal.client.FiscalMSService;
import com.esferalia.aon.gwt.fiscal.client.FiscalMSServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalMSServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.fiscal.Activity;
import com.esferalia.aon.occam.api.model.type.ActivityGroup;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;

public class ActivityPanel extends CustomDialog {

	public static final ProvidesKey<Activity> ACTIVITY_PROVIDES_KEY = new ProvidesKey<Activity>() {
		@Override
		public Object getKey(Activity activity) {
			return activity == null ? null : activity.getEpigraph();
		}
	};

	public interface SelectionCallBack {
		void onSelect(Activity activity);

		void onClose();
	}

	private final AonResources AON_RESOURCES = GWT.create(AonResources.class);

	interface ActivityPanelBinder extends UiBinder<Widget, ActivityPanel> {
	}

	private static final ActivityPanelBinder activityPanelBinder = GWT
			.create(ActivityPanelBinder.class);

	private FiscalMSServiceAsync fiscalService;
	
	private SelectionCallBack callback;

	private NoSelectionModel<Activity> model;

	@UiField(provided = true)
	ListBox activityGroup;

	@UiField
	DeckLayoutPanel deckPanel;
	@UiField
	Panel tablePanel;
	@UiField(provided = true)
	CellTable<Activity> table;

	private Activity selected;

	@UiField
	Panel descriptionPanel;
	@UiField
	TextBox description;
	@UiField
	Button acceptDescription;

	public ActivityPanel(SelectionCallBack callback) {
		this();
		setCallback(callback);
	}

	public ActivityPanel() {
		setVisible(false);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setModal(true);
		setCaption(AON.MSG.activitySelection());

		AON_RESOURCES.css().ensureInjected();
		CellTable.Resources tableStyle = GWT.create(AonCellTable.class);

		table = new CellTable<Activity>(1, tableStyle, ACTIVITY_PROVIDES_KEY);
		table.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		addSelectorColumn();
		addEpigraphColumn();
		addDescriptionColumn();

		model = new NoSelectionModel<Activity>(ACTIVITY_PROVIDES_KEY);
		model.addSelectionChangeHandler(new ActivitySelectionHandler());
		table.setSelectionModel(model);
		table.setEmptyTableWidget(new HTML(AON.MSG.noData()));

		FiscalMSServiceAsync fiscalServiceRaw = GWT.create(FiscalMSService.class);
		fiscalService = new FiscalMSServiceAsyncDecorator(fiscalServiceRaw);
		activityGroup = new ListBox();
		Widget ui = activityPanelBinder.createAndBindUi(this);
		setWidget(ui);

		activityGroup.addItem("-------------", new String());
		for (ActivityGroup ag : ActivityGroup.values()) {
			activityGroup.addItem(AON.MSG.activityGroup(ag));
		}
		descriptionPanel.setVisible(false);
	}

	public void setCallback(SelectionCallBack callback) {
		this.callback = callback;
		activityGroup.setItemSelected(0, true);
		table.setRowData(new LinkedList<Activity>());
		table.setRowCount(0, true);
		int i = deckPanel.getWidgetIndex(tablePanel);
		deckPanel.showWidget(i);
		selected = null;
		description.setText(null);
	}

	@Override
	public void onClose() {
		this.hide();
		callback.onClose();
	}

	private void addSelectorColumn() {
		final Column<Activity, ImageResource> selectorColumn = new Column<Activity, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Activity activity) {
				return AON_RESOURCES.aonIconRowSelector();
			}
		};
		table.addColumn(selectorColumn);
		table.setColumnWidth(selectorColumn, 20, Unit.PX);
	}

	private void addEpigraphColumn() {
		final TextColumn<Activity> epigraphColumn = new TextColumn<Activity>() {
			@Override
			public String getValue(Activity activity) {
				return activity.getEpigraph();
			}
		};
		table.addColumn(epigraphColumn, AON.MSG.epigraph());
		epigraphColumn.setCellStyleNames(AON_RESOURCES.css().aonTextCenter());
		table.setColumnWidth(epigraphColumn, 100, Unit.PX);
	}

	private void addDescriptionColumn() {
		final TextColumn<Activity> descriptionColumn = new TextColumn<Activity>() {
			@Override
			public String getValue(Activity activity) {
				return activity.getDescription();
			}
		};
		table.addColumn(descriptionColumn, AON.MSG.fiscalYear());
		descriptionColumn.setCellStyleNames(AON_RESOURCES.css().aonTextLeft());
		table.setColumnWidth(descriptionColumn, 90, Unit.PCT);
	}

	@UiHandler("activityGroup")
	void onChangeGroup(ChangeEvent event) {
		int i = activityGroup.getSelectedIndex() - 1;
		if (i >= 0 && (ActivityGroup.values()[i] == ActivityGroup.GROUP5 
				    || ActivityGroup.values()[i] == ActivityGroup.GROUP6)) {
			i = deckPanel.getWidgetIndex(descriptionPanel);
			deckPanel.showWidget(i);
		} else {
			i = deckPanel.getWidgetIndex(tablePanel);
			deckPanel.showWidget(i);
			table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
		}
	}

	@UiHandler("table")
	void onTableRangeChange(RangeChangeEvent event) {
		int i = activityGroup.getSelectedIndex() - 1;
		if (i >= 0) {
			ActivityGroup ag = ActivityGroup.values()[i];
			fiscalService.getActivities(ag.ordinal(),
					new AsyncCallback<LinkedList<Activity>>() {
						@Override
						public void onSuccess(LinkedList<Activity> result) {
							table.setRowData(result);
							table.setRowCount(result.size(), true);
						}

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(AON.MSG.unexpectedError(caught.getMessage()));
						}
					});
		} else {
			table.setRowData(new LinkedList<Activity>());
			table.setRowCount(0, true);
			table.redraw();
		}
	}

	@UiHandler("acceptDescription")
	void onAcceptDescriptionClick(ClickEvent event) {
		ActivityGroup ag = ActivityGroup.values()[activityGroup
				.getSelectedIndex() - 1];
		selected = new Activity();
		selected.setDescription(description.getValue());
		selected.setEpigraph(null);
		selected.setKey(ag.getKey());
		this.hide();
		callback.onSelect(selected);
	}

	class ActivitySelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			selected = model.getLastSelectedObject();
			hide();
			ActivityGroup ag = ActivityGroup.values()[activityGroup
					.getSelectedIndex() - 1];
			selected.setKey(ag.getKey());
			callback.onSelect(selected);
		}
	}

}

package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonEnterpriseActivityPanel.AonEnterpriseActivityPanelCallback;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class EnterpriseActivityTable extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;

	private static final Logger LOGGER = Logger.getLogger(EnterpriseActivityTable.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt(0);

	private SimplePanel container;
	private ScrollPanel scrollPanel;
	private AonCustomTable tab;
	private int lastScrollPos = 0;

	private String domainName;
	private Integer domain;
	private String user;
	private Integer registry;

	private static enum COLS {
		PRI("Principal", "4rem", ""),
		DES(AON.MSG.description(), "15rem",  "max-width: 15rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		CNA("CNAE 2025", "-moz-available", "min-width: 25rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		CCC("CCCs", "3rem", ""), 
		BUT(AonStringUtils.EMPTY, "3rem", "");

		String headerLabel;
		String colWidth;
		String styles;

		private COLS(String headerLabel, String colWidth, String styles) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.styles = styles;
		}

		public String getColWidth() {
			return colWidth;
		}

		public String getHeaderLabel() {
			return headerLabel;
		}

		public String getStyles() {
			return styles;
		}
	}

	public EnterpriseActivityTable(String domainName, int domain, String user, Integer registry) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		this.domainName = domainName;
		this.domain = domain;
		this.user = user;
		this.registry = registry;

		container = new SimplePanel();
		container.getElement().getStyle().setProperty("max-height", "200px");
		container.getElement().getStyle().setProperty("padding-left", "1px");
		setWidget(container);

		addScrollHandler(new ScrollHandler() {

			public void onScroll(ScrollEvent event) {
				// ------------------------------------ Ignore scroll up.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = getVerticalScrollPosition();
				if (oldScrollPos >= lastScrollPos) {
					return;
				}
				// -----------------------------------------------------
				if (isSearchEnabled()) {
					int maxScrollTop = getWidget().getOffsetHeight() - getOffsetHeight();
					if (lastScrollPos >= maxScrollTop) {
						disableSearch();
						searchData();
					}
				}
			}
		});

		onSearch();
	}

	public boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0);
	}

	public void disableSearch() {
		searchEnabled.setValue(-1);
	}

	public void enableSearch() {
		searchEnabled.setValue(0);
	}

	public boolean isMoreData() {
		return (moreData.getValue() == 0);
	}

	public void disableMoreData() {
		moreData.setValue(-1);
	}

	public void enableMoreData() {
		moreData.setValue(0);
	}

	public void onSearch() {
		enableMoreData();
		search();
	}

	private void search() {
		container.clear();
		tab = new AonCustomTable();
		tab.setMaxHeight("160x");
		tab.getElement().getStyle().setProperty("padding", "1rem 0");
		scrollPanel = new ScrollPanel(tab);

		paintHeader();
		container.setWidget(scrollPanel);
		searchData();
	}

	private void paintHeader() {
		tab.createHeader();
		for (COLS col : COLS.values()) {
			if (col.equals(COLS.BUT)) {
				FlowPanel buttonContainer = new FlowPanel();
				buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);

				AonTableButton button = new AonTableButton("Nueva Actividad", AON.CSS.aonIconAdd());
				button.addStyleName(AON.CSS.aonCustomRowButtom());
				button.getElement().getStyle().setProperty("border", "2px solid #434548");
				button.getElement().getStyle().setProperty("padding", "10px");
				button.getElement().getStyle().setProperty("border-radius", "50%");
				button.addClickHandler(e -> createEnterpriseActivity());
				buttonContainer.add(button);

				tab.addHeader(buttonContainer, col.getColWidth(), col.getStyles());
			} else
				tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());

		}
	}

	private void searchData() {
		if (!isMoreData())
			return;

		getList(enterpriseActivities -> {
			boolean something = false;

			for (Activity enterpriseActivity : enterpriseActivities) {
				something = true;
				paintRow(enterpriseActivity);
			}

			if (enterpriseActivities.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + enterpriseActivities.size() - 1);
				enableMoreData();
			}

			if (!something) {
				HTMLPanel row = tab.createRow();
				
				Label blank = new Label("");
				tab.addInlineStyle(blank, COLS.PRI.getStyles());
				tab.addRow(row, blank, COLS.PRI.getColWidth());
				
				Label empty = new Label("No exiten datos");
				tab.addInlineStyle(empty, COLS.DES.getStyles());
				tab.addRow(row, empty, COLS.DES.getColWidth());
				disableMoreData();
			}
			enableSearch();

		});
	}

	private void paintRow(Activity enterpriseActivity) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);

		AonTableButton button;
		button = new AonTableButton("Borrar Centro Trabajo", AON.CSS.aonIconDelete());
		button.addStyleName(AON.CSS.aonCustomRowButtom());
		button.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				button.setEnabled(false);
				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Media",
						new HTML("Se va a proceder a eliminar la actividad <b>" + enterpriseActivity.getDescription()
								+ "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));

				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						button.setEnabled(true);
					}

					@Override
					public void onAccept() {
						delete(enterpriseActivity);
					}
				});
			}
		});
		buttonContainer.add(button);

		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onUpdateEnterpriseActivity(enterpriseActivity), ClickEvent.getType());

		Button principal = new Button();
		getEnableDisableButton(principal, enterpriseActivity.isPrincipal());
		principal.setTitle(enterpriseActivity.isPrincipal() ? "Pricipal" : "Secundaria");
		tab.addInlineStyle(principal, COLS.PRI.getStyles());
		tab.addRow(row, principal, COLS.PRI.getColWidth());

		Label description = new Label(enterpriseActivity.getDescription());
		description.setTitle(enterpriseActivity.getDescription());
		tab.addInlineStyle(description, COLS.DES.getStyles());
		tab.addRow(row, description, COLS.DES.getColWidth());

		Label cnae = new Label(enterpriseActivity.getCnae25Code() + " - " + enterpriseActivity.getCnae25Description());
		cnae.setTitle(enterpriseActivity.getCnaeDescription());
		tab.addInlineStyle(cnae, COLS.CNA.getStyles());
		tab.addRow(row, cnae, COLS.CNA.getColWidth());

		Label cccs = new Label(enterpriseActivity.getCccs().isEmpty() ? "0" : enterpriseActivity.getCccs().size() + "");
		tab.addInlineStyle(cccs, COLS.CCC.getStyles());
		tab.addRow(row, cccs, COLS.CCC.getColWidth());

		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}

	private void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);
		
		button.setStyleName(!disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE );
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}

	private void getList(Consumer<List<Activity>> success) {
		COMMON_SERVICE.getEnterpriseActivities(domainName, domain, user, registry, new AsyncCallback<List<Activity>>() {

			@Override
			public void onSuccess(List<Activity> enterpriseActivities) {
				success.accept(enterpriseActivities);
			}

			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}

	private void delete(Activity enterpriseActivity) {
		COMMON_SERVICE.deleteEnterpriseActivity(domainName, domain, user, enterpriseActivity.getId(),
				new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						onSearch();
					}

					@Override
					public void onFailure(Throwable caught) {
						onShowErrorMessage("Error borrado: " + caught.getMessage());
					}
				});
	}

	private void onUpdateEnterpriseActivity(Activity enterpriseActivity) {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption("Editar Actividad");

		final AonEnterpriseActivityPanel marketingCampaignPanel = new AonEnterpriseActivityPanel(domainName, domain,
				user, enterpriseActivity, new AonEnterpriseActivityPanelCallback() {

					@Override
					public void onCancel() {
						dialog.hide();
					}

					@Override
					public void onAccept(Activity enterpriseActivity) {
						dialog.hide();
						onSearch();
					}

					@Override
					public void onLoadedEnd() {
						dialog.showLoaded();
					}
				});

		dialog.add(marketingCampaignPanel);
	}

	private void createEnterpriseActivity() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption("Nueva Actividad");

		final AonEnterpriseActivityPanel marketingCampaignPanel = new AonEnterpriseActivityPanel(domainName, domain,
				user, registry, new AonEnterpriseActivityPanelCallback() {

					@Override
					public void onCancel() {
						dialog.hide();
					}

					@Override
					public void onAccept(Activity enterpriseActivity) {
						dialog.hide();
						onSearch();
					}

					@Override
					public void onLoadedEnd() {
						dialog.showLoaded();
					}
				});

		dialog.add(marketingCampaignPanel);
		dialog.showLoaded();
	}

	protected abstract void onShowErrorMessage(String errorMessage);

}

package com.esferalia.aon.gwt.fiscal.client.tedi;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleTEDI;
import com.esferalia.aon.gwt.fiscal.client.accounting.utilities.CustomPopup;
import com.esferalia.aon.gwt.fiscal.client.tedi.TediCenter.TediCenterCallback;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.tedi.TediError;
import com.esferalia.aon.occam.api.model.tedi.TediLevel;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class TediInvoiceList extends DockLayoutPanel {

//	private static  final Logger LOGGER = Logger.getLogger(TediInvoiceListOLD.class.getName());
	
	private static TediServiceAsync SERVICE;

	private String currentDomainName;
	private int currentDomain;
	private String currentUser;
	private AonConfiguration configuration;
	private Company company;

	private TediCenterCallback callback;
	private SimpleLayoutPanel tabContainer = new SimpleLayoutPanel();
	private LinkedList<TediResult> list;
	private String color;

	public TediInvoiceList(String currentDomainName, int currentDomain, String currentUser, String color,
			TediCenterCallback callback) {
		super(Unit.PX);
		this.currentDomainName = currentDomainName;
		this.currentDomain = currentDomain;
		this.currentUser = currentUser;
		this.color = color;
		this.callback = callback;

		if (this.callback == null) {
			MessageDialog.error("No TEDI CENTER detected");
			throw new IllegalArgumentException("No TEDI CENTER detected");
		}

		TediServiceAsync serviceRaw = GWT.create(TediService.class);
		SERVICE = new TediServiceAsyncDecorator(serviceRaw);

		SERVICE.getAonConfiguration(this.currentDomainName, this.currentUser, this.currentDomain,
				new AsyncCallback<AonConfiguration>() {
					@Override
					public void onSuccess(AonConfiguration result) {
						TediInvoiceList.this.configuration = result;
						TediInvoiceList.this.company = configuration.getCompany();
						addNorth(getToolbarPanel(), 25);
						addNorth(paintFilterWidget(), 10);
						add(tabContainer);
						refreshData();
					}

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog.error(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
					}
				});
	}

	public void setData(LinkedList<TediResult> list) {
		this.list = list;
		tabContainer.clear();
		tabContainer.setWidget(getTableWidget(list));
	}

	private Widget getToolbarPanel() {
		FlowPanel toolbarPanel = new FlowPanel();
		toolbarPanel.setStyleName(AON.AON_CSS.aonFindingTitleToolbar());
		toolbarPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		FlexTable toolbar = new FlexTable();
		toolbar.setCellPadding(0);
		toolbar.setCellSpacing(0);
		toolbar.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.setStyleName(AON.AON_CSS.aonFindingTitleInternal());
		toolbar.setWidget(0, 0, titlePanel);
		toolbar.setWidget(0, 0, new Label("tEDI center - " + company.getName()));
		toolbar.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFindingToolbar());

		final Button clean = new Button();
		clean.setText(AON.MSG.refresh());
		clean.setTitle(AON.MSG.refresh());
		clean.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		clean.addStyleName(AON.AON_CSS.aonIconSearch());
		clean.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				refreshData();
			}
		});
		buttonContainer.add(clean);
/*
		final Button acceptAll = new Button();
		acceptAll.setText(AON.MSG.accept());
		acceptAll.setTitle(AON.MSG.accept());
		acceptAll.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		acceptAll.addStyleName(AON.AON_CSS.aonIconSave());
		acceptAll.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onAcceptAll();
			}
		});
		buttonContainer.add(acceptAll);

		final Button rejectAll = new Button();
		rejectAll.setText(AON.MSG.reject());
		rejectAll.setTitle(AON.MSG.reject());
		rejectAll.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		rejectAll.addStyleName(AON.AON_CSS.aonIconDelete());
		rejectAll.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onRejectAll();
			}
		});
		buttonContainer.add(rejectAll);
*/
		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}

	/*
	private void onReject(TediResult result) {
		SERVICE.rejectInvoice(this.currentDomainName, this.currentUser, this.currentDomain, isTediSnapshot(),
				result.getTedi(), new AsyncCallback<TediResult>() {

					@Override
					public void onSuccess(TediResult result) {
						callback.onDettachTab(result.getUuid());
						refreshData();
					}

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog.error(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
					}
				});
	}

	private void onRejectAll() {
		LinkedList<TediResult> selectedList = getSelectedList();
		if (selectedList != null && selectedList.size() > 0) {
			ConfirmDialog cd = new ConfirmDialog();
			String msg = (selectedList.size() == 1) ? "\u00BFDesea rechazar la factura seleccionada?"
					: "\u00BFDesea rechazar las " + selectedList.size() + " facturas seleccionadas?";
			cd.confirm(msg, new ConfirmDialogCallback() {

				@Override
				public void onCancel() {
				}

				@Override
				public void onAccept() {
					SERVICE.rejectInvoices(currentDomainName, currentUser, currentDomain, isTediSnapshot(),
							selectedList, new AsyncCallback<LinkedList<TediResult>>() {

								@Override
								public void onSuccess(LinkedList<TediResult> results) {
									for (TediResult result : results) {
										callback.onDettachTab(result.getUuid());
									}
									refreshData();
								}

								@Override
								public void onFailure(Throwable caught) {
									MessageDialog.error(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
								}

							});
				}
			});
		} else {
			MessageDialog.error("No ha seleccionado ninguna factura");
		}
	}
*/	
	private void onAccept(TediResult result) {
		SERVICE.acceptInvoice(this.currentDomainName, this.currentUser, this.currentDomain, isTediSnapshot(),
				result.getTedi(), new AsyncCallback<TediResult>() {

					@Override
					public void onSuccess(TediResult result) {
						callback.onDettachTab(result.getUuid());
						refreshData();
					}

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog.error(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
					}
				});
	}
/*
	private void onAcceptAll() {
		LinkedList<TediResult> selectedList = getSelectedList();
		if (selectedList != null && selectedList.size() > 0) {
			ConfirmDialog cd = new ConfirmDialog();
			String msg = (selectedList.size() == 1) ? "\u00BFDesea registrar la factura seleccionada?"
					: "\u00BFDesea registrar las " + selectedList.size() + " facturas seleccionadas?";
			cd.confirm(msg, new ConfirmDialogCallback() {

				@Override
				public void onCancel() {
				}

				@Override
				public void onAccept() {
					SERVICE.acceptInvoices(currentDomainName, currentUser, currentDomain, isTediSnapshot(), selectedList,
							new AsyncCallback<LinkedList<TediResult>>() {

								@Override
								public void onSuccess(LinkedList<TediResult> results) {
									for (TediResult result : results) {
										callback.onDettachTab(result.getUuid());
									}
									refreshData();
								}

								@Override
								public void onFailure(Throwable caught) {
									MessageDialog.error(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
								}

							});
				}
			});
		} else {
			MessageDialog.error("No ha seleccionado ninguna factura");
		}
	}
	*/

	private void refreshData() {
		FlowPanel p = new FlowPanel();
		p.setStyleName(AON.AON_CSS.aonWidthAll());
		p.addStyleName(AON.AON_CSS.aonTextCenter());
		Label wait = new Label("Cargando. Un momento, por favor .....");
		wait.setStyleName(AON.AON_CSS.aonTextCenter());
		wait.addStyleName(AON.AON_CSS.aonBold());
		wait.addStyleName(AON.AON_CSS.aonColoRoyalblue());
		wait.addStyleName(AON.AON_CSS.aonMarginTop());
		p.add(wait);

		tabContainer.setWidget(p);
		SERVICE.getVerifiedInvoices(this.currentDomainName, this.currentUser, this.currentDomain, isTediSnapshot(),
				this.company, new AsyncCallback<LinkedList<TediResult>>() {
					@Override
					public void onSuccess(LinkedList<TediResult> results) {
						setData(results);
					}

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog.error(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
					}
				});
	}

	private boolean isTediSnapshot() {
		return callback.isSnapshot();
	}

	private Widget paintFilterWidget() {
		SimpleLayoutPanel northPanel = new SimpleLayoutPanel();
		FlexTable filterTab = new FlexTable();
		filterTab.setStyleName(AON.AON_CSS.aonPanelGridSearch());
		filterTab.addStyleName(AON.AON_CSS.aonWidthAll());
		filterTab.setWidget(0, 0, new Label("."));
		northPanel.setWidget(filterTab);
		return northPanel;
	}

	private Widget getTableWidget(LinkedList<TediResult> list) {
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(AON.AON_CSS.aonScrollArea());

		if (list == null || list.size() == 0) {
			Label noData = new Label("No se han encontrado facturas pendientes");
			noData.setStyleName(AON.AON_CSS.aonTextCenter());
			noData.addStyleName(AON.AON_CSS.aonBold());
			noData.addStyleName(AON.AON_CSS.aonMarginTop());
			scrollPanel.setWidget(noData);
		} else {
			FlexTable tab = new FlexTable();
			tab.setStyleName(AON.AON_CSS.aonDataTable());
			tab.addStyleName(AON.AON_CSS.aonBlockCenter());
			tab.addStyleName(AON.AON_CSS.aonReport());

			int row = 0;
			int cell = 0;
			String[] widths = { "20px", "20px", "20px", "80px", "20px", "80px", "150px", "150px", "auto", "100px" };
			String[] labels = { "", "", "", AON.MSG.type(), "", AON.MSG.date(), AON.MSG.invoiceNumber(),
					AON.MSG.document(), AON.MSG.name(), AON.MSG.total() };
			for (String w : widths) {
				tab.getCellFormatter().setStyleName(row, cell, AON.AON_CSS.aonDataTableHeader());
				tab.getColumnFormatter().setWidth(cell, w);
				tab.setWidget(row, cell, new Label(labels[cell]));
				cell++;
			}
			tab.getCellFormatter().addStyleName(row, cell - 1, AON.AON_CSS.aonTextRight());
			row++;

			for (TediResult result : list) {
				cell = 0;
				tab.getRowFormatter().setStyleName(row, AON.AON_CSS.aonReportRow());

				Label selectButton = new Label();
				selectButton.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
				selectButton.addStyleName(AON.AON_CSS.aonClickableLabel());
				selectButton.addStyleName(AON.AON_CSS.aonIconRowSelector());
				selectButton.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						onSelect(result, true);
					}
				});
				tab.setWidget(row, cell, selectButton);
				cell++;

				Label checkButton = new Label();
				checkButton.setStyleName(AON.AON_CSS.aonClickableLabel());
				checkButton.addStyleName(AON.AON_CSS.aonBold());
				checkButton.addStyleName(AON.AON_CSS.aonTextCenter());
				if (result.isChecked()) {
					tab.getRowFormatter().addStyleName(row, AON.AON_CSS.aonDataTableRowHighlight());
				}
				String unchecked = "\u25A2";
				String checked = "\u25A3";
				checkButton.setText(result.isChecked() ? checked : unchecked);
				final int rw = row;
				checkButton.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						result.setChecked(!result.isChecked());
						checkButton.setText(result.isChecked() ? checked : unchecked);
						if (result.isChecked()) {
							tab.getRowFormatter().addStyleName(rw, AON.AON_CSS.aonDataTableRowHighlight());
						} else {
							tab.getRowFormatter().removeStyleName(rw, AON.AON_CSS.aonDataTableRowHighlight());
						}
					}
				});
				tab.setWidget(row, cell, checkButton);
				cell++;

				Label errorsButton = new Label();
				errorsButton.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
				TediLevel curLevel = result.getMoreSeriousLevel();
				if (curLevel == TediLevel.INF) {
					errorsButton.addStyleName(AON.AON_CSS.aonIconPointLightGreen());
				} else if (curLevel == TediLevel.WRN) {
					errorsButton.addStyleName(AON.AON_CSS.aonIconPointOrange());
				} else if (curLevel == TediLevel.ERR) {
					errorsButton.addStyleName(AON.AON_CSS.aonIconPointRed());
				} else {
					errorsButton.addStyleName(AON.AON_CSS.aonIconPointGreen());
				}
				if (curLevel != null) {
					errorsButton.addStyleName(AON.AON_CSS.aonClickableLabel());
					errorsButton.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							CustomPopup popup = new CustomPopup();
							popup.setWidth("600px");
							popup.setHeight("300px");
							popup.setAnimationEnabled(true);
							popup.setGlassEnabled(true);
							popup.setModal(true);
							popup.setCaption("Avisos");
							ScrollPanel scr = new ScrollPanel();
							FlowPanel toastPanel = new FlowPanel();
							toastPanel.setStyleName(AON.AON_CSS.aonMarginTop());
							toastPanel.addStyleName(AON.AON_CSS.aonMarginLeft());
							toastPanel.addStyleName(AON.AON_CSS.aonMarginRight());
							scr.setWidget(toastPanel);
							for (TediError error : result.getMessages()) {
								FlowPanel flowPanel = new FlowPanel();
								InlineLabel colorLabel = new InlineLabel("");
								colorLabel.setStyleName(AON.AON_CSS.aonPaddingLeft());
								colorLabel.addStyleName(AON.AON_CSS.aonPaddingRight());
								colorLabel.getElement().getStyle()
										.setBackgroundColor(getBackgroundColor(error.getLevel()));
								flowPanel.add(colorLabel);

								InlineLabel errLabel = new InlineLabel(error.getLevel().getLabel());
								errLabel.setStyleName(AON.AON_CSS.aonClickableLabel());
								errLabel.addStyleName(AON.AON_CSS.aonPaddingLeft());
								errLabel.addStyleName(AON.AON_CSS.aonPaddingRight());
								errLabel.addStyleName(AON.AON_CSS.aonBold());
								flowPanel.add(errLabel);

								InlineLabel msgLabel = new InlineLabel(error.getMessage());
								msgLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
								msgLabel.addStyleName(AON.AON_CSS.aonBorderBottomImportant());
								flowPanel.add(msgLabel);
								toastPanel.add(flowPanel);
							}
							popup.add(scr);
							popup.center();
							popup.show();
						}
					});
				}
				tab.setWidget(row, cell, errorsButton);
				cell++;

				Label type = new Label(result.getInvoice().getType().getDescription());
				tab.setWidget(row, cell, type);
				cell++;

				Label attachButton = new Label();
				attachButton.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
				attachButton
						.addStyleName(result.hasAttach() ? AON.AON_CSS.aonIconAttach() : AON.AON_CSS.aonIconBlocked());
				tab.setWidget(row, cell, attachButton);
				cell++;

				Label issueDate = new Label(result.getInvoice().getIssueDate() != null
						? AON.DATE_FORMAT.format(result.getInvoice().getIssueDate())
						: "");
				tab.setWidget(row, cell, issueDate);
				cell++;

				Label referenceCode = new Label(result.getInvoice().getReferenceCode());
				tab.setWidget(row, cell, referenceCode);
				tab.getCellFormatter().setStyleName(row, cell, AON.AON_CSS.aonNowrap());
				cell++;

				Label document = new Label(result.getInvoice().getRegistryDocument());
				tab.setWidget(row, cell, document);
				cell++;

				Label name = new Label(result.getInvoice().getRegistryName());
				tab.setWidget(row, cell, name);
				cell++;

				Label total = new Label(AON.FMT.format(result.getInvoice().getTotal()));
				tab.setWidget(row, cell, total);
				tab.getCellFormatter().setStyleName(row, cell, AON.AON_CSS.aonTextRight());
				cell++;

				row++;
			}
			scrollPanel.setWidget(tab);
		}
		return scrollPanel;
	}

	public LinkedList<TediResult> getSelectedList() {
		LinkedList<TediResult> selected = new LinkedList<TediResult>();
		if (this.list != null) {
			for (TediResult r : this.list) {
				if (r.isChecked()) {
					selected.add(r);
				}
			}
		}
		return selected;
	}

	public void clearSelection() {
		for (TediResult r : this.list) {
			r.setChecked(false);
		}
	}

	private void onSelect(TediResult result, boolean forceRefresh) {
		SplitLayoutPanel tediSplitPanel;
		boolean paint = false;
		if (callback.isAttached(result.getTedi().getUuid())) {
			tediSplitPanel = (SplitLayoutPanel) callback.selectTab(result.getTedi().getUuid());
			if (forceRefresh) {
				tediSplitPanel.clear();
				paint = true;
			}
		} else {
			paint = true;
			tediSplitPanel = new SplitLayoutPanel(10);
			String tabLabel = AonStringUtils.trim(AonStringUtils.abbreviate(
					(result.getInvoice().getType() != null ? result.getInvoice().getType().getDescription() : " ") + " "
							+ AonStringUtils.defaultString((result.getTedi().getRegistry() != null ? result.getTedi().getRegistry().getName() : ""),"?"),
					25));
			InvoicesCloseTab closeTab = new InvoicesCloseTab(tabLabel,this.color, true);
			closeTab.addCloseHandler(new CloseHandler<Integer>() {
				@Override
				public void onClose(CloseEvent<Integer> event) {
					callback.onDettachTab(result.getTedi().getUuid());
				}
			});
			callback.onAttachTab(result.getTedi().getUuid(), tediSplitPanel, closeTab);
		}
		if (paint) {
			SimpleLayoutPanel contentPanel = new SimpleLayoutPanel();
			tediSplitPanel.add(contentPanel);
			paintAccountEntryModule(contentPanel, result);
		}
	}
	
	private void paintAccountEntryModule(SimplePanel container, TediResult result) {
		SERVICE.fillAttach(this.currentDomainName, this.currentUser, this.currentDomain, isTediSnapshot(), result, new AsyncCallback<TediResult>() {

					@Override
					public void onSuccess(TediResult result) {
						AccountEntryModuleTEDI module = new AccountEntryModuleTEDI();
						module.onModuleLoad(new AccountEntryModuleOptions()
								.setParentWidget(container)
								.setDomainName(TediInvoiceList.this.currentDomainName)
								.setDomain(TediInvoiceList.this.currentDomain)
								.setUser(TediInvoiceList.this.currentUser)
								.setConfiguration(TediInvoiceList.this.configuration)
								.setAccountingInvoice(result.getAccountingInvoice())
								.setTediResult(result)
								.setBackButtonVisible(false)
								.setSessionLogTabVisible(false)
								.setPreviewSectionVisible(true)
								.setBalancesSectionVisible(false)
								.setStatementTabVisible(false)
								.setJournalTabVisible(false)
								.setExtraInfoTabVisible(false)
								.setExternalCallback(new ModuleCallback() {

									@Override
									public void onRemove(IAccountEntryWrapper removed) {
									}

									@Override
									public void onFailure(Throwable caught) {
									}

									@Override
									public void onExit() {
									}

									@Override
									public void onChange(IAccountEntryWrapper changed) {
										result.setAon((AccountingInvoice) changed);
										onAccept(result);
									}
								}));
					}

					@Override
					public void onFailure(Throwable caught) {
						Label label =  new Label("Se ha producido un error al intentar mostrar el documento de la factura.");
						label.setStyleName(AON.AON_CSS.aonBold());
						label.addStyleName(AON.AON_CSS.aonColorRed());
						label.addStyleName(AON.AON_CSS.aonMargin());
						label.addStyleName(AON.AON_CSS.aonTextCenter());
						container.setWidget(label);
					}
				});
	}

	private String getBackgroundColor(TediLevel curLevel) {
		String color = null;
		if (curLevel == null) {
			color = "#e6ffe6";
		} else if (curLevel == TediLevel.INF) {
			color = "#e7f5fe";
		} else if (curLevel == TediLevel.WRN) {
			color = "#ffbf80";
		} else if (curLevel == TediLevel.ERR) {
			color = "#ffc2b3";
		}
		return color;
	}

}

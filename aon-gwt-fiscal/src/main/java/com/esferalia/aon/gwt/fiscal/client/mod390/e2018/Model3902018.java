package com.esferalia.aon.gwt.fiscal.client.mod390.e2018;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390.Model390Callback;
import com.esferalia.aon.gwt.fiscal.client.mod390.ValidationMessage;
import com.esferalia.aon.gwt.fiscal.client.mod390.ValidationMessage.ValidationMessages;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class Model3902018 extends DockLayoutPanel  {
	
	private static final Integer DEFAULT_YEAR = 2017;
	private static final  String MOD390_2018_PRINT = "/aon_gwt_fiscal/Model3902018Print";
	private static final  String MOD390_2018_FILE = "/aon_gwt_fiscal/Model3902018File";
			
	private Mod3902018ServiceAsync MOD390_SERVICE;
	
	static interface IMod3902018CallBack {
		String getDomainName();
		Integer getDomainId();
		String getUser();	
		void calculateAndRefresh();
		Mod3902018 getMod390();
		void showVisorAEAT();
		void showInfoPanel(String str);
		
	}	

	static interface IMod3902018Page extends IsWidget {
		void refresh(Mod3902018 m390);
		void populate(Mod3902018 m390);
		void setCallback( IMod3902018CallBack callback );
	}	
	
	private FlowPanel linkContainer;
	private DeckPanel pagesPanel;
	
	private FormPanel diskForm;
	private Hidden mod390Hidden = new Hidden("mod390");
	private Hidden domainIdHidden = new Hidden("domainId");
	private Hidden domainNameHidden = new Hidden("domainName");
	private Hidden userHidden = new Hidden("user");
	private AonData aonData;
	private Model390Callback cbk;
	public Model3902018(Mod390 mod390, final Model390Callback cbk, AonData aonData) {
		super(Unit.PX);
		this.aonData = aonData;
		this.cbk = cbk;
		AON.ensureInjected();

		Mod3902018ServiceAsync mod3902018ServiceRaw = GWT.create(Mod3902018Service.class);
		MOD390_SERVICE = new Mod3902018ServiceAsyncDecorator(mod3902018ServiceRaw);
		
		
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		try {
			MOD390_SERVICE.getMod3902018(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(),
					mod390, new AsyncCallback<Mod3902018>() {
				@Override
				public void onSuccess(Mod3902018 selected) {
					if (selected == null) {
						cbk.showError(AON.MSG.unableToFindDeclaration());
					} else {
						select(selected, cbk);
					}
					popup.hide();
				}

				@Override
				public void onFailure(Throwable caught) {
					popup.hide();
					cbk.showError(AON.MSG.unableToReadDeclaration(caught.getMessage()));
				}
			});
		} catch (IllegalArgumentException e) {
			popup.hide();
			cbk.showError(e.getMessage());
		}
	}

	private Widget getToolbar(Mod3902018 m390, Model390Callback cbk) {
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
		toolbar.setWidget(0, 0, new Label( AON.MSG.mod390()));
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		
		Button newButton = new Button();
		newButton.setVisible(!m390.isNew());
		newButton.setText(AON.MSG.newAction());
		newButton.setTitle(newButton.getText());
		newButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		newButton.addStyleName(AON.AON_CSS.aonIconReset());
		newButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cbk.cleanErrorPanel();
				cbk.onNew(DEFAULT_YEAR);
			}
		});
		buttonContainer.add(newButton);
		
		Button saveButton = new Button();
		saveButton.setVisible(!m390.isFinished() && !m390.isSent());
		saveButton.setText(AON.MSG.saveAction());
		saveButton.setTitle(newButton.getText());
		saveButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		saveButton.addStyleName(AON.AON_CSS.aonIconSave());
		saveButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final PopupPanel popup = new PopupPanel(false, true);
				Label label = new Label(AON.MSG.processing());
				label.addStyleName(AON.AON_CSS.aonTimer());
				popup.add(label);
				popup.setGlassEnabled(true);
				popup.setAnimationEnabled(true);
				popup.center();
				try {
					m390.setDomain(getCurrentDomain());
					m390.setConfidential(false);
					for (int i = 0 ; i < linkContainer.getWidgetCount(); i ++) {
						WestFocusPanel page = (WestFocusPanel) linkContainer.getWidget(i);
						page.populate(m390);	
					}
					cbk.cleanErrorPanel();
					validate(m390, cbk);
					MOD390_SERVICE.saveMod3902018(getCurrentDomainName(),getCurrentDomain(),getCurrentUser(),m390
							, new AsyncCallback<Mod3902018>() {
								@Override
								public void onSuccess(Mod3902018 result) {
									select(result, cbk);
									popup.hide();
								}

								@Override
								public void onFailure(Throwable caught) {
									popup.hide();
									cbk.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
								}
							});
				} catch (IllegalArgumentException e) {
					popup.hide();
					cbk.showError(e.getMessage());
				}
			}
		});
		buttonContainer.add(saveButton);

		Button cancelButton = new Button();
		cancelButton.setText(AON.MSG.cancelAction());
		cancelButton.setTitle(newButton.getText());
		cancelButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		cancelButton.addStyleName(AON.AON_CSS.aonIconCancel());
		cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cbk.cleanErrorPanel();
				cbk.onCancel();
			}
		});
		buttonContainer.add(cancelButton);

		Button deleteButton = new Button();
		deleteButton.setVisible(!m390.isNew() && !m390.isFinished() && !m390.isSent());
		deleteButton.setText(AON.MSG.deleteAction());
		deleteButton.setTitle(newButton.getText());
		deleteButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		deleteButton.addStyleName(AON.AON_CSS.aonIconDelete());
		deleteButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				deleteButton.setEnabled(false);
				ConfirmDialog cd = new ConfirmDialog();
				cd.confirm(AON.MSG.confirmDeclarationDeleteAction(), new ConfirmDialogCallback() {

					@Override
					public void onAccept() {
						MOD390_SERVICE.deleteMod3902018(getCurrentDomainName(),getCurrentDomain(),getCurrentUser(),
								m390, new AsyncCallback<Void>() {
							@Override
							public void onSuccess(Void result) {
								deleteButton.setEnabled(true);
								cbk.cleanErrorPanel();
								cbk.onCancel();
							}

							@Override
							public void onFailure(Throwable caught) {
								deleteButton.setEnabled(true);
								cbk.showError(AON.MSG.unableToDeleteDeclaration(caught.getMessage()));
							}
						});
					}

					@Override
					public void onCancel() {
						deleteButton.setEnabled(true);
					}
					
				});
			}
		});
		buttonContainer.add(deleteButton);
		
		Button markAsFinishedButton = new Button();
		markAsFinishedButton.setVisible(!m390.isNew() &&
				(m390.getStatus() == FiscalStatus.PENDING 
				|| m390.getStatus() == FiscalStatus.MISSING));
		markAsFinishedButton.setText(AON.MSG.finish());
		markAsFinishedButton.setTitle(markAsFinishedButton.getText());
		markAsFinishedButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		markAsFinishedButton.addStyleName(AON.AON_CSS.aonIconPointLightGreen());
		markAsFinishedButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				markAsFinishedButton.setEnabled(false);
				MOD390_SERVICE.changeStatus(getCurrentDomainName(), getCurrentUser(), m390, FiscalStatus.FINISHED, new AsyncCallback<Mod3902018>() {
					@Override
					public void onSuccess(Mod3902018 result) {
						select(result, cbk);
					}

					@Override
					public void onFailure(Throwable caught) {
						markAsFinishedButton.setEnabled(true);
						cbk.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
					}
				});
			}
		});
		buttonContainer.add(markAsFinishedButton);

		Button markAsSentButton = new Button();
		markAsSentButton.setVisible(!m390.isNew() && (m390.isFinished()));		
		markAsSentButton.setText(AON.MSG.markAsSent());
		markAsSentButton.setTitle(markAsSentButton.getText());
		markAsSentButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		markAsSentButton.addStyleName(AON.AON_CSS.aonIconPointGreen());
		markAsSentButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				markAsSentButton.setEnabled(false);
				MOD390_SERVICE.changeStatus(getCurrentDomainName(), getCurrentUser(), m390, FiscalStatus.SENT, new AsyncCallback<Mod3902018>() {
					@Override
					public void onSuccess(Mod3902018 result) {
						select(result, cbk);
					}

					@Override
					public void onFailure(Throwable caught) {
						markAsSentButton.setEnabled(true);
						cbk.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
					}
				});
			}
		});
		buttonContainer.add(markAsSentButton);
		
		Button markAsPendingButton = new Button();
		markAsPendingButton.setVisible(!m390.isNew() 
				&& (m390.isFinished() 
				|| m390.getStatus() == FiscalStatus.BATCHED 
				|| m390.isSent()
				|| m390.isBlocked()));
		markAsPendingButton.setText(AON.MSG.reopen());
		markAsPendingButton.setTitle(markAsPendingButton.getText());
		markAsPendingButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		markAsPendingButton.addStyleName(AON.AON_CSS.aonIconPointOrange());
		markAsPendingButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				markAsPendingButton.setEnabled(false);
				MOD390_SERVICE.changeStatus(getCurrentDomainName(), getCurrentUser(), m390, FiscalStatus.PENDING, new AsyncCallback<Mod3902018>() {
					@Override
					public void onSuccess(Mod3902018 result) {
						select(result, cbk);
					}

					@Override
					public void onFailure(Throwable caught) {
						markAsPendingButton.setEnabled(true);
						cbk.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
					}
				});
			}
		});
		buttonContainer.add(markAsPendingButton);
		
		toolbarPanel.add(toolbar);
				
		FlowPanel formContainer = new FlowPanel();
		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(mod390Hidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		formContainer.add(diskForm);
		toolbarPanel.add(formContainer);
		return toolbarPanel;
	}

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	public static native String getCurrentUser()
	/*-{
		return $wnd.getCurrentUser();
	}-*/;
	
	private void showPage(int page) {
		WestFocusPanel panel = (WestFocusPanel) linkContainer.getWidget(page);
		panel.showPage();	
	}
	

	public void select(Mod3902018 m390, Model390Callback cbk) {
		if ("INVALID".equals(m390.getXmlFormat()) ) {
			ConfirmDialog cd = new ConfirmDialog();
			cd.confirm( "La declaraci\u00F3n est\u00E1 creada con un formato anterior al del ejercicio 2018 y es inv\u00E1lida. \u00BFDesea borrarla?"
					, new ConfirmDialogCallback() {

				@Override
				public void onAccept() {
					MOD390_SERVICE.deleteMod3902018(getCurrentDomainName(),getCurrentDomain(),getCurrentUser(),
							m390, new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							cbk.cleanErrorPanel();
							cbk.onCancel();
						}

						@Override
						public void onFailure(Throwable caught) {
							cbk.showError(AON.MSG.unableToDeleteDeclaration(caught.getMessage()));
						}
					});
				}

				@Override
				public void onCancel() {
					cbk.cleanErrorPanel();
					cbk.onCancel();
				}
			});
		} else {
			clear();
			addNorth( getToolbar(m390,cbk), 26 );
			addNorth( getHeaderPanel(m390,cbk), 60 );
			addNorth( getDeclarationHeaderTable(m390,cbk) , 40);
			pagesPanel = new DeckPanel();  
			addWest( getLinksPanel(m390,cbk), 300 );
			ScrollPanel container = new ScrollPanel();
			container.addStyleName(AON.AON_CSS.aonScrollArea());
			container.add(pagesPanel);
			add(container);
			WestFocusPanel wfp = (WestFocusPanel) linkContainer.getWidget(0);
			wfp.showPage();
		}
	}

	private Widget getLinksPanel(Mod3902018 m390, Model390Callback cbk) {
		IMod3902018CallBack callback = new IMod3902018CallBack() {

			@Override
			public void calculateAndRefresh() {
				m390.calculate();
				((WestFocusPanel) linkContainer.getWidget(3)).refresh(m390);
				((WestFocusPanel) linkContainer.getWidget(4)).refresh(m390);
				((WestFocusPanel) linkContainer.getWidget(5)).refresh(m390);
				((WestFocusPanel) linkContainer.getWidget(6)).refresh(m390);
				((WestFocusPanel) linkContainer.getWidget(8)).refresh(m390);
			}

			@Override
			public Mod3902018 getMod390() {
				return m390;
			}
			
			@Override
			public String getDomainName() {
				return getCurrentDomainName();
			}
			
			@Override
			public Integer getDomainId() {
				return getCurrentDomain();
			}
			
			@Override
			public String getUser() {
				return getCurrentUser();
			}

			@Override
			public void showVisorAEAT() {
				cbk.showVisorAEAT();
			}

			@Override
			public void showInfoPanel(String str) {
				cbk.showBreakdownPanel(str);
			}
			
		};
		ScrollPanel container = new ScrollPanel();
		container.addStyleName(AON.AON_CSS.aonLinkPanel());
		linkContainer = new FlowPanel();
		linkContainer.add(new WestFocusPanel(AON.MSG.pasiveSubjectAndAccrual(), new Page00(m390), callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.stadisticalData(), new Page01(m390),callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.representativeData(), new Page02(m390),callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.generalRegimeOperations(), new Page03(m390),callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.simplifiedRegimeOperations(), new Page04(m390),callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.annualLiquidationResult(), new Page05(m390),callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.taxByTerritory(), new Page06(m390),callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.liquidationsResult(), new Page07(m390),callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.operationsVolume(), new Page08(m390),callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.specificOperations(), new Page09(m390),callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.prorrata(), new Page10(m390),callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.difActivitiesRegime(), new Page11(m390),callback));
		linkContainer.add(new WestFocusPanel("Agencia Tributaria", new Page12(m390, aonData, callback), callback));
		container.add(linkContainer);
		return container;
	}

	// -------------------------------------------------------------- UiHandler

	protected void submitForm(String action, Integer id) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		mod390Hidden.setValue(String.valueOf(id));
		domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
		domainNameHidden.setValue(getCurrentDomainName());
		userHidden.setValue(getCurrentUser());
		diskForm.submit();
	}

	private SimplePanel getHeaderPanel(Mod3902018 m390, Model390Callback cbk) {
		SimplePanel headerPanel	= new SimplePanel(); 
		headerPanel.setStyleName(AON.AON_CSS.aonWidthAll());
		FlexTable headerTable = new FlexTable();
		headerTable.setStyleName(AON.AON_CSS.aonFiscalModelTable());
		
		Label image = new Label("");
		image.setStyleName(AON.AON_CSS.aonAeatHeaderImage());
		
		headerTable.setWidget(0, 0, image);
		headerTable.getFlexCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderImage());
		headerTable.getFlexCellFormatter().setRowSpan(0, 0, 2);
		
		headerTable.setWidget(0, 1, new Label(AON.MSG.fiscalModelDescriptionlong(FiscalModelType.M390)));
		headerTable.getFlexCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		headerTable.getFlexCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonFiscalAeatBg());
		headerTable.getFlexCellFormatter().setRowSpan(0, 1, 2);
		
		headerTable.setWidget(0, 2, new Label(FiscalModelType.M390.getName() + "*"));
		headerTable.getFlexCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(0, 2, AON.AON_CSS.aonFiscalAeatBg());
		
		headerTable.setWidget(1, 0, new Label(""+m390.getYear()));
		headerTable.getFlexCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(1, 0, AON.AON_CSS.aonFiscalAeatBg());
		headerPanel.setWidget(headerTable);
		return headerPanel;
	}

	private class WestFocusPanel extends FocusPanel {
		
		private IMod3902018Page content;
		
		public WestFocusPanel(String label, final IMod3902018Page content,IMod3902018CallBack callback) {
			super();
			this.content = content;
			setStyleName(AON.AON_CSS.aonLinkItem());
			FlowPanel fp = new FlowPanel();
			fp.setStyleName(AON.AON_CSS.aonLinkListItem());
			InlineLabel lb = new  InlineLabel(label);
			fp.add(lb);
			setWidget(fp);
			
			pagesPanel.add(content);
			content.setCallback(callback);
			
			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showPage();
				}
			});
		}

		public void showPage() {
			for (int i = 0 ; i < linkContainer.getWidgetCount(); i ++) {
				linkContainer.getWidget(i).removeStyleName(AON.AON_CSS.aonLinkItemSelected());
			}
			addStyleName(AON.AON_CSS.aonLinkItemSelected());
			pagesPanel.showWidget(pagesPanel.getWidgetIndex(content));
		}

		public void refresh(Mod3902018 m390) {
			content.refresh(m390);
		}
//		public void setValue(Mod3902018 m390) {
//			content.setValue(m390);
//		}
		public void populate(Mod3902018 m390) {
			content.populate(m390);
		}
		
	}
	
	private void validate(Mod3902018 m390, Model390Callback cbk) {
		LinkedList<ValidationMessage> msg = new LinkedList<ValidationMessage>();
		if (m390.getYear() != 2018 
			&& m390.getYear() != 2016
			&& m390.getYear() != 2017) msg.add(ValidationMessages.EMPTY_YEAR.getMsg());
		if (!m390.isLegalEntity()) {
			if (AonStringUtils.isEmpty(m390.getDocument())) msg.add(ValidationMessages.EMPTY_DOCUMENT.getMsg());
			if (!AonDocumentUtil.isValid(m390.getDocument())) msg.add(ValidationMessages.WRONG_DOCUMENT.getMsg());
			if (AonStringUtils.isEmpty(m390.getName())) msg.add(ValidationMessages.REQ_NAME.getMsg());
			if (AonStringUtils.isEmpty(m390.getFirstSurname())) msg.add(ValidationMessages.REQ_SURNAME.getMsg());
		} else {
			if (AonStringUtils.isEmpty(m390.getName())) msg.add(ValidationMessages.EMPTY_NAME.getMsg());
		}
		if (m390.getMainActivity() == null || AonStringUtils.isEmpty(m390.getMainActivity().getKey())) 
			msg.add(ValidationMessages.EMPTY_ACTI.getMsg());
		if (!m390.isLegalEntity()) {
			if (m390.getAddress() == null) msg.add(ValidationMessages.EMPTY_REPR.getMsg());
			if (AonStringUtils.isEmpty(m390.getAddress().getRdocument())) msg.add(ValidationMessages.EMPTY_REPR_DOC.getMsg());
			if (!AonDocumentUtil.isValid(m390.getAddress().getRdocument())) msg.add(ValidationMessages.WRONG_REPR_DOC.getMsg());
		} else {
			if (m390.getLegalRepr1() != null &&
				!AonDocumentUtil.isValid(m390.getLegalRepr1().getDocument())) msg.add(ValidationMessages.LG1_WRONG_DOC.getMsg());
			if (m390.getLegalRepr2() != null &&
				!AonDocumentUtil.isValid(m390.getLegalRepr2().getDocument())) msg.add(ValidationMessages.LG2_WRONG_DOC.getMsg());
			if (m390.getLegalRepr3() != null &&
				!AonDocumentUtil.isValid(m390.getLegalRepr3().getDocument())) msg.add(ValidationMessages.LG3_WRONG_DOC.getMsg());
		}
		if (!msg.isEmpty()) {
			LinkedList<Widget> widgets = new LinkedList<Widget>();
			for (ValidationMessage m : msg ) {
				String text = m.getPage()>=0?("P\u00E1g: " + Integer.toString(m.getPage()+1) + " "):"";
				text += AonStringUtils.isEmpty(m.getKey())?"":("Casilla: " + m.getKey() + " ");
				text += m.getMessage();
				InlineLabel label = new InlineLabel(text);
				// label.addStyleName(AON.AON_CSS.aonColorRed());
				label.addStyleName(AON.AON_CSS.aonBold());
				if (m.getPage()>=0) {
					label.addClickHandler( new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							showPage(m.getPage());
						}
					});
				}
				widgets.add(label);
			}
			cbk.showError(widgets);
		}		
	}
	
	protected SimplePanel getDeclarationHeaderTable(Mod3902018 m390, Model390Callback cbk) {
		SimplePanel panel = new SimplePanel();
		panel.addStyleName(AON.AON_CSS.aonScrollArea());
		FlexTable table = new FlexTable();
		table.setStyleName(AON.AON_CSS.aonPanelGrid());
		table.addStyleName(AON.AON_CSS.aonWidthAll());
		table.addStyleName(AON.AON_CSS.aonBlockCenter());
		
		table.getColumnFormatter().setWidth(0, "auto");
		
		table.getColumnFormatter().setWidth(1, "150px");
		table.getColumnFormatter().setWidth(2, "100px");
		table.getColumnFormatter().setWidth(3, "150px");
		table.getColumnFormatter().setWidth(4, "150px");
		table.getColumnFormatter().setWidth(5, "110px");
		
		FlowPanel cell1 = new FlowPanel();
		FlowPanel cell01 = new FlowPanel();
		cell01.setStyleName(AON.AON_CSS.aonFontBig());
		cell01.addStyleName(AON.AON_CSS.aonTextCenter());
		InlineLabel documentLabel = new InlineLabel();
		documentLabel.setText(m390.getDocument());
		cell01.add(documentLabel);
		
		InlineLabel  nameLabel = new InlineLabel();
		nameLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
		if (m390.isLegalEntity()) {
			nameLabel.setText(AonStringUtils.defaultIfBlank(m390.getName(),""));
		} else {
			nameLabel.setText(
					AonStringUtils.defaultIfBlank(m390.getName(),"") 
					+ " " + AonStringUtils.defaultIfBlank(m390.getFirstSurname(),"") 
					+ " " + AonStringUtils.defaultIfBlank(m390.getSecondSurname(),""));
		}
		cell01.add(nameLabel);
		
		InlineLabel surnameLabel = new InlineLabel();
		surnameLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
		surnameLabel.setText(m390.getSurname());
		cell01.add(surnameLabel);
		cell1.add(cell01);
		
		table.setWidget(0, 0, cell1);
		table.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonNowrap());

		InlineLabel replacedLabel = new InlineLabel();
		if (m390.isReplacement()) {
			replacedLabel.setText("Sustit.");
			replacedLabel.setStyleName(AON.AON_CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
		}
		if (m390.isComplementary()) {
			replacedLabel.setText("Complem.");
			replacedLabel.setStyleName(AON.AON_CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
		}
		table.setWidget(0, 1, replacedLabel);
		table.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonTextCenter());
		
		table.setWidget(0, 2, new Label());
		table.getCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 2, AON.AON_CSS.aonTextCenter());
		
		FlowPanel  dirtyPanel = new FlowPanel ();
		table.setWidget(0, 3, dirtyPanel);
		table.getCellFormatter().setStyleName(0, 3, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 3, AON.AON_CSS.aonTextCenter());
		
		InlineLabel statusLabel = new InlineLabel();
		statusLabel.setText(m390.getStatus().getName());
		statusLabel.setStyleName(FiscalModelUtils.getStatusIconStyle(m390.getStatus()));
		statusLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());

		table.setWidget(0, 4, statusLabel);
		table.getCellFormatter().setStyleName(0, 4, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 4, AON.AON_CSS.aonNowrap());
		table.getCellFormatter().addStyleName(0, 4, AON.AON_CSS.aonTextCenter());
		
		FlowPanel commentsPanel = new FlowPanel();
		Button commentsButton = new Button();
		commentsButton.setStyleName(AON.AON_CSS.aonIconCommandButton());
		commentsButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final AonToast toast = new AonToast();
				FlowPanel commentPanel = new FlowPanel();
				commentPanel.setStyleName( FiscalModelUtils.getAdministrationBG(m390.getAdministration()) );
				commentPanel.setStyleName(AON.AON_CSS.aonHeightAll());
				commentPanel.addStyleName(AON.AON_CSS.aonTextCenter());
				TextArea comment = new TextArea();
				comment.addValueChangeHandler(new ValueChangeHandler<String>() {
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						m390.setComments(event.getValue());
						styleCommentsButton(m390,commentsButton);
						Model390.MOD390_SERVICE.saveComments(getCurrentDomainName(),getCurrentUser(), m390, new AsyncCallback<Mod390>() {
							@Override
							public void onSuccess(Mod390 result) {
								toast.hide();
							}

							@Override
							public void onFailure(Throwable caught) {
								toast.hide();
								cbk.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
							}
						});
						
						
						
					}
				});
				comment.setText(m390.getComments());
				comment.setWidth("90%");
				comment.setHeight("5em");
				commentPanel.add(comment);
				toast.show(AON.MSG.comments(), commentPanel);
			}
		});
		commentsPanel.add(commentsButton);
		commentsPanel.add(new InlineLabel(AON.MSG.comments()));
		table.setWidget(0, 5, commentsPanel);
		styleCommentsButton(m390,commentsButton);
		table.getCellFormatter().setStyleName(0, 5, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 5, AON.AON_CSS.aonTextCenter());

		panel.setWidget(table);
		return panel;
	}

	private void styleCommentsButton(Mod3902018 m390, Button commentsButton) {
		if (AonStringUtils.isEmpty(m390.getComments())) {
			commentsButton.addStyleName(AON.AON_CSS.aonIconComment());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconCommentRed());
		} else {
			commentsButton.addStyleName(AON.AON_CSS.aonIconCommentRed());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconComment());
		}
		commentsButton.setTitle(m390.getComments());
	}
}


package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.EnumMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.AuditDialog;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox.ExpressionResolver;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303IdentificationData.IModel303IdentificationDataCallback;
import com.esferalia.aon.gwt.fiscal.client.model.FinishDeclarationPopup;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public abstract class Model303Base extends DockLayoutPanel  {

	protected static final boolean ENABLED = true;
	protected static final boolean DISABLED = false;
	protected static final boolean HAS_INFO = true;
	protected static final boolean HAS_NOT_INFO = false;
	
	private static final int MAX_LABEL_LENGTH = 100;
	
	private Mod303 mod303;
	private Model303Callback callback;
	private EnumMap<Mod303Key,DoubleBox> fieldsMap;
	private boolean dirty;
	
	protected InlineLabel documentLabel = new InlineLabel();
	protected InlineLabel nameLabel = new InlineLabel();
	protected InlineLabel surnameLabel = new InlineLabel();
	protected FlowPanel paymentInfo = new FlowPanel();
	protected InlineLabel dirtyLabel = new InlineLabel();
	protected InlineLabel statusLabel = new InlineLabel();
	protected Button commentsButton = new Button();
	
	final Button newButton = new Button();
	final Button saveButton = new Button();
	final Button cancelButton = new Button();		
	final Button deleteButton = new Button();		
	final Button reopenButton = new Button();
	final Button finalizeButton = new Button();
	final Button auditButton = new Button();
	
	private ExpressionResolver resolver = new ExpressionResolver() {
		@Override
		public void resolve(String expression, AsyncCallback<Double> callback) {
			Model303.fiscalService.mathExpression(expression,callback);
		}
	};
	
	interface TabLabelTemplate extends SafeHtmlTemplates {
		@Template("<span class=\"{1} aon-padding-right aon-padding-left-20\" style=\"width: auto !important\">{0}</span>")
		SafeHtml render(String label, String iconStyle);
	}
	protected static final TabLabelTemplate TAB_TEMPLATE = GWT.create(TabLabelTemplate.class);

	
	public Model303Base(Mod303 mod303,Model303Callback cbk) {
		super(Unit.PX);
		select( mod303 );
		this.callback = cbk;
		
		setStyleName(AON.AON_CSS.aonSelector());
		addNorth(getToolbarPanel(), 25);
		
		SimplePanel headerPanel = new SimplePanel();
		FiscalModelUtils.paintHeaderTable(headerPanel, this.mod303 );
		addNorth(headerPanel, 65);
		
		SimplePanel declarationHeaderPanel = new SimplePanel();
		paintDeclarationHeaderTable(declarationHeaderPanel);
		addNorth(declarationHeaderPanel , 40);

		fieldsMap = new EnumMap<>(Mod303Key.class);
	}
	protected Mod303 getMod303() {
		return mod303;
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
		toolbar.setWidget(0, 0, new Label( "IVA. Autoliquidaci\u00F3n."));
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		
		newButton.setText(AON.MSG.newAction());
		newButton.setTitle(newButton.getText());
		newButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		newButton.addStyleName(AON.AON_CSS.aonIconReset());
		newButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				callback.onNew();
			}
		});
		buttonContainer.add(newButton);
		
		
		saveButton.setText(AON.MSG.saveAction());
		saveButton.setTitle(newButton.getText());
		saveButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		saveButton.addStyleName(AON.AON_CSS.aonIconSave());
		saveButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				save();
			}
		});
		buttonContainer.add(saveButton);
		
		cancelButton.setText(AON.MSG.cancelAction());
		cancelButton.setTitle(cancelButton.getText());
		cancelButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		cancelButton.addStyleName(AON.AON_CSS.aonIconCancel());
		cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cancelButton.setEnabled(false);
				if (isDirty()) {
					ConfirmDialog cd = new ConfirmDialog();
					cd.confirm(AON.MSG.confirmDeclarationCancelAction(), new ConfirmDialogCallback() {

						@Override
						public void onAccept() {
							callback.onCancel();
						}

						@Override
						public void onCancel() {
							cancelButton.setEnabled(true);
						}
					});
				} else {
					callback.onCancel();
				}
			}
		});
		buttonContainer.add(cancelButton);

		deleteButton.setText(AON.MSG.deleteAction());
		deleteButton.setTitle(deleteButton.getText());
		deleteButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		deleteButton.addStyleName(AON.AON_CSS.aonIconDelete());
		deleteButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				delete();
			}
		});
		buttonContainer.add(deleteButton);
		
		finalizeButton.setText(AON.MSG.finish());
		finalizeButton.setTitle(finalizeButton.getText());
		finalizeButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		finalizeButton.addStyleName(AON.AON_CSS.aonIconLock());
		finalizeButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				onFinalize();
			}
		});
		buttonContainer.add(finalizeButton);

		reopenButton.setText(AON.MSG.reopen());
		reopenButton.setTitle(reopenButton.getText());
		reopenButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		reopenButton.addStyleName(AON.AON_CSS.aonIconUnlock());
		reopenButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				reopenDeclaration();
			}
		});
		buttonContainer.add(reopenButton);
		
		auditButton.setText(AON.MSG.audit());
		auditButton.setTitle(auditButton.getText());
		auditButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		auditButton.addStyleName(AON.AON_CSS.aonIconAudit());
		auditButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				audit();
			}
		});
		buttonContainer.add(auditButton);
		
/*
		<g:Button ui:field="printButton" text="&nbsp;" 
			styleName="{aonResources.css.aonFindingToolbarItem} {aonResources.css.aonIconExcel}" />
		<g:Button ui:field="printPDFButton" text="{msg.draft}"
			styleName="{aonResources.css.aonFindingToolbarItem} {aonResources.css.aonIconPdfPreview}" />
 */

		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}
	private void select( Mod303 mod303) {
		this.mod303 = mod303;
		refreshToolbarState();	
	}
	
	private void refreshToolbarState() {
		deleteButton.setVisible(!mod303.isNew());
		auditButton.setVisible(!mod303.isNew());
		newButton.setVisible(!mod303.isNew());
		cancelButton.setVisible(true);
		saveButton.setVisible(true);
		saveButton.setEnabled(!isSnapshot() && !mod303.isFinished());
		deleteButton.setEnabled(!mod303.isFinished());
		
		reopenButton.setVisible(!isSnapshot() && !mod303.isNew() && mod303.isFinished());
		finalizeButton.setVisible(!isSnapshot() && !mod303.isNew() && !mod303.isFinished());
	}

	protected void paintDeclarationHeaderTable(SimplePanel panel) {
		FlexTable table = new FlexTable();
		table.setStyleName(AON.AON_CSS.aonPanelGrid());
		table.addStyleName(AON.AON_CSS.aonWidth90Percent());
		table.addStyleName(AON.AON_CSS.aonBlockCenter());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().setWidth(1, "150px");
		table.getColumnFormatter().setWidth(2, "150px");
		table.getColumnFormatter().setWidth(3, "110px");
		
		FlowPanel cell1 = new FlowPanel();
		FlowPanel cell01 = new FlowPanel();
		cell01.setStyleName(AON.AON_CSS.aonFontBig());
		cell01.addStyleName(AON.AON_CSS.aonTextCenter());
		documentLabel.setText(this.mod303.getDocument());
		cell01.add(documentLabel);
		nameLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
		nameLabel.setText(this.mod303.getName());
		cell01.add(nameLabel);
		surnameLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
		surnameLabel.setText(this.mod303.getSurname());
		cell01.add(surnameLabel);
		cell1.add(cell01);
		
		paymentInfo.setStyleName(AON.AON_CSS.aonFontSmall());
		paymentInfo.addStyleName(AON.AON_CSS.aonTextRight());
		cell1.add(paymentInfo);
		
		table.setWidget(0, 0, cell1);
		table.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonNowrap());

		styleDirtyLabel();
		dirtyLabel.setStyleName(AON.AON_CSS.aonColorRed());
		table.setWidget(0, 1, dirtyLabel);
		table.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonPanelGridEven());
		
		styleStatusLabel(this.mod303);
		table.setWidget(0, 2, statusLabel);
		table.getCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 2, AON.AON_CSS.aonNowrap());
		table.getCellFormatter().addStyleName(0, 2, AON.AON_CSS.aonTextCenter());
		
		FlowPanel commentsPanel = new FlowPanel();
		commentsButton.setStyleName(AON.AON_CSS.aonIconCommandButton());
		commentsButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final AonToast toast = new AonToast();
				FlowPanel commentPanel = new FlowPanel();
				commentPanel.setStyleName( FiscalModelUtils.getAdministrationBG(mod303.getAdministration()) );
				commentPanel.setStyleName(AON.AON_CSS.aonHeightAll());
				commentPanel.addStyleName(AON.AON_CSS.aonTextCenter());
				TextArea comment = new TextArea();
				comment.addValueChangeHandler(new ValueChangeHandler<String>() {
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						mod303.setComments(event.getValue());
						styleCommentsButton();
						Model303.mod303Service.saveCommentsMod303(Model303.getCurrentDomainName(), mod303, new AsyncCallback<Mod303>() {
							@Override
							public void onSuccess(Mod303 result) {
								toast.hide();
							}

							@Override
							public void onFailure(Throwable caught) {
								toast.hide();
								callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
							}
						});
						
						
						
					}
				});
				comment.setText(mod303.getComments());
				comment.setWidth("90%");
				comment.setHeight("5em");
				commentPanel.add(comment);
				toast.show(AON.MSG.comments(), commentPanel);
			}
		});
		commentsPanel.add(commentsButton);
		commentsPanel.add(new InlineLabel(AON.MSG.comments()));
		table.setWidget(0, 3, commentsPanel);
		styleCommentsButton();
		table.getCellFormatter().setStyleName(0, 3, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 3, AON.AON_CSS.aonTextCenter());

		panel.setWidget(table);
	}
	private void styleStatusLabel(Mod303 mod) {
		statusLabel.setText(mod.getStatus().getName());
		statusLabel.setStyleName(mod.getStatus() == FiscalStatus.FINISHED
			?AON.AON_CSS.aonIconLock()
			:AON.AON_CSS.aonIconUnlock()
		);
		statusLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
	}
	private void styleCommentsButton() {
		if (AonStringUtils.isEmpty(mod303.getComments())) {
			commentsButton.addStyleName(AON.AON_CSS.aonIconComment());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconCommentRed());
		} else {
			commentsButton.addStyleName(AON.AON_CSS.aonIconCommentRed());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconComment());
		}
		commentsButton.setTitle(mod303.getComments());
	}
	
	
	protected EnumMap<Mod303Key, DoubleBox> getFieldsMap() {
		return fieldsMap;
	}
	protected void paintAdditionalData(FlexTable table) {
		
	}
	protected void paintDeclaration(FlexTable table, IModelScript<Mod303Key>[] script, int colsNumber) {
		if (table.getRowCount() > 0) {
			table.removeAllRows();
		}
		for (IModelScript<Mod303Key> ms : script) {
			paintRow(table,ms,colsNumber);	
		}
	}
	
	protected void paintEmptyRow(FlexTable table) {
		int row = table.getRowCount();
		table.setWidget(row, 0, new Label());
	}
	
	protected int paintEmptyCol(FlexTable table,int row, int col, int colspan) {
		table.setWidget(row, col, new Label());
		table.getFlexCellFormatter().addStyleName(row, col,AON.AON_CSS.aonBorderBottomImportant() );
		table.getFlexCellFormatter().setColSpan(row, col, colspan);
		return ++col;
	}

	protected void paintRow(FlexTable table,IModelScript<Mod303Key> script, int colsNumber) {
		if (script.hasGraphicParticularity()) {
			paintParticularyRow(table,script);		
		} else {
			int row = table.getRowCount();
			paintLabel(table,row,script);
			if (script.getKeys() == null) {
				table.getFlexCellFormatter().setColSpan(row, 0, colsNumber);	
			} else {
				table.getFlexCellFormatter().setColSpan(row, 0, (colsNumber -  ( ( script.getKeys().length * 2) + 1) ) );
				int col = 1;
				for (Mod303Key key : script.getKeys()) {
					if (key == null) {
						col = paintEmptyCol(table,row, col, 2);
					} else {
						col = paintBox(table, row, col, key );
						col = paintField(table, row, col, script, key );
					}
				}
				paintInfoCol(table,row,col,script);	
			}
		}
	}
	
	protected void paintParticularyRow(FlexTable table, IModelScript<Mod303Key> script) {
		
	}
	
	protected void paintLabel(FlexTable table,int row,IModelScript<Mod303Key> script) {
		String labelText = script.getLabel();
		Label label = new Label();
		if (AonStringUtils.length(labelText) > MAX_LABEL_LENGTH) {
			label.setTitle(labelText);	
			labelText = AonStringUtils.abbreviate(labelText, MAX_LABEL_LENGTH);
		}
		label.setText(labelText);
		table.setWidget(row, 0, label);
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		if (script.isTitle() ) {
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBold() );
		} else {
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft20() );
		}
	}
	
	private int paintBox(FlexTable table, int row, int col, Mod303Key key) {
		if (key.getBox() > 0.0 ) {
			table.setWidget(row, col, new BoxLabel(key.getBox()));
		} else {
			table.setWidget(row, col, new Label());
		}
		return ++col;
	}

	private int paintField(FlexTable table, int row, int col, IModelScript<Mod303Key> script, final Mod303Key key) {
		final FiscalModelDetail det1 = this.mod303.ensureDetail(key);
		final DoubleBox input = new DoubleBox( script.getFieldSize(key));
		input.setResolver(resolver);
		fieldsMap.put(key, input);
		input.setEnabled(this.mod303.isNotFinished() && (script.isEnabled( key ) )); 
		input.setValue(det1.getAmount());
		input.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				double result = mod303.getResultAmount(key);
				double adjust = mod303.getAdjustAmount(key);
				double amount = input.getValue();
				if (AonMathUtils.isNotZero(result - adjust - amount)) {
					mod303.ensureDetail(key).setAdjustAmount( result - amount);	
				}
				mod303.ensureDetail(key).setAmount(input.getValue());
				if (input.isEnabled()) {
					calculateAndRefresh();
				}
				markAsDirty();
			}
		});
		table.setWidget(row, col, input);
		return ++col;
	}
	
	private void paintInfoCol(FlexTable table, int row, int col, final IModelScript<Mod303Key> script) {
		FlowPanel buttonContainer = new FlowPanel();
		for (final FiscalModelKeyInfo infoKey : script.getInfoKeys()) {
			buttonContainer.setStyleName(AON.AON_CSS.aonNowrap());
			if 	(infoKey != FiscalModelKeyInfo.NONE) {
				final Button button = new Button("");
				button.setTitle(infoKey.getLabel());
				button.setStyleName(AON.AON_CSS.aonIconCommandButton());
				
				if 	(infoKey == FiscalModelKeyInfo.INVOICE) button.addStyleName(AON.AON_CSS.aonIconInvoice());
				if 	(infoKey == FiscalModelKeyInfo.DIFF_INVOICE) button.addStyleName(AON.AON_CSS.aonIconDiff());
				if 	(infoKey == FiscalModelKeyInfo.COMPUTE) button.addStyleName(AON.AON_CSS.aonIconCalculator());
				if 	(infoKey == FiscalModelKeyInfo.COMPUTE_KEY) button.addStyleName(AON.AON_CSS.aonIconCompanyData());
				
				button.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						Model303.mod303Service.getInfo(Model303.getCurrentDomainName(),Model303.getCurrentDomain(),
								mod303,script, infoKey,new AsyncCallback<String>() {

									@Override
									public void onFailure(Throwable caught) {
										callback.showError(AON.MSG.errorMessage());
									}

									@Override
									public void onSuccess(String result) {
										callback.showBreakdownPanel(result);
									}
							
								}
							);	
					}
				});
				buttonContainer.add(button);
			}
			table.setWidget(row, col, buttonContainer);
		}
	}

	public void calculateAndRefresh() {
		Model303.mod303Service.calculateMod303(Model303.getCurrentDomainName(),this.mod303,
				new AsyncCallback<Mod303>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.showError(AON.MSG.errorMessage());
					}

					@Override
					public void onSuccess(Mod303 result) {
						for (Mod303Key key : fieldsMap.keySet()) {
							double d1 = result.getAmount(key);
							double d2 = fieldsMap.get(key).getValue();
							if (!AonNumberUtils.equals(d1, d2)) {
								fieldsMap.get(key).setValue(d1,true,true);

							}
						}
					}
			
				}
			);	
	}
	
	private void identificationLabelChanged() {
		documentLabel.setText(mod303.getDocument());
		nameLabel.setText(mod303.getName());
		surnameLabel.setText(mod303.getSurname());
	}

	private void markAsDirty() {
		if (!isDirty()) {
			setDirty(true);
		}
	}
	private boolean isDirty() {
		return this.dirty;
	}
	private void setDirty(boolean dirty) {
		this.dirty = dirty;
		styleDirtyLabel();
	}

	protected void styleDirtyLabel() {
		dirtyLabel.setText(isDirty()?"[CAMBIOS]":"");
	}

	private void save() {
		saveButton.setEnabled(false);
		callback.cleanErrorPanel();
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model303.mod303Service.saveMod303(Model303.getCurrentDomainName(), this.mod303, new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 result) {
						select( result );
						popup.hide();
						saveButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						saveButton.setEnabled(true);
					}
				});
	}
	
	private void delete() {
		deleteButton.setEnabled(false);
		ConfirmDialog cd = new ConfirmDialog();
		cd.confirm(AON.MSG.confirmDeclarationDeleteAction(), new ConfirmDialogCallback() {

			@Override
			public void onAccept() {
				Model303.mod303Service.deleteMod303(Model303.getCurrentDomainName(),mod303, new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						deleteButton.setEnabled(true);
						callback.onCancel();
					}

					@Override
					public void onFailure(Throwable caught) {
						deleteButton.setEnabled(true);
						callback.showError(AON.MSG.unableToDeleteDeclaration(caught.getMessage()));
					}
				});
			}

			@Override
			public void onCancel() {
				deleteButton.setEnabled(true);
			}
		});
	}
	
	private void onFinalize() {
		finalizeButton.setEnabled(false);
		callback.cleanErrorPanel();
		Model303.mod303Service.initializeForFinishMod303(Model303.getCurrentDomainName(),mod303,
				new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 m303) {
						select( m303 );
						showFinalizePopup();
						finalizeButton.setEnabled(true);
					}
	
					@Override
					public void onFailure(Throwable caught) {
						callback.showError(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
						finalizeButton.setEnabled(true);
					}
				});
	}
	
	private void showFinalizePopup() {
		FinishDeclarationPopup<Mod303> finalizeDialog = new FinishDeclarationPopup<Mod303>( 
				new IFiscalModelCallback<Mod303>() {
			@Override
			public void onAccept() {
				finish();
			}
			@Override
			public void onCancel() {
				
			}
			@Override
			public String getDomainName() {
				return Model303.getCurrentDomainName();
			}
			@Override
			public int getDomain() {
				return Model303.getCurrentDomain();
			}
			@Override
			public Mod303 getFiscalModel() {
				return mod303;
			}
			@Override
			public boolean isFinished() {
				return mod303.isFinished();
			}
			@Override
			public boolean isDirty() {
				return isDirty();
			}
			@Override
			public void markAsDirty() {
				Model303Base.this.markAsDirty();
				
			}
			@Override
			public void identificationLabelChanged() {
				Model303Base.this.identificationLabelChanged();
			}
			
			@Override
			public void showErrorMsg(String msg) {
				callback.showError(msg);
			}
			@Override
			public void showInfoPanel(String text) {
				callback.showBreakdownPanel(text);
			}
		});
		finalizeDialog.center();
		finalizeDialog.show();
	}
	
	private void finish() {
		finalizeButton.setEnabled(false);
		callback.cleanErrorPanel();
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model303.mod303Service.finishMod303(Model303.getCurrentDomainName(), mod303, new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 result) {
						select( result );
						popup.hide();
						finalizeButton.setEnabled(true);
						FiscalModelUtils.paintPaymentInfo(paymentInfo,mod303);
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						finalizeButton.setEnabled(true);
					}
				});
	}
	private void reopenDeclaration() {
		reopenButton.setEnabled(false);
		callback.cleanErrorPanel();
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model303.mod303Service.reopenMod303(Model303.getCurrentDomainName(), mod303, new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 result) {
						select( result );
						popup.hide();
						reopenButton.setEnabled(true);
						FiscalModelUtils.paintPaymentInfo(paymentInfo,mod303);
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						callback.showError(AON.MSG.unableToReopenDeclaration(caught.getMessage()));
						reopenButton.setEnabled(true);
					}
				});
	}
	
	private void audit() {
		AuditDialog dialog = new AuditDialog();
		dialog.show(mod303);
	}

	protected class Model303IdentificationDataCallback implements IModel303IdentificationDataCallback {

		@Override public boolean isFinished() 		{ return getMod303().isFinished(); 			}
		
		@Override public String getDocument() 		{return getMod303().getDocument();			}
		@Override public String getName() 			{return getMod303().getName(); 				}
		@Override public String getSurname() 		{return getMod303().getSurname();			}
		@Override public String getPhone() 			{return getMod303().getPhone();				}
		@Override public String getStreetInitial() 	{return getMod303().getStreetInitial();		}
		@Override public String getStreetName() 	{return getMod303().getStreetName();		}
		@Override public String getStreetNumber() 	{return getMod303().getStreetNumber();		}
		@Override public String getStreetStair() 	{return getMod303().getStreetStair();		}
		@Override public String getStreetFloor() 	{return getMod303().getStreetFloor();		}
		@Override public String getStreetDoor() 	{return getMod303().getStreetDoor();		}
		@Override public String getTown() 			{return getMod303().getTown();				}
		@Override public String getProvince() 		{return getMod303().getProvince();			}
		@Override public String getZip() 			{return getMod303().getZip();				}
		@Override public String getContactPerson() 	{return getMod303().getContactPerson();		}
		@Override public String getContactPhone() 	{return getMod303().getContactPhone();		}
		@Override public String getContactCellular(){return getMod303().getContactCellular();	}
		@Override public String getContactEmail() 	{return getMod303().getContactEmail();		}
	
		@Override public void documentChanged(String value) {
			getMod303().setDocument(value);
			identificationLabelChanged();
			markAsDirty();			
		}
		@Override public void nameChanged(String value) {
			getMod303().setName(value);
			identificationLabelChanged();
			markAsDirty();			
		}
		@Override public void surnameChanged(String value) {
			getMod303().setSurname(value);
			identificationLabelChanged();
			markAsDirty();			
		}
		@Override public void phoneChanged(String value) {
			getMod303().setPhone(value);
			markAsDirty();			
		}
		@Override public void streetInitialChanged(String value) {
			getMod303().setStreetInitial(value);
			markAsDirty();			
		}
		@Override public void streetNameChanged(String value) {
			getMod303().setStreetName(value);
			markAsDirty();			
		}
		@Override public void streetNumberChanged(String value) {
			getMod303().setStreetNumber(value);
			markAsDirty();			
		}
		@Override public void streetStairChanged(String value) {
			getMod303().setStreetStair(value);
			markAsDirty();			
		}
		@Override public void streetFloorChanged(String value) {
			getMod303().setStreetFloor(value);
			markAsDirty();			
		}
		@Override public void streetDoorChanged(String value) {
			getMod303().setStreetDoor(value);
			markAsDirty();			
		}
		@Override public void townChanged(String value) {
			getMod303().setTown(value);
			markAsDirty();			
		}
		@Override public void provinceChanged(String value) {
			getMod303().setProvince(value);
			markAsDirty();			
		}
		@Override public void zipChanged(String value) {
			getMod303().setZip(value);
			markAsDirty();			
		}
		@Override public void contactPersonChanged(String value) {
			getMod303().setContactPerson(value);
			markAsDirty();			
		}
		@Override public void contactPhoneChanged(String value) {
			getMod303().setContactPhone(value);
			markAsDirty();			
		}
		@Override public void contactCellularChanged(String value) {
			getMod303().setContactCellular(value);
			markAsDirty();			
		}
		@Override public void contactMailChanged(String value) {
			getMod303().setContactEmail(value);
			markAsDirty();			
		}
	}
	
	protected FlowPanel getInformationPanel(Model303Callback callback) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.addStyleName(AON.AON_CSS.aonWidthAll());
		panel.addStyleName(AON.AON_CSS.aonMarginTop());
		panel.addStyleName(AON.AON_CSS.aonPaddingTop());
		panel.addStyleName(AON.AON_CSS.aonPaddingLeft());
		 
		FlexTable tab = new FlexTable();
		tab.getColumnFormatter().setWidth(0, "30px");
		tab.getColumnFormatter().setWidth(1
				, "auto");
		tab.setStyleName(AON.AON_CSS.aonWidth90Percent());
		tab.addStyleName(AON.AON_CSS.aonBlockCenter());
		tab.addStyleName(AON.AON_CSS.aonPanelGrid());
		Label title = new Label("Informaci\u00F3n \u00FAtil para la confecci\u00F3n del modelo");
		tab.getFlexCellFormatter().setColSpan(0, 0, 2);
		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonMarginTop());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(mod303.getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;
		for (Pair<String, String> pair : getInformationLinks()) {
			Label icon = new Label();
			icon.addStyleName(FiscalModelUtils.getAdministrationIcon(mod303.getAdministration()));
			tab.setWidget(row, 0, icon );
			tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());

			FlowPanel p = new FlowPanel();
			p.setStyleName(AON.AON_CSS.aonPadding2());
			Anchor a = new Anchor(pair.getLeft(),pair.getRight(), "_blank");
			a.setStyleName(AON.AON_CSS.aonPaddingLeft());
			p.add(a);
			tab.setWidget(row, 1, p );
			tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
			row++;
		}
		panel.add(tab);
		return panel;
	}

	protected abstract LinkedList<Pair<String, String>> getInformationLinks();
	
	// *********** [BORRAR]
	public boolean isSnapshot() {
		return true;
	}
}

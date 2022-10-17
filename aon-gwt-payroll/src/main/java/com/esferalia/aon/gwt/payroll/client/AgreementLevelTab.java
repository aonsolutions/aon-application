package com.esferalia.aon.gwt.payroll.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.Level;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AgreementLevelTab extends ResizeComposite {
	
	// ------------------------------------------ UiBinder 

	private static AgreementSalaryTableTabUiBinder uiBinder = GWT.create(AgreementSalaryTableTabUiBinder.class);

	interface AgreementSalaryTableTabUiBinder extends UiBinder<Widget, AgreementLevelTab> {}

	// ------------------------------------------ UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String dialogGlass();
		String dialogZIndex();
		String gridTitle();
		String headerColor();
		String headerFSize();
		String headerSticky();
		String modify();
		String oddRow();
		String textCenter();
	}
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField(provided = true)
	AonToolbar toolbar;
	
	@UiField
	ScrollPanel levelScrollPanel;
	
	@UiField
	Grid levelGrid;
	
	@UiField
	HTMLPanel agreementLevelMessage;
	
	// ------------------------------------------ Variables
	
	private AgreementInfo agreement;
	private boolean hasChange = false;
	private AonToolbarSmallButton saveBtn;
	
	// ------------------------------------------ Constructor

	protected AgreementLevelTab() {
		createToolbar();
		initWidget(uiBinder.createAndBindUi(this));
	}

	// ------------------------------------------ setAgreementPreview
	
	public void setAgreementLevel(AgreementInfo agreementIn) {
		agreement = agreementIn;
		hideMessage();
		showCategoryTable();
		toolbar.setTitle(agreement.getDescription());
		createCategoryTable();
	}

	// ------------------------------------------ categoryTable

	private void createCategoryTable() {
		if(agreement.getActiveLevels().size() <= 1)
			showLevelPanel();
		else {
			showCategoryTable();
			getCategoryTableHeader();
			fillCategoryTable();
			categoryTableWidth();
		}
	}

	private void getCategoryTableHeader() {
		levelGrid.clear();
		levelGrid.resize(0, 3);
		int row = levelGrid.insertRow(levelGrid.getRowCount());
		
		Label level = new Label("Nivel");
		level.addStyleName(style.gridTitle());
		level.addStyleName(style.textCenter());
		level.addStyleName(style.headerFSize());
		levelGrid.setWidget(row, 0, level);
		
		Label category = new Label("Categoria");
		category.addStyleName(style.gridTitle());
		category.addStyleName(style.headerFSize());
		levelGrid.setWidget(row, 1, category);
		
		Label delete = new Label("");
		levelGrid.setWidget(row, 2, delete);
		
		levelGrid.getRowFormatter().addStyleName(row, style.headerSticky());
		levelGrid.getRowFormatter().addStyleName(row, style.headerColor());
	}

	private void fillCategoryTable() {
		for(Entry<Integer, Set<String>> e : agreement.getCategoriesMap().entrySet()) {
			
			Level level = agreement.getLevelById(e.getKey());
			if(level.isDeleted() || level.getId() == 0) continue;
			
			int row = levelGrid.insertRow(levelGrid.getRowCount());
			
			Set<String> categories = e.getValue();
			StringBuilder categoriesBuilder = new StringBuilder();
			for(String category : categories){
				if(AonStringUtils.isBlank(categoriesBuilder.toString()))
					categoriesBuilder.append(category);
				else
					categoriesBuilder.append(", " + category);
			}
			
			TextBox levelCell = new TextBox();
			levelCell.setValue(level.getDescription());
			levelCell.addStyleName(style.gridTitle());
			levelCell.addStyleName(style.textCenter());
			levelCell.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
			if(row % 2 == 0 ) levelCell.addStyleName(style.oddRow());
			
			if(level != null && level.isModify())
				levelCell.addStyleName(style.modify());
			else
				levelCell.removeStyleName(style.modify());
			
			levelCell.addValueChangeHandler(ev -> {
				if(AonStringUtils.isBlank(ev.getValue()) || agreement.existLevel(ev.getValue())) {
					showWarning("Nivel existente", "La descripci\u00f3n no puede ser vacia o coincidir con la de otro nivel ya existente");
					levelCell.setValue(level.getDescription());
				} else {
					level.setDescription(ev.getValue());
					level.setModify(true);
					setAgreementLevel(agreement);
					setHasChange(true);
				}
			});
			
			TextBox categoryCell = new TextBox();
			categoryCell.setWidth("99%");
			categoryCell.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
			categoryCell.setValue(categoriesBuilder.toString());
			categoryCell.setTitle("Categorias nivel " + level.getDescription());
			if(row % 2 == 0 ) categoryCell.addStyleName(style.oddRow());
			
			if(level != null && level.isCatModify())
				categoryCell.addStyleName(style.modify());
			else
				categoryCell.removeStyleName(style.modify());
			
			categoryCell.addValueChangeHandler(categoryValue -> {
				if(AonStringUtils.isNotBlank(categoryValue.getValue())) {
					agreement.getCategoriesMap().remove(level.getId());
					String[] categorySplit = AonStringUtils.split(categoryValue.getValue(), ',');
					for(int i = 0; i < categorySplit.length; i++)
						agreement.addCategory(level.getId(), categorySplit[i].trim());
					
					level.setCatModify(true);
					setAgreementLevel(agreement);
					setHasChange(true);
				}
				
			});
			
			AonToolbarSmallButton deleteBtn = new AonToolbarSmallButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
			deleteBtn.addClickHandler(event -> {
				AonDialog deleteDialog = new AonDialog("Borrar nivel", new HTMLPanel("\u00bfDesea realmente eliminar el nivel <b>" + level.getDescription() +"</b>\u003f"));
				deleteDialog.setGlassStyleName(style.dialogGlass());
				deleteDialog.addStyleName(style.dialogZIndex());
				deleteDialog.confirm(new AonAcceptDialogCallback() {
					
					@Override
					public void onCancel() {
						// Not use here
					}
					
					@Override
					public void onAccept() {
						agreement.deleteLevel(level.getId());
						setAgreementLevel(agreement);
						setHasChange(true);
					}
				});
			});
			
			levelGrid.setWidget(row, 0, levelCell);
			levelGrid.setWidget(row, 1, categoryCell);
			levelGrid.setWidget(row, 2, deleteBtn);
			
			if(row % 2 == 0 ) levelGrid.getCellFormatter().addStyleName(row, 0, style.oddRow());
			if(row % 2 == 0 ) levelGrid.getCellFormatter().addStyleName(row, 1, style.oddRow());
			if(row % 2 == 0 ) levelGrid.getCellFormatter().addStyleName(row, 2, style.oddRow());
		}
	}

	private void categoryTableWidth() {
		levelGrid.setWidth("100%");
		levelGrid.getColumnFormatter().setWidth(0, "120px");
		levelGrid.getColumnFormatter().setWidth(1, "75%");
		levelGrid.getColumnFormatter().setWidth(2, "5%");
	}
	
	// ------------------------------------------ toolbar

	private void createToolbar() {
		toolbar = new AonToolbar("Nivel / Categoria");
		
		saveBtn = new AonToolbarSmallButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
		saveBtn.addClickHandler(e -> {
			showLoading("Guardando convenio " + toolbar.getTitle() + " ...");
			setHasChange(false);
			onSaved();
		});
		
		AonToolbarSmallButton newLevelBtn = new AonToolbarSmallButton(AON.MSG.newAction() + " nivel/categoria", AON.CSS.aonIconAdd());
		newLevelBtn.addClickHandler(e -> {
			HorizontalPanel panel = new HorizontalPanel();
			Label description = new Label("Descripci\u00f3n: ");
			TextBox levelDescription = new TextBox();
			levelDescription.setWidth("100%");
			panel.setWidth("98%");
			panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			panel.add(description);
			panel.add(levelDescription);
			AonDialog dialog = new AonDialog("Nuevo nivel", panel);
			dialog.setGlassStyleName(style.dialogGlass());
			dialog.addStyleName(style.dialogZIndex());
			dialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Not use here
				}
				
				@Override
				public void onAccept() {
					agreement.createLevel(levelDescription.getValue());
					setAgreementLevel(agreement);
					setHasChange(true);
				}
			});
		});
		
		toolbar.add(saveBtn);
		toolbar.add(newLevelBtn);
		
	}
	
	// ------------------------------------------ Dates controler
	
	private void showCategoryTable() {
		agreementLevelMessage.getElement().getStyle().setDisplay(Display.NONE);
	}

	private void showLevelPanel() {
		agreementLevelMessage.getElement().getStyle().clearDisplay();
	}
	
	// ------------------------------------------ HasChange
	
	public boolean hasChange() {
		return hasChange;
	}

	public void setHasChange(boolean hasChange) {
		this.hasChange = hasChange;
		saveBtn.setEnabled(hasChange());
		if(!hasChange()) {
			saveBtn.getElement().getStyle().setDisplay(Display.BLOCK);
			saveBtn.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		}
	}

	// ------------------------------------------ Abstract methods
	
	public abstract void onSaved();
	
	// ------------------------------------------------- Aon Messages panel

	public void showSuccess(String title, String message) {
		Map<String, String> successMap = new HashMap<>();
		successMap.put(title, message);
		AonMessagePanel.showSuccess(messagePanel, successMap);
	}

	public void showError(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(messagePanel, errorMap);
	}
	
	public void showWarning(String title, String message) {
		Map<String, String> warningMap = new HashMap<>();
		warningMap.put(title, message);
		AonMessagePanel.showWarning(messagePanel, warningMap);
	}

	public void showLoading(String message) {
		AonMessagePanel.showLoading(messagePanel, message);
	}

	private void hideMessage() {
		AonMessagePanel.hideMessage(messagePanel);
	}
	
}

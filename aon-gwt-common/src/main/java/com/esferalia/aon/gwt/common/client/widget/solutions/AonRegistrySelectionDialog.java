package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

public abstract class AonRegistrySelectionDialog extends AonCustomDialog {
	
	static class RegistryCell extends AbstractCell<Registry> {

	    @Override
	    public void render(Context context, Registry value, SafeHtmlBuilder sb) {
	      // Value can be null, so do a null check..
	      if (value == null) {
	        return;
	      }

	      sb.appendHtmlConstant("<p>" + value.getName() + " (" + value.getDocument() + ")" + "</p>");
	    }
	  }
    
	private Button acceptBtnDialog;
	
	private List<Registry> registries;
	private Integer registry;
	
	public AonRegistrySelectionDialog(String caption, List<Registry> registries) {
		this.setCaption(caption);
		this.showCloseButton(true);
		
		this.registries = registries;
		
		this.add(createContent());
		
		showDialog();
	}

	private Widget createContent() {
		HTMLPanel content = new HTMLPanel("");
		content.addStyleName(AON.CSS.aonFlexColumn());
		content.getElement().getStyle().setPadding(1, Unit.EM);
		
		Label selectLabel = new Label("Seleccione un " + this.getCaption() + ":");
		selectLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		content.add(selectLabel);
		
		ScrollPanel scroll = new ScrollPanel();
		scroll.getElement().getStyle().setProperty("min-width", "300px");
		scroll.getElement().getStyle().setProperty("max-height", "300px");
		
		Cell<Registry> textCell = new RegistryCell();
		CellList<Registry> cellList = new CellList<Registry>(textCell);
		
		SingleSelectionModel<Registry> selectionModel = new SingleSelectionModel<Registry>();
	    cellList.setSelectionModel(selectionModel);
	    
	    selectionModel.addSelectionChangeHandler(e -> {
	    	Registry selected = selectionModel.getSelectedObject();
    		if (selected != null) registry = selected.getId();
	    });

	    cellList.setRowCount(registries.size(), true);
	    cellList.setRowData(0, registries);
	    selectionModel.setSelected(registries.get(0), true);
	      
	    scroll.add(cellList);
	    content.add(scroll);
	    
	    HTMLPanel buttonsPanel = new HTMLPanel("");
	    buttonsPanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
	    
	    acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText(AON.MSG.accept());
		acceptBtnDialog.addClickHandler(e -> {
			onAccept(registry);
			hide();
		});

		buttonsPanel.add(acceptBtnDialog);
	    
		content.add(buttonsPanel);
		
		return content;
	}
	
	private void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}

	protected abstract void onAccept(Integer registry);
	
}

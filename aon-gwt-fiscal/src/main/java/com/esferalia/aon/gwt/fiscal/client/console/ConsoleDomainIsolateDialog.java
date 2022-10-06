package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.console.ConsoleDomainTable.ConsoleDomainTableCallback;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
 
public class ConsoleDomainIsolateDialog extends AonCustomDialog {
	
	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainIsolateDialog.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	private CheckBox mustFlatten = new CheckBox("Aplanar el dominio origen.");
	private CheckBox validate = new CheckBox("Validar antes de ejecutar.");
	
	private ListBox newSchemaBox = new ListBox();
	private AonTextBox newDomainBox = new AonTextBox();
	private JsConsoleDomain domain;

	public ConsoleDomainIsolateDialog(JsConsoleDomain domain, ConsoleDomainTableCallback callback) {
		this.domain = domain;
		
		setCaption("DUPLICAR DOMINIO");
		setGlassEnabled(true);
		setAnimationEnabled(true);

		
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonScrollArea());
		
		AonDisplayTable table = new AonDisplayTable();
		FlowPanel flattenContainer = new FlowPanel();
		flattenContainer.setStyleName(AON.CSS.aonTextCenter());
		flattenContainer.addStyleName(AON.CSS.aonMarginTop());		
		flattenContainer.addStyleName(AON.CSS.aonPaddingTop());
		flattenContainer.addStyleName(AON.CSS.aonPaddingBottom());
		flattenContainer.addStyleName(AON.CSS.aonBlockCenter());
		flattenContainer.addStyleName(AON.CSS.aonBorder());
		validate.setValue(true);
		validate.setStyleName(AON.CSS.aonMarginLeft());
		
		mustFlatten.setStyleName(AON.CSS.aonMarginLeft());
		mustFlatten.setEnabled(false);
		flattenContainer.add(validate);
		flattenContainer.add(mustFlatten);
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonPadding());
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());

		FlowPanel container = new FlowPanel();
		container.add(table);
		container.add(flattenContainer);
		container.add(buttonsPanel);
		scroll.setWidget(container);
		table.addStyleName(AON.CSS.aonBlockCenter());
		
		Label originLabel = new Label("ORIGEN");
		originLabel.setStyleName(AON.CSS.aonBold());
		
		table.addRow()
			.addCell(originLabel)
			.addCell(new Label("Esquema"), AON.CSS.aonInnerLabel())
			.addCell(new Label( callback.getSchema() ), AON.CSS.aonBold());
		table.addRow()
			.addCell(new Label())
			.addCell(new Label("Dominio"), AON.CSS.aonInnerLabel())
			.addCell(new Label( domain.getName() ), AON.CSS.aonBold(), AON.CSS.aonFontLarger() );	
		table.addRow()
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label( domain.getDescription() ), AON.CSS.aonBold(), AON.CSS.aonFontLarger());	
			
		Label targetLabel = new Label("DESTINO");
		targetLabel.setStyleName(AON.CSS.aonBold());

		newSchemaBox.clear();
		newSchemaBox.addItem(AonStringUtils.EMPTY);
		int i = 1;
		for (String sch : callback.getSchemas()) {
			newSchemaBox.addItem(sch);
			if (AonStringUtils.equals(sch, callback.getSchema())) {
				newSchemaBox.setSelectedIndex(i);
			}
			i++;
		}
		newSchemaBox.addChangeHandler( e -> manageMustFlatten(callback));
		
		newDomainBox.setVisibleLength(25);
		table.addRow()
			.addCell(targetLabel)
			.addCell(new Label("Esquema"), AON.CSS.aonInnerLabel())
			.addCell(newSchemaBox);
		table.addRow()
			.addCell(new Label())
			.addCell(new Label("Dominio"), AON.CSS.aonInnerLabel())
			.addCell(newDomainBox);

		Button acceptButton = new Button();		
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText( AON.MSG.accept());
		
		acceptButton.addClickHandler(event -> {
			acceptButton.setEnabled(false);
			if (validate()) {
				DomainParams params = new DomainParams()
					.setSchema(callback.getSchema())
					.setId(AonNumberUtils.toInteger("" +  domain.getId()))
					.setName(domain.getName())
					.setDescription(domain.getDescription())
					.setValidate(validate.getValue().booleanValue())
					.setMustFlatten(mustFlatten.getValue().booleanValue())
					;
				DomainParams target = new DomainParams()
					.setSchema(newSchemaBox.getSelectedValue())
					.setName(newDomainBox.getValue())
					;
				hide();
				callback.onDuplicate(params, target );
			} else {
				acceptButton.setEnabled(true);
			}
		});
		buttonsPanel.add(acceptButton);
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(event -> hide());
		buttonsPanel.add(cancelButton);
		
		setWidget(scroll);
	}

	private void manageMustFlatten(ConsoleDomainTableCallback callback) {
		if (!AonStringUtils.equals(callback.getSchema(), newSchemaBox.getSelectedValue())) {
			mustFlatten.setEnabled(false);
			mustFlatten.setValue(domain.getParentId() != null && domain.isEnableHeredity());
		} else {
			if (domain.getParentId() == null || !domain.isEnableHeredity()) {
				mustFlatten.setValue(false);
				mustFlatten.setEnabled(false);
			} else {
				mustFlatten.setEnabled(true);		
			}
		}
	}
	
	private boolean validate() {
		if (AonStringUtils.isBlank(newDomainBox.getValue())) {
			Window.alert("El nuevo nombre del dominio debe tener valor");			
			return false;
		}
		return true;
	}

	
}

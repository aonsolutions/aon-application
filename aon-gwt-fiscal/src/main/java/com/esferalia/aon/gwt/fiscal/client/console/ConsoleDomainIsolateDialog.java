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
	private boolean accepted;

	public ConsoleDomainIsolateDialog(JsConsoleDomain domain, ConsoleDomainTableCallback callback) {
		this.domain = domain;
		
		setCaption("DUPLICAR DOMINIO");
		setGlassEnabled(true);
		setAnimationEnabled(true);

		
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonScrollArea());
		
		FlowPanel container = new FlowPanel();
		scroll.setWidget(container);
		
		Label originLabel = new Label("Origen");
		originLabel.setStyleName(AON.CSS.aonBold());
		originLabel.addStyleName(AON.CSS.aonMarginTop());
		originLabel.addStyleName(AON.CSS.aonBorderBottom());
		container.add(originLabel);
			
		AonDisplayTable originTable = new AonDisplayTable();
		originTable.setStyleName(AON.CSS.aonWidthAlmostAll());
		originTable.addStyleName(AON.CSS.aonBlockCenter());
		originTable.addStyleName(AON.CSS.aonBackgroundLigthGray());
		container.add(originTable);
		
		Label nameLabel = new Label( domain.getName());
		nameLabel.setStyleName(AON.CSS.aonClickableLabel());
		nameLabel.setTitle("Copiar valor a nuevo nombre");
		nameLabel.addClickHandler(e -> newDomainBox.setValue(domain.getName()));
		
		originTable.addRow()
			.addCell(new Label("Esquema"), AON.CSS.aonTableLabel())
			.addCell(new Label( callback.getSchema() ), AON.CSS.aonBold());
		originTable.addRow()
			.addCell(new Label("ID Dominio"), AON.CSS.aonTableLabel())
			.addCell(new Label( AonNumberUtils.toString(domain.getId())), AON.CSS.aonBold());	
		originTable.addRow()
			.addCell(new Label(AON.MSG.name()), AON.CSS.aonTableLabel())
			.addCell(nameLabel, AON.CSS.aonNowrap() );	
		originTable.addRow()
			.addCell(new Label(AON.MSG.description()), AON.CSS.aonTableLabel())
			.addCell(new Label( domain.getDescription() ));	
		
		Label targetLabel = new Label("Destino");
		targetLabel.setStyleName(AON.CSS.aonBold());
		targetLabel.addStyleName(AON.CSS.aonMarginTop());
		targetLabel.addStyleName(AON.CSS.aonBorderBottom());
		container.add(targetLabel);

		AonDisplayTable targetTable = new AonDisplayTable();
		targetTable.setStyleName(AON.CSS.aonWidthAlmostAll());
		targetTable.addStyleName(AON.CSS.aonBlockCenter());
		container.add(targetTable);
		
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
		
		newDomainBox.setVisibleLength(40);

		targetTable.addRow()
			.addCell(new Label("Esquema"), AON.CSS.aonTableLabel())
			.addCell(newSchemaBox);
		targetTable.addRow()
			.addCell(new Label("Nuevo nombre"), AON.CSS.aonTableLabel())
			.addCell(newDomainBox);
		
		Label optionsLabel = new Label("Opciones");
		optionsLabel.setStyleName(AON.CSS.aonMarginTop());
		optionsLabel.addStyleName(AON.CSS.aonBorderBottom());
		optionsLabel.addStyleName(AON.CSS.aonBold());
		container.add(optionsLabel);
		
		FlowPanel validatePanel = new FlowPanel();
		container.add(validatePanel);
		
		validate.setValue(true);
		validate.setStyleName(AON.CSS.aonMarginLeft());
		validatePanel.add(validate);

		FlowPanel mustFlattenPanel = new FlowPanel();
		container.add(mustFlattenPanel);
		
		mustFlatten.setStyleName(AON.CSS.aonMarginLeft());
		mustFlatten.setEnabled(false);
		mustFlattenPanel.add(mustFlatten);
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonPadding());
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());
		container.add(buttonsPanel);


		Button acceptButton = new Button();		
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText( AON.MSG.accept());
		
		acceptButton.addClickHandler(event -> {
			acceptButton.setEnabled(false);
			if (!accepted) {
				accepted = true;
				Label acceptLabel = new Label("Pulse \"Aceptar\" si desea continuar");
				acceptLabel.setStyleName(AON.CSS.aonBold());
				acceptLabel.addStyleName(AON.CSS.aonMarginTop());
				acceptLabel.addStyleName(AON.CSS.aonColorBlue());
				buttonsPanel.add(acceptLabel);				
				acceptButton.setEnabled(true);
			} else {
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
		
		manageMustFlatten(callback);
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

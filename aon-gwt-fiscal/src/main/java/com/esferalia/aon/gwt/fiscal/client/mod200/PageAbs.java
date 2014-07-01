package com.esferalia.aon.gwt.fiscal.client.mod200;

import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.RESOURCES;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200ConstantsBehaviour.BEHAVIOUR_KEYS_MAP;

import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.fiscal.shared.mod200.DoubleVariable;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;

public abstract class PageAbs extends ResizeComposite {

	protected Mod200Object mod200Object;

	private Map<Mod200Key, DoubleTextBox> inputs = new HashMap<Mod200Key, DoubleTextBox>();
	private Map<Mod200Key, Label> labels = new HashMap<Mod200Key, Label>();

	@UiField
	Panel basePanel;
	
	@UiField(provided = true)
	FlexTable table;
	
	public PageAbs() {
		table = new FlexTable();
	}
	
	public void dump(Mod200Object mod200) {
		this.mod200Object = mod200;
		this.mod200Object.register( new IMod200ChangeListener() {
			
			@Override
			public void mod200Changed(Mod200 mod200) {
				for (Mod200Key key : mod200.getDraftMap().keySet()) {
					if (inputs.containsKey(key)) {
						DoubleTextBox input = inputs.get(key);
						DoubleVariable var = mod200.getDraftMap().get(key);
						if (!var.isChangedByUser()) {
							input.setValue(var.getValue()); ;
						}
						input.addStyleName(RESOURCES.css().aonChanged());
					}
					if (labels.containsKey(key)) {
						Label label = labels.get(key);
						label.removeStyleName(RESOURCES.css().aonMod200BoxError());
						label.setTitle(null);
					}
				}
			}
			
		});
		initializeTable();
	}
	
	public Map<Mod200Key, DoubleTextBox> getInputs() {
		return inputs;
	}
	public Map<Mod200Key, Label> getLabels() {
		return labels;
	}
	
	protected int paintKey(final Mod200Key key,int row) {
		return paintKey(table,key,row);
	}
	
	protected int paintKey(FlexTable tab,final Mod200Key key,int row) {
		Boolean[] behaviour = BEHAVIOUR_KEYS_MAP.get(key.toString());
		boolean title = behaviour != null && behaviour[0]; 
		boolean disabled = behaviour != null && behaviour[1];
		
		Label desc = new Label(key.getDescription() );
		if (title) {
			desc.setStyleName(RESOURCES.css().aonBold());
		}
		tab.setWidget(row, 0, desc);
		tab.getFlexCellFormatter().setStyleName(row, 0, RESOURCES.css().aonMod200BorderBottom());
		
		FlowPanel panel = new FlowPanel();
		Label code = new Label(key.getCode( mod200Object.getAdministration() ));
		code.setStyleName(RESOURCES.css().aonMod200Box());
		panel.add(code);
		getLabels().put(key, code);
		
		final DoubleTextBox text = new DoubleTextBox();
		text.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if (text.isValidValue()) {
					text.addStyleName(RESOURCES.css().aonChanged());
					mod200Object.doubleValueChanged(key, text.getDoubleValue());
				}
			}
		});
		text.setValue(mod200Object.getDoubleValue(key));
		text.addStyleName(RESOURCES.css().aonMod200MarginLeft());
		text.addStyleName(RESOURCES.css().aonMod200PaddingLeft());
		text.setChangeDisplayStyleName(RESOURCES.css().aonValueChanged());
		text.setEnabled(!disabled);
		panel.add(text);
		
		getInputs().put(key, text);
		if (!title) {
			panel.addStyleName(RESOURCES.css().aonMod200PaddingRight());
		}
		tab.setWidget(row, 1, panel);
		tab.getFlexCellFormatter().addStyleName(row, 1, RESOURCES.css().aonTextRight());
		tab.getFlexCellFormatter().addStyleName(row, 1, RESOURCES.css().aonNowrap());
		return ++row;
	}

	protected abstract void initializeTable();
}

package com.esferalia.aon.gwt.fiscal.client.mod200;

import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.RESOURCES;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200ConstantsBehaviour.BEHAVIOUR_KEYS_MAP;

import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.fiscal.shared.mod200.DoubleVariable;
import com.esferalia.aon.gwt.fiscal.shared.mod200.IMod200KeysProvider;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;

public abstract class PageAbs extends ResizeComposite {

	protected Mod200Object mod200Object;

	private Map<Mod200Key, DoubleTextBox> inputs = new HashMap<Mod200Key, DoubleTextBox>();
	private Map<Mod200Key, BoxLabel> labels = new HashMap<Mod200Key, BoxLabel>();

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
						BoxLabel label = labels.get(key);
						label.removeErrorState();
					}
				}
			}
			
		});
		initializeTable();
	}
	
	public Map<Mod200Key, DoubleTextBox> getInputs() {
		return inputs;
	}
	public Map<Mod200Key, BoxLabel> getLabels() {
		return labels;
	}
	
	protected int paintKey(final Mod200Key key,int row) {
		return paintKey(table,key,row);
	}
	
	protected int paintKey(FlexTable tab,final Mod200Key key,int row) {
		boolean title = isTitle(key);
		boolean disabled = isDisabled(key);
		
		Label desc = new Label(key.getDescription() );
		if (title) {
			desc.setStyleName(RESOURCES.css().aonBold());
		}
		tab.setWidget(row, 0, desc);
		tab.getFlexCellFormatter().setStyleName(row, 0, RESOURCES.css().aonMod200BorderBottom());
		
		FlowPanel panel = new FlowPanel();
		BoxLabel code = new BoxLabel(key.getCode( mod200Object.getAdministration() ));
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

	protected boolean isDisabled(Mod200Key key) {
		Boolean[] behaviour = BEHAVIOUR_KEYS_MAP.get(key.toString());
		return behaviour != null && behaviour[1];
	}

	protected boolean isTitle(Mod200Key key) {
		Boolean[] behaviour = BEHAVIOUR_KEYS_MAP.get(key.toString());
		return (behaviour != null && behaviour[0]); 
	}

	protected int paintKeyBreakdown(FlexTable tab,int row,final String label, IMod200KeysProvider[] keysProvider,String[] headers) {
		
		FlexTable tableDetail = getFlexTable(tab,row,label,headers);
		int r = 1;
		int col = 0;
		for (IMod200KeysProvider key : keysProvider) {
			Label desc = new Label(key.getDescription() );
			tableDetail.setWidget(r, 0, desc);
			tableDetail.getFlexCellFormatter().setStyleName(r, 0, RESOURCES.css().aonMod200BorderBottom());
			col = 1;
			for (final Mod200Key k : key.getKeys() ) {
				if (k != null){
					Boolean[] behaviour = BEHAVIOUR_KEYS_MAP.get(k.toString());
					boolean disabled = behaviour != null && behaviour[1];
	
					FlowPanel panel = new FlowPanel();
					BoxLabel code = new BoxLabel(k.getCode( mod200Object.getAdministration() ));
					panel.add(code);
					getLabels().put(k, code);
					
					final DoubleTextBox text = new DoubleTextBox(12);
					text.addChangeHandler(new ChangeHandler() {
						@Override
						public void onChange(ChangeEvent event) {
							if (text.isValidValue()) {
								text.addStyleName(RESOURCES.css().aonChanged());
								mod200Object.doubleValueChanged(k, text.getDoubleValue());
							}
						}
					});
					text.setValue(mod200Object.getDoubleValue(k));
					text.addStyleName(RESOURCES.css().aonMod200MarginLeft());
					text.addStyleName(RESOURCES.css().aonMod200PaddingLeft());
					text.setChangeDisplayStyleName(RESOURCES.css().aonValueChanged());
					text.setEnabled(!disabled);
					panel.add(text);
					
					getInputs().put(k, text);
					tableDetail.setWidget(r, col, panel);
					tableDetail.getFlexCellFormatter().addStyleName(r, col, RESOURCES.css().aonTextRight());
					tableDetail.getFlexCellFormatter().addStyleName(r, col, RESOURCES.css().aonNowrap());
				}
				++col;
			}
			++r;
		}
		return ++row;
	}
	
	protected FlexTable getFlexTable(FlexTable tab,int row,final String label, String[] headers) {
		FlowPanel container = new FlowPanel();
		container.addStyleName(RESOURCES.css().aonGroup());
		
		FlowPanel titleContainer = new FlowPanel();
		titleContainer.addStyleName(RESOURCES.css().aonGroupTitle());
		container.add(titleContainer);
		final InlineLabel titleLabel = new InlineLabel();
		titleLabel.addStyleName(RESOURCES.css().aonClickable());
		titleContainer.add(titleLabel);
		titleLabel.setText(label + " \u25BA");
		
		final DisclosurePanel bodyContainer = new DisclosurePanel();
		bodyContainer.addStyleName(RESOURCES.css().aonWidthAll());
		titleLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				bodyContainer.setOpen(!bodyContainer.isOpen());
			}
		});
		bodyContainer.addOpenHandler(new OpenHandler<DisclosurePanel>() {
			
			@Override
			public void onOpen(OpenEvent<DisclosurePanel> event) {
				titleLabel.setText(label + " \u25BC");
				bodyContainer.addStyleName(RESOURCES.css().aonMod200InnerGroupBody());
			}
		});
		bodyContainer.addCloseHandler(new CloseHandler<DisclosurePanel>() {
			@Override
			public void onClose(CloseEvent<DisclosurePanel> event) {
				titleLabel.setText(label + " \u25BA");
				bodyContainer.removeStyleName(RESOURCES.css().aonMod200InnerGroupBody());
			}
		});
		bodyContainer.addStyleName(RESOURCES.css().aonGroupBody());
		container.add(bodyContainer);
		FlexTable tableDetail = new FlexTable();
		tableDetail.addStyleName(RESOURCES.css().aonWidthAll());
		bodyContainer.add(tableDetail);
		int r = 0;
		int col = 0;
		if (headers != null) {
			for (String  headerText : headers ) {
				Label headerLabel = new Label(headerText);
				tableDetail.setWidget(r, col, headerLabel);
				tableDetail.getFlexCellFormatter().addStyleName(r, col, RESOURCES.css().aonBold());
				tableDetail.getFlexCellFormatter().addStyleName(r, col, RESOURCES.css().aonBorderBottom());
				tableDetail.getFlexCellFormatter().addStyleName(r, col, RESOURCES.css().aonTextCenter());
				tableDetail.getColumnFormatter().setWidth(col, col==0?"auto":"160px");
				++col;
			}
		}
		tab.setWidget(row, 0, container);
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		return tableDetail;
	}

	protected abstract void initializeTable();
}

package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;


import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.tree.node.D2DepositTreeObject;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositBehaviour;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositFooterKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.DepositType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;

public abstract class PageAbs extends ResizeComposite {
	
	protected static final int MEMORY_NOTE_VISIBLE_LENGTH = 8;
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	NormalizedMemory normalizedMemory;
	Map<String, String> map;
	Enterprise enterprise;
	
	interface DeleteButtonTemplate extends SafeHtmlTemplates {
		@Template("<input type=\"button\" value=\"&nbsp;\" class=\"aon-icon-delete\" style=\"border: medium none !important;\">")
		SafeHtml render(String option);
	}
	

	
	

	protected D2DepositTreeObject d2DepositObject;
	

	private HashMap<D2DepositKey, DoubleBox> inputs = new HashMap<D2DepositKey, DoubleBox>();
	private HashMap<D2DepositKey, BoxLabel> labels = new HashMap<D2DepositKey, BoxLabel>();
	
	private static DepositType depositType;

	@UiField
	Panel basePanel;

	@UiField(provided = true)
	FlexTable table;
	
	public PageAbs() {
		table = new FlexTable();
	}
	
	public void dump(D2DepositTreeObject d2DepositObject, String part) {
		this.d2DepositObject = d2DepositObject;
		inma.getSchema(enterprise.getDocument(),part,enterprise.getDomain(),false, new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				map = result;
				depositType = DepositType.valueOfLabel( map.get(D2DepositConstants.DEPOSIT_TYPE));
				initializeTable();
			}
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
		
		//refreshDraftMap(this.d2DepositObject.getD2Deposit());
	}
	public boolean isPymes() {
		return depositType == DepositType.PYMES;
	}
	
	public Map<D2DepositKey, DoubleBox> getInputs() {
		return inputs;
	}
	public Map<D2DepositKey, BoxLabel> getLabels() {
		return labels;
	}
	
	protected int paintKey(final D2DepositKey key,int row) {
		return paintKey(table,key,row);
	}
	
	protected int paintKey(final D2DepositHeaderKey key,int row) {
		return paintKey(table,key,row);
	}
	
	protected int paintKey(final D2DepositFooterKey key,int row) {
		return paintKey(table,key,row);
	}
	
	protected int paintKey(FlexTable tab,final D2DepositKey key,int row) {
		paintKeyDescription(tab,key,row,0);
		paintKeyField(tab,key,row,1);	
		return ++row;
	}
	
	protected int paintKey(FlexTable tab,final D2DepositHeaderKey key,int row) {
		paintKeyDescription(tab,key,row,0);
		paintKeyField(tab,key,row,1);	
		return ++row;
	}
	
	protected int paintKey(FlexTable tab,final D2DepositFooterKey key,int row) {
		paintKeyDescription(tab,key,row,0);
		paintKeyField(tab,key,row,1);	
		return ++row;
	}
	
	protected void paintEmptyCell(FlexTable tab, int row,int col) {
		tab.setWidget(row, col, new Label());
	}
	
	protected void paintKeyDescription(FlexTable tab, D2DepositKey key, int row,int col) {
		String d = key.getDescription();
		Label desc = new Label( AonStringUtils.abbreviate(d, 120) );
		if (AonStringUtils.length(d) > 117) {
			desc.setTitle(key.getDescription());
		}
		if (isTitle(key)) {
			desc.setStyleName(AON.AON_CSS.aonBold());
		}
		tab.setWidget(row, col, desc);
		tab.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalBorderBottom());
	}

	protected void paintKeyDescription(FlexTable tab, D2DepositHeaderKey key, int row,int col) {
		String d = key.getDescription();
		Label desc = new Label( AonStringUtils.abbreviate(d, 120) );
		if (AonStringUtils.length(d) > 117) {
			desc.setTitle(key.getDescription());
		}
		if (isTitle(key)) {
			desc.setStyleName(AON.AON_CSS.aonBold());
		}
		tab.setWidget(row, col, desc);
		tab.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalBorderBottom());
	}
	
	protected void paintKeyDescription(FlexTable tab, D2DepositFooterKey key, int row,int col) {
		String d = key.getDescription();
		Label desc = new Label( AonStringUtils.abbreviate(d, 120) );
		if (AonStringUtils.length(d) > 117) {
			desc.setTitle(key.getDescription());
		}
		if (isTitle(key)) {
			desc.setStyleName(AON.AON_CSS.aonBold());
		}
		tab.setWidget(row, col, desc);
		tab.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalBorderBottom());
	}
	
	String codeAux;
	protected void paintKeyField(FlexTable tab,final D2DepositKey key,int row, int col) {
		boolean disabled = isDisabled(key);
		
		FlowPanel panel = new FlowPanel();
		String codeId = key.getCode();
		boolean show = true;
		try {
			show = Integer.parseInt(codeId) > 0;
		} catch (NumberFormatException e) {
			// Nothing;
		}
		if (show) {
			BoxLabel code = new BoxLabel(codeId);
			getLabels().put(key, code);
			panel.add(code);
		}

		final DoubleBox text = new DoubleBox();
		
		codeAux = codeId;
		text.addChangeHandler(new ChangeHandler() {
			String code = codeAux;
			@Override
			public void onChange(ChangeEvent event) {
				try {
					if (AonStringUtils.isEmpty(text.getText())) {
						text.setValue(0.0,false);
					}
					Double d = text.getValueOrThrow();
					text.addStyleName(AON.AON_CSS.aonChanged());
					
					normalizedMemory.saveButton.setEnabled(true);
					normalizedMemory.cancelButton.setVisible(true);
					inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),code, d.toString() , new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {}
						@Override
						public void onSuccess(Void result) {}
					});
				} catch (ParseException e) {
					// nothing.
				}
				
			}
		});
		if(map.containsKey(key.getName())){
			Double d = Double.parseDouble(map.get(key.getName()));
			text.setValue(d);
		}
		else text.setValue(0.0);
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		text.setEnabled(!disabled);
		panel.add(text);
		
		getInputs().put(key, text);
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextRight());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}
	
	protected void paintKeyField(FlexTable tab,final D2DepositHeaderKey key,int row, int col) {
		paintKeyField(tab,key,row,col, true);	
	}
	
	protected void paintKeyField(FlexTable tab,final D2DepositHeaderKey key,int row, int col, boolean showBox) {
		boolean disabled = isDisabled(key);
		
		FlowPanel panel = new FlowPanel();
		if (showBox) {
			BoxLabel code = new BoxLabel(key.getCode());
			panel.add(code);
		}
		final DoubleBox text = new DoubleBox();
		text.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				try {
					if (AonStringUtils.isEmpty(text.getText())) {
						text.setValue(0.0,false);
					}
					Double d = text.getValueOrThrow();
					text.addStyleName(AON.AON_CSS.aonChanged());
					normalizedMemory.saveButton.setEnabled(true);
					normalizedMemory.cancelButton.setVisible(true);
					inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),key.getCode(), d.toString() , new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {}
						@Override
						public void onSuccess(Void result) {}
					});
				} catch (ParseException e) {
					// nothing.
				}
				
			}
		});
		if(map.containsKey(key.getName())){
			Double d = Double.parseDouble(map.get(key.getName()));
			text.setValue(d);
		}
		else text.setValue(0.0);
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		text.setEnabled(!disabled);
		panel.add(text);
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextRight());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}
	
	protected void paintKeyField(FlexTable tab,final D2DepositFooterKey key,int row, int col) {
		boolean disabled = isDisabled(key);
		
		FlowPanel panel = new FlowPanel();
		String codeId = key.getCode();
		boolean show = true;
		try {
			show = Integer.parseInt(codeId) > 0;
		} catch (NumberFormatException e) {
			// Nothing;
		}
		if (show) {
			BoxLabel code = new BoxLabel(codeId);
			//getLabels().put(key, code);
			panel.add(code);
		}

		final DoubleBox text = new DoubleBox();
		
		codeAux = codeId;
		text.addChangeHandler(new ChangeHandler() {
			String code = codeAux;
			@Override
			public void onChange(ChangeEvent event) {
				try {
					if (AonStringUtils.isEmpty(text.getText())) {
						text.setValue(0.0,false);
					}
					Double d = text.getValueOrThrow();
					text.addStyleName(AON.AON_CSS.aonChanged());
					
					normalizedMemory.saveButton.setEnabled(true);
					normalizedMemory.cancelButton.setVisible(true);
					inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),code, d.toString() , new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {}
						@Override
						public void onSuccess(Void result) {}
					});
					//d2DepositObject.doubleValueChanged(key, d );
				} catch (ParseException e) {
					// nothing.
				}
				
			}
		});
		//text.setValue(d2DepositObject.getDoubleValue(key));
		if(map.containsKey(key.getName())){
			Double d = Double.parseDouble(map.get(key.getName()));
			text.setValue(d);
		}
		else text.setValue(0.0);
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		text.setEnabled(!disabled);
		panel.add(text);
		
		//getInputs().put(key, text);
		if (!isTitle(key)) {
			panel.addStyleName(AON.AON_CSS.aonFiscalPaddingRight());
		}
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextRight());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}

	protected boolean isDisabled(D2DepositKey key) {
		Boolean[] behaviour = D2DepositBehaviour.BEHAVIOUR_KEYS_MAP.get(key.getName());
		return behaviour != null && behaviour[1];
	}
	protected boolean isDisabled(D2DepositHeaderKey key) {
		Boolean[] behaviour = D2DepositBehaviour.BEHAVIOUR_KEYS_MAP.get(key.getName());
		return behaviour != null && behaviour[1];
	}
	
	protected boolean isDisabled(D2DepositFooterKey key) {
		Boolean[] behaviour = D2DepositBehaviour.BEHAVIOUR_KEYS_MAP.get(key.getName());
		return behaviour != null && behaviour[1];
	}

	protected boolean isTitle(D2DepositKey key) {
		Boolean[] behaviour = D2DepositBehaviour.BEHAVIOUR_KEYS_MAP.get(key.getName());
		return (behaviour != null && behaviour[0]); 
	}
	
	protected boolean isTitle(D2DepositHeaderKey key) {
		Boolean[] behaviour = D2DepositBehaviour.BEHAVIOUR_KEYS_MAP.get(key.getName());
		return (behaviour != null && behaviour[0]); 
	}
	
	protected boolean isTitle(D2DepositFooterKey key) {
		Boolean[] behaviour = D2DepositBehaviour.BEHAVIOUR_KEYS_MAP.get(key.getName());
		return (behaviour != null && behaviour[0]); 
	}

	protected FlexTable getFlexTable(FlexTable tab,int row,final String label, String[] headers) {
		FlowPanel container = new FlowPanel();
		container.addStyleName(AON.AON_CSS.aonGroup());
		
		FlowPanel titleContainer = new FlowPanel();
		titleContainer.addStyleName(AON.AON_CSS.aonGroupTitle());
		container.add(titleContainer);
		final InlineLabel titleLabel = new InlineLabel();
		titleLabel.addStyleName(AON.AON_CSS.aonClickable());
		titleContainer.add(titleLabel);
		titleLabel.setText(label + " \u25BA");
		
		final DisclosurePanel bodyContainer = new DisclosurePanel();
		bodyContainer.addStyleName(AON.AON_CSS.aonWidthAll());
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
				bodyContainer.addStyleName(AON.AON_CSS.aonFiscalInnerGroupBody());
			}
		});
		bodyContainer.addCloseHandler(new CloseHandler<DisclosurePanel>() {
			@Override
			public void onClose(CloseEvent<DisclosurePanel> event) {
				titleLabel.setText(label + " \u25BA");
				bodyContainer.removeStyleName(AON.AON_CSS.aonFiscalInnerGroupBody());
			}
		});
		bodyContainer.addStyleName(AON.AON_CSS.aonGroupBody());
		container.add(bodyContainer);
		FlexTable tableDetail = new FlexTable();
		tableDetail.addStyleName(AON.AON_CSS.aonWidthAll());
		bodyContainer.add(tableDetail);
		int r = 0;
		int col = 0;
		if (headers != null) {
			for (String  headerText : headers ) {
				Label headerLabel = new Label(headerText);
				tableDetail.setWidget(r, col, headerLabel);
				tableDetail.getFlexCellFormatter().addStyleName(r, col, AON.AON_CSS.aonBold());
				tableDetail.getFlexCellFormatter().addStyleName(r, col, AON.AON_CSS.aonBorderBottom());
				tableDetail.getFlexCellFormatter().addStyleName(r, col, AON.AON_CSS.aonTextCenter());
				if (col>0) {
					tableDetail.getColumnFormatter().setWidth(col, "160px");
				}
				++col;
			}
		}
		tab.setWidget(row, 0, container);
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		return tableDetail;
	}

	protected void paintKeyFieldTextBox(FlexTable tab,final String codeId, final D2DepositHeaderKey key,int row, int col) {
		boolean disabled = isDisabled(key);
		FlowPanel panel = new FlowPanel();
		BoxLabel code = new BoxLabel(codeId);
		panel.add(code);

		final TextBox text = new TextBox();
		text.setVisibleLength(MEMORY_NOTE_VISIBLE_LENGTH);
		text.setStyleName(AON.AON_CSS.aonInputText());
		text.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				try {
					if (AonStringUtils.isEmpty(text.getText())) {
						text.setValue("",false);
					}
					String d = text.getValueOrThrow();
					text.addStyleName(AON.AON_CSS.aonChanged());
					
					normalizedMemory.saveButton.setEnabled(true);
					normalizedMemory.cancelButton.setVisible(true);
					inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),key.getCode(), d , new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {}
						@Override
						public void onSuccess(Void result) {}
					});
				} catch (ParseException e) {
					// nothing.
				}
			}
		});
		if (map.containsKey(key.getName())){
			String d = map.get(key.getName());
			text.setValue(d);
		} else {
			text.setValue(AonStringUtils.EMPTY);
		}
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		text.setEnabled(!disabled);
		panel.add(text);
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextLeft());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}

	protected abstract void initializeTable();
}

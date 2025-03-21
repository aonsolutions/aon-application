package com.esferalia.aon.gwt.fiscal.client.mod131;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod131ActivityModule;
import com.esferalia.aon.occam.api.model.fiscal.modules.IEpigraph;
import com.esferalia.aon.occam.api.model.fiscal.modules.Module;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2016.Epigraph;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model131Activity2023 extends DockLayoutPanel implements HasValueChangeHandlers<Mod131Activity> {
	
	private static final String WIDTH_150PX = "150px";
	private final Label epigraph = new Label();
	private final Label epigraphLabel = new Label();
	
	private TabLayoutPanel tabLayoutPanel = new TabLayoutPanel(26, Unit.PX);
	
	private CheckBox dis = new CheckBox();
	private AonDoubleBox com = new AonDoubleBox(6);
	private AonIntegerBox tem = new AonIntegerBox(6);
	private AonIntegerBox nue = new AonIntegerBox(6);
	private CheckBox ceu = new CheckBox();
	private CheckBox loc = new CheckBox();
	private AonIntegerBox veh = new AonIntegerBox(6);
	private CheckBox cap = new CheckBox();
	private CheckBox tns = new CheckBox(); 
	private CheckBox tss = new CheckBox();
	private ListBox mun = new ListBox();
	private AonIntegerBox emp = new AonIntegerBox(6);
	private ListBox lor = new ListBox();
	private ListBox bat = new ListBox();
	private AonDoubleBox prc = new AonDoubleBox(6);

	private Label description0 = new Label();
	private AonDoubleBox value0 = new AonDoubleBox(6);
	private Label unit0 = new Label();
	private AonDoubleBox factor0 = new AonDoubleBox(6);
	private AonDoubleBox result0 = new AonDoubleBox(6);

	private Label description1 = new Label();
	private AonDoubleBox value1 = new AonDoubleBox(6);
	private Label unit1 = new Label();
	private AonDoubleBox factor1 = new AonDoubleBox(6);
	private AonDoubleBox result1 = new AonDoubleBox(6);

	private Label description2 = new Label();
	private AonDoubleBox value2 = new AonDoubleBox(6);
	private Label unit2 = new Label();
	private AonDoubleBox factor2 = new AonDoubleBox(6);
	private AonDoubleBox result2 = new AonDoubleBox(6);

	private Label description3 = new Label();
	private AonDoubleBox value3 = new AonDoubleBox(6);
	private Label unit3 = new Label();
	private AonDoubleBox factor3 = new AonDoubleBox(6);
	private AonDoubleBox result3 = new AonDoubleBox(6);

	private Label description4 = new Label();
	private AonDoubleBox value4 = new AonDoubleBox(6);
	private Label unit4 = new Label();
	private AonDoubleBox factor4 = new AonDoubleBox(6);
	private AonDoubleBox result4 = new AonDoubleBox(6);

	private Label description5 = new Label();
	private AonDoubleBox value5 = new AonDoubleBox(6);
	private Label unit5 = new Label();
	private AonDoubleBox factor5 = new AonDoubleBox(6);
	private AonDoubleBox result5 = new AonDoubleBox(6);

	private Label description6 = new Label();
	private AonDoubleBox value6 = new AonDoubleBox(6);
	private Label unit6 = new Label();
	private AonDoubleBox factor6 = new AonDoubleBox(6);
	private AonDoubleBox result6 = new AonDoubleBox(6);

	private AonDoubleBox rnp0 = new AonDoubleBox(6);
	private AonDoubleBox rnp = new AonDoubleBox(6);
	private AonDoubleBox iem = new AonDoubleBox(6);
	private AonDoubleBox iin = new AonDoubleBox(6);
	private AonDoubleBox rnm = new AonDoubleBox(6);
	private AonDoubleBox ic1 = new AonDoubleBox(6);
	private AonDoubleBox ic2 = new AonDoubleBox(6);
	private AonDoubleBox ic3 = new AonDoubleBox(6);
	private AonDoubleBox ic4 = new AonDoubleBox(6);
	private AonDoubleBox ic5 = new AonDoubleBox(6);
	private AonDoubleBox rpf = new AonDoubleBox(6);
	private AonDoubleBox rlo = new AonDoubleBox(6);
	private AonDoubleBox rdr = new AonDoubleBox(6);
	private AonIntegerBox dia = new AonIntegerBox(6);
	private AonDoubleBox net = new AonDoubleBox(6);
	private AonDoubleBox por = new AonDoubleBox(6);
	private AonDoubleBox res = new AonDoubleBox(6);
	
	public static interface IMod131ActivityCallback {
		Model131ModuleOptions getOptions();
		Mod131 getModel();
		Mod131Activity getActivity();
		void onAccept(Mod131Activity act);
		void onCancel();
		void onRemove();
	}
	
	public Model131Activity2023(final IMod131ActivityCallback callback) {
		super(Unit.PX);
		setStyleName(AON.CSS.aonSelector());
		setWidth("700px");
		setHeight("590px");
		
		mun.addItem("Hasta 2.000 habitantes.");
		mun.addItem("Desde 2.001 hasta 5.000 habitantes.");
		mun.addItem("Desde 5.001 hasta 10.000 habitantes.");
		mun.addItem("Desde 10.001 hasta 50.000 habitantes.");
		mun.addItem("Desde 50.001 hasta 100.000 habitantes.");
		mun.addItem("M\u00E1s de 100.000 habitantes.");
		mun.addItem("Madrid o Barcelona.");
		mun.setWidth(WIDTH_150PX);
		
		lor.addItem("-");
		lor.addItem("Actividad realizada exclusivamente en Lorca.");
		lor.addItem("Actividad realizada en Lorca y otros municipios.");
		lor.setWidth(WIDTH_150PX);
		
		bat.addItem("-");
		bat.addItem("Una batea y ning\u00FAn barco");
		bat.addItem("Una batea y un barco de menos de 15 TRB");
		bat.addItem("Una batea y un barco de 15 a 30 TRB");
		bat.addItem("Una batea y un barco de m\u00E1s de 30 TRB");
		bat.addItem("Dos bateas y ning\u00FAn barco");
		bat.addItem("Dos bateas y un barco de menos de 15 TRB");
		bat.addItem("Otros: n\u00FAmero de bateas, barcos o TRB distintos de los anteriores");
		bat.setWidth(WIDTH_150PX);

		AonToolbar toolbarPanel = new AonToolbar("");
		addNorth(toolbarPanel, AonToolbar.HEIGTH);

		final AonToolbarButton accept = new AonToolbarButton(AON.MSG.saveAction(),AON.CSS.aonIconSave());
		accept.addClickHandler(event -> callback.onAccept(callback.getActivity()));
		toolbarPanel.add(accept);
		
		final AonToolbarButton cancel = new AonToolbarButton(AON.MSG.cancelAction(),AON.CSS.aonIconCancel());
		cancel.addClickHandler(event -> callback.onCancel());
		toolbarPanel.add(cancel);
		
		final AonToolbarButton remove = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		remove.addClickHandler(event -> {
			AonConfirmDialog dialog = new AonConfirmDialog();
			dialog.confirm(AON.MSG.confirmDeleteAction(), new AonConfirmDialogCallback() {
				@Override
				public void onCancel() {
					// Nothing
				}
				
				@Override
				public void onAccept() {
					callback.onRemove();
				}
			});
		});
		toolbarPanel.add(remove);
		
		FlowPanel epigraphContainerPanel = new FlowPanel();
		epigraphContainerPanel.setStyleName(AON.CSS.aonPadding());
		epigraphContainerPanel.addStyleName(AON.CSS.aonWidthAlmostAll());
		epigraphContainerPanel.addStyleName(AON.CSS.aonBlockCenter());
		epigraphContainerPanel.addStyleName(AON.CSS.aonBorder());
		epigraphContainerPanel.addStyleName(AON.CSS.aonFlexBlock());
		epigraphContainerPanel.addStyleName(AON.CSS.aonMarginBottom());
		epigraphContainerPanel.addStyleName(AON.CSS.aonBackgroundLigthGray());
		
		epigraph.setStyleName(AON.CSS.aonBold());
		epigraph.addStyleName(AON.CSS.aonFontLarger());
		epigraph.getElement().getStyle().setWidth(60, Unit.PX);
		epigraphContainerPanel.add(epigraph);

		AonTableButton showEpigraphs = new AonTableButton(AON.MSG.epigraph() ,AON.CSS.aonIconSearch());
		epigraphContainerPanel.add(showEpigraphs);
		
		epigraphLabel.setStyleName(AON.CSS.aonFontLarger());
		epigraphLabel.addStyleName(AON.CSS.aonNowrap());
		epigraphLabel.addStyleName(AON.CSS.aonFlexGrow1());
		epigraphLabel.addStyleName(AON.CSS.aonMarginLeft());
		epigraphContainerPanel.add(epigraphLabel);
		
		addNorth(epigraphContainerPanel, 40);
		
		EpigraphPanel epigraphPanel = new EpigraphPanel( callback.getModel().getYear() );
		epigraphPanel.addSelectionHandler(event -> {
			if (AonStringUtils.isNotBlank( callback.getActivity().getEpigraph())) {
				AonConfirmDialog dialog = new AonConfirmDialog();
				dialog.confirm(AON.MSG.newEpigrapSelected(), new AonConfirmDialogCallback() {
					
					@Override
					public void onCancel() {
						// Nothing
					}
					
					@Override
					public void onAccept() {
						accept(callback, event.getSelectedItem());
					}
				});
			} else {
				accept(callback,event.getSelectedItem());
			}
		});
		showEpigraphs.addClickHandler(event -> epigraphPanel.onShow());
		addNorth(epigraphContainerPanel, 40);

		populateActivity(callback);
		
		ScrollPanel additionalDataPanel = new ScrollPanel();
		additionalDataPanel.setWidget(getAdditionalDataPanel(callback));
		tabLayoutPanel.add(additionalDataPanel, AON.MSG.additionalData());
		
		ScrollPanel modulesPanel = new ScrollPanel();
		modulesPanel.setWidget(getModulesPanel(callback));
		tabLayoutPanel.add(modulesPanel, AON.MSG.modules());

		ScrollPanel resultPanel = new ScrollPanel();
		resultPanel.setWidget(getResultPanel(callback));
		tabLayoutPanel.add(resultPanel, AON.MSG.irpfActivityRpf());

		add(tabLayoutPanel);
		
		onResize();
	}

	private Widget getAdditionalDataPanel(IMod131ActivityCallback callback) {
		AonDisplayTable table = new AonDisplayTable();
		table.addStyleName(AON.CSS.aonWidthAlmostAll());
		table.addStyleName(AON.CSS.aonBlockCenter());
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		
		dis.addClickHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityDis()),AON.CSS.aonBold() )
			.addCell(dis);
		
		com.addValueChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityCom()),AON.CSS.aonBold() )
			.addCell(com);

		tem.addValueChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityTem()),AON.CSS.aonBold() )
			.addCell(tem);

		nue.addValueChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityNue()),AON.CSS.aonBold() )
			.addCell(nue);

		ceu.addValueChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityCeu()),AON.CSS.aonBold() )
			.addCell(ceu);

		loc.addValueChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityLoc()),AON.CSS.aonBold() )
			.addCell(loc);
		
		veh.addValueChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityVeh()),AON.CSS.aonBold() )
			.addCell(veh);
		
		cap.addValueChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityCap()),AON.CSS.aonBold() )
			.addCell(cap);
		
		tns.addClickHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityTns()),AON.CSS.aonBold() )
			.addCell(tns);
		
		tss.addClickHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityTss()),AON.CSS.aonBold() )
			.addCell(tss);
		
		mun.addChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityMun()),AON.CSS.aonBold() )
			.addCell(mun);
		
		emp.addValueChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityEmp()),AON.CSS.aonBold() )
			.addCell(emp);

		lor.addChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityLor()),AON.CSS.aonBold() )
			.addCell(lor);

		bat.addChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityBat()),AON.CSS.aonBold() )
			.addCell(bat);
		
		prc.addValueChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityPrc()),AON.CSS.aonBold() )
			.addCell(prc);

		return table;
	}

	private Widget getModulesPanel(IMod131ActivityCallback callback) {
		AonDisplayTable table = new AonDisplayTable();
		table.addStyleName(AON.CSS.aonWidthAlmostAll());
		table.addStyleName(AON.CSS.aonBlockCenter());
		table.addStyleName(AON.CSS.aonMarginBottom());
		
//	<colgropup>
//		<col width="auto" />
//		<col width="100px" />
//		<col width="150px" />
//		<col width="100px" />
//		<col width="100px" />
//	</colgropup>
		
		table.addRow()
			.addCell(new Label(AON.MSG.description()), AON.CSS.aonDisplayGridHeaderCell(), AON.CSS.aonFlexGrow1())
			.addCell(new Label(AON.MSG.amount()), AON.CSS.aonDisplayGridHeaderCell())
			.addCell(new Label(AON.MSG.unit()), AON.CSS.aonDisplayGridHeaderCell())
			.addCell(new Label(AON.MSG.factor()), AON.CSS.aonDisplayGridHeaderCell())
			.addCell(new Label(AON.MSG.result()), AON.CSS.aonDisplayGridHeaderCell());

		factor0.setEnabled(false);
		result0.setEnabled(false);
		factor1.setEnabled(false);
		result1.setEnabled(false);
		factor2.setEnabled(false);
		result2.setEnabled(false);
		factor3.setEnabled(false);
		result3.setEnabled(false);
		factor4.setEnabled(false);
		result4.setEnabled(false);
		factor5.setEnabled(false);
		result5.setEnabled(false);
		factor6.setEnabled(false);
		result6.setEnabled(false);
		rnp0.setEnabled(false);
		
		value0.addValueChangeHandler(event -> onFieldChange(callback));
		value1.addValueChangeHandler(event -> onFieldChange(callback));
		value2.addValueChangeHandler(event -> onFieldChange(callback));
		value3.addValueChangeHandler(event -> onFieldChange(callback));
		value4.addValueChangeHandler(event -> onFieldChange(callback));
		value5.addValueChangeHandler(event -> onFieldChange(callback));
		value6.addValueChangeHandler(event -> onFieldChange(callback));
		
		table.addRow()
			.addCell(description0)
			.addCell(value0)
			.addCell(unit0)
			.addCell(factor0)
			.addCell(result0);

		table.addRow()
			.addCell(description1)
			.addCell(value1)
			.addCell(unit1)
			.addCell(factor1)
			.addCell(result1);
		
		table.addRow()
			.addCell(description2)
			.addCell(value2)
			.addCell(unit2)
			.addCell(factor2)
			.addCell(result2);
		
		table.addRow()
			.addCell(description3)
			.addCell(value3)
			.addCell(unit3)
			.addCell(factor3)
			.addCell(result3);

		table.addRow()
			.addCell(description4)
			.addCell(value4)
			.addCell(unit4)
			.addCell(factor4)
			.addCell(result4);

		table.addRow()
			.addCell(description5)
			.addCell(value5)
			.addCell(unit5)
			.addCell(factor5)
			.addCell(result5);


		table.addRow()
			.addCell(description6)
			.addCell(value6)
			.addCell(unit6)
			.addCell(factor6)
			.addCell(result6);
		
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityRnp()), AON.CSS.aonTableLabel())
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label())
			.addCell(rnp0, AON.CSS.aonBold());

		return table;
	}
	
	private Widget getResultPanel(IMod131ActivityCallback callback) {
		AonDisplayTable table = new AonDisplayTable();
		table.addStyleName(AON.CSS.aonWidthAlmostAll());
		table.addStyleName(AON.CSS.aonBlockCenter());
		table.addStyleName(AON.CSS.aonMarginBottom());

		rnp.setEnabled(false);
		iem.setEnabled(false);
		rnm.setEnabled(false);
		ic1.setEnabled(false);
		ic2.setEnabled(false);
		ic3.setEnabled(false);
		ic4.setEnabled(false);
		ic5.setEnabled(false);
		rpf.setEnabled(false);
		rlo.setEnabled(false);
		rdr.setEnabled(false);
		net.setEnabled(false);
		por.setEnabled(false);
		res.setEnabled(false);
		
		iin.addValueChangeHandler(event -> onFieldChange(callback));
		dia.addValueChangeHandler(event -> onFieldChange(callback));
		
		table
			.addLabelWidgetRow(AON.MSG.irpfActivityRnp(), rnp)
			.addLabelWidgetRow(AON.MSG.irpfActivityIem(), iem)
			.addLabelWidgetRow(AON.MSG.irpfActivityIin(), iin)
			.addLabelWidgetRow(AON.MSG.irpfActivityRnm(), rnm)
			.addLabelWidgetRow(AON.MSG.irpfActivityIc1(), ic1)
			.addLabelWidgetRow(AON.MSG.irpfActivityIc2(), ic2)
			.addLabelWidgetRow(AON.MSG.irpfActivityIc3(), ic3)
			.addLabelWidgetRow(AON.MSG.irpfActivityIc4(), ic4)
			.addLabelWidgetRow(AON.MSG.irpfActivityIc5(), ic5)
			.addLabelWidgetRow(AON.MSG.irpfActivityRpf(), rpf)
			.addLabelWidgetRow(AON.MSG.irpfActivityRlo(), rlo)
			.addLabelWidgetRow(AON.MSG.irpfActivityRdr(), rdr)
			.addLabelWidgetRow(AON.MSG.irpfActivityDia(), dia)
			.addLabelWidgetRow(AON.MSG.irpfActivityNet(), net)
			.addLabelWidgetRow(AON.MSG.irpfActivityPor(), por)
			.addLabelWidgetRow(AON.MSG.irpfActivityRes(), res)
		;
		
		return table;
	}
	

	private void accept(IMod131ActivityCallback callback, final IEpigraph selected) {
		Model131Activity2023.this.tabLayoutPanel.setVisible(true);
		callback.getActivity().initialize();
		callback.getActivity().setEpi(selected);
		callback.getActivity().setEpigraph(selected.getEpigraph());
		callback.getActivity().setDescription(selected.getDescription());
		callback.getActivity().setMaxImport(selected.getLimExceso());
		for (Module mod : selected.getIRPFModules()) {
			Mod131ActivityModule m = new Mod131ActivityModule();
			m.setDescription(mod.getKey().getDescription());
			m.setValue(0.0);
			m.setUnit(mod.getUnit());
			m.setFactor(mod.getAmount());
			m.setResult(0.0);
			m.setSalariedStaff(mod.isSalariedStaff());
			m.setNoSalariedStaff(mod.isNoSalariedStaff());
			callback.getActivity().getModules().add(m);
		}
		calculate(callback);
	}
	
	
	private void populateActivity(IMod131ActivityCallback callback) {
		Mod131Activity act = callback.getActivity();
		epigraph.setText(act.getEpigraph());
		epigraphLabel.setText(AonStringUtils.abbreviate(act.getDescription(),100));
		epigraphLabel.setTitle(act.getDescription());
		
		Model131Activity2023.this.tabLayoutPanel.setVisible(AonStringUtils.isNotBlank( act.getEpigraph()) );
		
		dis.setValue(act.isDis());
		com.setValue(act.getCom());
		tem.setValue(act.getTem());
		nue.setValue(act.getNue());
		ceu.setValue(act.isCeu());
		loc.setValue(act.isLoc());
		veh.setValue(act.getVeh());
		cap.setValue(act.isCap());
		tns.setValue(act.isTns());
		tss.setValue(act.isTss());
		
		mun.setSelectedIndex(act.getMun());
		emp.setValue(act.getEmp());
		lor.setSelectedIndex(act.getLor());
		bat.setSelectedIndex(act.getBat());
		prc.setValue(act.getPrc());
		int i = 0;
		Mod131ActivityModule mod = (act.getModules().size() > i)?act.getModules().get(i):new Mod131ActivityModule();
		i++;
		description0.setText(mod.getDescription());
		value0.setValue(mod.getValue());
		unit0.setText(mod.getUnit());
		factor0.setValue(mod.getFactor());
		result0.setValue(mod.getResult());

		mod = (act.getModules().size() > i)?act.getModules().get(i):new Mod131ActivityModule();
		i++;
		description1.setText(mod.getDescription());
		value1.setValue(mod.getValue());
		unit1.setText(mod.getUnit());
		factor1.setValue(mod.getFactor());
		result1.setValue(mod.getResult());
		
		mod = (act.getModules().size() > i)?act.getModules().get(i):new Mod131ActivityModule();
		i++;
		description2.setText(mod.getDescription());
		value2.setValue(mod.getValue());
		unit2.setText(mod.getUnit());
		factor2.setValue(mod.getFactor());
		result2.setValue(mod.getResult());		
		
		mod = (act.getModules().size() > i)?act.getModules().get(i):new Mod131ActivityModule();
		i++;
		description3.setText(mod.getDescription());
		value3.setValue(mod.getValue());
		unit3.setText(mod.getUnit());
		factor3.setValue(mod.getFactor());
		result3.setValue(mod.getResult());
		
		mod = (act.getModules().size() > i)?act.getModules().get(i):new Mod131ActivityModule();
		i++;
		description4.setText(mod.getDescription());
		value4.setValue(mod.getValue());
		unit4.setText(mod.getUnit());
		factor4.setValue(mod.getFactor());
		result4.setValue(mod.getResult());		
		
		mod = (act.getModules().size() > i)?act.getModules().get(i):new Mod131ActivityModule();
		i++;
		description5.setText(mod.getDescription());
		value5.setValue(mod.getValue());
		unit5.setText(mod.getUnit());
		factor5.setValue(mod.getFactor());
		result5.setValue(mod.getResult());		

		mod = (act.getModules().size() > i)?act.getModules().get(i):new Mod131ActivityModule();
		
		description6.setText(mod.getDescription());
		value6.setValue(mod.getValue());
		unit6.setText(mod.getUnit());
		factor6.setValue(mod.getFactor());
		result6.setValue(mod.getResult());
		
		rnp0.setValue(act.getRnp());
		rnp.setValue(act.getRnp());
		iem.setValue(act.getIem());
		iin.setValue(act.getIin());
		rnm.setValue(act.getRnm());
		ic1.setValue(act.getIc1());
		ic2.setValue(act.getIc2());
		ic3.setValue(act.getIc3());
		ic4.setValue(act.getIc4());
		ic5.setValue(act.getIc5());
		rpf.setValue(act.getRpf());
		rlo.setValue(act.getRlo());
		rdr.setValue(act.getRdr());
		dia.setValue(act.getDia());
		net.setValue(act.getNet());
		por.setValue(act.getPor());
		res.setValue(act.getRes());
		
		enableFields(callback);
	}
	
	private void onFieldChange( IMod131ActivityCallback callback ) {
		com.setValue(AonNumberUtils.zeroIfNull(com.getValue()),false);
		tem.setValue(AonNumberUtils.zeroIfNull(tem.getValue()),false);
		if (tem.getValue() < 0) tem.setValue(0,false);
		if (tem.getValue() > 180) tem.setValue(180,false);
		nue.setValue(AonNumberUtils.zeroIfNull(nue.getValue()),false);
		veh.setValue(AonNumberUtils.zeroIfNull(veh.getValue()),false);
		emp.setValue(AonNumberUtils.zeroIfNull(emp.getValue()),false);
		prc.setValue(AonNumberUtils.zeroIfNull(prc.getValue()),false);
		value0.setValue(AonNumberUtils.zeroIfNull(value0.getValue()),false);
		value1.setValue(AonNumberUtils.zeroIfNull(value1.getValue()),false);
		value2.setValue(AonNumberUtils.zeroIfNull(value2.getValue()),false);
		value3.setValue(AonNumberUtils.zeroIfNull(value3.getValue()),false);
		value4.setValue(AonNumberUtils.zeroIfNull(value4.getValue()),false);
		value5.setValue(AonNumberUtils.zeroIfNull(value5.getValue()),false);
		value6.setValue(AonNumberUtils.zeroIfNull(value6.getValue()),false);
		iin.setValue(AonNumberUtils.zeroIfNull(iin.getValue()),false);
		dia.setValue(AonNumberUtils.zeroIfNull(dia.getValue()),false);
		callback.getActivity().setCom(com.getValue());
		callback.getActivity().setTem(tem.getValue());
		callback.getActivity().setNue(nue.getValue());
		callback.getActivity().setVeh(veh.getValue());
		callback.getActivity().setEmp(emp.getValue());
		callback.getActivity().setMun(mun.getSelectedIndex());
		callback.getActivity().setLor(lor.getSelectedIndex());
		callback.getActivity().setBat(bat.getSelectedIndex());
		callback.getActivity().setPrc(prc.getValue());
		callback.getActivity().setDis(dis.getValue());
		callback.getActivity().setCeu(ceu.getValue());
		callback.getActivity().setLoc(loc.getValue());
		callback.getActivity().setCap(cap.getValue());
		callback.getActivity().setTns(tns.getValue());
		callback.getActivity().setTss(tss.getValue());
		if  (!callback.getActivity().getModules().isEmpty())
			callback.getActivity().getModules().get(0).setValue(value0.getValue());
		if  (callback.getActivity().getModules().size() > 1)
			callback.getActivity().getModules().get(1).setValue(value1.getValue());
		if  (callback.getActivity().getModules().size() > 2)
			callback.getActivity().getModules().get(2).setValue(value2.getValue());
		if  (callback.getActivity().getModules().size() > 3)
			callback.getActivity().getModules().get(3).setValue(value3.getValue());
		if  (callback.getActivity().getModules().size() > 4)
			callback.getActivity().getModules().get(4).setValue(value4.getValue());
		if  (callback.getActivity().getModules().size() > 5)
			callback.getActivity().getModules().get(5).setValue(value5.getValue());
		if  (callback.getActivity().getModules().size() > 6)
			callback.getActivity().getModules().get(6).setValue(value6.getValue());
		callback.getActivity().setIin(iin.getValue());
		callback.getActivity().setDia(dia.getValue());
		calculate( callback );
		ValueChangeEvent.<Mod131Activity>fire(Model131Activity2023.this, callback.getActivity());
	}

	private void enableFields(IMod131ActivityCallback callback) {
		if (callback.getActivity().getEpi() == Epigraph.E____) {
			bat.setEnabled(true);
			loc.setEnabled(false);
			veh.setEnabled(false);
			cap.setEnabled(false);
			tns.setEnabled(false);
			tss.setEnabled(false);
			mun.setEnabled(false);
		} else if (callback.getActivity().getEpi() == Epigraph.E_722A 
			|| callback.getActivity().getEpi() == Epigraph.E_722B
			|| callback.getActivity().getEpi() == Epigraph.E_757
			) {
			bat.setEnabled(false);
			tss.setEnabled(true);
			tns.setEnabled(true);
		} else {
			bat.setEnabled(false);
			tss.setEnabled(false);
			tns.setEnabled(false);
		}
		cap.setEnabled( callback.getActivity().getVeh() > 0 );
	}
	
	private void calculate(IMod131ActivityCallback callback) {
		Model131.SERVICE.calculateActivity( 
			callback.getOptions().getOccam(), 
			callback.getModel(), callback.getActivity(), new AsyncCallback<Mod131Activity>() {
				
				@Override
				public void onSuccess(Mod131Activity result) {
					for (int i = 0 ; i < callback.getActivity().getModules().size(); i++) {
						callback.getActivity().getModules().get(i).setResult(result.getModules().get(i).getResult());	
					}
					callback.getActivity().setPrc(result.getPrc());
					callback.getActivity().setRnp(result.getRnp());
					callback.getActivity().setIem(result.getIem());
					callback.getActivity().setRnm(result.getRnm());
					callback.getActivity().setIc1(result.getIc1());
					callback.getActivity().setIc2(result.getIc2());
					callback.getActivity().setIc3(result.getIc3());
					callback.getActivity().setIc4(result.getIc4());
					callback.getActivity().setIc5(result.getIc5());
					callback.getActivity().setRpf(result.getRpf());
					callback.getActivity().setRlo(result.getRlo());
					callback.getActivity().setRdr(result.getRdr());
					callback.getActivity().setNet(result.getNet());
					callback.getActivity().setPor(result.getPor());
					callback.getActivity().setRes(result.getRes());
					populateActivity(callback);						
				}
				
				@Override
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
				}
			});
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Mod131Activity> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

}

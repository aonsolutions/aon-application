package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.TextCell;
import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.HasId;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.LongBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class CretaRequestDialog<T extends HasId<?>> extends SelectDialog<T> {
	
	
	public abstract static class CretaCCCRequestDialog extends CretaRequestDialog<CCC> {

		public CretaCCCRequestDialog(Callback callback) {
			super(callback);
			// Full CCC.
			Column<CCC, String> fullNameColumn = new Column<CCC, String>(
					new TextCell()) {
				@Override
				public String getValue(CCC ccc) {
					return getDescription(ccc);
				}
			};

			addColumn(fullNameColumn, "C\u00F3digo de Cuenta de Cotizaci\u00F3n");
		}
		
		protected void checkSelected() throws Exception{
			if ( getSelectedData().isEmpty() )
				throw new Exception("Debe seleccionar al menos un C\u00F3digo de Cuenta de Cotizaci\u00F3n (CCC).");
		}
		
		@Override
		public String getDescription(CCC ccc) {
			return ccc.getCode();
		}

	}
	
	public abstract static class CretaEmployeeRequestDialog extends CretaRequestDialog<Employee> {

		public CretaEmployeeRequestDialog(Callback callback) {
			super(callback);
			// Full Employee.
			Column<Employee, String> fullNameColumn = new Column<Employee, String>(
					new TextCell()) {
				@Override
				public String getValue(Employee employee) {
					return employee.getFullname();
				}
			};

			addColumn(fullNameColumn, "Trabajador");
		}
		
		protected void checkSelected() throws Exception{
			if ( getSelectedData().isEmpty() )
				throw new Exception("Debe seleccionar al menos un Trabajador.");
		}
		
		
		@Override
		public String getDescription(Employee employee) {
			return employee.getFullname() + " " + employee.getSocialSecurity() + "(" + employee.getDocument() + ")";
		}
	}

	private static enum Type {
		L00 {
			@Override
			<T> T visit(TypeVisitor<T> v) {
				return v.visitL00();
			}

		},
		L02{
			@Override
			<T> T visit(TypeVisitor<T> v) {
				return v.visitL02();
			}
		},
		L03{
			@Override
			<T> T visit(TypeVisitor<T> v) {
				return v.visitL03();
			}
		},
		L13{
			@Override
			<T> T visit(TypeVisitor<T> v) {
				return v.visitL13();
			}
		},
		L90{
			@Override
			<T> T visit(TypeVisitor<T> v) {
				return v.visitL90();
			}
		},
		L91{
			@Override
			<T> T visit(TypeVisitor<T> v) {
				return v.visitL91();
			}
		};
		
				
		abstract <T> T visit (TypeVisitor<T> v);
	}

	private static interface TypeVisitor<T> {
		T visitL00();
		T visitL02();
		T visitL03();
		T visitL13();
		T visitL90();
		T visitL91();
	}

	public static interface Callback<T extends HasId<?>> {
		boolean onAccept(CretaRequestDialog<T> dialog);
	}

	interface Binder extends UiBinder<Widget, CretaRequestDialog<?>>{
		
	}
	private static final Binder binder = GWT.create(Binder.class);
	
	
	
	@UiField
	LongBox authLongBox;

	@UiField
	Element monthTR;

	@UiField
	Element withIDCTR;

	@UiField
	MonthListBox monthListBox;

	@UiField
	Element toMonthTR;

	@UiField
	MonthListBox toMonthListBox;
	
	@UiField
	Element fromMonthTR;

	@UiField
	MonthListBox fromMonthListBox;
	
	@UiField
	Element ctrlMonthTR;

	@UiField
	MonthListBox ctrlMonthListBox;

	@UiField
	Anchor downloadAnchor;
	
	@UiField
	Label selectLabel;
	
	@UiField
	Label messagesLabel;

	@UiField
	Element previousBasesTR;

	@UiField
	Element calcsDetailedTR;

	@UiField
	Element reftificationMarkTR;

	@UiField
	Element solicitudRecepcionRNTTR;

	@UiField
	Element aceptarBasesAnterioresTR;

	@UiField
	ListBox typeListBox;
	
	@UiField
	CheckBox withIDCCheckBox;

	@UiField
	CheckBox previousBasesCheckBox;

	@UiField
	CheckBox calcsDetailedCheckBox;

	@UiField
	CheckBox reftificationMarkCheckBox;

	@UiField
	CheckBox solicitudRecepcionRNTCheckBox;

	@UiField
	CheckBox aceptarBasesAnterioresCheckBox;

	@UiField
	Element i54TR;

	@UiField
	ListBox i54ListBox;
	
	@UiField
	Element outOfDateTR;
	
	@UiField
	LongBox outOfDateLiquidationLongBox;
	

	private Callback<T> callback;
	
	public CretaRequestDialog(Callback<T> callback) {
		
		this.callback = callback;
		
		setCaption("Sistema de Liquidaci\u00F3n Directa (Proyecto Cret@)");
		
		setWidget(binder.createAndBindUi(this));

		typeListBox.addItem(PayrollAON.MSGS.liquidationL00(), "L00");
		typeListBox.addItem(PayrollAON.MSGS.liquidationL02(), "L02");
		typeListBox.addItem(PayrollAON.MSGS.liquidationL03(), "L03");
		typeListBox.addItem(PayrollAON.MSGS.liquidationL13(), "L13");
		typeListBox.addItem(PayrollAON.MSGS.liquidationL90(), "L90");
		typeListBox.addItem(PayrollAON.MSGS.liquidationL91(), "L91");

		i54ListBox.addItem(PayrollAON.MSGS.causeI541(), "1");
		i54ListBox.addItem(PayrollAON.MSGS.causeI542(), "2");
		i54ListBox.addItem(PayrollAON.MSGS.causeI543(), "3");
		i54ListBox.addItem(PayrollAON.MSGS.causeI544(), "4");
		i54ListBox.addItem(PayrollAON.MSGS.causeI545(), "5");

		setVisibleI54(false);

		setVisibleWithIDC(false);

		setVisiblePreviousBases(false);
		
		setVisibleCalcsDetailed(false);
		
		setVisibleReftificationMark(false);
		
		setVisibleSolicitudRecepcionRNT(false);

		Date prevMonth = DateUtils.addMonths2Date(DateUtils.getFirstDayOfMonth(), -1); 
		monthListBox.setLastMonth(prevMonth);
		monthListBox.setSelectedMonth(prevMonth);
		
		toMonthListBox.setLastMonth(prevMonth);
		toMonthListBox.setSelectedMonth(prevMonth);

		fromMonthListBox.setLastMonth(prevMonth);
		fromMonthListBox.setSelectedMonth(prevMonth);
		
		ctrlMonthListBox.setLastMonth(prevMonth);
		ctrlMonthListBox.setSelectedMonth(prevMonth);

		typeListBox.setSelectedIndex(0);//L00
		setVisibleTo(true);
		setVisibleFrom(true);
		setVisibleCtrl(false);
		setVisibleMonth(false);
		setVisibleOutOfDate(false);
		
		acceptButton.setEnabled(enableAccept());
	}
	
	
	// ------------------------------------------------------------------------
	
	@UiHandler("acceptButton")
	void  onAcceptClicked(ClickEvent e){
		if ( callback.onAccept(this) )
			hide();
	}
	
	@UiHandler("acceptButton")
	void  onCancelClicked(ClickEvent e){
		hide();
	}
	
	@UiHandler("authLongBox")
	void onAuthChanged(ValueChangeEvent<Long> e) {
		acceptButton.setEnabled(enableAccept());
	}
	
	@UiHandler("authLongBox")
	void onAuthChanged(KeyUpEvent e ){
		acceptButton.setEnabled(enableAccept());
	}
	
	@UiHandler("typeListBox")
	void onTypeChanged( ChangeEvent e ){
		Type.valueOf(typeListBox.getSelectedValue())
		.visit( new TypeVisitor<Void>() {
			@Override
			public Void visitL00() {
				setVisibleTo(true);
				setVisibleFrom(true);
				setVisibleCtrl(false);
				setVisibleMonth(false);
				onMonthChanged(null);
				return null;
			}

			@Override
			public Void visitL02() {
				setVisibleTo(false);
				setVisibleFrom(false);
				setVisibleCtrl(false);
				setVisibleMonth(true);
				onMonthsChanged(null);
				return null;
			}

			@Override
			public Void visitL03() {
				setVisibleTo(true);
				setVisibleFrom(true);
				setVisibleCtrl(true);
				setVisibleMonth(false);
				onMonthsChanged(null);
				return null;
			}

			@Override
			public Void visitL13() {
				return visitL00();
			}

			@Override
			public Void visitL90() {
				return visitL00();
			}

			@Override
			public Void visitL91() {
				return visitL00();
			}

		});
		
	}
	
	@UiHandler("monthListBox")
	abstract void onMonthChanged( ChangeEvent e );
	
	@UiHandler({"fromMonthListBox", "toMonthListBox"})
	abstract void onMonthsChanged( ChangeEvent e );
	

	// ------------------------------------------------------------------------
	
	public String getType(){
		return typeListBox.getSelectedValue();
	}

	public Date getToMonth(){
		return toMonthListBox.getSelected();
	}
	
	public Date getFromMonth(){
		return fromMonthListBox.getSelected();
	}

	public Date getCtrlMonth(){
		return 
		Type.valueOf(typeListBox.getSelectedValue())
		.visit(new TypeVisitor<Date>() {

			@Override
			public Date visitL00() {
				return toMonthListBox.getSelected();
			}
			
			@Override
			public Date visitL02() {
				return monthListBox.getSelected();
			}

			@Override
			public Date visitL03() {
				return ctrlMonthListBox.getSelected();
			}

			@Override
			public Date visitL13() {
				return toMonthListBox.getSelected();
			}

			@Override
			public Date visitL90() {
				return toMonthListBox.getSelected();
			}

			@Override
			public Date visitL91() {
				return toMonthListBox.getSelected();
			}
		});
	}

	public Long getAuthorized() {
		return authLongBox.getValue();
	}
	
	public void setAuthorized(Long authorized){
		authLongBox.setValue(authorized, true);
	}
	
	public void setAuthorized(String authorized){
		authLongBox.setText(authorized);
		onAuthChanged((ValueChangeEvent<Long>)null);
	}

	public boolean withIDC(){
		return withIDCCheckBox.getValue();
	}

	public boolean previousBases(){
		return previousBasesCheckBox.getValue();
	}
	
	public boolean calcsDetailed(){
		return calcsDetailedCheckBox.getValue();
	}

	public boolean reftificationMark(){
		return reftificationMarkCheckBox.getValue();
	}
	
	public boolean solicitudRecepcionRNT(){
		return solicitudRecepcionRNTCheckBox.getValue();
	}


	public String getI54(){
		return i54ListBox.getSelectedValue();
	}

	public Long getOutOfDateLiquidation() {
		return outOfDateLiquidationLongBox.getValue();
	}
	
	public void setOutOfDateLiquidation(Long outOfDateLiquidation){
		outOfDateLiquidationLongBox.setValue(outOfDateLiquidation, true);
	}
	

	public abstract String getDescription(T t);
	
	// ------------------------------------------------------------------------
	
	public void download(String fileName,String url){
		downloadAnchor.setHref(url);
		downloadAnchor.setTarget(fileName);
		downloadAnchor.getElement().setAttribute("download", fileName);
		click(downloadAnchor.getElement());
		
	}
	// ------------------------------------------------------------------------
	
	protected void setVisibleWithIDC(boolean visible){
		setVisible(visible, withIDCTR);
	}

	protected void setVisibleMonth(boolean visible){
		setVisible(visible, monthTR);
	}

	protected void setVisibleTo(boolean visible){
		setVisible(visible, toMonthTR);
	}

	protected void setVisibleFrom(boolean visible){
		setVisible(visible, fromMonthTR);
	}

	protected void setVisibleCtrl(boolean visible){
		setVisible(visible, ctrlMonthTR);
	}


	protected void setVisibleOutOfDate(boolean visible){
		setVisible(visible, outOfDateTR);
	}

	protected void setVisiblePreviousBases(boolean visible){
		setVisible(visible, previousBasesTR);
	}
	
	protected void setVisibleCalcsDetailed(boolean visible){
		setVisible(visible, calcsDetailedTR);
	}
	
	protected void setVisibleReftificationMark(boolean visible){
		setVisible(visible, reftificationMarkTR);
	}

	protected void setVisibleSolicitudRecepcionRNT(boolean visible){
		setVisible(visible, solicitudRecepcionRNTTR);
	}

	protected void setVisible(boolean visible, Element el){
		if ( visible )
			el.getStyle().clearDisplay();
		else
			el.getStyle().setDisplay(Display.NONE);
		
	}

	protected void setVisibleI54(boolean visible){
		setVisible(visible, i54TR);
	}
	// ------------------------------------------------------------------------
	
	@Override
	protected boolean enableAccept() {
		try {
			checkAuth();
			checkSelected();
			hide(messagesLabel, true);
			return true;
		} catch ( Exception e ){
			hide(messagesLabel, false);
			messagesLabel.setText(e.getMessage());
			return false;
		}
		
	}
	
	protected abstract void checkSelected() throws Exception;
	// ------------------------------------------------------------------------
	
	

	private void checkAuth() throws Exception{
		if ( authLongBox.getValue() == null )
			throw new Exception("Autorizado no v\u00E1lido. Debe ser un n\u00FAmero que contenga 8 d\u00EDgitos o menos.");
			
	}
	// ------------------------------------------------------------------------
	
	private static void hide(Widget widget, boolean hide){
		if ( hide )
			widget.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		else 
			widget.getElement().getStyle().clearVisibility();
	}
	

	private static native void click(Element a)/*-{
		a.click();
	}-*/;
	
}

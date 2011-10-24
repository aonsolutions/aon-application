package com.esferalia.aon.ui.payroll.controller.contract;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aeat.jaxb.TipoRetenedorError2011;
import com.aeat.jaxb.TipoRetenedorSalida2011;
import com.aeat.jaxb.TipoRetenidoEntrada2011.Reducciones;
import com.aeat.jaxb.TipoRetenidoEntrada2011.Regularizacion;
import com.aeat.jaxb.TipoRetenidoEntrada2011.Regularizacion.MinoracionPrestamosVivienda;
import com.aeat.jaxb.TipoRetenidoError2011;
import com.aeat.jaxb.TipoRetenidoSalida2011;
import com.aeat.jaxb.TipoRetenidoSalida2011.Ascendientes;
import com.aeat.jaxb.TipoRetenidoSalida2011.Ascendientes.Mayores75;
import com.aeat.jaxb.TipoRetenidoSalida2011.Ascendientes.Menores75;
import com.aeat.jaxb.TipoRetenidoSalida2011.Descendientes;
import com.aeat.jaxb.TipoRetenidoSalida2011.Descendientes.ComputoDescendientes;
import com.aeat.jaxb.TipoRetenidoSalida2011.Descendientes.ComputoDescendientes.CuartoySucesivos;
import com.aeat.jaxb.TipoRetenidoSalida2011.Descendientes.ConDiscapacidad;
import com.aeat.jaxb.TipoRetenidoSalida2011.Descendientes.ConDiscapacidad.EnGrado1;
import com.aeat.jaxb.TipoRetenidoSalida2011.Descendientes.ConDiscapacidad.EnGrado1.ConMovilidadReducida;
import com.aeat.jaxb.TipoRetenidoSalida2011.Descendientes.ConDiscapacidad.EnGrado2;
import com.aeat.jaxb.TipoRetenidoSalida2011.Descendientes.Menores3Años;
import com.aeat.jaxb.TipoRetenidoSalida2011.Descendientes.Resto;
import com.aeat.jaxb.TipoRetenidoSalida2011.MinimoPersonalFamiliar;
import com.aeat.jaxb.TipoRetenidoSalida2011.MinimoPersonalFamiliar.MinimoAscendientes;
import com.aeat.jaxb.TipoRetenidoSalida2011.MinimoPersonalFamiliar.MinimoCtye;
import com.aeat.jaxb.TipoRetenidoSalida2011.MinimoPersonalFamiliar.MinimoDescendientes;
import com.aeat.jaxb.TipoRetenidoSalida2011.MinimoPersonalFamiliar.MinimoDiscapacidad;
import com.aeat.jaxb.TipoRetenidoSalida2011.Reduccion;
import com.aeat.jaxb.TipoRetenidoSalida2011.Reduccion.RdtosTrabajo;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.IrpfResult;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.IrpfRegularizationReason;
import com.esferalia.aon.payroll.irpf.IrpfCalculator.CallbackHandler;
import com.esferalia.aon.payroll.irpf.sql.DefaultEntrada2011Handler;
import com.esferalia.aon.payroll.irpf.sql.SQLAEAT2011Factory;
import com.esferalia.aon.payroll.irpf.sql.SQLAEAT2011Factory.Entrada2011Handler;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;

public class IrpfDraftController extends BasicController implements IIrpfController {

	private static final Logger LOGGER = LoggerFactory.getLogger(IrpfDraftController.class.getName());

	private Date date = 
		Calendar.getInstance().getTime();
	private Integer otherIndex;
	
	private List<IIrpfController>		others ;
	private TipoRetenidoSalida2011 		retenidoSalida2011;
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = new Date( DateUtils.truncate(date, Calendar.DATE).getTime());
	}
	
	
	public Integer getOtherIndex() {
		return otherIndex;
	}
	
	public void setOtherIndex(Integer otherIndex) {
		this.otherIndex = otherIndex;
	}
	
	public List<SelectItem> getOthers() {
		String pattern = 
			AonUtil.getMessage("bundle", "aon_date_pattern");
		
		List<SelectItem> items = 
			new LinkedList<SelectItem>();
		
		for (int i = 0; i < others.size(); i++) {
			IIrpfController other = others.get(i);
			Date effectiveDate = other.getEffectiveDate();
			items.add(new SelectItem(i, DateFormatUtils.format(effectiveDate, pattern)));
		}
		
		return items;
	}
	
	@Override
	public void select(ActionEvent event) {
		super.select(event);
		reset();
	}
	
	
	public void onReloadDraft(ActionEvent event) {
		try {
			calculateIrpf();
		} catch (ExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SalaryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void reset() {
		try {
			calculateIrpf();
		} catch (ExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SalaryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			loadOthers();
			resetOther();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public IIrpfController getOther(){
		try {
			return otherIndex == null ? null : others.get(otherIndex);
		} catch ( IndexOutOfBoundsException e ) {
			return null;
		}
	}

	// ------------------------------------------
	// IIrpfController
	// ------------------------------------------
	
	@Override
	public Integer getBirthYear() {
		return retenidoSalida2011.getAñoNacimiento();
	}

	@Override
	public String getDocument() {
		return retenidoSalida2011.getNif();
	}

	@Override
	public Date getEffectiveDate() {
		return date;
	}

	@Override
	public Integer getEffectiveYear() {
		return CommonUtil.getYear(date);
	}

	@Override
	public Double getBaseIrpf() {
		return toDouble(retenidoSalida2011.getBaseRetencion());
	}

	@Override
	public Double getMinimunPersonalFamily() {
		MinimoPersonalFamiliar minimoPersonalFamiliar = 
			retenidoSalida2011.getMinimoPersonalFamiliar();
		return minimoPersonalFamiliar == null ?
				0.00 : 
				toDouble(minimoPersonalFamiliar.getTotal());
	}

	@Override
	public Double getDeductHomeLoanAmount() {
		return toDouble(retenidoSalida2011.getMinoracionPrestamo());
	}

	@Override
	public Double getDeduct80Bis() {
		return toDouble(retenidoSalida2011.getDeduccion80Bis());
	}

	@Override
	public Double getIrpf() {
		return toDouble(retenidoSalida2011.getTipoRetencion());
	}

	@Override
	public Double getAnnualIrpf() {
		return toDouble(retenidoSalida2011.getImpAnualRetencionesIngresosCuenta());
	}

	@Override
	public Double getAnnualRemuneration() {
		return toDouble(retenidoSalida2011.getRetribAnuales());
	}

	@Override
	public Double getIrregular18_2Reduction() {
		Reducciones reducciones = 
			retenidoSalida2011.getReducciones();
		return reducciones == null ? 0.00 : toDouble(reducciones.getIrregularidad1());
	}

	@Override
	public Double getIrregular18_3Reduction() {
		Reducciones reducciones = 
			retenidoSalida2011.getReducciones();
		return reducciones == null ? 0.00 : toDouble(reducciones.getIrregularidad2());
	}

	@Override
	public Double getDeducciblesExpenses() {
		return toDouble(retenidoSalida2011.getGastosAnuales());
	}

	@Override
	public Double getWorkRemunerationReduction() {
		Reduccion reduccion = 
			retenidoSalida2011.getReduccion();
		if ( reduccion == null ) {
			return 0.00;
		}
		RdtosTrabajo rdtosTrabajo = 
			reduccion.getRdtosTrabajo();
		return reduccion == null ? 0.00 : toDouble(rdtosTrabajo.getGeneral());
	}

	@Override
	public Double getWorkProlongationReduction() {
		Reduccion reduccion = 
			retenidoSalida2011.getReduccion();
		if ( reduccion == null ) {
			return 0.00;
		}
		RdtosTrabajo rdtosTrabajo = 
			reduccion.getRdtosTrabajo();
		return reduccion == null ? 0.00 : toDouble(rdtosTrabajo.getProlongacionActividadLaboral());
	}

	@Override
	public Double getWorkMovingReduction() {
		Reduccion reduccion = 
			retenidoSalida2011.getReduccion();
		if ( reduccion == null ) {
			return 0.00;
		}
		RdtosTrabajo rdtosTrabajo = 
			reduccion.getRdtosTrabajo();
		return reduccion == null ? 0.00 : toDouble(rdtosTrabajo.getMovilidadGeografica());
	}

	@Override
	public Double getWorkDisabilityReduction() {
		Reduccion reduccion = 
			retenidoSalida2011.getReduccion();
		if ( reduccion == null ) {
			return 0.00;
		}
		RdtosTrabajo rdtosTrabajo = 
			reduccion.getRdtosTrabajo();
		return reduccion == null ? 0.00 : toDouble(rdtosTrabajo.getDiscapacidadTrabajadoresActivos());
	}

	@Override
	public Double getSocialSecurityPensioner() {
		Reduccion reduccion = 
			retenidoSalida2011.getReduccion();
		return reduccion == null ? 0.00 : toDouble(reduccion.getPensionista());
	}

	@Override
	public Double getTwoOrMoreDescendentsMin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getSpousalSupport() {
		return toDouble(retenidoSalida2011.getPensionCompensatoria());
	}

	@Override
	public Double getFoodAnnuity() {
		return toDouble(retenidoSalida2011.getAnualidadesHijos());
	}

	@Override
	public Double getMinimunPersonal() {
		MinimoCtye minimoCtye = getMinimoCtye();
		return minimoCtye == null ? null : toDouble(minimoCtye.getTotal());
	}

	@Override
	public Double getMinimunAscendents() {
		MinimoAscendientes minimoAscendientes = getMinimoAscendientes();
		return minimoAscendientes == null ? null : toDouble(minimoAscendientes.getTotal());
	}

	@Override
	public Double getMinimunDescendents() {
		MinimoDescendientes minimoDescendientes = getMinimoDescendientes();
		return minimoDescendientes == null ? null : toDouble(minimoDescendientes.getTotal());
	}

	@Override
	public Double getMinimunDisability() {
		MinimoDiscapacidad minimoDiscapacidad = getMinimoDiscapacidad();
		return minimoDiscapacidad == null ? null : toDouble(minimoDiscapacidad.getTotal());
	}

	@Override
	public Integer getDescendentsMinor3Total() {
		Menores3Años menores3Años = getMenores3Años();
		return menores3Años == null ? 0 : toInteger(menores3Años.getTotal());
	}

	@Override
	public Integer getDescendentsMinor3Entirely() {
		Menores3Años menores3Años = getMenores3Años();
		return menores3Años == null ? 0 : toInteger(menores3Años.getPorEntero());
	}

	@Override
	public Integer getDescendentsRemainderTotal() {
		Resto resto = getResto();
		return resto == null ? 0 : toInteger(resto.getTotal());
	}

	@Override
	public Integer getDescendentsRemainderEntirely() {
		Resto resto = getResto();
		return resto == null ? 0 : toInteger(resto.getPorEntero());
	}

	@Override
	public Integer getDescendents33_65Total() {
		ConDiscapacidad conDiscapacidad = 
			getConDiscapacidad();
		if ( conDiscapacidad == null ) {
			return 0;
		}
		EnGrado1 enGrado1 = conDiscapacidad.getEnGrado1();
		return enGrado1 == null ? 0 : toInteger(enGrado1.getTotal());
	}

	@Override
	public Integer getDescendents33_65Entirely() {
		ConDiscapacidad conDiscapacidad = 
			getConDiscapacidad();
		if ( conDiscapacidad == null ) {
			return 0;
		}
		EnGrado1 enGrado1 = conDiscapacidad.getEnGrado1();
		return enGrado1 == null ? 0 : toInteger(enGrado1.getPorEntero());
	}

	@Override
	public Integer getDescendentsMovingTotal() {
		ConDiscapacidad conDiscapacidad = 
			getConDiscapacidad();
		if ( conDiscapacidad == null ) {
			return 0;
		}
		EnGrado1 enGrado1 = conDiscapacidad.getEnGrado1();
		if ( enGrado1 == null  ){
			return 0;
		}
		ConMovilidadReducida conMovilidadReducida = 
			enGrado1.getConMovilidadReducida();
		return conMovilidadReducida == null ? 0 : toInteger(conMovilidadReducida.getTotal());
	}

	@Override
	public Integer getDescendentsMovingEntirely() {
		ConDiscapacidad conDiscapacidad = 
			getConDiscapacidad();
		if ( conDiscapacidad == null ) {
			return 0;
		}
		EnGrado1 enGrado1 = conDiscapacidad.getEnGrado1();
		if ( enGrado1 == null  ){
			return 0;
		}
		ConMovilidadReducida conMovilidadReducida = 
			enGrado1.getConMovilidadReducida();
		return conMovilidadReducida == null ? 0 : toInteger(conMovilidadReducida.getPorEntero());
	}

	@Override
	public Integer getDescendents65Total() {
		ConDiscapacidad conDiscapacidad = 
			getConDiscapacidad();
		if ( conDiscapacidad == null ) {
			return 0;
		}
		EnGrado2 enGrado2 = conDiscapacidad.getEnGrado2();
		return enGrado2 == null ? 0 : toInteger(enGrado2.getTotal());
	}

	@Override
	public Integer getDescendents65Entirely() {
		ConDiscapacidad conDiscapacidad = 
			getConDiscapacidad();
		if ( conDiscapacidad == null ) {
			return 0;
		}
		EnGrado2 enGrado2 = conDiscapacidad.getEnGrado2();
		return enGrado2 == null ? 0 : toInteger(enGrado2.getPorEntero());
	}

	@Override
	public Integer getDescendentsFirst() {
		ComputoDescendientes computoDescendientes = 
			getComputoDescendientes();
		if ( computoDescendientes == null ) {
			return 0;
		}
		return computoDescendientes.getHijo1() == null ? 0 : 1;
	}

	@Override
	public Integer getDescendentsSecond() {
		ComputoDescendientes computoDescendientes = 
			getComputoDescendientes();
		if ( computoDescendientes == null ) {
			return 0;
		}
		return computoDescendientes.getHijo2() == null ? 0 : 1;
	}

	@Override
	public Integer getDescendentsThird() {
		ComputoDescendientes computoDescendientes = 
			getComputoDescendientes();
		if ( computoDescendientes == null ) {
			return 0;
		}
		return computoDescendientes.getHijo3() == null ? 0 : 1;
	}

	@Override
	public Integer getDescendentsFourthSubsequentTotal() {
		ComputoDescendientes computoDescendientes = 
			getComputoDescendientes();
		if ( computoDescendientes == null ) {
			return 0;
		}
		CuartoySucesivos cuartoySucesivos = 
			computoDescendientes.getCuartoySucesivos();
		return cuartoySucesivos == null ? 0 : toInteger(cuartoySucesivos.getTotal());
	}

	@Override
	public Integer getDescendentsFourthSubsequentEntirely() {
		ComputoDescendientes computoDescendientes = 
			getComputoDescendientes();
		if ( computoDescendientes == null ) {
			return 0;
		}
		CuartoySucesivos cuartoySucesivos = 
			computoDescendientes.getCuartoySucesivos();
		return cuartoySucesivos == null ? 0 : toInteger(cuartoySucesivos.getPorEntero());
	}

	@Override
	public Integer getAscendentsMinor75Total() {
		Menores75 menores75 = getMenores75();
		return menores75 == null ? 0 :toInteger(menores75.getTotal());
	}

	@Override
	public Integer getAscendentsMinor75Entirely() {
		Menores75 menores75 = getMenores75();
		return menores75 == null ? 0 :toInteger(menores75.getPorEntero());
	}

	@Override
	public Integer getAscendentsMayor75Total() {
		Mayores75 mayores75 = getMayores75();
		return mayores75 == null ? 0 :toInteger(mayores75.getTotal());
	}

	@Override
	public Integer getAscendentsMayor75Entirely() {
		Mayores75 mayores75 = getMayores75();
		return mayores75 == null ? 0 :toInteger(mayores75.getPorEntero());
	}

	@Override
	public Integer getAscendents33_65Total() {
		com.aeat.jaxb.TipoRetenidoSalida2011.Ascendientes.ConDiscapacidad conDiscapacidad = 
			getAscendientesConDiscapacidad();
		if ( conDiscapacidad == null ) {
			return 0;
		}
		com.aeat.jaxb.TipoRetenidoSalida2011.Ascendientes.ConDiscapacidad.EnGrado1  enGrado1 = 
			conDiscapacidad.getEnGrado1();
		return enGrado1 == null ? 0 : toInteger(enGrado1.getTotal());
	}

	@Override
	public Integer getAscendents33_65Entirely() {
		com.aeat.jaxb.TipoRetenidoSalida2011.Ascendientes.ConDiscapacidad conDiscapacidad = 
			getAscendientesConDiscapacidad();
		if ( conDiscapacidad == null ) {
			return 0;
		}
		com.aeat.jaxb.TipoRetenidoSalida2011.Ascendientes.ConDiscapacidad.EnGrado1  enGrado1 = 
			conDiscapacidad.getEnGrado1();
		return enGrado1 == null ? 0 : toInteger(enGrado1.getPorEntero());
	}

	@Override
	public Integer getAscendentsMovingTotal() {
		com.aeat.jaxb.TipoRetenidoSalida2011.Ascendientes.ConDiscapacidad conDiscapacidad = 
			getAscendientesConDiscapacidad();
		if ( conDiscapacidad == null ) {
			return 0;
		}
		com.aeat.jaxb.TipoRetenidoSalida2011.Ascendientes.ConDiscapacidad.EnGrado1  enGrado1 = 
			conDiscapacidad.getEnGrado1();
		if ( enGrado1 == null ) {
			return 0;
		}
		com.aeat.jaxb.TipoRetenidoSalida2011.Ascendientes.ConDiscapacidad.EnGrado1.ConMovilidadReducida conMovilidadReducida = 
			enGrado1.getConMovilidadReducida();
		return conMovilidadReducida == null ? 0 : toInteger(conMovilidadReducida.getTotal());
	}

	@Override
	public Integer getAscendentsMovingEntirely() {
		com.aeat.jaxb.TipoRetenidoSalida2011.Ascendientes.ConDiscapacidad conDiscapacidad = 
			getAscendientesConDiscapacidad();
		if ( conDiscapacidad == null ) {
			return 0;
		}
		com.aeat.jaxb.TipoRetenidoSalida2011.Ascendientes.ConDiscapacidad.EnGrado1  enGrado1 = 
			conDiscapacidad.getEnGrado1();
		if ( enGrado1 == null ) {
			return 0;
		}
		com.aeat.jaxb.TipoRetenidoSalida2011.Ascendientes.ConDiscapacidad.EnGrado1.ConMovilidadReducida conMovilidadReducida = 
			enGrado1.getConMovilidadReducida();
		return conMovilidadReducida == null ? 0 : toInteger(conMovilidadReducida.getPorEntero());
	}

	@Override
	public Integer getAscendents65Total() {
		com.aeat.jaxb.TipoRetenidoSalida2011.Ascendientes.ConDiscapacidad conDiscapacidad = 
			getAscendientesConDiscapacidad();
		if ( conDiscapacidad == null ) {
			return 0;
		}
		com.aeat.jaxb.TipoRetenidoSalida2011.Ascendientes.ConDiscapacidad.EnGrado2  enGrado2 = 
			conDiscapacidad.getEnGrado2();
		return enGrado2 == null ? 0 : toInteger(enGrado2.getTotal());
	}

	@Override
	public Integer getAscendents65Entirely() {
		com.aeat.jaxb.TipoRetenidoSalida2011.Ascendientes.ConDiscapacidad conDiscapacidad = 
			getAscendientesConDiscapacidad();
		if ( conDiscapacidad == null ) {
			return 0;
		}
		com.aeat.jaxb.TipoRetenidoSalida2011.Ascendientes.ConDiscapacidad.EnGrado2  enGrado2 = 
			conDiscapacidad.getEnGrado2();
		return enGrado2 == null ? 0 : toInteger(enGrado2.getPorEntero());
	}

	@Override
	public Double getPaidIrpf() {
		Regularizacion regularizacion = 
			retenidoSalida2011.getRegularizacion();
		return regularizacion == null ? 0.00 : toDouble(regularizacion.getRetencionPracticada());
	}

	@Override
	public IrpfRegularizationReason getReason() {
		Regularizacion regularizacion = 
			retenidoSalida2011.getRegularizacion();
		if ( regularizacion == null ) {
			return null;
		}
		List<Integer> causas = regularizacion.getCausa();
		return causas == null ||causas.isEmpty() ? 
				null : IrpfRegularizationReason.valueof(causas.get(0));
	}

	@Override
	public Double getPaidRemuneration() {
		Regularizacion regularizacion = 
			retenidoSalida2011.getRegularizacion();
		return regularizacion == null ? 0.00 : toDouble(regularizacion.getRetribSatisfechas()); 
	}

	@Override
	public Double getPriorAnnualIrpf() {
		Regularizacion regularizacion = 
			retenidoSalida2011.getRegularizacion();
		return regularizacion == null ? 0.00 : toDouble(regularizacion.getRetencionAnualInicial());
	}

	@Override
	public Double getPriorAnnualRemuneration() {
		Regularizacion regularizacion = 
			retenidoSalida2011.getRegularizacion();
		return regularizacion == null ? 0.00 : toDouble(regularizacion.getRetribAnualesIniciales());
	}

	@Override
	public Double getPriorBaseIrpf() {
		Regularizacion regularizacion = 
			retenidoSalida2011.getRegularizacion();
		return regularizacion == null ? 0.00 : toDouble(regularizacion.getBaseRetencion());
	}

	@Override
	public Double getPriorIrpf() {
		Regularizacion regularizacion = 
			retenidoSalida2011.getRegularizacion();
		return regularizacion == null ? 0.00 : toDouble(regularizacion.getTipoRetencion());
	}

	@Override
	public Double getPriorMinimunPersonalFamily() {
		Regularizacion regularizacion = 
			retenidoSalida2011.getRegularizacion();
		return regularizacion == null ? 0.00 : toDouble(regularizacion.getMinimoPersonalFamiliarInicial());
	}

	@Override
	public Double getPriorDeductHomeLoanAmount() {
		Regularizacion regularizacion = 
			retenidoSalida2011.getRegularizacion();
		if ( regularizacion == null ) {
			return 0.00;
		}
		MinoracionPrestamosVivienda minoracionPrestamosVivienda = 
			regularizacion.getMinoracionPrestamosVivienda();
		return minoracionPrestamosVivienda == null ? 0.00 : toDouble(minoracionPrestamosVivienda.getImporteMinoracion());
	}
	
	public Administration getAdministration() {
		Contract contract =(Contract) getTo();
		WorkPlace workPlace = contract.getWorkPlace();
		return workPlace.getEconomicAgreement();
	}


	private Integer toInteger(Byte bite){
		return bite != null ? bite.intValue() : 0;
	}

	private Integer toInteger(Integer integer){
		return integer != null ? integer : 0;
	}

	private Double toDouble(BigDecimal bigDecimal){
		return bigDecimal != null ? bigDecimal.doubleValue() : 0.00;
	}
	
	private ComputoDescendientes getComputoDescendientes(){
		Descendientes descendientes = 
			retenidoSalida2011.getDescendientes();
		if (descendientes == null ){
			return null;
		}
		return descendientes.getComputoDescendientes();
	}
	
	
	private ConDiscapacidad getConDiscapacidad(){
		Descendientes descendientes = 
			retenidoSalida2011.getDescendientes();
		if (descendientes == null ){
			return null;
		}
		return descendientes.getConDiscapacidad();
	}
	
	private Menores3Años getMenores3Años(){
		Descendientes descendientes = 
			retenidoSalida2011.getDescendientes();
		if (descendientes == null ){
			return null;
		}
		return descendientes.getMenores3Años();
	}

	private Resto getResto(){
		Descendientes descendientes = 
			retenidoSalida2011.getDescendientes();
		if (descendientes == null ){
			return null;
		}
		return descendientes.getResto();
	}

	private com.aeat.jaxb.TipoRetenidoSalida2011.Ascendientes.ConDiscapacidad getAscendientesConDiscapacidad(){
		Ascendientes ascendientes = 
			retenidoSalida2011.getAscendientes();
		if (ascendientes == null ){
			return null;
		}
		return ascendientes.getConDiscapacidad();
	}
	
	private Menores75 getMenores75(){
		Ascendientes ascendientes = 
			retenidoSalida2011.getAscendientes();
		return ascendientes == null ? null : ascendientes.getMenores75();
	}
	
	private Mayores75 getMayores75(){
		Ascendientes ascendientes = 
			retenidoSalida2011.getAscendientes();
		return ascendientes == null ? null : ascendientes.getMayores75();
	}
	
	private MinimoCtye getMinimoCtye() {
		MinimoPersonalFamiliar minimoPersonalFamiliar = 
			retenidoSalida2011.getMinimoPersonalFamiliar();
		return  minimoPersonalFamiliar == null ? null : minimoPersonalFamiliar.getMinimoCtye();
	}

	private MinimoAscendientes getMinimoAscendientes() {
		MinimoPersonalFamiliar minimoPersonalFamiliar = 
			retenidoSalida2011.getMinimoPersonalFamiliar();
		return  minimoPersonalFamiliar == null ? null : minimoPersonalFamiliar.getMinimoAscendientes();
	}
	
	private MinimoDescendientes getMinimoDescendientes() {
		MinimoPersonalFamiliar minimoPersonalFamiliar = 
			retenidoSalida2011.getMinimoPersonalFamiliar();
		return  minimoPersonalFamiliar == null ? null : minimoPersonalFamiliar.getMinimoDescendientes();
	}

	private MinimoDiscapacidad getMinimoDiscapacidad() {
		MinimoPersonalFamiliar minimoPersonalFamiliar = 
			retenidoSalida2011.getMinimoPersonalFamiliar();
		return  minimoPersonalFamiliar == null ? null : minimoPersonalFamiliar.getMinimoDiscapacidad();
	}

	// ------------------------------------------
	// Gets ( calculate ) irpf draft.
	// ------------------------------------------
	private void calculateIrpf() throws SQLException, ExpressionException, SalaryException {
		Contract contract = (Contract ) getTo();
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				SQLConstants.CONTRACT + "."+ ContractColumns.ID, 
				contract.getId());
		
		Connection connection =  getConnection();

		SQLAEAT2011Factory factory =  
			new SQLAEAT2011Factory(connection, date, criteria);
		
		Entrada2011Handler entrada2011Handler = 
			new DefaultEntrada2011Handler(new CallbackHandler() {
				
				@Override
				public void onSalida(TipoRetenedorSalida2011 retenedorSalida2011,
						TipoRetenidoSalida2011 retenidoSalida2011) {
					IrpfDraftController.this.retenidoSalida2011 = retenidoSalida2011;
				}
				
				@Override
				public void onError(TipoRetenedorError2011 retenedorError2011,
						TipoRetenidoError2011 retenidoError2011) {
				}
			});
		
		factory.forEachTipoRetenidoEntrada2011(entrada2011Handler);

	}
	
	
	private void loadOthers() throws ManagerBeanException{
		others = new ArrayList<IIrpfController>();
	
		Contract contract = ( Contract ) getTo();
		IManagerBean bean = 
			BeanManager.getManagerBean(IrpfResult.class);
		Criteria criteria = 
			new Criteria();
		criteria.addEqualExpression(
				bean.getFieldName(IPayrollAlias.IRPF_RESULT_CONTRACT_ID), 
				contract.getId());

		List<?> list = bean.getList(criteria);
		if ( list == null ) {
			return;
		}

		@SuppressWarnings("unchecked")
		List<IrpfResult> irpfs = ( List<IrpfResult>) list;
		
		for (IrpfResult irpfResult : irpfs) {
			others.add(new IrpfController(irpfResult) );
		}

	}
	
	
	private void resetOther() {
		otherIndex = null;
		for (int i = 0; i < others.size(); i++) {
			IIrpfController other = others.get(i);
			if ( other.getEffectiveDate().equals(date)){
				otherIndex = i;
				break;
			}
		}
	}
	
	private static Connection getConnection(){
		String sessionFactory = HibernateUtil.getSessionFactoryName(Salary.class.getName());
		return  HibernateUtil.getSQLConnection(sessionFactory);
	}

	
}

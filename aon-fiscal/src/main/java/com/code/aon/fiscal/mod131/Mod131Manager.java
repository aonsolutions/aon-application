package com.code.aon.fiscal.mod131;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.Mod131Key;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.model.FiscalModelManager;
import com.code.aon.fiscal.model.IFiscalDeclaration;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod131Manager extends FiscalModelManager {
	
	private static String SELECT_15 = "SELECT " 
			+" SUM( IF(fmd.amount<0,fmd.amount,0) )"
			+" FROM fs_model_detail fmd"
			+" INNER JOIN fs_model fm ON (fmd.fs_model = fm.id)" 
			+" WHERE " + DomainManager.getStaticSQLWhereClause("fmd.domain")
			+" AND fm.model = ? "
			+" AND fm.administration = ? "
			+" AND fm.year = ? "
			+" AND fm.period < ? "
			+" AND fmd.type = ? ";
	
	private static String SELECT_11 = "SELECT " 
			+" SUM( fmd.amount )"
			+" FROM fs_model_detail fmd"
			+" INNER JOIN fs_model fm ON (fmd.fs_model = fm.id)" 
			+" WHERE " + DomainManager.getStaticSQLWhereClause("fmd.domain")
			+" AND fm.model = ? "
			+" AND fm.administration = ? "
			+" AND fm.year = ? "
			+" AND fm.period < ? "
			+" AND fmd.type = ? ";

	private static String SELECT_08 = "SELECT " 
			+" SUM( IF(it.quota != 0,it.quota,ROUND(id.taxable_base * it.percentage / 100, 2) ) ) RET "
			+" FROM invoice_tax it "
			+" INNER JOIN invoice_detail id ON (it.invoice_detail = id.id)" 
			+" INNER JOIN invoice i ON (id.invoice = i.id)"
			+" WHERE " + DomainManager.getStaticSQLWhereClause("i.domain")
			+" AND i.type = 1 " 			// Ventas
			+" AND it.tax_type = 2" 		// IRPF
			+" AND i.tax_date >= ?"
			+" AND i.tax_date <= ?";

	private static String SELECT_09_03 = "SELECT " 
			+" fm.period,fmd.amount"
			+" FROM fs_model_detail fmd"
			+" INNER JOIN fs_model fm ON (fmd.fs_model = fm.id)" 
			+" WHERE " + DomainManager.getStaticSQLWhereClause("fmd.domain")
			+" AND fm.model = ? "
			+" AND fm.administration = ? "
			+" AND fm.year = ? "
			+" AND fmd.type = ? "
			+" ORDER by fm.period";
	
	private static String SELECT_09_08 = "SELECT " 
			+" fm.period,fmd.amount"
			+" FROM fs_model_detail fmd"
			+" INNER JOIN fs_model fm ON (fmd.fs_model = fm.id)" 
			+" WHERE " + DomainManager.getStaticSQLWhereClause("fmd.domain")
			+" AND fm.model = ? "
			+" AND fm.administration!= ? "
			+" AND fm.year = ? "
			+" AND fmd.type = ? "
			+" ORDER by fm.period";
	
	private String domainName;
	
	public Mod131Manager(String domainName) {
		this.domainName = domainName;
	}
	
	public String getDomainName() {
		return domainName;
	}

	@Override
	public boolean accept(FiscalModelType type) {
		return type == FiscalModelType.M131;
	}

	@Override
	public Mod131 initializeFiscalModel(FiscalModel fiscalModel) throws AonException {
		Mod131 mod131 = new Mod131();
		mod131.setFiscalModel(fiscalModel);
		return mod131;
	}
	
	@Override
	public Mod131 initializeFiscalModelDetails(IFiscalDeclaration declaration) throws AonException {
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(getDomainName());
			
			Mod131 mod131 = (Mod131) declaration;
			FiscalModel fiscalModel = mod131.getHeader();
			mod131.initializeDetails();
			mod131.calculate();
			
			Date dateFrom = getInitialDate(fiscalModel);	
			Date dateTo = getDueDate(fiscalModel);
			
	//		Cumplimentarán este apartado los contribuyentes que desarrollen actividades económicas 
	//		en estimación objetiva distintas de las agrícolas, ganaderas y forestales, respecto de 
	//		las cuales no resulte posible determinar ninguno de los datos-base a efectos del pago 
	//		fraccionado y, por tanto, el importe del mismo no pueda calcularse con arreglo al 
	//		procedimiento a que se refiere el apartado I anterior
	//		Casilla 03. Consigne en esta casilla el volumen de ventas o ingresos de las actividades 
	//		a que se refiere este apartado correspondientes al trimestre por el que se realiza el pago
	//		fraccionado, incluidas las subvenciones corrientes y excluidas las subvenciones de capital
	//		y las indemnizaciones.
			
			double c02 = mod131.getDetail( Mod131Key.AC02).getAmount();
			double c03 = 0;
			if (c02 == 0) {
				SummaryProvider sp = new SummaryProvider();
				SummaryProviderParameters params = new SummaryProviderParameters(getDomainName());
				params.setAccountExpression( "70*|71*|72*|73*|75*|76*|77*|78*|79*" );
				params.setAccountLevel(5);
				params.setFromDate(dateFrom);
				params.setToDate(dateTo);
				SummaryCollection sc = sp.getSummaryCollection(conn,params,false);
				//c03 = sc.getCreditBalance();
				c03 = CommonUtil.round(sc.getOpeningCredit() + sc.getCredit() - sc.getOpeningDebit() - sc.getDebit());
				mod131.ensureDetail(Mod131Key.C03).addAccumulatedAmount(c03);
			}
			
	//		Casilla 08. En su caso, se consignará en esta casilla la suma de las retenciones e ingresos 
	//		a cuenta que, habiendo sido practicados sobre las contraprestaciones procedentes de las 
	//		actividades económicas en estimación objetiva cuyos rendimientos están sujetos a retención o 
	//		ingreso a cuenta, correspondan al trimestre a que se refiere el pago fraccionado.
			mod131.ensureDetail(Mod131Key.C08).addAccumulatedAmount(getC08(conn,dateFrom,dateTo));		
	
	//		Casilla 09. Podrán cumplimentar esta casilla únicamente los contribuyentes que tengan 
	//		derecho a la deducción por obtención de rendimientos de actividades económicas a efectos
	//		del pago fraccionado por cumplir el siguiente requisito:
	//		Que la suma del importe de la casilla 01 o, en el supuesto de que no pudiera determinarse 
	//		ningún dato-base, el resultado de elevar al año el 25 por 100 de la casilla 03 del primer
	//		trimestre del ejercicio o del primer trimestre de inicio de actividades y/o, en su caso, 
	//		para actividades agrícolas, ganaderas y forestales, el resultado de elevar al año el 25 
	//		por 100 de la casilla 05 del primer trimestre del ejercicio o del primer trimestre de 
	//		inicio de actividades, sea igual o inferior a 12.000 euros. En los supuestos de inicio 
	//		de la actividad a lo largo del ejercicio en la elevación al año se tendrán en consideración 
	//		los días que resten hasta el final del año.
	//		Además, si el contribuyente también está obligado a presentar el modelo 130 para declarar 
	//		el pago fraccionado correspondiente a las actividades económicas en estimación directa
	//		que realice, como sucede, entre otros, en el supuesto a que se refiere el segundo párrafo 
	//		del artículo 35 del Reglamento del Impuesto, deberá adicionar a las magnitudes anteriores
	//		el resultado de elevar al año el importe de la casilla 03, y/o el resultado de elevar al 
	//		año el 25 por 100 de la casilla 08, ambos del primer trimestre del ejercicio o del primer 
	//		trimestre de inicio de actividades.
	//		Si la suma total de estas magnitudes no excede de 12.000 euros, se consignará en esta 
	//		casilla 09 el importe obtenido de dividir entre cuatro la cuantía de la deducción por 
	//		obtención de rendimientos de actividades económicas, prevista en el apartado 1 del 
	//		artículo 80 bis de la Ley del Impuesto, que corresponda a efectos de los pagos fraccionados.
	//		Para calcular la cuantía de esta deducción deberá tenerse en cuenta que:
	//		- Cuando la suma de las magnitudes anteriormente indicadas (incluidas, en su caso, las 
	//		  referidas al modelo 130) sea igual o inferior a 8.000 euros anuales, esta deducción asciende
	//		  a 400 euros.
	//		- Cuando dicha suma esté comprendida entre 8.000,01 y 12.000 euros anuales, la cuantía 
	//		  de la deducción vendrá determinada por la siguiente operación: (400 euros menos el 
	//		  producto de multiplicar por 0,1 la diferencia entre la suma de las magnitudes indicadas 
	//		  y 8.000 euros anuales).
	
			double sumC01 = mod131.getDetail( Mod131Key.AC01).getAmount();
			double previousC03 = getC09_03(conn,SELECT_09_03,fiscalModel,Mod131Key.C03);
			double previousC08 = getC09_08(conn,SELECT_09_08,fiscalModel,Mod131Key.C08);
			double c09 = 0;
			double temp = CommonUtil.round(sumC01 + previousC03 + previousC08);
			if ( temp <= 12000 ) {
				if ( temp <= 8000 ) {
					c09 = CommonUtil.round( 400.0 / 4 );
				} else {
					c09 = CommonUtil.round( (400.0 - ((temp - 8000) * 0.1)) / 4);
				}
			}
			mod131.ensureDetail(Mod131Key.C09).addAccumulatedAmount(c09);
			
	//		Casilla 11. Si en la casilla 10 anterior se hubiera obtenido una cantidad positiva, 
	//		se hará constar en la casilla 11 el importe de los resultados negativos que, en su 
	//		caso, se hubieran obtenido en la casilla 15 de cualquiera de las declaraciones 
	//		anteriores, modelo 131, del mismo ejercicio y que no hubieran sido deducidos 
	//		anteriormente, teniendo en cuenta que en ningún caso podrá figurar en la casilla 11 
	//		un importe superior a la cantidad positiva consignada en la casilla 10.
			mod131.calculate();
			double c10 = mod131.getDetail( Mod131Key.C10 ).getAmount();
			double c11 = 0.0;
			if (c10 > 0) {
				double previousC11 = getPreviousAmount(conn,SELECT_11,fiscalModel,Mod131Key.C11);
				double previousC15 = getPreviousAmount(conn,SELECT_15,fiscalModel,Mod131Key.C15);
				previousC15 = CommonUtil.round( previousC15 * (-1));
				c11 = CommonUtil.round( previousC15 - previousC11 );
				c11 = c11>c10?c10:c11;
			}
			mod131.ensureDetail(Mod131Key.C11).addAccumulatedAmount(c11);
			
			
	//		Casilla 12. Si en la casilla 10 se hubiera obtenido una cantidad positiva y el 
	//		contribuyente está realizando pagos por préstamos destinados a la adquisición 
	//		o rehabilitación de su vivienda habitual, se hará constar, en su caso, en la casilla 
	//		12 el importe de la deducción a que se refiere el artículo 110.3.d) del Reglamento del Impuesto.
	//		Si únicamente se hubiese cumplimentado el apartado I y/o el apartado II de este modelo, 
	//		dicha deducción está constituida por la suma de los importes resultantes de aplicar el 
	//		porcentaje del 0,5 por 100 sobre la cantidad consignada en la casilla 01 y el porcentaje 
	//		del 2 por 100 sobre la cantidad consignada en la casilla 03. Si solamente se hubiese 
	//		cumplimentado el apartado III, la deducción está constituida por el importe resultante 
	//		de aplicar el porcentaje del 2 por 100 sobre la cantidad consignada en la casilla 05.
	//		En cualquier caso, deberá tenerse en cuenta que el importe consignado en la casilla 12 
	//		no podrá ser superior a la diferencia positiva entre las casillas 10 y 11 anteriores y 
	//		que la deducción por este concepto tiene como límite máximo la cantidad de 660,14 euros 
	//		anuales, por lo que en la casilla 12 no podrá figurar en ningún caso un importe superior 
	//		a dicha cantidad, sin que tampoco pueda superar la citada cantidad el conjunto de los 
	//		importes consignados en las casillas 12 de los cuatro modelos 131 del mismo ejercicio.
			if (mod131.isPermanentAddressChanges() && CommonUtil.round(c10 - c11) > 0 ) {
				double c12 = 0.0;
				mod131.calculate();
				double c01 = mod131.getDetail( Mod131Key.AC01 ).getAmount();
				c03 = mod131.getDetail( Mod131Key.C03 ).getAmount();
				double c05 = mod131.getDetail( Mod131Key.C05 ).getAmount();
	
				if (c01 > 0 && c05 > 0 ) { // no resultará aplicable cuando el contribuyente realice simultáneamente   
				   						   // actividades agrícolas y actividades distintas de éstas.
					c12 = CommonUtil.round(c01 * 0.5 / 100);
					if (c03 > 0 ) {
						c12 = CommonUtil.round(c03 * 2 / 100);
						c12 = c12 + (c12>660.14?660.14:c12);
					} else  if (c05 > 0 ) {
						c12 = CommonUtil.round(c05 * 2 / 100);
						c12 = c12 + (c12>660.14?660.14:c12);
					}
					if (CommonUtil.round(c10 - c11) < c12) {
						c12 = CommonUtil.round(c10 - c11); 
					}
					mod131.ensureDetail(Mod131Key.C12).addAccumulatedAmount(c12);
				}
			}
	
			
			mod131.calculate();
			return mod131;
		} catch (AonConnectionException e) {
			throw new AonException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(conn);
		}
	}

	@Override
	public IFiscalDeclaration loadFiscalModel(FiscalModel fiscalModel) throws AonException {
		Mod131 mod131 = new Mod131();
		mod131.setFiscalModel(fiscalModel);
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName( IEntityAlias.FISCAL_MODEL_DETAIL_FISCAL_MODEL_ID );
		criteria.addEqualExpression(alias, fiscalModel.getId());
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			FiscalModelDetail detail = (FiscalModelDetail) to;
			mod131.addDetail(detail);
		}
		return mod131;
	}
	
	private double getPreviousAmount(Connection conn,String select,FiscalModel fiscalModel, Mod131Key key) throws AonException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		double retention = 0;
		try {
			ps = conn.prepareStatement(select,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			int filled = DomainManager.fillHostVariables(ps, 1);
			i = i + filled;
			ps.setString(++i, FiscalModelType.M131.getValue());
			ps.setInt(++i, fiscalModel.getAdministration().ordinal());
			ps.setInt(++i, fiscalModel.getYear());
			ps.setInt(++i, fiscalModel.getPeriod().ordinal());
			ps.setString(++i, key.getValue());
			rs = ps.executeQuery();
			if (rs.next()) {
				retention = rs.getDouble(1);
			}
			rs.close();
			ps.close();
			return retention;
		} catch (SQLException e) {
			throw new AonException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
	private double getC08(Connection conn,Date dateFrom, Date dateTo) throws AonException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		double retention = 0;
		try {
			ps = conn.prepareStatement(SELECT_08,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			int filled = DomainManager.fillHostVariables(ps, 1);
			i = i + filled;
			ps.setDate(++i, new java.sql.Date( dateFrom.getTime() ));
			ps.setDate(++i, new java.sql.Date( dateTo.getTime()));
			rs = ps.executeQuery();
			if (rs.next()) {
				retention = rs.getDouble(1);
			}
			rs.close();
			ps.close();
			return retention;
		} catch (SQLException e) {
			throw new AonException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	private double getC09_03(Connection conn,String select,FiscalModel fiscalModel, Mod131Key key) throws AonException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		double c03 = 0;
		try {
			ps = conn.prepareStatement(select,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			int filled = DomainManager.fillHostVariables(ps, 1);
			i = i + filled;
			ps.setString(++i, fiscalModel.getModel().getValue());
			ps.setInt(++i, fiscalModel.getAdministration().ordinal());
			ps.setInt(++i, fiscalModel.getYear());
			ps.setString(++i, key.getValue());
			rs = ps.executeQuery();
			if (rs.next()) {
				Period period = Period.values()[rs.getInt(1)]; 
				c03 = rs.getDouble(2);
				Date firstPeriodDay = period.getStartDate(fiscalModel.getYear());
				Date firstYearDay = CommonUtil.getYearFirstDay(fiscalModel.getYear());
				double product;
				if ( DateUtils.isSameDay(firstPeriodDay, firstYearDay) ) {
					product = 4.0;
				} else {
					long days = CommonUtil.getDaysBetweenDates(period.getDueDate(fiscalModel.getYear()), CommonUtil.getYearLastDay(fiscalModel.getYear()));
					product = CommonUtil.round(365/days);
				}
				c03 = CommonUtil.round(c03 * product);
			}
			rs.close();
			ps.close();
			return c03 ;
		} catch (SQLException e) {
			throw new AonException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
	private double getC09_08(Connection conn,String select,FiscalModel fiscalModel, Mod131Key key) throws AonException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		double c08 = 0;
		try {
			ps = conn.prepareStatement(select,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			int filled = DomainManager.fillHostVariables(ps, 1);
			i = i + filled;
			ps.setString(++i, fiscalModel.getModel().getValue());
			ps.setInt(++i, fiscalModel.getAdministration().ordinal());
			ps.setInt(++i, fiscalModel.getYear());
			ps.setString(++i, key.getValue());
			rs = ps.executeQuery();
			if (rs.next()) {
				Period period = Period.values()[rs.getInt(1)]; 
				c08 = rs.getDouble(2);
				Date firstPeriodDay = period.getStartDate(fiscalModel.getYear());
				Date firstYearDay = CommonUtil.getYearFirstDay(fiscalModel.getYear());
				double product;
				if ( DateUtils.isSameDay(firstPeriodDay, firstYearDay) ) {
					product = 4.0;
				} else {
					long days = CommonUtil.getDaysBetweenDates(period.getDueDate(fiscalModel.getYear()), CommonUtil.getYearLastDay(fiscalModel.getYear()));
					product = CommonUtil.round(365/days);
				}
				c08 = CommonUtil.round(( c08 * 25 / 100) * product);
			}
			rs.close();
			ps.close();
			return c08 ;
		} catch (SQLException e) {
			throw new AonException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}
}

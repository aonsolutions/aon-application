package com.code.aon.fiscal.mod130;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.IFiscalConstants;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.Mod130Key;
import com.code.aon.fiscal.model.FiscalModelManager;
import com.code.aon.fiscal.model.IFiscalDeclaration;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.enumeration.TaxRegime;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod130Manager extends FiscalModelManager {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static String SELECT_TAX_REGIME = "SELECT "
		+" value FROM app_param "
		+" WHERE " + DomainManager.getStaticSQLWhereClause("app_param.domain") 
		+" AND name = '" + IFiscalConstants.FS_TAX_REGIME + "'";
	
	private static String SELECT_19 = "SELECT " 
			+" SUM( IF(fmd.amount<0,fmd.amount,0) )"
			+" FROM fs_model_detail fmd"
			+" INNER JOIN fs_model fm ON (fmd.fs_model = fm.id)" 
			+" WHERE " + DomainManager.getStaticSQLWhereClause("fmd.domain")
			+" AND fm.model = ? "
			+" AND fm.administration = ? "
			+" AND fm.year = ? "
			+" AND fm.period < ? "
			+" AND fm.document = ? "
			+" AND fmd.type = ? ";
	
	private static String SELECT_15 = "SELECT " 
			+" SUM( fmd.amount )"
			+" FROM fs_model_detail fmd"
			+" INNER JOIN fs_model fm ON (fmd.fs_model = fm.id)" 
			+" WHERE " + DomainManager.getStaticSQLWhereClause("fmd.domain")
			+" AND fm.model = ? "
			+" AND fm.administration = ? "
			+" AND fm.year = ? "
			+" AND fm.period < ? "
			+" AND fm.document = ? "
			+" AND fmd.type = ? ";

	private static String SELECT_05_1 = "SELECT " 
			+" SUM( IF(fmd.amount<0,0,fmd.amount) )"
			+" FROM fs_model_detail fmd"
			+" INNER JOIN fs_model fm ON (fmd.fs_model = fm.id)" 
			+" WHERE " + DomainManager.getStaticSQLWhereClause("fmd.domain")
			+" AND fm.model = ? "
			+" AND fm.administration = ? "
			+" AND fm.year = ? "
			+" AND fm.period < ? "
			+" AND fm.document = ? "
			+" AND fmd.type = ? ";

	private static String SELECT_05_2 = "SELECT " 
			+" SUM( fmd.amount )"
			+" FROM fs_model_detail fmd"
			+" INNER JOIN fs_model fm ON (fmd.fs_model = fm.id)" 
			+" WHERE " + DomainManager.getStaticSQLWhereClause("fmd.domain")
			+" AND fm.model = ? "
			+" AND fm.administration = ? "
			+" AND fm.year = ? "
			+" AND fm.period < ? "
			+" AND fm.document = ? "
			+" AND fmd.type = ? ";
	

	private static String SELECT_06 = "SELECT " 
			+" SUM( IF(it.quota != 0,it.quota,ROUND(it.base * it.percentage / 100, 2) ) ) RET "
			+" FROM invoice_tax it "
			+" INNER JOIN invoice_detail id ON (it.invoice_detail = id.id)" 
			+" INNER JOIN invoice i ON (id.invoice = i.id)"
			+" WHERE " + DomainManager.getStaticSQLWhereClause("i.domain")
			+" AND i.type = 1 " 			// Ventas
			+" AND it.tax_type = 2" 		// IRPF
			+" AND i.tax_date >= ?"
			+" AND i.tax_date <= ?";
	
	private String domainName;
	
	public Mod130Manager(String domainName) {
		this.domainName = domainName;
	}
	
	public String getDomainName() {
		return domainName;
	}
	
	@Override
	public boolean accept(FiscalModelType type) {
		return type == FiscalModelType.M130;
	}

	@Override
	public Mod130 initializeFiscalModel(FiscalModel fiscalModel) throws AonException {
		Mod130 mod130 = new Mod130();
		mod130.setFiscalModel(fiscalModel);
		return mod130;
	}
	
	@Override
	public Mod130 initializeFiscalModelDetails(IFiscalDeclaration declaration) throws AonException {
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(getDomainName());

			Mod130 mod130 = (Mod130) declaration;
			FiscalModel fiscalModel = mod130.getHeader();
			mod130.initializeDetails();
			Date dateFrom = getInitialDate(fiscalModel);	
			Date dateTo = getDueDate(fiscalModel);
			
			double p1 = fiscalModel.getParticipationPercent();
			if (p1 == 0) {
				p1 = 100;
			}
			mod130.ensureDetail(Mod130Key.P1).addAccumulatedAmount(p1);
			
	//		 Casilla 01. Consigne la totalidad de los ingresos í­ntegros fiscalmente 
	//		 computables procedentes de las actividades económicas a las que se 
	//		 refiere este apartado y que correspondan al perí­odo comprendido entre 
	//		 el primer dí­a del año y el último dí­a del trimestre.
			SummaryProvider sp = new SummaryProvider();
			SummaryProviderParameters params = new SummaryProviderParameters(getDomainName());
			params.setAccountExpression( "7*" );
			params.setAccountLevel(5);
			params.setFromDate(dateFrom);
			params.setToDate(dateTo);
			SummaryCollection sc = sp.getSummaryCollection(conn,params,false);
			double c01 = CommonUtil.round(sc.getOpeningCredit() + sc.getCredit() - sc.getOpeningDebit() - sc.getDebit());
			c01 = CommonUtil.round( c01 * p1 / 100);
			mod130.ensureDetail(Mod130Key.C01).addAccumulatedAmount(c01);
			
	//		 Casilla 02. Haga constar el importe de los gastos que, teniendo la 
	//		 consideración de fiscalmente deducibles, resulten imputables a las 
	//		 actividades económicas a las que se refiere este apartado y correspondan 
	//		 al perí­odo temporal indicado anteriormente. Se incluirá en esta casilla 
	//		 el importe de las amortizaciones fiscalmente deducibles correspondientes 
	//		 a la depreciación experimentada por el inmovilizado afecto a las actividades 
	//		 desarrolladas en el perí­odo comprendido entre el primer dí­a del año y el 
	//		 último dí­a del trimestre, así­ como, en su caso, el importe de las provisiones 
	//		 que, correspondiendo al citado perí­odo temporal, tengan, asimismo, la 
	//		 consideración de fiscalmente deducibles. Tratándose de actividades a las 
	//		 que resulte aplicable la modalidad simplificada del método de estimación 
	//		 directa, se incluirán en esta casilla los importes de las amortizaciones, 
	//		 de las provisiones deducibles y de los gastos de difí­cil justificación 
	//		 correspondientes al perí­odo comprendido entre el primer dí­a del año y el 
	//		 último dí­a del trimestre, determinados conforme a las especialidades 
	//		 establecidas en el artí­culo 30 del Reglamento del Impuesto.
			params = new SummaryProviderParameters(getDomainName());
			params.setAccountExpression( "6*" );
			params.setAccountLevel(5);
			params.setFromDate(dateFrom);
			params.setToDate(dateTo);
			sc = sp.getSummaryCollection(conn,params,false);
			double c02 = CommonUtil.round(sc.getOpeningDebit() + sc.getDebit() - sc.getOpeningCredit() - sc.getCredit());
			c02 = CommonUtil.round( c02 * p1 / 100);

	// 		Artí­culo 30. Determinación del rendimiento neto en el método de estimación 
	// 		directa simplificada.
	// 		El conjunto de las provisiones deducibles y los gastos de difí­cil justificación 
	// 		se cuantificará aplicando el porcentaje del 5 por ciento sobre el rendimiento 
	// 		neto, excluido este concepto. No obstante, no resultará de aplicación dicho 
	// 		porcentaje de deducción cuando el contribuyente opte por la aplicación de la 
	// 		reducción prevista en el artí­culo 26 de este Reglamento.
			double c03 = CommonUtil.round(c01 - c02);
			TaxRegime taxRegime = searchTaxRegime(conn);
			mod130.setTaxRegime(taxRegime);
			if (c03 > 0 && taxRegime == TaxRegime.EDS) {
				c02 = CommonUtil.round(c02 + (c03 * 5 / 100));
			}
			mod130.ensureDetail(Mod130Key.C02).addAccumulatedAmount(c02);
			
	// 		Casilla 03. Consigne el resultado de efectuar la operación indicada en el impreso. 
	// 		De resultar una cantidad negativa, consígnela con signo menos (-). No obstante, 
	// 		los rendimientos netos en los que concurra alguna de las circunstancias contempladas 
	// 		en el artículo 32.1 de la Ley del Impuesto, se computarán a efectos del pago 
	// 		fraccionado previa aplicación de la reducción establecida en el citado artículo.
			c03 = CommonUtil.round(c01 - c02);
			mod130.ensureDetail(Mod130Key.C03).setAccumulatedAmount(c03);
			
	
	//		 Casilla 05. Haga constar en esta casilla la suma de las cantidades positivas 
	//		 consignadas en la casilla 07 de las declaraciones, modelo 130, correspondientes 
	//		 a los trimestres anteriores del mismo ejercicio, minorada en el importe de la 
	//		 suma de las cantidades consignadas en la casilla 16 de las citadas declaraciones. 
	//		 No se computarán las cantidades negativas consignadas en la casilla 07 de las 
	//		 declaraciones correspondientes a los trimestres anteriores.
			double previousC07 = getPreviousAmount(conn,SELECT_05_1,fiscalModel,Mod130Key.C07);
			double previousC16 = getPreviousAmount(conn,SELECT_05_2,fiscalModel,Mod130Key.C16);
			double c05 = CommonUtil.round(previousC07 - previousC16);
			mod130.ensureDetail(Mod130Key.C05).addAccumulatedAmount(c05);
	
	//		 Casilla 06. Se hará constar en esta casilla la suma de las retenciones e 
	//		 ingresos a cuenta soportados sobre los rendimientos procedentes de las 
	//		 actividades económicas a que se refiere este apartado, correspondientes 
	//		 al perí­odo comprendido entre el primer dí­a del año y el último dí­a del 
	//		 trimestre a que se refiere el pago fraccionado.
			double c06 = getC06(conn,dateFrom,dateTo);
			c06 = CommonUtil.round(c06 * p1 / 100);
			mod130.ensureDetail(Mod130Key.C06).addAccumulatedAmount(c06);
	//		 ------------------------------------------------------------------------
			
	//		Casilla 13. Cuando la cuantía de los rendimientos NETOS ( * ) de actividades económicas 
	//		del obligado tributario, obtenidos en el ejercicio anterior al que corresponde el trimestre 
	//		por el que se efectúa la autoliquidación, haya sido igual o inferior a 12.000 euros, se 
	//		consignará en esta casilla el siguiente importe, en función de la cuantía de los citados 
	//		rendimientos: 
	//		
	//		Cuantía de los rendimientos netos 
	//		del ejercicio anterior (en euros) 	Importe de la minoración (en euros)
	//		-----------------------------------------------------------------------
	//		Igual o inferior a 9.000 				100
	//		Entre 9.000,01 y 10.000 				75
	//		Entre 10.000,01 y 11.000 				50
	//		Entre 11.000,01 y 12.000 				25
	//		
	//		(*) Rendimiento neto de la actividad previo a la aplicación, en su caso de la reducción 
	//		por obtención de rendimientos irregulares, por mantenimiento o creación de empleo y resto 
	//		de reducciones aplicables en su caso sobre el rendimiento neto reducido de la actividad 
	//		(casillas 118, 143 o/y 171 del modelo de declaración de IRPF-2014)
	//		En el supuesto de que en el ejercicio anterior no se hubiese ejercicio actividad económica 
	//		alguna, se considerará que la cuantía de los rendimientos netos del ejercicio anterior son cero.
	//		Adviértase que, en el caso de que el contribuyente también esté obligado a presentar el 
	//		modelo 131, el importe correspondiente a la minoración del pago fraccionado contemplada en 
	//		el artículo 110.3.c) del Reglamento del Impuesto, calculada conforme se ha señalado en esta 
	//		casilla, puede distribuirse, si así se decide, entre ambos modelos 130 y 131 siempre que 
	//		los importes consignados en las casillas 13 del modelo 130 y 09 del modelo 131 no superen 
	//		en su conjunto, para cada trimestre, el importe de la minoración.
			
			double c13 = 0;
			mod130.ensureDetail(Mod130Key.C131).addAccumulatedAmount(c13);
	
	//		 ------------------------------------------------------------------------
	//		Casilla 15. Si en la casilla 14 anterior se hubiera obtenido una cantidad positiva, 
	//		se hará constar en la casilla 15 el importe de los resultados negativos que, en su 
	//		caso, se hubieran obtenido en la casilla 19 de cualquiera de las declaraciones 
	//		anteriores, modelo 130, del mismo ejercicio y que no hubieran sido deducidos 
	//		anteriormente, teniendo en cuenta que en ningún caso podrá figurar en la casilla 15 
	//		un importe superior a la cantidad positiva consignada en la casilla 14.
			mod130.calculate();
			double c14 = mod130.getDetail( Mod130Key.C14 ).getAmount();
			double c15 = 0.0;
			if (c14 > 0) {
				double previousC15 = getPreviousAmount(conn,SELECT_15,fiscalModel,Mod130Key.C15);
				double previousC19 = getPreviousAmount(conn,SELECT_19,fiscalModel,Mod130Key.C19);
				previousC19 = CommonUtil.round( previousC19 * (-1));
				c15 = CommonUtil.round( previousC19 - previousC15 );
				c15 = c15>c14?c14:c15;
			}
			mod130.ensureDetail(Mod130Key.C15).addAccumulatedAmount(c15);
	//		 ------------------------------------------------------------------------
	//		Casilla 16. Si en la casilla 14 se hubiera obtenido una cantidad positiva y el 
	//		contribuyente está realizando pagos por préstamos destinados a la adquisición o 
	//		rehabilitación de su vivienda habitual, se hará constar, en su caso, en la casilla 
	//		16 el importe de la deducción a que se refiere el artí­culo 110.3.d) del Reglamento 
	//		del Impuesto. 
	//		Si únicamente se hubiese cumplimentado el apartado I de este modelo, 
	//		dicha deducción está constituida por el importe resultante de aplicar el porcentaje 
	//		del 2 por 100 sobre la cantidad consignada en la casilla 03, con el lí­mite máximo 
	//		de 660,14 euros para cada trimestre. 
	//		Si solamente se hubiese cumplimentado el apartado II, la deducción está constituida 
	//		por el importe resultante de aplicar el porcentaje del 2 por 100 sobre la cantidad 
	//		consignada en la casilla 08. En este caso, el lí­mite máximo de deducción por este 
	//		concepto será de 660,14 euros anuales.
	//		En cualquier caso, deberá tenerse en cuenta que el importe consignado en la casilla 16 
	//		no podrá ser superior a la diferencia positiva entre las casillas 14 y 15 anteriores.
			if (mod130.isPermanentAddressChanges() && CommonUtil.round(c14 - c15) > 0 ) {
				double c16 = 0.0;
				mod130.calculate();
				double c08 = mod130.getDetail( Mod130Key.C08 ).getAmount();
				if (c03 == 0 || c08 == 0 ) { // no resultará aplicable cuando el contribuyente realice simultáneamente   
										   // actividades agrí­colas y actividades distintas de éstas.
					if (c03 > 0 ) {
						c16 = CommonUtil.round(c03 * 2 / 100);
						c16 = c16>660.14?660.14:c16;
					} else if (c08 > 0 ) {
						c16 = CommonUtil.round(c08 * 2 / 100);
						c16 = c16>660.14?660.14:c16;
					}
					if (CommonUtil.round(c14 - c15) < c16) {
						c16 = CommonUtil.round(c14 - c15); 
					}
					mod130.ensureDetail(Mod130Key.C16).addAccumulatedAmount(c16);
				}
			}
			
			mod130.calculate();
			return mod130;
		} catch (AonConnectionException e) {
			throw new AonException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(conn);
		}
	}

	private TaxRegime searchTaxRegime(Connection c) throws AonException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String value = null;
		try {
			ps = c.prepareStatement(SELECT_TAX_REGIME, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			DomainManager.fillHostVariables(ps, 1);
			rs = ps.executeQuery();
			if (rs.next()) {
				value = rs.getString(1);
			}
			rs.close();
			ps.close();
			if (StringUtils.isEmpty(value)) {
				return TaxRegime.BUSINESS_SOCIETY;
			}
			try {
				int tr = Integer.parseInt(value);
				return TaxRegime.values()[tr];
			} catch (NumberFormatException e) {
				return TaxRegime.BUSINESS_SOCIETY;
			} catch (ArrayIndexOutOfBoundsException e) {
				return TaxRegime.BUSINESS_SOCIETY;
			}
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

	private double getPreviousAmount(Connection c,String select,FiscalModel fiscalModel, Mod130Key key) throws AonException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		double retention = 0;
		try {
			ps = c.prepareStatement(select,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			int filled = DomainManager.fillHostVariables(ps, 1);
			i = i + filled;
			ps.setString(++i, FiscalModelType.M130.getValue());
			ps.setInt(++i, fiscalModel.getAdministration().ordinal());
			ps.setInt(++i, fiscalModel.getYear());
			ps.setInt(++i, fiscalModel.getPeriod().ordinal());
			ps.setString(++i, fiscalModel.getDocument());
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

	private double getC06(Connection c, Date dateFrom, Date dateTo) throws AonException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		double retention = 0;
		try {
			ps = c.prepareStatement(SELECT_06,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
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

	@Override
	public IFiscalDeclaration loadFiscalModel(FiscalModel fiscalModel) throws AonException {
		Mod130 mod130 = new Mod130();
		mod130.setFiscalModel(fiscalModel);
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName( IEntityAlias.FISCAL_MODEL_DETAIL_FISCAL_MODEL_ID );
		criteria.addEqualExpression(alias, fiscalModel.getId());
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			FiscalModelDetail detail = (FiscalModelDetail) to;
			mod130.addDetail(detail);
		}
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(getDomainName());
			TaxRegime taxRegime = searchTaxRegime(conn);
			mod130.setTaxRegime(taxRegime);
		} catch (AonConnectionException e) {
			throw new AonException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(conn);
		}
		return mod130;
	}
	
}

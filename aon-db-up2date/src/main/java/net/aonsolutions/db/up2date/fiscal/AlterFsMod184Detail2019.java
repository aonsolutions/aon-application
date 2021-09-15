package net.aonsolutions.db.up2date.fiscal;

import static com.esferalia.aon.jooq.tables.FsModel184Detail.FS_MODEL184_DETAIL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import net.aonsolutions.db.up2date.Update;

public class AlterFsMod184Detail2019 implements Update {

	private static final Logger LOGGER  = Logger.getLogger(AlterFsMod184Detail2019.class.getName());

	public static final AlterFsMod184Detail2019 ALTER_FS_MODEL_184_DETAIL_2019 = new AlterFsMod184Detail2019();

	private AlterFsMod184Detail2019() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		boolean consumosExplotacion = false;
		boolean arrendamientosCanones = false;
		boolean reparacionConservacion = false;
		boolean servProfIndep = false;
		boolean suministros = false;
		boolean gastosFinancieros = false;
		boolean amortizaciones = false;
		boolean provisiones = false;
		boolean inmIntFin = false;
		boolean inmRepCon = false;
		boolean inmGasRepCon = false;
		boolean inmTribRec = false;
		boolean inmSaldDudCobr = false;
		boolean inmCantDevTer = false;
		boolean inmPrimSeg = false;
		boolean inmAmort = false;
		boolean inmAmortMueb = false;
		boolean inmOtrGasDed = false;
		boolean inmNumDiasArr = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model184_detail");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);

				if ("consumos_explotacion".equals(name)) 	{consumosExplotacion = true;}
				if ("arrendamientos_canones".equals(name)) 	{arrendamientosCanones = true;}
				if ("servProfIndep".equals(name)) {reparacionConservacion = true;}
				if ("serv_prof_indep".equals(name)) 		{servProfIndep = true;}
				if ("suministros".equals(name)) 			{suministros = true;}
				if ("gastos_financieros".equals(name)) 		{gastosFinancieros = true;}
				if ("amortizaciones".equals(name)) 			{amortizaciones = true;}
				if ("provisiones".equals(name)) 			{provisiones = true;}
				if ("inm_int_fin".equals(name)) 			{inmIntFin = true;}
				if ("inm_rep_con".equals(name)) 			{inmRepCon = true;}
				if ("inm_gas_rep_con".equals(name)) 		{inmGasRepCon = true;}
				if ("inm_trib_rec".equals(name)) 			{inmTribRec = true;}
				if ("inm_sald_dud_cobr".equals(name)) 		{inmSaldDudCobr = true;}
				if ("inm_cant_dev_ter".equals(name)) 		{inmCantDevTer = true;}
				if ("inm_prim_seg".equals(name)) 			{inmPrimSeg = true;}
				if ("inm_amort".equals(name)) 				{inmAmort = true;}
				if ("inm_amort_mueb".equals(name)) 			{inmAmortMueb = true;}
				if ("inm_otr_gas_ded".equals(name))	 		{inmOtrGasDed = true;}
				if ("inm_num_dias_arr".equals(name)) 		{inmNumDiasArr = true;}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		if (!consumosExplotacion) {
			try {
				LOGGER.info("\tAlterFsMod184Detail2019. consumos_explotacion NOT EXISTS!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("consumos_explotacion", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				LOGGER.info("\tAlterFsMod184Detail2019. consumos_explotacion CREATED!");
			} catch(Exception e) {
				LOGGER.warning("\tAlterFsMod184Detail2019. consumos_explotacion NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterFsMod184Detail2019. consumos_explotacion EXISTS!");
		}
		
		if (!arrendamientosCanones) {
			try {
				LOGGER.info("\tAlterFsMod184Detail2019. arrendamientos_canones NOT EXISTS!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("arrendamientos_canones", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				LOGGER.info("\tAlterFsMod184Detail2019. arrendamientos_canones CREATED!");
			} catch (Exception e) {
				LOGGER.warning("\tAlterFsMod184Detail2019. arrendamientos_canones NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterFsMod184Detail2019. arrendamientos_canones EXISTS!");
		}
		
		if (!reparacionConservacion) {
			try {
				LOGGER.info("\tAlterFsMod184Detail2019. reparacion_conservacion NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("reparacion_conservacion", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				LOGGER.info("\tAlterFsMod184Detail2019. reparacion_conservacion CREATED!");
			} catch (Exception e) {
				LOGGER.warning("\tAlterFsMod184Detail2019. reparacion_conservacion NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterFsMod184Detail2019. reparacion_conservacion EXISTS!");
		}
		
		if (!servProfIndep) {
			try {
				LOGGER.info("\tAlterFsMod184Detail2019. serv_prof_indep NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("serv_prof_indep", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				LOGGER.info("\tAlterFsMod184Detail2019. serv_prof_indep CREATED!");
			} catch (Exception e) {
				LOGGER.warning("\tAlterFsMod184Detail2019. serv_prof_indep NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterFsMod184Detail2019. serv_prof_indep EXISTS!");
		}
		
		if (!suministros) {
			try {
				LOGGER.info("\tAlterFsMod184Detail2019. suministros NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("suministros", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				LOGGER.info("\tAlterFsMod184Detail2019. suministros CREATED!");
			} catch (Exception e) {
				LOGGER.info("\tAlterFsMod184Detail2019. suministros NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterFsMod184Detail2019. suministros EXISTS!");
		}

		if (!gastosFinancieros) {
			try {
				LOGGER.info("\tAlterFsMod184Detail2019. gastos_financieros NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("gastos_financieros", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				LOGGER.info("\tAlterFsMod184Detail2019. gastos_financieros CREATED!");
			} catch (Exception e) {
				LOGGER.info("\tAlterFsMod184Detail2019. gastos_financieros NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterFsMod184Detail2019. gastos_financieros EXISTS!");
		}

		if (!amortizaciones) {
			try {
				LOGGER.info("\tAlterFsMod184Detail2019. amortizaciones NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("amortizaciones", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				LOGGER.info("\tAlterFsMod184Detail2019. amortizaciones CREATED!");
			} catch (Exception e) {
				LOGGER.info("\tAlterFsMod184Detail2019. amortizaciones NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterFsMod184Detail2019. amortizaciones EXISTS!");
		}

		if (!provisiones) {
			try {
				LOGGER.info("\tAlterFsMod184Detail2019. provisiones NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("provisiones", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				LOGGER.info("\tAlterFsMod184Detail2019. provisiones CREATED!");
			} catch (Exception e) {
				LOGGER.info("\tAlterFsMod184Detail2019. provisiones NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterFsMod184Detail2019. provisiones EXISTS!");
		}

		if (!inmIntFin) {
			try {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_int_fin NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_int_fin", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				LOGGER.info("\tAlterFsMod184Detail2019. inm_int_fin CREATED!");
			} catch (Exception e) {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_int_fin NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterFsMod184Detail2019. inm_int_fin EXISTS!");
		}
		
		if (!inmRepCon) {
			try {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_rep_con NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_rep_con", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				LOGGER.info("\tAlterFsMod184Detail2019. inm_rep_con CREATED!");
			} catch (Exception e) {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_rep_con NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterFsMod184Detail2019. inm_rep_con EXISTS!");
		}
		
		if (!inmGasRepCon) {
			try {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_gas_rep_con NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_gas_rep_con", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				LOGGER.info("\tAlterFsMod184Detail2019. inm_gas_rep_con CREATED!");
			} catch (Exception e) {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_gas_rep_con NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterFsMod184Detail2019. inm_gas_rep_con EXISTS!");
		}
		
		if (!inmTribRec) {
			try {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_trib_rec NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_trib_rec", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				LOGGER.info("\tAlterFsMod184Detail2019. inm_trib_rec CREATED!");
			} catch (Exception e) {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_trib_rec NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterFsMod184Detail2019. inm_trib_rec EXISTS!");
		}
		
		if (!inmSaldDudCobr) {
			try {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_sald_dud_cobr NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_sald_dud_cobr", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				LOGGER.info("\tAlterFsMod184Detail2019. inm_sald_dud_cobr CREATED!");
			} catch (Exception e) {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_sald_dud_cobr NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterFsMod184Detail2019. inm_sald_dud_cobr EXISTS!");
		}

		if (!inmCantDevTer) {
			try {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_cant_dev_ter NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_cant_dev_ter", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				LOGGER.info("\tAlterFsMod184Detail2019. inm_cant_dev_ter CREATED!");
			} catch (Exception e) {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_cant_dev_ter NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterFsMod184Detail2019. inm_cant_dev_ter EXISTS!");
		}
		
		if (!inmPrimSeg) {
			try {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_prim_seg NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_prim_seg", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				LOGGER.info("\tAlterFsMod184Detail2019. inm_prim_seg CREATED!");
			} catch (Exception e) {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_prim_seg NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterFsMod184Detail2019. inm_prim_seg EXISTS!");
		}
		
		if (!inmAmort) {
			try {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_amort NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_amort", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				LOGGER.info("\tAlterFsMod184Detail2019. inm_amort CREATED!");
			} catch (Exception e) {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_amort NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterFsMod184Detail2019. inm_amort EXISTS!");
		}

		if (!inmAmortMueb) {
			try {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_amort_mueb NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_amort_mueb", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				LOGGER.info("\tAlterFsMod184Detail2019. inm_amort_mueb CREATED!");
			} catch (Exception e) {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_amort_mueb NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterFsMod184Detail2019. inm_amort_mueb EXISTS!");
		}
		
		if (!inmOtrGasDed) {
			try {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_otr_gas_ded NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_otr_gas_ded", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				LOGGER.info("\tAlterFsMod184Detail2019. inm_otr_gas_ded CREATED!");
			} catch (Exception e) {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_otr_gas_ded NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterFsMod184Detail2019. inm_otr_gas_ded EXISTS!");
		}

		if (!inmNumDiasArr) {
			try {
				LOGGER.info("\tAlterFsMod184Detail2019. inm_num_dias_arr NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_num_dias_arr", SQLDataType.INTEGER.nullable(true).defaultValue(0)).execute();
				LOGGER.info("\tAlterFsMod184Detail2019. inm_num_dias_arr CREATED!");
			} catch (Exception e) {
				LOGGER.warning("\tAlterFsMod184Detail2019. inm_num_dias_arr NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterFsMod184Detail2019. inm_num_dias_arr EXISTS!");
		}
	}

}

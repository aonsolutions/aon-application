package net.aonsolutions.db.up2date.fiscal;

import static com.esferalia.aon.jooq.tables.FsModel184Detail.FS_MODEL184_DETAIL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import net.aonsolutions.db.up2date.Update;

public class AlterFsMod184Detail2019 implements Update {

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

		boolean consumos_explotacion = false;
		boolean arrendamientos_canones = false;
		boolean reparacion_conservacion = false;
		boolean serv_prof_indep = false;
		boolean suministros = false;
		boolean gastos_financieros = false;
		boolean amortizaciones = false;
		boolean provisiones = false;
		boolean inm_int_fin = false;
		boolean inm_rep_con = false;
		boolean inm_gas_rep_con = false;
		boolean inm_trib_rec = false;
		boolean inm_sald_dud_cobr = false;
		boolean inm_cant_dev_ter = false;
		boolean inm_prim_seg = false;
		boolean inm_amort = false;
		boolean inm_amort_mueb = false;
		boolean inm_otr_gas_ded = false;
		boolean inm_num_dias_arr = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model184_detail");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);

				if ("consumos_explotacion".equals(name)) 	{consumos_explotacion = true;}
				if ("arrendamientos_canones".equals(name)) 	{arrendamientos_canones = true;}
				if ("reparacion_conservacion".equals(name)) {reparacion_conservacion = true;}
				if ("serv_prof_indep".equals(name)) 		{serv_prof_indep = true;}
				if ("suministros".equals(name)) 			{suministros = true;}
				if ("gastos_financieros".equals(name)) 		{gastos_financieros = true;}
				if ("amortizaciones".equals(name)) 			{amortizaciones = true;}
				if ("provisiones".equals(name)) 			{provisiones = true;}
				if ("inm_int_fin".equals(name)) 			{inm_int_fin = true;}
				if ("inm_rep_con".equals(name)) 			{inm_rep_con = true;}
				if ("inm_gas_rep_con".equals(name)) 		{inm_gas_rep_con = true;}
				if ("inm_trib_rec".equals(name)) 			{inm_trib_rec = true;}
				if ("inm_sald_dud_cobr".equals(name)) 		{inm_sald_dud_cobr = true;}
				if ("inm_cant_dev_ter".equals(name)) 		{inm_cant_dev_ter = true;}
				if ("inm_prim_seg".equals(name)) 			{inm_prim_seg = true;}
				if ("inm_amort".equals(name)) 				{inm_amort = true;}
				if ("inm_amort_mueb".equals(name)) 			{inm_amort_mueb = true;}
				if ("inm_otr_gas_ded".equals(name))	 		{inm_otr_gas_ded = true;}
				if ("inm_num_dias_arr".equals(name)) 		{inm_num_dias_arr = true;}
				
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
				}
			;
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
				}
			;
		}
		if (!consumos_explotacion) {
			try {
				System.out.println("\tAlterFsMod184Detail2019. consumos_explotacion NOT EXISTS!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("consumos_explotacion", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				System.out.println("\tAlterFsMod184Detail2019. consumos_explotacion CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2019. consumos_explotacion NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2019. consumos_explotacion EXISTS!");
		}
		
		if (!arrendamientos_canones) {
			try {
				System.out.println("\tAlterFsMod184Detail2019. arrendamientos_canones NOT EXISTS!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("arrendamientos_canones", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				System.out.println("\tAlterFsMod184Detail2019. arrendamientos_canones CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2019. arrendamientos_canones NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2019. arrendamientos_canones EXISTS!");
		}
		
		if (!reparacion_conservacion) {
			try {
				System.out.println("\tAlterFsMod184Detail2019. reparacion_conservacion NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("reparacion_conservacion", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				System.out.println("\tAlterFsMod184Detail2019. reparacion_conservacion CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2019. reparacion_conservacion NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2019. reparacion_conservacion EXISTS!");
		}
		
		if (!serv_prof_indep) {
			try {
				System.out.println("\tAlterFsMod184Detail2019. serv_prof_indep NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("serv_prof_indep", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				System.out.println("\tAlterFsMod184Detail2019. serv_prof_indep CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2019. serv_prof_indep NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2019. serv_prof_indep EXISTS!");
		}
		
		if (!suministros) {
			try {
				System.out.println("\tAlterFsMod184Detail2019. suministros NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("suministros", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				System.out.println("\tAlterFsMod184Detail2019. suministros CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2019. suministros NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2019. suministros EXISTS!");
		}

		if (!gastos_financieros) {
			try {
				System.out.println("\tAlterFsMod184Detail2019. gastos_financieros NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("gastos_financieros", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				System.out.println("\tAlterFsMod184Detail2019. gastos_financieros CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2019. gastos_financieros NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2019. gastos_financieros EXISTS!");
		}

		if (!amortizaciones) {
			try {
				System.out.println("\tAlterFsMod184Detail2019. amortizaciones NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("amortizaciones", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				System.out.println("\tAlterFsMod184Detail2019. amortizaciones CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2019. amortizaciones NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2019. amortizaciones EXISTS!");
		}

		if (!provisiones) {
			try {
				System.out.println("\tAlterFsMod184Detail2019. provisiones NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("provisiones", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				System.out.println("\tAlterFsMod184Detail2019. provisiones CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2019. provisiones NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2019. provisiones EXISTS!");
		}

		if (!inm_int_fin) {
			try {
				System.out.println("\tAlterFsMod184Detail2019. inm_int_fin NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_int_fin", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				System.out.println("\tAlterFsMod184Detail2019. inm_int_fin CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2019. inm_int_fin NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2019. inm_int_fin EXISTS!");
		}
		
		if (!inm_rep_con) {
			try {
				System.out.println("\tAlterFsMod184Detail2019. inm_rep_con NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_rep_con", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				System.out.println("\tAlterFsMod184Detail2019. inm_rep_con CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2019. inm_rep_con NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2019. inm_rep_con EXISTS!");
		}
		
		if (!inm_gas_rep_con) {
			try {
				System.out.println("\tAlterFsMod184Detail2019. inm_gas_rep_con NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_gas_rep_con", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				System.out.println("\tAlterFsMod184Detail2019. inm_gas_rep_con CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2019. inm_gas_rep_con NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2019. inm_gas_rep_con EXISTS!");
		}
		
		if (!inm_trib_rec) {
			try {
				System.out.println("\tAlterFsMod184Detail2019. inm_trib_rec NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_trib_rec", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				System.out.println("\tAlterFsMod184Detail2019. inm_trib_rec CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2019. inm_trib_rec NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2019. inm_trib_rec EXISTS!");
		}
		
		if (!inm_sald_dud_cobr) {
			try {
				System.out.println("\tAlterFsMod184Detail2019. inm_sald_dud_cobr NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_sald_dud_cobr", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				System.out.println("\tAlterFsMod184Detail2019. inm_sald_dud_cobr CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2019. inm_sald_dud_cobr NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2019. inm_sald_dud_cobr EXISTS!");
		}

		if (!inm_cant_dev_ter) {
			try {
				System.out.println("\tAlterFsMod184Detail2019. inm_cant_dev_ter NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_cant_dev_ter", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				System.out.println("\tAlterFsMod184Detail2019. inm_cant_dev_ter CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2019. inm_cant_dev_ter NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2019. inm_cant_dev_ter EXISTS!");
		}
		
		if (!inm_prim_seg) {
			try {
				System.out.println("\tAlterFsMod184Detail2019. inm_prim_seg NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_prim_seg", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				System.out.println("\tAlterFsMod184Detail2019. inm_prim_seg CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2019. inm_prim_seg NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2019. inm_prim_seg EXISTS!");
		}
		
		if (!inm_amort) {
			try {
				System.out.println("\tAlterFsMod184Detail2019. inm_amort NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_amort", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				System.out.println("\tAlterFsMod184Detail2019. inm_amort CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2019. inm_amort NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2019. inm_amort EXISTS!");
		}

		if (!inm_amort_mueb) {
			try {
				System.out.println("\tAlterFsMod184Detail2019. inm_amort_mueb NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_amort_mueb", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				System.out.println("\tAlterFsMod184Detail2019. inm_amort_mueb CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2019. inm_amort_mueb NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2019. inm_amort_mueb EXISTS!");
		}
		
		if (!inm_otr_gas_ded) {
			try {
				System.out.println("\tAlterFsMod184Detail2019. inm_otr_gas_ded NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_otr_gas_ded", SQLDataType.DOUBLE.nullable(true).defaultValue(0.0)).execute();
				System.out.println("\tAlterFsMod184Detail2019. inm_otr_gas_ded CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2019. inm_otr_gas_ded NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2019. inm_otr_gas_ded EXISTS!");
		}

		if (!inm_num_dias_arr) {
			try {
				System.out.println("\tAlterFsMod184Detail2019. inm_num_dias_arr NOT EXIST!");
				dslContext.alterTable(FS_MODEL184_DETAIL).addColumn("inm_num_dias_arr", SQLDataType.INTEGER.nullable(true).defaultValue(0)).execute();
				System.out.println("\tAlterFsMod184Detail2019. inm_num_dias_arr CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2019. inm_num_dias_arr NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2019. inm_num_dias_arr EXISTS!");
		}
	}

}

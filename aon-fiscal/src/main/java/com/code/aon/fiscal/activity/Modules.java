package com.code.aon.fiscal.activity;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;

public class Modules implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static String AEAT_DATABASE = "aeat/Modulos";
	private static String AEAT_DATABASE_URL = "jdbc:derby:classpath:"+AEAT_DATABASE;
	
	private static String SELECT_EPIGRAFES = "SELECT" 
 			+ " EPIGRAFE.SECTOR, EPIGRAFE.EPIGRAFE, EPIGRAFE.DESCRIPCION, SECTOR.DESCRIPCION"
			+ " FROM EPIGRAFE, SECTOR"
			+ " WHERE EPIGRAFE.SECTOR = ? "
			+ " AND EPIGRAFE.SECTOR = SECTOR.ID "; 
	
	private static String SELECT_CUOTAMIN = "SELECT" 
 			+ " SECTOR.CUOTAMIN"
			+ " FROM EPIGRAFE, SECTOR"
			+ " WHERE EPIGRAFE.EPIGRAFE = ? "
			+ " AND EPIGRAFE.SECTOR = SECTOR.ID "; 

	private static String SELECT_SECTORS = "SELECT " 
			+"ID"
			+",PERIOD"
			+",ADMON"
			+",SECTOR"
			+",DESCRIPCION"
			+",MOD_AGR"
			+",MOD_IVA"
			+",IRPF"
			+",IVA"
			+",IRPF_AGR"
			+",IVA_AGR"
			+",IVA_OP"
			+",CUOTAMIN"	
			+",MAX_IMPORT"
			+",MAX_PERSON"
			+",PORCENTAJE"
			+ " FROM APP.SECTOR ";
	
	private static String SELECT_NON_FARMER_SECTORS_WHERE = " WHERE MOD_AGR = 0" 
			+ " AND (IVA = 1 OR IRPF = 1)";
	private static String SELECT_FARMER_SECTORS_WHERE = " WHERE IVA_AGR = 1";
	
	private static String SELECT_NON_FARMER_ALL_SECTORS = SELECT_SECTORS + SELECT_NON_FARMER_SECTORS_WHERE; 
	private static String SELECT_FARMER_ALL_SECTORS = SELECT_SECTORS + SELECT_FARMER_SECTORS_WHERE; 

	private static String SECTORS_FILTER = " AND (SECTOR LIKE ? " 
			+ " OR UPPER(DESCRIPCION) LIKE ?)";
	
	private static String SELECT_SECTORS_ORDER = " ORDER BY SECTOR "; 

	private static String SELECT_NON_FARMER_SECTORS_UNFILTERED = SELECT_NON_FARMER_ALL_SECTORS + SELECT_SECTORS_ORDER;
	private static String SELECT_FARMER_SECTORS_UNFILTERED = SELECT_FARMER_ALL_SECTORS + SELECT_SECTORS_ORDER;

	private static String SELECT_NON_FARMER_SECTORS_FILTERED = SELECT_NON_FARMER_ALL_SECTORS + SECTORS_FILTER + SELECT_SECTORS_ORDER;
	private static String SELECT_FARMER_SECTORS_FILTERED = SELECT_FARMER_ALL_SECTORS + SECTORS_FILTER + SELECT_SECTORS_ORDER;

	private static String SELECT_IRPF_MODULES = "SELECT " 
			+ " MODULE.ID" 
			+" ,MODULE.DESCRIPCION" 
			+" ,MODULE.UNIDAD" 
			+" ,IRPFMOD.IMPORTE" 
			+" ,IRPFMOD.ORDENMOD" 
			+ " FROM IRPFMOD,MODULE" 
			+ " WHERE IRPFMOD.SECTOR = ?" 
			+ " AND MODULE.ID = IRPFMOD.MODULE"
			+ " ORDER BY IRPFMOD.ORDENMOD";

	private static String SELECT_IVA_MODULES = "SELECT " 
			+ " MODULE.ID" 
			+" ,MODULE.DESCRIPCION" 
			+" ,MODULE.UNIDAD" 
			+" ,IVAMOD.IMPORTE" 
			+" ,IVAMOD.ORDENMOD" 
			+ " FROM IVAMOD,MODULE" 
			+ " WHERE IVAMOD.SECTOR = ?" 
			+ " AND MODULE.ID = IVAMOD.MODULE"
			+ " ORDER BY IVAMOD.ORDENMOD";

	private Connection getConnection() throws ClassNotFoundException, SQLException {
		Connection conn = DriverManager.getConnection(AEAT_DATABASE_URL);
		return conn;
	}
	
	public List<Epigrafe> getEpigrafes(Integer sectorID) throws AonException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(SELECT_EPIGRAFES);
			stmt.setInt(1,sectorID);
			rs = stmt.executeQuery();
			List<Epigrafe> epigrafes = new LinkedList<Epigrafe>();
			while (rs.next()) {
				Epigrafe e = new Epigrafe();
				e.setSector(rs.getInt(1));
				e.setCode(rs.getString(2));
				String description = rs.getString(3);
				if (StringUtils.isBlank(description)) {
					description = rs.getString(4);	
				}
				e.setDescription(description);
				epigrafes.add(e);
			}
			return epigrafes;
		} catch (ClassNotFoundException e) {
			throw new AonException(e.getMessage(),e);
		} catch (SQLException e) {
			throw new AonException(e.getMessage(),e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(conn);
		}
	}
	
	public Double getCuotaMin(String epigrafe) throws AonException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(SELECT_CUOTAMIN);
			stmt.setString(1,epigrafe);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getDouble(1);
			}
			return 0.0;
		} catch (ClassNotFoundException e) {
			throw new AonException(e.getMessage(),e);
		} catch (SQLException e) {
			throw new AonException(e.getMessage(),e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(conn);
		}
	}

	public List<Sector> getSectors(boolean farmer) throws AonException {
		return getSectors(null,farmer);
	}

	public List<Sector> getSectors(String sug, boolean farmer) throws AonException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			if (StringUtils.isBlank(sug)) {
				stmt = conn.prepareStatement(farmer?SELECT_FARMER_SECTORS_UNFILTERED:SELECT_NON_FARMER_SECTORS_UNFILTERED );
			} else {
				stmt = conn.prepareStatement(farmer?SELECT_FARMER_SECTORS_FILTERED:SELECT_NON_FARMER_SECTORS_FILTERED);
				sug = StringUtils.upperCase(sug);
				stmt.setString(1, sug);
				stmt.setString(2, sug);
			}
			rs = stmt.executeQuery();
			List<Sector> list = new LinkedList<Sector>();
			while (rs.next()) {
				Sector i = new Sector();
				i.setId(rs.getInt(1));
				i.setPeriod(rs.getInt(2));
				i.setAdmon(rs.getInt(3));
				i.setSector(rs.getString(4));
				i.setDescription(rs.getString(5));
				i.setModAgr(rs.getBoolean(6));
				i.setModIva(rs.getBoolean(7));
				i.setIrpf(rs.getBoolean(8));
				i.setIva(rs.getBoolean(9));
				i.setIrpfAgr(rs.getBoolean(10));
				i.setIvaAgr(rs.getBoolean(11));
				i.setIvaOp(rs.getBoolean(12));
				i.setCuotamin(rs.getDouble(13));
				i.setMaxImport(rs.getDouble(14));
				i.setMaxPerson(rs.getDouble(15));
				i.setVatPercent(rs.getDouble(16));
				list.add(i);	
			}
			return list;
		} catch (ClassNotFoundException e) {
			throw new AonException(e.getMessage(),e);
		} catch (SQLException e) {
			throw new AonException(e.getMessage(),e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(conn);
		}
	}

	public List<Mod> getIRPFMods(Epigrafe epigraph) throws AonException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(SELECT_IRPF_MODULES);
			stmt.setInt(1, epigraph.getSector());
			rs = stmt.executeQuery();
			List<Mod> list = new LinkedList<Mod>();
			while (rs.next()) {
				Mod m = new Mod();
				m.setId(rs.getInt(1));
				m.setDescription(rs.getString(2));
				m.setUnit(rs.getString(3));
				m.setFactor(rs.getDouble(4));
				m.setLine(rs.getInt(5));
				list.add(m);	
			}
			return list;
		} catch (ClassNotFoundException e) {
			throw new AonException(e.getMessage(),e);
		} catch (SQLException e) {
			throw new AonException(e.getMessage(),e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(conn);
		}
	}

	public List<Mod> getIVAMods(Epigrafe epigraph) throws AonException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(SELECT_IVA_MODULES);
			stmt.setInt(1, epigraph.getSector());
			rs = stmt.executeQuery();
			List<Mod> list = new LinkedList<Mod>();
			while (rs.next()) {
				Mod m = new Mod();
				m.setId(rs.getInt(1));
				m.setDescription(rs.getString(2));
				m.setUnit(rs.getString(3));
				m.setFactor(rs.getDouble(4));
				m.setLine(rs.getInt(5));
				list.add(m);	
			}
			return list;
		} catch (ClassNotFoundException e) {
			throw new AonException(e.getMessage(),e);
		} catch (SQLException e) {
			throw new AonException(e.getMessage(),e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(conn);
		}
	}
	
	public double getFarmerQuota(int year, int activity) {
		if (year >= 2015) {
			if (activity == 1) {
				//01	Ganaderia intensiva porcino carne y avicultura carne.
				return 0.10;
			} else if (activity == 2) {
			 	//02	Avicultura de huevos y ovino, caprino y bovino leche.
				return 0.04;
			} else if (activity == 3) {
			 	//03	Ganaderia intensiva de bovino de carne y cunicultura.
				return 0.10;
			} else if (activity == 4) {
				//04	Ganad. intensiva ganado porcino y bovino de cria y otras.
				return 0.10;
			} else if (activity == 5) {
				//05	Ganaderia intensiva de ovino y caprino de carne.
				return 0.10;
			} else if (activity == 6) {
				//06	Servicios de cria, guarda y engorde de aves.
				return 0.06625;
			} else if (activity == 7) {
				//07	Apicultura
				return 0.070;
			} else if (activity == 8) {
				//08	Trabajos y servicios accesorios excluidos R.E.A.G.P.
				return 0.10;
			} else if (activity == 9) {
				//09	Act. accesorias no incluidas en el R.E.A.G.P.
				return 0.21;
			} else if (activity == 10) {
				//10	Aparceria(Aprov.cedente) prod.agric.distintos a los ss.
				return 0.04;
			} else if (activity == 11) {
				//11	Aparceria obtencion de forrajes:Aprov. cedente.
				return 0.07625;
			} else if (activity == 12) {
				//12	Aparceria(Aprov.cedente) obt. plantas textiles y tabaco.
				return  0.21;
			} else if (activity == 13) {
				//13	Aparceria (Aprovch. cedente) de act. forestales.
				return 0.21;
			} else if (activity == 14) {
				//14	Procesos de transf. para obtencion de queso
				return 0.070;
			} else if (activity == 15) {
				//15	Procesos de transf. para obtencion de vino mesa.
				return 0.2675;
			} else if (activity == 16) {
				//16	Procesos de transf. para obtencion de vino con D.O.
				return 0.2675;
			} else if (activity == 17) {
				//17	Procesos de transf. para obtencion de otros productos
				return 0.19625;
			}
			throw new IllegalArgumentException("Actividad agr�cola no soportada");
		}
		throw new IllegalArgumentException("Ejercicio no soportado");
	}
	
	public static void main(String[] args) throws AonException {
		Modules modules = new Modules();
		for (Sector sector : modules.getSectors(false)) {
			for (Epigrafe epi: modules.getEpigrafes(sector.getId())) {
				
				if ("972.2".equals(epi.getCode())) {
					System.out.println(  
						" - " + sector.getVatPercent() 
						+" - " + sector.getCuotamin()
						+" - " + sector.getMaxPerson()
						+" - " + sector.getMaxImport()
					
	);
					System.out.println( "SECTOR ..: " + epi.getSector() );
					for (Mod mod: modules.getIRPFMods(epi)) {
						System.out.println( "IRPF: " + mod.getId() );	
					}
					for (Mod mod: modules.getIVAMods(epi)) {
						System.out.println( "IVA : " + mod.getId() );	
					}
				}
			}
		}
		
	}
}

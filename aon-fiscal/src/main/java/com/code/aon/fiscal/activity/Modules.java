package com.code.aon.fiscal.activity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;

public class Modules {
	
	private static String AEAT_DATABASE = "aeat/Modulos";
	private static String AEAT_DATABASE_URL = "jdbc:derby:classpath:"+AEAT_DATABASE;
	
	private static String SELECT_EPIGRAFES = "SELECT" 
 			+ " SECTOR, EPIGRAFE, DESCRIPCION"
			+ " FROM EPIGRAFE"
			+ " WHERE EPIGRAFE.SECTOR = ? "; 
	
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
	private static String SELECT_ALL_SECTORS_WHERE = " WHERE MOD_AGR = 0" 
			+ " AND (IVA = 1 OR IRPF = 1)";
	
	private static String SELECT_ALL_SECTORS = SELECT_SECTORS + SELECT_ALL_SECTORS_WHERE; 

	private static String SELECT_SECTORS_FILTERED = SELECT_ALL_SECTORS
			+ " AND (SECTOR LIKE ? " 
			+ " OR UPPER(DESCRIPCION) LIKE ?)";
	
	private static String SELECT_SECTORS_ORDER = " ORDER BY SECTOR "; 

	private static String SELECT_SECTORS_UNFILTERED = SELECT_ALL_SECTORS + SELECT_SECTORS_ORDER;
	
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
		Class.forName(org.apache.derby.jdbc.EmbeddedDriver.class.getName());
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
				e.setDescription(rs.getString(3));
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
	
	public List<Sector> getSectors() throws AonException {
		return getSectors(null);
	}

	public List<Sector> getSectors(String sug) throws AonException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			if (StringUtils.isBlank(sug)) {
				stmt = conn.prepareStatement(SELECT_SECTORS_UNFILTERED);	
			} else {
				stmt = conn.prepareStatement(SELECT_SECTORS_FILTERED);
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

	
	public static void main(String[] args) throws AonException {
		Modules m = new Modules();
		
		m.getSectors();
	} 
}

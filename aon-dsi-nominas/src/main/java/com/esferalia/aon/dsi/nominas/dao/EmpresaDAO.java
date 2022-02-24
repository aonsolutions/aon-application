package com.esferalia.aon.dsi.nominas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.LinkedList;

import com.esferalia.aon.dsi.nominas.model.Antiguedad;
import com.esferalia.aon.dsi.nominas.model.Banco;
import com.esferalia.aon.dsi.nominas.model.Centro;
import com.esferalia.aon.dsi.nominas.model.Complemento;
import com.esferalia.aon.dsi.nominas.model.Concepto;
import com.esferalia.aon.dsi.nominas.model.Empresa;
import com.esferalia.aon.dsi.nominas.model.Paga;
import com.esferalia.aon.watson.util.AonStringUtils;

public class EmpresaDAO {

	// Devuelve las empresas de Nominas Omega que no tienen fecha de baja
	public static LinkedList<Empresa> select(Connection dsiConn) throws SQLException {

		try (final Statement statement = dsiConn.createStatement()) {
			LinkedList<Empresa> empresas = new LinkedList<Empresa>();
			String sql = "SELECT * FROM FNEMPRES WHERE F20FBAJA IS NULL";
			try (final ResultSet rs = statement.executeQuery(sql)) {
				while (rs.next()) {
						empresas.add(new Empresa()
							.setSscod(rs.getString("F20SSCOD"))
							.setSsnum(rs.getString("F20SSNUM"))
							.setSsctrl(rs.getString("F20SSCTRL"))
							.setNif(rs.getString("F20NIF"))
							.setTipo(rs.getString("F20TIPO"))
							.setSscodp(rs.getString("F20SSCODP"))
							.setSsnump(rs.getString("F20SSNUMP"))
							.setSsctrp(rs.getString("F20SSCTRP"))
							.setCotatep(rs.getString("F20COTATEP"))
							.setRsocial(rs.getString("F20RSOCIAL"))
							.setSg(rs.getString("F20SG"))
							.setDomicil(rs.getString("F20DOMICIL"))
							.setNumero(rs.getString("F20NUMERO"))
							.setEscaler(rs.getString("F20ESCALER"))
							.setPiso(rs.getString("F20PISO"))
							.setPuerta(rs.getString("F20PUERTA"))
							.setCp(rs.getString("F20CP"))
							.setPoblaci(rs.getString("F20POBLACI"))
							.setProvin(rs.getString("F20PROVIN"))
							.setTelef(rs.getString("F20TELEF"))
							.setFalta(rs.getDate("F20FALTA"))
							.setFbaja(rs.getDate("F20FBAJA"))
							.setConven(rs.getString("F20CONVEN"))
							.setActiv(rs.getString("F20ACTIV"))
							.setNomact(rs.getString("F20NOMACT"))
							.setLicfis(rs.getString("F20LICFIS"))
							.setEntiat(rs.getString("F20ENTIAT"))
							.setCodadm(rs.getString("F20CODADM"))
							.setNomlab(rs.getString("F20NOMLAB"))
							.setNiflab(rs.getString("F20NIFLAB"))
							.setNomfis(rs.getString("F20NOMFIS"))
							.setNiffis(rs.getString("F20NIFFIS"))
							.setCalend(rs.getString("F20CALEND"))
							.setCtasegs(rs.getString("F20CTASEGS"))
							.setCtassac(rs.getString("F20CTASSAC"))
							.setCtahacp(rs.getString("F20CTAHACP"))
							.setCtacaja(rs.getString("F20CTACAJA"))
							.setCtasuel(rs.getString("F20CTASUEL"))
							.setCtaanti(rs.getString("F20CTAANTI"))
							.setCtapend(rs.getString("F20CTAPEND"))
							.setTipoAntig(rs.getString("F20ANTIG2"))
							.setTipoComple(rs.getString("F20COMPLE"))
							.setConceptos(getConceptos(dsiConn, rs.getString("F20SSCOD"), rs.getString("F20SSNUM")))
							.setPagas(getPagas(dsiConn, rs.getString("F20SSCOD"), rs.getString("F20SSNUM")))
							.setAntiguedades(getAntiguedades(dsiConn, rs.getString("F20SSCOD"), rs.getString("F20SSNUM")))
							.setComplementos(getComplementos(dsiConn, rs.getString("F20SSCOD"), rs.getString("F20SSNUM")))
							.setBancos(getBancos(dsiConn, rs.getString("F20SSCOD"), rs.getString("F20SSNUM")))
							.setCentros(getCentros(dsiConn, rs.getString("F20SSCOD"), rs.getString("F20SSNUM")))
							);
				}
			}
			
			// ME ESTOY ENCONTRANDO DATOS CON CCC PRINCIPAL RELLENO CON EL MISMO NUMERO QUE EL CCC, EN ESTOS CASOS EL CCC PRAL DEBERIA ESTAR EN BLANCO 
			// PARA QUE FUERA UNA DE LAS PRIMERAS QUE SE LEE. POR ESO NO SE HACE UN ORDER BY EN LA CONSULTA SQL 
			
			// Ordenar por CIF + CCC_PRINCIPAL (si es distinto del CCC)
			empresas.sort(new Comparator<Empresa>() {

				@Override
				public int compare(Empresa o1, Empresa o2) {					 
					String c1 = AonStringUtils.trimToEmpty(o1.getNif());
					if (!AonStringUtils.equals(o1.getSscod(), o1.getSscodp()) && !AonStringUtils.equals(o1.getSsnum(), o1.getSsnump())) {
						c1 = c1 + AonStringUtils.trimToEmpty(o1.getSscodp()) + AonStringUtils.trimToEmpty(o1.getSsnump());
					}
					
					String c2 = AonStringUtils.trimToEmpty(o2.getNif());
					if (!AonStringUtils.equals(o2.getSscod(), o2.getSscodp()) && !AonStringUtils.equals(o2.getSsnum(), o2.getSsnump())) {
						c2 = c2 + AonStringUtils.trimToEmpty(o2.getSscodp()) + AonStringUtils.trimToEmpty(o2.getSsnump());						
					}
					
					return AonStringUtils.compare(c1, c2);
				}
				
			});
			
			return empresas;
		}
	}
	
	// Devuelve todos los conceptos de la empresa que se le pasa
	private static LinkedList<Concepto> getConceptos(Connection dsiConn, String ssCodEmp, String ssNumEmp) throws SQLException {
		
		String sql = "SELECT * FROM FNECONCE WHERE F21SSCOD = ? AND F21SSNUM = ? ORDER BY F21NORDEN";
		try (final PreparedStatement prepareStatement = dsiConn.prepareStatement(sql)) {
			LinkedList<Concepto> conceptos = new LinkedList<Concepto>();
			
			prepareStatement.setString(1, ssCodEmp);
			prepareStatement.setString(2, ssNumEmp);
						
            try (final ResultSet rs = prepareStatement.executeQuery()) {
                while (rs.next()) {    
                	if (AonStringUtils.isNotBlank(rs.getString("F21CLAVE")))
                		conceptos.add(new Concepto()                    		
			                    .setClave(rs.getString("F21CLAVE"))
			                	.setNombre(rs.getString("F21NOMBRE"))
			                	.setSs(rs.getString("F21SEGSOC"))
			                	.setIrpf(rs.getString("F21IRPF"))
			                	.setPag(rs.getString("F21PAGAS"))
			                	.setEnf(rs.getString("F21ENFER"))
			                	.setAcc(rs.getString("F21ACCID"))
			                	.setTipo(rs.getString("F21TIPOCON"))
			                	.setClaveCRA(rs.getString("F21CLAVECRA")));
                }                
            }
            return conceptos;
        }
		
	}
	
	// Devuelve todas las pagas extras de la empresa que se le pasa
	private static LinkedList<Paga> getPagas(Connection dsiConn, String ssCodEmp, String ssNumEmp) throws SQLException {
		
		String sql = "SELECT * FROM FNEPAGAS WHERE F21SSCOD = ? AND F21SSNUM = ? ORDER BY F21NORDEN";
		try (final PreparedStatement prepareStatement = dsiConn.prepareStatement(sql)) {
			LinkedList<Paga> pagas = new LinkedList<Paga>();
			
			prepareStatement.setString(1, ssCodEmp);
			prepareStatement.setString(2, ssNumEmp);
						
            try (final ResultSet rs = prepareStatement.executeQuery()) {
                while (rs.next()) {
                	if (AonStringUtils.isNotBlank(rs.getString("F21MES")))
                		pagas.add(new Paga()
                    		.setMes(rs.getString("F21MES"))
		                	.setImporte(rs.getDouble("F21VALOR"))
		                	.setDescripcion(rs.getString("F21DESCRI"))
		                	.setDiaInicio(rs.getString("F21DIADES"))
		                	.setMesInicio(rs.getString("F21MESDES"))
		                	.setAnoInicio(rs.getString("F21ANNODES")) 
		                	.setDiaFin(rs.getString("F21DIAHAS"))
		                	.setMesFin(rs.getString("F21MESHAS"))
		                	.setAnoFin(rs.getString("F21ANNOHAS"))
		                	.setTipo(rs.getString("F21TIPO")));
                }                
            }
            return pagas;
        }
		
	}
	
	// Devuelve toda la tabla de antigüedad de la empresa que se le pasa
	private static LinkedList<Antiguedad> getAntiguedades(Connection dsiConn, String ssCodEmp, String ssNumEmp) throws SQLException {
		
		String sql = "SELECT * FROM FNEANTIG WHERE F21SSCOD = ? AND F21SSNUM = ? ORDER BY F21ANNOS";
		try (final PreparedStatement prepareStatement = dsiConn.prepareStatement(sql)) {
			LinkedList<Antiguedad> antiguedades = new LinkedList<Antiguedad>();
			
			prepareStatement.setString(1, ssCodEmp);
			prepareStatement.setString(2, ssNumEmp);
						
            try (final ResultSet rs = prepareStatement.executeQuery()) {
                while (rs.next()) {                  
                	if (AonStringUtils.isNotBlank(rs.getString("F21ANNOS")))
                		antiguedades.add(new Antiguedad()                    		
		                    		.setAnos(rs.getString("F21ANNOS"))
		                    		.setImporte(rs.getDouble("F21VALOR"))
				                	.setTipo(rs.getString("F21TIPO")));
                }                
            }
            return antiguedades;
        }
		
	}
	
	// Devuelve todos los complementos por enfermedad/accidente de la empresa que se le pasa
	private static LinkedList<Complemento> getComplementos(Connection dsiConn, String ssCodEmp, String ssNumEmp) throws SQLException {
		
		String sql = "SELECT * FROM FNECOMPL WHERE F21SSCOD = ? AND F21SSNUM = ? ORDER BY F21NORDEN";
		try (final PreparedStatement prepareStatement = dsiConn.prepareStatement(sql)) {
			LinkedList<Complemento> complementos = new LinkedList<Complemento>();
			
			prepareStatement.setString(1, ssCodEmp);
			prepareStatement.setString(2, ssNumEmp);
						
            try (final ResultSet rs = prepareStatement.executeQuery()) {
                while (rs.next()) {
                	if (AonStringUtils.isNotBlank(rs.getString("F21TIPO")))
                		complementos.add(new Complemento()
		                    		.setTipo(rs.getString("F21TIPO"))
		                    		.setPorcentaje(rs.getInt("F21VALOR"))
		                    		.setDiaDesde(rs.getInt("F21DIADES"))
		                    		.setDiaHasta(rs.getInt("F21DIAHAS"))
				                	.setCalculo(rs.getString("F21CONCEP")));
                }                
            }
            return complementos;
        }
		
	}
	
	// Devuelve todos los bancos de la empresa que se le pasa
	private static LinkedList<Banco> getBancos(Connection dsiConn, String ssCodEmp, String ssNumEmp) throws SQLException {
		
		String sql = "SELECT * FROM FNEBANCO WHERE F21SSCOD = ? AND F21SSNUM = ? ORDER BY F21NORDEN";
		try (final PreparedStatement prepareStatement = dsiConn.prepareStatement(sql)) {
			LinkedList<Banco> bancos = new LinkedList<Banco>();
			
			prepareStatement.setString(1, ssCodEmp);
			prepareStatement.setString(2, ssNumEmp);
						
            try (final ResultSet rs = prepareStatement.executeQuery()) {
                while (rs.next()) {
                	if (AonStringUtils.isNotBlank(rs.getString("F21CODIGO")))
                		bancos.add(new Banco()
	                    		.setCodigo(rs.getString("F21CODIGO"))
	                    		.setNombre(rs.getString("F21AGENCIA"))
	                    		.setIban(rs.getString("F21IBAN"))
	                    		.setBic(rs.getString("F21BIC"))
	                    		.setSufijo(rs.getString("F21SUFIJO"))	                    		
			                	.setCuentaContable(rs.getString("F21CONTA")));
                }                
            }
            return bancos;
        }
		
	}
	
	// Devuelve todos los centros de la empresa que se le pasa
	private static LinkedList<Centro> getCentros(Connection dsiConn, String ssCodEmp, String ssNumEmp) throws SQLException {
		
		String sql = "SELECT * FROM FNECENTR WHERE F21SSCOD = ? AND F21SSNUM = ? ORDER BY F21NORDEN";
		try (final PreparedStatement prepareStatement = dsiConn.prepareStatement(sql)) {
			LinkedList<Centro> centros = new LinkedList<Centro>();
			
			prepareStatement.setString(1, ssCodEmp);
			prepareStatement.setString(2, ssNumEmp);
						
            try (final ResultSet rs = prepareStatement.executeQuery()) {
                while (rs.next()) {
                	if (AonStringUtils.isNotBlank(rs.getString("F21CODIGO")))
                		centros.add(new Centro()
	                    		.setCodigo(rs.getString("F21CODIGO"))
	                    		.setSituacion(rs.getString("F21SITUACI")));
                }                
            }
            return centros;
        }
		
	}
	

}



package com.esferalia.aon.dsi.nominas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

//import static com.esferalia.aon.dsi.dao.Conexion.dsiConn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.dsi.nominas.model.Antiguedad;
import com.esferalia.aon.dsi.nominas.model.Ascendiente;
import com.esferalia.aon.dsi.nominas.model.Concepto;
import com.esferalia.aon.dsi.nominas.model.Descendiente;
import com.esferalia.aon.dsi.nominas.model.Paga;
import com.esferalia.aon.dsi.nominas.model.Trabajador;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class TrabajadorDAO {
	
	// Solo se traspasan los trabajadores sin fecha de baja, o fecha de baja >= 01/01/2021
	private static final Date MIN_FECHA_BAJA = AonDateUtils.getDate(2021, 0, 1);
	
	// Devuelve los trabajadores de la empresa que se le pasa. 
	// Solo los que no tienen fecha de baja o los que la fecha de baja es mayor o igual al 01/01/2021.	 
	public static LinkedList<Trabajador> select(Connection dsiConn, String ssCodEmp, String ssNumEmp) throws SQLException {
		
		String sql = "SELECT * FROM FNTRABAJ AS T "
				+ "LEFT JOIN FNTRABA2 AS T2 ON T2.F20SSCODEM=T.F20SSCODEM AND T2.F20SSNUMEM=T.F20SSNUMEM AND T2.F20SSCOD=T.F20SSCOD AND T2.F20SSNUM=T.F20SSNUM AND T2.F20FALTA=T.F20FALTA "                    
				+ "WHERE T.F20SSCODEM = ? AND T.F20SSNUMEM = ? AND (F20FBAJA IS NULL OR F20FBAJA >= ?) "
				+ "ORDER BY T.F20DNI, T.F20FALTA"; 
		
		try (final PreparedStatement preparedStatement = dsiConn.prepareStatement(sql)) {
			LinkedList<Trabajador> trabajadores = new LinkedList<Trabajador>();
			
			preparedStatement.setString(1, ssCodEmp);
			preparedStatement.setString(2, ssNumEmp);			
			preparedStatement.setDate(3, AonDateUtils.toSql(MIN_FECHA_BAJA));
			
			try (final ResultSet rs = preparedStatement.executeQuery()) {
				while (rs.next()) {									
						Trabajador trabajador = new Trabajador()
										.setSscod(rs.getString("F20SSCOD"))
										.setSsnum(rs.getString("F20SSNUM"))
										.setSsctrl(rs.getString("F20SSCTRL"))
										.setFalta(rs.getDate("F20FALTA"))
										.setFbaja(rs.getDate("F20FBAJA"))
										.setApell1(rs.getString("F20APELL1"))
										.setApell2(rs.getString("F20APELL2"))
										.setNombre(rs.getString("F20NOMBRE"))
										.setDni(rs.getString("F20DNI"))
										.setIdentif(rs.getString("F20IDENTIF"))
										.setPaisemi(rs.getString("F20PAISEMI"))
										.setSg(rs.getString("F20SG"))
										.setDirecci(rs.getString("F20DIRECCI"))
										.setNumero(rs.getString("F20NUMERO"))
										.setEsc(rs.getString("F20ESC"))
										.setPiso(rs.getString("F20PISO"))
										.setPuerta(rs.getString("F20PUERTA"))
										.setCp(rs.getString("F20CP"))
										.setPoblaci(rs.getString("F20POBLACI"))
										.setProvin(rs.getString("F20PROVIN"))
										.setTelef(rs.getString("F20TELEF"))
										.setSexo(rs.getString("F20SEXO"))
										.setEcivil2(rs.getString("F20ECIVIL2"))
										.setFnac(rs.getDate("F20FNAC"))
										.setNaciona(rs.getString("F20NACIONA"))
										.setFantig(rs.getDate("F20FANTIG"))
										.setBanco(rs.getString("F20BANCO"))
										.setIban(rs.getString("F20IBAN"))  // T2
										.setBic(rs.getString("F20BIC"))    // T2
										.setTalon(rs.getString("F20TALON"))
										.setTransf(rs.getString("F20TRANSF"))
										.setConven(rs.getString("F20CONVEN"))
										.setCateg(rs.getString("F20CATEG"))
										.setNomcat(rs.getString("F20NOMCAT"))
										.setPorcen(rs.getDouble("F20PORCEN"))
										.setGrupo(rs.getString("F20GRUPO"))
										.setMatric(rs.getString("F20MATRIC"))
										.setPuesto(rs.getString("F20PUESTO"))
										.setCentro(rs.getString("F20CENTRO"))
										.setCalend(rs.getString("F20CALEND"))
										.setClcto(rs.getString("F20CLCTO"))
										.setFfincto(rs.getDate("F20FFINCTO"))
										.setRetrib(rs.getString("F20RETRIB"))
										.setCotatep(rs.getString("F20COTATEP"))
										.setDias(rs.getDouble("F20DIAS"))
										.setHoras(rs.getDouble("F20HORAS"))
										.setDiastra(rs.getString("F20DIASTRA"))
										.setReamodcot(rs.getString("F20REAMODCOT"))
										.setAgrdes(rs.getString("F20AGRDES"))
										.setAutono(rs.getString("F20AUTONO"))
										.setSocio(rs.getString("F20SOCIO"))
										.setMculto(rs.getString("F20MCULTO"))
										.setPfrdl1493(rs.getString("F20PFRDL1493"))  // T2
										.setCespsol(rs.getString("F20CESPSOL"))  // T2
										.setExcldes(rs.getString("F20EXCLDES"))  // T2
										.setExclfgs(rs.getString("F20EXCLFGS"))  // T2
										.setExclfp(rs.getString("F20EXCLFP"))  // T2
										.setHorasl(rs.getDouble("F20HORASL"))  // T2
										.setHorasm(rs.getDouble("F20HORASM"))  // T2
										.setHorasx(rs.getDouble("F20HORASX"))  // T2
										.setHorasj(rs.getDouble("F20HORASJ"))  // T2
										.setHorasv(rs.getDouble("F20HORASV"))  // T2
										.setHorass(rs.getDouble("F20HORASS"))  // T2
										.setHorasd(rs.getDouble("F20HORASD"))  // T2
										.setBonif65(rs.getString("F20BONIF65"))
										.setFini65(rs.getDate("F20FINI65"))
										.setSngj(rs.getString("F20SNGJ"))  // T2
										.setEcivil(rs.getString("F20ECIVIL"))
										.setNifcony(rs.getString("F20NIFCONY"))
										.setGradomi(rs.getString("F20GRADOMI"))
										.setAyudami(rs.getString("F20AYUDAMI"))
										.setRedmovi(rs.getString("F20REDMOVI"))
										.setFtrasl(rs.getDate("F20FTRASL"))  // T2
										.setRedacti(rs.getString("F20REDACTI"))
										.setRedcopa(rs.getString("F20REDCOPA"))
										.setPresviv(rs.getString("F20PRESVIV"))
										.setCeuta(rs.getString("F20CEUTA"))
										.setRelaesp(rs.getString("F20RELAESP"))
										.setRedar17(rs.getDouble("F20REDAR17"))
										.setRedar183(rs.getDouble("F20REDAR183"))  // T2
										.setPension(rs.getDouble("F20PENSION"))
										.setAnualid(rs.getDouble("F20ANUALID"))
										.setIrpfijo(rs.getString("F20IRPFIJO"))
										.setIrpf(rs.getDouble("F20IRPF"))
										.setConceptos(getConceptos(dsiConn, rs.getString("F20SSCODEM"), rs.getString("F20SSNUMEM"), rs.getString("F20SSCOD"), rs.getString("F20SSNUM"), rs.getDate("F20FALTA")))
										.setPagas(getPagas(dsiConn, rs.getString("F20SSCODEM"), rs.getString("F20SSNUMEM"), rs.getString("F20SSCOD"), rs.getString("F20SSNUM"), rs.getDate("F20FALTA")))
										.setAntiguedades(getAntiguedades(dsiConn, rs.getString("F20SSCODEM"), rs.getString("F20SSNUMEM"), rs.getString("F20SSCOD"), rs.getString("F20SSNUM"), rs.getDate("F20FALTA")));
						
						// Descendientes
						for (int i = 1; i <= 8; i++) {
							if (AonStringUtils.isNotBlank(rs.getString("F20HIJO"+i+"AN"))) {
								trabajador.getDescendientes().add( new Descendiente()
										.setAn(rs.getString("F20HIJO"+i+"AN"))
										.setAd(rs.getString("F20HIJO"+i+"AD"))
										.setEn(rs.getString("F20HIJO"+i+"EN"))
										.setMi(rs.getString("F20HIJO"+i+"MI"))
										.setMr(rs.getString("F20HIJO"+i+"MR")));
							}							
						}
						
						// Ascendientes
						for (int i = 1; i <= 4; i++) {
							if (AonStringUtils.isNotBlank(rs.getString("F20ASCE"+i+"AN"))) {
								trabajador.getAscendientes().add( new Ascendiente()
										.setAn(rs.getString("F20ASCE"+i+"AN"))
										.setCo(rs.getString("F20ASCE"+i+"CO"))
										.setMi(rs.getString("F20ASCE"+i+"MI"))
										.setMr(rs.getString("F20ASCE"+i+"MR")));
							}							
						}
						
						trabajadores.add(trabajador);								
				}
			}
			return trabajadores;
		}
		
	}
	
	// Devuelve todos los conceptos del trabajador que se le pasa
	private static LinkedList<Concepto> getConceptos(Connection dsiConn, String ssCodEmp, String ssNumEmp, String ssCodTra, String ssNumTra, Date fechaAlta) throws SQLException {
		
		String sql = "SELECT * FROM FNTCONCE WHERE F21SSCODEM = ? AND F21SSNUMEM = ? AND F21SSCOD = ? AND F21SSNUM = ? AND F21FALTA = ? ORDER BY F21NORDEN";
		try (final PreparedStatement prepareStatement = dsiConn.prepareStatement(sql)) {
			LinkedList<Concepto> conceptos = new LinkedList<Concepto>();
			
			prepareStatement.setString(1, ssCodEmp);
			prepareStatement.setString(2, ssNumEmp);
			prepareStatement.setString(3, ssCodTra);
			prepareStatement.setString(4, ssNumTra);
			prepareStatement.setDate(5, AonDateUtils.toSql(fechaAlta));
						
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
			                	.setClaveCRA(rs.getString("F21CLAVECRA"))
			                	.setImporte(rs.getDouble("F21IMPOR"))
			                	.setCobro(rs.getString("F21TIPO"))
			                	.setUnidades(rs.getDouble("F21UNIDADE")));
                }                
            }
            return conceptos;
        }
		
	}
	
	// Devuelve todas las pagas extras del trabajador y categoria que se le pasa
	private static LinkedList<Paga> getPagas(Connection dsiConn, String ssCodEmp, String ssNumEmp, String ssCodTra, String ssNumTra, Date fechaAlta) throws SQLException {
		
		String sql = "SELECT * FROM FNTPAGAS WHERE F21SSCODEM = ? AND F21SSNUMEM = ? AND F21SSCOD = ? AND F21SSNUM = ? AND F21FALTA = ? ORDER BY F21NORDEN";
		try (final PreparedStatement prepareStatement = dsiConn.prepareStatement(sql)) {
			LinkedList<Paga> pagas = new LinkedList<Paga>();
			
			prepareStatement.setString(1, ssCodEmp);
			prepareStatement.setString(2, ssNumEmp);
			prepareStatement.setString(3, ssCodTra);
			prepareStatement.setString(4, ssNumTra);
			prepareStatement.setDate(5, AonDateUtils.toSql(fechaAlta));
						
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
	
	// Devuelve toda la tabla de antigüedad del trabajador y categoria que se le pasa
	private static LinkedList<Antiguedad> getAntiguedades(Connection dsiConn, String ssCodEmp, String ssNumEmp, String ssCodTra, String ssNumTra, Date fechaAlta) throws SQLException {
		
		String sql = "SELECT * FROM FNTANTIG WHERE F21SSCODEM = ? AND F21SSNUMEM = ? AND F21SSCOD = ? AND F21SSNUM = ? AND F21FALTA = ? ORDER BY F21ANNOS";
		try (final PreparedStatement prepareStatement = dsiConn.prepareStatement(sql)) {
			LinkedList<Antiguedad> antiguedades = new LinkedList<Antiguedad>();
			
			prepareStatement.setString(1, ssCodEmp);
			prepareStatement.setString(2, ssNumEmp);
			prepareStatement.setString(3, ssCodTra);
			prepareStatement.setString(4, ssNumTra);
			prepareStatement.setDate(5, AonDateUtils.toSql(fechaAlta));
						
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

}

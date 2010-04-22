package com.code.aon.finance.model347;


import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.enumeration.Model347ReportOrder;
import com.code.aon.finance.enumeration.Model347Type;
import com.code.aon.registry.RegistryDocument;

public class Model347CollectionProvider {

	public List<Model347> getList(Model347Parameters params) throws ManagerBeanException {
		PreparedStatement ps = null; 
		ResultSet rs = null;
		try {
			StringWriter stmt = new StringWriter();
			stmt.append("SELECT ELT(i.type+1, 'A', 'B', 'A') key347 ");
			stmt.append(",i.registry ");
			stmt.append(",i.rdocument ");
			stmt.append(",i.rname ");
			stmt.append(",IF(giz.id IS NOT null,giz.id,gz.id) geozone ");
			stmt.append(",IF(giz.name IS NOT null,giz.name,gz.name) geozoneName ");
			stmt.append(",IF(giz2.id IS NOT null,giz2.id,gz2.id) country ");
			stmt.append(",IF(giz2.name IS NOT null,giz2.name,gz2.name) countryName ");
			stmt.append(",SUM( ");
			stmt.append("id.taxable_base ");
			stmt.append("+ ( ");
			stmt.append("IF(it.quota=0, ");
			stmt.append("ROUND(it.percentage * id.taxable_base / 100,2) ");
			stmt.append(",IF(it.quota is NULL,0,it.quota)) ");
			stmt.append("+ ");
			stmt.append("IF( ");
			stmt.append("it.surcharge_quota=0, ");
			stmt.append("ROUND(it.surcharge * id.taxable_base / 100,2) ");
			stmt.append(",IF(it.surcharge_quota is NULL,0,it.surcharge_quota) ");
			stmt.append("))) total ");
			stmt.append("FROM invoice_detail id ");
			stmt.append("INNER JOIN invoice i ON (id.invoice = i.id) ");
			stmt.append("LEFT OUTER JOIN invoice_tax it ON it.invoice_detail = id.id ");
			stmt.append("LEFT OUTER JOIN invoice_address ia ON ia.invoice = i.id ");
			stmt.append("LEFT OUTER JOIN geozone giz ON ia.geozone = giz.id ");
			stmt.append("LEFT OUTER JOIN geotree git ON giz.id = git.child ");
			stmt.append("LEFT OUTER JOIN geozone giz2 ON git.parent = giz2.id ");
			stmt.append("LEFT OUTER JOIN raddress ra ON ra.registry = i.registry AND ra.type = 0 ");
			stmt.append("LEFT OUTER JOIN geozone gz ON ra.geozone = gz.id ");
			stmt.append("LEFT OUTER JOIN geotree gt ON gz.id = gt.child ");
			stmt.append("LEFT OUTER JOIN geozone gz2 ON gt.parent = gz2.id ");
			stmt.append("WHERE i.id=i.id");
			stmt.append(" AND it.tax_type=1 ");
			if (params.getType() == Model347Type.A_KEY ) {
				stmt.append(" AND i.type = 1");			
			}
			if (params.getType() == Model347Type.B_KEY ) {
				stmt.append(" AND (i.type = 0 OR i.type = 2)");
			}
			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append("AND i.issue_date <= ?");	
			}
			stmt.append("GROUP BY  key347,i.rdocument,i.registry,i.rname,geozone,geozoneName,country,countryName ");
			stmt.append("having total > ? ");
			if (params.getOrder() == null) {
				stmt.append(" ORDER BY key347,total desc");
			} else if (params.getOrder() == Model347ReportOrder.INVOICE_REGISTRY_DOCUMENT) {
				stmt.append(" ORDER BY key347,i.rdocument,total desc");
			} else if (params.getOrder() == Model347ReportOrder.INVOICE_REGISTRY_ID) {
				stmt.append(" ORDER BY key347,i.registry,total desc");
			} else if (params.getOrder() == Model347ReportOrder.INVOICE_REGISTRY_NAME) {
				stmt.append("ORDER BY key347,i.rname,total desc");
			}else if (params.getOrder() == Model347ReportOrder.INVOICE_TOTAL_AMOUNT) {
				stmt.append("ORDER BY key347,total desc");
			}
		
			String sessionName = HibernateUtil.getSessionFactoryName();
			ps = HibernateUtil.getSQLConnection(sessionName).prepareStatement(stmt.toString(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			if (params.getFromDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getToDate().getTime()));
			}
			ps.setDouble(++i, params.getMinimunAmount());
			rs = ps.executeQuery();
			List<Model347> m347s = new LinkedList<Model347>();
			while (rs.next()) {
				Model347 m347 = new Model347();
				String type = rs.getString(1);
				if ("A".equals(type)) {
					m347.setType(Model347Type.A_KEY);	
				} else if ("B".equals(type)) {
					m347.setType(Model347Type.B_KEY);
				}
				m347.setId(rs.getInt(2));
				RegistryDocument rd = new RegistryDocument( rs.getString(3) );
				m347.setDocument(rd);
				m347.setName(rs.getString(4));
				Integer geozone = rs.getInt(5); 
				String geozoneName =  rs.getString(6);
				ensureGeozone(m347,geozone,geozoneName);
				Integer c = rs.getInt(7);
				String country = null;
				if (c!=null) {
					country = Integer.toString(c);
				}
				String countryName = rs.getString(8);
				ensureCountry(m347,country,countryName);
				m347.setTotal(CommonUtil.round(rs.getDouble(9)));
				m347s.add(m347);
			}
			return m347s;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
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

	private void ensureCountry(Model347 m347, String country, String countryName) {
		if (countryName == null) {
			m347.setCountry("");
		} else if (StringUtils.equalsIgnoreCase("españa", StringUtils.trim(countryName))) {
			m347.setCountry("ES");
		} else if (StringUtils.equalsIgnoreCase("espana", StringUtils.trim(countryName))) {
			m347.setCountry("ES");
		} else if (StringUtils.equalsIgnoreCase("espania", StringUtils.trim(countryName))) {
			m347.setCountry("ES");
		} else if (StringUtils.equalsIgnoreCase("AFGANISTÁN", StringUtils.trim(countryName))) {
			m347.setCountry("AF");
		} else if (StringUtils.equalsIgnoreCase("ALBANIA", StringUtils.trim(countryName))) {
			m347.setCountry("AL");
		} else if (StringUtils.equalsIgnoreCase("ALEMANIA", StringUtils.trim(countryName))) {
			m347.setCountry("DE");
		} else if (StringUtils.equalsIgnoreCase("ANDORRA", StringUtils.trim(countryName))) {
			m347.setCountry("AD");
		} else if (StringUtils.equalsIgnoreCase("ANGOLA", StringUtils.trim(countryName))) {
			m347.setCountry("AO");
		} else if (StringUtils.equalsIgnoreCase("ANGUILA", StringUtils.trim(countryName))) {
			m347.setCountry("AI");
		} else if (StringUtils.equalsIgnoreCase("ANTÁRTIDA", StringUtils.trim(countryName))) {
			m347.setCountry("AQ");
		} else if (StringUtils.equalsIgnoreCase("ANTIGUA Y BARBUDA", StringUtils.trim(countryName))) {
			m347.setCountry("AG");
		} else if (StringUtils.equalsIgnoreCase("ANTILLAS NEERLANDESAS", StringUtils.trim(countryName))) {
			m347.setCountry("AN");
		} else if (StringUtils.equalsIgnoreCase("ARABIA SAUDÍ", StringUtils.trim(countryName))) {
			m347.setCountry("SA");
		} else if (StringUtils.equalsIgnoreCase("ARGELIA", StringUtils.trim(countryName))) {
			m347.setCountry("DZ");
		} else if (StringUtils.equalsIgnoreCase("ARGENTINA", StringUtils.trim(countryName))) {
			m347.setCountry("AR");
		} else if (StringUtils.equalsIgnoreCase("ARMENIA", StringUtils.trim(countryName))) {
			m347.setCountry("AM");
		} else if (StringUtils.equalsIgnoreCase("ARUBA", StringUtils.trim(countryName))) {
			m347.setCountry("AW");
		} else if (StringUtils.equalsIgnoreCase("AUSTRALIA", StringUtils.trim(countryName))) {
			m347.setCountry("AU");
		} else if (StringUtils.equalsIgnoreCase("AUSTRIA", StringUtils.trim(countryName))) {
			m347.setCountry("AT");
		} else if (StringUtils.equalsIgnoreCase("AZERBAIYÁN", StringUtils.trim(countryName))) {
			m347.setCountry("AZ");
		} else if (StringUtils.equalsIgnoreCase("BAHAMAS", StringUtils.trim(countryName))) {
			m347.setCountry("BS");
		} else if (StringUtils.equalsIgnoreCase("BAHRÉIN", StringUtils.trim(countryName))) {
			m347.setCountry("BH");
		} else if (StringUtils.equalsIgnoreCase("BANGLADESH", StringUtils.trim(countryName))) {
			m347.setCountry("BD");
		} else if (StringUtils.equalsIgnoreCase("BARBADOS", StringUtils.trim(countryName))) {
			m347.setCountry("BB");
		} else if (StringUtils.equalsIgnoreCase("BÉLGICA", StringUtils.trim(countryName))) {
			m347.setCountry("BE");
		} else if (StringUtils.equalsIgnoreCase("BELICE", StringUtils.trim(countryName))) {
			m347.setCountry("BZ");
		} else if (StringUtils.equalsIgnoreCase("BENÍN", StringUtils.trim(countryName))) {
			m347.setCountry("BJ");
		} else if (StringUtils.equalsIgnoreCase("BERMUDAS", StringUtils.trim(countryName))) {
			m347.setCountry("BM");
		} else if (StringUtils.equalsIgnoreCase("BIELORRUSIA", StringUtils.trim(countryName))) {
			m347.setCountry("BY");
		} else if (StringUtils.equalsIgnoreCase("BOLIVIA", StringUtils.trim(countryName))) {
			m347.setCountry("BO");
		} else if (StringUtils.equalsIgnoreCase("BOSNIA-HERZEGOVINA", StringUtils.trim(countryName))) {
			m347.setCountry("BA");
		} else if (StringUtils.equalsIgnoreCase("BOTSUANA", StringUtils.trim(countryName))) {
			m347.setCountry("BW");
		} else if (StringUtils.equalsIgnoreCase("BOUVET, ISLA", StringUtils.trim(countryName))) {
			m347.setCountry("BV");
		} else if (StringUtils.equalsIgnoreCase("BRASIL", StringUtils.trim(countryName))) {
			m347.setCountry("BR");
		} else if (StringUtils.equalsIgnoreCase("BRUNÉI", StringUtils.trim(countryName))) {
			m347.setCountry("BN");
		} else if (StringUtils.equalsIgnoreCase("BULGARIA", StringUtils.trim(countryName))) {
			m347.setCountry("BG");
		} else if (StringUtils.equalsIgnoreCase("BURKINA FASO", StringUtils.trim(countryName))) {
			m347.setCountry("BF");
		} else if (StringUtils.equalsIgnoreCase("BURUNDI", StringUtils.trim(countryName))) {
			m347.setCountry("BI");
		} else if (StringUtils.equalsIgnoreCase("BUTÁN", StringUtils.trim(countryName))) {
			m347.setCountry("BT");
		} else if (StringUtils.equalsIgnoreCase("CABO VERDE, REPÚBLICA DE", StringUtils.trim(countryName))) {
			m347.setCountry("CV");
		} else if (StringUtils.equalsIgnoreCase("CAIMÁN, ISLAS", StringUtils.trim(countryName))) {
			m347.setCountry("KY");
		} else if (StringUtils.equalsIgnoreCase("CAMBOYA", StringUtils.trim(countryName))) {
			m347.setCountry("KH");
		} else if (StringUtils.equalsIgnoreCase("CAMERÚN", StringUtils.trim(countryName))) {
			m347.setCountry("CM");
		} else if (StringUtils.equalsIgnoreCase("CANADÁ", StringUtils.trim(countryName))) {
			m347.setCountry("CA");
		} else if (StringUtils.equalsIgnoreCase("CENTROAFRICANA, REPÚBLICA", StringUtils.trim(countryName))) {
			m347.setCountry("CF");
		} else if (StringUtils.equalsIgnoreCase("COCOS, ISLA DE KEELING", StringUtils.trim(countryName))) {
			m347.setCountry("CC");
		} else if (StringUtils.equalsIgnoreCase("COLOMBIA", StringUtils.trim(countryName))) {
			m347.setCountry("CO");
		} else if (StringUtils.equalsIgnoreCase("COMORAS", StringUtils.trim(countryName))) {
			m347.setCountry("KM");
		} else if (StringUtils.equalsIgnoreCase("CONGO", StringUtils.trim(countryName))) {
			m347.setCountry("CG");
		} else if (StringUtils.equalsIgnoreCase("CONGO, REPUBLICA DEMOCRÁTICA DEL (Zaire)", StringUtils.trim(countryName))) {
			m347.setCountry("CD");
		} else if (StringUtils.equalsIgnoreCase("COOK, ISLAS", StringUtils.trim(countryName))) {
			m347.setCountry("CK");
		} else if (StringUtils.equalsIgnoreCase("COREA DEL NORTE (República Popular Democrática de Corea)", StringUtils.trim(countryName))) {
			m347.setCountry("KP");
		} else if (StringUtils.equalsIgnoreCase("COREA DEL SUR (República de Corea)", StringUtils.trim(countryName))) {
			m347.setCountry("KR");
		} else if (StringUtils.equalsIgnoreCase("COSTA DE MARFIL", StringUtils.trim(countryName))) {
			m347.setCountry("CI");
		} else if (StringUtils.equalsIgnoreCase("COSTA RICA", StringUtils.trim(countryName))) {
			m347.setCountry("CR");
		} else if (StringUtils.equalsIgnoreCase("CROACIA", StringUtils.trim(countryName))) {
			m347.setCountry("HR");
		} else if (StringUtils.equalsIgnoreCase("CUBA", StringUtils.trim(countryName))) {
			m347.setCountry("CU");
		} else if (StringUtils.equalsIgnoreCase("CHAD", StringUtils.trim(countryName))) {
			m347.setCountry("TD");
		} else if (StringUtils.equalsIgnoreCase("CHECA, REPÚBLICA", StringUtils.trim(countryName))) {
			m347.setCountry("CZ");
		} else if (StringUtils.equalsIgnoreCase("CHILE", StringUtils.trim(countryName))) {
			m347.setCountry("CL");
		} else if (StringUtils.equalsIgnoreCase("CHINA", StringUtils.trim(countryName))) {
			m347.setCountry("CN");
		} else if (StringUtils.equalsIgnoreCase("CHIPRE", StringUtils.trim(countryName))) {
			m347.setCountry("CY");
		} else if (StringUtils.equalsIgnoreCase("DINAMARCA", StringUtils.trim(countryName))) {
			m347.setCountry("DK");
		} else if (StringUtils.equalsIgnoreCase("DOMINICA", StringUtils.trim(countryName))) {
			m347.setCountry("DM");
		} else if (StringUtils.equalsIgnoreCase("DOMINICANA, REPÚBLICA", StringUtils.trim(countryName))) {
			m347.setCountry("DO");
		} else if (StringUtils.equalsIgnoreCase("ECUADOR", StringUtils.trim(countryName))) {
			m347.setCountry("EC");
		} else if (StringUtils.equalsIgnoreCase("EGIPTO", StringUtils.trim(countryName))) {
			m347.setCountry("EG");
		} else if (StringUtils.equalsIgnoreCase("EMIRATOS ÁRABES UNIDOS", StringUtils.trim(countryName))) {
			m347.setCountry("AE");
		} else if (StringUtils.equalsIgnoreCase("ERITREA", StringUtils.trim(countryName))) {
			m347.setCountry("ER");
		} else if (StringUtils.equalsIgnoreCase("ESLOVAQUIA", StringUtils.trim(countryName))) {
			m347.setCountry("SK");
		} else if (StringUtils.equalsIgnoreCase("ESLOVENIA", StringUtils.trim(countryName))) {
			m347.setCountry("SI");
		} else if (StringUtils.equalsIgnoreCase("ESTADOS UNIDOS DE AMÉRICA", StringUtils.trim(countryName))) {
			m347.setCountry("US");
		} else if (StringUtils.equalsIgnoreCase("ESTONIA", StringUtils.trim(countryName))) {
			m347.setCountry("EE");
		} else if (StringUtils.equalsIgnoreCase("ETIOPÍA", StringUtils.trim(countryName))) {
			m347.setCountry("ET");
		} else if (StringUtils.equalsIgnoreCase("FEROE, ISLAS", StringUtils.trim(countryName))) {
			m347.setCountry("FO");
		} else if (StringUtils.equalsIgnoreCase("FILIPINAS", StringUtils.trim(countryName))) {
			m347.setCountry("PH");
		} else if (StringUtils.equalsIgnoreCase("FINLANDIA", StringUtils.trim(countryName))) {
			m347.setCountry("FI");
		} else if (StringUtils.equalsIgnoreCase("FIYI", StringUtils.trim(countryName))) {
			m347.setCountry("FJ");
		} else if (StringUtils.equalsIgnoreCase("FRANCIA", StringUtils.trim(countryName))) {
			m347.setCountry("FR");
		} else if (StringUtils.equalsIgnoreCase("GABÓN", StringUtils.trim(countryName))) {
			m347.setCountry("GA");
		} else if (StringUtils.equalsIgnoreCase("GAMBIA", StringUtils.trim(countryName))) {
			m347.setCountry("GM");
		} else if (StringUtils.equalsIgnoreCase("GEORGIA", StringUtils.trim(countryName))) {
			m347.setCountry("GE");
		} else if (StringUtils.equalsIgnoreCase("GEORGIA DEL SUR Y LAS ISLAS SANDWICH DEL SUR", StringUtils.trim(countryName))) {
			m347.setCountry("GS");
		} else if (StringUtils.equalsIgnoreCase("GHANA", StringUtils.trim(countryName))) {
			m347.setCountry("GH");
		} else if (StringUtils.equalsIgnoreCase("GIBRALTAR", StringUtils.trim(countryName))) {
			m347.setCountry("GI");
		} else if (StringUtils.equalsIgnoreCase("GRANADA", StringUtils.trim(countryName))) {
			m347.setCountry("GD");
		} else if (StringUtils.equalsIgnoreCase("GRECIA", StringUtils.trim(countryName))) {
			m347.setCountry("GR");
		} else if (StringUtils.equalsIgnoreCase("GROENLANDIA", StringUtils.trim(countryName))) {
			m347.setCountry("GL");
		} else if (StringUtils.equalsIgnoreCase("GUAM", StringUtils.trim(countryName))) {
			m347.setCountry("GU");
		} else if (StringUtils.equalsIgnoreCase("GUATEMALA", StringUtils.trim(countryName))) {
			m347.setCountry("GT");
		} else if (StringUtils.equalsIgnoreCase("GUERNESEY", StringUtils.trim(countryName))) {
			m347.setCountry("GG");
		} else if (StringUtils.equalsIgnoreCase("GUINEA", StringUtils.trim(countryName))) {
			m347.setCountry("GN");
		} else if (StringUtils.equalsIgnoreCase("GUINEA ECUATORIAL", StringUtils.trim(countryName))) {
			m347.setCountry("GQ");
		} else if (StringUtils.equalsIgnoreCase("GUINEA-BISSAU", StringUtils.trim(countryName))) {
			m347.setCountry("GW");
		} else if (StringUtils.equalsIgnoreCase("GUYANA", StringUtils.trim(countryName))) {
			m347.setCountry("GY");
		} else if (StringUtils.equalsIgnoreCase("HAITÍ", StringUtils.trim(countryName))) {
			m347.setCountry("HT");
		} else if (StringUtils.equalsIgnoreCase("HEARD Y MCDONALD, ISLAS", StringUtils.trim(countryName))) {
			m347.setCountry("HM");
		} else if (StringUtils.equalsIgnoreCase("HONDURAS", StringUtils.trim(countryName))) {
			m347.setCountry("HN");
		} else if (StringUtils.equalsIgnoreCase("HONG-KONG", StringUtils.trim(countryName))) {
			m347.setCountry("HK");
		} else if (StringUtils.equalsIgnoreCase("HUNGRÍA", StringUtils.trim(countryName))) {
			m347.setCountry("HU");
		} else if (StringUtils.equalsIgnoreCase("INDIA", StringUtils.trim(countryName))) {
			m347.setCountry("IN");
		} else if (StringUtils.equalsIgnoreCase("INDONESIA", StringUtils.trim(countryName))) {
			m347.setCountry("ID");
		} else if (StringUtils.equalsIgnoreCase("IRÁN", StringUtils.trim(countryName))) {
			m347.setCountry("IR");
		} else if (StringUtils.equalsIgnoreCase("IRAQ", StringUtils.trim(countryName))) {
			m347.setCountry("IQ");
		} else if (StringUtils.equalsIgnoreCase("IRLANDA", StringUtils.trim(countryName))) {
			m347.setCountry("IE");
		} else if (StringUtils.equalsIgnoreCase("ISLA DE MAN", StringUtils.trim(countryName))) {
			m347.setCountry("IM");
		} else if (StringUtils.equalsIgnoreCase("ISRAEL", StringUtils.trim(countryName))) {
			m347.setCountry("IL");
		} else if (StringUtils.equalsIgnoreCase("ITALIA", StringUtils.trim(countryName))) {
			m347.setCountry("IT");
		} else if (StringUtils.equalsIgnoreCase("JAMAICA", StringUtils.trim(countryName))) {
			m347.setCountry("JM");
		} else if (StringUtils.equalsIgnoreCase("JAPÓN", StringUtils.trim(countryName))) {
			m347.setCountry("JP");
		} else if (StringUtils.equalsIgnoreCase("JERSEY", StringUtils.trim(countryName))) {
			m347.setCountry("JE");
		} else if (StringUtils.equalsIgnoreCase("JORDANIA", StringUtils.trim(countryName))) {
			m347.setCountry("JO");
		} else if (StringUtils.equalsIgnoreCase("KAZAJSTÁN", StringUtils.trim(countryName))) {
			m347.setCountry("KZ");
		} else if (StringUtils.equalsIgnoreCase("KENIA", StringUtils.trim(countryName))) {
			m347.setCountry("KE");
		} else if (StringUtils.equalsIgnoreCase("KIRGUISTÁN", StringUtils.trim(countryName))) {
			m347.setCountry("KG");
		} else if (StringUtils.equalsIgnoreCase("KIRIBATI", StringUtils.trim(countryName))) {
			m347.setCountry("KI");
		} else if (StringUtils.equalsIgnoreCase("KUWAIT", StringUtils.trim(countryName))) {
			m347.setCountry("KW");
		} else if (StringUtils.equalsIgnoreCase("LAOS (LAO)", StringUtils.trim(countryName))) {
			m347.setCountry("LA");
		} else if (StringUtils.equalsIgnoreCase("LESOTHO", StringUtils.trim(countryName))) {
			m347.setCountry("LS");
		} else if (StringUtils.equalsIgnoreCase("LETONIA", StringUtils.trim(countryName))) {
			m347.setCountry("LV");
		} else if (StringUtils.equalsIgnoreCase("LÍBANO", StringUtils.trim(countryName))) {
			m347.setCountry("LB");
		} else if (StringUtils.equalsIgnoreCase("LIBERIA", StringUtils.trim(countryName))) {
			m347.setCountry("LR");
		} else if (StringUtils.equalsIgnoreCase("LIBIA", StringUtils.trim(countryName))) {
			m347.setCountry("LY");
		} else if (StringUtils.equalsIgnoreCase("LIECHTENSTEIN", StringUtils.trim(countryName))) {
			m347.setCountry("LI");
		} else if (StringUtils.equalsIgnoreCase("LITUANIA", StringUtils.trim(countryName))) {
			m347.setCountry("LT");
		} else if (StringUtils.equalsIgnoreCase("LUXEMBURGO", StringUtils.trim(countryName))) {
			m347.setCountry("LU");
		} else if (StringUtils.equalsIgnoreCase("MACAO", StringUtils.trim(countryName))) {
			m347.setCountry("MO");
		} else if (StringUtils.equalsIgnoreCase("MACEDONIA", StringUtils.trim(countryName))) {
			m347.setCountry("MK");
		} else if (StringUtils.equalsIgnoreCase("MADAGASCAR", StringUtils.trim(countryName))) {
			m347.setCountry("MG");
		} else if (StringUtils.equalsIgnoreCase("MALASIA", StringUtils.trim(countryName))) {
			m347.setCountry("MY");
		} else if (StringUtils.equalsIgnoreCase("MALAWI", StringUtils.trim(countryName))) {
			m347.setCountry("MW");
		} else if (StringUtils.equalsIgnoreCase("MALDIVAS", StringUtils.trim(countryName))) {
			m347.setCountry("MV");
		} else if (StringUtils.equalsIgnoreCase("MALI", StringUtils.trim(countryName))) {
			m347.setCountry("ML");
		} else if (StringUtils.equalsIgnoreCase("MALTA", StringUtils.trim(countryName))) {
			m347.setCountry("MT");
		} else if (StringUtils.equalsIgnoreCase("MALVINAS, ISLAS (FALKLANDS)", StringUtils.trim(countryName))) {
			m347.setCountry("FK");
		} else if (StringUtils.equalsIgnoreCase("MARIANAS DEL NORTE, ISLAS", StringUtils.trim(countryName))) {
			m347.setCountry("MP");
		} else if (StringUtils.equalsIgnoreCase("MARRUECOS", StringUtils.trim(countryName))) {
			m347.setCountry("MA");
		} else if (StringUtils.equalsIgnoreCase("MARSHALL, ISLAS", StringUtils.trim(countryName))) {
			m347.setCountry("MH");
		} else if (StringUtils.equalsIgnoreCase("MAURICIO", StringUtils.trim(countryName))) {
			m347.setCountry("MU");
		} else if (StringUtils.equalsIgnoreCase("MAURITANIA", StringUtils.trim(countryName))) {
			m347.setCountry("MR");
		} else if (StringUtils.equalsIgnoreCase("MAYOTTE", StringUtils.trim(countryName))) {
			m347.setCountry("YT");
		} else if (StringUtils.equalsIgnoreCase("MENORES ALEJADAS DE LOS EE.UU, ISLAS", StringUtils.trim(countryName))) {
			m347.setCountry("UM");
		} else if (StringUtils.equalsIgnoreCase("MÉXICO", StringUtils.trim(countryName))) {
			m347.setCountry("MX");
		} else if (StringUtils.equalsIgnoreCase("MICRONESIA", StringUtils.trim(countryName))) {
			m347.setCountry("FM");
		} else if (StringUtils.equalsIgnoreCase("MOLDAVIA", StringUtils.trim(countryName))) {
			m347.setCountry("MD");
		} else if (StringUtils.equalsIgnoreCase("MÓNACO", StringUtils.trim(countryName))) {
			m347.setCountry("MC");
		} else if (StringUtils.equalsIgnoreCase("MONGOLIA", StringUtils.trim(countryName))) {
			m347.setCountry("MN");
		} else if (StringUtils.equalsIgnoreCase("MONTENEGRO", StringUtils.trim(countryName))) {
			m347.setCountry("ME");
		} else if (StringUtils.equalsIgnoreCase("MONTSERRAT", StringUtils.trim(countryName))) {
			m347.setCountry("MS");
		} else if (StringUtils.equalsIgnoreCase("MOZAMBIQUE", StringUtils.trim(countryName))) {
			m347.setCountry("MZ");
		} else if (StringUtils.equalsIgnoreCase("MYANMAR", StringUtils.trim(countryName))) {
			m347.setCountry("MM");
		} else if (StringUtils.equalsIgnoreCase("NAMIBIA", StringUtils.trim(countryName))) {
			m347.setCountry("NA");
		} else if (StringUtils.equalsIgnoreCase("NAURU", StringUtils.trim(countryName))) {
			m347.setCountry("NR");
		} else if (StringUtils.equalsIgnoreCase("NAVIDAD, ISLA", StringUtils.trim(countryName))) {
			m347.setCountry("CX");
		} else if (StringUtils.equalsIgnoreCase("NEPAL", StringUtils.trim(countryName))) {
			m347.setCountry("NP");
		} else if (StringUtils.equalsIgnoreCase("NICARAGUA", StringUtils.trim(countryName))) {
			m347.setCountry("NI");
		} else if (StringUtils.equalsIgnoreCase("NÍGER", StringUtils.trim(countryName))) {
			m347.setCountry("NE");
		} else if (StringUtils.equalsIgnoreCase("NIGERIA", StringUtils.trim(countryName))) {
			m347.setCountry("NG");
		} else if (StringUtils.equalsIgnoreCase("NIUE, ISLA", StringUtils.trim(countryName))) {
			m347.setCountry("NU");
		} else if (StringUtils.equalsIgnoreCase("NORFOLK, ISLA", StringUtils.trim(countryName))) {
			m347.setCountry("NF");
		} else if (StringUtils.equalsIgnoreCase("NORUEGA", StringUtils.trim(countryName))) {
			m347.setCountry("NO");
		} else if (StringUtils.equalsIgnoreCase("NUEVA CALEDONIA", StringUtils.trim(countryName))) {
			m347.setCountry("NC");
		} else if (StringUtils.equalsIgnoreCase("NUEVA ZELANDA", StringUtils.trim(countryName))) {
			m347.setCountry("NZ");
		} else if (StringUtils.equalsIgnoreCase("OCÉANO ÍNDICO, TERRITORIO BRITÁNICO DEL", StringUtils.trim(countryName))) {
			m347.setCountry("IO");
		} else if (StringUtils.equalsIgnoreCase("OMÁN", StringUtils.trim(countryName))) {
			m347.setCountry("OM");
		} else if (StringUtils.equalsIgnoreCase("PAÍSES BAJOS", StringUtils.trim(countryName))) {
			m347.setCountry("NL");
		} else if (StringUtils.equalsIgnoreCase("HOLANDA", StringUtils.trim(countryName))) {
			m347.setCountry("NL");
		} else if (StringUtils.equalsIgnoreCase("PAKISTÁN", StringUtils.trim(countryName))) {
			m347.setCountry("PK");
		} else if (StringUtils.equalsIgnoreCase("PALAU", StringUtils.trim(countryName))) {
			m347.setCountry("PW");
		} else if (StringUtils.equalsIgnoreCase("PANAMÁ", StringUtils.trim(countryName))) {
			m347.setCountry("PA");
		} else if (StringUtils.equalsIgnoreCase("PAPÚA NUEVA GUINEA", StringUtils.trim(countryName))) {
			m347.setCountry("PG");
		} else if (StringUtils.equalsIgnoreCase("PARAGUAY", StringUtils.trim(countryName))) {
			m347.setCountry("PY");
		} else if (StringUtils.equalsIgnoreCase("PERÚ", StringUtils.trim(countryName))) {
			m347.setCountry("PE");
		} else if (StringUtils.equalsIgnoreCase("PITCAIRN", StringUtils.trim(countryName))) {
			m347.setCountry("PN");
		} else if (StringUtils.equalsIgnoreCase("POLINESIA FRANCESA", StringUtils.trim(countryName))) {
			m347.setCountry("PF");
		} else if (StringUtils.equalsIgnoreCase("POLONIA", StringUtils.trim(countryName))) {
			m347.setCountry("PL");
		} else if (StringUtils.equalsIgnoreCase("PORTUGAL", StringUtils.trim(countryName))) {
			m347.setCountry("PT");
		} else if (StringUtils.equalsIgnoreCase("PUERTO RICO", StringUtils.trim(countryName))) {
			m347.setCountry("PR");
		} else if (StringUtils.equalsIgnoreCase("QATAR", StringUtils.trim(countryName))) {
			m347.setCountry("QA");
		} else if (StringUtils.equalsIgnoreCase("REINO UNIDO", StringUtils.trim(countryName))) {
			m347.setCountry("GB");
		} else if (StringUtils.equalsIgnoreCase("RUANDA", StringUtils.trim(countryName))) {
			m347.setCountry("RW");
		} else if (StringUtils.equalsIgnoreCase("RUMANÍA", StringUtils.trim(countryName))) {
			m347.setCountry("RO");
		} else if (StringUtils.equalsIgnoreCase("RUSIA", StringUtils.trim(countryName))) {
			m347.setCountry("RU");
		} else if (StringUtils.equalsIgnoreCase("SALOMÓN, ISLAS", StringUtils.trim(countryName))) {
			m347.setCountry("SB");
		} else if (StringUtils.equalsIgnoreCase("SALVADOR, EL", StringUtils.trim(countryName))) {
			m347.setCountry("SV");
		} else if (StringUtils.equalsIgnoreCase("SAMOA", StringUtils.trim(countryName))) {
			m347.setCountry("WS");
		} else if (StringUtils.equalsIgnoreCase("SAMOA AMERICANA", StringUtils.trim(countryName))) {
			m347.setCountry("AS");
		} else if (StringUtils.equalsIgnoreCase("SAN CRISTOBAL Y NIEVES", StringUtils.trim(countryName))) {
			m347.setCountry("KN");
		} else if (StringUtils.equalsIgnoreCase("SAN MARINO", StringUtils.trim(countryName))) {
			m347.setCountry("SM");
		} else if (StringUtils.equalsIgnoreCase("SAN PEDRO Y MIQUELÓN", StringUtils.trim(countryName))) {
			m347.setCountry("PM");
		} else if (StringUtils.equalsIgnoreCase("SAN VICENTE Y LAS GRANADINAS", StringUtils.trim(countryName))) {
			m347.setCountry("VC");
		} else if (StringUtils.equalsIgnoreCase("SANTA ELENA", StringUtils.trim(countryName))) {
			m347.setCountry("SH");
		} else if (StringUtils.equalsIgnoreCase("SANTA LUCÍA", StringUtils.trim(countryName))) {
			m347.setCountry("LC");
		} else if (StringUtils.equalsIgnoreCase("SANTO TOMÉ Y PRÍNCIPE", StringUtils.trim(countryName))) {
			m347.setCountry("ST");
		} else if (StringUtils.equalsIgnoreCase("SENEGAL", StringUtils.trim(countryName))) {
			m347.setCountry("SN");
		} else if (StringUtils.equalsIgnoreCase("SERVIA", StringUtils.trim(countryName))) {
			m347.setCountry("RS");
		} else if (StringUtils.equalsIgnoreCase("SEYCHELLES", StringUtils.trim(countryName))) {
			m347.setCountry("SC");
		} else if (StringUtils.equalsIgnoreCase("SIERRA LEONA", StringUtils.trim(countryName))) {
			m347.setCountry("SL");
		} else if (StringUtils.equalsIgnoreCase("SINGAPUR", StringUtils.trim(countryName))) {
			m347.setCountry("SG");
		} else if (StringUtils.equalsIgnoreCase("SIRIA", StringUtils.trim(countryName))) {
			m347.setCountry("SY");
		} else if (StringUtils.equalsIgnoreCase("SOMALIA", StringUtils.trim(countryName))) {
			m347.setCountry("SO");
		} else if (StringUtils.equalsIgnoreCase("SRI LANKA", StringUtils.trim(countryName))) {
			m347.setCountry("LK");
		} else if (StringUtils.equalsIgnoreCase("SUAZILANDIA", StringUtils.trim(countryName))) {
			m347.setCountry("SZ");
		} else if (StringUtils.equalsIgnoreCase("SUDÁFRICA", StringUtils.trim(countryName))) {
			m347.setCountry("ZA");
		} else if (StringUtils.equalsIgnoreCase("SUDÁN", StringUtils.trim(countryName))) {
			m347.setCountry("SD");
		} else if (StringUtils.equalsIgnoreCase("SUECIA", StringUtils.trim(countryName))) {
			m347.setCountry("SE");
		} else if (StringUtils.equalsIgnoreCase("SUIZA", StringUtils.trim(countryName))) {
			m347.setCountry("CH");
		} else if (StringUtils.equalsIgnoreCase("SURINAM", StringUtils.trim(countryName))) {
			m347.setCountry("SR");
		} else if (StringUtils.equalsIgnoreCase("TAILANDIA", StringUtils.trim(countryName))) {
			m347.setCountry("TH");
		} else if (StringUtils.equalsIgnoreCase("TAIWÁN", StringUtils.trim(countryName))) {
			m347.setCountry("TW");
		} else if (StringUtils.equalsIgnoreCase("TANZANIA", StringUtils.trim(countryName))) {
			m347.setCountry("TZ");
		} else if (StringUtils.equalsIgnoreCase("TAYIKISTÁN", StringUtils.trim(countryName))) {
			m347.setCountry("TJ");
		} else if (StringUtils.equalsIgnoreCase("TERRITORIO PALESTINO OCUPADO", StringUtils.trim(countryName))) {
			m347.setCountry("PS");
		} else if (StringUtils.equalsIgnoreCase("TIERRAS AUSTRALES FRANCESAS", StringUtils.trim(countryName))) {
			m347.setCountry("TF");
		} else if (StringUtils.equalsIgnoreCase("TIMOR LESTE", StringUtils.trim(countryName))) {
			m347.setCountry("TL");
		} else if (StringUtils.equalsIgnoreCase("TOGO", StringUtils.trim(countryName))) {
			m347.setCountry("TG");
		} else if (StringUtils.equalsIgnoreCase("TOKELAU, ISLAS", StringUtils.trim(countryName))) {
			m347.setCountry("TK");
		} else if (StringUtils.equalsIgnoreCase("TONGA", StringUtils.trim(countryName))) {
			m347.setCountry("TO");
		} else if (StringUtils.equalsIgnoreCase("TRINIDAD Y TOBAGO", StringUtils.trim(countryName))) {
			m347.setCountry("TT");
		} else if (StringUtils.equalsIgnoreCase("TÚNEZ", StringUtils.trim(countryName))) {
			m347.setCountry("TN");
		} else if (StringUtils.equalsIgnoreCase("TURCAS Y CAICOS, ISLAS", StringUtils.trim(countryName))) {
			m347.setCountry("TC");
		} else if (StringUtils.equalsIgnoreCase("TURKMENISTÁN", StringUtils.trim(countryName))) {
			m347.setCountry("TM");
		} else if (StringUtils.equalsIgnoreCase("TURQUÍA", StringUtils.trim(countryName))) {
			m347.setCountry("TR");
		} else if (StringUtils.equalsIgnoreCase("TUVALU", StringUtils.trim(countryName))) {
			m347.setCountry("TV");
		} else if (StringUtils.equalsIgnoreCase("UCRANIA", StringUtils.trim(countryName))) {
			m347.setCountry("UA");
		} else if (StringUtils.equalsIgnoreCase("UGANDA", StringUtils.trim(countryName))) {
			m347.setCountry("UG");
		} else if (StringUtils.equalsIgnoreCase("URUGUAY", StringUtils.trim(countryName))) {
			m347.setCountry("UY");
		} else if (StringUtils.equalsIgnoreCase("UZBEKISTÁN", StringUtils.trim(countryName))) {
			m347.setCountry("UZ");
		} else if (StringUtils.equalsIgnoreCase("VANUATU", StringUtils.trim(countryName))) {
			m347.setCountry("VU");
		} else if (StringUtils.equalsIgnoreCase("VATICANO, CIUDAD DEL", StringUtils.trim(countryName))) {
			m347.setCountry("VA");
		} else if (StringUtils.equalsIgnoreCase("VENEZUELA", StringUtils.trim(countryName))) {
			m347.setCountry("VE");
		} else if (StringUtils.equalsIgnoreCase("VIETNAM", StringUtils.trim(countryName))) {
			m347.setCountry("VN");
		} else if (StringUtils.equalsIgnoreCase("VÍRGENES BRITÁNICAS, ISLAS", StringUtils.trim(countryName))) {
			m347.setCountry("VG");
		} else if (StringUtils.equalsIgnoreCase("VÍRGENES DE LOS EE.UU., ISLAS", StringUtils.trim(countryName))) {
			m347.setCountry("VI");
		} else {
			m347.setCountry(country);
		}
		m347.setCountryName(countryName);
	}

	private void ensureGeozone(Model347 m347, Integer geozone, String geozoneName) {
		m347.setGeozoneName(geozoneName);
		if (geozone == null || geozone  == 0) {
			m347.setGeozone(0);
		} else if (geozone == 99) {
			m347.setGeozone(99);
			m347.setGeozoneName("NO RESIDENTE");
		} else if (StringUtils.equalsIgnoreCase("ÁLAVA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(1);
		} else if (StringUtils.equalsIgnoreCase("ALAVA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(1);
		} else if (StringUtils.equalsIgnoreCase("ARABA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(1);
		} else if (StringUtils.equalsIgnoreCase("ALBACETE", StringUtils.trim(geozoneName))) {
			m347.setGeozone(2);
		} else if (StringUtils.equalsIgnoreCase("ALICANTE", StringUtils.trim(geozoneName))) {
			m347.setGeozone(3);
		} else if (StringUtils.equalsIgnoreCase("ALMERIA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(4);
		} else if (StringUtils.equalsIgnoreCase("ASTURIAS", StringUtils.trim(geozoneName))) {
			m347.setGeozone(33);
		} else if (StringUtils.equalsIgnoreCase("ÁVILA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(5);
		} else if (StringUtils.equalsIgnoreCase("AVILA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(5);
		} else if (StringUtils.equalsIgnoreCase("BADAJOZ", StringUtils.trim(geozoneName))) {
			m347.setGeozone(6);
		} else if (StringUtils.equalsIgnoreCase("BARCELONA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(8);
		} else if (StringUtils.equalsIgnoreCase("BURGOS", StringUtils.trim(geozoneName))) {
			m347.setGeozone(9);
		} else if (StringUtils.equalsIgnoreCase("CÁCERES", StringUtils.trim(geozoneName))) {
			m347.setGeozone(10);
		} else if (StringUtils.equalsIgnoreCase("CACERES", StringUtils.trim(geozoneName))) {
			m347.setGeozone(10);
		} else if (StringUtils.equalsIgnoreCase("CÁDIZ", StringUtils.trim(geozoneName))) {
			m347.setGeozone(11);
		} else if (StringUtils.equalsIgnoreCase("CANTABRIA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(39);
		} else if (StringUtils.equalsIgnoreCase("CASTELLÓN", StringUtils.trim(geozoneName))) {
			m347.setGeozone(12);
		} else if (StringUtils.equalsIgnoreCase("CASTELLON", StringUtils.trim(geozoneName))) {
			m347.setGeozone(12);
		} else if (StringUtils.equalsIgnoreCase("CEUTA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(51);
		} else if (StringUtils.equalsIgnoreCase("CIUDAD REAL", StringUtils.trim(geozoneName))) {
			m347.setGeozone(13);
		} else if (StringUtils.equalsIgnoreCase("CÓRDOBA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(14);
		} else if (StringUtils.equalsIgnoreCase("CORDOBA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(14);
		} else if (StringUtils.equalsIgnoreCase("CORUÑA, A", StringUtils.trim(geozoneName))) {
			m347.setGeozone(15);
		} else if (StringUtils.equalsIgnoreCase("LA CORUÑA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(15);
		} else if (StringUtils.equalsIgnoreCase("CUENCA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(16);
		} else if (StringUtils.equalsIgnoreCase("GIRONA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(17);
		} else if (StringUtils.equalsIgnoreCase("GRANADA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(18);
		} else if (StringUtils.equalsIgnoreCase("GUADALAJARA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(19);
		} else if (StringUtils.equalsIgnoreCase("GIPUZKOA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(20);
		} else if (StringUtils.equalsIgnoreCase("GUIPUZCOA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(20);
		} else if (StringUtils.equalsIgnoreCase("HUELVA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(21);
		} else if (StringUtils.equalsIgnoreCase("HUESCA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(22);
		} else if (StringUtils.equalsIgnoreCase("ILLES BALEARS", StringUtils.trim(geozoneName))) {
			m347.setGeozone(07);
		} else if (StringUtils.equalsIgnoreCase("ISLAS BALEARES", StringUtils.trim(geozoneName))) {
			m347.setGeozone(07);
		} else if (StringUtils.equalsIgnoreCase("JAÉN", StringUtils.trim(geozoneName))) {
			m347.setGeozone(23);
		} else if (StringUtils.equalsIgnoreCase("JAEN", StringUtils.trim(geozoneName))) {
			m347.setGeozone(23);
		} else if (StringUtils.equalsIgnoreCase("LEÓN", StringUtils.trim(geozoneName))) {
			m347.setGeozone(24);
		} else if (StringUtils.equalsIgnoreCase("LEON", StringUtils.trim(geozoneName))) {
			m347.setGeozone(24);
		} else if (StringUtils.equalsIgnoreCase("LLEIDA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(25);
		} else if (StringUtils.equalsIgnoreCase("LERIDA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(25);
		} else if (StringUtils.equalsIgnoreCase("LUGO", StringUtils.trim(geozoneName))) {
			m347.setGeozone(27);
		} else if (StringUtils.equalsIgnoreCase("MADRID", StringUtils.trim(geozoneName))) {
			m347.setGeozone(28);
		} else if (StringUtils.equalsIgnoreCase("MÁLAGA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(29);
		} else if (StringUtils.equalsIgnoreCase("MALAGA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(29);
		} else if (StringUtils.equalsIgnoreCase("MELILLA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(52);
		} else if (StringUtils.equalsIgnoreCase("MURCIA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(30);
		} else if (StringUtils.equalsIgnoreCase("NAVARRA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(31);
		} else if (StringUtils.equalsIgnoreCase("OURENSE", StringUtils.trim(geozoneName))) {
			m347.setGeozone(32);
		} else if (StringUtils.equalsIgnoreCase("PALENCIA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(34);
		} else if (StringUtils.equalsIgnoreCase("PALMAS, LAS", StringUtils.trim(geozoneName))) {
			m347.setGeozone(35);
		} else if (StringUtils.equalsIgnoreCase("LAS PALMAS", StringUtils.trim(geozoneName))) {
			m347.setGeozone(35);
		} else if (StringUtils.equalsIgnoreCase("PONTEVEDRA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(36);
		} else if (StringUtils.equalsIgnoreCase("RIOJA, LA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(26);
		} else if (StringUtils.equalsIgnoreCase("LA RIOJA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(26);
		} else if (StringUtils.equalsIgnoreCase("SALAMANCA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(37);
		} else if (StringUtils.equalsIgnoreCase("S.C. TENERIFE", StringUtils.trim(geozoneName))) {
			m347.setGeozone(38);
		} else if (StringUtils.equalsIgnoreCase("SANTA CRUZ DE TENERIFE", StringUtils.trim(geozoneName))) {
			m347.setGeozone(38);
		} else if (StringUtils.equalsIgnoreCase("TENERIFE", StringUtils.trim(geozoneName))) {
			m347.setGeozone(38);
		} else if (StringUtils.equalsIgnoreCase("SEGOVIA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(40);
		} else if (StringUtils.equalsIgnoreCase("SEVILLA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(41);
		} else if (StringUtils.equalsIgnoreCase("SORIA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(42);
		} else if (StringUtils.equalsIgnoreCase("TARRAGONA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(43);
		} else if (StringUtils.equalsIgnoreCase("TERUEL", StringUtils.trim(geozoneName))) {
			m347.setGeozone(44);
		} else if (StringUtils.equalsIgnoreCase("TOLEDO", StringUtils.trim(geozoneName))) {
			m347.setGeozone(45);
		} else if (StringUtils.equalsIgnoreCase("VALENCIA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(46);
		} else if (StringUtils.equalsIgnoreCase("VALLADOLID", StringUtils.trim(geozoneName))) {
			m347.setGeozone(47);
		} else if (StringUtils.equalsIgnoreCase("BIZKAIA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(48);
		} else if (StringUtils.equalsIgnoreCase("VIZCAIA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(48);
		} else if (StringUtils.equalsIgnoreCase("VIZCAYA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(48);
		} else if (StringUtils.equalsIgnoreCase("ZAMORA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(49);
		} else if (StringUtils.equalsIgnoreCase("ZARAGOZA", StringUtils.trim(geozoneName))) {
			m347.setGeozone(50);
		}
	}

}

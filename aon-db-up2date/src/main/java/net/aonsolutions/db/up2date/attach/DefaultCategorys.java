package net.aonsolutions.db.up2date.attach;

import java.math.BigInteger;
import java.sql.Connection;
import java.util.ArrayList;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.CategoryTree;
import com.esferalia.aon.jooq.tables.Domain;

import net.aonsolutions.db.up2date.Update;

public class DefaultCategorys implements Update {
	
	public static final DefaultCategorys DEFAULT_CATEGORY = new DefaultCategorys();

	private DefaultCategorys() {
		
	}

	@Override
	public void upgrade(Connection conn) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		DSLContext dslContext = DSL.using(conn, SQLDialect.MARIADB, settings);
		System.out.println("[START]");
		System.out.println("Add default categorys");
		ArrayList<Integer> domains = new ArrayList<>();
		dslContext.select(Domain.DOMAIN.ID).from(Domain.DOMAIN).where(Domain.DOMAIN.PARENT.isNull().and(Domain.DOMAIN.TYPE.eq((byte)1))).fetch().stream().forEach(r -> {
			domains.add(r.get(Domain.DOMAIN.ID));
		});
		for(Integer domain: domains) {
			if(!(dslContext.select(CategoryTree.CATEGORY_TREE.ID)
				.from(CategoryTree.CATEGORY_TREE)
				.where(CategoryTree.CATEGORY_TREE.DOMAIN.eq(domain).and(CategoryTree.CATEGORY_TREE.IS_DELETABLE.eq((byte) 0))).fetch().size() >= 989))
			addDefaultCategories(dslContext, domain);
		}
	}
	
private void addDefaultCategories(DSLContext dslContext, Integer domain) {
		
		String [] categorias = {"Fiscal", "Contable", "Laboral", "Notificaciones", "General", "Listados excel"};
		
		String [] fiscalTags = {"1T", "2T", "3T", "4T", "Anual"};
		String [] contableTags = {"CCAA", "Libros Contables", "Libros Diarios", "Balance situación", "PyG"};
		String [] laboralTags = {"Contrato", "Nómina", "IDC", "Resolución", "Alta", "Baja"};
		String [] notificationTags = {"Hacienda", "Seg. Social", "Inspección de trabajo", "Registro Mercantil"};
		String [] generalTags = {"Escrituras", "Constitución", "Modificación", "Banco", "DNI/NIE", "CIF"};
		String [] excelTags = {"Banco", "Libro Diario", "Facturas"};
		
		String [][] subcategories = { fiscalTags, contableTags, laboralTags, notificationTags, generalTags, excelTags };
		
		String [] administrations = {"AEAT", "Bizkaia", "Gipuzkoa", "Navarra", "Álava", "Canarias"};
		
		String [] aeatModels = {"303", "309", "368", "360", "349", "390", "347", "111", "115", "123", "130", "131", "145", "190", "180", "193", "100", "720", "184", "202", "200", "232", "165", "840", "848", "otros"};
		String [] bizkaia = {"303", "310", "309", "368", "360", "349", "390", "391", "347", "110", "115", "123", "130", "130", "145", "190", "180", "193", "100", "720", "184", "200", "232", "840", "848", "140", "otros"};
		String [] gipuzkoa = {"300", "310", "309", "368", "360", "390", "391", "349", "347", "110", "115", "123", "130", "130", "145", "190", "180", "193", "109", "720", "184", "202", "200", "840", "848", "otros"};
		String [] navarra = {"F69", "309", "368", "360", "349", "F50", "715", "759", "716", "190", "180", "193", "130", "130", "145", "F93", "720", "184", "S91", "S90", "232", "otros"};
		String [] alava = {"303", "310", "309", "368", "390", "391", "360", "349", "347", "110", "115", "123", "130", "131", "145", "190", "180", "193", "100", "720", "184", "200", "232", "840", "841", "842", "848", "otros"};
		String [] canarias = {"420", "425", "415", "412", "347", "111", "115", "123", "130", "131", "145", "190", "180", "193", "100", "720", "184", "202", "200", "232", "165", "840", "848", "otros"};
		
		String [][] models = { aeatModels, bizkaia, gipuzkoa, navarra, alava, canarias };
		
		for(int i = 0; i < categorias.length; i++) {
			dslContext.execute(insertIntoCategoryClause(domain, categorias[i]));
			BigInteger last_id = dslContext.lastID();
			dslContext.execute(insertIntoCategoryTreeClause(last_id, null, domain));
			for(int j = 0; j < subcategories[i].length; j++) {
				dslContext.execute(insertIntoCategoryClause(domain, subcategories[i][j]));
				BigInteger last_id2 = dslContext.lastID();
				dslContext.execute(insertIntoCategoryTreeClause(last_id2, last_id, domain));
				if(i == 0 || (i == 3 && j == 0)) {
					for(int k = 0; k < administrations.length; k++) {
						dslContext.execute(insertIntoCategoryClause(domain, administrations[k]));
						BigInteger last_id3 = dslContext.lastID();
						dslContext.execute(insertIntoCategoryTreeClause(last_id3, last_id2, domain));
						for(int h = 0; h < models[k].length; h++) {
							dslContext.execute(insertIntoCategoryClause(domain, models[k][h]));
							BigInteger last_id4 = dslContext.lastID();
							dslContext.execute(insertIntoCategoryTreeClause(last_id4, last_id3, domain));
						}
					}
				}
			}
		}
		
	}
	
	private String insertIntoCategoryClause(Integer domain, String name) {
		return "INSERT INTO category (domain, name, type, scope, description, url, rattach) VALUES (" + domain + ", \"" + name + "\", 0, null, null, null, null)";
	}
	
	private String insertIntoCategoryTreeClause(BigInteger category, BigInteger parent, Integer domain) {
		return "INSERT INTO category_tree (category, parent, is_visible, is_deletable, domain) VALUES (" + category + ", " + (parent != null ? parent : "NULL") + ", true, false, " + domain + ")";
	}
	
}

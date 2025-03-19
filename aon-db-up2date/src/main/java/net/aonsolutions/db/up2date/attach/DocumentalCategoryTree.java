package net.aonsolutions.db.up2date.attach;

import java.math.BigInteger;
import java.sql.Connection;
import java.util.ArrayList;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Domain;

import net.aonsolutions.db.up2date.Update;

public class DocumentalCategoryTree implements Update {
	
	public static final DocumentalCategoryTree DOCUMENTAL_CATEGORY_TREE = new DocumentalCategoryTree();

	private DocumentalCategoryTree() {
		
	}

	@Override
	public void upgrade(Connection conn) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		DSLContext dslContext = DSL.using(conn, SQLDialect.MARIADB, settings);
		
		System.out.println("[START]");
		System.out.println("Creation table `category_tree`");
		
		// Comprobamos que la tabla category_tree existe o no
		String database = dslContext.fetchOne("SELECT DATABASE()").getValue(0, String.class);
		boolean existsTable = dslContext.fetchExists(dslContext.selectOne().from("information_schema.tables")
		        .where(DSL.field("table_name").eq(DSL.inline("category_tree"))
		        .and(DSL.field("table_schema").eq(DSL.inline(database)))));
		
		// Si la tabla ya existe la migración ya se hizo no es necesario volver a intentarlo ni volver a generar todas las categorías, en caso contrario se realiza
		if(!existsTable) {
			// Creación de la tabla category_tree para crear la jerarquía de categorías
			String create_table = 
					"CREATE TABLE IF NOT EXISTS category_tree ("
					+ "    id INT AUTO_INCREMENT PRIMARY KEY,"
					+ "    id_category INT NOT NULL COMMENT 'identificador de la categoria a la que hace referencia',"
					+ "    id_parent INT NULL COMMENT 'identificador de la categoria padre, si es null es un nodo raiz',"
					+ "    is_visible BOOLEAN DEFAULT TRUE COMMENT 'indica si sera o no visible para los subdominios',"
					+ "    is_deletable BOOLEAN DEFAULT TRUE COMMENT 'indica si se puede borrar o no, asi diferenciamos las categorias por defecto de las creadas por usuarios',"
					+ "    CONSTRAINT fk_category FOREIGN KEY (id_category) REFERENCES category(id) ON DELETE CASCADE,"
					+ "    CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES category_tree(id_category) ON DELETE CASCADE"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Creación de jerarquía en las categorias del documental';";
			
			System.out.println("Creation index for `category_tree` on id_category and id_parent");
			dslContext.execute(create_table);
			
			// Creación del índice en id_category y id_parent para que las búsquedas en el árbol sean rápidas
			String create_index = "CREATE INDEX idx_category_parent ON category_tree(id_category, id_parent);";
			dslContext.execute(create_index);
			
			// Seleccionamos todos los dominios de tipo despacho en esa base de datos para añadirles los campos por defecto
			System.out.println("Select domains to create default categories");
			ArrayList<Integer> domains = new ArrayList<>();
			dslContext.select(Domain.DOMAIN.ID).from(Domain.DOMAIN).where(Domain.DOMAIN.PARENT.isNull().and(Domain.DOMAIN.TYPE.ne((byte)5))).fetch().stream().forEach(r -> {
				domains.add(r.get(Domain.DOMAIN.ID));
			});
			
			// Generamos el árbol de categorias por defecto para todos los dominios de tipo despacho contenidos en dicha base de datos
			for(Integer domain: domains) {
				addDefaultCategories(dslContext, domain);
			}
		}
		System.out.println("[END]");
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
			dslContext.execute(insertIntoCategoryTreeClause(last_id, null));
			for(int j = 0; j < subcategories[i].length; j++) {
				dslContext.execute(insertIntoCategoryClause(domain, subcategories[i][j]));
				BigInteger last_id2 = dslContext.lastID();
				dslContext.execute(insertIntoCategoryTreeClause(last_id2, last_id));
				if(i == 0 || (i == 3 && j == 0)) {
					for(int k = 0; k < administrations.length; k++) {
						dslContext.execute(insertIntoCategoryClause(domain, administrations[k]));
						BigInteger last_id3 = dslContext.lastID();
						dslContext.execute(insertIntoCategoryTreeClause(last_id3, last_id2));
						for(int h = 0; h < models[k].length; h++) {
							dslContext.execute(insertIntoCategoryClause(domain, models[k][h]));
							BigInteger last_id4 = dslContext.lastID();
							dslContext.execute(insertIntoCategoryTreeClause(last_id4, last_id3));
						}
					}
				}
			}
		}
		
	}
	
	private String insertIntoCategoryClause(Integer domain, String name) {
		return "INSERT INTO category (domain, name, type, scope, description, url, rattach) VALUES (" + domain + ", \"" + name + "\", 0, null, null, null, null)";
	}
	
	private String insertIntoCategoryTreeClause(BigInteger category, BigInteger parent) {
		return "INSERT INTO category_tree (id_category, id_parent, is_visible, is_deletable) VALUES (" + category + ", " + (parent != null ? parent : "NULL") + ", true, false)";
	}
	
}

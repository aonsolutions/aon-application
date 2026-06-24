package net.aonsolutions.db.up2date.security;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class UserScopeAlterStartEndAuditory implements Update {
	
	public static final UserScopeAlterStartEndAuditory USERSCOPEALTERSTARTENDAUDITORY = new UserScopeAlterStartEndAuditory();
	

	private UserScopeAlterStartEndAuditory() {}

	@Override
	public void upgrade(Connection connection) {
		
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
	
		dslContext.transaction( (config) -> {
			
			String sql = "ALTER TABLE `user_scope` "
				+ "ADD COLUMN `start_date` date DEFAULT NULL COMMENT 'Fecha inicio autorizado'";
			
			String sql2 = "ALTER TABLE `user_scope` "
				+ "ADD COLUMN `end_date` date DEFAULT NULL COMMENT 'Fecha fin autorizado'";
			
			String sql3 = "ALTER TABLE `user_scope` "
				+ "ADD COLUMN `owner` int(11) DEFAULT NULL COMMENT 'Usuario propietario'";
			
			String sql4 = "ALTER TABLE `user_scope` "
				+ "ADD KEY `IDX_USER_SCOPE_OWNER` (`owner`)";
			
			String sql5 = "ALTER TABLE `user_scope` "
				+ "ADD CONSTRAINT `FK_USER_SCOPE_OWNER` FOREIGN KEY (`owner`) REFERENCES `user` (`id`)";
			
			String sql6 = "ALTER TABLE `user_scope` "
				+ "ADD COLUMN `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion'";
			
			String sql7 = "ALTER TABLE `user_scope` "
				+ "ADD COLUMN `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion'";
			
			String sql8 = "ALTER TABLE `user_scope` "
				+ "ADD COLUMN `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion'";
			
			String sql9 = "ALTER TABLE `user_scope` "
				+ "ADD COLUMN `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion'";
	
			dslContext.execute(sql);
			dslContext.execute(sql2);
			dslContext.execute(sql3);
			dslContext.execute(sql4);
			dslContext.execute(sql5);
			dslContext.execute(sql6);
			dslContext.execute(sql7);
			dslContext.execute(sql8);
			dslContext.execute(sql9);
			
		});
	}

	
}
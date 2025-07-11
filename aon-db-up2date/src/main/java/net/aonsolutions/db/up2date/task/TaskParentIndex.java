package net.aonsolutions.db.up2date.task;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class TaskParentIndex implements Update {



	public static final TaskParentIndex TASK_PARENT_INDEX = new TaskParentIndex();

	private TaskParentIndex() {
		// private constructor to prevent instantiation
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		dslContext.resultQuery("SHOW INDEX FROM `task` WHERE `column_name` = 'parent'")
		.fetchOptional()
		.ifPresentOrElse(
			index -> System.out.println("Index on 'parent' already exists in 'task' table."),
			() ->  dslContext.execute("ALTER TABLE task ADD INDEX `IDX_TASK_PARENT` (`parent`);")
		);
	}

}

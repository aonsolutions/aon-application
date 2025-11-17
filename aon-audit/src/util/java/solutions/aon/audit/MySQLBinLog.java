package solutions.aon.audit;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.jooq.DDLExportConfiguration;
import org.jooq.DDLFlag;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Queries;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.AonMaster;
import com.esferalia.aon.watson.util.AonStringUtils;

public class MySQLBinLog {
	
	
	
	static class Parser {
		
		ParserState state ;
		
		Parser(Schema schema) {
			this.state = new MainState(schema);
		}
		
		String parse(String line) {
			return state.parse(line, this);
		}

		void setState(ParserState state) {
			this.state = state;
		}
		
	}
	
	interface  ParserState {
		boolean accept(String line);
		String parse(String line, Parser parser);
	}

	static class MainState implements ParserState {
		
		Schema schema;
		
		ParserState [] states ;
		
		
		public MainState(Schema schema) {
			this.schema = schema;
			states = new ParserState[] {
					new InsertState(this),
					new UpdateState(this),
					new DeleteState(this)
			};		}
		
		public Schema getSchema() {
			return schema;
		}
		
		@Override
		public boolean accept(String line) {
			return !line.startsWith("###");
		}
		
		@Override
		public String parse(String line, Parser parser) {
			if (accept(line)) {
				return line;
			} else {
				for (ParserState state : states) {
					if (state.accept(line)) {
						parser.setState(state);
						return parser.state.parse(line, parser);
					}
				}
				throw new IllegalStateException("No state found for line: " + line);
			}
		}
		
	}

	static abstract class StmtState implements ParserState {

		String table;
		String database;
		
		MainState mainState;
		
		public StmtState(MainState mainState) {
			this.mainState = mainState;
		}
		
		MainState getMainState() {
			return mainState;
		}
		
		public String getTable() {
			return table;
		}
		
		StmtState setTable(String table) {
			this.table = table;
			return this;
		}
		
		public String getDatabase() {
			return database;
		}
		
		StmtState setDatabase(String database) {
			this.database = database;
			return this;
		}
		

		@Override
		public String parse(String line, Parser parser) {
			parser.setState(new ColumsState(this));
			return String.format("INSERT IGNORE INTO `%s`;", table);
		}

	}

	static class InsertState extends StmtState {

		static final Pattern INSERT_PATTERN = Pattern
				.compile("###\\s+INSERT\\s+INTO\\s+`(?<schema>.*)`\\.`(?<table>.*)`.*");
		
		private InsertState(MainState mainState) {
			super(mainState);
		}

		@Override
		public boolean accept(String line) {
			Matcher matcher = INSERT_PATTERN.matcher(line);
			if (matcher.matches()) {
				this.table = matcher.group("table");
				this.database = matcher.group("schema");
				return true;
			} else {
				return false;
			}
		}

	}

	static class UpdateState extends StmtState {

		static final Pattern UPDATE_PATTERN = Pattern
				.compile("###\\s+UPDATE\\s+`(?<schema>.*)`\\.`(?<table>.*)`.*");
		
		private UpdateState(MainState mainState) {
			super(mainState);
		}
		
		@Override
		public boolean accept(String line) {
			Matcher matcher = UPDATE_PATTERN.matcher(line);
			if (matcher.matches()) {
				this.table = matcher.group("table");
				this.database = matcher.group("schema");
				return true;
			} else {
				return false;
			}
		}

	}

	static class DeleteState extends StmtState {

		static final Pattern UPDATE_PATTERN = Pattern
				.compile("###\\s+DELETE\\s*FROM\\s+`(?<schema>.*)`\\.`(?<table>.*)`.*");
		
		private DeleteState(MainState mainState) {
			super(mainState);
		}

		@Override
		public boolean accept(String line) {
			Matcher matcher = UPDATE_PATTERN.matcher(line);
			if (matcher.matches()) {
				this.table = matcher.group("table");
				this.database = matcher.group("schema");
				return true;
			} else {
				return false;
			}
		}

	}

	static class ColumsState implements ParserState {

		static final Pattern ACCEPT_PATTERN = Pattern
				.compile("^###\\s*(@|SET|WHERE).*");

		static final Pattern COLUMN_VALUE_PATTERN = Pattern
				.compile("###\\s+@(?<column>\\d+)\\s*=\\s*(?<value>.*)");

		StmtState stmtState;
		
		private ColumsState(StmtState stmtState) {
			this.stmtState = stmtState;
		}

		@Override
		public boolean accept(String line) {
			return ACCEPT_PATTERN.matcher(line).find();
		}

		@Override
		public String parse(String line, Parser parser) {
			if ( !accept(line)) {
				parser.setState(stmtState.getMainState());
				return parser.state.parse(line, parser);
			} else {
				Matcher matcher = COLUMN_VALUE_PATTERN.matcher(line);
				if (matcher.matches()) {
					String value = matcher.group("value");
					String column = matcher.group("column");
					Field<?> field = getField(Integer.parseInt(column));
					if ( field == null ) {
						throw new IllegalStateException("No field found for column index: " + column + " in table: " + stmtState.getTable());
					}
					return String.format("SET `%s` = %s,", field.getName(), value);
				} else  {
					return "";
				} 
			} 
		}
		
		private Field<?> getField(int columnIndex) {
			return stmtState.getMainState().getSchema().getTable(stmtState.getTable()).field(columnIndex -1);
		}
		

	}

	static Table<?>[] getAuditTables4(Schema schema) {
		return schema.getTables().stream()
				.map(AuditTable::new)
				.toArray(Table<?>[]::new);
	}
	
	static Queries getDDL(DSLContext dslContext,Schema schema) {
		Table<?>[] auditTables = getAuditTables4(schema);
		DDLExportConfiguration config = 
				new DDLExportConfiguration()
				
				.respectColumnOrder(true)
				.respectCatalogOrder(true)

				.createTableIfNotExists(true)
				.createSchemaIfNotExists(true)
				
				.flags(DDLFlag.TABLE, DDLFlag.INDEX)
				;
		return dslContext.ddl(auditTables, config);
	} 
	
	
	static Stream<String> parse(InputStream is, String database) {
		
		Parser parser = new Parser(AonMaster.AON_MASTER);
		InputStreamReader reader = new InputStreamReader(is);
		LineNumberReader lineReader = new LineNumberReader(reader);
		
		return
		lineReader.lines()
		.filter(AonStringUtils::isNotBlank)
		.filter(MySQLBinLog::isNotCommentLine)
		.map(parser::parse);
		
	}
	
	static boolean isNotCommentLine(String line) {
		return  !AonStringUtils.startsWith(line, "#") 
				|| AonStringUtils.startsWith(line, "###");
	}
	
	
	public static void main(String[] args) {
		
		String database = args[0];
		
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		DSLContext dslContext = DSL.using(SQLDialect.MYSQL, settings);
		
		parse(System.in, database).forEach(System.out::println);
		
		//System.out.println(dslContext.createDatabaseIfNotExists("Hola").getSQL());
		//System.out.println(getDDL(dslContext, AonMaster.AON_MASTER).getSQL());
	}
	
	
	

}

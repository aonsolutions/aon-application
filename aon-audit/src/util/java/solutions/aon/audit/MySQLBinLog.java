package solutions.aon.audit;

import static solutions.aon.audit.AuditTable.AuditFields.AUDIT_DIGEST;
import static solutions.aon.audit.AuditTable.AuditFields.AUDIT_EVENT;
import static solutions.aon.audit.AuditTable.AuditFields.AUDIT_SCHEMA;
import static solutions.aon.audit.AuditTable.AuditFields.AUDIT_TIMESTAMP;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.UniqueKey;
import org.jooq.conf.ParamType;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.AonMaster;
import com.esferalia.aon.watson.server.codec.AonDigestUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import solutions.aon.audit.MySQLBinLog.StmtState.EventType;

public class MySQLBinLog {
	
	
	
	static class Parser {
		
		ParserState state ;
		
		Parser(Schema schema, String ...databases) {
			this.state = new MainState(schema, databases);
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
		
		public MainState(Schema schema, String ...databases) {
			this.schema = schema;
			states = new ParserState[] {
					new InsertState(this, databases),
					new UpdateState(this, databases),
					new DeleteState(this, databases),
					new DDLState(this),
			};		}
		
		public Schema getSchema() {
			return schema;
		}
		
		@Override
		public boolean accept(String line) {
			return !line.startsWith("###") && !isDDLStatement(line);
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
				return null; // skip line
				//throw new IllegalStateException("No state found for line: " + line);
			}
		}
		
		private boolean isDDLStatement(String line) {
			return DDLState.DDL_PATTERN.matcher(line).matches();
		}
		
	}
	
	static class DDLState implements ParserState {
		// /*!80016 SET @@session.default_table_encryption=0*//*!*/;
		static final Pattern DDL_PATTERN = Pattern
				.compile("^\\s*(use|create|alter|drop|.*@@session.default_table_encryption).*", Pattern.CASE_INSENSITIVE);
		
		static final Pattern END_PATTERN = Pattern
				.compile("^/\\*!\\*/;\\s*", Pattern.CASE_INSENSITIVE);
		
		MainState mainState;
		
		public DDLState(MainState mainState) {
			this.mainState = mainState;
		}

		@Override
		public String parse(String line, Parser parser) {
			//System.err.println(line);
			if ( END_PATTERN.matcher(line).matches() ) {
				parser.setState(mainState);
				return null;
			} else {
				return null;
			}
		}

		@Override
		public boolean accept(String line) {
			return DDL_PATTERN.matcher(line).matches();
		}
		
		

	}
	
	static abstract class StmtState implements ParserState {
		
		enum EventType {
			INSERT,
			UPDATE,
			DELETE
		}
		
		String table;
		String database;
		
		String databases[];
		
		MainState mainState;
		
		public StmtState(MainState mainState, String ...databases) {
			this.databases = databases;
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
			return "SET @AUDIT_TIMESTAMP=@@session.original_commit_timestamp;\n";
		}
		
		public boolean checkDatabase() {
			return Arrays.stream(databases)
					.anyMatch(db -> database.matches(db));
		}
		
		abstract EventType getEventType();

	}

	static class InsertState extends StmtState {

		static final Pattern INSERT_PATTERN = Pattern
				.compile("###\\s+INSERT\\s+INTO\\s+`(?<schema>.*)`\\.`(?<table>.*)`.*");
		
		private InsertState(MainState mainState, String ...databases) {
			super(mainState, databases);
		}

		@Override
		public boolean accept(String line) {
			Matcher matcher = INSERT_PATTERN.matcher(line);
			if (matcher.matches()) {
				this.table = matcher.group("table");
				this.database = matcher.group("schema");
				return checkDatabase();
			} else {
				return false;
			}
		}
		
		EventType getEventType() {
			return EventType.INSERT;
		}
	}

	static class UpdateState extends StmtState {

		static final Pattern UPDATE_PATTERN = Pattern
				.compile("###\\s+UPDATE\\s+`(?<schema>.*)`\\.`(?<table>.*)`.*");
		
		private UpdateState(MainState mainState, String ...databases) {
			super(mainState, databases);
		}
		
		@Override
		public boolean accept(String line) {
			Matcher matcher = UPDATE_PATTERN.matcher(line);
			if (matcher.matches()) {
				this.table = matcher.group("table");
				this.database = matcher.group("schema");
				return checkDatabase();
			} else {
				return false;
			}
		}
		
		EventType getEventType() {
			return EventType.UPDATE;
		}

	}

	static class DeleteState extends StmtState {

		static final Pattern UPDATE_PATTERN = Pattern
				.compile("###\\s+DELETE\\s*FROM\\s+`(?<schema>.*)`\\.`(?<table>.*)`.*");
		
		private DeleteState(MainState mainState, String ...databases) {
			super(mainState, databases);
		}

		@Override
		public boolean accept(String line) {
			Matcher matcher = UPDATE_PATTERN.matcher(line);
			if (matcher.matches()) {
				this.table = matcher.group("table");
				this.database = matcher.group("schema");
				return checkDatabase();
			} else {
				return false;
			}
		}
		
		EventType getEventType() {
			return EventType.DELETE;
		}

	}

	static class ColumsState implements ParserState {

		static final Pattern SET_PATTERN = Pattern
				.compile("^###\\s*(SET).*");

		static final Pattern ACCEPT_PATTERN = Pattern
				.compile("^###\\s*(@|SET|WHERE).*");

		static final Pattern COLUMN_VALUE_PATTERN = Pattern
				.compile("###\\s+@(?<column>\\d+)\\s*=\\s*(?<value>.*)", 
						Pattern.DOTALL 
						| Pattern.MULTILINE 
						| Pattern.CASE_INSENSITIVE 
						| Pattern.UNICODE_CHARACTER_CLASS);

		static final Pattern NEGATIVE_VALUE_PATTERN = Pattern
				.compile("(?<value>-\\d+)\\s*\\(\\d+\\)");

		StmtState stmtState;
		Map<Integer, String> values;
		
		private ColumsState(StmtState stmtState) {
			this.stmtState = stmtState;
			this.values = new HashMap<>();
		}

		@Override
		public boolean accept(String line) {
			return ACCEPT_PATTERN.matcher(line).find();
		}

		@Override
		public String parse(String line, Parser parser) {
			if ( !accept(line)) {
				// end of statement
				parser.setState(stmtState.getMainState());
				String binlogCols = getBinlogFieds(0);
				resetValues();
				String parsedLine = parser.state.parse(line, parser);
				return String.format("%s;%n%s", binlogCols, parsedLine != null ? parsedLine : "");
			} else {
				Matcher matcher = COLUMN_VALUE_PATTERN.matcher(line);
				if (matcher.matches()) {
					// column value line @column = value
					String value = matcher.group("value");
					String column = matcher.group("column");
					Integer columnIndex = Integer.parseInt(column);
					Field<?> field = getField(columnIndex);
					if ( field == null ) {
						System.err.println("No field found for column index: " + column + " in table: " + stmtState.getTable());
						return null;
					}
					value = checkValue(value);
					values.put(columnIndex, value);
					return String.format("%s,", formatSET(field, value));
				} else  {
					// start of SET or WHERE
					StringBuilder stringBuilder = new StringBuilder();
					if ( is(EventType.UPDATE) && SET_PATTERN.matcher(line).find() ) {
						// before SET clause in UPDATE statement. INSERT where previous values
						stringBuilder.append(String.format("%s;%n", getBinlogFieds(-1) ));
						stringBuilder.append(String.format("COMMIT/*!*/;%n"));
						stringBuilder.append(String.format("BEGIN;%n/*!*/;%n"));
						resetValues();
					} 
					stringBuilder.append(String.format("INSERT IGNORE INTO `%s`%nSET", stmtState.getTable()));
					return stringBuilder.toString();
				} 
			} 
		}

		protected boolean is(EventType eventType) {
			return stmtState.getEventType() == eventType;
		}
		
		protected String checkValue(String value) {
			Matcher negativeMatcher = NEGATIVE_VALUE_PATTERN.matcher(value);
			if ( negativeMatcher.matches() ) {
				return negativeMatcher.group("value");
			}
			return value;
		}
		
		protected <T> String formatSET(Field<T> field, String value) {
			return String.format("`%s` = %s", field.getName(), value);
		}
		
		private Field<?> getField(int columnIndex) {
			return stmtState.getMainState().getSchema().getTable(stmtState.getTable()).field(columnIndex -1);
		}
		
		private String getValue(Table<?> table, Field<?> field) {
			int count = table.fields().length;
			for (int i = 0; i < count; i++) {
				if ( table.field(i).equals(field) ) {
					//0-based index of the field
					return values.get(i + 1);
				}
			}
			throw new IllegalStateException("Field not found in table: " + table.getName() + " for field: " + field.getName());
		}
		
		private String getPrimaryKeyWhere() {
			Table<?> table = stmtState.getMainState().getSchema().getTable(stmtState.getTable());
			UniqueKey<?> primaryKey = table.getPrimaryKey();
			return primaryKey.getFields().stream().map(field -> String.format("`%s` = %s", field.getName(), getValue(table, field)) )
					.collect(Collectors.joining(" AND "));
		}

		private String getBinlogFieds( int offset ) {
			String binlogTime = formatSET(AUDIT_TIMESTAMP, String.format("(@AUDIT_TIMESTAMP + %1$d )", offset + 1));
			String binlogSchema = formatSET(AUDIT_SCHEMA, String.format("'%s'",stmtState.getDatabase()));
			String binlogEvent = formatSET(AUDIT_EVENT, Integer.toString(stmtState.getEventType().ordinal()));
			String md5 = getMd5();
			String digest = formatSET(AuditTable.AuditFields.AUDIT_DIGEST, 
					String.format("CONCAT('%1$s', (SELECT GREATEST(0,COUNT(*) + (%5$d)) FROM `%2$s` AS `order` WHERE %3$s AND `%4$s` < ( @AUDIT_TIMESTAMP + %6$d )  ), '%7$s' )",
							md5.substring(0, 16),
							stmtState.getTable() , 
							getPrimaryKeyWhere(), 
							AUDIT_TIMESTAMP.getName(),
							offset,
							offset + 1,
							md5.substring(16) )
					);
			return String.format("%s,%n%s,%n%s,%n%s", binlogTime, binlogSchema, binlogEvent, digest);
		}

		private void resetValues() {
			values.clear();
		}
		
		private String getMd5() {
			StringBuilder sb = new StringBuilder();
			values.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).forEach(sb::append);
			sb.append(stmtState.getDatabase());
			sb.append(stmtState.getEventType() == EventType.DELETE ? EventType.DELETE.name() : EventType.UPDATE.name());
			return AonDigestUtils.md5Hex(sb.toString());
		}
	}
	
	static Stream<String> parse(InputStream is, String ...databases) {
		
		InputStreamReader reader = new InputStreamReader(is);
		LineNumberReader lineReader = new LineNumberReader(reader);
		Parser parser = new Parser(AonMaster.AON_MASTER, databases);
		
		return
		lineReader.lines()
		.filter(AonStringUtils::isNotBlank)
		.filter(MySQLBinLog::isNotCommentLine)
		.filter(MySQLBinLog::isNotDDLStatement)
		.filter(MySQLBinLog::isNotSetSessionGtidNext)
		.map(parser::parse)
		.filter(Objects::nonNull);
		
	}
	
	static boolean isNotCommentLine(String line) {
		return  !AonStringUtils.startsWith(line, "#") 
				|| AonStringUtils.startsWith(line, "###");
	}
	
	static boolean isDDLStatement(String line) {
		return  AonStringUtils.startsWith(line, "use ") 
				|| AonStringUtils.startsWith(line, "DROP")
				|| AonStringUtils.startsWith(line, "ALTER")
				|| AonStringUtils.startsWith(line, "CREATE")
				;
	}
	
	static boolean isNotDDLStatement(String line) {
		return  !isDDLStatement(line);
	}
	
	static boolean isNotSetSessionGtidNext(String line) {
		return !AonStringUtils.startsWith(line, "SET @@SESSION.GTID_NEXT=");
	}

	public static void main(String[] args) {
		
		String [] databases = args;
		
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setRenderFormatted(true);
		settings.setParamType(ParamType.INLINED);
		settings.setRenderQuotedNames(RenderQuotedNames.EXPLICIT_DEFAULT_QUOTED);
		DSLContext dslContext = DSL.using(SQLDialect.MYSQL, settings);
		
		parse(System.in, databases).forEach(System.out::println);
		
	}
	
	
	

}

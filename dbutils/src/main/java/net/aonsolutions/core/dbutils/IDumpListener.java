package net.aonsolutions.core.dbutils;

public interface IDumpListener {

	void initDump( String databaseName, String version, int numberOfTables );
	
	void startDumpTable( String table );
	void dumpTable( String table, int rowCount );
	void endDumpTable( String table, int rowCount );
	
	void finishDump();
	
}

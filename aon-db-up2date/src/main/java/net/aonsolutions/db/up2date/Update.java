package net.aonsolutions.db.up2date;

import java.sql.Connection;

public interface Update {
	
	void upgrade( Connection conn);

}

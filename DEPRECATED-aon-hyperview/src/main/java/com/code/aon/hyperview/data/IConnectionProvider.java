package com.code.aon.hyperview.data;

import java.sql.Connection;

public interface IConnectionProvider {
	
	Connection getConnection() throws ConnectionProviderException;

}

import MariaDB from 'mariadb';

export class Database {
  public static getConnection(): Promise<MariaDB.Connection> {
    const connectionConfig: MariaDB.ConnectionConfig = {
      host: process.env.DB_HOST,
      user: process.env.DB_USER,
      password: process.env.DB_PASSWD,
      database: process.env.DB_NAME,
    };
    return MariaDB.createConnection(connectionConfig);
  }

  public static connect(connection: MariaDB.Connection, database: string): Promise<MariaDB.Connection> {
    return connection.query(`use \`${database}\``).then(() => connection);
  }

  public static getDatabases(connection: MariaDB.Connection): Promise<string[]> {
    return connection.query(`SHOW DATABASES`).then(rows => rows.map(row => row.Database));
  }
}

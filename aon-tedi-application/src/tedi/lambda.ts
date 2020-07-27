import AWS from 'aws-sdk';
import MariaDB from 'mariadb';
import { Registry } from '../tedi-ewok/TediEwok';

function getConnection(): Promise<MariaDB.Connection> {
  const connectionConfig: MariaDB.ConnectionConfig = {
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWD,
    database: 'ayudat-aonsolutions-net',
  };
  return MariaDB.createConnection(connectionConfig);
}

function connect(connection: MariaDB.Connection, database: string): Promise<MariaDB.Connection> {
  return connection.query(`use \`${database}\``).then(() => connection);
}

function getDatabases(connection: MariaDB.Connection): Promise<string[]> {
  return connection.query(`SHOW DATABASES`).then(rows => rows.map(row => row.Database));
}

function getRegistries(connection: MariaDB.Connection, documents: string[]): Promise<Registry[]> {
  return new Promise((resolve, reject) => {
    connection
      .query({
        sql: `
    SELECT * 
    FROM registry
    LEFT JOIN raddress ON ( registry.id = raddress.registry )
    LEFT JOIN geozone ON ( raddress.geozone = geozone.id )
    WHERE document IN ('${documents.join(',')}')
    `,
        nestTables: true,
      })
      .then((resultSet: any[]) =>
        resultSet.map(row => {
          const registry: Registry = {
            document: row.document,
          };
          return registry;
        }),
      )
      .then(registries => resolve(registries))
      .catch(error => {
        // tslint:disable-next-line: no-console
        console.error(error);

        reject();
      });
  });
}
module.exports.getConnection = getConnection;
module.exports.getRegistries = getRegistries;

module.exports.getRegistriesHandler = (event: any, context: any, callback: (err: AWS.AWSError | null, data?: any | null) => void) => {
  const queryStringParameters = event.queryStringParameters;
  const documents: string[] = queryStringParameters.documents.split(',');

  // tslint:disable-next-line: no-console
  console.error(documents);

  getConnection()
    .then(connection => getDatabases(connection).then(databases => databases.map(database => getRegistries(connection, documents))))
    .then(registries => Promise.all(registries))
    .then(registries => registries.reduce((acc: Registry[], val) => acc.concat(val), []))
    .then(registries => {
      const response = {
        statusCode: 200,
        headers: {
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
        },
        body: JSON.stringify(registries),
      };
      callback(null, response);
    })
    .catch(reason => callback(reason, null));
};

module.exports.getRegistriesHandler(
  {
    queryStringParameters: {
      documents: 'A50141969,B50442169',
    },
  },
  {},
  (error, response) => {
    if (error) {
      // tslint:disable-next-line: no-console
      console.error(error);
    } else {
      // tslint:disable-next-line: no-console
      console.info(response);
    }
  },
);

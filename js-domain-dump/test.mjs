import mysql from 'mysql';
import dump  from './dump.mjs';

const connection = mysql.createConnection({
  host     : process.env.DB_HOST,
  user     : process.env.DB_USER,
  password : process.env.DB_PASSWD,
  port     : process.env.DB_PORT
});

const domain = process.env.DB_DOMAIN;

dump(connection, domain, process.stdout).finally( () => { process.exit(); } );


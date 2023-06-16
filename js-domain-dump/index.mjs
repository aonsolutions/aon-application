import mysql from 'mysql';
import dump  from './dump.mjs';

const connection = mysql.createConnection({
  host     : process.env.DB_HOST,
  user     : process.env.DB_USER,
  password : process.env.DB_PASSWD,
  port     : process.env.DB_PORT
});

export const handler = awslambda.streamifyResponse(
    async (event, responseStream, context) => {
        responseStream.setContentType('text/javascript');
        //responseStream.write(JSON.stringify(event));
        let domain = event.queryStringParameters.domain;
        dump(connection, domain, responseStream).finally( () => {responseStream.end();} );
    }
);


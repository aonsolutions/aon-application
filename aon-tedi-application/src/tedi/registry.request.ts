import { ClientRequest, IncomingMessage } from 'http';
import { request, RequestOptions } from 'https';
import { Registry } from '../tedi-ewok/TediEwok';

export class TediRegistry {
  public static getRegistries(documents: string[]): Promise<Registry[]> {
    // tslint:disable-next-line: no-console
    // console.log(`NIFs: ${JSON.stringify(documents)} :-( !!!!`);
    return new Promise((resolve, reject) => {
      const options: RequestOptions = {
        hostname: '55evus1cy8.execute-api.eu-west-1.amazonaws.com',
        port: 443,
        path: `/default/registries?documents=${documents.join(',')}`,
        method: 'GET',
        headers: {
          Origin: 'http://127.0.0.1:80',
        },
      };

      const req: ClientRequest = request(options, (res: IncomingMessage) => {
        // tslint:disable-next-line: no-console
        // console.log(`statusCode: ${res.statusCode}`);

        let data: string = '';

        res.on('data', d => {
          data += d;
        });

        res.on('end', () => {
          // tslint:disable-next-line: no-console
          // console.log(`data: ${data}`);
          resolve(JSON.parse(data));
        });
      });

      req.on('error', error => {
        // tslint:disable-next-line: no-console
        console.error(error);
        reject(error);
      });

      req.end();
    });
  }
}

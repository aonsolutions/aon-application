import { ClientRequest, IncomingMessage } from 'http';
import { request, RequestOptions } from 'https';
import Jimp from 'jimp/es';
import { PDFExtractOptions, PDFExtractResult } from 'pdf.js-extract';
import { Invoice, TediImportInvoicesInfo } from '../tedi-ewok/TediEwok';
import { AutoML } from '../tedi-pdf-parser/invoice/AutoML';
import { TediPdfParser } from '../tedi-pdf-parser/TediPdfParser';

export class TediImgParser {
  public static parse(info: TediImportInvoicesInfo): Promise<Invoice> {
    const imgExtract: ImgExtract = new ImgExtract();
    const options: PDFExtractOptions = { disableCombineTextItems: true };
    const buffer: Buffer = info.content instanceof Buffer ? info.content : Buffer.from(info.content, 'base64');

    return new Promise((resolve, reject) =>
      imgExtract.resize(buffer).then(rbuffer =>
        imgExtract.extractBuffer(rbuffer, options, (err: Error | null, result: PDFExtractResult | undefined) => {
          if (err !== null) {
            reject(err);
          } else if (result === undefined) {
            reject(new Error('No Data'));
          } else {
            try {
              resolve(TediPdfParser.precog(result));
            } catch (errr) {
              AutoML.predict(info, result).subscribe(
                (invoice: Invoice) => resolve(AutoML.insight(invoice)),
                errrr => reject(errrr),
              );
            }
          }
        }),
      ),
    );
  }
}

export class ImgExtract {
  public resize(buffer: Buffer): Promise<Buffer> {
    return Jimp.read(buffer)
      .then(image => {
        // Do stuff with the image.
        return (
          image
            .greyscale()
            .scaleToFit(1024, 768) // resize
            // .resize(Jimp.AUTO, 768)
            .getBufferAsync(image.getMIME())
        );
      })
      .catch(err => {
        // Handle an exception.
        return Promise.reject(err);
      });
  }

  public extractBuffer(buffer: Buffer, opts: PDFExtractOptions, callback: (err: Error | null, pdf?: PDFExtractResult) => void): void {
    const options: RequestOptions = {
      hostname: '55evus1cy8.execute-api.eu-west-1.amazonaws.com',
      port: 443,
      path: '/default/google-cloud-vision',
      // path: '/default/amazon-textract',
      method: 'POST',
      headers: {
        Origin: 'http://127.0.0.1:80',
        'Content-Type': 'image/jpg',
        'Content-Length': buffer.length,
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
        callback(null, JSON.parse(data));
      });
    });

    req.on('error', error => {
      // tslint:disable-next-line: no-console
      console.error(error);
      callback(error);
    });

    req.write(buffer);
    req.end();
  }
}

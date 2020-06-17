import { existsSync, readFile } from 'fs';
import { PDFExtract, PDFExtractOptions, PDFExtractResult } from 'pdf.js-extract';
import { Observable, Observer } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { TediAutoML } from '../tedi-automl/TediAutoML';
import { Invoice, TediError, TediImportInvoicesInfo } from '../tedi-ewok/TediEwok';
import { Amazon } from './invoice/Amazon';
import { AutoML } from './invoice/AutoML';
import { BipDrive } from './invoice/BipDrive';
import { CaixaBankSolred } from './invoice/CaixaBankSolred';
import { CaixaBankViat } from './invoice/CaixaBankViat';
import { Endesa } from './invoice/Endesa';
import { Euskaltel } from './invoice/Euskaltel';
import { IberdrolaClientes } from './invoice/IberdrolaClientes';
import { IberdrolaCUR } from './invoice/IberdrolaCUR';
import { Ionos } from './invoice/Ionos';
import { Makro } from './invoice/Makro';
import { MovistarFijo } from './invoice/MovistarFijo';
import { MovistarFusion } from './invoice/MovistarFusion';
import { MovistarMovil } from './invoice/MovistarMovil';
import { Naturgy } from './invoice/Naturgy';
import { Orange } from './invoice/Orange';
import { Renfe } from './invoice/Renfe';
import { Securitas } from './invoice/Securitas';
import { TAPO } from './invoice/TAPO';
import { Vodafone } from './invoice/Vodafone';

const FACTORIES = [
  (r: PDFExtractResult) => Vodafone.extract(r),
  (r: PDFExtractResult) => MovistarFusion.extract(r),
  (r: PDFExtractResult) => MovistarFijo.extract(r),
  (r: PDFExtractResult) => Naturgy.extract(r),
  (r: PDFExtractResult) => IberdrolaClientes.extract(r),
  (r: PDFExtractResult) => IberdrolaCUR.extract(r),
  (r: PDFExtractResult) => Orange.extract(r),
  (r: PDFExtractResult) => TAPO.extract(r),
  (r: PDFExtractResult) => Renfe.extract(r),
  (r: PDFExtractResult) => Makro.extract(r),
  (r: PDFExtractResult) => Amazon.extract(r),
  (r: PDFExtractResult) => MovistarMovil.extract(r),
  (r: PDFExtractResult) => Euskaltel.extract(r),
  (r: PDFExtractResult) => Securitas.extract(r),
  (r: PDFExtractResult) => Endesa.extract(r),
  (r: PDFExtractResult) => Ionos.extract(r),
  (r: PDFExtractResult) => CaixaBankSolred.extract(r),
  (r: PDFExtractResult) => CaixaBankViat.extract(r),
  (r: PDFExtractResult) => BipDrive.extract(r),
];

export class TediUnknownInvoiceFormatError extends TediError {
  constructor(message: string) {
    super(message);
    this.code = 'TediUnknownInvoiceFormat';
  }
}

export class TediPdfParser {
  public static extractFromFile(filename: string): Observable<Invoice> {
    if (existsSync(filename)) {
      return Observable.create((obser: Observer<Buffer>) => {
        readFile(filename, {}, (err, data) => {
          if (err) {
            obser.error(err);
          } else {
            obser.next(data);
            obser.complete();
          }
        });
      }).pipe(switchMap((content: Buffer) => TediPdfParser.extract(content)));
    } else {
      throw new Error('Unable to find file: ' + filename);
    }
  }

  public static extract(buffer: Buffer): Observable<Invoice> {
    return Observable.create((obser: Observer<PDFExtractResult>) => {
      const pdfExtract = new PDFExtract();
      const options: PDFExtractOptions = { disableCombineTextItems: true };
      pdfExtract.extractBuffer(buffer, options, (err, data) => {
        if (err) {
          return obser.error(err);
        }
        if (!data) {
          return obser.error(new Error('No Data'));
        }

        obser.next(data);
        obser.complete();
      });
    }).pipe(map((result: PDFExtractResult) => TediPdfParser.mapToInvoice(result)));
  }

  public static predictFromFile(filename: string, info: TediImportInvoicesInfo): Observable<Invoice> {
    if (existsSync(filename)) {
      return Observable.create((obser: Observer<Buffer>) => {
        readFile(filename, {}, (err, data) => {
          if (err) {
            obser.error(err);
          } else {
            obser.next(data);
            obser.complete();
          }
        });
      }).pipe(
        switchMap((content: Buffer) => {
          info = info || { content: '', contentType: 'application/pdf' };
          info.content = content;

          return TediPdfParser.predict(info);
        }),
      );
    } else {
      throw new Error('Unable to find file: ' + filename);
    }
  }

  public static predict(info: TediImportInvoicesInfo): Observable<Invoice> {
    return Observable.create((obser: Observer<PDFExtractResult>) => {
      const pdfExtract = new PDFExtract();
      const options: PDFExtractOptions = { disableCombineTextItems: true };
      const content: Buffer = info.content instanceof Buffer ? info.content : Buffer.from(info.content, 'base64');
      pdfExtract.extractBuffer(content, options, (err, data) => {
        if (err) {
          return obser.error(err);
        }
        if (!data) {
          return obser.error(new Error('No Data'));
        }

        obser.next(data);
        obser.complete();
      });
    }).pipe(switchMap((result: PDFExtractResult) => AutoML.predict(info, result)));
  }

  public static parse(info: TediImportInvoicesInfo): Promise<Invoice> {
    const pdfExtract = new PDFExtract();
    const options: PDFExtractOptions = { disableCombineTextItems: true };
    const buffer: Buffer = info.content instanceof Buffer ? info.content : Buffer.from(info.content, 'base64');

    return new Promise((resolve, reject) =>
      pdfExtract.extractBuffer(buffer, options, (err: Error | null, data: PDFExtractResult | undefined) => {
        if (err !== null) {
          reject(err);
        } else if (data === undefined) {
          reject(new Error('No Data'));
        } else {
          try {
            resolve(this.mapToInvoice(data));
          } catch (errr) {
            AutoML.predict(info, data).subscribe(
              (invoice: Invoice) => resolve(TediAutoML.insight(invoice)),
              errrr => reject(errrr),
            );
          }
        }
      }),
    );
  }

  private static mapToInvoice(result: PDFExtractResult): Invoice {
    for (const factory of FACTORIES) {
      try {
        return factory(result);
      } catch (e) {
        if (e.code !== 'TediUnknownInvoiceFormat') {
          throw e;
        }
      }
    }
    throw new TediUnknownInvoiceFormatError('Unknown invoice format.');
  }
}

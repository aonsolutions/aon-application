import fileType from 'file-type';
import moment from 'moment';
import { empty, from, Observable, Observer, of } from 'rxjs';
import { filter, map, mergeMap } from 'rxjs/operators';
import { TediAutoML } from '../tedi-automl/TediAutoML';
import { TediBulkParser } from '../tedi-bulk-parser/TediBulkParser';
import { TediSpeech } from '../tedi-speech/TediSpeech';

import {
  Company,
  Invoice,
  // InvoiceHistory,
  // InvoiceHistoryAction,
  InvoiceStatus,
  InvoiceType,
  OperationType,
  TediImportInvoicesInfo,
  TediSendInvoicesInfo,
  User,
} from '../tedi-ewok/TediEwok';
import { TediGmail } from '../tedi-gmail/lib/helpers';
import { TediImgParser } from '../tedi-img-parser/TediImgParser';
import { TediOccam } from '../tedi-occam/TediOccam';
import { TediPDFBuilder } from '../tedi-pdf-builder/TediPdfBuilder';
import { TediPdfParser } from '../tedi-pdf-parser/TediPdfParser';
import { INVOICE, SidAttribute } from '../tedi-sid/TediSid';
import * as gfu from '../tedi-storage/FileUtils';
import { TediCompany } from './company';

export class TediInvoice {
  public static checkPermission(user: User, operation: OperationType) {
    // TODO Raise Error if permission is not granted.
    // By default all is granted. :)
  }

  public static getInvoice = (company: string, uuid: string, status?: InvoiceStatus): Observable<Invoice> => {
    return TediOccam.getInvoice(company, uuid, status);
  };

  public static getCountInvoices = (company: string, status?: InvoiceStatus): Observable<number> => {
    return TediOccam.getCountInvoices(company, status);
  };

  public static getInvoices = (company: string, query?: any, status?: InvoiceStatus): Observable<Invoice[]> => {
    return TediInvoice.getFilter(query).pipe(mergeMap((sidFilter: SidAttribute) => TediOccam.getInvoicesArray(company, sidFilter, query, status)));
  };

  public static updateFile = (data: TediImportInvoicesInfo) => {
    return gfu.upload$(data);
  };

  public static importFiles(data: TediImportInvoicesInfo, user?: User): Observable<Invoice> {
    if (typeof data.content === 'string') {
      const content: string = data.content;
      data.content = Buffer.from(content, 'base64');
    }
    if (data.uuid && data.update) {
      const status = data.status || InvoiceStatus.inbox;
      return gfu.upload$(data).pipe(
        mergeMap(() =>
          TediOccam.updateInvoice(
            data.company ? data.company : 'UNKNOWN',
            {
              uuid: data.uuid,
              status,
              file: {
                content_type: data.contentType,
              },
            },
            status,
          ),
        ),
      );
    } else {
      return TediInvoice.parseFile(data)
        .pipe(
          map(inv => {
            inv.uuid = data.uuid;
            const comp = TediInvoice.getCompany(inv, data);
            if (comp && comp.document) {
              inv.company = comp.document;
            }
            inv.file = {
              content_type: data.contentType,
            };
            inv.email = data.email;
            // const history: InvoiceHistory = {
            //   date: new Date(),
            //   action: InvoiceHistoryAction.create,
            //   user: '',
            // };
            if (user) {
              // history.user = user.email;
              inv.create_user = user.email;
            } else if (data.email && data.email.from) {
              // history.user = data.email.from[0];
              inv.create_user = data.email.from[0];
            }
            // inv.history = [history];
            inv.source = data.source;
            inv.status = inv.status || InvoiceStatus.inbox;

            return inv;
          }),
        )
        .pipe(mergeMap(inv => TediOccam.putInvoice(inv.company ? inv.company : 'UNKNOWN', inv, inv.status)))
        .pipe(
          mergeMap(inv => {
            data.uuid = inv.uuid;
            return gfu.upload$(data).pipe(map(r => inv));
          }),
        );
    }
  }

  public static importInvoice(data: Invoice, user: User): Observable<Invoice> {
    data.date = moment(data.date).toDate();
    if (data.finances) {
      data.finances.map(f => (f.due_date = moment(f.due_date).toDate()));
    }
    if (!data.uuid) {
      // const history: InvoiceHistory = {
      //   date: new Date(),
      //   action: InvoiceHistoryAction.create,
      //   user: user.email,
      // };
      // data.history = [history];
      data.create_date = new Date();
      data.create_user = user.email;
    }
    // else {
    //   const history: InvoiceHistory = {
    //     date: new Date(),
    //     action: data.oldStatus ? InvoiceHistoryAction.status : InvoiceHistoryAction.update,
    //     user: user.email,
    //   };
    //   data.history = data.history ? data.history.concat([history]) : [history];
    // }
    return data.oldStatus
      ? TediOccam.putInvoice(data.company!, data, data.status)
          .pipe(mergeMap(() => TediOccam.deleteInvoice(data.company!, data.uuid!, data.oldStatus)))
          .pipe(map(() => data))
      : TediOccam.putInvoice(data.company!, data, data.status);
  }

  public static updateInvoice(company: string, invoice: Invoice, status?: InvoiceStatus) {
    return TediOccam.updateInvoice(company, invoice, status);
  }

  public static deleteInvoice(company: string, uuid: string, status?: InvoiceStatus) {
    return TediOccam.deleteInvoice(company, uuid, status).pipe(mergeMap(() => gfu.deleteFile(`invoices/${uuid}`)));
  }

  public static deleteInvoices(company: string, uuids: string[], status?: InvoiceStatus) {
    return TediOccam.deleteInvoices(company, uuids, status);
  }

  public static getFilter(data: any): Observable<SidAttribute> {
    let f; // = INVOICE.COMPANY.eq(data.company.toUpperCase());
    if (data.type) {
      f = INVOICE.TYPE.eq(data.type);
    }
    if (data.verified) {
      const condition = INVOICE.VERIFIED.eq(data.verified === 'true');
      f = f ? f.and(condition) : condition;
    }
    if (data.category) {
      const condition = INVOICE.CATEGORY.eq(data.category);
      f = f ? f.and(condition) : condition;
    }
    // if (data.fromDate) {
    //   const condition = INVOICE.DATE.gt(moment(data.fromDate).toDate());
    //   f = f ? f.and(condition) : f;
    // }
    if (data.document) {
      const condition = INVOICE.RDOCUMENT.eq(data.document);
      f = f ? f.and(condition) : condition;
    }
    // if (data.reference) {
    //   const condition = INVOICE.REFERENCE.like(data.reference);
    //   f = f ? f.and(condition) : f;
    // }
    return of(f);
  }

  public static sendInvoice(user: User, data: TediSendInvoicesInfo) {
    return Observable.create(observer => {
      from(data.invoices)
        .pipe(filter((invoice: Invoice) => !!invoice.uuid))
        .pipe(
          mergeMap((invoice: Invoice) =>
            TediInvoice.getUrl(invoice).pipe(
              mergeMap((url: string) =>
                TediCompany.getCompany(invoice.company || '').pipe(
                  mergeMap((cp: Company) => TediGmail.buildInvoiceMessage(data.invoices, cp, user, url, data.to, 'Factura compartida')),
                ),
              ),
            ),
          ),
        )
        .subscribe((msg: string) => {
          TediGmail.sendMail('invoice@translogia.net', msg).subscribe(
            r => {
              observer.next({ ok: 'ok' });
              observer.complete();
            },
            e => observer.error(e),
          );
        });
    });
  }

  // public static sendInvoice(user: User, data: TediSendInvoicesInfo) {
  //   return Observable.create(observer => {
  //     let htmlData = '<p> Estimado cliente: </p>';
  //     let cont = 1;
  //     from(data.invoices)
  //       .pipe(filter((invoice: Invoice) => !!invoice.uuid))
  //       .pipe(
  //         mergeMap((invoice: Invoice) => TediInvoice.getUrl(invoice).pipe(mergeMap((url: string) => TediGmail.buildInvoiceMessage(invoice, url)))),
  //       )
  //       .subscribe(msg => {
  //         htmlData = htmlData + msg;
  //         if (data.invoices.length === cont) {
  //           const message = TediGmail.buildRawMessage('invoice@translogia.net', data.to, 'Factura compartida', htmlData);
  //           TediGmail.sendMail('invoice@translogia.net', message).subscribe(
  //             r => {
  //               observer.next({ ok: 'ok' });
  //               observer.complete();
  //             },
  //             e => observer.error(e),
  //           );
  //         } else {
  //           htmlData = htmlData + '<hr>';
  //         }
  //         cont++;
  //       });
  //   });
  // }

  private static getCompany(invoice: Invoice, data: TediImportInvoicesInfo) {
    let document = invoice.receiver && invoice.receiver.document;
    document = document ? document.replace(/^[0]+/g, '') : document;
    if (data.companies) {
      return data.companies.filter(company => company.document === document).reduce((def, company) => company, { document: '' });
    } else if (data.company) {
      return { document: data.company };
    }
    return undefined;
  }

  private static parseFile(data: TediImportInvoicesInfo): Observable<Invoice> {
    const inv = data.invoice || {
      company: data.company,
      status: InvoiceStatus.inbox,
      type: data.type || InvoiceType.RECIBIDA,
    };
    inv.verified = inv.verified || false;
    return TediInvoice.parse(data, inv);
  }

  private static getUrl(invoice: Invoice): Observable<string> {
    return Observable.create((observer: Observer<string>) => {
      gfu.getDownloadURL$('invoices/' + invoice.uuid).subscribe(
        url => {
          observer.next(url);
          observer.complete();
        },
        error => {
          TediOccam.getInvoice(invoice.company!, invoice.uuid!, invoice.status)
            .pipe(
              mergeMap(inv =>
                inv !== undefined && inv.company !== undefined
                  ? TediOccam.getCompany(inv.company)
                      .pipe(mergeMap((cp: Company) => TediCompany.getPrinterConfiguration(cp)))
                      .pipe(mergeMap(printerConfiguration => TediPDFBuilder.build$(inv, printerConfiguration)))
                  : empty,
              ),
            )
            .pipe(
              mergeMap(pdf => {
                const data = {
                  name: 'build_invoices/' + invoice.uuid,
                  content: Buffer.from(pdf, 'base64'),
                  contentType: 'application/pdf',
                };
                return gfu.uploadFile$(data);
              }),
            )
            .pipe(mergeMap(x => gfu.getDownloadURL$('build_invoices/' + invoice.uuid)))
            .subscribe(url => {
              observer.next(url);
              observer.complete();
            });
        },
      );
    });
  }

  private static parse(data: TediImportInvoicesInfo, invoice: Invoice): Observable<Invoice> {
    let content: Buffer;
    if (typeof data.content === 'string') {
      content = Buffer.from(data.content);
    } else {
      content = data.content;
    }
    const chunk = content.slice(0, 266);
    const ft: fileType.FileTypeResult | null = fileType(chunk);
    const mime = (ft && ft.mime) || data.contentType || '';

    if (mime.match(/^application\/pdf$/i)) {
      return Observable.create((observer: Observer<Invoice>) => {
        TediPdfParser.extract(content).subscribe(
          extract => {
            observer.next(extract);
            observer.complete();
          },
          err => {
            TediPdfParser.predict(data).subscribe(
              predict => {
                TediAutoML.insight(predict);
                observer.next(predict);
                observer.complete();
              },
              () => {
                observer.next(invoice);
                observer.complete();
              },
            );
          },
        );
      });
    } else if (mime.match(/^image/i)) {
      return Observable.create((observer: Observer<Invoice>) => {
        TediImgParser.predict(data).subscribe(
          predict => {
            TediAutoML.insight(predict);
            observer.next(predict);
            observer.complete();
          },
          () => {
            observer.next(invoice);
            observer.complete();
          },
        );
      });
      // return Observable.create(observer => {
      //   observer.next(invoice);
      //   observer.complete();
      // });
    } else if (mime.match(/spreadsheet|x-msi/)) {
      return TediBulkParser.extractXLSX(data.content);
    } else if (mime.match(/^audio/i)) {
      return TediSpeech.tediSpeech(data.content);
    } else {
      let cont: string;
      if (typeof data.content === 'string') {
        cont = data.content;
      } else {
        cont = data.content.toString('utf8');
      }
      return TediBulkParser.extractCSV(cont);
    }
  }
}

'use strict';
import { Company, Invoice, TediImportInvoicesInfo } from './tedi-ewok/TediEwok';
import { TediImgParser } from './tedi-img-parser/TediImgParser';
import { TediPdfParser } from './tedi-pdf-parser/TediPdfParser';

module.exports.parse = (buffer: ArrayBuffer, contentType: string): Promise<Invoice> => {
  const companies: Company[] = [];
  const info: TediImportInvoicesInfo = {
    content: new Buffer(buffer),
    contentType,
    companies,
  };
  if (contentType.match(/^image/)) {
    // tslint:disable-next-line: no-console
    console.log('TediImgParser.parse');
    return TediImgParser.parse(info);
  }
  return TediPdfParser.parse(info).catch(reason => TediImgParser.parse(info));
};

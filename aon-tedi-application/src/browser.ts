'use strict';
import { Company, Invoice, TediImportInvoicesInfo } from './tedi-ewok/TediEwok';
import { TediPdfParser } from './tedi-pdf-parser/TediPdfParser';

module.exports.parse = (buffer: ArrayBuffer, contentType: string): Promise<Invoice> => {
  const companies: Company[] = [];
  const info: TediImportInvoicesInfo = {
    content: new Buffer(buffer),
    contentType,
    companies,
  };
  return TediPdfParser.parse(info);
};

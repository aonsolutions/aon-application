import { PDFExtractPage, PDFExtractResult } from 'pdf.js-extract';
import { Invoice } from '../../tedi-ewok/TediEwok';
import { TediUnknownInvoiceFormatError } from '../TediPdfParser';
import { TediPDFParserUtils } from './Utils';

export class Tedi {
  public static extract(pdf: PDFExtractResult): Invoice {
    const pages: PDFExtractPage[] = pdf.pages;

    let page;
    let line;
    let match;
    let content;

    let i = 0;
    for (i = 0; i < pages.length; i++) {
      page = pages[i];
      try {
        content = page.content;
        line = TediPDFParserUtils.getLine(content);
        do {
          match = line.str.match(/^Created\s*by\s*TEDI\s*CENTER/i);
          if (match) {
            break;
          }
          line = TediPDFParserUtils.getLine(content, line.index);
        } while (line);

        if (match) {
          break;
        }
      } catch (e) {
        // tslint:disable-next-line: no-console
        console.error(' Catched!!' + e);
        throw new TediUnknownInvoiceFormatError(e);
      }
    }
    if (i === pages.length) {
      throw new Error('Not TEDI CENTER invoice');
    }

    line = TediPDFParserUtils.getLine(content, line.index);
    if (line) {
      const invoice = JSON.parse(line.str);
      delete invoice.reference;
      invoice.type = 'RECIBIDA';
      return invoice;
    }
    throw new Error('Unknown invoice format');
  }
}

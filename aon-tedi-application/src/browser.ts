'use strict';
import { Company, Invoice, TediImportInvoicesInfo } from './tedi-ewok/TediEwok';
import { TediImgParser } from './tedi-img-parser/TediImgParser';
import { TediPdfParser } from './tedi-pdf-parser/TediPdfParser';

function parse(content: Buffer, contentType: string): Promise<Invoice> {
  const companies: Company[] = [];
  const info: TediImportInvoicesInfo = {
    content,
    contentType,
    companies,
  };
  // if (contentType.match(/^image/)) {
  //   return TediImgParser.parse(info);
  // }
  return TediPdfParser.parse(info).catch(reason => TediImgParser.parse(info));
}

module.exports.parse = (dataUrl: string, callback: (err: Error | null, invoice?: Invoice) => void) => {
  // data:[<mediatype>][;base64],<data>

  const colon: number = dataUrl.indexOf(':');
  const comma: number = dataUrl.indexOf(',');

  const data = dataUrl.substring(comma + 1);
  const mediaType = dataUrl.substring(colon + 1, comma);

  const buffer = Buffer.from(data, 'base64');
  const contentType: string = mediaType;

  parse(buffer, contentType)
    .then(invoice => callback(null, invoice))
    .catch(reason => callback(reason));
};

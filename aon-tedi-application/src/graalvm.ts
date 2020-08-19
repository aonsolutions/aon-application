'use strict';
import { Invoice, TediImportInvoicesInfo } from './tedi-ewok/TediEwok';
// import { TediImgParser } from './tedi-img-parser/TediImgParser';
import { TediPdfParser } from './tedi-pdf-parser/TediPdfParser';

module.exports.parse = (content: string | Buffer, contentType: string): Promise<Invoice> => {
  const info: TediImportInvoicesInfo = {
    content,
    contentType,
  };
  // if (contentType.match(/^image/)) {
  //   return TediImgParser.parse(info);
  // }
  return TediPdfParser.parse(info);
  // .catch(reason => TediImgParser.parse(info))
};

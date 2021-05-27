'use strict';
import AWS from 'aws-sdk';
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

module.exports.apiGatewayHandler = (event: any, context: any, callback: (err: AWS.AWSError | null, data?: any | null) => void) => {
  // tslint:disable-next-line: no-console
  // console.log(JSON.stringify(event));

  const contentType: string = event.contentType;
  const buffer: Buffer = Buffer.from(event.body, 'base64');

  parse(buffer, contentType)
    .then(result => {
      const response = {
        statusCode: 200,
        headers: {
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
          'Access-Control-Allow-Methods': '*',
          'Access-Control-Allow-Headers': '*',
        },
        body: JSON.stringify(result),
      };

      callback(null, response);
    })
    .catch(error => {
      callback(error, null);
    });
};

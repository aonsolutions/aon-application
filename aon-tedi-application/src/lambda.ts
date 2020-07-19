'use strict';
import AWS from 'aws-sdk';
// import { Company, Invoice, TediImportInvoicesInfo } from './tedi-ewok/TediEwok';
// import { TediPdfParser } from './tedi-pdf-parser/TediPdfParser';
import MailParser from 'mailparser';
import { Invoice, TediImportInvoicesInfo } from './tedi-ewok/TediEwok';
import { TediPdfParser } from './tedi-pdf-parser/TediPdfParser';

const S3 = new AWS.S3();

function parse(attach: MailParser.Attachment): Promise<Invoice> {
  const info: TediImportInvoicesInfo = {
    content: attach.content,
    contentType: attach.contentType,
  };
  return TediPdfParser.parse(info);
}

module.exports.handler = (event: any, context: any, callback: (err: AWS.AWSError | null, data?: any | null) => void) => {
  // tslint:disable-next-line: no-console
  console.log('Process email');

  const sesNotification = event.Records[0].ses;
  // tslint:disable-next-line: no-console
  console.log('SES Notification:\n', JSON.stringify(sesNotification, null, 2));

  // Retrieve the email from your bucket
  S3.getObject(
    {
      Bucket: 'aon-ses-inbox',
      Key: `facturas@aon.solutions/${sesNotification.mail.messageId}`,
    },
    (err, data) => {
      if (err) {
        // tslint:disable-next-line: no-console
        console.log(err, err.stack);
        callback(err);
      } else {
        // tslint:disable-next-line: no-console
        console.log('Raw email:\n' + data.Body);

        // Custom email processing goes here
        return MailParser.simpleParser(data.Body as string | Buffer)
          .then(message => message.attachments.map(attach => parse(attach)))
          .then(invoices => Promise.all(invoices))
          .then(invoices => {
            // tslint:disable-next-line: no-console
            invoices.forEach(invoice => console.log(JSON.stringify(invoice)));
            callback(null, invoices);
          })
          .catch(exception => {
            // tslint:disable-next-line: no-console
            console.log(exception, exception.stack);
            callback(exception);
          });
      }
    },
  );
};

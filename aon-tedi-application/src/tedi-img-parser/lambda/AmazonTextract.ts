import AWS from 'aws-sdk';
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';

function detectText(buffer: Buffer): Promise<PDFExtractResult> {
  return detectImgText(buffer);
}

function detectImgText(buffer: Buffer): Promise<PDFExtractResult> {
  return new Promise((resolve, reject) => {
    const textract = new AWS.Textract();

    const request: AWS.Textract.DetectDocumentTextRequest = {
      Document: {
        /* required */
        Bytes: buffer,
      },
    };

    textract.detectDocumentText(request, (err: AWS.AWSError, data: AWS.Textract.DetectDocumentTextResponse) => {
      if (err) {
        reject(err);
      } // an error occurred
      else {
        // tslint:disable-next-line: no-console
        console.log(JSON.stringify(data));
        resolve(textResponse2PDFResult(data));
      } // successful response
    });
  });
}

function textResponse2PDFResult(response: AWS.Textract.DetectDocumentTextResponse): PDFExtractResult {
  // const pages : AWS.Textract.Block [] | undefined= response.Blocks?.filter(block => block.BlockType == "PAGE" );

  const words: AWS.Textract.Block[] = response.Blocks?.filter(block => block.BlockType === 'WORD') || [];

  const content: PDFExtractText[] = words
    ?.filter(word => word.Text)

    .map(word => {
      const pdfText: PDFExtractText = {
        x: 0,
        y: 0,
        width: 0,
        height: 0,
        dir: 'Unknown',
        str: word.Text || '',
        fontName: 'Unknown',
      };
      return pdfText;
    });

  const pdfPage: PDFExtractPage = {
    pageInfo: {
      num: 1,
      scale: 1,
      width: 0,
      height: 0,
      offsetX: 0,
      offsetY: 0,
      rotation: 0,
    },
    links: [],
    content,
  };

  const pdfResult: PDFExtractResult = {
    pages: [pdfPage],
    pdfInfo: {
      numPages: 1,
      fingerprint: 'AWS.Textract.Types.DetectDocumentTextResponse',
    },
  };

  return pdfResult;
}

module.exports.handler = (event: any, context: any, callback: (err: AWS.AWSError | null, data?: any | null) => void) => {
  // tslint:disable-next-line: no-console
  // console.log(JSON.stringify(event));

  const buffer: Buffer = Buffer.from(event.body, 'base64');

  detectText(buffer)
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
      callback(null, null);
    });
};

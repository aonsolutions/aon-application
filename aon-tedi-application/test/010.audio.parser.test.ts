// tslint:disable: no-unused-expression
import { expect } from 'chai';
import fs = require('fs');
import { TediSpeech } from '../src/tedi-speech/TediSpeech';

describe.skip('TEDI AUDIO PARSER TESTs', () => {
  it('PARSE AUDIO 1', done => {
    const fileName = "./test/resources/audio/audio3.flac";

    // Reads a local audio file and converts it to base64
    const file = fs.readFileSync(fileName);
    const audioBytes = file.toString("base64");



		TediSpeech.tediSpeech(audioBytes).subscribe(
      invoice => {
        // tslint:disable-next-line: no-console
        console.log(invoice, null, 1);
      	expect(invoice).not.to.be.null;
		    expect(invoice).not.to.be.undefined;
				done();
			},
			error => {
        done(error);
      }
		);
  });
});

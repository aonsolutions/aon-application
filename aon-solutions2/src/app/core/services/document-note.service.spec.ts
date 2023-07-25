import { TestBed } from '@angular/core/testing';

import { DocumentNoteService } from './document-note.service';

describe('DocumentNoteService', () => {
  let service: DocumentNoteService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DocumentNoteService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

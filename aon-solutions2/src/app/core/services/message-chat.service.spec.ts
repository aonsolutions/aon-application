import { TestBed } from '@angular/core/testing';

import { MessageChatService } from './message-chat.service';

describe('MessageChatService', () => {
  let service: MessageChatService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MessageChatService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

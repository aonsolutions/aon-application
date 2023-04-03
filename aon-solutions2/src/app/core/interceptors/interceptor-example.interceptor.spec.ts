import { TestBed } from '@angular/core/testing';

import { InterceptorExampleInterceptor } from './interceptor-example.interceptor';

describe('InterceptorExampleInterceptor', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [
      InterceptorExampleInterceptor
      ]
  }));

  it('should be created', () => {
    const interceptor: InterceptorExampleInterceptor = TestBed.inject(InterceptorExampleInterceptor);
    expect(interceptor).toBeTruthy();
  });
});

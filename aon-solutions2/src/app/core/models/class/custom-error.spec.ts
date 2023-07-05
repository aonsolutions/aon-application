import { CustomError } from './custom-error';

describe('CustomError', () => {
  it('should create an instance', () => {
    expect(new CustomError()).toBeTruthy();
  });
});

import { CustomError } from './custom-error';

describe('CustomError', () => {
  const data : string | string [] = '';
  it('should create an instance', () => {
    expect(new CustomError(data)).toBeTruthy();
  });
});

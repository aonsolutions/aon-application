import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'sharedPipe'
})
export class SharedPipePipe implements PipeTransform {

  transform(value: unknown, ...args: unknown[]): unknown {
    return null;
  }

}

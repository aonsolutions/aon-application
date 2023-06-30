import { MatGridTile } from '@angular/material/grid-list';
import { GridColsDirective } from './grid-cols.directive';
import { BreakpointObserver } from '@angular/cdk/layout';
// private tile: MatGridTile, private breakpointObserver: BreakpointObserver)
describe('GridColsDirective', () => {
  let tile :MatGridTile;
  let breakpointObserver :BreakpointObserver;


  it('should create an instance', () => {
    const directive = new GridColsDirective(tile,breakpointObserver);
    expect(directive).toBeTruthy();
  });


});

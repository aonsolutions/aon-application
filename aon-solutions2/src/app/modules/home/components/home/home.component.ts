import { Component, OnInit, ViewChild } from '@angular/core';
import { DropdownMenuComponent } from 'src/app/shared/components/dropdown-menu/dropdown-menu.component';
import { MenuItem } from 'src/app/shared/components/dropdown-menu/dropdown-menu.component';
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  @ViewChild('first') dropdownMenuComponent: DropdownMenuComponent = new DropdownMenuComponent;
  @ViewChild('second') dropdown: DropdownMenuComponent = new DropdownMenuComponent;

  myValue: number = 0;

  menuItem: MenuItem [] = [
    {root:true, text:'first', children: [{root:false, text:'first-a', routerlink:'/inbox'},{root:false, text:'first-b', value:1234}]},
    {root:true, text:'third', value:123},
    {root:true, text:'second', children: [{root:false, text:'second-a'},{root:false, text:'second-b'}]},
  ]

  constructor() {
  }

  ngOnInit(): void {
  }

  log($event:any){
    console.log('asd');
    console.log($event);
  }
}

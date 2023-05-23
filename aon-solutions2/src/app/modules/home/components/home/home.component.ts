import { Component, OnInit, ViewChild } from '@angular/core';
import { DropdownMenuComponent } from 'src/app/shared/components/dropdown-menu/dropdown-menu.component';
import { MenuItem } from 'src/app/core/models/interface/menu-item';
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
    {root:true, text:'first', children: [
      {root:false, text:'first-a', routerlink:'/inbox'},
      {root:false, text:'first-b', click:() => this.setValue('1234')}
    ]},
    {root:true, text:'third', click:() => this.log('123')},
    {root:true, text:'second', children: [
      {root:false, text:'second-a'},
      {root:false, text:'second-b', children: [
        {root:false, text:'second-a-x'},
        {root:false, text:'second-a-z'},
      ]}
    ]},
  ]

  constructor() {
  }

  ngOnInit(): void {
  }

  log(val:string){
    console.log(val);
  }

  setValue(val:any){
    this.myValue = val;
  }
  
}

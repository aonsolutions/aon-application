import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  accordionState: boolean = false;

  constructor() {
  }

  ngOnInit(): void {
  }

  log($event: any){
    console.log('home',$event)
  }
  
}

import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-content',
  templateUrl: './content.component.html',
  styleUrls: ['./content.component.scss']
})
export class ContentComponent implements OnInit {

  @Input() heightHeader : number;

  constructor() {
    this.heightHeader = 0;
  }

  ngOnInit(): void {
  }

}

import { Component, Input }  from '@angular/core';

@Component({
  selector:    'hello-elements',
  templateUrl: './hello-elements.html',
  styleUrls:  ['./hello-elements.scss']
})
export class HelloElementsComponent {
  @Input() displayText: string = 'default text'

  constructor(){}
}

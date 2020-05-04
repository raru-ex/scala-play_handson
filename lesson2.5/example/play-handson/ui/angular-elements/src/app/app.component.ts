import { Component, Injector }    from '@angular/core';
import { createCustomElement }    from '@angular/elements'
import { HelloElementsComponent } from 'src/app/sample';

@Component({
  selector: 'app-root',
  template: ''
})
export class AppComponent {
  constructor(injector: Injector) {
    customElements.define('hello-elements', createCustomElement(HelloElementsComponent, {injector}))
  }
}

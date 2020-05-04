import { BrowserModule }          from '@angular/platform-browser';
import { NgModule }               from '@angular/core';
import { AppComponent }           from './app.component';
import { HelloElementsComponent } from 'src/app/sample';

@NgModule({
  declarations: [
    AppComponent,
    HelloElementsComponent
  ],
  imports: [
    BrowserModule
  ],
  providers: [],
  bootstrap: [AppComponent],
  entryComponents: [HelloElementsComponent]
})
export class AppModule { }

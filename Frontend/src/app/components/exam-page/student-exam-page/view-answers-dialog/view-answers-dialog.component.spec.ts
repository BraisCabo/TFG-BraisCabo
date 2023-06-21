import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewAnswersDialogComponent } from './view-answers-dialog.component';

describe('ViewAnswersDialogComponent', () => {
  let component: ViewAnswersDialogComponent;
  let fixture: ComponentFixture<ViewAnswersDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ViewAnswersDialogComponent]
    });
    fixture = TestBed.createComponent(ViewAnswersDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

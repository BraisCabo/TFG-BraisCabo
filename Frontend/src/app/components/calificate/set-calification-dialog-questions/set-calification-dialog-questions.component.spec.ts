import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetCalificationDialogQuestionsComponent } from './set-calification-dialog-questions.component';

describe('SetCalificationDialogQuestionsComponent', () => {
  let component: SetCalificationDialogQuestionsComponent;
  let fixture: ComponentFixture<SetCalificationDialogQuestionsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SetCalificationDialogQuestionsComponent]
    });
    fixture = TestBed.createComponent(SetCalificationDialogQuestionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

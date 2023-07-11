import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditCalificationDialogQuestionsComponent } from './edit-calification-dialog-questions.component';

describe('EditCalificationDialogQuestionsComponent', () => {
  let component: EditCalificationDialogQuestionsComponent;
  let fixture: ComponentFixture<EditCalificationDialogQuestionsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EditCalificationDialogQuestionsComponent]
    });
    fixture = TestBed.createComponent(EditCalificationDialogQuestionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

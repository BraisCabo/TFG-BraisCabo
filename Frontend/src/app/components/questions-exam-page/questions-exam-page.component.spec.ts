import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuestionsExamPageComponent } from './questions-exam-page.component';

describe('QuestionsExamPageComponent', () => {
  let component: QuestionsExamPageComponent;
  let fixture: ComponentFixture<QuestionsExamPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [QuestionsExamPageComponent]
    });
    fixture = TestBed.createComponent(QuestionsExamPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

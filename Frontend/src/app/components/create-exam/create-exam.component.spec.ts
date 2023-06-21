import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateExamComponent } from './create-exam.component';

describe('CreateExamComponent', () => {
  let component: CreateExamComponent;
  let fixture: ComponentFixture<CreateExamComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateExamComponent]
    });
    fixture = TestBed.createComponent(CreateExamComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

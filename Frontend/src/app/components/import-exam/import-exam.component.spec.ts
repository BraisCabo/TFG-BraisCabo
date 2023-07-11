import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImportExamComponent } from './import-exam.component';

describe('ImportExamComponent', () => {
  let component: ImportExamComponent;
  let fixture: ComponentFixture<ImportExamComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ImportExamComponent]
    });
    fixture = TestBed.createComponent(ImportExamComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

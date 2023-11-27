package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import com.example.demo.student.exception.StudentNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository; //we know 100% that all methods are tested and work as expected
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentService = new StudentService(studentRepository); //fresh instance before each test
    }

    /*
    We don't want to test student repository, we mock it inside student service
     */
    @Test
    void canGetAllStudents() {
        //when
        studentService.getAllStudents();
        //then
        verify(studentRepository).findAll(); //verify that studentRepository is invoked
    }

    @Test
    void canAddStudent() {

        //given
        String email = "jane@gmail.com";
        Student student = new Student(
                "Jane",
                email,
                Gender.FEMALE
        );

        //when
        studentService.addStudent(student);

        //then - use Argument capture to verify the studentRepository is invoked
        ArgumentCaptor<Student> studentArgumentCaptor =
                ArgumentCaptor.forClass(Student.class);

        /*
        - verify that the studentRepository is called with save(). We want to capture the actual Student that is passed
        inside save()
        - We are capturing the value: studentRepository.save(student);
         */
        verify(studentRepository).
                save(studentArgumentCaptor.capture());

        Student capturedStudent = studentArgumentCaptor.getValue();

        /*
        studentRepository.save(student); method never called as we throw an exception
         */
        assertThat(capturedStudent).isEqualTo(student); //asserts that captured student is the one that is passed in StudentService class

    }

    @Test
    void willThrowExceptionWhenEmailIsTaken() {

        //given
        String email = "jane@gmail.com";
        Student student = new Student(
                "Jane",
                email,
                Gender.FEMALE
        );

        //setting up to true as by default it returns false
        /*
        .selectExistsEmail(student.getEmail());
        if (existsEmail) { default=false
         */
        given(studentRepository.selectExistsEmail(student.getEmail())).willReturn(true);

        //when
        //then
        assertThatThrownBy(() -> studentService.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining( "Email " + student.getEmail() + " taken");

        //Mock never saves any student
        verify(studentRepository, never()).save(any());

    }




    @Test
    void canDeleteStudent() {

        //given
        long id = 10;
        given(studentRepository.existsById(id)).willReturn(true);

        //when
        studentService.deleteStudent(id);
        //then
        verify(studentRepository).deleteById(id);
    }

    @Test
    void willThrowWhenDeleteStudentNotFound() {
        // given
        long id = 10;
        given(studentRepository.existsById(id))
                .willReturn(false);

        //when
        studentService.deleteStudent(id);
        //then
        assertThatThrownBy(() -> studentService.deleteStudent(id))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + id + " does not exists");

        verify(studentRepository, never()).deleteById(any());

    }

}
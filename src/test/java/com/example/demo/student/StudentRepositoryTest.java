package com.example.demo.student;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @AfterEach
    void teaDown () {
        studentRepository.deleteAll();
    }

    @Test
    void itShouldCheckExistsEmail () {

        //given
        String email = "jane@gmail.com";
        Student student = new Student(
                "Jane",
                email,
                Gender.FEMALE
        );
        studentRepository.save(student);
        System.out.println("Student object saved into a repo: " + student.toString());


        //when
        boolean emailExists = studentRepository.selectExistsEmail(email);

        //then
        assertThat(emailExists).isTrue();

    }
    
    @Test
    void itShouldCheckIfStudentEmailDoesNotExists () {
        //given
        String email = "jane@gmail.com";

        //when
        boolean expected = studentRepository.selectExistsEmail(email);

        //then
        assertThat(expected).isFalse();
    }

}
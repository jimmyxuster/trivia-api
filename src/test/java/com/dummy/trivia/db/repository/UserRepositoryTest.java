package com.dummy.trivia.db.repository;

import com.dummy.trivia.TriviaApplication;
import com.dummy.trivia.db.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;


@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
//@ContextConfiguration(locations = "classpath:application-test.yml")
//@ContextConfiguration
public class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByUsername() throws Exception {
        this.entityManager.persist(new User("testname", "12345"));
        User user = this.userRepository.findByUsername("testname");
        assertThat(user.getUsername()).isEqualTo("testname");
        assertThat(user.getPassword()).isEqualTo("12345");
    }

    @Test
    public void findByIsReady() throws Exception {
    }

}
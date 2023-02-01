package com.application.springboot.Controllers;

import com.application.springboot.persistence.User;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
/**
 *
 * @author "paul.perez"
 */
@SpringBootTest
public class UserControllerITest {/*
    private final static String USER_1_FN = "first1";
    private final static String USER_1_LN = "last1";
    private final static String USER_1_UN = "user1";
    private final static String USER_1_EM = "user1@email.com";
    
    private final static String USER_2_FN = "first2";
    private final static String USER_2_LN = "last2";
    private final static String USER_2_UN = "user2";
    private final static String USER_2_EM = "user2@email.com";
    private MongoTemplate mongoTemplate;
    
    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    @MockBean
    private MongoTemplate mockMongoTemplate;
    @Mock
    private UserControllerImpl mockUserController;
    @Mock
    public User mockUser1;
    @Mock
    public User mockUser2;
    @BeforeEach
    public void setup(){
        mockUserController.setMongoTemplate(mockMongoTemplate);
        EasyMockSupport.injectMocks(this);
    }
    

    @Test
    void testUserController(){
        mockUser1.setUserName(USER_1_UN);
        mockUser1.setFirstName(USER_1_FN);
        mockUser1.setLastName(USER_1_LN);
        mockUser1.setEmail(USER_1_EM);
        EasyMock.replay(mockUser1);
        mockUser2.setUserName(USER_2_UN);
        mockUser2.setFirstName(USER_2_FN);
        mockUser2.setLastName(USER_2_LN);
        mockUser2.setEmail(USER_2_EM);
        EasyMock.replay(mockUser2);
        //test add users
        expect(mockUserController.addUser(USER_2_UN, USER_2_FN, USER_2_LN, USER_2_EM)).andReturn(mockUser2);
        expect(mockUserController.addUser(USER_1_UN, USER_1_FN, USER_1_LN, USER_1_EM)).andReturn(mockUser1);
        replay(mockUserController);
        //test get users
        assertTrue(mockUserController.getUser(USER_1_UN, UserOperations.SearchType.Username).contains(mockUser1));
        assertTrue(mockUserController.getUser(USER_2_UN, UserOperations.SearchType.Username).contains(mockUser2));
        //test list users
        assertTrue(mockUserController.listUsers().size() == 2);
        assertTrue(mockUserController.listUsers().contains(USER_1_UN));
        assertTrue(mockUserController.listUsers().contains(USER_2_UN));

    }
    */
}

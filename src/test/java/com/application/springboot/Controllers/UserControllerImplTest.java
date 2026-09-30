package com.application.springboot.Controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.application.springboot.objects.Bankroll;
import com.application.springboot.objects.User;
import com.application.springboot.controllers.UserControllerImpl;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import java.util.regex.Pattern;

class UserControllerImplTest {

    @Test
    void getUserQueriesAnExactUsernameWithoutCaseSensitivity() {
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        User expectedUser = new User();
        when(mongoTemplate.exists(any(Query.class), eq(User.class), eq("users"))).thenReturn(true);
        when(mongoTemplate.find(any(Query.class), eq(User.class), eq("users")))
            .thenReturn(List.of(expectedUser));
        UserControllerImpl controller = new UserControllerImpl();
        controller.setMongoTemplate(mongoTemplate);

        User actualUser = controller.getUser("sports.user+one");

        assertSame(expectedUser, actualUser);
        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        verify(mongoTemplate).find(queryCaptor.capture(), eq(User.class), eq("users"));
        Object usernameCriteria = queryCaptor.getValue().getQueryObject().get("userName");
        Pattern usernamePattern = assertInstanceOf(Pattern.class, usernameCriteria);
        assertEquals("^\\Qsports.user+one\\E$", usernamePattern.pattern());
        assertEquals(Pattern.CASE_INSENSITIVE, usernamePattern.flags());
    }

    @Test
    void addUserRejectsUsernameThatDiffersOnlyByCase() {
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        when(mongoTemplate.exists(any(Query.class), eq(User.class), eq("users"))).thenReturn(true);
        UserControllerImpl controller = new UserControllerImpl();
        controller.setMongoTemplate(mongoTemplate);

        assertThrows(Exception.class, () -> controller.addUser(
            "ExistingUser", "First", "Last", "new@example.com", "Street", "City", "ST", "12345"));

        verify(mongoTemplate).exists(any(Query.class), eq(User.class), eq("users"));
        verify(mongoTemplate, never()).save(any());
    }

    @Test
    void addUserRejectsEmailThatDiffersOnlyByCase() {
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        when(mongoTemplate.exists(any(Query.class), eq(User.class), eq("users")))
            .thenReturn(false, true);
        UserControllerImpl controller = new UserControllerImpl();
        controller.setMongoTemplate(mongoTemplate);

        assertThrows(Exception.class, () -> controller.addUser(
            "NewUser", "First", "Last", "existing@example.com", "Street", "City", "ST", "12345"));

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        verify(mongoTemplate, times(2)).exists(queryCaptor.capture(), eq(User.class), eq("users"));
        Pattern emailPattern = assertInstanceOf(Pattern.class,
            queryCaptor.getAllValues().get(1).getQueryObject().get("email"));
        assertEquals("^\\Qexisting@example.com\\E$", emailPattern.pattern());
        assertEquals(Pattern.CASE_INSENSITIVE, emailPattern.flags());
        verify(mongoTemplate, never()).save(any());
    }

    @Test
    void deleteUserMatchesUsernameWithoutCaseSensitivity() {
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        when(mongoTemplate.exists(any(Query.class), eq(User.class), eq("users"))).thenReturn(true);
        UserControllerImpl controller = new UserControllerImpl();
        controller.setMongoTemplate(mongoTemplate);

        controller.deleteUser("mixedcase");

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        verify(mongoTemplate).findAllAndRemove(queryCaptor.capture(), eq(User.class), eq("users"));
        verify(mongoTemplate).findAllAndRemove(queryCaptor.capture(), eq(Bankroll.class), eq("bankrolls"));
        assertCaseInsensitiveUsernameQuery(queryCaptor.getAllValues().get(0));
        assertCaseInsensitiveUsernameQuery(queryCaptor.getAllValues().get(1));
    }

    @Test
    void editEndpointsMatchUsernameWithoutCaseSensitivity() {
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        User expectedUser = new User();
        when(mongoTemplate.exists(any(Query.class), eq(User.class), eq("users"))).thenReturn(true);
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), eq(User.class)))
            .thenReturn(expectedUser);
        when(mongoTemplate.find(any(Query.class), eq(User.class), eq("users")))
            .thenReturn(List.of(expectedUser));
        UserControllerImpl controller = new UserControllerImpl();
        controller.setMongoTemplate(mongoTemplate);

        controller.editUserAddress("mixedcase", "Street", "City", "ST", "12345");
        controller.editUserProperName("mixedcase", "First", "Last");
        controller.editUserEmail("mixedcase", "updated@example.com");

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        verify(mongoTemplate, times(3)).findAndModify(queryCaptor.capture(), any(Update.class), eq(User.class));
        for (Query query : queryCaptor.getAllValues()) {
            assertCaseInsensitiveUsernameQuery(query);
        }
    }

    private void assertCaseInsensitiveUsernameQuery(Query query) {
        Pattern usernamePattern = assertInstanceOf(Pattern.class, query.getQueryObject().get("userName"));
        assertEquals("^\\Qmixedcase\\E$", usernamePattern.pattern());
        assertEquals(Pattern.CASE_INSENSITIVE, usernamePattern.flags());
    }
}
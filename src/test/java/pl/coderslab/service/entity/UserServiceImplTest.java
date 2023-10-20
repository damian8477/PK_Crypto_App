package pl.coderslab.service.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.coderslab.TestFixtures;
import pl.coderslab.entity.user.User;
import pl.coderslab.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository mockUserRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testGetUserListWithUserSettings() {
        User user = TestFixtures.user();

        when(mockUserRepository.findByUsername("username")).thenReturn(user);

        User findUser = userService.getUserWithUserSettingsByUserName(user.getUsername());

        assertEquals(findUser, user);
    }

    @Test
    void testGetUserList() {
        User user = TestFixtures.user();
        User user2 = TestFixtures.user();
        user2.setUsername("user2");

        when(mockUserRepository.findAll()).thenReturn(List.of(user, user2));

        List<User> users = userService.getUserList();

        assertEquals(2, users.size());
        assertTrue(users.contains(user));
        assertTrue(users.contains(user2));
    }
}
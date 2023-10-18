package pl.coderslab;


import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserSetting;


public class TestFixtures {

    public static User user() {
        return User.builder()
                .active(true)
                .role("ROLE_USER")
                .username("username")
                .firstName("Andrzej")
                .lastName("Pajda")
                .password("{noop}password")
                .build();
    }

    public static UserSetting userSetting(User user){
        return UserSetting.builder()
                .active(true)
                .user(user)
                .telegramChatId("012345678901")
                .binanceKey("NWtuZVbCa8esr4VG6usHzS0Ms1aMz7NwLBjPcUmU0SGRb6uXKvpiK77HM1ntHoMx")
                .binanceSecret("clf59olo5MjQeQ8Qxe6RnPC4ePjxZsAeTdxZgdWedBtWt1f4Z7zohD2qEIYOgXdk")
                .build();
    }

    public static UserSetting badUserSetting(User user){
        return UserSetting.builder()
                .active(true)
                .user(user)
                .telegramChatId("0123456789")
                .binanceKey("0123456789012345678")
                .binanceSecret("01234567890123451234567890123")
                .build();
    }


}

package com.hayday.notifications;

import com.hayday.notifications.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class NotificationsServiceApplicationTests {

    @MockBean
    private NotificationRepository notificationRepository;

    @Test
    void contextLoads() {
    }
}

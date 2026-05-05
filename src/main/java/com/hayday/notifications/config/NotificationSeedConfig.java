package com.hayday.notifications.config;

import com.hayday.notifications.model.NotificationDocument;
import com.hayday.notifications.repository.NotificationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Configuration
@Profile("!prod & !test")
public class NotificationSeedConfig {

    @Bean
    public CommandLineRunner seedNotifications(NotificationRepository repository) {
        return args -> {
            // Only seed if collection is empty
            if (repository.count() > 0) {
                return;
            }

            NotificationDocument attention = new NotificationDocument(
                    "user-1",
                    "attention",
                    "warning",
                    "Cow needs review",
                    "Cow #7 needs a health check.",
                    "/farm/animals/7"
            );
            attention.setId(UUID.randomUUID().toString());
            attention.setAnimal_id("animal-7");
            attention.setMetadata(Map.of("source", "seed"));
            attention.setCreated_at(LocalDateTime.now().minusMinutes(12));

            NotificationDocument activity = new NotificationDocument(
                    "user-1",
                    "activity",
                    "success",
                    "Milk collected",
                    "You collected milk from the dairy.",
                    "/farm/activity"
            );
            activity.setId(UUID.randomUUID().toString());
            activity.setStatus("read");
            activity.setMetadata(Map.of("source", "seed"));
            activity.setCreated_at(LocalDateTime.now().minusHours(1));
            activity.setRead_at(LocalDateTime.now().minusMinutes(50));

            repository.save(attention);
            repository.save(activity);

            System.out.println("Seeded 2 sample notifications");
        };
    }
}

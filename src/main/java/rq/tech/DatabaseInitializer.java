package rq.tech;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rq.tech.services.AuthService;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final AuthService authService;

    @Override
    public void run(String... args) throws Exception {
        authService.ensureAdminExists();
        System.out.println("✓ Database initialization check complete.");
    }
}

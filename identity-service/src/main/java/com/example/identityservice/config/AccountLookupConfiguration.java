package com.example.identityservice.config;
import com.locally.security.AccountLookup;
import com.example.identityservice.users.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class AccountLookupConfiguration {
    @Bean
    @org.springframework.context.annotation.Primary
    AccountLookup localAccounts(UserService users) {
        return id -> {
            try {
                var user = users.getUserById(id);
                return new AccountLookup.Account(user.getId(), user.getStatus().name(), user.getRoles().stream().map(Enum::name).toList());
            } catch (org.springframework.web.server.ResponseStatusException e) {
                if (e.getStatusCode().value() == 404) return null;
                throw e;
            }
        };
    }
}

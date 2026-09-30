package com.locally.security;

import java.util.List;

public interface AccountLookup {
    Account find(String userId);
    record Account(String userId, String status, List<String> roles) {}
}

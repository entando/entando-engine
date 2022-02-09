package org.entando.entando.aps.system.services.userpreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserPreferencesServiceTest {

    private static final String USER_1 = "user1";
    private static final String USER_2 = "user2";

    @Mock
    private IUserPreferencesManager userPreferencesManager;

    @InjectMocks
    private UserPreferencesService userPreferencesService;

    @Test
    void testGetNewPreferencesConcurrent() throws Exception {

        // Map used to mimic database behavior; it will store preferences for user1 and user2
        Map<String, UserPreferences> preferencesMap = new ConcurrentHashMap<>();

        // List used to verify that the calls for different users are interleaved
        // (two different users can still access the method concurrently)
        List<String> sequence = Collections.synchronizedList(new ArrayList<>());

        Mockito.doAnswer(invocationOnMock -> {
            UserPreferences preferences = invocationOnMock.getArgument(0);
            preferencesMap.put(preferences.getUsername(), preferences);
            return null;
        }).when(userPreferencesManager).addUserPreferences(ArgumentMatchers.any());

        Mockito.doAnswer(invocationOnMock -> {
            String username = invocationOnMock.getArgument(0);
            sequence.add(username);
            return preferencesMap.get(username);
        }).when(userPreferencesManager).getUserPreferences(ArgumentMatchers.any());

        CompletableFuture.allOf(
                getUserPreferencesCall(USER_1),
                getUserPreferencesCall(USER_1),
                getUserPreferencesCall(USER_2),
                getUserPreferencesCall(USER_2)
        ).join();

        Mockito.verify(userPreferencesManager, Mockito.times(3)).getUserPreferences(USER_1);
        Mockito.verify(userPreferencesManager, Mockito.times(1))
                .addUserPreferences(ArgumentMatchers.argThat(u -> u.getUsername().equals(USER_1)));

        Mockito.verify(userPreferencesManager, Mockito.times(3)).getUserPreferences(USER_2);
        Mockito.verify(userPreferencesManager, Mockito.times(1))
                .addUserPreferences(ArgumentMatchers.argThat(u -> u.getUsername().equals(USER_2)));

        // check that the first 3 invocations weren't made by the same user
        String firstUsername = sequence.get(0);
        Assertions.assertFalse(sequence.get(1).equals(firstUsername) && sequence.get(2).equals(firstUsername));
    }

    private CompletableFuture<Void> getUserPreferencesCall(String username) {
        return CompletableFuture.runAsync(() -> userPreferencesService.getUserPreferences(username));
    }
}

package com.example.ums;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class UserCache {
    private static final UserCache INSTANCE = new UserCache();
    private static final Duration TTL = Duration.ofMinutes(5);

    private final DatabaseManager dm = new DatabaseManager();
    private List<User> usersLite = new ArrayList<>();
    private final Map<String, User> detailedUsers = new HashMap<>();
    private Instant lastFetchedLite = Instant.MIN;

    private UserCache() {
    }

    public static UserCache getInstance() {
        return INSTANCE;
    }

    /**
     * Returns lightweight user rows (from users table only) with caching.
     */
    public synchronized List<User> getUsersLiteCached() {
        if (!usersLite.isEmpty() && lastFetchedLite != null &&
                Duration.between(lastFetchedLite, Instant.now()).compareTo(TTL) < 0) {
            return new ArrayList<>(usersLite);
        }
        usersLite = loadAllUsersLite();
        lastFetchedLite = Instant.now();
        return new ArrayList<>(usersLite);
    }

    public synchronized void invalidate() {
        usersLite = new ArrayList<>();
        detailedUsers.clear();
        lastFetchedLite = Instant.MIN;
    }

    public synchronized void addOrUpdateUser(User user) {
        if (user == null || user.getId() == null) {
            return;
        }
        boolean updated = false;
        for (int i = 0; i < usersLite.size(); i++) {
            if (user.getId().equals(usersLite.get(i).getId())) {
                usersLite.set(i, user);
                updated = true;
                break;
            }
        }
        if (!updated) {
            usersLite.add(user);
        }
        detailedUsers.put(user.getId(), user);
        lastFetchedLite = Instant.now();
    }

    public synchronized void removeUserById(String userId) {
        if (userId == null) {
            return;
        }
        usersLite.removeIf(u -> userId.equals(u.getId()));
        detailedUsers.remove(userId);
        lastFetchedLite = Instant.now();
    }

    /**
     * Get a detailed user; if not cached, fetch by type and cache it.
     */
    public User getUserDetailed(String userId, String typeHint) {
        synchronized (this) {
            if (detailedUsers.containsKey(userId)) {
                return detailedUsers.get(userId);
            }
        }
        User detailed = fetchDetailedUser(userId, typeHint);
        if (detailed != null) {
            synchronized (this) {
                detailedUsers.put(userId, detailed);
            }
        }
        return detailed;
    }

    /**
     * Warm all detailed users in a background thread (non-blocking for UI).
     */
    public void warmUserDetailsAsync() {
        List<User> snapshot = getUsersLiteCached();
        new Thread(() -> {
            for (User u : snapshot) {
                if (u != null) {
                    getUserDetailed(u.getId(), u.getType());
                }
            }
        }, "user-detail-warmup").start();
    }

    private List<User> loadAllUsersLite() {
        return dm.getAllUsersLite();
    }

    private User fetchDetailedUser(String userId, String typeHint) {
        try {
            if ("Student".equalsIgnoreCase(typeHint)) {
                return dm.getStudent(userId);
            } else if ("Instructor".equalsIgnoreCase(typeHint) || "Department Head".equalsIgnoreCase(typeHint)) {
                return dm.getInstructor(userId);
            } else if ("Admin".equalsIgnoreCase(typeHint)) {
                return dm.getAdmin(userId);
            } else if ("HR".equalsIgnoreCase(typeHint)) {
                return dm.getHR(userId);
            } else if ("Parent".equalsIgnoreCase(typeHint)) {
                return dm.getParent(userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


package carPartsStore.auth;

public enum UserRole {
    ADMIN, USER;

    UserRole() {
    }

    String scope() {
        return "SCOPE_" + name();
    }
}
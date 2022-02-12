package net.mikc.serde.entities;

public enum ProjectState {
    ACCEPTED(0), IN_PROGRESS(1), DELETED(2), DELETING(3);

    private final int value;

    ProjectState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ProjectState fromValue(int value) {
        for(ProjectState v: values()) {
            if(v.getValue() == value) {
                return v;
            }
        }
        return null;
    }
}

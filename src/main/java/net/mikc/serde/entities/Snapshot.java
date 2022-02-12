package net.mikc.serde.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder(builderClassName = "Builder", toBuilder = true)
@JsonDeserialize(builder = Snapshot.Builder.class)
@Getter
@Setter
@EqualsAndHashCode
public class Snapshot {
    List<ProjectMetadataEntity> projects;

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {}
}

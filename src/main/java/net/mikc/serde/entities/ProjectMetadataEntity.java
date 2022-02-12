package net.mikc.serde.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder(builderClassName = "Builder", toBuilder = true)
@JsonDeserialize(builder = ProjectMetadataEntity.Builder.class)
@Getter
@Setter
@EqualsAndHashCode
public class ProjectMetadataEntity {
    private String version;
    private String eventId;
    private String opcRequestId;
    private Long sequenceId;
    private String projectId;
    private String compartmentId;
    private ProjectState state;

    private Integer maxConcurrentQueries;
    private Integer maxQueryTime;
    private Integer maxQueryOcpu;
    private Integer maxQueryRead;

    private String databaseId;
    private String secretId;

    private List<String> logs;

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {}

    @Override
    public String toString() {
        return "ProjectMetadataEntity{" +
                "version='" + version + '\'' +
                ", eventId='" + eventId + '\'' +
                ", opcRequestId='" + opcRequestId + '\'' +
                ", sequenceId=" + sequenceId +
                ", projectId='" + projectId + '\'' +
                ", compartmentId='" + compartmentId + '\'' +
                ", state=" + state +
                ", maxConcurrentQueries=" + maxConcurrentQueries +
                ", maxQueryTime=" + maxQueryTime +
                ", maxQueryOcpu=" + maxQueryOcpu +
                ", maxQueryRead=" + maxQueryRead +
                ", databaseId='" + databaseId + '\'' +
                ", secretId='" + secretId + '\'' +
                ", logs=" + logs +
                '}';
    }
}


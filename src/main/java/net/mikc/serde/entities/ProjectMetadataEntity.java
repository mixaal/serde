package net.mikc.serde.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.mikc.serializers.SerializeBytes;

import java.util.List;

@Builder(builderClassName = "Builder", toBuilder = true)
@JsonDeserialize(builder = ProjectMetadataEntity.Builder.class)
@Getter
@Setter
@EqualsAndHashCode
public class ProjectMetadataEntity {
    @SerializeBytes(id=1)
    private String version;
    @SerializeBytes(id=2)
    private String eventId;
    @SerializeBytes(id=3)
    private String opcRequestId;
    @SerializeBytes(id=4)
    private Long sequenceId;
    @SerializeBytes(id=5)
    private String projectId;
    @SerializeBytes(id=6)
    private String compartmentId;
    @SerializeBytes(id=7, isEnum=true)
    private ProjectState state;

    @SerializeBytes(id=8)
    private Integer maxConcurrentQueries;
    @SerializeBytes(id=9)
    private Integer maxQueryTime;
    @SerializeBytes(id=10)
    private Integer maxQueryOcpu;
    @SerializeBytes(id=11)
    private Integer maxQueryRead;

    @SerializeBytes(id=12)
    private String databaseId;
    @SerializeBytes(id=13)
    private String secretId;

    @SerializeBytes(id=14)
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


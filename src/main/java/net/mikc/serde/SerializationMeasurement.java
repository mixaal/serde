package net.mikc.serde;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.mikc.serde.entities.ProjectMetadataEntity;
import net.mikc.serde.entities.ProjectMetadataEntitySerializer;
import net.mikc.serde.entities.ProjectState;
import net.mikc.serde.entities.Snapshot;
import net.mikc.serde.protos.*;

import java.nio.ByteBuffer;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class SerializationMeasurement {
    private final int capacity;
    private final RandomString stringGenerator = new RandomString();
    private final Random randomGenerator = new Random();

    private static final List<ProjectState> VALUES =
            Collections.unmodifiableList(Arrays.asList(ProjectState.values()));
    private static final int SIZE = VALUES.size();

    private static final List<Project.State> SVALUES =
            Collections.unmodifiableList(Arrays.asList(Project.State.values()));
    private static final int SSIZE = SVALUES.size();


    private ProjectMetadataEntity[] serArray;
    private Project[] serProtoArray;
    private String[] serArraySerializedForm;
    private byte[][] serByteArraySerializedForm;
    private ProjectMetadataEntity[] deserArray;
    private Project[] deserProtoArray;

    private final ObjectMapper jackson = new ObjectMapper();
    private final Gson gson = new Gson();

    private static class SnapContainer {
        Snapshot snapshot;
        byte[] binary;
        SnapshotProtoBufs snapProtobuf;
    }

    public SerializationMeasurement(int capacity) {
        this.capacity = capacity;
        serArray = new ProjectMetadataEntity[capacity];
        serProtoArray = new Project[capacity];
        deserArray = new ProjectMetadataEntity[capacity];
        deserProtoArray = new Project[capacity];
        serArraySerializedForm = new String[capacity];
        serByteArraySerializedForm = new byte[capacity][];
        for (int i = 0; i < capacity; i++) {
            serArray[i] = getRandomEntity();
            serProtoArray[i] = getRandomProtobufEntity();
        }
    }

    private byte[] byteBufferSerialize(ProjectMetadataEntity entity) {
//        ObjectsToBytes strings = ByteBufferSerializer.convertToBytes(
//                entity.getMaxConcurrentQueries(), entity.getMaxQueryOcpu(), entity.getMaxQueryRead(), entity.getMaxQueryTime(), entity.getSequenceId(), entity.getState().getValue(),
//                entity.getCompartmentId(), entity.getDatabaseId(), entity.getEventId(), entity.getOpcRequestId(), entity.getProjectId(), entity.getSecretId(), entity.getVersion(),
//                entity.getLogs()
//        );
//        ByteBuffer bb = ByteBuffer.allocate(strings.getTotalLen());
//        strings.appendToByteBuffer(bb);
//        return bb.array();

        return ProjectMetadataEntitySerializer.serialize(entity);
    }

    private byte[] byteBufferSerialize2(ByteBufferSerializer bbs, ProjectMetadataEntity entity) {
        ObjectsToBytes strings = bbs.convertToBytes2(
                entity.getMaxConcurrentQueries(), entity.getMaxQueryOcpu(), entity.getMaxQueryRead(), entity.getMaxQueryTime(), entity.getSequenceId(), entity.getState().getValue(),
                entity.getCompartmentId(), entity.getDatabaseId(), entity.getEventId(), entity.getOpcRequestId(), entity.getProjectId(), entity.getSecretId(), entity.getVersion(),
                entity.getLogs()
        );
        ByteBuffer bb = ByteBuffer.allocate(strings.getTotalLen());
        strings.appendToByteBuffer(bb);
        return bb.array();
    }

    public void byteBufferDeserialize(List<ProjectMetadataEntity> projects, byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        while (bb.remaining() > 0) {
            ProjectMetadataEntity.Builder entityBuilder = ProjectMetadataEntity.builder()
                    .maxConcurrentQueries(bb.getInt())
                    .maxQueryOcpu(bb.getInt())
                    .maxQueryRead(bb.getInt())
                    .maxQueryTime(bb.getInt())
                    .sequenceId(bb.getLong())
                    .state(ProjectState.fromValue(bb.getInt()))
                    .compartmentId(ByteBufferSerializer.readString(bb))
                    .databaseId(ByteBufferSerializer.readString(bb))
                    .eventId(ByteBufferSerializer.readString(bb))
                    .opcRequestId(ByteBufferSerializer.readString(bb))
                    .projectId(ByteBufferSerializer.readString(bb))
                    .secretId(ByteBufferSerializer.readString(bb))
                    .version(ByteBufferSerializer.readString(bb));

            int sz = bb.getInt();
            List<String> logs = new ArrayList<>();
            for (int i = 0; i < sz; i++) {
                logs.add(ByteBufferSerializer.readString(bb));
            }
            projects.add(entityBuilder.logs(logs).build());
        }
    }

    public ProjectMetadataEntity byteBufferDeserialize(byte[] bytes) {
//        ByteBuffer bb = ByteBuffer.wrap(bytes);
//        ProjectMetadataEntity.Builder entityBuilder = ProjectMetadataEntity.builder()
//                .maxConcurrentQueries(bb.getInt())
//                .maxQueryOcpu(bb.getInt())
//                .maxQueryRead(bb.getInt())
//                .maxQueryTime(bb.getInt())
//                .sequenceId(bb.getLong())
//                .state(ProjectState.fromValue(bb.getInt()))
//                .compartmentId(ByteBufferSerializer.readString(bb))
//                .databaseId(ByteBufferSerializer.readString(bb))
//                .eventId(ByteBufferSerializer.readString(bb))
//                .opcRequestId(ByteBufferSerializer.readString(bb))
//                .projectId(ByteBufferSerializer.readString(bb))
//                .secretId(ByteBufferSerializer.readString(bb))
//                .version(ByteBufferSerializer.readString(bb));
//
//        int sz = bb.getInt();
//        List<String> logs = new ArrayList<>();
//        for (int i = 0; i < sz; i++) {
//            logs.add(ByteBufferSerializer.readString(bb));
//        }
//        return entityBuilder.logs(logs).build();
        return ProjectMetadataEntitySerializer.deserialize(bytes);
    }


    public void testProtoBuf() {
        TestHelper.measure(capacity, "Protobuf - serialization", () -> {
            for (int i = 0; i < serArray.length; i++) {
                serByteArraySerializedForm[i] = serProtoArray[i].toByteArray();
            }
        });

        TestHelper.measure(capacity, "Protobuf - deserialization", () -> {
            for (int i = 0; i < serArray.length; i++) {
                try {
                    deserProtoArray[i] = Project.parseFrom(serByteArraySerializedForm[i]);
                } catch (Throwable t) {

                }
            }
        });

        boolean testPassed = true;
        for (int i = 0; i < serArray.length; i++) {
            if (!TestHelper.assertEquals(deserProtoArray[i], serProtoArray[i])) {
                testPassed = false;
                break;
            }
        }
        TestHelper.assertTrue("testByteBuffer", testPassed);
    }

    public void testByteBuffer() {
        TestHelper.measure(capacity, "ByteBuffer - serialization", () -> {
            for (int i = 0; i < serArray.length; i++) {
                serByteArraySerializedForm[i] = byteBufferSerialize(serArray[i]);
            }
        });

        TestHelper.measure(capacity, "ByteBuffer - deserialization", () -> {
            for (int i = 0; i < serArray.length; i++) {
                deserArray[i] = byteBufferDeserialize(serByteArraySerializedForm[i]);
            }
        });

        boolean testPassed = true;
        for (int i = 0; i < serArray.length; i++) {
            if (!TestHelper.assertEquals(deserArray[i], serArray[i])) {
                testPassed = false;
                break;
            }
        }
        TestHelper.assertTrue("testByteBuffer", testPassed);

    }


    public void testGson() {
        TestHelper.measure(capacity, "Gson - serialization", () -> {
            for (int i = 0; i < serArray.length; i++) {
                serArraySerializedForm[i] = gson.toJson(serArray[i]);
            }
        });

        TestHelper.measure(capacity, "Gson - deserialization", () -> {
            for (int i = 0; i < serArray.length; i++) {
                deserArray[i] = gson.fromJson(serArraySerializedForm[i], ProjectMetadataEntity.class);
            }
        });

        boolean testPassed = true;
        for (int i = 0; i < serArray.length; i++) {
            if (!TestHelper.assertEquals(deserArray[i], serArray[i])) {
                testPassed = false;
                break;
            }
        }
        TestHelper.assertTrue("testGson", testPassed);

    }

    public void testJackson() {
        TestHelper.measure(capacity, "Jackson - serialization", () -> {
            for (int i = 0; i < serArray.length; i++) {
                try {
                    serArraySerializedForm[i] = jackson.writeValueAsString(serArray[i]);
                } catch (Throwable t) {

                }
            }
        });

        TestHelper.measure(capacity, "Jackson - deserialization", () -> {
            for (int i = 0; i < serArray.length; i++) {
                try {
                    deserArray[i] = jackson.readValue(serArraySerializedForm[i], ProjectMetadataEntity.class);
                } catch (Throwable t) {

                }
            }
        });

        boolean testPassed = true;
        for (int i = 0; i < serArray.length; i++) {
            if (!TestHelper.assertEquals(deserArray[i], serArray[i])) {
                testPassed = false;
                break;
            }
        }
        TestHelper.assertTrue("testGson", testPassed);
    }

    public void testBigJackson() {
        Snapshot snapshot = Snapshot.builder()
                .projects(Arrays.asList(serArray))
                .build();
        TestHelper.measure(capacity, "Jackson - big serialization", () -> {
            try {
                serArraySerializedForm[0] = jackson.writeValueAsString(snapshot);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });

        final SnapContainer container = new SnapContainer();
        TestHelper.measure(capacity, "Jackson - big deserialization", () -> {
            try {
                container.snapshot = jackson.readValue(serArraySerializedForm[0], Snapshot.class);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
        TestHelper.assertTrue("testBigJackson", TestHelper.assertEquals(container.snapshot, snapshot));
    }

    public void testBigGson() {
        Snapshot snapshot = Snapshot.builder()
                .projects(Arrays.asList(serArray))
                .build();
        TestHelper.measure(capacity, "Gson - big serialization", () -> {
            serArraySerializedForm[0] = gson.toJson(snapshot);
        });

        final SnapContainer container = new SnapContainer();
        TestHelper.measure(capacity, "Gson - big deserialization", () -> {
            container.snapshot = gson.fromJson(serArraySerializedForm[0], Snapshot.class);
        });
        TestHelper.assertTrue("testBigGson", TestHelper.assertEquals(container.snapshot, snapshot));
    }

    public void testBigByteBuffer() {
        final SnapContainer container = new SnapContainer();
        TestHelper.measure(capacity, "Big ByteBuffer - serialization", () -> {
            ByteBufferSerializer bbs = new ByteBufferSerializer(true);
            ByteArrayDataOutput bos = ByteStreams.newDataOutput();
            for (int i = 0; i < serArray.length; i++) {
                byte[] partial = byteBufferSerialize2(bbs, serArray[i]);
                bos.write(partial);
            }
            container.binary = bos.toByteArray();
        });

        List<ProjectMetadataEntity> projects = new ArrayList<>();
        TestHelper.measure(capacity, "Big ByteBuffer - deserialization", () -> {
            byteBufferDeserialize(projects, container.binary);
        });

        TestHelper.assertTrue("testBigByteBuffer", TestHelper.assertEquals(projects, Arrays.asList(serArray)));
    }

    public void testBigProtobufs() {
        SnapshotProtoBufs snapshot = SnapshotProtoBufs.newBuilder()
                .addAllProjects(Arrays.asList(serProtoArray))
                .build();
        SnapContainer container = new SnapContainer();
        TestHelper.measure(capacity, "Big Protobuf - serialization", () -> {
            container.binary = snapshot.toByteArray();
        });
        TestHelper.measure(capacity, "Big Protobuf - deserialization", () -> {
            try {
                container.snapProtobuf = SnapshotProtoBufs.parseFrom(container.binary);
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        });

        TestHelper.assertTrue("testBigProtobuf", TestHelper.assertEquals(snapshot, container.snapProtobuf));
    }


    private Project getRandomProtobufEntity() {
        return Project.newBuilder()
                .setCompartmentId(stringGenerator.nextString())
                .setDatabaseId(stringGenerator.nextString())
                .setEventId(UUID.randomUUID().toString())
                .setMaxConcurrentQueries(randomGenerator.nextInt(100))
                .setMaxQueryOcpu(randomGenerator.nextInt(128))
                .setMaxQueryRead(randomGenerator.nextInt(12 * 3600))
                .setMaxQueryTime(randomGenerator.nextInt(12 * 3600))
                .setOpcRequestId(UUID.randomUUID().toString())
                .setProjectId(stringGenerator.nextString())
                .setSecretId(stringGenerator.nextString())
                .setVersion(stringGenerator.nextString())
                .setSequenceId(randomGenerator.nextLong())
                .setState(SVALUES.get(randomGenerator.nextInt(SSIZE)))
                .build();
    }

    private ProjectMetadataEntity getRandomEntity() {
        ProjectMetadataEntity entity = ProjectMetadataEntity.builder()
                .compartmentId(stringGenerator.nextString())
                .databaseId(stringGenerator.nextString())
                .eventId(UUID.randomUUID().toString())
                .maxConcurrentQueries(randomGenerator.nextInt(100))
                .maxQueryOcpu(randomGenerator.nextInt(128))
                .maxQueryRead(randomGenerator.nextInt(12 * 3600))
                .maxQueryTime(randomGenerator.nextInt(12 * 3600))
                .opcRequestId(UUID.randomUUID().toString())
                .projectId(stringGenerator.nextString())
                .secretId(stringGenerator.nextString())
                .version(stringGenerator.nextString())
                .sequenceId(randomGenerator.nextLong())
                .state(VALUES.get(randomGenerator.nextInt(SIZE)))
                .logs(Arrays.asList("1", "2", "3"))
                .build();
        return entity;
    }
}

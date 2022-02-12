package net.mikc.serde;

import java.util.Arrays;
import java.util.List;

public class Main {

    private static void runTestCycle(int capacity) {
        System.out.println("--------------------------- [ TEST CAPACITY "+capacity+" ] ------------------------");
        final SerializationMeasurement serializers = new SerializationMeasurement(capacity);
        serializers.testJackson();
        serializers.testGson();
        serializers.testByteBuffer();
        serializers.testProtoBuf();
        serializers.testBigJackson();
        serializers.testBigGson();
        serializers.testBigByteBuffer();
        serializers.testBigProtobufs();
    }

    public static void main(String []args) {
        List<Integer> testCapacities = Arrays.asList(1, 10, 100, 1000, 10000, 100_000);

        for(Integer capacity: testCapacities) {
            runTestCycle(capacity);
        }
        TestHelper.testReport();
    }
}

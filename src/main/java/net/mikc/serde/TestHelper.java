package net.mikc.serde;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

public class TestHelper {

    private static Map<Integer, Map<String, Long>> report = new HashMap<>();
    private static Set<String> tests = new HashSet<>();


    public static void measure(int capacity, String name, Runnable runnable) {
        long start = System.nanoTime();
        runnable.run();
        long d = System.nanoTime() - start;
        System.out.println(name + " :");
        System.out.println("   -> total time taken       : " + printTime(d));
        System.out.println("   -> per instance time taken: " + printTime((float) d / capacity));
        tests.add(name);
        Map<String, Long> row = report.get(capacity);
        if(row == null) {
            report.put(capacity, new HashMap<String, Long>());
            row = report.get(capacity);
        }
        row.put(name, d);
    }

    public static void testReport() {
        try {
            FileWriter fw = new FileWriter("results.csv", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("Capacity");
            Map<Integer, String> rowHeader = new HashMap<>();
            int i=0;
            for(String testName: tests) {
                rowHeader.put(i, testName);
                i++;
                bw.write(", ");
                bw.write(testName);
            }
            bw.newLine();
            int totalRowNum = i;
            List<Integer> capacities = new ArrayList<>(report.keySet());
            Collections.sort(capacities);
            for(Integer capacity: capacities) {
                bw.write(capacity.toString());
                Map<String, Long> row = report.get(capacity);
                for(int j=0; j<totalRowNum; j++) {
                    String testName = rowHeader.get(j);
                    Float duration = (float)row.get(testName)/capacity;
                    bw.write(", ");
                    bw.write(duration.toString());
                }
                bw.newLine();
            }

            bw.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    private static String printTime(float t) {
        if (t > 1e9) {
            return (t / 1e9) + "secs";
        }

        if (t > 1e6) {
            return (t / 1e6) + "ms";
        }

        if (t > 1e3) {
            return (t / 1e3) + "μs";
        }

        return t + "ns";
    }

    public static boolean assertEquals(Object o1, Object o2) {
        if (o1 == null || o2 == null) return false;
        if(!o1.equals(o2)) {
            System.out.println(o1.toString());
            System.out.println(o2.toString());
        }
        return o1.equals(o2);
    }


    public static void assertTrue(String name, boolean value) {
        System.out.println("Test " + name + ": " + (value ? "PASSED" : "FAILED"));
        if(!value) {
            System.exit(-1);
        }
    }
}

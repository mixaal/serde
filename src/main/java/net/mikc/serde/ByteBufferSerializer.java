package net.mikc.serde;

import com.google.common.io.ByteArrayDataOutput;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ByteBufferSerializer {

    private boolean preCompileTypes;
    private InternalType []internalTypes = null;

    public ByteBufferSerializer(boolean preCompileTypes) {
        this.preCompileTypes = preCompileTypes;
    }

    public static void convertToFlat(List target, Object... values) {
        for (int i=0; i<values.length; i++) {
            if(values[i] instanceof List) {
                List l = (List) values[i];
                target.add(l.size());
                convertToFlat(target, l.toArray());
            } else {
                target.add(values[i]);
            }

        }
    }

    enum InternalType {
        INTEGER, LONG, FLOAT, DOUBLE, BOOLEAN, STRING
    }

    public static InternalType[] compileValues(Object ... values) {
        InternalType[] internal = new InternalType[values.length];
        for(int i=0; i< values.length; i++) {
            Object v = values[i];
            if(v instanceof Integer) {
                internal[i] = InternalType.INTEGER;
                continue;
            }
            if(v instanceof Long) {
                internal[i] = InternalType.LONG;
                continue;
            }
            if(v instanceof Float) {
                internal[i] = InternalType.FLOAT;
                continue;
            }
            if(v instanceof Double) {
                internal[i] = InternalType.DOUBLE;
                continue;
            }
            if(v instanceof Boolean) {
                internal[i] = InternalType.BOOLEAN;
                continue;
            }
            if(v instanceof String) {
                internal[i] = InternalType.STRING;
                continue;
            }
            throw new IllegalArgumentException("Unknown type: "+v);
        }
        return internal;
    }

    public ObjectsToBytes convertToBytes2(Object... _values) {
        List flatValues = new ArrayList<>();
        convertToFlat(flatValues, _values);
        long s = System.nanoTime();
        Object []values = flatValues.toArray();
        if(this.preCompileTypes) {
            if(this.internalTypes==null) {
                this.internalTypes = compileValues(values);
            }
        } else {
            this.internalTypes = compileValues(values);
        }

//        InternalType []internalTypes = compileValues(values);
        long d = System.nanoTime() - s;
//        System.out.println("d="+d+"ns");
        s = System.nanoTime();
        int total = 0;
        String sv;
        Integer iv;
        Long lv;
        Boolean bv;
        Float fv;
        Double dv;
        ByteBuffer ibb = ByteBuffer.allocate(4);
        ByteBuffer lbb = ByteBuffer.allocate(8);
        byte[] a = ibb.array();
        byte[] b = lbb.array();

        byte [][]conversion = new byte[values.length][];

        for (int i = 0; i < values.length; i++) {
            switch(internalTypes[i]) {
                case INTEGER:
                iv = ((Integer) values[i]);
                ibb.putInt(0, iv);
                conversion[i] = new byte[4];
                conversion[i][0] = a[0];
                conversion[i][1] = a[1];
                conversion[i][2] = a[2];
                conversion[i][3] = a[3];
                total += 4;
                break;
            case LONG:
                lv = (Long) values[i];
                lbb.putLong(0, lv);
                conversion[i] = new byte[8];
                conversion[i][0] = b[0];
                conversion[i][1] = b[1];
                conversion[i][2] = b[2];
                conversion[i][3] = b[3];
                conversion[i][4] = b[4];
                conversion[i][5] = b[5];
                conversion[i][6] = b[6];
                conversion[i][7] = b[7];
                total += 8;
                break;
            case FLOAT:
                fv = (Float) values[i];
                ibb.putFloat(0, fv);
                conversion[i] = new byte[4];
                conversion[i][0] = a[0];
                conversion[i][1] = a[1];
                conversion[i][2] = a[2];
                conversion[i][3] = a[3];
                total += 4;
                break;
            case DOUBLE:
                dv = (Double) values[i];
                lbb.putDouble(0, dv);
                conversion[i] = new byte[8];
                conversion[i][0] = b[0];
                conversion[i][1] = b[1];
                conversion[i][2] = b[2];
                conversion[i][3] = b[3];
                conversion[i][4] = b[4];
                conversion[i][5] = b[5];
                conversion[i][6] = b[6];
                conversion[i][7] = b[7];
                total += 8;
                break;
            case BOOLEAN:
                bv = (Boolean) values[i];
                conversion[i] = new byte[1];
                conversion[i][0] = bv ? (byte) 1 : (byte) 0;
                total++;
                break;
            case STRING:
                sv = (String) values[i];
                ibb.rewind();
                int l = sv.length();
                ibb.putInt(l);
                byte[] sb = sv.getBytes(StandardCharsets.UTF_8);
//                byte[] sb = new byte[sv.length()];
//                sv.getBytes(0, sv.length(), sb, 0);
                conversion[i] = combineArrays(ibb.array(), sb);
                total += sb.length;
                total += 4; // string length
                break;
            default:
                throw new IllegalArgumentException("Value type not supported: " + values[i]);
            }
        }
        d = System.nanoTime() - s;
//        System.out.println("d2="+d+"ns");
        return new ObjectsToBytes(total, conversion);
    }

    /**
     * Non-recursive solution for fast serialization.
     *
     * @param _values varargs of values to serialize
     * @return {@link ObjectsToBytes}
     */
    public static ObjectsToBytes convertToBytes(Object... _values) {
        List flatValues = new ArrayList<>();
        convertToFlat(flatValues, _values);
        long s = System.nanoTime();
        Object []values = flatValues.toArray();
        long d = System.nanoTime() - s;
//        System.out.println("d="+d+"ns");
        s = System.nanoTime();
        int total = 0;
        String sv;
        Integer iv;
        Long lv;
        Boolean bv;
        Float fv;
        Double dv;
        ByteBuffer ibb = ByteBuffer.allocate(4);
        ByteBuffer lbb = ByteBuffer.allocate(8);
        byte[] a = ibb.array();
        byte[] b = lbb.array();

        byte [][]conversion = new byte[values.length][];

        for (int i = 0; i < values.length; i++) {
            if (values[i] instanceof Integer) {
                iv = ((Integer) values[i]);
                ibb.putInt(0, iv);
                conversion[i] = new byte[4];
                conversion[i][0] = a[0];
                conversion[i][1] = a[1];
                conversion[i][2] = a[2];
                conversion[i][3] = a[3];
                total += 4;
            } else if (values[i] instanceof Long) {
                lv = (Long) values[i];
                lbb.putLong(0, lv);
                conversion[i] = new byte[8];
                conversion[i][0] = b[0];
                conversion[i][1] = b[1];
                conversion[i][2] = b[2];
                conversion[i][3] = b[3];
                conversion[i][4] = b[4];
                conversion[i][5] = b[5];
                conversion[i][6] = b[6];
                conversion[i][7] = b[7];
                total += 8;
            } else if (values[i] instanceof Float) {
                fv = (Float) values[i];
                ibb.putFloat(0, fv);
                conversion[i] = new byte[4];
                conversion[i][0] = a[0];
                conversion[i][1] = a[1];
                conversion[i][2] = a[2];
                conversion[i][3] = a[3];
                total += 4;
            } else if (values[i] instanceof Double) {
                dv = (Double) values[i];
                lbb.putDouble(0, dv);
                conversion[i] = new byte[8];
                conversion[i][0] = b[0];
                conversion[i][1] = b[1];
                conversion[i][2] = b[2];
                conversion[i][3] = b[3];
                conversion[i][4] = b[4];
                conversion[i][5] = b[5];
                conversion[i][6] = b[6];
                conversion[i][7] = b[7];
                total += 8;
            } else if (values[i] instanceof Boolean) {
                bv = (Boolean) values[i];
                conversion[i] = new byte[1];
                conversion[i][0] = bv ? (byte) 1 : (byte) 0;
                total++;
            } else if (values[i] instanceof String) {
                sv = (String) values[i];
                ibb.rewind();
                int l = sv.length();
                ibb.putInt(l);
                byte[] sb = sv.getBytes(StandardCharsets.UTF_8);
//                byte[] sb = new byte[sv.length()];
//                sv.getBytes(0, sv.length(), sb, 0);
                conversion[i] = combineArrays(ibb.array(), sb);
                total += sb.length;
                total += 4; // string length
            } else {
                throw new IllegalArgumentException("Value type not supported: " + values[i]);
            }
        }
        d = System.nanoTime() - s;
//        System.out.println("d2="+d+"ns");
        return new ObjectsToBytes(total, conversion);
    }

    /**
     * Deep recursive solution. However using {@link ByteArrayDataOutput} is kind of slow.
     * Example:
     *   ByteArrayDataOutput byteStream = ByteStreams.newDataOutput();
     *   convertToBytesRecursive(byteStream, ...);
     *   byteStream.byteArray() => byte[]
     *
     * @param byteStream initalized byte stream to use
     * @param values vararg array of values to serialize
     */
//    private static void convertToBytesRecursive(ByteArrayDataOutput byteStream, Object... values) {
//        String sv;
//        for (int i = 0; i < values.length; i++) {
//            if (values[i] instanceof Integer) {
//                byteStream.writeInt((Integer) values[i]);
//            } else if (values[i] instanceof Long) {
//                byteStream.writeLong((Long) values[i]);
//            } else if (values[i] instanceof Boolean) {
//                byteStream.writeBoolean((Boolean) values[i]);
//            } else if (values[i] instanceof String) {
//                sv = (String) values[i];
//                byte[] sb = sv.getBytes(StandardCharsets.UTF_8);
//                byteStream.writeInt(sb.length);
//                byteStream.write(sb);
//            } else if (values[i] instanceof List) {
//                List l = (List) values[i];
//                byteStream.writeInt(l.size());
//                convertToBytes(byteStream, l.toArray());
//            } else {
//                throw new IllegalArgumentException("Value type not supported: " + values[i]);
//            }
//        }
//    }


    public static String readString(ByteBuffer bb) {
        int len = bb.getInt();
        byte[] out = new byte[len];
        bb.get(out);
        return new String(out, StandardCharsets.UTF_8);
    }

    public static byte[] combineArrays(byte[] one, byte[] two) {
        byte[] combined = new byte[one.length + two.length];

        System.arraycopy(one, 0, combined, 0, one.length);
        System.arraycopy(two, 0, combined, one.length, two.length);
        return combined;
    }
}

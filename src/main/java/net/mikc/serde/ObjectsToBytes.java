package net.mikc.serde;

import java.nio.ByteBuffer;

public class ObjectsToBytes {
    private int total;
    private byte[][] conversion;

    public ObjectsToBytes(int total, byte[][]conversion) {
        this.total = total;
        this.conversion = conversion;
    }

    public int getTotalLen() {
        return total;
    }

    public void appendToByteBuffer(ByteBuffer bb) {
        for(int i=0; i<conversion.length; i++) {
            bb.put(conversion[i]);
        }
    }
}

package tech.yongcheng.yongan.sdk.v3.codec.scale.reader;

import tech.yongcheng.yongan.sdk.v3.codec.scale.ScaleCodecReader;
import tech.yongcheng.yongan.sdk.v3.codec.scale.ScaleReader;

public class BoolReader implements ScaleReader<Boolean> {
    @Override
    public Boolean read(ScaleCodecReader rdr) {
        byte b = rdr.readByte();
        if (b == 0) {
            return false;
        }
        if (b == 1) {
            return true;
        }
        throw new IllegalStateException("Not a boolean value: " + b);
    }
}

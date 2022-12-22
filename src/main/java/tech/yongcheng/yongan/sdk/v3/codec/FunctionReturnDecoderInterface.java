package tech.yongcheng.yongan.sdk.v3.codec;

import java.util.List;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.Type;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.TypeReference;

public interface FunctionReturnDecoderInterface {
    List<Type> decode(String rawInput, List<TypeReference<Type>> outputParameters);

    <T extends Type> Type decodeIndexedValue(String rawInput, TypeReference<T> typeReference);
}

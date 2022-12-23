package tech.yongcheng.yongan.sdk.v3.codec.scale;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import tech.yongcheng.yongan.sdk.v3.codec.FunctionEncoderInterface;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.Function;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.Type;
import tech.yongcheng.yongan.sdk.v3.crypto.CryptoSuite;

public class FunctionEncoder extends FunctionEncoderInterface {
    public FunctionEncoder(CryptoSuite cryptoSuite) {
        super(cryptoSuite);
    }

    @Override
    public byte[] encode(Function function) {
        List<Type> parameters = function.getInputParameters();

        String methodSignature = buildMethodSignature(function.getName(), parameters);
        byte[] methodId = this.buildMethodId(methodSignature);
        return encodeParameters(parameters, methodId);
    }

    public static byte[] encodeConstructor(List<Type> parameters) {
        return encodeParameters(parameters, null);
    }

    public static byte[] encodeParameters(List<Type> parameters, byte[] methodID) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        // when wasm call, input first byte is 1; when wasm deploy, input first byte is 0
        try {
            ScaleCodecWriter writer = new ScaleCodecWriter(result);
            if (methodID != null) {
                result.write(methodID);
            }
            for (Type parameter : parameters) {
                TypeEncoder.encode(parameter, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toByteArray();
    }
}

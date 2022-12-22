package tech.yongcheng.yongan.sdk.v3.client.protocol.response;

import tech.yongcheng.yongan.sdk.v3.model.JsonRpcResponse;

public class Abi extends JsonRpcResponse<String> {
    public String getABI() {
        return this.getResult();
    }
}

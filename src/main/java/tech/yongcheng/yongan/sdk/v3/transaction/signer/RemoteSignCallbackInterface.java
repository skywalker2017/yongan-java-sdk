package tech.yongcheng.yongan.sdk.v3.transaction.signer;

import tech.yongcheng.yongan.sdk.v3.crypto.signature.SignatureResult;

public interface RemoteSignCallbackInterface {
    /**
     * receive the signature,and execute the callback function later.
     *
     * @param signature remote sign result
     * @return result code
     */
    int handleSignedTransaction(SignatureResult signature);
}

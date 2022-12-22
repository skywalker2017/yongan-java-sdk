package tech.yongcheng.yongan.sdk.v3.test.transaction.mock;

import tech.yongcheng.yongan.sdk.v3.client.Client;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.Function;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.Type;
import tech.yongcheng.yongan.sdk.v3.contract.Contract;
import tech.yongcheng.yongan.sdk.v3.crypto.keypair.CryptoKeyPair;
import tech.yongcheng.yongan.sdk.v3.model.TransactionReceipt;
import tech.yongcheng.yongan.sdk.v3.model.callback.TransactionCallback;
import tech.yongcheng.yongan.sdk.v3.transaction.model.exception.ContractException;

import java.util.List;

public class MockContract extends Contract {

    protected MockContract(String contractAddress, Client client, CryptoKeyPair credential) {
        super("contractBinary", contractAddress, client, credential);
    }

    public <T extends Type, R> R executeCallWithSingleValueReturn(Function function, Class<R> returnType) throws ContractException {
        return super.executeCallWithSingleValueReturn(function, returnType);
    }

    public List<Type> executeCallWithMultipleValueReturn(Function function) throws ContractException {
        return super.executeCallWithMultipleValueReturn(function);
    }

    public String asyncExecuteTransaction(byte[] data, String funName, TransactionCallback callback, int dagAttribute) {
        return super.asyncExecuteTransaction(data, funName, callback, dagAttribute);
    }

    public String asyncExecuteTransaction(Function function, TransactionCallback callback) {
        return super.asyncExecuteTransaction(function, callback);
    }

    public TransactionReceipt executeTransaction(Function function) {
        return super.executeTransaction(function);
    }

    public TransactionReceipt executeDeployTransaction(byte[] data, String abi) {
        return super.executeDeployTransaction(data, abi);
    }

    public static MockContract load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new MockContract(contractAddress, client, credential);
    }

    public static MockContract deploy(
            Client client,
            String path) throws ContractException {
        return deploy(MockContract.class,
                client,
                client.getCryptoSuite().getCryptoKeyPair(), "", "", new byte[0], path);
    }
}

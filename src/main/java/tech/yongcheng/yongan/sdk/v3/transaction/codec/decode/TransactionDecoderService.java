/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package tech.yongcheng.yongan.sdk.v3.transaction.codec.decode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.yongcheng.yongan.sdk.v3.codec.ContractCodec;
import tech.yongcheng.yongan.sdk.v3.codec.ContractCodecException;
import tech.yongcheng.yongan.sdk.v3.codec.EventEncoder;
import tech.yongcheng.yongan.sdk.v3.codec.FunctionReturnDecoderInterface;
import tech.yongcheng.yongan.sdk.v3.codec.Utils;
import tech.yongcheng.yongan.sdk.v3.codec.abi.FunctionReturnDecoder;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.Type;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.TypeReference;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.Utf8String;
import tech.yongcheng.yongan.sdk.v3.codec.wrapper.ABIDefinition;
import tech.yongcheng.yongan.sdk.v3.codec.wrapper.ABIDefinitionFactory;
import tech.yongcheng.yongan.sdk.v3.codec.wrapper.ABIObject;
import tech.yongcheng.yongan.sdk.v3.codec.wrapper.ABIObjectFactory;
import tech.yongcheng.yongan.sdk.v3.codec.wrapper.ContractABIDefinition;
import tech.yongcheng.yongan.sdk.v3.codec.wrapper.ContractCodecTools;
import tech.yongcheng.yongan.sdk.v3.crypto.CryptoSuite;
import tech.yongcheng.yongan.sdk.v3.model.RetCode;
import tech.yongcheng.yongan.sdk.v3.model.TransactionReceipt;
import tech.yongcheng.yongan.sdk.v3.transaction.model.dto.TransactionResponse;
import tech.yongcheng.yongan.sdk.v3.transaction.model.exception.ContractException;
import tech.yongcheng.yongan.sdk.v3.transaction.tools.JsonUtils;
import tech.yongcheng.yongan.sdk.v3.utils.Hex;

public class TransactionDecoderService implements TransactionDecoderInterface {
    protected static Logger logger = LoggerFactory.getLogger(TransactionDecoderService.class);

    private CryptoSuite cryptoSuite;
    private final ContractCodec contractCodec;
    private final EventEncoder eventEncoder;

    /**
     * create TransactionDecoderService
     *
     * @param cryptoSuite the cryptoSuite used to calculate hash and signatures
     * @param isWasm whether the invoked contract is a Wasm contract
     */
    public TransactionDecoderService(CryptoSuite cryptoSuite, boolean isWasm) {
        super();
        this.cryptoSuite = cryptoSuite;
        this.contractCodec = new ContractCodec(cryptoSuite, isWasm);
        this.eventEncoder = new EventEncoder(cryptoSuite);
    }

    @Override
    public String decodeRevertMessage(String output) {
        output = Hex.trimPrefix(output);
        if (output.length() <= 8) {
            return null;
        } else {
            // This revert msg encoder/decoder only use ABI
            FunctionReturnDecoderInterface functionReturnDecoderInterface =
                    new FunctionReturnDecoder();
            // substring(8) cut off "Error(string)" selector 4 bytes
            List<Type> r =
                    functionReturnDecoderInterface.decode(
                            output.substring(8),
                            Utils.convert(
                                    Collections.singletonList(new TypeReference<Utf8String>() {})));
            return (r.get(0)).toString();
        }
    }

    @Override
    public TransactionResponse decodeReceiptWithValues(
            String abi, String functionName, TransactionReceipt transactionReceipt)
            throws ContractCodecException {
        TransactionResponse response = decodeReceiptWithoutValues(abi, transactionReceipt);
        // only successful tx has return values.
        if (transactionReceipt.getStatus() == 0) {
            List<Type> results =
                    contractCodec.decodeMethodAndGetOutputObject(
                            abi, functionName, transactionReceipt.getOutput());
            response.setResults(results);
        }
        return response;
    }

    @Override
    public TransactionResponse decodeReceiptWithoutValues(
            String abi, TransactionReceipt transactionReceipt) {
        TransactionResponse response = decodeReceiptStatus(transactionReceipt);
        response.setTransactionReceipt(transactionReceipt);
        response.setContractAddress(transactionReceipt.getContractAddress());
        // the exception transaction
        if (transactionReceipt.getStatus() != 0) {
            return response;
        }
        String events = JsonUtils.toJson(decodeEvents(abi, transactionReceipt.getLogEntries()));
        response.setEvents(events);
        return response;
    }

    @Override
    public TransactionResponse decodeReceiptStatus(TransactionReceipt receipt) {
        TransactionResponse response = new TransactionResponse();
        try {
            RetCode retCode = ReceiptParser.parseTransactionReceipt(receipt, null);
            response.setReturnCode(retCode.getCode());
            response.setReceiptMessages(retCode.getMessage());
            response.setReturnMessage(retCode.getMessage());
        } catch (ContractException e) {
            response.setReturnCode(e.getErrorCode());
            response.setReceiptMessages(e.getMessage());
            response.setReturnMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Map<String, List<List<Object>>> decodeEvents(
            String abi, List<TransactionReceipt.Logs> logs) {
        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(cryptoSuite);
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(abi);
        Map<String, List<ABIDefinition>> eventsMap = contractABIDefinition.getEvents();
        Map<String, List<List<Object>>> result = new HashMap<>();
        if (logs == null) {
            return result;
        }
        eventsMap.forEach(
                (name, events) -> {
                    for (ABIDefinition abiDefinition : events) {
                        ABIObject outputObject =
                                ABIObjectFactory.createEventInputObject(abiDefinition);
                        for (TransactionReceipt.Logs log : logs) {
                            String eventSignature =
                                    eventEncoder.buildEventSignature(
                                            decodeMethodSign(abiDefinition));
                            if (log.getTopics().isEmpty()
                                    || !log.getTopics().contains(eventSignature)) {
                                continue;
                            }
                            try {
                                List<Object> list =
                                        ContractCodecTools.decodeJavaObject(
                                                outputObject,
                                                log.getData(),
                                                this.contractCodec.isWasm());
                                if (result.containsKey(name)) {
                                    result.get(name).add(list);
                                } else {
                                    List<List<Object>> l = new ArrayList<>();
                                    l.add(list);
                                    result.put(name, l);
                                }
                            } catch (Exception e) {
                                logger.error(" exception in decodeEventToObject: ", e);
                            }
                        }
                    }
                });
        return result;
    }

    private String decodeMethodSign(ABIDefinition abiDefinition) {
        List<ABIDefinition.NamedType> inputTypes = abiDefinition.getInputs();
        StringBuilder methodSign = new StringBuilder();
        methodSign.append(abiDefinition.getName());
        methodSign.append("(");
        String params =
                inputTypes.stream()
                        .map(ABIDefinition.NamedType::getType)
                        .collect(Collectors.joining(","));
        methodSign.append(params);
        methodSign.append(")");
        return methodSign.toString();
    }

    /** @return the cryptoSuite */
    public CryptoSuite getCryptoSuite() {
        return cryptoSuite;
    }

    /** @param cryptoSuite the cryptoSuite to set */
    public void setCryptoSuite(CryptoSuite cryptoSuite) {
        this.cryptoSuite = cryptoSuite;
    }
}

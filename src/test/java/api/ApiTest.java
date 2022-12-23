package api;
/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fisco.bcos.sdk.jni.rpc.RpcJniObj;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import tech.yongcheng.yongan.sdk.v3.BcosSDK;
import tech.yongcheng.yongan.sdk.v3.client.Client;
import tech.yongcheng.yongan.sdk.v3.client.protocol.model.GroupNodeIniConfig;
import tech.yongcheng.yongan.sdk.v3.client.protocol.response.BcosGroupInfo;
import tech.yongcheng.yongan.sdk.v3.client.protocol.response.BlockNumber;
import tech.yongcheng.yongan.sdk.v3.crypto.CryptoSuite;
import tech.yongcheng.yongan.sdk.v3.http.HttpUtils;
import tech.yongcheng.yongan.sdk.v3.utils.ObjectMapperFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

import static org.mockito.Mockito.*;


public class ApiTest {
    private static final Logger logger = LoggerFactory.getLogger(ApiTest.class);
    private static final int BLOCK_LIMIT_RANGE = 500;

    // ------------basic group info --------------
    private String groupID = "";
    private String chainID;
    private Boolean wasm;
    private Boolean authCheck = false;
    private boolean serialExecute;
    private Boolean smCrypto;
    // ------------basic group info --------------

    // ------------ runtime info -----------------

    private long blockNumber = 0;
    private BcosGroupInfo.GroupInfo groupInfo;
    private GroupNodeIniConfig groupNodeIniConfig;
    private CryptoSuite cryptoSuite;
    private RpcJniObj rpcJniObj;

    private static Client client;

    protected final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    public ApiTest() {
        client = mock(Client.class);
    }

    private void getMock() {
        MockedStatic<HttpUtils> mocked = Mockito.mockStatic(HttpUtils.class);

        //mock带参数的static方法
        mocked.when(() -> HttpUtils.httpRequest("url", body, auth, timeout)).thenReturn("xxx");

        //mock代码中自己new的实例及“该实例的方法”
        MockedConstruction<NewObject> newObjectMocked = Mockito.mockConstruction(NewObject.class);
        Mockito.when(obj.haha()).thenReturn("who am i ?");

        sampleService.helloWorld();
    }

    @BeforeClass
    public static void before() {
        String configFileName =
                "/Users/wangyuan/Downloads/projects/yongcheng/java-sdk/src/test/resources/config.toml";
        BcosSDK sdk = BcosSDK.build(configFileName);
        client = sdk.getClient("group0");
    }

    @Test
    public void getGroup() {
        System.out.println(client.getGroup());
    }

    @Test
    public void getChainId() {
        System.out.println(client.getChainId());
    }

    /*public void getSmCrypto() {
        System.out.println(client..smCrypto);
    }*/

    @Test
    public void getCryptoSuite() {
        System.out.println(client.getCryptoSuite());
    }

    @Test
    public void getCryptoType() {
        System.out.println(client.getCryptoType());
    }

    @Test
    public void isWASM() {
        System.out.println(client.isWASM());
    }

    @Test
    public void isAuthCheck() {
        System.out.println(this.authCheck);
    }

    public void isSerialExecute() {
        System.out.println(this.serialExecute);
    }

    @Test
    public void sendTransaction() {
        client.sendTransaction("", "", true);
        System.out.println(client.sendTransaction("0x1a1c2606636861696e30360667726f757030410d05564d363832303433353530353635393239313035343337373331333736333336323638323731333838313336383236323730343937333630383535363931313738343034393638393332343434323566007d0001060e60806040526040805190810160405280600181526020017f31000000000000000000000000000000000000000000000000000000000000008152506001908051906020019061004f9291906100ae565b5034801561005c57600080fd5b506040805190810160405280600d81526020017f48656c6c6f2c20576f726c642100000000000000000000000000000000000000815250600090805190602001906100a89291906100ae565b50610153565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100ef57805160ff191683800117855561011d565b8280016001018555821561011d579182015b8281111561011c578251825591602001919060010190610101565b5b50905061012a919061012e565b5090565b61015091905b8082111561014c576000816000905550600101610134565b5090565b90565b6104ac806101626000396000f300608060405260043610610057576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680634ed3885e1461005c57806354fd4d50146100c55780636d4ce63c14610155575b600080fd5b34801561006857600080fd5b506100c3600480360381019080803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506101e5565b005b3480156100d157600080fd5b506100da61029b565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561011a5780820151818401526020810190506100ff565b50505050905090810190601f1680156101475780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561016157600080fd5b5061016a610339565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156101aa57808201518184015260208101905061018f565b50505050905090810190601f1680156101d75780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b80600090805190602001906101fb9291906103db565b507f93a093529f9c8a0c300db4c55fcd27c068c4f5e0e8410bc288c7e76f3d71083e816040518080602001828103825283818151815260200191508051906020019080838360005b8381101561025e578082015181840152602081019050610243565b50505050905090810190601f16801561028b5780820380516001836020036101000a031916815260200191505b509250505060405180910390a150565b60018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156103315780601f1061030657610100808354040283529160200191610331565b820191906000526020600020905b81548152906001019060200180831161031457829003601f168201915b505050505081565b606060008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156103d15780601f106103a6576101008083540402835291602001916103d1565b820191906000526020600020905b8154815290600101906020018083116103b457829003601f168201915b5050505050905090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061041c57805160ff191683800117855561044a565b8280016001018555821561044a579182015b8281111561044957825182559160200191906001019061042e565b5b509050610457919061045b565b5090565b61047d91905b80821115610479576000816000905550600101610461565b5090565b905600a165627a7a72305820fd433a091cb8e1aba3f49e5efb35f937e4b22a85a46f35574834d120699d7ae500290b2d00002090ca5b315cb317e2363e7544662af5370f064424af933cf798816df9a11158e93d000046304402205c6ab182e322a63d12f45bf86e1772ad9f8a14c49d2c981f88c8ed898512b5fe02203f5f27b9f46c264dd4499470c8ab195bbfc3fb478d29b133b8f60e00874cf9af4c5c66007d000014743de6185fa5def2794838f67ea050353ba186b7", true).getTransactionReceipt());
    }

    // todo build in demo
    @Test
    public void call() {
    }

    @Test
    public void getBlockNumber() {
        // create request
        BlockNumber blockNumber = client.getBlockNumber("");
        System.out.println(blockNumber.getBlockNumber().longValue());
    }

    @Test
    public void getBlockNumber2() {
        // create request
        BlockNumber blockNumber = client.getBlockNumber("");
        System.out.println(blockNumber.getBlockNumber().longValue());
    }

    @Test
    public void getCode() {

        System.out.println(client.getCode(""));
    }

    @Test
    public void getCode2() {

        System.out.println(client.getCode("", ""));
    }


    @Test
    public void getABI() {

        System.out.println(client.getABI("", ""));
        System.out.println(client.getABI(""));
    }

    @Test
    public void getTotalTransactionCount() {
        // create request for getTotalTransactionCount
        System.out.println(client.getTotalTransactionCount().getTotalTransactionCount().toString());
        System.out.println(
                client.getTotalTransactionCount("").getTotalTransactionCount().toString());
    }

    @Test
    public void getBlockByHash() {
        System.out.println(client.getBlockByHash("blockHash", true, true).getBlock().toString());
        System.out.println(
                client.getBlockByHash("", "blockHash", true, true).getBlock().toString());
    }

    @Test
    public void getBlockByNumber() {
        System.out.println(
                client.getBlockByNumber(new BigInteger("20"), true, true).getBlock().toString());
        System.out.println(
                client.getBlockByNumber("", new BigInteger("20"), true, true)
                        .getBlock()
                        .toString());
    }

    @Test
    public void getBlockHashByNumber() {
        System.out.println(client.getBlockHashByNumber(new BigInteger("20")).getBlockHashByNumber());
        System.out.println(client.getBlockHashByNumber("", new BigInteger("20")).getBlockHashByNumber());
    }

    @Test
    public void getTransaction() {
        System.out.println(client.getTransaction("0x994a177b7d790f46173aa4047c7d563ee513ed9769e6629323d5495c7e4b5fdf", true).getTransaction());
        System.out.println(client.getTransaction("", "0x994a177b7d790f46173aa4047c7d563ee513ed9769e6629323d5495c7e4b5fdf", true).getTransaction());
    }

    @Test
    public void getTransactionReceipt() {
        System.out.println(client.getTransactionReceipt("0x2cdb678cac210b2e4f09fcf590b51be1c04eeb748caecceaccf5c75c665591ea", true).getTransactionReceipt());
        System.out.println(client.getTransactionReceipt("", "0x2cdb678cac210b2e4f09fcf590b51be1c04eeb748caecceaccf5c75c665591ea", true).getTransactionReceipt());
    }

    @Test
    public void getPendingTxSize() {
        System.out.println(client.getPendingTxSize().getPendingTxSize());
        System.out.println(client.getPendingTxSize("").getPendingTxSize());
    }

    //todo useless
    @Test
    public void getBlockLimit() {
        System.out.println(client.getBlockLimit().longValue());
    }

    @Test
    public void getGroupPeers() {
        System.out.println(client.getGroupPeers().getGroupPeers());
    }

    @Test
    public void getPeers() {
        System.out.println(client.getPeers().getPeers());
    }


    @Test
    public void getObserverList() {
        System.out.println(client.getObserverList().getObserverList());
        System.out.println(client.getObserverList("").getObserverList());
    }

    @Test
    public void getSealerList() {
        System.out.println(client.getSealerList());
        System.out.println(client.getSealerList(""));
    }

    @Test
    public void getPbftView() {
        System.out.println(client.getPbftView().getPbftView());
        System.out.println(client.getPbftView("").getPbftView());
    }

    @Test
    public void getSystemConfigByKey() {
        System.out.println(client.getSystemConfigByKey("tx_count_limit").getSystemConfig());
        System.out.println(client.getSystemConfigByKey("", "tx_count_limit").getSystemConfig());
    }

    @Test
    public void getSyncStatus() {
        System.out.println(client.getSyncStatus().getSyncStatus());
        System.out.println(client.getSyncStatus("").getSyncStatus());
    }

    @Test
    public void getConsensusStatus() {
        System.out.println(client.getConsensusStatus().getConsensusStatus());
        System.out.println(client.getConsensusStatus("").getConsensusStatus());
    }

    @Test
    public void getGroupList() {
        System.out.println(client.getGroupList().getResult().toString());
    }

    @Test
    public void getGroupInfo() {
        System.out.println(client.getGroupInfo().getResult().toString());

    }

    @Test
    public void getGroupInfoList() {
        System.out.println(client.getGroupInfoList().getResult());
    }


    //todo fix
    @Test
    public void getGroupNodeInfo() {
        System.out.println(client.getGroupNodeInfo("").getResult());
    }

}

# JAVA-SDK快速开始

## 安装环境
+ Java推荐：JDK 8 （JDK8 至 JDK 14 都支持）

+ IDE：IntelliJ IDE.

## 第一个Solidity智能合约应用
### 1. 创建一个Gradle应用
在IntelliJ IDE中创建一个gradle项目。勾选Gradle和Java
### 2. 在build.gradle中引入Java SDK
`    
compile 'tech.yongcheng.yongan.java-sdk:yongan-java-sdk:3.0.1'
`
### 3. 或通过maven方法引入Java SDK
```xml
<dependency>
  <groupId>tech.yongcheng.yongan.java-sdk</groupId>
  <artifactId>yongan-java-sdk</artifactId>
  <version>3.0.1</version>
  </dependency>
```

### 4. 准备智能合约

#### 编写智能合约
智能合约样例
```solidity
pragma solidity >=0.6.10 <0.8.20;
contract HelloWorld {
    string name;

    constructor(string memory n) public {
        name = n;
    }

    function get() public view returns (string memory) {
        return name;
    }

    function set(string memory n) public {
        name = n;
    }
}
```
#### 编译智能合约
`.sol`的智能合约需要编译成ABI和BIN文件才能部署至区块链网络上。有了这两个文件即可凭借Java SDK进行合约部署和调用。但这种调用方式相对繁琐，需要用户根据合约ABI来传参和解析结果。为此，控制台提供的编译工具不仅可以编译出ABI和BIN文件，还可以自动生成一个与编译的智能合约同名的合约Java类。这个Java类是根据ABI生成的，帮助用户解析好了参数，提供同名的方法。当应用需要部署和调用合约时，可以调用该合约类的对应方法，传入指定参数即可。使用这个合约Java类来开发应用，可以极大简化用户的代码。
```shell
# 假设你已经完成控制台的下载操作，若还没有请查看本文第二节的开发源码步骤
# 切换到fisco/console/目录
cd ~/fisco/console/

# 可通过bash contract2java.sh -h命令查看该脚本使用方法
bash contract2java.sh solidity -p tech.yongcheng.yongan.contract
```

运行成功之后，将会在console/contracts/sdk目录生成java、abi和bin目录，如下所示。

```vtl
# 其它无关文件省略
|-- abi # 生成的abi目录，存放solidity合约编译生成的abi文件
|   |-- HelloWorld.abi
|-- bin # 生成的bin目录，存放solidity合约编译生成的bin文件
|   |-- HelloWorld.bin
|-- java  # 存放编译的包路径及Java合约文件
|   |-- org
|        |--yongcheng
|             |--yongan
|                   |--contract
|                         |--HelloWorld.java  # HelloWorld.sol合约生成的Java文件

```

HelloWorld.java主要接口：
```java
public class HelloWorld extends Contract {

    protected HelloWorld(String contractAddress, Client client, CryptoKeyPair credential);

    public String get() throws ContractException;

    public TransactionReceipt set(String n);
    
    public static HelloWorld load(String contractAddress, Client client, CryptoKeyPair credential);

    public static HelloWorld deploy(Client client, CryptoKeyPair credential, String n) throws
            ContractException;
}

```

其中load与deploy函数用于构造HelloWorld合约对象，其他接口分别用来调用对应的solidity合约的接口。


# java-sdk 配置说明

## 项目目录结构：
```vtl
├── lib
│   ├── yongan-java-sdk-3.x.x.jar
│   └── XXXXX.jar
├── conf
│   ├── applicationContext.xml
│   ├── clog.ini
│   ├── config.toml
│   └── log4j.properties
├── apps
│   └── XXXX.jar
└── other folders
```
其中：
+ conf/config.toml项目配置文件

## 配置项解读
Java SDK主要包括五个配置选项，分别是

+ 权限配置 （必须）

+ 网络连接配置 （必须）

+ 线程池配置（非必须，不配置则使用默认配置值）

+ Cpp SDK日志配置（必须）
### 证书配置
基于安全考虑，Java SDK与节点间采用SSL加密通信，目前同时支持非国密SSL连接以及国密SSL连接，`[cryptoMaterial]`配置SSL连接的证书信息，具体包括如下配置项：

+ `auth`: 商户token；

配置示例
```toml
[cryptoMaterial]

auth = "auth"                           # The certification path
useSMCrypto = "false"                       # RPC SM crypto type
```
### 网络配置

SDK与yongan链节点通信，必须配置SDK连接的节点的IP和Port，`[network]`配置了Java SDK连接的节点信息，具体包括如下配置项：

peers：SDK连接的节点的IP:Port信息，可配置多个连接。

defaultGroup：SDK默认发送请求的群组

配置示例

```toml
[network]
peers=["https://yongan-dev.yongcheng.tech/json-rpc"]    # The peer list to connect
```

### 线程池配置
配置示例
```toml
[threadPool]
# threadPoolSize = "16"         # The size of the thread pool to process message callback
                                # Default is the number of cpu cores
```

### Cpp SDK日志配置
配置示例
```toml
[log]
    enable=true
    log_path=./log
    ; info debug trace
    level=DEBUG
    ; MB
    max_log_file_size=200
```

### 配置示例
config-example.toml
```toml
[cryptoMaterial]

useSMCrypto = "false"
auth = "auth"

[network]
peers=["https://yongan-dev.yongcheng.tech/json-rpc"]


accountAddress = "0xbde319ce76d14a954a1168228ce7f4eda273f085"           # The transactions sending account address
# Default is a randomly generated account
# The randomly generated account is stored in the path specified by the keyStoreDir

# password = ""                 # The password used to load the account file

[threadPool]
# channelProcessorThreadSize = "16"         # The size of the thread pool to process channel callback
# Default is the number of cpu cores

# receiptProcessorThreadSize = "16"         # The size of the thread pool to process transaction receipt notification
# Default is the number of cpu cores

maxBlockingQueueSize = "102400"             # The max blocking queue size of the thread pool


```

# RPC接口文档
## 合约操作接口
### sendTransaction
发送交易到区块链RPC。

参数

+ node：可让RPC发送请求到指定节点

+ signedTransactionData：签名后的交易

+ withProof：返回是否带上默克尔树证明

返回值

+ BcosTransactionReceipt: 节点收到交易后，回复给SDK的回包，包括交易哈希信息。

### call
向节点发送请求，调用合约常量接口。

参数

+ node：可让RPC发送请求到指定节点

+ transaction: 合约调用信息，包含合约地址、合约调用者以及调用的合约接口和参数的abi编码

返回值

+ Call: 合约常量接口的返回结果，包括当前块高、接口执行状态信息以及接口执行结果

### getCode
查询指定合约地址对应的合约代码信息。

参数

+ node：可让RPC发送请求到指定节点

+ address: 合约地址。

返回值

+ Code: 合约地址对应的合约代码。

## 区块链查询类接口
### getBlockNumber
获取Client对象对应的群组最新块高。

参数

+ node：可让RPC发送请求到指定节点

返回值

+ BlockNumber: Client对象对应的群组最新区块高度。
### getTotalTransactionCount
获取Client对应群组的交易统计信息，包括上链的交易数、上链失败的交易数目。

参数

+ node：可让RPC发送请求到指定节点

返回值

+ TotalTransactionCount: 交易统计信息，包括：

+ txSum: 上链的交易总量

+ blockNumber: 群组的当前区块高度

+ failedTxSum: 上链执行异常的交易总量
### getBlockByNumber
根据区块高度获取区块信息。

参数

+ node：可让RPC发送请求到指定节点；

+ blockNumber: 区块高度；

+ onlyHeader：true/false，表明获取的区块信息中是否只获取区块头数据；

+ onlyTxHash：true/false，表明获取的区块信息中是否包含完整的交易信息；

返回值

+ BcosBlock: 查询获取的区块信息

### getBlockByHash
根据区块哈希获取区块信息。

参数

+ node：可让RPC发送请求到指定节点

+ blockHash: 区块哈希

+ onlyHeader：true/false，表明获取的区块信息中是否只获取区块头数据；

+ onlyTxHash: true/false，表明获取的区块信息中是否包含完整的交易信息；

返回值

+ BcosBlock: 查询获取的区块信息。

### getBlockHashByNumber
根据区块高度获取区块哈希

参数

+ node：可让RPC发送请求到指定节点

+ blockNumber: 区块高度

返回值

+ BlockHash: 指定区块高度对应的区块哈希

### getTransactionByHash
根据交易哈希获取交易信息。

参数

+ node：可让RPC发送请求到指定节点

+ transactionHash: 交易哈希

+ withProof：是否带上默克尔树证明

返回值

+ BcosTransaction: 指定哈希对应的交易信息。

### getTransactionReceipt
根据交易哈希获取交易回执信息。

参数

+ node：可让RPC发送请求到指定节点

+ transactionHash: 交易哈希

+ withProof：返回是否带上默克尔树证明

返回值

+ BcosTransactionReceipt: 交易哈希对应的回执信息。

### getPendingTxSize
获取交易池内未处理的交易数目。

参数

+ node：可让RPC发送请求到指定节点

返回值

+ PendingTxSize: 交易池内未处理的交易数目。

### getBlockLimit
获取Client对应群组的BlockLimit，BlockLimit主要用于交易防重。

参数

+ 无

返回值

+ BigInteger: 群组的BlockLimit。

### getPeers
获取指定节点的网络连接信息。

参数

+ endpoint: 被查询的节点的IP:Port。

返回值

+ Peers: 指定节点的网络连接信息。

### getSyncStatus
获取节点同步状态。

参数

+ node：可让RPC发送请求到指定节点

返回值

+ SyncStatus: 区块链节点同步状态。

### getSystemConfigByKey
根据指定配置关键字获取系统配置项的值。

参数

+ node：可让RPC发送请求到指定节点

+ key: 系统配置项，目前包括tx_count_limit, consensus_leader_period.

返回值

+ SystemConfig: 系统配置项的值。

## 共识查询接口
### getObserverList
获取Client对应群组的观察节点列表。

参数

+ node：可让RPC发送请求到指定节点

返回值

+ ObserverList: 观察节点列表。

### getSealerList
获取Client对应群组的共识节点列表。

参数

+ node：可让RPC发送请求到指定节点

返回值

+ SealerList: 共识节点列表。

### getPbftView
节点使用PBFT共识算法时，获取PBFT视图信息。

参数

+ node：可让RPC发送请求到指定节点

返回值

+ PbftView: PBFT视图信息。

### getConsensusStatus
获取节点共识状态。

参数

+ node：可让RPC发送请求到指定节点

返回值

+ ConsensusStatus: 节点共识状态。

## 群组查询接口

### getGroupInfo
查询当前群组的状态信息。

参数

+ 无

返回值

+ BcosGroupInfo: 被查询的群组状态信息。

### getGroupList
获取当前节点的群组列表。

参数

+ 无

返回值

+ BcosGroupList: 当前节点的群组列表。

### getGroupPeers
获取当前节点指定群组连接的节点列表。

参数

+ 无

返回值

+ GroupPeers: 指定群组连接的节点列表。

### getGroupInfoList
获取当前节点群组信息列表。

参数

+ 无

返回值

+ BcosGroupInfoList: 当前节点群组信息列表。

### getGroupNodeInfo
获取群组内指定节点的信息。

参数

+ node: 指定节点名

返回值

+ BcosGroupNodeInfo: 查询获取的节点信息。
 

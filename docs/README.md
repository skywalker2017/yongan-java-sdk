# JAVA-SDK快速入门
## 安装环境
+ Java推荐：JDK 8 （JDK8 至 JDK 14 都支持）

+ IDE：IntelliJ IDE.

## 创建工程
### 1. 创建一个Gradle应用
在IntelliJ IDE中创建一个gradle项目。勾选Gradle和Java
### 2. 在build.gradle中引入Java SDK
`compile ('org.yong-an.java-sdk:yong-an-java-sdk:3.0.1')
`
### 3. 或通过maven方法引入Java SDK
```xml
<dependency>
  <groupId>org.yong-an.java-sdk</groupId>
  <artifactId>yong-an-java-sdk</artifactId>
  <version>3.0.1</version>
  </dependency>
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
 

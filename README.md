# Bridge activation guide
## Environment requirements
You need to have Java at least of version 8 installed. If you don't have it, install it the most
 convenient way for you. For example, from [the official website](https://openjdk.java.net/install/index.html)
 or with [Sdkman](https://sdkman.io/install).
## Step by step activation
1. Update `/src/main/resources/` with the json from the referendum proof endpoint
2. Update `src/main/java/ContractActivation.java':
    * Set ```private static final String url = "";``` to the Ethereum node, for example ```private static final String url = "http://ethereumnode.com";```
    * Update ```private static final String privateKey = ""``` with you Ethereum private key, for example ```private static final String privateKey = "1sb3rbf7r7g7djwrt";```
    * Update ```private static final String contractAddress = "";``` with contract address, for example ```private static final String contractAddress = "0xbbf10481398c7110d8d0640e8c56afcb8ad43a62";```
3. Execute ```gradlew run``` to run the application from the command line
4. Check the output. There should be a transaction hash.

[![Version](https://jitpack.io/v/unstoppabledomains/resolution-java.svg)](https://jitpack.io/#unstoppabledomains/resolution-java)
[![CI](https://github.com/unstoppabledomains/resolution-java/workflows/Build%20%26%20test/badge.svg?branch=master)](https://github.com/unstoppabledomains/resolution-java/actions)
[![Version](https://img.shields.io/github/license/unstoppabledomains/resolution-java)](LICENSE)
[![Unstoppable Domains Documentation](https://img.shields.io/badge/docs-unstoppabledomains.com-blue)](https://docs.unstoppabledomains.com/)
[![Get help on Discord](https://img.shields.io/badge/Get%20help%20on-Discord-blueviolet)](https://discord.gg/b6ZVxSZ9Hn)

# Resolution-Java

Resolution-Java is a library for interacting with blockchain domain names. It can be used to retrieve [payment addresses](https://unstoppabledomains.com/features#Add-Crypto-Addresses) and IPFS hashes for [decentralized websites](https://unstoppabledomains.com/features#Build-Website).

Resolution-Java is primarily built and maintained by [Unstoppable Domains](https://unstoppabledomains.com/).

Resolution supports different decentralized domains. Please, refer to the [Top Level Domains List](https://resolve.unstoppabledomains.com/supported_tlds)

- [Installing Resolution](#installing-resolution-java)
- [Using Resolution](#using-resolution)
- [Development](#development)
- [Contributions](#contributions)
- [Free advertising for integrated apps](#free-advertising-for-integrated-apps)

# Installing resolution-java

The most recent release of this library is available on [JitPack](https://jitpack.io/#unstoppabledomains/resolution-java).

### Prerequisites

Java 8+ version is required to use this library.

# Using Resolution

## Initialize with Unstoppable Domains' Proxy Provider

```java
// obtain a key by following this document https://docs.unstoppabledomains.com/domain-distribution-and-management/quickstart/retrieve-an-api-key/#api-key
DomainResolution resolution = new Resolution("<api_key>");

// or

DomainResolution resolution = Resolution
  .builder()
  .udUnsClient("<api_key>")
  .build();
```

> NOTE: The apiKey is only used resolve domains from UNS. Behind the scene, it still uses the default ZNS (Zilliqa) RPC url. For additional control, please specify your ZNS configuration.

```java
DomainResolution resolution = Resolution
  .builder()
  .udUnsClient("<api_key>")
  .znsProviderUrl("https://api.zilliqa.com")
  .build();

```

## Initialize with Custom Ethereum Configuration

You may want to specify a custom provider:
 - if you want to use a dedicated blockchain node
 - if you want to monitor app usage
 - if you already have a provider in your app to re-use it for domain resolution

Default provider can be changed by using the builder class `ResolutionBuilder`.


```java
// obtain a key from https://www.infura.io
String ethProviderURL = "https://mainnet.infura.io/v3/<infura_api_key>";
String polygonProviderURL = "https://polygon-mainnet.infura.io/v3/<infura_api_key>";

DomainResolution resolution = Resolution.builder()
  .unsProviderUrl(UNSLocation.Layer1, ethProviderURL)
  .unsProviderUrl(UNSLocation.Layer2, polygonProviderURL)
  .znsProviderUrl("https://api.zilliqa.com")
  .build();

```
## Additional control with custom HTTP provider

```java
DomainResolution resolution = Resolution
  .builder()
  .udUnsClient("<api_key>") // or your custom Ethereum provider
  .provider(
    new IProvider() {
      @Override
      public JsonObject request(String url, JsonObject body) throws IOException {
          // TODO Make post request to url with given body
          // and return JsonObject from the response
          return null;
      }

      @Override
      public IProvider setHeader(String key, String value) {
          return this;
      }
    }
  )
  .build();

// Adding a custom header to the DefaultProvider

DefaultProvider myProvider = DefaultProvider
  .cleanBuild()
  .setHeader("custom-header", "custom-value")
  .setHeader("new-header", "new-value");

DomainResolution resolution = Resolution
  .builder()
  .udUnsClient("<api_key>") // or your custom Ethereum provider
  .provider(myProvider)
  .build();

// All network calls will be made with headers "custom-header" and "new-header" instead of default ones

```

## Examples

[Live usage examples](samples.md)

### Getting a domain's crypto address

Resolving a domain and getting a currency address.

**`getAddress(String domain, String ticker)`**

This API is used to retrieve wallet address for single address record. (See
[Cryptocurrency payment](https://docs.unstoppabledomains.com/resolution/guides/records-reference/#cryptocurrency-payments)
section for the record format)

With `brad.crypto` has `crypto.ETH.address` on-chain:
```java
String addr = resolution.getAddress("brad.crypto", "eth");
assertEquals("0x8aaD44321A86b170879d7A244c1e8d360c99DdA8", addr);

```

**`getMultiChainAddress(String domain, String ticker, String network)`**

This API is used to retrieve wallet address for multi-chain address records.
(See
[multi-chain currency](https://docs.unstoppabledomains.com/resolution/guides/records-reference/#multi-chain-currencies))

With `brad.crypto` has `crypto.USDT.version.ERC20.address` and `crypto.USDT.version.OMNI.address` on-chain:
```java
// Get address of token present in multiple chains
String usdtErc20Addr = resolution.getMultiChainAddress("brad.crypto", "USDT", "ERC20");
assertEquals("0x8aaD44321A86b170879d7A244c1e8d360c99DdA8", usdtErc20Addr);

String usdtOmniAddr = resolution.getMultiChainAddress("brad.crypto", "USDT", "OMNI");
assertEquals("1Ap8kmF4ZoPjt6ZYAfCaTKsbncky3F8eTV", usdtOmniAddr);
```

**`getAddress(String domain, String network, String token)`**

This (Beta) API can be used to retrieve wallet address for single chain and multi-chain address records.

With `brad.crypto` has `crypto.ETH.address`, `crypto.USDT.version.ERC20.address` and `crypto.USDT.version.OMNI.address` on-chain:

```java
String addr = resolution.getAddress("brad.crypto", "eth", "eth");
assertEquals("0x8aaD44321A86b170879d7A244c1e8d360c99DdA8", addr);
// Get address of token present in multiple chains
String usdtErc20Addr = resolution.getAddress("brad.crypto", "ETH", "USDT");
assertEquals("0x8aaD44321A86b170879d7A244c1e8d360c99DdA8", usdtErc20Addr);

String usdtOmniAddr = resolution.getAddress("brad.crypto", "OMNI", "USDT");
assertEquals("1Ap8kmF4ZoPjt6ZYAfCaTKsbncky3F8eTV", usdtOmniAddr);
```

> **Note** that the API will infer `ERC20` standard as `ETH` network. 


The API can also be used by crypto exchanges to infer wallet addresses. In
centralized exchanges, users have same wallet addresses on different networks
with same wallet family. (See [Blockchain Family, Network, Token Level Addresses](https://apidocs.unstoppabledomains.com/resolution/guides/records-reference/#blockchain-family-network-token-level-addresses) section for the record format)

With `brad.crypto` only has `token.EVM.address` record on-chain.
The API resolves to same wallet address for tokens live on EVM compatible networks.

```java
String ethAddr = resolution.getAddress("brad.crypto", "eth", "eth");
assertEquals("0x8aaD44321A86b170879d7A244c1e8d360c99DdA8", addr);
String usdtETHAddr = resolution.getAddress("brad.crypto", "eth", "usdt");
assertEquals("0x8aaD44321A86b170879d7A244c1e8d360c99DdA8", addr);
String usdtOnAvaxAddr = resolution.getAddress("brad.crypto", "avax", "usdt");
assertEquals("0x8aaD44321A86b170879d7A244c1e8d360c99DdA8", addr);
```

With `brad.crypto` only has `token.EVM.ETH.address`
record on chain. The API resolves to the same wallet address for tokens
specifically on Ethereum network.

```java
String ethAddr = resolution.getAddress("brad.crypto", "eth", "eth");
assertEquals("0x8aaD44321A86b170879d7A244c1e8d360c99DdA8", addr);
String usdtETHAddr = resolution.getAddress("brad.crypto", "eth", "usdt");
assertEquals("0x8aaD44321A86b170879d7A244c1e8d360c99DdA8", addr);
String usdtOnAvaxAddr = resolution.getAddress("brad.crypto", "avax", "usdt");
assertEquals(null, addr); // it won't resolve for AVAX
```

The API is compatible with other address formats. If a domain has multiple
address formats set, it will follow the algorithm described as follow:

if a domain has following records set:

```
token.EVM.address
crypto.USDC.version.ERC20.address
token.EVM.ETH.USDC.address
crypto.USDC.address
token.EVM.ETH.address
```

`getAddress(domain, 'ETH', 'USDC')` will lookup records in the following order:

```
1. token.EVM.ETH.USDC.address
2. crypto.USDC.address
3. crypto.USDC.version.ERC20.address
4. token.EVM.ETH.address
5. token.EVM.address
```

### Getting a domain owner's Ethereum address

Each decentralized domain is owned by someone on the blockchain and held within their wallet. The following command will return the domain owner's Ethereum address.

```java
String owner = resolution.getOwner("brad.crypto");
assertEquals("0x8aad44321a86b170879d7a244c1e8d360c99dda8", owner);
```

You can also get the result in a batch format for UNS:

```java
List<String> domains = Arrays.asList("brad.crypto", "homecakes.crypto");
Map<String, String> owners = mainnetResolution.getBatchOwners(domains);
assertEquals("0x8aad44321a86b170879d7a244c1e8d360c99dda8", owners.get("brad.crypto"));
assertEquals("0xe7474d07fd2fa286e7e0aa23cd107f8379085037", owners.get("homecakes.crypto"));
```

### Getting a domain's IPFS hash

Decentralized websites host their content on decentralized file storage systems such as [IPFS](http://ipfs.io/).
To get the IPFS hash associated with a domain (and therefore its content), you can use the `getIpfsHash` method.

```java
String ipfs = resolution.getIpfsHash("brad.crypto");
assertEquals("Qme54oEzRkgooJbCDr78vzKAWcv6DDEZqRhhDyDtzgrZP6", ipfs);
```

### Getting a domain's record

Retrieve any record of domain. Applications sometimes set custom records for a domain to use within their application. To read these records, use the `getRecord` method.

```java
String record = resolution.getRecord("ryan.crypto", "custom.record.value");
assertEquals("Example custom record value", record);
```

## Errors

If the domain you requested is not registered or doesn't have the record you are looking for, this library will throw a `NamingServiceException` error with one of these codes. We recommend creating customized errors in your app based on the return value of the error.

```java
public enum NSExceptionCode {
  UnsupportedDomain,
  UnregisteredDomain,
  UnknownCurrency,
  RecordNotFound,
  BlockchainIsDown,
  UnknownError,
  IncorrectContractAddress,
  IncorrectMethodName,
  UnspecifiedResolver,
  UnsupportedCurrency,
  NotImplemented,
  InconsistentDomainArray,
  InvalidDomain,
  ReverseResolutionNotSpecified;
}
```

Please see the [Resolution-Java Error Codes](https://docs.unstoppabledomains.com/developer-toolkit/resolution-integration-methods/resolution-libraries/resolution-java/#error-codes) page for details of the specific error codes.

# Development

## Build & test

Resolution library relies on environment variables to load TestNet RPC Urls. This way, our keys don't expose directly to the code. These environment variables are:

* L1_TEST_NET_RPC_URL
* L2_TEST_NET_RPC_URL

> Note: if you don't wish to install Gradle you can use it with wrapper: `./gradlew` instead of `gradle`.

- To run a build with associated tests, use `gradle build`.
- To run a build without running the tests, use `gradle build -x test`.

## Internal network config

Internal [network config](src/main/resources/com/unstoppabledomains/config/network/uns-config.json)
can be updated by running the `gradle pullNetworkConfig` task and committing the updated file.

# Contributions

Contributions to this library are more than welcome. The easiest way to contribute is through GitHub issues and pull requests.

# Free advertising for integrated apps

Once your app has a working Unstoppable Domains integration, [register it here](https://unstoppabledomains.com/app-submission). Registered apps appear on the Unstoppable Domains [homepage](https://unstoppabledomains.com/) and [Applications](https://unstoppabledomains.com/apps) page — putting your app in front of tens of thousands of potential customers per day.

Also, every week we select a newly-integrated app to feature in the Unstoppable Update newsletter. This newsletter is delivered to straight into the inbox of ~100,000 crypto fanatics — all of whom could be new customers to grow your business.

# Get help

[Join our discord community](https://discord.gg/unstoppabledomains) and ask questions.

# Help us improve

We're always looking for ways to improve how developers use and integrate our products into their applications. We'd love to hear about your experience to help us improve by [taking our survey](https://form.typeform.com/to/uHPQyHO6).

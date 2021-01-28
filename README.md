[![Version](https://img.shields.io/maven-central/v/com.unstoppabledomains/resolution)](https://search.maven.org/artifact/com.unstoppabledomains/resolution)
[![CI](https://github.com/unstoppabledomains/resolution-java/workflows/Build%20%26%20test/badge.svg?branch=master)](https://github.com/unstoppabledomains/resolution-java/actions)
[![Version](https://img.shields.io/github/license/unstoppabledomains/resolution-java)](LICENSE)
[![Unstoppable Domains Documentation](https://img.shields.io/badge/docs-unstoppabledomains.com-blue)](https://docs.unstoppabledomains.com/)
[![Get help on Discord](https://img.shields.io/badge/Get%20help%20on-Discord-blueviolet)](https://discord.gg/b6ZVxSZ9Hn)

# Resolution-Java

Resolution-Java is a library for interacting with blockchain domain names. It can be used to retrieve [payment addresses](https://unstoppabledomains.com/features#Add-Crypto-Addresses), IPFS hashes for [decentralized websites](https://unstoppabledomains.com/features#Build-Website), and GunDB usernames for [decentralized chat](https://unstoppabledomains.com/chat).

Resolution-Java is primarily built and maintained by [Unstoppable Domains](https://unstoppabledomains.com/).

Resoultion-Java supports decentralized domains across two zones:

- Crypto Name Service (CNS)
  - `.crypto`
- Ethereum Name Service (ENS)
  - `.eth`
  - `.kred`
  - `.xyz`
  - `.luxe`

# Releases

The most recent release of this library is available on [Maven Central](https://search.maven.org/artifact/com.unstoppabledomains/resolution). 

# Usage

This library uses [linkpool](https://linkpool.io/) as default blockchain provider for CNS & ENS and 
[zilliqa](https://zilliqa.com) for ZNS (**Mainnet** is default network for all).  
If you want to use an alternative blockchain provider such as [infura](https://infura.io/) (or any other), you can
 change default settings:
 
```java
// Default config: 

DomainResolution resolution = new Resolution(); 

// Optionally override default config using builder options:

DomainResolution resolution = Resolution.builder()
                .chainId(NamingServiceType.ENS, Network.ROPSTEN)
                .providerUrl(NamingServiceType.ENS, "https://ropsten-rpc.linkpool.io/")
                .build(); 

// Infura config:

DomainResolution resolution = Resolution.builder()
                .chainId(NamingServiceType.ENS, Network.ROPSTEN)
                .infura(NamingServiceType.ENS, <ProjectId>)
                .infura(NamingServiceType.CNS, Network.MAINNET, <ProjectId>)
                .build();

// Custom provider config:

DomainResolution resolution = Resolution.builder()
                .provider(new IProvider() {      
                    @Override
                    public JsonObject post(String url, JsonObject body) throws IOException {
                        // TODO Make post request to url with given body 
                        // and return JsonObject from the response
                        return null;
                    }
                })
                .build();

```

[Live usage examples](samples.md)

### Getting a domain's crypto address

Resolving a domain and getting a currency address.

```java
String addr = resolution.getAddress("brad.crypto", "eth");
assertEquals("0x8aaD44321A86b170879d7A244c1e8d360c99DdA8", addr);
```

### Getting a domain owner's Ethereum address

Each decentralized domain is owned by someone on the blockchain and held within their wallet. The following command will return the domain owner's Ethereum address.

```java
String owner = resolution.getOwner("brad.crypto");
assertEquals("0x8aad44321a86b170879d7a244c1e8d360c99dda8", owner);
```

### Getting a domain's IPFS hash

Decentralized websites host their content on decentralized file storage systems such as [IPFS](http://ipfs.io/). 
To get the IPFS hash associated with a domain (and therefore its content), you can use the `getIpfsHash` method.

```java
String ipfs = resolution.getIpfsHash("brad.crypto");
assertEquals("Qme54oEzRkgooJbCDr78vzKAWcv6DDEZqRhhDyDtzgrZP6", ipfs);
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
  UnspecifiedResolver;
}
```

# Development

## Build & test

> Note: if you don't wish to install Gradle you can use it with wrapper: `./gradlew` instead of `gradle`.

- Configure a `TESTING_PROVIDER_URL` and `TESTING_INFURA_PROJECT_ID` environment variables with your blockchain
 provider for testing.
- To run a build with associated tests, use `gradle build`.
- To run a build without running the tests, use `gradle build -x test`.

## Internal network config

Internal [network config](src/main/resources/com/unstoppabledomains/config/network/network-config.json) 
can be updated by running the `gradle pullNetworkConfig` task and committing the updated file.

## Versioning & release process

### On codebase updates

- Contributors should update [CHANGELOG.md](CHANGELOG.md) with the listed changes, and increment the client version in [client.json](src/main/resources/com/unstoppabledomains/client/client.json).

### New packages

- Create a new Github release (using `semver`);
- Wait for a successful Github Workflow publishing;
- Release staging repository by logging into [Nexus Repository Manager](https://oss.sonatype.org/) and
 performing 'close' and then 'release' actions.

As an alternative to a CI release, you can perform a manual publish by following these steps:

- Configure environment variables: `nexusUsername`, `nexusPassword`, `signingKey`, `signingPassword`;
- Run `gradle publish`;
- Login to [Nexus Repository Manager](https://oss.sonatype.org/) and perform 'close' and then
  'release' actions on a staged repository **OR** run `gradle closeAndReleaseRepository`.

# Contributions

Contributions to this library are more than welcome. The easiest way to contribute is through GitHub issues and pull requests.

# Free advertising for integrated apps

Once your app has a working Unstoppable Domains integration, [register it here](https://unstoppabledomains.com/app-submission). Registered apps appear on the Unstoppable Domains [homepage](https://unstoppabledomains.com/) and [Applications](https://unstoppabledomains.com/apps) page — putting your app in front of tens of thousands of potential customers per day.

Also, every week we select a newly-integrated app to feature in the Unstoppable Update newsletter. This newsletter is delivered to straight into the inbox of ~100,000 crypto fanatics — all of whom could be new customers to grow your business.

# Get help
[Join our discord community](https://discord.com/invite/b6ZVxSZ9Hn) and ask questions.  

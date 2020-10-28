[![Version](https://img.shields.io/maven-central/v/com.unstoppabledomains/resolution)](https://search.maven.org/artifact/com.unstoppabledomains/resolution)
[![CI](https://github.com/unstoppabledomains/resolution-java/workflows/Build%20%26%20test/badge.svg?branch=master)](https://github.com/unstoppabledomains/resolution-java/actions)
[![Version](https://img.shields.io/github/license/unstoppabledomains/resolution-java)](LICENSE)
[![Unstoppable Domains Documentation](https://img.shields.io/badge/docs-unstoppabledomains.com-blue)](https://docs.unstoppabledomains.com/)
[![Chat on Telegram](https://img.shields.io/badge/Chat%20on-Telegram-brightgreen.svg)](https://t.me/unstoppabledev)

# Easy way to resolve .crypto blockchain name
This java library allows to resolve the various cryptocurrencies addresses attached to a domain, ipfs-hash and other
 owners meta-data.

# Releases
Latest library release is available on [Maven Central](https://search.maven.org/artifact/com.unstoppabledomains/resolution). 

# Usage 
We are using [linkpool](https://www.linkpool.io/) as our choice of blockchain provider.
Feel free to try other blockchain providers (as [infura](https://infura.io/) or any others):
```
resolution = new Resolution("https://main-rpc.linkpool.io");

resolution = new Resolution("https://mainnet.infura.io/v3/<ProjectId>");
```

[Live usage examples](samples.md)

### Getting the currency address
To resolve a domain and get the currency address is easy as this:
```
  String addr = resolution.addr("brad.crypto", "eth");
  assertEquals("0x8aaD44321A86b170879d7A244c1e8d360c99DdA8", addr);
```

### Getting the owner's etherium address
Domains are owned by someone on the blockchain. This is how you can request the owner address from etherium:
```
  String owner = resolution.owner("brad.crypto");
  assertEquals("0x8aad44321a86b170879d7a244c1e8d360c99dda8", owner);
```

### Getting the ipfs hash from domain
Trully decentralized websites are obtain by putting the content on decentralized storage such as [ipfs](http://ipfs.io/) 
and attaching the ipfs-hash to the domain. In order to get the files back someone need to resolve the domain for that
 hash and talk with ipfs system. To resolve
 the domain for the hash you can use ipfsHash method:
```
  String ipfs = resolution.ipfsHash("brad.crypto");
  assertEquals( "Qme54oEzRkgooJbCDr78vzKAWcv6DDEZqRhhDyDtzgrZP6", ipfs);
```

## Errors
When something goes wrong (domain is not registered or doesn't have a certain record) this library is throwing a 
NamingServiceException with one of these codes:
```
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

Note: if you don't wish to install Gradle you can use it with wrapper: `./gradlew` instead of `gradle`.

- Configure `TESTING_PROVIDER_URL` env variable with your blockchain provider for testing;
- Run builds with `gradle build` (implies run of tests);
- `gradle build -x test` - build without running tests. 

## Internal network config

Internal [network config](src/main/resources/com/unstoppabledomains/config/network/network-config.json) 
can be updated by running `gradle pullNetworkConfig` task and commiting updated file.

## Versioning & release process

##### With the updates to the codebase:  

- [CHANGELOG.md](CHANGELOG.md) should be updated with the listed changes, and 
a corresponding client version should be bumped in [client.json](src/main/resources/com/unstoppabledomains/client/client.json).

##### New packages:  

- Create a new Github release (using semver);
- Wait for a successful Github Workflow publishing;
- Release staging repository by logging into [Nexus Repository Manager](https://oss.sonatype.org/) and
 performing 'close' and then 'release' actions.

**Alternatively** to a CI release you can perform a manual publish by following next steps:
- Configure env variables: `nexusUsername`, `nexusPassword`, `signingKey`, `signingPassword`;  

- Run `gradle publish`;
- Login to [Nexus Repository Manager](https://oss.sonatype.org/) and perform 'close' and then
  'release' actions on a staged repository **OR** run `gradle closeAndReleaseRepository`.  

# To-Do
It is planned to add a support for Zilliqa blockchain (domain extension .zil) and more. Feel free to contribute!

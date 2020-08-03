# Easy way to resolve .crypto blockchain name
This java library allows to resolve the various cryptocurrencies addresses attached to a domain, ipfs-hash and other owners meta-data

# Releases
Latest library release is available on [Maven Central](https://search.maven.org/artifact/com.unstoppabledomains.resolution/resolution/1.0.8/jar) 

```
  implementation("com.unstoppabledomains.resolution:resolution:1.0.8")
```

# Usage Examples
We are using infura as our choice of blockchain provider and it requires a [project id](https://infura.io/docs/gettingStarted/authentication) to be bundled with the url.
Feel free to try other blockchain providers
```
resolution = new Resolution("https://mainnet.infura.io/v3/<ProjectId>");
```

[Live usage examples](/samples.md)

### Getting the currency address
To resolve a domain and get the currency address is easy as this
```
  String addr = resolution.addr("brad.crypto", "eth");
  assertEquals("0x8aaD44321A86b170879d7A244c1e8d360c99DdA8", addr);
```

### Getting the owner's etherium address
Domains are owned by someone on the blockchain. This is how you can request the owner address from etherium
```
  String owner = resolution.owner("brad.crypto");
  assertEquals("0x8aad44321a86b170879d7a244c1e8d360c99dda8", owner);
```

### Getting the ipfs hash from domain
Trully decentralized websites are obtain by putting the content on decentralized storage such as [ipfs](http://ipfs.io/) and attaching the ipfs-hash to the domain.
In order to get the files back someone need to resolve the domain for that hash and talk with ipfs system. To resolve the domain for the hash you can use ipfsHash method
```
  String ipfs = resolution.ipfsHash("brad.crypto");
  assertEquals( "Qme54oEzRkgooJbCDr78vzKAWcv6DDEZqRhhDyDtzgrZP6", ipfs);
```


# Errors
When something goes wrong ( domain is not registered or doesn't have a certain record ) this library is throwing a NamingServiceException with one of these codes
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

# To-Do
It is planned to add a support for Zilliqa blockchain (domain extension .zil) and ENS support as well. Feel free to contribute!

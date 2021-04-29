## 1.13.1
* Fix the deserialization bug with Jakson on NetworkConfigLoader.java

## 1.13.0
* Introduce DomainResolution#getMultiChainAddress general method to fetch a ticker address from specific chain
* Deprecate DomainResolution#getUsdt method in favor of DomainResolution#getMultiChainAddress

## 1.12.1
* Remove dependency 'org.apache.commons:commons-math3:3.6.1' 
* Added linter to make sure there is no unused dependencies. 

## 1.12.0
* Autoconfigure chainId from the blockchainprovider url when setting one via Builder#ProviderUrl

## 1.11.0
* Use Infura Ethereum Provider by default

## 1.10.0
* Fixed missing information on NamingServiceException thrown from ZNS
* Introduced IProvider interface -- implement it in order to get full control over HTTP requests to blockchain provider  
* Moved Lombok dependency to compileOnly group
## 1.9.1
* Added ability to instantly check a domain according to naming service rules for valid domain names

## 1.9.0
* Introduced Resolution#dns method to query dns records from .crypto domains
* Introduced Resolution#usdt method to query usdt address of different chains such as tron, omni, eos and erc20

## 1.8.1
* Solve issue with recent etherium node update
* Moved to use ProxyReader contract instead of direct communication with Registry/Resolver contracts

## 1.8.0
* Replace Resolution constructor with configurable builder
* Introduce DomainResolution interface
* Replace deprecated APIs 
* Update network-config 
* Code cleanup
* Fix infura response handling to align with a new format

## 1.7.0
* Plugged-in network config file with contracts
* Code cleanup

## 1.6.1
* Deprecated following methods from Resolution.java class: 
 - owner in favor of getOwner
 - addr in favor of getAddress
 - email in favor of getEmail
 - ipfsHash in favor of getIpfsHash
 - namehash in favor of getNamehash

## 1.6.0
* Added Ens support

## 1.5.1

* Code cleanup

## 1.5.0

* Fix resource read
* Change group id to 'com.unstoppabledomains'

## 1.4.0

* Setup CI/CD process

## 1.3.0

* Optimized build process 
* Client version specification 
* Changelog introduced

## 1.2.2 and earlier

* Changelog is not tracked

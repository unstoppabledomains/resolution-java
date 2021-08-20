package com.unstoppabledomains.resolution;

import java.util.List;

import com.unstoppabledomains.config.network.model.Network;
import com.unstoppabledomains.exceptions.dns.DnsException;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;
import com.unstoppabledomains.resolution.dns.DnsRecord;
import com.unstoppabledomains.resolution.dns.DnsRecordsType;
import com.unstoppabledomains.resolution.naming.service.NamingServiceType;

public interface DomainResolution {

    /**
     * Checks if the domain name is valid according to naming service rules for valid domain names
     *
     * @param domain domain name to be checked
     * @return true if domain name is valid
     * @throws NamingServiceException when it fails to make network call
     */
    boolean isSupported(String domain) throws NamingServiceException;

    /**
     * Returns configured network id
     * @param type which NamingService you are interested in
     * @return Network object with name and code property
     */
    Network getNetwork(NamingServiceType type);

    /**
     * Resolves domain for a specific record
     *
     * @param domain domain name such as "brad.crypto"
     * @param recordKey key of the record
     * @return address in hex-string format
     * @throws NamingServiceException when domain has no record of key
     */
    String getRecord(String domain, String recordKey) throws NamingServiceException;
    
    /**
     * Resolves domain for a specific ticker address
     *
     * @param domain domain name such as "brad.crypto"
     * @param ticker coin ticker such as ETH
     * @return address in hex-string format
     * @throws NamingServiceException when domain has no record of a given currency or it's tld is not supported
     */
    String getAddress(String domain, String ticker) throws NamingServiceException;

    /**
     * Resolves domain for a cross chain address
     * @param domain domain name usch as "brad.crypto"
     * @param ticker coin ticker such as usdt, ftm and etc.
     * @param chain chain to look for, usually means blockcahin ( erc20,  omni, tron, etc. )
     * @return address for specific chain
     * @throws NamingServiceException when domain has no record
     */
    String getMultiChainAddress(String domain, String ticker, String chain) throws NamingServiceException;

    /**
     * Produces a getNamehash for a specific domain
     *
     * @param domain domain name such as "brad.crypto"
     * @return getNamehash of a domain for a specific NamingService
     * @throws NamingServiceException if tld of the domain is not recognized
     * @see <a href="https://docs.ens.domains/contract-api-reference/name-processing">
     * https://docs.ens.domains/contract-api-reference/name-processing </a>
     */
    String getNamehash(String domain) throws NamingServiceException;

    /**
     * Resolves domain for an ipfs hash
     *
     * @param domain domain name such as "brad.crypto"
     * @return ipfs hash used to redirect people to ipfs content
     * @throws NamingServiceException if no record is present
     * @see <a href="https://docs.ipfs.io/concepts/what-is-ipfs">
     * https://docs.ipfs.io/concepts/what-is-ipfs </a>
     */
    String getIpfsHash(String domain) throws NamingServiceException;

    /**
     * Resolves an getEmail address from a domain
     *
     * @param domain domain name such as "brad.crypto"
     * @return getEmail address
     * @throws NamingServiceException if no getEmail is present
     */
    String getEmail(String domain) throws NamingServiceException;

    /**
     * Resolves getOwner address from a domain
     *
     * @param domain domain name such as "brad.crypto"
     * @return Ethereum address of a domain's getOwner
     * @throws NamingServiceException if getOwner is not present
     */
    String getOwner(String domain) throws NamingServiceException;
    
    /**
     * Resolves owner addresses for many domains
     * @param domains string list of domain names you want to resolve
     * @return List<String> of owner addreses.
     * @throws NamingServiceException
     */
    List<String> getBatchOwners(List<String> domains) throws NamingServiceException;


    /**
     * Resolves dns records from a domain
     * 
     * @param domain domain name such as "brad.crypto"
     * @param types List of DnsRecordsType to resolve for
     * @return List of DnsRecord
     * @throws NamingServiceException
     */
    List<DnsRecord> getDns(String domain, List<DnsRecordsType> types) throws NamingServiceException, DnsException;
    
    /**
     * Retrieves the tokenURI from the registry smart contract.
     *
     * @param domain domain name such as "brad.crypto"
     * @return the ERC721Metadata#tokenURI contract method result
     * @throws NamingServiceException if domain is not found or invalid
     */
    String getTokenURI(String domain) throws NamingServiceException;

    /**
     * Retrieves the data from the endpoint provided by tokenURI from the registry smart contract.
     *
     * @param domain domain name such as "brad.crypto"
     * @return the JSON response of the token URI endpoint
     * @throws NamingServiceException if domain is not found or invalid
     */
    TokenUriMetadata getTokenURIMetadata(String domain) throws NamingServiceException;

    /**
     * Retrieves the domain name from token metadata that is provided by tokenURI from the registry smart contract.
     * The function also checks if the returned domain matches the hash parameter.
     *
     * @param hash domain name hash
     * @param service nameservice which is used for lookup
     * @return the JSON response of the token URI endpoint
     * @throws NamingServiceException if domain is not found or invalid
     */
    String unhash(String hash, NamingServiceType service) throws NamingServiceException;
}

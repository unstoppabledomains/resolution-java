package com.unstoppabledomains.resolution;

import com.unstoppabledomains.exceptions.NamingServiceException;

public interface DomainResolution {

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
     * Resolves domain for a specific ticker address
     *
     * @param domain domain name such as "brad.crypto"
     * @param ticker coin ticker such as ETH
     * @return address in hex-string format
     * @throws NamingServiceException if tld is not recognized or there record for such currency is not presented
     * @deprecated this method is deprecated since 1.6.0.
     * <p> Use {@link DomainResolution#getAddress(String, String)} instead.
     */
    @Deprecated
    String addr(String domain, String ticker) throws NamingServiceException;

    /**
     * Produces a getNamehash for a specific domain
     *
     * @param domain domain name such as "brad.crypto"
     * @return getNamehash of a domain for a specific NamingService
     * @throws NamingServiceException if tld of the domain is not recognized
     * @see <a href="https://docs.ens.domains/contract-api-reference/name-processing">
     * https://docs.ens.domains/contract-api-reference/name-processing </a>
     * @deprecated this method is deprecated since 1.6.0.
     * <p> Use {@link DomainResolution#getNamehash(String)} instead.
     */
    @Deprecated
    String namehash(String domain) throws NamingServiceException;

    /**
     * Resolves domain for an ipfs hash
     *
     * @param domain domain name such as "brad.crypto"
     * @return ipfs hash used to redirect people to ipfs content
     * @throws NamingServiceException if no record is present
     * @see <a href="https://docs.ipfs.io/concepts/what-is-ipfs">
     * https://docs.ipfs.io/concepts/what-is-ipfs </a>
     * @deprecated this method is deprecated since 1.6.0.
     * <p> Use {@link DomainResolution#getIpfsHash(String)} instead.
     */
    @Deprecated
    String ipfsHash(String domain) throws NamingServiceException;

    /**
     * Resolves an getEmail address from a domain
     *
     * @param domain domain name such as "brad.crypto"
     * @return getEmail address
     * @throws NamingServiceException if no getEmail is present
     * @deprecated this method is deprecated since 1.6.0.
     * <p> Use {@link DomainResolution#getEmail(String)} instead.
     */
    @Deprecated
    String email(String domain) throws NamingServiceException;

    /**
     * Resolves getOwner address from a domain
     *
     * @param domain domain name such as "brad.crypto"
     * @return Ethereum address of a domain's getOwner
     * @throws NamingServiceException if getOwner is not present
     * @deprecated this method is deprecated since 1.6.0.
     * <p> Use {@link DomainResolution#getOwner(String)} instead.
     */
    @Deprecated
    String owner(String domain) throws NamingServiceException;
}

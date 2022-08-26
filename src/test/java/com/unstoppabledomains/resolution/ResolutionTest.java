package com.unstoppabledomains.resolution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.unstoppabledomains.TestUtils;
import com.unstoppabledomains.config.network.NetworkConfigLoader;
import com.unstoppabledomains.config.network.model.Location;
import com.unstoppabledomains.config.network.model.Network;
import com.unstoppabledomains.exceptions.ns.NSExceptionCode;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.DefaultProvider;
import com.unstoppabledomains.resolution.contracts.interfaces.IProvider;
import com.unstoppabledomains.resolution.dns.DnsRecord;
import com.unstoppabledomains.resolution.dns.DnsRecordsType;
import com.unstoppabledomains.resolution.dns.DnsUtils;
import com.unstoppabledomains.resolution.naming.service.NamingServiceType;
import com.unstoppabledomains.resolution.naming.service.uns.UNSLocation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ResolutionTest {
    private static DomainResolution resolution;

    @BeforeAll
    public static void init() {
        resolution = Resolution.builder()
        .znsChainId(Network.ZIL_TESTNET)
        .znsProviderUrl(TestUtils.TESTING_ZNS_PROVIDER_URL)
        .unsProviderUrl(UNSLocation.Layer1, TestUtils.TESTING_UNS_PROVIDER_URL)
        .unsProviderUrl(UNSLocation.Layer2, TestUtils.TESTING_UNS_L2_PROVIDER_URL)
        .znsContractAddress("0xB925adD1d5EaF13f40efD43451bF97A22aB3d727")
        .unsContractAddress(UNSLocation.Layer1, NetworkConfigLoader.getContractAddress(Network.GOERLI, "ProxyReader"))
        .unsContractAddress(UNSLocation.Layer2, NetworkConfigLoader.getContractAddress(Network.MUMBAI_TESTNET, "ProxyReader"))
        .build();
    }

    @Test
    public void resolveTestnetDomain() throws Exception {
        DomainResolution goerliResolution = Resolution.builder()
            .znsChainId(Network.ZIL_TESTNET)
            .znsProviderUrl(TestUtils.TESTING_ZNS_PROVIDER_URL)
            .unsProviderUrl(UNSLocation.Layer1, TestUtils.TESTING_UNS_PROVIDER_URL)
            .unsProviderUrl(UNSLocation.Layer2, TestUtils.TESTING_UNS_L2_PROVIDER_URL)
            .znsContractAddress("0xB925adD1d5EaF13f40efD43451bF97A22aB3d727")
            .unsContractAddress(UNSLocation.Layer1, NetworkConfigLoader.getContractAddress(Network.GOERLI, "ProxyReader"))
            .unsContractAddress(UNSLocation.Layer2, NetworkConfigLoader.getContractAddress(Network.MUMBAI_TESTNET, "ProxyReader"))
            .build();
        String ethAddress = goerliResolution.getAddress("cryptoalpaca9798.blockchain", "ETH");
        assertEquals("0x499dD6D875787869670900a2130223D85d4F6Aa7", ethAddress);
    }

    @Test
    public void testDefaultNetworks() throws Exception {
        DomainResolution defaultSettings = new Resolution();
        Network defaultUnsChainId = defaultSettings.getNetwork(NamingServiceType.UNS);
        Network defaultZnsChainId = defaultSettings.getNetwork(NamingServiceType.ZNS);
        assertEquals(Network.MAINNET, defaultUnsChainId);
        assertEquals(Network.MAINNET, defaultZnsChainId);
    }

    @Test
    public void shouldResolveFromResolutionCreatedByBuilder() throws Exception {
        DomainResolution resolutionFromBuilder = Resolution.builder()
        .unsChainId(UNSLocation.Layer1, Network.GOERLI)
        .znsChainId(Network.ZIL_TESTNET)
        .unsProviderUrl(UNSLocation.Layer1, TestUtils.TESTING_UNS_PROVIDER_URL)
        .unsProviderUrl(UNSLocation.Layer2, TestUtils.TESTING_UNS_L2_PROVIDER_URL)
        .znsProviderUrl(TestUtils.TESTING_ZNS_PROVIDER_URL)
        .znsContractAddress("0xB925adD1d5EaF13f40efD43451bF97A22aB3d727")
        .unsContractAddress(UNSLocation.Layer1, NetworkConfigLoader.getContractAddress(Network.GOERLI, "ProxyReader"))
        .unsContractAddress(UNSLocation.Layer2, NetworkConfigLoader.getContractAddress(Network.MUMBAI_TESTNET, "ProxyReader"))
        .build();

        assertEquals("0x499dd6d875787869670900a2130223d85d4f6aa7", resolutionFromBuilder.getOwner("cryptoalpaca9798.blockchain"));
        assertEquals("0x003e3cdfeceae96efe007f8196a1b1b1df547eee", resolutionFromBuilder.getOwner("testing.zil"));
        assertEquals("0x499dd6d875787869670900a2130223d85d4f6aa7", resolutionFromBuilder.getOwner("udtestdev-test-l2-domain-784391.wallet"));
    }

    @Test
    public void isSupported() throws NamingServiceException{
        boolean isValid = resolution.isSupported("example.test");
        assertFalse(isValid);

        isValid = resolution.isSupported("example.tqwdest");
        assertFalse(isValid);

        isValid = resolution.isSupported("example.qwdqwdq.wd.tqwdest");
        assertFalse(isValid);

        isValid = resolution.isSupported("udtestdev-my-new-tls.wallet");
        assertTrue(isValid);

        isValid = resolution.isSupported("example.crypto");
        assertTrue(isValid);

        isValid = resolution.isSupported("example.coin");
        assertTrue(isValid);

        isValid = resolution.isSupported("example.wallet");
        assertTrue(isValid);

        isValid = resolution.isSupported("example.bitcoin");
        assertTrue(isValid);

        isValid = resolution.isSupported("example.x");
        assertTrue(isValid);

        isValid = resolution.isSupported("example.888");
        assertTrue(isValid);

        isValid = resolution.isSupported("example.nft");
        assertTrue(isValid);

        isValid = resolution.isSupported("example.dao");
        assertTrue(isValid);

        isValid = resolution.isSupported("example.blockchain");
        assertTrue(isValid);
    }

    @Test
    public void namehash() throws NamingServiceException {
        String hash = resolution.getNamehash("crypto", NamingServiceType.UNS);
        assertEquals("0x0f4a10a4f46c288cea365fcf45cccf0e9d901b945b9829ccdb54c10dc3cb7a6f", hash);
        hash = resolution.getNamehash("brad.crypto", NamingServiceType.UNS);
        assertEquals("0x756e4e998dbffd803c21d23b06cd855cdc7a4b57706c95964a37e24b47c10fc9", hash);
        hash = resolution.getNamehash("    manyspace.crypto     ", NamingServiceType.UNS);
        assertEquals("0x09d8df1b31fdca2df375ae7f345a001b498733fce6f476eaaac20c9c9eeb639c", hash);

        hash = resolution.getNamehash("wallet", NamingServiceType.UNS);
        assertEquals("0x1e3f482b3363eb4710dae2cb2183128e272eafbe137f686851c1caea32502230", hash);
        hash = resolution.getNamehash("udtestdev-my-new-tls.wallet", NamingServiceType.UNS);
        assertEquals("0x1586d090e1b5781399f988e4b4f5639f4c2775ef5ec093d1279bb95b9bceb1a0", hash);

        hash = resolution.getNamehash("zil", NamingServiceType.ZNS);
        assertEquals("0x9915d0456b878862e822e2361da37232f626a2e47505c8795134a95d36138ed3", hash);
        hash = resolution.getNamehash("testing.zil", NamingServiceType.ZNS);
        assertEquals("0xee0e6cb578ffb17b0f374b11324240aa9498da475879d4459d13bc387cdbe90b", hash);
    }

    @Test
    public void getRecord() throws Exception {
        String recordValue = resolution.getRecord("reseller-test-udtesting-459239285.crypto", "crypto.ETH.address");
        assertEquals("0x084Ac37CDEfE1d3b68a63c08B203EFc3ccAB9742", recordValue);

        recordValue = resolution.getRecord("cryptoalpaca123.nft", "crypto.BTC.address");
        assertEquals("bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh", recordValue);

        recordValue = resolution.getRecord("udtestdev-test-l2-domain-784391.wallet", "crypto.LINK.address");
        assertEquals("0x6A1fd9a073256f14659fe59613bbf169Ed27CdcC", recordValue);
    }

    @Test
    public void getAllRecords() throws Exception {
        Map<String,String> expected = new HashMap<String, String>() {{
            put("custom.record", "custom.value");
            put("crypto.USDT.version.EOS.address", "letsminesome");
            put("crypto.USDT.version.OMNI.address", "19o6LvAdCPkjLi83VsjrCsmvQZUirT4KXJ");
            put("crypto.USDT.version.ERC20.address", "0xe7474D07fD2FA286e7e0aa23cd107F8379085037");
            put("crypto.ETH.address", "0x8aaD44321A86b170879d7A244c1e8d360c99DdA8");
            put("ipfs.html.value", "QmdyBw5oTgCtTLQ18PbDvPL8iaLoEPhSyzD91q9XmgmAjb");
            put("dweb.ipfs.hash", "QmdyBw5oTgCtTLQ18PbDvPL8iaLoEPhSyzD91q9XmgmAjb");
            put("crypto.USDT.version.TRON.address", "TNemhXhpX7MwzZJa3oXvfCjo5pEeXrfN2h");
            put("dns.ttl", "128");
            put("dns.A.ttl", "98");
            put("dns.A", "[\"10.0.0.1\", \"10.0.0.3\"]");
            put("dns.AAAA", "[]");
            put("whois.email.value", "testing@example.com");
            put("gundb.username.value", "0x8912623832e174f2eb1f59cc3b587444d619376ad5bf10070e937e0dc22b9ffb2e3ae059e6ebf729f87746b2f71e5d88ec99c1fb3c7c49b8617e2520d474c48e1c");
        }};
        Map<String, String> result = resolution.getAllRecords("uns-devtest-265f8f.wallet");
        assertEquals(expected, result);
    }

    @Test
    public void getAllZilRecords() throws Exception {
        Map<String, String> expected = new HashMap<String, String>() {{
            put("crypto.BCH.address", "qrq4sk49ayvepqz7j7ep8x4km2qp8lauvcnzhveyu6");
            put("crypto.BTC.address", "1EVt92qQnaLDcmVFtHivRJaunG2mf2C3mB");
            put("crypto.DASH.address", "XnixreEBqFuSLnDSLNbfqMH1GsZk7cgW4j");
            put("crypto.ETH.address", "0x45b31e01AA6f42F0549aD482BE81635ED3149abb");
            put("crypto.LTC.address", "LetmswTW3b7dgJ46mXuiXMUY17XbK29UmL");
            put("crypto.USDT.version.ERC20.address", "0x8aaD44321A86b170879d7A244c1e8d360c99DdA8");
            put("crypto.XMR.address", "447d7TVFkoQ57k3jm3wGKoEAkfEym59mK96Xw5yWamDNFGaLKW5wL2qK5RMTDKGSvYfQYVN7dLSrLdkwtKH3hwbSCQCu26d");
            put("crypto.ZEC.address", "t1h7ttmQvWCSH1wfrcmvT4mZJfGw2DgCSqV");
            put("crypto.ZIL.address", "zil1yu5u4hegy9v3xgluweg4en54zm8f8auwxu0xxj");
            put("ipfs.html.value", "QmVaAtQbi3EtsfpKoLzALm6vXphdi2KjMgxEDKeGg6wHuK");
            put("ipfs.redirect_domain.value", "www.unstoppabledomains.com");
            put("whois.email.value", "derainberk@gmail.com");
        }};
        Map<String, String> result = resolution.getAllRecords("testing.zil");
        assertEquals(expected, result);   
    }

    @Test
    public void getAllZilOnUNSRecords() throws Exception {
        Map<String, String> expected = new HashMap<String, String>() {{
            put("crypto.ETH.address", "0x45b31e01AA6f42F0549aD482BE81635ED3149abb");
            put("crypto.USDT.version.ERC20.address", "0x8aaD44321A86b170879d7A244c1e8d360c99DdA8");
            put("ipfs.html.value", "QmVaAtQbi3EtsfpKoLzALm6vXphdi2KjMgxEDKeGg6wHuK");
            put("whois.email.value", "derainberk@gmail.com");
        }};
        Map<String, String> result = resolution.getAllRecords("uns-devtest-testdomain303030.zil");
        assertEquals(expected, result);   
    }

    @Test
    public void getAllRecordsAllFails() throws Exception {
        TestUtils.expectError(() -> resolution.getAllRecords("unregistered.zil"), NSExceptionCode.UnregisteredDomain);
        TestUtils.expectError(() -> resolution.getAllRecords("myjohnny.wallet"), NSExceptionCode.UnregisteredDomain);
        TestUtils.expectError(() -> resolution.getAllRecords("unregistered.crypto"), NSExceptionCode.UnregisteredDomain);
        TestUtils.expectError(() -> resolution.getAllRecords("unregistered.nft"), NSExceptionCode.UnregisteredDomain);
    }

    @Test
    public void getRecords() throws Exception {
        Map<String, String> given = new HashMap<String, String>() {{
            put("crypto.ETH.address", "0x8aaD44321A86b170879d7A244c1e8d360c99DdA8");
            put("crypto.BTC.address", "");
            put("ipfs.html.value", "QmdyBw5oTgCtTLQ18PbDvPL8iaLoEPhSyzD91q9XmgmAjb");
        }};
        List<String> recordsKeys = new ArrayList<String>(given.keySet());
        Map<String, String> result = resolution.getRecords("uns-devtest-265f8f.wallet", recordsKeys);
        assertEquals(result.size(), recordsKeys.size());
        for (Map.Entry<String, String> entry: given.entrySet()) {
            String key = entry.getKey();
            assertEquals(result.get(key), entry.getValue());
        }
    }

    @Test
    public void getZilliqaRecords() throws Exception {
        Map<String, String> given = new HashMap<String, String>() {{
            put("crypto.ETH.address", "0x45b31e01AA6f42F0549aD482BE81635ED3149abb");
            put("crypto.LTC.address", "LetmswTW3b7dgJ46mXuiXMUY17XbK29UmL");
            put("ipfs.html.value", "QmVaAtQbi3EtsfpKoLzALm6vXphdi2KjMgxEDKeGg6wHuK");
            put("whois.email.value", "derainberk@gmail.com");
            put("unknown.record", "");
        }};
        Map<String, String> result = resolution.getRecords("testing.zil", new ArrayList<>(given.keySet()));
        assertEquals(result.size(), given.keySet().size());
        for (Map.Entry<String, String> entry: given.entrySet()) {
            String key = entry.getKey();
            assertEquals(result.get(key), entry.getValue());
        }
    }

    @Test
    public void getZilDomainRecordsOnUNS() throws Exception {
        Map<String, String> given = new HashMap<String, String>() {{
            put("crypto.ETH.address", "0x45b31e01AA6f42F0549aD482BE81635ED3149abb");
            put("ipfs.html.value", "QmVaAtQbi3EtsfpKoLzALm6vXphdi2KjMgxEDKeGg6wHuK");
            put("whois.email.value", "derainberk@gmail.com");
            put("unknown.record", "");
        }};
        Map<String, String> result = resolution.getRecords("uns-devtest-testdomain303030.zil", new ArrayList<>(given.keySet()));
        assertEquals(result.size(), given.keySet().size());
        for (Map.Entry<String, String> entry: given.entrySet()) {
            String key = entry.getKey();
            assertEquals(result.get(key), entry.getValue());
        }
    }

    @Test
    public void UnregisteredGetRecords() throws Exception {
        List<String> records = Arrays.asList("crypto.ETH.address", "ipfs.html.value");
        TestUtils.expectError(() -> resolution.getRecords("unregistered.crypto", records), NSExceptionCode.UnregisteredDomain);
    }

    @Test
    public void noRecord() throws Exception {
        TestUtils.expectError(() -> resolution.getRecord("unregistered.crypto", "crypto.ETH.address"), NSExceptionCode.UnregisteredDomain);
        TestUtils.expectError(() -> resolution.getRecord("cryptoalpaca9798.blockchain", "invalid.record.value"), NSExceptionCode.RecordNotFound);
    }

    @Test
    public void getAddress() throws Exception {
        String addr = resolution.getAddress("cryptoalpaca9798.blockchain", "eth");
        assertEquals("0x499dD6D875787869670900a2130223D85d4F6Aa7", addr, "cryptoalpaca9798.blockchain --> eth");

        addr = resolution.getAddress("testing.zil", "zil");
        assertEquals("zil1yu5u4hegy9v3xgluweg4en54zm8f8auwxu0xxj", addr, "testing.zil --> zil");

        addr = resolution.getAddress("udtestdev-test-l2-domain-784391.wallet", "link");
        assertEquals("0x6A1fd9a073256f14659fe59613bbf169Ed27CdcC", addr, "udtestdev-test-l2-domain-784391.wallet --> link");
    }

    @Test
    public void NormalizeDomainTest() throws Exception {
        String addr = resolution.getAddress("   uns-devtest-265f8f.wallet    ", "ETH");
        assertEquals("0x8aaD44321A86b170879d7A244c1e8d360c99DdA8", addr, "|   testing.crypto    | --> eth");

        String uppercaseDomainTestResult = resolution.getAddress("  UNS-DEVTEST-265f8f.WALLET", "ETH");
        assertEquals("0x8aaD44321A86b170879d7A244c1e8d360c99DdA8", uppercaseDomainTestResult, "|  TESTING.CRYPTO| --> eth");
    }

    @Test
    public void wrongDomainAddr() throws Exception {
        TestUtils.expectError(() -> resolution.getAddress("unregistered.crypto", "eth"), NSExceptionCode.UnregisteredDomain);
        TestUtils.expectError(() -> resolution.getAddress("unregistered.wallet", "eth"), NSExceptionCode.UnregisteredDomain);
        TestUtils.expectError(() -> resolution.getAddress("unregistered.blockchain", "eth"), NSExceptionCode.UnregisteredDomain);
        TestUtils.expectError(() -> resolution.getAddress("unregistered26572654326523456.zil", "eth"), NSExceptionCode.UnregisteredDomain);
    }

    @Test
    public void UnknownCurrency() throws Exception {
        TestUtils.expectError(() -> resolution.getAddress("uns-devtest-265f8f.wallet", "unknown"), NSExceptionCode.UnknownCurrency);
        TestUtils.expectError(() -> resolution.getAddress("reseller-test-udtesting-459239285.crypto", "unknown"), NSExceptionCode.UnknownCurrency);
        TestUtils.expectError(() -> resolution.getAddress("testing.zil", "unknown"), NSExceptionCode.UnknownCurrency);
        TestUtils.expectError(() -> resolution.getAddress("reseller-test-udtesting-459239285.crypto", "dodge"), NSExceptionCode.UnknownCurrency);
        TestUtils.expectError(() -> resolution.getAddress("testing.zil", "dodge"), NSExceptionCode.UnknownCurrency);
    }

    @Test
    public void getIpfsHash() throws NamingServiceException {
        String ipfs = resolution.getIpfsHash("uns-devtest-265f8f.wallet");
        assertEquals("QmdyBw5oTgCtTLQ18PbDvPL8iaLoEPhSyzD91q9XmgmAjb", ipfs);

        ipfs = resolution.getIpfsHash("testing.zil");
        assertEquals("QmVaAtQbi3EtsfpKoLzALm6vXphdi2KjMgxEDKeGg6wHuK", ipfs);
        
        ipfs = resolution.getIpfsHash(" uns-dEVteSt-265f8f.wAlLet ");
        assertEquals("QmdyBw5oTgCtTLQ18PbDvPL8iaLoEPhSyzD91q9XmgmAjb", ipfs);

        ipfs = resolution.getIpfsHash("udtestdev-test-l2-domain-784391.wallet");
        assertEquals("QmfRXG3CcM1eWiCUA89uzimCvQUnw4HzTKLo6hRZ47PYsN", ipfs);
    }

    @Test
    public void getEmailTest() throws NamingServiceException {
        String email = resolution.getEmail("uns-devtest-265f8f.wallet");
        assertEquals("testing@example.com", email);
        
        String nonNormalizedTest = resolution.getEmail("    uns-DEVtesT-265f8f.WALLet     ");
        assertEquals("testing@example.com", nonNormalizedTest);

        email = resolution.getEmail("udtestdev-test-l2-domain-784391.wallet");
        assertEquals("l2email@l2mail.mail", email);
    }

    @Test
    public void getOwnerTest() throws NamingServiceException {
        String owner = resolution.getOwner("reseller-test-udtesting-459239285.crypto"); // cns
        assertEquals("0xe586d5bf4d7779498648df67b73c88a712e4359d", owner);

        owner = resolution.getOwner("  uns-DEVtest-265f8f.WALLET    "); // uns
        assertEquals("0xd92d2a749424a5181ad7d45f786a9ffe46c10a7c", owner);

        owner = resolution.getOwner("testing.zil"); // zil
        assertEquals("0x003e3cdfeceae96efe007f8196a1b1b1df547eee", owner);

        owner = resolution.getOwner("udtestdev-test-l2-domain-784391.wallet"); // l2
        assertEquals("0x499dd6d875787869670900a2130223d85d4f6aa7", owner);
    }

    @Test
    public void getBatchOwnersTest() throws NamingServiceException {
        Map<String,String> domainForTest = new HashMap<String, String>() {{
            put("reseller-test-udtesting-459239285.crypto", "0xe586d5bf4d7779498648df67b73c88a712e4359d");
            put("unregistered.crypto", null);
            put("uns-devtest-265f8f.wallet", "0xd92d2a749424a5181ad7d45f786a9ffe46c10a7c");
            put("udtestdev-test-l2-domain-784391.wallet", "0x499dd6d875787869670900a2130223d85d4f6aa7");
        }};
        List<String> domains = domainForTest.keySet().stream().collect(Collectors.toList());
        Map<String, String> owners = resolution.getBatchOwners(domains);
        assertEquals(true, domainForTest.equals(owners));
    }
    
    @Test
    public void getBatchOwnersMixed() throws Exception {
        Map<String,String> domainForTest = new HashMap<String, String>() {{
            put("reseller-test-udtesting-459239285.crypto", "0xe586d5bf4d7779498648df67b73c88a712e4359d");
            put("unregistered.crypto", null);
            put("testing.zil", "0x003e3cdfeceae96efe007f8196a1b1b1df547eee");
            put("uns-devtest-testdomain303030.zil", "0x499dd6d875787869670900a2130223d85d4f6aa7");
        }};
        List<String> domains = domainForTest.keySet().stream().collect(Collectors.toList());
        Map<String, String> owners = resolution.getBatchOwners(domains);
        assertEquals(true, domainForTest.equals(owners));
    }

    @Test
    public void getOwnerFailTest() throws Exception {
        TestUtils.expectError(() -> resolution.getOwner("unregistered.crypto"), NSExceptionCode.UnregisteredDomain);
        TestUtils.expectError(() -> resolution.getOwner("unregistered.wallet"), NSExceptionCode.UnregisteredDomain);
    }

    @Test
    public void noIpfsHash() throws Exception {
        TestUtils.expectError(() -> resolution.getIpfsHash("unregstered.crypto"), NSExceptionCode.UnregisteredDomain);
        TestUtils.expectError(() -> resolution.getIpfsHash("reseller-test-udtesting-459239285.crypto"), NSExceptionCode.RecordNotFound);
        TestUtils.expectError(() -> resolution.getIpfsHash("udtestdev-429034.crypto"), NSExceptionCode.RecordNotFound);
        TestUtils.expectError(() -> resolution.getIpfsHash("udtestdev-test-l2-domain-empty.wallet"), NSExceptionCode.RecordNotFound);
    }

    public void invalidDomains() throws Exception {
        String[] invalidDomains = { "some#.crypto", "special!.zil", "character?.eth", "notAllowed%.nft"};
        for (int i = 0; i < invalidDomains.length; i++) {
            final int index = i;
            TestUtils.expectError(() -> resolution.getOwner(invalidDomains[index]), NSExceptionCode.InvalidDomain);
        }
    }

    @Test
    public void noEmailRecord() throws Exception {
        TestUtils.expectError(() -> resolution.getEmail("reseller-test-udtesting-459239285.crypto"), NSExceptionCode.RecordNotFound);
        TestUtils.expectError(() -> resolution.getEmail("udtestdev-429034.crypto"), NSExceptionCode.RecordNotFound);
        TestUtils.expectError(() -> resolution.getEmail("udtestdev-test-l2-domain-empty.wallet"), NSExceptionCode.RecordNotFound);
    }

    @Test
    public void dnsRecords() throws Exception {
        String domain = "uns-devtest-265f8f.wallet";
        List<DnsRecordsType> types = Arrays.asList(DnsRecordsType.A, DnsRecordsType.AAAA);
        List<DnsRecord> dnsRecords = resolution.getDns(domain, types);
        assertEquals(2, dnsRecords.size());
        List<DnsRecord> correctResult = Arrays.asList(new DnsRecord(DnsRecordsType.A, 98, "10.0.0.1"), new DnsRecord(DnsRecordsType.A, 98, "10.0.0.3"));
        assertEquals(dnsRecords, correctResult);
    }

    @Test
    public void normalizeDomainDnsRecords() throws Exception {
        String domain = "    uns-DEVtest-265f8F.waLLET    ";
        List<DnsRecordsType> types = Arrays.asList(DnsRecordsType.A, DnsRecordsType.AAAA);
        List<DnsRecord> dnsRecords = resolution.getDns(domain, types);
        assertEquals(2, dnsRecords.size());
        List<DnsRecord> correctResult = Arrays.asList(new DnsRecord(DnsRecordsType.A, 98, "10.0.0.1"), new DnsRecord(DnsRecordsType.A, 98, "10.0.0.3"));
        assertEquals(dnsRecords, correctResult);
    }

    @Test
    public void dnsRecordsToMap() throws Exception {
        DnsUtils utils = new DnsUtils();
        List<DnsRecord> dnsRecords = Arrays.asList(new DnsRecord(DnsRecordsType.A, 98, "10.0.0.1"), new DnsRecord(DnsRecordsType.A, 98, "10.0.0.3"));
        Map<String, String> map = utils.toMap(dnsRecords);
        List<DnsRecord> revert = utils.toList(map);
        assertEquals(dnsRecords, revert);
    }

    @Test
    public void passingCustomProvider() throws Exception {
        IOException cause = new IOException("for testing purposes");
        IProvider provider = new IProvider() {

            @Override
            public JsonObject request(String url, JsonObject body) throws IOException {
                throw cause;
            }

        };
        Resolution resolutionWithProvider = Resolution.builder().provider(provider).build();
        TestUtils.expectError(
            () -> resolutionWithProvider.getAddress("udtestdev-my-new-tls.wallet", "eth"),
            NSExceptionCode.BlockchainIsDown,
            cause
        );
    }

    @Test
    public void passingCorrectProvider() throws Exception {
        IProvider provider = new IProvider() {
            @Override
            public JsonObject request(String url, JsonObject body) throws IOException {
                if (body.has("params")) {
                    JsonArray params = body.getAsJsonArray("params");
                    JsonObject object = params.get(0).getAsJsonObject();
                    if (
                        object.has("data") &&
                        object.get("data").getAsString()
                            .equals("0x91015f6b0000000000000000000000000000000000000000000000000000000000000040756e4e998dbffd803c21d23b06cd855cdc7a4b57706c95964a37e24b47c10fc900000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000001263727970746f2e4554482e616464726573730000000000000000000000000000")
                    ) {
                        JsonObject answer = new JsonObject();
                        answer.addProperty("jsonrpc", "2.0");
                        answer.addProperty("id", "1");
                        answer.addProperty("method", "eth_call");
                        answer.addProperty("result", "0x000000000000000000000000b66dce2da6afaaa98f2013446dbcb0f4b0ab28420000000000000000000000008aad44321a86b170879d7a244c1e8d360c99dda8000000000000000000000000000000000000000000000000000000000000006000000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000002a30783861614434343332314138366231373038373964374132343463316538643336306339394464413800000000000000000000000000000000000000000000");
                        return answer;
                    }
                }
                throw new IOException("body has incorrect data in the test");
            }
        };
        Resolution resolutionWithProvider = Resolution.builder().provider(provider).build();
        String ethAddress = resolutionWithProvider.getAddress("brad.crypto", "eth");
        assertEquals("0x8aaD44321A86b170879d7A244c1e8d360c99DdA8", ethAddress);
    }

    @Test
    public void defaultProvider() throws Exception {
        IProvider provider = new DefaultProvider();
        JsonObject result = provider.request("https://httpbin.org/response-headers?freeform=", new JsonObject());
        JsonObject correctAnswer = JsonParser.parseString("{\"Content-Length\":\"87\",\"Content-Type\":\"application/json\",\"freeform\":\"\"}").getAsJsonObject();
        assertEquals(correctAnswer, result);
    }

    @Test
    public void buildingDefaultProvider() throws Exception {
        DefaultProvider provider = DefaultProvider.cleanBuild().setHeader("custom-header", "custom-value").setHeader("new-key", "new-value");
        Map<String,String> headers = provider.getHeaders();
        assertEquals(headers.size(), 2);
        assertEquals("custom-value", headers.get("custom-header"));
        assertEquals("new-value", headers.get("new-key"));
    }

    @Test
    public void testGetMultiChainAddress() throws Exception {
        String domainWithMultiChainRecords = "uns-devtest-265f8f.wallet";
        String notNormalizedDomainWithMultiChainRecords = "   uns-DEVtest-265f8f.waLLET ";

        String erc20 = resolution.getMultiChainAddress(domainWithMultiChainRecords, "usdt", "erc20");
        assertEquals("0xe7474D07fD2FA286e7e0aa23cd107F8379085037", erc20);
        String tron = resolution.getMultiChainAddress(domainWithMultiChainRecords, "usdt", "tron");
        assertEquals("TNemhXhpX7MwzZJa3oXvfCjo5pEeXrfN2h", tron);
        String omni = resolution.getMultiChainAddress(domainWithMultiChainRecords, "usdt", "omni");
        assertEquals("19o6LvAdCPkjLi83VsjrCsmvQZUirT4KXJ", omni);
        String eos = resolution.getMultiChainAddress(notNormalizedDomainWithMultiChainRecords, "usdt", "eos");
        assertEquals("letsminesome", eos);
    }

    @Test
    public void testTokenURIUNS() throws Exception {
        String tokenUri = resolution.getTokenURI("uns-devtest-ngin.blockchain");
        assertEquals("https://metadata.staging.unstoppabledomains.com/metadata/38341110048240109319578877561688040885276568114621087858154157305222841866728", tokenUri);
    }

    @Test
    public void testTokenURIUnregistered() throws Exception {
        TestUtils.expectError(() -> resolution.getTokenURI("fake-domain-that-does-not-exist-949499.crypto"), NSExceptionCode.UnregisteredDomain);
    }

    @Test
    public void testTokenURIZNS() throws Exception {
        String tokenUri = resolution.getTokenURI("uns-devtest-testdomain303030.zil");
        assertEquals("https://metadata.staging.unstoppabledomains.com/metadata/95877446756833684138630559105836459661025775644235428329510679487153930510531", tokenUri);
    }

    @Test
    public void testTokenURIMetadata() throws Exception {
        String testDomain = "uns-devtest-265f8f.wallet";

        TokenUriMetadata metadata = resolution.getTokenURIMetadata(testDomain);
        assertNotNull(metadata);
        assertEquals(testDomain, metadata.getName());
        assertEquals(5, metadata.getAttributes().size());
        Map<String, String> expectedRecords = new HashMap<String, String>() {{
            put("dns.A", "[\"10.0.0.1\", \"10.0.0.3\"]");
            put("dns.ttl", "128");
            put("dns.AAAA", "[]");
            put("dns.A.ttl", "98");
            put("custom.record", "custom.value");
            put("dweb.ipfs.hash", "QmdyBw5oTgCtTLQ18PbDvPL8iaLoEPhSyzD91q9XmgmAjb");
            put("ipfs.html.value", "QmdyBw5oTgCtTLQ18PbDvPL8iaLoEPhSyzD91q9XmgmAjb");
            put("crypto.ETH.address", "0x8aaD44321A86b170879d7A244c1e8d360c99DdA8");
            put("gundb.username.value", "0x8912623832e174f2eb1f59cc3b587444d619376ad5bf10070e937e0dc22b9ffb2e3ae059e6ebf729f87746b2f71e5d88ec99c1fb3c7c49b8617e2520d474c48e1c");
            put("crypto.USDT.version.EOS.address", "letsminesome");
            put("crypto.USDT.version.OMNI.address", "19o6LvAdCPkjLi83VsjrCsmvQZUirT4KXJ");
            put("crypto.USDT.version.TRON.address", "TNemhXhpX7MwzZJa3oXvfCjo5pEeXrfN2h");
            put("crypto.USDT.version.ERC20.address", "0xe7474D07fD2FA286e7e0aa23cd107F8379085037");
            put("whois.email.value", "testing@example.com");
            put("custom.record", "custom.value");
        }};
        Map<String, String> recordsFromProperties = metadata.getProperties().getRecords();
        assertEquals(expectedRecords, recordsFromProperties);
    }

    @Test
    public void testUnhashCNS() throws Exception {
        String testHash = "0x4fe5c8229795fec5cab66bf7e2c301f2f54cada203afb9b7b8b1d01213ede26d";
        String tokenName = resolution.unhash(testHash, NamingServiceType.UNS);
        assertEquals("reseller-test-udtesting-459239285.crypto", tokenName);
    }

    @Test
    public void testUnhashUnregistered() throws Exception {
        TestUtils.expectError(() -> resolution.unhash("0x0a1e7db0adb5b2b4d7de50f8091def73070759aec2a463006cbcd31932cca14b", NamingServiceType.UNS), NSExceptionCode.UnregisteredDomain);
    }

    @Test
    public void testUnhashUNS() throws Exception {
        String testHash = "0x0df03d18a0a02673661da22d06f43801a986840e5812989139f0f7a2c41037c2";
        String tokenName = resolution.unhash(testHash, NamingServiceType.UNS);
        assertEquals("uns-devtest-265f8f.wallet", tokenName);
    }

    @Test
    public void testUnhashZNS() throws Exception {
        String testHash = "0x5fc604da00f502da70bfbc618088c0ce468ec9d18d05540935ae4118e8f50787";
        TestUtils.expectError(() -> resolution.unhash(testHash, NamingServiceType.ZNS), NSExceptionCode.NotImplemented);
    }

    @Test
    public void testReturnsDataFromL2() throws Exception {
        String record = resolution.getRecord("udtestdev-test-l1-and-l2-ownership.wallet", "crypto.ETH.address");
        assertEquals("0x499dd6d875787869670900a2130223d85d4f6aa7", record);

        String address = resolution.getOwner("udtestdev-test-l1-and-l2-ownership.wallet");
        assertEquals("0x499dd6d875787869670900a2130223d85d4f6aa7", address);
    }

    @Test
    public void testLocationsUNS() throws Exception {
        Location uns = new Location(
            "0x801452cfac27e79a11c6b185986fde09e8637589", 
            "0x0555344a5f440bd1d8cb6b42db46c5e5d4070437", 
            Network.GOERLI,
            "ETH",
            "0xe586d5bf4d7779498648df67b73c88a712e4359d",
            TestUtils.TESTING_UNS_PROVIDER_URL);

        Location l2 = new Location(
            "0x2a93c52e7b6e7054870758e15a1446e769edfb93", 
            "0x2a93c52e7b6e7054870758e15a1446e769edfb93", 
            Network.MUMBAI_TESTNET,
            "MATIC",
            "0x499dd6d875787869670900a2130223d85d4f6aa7",
            TestUtils.TESTING_UNS_L2_PROVIDER_URL);

        Map<String, Location> locations = resolution.getLocations("reseller-test-udtesting-459239285.crypto", "udtestdev-my-new-tls.wallet", "not-registered-12345abc.crypto", "udtestdev-test-l2-domain-784391.wallet");
        assertEquals(uns, locations.get("reseller-test-udtesting-459239285.crypto"));
        assertEquals(null, locations.get("not-registered-12345abc.crypto"));
        assertEquals(l2, locations.get("udtestdev-test-l2-domain-784391.wallet"));
    }

    @Test
    public void testLocationsZNS() throws Exception {
        Location zil = new Location(
            null, 
            null, 
            Network.ZIL_TESTNET,
            "ZIL",
            "0x003e3cdfeceae96efe007f8196a1b1b1df547eee",
            TestUtils.TESTING_ZNS_PROVIDER_URL);

        Location uns = new Location(
            "0x2a93c52e7b6e7054870758e15a1446e769edfb93", 
            "0x2a93c52e7b6e7054870758e15a1446e769edfb93", 
            Network.MUMBAI_TESTNET,
            "MATIC",
            "0x499dd6d875787869670900a2130223d85d4f6aa7",
            TestUtils.TESTING_UNS_L2_PROVIDER_URL);

        Map<String, Location> locations = resolution.getLocations(
            "uns-devtest-testdomain303030.zil",
            "testing.zil"
            );
        assertEquals(uns, locations.get("uns-devtest-testdomain303030.zil"));
        assertEquals(zil, locations.get("testing.zil"));
    }

    @Test
    public void testLocationsMixed() throws Exception {
        Location uns = new Location(
            "0x801452cfac27e79a11c6b185986fde09e8637589", 
            "0x0555344a5f440bd1d8cb6b42db46c5e5d4070437", 
            Network.GOERLI,
            "ETH",
            "0xe586d5bf4d7779498648df67b73c88a712e4359d",
            TestUtils.TESTING_UNS_PROVIDER_URL);

        Location l2 = new Location(
            "0x2a93c52e7b6e7054870758e15a1446e769edfb93", 
            "0x2a93c52e7b6e7054870758e15a1446e769edfb93", 
            Network.MUMBAI_TESTNET,
            "MATIC",
            "0x499dd6d875787869670900a2130223d85d4f6aa7",
            TestUtils.TESTING_UNS_L2_PROVIDER_URL);

        Location l2zil = new Location(
            "0x2a93c52e7b6e7054870758e15a1446e769edfb93", 
            "0x2a93c52e7b6e7054870758e15a1446e769edfb93", 
            Network.MUMBAI_TESTNET,
            "MATIC",
            "0x499dd6d875787869670900a2130223d85d4f6aa7",
            TestUtils.TESTING_UNS_L2_PROVIDER_URL);

        Location zil = new Location(
            null, 
            null, 
            Network.ZIL_TESTNET,
            "ZIL",
            "0x003e3cdfeceae96efe007f8196a1b1b1df547eee",
            TestUtils.TESTING_ZNS_PROVIDER_URL);

        Map<String, Location> locations = resolution.getLocations(
            "reseller-test-udtesting-459239285.crypto", 
            "udtestdev-my-new-tls.wallet", 
            "not-registered-12345abc.crypto", 
            "udtestdev-test-l2-domain-784391.wallet",
            "uns-devtest-testdomain303030.zil",
            "testing.zil"
            );
        assertEquals(uns, locations.get("reseller-test-udtesting-459239285.crypto"));
        assertEquals(null, locations.get("not-registered-12345abc.crypto"));
        assertEquals(l2, locations.get("udtestdev-test-l2-domain-784391.wallet"));
        assertEquals(l2zil, locations.get("uns-devtest-testdomain303030.zil"));
        assertEquals(zil, locations.get("testing.zil"));
    }

    @Test
    public void testGetReverse() throws Exception {
        String addressL1 = "0xd92d2a749424a5181ad7d45f786a9ffe46c10a7c";
        String tokenName = resolution.getReverse(addressL1);
        String tokenNameL1 = resolution.getReverse(addressL1, UNSLocation.Layer2); // todo change to L1
        assertEquals("uns-devtest-265f8f.wallet", tokenName);
        assertEquals("uns-devtest-265f8f.wallet", tokenNameL1);

        String addressL2 = "0xd92d2a749424a5181ad7d45f786a9ffe46c10a7c";
        tokenName = resolution.getReverse(addressL2);
        String tokenNameL2 = resolution.getReverse(addressL2, UNSLocation.Layer2);
        assertEquals("uns-devtest-265f8f.wallet", tokenName);
        assertEquals("uns-devtest-265f8f.wallet", tokenNameL2);
    }

    @Test
    public void testGetReverseDoesntExist() throws Exception {
        String address = "0x0000000000000000000000000000000000000001";
        TestUtils.expectError(() -> resolution.getReverse(address), NSExceptionCode.ReverseResolutionNotSpecified);
    }

    @Test
    public void testGetReverseInvalid() throws Exception {
        String address = "invalid0x";
        TestUtils.expectError(() -> resolution.getReverse(address), NSExceptionCode.IncorrectAddress);
        TestUtils.expectError(() -> resolution.getReverse(address, UNSLocation.Layer1), NSExceptionCode.IncorrectAddress);
    }

    @Test
    public void testGetReverseTokenId() throws Exception {
        String addressL1 = "0xd92d2a749424a5181ad7d45f786a9ffe46c10a7c";
        String tokenName = resolution.getReverseTokenId(addressL1);
        String tokenNameL1 = resolution.getReverseTokenId(addressL1, UNSLocation.Layer2); // todo change to L1
        assertEquals("0x0df03d18a0a02673661da22d06f43801a986840e5812989139f0f7a2c41037c2", tokenName);
        assertEquals("0x0df03d18a0a02673661da22d06f43801a986840e5812989139f0f7a2c41037c2", tokenNameL1);

        String addressL2 = "0xd92d2a749424a5181ad7d45f786a9ffe46c10a7c";
        tokenName = resolution.getReverseTokenId(addressL2);
        String tokenNameL2 = resolution.getReverseTokenId(addressL2, UNSLocation.Layer2);
        assertEquals("0x0df03d18a0a02673661da22d06f43801a986840e5812989139f0f7a2c41037c2", tokenName);
        assertEquals("0x0df03d18a0a02673661da22d06f43801a986840e5812989139f0f7a2c41037c2", tokenNameL2);
    }

    @Test
    public void testGetReverseTokenIdDoesntExist() throws Exception {
        String address = "0x0000000000000000000000000000000000000001";
        TestUtils.expectError(() -> resolution.getReverseTokenId(address), NSExceptionCode.ReverseResolutionNotSpecified);
    }

    @Test
    public void testGetReverseTokenIdInvalid() throws Exception {
        String address = "invalid0x";
        TestUtils.expectError(() -> resolution.getReverseTokenId(address), NSExceptionCode.IncorrectAddress);
        TestUtils.expectError(() -> resolution.getReverseTokenId(address, UNSLocation.Layer1), NSExceptionCode.IncorrectAddress);
    }

}

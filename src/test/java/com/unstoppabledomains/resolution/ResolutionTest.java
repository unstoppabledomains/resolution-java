package com.unstoppabledomains.resolution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.unstoppabledomains.resolution.TokenUriMetadata.TokenUriMetadataAttribute;
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
        .unsContractAddress(UNSLocation.Layer1, NetworkConfigLoader.getContractAddress(Network.RINKEBY, "ProxyReader"))
        .unsContractAddress(UNSLocation.Layer2, NetworkConfigLoader.getContractAddress(Network.MUMBAI_TESTNET, "ProxyReader"))
        .build();
    }

    @Test
    public void resolveTestnetDomain() throws Exception {
        DomainResolution rinkebyResolution = Resolution.builder()
            .znsChainId(Network.ZIL_TESTNET)
            .znsProviderUrl(TestUtils.TESTING_ZNS_PROVIDER_URL)
            .ensProviderUrl("https://mainnet.infura.io/v3/e0c0cb9d12c440a29379df066de587e6")
            .unsProviderUrl(UNSLocation.Layer1, TestUtils.TESTING_UNS_PROVIDER_URL)
            .unsProviderUrl(UNSLocation.Layer2, TestUtils.TESTING_UNS_L2_PROVIDER_URL)
            .znsContractAddress("0xB925adD1d5EaF13f40efD43451bF97A22aB3d727")
            .ensContractAddress(ResolutionBuilder.ENS_DEFAULT_REGISTRY_ADDRESS)
            .unsContractAddress(UNSLocation.Layer1, NetworkConfigLoader.getContractAddress(Network.RINKEBY, "ProxyReader"))
            .unsContractAddress(UNSLocation.Layer2, NetworkConfigLoader.getContractAddress(Network.MUMBAI_TESTNET, "ProxyReader"))
            .build();
        String ethAddress = rinkebyResolution.getAddress("udtestdev-creek.crypto", "eth");
        assertEquals("0x1C8b9B78e3085866521FE206fa4c1a67F49f153A", ethAddress);
    }

    @Test
    public void testDefaultNetworks() throws Exception {
        DomainResolution defaultSettings = new Resolution();
        Network defaultUnsChainId = defaultSettings.getNetwork(NamingServiceType.UNS);
        Network defaultEnsChainId = defaultSettings.getNetwork(NamingServiceType.ENS);
        Network defaultZnsChainId = defaultSettings.getNetwork(NamingServiceType.ZNS);
        assertEquals(Network.MAINNET, defaultUnsChainId);
        assertEquals(Network.MAINNET, defaultEnsChainId);
        assertEquals(Network.MAINNET, defaultZnsChainId);
    }

    @Test
    public void shouldResolveFromResolutionCreatedByBuilder() throws Exception {
        DomainResolution resolutionFromBuilder = Resolution.builder()
        .unsChainId(UNSLocation.Layer1, Network.RINKEBY)
        .znsChainId(Network.ZIL_TESTNET)
        .unsProviderUrl(UNSLocation.Layer1, TestUtils.TESTING_UNS_PROVIDER_URL)
        .unsProviderUrl(UNSLocation.Layer2, TestUtils.TESTING_UNS_L2_PROVIDER_URL)
        .znsProviderUrl(TestUtils.TESTING_ZNS_PROVIDER_URL)
        .znsContractAddress("0xB925adD1d5EaF13f40efD43451bF97A22aB3d727")
        .unsContractAddress(UNSLocation.Layer1, NetworkConfigLoader.getContractAddress(Network.RINKEBY, "ProxyReader"))
        .unsContractAddress(UNSLocation.Layer2, NetworkConfigLoader.getContractAddress(Network.MUMBAI_TESTNET, "ProxyReader"))
        .build();

        assertEquals("0x58ca45e932a88b2e7d0130712b3aa9fb7c5781e2", resolutionFromBuilder.getOwner("testing.crypto"));
        assertEquals("0x842f373409191cff2988a6f19ab9f605308ee462", resolutionFromBuilder.getOwner("monkybrain.eth"));
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
        String hash = resolution.getNamehash("crypto");
        assertEquals("0x0f4a10a4f46c288cea365fcf45cccf0e9d901b945b9829ccdb54c10dc3cb7a6f", hash);
        hash = resolution.getNamehash("brad.crypto");
        assertEquals("0x756e4e998dbffd803c21d23b06cd855cdc7a4b57706c95964a37e24b47c10fc9", hash);
        hash = resolution.getNamehash("    manyspace.crypto     ");
        assertEquals("0x09d8df1b31fdca2df375ae7f345a001b498733fce6f476eaaac20c9c9eeb639c", hash);

        hash = resolution.getNamehash("wallet");
        assertEquals("0x1e3f482b3363eb4710dae2cb2183128e272eafbe137f686851c1caea32502230", hash);
        hash = resolution.getNamehash("udtestdev-my-new-tls.wallet");
        assertEquals("0x1586d090e1b5781399f988e4b4f5639f4c2775ef5ec093d1279bb95b9bceb1a0", hash);

        hash = resolution.getNamehash("zil");
        assertEquals("0x9915d0456b878862e822e2361da37232f626a2e47505c8795134a95d36138ed3", hash);
        hash = resolution.getNamehash("testing.zil");
        assertEquals("0xee0e6cb578ffb17b0f374b11324240aa9498da475879d4459d13bc387cdbe90b", hash);
    }

    @Test
    public void getRecord() throws Exception {
        String recordValue = resolution.getRecord("testing.crypto", "crypto.ETH.address");
        assertEquals("0x58cA45E932a88b2E7D0130712B3AA9fB7c5781e2", recordValue);

        recordValue = resolution.getRecord("udtestdev-my-new-tls.wallet", "crypto.BTC.address");
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
            put("gundb.username.value", "0x8912623832e174f2eb1f59cc3b587444d619376ad5bf10070e937e0dc22b9ffb2e3ae059e6ebf729f87746b2f71e5d88ec99c1fb3c7c49b8617e2520d474c48e1c");
        }};
        Map<String, String> result = resolution.getAllRecords("udtestdev-265f8f.crypto");
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
    public void getAllRecordsUnregistered() throws Exception {
        TestUtils.expectError(() -> resolution.getAllRecords("unregistered.zil"), NSExceptionCode.UnregisteredDomain);
        TestUtils.expectError(() -> resolution.getAllRecords("myjohnny.wallet"), NSExceptionCode.UnregisteredDomain);
    }


    @Test
    public void noRecord() throws Exception {
        TestUtils.expectError(() -> resolution.getRecord("unregistered.crypto", "crypto.ETH.address"), NSExceptionCode.UnregisteredDomain);
        TestUtils.expectError(() -> resolution.getRecord("testing.crypto", "invalid.record.value"), NSExceptionCode.RecordNotFound);
    }

    @Test
    public void getAddress() throws Exception {
        String addr = resolution.getAddress("udtestdev--awefawef.crypto", "eth");
        assertEquals("0x58cA45E932a88b2E7D0130712B3AA9fB7c5781e2", addr);

        addr = resolution.getAddress("udtestdev-my-new-tls.wallet", "eth");
        assertEquals("0x6EC0DEeD30605Bcd19342f3c30201DB263291589", addr, "udtestdev-my-new-tls.wallet --> eth");

        addr = resolution.getAddress("testing.crypto", "eth");
        assertEquals("0x58cA45E932a88b2E7D0130712B3AA9fB7c5781e2", addr, "testing.crypto --> eth");

        addr = resolution.getAddress("testing.zil", "zil");
        assertEquals("zil1yu5u4hegy9v3xgluweg4en54zm8f8auwxu0xxj", addr, "testing.zil --> zil");

        addr = resolution.getAddress("udtestdev-test-l2-domain-784391.wallet", "link");
        assertEquals("0x6A1fd9a073256f14659fe59613bbf169Ed27CdcC", addr, "udtestdev-test-l2-domain-784391.wallet --> link");
    }

    @Test
    public void NormalizeDomainTest() throws Exception {
        String addr = resolution.getAddress("   testing.crypto    ", "ETH");
        assertEquals("0x58cA45E932a88b2E7D0130712B3AA9fB7c5781e2", addr, "|   testing.crypto    | --> eth");

        String uppercaseDomainTestResult = resolution.getAddress("  TESTING.CRYPTO", "ETH");
        assertEquals("0x58cA45E932a88b2E7D0130712B3AA9fB7c5781e2", uppercaseDomainTestResult, "|  TESTING.CRYPTO| --> eth");
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
        TestUtils.expectError(() -> resolution.getAddress("udtestdev-my-new-tls.wallet", "unknown"), NSExceptionCode.UnknownCurrency);
        TestUtils.expectError(() -> resolution.getAddress("testing.crypto", "unknown"), NSExceptionCode.UnknownCurrency);
        TestUtils.expectError(() -> resolution.getAddress("testing.zil", "unknown"), NSExceptionCode.UnknownCurrency);
        TestUtils.expectError(() -> resolution.getAddress("testing.crypto", "dodge"), NSExceptionCode.UnknownCurrency);
        TestUtils.expectError(() -> resolution.getAddress("testing.zil", "dodge"), NSExceptionCode.UnknownCurrency);
    }

    @Test
    public void getIpfsHash() throws NamingServiceException {
        String ipfs = resolution.getIpfsHash("testing.crypto");
        assertEquals("QmS23QDsc3Y26rUfME32Q7jawTrCH8bTrZ7iW8EGLJYMvD", ipfs);

        ipfs = resolution.getIpfsHash("testing.zil");
        assertEquals("QmVaAtQbi3EtsfpKoLzALm6vXphdi2KjMgxEDKeGg6wHuK", ipfs);
        
        ipfs = resolution.getIpfsHash(" TESTING.crYpto ");
        assertEquals("QmS23QDsc3Y26rUfME32Q7jawTrCH8bTrZ7iW8EGLJYMvD", ipfs);

        ipfs = resolution.getIpfsHash("udtestdev-test-l2-domain-784391.wallet");
        assertEquals("QmfRXG3CcM1eWiCUA89uzimCvQUnw4HzTKLo6hRZ47PYsN", ipfs);
    }

    @Test
    public void getEmailTest() throws NamingServiceException {
        String email = resolution.getEmail("testing.crypto");
        assertEquals("testing@example.com", email);
        
        String nonNormalizedTest = resolution.getEmail("    tesTING.crypto     ");
        assertEquals("testing@example.com", nonNormalizedTest);

        email = resolution.getEmail("udtestdev-test-l2-domain-784391.wallet");
        assertEquals("l2email@l2mail.mail", email);
    }

    @Test
    public void getOwnerTest() throws NamingServiceException {
        String owner = resolution.getOwner("testing.crypto");
        assertEquals("0x58ca45e932a88b2e7d0130712b3aa9fb7c5781e2", owner);

        owner = resolution.getOwner("  UDTESTDEV-my-NEW-TLS.wallet    ");
        assertEquals("0x6ec0deed30605bcd19342f3c30201db263291589", owner);

        owner = resolution.getOwner("testing.zil");
        assertEquals("0x003e3cdfeceae96efe007f8196a1b1b1df547eee", owner);

        owner = resolution.getOwner("udtestdev-test-l2-domain-784391.wallet");
        assertEquals("0x499dd6d875787869670900a2130223d85d4f6aa7", owner);
    }

    @Test
    public void getBatchOwnersTest() throws NamingServiceException {
        Map<String,String> domainForTest = new HashMap<String, String>() {{
            put("testing.crypto", "0x58ca45e932a88b2e7d0130712b3aa9fb7c5781e2");
            put("unregistered.crypto", null);
            put("udtestdev-my-new-tls.wallet", "0x6ec0deed30605bcd19342f3c30201db263291589");
            put("brad.crypto", "0x499dd6d875787869670900a2130223d85d4f6aa7");
            put("udtestdev-test-l2-domain-784391.wallet", "0x499dd6d875787869670900a2130223d85d4f6aa7");
        }};
        List<String> domains = domainForTest.keySet().stream().collect(Collectors.toList());
        Map<String, String> owners = resolution.getBatchOwners(domains);
        assertEquals(true, domainForTest.equals(owners));
    }
    
    @Test
    public void getBatchOwnersInconsistentArray() throws Exception {
        List<String> domains = Arrays.asList("brad.crypto", "domain.eth", "something.zil");
        TestUtils.expectError(() -> resolution.getBatchOwners(domains), NSExceptionCode.InconsistentDomainArray);
    }

    @Test
    public void getOwnerFailTest() throws Exception {
        TestUtils.expectError(() -> resolution.getOwner("unregistered.crypto"), NSExceptionCode.UnregisteredDomain);
        TestUtils.expectError(() -> resolution.getOwner("unregistered.wallet"), NSExceptionCode.UnregisteredDomain);
    }

    @Test
    public void noIpfsHash() throws Exception {
        TestUtils.expectError(() -> resolution.getIpfsHash("unregstered.crypto"), NSExceptionCode.UnregisteredDomain);
        TestUtils.expectError(() -> resolution.getIpfsHash("udtestdev-my-new-tls.wallet"), NSExceptionCode.RecordNotFound);
        TestUtils.expectError(() -> resolution.getIpfsHash("udtestdev--awefawef.crypto"), NSExceptionCode.RecordNotFound);
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
        TestUtils.expectError(() -> resolution.getEmail("brad.crypto"), NSExceptionCode.RecordNotFound);
        TestUtils.expectError(() -> resolution.getEmail("udtestdev-my-new-tls.wallet"), NSExceptionCode.RecordNotFound);
        TestUtils.expectError(() -> resolution.getEmail("udtestdev-test-l2-domain-empty.wallet"), NSExceptionCode.RecordNotFound);
    }

    @Test
    public void dnsRecords() throws Exception {
        String domain = "testing.crypto";
        List<DnsRecordsType> types = Arrays.asList(DnsRecordsType.A, DnsRecordsType.AAAA);
        List<DnsRecord> dnsRecords = resolution.getDns(domain, types);
        assertEquals(2, dnsRecords.size());
        List<DnsRecord> correctResult = Arrays.asList(new DnsRecord(DnsRecordsType.A, 98, "10.0.0.1"), new DnsRecord(DnsRecordsType.A, 98, "10.0.0.3"));
        assertEquals(dnsRecords, correctResult);
    }

    @Test
    public void normalizeDomainDnsRecords() throws Exception {
        String domain = "    TEstING.CRYPTO    ";
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
        String domainWithMultiChainRecords = "testing.crypto";
        String notNormalizedDomainWithMultiChainRecords = "   Testing.crypto ";

        String erc20 = resolution.getMultiChainAddress(domainWithMultiChainRecords, "usdt", "erc20");
        assertEquals("0x58cA45E932a88b2E7D0130712B3AA9fB7c5781e2", erc20);
        String tron = resolution.getMultiChainAddress(domainWithMultiChainRecords, "usdt", "tron");
        assertEquals("TRMJfXXbmwb3WFSRKbeRgKsYoD8o1a9xxV", tron);
        String omni = resolution.getMultiChainAddress(domainWithMultiChainRecords, "usdt", "omni");
        assertEquals("1KvzMF2Vjy14d6JGY7dG7vjT5kfpmzSQXM", omni);
        String eos = resolution.getMultiChainAddress(notNormalizedDomainWithMultiChainRecords, "usdt", "eos");
        assertEquals("karaarishmen", eos);


    }

    @Test
    public void testTokenURIUNS() throws Exception {
        String tokenUri = resolution.getTokenURI("brad.crypto");
        assertEquals("https://metadata.staging.unstoppabledomains.com/metadata/brad.crypto", tokenUri);

        TestUtils.expectError(() -> resolution.getTokenURI("fake-domain-that-does-not-exist.crypto"), NSExceptionCode.UnregisteredDomain);
    }

    @Test
    public void testTokenURIUnregistered() throws Exception {
        TestUtils.expectError(() -> resolution.getTokenURI("fake-domain-that-does-not-exist.crypto"), NSExceptionCode.UnregisteredDomain);
    }

    @Test
    public void testTokenURIZNS() throws Exception {
        String testDomain = "brad.zil";
        TestUtils.expectError(() -> resolution.getTokenURI(testDomain), NSExceptionCode.NotImplemented);
    }

    @Test
    public void testTokenURIMetadata() throws Exception {
        String testDomain = "brad.crypto";

        TokenUriMetadata metadata = resolution.getTokenURIMetadata(testDomain);
        assertNotNull(metadata);
        assertEquals(metadata.getName(), testDomain);
        assertEquals(metadata.getAttributes().size(), 8);
        TokenUriMetadataAttribute attribute = metadata.new TokenUriMetadataAttribute();
        attribute.setTraitType("ETH");
        attribute.setValue("0x8aaD44321A86b170879d7A244c1e8d360c99DdA8");
        assertTrue(metadata.getAttributes().contains(attribute));
    }

    @Test
    public void testUnhashCNS() throws Exception {
        String testHash = "0x756e4e998dbffd803c21d23b06cd855cdc7a4b57706c95964a37e24b47c10fc9";
        String tokenName = resolution.unhash(testHash, NamingServiceType.UNS);
        assertEquals("brad.crypto", tokenName);

    }

    @Test
    public void testUnhashUnregistered() throws Exception {
        TestUtils.expectError(() -> resolution.unhash("0x0a1e7db0adb5b2b4d7de50f8091def73070759aec2a463006cbcd31932cca14b", NamingServiceType.UNS), NSExceptionCode.UnregisteredDomain);
    }

    @Test
    public void testUnhashUNS() throws Exception {
        String testHash = "0x1586d090e1b5781399f988e4b4f5639f4c2775ef5ec093d1279bb95b9bceb1a0";
        String tokenName = resolution.unhash(testHash, NamingServiceType.UNS);
        assertEquals("udtestdev-my-new-tls.wallet", tokenName);
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
        Location cns = new Location(
            "0xaad76bea7cfec82927239415bb18d2e93518ecbb", 
            "0x95ae1515367aa64c462c71e87157771165b1287a", 
            Network.RINKEBY,
            "ETH",
            "0x499dd6d875787869670900a2130223d85d4f6aa7",
            "https://rinkeby.infura.io/v3/e0c0cb9d12c440a29379df066de587e6");

        Location uns = new Location(
            "0x7fb83000b8ed59d3ead22f0d584df3a85fbc0086", 
            "0x7fb83000b8ed59d3ead22f0d584df3a85fbc0086", 
            Network.RINKEBY,
            "ETH",
            "0x6ec0deed30605bcd19342f3c30201db263291589",
            "https://rinkeby.infura.io/v3/e0c0cb9d12c440a29379df066de587e6");

        Location l2 = new Location(
            "0x2a93c52e7b6e7054870758e15a1446e769edfb93", 
            "0x2a93c52e7b6e7054870758e15a1446e769edfb93", 
            Network.MUMBAI_TESTNET,
            "MATIC",
            "0x499dd6d875787869670900a2130223d85d4f6aa7",
            "https://matic-testnet-archive-rpc.bwarelabs.com/");

        Map<String, Location> locations = resolution.getLocations("brad.crypto", "udtestdev-my-new-tls.wallet", "not-registered-12345abc.crypto", "udtestdev-test-l2-domain-784391.wallet");
        assertEquals(cns, locations.get("brad.crypto"));
        assertEquals(uns, locations.get("udtestdev-my-new-tls.wallet"));
        assertEquals(null, locations.get("not-registered-12345abc.crypto"));
        assertEquals(l2, locations.get("udtestdev-test-l2-domain-784391.wallet"));
    }

    @Test
    public void testLocationsZNS() throws Exception {
        TestUtils.expectError(() -> resolution.getLocations("some.zil"), NSExceptionCode.NotImplemented);
    }

    @Test
    public void testLocationsMixed() throws Exception {
        TestUtils.expectError(() -> resolution.getLocations("brad.crypto", "testing.zil"), NSExceptionCode.InconsistentDomainArray);
    }
}

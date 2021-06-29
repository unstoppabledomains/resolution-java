package com.unstoppabledomains.resolution;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.unstoppabledomains.TestUtils;
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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ResolutionTest {

    private static DomainResolution resolution;

    @BeforeAll
    public static void init() {
        resolution = Resolution.builder()
        .chainId(NamingServiceType.UNS, Network.RINKEBY)
        .chainId(NamingServiceType.ZNS, Network.MAINNET)
        .providerUrl(NamingServiceType.UNS, TestUtils.TESTING_UNS_PROVIDER_URL)
        .build();
    }

    @Test
    public void resolveRinkebyDomain() throws Exception {
        DomainResolution rinkebyResolution = Resolution.builder()
            .providerUrl(NamingServiceType.UNS, "https://rinkeby.infura.io/v3/e0c0cb9d12c440a29379df066de587e6")
            .build();
        String ethAddress = rinkebyResolution.getAddress("udtestdev-creek.crypto", "eth");
        assertEquals("0x1C8b9B78e3085866521FE206fa4c1a67F49f153A", ethAddress);

    }

    @Test
    public void testDifferentNetworks() throws Exception {
        DomainResolution customUnsNetwork = Resolution.builder()
            .providerUrl(NamingServiceType.UNS, "https://rinkeby.infura.io/v3/e0c0cb9d12c440a29379df066de587e6")
            .chainId(NamingServiceType.ZNS, Network.KOVAN)
            .build();

        Network customUnsChainId = customUnsNetwork.getNetwork(NamingServiceType.UNS);
        Network customZnsChainId = customUnsNetwork.getNetwork(NamingServiceType.ZNS);
        assertEquals(Network.RINKEBY, customUnsChainId);
        assertEquals(Network.KOVAN, customZnsChainId);
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
                .chainId(NamingServiceType.UNS, Network.RINKEBY)
                .chainId(NamingServiceType.ZNS, Network.MAINNET)
                .providerUrl(NamingServiceType.UNS, TestUtils.TESTING_UNS_PROVIDER_URL)
                .build();

        assertEquals("0x58ca45e932a88b2e7d0130712b3aa9fb7c5781e2", resolutionFromBuilder.getOwner("testing.crypto"));
        assertEquals("0xcea21f5a6afc11b3a4ef82e986d63b8b050b6910", resolutionFromBuilder.getOwner("johnnyjumper.zil"));
    }

    @Test
    public void shouldResolveFromResolutionCreatedByBuilderWithInfura() throws Exception {
        DomainResolution resolutionFromBuilderWithInfura = Resolution.builder()
                .chainId(NamingServiceType.ZNS, Network.MAINNET)
                .infura(NamingServiceType.UNS, Network.RINKEBY, TestUtils.TESTING_INFURA_UNS_PROJECT_ID)
                .build();
        assertEquals("0x58ca45e932a88b2e7d0130712b3aa9fb7c5781e2", resolutionFromBuilderWithInfura.getOwner("testing.crypto"));
        assertEquals("0xcea21f5a6afc11b3a4ef82e986d63b8b050b6910", resolutionFromBuilderWithInfura.getOwner("johnnyjumper.zil"));
    }

    @Test
    public void isSupported() {
        boolean isValid = resolution.isSupported("example.test");
        assertTrue(isValid);

        isValid = resolution.isSupported("example.tqwdest");
        assertTrue(isValid);

        isValid = resolution.isSupported("example.qwdqwdq.wd.tqwdest");
        assertTrue(isValid);

        isValid = resolution.isSupported("example.crypto");
        assertTrue(isValid);

        isValid = resolution.isSupported("example.zil");
        assertTrue(isValid);
    }

    @Test
    public void namehash() throws NamingServiceException {
        String hash = resolution.getNamehash("crypto");
        assertEquals("0x0f4a10a4f46c288cea365fcf45cccf0e9d901b945b9829ccdb54c10dc3cb7a6f", hash);
        hash = resolution.getNamehash("brad.crypto");
        assertEquals("0x756e4e998dbffd803c21d23b06cd855cdc7a4b57706c95964a37e24b47c10fc9", hash);

        hash = resolution.getNamehash("zil");
        assertEquals("0x9915d0456b878862e822e2361da37232f626a2e47505c8795134a95d36138ed3", hash);
        hash = resolution.getNamehash("johnnyjumper.zil");
        assertEquals("0x08ab2ffa92966738c881a37d0d97f168d2e076d24639921762d0985ebaa62e31", hash);
    }

    @Test
    public void getRecord() throws Exception {
        String recordValue = resolution.getRecord("testing.crypto", "crypto.ETH.address");
        assertEquals("0x58cA45E932a88b2E7D0130712B3AA9fB7c5781e2", recordValue);
    }

    @Test
    public void noRecord() throws Exception {
        TestUtils.expectError(() -> resolution.getRecord("testing.crypto", "invalid.record.value"), NSExceptionCode.RecordNotFound);
    }

    @Test
    public void addr() throws Exception {
        String addr = resolution.getAddress("udtestdev--awefawef.crypto", "eth");
        assertEquals("0x58cA45E932a88b2E7D0130712B3AA9fB7c5781e2", addr);

        addr = resolution.getAddress("testing.crypto", "eth");
        assertEquals("0x58cA45E932a88b2E7D0130712B3AA9fB7c5781e2", addr, "testing.crypto --> eth");

        addr = resolution.getAddress("johnnyjumper.zil", "eth");
        assertEquals("0xe7474D07fD2FA286e7e0aa23cd107F8379085037", addr, "johnnyjumper.zil --> eth");
    }

    @Test
    public void wrongDomainAddr() throws Exception {
        TestUtils.expectError(() -> resolution.getAddress("unregistered.crypto", "eth"), NSExceptionCode.UnregisteredDomain);
        TestUtils.expectError(() -> resolution.getAddress("unregistered26572654326523456.zil", "eth"), NSExceptionCode.UnregisteredDomain);
    }

    @Test
    public void UnknownCurrency() throws Exception {
        TestUtils.expectError(() -> resolution.getAddress("testing.crypto", "unknown"), NSExceptionCode.UnknownCurrency);
        TestUtils.expectError(() -> resolution.getAddress("johnnyjumper.zil", "unknown"), NSExceptionCode.UnknownCurrency);
        TestUtils.expectError(() -> resolution.getAddress("testing.crypto", "dodge"), NSExceptionCode.UnknownCurrency);
        TestUtils.expectError(() -> resolution.getAddress("johnnyjumper.zil", "dodge"), NSExceptionCode.UnknownCurrency);
    }

    @Test
    public void ipfsHash() throws NamingServiceException {
        String ipfs = resolution.getIpfsHash("testing.crypto");
        assertEquals("QmRi3PBpUGFnYrCKUoWhntRLfA9PeRhepfFu4Lz21mGd3X", ipfs);

        ipfs = resolution.getIpfsHash("johnnyjumper.zil");
        assertEquals("QmQ38zzQHVfqMoLWq2VeiMLHHYki9XktzXxLYTWXt8cydu", ipfs);
    }

    @Test
    public void emailTest() throws NamingServiceException {
        String email = resolution.getEmail("testing.crypto");
        assertEquals("testing@example.com", email);
    }

    @Test
    public void ownerTest() throws NamingServiceException {
        String owner = resolution.getOwner("testing.crypto");
        assertEquals("0x58ca45e932a88b2e7d0130712b3aa9fb7c5781e2", owner);

        owner = resolution.getOwner("johnnyjumper.zil");
        assertEquals("0xcea21f5a6afc11b3a4ef82e986d63b8b050b6910", owner);
    }

    @Test
    public void usdtTest() throws Exception {
        String domain = "testing.crypto";
        String erc20 = resolution.getUsdt(domain, TickerVersion.ERC20);
        assertEquals("0x58cA45E932a88b2E7D0130712B3AA9fB7c5781e2", erc20);
        String tron = resolution.getUsdt(domain, TickerVersion.TRON);
        assertEquals("TRMJfXXbmwb3WFSRKbeRgKsYoD8o1a9xxV", tron);
        String omni = resolution.getUsdt(domain, TickerVersion.OMNI);
        assertEquals("1KvzMF2Vjy14d6JGY7dG7vjT5kfpmzSQXM", omni);
        String eos = resolution.getUsdt(domain, TickerVersion.EOS);
        assertEquals("karaarishmen", eos);

        TestUtils.expectError(
            () -> resolution.getUsdt("unregistered.crypto", TickerVersion.ERC20),
            NSExceptionCode.UnregisteredDomain
        );
        
        TestUtils.expectError(
            () -> resolution.getUsdt("udtestdev--awefawef.crypto", TickerVersion.TRON),
            NSExceptionCode.RecordNotFound
        );
    }

    @Test
    public void ownerFailTest() throws Exception {
        TestUtils.expectError(() -> resolution.getOwner("unregistered.crypto"), NSExceptionCode.UnregisteredDomain);
    }

    @Test
    public void noIpfsHash() throws Exception {
        TestUtils.expectError(() -> resolution.getIpfsHash("unregstered.crypto"), NSExceptionCode.UnregisteredDomain);
        TestUtils.expectError(() -> resolution.getIpfsHash("udtestdev--awefawef.crypto"), NSExceptionCode.RecordNotFound);

    }

    @Test
    public void noEmailRecord() throws Exception {
        TestUtils.expectError(() -> resolution.getEmail("brad.crypto"), NSExceptionCode.RecordNotFound);
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
            () -> resolutionWithProvider.getAddress("brad.crypto", "eth"),
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

        String erc20 = resolution.getMultiChainAddress(domainWithMultiChainRecords, "usdt", "erc20");
        assertEquals("0x58cA45E932a88b2E7D0130712B3AA9fB7c5781e2", erc20);
        String tron = resolution.getMultiChainAddress(domainWithMultiChainRecords, "usdt", "tron");
        assertEquals("TRMJfXXbmwb3WFSRKbeRgKsYoD8o1a9xxV", tron);
        String omni = resolution.getMultiChainAddress(domainWithMultiChainRecords, "usdt", "omni");
        assertEquals("1KvzMF2Vjy14d6JGY7dG7vjT5kfpmzSQXM", omni);
        String eos = resolution.getMultiChainAddress(domainWithMultiChainRecords, "usdt", "eos");
        assertEquals("karaarishmen", eos);

        
    }

    // @Test
    // public void testTokenURIUNS() throws Exception {
    //     String tokenUri = resolution.tokenURI("brad.crypto");
    //     assertEquals("https://metadata.unstoppabledomains.com/metadata/brad.crypto", tokenUri);

    //     TestUtils.expectError(() -> resolution.tokenURI("fake-domain-that-does-not-exist.crypto"), NSExceptionCode.UnregisteredDomain);
    // }

    @Test
    public void testTokenURIZNS() throws Exception {
        String testDomain = "brad.zil";
        TestUtils.expectError(() -> resolution.tokenURI(testDomain), NSExceptionCode.NotImplemented);
    }

    @Test
    public void testTokenURIMetadata() throws Exception {
        String testDomain = "brad.crypto";

        TokenUriMetadata metadata = resolution.tokenURIMetadata(testDomain);
        assertNotNull(metadata);
        assertEquals(metadata.getName(), testDomain);
        assertEquals(metadata.getAttributes().size(), 8);
        TokenUriMetadataAttribute attribute = metadata.new TokenUriMetadataAttribute();
        attribute.setTraitType("ETH");
        attribute.setValue("0x8aaD44321A86b170879d7A244c1e8d360c99DdA8");
        assertTrue(metadata.getAttributes().contains(attribute));
    }

    @Test
    public void testUnhashUNS() throws Exception {
        String testHash = "0x756e4e998dbffd803c21d23b06cd855cdc7a4b57706c95964a37e24b47c10fc9";

        String tokenName = resolution.unhash(testHash, NamingServiceType.UNS);
        assertEquals("brad.crypto", tokenName);
    }

    @Test
    public void testUnhashZNS() throws Exception {
        String testHash = "0x5fc604da00f502da70bfbc618088c0ce468ec9d18d05540935ae4118e8f50787";
        TestUtils.expectError(() -> resolution.unhash(testHash, NamingServiceType.ZNS), NSExceptionCode.NotImplemented);
    }
}

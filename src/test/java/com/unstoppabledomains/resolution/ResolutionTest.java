package com.unstoppabledomains.resolution;

import com.unstoppabledomains.TestUtils;
import com.unstoppabledomains.config.network.model.Network;
import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.naming.service.NamingServiceType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResolutionTest {

    private static final String TESTING_PROVIDER_URL = System.getenv("TESTING_PROVIDER_URL");
    private static final String TESTING_INFURA_PROJECT_ID = System.getenv("TESTING_INFURA_PROJECT_ID");

    private static DomainResolution resolution;

    @BeforeAll
    public static void init() {
        resolution = Resolution.builder()
                .providerUrl(NamingServiceType.CNS, TESTING_PROVIDER_URL)
                .providerUrl(NamingServiceType.ENS, TESTING_PROVIDER_URL)
                .build();
    }

    @Test
    public void shouldResolveFromResolutionCreatedByBuilder() throws Exception {
        DomainResolution resolutionFromBuilder = Resolution.builder()
                .chainId(NamingServiceType.ENS, Network.ROPSTEN)
                .providerUrl(NamingServiceType.ENS, TESTING_PROVIDER_URL)
                .providerUrl(NamingServiceType.CNS, TESTING_PROVIDER_URL)
                .build();

        assertEquals("0x8aad44321a86b170879d7a244c1e8d360c99dda8", resolutionFromBuilder.getOwner("brad.crypto"));
        assertEquals("0x842f373409191cff2988a6f19ab9f605308ee462", resolutionFromBuilder.getOwner("monkybrain.eth"));
        assertEquals("0xcea21f5a6afc11b3a4ef82e986d63b8b050b6910", resolutionFromBuilder.getOwner("johnnyjumper.zil"));
    }

    @Test
    public void shouldResolveFromResolutionCreatedByBuilderWithInfura() throws Exception {
        DomainResolution resolutionFromBuilderWithInfura = Resolution.builder()
                .chainId(NamingServiceType.ENS, Network.ROPSTEN)
                .infura(NamingServiceType.ENS, TESTING_INFURA_PROJECT_ID)
                .infura(NamingServiceType.CNS, Network.MAINNET, TESTING_INFURA_PROJECT_ID)
                .build();

        assertEquals("0x8aad44321a86b170879d7a244c1e8d360c99dda8", resolutionFromBuilderWithInfura.getOwner("brad.crypto"));
        assertEquals("0x5d069edc8cc1c559e4482bec199c13547455208", resolutionFromBuilderWithInfura.getOwner("monkybrain.eth"));
        assertEquals("0xcea21f5a6afc11b3a4ef82e986d63b8b050b6910", resolutionFromBuilderWithInfura.getOwner("johnnyjumper.zil"));
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
    public void wrongDomainNamehash() throws Exception {
        TestUtils.checkError(() -> resolution.getNamehash("unupported"), NSExceptionCode.UnsupportedDomain);
    }

    @Test
    public void addr() throws Exception {
        String addr = resolution.getAddress("homecakes.crypto", "eth");
        assertEquals("0xe7474D07fD2FA286e7e0aa23cd107F8379085037", addr);

        addr = resolution.getAddress("brad.crypto", "eth");
        assertEquals("0x8aaD44321A86b170879d7A244c1e8d360c99DdA8", addr, "brad.crypto --> eth");

        addr = resolution.getAddress("johnnyjumper.zil", "eth");
        assertEquals("0xe7474D07fD2FA286e7e0aa23cd107F8379085037", addr, "johnnyjumper.zil --> eth");
    }


    @Test
    public void wrongDomainAddr() throws Exception {
        TestUtils.checkError(() -> resolution.getAddress("unregistered.crypto", "eth"), NSExceptionCode.UnregisteredDomain);
        TestUtils.checkError(() -> resolution.getAddress("unregistered26572654326523456.zil", "eth"), NSExceptionCode.UnregisteredDomain);
    }

    @Test
    public void UnknownCurrency() throws Exception {
        TestUtils.checkError(() -> resolution.getAddress("brad.crypto", "unknown"), NSExceptionCode.UnknownCurrency);
        TestUtils.checkError(() -> resolution.getAddress("johnnyjumper.zil", "unknown"), NSExceptionCode.UnknownCurrency);
        TestUtils.checkError(() -> resolution.getAddress("brad.crypto", "dodge"), NSExceptionCode.UnknownCurrency);
        TestUtils.checkError(() -> resolution.getAddress("johnnyjumper.zil", "dodge"), NSExceptionCode.UnknownCurrency);
    }

    @Test
    public void ipfsHash() throws NamingServiceException {
        String ipfs = resolution.getIpfsHash("brad.crypto");
        assertEquals("Qme54oEzRkgooJbCDr78vzKAWcv6DDEZqRhhDyDtzgrZP6", ipfs);

        ipfs = resolution.getIpfsHash("johnnyjumper.zil");
        assertEquals("QmQ38zzQHVfqMoLWq2VeiMLHHYki9XktzXxLYTWXt8cydu", ipfs);

        ipfs = resolution.getIpfsHash("reseller-test-udtesting-341567718146.crypto");
        assertEquals("QmVJ26hBrwwNAPVmLavEFXDUunNDXeFSeMPmHuPxKe6dJv", ipfs);
    }

    @Test
    public void emailTest() throws NamingServiceException {
        String email = resolution.getEmail("johnnyjumper.zil");
        assertEquals("jeyhunt@gmail.com", email);
    }

    @Test
    public void ownerTest() throws NamingServiceException {
        String owner = resolution.getOwner("brad.crypto");
        assertEquals("0x8aad44321a86b170879d7a244c1e8d360c99dda8", owner);

        owner = resolution.getOwner("johnnyjumper.zil");
        assertEquals("0xcea21f5a6afc11b3a4ef82e986d63b8b050b6910", owner);
    }

    @Test
    public void ownerFailTest() throws Exception {
        TestUtils.checkError(() -> resolution.getOwner("unregistered.crypto"), NSExceptionCode.UnregisteredDomain);
    }

    @Test
    public void noIpfsHash() throws Exception {
        TestUtils.checkError(() -> resolution.getIpfsHash("unregstered.crypto"), NSExceptionCode.UnregisteredDomain);
        TestUtils.checkError(() -> resolution.getIpfsHash("pickleberrypop.crypto"), NSExceptionCode.RecordNotFound);

    }

    @Test
    public void noEmailRecord() throws Exception {
        TestUtils.checkError(() -> resolution.getEmail("brad.crypto"), NSExceptionCode.RecordNotFound);
    }
}

package com.unstoppabledomains.resolution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import java.util.Map;

import com.unstoppabledomains.config.network.model.Network;
import com.unstoppabledomains.resolution.naming.service.NSConfig;
import com.unstoppabledomains.resolution.naming.service.NamingService;
import com.unstoppabledomains.resolution.naming.service.NamingServiceType;
import com.unstoppabledomains.resolution.naming.service.uns.UNS;
import com.unstoppabledomains.resolution.naming.service.uns.UNSLocation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ResolutionBuilderTest {
    @Mock
    Resolution.ResolutionBuilderConnector mockConnector;

    @Captor
    ArgumentCaptor<Map<NamingServiceType, NamingService>> servicesCaptor;


    private void checkConfigurations(NSConfig expected, NamingService service) {
        assertEquals(expected.getChainId(), service.getNetwork());
        assertEquals(expected.getBlockchainProviderUrl(), service.getProviderUrl());
        assertEquals(expected.getContractAddress(), service.getContractAddress());
    }

    private void checkUnsConfigurations(NSConfig expected, UNS service, UNSLocation layer) {
        assertEquals(expected.getChainId(), service.getNetwork(layer));
        assertEquals(expected.getBlockchainProviderUrl(), service.getProviderUrl(layer));
        assertEquals(expected.getContractAddress(), service.getContractAddress(layer));
    }

    @Test
    public void buildsWithDefaultParams() throws Exception {
        NSConfig expectedUNSL1Config = new NSConfig(Network.MAINNET, "https://mainnet.infura.io/v3/e0c0cb9d12c440a29379df066de587e6", "0xfEe4D4F0aDFF8D84c12170306507554bC7045878");
        NSConfig expectedUNSL2Config = new NSConfig(Network.MUMBAI_TESTNET, "https://polygon-mumbai.infura.io/v3/e0c0cb9d12c440a29379df066de587e6", "0x8F4870e8aD6F0307CD3AAE3ED1d66FffCB873F3A");
        NSConfig expectedENSConfig = new NSConfig(Network.MAINNET, "https://mainnet.infura.io/v3/d423cf2499584d7fbe171e33b42cfbee", "0x00000000000C2E074eC69A0dFb2997BA6C7d2e1e");
        NSConfig expectedZNSConfig = new NSConfig(Network.MAINNET, "https://api.zilliqa.com", "0x9611c53BE6d1b32058b2747bdeCECed7e1216793");

        ResolutionBuilder builder = new ResolutionBuilder(mockConnector);
        builder.build();

        verify(mockConnector).buildResolution(servicesCaptor.capture());

        Map<NamingServiceType, NamingService> capturedServices = servicesCaptor.getValue();
        UNS unsService = (UNS) capturedServices.get(NamingServiceType.UNS);
        checkUnsConfigurations(expectedUNSL1Config, unsService, UNSLocation.Layer1);
        checkUnsConfigurations(expectedUNSL2Config, unsService, UNSLocation.Layer2);
        checkConfigurations(expectedENSConfig, capturedServices.get(NamingServiceType.ENS));
        checkConfigurations(expectedZNSConfig, capturedServices.get(NamingServiceType.ZNS));
    }

    @Test
    public void buildsWithCustomParams() throws Exception {
        NSConfig expectedUNSL1Config = new NSConfig(Network.RINKEBY, "https://rinkeby.infura.io/v3/e0c0cb9d12c440a29379df066de587e6", "0x00000000000000000000000000000000000000001");
        NSConfig expectedUNSL2Config = new NSConfig(Network.MATIC_MAINNET, "https://polygon-mainnet.infura.io/v3/e0c0cb9d12c440a29379df066de587e6", "0x00000000000000000000000000000000000000002");
        NSConfig expectedENSConfig = new NSConfig(Network.RINKEBY, "https://rinkeby.infura.io/v3/d423cf2499584d7fbe171e33b42cfbee", "0x00000000000000000000000000000000000000003");
        NSConfig expectedZNSConfig = new NSConfig(Network.ZIL_TESTNET, "https://dev-api.zilliqa.com", "0x00000000000000000000000000000000000000004");

        ResolutionBuilder builder = new ResolutionBuilder(mockConnector);
        builder
            .unsChainId(UNSLocation.Layer1, expectedUNSL1Config.getChainId())
            .unsChainId(UNSLocation.Layer2, expectedUNSL2Config.getChainId())
            .chainId(NamingServiceType.ENS, expectedENSConfig.getChainId())
            .chainId(NamingServiceType.ZNS, expectedZNSConfig.getChainId())
            .unsProviderUrl(UNSLocation.Layer1, expectedUNSL1Config.getBlockchainProviderUrl())
            .unsProviderUrl(UNSLocation.Layer2, expectedUNSL2Config.getBlockchainProviderUrl())
            .providerUrl(NamingServiceType.ENS, expectedENSConfig.getBlockchainProviderUrl())
            .providerUrl(NamingServiceType.ZNS, expectedZNSConfig.getBlockchainProviderUrl())
            .unsContractAddress(UNSLocation.Layer1, expectedUNSL1Config.getContractAddress())
            .unsContractAddress(UNSLocation.Layer2, expectedUNSL2Config.getContractAddress())
            .contractAddress(NamingServiceType.ENS, expectedENSConfig.getContractAddress())
            .contractAddress(NamingServiceType.ZNS, expectedZNSConfig.getContractAddress())
            .build();

        verify(mockConnector).buildResolution(servicesCaptor.capture());

        Map<NamingServiceType, NamingService> capturedServices = servicesCaptor.getValue();
        UNS unsService = (UNS) capturedServices.get(NamingServiceType.UNS);
        checkUnsConfigurations(expectedUNSL1Config, unsService, UNSLocation.Layer1);
        checkUnsConfigurations(expectedUNSL2Config, unsService, UNSLocation.Layer2);
        checkConfigurations(expectedENSConfig, capturedServices.get(NamingServiceType.ENS));
        checkConfigurations(expectedZNSConfig, capturedServices.get(NamingServiceType.ZNS));
    }

    @Test
    public void allowsToCustomizeOneService() throws Exception {
        NSConfig expectedUNSL1Config = new NSConfig(Network.MAINNET, "https://mainnet.infura.io/v3/e0c0cb9d12c440a29379df066de587e6", "0xfEe4D4F0aDFF8D84c12170306507554bC7045878");
        NSConfig expectedUNSL2Config = new NSConfig(Network.MUMBAI_TESTNET, "https://polygon-mumbai.infura.io/v3/e0c0cb9d12c440a29379df066de587e6", "0x8F4870e8aD6F0307CD3AAE3ED1d66FffCB873F3A");
        NSConfig expectedENSConfig = new NSConfig(Network.MAINNET, "https://mainnet.infura.io/v3/d423cf2499584d7fbe171e33b42cfbee", "0x00000000000C2E074eC69A0dFb2997BA6C7d2e1e");
        NSConfig expectedZNSConfig = new NSConfig(Network.ZIL_TESTNET, "https://dev-api.zilliqa.com", "0x00000000000000000000000000000000000000004");

        ResolutionBuilder builder = new ResolutionBuilder(mockConnector);
        builder
            .chainId(NamingServiceType.ZNS, expectedZNSConfig.getChainId())
            .providerUrl(NamingServiceType.ZNS, expectedZNSConfig.getBlockchainProviderUrl())
            .contractAddress(NamingServiceType.ZNS, expectedZNSConfig.getContractAddress())
            .build();

        verify(mockConnector).buildResolution(servicesCaptor.capture());

        Map<NamingServiceType, NamingService> capturedServices = servicesCaptor.getValue();
        UNS unsService = (UNS) capturedServices.get(NamingServiceType.UNS);
        checkUnsConfigurations(expectedUNSL1Config, unsService, UNSLocation.Layer1);
        checkUnsConfigurations(expectedUNSL2Config, unsService, UNSLocation.Layer2);
        checkConfigurations(expectedENSConfig, capturedServices.get(NamingServiceType.ENS));
        checkConfigurations(expectedZNSConfig, capturedServices.get(NamingServiceType.ZNS));
    }

    @Test
    public void checksIfBothUNSConfigsAreSet() throws Exception {
        ResolutionBuilder builder = new ResolutionBuilder(mockConnector);
        builder.unsChainId(UNSLocation.Layer1, Network.RINKEBY);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> builder.build());
        assertEquals("Configuration provided only for one UNS layer", ex.getMessage());
    }

    @Test
    public void checksIfAllConfigsAreSetForOneService() throws Exception {
        ResolutionBuilder builder = new ResolutionBuilder(mockConnector);

        builder.chainId(NamingServiceType.ZNS, Network.ZIL_TESTNET);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> builder.build());
        assertEquals("Invalid configuration for service ZNS: Provider URL is not set; Contract address is not set", ex.getMessage());

        builder.providerUrl(NamingServiceType.ZNS, "https://dev-api.zilliqa.com");
        ex = assertThrows(IllegalArgumentException.class, () -> builder.build());
        assertEquals("Invalid configuration for service ZNS: Contract address is not set", ex.getMessage());
    }
}

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
        NSConfig expectedUNSL1Config = new NSConfig(Network.MAINNET, "https://mainnet.infura.io/v3/e0c0cb9d12c440a29379df066de587e6", "0x578853aa776Eef10CeE6c4dd2B5862bdcE767A8B");
        NSConfig expectedUNSL2Config = new NSConfig(Network.MATIC_MAINNET, "https://polygon-mainnet.infura.io/v3/e0c0cb9d12c440a29379df066de587e6", "0x91EDd8708062bd4233f4Dd0FCE15A7cb4d500091");
        NSConfig expectedZNSConfig = new NSConfig(Network.MAINNET, "https://api.zilliqa.com", "0x9611c53BE6d1b32058b2747bdeCECed7e1216793");

        ResolutionBuilder builder = new ResolutionBuilder(mockConnector);
        builder.build();

        verify(mockConnector).buildResolution(servicesCaptor.capture());

        Map<NamingServiceType, NamingService> capturedServices = servicesCaptor.getValue();
        UNS unsService = (UNS) capturedServices.get(NamingServiceType.UNS);
        checkUnsConfigurations(expectedUNSL1Config, unsService, UNSLocation.Layer1);
        checkUnsConfigurations(expectedUNSL2Config, unsService, UNSLocation.Layer2);
        checkConfigurations(expectedZNSConfig, capturedServices.get(NamingServiceType.ZNS));
    }

    @Test
    public void buildsWithCustomParams() throws Exception {
        NSConfig expectedUNSL1Config = new NSConfig(Network.GOERLI, "https://goerli.infura.io/v3/e0c0cb9d12c440a29379df066de587e6", "0x00000000000000000000000000000000000000001");
        NSConfig expectedUNSL2Config = new NSConfig(Network.MUMBAI_TESTNET, "https://polygon-mumbai.infura.io/v3/e0c0cb9d12c440a29379df066de587e6", "0x00000000000000000000000000000000000000002");
        NSConfig expectedZNSConfig = new NSConfig(Network.ZIL_TESTNET, "https://dev-api.zilliqa.com", "0x00000000000000000000000000000000000000004");

        ResolutionBuilder builder = new ResolutionBuilder(mockConnector);
        builder
            .unsChainId(UNSLocation.Layer1, expectedUNSL1Config.getChainId())
            .unsChainId(UNSLocation.Layer2, expectedUNSL2Config.getChainId())
            .znsChainId(expectedZNSConfig.getChainId())
            .unsProviderUrl(UNSLocation.Layer1, expectedUNSL1Config.getBlockchainProviderUrl())
            .unsProviderUrl(UNSLocation.Layer2, expectedUNSL2Config.getBlockchainProviderUrl())
            .znsProviderUrl(expectedZNSConfig.getBlockchainProviderUrl())
            .unsContractAddress(UNSLocation.Layer1, expectedUNSL1Config.getContractAddress())
            .unsContractAddress(UNSLocation.Layer2, expectedUNSL2Config.getContractAddress())
            .znsContractAddress(expectedZNSConfig.getContractAddress())
            .build();

        verify(mockConnector).buildResolution(servicesCaptor.capture());

        Map<NamingServiceType, NamingService> capturedServices = servicesCaptor.getValue();
        UNS unsService = (UNS) capturedServices.get(NamingServiceType.UNS);
        checkUnsConfigurations(expectedUNSL1Config, unsService, UNSLocation.Layer1);
        checkUnsConfigurations(expectedUNSL2Config, unsService, UNSLocation.Layer2);
        checkConfigurations(expectedZNSConfig, capturedServices.get(NamingServiceType.ZNS));
    }

    @Test
    public void allowsToCustomizeOneService() throws Exception {
        NSConfig expectedUNSL1Config = new NSConfig(Network.MAINNET, "https://mainnet.infura.io/v3/e0c0cb9d12c440a29379df066de587e6", "0x578853aa776Eef10CeE6c4dd2B5862bdcE767A8B");
        NSConfig expectedUNSL2Config = new NSConfig(Network.MATIC_MAINNET, "https://polygon-mainnet.infura.io/v3/e0c0cb9d12c440a29379df066de587e6", "0x91EDd8708062bd4233f4Dd0FCE15A7cb4d500091");
        NSConfig expectedZNSConfig = new NSConfig(Network.ZIL_TESTNET, "https://dev-api.zilliqa.com", "0x00000000000000000000000000000000000000004");

        ResolutionBuilder builder = new ResolutionBuilder(mockConnector);
        builder
            .znsChainId(expectedZNSConfig.getChainId())
            .znsProviderUrl(expectedZNSConfig.getBlockchainProviderUrl())
            .znsContractAddress(expectedZNSConfig.getContractAddress())
            .build();

        verify(mockConnector).buildResolution(servicesCaptor.capture());

        Map<NamingServiceType, NamingService> capturedServices = servicesCaptor.getValue();
        UNS unsService = (UNS) capturedServices.get(NamingServiceType.UNS);
        checkUnsConfigurations(expectedUNSL1Config, unsService, UNSLocation.Layer1);
        checkUnsConfigurations(expectedUNSL2Config, unsService, UNSLocation.Layer2);
        checkConfigurations(expectedZNSConfig, capturedServices.get(NamingServiceType.ZNS));
    }

    @Test
    public void checksIfBothUNSConfigsAreSet() throws Exception {
        ResolutionBuilder builder = new ResolutionBuilder(mockConnector);
        builder.unsChainId(UNSLocation.Layer1, Network.RINKEBY);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> builder.build());
        assertEquals("Configuration should be provided for UNS Layer1 and UNS Layer2", ex.getMessage());
    }

    @Test
    public void checksIfAllConfigsAreSetForOneService() throws Exception {
        ResolutionBuilder builder = new ResolutionBuilder(mockConnector);

        builder.znsChainId(Network.ZIL_TESTNET);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> builder.build());
        assertEquals("Invalid configuration for service ZNS: Provider URL is not set; Contract address is not set", ex.getMessage());

        builder.znsProviderUrl("https://dev-api.zilliqa.com");
        ex = assertThrows(IllegalArgumentException.class, () -> builder.build());
        assertEquals("Invalid configuration for service ZNS: Contract address is not set", ex.getMessage());
    }

    @Test
    public void pullsContractAddressFromConfig() throws Exception {
        NSConfig expectedUNSL1Config = new NSConfig(Network.MAINNET, "https://mainnet.infura.io/v3/e0c0cb9d12c440a29379df066de587e6", "0x578853aa776Eef10CeE6c4dd2B5862bdcE767A8B");
        NSConfig expectedUNSL2Config = new NSConfig(Network.MATIC_MAINNET, "https://polygon-mainnet.infura.io/v3/e0c0cb9d12c440a29379df066de587e6", "0x91EDd8708062bd4233f4Dd0FCE15A7cb4d500091");

        ResolutionBuilder builder = new ResolutionBuilder(mockConnector);
        builder
            .unsChainId(UNSLocation.Layer1, Network.MAINNET)
            .unsChainId(UNSLocation.Layer2, Network.MATIC_MAINNET)
            .unsProviderUrl(UNSLocation.Layer1, expectedUNSL1Config.getBlockchainProviderUrl())
            .unsProviderUrl(UNSLocation.Layer2, expectedUNSL2Config.getBlockchainProviderUrl())
            .build();

        verify(mockConnector).buildResolution(servicesCaptor.capture());

        Map<NamingServiceType, NamingService> capturedServices = servicesCaptor.getValue();
        UNS unsService = (UNS) capturedServices.get(NamingServiceType.UNS);
        checkUnsConfigurations(expectedUNSL1Config, unsService, UNSLocation.Layer1);
        checkUnsConfigurations(expectedUNSL2Config, unsService, UNSLocation.Layer2);
    }
}

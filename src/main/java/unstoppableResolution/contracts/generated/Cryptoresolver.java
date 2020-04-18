package unstoppableResolution.contracts.generated;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.5.16.
 */
@SuppressWarnings("rawtypes")
public class Cryptoresolver extends Contract {
    public static final String BINARY = "60806040523480156200001157600080fd5b5060405162001c1d38038062001c1d833981016040819052620000349162000160565b8180600160006101000a8154816001600160a01b0302191690836001600160a01b0316021790555050806001600160a01b0316637b1039996040518163ffffffff1660e01b815260040160206040518083038186803b1580156200009757600080fd5b505afa158015620000ac573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250620000d2919081019062000137565b6001600160a01b0316826001600160a01b031614620000f057600080fd5b600480546001600160a01b0319166001600160a01b039290921691909117905550620001ea565b80516200012481620001c5565b92915050565b80516200012481620001df565b6000602082840312156200014a57600080fd5b600062000158848462000117565b949350505050565b600080604083850312156200017457600080fd5b60006200018285856200012a565b925050602062000195858286016200012a565b9150509250929050565b60006200012482620001b9565b600062000124826200019f565b6001600160a01b031690565b620001d0816200019f565b8114620001dc57600080fd5b50565b620001d081620001ac565b611a2380620001fa6000396000f3fe608060405234801561001057600080fd5b50600436106100ea5760003560e01c80637a9eea461161008c578063b87abc1111610066578063b87abc11146101e1578063c5974073146101f4578063ce92b33e14610207578063e837ae741461021a576100ea565b80637a9eea46146101a65780637b103999146101b95780638f69c188146101ce576100ea565b806332184f2e116100c857806332184f2e1461014d57806339b9d1651461016057806347c81699146101735780636ccbae5f14610186576100ea565b80631bd8cc1a146100ef5780631be5e7ed14610118578063310bd74b14610138575b600080fd5b6101026100fd366004611108565b61022d565b60405161010f91906117a9565b60405180910390f35b61012b6101263660046113c6565b61047c565b60405161010f919061186c565b61014b610146366004611416565b6105fd565b005b61014b61015b366004611489565b610695565b61014b61016e3660046114a8565b61072e565b61014b61018136600461128f565b6107cb565b610199610194366004611416565b6108e6565b60405161010f919061188d565b6101996101b4366004611416565b6108f8565b6101c161090a565b60405161010f9190611772565b61014b6101dc3660046111cc565b61091a565b61014b6101ef366004611434565b610990565b61014b610202366004611313565b6109bb565b61014b61021536600461115c565b610a71565b61014b61022836600461115c565b610b17565b60015460405163b3f9e4cb60e01b815260609183916001600160a01b039091169063b3f9e4cb9061026290849060040161188d565b60206040518083038186803b15801561027a57600080fd5b505afa15801561028e573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052506102b291908101906110e2565b6001600160a01b0316306001600160a01b0316146102eb5760405162461bcd60e51b81526004016102e29061187d565b60405180910390fd5b604080518581526020808702820101909152849060609082801561032357816020015b606081526020019060019003908161030e5790505b506000868152600360205260408120549192505b8381101561046f576000878152600260209081526040808320858452909152902089898381811061036457fe5b602002820190508035601e193684900301811261038057600080fd5b909101602081019150356001600160401b0381111561039e57600080fd5b368190038213156103ae57600080fd5b6040516103bc929190611739565b9081526040805160209281900383018120805460026001821615610100026000190190911604601f8101859004850283018501909352828252909290919083018282801561044b5780601f106104205761010080835404028352916020019161044b565b820191906000526020600020905b81548152906001019060200180831161042e57829003601f168201915b505050505083828151811061045c57fe5b6020908102919091010152600101610337565b5090979650505050505050565b60015460405163b3f9e4cb60e01b815260609183916001600160a01b039091169063b3f9e4cb906104b190849060040161188d565b60206040518083038186803b1580156104c957600080fd5b505afa1580156104dd573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525061050191908101906110e2565b6001600160a01b0316306001600160a01b0316146105315760405162461bcd60e51b81526004016102e29061187d565b600083815260026020908152604080832060038352818420548452909152908190209051610560908690611746565b9081526040805160209281900383018120805460026001821615610100026000190190911604601f810185900485028301850190935282825290929091908301828280156105ef5780601f106105c4576101008083540402835291602001916105ef565b820191906000526020600020905b8154815290600101906020018083116105d257829003601f168201915b505050505091505092915050565b60015460405163430c208160e01b81526001600160a01b039091169063430c20819061062f9033908590600401611780565b60206040518083038186803b15801561064757600080fd5b505afa15801561065b573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525061067f9190810190611271565b61068857600080fd5b6106924282610b2e565b50565b60015460405163430c208160e01b81526001600160a01b039091169063430c2081906106c79033908590600401611780565b60206040518083038186803b1580156106df57600080fd5b505afa1580156106f3573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052506107179190810190611271565b61072057600080fd5b61072a8282610b2e565b5050565b6107bb6332184f2e60e01b858560405160240161074c92919061189b565b60408051601f19818403018152918152602080830180516001600160e01b03166001600160e01b031990951694909417845291519092208251601f86018390048302810183019093528483529186918690869081908401838280828437600092019190915250610b6d92505050565b6107c58484610b2e565b50505050565b60015460405163430c208160e01b81526001600160a01b039091169063430c2081906107fd9033908590600401611780565b60206040518083038186803b15801561081557600080fd5b505afa158015610829573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525061084d9190810190611271565b61085657600080fd5b6108df600360008381526020019081526020016000205486868080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525050604080516020601f8a018190048102820181019092528881529250889150879081908401838280828437600092019190915250879250610c7e915050565b5050505050565b60009081526020819052604090205490565b60009081526003602052604090205490565b6001546001600160a01b03165b90565b61097563ce92b33e60e01b85858560405160240161093a939291906117ba565b60408051601f198184030181529190526020810180516001600160e01b03166001600160e01b03199093169290921782525190208383610b6d565b6000828152600360205260409020546107c590858585610d89565b6109ac63310bd74b60e01b8460405160240161074c919061188d565b6109b64284610b2e565b505050565b6109df6347c8169960e01b888888888860405160240161074c95949392919061182c565b610a68600360008581526020019081526020016000205488888080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525050604080516020601f8c018190048102820181019092528a815292508a9150899081908401838280828437600092019190915250899250610c7e915050565b50505050505050565b60015460405163430c208160e01b81526001600160a01b039091169063430c208190610aa39033908590600401611780565b60206040518083038186803b158015610abb57600080fd5b505afa158015610acf573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250610af39190810190611271565b610afc57600080fd5b6000818152600360205260409020546109b690848484610d89565b6004546001600160a01b03163314610afc57600080fd5b60008181526003602052604080822084905551829184917fbfc300c1fa899f7ccfe35f4b00db10276968e3555c53921db20c8b82e97bc3209190a35050565b600082815260208181526040808320549051909291610bc3918591610bb791610b9c918a913091899101611702565b60405160208183030381529060405280519060200120610dd8565b9063ffffffff610e0816565b90506001600160a01b03811615801590610c5a575060015460405163430c208160e01b81526001600160a01b039091169063430c208190610c0a908490889060040161179b565b60206040518083038186803b158015610c2257600080fd5b505afa158015610c36573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250610c5a9190810190611271565b610c6357600080fd5b50505060009081526020819052604090208054600101905550565b6001548351602085012060405163538361a760e01b81526001600160a01b039092169163538361a791610cb69185919060040161189b565b600060405180830381600087803b158015610cd057600080fd5b505af1158015610ce4573d6000803e3d6000fd5b5050506000828152600260209081526040808320888452909152908190209051849250610d12908690611746565b90815260200160405180910390209080519060200190610d33929190610ee5565b508083604051610d439190611746565b6040518091039020857f531c34f1430b76d953b041b6ae19c2d0a9c4ed1c570242b24c147ace27ab6ef385604051610d7b919061186c565b60405180910390a450505050565b825160005b81811015610dd057610dc886868381518110610da657fe5b6020026020010151868481518110610dba57fe5b602002602001015186610c7e565b600101610d8e565b505050505050565b600081604051602001610deb9190611752565b604051602081830303815290604052805190602001209050919050565b60008151604114610e1b57506000610edf565b60208201516040830151606084015160001a7f7fffffffffffffffffffffffffffffff5d576e7357a4501ddfe92f46681b20a0821115610e615760009350505050610edf565b8060ff16601b14158015610e7957508060ff16601c14155b15610e8a5760009350505050610edf565b60018682858560405160008152602001604052604051610ead94939291906117ee565b6020604051602081039080840390855afa158015610ecf573d6000803e3d6000fd5b5050506020604051035193505050505b92915050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610f2657805160ff1916838001178555610f53565b82800160010185558215610f53579182015b82811115610f53578251825591602001919060010190610f38565b50610f5f929150610f63565b5090565b61091791905b80821115610f5f5760008155600101610f69565b8051610edf816119ba565b60008083601f840112610f9a57600080fd5b5081356001600160401b03811115610fb157600080fd5b602083019150836020820283011115610fc957600080fd5b9250929050565b600082601f830112610fe157600080fd5b8135610ff4610fef826118cf565b6118a9565b81815260209384019390925082018360005b83811015611032578135860161101c8882611088565b8452506020928301929190910190600101611006565b5050505092915050565b8051610edf816119ce565b60008083601f84011261105957600080fd5b5081356001600160401b0381111561107057600080fd5b602083019150836001820283011115610fc957600080fd5b600082601f83011261109957600080fd5b81356110a7610fef826118ef565b915080825260208301602083018583830111156110c357600080fd5b6110ce838284611961565b50505092915050565b8035610edf816119d7565b6000602082840312156110f457600080fd5b60006111008484610f7d565b949350505050565b60008060006040848603121561111d57600080fd5b83356001600160401b0381111561113357600080fd5b61113f86828701610f88565b93509350506020611152868287016110d7565b9150509250925092565b60008060006060848603121561117157600080fd5b83356001600160401b0381111561118757600080fd5b61119386828701610fd0565b93505060208401356001600160401b038111156111af57600080fd5b6111bb86828701610fd0565b9250506040611152868287016110d7565b600080600080608085870312156111e257600080fd5b84356001600160401b038111156111f857600080fd5b61120487828801610fd0565b94505060208501356001600160401b0381111561122057600080fd5b61122c87828801610fd0565b935050604061123d878288016110d7565b92505060608501356001600160401b0381111561125957600080fd5b61126587828801611088565b91505092959194509250565b60006020828403121561128357600080fd5b6000611100848461103c565b6000806000806000606086880312156112a757600080fd5b85356001600160401b038111156112bd57600080fd5b6112c988828901611047565b955095505060208601356001600160401b038111156112e757600080fd5b6112f388828901611047565b93509350506040611306888289016110d7565b9150509295509295909350565b60008060008060008060006080888a03121561132e57600080fd5b87356001600160401b0381111561134457600080fd5b6113508a828b01611047565b975097505060208801356001600160401b0381111561136e57600080fd5b61137a8a828b01611047565b9550955050604061138d8a828b016110d7565b93505060608801356001600160401b038111156113a957600080fd5b6113b58a828b01611047565b925092505092959891949750929550565b600080604083850312156113d957600080fd5b82356001600160401b038111156113ef57600080fd5b6113fb85828601611088565b925050602061140c858286016110d7565b9150509250929050565b60006020828403121561142857600080fd5b600061110084846110d7565b60008060006040848603121561144957600080fd5b600061145586866110d7565b93505060208401356001600160401b0381111561147157600080fd5b61147d86828701611047565b92509250509250925092565b6000806040838503121561149c57600080fd5b60006113fb85856110d7565b600080600080606085870312156114be57600080fd5b60006114ca87876110d7565b94505060206114db878288016110d7565b93505060408501356001600160401b038111156114f757600080fd5b61150387828801611047565b95989497509550505050565b600061151b838361161e565b9392505050565b61152b81611950565b82525050565b61152b8161192e565b61152b6115468261192e565b611999565b60006115568261191c565b6115608185611920565b93508360208202850161157285611916565b8060005b858110156115ac578484038952815161158f858261150f565b945061159a83611916565b60209a909a0199925050600101611576565b5091979650505050505050565b61152b81610917565b61152b6115ce82610917565b610917565b60006115df8385611920565b93506115ec838584611961565b6115f5836119aa565b9093019392505050565b600061160b8385611929565b9350611618838584611961565b50500190565b60006116298261191c565b6116338185611920565b935061164381856020860161196d565b6115f5816119aa565b60006116578261191c565b6116618185611929565b935061167181856020860161196d565b9290920192915050565b6000611688601c83611929565b7f19457468657265756d205369676e6564204d6573736167653a0a3332000000008152601c0192915050565b60006116c1602383611920565b7f53696d706c655265736f6c7665723a206973206e6f7420746865207265736f6c8152623b32b960e91b602082015260400192915050565b61152b8161194a565b600061170e82866115c2565b60208201915061171e828561153a565b60148201915061172e82846115c2565b506020019392505050565b60006111008284866115ff565b600061151b828461164c565b600061175d8261167b565b915061176982846115c2565b50602001919050565b60208101610edf8284611531565b6040810161178e8285611522565b61151b60208301846115b9565b6040810161178e8285611531565b6020808252810161151b818461154b565b606080825281016117cb818661154b565b905081810360208301526117df818561154b565b905061110060408301846115b9565b608081016117fc82876115b9565b61180960208301866116f9565b61181660408301856115b9565b61182360608301846115b9565b95945050505050565b6060808252810161183e8187896115d3565b905081810360208301526118538185876115d3565b905061186260408301846115b9565b9695505050505050565b6020808252810161151b818461161e565b60208082528101610edf816116b4565b60208101610edf82846115b9565b6040810161178e82856115b9565b6040518181016001600160401b03811182821017156118c757600080fd5b604052919050565b60006001600160401b038211156118e557600080fd5b5060209081020190565b60006001600160401b0382111561190557600080fd5b506020601f91909101601f19160190565b60200190565b5190565b90815260200190565b919050565b6000610edf8261193e565b151590565b6001600160a01b031690565b60ff1690565b6000610edf826000610edf8261192e565b82818337506000910152565b60005b83811015611988578181015183820152602001611970565b838111156107c55750506000910152565b6000610edf826000610edf826119b4565b601f01601f191690565b60601b90565b6119c38161192e565b811461069257600080fd5b6119c381611939565b6119c38161091756fea365627a7a7231582095c236bb08d6c28ac249309622ed4f27245cd933140c6b7fdf9ef20e10e2908c6c6578706572696d656e74616cf564736f6c634300050c0040000000000000000000000000d1e5b0ff1287aa9f9a268759062e4ab08b9dacbe000000000000000000000000b0ee56339c3253361730f50c08d3d7817ecd60ca\n";

    public static final String FUNC_GET = "get";

    public static final String FUNC_GETMANY = "getMany";

    public static final String FUNC_NONCEOF = "nonceOf";

    public static final String FUNC_PRECONFIGURE = "preconfigure";

    public static final String FUNC_PRESETOF = "presetOf";

    public static final String FUNC_REGISTRY = "registry";

    public static final String FUNC_RESET = "reset";

    public static final String FUNC_RESETFOR = "resetFor";

    public static final String FUNC_SET = "set";

    public static final String FUNC_SETFOR = "setFor";

    public static final String FUNC_SETMANY = "setMany";

    public static final String FUNC_SETMANYFOR = "setManyFor";

    public static final String FUNC_SETPRESET = "setPreset";

    public static final String FUNC_SETPRESETFOR = "setPresetFor";

    public static final Event SET_EVENT = new Event("Set", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Utf8String>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>(true) {}));
    ;

    public static final Event SETPRESET_EVENT = new Event("SetPreset", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Uint256>(true) {}));
    ;

    @Deprecated
    protected Cryptoresolver(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Cryptoresolver(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Cryptoresolver(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Cryptoresolver(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<SetEventResponse> getSetEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SET_EVENT, transactionReceipt);
        ArrayList<SetEventResponse> responses = new ArrayList<SetEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SetEventResponse typedResponse = new SetEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.preset = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.key = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.value = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SetEventResponse> setEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, SetEventResponse>() {
            @Override
            public SetEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SET_EVENT, log);
                SetEventResponse typedResponse = new SetEventResponse();
                typedResponse.log = log;
                typedResponse.preset = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.key = (byte[]) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
                typedResponse.value = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<SetEventResponse> setEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SET_EVENT));
        return setEventFlowable(filter);
    }

    public List<SetPresetEventResponse> getSetPresetEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SETPRESET_EVENT, transactionReceipt);
        ArrayList<SetPresetEventResponse> responses = new ArrayList<SetPresetEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SetPresetEventResponse typedResponse = new SetPresetEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.preset = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SetPresetEventResponse> setPresetEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, SetPresetEventResponse>() {
            @Override
            public SetPresetEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SETPRESET_EVENT, log);
                SetPresetEventResponse typedResponse = new SetPresetEventResponse();
                typedResponse.log = log;
                typedResponse.preset = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<SetPresetEventResponse> setPresetEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SETPRESET_EVENT));
        return setPresetEventFlowable(filter);
    }

    public RemoteFunctionCall<String> get(String key, BigInteger tokenId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GET, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(key), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<List> getMany(List<String> keys, BigInteger tokenId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETMANY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Utf8String>(
                        org.web3j.abi.datatypes.Utf8String.class,
                        org.web3j.abi.Utils.typeMap(keys, org.web3j.abi.datatypes.Utf8String.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Utf8String>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> nonceOf(BigInteger tokenId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_NONCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> preconfigure(List<String> keys, List<String> values, BigInteger tokenId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_PRECONFIGURE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Utf8String>(
                        org.web3j.abi.datatypes.Utf8String.class,
                        org.web3j.abi.Utils.typeMap(keys, org.web3j.abi.datatypes.Utf8String.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Utf8String>(
                        org.web3j.abi.datatypes.Utf8String.class,
                        org.web3j.abi.Utils.typeMap(values, org.web3j.abi.datatypes.Utf8String.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> presetOf(BigInteger tokenId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_PRESETOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> registry() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_REGISTRY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> reset(BigInteger tokenId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RESET, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> resetFor(BigInteger tokenId, byte[] signature) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RESETFOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId), 
                new org.web3j.abi.datatypes.DynamicBytes(signature)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> set(String key, String value, BigInteger tokenId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SET, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(key), 
                new org.web3j.abi.datatypes.Utf8String(value), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setFor(String key, String value, BigInteger tokenId, byte[] signature) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETFOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(key), 
                new org.web3j.abi.datatypes.Utf8String(value), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId), 
                new org.web3j.abi.datatypes.DynamicBytes(signature)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setMany(List<String> keys, List<String> values, BigInteger tokenId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETMANY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Utf8String>(
                        org.web3j.abi.datatypes.Utf8String.class,
                        org.web3j.abi.Utils.typeMap(keys, org.web3j.abi.datatypes.Utf8String.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Utf8String>(
                        org.web3j.abi.datatypes.Utf8String.class,
                        org.web3j.abi.Utils.typeMap(values, org.web3j.abi.datatypes.Utf8String.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setManyFor(List<String> keys, List<String> values, BigInteger tokenId, byte[] signature) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETMANYFOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Utf8String>(
                        org.web3j.abi.datatypes.Utf8String.class,
                        org.web3j.abi.Utils.typeMap(keys, org.web3j.abi.datatypes.Utf8String.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Utf8String>(
                        org.web3j.abi.datatypes.Utf8String.class,
                        org.web3j.abi.Utils.typeMap(values, org.web3j.abi.datatypes.Utf8String.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId), 
                new org.web3j.abi.datatypes.DynamicBytes(signature)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setPreset(BigInteger presetId, BigInteger tokenId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETPRESET, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(presetId), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setPresetFor(BigInteger presetId, BigInteger tokenId, byte[] signature) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETPRESETFOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(presetId), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId), 
                new org.web3j.abi.datatypes.DynamicBytes(signature)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static Cryptoresolver load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Cryptoresolver(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Cryptoresolver load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Cryptoresolver(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Cryptoresolver load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Cryptoresolver(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Cryptoresolver load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Cryptoresolver(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Cryptoresolver> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String registry, String mintingController) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, registry), 
                new org.web3j.abi.datatypes.Address(160, mintingController)));
        return deployRemoteCall(Cryptoresolver.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<Cryptoresolver> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String registry, String mintingController) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, registry), 
                new org.web3j.abi.datatypes.Address(160, mintingController)));
        return deployRemoteCall(Cryptoresolver.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Cryptoresolver> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String registry, String mintingController) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, registry), 
                new org.web3j.abi.datatypes.Address(160, mintingController)));
        return deployRemoteCall(Cryptoresolver.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Cryptoresolver> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String registry, String mintingController) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, registry), 
                new org.web3j.abi.datatypes.Address(160, mintingController)));
        return deployRemoteCall(Cryptoresolver.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static class SetEventResponse extends BaseEventResponse {
        public BigInteger preset;

        public byte[] key;

        public BigInteger tokenId;

        public String value;
    }

    public static class SetPresetEventResponse extends BaseEventResponse {
        public BigInteger preset;

        public BigInteger tokenId;
    }
}

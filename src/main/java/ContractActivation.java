import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.web3j.tx.Contract.BIN_NOT_PROVIDED;

public class ContractActivation {

    private static final String url = ""; //URL to Ethereum node
    private static final String privateKey = ""; //Ethereum private key
    private static final String pathToProof = "proof.json"; //Path to proof in JSON format
    private static final String contractAddress = ""; //Contract address

    public static void main(String[] args) throws Exception {
        Web3jService service = new HttpService(url);
        Web3j web3j = Web3j.build(service);

        Credentials credentials = Credentials.create(ECKeyPair.create(new BigInteger(privateKey, 16)));

        ObjectMapper objectMapper = new ObjectMapper();
        ProofDTO proofs = objectMapper.readValue(ContractActivation.class.getClassLoader().getResourceAsStream(pathToProof), ProofDTO.class);

        Bytes32 proof = new Bytes32(DatatypeConverter.parseHexBinary(proofs.hash));
        List<Uint8> v = new ArrayList<>();
        List<Bytes32> r = new ArrayList<>();
        List<Bytes32> s = new ArrayList<>();
        for (ProofDTO.SignatureDTO signature : proofs.signatures) {
            v.add(new Uint8(new BigInteger(signature.v, 16)));
            r.add(new Bytes32(DatatypeConverter.parseHexBinary(signature.r)));
            s.add(new Bytes32(DatatypeConverter.parseHexBinary(signature.s)));
        }


        Function function = new Function(
                "submitProof",
                Arrays.asList(proof, new DynamicArray(v), new DynamicArray(r), new DynamicArray(s)),
                Collections.emptyList()
        );


        TransactionManager transactionManager = new TransactionManager(
                BIN_NOT_PROVIDED,
                contractAddress,
                web3j,
                credentials,
                new DefaultGasProvider()
        );
        RemoteCall<TransactionReceipt> transaction = transactionManager.activate(function);
        String hash = transaction.send().getTransactionHash();
        System.out.println(hash);
    }

    private static class TransactionManager extends Contract {


        public TransactionManager(String contractBinary, String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider gasProvider) {
            super(contractBinary, contractAddress, web3j, credentials, gasProvider);
        }

        public RemoteCall<TransactionReceipt> activate(Function function) {
            return executeRemoteCallTransaction(function);
        }
    }

    private static class ProofDTO {

        @JsonProperty("status")
        StatusDTO status;

        @JsonProperty("hash")
        String hash;

        @JsonProperty("signatures")
        List<SignatureDTO> signatures;

        private static class StatusDTO {

            @JsonProperty("code")
            String code;

            @JsonProperty("message")
            String message;
        }

        private static class SignatureDTO {

            @JsonProperty("v")
            String v;

            @JsonProperty("r")
            String r;

            @JsonProperty("s")
            String s;
        }
    }
}

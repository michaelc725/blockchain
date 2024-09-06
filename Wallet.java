
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.HashMap;
import java.util.Map;

public class Wallet
{
    public PrivateKey privateKey;
    public PublicKey publicKey;

    public HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();



    public Wallet()
    {
        generateKeyPair();
    }

    public void generateKeyPair()
    {
        try
        {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            //Initialize they key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random); // 256 bytes provide an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the KeyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();


        }
        catch(Exception e)
        {
            throw new RuntimeException(e);


        }
    }
    //returns balance and stores the UTXO's owned by this wallet in this.UTXOs
    public float getBalance()
    {
        float total = 0;
        for(Map.Entry<String, TransactionOutput> item: Chain.UTXOs.entrySet())
        {
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)) { //if output belongs to me ( if coins belong to me )
                UTXOs.put(UTXO.id,UTXO); //add it to out list of unspent transactions.
                total += UTXO.value;

            }
        }
        return total;
    }

    //Generates and returns a new transaction from this wallet.
    public Transaction sendFunds(PublicKey _recipient, float value)
    {
        
    }
}

import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction
{
    public String transactionId; // also is the hash of the transaction
    public PublicKey sender; // senders address/public key
    public PublicKey recipient; // recipient address/public key
    public float value;
    public byte[] signature; // this is to prevent anybody else from spending funds in our wallet

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0; // rough count of how many transactions have been generated.

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs)
    {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    // This calculates the transaction hash (which will be used as its Id)

    private String calculateHash()
    {
        sequence++; // increase the sequence to avoid 2 identical transactions having the same hash
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient) +
                        Float.toString(value) + sequence
        );
    }

}

import java.security.PrivateKey;
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

    // Signs all the data that we do not wish to be tampered with.
    public void generateSignature(PrivateKey privateKey)
    {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey, data);

    }

    // Verifies the data that we signed has not been tampered with.
    public boolean verifySignature()
    {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    public boolean processTransaction()
    {
        if(verifySignature() == false)
        {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        //gather transaction inputs (Make sure they are unspent):
        for(TransactionInput i : inputs)
        {
            i.UTXO = Chain.UTXOs.get(i.transactionOutputId);

        }

        //check if transaction is valid;
        if(getInputsValue() < Chain.minimumTransaction)
        {
            System.out.println("#Transaction Signature failed to small: " + getInputsValue());
            return false;
        }

        //generate transaction inputs
        float leftOver = getInputsValue() - value; //get value inputs then the left over change:
        transactionId = calculateHash();
        outputs.add(new TransactionOutput( this.recipient, value, transactionId));
        outputs.add(new TransactionOutput( this.sender, leftOver, transactionId));

        //add outputs to Unspent list
        for(TransactionOutput o : outputs)
        {
            Chain.UTXOs.put(o.id , o);
        }

        //remove transaction inputs from UTXO lists as spent:
        for (TransactionInput i : inputs )
        {
            if(i.UTXO == null) continue; //if Transaction can't be found skip it
            Chain.UTXOs.remove(i.UTXO.id);
        }
        return true;
    }

    //returns sum of inputs(UTXOs) values
    public float getInputsValue()
    {
        float total = 0;
        for(TransactionInput i : inputs)
        {
            if(i.UTXO == null) continue; //if Transaction can't be found skip it
            total += i.UTXO.value;
        }
        return total;
    }

    //returns sum of outputs:
    public float getOutputsValue()
    {
        float total = 0;
        for(TransactionOutput o : outputs)
        {
            total += o.value;
        }
        return total;
    }

}

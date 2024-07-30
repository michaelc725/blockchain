import com.google.gson.*;
import java.util.ArrayList;


public class Chain {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();

    public static void main(String[] args) {

        blockchain.add(new Block("Hello I am first block", "0"));
        blockchain.add(new Block("Hello I am second block", blockchain.get(blockchain.size()-1).hash));
        blockchain.add(new Block("Hello I am third block", blockchain.get(blockchain.size()-1).hash));

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainJson);

    }
}

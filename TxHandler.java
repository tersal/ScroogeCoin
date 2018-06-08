public class TxHandler {
	
	UTXOPool myUTXOPool;
	
	/**
	 * Created a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
	 * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
	 * constructor.
	 */
	 public TxHandler(UTXOPool utxoPool) {
		this.myUTXOPool = new UTXOPool(utxoPool);
	 }
	 
	 /**
	 * @return true if:
	 * (1) all outputs claimed by {@code tx} are in the current UTXO pool,
	 * (2) the signatures on each input of {@code tx} are valid,
	 * (3) no UTXO is claimed multiple times by {@code tx},
	 * (4) all of {@code tx}s output values are non-negative, and
	 * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
	 *     values; and false otherwise.
	 */
	 public boolean isValidTx(Transaction tx) {
		 
		 // Iterate all the inputs
		 for(int i = 0; i < tx.numInputs; i++) {
			 Transaction.Input txInput = tx.getInput(i);
			 Transaction.Output utxoOutput;
			 
			 // Create an UTXO of the Input
			 UTXO currentUTXO = new UTXO(txInput.prevTxHash, txInput.outputIndex);
			 
			 // (1) Check if the UTXO is contained in the current UTXOPool, return false if not
			 if (!myUTXOPool.contains(currentUTXO)) {
				 return false;
			 }
 
			 // (2) Check if the public key of the UTXO corresponds to the signature of the message in the input
			 utxoOutput = myUTXOPool.getTxOutput(currentUTXO);
			 if(!Crypto.verifySignature(utxoOutput.address, tx.getRawDataToSign(i), txInput.signature)) {
				 return false
			 }
			 
			 
			 
			 
		 }
		 
		 
	 }
	 
	 /**
	  * Handles each epoch by receiving an unordered array of proposed transactions, checking each
	  * transaction for correctness, returning a mutually valid array of accepted transactions, and
	  * updating the current UTXO pool as appropiate.
	  */
	  public Transaction[] handleTxs(Transaction[] possibleTxs) {
		  // IMPLEMENT THIS
	  }
}
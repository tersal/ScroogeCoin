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
		 UTXOPool usedUTXO = new UTXOPool;
		 double sumOutUTXO = 0;
		 double sumOut = 0;
		 
		 // Iterate all the inputs
		 for(int i = 0; i < tx.numInputs(); i++) {
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
			 
			 // (3) Check if the input was not already used, return false if used and if not, add it to te local "spent" pool
			 if(usedUTXO.contains(currentUTXO)) {
				 return false;
			 }
			 usedUTXO.addUTXO(currentUTXO, utxoOutput);
			 
			 // Increase the total value of the used outputs
			 sumOutUTXO += utxoOutput.value;
		 }
		 
		 // (4) Check if all the outputs of the transactions have values larger than one
		 // and also get the total value of all
		 for (int i = 0; i < tx.numOutputs(); i++) {
			 Transaction.Output out = tx.getOutput(i);
			 
			 if (out.value < 0) {
				 return false;
			 }
			 
			 sumOut += out.value;
		 }
		 
		 // (5) Validate if the sum of the UTXOs used for the inputs is larger or equal than the outputs
		 // of this transaction
		 if (sumOut > sumOutUTXO) {
			 return false;
		 }
		 
		 return true;
	 }
	 
	 /**
	  * Handles each epoch by receiving an unordered array of proposed transactions, checking each
	  * transaction for correctness, returning a mutually valid array of accepted transactions, and
	  * updating the current UTXO pool as appropriate.
	  */
	  public Transaction[] handleTxs(Transaction[] possibleTxs) {
		  ArrayList<Transaction> validTxs = new ArrayList<Transaction>():
		  
		  for (int i = 0; i < possibleTxs.length; i++) {
			  if (isValidTx(possibleTxs[i])) {
				  // If transaction is valid, then we add it to the list.
				  validTxs.add(possibleTxs[i]);
				  
				  // Remove the spent outputs from the UTXO pool
				  for (int j = 0; j < possibleTxs[i].numInputs(); j++) {
					  Transaction.Input input = possibleTxs[i].getInput(i);
					  UTXO spentUTXO = new UTXO(input.prevTxHash, input.outputIndex);
					  myUTXOPool.removeUTXO(spentUTXO);
				  }
				  
				  // Add new outputs from the current transaction
				  for (int j = 0; j < possibleTxs[i].numOutputs(); j++) {
					  Transaction.Output output = possibeTxs[i].getOutput(i);
					  UTXO newUTXO = new UTXO(possibleTxs[i].getHash(), i);
					  myUTXOPool.addUTXO(newUTXO, output);
				  }
			  }
		  }
		  
		  Transaction[] transactions = new Transaction[validTxs.size()];
		  transactions = validTxs.toArray(transactions);
		  
		  return transactions;
	  }
}
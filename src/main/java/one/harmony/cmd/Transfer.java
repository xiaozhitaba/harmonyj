package one.harmony.cmd;

import java.util.List;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import one.harmony.account.Account;
import one.harmony.account.Address;
import one.harmony.keys.Store;
import one.harmony.rpc.ShardingStructure.RPCRoutes;
import one.harmony.sharding.Sharding;
import one.harmony.transaction.Handler;

/**
 * Transfer class provides api for transfering funds between Harmony accounts.
 * 
 * @author gupadhyaya
 *
 */
public class Transfer {

	private final String from;
	private final String to;
	private final String amount;
	private final long gasPrice;
	private final int fromShard;
	private final int toShard;
	private final String data;

	/**
	 * Transfers default amount (0.0) using two Harmony addresses between same
	 * shards (shard 0)
	 * 
	 * @param from
	 * @param to
	 */
	public Transfer(String from, String to) {
		this.from = from;
		this.to = to;
		this.amount = "0";
		this.gasPrice = 1;
		this.fromShard = 0;
		this.toShard = 0;
		this.data = "";
	}

	/**
	 * Transfers the specified amount using two Harmony addresses
	 * 
	 * @param from
	 * @param to
	 * @param amount
	 */
	public Transfer(String from, String to, String amount) {
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.gasPrice = 1;
		this.fromShard = 0;
		this.toShard = 0;
		this.data = "";
	}

	/**
	 * Transfer the provided amount using the specified gasPrice
	 * 
	 * @param from
	 * @param to
	 * @param amount
	 * @param gasPrice
	 */
	public Transfer(String from, String to, String amount, long gasPrice) {
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.gasPrice = gasPrice;
		this.fromShard = 0;
		this.toShard = 0;
		this.data = "";
	}

	/**
	 * Transfer the specified amount between Harmony addresses using the specified
	 * shards
	 * 
	 * @param from
	 * @param to
	 * @param amount
	 * @param fromShard
	 * @param toShard
	 */
	public Transfer(String from, String to, String amount, int fromShard, int toShard) {
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.gasPrice = 1;
		this.fromShard = fromShard;
		this.toShard = toShard;
		this.data = "";
	}

	/**
	 * Transfer the specified amount between Harmony addresses using the specified
	 * shards and payload
	 * 
	 * @param from
	 * @param to
	 * @param amount
	 * @param fromShard
	 * @param toShard
	 * @param data
	 */
	public Transfer(String from, String to, String amount, int fromShard, int toShard, String data) {
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.gasPrice = 1;
		this.fromShard = fromShard;
		this.toShard = toShard;
		this.data = data;
	}

	/**
	 * Transfer the specified amount using the specified gas price and shards
	 * 
	 * @param from
	 * @param to
	 * @param amount
	 * @param gasPrice
	 * @param fromShard
	 * @param toShard
	 */
	public Transfer(String from, String to, String amount, long gasPrice, int fromShard, int toShard) {
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.gasPrice = gasPrice;
		this.fromShard = fromShard;
		this.toShard = toShard;
		this.data = "";
	}

	/**
	 * 
	 * @param from
	 * @param to
	 * @param amount
	 * @param gasPrice
	 * @param fromShard
	 * @param toShard
	 * @param data
	 */
	public Transfer(String from, String to, String amount, long gasPrice, int fromShard, int toShard, String data) {
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.gasPrice = gasPrice;
		this.fromShard = fromShard;
		this.toShard = toShard;
		this.data = data;
	}

	/**
	 * Method to execute the transfer.
	 * 
	 * @param chainID       represents which chain id to target
	 * @param passphrase    to unlock sender's keystore
	 * @param dryRun        does not send signed transaction
	 * @param waitToConfirm only waits if non-zero value, in seconds
	 * @return transaction hash
	 * @throws Exception
	 */
	public String execute(int chainID, String passphrase, boolean dryRun, int waitToConfirmTime) throws Exception {
		List<RPCRoutes> shards = Sharding.getShardingStructure();
		if (!Sharding.validateShardIDs(this.fromShard, this.toShard, shards.size())) {
			throw new IllegalArgumentException("Invalid shard ids passed");
		}

		String url = Sharding.getHandlerFor(shards, this.fromShard);
		String accountName = Store.getAccountNameFromAddress(this.from);
		boolean isHex = false;
		Address address = new Address(this.from, isHex);
		WalletFile walletFile = Store.extractWalletFileFromAddress(this.from);
		Credentials credentials = Credentials.create(Wallet.decrypt(passphrase, walletFile));
		Account account = new Account(accountName, address, credentials, walletFile);

		return new Handler(account, url).execute(chainID, this.to, this.data, this.amount, this.gasPrice,
				this.fromShard, this.toShard, dryRun, waitToConfirmTime);

	}
}

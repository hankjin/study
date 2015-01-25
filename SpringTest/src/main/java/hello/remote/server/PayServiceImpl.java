package hello.remote.server;

import hello.remote.share.PayService;

public class PayServiceImpl implements PayService {

	public String pay(String from, String to, int amount) {
		return from + " pay " + to + " " + amount;
	}

}

package ekrut.server.intefaces;

public class StubPaymentProcessor implements IPaymentProcessor {

	@Override
	public boolean submitPayment(String creditCard, float debitAmount) {
		return true;
	}

	@Override
	public boolean addToCharges(String creditCard, float debitAmount) {
		return true;
	}

}

package ekrut.server.intefaces;

public interface IPaymentProcessor {

	boolean submitPayment(String creditCard, float debitAmount);
	boolean addToCharges(String creditCard, float debitAmount);
}

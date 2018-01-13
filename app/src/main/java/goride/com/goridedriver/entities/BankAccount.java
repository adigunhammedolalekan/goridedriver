package goride.com.goridedriver.entities;

/**
 * Created by Lekan Adigun on 12/25/2017.
 */

public class BankAccount {

    private String accountName = "";
    private String accountNumber = "";
    private String bankName = "";

    public BankAccount(String accountName, String accountNumber, String bankName) {
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.bankName = bankName;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public BankAccount() {}
}

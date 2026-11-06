package atm;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import atm.dispatcher.MessageDispatcher;
import atm.exceptions.InvalidAmountException;
import atm.ui.panels.MainPanel;
import atm.utils.CredentialsCheck;
import atm.utils.FormatChecker;
import bank.transactions.utils.AccountType;
import bank.transactions.utils.TransactionResult;
import bank.transactions.utils.TransactionType;

@ExtendWith(MockitoExtension.class)
public class ATMAmountTest {

    /*
      - Robust Worst Case Boundary Value Analysis (spec: Session Withdrawal Amount)
         - 1 variable, 7 BVA values + 2 extra,
             - daily limit is fixed at $1000
             - with one variable, robust worst case is the same as robust BVA: 7 points, plus 2 extra.
             - amount values: min−1 = 0, min = 20, min+1 = 40, nom = 500, max−1 = 990, max = 1000, max+1 = 1010, extra = -20, 1020
         - $5,000 account simulated by stubbing performTransaction, with balance $5,000 minus the amount
         - increments of 20 and 50 are used, as these are the only valid bill amounts (from assignment description)
         - tested through atm.setAmount(), the path the UI uses into Session
         - pin uses a length of 4, this is what code expects, spec says 5 (change when code is updated)
         - known defects:
             - TWA_CASE_09: no limit check, so the amount passes CashValidator, the unstubbed transaction returns null, and a NullPointerException is thrown instead of InvalidAmountException
             - TWA_CASE_08: other compositions of $20 and $50 are not considered,
                 - $1,010 (19 × $50 + 3 × $20) is wrongly rejected, so CASE_08 passes for the wrong reason and masks the missing limit check
             - TWA_CASE_01, CASE_02: no zero/negative check, so the amount passes CashValidator, the unstubbed transaction returns null, and a NullPointerException is thrown instead of InvalidAmountException
     */

    ATM atm;
    MessageDispatcher dispatcher;
    FormatChecker formatCheck;
    String cardNumber;
    char[] pinNumber;

    @BeforeEach
    public void setUp() throws Exception {
        dispatcher = Mockito.mock(MessageDispatcher.class);
        formatCheck = new FormatChecker();
        CredentialsCheck credentialsCheck = new CredentialsCheck(dispatcher);
        atm = new ATM(formatCheck, credentialsCheck, dispatcher);

        MainPanel mainPanel = Mockito.mock(MainPanel.class);
        atm.setMainPanel(mainPanel);
        atm.createSession();

        cardNumber = "4111-1111-1111-1111";
        pinNumber = new char[] { '4', '4', '4', '4' };
        atm.checkCardNumber(cardNumber);
        // Mock the dispatcher's correct behaviour
        Mockito.when(dispatcher.checkCredentials(cardNumber, pinNumber)).thenReturn(true);
        // Use 4-digit pin since this is what the code currently accepts
        atm.checkPin(pinNumber);
        atm.setTransaction(TransactionType.Withdrawal);
        atm.setFromAccount(AccountType.Chequing);

    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    //Should be rejected with InvalidAmountException.
    //But since the CashValidator lets it through a NullPointerException is thrown since there is no stub for the test
    @Test
    @DisplayName("TWA_CASE_01 [extra]: amount=-20 -> rejected")
    public void amountNegative20() {
        Assertions.assertThrows(InvalidAmountException.class, () -> atm.setAmount(-20));
    }

    //Should be rejected with InvalidAmountException.
    //But since the CashValidator lets it through a NullPointerException is thrown since there is no stub for the test
    @Test
    @DisplayName("TWA_CASE_02 [min-1]: amount=0 -> rejected")
    public void amount0() {
        Assertions.assertThrows(InvalidAmountException.class, () -> atm.setAmount(0));
    }

    @Test
    @DisplayName("TWA_CASE_03 [min]: amount=20 -> accepted")
    public void amount20() {
        int amount = 20;
        stubSuccessfulWithdrawal(amount);
        Assertions.assertDoesNotThrow(() -> atm.setAmount(amount));
    }

    @Test
    @DisplayName("TWA_CASE_04 [min+1]: amount=40 -> accepted")
    public void amount40() {
        int amount = 40;
        stubSuccessfulWithdrawal(amount);
        Assertions.assertDoesNotThrow(() -> atm.setAmount(amount));
    }

    @Test
    @DisplayName("TWA_CASE_05 [nom]: amount=500 -> accepted")
    public void amount500() {
        int amount = 500;
        stubSuccessfulWithdrawal(amount);
        Assertions.assertDoesNotThrow(() -> atm.setAmount(amount));
    }

    @Test
    @DisplayName("TWA_CASE_06 [max-1]: amount=990 -> accepted")
    public void amount990() {
        int amount = 990;
        stubSuccessfulWithdrawal(amount);
        Assertions.assertDoesNotThrow(() -> atm.setAmount(amount));
    }

    @Test
    @DisplayName("TWA_CASE_07 [max]: amount=1000 -> accepted")
    public void amount1000() {
        int amount = 1000;
        stubSuccessfulWithdrawal(amount);
        Assertions.assertDoesNotThrow(() -> atm.setAmount(amount));
    }

    //Should be rejected with InvalidAmountException which is correct but for the wrong reason.
    //It is rejected because of the way the number of each bill is calculated, mod 50 then 20 calls this invalid not because of the daily limit
    //When a valid composition of bills is 50x19 and 20x3
    @Test
    @DisplayName("TWA_CASE_08 [max+1]: amount=1010 -> rejected")
    public void amount1010() {
        Assertions.assertThrows(InvalidAmountException.class, () -> atm.setAmount(1010));
    }

    //Should be rejected with InvalidAmountException.
    //But since there is no daily limit check the CashValidator lets it through
    //A NullPointerException is thrown since there is no stub for the test.
    @Test
    @DisplayName("TWA_CASE_09 [extra]: amount=1020 -> rejected")
    public void amount1020() {
        Assertions.assertThrows(InvalidAmountException.class, () -> atm.setAmount(1020));
    }

    //Stubs the bank to accept the withdrawal from a $5,000 account
    private void stubSuccessfulWithdrawal(int amount) {
        Mockito.when(dispatcher.performTransaction(ArgumentMatchers.eq(cardNumber), AdditionalMatchers.aryEq(pinNumber),
            ArgumentMatchers.any())).thenReturn(new TransactionResult(true, "", 0.0, new double[] { 5000 - amount }));
    }
}

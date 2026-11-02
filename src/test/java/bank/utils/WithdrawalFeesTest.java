package bank.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class WithdrawalFeesTest {

    /*  Robust Worst Case Boundary Value Analysis (spec: Withdrawal)
        - 7 values per variable (min-, min, min+, nom, max-, max, max+), all combinations: 7^n
        - student is boolean (no boundaries), so it splits the tests into two groups,
          each crossing the two variables the spec makes the fee depend on:
            - students:     amount x dayOfWeek  = 7^2 = 49 cases (balance fixed at nominal 2500)
            - non-students: amount x balance    = 7^2 = 49 cases (dayOfWeek fixed at nominal 4, Wednesday)
        - amount (1 .. 1000, the daily limit):              0, 1, 2, 500, 999, 1000, 1001
        - dayOfWeek (Calendar: 1 = Sunday .. 7 = Saturday): 0, 1, 2, 4, 6, 7, 8
        - balance (0 .. 5000):                              -1, 0, 1, 2500, 4999, 5000, 5001
        - expected fees from the spec:
            - student, weekday: no fee / weekend: 0.1%
            - non-student, balance < 1000: 0.3% / 1000 <= balance < 5000: 0.1% / balance >= 5000: no fee
        - assumption: the spec does not define invalid-input behaviour, so we expect
          an IllegalArgumentException (ERR), same as the deposit tests
    */

    private static final double DELTA = 1e-6;
    private static final double NOMINAL_BALANCE = 2500.0;
    private static final int NOMINAL_DAY = 4;
    private FeesCalculator calculator;

    @BeforeEach
    public void setUp() {
        calculator = new FeesCalculator();
    }

    @ParameterizedTest(name = "{0}: student, amount = {1}, day = {2} -> {3}")
    @CsvSource({
        // amount min- (invalid)
        "W_S01, 0, 0, ERR",
        "W_S02, 0, 1, ERR",
        "W_S03, 0, 2, ERR",
        "W_S04, 0, 4, ERR",
        "W_S05, 0, 6, ERR",
        "W_S06, 0, 7, ERR",
        "W_S07, 0, 8, ERR",
        // amount min
        "W_S08, 1, 0, ERR",
        "W_S09, 1, 1, 0.001",
        "W_S10, 1, 2, 0.0",
        "W_S11, 1, 4, 0.0",
        "W_S12, 1, 6, 0.0",
        "W_S13, 1, 7, 0.001",
        "W_S14, 1, 8, ERR",
        // amount min+
        "W_S15, 2, 0, ERR",
        "W_S16, 2, 1, 0.002",
        "W_S17, 2, 2, 0.0",
        "W_S18, 2, 4, 0.0",
        "W_S19, 2, 6, 0.0",
        "W_S20, 2, 7, 0.002",
        "W_S21, 2, 8, ERR",
        // amount nom
        "W_S22, 500, 0, ERR",
        "W_S23, 500, 1, 0.5",
        "W_S24, 500, 2, 0.0",
        "W_S25, 500, 4, 0.0",
        "W_S26, 500, 6, 0.0",
        "W_S27, 500, 7, 0.5",
        "W_S28, 500, 8, ERR",
        // amount max-
        "W_S29, 999, 0, ERR",
        "W_S30, 999, 1, 0.999",
        "W_S31, 999, 2, 0.0",
        "W_S32, 999, 4, 0.0",
        "W_S33, 999, 6, 0.0",
        "W_S34, 999, 7, 0.999",
        "W_S35, 999, 8, ERR",
        // amount max
        "W_S36, 1000, 0, ERR",
        "W_S37, 1000, 1, 1.0",
        "W_S38, 1000, 2, 0.0",
        "W_S39, 1000, 4, 0.0",
        "W_S40, 1000, 6, 0.0",
        "W_S41, 1000, 7, 1.0",
        "W_S42, 1000, 8, ERR",
        // amount max+ (invalid)
        "W_S43, 1001, 0, ERR",
        "W_S44, 1001, 1, ERR",
        "W_S45, 1001, 2, ERR",
        "W_S46, 1001, 4, ERR",
        "W_S47, 1001, 6, ERR",
        "W_S48, 1001, 7, ERR",
        "W_S49, 1001, 8, ERR"
    })
    public void testStudentWithdrawalFee(String id, double amount, int dayOfWeek, String expected) {
        if ("ERR".equals(expected)) {
            assertThrows(IllegalArgumentException.class,
                    () -> calculator.calculateWithdrawalFee(amount, NOMINAL_BALANCE, true, dayOfWeek));
        } else {
            assertEquals(Double.parseDouble(expected),
                    calculator.calculateWithdrawalFee(amount, NOMINAL_BALANCE, true, dayOfWeek), DELTA);
        }
    }

    @ParameterizedTest(name = "{0}: non-student, amount = {1}, balance = {2} -> {3}")
    @CsvSource({
        // amount min- (invalid)
        "W_N01, 0, -1, ERR",
        "W_N02, 0, 0, ERR",
        "W_N03, 0, 1, ERR",
        "W_N04, 0, 2500, ERR",
        "W_N05, 0, 4999, ERR",
        "W_N06, 0, 5000, ERR",
        "W_N07, 0, 5001, ERR",
        // amount min
        "W_N08, 1, -1, ERR",
        "W_N09, 1, 0, 0.003",
        "W_N10, 1, 1, 0.003",
        "W_N11, 1, 2500, 0.001",
        "W_N12, 1, 4999, 0.001",
        "W_N13, 1, 5000, 0.0",
        "W_N14, 1, 5001, 0.0",
        // amount min+
        "W_N15, 2, -1, ERR",
        "W_N16, 2, 0, 0.006",
        "W_N17, 2, 1, 0.006",
        "W_N18, 2, 2500, 0.002",
        "W_N19, 2, 4999, 0.002",
        "W_N20, 2, 5000, 0.0",
        "W_N21, 2, 5001, 0.0",
        // amount nom
        "W_N22, 500, -1, ERR",
        "W_N23, 500, 0, 1.5",
        "W_N24, 500, 1, 1.5",
        "W_N25, 500, 2500, 0.5",
        "W_N26, 500, 4999, 0.5",
        "W_N27, 500, 5000, 0.0",
        "W_N28, 500, 5001, 0.0",
        // amount max-
        "W_N29, 999, -1, ERR",
        "W_N30, 999, 0, 2.997",
        "W_N31, 999, 1, 2.997",
        "W_N32, 999, 2500, 0.999",
        "W_N33, 999, 4999, 0.999",
        "W_N34, 999, 5000, 0.0",
        "W_N35, 999, 5001, 0.0",
        // amount max
        "W_N36, 1000, -1, ERR",
        "W_N37, 1000, 0, 3.0",
        "W_N38, 1000, 1, 3.0",
        "W_N39, 1000, 2500, 1.0",
        "W_N40, 1000, 4999, 1.0",
        "W_N41, 1000, 5000, 0.0",
        "W_N42, 1000, 5001, 0.0",
        // amount max+ (invalid)
        "W_N43, 1001, -1, ERR",
        "W_N44, 1001, 0, ERR",
        "W_N45, 1001, 1, ERR",
        "W_N46, 1001, 2500, ERR",
        "W_N47, 1001, 4999, ERR",
        "W_N48, 1001, 5000, ERR",
        "W_N49, 1001, 5001, ERR"
    })
    public void testNonStudentWithdrawalFee(String id, double amount, double balance, String expected) {
        if ("ERR".equals(expected)) {
            assertThrows(IllegalArgumentException.class,
                    () -> calculator.calculateWithdrawalFee(amount, balance, false, NOMINAL_DAY));
        } else {
            assertEquals(Double.parseDouble(expected),
                    calculator.calculateWithdrawalFee(amount, balance, false, NOMINAL_DAY), DELTA);
        }
    }
}

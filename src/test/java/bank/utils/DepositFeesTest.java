package bank.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DepositFeesTest {

    /*  Weak Robust Equivalence Class Analysis (spec: Deposit)
        - variables: student, amount deposited, account balance
        - valid classes: one test per fee rule (8), since thresholds differ per branch
        - invalid classes (one invalid value per test, others valid):
            - amount <= 0
            - balance < 0
        - assumption: the spec does not define invalid-input behaviour, so we expect
          an IllegalArgumentException
    */

    private static final double DELTA = 1e-4;
    private FeesCalculator calculator;

    @BeforeEach
    public void setUp() {
        calculator = new FeesCalculator();
    }

    // Students
    @Test
    @DisplayName("D_01: student, amount > 50, balance > 500 (0.5%)")
    public void testStudentLargeAmountHighBalance() {
        assertEquals(0.50, calculator.calculateDepositInterest(100.0, 1000.0, true), DELTA);
    }

    @Test
    @DisplayName("D_02: student, amount > 50, balance <= 500 (0.25%)")
    public void testStudentLargeAmountLowBalance() {
        assertEquals(0.25, calculator.calculateDepositInterest(100.0, 300.0, true), DELTA);
    }

    @Test
    @DisplayName("D_03: student, amount <= 50, balance > 5000 (0.5%)")
    public void testStudentSmallAmountHighBalance() {
        assertEquals(0.15, calculator.calculateDepositInterest(30.0, 6000.0, true), DELTA);
    }

    @Test
    @DisplayName("D_04: student, amount <= 50, balance <= 5000 (no fee)")
    public void testStudentSmallAmountLowBalance() {
        assertEquals(0.0, calculator.calculateDepositInterest(30.0, 1000.0, true), DELTA);
    }

    // Non Students
    @Test
    @DisplayName("D_05: non-student, amount > 250, balance > 2500 (0.8%)")
    public void testNonStudentLargeAmountHighBalance() {
        assertEquals(4.00, calculator.calculateDepositInterest(500.0, 5000.0, false), DELTA);
    }

    @Test
    @DisplayName("D_06: non-student, amount > 250, balance <= 2500 (0.4%)")
    public void testNonStudentLargeAmountLowBalance() {
        assertEquals(2.00, calculator.calculateDepositInterest(500.0, 1000.0, false), DELTA);
    }

    @Test
    @DisplayName("D_07: non-student, amount <= 250, balance > 10000 (no fee)")
    public void testNonStudentSmallAmountHighBalance() {
        assertEquals(0.0, calculator.calculateDepositInterest(100.0, 20000.0, false), DELTA);
    }

    @Test
    @DisplayName("D_08: non-student, amount <= 250, balance <= 10000 (0.1%)")
    public void testNonStudentSmallAmountLowBalance() {
        assertEquals(0.10, calculator.calculateDepositInterest(100.0, 5000.0, false), DELTA);
    }

    // Invalid
    @Test
    @DisplayName("D_09: invalid amount (negative), other inputs valid")
    public void testInvalidAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateDepositInterest(-100.0, 5000.0, false));
    }

    @Test
    @DisplayName("D_10: invalid balance (negative), other inputs valid")
    public void testInvalidBalance() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateDepositInterest(100.0, -500.0, false));
    }
}
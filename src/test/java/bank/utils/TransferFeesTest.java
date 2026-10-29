package bank.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class TransferFeesTest {

    /*  decision table analysis     
        - 4 condition vars = 2**4 = 16 rules
            - is student?
            - transfer amount 
                - for student: < 200 or >= 200
            - from balance
            - to balance
            - fee rates:
                - student: 0.1%, 0.05%, 0.05%, 0.025%, 0.005%, 0.025%, 0.25%, 0.125%
                - non-student: 0.2%, 0.1%, 1.0%, 0.5%, 0.2%, 0.1%, 0.5%, 0.25%
    */

    private static final double DELTA = 1e-4;
    private FeesCalculator calculator;

    @BeforeEach
    public void setUp() {
        calculator = new FeesCalculator();
    }
    
    // when students = true
    @Test
    @DisplayName("T_DT_01: Rule 1: student amount < 200, from < 2000, to < 1000 (0.1%)")
    public void testRule1(){
        double fee = calculator.calculateTransferFee(100.0, 1500.0, 500.0, true);
        assertEquals(0.10, fee, DELTA); 
    }

    @Test
    @DisplayName("T_DT_02: Rule 2; student amount  < 200, from < 2000, to >= 1000 (0.05%)")
    public void testRule2(){
        double fee = calculator.calculateTransferFee(100.0, 1500.0, 1500.0, true);
        assertEquals(0.05, fee, DELTA);
    }

    @Test
    @DisplayName("T_DT_03: Rule 3; student, amount < 200, from >= 2000, to < 1000 (0.05%)")
    public void testRule3(){
        double fee = calculator.calculateTransferFee(100.0, 2500.0, 500.0, true);
        assertEquals(0.05, fee, DELTA);
    }

    @Test
    @DisplayName("T_DT_04: Rule 4; student, amount < 200, from >= 2000, to >= 1000 (0.025%)")
    public void testRule4(){
        double fee = calculator.calculateTransferFee(100.0, 2500.0, 1500.0, true);
        assertEquals(0.025, fee, DELTA);
    }

    @Test
    @DisplayName("T_DT_05: Rule 5; student, amount >= 200, from < 2000, to < 1000 (0.005%)")
    public void testRule5(){
        double fee = calculator.calculateTransferFee(400.0, 1500.0, 500.0, true);
        assertEquals(0.2, fee, DELTA);
    }

    @Test
    @DisplayName("T_DT_06: Rule 6; student, amount >= 200, from < 2000, to >= 1000 (0.025%)")
    public void testRule6(){
        double fee = calculator.calculateTransferFee(400.0, 1500.0, 1500.0, true);
        assertEquals(0.10, fee, DELTA);
    }

    @Test
    @DisplayName("T_DT_07: Rule 7; student, amount >= 200, from >= 2000, to < 1000 (0.25%)")
    public void testRule7(){
        double fee = calculator.calculateTransferFee(400.0, 2500.0, 500.0, true);
        assertEquals(1.00, fee, DELTA);
    }
    
    @Test
    @DisplayName("T_DT_08: Rule 8; student, amount >= 200, from >= 2000, to >= 1000 (0.125%)")
    public void testRule8(){
        double fee = calculator.calculateTransferFee(400.0, 2500.0, 1500.0, true);
        assertEquals(0.50, fee, DELTA);
    }
// when student = false
    @Test
    @DisplayName("T_DT_09: Rule 9; Non-student, amount < 100, from < 4000, to <2000 (0.2%)")
    public void testRule9(){
        double fee = calculator.calculateTransferFee(50.0, 3000.0, 1500.0, false);
        assertEquals(0.10, fee, DELTA);
    }

    @Test
    @DisplayName("T_DT_10: Rule 10 - Non-Student, amount < 100, from < 4000, to >= 2000 (0.1%)")
    public void testRule10() {
        double fee = calculator.calculateTransferFee(50.0, 3000.0, 2500.0, false);
        assertEquals(0.05, fee, DELTA);
    }


    @Test
    @DisplayName("T_DT_11: Rule 11 - Non-Student, amount < 100, from >= 4000, to < 2000 (1.0%)")
    public void testRule11() {
        double fee = calculator.calculateTransferFee(50.0, 4500.0, 1500.0, false);
        assertEquals(0.50, fee, DELTA);
    }

    @Test
    @DisplayName("T_DT_12: Rule 12 - Non-Student, amount < 100, from >= 4000, to >= 2000 (0.5%)")
    public void testRule12() {
        double fee = calculator.calculateTransferFee(50.0, 4500.0, 2500.0, false);
        assertEquals(0.25, fee, DELTA);
    }
 
    @Test
    @DisplayName("T_DT_13: Rule 13 - Non-Student, amount >= 100, from < 2000, to < 1000 (0.2%)")
    public void testRule13() {
        double fee = calculator.calculateTransferFee(200.0, 1500.0, 500.0, false);
        assertEquals(0.40, fee, DELTA);
    }

    @Test
    @DisplayName("T_DT_14: Rule 14 - Non-Student, amount >= 100, from < 2000, to >= 1000 (0.1%)")
    public void testRule14() {
        double fee = calculator.calculateTransferFee(200.0, 1500.0, 1500.0, false);
        assertEquals(0.20, fee, DELTA);
    }

    @Test
    @DisplayName("T_DT_15: Rule 15 - Non-Student, amount >= 100, from >= 2000, to < 1000 (0.5%)")
    public void testRule15() {
        double fee = calculator.calculateTransferFee(200.0, 2500.0, 500.0, false);
        assertEquals(1.00, fee, DELTA);
    }

    @Test
    @DisplayName("T_DT_16: Rule 16 - Non-Student, amount >= 100, from >= 2000, to >= 1000 (0.25%)")
    public void testRule16() {
        double fee = calculator.calculateTransferFee(200.0, 2500.0, 1500.0, false);
        assertEquals(0.50, fee, DELTA);
    }
}

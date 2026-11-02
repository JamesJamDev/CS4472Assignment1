
# Assignment 1 Documentation - Black Box Testing

---

## Section 1 - Process and Assumptions


### Transfer Fee Calculation / Decision Table Analysis

#### Process

The specification defines four condition variables:
1. **$C_1$: Student Status (`student`)**: Boolean domain `{true, false}`. 
2. **$C_2$: Transfer Amount (`amount`)**:
- For students: partitioned at $\$200.00$ ($< \$200.00$ vs. $\ge \$200.00$).
- For non-students: partitioned at $\$100.00$ ($< \$100.00$ vs. $\ge \$100.00$).  
3. **$C_3$: Origin Account Balance (`fromAccountBalance`)**:
- For students (all amounts) and non-students ($\text{amount} \ge \$100.00$): partitioned at $\$2,000.00$ ($< \$2,000.00$ vs. $\ge \$2,000.00$).
- For non-students ($\text{amount} < \$100.00$): partitioned at $\$4,000.00$ ($< \$4,000.00$ vs. $\ge \$4,000.00$).
4. **$C_4$: Destination Account Balance (`toAccountBalance`)**:
- For students (all amounts) and non-students ($\text{amount} \ge \$100.00$): partitioned at $\$1,000.00$ ($< \$1,000.00$ vs. $\ge \$1,000.00$).
- For non-students ($\text{amount} < \$100.00$): partitioned at $\$2,000.00$ ($< \$2,000.00$ vs. $\ge \$2,000.00$).

With four condition stubs each admitting 2 mutally exclusive states, the truth matrix would be $2^4 = 16$ distinct rules ($R_1$ to $R_{16}$). Each rule will map to a single designated fee percentage rate action.

#### Assumptions
1. All monetary values (`amount`, `fromAccountBalance`, `toAccountBalance`) are non-negative double values ($x \ge 0.0$).
2. Threshold conditions are evaluated strictly as stated in the specification:
   - "less than $\$X$" is evaluated as $\text{balance} < X$.
   - "$\$X$ or more" is evaluated as $\text{balance} \ge X$.
3. The calculated fee is computed as:
   $$\text{Fee} = \text{amount} \times \left(\frac{\text{Fee Rate \%}}{100}\right)$$


---

### Deposit Fee Calculation

#### Process

The method under test is `FeesCalculator.calculateDepositInterest(amount, accountBalance, student)`. The specification calls the charge a "fee", while the code calls it "interest"; they are the same quantity.

The specification defines three input variables:
1. **$V_1$: Client type (`student`)**: Boolean domain `{true, false}`.
2. **$V_2$: Amount deposited (`amount`)**:
- For students: partitioned at $\$50.00$ ($> \$50.00$ vs. $\le \$50.00$).
- For non-students: partitioned at $\$250.00$ ($> \$250.00$ vs. $\le \$250.00$).
3. **$V_3$: Account balance (`accountBalance`)**: the threshold depends on the client type and the amount class:
- Student, amount $> \$50.00$: $> \$500.00$ vs. $\le \$500.00$.
- Student, amount $\le \$50.00$: $> \$5,000.00$ vs. $\le \$5,000.00$.
- Non-student, amount $> \$250.00$: $> \$2,500.00$ vs. $\le \$2,500.00$.
- Non-student, amount $\le \$250.00$: $> \$10,000.00$ vs. $\le \$10,000.00$.

Because the amount and balance thresholds change with the client type, the valid classes are nested inside each client type. This gives 8 valid equivalence classes (4 for students, 4 for non-students), one per fee rule in the specification.

The invalid classes are:
- $IC_1$: amount $\le 0$
- $IC_2$: balance $< 0$

Under the **weak robust** approach, each test case covers one valid class for every variable, except that an invalid test case contains exactly one invalid value with all other inputs valid. This gives 8 valid test cases and 2 invalid test cases.

#### Assumptions
1. The specification does not state how invalid input should be handled. We assume that a negative amount or a negative balance result causes `IllegalArgumentException` to be thrown.
2. Each test uses a representative value inside its class. Values on the class boundaries (for example 50 or 51) are left to boundary value analysis.
3. The fee is computed as:
   $$\text{Fee} = \text{amount} \times \left(\frac{\text{Fee Rate \%}}{100}\right)$$
4. Fees are compared with a tolerance (delta) of $10^{-4}$.



### Withdrawal fee calculation



### ATM Pink Format Validation




## Section 2: Test Case Tables

### Transfer Fee Descision Table
| Condition / Action | R1 | R2 | R3 | R4 | R5 | R6 | R7 | R8 | R9 | R10 | R11 | R12 | R13 | R14 | R15 | R16 |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **$C_1$: Student?** | T | T | T | T | T | T | T | T | F | F | F | F | F | F | F | F |
| **$C_2$: Amount ($)** | <200 | <200 | <200 | <200 | $\ge$200 | $\ge$200 | $\ge$200 | $\ge$200 | <100 | <100 | <100 | <100 | $\ge$100 | $\ge$100 | $\ge$100 | $\ge$100 |
| **$C_3$: From Balance ($)** | <2k | <2k | $\ge$2k | $\ge$2k | <2k | <2k | $\ge$2k | $\ge$2k | <4k | <4k | $\ge$4k | $\ge$4k | <2k | <2k | $\ge$2k | $\ge$2k |
| **$C_4$: To Balance ($)** | <1k | $\ge$1k | <1k | $\ge$1k | <1k | $\ge$1k | <1k | $\ge$1k | <2k | $\ge$2k | <2k | $\ge$2k | <1k | $\ge$1k | <1k | $\ge$1k |
| **Action: Rate (%)** | **0.1%** | **0.05%** | **0.05%** | **0.025%** | **0.05%** | **0.025%** | **0.25%** | **0.125%** | **0.2%** | **0.1%** | **1.0%** | **0.5%** | **0.2%** | **0.1%** | **0.5%** | **0.25%** |


### Transfer Fee Test Case Matrix 

| Test ID | Rule | Amount ($) | From Balance ($) | To Balance ($) | Student | Expected Fee Rate | Expected Fee ($) |
|---|---|---|---|---|---|---|---|
| **T_DT_01** | R1 | 100.00 | 1,500.00 | 500.00 | true | 0.1% | 0.10 |
| **T_DT_02** | R2 | 100.00 | 1,500.00 | 1,500.00 | true | 0.05% | 0.05 |
| **T_DT_03** | R3 | 100.00 | 2,500.00 | 500.00 | true | 0.05% | 0.05 |
| **T_DT_04** | R4 | 100.00 | 2,500.00 | 1,500.00 | true | 0.025% | 0.025 |
| **T_DT_05** | R5 | 400.00 | 1,500.00 | 500.00 | true | 0.05% | 0.20 |
| **T_DT_06** | R6 | 400.00 | 1,500.00 | 1,500.00 | true | 0.025% | 0.10 |
| **T_DT_07** | R7 | 400.00 | 2,500.00 | 500.00 | true | 0.25% | 1.00 |
| **T_DT_08** | R8 | 400.00 | 2,500.00 | 1,500.00 | true | 0.125% | 0.50 |
| **T_DT_09** | R9 | 50.00 | 3,000.00 | 1,500.00 | false | 0.2% | 0.10 |
| **T_DT_10** | R10 | 50.00 | 3,000.00 | 2,500.00 | false | 0.1% | 0.05 |
| **T_DT_11** | R11 | 50.00 | 4,500.00 | 1,500.00 | false | 1.0% | 0.50 |
| **T_DT_12** | R12 | 50.00 | 4,500.00 | 2,500.00 | false | 0.5% | 0.25 |
| **T_DT_13** | R13 | 200.00 | 1,500.00 | 500.00 | false | 0.2% | 0.40 |
| **T_DT_14** | R14 | 200.00 | 1,500.00 | 1,500.00 | false | 0.1% | 0.20 |
| **T_DT_15** | R15 | 200.00 | 2,500.00 | 500.00 | false | 0.5% | 1.00 |
| **T_DT_16** | R16 | 200.00 | 2,500.00 | 1,500.00 | false | 0.25% | 0.50 |

---

### Deposit Fee Test Case Matrix

| Test ID | Class covered | Student | Amount ($) | Balance ($) | Expected Fee Rate | Expected Fee ($) |
|---|---|---|---|---|---|---|
| **D_01** | Student, amount > 50, balance > 500 | true | 100.00 | 1,000.00 | 0.5% | 0.50 |
| **D_02** | Student, amount > 50, balance ≤ 500 | true | 100.00 | 300.00 | 0.25% | 0.25 |
| **D_03** | Student, amount ≤ 50, balance > 5,000 | true | 30.00 | 6,000.00 | 0.5% | 0.15 |
| **D_04** | Student, amount ≤ 50, balance ≤ 5,000 | true | 30.00 | 1,000.00 | none | 0.00 |
| **D_05** | Non-student, amount > 250, balance > 2,500 | false | 500.00 | 5,000.00 | 0.8% | 4.00 |
| **D_06** | Non-student, amount > 250, balance ≤ 2,500 | false | 500.00 | 1,000.00 | 0.4% | 2.00 |
| **D_07** | Non-student, amount ≤ 250, balance > 10,000 | false | 100.00 | 20,000.00 | none | 0.00 |
| **D_08** | Non-student, amount ≤ 250, balance ≤ 10,000 | false | 100.00 | 5,000.00 | 0.1% | 0.10 |
| **D_09** | Invalid amount (negative) | false | -100.00 | 5,000.00 | n/a | `IllegalArgumentException` |
| **D_10** | Invalid balance (negative) | false | 100.00 | -500.00 | n/a | `IllegalArgumentException` |


### Withdrawal Test cases


### ATM PIN Format Test Cases



## Section 3: Test Results and Fault Localization

### Transfer Fee Test Execution Results

Ran suite with test failures tolerated (as instructed used `mvn -B -Dmaven.test.failure.ignore=true test` ):
| Test ID | Method | Expected Fee ($) | Actual Returned Fee ($) | Result |
|---|---|---|---|---|
| **T_DT_01** | `testRule1` | 0.10 | 2.50 | **FAIL** |
| **T_DT_02** | `testRule2` | 0.05 | 1.25 | **FAIL** |
| **T_DT_03** | `testRule3` | 0.05 | 2.50 | **FAIL** |
| **T_DT_04** | `testRule4` | 0.025 | 1.25 | **FAIL** |
| **T_DT_05** | `testRule5` | 0.20 | 10.00 | **FAIL** |
| **T_DT_06** | `testRule6` | 0.10 | 5.00 | **FAIL** |
| **T_DT_07** | `testRule7` | 1.00 | 10.00 | **FAIL** |
| **T_DT_08** | `testRule8` | 0.50 | 5.00 | **FAIL** |
| **T_DT_09** | `testRule9` | 0.10 | 2.50 | **FAIL** |
| **T_DT_10** | `testRule10` | 0.05 | 2.50 | **FAIL** |
| **T_DT_11** | `testRule11` | 0.50 | 2.50 | **FAIL** |
| **T_DT_12** | `testRule12` | 0.25 | 2.50 | **FAIL** |
| **T_DT_13** | `testRule13` | 0.40 | 10.00 | **FAIL** |
| **T_DT_14** | `testRule14` | 0.20 | 11.00 | **FAIL** |
| **T_DT_15** | `testRule15` | 1.00 | 10.00 | **FAIL** |
| **T_DT_16** | `testRule16` | 0.50 | 11.00 | **FAIL** |


### Deposit Fee Test Execution Results

Ran suite with test failures tolerated (as instructed used `mvn -B -Dmaven.test.failure.ignore=true test` ):

| Test ID | Method | Expected | Actual | Result |
|---|---|---|---|---|
| **D_01** | `testStudentLargeAmountHighBalance` | 0.50 | 0.20 | **FAIL** |
| **D_02** | `testStudentLargeAmountLowBalance` | 0.25 | 0.20 | **FAIL** |
| **D_03** | `testStudentSmallAmountHighBalance` | 0.15 | 0.15 | **PASS** |
| **D_04** | `testStudentSmallAmountLowBalance` | 0.00 | 0.06 | **FAIL** |
| **D_05** | `testNonStudentLargeAmountHighBalance` | 4.00 | 0.00 | **FAIL** |
| **D_06** | `testNonStudentLargeAmountLowBalance` | 2.00 | 0.00 | **FAIL** |
| **D_07** | `testNonStudentSmallAmountHighBalance` | 0.00 | 0.50 | **FAIL** |
| **D_08** | `testNonStudentSmallAmountLowBalance` | 0.10 | 0.00 | **FAIL** |
| **D_09** | `testInvalidAmount` | `IllegalArgumentException` | no exception thrown | **FAIL** |
| **D_10** | `testInvalidBalance` | `IllegalArgumentException` | no exception thrown | **FAIL** | 


### withdrawal execution results

---

### Fault Localization

| Fault ID | Failing Test Cases | Class & Method | Fault Location & Justification |
|---|---|---|---|
| **FL_TR_01** | `T_DT_01` – `T_DT_04` | `bank.utils.FeesCalculator.calculateTransferFee` | The student branch incorrectly checks `amount < 100.00` and `fromAccountBalance < 1000.00` instead of the specified `$200.00` and `$2,000.00` boundaries |
| **FL_TR_02** | `T_DT_01` – `T_DT_08` | `bank.utils.FeesCalculator.calculateTransferFee` | Student fee multipliers in the implementation are 10× to 100× too large (ex: hardcoded values of `0.01` to `0.05` instead of `0.001` down to `0.00025`). |
| **FL_TR_03** | `T_DT_09` – `T_DT_12` | `bank.utils.FeesCalculator.calculateTransferFee` | For non-students transferring $< \$100$, account balances are checked against `$1,000.00` instead of `$4,000.00` for sender and `$2,000.00` for recipient. |
| **FL_TR_04** | `T_DT_13` – `T_DT_16` | `bank.utils.FeesCalculator.calculateTransferFee` | In the non-student $\ge \$100$ branch, an invalid multiplier rate of 5.5% is applied instead of 0.25%, producing inflated fees (e.g., $\$11.00$ on a $\$200.00$ transfer). |
| **FL_DP_01** | `D_01`, `D_02` | `bank.utils.FeesCalculator.calculateDepositInterest` | The student branch tests `amount > 100.00` instead of the specified `$50.00`. A $100 deposit is "more than $50" per the specification, but the code treats it as a small deposit and applies 0.2%, giving 0.20 instead of 0.50 and 0.25. |
| **FL_DP_02** | `D_04` | `bank.utils.FeesCalculator.calculateDepositInterest` | In the student, small-amount, low-balance branch, the code sets `interestPercentage = 0.002` where the specification says there is no fee, so a $30 deposit is charged 0.06 instead of 0.00. |
| **FL_DP_03** | `D_05`, `D_06` | `bank.utils.FeesCalculator.calculateDepositInterest` | The non-student branch tests `amount > 500.00` instead of the specified `$250.00`. A $500 deposit is "more than $250" per the specification, but the code treats it as a small deposit with balance at most $10,000 and returns 0 instead of 4.00 and 2.00. |
| **FL_DP_04** | `D_07`, `D_08` | `bank.utils.FeesCalculator.calculateDepositInterest` | In the non-student, small-amount branch the rates are swapped. The code charges 0.5% when the balance is above $10,000 (expected none) and 0% when it is at most $10,000 (expected 0.1%). |
| **FL_DP_05** | `D_09`, `D_10` | `bank.utils.FeesCalculator.calculateDepositInterest` | The method does no input validation. A negative amount or negative balance is accepted and no exception is thrown. |











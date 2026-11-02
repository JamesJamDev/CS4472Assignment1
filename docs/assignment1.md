
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

### Depositi test caes

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


### deposit and withdrawal execution results

---

### Fault Localization

| Fault ID | Failing Test Cases | Class & Method | Fault Location & Justification |
|---|---|---|---|
| **FL_TR_01** | `T_DT_01` – `T_DT_04` | `bank.utils.FeesCalculator.calculateTransferFee` | The student branch incorrectly checks `amount < 100.00` and `fromAccountBalance < 1000.00` instead of the specified `$200.00` and `$2,000.00` boundaries |
| **FL_TR_02** | `T_DT_01` – `T_DT_08` | `bank.utils.FeesCalculator.calculateTransferFee` | Student fee multipliers in the implementation are 10× to 100× too large (ex: hardcoded values of `0.01` to `0.05` instead of `0.001` down to `0.00025`). |
| **FL_TR_03** | `T_DT_09` – `T_DT_12` | `bank.utils.FeesCalculator.calculateTransferFee` | For non-students transferring $< \$100$, account balances are checked against `$1,000.00` instead of `$4,000.00` for sender and `$2,000.00` for recipient. |
| **FL_TR_04** | `T_DT_13` – `T_DT_16` | `bank.utils.FeesCalculator.calculateTransferFee` | In the non-student $\ge \$100$ branch, an invalid multiplier rate of 5.5% is applied instead of 0.25%, producing inflated fees (e.g., $\$11.00$ on a $\$200.00$ transfer). |











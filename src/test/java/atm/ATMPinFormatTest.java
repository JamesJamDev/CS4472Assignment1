package atm;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import atm.dispatcher.MessageDispatcher;
import atm.exceptions.InvalidPinFormatException;
import atm.ui.panels.MainPanel;
import atm.utils.CredentialsCheck;
import atm.utils.FormatChecker;

@ExtendWith(MockitoExtension.class)
public class ATMPinFormatTest {

	/*
	  Robust Worst Case Boundary Value Analysis (spec: PIN format)
		- 2 variables, char: 7 values, length: 3 values, 21 tests.
			- PIN length: 4, 5 (valid), 6
			- min = max = 5, so 3 robust points
			- char value (ASCII): '/', '0', '1', '4', '8', '9', ':'
			- '/' and ':' are the chars just outside '0'-'9'
		- same char used in every position (e.g. len 4, '0' -> "0000")
		- tested through atm.checkPin(), the path the UI uses into Session
		- the stubs pass null as the card, as this class’s setup never inserts a card, so the session’s card is null
		- nom == nominal (nom == max == min)
		- known defects:
			- TPF_CASE_05, 06, 08, 09, 11, 12, 14, 15, 17: length check expects 4, spec says 5
			- TPF_CASE_18: '>= 9' condition excludes '9'
				- passes only because the two defects cancel out
	 */

	ATM atm;
	MessageDispatcher dispatcher;
	FormatChecker formatCheck;

	@BeforeEach
	public void setUp() throws Exception {
		dispatcher = Mockito.mock(MessageDispatcher.class);
		formatCheck = new FormatChecker();
		CredentialsCheck credentialsCheck = new CredentialsCheck(dispatcher);
		atm = new ATM(formatCheck, credentialsCheck, dispatcher);

		MainPanel mainPanel = Mockito.mock(MainPanel.class);
		atm.setMainPanel(mainPanel);
		atm.createSession();
	}

	@AfterEach
	public void tearDown() throws Exception {
	}

	@Test
	@DisplayName("TPF_CASE_01 [char: min-1, len: max+1]: length=6, char='/' -> rejected")
	public void pinLen6CharSlash() {
		Assertions.assertThrows(InvalidPinFormatException.class,
				() -> atm.checkPin(new char[] { '/', '/', '/', '/', '/', '/' }));
	}

	@Test
	@DisplayName("TPF_CASE_02 [char: min-1, len: nom]: length=5, char='/' -> rejected")
	public void pinLen5CharSlash() {
		Assertions.assertThrows(InvalidPinFormatException.class,
				() -> atm.checkPin(new char[] { '/', '/', '/', '/', '/' }));
	}

	@Test
	@DisplayName("TPF_CASE_03 [char: min-1, len: min-1]: length=4, char='/' -> rejected")
	public void pinLen4CharSlash() {
		Assertions.assertThrows(InvalidPinFormatException.class, () -> atm.checkPin(new char[] { '/', '/', '/', '/' }));
	}

	@Test
	@DisplayName("TPF_CASE_04 [char: min, len: max+1]: length=6, char='0' -> rejected")
	public void pinLen6Char0() {
		Assertions.assertThrows(InvalidPinFormatException.class,
				() -> atm.checkPin(new char[] { '0', '0', '0', '0', '0', '0' }));
	}

	@Test
	@DisplayName("TPF_CASE_05 [char: min, len: nom]: length=5, char='0' -> accepted")
	public void pinLen5Char0() {
		Mockito.when(dispatcher.checkCredentials(null, new char[] { '0', '0', '0', '0', '0' })).thenReturn(true);
		Assertions.assertDoesNotThrow(() -> atm.checkPin(new char[] { '0', '0', '0', '0', '0' }));
	}

	@Test
	@DisplayName("TPF_CASE_06 [char: min, len: min-1]: length=4, char='0' -> rejected")
	public void pinLen4Char0() {
		Assertions.assertThrows(InvalidPinFormatException.class, () -> atm.checkPin(new char[] { '0', '0', '0', '0' }));
	}

	@Test
	@DisplayName("TPF_CASE_07 [char: min+1, len: max+1]: length=6, char='1' -> rejected")
	public void pinLen6Char1() {
		Assertions.assertThrows(InvalidPinFormatException.class,
				() -> atm.checkPin(new char[] { '1', '1', '1', '1', '1', '1' }));
	}

	@Test
	@DisplayName("TPF_CASE_08 [char: min+1, len: nom]: length=5, char='1' -> accepted")
	public void pinLen5Char1() {
		Mockito.when(dispatcher.checkCredentials(null, new char[] { '1', '1', '1', '1', '1' })).thenReturn(true);
		Assertions.assertDoesNotThrow(() -> atm.checkPin(new char[] { '1', '1', '1', '1', '1' }));
	}

	@Test
	@DisplayName("TPF_CASE_09 [char: min+1, len: min-1]: length=4, char='1' -> rejected")
	public void pinLen4Char1() {
		Assertions.assertThrows(InvalidPinFormatException.class, () -> atm.checkPin(new char[] { '1', '1', '1', '1' }));
	}

	@Test
	@DisplayName("TPF_CASE_10 [char: nom, len: max+1]: length=6, char='4' -> rejected")
	public void pinLen6Char4() {
		Assertions.assertThrows(InvalidPinFormatException.class,
				() -> atm.checkPin(new char[] { '4', '4', '4', '4', '4', '4' }));
	}

	@Test
	@DisplayName("TPF_CASE_11 [char: nom, len: nom]: length=5, char='4' -> accepted")
	public void pinLen5Char4() {
		Mockito.when(dispatcher.checkCredentials(null, new char[] { '4', '4', '4', '4', '4' })).thenReturn(true);
		Assertions.assertDoesNotThrow(() -> atm.checkPin(new char[] { '4', '4', '4', '4', '4' }));
	}

	@Test
	@DisplayName("TPF_CASE_12 [char: nom, len: min-1]: length=4, char='4' -> rejected")
	public void pinLen4Char4() {
		Assertions.assertThrows(InvalidPinFormatException.class, () -> atm.checkPin(new char[] { '4', '4', '4', '4' }));
	}

	@Test
	@DisplayName("TPF_CASE_13 [char: max-1, len: max+1]: length=6, char='8' -> rejected")
	public void pinLen6Char8() {
		Assertions.assertThrows(InvalidPinFormatException.class,
				() -> atm.checkPin(new char[] { '8', '8', '8', '8', '8', '8' }));
	}

	@Test
	@DisplayName("TPF_CASE_14 [char: max-1, len: nom]: length=5, char='8' -> accepted")
	public void pinLen5Char8() {
		Mockito.when(dispatcher.checkCredentials(null, new char[] { '8', '8', '8', '8', '8' })).thenReturn(true);
		Assertions.assertDoesNotThrow(() -> atm.checkPin(new char[] { '8', '8', '8', '8', '8' }));
	}

	@Test
	@DisplayName("TPF_CASE_15 [char: max-1, len: min-1]: length=4, char='8' -> rejected")
	public void pinLen4Char8() {
		Assertions.assertThrows(InvalidPinFormatException.class, () -> atm.checkPin(new char[] { '8', '8', '8', '8' }));
	}

	@Test
	@DisplayName("TPF_CASE_16 [char: max, len: max+1]: length=6, char='9' -> rejected")
	public void pinLen6Char9() {
		Assertions.assertThrows(InvalidPinFormatException.class,
				() -> atm.checkPin(new char[] { '9', '9', '9', '9', '9', '9' }));
	}

	@Test
	@DisplayName("TPF_CASE_17 [char: max, len: nom]: length=5, char='9' -> accepted")
	public void pinLen5Char9() {
		Mockito.when(dispatcher.checkCredentials(null, new char[] { '9', '9', '9', '9', '9' })).thenReturn(true);
		Assertions.assertDoesNotThrow(() -> atm.checkPin(new char[] { '9', '9', '9', '9', '9' }));
	}

	@Test
	@DisplayName("TPF_CASE_18 [char: max, len: min-1]: length=4, char='9' -> rejected")
	public void pinLen4Char9() {
		Assertions.assertThrows(InvalidPinFormatException.class, () -> atm.checkPin(new char[] { '9', '9', '9', '9' }));
	}

	@Test
	@DisplayName("TPF_CASE_19 [char: max+1, len: max+1]: length=6, char=':' -> rejected")
	public void pinLen6CharColon() {
		Assertions.assertThrows(InvalidPinFormatException.class,
				() -> atm.checkPin(new char[] { ':', ':', ':', ':', ':', ':' }));
	}

	@Test
	@DisplayName("TPF_CASE_20 [char: max+1, len: nom]: length=5, char=':' -> rejected")
	public void pinLen5CharColon() {
		Assertions.assertThrows(InvalidPinFormatException.class,
				() -> atm.checkPin(new char[] { ':', ':', ':', ':', ':' }));
	}

	@Test
	@DisplayName("TPF_CASE_21 [char: max+1, len: min-1]: length=4, char=':' -> rejected")
	public void pinLen4CharColon() {
		Assertions.assertThrows(InvalidPinFormatException.class, () -> atm.checkPin(new char[] { ':', ':', ':', ':' }));
	}
}

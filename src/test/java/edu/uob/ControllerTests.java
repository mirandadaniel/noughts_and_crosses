package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
//import edu.uob.OXOMoveException.*;


// PLEASE READ:
// The tests in this file will fail by default for a template skeleton, your job is to pass them
// and maybe write some more, read up on how to write tests at
// https://junit.org/junit5/docs/current/user-guide/#writing-tests
final class ControllerTests {
  OXOModel model;
  OXOController controller;

  // create your standard 3*3 OXO board (where three of the same symbol in a line wins) with the X
  // and O player
  private static OXOModel createStandardModel() {
    OXOModel model = new OXOModel(3, 3, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    return model;
  }

  // we make a new board for every @Test (i.e. this method runs before every @Test test case)
  @BeforeEach
  void setup() {
    model = createStandardModel();
    controller = new OXOController(model);
  }

  @Test
  void test_add_row(){
    model.addRow();
    assertEquals(4, model.getNumberOfRows());
    assertEquals(3, model.getNumberOfColumns());
  }

  @Test
  void test_add_col(){
    model.addColumn();
    assertEquals(4, model.getNumberOfColumns());
    assertEquals(3, model.getNumberOfRows());
  }

  @Test
  void test_remove_col(){
    model.removeColumn();
    assertEquals(2, model.getNumberOfColumns());
    assertEquals(3, model.getNumberOfRows());
  }

  @Test
  void test_remove_row(){
    model.removeRow();
    assertEquals(2, model.getNumberOfRows());
    assertEquals(3, model.getNumberOfColumns());
  }

  @Test
  void test_command2row() throws OXOMoveException {
    String command = "a1";
    controller.command2row(command);
    assertEquals(0, controller.command2row(command));
  }

  @Test
  void test_command2row2() throws OXOMoveException {
    String command = "b1";
    controller.command2row(command);
    assertEquals(1, controller.command2row(command));
  }

  @Test
  void test_command2col () throws OXOMoveException {
    String command = "a1";
    controller.command2col(command);
    assertEquals(0, controller.command2col(command));
  }

  @Test
  void test_command2col2() throws OXOMoveException {
    String command = "a3";
    controller.command2col(command);
    assertEquals(2, controller.command2col(command));
  }

  @Test
  void test_invalidCol() throws OXOMoveException {
    assertThrows(OXOMoveException.InvalidIdentifierCharacterException.class, ()-> controller.handleIncomingCommand("aa"));
  }

  @Test
  void test_invalidRow() throws OXOMoveException {
    assertThrows(OXOMoveException.InvalidIdentifierCharacterException.class, ()-> controller.handleIncomingCommand("1a"));
  }

  @Test
  void test_outsideRow() throws OXOMoveException{
    assertThrows(OXOMoveException.OutsideCellRangeException.class, ()-> controller.handleIncomingCommand("z1"));
  }

  @Test
  void test_outsideCol() throws OXOMoveException{
    assertThrows(OXOMoveException.OutsideCellRangeException.class, ()-> controller.handleIncomingCommand("a0"));
  }

  @Test
  void test_tooLong() throws OXOMoveException{
    assertThrows(OXOMoveException.InvalidIdentifierLengthException.class, ()-> controller.handleIncomingCommand("a100"));
  }


  @Test
  void test_invalidBoth() throws OXOMoveException {
    assertThrows(OXOMoveException.InvalidIdentifierCharacterException.class, ()-> controller.handleIncomingCommand("??"));
  }

  @Test
  void testWinIncrease(){
    assertEquals(3, model.getWinThreshold());
    controller.increaseWinThreshold();
    assertEquals(4, model.getWinThreshold());
  }

  @Test
  void testWinDecrease(){
    assertEquals(3, model.getWinThreshold());
    controller.decreaseWinThreshold();
    assertEquals(2, model.getWinThreshold());
  }

  @Test
  void increasePlayers(){
    assertEquals(2, model.getNumberOfPlayers());
    model.addPlayer(new OXOPlayer('!'));
    assertEquals(3, model.getNumberOfPlayers());
  }


  @Test
  void testHandleIncomingCommand() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("a1");
    assertEquals(firstMovingPlayer, controller.gameModel.getCellOwner(0, 0));

  }


  @Test
  void testBasicWinWithA1A2A3() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("a2");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("a3");

    assertEquals(
        firstMovingPlayer,
        model.getWinner(),
        "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testBasicWinWithC1C2C3() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("c1");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("c3");
    assertTrue(controller.rowWin(firstMovingPlayer));
    assertTrue(controller.checkWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testSecondPlayerWin() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("a1");
    OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("a2");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("c1");
    controller.handleIncomingCommand("b3");
    assertTrue(controller.rowWin(secondMovingPlayer));

    assertEquals(
            secondMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(secondMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testDiagLeftUp() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("c1");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("a3");
    assertTrue(controller.diagleftup(firstMovingPlayer, 2, 0));
    assertTrue(controller.diag_check1(firstMovingPlayer));
    assertTrue(controller.checkWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testDiagLeftUp1() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    model.addRow();
    model.addColumn();
    controller.handleIncomingCommand("d1");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("b3");
    assertTrue(controller.diagleftup(firstMovingPlayer, 3, 0));
    assertTrue(controller.diag_check1(firstMovingPlayer));
    assertTrue(controller.checkWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testDiagLeftUp2() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.decreaseWinThreshold();
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("a3");
    assertTrue(controller.diagleftup(firstMovingPlayer, 1, 1));

    assertTrue(controller.diag_check1(firstMovingPlayer));
    assertTrue(controller.checkWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testDiagRightUp() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("c3");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("c1");
    controller.handleIncomingCommand("a1");
    assertTrue(controller.diagrightup(firstMovingPlayer, 2, 2));
    assertTrue(controller.diag_check1(firstMovingPlayer));
    assertTrue(controller.checkWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testDiagRightUp1() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    model.addRow();
    model.addColumn();
    controller.handleIncomingCommand("d4");
    controller.handleIncomingCommand("c3");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("d1");
    controller.handleIncomingCommand("a1");
    assertFalse(controller.diagrightup(firstMovingPlayer, 2, 0));
    assertTrue(controller.diag_check1(firstMovingPlayer));
    assertTrue(controller.checkWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testDiagRightUp2() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.decreaseWinThreshold();
    controller.handleIncomingCommand("c3");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("b2");
    assertTrue(controller.diagrightup(firstMovingPlayer, 2, 2));
    assertTrue(controller.diag_check1(firstMovingPlayer));
    assertTrue(controller.checkWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }




  @Test
  void testLongDiagRightUp2() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    model.addRow();
    model.addRow();
    model.addColumn();
    model.addColumn();
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    assertEquals(5, model.getNumberOfRows());
    assertEquals(5, model.getNumberOfColumns());
    assertEquals(5, model.getWinThreshold());
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("c3");
    controller.handleIncomingCommand("d1");
    controller.handleIncomingCommand("d4");
    controller.handleIncomingCommand("e2");
    controller.handleIncomingCommand("e5");
    assertTrue(controller.diagrightup(firstMovingPlayer, 4, 4));
    assertTrue(controller.diag_check1(firstMovingPlayer));
    assertTrue(controller.checkWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }


  @Test
  void testLongerDiagRightUp() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    model.addRow();
    model.addRow();
    model.addColumn();
    model.addColumn();
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    assertEquals(5, model.getNumberOfRows());
    assertEquals(5, model.getNumberOfColumns());
    assertEquals(6, model.getWinThreshold());
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("c3");
    controller.handleIncomingCommand("d1");
    controller.handleIncomingCommand("d4");
    controller.handleIncomingCommand("e2");
    controller.handleIncomingCommand("e5");
    controller.decreaseWinThreshold();
    assertEquals(5, model.getWinThreshold());
    assertTrue(controller.diagrightup(firstMovingPlayer, 4, 4));
    assertTrue(controller.diag_check1(firstMovingPlayer));
    assertTrue(controller.checkWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testVeryLongDiagLeftUp() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    model.addRow();
    model.addRow();
    model.addRow();
    model.addRow();
    model.addRow();
    model.addColumn();
    model.addColumn();
    model.addColumn();
    model.addColumn();
    model.addColumn();
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    assertEquals(8, model.getNumberOfRows());
    assertEquals(8, model.getNumberOfColumns());
    assertEquals(8, model.getWinThreshold());
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("c3");
    controller.handleIncomingCommand("d1");
    controller.handleIncomingCommand("d4");
    controller.handleIncomingCommand("e2");
    controller.handleIncomingCommand("e5");
    controller.handleIncomingCommand("b3");
    controller.handleIncomingCommand("f6");
    controller.handleIncomingCommand("b4");
    controller.handleIncomingCommand("g7");
    controller.handleIncomingCommand("b5");
    controller.handleIncomingCommand("h8");
    assertTrue( controller.diagrightup(firstMovingPlayer, 7, 7));
    assertTrue(controller.diag_check1(firstMovingPlayer));
    assertTrue(controller.checkWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testDiagLeftUpAgain() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("c1");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("a3");
    assertTrue(controller.diagleftup(firstMovingPlayer, 2, 0));
    assertEquals(true, controller.diag_check1(firstMovingPlayer));
    assertEquals(true, controller.checkWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }


  @Test
  void testLongDiagLeftUp() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    model.addRow();
    model.addRow();
    model.addColumn();
    model.addColumn();
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    assertEquals(5, model.getNumberOfRows());
    assertEquals(5, model.getNumberOfColumns());
    assertEquals(5, model.getWinThreshold());
    controller.handleIncomingCommand("e1");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("d2");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("c3");
    controller.handleIncomingCommand("d1");
    controller.handleIncomingCommand("b4");
    controller.handleIncomingCommand("e2");
    controller.handleIncomingCommand("a5");
    assertTrue(controller.diagleftup(firstMovingPlayer, 4, 0));
    assertTrue(controller.diag_check1(firstMovingPlayer));
    assertTrue(controller.checkWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }



  @Test
  void testLongDiagLeftDown() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.addRow();
    controller.addColumn();
    controller.increaseWinThreshold();
    controller.handleIncomingCommand("a4");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("b3");
    controller.handleIncomingCommand("c1");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("d4");
    controller.handleIncomingCommand("d1");
    assertTrue(controller.diagleftup(firstMovingPlayer, 3, 0));
    assertEquals(true, controller.diag_check1(firstMovingPlayer));
    assertEquals(true, controller.checkWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testLongDiagLeftDown2() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.addRow();
    controller.addColumn();
    controller.addRow();
    controller.addColumn();
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    controller.handleIncomingCommand("a5");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("b4");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("c3");
    controller.handleIncomingCommand("d4");
    controller.handleIncomingCommand("d2");
    controller.handleIncomingCommand("c4");
    controller.handleIncomingCommand("e1");
    controller.handleIncomingCommand("d1");
    assertTrue(controller.diagleftup(firstMovingPlayer, 4, 0));
    assertEquals(true, controller.diag_check1(firstMovingPlayer));
    assertEquals(true, controller.checkWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }


  @Test
  void testLongDiagUp() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    model.addRow();
    model.addColumn();
    controller.increaseWinThreshold();
    assertEquals(4, model.getWinThreshold());
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("c3");
    controller.handleIncomingCommand("a2");
    controller.handleIncomingCommand("d4");
    assertTrue(controller.diagrightup(firstMovingPlayer, 3, 3));

    assertEquals(true, controller.diag_check1(firstMovingPlayer));
    assertEquals(true, controller.checkWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }


  @Test
  void testRowWin() throws OXOMoveException {
    setup();
    createStandardModel();
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("a2");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("a3");
    model.setCurrentPlayerNumber(0);
    assertEquals(true, controller.rowWin(firstMovingPlayer));
    assertEquals(true, controller.checkWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testRowWin2() throws OXOMoveException {
    setup();
    createStandardModel();
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    model.addColumn();
    model.addColumn();
    model.addColumn();
    assertEquals(6, model.getNumberOfColumns());
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    assertEquals(6, model.getWinThreshold());
    controller.handleIncomingCommand("c1");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("c3");
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("c4");
    controller.handleIncomingCommand("a4");
    controller.handleIncomingCommand("c5");
    controller.handleIncomingCommand("a3");
    controller.handleIncomingCommand("c6");
    assertTrue(controller.rowWin(firstMovingPlayer));
    assertTrue(controller.checkWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testColWin() throws OXOMoveException {
    setup();
    createStandardModel();
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    OXOPlayer x;
    controller.handleIncomingCommand("a1");
    x = model.getCellOwner(0, 0);
    controller.handleIncomingCommand("b3");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("c1");
    assertTrue(controller.colWin(firstMovingPlayer));
    assertTrue(controller.checkWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testColWin2() throws OXOMoveException {
    setup();
    createStandardModel();
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    model.addRow();
    model.addRow();
    assertEquals(5, model.getNumberOfRows());
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    assertEquals(5, model.getWinThreshold());
    controller.handleIncomingCommand("a2");
    controller.handleIncomingCommand("b3");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("c3");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("d2");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("e2");
    assertTrue(controller.colWin(firstMovingPlayer));
    assertTrue(controller.checkWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testLots() throws OXOMoveException {
    setup();
    createStandardModel();
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    model.addPlayer(new OXOPlayer('A'));
    model.addPlayer(new OXOPlayer('B'));
    model.addRow();
    assertEquals(4, model.getNumberOfRows());
    model.addColumn();
    assertEquals(4, model.getNumberOfColumns());
    controller.decreaseWinThreshold();
    assertEquals(2, model.getWinThreshold());
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("a3");
    controller.handleIncomingCommand("b3");
    assertEquals(true, controller.rowWin(firstMovingPlayer));
    assertEquals(true, controller.checkWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testMultiPlayerRowWin() throws OXOMoveException {
    setup();
    createStandardModel();
    model.addColumn();
    model.addRow();
    OXOPlayer second;
    OXOPlayer third = new OXOPlayer('!');
    model.addPlayer(third);
    assertEquals(3, model.getWinThreshold());
    controller.handleIncomingCommand("d1");
    second = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    assertEquals('O', second.getPlayingLetter());
    controller.handleIncomingCommand("b2");
    third = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("c1");
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("a2");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("b3");
    controller.handleIncomingCommand("a3");
    controller.handleIncomingCommand("c3");
    assertTrue(controller.rowWin(third));

    assertEquals(
            third,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(third.getPlayingLetter()));

  }

  @Test
  void testMultiPlayerColWin() throws OXOMoveException {
    setup();
    createStandardModel();
    controller.decreaseWinThreshold();
    assertEquals(2, model.getWinThreshold());
    assertEquals(3, model.getNumberOfColumns());
    assertEquals(3, model.getNumberOfRows());
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    OXOPlayer second;
    OXOPlayer third = new OXOPlayer('!');
    OXOPlayer fourth = new OXOPlayer('%');
    model.addPlayer(third);
    model.addPlayer(fourth);
    assertEquals(4, model.getNumberOfPlayers());
    controller.handleIncomingCommand("a2");
    second = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    assertEquals('O', second.getPlayingLetter());
    controller.handleIncomingCommand("a1");
    third = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    assertEquals('!', third.getPlayingLetter());
    controller.handleIncomingCommand("c1");
    fourth = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    assertEquals('%', fourth.getPlayingLetter());
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("b2");

    assertTrue(controller.colWin(firstMovingPlayer));

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));

  }


  @Test
  void decreaseInWinThresDuringDiagonals() throws OXOMoveException{
    setup();
    createStandardModel();
    OXOPlayer first = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    model.setCurrentPlayerNumber(1);
    OXOPlayer second = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    OXOPlayer third = new OXOPlayer('!');
    model.addPlayer(third);
    controller.addColumn();
    controller.addColumn();
    controller.addRow();
    controller.addRow();
    controller.increaseWinThreshold();
    controller.handleIncomingCommand("d5");
    controller.handleIncomingCommand("c1");
    controller.handleIncomingCommand("e1");
    controller.handleIncomingCommand("c4");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("d2");
    controller.handleIncomingCommand("b3");
    controller.handleIncomingCommand("a3");
    controller.handleIncomingCommand("c3");
    controller.decreaseWinThreshold();
    assertTrue(model.isGameDrawn());
  }

  @Test
  void testChangeInWinThres() throws OXOMoveException {
    setup();
    createStandardModel();
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    OXOPlayer secondMovingPlayer;
    controller.handleIncomingCommand("a3");
    secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("a2");
    controller.handleIncomingCommand("b2");
    controller.decreaseWinThreshold();
    assertEquals(model.isGameDrawn(), true);
    assertEquals(2, model.getWinThreshold());
    assertEquals(true, controller.rowWin(firstMovingPlayer));
    assertEquals(true, controller.rowWin(secondMovingPlayer));
    assertEquals(true, controller.checkWinDraw());
    assertTrue(model.isGameDrawn());
  }

  @Test
  void testChangeInWinThres2() throws OXOMoveException {
    setup();
    createStandardModel();
    model.addColumn();
    model.addRow();
    OXOPlayer second;
    controller.handleIncomingCommand("d1");
    second = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    assertEquals('O', second.getPlayingLetter());
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("d4");
    controller.handleIncomingCommand("b3");
    controller.decreaseWinThreshold();
    assertEquals(2, model.getWinThreshold());
    assertFalse(model.isGameDrawn());
    assertTrue(controller.rowWin(second));

    assertEquals(
            second,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(second.getPlayingLetter()));

  }

  @Test
  void testChangeInWinThres3() throws OXOMoveException {
    setup();
    createStandardModel();
    model.addColumn();
    model.addRow();
    controller.handleIncomingCommand("a1");
    OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("b3");
    controller.handleIncomingCommand("d1");
    controller.handleIncomingCommand("c3");
    controller.decreaseWinThreshold();
    assertFalse(model.isGameDrawn());
    assertTrue(controller.colWin(secondMovingPlayer));

    assertTrue(controller.checkWin(secondMovingPlayer));
    assertNotNull(model.getWinner());

    assertEquals(
            secondMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(secondMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testChangeInWinThres4() throws OXOMoveException {
    setup();
    createStandardModel();
    model.addColumn();
    model.addRow();
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("c1");
    controller.handleIncomingCommand("a2");
    controller.handleIncomingCommand("c2");
    controller.decreaseWinThreshold();
    assertTrue(controller.checkWinDraw());
    assertTrue(model.isGameDrawn());
  }

  @Test
  void decreaseWinThresFindDraw() throws OXOMoveException{
    setup();
    createStandardModel();
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    model.addColumn();
    model.addRow();
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("c1");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("c3");
    controller.decreaseWinThreshold();
    assertEquals(2, model.getWinThreshold());
    assertTrue(controller.checkWinDraw());
    assertTrue(model.isGameDrawn());

  }


  @Test
  void decreaseWinThresFindWin() throws OXOMoveException{
    setup();
    createStandardModel();
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    model.addColumn();
    model.addRow();
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("c1");
    controller.handleIncomingCommand("b1");
    controller.decreaseWinThreshold();
    assertEquals(2, model.getWinThreshold());

    assertTrue(controller.colWin(firstMovingPlayer));
    assertTrue(controller.checkWin(firstMovingPlayer));
    assertNotNull(model.getWinner());

    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));

  }


  @Test
  void testDrawState() throws OXOMoveException {
    setup();
    createStandardModel();
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    model.addColumn();
    model.addRow();
    controller.increaseWinThreshold();
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("a2");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("a3");
    controller.handleIncomingCommand("b3");
    controller.handleIncomingCommand("b4");
    controller.handleIncomingCommand("a4");
    controller.handleIncomingCommand("c1");
    controller.handleIncomingCommand("d1");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("d2");
    controller.handleIncomingCommand("c3");
    controller.handleIncomingCommand("d3");
    controller.handleIncomingCommand("d4");
    controller.handleIncomingCommand("c4");
    assertEquals(model.isGameDrawn(), true);

  }

}


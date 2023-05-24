package edu.uob;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class OXOModel {
  private final List<List<OXOPlayer>> cells;
  private final List<OXOPlayer> players;
  private int currentPlayerNumber;
  private OXOPlayer winner;
  private boolean gameDrawn;
  private int winThreshold;
  public int move = 0;


  public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
    winThreshold = winThresh;
    cells = new ArrayList<>();
    players = new ArrayList<>();

    for(int i = 0; i < numberOfRows; i++){
      List<OXOPlayer> parent = new ArrayList<>();
      cells.add(parent);
      for(int j = 0; j < numberOfColumns; j++){
        parent.add(null);
      }
    }
  }


  public void addColumn(){
    int rows = getNumberOfRows();
    int cols = getNumberOfColumns();
    if(cols < 9) {
      for (int j = 0; j < rows; j++) {
        cells.get(j).add(null);
      }
    }
  }

  public void addRow(){
    int cols = getNumberOfColumns();
    int rows = getNumberOfRows();
    if(rows < 9) {
      List<OXOPlayer> parent_row = new ArrayList<>();
      cells.add(parent_row);
      for (int j = 0; j < cols; j++) {
        cells.get(rows).add(null);
      }
    }
  }

  public void removeColumn(){
    int rows = getNumberOfRows();
    int cols = getNumberOfColumns();
    for(int j = 0; j < rows; j++){
      cells.get(j).remove(cols-1);
    }
  }

  public void removeRow(){
    int row = getNumberOfRows();
    cells.remove(row-1);
  }

  public int getNumberOfPlayers() {
    return players.size();
  }

  public void addPlayer(OXOPlayer player) {
    int num_of_players = getNumberOfPlayers();
    if(num_of_players < 2147483647) {
      players.add(player);
    }
  }

  public OXOPlayer getPlayerByNumber(int number) {
    OXOPlayer player_by_num = players.get(number);
    return player_by_num;
  }

  public OXOPlayer getWinner() {
    return winner;
  }

  public void setWinner(OXOPlayer player) {
    winner = player;
  }

  public int getCurrentPlayerNumber() {
    return currentPlayerNumber;
  }

  public void setCurrentPlayerNumber(int playerNumber) {
    currentPlayerNumber = playerNumber;
  }

  public int getNumberOfRows() {
    return cells.size();
  }

  public int getNumberOfColumns() {
    return cells.get(0).size();
  }

  public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
    return cells.get(rowNumber).get(colNumber);
  }

  public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
    cells.get(rowNumber).set(colNumber, player);
  }

  public void setWinThreshold(int winThresh) {
    winThreshold = winThresh;
  }

  public int getWinThreshold() {
    return winThreshold;
  }

  public void setGameDrawn() {
    gameDrawn = true;
  }

  public boolean isGameDrawn() {
    return gameDrawn;
  }


}

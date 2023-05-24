package edu.uob;
import edu.uob.OXOMoveException.*;

import java.sql.SQLOutput;

class OXOController {
    OXOModel gameModel;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    public void handleIncomingCommand(String command) throws OXOMoveException {
        int total_rows = gameModel.getNumberOfRows();
        int total_cols = gameModel.getNumberOfColumns();
        int total_moves = total_rows * total_cols;

        int length = command.length();
        if(length != 2){
            throw new InvalidIdentifierLengthException(length);
        }

        int row = command2row(command);
        int col = command2col(command);


       if(checkInBounds(row, col)) {
           gameModel.move++;

           if (gameModel.move == total_moves) {
               int curr_player_num = gameModel.getCurrentPlayerNumber();
               OXOPlayer player = gameModel.getPlayerByNumber(curr_player_num);
               gameModel.setCellOwner(row, col, player);
               gameModel.setGameDrawn();
           }

           boolean draw = gameModel.isGameDrawn();
           OXOPlayer winner = gameModel.getWinner();
           if (winner == null && !draw) {

               if (gameModel.getCellOwner(row, col) == null) {

                   int curr_player_num = gameModel.getCurrentPlayerNumber();
                   OXOPlayer player = gameModel.getPlayerByNumber(curr_player_num);
                   int num_players = gameModel.getNumberOfPlayers();
                   if (curr_player_num < (num_players-1)) {
                       gameModel.setCellOwner(row, col, player);
                       if (checkWin(player)) {
                           gameModel.setWinner(player);
                       }
                       int player_num = curr_player_num+1;
                       gameModel.setCurrentPlayerNumber(player_num);

                   } else {
                       gameModel.setCellOwner(row, col, player);
                       if (checkWin(player)) {
                           gameModel.setWinner(player);
                       }
                       gameModel.setCurrentPlayerNumber(0);
                   }
               } else {
                   char check_cell = gameModel.getCellOwner(row, col).getPlayingLetter();
                   if ((check_cell == 'X') || (check_cell == 'O')) {
                       throw new CellAlreadyTakenException(row, col);
                   }
               }
           }
       }
    }

    public boolean checkWinDraw(){
        int numPlayers = gameModel.getNumberOfPlayers();
        int i;
        int draw_count = 0;
        int curr_winner_num = 0;
        OXOPlayer player2check;
        for(i = 0; i < numPlayers; i++){
            player2check = gameModel.getPlayerByNumber(i);
            if(checkWin(player2check)){
                draw_count++;
                if(draw_count == 1){
                   curr_winner_num = i;
                }
                if(draw_count >= 2){
                    gameModel.setGameDrawn();
                    return true;
                }
            }
        }
        if(draw_count == 1){
            OXOPlayer winner = gameModel.getPlayerByNumber(curr_winner_num);
            gameModel.setWinner(winner);
            return true;
        }
       return false;
    }

    public void decreaseWinThreshold() {
        int curr_thres = gameModel.getWinThreshold();
        int new_thres = curr_thres - 1;
        if (new_thres < 1) {
            new_thres = 1;
        }
        gameModel.setWinThreshold(new_thres);

        checkWinDraw();
    }

    public boolean checkInBounds(int row, int col) throws OXOMoveException{
        int totalRows = gameModel.getNumberOfRows()-1;
        int totalCols = gameModel.getNumberOfColumns()-1;
        if((row > totalRows) && (col > totalCols)){
            throw new CellDoesNotExistException(row, col);
        }

        if(row > totalRows){
            throw new OutsideCellRangeException(RowOrColumn.ROW, row);
        }
        if(col > totalCols){
            throw new OutsideCellRangeException(RowOrColumn.COLUMN, col);
        }
        if(col < 0){
            throw new OutsideCellRangeException(RowOrColumn.COLUMN, col);
        }
        else{
            return true;
        }
    }


    public int command2row(String command) throws OXOMoveException {
        boolean letterFlag = Character.isLetter(command.charAt(0));
        if(!letterFlag){
            throw new InvalidIdentifierCharacterException(RowOrColumn.ROW, command.charAt(0));
        }

        else {
            char row1 = command.charAt(0);
            if(Character.isUpperCase(row1)){
                int row = row1 - 65;
                return row;
            }
            else {
                int row = row1 - 97;
                return row;
            }
        }
    }

    public int command2col(String command) throws OXOMoveException {
        boolean digitFlag = Character.isDigit(command.charAt(1));
        if(!digitFlag){
            throw new InvalidIdentifierCharacterException(RowOrColumn.COLUMN, command.charAt(1));
        }

        else {
            char col1 = command.charAt(1);
            int col = col1 - 49;
            return col;
        }
    }

    public void addRow() {
        gameModel.addRow();
    }

    public void removeRow() {
        gameModel.removeRow();
    }

    public void addColumn() {
        gameModel.addColumn();
    }

    public void removeColumn() {
        gameModel.removeColumn();
    }

    public void increaseWinThreshold() {
        int curr_thres = gameModel.getWinThreshold();
        if (curr_thres < 2147483647) {
            int new_thres = curr_thres + 1;
            gameModel.setWinThreshold(new_thres);
        }
    }


    public boolean checkWin(OXOPlayer player){
        if(rowWin(player)){
            return true;
        }
        if(colWin(player)){
            return true;
        }
        if(diag_check1(player)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean rowWin(OXOPlayer player) {
        int row_count = gameModel.getNumberOfRows();
        int col_count = gameModel.getNumberOfColumns();
        int win_threshold = gameModel.getWinThreshold();
        int win_count;
        int i, j;
        OXOPlayer cell_owner;
        for (i = 0; i < row_count; i++) {
            win_count = 0;
            for(j = 0; j < col_count; j++){
                cell_owner = gameModel.getCellOwner(i, j);
                if (cell_owner != null && cell_owner.getPlayingLetter() == player.getPlayingLetter()) {
                    win_count++;
                }
                else{
                    win_count = 0;
                }
                if(win_count == win_threshold){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean colWin(OXOPlayer player) {
        int col_count = gameModel.getNumberOfColumns();
        int win_threshold = gameModel.getWinThreshold();
        int win_count;
        int row_count = gameModel.getNumberOfRows();
        OXOPlayer cell_owner;
        int i, j;
        for (i = 0; i < col_count; i++) {
            win_count = 0;
            for(j = 0; j < row_count; j++){
                cell_owner = gameModel.getCellOwner(j, i);
                if (cell_owner != null && cell_owner.getPlayingLetter() == player.getPlayingLetter()) {
                    win_count++;
                }
                else{
                    win_count = 0;
                }
                if(win_count == win_threshold){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean diag_check1(OXOPlayer player){
        int i ;
        int j;
        int rows = gameModel.getNumberOfRows();
        int cols = gameModel.getNumberOfColumns();
        for(i = 0; i < rows; i++){
            for(j = 0; j < cols; j++){
                if(diagleftup(player, i, j)){
                    return true;
                }
                if(diagrightup(player, i, j)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean diagleftup(OXOPlayer player, int row, int col){
        int cols = gameModel.getNumberOfColumns();
        int count = 0;
        int win_thres = gameModel.getWinThreshold();
        OXOPlayer cell_owner;
        while(row >= 0 && col < cols){
            cell_owner = gameModel.getCellOwner(row, col);
            if(cell_owner != null && cell_owner.getPlayingLetter() == player.getPlayingLetter()){
                count++;
                if(count == win_thres){
                    return true;
                }
            }
            row--;
            col++;
        }
        return false;
    }

    public boolean diagrightup(OXOPlayer player, int row, int col){
        int count = 0;
        int win_thres = gameModel.getWinThreshold();
        OXOPlayer cell_owner;
        while(row >= 0 && col >= 0){
            cell_owner = gameModel.getCellOwner(row, col);
            if(cell_owner != null && cell_owner.getPlayingLetter() == player.getPlayingLetter()){
                count++;
                if(count == win_thres){
                    return true;
                }
            }
            row--;
            col--;
        }
        return false;
    }

}

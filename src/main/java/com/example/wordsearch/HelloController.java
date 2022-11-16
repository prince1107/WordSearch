package com.example.wordsearch;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class HelloController {
    Button[][] btn;
    String[][] board;
    private ArrayList<String> words = new ArrayList<> ();
    private ArrayList<Integer> directions = new ArrayList<>();

    @FXML
    private AnchorPane apane;
    @FXML
    private GridPane searchBoard;

    private String[] testWords = {"come", "bell", "bear", "play", "sing", "bird", "bean", "game", "rice", "four", "five", "tree", "keep", "dark", "moon", "cool","mat", "bat", "tag", "bag", "fan", "man", "gas", "ram", "rat", "can", "tan", "mad", "bad", "sad"};

    private ArrayList<String> wordList = new ArrayList<>();

    int length = 10;
    int height = 10;

    int wordStartx = -1;
    int wordStarty = -1;

    int wordEndx = -1;
    int wordEndy = -1;

    @FXML
    protected void handleClickMe(ActionEvent event) {
        for (String word: testWords) {
            wordList.add(word);
        }

        btn = new Button[length][height];
        board = new String[length][height];

        searchBoard.setGridLinesVisible(true);
        searchBoard.setVisible(true);
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < height; j++) {
                btn[i][j] = new Button("_");
                btn[i][j].setPrefSize(50,50);
                searchBoard.add(btn[i][j],j,i);
            }
        }

        EventHandler z = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //all code for the buttons goes here
                checkWord();
            }
        };
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < height; j++) {
                btn[i][j].setOnAction(z);
            }
        }

        //Example code will only do 1 word in 1 direction
        for (int i = 0; i < btn.length; i++) {
            for (int j = 0; j < btn.length; j++) {
                board[i][j] = "_";
            }
        }


        for (int r = 0; r < 30; r++) {
            fillWordSearch();
        }

        finishWordSearch();

        updateGraphics();
    }

    private void checkWord() {

    }

    private void finishWordSearch() {
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < height; j++) {
                if (board[i][j].equals("_")){
                    int asciiLetter = (int)(Math.random()*25 + 97);
                    char letter = (char)asciiLetter;
                    board[i][j] = String.valueOf(letter);
                }
            }
        }
    }

    private void fillWordSearch() {
        //directions start right at 0 and goes counterclockwise
        boolean boardWorks = false;
        String currentWord;
        directions.clear();
        int tryCount = 0;
        int rowStart;
        int colStart;

        do{
            //get random start location
            rowStart = (int)(Math.random ()*btn.length);
            colStart = (int) (Math.random ()*btn.length);
            System.out.println(colStart + ", " + rowStart);
            //get the first word out of the arraylist
            currentWord = wordList.get(0);
            //check if start position is empty or equal to the first letter of the word
            if (board[rowStart][colStart].equals("_") || currentWord.substring(0,1).equals(board[rowStart][colStart])) {
                //choose direction right check if their are enough buttons in that direction
                if (board.length-colStart>=currentWord.length()){
                    var directionWorks = true;
                    //check if each letter of the word con go in the buttons in that direction
                    for (int i = 0; i < currentWord.length(); i++) {
                        if (!(board[rowStart][colStart + i].equals(currentWord.substring(i, i + 1)) || board[rowStart][colStart + i].equals("_"))){
                            directionWorks = false;
                        }
                    }
                    //if it works store that direction in an array
                    if (directionWorks) {
                        directions.add(0);
                        System.out.println("right");
                    }
                }
            }
            if (board[rowStart][colStart].equals("_") || currentWord.substring(0,1).equals(board[rowStart][colStart])) {
                //choose direction down check if their are enough buttons in that direction
                if (board.length-rowStart>=currentWord.length()){
                    var directionWorks = true;
                    //check if each letter of the word con go in the buttons in that direction
                    for (int i = 0; i < currentWord.length(); i++) {
                        if (!(board[rowStart + i][colStart].equals(currentWord.substring(i, i + 1)) || board[rowStart + i][colStart].equals("_"))){
                            directionWorks = false;
                        }
                    }
                    //if it works store that direction in an array
                    if (directionWorks) {
                        directions.add(1);
                        System.out.println("down");
                    }
                }
            }
            if (board[rowStart][colStart].equals("_") || currentWord.substring(0,1).equals(board[rowStart][colStart])) {
                //choose direction left check if their are enough buttons in that direction
                if (colStart>=currentWord.length()){
                    var directionWorks = true;
                    //check if each letter of the word con go in the buttons in that direction
                    for (int i = 0; i < currentWord.length(); i++) {
                        if (!(board[rowStart][colStart - i].equals(currentWord.substring(i, i + 1)) || board[rowStart][colStart - i].equals("_"))){
                            directionWorks = false;
                        }
                    }
                    //if it works store that direction in an array
                    if (directionWorks) {
                        directions.add(2);
                    }
                }
            }
            if (board[rowStart][colStart].equals("_") || currentWord.substring(0,1).equals(board[rowStart][colStart])) {
                //choose direction up check if their are enough buttons in that direction
                if (rowStart>=currentWord.length()){
                    var directionWorks = true;
                    //check if each letter of the word con go in the buttons in that direction
                    for (int i = 0; i < currentWord.length(); i++) {
                        if (!(board[rowStart-i][colStart].equals(currentWord.substring(i, i + 1)) || board[rowStart-i][colStart].equals("_"))){
                            directionWorks = false;
                        }
                    }
                    //if it works store that direction in an array
                    if (directionWorks) {
                        directions.add(3);
                    }
                }
            }
            if (directions.size()>0) {
                boardWorks = true;
            }
            tryCount++;
        }while (!boardWorks && tryCount<10000);
        wordList.remove(0);
        if (tryCount >= 10000){
            System.out.println("didn't work");
        } else {
            if(directions.size()>0) {
                int index = (int) (Math.random() * directions.size());
                int directionUsed =  directions.get(index);
                if (directionUsed == 0) {
                    for (int i = 0; i < currentWord.length(); i++) {
                        board[rowStart][colStart + i] = currentWord.substring(i, i + 1);
                    }
                }
                else if (directionUsed == 1) {
                    for (int i = 0; i < currentWord.length(); i++) {
                        board[rowStart + i][colStart] = currentWord.substring(i, i + 1);
                    }
                }
                else if (directionUsed == 2) {
                    for (int i = 0; i < currentWord.length(); i++) {
                        board[rowStart][colStart - i] = currentWord.substring(i, i + 1);
                    }
                }
                else if (directionUsed == 3) {
                    for (int i = 0; i < currentWord.length(); i++) {
                        board[rowStart - i][colStart] = currentWord.substring(i, i + 1);
                    }
                }
            }
        }

    }

    public void updateGraphics() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                btn[i][j].setText(board[i][j]);
            }
        }
    }


}
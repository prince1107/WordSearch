package com.example.wordsearch;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class HelloController {

    @FXML
    private ListView wordsView, leaderboardView;
    private Button hardModeButton;
    private Button generalWordsButton;
    private Button spaceWordsButton;
    private Button oceanWordsButton;
    private Button easyModeButton;
    private Button hackButton;
    private Label timeLabel;
    private Label modeLabel;
    private Label themeLabel;
    private Button mediumModeButton;
    private Label nameLabel;
    private TextField nameField;

    private Button[][] btn;
    private String[][] board;
    private ArrayList<String> words = new ArrayList<>();
    private ArrayList<String> dictionary = new ArrayList<>();

    private ArrayList<String> leaderboardArray = new ArrayList<>();

    private ArrayList<Integer> directions = new ArrayList<>();

    @FXML
    private AnchorPane apane;
    @FXML
    private GridPane searchBoard;

    @FXML
    private Label wordsLabel, leaderboardLabel;

    private ArrayList<String> searchWords = new ArrayList<>();

    int length = 10;
    int height = 10;

    private int selectedRow;
    private int selectedColumn;

    int wordStartx = -1;
    int wordStarty = -1;

    int wordEndx = -1;
    int wordEndy = -1;

    double mode = 1;

    boolean hacked = false;

    int score = 0;
    Timer myTimer = new Timer();

    int time = 0;

    private String name = "player";


    @FXML
    protected void handleClickMe(ActionEvent event) {
        dictionary.clear();
        try{
            FileReader reader = new FileReader("src/main/resources/com/example/wordsearch/words_alpha.txt");
            Scanner in = new Scanner(reader);

            while(in.hasNext()){
                dictionary.add(in.next());
            }

        } catch (FileNotFoundException ex){
            System.out.println("Something is very wrong");
        }

        score = 0;

        time = 0;

        startTimer();

        searchWords.clear();

        int length = (int) (10 * mode);
        int height = (int) (10 * mode);

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
                selectedRow = GridPane.getRowIndex(((Button) event.getSource()));
                selectedColumn = GridPane.getColumnIndex((Button) event.getSource());
                checkWord(selectedRow, selectedColumn);
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


        for (int r = 0; r < 8 * mode; r++) {
            fillWordSearch();
        }

        finishWordSearch();

        updateGraphics();
    }

    private void startTimer() {
        myTimer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        timeLabel.setText("Time: " + time + " seconds");
                    }
                });
                System.out.println("Time: " + time + " seconds");
                time++;
            }
        }, 0, 1000);
    }

    private void checkWord(int row, int col) {
        timeLabel.setText("Time: " + time + " seconds");
        if (wordStartx == -1) {
            wordStartx = col;
            wordStarty = row;
        } else if (wordEndx == -1) {
            wordEndx = col;
            wordEndy = row;

            System.out.println(wordStartx + " " + wordEndx);
            System.out.println(wordStarty + " " + wordEndy);

            String word = "";

            int changex = wordEndx - wordStartx;
            int changey = wordEndy - wordStarty;
            double eqchangex = 0;
            double eqchangey = 0;
            if (changey == 0) {
                if (changex > 0) {
                    eqchangex = 1;
                } else {
                    eqchangex = -1;
                }
                eqchangey = 0;
            } else if (changex == 0) {
                if (changey > 0) {
                    eqchangey = 1;
                } else {
                    eqchangey = -1;
                }
                eqchangex = 0;
            } else if (!(changex == 0 && changey == 0)) {
                eqchangex = changex / Math.abs(changey);
                eqchangey = changey / Math.abs(changex);
            }


            if (!(eqchangex == 1 || eqchangex == -1 || eqchangex == 0)) {
                System.out.println("doesn't work");
            } else if (!(eqchangey == 1 || eqchangey == -1 || eqchangey == 0)) {
                System.out.println("doesn't work");
            } else {
                for (int i = 0; i <= Math.max(Math.abs(changex), Math.abs(changey)); i++) {
                        word += board[(int) (wordStarty + eqchangey * i)][(int) (wordStartx + eqchangex * i)];
                }
            }

            System.out.println(word);

            boolean wordInArray = false;

            for (String testword : searchWords) {
                if (testword.equals(word)) {
                    System.out.println(testword);
                    wordInArray = true;
                    for (int i = 0; i <= Math.max(Math.abs(changex), Math.abs(changey)); i++) {
                        btn[(int) (wordStarty + eqchangey * i)][(int) (wordStartx + eqchangex * i)].setStyle("-fx-background-color: #ffff00; ");
                    }
                }
            }
            
            if (wordInArray) {
                searchWords.remove(word);
                updateGraphics();
            } else if (dictionary.contains(word)) {
                score += 50;
                for (int i = 0; i <= Math.max(Math.abs(changex), Math.abs(changey)); i++) {
                    btn[(int) (wordStarty + eqchangey * i)][(int) (wordStartx + eqchangex * i)].setStyle("-fx-background-color: #ffff00; ");
                }
            }

            wordStartx = -1;
            wordEndx = -1;
            System.out.println(wordStartx + " " + wordEndx);
        }
        checkEnd();
    }

    private void checkEnd() {
        if (searchWords.size() == 0){
            myTimer.cancel();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            score += (500 - time) * 2 * mode;
            String leaderboard = name + "; Date - " + formatter.format(date) +  " Score - " + score;

            System.out.println(leaderboard);

            try {
                FileWriter output = new FileWriter("src/main/resources/com/example/wordsearch/output.txt", true);

                output.write(leaderboard + "\n");
                output.close();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        updateGraphics();
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

            currentWord = words.get((int) (Math.random() * words.size()));
            while (searchWords.contains(currentWord)){
                currentWord = words.get((int) (Math.random() * words.size()));
            }
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

            if (directions.contains(0) && directions.contains(1)) {
                //choose direction down right check if their are enough buttons in that direction
                if (board.length-colStart>=currentWord.length() && board.length-rowStart>=currentWord.length()){
                    var directionWorks = true;
                    //check if each letter of the word con go in the buttons in that direction
                    for (int i = 0; i < currentWord.length(); i++) {
                        if (!(board[rowStart+i][colStart+i].equals(currentWord.substring(i, i + 1)) || board[rowStart+i][colStart+i].equals("_"))){
                            directionWorks = false;
                        }
                    }
                    //if it works store that direction in an array
                    if (directionWorks) {
                        directions.add(4);
                    }
                }
            }

            if (directions.contains(0) && directions.contains(3)) {
                //choose direction down right check if their are enough buttons in that direction
                if (board.length-colStart>=currentWord.length() && rowStart>=currentWord.length()){
                    var directionWorks = true;
                    //check if each letter of the word con go in the buttons in that direction
                    for (int i = 0; i < currentWord.length(); i++) {
                        if (!(board[rowStart-i][colStart+i].equals(currentWord.substring(i, i + 1)) || board[rowStart-i][colStart+i].equals("_"))){
                            directionWorks = false;
                        }
                    }
                    //if it works store that direction in an array
                    if (directionWorks) {
                        directions.add(5);
                    }
                }
            }

            if (directions.contains(2) && directions.contains(1)) {
                //choose direction down right check if their are enough buttons in that direction
                if (colStart>=currentWord.length() && board.length-rowStart>=currentWord.length()){
                    var directionWorks = true;
                    //check if each letter of the word con go in the buttons in that direction
                    for (int i = 0; i < currentWord.length(); i++) {
                        if (!(board[rowStart+i][colStart-i].equals(currentWord.substring(i, i + 1)) || board[rowStart+i][colStart-i].equals("_"))){
                            directionWorks = false;
                        }
                    }
                    //if it works store that direction in an array
                    if (directionWorks) {
                        directions.add(6);
                    }
                }
            }

            if (directions.contains(2) && directions.contains(1)) {
                //choose direction down right check if their are enough buttons in that direction
                if (colStart>=currentWord.length() && rowStart>=currentWord.length()){
                    var directionWorks = true;
                    //check if each letter of the word con go in the buttons in that direction
                    for (int i = 0; i < currentWord.length(); i++) {
                        if (!(board[rowStart-i][colStart-i].equals(currentWord.substring(i, i + 1)) || board[rowStart-i][colStart-i].equals("_"))){
                            directionWorks = false;
                        }
                    }
                    //if it works store that direction in an array
                    if (directionWorks) {
                        directions.add(7);
                    }
                }
            }

            if (directions.size()>0) {
                boardWorks = true;
            }
            tryCount++;
        }while (!boardWorks && tryCount<10000);

        if (tryCount >= 10000){
            System.out.println(currentWord + " didn't work");
            words.remove(currentWord);
        } else {
            words.remove(currentWord);
            if(directions.size()>0) {
                searchWords.add(currentWord);
                int index = (int) (Math.random() * directions.size());
                int directionUsed =  directions.get(index);
                if (directionUsed == 0) {
                    for (int i = 0; i < currentWord.length(); i++) {
                        board[rowStart][colStart + i] = currentWord.substring(i, i + 1);
                        if (hacked) {
                            btn[rowStart][colStart + i].setStyle("-fx-background-color: #00ffff; ");
                        }
                    }
                }
                else if (directionUsed == 1) {
                    for (int i = 0; i < currentWord.length(); i++) {
                        board[rowStart + i][colStart] = currentWord.substring(i, i + 1);
                        if (hacked) {
                            btn[rowStart + i][colStart].setStyle("-fx-background-color: #00ffff; ");
                        }
                    }
                }
                else if (directionUsed == 2) {
                    for (int i = 0; i < currentWord.length(); i++) {
                        board[rowStart][colStart - i] = currentWord.substring(i, i + 1);
                        if (hacked) {
                            btn[rowStart][colStart - i].setStyle("-fx-background-color: #00ffff; ");
                        }
                    }
                }
                else if (directionUsed == 3) {
                    for (int i = 0; i < currentWord.length(); i++) {
                        board[rowStart - i][colStart] = currentWord.substring(i, i + 1);
                        if (hacked) {
                            btn[rowStart - i][colStart].setStyle("-fx-background-color: #00ffff; ");
                        }
                    }
                }
                else if (directionUsed == 4) {
                    for (int i = 0; i < currentWord.length(); i++) {
                        board[rowStart + i][colStart + i] = currentWord.substring(i, i + 1);
                        if (hacked) {
                            btn[rowStart + i][colStart + i].setStyle("-fx-background-color: #00ffff; ");
                        }
                    }
                }
                else if (directionUsed == 5) {
                    for (int i = 0; i < currentWord.length(); i++) {
                        board[rowStart - i][colStart + i] = currentWord.substring(i, i + 1);
                        if (hacked) {
                            btn[rowStart - i][colStart + i].setStyle("-fx-background-color: #00ffff; ");
                        }
                    }
                }
                else if (directionUsed == 6) {
                    for (int i = 0; i < currentWord.length(); i++) {
                        board[rowStart + i][colStart - i] = currentWord.substring(i, i + 1);
                        if (hacked) {
                            btn[rowStart + i][colStart - i].setStyle("-fx-background-color: #00ffff; ");
                        }
                    }
                }
                else if (directionUsed == 7) {
                    for (int i = 0; i < currentWord.length(); i++) {
                        board[rowStart - i][colStart - i] = currentWord.substring(i, i + 1);
                        if (hacked) {
                            btn[rowStart - i][colStart - i].setStyle("-fx-background-color: #00ffff; ");
                        }
                    }
                }
            }
        }
        updateGraphics();
    }

    protected void updateGraphics() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                btn[i][j].setText(board[i][j]);
            }
        }
        wordsView.getItems().clear();
        wordsView.getItems().addAll(searchWords);

        leaderboardArray.clear();
        try{
            FileReader reader = new FileReader("src/main/resources/com/example/wordsearch/output.txt");
            Scanner in = new Scanner(reader);

            while(in.hasNext()){
                leaderboardArray.add(in.nextLine());
            }

        } catch (FileNotFoundException ex){
            System.out.println("Something is very wrong");
        }

        leaderboardView.getItems().clear();
        leaderboardView.getItems().addAll(leaderboardArray);
    }


    protected void handleModeHard() {
        mode = 1.5;
    }

    protected void handleModeMedium() {
        mode = 1;
    }

    protected void handleModeEasy() {
        mode = 0.75;
    }

    protected void handleSetGeneralWords() {
        words.clear();
        try{
            FileReader reader = new FileReader("src/main/resources/com/example/wordsearch/~1000CommonWords.txt");
            Scanner in = new Scanner(reader);

            while(in.hasNext()){
                words.add(in.next());
            }

        } catch (FileNotFoundException ex){
            System.out.println("Something is very wrong");
        }
    }

    protected void handleSetSpaceWords() {
        words.clear();
        try{
            FileReader reader = new FileReader("src/main/resources/com/example/wordsearch/SpaceWords.txt");
            Scanner in = new Scanner(reader);

            while(in.hasNext()){
                words.add(in.next());
            }

        } catch (FileNotFoundException ex){
            System.out.println("Something is very wrong");
        }
    }

    protected void handleSetOceanWords() {
        words.clear();
        try{
            FileReader reader = new FileReader("src/main/resources/com/example/wordsearch/OceanWords.txt");
            Scanner in = new Scanner(reader);

            while(in.hasNext()){
                words.add(in.next());
            }

        } catch (FileNotFoundException ex){
            System.out.println("Something is very wrong");
        }
    }

    protected void handlehackedMode() {
        hacked = !hacked;
    }

    protected void handleGetName() {
        name = nameField.getText();
    }
}
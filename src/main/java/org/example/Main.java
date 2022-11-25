package org.example;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.Random;

public class Main {

    static int lives = 4;

    final static String death = "death.wav";
    final static String gameStart = "gameStart.wav";
    final static String hit = "hit.wav";
    final static String appleGet = "appleGet.wav";

    final static String upperBlock = "▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒";
    final static String lowerBlock = "▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒";

    public static void main(String[] args) throws Exception {

        // SETUP

        TerminalSize terminalSize = new TerminalSize(40, 15);
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory().setInitialTerminalSize(terminalSize);
        Terminal terminal = terminalFactory.createTerminal();

        terminal.setCursorVisible(false);
        TextGraphics textGraphics = terminal.newTextGraphics();
        textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
        textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

        terminal.flush();

        KeyStroke keyStroke = null;

        // TITLE SCREEN

        String title = "SIKTA MOT STJÄRNORNA © ™";
        String useNumpad = "Use ARROW KEYS to control";
        String toStart = "Press any button to start!";
        textGraphics.putString(9, 3, title, SGR.BOLD);
        textGraphics.putString(9, 6, useNumpad, SGR.BOLD);
        textGraphics.putString(8, 9, toStart, SGR.BLINK, SGR.BOLD);

        terminal.flush();

        while (true){
            do {
                keyStroke = terminal.pollInput();
            }
            while (keyStroke == null);

            Character c = keyStroke.getCharacter(); // used Character instead of char because it might be null
            if (c == keyStroke.getCharacter()) {
                terminal.clearScreen();
                terminal.flush();
                break;
            }
        }

        playSound(gameStart);

        final char apple = '✨';

        String playerStart = "►";

        Random r = new Random();

        Position applePosition = new Position(35, r.nextInt(13));
        terminal.setCursorPosition(applePosition.col, applePosition.row);
        terminal.putCharacter(apple);

        textGraphics.putString(0, 0, "Score: " + 0, SGR.BOLD);
        Position playerPosition = new Position(10, 10);

        textGraphics.putString(10, 10, playerStart, SGR.BOLD);

        handleHeart(terminal,lives);

        paintBorders(terminal);

        terminal.flush();

        KeyStroke latestKeyStroke = null;

        int score = 1;

        // THE GAME STARTS

        boolean continueReadingInput = true;
        while (continueReadingInput) {
            int index = 0;
            keyStroke = null;

            do {
                index++;
                if (index % 50 == 0) {
                    if (latestKeyStroke != null) {

                        if (applePosition.col == playerPosition.col && applePosition.row == playerPosition.row) {
                            // If player is at same position as apple, he/she gains points
                            textGraphics.putString(0, 0, "Score: " + score++, SGR.BOLD);
                            // Apple changes position if player eats it or if it's in the wall
                            applePosition = new Position(r.nextInt(37),r.nextInt(13));
                            if((applePosition.col > 37 || applePosition.col < 3) || (applePosition.row < 3 || applePosition.row > 14)) {
                                applePosition = new Position(10,10);
                            }
                            terminal.flush();
                            terminal.setCursorPosition(applePosition.col, applePosition.row);
                            terminal.putCharacter(apple);
                            terminal.flush();
                            playSound(appleGet);

                        }
                        handlePlayer(playerPosition, latestKeyStroke, terminal);
                    }
                }
                Thread.sleep(1);

                keyStroke = terminal.pollInput();

                // GAME OVER SCREEN

                if (lives == 0){
                    textGraphics.setForegroundColor(TextColor.ANSI.RED);
                    String gameOver= "GAME OVER (you are dead) :(";
                    textGraphics.putString(8, 7, gameOver, SGR.BOLD);
                    terminal.flush();
                    break;
                }

            } while (keyStroke == null);
            latestKeyStroke = keyStroke;

        }

    }

    public static void handleHeart (Terminal terminal, int lives) throws Exception {
        // Handle enemy
        lives--;
        String heart;

        if(lives == 3){
            heart = "♥♥♥♥";
        }
        else if(lives == 2){
            heart = "♡♥♥♥";
        }
        else if(lives == 1){
            heart = "♡♡♥♥";
        }
        else if(lives == 0){
            heart = "♡♡♡♥";
        }
        else{
            heart = "♡♡♡♡";
        }
        terminal.flush();

        TextGraphics textGraphics = terminal.newTextGraphics();
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

        textGraphics.putString(35, 0, heart, SGR.BOLD);

        terminal.flush();
        if (lives == -1){
            playSound(death);
        }
    }

    private static void handlePlayer (Position playerPosition, KeyStroke keyStroke, Terminal terminal) throws Exception {

        String delete = " ";

        String playerhead;

        Position oldPlayerPosition = new Position(playerPosition.col, playerPosition.row);

        TextGraphics textGraphics = terminal.newTextGraphics();
        textGraphics.setForegroundColor(TextColor.ANSI.MAGENTA);
        textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

        terminal.flush();

        switch(keyStroke.getKeyType()){
            case ArrowUp :
                playerPosition.row--;
                playerhead = "▲";
                break;
            case ArrowDown :
                playerhead = "▼";
                playerPosition.row++;
                break;
            case ArrowLeft :
                playerPosition.col--;
                playerhead = "◄";
                break;
            case ArrowRight :
                playerPosition.col++;
                playerhead = "►";
                break;
            default:
                playerPosition.col++;
                playerhead = "!";
                break;

        }

        if (playerPosition.col == 39){
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.setBackgroundColor(TextColor.ANSI.RED);
            playSound(hit);
            lives--;
            handleHeart(terminal, lives);
            playerPosition.col = oldPlayerPosition.col;
        }
        else if(playerPosition.col == 0){
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.setBackgroundColor(TextColor.ANSI.RED);
            playSound(hit);
            lives--;
            handleHeart(terminal, lives);
            playerPosition.col = oldPlayerPosition.col;
        }
        else if (playerPosition.row == 1){
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.setBackgroundColor(TextColor.ANSI.RED);
            playSound(hit);
            lives--;
            handleHeart(terminal, lives);
            playerPosition.row = oldPlayerPosition.row;
        }
        else if (playerPosition.row == 14){
            playSound(hit);
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.setBackgroundColor(TextColor.ANSI.RED);
            lives--;
            handleHeart(terminal, lives);
            playerPosition.row = oldPlayerPosition.row;
        }

        textGraphics.putString(oldPlayerPosition.col, oldPlayerPosition.row, delete, SGR.BOLD);

        textGraphics.putString(playerPosition.col, playerPosition.row, playerhead, SGR.BOLD);

        terminal.flush();
    }

    public static void playSound(String filepath){

        try{
            File musicPath = new File (filepath);
            if(musicPath.exists()){
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();

//                clip.loop(Clip.LOOP_CONTINUOUSLY);

            }

            else {
                System.out.println("Error:(");
            }
        }

        catch(Exception e){

        }
    }

    public static void paintBorders(Terminal terminal) throws Exception{
        TextGraphics textGraphics = terminal.newTextGraphics();
        textGraphics.setForegroundColor(TextColor.ANSI.BLUE);
        textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

        textGraphics.putString(0, 1, upperBlock, SGR.BOLD);  // Paint upper block
        textGraphics.putString(0, 14, lowerBlock, SGR.BOLD); // Paint lower block
        for (int i = 2; i < 14; i++){
            textGraphics.putString(0, i, "▒", SGR.BOLD);
        }

        for (int i = 2; i <= 14; ++i) {
            textGraphics.putString(39, i, "▒", SGR.BOLD);
        }


    }
}


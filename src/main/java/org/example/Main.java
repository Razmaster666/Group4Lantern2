package org.example;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.util.Random;

public class Main {
    static int lives = 4;
    public static void main(String[] args) throws Exception {
        // Setup
        TerminalSize terminalSize = new TerminalSize(40, 15);
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory().setInitialTerminalSize(terminalSize);
        Terminal terminal = terminalFactory.createTerminal();

        terminal.setCursorVisible(false);
        TextGraphics textGraphics = terminal.newTextGraphics();
        textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
        textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

        terminal.flush();

        KeyStroke keyStroke = null;

        String title = "❦ APPLE GAME ❦©™";
        String useNumpad = "Use NUMPAD to control";
        String toStart = "Press E to start!";
        textGraphics.putString(13, 3, title, SGR.BOLD);
        textGraphics.putString(10, 6, useNumpad, SGR.BOLD);
        textGraphics.putString(12, 9, toStart, SGR.BLINK, SGR.BOLD);

        terminal.flush();

        while (true){
            do {
                keyStroke = terminal.pollInput();
            }
            while (keyStroke == null);

            Character c = keyStroke.getCharacter(); // used Character instead of char because it might be null
            if (c == Character.valueOf('e')) {
                terminal.clearScreen();
                terminal.flush();
                break;
            }
        }
        // "Static" things
        final char apple = '❦';
//        int x = 10;
//        int y = 10;

        // Lets go

        String playerStart = "►";

        Random r = new Random();
//        Random randomchar = new Random();
        Position applePosition = new Position(r.nextInt(35), r.nextInt(13));
        terminal.setCursorPosition(applePosition.col, applePosition.row);
        terminal.putCharacter(apple);
        textGraphics.putString(0, 0, "Score: " + 0, SGR.ITALIC, SGR.CIRCLED);
        Position playerPosition = new Position(10, 10);

        Position enemyPosition = new Position(5, 5);

        textGraphics.putString(10, 10, playerStart, SGR.BOLD);

        terminal.flush();

        KeyStroke latestKeyStroke = null;

        int score = 1;
        int sum = 0;

        boolean continueReadingInput = true;
        while (continueReadingInput) {
            int index = 0;
            keyStroke = null;

            do {
                index++;
                if (index % 50 == 0) {
                    if (latestKeyStroke != null) {

                        if (applePosition.col == playerPosition.col && applePosition.row == playerPosition.row) {
//                          terminal.close();
                            textGraphics.putString(0, 0, "Score: " + score++, SGR.ITALIC, SGR.CIRCLED);
                            applePosition = new Position(r.nextInt(34),r.nextInt(13));// Apple changes position
                            terminal.flush();
                            terminal.setCursorPosition(applePosition.col, applePosition.row);
                            terminal.putCharacter(apple);
                            terminal.flush();

                            lives--;
                            handleHeart(terminal, lives);
//                          continueReadingInput = false;
                        }
                        handlePlayer(playerPosition, latestKeyStroke, terminal);

                    }
                }

                Thread.sleep(1); // might throw InterruptedException
                keyStroke = terminal.pollInput();


            } while (keyStroke == null);
            latestKeyStroke = keyStroke;

        }
    }

    private static void handleHeart (Terminal terminal, int lives) throws Exception {
        // Handle enemy
        lives--;
        String heart = "♡♡♡";

        if(lives == 3){
            heart = "♡♡♡";
        }
        else if(lives == 2){
            heart = " ♡♡";
        }
        else if(lives == 1){
            heart = "  ♡";
        }
        terminal.flush();

        TextGraphics textGraphics = terminal.newTextGraphics();
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

        textGraphics.putString(37, 0, heart, SGR.BOLD);

        terminal.flush();
    }

    private static void handlePlayer (Position playerPosition, KeyStroke keyStroke, Terminal terminal) throws Exception {
        // Handle player
        String delete = " ";

        String playerhead;

        Position oldPlayerPosition = new Position(playerPosition.col, playerPosition.row);

        TextGraphics textGraphics = terminal.newTextGraphics();
        textGraphics.setForegroundColor(TextColor.ANSI.MAGENTA);
        textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

        terminal.flush();

        boolean up = false;
        boolean down = false;
        boolean left = false;
        boolean right = false;

        switch(keyStroke.getKeyType()){
            case ArrowUp :
                playerPosition.row--;
                up = true;
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

        if (playerPosition.col == 38){
            playerPosition.col = oldPlayerPosition.col;
            lives--;
            handleHeart(terminal, lives);
            terminal.bell();
        }
        else if(playerPosition.col == 0){
            playerPosition.col = oldPlayerPosition.col;
        }
        else if (playerPosition.row == 0){
            playerPosition.row = oldPlayerPosition.row;
        }
        else if (playerPosition.row == 14){
            playerPosition.row = oldPlayerPosition.row;
        }

        textGraphics.putString(oldPlayerPosition.col, oldPlayerPosition.row, delete, SGR.BOLD);

        textGraphics.putString(playerPosition.col, playerPosition.row, playerhead, SGR.BOLD);

        terminal.flush();
    }
}


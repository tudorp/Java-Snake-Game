package com.zetcode;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {
    private final int DOT_SIZE = 10;
    private final int HORIZONTAL_DOTS = 50;
    private final int VERTICAL_DOTS = 50;
    private final int B_WIDTH = DOT_SIZE * HORIZONTAL_DOTS;
    private final int B_HEIGHT = DOT_SIZE * VERTICAL_DOTS;
    private final int ALL_DOTS = HORIZONTAL_DOTS * VERTICAL_DOTS;
    private final int DELAY = 240;

    private final int[] x = new int[ALL_DOTS];
    private final int[] y = new int[ALL_DOTS];

    private int worm_length;
    private int apple_x;
    private int apple_y;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;

    public Board() {
        
        initBoard();
    }
    
    private void initBoard() {

        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }

    private void loadImages() {

        ImageIcon iid = new ImageIcon("src/resources/dot.png");
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon("src/resources/apple.png");
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon("src/resources/head.png");
        head = iih.getImage();
    }

    private void initGame() {

        worm_length = 3;

        for (int z = 0; z < worm_length; z++) {
            x[z] = (HORIZONTAL_DOTS/2 - z) * DOT_SIZE;
            y[z] = (VERTICAL_DOTS/2) * DOT_SIZE;
        }
        
        relocateApple();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
    
    private void doDrawing(Graphics g) {
        
        if (inGame) {

            g.drawImage(apple, apple_x, apple_y, this);

            for (int z = 0; z < worm_length; z++) {
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }

            Toolkit.getDefaultToolkit().sync();

        } else {

            gameOver(g);
        }        
    }

    private void gameOver(Graphics g) {
        
        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
    }

    private void checkApple() {

        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            worm_length++;
            relocateApple();
        }
    }

    private void move() {
        // 0123456789
        //0
        //1
        //2
        //3
        //4
        //5    *---
        //6
        // 01234

        // worm_length=4
        //    0 1 2 3 4
        // x: 4 5 6 7
        // y: 5 5 5 5

        for (int z = worm_length; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

        // 0123456789
        //0
        //1
        //2
        //3
        //4
        //5    x--
        //6
        // 0123456

        // worm_length=4
        //    0 1 2 3 4
        // x: 4 4 5 6 7
        // y: 5 5 5 5 5

        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }
        // 0123456789
        //0
        //1
        //2
        //3
        //4
        //5   *---
        //6
        // 0123456

        // worm_length=4
        //    0 1 2 3 4
        // x: 3 4 5 6 7
        // y: 5 5 5 5 5

        if (rightDirection) { //not allowed when before we were moving to the left
            x[0] += DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
        }

        // 0123456789
        //0
        //1
        //2
        //3
        //4    *
        //5    |--
        //6
        // 0123456

        // worm_length=4
        //    0 1 2 3 4
        // x: 4 4 5 6 7
        // y: 4 5 5 5 5

        if (downDirection) {
            y[0] += DOT_SIZE;
        }
        // 0123456789
        //0
        //1
        //2
        //3
        //4
        //5    |--
        //6    *
        // 0123456

        // worm_length=4
        //    0 1 2 3 4
        // x: 4 4 5 6 7
        // y: 6 5 5 5 5
    }

    private void checkCollision() {

        for (int z = worm_length; z > 4; z--) {
            //only interested in the queue of the worm (ignore head+3)
            if ((x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }

        if (y[0] >= B_HEIGHT) {
            inGame = false;
        }

        if (y[0] < 0) {
            inGame = false;
        }

        if (x[0] >= B_WIDTH) {
            inGame = false;
        }

        if (x[0] < 0) {
            inGame = false;
        }
        
        if (!inGame) {
            timer.stop();
        }
    }

    private void relocateApple() {
        Random rand = new Random();
        int r = rand.nextInt(HORIZONTAL_DOTS);
        apple_x = ((r * DOT_SIZE));

        r = rand.nextInt(VERTICAL_DOTS);
        apple_y = ((r * DOT_SIZE));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {
            checkApple();
            checkCollision();
            move();
        }

        repaint();
    }

    //nested class
    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}

package bumblebee945.com.github.gamecode;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;

import static bumblebee945.com.github.gamecode.Displayer.color;

public class Main extends Application {
    static int nulls = 0;
    static int money = 0;
    static int sum;
    static Store store;
    static ArrayList<Die> die = new ArrayList<>();
    static ArrayList<Face> inventory = new ArrayList<>();
    static ArrayList<Bluff> bluff = new ArrayList<>();
    static Item selectedI;
    static boolean selected;
    static int bluffN;
    static Bluff bluffO;
    static boolean inBluff;
    static int board;
    static Stage cStage;

    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage stage) {
        Scene phs = new Scene(new Pane(), 720, 720); //placeholder scene
        stage.setTitle("Boxcars");
        stage.setScene(phs);
        stage.show();
        cStage = stage;

        /*for (int i = 0; i < 5; i++)
            die.add(new Die("basic", 0));*/
        for (int i = 0; i < 2; i++)
            die.add(Reader.getDie("basic", 0));
        die.add(Reader.randomDie());
        for (int i = 0; i < 2; i++)
            die.add(Reader.getDie("mult", 4));
        selected = false;

        //for (int i = 0; i < 5; i++)
         //   inventory.add(Reader.randomFace());

        board = 1;
        bluffN = 3;
        for (int i = 0; i < 4; i++)
            bluff.add(new Bluff(i));

        Displayer.display("board");
    }

    static void play(int num) {
        if (num == bluffN) {
            if (num == 1) {
                beat();
            } else {
                bluffO = bluff.get(num);
                inBluff = true;
                Displayer.display("bluff");
            }
        }
    }
    static void beat() {
        inBluff = false;
        if (bluffN == 2)
            bluffN--;
        if (--bluffN == -1) { //if done with this board
            if (++board == 7) { //if done with this game
                Displayer.display("win");
                return;
            }
            bluff.clear();
            for (int i = 0; i < 4; i++)
                bluff.add(new Bluff(i));
            bluffN = 3;
        }
        Displayer.display("board");
    }
}

class Displayer {
    static double pixel;
    static String screen;

    static void display(String type) {
        Scene scene = Main.cStage.getScene();
        pixel = scene.getWidth() / 550;
        screen = type;
        if (Main.selected) {
            Main.selected = false;
            Main.selectedI.selected = false;
            Main.selectedI = null;
        }
        Pane pane = new Pane();
        scene.setRoot(pane);
        scene.setFill(color(230));

        switch (type) {
            case "board" -> {
                pane.getChildren().addAll(boardTitle(), boardNum(), invButton());
                int i = 0;
                for (Bluff b : Main.bluff) {
                    i++;
                    if (i != 2)
                        pane.getChildren().addAll(b.getSquare(), b.getTitle(), b.getButton());
                }
            }
            case "inventory" -> pane.getChildren().add(inventory());
            case "bluff" -> pane.getChildren().add(bluff());
            case "reward" -> pane.getChildren().add(reward());
            case "store" -> pane.getChildren().add(store());
            case "replace" -> pane.getChildren().add(replace());
            //case "cashout" -> pane.getChildren().add(cashout());
            case "win" -> {
                pane.getChildren().add(getPane(0, 0,
                        getRect(550, 550, 0, 0, color(100), color(200)),
                        getText("You win!", 100, color(150))
                ));
                pane.setOnMouseClicked(e -> {
                    if (e.getButton().equals(MouseButton.PRIMARY))
                        System.exit(99);
                });
            }
            case "lose" -> {
                pane.getChildren().add(getPane(0, 0,
                        getRect(550, 550, 0, 0, color(100), color(200)),
                        getText("You lost!", 100, color(150))
                ));
                pane.setOnMouseClicked(e -> {
                    if (e.getButton().equals(MouseButton.PRIMARY))
                        System.exit(99);
                });
            }
        }
    }

    static Color color(String color) {
        return Color.valueOf(color);
    }
    static Color color(int color) {
        return Color.rgb(color, color, color);
    }
    static Rectangle getRect(int x, int y, int arc, int strokeWidth, Color stroke, Color fill) {
        Rectangle result = new Rectangle(0, 0, pixel*x, pixel*y);
        result.setStroke(stroke);
        result.setFill(fill);
        result.setArcHeight(arc);
        result.setArcWidth(arc);
        result.setStrokeWidth(strokeWidth);
        return result;
    }
    static Text getText(String text, int fontSize, Color fill) {
        return getText(text, fontSize, fill, false);
    }
    static Text getText(String text, int fontSize, Color fill, boolean italic) {
        Text result = new Text(0, 0, text);
        if (italic)
            result.setFont(Font.font("Futura", FontWeight.MEDIUM, FontPosture.ITALIC, fontSize));
        else
            result.setFont(Font.font("Futura", FontWeight.MEDIUM, FontPosture.REGULAR, fontSize));
        result.setFill(fill);
        result.setTextAlignment(TextAlignment.CENTER);
        return result;
    }
    static StackPane getPane(int layoutX, int layoutY, Node... nodes) {
        StackPane pane = new StackPane(nodes);
        pane.setLayoutX(pixel * layoutX);
        pane.setLayoutY(pixel * layoutY);
        return pane;
    }

    static Pane boardTitle() {
        Rectangle boardTitleRect = getRect(55, 30, 20, 2, color(100), color(240));
        Text boardTitleText = getText("Board", 20, color(70));
        return getPane(10, 10, boardTitleRect, boardTitleText);
    }
    static Pane boardNum() {
        Rectangle boardNumRect = getRect(25, 25, 20, 2, color(100), color(240));
        Text boardNumText = getText(String.valueOf(Main.board), 20, color(70));
        return getPane(25, 45, boardNumRect, boardNumText);
    }
    static Pane invButton() {
        Rectangle invRect = getRect(75, 30, 20, 2, color(100), color(240));
        Text invText = getText("Inventory", 20, color(70));
        Pane pane = getPane(465, 510, invRect, invText);

        pane.setOnMouseClicked(e -> {
                if (e.getButton().equals(MouseButton.PRIMARY))
                    display("inventory");
            });

        return pane;
    }
    static Pane inventory() {
        Pane pane = new Pane();

        Rectangle bg = getRect(550, 550, 0, 0, color(0), color("C2D1EEFF"));
        pane.getChildren().add(getPane(0, 0, bg));
        bg = getRect(550, 225, 0, 0, color(0), color("C6D9E9FF"));
        pane.getChildren().add(getPane(0, 325, bg));

        for (int i = 0; i < 5; i++) { // dice
            Die die = Main.die.get(i);
            Rectangle rect = getRect(180, 55, 5, 2, color(0), color(240));
            int fontSize = 45;
            if (die.name.length() > 13)
                fontSize = 30;
            Text text = getText(die.name, fontSize, color(0));
            pane.getChildren().add(getPane(5, 5 + i*65, rect, text));

            for (int j = 0; j < 6; j++) { // dice faces
                Face face = die.faces.get(j);
                Pane facePane = face.getSquare(55, 55, 190 + (j*60), 5 + (i*65));
                pane.getChildren().add(facePane);
            }
        }

        Rectangle rect = getRect(120, 55, 3, 2, color(0), color(255));
        Text text = getText("Back", 40, color(0));
        Pane backPane = getPane(20, 340, rect, text);
        backPane.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && !Main.inBluff)
                display("board");
            else if (e.getButton().equals(MouseButton.PRIMARY))
                display("bluff");
        });
        pane.getChildren().add(backPane);

        rect = getRect(250, 55, 5, 2, color(0), color(240));
        text = getText("Inventory", 50, color(0));
        pane.getChildren().add(getPane(150, 340, rect, text));

        rect = getRect(120, 55, 3, 2, color(0), color(240));
        text = getText("Sell", 40, color(0));
        Pane sellPane = getPane(410, 340, rect, text);
        sellPane.setOnMouseClicked(_ -> Item.sell());
        pane.getChildren().add(sellPane);

        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 8; j++)
                if (Main.inventory.size() > (j + (i * 8))) {
                    Pane facePane = Main.inventory.get(j + (i * 8)).getSquare(55, 55, 20 + (j*65), 415 + (i*65));
                    pane.getChildren().add(facePane);
                } else
                    pane.getChildren().add(getPane(20 + (j*65), 415 + (i*65),
                        getRect(55, 55, 3, 2, color(0), color(255))));

        return pane;
    }
    static Pane bluff() {
        Pane pane = new Pane();
        String ba = Main.bluffO.getAction();
        Bluff bo = Main.bluffO;

        Rectangle bg = getRect(548, 550, 0, 3, color(0), color("C6D9E9FF"));
        pane.getChildren().add(getPane(0, 0, bg));
        bg = getRect(548, 118, 0, 3, color(0), color("9ABCDFFF"));
        pane.getChildren().add(getPane(0, 430, bg));

        Rectangle rect = getRect(548, 60, 0, 3, color(0), color("C7D6DAFF"));
        Text text = getText("Small Bluff", 40, color(0));
        pane.getChildren().add(getPane(0, 0, rect, text));

        for (int i = 0; i < 5; i++) {
            rect = getRect(90, 90, 0, 0, color(0), color("7DA2C8FF"));
            pane.getChildren().add(getPane(20 + (105 * i), 445, rect));
            if (ba.equals("turnRolled") || ba.equals("turnPlayed")) {
                Die die = Main.die.get(i);
                Pane diePane = die.getSquare
                        (90, 90, 20 + (105 * i), 445);
                if (bo.hide > 0)
                    bo.hide--;
                else
                    pane.getChildren().add(diePane);
            }
        }

        rect = getRect(238, 115, 0, 3, color(0), color(200));
        text = getText("Dice", 120, color(50), true);
        if (ba.equals("turnStart")) {
            rect.setFill(color("F4E8C4FF"));
            text.setFill(color("7B5C06FF"));
        }
        Pane dicePane = getPane(310, 59, rect, text);
        dicePane.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && ba.equals("turnStart"))
                display("inventory");
        });
        pane.getChildren().add(dicePane);

        rect = getRect(238, 80, 0, 3, color(0), color(250));
        if (ba.equals("turnPlayed"))
            text = getText("X", 80, color(140));
        else if (ba.equals("turnTotal"))
            text = getText(String.format("%d pips", bo.total), 60, color(50));
        else
            text = getText(". . .", 80, color(50), true);
        Pane rrPane = getPane(310, 175, rect, text);
        rrPane.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY))
                for (Node n : rrPane.getChildren())
                    if (n instanceof Text && ((Text) n).getText().equals("Reroll")) {
                        ((Text) n).setText(". . .");
                        ((Die) Main.selectedI).roll();
                        Main.selectedI.unselect();
                        bo.rerolls--;
                        Displayer.display("bluff");
                    }
        });
        pane.getChildren().add(rrPane);

        if (ba.equals("turnPlayed")) {
            //base of 310, 175
            rect = getRect(97, 30, 0, 0, color(0), color(250));
            text = getText(String.format("%d", bo.pips), 60, color(80));
            pane.getChildren().add(getPane(318, 178, rect, text));
            rect = getRect(97, 20, 0, 0, color(0), color(250));
            text = getText("pips", 30, color(100));
            pane.getChildren().add(getPane(318, 223, rect, text));
            rect = getRect(97, 30, 0, 0, color(0), color(250));
            text = getText(String.format("%d", bo.mult), 60, color(80));
            pane.getChildren().add(getPane(445, 178, rect, text));
            rect = getRect(97, 20, 0, 0, color(0), color(250));
            text = getText("times", 30, color(100));
            pane.getChildren().add(getPane(445, 223, rect, text));
        }

        rect = getRect(238, 115, 0, 3, color(0), color(200));
        if (ba.equals("turnStart"))
            text = getText("Roll", 120, color(0), true);
        else
            text = getText("Play", 120, color(50), true);
        if (ba.equals("turnStart") || ba.equals("turnRolled")) {
            rect.setFill(color("C1D0BBFF"));
            text.setFill(color("38721FFF"));
        }
        Pane playPane = getPane(310, 254, rect, text);
        playPane.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && ba.equals("turnStart"))
                bo.roll();
            else if (e.getButton().equals(MouseButton.PRIMARY) && ba.equals("turnRolled"))
                bo.play();
        });
        pane.getChildren().add(playPane);

        rect = getRect(310, 60, 0, 3, color(0), color("C2D1EEFF"));
        text = getText(String.format("%d rerolls left!", bo.getRerolls()), 40, color(0), true);
        pane.getChildren().add(getPane(0, 370, rect, text));

        rect = getRect(238, 60, 0, 3, color(0), color("C6D9E9FF"));
        text = getText(String.format("%d turns left!", bo.getTurns()), 40, color(0), true);
        pane.getChildren().add(getPane(310, 370, rect, text));

        Circle circ = new Circle(0, 0, 140*pixel);
        circ.setFill(color("E9E9E9FF"));
        circ.setStroke(color("424242FF"));
        circ.setStrokeWidth(8);
        text = getText(String.format("\n%d\n", Main.bluffO.getCountdown()), 100, color(0));
        Text text2 = getText("\n\n\n\n\nto win\n", 30, color(50));
        pane.getChildren().add(getPane(13, 73, circ, text, text2));

        return pane;
    }
    static Pane reward() {
        Pane pane = new Pane();

        Rectangle bg = getRect(548, 550, 0, 3, color(0), color("C6D9E9FF"));
        pane.getChildren().add(getPane(0, 0, bg));
        bg = getRect(548, 118, 0, 3, color(0), color("9ABCDFFF"));
        pane.getChildren().add(getPane(0, 430, bg));

        Rectangle rect = getRect(548, 60, 0, 3, color(0), color("C7D6DAFF"));
        Text text = getText("Rewards!", 40, color(0));
        pane.getChildren().add(getPane(0, 0, rect, text));

        Main.sum = 0;
        for (int i = 0, j = 0; i < 6; i++, j++) {
            String desc = "";
            int repeat = 0;
            switch (i) {
                case 0:
                    repeat = (Main.bluffN == 3 ? 4 : (Main.bluffN == 2 ? 5 : 6));
                    desc = "Bluff completion";
                    break;
                case 1:
                    if ((repeat = (Main.bluffO.getTurns()) - 1) > 0)
                        desc = String.format("%d remaining turns ($1 each)", repeat);
                    break;
                case 2:
                    if (Main.money > 5) {
                        repeat = (Math.min(Main.money / 6, 6));
                        desc = String.format("%d interest per $6 (6 max)", repeat);
                    }
                    break;
                case 3:
                    int original = new Bluff(Main.bluffN).getCountdown();
                    int current = Main.bluffO.getCountdown();
                    if (original * -1 >= current) {
                        repeat++;
                        desc = "Overkill!";
                        if (original * -3 >= current) {
                            repeat++;
                            desc = "Triple overkill!!";
                            if (original * -5 >= current) {
                                repeat++;
                                desc = "Quintuple overkill!!!";
                            }
                        }
                    }
                    break;
            }
            if (!desc.isEmpty()) {
                rect = getRect(115, 40, 50, 3, color(0), color("F4E8C4FF"));
                text = getText("$".repeat(repeat), 35, color("7B5C06FF"));
                Main.sum += repeat;
                pane.getChildren().add(getPane(35, 85 + (55 * j), rect, text));
                rect = getRect(355, 40, 50, 3, color(0), color(233));
                text = getText(desc, 35, color(66));
                pane.getChildren().add(getPane(160, 85 + (55 * j), rect, text));
            } else
                j--;
        }

        rect = getRect(115, 70, 70, 3, color(0), color("F4E8C4FF"));
        text = getText(String.format("$%d", Main.sum), 70, color("7B5C06FF"));
        pane.getChildren().add(getPane(35, 455, rect, text));

        rect = getRect(355, 70, 70, 3, color(0), color(233));
        text = getText("Collect", 60, color(66));
        Pane collectPane = getPane(160, 455, rect, text);
        collectPane.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                Main.money += Main.sum;
                Main.store = new Store();
                Displayer.display("store");
            }
        });
        pane.getChildren().add(collectPane);

        return pane;
    }
    static Pane store() {
        Pane pane = new Pane();

        Rectangle bg = getRect(548, 370, 0, 3, color(0), color("C7D6DAFF"));
        pane.getChildren().add(getPane(0, 0, bg));
        bg = getRect(548, 178, 0, 3, color(0), color("C6D9E9FF"));
        pane.getChildren().add(getPane(0, 370, bg));

        Rectangle rect = getRect(130, 40, 5, 3, color(0), color(255));
        Text text = getText("Back", 35, color(0));
        Pane backPane = getPane(15, 10, rect, text);
        backPane.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY))
                Main.beat();
        });
        pane.getChildren().add(backPane);
        rect = getRect(230, 40, 5, 3, color(0), color(255));
        text = getText("Investment!", 35, color(0));
        pane.getChildren().add(getPane(160, 10, rect, text));
        rect = getRect(130, 40, 5, 3, color(0), color(255));
        text = getText(String.format("%d gold", Main.money), 35, color(0));
        pane.getChildren().add(getPane(405, 10, rect, text));

        for (int i = 0; i < 3; i++) {
            rect = getRect(170, 180, 2, 3, color(0), color("C2D1EEFF"));
            pane.getChildren().add(getPane(10 + (180 * i), 60, rect));
            if (Main.store.item.size() > i) {
                Item item = Main.store.item.get(i);
                if (item instanceof Die) {
                    Pane facePane = item.getSquare(60, 60, 70 + (180 * i), 95);
                    pane.getChildren().add(facePane);
                }
                Pane facePane = item.getSquare(60, 60, 65 + (180 * i), 100);
                pane.getChildren().add(facePane);
                rect = getRect(140, 45, 3, 3, color(0), color(255));
                if (i == Main.store.selected)
                    rect.setStroke(color("DCBE1EFF"));
                text = getText(item.getName(), 25, color(0));
                Pane namePane = getPane(25 + (180 * i), 180, rect, text);
                namePane.setOnMouseClicked(e -> {
                    if (e.getButton().equals(MouseButton.PRIMARY))
                        Main.store.select(namePane);
                });
                pane.getChildren().add(namePane);
            }
        }

        if (Main.store.dPanes == 4) {
            System.out.printf("M.s.d: P %d, T %s, N %s, R %s, D %s\n",
                    Main.store.dPanes, Main.store.dType, Main.store.dName, Main.store.dRarity, Main.store.dDesc);
            rect = getRect(130, 50, 5, 3, color(0), color(255));
            text = getText(Main.store.dType, 40, color(0));
            pane.getChildren().add(getPane(15, 250, rect, text));
            rect = getRect(230, 50, 5, 3, color(0), color(255));
            text = getText(Main.store.dName, 35, color(0));
            pane.getChildren().add(getPane(160, 250, rect, text));
            rect = getRect(130, 50, 5, 3, color(0), color(255));
            text = getText(Main.store.dRarity, 40, color(0));
            pane.getChildren().add(getPane(405, 250, rect, text));
        } else {
            rect = getRect(520, 50, 5, 3, color(0), color(255));
            text = getText(Main.store.dName, 35, color(0));
            pane.getChildren().add(getPane(15, 250, rect, text));
        }
        rect = getRect(520, 50, 5, 3, color(0), color(255));
        text = getText(Main.store.dDesc, 30, color(0));
        pane.getChildren().add(getPane(15, 310, rect, text));

        rect = getRect(190, 150, 5, 3, color(0), color(233));
        pane.getChildren().add(getPane(15, 385, rect));
        if (Main.store.selected != -1) {
            rect = getRect(160, 50, 30, 3, color("E6BA32FF"), color("EDE0BDFF"));
            text = getText(String.format("%d gold", Main.store.dCost), 30, color("7A5D06FF"));
            pane.getChildren().addAll(Main.store.buyButton(), getPane(30, 400, rect, text));
        }

        /*rect = getRect(150, 150, 5, 3, color(0), color(255));
        if (Main.store.selected == 10)
            rect.setStroke(color("DCBE1EFF"));
        text = getText("Face\nBox", 60, color(0), true);
        Pane boxPane = getPane(220, 385, rect, text);
        boxPane.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY))
                Main.store.select(boxPane);
        });
        pane.getChildren().add(boxPane);
        rect = getRect(150, 150, 5, 3, color(0), color(255));
        if (Main.store.selected == 11)
            rect.setStroke(color("DCBE1EFF"));
        text = getText("Dice\nBox", 60, color(0), true);
        Pane boxPane2 = getPane(385, 385, rect, text);
        boxPane2.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY))
                Main.store.select(boxPane2);
        });
        pane.getChildren().add(boxPane2);*/

        return pane;
    }
    static Pane replace() {
        Pane pane = new Pane();

        Rectangle bg = getRect(548, 60, 0, 3, color(0), color("C7D6DAFF"));
        pane.getChildren().add(getPane(0, 0, bg));
        bg = getRect(548, 490, 0, 3, color(0), color("C6D9E9FF"));
        pane.getChildren().add(getPane(0, 60, bg));

        Rectangle rect = getRect(520, 40, 5, 3, color(0), color(255));
        Text text = getText("Replace which die?", 35, color(0));
        pane.getChildren().add(getPane(15, 10, rect, text));

        Pane diePane;
        for (int i = 0; i < 2; i++) {
            rect = getRect(150, 150, 5, 3, color(0), color(255));
            text = getText(Main.die.get(i).getName().replace(" ", "\n"), 40, color(0));
            diePane = getPane(30 + (i * 340), 150, rect, text);
            int finalI = i;
            diePane.setOnMouseClicked(_ -> Main.store.replace(finalI));
            pane.getChildren().add(diePane);
        }
        rect = getRect(150, 150, 5, 3, color("DCBE1EFF"), color(255));
        text = getText(Main.store.item.get(Main.store.selected).getName().replace(" ", "\n"), 40, color(0));
        pane.getChildren().add(getPane(200, 150, rect, text));
        for (int i = 2; i < 5; i++) {
            rect = getRect(150, 150, 5, 3, color(0), color(255));
            text = getText(Main.die.get(i).getName().replace(" ", "\n"), 40, color(0));
            diePane = getPane(30 + ((i - 2) * 170), 320, rect, text);
            int finalI = i;
            diePane.setOnMouseClicked(_ -> Main.store.replace(finalI));
            pane.getChildren().add(diePane);
        }

        return pane;
    }
    /*static Pane cashout() {
        Pane pane = new Pane();
        return pane;
    }*/

}

class Store {
    ArrayList<Item> item = new ArrayList<>();

    int dPanes;
    String dType;
    String dName;
    String dRarity;
    String dDesc;
    int selected;
    int dCost;

    Store() {
        for (int i = 0; i < 3; i++) {
            int rando = (int) (Math.random() * 4);
            item.add(switch (rando) {
                case 0 -> Reader.getFace("mult", (int) (Math.random() * 3) + 2);
                case 1 -> Reader.getFace("pip", (int) (Math.random() * 3) + 4);
                case 2 -> Reader.getDie("mult", (int) (Math.random() * 3) + 2);
                default -> Reader.getDie("basic", 0);
            });
        }

        reset();
    }

    void select(Pane pane) {
        int paneX = (int) (pane.getLayoutX() / Displayer.pixel);
        if (selected == -1) {
            System.out.println("Selected -1");
            if ((pane.getLayoutY() / Displayer.pixel) < 300) {
                selected = paneX / 180;
                dPanes = 4;
                dType = item.get(selected) instanceof Die ? "Die" : "Face";
                dName = item.get(selected).getName();
                dRarity = item.get(selected).rarity.substring(0, 1).toUpperCase() + item.get(selected).rarity.substring(1);
                dDesc = item.get(selected).getDesc();
                dCost = item.get(selected).cost;
            } else {
                selected = 10 + (paneX / 230);
                dPanes = 2;
                if (paneX / 230 == 0) {
                    dName = "Face Box";
                    dDesc = "Open it up and pick from two random faces!";
                    dCost = 4;
                } else {
                    dName = "Dice Box";
                    dDesc = "Open it up and pick from two random dice!";
                    dCost = 6;
                }
            }
        } else {
            System.out.println("Selected !-1");
            int temp1 = selected;
            int temp2;
            if ((pane.getLayoutY() / Displayer.pixel) < 300)
                temp2 = paneX / 180;
            else
                temp2 = 10 + (paneX / 230);
            reset();
            if (temp1 != temp2)
                select(pane);
        }
        System.out.println("Displaying store");
        System.out.printf("d: P %d, T %s, N %s, R %s, D %s\n", dPanes, dType, dName, dRarity, dDesc);
        Displayer.display("store");
    }

    void reset() {
        System.out.println("Store reset");
        dPanes = 4;
        selected = -1;
        dType = "";
        dName = "";
        dRarity = "";
        dDesc = "";
    }

    Pane buyButton() {
        Rectangle rect = Displayer.getRect(130, 40, 50, 3, color("66A14EFF"), color("D0E0CBFF"));
        Text text = Displayer.getText("Buy", 30, color("294B15FF"));
        Pane buyPane = Displayer.getPane(45, 475, rect, text);

        buyPane.setOnMouseClicked(_ -> buy());

        return buyPane;
    }

    void buy() {
        int itemCost = switch (selected) {
            case 10 -> 4;
            case 11 -> 6;
            default -> item.get(selected).cost;
        };

        if (Main.money < itemCost)
            return;
        if (Main.inventory.size() > 15)
            return;
        if (Main.inventory.size() > 10 && item.get(selected) instanceof Die)
            return;

        Main.money -= itemCost;

        if (item.get(selected) instanceof Face) {
            Main.inventory.add((Face)item.get(selected));
            item.remove(selected);
            reset();
            Displayer.display("store");
        } else {
            System.out.println("replace");
            Displayer.display("replace");
        }
    }
    void replace(int die) {
        for (int i = 0; i < 6; i++)
            Main.inventory.add(Main.die.get(die).getFace(i));
        Main.die.set(die, (Die)item.get(selected));
        item.remove(selected);
        reset();
        Displayer.display("store");
    }
}

class Bluff {
    //attributes
    int num;
    int rerolls;
    int turns;
    int total;
    int pips;
    int mult;
    int hide;
    int countdown;
    String action;

    //constructors
    Bluff(int num) {
        this.num = num;
        this.turns = 3;
        this.rerolls = 3;
        this.mult = 1;
        this.pips = 0;
        this.hide = 0;
        this.action = "turnStart";

        switch (Main.board) {
            case 1: countdown = 20; break;
            case 2: countdown = 50; break;
            case 3: countdown = 140; break;
            case 4: countdown = 320; break;
            case 5: countdown = 800; break;
            case 6: countdown = 1400; break;
            case 7: countdown = 2400; break;
            case 8: countdown = 3400; break;
        }

        if (num == 2)
            this.countdown = (int)(this.countdown * 1.5);
        else if (num == 0)
            this.countdown *= 2;

    }

    //accessors
    String getAction() { return action; }
    int getCountdown() { return countdown; }
    int getRerolls() { return rerolls; }
    int getTurns() { return turns; }
    Pane getSquare() {
        Rectangle squareRect = Displayer.getRect(125, 125, 10, 3,
                (num < Main.bluffN ? color(50) : (num == Main.bluffN ? color(70) : color(30))), color(0)); // (? upcoming : (? current : beaten))
        switch (num) {
            case 0: squareRect.setFill(color("E09494FF")); break;
            case 1: squareRect.setFill(color("F1DCC5FF")); break;
            case 2: squareRect.setFill(color("E8ABABFF")); break;
            case 3: squareRect.setFill(color("EAC4C4FF")); break;
        }

        return Displayer.getPane(75, 10 + (num*135), squareRect);
    }
    Pane getTitle() {
        Rectangle titleRect = Displayer.getRect(250, 50, 35, 2, color(100), color(240));

        String titleS = switch (num) {
            case 0 -> "Boss Bluff";
            case 1 -> "Cash Out";
            case 2 -> "Big Bluff";
            case 3 -> "Small Bluff";
            default -> "";
        };
        Text titleText = Displayer.getText(titleS, 50, color(70));

        return Displayer.getPane(225, 25 + (num*135), titleRect, titleText);
    }
    Pane getButton() {
        Rectangle buttonRect = Displayer.getRect(190, 30, 35, 2, color(150), color(200));
        Text buttonText = Displayer.getText("Defeated", 30, color(100));

        if (num < Main.bluffN) { // upcoming
            buttonRect.setStroke(color("E6BA32FF"));
            buttonRect.setFill(color("EDE0BDFF"));
            buttonText.setFill(color("7A5D06FF"));
            buttonText.setText("Upcoming");
        } else if (num == Main.bluffN) { // current
            buttonRect.setStroke(color("66A14EFF"));
            buttonRect.setFill(color("D0E0CBFF"));
            buttonText.setFill(color("294B15FF"));
            buttonText.setText("Play");
        } // beaten is default buttonText

        StackPane button = Displayer.getPane(255, 90 + (num*135), buttonRect, buttonText);

        button.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY))
                Main.play(num);
        });

        return button;
    }

    //methods
    void roll() {
        action = "turnRolled";

        for (Die d : Main.die) {
            d.roll();
        }

        Displayer.display("bluff");
    }
    void play() {
        action = "turnPlayed";
        Displayer.display("bluff");
        play(1);
    }
    void play(int i) {
        PauseTransition pt = new PauseTransition(Duration.millis(500));
        if (i == 6) {
            pt.setOnFinished(_ -> {
                total = pips * mult;
                total(0);
            });
        } else {
            pt.setOnFinished(_ -> {
                hide = i;
                playDie(Main.die.get(i - 1), i);
                Displayer.display("bluff");
            });
        }
        pt.play();
    }
    void playDie(Die die, int i) {
        Face face = die.getFace();
        if (face.type.equals("pip"))
            pips += face.value;
        else if (face.type.equals("mult"))
            mult *= face.value;

        if (die.type.equals("basic")) {
            play(i + 1);
            return;
        }

        PauseTransition pt = new PauseTransition(Duration.millis(500));
        pt.setOnFinished(_ -> {
            if (die.type.equals("mult"))
                mult *= die.value;
            hide = i;
            Displayer.display("bluff");
            play(i + 1);
        });

        pt.play();

    }
    void total(int delay) {
        if (delay != 0) {
            PauseTransition pt = new PauseTransition(Duration.millis(delay));
            pt.setOnFinished(_ -> {
                int digits = Integer.toString(total).length();
                int subtract = (int) (Math.pow(10, digits) / 10);
                if (digits > 1) {
                    char first = Integer.toString(total).charAt(0);
                    char second = Integer.toString(total).charAt(1);
                    if (first == '1')
                        subtract = (int) (Math.pow(10, digits) / 100) * ((second - '0') + 1);
                }
                countdown -= subtract;
                total -= subtract;
                Displayer.display("bluff");
                if (total > 0)
                    total(delay);
                else
                    endTurn(true);
            });
            pt.play();
        } else {
            action = "turnTotal";
            Displayer.display("bluff");
            int digits = Integer.toString(total).length();
            int newDelay = (int) (Math.pow(0.75, digits) * 100);
            PauseTransition pt = new PauseTransition(Duration.millis(500));
            pt.setOnFinished(_ -> total(newDelay));
            pt.play();
        }
    }

    void endTurn(boolean wait) {
        if (!wait) {
            if (countdown > 0) {
                turns--;
                rerolls = 3;
                mult = 1;
                pips = 0;
                hide = 0;
                action = "turnStart";
                if (turns > 0)
                    Displayer.display("bluff");
                else
                    Displayer.display("lose");
            } else {
                Displayer.display("reward");
                action = "";
            }
        } else {
            PauseTransition pt = new PauseTransition(Duration.millis(500));
            pt.setOnFinished(_ -> endTurn(false));
            pt.play();
        }
    }

    //mutators
    //void setAction(String action) { this.action = action; }
}

class Item {
    int cost = 0;
    int value;
    String name;
    String type;
    String desc;
    String rarity;
    int x = 0;
    int y = 0;
    int lx = 0;
    int ly = 0;
    boolean selected = false;
    Pane pane = new Pane();

    String getName() {
        return this.name;
    }
    String getDesc() {
        return this.desc;
    }
    Pane getSquare(int sizeX, int sizeY, int layoutX, int layoutY) {
        x = sizeX;
        y = sizeY;
        lx = layoutX;
        ly = layoutY;
        makePane();
        return pane;
    }

    void makePane() {
        Color stroke;
        Color fill;
        if (this instanceof Face) {
            stroke = ((Face)this).die.stroke;
            fill = ((Face)this).fill;
        } else {
            stroke = ((Die)this).stroke;
            fill = ((Die)this).getFace().fill;
        }

        String type = (this instanceof Die ? ((Die)this).getFace().type : this.type);
        int value = (this instanceof Die ? ((Die)this).getFace().value : this.value);

        Rectangle faceRect = Displayer.getRect(x, y, 5, 2, stroke, fill);
        Text faceText = Displayer.getText("", 50, color(0));

        if (type.equals("pip"))
            faceText.setText(String.valueOf(value));
        else if (type.equals("mult")) {
            faceText.setText(String.format("X%d", value));
            faceText.setFill(color(80));
        }
        pane = Displayer.getPane(lx, ly, faceRect, faceText);

        pane.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && !Displayer.screen.equals("store"))
                select();
        });
    }

    void rerollPanel(boolean toReroll) {
        String check = (toReroll ? ". . ." : "Reroll");
        String setTo = (toReroll ? "Reroll" : ". . .");
        if (Main.inBluff && Main.bluffO.getRerolls() > 0 && Displayer.screen.equals("bluff"))
            for (Node firstChild : Main.cStage.getScene().getRoot().getChildrenUnmodifiable())
                if (firstChild instanceof Pane)
                    for (Node secondChild : ((Pane)firstChild).getChildren())
                        if (secondChild instanceof Pane)
                            for (Node thirdChild : ((Pane)secondChild).getChildren())
                                if (thirdChild instanceof Text && ((Text)thirdChild).getText().equals(check))
                                    ((Text) thirdChild).setText(setTo);
    }
    void sellButton(boolean toWhite) {
        Color newColor = (toWhite ? color(255) : color(240));
        for (Node firstChild : Main.cStage.getScene().getRoot().getChildrenUnmodifiable())
            if (firstChild instanceof Pane)
                for (Node secondChild : ((Pane)firstChild).getChildren()) {
                    boolean sellPane = false;
                    if (secondChild instanceof Pane) {
                        for (Node thirdChild : ((Pane) secondChild).getChildren())
                            if (thirdChild instanceof Text && ((Text) thirdChild).getText().equals("Sell"))
                                sellPane = true;
                        for (Node thirdChild : ((Pane) secondChild).getChildren())
                            if (thirdChild instanceof Rectangle && sellPane)
                                ((Rectangle) thirdChild).setFill(newColor);
                    }
                }
    }
    void unselect() {
        Main.selected = false;
        Main.selectedI.selected = false;
        Main.selectedI = null;
        if (Main.inBluff && Main.bluffO.getAction().equals("turnRolled"))
            rerollPanel(false);
        if (Displayer.screen.equals("inventory") && inInventory(this))
            sellButton(false);
    }
    void selectI() {
        selected = true;
        Main.selected = true;
        Main.selectedI = this;
        if (Main.inBluff && Main.bluffO.getAction().equals("turnRolled"))
            rerollPanel(true);
        if (Displayer.screen.equals("inventory") && inInventory(this))
            sellButton(true);
    }
    void select() {
        System.out.println("clicked");

        if (selected) {
            System.out.println("return");
            ((Rectangle)pane.getChildren().getFirst()).setStroke(color(0));
            unselect();
            return;
        }

        if (!Main.selected) {
            System.out.println("main unselected");
            ((Rectangle)pane.getChildren().getFirst()).setStroke(color("DCBE1EFF"));
            selectI();
        } else {
            ((Rectangle)Main.selectedI.pane.getChildren().getFirst()).setStroke(color(0));
            System.out.println("main selected");
            if (Displayer.screen.equals("inventory"))
                swap((Face)this, (Face)Main.selectedI);
            else if (Displayer.screen.equals("bluff"))
                swap((Die)this, (Die)Main.selectedI);
            unselect();
        }

    }

    static boolean inInventory(Item item) {
        for (Face f : Main.inventory)
            if (f.equals(item))
                return true;
        return false;
    }

    static void sell() {
        if (inInventory(Main.selectedI)) {
            Main.money += (int)Math.ceil(Main.selectedI.cost / 2.0);
            Main.inventory.remove((Face)Main.selectedI);
            Main.selectedI.unselect();
            Displayer.display("inventory");
        }
    }

    static void swap(Face a, Face b) {
        Face c = nullFace();
        copy(a, c);
        copy(b, a);
        copy(c, b);
    }
    static void swap(Die a, Die b) {
        Die c = nullDie();
        copy(a, c);
        copy(b, a);
        copy(c, b);
    }
    static void copyItem(Item from, Item to) {
        to.name = from.name;
        to.type = from.type;
        to.desc = from.desc;
        to.rarity = from.rarity;
        to.value = from.value;
        to.cost = from.cost;
        to.pane.getChildren().clear();
        to.pane.getChildren().addAll(from.pane.getChildren());
    }
    static void copy(Die from, Die to) {
        to.faceN = from.faceN;
        to.stroke = from.stroke;
        to.faces.clear();
        for (int i = 0; i < 6; i++)
            to.faces.add(from.getFace(i));
        copyItem(from, to);
    }
    static void copy(Face from, Face to) {
        to.die = from.die;
        to.fill = from.fill;
        copyItem(from, to);
    }

    static Die nullDie() { return Reader.getDie("null", 0); }
    static Face nullFace() { System.out.println("NullFace"); return Reader.getFace("null", 0); }
}

class Die extends Item {
    //attributes
    ArrayList<Face> faces = new ArrayList<>();
    int faceN;
    Color stroke;

    Die(String type, int value, Color stroke, String name, String desc, String rarity) {
        this.type = type;
        this.value = value;
        this.stroke = stroke;
        this.name = name;
        this.desc = desc;
        this.rarity = rarity;
        cost = (rarity.equals("common") ? 5 : (rarity.equals("uncommon") ? 8 : 11));
    }

    void roll() {
        faceN = (int)(Math.random() * 6); //0-5
    }

    Face getFace() { return faces.get(faceN); }
    Face getFace(int face) { return faces.get(face); }
}

class Face extends Item {
    //attributes
    Die die;
    Color fill;

    Face(Die die, String type, String desc, int value, String name, Color fill, String rarity) {
        this.die = die;
        this.type = type;
        this.value = value;
        this.name = name;
        this.desc = desc;
        this.fill = fill;
        this.rarity = rarity;
        cost = (rarity.equals("common") ? 3 : (rarity.equals("uncommon") ? 5 : 7));
    }

}

class Reader {

    static public Die randomDie() {
        return (Die)random(true);
    }


    static public Item random(boolean isDie) {
        ArrayList<String> commonTypes = new ArrayList<>();
        ArrayList<String> uncommonTypes = new ArrayList<>();
        ArrayList<String> rareTypes = new ArrayList<>();
        ArrayList<String> commonValues = new ArrayList<>();
        ArrayList<String> uncommonValues = new ArrayList<>();
        ArrayList<String> rareValues = new ArrayList<>();
        String fileName = isDie ? "diceList.txt" : "faceList.txt";
        String currentType = "";
        String currentValue = "";
        String currentRarity;

        Scanner reader;
        String next;

        try {
            reader = new Scanner(new File(fileName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        while (reader.hasNext()) {
            next = reader.next();
            switch (next) {
                case "--rarity" -> {
                    currentRarity = reader.next();
                    switch (currentRarity) {
                        case "common" -> {
                            commonValues.add(currentValue);
                            commonTypes.add(currentType);
                        }
                        case "uncommon" -> {
                            uncommonValues.add(currentValue);
                            uncommonTypes.add(currentType);
                        }
                        case "rare" -> {
                            rareValues.add(currentValue);
                            rareTypes.add(currentType);
                        }
                    }
                }
                case "--value" -> currentValue = reader.next();
                case "--type" -> currentType = reader.next();
            }
        }

        System.out.printf("iD %s, cVs %d, cTs %d, uVs %d, uTs %d, rVs %d, rTs %d\n", isDie, commonValues.size(), commonTypes.size(), uncommonValues.size(), uncommonTypes.size(), rareValues.size(), rareTypes.size());

        double randomPercent = Math.random(); // 0.00 - 0.99
        if (randomPercent < 0.02 && !rareTypes.isEmpty()) // 2%
            currentRarity = "rare";
        else if (randomPercent < 0.17 && !uncommonTypes.isEmpty()) // 15%
            currentRarity = "uncommon";
        else
            currentRarity = "common";
        currentType = "null";
        currentValue = "0";
        int current;
        switch (currentRarity) {
            case "common" -> {
                System.out.printf("Common called, size is %d\n", commonTypes.size());
                current = (int)(Math.random() * commonTypes.size());
                currentType = commonTypes.get(current);
                currentValue = commonValues.get(current);
            }
            case "uncommon" -> {
                current = (int)(Math.random() * uncommonTypes.size());
                currentType = uncommonTypes.get(current);
                currentValue = uncommonValues.get(current);

            }
            case "rare" -> {
                current = (int)(Math.random() * rareTypes.size());
                currentType = rareTypes.get(current);
                currentValue = rareValues.get(current);
            }
        }
        if (isDie)
            return getDie(currentType, Integer.parseInt(currentValue));
        else
            return getFace(currentType, Integer.parseInt(currentValue));
    }

    static public Face getFace(String type, int value) {
        Main.nulls++;
        System.out.println("Nulls: " + Main.nulls);
        Scanner reader;
        String next;

        try {
            reader = new Scanner(new File("faceList.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        do {
            next = reader.nextLine();
        } while (!next.equals("\t--type " + type));
        reader.next();
        String desc = reader.nextLine();
        do {
            next = reader.nextLine();
        } while (!(next.equals("\t\t--value " + value) || next.equals("\t\t--value -1")));

        reader.next("--name");
        String name = reader.nextLine().substring(1);
        reader.next("--fill");
        Color fill = color(reader.next());
        reader.next("--rarity");
        String rarity = reader.next();

        return new Face(Item.nullDie(), type, desc, value, name, fill, rarity);
    }
    static public Die getDie(String type, int value) {
        Scanner reader;
        String next;

        try {
            reader = new Scanner(new File("diceList.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        do {
            next = reader.nextLine();
        } while (!next.equals("\t--type " + type));
        do {
            next = reader.nextLine();
        } while (!next.equals("\t\t--value " + value));

        reader.next("--faces");
        String faceString = reader.next();
        reader.next("--stroke");
        String colorString = reader.next();
        Color stroke = color(colorString);
        reader.next("--name");
        String name = reader.nextLine().substring(1);
        reader.next("--desc");
        String desc = reader.nextLine().substring(1);
        reader.next("--rarity");
        String rarity = reader.next();

        Die die = new Die(type, value, stroke, name, desc, rarity);
        System.out.printf("New die with %s C, %s T, %d V, %s S, %s N, %s R, %s F\n", colorString, type, value, stroke, name, rarity, faceString);

        if (type.equals("null"))
            return die;

        die.faces.clear();
        String[] parts = faceString.split(":");
        int num = Integer.parseInt(parts[0]);
        if (num == -1)
            num = 0;
        for (int i = 0; i < 6; i++) {
            die.faces.add(getFace(parts[1], num));
            if (parts[2].charAt(0) == '+')
                num += Integer.parseInt(parts[2].substring(1));
        }
        
        return die;
    }
}
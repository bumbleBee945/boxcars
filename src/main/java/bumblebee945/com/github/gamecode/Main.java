package bumblebee945.com.github.gamecode;

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

import java.util.ArrayList;

public class Main extends Application {
    static ArrayList<Die> die = new ArrayList<>();
    //static ArrayList<Face> inventory = new ArrayList<>();
    static ArrayList<Bluff> bluff = new ArrayList<>();
    static Item selectedI;
    static boolean selected;
    static int bluffN;
    static Bluff bluffO;
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

        for (int i = 0; i < 5; i++)
            die.add(new Die());
        selected = false;

        board = 1;
        bluffN = 3;
        for (int i = 0; i < 4; i++)
            bluff.add(new Bluff(i));

        Displayer.display("board");
    }

    static void play(int num) {
        if (num == bluffN) {
            bluffO = bluff.get(num);
            Displayer.display("bluff");
            /*
            if (--bluffN == -1) { //if done with this board
                bluffN = 3;
                if (++board == 7) { //if done with this game
                    Displayer.display("win");
                    return;
                }
            }*/
        }
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
        scene.setFill(Color.rgb(230, 230, 230));

        switch (type) {
            case "board" -> {
                pane.getChildren().addAll(boardTitle(), boardNum(), invButton());
                for (Bluff b : Main.bluff) {
                    pane.getChildren().addAll(b.getSquare(), b.getTitle(), b.getButton());
                }
            }
            case "inventory" -> pane.getChildren().add(inventory());
            case "bluff" -> pane.getChildren().add(bluff());
            case "win" -> {
                pane.getChildren().add(getPane(0, 0,
                        getRect(550, 550, 0, 0, 100, 200),
                        getText("You win!", 100, 150)
                ));
                pane.setOnMouseClicked(e -> {
                    if (e.getButton().equals(MouseButton.PRIMARY))
                        System.exit(99);
                });
            }
        }
    }

    static Rectangle getRect(int x, int y, int arc, int strokeWidth, int stroke, int fill) {
        Rectangle result = new Rectangle(0, 0, pixel*x, pixel*y);
        result.setStroke(Color.rgb(stroke, stroke, stroke));
        result.setFill(Color.rgb(fill, fill, fill));
        result.setArcHeight(arc);
        result.setArcWidth(arc);
        result.setStrokeWidth(strokeWidth);
        return result;
    }
    static Text getText(String text, int fontSize, int fill) {
        return getText(text, fontSize, fill, false);
    }
    static Text getText(String text, int fontSize, int fill, boolean italic) {
        Text result = new Text(0, 0, text);
        if (italic)
            result.setFont(Font.font("Futura", FontWeight.MEDIUM, FontPosture.ITALIC, fontSize));
        else
            result.setFont(Font.font("Futura", FontWeight.MEDIUM, FontPosture.REGULAR, fontSize));
        result.setFill(Color.rgb(fill, fill, fill));
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
        Rectangle boardTitleRect = getRect(55, 30, 20, 2, 100, 240);
        Text boardTitleText = getText("Board", 20, 70);
        return getPane(10, 10, boardTitleRect, boardTitleText);
    }
    static Pane boardNum() {
        Rectangle boardNumRect = getRect(25, 25, 20, 2, 100, 240);
        Text boardNumText = getText(String.valueOf(Main.board), 20, 70);
        return getPane(25, 45, boardNumRect, boardNumText);
    }
    static Pane invButton() {
        Rectangle invRect = getRect(75, 30, 20, 2, 100, 240);
        Text invText = getText("Inventory", 20, 70);
        Pane pane = getPane(465, 510, invRect, invText);

        pane.setOnMouseClicked(e -> {
                if (e.getButton().equals(MouseButton.PRIMARY))
                    display("inventory");
            });

        return pane;
    }
    static Pane inventory() {
        Pane pane = new Pane();

        Rectangle bg = getRect(550, 550, 0, 0, 0, 0);
        bg.setFill(Color.valueOf("C2D1EEFF"));
        pane.getChildren().add(getPane(0, 0, bg));
        bg = getRect(550, 225, 0, 0, 0, 0);
        bg.setFill(Color.valueOf("C6D9E9FF"));
        pane.getChildren().add(getPane(0, 325, bg));

        for (int i = 0; i < 5; i++) { // dice
            Die die = Main.die.get(i);
            Rectangle rect = getRect(180, 55, 5, 2, 0, 240);
            Text text = getText(die.name, 45, 0);
            pane.getChildren().add(getPane(5, 5 + i*65, rect, text));

            for (int j = 0; j < 6; j++) { // dice faces
                Face face = die.faces.get(j);
                Pane facePane = face.getSquare(55, 55, 190 + (j*60), 5 + (i*65));
                pane.getChildren().add(facePane);
            }
        }

        Rectangle rect = getRect(120, 55, 3, 2, 0, 255);
        Text text = getText("Back", 40, 0);
        Pane backPane = getPane(20, 340, rect, text);
        backPane.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY))
                display("board");
        });
        pane.getChildren().add(backPane);

        rect = getRect(250, 55, 5, 2, 0, 240);
        text = getText("Inventory", 50, 0);
        pane.getChildren().add(getPane(150, 340, rect, text));

        rect = getRect(120, 55, 3, 2, 0, 240);
        text = getText("Sell", 40, 0);
        pane.getChildren().add(getPane(410, 340, rect, text));

        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 8; j++)
                pane.getChildren().add(getPane(20 + (j*65), 415 + (i*65),
                        getRect(55, 55, 3, 2, 0, 255)));

        return pane;
    }
    static Pane bluff() {
        Pane pane = new Pane();
        String ba = Main.bluffO.getAction();

        Rectangle bg = getRect(550, 550, 0, 3, 0, 0);
        bg.setFill(Color.valueOf("C6D9E9FF"));
        pane.getChildren().add(getPane(0, 0, bg));
        bg = getRect(548, 118, 0, 3, 0, 0);
        bg.setFill(Color.valueOf("9ABCDFFF"));
        pane.getChildren().add(getPane(0, 430, bg));

        Rectangle rect = getRect(548, 60, 0, 3, 0, 0);
        Text text = getText("Small Bluff", 40, 0);
        rect.setFill(Color.valueOf("C7D6DAFF"));
        pane.getChildren().add(getPane(0, 0, rect, text));

        rect = getRect(238, 115, 0, 3, 0, 200);
        text = getText("Dice", 120, 50, true);
        if (ba.equals("turnStart")) {
            rect.setFill(Color.valueOf("F4E8C4FF"));
            text.setFill(Color.valueOf("7B5C06FF"));
        }
        Pane dicePane = getPane(310, 59, rect, text);
        pane.getChildren().add(dicePane);

        rect = getRect(238, 80, 0, 3, 0, 250);
        text = getText(". . .", 80, 50, true);
        pane.getChildren().add(getPane(310, 175, rect, text));

        rect = getRect(238, 115, 0, 3, 0, 0);
        if (ba.equals("turnStart"))
            text = getText("Roll", 120, 0, true);
        else
            text = getText("Play", 120, 0, true);
        rect.setFill(Color.valueOf("C1D0BBFF"));
        text.setFill(Color.valueOf("38721FFF"));
        Pane playPane = getPane(310, 254, rect, text);
        playPane.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY))
                Main.bluffO.roll();
        });
        pane.getChildren().add(playPane);

        rect = getRect(310, 60, 0, 3, 0, 0);
        text = getText("3 rerolls left!", 40, 0, true);
        rect.setFill(Color.valueOf("C2D1EEFF"));
        pane.getChildren().add(getPane(0, 370, rect, text));

        rect = getRect(238, 60, 0, 3, 0, 0);
        text = getText("3 turns left!", 40, 0, true);
        rect.setFill(Color.valueOf("C6D9E9FF"));
        pane.getChildren().add(getPane(310, 370, rect, text));

        for (int i = 0; i < 5; i++) {
            rect = getRect(90, 90, 0, 0, 0, 0);
            rect.setFill(Color.valueOf("7DA2C8FF"));
            pane.getChildren().add(getPane(20 + (105 * i), 445, rect));
            if (ba.equals("turnRolled")) {
                Die die = Main.die.get(i);
                Pane diePane = die.getSquare
                        (90, 90, 20 + (105 * i), 445);
                pane.getChildren().add(diePane);
            }
        }

        Circle circ = new Circle(0, 0, 140*pixel);
        circ.setFill(Color.valueOf("E9E9E9FF"));
        circ.setStroke(Color.valueOf("424242FF"));
        circ.setStrokeWidth(8);
        text = getText("\n72000\n", 100, 0);
        Text text2 = getText("\n\n\n\n\nto win\n", 30, 50);
        pane.getChildren().add(getPane(13, 73, circ, text, text2));

        return pane;
    }
}

class Bluff {
    //attributes
    int num;
    int countdown;
    String action;

    //constructors
    Bluff(int num) {
        this.num = num;
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
    String getAction() { return this.action; }
    //int getCountdown() { return this.countdown; }
    Pane getSquare() {
        Rectangle squareRect = Displayer.getRect(125, 125, 10, 3,
                (num < Main.bluffN ? 50 : (num == Main.bluffN ? 70 : 30)), 0); // (? upcoming : (? current : beaten))
        switch (num) {
            case 0: squareRect.setFill(Color.valueOf("E09494FF")); break;
            case 1: squareRect.setFill(Color.valueOf("F1DCC5FF")); break;
            case 2: squareRect.setFill(Color.valueOf("E8ABABFF")); break;
            case 3: squareRect.setFill(Color.valueOf("EAC4C4FF")); break;
        }

        return Displayer.getPane(75, 10 + (num*135), squareRect);
    }
    Pane getTitle() {
        Rectangle titleRect = Displayer.getRect(250, 50, 35, 2, 100, 240);

        String titleS = switch (num) {
            case 0 -> "Boss Bluff";
            case 1 -> "Cash Out";
            case 2 -> "Big Bluff";
            case 3 -> "Small Bluff";
            default -> "";
        };
        Text titleText = Displayer.getText(titleS, 50, 70);

        return Displayer.getPane(225, 25 + (num*135), titleRect, titleText);
    }
    Pane getButton() {
        Rectangle buttonRect = Displayer.getRect(190, 30, 35, 2, 150, 200);
        Text buttonText = Displayer.getText("Defeated", 30, 100);

        if (num < Main.bluffN) { // upcoming
            buttonRect.setStroke(Color.valueOf("E6BA32FF"));
            buttonRect.setFill(Color.valueOf("EDE0BDFF"));
            buttonText.setFill(Color.valueOf("7A5D06FF"));
            buttonText.setText("Upcoming");
        } else if (num == Main.bluffN) { // current
            buttonRect.setStroke(Color.valueOf("66A14EFF"));
            buttonRect.setFill(Color.valueOf("D0E0CBFF"));
            buttonText.setFill(Color.valueOf("294B15FF"));
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

    //mutators
    //void setAction(String action) { this.action = action; }
}

class Item {
    int x = 0;
    int y = 0;
    int lx = 0;
    int ly = 0;
    boolean selected = false;
    Pane pane = new Pane();

    Pane getSquare(int sizeX, int sizeY, int layoutX, int layoutY) {
        x = sizeX;
        y = sizeY;
        lx = layoutX;
        ly = layoutY;
        makePane();
        return pane;
    }

    void makePane() {
        Rectangle faceRect = Displayer.getRect(x, y, 5, 2, 0, 255);
        Text faceText = Displayer.getText("", 50, 0);
        String type;
        int value;
        if (this instanceof Die) {
            type = ((Die)this).getFace().type;
            value = ((Die)this).getFace().value;
        } else {
            type = ((Face)this).type;
            value = ((Face)this).value;
        }
        if (type.equals("pip"))
            faceText.setText(String.valueOf(value));
        pane = Displayer.getPane(lx, ly, faceRect, faceText);

        pane.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY))
                select();
        });
    }
    
    void select() {
        System.out.println("clicked");
        if (selected) {
            System.out.println("return");
            ((Rectangle)pane.getChildren().getFirst()).setStroke(Color.rgb(0, 0, 0));
            Main.selected = false;
            Main.selectedI.selected = false;
            Main.selectedI = null;
            return;
        }

        if (!Main.selected) {
            ((Rectangle)pane.getChildren().getFirst()).setStroke(Color.rgb(220, 190, 30));
            System.out.println("main unselected");

            selected = true;
            Main.selected = true;
            Main.selectedI = this;
        } else {
            ((Rectangle)Main.selectedI.pane.getChildren().getFirst()).setStroke(Color.rgb(0, 0, 0));
            System.out.println("main selected");
            if (Displayer.screen.equals("inventory"))
                swap((Face)this, (Face)Main.selectedI);
            else if (Displayer.screen.equals("bluff"))
                swap((Die)this, (Die)Main.selectedI);

            Main.selected = false;
            Main.selectedI.selected = false;
            Main.selectedI = null;
        }
    }
    
    static void swap(Face a, Face b) {
        Face c = new Face();
        copy(a, c);
        copy(b, a);
        copy(c, b);
    }
    static void swap(Die a, Die b) {
        Die c = new Die(true);
        copy(a, c);
        copy(b, a);
        copy(c, b);
    }
    static void copy(Die from, Die to) {
        to.name = from.name;
        to.faceN = from.faceN;
        to.faces.clear();
        for (int i = 0; i < 6; i++)
            to.faces.add(from.getFace(i));
        to.pane.getChildren().clear();
        to.pane.getChildren().addAll(from.pane.getChildren());
    }
    static void copy(Face from, Face to) {
        to.type = from.type;
        to.value = from.value;
        to.die = from.die;
        to.pane.getChildren().clear();
        to.pane.getChildren().addAll(from.pane.getChildren());
    }
}

class Die extends Item {
    //attributes
    ArrayList<Face> faces = new ArrayList<>();
    int faceN;
    String name;

    Die(boolean isNull) {
        if (isNull)
            name = "Null";
    }
    Die() {
        name = "Basic Die";
        for (int i = 0; i < 6; i++)
            faces.add(new Face("pip", i + 1, this));
    }

    void roll() {
        faceN = (int)(Math.random() * 6); //0-5
    }

    Face getFace() { return faces.get(faceN); }
    Face getFace(int face) { return faces.get(face); }
}

class Face extends Item {
    //attributes
    String type = "";
    int value = 0;
    Die die = new Die(true);

    Face() {}
    Face(String type, int value, Die die) {
        this.type = type;
        this.value = value;
        this.die = die;
    }

}

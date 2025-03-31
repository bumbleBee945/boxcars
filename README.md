# Boxcars

## Synopsis
Boxcars is a Balatro-inspired roguelite centered around rolling dice and scoring high to beat bluffs.

## Motivation
I built this because I deeply love Balatro's game design, and want experience making roguelites. And I needed a school project.

## How to Run
This project is built with JavaFX, and sometimes requires multiple runs to not crash after entering a menu display.

## Code Example
Show a small snippet of the code you are proud of and why.
```
//total scored points (total), starts with 0
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
```

## Contributors
Inspiration from localthunk, creator of Balatro

package prog;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static java.lang.StrictMath.random;
import static prog.Main.root;

public class Sprzet {
    volatile Rectangle sprzet;

    public Sprzet() {
        sprzet = new Rectangle();
        sprzet.setWidth(30);
        sprzet.setHeight(30);
        sprzet.setFill(Color.color(random(), random(), random()));

        root.getChildren().add(sprzet);

    }

}

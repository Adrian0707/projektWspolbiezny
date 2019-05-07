package prog;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import static prog.Main.root;

public class Klient {
    Group klient = new Group();
    Circle glowa;
    Rectangle cialo;
    int x = -10;
    int y = 400;
    Sprzet donaprawy;

    public Klient() {
        glowa = new Circle(-40, -40, 10);
        cialo = new Rectangle(-50, -35, 20, 30);
        glowa.setFill(Color.SANDYBROWN);
        donaprawy = new Sprzet();
        donaprawy.sprzet.setX(-50);
        donaprawy.sprzet.setY(-40);

        cialo.setFill(donaprawy.sprzet.getFill());
        klient.getChildren().add(cialo);
        klient.getChildren().add(glowa);

        root.getChildren().add(klient);

    }

}
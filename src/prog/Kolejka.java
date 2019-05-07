package prog;

import javafx.animation.PathTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.shape.Polyline;
import javafx.util.Duration;

import java.util.ArrayList;

import static prog.Main.N;
import static prog.Main.obsluzony;

public class Kolejka {
    public ArrayList<Klient> kolejka = new ArrayList<Klient>();

    public boolean dodaj(Klient a) {

        if (kolejka.size() >= N)
            return false;
        else {
            kolejka.add(a);
            a.klient.setTranslateY(430);
            a.klient.setTranslateX(270 - kolejka.indexOf(a) * 30);
            return true;
        }
    }

    public void ods() {
        for (Klient a : kolejka) {


            a.klient.setTranslateY(430);
            a.klient.setTranslateX(270 - kolejka.indexOf(a) * 30);

        }
    }

    public Sprzet doKasy() {
        if (kolejka.size() >= 1) {
            Klient k = kolejka.get(0);
            Sprzet a = k.donaprawy;
            kolejka.remove(k);

            obsluzony = new PathTransition();
            obsluzony.setNode(k.klient);
            obsluzony.setDuration(Duration.seconds(2));
            obsluzony.setPath(new Polyline(230, 410, 230, 510, -10, 500));
            obsluzony.setCycleCount(1);
            obsluzony.setOnFinished(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    synchronized (kolejka) {
                        ods();
                    }
                }
            });

            obsluzony.play();
            return a;
        } else
            return null;
    }

}



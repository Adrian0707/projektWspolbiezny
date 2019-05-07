package prog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;

import static prog.Main.*;

public class Obsluga extends Thread {
    protected volatile Magazyn bufor;
    protected Random random;
    static public volatile Sprzet sprzetObsluga;
    Group pracownik = new Group();
    Circle glowa;
    Rectangle cialo;

    public Obsluga(Magazyn bufor) {
        this.bufor = bufor;
        this.random = new Random();

        glowa = new Circle(-40, -40, 10);
        cialo = new Rectangle(-50, -35, 20, 30);
        glowa.setFill(Color.SANDYBROWN);
        ;
        cialo.setFill(Color.BLUE);
        pracownik.getChildren().add(cialo);
        pracownik.getChildren().add(glowa);
        root.getChildren().add(pracownik);
        pracownik.setTranslateY(440);
        pracownik.setTranslateX(350);

        doMagazynu.setDuration(Duration.seconds(0.1));
        doMagazynu.setPath(new Polyline(270, 410, 270, 500, 330, 510));
        doMagazynu.setCycleCount(1);
        doMagazynu.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                synchronized (obsluga) {


                }
            }
        });

        doMagazynuObs.setDuration(Duration.seconds(0.1));
        doMagazynuObs.setPath(new Polyline(310, 410, 310, 510, 330, 510, 310, 410));
        doMagazynuObs.setCycleCount(1);
        doMagazynuObs.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                /*synchronized (obsluga){

                    obsluga.notify();
                }*/
            }
        });
    }

    protected Sprzet odbior() throws InterruptedException {

        Sprzet a = null;
        while (a == null) {
            Thread.sleep(random.nextInt(10));

            a = kolejka.doKasy();

        }
        return a;
    }

    public void run() {

        try {
            while (true) {

                sprzetObsluga = odbior();
                wZakladzie.sprzety.add(sprzetObsluga);
                doMagazynu.setNode(sprzetObsluga.sprzet);

                doMagazynuObs.setNode(pracownik);
                doMagazynu.play();
                doMagazynuObs.play();
               /* synchronized (obsluga){
                    obsluga.wait();
                }*/
                long t = (long) doMagazynuObs.getDuration().toMillis();
                try {
                    Thread.sleep(t);
                } catch (InterruptedException e) {
                    ;
                }
                bufor.wstaw(sprzetObsluga);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

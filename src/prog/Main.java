package prog;

import javafx.animation.AnimationTimer;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import static java.lang.StrictMath.random;

public class Main extends Application {
    private static Scene mainScene;
    public static int WIDTH = 1200;
    public static int HEIGHT = 600;
    public volatile static Group root = new Group();
    private static Canvas canvas = new Canvas(WIDTH, HEIGHT);

    volatile static PathTransition obsluzony = new PathTransition();
    volatile static PathTransition doMagazynu = new PathTransition();
    volatile static PathTransition doMagazynuObs = new PathTransition();
    volatile static PathTransition[] zMagazynu = {new PathTransition(), new PathTransition(), new PathTransition()};
    volatile static PathTransition[] zMagazynuPrac = {new PathTransition(), new PathTransition(), new PathTransition()};
    volatile static PathTransition[] doMagazynuPrac = {new PathTransition(), new PathTransition(), new PathTransition()};
    volatile static PathTransition[] naprawa = {new PathTransition(), new PathTransition(), new PathTransition()};
    volatile static PathTransition[] naprawaSp = {new PathTransition(), new PathTransition(), new PathTransition()};
    volatile static PathTransition[] naPocztePrac = {new PathTransition(), new PathTransition(), new PathTransition()};
    volatile static PathTransition[] naPoczteSp = {new PathTransition(), new PathTransition(), new PathTransition()};

    volatile static PathTransition[] powrotPrac = {new PathTransition(), new PathTransition(), new PathTransition()};

    static volatile Semaphore poczta = new Semaphore(1);

    Rectangle lada = new Rectangle(253, 398, 40, 120);
    Rectangle stanowisko1 = new Rectangle(976, 250, 100, 30);
    Rectangle stanowisko2 = new Rectangle(976, 350, 100, 30);
    Rectangle stanowisko3 = new Rectangle(976, 450, 100, 30);
    Rectangle skrzynka = new Rectangle(1056, 550, 70, 30);
    Rectangle magazyn = new Rectangle(390, 100, 410, 410);
    static Ewidencja wZakladzie = new Ewidencja();
    List<Rectangle> wZakladzieWyswietlone = new ArrayList<>();
    static volatile Kolejka kolejka = new Kolejka();
    static int m = 1;
    static int n = 3;

    volatile static Object obsluga = new Object();
    //volatile static Object[] pracownikcy = {new Object(), new Object(), new Object()};
    static int N = 7;
    static int rozmiarBufora = 100;

    static volatile Magazyn bufor = new Magazyn(rozmiarBufora);

    static volatile Thread[] watek = new Thread[4];

    public static void main(String[] args) {

        watek[0] = new Pracownik(bufor, 0);
        watek[1] = new Pracownik(bufor, 1);
        watek[2] = new Pracownik(bufor, 2);
        watek[3] = new Obsluga(bufor);

        System.out.println("Start!");

        try {
            for (int i = 0; i < m + n; i++) {
                watek[i].start();
            }
            launch(args);
            for (int i = 0; i < m + n; i++) {
                watek[i].join();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("PROJEKT");
        lada.setFill(Color.BROWN);
        stanowisko1.setFill(Color.BROWN);
        stanowisko2.setFill(Color.BROWN);
        stanowisko3.setFill(Color.BROWN);
        skrzynka.setFill(Color.RED);
        magazyn.setFill(Color.BLACK);
        magazyn.setFill(Color.WHITE);
        magazyn.setStroke(Color.BLACK);
        magazyn.setStrokeWidth(10);
        magazyn.strokeDashOffsetProperty();
        root.getChildren().add(lada);
        root.getChildren().add(stanowisko1);
        root.getChildren().add(stanowisko2);
        root.getChildren().add(stanowisko3);
        root.getChildren().add(skrzynka);

        root.getChildren().add(magazyn);

        mainScene = new Scene(root);
        primaryStage.setScene(mainScene);
        primaryStage.setMinHeight(HEIGHT + 30);
        primaryStage.setMinWidth(WIDTH);
        primaryStage.setMaxHeight(HEIGHT + 30);
        primaryStage.setMaxWidth(WIDTH);

        root.getChildren().add(canvas);


        new AnimationTimer() {

            public void handle(long currentNanoTime) {
                for (int i = 0; i < wZakladzieWyswietlone.size(); i++) {
                    root.getChildren().remove(wZakladzieWyswietlone.get(i));
                }
                wZakladzieWyswietlone.clear();
                int ewid = 0;
                for (int i = 0; i < wZakladzie.sprzety.size(); i++) {
                    ewid++;

                    Rectangle w = new Rectangle(ewid * 10, 0, 10, 10);
                    w.setFill(wZakladzie.sprzety.get(i).sprzet.getFill());
                    root.getChildren().add(w);
                    wZakladzieWyswietlone.add(w);

                }
                bufor.getLok().lock();

                for (int i = 0; i < rozmiarBufora; i++) {
                    if (bufor.pula[i] != null) {

                        bufor.pula[i].sprzet.setTranslateX(450 + i % 10 * 40);
                        bufor.pula[i].sprzet.setTranslateY(150 + (int) (i / 10) * 40);

                    }

                }
                bufor.getLok().unlock();

                if (random() <= 0.08) {
                    Klient k = new Klient();
                    PathTransition doKolejki = new PathTransition();
                    doKolejki.setNode(k.klient);
                    doKolejki.setDuration(Duration.seconds(1));
                    doKolejki.setPath(new Line(k.x, k.y, 280 - (kolejka.kolejka.size() + 1) * 30, 410));
                    doKolejki.setCycleCount(1);

                    PathTransition zKolejki = new PathTransition();
                    zKolejki.setNode(k.klient);
                    zKolejki.setDuration(Duration.seconds(3));
                    zKolejki.setPath(new Line(280 - (kolejka.kolejka.size() + 1) * 30, 410, -10, 500));
                    zKolejki.setCycleCount(1);
                    zKolejki.setOnFinished(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent event) {
                            k.klient.getChildren().clear();
                            root.getChildren().remove(k.klient);
                        }
                    });

                    doKolejki.setOnFinished(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent event) {
                            synchronized (kolejka) {
                                if (kolejka.dodaj(k))
                                    ;
                                else
                                    zKolejki.play();
                            }
                        }
                    });
                    doKolejki.play();

                }

            }

        }.start();
        primaryStage.show();
    }
}


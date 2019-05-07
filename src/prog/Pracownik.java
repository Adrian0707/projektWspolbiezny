package prog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

import java.util.Random;

import static prog.Main.*;

public class Pracownik extends Thread {
    volatile protected Magazyn bufor;
    volatile protected Random random;
    volatile int nr;
    Group pracownik = new Group();
    Circle glowa;
    Rectangle cialo;

    public Pracownik(Magazyn bufor, int nr) {

        this.bufor = bufor;
        this.random = new Random();

        this.nr = nr;
        glowa = new Circle(-40, -40, 10);
        cialo = new Rectangle(-50, -35, 20, 30);
        glowa.setFill(Color.SANDYBROWN);

        cialo.setFill(Color.RED);
        pracownik.getChildren().add(cialo);
        pracownik.getChildren().add(glowa);
        pracownik.setTranslateX(1030);
        pracownik.setTranslateY(285 + nr * 100);
        root.getChildren().add(pracownik);
        zMagazynu[nr].setDuration(Duration.seconds(4));
        zMagazynu[nr].setCycleCount(1);
        zMagazynu[nr].setPath(new Polyline(830, 360 + 10 * nr, 1000, 260 + nr * 100));
        zMagazynuPrac[nr].setDuration(Duration.seconds(4));
        zMagazynuPrac[nr].setCycleCount(1);
        zMagazynuPrac[nr].setPath(new Polyline(820, 360 + 10 * nr, 990, 260 + nr * 100));
        zMagazynuPrac[nr].setNode(pracownik);
        zMagazynuPrac[nr].setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
           /*     synchronized (pracownikcy[nr]){
                    pracownikcy[nr].notify();
                }*/
            }
        });
        doMagazynuPrac[nr].setDuration(Duration.seconds(2));
        doMagazynuPrac[nr].setCycleCount(1);
        doMagazynuPrac[nr].setNode(pracownik);
        doMagazynuPrac[nr].setPath(new Polyline(990, 260 + nr * 100, 820, 360 + 10 * nr));
        doMagazynuPrac[nr].setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {/*
                synchronized (pracownikcy[nr]){
                    pracownikcy[nr].notify();
                }*/
            }
        });
        naprawa[nr].setDuration(Duration.seconds(2));
        naprawa[nr].setCycleCount(1);
        naprawa[nr].setNode(pracownik);
        naprawa[nr].setPath(new Polyline(990, 260 + nr * 100, 1080, 260 + nr * 100));
        naprawa[nr].setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                /*synchronized (pracownikcy[nr]){
                    pracownikcy[nr].notify();
                }*/
            }
        });
        naprawaSp[nr].setDuration(Duration.seconds(2));
        naprawaSp[nr].setCycleCount(1);
        naprawaSp[nr].setPath(new Polyline(1000, 260 + nr * 100, 1080, 260 + nr * 100));

        naPocztePrac[nr].setDuration(Duration.seconds(2));
        naPocztePrac[nr].setCycleCount(1);
        naPocztePrac[nr].setNode(pracownik);
        naPocztePrac[nr].setPath(new Polyline(1080, 260 + nr * 100, 1100, 560));
        naPocztePrac[nr].setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
             /*   synchronized (pracownikcy[nr]){
                    pracownikcy[nr].notify();
                }*/
            }
        });
        naPoczteSp[nr].setDuration(Duration.seconds(2));
        naPoczteSp[nr].setCycleCount(1);
        naPoczteSp[nr].setPath(new Polyline(1080, 260 + nr * 100, 1100, 560));

        powrotPrac[nr].setDuration(Duration.seconds(2));
        powrotPrac[nr].setCycleCount(1);
        powrotPrac[nr].setNode(pracownik);
        powrotPrac[nr].setPath(new Polyline(1100, 560, 990, 260 + nr * 100));
        powrotPrac[nr].setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
             /*   synchronized (pracownikcy[nr]){
                    pracownikcy[nr].notify();
                }*/
            }
        });
    }

    protected void napraw(Sprzet elem) throws InterruptedException {
        naprawaSp[nr].setNode(elem.sprzet);
        naprawaSp[nr].setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                elem.sprzet.setStroke(Color.SADDLEBROWN);
                elem.sprzet.setStrokeWidth(10);
                elem.sprzet.setStrokeType(StrokeType.INSIDE);
            }
        });
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            ;
        }
        naprawa[nr].play();
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            ;
        }
        naprawaSp[nr].play();


        //synchronized (pracownikcy[nr]){
        //   pracownikcy[nr].wait();
        // }
        long t = (long) naprawa[nr].getDuration().toMillis();
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            ;
        }

        poczta.acquire();

        wyslij(elem);

        poczta.release();

    }

    protected void wyslij(Sprzet elem) {

        naPoczteSp[nr].setNode(elem.sprzet);
        naPoczteSp[nr].setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                wZakladzie.sprzety.remove(elem);
                root.getChildren().remove(naPoczteSp[nr].getNode());

            }
        });
        naPoczteSp[nr].play();
        naPocztePrac[nr].play();

     /*   try{
            synchronized (pracownikcy[nr]){
                pracownikcy[nr].wait();
            }

        }
        catch (InterruptedException e){
            e.printStackTrace();
        }*/
        long t = (long) naPocztePrac[nr].getDuration().toMillis();
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            ;
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            ;
        }
        powrotPrac[nr].play();
       /* try{
            synchronized (pracownikcy[nr]){
                pracownikcy[nr].wait();
            }

        }
        catch (InterruptedException e){
            e.printStackTrace();
        }*/
        t = (long) powrotPrac[nr].getDuration().toMillis();
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            ;
        }
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            ;
        }
    }

    public void run() {
        Sprzet elem;
        try {
            while (true) {
                elem = bufor.pobierz();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                doMagazynuPrac[nr].play();
                long t = (long) doMagazynuPrac[nr].getDuration().toMillis();
                try {
                    Thread.sleep(t);
                } catch (InterruptedException e) {
                    ;
                }
                /*synchronized (pracownikcy[nr]){
                    pracownikcy[nr].wait();
                }*/
                zMagazynu[nr].setNode(elem.sprzet);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    ;
                }
                zMagazynu[nr].play();
                zMagazynuPrac[nr].play();
               /* synchronized (pracownikcy[nr]){
                    pracownikcy[nr].wait();
                }*/
                t = (long) zMagazynuPrac[nr].getDuration().toMillis();
                try {
                    Thread.sleep(t);
                } catch (InterruptedException e) {
                    ;
                }

                napraw(elem);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


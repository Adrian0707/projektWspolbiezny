package prog;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Magazyn{

    volatile private int N;

    volatile Sprzet[] pula;
    volatile private int wej;
    volatile private int wyj;
    volatile int licz;
    final Object pelny = new Object();
    final Object pusty = new Object();

    public Lock getLok(){
        return lok;
    }

    Lock lok = new ReentrantLock();

    public Magazyn(int rozmiarBufora){
        N = rozmiarBufora;
        pula = new Sprzet[rozmiarBufora];
        wej = 0;
        wyj = 0;
        licz = 0;
    }

    public synchronized Sprzet pobierz(){
        lok.lock();
        while (licz == 0){
            lok.unlock();
            synchronized (pusty){
                try{
                    pusty.wait();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            lok.lock();
        }
        Sprzet ele = pula[wyj];
        pula[wyj] = null;
        wyj = (wyj + 1) % N;
        licz = licz - 1;
        synchronized (pelny){
            pelny.notify();
        }
        lok.unlock();
        return ele;

    }

    public void wstaw(Sprzet ele){
        lok.lock();
        while (licz == N){
            lok.unlock();
            synchronized (pelny){

                try{
                    pelny.wait();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            lok.lock();
        }
        if (pula[wej] == null)
            pula[wej] = ele;
        else
            while (true)
                ;
        wej = (wej + 1) % N;
        licz = licz + 1;
        synchronized (pusty){
            pusty.notify();
        }
        lok.unlock();
    }
}



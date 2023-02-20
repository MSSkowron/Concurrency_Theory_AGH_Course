package asymetryczne;

import java.util.concurrent.Semaphore;

public class Filozof extends Thread {
    private final int liczba_posilkow;
    private final Semaphore lewy_widelec;
    private final Semaphore prawy_widelec;
    private long calkowity_czas_czekania;

    public Filozof(int ID, Stol stol, int liczba_posilkow) {
        this.liczba_posilkow = liczba_posilkow;
        this.calkowity_czas_czekania = 0;

        if (ID % 2 == 0) {
            this.lewy_widelec = stol.widelce[(ID + 1) % stol.liczba_filozofow];
            this.prawy_widelec = stol.widelce[ID % stol.liczba_filozofow];
        } else {
            this.lewy_widelec = stol.widelce[ID % stol.liczba_filozofow];
            this.prawy_widelec = stol.widelce[(ID + 1) % stol.liczba_filozofow];
        }
    }

    public double sredniCzasCzekania() {
        return calkowity_czas_czekania / (double) liczba_posilkow;
    }

    public void jedz() {
        long czas_rozpoczenia_czekania = System.currentTimeMillis();

        try {
            lewy_widelec.acquire();
            prawy_widelec.acquire();

            calkowity_czas_czekania += System.currentTimeMillis() - czas_rozpoczenia_czekania;

            Thread.sleep(3);

            prawy_widelec.release();
            lewy_widelec.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void mysl() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-2);
        }
    }

    @Override
    public void run() {
        for (int i = 0; i < liczba_posilkow; i++) {
            jedz();
            mysl();
        }
    }
}

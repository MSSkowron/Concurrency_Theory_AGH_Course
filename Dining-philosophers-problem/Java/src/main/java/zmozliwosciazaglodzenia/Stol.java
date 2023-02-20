package zmozliwosciazaglodzenia;

import java.util.concurrent.Semaphore;

public class Stol {
    final int liczba_filozofow;
    final Semaphore[] widelce;

    public Stol(int n) {
        this.liczba_filozofow = n;
        this.widelce = new Semaphore[n];

        for (int i = 0; i < n; i++) {
            widelce[i] = new Semaphore(1);
        }
    }
}

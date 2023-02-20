package zarbitrem;

import java.util.concurrent.Semaphore;

public class Stol {
    final int liczba_filozofow;
    final Semaphore[] widelce;

    final Semaphore arbiter;

    public Stol(int n) {
        this.liczba_filozofow = n;
        this.widelce = new Semaphore[n];
        this.arbiter = new Semaphore(n - 1);

        for (int i = 0; i < n; i++) {
            widelce[i] = new Semaphore(1);
        }
    }
}

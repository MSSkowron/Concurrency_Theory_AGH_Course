package asymetryczne;

public class Symulacja extends Thread {
    private final int liczba_filozofow;
    private final int liczba_posilkow;

    public Symulacja(int f, int p) {
        liczba_filozofow = f;
        liczba_posilkow = p;
    }

    @Override
    public void start() {
        Stol stol = new Stol(liczba_filozofow);

        Filozof[] filozofowie = new Filozof[liczba_filozofow];

        for (int i = 0; i < liczba_filozofow; i++) {
            filozofowie[i] = new Filozof(i, stol, liczba_posilkow);
        }

        for (Filozof f : filozofowie) {
            f.start();
        }

        for (Filozof f : filozofowie) {
            try {
                f.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Wynik Asym: ");

        for (int i = 0; i < liczba_filozofow; i++) {
            System.out.println(filozofowie[i].sredniCzasCzekania());
        }

        System.out.println("");
    }
}

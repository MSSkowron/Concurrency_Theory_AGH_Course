public class Main {
    public static void main(String[] args) {
        int liczba_filozofow, liczba_posilkow;

        liczba_filozofow = Integer.parseInt(args[0]);
        liczba_posilkow = Integer.parseInt(args[1]);

        System.out.println("");

        asymetryczne.Symulacja symulacjaAsymetrczyne = new asymetryczne.Symulacja(liczba_filozofow, liczba_posilkow);
        zmozliwosciazaglodzenia.Symulacja symulacjaZMozliwosciaZaglodzenia = new zmozliwosciazaglodzenia.Symulacja(
                liczba_filozofow, liczba_posilkow);
        zarbitrem.Symulacja symulacjaZArbitrem = new zarbitrem.Symulacja(liczba_filozofow, liczba_posilkow);

        symulacjaAsymetrczyne.start();
        try {
            symulacjaAsymetrczyne.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        symulacjaZMozliwosciaZaglodzenia.start();
        try {
            symulacjaZMozliwosciaZaglodzenia.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        symulacjaZArbitrem.start();
        try {
            symulacjaZArbitrem.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}

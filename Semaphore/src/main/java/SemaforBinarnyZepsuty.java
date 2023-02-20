/**
 * W pliku TestSemaforyBinarne przy uzyciu klasy WyscigSemaforyBinarne są testowane oba semafory binarne
 * (zepsuty używający if'a oraz znajdujący się w pliku SemaforBinarny używający while'a).
 *
 * Semafor z while'em nie wskazuje na żadne błędy. Wartość jest zwiększana tyle samo razy co zmniejszana, a ostatecznie ma wartość 0.
 * Semafor z if'em nie działa poprawnie. Często występują deadlocki oraz kończy się niezerową wartością
 *
 * Czytajac dokumentacje metody wait() mozna znalezc informacje, ze watek moze obudzic sie spontanicznie (bez wywolania metody notify/notifyAll).
 * Oznacza to, ze bez ponownego sprawdzenia warunku (co zapewnia petla while) moze spowodowac, ze przejdziemy dalej mimo, ze warunek nie jest spelniony.
 *
 * Najistotniejszym problemem jest brak wiedzy na temat tego, który watek bedzie jako kolejny dopuszczony do monitora obiektu po watku, ktory wywolal notifyAll().
 * Przykład ilustrujący problem:
 *      Mamy semafor binarny S (zaimplementowany z instrukcja if zamiast while) z poczatkowym stanem rownym false, watek W1 oraz watek W2.
 *      W1 chce wykonac operacje na semaforze kolejno V() oraz P().
 *      W2 chce wykonac operacje na semaforze P().
 *      W2 jako pierwszy jest dopuszczony do monitora obiektu jednakze widzi, ze stan wynosi false, a zatem nie moze zmniejszyc i wola wait()'a - czeka.
 *      W1 wykonuje operacje V() zmieniajac stan na true. Wywoluje NotifyAll(), co powinno obudzic W2, aby wykonal swoje operacje.
 *      Zanim W2 zostanie wybudzony i zajmie monitor obiektu, W1 zaczyna operacje P() i tez probuje zajac monitor obiektu.
 *      W kolejkach monitora czekaja watki W1 - czekajacy na wejscie do monitora obiektu (metoda P()) oraz W2 - kolejka zwiazana wait()'em.
 *      Nie wiemy, z ktorej kolejki watek wejdzie jako pierwszy. Powiedzmy, ze wejdzie W1.
 *      W1 widzi, ze stan S to true, a zatem zmienia go na false, konczy operacje P() wola notifyAll i zwalnia dostep.
 *      Teraz w kolejce jest tylko W2, a zatem zostaje on dopuszczony do monitora obiektu i przez to, ze nie sprawdza warunku po raz kolejny (bo ma if'a zamiast while'a)
 *      zmienia stan S z false na false, wola notifyAll i zwalnia dostep.
 *      Podsumowujac wykonano jedna operacje V() oraz dwie operacje P(),a stan pozostal taki sam - false. Jest to sprzeczne z tym jak powinien dzialac poprawny semafor.
 * */
public class SemaforBinarnyZepsuty implements ISemafor{
    private boolean stan = false;

    @Override
    public synchronized void V() {
        if (stan) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        stan = true;
        this.notifyAll();
    }

    @Override
    public synchronized void P() {
        if (!stan) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        stan = false;
        this.notifyAll();
    }
}

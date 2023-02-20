/**
 * Semafor binarny jest szczegolnym przypadkiem semafora licznikowego (ogolnego), poniewaz
 * dla semafora binarnego limit wynosi 1, a reszta dzialania jest taka sama.
* */
public class SemaforLicznikowy implements ISemafor{
    private int stan;
    private final SemaforBinarny sem_mozna_zmniejszac;
    private final SemaforBinarny sem_stan_dostep;

    public SemaforLicznikowy(int limit) {
        if (limit <= 0) {
            System.err.println("Nie mozna zainicjalizowac limitu ilosci dostepnego zasobu semafora liczba niedodatnia!");
            System.exit(-1);
        }
        stan = limit;

        sem_mozna_zmniejszac = new SemaforBinarny();
        sem_stan_dostep = new SemaforBinarny();
    }

    @Override
    public void V() {
        sem_stan_dostep.V();
        {
            if (stan == 0) {
                sem_mozna_zmniejszac.P();
            }

            stan++;
        }
        sem_stan_dostep.P();
    }

    @Override
    public void P() {
        sem_mozna_zmniejszac.V();

        sem_stan_dostep.V();
        {
            stan--;

            if (stan > 0) {
                sem_mozna_zmniejszac.P();
            }
        }
        sem_stan_dostep.P();
    }
}

public class TestSemaforyBinarne {
    public static void main(String[] args) {
        WyscigSemaforyBinarne wyscigSemaforyBinarneSemaforBinarny = new WyscigSemaforyBinarne(new SemaforBinarny());
        WyscigSemaforyBinarne wyscigSemaforyBinarneSemaforBinarnyZepsuty = new WyscigSemaforyBinarne(new SemaforBinarnyZepsuty());

        System.out.println("\nSemafor binarny poprawny: ");
        wyscigSemaforyBinarneSemaforBinarny.start();

        System.out.println("\nSemafor binarny zepsuty: ");
        wyscigSemaforyBinarneSemaforBinarnyZepsuty.start();
    }
}

public class SemaforBinarny implements ISemafor{
    private boolean stan = false;

    @Override
    public synchronized void V() {
        while (stan)
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(-1);
            }

        stan = true;
        this.notifyAll();
    }
    @Override
    public synchronized void P() {
        while (!stan)
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(-1);
            }

        stan = false;
        this.notifyAll();
    }
}

public class WyscigSemaforyBinarne {
    private int licznik = 0;
    private final ISemafor semafor;

    public WyscigSemaforyBinarne(ISemafor semafor) {
        this.semafor = semafor;
    }

    public void start() {
        Thread[] inc_thread = new Thread[5];
        Thread[] dec_thread = new Thread[5];

        for (int i = 0; i < 5; i++) {
            inc_thread[i] = new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    semafor.V();
                    licznik++;
                    semafor.P();
                }
            });

            dec_thread[i] = new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    semafor.V();
                    licznik--;
                    semafor.P();
                }
            });
        }

        for (int i = 0; i < 5; i++) {
            inc_thread[i].start();
            dec_thread[i].start();
        }

        for (int i = 0; i < 5; i++) {
            try {
                inc_thread[i].join();
                dec_thread[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        System.out.println("Licznik wynosi: " + licznik);
    }
}

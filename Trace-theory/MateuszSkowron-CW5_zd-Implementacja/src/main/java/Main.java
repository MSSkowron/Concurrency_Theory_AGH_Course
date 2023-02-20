import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("One argument with input data path is required!");
            System.exit(1);
        }

        List<Character> A = new ArrayList<>();
        List<Character> w = new ArrayList<>();
        List<String> transactions = new ArrayList<>();
        Map<Character, List<Character>> D = new HashMap<>();
        Map<Character, List<Character>> I = new HashMap<>();
        List<List<Character>> FNF = new ArrayList<>();
        List<List<Integer>> diekertGraph = new ArrayList<>();

        // W pierwszej kolejnosci wczytujemy i walidujemy dane wejsciowe.
        try {
            File inputFile = new File(args[0]);
            Scanner scanner = new Scanner(inputFile);

            // Wczytujemy alfabet A
            if (!scanner.hasNextLine()) {
                System.out.println("Wymagany jest alfabet!");
                System.exit(2);
            }

            String data = scanner.nextLine();
            String[] parts = data.split(" ");
            if (parts.length != 3 || !Objects.equals(parts[0], "A") || !Objects.equals(parts[1], "=") || !parts[2].startsWith("{") || !parts[2].endsWith("}")) {
                System.out.println("Niepoprawny format alfabetu");
                System.exit(2);
            }

            String[] aInput = parts[2].substring(1, parts[2].length() - 1).split(",");
            for(String s : aInput){
                A.add(s.charAt(0));
            }

            // Wczytujemy slowo w
            if (!scanner.hasNextLine()) {
                System.out.println("Wymagany jest slad!");
                System.exit(2);
            }

            data = scanner.nextLine();
            parts = data.split(" ");
            if (parts.length != 3 || !Objects.equals(parts[0], "w") || !Objects.equals(parts[1], "=")) {
                System.out.println("Niepoprawny format sladu");
                System.exit(2);
            }

             aInput = parts[2].split("");
            for(String s : aInput){
                if(!A.contains(s.charAt(0))) {
                    System.out.println("Element sladu nie nalezy do alfabetu: " + s.charAt(0));
                    System.exit(2);
                }
                w.add(s.charAt(0));
            }

            // Przeczytaj akcje
            if (!scanner.hasNextLine()) {
                System.out.println("Wymagana jest przynajmniej jedna akcja!");
                System.exit(2);
            }

            while (scanner.hasNextLine()) {
                String transaction = scanner.nextLine();
                parts = transaction.split(" ");
                if (parts.length != 4 || !Objects.equals(parts[2], ":=")) {
                    System.out.println("Niepoprawna akcja: " + transaction);
                    System.exit(2);
                }
                if (!A.contains(parts[0].charAt(0))) {
                    System.out.println("Transakcja nie nalezy do alfabetu: " + parts[0].charAt(0));
                    System.exit(2);
                }
                transactions.add(transaction);
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            e.printStackTrace();
            System.exit(2);
        }

        // Zbior relacji zaleznosci D i niezaleznosci I
        // Inicjalizujemy zbiór zaleznosci D
        for (String i: transactions) {
            ArrayList<Character> a = new ArrayList<>();
            // Kazdy element jest w relacji z samym soba
            a.add(i.charAt(0));
            D.put(i.charAt(0), a);
        }

        for (String i: transactions) {
            I.put(i.charAt(0), new ArrayList<>());
        }

        // Wyznaczenie relacji zaleznosci D i niezaleznosci I
        for (int i = 0; i < transactions.size(); i++) {
            for (int j = i + 1; j < transactions.size(); j++) {
                String t1 = transactions.get(i);
                String t2 = transactions.get(j);

                if (t1.charAt(3) == t2.charAt(3) || t1.substring(8).contains(String.valueOf(t2.charAt(3))) || t2.substring(8).contains(String.valueOf(t1.charAt(3)))) {
                    D.get(t1.charAt(0)).add(t2.charAt(0));
                    D.get(t2.charAt(0)).add(t1.charAt(0));
                } else {
                    I.get(t1.charAt(0)).add(t2.charAt(0));
                    I.get(t2.charAt(0)).add(t1.charAt(0));
                }
            }
        }

        // Wyznaczenie postaci normalej foaty FNF sladu w
        for (Character c : w) {
            if (FNF.size() == 0) {
                ArrayList<Character> x = new ArrayList<>();
                x.add(c);
                FNF.add(x);
                continue;
            }

            int classIndex = -1;
            for (int i = FNF.size() - 1; i >= 0 ; i--) {
                boolean isIndependent = true;
                for (Character cSub : FNF.get(i)) {
                    if (D.get(cSub).contains(c)) {
                        isIndependent = false;
                    }
                }
                if(isIndependent) {
                    classIndex = i;
                } else {
                    break;
                }
            }

            if (classIndex == -1) {
                ArrayList<Character> x = new ArrayList<>();
                x.add(c);
                FNF.add(x);
            } else {
                FNF.get(classIndex).add(c);
            }

        }

        // Wyznaczenie grafu Diekerta
        // Najpierw tworzymy graf pelny
        for (int i = 0; i < w.size(); i++) {
            ArrayList<Integer> vertices = new ArrayList<>();
            for (int j = i + 1; j < w.size(); j++) {
                if (D.get(w.get(i)).contains(w.get(j))) {
                    vertices.add(j);
                }
            }
            diekertGraph.add(vertices);
        }

        // Usuwamy przechodniosc w grafie pelnym
        ArrayList<ArrayList<Integer>> toRemove = new ArrayList<>();
        for (int i = 0; i < w.size(); i++) {
            toRemove.add(new ArrayList<>());
        }

        for (int i = 0; i < w.size(); i++) {
            for (int j = i + 1; j < w.size(); j++) {
                List<Integer> v1 = diekertGraph.get(i);
                if (!v1.contains(j)) {
                    continue;
                }

                List<Integer> v2 = diekertGraph.get(j);
                for (Integer v: v1) {
                    if (v2.contains(v)){
                        toRemove.get(i).add(v);
                    }
                }
            }
        }

        // Wyswietlamy zbior relacji zaleznosci D
        System.out.print("D={");
        for (var entry : D.entrySet()) {
            for(Character c : entry.getValue()){
                System.out.print("("+entry.getKey()+","+c+")");
            }
        }
        System.out.print("}\n");

        // Wyswietlamy zbior relacji niezaleznosci I
        System.out.print("I={");
        for (var entry : I.entrySet()) {
            for(Character c : entry.getValue()){
                System.out.print("("+entry.getKey()+","+c+")");
            }
        }
        System.out.print("}\n");

        // Wyswietlamy postac normalna Foaty FNF sladu w
        System.out.print("FNF([w])=");
        for(List<Character> fClass : FNF){
            System.out.print("(");
            for (Character c : fClass) {
                System.out.print(c);
            }
            System.out.print(")");
        }
        System.out.print("\n");

        // Wyswietlamy graf Diekerta w postaci minimalnej dla slowa w
        for (int i = 0; i < toRemove.size(); i++) {
            for(Integer el: toRemove.get(i)) {
                diekertGraph.get(i).remove(el);
            }
        }

        System.out.println("digraph g{");
        for (int i = 0; i < diekertGraph.size(); i++) {
            for (Integer j : diekertGraph.get(i)) {
                System.out.println((i+1) + " -> " + (j+1));
            }
        }
        for (int i = 0; i < diekertGraph.size(); i++) {
            System.out.println((i+1)+"[label="+w.get(i)+"]");
        }
        System.out.println("}");
    }
}

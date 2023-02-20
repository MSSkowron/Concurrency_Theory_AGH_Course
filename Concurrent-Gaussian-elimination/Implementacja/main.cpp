#include "gauss_matrix.h"

GaussMatrix GM;

int main(int argc, char *argv[])
{
    if (argc != 3) {
        throw std::invalid_argument("Wrong number of arguments!");
    }

    // inicjalizujemy macierz za pomocą pliku wejściowego
    GM.fromFile(argv[1]);

    // wykonujemy współbieżną eliminację Gaussa
    GM.concurrentGaussElimination();

    // stosujemy podstawianie wsteczne
    GM.backwardSubstitution();

    // wynik zapisujemy do pliku
    GM.toFile(argv[2]);
}
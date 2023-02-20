#include <fstream>
#include <math.h>
#include <iomanip>
#include <thread>

#include "gauss_matrix.h"

std::vector<double> GaussMatrix::numbersFromLine(std::string &line, int N)
{
    std::vector<double> result;

    std::string token;
    size_t start = 0;
    size_t end = line.find(' ');
    for(int i = 0; i < N; i++)
    {
        token = line.substr(start, end - start);
        result.push_back(std::stod(token));
        start = end + 1;
        end = line.find(' ', start);
    }

    return result;
}

// Operacja A: znajdowanie mnoznika dla wiersza i, ktory bedzie potrzebny do odejmowania go od wiersza k
void GaussMatrix::actionA(int i, int k)
{
    m[k] = values[k][i] / values[i][i];
}

// Operacja B: mnozenie j'tego elementu wiersza i'tego przez wczesniej wyznaczony mnoznik
void GaussMatrix::actionB(int i, int j, int k)
{
    n[k][j] = values[i][j] * m[k];
}

// Operacja C: odejmowanie od j'tego elementu w k'tym wierszu wczesniej obliczonej wartosci
void GaussMatrix::actionC(int j, int k)
{
    values[k][j] -= n[k][j];
}

// Pivoting
void GaussMatrix::pivoting(int i)
{
    if (std::abs(values[i][i]) < eps)
    {
        int j = i + 1;
        while (j < N && std::abs(values[j][i]) < eps)
            j++;

        if (j == N)
            throw std::invalid_argument("Could not perform pivoting.");

        for (int k = i; k <= N; k++)
        {
            std::swap(values[i][k], values[j][k]);
        }
    }
}

// Odczytanie macierzy podanej w pliku wejsciowym
void GaussMatrix::fromFile(std::string filename)
{
    std::ifstream file(filename);

    std::string line;
    std::getline(file, line);
    int N = std::stoi(line);

    // Y - macierz wejsiowa
    std::vector< std::vector<double> > A(N, std::vector<double>(N));
    for (int i = 0; i < N; i++)
    {
        std::getline(file, line);
        auto row = numbersFromLine(line, N);
        for (int j = 0; j < N; j++)
        {
            A[i][j] = row[j];
        }
    }

    // Y - wektor wyrazow wolnych
    std::getline(file, line);
    auto Y = numbersFromLine(line, N);

    file.close();

    init(N, A, Y);
}

// Zapisanie macierzy wynikowej do pliku wyjsciowego
void GaussMatrix::toFile(std::string filename)
{
    std::ofstream file(filename);

    file << (*this);

    file.close();
}

// Inizjalizacja macierzy o rozmiarze Nx(N+1) na podstawie wczesniej zczytanej macierzy A oraz wektora wyrazow wolnych Y
void GaussMatrix::init(int N, std::vector< std::vector<double> > &A, std::vector<double> &Y)
{
    this->N = N;
    values.resize(N);
    m.resize(N);
    n.resize(N);
    for (int i = 0; i < N; i++)
    {
        values[i].resize(N + 1);
        n[i].resize(N + 1);
        for (int j = 0; j < N; j++)
        {
            values[i][j] = A[i][j];
        }
        values[i][N] = Y[i];
    }
}

int GaussMatrix::getN()
{
    return N;
}

// Wspolbiezna eliminacja Gaussa
void GaussMatrix::concurrentGaussElimination()
{
    std::vector<std::thread> threads;
    for (int i = 0; i < N - 1; i++)
    {
        // Pivoting
        pivoting(i);
        
        // Operacje A z wiersza i'tego dla wierszy [i+1;N-1](bo indeksowanie od 0 do N-1).
        threads.resize(0);
        for (int j = i + 1; j < N; j++)
        {
            std::thread threadA(&GaussMatrix::actionA, this, i, j);
            threads.push_back(std::move(threadA));
        }
        for (auto &t : threads)
        {
            t.join();
        }

        // Operacje B mnozenia wczesniej obliczonych mnoznikow przez wszystkie kolumny (od i'tej bo bedzie to element na przekatnej) kolejnych wierszy od [i+1; N-1]
        threads.resize(0);
        for (int k = i + 1; k < N; k++)
        {
            for (int j = i; j <= N; j++)
            {
                std::thread threadB(&GaussMatrix::actionB, this, i, j, k);
                threads.push_back(std::move(threadB));
            }
        }
        for (auto &t : threads)
        {
            t.join();
        }

        // Operacje C odejmowania wczesniej obliczonych wartosci od wszystkich kolumn (od i'tej bo bedzie to element na przekatnej) kolejnych wierszy od [i+1; N-1]
        threads.resize(0);
        for (int k = i + 1; k < N; k++)
        {
            for (int j = i; j <= N; j++)
            {
                std::thread threadC(&GaussMatrix::actionC, this, j, k);
                threads.push_back(std::move(threadC));
            }
        }
        for (auto &t : threads)
        {
            t.join();
        }
    }
}

// Podstawianie wsteczne
void GaussMatrix::backwardSubstitution()
{
    for (int i = N - 1; i >= 0; i--)
    {
        for (int j = N - 1; j > i; j--)
        {
            values[i][N] -= values[i][j] * values[j][N];
            values[i][j] = 0;
        }
        values[i][N] /= values[i][i];
        values[i][i] = 1;
    }
}

// aby mozna bylo wykonywac [][] do pobierania wartosci
double& GaussMatrix::operator()(int row, int col)
{
    return values[row][col];
}

// wyswietlanie macierzy
std::ostream& operator<<(std::ostream& out, const GaussMatrix& M)
{
    out << M.N << std::endl;
    out << std::setprecision(16);
    for (int i = 0; i < M.N; i++)
    {
        for (int j = 0; j < M.N; j++)
        {
            out << M.values[i][j] << " ";
        }
        out << std::endl;
    }
    for (int i = 0; i < M.N; i++)
    {
        out << M.values[i][M.N] << " ";
    }
    return out;
}
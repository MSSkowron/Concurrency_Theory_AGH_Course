#include <iostream>
#include <vector>
#include <string>

#pragma once

class GaussMatrix
{
private:
    int N;
    std::vector<std::vector<double>> values;
    std::vector<double> m;                  // wartosci zwrocone przez operacje A dla i'tego wiersza pod wartościa m[i]
    std::vector< std::vector<double> > n;   // wartosci zwrocone przez operacje B dla i'tego wiersza j'tej kolumny pod wartością n[k][j]
    static constexpr double eps = 0.00001;  // zero

    static std::vector<double> numbersFromLine(std::string &line, int N);
    void init(int N, std::vector< std::vector<double> > &A, std::vector<double> &Y);
    int getN();
    void actionA(int i, int k);
    void actionB(int i, int j, int k);
    void actionC(int j, int k);
    void pivoting(int i);

public:
    void fromFile(std::string filename);
    void toFile(std::string filename);
    void concurrentGaussElimination();
    void backwardSubstitution();

    double& operator()(int row, int col);
    friend std::ostream& operator<<(std::ostream& out, const GaussMatrix& M);
};
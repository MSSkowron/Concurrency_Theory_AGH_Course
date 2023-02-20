#!/bin/bash
gnuplot -e "
set terminal png;
set output '5philosophers25meals.png';
set title 'Java 5 filozofów 25 posiłków';
set yrange [0:20];
set boxwidth 0.9;
set xtics 0, 1, $(( 5 - 1 ));
set xtics format 'Filozof %h';
set xtics rotate by -75;
set ylabel 'średni czas oczekiwania';
set ytics format '%hms';
set style data histograms;
set style fill solid;
set key vertical right above;
plot 'WithPossibleStarvation_data.txt' title 'Z Mozliwoscia Zaglodzenia',
     'Conductor_data.txt' title 'Z Arbitrem',
     'Asym_data.txt' title 'Asymetrycznie'"
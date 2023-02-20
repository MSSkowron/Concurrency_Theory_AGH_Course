#!/bin/bash

NPHILOSOPHERS="5 10"
NMEALS="10 15"

for NP in $NPHILOSOPHERS; do
    for NM in $NMEALS; do

	echo "$NP filozofów $NM posiłków"
	
	echo "$NP \n $NM" | node main.js | grep -vE 'filozof|Podaj' > tmpfile.txt

	grep -A $NP Asym < tmpfile.txt | grep -v : | grep -oE "[^ ]*ms" | sed 's/ms//g' > Asym_data.txt

	grep -A $NP BothForksAtOnce < tmpfile.txt | grep -oE "[^ ]*ms" | sed 's/ms//g' > BothForksAtOnce_data.txt

	grep -A $NP Conductor < tmpfile.txt | grep -oE "[^ ]*ms" | sed 's/ms//g' > Conductor_data.txt

	gnuplot -e "
    set terminal png;
    set output '${NP}philosophers${NM}meals.png';
    set title 'JavaScript ${NP} filozofów ${NM} posiłków';
    set yrange [0:(( ${NM} * 2  + 20))];
    set boxwidth 0.9;
    set xtics 0, 1, $(( NP - 1 ));
    set xtics format 'Filozof %h';
    set xtics rotate by -75;
    set ylabel 'średni czas oczekiwania';
    set ytics format '%hms';
    set style data histograms;
    set style fill solid;
    set key vertical right above;
    plot 'BothForksAtOnce_data.txt' title 'Dwa widelce jednoczesnie',
        'Conductor_data.txt' title 'Z Arbitrem',
        'Asym_data.txt' title 'Asymetrycznie'"
        done
    done

rm tmpfile.txt 
rm Asym_data.txt
rm Conductor_data.txt 
rm BothForksAtOnce_data.txt
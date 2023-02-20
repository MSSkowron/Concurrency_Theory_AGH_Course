// Teoria Współbieżnośi, implementacja problemu 5 filozofów w node.js
// Opis problemu: http://en.wikipedia.org/wiki/Dining_philosophers_problem
//   https://pl.wikipedia.org/wiki/Problem_ucztuj%C4%85cych_filozof%C3%B3w
// 1. Dokończ implementację funkcji podnoszenia widelca (Fork.acquire).
// 2. Zaimplementuj "naiwny" algorytm (każdy filozof podnosi najpierw lewy, potem
//    prawy widelec, itd.).
// 3. Zaimplementuj rozwiązanie asymetryczne: filozofowie z nieparzystym numerem
//    najpierw podnoszą widelec lewy, z parzystym -- prawy. 
// 4. Zaimplementuj rozwiązanie z kelnerem (według polskiej wersji strony)
// 5. Zaimplementuj rozwiążanie z jednoczesnym podnoszeniem widelców:
//    filozof albo podnosi jednocześnie oba widelce, albo żadnego.
// 6. Uruchom eksperymenty dla różnej liczby filozofów i dla każdego wariantu
//    implementacji zmierz średni czas oczekiwania kassssżdego filozofa na dostęp 
//    do widelców. Wyniki przedstaw na wykresach.

var Fork = function() {
    this.state = 0;
    return this;
}

Fork.prototype.acquire = function(cb) {
    // zaimplementuj funkcję acquire, tak by korzystala z algorytmu BEB
    // (http://pl.wikipedia.org/wiki/Binary_Exponential_Backoff), tzn:
    // 1. przed pierwszą próbą podniesienia widelca Filozof odczekuje 1ms
    // 2. gdy próba jest nieudana, zwiększa czas oczekiwania dwukrotnie
    //    i ponawia próbę, itd.

    var fork = this,
	loop = function (waitTime) {
	    setTimeout(function () {
		if (fork.state == 0) {
		    fork.state = 1;
		    cb();
		}
		else
		    loop (waitTime * 2);
 	    }, waitTime);
	};

    loop(1);
}

Fork.prototype.release = function() { 
    this.state = 0; 
}

var Philosopher = function(id, forks) {
    this.id = id;
    this.forks = forks;
    this.f1 = id % forks.length;
    this.f2 = (id+1) % forks.length;
    this.totalWaitingTime = 0;
    this.startCurrentWaitingTime = 0;
    
    return this;
}

Philosopher.prototype.startNaive = function(count) {
    this.totalWaitingTime = 0;
    
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
		myself = this,
    
	// zaimplementuj rozwiązanie naiwne
	// każdy filozof powinien 'count' razy wykonywać cykl
	// podnoszenia widelców -- jedzenia -- zwalniania widelców

	loopNaive = function (count) {
	    if (count > 0) {
			myself.startCurrentWaitingTime = new Date().getTime();
			forks[f1].acquire(function () {
				forks[f2].acquire(function () {
					myself.totalWaitingTime += new Date().getTime() - myself.startCurrentWaitingTime;
					setTimeout(function () {
						forks[f1].release();
						forks[f2].release();
						loopNaive(count - 1);
					}, 1) // zakładamy, że jedzenie trwa 1 milisekundę
				})
			})
		};
	};

    loopNaive(count);
}

Philosopher.prototype.startAsym = function(count) {
    this.totalWaitingTime = 0;

    var forks = this.forks,
        f1 = this.id % 2 == 0 ? this.f1 : this.f2,
        f2 = this.id % 2 == 0 ? this.f2 : this.f1,
        id = this.id,
		myself = this,
	
	// zaimplementuj rozwiązanie asymetryczne
	// każdy filozof powinien 'count' razy wykonywać cykl
	// podnoszenia widelców -- jedzenia -- zwalniania widelców

	loopAsym = function (count) {
	    if (count > 0) {
			myself.startCurrentWaitingTime = new Date().getTime();
			forks[f1].acquire(function () {
				forks[f2].acquire(function () {
					myself.totalWaitingTime += new Date().getTime() - myself.startCurrentWaitingTime;
					setTimeout(function () {
						forks[f1].release();
						forks[f2].release();
						loopAsym(count - 1);
					}, 1) // zakładamy, że jedzenie trwa 1 milisekundę
				})
			})
		} else {
			--noPhilosophersStillRunning;
			if (noPhilosophersStillRunning == 0) {
				callbackOnEnd();
			}
	    }
	};

    loopAsym(count);
}

var Kelner = function (n) {
    this.state = 0;
	this.maxState = n - 1;
    
    return this;
}

Kelner.prototype.acquire = function(cb) {
    var kelner = this,
	loop = function (waitTime) {
	    setTimeout(function () {
		if (kelner.state < kelner.maxState) {
		    ++kelner.state;
		    cb();
		}
		else
		    loop (waitTime * 2);
 	    }, waitTime);
	};

    loop(1);
}

Kelner.prototype.release = function() { 
    --this.state;
}

Philosopher.prototype.startKelner = function(count, kelner) {
    this.totalWaitingTime = 0;

    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id,
		myself = this,
    
	// zaimplementuj rozwiązanie z kelnerem
	// każdy filozof powinien 'count' razy wykonywać cykl
	// podnoszenia widelców -- jedzenia -- zwalniania widelców

	loopKelner = function (count, kelner) {
	    if (count > 0) {
			myself.startCurrentWaitingTime = new Date().getTime();
			kelner.acquire(function () {
				forks[f1].acquire(function () {
					forks[f2].acquire(function () {
						myself.totalWaitingTime += new Date().getTime() - myself.startCurrentWaitingTime;
						setTimeout(function () {
							forks[f1].release();
							forks[f2].release();
							kelner.release();

							loopKelner(count - 1, kelner);
						}, 1) // zakładamy, że jedzenie trwa 1 milisekundę
					})
				})
			})
		} else {
			--noPhilosophersStillRunning;
			if (noPhilosophersStillRunning == 0) {
				callbackOnEnd();
			}
	    }
	};

    loopKelner(count, kelner);
}

// TODO: wersja z jednoczesnym podnoszeniem widelców
// Algorytm BEB powinien obejmować podnoszenie obu widelców, 
// a nie każdego z osobna

function acquireBothForksAtOnce(fork1, fork2, cb) {
    var loop = function (waitTime) {
		setTimeout(function () {
			if (fork1.state == 0 && fork2.state == 0) {
				fork1.state = fork2.state = 1;
				cb();
			} else {
				loop (waitTime * 2);
			}
		}, waitTime);
    };

    loop(1); // jednomilisekundowy timeout na początek
}

Philosopher.prototype.startBothForksAtOnce = function(count) {
    this.totalWaitingTime = 0;
    
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
		id = this.id,
		myself = this,
    
	// zaimplementuj rozwiązanie naiwne
	// każdy filozof powinien 'count' razy wykonywać cykl
	// podnoszenia widelców -- jedzenia -- zwalniania widelców

	loopBothForksAtOnce = function (count) {
	    if (count > 0) {
			myself.startCurrentWaitingTime = new Date().getTime();
			acquireBothForksAtOnce(forks[f1], forks[f2], function () {
				myself.totalWaitingTime += new Date().getTime() - myself.startCurrentWaitingTime;
				setTimeout(function () {
					forks[f1].release();
					forks[f2].release();
					loopBothForksAtOnce(count - 1);
				}, 1) // zakładamy, że jedzenie trwa 1 milisekundę
			})
		} else {
			--noPhilosophersStillRunning;
			if (noPhilosophersStillRunning == 0) {
				callbackOnEnd();
			}
	    }
	};
    
    loopBothForksAtOnce(count);
}

var noPhilosophers;
var noMeals;
var noPhilosophersStillRunning;
var callbackOnEnd;

var forks = [];
var philosophers = [];

function printAvgTimes() {
	for (var i = 0; i < noPhilosophers; i++) {
		console.log(i + " czekał średnio " + (philosophers[i].totalWaitingTime / noMeals) + "ms");
	}
	console.log("");
}

function prepareVariables() {
	forks = [];

	for (var i = 0; i < noPhilosophers; i++) {
	    forks.push(new Fork());
	}

	philosophers = [];

	for (var i = 0; i < noPhilosophers; i++) {
	    philosophers.push(new Philosopher(i, forks));
	}
}

const rl = require('readline').createInterface({
  input: process.stdin,
  output: process.stdout
});

rl.question('Podaj liczbę filozofów: ', (NPhilosophers) => {
    noPhilosophers = parseInt(NPhilosophers);
    rl.question('Podaj, ile razy każdy będzie jadł: ', (NnoMeals) => {
		noMeals = parseInt(NnoMeals);
		rl.close();

		prepareVariables();

		for (var i = 0; i < noPhilosophers; i++) {
			philosophers[i].startAsym(noMeals);
		}

		noPhilosophersStillRunning = noPhilosophers;
		callbackOnEnd = function () {
			console.log("\nwyniki Asym:");
			printAvgTimes();
			
			for (var i = 0; i < noPhilosophers; i++) {
				philosophers[i].startBothForksAtOnce(noMeals);
			}
			
			noPhilosophersStillRunning = noPhilosophers;
			callbackOnEnd = function () {
				console.log("wyniki BothForksAtOnce:");
				printAvgTimes();
				
				var kelner = new Kelner(noPhilosophers);

				for (var i = 0; i < noPhilosophers; i++) {
					philosophers[i].startKelner(noMeals, kelner);
				}
				
				noPhilosophersStillRunning = noPhilosophers;
				callbackOnEnd = function () {
					console.log("wyniki Conductor:");
					printAvgTimes();
				}
			}
		}	
    });
});

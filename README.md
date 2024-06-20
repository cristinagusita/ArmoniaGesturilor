# Armonia Gesturilor
Armonia Gesturilor este o aplicație web care folosește un model personalizat de inteligență artificială pentru a permite utilizatorilor de orice vârstă să experimenteze sunete muzicale de pian realizate prin mișcarea mâinilor în aer, să înregistreze, salveze, posteze sau să șteargă melodiile personale. 
<br> Tehnologiile utilizate sunt următoarele:

* [Java 17](https://www.oracle.com/java/technologies/javase-downloads.html)
* [Spring](https://spring.io/)
* [Maven](https://maven.apache.org/)
* [PostgreSQL 16.2](https://www.postgresql.org/download/)

## Cerințe preliminare
Pentru a putea rula acest proiect este necesară o versiune de Java 17 sau una ulterioară. Versiunea de Java poate fi verificată în terminal cu ajutorul comenzii `java -version`.

Instalarea unei versiuni de Java se poate realiza prin accesarea [Oracle](https://www.oracle.com/java/technologies/javase-downloads.html).

De asemenea, versiunea de Maven poate fi verificată prin rularea comenzii `mvn -version`

În cazul în care Maven nu există local, acesta poate fi instalat după cum se prezintă în acest [Tutorial](https://www.baeldung.com/install-maven-on-windows-linux-mac).

PostgreSQL poate fi descărcat [aici](https://www.postgresql.org/download/windows/).

## Configurare și rulare
Proiectul se poate configura și rula local parcurgând următorii pași:

### Clonare repository
```git
git clone https://github.com/cristinagusita/ArmoniaGesturilor
```

### Compilare locală
În terminal, se rulează comanda `cd ArmoniaGesturilor`. Dacă toate cerințele preliminare sunt îndeplinite, se poate rula comanda:
```
mvn clean install
```
### Configurare baza de date
Se deschide `psql` și se creează baza de date `registration` cu ajutorul `CREATE DATABASE registration`. Acest nume se poate modifica în fișierul `application.yml` sau `application.properties` prin schimbarea liniei `spring.datasource.url=jdbc:postgresql://localhost:5433/registration` cu `spring.datasource.url=jdbc:postgresql://localhost:5433/*numele modificat în funcție de preferințe*`.

În `application.yml` se introduce numele și parola utilizatorului PostgreSQL care are drepturi pentru baza de date creată:

```
spring.datasource.username=username
spring.datasource.password=password
```

### Asistență locală pentru emailuri
Se instalează [Maildev](https://github.com/maildev/maildev) pentru testarea generării emailurilor.

În terminal, se introduce comanda `maildev` care va configura în mod implicit aplicația web să ruleze pe portul `1080` și serverul de email pe portul `1025`. Aceste setări pot fi modificate, dar schimbările vor trebui propagate în fișierul `application.yml` sau `application.properties`.

### Rularea aplicației
Se rulează metoda principală a clasei `ArmoniaGesturilorApplication` și se deschide `http://localhost:8081/` pentru vizualizarea în browser a aplicației. 

## Modelul personalizat de inteligență artificială

Dacă se dorește rularea modelului este necesară parcurgerea următorilor pași:

### Cerințe 

* [Python](https://www.python.org/downloads/windows/) (3.10.7 sau o versiune ulterioară)
* [MediaPipe](https://pypi.org/project/mediapipe/) (0.8.1 sau o versiune ulterioară)
* [OpenCV](https://docs.opencv.org/4.x/d5/de5/tutorial_py_setup_in_windows.html) (3.4.2 sau o versiune ulterioară)
* [TensorFlow](https://www.tensorflow.org/install/pip) (2.3.0 sau o versiune ulterioară)
* [scikit-learn](https://scikit-learn.org/stable/install) (0.23.2 sau o versiune ulterioară - doar dacă se dorește afișarea matricii de confuzie)  
* [Matplotlib]() (3.3.2 sau o versiune ulterioară - doar dacă se dorește afișarea matricii de confuzie)

Pentru verificarea versiunii de Python, se introduce în terminal comanda `py --version`. Dacă nicio versiune de Python nu este găsită, aceasta se poate descărca și instala de [aici](https://www.python.org/downloads/windows/). 

Mai departe, cu ajutorul `pip` se vor instala restul cerințelor. La instalarea Python, `pip` este inclus, însă versiunea lui se poate verifica prin `pip --version`. 

Astfel, se introduc pe rând următoarele comenzi:
```
pip install mediapipe
pip install opencv-python
pip install tensorflow
pip install scikit-learn
pip install numpy
pip install matplotlib
```

Se recomandă utilizarea Jupyter Notebook dacă se dorește modificarea modelului sau reantrenarea lui. Acesta se poate instala prin rularea în terminal a comenzii:
```
pip install jupyter
```

### Demo

`app.py` este un exemplu de program pentru inferență. Acesta poate fi rulat prin apăsarea butonului `Run Python File` sau prin comanda:
```
python app.py
```

# Referință
* [MediaPipe](https://mediapipe.dev/)
 
# Licență 
Armonia Gesturilor este sub licența [Apache v2 license](LICENSE).
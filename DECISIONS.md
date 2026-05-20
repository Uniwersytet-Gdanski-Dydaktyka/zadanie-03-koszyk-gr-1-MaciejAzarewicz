## 1. Strategy Pattern dla promocji

**Wybór:** Strategy Pattern

Każda promocja (rabat %, 2+1, kupon) to osobna klasa implementująca. Strategy doskonale pasuje do promocji, bo to algorytmy obliczania cen, które mogą się zmieniać i łączyć w dowolnej kolejności. Command byłby niepotrzebnie skomplikowany, bo nie potrzebujemy cofać operacji. Strategy pozwala na naturalne testowanie różnych kombinacji promocji i łatwe dodawanie nowych promocji bez zmiany kodu.

---

## 2. Immutable Product

**Wybór:** Niemutowalna klasa

Klasa `Product` ma `final` fields i bez setterów — zamiast zmieniać produkt, tworzymy nowy. Immutable gwarantuje bezpieczeństwo, konsystencję danych i idealnie pasuje do Strategy — każda promocja zwraca nową listę ze zmienionymi obiektami.
error id: file:///C:/Users/azare/Desktop/aaa/zadanie-03-koszyk-gr-1-MaciejAzarewicz/src/main/java/Product.java:java/lang/String#equals().
file:///C:/Users/azare/Desktop/aaa/zadanie-03-koszyk-gr-1-MaciejAzarewicz/src/main/java/Product.java
empty definition using pc, found symbol in pc: java/lang/String#equals().
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 3434
uri: file:///C:/Users/azare/Desktop/aaa/zadanie-03-koszyk-gr-1-MaciejAzarewicz/src/main/java/Product.java
text:
```scala
import java.util.Objects;

/*
 * WYMAGANIE: klasy, enkapsulacja i mutowalnosc.
 * Ta klasa reprezentuje produkt z zadania.
 * Pola sa private final, nie ma setterow, wiec Product jest immutable.
 */
public final class Product {
    private final String code;
    private final String name;
    private final double price;
    private final double discountPrice;

    /*
     * Tworzy nowy produkt z kodem, nazwa i cena podstawowa.
     * Na poczatku cena po promocji jest taka sama jak zwykla cena,
     * bo zadna promocja nie zostala jeszcze zastosowana.
     */
    public Product(String code, String name, double price) {
        this(code, name, price, price);
    }

    /*
     * Prywatny konstruktor uzywany wewnatrz klasy.
     * Sprawdza poprawnosc danych, a potem zapisuje je w polach finalnych.
     * Dzieki temu po utworzeniu obiektu nie da sie juz zmienic produktu.
     */
    private Product(String code, String name, double price, double discountPrice) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Kod produktu nie moze byc pusty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nazwa produktu nie moze byc pusta");
        }
        if (price < 0 || discountPrice < 0) {
            throw new IllegalArgumentException("Cena nie moze byc ujemna");
        }

        this.code = code;
        this.name = name;
        this.price = price;
        this.discountPrice = discountPrice;
    }

    /*
     * Zwraca kod produktu.
     * Kod sluzy do rozpoznawania konkretnego produktu, np. przy kuponie rabatowym.
     */
    public String getCode() {
        return code;
    }

    /*
     * Zwraca nazwe produktu.
     * Nazwa jest wykorzystywana miedzy innymi jako drugie kryterium sortowania.
     */
    public String getName() {
        return name;
    }

    /*
     * Zwraca podstawowa cene produktu.
     * Ta cena nie zmienia sie po zastosowaniu promocji.
     */
    public double getPrice() {
        return price;
    }

    /*
     * Zwraca cene produktu po promocjach.
     * Jesli promocji nie bylo, ta wartosc jest taka sama jak price.
     */
    public double getDiscountPrice() {
        return discountPrice;
    }

    /*
     * Tworzy nowy produkt z taka sama nazwa, kodem i cena podstawowa,
     * ale z nowa cena po promocji.
     * Oryginalny obiekt zostaje bez zmian, bo Product jest immutable.
     */
    public Product withDiscountPrice(double discountPrice) {
        return new Product(code, name, price, discountPrice);
    }

    /*
     * Tworzy nowy produkt bez promocji.
     * Cena po promocji wraca wtedy do zwyklej ceny produktu.
     */
    public Product resetDiscount() {
        return new Product(code, name, price);
    }

    /*
     * Porownuje dwa produkty.
     * Produkty sa uznane za takie same tylko wtedy, gdy maja ten sam kod,
     * nazwe, cene podstawowa i cene po promocji.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Product)) {
            return false;
        }

        Product product = (Product) object;
        return Double.compare(product.price, price) == 0
                && Double.compare(product.discountPrice, discountPrice) == 0
                && code.equals(product.code)
                && name.equals@@(product.name);
    }

    /*
     * Tworzy liczbe hash na podstawie tych samych pol, ktore sa uzywane w equals.
     * To jest potrzebne, zeby obiekty poprawnie dzialaly np. w kolekcjach typu Set.
     */
    @Override
    public int hashCode() {
        return Objects.hash(code, name, price, discountPrice);
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: java/lang/String#equals().
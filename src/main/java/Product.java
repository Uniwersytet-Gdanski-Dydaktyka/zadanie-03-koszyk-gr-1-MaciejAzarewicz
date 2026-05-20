public final class Product {
    private final String code;
    private final String name;
    private final double price;
    private final double discountPrice;

    public Product(String code, String name, double price) {
        this(code, name, price, price);
    }

    // Sprawdza dane produktu i ustawia wszystkie pola
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

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    // Tworzy nowy produkt z nowa cena po promocji
    public Product withDiscountPrice(double discountPrice) {
        return new Product(code, name, price, discountPrice);
    }

    // Tworzy nowy produkt bez rabatu
    public Product resetDiscount() {
        return new Product(code, name, price);
    }

    // Porownuje produkty przy promocji najtanszy gratis
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
                && name.equals(product.name);
    }
}
